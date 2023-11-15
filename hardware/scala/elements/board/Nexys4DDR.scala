package elements.board

import spinal.core._
import spinal.lib._

import zibal.board.{BoardParameter, KitParameter}

object Nexys4DDR {

  object SystemClock {
    val clock = "E3"
    val frequency = 100 MHz
  }
  object UartStd {
    val txd = "D4"
    val rxd = "C4"
    val rts = "D3"
    val cts = "E5"
  }
  object Jtag {
    val tms = "H2"
    val tdi = "G4"
    val tdo = "G2"
    val tck = "F3"
    val frequency = 10 MHz
  }
  object LEDs {
    object LED16 {
      val blue = "R12"
    }
  }
  object Buttons {
    val cpuResetN = "C12"
  }

  case class Parameter(
      kitParameter: KitParameter,
      mainClockFrequency: HertzNumber
  ) extends BoardParameter(
        kitParameter,
        mainClockFrequency
      ) {}
}
