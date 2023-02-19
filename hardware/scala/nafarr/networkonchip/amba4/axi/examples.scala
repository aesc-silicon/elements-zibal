package nafarr.networkonchip.amba4.axi

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba3.apb._

case class Axi4NodeWithExtensionAndTranslation(
    nocConfig: Axi4Config,
    coreConfig: Axi4Config,
    coreUpsizedConfig: Axi4Config
) extends Component {
  val io = new Bundle {
    val core = new Bundle {
      val input = slave(Axi4(coreUpsizedConfig))
      val output = master(Axi4(coreUpsizedConfig))
    }
    val north = new Bundle {
      val input = slave(Axi4(nocConfig))
      val output = master(Axi4(nocConfig))
    }
    val east = new Bundle {
      val input = slave(Axi4(nocConfig))
      val output = master(Axi4(nocConfig))
    }
    val south = new Bundle {
      val input = slave(Axi4(nocConfig))
      val output = master(Axi4(nocConfig))
    }
    val west = new Bundle {
      val input = slave(Axi4(nocConfig))
      val output = master(Axi4(nocConfig))
    }
    val bus = slave(Apb3(apb3Config))
  }

  val extensionEntries = 16
  val translationEntries = 16

  val router = Axi4Router(nocConfig, apb3Config)
  router.io.north <> io.north
  router.io.east <> io.east
  router.io.south <> io.south
  router.io.west <> io.west

  val local = Axi4LocalPort(nocConfig, coreConfig, apb3Config, true, true)
  local.io.local <> router.io.local
  local.io.bus.router <> router.io.bus

  val extension = Axi4MemoryExtension(coreUpsizedConfig, coreConfig, apb3Config, extensionEntries)
  extension.io.fromCore.input <> io.core.output
  extension.io.fromNoc.output <> io.core.input
  local.io.bus.extension <> extension.io.bus

  val translation = Axi4MemoryTranslation(coreConfig, apb3Config, translationEntries)
  translation.io.input <> extension.io.fromCore.output
  local.io.core.input <> translation.io.output
  local.io.bus.translation <> translation.io.bus

  local.io.core.output <> extension.io.fromNoc.input
}

case class Axi4Node(nocConfig: Axi4Config, coreConfig: Axi4Config) extends Component {
  val io = new Bundle {
    val core = new Bundle {
      val input = slave(Axi4(coreConfig))
      val output = master(Axi4(coreConfig))
    }
    val north = new Bundle {
      val input = slave(Axi4(nocConfig))
      val output = master(Axi4(nocConfig))
    }
    val east = new Bundle {
      val input = slave(Axi4(nocConfig))
      val output = master(Axi4(nocConfig))
    }
    val south = new Bundle {
      val input = slave(Axi4(nocConfig))
      val output = master(Axi4(nocConfig))
    }
    val west = new Bundle {
      val input = slave(Axi4(nocConfig))
      val output = master(Axi4(nocConfig))
    }
    val bus = slave(Apb3(apb3Config))
  }

  val router = Axi4Router(nocConfig, apb3Config)
  router.io.north <> io.north
  router.io.east <> io.east
  router.io.south <> io.south
  router.io.west <> io.west

  val local = Axi4LocalPort(nocConfig, coreConfig, apb3Config)
  local.io.core <> io.core
  local.io.local <> router.io.local
  local.io.bus.router <> router.io.bus
}
