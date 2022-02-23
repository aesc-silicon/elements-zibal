package zibal.soc.blinky

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.board.Nexys4DDR
import zibal.soc.Blinky
import zibal.misc.{ElementsConfig, XilinxTools, SimulationHelper, TestCases}
import zibal.blackboxes.xilinx.a7._


object Nexys4DDRBoard {
  def apply(source: String) = Nexys4DDRBoard(source)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)

    val compiled = elementsConfig.genFPGASimConfig.compile {
      val board = Nexys4DDRBoard(args(0))
      board
    }
    args(1) match {
      case "simulate" =>
        compiled.doSimUntilVoid("simulate") { dut =>
          val testCases = TestCases()
          testCases.addClock(dut.io.clock, Nexys4DDR.oscillatorFrequency, 1 ms)
          testCases.addReset(dut.io.reset, 1 us)
        }
      case _ =>
        println(s"Unknown simulation ${args(1)}")
    }
  }

  case class Nexys4DDRBoard(source: String) extends Component {
    val io = new Bundle {
      val clock = inout(Analog(Bool))
      val reset = inout(Analog(Bool))
      val led = Vec(inout(Analog(Bool())), 6)
    }

    val top = Nexys4DDRTop()
    val analogFalse = Analog(Bool)
    analogFalse := False
    val analogTrue = Analog(Bool)
    analogTrue := True

    top.io.clock.PAD := io.clock
    top.io.reset.PAD := io.reset

    for (index <- 0 until 6) {
      io.led(index) <> top.io.led(index).PAD
    }
  }
}


object Nexys4DDRTop {
  def apply() = Nexys4DDRTop()

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)
    val spinalConfig = elementsConfig.genFPGASpinalConfig

    args(0) match {
      case "prepare" =>
        println("Nothing to do here!")
      case _ =>
        spinalConfig.generateVerilog({
          val top = Nexys4DDRTop()
          XilinxTools.Xdc(elementsConfig).generate(top.io)
          top
        })
    }
  }

  case class Nexys4DDRTop() extends Component {
    val io = new Bundle {
      val clock = XilinxCmosIo("E3").clock(Nexys4DDR.oscillatorFrequency)
      val reset = XilinxCmosIo("C12")
      val led = Vec(
        XilinxCmosIo("H17"),
        XilinxCmosIo("K15"),
        XilinxCmosIo("J13"),
        XilinxCmosIo("N14"),
        XilinxCmosIo("R18"),
        XilinxCmosIo("V17")
      )
    }

    val soc = Blinky()

    io.clock <> IBUF(soc.io.clock)
    io.reset <> IBUF(soc.io.reset)

    for (index <- 0 until 6) {
      io.led(index) <> OBUF(soc.io.led(index))
    }
  }
}
