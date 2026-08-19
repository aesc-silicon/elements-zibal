// SPDX-FileCopyrightText: 2026 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal.platform

import spinal.core._
import spinal.lib._

import zibal.soc.SocParameter

import spinal.lib.bus.misc.{SizeMapping, AddressMapping}
import spinal.lib.bus.tilelink.{
  Bus => TileLinkBus,
  BusParameter => TileLinkParameter,
  Arbiter,
  Decoder,
  DecoderDownSpec,
  FifoCc,
  NodeParameters,
  M2sParameters,
  M2sSupport,
  M2sTransfers,
  SizeRange
}
import spinal.lib.system.tag.{MappedNode, MappedTransfers}

import nafarr.system.mtimer.{TileLinkMachineTimer, MachineTimerCtrl}
import nafarr.system.plic.{TileLinkPlic, PlicCtrl}
import nafarr.system.reset.{TileLinkResetController, ResetControllerCtrl}
import nafarr.system.clock.{TileLinkClockController, ClockControllerCtrl}
import nafarr.system.syscon.{TileLinkSyscon, Syscon}
import nafarr.system.esm.{TileLinkEsm, EsmCtrl}
import nafarr.{Vendor, Platform, PlatformClass, Feature}
import nafarr.system.timer.{TileLinkTimer, TimerCtrl}
import nafarr.system.watchdog.{TileLinkWatchdog, WatchdogCtrl}
import nafarr.memory.spi.{TileLinkSpiXipController}
import nafarr.memory.ocram.TileLinkOnChipRam
import nafarr.memory.hyperbus.{
  TileLinkHyperBusCluster,
  TileLinkHyperBusGenericPhyCluster,
  TileLinkHyperBusGenericDdrPhyCluster,
  HyperBus,
  HyperBusCtrl
}
import nafarr.peripherals.com.spi.{Spi, SpiControllerCtrl}
import nafarr.cores.cpu.vexiiriscv.{VexiiRiscvCoreParameter, TileLinkVexiiRiscv, VexiiRiscvBlock}

import spinal.lib.com.jtag.Jtag

object Nitrogen {

  case class Parameter(
      socParameter: SocParameter,
      onChipRamSize: BigInt,
      spiFlashSize: BigInt,
      hyperbusPartitions: List[(BigInt, Boolean)],
      iCacheSize: BigInt,
      dCacheSize: BigInt,
      btbSets: Int = 16,
      resetCtrl: (
          ResetControllerCtrl.Parameter
      ) => ResetControllerCtrl.ResetControllerBase,
      clockCtrl: (
          ClockControllerCtrl.Parameter,
          ResetControllerCtrl.ResetControllerBase
      ) => ClockControllerCtrl.ClockControllerBase,
      hasEsm: Boolean = true,
      onChipRamLogic: (TileLinkParameter, BigInt) => (Component, TileLinkBus) =
        (p: TileLinkParameter, size: BigInt) => {
          val ram = TileLinkOnChipRam(p = p, size = size)
          (ram, ram.io.bus)
        },
      hyperBusLogic: (
          HyperBusCtrl.Parameter,
          TileLinkParameter,
          TileLinkParameter
      ) => TileLinkHyperBusCluster =
        (hp: HyperBusCtrl.Parameter, bp: TileLinkParameter, cp: TileLinkParameter) => {
          TileLinkHyperBusGenericPhyCluster(hp, bp, cp)
        }
  ) extends PlatformParameter(socParameter) {
    val ocramMapping = SizeMapping(0x80000000L, onChipRamSize)
    val hyperbusMapping = SizeMapping(0x90000000L, 64 MB)
    val spiMapping = SizeMapping(0xa0000000L, spiFlashSize)
    val periphMapping = SizeMapping(0xf0000000L, 16 MB)
    val hyperbusUncachedMapping = SizeMapping(0xb0000000L, 64 MB)

    val core = VexiiRiscvCoreParameter.performance(
      0xa0000000L,
      iCacheSize = iCacheSize,
      dCacheSize = dCacheSize,
      btbSets = btbSets,
      pmpRegions = 8,
      withCompressed = true,
      mainRegions = Seq(ocramMapping, hyperbusMapping, spiMapping),
      ioRegions = Seq(periphMapping, hyperbusUncachedMapping)
    )
    val mtimer = MachineTimerCtrl.Parameter.default
    val clocks = ClockControllerCtrl.Parameter(
      getKitParameter.clocks,
      getKitParameter.inputClock
    )
    val resets = ResetControllerCtrl.Parameter(getKitParameter.resets)
    def buildSyscon(features: List[Feature.E] = Nil) = Syscon.Parameter(
      vendor = getBoardParameter.sysconInfo.vendor,
      platform = Platform.Nitrogen,
      platformClass = PlatformClass.NonMetal,
      product = getBoardParameter.sysconInfo.product,
      refClockHz = getKitParameter.inputClock.frequency.toLong,
      siliconMajor = getBoardParameter.sysconInfo.siliconMajor,
      siliconMinor = getBoardParameter.sysconInfo.siliconMinor,
      features = features
    )
    val hyperbus = HyperBusCtrl.Parameter.default(hyperbusPartitions)
    val spi = SpiControllerCtrl.Parameter.xip()
    val timer = TimerCtrl.Parameter.small()
    val watchdog = WatchdogCtrl.Parameter.windowed()
    val platformErrors = Seq(watchdog, hyperbus)
    val esm = EsmCtrl.Parameter.small(getSocParameter.getErrorCount(platformErrors.size))
    val platformIrqs = Seq(timer, watchdog, esm)
    val plic = PlicCtrl.Parameter.default(getSocParameter.getInterruptCount(platformIrqs.size))
  }

