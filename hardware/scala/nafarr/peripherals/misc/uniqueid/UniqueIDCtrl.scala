package nafarr.peripherals.misc.uniqueid

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc.BusSlaveFactory

object UniqueIDCtrl {
  def apply(p: Parameter = Parameter.default) = UniqueIDCtrl(p)

  case class Parameter() {}
  object Parameter {
    def default = Parameter()
  }

  case class Config(p: Parameter) extends Bundle {}

  case class Io(p: Parameter) extends Bundle {}

  case class UniqueIDCtrl(p: Parameter) extends Component {
    val io = Io(p)

  }

  case class Mapper(
      busCtrl: BusSlaveFactory,
      ctrl: Io,
      p: Parameter
  ) extends Area {

    val id = Reg(UInt(32 bits)).init(U"hCAFEBABE")

    busCtrl.read(id, 0x0)
  }
}
