package nafarr.multimedia.framebuffer

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc.BusSlaveFactory
import nafarr.multimedia.{Rgb, MultimediaConfig, MultimediaStream}

object SlowFramebufferCtrl {
  def apply(c: MultimediaConfig, scale: Int) = SlowFramebufferCtrl(c, scale)

  case class Io(c: MultimediaConfig) extends Bundle {
    val stream = master(MultimediaStream(c))
    val address = in(UInt(16 bits))
    val pixel = in(Rgb(c.rgbConfig))
    val write = in(Bool)
  }

  case class SlowFramebufferCtrl(c: MultimediaConfig, scale: Int) extends Component {
    val io = Io(c)

    def content = for (index <- 0 until c.getPixelCount() / (scale * scale)) yield {
      val data = Rgb(c.rgbConfig)
      data.r := 0
      data.g := 0
      data.b := 0
      data
    }

    val address = UInt(13 bits)
    val write = RegNext(io.write)
    val framebuffer = Mem(Rgb(c.rgbConfig), content)

    address := io.stream.pixel.x.resize(13) +
      ((c.hTimings.visibleArea / scale) * io.stream.pixel.y).resize(13)
    io.stream.data.payload := framebuffer.readSync(address(0, 13 bits), io.stream.enable)
    io.stream.data.valid := RegNext(io.stream.enable)

    framebuffer.write(io.address(0, 13 bits), io.pixel, write)
  }

  case class Mapper(
      busCtrl: BusSlaveFactory,
      ctrl: Io,
      c: MultimediaConfig
  ) extends Area {

    busCtrl.drive(ctrl.address, 0x0)
    busCtrl.drive(ctrl.pixel, 0x0, 16)
    ctrl.write := busCtrl.isWriting(0x0)
  }
}
