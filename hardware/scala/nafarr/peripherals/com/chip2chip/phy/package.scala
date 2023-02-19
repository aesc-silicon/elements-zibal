package nafarr.peripherals.com.chip2chip

import spinal.core._
import spinal.lib._

package object phy {
  case class DataBlockContainer(ioPins: Int = 16) extends Bundle {
    val data = Vec(Bits(10 bits), ioPins)
    val aux = Bool
    val fec = Bool
  }
}
