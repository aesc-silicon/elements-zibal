package nafarr.peripherals.com.uart

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc.BusSlaveFactory
import spinal.lib.misc.InterruptCtrl

object UartCtrl {
  def apply(p: Parameter = Parameter.default) = UartCtrl(p)

  case class InitParameter(
      baudrate: Int = 0,
      dataLength: Int = 0,
      parity: Uart.ParityType.E = null,
      stop: Uart.StopType.E = null
  ) {
    def getBaudPeriod() = {
      1000000000 / baudrate
    }
  }
  object InitParameter {
    def default(baudrate: Int) =
      InitParameter(baudrate, 7, Uart.ParityType.NONE, Uart.StopType.ONE)
  }

  case class PermissionParameter(
      busCanWriteFrameConfig: Boolean,
      busCanWriteClockDividerConfig: Boolean,
      busCanWriteInterruptConfig: Boolean
  ) {
    require(busCanWriteFrameConfig)
    require(busCanWriteClockDividerConfig)
    require(busCanWriteInterruptConfig)
  }
  object PermissionParameter {
    def full = PermissionParameter(true, true, true)
    def restricted = PermissionParameter(false, false, false)
  }

  case class MemoryMappedParameter(
      txFifoDepth: Int,
      rxFifoDepth: Int
  ) {
    require(txFifoDepth > 0 && txFifoDepth < 256)
    require(rxFifoDepth > 0 && rxFifoDepth < 256)
  }
  object MemoryMappedParameter {
    def lightweight = MemoryMappedParameter(4, 4)
    def default = MemoryMappedParameter(16, 16)
    def full = MemoryMappedParameter(64, 64)
  }

  case class Parameter(
      permission: PermissionParameter,
      memory: MemoryMappedParameter, /* TODO: make memory optional */
      init: InitParameter = null,
      clockDividerWidth: Int = 20,
      dataWidthMax: Int = 9,
      dataWidthMin: Int = 5,
      preSamplingSize: Int = 1,
      samplingSize: Int = 5,
      postSamplingSize: Int = 2,
      interrupt: Boolean = true, /* TODO: make interrupts optional */
      flowControl: Boolean = true
  ) {
    require(dataWidthMax < 10 && dataWidthMax > 4)
    require(dataWidthMin < 10 && dataWidthMin > 4)
    require(preSamplingSize > 0)
    require(samplingSize > 0)
    require(postSamplingSize > 0)
    if ((samplingSize % 2) == 0)
      SpinalWarning(
        s"It's not nice to have a even samplingSize value at ${ScalaLocated.short} (because of the majority vote)"
      )

    val samplesPerBit = preSamplingSize + samplingSize + postSamplingSize
    def getClockDivider(baudrate: Int): Int = {
      return (ClockDomain.current.frequency.getValue / baudrate / samplesPerBit).toInt - 1
    }
  }
  object Parameter {
    def lightweight = Parameter(
      permission = PermissionParameter.full,
      memory = MemoryMappedParameter.lightweight,
      init = InitParameter.default(115200)
    )
    def default = Parameter(
      permission = PermissionParameter.full,
      memory = MemoryMappedParameter.default,
      init = InitParameter.default(115200)
    )
    def full = Parameter(
      permission = PermissionParameter.full,
      memory = MemoryMappedParameter.full,
      init = InitParameter.default(115200)
    )
  }

  case class Config(p: Parameter) extends Bundle {
    val clockDivider = UInt(p.clockDividerWidth bits)
  }
  case class FrameConfig(p: Parameter) extends Bundle {
    val parity = Uart.ParityType()
    val stop = Uart.StopType()
    val dataLength = UInt(log2Up(p.dataWidthMax) bits)
  }

  case class Io(p: Parameter) extends Bundle {
    val config = in(Config(p))
    val frameConfig = in(FrameConfig(p))
    val uart = master(Uart.Io(p))
    val interrupt = out(Bool)
    val pendingInterrupts = in(Bits(2 bits))
    val write = slave(Stream(Bits(p.dataWidthMax bits)))
    val read = master(Stream(Bits(p.dataWidthMax bits)))
    val readIsFull = in(Bool)
  }

