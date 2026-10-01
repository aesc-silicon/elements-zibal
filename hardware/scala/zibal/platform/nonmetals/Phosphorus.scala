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
  SizeRange,
  WidthAdapter
}
import spinal.lib.system.tag.{MappedNode, MappedTransfers}

import nafarr.system.mtimer.{TileLinkMachineTimer, MachineTimerCtrl}
import nafarr.system.plic.{TileLinkPlic, PlicCtrl}
import nafarr.system.reset.{TileLinkResetController, ResetControllerCtrl}
import nafarr.system.clock.{TileLinkClockController, ClockControllerCtrl}
import nafarr.system.syscon.{TileLinkSyscon, Syscon}
import nafarr.system.esm.{TileLinkEsm, EsmCtrl}
import nafarr.system.dma.{TileLinkDma, DmaCtrl, DmaHandshake, DmaHandshakeCc}
import nafarr.{Vendor, Platform, PlatformClass, Feature}
import nafarr.system.timer.{TileLinkTimer, TimerCtrl}
import nafarr.system.watchdog.{TileLinkWatchdog, WatchdogCtrl}
import nafarr.bus.tilelink.TileLinkSerializer
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
import nafarr.cores.cpu.vexiiriscv.{
  Fpu,
  VexiiRiscvCoreParameter,
  TileLinkVexiiRiscv,
  VexiiRiscvBlock,
  DebugTransport
}

import spinal.lib.com.jtag.Jtag
import spinal.lib.com.swd.Swd

/** Oxygen with a dual-issue core and a wide system bus.
  *
  * `memDataWidth` (32, 64 or 128 bits) sets the minimum width of the instruction and cached
  * data buses. The system bus runs at the widest CPU bus: dual-issue fetches at least 64
  * bits. OCRAM, HyperBus, the SPI flash and the DMA use the system width; narrower CPU buses
  * are widened and the peripheral bus stays 32 bits wide.
  *
  * The DMA is a second bus master next to the CPU. It is not cache coherent: software cleans
  * or invalidates DMA buffers with Zicbom or places them in the uncached HyperBus window.
  */
object Phosphorus {

