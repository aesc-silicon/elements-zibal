package elements.soc

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._

import zibal.misc._
import zibal.platform.Helium
import zibal.board.BoardParameter
import zibal.soc.SocParameter

import nafarr.peripherals.io.gpio.{Apb3Gpio, Gpio, GpioCtrl}
import nafarr.peripherals.com.uart.{Apb3Uart, Uart, UartCtrl}
import nafarr.peripherals.io.pwm.{Apb3Pwm, Pwm, PwmCtrl}
import nafarr.peripherals.com.i2c.{Apb3I2cController, I2c, I2cCtrl, I2cControllerCtrl}
import nafarr.peripherals.com.spi.{Apb3SpiMaster, Spi, SpiCtrl, SpiMasterCtrl}

object Helium1 {
  def apply(parameter: Helium.Parameter) = Helium1(parameter)

  case class Parameter(boardParameter: BoardParameter) extends SocParameter(boardParameter, 4) {
    val uartStd = UartCtrl.Parameter.full
    val gpioStatus = GpioCtrl.Parameter(Gpio.Parameter(4), 3, (0 to 2), (3 to 3), (3 to 3))
    val pwmLED = PwmCtrl.Parameter.default(3)
    val i2cHDMI = I2cCtrl.Parameter.full
    val spiFlash = SpiCtrl.Parameter.full
  }

  case class Helium1(parameter: Helium.Parameter) extends Helium.Helium(parameter) {
    var socParameter = parameter.getSocParameter.asInstanceOf[Parameter]
    val io_per = new Bundle {
      val uartStd = master(Uart.Io(socParameter.uartStd))
      val gpioStatus = Gpio.Io(socParameter.gpioStatus.io)
      val pwmLED = Pwm.Io(socParameter.pwmLED.io)
      val i2cHDMI = master(I2c.Io(socParameter.i2cHDMI.io))
      val spiFlash = master(Spi.Io(socParameter.spiFlash.io))
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

      val pwmLEDCtrl = Apb3Pwm(socParameter.pwmLED)
      pwmLEDCtrl.io.pwm <> io_per.pwmLED
      addApbDevice(pwmLEDCtrl.io.bus, 0x10000, 4 kB)

      val i2cHDMICtrl = Apb3I2cController(socParameter.i2cHDMI)
      i2cHDMICtrl.io.i2c <> io_per.i2cHDMI
      addApbDevice(i2cHDMICtrl.io.bus, 0x11000, 4 kB)
      addInterrupt(i2cHDMICtrl.io.interrupt)

      val spiFlashCtrl = Apb3SpiMaster(socParameter.spiFlash)
      spiFlashCtrl.io.spi <> io_per.spiFlash
      addApbDevice(spiFlashCtrl.io.bus, 0x12000, 4 kB)
      addInterrupt(spiFlashCtrl.io.interrupt)

      connectPeripherals()
    }
  }
}
