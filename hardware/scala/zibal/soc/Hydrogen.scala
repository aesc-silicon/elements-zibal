/*
 * Copyright (c) 2019 Phytec Messtechnik GmbH
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
    gpio0: GpioCtrl.Parameter,
    gpioStatus: GpioCtrl.Parameter,
    uartStd: UartCtrl.Parameter,
    uartCom: UartCtrl.Parameter,
    uartRS232: UartCtrl.Parameter,
    spi0: SpiCtrl.Parameter,
    plic: PlicCtrl.Parameter,
    uniqueID: UniqueIDCtrl.Parameter,
    core: ArrayBuffer[Plugin[VexRiscv]]
) {}

object HydrogenParameter {
  def default = HydrogenParameter(
    sysFrequency = 100 MHz,
    dbgFrequency = 10 MHz,
    onChipRamSize = 128 kB,
    mtimer = MachineTimerCtrl.Parameter.default,
    gpio0 = GpioCtrl.Parameter(12, 2, null, (0 until 11), (0 until 11)),
    gpioStatus =
      GpioCtrl.Parameter(3, 2, (0 until 3), (1 until 3), (0 until 1)),
    uartStd = UartCtrl.Parameter.default,
    uartCom = UartCtrl.Parameter.full,
    uartRS232 = UartCtrl.Parameter.full,
    spi0 = SpiCtrl.Parameter.default,
    plic = PlicCtrl.Parameter.default(4),
    uniqueID = UniqueIDCtrl.Parameter.default,
    core = VexRiscvCoreParameter.default(0x80000000L).plugins
  )
}

case class Hydrogen(p: HydrogenParameter) extends Component {
  val io = new Bundle {
//    val reset = in(Bool)
    val clock = in(Bool)
    val reset = in(Bool)
    val sysReset_out = out(Bool)
    val jtag = slave(Jtag())
    val gpio0 = Gpio.Io(p.gpio0)
    val gpioStatus = Gpio.Io(p.gpioStatus)
    val uartStd = master(Uart.Io(p.uartCom))
    val uartCom = master(Uart.Io(p.uartCom))
    val uartRS232 = master(Uart.Io(p.uartRS232))
    val spi0 = master(Spi.Io(p.spi0))
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

    val uartComCtrl = Apb3Uart(p.uartCom)
    apbMapping += uartComCtrl.io.bus -> (0x01000, 4 kB)
    uartComCtrl.io.uart <> io.uartCom
    plicCtrl.io.sources(2) := uartComCtrl.io.interrupt

    val uartRS232Ctrl = Apb3Uart(p.uartRS232)
    apbMapping += uartRS232Ctrl.io.bus -> (0x02000, 4 kB)
    uartRS232Ctrl.io.uart <> io.uartRS232
    plicCtrl.io.sources(3) := uartRS232Ctrl.io.interrupt

    val gpioStatusCtrl = Apb3Gpio(p.gpioStatus)
    apbMapping += gpioStatusCtrl.io.bus -> (0x10000, 4 kB)
    gpioStatusCtrl.io.gpio <> io.gpioStatus

    val gpio0Ctrl = Apb3Gpio(p.gpio0)
    apbMapping += gpio0Ctrl.io.bus -> (0x11000, 4 kB)
    gpio0Ctrl.io.gpio <> io.gpio0

    val spiMaster0Ctrl = Apb3SpiMaster(p.spi0)
    apbMapping += spiMaster0Ctrl.io.bus -> (0x40000, 4 kB)
    spiMaster0Ctrl.io.spi <> io.spi0

    val uniqueID0Ctrl = Apb3UniqueID(p.uniqueID)
    apbMapping += uniqueID0Ctrl.io.bus -> (0xA0000, 4 kB)

    /*
    val uartACtrl = Apb3Uart(p.uartA)
    apbMapping += uartACtrl.io.bus -> (0x00000, 4 kB)
    uartACtrl.io.uart <> io.uartA
    plicCtrl.io.sources(1) := uartACtrl.io.interrupt

    val uartBCtrl = Apb3Uart(p.uartB)
    apbMapping += uartBCtrl.io.bus -> (0x01000, 4 kB)
    uartBCtrl.io.uart <> io.uartB
    plicCtrl.io.sources(2) := uartBCtrl.io.interrupt

    val gpioACtrl = Apb3Gpio(p.gpioA)
    apbMapping += gpioACtrl.io.bus -> (0x10000, 4 kB)
    gpioACtrl.io.gpio <> io.gpioA

    val gpioBCtrl = Apb3Gpio(p.gpioB)
    apbMapping += gpioBCtrl.io.bus -> (0x11000, 4 kB)
    gpioBCtrl.io.gpio <> io.gpioB
    plicCtrl.io.sources(9 downto 4) := gpioBCtrl.io.interrupt(5 downto 0)

    val spiMasterACtrl = Apb3SpiMaster(p.spiA)
    apbMapping += spiMasterACtrl.io.bus -> (0x40000, 4 kB)
    spiMasterACtrl.io.spi <> io.spiA
    plicCtrl.io.sources(3) := spiMasterACtrl.io.interrupt


    val sevenSegCtrl = Apb3SevenSegment(p.sevenSeg)
    apbMapping += sevenSegCtrl.io.bus -> (0x30000, 4 kB)
    sevenSegCtrl.io.segments <> io.sevenSegmentsA
     */
    val apbDecoder = Apb3Decoder(
      master = apbBridge.io.apb,
      slaves = apbMapping
    )

  }
}

object Hydrogen {
  def main(args: Array[String]) {
    val config = SpinalConfig(noRandBoot = false, targetDirectory = "./build/")
    config.generateVerilog({
      val toplevel = Hydrogen(HydrogenParameter.default)
      BinTools
        .initRam(toplevel.system.onChipRam.ram, "software/zephyr/firmware.bin")
      toplevel
    })
  }
}
