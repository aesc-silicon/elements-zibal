package nafarr.peripherals.misc.frequencycounter

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc.BusSlaveFactory

object FrequencyCounterCtrl {
  def apply(p: Parameter = Parameter.default) = FrequencyCounterCtrl(p)

  case class Parameter(
      divider: Int = 4,
      sampleWidth: Int = 8,
      measureWidth: Int = 32
  ) {
    require(divider > 0, "Divider must be greater than 0.")
    require(measureWidth >= sampleWidth * divider)
    require(measureWidth <= 64, "Only 64 bits allowed for measuring")
  }

  object Parameter {
    def default() = Parameter()
  }

  case class Config(p: Parameter) extends Bundle {
    val enable = Bool
  }

  case class Io(p: Parameter) extends Bundle {
    val config = in(Config(p))
    val clock = FrequencyCounter.Io(p)
    val count = out(UInt(p.measureWidth bit))
  }

  case class FrequencyCounterCtrl(p: Parameter) extends Component {
    val io = Io(p)
    val measurementEnable = io.config.enable
    val sampleEnable = io.config.enable

    // Input clock might be too high to sample the clock with adders.
    // Divide it to decrease the timing requirements for sampling.
    val dividerClockDomain = ClockDomain(
      clock = io.clock.clock,
      reset = ClockDomain.current.reset,
      config = ClockDomain.current.config,
      clockEnable = sampleEnable
    )

    val inputClockDivider = new ClockingArea(dividerClockDomain) {

      val sampleClock = new SlowArea(p.divider) {
        val valueNext = UInt(p.sampleWidth bit)
        val value = RegNext(valueNext).init(0)
        // Ignore LSB to keep start up for two cycles.
        val start = value(p.sampleWidth - 1 downto 1) === 0

        valueNext := value + 1
      }

    }

    val measurement = new ClockEnableArea(measurementEnable) {
      val valueNext = UInt(p.measureWidth bit)
      val value = RegNext(valueNext).init(0)
      val count = Reg(UInt(p.measureWidth bit)).init(0)
      val start = BufferCC(inputClockDivider.sampleClock.start, False)
      val firstBit = Reg(Bool).init(True)

      valueNext := value + 1
      when(start && firstBit) {
        valueNext := 0
        count := value
        firstBit := False
      }
      when(!start && !firstBit) {
        firstBit := True
      }
      io.count := count
    }

  }

  case class Mapper(
      busCtrl: BusSlaveFactory,
      ctrl: Io,
      p: Parameter
  ) extends Area {

    val cfg = Reg(ctrl.config)
    cfg.enable.init(True)
    val divider = RegInit(U(p.divider * scala.math.pow(2, p.sampleWidth).toInt))
    divider.allowUnsetRegToAvoidLatch

    busCtrl.readAndWrite(cfg.enable, address = 0x00)
    busCtrl.read(divider, address = 0x04)
    busCtrl.readMultiWord(ctrl.count, address = 0x08)

    ctrl.config <> cfg
  }
}
