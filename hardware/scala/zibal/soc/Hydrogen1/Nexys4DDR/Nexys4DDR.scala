/*
 * Copyright (c) 2021 Phytec Messtechnik GmbH
 */

package zibal.soc.hydrogen1

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.soc.Hydrogen1
import zibal.misc.{ElementsConfig, BinTools, XilinxTools, SimulationHelper}
import zibal.blackboxes.xilinx.a7._

import sys.process._


object Nexys4DDR {
  case class Io() extends Bundle {
    val clock = XilinxCmosIo("E3").clock(100 MHz)
    val jtag = new Bundle {
      val tms = XilinxCmosIo("H2")
      val tdi = XilinxCmosIo("G4")
      val tdo = XilinxCmosIo("G2")
      val tck = XilinxCmosIo("F3").clock(10 MHz).
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
  }
}


object Nexys4DDRBoard {
  def apply(source: String) = Nexys4DDRBoard(source)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig()

    var baudPeriod: Int = 0
    var clockPeriod: Int = 0

    val config = SpinalConfig(noRandBoot = false, targetDirectory = elementsConfig.zibalBuildPath)
    val compiled = SimConfig.withConfig(config).withWave.workspacePath(elementsConfig.zibalBuildPath).allOptimisation.compile {
      val parameter = Hydrogen1.Peripherals.default
      val peripherals = parameter.peripherals.asInstanceOf[Hydrogen1.Peripherals]
      baudPeriod = peripherals.uartStd.init.getBaudPeriod()
      clockPeriod = 1000000000 / parameter.sysFrequency.toInt

      val board = Nexys4DDRBoard(args(0))
      board
    }
    args(1) match {
      case "boot" =>
        compiled.doSimUntilVoid("boot") { dut =>
          SimulationHelper.generateClock(dut.io.clock, clockPeriod, 10000000)
          SimulationHelper.dumpStdout(dut.io.uartStd.txd, baudPeriod)
          SimulationHelper.expectZephyrPrompt(dut.io.uartStd.txd, baudPeriod)
        }
      case "mtimer" =>
        compiled.doSimUntilVoid("mtimer") { dut =>
          SimulationHelper.generateClock(dut.io.clock, clockPeriod, 400000000)
          fork {
            sleep(100000)
            assert(dut.io.gpioStatus(0).toBoolean == false)
            println("Heartbeat LED: OFF")
            sleep(400000)
            assert(dut.io.gpioStatus(0).toBoolean == true)
            println("Heartbeat LED: ON")
            sleep(150500000)
            assert(dut.io.gpioStatus(0).toBoolean == false)
            println("Heartbeat LED: OFF")
            sleep(50000000)
            assert(dut.io.gpioStatus(0).toBoolean == true)
            println("Heartbeat LED: ON")
            sleep(150000000)
            assert(dut.io.gpioStatus(0).toBoolean == false)
            println("Heartbeat LED: OFF")
            simSuccess
          }
        }
      case _ =>
        println(s"Unknown simulation ${args(1)}")
    }
  }

  case class Nexys4DDRTop(source: String) extends BlackBox {
    val io = Nexys4DDR.Io()

    val elementsConfig = ElementsConfig()
    val className = this.getClass().getName().split("\\.").last.split("\\$").last

    addRTLPath("hardware/scala/zibal/blackboxes/xilinx/a7/IO.v")
    if (source.equals("generated")) {
      addRTLPath(elementsConfig.zibalBuildPath + s"${className}.v")
    } else {
      println(s"Unsupported source $source for $className")
    }

    val result = Process(s"ls ${elementsConfig.zibalBuildPath}").!!
    for (line <- result.lines().toArray()) {
      if (line.asInstanceOf[String].endsWith(".bin")) {
        addRTLPath(elementsConfig.zibalBuildPath + line)
      }
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
    }
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

    for (index <- 0 until 4) {
      io.gpioStatus(index) <> top.io.gpioStatus(index).PAD
    }
  }
}


object Nexys4DDRTop {
  def apply() = Nexys4DDRTop()

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig()
    val className = this.getClass().getName().stripSuffix("$").split("\\.").last

    val config = SpinalConfig(noRandBoot = false, targetDirectory = elementsConfig.zibalBuildPath)

    config.generateVerilog({
      val top = Nexys4DDRTop()
      val system = top.soc.system
      BinTools.initRam(system.onChipRam.ram, elementsConfig.zephyrBuildPath + "/zephyr.bin")
      XilinxTools.Xdc(elementsConfig).generate(top.io, className)
      top
    })
  }

  case class Nexys4DDRTop() extends Component {
    val io = Nexys4DDR.Io()

    val soc = Hydrogen1()

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

    for (index <- 0 until 4) {
      io.gpioStatus(index) <> IOBUF(soc.io_per.gpioStatus.pins(index))
    }
  }
}
