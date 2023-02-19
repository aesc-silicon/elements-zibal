package nafarr.peripherals.com.spi

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.avalon._
import spinal.lib.bus.wishbone._

object SpiMaster {
  object CmdMode extends SpinalEnum(binarySequential) {
    val DATA, SS = newElement()
  }

  case class CmdData(p: SpiCtrl.Parameter) extends Bundle {
    val data = Bits(p.dataWidth bits)
    val read = Bool
  }

  case class CmdSs(p: SpiCtrl.Parameter) extends Bundle {
    val enable = Bool
    val index = UInt(log2Up(p.ssWidth) bits)
  }

  case class Cmd(p: SpiCtrl.Parameter) extends Bundle {
    val mode = CmdMode()
    val args = Bits(Math.max(widthOf(CmdData(p)), log2Up(p.ssWidth) + 1) bits)

    def isData = mode === CmdMode.DATA
    def argsData = {
      val ret = CmdData(p)
      ret.assignFromBits(args)
      ret
    }
    def argsSs = {
      val ret = CmdSs(p)
      ret.assignFromBits(args)
      ret
    }
  }

  class Core[T <: spinal.core.Data with IMasterSlave](
      p: SpiCtrl.Parameter,
      busType: HardType[T],
      factory: T => BusSlaveFactory
  ) extends Component {
    val io = new Bundle {
      val bus = slave(busType())
      val spi = master(Spi.Io(p))
      val interrupt = out(Bool)
    }

    val spiMasterCtrl = SpiMasterCtrl(p)
    spiMasterCtrl.io.spi <> io.spi
    io.interrupt := spiMasterCtrl.io.interrupt

    val busFactory = factory(io.bus)
    SpiMasterCtrl.Mapper(busFactory, spiMasterCtrl.io, p)
    SpiMasterCtrl.StreamMapper(busFactory, spiMasterCtrl.io, p)

    def deviceTreeZephyr(name: String, address: BigInt, size: BigInt, irqNumber: Int = -1) = {
      val baseAddress = "%x".format(address.toInt)
      val regSize = "%04x".format(size.toInt)
      var dt = s"""
\t\t$name: $name@$baseAddress {
\t\t\tcompatible = "elements,spi";
\t\t\treg = <0x$baseAddress 0x$regSize>;
\t\t\tstatus = "okay";"""
      if (irqNumber > 0) {
        dt += s"""
\t\t\tinterrupt-parent = <&plic>;
\t\t\tinterrupts = <$irqNumber 1>;"""
      }
      dt += s"""
\t\t};"""
      dt
    }
    def headerBareMetal(name: String, address: BigInt, size: BigInt, irqNumber: Int = -1) = {
      val baseAddress = "%08x".format(address.toInt)
      val regSize = "%04x".format(size.toInt)
      var dt = s"""#define ${name.toUpperCase}_BASE\t\t0x${baseAddress}\n"""
      dt
    }
  }
}

case class Apb3SpiMaster(
    parameter: SpiCtrl.Parameter,
    busConfig: Apb3Config = Apb3Config(12, 32)
) extends SpiMaster.Core[Apb3](
      parameter,
      Apb3(busConfig),
      Apb3SlaveFactory(_)
    ) { val dummy = 0 }

case class WishboneSpiMaster(
    parameter: SpiCtrl.Parameter,
    busConfig: WishboneConfig = WishboneConfig(12, 32)
) extends SpiMaster.Core[Wishbone](
      parameter,
      Wishbone(busConfig),
      WishboneSlaveFactory(_)
    ) { val dummy = 0 }

case class AvalonMMSpiMaster(
    parameter: SpiCtrl.Parameter,
    busConfig: AvalonMMConfig = AvalonMMConfig.fixed(12, 32, 1)
) extends SpiMaster.Core[AvalonMM](
      parameter,
      AvalonMM(busConfig),
      AvalonMMSlaveFactory(_)
    ) { val dummy = 0 }
