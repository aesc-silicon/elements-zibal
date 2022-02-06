package zibal.misc

import spinal.core._
import spinal.core.sim._

object ElementsConfig {
  def apply(top: Object) = ElementsConfig(top)
  case class ElementsConfig(top: Object) {
    val socName = System.getenv("SOC")
    val boardName = System.getenv("BOARD")
    val socBoard = socName + "/" + boardName
    val buildPath = "./../build/"+socBoard+"/"
    val zibalBuildPath = buildPath+"/zibal/"
    val zephyrBuildPath = buildPath+"/zephyr/zephyr/"
    val zephyrBoardPath = buildPath+"/zephyr-boards/boards/riscv/" + socName
    val symbiflowBuildPath = buildPath+"/symbiflow/"
    val vivadoBuildPath = buildPath+"/vivado/syn/"
    val fplBuildPath = buildPath+"/fpl/"
    val className = top.getClass().getName().stripSuffix("$").split("\\.").last.split("\\$").last
    def genFPGASpinalConfig = SpinalConfig(noRandBoot = false, targetDirectory = zibalBuildPath)
    def genFPGASimConfig = SimConfig.withConfig(this.genFPGASpinalConfig).withWave.addSimulatorFlag("--trace-max-width 100000").workspacePath(this.zibalBuildPath).allOptimisation
    def genASICSpinalConfig = SpinalConfig(noRandBoot = true, targetDirectory = zibalBuildPath)
    def genASICSimConfig = SimConfig.withConfig(this.genASICSpinalConfig).withWave.addSimulatorFlag("--trace-max-width 100000").workspacePath(this.zibalBuildPath).allOptimisation
  }
}
