package nafarr.peripherals.misc.uniqueid

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.avalon._
import spinal.lib.bus.wishbone._

object UniqueID {
  class Core[T <: spinal.core.Data with IMasterSlave](
      p: UniqueIDCtrl.Parameter,
      busType: HardType[T],
      factory: T => BusSlaveFactory
  ) extends Component {
    val io = new Bundle {
      val bus = slave(busType())
    }

    val ctrl = UniqueIDCtrl(p)

    val mapper = UniqueIDCtrl.Mapper(factory(io.bus), ctrl.io, p)
  }
}

case class Apb3UniqueID(
    parameter: UniqueIDCtrl.Parameter,
    busConfig: Apb3Config = Apb3Config(12, 32)
) extends UniqueID.Core[Apb3](
      parameter,
      Apb3(busConfig),
      Apb3SlaveFactory(_)
    ) { val dummy = 0 }

case class WishboneUniqueID(
    parameter: UniqueIDCtrl.Parameter,
    busConfig: WishboneConfig = WishboneConfig(12, 32)
) extends UniqueID.Core[Wishbone](
      parameter,
      Wishbone(busConfig),
      WishboneSlaveFactory(_)
    ) { val dummy = 0 }

case class AvalonMMUniqueID(
    parameter: UniqueIDCtrl.Parameter,
    busConfig: AvalonMMConfig = AvalonMMConfig.fixed(12, 32, 1)
) extends UniqueID.Core[AvalonMM](
      parameter,
      AvalonMM(busConfig),
      AvalonMMSlaveFactory(_)
    ) { val dummy = 0 }
