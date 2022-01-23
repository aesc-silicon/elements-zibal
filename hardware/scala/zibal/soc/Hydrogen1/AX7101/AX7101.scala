package zibal.soc.hydrogen1

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.platform.Hydrogen
import zibal.soc.Hydrogen1
import zibal.misc.{ElementsConfig, BinTools, XilinxTools, SimulationHelper, TestCases}
import zibal.blackboxes.xilinx.a7._


object AX7101 {
  case class Io(parameter: Hydrogen.Parameter) extends Bundle {
    val clockPos = XilinxLvdsInput.Pos("R4").clock(parameter.sysFrequency).ioStandard("DIFF_SSTL15")
    val clockNeg = XilinxLvdsInput.Neg("T4").clock(parameter.sysFrequency).ioStandard("DIFF_SSTL15")
    val uartStd = new Bundle {
      val txd = XilinxCmosIo("AB15")
      val rxd = XilinxCmosIo("AA15")
    }
    val gpioStatus = Vec(
      XilinxCmosIo("W5").ioStandard("LVCMOS15"),
      XilinxCmosIo("E17"),
      XilinxCmosIo("F16"),
      XilinxCmosIo("T6").ioStandard("LVCMOS15")
    )
  }
}


object AX7101Board {
  def apply(source: String) = AX7101Board(source)

  case class Parameter(sysFrequency: HertzNumber, dbgFrequency: HertzNumber) {
    def convert = Hydrogen1.Parameter.default(sysFrequency, dbgFrequency)
  }
  val parameter = Parameter(200 MHz, 0 MHz)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)
    val spinalConfig = SpinalConfig(noRandBoot = false,
      targetDirectory = elementsConfig.zibalBuildPath)

    val compiled = SimConfig.withConfig(spinalConfig).withWave.workspacePath(elementsConfig.zibalBuildPath).allOptimisation.compile {
      val board = AX7101Board(args(0))
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

  case class AX7101Board(source: String) extends Component {
    val io = new Bundle {
      val clock = in(Bool)
      val uartStd = new Bundle {
        val txd = inout(Analog(Bool))
        val rxd = inout(Analog(Bool))
      }
      val gpioStatus = Vec(inout(Analog(Bool())), 4)
    }
    val peripherals = parameter.convert.peripherals.asInstanceOf[Hydrogen1.Peripherals]
    val baudPeriod = peripherals.uartStd.init.getBaudPeriod()
    val clockFrequency = parameter.convert.sysFrequency

    val top = AX7101Top(source)
    val analogFalse = Analog(Bool)
    analogFalse := False
    val analogTrue = Analog(Bool)
    analogTrue := True

    top.io.clockPos.PAD := io.clock
    top.io.clockNeg.PAD := False

    top.io.uartStd.rxd.PAD := io.uartStd.rxd
    io.uartStd.txd := top.io.uartStd.txd.PAD

    for (index <- 0 until 4) {
      io.gpioStatus(index) <> top.io.gpioStatus(index).PAD
    }
  }

  case class AX7101Top(source: String) extends BlackBox {
    val io = AX7101.Io(parameter.convert)

    val elementsConfig = ElementsConfig(this)
    SimulationHelper.Xilinx.addRtl(this, elementsConfig, source)
    SimulationHelper.Xilinx.addBinary(this, elementsConfig)
  }
}


object AX7101Top {
  def apply() = AX7101Top(AX7101Board.parameter.convert)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)
    val spinalConfig = SpinalConfig(noRandBoot = false,
      targetDirectory = elementsConfig.zibalBuildPath)

    spinalConfig.generateVerilog({
      val parameter = AX7101Board.parameter.convert
      args(0) match {
        case "prepare" =>
          val soc = Hydrogen1(parameter)
          Hydrogen1.prepare(soc, elementsConfig)
          soc
        case _ =>
          val top = AX7101Top(parameter)
          val system = top.soc.system
          BinTools.initRam(system.onChipRam.ram, elementsConfig.zephyrBuildPath + "/zephyr.bin")
          XilinxTools.Xdc(elementsConfig).generate(top.io)
          top
      }
    })
  }

  case class AX7101Top(parameter: Hydrogen.Parameter) extends Component {
    val io = AX7101.Io(parameter)

    val soc = Hydrogen1(parameter)

    val clock = IBUFDS(soc.io_sys.clock)
    io.clockPos <> clock
    io.clockNeg <> clock
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
