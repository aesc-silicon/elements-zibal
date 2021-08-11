/*
 * Copyright (c) 2021 Phytec Messtechnik GmbH
 */

package zibal.soc

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.platform.Carbon

import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._

import zibal.peripherals.io.gpio.{Apb3Gpio, Gpio, GpioCtrl}
import zibal.peripherals.com.uart.{Apb3Uart, Uart, UartCtrl}
import zibal.peripherals.com.spi.{Apb3SpiMaster, Spi, SpiCtrl}
import zibal.peripherals.com.i2c.{Apb3I2cController, I2c, I2cCtrl}


object Carbon1 {

  def main(args: Array[String]) {
    val socBoard = System.getenv("SOC") + "/" + System.getenv("BOARD")
    val zibalBuildPath = "./../build/"+socBoard+"/zibal/"
    val buildPath = "./../build/"+System.getenv("SOC")+"/"
    val className = this.getClass().getName().stripSuffix("$").split("\\.").last
    val config = SpinalConfig(noRandBoot = false, targetDirectory = zibalBuildPath)

    config.generateVerilog({
      val soc = Carbon1(Peripherals.default)
      soc
    })
  }

  case class Io(peripherals: Peripherals) extends Bundle {
    val uartStd = master(Uart.Io(peripherals.uartStd))
    val gpioStatus = Gpio.Io(peripherals.gpioStatus)
    val gpioA = Gpio.Io(peripherals.gpioA)
    val i2cA = master(I2c.Io(peripherals.i2cA))
  }

  case class Peripherals (
    uartStd: UartCtrl.Parameter,
    gpioStatus: GpioCtrl.Parameter,
    gpioA: GpioCtrl.Parameter,
    i2cA: I2cCtrl.Parameter
  ) {}

  object Peripherals {
    def default = Carbon.Parameter.light(
      Peripherals(
        uartStd = UartCtrl.Parameter.default,
        gpioStatus = GpioCtrl.Parameter(4, 2, (0 to 2), (3 to 3), (3 to 3)),
        gpioA = GpioCtrl.Parameter(7, 2, null, null, null),
        i2cA = I2cCtrl.Parameter.default
      ),
      4
    )
  }

  case class Carbon1(p: Carbon.Parameter) extends Carbon.Carbon(p) {
    var peripherals = p.peripherals.asInstanceOf[Peripherals]
    val io_per = Io(peripherals)

    val pers = new ClockingArea(clocks.systemClockDomain) {

      val uartStdCtrl = Apb3Uart(peripherals.uartStd)
      system.apbMapping += uartStdCtrl.io.bus -> (0x00000, 4 kB)
      uartStdCtrl.io.uart <> io_per.uartStd
      system.irqMapping += 2 -> uartStdCtrl.io.interrupt

      val gpioStatusCtrl = Apb3Gpio(peripherals.gpioStatus)
      system.apbMapping += gpioStatusCtrl.io.bus -> (0x10000, 4 kB)
      gpioStatusCtrl.io.gpio <> io_per.gpioStatus
      system.irqMapping += 3 -> gpioStatusCtrl.io.interrupt

      val gpioACtrl = Apb3Gpio(peripherals.gpioA)
      system.apbMapping += gpioACtrl.io.bus -> (0x11000, 4 kB)
      gpioACtrl.io.gpio <> io_per.gpioA
      system.irqMapping += 4 -> gpioACtrl.io.interrupt

      val i2cControllerACtrl = Apb3I2cController(peripherals.i2cA)
      system.apbMapping += i2cControllerACtrl.io.bus -> (0x50000, 4 kB)
      i2cControllerACtrl.io.i2c <> io_per.i2cA
      system.irqMapping += 5 -> i2cControllerACtrl.io.interrupt

      connectPeripherals()
    }
  }
}
