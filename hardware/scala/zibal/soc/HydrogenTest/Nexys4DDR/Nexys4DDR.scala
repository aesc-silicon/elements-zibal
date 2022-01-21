/*
 * Copyright (c) 2021 Phytec Messtechnik GmbH
 */

package zibal.soc.hydrogentest

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.platform.Hydrogen
import zibal.soc.HydrogenTest
import zibal.misc.{ElementsConfig, BinTools, XilinxTools, SimulationHelper, TestCases}
import zibal.blackboxes.xilinx.a7._

import sys.process._


object Nexys4DDR {
  case class Io(parameter: Hydrogen.Parameter) extends Bundle {
    val clock = XilinxCmosIo("E3").clock(parameter.sysFrequency)
    val jtag = new Bundle {
      val tms = XilinxCmosIo("H2")
      val tdi = XilinxCmosIo("G4")
      val tdo = XilinxCmosIo("G2")
      val tck = XilinxCmosIo("F3").clock(parameter.dbgFrequency).
        comment("set_property CLOCK_DEDICATED_ROUTE FALSE [get_nets {iBUF_4_O}]")
    }
    val uartStd = new Bundle {
      val txd = XilinxCmosIo("D4")
      val rxd = XilinxCmosIo("C4")
      val rts = XilinxCmosIo("D3")
      val cts = XilinxCmosIo("E5")
    }
    val gpioStatus = Vec(
      XilinxCmosIo("R12"),
      XilinxCmosIo("R11"),
      XilinxCmosIo("G14"),
      XilinxCmosIo("N16")
    )
    val gpioA = Vec(
      XilinxCmosIo("J15"),
      XilinxCmosIo("L16"),
      XilinxCmosIo("M13"),
      XilinxCmosIo("R15"),
      XilinxCmosIo("R17"),
      XilinxCmosIo("T18"),
      XilinxCmosIo("U18"),
      XilinxCmosIo("R13"),
      XilinxCmosIo("T8"),
      XilinxCmosIo("U8"),
      XilinxCmosIo("R16"),
      XilinxCmosIo("T13"),
      XilinxCmosIo("H6"),
      XilinxCmosIo("U12"),
      XilinxCmosIo("U11"),
      XilinxCmosIo("V10"),
      XilinxCmosIo("H17"),
      XilinxCmosIo("K15"),
      XilinxCmosIo("J13"),
      XilinxCmosIo("N14"),
      XilinxCmosIo("R18"),
      XilinxCmosIo("V17"),
      XilinxCmosIo("U17"),
      XilinxCmosIo("U16"),
      XilinxCmosIo("V16"),
      XilinxCmosIo("T15"),
      XilinxCmosIo("U14"),
      XilinxCmosIo("T16"),
      XilinxCmosIo("V15"),
      XilinxCmosIo("V14"),
      XilinxCmosIo("V12"),
      XilinxCmosIo("V11")
    )
    val spiA = new Bundle {
      val ss = XilinxCmosIo("D14")
      val sclk = XilinxCmosIo("F16")
      val mosi = XilinxCmosIo("G16")
      val miso = XilinxCmosIo("H14")
    }
    val i2cA = new Bundle {
      val scl = XilinxCmosIo("C14")
      val sda = XilinxCmosIo("C15")
    }
    val freqCounterA = XilinxCmosIo("C17").clock(100 MHz).
        comment("set_property CLOCK_DEDICATED_ROUTE FALSE [get_nets {iBUF_10_O}]")
  }
}


object Nexys4DDRBoard {
  def apply(source: String) = Nexys4DDRBoard(source)

