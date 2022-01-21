/*
 * Copyright (c) 2021 Phytec Messtechnik GmbH
 */

package zibal.soc.carbon1

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.platform.Carbon
import zibal.soc.Carbon1
import zibal.misc.{BinTools, CadenceTools, SimulationHelper, ElementsConfig, TestCases}
import zibal.blackboxes.ihp.sg13s._

import zibal.sim.MT25Q


object DH012 {
  case class Io() extends Bundle {
    val clock = IhpCmosIo("top", 0)
    val reset = IhpCmosIo("top", 1)
    val sysReset_out = IhpCmosIo("top", 2)
    val jtag = new Bundle {
      val tms = IhpCmosIo("right", 0)
      val tdi = IhpCmosIo("right", 1)
      val tdo = IhpCmosIo("right", 2)
      val tck = IhpCmosIo("right", 3)
    }
    val uartStd = new Bundle {
      val txd = IhpCmosIo("bottom", 7)
      val rxd = IhpCmosIo("bottom", 8)
      val rts = IhpCmosIo("bottom", 9)
      val cts = IhpCmosIo("bottom", 10)
    }
    val spiXip = new Bundle {
      val ss = IhpCmosIo("left", 0)
      val sclk = IhpCmosIo("left", 1)
      val mosi = IhpCmosIo("left", 2)
      val miso = IhpCmosIo("left", 3)
    }
    val i2cA = new Bundle {
      val scl = IhpCmosIo("left", 8)
      val sda = IhpCmosIo("left", 9)
    }
    val gpioStatus = Vec(IhpCmosIo("top", 7), IhpCmosIo("top", 8), IhpCmosIo("top", 9),
                         IhpCmosIo("top", 10))
    val gpioA = Vec(IhpCmosIo("right", 8), IhpCmosIo("right", 9), IhpCmosIo("right", 10),
                    IhpCmosIo("bottom", 0), IhpCmosIo("bottom", 1), IhpCmosIo("bottom", 2),
                    IhpCmosIo("left", 10))
  }
}


object DH012Board {
  def apply(source: String) = DH012Board(source)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)
    val spinalConfig = SpinalConfig(noRandBoot = false,
      targetDirectory = elementsConfig.zibalBuildPath)

    val compiled = SimConfig.withConfig(spinalConfig).withWave.workspacePath(elementsConfig.zibalBuildPath).allOptimisation.compile {
      val board = DH012Board(args(0))
      BinTools.initRam(board.spiNor.deviceOut.data, elementsConfig.fplBuildPath + "/kernel.img")
      board
    }
    args(1) match {
      case "simulate" =>
        compiled.doSimUntilVoid("simulate") { dut =>
          val testCases = TestCases()
          testCases.addClock(dut.io.clock, dut.clockFrequency, 1 ms)
          testCases.addReset(dut.io.reset, 1 us)
          testCases.dump(dut.io.uartStd.txd, dut.baudPeriod)
        }
      case _ =>
        println(s"Unknown simulation ${args(1)}")
    }
  }


  case class DH012Board(source: String) extends Component {
    val io = new Bundle {
      val clock = inout(Analog(Bool))
      val reset = inout(Analog(Bool))
      val sysReset_out = inout(Analog(Bool))
      val jtag = new Bundle {
        val tdo = inout(Analog(Bool))
      }
      val uartStd = new Bundle {
        val txd = inout(Analog(Bool))
        val rts = inout(Analog(Bool))
      }
      val spiXip = new Bundle {
        val ss = inout(Analog(Bool))
        val sclk = inout(Analog(Bool))
        val mosi = inout(Analog(Bool))
        val miso = inout(Analog(Bool))
      }
      val gpioStatus = Vec(inout(Analog(Bool())), 4)
      val gpioA = Vec(inout(Analog(Bool())), 7)
    }
    val peripherals = Carbon1.Parameter.default.peripherals.asInstanceOf[Carbon1.Peripherals]
    val baudPeriod = peripherals.uartStd.init.getBaudPeriod()
    val clockFrequency = Carbon1.Parameter.default.sysFrequency

    val top = DH012Top(source)
    val analogFalse = Analog(Bool)
    analogFalse := False
    val analogTrue = Analog(Bool)
    analogTrue := True

    val spiNor = MT25Q()
    spiNor.io.clock := io.clock
    spiNor.io.dataClock := top.io.spiXip.sclk.PAD
    spiNor.io.reset := io.reset
    spiNor.io.chipSelect := top.io.spiXip.ss.PAD
    spiNor.io.dataIn := top.io.spiXip.mosi.PAD
    val analogDataOut = Analog(Bool)
    analogDataOut := spiNor.io.dataOut
    top.io.spiXip.miso.PAD := analogDataOut

    top.io.clock.PAD := io.clock
    top.io.reset.PAD := io.reset
    io.sysReset_out := top.io.sysReset_out.PAD

    top.io.jtag.tms.PAD := analogFalse
    top.io.jtag.tdi.PAD := analogFalse
    io.jtag.tdo := top.io.jtag.tdo.PAD
    top.io.jtag.tck.PAD := analogFalse
    io.spiXip.ss := top.io.spiXip.ss.PAD
    io.spiXip.sclk := top.io.spiXip.sclk.PAD
    io.spiXip.mosi := top.io.spiXip.mosi.PAD
    top.io.spiXip.miso.PAD := io.spiXip.miso
    top.io.uartStd.rxd.PAD := analogFalse
    io.uartStd.txd := top.io.uartStd.txd.PAD
    top.io.uartStd.cts.PAD := analogFalse
    io.uartStd.rts := top.io.uartStd.rts.PAD

    top.io.i2cA.scl.PAD := analogFalse
    top.io.i2cA.sda.PAD := analogFalse

    for (index <- 0 until 4) {
      io.gpioStatus(index) <> top.io.gpioStatus(index).PAD
    }

    for (index <- 0 until 7) {
      io.gpioA(index) <> top.io.gpioA(index).PAD
    }
  }

  case class DH012Top(source: String) extends BlackBox {
    val io = DH012.Io()

    val elementsConfig = ElementsConfig(this)
    SimulationHelper.IHP.addRtl(this, elementsConfig, source)
  }
}


