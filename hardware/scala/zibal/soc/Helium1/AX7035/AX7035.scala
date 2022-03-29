package zibal.soc.helium1

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import nafarr.system.reset._
import nafarr.system.reset.ResetControllerCtrl._
import nafarr.system.clock._
import nafarr.system.clock.ClockControllerCtrl._
import nafarr.blackboxes.xilinx.a7._

import zibal.board.{KitParameter, BoardParameter}
import zibal.board.AX7035
import zibal.platform.Helium
import zibal.soc.Helium1
import zibal.misc.{ElementsConfig, BinTools, XilinxTools, SimulationHelper, TestCases}


object AX7035Board {
  def apply(source: String) = AX7035Board(source)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)

    val compiled = elementsConfig.genFPGASimConfig.compile {
      val board = AX7035Board(args(0))
      board.top.soc.initOnChipRam(elementsConfig.zephyrBuildPath + "/zephyr.bin")
      for (domain <- board.top.soc.parameter.getKitParameter.clocks) {
        board.top.soc.clockCtrl.getClockDomainByName(domain.name).clock.simPublic()
      }
      board
    }
    args(1) match {
      case "simulate" =>
        compiled.doSimUntilVoid("simulate") { dut =>
          dut.simHook()
          val testCases = TestCases()
          testCases.addClock(dut.io.clock, AX7035.oscillatorFrequency, 10 ms)
          testCases.dump(dut.io.uartStd.txd, dut.baudPeriod)
        }
      case "boot" =>
        compiled.doSimUntilVoid("boot") { dut =>
          dut.simHook()
          val testCases = TestCases()
          testCases.addClockWithTimeout(dut.io.clock, AX7035.oscillatorFrequency, 10 ms)
          testCases.boot(dut.io.uartStd.txd, dut.baudPeriod)
        }
      case "mtimer" =>
        compiled.doSimUntilVoid("mtimer") { dut =>
          dut.simHook()
          val testCases = TestCases()
          testCases.addClockWithTimeout(dut.io.clock, AX7035.oscillatorFrequency, 400 ms)
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

    val baudPeriod = top.soc.socParameter.uartStd.init.getBaudPeriod()

    def simHook() {
      for ((domain, index) <- top.soc.parameter.getKitParameter.clocks.zipWithIndex) {
        val clockDomain = top.soc.clockCtrl.getClockDomainByName(domain.name)
        SimulationHelper.generateEndlessClock(clockDomain.clock, domain.frequency)
      }
    }
  }
}


object AX7035Top {
  def apply() = AX7035Top(getConfig)

  def getConfig = {
    val resets = List[ResetParameter](ResetParameter("system", 64), ResetParameter("debug", 64))
    val clocks = List[ClockParameter](
      ClockParameter("system", 105 MHz, "system"),
      ClockParameter("debug", 105 MHz, "debug", synchronousWith = "system")
    )

    val kitParameter = KitParameter(resets, clocks)
    val boardParameter = AX7035.Parameter(kitParameter)
    val socParameter = Helium1.Parameter(boardParameter)
    Helium.Parameter(socParameter, 128 kB,
      (resetCtrl: ResetControllerCtrl, _, clock: Bool) => { resetCtrl.buildXilinx(clock) },
      (clockCtrl: ClockControllerCtrl, resetCtrl: ResetControllerCtrl, clock: Bool) => {
        clockCtrl.buildXilinxPll(clock, boardParameter.getOscillatorFrequency,
          List("system", "debug"), 21)
      })
  }

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)
    val spinalConfig = elementsConfig.genFPGASpinalConfig

    spinalConfig.generateVerilog({
      args(0) match {
        case "prepare" =>
          val soc = Helium1(getConfig)
          Helium1.prepare(soc, elementsConfig)
          soc
        case _ =>
          val top = AX7035Top(getConfig)
          top.soc.initOnChipRam(elementsConfig.zephyrBuildPath + "/zephyr.bin")
          val xdc = XilinxTools.Xdc(elementsConfig)
          top.soc.clockCtrl.generatedClocks foreach { clock => xdc.addGeneratedClock(clock) }
          xdc.generate(top.io)
          top
      }
    })
  }

  case class AX7035Top(parameter: Helium.Parameter) extends Component {
    var boardParameter = parameter.getBoardParameter.asInstanceOf[AX7035.Parameter]
    val io = new Bundle {
      val clock = XilinxCmosIo("Y18").clock(boardParameter.getOscillatorFrequency)
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

    io.clock <> IBUF(soc.io_plat.clock)

    soc.io_plat.jtag.tms := False
    soc.io_plat.jtag.tdi := False
    soc.io_plat.jtag.tck := False

    io.uartStd.txd <> OBUF(soc.io_per.uartStd.txd)
    io.uartStd.rxd <> IBUF(soc.io_per.uartStd.rxd)
    soc.io_per.uartStd.cts := False

    for (index <- 0 until 4) {
      io.gpioStatus(index) <> IOBUF(soc.io_per.gpioStatus.pins(index))
    }
  }
}
