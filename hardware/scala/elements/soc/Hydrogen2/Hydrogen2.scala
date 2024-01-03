package elements.soc

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._

import zibal.misc._
import zibal.platform.Hydrogen
import zibal.board.BoardParameter
import zibal.soc.SocParameter

import nafarr.peripherals.io.gpio.{Apb3Gpio, Gpio, GpioCtrl}
import nafarr.peripherals.com.uart.{Apb3Uart, Uart, UartCtrl}
import nafarr.peripherals.io.pwm.{Apb3Pwm, Pwm, PwmCtrl}
import nafarr.peripherals.com.i2c.{Apb3I2cController, I2c, I2cCtrl, I2cControllerCtrl}
import nafarr.peripherals.com.spi.{Apb3SpiMaster, Spi, SpiCtrl, SpiMasterCtrl}
import nafarr.peripherals.misc.sevensegment.{Apb3SevenSegment, SevenSegment, SevenSegmentCtrl}

object Hydrogen2 {
  def apply(parameter: Hydrogen.Parameter) = Hydrogen2(parameter)

  case class Parameter(boardParameter: BoardParameter) extends SocParameter(boardParameter, 5) {
    val uartStd = UartCtrl.Parameter.full
    val gpioStatus = GpioCtrl.Parameter(Gpio.Parameter(9), 3, (0 to 2), (3 to 8), (3 to 8))
    val pwmLED = PwmCtrl.Parameter.default(3)
    val i2cTemp = I2cCtrl.Parameter.full
    val i2cA = I2cCtrl.Parameter(
      permission = I2cCtrl.PermissionParameter.full,
      memory = I2cCtrl.MemoryMappedParameter.full,
      io = I2c.Parameter(2)
    )
    val spiA = SpiCtrl.Parameter.full
    val gpioA = GpioCtrl.Parameter(Gpio.Parameter(32), 3)
    val sevenSegment = SevenSegmentCtrl.Parameter.default
  }

  case class Hydrogen2(parameter: Hydrogen.Parameter) extends Hydrogen.Hydrogen(parameter) {
    var socParameter = parameter.getSocParameter.asInstanceOf[Parameter]
    val io_per = new Bundle {
      val uartStd = master(Uart.Io(socParameter.uartStd))
      val gpioStatus = Gpio.Io(socParameter.gpioStatus.io)
      val pwmLED = Pwm.Io(socParameter.pwmLED.io)
      val i2cA = master(I2c.Io(socParameter.i2cA.io))
      val spiA = master(Spi.Io(socParameter.spiA.io))
      val gpioA = Gpio.Io(socParameter.gpioA.io)
      val sevenSegment = SevenSegment.Io(socParameter.sevenSegment)
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

      val i2cACtrl = Apb3I2cController(socParameter.i2cA)
      i2cACtrl.io.i2c <> io_per.i2cA
      addApbDevice(i2cACtrl.io.bus, 0x11000, 4 kB)
      addInterrupt(i2cACtrl.io.interrupt)

      val spiACtrl = Apb3SpiMaster(socParameter.spiA)
      spiACtrl.io.spi <> io_per.spiA
      addApbDevice(spiACtrl.io.bus, 0x12000, 4 kB)
      addInterrupt(spiACtrl.io.interrupt)

      val gpioACtrl = Apb3Gpio(socParameter.gpioA)
      gpioACtrl.io.gpio <> io_per.gpioA
      addApbDevice(gpioACtrl.io.bus, 0x13000, 4 kB)
      addInterrupt(gpioACtrl.io.interrupt)

      val sevenSegmentCtrl = Apb3SevenSegment(socParameter.sevenSegment)
      sevenSegmentCtrl.io.segments <> io_per.sevenSegment
      addApbDevice(sevenSegmentCtrl.io.bus, 0x14000, 4 kB)

      connectPeripherals()
    }
  }
}
