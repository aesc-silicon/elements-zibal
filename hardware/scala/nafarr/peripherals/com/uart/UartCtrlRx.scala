package nafarr.peripherals.com.uart

import spinal.core._
import spinal.lib._

object UartCtrlRx {
  def apply(p: UartCtrl.Parameter = UartCtrl.Parameter.default) = UartCtrlRx(p)

  object State extends SpinalEnum {
    val IDLE, START, DATA, PARITY, STOP = newElement()
  }

  case class Io(p: UartCtrl.Parameter) extends Bundle {
    val config = in(UartCtrl.FrameConfig(p))
    val samplingTick = in(Bool)
    val read = master(Stream(Bits(p.dataWidthMax bits)))
    val rxd = in(Bool)
  }

  case class UartCtrlRx(p: UartCtrl.Parameter) extends Component {
    val io = Io(p)

    // Implement the rxd sampling with a majority vote over samplingSize bits
    // Provide a new sampler.value each time sampler.tick is high
    val sampler = new Area {
      val synchroniser = BufferCC(io.rxd, init = False)
      val samples = History(
        that = synchroniser,
        length = p.samplingSize,
        when = io.samplingTick,
        init = True
      )
      val value = RegNext(MajorityVote(samples)).init(True)
      val tick = RegNext(io.samplingTick).init(False)
    }

    // Provide a bitTimer.tick each rxSamplePerBit
    // reset() can be called to recenter the counter over a start bit.
    val bitTimer = new Area {
      val counter = Reg(UInt(log2Up(p.samplesPerBit) bit))
      def reset() = counter := p.preSamplingSize + (p.samplingSize - 1) / 2 - 1
      val tick = False
      when(sampler.tick) {
        counter := counter - 1
        when(counter === 0) {
          tick := True
          if (!isPow2(p.samplesPerBit))
            counter := p.samplesPerBit - 1
        }
      }
    }

    // Provide bitCounter.value that count up each bitTimer.tick, Used by the state machine to count data bits and stop bits
    // reset() can be called to reset it to zero
    val bitCounter = new Area {
      val value = Reg(UInt(Math.max(log2Up(p.dataWidthMax), 2) bit))
      def reset() = value := 0

      when(bitTimer.tick) {
        value := value + 1
      }
    }

    val stateMachine = new Area {

      val state = RegInit(State.IDLE)
      val parity = Reg(Bool)
      val shifter = Reg(io.read.payload)
      val validReg = RegNext(False).init(False)
      io.read.valid := validReg

      // Parity calculation
      when(bitTimer.tick) {
        parity := parity ^ sampler.value
      }

      switch(state) {
        is(State.IDLE) {
          when(sampler.tick && !sampler.value) {
            state := State.START
            bitTimer.reset()
          }
        }
        is(State.START) {
          when(bitTimer.tick) {
            state := State.DATA
            bitCounter.reset()
            parity := io.config.parity === Uart.ParityType.ODD
            shifter := 0
            when(sampler.value === True) {
              state := State.IDLE
            }
          }
        }
        is(State.DATA) {
          when(bitTimer.tick) {
            shifter(bitCounter.value) := sampler.value
            when(bitCounter.value === io.config.dataLength) {
              bitCounter.reset()
              when(io.config.parity === Uart.ParityType.NONE) {
                state := State.STOP
                validReg := True
              } otherwise {
                state := State.PARITY
              }
            }
          }
        }
        is(State.PARITY) {
          when(bitTimer.tick) {
            bitCounter.reset()
            when(parity === sampler.value) {
              state := State.STOP
              validReg := True
            } otherwise {
              state := State.IDLE
            }
          }
        }
        is(State.STOP) {
          when(bitTimer.tick) {
            val stopBits = Uart.StopType.toBitCount(io.config.stop)
            when(!sampler.value) {
              state := State.IDLE
            } elsewhen (bitCounter.value === stopBits) {
              state := State.IDLE
            }
          }
        }
      }
    }
    io.read.payload := stateMachine.shifter
  }
}
