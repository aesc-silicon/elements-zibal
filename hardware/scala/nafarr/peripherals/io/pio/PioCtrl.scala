package nafarr.peripherals.io.pio

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._
import spinal.lib.bus.misc.BusSlaveFactory
import spinal.lib.misc.InterruptCtrl
import spinal.lib.io.{TriStateArray, TriState}

object PioCtrl {
  def apply(parameter: Parameter = Parameter(1)) = PioCtrl(parameter)

  case class Parameter(
      width: Int,
      dataWidth: Int = 24,
      clockDividerWidth: Int = 20,
      commandFifoDepth: Int = 32,
      readFifoDepth: Int = 8,
      readBufferDepth: Int = 0
  ) {
    require(width < 8, "Only up to 8 pins are supported")
    val readWidth = 1
  }

  object CommandType extends SpinalEnum(binarySequential) {
    val HIGH, LOW, WAIT, READ, A0, A1, A2, A3, A4 = newElement()
  }

  case class CommandContainer(parameter: Parameter) extends Bundle {
    val command = CommandType()
    val pin = UInt(4 bits)
    val data = Bits(parameter.dataWidth bits)

    def getWidth = parameter.dataWidth + 4
  }

  case class ReadContainer(parameter: Parameter) extends Bundle {
    val result = Bits(parameter.readWidth bits)

    def getWidth = parameter.readWidth + 1
  }

  case class Config(parameter: Parameter) extends Bundle {
    val clockDivider = UInt(parameter.clockDividerWidth bits)
    val readDelay = UInt(8 bits)
  }

  case class Io(parameter: Parameter) extends Bundle {
    val pio = Pio.Io(parameter)
    val config = in(Config(parameter))
    val commands = slave(Stream(CommandContainer(parameter)))
    val read = master(Stream(ReadContainer(parameter)))
    val readIsFull = in(Bool)
  }

  case class PioCtrl(parameter: Parameter) extends Component {
    val io = Io(parameter)

    val value = Bits(parameter.width bits)
    if (parameter.readBufferDepth > 0) {
      value := BufferCC(io.pio.pins.read, bufferDepth = parameter.readBufferDepth)
    } else {
      value := io.pio.pins.read
    }

    val clockDivider = new Area {
      val counter = Reg(UInt(parameter.clockDividerWidth bits)).init(0)
      val tick = counter === 0
      def reset() = counter := io.config.clockDivider

      counter := counter - 1
      when(tick) {
        this.reset()
      }
    }

    val fsm = new StateMachine {
      val counter = Reg(UInt(parameter.dataWidth bits)).init(0)
      val write = Reg(Bits(parameter.width bits)).init(0)
      val direction = Reg(Bits(parameter.width bits)).init(0)
      io.commands.ready := False
      io.read.valid := False
      val pinNumber = io.commands.payload.pin.resize(log2Up(parameter.width))
      val readContainer = ReadContainer(parameter)
      readContainer.result := value(pinNumber).asBits
      io.read.payload := readContainer

      val stateIdle: State = new State with EntryPoint {
        whenIsActive {
          when(io.commands.valid) {
            switch(io.commands.payload.command) {
              is(CommandType.HIGH) {
                goto(stateHigh)
              }
              is(CommandType.LOW) {
                goto(stateLow)
              }
              is(CommandType.WAIT) {
                goto(stateWait)
              }
              is(CommandType.READ) {
                goto(stateRead)
              }
            }
          }
        }
      }
      val stateAck = new State {
        whenIsActive {
          io.commands.ready := True
          goto(stateIdle)
        }
      }
      val stateHigh = new State {
        whenIsActive {
          direction(pinNumber) := True
          write(pinNumber) := True
          goto(stateIdle)
        }
        onExit(io.commands.ready := True)
      }
      val stateLow = new State {
        whenIsActive {
          direction(pinNumber) := True
          write(pinNumber) := False
          goto(stateIdle)
        }
        onExit(io.commands.ready := True)
      }
      val stateWait = new State {
        onEntry {
          clockDivider.reset()
          counter := 0
        }
        whenIsActive {
          when(clockDivider.tick) {
            counter := counter + 1
          }
          when(counter.asBits === io.commands.payload.data) {
            goto(stateIdle)
          }
        }
        onExit(io.commands.ready := True)
      }
      val stateRead = new State {
        onEntry(counter := 0)
        whenIsActive {
          direction(pinNumber) := False
          counter := counter + 1
          when(counter.asBits === B(io.config.readDelay, parameter.dataWidth bits)) {
            io.read.valid := True
            goto(stateIdle)
          }
        }
        onExit(io.commands.ready := True)
      }
    }

    io.pio.pins.write := fsm.write
    io.pio.pins.writeEnable := fsm.direction
  }

  case class Mapper(
      busCtrl: BusSlaveFactory,
      ctrl: Io,
      parameter: Parameter
  ) extends Area {

    val tx = new Area {
      val streamUnbuffered = busCtrl
        .createAndDriveFlow(
          CommandContainer(parameter),
          address = 0x00
        )
        .toStream
      val (stream, fifoOccupancy) =
        streamUnbuffered.queueWithOccupancy(parameter.commandFifoDepth)
      busCtrl.read(
        parameter.commandFifoDepth - fifoOccupancy,
        address = 0x04,
        bitOffset = 16
      )
      ctrl.commands << stream
      streamUnbuffered.ready.allowPruning()
    }

    val rx = new Area {
      val (stream, fifoOccupancy) = ctrl.read.queueWithOccupancy(parameter.readFifoDepth)
      ctrl.readIsFull := fifoOccupancy >= parameter.readFifoDepth - 1
      busCtrl.readStreamNonBlocking(
        stream,
        address = 0x0,
        validBitOffset = 16,
        payloadBitOffset = 0
      )
      busCtrl.read(fifoOccupancy, address = 0x04, bitOffset = 24)
    }

    busCtrl
      .driveAndRead(ctrl.config.clockDivider, 0x08)
      .init(U(100, parameter.clockDividerWidth bits))
    busCtrl.driveAndRead(ctrl.config.readDelay, 0x0c).init(U(5, 8 bits))
  }
}
