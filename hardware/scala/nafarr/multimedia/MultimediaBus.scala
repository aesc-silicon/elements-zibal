package nafarr.multimedia

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.misc.BusSlaveFactory

case class MultimediaConfig(
    rgbConfig: RgbConfig,
    hTimings: TimingsConfig,
    vTimings: TimingsConfig
) {
  def getPixelXWidth() = hTimings.width
  def getPixelYWidth() = vTimings.width
  def getPixelCount() = hTimings.visibleArea * vTimings.visibleArea
}
object MultimediaConfig {
  def default() = MultimediaConfig(
    RgbConfig.set12bit(),
    TimingsConfig.set800x600x72h(),
    TimingsConfig.set800x600x72v()
  )
  def full() = MultimediaConfig(
    RgbConfig.set12bit(),
    TimingsConfig.set800x600x72h(),
    TimingsConfig.set800x600x72v()
  )
}

object MultimediaStream {
  def apply(c: MultimediaConfig) = MultimediaStream(c)

  case class Pixel(c: MultimediaConfig) extends Bundle with IMasterSlave {
    val x = UInt(c.getPixelXWidth() bit)
    val y = UInt(c.getPixelYWidth() bit)

    override def asMaster(): Unit = {
      in(x)
      in(y)
    }
    override def asSlave(): Unit = {
      out(x)
      out(y)
    }
  }

  case class MultimediaStream(c: MultimediaConfig) extends Bundle with IMasterSlave {
    val pixel = Pixel(c)
    val enable = Bool
    val data = Stream(Rgb(c.rgbConfig))

    override def asMaster(): Unit = {
      master(pixel)
      in(enable)
      master(data)
    }
    override def asSlave(): Unit = {
      slave(pixel)
      out(enable)
      slave(data)
    }
  }
}
