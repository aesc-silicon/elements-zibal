package zibal.misc

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
  }
}
