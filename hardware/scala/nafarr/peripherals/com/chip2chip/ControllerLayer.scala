package nafarr.peripherals.com.chip2chip

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._

object ControllerLayer {

  case class ControllerLayer(
      txIoPins: Int = 16,
      rxIoPins: Int = 16,
      outputDepth: Int = 4,
      inputDepth: Int = 8,
      transactionsDepth: Int = 8,
      responsesDepth: Int = 8
  ) extends Component {
    val io = new Bundle {
      val fromFrontend = slave(Stream(Bits(dataBlock * txIoPins bits)))
      val toLinkLayer = master(Stream(RawDataContainer(dataBlock * txIoPins)))
      val fromLinkLayer = slave(Stream(RawDataContainer(dataBlock * txIoPins)))
      val toFrontend = master(Stream(Bits(dataBlock * txIoPins bits)))
    }

    val syncWord = B"00111100"

    val fetchWord = B"01011100"
    val sizeWord = B"01111100"
    val msgWord = B"10011100"
    val stopWord = B"11011100"
    val ackWord = B"11110111"
    val nackWord = B"11111011"

    def concat(list: List[Bits]): Bits = list match {
      case Nil => B""
      case x :: xs => x ## concat(xs)
    }

    def multiply(data: Bits, n: Int): Bits = concat(List.tabulate(n)(_ => data))

    io.fromFrontend.ready := False

    val responses = StreamFifo(RawDataContainer(dataBlock), responsesDepth)
    responses.io.push.payload.data := B"00000000"
    responses.io.push.payload.kWord := False
    responses.io.push.payload.fec := False
    responses.io.push.valid := False
    responses.io.pop.ready := False

    val output = StreamFifo(RawDataContainer(dataBlock * txIoPins), outputDepth)
    val outputFrame = RawDataContainer(dataBlock * txIoPins)
    io.toLinkLayer << output.io.pop
    output.io.push.payload := outputFrame
    output.io.push.valid := False
    outputFrame.data := B(dataBlock * txIoPins bits, default -> False)
    outputFrame.kWord := False
    outputFrame.fec := False

    val tx = new StateMachine {
      val counter = Reg(UInt(dataBlock bits)).init(0)
      val timeout = Reg(UInt(dataBlock bits)).init(0)
      val scrambleWord = counter.asBits

      val freeSpace = Reg(UInt(8 bits)).init(0)
      val canSend = Reg(Bool()).init(False)

      val transactions = StreamFifo(Bits(dataBlock * txIoPins bits), transactionsDepth)
      transactions.io.flush := False
      transactions.io.pop.ready := False
      transactions.io.push.payload := B(dataBlock * txIoPins bits, default -> False)
      transactions.io.push.valid := False

      val sync: State = new State with EntryPoint {
        whenIsActive {
          outputFrame.data := multiply(syncWord, txIoPins)
          outputFrame.kWord := True
          outputFrame.fec := False
          output.io.push.valid := True
          when(output.io.push.ready) {
            counter := counter + 1
            when(counter === U(1)) {
              goto(scramble)
            }
          }
        }
      }
      val scramble: State = new State {
        onEntry(counter := 0)
        whenIsActive {
          // TODO use random data. Idea: Do not init timeout and use this register. It will be set
          // to 0 in the message stage and not elsewhere.
          outputFrame.data := multiply(scrambleWord, txIoPins)
          outputFrame.kWord := False
          outputFrame.fec := True
          output.io.push.valid := True
          when(output.io.push.ready) {
            counter := counter + 1
            when(counter === U(9)) {
              goto(start)
            }
          }
        }
      }
      val start: State = new State {
        whenIsActive {
          outputFrame.data := multiply(fetchWord, txIoPins)
          outputFrame.kWord := True
          outputFrame.fec := False
          output.io.push.valid := True
          when(output.io.push.ready) {
            goto(stall)
          }
        }
      }
      val stall: State = new State {
        whenIsActive {
          when(canSend && io.fromFrontend.valid) {
            goto(messageStart)
          } elsewhen (responses.io.pop.valid) {
            // Messages to send back other controller
            when(responses.io.pop.payload.data === fetchWord) {
              goto(fetchStart)
            }
            when(responses.io.pop.payload.data === sizeWord) {
              responses.io.pop.ready := True
              goto(size)
            }
            when(responses.io.pop.payload.data === ackWord) {
              // fec=1 - send ack back. Otherwise flush transactions and start new transmission.
              when(responses.io.pop.payload.fec) {
                outputFrame.data := multiply(responses.io.pop.payload.data, txIoPins)
                outputFrame.kWord := responses.io.pop.payload.kWord
                output.io.push.valid := True
                when(output.io.push.ready) {
                  responses.io.pop.ready := True
                }
              } otherwise {
                transactions.io.flush := True
                when(output.io.push.ready) {
                  responses.io.pop.ready := True
                  goto(start)
                }
              }
            }
          }
        }
      }
      val fetchStart: State = new State {
        whenIsActive {
          outputFrame.data := multiply(sizeWord, txIoPins)
          outputFrame.kWord := True
          output.io.push.valid := True
          when(output.io.push.ready) {
            responses.io.pop.ready := True
            goto(fetch)
          }
        }
      }
      val fetch: State = new State {
        onEntry(counter := 0)
        whenIsActive {
          outputFrame.data := multiply(responses.io.pop.payload.data, txIoPins)
          outputFrame.kWord := responses.io.pop.payload.kWord
          when(responses.io.pop.valid) {
            output.io.push.valid := True
            when(output.io.push.ready) {
              responses.io.pop.ready := True
              counter := counter + 1
              when(counter === U(1)) {
                goto(stall)
              }
            }
          }
        }
      }
      val size: State = new State {
        onEntry(counter := 0)
        whenIsActive {
          when(counter === U(0)) {
            freeSpace := responses.io.pop.payload.data.asUInt - 1
            canSend := True
            responses.io.pop.ready := True
            when(responses.io.pop.fire) {
              counter := counter + 1
            }
          }
          when(counter === U(1)) {
            responses.io.pop.ready := True
            when(responses.io.pop.fire) {
              goto(stall)
            }
          }
        }
      }
      val messageStart: State = new State {
        whenIsActive {
          outputFrame.data := multiply(msgWord, txIoPins)
          outputFrame.kWord := True
          output.io.push.valid := True
          when(output.io.push.ready) {
            goto(message)
          }
        }
      }
      val message: State = new State {
        onEntry {
          counter := 0
          timeout := 0
        }
        whenIsActive {
          when(io.fromFrontend.valid) {
            when(counter(0) === False) {
              transactions.io.push.payload := io.fromFrontend.payload
              transactions.io.push.valid := True
              when(transactions.io.push.fire) {
                counter := counter + 1
              }
            }
            when(counter(0) === True) {
              outputFrame.data := io.fromFrontend.payload
              outputFrame.kWord := False
              outputFrame.fec := True
              output.io.push.valid := True
              when(output.io.push.fire) {
                io.fromFrontend.ready := True
                counter := counter + 1
                when((counter >> 1) === freeSpace || transactions.io.availability === 0) {
                  goto(messageStop)
                }
              }
            }
          } otherwise {
            timeout := timeout + 1
            when(timeout === U(16)) {
              goto(messageStop)
            }
          }
        }
      }
      val messageStop: State = new State {
        whenIsActive {
          outputFrame.data := multiply(stopWord, txIoPins)
          outputFrame.kWord := True
          canSend := False
          output.io.push.valid := True
          when(output.io.push.ready) {
            goto(stall)
          }
        }
      }
    }

    val input = StreamFifo(RawDataContainer(dataBlock * txIoPins), inputDepth)
    input.io.push << io.fromLinkLayer
    input.io.pop.ready := False

    io.toFrontend.payload := B(dataBlock * txIoPins bits, default -> False)
    io.toFrontend.valid := False

    val rx = new StateMachine {
      val counter = Reg(UInt(dataBlock bits)).init(0)

      val scramble: State = new State with EntryPoint {
        onEntry(counter := 0)
        whenIsActive {
          when(input.io.pop.valid) {
            input.io.pop.ready := True
            counter := counter + 1
            when(counter === U(9)) {
              goto(default)
            }
          }
        }
      }
      val default: State = new State {
        onEntry(counter := 0)
        whenIsActive {
          when(input.io.pop.valid) {
            when(input.io.pop.payload.data === multiply(fetchWord, txIoPins)) {
              input.io.pop.ready := True
              goto(fetch)
            }
            when(input.io.pop.payload.data === multiply(sizeWord, txIoPins)) {
              goto(size)
            }
            when(input.io.pop.payload.data === multiply(ackWord, txIoPins)) {
              goto(ack)
            }
            when(input.io.pop.payload.data === multiply(msgWord, txIoPins)) {
              input.io.pop.ready := True
              goto(message)
            }
          }
        }
      }
      val fetch: State = new State {
        onEntry(counter := 0)
        whenIsActive {
          responses.io.push.valid := True
          when(counter === U(0)) {
            responses.io.push.payload.data := fetchWord
            responses.io.push.payload.kWord := True
            when(responses.io.push.fire) {
              counter := counter + 1
            }
          }
          when(counter === U(1)) {
            responses.io.push.payload.data := input.io.availability.asBits.resized
            responses.io.push.payload.kWord := True
            when(responses.io.push.fire) {
              counter := counter + 1
            }
          }
          when(counter === U(2)) {
            responses.io.push.payload.data := ackWord
            responses.io.push.payload.kWord := False
            when(responses.io.push.fire) {
              goto(default)
            }
          }
        }
      }
      val size: State = new State {
        onEntry(counter := 0)
        whenIsActive {
          when(input.io.pop.valid) {
            responses.io.push.valid := True
            responses.io.push.payload.data := input.io.pop.payload.data(7 downto 0)
            responses.io.push.payload.kWord := input.io.pop.payload.kWord
            when(responses.io.push.fire) {
              input.io.pop.ready := True
              counter := counter + 1
              when(counter === U(2)) {
                goto(default)
              }
            }
          }
        }
      }
      val message: State = new State {
        onEntry(counter := 0)
        whenIsActive {
          when(input.io.pop.valid) {
            when(input.io.pop.payload.data === multiply(stopWord, txIoPins)) {
              // TODO error handling
              responses.io.push.payload.data := ackWord
              responses.io.push.payload.kWord := True
              responses.io.push.payload.fec := True
              responses.io.push.valid := True
              when(responses.io.push.fire) {
                input.io.pop.ready := True
                goto(default)
              }
            } otherwise {
              io.toFrontend.payload := input.io.pop.payload.data
              io.toFrontend.valid := input.io.pop.valid
              input.io.pop.ready := io.toFrontend.ready
            }
          }
        }
      }
      val ack: State = new State {
        onEntry(counter := 0)
        whenIsActive {
          when(input.io.pop.valid) {
            responses.io.push.valid := True
            responses.io.push.payload.data := input.io.pop.payload.data(7 downto 0)
            responses.io.push.payload.kWord := input.io.pop.payload.kWord
            responses.io.push.payload.fec := False
            when(responses.io.push.fire) {
              input.io.pop.ready := True
              goto(default)
            }
          }
        }
      }
    }
  }
}
