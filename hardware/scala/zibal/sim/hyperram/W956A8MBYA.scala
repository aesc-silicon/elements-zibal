package zibal.sim.hyperram

import spinal.core._
import spinal.core.sim._
import spinal.lib._

object W956A8MBYA {
  def apply() = W956A8MBYA()

  case class W956A8MBYA() extends Component {
    val io = new Bundle {
      val ck = in(Bool)
      val ckN = in(Bool)
      val dqIn = in(Bits(8 bits))
      val dqOut = out(Bits(8 bits))
      val rwdsIn = in(Bool)
      val rwdsOut = out(Bool)
      val csN = in(Bool)
      val resetN = in(Bool)
    }

    io.dqOut := 0
    io.rwdsOut := False
  }
}
