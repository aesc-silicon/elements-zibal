package zibal.sim

import spinal.core._
import spinal.core.sim._
import spinal.lib._

object MT25Q {
  def apply() = MT25Q()

  object SpiState extends SpinalEnum {
    val COMMAND, ADDRESS0, ADDRESS1, ADDRESS2, RESPONSE = newElement()
  }

  case class MT25Q() extends Component {
    val io = new Bundle {
      val clock = in(Bool)
      val reset = in(Bool)
      val chipSelect = in(Bool)
      val dataClock = in(Bool)
      val dataIn = in(Bool).addTag(crossClockDomain)
      val dataOut = out(Bool).addTag(crossClockDomain)
    }

    val chipSelect = !io.chipSelect
    val chipReset = False
    val resetIn = io.reset & !io.chipSelect
    val reset = resetIn | chipReset

    val dummyClockDomain = ClockDomain(
      clock = io.clock,
      config = ClockDomainConfig(
        resetKind = BOOT
      )
    )

    val dummyReset = new ClockingArea(dummyClockDomain) {
      val counter = Counter(5)
      when (counter =/= 4) {
        chipReset := counter.value.lsb
        counter.increment()
      }
    }

    val risingClockDomain = ClockDomain(
      clock = io.dataClock,
      reset = reset,
      config = ClockDomainConfig(
        resetKind = ASYNC,
        resetActiveLevel = LOW
      )
    )

    val fallingClockDomain = ClockDomain(
      clock = io.dataClock,
      reset = reset,
      config = ClockDomainConfig(
        clockEdge = FALLING,
        resetKind = ASYNC,
        resetActiveLevel = LOW
      )
    )

    val deviceIn = new ClockingArea(risingClockDomain) {
      val state = RegInit(SpiState.COMMAND)
      val command = Reg(UInt(8 bits)) init(0)
      val address = Reg(UInt(24 bits)) init(0)
      val counter = CounterFreeRun(8)

      val samples = History(
        that = io.dataIn,
        length = 8,
        when = True
      )

      switch(state) {
        is(SpiState.COMMAND) {
          when(counter.value === 7) {
            command := samples.asBits.asUInt
            counter.clear()
            state := SpiState.ADDRESS0
          }
        }
        is(SpiState.ADDRESS0) {
          when(counter.value === 7) {
            address(23 downto 16) := samples.asBits.asUInt
            counter.clear()
            state := SpiState.ADDRESS1
          }
        }
        is(SpiState.ADDRESS1) {
          when(counter.value === 7) {
            address(15 downto 8) := samples.asBits.asUInt
            counter.clear()
            state := SpiState.ADDRESS2
          }
        }
        is(SpiState.ADDRESS2) {
          when(counter.value === 7) {
            address(7 downto 0) := samples.asBits.asUInt
            counter.clear()
            state := SpiState.RESPONSE
          }
        }
      }

    }
    val deviceOut = new ClockingArea(fallingClockDomain) {
      val offset = Reg(UInt(24 bits)) init(0)
      val response = UInt(8 bits)
      val counter = Counter(8)
      val data = Mem(UInt(8 bits), 16777216)
      val output = Reg(Bool) init(False)
      io.dataOut := output

      response := data.readAsync(deviceIn.address + offset)

      output := False
      switch(deviceIn.state) {
        is(SpiState.RESPONSE) {
          when(counter.value === 7) {
            offset := offset + 1
          }
          counter.increment()
          output := response(7 - counter.value)
        }
      }
    }
  }
}
