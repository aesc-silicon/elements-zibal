// SPDX-FileCopyrightText: 2026 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package elements.board

import spinal.core._
import spinal.lib._

import zibal.board.{BoardParameter, KitParameter, SysconInfo}
import nafarr.{Product, Vendor}

object ElemRVFlask {

  val sysconInfo = SysconInfo(Vendor.AescSilicon, Product.ElemRV)

  object Hydrogen {
    val oscillatorFrequency = 50 MHz

    case class Parameter(
        kitParameter: KitParameter
    ) extends BoardParameter(
          kitParameter,
          oscillatorFrequency
        ) {
      def getJtagFrequency = 10 MHz
      override val sysconInfo = ElemRVFlask.sysconInfo
    }
  }

  object Nitrogen {
    val oscillatorFrequency = 60 MHz

    case class Parameter(
        kitParameter: KitParameter
    ) extends BoardParameter(
          kitParameter,
          oscillatorFrequency
        ) {
      def getJtagFrequency = 10 MHz
      override val sysconInfo = ElemRVFlask.sysconInfo
    }
  }
}
