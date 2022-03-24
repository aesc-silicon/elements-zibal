package zibal.platform

import spinal.core._
import spinal.lib._

import zibal.cores.VexRiscvCoreParameter
import zibal.soc.SocParameter
import zibal.misc.BinTools

import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._

import nafarr.peripherals.system.mtimer.{Apb3MachineTimer, MachineTimerCtrl}
import nafarr.peripherals.system.plic.{Apb3Plic, Plic, PlicCtrl}
import nafarr.peripherals.system.reset.{Apb3ResetController, ResetControllerCtrl}
import nafarr.peripherals.system.clock.{Apb3ClockController, ClockControllerCtrl}
import spinal.lib.com.jtag.Jtag

import vexriscv._

import vexriscv.plugin._

object Helium {

  case class Parameter(
    socParameter: SocParameter,
    onChipRamSize: BigInt,
    resetLogic: (ResetControllerCtrl.ResetControllerCtrl, Bool, Bool) => Unit,
    clockLogic: (ClockControllerCtrl.ClockControllerCtrl, ResetControllerCtrl.ResetControllerCtrl,
                 Bool) => Unit,
    onChipRamLogic: (BigInt) => (Axi4Shared, Mem[Bits]) = (onChipRamSize: BigInt) => {
      val ram = Axi4SharedOnChipRam(
        dataWidth = 32,
        byteCount = onChipRamSize,
        idWidth = 4
      )
      (ram.io.axi, ram.ram)
    }
  ) extends PlatformParameter(socParameter) {
    val core = VexRiscvCoreParameter.mcu(0x80000000L).plugins
    val mtimer = MachineTimerCtrl.Parameter.default
    val plic = PlicCtrl.Parameter.default(getSocParameter.getInterruptCount(0))
    val clocks = ClockControllerCtrl.Parameter(getKitParameter.clocks)
    val resets = ResetControllerCtrl.Parameter(getKitParameter.resets)
  }

  class Helium(parameter: Parameter) extends PlatformComponent(parameter) {
    val io_plat = new Bundle {
      val reset = in(Bool)
      val clock = in(Bool)
      val jtag = slave(Jtag())
    }

    override def initOnChipRam(path: String) = BinTools.initRam(system.onChipRamMem, path)

    val resetCtrl = ResetControllerCtrl(parameter.resets)
    parameter.resetLogic(resetCtrl, io_plat.reset, io_plat.clock)

    val clockCtrl = ClockControllerCtrl(parameter.clocks, resetCtrl)
    parameter.clockLogic(clockCtrl, resetCtrl, io_plat.clock)

    val system = new ClockingArea(clockCtrl.getClockDomainByName("system")) {

      /* AXI Manager */
      val core = new Area {
        val mtimerInterrupt = Bool
        val globalInterrupt = Bool

        val config = VexRiscvConfig(
          plugins = parameter.core += new DebugPlugin(clockCtrl.getClockDomainByName("debug"))
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
            clockCtrl.getClockDomainByName("debug") {
              resetCtrl.triggerByNameWithCond("system", RegNext(plugin.io.resetOut))
              io_plat.jtag <> plugin.io.bus.fromJtag()
            }
          case _ =>
        }
      }

      /* AXI Subordinates */
      val (onChipRamAxiPort, onChipRamMem) = parameter.onChipRamLogic(parameter.onChipRamSize)

      val apbBridge = Axi4SharedToApb3Bridge(
        addressWidth = 20,
        dataWidth = 32,
        idWidth = 4
      )

      /* Generate AXI Crossbar */
      val axiCrossbar = Axi4CrossbarFactory()

      axiCrossbar.addSlaves(
        onChipRamAxiPort -> (0x80000000L, parameter.onChipRamSize),
        apbBridge.io.axi -> (0xF0000000L, 1 MB)
      )

      axiCrossbar.addConnections(
        core.iBus -> List(onChipRamAxiPort),
        core.dBus -> List(onChipRamAxiPort, apbBridge.io.axi)
      )

      axiCrossbar.addPipelining(apbBridge.io.axi)((crossbar, bridge) => {
        crossbar.sharedCmd.halfPipe() >> bridge.sharedCmd
        crossbar.writeData.halfPipe() >> bridge.writeData
        crossbar.writeRsp << bridge.writeRsp
        crossbar.readRsp << bridge.readRsp
      })

      axiCrossbar.addPipelining(onChipRamAxiPort)((crossbar, ctrl) => {
        crossbar.sharedCmd >/-> ctrl.sharedCmd
        crossbar.writeData >/-> ctrl.writeData
        crossbar.writeRsp <-/< ctrl.writeRsp
        crossbar.readRsp <-/< ctrl.readRsp
      })

      axiCrossbar.addPipelining(core.dBus)((cpu, crossbar) => {
        cpu.sharedCmd >/-> crossbar.sharedCmd
        cpu.writeData >/-> crossbar.writeData
        cpu.writeRsp <-/< crossbar.writeRsp
        cpu.readRsp <-/< crossbar.readRsp
      })

      axiCrossbar.build()

      /* Peripheral IP-Cores */
      val plicCtrl = Apb3Plic(parameter.plic)
      core.globalInterrupt := plicCtrl.io.interrupt
      addApbDevice(plicCtrl.io.bus, 0xF0000, 64 kB)
      addInterrupt(False)

      val mtimerCtrl = Apb3MachineTimer(parameter.mtimer)
      core.mtimerInterrupt := mtimerCtrl.io.interrupt
      addApbDevice(mtimerCtrl.io.bus, 0x20000, 4 kB)

      val resetCtrlMapper = Apb3ResetController(parameter.resets)
      resetCtrlMapper.io.config <> resetCtrl.io.config
      addApbDevice(resetCtrlMapper.io.bus, 0x21000, 4 kB)

      val clockCtrlMapper = Apb3ClockController(parameter.clocks)
      clockCtrlMapper.io.config <> clockCtrl.io.config
      addApbDevice(clockCtrlMapper.io.bus, 0x22000, 4 kB)

      publishApbComponents(apbBridge, plicCtrl)
    }
  }
}
