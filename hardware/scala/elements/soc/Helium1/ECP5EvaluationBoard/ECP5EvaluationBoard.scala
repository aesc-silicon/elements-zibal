package elements.soc.helium1

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import nafarr.system.reset._
import nafarr.system.reset.ResetControllerCtrl._
import nafarr.system.clock._
import nafarr.system.clock.ClockControllerCtrl._
import nafarr.blackboxes.lattice.ecp5._

import zibal.misc._
import zibal.platform.Helium
import zibal.board.{KitParameter, BoardParameter}

import elements.sdk.ElementsApp
import elements.board.ECP5EvaluationBoard
import elements.soc.Helium1

case class ECP5EvaluationBoardBoard() extends Component {
  val io = new Bundle {
    val clock = inout(Analog(Bool))
    val uartStd = new Bundle {
      val txd = inout(Analog(Bool))
      val rxd = inout(Analog(Bool))
    }
    val gpioStatus = Vec(inout(Analog(Bool())), 2)
  }

  val top = ECP5EvaluationBoardTop()
  val analogFalse = Analog(Bool)
  analogFalse := False
  val analogTrue = Analog(Bool)
  analogTrue := True

  top.io.clock.PAD := io.clock

  top.io.uartStd.rxd.PAD := io.uartStd.rxd
  io.uartStd.txd := top.io.uartStd.txd.PAD

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

case class ECP5EvaluationBoardTop() extends Component {
  val resets = List[ResetParameter](ResetParameter("system", 128), ResetParameter("debug", 128))
  val clocks = List[ClockParameter](
    ClockParameter("system", 100 MHz, "system"),
    ClockParameter("debug", 100 MHz, "debug", synchronousWith = "system")
  )

  val kitParameter = KitParameter(resets, clocks)
  val boardParameter = ECP5EvaluationBoard.Parameter(kitParameter)
  val socParameter = Helium1.Parameter(boardParameter)
  val parameter = Helium.Parameter(
    socParameter,
    128 kB,
    (resetCtrl: ResetControllerCtrl, _, clock: Bool) => { resetCtrl.buildXilinx(clock) },
    (clockCtrl: ClockControllerCtrl, resetCtrl: ResetControllerCtrl, clock: Bool) => {
      clockCtrl.buildLatticeECP5Pll(
        clock,
        boardParameter.getOscillatorFrequency,
        List("system", "debug"),
        1,
        5,
        10
      )
    }
  )

  val io = new Bundle {
    val clock = LatticeCmosIo("A10").clock(boardParameter.getOscillatorFrequency)
    val uartStd = new Bundle {
      val txd = LatticeCmosIo("P3")
      val rxd = LatticeCmosIo("P2")
    }
    val gpioStatus = Vec(
      LatticeCmosIo("A13").ioStandard("LVCMOS25"),
      LatticeCmosIo("P4")
    )
  }

  val soc = Helium1(parameter)

  io.clock <> FakeI(soc.io_plat.clock)

  soc.io_plat.jtag.tms := False
  soc.io_plat.jtag.tdi := False
  soc.io_plat.jtag.tck := False

  io.uartStd.txd <> FakeO(soc.io_per.uartStd.txd)
  io.uartStd.rxd <> FakeI(soc.io_per.uartStd.rxd)
  soc.io_per.uartStd.cts := False

  for (index <- 0 until 2) {
    io.gpioStatus(index) <> FakeIo(soc.io_per.gpioStatus.pins(index))
  }
}

object ECP5EvaluationBoardGenerate extends ElementsApp {
  elementsConfig.genFPGASpinalConfig.generateVerilog {
    val top = ECP5EvaluationBoardTop()

    val lpf = LatticeTools.Lpf(elementsConfig)
    lpf.generate(top.io)

    top.soc.initOnChipRam(elementsConfig.zephyrBinary)
    top
  }
}

object ECP5EvaluationBoardSimulate extends ElementsApp {
  val compiled = elementsConfig.genFPGASimConfig.compile {
    val board = ECP5EvaluationBoardBoard()
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
        testCases.addClock(dut.io.clock, ECP5EvaluationBoard.oscillatorFrequency, 10 ms)
        fork {
          dut.io.uartStd.rxd #= true
        }
      }
    case "boot" =>
      compiled.doSimUntilVoid("boot") { dut =>
        dut.simHook()
        val testCases = TestCases()
        testCases.addClockWithTimeout(dut.io.clock, ECP5EvaluationBoard.oscillatorFrequency, 10 ms)
        testCases.boot(dut.io.uartStd.txd, dut.baudPeriod)
      }
    case "mtimer" =>
      compiled.doSimUntilVoid("mtimer") { dut =>
        dut.simHook()
        val testCases = TestCases()
        testCases.addClockWithTimeout(dut.io.clock, ECP5EvaluationBoard.oscillatorFrequency, 400 ms)
        testCases.heartbeat(dut.io.gpioStatus(0))
      }
    case _ =>
      println(s"Unknown simulation ${simType}")
  }
}
