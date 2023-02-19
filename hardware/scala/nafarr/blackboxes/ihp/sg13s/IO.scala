package nafarr.blackboxes.ihp.sg13s

import spinal.core._
import spinal.lib.io.{TriState, ReadableOpenDrain}
import spinal.lib.History

object IhpCmosIo {
  def apply(edge: String, number: Int) = new IhpCmosIo(edge, number)

  class IhpCmosIo(edge: String, number: Int) extends Bundle {
    val PAD = inout(Analog(Bool()))

    var cell = ""
    val edge_ = edge
    val number_ = number

    def <>(that: ixc013_b16m.ixc013_b16m) = {
      that.PAD := this.PAD
      cell = "ixc013_b16m"
    }
    def <>(that: ixc013_b16mpup.ixc013_b16mpup) = {
      that.PAD := this.PAD
      cell = "ixc013_b16mpup"
    }
    def <>(that: ixc013_i16x.ixc013_i16x) = {
      that.PAD := this.PAD
      cell = "ixc013_i16x"
    }
  }
}

object ixc013_b16m {
  def apply() = ixc013_b16m()
  def apply(pin: TriState[Bool]) = ixc013_b16m().withTriState(pin)

  case class ixc013_b16m() extends BlackBox {
    val DIN, OEN = in(Bool())
    val DOUT = out(Bool())
    val PAD = inout(Analog(Bool()))

    addRTLPath(System.getenv("NAFARR_BASE") + "/hardware/scala/nafarr/blackboxes/ihp/sg13s/IO.v")

    when(OEN) {
      PAD := DIN
    }
    DOUT := PAD

    def withTriState(pin: TriState[Bool]) = {
      val invertedEnable = Bool()
      INVJIX12(pin.writeEnable, invertedEnable)
      this.DIN := pin.write
      this.OEN := invertedEnable
      pin.read := this.DOUT
      this
    }
    def asOutput(pin: Bool) = {
      this.DIN := pin
      this.OEN := False
      this
    }
    def asInput(pin: Bool) = {
      this.DIN := False
      this.OEN := True
      pin := this.DOUT
      this
    }
  }
}

object ixc013_b16mpup {
  def apply() = ixc013_b16mpup()
  def apply(pin: TriState[Bool]) = ixc013_b16mpup().withTriState(pin)

  case class ixc013_b16mpup() extends BlackBox {
    val DIN, OEN = in(Bool())
    val DOUT = out(Bool())
    val PAD = inout(Analog(Bool()))

    addRTLPath(System.getenv("NAFARR_BASE") + "/hardware/scala/nafarr/blackboxes/ihp/sg13s/IO.v")

    when(OEN) {
      PAD := DIN
    }
    DOUT := PAD

    def withTriState(pin: TriState[Bool]) = {
      val invertedEnable = Bool()
      INVJIX12(pin.writeEnable, invertedEnable)
      this.DIN := pin.write
      this.OEN := invertedEnable
      pin.read := this.DOUT
      this
    }
    def withOpenDrain(pin: ReadableOpenDrain[Bool]) = {
      this.DIN := False
      this.OEN := pin.write
      pin.read := this.DOUT
      this
    }
    def asOutput(pin: Bool) = {
      this.DIN := pin
      this.OEN := False
      this
    }
    def asInput(pin: Bool) = {
      this.DIN := False
      this.OEN := True
      pin := this.DOUT
      this
    }
  }
}

object ixc013_i16x {
  def apply() = ixc013_i16x()
  def apply(pin: Bool) = ixc013_i16x().withBool(pin)

  case class ixc013_i16x() extends BlackBox {
    val PAD = in(Bool())
    val DOUT = out(Bool())

    addRTLPath(System.getenv("NAFARR_BASE") + "/hardware/scala/nafarr/blackboxes/ihp/sg13s/IO.v")

    DOUT := PAD

    def withBool(pin: Bool) = {
      pin := this.DOUT
      this
    }
  }
}

object INVJIX12 {
  def apply(normal: Bool, inverted: Bool) = INVJIX12().connect(normal, inverted)

  case class INVJIX12() extends BlackBox {
    val A = in(Bool())
    val Q = out(Bool())

    addRTLPath(System.getenv("NAFARR_BASE") + "/hardware/scala/nafarr/blackboxes/ihp/sg13s/IO.v")

    Q := !A

    def connect(normal: Bool, inverted: Bool) = {
      this.A := normal
      inverted := this.Q
      this
    }
  }
}
