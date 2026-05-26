// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal.platform

import spinal.core._
import spinal.lib._

import zibal.misc.BaremetalTools
import zibal.misc.ElementsConfig
import zibal.soc.SocParameter

import spinal.lib.bus.misc.{SizeMapping, AddressMapping}
import spinal.lib.bus.tilelink.{Bus => TileLinkBus, BusParameter => TileLinkParameter}

import nafarr.bus.tilelink.{TileLinkCache, TileLinkDecoder, TileLinkArbiter}
import nafarr.system.mtimer.{TileLinkMachineTimer, MachineTimerCtrl}
import nafarr.system.plic.{TileLinkPlic, PlicCtrl}
import nafarr.system.reset.{TileLinkResetController, ResetControllerCtrl}
import nafarr.system.clock.{TileLinkClockController, ClockControllerCtrl}
import nafarr.system.syscon.{TileLinkSyscon, Syscon}
import nafarr.system.esm.{TileLinkEsm, EsmCtrl}
import nafarr.{Vendor, Platform, PlatformClass, Feature}
import nafarr.system.semaphore.{TileLinkSemaphore, SemaphoreCtrl}
import nafarr.system.timer.{TileLinkTimer, TimerCtrl}
import nafarr.system.watchdog.{TileLinkWatchdog, WatchdogCtrl}
import nafarr.memory.spi.{TileLinkSpiXipController}
import nafarr.memory.ocram.TileLinkOnChipRam
import nafarr.peripherals.com.spi.{Spi, SpiControllerCtrl}
import nafarr.cores.cpu.vexiiriscv.{VexiiRiscvCoreParameter, TileLinkVexiiRiscv}

import spinal.lib.com.jtag.Jtag

object Hydrogen {

