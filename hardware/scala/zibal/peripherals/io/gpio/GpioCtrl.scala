package zibal.peripherals.io.gpio

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc.BusSlaveFactory
import spinal.lib.misc.InterruptCtrl
import spinal.lib.io.{TriStateArray, TriState}


object GpioCtrl {
  def apply(p: Parameter = Parameter.default) = GpioCtrl(p)

  case class Parameter(
    width: Int,
    readBufferDepth: Int = 0,
    var output: Seq[Int] = null,
    var input: Seq[Int] = null,
    var interrupt: Seq[Int] = null
  ) {
    if (output == null)
      output = (0 until width)
    if (input == null)
      input = (0 until width)
    if (interrupt == null)
      interrupt = (0 until width)
  }
  object Parameter {
    def default = Parameter(32, 1, null, null, null)
  }

  case class Config(p: Parameter) extends Bundle {
    val write = Bits(p.width bits)
    val direction = Bits(p.width bits)
  }
  case class EnableConfig(p: Parameter) extends Bundle {
    val high = Bits(p.width bits)
    val low = Bits(p.width bits)
    val rise = Bits(p.width bits)
    val fall = Bits(p.width bits)
  }
  case class InterruptConfig(p: Parameter) extends Bundle {
    val valid = out(Bits(p.width bits))
    val pending = in(Bits(p.width bits))
  }

  case class Io(p: Parameter) extends Bundle {
    val gpio = Gpio.Io(p)
    val config = in(Config(p))
    val value = out(Bits(p.width bits))
    /* TODO: enable is unused */
    val enable = in(EnableConfig(p))
    val interrupt = out(Bool)
    val irqHigh = InterruptConfig(p)
    val irqLow = InterruptConfig(p)
    val irqRise = InterruptConfig(p)
    val irqFall = InterruptConfig(p)
  }

  case class GpioCtrl(p: Parameter) extends Component {
    val io = Io(p)

    if (p.readBufferDepth > 0) {
      io.value := BufferCC(io.gpio.pins.read, bufferDepth = p.readBufferDepth)
    } else {
      io.value := io.gpio.pins.read
    }
    io.gpio.pins.write := io.config.write
    io.gpio.pins.writeEnable := io.config.direction

    val synchronized = BufferCC(io.gpio.pins.read)
    val last = RegNext(synchronized)

    io.irqHigh.valid := synchronized
    io.irqLow.valid := ~synchronized
    io.irqRise.valid := (synchronized & ~last)
    io.irqFall.valid := (~synchronized & last)

    io.interrupt := (io.irqHigh.pending | io.irqLow.pending |
                    io.irqRise.pending | io.irqFall.pending).orR
  }

  case class Mapper(
    busCtrl: BusSlaveFactory,
    ctrl: Io,
    p: Parameter
  ) extends Area {

    for (i <- 0 until p.width) {
      if (p.input.contains(i))
        busCtrl.read(ctrl.value(i), 0x00, i)
      if (p.output.contains(i)) {
        busCtrl.driveAndRead(ctrl.config.write(i), 0x04, i) init(False)
      } else {
        busCtrl.read(False, 0x04, i)
        ctrl.config.write(i) := False
      }
      if (p.output.contains(i) && p.input.contains(i)) {
        busCtrl.driveAndRead(ctrl.config.direction(i), 0x08, i) init(False)
      } else {
        val direction = RegInit(Bool(p.output.contains(i)))
        direction.allowUnsetRegToAvoidLatch
        busCtrl.read(direction, 0x08, i)
        ctrl.config.direction(i) := direction
      }
    }

    val interrupt = new Area {

      val irqHighCtrl = new InterruptCtrl(p.width)
      irqHighCtrl.driveFrom(busCtrl, 0x10)
      val irqLowCtrl = new InterruptCtrl(p.width)
      irqLowCtrl.driveFrom(busCtrl, 0x18)
      val irqRiseCtrl = new InterruptCtrl(p.width)
      irqRiseCtrl.driveFrom(busCtrl, 0x20)
      val irqFallCtrl = new InterruptCtrl(p.width)
      irqFallCtrl.driveFrom(busCtrl, 0x28)

      for (i <- 0 until p.width) {
        if (p.interrupt.contains(i)) {
          irqHighCtrl.io.inputs(i) := ctrl.irqHigh.valid(i)
          irqLowCtrl.io.inputs(i) := ctrl.irqLow.valid(i)
          irqRiseCtrl.io.inputs(i) := ctrl.irqRise.valid(i)
          irqFallCtrl.io.inputs(i) := ctrl.irqFall.valid(i)
        } else {
          irqHighCtrl.io.inputs(i) := False
          irqLowCtrl.io.inputs(i) := False
          irqRiseCtrl.io.inputs(i) := False
          irqFallCtrl.io.inputs(i) := False
        }
      }

      ctrl.irqHigh.pending := irqHighCtrl.io.pendings
      ctrl.irqLow.pending := irqLowCtrl.io.pendings
      ctrl.irqRise.pending := irqRiseCtrl.io.pendings
      ctrl.irqFall.pending := irqFallCtrl.io.pendings

    }
  }
}