  case class Parameter(sysFrequency: HertzNumber, dbgFrequency: HertzNumber) {
    def convert = HydrogenTest.Parameter.default(sysFrequency, dbgFrequency)
  }
  val parameter = Parameter(100 MHz, 10 MHz)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)
    val spinalConfig = SpinalConfig(noRandBoot = false,
      targetDirectory = elementsConfig.zibalBuildPath)

    val compiled = SimConfig.withConfig(spinalConfig).withWave.workspacePath(elementsConfig.zibalBuildPath).allOptimisation.compile {
      val board = Nexys4DDRBoard(args(0))
      board
    }
    args(1) match {
      case "simulate" =>
        compiled.doSimUntilVoid("simulate") { dut =>
          val testCases = TestCases()
          testCases.addClock(dut.io.clock, dut.clockFrequency, 10 ms)
          testCases.dump(dut.io.uartStd.txd, dut.baudPeriod)
        }
      case "boot" =>
        compiled.doSimUntilVoid("boot") { dut =>
          val testCases = TestCases()
          testCases.addClockWithTimeout(dut.io.clock, dut.clockFrequency, 10 ms)
          testCases.boot(dut.io.uartStd.txd, dut.baudPeriod)
        }
      case "mtimer" =>
        compiled.doSimUntilVoid("mtimer") { dut =>
          val testCases = TestCases()
          testCases.addClockWithTimeout(dut.io.clock, dut.clockFrequency, 400 ms)
          testCases.heartbeat(dut.io.gpioStatus(0))
        }
      case "gpio" =>
        compiled.doSimUntilVoid("gpio") { dut =>
          val testCases = TestCases()
          testCases.addClockWithTimeout(dut.io.clock, dut.clockFrequency, 1 ms)
          testCases.dump(dut.io.uartStd.txd, dut.baudPeriod)
          testCases.gpioLoopback(dut.io.gpioA(0), dut.io.gpioA(2))
        }
      case "uart" =>
        compiled.doSimUntilVoid("uart") { dut =>
          val testCases = TestCases()
          testCases.addClockWithTimeout(dut.io.clock, dut.clockFrequency, 1 ms)
          testCases.dump(dut.io.uartStd.txd, dut.baudPeriod)
          testCases.uartLoopback(dut.io.uartStd.txd, dut.io.uartStd.rxd, dut.baudPeriod)
        }
        compiled.doSimUntilVoid("uart-irq") { dut =>
          val testCases = TestCases()
          testCases.addClockWithTimeout(dut.io.clock, dut.clockFrequency, 1 ms)
          testCases.dump(dut.io.uartStd.txd, dut.baudPeriod)
          testCases.uartIrq(dut.io.uartStd.txd, dut.io.uartStd.rxd, dut.baudPeriod)
        }
      case "frequency" =>
        compiled.doSimUntilVoid("100Mhz") { dut =>
          val testCases = TestCases()
          testCases.addClockWithTimeout(dut.io.clock, dut.clockFrequency, 10 ms)
          testCases.dump(dut.io.uartStd.txd, dut.baudPeriod)
          testCases.frequency(dut.io.freqCounterA, 100 MHz, dut.io.uartStd.txd, dut.baudPeriod)
        }
        compiled.doSimUntilVoid("33Mhz") { dut =>
          val testCases = TestCases()
          testCases.addClockWithTimeout(dut.io.clock, dut.clockFrequency, 10 ms)
          testCases.dump(dut.io.uartStd.txd, dut.baudPeriod)
          testCases.frequency(dut.io.freqCounterA, 33 MHz, dut.io.uartStd.txd, dut.baudPeriod)
        }
        compiled.doSimUntilVoid("20Mhz") { dut =>
          val testCases = TestCases()
          testCases.addClockWithTimeout(dut.io.clock, dut.clockFrequency, 10 ms)
          testCases.dump(dut.io.uartStd.txd, dut.baudPeriod)
          testCases.frequency(dut.io.freqCounterA, 20 MHz, dut.io.uartStd.txd, dut.baudPeriod)
        }
      case _ =>
        println(s"Unknown simulation ${args(1)}")
    }
  }

  case class Nexys4DDRBoard(source: String) extends Component {
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
      val gpioStatus = Vec(inout(Analog(Bool())), 4)
      val spiA = new Bundle {
        val ss = inout(Analog(Bool))
        val sclk = inout(Analog(Bool))
        val mosi = inout(Analog(Bool))
      }
      val gpioA = Vec(inout(Analog(Bool())), 32)
      val freqCounterA = inout(Analog(Bool))
    }
    val peripherals = parameter.convert.peripherals.asInstanceOf[HydrogenTest.Peripherals]
    val baudPeriod = peripherals.uartStd.init.getBaudPeriod()
    val clockFrequency = parameter.convert.sysFrequency

    val top = Nexys4DDRTop(source)
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

    io.spiA.ss := top.io.spiA.ss.PAD
    io.spiA.sclk := top.io.spiA.sclk.PAD
    io.spiA.mosi := top.io.spiA.mosi.PAD
    top.io.spiA.miso.PAD := analogFalse
    top.io.i2cA.scl.PAD := analogFalse
    top.io.i2cA.sda.PAD := analogFalse
    top.io.freqCounterA.PAD := io.freqCounterA

    for (index <- 0 until 4) {
      io.gpioStatus(index) <> top.io.gpioStatus(index).PAD
    }

    for (index <- 0 until 32) {
      io.gpioA(index) <> top.io.gpioA(index).PAD
    }
    top.io.gpioA(1).PAD := analogTrue
  }

  case class Nexys4DDRTop(source: String) extends BlackBox {
    val io = Nexys4DDR.Io(parameter.convert)

    val elementsConfig = ElementsConfig(this)
    SimulationHelper.Xilinx.addRtl(this, elementsConfig, source)
    SimulationHelper.Xilinx.addBinary(this, elementsConfig)
  }
}


