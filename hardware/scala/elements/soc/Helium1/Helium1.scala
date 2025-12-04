// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

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
import nafarr.peripherals.io.pio.{Apb3Pio, Pio, PioCtrl}
import nafarr.peripherals.io.pwm.{Apb3Pwm, Pwm, PwmCtrl}
import nafarr.peripherals.com.uart.{Apb3Uart, Uart, UartCtrl}

object Helium1 {
  def apply(parameter: Helium.Parameter) = Helium1(parameter)

  case class Parameter(boardParameter: BoardParameter) extends SocParameter(boardParameter, 2) {
    val uartStd = UartCtrl.Parameter.full()
    val gpioStatus = GpioCtrl.Parameter(Gpio.Parameter(4), 3, (0 to 2), (3 to 3), (3 to 3))
    val pwmA = PwmCtrl.Parameter.default(3)
    val pioA = PioCtrl.Parameter.default(3)
  }

  case class Helium1(parameter: Helium.Parameter) extends Helium.Helium(parameter) {
    var socParameter = parameter.getSocParameter.asInstanceOf[Parameter]
    val io_per = new Bundle {
      val uartStd = master(Uart.Io(socParameter.uartStd))
      val gpioStatus = Gpio.Io(socParameter.gpioStatus.io)
      val pwmA = Pwm.Io(socParameter.pwmA.io)
      val pioA = Pio.Io(socParameter.pioA.io)
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

      val pwmACtrl = Apb3Pwm(socParameter.pwmA)
      pwmACtrl.io.pwm <> io_per.pwmA
      addApbDevice(pwmACtrl.io.bus, 0x2000, 4 kB)

      val pioACtrl = Apb3Pio(socParameter.pioA)
      pioACtrl.io.pio <> io_per.pioA
      addApbDevice(pioACtrl.io.bus, 0x3000, 4 kB)

      connectPeripherals()
    }
  }
}
