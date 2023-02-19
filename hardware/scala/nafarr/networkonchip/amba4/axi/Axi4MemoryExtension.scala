package nafarr.networkonchip.amba4.axi

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba3.apb._

case class Axi4MemoryExtension(
    config: Axi4Config,
    configExtended: Axi4Config,
    apb3Config: Apb3Config,
    lookupEntries: Int = 16,
    physicalWidth: Int = 4,
    virtualWidth: Int = 10
) extends Component {
  val io = new Bundle {
    val fromCore = new Bundle {
      val input = slave(Axi4(config))
      val output = master(Axi4(configExtended))
    }
    val fromNoc = new Bundle {
      val input = slave(Axi4(configExtended))
      val output = master(Axi4(config))
    }
    val bus = slave(Apb3(apb3Config))
  }

  val lookup = Vec(Reg(MemoryTranslationLookupRow(physicalWidth, virtualWidth)), lookupEntries)
  for (index <- 0 until lookupEntries) {
    lookup(index).physical.init(B(physicalWidth bits, default -> false))
    lookup(index).virtual.init(B(virtualWidth bits, default -> false))
    lookup(index).valid.init(False)
  }
  val locked = Reg(Bool()).init(False)

  val busInterface = new Area {
    val factory = Apb3SlaveFactory(io.bus)

    factory.read(locked, 0x0, 0)
    factory.read(U(lookupEntries, log2Up(lookupEntries) + 1 bits).asBits, 0x0, 8)
    for (index <- 0 until lookupEntries) {
      factory.read(lookup(index).physical, 0x4 + (index * 0x4), 0)
      factory.read(lookup(index).virtual, 0x4 + (index * 0x4), 0 + physicalWidth)
      factory.read(lookup(index).valid, 0x4 + (index * 0x4), 0 + physicalWidth + virtualWidth)
    }

    val tmpLookup = Vec(MemoryTranslationLookupRow(physicalWidth, virtualWidth), lookupEntries)
    for (index <- 0 until lookupEntries) {
      tmpLookup(index).physical := B(physicalWidth bits, default -> false)
      tmpLookup(index).virtual := B(virtualWidth bits, default -> false)
      tmpLookup(index).valid := True

      factory.write(tmpLookup(index).physical, 0x4 + (index * 0x4), 0)
      factory.write(tmpLookup(index).virtual, 0x4 + (index * 0x4), 0 + physicalWidth)

      factory.onWrite(0x4 + (index * 0x4)) {
        when(!locked) {
          lookup(index) := tmpLookup(index)
        }
      }
    }

    when(!locked) {
      factory.write(locked, 0x0, 0)
    }
  }

  val fromCore = new Area {
    val arLookup = new Area {
      val physical = io.fromCore.input.ar.addr(31 downto 28).asBits
      val virtual = B(10 bits, default -> false)
      val hit = False
      for (index <- 0 until lookupEntries) {
        when(lookup(index).physical === physical && lookup(index).valid) {
          hit := True
          virtual := lookup(index).virtual
        }
      }

      val pAddr = io.fromCore.input.ar.addr
      val virtualAddress = Reg(Bits(64 bits))
      // No hit: 000xx000yy0..0zz, Chiplet ID is x, y
      virtualAddress := B"000" ## pAddr(31 downto 30) ## B"000" ## pAddr(29 downto 28) ##
        B(26 bits, default -> false) ## pAddr(27 downto 0)
      when(hit && io.fromCore.input.ar.valid) {
        virtualAddress := virtual ## B(26 bits, default -> false) ## pAddr(27 downto 0).asBits
      }
      io.fromCore.output.ar.addr := virtualAddress.asUInt

      val valid = Reg(Bool()).init(False)
      valid := io.fromCore.input.ar.valid
      when(valid && io.fromCore.output.ar.ready) {
        valid := False
      }
      io.fromCore.output.ar.valid := valid

      io.fromCore.input.ar.ready := False
      when(valid) {
        io.fromCore.input.ar.ready := io.fromCore.output.ar.ready
      }
    }
    io.fromCore.output.ar.id := io.fromCore.input.ar.id
    if (config.useRegion) {
      io.fromCore.output.ar.region <> io.fromCore.input.ar.region
    }
    if (config.useLen) {
      io.fromCore.output.ar.len <> io.fromCore.input.ar.len
    }
    if (config.useSize) {
      io.fromCore.output.ar.size <> io.fromCore.input.ar.size
    }
    if (config.useBurst) {
      io.fromCore.output.ar.burst <> io.fromCore.input.ar.burst
    }
    if (config.useLock) {
      io.fromCore.output.ar.lock <> io.fromCore.input.ar.lock
    }
    if (config.useCache) {
      io.fromCore.output.ar.cache <> io.fromCore.input.ar.cache
    }
    if (config.useQos) {
      io.fromCore.output.ar.qos <> io.fromCore.input.ar.qos
    }
    if (config.arUserWidth > 0) {
      io.fromCore.output.ar.user <> io.fromCore.input.ar.user
    }
    if (config.useProt) {
      io.fromCore.output.ar.prot <> io.fromCore.input.ar.prot
    }

    val awLookup = new Area {
      val physical = io.fromCore.input.aw.addr(31 downto 28).asBits
      val virtual = B(10 bits, default -> false)
      val hit = False
      for (index <- 0 until lookupEntries) {
        when(lookup(index).physical === physical && lookup(index).valid) {
          hit := True
          virtual := lookup(index).virtual
        }
      }

      val pAddr = io.fromCore.input.aw.addr
      val virtualAddress = Reg(Bits(64 bits))
      // No hit: 000xx000yy0..0zz, Chiplet ID is x, y
      virtualAddress := B"000" ## pAddr(31 downto 30) ## B"000" ## pAddr(29 downto 28) ##
        B(26 bits, default -> false) ## pAddr(27 downto 0)
      when(hit && io.fromCore.input.aw.valid) {
        virtualAddress := virtual ## B(26 bits, default -> false) ## pAddr(27 downto 0).asBits
      }
      io.fromCore.output.aw.addr := virtualAddress.asUInt

      val valid = Reg(Bool()).init(False)
      valid := io.fromCore.input.aw.valid
      when(valid && io.fromCore.output.aw.ready) {
        valid := False
      }
      io.fromCore.output.aw.valid := valid

      io.fromCore.input.aw.ready := False
      when(valid) {
        io.fromCore.input.aw.ready := io.fromCore.output.aw.ready
      }
    }
    io.fromCore.output.aw.id := io.fromCore.input.aw.id
    if (config.useRegion) {
      io.fromCore.output.aw.region <> io.fromCore.input.aw.region
    }
    if (config.useLen) {
      io.fromCore.output.aw.len <> io.fromCore.input.aw.len
    }
    if (config.useSize) {
      io.fromCore.output.aw.size <> io.fromCore.input.aw.size
    }
    if (config.useBurst) {
      io.fromCore.output.aw.burst <> io.fromCore.input.aw.burst
    }
    if (config.useLock) {
      io.fromCore.output.aw.lock <> io.fromCore.input.aw.lock
    }
    if (config.useCache) {
      io.fromCore.output.aw.cache <> io.fromCore.input.aw.cache
    }
    if (config.useQos) {
      io.fromCore.output.aw.qos <> io.fromCore.input.aw.qos
    }
    if (config.awUserWidth > 0) {
      io.fromCore.output.aw.user <> io.fromCore.input.aw.user
    }
    if (config.useProt) {
      io.fromCore.output.aw.prot <> io.fromCore.input.aw.prot
    }

    /* Delay WVALID by at least one cycle to not overrun AW, which is delayed during lookup */
    val wDelay = new Area {
      val valid = Reg(Bool()).init(False)
      valid := io.fromCore.input.w.valid
      when(valid && io.fromCore.output.w.ready) {
        valid := False
      }
      io.fromCore.output.w.valid := valid

      io.fromCore.input.w.ready := False
      when(valid) {
        io.fromCore.input.w.ready := io.fromCore.output.w.ready
      }
    }
    io.fromCore.output.w.data := io.fromCore.input.w.data
    if (config.useStrb) {
      io.fromCore.output.w.strb := io.fromCore.input.w.strb
    }
    if (config.useWUser) {
      io.fromCore.output.w.user := io.fromCore.input.w.user
    }
    if (config.useLast) {
      io.fromCore.output.w.last := io.fromCore.input.w.last
    }

    io.fromCore.input.r <> io.fromCore.output.r
    io.fromCore.input.b <> io.fromCore.output.b
  }

  val fromNoc = new Area {
    val arLookup = new Area {
      val virtual = io.fromNoc.input.ar.addr(63 downto 54).asBits
      val physical = B(physicalWidth bits, default -> false)
      val hit = False
      for (index <- 0 until lookupEntries) {
        when(lookup(index).virtual === virtual && lookup(index).valid) {
          hit := True
          physical := lookup(index).physical
        }
      }

      val vAddr = io.fromNoc.input.ar.addr
      val physicalAddress = Reg(Bits(32 bits))
      // No hit: 000xx000yy0..0zz, Chiplet ID is x, y
      physicalAddress := vAddr(60 downto 59) ## vAddr(55 downto 54) ## vAddr(27 downto 0)
      when(hit && io.fromNoc.input.ar.valid) {
        physicalAddress := physical ## vAddr(27 downto 0).asBits
      }
      io.fromNoc.output.ar.addr := physicalAddress.asUInt

      val valid = Reg(Bool()).init(False)
      valid := io.fromNoc.input.ar.valid
      when(valid && io.fromNoc.output.ar.ready) {
        valid := False
      }
      io.fromNoc.output.ar.valid := valid

      io.fromNoc.input.ar.ready := False
      when(valid) {
        io.fromNoc.input.ar.ready := io.fromNoc.output.ar.ready
      }
    }
    io.fromNoc.output.ar.id := io.fromNoc.input.ar.id
    if (config.useRegion) {
      io.fromNoc.output.ar.region <> io.fromNoc.input.ar.region
    }
    if (config.useLen) {
      io.fromNoc.output.ar.len <> io.fromNoc.input.ar.len
    }
    if (config.useSize) {
      io.fromNoc.output.ar.size <> io.fromNoc.input.ar.size
    }
    if (config.useBurst) {
      io.fromNoc.output.ar.burst <> io.fromNoc.input.ar.burst
    }
    if (config.useLock) {
      io.fromNoc.output.ar.lock <> io.fromNoc.input.ar.lock
    }
    if (config.useCache) {
      io.fromNoc.output.ar.cache <> io.fromNoc.input.ar.cache
    }
    if (config.useQos) {
      io.fromNoc.output.ar.qos <> io.fromNoc.input.ar.qos
    }
    if (config.arUserWidth > 0) {
      io.fromNoc.output.ar.user <> io.fromNoc.input.ar.user
    }
    if (config.useProt) {
      io.fromNoc.output.ar.prot <> io.fromNoc.input.ar.prot
    }

    val awLookup = new Area {
      val virtual = io.fromNoc.input.aw.addr(63 downto 54).asBits
      val physical = B(physicalWidth bits, default -> false)
      val hit = False
      for (index <- 0 until lookupEntries) {
        when(lookup(index).virtual === virtual && lookup(index).valid) {
          hit := True
          physical := lookup(index).physical
        }
      }

      val vAddr = io.fromNoc.input.aw.addr
      val physicalAddress = Reg(Bits(32 bits))
      // No hit: 000xx000yy0..0zz, Chiplet ID is x, y
      physicalAddress := vAddr(60 downto 59) ## vAddr(55 downto 54) ## vAddr(27 downto 0)
      when(hit && io.fromNoc.input.aw.valid) {
        physicalAddress := physical ## vAddr(27 downto 0).asBits
      }
      io.fromNoc.output.aw.addr := physicalAddress.asUInt

      val valid = Reg(Bool()).init(False)
      valid := io.fromNoc.input.aw.valid
      when(valid && io.fromNoc.output.aw.ready) {
        valid := False
      }
      io.fromNoc.output.aw.valid := valid

      io.fromNoc.input.aw.ready := False
      when(valid) {
        io.fromNoc.input.aw.ready := io.fromNoc.output.aw.ready
      }
    }
    io.fromNoc.output.aw.id := io.fromNoc.input.aw.id
    if (config.useRegion) {
      io.fromNoc.output.aw.region <> io.fromNoc.input.aw.region
    }
    if (config.useLen) {
      io.fromNoc.output.aw.len <> io.fromNoc.input.aw.len
    }
    if (config.useSize) {
      io.fromNoc.output.aw.size <> io.fromNoc.input.aw.size
    }
    if (config.useBurst) {
      io.fromNoc.output.aw.burst <> io.fromNoc.input.aw.burst
    }
    if (config.useLock) {
      io.fromNoc.output.aw.lock <> io.fromNoc.input.aw.lock
    }
    if (config.useCache) {
      io.fromNoc.output.aw.cache <> io.fromNoc.input.aw.cache
    }
    if (config.useQos) {
      io.fromNoc.output.aw.qos <> io.fromNoc.input.aw.qos
    }
    if (config.awUserWidth > 0) {
      io.fromNoc.output.aw.user <> io.fromNoc.input.aw.user
    }
    if (config.useProt) {
      io.fromNoc.output.aw.prot <> io.fromNoc.input.aw.prot
    }

    /* Delay WVALID by at least one cycle to not overrun AW, which is delayed during lookup */
    val wDelay = new Area {
      val valid = Reg(Bool()).init(False)
      valid := io.fromNoc.input.w.valid
      when(valid && io.fromNoc.output.w.ready) {
        valid := False
      }
      io.fromNoc.output.w.valid := valid

      io.fromNoc.input.w.ready := False
      when(valid) {
        io.fromNoc.input.w.ready := io.fromNoc.output.w.ready
      }
    }
    io.fromNoc.output.w.data := io.fromNoc.input.w.data
    if (config.useStrb) {
      io.fromNoc.output.w.strb := io.fromNoc.input.w.strb
    }
    if (config.useWUser) {
      io.fromNoc.output.w.user := io.fromNoc.input.w.user
    }
    if (config.useLast) {
      io.fromNoc.output.w.last := io.fromNoc.input.w.last
    }

    io.fromNoc.input.r <> io.fromNoc.output.r
    io.fromNoc.input.b <> io.fromNoc.output.b
  }
}
