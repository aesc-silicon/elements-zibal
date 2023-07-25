package elements.board

import spinal.core._
import spinal.lib._

import zibal.board.{BoardParameter, KitParameter}

object ECPIX5 {

  object SystemClock {
    val clock = "K23"
    val frequency = 100 MHz
  }
  object UartStd {
    val txd = "R24"
    val rxd = "R26"
  }
  object LEDs {
    object LD5 {
      val red = "T23"
      val green = "R21"
      val blue = "T22"
    }
    object LD6 {
      val red = "U21"
      val green = "W21"
      val blue = "T24"
    }
    object LD7 {
      val red = "K21"
      val green = "K24"
      val blue = "M21"
    }
    object LD8 {
      val red = "P21"
      val green = "R23"
      val blue = "P22"
    }
  }
  object Buttons {
    val sw0 = "AB1"
  }

  case class Parameter(
      kitParameter: KitParameter,
      mainClockFrequency: HertzNumber
  ) extends BoardParameter(
        kitParameter,
        mainClockFrequency
      ) {
    def getJtagFrequency = 10 MHz
  }
}
