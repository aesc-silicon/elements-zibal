// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal.misc

import java.io._
import spinal.core._
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, Set, Map}
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.tilelink._
import spinal.lib.bus.tilelink.{Bus => TileLink}
import spinal.lib.bus.wishbone._
import spinal.lib.bus.misc.{SizeMapping, AddressMapping}
import nafarr.peripherals.PeripheralsComponent

object BaremetalTools {

  case class Header(config: ElementsConfig.ElementsConfig, name: String) {

    val storage = SoftwareStorage(config, name, "baremetal")
    storage.dump()

    def generate(
        configs: mutable.LinkedHashMap[Axi4Bus, Axi4CrossbarSlaveConfig],
        bridge: Axi4Shared,
        apbMapping: ArrayBuffer[(Apb3, SizeMapping)],
        irqMapping: ArrayBuffer[Bool],
        errorMapping: ArrayBuffer[Bool]
    ) = {
      val filename = "soc.h"
      val file = s"${config.swStorageBuildPath(name)}/${filename}"
      val writer = new PrintWriter(new File(file))
      SpinalInfo(s"Generating ${filename} for ${name}")

      writer.write("#ifndef SOC_HEADER\n")
      writer.write("#define SOC_HEADER\n\n")

      for ((connection, config) <- configs) {
        if (connection == bridge) {
          val address = config.mapping.base
          for ((ip, size) <- apbMapping) {
            val parent = ip.parent.component
            val regAddress = address + size.base
            val definition = buildDefinition(
              parent,
              componentName(parent),
              regAddress,
              size.size,
              irqMapping,
              errorMapping
            )
            writer.write(definition)
            if (definition.nonEmpty) writer.write("\n")
          }
        }
      }
      writer.write("#endif /* SOC_HEADER */\n")
      writer.close()
    }

    private def componentName(c: Component): String = {
      val raw = Option(c.getName()).filter(_.nonEmpty).getOrElse(c.getClass.getSimpleName)
      val idx = raw.indexOf('_')
      if (idx >= 0) raw.substring(idx + 1) else raw
    }

    def generateWishbone(
        bridgeMapping: SizeMapping,
        wbMapping: ArrayBuffer[(Wishbone, SizeMapping)],
        irqMapping: ArrayBuffer[Bool],
        errorMapping: ArrayBuffer[Bool]
    ) = {
      val filename = "soc.h"
      val file = s"${config.swStorageBuildPath(name)}/${filename}"
      val writer = new PrintWriter(new File(file))
      SpinalInfo(s"Generating ${filename} for ${name}")

      writer.write("#ifndef SOC_HEADER\n")
      writer.write("#define SOC_HEADER\n\n")

      val address = bridgeMapping.base
      for ((ip, size) <- wbMapping) {
        val parent = ip.parent.component
        val regAddress = address + size.base
        val definition = buildDefinition(
          parent,
          componentName(parent),
          regAddress,
          size.size,
          irqMapping,
          errorMapping
        )
        writer.write(definition)
        if (definition.nonEmpty) writer.write("\n")
      }
      writer.write("#endif /* SOC_HEADER */\n")
      writer.close()
    }

    def generateTileLink(
        bridgeMapping: SizeMapping,
        mapping: ArrayBuffer[(TileLink, SizeMapping)],
        irqMapping: ArrayBuffer[Bool],
        errorMapping: ArrayBuffer[Bool]
    ) = {
      val filename = "soc.h"
      val file = s"${config.swStorageBuildPath(name)}/${filename}"
      val writer = new PrintWriter(new File(file))
      SpinalInfo(s"Generating ${filename} for ${name}")

      writer.write("#ifndef SOC_HEADER\n")
      writer.write("#define SOC_HEADER\n\n")

      val address = bridgeMapping.base
      for ((ip, size) <- mapping) {
        val parent = ip.parent.component
        val regAddress = address + size.base
        val definition = buildDefinition(
          parent,
          componentName(parent),
          regAddress,
          size.size,
          irqMapping,
          errorMapping
        )
        writer.write(definition)
        if (definition.nonEmpty) writer.write("\n")
      }
      writer.write("#endif /* SOC_HEADER */\n")
      writer.close()
    }

    private def buildDefinition(
        component: Component,
        name: String,
        address: BigInt,
        size: BigInt,
        irqMapping: ArrayBuffer[Bool],
        errorMapping: ArrayBuffer[Bool]
    ): String = component match {
      case p: PeripheralsComponent =>
        val irqNumber = p.getInterrupt.flatMap { sig =>
          val idx = irqMapping.indexOf(sig)
          if (idx < 0) None else Some(idx)
        }
        val errorNumber = p.getError.flatMap { sig =>
          val idx = errorMapping.indexOf(sig)
          if (idx < 0) None else Some(idx)
        }
        var d = p.headerBareMetal(name, address, size)
        irqNumber.foreach(n => d += s"#define ${name.toUpperCase}_IRQ\t\t$n\n")
        errorNumber.foreach(n => d += s"#define ${name.toUpperCase}_ERROR\t\t$n\n")
        d
      case _ => ""
    }

  }
}
