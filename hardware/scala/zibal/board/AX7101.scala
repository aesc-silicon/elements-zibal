package zibal.board

import spinal.core._
import spinal.lib._


object AX7101 {

  val oscillatorFrequency = 200 MHz

  case class Parameter(
    kitParameter: KitParameter
  ) extends BoardParameter(
    kitParameter,
    oscillatorFrequency
  ) {
    def getJtagFrequency = 10 MHz
  }
}
