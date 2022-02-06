package zibal.soc.hydrogen1

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.platform.Hydrogen
import zibal.soc.Hydrogen1
import zibal.misc.{ElementsConfig, BinTools, XilinxTools, SimulationHelper, TestCases}
import zibal.blackboxes.xilinx.a7._


object DH006Board {
  def apply(source: String) = DH006Board(source)

  def quartzFrequency = 100 MHz

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)

    val compiled = elementsConfig.genFPGASimConfig.compile {
      val board = DH006Board(args(0))
      val system = board.top.soc.system
      BinTools.initRam(system.onChipRam.ram, elementsConfig.zephyrBuildPath + "/zephyr.bin")
      board
    }
    args(1) match {
      case "simulate" =>
        compiled.doSimUntilVoid("simulate") { dut =>
          dut.simHook()
          val testCases = TestCases()
          testCases.addClock(dut.io.clock, quartzFrequency, 10 ms)
          testCases.dump(dut.io.uartStd.txd, dut.baudPeriod)
        }
      case "boot" =>
        compiled.doSimUntilVoid("boot") { dut =>
          dut.simHook()
          val testCases = TestCases()
          testCases.addClockWithTimeout(dut.io.clock, quartzFrequency, 10 ms)
          testCases.boot(dut.io.uartStd.txd, dut.baudPeriod)
        }
      case "mtimer" =>
        compiled.doSimUntilVoid("mtimer") { dut =>
          dut.simHook()
          val testCases = TestCases()
          testCases.addClockWithTimeout(dut.io.clock, quartzFrequency, 400 ms)
          testCases.heartbeat(dut.io.gpioStatus(0))
        }
      case _ =>
        println(s"Unknown simulation ${args(1)}")
    }
  }

  case class DH006Board(source: String) extends Component {
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

    val top = DH006Top()
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

    val peripherals = top.soc.p.peripherals.asInstanceOf[Hydrogen1.Peripherals]
    val baudPeriod = peripherals.uartStd.init.getBaudPeriod()

    def simHook() {}
  }
}


object DH006Top {
  def apply() = DH006Top(Hydrogen1.Parameter.default(clocks))

  val clocks = Hydrogen1.Parameter.Clocks(DH006Board.quartzFrequency)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)
    val spinalConfig = elementsConfig.genFPGASpinalConfig

    spinalConfig.generateVerilog({
      val parameter = Hydrogen1.Parameter.default(clocks)
      args(0) match {
        case "prepare" =>
          val soc = Hydrogen1(parameter)
          Hydrogen1.prepare(soc, elementsConfig)
          soc
        case _ =>
          val top = DH006Top(parameter)
          val system = top.soc.system
          BinTools.initRam(system.onChipRam.ram, elementsConfig.zephyrBuildPath + "/zephyr.bin")
          XilinxTools.Xdc(elementsConfig).generate(top.io)
          top
      }
    })
  }

  case class DH006Top(parameter: Hydrogen.Parameter) extends Component {
    val io = new Bundle {
      val clock = XilinxCmosIo("E12").clock(clocks.sysFrequency)
      val jtag = new Bundle {
        val tms = XilinxCmosIo("R13")
        val tdi = XilinxCmosIo("N13")
        val tdo = XilinxCmosIo("P13")
        val tck = XilinxCmosIo("N14").clock(clocks.jtagFrequency)
      }
      val uartStd = new Bundle {
        val txd = XilinxCmosIo("M4")
        val rxd = XilinxCmosIo("L4")
        val rts = XilinxCmosIo("M2")
        val cts = XilinxCmosIo("M1")
      }
      val gpioStatus = Vec(XilinxCmosIo("K12"), XilinxCmosIo("L13"), XilinxCmosIo("K13"),
                           XilinxCmosIo("G11"))
    }

    val soc = Hydrogen1(parameter)

    io.clock <> IBUF(soc.io_sys.clock)
    soc.resetCtrl.buildFPGA(soc.io_sys.clock, soc.io_sys.resets)

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
