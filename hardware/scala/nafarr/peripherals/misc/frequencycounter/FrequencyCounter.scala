package nafarr.peripherals.misc.frequencycounter

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.avalon._
import spinal.lib.bus.wishbone._

object FrequencyCounter {
  case class Io(p: FrequencyCounterCtrl.Parameter) extends Bundle {
    val clock = in(Bool)
  }

  class Core[T <: spinal.core.Data with IMasterSlave](
      p: FrequencyCounterCtrl.Parameter,
      busType: HardType[T],
      factory: T => BusSlaveFactory
  ) extends Component {
    val io = new Bundle {
      val bus = slave(busType())
      val clock = Io(p)
    }

    val ctrl = FrequencyCounterCtrl(p)
    ctrl.io.clock <> io.clock

    val mapper = FrequencyCounterCtrl.Mapper(factory(io.bus), ctrl.io, p)
  }
}

case class Apb3FrequencyCounter(
    parameter: FrequencyCounterCtrl.Parameter,
    busConfig: Apb3Config = Apb3Config(12, 32)
) extends FrequencyCounter.Core[Apb3](
      parameter,
      Apb3(busConfig),
      Apb3SlaveFactory(_)
    ) { val dummy = 0 }

case class WishboneFrequencyCounter(
    parameter: FrequencyCounterCtrl.Parameter,
    busConfig: WishboneConfig = WishboneConfig(12, 32)
) extends FrequencyCounter.Core[Wishbone](
      parameter,
      Wishbone(busConfig),
      WishboneSlaveFactory(_)
    ) { val dummy = 0 }

case class AvalonMMFrequencyCounter(
    parameter: FrequencyCounterCtrl.Parameter,
    busConfig: AvalonMMConfig = AvalonMMConfig.fixed(12, 32, 1)
) extends FrequencyCounter.Core[AvalonMM](
      parameter,
      AvalonMM(busConfig),
      AvalonMMSlaveFactory(_)
    ) { val dummy = 0 }
