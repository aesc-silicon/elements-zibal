package zibal.peripherals.com.spi

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

    val mapper = SpiMasterCtrl.Mapper(factory(io.bus), spiMasterCtrl.io, p)
  }
}

case class Apb3SpiMaster(
  parameter: SpiCtrl.Parameter,
  busConfig: Apb3Config = Apb3Config(12, 32)
) extends SpiMaster.Core[Apb3] (
  parameter,
  Apb3(busConfig),
  Apb3SlaveFactory(_)
) { val dummy = 0 }

case class WishboneSpiMaster(
  parameter: SpiCtrl.Parameter,
  busConfig: WishboneConfig = WishboneConfig(12, 32)
) extends SpiMaster.Core[Wishbone] (
  parameter,
  Wishbone(busConfig),
  WishboneSlaveFactory(_)
) { val dummy = 0 }

case class AvalonMMSpiMaster(
  parameter: SpiCtrl.Parameter,
  busConfig: AvalonMMConfig = AvalonMMConfig.fixed(12, 32, 1)
) extends SpiMaster.Core[AvalonMM] (
  parameter,
  AvalonMM(busConfig),
  AvalonMMSlaveFactory(_)
) { val dummy = 0 }
