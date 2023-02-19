package nafarr.blackboxes.xilinx.a7

import spinal.core._
import spinal.core.sim._
import spinal.lib.io.TriState
import spinal.lib.History

abstract class XilinxIo(pin: String) extends Bundle {

  var pinName = pin

  var ioStandardName = ""
  var ioTerm = ""
  var ioSlew = ""
  var clockSpeed: HertzNumber = 1 Hz
  var dedicatedClockRoute = true
  var comment_ = ""
  var pullType = ""

  def getPin() = this.pinName
  def getIoStandard() = this.ioStandardName
  def getTerm() = this.ioTerm
  def getSlew() = this.ioSlew
  def getClockSpeed() = this.clockSpeed
}

object XilinxCmosIo {
  def apply(pin: String) = new XilinxCmosIo(pin)

  class XilinxCmosIo(pin: String) extends XilinxIo(pin) {
    val PAD = inout(Analog(Bool()))
    ioStandard("LVCMOS33")
    def ioStandard(ioStandard: String) = {
      this.ioStandardName = ioStandard
      this
    }
    def clock(speed: HertzNumber) = {
      this.clockSpeed = speed
      this
    }
    def disableDedicatedClockRoute = {
      this.dedicatedClockRoute = false
      this
    }
    def inTerm(term: String) = {
      this.ioTerm = term
      this
    }
    def slew(slew: String) = {
      this.ioSlew = slew
      this
    }
    def comment(comment: String) = {
      this.comment_ = comment
      this
    }
    // Only valid for IBUF, OBUFT and IOBUF!
    def pull(pull: String) = {
      this.pullType = pull
      this
    }
    def <>(that: IOBUF.IOBUF) = that.IO := this.PAD
    def <>(that: IBUF.IBUF) = that.I := this.PAD
    def <>(that: OBUF.OBUF) = this.PAD := that.O
    def <>(that: OBUFT.OBUFT) = this.PAD := that.O
    def <>(that: PULLUP.PULLUP) = that.O := this.PAD
    def <>(that: PULLDOWN.PULLDOWN) = that.O := this.PAD
  }
}

object XilinxLvdsInput {
  case class Pos(pin: String) extends XilinxIo(pin) {
    val PAD = in(Bool())
    ioStandard("LVDS_25")
    def ioStandard(ioStandard: String) = {
      this.ioStandardName = ioStandard
      this
    }
    def clock(speed: HertzNumber) = {
      this.clockSpeed = speed
      this
    }
    def disableDedicatedClockRoute = {
      this.dedicatedClockRoute = false
      this
    }
    def inTerm(term: String) = {
      this.ioTerm = term
      this
    }
    def slew(slew: String) = {
      this.ioSlew = slew
      this
    }
    def comment(comment: String) = {
      this.comment_ = comment
      this
    }
    def <>(that: IBUFDS.IBUFDS) = that.I := this.PAD
  }
  case class Neg(pin: String) extends XilinxIo(pin) {
    val PAD = in(Bool())
    ioStandard("LVDS_25")
    def ioStandard(ioStandard: String) = {
      this.ioStandardName = ioStandard
      this
    }
    def clock(speed: HertzNumber) = {
      this.clockSpeed = speed
      this
    }
    def disableDedicatedClockRoute = {
      this.dedicatedClockRoute = false
      this
    }
    def inTerm(term: String) = {
      this.ioTerm = term
      this
    }
    def slew(slew: String) = {
      this.ioSlew = slew
      this
    }
    def comment(comment: String) = {
      this.comment_ = comment
      this
    }
    def <>(that: IBUFDS.IBUFDS) = that.IB := this.PAD
  }
}

object XilinxLvdsOutput {
  case class Pos(pin: String) extends XilinxIo(pin) {
    val PAD = out(Bool())
    ioStandard("LVDS_25")
    def ioStandard(ioStandard: String) = {
      this.ioStandardName = ioStandard
      this
    }
    def clock(speed: HertzNumber) = {
      this.clockSpeed = speed
      this
    }
    def disableDedicatedClockRoute = {
      this.dedicatedClockRoute = false
      this
    }
    def inTerm(term: String) = {
      this.ioTerm = term
      this
    }
    def slew(slew: String) = {
      this.ioSlew = slew
      this
    }
    def comment(comment: String) = {
      this.comment_ = comment
      this
    }
    def <>(that: IBUFDS.IBUFDS) = that.I := this.PAD
  }
  case class Neg(pin: String) extends XilinxIo(pin) {
    val PAD = out(Bool())
    ioStandard("LVDS_25")
    def ioStandard(ioStandard: String) = {
      this.ioStandardName = ioStandard
      this
    }
    def clock(speed: HertzNumber) = {
      this.clockSpeed = speed
      this
    }
    def disableDedicatedClockRoute = {
      this.dedicatedClockRoute = false
      this
    }
    def inTerm(term: String) = {
      this.ioTerm = term
      this
    }
    def slew(slew: String) = {
      this.ioSlew = slew
      this
    }
    def comment(comment: String) = {
      this.comment_ = comment
      this
    }
    def <>(that: IBUFDS.IBUFDS) = that.IB := this.PAD
  }
}

