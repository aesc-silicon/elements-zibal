package nafarr.system.clock

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.avalon._
import spinal.lib.bus.wishbone._

case class ClockParameter(
    name: String,
    frequency: HertzNumber,
    reset: String = "",
    resetConfig: ClockDomainConfig =
      ClockDomainConfig(resetKind = spinal.core.SYNC, resetActiveLevel = LOW),
    synchronousWith: String = ""
)

object ClockController {
  class Core[T <: spinal.core.Data with IMasterSlave](
      p: ClockControllerCtrl.Parameter,
      busType: HardType[T],
      factory: T => BusSlaveFactory
  ) extends Component {
    val io = new Bundle {
      val bus = slave(busType())
      val config = out(ClockControllerCtrl.Config(p))
    }
    val busCtrl = factory(io.bus)

    busCtrl.driveAndRead(io.config.enable, 0x0).init(U((0 until p.domains.length) -> true))
  }
}

case class Apb3ClockController(
    parameter: ClockControllerCtrl.Parameter,
    busConfig: Apb3Config = Apb3Config(12, 32)
) extends ClockController.Core[Apb3](
      parameter,
      Apb3(busConfig),
      Apb3SlaveFactory(_)
    ) { val dummy = 0 }

case class WishboneClockController(
    parameter: ClockControllerCtrl.Parameter,
    busConfig: WishboneConfig = WishboneConfig(12, 32)
) extends ClockController.Core[Wishbone](
      parameter,
      Wishbone(busConfig),
      WishboneSlaveFactory(_)
    ) { val dummy = 0 }

case class AvalonMMClockController(
    parameter: ClockControllerCtrl.Parameter,
    busConfig: AvalonMMConfig = AvalonMMConfig.fixed(12, 32, 1)
) extends ClockController.Core[AvalonMM](
      parameter,
      AvalonMM(busConfig),
      AvalonMMSlaveFactory(_)
    ) { val dummy = 0 }
