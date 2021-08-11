/*
 * Copyright (c) 2021 Phytec Messtechnik GmbH
 */

package zibal.soc

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.platform.Hydrogen

import zibal.misc.{ElementsConfig, ZephyrTools}

import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._

import zibal.peripherals.io.gpio.{Apb3Gpio, Gpio, GpioCtrl}
import zibal.peripherals.com.uart.{Apb3Uart, Uart, UartCtrl}


object Hydrogen1 {
  def apply(p: Hydrogen.Parameter = Peripherals.default) = Hydrogen1(p)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig()
    val config = SpinalConfig(noRandBoot = false, targetDirectory = elementsConfig.zibalBuildPath)

    config.generateVerilog({
      val soc = Hydrogen1(Peripherals.default)
      val dt = ZephyrTools.DeviceTree(elementsConfig)
      dt.generate("hydrogen", soc.clocks.systemClockDomain, soc.system.axiCrossbar.slavesConfigs,
                  soc.system.apbBridge.io.axi, soc.system.apbMapping, soc.system.irqMapping,
                  "hydrogen1.dtsi")
      val board = ZephyrTools.Board(elementsConfig)
      board.addLed("heartbeat", soc.pers.gpioStatusCtrl, 0)
      board.addLed("ok", soc.pers.gpioStatusCtrl, 1)
      board.addLed("error", soc.pers.gpioStatusCtrl, 2)
      board.addKey("reset", soc.pers.gpioStatusCtrl, 3)
      board.generateDeviceTree(soc.pers.uartStdCtrl)
      board.generateKconfig()
      board.generateDefconfig(soc.system.apbMapping)
      soc
    })
  }

  case class Io(peripherals: Peripherals) extends Bundle {
    val uartStd = master(Uart.Io(peripherals.uartStd))
    val gpioStatus = Gpio.Io(peripherals.gpioStatus)
  }

  case class Peripherals (
    uartStd: UartCtrl.Parameter,
    gpioStatus: GpioCtrl.Parameter
  ) {}

  object Peripherals {
    def default = Hydrogen.Parameter.default(
      Peripherals(
        uartStd = UartCtrl.Parameter.full,
        gpioStatus = GpioCtrl.Parameter(4, 2, (0 to 2), (3 to 3), (3 to 3))
      ),
      2
    )
  }

  case class Hydrogen1(p: Hydrogen.Parameter) extends Hydrogen.Hydrogen(p) {
    var peripherals = p.peripherals.asInstanceOf[Peripherals]
    val io_per = Io(peripherals)

    val pers = new ClockingArea(clocks.systemClockDomain) {

      val uartStdCtrl = Apb3Uart(peripherals.uartStd)
      system.apbMapping += uartStdCtrl.io.bus -> (0x00000, 4 kB)
      uartStdCtrl.io.uart <> io_per.uartStd
      system.irqMapping += 1 -> uartStdCtrl.io.interrupt

      val gpioStatusCtrl = Apb3Gpio(peripherals.gpioStatus)
      system.apbMapping += gpioStatusCtrl.io.bus -> (0x10000, 4 kB)
      gpioStatusCtrl.io.gpio <> io_per.gpioStatus
      system.irqMapping += 2 -> gpioStatusCtrl.io.interrupt

      connectPeripherals()
    }
  }
}
