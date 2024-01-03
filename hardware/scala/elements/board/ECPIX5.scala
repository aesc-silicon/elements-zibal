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
  object SpiFlash {
    val io0 = "AE2" // MOSI
    val io1 = "AD2" // MISO
    val io2 = "AF2"
    val io3 = "AE1"
    val cs = "AA2"
  }
  object HDMITransmitter {
    object Control {
      val sda = "E17"
      val scl = "C17"
    }
  }
  object Pmods {
    object Pmod0 {
      val pin0 = "T25"
      val pin1 = "U25"
      val pin2 = "U24"
      val pin3 = "V24"
      val pin4 = "T26"
      val pin5 = "U26"
      val pin6 = "V26"
      val pin7 = "W26"
    }
    object Pmod1 {
      val pin0 = "U23"
      val pin1 = "V23"
      val pin2 = "U22"
      val pin3 = "V21"
      val pin4 = "W25"
      val pin5 = "W24"
      val pin6 = "W23"
      val pin7 = "W22"
    }
    object Pmod2 {
      val pin0 = "J24"
      val pin1 = "H22"
      val pin2 = "E21"
      val pin3 = "D18"
      val pin4 = "K22"
      val pin5 = "J21"
      val pin6 = "H21"
      val pin7 = "D22"
    }
    object Pmod3 {
      val pin0 = "E4"
      val pin1 = "F4"
      val pin2 = "E6"
      val pin3 = "H4"
      val pin4 = "F3"
      val pin5 = "D4"
      val pin6 = "D5"
      val pin7 = "F5"
    }
    object Pmod4 {
      val pin0 = "E25" // 0N
      val pin1 = "D25" // 0P
      val pin2 = "F26" // 1N
      val pin3 = "F25" // 1P
      val pin4 = "C26" // 3N
      val pin5 = "C25" // 3P
      val pin6 = "A25" // 2N
      val pin7 = "A24" // 2P
    }
    object Pmod5 {
      val pin0 = "D19"
      val pin1 = "C21"
      val pin2 = "B21"
      val pin3 = "C22"
      val pin4 = "D21"
      val pin5 = "A21"
      val pin6 = "A22"
      val pin7 = "A23"
    }
    object Pmod6 {
      val pin0 = "C16"
      val pin1 = "B17"
      val pin2 = "C18"
      val pin3 = "B19"
      val pin4 = "A17"
      val pin5 = "A18"
      val pin6 = "A19"
      val pin7 = "C19"
    }
    object Pmod7 {
      val pin0 = "D14"
      val pin1 = "B14"
      val pin2 = "E14"
      val pin3 = "B16"
      val pin4 = "C14"
      val pin5 = "A14"
      val pin6 = "A15"
      val pin7 = "A16"
    }
  }

  case class Parameter(
      kitParameter: KitParameter,
      mainClockFrequency: HertzNumber
  ) extends BoardParameter(
        kitParameter,
        mainClockFrequency
      ) {}
}
