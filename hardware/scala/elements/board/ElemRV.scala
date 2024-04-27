package elements.board

import spinal.core._
import spinal.lib._

import zibal.board.{BoardParameter, KitParameter}

object ElemRVBoard {

  object SystemClock {
    val frequency = 25 MHz
  }

  case class Parameter(
      kitParameter: KitParameter
  ) extends BoardParameter(
        kitParameter,
        SystemClock.frequency
      ) {
    def getJtagFrequency = 10 MHz
  }
}
