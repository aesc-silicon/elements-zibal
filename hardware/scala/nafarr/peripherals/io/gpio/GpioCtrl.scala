package nafarr.peripherals.io.gpio

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc.BusSlaveFactory
import spinal.lib.misc.InterruptCtrl
import spinal.lib.io.{TriStateArray, TriState}

object GpioCtrl {
  def apply(p: Parameter = Parameter.default) = GpioCtrl(p)

  /** Parameters for GPIO controller.
    *
    *  @param width bit width of the controller and therefore the number of GPIOs.
    *  @param readBufferDepth register depth for reading values. Disabled when 0. Defaults to 0.
    *  @param output list of pin numbers which can drive an output signal. Defaults to null.
    *  @param input list of pin numbers which can read ana input signal. Defaults to null
    *  @param interrupt list of pin numbers which are interrupt capable. Defaults to null.
    */
  case class Parameter(
      width: Int,
      readBufferDepth: Int = 0,
      var output: Seq[Int] = null,
      var input: Seq[Int] = null,
      var interrupt: Seq[Int] = null
  ) {
    require(width < 33, "Only up to 32 GPIOs are allowed.")
    if (output == null)
      output = (0 until width)
    if (input == null)
      input = (0 until width)
    if (interrupt == null)
      interrupt = (0 until width)
  }
  object Parameter {
    def default = Parameter(32, 1, null, null, null)
    def full(width: Int = 32) = Parameter(width, 1, null, null, null)
    def noInterrupt(width: Int = 32) = Parameter(width, 1, null, null, Seq[Int]())
    def onlyOutput(width: Int = 32) = Parameter(width, 0, null, Seq[Int](), Seq[Int]())
    def onlyInput(width: Int = 32) = Parameter(width, 0, Seq[Int](), null, null)
  }

  case class Config(p: Parameter) extends Bundle {
    val write = Bits(p.width bits)
    val direction = Bits(p.width bits)
  }
  case class InterruptConfig(p: Parameter) extends Bundle {
    val valid = out(Bits(p.width bits))
    val pending = in(Bits(p.width bits))
  }

  case class Io(p: Parameter) extends Bundle {
    val gpio = Gpio.Io(p)
    val config = in(Config(p))
    val value = out(Bits(p.width bits))
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

  /** Register mapping
    *
    * 0x0000|Rx: Input level of each pin. 0 when no input pin added.
    * 0x0004|RW: Output level of each pin. Always returns 0 when no output pin added.
    * 0x0008|RW: Direction of each pin. High means output, low input. Always return 0 when n
    *            output pin added.
    * 0x0010|RW: Input high interrupt pending.
    *            Returns pending interrupts for each pin during read.
    *            Clears interrupts during write.
    * 0x0014|RW: Input high interrupt mask.
    * 0x0018|RW: Input low interrupt pending.
    *            Returns pending interrupts for each pin during read.
    *            Clears interrupts during write.
    * 0x001C|RW: Input low interrupt mask.
    * 0x0020|RW: Input rising edge interrupt pending.
    *            Returns pending interrupts for each pin during read.
    *            Clears interrupts during write.
    * 0x0024|RW: Input rising edge interrupt mask.
    * 0x0028|RW: Input falling edge interrupt pending.
    *            Returns pending interrupts for each pin during read.
    *            Clears interrupts during write.
    * 0x002C|RW: Input falling edge interrupt mask.
    */
  case class Mapper(
      busCtrl: BusSlaveFactory,
      ctrl: Io,
      p: Parameter
  ) extends Area {

    for (i <- 0 until p.width) {
      if (p.input.contains(i))
        busCtrl.read(ctrl.value(i), 0x00, i)
      if (p.output.contains(i)) {
        busCtrl.driveAndRead(ctrl.config.write(i), 0x04, i).init(False)
      } else {
        busCtrl.read(False, 0x04, i)
        ctrl.config.write(i) := False
      }
      if (p.output.contains(i) && p.input.contains(i)) {
        busCtrl.driveAndRead(ctrl.config.direction(i), 0x08, i).init(False)
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
