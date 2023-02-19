package nafarr.peripherals.com.chip2chip

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._
import spinal.crypto.misc.LFSR
import nafarr.peripherals.com.linecode._
import nafarr.peripherals.com.chip2chip.phy.DataBlockContainer

object LinkLayer {

  case class LinkLayer(txIoPins: Int = 16, rxIoPins: Int = 16) extends Component {
    val io = new Bundle {
      val fromFrontend = slave(Stream(RawDataContainer(dataBlock * txIoPins)))
      val toPhy = master(Stream(DataBlockContainer(txIoPins)))
      val fromPhy = slave(Stream(DataBlockContainer(rxIoPins)))
      val toFrontend = master(Stream(RawDataContainer(dataBlock * rxIoPins)))
    }

    val outgoing = new Area {
      val stall = Bool()
      val stallOneTwo = Bool() // Stall pipeline stage one + two
      val lfsr = Reg(Bits(32 bits)).init(10)

      val stageHamming = new Area {
        val output = Vec(Reg(Bits(dataBlock * 2 bits)), txIoPins)
        val forSim = output.asBits
        for (index <- 0 until txIoPins) {
          val hammingCode = new HammingCode1611.Encoder()
          hammingCode.io.dataword := io.fromFrontend.payload.data.subdivideIn(8 bits)(index)
          val data = B"00" ## lfsr(0) ## hammingCode.io.codeword // fill with zeros to fit 16 bit

          when(!stallOneTwo) {
            when(io.fromFrontend.payload.fec) {
              output(index) := data
            } otherwise {
              output(index) := B"00000000" ##
                io.fromFrontend.payload.data.subdivideIn(8 bits)(index)
            }
          }
        }
        val kWord = RegNextWhen(io.fromFrontend.payload.kWord, !stallOneTwo).init(False)
        val fec = RegNextWhen(io.fromFrontend.payload.fec, !stallOneTwo).init(False)

        val valid = RegNextWhen(io.fromFrontend.valid, !stallOneTwo).init(False)
      }

      val stageScrambled = new Area {
        when(!stallOneTwo && stageHamming.valid && stageHamming.fec) {
          lfsr := LFSR.Fibonacci(lfsr, Seq(32, 22, 2, 1))
        }

        val output = Vec(Reg(Bits(dataBlock * 2 bits)), txIoPins)
        val forSim = output.asBits
        for (index <- 0 until txIoPins) {
          when(!stallOneTwo) {
            when(stageHamming.fec) {
              output(index) := stageHamming.output(index) ^ lfsr(index + 15 downto index)
            } otherwise {
              output(index) := stageHamming.output(index)
            }
          }

        }
        val kWord = RegNextWhen(stageHamming.kWord, !stallOneTwo).init(False)
        val fec = RegNextWhen(stageHamming.fec, !stallOneTwo).init(False)

        val valid = RegNextWhen(stageHamming.valid, !stallOneTwo).init(False)
      }

      val stageSerialize = new Area {
        val stallForSerialize = False
        val flip = RegInit(False)
        val output = Vec(Reg(Bits(dataBlock bits)), txIoPins)
        val forSim = output.asBits

        val kWord = Reg(Bool()).init(False)
        val fec = Reg(Bool()).init(False)

        val valid = Reg(Bool()).init(False)

        when(!stall) {
          when(stageScrambled.valid && stageScrambled.fec && !flip) {
            stallForSerialize := True
            flip := True
          }

          kWord := stageScrambled.kWord
          fec := stageScrambled.fec
          valid := stageScrambled.valid
        }

        for (index <- 0 until txIoPins) {
          when(!stall) {
            when(!flip) {
              output(index) := stageScrambled.output(index)(7 downto 0)
            } otherwise {
              output(index) := stageScrambled.output(index)(15 downto 8)
              flip := False
            }
          }
        }

      }

      val stage8b10b = new Area {
        val output = Vec(Bits(encodedBlock bits), txIoPins)
        val forSim = output.asBits
        for (index <- 0 until txIoPins) {
          val encoder8b10b = new Encoding8b10b.Encoder()
          encoder8b10b.io.stall := stall
          when(!stall && stageSerialize.valid) {
            encoder8b10b.io.data := stageSerialize.output(index)
          } otherwise {
            encoder8b10b.io.data := B"00000000"
          }
          encoder8b10b.io.kWord := stageSerialize.kWord

          output(index) := encoder8b10b.io.encoded
        }
        val fec1 = RegNextWhen(stageSerialize.fec, !stall).init(False)
        val fec = RegNextWhen(fec1, !stall).init(False)

        val valid1 = RegNextWhen(stageSerialize.valid, !stall).init(False)
        val valid = RegNextWhen(valid1, !stall).init(False)
      }

      val outputForSim = stage8b10b.output(0)
      io.toPhy.valid := stage8b10b.valid
      io.toPhy.payload.data := stage8b10b.output
      io.toPhy.payload.aux := True
      io.toPhy.payload.fec := stage8b10b.fec

      io.fromFrontend.ready := !stallOneTwo

      stall := stage8b10b.valid && !io.toPhy.ready
      stallOneTwo := stall || stageSerialize.stallForSerialize
    }

    val incoming = new Area {
      val stall = Bool()
      val lfsr = Reg(Bits(32 bits)).init(10)

      val stage10b8b = new Area {
        val output = Vec(Reg(Bits(dataBlock bits)), rxIoPins)
        val forSim = output.asBits
        val kWords = Vec(Bool(), rxIoPins)
        for (index <- 0 until rxIoPins) {
          val decoder10b8b = new Encoding8b10b.Decoder()
          decoder10b8b.io.stall := stall
          when(!stall && io.fromPhy.fire) {
            decoder10b8b.io.encoded := io.fromPhy.payload.data(index)
          } otherwise {
            decoder10b8b.io.encoded := B"0110101100"
          }
          when(!stall) {
            output(index) := decoder10b8b.io.data
          }
          kWords(index) := decoder10b8b.io.kWord
        }
        val fec1 = RegNextWhen(io.fromPhy.payload.fec, !stall).init(False)
        val fec = RegNextWhen(fec1, !stall).init(False)
        val kWord = RegNextWhen(kWords(0), !stall).init(False)

        val valid1 = RegNextWhen(io.fromPhy.fire && io.fromPhy.payload.aux, !stall).init(False)
        val valid = RegNextWhen(valid1, !stall).init(False)
      }

      val stageCombine = new Area {
        val output = Vec(Reg(Bits(dataBlock * 2 bits)), rxIoPins)
        val forSim = output.asBits
        val fec = Reg(Bool()).init(False)
        val kWord = Reg(Bool()).init(False)
        val valid = Reg(Bool()).init(False)

        val fsm = new StateMachine {
          val single: State = new State with EntryPoint {
            whenIsActive {
              when(!stall) {
                for (index <- 0 until rxIoPins) {
                  output(index)(7 downto 0) := stage10b8b.output(index)
                  output(index)(15 downto 8) := B"00110011"
                }
                fec := stage10b8b.fec
                kWord := stage10b8b.kWord
                valid := stage10b8b.valid
                when(stage10b8b.valid && stage10b8b.fec) {
                  valid := False
                  goto(double)
                }
              }
            }
          }
          val double: State = new State {
            whenIsActive {
              // TODO
              when(!stall && stage10b8b.valid) {
                for (index <- 0 until rxIoPins) {
                  output(index)(15 downto 8) := stage10b8b.output(index)
                }
                valid := True
                goto(single)
              }
            }
          }
        }
      }

      val stageDescrambled = new Area {
        when(!stall && stageCombine.valid && stageCombine.fec) {
          lfsr := LFSR.Fibonacci(lfsr, Seq(32, 22, 2, 1))
        }

        val output = Vec(Reg(Bits(16 bits)), rxIoPins)
        val forSim = output.asBits
        for (index <- 0 until rxIoPins) {
          when(!stall) {
            when(stageCombine.fec) {
              output(index) := stageCombine.output(index) ^ lfsr(index + 15 downto index)
            } otherwise {
              output(index) := stageCombine.output(index)
            }
          }
        }
        val fec = RegNextWhen(stageCombine.fec, !stall).init(False)
        val kWord = RegNextWhen(stageCombine.kWord, !stall).init(False)

        val valid = RegNextWhen(stageCombine.valid, !stall).init(False)
      }

      val stageHamming = new Area {
        val output = Vec(Reg(Bits(dataBlock bits)), rxIoPins)
        val forSim = output.asBits
        for (index <- 0 until rxIoPins) {
          val hammingCode = new HammingCode1611.Decoder()
          hammingCode.io.codeword := stageDescrambled.output(index)(12 downto 0)

          when(!stall) {
            when(stageDescrambled.fec) {
              output(index) := hammingCode.io.dataword
            } otherwise {
              output(index) := stageDescrambled.output(index)(7 downto 0)
            }
          }
        }
        val fec = RegNextWhen(stageDescrambled.fec, !stall).init(False)
        val kWord = RegNextWhen(stageDescrambled.kWord, !stall).init(False)

        val valid = RegNextWhen(stageDescrambled.valid, !stall).init(False)
      }

      stall := stageHamming.valid && !io.toFrontend.ready

      io.fromPhy.ready := !stall

      io.toFrontend.payload.data := stageHamming.output.asBits
      io.toFrontend.payload.kWord := stageHamming.kWord
      io.toFrontend.payload.fec := stageHamming.fec
      io.toFrontend.valid := stageHamming.valid
    }
  }
}
