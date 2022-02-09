package zibal.board

import spinal.core._
import spinal.lib._


object Nexys4DDR {

  val quartzFrequency = 100 MHz

  case class Parameter(
    kitParameter: KitParameter
  ) extends BoardParameter(
    kitParameter,
    quartzFrequency
  ) {
    def getJtagFrequency = 10 MHz
  }
}