object Nexys4DDRTop {
  def apply() = Nexys4DDRTop(Nexys4DDRBoard.parameter.convert)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)
    val spinalConfig = SpinalConfig(noRandBoot = false,
      targetDirectory = elementsConfig.zibalBuildPath)

    spinalConfig.generateVerilog({
      val parameter = Nexys4DDRBoard.parameter.convert
      args(0) match {
        case "prepare" =>
          val soc = HydrogenTest(parameter)
          HydrogenTest.prepare(soc, elementsConfig)
          soc
        case _ =>
          val top = Nexys4DDRTop(parameter)
          val system = top.soc.system
          BinTools.initRam(system.onChipRam.ram, elementsConfig.zephyrBuildPath + "/zephyr.bin")
          XilinxTools.Xdc(elementsConfig).generate(top.io)
          top
      }
    })
  }

  case class Nexys4DDRTop(parameter: Hydrogen.Parameter) extends Component {
    val io = Nexys4DDR.Io(parameter)

    val soc = HydrogenTest(parameter)

    io.clock <> IBUF(soc.io_sys.clock)
    soc.io_sys.reset := False

    io.jtag.tms <> IBUF(soc.io_sys.jtag.tms)
    io.jtag.tdi <> IBUF(soc.io_sys.jtag.tdi)
    io.jtag.tdo <> OBUF(soc.io_sys.jtag.tdo)
    io.jtag.tck <> IBUF(soc.io_sys.jtag.tck)

    io.uartStd.txd <> OBUF(soc.io_per.uartStd.txd)
    io.uartStd.rxd <> IBUF(soc.io_per.uartStd.rxd)
    io.uartStd.rts <> OBUF(soc.io_per.uartStd.rts)
    io.uartStd.cts <> IBUF(soc.io_per.uartStd.cts)

    io.spiA.ss <> OBUF(soc.io_per.spiA.ss(0))
    io.spiA.sclk <> OBUF(soc.io_per.spiA.sclk)
    io.spiA.mosi <> OBUF(soc.io_per.spiA.mosi)
    io.spiA.miso <> IBUF(soc.io_per.spiA.miso)

    io.i2cA.scl <> IOBUF(soc.io_per.i2cA.scl.read, False, soc.io_per.i2cA.scl.write)
    io.i2cA.sda <> IOBUF(soc.io_per.i2cA.sda.read, False, soc.io_per.i2cA.sda.write)

    io.freqCounterA <> IBUF(soc.io_per.freqCounterA.clock)

    for (index <- 0 until 4) {
      io.gpioStatus(index) <> IOBUF(soc.io_per.gpioStatus.pins(index))
    }

    for (index <- 0 until 32) {
      io.gpioA(index) <> IOBUF(soc.io_per.gpioA.pins(index))
    }
  }
}
