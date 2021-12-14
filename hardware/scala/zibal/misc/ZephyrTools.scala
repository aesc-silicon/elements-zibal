/*
 * Copyright (c) 2021 Phytec Messtechnik GmbH
 */

package zibal.misc

import java.io._
import spinal.core._
import scala.collection.mutable
import scala.collection.mutable.{ArrayBuffer, Set, Map}
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.misc.SizeMapping
import zibal.peripherals.io.gpio.Apb3Gpio
import zibal.peripherals.com.uart.Apb3Uart
import zibal.peripherals.com.spi.Apb3SpiMaster
import zibal.peripherals.com.spi.AmbaSpiXipMaster
import zibal.peripherals.com.i2c.Apb3I2cController


object ZephyrTools {

  case class Board(config: ElementsConfig.ElementsConfig) {

    def generateDefconfig(apbMapping: ArrayBuffer[(Apb3, SizeMapping)], clockDomain: ClockDomain) = {
      val clockSpeed = clockDomain.frequency.getValue.toInt
      val file = s"${config.zephyrBoardPath}/${config.socName.toLowerCase()}-${config.boardName.toLowerCase()}_defconfig"
      val writer = new PrintWriter(new File(file))
      writer.write(s"""CONFIG_SOC_SERIES_RISCV32_ELEMENTS=y
CONFIG_SOC_RISCV32_ELEMENTS=y
CONFIG_SOC_RISCV32_ELEMENTS_ISA_C=n
CONFIG_BOARD_${config.socName.toUpperCase()}_${config.boardName.toUpperCase()}=y
CONFIG_HEAP_MEM_POOL_SIZE=1024
CONFIG_SYS_CLOCK_HW_CYCLES_PER_SEC=${clockSpeed}
""")

      val ips = Set[spinal.core.Component]()
      apbMapping.foreach { case (ip, size) => ips += ip.parent.component }

      val xip = ips.filter {
        case _: AmbaSpiXipMaster => true
        case _ => false
      }
      if (xip.isEmpty) {
        writer.write("CONFIG_XIP=n\n")
      }

      ips.foreach { ip =>
        val config = ip match {
          case _: Apb3Uart => """CONFIG_SERIAL=y
CONFIG_UART_ELEMENTS=y
CONFIG_UART_INTERRUPT_DRIVEN=y
"""
          case _: Apb3Gpio => """CONFIG_GPIO=y
CONFIG_GPIO_ELEMENTS=y
CONFIG_GPIO_ELEMENTS_INTERRUPT=y
"""
          case _: Apb3I2cController => """CONFIG_I2C=y
CONFIG_I2C_ELEMENTS=y
"""
          case _: Apb3SpiMaster => """CONFIG_SPI=y
CONFIG_SPI_ELEMENTS=y
"""
          case _ => ""
        }
        if (!config.equals(""))
          writer.write(config)
      }

      writer.close()
    }

    def generateKconfig() = {
      var file = s"${config.zephyrBoardPath}/Kconfig.board"
      var writer = new PrintWriter(new File(file))

      writer.write(s"""config BOARD_${config.socName.toUpperCase()}_${config.boardName.toUpperCase()}
    bool "${config.socName} ${config.boardName} board"
    depends on SOC_RISCV32_ELEMENTS
    select HAS_DTS
    select RISCV
""")

      writer.close()

      file = s"${config.zephyrBoardPath}/Kconfig.defconfig"
      writer = new PrintWriter(new File(file))

      writer.write(s"""if BOARD_${config.socName.toUpperCase()}_${config.boardName.toUpperCase()}
config BOARD
    default "${config.socName.toLowerCase()}-${config.boardName.toLowerCase()}"
endif
""")

      writer.close()
    }

    val leds = Map[String, (String, Int)]()
    val keys = Map[String, (String, Int)]()

    def addLed(name: String, gpio: Apb3Gpio, pin: Int) = {
      leds += name -> (gpio.toString(), pin)
    }

    def addKey(name: String, gpio: Apb3Gpio, pin: Int) = {
      keys += name -> (gpio.toString(), pin)
    }

    def generateDeviceTree(stdout: Apb3Uart) = {
      val stdoutName = stdout.toString()
      val file = s"${config.zephyrBoardPath}/${config.socName.toLowerCase()}-${config.boardName.toLowerCase()}.dts"
      val writer = new PrintWriter(new File(file))

      writer.write(s"""/dts-v1/;
#include <${config.socName.toLowerCase()}.dtsi>

/ {
    model = "${config.socName.toLowerCase()}_${config.boardName.toLowerCase()}";
    compatible = "elements,${config.socName.toLowerCase()}_${config.boardName.toLowerCase()}";

    chosen {
        zephyr,console = &${stdoutName};
        zephyr,shell-uart = &${stdoutName};
        zephyr,sram = &ram0;
    };
""")

      writer.write("""    leds {
        compatible = "gpio-leds";
""")

      leds.foreach { case (name, (gpio, pin)) =>
        writer.write(s"""        ${name}: ${name.toUpperCase()} {
            gpios = <&${gpio} ${pin} GPIO_ACTIVE_HIGH>;
            label = "${name.capitalize} LED";
        };
""")
      }

      writer.write("    };\n")

      writer.write("""    keys {
        compatible = "gpio-keys";
""")

      keys.foreach { case (name, (gpio, pin)) =>
        writer.write(s"""        ${name}: ${name.toUpperCase()} {
            gpios = <&${gpio} ${pin} GPIO_ACTIVE_HIGH>;
            label = "${name.capitalize} LED";
        };
""")
      }

      writer.write("    };\n")

      writer.write("};\n")
      writer.close()
    }

  }

