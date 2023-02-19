package nafarr.peripherals.com

import spinal.core._
import spinal.lib._

package object chip2chip {
  case class RawDataContainer(width: Int = 128) extends Bundle {
    val data = Bits(width bits)
    val kWord = Bool()
    val fec = Bool()
  }

  val dataBlock = 8
  val encodedBlock = 10
}
