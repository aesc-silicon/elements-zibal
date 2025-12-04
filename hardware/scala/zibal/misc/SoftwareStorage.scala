// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal.misc

import java.io.{FileWriter, File, FileInputStream}
import java.util.HashMap
import spinal.core._
import scala.collection.mutable
import org.yaml.snakeyaml.{DumperOptions, Yaml}

case class SoftwareStorage(
    config: ElementsConfig.ElementsConfig,
    storage: String,
    os: String
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
      SpinalInfo(s"Creating path ${path}")
    }
  }

  createPath(path)
  if (os.equals("zephyr")) {
    createPath(config.swStorageZephyrBoardPath(storage))
  }

  val yaml = new Yaml()
  var storages = new HashMap[String, Any]()
  var content = new HashMap[String, Any]()
  content.put("os", os)

  def add(key: String, value: Any) {
    content.put(key, value)
  }

  def dump() {
    val file = new File(filePath)
    if (file.exists()) {
      storages = yaml.load(new FileInputStream(file)).asInstanceOf[HashMap[String, Any]]
    } else {
      SpinalInfo(s"Generating ${filePath.split('/').last}")
    }

    if (storages.containsKey(storage)) {
      storages.remove(storage)
    }
    storages.put(storage, content)

    val options = new DumperOptions()
    options.setWidth(50)
    options.setIndent(4)
    options.setCanonical(true)
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)

    yaml.dump(storages, new FileWriter(file))
  }
}
