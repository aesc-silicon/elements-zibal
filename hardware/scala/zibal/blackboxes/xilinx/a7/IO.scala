package zibal.blackboxes.xilinx.a7
import spinal.core._
import spinal.lib.io.TriState
import spinal.lib.History

object XilinxCmosIo {
  def apply(pin: String) = new XilinxCmosIo(pin)

  class XilinxCmosIo(pin: String) extends Bundle {
    val PAD = inout(Analog(Bool()))

    var pinName = pin
    var ioStandardName = "LVCMOS33"
    var clockSpeed: HertzNumber = 1 Hz
    var comment_ = ""

    def getPin() = this.pinName
    def getIoStandard() = this.ioStandardName
    def getClockSpeed() = this.clockSpeed

    def ioStandard(ioStandard: String) = {
      this.ioStandardName = ioStandard
      this
    }
    def clock(speed: HertzNumber) = {
      this.clockSpeed = speed
      this
    }
    def comment(comment: String) = {
      this.comment_ = comment
      this
    }
    def <>(that: IOBUF.IOBUF) = that.IO := this.PAD
    def <>(that: IBUF.IBUF) = that.I := this.PAD
    def <>(that: OBUF.OBUF) = this.PAD := that.O
    def <>(that: OBUFT.OBUFT) = this.PAD := that.O
  }
}

object IOBUF {
  def apply() = IOBUF()
  def apply(pin: TriState[Bool]) = IOBUF().withTriState(pin)

  case class IOBUF(
    DRIVE: Int = 12,
    IBUF_LOW_PWR: String = "TRUE",
    IOSTANDARD: String = " DEFAULT",
    SLEW: String = "SLOW"
  ) extends BlackBox {
    val I, T = in Bool()
    val O = out Bool()
    val IO = inout(Analog(Bool()))

    addGeneric("DRIVE", DRIVE)
    addGeneric("IBUF_LOW_PWR", IBUF_LOW_PWR)
    addGeneric("IOSTANDARD", IOSTANDARD)
    addGeneric("SLEW", SLEW)

    when(T){
      IO := I
    }
    O := IO

    def withTriState(pin: TriState[Bool]) = {
      this.I := pin.write
      this.T := !pin.writeEnable
      pin.read := this.O
      this
    }
  }
}

object IBUF {
  def apply() = IBUF()
  def apply(pin: Bool) = IBUF().withBool(pin)

  case class IBUF(
    CAPACITANCE: String = "DONT_CARE",
    IBUF_DELAY_VALUE: Int = 0,
    IBUF_LOW_PWR: String = "TRUE",
    IFD_DELAY_VALUE: String = "AUTO",
    IOSTANDARD: String = "DEFAULT"
  ) extends BlackBox {
    val I = in Bool()
    val O = out Bool()

    addGeneric("IBUF_LOW_PWR", IBUF_LOW_PWR)
    addGeneric("IOSTANDARD", IOSTANDARD)

    O := I

    def withBool(pin: Bool) = {
      pin := this.O
      this
    }
  }
}

object OBUF {
  def apply() = OBUF()
  def apply(pin: Bool) = OBUF().withBool(pin)

  case class OBUF(
    CAPACITANCE: String = "DONT_CARE",
    DRIVE: Int = 12,
    IOSTANDARD: String = "DEFAULT"
  ) extends BlackBox {
    val I = in Bool()
    val O = out Bool()

    addGeneric("DRIVE", DRIVE)
    addGeneric("IOSTANDARD", IOSTANDARD)

    O := I

    def withBool(pin: Bool) = {
      this.I := pin
      this
    }
    def driveHigh() = {
      this.I := True
      this
    }
    def driveLow() = {
      this.I := False
      this
    }
  }
}

object OBUFT {
  def apply() = OBUFT()
  def apply(write: Bool, writeEnable: Bool) = OBUFT().withBools(write, writeEnable)

  case class OBUFT(
    CAPACITANCE: String = "DONT_CARE",
    DRIVE: Int = 12,
    IOSTANDARD: String = "DEFAULT"
  ) extends BlackBox {
    val I, T = in Bool()
    val O = out Bool()

    addGeneric("CAPACITANCE", CAPACITANCE)
    addGeneric("DRIVE", DRIVE)
    addGeneric("IOSTANDARD", IOSTANDARD)

    when(T){
      O := I
    }

    def withBools(write: Bool, writeEnable: Bool) = {
      this.I := write
      this.T := writeEnable
      this
    }
  }
}

object PULLUP {
  def apply(pin: XilinxCmosIo.XilinxCmosIo) = PULLUP().withBool(pin.PAD)

  case class PULLUP() extends BlackBox {
    val O = out Bool()

    def withBool(pin: Bool) = {
      pin := this.O
      this
    }
  }
}

object PULLDOWN {
  def apply(pin: XilinxCmosIo.XilinxCmosIo) = PULLDOWN().withBool(pin.PAD)

  case class PULLDOWN() extends BlackBox {
    val O = out Bool()

    def withBool(pin: Bool) = {
      pin := this.O
      this
    }
  }
}
