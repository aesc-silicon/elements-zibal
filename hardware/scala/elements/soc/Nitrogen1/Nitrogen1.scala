package elements.soc

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._

import zibal.misc._
import zibal.platform.Nitrogen
import zibal.board.BoardParameter
import zibal.soc.SocParameter

import nafarr.peripherals.io.gpio.{Apb3Gpio, Gpio, GpioCtrl}
import nafarr.peripherals.com.uart.{Apb3Uart, Uart, UartCtrl}
import nafarr.peripherals.com.i2c.{Apb3I2cController, I2c, I2cCtrl, I2cControllerCtrl}
import nafarr.peripherals.com.spi.{Apb3SpiController, Spi, SpiCtrl, SpiControllerCtrl}

object Nitrogen1 {
  def apply(parameter: Nitrogen.Parameter) = Nitrogen1(parameter)

  case class Parameter(boardParameter: BoardParameter) extends SocParameter(boardParameter, 2) {
    val uartStd = UartCtrl.Parameter.full()
    val gpioStatus = GpioCtrl.Parameter(Gpio.Parameter(4), 3, (0 to 2), (3 to 3), (3 to 3))
  }

  case class Nitrogen1(parameter: Nitrogen.Parameter) extends Nitrogen.Nitrogen(parameter) {
    var socParameter = parameter.getSocParameter.asInstanceOf[Parameter]
    val io_per = new Bundle {
      val uartStd = master(Uart.Io(socParameter.uartStd))
      val gpioStatus = Gpio.Io(socParameter.gpioStatus.io)
    }

    val peripherals = new ClockingArea(clockCtrl.getClockDomainByName("system")) {

      val uartStdCtrl = Apb3Uart(socParameter.uartStd)
      uartStdCtrl.io.uart <> io_per.uartStd
      addApbDevice(uartStdCtrl.io.bus, 0x00000, 4 kB)
      addInterrupt(uartStdCtrl.io.interrupt)

      val gpioStatusCtrl = Apb3Gpio(socParameter.gpioStatus)
      gpioStatusCtrl.io.gpio <> io_per.gpioStatus
      addApbDevice(gpioStatusCtrl.io.bus, 0x1000, 4 kB)
      addInterrupt(gpioStatusCtrl.io.interrupt)

      connectPeripherals()
    }
  }
}