  class Nitrogen(parameter: Parameter) extends PlatformComponent(parameter) {

    val io_plat = new Bundle {
      val reset = in(Bool)
      val clock = in(Bool)
      val jtag = slave(Jtag())
      val hyperbus = master(HyperBus.Io(parameter.hyperbus))
      val spiXip = new Bundle {
        val spi = master(Spi.Io(parameter.spi.io))
        val reset = out(Bool())
      }
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

    io_plat.spiXip.reset := resetCtrl.resetDict
      .get("flash")
      .map(reset => resetCtrl.io.resets(reset._2))
      .getOrElse(clockCtrl.getClockDomainByName("system").reset)

    val core = new ClockingArea(clockCtrl.getClockDomainByName("system")) {
      val cpu = new VexiiRiscvBlock(
        TileLinkVexiiRiscv.Parameter(
          parameter.core.plugins,
          parameter.core.iBusTlParam,
          parameter.core.dBusTlParam,
          parameter.core.dIoBusTlParam
        ),
        clockCtrl.getClockDomainByName("debug")
      )

      clockCtrl.getClockDomainByName("debug") {
        val ndmreset = RegNext(cpu.ndmreset)
        for (domain <- Seq("system", "flash", "xip")) {
          if (resetCtrl.triggerDict.contains(domain)) {
            resetCtrl.triggerByNameWithCond(domain, ndmreset)
          }
        }
      }

      io_plat.jtag <> cpu.jtag
    }

    val system = new ClockingArea(clockCtrl.getClockDomainByName("system")) {

      val memParam = parameter.core.iBusTlParam
      val periphParam = TileLinkParameter.simple(32, 32, memParam.sizeBytes, 1)

      val memNode = NodeParameters(
        M2sParameters(
          M2sSupport(
            transfers = M2sTransfers(
              get = SizeRange.upTo(memParam.sizeBytes),
              putFull = SizeRange.upTo(memParam.sizeBytes),
              putPartial = SizeRange.upTo(memParam.sizeBytes)
            ),
            addressWidth = memParam.addressWidth,
            dataWidth = memParam.dataWidth
          ),
          1 << memParam.sourceWidth
        )
      )

      def downSpec(mappings: SizeMapping*): DecoderDownSpec = {
        val transfers = M2sTransfers(
          get = SizeRange.upTo(memParam.sizeBytes),
          putFull = SizeRange.upTo(memParam.sizeBytes),
          putPartial = SizeRange.upTo(memParam.sizeBytes)
        )
        DecoderDownSpec(
          mappeds = mappings.toList.map(m =>
            MappedTransfers(MappedNode(Component.current, m, Nil), transfers)
          ),
          transformers = Nil,
          nodeParam = memNode
        )
      }

      val memSlaves = Seq(
        downSpec(parameter.ocramMapping),
        downSpec(parameter.hyperbusMapping),
        downSpec(parameter.spiMapping)
      )
      val dIoMemSlaves = Seq(
        downSpec(parameter.ocramMapping),
        downSpec(parameter.hyperbusMapping, parameter.hyperbusUncachedMapping),
        downSpec(parameter.spiMapping)
      )
      val iBusDecoder = Decoder(memNode, memSlaves)
      val dBusDecoder = Decoder(memNode, memSlaves)
      val dIoBusDecoder = Decoder(memNode, dIoMemSlaves :+ downSpec(parameter.periphMapping))

      iBusDecoder.io.up <> core.cpu.iBus
      dBusDecoder.io.up <> core.cpu.dBus
      dIoBusDecoder.io.up <> core.cpu.dIoBus

      val arbiterDownNode = Arbiter.downNodeFrom(Seq(memNode, memNode, memNode))
      val ocramArbiter = Arbiter(Seq(memNode, memNode, memNode), arbiterDownNode)
      val hyperbusArbiter = Arbiter(Seq(memNode, memNode, memNode), arbiterDownNode)
      val spiArbiter = Arbiter(Seq(memNode, memNode, memNode), arbiterDownNode)

      iBusDecoder.io.downs(0) <> ocramArbiter.io.ups(0)
      dBusDecoder.io.downs(0) <> ocramArbiter.io.ups(1)
      dIoBusDecoder.io.downs(0) <> ocramArbiter.io.ups(2)

      iBusDecoder.io.downs(1) <> hyperbusArbiter.io.ups(0)
      dBusDecoder.io.downs(1) <> hyperbusArbiter.io.ups(1)
      dIoBusDecoder.io.downs(1) <> hyperbusArbiter.io.ups(2)

      iBusDecoder.io.downs(2) <> spiArbiter.io.ups(0)
      dBusDecoder.io.downs(2) <> spiArbiter.io.ups(1)
      dIoBusDecoder.io.downs(2) <> spiArbiter.io.ups(2)

      val onChipRam = new Area {
        val mapping = parameter.ocramMapping
        val busParam = ocramArbiter.io.down.p
        val (ctrl, port) = parameter.onChipRamLogic(busParam, mapping.size)
        port <> ocramArbiter.io.down
      }

      val hyperbus = new Area {
        val mapping = parameter.hyperbusMapping
        val busParam = hyperbusArbiter.io.down.p
        val systemCd = clockCtrl.getClockDomainByName("system")
        val hyperbusCd = clockCtrl.getClockDomainByName("hyperbus")
        val cc = FifoCc(busParam, systemCd, hyperbusCd, 8, 2, 2, 8, 2)
        cc.io.input <> hyperbusArbiter.io.down
        val cfgCc = FifoCc(periphParam, systemCd, hyperbusCd, 2, 2, 2, 2, 2)
        val cluster = hyperbusCd {
          parameter.hyperBusLogic(parameter.hyperbus, busParam, periphParam)
        }
        cluster.io.dataBus <> cc.io.output
        cluster.io.cfgBus <> cfgCc.io.output
        io_plat.hyperbus <> cluster.io.hyperbus
        val error = systemCd(BufferCC(cluster.io.error, False))
      }

      val spiXip = new Area {
        val mapping = parameter.spiMapping
        val busParam = spiArbiter.io.down.p
        val systemCd = clockCtrl.getClockDomainByName("system")
        val xipCd = clockCtrl.getClockDomainByName("xip")
        val ctrl = xipCd { TileLinkSpiXipController(parameter.spi, busParam) }
        val cc = FifoCc(busParam, systemCd, xipCd, 8, 2, 2, 8, 2)
        cc.io.input <> spiArbiter.io.down
        ctrl.io.bus <> cc.io.output
        val cfgSpiCc = FifoCc(ctrl.io.cfgSpiBus.p, systemCd, xipCd, 2, 2, 2, 2, 2)
        cfgSpiCc.io.output <> ctrl.io.cfgSpiBus
        val cfgXipCc = FifoCc(ctrl.io.cfgXipBus.p, systemCd, xipCd, 2, 2, 2, 2, 2)
        cfgXipCc.io.output <> ctrl.io.cfgXipBus
        io_plat.spiXip.spi <> ctrl.io.spi
      }

      val periphBusPort = TileLinkBus(periphParam)
      dIoBusDecoder.io.downs(3) <> periphBusPort

      val peripheralCd = clockCtrl.getClockDomainByName("peripheral")
      val periphSystemCd = clockCtrl.getClockDomainByName("system")
      val periphCc = FifoCc(periphParam, periphSystemCd, peripheralCd, 4, 2, 2, 4, 2)
      addPeripheralDevice(periphCc.io.input, 0x0, 128 kB)
      publishPeripheralDomain("peripheral", periphCc.io.output, 0xf0000000L)

      val plicCtrl = TileLinkPlic(parameter.plic)
      core.cpu.globalInterrupt := plicCtrl.io.interrupt
      addPeripheralDevice(plicCtrl.io.bus, 0x800000, 4 MB)

      val mtimerCtrl = TileLinkMachineTimer(parameter.mtimer)
      core.cpu.mtimerInterrupt := mtimerCtrl.io.interrupt
      addPeripheralDevice(mtimerCtrl.io.bus, 0x20000, 4 kB)

      val resetCtrlMapper = TileLinkResetController(parameter.resets)
      resetCtrlMapper.io.config <> resetCtrl.io.config
      addPeripheralDevice(resetCtrlMapper.io.bus, 0x21000, 4 kB)

      val clockCtrlMapper = TileLinkClockController(parameter.clocks)
      clockCtrlMapper.io.config <> clockCtrl.io.config
      addPeripheralDevice(clockCtrlMapper.io.bus, 0x22000, 4 kB)

      addPeripheralDevice(spiXip.cfgSpiCc.io.input, 0x24000, 4 kB)
      addPeripheralDevice(spiXip.cfgXipCc.io.input, 0x25000, 4 kB)

      val timerCtrlMapper = TileLinkTimer(parameter.timer)
      addPeripheralDevice(timerCtrlMapper.io.bus, 0x26000, 4 kB)
      addInterrupt(timerCtrlMapper.io.interrupt)

      val watchdogCtrlMapper = TileLinkWatchdog(parameter.watchdog)
      addPeripheralDevice(watchdogCtrlMapper.io.bus, 0x27000, 4 kB)
      addInterrupt(watchdogCtrlMapper.io.interrupt)
      addError(watchdogCtrlMapper.io.error)
      addError(hyperbus.error)

      val (esmInterrupt: Bool, esmError: Bool) = if (parameter.hasEsm) {
        val esmCtrlMapper = TileLinkEsm(parameter.esm)
        addPeripheralDevice(esmCtrlMapper.io.bus, 0x28000, 4 kB)
        publishEsm(esmCtrlMapper)
        (
          esmCtrlMapper.io.infoInterrupt || esmCtrlMapper.io.warnInterrupt,
          esmCtrlMapper.io.errorSignal
        )
      } else {
        (False, False)
      }
      addInterrupt(esmInterrupt)
      resetCtrl.triggerByNameWithCond("system", esmError)
      resetCtrl.triggerByNameWithCond("debug", esmError)
      if (resetCtrl.triggerDict.contains("flash")) {
        resetCtrl.triggerByNameWithCond("flash", esmError)
      }

      addPeripheralDevice(hyperbus.cfgCc.io.input, 0x29000, 4 kB)

      val sysconCtrlMapper = TileLinkSyscon(parameter.buildSyscon(getSysconFeatures()))
      addPeripheralDevice(sysconCtrlMapper.io.bus, 0x23000, 4 kB)

      publishPeripheralComponents(periphBusPort, 0xf0000000L, plicCtrl)
    }
  }
}
