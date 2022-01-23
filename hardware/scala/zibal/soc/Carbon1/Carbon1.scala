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
  def apply() = Carbon1(Parameter.default)

  case class Peripherals (
    uartStd: UartCtrl.Parameter,
    gpioStatus: GpioCtrl.Parameter,
    gpioA: GpioCtrl.Parameter,
    i2cA: I2cCtrl.Parameter
  ) {}

  object Parameter {
    def default = Carbon.Parameter.default(
      Peripherals(
        uartStd = UartCtrl.Parameter.default,
        gpioStatus = GpioCtrl.Parameter(4, 2, (0 to 2), (3 to 3), (3 to 3)),
        gpioA = GpioCtrl.Parameter(7, 2, null, null, null),
        i2cA = I2cCtrl.Parameter.default
      ),
      50 MHz,
      10 MHz,
      4,
      512 Byte
    )
  }

  case class Io(peripherals: Peripherals) extends Bundle {
    val uartStd = master(Uart.Io(peripherals.uartStd))
    val gpioStatus = Gpio.Io(peripherals.gpioStatus)
    val gpioA = Gpio.Io(peripherals.gpioA)
    val i2cA = master(I2c.Io(peripherals.i2cA))
  }

  case class Carbon1(p: Carbon.Parameter) extends Carbon.Carbon(p) {
    var peripherals = p.peripherals.asInstanceOf[Peripherals]
    val io_per = Io(peripherals)

    val pers = new ClockingArea(clocks.systemClockDomain) {

      val uartStdCtrl = Apb3Uart(peripherals.uartStd)
      uartStdCtrl.io.uart <> io_per.uartStd
      addApbDevice(uartStdCtrl.io.bus, 0x00000, 4 kB)
      addInterrupt(uartStdCtrl.io.interrupt)

      val gpioStatusCtrl = Apb3Gpio(peripherals.gpioStatus)
      gpioStatusCtrl.io.gpio <> io_per.gpioStatus
      addApbDevice(gpioStatusCtrl.io.bus, 0x10000, 4 kB)
      addInterrupt(gpioStatusCtrl.io.interrupt)

      val gpioACtrl = Apb3Gpio(peripherals.gpioA)
      gpioACtrl.io.gpio <> io_per.gpioA
      addApbDevice(gpioACtrl.io.bus, 0x11000, 4 kB)
      addInterrupt(gpioACtrl.io.interrupt)

      val i2cControllerACtrl = Apb3I2cController(peripherals.i2cA)
      i2cControllerACtrl.io.i2c <> io_per.i2cA
      addApbDevice(i2cControllerACtrl.io.bus, 0x50000, 4 kB)
      addInterrupt(i2cControllerACtrl.io.interrupt)

      connectPeripherals()
    }
  }
}
