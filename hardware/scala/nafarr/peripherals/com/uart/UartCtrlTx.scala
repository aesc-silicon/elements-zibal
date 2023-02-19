package nafarr.peripherals.com.uart

import spinal.core._
import spinal.lib._

object UartCtrlTx {
  def apply(p: UartCtrl.Parameter = UartCtrl.Parameter.default) = UartCtrlTx(p)

  object State extends SpinalEnum {
    val IDLE, START, DATA, PARITY, STOP = newElement()
  }

  case class Io(p: UartCtrl.Parameter) extends Bundle {
    val config = in(UartCtrl.FrameConfig(p))
    val samplingTick = in(Bool)
    val write = slave(Stream(Bits(p.dataWidthMax bits)))
    val txd = out(Bool)
    val cts = in(Bool)
  }

  case class UartCtrlTx(p: UartCtrl.Parameter) extends Component {
    val io = Io(p)
    val txEnable = RegInit(True)

    val txCtrl = new ClockEnableArea(txEnable) {

      val clockDivider = new Area {
        val counter = Counter(p.samplesPerBit)
        val tick = counter.willOverflow
        when(io.samplingTick) {
          counter.increment()
        }
      }

      val tickCounter = new Area {
        val value = Reg(UInt(Math.max(log2Up(p.dataWidthMax), 2) bit))
        def reset() = value := 0
        when(clockDivider.tick) {
          value := value + 1
        }
      }

      val stateMachine = new Area {
        val state = RegInit(State.IDLE)
        val parity = Reg(Bool)
        val txd = True

        when(clockDivider.tick) {
          parity := parity ^ txd
        }

        io.write.ready := False
        switch(state) {
          is(State.IDLE) {
            when(io.write.valid && clockDivider.tick && io.cts) {
              state := State.START
            }
          }
          is(State.START) {
            txd := False
            when(clockDivider.tick) {
              state := State.DATA
              parity := io.config.parity === Uart.ParityType.ODD
              tickCounter.reset()
            }
          }
          is(State.DATA) {
            txd := io.write.payload(tickCounter.value)
            when(clockDivider.tick) {
              when(tickCounter.value === io.config.dataLength) {
                io.write.ready := True
                tickCounter.reset()
                when(io.config.parity === Uart.ParityType.NONE) {
                  state := State.STOP
                } otherwise {
                  state := State.PARITY
                }
              }
            }
          }
          is(State.PARITY) {
            txd := parity
            when(clockDivider.tick) {
              state := State.STOP
              tickCounter.reset()
            }
          }
          is(State.STOP) {
            when(clockDivider.tick) {
              val stopBits = Uart.StopType.toBitCount(io.config.stop)
              when(tickCounter.value === stopBits) {
                state := io.write.valid ? State.START | State.IDLE
              }
            }
          }
        }
      }
    }
    when(io.write.valid || !(txCtrl.stateMachine.state === State.IDLE)) {
      txEnable := True
    } otherwise {
      txEnable := False
    }
    io.txd := RegNext(txCtrl.stateMachine.txd).init(True)
  }

}
