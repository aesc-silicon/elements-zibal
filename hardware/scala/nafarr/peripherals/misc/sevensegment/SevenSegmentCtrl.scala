package nafarr.peripherals.misc.sevensegment

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc.BusSlaveFactory

object SevenSegmentCtrl {
  def apply(p: Parameter = Parameter.default) = SevenSegmentCtrl(p)

  case class Parameter(
      count: Int
  )
  object Parameter {
    def default = Parameter(8)
  }
  case class Config(p: Parameter) extends Bundle {
    val output = Bits(32 bits)
  }

  case class Io(p: Parameter) extends Bundle {
    val config = in(Config(p))
    val segments = SevenSegment.Io(p)
  }

  case class SevenSegmentCtrl(p: Parameter) extends Component {
    val io = Io(p)

    val area50Hz = new SlowArea(500 Hz) {
      val select = Reg(Bits(p.count bits)).init(B(p.count bits, 0 -> False, default -> True))
      val muxer = Reg(UInt(log2Up(p.count) bits)).init(0)

      select := select.rotateLeft(1)
      muxer := muxer + 1
      when(muxer === p.count) {
        muxer := 0
      }
    }

    io.segments.select := area50Hz.select
    val dataWord = io.config.output.subdivideIn(4 bits)(area50Hz.muxer)
    io.segments.value := ~dataWord.asUInt.mux(
      0 -> B"00111111",
      1 -> B"00000110",
      2 -> B"01011011",
      3 -> B"01001111",
      4 -> B"01100110",
      5 -> B"01101101",
      6 -> B"01111101",
      7 -> B"00000111",
      8 -> B"01111111",
      9 -> B"01101111",
      default -> B"11110110"
    )

  }

  case class Mapper(
      busCtrl: BusSlaveFactory,
      ctrl: Io,
      p: Parameter
  ) extends Area {
    val cfg = Reg(ctrl.config)
    cfg.output.init(0)

    busCtrl.readAndWrite(cfg.output, 0x10)

    ctrl.config <> cfg
  }
}
