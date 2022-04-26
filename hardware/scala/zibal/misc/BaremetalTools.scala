package zibal.misc

import java.io._
import spinal.core._
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, Set, Map}
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.misc.SizeMapping
import nafarr.peripherals.io.gpio.Apb3Gpio
import nafarr.peripherals.com.uart.Apb3Uart
import nafarr.peripherals.com.spi.Apb3SpiMaster
import nafarr.peripherals.com.spi.Axi4SharedSpiXipMaster
import nafarr.peripherals.com.i2c.Apb3I2cController
import nafarr.system.mtimer.Apb3MachineTimer

object BaremetalTools {

  case class Header(config: ElementsConfig.ElementsConfig, name: String) {

    val storage = SoftwareStorage(config, name, "baremetal", "")

    def generate(
        configs: mutable.LinkedHashMap[Axi4Bus, Axi4CrossbarSlaveConfig],
        bridge: Axi4Shared,
        apbMapping: ArrayBuffer[(Apb3, SizeMapping)],
        irqMapping: ArrayBuffer[(Int, Bool)]
    ) = {
      val file = s"${config.swStorageBuildPath(name)}/soc.h"
      val writer = new PrintWriter(new File(file))
      SpinalInfo(s"Generate ${file}")

      writer.write("#ifndef SOC_HEADER\n")
      writer.write("#define SOC_HEADER\n")

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
                val irqNumber = if (irqLine.isEmpty) -1 else irqLine(0)._1
                ip.headerBareMetal(parent.toString(), regAddress, size.size, irqNumber)
              case _: Apb3I2cController =>
                val ip = parent.asInstanceOf[Apb3I2cController]
                val irqLine = irqMapping.filter(_._2 == ip.io.interrupt)
                val irqNumber = if (irqLine.isEmpty) -1 else irqLine(0)._1
                ip.headerBareMetal(parent.toString(), regAddress, size.size, irqNumber)
              case _: Apb3SpiMaster =>
                val ip = parent.asInstanceOf[Apb3SpiMaster]
                val irqLine = irqMapping.filter(_._2 == ip.io.interrupt)
                val irqNumber = if (irqLine.isEmpty) -1 else irqLine(0)._1
                ip.headerBareMetal(parent.toString(), regAddress, size.size, irqNumber)
              case _: Apb3Gpio =>
                val ip = parent.asInstanceOf[Apb3Gpio]
                val irqLine = irqMapping.filter(_._2 == ip.io.interrupt)
                val irqNumber = if (irqLine.isEmpty) -1 else irqLine(0)._1
                ip.headerBareMetal(parent.toString(), regAddress, size.size, irqNumber)
              case _: Apb3MachineTimer =>
                val ip = parent.asInstanceOf[Apb3MachineTimer]
                ip.headerBareMetal(parent.toString(), regAddress, size.size)
              case _ => ""
            }
            writer.write(definition)
          }
        }
      }
      writer.write("#endif /* SOC_HEADER */\n")
      writer.close()
    }
  }
}