  case class DeviceTree(config: ElementsConfig.ElementsConfig) {

    def generate(platform: String, clockDomain: ClockDomain,
                 configs: mutable.LinkedHashMap[Axi4Bus,Axi4CrossbarSlaveConfig],
                 bridge: Axi4Shared, apbMapping: ArrayBuffer[(Apb3, SizeMapping)],
                 irqMapping: ArrayBuffer[(Int, Bool)], filename: String) = {
     val writer = new PrintWriter(new File(config.buildPath + filename))

      writer.write(s"""#include <$platform.dtsi>
/ {
\t#address-cells = <1>;
\t#size-cells = <1>;
\tsoc {
\t\t#address-cells = <1>;
\t\t#size-cells = <1>;
\t\tcompatible = "elements,$platform-soc", "simple-bus";
\t\tranges;""")

      for ((connection, config) <- configs) {
        if (connection == bridge) {
          val address = config.mapping.base
          for ((ip, size) <- apbMapping) {
            val parent = ip.parent.component
            val regAddress = address + size.base
            val deviceTree = parent match {
              case _: Apb3Uart =>
                val ip = parent.asInstanceOf[Apb3Uart]
                val irqNumber = irqMapping.filter(_._2 == ip.io.interrupt)
                ip.deviceTree(parent.toString(), regAddress, size.size, clockDomain, irqNumber(0)._1)
              case _: Apb3I2cController =>
                val ip = parent.asInstanceOf[Apb3I2cController]
                val irqNumber = irqMapping.filter(_._2 == ip.io.interrupt)
                ip.deviceTree(parent.toString(), regAddress, size.size, clockDomain, irqNumber(0)._1)
              case _: Apb3SpiMaster =>
                val ip = parent.asInstanceOf[Apb3SpiMaster]
                val irqNumber = irqMapping.filter(_._2 == ip.io.interrupt)
                ip.deviceTree(parent.toString(), regAddress, size.size, irqNumber(0)._1)
              case _: Apb3Gpio =>
                val ip = parent.asInstanceOf[Apb3Gpio]
                val irqNumber = irqMapping.filter(_._2 == ip.io.interrupt)
                ip.deviceTree(parent.toString(), regAddress, size.size, irqNumber(0)._1)
              case _ => ""
            }
            writer.write(deviceTree)
          }
        }
      }

      val irqCount = irqMapping.size - 1

      writer.write(s"""
\t};
};
&plic {
\triscv,ndev = <$irqCount>;
};""")

      writer.close()
    }
  }
}