  case class Parameter(
      socParameter: SocParameter,
      onChipRamSize: BigInt,
      spiRomSize: BigInt,
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
        }
  ) extends PlatformParameter(socParameter) {
    val core = VexiiRiscvCoreParameter.realtime(0xa0000000L)
    val mtimer = MachineTimerCtrl.Parameter.default
    val clocks = ClockControllerCtrl.Parameter(getKitParameter.clocks)
    val resets = ResetControllerCtrl.Parameter(getKitParameter.resets)
    def buildSyscon(features: List[Feature.E] = Nil) = Syscon.Parameter(
      vendor = getBoardParameter.sysconInfo.vendor,
      platform = Platform.Hydrogen,
      platformClass = PlatformClass.NonMetal,
      product = getBoardParameter.sysconInfo.product,
      refClockHz =
        getKitParameter.clocks.find(_.name == "system").map(_.frequency.toLong).getOrElse(0L),
      siliconMajor = getBoardParameter.sysconInfo.siliconMajor,
      siliconMinor = getBoardParameter.sysconInfo.siliconMinor,
      features = features
    )
    val spi = SpiControllerCtrl.Parameter.xip()
    val timer = TimerCtrl.Parameter.small()
    val watchdog = WatchdogCtrl.Parameter.windowed()
    val platformErrors = Seq(watchdog)
    val esm = EsmCtrl.Parameter.small(getSocParameter.getErrorCount(platformErrors.size))
    val platformIrqs = Seq(timer, watchdog, esm)
    val plic = PlicCtrl.Parameter.default(getSocParameter.getInterruptCount(platformIrqs.size))
  }

  class Hydrogen(parameter: Parameter) extends PlatformComponent(parameter) {

    // -----------------------------------------------------------------------
    // Platform IO
    // -----------------------------------------------------------------------
    val io_plat = new Bundle {
      val reset = in(Bool)
      val clock = in(Bool)
      val jtag = slave(Jtag())
      val spi = master(Spi.Io(parameter.spi.io))
    }

    def prepareBaremetal(name: String, elementsConfig: ElementsConfig.ElementsConfig) {
      val header = BaremetalTools.Header(elementsConfig, name)
      header.generateTileLink(
        this.system.periphMapping,
        this.tileLinkMapping,
        this.irqMapping,
        this.errorMapping
      )
    }

    override def initOnChipRam(path: String) {}

    // -----------------------------------------------------------------------
    // Reset and clock controllers
    // -----------------------------------------------------------------------
    val resetCtrl = parameter.resetCtrl(parameter.resets)
    resetCtrl.io.mainReset := io_plat.reset
    resetCtrl.io.mainClock := io_plat.clock
    resetCtrl.io.trigger := 0

    val clockCtrl = parameter.clockCtrl(parameter.clocks, resetCtrl)
    ClockControllerCtrl.connect(parameter.clocks, clockCtrl, resetCtrl)
    clockCtrl.io.mainReset := io_plat.reset
    clockCtrl.io.mainClock := io_plat.clock

    // -----------------------------------------------------------------------
    // CPU (debug clock domain set explicitly; system CD from ClockingArea)
    // -----------------------------------------------------------------------
    val core = new ClockingArea(clockCtrl.getClockDomainByName("system")) {
      val cpu = new TileLinkVexiiRiscv(
        TileLinkVexiiRiscv.Parameter(
          parameter.core.plugins,
          parameter.core.iBusTlParam,
          parameter.core.dBusTlParam
        ),
        clockCtrl.getClockDomainByName("debug")
      )

      clockCtrl.getClockDomainByName("debug") {
        resetCtrl.triggerByNameWithCond("system", RegNext(cpu.ndmreset))
      }

      io_plat.jtag <> cpu.jtag
    }

    // -----------------------------------------------------------------------
    // System interconnect — dual-decoder crossbar
    //
    //   iBus ──→ iDecoder ──┬──→ ocramArbiter ──→ OCRAM
    //                       └──→ spiArbiter   ──→ SpiCache ──→ SpiXip
    //
    //   dBus ──→ dDecoder ──┬──→ ocramArbiter ─┘ (shared with iBus)
    //                       ├──→ spiArbiter   ─┘ (shared with iBus)
    //                       └──→ periphBus ──→ {PLIC, MTimer, ResetCtrl, …}
    //
    // iBus has no access to the peripheral bus (instruction fetches only).
    // connectPeripherals() wires the peripheral decoder to all slave devices.
    // -----------------------------------------------------------------------
    val system = new ClockingArea(clockCtrl.getClockDomainByName("system")) {

      val memParam = parameter.core.iBusTlParam // TL-UL, sourceWidth=1
      val periphParam = TileLinkParameter.simple(32, 32, 4, 1)

      // -----------------------------------------------------------------------
      // Address mappings
      // -----------------------------------------------------------------------
      val ocramMapping = SizeMapping(0x80000000L, parameter.onChipRamSize)
      val spiMapping = SizeMapping(0xa0000000L, parameter.spiRomSize)
      val periphMapping = SizeMapping(0xf0000000L, 16 MB)

      // -----------------------------------------------------------------------
      // Memory bus decoders (iBus → 2 slaves, dBus → 3 slaves)
      // -----------------------------------------------------------------------
      val iBusDecoder = TileLinkDecoder(memParam, Seq(ocramMapping, spiMapping))
      val dBusDecoder = TileLinkDecoder(memParam, Seq(ocramMapping, spiMapping, periphMapping))

      iBusDecoder.io.up <> core.cpu.iBus
      dBusDecoder.io.up <> core.cpu.dBus

      // -----------------------------------------------------------------------
      // Arbiters: combine iBus and dBus paths for OCRAM and SpiXip
      // -----------------------------------------------------------------------
      val ocramArbiter = TileLinkArbiter(memParam, 2)
      val spiArbiter = TileLinkArbiter(memParam, 2)

      iBusDecoder.io.downs(0) <> ocramArbiter.io.ups(0)
      dBusDecoder.io.downs(0) <> ocramArbiter.io.ups(1)

      iBusDecoder.io.downs(1) <> spiArbiter.io.ups(0)
      dBusDecoder.io.downs(1) <> spiArbiter.io.ups(1)

      // -----------------------------------------------------------------------
      // OCRAM (TileLinkArbiter output: sourceWidth = memParam.sourceWidth + 1)
      // -----------------------------------------------------------------------
      val onChipRam = new Area {
        val mapping = ocramMapping
        val busParam = ocramArbiter.io.down.p
        val (ctrl, port) = parameter.onChipRamLogic(busParam, mapping.size)
        port <> ocramArbiter.io.down
      }

      // -----------------------------------------------------------------------
      // SPI XIP controller with a 4-word cache
      // -----------------------------------------------------------------------
      val spiXip = new Area {
        val mapping = spiMapping
        val innerParam = spiArbiter.io.down.p // sourceWidth = memParam.sourceWidth + 1
        val outerParam = TileLinkCache.getOuterParameter(innerParam, 4)

        val cache = TileLinkCache.Cache(innerParam, 4)
        cache.io.inner <> spiArbiter.io.down

        val ctrl = TileLinkSpiXipController(parameter.spi, outerParam)
        ctrl.io.bus <> cache.io.outer
        io_plat.spi <> ctrl.io.spi
      }

      // -----------------------------------------------------------------------
      // Peripheral bus (Port 2): dBus → peripheral decoder
      // Published so that connectPeripherals() can wire the peripherals.
      // -----------------------------------------------------------------------
      val periphBusPort = TileLinkBus(periphParam)
      dBusDecoder.io.downs(2) <> periphBusPort

      // -----------------------------------------------------------------------
      // System peripherals
      // -----------------------------------------------------------------------
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

      addPeripheralDevice(spiXip.ctrl.io.cfgSpiBus, 0x24000, 4 kB)
      addPeripheralDevice(spiXip.ctrl.io.cfgXipBus, 0x25000, 4 kB)

      val timerCtrlMapper = TileLinkTimer(parameter.timer)
      addPeripheralDevice(timerCtrlMapper.io.bus, 0x26000, 4 kB)
      addInterrupt(timerCtrlMapper.io.interrupt)

      val watchdogCtrlMapper = TileLinkWatchdog(parameter.watchdog)
      addPeripheralDevice(watchdogCtrlMapper.io.bus, 0x27000, 4 kB)
      addInterrupt(watchdogCtrlMapper.io.interrupt)
      addError(watchdogCtrlMapper.io.error)

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

      val sysconCtrlMapper = TileLinkSyscon(parameter.buildSyscon(getSysconFeatures()))
      addPeripheralDevice(sysconCtrlMapper.io.bus, 0x23000, 4 kB)

      publishPeripheralComponents(periphBusPort, 0xf0000000L, plicCtrl)
    }
  }
}
