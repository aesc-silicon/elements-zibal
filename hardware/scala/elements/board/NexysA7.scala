// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package elements.board

import spinal.core._
import spinal.lib._

import zibal.board.{BoardParameter, KitParameter}

object NexysA7 {

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
      val red = "N15"
      val green = "M16"
      val blue = "R12"
    }
    object LED17 {
      val red = "N16"
      val green = "R11"
      val blue = "G14"
    }
    val led0 = "H17"
    val led1 = "K15"
    val led2 = "J13"
    val led3 = "N14"
    val led4 = "R18"
    val led5 = "V17"
    val led6 = "U17"
    val led7 = "U16"
    val led8 = "V16"
    val led9 = "T15"
    val led10 = "U14"
    val led11 = "T16"
    val led12 = "V15"
    val led13 = "V14"
    val led14 = "V12"
    val led15 = "V11"
  }
  object Buttons {
    val cpuResetN = "C12"
    val buttonCenter = "N17"
    val buttonUp = "M18"
    val buttonRight = "M17"
    val buttonDown = "P18"
    val buttonLeft = "P17"
    val sw0 = "J15"
    val sw1 = "L16"
    val sw2 = "M13"
    val sw3 = "R15"
    val sw4 = "R17"
    val sw5 = "T18"
    val sw6 = "U18"
    val sw7 = "R13"
    val sw8 = "T8"
    val sw9 = "U8"
    val sw10 = "R16"
    val sw11 = "T13"
    val sw12 = "H6"
    val sw13 = "U12"
    val sw14 = "U11"
    val sw15 = "V10"
  }
  object I2c {
    object Tmp {
      val scl = "C14"
      val sda = "C15"
      val int = "D13"
      val intCritical = "B14"
    }
  }
  object Spi {
    object Acl {
      val sclk = "F15"
      val csN = "D15"
      val mosi = "F14"
      val miso = "E15"
    }
  }
  object SevenSegment {
    object Cathodes {
      val ca = "T10"
      val cb = "R10"
      val cc = "K16"
      val cd = "K13"
      val ce = "P15"
      val cf = "T11"
      val cg = "L18"
    }
    val dp = "H15"
    object Anodes {
      val an0 = "J17"
      val an1 = "J18"
      val an2 = "T9"
      val an3 = "J14"
      val an4 = "P14"
      val an5 = "T14"
      val an6 = "K2"
      val an7 = "U13"
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
