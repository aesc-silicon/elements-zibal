package elements.soc.argon1

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import nafarr.system.reset._
import nafarr.system.reset.ResetControllerCtrl._
import nafarr.system.clock._
import nafarr.system.clock.ClockControllerCtrl._
import nafarr.blackboxes.lattice.ecp5._

import zibal.misc._
import zibal.platform.Argon
import zibal.board.{KitParameter, BoardParameter}
import zibal.sim.hyperram.W956A8MBYA

import elements.sdk.ElementsApp
import elements.board.ECPIX5
import elements.soc.Argon1

case class ECPIX5Board() extends Component {
  val io = new Bundle {
    val clock = inout(Analog(Bool))
    val uartStd = new Bundle {
      val txd = inout(Analog(Bool))
      val rxd = inout(Analog(Bool))
    }
    val gpioStatus = Vec(inout(Analog(Bool())), 4)
    val hyperbus = new Bundle {
      val cs = inout(Analog(Bool))
      val ck = inout(Analog(Bool))
      val reset = inout(Analog(Bool))
      val rwds = inout(Analog(Bool))
      val dq = Vec(inout(Analog(Bool())), 8)
    }
  }

  val top = ECPIX5Top()
  val analogFalse = Analog(Bool)
  analogFalse := False
  val analogTrue = Analog(Bool)
  analogTrue := True

  top.io.clock.PAD := io.clock

  top.io.uartStd.rxd.PAD := io.uartStd.rxd
  io.uartStd.txd := top.io.uartStd.txd.PAD

  for (index <- 0 until top.io.gpioStatus.length) {
    io.gpioStatus(index) <> top.io.gpioStatus(index).PAD
  }

  val w956a8mbya = W956A8MBYA()
  w956a8mbya.io.ck := io.hyperbus.ck
  w956a8mbya.io.ckN := analogFalse
  for (index <- 0 until top.io.hyperbus.dq.length) {
    w956a8mbya.io.dqIn(index) := io.hyperbus.dq(index)
    io.hyperbus.dq(index) := w956a8mbya.io.dqOut(index)
  }
  w956a8mbya.io.rwdsIn := io.hyperbus.rwds
  io.hyperbus.rwds := w956a8mbya.io.rwdsOut
  w956a8mbya.io.csN := io.hyperbus.cs
  w956a8mbya.io.resetN := io.hyperbus.reset

  io.hyperbus.cs := top.io.hyperbus.cs(0).PAD
  io.hyperbus.ck := top.io.hyperbus.ck.PAD
  io.hyperbus.reset := top.io.hyperbus.reset.PAD
  io.hyperbus.rwds <> top.io.hyperbus.rwds.PAD
  for (index <- 0 until top.io.hyperbus.dq.length) {
    io.hyperbus.dq(index) <> top.io.hyperbus.dq(index).PAD
  }

  for (index <- 0 until top.io.ledPullDown.length) {
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
    ClockParameter("debug", 10 MHz, "debug", synchronousWith = "system")
  )
  val hyperbusPartitions = List[(BigInt, Boolean)](
    (8 MB, true),
    (8 MB, false),
    (8 MB, false),
    (8 MB, false)
  )
  val kitParameter = KitParameter(resets, clocks)
  val boardParameter = ECPIX5.Parameter(kitParameter, ECPIX5.SystemClock.frequency)
  val socParameter = Argon1.Parameter(boardParameter)
  val parameter = Argon.Parameter(
    socParameter,
    192 kB,
    hyperbusPartitions,
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
        10
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
      LatticeCmosIo(ECPIX5.LEDs.LD6.red),
      LatticeCmosIo(ECPIX5.LEDs.LD7.green),
      LatticeCmosIo(ECPIX5.Buttons.sw0)
    )
    val hyperbus = new Bundle {
      val cs = Vec(
        LatticeCmosIo(ECPIX5.Pmods.Pmod5.pin1),
        LatticeCmosIo(ECPIX5.Pmods.Pmod5.pin5),
        LatticeCmosIo(ECPIX5.Pmods.Pmod5.pin0),
        LatticeCmosIo(ECPIX5.Pmods.Pmod5.pin4)
      )
      val ck = LatticeCmosIo(ECPIX5.Pmods.Pmod5.pin2).slewRateFast
      val ckN = LatticeCmosIo(ECPIX5.Pmods.Pmod5.pin3).slewRateFast
      val reset = LatticeCmosIo(ECPIX5.Pmods.Pmod5.pin6)
      val dq = Vec(
        LatticeCmosIo(ECPIX5.Pmods.Pmod4.pin0).slewRateFast,
        LatticeCmosIo(ECPIX5.Pmods.Pmod4.pin1).slewRateFast,
        LatticeCmosIo(ECPIX5.Pmods.Pmod4.pin2).slewRateFast,
        LatticeCmosIo(ECPIX5.Pmods.Pmod4.pin3).slewRateFast,
        LatticeCmosIo(ECPIX5.Pmods.Pmod4.pin7).slewRateFast,
        LatticeCmosIo(ECPIX5.Pmods.Pmod4.pin6).slewRateFast,
        LatticeCmosIo(ECPIX5.Pmods.Pmod4.pin5).slewRateFast,
        LatticeCmosIo(ECPIX5.Pmods.Pmod4.pin4).slewRateFast
      )
      val rwds = LatticeCmosIo(ECPIX5.Pmods.Pmod5.pin7).slewRateFast
    }
    val ledPullDown = Vec(
      LatticeCmosIo(ECPIX5.LEDs.LD5.red),
      LatticeCmosIo(ECPIX5.LEDs.LD5.green),
      LatticeCmosIo(ECPIX5.LEDs.LD6.green),
      LatticeCmosIo(ECPIX5.LEDs.LD6.blue),
      LatticeCmosIo(ECPIX5.LEDs.LD7.red),
      LatticeCmosIo(ECPIX5.LEDs.LD7.blue),
      LatticeCmosIo(ECPIX5.LEDs.LD8.red),
      LatticeCmosIo(ECPIX5.LEDs.LD8.green),
      LatticeCmosIo(ECPIX5.LEDs.LD8.blue)
    )
  }

  val soc = Argon1(parameter)

  io.clock <> FakeI(soc.io_plat.clock)

  soc.io_plat.jtag.tms := False
  soc.io_plat.jtag.tdi := False
  soc.io_plat.jtag.tck := False

  io.uartStd.txd <> FakeO(soc.io_per.uartStd.txd)
  io.uartStd.rxd <> FakeI(soc.io_per.uartStd.rxd)
  soc.io_per.uartStd.cts := False

  for (index <- 0 until io.gpioStatus.length - 1) {
    io.gpioStatus(index) <> FakeIo(soc.io_per.gpioStatus.pins(index), true)
  }
  for (index <- io.gpioStatus.length - 1 until io.gpioStatus.length) {
    io.gpioStatus(index) <> FakeIo(soc.io_per.gpioStatus.pins(index))
  }

  for (index <- 0 until io.hyperbus.cs.length) {
    io.hyperbus.cs(index) <> FakeO(soc.io_plat.hyperbus.cs(index))
  }
  io.hyperbus.ck <> FakeO(soc.io_plat.hyperbus.ck)
  io.hyperbus.ckN <> FakeO(False)
  io.hyperbus.reset <> FakeO(soc.io_plat.hyperbus.reset)
  for (index <- 0 until io.hyperbus.dq.length) {
    io.hyperbus.dq(index) <> FakeIo(soc.io_plat.hyperbus.dq(index))
  }
  io.hyperbus.rwds <> FakeIo(soc.io_plat.hyperbus.rwds)

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
        testCases.addClockWithTimeout(dut.io.clock, ECPIX5.SystemClock.frequency, 20 ms)
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
