// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package elements

import zibal.misc.ElementsConfig

package object sdk {
  trait ElementsApp extends App {
    val elementsConfig = ElementsConfig(this)
    lazy val simType = if (args.length < 1) "simulate" else args(0)
    lazy val simDuration = if (args.length < 2) 10 else args(1)
  }
}
