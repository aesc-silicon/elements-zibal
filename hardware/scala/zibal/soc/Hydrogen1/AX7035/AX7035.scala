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


object AX7035 {
  case class Io() extends Bundle {
    val clock = XilinxCmosIo("Y18").clock(50 MHz)
    val uartStd = new Bundle {
      val txd = XilinxCmosIo("G16")
      val rxd = XilinxCmosIo("G15")
    }
    val gpioStatus = Vec(
      XilinxCmosIo("F19"),
      XilinxCmosIo("E21"),
      XilinxCmosIo("D20"),
      XilinxCmosIo("F20")
    )
  }
}


object AX7035Board {
  def apply(source: String) = AX7035Board(source)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig()

    var baudPeriod: Int = 0
    var clockPeriod: Int = 0

    val config = SpinalConfig(noRandBoot = false, targetDirectory = elementsConfig.zibalBuildPath)
    val compiled = SimConfig.withConfig(config).withWave.workspacePath(elementsConfig.zibalBuildPath).allOptimisation.compile {
      val parameter = Hydrogen1.Peripherals.default(50 MHz)
      val peripherals = parameter.peripherals.asInstanceOf[Hydrogen1.Peripherals]
      baudPeriod = peripherals.uartStd.init.getBaudPeriod()
      clockPeriod = 1000000000 / parameter.sysFrequency.toInt

      val board = AX7035Board(args(0))
      board
    }
    args(1) match {
      case "boot" =>
        compiled.doSimUntilVoid("boot") { dut =>
          SimulationHelper.generateClock(dut.io.clock, clockPeriod, 20000000)
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

  case class AX7035Top(source: String) extends BlackBox {
    val io = AX7035.Io()

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

  case class AX7035Board(source: String) extends Component {
    val io = new Bundle {
      val clock = inout(Analog(Bool))
      val uartStd = new Bundle {
        val txd = inout(Analog(Bool))
        val rxd = inout(Analog(Bool))
      }
      val gpioStatus = Vec(inout(Analog(Bool())), 4)
    }
    val top = AX7035Top(source)
    val analogFalse = Analog(Bool)
    analogFalse := False
    val analogTrue = Analog(Bool)
    analogTrue := True

    top.io.clock.PAD := io.clock

    top.io.uartStd.rxd.PAD := io.uartStd.rxd
    io.uartStd.txd := top.io.uartStd.txd.PAD

    for (index <- 0 until 4) {
      io.gpioStatus(index) <> top.io.gpioStatus(index).PAD
    }
  }
}


object AX7035Top {
  def apply() = AX7035Top()

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig()
    val className = this.getClass().getName().stripSuffix("$").split("\\.").last

    val config = SpinalConfig(noRandBoot = false, targetDirectory = elementsConfig.zibalBuildPath)

    args(0) match {
      case "prepare" =>
        Hydrogen1.prepare(config, elementsConfig, 50 MHz)
      case _ =>
        config.generateVerilog({
          val top = AX7035Top()
          val system = top.soc.system
          BinTools.initRam(system.onChipRam.ram, elementsConfig.zephyrBuildPath + "/zephyr.bin")
          XilinxTools.Xdc(elementsConfig).generate(top.io, className)
          top
        })
    }
  }

  case class AX7035Top() extends Component {
    val io = AX7035.Io()

    val soc = Hydrogen1(Hydrogen1.Peripherals.default(50 MHz))

    io.clock <> IBUF(soc.io_sys.clock)
    soc.io_sys.reset := False

    soc.io_sys.jtag.tms := False
    soc.io_sys.jtag.tdi := False
    soc.io_sys.jtag.tck := False

    io.uartStd.txd <> OBUF(soc.io_per.uartStd.txd)
    io.uartStd.rxd <> IBUF(soc.io_per.uartStd.rxd)
    soc.io_per.uartStd.cts := False

    for (index <- 0 until 4) {
      io.gpioStatus(index) <> IOBUF(soc.io_per.gpioStatus.pins(index))
    }
  }
}