object IOBUF {
  def apply() = IOBUF()
  def apply(pin: TriState[Bool]) = IOBUF().withTriState(pin)
  def apply(in: Bool, out: Bool, en: Bool) = IOBUF().withBools(in, out, en)

  case class IOBUF(
      DRIVE: Int = 12,
      IBUF_LOW_PWR: String = "TRUE",
      IOSTANDARD: String = "DEFAULT",
      SLEW: String = "SLOW"
  ) extends BlackBox {
    val I, T = in(Bool())
    val O = out(Bool())
    val IO = inout(Analog(Bool()))

    addRTLPath(System.getenv("NAFARR_BASE") + "/hardware/scala/nafarr/blackboxes/xilinx/a7/IO.v")

    addGeneric("DRIVE", DRIVE)
    addGeneric("IBUF_LOW_PWR", IBUF_LOW_PWR)
    addGeneric("IOSTANDARD", IOSTANDARD)
    addGeneric("SLEW", SLEW)

    when(T) {
      IO := I
    }
    O := IO

    def withTriState(pin: TriState[Bool]) = {
      this.I := pin.write
      this.T := !pin.writeEnable
      pin.read := this.O
      this
    }
    def withBools(in: Bool, out: Bool, en: Bool) = {
      this.I := out
      this.T := !en
      in := this.O
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
    val I = in(Bool())
    val O = out(Bool())

    addRTLPath(System.getenv("NAFARR_BASE") + "/hardware/scala/nafarr/blackboxes/xilinx/a7/IO.v")

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
    val I = in(Bool())
    val O = out(Bool())

    addRTLPath(System.getenv("NAFARR_BASE") + "/hardware/scala/nafarr/blackboxes/xilinx/a7/IO.v")

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
    val I, T = in(Bool())
    val O = out(Bool())

    addRTLPath(System.getenv("NAFARR_BASE") + "/hardware/scala/nafarr/blackboxes/xilinx/a7/IO.v")

    addGeneric("DRIVE", DRIVE)
    addGeneric("IOSTANDARD", IOSTANDARD)

    when(T) {
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
  def apply() = PULLUP()

  case class PULLUP() extends BlackBox {
    // Fake direction for SpinalHDL. Pin O is output!
    val O = in(Bool())

    addRTLPath(System.getenv("NAFARR_BASE") + "/hardware/scala/nafarr/blackboxes/xilinx/a7/IO.v")
  }
}

object PULLDOWN {
  def apply() = PULLDOWN()

  case class PULLDOWN() extends BlackBox {
    // Fake direction for SpinalHDL. Pin O is output!
    val O = out(Bool())

    addRTLPath(System.getenv("NAFARR_BASE") + "/hardware/scala/nafarr/blackboxes/xilinx/a7/IO.v")
  }
}

object IBUFDS {
  def apply() = IBUFDS()
  def apply(pin: Bool) = IBUFDS().withBool(pin)

  case class IBUFDS(
      CAPACITANCE: String = "DONT_CARE",
      DIFF_TERM: String = "FALSE",
      DQS_BIAS: String = "FALSE",
      IBUF_DELAY_VALUE: Int = 0,
      IBUF_LOW_PWR: String = "TRUE",
      IFD_DELAY_VALUE: String = "AUTO",
      IOSTANDARD: String = "DEFAULT"
  ) extends BlackBox {
    val I = in(Bool())
    val IB = in(Bool())
    val O = out(Bool())

    addRTLPath(System.getenv("NAFARR_BASE") + "/hardware/scala/nafarr/blackboxes/xilinx/a7/IO.v")

    addGeneric("DIFF_TERM", DIFF_TERM)
    addGeneric("DQS_BIAS", DQS_BIAS)
    addGeneric("IBUF_LOW_PWR", IBUF_LOW_PWR)
    addGeneric("IOSTANDARD", IOSTANDARD)

    O := I

    def withBool(pin: Bool) = {
      pin := this.O
      this
    }
  }
}

object PLL {
  case class PLLE2_BASE(
      DIVCLK_DIVIDE: Int = 1,
      CLKFBOUT_MULT: Int = 5,
      CLKFBOUT_PHASE: Double = 0.0
  ) extends BlackBox {
    require(DIVCLK_DIVIDE >= 0, "DIVCLK_DIVIDE must be at least 0.")
    require(DIVCLK_DIVIDE <= 57, "DIVCLK_DIVIDE must be at most 56.")
    require(CLKFBOUT_MULT >= 2, "CLKFBOUT_MULT must be at least 2.")
    require(CLKFBOUT_MULT <= 64, "CLKFBOUT_MULT must be at most 64.")
    require(CLKFBOUT_PHASE >= 0.0, "CLKFBOUT_PHASE must be at least 0.0 degree.")
    require(CLKFBOUT_PHASE <= 360.0, "CLKFBOUT_PHASE must be at most 360.0 degree.")

    val CLKIN1 = in(Bool())
    val RST = in(Bool())
    val PWRDWN = in(Bool())

    val CLKOUT0 = out(Bool())
    val CLKOUT1 = out(Bool())
    val CLKOUT2 = out(Bool())
    val CLKOUT3 = out(Bool())
    val CLKOUT4 = out(Bool())
    val CLKOUT5 = out(Bool())
    val LOCKED = out(Bool())

    val CLKFBOUT = out(Bool())
    val CLKFBIN = in(Bool())

    val designClockDomain = ClockDomain.current
    mapCurrentClockDomain(CLKIN1)
    val multipliedFrequency = designClockDomain.frequency.getValue * CLKFBOUT_MULT

    addRTLPath(System.getenv("NAFARR_BASE") + "/hardware/scala/nafarr/blackboxes/xilinx/a7/IO.v")

    addGeneric("CLKIN1_PERIOD", (1000000000 / designClockDomain.frequency.getValue.toInt))
    addGeneric("DIVCLK_DIVIDE", DIVCLK_DIVIDE)
    addGeneric("CLKFBOUT_MULT", CLKFBOUT_MULT)
    addGeneric("CLKFBOUT_PHASE", CLKFBOUT_PHASE)

    def connect() = {
      if (this.designClockDomain.hasResetSignal) {
        RST := this.designClockDomain.reset
      } else {
        val reset = False
        val resetCounter = Reg(UInt(4 bits)).init(0)
        when(resetCounter =/= U(resetCounter.range -> true)) {
          resetCounter := resetCounter + 1
          reset := True
        }
        RST := reset
      }

      this.PWRDWN := False
      this.CLKFBIN := this.CLKFBOUT
      this
    }

    /* Formula to calculate the clock:
     *
     * CLKOUTn (MHz) = CLKIN1 (MHz) * CLKFBOUT_MULT (int)
     *                 ----------------------
     *                 DIVCLKn_DIVIDE (ns)
     *
     * Formula to calculate the divide value:
     *
     * DIVCLKn_DIVIDE (ns) = CLKIN1 (MHz) * CLKFBOUT_MULT (int)
     *                       ----------------------
     *                       CLKOUTn (MHz)
     */
    def addClock(
        clock: Bool,
        number: Int,
        divide: Int,
        phase: Double = 0.0,
        dutyCycle: Double = 0.5
    ) = {
      require(number >= 0, "PLL clock output must be at least 0.")
      require(number <= 5, "PLL clock output must be at most 5.")
      require(divide >= 1, "Divide must be at least 1.")
      require(divide <= 128, "Divide must be at most 128.")
      require(phase >= -360.0, "Phase must be at least -360.0 degree.")
      require(phase <= 360.0, "Phase must be at most 360.0 degree.")
      require(dutyCycle >= 0.01, "Duty cycle must be at least 0.01.")
      require(dutyCycle <= 0.99, "Duty cycle must be at most 0.99.")

      addGeneric("CLKOUT%d_DIVIDE".format(number), divide)
      addGeneric("CLKOUT%d_PHASE".format(number), phase)
      addGeneric("CLKOUT%d_DUTY_CYCLE".format(number), dutyCycle)
    }

    def addClock0(desiredFrequency: HertzNumber, phase: Double = 0.0, dutyCycle: Double = 0.5) = {
      val divide = (multipliedFrequency / desiredFrequency).toInt
      this.addClock(this.CLKOUT0, 0, divide, phase, dutyCycle)
      this.CLKOUT0
    }

    def addClock1(desiredFrequency: HertzNumber, phase: Double = 0.0, dutyCycle: Double = 0.5) = {
      val divide = (multipliedFrequency / desiredFrequency).toInt
      this.addClock(this.CLKOUT1, 1, divide, phase, dutyCycle)
      this.CLKOUT1
    }

    def addClock2(desiredFrequency: HertzNumber, phase: Double = 0.0, dutyCycle: Double = 0.5) = {
      val divide = (multipliedFrequency / desiredFrequency).toInt
      this.addClock(this.CLKOUT2, 2, divide, phase, dutyCycle)
      this.CLKOUT2
    }

    def addClock3(desiredFrequency: HertzNumber, phase: Double = 0.0, dutyCycle: Double = 0.5) = {
      val divide = (multipliedFrequency / desiredFrequency).toInt
      this.addClock(this.CLKOUT3, 3, divide, phase, dutyCycle)
      this.CLKOUT3
    }

    def addClock4(desiredFrequency: HertzNumber, phase: Double = 0.0, dutyCycle: Double = 0.5) = {
      val divide = (multipliedFrequency / desiredFrequency).toInt
      this.addClock(this.CLKOUT4, 4, divide, phase, dutyCycle)
      this.CLKOUT4
    }

    def addClock5(desiredFrequency: HertzNumber, phase: Double = 0.0, dutyCycle: Double = 0.5) = {
      val divide = (multipliedFrequency / desiredFrequency).toInt
      this.addClock(this.CLKOUT5, 5, divide, phase, dutyCycle)
      this.CLKOUT5
    }
  }
}
