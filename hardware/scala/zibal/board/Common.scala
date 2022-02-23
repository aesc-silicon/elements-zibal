package zibal.board

import spinal.core._
import spinal.lib._


case class KitParameter(
  resets: List[ResetParameter],
  clocks: List[ClockParameter]
)

abstract class BoardParameter(kitParameter: KitParameter, oscillatorFrequency: HertzNumber) {
  def getKitParameter = kitParameter
  def getOscillatorFrequency = oscillatorFrequency
}

case class ClockParameter(
  name: String,
  frequency: HertzNumber,
  reset: String = "",
  resetConfig: ClockDomainConfig =
    ClockDomainConfig(resetKind = spinal.core.SYNC, resetActiveLevel = LOW),
  synchronousWith: String = ""
)
case class ResetParameter(name: String, delay: Int)
