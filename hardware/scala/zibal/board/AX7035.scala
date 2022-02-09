package zibal.board

import spinal.core._
import spinal.lib._


object AX7035 {

  val quartzFrequency = 50 MHz

  case class Parameter(
    kitParameter: KitParameter
  ) extends BoardParameter(
    kitParameter,
    quartzFrequency
  ) {
    def getJtagFrequency = 10 MHz
  }
}
