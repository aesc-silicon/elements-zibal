package nafarr.multimedia.framebuffer

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.avalon._
import spinal.lib.bus.wishbone._
import nafarr.multimedia.{Rgb, MultimediaConfig, MultimediaStream}

object SlowFramebuffer {
  class Core[T <: spinal.core.Data with IMasterSlave](
      config: MultimediaConfig,
      scale: Int,
      busType: HardType[T],
      factory: T => BusSlaveFactory
  ) extends Component {
    val io = new Bundle {
      val bus = slave(busType())
      val stream = master(MultimediaStream(config))
    }

    val ctrl = SlowFramebufferCtrl(config, scale)
    ctrl.io.stream <> io.stream

    val mapper = SlowFramebufferCtrl.Mapper(factory(io.bus), ctrl.io, config)
  }
}

case class Apb3SlowFramebuffer(
    config: MultimediaConfig,
    scale: Int = 1,
    busConfig: Apb3Config = Apb3Config(12, 32)
) extends SlowFramebuffer.Core[Apb3](
      config,
      scale,
      Apb3(busConfig),
      Apb3SlaveFactory(_)
    ) { val dummy = 0 }

case class WishboneSlowFramebuffer(
    config: MultimediaConfig,
    scale: Int = 1,
    busConfig: WishboneConfig = WishboneConfig(12, 32)
) extends SlowFramebuffer.Core[Wishbone](
      config,
      scale,
      Wishbone(busConfig),
      WishboneSlaveFactory(_)
    ) { val dummy = 0 }

case class AvalonMMSlowFramebuffer(
    config: MultimediaConfig,
    scale: Int = 1,
    busConfig: AvalonMMConfig = AvalonMMConfig.fixed(12, 32, 1)
) extends SlowFramebuffer.Core[AvalonMM](
      config,
      scale,
      AvalonMM(busConfig),
      AvalonMMSlaveFactory(_)
    ) { val dummy = 0 }
