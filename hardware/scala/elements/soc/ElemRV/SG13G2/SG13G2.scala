package elements.soc.elemrv

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import nafarr.system.reset._
import nafarr.system.reset.ResetControllerCtrl._
import nafarr.system.clock._
import nafarr.system.clock.ClockControllerCtrl._
import nafarr.blackboxes.ihp.sg13g2._

import zibal.misc._
import zibal.platform.Hydrogen
import zibal.board.{KitParameter, BoardParameter}
import zibal.sim.hyperram.W956A8MBYA
import zibal.sim.MT25Q

import elements.sdk.ElementsApp
import elements.board.ElemRVBoard
import elements.soc.ElemRV

case class SG13G2Board() extends Component {
  val io = new Bundle {
    val clock = inout(Analog(Bool))
    val reset = inout(Analog(Bool))
    val uartStd = new Bundle {
      val txd = inout(Analog(Bool))
      val rxd = inout(Analog(Bool))
    }
    val gpioStatus = Vec(inout(Analog(Bool())), 4)
    val spiXip = new Bundle {
      val cs = inout(Analog(Bool))
      val sck = inout(Analog(Bool))
      val mosi = inout(Analog(Bool))
      val miso = inout(Analog(Bool))
    }
  }

  val top = ElemRVTop()
  val analogFalse = Analog(Bool)
  analogFalse := False
  val analogTrue = Analog(Bool)
  analogTrue := True

  top.io.clock.PAD := io.clock
  top.io.reset.PAD := io.reset

  top.io.uartStd.rxd.PAD := io.uartStd.rxd
  io.uartStd.txd := top.io.uartStd.txd.PAD
  top.io.uartStd.cts.PAD := analogTrue
  analogFalse := top.io.uartStd.rts.PAD

  top.io.jtag.tms.PAD := analogFalse
  top.io.jtag.tck.PAD := analogFalse
  top.io.jtag.tdi.PAD := analogFalse
  analogFalse := top.io.jtag.tdo.PAD
  for (index <- 0 until top.io.gpioStatus.length) {
    io.gpioStatus(index) <> top.io.gpioStatus(index).PAD
  }

  val w956a8mbya = W956A8MBYA()
  w956a8mbya.ck <> top.io.hyperbus.ck.PAD
  w956a8mbya.ckN := False
  for (index <- 0 until top.io.hyperbus.dq.length) {
    w956a8mbya.dq(index) <> top.io.hyperbus.dq(index).PAD
  }
  w956a8mbya.rwds <> top.io.hyperbus.rwds.PAD
  w956a8mbya.csN <> top.io.hyperbus.cs(0).PAD
  w956a8mbya.resetN <> top.io.hyperbus.reset.PAD

  val spiNor = MT25Q()
  spiNor.io.clock := io.clock
  spiNor.io.dataClock := io.spiXip.sck
  spiNor.io.reset := io.reset
  spiNor.io.chipSelect := io.spiXip.cs
  spiNor.io.dataIn := io.spiXip.mosi
  top.io.spiXip.dq(1).PAD := spiNor.io.dataOut

  io.spiXip.cs := top.io.spiXip.cs(0).PAD
  io.spiXip.sck := top.io.spiXip.sck.PAD
  io.spiXip.mosi := top.io.spiXip.dq(0).PAD
  top.io.spiXip.dq(1).PAD := io.spiXip.miso

  for (index <- 0 until top.io.pwr.io.length) {
    top.io.pwr.io(index).PAD := analogFalse
  }
  for (index <- 0 until top.io.gnd.io.length) {
    top.io.gnd.io(index).PAD := analogFalse
  }
  for (index <- 0 until top.io.pwr.core.length) {
    top.io.pwr.core(index).PAD := analogFalse
  }
  for (index <- 0 until top.io.gnd.core.length) {
    top.io.gnd.core(index).PAD := analogFalse
  }
}

