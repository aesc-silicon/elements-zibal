package zibal.soc.helium1

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.platform.Helium
import zibal.soc.Helium1
import zibal.misc.{ElementsConfig, BinTools, XilinxTools, SimulationHelper, TestCases}
import zibal.blackboxes.xilinx.a7._


object AX7035Board {
  def apply(source: String) = AX7035Board(source)

  def quartzFrequency = 50 MHz

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)

    val compiled = elementsConfig.genFPGASimConfig.compile {
      val board = AX7035Board(args(0))
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

  case class AX7035Board(source: String) extends Component {
    val io = new Bundle {
      val clock = inout(Analog(Bool))
      val uartStd = new Bundle {
        val txd = inout(Analog(Bool))
        val rxd = inout(Analog(Bool))
      }
      val gpioStatus = Vec(inout(Analog(Bool())), 4)
    }

    val top = AX7035Top()
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

    val peripherals = top.soc.p.peripherals.asInstanceOf[Helium1.Peripherals]
    val baudPeriod = peripherals.uartStd.init.getBaudPeriod()

    def simHook() {
      top.pllArea.pll.simClock(top.soc.p.sysFrequency, top.soc.io_sys.clock)
    }
  }
}


object AX7035Top {
  def apply() = AX7035Top(Helium1.Parameter.default(clocks))

  val clocks = Helium1.Parameter.Clocks(105 MHz)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)
    val spinalConfig = elementsConfig.genFPGASpinalConfig

    spinalConfig.generateVerilog({
      val parameter = Helium1.Parameter.default(clocks)
      args(0) match {
        case "prepare" =>
          val soc = Helium1(parameter)
          Helium1.prepare(soc, elementsConfig)
          soc
        case _ =>
          val top = AX7035Top(parameter)
          val system = top.soc.system
          BinTools.initRam(system.onChipRam.ram, elementsConfig.zephyrBuildPath + "/zephyr.bin")
          val xdc = XilinxTools.Xdc(elementsConfig)
          xdc.addGeneratedClock(top.pllArea.pll.CLKOUT0)
          xdc.generate(top.io)
          top
      }
    })
  }

  case class AX7035Top(parameter: Helium.Parameter) extends Component {
    val io = new Bundle {
      val clock = XilinxCmosIo("Y18").clock(AX7035Board.quartzFrequency)
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

    val soc = Helium1(parameter)

    val clock = Bool()
    io.clock <> IBUF(clock)
    val pllClockDomain = ClockDomain(
      clock = clock,
      frequency = FixedFrequency(AX7035Board.quartzFrequency),
      config = ClockDomainConfig(
        resetKind = BOOT
      )
    )
    val pllArea = new ClockingArea(pllClockDomain) {
      val pll = PLL.PLLE2_BASE(CLKFBOUT_MULT=21).connect()
      soc.io_sys.clock.simPublic()
      soc.io_sys.clock := pll.addClock0(clocks.sysFrequency)
    }
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
