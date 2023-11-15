package elements.soc.hydrogen1

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import nafarr.system.reset._
import nafarr.system.reset.ResetControllerCtrl._
import nafarr.system.clock._
import nafarr.system.clock.ClockControllerCtrl._
import nafarr.blackboxes.lattice.ecp5._

import zibal.misc._
import zibal.platform.Hydrogen
import zibal.board.{KitParameter, BoardParameter}

import elements.sdk.ElementsApp
import elements.board.ECPIX5
import elements.soc.Hydrogen1

case class ECPIX5Board() extends Component {
  val io = new Bundle {
    val clock = inout(Analog(Bool))
    val uartStd = new Bundle {
      val txd = inout(Analog(Bool))
      val rxd = inout(Analog(Bool))
    }
    val gpioStatus = Vec(inout(Analog(Bool())), 2)
  }

  val top = ECPIX5Top()
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

  for (index <- 0 until 11) {
    top.io.ledPullDown(index).PAD := analogFalse
  }

  val baudPeriod = top.soc.socParameter.uartStd.init.getBaudPeriod()

  def simHook() {
    for ((domain, index) <- top.soc.parameter.getKitParameter.clocks.zipWithIndex) {
      val clockDomain = top.soc.clockCtrl.getClockDomainByName(domain.name)
      SimulationHelper.generateEndlessClock(clockDomain.clock, domain.frequency)
    }
  }
}

case class ECPIX5Top() extends Component {
  val resets = List[ResetParameter](ResetParameter("system", 128), ResetParameter("debug", 128))
  val clocks = List[ClockParameter](
    ClockParameter("system", 100 MHz, "system"),
    ClockParameter("debug", 100 MHz, "debug", synchronousWith = "system")
  )

  val kitParameter = KitParameter(resets, clocks)
  val boardParameter = ECPIX5.Parameter(kitParameter, ECPIX5.SystemClock.frequency)
  val socParameter = Hydrogen1.Parameter(boardParameter)
  val parameter = Hydrogen.Parameter(
    socParameter,
    128 kB,
    (resetCtrl: ResetControllerCtrl, _, clock: Bool) => { resetCtrl.buildXilinx(clock) },
    (clockCtrl: ClockControllerCtrl, resetCtrl: ResetControllerCtrl, clock: Bool) => {
      clockCtrl.buildDummy(clock)
      /* TODO PLLs don't work when booting from flash
      clockCtrl.buildLatticeECP5Pll(
        clock,
        boardParameter.getOscillatorFrequency,
        List("system", "debug"),
        2,
        1,
        9
      )
       */
    }
  )

  val io = new Bundle {
    val clock = LatticeCmosIo(ECPIX5.SystemClock.clock).clock(ECPIX5.SystemClock.frequency)
    val uartStd = new Bundle {
      val txd = LatticeCmosIo(ECPIX5.UartStd.txd)
      val rxd = LatticeCmosIo(ECPIX5.UartStd.rxd)
    }
    val gpioStatus = Vec(
      LatticeCmosIo(ECPIX5.LEDs.LD5.blue),
      LatticeCmosIo(ECPIX5.Buttons.sw0)
    )
    val ledPullDown = Vec(
      LatticeCmosIo(ECPIX5.LEDs.LD5.red),
      LatticeCmosIo(ECPIX5.LEDs.LD5.green),
      LatticeCmosIo(ECPIX5.LEDs.LD6.red),
      LatticeCmosIo(ECPIX5.LEDs.LD6.green),
      LatticeCmosIo(ECPIX5.LEDs.LD6.blue),
      LatticeCmosIo(ECPIX5.LEDs.LD7.red),
      LatticeCmosIo(ECPIX5.LEDs.LD7.green),
      LatticeCmosIo(ECPIX5.LEDs.LD7.blue),
      LatticeCmosIo(ECPIX5.LEDs.LD8.red),
      LatticeCmosIo(ECPIX5.LEDs.LD8.green),
      LatticeCmosIo(ECPIX5.LEDs.LD8.blue)
    )
  }

  val soc = Hydrogen1(parameter)

  io.clock <> FakeI(soc.io_plat.clock)

  soc.io_plat.jtag.tms := False
  soc.io_plat.jtag.tdi := False
  soc.io_plat.jtag.tck := False

  io.uartStd.txd <> FakeO(soc.io_per.uartStd.txd)
  io.uartStd.rxd <> FakeI(soc.io_per.uartStd.rxd)
  soc.io_per.uartStd.cts := False

  io.gpioStatus(0) <> FakeIo(soc.io_per.gpioStatus.pins(0), true)
  for (index <- 1 until io.gpioStatus.length) {
    io.gpioStatus(index) <> FakeIo(soc.io_per.gpioStatus.pins(index))
  }
  for (index <- 0 until io.ledPullDown.length) {
    io.ledPullDown(index) <> FakeO(True)
  }
}

object ECPIX5Generate extends ElementsApp {
  elementsConfig.genFPGASpinalConfig.generateVerilog {
    val top = ECPIX5Top()

    val lpf = LatticeTools.Lpf(elementsConfig)
    lpf.generate(top.io)

    top.soc.initOnChipRam(elementsConfig.zephyrBinary)
    top
  }
}

object ECPIX5Simulate extends ElementsApp {
  val compiled = elementsConfig.genFPGASimConfig.compile {
    val board = ECPIX5Board()
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
          ECPIX5.SystemClock.frequency,
          simDuration.toString.toInt ms
        )
        testCases.uartRxIdle(dut.io.uartStd.rxd)
      }
    case "boot" =>
      compiled.doSimUntilVoid("boot") { dut =>
        dut.simHook()
        val testCases = TestCases()
        testCases.addClockWithTimeout(dut.io.clock, ECPIX5.SystemClock.frequency, 10 ms)
        testCases.uartRxIdle(dut.io.uartStd.rxd)
        testCases.boot(dut.io.uartStd.txd, dut.baudPeriod)
      }
    case "mtimer" =>
      compiled.doSimUntilVoid("mtimer") { dut =>
        dut.simHook()
        val testCases = TestCases()
        testCases.addClockWithTimeout(dut.io.clock, ECPIX5.SystemClock.frequency, 20 ms)
        testCases.uartRxIdle(dut.io.uartStd.rxd)
        testCases.heartbeat(dut.io.gpioStatus(0), true)
      }
    case "reset" =>
      compiled.doSimUntilVoid("reset") { dut =>
        dut.simHook()
        val testCases = TestCases()
        testCases.addClockWithTimeout(dut.io.clock, ECPIX5.SystemClock.frequency, 25 ms)
        testCases.uartRxIdle(dut.io.uartStd.rxd)
        testCases.reset(dut.io.uartStd.txd, dut.baudPeriod)
      }
    case _ =>
      println(s"Unknown simulation ${simType}")
  }
}
