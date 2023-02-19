package nafarr.peripherals.com.uart

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.avalon._
import spinal.lib.bus.wishbone._

object Uart {
  case class Io(p: UartCtrl.Parameter) extends Bundle with IMasterSlave {
    val txd = Bool
    val rxd = Bool
    val cts = Bool
    val rts = Bool

    override def asMaster(): Unit = {
      out(txd)
      in(rxd)
      in(cts)
      out(rts)
    }
    override def asSlave(): Unit = {
      in(txd)
      out(rxd)
      out(cts)
      in(rts)
    }
  }

  object ParityType extends SpinalEnum(binarySequential) {
    val NONE, EVEN, ODD = newElement()
  }

  object StopType extends SpinalEnum(binarySequential) {
    val ONE, TWO = newElement()
    def toBitCount(that: C): UInt = (that === ONE) ? U"0" | U"1"
  }

  class Core[T <: spinal.core.Data with IMasterSlave](
      p: UartCtrl.Parameter,
      busType: HardType[T],
      factory: T => BusSlaveFactory
  ) extends Component {
    val io = new Bundle {
      val bus = slave(busType())
      val uart = master(Io(p))
      val interrupt = out(Bool)
    }

    val ctrl = UartCtrl(p)
    ctrl.io.uart <> io.uart
    io.interrupt := ctrl.io.interrupt

    val mapper = UartCtrl.Mapper(factory(io.bus), ctrl.io, p)

    val clockSpeed = ClockDomain.current.frequency.getValue.toInt
    def deviceTreeZephyr(name: String, address: BigInt, size: BigInt, irqNumber: Int = -1) = {
      val baseAddress = "%x".format(address.toInt)
      val regSize = "%04x".format(size.toInt)
      val baudrate = this.p.init.baudrate
      var dt = s"""
\t\t$name: $name@$baseAddress {
\t\t\tcompatible = "elements,uart";
\t\t\treg = <0x$baseAddress 0x$regSize>;
\t\t\tstatus = "okay";"""
      if (irqNumber > 0) {
        dt += s"""
\t\t\tinterrupt-parent = <&plic>;
\t\t\tinterrupts = <$irqNumber 1>;"""
      }
      dt += s"""
\t\t\tclock-frequency = <$clockSpeed>;
\t\t\tcurrent-speed = <$baudrate>;
\t\t};"""
      dt
    }
    def headerBareMetal(name: String, address: BigInt, size: BigInt, irqNumber: Int = -1) = {
      val baseAddress = "%08x".format(address.toInt)
      val regSize = "%04x".format(size.toInt)
      var dt = s"""#define ${name.toUpperCase}_BASE\t\t0x${baseAddress}
#define ${name.toUpperCase}_FREQ\t\t${clockSpeed}
#define ${name.toUpperCase}_BAUD\t\t${this.p.init.baudrate}
"""
      dt
    }
  }
}

case class Apb3Uart(
    parameter: UartCtrl.Parameter,
    busConfig: Apb3Config = Apb3Config(12, 32)
) extends Uart.Core[Apb3](
      parameter,
      Apb3(busConfig),
      Apb3SlaveFactory(_)
    ) { val dummy = 0 }

case class WishboneUart(
    parameter: UartCtrl.Parameter,
    busConfig: WishboneConfig = WishboneConfig(12, 32)
) extends Uart.Core[Wishbone](
      parameter,
      Wishbone(busConfig),
      WishboneSlaveFactory(_)
    ) { val dummy = 0 }

case class AvalonMMUart(
    parameter: UartCtrl.Parameter,
    busConfig: AvalonMMConfig = AvalonMMConfig.fixed(12, 32, 1)
) extends Uart.Core[AvalonMM](
      parameter,
      AvalonMM(busConfig),
      AvalonMMSlaveFactory(_)
    ) { val dummy = 0 }
