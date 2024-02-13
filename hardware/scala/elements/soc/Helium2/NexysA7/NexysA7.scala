package elements.soc.helium2

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import nafarr.system.reset._
import nafarr.system.reset.ResetControllerCtrl._
import nafarr.system.clock._
import nafarr.system.clock.ClockControllerCtrl._
import nafarr.blackboxes.xilinx.a7._

import zibal.misc._
import zibal.platform.Helium
import zibal.board.{KitParameter, BoardParameter}

import elements.sdk.ElementsApp
import elements.board.NexysA7
import elements.soc.Helium2

case class NexysA7Board() extends Component {
  val io = new Bundle {
    val clock = inout(Analog(Bool))
    val jtag = new Bundle {
      val tdo = inout(Analog(Bool))
    }
    val uartStd = new Bundle {
      val txd = inout(Analog(Bool))
      val rxd = inout(Analog(Bool))
      val rts = inout(Analog(Bool))
      val cts = inout(Analog(Bool))
    }
    val gpioStatus = Vec(inout(Analog(Bool())), 9)
    val pwmLED = Vec(inout(Analog(Bool())), 3)
    val sevenSegment = new Bundle {
      val cathodes = Vec(inout(Analog(Bool())), 8)
      val anodes = Vec(inout(Analog(Bool())), 8)
    }
    val gpioA = Vec(inout(Analog(Bool())), 32)
  }

  val top = NexysA7Top()
  val analogFalse = Analog(Bool)
  analogFalse := False
  val analogTrue = Analog(Bool)
  analogTrue := True

  top.io.clock.PAD := io.clock

  top.io.jtag.tms.PAD := analogFalse
  top.io.jtag.tdi.PAD := analogFalse
  io.jtag.tdo := top.io.jtag.tdo.PAD
  top.io.jtag.tck.PAD := analogFalse
  top.io.uartStd.rxd.PAD := io.uartStd.rxd
  io.uartStd.txd := top.io.uartStd.txd.PAD
  top.io.uartStd.cts.PAD := io.uartStd.cts
  io.uartStd.rts := top.io.uartStd.rts.PAD

  for (index <- 0 until top.io.gpioStatus.length) {
    io.gpioStatus(index) <> top.io.gpioStatus(index).PAD
  }
  for (index <- 0 until top.io.pwmLED.length) {
    io.pwmLED(index) <> top.io.pwmLED(index).PAD
  }

  for (index <- 0 until top.io.sevenSegment.cathodes.length) {
    io.sevenSegment.cathodes(index) <> top.io.sevenSegment.cathodes(index).PAD
  }
  for (index <- 0 until top.io.sevenSegment.anodes.length) {
    io.sevenSegment.anodes(index) <> top.io.sevenSegment.anodes(index).PAD
  }

  for (index <- 0 until top.io.gpioA.length) {
    io.gpioA(index) <> top.io.gpioA(index).PAD
  }

  top.io.i2cA.scl.PAD := analogFalse
  top.io.i2cA.sda.PAD := analogFalse
  top.io.i2cA.int.PAD := analogFalse
  top.io.i2cA.intCrit.PAD := analogFalse
  top.io.spiA.mosi.PAD := analogFalse
  top.io.spiA.miso.PAD := analogFalse
  top.io.spiA.csN.PAD := analogFalse
  top.io.spiA.sclk.PAD := analogFalse

  val baudPeriod = top.soc.socParameter.uartStd.init.getBaudPeriod()

  def simHook() {
    for ((domain, index) <- top.soc.parameter.getKitParameter.clocks.zipWithIndex) {
      val clockDomain = top.soc.clockCtrl.getClockDomainByName(domain.name)
      SimulationHelper.generateEndlessClock(clockDomain.clock, domain.frequency)
    }
  }
}

