package zibal.misc

import java.io._
import spinal.core._
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, Set, Map}
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.misc.SizeMapping
import nafarr.peripherals.io.gpio.Apb3Gpio
import nafarr.peripherals.io.pio.Apb3Pio
import nafarr.peripherals.io.pwm.Apb3Pwm
import nafarr.peripherals.com.uart.Apb3Uart
import nafarr.peripherals.com.spi.Apb3SpiController
import nafarr.peripherals.com.spi.Axi4ReadOnlySpiXipController
import nafarr.peripherals.com.i2c.Apb3I2cController
import nafarr.peripherals.pinmux.Apb3Pinmux
import nafarr.system.mtimer.Apb3MachineTimer
import nafarr.system.plic.Apb3Plic
import nafarr.memory.hyperbus.Apb3HyperBus
import nafarr.crypto.aes.Apb3AesMaskedAccelerator

object BaremetalTools {

  case class Header(config: ElementsConfig.ElementsConfig, name: String) {

    val storage = SoftwareStorage(config, name, "baremetal")
    storage.dump()

    def generate(
        configs: mutable.LinkedHashMap[Axi4Bus, Axi4CrossbarSlaveConfig],
        bridge: Axi4Shared,
        apbMapping: ArrayBuffer[(Apb3, SizeMapping)],
        irqMapping: ArrayBuffer[(Int, Bool)]
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
            val definition = parent match {
              case _: Apb3Uart =>
                val ip = parent.asInstanceOf[Apb3Uart]
                val irqLine = irqMapping.filter(_._2 == ip.io.interrupt)
                val irqNumber = if (irqLine.isEmpty) null else Some(irqLine(0)._1)
                ip.headerBareMetal(parent.toString(), regAddress, size.size, irqNumber)
              case _: Apb3I2cController =>
                val ip = parent.asInstanceOf[Apb3I2cController]
                val irqLine = irqMapping.filter(_._2 == ip.io.interrupt)
                val irqNumber = if (irqLine.isEmpty) null else Some(irqLine(0)._1)
                ip.headerBareMetal(parent.toString(), regAddress, size.size, irqNumber)
              case _: Apb3SpiController =>
                val ip = parent.asInstanceOf[Apb3SpiController]
                val irqLine = irqMapping.filter(_._2 == ip.io.interrupt)
                val irqNumber = if (irqLine.isEmpty) null else Some(irqLine(0)._1)
                ip.headerBareMetal(parent.toString(), regAddress, size.size, irqNumber)
              case _: Apb3Gpio =>
                val ip = parent.asInstanceOf[Apb3Gpio]
                val irqLine = irqMapping.filter(_._2 == ip.io.interrupt)
                val irqNumber = if (irqLine.isEmpty) null else Some(irqLine(0)._1)
                ip.headerBareMetal(parent.toString(), regAddress, size.size, irqNumber)
              case _: Apb3Pio =>
                val ip = parent.asInstanceOf[Apb3Pio]
                ip.headerBareMetal(parent.toString(), regAddress, size.size)
              case _: Apb3Pwm =>
                val ip = parent.asInstanceOf[Apb3Pwm]
                ip.headerBareMetal(parent.toString(), regAddress, size.size)
              case _: Apb3Pinmux =>
                val ip = parent.asInstanceOf[Apb3Pinmux]
                ip.headerBareMetal(parent.toString(), regAddress, size.size)
              case _: Apb3MachineTimer =>
                val ip = parent.asInstanceOf[Apb3MachineTimer]
                ip.headerBareMetal(parent.toString(), regAddress, size.size)
              case _: Apb3Plic =>
                val ip = parent.asInstanceOf[Apb3Plic]
                ip.headerBareMetal(parent.toString(), regAddress, size.size)
              case _: Apb3HyperBus =>
                val ip = parent.asInstanceOf[Apb3HyperBus]
                ip.headerBareMetal(parent.toString(), regAddress, size.size)
              case _: Apb3AesMaskedAccelerator =>
                val ip = parent.asInstanceOf[Apb3AesMaskedAccelerator]
                ip.headerBareMetal(parent.toString(), regAddress, size.size)
              case _ => ""
            }
            writer.write(definition)
            if (definition.length > 0)
              writer.write("\n")
          }
        }
      }
      writer.write("#endif /* SOC_HEADER */\n")
      writer.close()
    }
  }
}
