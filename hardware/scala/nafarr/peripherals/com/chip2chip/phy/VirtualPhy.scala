package nafarr.peripherals.com.chip2chip.phy

import spinal.core._
import spinal.lib._

object VirtualPhy {

  case class Io(ioPins: Int = 16) extends Bundle with IMasterSlave {
    val data = Bits(ioPins bits)
    val aux = Bool()
    val fec = Bool()

    override def asMaster() = {
      out(data, aux, fec)
    }
    override def asSlave() = {
      in(data, aux, fec)
    }

    def <<(that: Io) = that >> this
    def >>(that: Io) = {
      that.data := this.data
      that.aux := this.aux
      that.fec := this.fec
    }
  }

  case class Tx(ioPins: Int = 16) extends Component {
    val io = new Bundle {
      val phy = master(Io(ioPins))
      val fromLinkLayer = slave(Stream(DataBlockContainer(ioPins)))
      val enable = in(Bool())
    }

    val data = Reg(Bits(ioPins bits)).init(B(0, ioPins bits))
    val aux = Reg(Bool()).init(False)
    val fec = Reg(Bool()).init(False)

    val indexCounter = Reg(UInt(log2Up(10) bits)).init(0)
    val run = Reg(Bool()).init(False)
    val validCycle = indexCounter === 0 && io.enable && io.fromLinkLayer.valid

    io.fromLinkLayer.ready := False
    indexCounter := indexCounter + 1
    when(validCycle) {
      run := True
    }
    when(indexCounter === 9) {
      indexCounter := 0
      run := False
      when(run) {
        io.fromLinkLayer.ready := True
      }
    }

    aux := False
    fec := False
    for (index <- 0 until ioPins) {
      data(index) := False
      when(validCycle || run) {
        data(index) := io.fromLinkLayer.payload.data(index)(indexCounter)
        aux := io.fromLinkLayer.payload.aux
        fec := io.fromLinkLayer.payload.fec
      }
    }
    io.phy.data := data
    io.phy.aux := aux
    io.phy.fec := fec
  }

  case class Rx(ioPins: Int = 16) extends Component {
    val io = new Bundle {
      val phy = slave(Io(ioPins))
      val fromPhy = master(Stream(DataBlockContainer(ioPins)))
      val locked = out(Bool())
      val pushError = out(Bool())
    }

    val indexCounter = Reg(UInt(log2Up(10) bits))
    val locked = Reg(Bool()).init(False)
    val run = Reg(Bool()).init(False)
    val sync = B"1001111100"
    val syncInv = B"0110000011"

    val findSync = Reg(Bits(20 bits)).init(B(0, 20 bits))
    val findSyncNext = findSync(18 downto 0) ## io.phy.data(0)
    findSync := findSyncNext

    val container = Reg(DataBlockContainer(ioPins))
    val tmp = Reg(Bits(ioPins bits))
    io.fromPhy.payload := container

    io.fromPhy.valid := False
    indexCounter := indexCounter + 1
    when(locked) {
      when(indexCounter === 0) {
        when(run) {
          io.fromPhy.valid := True
        }
        for (index <- 0 until ioPins) {
          tmp(index) := io.phy.data(index)
        }
      } otherwise {
        for (index <- 0 until ioPins) {
          container.data(index)(0) := tmp(index)
          container.data(index)(indexCounter) := io.phy.data(index)
        }
      }
      when(indexCounter === 9) {
        run := True
        indexCounter := 0
        container.aux := io.phy.aux
        container.fec := io.phy.fec
      }
    } otherwise {
      when(
        (findSyncNext(9 downto 0) === sync || findSyncNext(9 downto 0) === syncInv) &&
          (findSyncNext(19 downto 10) === sync || findSyncNext(19 downto 10) === syncInv)
      ) {
        indexCounter := 0
        locked := True
        run := False
      }
    }

    val pushError = Reg(Bool()).init(False)
    pushError := False
    when(io.fromPhy.valid && !io.fromPhy.ready) {
      pushError := True
    }
    io.pushError := pushError

    io.locked := locked
  }
}
