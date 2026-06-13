// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal

import spinal.core._
import spinal.lib._

import nafarr.system.reset.ResetParameter
import nafarr.system.clock.ClockParameter
import nafarr.{Product, Vendor, Feature}

package object board {
  case class KitParameter(
      resets: List[ResetParameter],
      clocks: List[ClockParameter],
      inputClock: ClockParameter
  )

  /** Board-level chip identity forwarded to the syscon IP.
    *
    * siliconMajor/Minor track tape-out revisions (0/1 = first spin).
    * Boards that don't override this get Product.ElemRV, rev 0.1 as default.
    */
  case class SysconInfo(
      vendor: Vendor.E,
      product: Product.E,
      siliconMajor: Int = 0,
      siliconMinor: Int = 1
  )

  abstract class BoardParameter(kitParameter: KitParameter, oscillatorFrequency: HertzNumber) {
    def getKitParameter = kitParameter
    def getOscillatorFrequency = oscillatorFrequency
    val sysconInfo: SysconInfo = SysconInfo(Vendor.AescSilicon, Product.ElemRV)
  }
}

package object soc {
  abstract class SocParameter(boardParameter: board.BoardParameter) {

    /** Interrupt-producing SoC IPs. Each entry contributes one interrupt line.
      * Declare the interrupt-generating IP parameters here so the PLIC is sized
      * automatically — no manual socInterrupts count needed.
      */
    val irqSources: Seq[_] = Seq()
    def getInterruptCount(platformInterrupts: Int) = irqSources.size + platformInterrupts

    /** Error-producing SoC IPs. Each entry contributes one ESM input line.
      * Declare the error-generating IP parameters here so the ESM is sized
      * automatically — no manual errorCount needed.
      */
    val errorSources: Seq[_] = Seq()
    def getErrorCount(platformErrors: Int) = errorSources.size + platformErrors

    def getKitParameter = boardParameter.getKitParameter
    def getBoardParameter = boardParameter
  }
}
