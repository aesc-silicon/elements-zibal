package zibal.board

import spinal.core._
import spinal.lib._


object AX7101 {

  val quartzFrequency = 200 MHz

  case class Parameter(
    kitParameter: KitParameter
  ) extends BoardParameter(
    kitParameter,
    quartzFrequency
  ) {
    def getJtagFrequency = 10 MHz
  }
}
