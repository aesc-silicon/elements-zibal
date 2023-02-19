package nafarr.networkonchip.amba4.axi

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba3.apb._

case class InputBuffer(config: Axi4Config, inputDepth: Int, outputDepth: Int) extends Component {
  val io = new Bundle {
    val input = slave(Axi4(config))
    val ar = master(Stream(Axi4Ar(config)))
    val aw = master(Stream(Axi4Aw(config)))
    val w = master(Stream(Axi4W(config)))
    val r = slave(Stream(Axi4R(config)))
    val b = slave(Stream(Axi4B(config)))
  }

  val ar = new Area {
    val fifo = StreamFifo(Axi4Ar(config), inputDepth)
    fifo.io.push.payload := io.input.ar.payload
    fifo.io.push.valid := io.input.ar.valid
    io.input.ar.ready := fifo.io.push.ready

    io.ar << fifo.io.pop
  }

  val aw = new Area {
    val fifo = StreamFifo(Axi4Aw(config), inputDepth)
    fifo.io.push.payload := io.input.aw.payload
    fifo.io.push.valid := io.input.aw.valid
    io.input.aw.ready := fifo.io.push.ready

    io.aw << fifo.io.pop
  }

  val w = new Area {
    val fifo = StreamFifo(Axi4W(config), inputDepth)
    fifo.io.push.payload := io.input.w.payload
    fifo.io.push.valid := io.input.w.valid
    io.input.w.ready := fifo.io.push.ready

    io.w << fifo.io.pop
  }

  val r = new Area {
    val fifo = StreamFifo(Axi4R(config), outputDepth)
    io.input.r.payload := fifo.io.pop.payload
    io.input.r.valid := fifo.io.pop.valid
    fifo.io.pop.ready := io.input.r.ready

    fifo.io.push << io.r
  }

  val b = new Area {
    val fifo = StreamFifo(Axi4B(config), outputDepth)
    io.input.b.payload := fifo.io.pop.payload
    io.input.b.valid := fifo.io.pop.valid
    fifo.io.pop.ready := io.input.b.ready

    fifo.io.push << io.b
  }

  def connect(port: Axi4) {
    port >> this.io.input
  }
}

case class OutputBuffer(config: Axi4Config, inputDepth: Int, outputDepth: Int) extends Component {
  val io = new Bundle {
    val output = master(Axi4(config))
    val ar = slave(Stream(Axi4Ar(config)))
    val aw = slave(Stream(Axi4Aw(config)))
    val w = slave(Stream(Axi4W(config)))
    val r = master(Stream(Axi4R(config)))
    val b = master(Stream(Axi4B(config)))
  }

  val ar = new Area {
    val fifo = StreamFifo(Axi4Ar(config), outputDepth)
    io.output.ar.payload := fifo.io.pop.payload
    io.output.ar.valid := fifo.io.pop.valid
    fifo.io.pop.ready := io.output.ar.ready

    fifo.io.push << io.ar
  }

  val aw = new Area {
    val fifo = StreamFifo(Axi4Aw(config), outputDepth)
    io.output.aw.payload := fifo.io.pop.payload
    io.output.aw.valid := fifo.io.pop.valid
    fifo.io.pop.ready := io.output.aw.ready

    fifo.io.push << io.aw
  }

  val w = new Area {
    val fifo = StreamFifo(Axi4W(config), outputDepth)
    io.output.w.payload := fifo.io.pop.payload
    io.output.w.valid := fifo.io.pop.valid
    fifo.io.pop.ready := io.output.w.ready

    fifo.io.push << io.w
  }

  val r = new Area {
    val fifo = StreamFifo(Axi4R(config), inputDepth)
    fifo.io.push.payload := io.output.r.payload
    fifo.io.push.valid := io.output.r.valid
    io.output.r.ready := fifo.io.push.ready

    io.r << fifo.io.pop
  }

  val b = new Area {
    val fifo = StreamFifo(Axi4B(config), inputDepth)
    fifo.io.push.payload := io.output.b.payload
    fifo.io.push.valid := io.output.b.valid
    io.output.b.ready := fifo.io.push.ready

    io.b << fifo.io.pop
  }

  def connect(port: Axi4) {
    this.io.output >> port
  }
}

object SourcePort extends SpinalEnum() {
  val NONE, LOCAL, NORTH, EAST, SOUTH, WEST = newElement()
}

