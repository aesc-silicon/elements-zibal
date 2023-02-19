package nafarr.peripherals.com.i2c

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc.BusSlaveFactory
import spinal.lib.misc.InterruptCtrl

object I2cControllerCtrl {
  def apply(p: I2cCtrl.Parameter = I2cCtrl.Parameter.default) = I2cControllerCtrl(p)

  object State extends SpinalEnum {
    val IDLE, START, SENDDATA, SENDACK, RECVDATA, RECVACK, STOP = newElement()
  }
  object Samples extends SpinalEnum {
    val FIRST, SECOND, THIRD, FOURTH = newElement()
  }

  case class Config(p: I2cCtrl.Parameter) extends Bundle {
    val config = Bits(31 bits)
    val clockDivider = UInt(p.timerWidth bits)
  }

  case class Io(p: I2cCtrl.Parameter) extends Bundle {
    val config = in(Config(p))
    val i2c = master(I2c.Io(p))
    val interrupt = out(Bool)
    val pendingInterrupts = in(Bits(2 bits))
    val cmd = slave(Stream(I2cController.Cmd(p)))
    val rsp = master(Stream(I2cController.Rsp(p)))
  }

  case class I2cControllerCtrl(p: I2cCtrl.Parameter) extends Component {
    val io = Io(p)
    val ctrlEnable = RegInit(True)

    val ctrl = new ClockEnableArea(ctrlEnable) {

      val clockDivider = new Area {
        val counter = Reg(UInt(p.timerWidth bits)).init(2)
        val tick = counter === 0

        counter := counter - 1
        when(tick) {
          counter := io.config.clockDivider
        }
      }

      val tickCounter = new Area {
        val value = Reg(UInt(2 bits)).init(0)
        def reset() = value := 0
        when(clockDivider.tick) {
          value := value + 1
        }
      }

      val dataCounter = new Area {
        val value = Reg(UInt(3 bits)).init(7)
        def reset() = value := 7
        def next() = value := value - 1
        def isLast() = value === 7
      }

      val stateMachine = new Area {
        val state = RegInit(State.IDLE)
        def firstState(cmd: I2cController.Cmd): Unit = {
          when(cmd.start) {
            state := State.START
          } otherwise {
            when(cmd.read) {
              state := State.RECVDATA
            } otherwise {
              state := State.SENDDATA
            }
          }
        }
        def skipIdle(valid: Bool, cmd: I2cController.Cmd): Unit = {
          when(valid) {
            firstState(cmd)
          } otherwise {
            state := State.IDLE
          }
        }
        val samples = RegInit(Samples.FIRST)
        val sclWrite = Reg(Bool).init(False)
        val sdaWrite = Reg(Bool).init(False)

        val ack = Reg(Bool).init(False)
        val hasStop = Reg(Bool).init(False)

        val rspValid = RegNext(False).init(False)
        val error = Reg(Bool)
        val data = Reg(Bits(8 bits))

        io.cmd.ready := False
        switch(state) {
          is(State.IDLE) {
            when(io.cmd.valid && clockDivider.tick) {
              tickCounter.reset()
              dataCounter.reset()
              firstState(io.cmd.payload)
            }
          }
          is(State.START) {
            when(clockDivider.tick) {
              when(tickCounter.value === 1) {
                sclWrite := False
              }
              when(tickCounter.value === 2) {
                sdaWrite := True
              }
              when(tickCounter.value === 3) {
                sclWrite := True
                state := State.SENDDATA
              }
            }
          }
          is(State.RECVDATA) {
            when(clockDivider.tick) {
              when(tickCounter.value === 0) {
                sdaWrite := False
                hasStop := io.cmd.payload.stop
              }
              when(tickCounter.value === 1) {
                sclWrite := False
              }
              when(tickCounter.value === 2) {
                data(dataCounter.value) := io.i2c.sda.read
                dataCounter.next()
              }
              when(tickCounter.value === 3) {
                sclWrite := True
                when(dataCounter.isLast()) {
                  state := State.RECVACK
                  error := False
                  rspValid := True
                }
              }
            }
          }
          is(State.RECVACK) {
            when(clockDivider.tick) {
              when(tickCounter.value === 0) {
                /* Do not ack when FIFO is full */
                sdaWrite := io.cmd.payload.ack & io.rsp.ready
              }
              when(tickCounter.value === 1) {
                io.cmd.ready := True
                sclWrite := False
              }
              when(tickCounter.value === 3) {
                sclWrite := True
                when(hasStop) {
                  state := State.STOP
                } otherwise {
                  skipIdle(io.cmd.valid, io.cmd.payload)
                }
              }
            }
          }
          is(State.SENDDATA) {
            when(clockDivider.tick) {
              when(tickCounter.value === 0) {
                hasStop := io.cmd.payload.stop
                sdaWrite := !io.cmd.payload.data(dataCounter.value)
              }
              when(tickCounter.value === 1) {
                sclWrite := False
                dataCounter.next()
              }
              when(tickCounter.value === 3) {
                sclWrite := True
                when(dataCounter.isLast()) {
                  state := State.SENDACK
                  io.cmd.ready := True
                }
              }
            }
          }
          is(State.SENDACK) {
            when(clockDivider.tick) {
              when(tickCounter.value === 0) {
                sdaWrite := False
              }
              when(tickCounter.value === 1) {
                sclWrite := False
              }
              when(tickCounter.value === 2) {
                error := io.i2c.sda.read
                data := 0
                rspValid := True
              }
              when(tickCounter.value === 3) {
                sclWrite := True
                when(hasStop) {
                  state := State.STOP
                } otherwise {
                  skipIdle(io.cmd.valid, io.cmd.payload)
                }
              }
            }
          }
          is(State.STOP) {
            when(clockDivider.tick) {
              when(tickCounter.value === 0) {
                sdaWrite := True
              }
              when(tickCounter.value === 1) {
                sclWrite := False
              }
              when(tickCounter.value === 2) {
                sdaWrite := False
              }
              when(tickCounter.value === 3) {
                skipIdle(io.cmd.valid, io.cmd.payload)
              }
            }
          }
        }
      }
    }
    when(io.cmd.valid || !(ctrl.stateMachine.state === State.IDLE)) {
      ctrlEnable := True
    } otherwise {
      ctrlEnable := False
    }

    io.rsp.valid := ctrl.stateMachine.rspValid
    io.rsp.payload.data := ctrl.stateMachine.data
    io.rsp.payload.error := ctrl.stateMachine.error

    io.i2c.scl.write := ctrl.stateMachine.sclWrite
    io.i2c.sda.write := ctrl.stateMachine.sdaWrite

    io.interrupt := io.pendingInterrupts.orR
  }

