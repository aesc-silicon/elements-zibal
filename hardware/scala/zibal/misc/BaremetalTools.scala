// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal.misc

import java.io._
import spinal.core._

import nafarr.peripherals.PeripheralsComponent
import nafarr.system.dma.DmaHandshake

object BaremetalTools {

  case class Header(config: ElementsConfig.ElementsConfig, name: String) {

    val storage = SoftwareStorage(config, name, "baremetal")
    storage.dump()

    /** Emit the bare-metal SoC header from a bus-agnostic device list: each
      * entry is (component, absolute base address, size).
      */
    def generate(
        devices: Seq[(Component, BigInt, BigInt)],
        irqMapping: Seq[Bool],
        errorMapping: Seq[Bool],
        dmaRequestMapping: Seq[DmaHandshake] = Nil
    ) = {
      val filename = "soc.h"
      val file = s"${config.swStorageBuildPath(name)}/${filename}"
      val writer = new PrintWriter(new File(file))
      SpinalInfo(s"Generating ${filename} for ${name}")

      writer.write("#ifndef SOC_HEADER\n")
      writer.write("#define SOC_HEADER\n\n")

      for ((component, address, size) <- devices) {
        val definition =
          buildDefinition(
            component,
            componentName(component),
            address,
            size,
            irqMapping,
            errorMapping,
            dmaRequestMapping
          )
        writer.write(definition)
        if (definition.nonEmpty) writer.write("\n")
      }
      writer.write("#endif /* SOC_HEADER */\n")
      writer.close()
    }

    private def componentName(c: Component): String = {
      val raw = Option(c.getName()).filter(_.nonEmpty).getOrElse(c.getClass.getSimpleName)
      val idx = raw.indexOf('_')
      if (idx >= 0) raw.substring(idx + 1) else raw
    }

    private def buildDefinition(
        component: Component,
        name: String,
        address: BigInt,
        size: BigInt,
        irqMapping: Seq[Bool],
        errorMapping: Seq[Bool],
        dmaRequestMapping: Seq[DmaHandshake]
    ): String = component match {
      case p: PeripheralsComponent =>
        val irqNumber = p.getInterrupt.flatMap { sig =>
          val idx = irqMapping.indexOf(sig)
          if (idx < 0) None else Some(idx + 1)
        }
        val errorNumber = p.getError.flatMap { sig =>
          val idx = errorMapping.indexOf(sig)
          if (idx < 0) None else Some(idx)
        }
        var d = p.headerBareMetal(name, address, size)
        irqNumber.foreach(n => d += s"#define ${name.toUpperCase}_IRQ\t\t$n\n")
        errorNumber.foreach(n => d += s"#define ${name.toUpperCase}_ERROR\t\t$n\n")
        // DMA request lines: <NAME>_DMA_{TX,RX}, or <NAME>_DMA<n>_{TX,RX} for several bundles.
        val requests = p.getDmaRequests
        for ((request, index) <- requests.zipWithIndex) {
          val prefix = name.toUpperCase + (if (requests.size > 1) s"_DMA$index" else "_DMA")
          for ((direction, handshake) <- Seq("TX" -> request.tx, "RX" -> request.rx)) {
            // Reference lookup: DmaHandshake is a case class, so == matches any instance.
            val line = dmaRequestMapping.indexWhere(_ eq handshake)
            if (line >= 0) {
              d += s"#define ${prefix}_${direction}\t\t$line\n"
            }
          }
        }
        d
      case _ => ""
    }

  }
}
