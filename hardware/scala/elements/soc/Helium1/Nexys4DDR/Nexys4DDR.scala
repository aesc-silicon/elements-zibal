package elements.soc.helium1

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
import elements.board.Nexys4DDR
import elements.soc.Helium1

case class Nexys4DDRBoard() extends Component {
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
    val gpioStatus = Vec(inout(Analog(Bool())), 2)
  }

  val top = Nexys4DDRTop()
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

  for (index <- 0 until 2) {
    io.gpioStatus(index) <> top.io.gpioStatus(index).PAD
  }

  val baudPeriod = top.soc.socParameter.uartStd.init.getBaudPeriod()

  def simHook() {
    for ((domain, index) <- top.soc.parameter.getKitParameter.clocks.zipWithIndex) {
      val clockDomain = top.soc.clockCtrl.getClockDomainByName(domain.name)
      SimulationHelper.generateEndlessClock(clockDomain.clock, domain.frequency)
    }
  }
}

case class Nexys4DDRTop() extends Component {
  val resets = List[ResetParameter](ResetParameter("system", 64), ResetParameter("debug", 64))
  val clocks = List[ClockParameter](
    ClockParameter("system", 100 MHz, "system"),
    ClockParameter("debug", 100 MHz, "debug", synchronousWith = "system")
  )

  val kitParameter = KitParameter(resets, clocks)
  val boardParameter = Nexys4DDR.Parameter(kitParameter, Nexys4DDR.SystemClock.frequency)
  val socParameter = Helium1.Parameter(boardParameter)
  val parameter = Helium.Parameter(
    socParameter,
    128 kB,
    (resetCtrl: ResetControllerCtrl, _, clock: Bool) => { resetCtrl.buildXilinx(clock) },
    (clockCtrl: ClockControllerCtrl, resetCtrl: ResetControllerCtrl, clock: Bool) => {
      clockCtrl.buildDummy(clock)
    }
  )

  val io = new Bundle {
    val clock = XilinxCmosIo(Nexys4DDR.SystemClock.clock).clock(Nexys4DDR.SystemClock.frequency)
    val jtag = new Bundle {
      val tms = XilinxCmosIo(Nexys4DDR.Jtag.tms)
      val tdi = XilinxCmosIo(Nexys4DDR.Jtag.tdi)
      val tdo = XilinxCmosIo(Nexys4DDR.Jtag.tdo)
      val tck =
        XilinxCmosIo(Nexys4DDR.Jtag.tck).clock(Nexys4DDR.Jtag.frequency).disableDedicatedClockRoute
    }
    val uartStd = new Bundle {
      val txd = XilinxCmosIo(Nexys4DDR.UartStd.txd)
      val rxd = XilinxCmosIo(Nexys4DDR.UartStd.rxd)
      val rts = XilinxCmosIo(Nexys4DDR.UartStd.rts)
      val cts = XilinxCmosIo(Nexys4DDR.UartStd.cts)
    }
    val gpioStatus = Vec(
      XilinxCmosIo(Nexys4DDR.LEDs.LED16.blue),
      XilinxCmosIo(Nexys4DDR.Buttons.cpuResetN)
    )
  }

  val soc = Helium1(parameter)

  io.clock <> IBUF(soc.io_plat.clock)

  io.jtag.tms <> IBUF(soc.io_plat.jtag.tms)
  io.jtag.tdi <> IBUF(soc.io_plat.jtag.tdi)
  io.jtag.tdo <> OBUF(soc.io_plat.jtag.tdo)
  io.jtag.tck <> IBUF(soc.io_plat.jtag.tck)

  io.uartStd.txd <> OBUF(soc.io_per.uartStd.txd)
  io.uartStd.rxd <> IBUF(soc.io_per.uartStd.rxd)
  io.uartStd.rts <> OBUF(soc.io_per.uartStd.rts)
  io.uartStd.cts <> IBUF(soc.io_per.uartStd.cts)

  for (index <- 0 until 2) {
    io.gpioStatus(index) <> IOBUF(soc.io_per.gpioStatus.pins(index))
  }
}

object Nexys4DDRGenerate extends ElementsApp {
  elementsConfig.genFPGASpinalConfig.generateVerilog {
    val top = Nexys4DDRTop()

    val xdc = XilinxTools.Xdc(elementsConfig)
    top.soc.clockCtrl.generatedClocks foreach { clock => xdc.addGeneratedClock(clock) }
    xdc.generate(top.io)

    top.soc.initOnChipRam(elementsConfig.zephyrBinary)
    top
  }
}

object Nexys4DDRSimulate extends ElementsApp {
  val compiled = elementsConfig.genFPGASimConfig.compile {
    val board = Nexys4DDRBoard()
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
          Nexys4DDR.SystemClock.frequency,
          simDuration.toString.toInt ms
        )
      }
    case "boot" =>
      compiled.doSimUntilVoid("boot") { dut =>
        dut.simHook()
        val testCases = TestCases()
        testCases.addClockWithTimeout(dut.io.clock, Nexys4DDR.SystemClock.frequency, 10 ms)
        testCases.uartRxIdle(dut.io.uartStd.rxd)
        testCases.boot(dut.io.uartStd.txd, dut.baudPeriod)
      }
    case "mtimer" =>
      compiled.doSimUntilVoid("mtimer") { dut =>
        dut.simHook()
        val testCases = TestCases()
        testCases.addClockWithTimeout(dut.io.clock, Nexys4DDR.SystemClock.frequency, 20 ms)
        testCases.uartRxIdle(dut.io.uartStd.rxd)
        testCases.heartbeat(dut.io.gpioStatus(0))
      }
    case "reset" =>
      compiled.doSimUntilVoid("reset") { dut =>
        dut.simHook()
        val testCases = TestCases()
        testCases.addClockWithTimeout(dut.io.clock, Nexys4DDR.SystemClock.frequency, 25 ms)
        testCases.uartRxIdle(dut.io.uartStd.rxd)
        testCases.reset(dut.io.uartStd.txd, dut.baudPeriod)
      }
    case _ =>
      println(s"Unknown simulation ${simType}")
  }
}