  case class Parameter(
      socParameter: SocParameter,
      onChipRamSize: BigInt,
      spiFlashSize: BigInt,
      hyperbusPartitions: List[(BigInt, Boolean)],
      iCacheSize: BigInt,
      dCacheSize: BigInt,
      btbSets: Int = 16,
      // 512 B keeps 256 rows with the four counters per row of a 64-bit fetch.
      gshareBytes: Int = 512,
      memDataWidth: Int = 64,
      dma: DmaCtrl.Parameter = DmaCtrl.Parameter(channels = 4, requestLines = 16, burstBytes = 64),
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
        },
      debugTransport: DebugTransport = DebugTransport.Jtag
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
      gshareBytes = gshareBytes,
      pmpRegions = 8,
      withCompressed = true,
      withCacheOps = true,
      withBitManip = true,
      withAtomics = true,
      withDualIssue = true,
      memDataWidth = memDataWidth,
      mainRegions = Seq(ocramMapping, hyperbusMapping, spiMapping),
      ioRegions = Seq(periphMapping, hyperbusUncachedMapping),
      debugTransport = debugTransport
    )
    // System bus width: the widest CPU bus.
    val dataWidth =
      Seq(core.iBusTlParam, core.dBusTlParam, core.dIoBusTlParam).map(_.dataWidth).max
    val dmaParam = dma.copy(dataWidth = dataWidth)
    require(
      dmaParam.burstBytes <= core.iBusTlParam.sizeBytes,
      s"DMA bursts must not exceed the ${core.iBusTlParam.sizeBytes} byte memory transfers"
    )
    val mtimer = MachineTimerCtrl.Parameter.default
    val clocks = ClockControllerCtrl.Parameter(
      getKitParameter.clocks,
      getKitParameter.inputClock
    )
    val resets = ResetControllerCtrl.Parameter(getKitParameter.resets)
    def buildSyscon(features: List[Feature.E] = Nil) = Syscon.Parameter(
      vendor = getBoardParameter.sysconInfo.vendor,
      platform = Platform.Phosphorus,
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
    val platformIrqs = Seq(timer, watchdog, esm, dmaParam)
    val plic = PlicCtrl.Parameter.default(getSocParameter.getInterruptCount(platformIrqs.size))
  }

  class Phosphorus(parameter: Parameter) extends PlatformComponent(parameter) {

    val io_plat = new Bundle {
      val reset = in(Bool)
      val clock = in(Bool)
      val jtag = (parameter.debugTransport == DebugTransport.Jtag) generate slave(Jtag())
      val swd = (parameter.debugTransport == DebugTransport.Swd) generate slave(Swd())
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

      parameter.debugTransport match {
        case DebugTransport.Jtag => io_plat.jtag <> cpu.jtag
        case DebugTransport.Swd => io_plat.swd <> cpu.swd
      }
    }

    // -----------------------------------------------------------------------
    // System interconnect
    //
    //   iBus   ──→ [widen] ──→ iDecoder   ──┬──→ ocramArbiter    ──→ OCRAM
    //   dBus   ──→ [widen] ──→ dDecoder   ──┼──→ hyperbusArbiter ──→ HyperBus
    //   dIoBus ──→ [widen] ──→ dIoDecoder ──┼──→ spiArbiter      ──→ SpiXip
    //   DMA    ──────────────→ dmaDecoder ──┴──→ periphArbiter   ──→ serializer
    //                                                           ──→ [narrow] ──→ periphBus
    //
    // Everything left of the narrowing adapter runs at the system width; [widen] is only
    // present for CPU buses narrower than that, [narrow] only when it exceeds 32 bits.
    // iBus and dBus have no access to the peripheral bus. The serializer lets one
    // transaction through at a time and restores the arbiter's source bits, which the
    // peripheral decoder drops.
    // -----------------------------------------------------------------------
    val system = new ClockingArea(clockCtrl.getClockDomainByName("system")) {

      val memParam = parameter.core.iBusTlParam.copy(dataWidth = parameter.dataWidth)
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
      val ioSlaves = Seq(
        downSpec(parameter.ocramMapping),
        downSpec(parameter.hyperbusMapping, parameter.hyperbusUncachedMapping),
        downSpec(parameter.spiMapping),
        downSpec(parameter.periphMapping)
      )
      val iBusDecoder = Decoder(memNode, memSlaves)
      val dBusDecoder = Decoder(memNode, memSlaves)
      val dIoBusDecoder = Decoder(memNode, ioSlaves)
      val dmaDecoder = Decoder(memNode, ioSlaves)

      // Widens a CPU bus to the system width.
      def widen(bus: TileLinkBus, name: String): TileLinkBus = {
        if (bus.p.dataWidth == memParam.dataWidth) {
          bus
        } else {
          val adapter = new WidthAdapter(bus.p, bus.p.copy(dataWidth = memParam.dataWidth))
            .setName(s"${name}WidthAdapter")
          adapter.io.up <> bus
          adapter.io.down
        }
      }
      iBusDecoder.io.up <> widen(core.cpu.iBus, "iBus")
      dBusDecoder.io.up <> widen(core.cpu.dBus, "dBus")
      dIoBusDecoder.io.up <> widen(core.cpu.dIoBus, "dIoBus")

      val memMasters = Seq(memNode, memNode, memNode, memNode)
      val arbiterDownNode = Arbiter.downNodeFrom(memMasters)
      val ocramArbiter = Arbiter(memMasters, arbiterDownNode)
      val hyperbusArbiter = Arbiter(memMasters, arbiterDownNode)
      val spiArbiter = Arbiter(memMasters, arbiterDownNode)
      val periphMasters = Seq(memNode, memNode)
      val periphArbiter = Arbiter(periphMasters, Arbiter.downNodeFrom(periphMasters))

      for ((arbiter, slave) <- Seq(ocramArbiter, hyperbusArbiter, spiArbiter).zipWithIndex) {
        iBusDecoder.io.downs(slave) <> arbiter.io.ups(0)
        dBusDecoder.io.downs(slave) <> arbiter.io.ups(1)
        dIoBusDecoder.io.downs(slave) <> arbiter.io.ups(2)
        dmaDecoder.io.downs(slave) <> arbiter.io.ups(3)
      }
      dIoBusDecoder.io.downs(3) <> periphArbiter.io.ups(0)
      dmaDecoder.io.downs(3) <> periphArbiter.io.ups(1)

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

      val periphSerializer = TileLinkSerializer(periphArbiter.io.down.p, periphParam.sourceWidth)
      periphSerializer.io.up <> periphArbiter.io.down
      val periphBusPort = if (periphSerializer.downParam.dataWidth == periphParam.dataWidth) {
        periphSerializer.io.down
      } else {
        val adapter = new WidthAdapter(
          periphSerializer.downParam,
          periphSerializer.downParam.copy(dataWidth = periphParam.dataWidth)
        ).setName("periphWidthAdapter")
        adapter.io.up <> periphSerializer.io.down
        adapter.io.down
      }

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

      val dmaCtrl = TileLinkDma(parameter.dmaParam)
      dmaDecoder.io.up <> dmaCtrl.io.mem
      addPeripheralDevice(dmaCtrl.io.bus, 0x2a000, 4 kB)
      addInterrupt(dmaCtrl.io.interrupt)

      addSyscon(0x23000, 4 kB) { features =>
        TileLinkSyscon(parameter.buildSyscon(features))
      }

      publishPeripheralComponents(periphBusPort, 0xf0000000L, plicCtrl)
    }

    /** Connects a peripheral's DMA request handshake to the next free DMA request line.
      *
      * Handshakes outside the system clock domain are synchronized by a DmaHandshakeCc.
      * Returns the request line, which software selects in the channel `req_sel` field.
      */
    def addDmaRequest(handshake: DmaHandshake): Int = {
      val line = dmaRequestMapping.size
      require(
        line < parameter.dmaParam.requestLines,
        s"Only ${parameter.dmaParam.requestLines} DMA request lines available"
      )
      dmaRequestMapping += handshake

      val systemCd = clockCtrl.getClockDomainByName("system")
      val dmaPort = system.dmaCtrl.io.request(line)
      if (ClockDomain.current == systemCd) {
        dmaPort <> handshake
      } else {
        val cc = DmaHandshakeCc(ClockDomain.current, systemCd).setName(s"dmaRequestCc_$line")
        cc.io.peripheral <> handshake
        dmaPort <> cc.io.dma
      }
      line
    }
  }
}