  case class UartCtrl(p: Parameter) extends Component {
    val io = Io(p)

    // Clock divider used by RX and TX
    val clockDivider = new Area {
      val counter = Reg(UInt(p.clockDividerWidth bits)).init(0)
      val tick = counter === 0

      counter := counter - 1
      when(tick) {
        counter := io.config.clockDivider
      }
    }

    io.interrupt := io.pendingInterrupts.orR

    val tx = UartCtrlTx(p)
    tx.io.config <> io.frameConfig
    tx.io.samplingTick := clockDivider.tick
    tx.io.write << io.write
    io.uart.txd <> tx.io.txd

    val rx = UartCtrlRx(p)
    rx.io.config <> io.frameConfig
    rx.io.samplingTick := clockDivider.tick
    io.read << rx.io.read
    io.uart.rxd <> rx.io.rxd

    if (p.flowControl) {
      io.uart.rts := io.readIsFull
      tx.io.cts := !io.uart.cts
    } else {
      io.uart.rts := False
      tx.io.cts := True
    }
  }

  case class Mapper(
      busCtrl: BusSlaveFactory,
      ctrl: Io,
      p: Parameter
  ) extends Area {

    val config = new Area {
      val cfg = Reg(ctrl.config)

      if (p.init != null && p.init.baudrate != 0)
        cfg.clockDivider.init(p.getClockDivider(p.init.baudrate))
      else
        cfg.clockDivider.init(0)

      if (p.permission.busCanWriteClockDividerConfig)
        busCtrl.writeMultiWord(cfg.clockDivider, address = 0x08)
      else
        cfg.allowUnsetRegToAvoidLatch

      val frameCfg = Reg(ctrl.frameConfig)

      if (p.init != null && p.init.dataLength != 0)
        frameCfg.dataLength.init(p.init.dataLength)
      if (p.init != null && p.init.parity != null)
        frameCfg.parity.init(p.init.parity)
      if (p.init != null && p.init.stop != null)
        frameCfg.stop.init(p.init.stop)

      if (p.permission.busCanWriteFrameConfig) {
        busCtrl.write(frameCfg.dataLength, address = 0x0c, bitOffset = 0)
        busCtrl.write(frameCfg.parity, address = 0x0c, bitOffset = 8)
        busCtrl.write(frameCfg.stop, address = 0x0c, bitOffset = 16)
      } else {
        frameCfg.allowUnsetRegToAvoidLatch
      }

      ctrl.config <> cfg
      ctrl.frameConfig <> frameCfg
    }

    val tx = new Area {
      val streamUnbuffered = busCtrl
        .createAndDriveFlow(
          Bits(p.dataWidthMax bits),
          address = 0x00
        )
        .toStream
      val (stream, fifoOccupancy) =
        streamUnbuffered.queueWithOccupancy(p.memory.txFifoDepth)
      busCtrl.read(
        p.memory.txFifoDepth - fifoOccupancy,
        address = 0x04,
        bitOffset = 16
      )
      ctrl.write << stream
      streamUnbuffered.ready.allowPruning()
    }

    val rx = new Area {
      val (stream, fifoOccupancy) =
        ctrl.read.queueWithOccupancy(p.memory.rxFifoDepth)
      ctrl.readIsFull := fifoOccupancy >= p.memory.rxFifoDepth - 1
      busCtrl.readStreamNonBlocking(
        stream,
        address = 0x0,
        validBitOffset = 16,
        payloadBitOffset = 0
      )
      busCtrl.read(fifoOccupancy, address = 0x04, bitOffset = 24)
    }

    val interrupt = new Area {

      val irqCtrl = new InterruptCtrl(2)
      irqCtrl.driveFrom(busCtrl, 0x10)
      irqCtrl.io.inputs(0) := !ctrl.write.valid
      irqCtrl.io.inputs(1) := ctrl.read.valid
      ctrl.pendingInterrupts := irqCtrl.io.pendings

    }

  }
}
