// SPDX-FileCopyrightText: 2026 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package elements.board

import spinal.core._
import spinal.lib._

import zibal.board.{BoardParameter, KitParameter}

/** FPGA version of ElemRV-Flask: a Radiona ULX3S (12F or 85F) with the ElemRV daughter board.
  *
  * Clock, LEDs and buttons are on the ULX3S. All other interfaces are on the daughter board,
  * which plugs into the GP/GN header; comments name the header pin of each ball.
  */
object ElemRVFlaskULX3S {

  object SystemClock {
    val clock = "G2"
    val frequency = 25 MHz
  }
  object LEDs {
    val d0 = "B2"
    val d1 = "C2"
    val d2 = "C1"
    val d3 = "D2"
    val d4 = "D1"
    val d5 = "E2"
    val d6 = "E1"
    val d7 = "H3"
  }
  // Active high.
  object Buttons {
    val b1 = "R1" // FIRE1
    val b2 = "T1" // FIRE2
  }
  object Uart0 {
    val txd = "N17" // GP15
    val rxd = "P16" // GN15
    val rts = "M17" // GN16
    val cts = "N16" // GP16
  }
  object SpiFlash {
    val cs = "A4" // GP8
    val sck = "B4" // GN10
    val io0 = "A2" // GP9, MOSI
    val io1 = "A5" // GN8, MISO
    val io2 = "B1" // GN9
    val io3 = "C4" // GP10
    val reset = "E3" // GN11
  }
  object HyperBus {
    val ck = "C11" // GN0
    val rwds = "B11" // GP0
    val dq0 = "A9" // GP2
    val dq1 = "A10" // GP1
    val dq2 = "B9" // GP3
    val dq3 = "A8" // GN4
    val dq4 = "A7" // GP4
    val dq5 = "C10" // GN3
    val dq6 = "B10" // GN2
    val dq7 = "A11" // GN1
    val cs0 = "B8" // GN5
    val reset = "C7" // GN6
  }
  object Jtag {
    val frequency = 10 MHz
    val tck = "L16" // GP17
    val tms = "H17" // GN18
    val tdi = "H18" // GP18
    val tdo = "L17" // GN17
    val trstN = "F17" // GP19
  }
  object Reset {
    val resetN = "B17" // GP23
  }
  object Boot {
    val boot0 = "E13" // GN27
    val boot1 = "D13" // GP27
  }
  // Test lines to the RP2040, named after its GPIOs.
  object PiGpio {
    val gpio02 = "B13" // GP26
    val gpio03 = "C13" // GN26
    val gpio04 = "D14" // GP25
    val gpio05 = "E14" // GN25
    val gpio06 = "C16" // GP24
    val gpio07 = "D16" // GN24
    val gpio08 = "G3" // GP12
    val gpio09 = "F3" // GN12
    val gpio10 = "U18" // GP14
    val gpio11 = "U17" // GN14
    val gpio22 = "G18" // GN19
    val gpio23 = "D18" // GP20
    val gpio24 = "E17" // GN20
    val gpio25 = "C18" // GP21
    val gpio26 = "D17" // GN21
    val gpio27 = "B15" // GP22
    val gpio28 = "C15" // GN22
    val gpio29 = "C17" // GN23
  }

  case class Parameter(
      kitParameter: KitParameter,
      mainClockFrequency: HertzNumber
  ) extends BoardParameter(
        kitParameter,
        mainClockFrequency
      ) {
    override val sysconInfo = ElemRVFlask.sysconInfo
  }
}
