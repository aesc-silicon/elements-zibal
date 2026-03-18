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
import nafarr.bus.wishbone._

import nafarr.bus.bmb.BmbCache
import nafarr.system.mtimer.{WishboneMachineTimer, MachineTimerCtrl}
import nafarr.system.plic.{WishbonePlic, Plic, PlicCtrl}
import nafarr.system.reset.{WishboneResetController, ResetControllerCtrl}
import nafarr.system.clock.{WishboneClockController, ClockControllerCtrl}
import nafarr.memory.hyperbus.{BmbHyperBusGenericPhyCluster, HyperBus, HyperBusCtrl}
import nafarr.memory.spi.{BmbSpiXipController}
import nafarr.peripherals.com.spi.{Spi, SpiControllerCtrl}
import spinal.lib.com.jtag.Jtag

import vexriscv._
import vexriscv.ip._
import vexriscv.plugin._

object Nitrogen {

  case class Parameter(
      socParameter: SocParameter,
      onChipRamSize: BigInt,
      spiRomSize: BigInt,
      hyperbusPartitions: List[(BigInt, Boolean)],
      resetCtrl: (
          ResetControllerCtrl.Parameter
      ) => ResetControllerCtrl.ResetControllerBase,
      clockCtrl: (
          ClockControllerCtrl.Parameter,
          ResetControllerCtrl.ResetControllerBase
      ) => ClockControllerCtrl.ClockControllerBase,
      onChipRamLogic: (BmbParameter, BigInt) => (Component, Bmb) =
        (p: BmbParameter, onChipRamSize: BigInt) => {
          val ram = BmbOnChipRam(
            p = p,
            size = onChipRamSize
          )
          (ram, ram.io.bus)
        }
  ) extends PlatformParameter(socParameter) {
    val core = VexRiscvCoreParameter.mcu(0xa0000000L, 1024, 1024).plugins
    val mtimer = MachineTimerCtrl.Parameter.default
    val plic = PlicCtrl.Parameter.default(getSocParameter.getInterruptCount(0))
    val clocks = ClockControllerCtrl.Parameter(getKitParameter.clocks)
    val resets = ResetControllerCtrl.Parameter(getKitParameter.resets)
    val hyperbus = HyperBusCtrl.Parameter.default(hyperbusPartitions)
    val spi = SpiControllerCtrl.Parameter.xip()
  }

