package zibal.board

import spinal.core._
import spinal.lib._

import nafarr.peripherals.system.reset.ResetParameter
import nafarr.peripherals.system.clock.ClockParameter

case class KitParameter(
  resets: List[ResetParameter],
  clocks: List[ClockParameter]
)

abstract class BoardParameter(kitParameter: KitParameter, oscillatorFrequency: HertzNumber) {
  def getKitParameter = kitParameter
  def getOscillatorFrequency = oscillatorFrequency
}