case class NexysA7Top() extends Component {
  val resets = List[ResetParameter](ResetParameter("system", 64), ResetParameter("debug", 64))
  val clocks = List[ClockParameter](
    ClockParameter("system", 100 MHz, "system"),
    ClockParameter("debug", 100 MHz, "debug", synchronousWith = "system")
  )

  val kitParameter = KitParameter(resets, clocks)
  val boardParameter = NexysA7.Parameter(kitParameter, NexysA7.SystemClock.frequency)
  val socParameter = Helium2.Parameter(boardParameter)
  val parameter = Helium.Parameter(
    socParameter,
    128 kB,
    (resetCtrl: ResetControllerCtrl, _, clock: Bool) => { resetCtrl.buildXilinx(clock) },
    (clockCtrl: ClockControllerCtrl, resetCtrl: ResetControllerCtrl, clock: Bool) => {
      clockCtrl.buildDummy(clock)
    }
  )

  val io = new Bundle {
    val clock = XilinxCmosIo(NexysA7.SystemClock.clock).clock(NexysA7.SystemClock.frequency)
    val jtag = new Bundle {
      val tms = XilinxCmosIo(NexysA7.Jtag.tms)
      val tdi = XilinxCmosIo(NexysA7.Jtag.tdi)
      val tdo = XilinxCmosIo(NexysA7.Jtag.tdo)
      val tck =
        XilinxCmosIo(NexysA7.Jtag.tck).clock(NexysA7.Jtag.frequency).disableDedicatedClockRoute
    }
    val uartStd = new Bundle {
      val txd = XilinxCmosIo(NexysA7.UartStd.txd)
      val rxd = XilinxCmosIo(NexysA7.UartStd.rxd)
      val rts = XilinxCmosIo(NexysA7.UartStd.rts)
      val cts = XilinxCmosIo(NexysA7.UartStd.cts)
    }
    val gpioStatus = Vec(
      XilinxCmosIo(NexysA7.LEDs.LED16.blue),
      XilinxCmosIo(NexysA7.LEDs.LED16.red),
      XilinxCmosIo(NexysA7.LEDs.LED16.green),
      XilinxCmosIo(NexysA7.Buttons.cpuResetN),
      XilinxCmosIo(NexysA7.Buttons.buttonCenter),
      XilinxCmosIo(NexysA7.Buttons.buttonUp),
      XilinxCmosIo(NexysA7.Buttons.buttonRight),
      XilinxCmosIo(NexysA7.Buttons.buttonDown),
      XilinxCmosIo(NexysA7.Buttons.buttonLeft)
    )
    val pwmLED = Vec(
      XilinxCmosIo(NexysA7.LEDs.LED17.red),
      XilinxCmosIo(NexysA7.LEDs.LED17.green),
      XilinxCmosIo(NexysA7.LEDs.LED17.blue)
    )
    val i2cA = new Bundle {
      val scl = XilinxCmosIo(NexysA7.I2c.Tmp.scl)
      val sda = XilinxCmosIo(NexysA7.I2c.Tmp.sda)
      val int = XilinxCmosIo(NexysA7.I2c.Tmp.int)
      val intCrit = XilinxCmosIo(NexysA7.I2c.Tmp.intCritical)
    }
    val spiA = new Bundle {
      val sclk = XilinxCmosIo(NexysA7.Spi.Acl.sclk)
      val csN = XilinxCmosIo(NexysA7.Spi.Acl.csN)
      val mosi = XilinxCmosIo(NexysA7.Spi.Acl.mosi)
      val miso = XilinxCmosIo(NexysA7.Spi.Acl.miso)
    }
    val gpioA = Vec(
      XilinxCmosIo(NexysA7.LEDs.led0),
      XilinxCmosIo(NexysA7.LEDs.led1),
      XilinxCmosIo(NexysA7.LEDs.led2),
      XilinxCmosIo(NexysA7.LEDs.led3),
      XilinxCmosIo(NexysA7.LEDs.led4),
      XilinxCmosIo(NexysA7.LEDs.led5),
      XilinxCmosIo(NexysA7.LEDs.led6),
      XilinxCmosIo(NexysA7.LEDs.led7),
      XilinxCmosIo(NexysA7.LEDs.led8),
      XilinxCmosIo(NexysA7.LEDs.led9),
      XilinxCmosIo(NexysA7.LEDs.led10),
      XilinxCmosIo(NexysA7.LEDs.led11),
      XilinxCmosIo(NexysA7.LEDs.led12),
      XilinxCmosIo(NexysA7.LEDs.led13),
      XilinxCmosIo(NexysA7.LEDs.led14),
      XilinxCmosIo(NexysA7.LEDs.led15),
      XilinxCmosIo(NexysA7.Buttons.sw0),
      XilinxCmosIo(NexysA7.Buttons.sw1),
      XilinxCmosIo(NexysA7.Buttons.sw2),
      XilinxCmosIo(NexysA7.Buttons.sw3),
      XilinxCmosIo(NexysA7.Buttons.sw4),
      XilinxCmosIo(NexysA7.Buttons.sw5),
      XilinxCmosIo(NexysA7.Buttons.sw6),
      XilinxCmosIo(NexysA7.Buttons.sw7),
      XilinxCmosIo(NexysA7.Buttons.sw8),
      XilinxCmosIo(NexysA7.Buttons.sw9),
      XilinxCmosIo(NexysA7.Buttons.sw10),
      XilinxCmosIo(NexysA7.Buttons.sw11),
      XilinxCmosIo(NexysA7.Buttons.sw12),
      XilinxCmosIo(NexysA7.Buttons.sw13),
      XilinxCmosIo(NexysA7.Buttons.sw14),
      XilinxCmosIo(NexysA7.Buttons.sw15)
    )
    val sevenSegment = new Bundle {
      val cathodes = Vec(
        XilinxCmosIo(NexysA7.SevenSegment.Cathodes.ca),
        XilinxCmosIo(NexysA7.SevenSegment.Cathodes.cb),
        XilinxCmosIo(NexysA7.SevenSegment.Cathodes.cc),
        XilinxCmosIo(NexysA7.SevenSegment.Cathodes.cd),
        XilinxCmosIo(NexysA7.SevenSegment.Cathodes.ce),
        XilinxCmosIo(NexysA7.SevenSegment.Cathodes.cf),
        XilinxCmosIo(NexysA7.SevenSegment.Cathodes.cg),
        XilinxCmosIo(NexysA7.SevenSegment.dp)
      )
      val anodes = Vec(
        XilinxCmosIo(NexysA7.SevenSegment.Anodes.an0),
        XilinxCmosIo(NexysA7.SevenSegment.Anodes.an1),
        XilinxCmosIo(NexysA7.SevenSegment.Anodes.an2),
        XilinxCmosIo(NexysA7.SevenSegment.Anodes.an3),
        XilinxCmosIo(NexysA7.SevenSegment.Anodes.an4),
        XilinxCmosIo(NexysA7.SevenSegment.Anodes.an5),
        XilinxCmosIo(NexysA7.SevenSegment.Anodes.an6),
        XilinxCmosIo(NexysA7.SevenSegment.Anodes.an7)
      )
    }
  }

  val soc = Helium2(parameter)

  io.clock <> IBUF(soc.io_plat.clock)

  io.jtag.tms <> IBUF(soc.io_plat.jtag.tms)
  io.jtag.tdi <> IBUF(soc.io_plat.jtag.tdi)
  io.jtag.tdo <> OBUF(soc.io_plat.jtag.tdo)
  io.jtag.tck <> IBUF(soc.io_plat.jtag.tck)

  io.uartStd.txd <> OBUF(soc.io_per.uartStd.txd)
  io.uartStd.rxd <> IBUF(soc.io_per.uartStd.rxd)
  io.uartStd.rts <> OBUF(soc.io_per.uartStd.rts)
  io.uartStd.cts <> IBUF(soc.io_per.uartStd.cts)

  for (index <- 0 until io.gpioStatus.length) {
    io.gpioStatus(index) <> IOBUF(soc.io_per.gpioStatus.pins(index))
  }
  for (index <- 0 until io.pwmLED.length) {
    io.pwmLED(index) <> OBUF(soc.io_per.pwmLED.output(index))
  }

  io.i2cA.scl <> IOBUF(soc.io_per.i2cA.scl)
  io.i2cA.sda <> IOBUF(soc.io_per.i2cA.sda)
  io.i2cA.int <> IBUF(soc.io_per.i2cA.interrupts(0))
  io.i2cA.intCrit <> IBUF(soc.io_per.i2cA.interrupts(1))

  io.spiA.sclk <> OBUF(soc.io_per.spiA.sclk)
  io.spiA.csN <> OBUF(soc.io_per.spiA.cs(0))
  io.spiA.mosi <> IOBUF(soc.io_per.spiA.dq(0))
  io.spiA.miso <> IOBUF(soc.io_per.spiA.dq(1))

  for (index <- 0 until io.gpioA.length) {
    io.gpioA(index) <> IOBUF(soc.io_per.gpioA.pins(index))
  }

  for (index <- 0 until io.sevenSegment.cathodes.length) {
    io.sevenSegment.cathodes(index) <> OBUF(soc.io_per.sevenSegment.value(index))
  }
  for (index <- 0 until io.sevenSegment.anodes.length) {
    io.sevenSegment.anodes(index) <> OBUF(soc.io_per.sevenSegment.select(index))
  }
}

