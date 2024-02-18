package zibal.sim.hyperram

import spinal.core._
import spinal.core.sim._
import spinal.lib._

object W956A8MBYA {
  def apply() = W956A8MBYA()

  case class W956A8MBYA() extends Component {
    val ck = inout(Analog(Bool()))
    val ckN = inout(Analog(Bool()))
    val dq = inout(Analog(Bits(8 bits)))
    val rwds = inout(Analog(Bool()))
    val csN = inout(Analog(Bool()))
    val resetN = inout(Analog(Bool()))

  }
}
