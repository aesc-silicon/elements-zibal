// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal.misc

import spinal.core._
import spinal.core.sim._

import java.time.LocalDate

object ElementsConfig {
  def apply(top: Object) = ElementsConfig(top)
  case class ElementsConfig(top: Object) {
    val socName = System.getenv("SOC")
    val boardName = System.getenv("BOARD")
    val buildRoot = scala.util.Properties.envOrElse("BUILD_ROOT", "./build") + "/"

    val socBoard = socName + "/" + boardName
    val buildPath = buildRoot + socBoard + "/"
    val zibalBuildPath = buildPath + "zibal/"
    val openroadBuildPath = buildPath + "openroad/"

    def zephyrBinary = buildRoot + "zephyr/zephyr.bin"
    val softwareBuildPath = buildPath + "firmware/"
    def swStorageBuildPath(name: String) = softwareBuildPath + name + "/"
    def swStorageBaremetalImage(name: String) = swStorageBuildPath(name) + "kernel.img"
    def swStorageZephyrBinary(name: String) = swStorageBuildPath(name) + "zephyr/zephyr/zephyr.bin"

    /* Probably not used anymore */
    val symbiflowBuildPath = buildPath + "symbiflow/"
    val vivadoBuildPath = buildPath + "vivado/syn/"
    def swStorageZephyrBoardPath(name: String) =
      swStorageBuildPath(name) + "zephyr-boards/boards/riscv/" + socName
    /* --- */

    val spdx = s"""
SPDX-FileCopyrightText: ${LocalDate.now().getYear} aesc silicon

SPDX-License-Identifier: CERN-OHL-W-2.0
"""

    // Prepare class will create files and therefore replace with Top
    val className = top
      .getClass()
      .getName()
      .stripSuffix("$")
      .split("\\.")
      .last
      .split("\\$")
      .last
      .replace("Generate", "Top")
    def genFPGASpinalConfig =
      SpinalConfig(noRandBoot = false, targetDirectory = zibalBuildPath, rtlHeader = spdx)
    def genFPGASimConfig = SimConfig
      .withConfig(this.genFPGASpinalConfig)
      .withWave
      .addSimulatorFlag("--trace-max-width 100000 --Wno-SPECIFYIGN")
      .workspacePath(this.zibalBuildPath)
      .allOptimisation
    def genASICSpinalConfig =
      SpinalConfig(noRandBoot = true, targetDirectory = zibalBuildPath, rtlHeader = spdx)
    def genASICSimConfig = SimConfig
      .withConfig(this.genASICSpinalConfig)
      .withWave
      .addSimulatorFlag("--trace-max-width 100000")
      .workspacePath(this.zibalBuildPath)
      .allOptimisation
  }
}
