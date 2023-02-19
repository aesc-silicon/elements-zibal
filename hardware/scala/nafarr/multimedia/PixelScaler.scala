package nafarr.multimedia

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc._

object PixelScaler {
  def apply(c: MultimediaConfig, scale: Int) = PixelScaler(c, scale)

  case class PixelScaler(c: MultimediaConfig, scale: Int) extends Component {
    val io = new Bundle {
      val sink = slave(MultimediaStream(c))
      val source = master(MultimediaStream(c))
    }

    io.sink.pixel.x := io.source.pixel.x / scale
    io.sink.pixel.y := io.source.pixel.y / scale

    io.sink.enable <> io.source.enable
    io.sink.data <> io.source.data
  }
}
