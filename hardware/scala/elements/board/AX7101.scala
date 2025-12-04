// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package elements.board

import spinal.core._
import spinal.lib._

import zibal.board.{BoardParameter, KitParameter}

object AX7101 {

  val oscillatorFrequency = 200 MHz

  case class Parameter(
      kitParameter: KitParameter
  ) extends BoardParameter(
        kitParameter,
        oscillatorFrequency
      ) {
    def getJtagFrequency = 10 MHz
  }
}
