package nafarr.multimedia

import spinal.core._

case class RgbConfig(
    rWidth: Int,
    gWidth: Int,
    bWidth: Int
) {
  def getWidth() = rWidth + gWidth + bWidth
}

object RgbConfig {
  def set3bit() = RgbConfig(1, 1, 1)
  def set6bit() = RgbConfig(2, 2, 2)
  def set8bit() = RgbConfig(3, 3, 2)
  def set12bit() = RgbConfig(4, 4, 4)
  def set16bit() = RgbConfig(5, 6, 5)
  def set24bit() = RgbConfig(8, 8, 8)
}

object Rgb {
  def apply(rWidth: Int, gWidth: Int, bWidth: Int): Rgb = Rgb(RgbConfig(rWidth, gWidth, bWidth))
}
case class Rgb(c: RgbConfig) extends Bundle {
  val r = UInt(c.rWidth bits)
  val g = UInt(c.gWidth bits)
  val b = UInt(c.bWidth bits)

  def clear(): Unit = {
    r := 0
    g := 0
    b := 0
  }
}
