package nafarr.peripherals.com.spi

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.avalon._
import spinal.lib.bus.wishbone._

object SpiXipMaster {
  class Core[T <: spinal.core.Data with IMasterSlave](
      p: SpiCtrl.Parameter,
      dataBusConfig: Axi4Config,
      busType: HardType[T],
      factory: T => BusSlaveFactory
  ) extends Component {
    val io = new Bundle {
      val bus = slave(busType())
      val dataBus = slave(Axi4Shared(dataBusConfig))
      val spi = master(Spi.Io(p))
      val interrupt = out(Bool)
    }

    val spiMasterCtrl = SpiMasterCtrl(p)
    spiMasterCtrl.io.spi <> io.spi
    io.interrupt := False

    val spiXipMasterCtrl = SpiXipMasterCtrl(p, dataBusConfig)
    spiMasterCtrl.io.cmd << spiXipMasterCtrl.io.cmd
    spiXipMasterCtrl.io.rsp << spiMasterCtrl.io.rsp
    spiXipMasterCtrl.io.bus << io.dataBus

    val busFactory = factory(io.bus)
    SpiMasterCtrl.Mapper(busFactory, spiMasterCtrl.io, p)
  }
}

case class Axi4SharedSpiXipMaster(
    parameter: SpiCtrl.Parameter,
    dataBusConfig: Axi4Config = Axi4Config(20, 32, 4),
    busConfig: Apb3Config = Apb3Config(12, 32)
) extends SpiXipMaster.Core[Apb3](
      parameter,
      dataBusConfig,
      Apb3(busConfig),
      Apb3SlaveFactory(_)
    ) { val dummy = 0 }
