package zibal.soc.blinky

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.board.Nexys4DDR
import zibal.soc.Blinky
import zibal.misc.{CadenceTools, SimulationHelper, ElementsConfig, TestCases}
import zibal.blackboxes.ihp.sg13s._


object VirtualSG13S2Board {
  def apply(source: String) = VirtualSG13S2Board(source)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)
    val spinalConfig = elementsConfig.genASICSpinalConfig

    val compiled = elementsConfig.genASICSimConfig.compile {
      val board = VirtualSG13S2Board(args(0))
      board
    }
    args(1) match {
      case "simulate" =>
        compiled.doSimUntilVoid("simulate") { dut =>
          val testCases = TestCases()
          testCases.addClock(dut.io.clock, Nexys4DDR.quartzFrequency, 3 ms)
          testCases.addReset(dut.io.reset, 1 us)
        }
      case _ =>
        println(s"Unknown simulation ${args(1)}")
    }
  }

  case class VirtualSG13S2Board(source: String) extends Component {
    val io = new Bundle {
      val clock = inout(Analog(Bool))
      val reset = inout(Analog(Bool))
      val led = Vec(inout(Analog(Bool())), 6)
    }
    val clockPeriod = 10

    val top = VirtualSG13S2Top()
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


object VirtualSG13S2Top {
  def apply() = VirtualSG13S2Top()

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig(this)
    val spinalConfig = elementsConfig.genASICSpinalConfig

    args(0) match {
      case "prepare" =>
        println("Nothing to do here!")
      case _ =>
        spinalConfig.generateVerilog({
          val top = VirtualSG13S2Top()
          val io = CadenceTools.Io(elementsConfig)
          io.addPad("top", 1, "gndpad")
          io.addPad("right", 1, "gndcore")
          io.addPad("bottom", 1, "vddcore")
          io.addPad("left", 1, "vddpad")
          io.addCorner("topright", 90, "corner")
          io.addCorner("bottomright", 0, "corner")
          io.addCorner("bottomleft", 270, "corner")
          io.addCorner("topleft", 180, "corner")
          io.generate(top.io, elementsConfig.zibalBuildPath)
          val sdc = CadenceTools.Sdc(elementsConfig)
          sdc.addClock(top.io.clock.PAD, Nexys4DDR.quartzFrequency)
          sdc.generate(elementsConfig.zibalBuildPath)
          top
        })
    }
  }

  case class VirtualSG13S2Top() extends Component {
    val io = new Bundle {
      val clock = IhpCmosIo("top", 0)
      val reset = IhpCmosIo("top", 2)
      val led = Vec(IhpCmosIo("right", 0), IhpCmosIo("right", 2),
                    IhpCmosIo("bottom", 0), IhpCmosIo("bottom", 2),
                    IhpCmosIo("left", 0), IhpCmosIo("left", 2))
    }

    val soc = Blinky()

    io.clock <> ixc013_i16x(soc.io.clock)
    io.reset <> ixc013_i16x(soc.io.reset)

    for (index <- 0 until 6) {
      io.led(index) <> ixc013_b16m().asOutput(soc.io.led(index))
    }
  }
}
