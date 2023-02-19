package nafarr.peripherals.com.spi

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba4.axi._

object SpiXipMasterCtrl {
  def apply(p: SpiCtrl.Parameter, dataBusConfig: Axi4Config) = SpiMasterCtrl(p, dataBusConfig)

  object State extends SpinalEnum {
    val IDLE, ENABLESPI, COMMAND, ADDRESS, DATA, DISABLESPI, RESPONSE = newElement()
  }

  case class Io(p: SpiCtrl.Parameter, dataBusConfig: Axi4Config) extends Bundle {
    val bus = slave(Axi4Shared(dataBusConfig))
    val cmd = master(Stream(SpiMaster.Cmd(p)))
    val rsp = slave(Flow(Bits(p.dataWidth bits)))
  }

  case class SpiMasterCtrl(p: SpiCtrl.Parameter, dataBusConfig: Axi4Config) extends Component {
    val io = Io(p, dataBusConfig)

    val cmdStream = Stream(SpiMaster.Cmd(p))
    cmdStream.valid := False
    cmdStream.payload.mode := SpiMaster.CmdMode.DATA
    cmdStream.payload.args := 0

    io.cmd << cmdStream

    val rspHandler = new Area {
      val data = Reg(Bits(32 bits))
      val counter = Reg(UInt(2 bits)).init(0)

      when(io.rsp.valid) {
        data(8 * counter, 8 bits) := io.rsp.payload.asBits
        counter := counter + 1
      }
    }

    io.bus.readRsp.valid := False
    io.bus.readRsp.data := rspHandler.data
    io.bus.readRsp.setOKAY()
    io.bus.readRsp.last := True

    val stateMachine = new Area {
      val state = RegInit(State.IDLE)
      val addr = Reg(UInt(24 bits))
      val counter = Reg(UInt(2 bits))

      io.bus.arw.ready := False
      switch(state) {
        is(State.IDLE) {
          when(io.bus.arw.valid) {
            io.bus.arw.ready := True
            addr := io.bus.arw.addr.resize(24)
            state := State.ENABLESPI
          }
        }
        is(State.ENABLESPI) {
          val enableSpi = SpiMaster.CmdSs(p)
          enableSpi.enable := True
          enableSpi.index := 0

          cmdStream.valid := True
          cmdStream.payload.mode := SpiMaster.CmdMode.SS
          cmdStream.payload.args.assignFromBits(enableSpi.asBits.resized)

          when(cmdStream.ready) {
            state := State.COMMAND
          } otherwise {
            state := State.ENABLESPI
          }
        }
        is(State.COMMAND) {
          val readCommand = SpiMaster.CmdData(p)
          readCommand.read := False
          readCommand.data := 3

          cmdStream.valid := True
          cmdStream.payload.mode := SpiMaster.CmdMode.DATA
          cmdStream.payload.args.assignFromBits(readCommand.asBits)

          counter := 2
          when(cmdStream.ready) {
            state := State.ADDRESS
          } otherwise {
            state := State.COMMAND
          }
        }
        is(State.ADDRESS) {
          val addressCommand = SpiMaster.CmdData(p)
          addressCommand.read := False
          addressCommand.data := addr(8 * counter, 8 bits).asBits

          cmdStream.valid := True
          cmdStream.payload.mode := SpiMaster.CmdMode.DATA
          cmdStream.payload.args.assignFromBits(addressCommand.asBits)

          when(cmdStream.ready) {
            when(counter === 0) {
              counter := 3
              state := State.DATA
            } otherwise {
              counter := counter - 1
              state := State.ADDRESS
            }
          } otherwise {
            state := State.ADDRESS
          }
        }
        is(State.DATA) {
          val dataCommand = SpiMaster.CmdData(p)
          dataCommand.read := True
          dataCommand.data := 0

          cmdStream.valid := True
          cmdStream.payload.mode := SpiMaster.CmdMode.DATA
          cmdStream.payload.args.assignFromBits(dataCommand.asBits)

          when(cmdStream.ready) {
            when(counter === 0) {
              counter := 3
              state := State.DISABLESPI
            } otherwise {
              counter := counter - 1
              state := State.DATA
            }
          } otherwise {
            state := State.DATA
          }
        }
        is(State.DISABLESPI) {
          val enableSpi = SpiMaster.CmdSs(p)
          enableSpi.enable := False
          enableSpi.index := 0

          cmdStream.valid := True
          cmdStream.payload.mode := SpiMaster.CmdMode.SS
          cmdStream.payload.args.assignFromBits(enableSpi.asBits.resized)

          when(cmdStream.ready) {
            state := State.RESPONSE
          } otherwise {
            state := State.DISABLESPI
          }
        }
        is(State.RESPONSE) {
          io.bus.readRsp.valid := True
          state := State.IDLE
        }
      }
    }
  }
}
