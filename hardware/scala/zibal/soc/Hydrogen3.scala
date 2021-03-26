/*
 * Copyright (c) 2021 Phytec Messtechnik GmbH
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
import zibal.peripherals.multimedia.vga.{Apb3Vga, Vga, VgaCtrl}
import zibal.multimedia.PixelScaler
import zibal.multimedia.framebuffer.Apb3SlowFramebuffer


object Hydrogen3 {
  def main(args: Array[String]) {
    val config = SpinalConfig(noRandBoot = false,
                              targetDirectory = "./../build/"+System.getenv("BOARD")+"/zibal/")
    config.generateVerilog({
      val toplevel = Hydrogen3(Peripherals.default)
      BinTools.initRam(toplevel.system.onChipRam.ram,
                       "../build/"+System.getenv("BOARD")+"/zephyr/zephyr/zephyr.bin")
      toplevel
    })
  }

  case class Peripherals (
    uartStd: UartCtrl.Parameter,
    gpioStatus: GpioCtrl.Parameter,
    gpio1: GpioCtrl.Parameter,
    vga0: VgaCtrl.Parameter
  ) {}

  object Peripherals {
    def default = Hydrogen.Parameter.default(
      Peripherals(
        uartStd = UartCtrl.Parameter.full,
        gpioStatus = GpioCtrl.Parameter(4, 2, (0 to 2), (3 to 3), (3 to 3)),
        gpio1 = GpioCtrl.Parameter(5, 2, null, null, null),
        vga0 = VgaCtrl.Parameter.default
      ),
      3
    )
  }

  case class Hydrogen3(p: Hydrogen.Parameter) extends Hydrogen.Hydrogen(p) {
    var peripherals = p.peripherals.asInstanceOf[Peripherals]
    val io_per = new Bundle {
      val uartStd = master(Uart.Io(peripherals.uartStd))
      val gpioStatus = Gpio.Io(peripherals.gpioStatus)
      val gpio1 = Gpio.Io(peripherals.gpio1)
      val vga0 = master(Vga.Io(peripherals.vga0))
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

      val vga0Ctrl = Apb3Vga(peripherals.vga0)
      system.apbMapping += vga0Ctrl.io.bus -> (0x30000, 4 kB)
      vga0Ctrl.io.vga <> io_per.vga0

      val scale = 8
      val pixelScaler = PixelScaler(peripherals.vga0.multimediaConfig, scale)
      val framebufferCtrl = Apb3SlowFramebuffer(peripherals.vga0.multimediaConfig, scale)
      system.apbMapping += framebufferCtrl.io.bus -> (0x50000, 4 kB)
      pixelScaler.io.source <> vga0Ctrl.io.stream
      framebufferCtrl.io.stream <> pixelScaler.io.sink

      val apbDecoder = Apb3Decoder(
        master = system.apbBridge.io.apb,
        slaves = system.apbMapping
      )
    }
  }
}
