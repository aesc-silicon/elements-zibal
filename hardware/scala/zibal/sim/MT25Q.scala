// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal.sim

import spinal.core._
import spinal.core.sim._
import spinal.lib._

object MT25Q {
  def apply() = MT25Q()

  object SpiState extends SpinalEnum {
    val COMMAND, ADDRESS0, ADDRESS1, ADDRESS2, DUMMYCYCLES, RESPONSE = newElement()
  }

  case class MultiProtocol() extends Component {
    val io = new Bundle {
      val clock = in(Bool)
      val reset = in(Bool)
      val chipSelect = in(Bool)
      val dataClock = in(Bool)
      val dqIn = Vec(in(Bool), 4).addTag(crossClockDomain)
      val dqOut = Vec(out(Bool), 4).addTag(crossClockDomain)
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
      when(counter =/= 4) {
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

    val chipClockDomain = ClockDomain(
      clock = io.dataClock,
      reset = io.reset,
      config = ClockDomainConfig(
        resetKind = ASYNC,
        resetActiveLevel = LOW
      )
    )

    val deviceManagement = new ClockingArea(chipClockDomain) {
      val protocol = Reg(Bits(4 bits)).init(0)
    }

    val deviceIn = new ClockingArea(risingClockDomain) {
      val state = RegInit(SpiState.COMMAND)
      val command = Reg(UInt(8 bits)).init(0)
      val address = Reg(UInt(24 bits)).init(0)
      val counter = CounterFreeRun(8)
      val cycles = Reg(UInt(8 bits)).init(7)

      when(command === U(0x61)) {
        deviceManagement.protocol := B(2)
        cycles := 1
      }

      val samplesSingles = History(
        that = io.dqIn(0),
        length = 8,
        when = True
      )

      val samplesQuad = History(
        that = io.dqIn(3) ## io.dqIn(2) ## io.dqIn(1) ## io.dqIn(0),
        length = 2,
        when = True
      )

      val samples = samplesSingles.asBits
      when(deviceManagement.protocol === B(2)) {
        samples := samplesQuad.asBits
      }

      switch(state) {
        is(SpiState.COMMAND) {
          when(counter.value === cycles) {
            command := samples.asUInt
            counter.clear()
            state := SpiState.ADDRESS0
          }
        }
        is(SpiState.ADDRESS0) {
          when(counter.value === cycles) {
            address(23 downto 16) := samples.asUInt
            counter.clear()
            state := SpiState.ADDRESS1
          }
        }
        is(SpiState.ADDRESS1) {
          when(counter.value === cycles) {
            address(15 downto 8) := samples.asUInt
            counter.clear()
            state := SpiState.ADDRESS2
          }
        }
        is(SpiState.ADDRESS2) {
          when(counter.value === cycles) {
            address(7 downto 0) := samples.asUInt
            counter.clear()
            when(deviceManagement.protocol === B(0)) {
              state := SpiState.RESPONSE
            } otherwise {
              state := SpiState.DUMMYCYCLES
            }
          }
        }
        is(SpiState.DUMMYCYCLES) {
          when(counter.value === 3) {
            state := SpiState.RESPONSE
          }
        }
      }
    }

    val deviceOut = new ClockingArea(fallingClockDomain) {
      val offset = Reg(UInt(24 bits)).init(0)
      val response = UInt(8 bits)
      val counter = Counter(8)
      val data = Mem(UInt(8 bits), 16 MB)
      val output = Reg(Bits(4 bits)).init(0)
      val cycles = Reg(UInt(8 bits)).init(7)

      when(deviceManagement.protocol === B(2)) {
        cycles := 1
      }
      for (index <- 0 until io.dqOut.length) {
        io.dqOut(index) := output(index)
      }

      response := data.readAsync(deviceIn.address + offset)

      output := B(0)
      switch(deviceIn.state) {
        is(SpiState.RESPONSE) {
          when(counter.value === cycles) {
            offset := offset + 1
          }
          counter.increment()
          when(deviceManagement.protocol === B(2)) {
            output := response(7 - (counter.value * 4) - 3, 4 bits).asBits
          } otherwise {
            output(1) := response(7 - counter.value)
          }
        }
      }
    }
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
      when(counter =/= 4) {
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
      val command = Reg(UInt(8 bits)).init(0)
      val address = Reg(UInt(24 bits)).init(0)
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
      val offset = Reg(UInt(24 bits)).init(0)
      val response = UInt(8 bits)
      val counter = Counter(8)
      val data = Mem(UInt(8 bits), 16 MB)
      val output = Reg(Bool).init(False)
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