object NexysA7Generate extends ElementsApp {
  elementsConfig.genFPGASpinalConfig.generateVerilog {
    val top = NexysA7Top()

    val xdc = XilinxTools.Xdc(elementsConfig)
    top.soc.clockCtrl.generatedClocks foreach { clock => xdc.addGeneratedClock(clock) }
    xdc.generate(top.io)

    top.soc.initOnChipRam(elementsConfig.zephyrBinary)
    top
  }
}

object NexysA7Simulate extends ElementsApp {
  val compiled = elementsConfig.genFPGASimConfig.compile {
    val board = NexysA7Board()
    board.top.soc.initOnChipRam(elementsConfig.zephyrBinary)
    for (domain <- board.top.soc.parameter.getKitParameter.clocks) {
      board.top.soc.clockCtrl.getClockDomainByName(domain.name).clock.simPublic()
    }
    board
  }
  simType match {
    case "simulate" =>
      compiled.doSimUntilVoid("simulate") { dut =>
        dut.simHook()
        val testCases = TestCases()
        testCases.addClock(
          dut.io.clock,
          NexysA7.SystemClock.frequency,
          simDuration.toString.toInt ms
        )
      }
    case "boot" =>
      compiled.doSimUntilVoid("boot") { dut =>
        dut.simHook()
        val testCases = TestCases()
        testCases.addClockWithTimeout(dut.io.clock, NexysA7.SystemClock.frequency, 20 ms)
        testCases.uartRxIdle(dut.io.uartStd.rxd)
        testCases.boot(dut.io.uartStd.txd, dut.baudPeriod)
      }
    case "mtimer" =>
      compiled.doSimUntilVoid("mtimer") { dut =>
        dut.simHook()
        val testCases = TestCases()
        testCases.addClockWithTimeout(dut.io.clock, NexysA7.SystemClock.frequency, 20 ms)
        testCases.uartRxIdle(dut.io.uartStd.rxd)
        testCases.heartbeat(dut.io.gpioStatus(0))
      }
    case "reset" =>
      compiled.doSimUntilVoid("reset") { dut =>
        dut.simHook()
        val testCases = TestCases()
        testCases.addClockWithTimeout(dut.io.clock, NexysA7.SystemClock.frequency, 25 ms)
        testCases.uartRxIdle(dut.io.uartStd.rxd)
        testCases.reset(dut.io.uartStd.txd, dut.baudPeriod)
      }
    case _ =>
      println(s"Unknown simulation ${simType}")
  }
}
