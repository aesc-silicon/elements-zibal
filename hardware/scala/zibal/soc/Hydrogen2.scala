/*
 * Copyright (c) 2020 Phytec Messtechnik GmbH
 */

package zibal.platform

import spinal.core._
import spinal.lib._

import zibal.arch.Hydrogen

import zibal.misc.BinTools
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import spinal.lib.io.TriStateArray

import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._

import zibal.peripherals.io.gpio.{Apb3Gpio, Gpio, GpioCtrl}
import zibal.peripherals.misc.mtimer.{Apb3MachineTimer, MachineTimerCtrl}
import zibal.peripherals.com.uart.{Apb3Uart, Uart, UartCtrl}
import zibal.peripherals.com.spi.{
  Apb3SpiMaster,
  Spi,
  SpiCtrl,
  SpiMaster,
  SpiMasterCtrl
}


object Hydrogen2 {
  def main(args: Array[String]) {
    val config = SpinalConfig(noRandBoot = false, targetDirectory = "./../build/zibal/")
    config.generateVerilog({
      val toplevel = Hydrogen2(Peripherals.default)
      BinTools
        .initRam(toplevel.system.onChipRam.ram, "../build/zephyr/zephyr/zephyr.bin")
      toplevel
    })
  }

  case class Peripherals (
    uartStd: UartCtrl.Parameter,
    gpioStatus: GpioCtrl.Parameter,
    spi0: SpiCtrl.Parameter,
    gpio1: GpioCtrl.Parameter,
    gpio2: GpioCtrl.Parameter,
    gpio3: GpioCtrl.Parameter
  ) {}

  object Peripherals {
    def default = Hydrogen.Parameter.default(
      Peripherals(
        uartStd = UartCtrl.Parameter.default,
        gpioStatus = GpioCtrl.Parameter(4, 2, (0 to 2), (3 to 3), (3 to 3)),
        spi0 = SpiCtrl.Parameter.default,
        gpio1 = GpioCtrl.Parameter.default,
        gpio2 = GpioCtrl.Parameter.default,
        gpio3 = GpioCtrl.Parameter(2, 2, null, null, null)
      ),
      7
    )
  }

  case class Hydrogen2(p: Hydrogen.Parameter) extends Hydrogen.Hydrogen(p) {
    var peripherals = p.peripherals.asInstanceOf[Peripherals]
    val io_per = new Bundle {
      val uartStd = master(Uart.Io(peripherals.uartStd))
      val gpioStatus = Gpio.Io(peripherals.gpioStatus)
      val spi0 = master(Spi.Io(peripherals.spi0))
      val gpio1 = Gpio.Io(peripherals.gpio1)
      val gpio2 = Gpio.Io(peripherals.gpio2)
      val gpio3 = Gpio.Io(peripherals.gpio3)
    }

    val pers = new ClockingArea(clocks.systemClockDomain) {

      val uartStdCtrl = Apb3Uart(peripherals.uartStd)
      system.apbMapping += uartStdCtrl.io.bus -> (0x00000, 4 kB)
      uartStdCtrl.io.uart <> io_per.uartStd
      system.plicCtrl.io.sources(1) := uartStdCtrl.io.interrupt

      val gpioStatusCtrl = Apb3Gpio(peripherals.gpioStatus)
      system.apbMapping += gpioStatusCtrl.io.bus -> (0x10000, 4 kB)
      gpioStatusCtrl.io.gpio <> io_per.gpioStatus
      system.plicCtrl.io.sources(2) := gpioStatusCtrl.io.interrupt

      val spiMaster0Ctrl = Apb3SpiMaster(peripherals.spi0)
      system.apbMapping += spiMaster0Ctrl.io.bus -> (0x40000, 4 kB)
      spiMaster0Ctrl.io.spi <> io_per.spi0
      system.plicCtrl.io.sources(3) := spiMaster0Ctrl.io.interrupt

      val gpio1Ctrl = Apb3Gpio(peripherals.gpio1)
      system.apbMapping += gpio1Ctrl.io.bus -> (0x11000, 4 kB)
      gpio1Ctrl.io.gpio <> io_per.gpio1
      system.plicCtrl.io.sources(4) := gpio1Ctrl.io.interrupt

      val gpio2Ctrl = Apb3Gpio(peripherals.gpio2)
      system.apbMapping += gpio2Ctrl.io.bus -> (0x12000, 4 kB)
      gpio2Ctrl.io.gpio <> io_per.gpio2
      system.plicCtrl.io.sources(5) := gpio2Ctrl.io.interrupt

      val gpio3Ctrl = Apb3Gpio(peripherals.gpio3)
      system.apbMapping += gpio3Ctrl.io.bus -> (0x13000, 4 kB)
      gpio3Ctrl.io.gpio <> io_per.gpio3
      system.plicCtrl.io.sources(6) := gpio3Ctrl.io.interrupt

      val apbDecoder = Apb3Decoder(
        master = system.apbBridge.io.apb,
        slaves = system.apbMapping
      )
    }
  }
}
