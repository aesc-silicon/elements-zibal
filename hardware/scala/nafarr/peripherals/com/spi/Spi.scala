package nafarr.peripherals.com.spi

import spinal.core._
import spinal.lib._

object Spi {
  case class Io(p: SpiCtrl.Parameter) extends Bundle with IMasterSlave {
    val ss = Bits(p.ssWidth bits)
    val sclk = Bool
    val mosi = Bool
    val miso = Bool

    override def asMaster(): Unit = {
      out(ss)
      out(sclk)
      out(mosi)
      in(miso)
    }
    override def asSlave(): Unit = {
      in(ss)
      in(sclk)
      in(mosi)
      out(miso)
    }
  }
}
