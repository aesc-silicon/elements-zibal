package nafarr.peripherals.com.i2c

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.avalon._
import spinal.lib.bus.wishbone._

object I2cController {
  case class Cmd(p: I2cCtrl.Parameter) extends Bundle {
    val data = Bits(8 bits)
    val read = Bool
    val start = Bool
    val stop = Bool
    val ack = Bool
  }

  case class Rsp(p: I2cCtrl.Parameter) extends Bundle {
    val data = Bits(8 bits)
    val error = Bool
  }

  class Core[T <: spinal.core.Data with IMasterSlave](
      p: I2cCtrl.Parameter,
      busType: HardType[T],
      factory: T => BusSlaveFactory
  ) extends Component {
    val io = new Bundle {
      val bus = slave(busType())
      val i2c = master(I2c.Io(p))
      val interrupt = out(Bool)
    }

    val i2cControllerCtrl = I2cControllerCtrl(p)
    i2cControllerCtrl.io.i2c <> io.i2c
    io.interrupt := i2cControllerCtrl.io.interrupt

    val mapper = I2cControllerCtrl.Mapper(factory(io.bus), i2cControllerCtrl.io, p)

    val clockSpeed = ClockDomain.current.frequency.getValue.toInt
    def deviceTreeZephyr(name: String, address: BigInt, size: BigInt, irqNumber: Int = -1) = {
      val baseAddress = "%x".format(address.toInt)
      val regSize = "%04x".format(size.toInt)
      var dt = s"""
\t\t$name: $name@$baseAddress {
\t\t\tcompatible = "elements,i2c";
\t\t\treg = <0x$baseAddress 0x$regSize>;
\t\t\tstatus = "okay";"""
      if (irqNumber > 0) {
        dt += s"""
\t\t\tinterrupt-parent = <&plic>;
\t\t\tinterrupts = <$irqNumber 1>;"""
      }
      dt += s"""
\t\t\tinput-frequency = <$clockSpeed>;
\t\t\tclock-frequency = <100000>;
\t\t};"""
      dt
    }
    def headerBareMetal(name: String, address: BigInt, size: BigInt, irqNumber: Int = -1) = {
      val baseAddress = "%08x".format(address.toInt)
      val regSize = "%04x".format(size.toInt)
      var dt = s"""#define ${name.toUpperCase}_BASE\t\t0x${baseAddress}
#define ${name.toUpperCase}_FREQ\t\t${clockSpeed}
"""
      dt
    }
  }
}

case class Apb3I2cController(
    parameter: I2cCtrl.Parameter,
    busConfig: Apb3Config = Apb3Config(12, 32)
) extends I2cController.Core[Apb3](
      parameter,
      Apb3(busConfig),
      Apb3SlaveFactory(_)
    ) { val dummy = 0 }

case class WishboneI2cController(
    parameter: I2cCtrl.Parameter,
    busConfig: WishboneConfig = WishboneConfig(12, 32)
) extends I2cController.Core[Wishbone](
      parameter,
      Wishbone(busConfig),
      WishboneSlaveFactory(_)
    ) { val dummy = 0 }

case class AvalonMMI2cController(
    parameter: I2cCtrl.Parameter,
    busConfig: AvalonMMConfig = AvalonMMConfig.fixed(12, 32, 1)
) extends I2cController.Core[AvalonMM](
      parameter,
      AvalonMM(busConfig),
      AvalonMMSlaveFactory(_)
    ) { val dummy = 0 }
