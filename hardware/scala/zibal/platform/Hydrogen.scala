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
import zibal.peripherals.misc.reset.{Apb3ResetController, ResetControllerCtrl}
import spinal.lib.com.jtag.Jtag

import vexriscv.{plugin, _}
import vexriscv.plugin._

object Hydrogen {

  case class Parameter(
    sysFrequency: HertzNumber,
    dbgFrequency: HertzNumber,
    onChipRamSize: BigInt,
    interrupts: Int,
    peripherals: Any
  ) {
    require(interrupts >= 0)
    val mtimer = MachineTimerCtrl.Parameter.default
    val plic = PlicCtrl.Parameter.default(interrupts + 1)
    val core = VexRiscvCoreParameter.realtime(0x80000000L).plugins
    val resets = ResetControllerCtrl.Parameter(List(("system", 64), ("debug", 64)))
  }

  object Parameter {
    def default(
      peripherals: Any,
      sysFrequency: HertzNumber,
      dbgFrequency: HertzNumber,
      interrupts: Int,
      onChipRamSize: BigInt = 128 kB
    ) = Parameter(sysFrequency, dbgFrequency, onChipRamSize, interrupts, peripherals)
  }

  case class Io(p: Parameter) extends Bundle {
    val clock = in(Bool)
    val jtag = slave(Jtag())
    val resets = ResetControllerCtrl.BuildConnection(p.resets)
  }

  class Hydrogen(p: Parameter) extends Component {
    val io_sys = Io(p)

    def connectPeripherals() {
      val apbDecoder = Apb3Decoder(
        master = system.apbBridge.io.apb,
        slaves = system.apbMapping
      )

      for ((index, interrupt) <- system.irqMapping) {
        system.plicCtrl.io.sources(index) := interrupt
      }
    }

    def addApbDevice(port: Apb3, address: BigInt, size: BigInt) {
      system.apbMapping += port -> (address, size)
    }

    var nextInterruptNumber = 1
    def addInterrupt(pin: Bool) {
      system.irqMapping += nextInterruptNumber -> pin
      nextInterruptNumber += 1
    }

    val resetCtrl = ResetControllerCtrl(p.resets)
    resetCtrl.io.buildConnection <> io_sys.resets

    val clocks = new Area {
      val systemClockDomain = ClockDomain(
        clock = io_sys.clock,
        reset = resetCtrl.getResetByName("system"),
        frequency = FixedFrequency(p.sysFrequency),
        config = ClockDomainConfig(
          resetKind = spinal.core.SYNC,
          resetActiveLevel = LOW
        )
      )

      val debugClockDomain = ClockDomain(
        clock = io_sys.clock,
        reset = resetCtrl.getResetByName("debug"),
        frequency = FixedFrequency(p.sysFrequency),
        config = ClockDomainConfig(
          resetKind = spinal.core.SYNC,
          resetActiveLevel = LOW
        )
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
              resetCtrl.triggerByNameWithCond("system", RegNext(plugin.io.resetOut))
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
      val irqMapping = ArrayBuffer[(Int, Bool)]()

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
      core.mtimerInterrupt := mtimerCtrl.io.interrupt
      apbMapping += mtimerCtrl.io.bus -> (0x20000, 4 kB)

      val plicCtrl = Apb3Plic(p.plic)
      core.globalInterrupt := plicCtrl.io.interrupt
      apbMapping += plicCtrl.io.bus -> (0xF0000, 64 kB)
      irqMapping += 0 -> False

      val resetCtrlMapper = Apb3ResetController(p.resets)
      apbMapping += resetCtrlMapper.io.bus -> (0x21000, 4 kB)
      resetCtrlMapper.io.config <> resetCtrl.io.config
    }
  }
}
