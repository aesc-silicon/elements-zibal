package nafarr.networkonchip.amba4.axi

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba3.apb._

case class Axi4MemoryTranslation(
    config: Axi4Config,
    apb3Config: Apb3Config,
    lookupEntries: Int = 16,
    physicalWidth: Int = 52,
    virtualWidth: Int = 52
) extends Component {
  val io = new Bundle {
    val input = slave(Axi4(config))
    val output = master(Axi4(config))
    val bus = slave(Apb3(apb3Config))
  }

  /*
   * "Page types: page size / translated from [64:x] down
   * 4kB:  12 / 52 bit
   * 64kB: 16 / 52 bit
   * 2MB:  21 / 43 bit
   * 4MB:  22 / 42 bit
   * 1GB:  30 / 34 bit
   * 1TB:  40 / 24 bit
   * 1 Chiplet: 54 / 10 bit
   */
  val supportedPages = 7
  val pageTypeWidth = log2Up(supportedPages - 1)

  val lookup =
    Vec(Reg(MemoryTranslationLookupRow(physicalWidth, virtualWidth, supportedPages)), lookupEntries)
  for (index <- 0 until lookupEntries) {
    lookup(index).physical.init(B(physicalWidth bits, default -> false))
    lookup(index).virtual.init(B(virtualWidth bits, default -> false))
    lookup(index).pageType.init(U(0, pageTypeWidth bits))
    lookup(index).valid.init(False)
  }
  val locked = Reg(Bool()).init(False)

  val busInterface = new Area {
    val factory = Apb3SlaveFactory(io.bus)

    factory.read(locked, 0x0, 0)
    factory.read(U(lookupEntries, log2Up(lookupEntries) + 1 bits).asBits, 0x0, 8)
    factory.read(U(supportedPages, pageTypeWidth + 1 bits).asBits, 0x0, 16)
    for (index <- 0 until lookupEntries) {
      factory.read(lookup(index).physical(31 downto 0), 0x4 + (index * 0x14))
      factory.read(lookup(index).physical(physicalWidth - 1 downto 32), 0x4 + (index * 0x14) + 0x4)
      factory.read(lookup(index).virtual(31 downto 0), 0x4 + (index * 0x14) + 0x8)
      factory.read(lookup(index).virtual(physicalWidth - 1 downto 32), 0x4 + (index * 0x14) + 0xc)
      factory.read(lookup(index).valid, 0x4 + (index * 0x14) + 0x10, 0)
      factory.read(lookup(index).pageType, 0x4 + (index * 0x14) + 0x10, 8)
    }

    val tmpLookup =
      Vec(MemoryTranslationLookupRow(physicalWidth, virtualWidth, supportedPages), lookupEntries)
    for (index <- 0 until lookupEntries) {
      tmpLookup(index).physical := lookup(index).physical
      tmpLookup(index).virtual := lookup(index).virtual
      tmpLookup(index).pageType := lookup(index).pageType
      tmpLookup(index).valid := True

      factory.write(tmpLookup(index).physical(31 downto 0), 0x4 + (index * 0x14))
      factory.write(
        tmpLookup(index).physical(physicalWidth - 1 downto 32),
        0x4 + (index * 0x14) + 0x4
      )
      factory.write(tmpLookup(index).virtual(31 downto 0), 0x4 + (index * 0x14) + 0x8)
      factory.write(
        tmpLookup(index).virtual(virtualWidth - 1 downto 32),
        0x4 + (index * 0x14) + 0xc
      )
      factory.write(tmpLookup(index).pageType, 0x4 + (index * 0x14) + 0x10, 8)

      val realIndex = index * 0x14
      factory.onWrite(0x4 + realIndex + 0x00)(when(!locked) { lookup(index) := tmpLookup(index) })
      factory.onWrite(0x4 + realIndex + 0x04)(when(!locked) { lookup(index) := tmpLookup(index) })
      factory.onWrite(0x4 + realIndex + 0x08)(when(!locked) { lookup(index) := tmpLookup(index) })
      factory.onWrite(0x4 + realIndex + 0x0c)(when(!locked) { lookup(index) := tmpLookup(index) })
      factory.onWrite(0x4 + realIndex + 0x10)(when(!locked) { lookup(index) := tmpLookup(index) })
    }

    when(!locked) {
      factory.write(locked, 0x0, 0)
    }
  }

  val arLookup = new Area {
    val physical = io.input.ar.addr.asBits
    val virtualAddress = Reg(Bits(64 bits))
    virtualAddress := io.input.ar.addr.asBits

    val pAddr = io.input.ar.addr
    for (index <- 0 until lookupEntries) {
      when(lookup(index).valid) {
        val page4k = lookup(index).physical(63 - 12 downto 12 - 12) === physical(63 downto 12)
        val page64k = lookup(index).physical(63 - 12 downto 16 - 12) === physical(63 downto 16)
        val page2m = lookup(index).physical(63 - 12 downto 21 - 12) === physical(63 downto 21)
        val page4m = lookup(index).physical(63 - 12 downto 22 - 12) === physical(63 downto 22)
        val page1g = lookup(index).physical(63 - 12 downto 30 - 12) === physical(63 downto 30)
        val page1t = lookup(index).physical(63 - 12 downto 40 - 12) === physical(63 downto 40)
        val pageId = lookup(index).physical(63 - 12 downto 54 - 12) === physical(63 downto 54)

        when(lookup(index).pageType === U(0) && page4k) {
          virtualAddress := lookup(index).virtual(63 - 12 downto 12 - 12) ## pAddr(11 downto 0)
        } elsewhen (lookup(index).pageType === U(1) && page64k) {
          virtualAddress := lookup(index).virtual(63 - 12 downto 16 - 12) ## pAddr(15 downto 0)
        } elsewhen (lookup(index).pageType === U(2) && page2m) {
          virtualAddress := lookup(index).virtual(63 - 12 downto 21 - 12) ## pAddr(20 downto 0)
        } elsewhen (lookup(index).pageType === U(3) && page4m) {
          virtualAddress := lookup(index).virtual(63 - 12 downto 22 - 12) ## pAddr(21 downto 0)
        } elsewhen (lookup(index).pageType === U(4) && page1g) {
          virtualAddress := lookup(index).virtual(63 - 12 downto 30 - 12) ## pAddr(29 downto 0)
        } elsewhen (lookup(index).pageType === U(5) && page1t) {
          virtualAddress := lookup(index).virtual(63 - 12 downto 40 - 12) ## pAddr(39 downto 0)
        } elsewhen (lookup(index).pageType === U(6) && pageId) {
          virtualAddress := lookup(index).virtual(63 - 12 downto 54 - 12) ## pAddr(53 downto 0)
        }
      }
    }
    io.output.ar.addr := virtualAddress.asUInt

    val valid = Reg(Bool()).init(False)
    valid := io.input.ar.valid
    when(valid && io.output.ar.ready) {
      valid := False
    }
    io.output.ar.valid := valid

    io.input.ar.ready := False
    when(valid) {
      io.input.ar.ready := io.output.ar.ready
    }
  }
  io.output.ar.id := io.input.ar.id
  if (config.useRegion) {
    io.output.ar.region <> io.input.ar.region
  }
  if (config.useLen) {
    io.output.ar.len <> io.input.ar.len
  }
  if (config.useSize) {
    io.output.ar.size <> io.input.ar.size
  }
  if (config.useBurst) {
    io.output.ar.burst <> io.input.ar.burst
  }
  if (config.useLock) {
    io.output.ar.lock <> io.input.ar.lock
  }
  if (config.useCache) {
    io.output.ar.cache <> io.input.ar.cache
  }
  if (config.useQos) {
    io.output.ar.qos <> io.input.ar.qos
  }
  if (config.arUserWidth > 0) {
    io.output.ar.user <> io.input.ar.user
  }
  if (config.useProt) {
    io.output.ar.prot <> io.input.ar.prot
  }

  val awLookup = new Area {
    val physical = io.input.aw.addr.asBits
    val virtualAddress = Reg(Bits(64 bits))
    virtualAddress := io.input.aw.addr.asBits

    val pAddr = io.input.aw.addr
    for (index <- 0 until lookupEntries) {
      when(lookup(index).valid) {
        val page4k = lookup(index).physical(63 - 12 downto 12 - 12) === physical(63 downto 12)
        val page64k = lookup(index).physical(63 - 12 downto 16 - 12) === physical(63 downto 16)
        val page2m = lookup(index).physical(63 - 12 downto 21 - 12) === physical(63 downto 21)
        val page4m = lookup(index).physical(63 - 12 downto 22 - 12) === physical(63 downto 22)
        val page1g = lookup(index).physical(63 - 12 downto 30 - 12) === physical(63 downto 30)
        val page1t = lookup(index).physical(63 - 12 downto 40 - 12) === physical(63 downto 40)
        val pageId = lookup(index).physical(63 - 12 downto 54 - 12) === physical(63 downto 54)

        when(lookup(index).pageType === U(0) && page4k) {
          virtualAddress := lookup(index).virtual(63 - 12 downto 12 - 12) ## pAddr(11 downto 0)
        } elsewhen (lookup(index).pageType === U(1) && page64k) {
          virtualAddress := lookup(index).virtual(63 - 12 downto 16 - 12) ## pAddr(15 downto 0)
        } elsewhen (lookup(index).pageType === U(2) && page2m) {
          virtualAddress := lookup(index).virtual(63 - 12 downto 21 - 12) ## pAddr(20 downto 0)
        } elsewhen (lookup(index).pageType === U(3) && page4m) {
          virtualAddress := lookup(index).virtual(63 - 12 downto 22 - 12) ## pAddr(21 downto 0)
        } elsewhen (lookup(index).pageType === U(4) && page1g) {
          virtualAddress := lookup(index).virtual(63 - 12 downto 30 - 12) ## pAddr(29 downto 0)
        } elsewhen (lookup(index).pageType === U(5) && page1t) {
          virtualAddress := lookup(index).virtual(63 - 12 downto 40 - 12) ## pAddr(39 downto 0)
        } elsewhen (lookup(index).pageType === U(6) && pageId) {
          virtualAddress := lookup(index).virtual(63 - 12 downto 54 - 12) ## pAddr(53 downto 0)
        }
      }
    }
    io.output.aw.addr := virtualAddress.asUInt

    val valid = Reg(Bool()).init(False)
    valid := io.input.aw.valid
    when(valid && io.output.aw.ready) {
      valid := False
    }
    io.output.aw.valid := valid

    io.input.aw.ready := False
    when(valid) {
      io.input.aw.ready := io.output.aw.ready
    }
  }
  io.output.aw.id := io.input.aw.id
  if (config.useRegion) {
    io.output.aw.region <> io.input.aw.region
  }
  if (config.useLen) {
    io.output.aw.len <> io.input.aw.len
  }
  if (config.useSize) {
    io.output.aw.size <> io.input.aw.size
  }
  if (config.useBurst) {
    io.output.aw.burst <> io.input.aw.burst
  }
  if (config.useLock) {
    io.output.aw.lock <> io.input.aw.lock
  }
  if (config.useCache) {
    io.output.aw.cache <> io.input.aw.cache
  }
  if (config.useQos) {
    io.output.aw.qos <> io.input.aw.qos
  }
  if (config.arUserWidth > 0) {
    io.output.aw.user <> io.input.aw.user
  }
  if (config.useProt) {
    io.output.aw.prot <> io.input.aw.prot
  }

  /* Delay WVALID by at least one cycle to not overrun AW, which is delayed during lookup */
  val wDelay = new Area {
    val valid = Reg(Bool()).init(False)
    valid := io.input.w.valid
    when(valid && io.output.w.ready) {
      valid := False
    }
    io.output.w.valid := valid

    io.input.w.ready := False
    when(valid) {
      io.input.w.ready := io.output.w.ready
    }
  }
  io.output.w.data := io.input.w.data
  if (config.useStrb) {
    io.output.w.strb := io.input.w.strb
  }
  if (config.useWUser) {
    io.output.w.user := io.input.w.user
  }
  if (config.useLast) {
    io.output.w.last := io.input.w.last
  }

  io.input.r <> io.output.r
  io.input.b <> io.output.b
}
