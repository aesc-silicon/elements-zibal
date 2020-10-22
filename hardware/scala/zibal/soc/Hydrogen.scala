/*
 * Copyright (c) 2020 Phytec Messtechnik GmbH
 */

package zibal.soc

import spinal.core._
import spinal.lib._

import zibal.cores.VexRiscvCoreParameter

import zibal.misc.BinTools
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import spinal.lib.io.TriStateArray

import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.misc.SizeMapping

import zibal.peripherals.io.gpio.{Apb3Gpio, Gpio, GpioCtrl}
import zibal.peripherals.misc.mtimer.{Apb3MachineTimer, MachineTimerCtrl}
import zibal.peripherals.misc.sevensegment.{
  Apb3SevenSegment,
  SevenSegment,
  SevenSegmentCtrl
}
import zibal.peripherals.com.uart.{Apb3Uart, Uart, UartCtrl}
import zibal.peripherals.misc.plic.{Apb3Plic, Plic, PlicCtrl}
import zibal.peripherals.com.spi.{
  Apb3SpiMaster,
  Spi,
  SpiCtrl,
  SpiMaster,
  SpiMasterCtrl
}
import zibal.peripherals.misc.uniqueid.{Apb3UniqueID, UniqueIDCtrl}
import spinal.lib.com.jtag.Jtag

import vexriscv.{plugin, _}
import vexriscv.plugin._

case class HydrogenParameter(
  sysFrequency: HertzNumber,
  dbgFrequency: HertzNumber,
  onChipRamSize: BigInt,
  mtimer: MachineTimerCtrl.Parameter,
  plic: PlicCtrl.Parameter,
  uartStd: UartCtrl.Parameter,
  gpioStatus: GpioCtrl.Parameter,
  //gpio1: GpioCtrl.Parameter,
  //gpio2: GpioCtrl.Parameter,
  //gpio3: GpioCtrl.Parameter,
  spi0: SpiCtrl.Parameter,
  core: ArrayBuffer[Plugin[VexRiscv]]
) {}

object HydrogenParameter {
  def default = HydrogenParameter(
    sysFrequency = 100 MHz,
    dbgFrequency = 10 MHz,
    onChipRamSize = 128 kB,
    mtimer = MachineTimerCtrl.Parameter.default,
    //plic = PlicCtrl.Parameter.default(7),
    plic = PlicCtrl.Parameter.default(4),
    uartStd = UartCtrl.Parameter.default,
    //gpioStatus = GpioCtrl.Parameter(5, 2, (0 to 3), (4 to 4), (4 to 4)),
    gpioStatus = GpioCtrl.Parameter(4, 2, (0 to 3), List(), List()),
    spi0 = SpiCtrl.Parameter.default,
    //gpio1 = GpioCtrl.Parameter.default,
    //gpio2 = GpioCtrl.Parameter.default,
    //gpio3 = GpioCtrl.Parameter(2, 2, null, null, null),
    core = VexRiscvCoreParameter.default(0x80000000L).plugins
  )
}

