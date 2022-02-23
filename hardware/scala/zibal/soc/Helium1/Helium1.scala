package zibal.soc

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.platform.Helium
import zibal.board.BoardParameter

import zibal.misc.{ElementsConfig, ZephyrTools, EmbenchIotTools}

import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._

import zibal.peripherals.io.gpio.{Apb3Gpio, Gpio, GpioCtrl}
import zibal.peripherals.com.uart.{Apb3Uart, Uart, UartCtrl}


object Helium1 {
  def apply(parameter: Helium.Parameter) = Helium1(parameter)

  def prepare(soc: Helium1, elementsConfig: ElementsConfig.ElementsConfig) {
    val dt = ZephyrTools.DeviceTree(elementsConfig)
    dt.generate("helium1.dtsi", "helium", soc.system.axiCrossbar.slavesConfigs,
                soc.system.apbBridge.io.axi, soc.apbMapping, soc.irqMapping)
    val board = ZephyrTools.Board(elementsConfig)
    board.addLed("heartbeat", soc.peripherals.gpioStatusCtrl, 0)
    board.addLed("ok", soc.peripherals.gpioStatusCtrl, 1)
    board.addLed("error", soc.peripherals.gpioStatusCtrl, 2)
    board.addKey("reset", soc.peripherals.gpioStatusCtrl, 3)
    board.generateDeviceTree(soc.peripherals.uartStdCtrl)
    board.generateKconfig()
    val cpuClockDomain = soc.clockCtrl.getClockDomainByName("system")
    board.generateDefconfig(soc.apbMapping, cpuClockDomain)
    EmbenchIotTools(elementsConfig).generate(cpuClockDomain.frequency.getValue)
  }

  case class Parameter(boardParameter: BoardParameter) extends SocParameter(boardParameter, 2) {
    val uartStd = UartCtrl.Parameter.full
    val gpioStatus = GpioCtrl.Parameter(4, 2, (0 to 2), (3 to 3), (3 to 3))
  }

  case class Helium1(parameter: Helium.Parameter) extends Helium.Helium(parameter) {
    var socParameter = parameter.getSocParameter.asInstanceOf[Parameter]
    val io_per = new Bundle {
      val uartStd = master(Uart.Io(socParameter.uartStd))
      val gpioStatus = Gpio.Io(socParameter.gpioStatus)
    }

    val peripherals = new ClockingArea(clockCtrl.getClockDomainByName("system")) {

      val uartStdCtrl = Apb3Uart(socParameter.uartStd)
      uartStdCtrl.io.uart <> io_per.uartStd
      addApbDevice(uartStdCtrl.io.bus, 0x00000, 4 kB)
      addInterrupt(uartStdCtrl.io.interrupt)

      val gpioStatusCtrl = Apb3Gpio(socParameter.gpioStatus)
      gpioStatusCtrl.io.gpio <> io_per.gpioStatus
      addApbDevice(gpioStatusCtrl.io.bus, 0x10000, 4 kB)
      addInterrupt(gpioStatusCtrl.io.interrupt)

      connectPeripherals()
    }
  }
}
