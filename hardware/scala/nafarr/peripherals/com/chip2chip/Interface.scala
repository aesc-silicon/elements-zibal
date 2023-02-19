package nafarr.peripherals.com.chip2chip

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba4.axi._
import nafarr.peripherals.com.chip2chip.phy._

object Interface {

  case class Axi4Interface(
      config: Axi4Config,
      phyCount: Int = 1,
      ioPins: Int = 16,
      outputDepth: Int = 4,
      inputDepth: Int = 8,
      transactionsDepth: Int = 8,
      responsesDepth: Int = 8
  ) extends Component {
    val io = new Bundle {
      val axiIn = slave(Axi4(config))
      val axiOut = master(Axi4(config))
      val txPhy = Vec(master(VirtualPhy.Io(ioPins)), phyCount)
      val rxPhy = Vec(slave(VirtualPhy.Io(ioPins)), phyCount)
    }

    val frontend = Frontend.Axi4Frontend(config)
    io.axiIn <> frontend.io.axiIn
    io.axiOut <> frontend.io.axiOut

    val multiplexer = Multiplexer(frontend.dataBlocks, phyCount)
    multiplexer.io.fromFrontend <> frontend.io.toLinkLayer
    frontend.io.fromLinkLayer <> multiplexer.io.toFrontend

    for (index <- 0 until phyCount) {
      val controller = ControllerLayer.ControllerLayer(
        ioPins,
        ioPins,
        outputDepth,
        inputDepth,
        transactionsDepth,
        responsesDepth
      )
      controller.io.fromFrontend <> multiplexer.io.toLinkLayer(index)
      multiplexer.io.fromLinkLayer(index) <> controller.io.toFrontend

      val linkLayer = LinkLayer.LinkLayer(ioPins, ioPins)
      linkLayer.io.fromFrontend <> controller.io.toLinkLayer
      controller.io.fromLinkLayer <> linkLayer.io.toFrontend

      val txPhy = VirtualPhy.Tx(ioPins)
      io.txPhy(index) <> txPhy.io.phy
      txPhy.io.enable := True
      val rxPhy = VirtualPhy.Rx(ioPins)
      io.rxPhy(index) <> rxPhy.io.phy

      val txFifo = StreamFifo(DataBlockContainer(ioPins), 2)
      txFifo.io.push <> linkLayer.io.toPhy
      txPhy.io.fromLinkLayer <> txFifo.io.pop

      rxPhy.io.fromPhy <> linkLayer.io.fromPhy
    }
  }
}
