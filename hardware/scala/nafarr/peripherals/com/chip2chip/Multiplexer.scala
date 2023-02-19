package nafarr.peripherals.com.chip2chip

import scala.math._

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._
import spinal.lib.bus.amba4.axi._

case class Multiplexer(inputs: Int, outputs: Int) extends Component {
  require(inputs >= outputs)
  val io = new Bundle {
    val fromFrontend = slave(Stream(Vec(Bits(128 + 1 bits), inputs)))
    val toLinkLayer = Vec(master(Stream(Bits(128 bits))), outputs)
    val fromLinkLayer = Vec(slave(Stream(Bits(128 bits))), outputs)
    val toFrontend = master(Stream(Vec(Bits(128 bits), inputs)))
  }
  val cycles = ceil(inputs / outputs.toFloat).toInt

  if (inputs == outputs) {
    val fromFrontend = new Area {
      val acks = Reg(Bits(outputs bits)).init(B(0, outputs bits))
      io.fromFrontend.ready := acks.andR

      for (index <- 0 until outputs) {
        io.toLinkLayer(index).payload := B(0, 128 bits)
        io.toLinkLayer(index).valid := False

        val fsm = new StateMachine {
          val init: State = new State with EntryPoint {
            whenIsActive {
              acks(index) := False
              when(io.fromFrontend.valid) {
                io.toLinkLayer(index).payload := io.fromFrontend.payload(index)(127 downto 0)
                io.toLinkLayer(index).valid := True
                when(io.toLinkLayer(index).ready) {
                  acks(index) := True
                  goto(acknowledge)
                }
              }
            }
          }
          val acknowledge: State = new State {
            whenIsActive {
              when(acks === ~B(0, outputs bits)) {
                acks(index) := False
                goto(init)
              }
            }
          }
        }
      }
    }
    val toFrontend = new Area {
      val payload = Reg(Vec(Bits(128 bits), inputs))
      val push = Stream(Vec(Bits(128 bits), inputs))
      push.payload := payload
      io.toFrontend << push
      val acks = Reg(Bits(outputs bits)).init(B(0, outputs bits))
      push.valid := acks.andR

      for (index <- 0 until outputs) {
        payload(index).init(B(0, 128 bits))
        io.fromLinkLayer(index).ready := False

        val fsm = new StateMachine {
          val init: State = new State with EntryPoint {
            whenIsActive {
              acks(index) := False
              when(io.fromLinkLayer(index).valid) {
                payload(index) := io.fromLinkLayer(index).payload
                io.fromLinkLayer(index).ready := True
                acks(index) := True
                goto(acknowledge)
              }
            }
          }
          val acknowledge: State = new State {
            whenIsActive {
              when(io.toFrontend.fire) {
                acks(index) := False
                goto(init)
              }
            }
          }
        }
      }
    }
  } else {
    val fromFrontend = new Area {
      val acks = Reg(Bits(outputs bits)).init(B(0, outputs bits))
      io.fromFrontend.ready := acks.andR

      for (index <- 0 until outputs) {
        val idx = Reg(UInt(log2Up(inputs) + 1 bits))
        val counter = Reg(UInt(log2Up(cycles) + 1 bits))
        io.toLinkLayer(index).payload := B(0, 128 bits)
        io.toLinkLayer(index).valid := False

        val fsm = new StateMachine {
          val init: State = new State with EntryPoint {
            whenIsActive {
              acks(index) := False
              idx := index + outputs
              counter := 1
              when(io.fromFrontend.valid) {
                io.toLinkLayer(index).payload := io.fromFrontend.payload(index)(127 downto 0)
                io.toLinkLayer(index).valid := True
                when(io.toLinkLayer(index).ready) {
                  goto(sendFurther)
                }
              }
            }
          }
          val sendFurther: State = new State {
            whenIsActive {
              when(idx < inputs) {
                io.toLinkLayer(index).payload := io.fromFrontend.payload(
                  idx(0, log2Up(inputs) bits)
                )(127 downto 0)
              }
              io.toLinkLayer(index).valid := True
              when(io.toLinkLayer(index).ready) {
                idx := idx + outputs
                counter := counter + 1
                when(counter === cycles - 1) {
                  acks(index) := True
                  goto(acknowledge)
                }
              }
            }
          }
          val acknowledge: State = new State {
            whenIsActive {
              when(acks === ~B(0, outputs bits)) {
                acks(index) := False
                goto(init)
              }
            }
          }
        }
      }
    }

    val toFrontend = new Area {
      val payload = Reg(Vec(Bits(128 bits), inputs))
      val push = Stream(Vec(Bits(128 bits), inputs))
      push.payload := payload
      io.toFrontend << push
      val acks = Reg(Bits(outputs bits)).init(B(0, outputs bits))
      push.valid := acks.andR

      for (index <- 0 until outputs) {
        val idx = Reg(UInt(log2Up(inputs) + 1 bits))
        val counter = Reg(UInt(log2Up(cycles) + 1 bits))
        payload(index).init(B(0, 128 bits))
        io.fromLinkLayer(index).ready := False

        val fsm = new StateMachine {
          val init: State = new State with EntryPoint {
            whenIsActive {
              acks(index) := False
              idx := index + outputs
              counter := 1
              when(io.fromLinkLayer(index).valid) {
                payload(index) := io.fromLinkLayer(index).payload
                io.fromLinkLayer(index).ready := True
                goto(receiveFurther)
              }
            }
          }
          val receiveFurther: State = new State {
            whenIsActive {
              when(io.fromLinkLayer(index).valid) {
                when(idx < inputs) {
                  payload(idx(0, log2Up(inputs) bits)) := io.fromLinkLayer(index).payload
                }
                io.fromLinkLayer(index).ready := True
                idx := idx + outputs
                counter := counter + 1
                when(counter === cycles - 1) {
                  acks(index) := True
                  goto(acknowledge)
                }
              }
            }
          }
          val acknowledge: State = new State {
            whenIsActive {
              when(io.toFrontend.fire) {
                acks(index) := False
                goto(init)
              }
            }
          }
        }
      }
    }
  }
}
