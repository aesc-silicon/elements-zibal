package zibal.misc

import java.io._
import spinal.core._
import scala.collection.mutable
import zibal.blackboxes.xilinx.a7._


case class EmbenchIotTools(config: ElementsConfig.ElementsConfig) {
  def generate(cpuFrequency: HertzNumber) = {
    val file = s"${config.zibalBuildPath}${config.className}.yaml"
    val writer = new PrintWriter(new File(file))
    println(s"Generate ${config.className}.yaml")
    writer.write(s"{frequencies: {cpu: ${cpuFrequency.toInt}}}\n")
    writer.close()
  }
}
