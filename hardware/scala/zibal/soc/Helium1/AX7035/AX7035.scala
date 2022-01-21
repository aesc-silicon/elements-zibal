/*
 * Copyright (c) 2021 Phytec Messtechnik GmbH
 */

package zibal.soc.helium1

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.platform.Helium
import zibal.soc.Helium1
import zibal.misc.{ElementsConfig, BinTools, XilinxTools, SimulationHelper, TestCases}
import zibal.blackboxes.xilinx.a7._


object AX7035 {
  case class Io(parameter: Helium.Parameter) extends Bundle {
    val clock = XilinxCmosIo("Y18").clock(parameter.sysFrequency)
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

  case class Parameter(sysFrequency: HertzNumber, dbgFrequency: HertzNumber) {
    def convert = Helium1.Parameter.default(sysFrequency, dbgFrequency)
  }
  val parameter = Parameter(50 MHz, 0 MHz)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)
    val spinalConfig = SpinalConfig(noRandBoot = false,
      targetDirectory = elementsConfig.zibalBuildPath)

    val compiled = SimConfig.withConfig(spinalConfig).withWave.workspacePath(elementsConfig.zibalBuildPath).allOptimisation.compile {
      val board = AX7035Board(args(0))
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
      case _ =>
        println(s"Unknown simulation ${args(1)}")
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
    val peripherals = parameter.convert.peripherals.asInstanceOf[Helium1.Peripherals]
    val baudPeriod = peripherals.uartStd.init.getBaudPeriod()
    val clockFrequency = parameter.convert.sysFrequency

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

  case class AX7035Top(source: String) extends BlackBox {
    val io = AX7035.Io(parameter.convert)

    val elementsConfig = ElementsConfig(this)
    SimulationHelper.Xilinx.addRtl(this, elementsConfig, source)
    SimulationHelper.Xilinx.addBinary(this, elementsConfig)
  }
}


object AX7035Top {
  def apply() = AX7035Top(AX7035Board.parameter.convert)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)
    val spinalConfig = SpinalConfig(noRandBoot = false,
      targetDirectory = elementsConfig.zibalBuildPath)

    spinalConfig.generateVerilog({
      val parameter = AX7035Board.parameter.convert
      args(0) match {
        case "prepare" =>
          val soc = Helium1(parameter)
          Helium1.prepare(soc, elementsConfig)
          soc
        case _ =>
          val top = AX7035Top(parameter)
          val system = top.soc.system
          BinTools.initRam(system.onChipRam.ram, elementsConfig.zephyrBuildPath + "/zephyr.bin")
          XilinxTools.Xdc(elementsConfig).generate(top.io)
          top
      }
    })
  }

  case class AX7035Top(parameter: Helium.Parameter) extends Component {
    val io = AX7035.Io(parameter)

    val soc = Helium1(parameter)

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
