package nafarr.multimedia

import spinal.core._

case class TimingsConfig(
    frontPorch: Int,
    syncPulse: Int,
    backPorch: Int,
    visibleArea: Int,
    width: Int,
    polarity: Boolean,
    syncOffset: Int = 0
) {
  def syncStart(): Int = frontPorch - 1 - syncOffset
  def syncEnd(): Int = syncStart() + syncPulse
  def dataStart(): Int = syncEnd() + backPorch + syncOffset
  def dataEnd(): Int = dataStart() + visibleArea
}
object TimingsConfig {
  def set800x600x72h() = TimingsConfig(56, 120, 64, 800, 12, true, 4)
  def set800x600x72v() = TimingsConfig(37, 6, 23, 600, 12, true)
}
