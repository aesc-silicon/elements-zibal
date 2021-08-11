/*
 * Copyright (c) 2021 Phytec Messtechnik GmbH
 */

package zibal.misc

object ElementsConfig {
  def apply() = ElementsConfig()
  case class ElementsConfig() {
    val socName = System.getenv("SOC")
    val boardName = System.getenv("BOARD")
    val socBoard = socName + "/" + boardName
    val buildPath = "./../build/"+socBoard+"/"
    val zibalBuildPath = buildPath+"/zibal/"
    val zephyrBuildPath = buildPath+"/zephyr/zephyr/"
    val zephyrBoardPath = buildPath+"/zephyr-boards/boards/riscv/" + socName
    val symbiflowBuildPath = buildPath+"/symbiflow/"
    val vivadoBuildPath = buildPath+"/vivado/syn/"

  }
}
