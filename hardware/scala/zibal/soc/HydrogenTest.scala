/*
 * Copyright (c) 2020 Phytec Messtechnik GmbH
 */

package zibal.soc

import spinal.core._
import spinal.lib._

import zibal.platform.Hydrogen

import zibal.misc.BinTools
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import spinal.lib.io.TriStateArray

import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._

import zibal.peripherals.io.gpio.{Apb3Gpio, Gpio, GpioCtrl}
import zibal.peripherals.com.uart.{Apb3Uart, Uart, UartCtrl}
import zibal.peripherals.com.spi.{Apb3SpiMaster, Spi, SpiCtrl}
import zibal.peripherals.com.i2c.{Apb3I2cController, I2c, I2cCtrl}
import zibal.peripherals.misc.frequencycounter.{Apb3FrequencyCounter, FrequencyCounter, FrequencyCounterCtrl}


object HydrogenTest {
  def main(args: Array[String]) {
    val config = SpinalConfig(noRandBoot = false, targetDirectory = "./../build/zibal/")
    config.generateVerilog({
      val toplevel = HydrogenTest(Peripherals.default)
      BinTools
        .initRam(toplevel.system.onChipRam.ram, "../build/zephyr/zephyr/zephyr.bin")
      toplevel
    })
  }

  case class Peripherals (
    uartStd: UartCtrl.Parameter,
    gpioStatus: GpioCtrl.Parameter,
    gpio1: GpioCtrl.Parameter,
    spi0: SpiCtrl.Parameter,
    i2c0: I2cCtrl.Parameter,
    freqCounter0: FrequencyCounterCtrl.Parameter
  ) {}

  object Peripherals {
    def default = Hydrogen.Parameter.default(
      Peripherals(
        uartStd = UartCtrl.Parameter.default,
        gpioStatus = GpioCtrl.Parameter(4, 2, (0 to 2), (3 to 3), (3 to 3)),
        gpio1 = GpioCtrl.Parameter(4, 2, null, null, null),
        spi0 = SpiCtrl.Parameter.default,
        i2c0 = I2cCtrl.Parameter.default,
        freqCounter0 = FrequencyCounterCtrl.Parameter.default
      ),
      5
    )
  }

  case class HydrogenTest(p: Hydrogen.Parameter) extends Hydrogen.Hydrogen(p) {
    var peripherals = p.peripherals.asInstanceOf[Peripherals]
    val io_per = new Bundle {
      val uartStd = master(Uart.Io(peripherals.uartStd))
      val gpioStatus = Gpio.Io(peripherals.gpioStatus)
      val gpio1 = Gpio.Io(peripherals.gpio1)
      val spi0 = master(Spi.Io(peripherals.spi0))
      val i2c0 = master(I2c.Io(peripherals.i2c0))
      val freqCounter0 = FrequencyCounter.Io(peripherals.freqCounter0)
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

      val gpio1Ctrl = Apb3Gpio(peripherals.gpio1)
      system.apbMapping += gpio1Ctrl.io.bus -> (0x11000, 4 kB)
      gpio1Ctrl.io.gpio <> io_per.gpio1
      system.plicCtrl.io.sources(3) := gpio1Ctrl.io.interrupt

      val spiMaster0Ctrl = Apb3SpiMaster(peripherals.spi0)
      system.apbMapping += spiMaster0Ctrl.io.bus -> (0x40000, 4 kB)
      spiMaster0Ctrl.io.spi <> io_per.spi0
      system.plicCtrl.io.sources(4) := spiMaster0Ctrl.io.interrupt

      val i2cController0Ctrl = Apb3I2cController(peripherals.i2c0)
      system.apbMapping += i2cController0Ctrl.io.bus -> (0x50000, 4 kB)
      i2cController0Ctrl.io.i2c <> io_per.i2c0
      system.plicCtrl.io.sources(5) := i2cController0Ctrl.io.interrupt

      val frequencyCounter0Ctrl = Apb3FrequencyCounter(peripherals.freqCounter0)
      system.apbMapping += frequencyCounter0Ctrl.io.bus -> (0x60000, 4 kB)
      frequencyCounter0Ctrl.io.clock <> io_per.freqCounter0

      val apbDecoder = Apb3Decoder(
        master = system.apbBridge.io.apb,
        slaves = system.apbMapping
      )
    }
  }
}
