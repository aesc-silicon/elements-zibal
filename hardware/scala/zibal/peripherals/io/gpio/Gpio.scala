package zibal.peripherals.io.gpio

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc._
import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.avalon._
import spinal.lib.bus.wishbone._
import spinal.lib.io.{TriStateArray, TriState}


object Gpio {
  case class Io(p: GpioCtrl.Parameter) extends Bundle {
    val pins = master(TriStateArray(p.width bits))
  }

  class Core[T <: spinal.core.Data with IMasterSlave](
    p: GpioCtrl.Parameter,
    busType: HardType[T],
    factory: T => BusSlaveFactory
  ) extends Component {
    val io = new Bundle {
      val bus = slave(busType())
      val gpio = Io(p)
      val interrupt = out(Bool)
    }

    val ctrl = GpioCtrl(p)
    ctrl.io.gpio <> io.gpio
    io.interrupt <> ctrl.io.interrupt

    val mapper = GpioCtrl.Mapper(factory(io.bus), ctrl.io, p)
  }
}

case class Apb3Gpio(
  parameter: GpioCtrl.Parameter,
  busConfig: Apb3Config = Apb3Config(12, 32)
) extends Gpio.Core[Apb3] (
  parameter,
  Apb3(busConfig),
  Apb3SlaveFactory(_)
) { val dummy = 0 }

case class WishboneGpio(
  parameter: GpioCtrl.Parameter,
  busConfig: WishboneConfig = WishboneConfig(12, 32)
) extends Gpio.Core[Wishbone] (
  parameter,
  Wishbone(busConfig),
  WishboneSlaveFactory(_)
) { val dummy = 0 }

case class AvalonMMGpio(
  parameter: GpioCtrl.Parameter,
  busConfig: AvalonMMConfig = AvalonMMConfig.fixed(12, 32, 1)
) extends Gpio.Core[AvalonMM] (
  parameter,
  AvalonMM(busConfig),
  AvalonMMSlaveFactory(_)
) { val dummy = 0 }