case class ElemRVTop() extends Component {
  val resets = List[ResetParameter](ResetParameter("system", 128), ResetParameter("debug", 128))
  val clocks = List[ClockParameter](
    ClockParameter("system", 100 MHz, "system"),
    ClockParameter("debug", 10 MHz, "debug", synchronousWith = "system")
  )
  val hyperbusPartitions = List[(BigInt, Boolean)](
    (8 MB, false)
  )
  val kitParameter = KitParameter(resets, clocks)
  val boardParameter = ElemRVBoard.Parameter(kitParameter)
  val socParameter = ElemRV.Parameter(boardParameter)
  val parameter = Hydrogen.Parameter(
    socParameter,
    8 MB,
    hyperbusPartitions,
    (resetCtrl: ResetControllerCtrl, reset: Bool, _) => {
      resetCtrl.buildDummy(reset)
    },
    (clockCtrl: ClockControllerCtrl, resetCtrl: ResetControllerCtrl, clock: Bool) => {
      clockCtrl.buildDummy(clock)
    }
  )

  val io = new Bundle {
    val clock = IhpCmosIo("e", 0)
    val reset = IhpCmosIo("e", 0)
    val uartStd = new Bundle {
      val txd = IhpCmosIo("s", 0)
      val rxd = IhpCmosIo("s", 0)
      val cts = IhpCmosIo("s", 0)
      val rts = IhpCmosIo("s", 0)
    }
    val jtag = new Bundle {
      val tms = IhpCmosIo("s", 0)
      val tdi = IhpCmosIo("s", 0)
      val tdo = IhpCmosIo("s", 0)
      val tck = IhpCmosIo("s", 0)
    }
    val gpioStatus = Vec(
      IhpCmosIo("e", 0),
      IhpCmosIo("w", 0),
      IhpCmosIo("w", 0),
      IhpCmosIo("w", 0)
    )
    val hyperbus = new Bundle {
      val cs = Vec(
        IhpCmosIo("w", 0)
      )
      val ck = IhpCmosIo("e", 0)
      val reset = IhpCmosIo("e", 0)
      val dq = Vec(
        IhpCmosIo("n", 0),
        IhpCmosIo("n", 0),
        IhpCmosIo("n", 0),
        IhpCmosIo("n", 0),
        IhpCmosIo("n", 0),
        IhpCmosIo("n", 0),
        IhpCmosIo("n", 0),
        IhpCmosIo("n", 0)
      )
      val rwds = IhpCmosIo("e", 0)
    }
    val spiXip = new Bundle {
      val cs = Vec(
        IhpCmosIo("w", 0)
      )
      val dq = Vec(
        IhpCmosIo("w", 0),
        IhpCmosIo("w", 0)
      )
      val sck = IhpCmosIo("w", 0)
    }
    val pwr = new Bundle {
      val io = Vec(
        IhpCmosIo("", 0),
        IhpCmosIo("", 0),
        IhpCmosIo("", 0),
        IhpCmosIo("", 0),
        IhpCmosIo("", 0)
      )
      val core = Vec(
        IhpCmosIo("", 0),
        IhpCmosIo("", 0),
        IhpCmosIo("", 0)
      )
    }
    val gnd = new Bundle {
      val io = Vec(
        IhpCmosIo("", 0),
        IhpCmosIo("", 0),
        IhpCmosIo("", 0),
        IhpCmosIo("", 0),
        IhpCmosIo("", 0)
      )
      val core = Vec(
        IhpCmosIo("", 0),
        IhpCmosIo("", 0),
        IhpCmosIo("", 0)
      )
    }
  }

  val soc = ElemRV(parameter)

  io.clock <> IOPadIn(soc.io_plat.clock)
  io.reset <> IOPadIn(soc.io_plat.reset)

  io.jtag.tms <> IOPadIn(soc.io_plat.jtag.tms)
  io.jtag.tdi <> IOPadIn(soc.io_plat.jtag.tdi)
  io.jtag.tdo <> IOPadOut4mA(soc.io_plat.jtag.tdo)
  io.jtag.tck <> IOPadIn(soc.io_plat.jtag.tck)

  io.uartStd.txd <> IOPadOut4mA(soc.io_per.uartStd.txd)
  io.uartStd.rxd <> IOPadIn(soc.io_per.uartStd.rxd)
  io.uartStd.cts <> IOPadIn(soc.io_per.uartStd.cts)
  io.uartStd.rts <> IOPadOut4mA(soc.io_per.uartStd.rts)

  for (index <- 0 until io.gpioStatus.length) {
    io.gpioStatus(index) <> IOPadInOut4mA(soc.io_per.gpioStatus.pins(index))
  }

  for (index <- 0 until io.hyperbus.cs.length) {
    io.hyperbus.cs(index) <> IOPadOut4mA(soc.io_plat.hyperbus.cs(index))
  }
  io.hyperbus.ck <> IOPadOut30mA(soc.io_plat.hyperbus.ck)
  io.hyperbus.reset <> IOPadOut4mA(soc.io_plat.hyperbus.reset)
  for (index <- 0 until io.hyperbus.dq.length) {
    io.hyperbus.dq(index) <> IOPadInOut30mA(soc.io_plat.hyperbus.dq(index))
  }
  io.hyperbus.rwds <> IOPadInOut30mA(soc.io_plat.hyperbus.rwds)

  for (index <- 0 until io.spiXip.cs.length) {
    io.spiXip.cs(index) <> IOPadOut4mA(soc.io_plat.spiXip.cs(index))
  }
  io.spiXip.sck <> IOPadOut4mA(soc.io_plat.spiXip.sclk)
  for (index <- 0 until io.spiXip.dq.length) {
    io.spiXip.dq(index) <> IOPadInOut4mA(soc.io_plat.spiXip.dq(index))
  }

  for (index <- 0 until io.pwr.io.length) {
    io.pwr.io(index) <> IOPadIOVdd()
  }
  for (index <- 0 until io.gnd.io.length) {
    io.gnd.io(index) <> IOPadIOVss()
  }
  for (index <- 0 until io.pwr.core.length) {
    io.pwr.core(index) <> IOPadVdd()
  }
  for (index <- 0 until io.gnd.core.length) {
    io.gnd.core(index) <> IOPadVss()
  }
}

object SG13G2Generate extends ElementsApp {
  elementsConfig.genASICSpinalConfig.generateVerilog {
    val top = ElemRVTop()

    top
  }
}

object SG13G2Simulate extends ElementsApp {
  val compiled = elementsConfig.genFPGASimConfig.compile {
    val board = SG13G2Board()
    board
  }
  simType match {
    case "simulate" =>
      compiled.doSimUntilVoid("simulate") { dut =>
        val testCases = TestCases()
        testCases.addClock(
          dut.io.clock,
          ElemRVBoard.SystemClock.frequency,
          simDuration.toString.toInt ms
        )
        testCases.addReset(dut.io.reset, 100 us)
        testCases.uartRxIdle(dut.io.uartStd.rxd)
      }
  }
}
