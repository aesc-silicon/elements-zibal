package nafarr.peripherals.misc.sevensegment

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.avalon._
import spinal.lib.bus.wishbone._

object SevenSegment {
  case class Io(p: SevenSegmentCtrl.Parameter) extends Bundle {
    val value = out(Bits(8 bits))
    val select = out(Bits(p.count bits))
  }

  class Core[T <: spinal.core.Data with IMasterSlave](
      p: SevenSegmentCtrl.Parameter,
      busType: HardType[T],
      factory: T => BusSlaveFactory
  ) extends Component {
    val io = new Bundle {
      val bus = slave(busType())
      val segments = SevenSegment.Io(p)
    }

    val sevenSegCtrl = SevenSegmentCtrl(p)
    io.segments <> sevenSegCtrl.io.segments

    val mapper = SevenSegmentCtrl.Mapper(factory(io.bus), sevenSegCtrl.io, p)
  }
}

case class Apb3SevenSegment(
    parameter: SevenSegmentCtrl.Parameter,
    busConfig: Apb3Config = Apb3Config(12, 32)
) extends SevenSegment.Core[Apb3](
      parameter,
      Apb3(busConfig),
      Apb3SlaveFactory(_)
    ) { val dummy = 0 }

case class WishboneSevenSegment(
    parameter: SevenSegmentCtrl.Parameter,
    busConfig: WishboneConfig = WishboneConfig(12, 32)
) extends SevenSegment.Core[Wishbone](
      parameter,
      Wishbone(busConfig),
      WishboneSlaveFactory(_)
    ) { val dummy = 0 }

case class AvalonMMSevenSegment(
    parameter: SevenSegmentCtrl.Parameter,
    busConfig: AvalonMMConfig = AvalonMMConfig.fixed(12, 32, 1)
) extends SevenSegment.Core[AvalonMM](
      parameter,
      AvalonMM(busConfig),
      AvalonMMSlaveFactory(_)
    ) { val dummy = 0 }
