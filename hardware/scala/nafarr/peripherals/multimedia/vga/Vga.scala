package nafarr.peripherals.multimedia.vga

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.avalon._
import spinal.lib.bus.wishbone._
import nafarr.multimedia.{Rgb, MultimediaStream}

object Vga {
  case class Io(p: VgaCtrl.Parameter) extends Bundle with IMasterSlave {
    val pixels = Rgb(p.multimediaConfig.rgbConfig)
    val hSync = Bool
    val vSync = Bool

    override def asMaster(): Unit = {
      out(pixels)
      out(hSync)
      out(vSync)
    }
  }

  class Core[T <: spinal.core.Data with IMasterSlave](
      p: VgaCtrl.Parameter,
      busType: HardType[T],
      factory: T => BusSlaveFactory
  ) extends Component {
    val io = new Bundle {
      val bus = slave(busType())
      val vga = master(Io(p))
      val stream = slave(MultimediaStream(p.multimediaConfig))
    }

    val ctrl = VgaCtrl(p)
    ctrl.io.vga <> io.vga
    ctrl.io.stream <> io.stream

    val mapper = VgaCtrl.Mapper(factory(io.bus), ctrl.io, p)
  }
}

case class Apb3Vga(
    parameter: VgaCtrl.Parameter,
    busConfig: Apb3Config = Apb3Config(12, 32)
) extends Vga.Core[Apb3](
      parameter,
      Apb3(busConfig),
      Apb3SlaveFactory(_)
    ) { val dummy = 0 }

case class WishboneVga(
    parameter: VgaCtrl.Parameter,
    busConfig: WishboneConfig = WishboneConfig(12, 32)
) extends Vga.Core[Wishbone](
      parameter,
      Wishbone(busConfig),
      WishboneSlaveFactory(_)
    ) { val dummy = 0 }

case class AvalonMMVga(
    parameter: VgaCtrl.Parameter,
    busConfig: AvalonMMConfig = AvalonMMConfig.fixed(12, 32, 1)
) extends Vga.Core[AvalonMM](
      parameter,
      AvalonMM(busConfig),
      AvalonMMSlaveFactory(_)
    ) { val dummy = 0 }
