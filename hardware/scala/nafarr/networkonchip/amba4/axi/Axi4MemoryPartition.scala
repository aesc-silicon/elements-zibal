package nafarr.networkonchip.amba4.axi

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba3.apb._

case class Axi4MemoryPartition(
    config: Axi4Config,
    apb3Config: Apb3Config,
    partitions: Int = 16,
    boundaryWidth: Int = 42
) extends Component {
  require(boundaryWidth == 42 || boundaryWidth == 20 || boundaryWidth == 10) /* TODO */
  val io = new Bundle {
    val input = slave(Axi4(config))
    val output = master(Axi4(config))
    val bus = slave(Apb3(apb3Config))
  }

  val lookup = Vec(Reg(MemoryPartitionRow(boundaryWidth)), partitions)
  for (index <- 0 until partitions) {
    lookup(index).lowerBoundary.init(U(0, boundaryWidth bits))
    lookup(index).upperBoundary.init(U(0, boundaryWidth bits))
    lookup(index).chipletId.init(U(0, 10 bits))
    lookup(index).rwxPermission.init(B(0, 3 bits))
    lookup(index).valid.init(False)
  }
  val locked = Reg(Bool()).init(False)
  val id = Reg(UInt(chipletIdWidth bits)).init(U(0, chipletIdWidth bits))

  val busInterface = new Area {
    val factory = Apb3SlaveFactory(io.bus)

    factory.read(locked, 0x0, 0)
    factory.read(U(partitions, log2Up(partitions) + 1 bits).asBits, 0x0, 8)
    factory.read(id, 0x0, 16)

    for (index <- 0 until partitions) {
      factory.read(lookup(index).lowerBoundary(31 downto 0), 0x4 + (index * 0x14))
      factory.read(
        lookup(index).lowerBoundary(boundaryWidth - 1 downto 32),
        0x4 + (index * 0x14) + 0x4
      )
      factory.read(lookup(index).upperBoundary(31 downto 0), 0x4 + (index * 0x14) + 0x8)
      factory.read(
        lookup(index).upperBoundary(boundaryWidth - 1 downto 32),
        0x4 + (index * 0x14) + 0xc
      )
      factory.read(lookup(index).valid, 0x4 + (index * 0x14) + 0x10, 0)
      factory.read(lookup(index).rwxPermission, 0x4 + (index * 0x14) + 0x10, 8)
      factory.read(lookup(index).chipletId, 4 + (index * 0x14) + 0x10, 16)
    }

    val tmpLookup = Vec(MemoryPartitionRow(boundaryWidth), partitions)
    for (index <- 0 until partitions) {
      tmpLookup(index).lowerBoundary := lookup(index).lowerBoundary
      tmpLookup(index).upperBoundary := lookup(index).upperBoundary
      tmpLookup(index).chipletId := lookup(index).chipletId
      tmpLookup(index).rwxPermission := lookup(index).rwxPermission
      tmpLookup(index).valid := True

      factory.write(tmpLookup(index).lowerBoundary(31 downto 0), 0x4 + (index * 0x14))
      factory.write(
        tmpLookup(index).lowerBoundary(boundaryWidth - 1 downto 32),
        0x4 + (index * 0x14) + 0x4
      )
      factory.write(tmpLookup(index).upperBoundary(31 downto 0), 0x4 + (index * 0x14) + 0x8)
      factory.write(
        tmpLookup(index).upperBoundary(boundaryWidth - 1 downto 32),
        0x4 + (index * 0x14) + 0xc
      )
      factory.write(tmpLookup(index).rwxPermission, 0x4 + (index * 0x14) + 0x10, 8)
      factory.write(tmpLookup(index).chipletId, 0x4 + (index * 0x14) + 0x10, 16)

      val realIndex = index * 0x14
      factory.onWrite(0x4 + realIndex + 0x00)(when(!locked) { lookup(index) := tmpLookup(index) })
      factory.onWrite(0x4 + realIndex + 0x04)(when(!locked) { lookup(index) := tmpLookup(index) })
      factory.onWrite(0x4 + realIndex + 0x08)(when(!locked) { lookup(index) := tmpLookup(index) })
      factory.onWrite(0x4 + realIndex + 0x0c)(when(!locked) { lookup(index) := tmpLookup(index) })
      factory.onWrite(0x4 + realIndex + 0x10)(when(!locked) { lookup(index) := tmpLookup(index) })
    }

    when(!locked) {
      factory.write(locked, 0x0, 0)
      factory.write(id, 0x0, 16)
    }
  }

  val readError = StreamFifo(PermissionError(config), 8)

  val readLookup = new Area {
    val chipletId = io.input.ar.id(config.idWidth - 1 downto config.idWidth - chipletIdWidth)
    val address = io.input.ar.addr(boundaryWidth + 12 - 1 downto 12)
    val approvals = Reg(Bits(partitions bits)).init(B(partitions bits, default -> False))

    for (index <- 0 until partitions) {
      /* Permission X is not implemented yet */
      approvals(index) := False
      when(io.input.ar.valid) {
        when(
          lookup(index).valid && lookup(index).chipletId === chipletId && lookup(index)
            .hasReadPermission()
        ) {
          when(address >= lookup(index).lowerBoundary && address < lookup(index).upperBoundary) {
            approvals(index) := True
          }
        }
      }
    }

    val validOutput = RegInit(False)
    val error = PermissionError(config)

    validOutput := False
    error.id := io.input.ar.id

    io.input.ar.ready := False
    readError.io.push.valid := False
    readError.io.push.payload := error
    val handler = new StateMachine {
      val idle: State = new State with EntryPoint {
        whenIsActive {
          when(io.input.ar.valid) {
            goto(decide)
          }
        }
      }
      // Wait one cycle for register "approval"
      val decide: State = new State {
        whenIsActive {
          when(approvals.orR) {
            validOutput := True
            goto(approve)
          } otherwise {
            goto(decline)
          }
        }
      }
      val approve: State = new State {
        whenIsActive {
          io.input.ar.ready := io.output.ar.ready
          validOutput := True
          when(io.output.ar.ready) {
            validOutput := False
            goto(idle)
          }
        }
      }
      val decline: State = new State {
        whenIsActive {
          readError.io.push.valid := True
          when(readError.io.push.ready) {
            io.input.ar.ready := True
            goto(idle)
          }
        }
      }
    }
  }
  io.output.ar.valid := readLookup.validOutput
  io.output.ar.addr <> io.input.ar.addr
  io.output.ar.id <> io.input.ar.id
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

  val readResponse = new StateMachine {
    readError.io.pop.ready := False

    io.input.r.valid := False
    io.output.r.ready := False
    io.input.r.data := io.output.r.data
    io.input.r.id := U(0, config.idWidth bits)
    io.input.r.resp := Axi4.resp.OKAY
    io.input.r.last := False
    io.input.r.user := io.output.r.user
    val idle: State = new State with EntryPoint {
      whenIsActive {
        when(readError.io.pop.valid) {
          goto(publishError)
        } elsewhen (io.output.r.valid) {
          goto(forwardResponse)
        }
      }
    }
    val publishError: State = new State {
      whenIsActive {
        io.input.r.valid := True
        io.input.r.id := (id ## readError.io.pop.payload.id(3 downto 0)).asUInt
        io.input.r.user := readError.io.pop.payload.id(13 downto 4).asBits
        io.input.r.resp := Axi4.resp.DECERR
        io.input.r.last := True
        when(io.input.r.ready) {
          readError.io.pop.ready := True
          goto(idle)
        }
      }
    }
    val forwardResponse: State = new State {
      whenIsActive {
        io.input.r <> io.output.r
        when(io.output.r.valid && io.output.r.last && io.input.r.ready) {
          goto(idle)
        }
      }
    }
  }

  val writeApprovals = StreamFifo(WriteApproval(), 8)
  val writeError = StreamFifo(PermissionError(config), 8)

  val writeLookup = new Area {
    val chipletId = io.input.aw.id(config.idWidth - 1 downto config.idWidth - chipletIdWidth)
    val address = io.input.aw.addr(boundaryWidth + 12 - 1 downto 12)
    val approvals = Reg(Bits(partitions bits)).init(B(partitions bits, default -> False))

    for (index <- 0 until partitions) {
      when(io.input.aw.valid) {
        approvals(index) := False
        when(
          lookup(index).valid && lookup(index).chipletId === chipletId && lookup(index)
            .hasWritePermission()
        ) {
          when(address >= lookup(index).lowerBoundary && address < lookup(index).upperBoundary) {
            approvals(index) := True
          }
        }
      }
    }

    val validOutput = RegInit(False)
    val writeApproval = WriteApproval()
    val error = PermissionError(config)
    val approvedForReady = RegInit(False)

    validOutput := False
    writeApproval.approved := False
    error.id := io.input.aw.id

    io.input.aw.ready := False
    writeApprovals.io.push.valid := False
    writeApprovals.io.push.payload := writeApproval
    writeError.io.push.valid := False
    writeError.io.push.payload := error
    val handler = new StateMachine {
      val idle: State = new State with EntryPoint {
        onEntry(approvedForReady := False)
        whenIsActive {
          when(io.input.aw.valid) {
            goto(decide)
          }
        }
      }
      // Wait one cycle for register "approval"
      val decide: State = new State {
        whenIsActive {
          when(approvals.orR) {
            validOutput := True
            goto(approve)
          } otherwise {
            goto(decline)
          }
        }
      }
      val approve: State = new State {
        whenIsActive {
          io.input.aw.ready := io.output.aw.ready
          validOutput := True
          when(io.output.aw.ready) {
            validOutput := False
            writeApproval.approved := True
            approvedForReady := True
            writeApprovals.io.push.valid := True
            when(writeApprovals.io.push.ready) {
              goto(idle)
            } otherwise {
              goto(waitForReady)
            }
          }
        }
      }
      val waitForReady: State = new State {
        whenIsActive {
          writeApproval.approved := approvedForReady
          writeApprovals.io.push.valid := True
          when(writeApprovals.io.push.ready) {
            goto(idle)
          }
        }
      }
      val decline: State = new State {
        whenIsActive {
          writeError.io.push.valid := True
          when(writeError.io.push.ready) {
            io.input.aw.ready := True
            approvedForReady := False
            writeApprovals.io.push.valid := True
            when(writeApprovals.io.push.ready) {
              goto(idle)
            } otherwise {
              goto(waitForReady)
            }
          }
        }
      }
    }
  }
  io.output.aw.valid := writeLookup.validOutput
  io.output.aw.addr <> io.input.aw.addr
  io.output.aw.id <> io.input.aw.id
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
  if (config.awUserWidth > 0) {
    io.output.aw.user <> io.input.aw.user
  }
  if (config.useProt) {
    io.output.aw.prot <> io.input.aw.prot
  }

  val writeForwared = new StateMachine {
    writeApprovals.io.pop.ready := False

    io.output.w.valid := False
    io.input.w.ready := False
    io.output.w.data := io.input.w.data
    if (config.useStrb) {
      io.output.w.strb := io.input.w.strb
    }
    if (config.useWUser) {
      io.output.w.user := io.input.w.user
    }
    io.output.w.last := io.input.w.last
    val idle: State = new State with EntryPoint {
      whenIsActive {
        when(io.input.w.valid && writeApprovals.io.pop.valid) {
          writeApprovals.io.pop.ready := True
          when(writeApprovals.io.pop.payload.approved) {
            goto(forward)
          } otherwise {
            goto(block)
          }
        }
      }
    }
    val forward: State = new State {
      whenIsActive {
        io.output.w <> io.input.w
        when(io.input.w.valid && io.output.w.ready && io.input.w.last) {
          goto(idle)
        }
      }
    }
    val block: State = new State {
      whenIsActive {
        io.input.w.ready := True
        when(io.input.w.valid && io.input.w.last) {
          goto(idle)
        }
      }
    }
  }

  val writeResponse = new StateMachine {
    writeError.io.pop.ready := False

    io.input.b.valid := False
    io.output.b.ready := False
    io.input.b.id := U(0, config.idWidth bits)
    io.input.b.resp := Axi4.resp.OKAY
    io.input.b.user := io.output.b.user
    val idle: State = new State with EntryPoint {
      whenIsActive {
        when(writeError.io.pop.valid) {
          goto(publishError)
        } elsewhen (io.output.b.valid) {
          goto(forwardResponse)
        }
      }
    }
    val publishError: State = new State {
      whenIsActive {
        io.input.b.valid := True
        io.input.b.id := (id ## writeError.io.pop.payload.id(3 downto 0)).asUInt
        io.input.b.user := writeError.io.pop.payload.id(13 downto 4).asBits
        io.input.b.resp := Axi4.resp.DECERR
        when(io.input.b.ready) {
          writeError.io.pop.ready := True
          goto(idle)
        }
      }
    }
    val forwardResponse: State = new State {
      whenIsActive {
        io.input.b <> io.output.b
        when(io.output.b.valid && io.input.b.ready) {
          goto(idle)
        }
      }
    }
  }
}
