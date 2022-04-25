package zibal.misc

import java.io.{FileWriter, File, FileInputStream}
import java.util.HashMap
import spinal.core._
import scala.collection.mutable
import org.yaml.snakeyaml.{DumperOptions, Yaml}

case class SoftwareStorage(
    config: ElementsConfig.ElementsConfig,
    storage: String,
    os: String,
    app: String
) {
  val path = config.swStorageBuildPath(storage)
  val filePath = config.softwareBuildPath + "storages.yaml"

  require(
    List("zephyr", "baremetal", "linux") contains os,
    "Allowed OS types: zeyphr, baremetal, linux"
  )

  def createPath(path: String) = {
    val directory = new File(path);
    if (!directory.exists()) {
      directory.mkdirs();
      SpinalInfo(s"Create path ${path}")
    }
  }

  createPath(path)
  if (os.equals("zephyr")) {
    createPath(config.swStorageZephyrBoardPath(storage))
  }

  val yaml = new Yaml()
  var storages = new HashMap[String, Any]()
  var content = new HashMap[String, Any]()
  val file = new File(filePath)
  if (file.exists()) {
    val storages = yaml.load(new FileInputStream(file))
  } else {
    SpinalInfo(s"Generate ${filePath.split('/').last}")
  }

  if (!storages.containsKey(storage)) {
    content.put("os", os)
    if (!app.equals("")) {
      content.put("application", app)
    }
    storages.put(storage, content)
  }

  val options = new DumperOptions()
  options.setWidth(50)
  options.setIndent(4)
  options.setCanonical(true)
  options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)

  yaml.dump(storages, new FileWriter(file))
}
