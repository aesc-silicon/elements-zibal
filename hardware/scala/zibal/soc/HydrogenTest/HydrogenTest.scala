package zibal.soc

import spinal.core._
import spinal.lib._

import zibal.platform.Hydrogen

import zibal.misc.{ElementsConfig, ZephyrTools}

import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._

import zibal.peripherals.io.gpio.{Apb3Gpio, Gpio, GpioCtrl}
import zibal.peripherals.com.uart.{Apb3Uart, Uart, UartCtrl}
import zibal.peripherals.com.spi.{Apb3SpiMaster, Spi, SpiCtrl}
import zibal.peripherals.com.i2c.{Apb3I2cController, I2c, I2cCtrl}
import zibal.peripherals.misc.frequencycounter.{Apb3FrequencyCounter, FrequencyCounter, FrequencyCounterCtrl}
import zibal.peripherals.io.pio.{Apb3Pio, Pio, PioCtrl}


object HydrogenTest {
  def apply(sysFrequency: HertzNumber) =
      HydrogenTest(Parameter.default(Parameter.Clocks(sysFrequency)))
  def apply(parameter: Hydrogen.Parameter) = HydrogenTest(parameter)

  def prepare(soc: HydrogenTest, elementsConfig: ElementsConfig.ElementsConfig) {
    val dt = ZephyrTools.DeviceTree(elementsConfig)
    dt.generate("hydrogen", soc.clocks.systemClockDomain, soc.system.axiCrossbar.slavesConfigs,
                soc.system.apbBridge.io.axi, soc.system.apbMapping, soc.system.irqMapping,
                "hydrogentest.dtsi")
    val board = ZephyrTools.Board(elementsConfig)
    board.addLed("heartbeat", soc.pers.gpioStatusCtrl, 0)
    board.addLed("ok", soc.pers.gpioStatusCtrl, 1)
    board.addLed("error", soc.pers.gpioStatusCtrl, 2)
    board.addKey("reset", soc.pers.gpioStatusCtrl, 3)
    board.generateDeviceTree(soc.pers.uartStdCtrl)
    board.generateKconfig()
    board.generateDefconfig(soc.system.apbMapping, soc.clocks.systemClockDomain)
  }

  case class Peripherals (
    uartStd: UartCtrl.Parameter,
    gpioStatus: GpioCtrl.Parameter,
    gpioA: GpioCtrl.Parameter,
    spiA: SpiCtrl.Parameter,
    i2cA: I2cCtrl.Parameter,
    freqCounterA: FrequencyCounterCtrl.Parameter,
    pioA: PioCtrl.Parameter
  )

  object Parameter {
    case class Clocks(sysFrequency: HertzNumber) {
      val jtagFrequency = 10 MHz
    }

    def default(clocks: Clocks) =
      Hydrogen.Parameter.default(
        Peripherals(
          uartStd = UartCtrl.Parameter.default,
          gpioStatus = GpioCtrl.Parameter(4, 2, (0 to 2), (3 to 3), (3 to 3)),
          gpioA = GpioCtrl.Parameter(32, 2, null, null, null),
          spiA = SpiCtrl.Parameter.default,
          i2cA = I2cCtrl.Parameter.default,
          freqCounterA = FrequencyCounterCtrl.Parameter.default,
          pioA = PioCtrl.Parameter(2)
        ),
        clocks.sysFrequency,
        clocks.jtagFrequency,
        5
      )
  }

  case class Io(peripherals: Peripherals) extends Bundle {
    val uartStd = master(Uart.Io(peripherals.uartStd))
    val gpioStatus = Gpio.Io(peripherals.gpioStatus)
    val gpioA = Gpio.Io(peripherals.gpioA)
    val spiA = master(Spi.Io(peripherals.spiA))
    val i2cA = master(I2c.Io(peripherals.i2cA))
    val freqCounterA = FrequencyCounter.Io(peripherals.freqCounterA)
    val pioA = Pio.Io(peripherals.pioA)
  }

  case class HydrogenTest(p: Hydrogen.Parameter) extends Hydrogen.Hydrogen(p) {
    var peripherals = p.peripherals.asInstanceOf[Peripherals]
    val io_per = Io(peripherals)

    val pers = new ClockingArea(clocks.systemClockDomain) {

      val uartStdCtrl = Apb3Uart(peripherals.uartStd)
      uartStdCtrl.io.uart <> io_per.uartStd
      addApbDevice(uartStdCtrl.io.bus, 0x00000, 4 kB)
      addInterrupt(uartStdCtrl.io.interrupt)

      val gpioStatusCtrl = Apb3Gpio(peripherals.gpioStatus)
      gpioStatusCtrl.io.gpio <> io_per.gpioStatus
      addApbDevice(gpioStatusCtrl.io.bus, 0x10000, 4 kB)
      addInterrupt(gpioStatusCtrl.io.interrupt)

      val gpioACtrl = Apb3Gpio(peripherals.gpioA)
      gpioACtrl.io.gpio <> io_per.gpioA
      addApbDevice(gpioACtrl.io.bus, 0x11000, 4 kB)
      addInterrupt(gpioACtrl.io.interrupt)

      val spiMasterACtrl = Apb3SpiMaster(peripherals.spiA)
      spiMasterACtrl.io.spi <> io_per.spiA
      addApbDevice(spiMasterACtrl.io.bus, 0x4000, 4 kB)
      addInterrupt(spiMasterACtrl.io.interrupt)

      val i2cControllerACtrl = Apb3I2cController(peripherals.i2cA)
      i2cControllerACtrl.io.i2c <> io_per.i2cA
      addApbDevice(i2cControllerACtrl.io.bus, 0x50000, 4 kB)
      addInterrupt(i2cControllerACtrl.io.interrupt)

      val frequencyCounterACtrl = Apb3FrequencyCounter(peripherals.freqCounterA)
      frequencyCounterACtrl.io.clock <> io_per.freqCounterA
      addApbDevice(frequencyCounterACtrl.io.bus, 0x60000, 4 kB)

      val pioACtrl = Apb3Pio(peripherals.pioA)
      pioACtrl.io.pio <> io_per.pioA
      addApbDevice(pioACtrl.io.bus, 0x12000, 4 kB)

      connectPeripherals()
    }
  }
}
