// SPDX-FileCopyrightText: 2026 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal.misc

import spinal.core._
import spinal.lib.bus.misc.SizeMapping
import spinal.lib.bus.tilelink.{Bus => TileLinkBus}

import zibal.platform.PlatformComponent
import nafarr.peripherals.PeripheralsComponent

/** Renode co-simulation helpers. */
object RenodeTools {

  /** Write the peripheral manifest consumed by the Renode co-simulation flow:
    * one tab-separated row per device with instance name, type, clock domain
    * (= clk/resetn port prefix), absolute base, size, and IRQ/error indices.
    */
  def dumpCosimManifest(platform: PlatformComponent, file: String) {
    import java.io.{File, PrintWriter}
    val allIrqs = (platform.irqMapping ++ platform.peripheralDomains.values.flatMap(_.irqs)).toSeq
    val allErrors =
      (platform.errorMapping ++ platform.peripheralDomains.values.flatMap(_.errors)).toSeq

    def instanceName(c: Component): String = {
      val raw = Option(c.getName()).filter(_.nonEmpty).getOrElse(c.getClass.getSimpleName)
      val idx = raw.indexOf('_')
      if (idx >= 0) raw.substring(idx + 1) else raw
    }

    // (clock-domain name, absolute domain base, devices in that domain).
    val groups: Seq[(String, BigInt, Seq[(TileLinkBus, SizeMapping)])] =
      ("system", platform.periphBase, platform.tileLinkMapping.toSeq) +:
        platform.peripheralDomains.toSeq.map { case (n, d) => (n, d.base, d.devices.toSeq) }

    val out = new File(file)
    Option(out.getParentFile).foreach(_.mkdirs())
    val writer = new PrintWriter(out)
    for ((domainName, base, devices) <- groups; (bus, localMapping) <- devices) {
      val component = bus.parent.component
      val (irq, error) = component match {
        case p: PeripheralsComponent =>
          (
            p.getInterrupt.map(allIrqs.indexOf(_)).filter(_ >= 0),
            p.getError.map(allErrors.indexOf(_)).filter(_ >= 0)
          )
        case _ => (None, None)
      }
      writer.write(
        "%s\t%s\t%s\t0x%08x\t0x%x\t%s\t%s\n".format(
          instanceName(component),
          component.getClass.getSimpleName,
          domainName,
          base + localMapping.base,
          localMapping.size,
          irq.map(_.toString).getOrElse("-"),
          error.map(_.toString).getOrElse("-")
        )
      )
    }
    writer.close()
    SpinalInfo(s"Generating cosim manifest ${file}")
  }
}
