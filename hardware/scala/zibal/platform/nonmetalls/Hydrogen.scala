package zibal.platform

import spinal.core._
import spinal.lib._

import zibal.cores.VexRiscvCoreParameter
import zibal.soc.SocParameter
import zibal.misc.BinTools
import zibal.misc.BaremetalTools
import zibal.misc.ElementsConfig

import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._

import nafarr.system.mtimer.{Apb3MachineTimer, MachineTimerCtrl}
import nafarr.system.plic.{Apb3Plic, Plic, PlicCtrl}
import nafarr.system.reset.{Apb3ResetController, ResetControllerCtrl}
import nafarr.system.clock.{Apb3ClockController, ClockControllerCtrl}
import nafarr.peripherals.com.spi.{Axi4ReadOnlySpiXipController, Spi, SpiControllerCtrl}
import spinal.lib.com.jtag.Jtag

import vexriscv._
import vexriscv.plugin._

object Hydrogen {

  case class Parameter(
      socParameter: SocParameter,
      onChipRamSize: BigInt,
      spiRomSize: BigInt,
      resetLogic: (ResetControllerCtrl.ResetControllerCtrl, Bool, Bool) => Unit,
      clockLogic: (
          ClockControllerCtrl.ClockControllerCtrl,
          ResetControllerCtrl.ResetControllerCtrl,
          Bool
      ) => Unit,
      onChipRamLogic: (BigInt) => (Component, Axi4Shared) = (onChipRamSize: BigInt) => {
        val ram = Axi4SharedOnChipRam(
          dataWidth = 32,
          byteCount = onChipRamSize,
          idWidth = 4
        )
        (ram, ram.io.axi)
      }
  ) extends PlatformParameter(socParameter) {
    val core = VexRiscvCoreParameter.realtime(0xa0000000L).plugins
    val mtimer = MachineTimerCtrl.Parameter.default
    val plic = PlicCtrl.Parameter.default(getSocParameter.getInterruptCount(0))
    val clocks = ClockControllerCtrl.Parameter(getKitParameter.clocks)
    val resets = ResetControllerCtrl.Parameter(getKitParameter.resets)
    val spi = SpiControllerCtrl.Parameter.xip()
  }

  class Hydrogen(parameter: Parameter) extends PlatformComponent(parameter) {
    val io_plat = new Bundle {
      val reset = in(Bool)
      val clock = in(Bool)
      val jtag = slave(Jtag())
      val spi = master(Spi.Io(parameter.spi.io))
    }

    def prepareBaremetal(name: String, elementsConfig: ElementsConfig.ElementsConfig) {
      val header = BaremetalTools.Header(elementsConfig, name)
      header.generate(
        this.system.axiCrossbar.slavesConfigs,
        this.system.apbBridge.io.axi,
        this.apbMapping,
        this.irqMapping
      )
    }

    override def initOnChipRam(path: String) {}

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
      val (onChipCtrl, onChipRamAxiPort) = parameter.onChipRamLogic(parameter.onChipRamSize)

      val spiXipControllerCtrl = Axi4ReadOnlySpiXipController(parameter.spi)
      io_plat.spi <> spiXipControllerCtrl.io.spi

      val apbBridge = Axi4SharedToApb3Bridge(
        addressWidth = 24,
        dataWidth = 32,
        idWidth = 4
      )

      /* Generate AXI Crossbar */
      val axiCrossbar = Axi4CrossbarFactory()

      axiCrossbar.addSlaves(
        onChipRamAxiPort -> (0x80000000L, parameter.onChipRamSize),
        spiXipControllerCtrl.io.dataBus -> (0xa0000000L, parameter.spiRomSize),
        apbBridge.io.axi -> (0xf0000000L, 16 MB)
      )

      axiCrossbar.addConnections(
        core.iBus -> List(
          onChipRamAxiPort,
          spiXipControllerCtrl.io.dataBus
        ),
        core.dBus -> List(
          onChipRamAxiPort,
          apbBridge.io.axi,
          spiXipControllerCtrl.io.dataBus
        )
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

      axiCrossbar.addPipelining(spiXipControllerCtrl.io.dataBus)((crossbar, ctrl) => {
        crossbar.readCmd >/-> ctrl.readCmd
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
      addApbDevice(plicCtrl.io.bus, 0x800000, 4 MB)

      val mtimerCtrl = Apb3MachineTimer(parameter.mtimer)
      core.mtimerInterrupt := mtimerCtrl.io.interrupt
      addApbDevice(mtimerCtrl.io.bus, 0x20000, 4 kB)

      val resetCtrlMapper = Apb3ResetController(parameter.resets)
      resetCtrlMapper.io.config <> resetCtrl.io.config
      addApbDevice(resetCtrlMapper.io.bus, 0x21000, 4 kB)

      val clockCtrlMapper = Apb3ClockController(parameter.clocks)
      clockCtrlMapper.io.config <> clockCtrl.io.config
      addApbDevice(clockCtrlMapper.io.bus, 0x22000, 4 kB)

      addApbDevice(spiXipControllerCtrl.io.bus, 0x24000, 4 kB)

      publishApbComponents(apbBridge, plicCtrl)
    }
  }
}