object DH012Top {
  def apply() = DH012Top()

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)
    val spinalConfig = SpinalConfig(noRandBoot = false,
      targetDirectory = elementsConfig.zibalBuildPath)

    args(0) match {
      case "prepare" =>
        println("Nothing to do here!")
      case _ =>
        spinalConfig.generateVerilog({
          val top = DH012Top()
          val io = CadenceTools.Io(elementsConfig)
          io.addPad("top", 3, "gndpad")
          io.addPad("top", 4, "gndcore")
          io.addPad("top", 5, "vddcore")
          io.addPad("top", 6, "vddpad")
          io.addPad("right", 4, "gndpad")
          io.addPad("right", 5, "gndcore")
          io.addPad("right", 6, "vddcore")
          io.addPad("right", 7, "vddpad")
          io.addPad("bottom", 3, "gndpad")
          io.addPad("bottom", 4, "gndcore")
          io.addPad("bottom", 5, "vddcore")
          io.addPad("bottom", 6, "vddpad")
          io.addPad("left", 4, "gndpad")
          io.addPad("left", 5, "gndcore")
          io.addPad("left", 6, "vddcore")
          io.addPad("left", 7, "vddpad")
          io.addCorner("topright", 90, "corner")
          io.addCorner("bottomright", 0, "corner")
          io.addCorner("bottomleft", 270, "corner")
          io.addCorner("topleft", 180, "corner")
          io.generate(top.io, elementsConfig.zibalBuildPath)
          val sdc = CadenceTools.Sdc(elementsConfig)
          sdc.addClock(top.io.clock.PAD, top.soc.p.sysFrequency)
          sdc.addClock(top.io.jtag.tck.PAD, top.soc.p.dbgFrequency)
          sdc.generate(elementsConfig.zibalBuildPath)
          top
        })
    }
  }

  case class DH012Top() extends Component {
    val io = DH012.Io()

    val soc = Carbon1()

    io.clock <> ixc013_i16x(soc.io_sys.clock)
    io.reset <> ixc013_i16x(soc.io_sys.reset)
    io.sysReset_out <> ixc013_b16m().asOutput(soc.io_sys.sysReset_out)

    io.jtag.tms <> ixc013_b16m().asInput(soc.io_sys.jtag.tms)
    io.jtag.tdi <> ixc013_b16m().asInput(soc.io_sys.jtag.tdi)
    io.jtag.tdo <> ixc013_b16m().asOutput(soc.io_sys.jtag.tdo)
    io.jtag.tck <> ixc013_b16m().asInput(soc.io_sys.jtag.tck)

    io.spiXip.ss <> ixc013_b16m().asOutput(soc.io_sys.spiXip.ss(0))
    io.spiXip.sclk <> ixc013_b16m().asOutput(soc.io_sys.spiXip.sclk)
    io.spiXip.mosi <> ixc013_b16m().asOutput(soc.io_sys.spiXip.mosi)
    io.spiXip.miso <> ixc013_b16m().asInput(soc.io_sys.spiXip.miso)

    io.uartStd.txd <> ixc013_b16m().asOutput(soc.io_per.uartStd.txd)
    io.uartStd.rxd <> ixc013_b16m().asInput(soc.io_per.uartStd.rxd)
    io.uartStd.rts <> ixc013_b16m().asOutput(soc.io_per.uartStd.rts)
    io.uartStd.cts <> ixc013_b16m().asInput(soc.io_per.uartStd.cts)

    io.i2cA.scl <> ixc013_b16mpup().withOpenDrain(soc.io_per.i2cA.scl)
    io.i2cA.sda <> ixc013_b16mpup().withOpenDrain(soc.io_per.i2cA.sda)

    for (index <- 0 until 4) {
      io.gpioStatus(index) <> ixc013_b16m(soc.io_per.gpioStatus.pins(index))
    }

    for (index <- 0 until 7) {
      io.gpioA(index) <> ixc013_b16m(soc.io_per.gpioA.pins(index))
    }
  }
}
