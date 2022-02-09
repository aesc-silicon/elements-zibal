package zibal.soc

import spinal.core._
import spinal.lib._

import zibal.board.BoardParameter


abstract class SocParameter(boardParameter: BoardParameter, socInterrupts: Int) {
  // Interrupt on line 0 is False. Add one interrupt be default.
  def getInterruptCount(platformInterrupts: Int) = 1 + socInterrupts + platformInterrupts

  def getKitParameter = boardParameter.getKitParameter
  def getBoardParameter = boardParameter
}
