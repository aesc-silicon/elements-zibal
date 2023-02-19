package nafarr.peripherals.com.i2c

import spinal.core._
import spinal.lib._
import spinal.lib.io.ReadableOpenDrain

object I2c {
  case class Io(p: I2cCtrl.Parameter) extends Bundle with IMasterSlave {
    val scl = ReadableOpenDrain(Bool)
    val sda = ReadableOpenDrain(Bool)
    val interrupts = Bits(p.interrupts bits)

    override def asMaster(): Unit = {
      master(scl)
      master(sda)
      in(interrupts)
    }
    override def asSlave(): Unit = {
      slave(scl)
      slave(sda)
      out(interrupts)
    }
  }
}