  case class Mapper(
      busCtrl: BusSlaveFactory,
      ctrl: Io,
      p: I2cCtrl.Parameter
  ) extends Area {

    val config = new Area {
      val cfg = Reg(ctrl.config)

      busCtrl.drive(cfg.config, address = 0x08)

      if (p.permission.busCanWriteClockDividerConfig)
        busCtrl.writeMultiWord(cfg.clockDivider, address = 0x0c)
      else
        cfg.allowUnsetRegToAvoidLatch

      ctrl.config <> cfg
    }

    val cmdLogic = new Area {
      val streamUnbuffered = Stream(I2cController.Cmd(p))
      streamUnbuffered.valid := busCtrl.isWriting(address = 0x00)
      busCtrl.nonStopWrite(streamUnbuffered.data, bitOffset = 0)
      busCtrl.nonStopWrite(streamUnbuffered.start, bitOffset = 8)
      busCtrl.nonStopWrite(streamUnbuffered.stop, bitOffset = 9)
      busCtrl.nonStopWrite(streamUnbuffered.read, bitOffset = 10)
      busCtrl.nonStopWrite(streamUnbuffered.ack, bitOffset = 11)

      // busCtrl.createAndDriveFlow(I2cController.Cmd(p), address = 0x00).toStream
      val (stream, fifoAvailability) = streamUnbuffered.queueWithAvailability(p.memory.cmdFifoDepth)
      ctrl.cmd << stream
      busCtrl.read(fifoAvailability, address = 0x04, 16)
    }

    val rspLogic = new Area {
      val (stream, fifoOccupancy) = ctrl.rsp.queueWithOccupancy(p.memory.rspFifoDepth)
      busCtrl.readStreamNonBlocking(
        stream,
        address = 0x00,
        validBitOffset = 31,
        payloadBitOffset = 0
      )
      busCtrl.read(fifoOccupancy, address = 0x04, 0)
    }

    val interruptCtrl = new Area {
      val irqCtrl = new InterruptCtrl(2 + p.interrupts)
      irqCtrl.driveFrom(busCtrl, 0x10)
      irqCtrl.io.inputs(0) := !cmdLogic.stream.valid
      irqCtrl.io.inputs(1) := rspLogic.stream.valid
      for (i <- 0 until p.interrupts) {
        irqCtrl.io.inputs(2 + i) := ctrl.i2c.interrupts(i)
      }
      ctrl.pendingInterrupts := irqCtrl.io.pendings
    }
  }
}
