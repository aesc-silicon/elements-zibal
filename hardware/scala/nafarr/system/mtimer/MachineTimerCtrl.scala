package nafarr.system.mtimer

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc.BusSlaveFactory

object MachineTimerCtrl {
  def apply(p: Parameter = Parameter.default) = MachineTimerCtrl(p)

  case class Parameter(
      width: Int
  ) {
    assert(width > 32, "MachineTimer needs two compare registers")
  }
  object Parameter {
    def default = Parameter(64)
    def lightweight = Parameter(40)
  }

  case class Config(p: Parameter) extends Bundle {
    val compare = UInt(p.width bits)
  }

  case class Io(p: Parameter) extends Bundle {
    val config = in(Config(p))
    val counter = out(UInt(p.width bits))
    val clear = in(Bool)
    val interrupt = out(Bool)
  }

  case class MachineTimerCtrl(p: Parameter) extends Component {
    val io = Io(p)

    /* Start with initiale value of 1 to skip 'lock' cycle at ramp up */
    val counter = Reg(UInt(p.width bits)).init(1)
    io.counter := counter
    val hit = RegInit(False)
    val lock = RegInit(True)

    when(io.clear) {
      lock := False
    }
    when(!lock) {
      counter := counter + 1
    }

    when(io.clear || lock) {
      hit := False
    }.elsewhen(!(counter - io.config.compare).msb) {
      hit := True
    }
    io.interrupt := hit
  }

  case class Mapper(
      busCtrl: BusSlaveFactory,
      ctrl: Io,
      p: Parameter
  ) extends Area {

    val cfg = Reg(ctrl.config)
    cfg.compare.init(0)

    busCtrl.readMultiWord(ctrl.counter, 0x0)
    busCtrl.writeMultiWord(cfg.compare, 0x8)

    /* Clear interrupt when writing to a compare register */
    ctrl.clear := busCtrl.isWriting(0x8) || busCtrl.isWriting(0xc)

    ctrl.config <> cfg
  }
}
