package zibal.misc

import java.io.FileWriter
import java.util.HashMap
import spinal.core._
import scala.collection.mutable
import org.yaml.snakeyaml.{DumperOptions, Yaml}

case class EmbenchIotTools(config: ElementsConfig.ElementsConfig) {
  def generate(cpuFrequency: HertzNumber) = {
    val file = s"${config.zibalBuildPath}${config.className}.yaml"
    SpinalInfo(s"Generate ${config.className}.yaml")

    val frequencies = new HashMap[String, Any]()
    frequencies.put("cpu", cpuFrequency.toInt)
    val content = new HashMap[String, Any]()
    content.put("frequencies", frequencies)

    val options = new DumperOptions()
    options.setWidth(50)
    options.setIndent(4)
    options.setCanonical(true)
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)

    val yaml = new Yaml()
    yaml.dump(content, new FileWriter(file))
  }
}