  class Nitrogen(parameter: Parameter) extends PlatformComponent(parameter) {
    val io_plat = new Bundle {
      val reset = in(Bool)
      val clock = in(Bool)
      val jtag = slave(Jtag())
      val hyperbus = master(HyperBus.Io(parameter.hyperbus))
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

    val resetCtrl = parameter.resetCtrl(parameter.resets)
    resetCtrl.io.mainReset := io_plat.reset
    resetCtrl.io.mainClock := io_plat.clock
    resetCtrl.io.trigger := 0

    val clockCtrl = parameter.clockCtrl(parameter.clocks, resetCtrl)
    ClockControllerCtrl.connect(parameter.clocks, clockCtrl, resetCtrl)
    clockCtrl.io.mainReset := io_plat.reset
    clockCtrl.io.mainClock := io_plat.clock

    val core = new ClockingArea(clockCtrl.getClockDomainByName("cpu")) {
      val mtimerInterrupt = Bool
      val globalInterrupt = Bool

      val configs = parameter.core += new DebugPlugin(clockCtrl.getClockDomainByName("debug"))
      for ((plugin, index) <- configs.zipWithIndex) plugin match {
        case p: IBusCachedPlugin =>
          configs(index) = new IBusCachedPlugin(
            resetVector = p.resetVector,
            config = p.config,
            memoryTranslatorPortConfig = p.memoryTranslatorPortConfig,
            instructionCacheGen = new IhpInstructionCache(_, _)
          )
        case p: DBusCachedPlugin =>
          configs(index) = new DBusCachedPlugin(
            config = p.config,
            memoryTranslatorPortConfig = p.memoryTranslatorPortConfig,
            dBusCmdMasterPipe = p.dBusCmdMasterPipe,
            dataCacheGen = new IhpDataCache(_, _)
          )
        case _ =>
      }

      val config = VexRiscvConfig(plugins = configs)

      val cpu = new VexRiscv(config)
      val internal = new Area {
        val iCacheBanks =
          cpu.service(classOf[IBusCachedPlugin]).cacheIp.asInstanceOf[IhpInstructionCache].banks
        val iCacheTags =
          cpu.service(classOf[IBusCachedPlugin]).cacheIp.asInstanceOf[IhpInstructionCache].ways
        val dCacheWays =
          cpu.service(classOf[DBusCachedPlugin]).cacheIp.asInstanceOf[IhpDataCache].ways
        val iBus = cpu.service(classOf[IBusCachedPlugin]).iBus
        val dBus = cpu.service(classOf[DBusCachedPlugin]).dBus
        val debugBus = cpu.service(classOf[DebugPlugin]).io.bus
        val debugResetOut = cpu.service(classOf[DebugPlugin]).io.resetOut
        val externalInterrupt = cpu.service(classOf[CsrPlugin]).externalInterrupt
        val timerInterrupt = cpu.service(classOf[CsrPlugin]).timerInterrupt
      }

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
    }

    val hyperbus = new ClockingArea(clockCtrl.getClockDomainByName("hyperbus")) {
      val mapping = SizeMapping(0x90000000L, 64 MB)
      val bmbParameter = BmbParameter(
        addressWidth = log2Up(mapping.size) + 2,
        dataWidth = 32,
        lengthWidth = 6,
        sourceWidth = 4,
        contextWidth = 4
      )
      val ctrl =
        BmbHyperBusGenericPhyCluster(parameter.hyperbus, bmbParameter, system.wishboneConfig)
      io_plat.hyperbus <> ctrl.io.hyperbus

      val bmbCc = BmbCcFifo(
        p = bmbParameter,
        cmdDepth = 2,
        rspDepth = 2,
        inputCd = clockCtrl.getClockDomainByName("system"),
        outputCd = clockCtrl.getClockDomainByName("hyperbus")
      )
      ctrl.io.dataBus << bmbCc.io.output

      val wishboneCc = WishboneCcFifo(
        cfg = system.wishboneConfig,
        inputCd = clockCtrl.getClockDomainByName("system"),
        outputCd = clockCtrl.getClockDomainByName("hyperbus")
      )
      ctrl.io.cfgBus << wishboneCc.io.output
    }

    val spiXip = new ClockingArea(clockCtrl.getClockDomainByName("spiXip")) {
      val mapping = SizeMapping(0xa0000000L, parameter.spiRomSize)
      val bmbParameter = BmbParameter(
        addressWidth = log2Up(mapping.size) + 2,
        dataWidth = 32,
        lengthWidth = 6,
        sourceWidth = 4,
        contextWidth = 4
      )
      val ctrl = BmbSpiXipController(parameter.spi, bmbParameter, system.wishboneConfig)
      io_plat.spi <> ctrl.io.spi

      val bmbCc = BmbCcFifo(
        p = bmbParameter,
        cmdDepth = 2,
        rspDepth = 2,
        inputCd = clockCtrl.getClockDomainByName("system"),
        outputCd = clockCtrl.getClockDomainByName("spiXip")
      )
      ctrl.io.dataBus << bmbCc.io.output

      val wishboneSpiCc = WishboneCcFifo(
        cfg = system.wishboneConfig,
        inputCd = clockCtrl.getClockDomainByName("system"),
        outputCd = clockCtrl.getClockDomainByName("spiXip")
      )
      ctrl.io.cfgSpiBus << wishboneSpiCc.io.output

      val wishboneXipCc = WishboneCcFifo(
        cfg = system.wishboneConfig,
        inputCd = clockCtrl.getClockDomainByName("system"),
        outputCd = clockCtrl.getClockDomainByName("spiXip")
      )
      ctrl.io.cfgXipBus << wishboneXipCc.io.output
    }

    val crossbar = new ClockingArea(clockCtrl.getClockDomainByName("system")) {
      /* Generate BMB Crossbar */
      val iBusDecoder = BmbDecoder(
        p = VexRiscvCoreParameter.iBusConfig,
        mappings = Seq(system.onChipRam.mapping, hyperbus.mapping, spiXip.mapping),
        capabilities =
          Seq(system.onChipRam.bmbParameter, hyperbus.bmbParameter, spiXip.bmbParameter)
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
        mappings = Seq(
          system.onChipRam.mapping,
          hyperbus.mapping,
          spiXip.mapping,
          system.wishboneBridge.mapping
        ),
        capabilities = Seq(
          system.onChipRam.bmbParameter,
          hyperbus.bmbParameter,
          spiXip.bmbParameter,
          system.wishboneBridge.bmbParameter
        )
      )
      dBusDecoder.io.input << core.dBus.pipelined(
        cmdValid = true,
        cmdReady = true,
        rspValid = true,
        rspReady = true
      )

      val onChipRamArbiter = BmbArbiter(
        inputsParameter = Seq(VexRiscvCoreParameter.iBusConfig, VexRiscvCoreParameter.dBusConfig),
        outputParameter = system.onChipRam.bmbParameter,
        lowerFirstPriority = true
      )
      system.onChipRam.port << onChipRamArbiter.io.output
      onChipRamArbiter.io.inputs(0) << iBusDecoder.io.outputs(0)
      onChipRamArbiter.io.inputs(1) << dBusDecoder.io.outputs(0)

      val hyperbusArbiter = BmbArbiter(
        inputsParameter = Seq(VexRiscvCoreParameter.iBusConfig, VexRiscvCoreParameter.dBusConfig),
        outputParameter = hyperbus.bmbParameter,
        lowerFirstPriority = true
      )
      hyperbus.bmbCc.io.input << hyperbusArbiter.io.output
      hyperbusArbiter.io.inputs(0) << iBusDecoder.io.outputs(1)
      hyperbusArbiter.io.inputs(1) << dBusDecoder.io.outputs(1)

      val spiXipControllerArbiter = BmbArbiter(
        inputsParameter = Seq(VexRiscvCoreParameter.iBusConfig, VexRiscvCoreParameter.dBusConfig),
        outputParameter = spiXip.bmbParameter,
        lowerFirstPriority = true
      )
      spiXip.bmbCc.io.input << spiXipControllerArbiter.io.output
      spiXipControllerArbiter.io.inputs(0) << iBusDecoder.io.outputs(2)
      spiXipControllerArbiter.io.inputs(1) << dBusDecoder.io.outputs(2)

      system.wishboneBridge.bridge.io.input << dBusDecoder.io.outputs(3)

      /* Peripheral IP-Cores */
      val plicCtrl = WishbonePlic(parameter.plic, system.wishboneConfig)
      core.globalInterrupt := plicCtrl.io.interrupt
      addPeripheralDevice(plicCtrl.io.bus, 0x800000, 4 MB)

      val mtimerCtrl = WishboneMachineTimer(parameter.mtimer, system.wishboneConfig)
      core.mtimerInterrupt := mtimerCtrl.io.interrupt
      addPeripheralDevice(mtimerCtrl.io.bus, 0x20000, 4 kB)

      val resetCtrlMapper = WishboneResetController(parameter.resets, system.wishboneConfig)
      resetCtrlMapper.io.config <> resetCtrl.io.config
      addPeripheralDevice(resetCtrlMapper.io.bus, 0x21000, 4 kB)

      val clockCtrlMapper = WishboneClockController(parameter.clocks, system.wishboneConfig)
      clockCtrlMapper.io.config <> clockCtrl.io.config
      addPeripheralDevice(clockCtrlMapper.io.bus, 0x22000, 4 kB)

      addPeripheralDevice(hyperbus.wishboneCc.io.input, 0x23000, 4 kB)

      addPeripheralDevice(spiXip.wishboneSpiCc.io.input, 0x24000, 4 kB)
      addPeripheralDevice(spiXip.wishboneXipCc.io.input, 0x25000, 4 kB)

      publishPeripheralComponents(system.wishboneBridge.bridge, plicCtrl)
    }
  }
}
