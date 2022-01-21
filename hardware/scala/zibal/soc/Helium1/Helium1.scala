/*
 * Copyright (c) 2021 Phytec Messtechnik GmbH
 */

package zibal.soc

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.platform.Helium

import zibal.misc.{ElementsConfig, ZephyrTools}

import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._

import zibal.peripherals.io.gpio.{Apb3Gpio, Gpio, GpioCtrl}
import zibal.peripherals.com.uart.{Apb3Uart, Uart, UartCtrl}


object Helium1 {
  def apply(sysFrequency: HertzNumber, dbgFrequency: HertzNumber) =
      Helium1(Parameter.default(sysFrequency, dbgFrequency))
  def apply(parameter: Helium.Parameter) = Helium1(parameter)

  def prepare(soc: Helium1, elementsConfig: ElementsConfig.ElementsConfig) {
    val dt = ZephyrTools.DeviceTree(elementsConfig)
    dt.generate("helium", soc.clocks.systemClockDomain, soc.system.axiCrossbar.slavesConfigs,
                soc.system.apbBridge.io.axi, soc.system.apbMapping, soc.system.irqMapping,
                "helium1.dtsi")
    val board = ZephyrTools.Board(elementsConfig)
    board.addLed("heartbeat", soc.pers.gpioStatusCtrl, 0)
    board.addLed("ok", soc.pers.gpioStatusCtrl, 1)
    board.addLed("error", soc.pers.gpioStatusCtrl, 2)
    board.addKey("reset", soc.pers.gpioStatusCtrl, 3)
    board.generateDeviceTree(soc.pers.uartStdCtrl)
    board.generateKconfig()
    board.generateDefconfig(soc.system.apbMapping, soc.clocks.systemClockDomain)
  }

  case class Peripherals (
    uartStd: UartCtrl.Parameter,
    gpioStatus: GpioCtrl.Parameter
  ) {}

  object Parameter {
    def default(sysFrequency: HertzNumber, dbgFrequency: HertzNumber) =
      Helium.Parameter.default(
        Peripherals(
          uartStd = UartCtrl.Parameter.full,
          gpioStatus = GpioCtrl.Parameter(4, 2, (0 to 2), (3 to 3), (3 to 3))
        ),
        sysFrequency,
        dbgFrequency,
        2
      )
  }

  case class Io(peripherals: Peripherals) extends Bundle {
    val uartStd = master(Uart.Io(peripherals.uartStd))
    val gpioStatus = Gpio.Io(peripherals.gpioStatus)
  }

  case class Helium1(p: Helium.Parameter) extends Helium.Helium(p) {
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
