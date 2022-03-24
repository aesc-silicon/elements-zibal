package zibal.soc

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.platform.Helium
import zibal.board.BoardParameter

import zibal.misc.{ElementsConfig, ZephyrTools, EmbenchIotTools}

import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._

import nafarr.peripherals.io.gpio.{Apb3Gpio, Gpio, GpioCtrl}
import nafarr.peripherals.com.uart.{Apb3Uart, Uart, UartCtrl}
import nafarr.peripherals.multimedia.vga.{Apb3Vga, Vga, VgaCtrl}
import nafarr.multimedia.PixelScaler
import nafarr.multimedia.framebuffer.Apb3SlowFramebuffer


object Helium2 {
  def apply(parameter: Helium.Parameter) = Helium2(parameter)

  def prepare(soc: Helium2, elementsConfig: ElementsConfig.ElementsConfig) {
    val dt = ZephyrTools.DeviceTree(elementsConfig)
    dt.generate("helium2.dtsi", "helium", soc.system.axiCrossbar.slavesConfigs,
                soc.system.apbBridge.io.axi, soc.apbMapping, soc.irqMapping)
    val board = ZephyrTools.Board(elementsConfig)
    board.addLed("heartbeat", soc.peripherals.gpioStatusCtrl, 0)
    board.addLed("ok", soc.peripherals.gpioStatusCtrl, 1)
    board.addLed("error", soc.peripherals.gpioStatusCtrl, 2)
    board.addKey("reset", soc.peripherals.gpioStatusCtrl, 3)
    board.addKey("player1_back_left", soc.peripherals.gpioACtrl, 0)
    board.addKey("player1_back_right", soc.peripherals.gpioACtrl, 1)
    board.addKey("player1_arrow_down", soc.peripherals.gpioACtrl, 2)
    board.addKey("player1_arrow_left", soc.peripherals.gpioACtrl, 3)
    board.addKey("player1_arrow_up", soc.peripherals.gpioACtrl, 4)
    board.addKey("player1_arrow_right", soc.peripherals.gpioACtrl, 5)
    board.addKey("player1_b", soc.peripherals.gpioACtrl, 6)
    board.addKey("player1_a", soc.peripherals.gpioACtrl, 7)
    board.addKey("player1_y", soc.peripherals.gpioACtrl, 8)
    board.addKey("player1_x", soc.peripherals.gpioACtrl, 9)
    board.addKey("player1_select", soc.peripherals.gpioACtrl, 10)
    board.addKey("player1_start", soc.peripherals.gpioACtrl, 11)
    board.generateDeviceTree(soc.peripherals.uartStdCtrl)
    board.generateKconfig()
    val cpuClockDomain = soc.clockCtrl.getClockDomainByName("system")
    board.generateDefconfig(soc.apbMapping, cpuClockDomain)
    EmbenchIotTools(elementsConfig).generate(cpuClockDomain.frequency.getValue)
  }

  case class Parameter(boardParameter: BoardParameter) extends SocParameter(boardParameter, 3) {
    val uartStd = UartCtrl.Parameter(
      permission = UartCtrl.PermissionParameter.full,
      memory = UartCtrl.MemoryMappedParameter.full,
      init = UartCtrl.InitParameter.default(115200),
      flowControl = false
    )

    val gpioStatus = GpioCtrl.Parameter(4, 2, (0 to 2), (3 to 3), (3 to 3))
    val gpioA = GpioCtrl.Parameter(12, 2, null, null, null)
    val vgaA = VgaCtrl.Parameter.default
  }

  case class Helium2(parameter: Helium.Parameter) extends Helium.Helium(parameter) {
    var socParameter = parameter.getSocParameter.asInstanceOf[Parameter]
    val io_per = new Bundle {
      val uartStd = master(Uart.Io(socParameter.uartStd))
      val gpioStatus = Gpio.Io(socParameter.gpioStatus)
      val gpioA = Gpio.Io(socParameter.gpioA)
      val vgaA = master(Vga.Io(socParameter.vgaA))
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

      val gpioACtrl = Apb3Gpio(socParameter.gpioA)
      gpioACtrl.io.gpio <> io_per.gpioA
      addApbDevice(gpioACtrl.io.bus, 0x11000, 4 kB)
      addInterrupt(gpioACtrl.io.interrupt)

      val vgaACtrl = Apb3Vga(socParameter.vgaA)
      vgaACtrl.io.vga <> io_per.vgaA
      addApbDevice(vgaACtrl.io.bus, 0x30000, 4 kB)

      val scale = 8
      val pixelScaler = PixelScaler(socParameter.vgaA.multimediaConfig, scale)
      val framebufferCtrl = Apb3SlowFramebuffer(socParameter.vgaA.multimediaConfig, scale)
      pixelScaler.io.source <> vgaACtrl.io.stream
      framebufferCtrl.io.stream <> pixelScaler.io.sink
      addApbDevice(framebufferCtrl.io.bus, 0x50000, 4 kB)

      connectPeripherals()
    }
  }
}
