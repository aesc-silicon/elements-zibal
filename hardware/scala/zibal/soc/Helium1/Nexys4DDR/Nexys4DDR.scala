package zibal.soc.helium1

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.platform.Helium
import zibal.soc.Helium1
import zibal.misc.{ElementsConfig, BinTools, XilinxTools, SimulationHelper, TestCases}
import zibal.blackboxes.xilinx.a7._


object Nexys4DDR {
  case class Io(parameter: Helium.Parameter) extends Bundle {
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
  }
}


object Nexys4DDRBoard {
  def apply(source: String) = Nexys4DDRBoard(source)

  case class Parameter(sysFrequency: HertzNumber, dbgFrequency: HertzNumber) {
    def convert = Helium1.Parameter.default(sysFrequency, dbgFrequency)
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
    }
    val peripherals = parameter.convert.peripherals.asInstanceOf[Helium1.Peripherals]
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

    for (index <- 0 until 4) {
      io.gpioStatus(index) <> top.io.gpioStatus(index).PAD
    }
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
          val soc = Helium1(parameter)
          Helium1.prepare(soc, elementsConfig)
          soc
        case _ =>
          val top = Nexys4DDRTop(parameter)
          val system = top.soc.system
          BinTools.initRam(system.onChipRam.ram, elementsConfig.zephyrBuildPath + "/zephyr.bin")
          XilinxTools.Xdc(elementsConfig).generate(top.io, true, false)
          top
      }
    })
  }

  case class Nexys4DDRTop(parameter: Helium.Parameter) extends Component {
    val io = Nexys4DDR.Io(parameter)

    val soc = Helium1(parameter)

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
