/*
 * Copyright (c) 2020 Phytec Messtechnik GmbH
 */

package zibal.platform

import spinal.core._
import spinal.lib._

import zibal.cores.VexRiscvCoreParameter

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import spinal.lib.io.TriStateArray

import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.misc.SizeMapping

import zibal.peripherals.misc.mtimer.{Apb3MachineTimer, MachineTimerCtrl}
import zibal.peripherals.misc.plic.{Apb3Plic, Plic, PlicCtrl}
import zibal.peripherals.com.spi.{AmbaSpiXipMaster, Spi, SpiCtrl}
import spinal.lib.com.jtag.Jtag

import vexriscv.{plugin, _}
import vexriscv.plugin._

object Carbon {

  case class Parameter(
    sysFrequency: HertzNumber,
    dbgFrequency: HertzNumber,
    onChipRamSize: BigInt,
    onChipRomSize: BigInt,
    mtimer: MachineTimerCtrl.Parameter,
    plic: PlicCtrl.Parameter,
    spiXip: SpiCtrl.Parameter,
    core: ArrayBuffer[Plugin[VexRiscv]],
    peripherals: Any
  ) {}

  object Parameter {
    def default(peripherals: Any, interrupts: Int = 0) = Parameter(
      sysFrequency = 100 MHz,
      dbgFrequency = 10 MHz,
      onChipRamSize = 4 kB,
      onChipRomSize = 4 MB,
      mtimer = MachineTimerCtrl.Parameter.default,
      plic = PlicCtrl.Parameter.default(interrupts + 2),
      spiXip = SpiCtrl.Parameter.default,
      core = VexRiscvCoreParameter.realtime(0xA0000000L).plugins,
      peripherals = peripherals
    )
    def light(peripherals: Any, interrupts: Int = 0) = Parameter(
      sysFrequency = 50 MHz,
      dbgFrequency = 10 MHz,
      onChipRamSize = 512 Byte,
      onChipRomSize = 4 MB,
      mtimer = MachineTimerCtrl.Parameter.default,
      plic = PlicCtrl.Parameter.default(interrupts + 2),
      spiXip = SpiCtrl.Parameter.xip,
      core = VexRiscvCoreParameter.realtime(0xA0000000L).plugins,
      peripherals = peripherals
    )
  }

  case class Io(p: Parameter) extends Bundle {
    val clock = in(Bool)
    val reset = in(Bool)
    val sysReset_out = out(Bool)
    val jtag = slave(Jtag())
    val spiXip = master(Spi.Io(p.spiXip))
  }

  class Carbon(p: Parameter) extends Component {
    val io_sys = Io(p)

    def connectPeripherals() = {
      val apbDecoder = Apb3Decoder(
        master = system.apbBridge.io.apb,
        slaves = system.apbMapping
      )

      for ((index, interrupt) <- system.irqMapping) {
        system.plicCtrl.io.sources(index) := interrupt
      }
    }

    val resetCtrlClockDomain = ClockDomain(
      clock = io_sys.clock,
      config = ClockDomainConfig(
        resetKind = BOOT
      )
    )

    val debugReset = Bool
    val systemReset = io_sys.reset | !debugReset
    io_sys.sysReset_out := systemReset

    val clocks = new Area {
      val systemClockDomain = ClockDomain(
        clock = io_sys.clock,
        reset = io_sys.reset,
        frequency = FixedFrequency(p.sysFrequency),
        config = ClockDomainConfig(
          resetKind = spinal.core.SYNC,
          resetActiveLevel = LOW
        )
      )

      val debugClockDomain = ClockDomain(
        clock = io_sys.clock,
        reset = io_sys.reset,
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
            plugin.debugClockDomain {
              debugReset := plugin.io.resetOut
              io_sys.jtag <> plugin.io.bus.fromJtag()
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

      val spiXipMasterCtrl = AmbaSpiXipMaster(p.spiXip, Axi4Config(20, 32, 4))

      val apbMapping = ArrayBuffer[(Apb3, SizeMapping)]()
      val irqMapping = ArrayBuffer[(Int, Bool)]()

      /* Generate AXI Crossbar */

      axiCrossbar.addSlaves(
        onChipRam.io.axi -> (0x80000000L, p.onChipRamSize),
        spiXipMasterCtrl.io.dataBus -> (0xA0000000L, p.onChipRomSize),
        apbBridge.io.axi -> (0xF0000000L, 1 MB)
      )

      axiCrossbar.addConnections(
        core.iBus -> List(spiXipMasterCtrl.io.dataBus),
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

      axiCrossbar.addPipelining(spiXipMasterCtrl.io.dataBus)((crossbar, ctrl) => {
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
      irqMapping += 0 -> False

      apbMapping += spiXipMasterCtrl.io.bus -> (0x40000, 4 kB)
      spiXipMasterCtrl.io.spi <> io_sys.spiXip
      irqMapping += 1 -> spiXipMasterCtrl.io.interrupt
    }
  }
}
