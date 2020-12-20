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
import spinal.lib.com.jtag.Jtag

import vexriscv.{plugin, _}
import vexriscv.plugin._

object Hydrogen {

  case class Parameter(
    sysFrequency: HertzNumber,
    dbgFrequency: HertzNumber,
    onChipRamSize: BigInt,
    mtimer: MachineTimerCtrl.Parameter,
    plic: PlicCtrl.Parameter,
    core: ArrayBuffer[Plugin[VexRiscv]],
    peripherals: Any
  ) {}

  object Parameter {
    def default(peripherals: Any, interrupts: Int = 0) = Parameter(
      sysFrequency = 100 MHz,
      dbgFrequency = 10 MHz,
      onChipRamSize = 128 kB,
      mtimer = MachineTimerCtrl.Parameter.default,
      plic = PlicCtrl.Parameter.default(interrupts + 1),
      core = VexRiscvCoreParameter.default(0x80000000L).plugins,
      peripherals = peripherals
    )
  }

  class Hydrogen(p: Parameter) extends Component {
    val io_sys = new Bundle {
      val clock = in(Bool)
      val reset = in(Bool)
      val sysReset_out = out(Bool)
      val jtag = slave(Jtag())
    }

    val resetCtrlClockDomain = ClockDomain(
      clock = io_sys.clock,
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
      when(BufferCC(io_sys.reset)) {
        systemClkResetCounter := 0
      }

      //Create all reset used later in the design
      val systemReset = RegNext(mainClkResetUnbuffered)
      val debugReset = RegNext(mainClkResetUnbuffered)
    }

    io_sys.sysReset_out <> resetCtrl.systemReset

    val clocks = new Area {
      val systemClockDomain = ClockDomain(
        clock = io_sys.clock,
        reset = resetCtrl.systemReset,
        frequency = FixedFrequency(p.sysFrequency)
      )

      val debugClockDomain = ClockDomain(
        clock = io_sys.clock,
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
              resetCtrl.systemReset.setWhen(RegNext(plugin.io.resetOut))
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
    }
  }
}
