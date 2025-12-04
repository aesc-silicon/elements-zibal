// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal.sim.hyperram

import spinal.core._
import spinal.core.sim._
import spinal.lib._

object W956A8MBYA {
  def apply() = W956A8MBYA()

  case class W956A8MBYA() extends Component {
    val io = new Bundle {
      val clock = in(Bool)
      val ck = in(Bool)
      val ckN = in(Bool)
      val dqIn = in(Bits(8 bits))
      val dqOut = out(Bits(8 bits))
      val rwdsIn = in(Bool)
      val rwdsOut = out(Bool)
      val csN = in(Bool)
      val resetN = in(Bool)
    }

    val dummyClockDomain2 = ClockDomain(
      clock = io.clock,
      reset = io.csN,
      config = ClockDomainConfig(
        resetKind = ASYNC,
        resetActiveLevel = HIGH
      )
    )

    object HyperBusState extends SpinalEnum {
      val START, LATENCY, READ = newElement()
    }

    val device = new ClockingArea(dummyClockDomain2) {
      val state = RegInit(HyperBusState.START)
      val counter = CounterFreeRun(512)
      val rwds = Reg(Bool).init(True)
      val data = Mem(UInt(8 bits), 8 MB)
      val output = Reg(Bits(8 bits)).init(B"00000000")
      val address = Reg(UInt(log2Up(8 MB) bits)).init(0)

      switch(state) {
        is(HyperBusState.START) {
          when(counter.value === 27) {
            rwds := False
            counter.clear()
            state := HyperBusState.LATENCY
          }
        }
        is(HyperBusState.LATENCY) {
          when(counter.value === 104) {
            counter.clear()
            state := HyperBusState.READ
            rwds := True
            output := data(address).asBits
            address := address + 1
          }
        }
        is(HyperBusState.READ) {
          when(counter.value === 3) {
            rwds := !rwds
            output := data(address).asBits
            address := address + 1
            counter.clear()
          }
        }
      }
    }
    io.rwdsOut := device.rwds
    io.dqOut := device.output
  }
}
