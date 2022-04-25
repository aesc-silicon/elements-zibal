package zibal

import spinal.core._
import spinal.lib._

import nafarr.system.reset.ResetParameter
import nafarr.system.clock.ClockParameter

package object board {
  case class KitParameter(
      resets: List[ResetParameter],
      clocks: List[ClockParameter]
  )

  abstract class BoardParameter(kitParameter: KitParameter, oscillatorFrequency: HertzNumber) {
    def getKitParameter = kitParameter
    def getOscillatorFrequency = oscillatorFrequency
  }
}

package object soc {
  abstract class SocParameter(boardParameter: board.BoardParameter, socInterrupts: Int) {
    // Interrupt on line 0 is False. Add one interrupt be default.
    def getInterruptCount(platformInterrupts: Int) = 1 + socInterrupts + platformInterrupts

    def getKitParameter = boardParameter.getKitParameter
    def getBoardParameter = boardParameter
  }
}
