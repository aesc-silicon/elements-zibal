package zibal.misc

import spinal.core._
import spinal.core.sim._

object ElementsConfig {
  def apply(top: Object) = ElementsConfig(top)
  case class ElementsConfig(top: Object) {
    val socName = System.getenv("SOC")
    val boardName = System.getenv("BOARD")
    val socBoard = socName + "/" + boardName
    val buildPath = "./build/" + socBoard + "/"
    val zibalBuildPath = buildPath + "zibal/"
    val symbiflowBuildPath = buildPath + "symbiflow/"
    val vivadoBuildPath = buildPath + "vivado/syn/"
    val softwareBuildPath = buildPath + "software/"
    def swStorageBuildPath(name: String) = softwareBuildPath + name + "/"
    def swStorageZephyrBinary(name: String) = swStorageBuildPath(name) + "zephyr/zephyr/zephyr.bin"
    def swStorageZephyrBoardPath(name: String) =
      swStorageBuildPath(name) + "zephyr-boards/boards/riscv/" + socName
    def swStorageBaremetalRom(name: String) = swStorageBuildPath(name) + "kernel.rom"
    def swStorageBaremetalImage(name: String) = swStorageBuildPath(name) + "kernel.img"

    // Prepare class will create files and therefore  replace with Top
    val className = top
      .getClass()
      .getName()
      .stripSuffix("$")
      .split("\\.")
      .last
      .split("\\$")
      .last
      .replace("Prepare", "Top")
    def genFPGASpinalConfig = SpinalConfig(noRandBoot = false, targetDirectory = zibalBuildPath)
    def genFPGASimConfig = SimConfig
      .withConfig(this.genFPGASpinalConfig)
      .withWave
      .addSimulatorFlag("--trace-max-width 100000")
      .workspacePath(this.zibalBuildPath)
      .allOptimisation
    def genASICSpinalConfig = SpinalConfig(noRandBoot = true, targetDirectory = zibalBuildPath)
    def genASICSimConfig = SimConfig
      .withConfig(this.genASICSpinalConfig)
      .withWave
      .addSimulatorFlag("--trace-max-width 100000")
      .workspacePath(this.zibalBuildPath)
      .allOptimisation
  }
}