case class RoutingDecoderLogic[T <: spinal.core.Bundle](
    busType: HardType[T],
    config: Axi4Config,
    addUnroutableError: Boolean = false
) extends Component {
  val io = new Bundle {
    val directions = in(RoutingDirections())
    val chipletId = in(UInt(chipletIdWidth bits))
    val destinationId = in(UInt(chipletIdWidth bits))
    val input = slave(Stream(busType()))
    val outputs = Vec(master(Stream(busType())), 5)
    val flipDirection = in(Bool())
    val error = if (addUnroutableError) master(Stream(PermissionError(config))) else null
    val sourceId = if (addUnroutableError) in(UInt(chipletIdWidth bits)) else null
  }

  val errors = StreamFifo(PermissionError(config), 8)
  if (addUnroutableError) {
    val payload = PermissionError(config)
    payload.id := (io.sourceId ## B"0000").asUInt
    errors.io.push.valid := False
    errors.io.push.payload := payload
    io.error << errors.io.pop
  } else {
    errors.io.push.valid := False
    errors.io.pop.ready := False
  }

  io.input.ready := False
  for (index <- 0 until 5) {
    io.outputs(index).payload := io.input.payload
    io.outputs(index).valid := False
  }

  val destination = new Area {
    val id = io.destinationId
    val x = id(9 downto 5)
    val y = id(4 downto 0)
  }
  val chiplet = new Area {
    val id = io.chipletId
    val x = id(9 downto 5)
    val y = id(4 downto 0)
  }
  val routeLocal = io.directions.isDisabled || chiplet.id === destination.id
  val routeNorth = io.directions.northEnabled && (destination.y > chiplet.y)
  val routeEast = io.directions.eastEnabled && (destination.x > chiplet.x)
  val routeSouth = io.directions.southEnabled && (destination.y < chiplet.y)
  val routeWest = io.directions.westEnabled && (destination.x < chiplet.x)

  def connectHandshake(index: Int) {
    io.outputs(index).valid := io.input.valid
    io.input.ready := io.outputs(index).ready
  }

  val flip = RegInit(False)
  when(io.flipDirection) {
    flip := !flip
  }
  when(io.input.valid) {
    when(routeLocal) {
      connectHandshake(0)
    } elsewhen (routeNorth && routeEast) {
      when(flip) {
        connectHandshake(1)
      } otherwise {
        connectHandshake(2)
      }
    } elsewhen (routeEast && routeSouth) {
      when(flip) {
        connectHandshake(2)
      } otherwise {
        connectHandshake(3)
      }
    } elsewhen (routeSouth && routeWest) {
      when(flip) {
        connectHandshake(3)
      } otherwise {
        connectHandshake(4)
      }
    } elsewhen (routeWest && routeNorth) {
      when(flip) {
        connectHandshake(4)
      } otherwise {
        connectHandshake(1)
      }
    } elsewhen (routeNorth) {
      connectHandshake(1)
    } elsewhen (routeEast) {
      connectHandshake(2)
    } elsewhen (routeSouth) {
      connectHandshake(3)
    } elsewhen (routeWest) {
      connectHandshake(4)
    } otherwise {
      /* Is unroutable. Send DECERR as response */
      if (addUnroutableError) {
        errors.io.push.valid := True
        when(errors.io.push.ready) {
          io.input.ready := True
        }
      } else {
        io.input.ready := True
      }
    }
  }
}

case class RoutingArbiterLogic[T <: spinal.core.Bundle](
    busType: HardType[T],
    hasLast: Boolean = false
) extends Component {
  val io = new Bundle {
    val inputs = Vec(slave(Stream(busType())), 5)
    val output = master(Stream(busType()))
  }

  for (index <- 0 until 5) {
    io.inputs(index).ready := False
  }
  io.output.valid := False
  io.output.payload := io.inputs(0).payload

  val portLock = Reg(SourcePort()).init(SourcePort.NONE)
  def portLockLogic(index: Int) {
    io.output <> io.inputs(index)
    if (hasLast) {
      val payload = io.inputs(index).payload
      val last =
        if (payload.isInstanceOf[Axi4W]) payload.asInstanceOf[Axi4W].last
        else payload.asInstanceOf[Axi4R].last
      when(io.inputs(index).ready && last) {
        portLock := SourcePort.NONE
      }
    } else {
      when(io.inputs(index).ready) {
        portLock := SourcePort.NONE
      }
    }
  }
  when(portLock === SourcePort.NONE) {
    when(io.inputs(0).valid) {
      portLock := SourcePort.LOCAL
    } elsewhen (io.inputs(1).valid) {
      portLock := SourcePort.NORTH
    } elsewhen (io.inputs(2).valid) {
      portLock := SourcePort.EAST
    } elsewhen (io.inputs(3).valid) {
      portLock := SourcePort.SOUTH
    } elsewhen (io.inputs(4).valid) {
      portLock := SourcePort.WEST
    }
  } otherwise {
    when(portLock === SourcePort.LOCAL) {
      portLockLogic(0)
    } elsewhen (portLock === SourcePort.NORTH) {
      portLockLogic(1)
    } elsewhen (portLock === SourcePort.EAST) {
      portLockLogic(2)
    } elsewhen (portLock === SourcePort.SOUTH) {
      portLockLogic(3)
    } elsewhen (portLock === SourcePort.WEST) {
      portLockLogic(4)
    }
  }
}

// Ordering: 0=local, 1=north, 2=east, 3=south, 4=west
case class RoutingDecoder(config: Axi4Config) extends Component {
  val io = new Bundle {
    val directions = in(RoutingDirections())
    val chipletId = in(UInt(chipletIdWidth bits))
    val ar = new Bundle {
      val input = slave(Stream(Axi4Ar(config)))
      val outputs = Vec(master(Stream(Axi4Ar(config))), 5)
    }
    val r = new Bundle {
      val inputs = Vec(slave(Stream(Axi4R(config))), 5)
      val output = master(Stream(Axi4R(config)))
    }
    val aw = new Bundle {
      val input = slave(Stream(Axi4Aw(config)))
      val outputs = Vec(master(Stream(Axi4Aw(config))), 5)
    }
    val w = new Bundle {
      val input = slave(Stream(Axi4W(config)))
      val outputs = Vec(master(Stream(Axi4W(config))), 5)
    }
    val b = new Bundle {
      val inputs = Vec(slave(Stream(Axi4B(config))), 5)
      val output = master(Stream(Axi4B(config)))
    }
  }

  val ar = new Area {
    val logic = new RoutingDecoderLogic[Axi4Ar](Axi4Ar(config), config, true)
    logic.io.directions := io.directions
    logic.io.chipletId := io.chipletId
    logic.io.destinationId := io.ar.input.addr(63 downto 64 - chipletIdWidth)
    logic.io.input << io.ar.input
    io.ar.outputs <> logic.io.outputs
    logic.io.flipDirection := (io.ar.input.valid && io.ar.input.ready)
    logic.io.sourceId := io.ar.input.id(13 downto 4)
  }

  val r = new Area {
    val logic = new RoutingArbiterLogic[Axi4R](Axi4R(config), true)
    io.r.inputs <> logic.io.inputs

    val response = new StateMachine {
      ar.logic.io.error.ready := False

      logic.io.output.ready := False
      io.r.output.valid := False
      io.r.output.payload <> logic.io.output.payload
      val idle: State = new State with EntryPoint {
        whenIsActive {
          when(ar.logic.io.error.valid) {
            goto(publishError)
          } elsewhen (logic.io.output.valid) {
            goto(forwardResponse)
          }
        }
      }
      val publishError: State = new State {
        whenIsActive {
          io.r.output.valid := True
          io.r.output.id := (io.chipletId ## ar.logic.io.error.payload.id(3 downto 0)).asUInt
          io.r.output.user := ar.logic.io.error.payload.id(13 downto 4).asBits
          io.r.output.resp := Axi4.resp.DECERR
          io.r.output.last := True
          when(io.r.output.ready) {
            ar.logic.io.error.ready := True
            logic.io.output.ready := True
            goto(idle)
          }
        }
      }
      val forwardResponse: State = new State {
        whenIsActive {
          io.r.output << logic.io.output
          when(io.r.output.valid && io.r.output.last && io.r.output.ready) {
            goto(idle)
          }
        }
      }
    }
  }

  val aw = new Area {
    val logic = new RoutingDecoderLogic[Axi4Aw](Axi4Aw(config), config, true)
    logic.io.directions := io.directions
    logic.io.chipletId := io.chipletId
    logic.io.destinationId := io.aw.input.addr(63 downto 64 - chipletIdWidth)
    logic.io.input << io.aw.input
    io.aw.outputs <> logic.io.outputs
    logic.io.flipDirection := (io.aw.input.valid && io.aw.input.ready)
    logic.io.sourceId := io.aw.input.id(13 downto 4)
  }

  val w = new Area {
    val logic = new RoutingDecoderLogic[Axi4W](Axi4W(config), config)
    logic.io.directions := io.directions
    logic.io.chipletId := io.chipletId
    logic.io.destinationId := io.w.input.user(9 downto 0).asUInt
    logic.io.input << io.w.input
    io.w.outputs <> logic.io.outputs
    logic.io.flipDirection := (io.w.input.valid && io.w.input.ready && io.w.input.last)
  }

  val b = new Area {
    val logic = new RoutingArbiterLogic[Axi4B](Axi4B(config))
    io.b.inputs <> logic.io.inputs

    val response = new StateMachine {
      aw.logic.io.error.ready := False

      logic.io.output.ready := False
      io.b.output.valid := False
      io.b.output.payload <> logic.io.output.payload
      val idle: State = new State with EntryPoint {
        whenIsActive {
          when(aw.logic.io.error.valid) {
            goto(publishError)
          } elsewhen (logic.io.output.valid) {
            goto(forwardResponse)
          }
        }
      }
      val publishError: State = new State {
        whenIsActive {
          io.b.output.valid := True
          io.b.output.id := (io.chipletId ## aw.logic.io.error.payload.id(3 downto 0)).asUInt
          io.b.output.user := aw.logic.io.error.payload.id(13 downto 4).asBits
          io.b.output.resp := Axi4.resp.DECERR
          when(io.b.output.ready) {
            aw.logic.io.error.ready := True
            logic.io.output.ready := True
            goto(idle)
          }
        }
      }
      val forwardResponse: State = new State {
        whenIsActive {
          io.b.output << logic.io.output
          when(io.b.output.valid && io.b.output.ready) {
            goto(idle)
          }
        }
      }
    }
  }

  def connect(input: InputBuffer, directions: RoutingDirections, id: UInt) {
    this.io.directions <> directions
    this.io.chipletId <> id
    this.io.ar.input <> input.io.ar
    this.io.r.output <> input.io.r
    this.io.aw.input <> input.io.aw
    this.io.w.input <> input.io.w
    this.io.b.output <> input.io.b
  }
}

// Ordering: 0=local, 1=north, 2=east, 3=south, 4=west
case class RoutingArbiter(config: Axi4Config) extends Component {
  val io = new Bundle {
    val directions = in(RoutingDirections())
    val chipletId = in(UInt(chipletIdWidth bits))
    val ar = new Bundle {
      val inputs = Vec(slave(Stream(Axi4Ar(config))), 5)
      val output = master(Stream(Axi4Ar(config)))
    }
    val r = new Bundle {
      val input = slave(Stream(Axi4R(config)))
      val outputs = Vec(master(Stream(Axi4R(config))), 5)
    }
    val aw = new Bundle {
      val inputs = Vec(slave(Stream(Axi4Aw(config))), 5)
      val output = master(Stream(Axi4Aw(config)))
    }
    val w = new Bundle {
      val inputs = Vec(slave(Stream(Axi4W(config))), 5)
      val output = master(Stream(Axi4W(config)))
    }
    val b = new Bundle {
      val input = slave(Stream(Axi4B(config)))
      val outputs = Vec(master(Stream(Axi4B(config))), 5)
    }
  }

  val ar = new Area {
    val logic = new RoutingArbiterLogic[Axi4Ar](Axi4Ar(config))
    io.ar.output << logic.io.output
    io.ar.inputs <> logic.io.inputs
  }

  val r = new Area {
    val logic = new RoutingDecoderLogic[Axi4R](Axi4R(config), config)
    logic.io.directions := io.directions
    logic.io.chipletId := io.chipletId
    logic.io.destinationId := io.r.input.user(9 downto 0).asUInt
    logic.io.input << io.r.input
    io.r.outputs <> logic.io.outputs
    logic.io.flipDirection := (io.r.input.valid && io.r.input.ready && io.r.input.last)
  }

  val aw = new Area {
    val logic = new RoutingArbiterLogic[Axi4Aw](Axi4Aw(config))
    io.aw.output << logic.io.output
    io.aw.inputs <> logic.io.inputs
  }

  val w = new Area {
    val logic = new RoutingArbiterLogic[Axi4W](Axi4W(config), true)
    io.w.output << logic.io.output
    io.w.inputs <> logic.io.inputs
  }

  val b = new Area {
    val logic = new RoutingDecoderLogic[Axi4B](Axi4B(config), config)
    logic.io.directions := io.directions
    logic.io.chipletId := io.chipletId
    logic.io.destinationId := io.b.input.user(9 downto 0).asUInt
    logic.io.input << io.b.input
    io.b.outputs <> logic.io.outputs
    logic.io.flipDirection := (io.b.input.valid && io.b.input.ready)
  }

  def connect(
      output: OutputBuffer,
      directions: RoutingDirections,
      id: UInt,
      index: Int,
      r: Axi4Router
  ) {
    this.io.directions <> directions
    this.io.chipletId <> id
    this.io.ar.output <> output.io.ar
    this.io.r.input <> output.io.r
    this.io.aw.output <> output.io.aw
    this.io.w.output <> output.io.w
    this.io.b.input <> output.io.b
    for ((component, idx) <- Array(r.local, r.north, r.east, r.south, r.west).zipWithIndex) {
      this.io.ar.inputs(idx) << component.decoder.io.ar.outputs(index)
      this.io.r.outputs(idx) >> component.decoder.io.r.inputs(index)
      this.io.aw.inputs(idx) << component.decoder.io.aw.outputs(index)
      this.io.w.inputs(idx) << component.decoder.io.w.outputs(index)
      this.io.b.outputs(idx) >> component.decoder.io.b.inputs(index)
    }
  }
}

case class Axi4Router(
    config: Axi4Config,
    apb3Config: Apb3Config,
    inputDepth: Int = 4,
    outputDepth: Int = 4
) extends Component {
  require(inputDepth > 0)
  require(outputDepth > 0)

  val io = new Bundle {
    val local = new Bundle {
      val input = slave(Axi4(config))
      val output = master(Axi4(config))
    }
    val north = new Bundle {
      val input = slave(Axi4(config))
      val output = master(Axi4(config))
    }
    val east = new Bundle {
      val input = slave(Axi4(config))
      val output = master(Axi4(config))
    }
    val south = new Bundle {
      val input = slave(Axi4(config))
      val output = master(Axi4(config))
    }
    val west = new Bundle {
      val input = slave(Axi4(config))
      val output = master(Axi4(config))
    }
    val bus = slave(Apb3(apb3Config))
  }

  val locked = RegInit(False)
  val id = Reg(UInt(chipletIdWidth bits)).init(U(0, chipletIdWidth bits))
  val directions = Reg(RoutingDirections())
  directions.directions.init(B"0000")
  val busInterface = new Area {
    val factory = Apb3SlaveFactory(io.bus)

    factory.read(locked, 0x0)
    factory.read(id, 0x4, 0)
    factory.read(directions.directions, 0x4, 16)

    factory.onWrite(0x0) {
      locked := True
    }
    val tmpId = UInt(chipletIdWidth bits)
    val tmpDirection = RoutingDirections()
    tmpId := id
    tmpDirection := directions
    factory.write(tmpId, 0x4, 0)
    factory.write(tmpDirection.directions, 0x4, 16)
    factory.onWrite(0x4) {
      when(!locked) {
        id := tmpId
        directions := tmpDirection
      }
    }
  }

  val local = new Area {
    val input = InputBuffer(config, inputDepth, outputDepth)
    input.connect(io.local.input)
    val output = OutputBuffer(config, inputDepth, outputDepth)
    output.connect(io.local.output)
    val decoder = RoutingDecoder(config)
    decoder.connect(input, directions, id)
    val arbiter = RoutingArbiter(config)
  }

  val north = new Area {
    val input = InputBuffer(config, inputDepth, outputDepth)
    input.connect(io.north.input)
    val output = OutputBuffer(config, inputDepth, outputDepth)
    output.connect(io.north.output)
    val decoder = RoutingDecoder(config)
    decoder.connect(input, directions, id)
    val arbiter = RoutingArbiter(config)
  }

  val east = new Area {
    val input = InputBuffer(config, inputDepth, outputDepth)
    input.connect(io.east.input)
    val output = OutputBuffer(config, inputDepth, outputDepth)
    output.connect(io.east.output)
    val decoder = RoutingDecoder(config)
    decoder.connect(input, directions, id)
    val arbiter = RoutingArbiter(config)
  }

  val south = new Area {
    val input = InputBuffer(config, inputDepth, outputDepth)
    input.connect(io.south.input)
    val output = OutputBuffer(config, inputDepth, outputDepth)
    output.connect(io.south.output)
    val decoder = RoutingDecoder(config)
    decoder.connect(input, directions, id)
    val arbiter = RoutingArbiter(config)
  }

  val west = new Area {
    val input = InputBuffer(config, inputDepth, outputDepth)
    input.connect(io.west.input)
    val output = OutputBuffer(config, inputDepth, outputDepth)
    output.connect(io.west.output)
    val decoder = RoutingDecoder(config)
    decoder.connect(input, directions, id)
    val arbiter = RoutingArbiter(config)
  }

  for ((component, index) <- Array(local, north, east, south, west).zipWithIndex) {
    component.arbiter.connect(component.output, directions, id, index, this)
  }
}
