// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal.platform

import spinal.core._
import spinal.lib._

import zibal.cores.VexRiscvCoreParameter
import zibal.soc.SocParameter
import zibal.misc.BinTools
import zibal.misc.BaremetalTools
import zibal.misc.ElementsConfig

import spinal.lib.bus.misc.{SizeMapping, AddressMapping}
import spinal.lib.bus.bmb._
import spinal.lib.bus.wishbone._

import nafarr.bus.bmb.BmbCache
import nafarr.system.mtimer.{WishboneMachineTimer, MachineTimerCtrl}
import nafarr.system.plic.{WishbonePlic, Plic, PlicCtrl}
import nafarr.system.reset.{WishboneResetController, ResetControllerCtrl}
import nafarr.system.clock.{WishboneClockController, ClockControllerCtrl}
import nafarr.memory.spi.{BmbSpiXipController}
import nafarr.peripherals.com.spi.{Spi, SpiControllerCtrl}
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
      onChipRamLogic: (BmbParameter, BigInt) => (Component, Bmb) =
        (p: BmbParameter, onChipRamSize: BigInt) => {
          val ram = BmbOnChipRam(
            p = p,
            size = onChipRamSize
          )
          (ram, ram.io.bus)
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
      header.generateWishbone(
        this.system.wishboneBridge.mapping,
        this.wishboneMapping,
        this.irqMapping
      )
    }

    override def initOnChipRam(path: String) {}

    val resetCtrl = ResetControllerCtrl(parameter.resets)
    parameter.resetLogic(resetCtrl, io_plat.reset, io_plat.clock)

    val clockCtrl = ClockControllerCtrl(parameter.clocks, parameter.resets, resetCtrl)
    parameter.clockLogic(clockCtrl, resetCtrl, io_plat.clock)

    val core = new ClockingArea(clockCtrl.getClockDomainByName("system")) {
      val mtimerInterrupt = Bool
      val globalInterrupt = Bool

      val config = VexRiscvConfig(
        plugins = parameter.core += new DebugPlugin(clockCtrl.getClockDomainByName("debug"))
      )

      val cpu = new VexRiscv(config)
      var iBus: Bmb = null
      var dBus: Bmb = null
      for (plugin <- config.plugins) plugin match {
        case plugin: IBusSimplePlugin => iBus = plugin.iBus.toBmb()
        case plugin: IBusCachedPlugin => iBus = plugin.iBus.toBmb()
        case plugin: DBusSimplePlugin => dBus = plugin.dBus.toBmb()
        case plugin: DBusCachedPlugin => dBus = plugin.dBus.toBmb()
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

    val system = new ClockingArea(clockCtrl.getClockDomainByName("system")) {

      /* BMB Subordinates */
      val onChipRam = new Area {
        val mapping = SizeMapping(0x80000000L, parameter.onChipRamSize)
        val bmbParameter = BmbParameter(
          addressWidth = log2Up(mapping.size) + 2,
          dataWidth = 32,
          lengthWidth = 6,
          sourceWidth = 4,
          contextWidth = 4
        )
        val (ctrl, port) = parameter.onChipRamLogic(bmbParameter, mapping.size)
      }

      val wishboneBridge = new Area {
        val mapping = SizeMapping(0xf0000000L, 16 MB)
        val bmbParameter = BmbParameter(
          addressWidth = log2Up(mapping.size) + 2,
          dataWidth = 32,
          lengthWidth = 6,
          sourceWidth = 4,
          contextWidth = 4
        )
        val bridge = BmbToWishbone(p = bmbParameter)
      }
      val wishboneConfig = BmbToWishbone.getWishboneConfig(wishboneBridge.bmbParameter.access)

      val spiXipController = new Area {
        val mapping = SizeMapping(0xa0000000L, parameter.spiRomSize)
        val bmbParameter = BmbParameter(
          addressWidth = log2Up(mapping.size) + 2,
          dataWidth = 32,
          lengthWidth = 6,
          sourceWidth = 4,
          contextWidth = 4
        )
        val ctrl = BmbSpiXipController(parameter.spi, bmbParameter, wishboneConfig)
        io_plat.spi <> ctrl.io.spi

        val cache = BmbCache(bmbParameter, 4)
        ctrl.io.dataBus << cache.io.output
      }

      /* Generate BMB Crossbar */
      val iBusDecoder = BmbDecoder(
        p = VexRiscvCoreParameter.iBusConfig,
        mappings = Seq(onChipRam.mapping, spiXipController.mapping),
        capabilities = Seq(onChipRam.bmbParameter, spiXipController.bmbParameter)
      )
      iBusDecoder.io.input << core.iBus.pipelined(
        cmdValid = true,
        cmdReady = true,
        rspValid = true,
        rspReady = true
      )
      iBusDecoder.io.input.cmd.mask := B"1111"

      val dBusDecoder = BmbDecoder(
        p = VexRiscvCoreParameter.dBusConfig,
        mappings = Seq(onChipRam.mapping, spiXipController.mapping, wishboneBridge.mapping),
        capabilities =
          Seq(onChipRam.bmbParameter, spiXipController.bmbParameter, wishboneBridge.bmbParameter)
      )
      dBusDecoder.io.input << core.dBus.pipelined(
        cmdValid = true,
        cmdReady = true,
        rspValid = true,
        rspReady = true
      )

      val onChipRamArbiter = BmbArbiter(
        inputsParameter = Seq(VexRiscvCoreParameter.iBusConfig, VexRiscvCoreParameter.dBusConfig),
        outputParameter = onChipRam.bmbParameter,
        lowerFirstPriority = true
      )
      onChipRam.port << onChipRamArbiter.io.output
      onChipRamArbiter.io.inputs(0) << iBusDecoder.io.outputs(0)
      onChipRamArbiter.io.inputs(1) << dBusDecoder.io.outputs(0)

      val spiXipControllerArbiter = BmbArbiter(
        inputsParameter = Seq(VexRiscvCoreParameter.iBusConfig, VexRiscvCoreParameter.dBusConfig),
        outputParameter = spiXipController.bmbParameter,
        lowerFirstPriority = true
      )
      spiXipController.cache.io.input << spiXipControllerArbiter.io.output
      spiXipControllerArbiter.io.inputs(0) << iBusDecoder.io.outputs(1)
      spiXipControllerArbiter.io.inputs(1) << dBusDecoder.io.outputs(1)

      wishboneBridge.bridge.io.input << dBusDecoder.io.outputs(2)

      /* Peripheral IP-Cores */
      val plicCtrl = WishbonePlic(parameter.plic, wishboneConfig)
      core.globalInterrupt := plicCtrl.io.interrupt
      addPeripheralDevice(plicCtrl.io.bus, 0x800000, 4 MB)

      val mtimerCtrl = WishboneMachineTimer(parameter.mtimer, wishboneConfig)
      core.mtimerInterrupt := mtimerCtrl.io.interrupt
      addPeripheralDevice(mtimerCtrl.io.bus, 0x20000, 4 kB)

      val resetCtrlMapper = WishboneResetController(parameter.resets, wishboneConfig)
      resetCtrlMapper.io.config <> resetCtrl.io.config
      addPeripheralDevice(resetCtrlMapper.io.bus, 0x21000, 4 kB)

      val clockCtrlMapper = WishboneClockController(parameter.clocks, wishboneConfig)
      clockCtrlMapper.io.config <> clockCtrl.io.config
      addPeripheralDevice(clockCtrlMapper.io.bus, 0x22000, 4 kB)

      addPeripheralDevice(spiXipController.ctrl.io.cfgSpiBus, 0x24000, 4 kB)
      addPeripheralDevice(spiXipController.ctrl.io.cfgXipBus, 0x25000, 4 kB)

      publishPeripheralComponents(wishboneBridge.bridge, plicCtrl)
    }
  }
}