case class Hydrogen1(p: HydrogenParameter) extends Component {
  val io = new Bundle {
    val clock = in(Bool)
    val reset = in(Bool)
    val sysReset_out = out(Bool)
    val jtag = slave(Jtag())
    val uartStd = master(Uart.Io(p.uartStd))
    val gpioStatus = Gpio.Io(p.gpioStatus)
    val spi0 = master(Spi.Io(p.spi0))
    //val gpio1 = Gpio.Io(p.gpio1)
    //val gpio2 = Gpio.Io(p.gpio2)
    //val gpio3 = Gpio.Io(p.gpio3)
  }

  val resetCtrlClockDomain = ClockDomain(
    clock = io.clock,
    config = ClockDomainConfig(
      resetKind = BOOT
    )
  )

  val resetCtrl = new ClockingArea(resetCtrlClockDomain) {
    val mainClkResetUnbuffered = False

    //Implement an counter to keep the reset axiResetOrder high 64 cycles
    // Also this counter will automatically do a reset when the system boot.
    val systemClkResetCounter = Reg(UInt(6 bits)) init (0)
    when(systemClkResetCounter =/= U(systemClkResetCounter.range -> true)) {
      systemClkResetCounter := systemClkResetCounter + 1
      mainClkResetUnbuffered := True
    }
    when(BufferCC(io.reset)) {
      systemClkResetCounter := 0
    }

    //Create all reset used later in the design
    val systemReset = RegNext(mainClkResetUnbuffered)
    val debugReset = RegNext(mainClkResetUnbuffered)
  }

  io.sysReset_out <> resetCtrl.systemReset

  val clocks = new Area {
    val systemClockDomain = ClockDomain(
      clock = io.clock,
      reset = resetCtrl.systemReset,
      frequency = FixedFrequency(p.sysFrequency)
    )

    val debugClockDomain = ClockDomain(
      clock = io.clock,
      reset = resetCtrl.debugReset,
      frequency = FixedFrequency(p.dbgFrequency)
    )
  }

  val system = new ClockingArea(clocks.systemClockDomain) {

    val axiCrossbar = Axi4CrossbarFactory()

    /* AXI Masters */

    val core = new Area {
      val mtimerInterrupt = Bool
      val globalInterrupt = Bool

      val config = VexRiscvConfig(
        plugins = p.core += new DebugPlugin(clocks.debugClockDomain)
      )

      val cpu = new VexRiscv(config)
      var iBus: Axi4ReadOnly = null
      var dBus: Axi4Shared = null
      for (plugin <- config.plugins) plugin match {
        case plugin: IBusSimplePlugin => iBus = plugin.iBus.toAxi4ReadOnly()
        case plugin: IBusCachedPlugin => iBus = plugin.iBus.toAxi4ReadOnly()
        case plugin: DBusSimplePlugin => dBus = plugin.dBus.toAxi4Shared()
        case plugin: DBusCachedPlugin => dBus = plugin.dBus.toAxi4Shared()
        case plugin: CsrPlugin => {
          plugin.externalInterrupt := globalInterrupt
          plugin.timerInterrupt := mtimerInterrupt
        }
        case plugin: DebugPlugin =>
          clocks.debugClockDomain {
            //resetCtrl.foo.setWhen(RegNext(plugin.io.resetOut))
            resetCtrl.systemReset.setWhen(RegNext(plugin.io.resetOut))
            io.jtag <> plugin.io.bus.fromJtag()
          }
        case _ =>
      }
    }

    /* AXI Slaves */

    val onChipRam = Axi4SharedOnChipRam(
      dataWidth = 32,
      byteCount = p.onChipRamSize,
      idWidth = 4
    )

    val apbBridge = Axi4SharedToApb3Bridge(
      addressWidth = 20,
      dataWidth = 32,
      idWidth = 4
    )

    val apbMapping = ArrayBuffer[(Apb3, SizeMapping)]()

    /* Generate AXI Crossbar */

    axiCrossbar.addSlaves(
      onChipRam.io.axi -> (0x80000000L, p.onChipRamSize),
      apbBridge.io.axi -> (0xF0000000L, 1 MB)
    )

    axiCrossbar.addConnections(
      core.iBus -> List(onChipRam.io.axi),
      core.dBus -> List(onChipRam.io.axi, apbBridge.io.axi)
    )

    axiCrossbar.addPipelining(apbBridge.io.axi)((crossbar, bridge) => {
      crossbar.sharedCmd.halfPipe() >> bridge.sharedCmd
      crossbar.writeData.halfPipe() >> bridge.writeData
      crossbar.writeRsp << bridge.writeRsp
      crossbar.readRsp << bridge.readRsp
    })

    axiCrossbar.addPipelining(onChipRam.io.axi)((crossbar, ctrl) => {
      crossbar.sharedCmd.halfPipe() >> ctrl.sharedCmd
      crossbar.writeData >/-> ctrl.writeData
      crossbar.writeRsp << ctrl.writeRsp
      crossbar.readRsp << ctrl.readRsp
    })

    axiCrossbar.addPipelining(core.dBus)((cpu, crossbar) => {
      cpu.sharedCmd >> crossbar.sharedCmd
      cpu.writeData >> crossbar.writeData
      cpu.writeRsp << crossbar.writeRsp
      cpu.readRsp <-< crossbar.readRsp //Data cache directly use read responses without buffering, so pipeline it for FMax
    })

    axiCrossbar.build()

    /* Peripheral IP-Cores */
    val mtimerCtrl = Apb3MachineTimer(p.mtimer)
    apbMapping += mtimerCtrl.io.bus -> (0x20000, 4 kB)
    core.mtimerInterrupt := mtimerCtrl.io.interrupt

    val plicCtrl = Apb3Plic(p.plic)
    apbMapping += plicCtrl.io.bus -> (0xF0000, 64 kB)
    core.globalInterrupt := plicCtrl.io.interrupt
    plicCtrl.io.sources(0) := False

    val uartStdCtrl = Apb3Uart(p.uartStd)
    apbMapping += uartStdCtrl.io.bus -> (0x00000, 4 kB)
    uartStdCtrl.io.uart <> io.uartStd
    plicCtrl.io.sources(1) := uartStdCtrl.io.interrupt

    val gpioStatusCtrl = Apb3Gpio(p.gpioStatus)
    apbMapping += gpioStatusCtrl.io.bus -> (0x10000, 4 kB)
    gpioStatusCtrl.io.gpio <> io.gpioStatus
    plicCtrl.io.sources(2) := gpioStatusCtrl.io.interrupt

    val spiMaster0Ctrl = Apb3SpiMaster(p.spi0)
    apbMapping += spiMaster0Ctrl.io.bus -> (0x40000, 4 kB)
    spiMaster0Ctrl.io.spi <> io.spi0
    plicCtrl.io.sources(3) := spiMaster0Ctrl.io.interrupt
/*
    val gpio1Ctrl = Apb3Gpio(p.gpio1)
    apbMapping += gpio1Ctrl.io.bus -> (0x11000, 4 kB)
    gpio1Ctrl.io.gpio <> io.gpio1
    plicCtrl.io.sources(4) :=gpio1Ctrl.io.interrupt

    val gpio2Ctrl = Apb3Gpio(p.gpio2)
    apbMapping += gpio2Ctrl.io.bus -> (0x12000, 4 kB)
    gpio2Ctrl.io.gpio <> io.gpio2
    plicCtrl.io.sources(5) := gpio2Ctrl.io.interrupt

    val gpio3Ctrl = Apb3Gpio(p.gpio3)
    apbMapping += gpio3Ctrl.io.bus -> (0x13000, 4 kB)
    gpio3Ctrl.io.gpio <> io.gpio3
    plicCtrl.io.sources(6) := gpio3Ctrl.io.interrupt
*/
    val apbDecoder = Apb3Decoder(
      master = apbBridge.io.apb,
      slaves = apbMapping
    )

  }
}

object Hydrogen1 {
  def main(args: Array[String]) {
    val config = SpinalConfig(noRandBoot = false, targetDirectory = "./../build/zibal/")
    config.generateVerilog({
      val toplevel = Hydrogen1(HydrogenParameter.default)
      BinTools
        .initRam(toplevel.system.onChipRam.ram, "../build/zephyr/zephyr/zephyr.bin")
      toplevel
    })
  }
}
