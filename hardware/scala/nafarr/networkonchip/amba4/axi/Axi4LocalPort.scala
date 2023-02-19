package nafarr.networkonchip.amba4.axi

import scala.collection.mutable.ArrayBuffer

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.misc.SizeMapping

case class Axi4LocalPort(
    nocConfig: Axi4Config,
    coreConfig: Axi4Config,
    apb3Config: Apb3Config,
    hasMemoryExtension: Boolean = false,
    hasMemoryTranslation: Boolean = false,
    partitionEntries: Int = 16,
    hasBlockage: Boolean = true
) extends Component {
  val io = new Bundle {
    val core = new Bundle {
      val input = slave(Axi4(coreConfig))
      val output = master(Axi4(coreConfig))
    }
    val local = new Bundle {
      val input = slave(Axi4(nocConfig))
      val output = master(Axi4(nocConfig))
    }
    val bus = new Bundle {
      val router = master(Apb3(apb3Config))
      val extension = if (hasMemoryExtension) master(Apb3(apb3Config)) else null
      val translation = if (hasMemoryTranslation) master(Apb3(apb3Config)) else null
    }
  }

  val apbMapping = ArrayBuffer[(Apb3, SizeMapping)]()

  val chipletId = new Axi4ChipletID(coreConfig, nocConfig, apb3Config)
  chipletId.io.fromCore.input <> io.core.input
  chipletId.io.fromNoc.output <> io.core.output

  val partition = new Axi4MemoryPartition(nocConfig, apb3Config, partitionEntries)
  partition.io.output <> chipletId.io.fromNoc.input

  if (hasBlockage) {
    val blockage = new Axi4Blockage(nocConfig)
    blockage.io.input <> chipletId.io.fromCore.output
    io.local.output <> blockage.io.output
    apbMapping += blockage.io.bus -> (0x30000, 4 KiB)
  } else {
    io.local.output <> chipletId.io.fromCore.output
  }

  apbMapping += chipletId.io.bus -> (0x10000, 4 KiB)
  apbMapping += partition.io.bus -> (0x20000, 4 KiB)
  apbMapping += io.bus.router -> (0x40000, 4 KiB)
  if (hasMemoryExtension) {
    apbMapping += io.bus.extension -> (0x50000, 4 KiB)
  }
  if (hasMemoryTranslation) {
    apbMapping += io.bus.translation -> (0x60000, 4 KiB)
  }

  val apbBus = new Area {
    val bridge = Axi4SharedToApb3Bridge(
      addressWidth = 20,
      dataWidth = 32,
      idWidth = nocConfig.idWidth
    )
    val config = bridge.axiConfig.copy(
      addressWidth = nocConfig.addressWidth,
      bUserWidth = nocConfig.bUserWidth,
      rUserWidth = nocConfig.rUserWidth
    )

    val decoder = Apb3Decoder(
      master = bridge.io.apb,
      slaves = apbMapping
    )
  }

  val apbInterconnect = new Area {

    val downsizer = Axi4Downsizer(nocConfig, apbBus.config)

    val localInput = Axi4(nocConfig)
    localInput.ar.valid <> io.local.input.ar.valid
    localInput.ar.ready <> io.local.input.ar.ready
    localInput.ar.addr := (B"0000000000" ## io.local.input.ar.addr(53 downto 0)).asUInt
    localInput.ar.id := io.local.input.ar.id
    if (nocConfig.useRegion) {
      localInput.ar.region <> io.local.input.ar.region
    }
    if (nocConfig.useLen) {
      localInput.ar.len <> io.local.input.ar.len
    }
    if (nocConfig.useSize) {
      localInput.ar.size <> io.local.input.ar.size
    }
    if (nocConfig.useBurst) {
      localInput.ar.burst <> io.local.input.ar.burst
    }
    if (nocConfig.useLock) {
      localInput.ar.lock <> io.local.input.ar.lock
    }
    if (nocConfig.useCache) {
      localInput.ar.cache <> io.local.input.ar.cache
    }
    if (nocConfig.useQos) {
      localInput.ar.qos <> io.local.input.ar.qos
    }
    if (nocConfig.arUserWidth > 0) {
      localInput.ar.user <> io.local.input.ar.user
    }
    if (nocConfig.useProt) {
      localInput.ar.prot <> io.local.input.ar.prot
    }

    localInput.aw.valid <> io.local.input.aw.valid
    localInput.aw.ready <> io.local.input.aw.ready
    localInput.aw.addr := (B"0000000000" ## io.local.input.aw.addr(53 downto 0)).asUInt
    localInput.aw.id := io.local.input.aw.id
    if (nocConfig.useRegion) {
      localInput.aw.region <> io.local.input.aw.region
    }
    if (nocConfig.useLen) {
      localInput.aw.len <> io.local.input.aw.len
    }
    if (nocConfig.useSize) {
      localInput.aw.size <> io.local.input.aw.size
    }
    if (nocConfig.useBurst) {
      localInput.aw.burst <> io.local.input.aw.burst
    }
    if (nocConfig.useLock) {
      localInput.aw.lock <> io.local.input.aw.lock
    }
    if (nocConfig.useCache) {
      localInput.aw.cache <> io.local.input.aw.cache
    }
    if (nocConfig.useQos) {
      localInput.aw.qos <> io.local.input.aw.qos
    }
    if (nocConfig.awUserWidth > 0) {
      localInput.aw.user <> io.local.input.aw.user
    }
    if (nocConfig.useProt) {
      localInput.aw.prot <> io.local.input.aw.prot
    }

    localInput.r <> io.local.input.r
    localInput.w <> io.local.input.w
    localInput.b <> io.local.input.b

    val axiCrossbar = Axi4CrossbarFactory()
    axiCrossbar.addSlave(partition.io.input, (0x00000000000000L, 128 MiB))
    axiCrossbar.addSlave(downsizer.io.input, (0x00000008000000L, 1 MiB))
    axiCrossbar.addConnections(
      localInput -> List(partition.io.input, downsizer.io.input)
    )
    axiCrossbar.build()

    val downsizerShared = downsizer.io.output.toShared()
    apbBus.bridge.io.axi.arw.write <> downsizerShared.arw.write
    apbBus.bridge.io.axi.arw.valid <> downsizerShared.arw.valid
    apbBus.bridge.io.axi.arw.ready <> downsizerShared.arw.ready
    apbBus.bridge.io.axi.arw.addr <> downsizerShared.arw.addr(19 downto 0)
    apbBus.bridge.io.axi.arw.id := downsizerShared.arw.id
    if (nocConfig.useLen) {
      apbBus.bridge.io.axi.arw.len <> downsizerShared.arw.len
    }
    if (nocConfig.useSize) {
      apbBus.bridge.io.axi.arw.size <> downsizerShared.arw.size
    }
    if (nocConfig.useBurst) {
      apbBus.bridge.io.axi.arw.burst <> downsizerShared.arw.burst
    }
    if (nocConfig.arwUserWidth > 0) {
      apbBus.bridge.io.axi.arw.user <> downsizerShared.arw.user
    }

    apbBus.bridge.io.axi.w <> downsizerShared.w
    apbBus.bridge.io.axi.r.valid <> downsizerShared.r.valid
    apbBus.bridge.io.axi.r.ready <> downsizerShared.r.ready
    apbBus.bridge.io.axi.r.data <> downsizerShared.r.data
    apbBus.bridge.io.axi.r.id <> downsizerShared.r.id
    apbBus.bridge.io.axi.r.resp <> downsizerShared.r.resp
    apbBus.bridge.io.axi.r.last <> downsizerShared.r.last

    apbBus.bridge.io.axi.b.valid <> downsizerShared.b.valid
    apbBus.bridge.io.axi.b.ready <> downsizerShared.b.ready
    apbBus.bridge.io.axi.b.id <> downsizerShared.b.id
    apbBus.bridge.io.axi.b.resp <> downsizerShared.b.resp

    downsizerShared.b.payload.user := B(0, nocConfig.bUserWidth bits)
    downsizerShared.r.payload.user := B(0, nocConfig.bUserWidth bits)
  }
}
