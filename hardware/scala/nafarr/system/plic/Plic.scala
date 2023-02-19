package nafarr.system.plic

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.avalon._
import spinal.lib.bus.wishbone._
import spinal.lib.misc.plic._

import scala.collection.mutable.ArrayBuffer

object Plic {
  class Core[T <: spinal.core.Data with IMasterSlave](
      p: PlicCtrl.Parameter,
      busType: HardType[T],
      factory: T => BusSlaveFactory
  ) extends Component {
    val io = new Bundle {
      val bus = slave(busType())
      val interrupt = out(Bool)
      val sources = in(Bits(p.sources bits))
    }

    val gateways = ArrayBuffer[PlicGateway]()
    for (i <- 0 until p.sources) {
      gateways += PlicGatewayActiveHigh(
        source = io.sources(i),
        id = i,
        priorityWidth = p.priorityWidth
      )
    }
    gateways.foreach(_.priority := 1)

    val targets = Seq(
      PlicTarget(
        gateways = gateways,
        priorityWidth = p.priorityWidth
      )
    )
    targets.foreach(_.threshold := 0)
    /*
    TODO: Check if 'PlicMapping.light' is equal to following configuration
    val plicMapping = PlicMapping(
        gatewayPriorityOffset = 0x0000,
        gatewayPendingOffset = 0x1000,
        targetEnableOffset = 0x2000,
        targetThresholdOffset = 0xF000,
        targetClaimOffset = 0xF004,
        gatewayPriorityShift = 2,
        gatewayPendingShift = 2,
        targetThresholdShift = 12,
        targetClaimShift = 12,
        targetEnableShift = 7,
        gatewayPriorityReadGen = true,
        gatewayPendingReadGen = true,
        targetThresholdReadGen = true,
        targetEnableReadGen = true
    )
     */
    val mapping = PlicMapper(factory(io.bus), PlicMapping.light)(
      gateways = gateways,
      targets = targets
    )

    io.interrupt := targets(0).iep
  }
}

case class Apb3Plic(
    parameter: PlicCtrl.Parameter,
    busConfig: Apb3Config = Apb3Config(16, 32)
) extends Plic.Core[Apb3](
      parameter,
      Apb3(busConfig),
      Apb3SlaveFactory(_)
    ) { val dummy = 0 }

case class WishbonePlic(
    parameter: PlicCtrl.Parameter,
    busConfig: WishboneConfig = WishboneConfig(16, 32)
) extends Plic.Core[Wishbone](
      parameter,
      Wishbone(busConfig),
      WishboneSlaveFactory(_)
    ) { val dummy = 0 }

case class AvalonMMPlic(
    parameter: PlicCtrl.Parameter,
    busConfig: AvalonMMConfig = AvalonMMConfig.fixed(16, 32, 1)
) extends Plic.Core[AvalonMM](
      parameter,
      AvalonMM(busConfig),
      AvalonMMSlaveFactory(_)
    ) { val dummy = 0 }
