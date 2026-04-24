// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal.platform

import spinal.core._
import spinal.lib._

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
import nafarr.cores.cpu.vexiiriscv.{VexiiRiscvCoreParameter, VexiiRiscvBmb}

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
      onChipRamLogic: (BmbParameter, BigInt) => (Component, Bmb) =
        (p: BmbParameter, onChipRamSize: BigInt) => {
          val ram = BmbOnChipRam(
            p = p,
            size = onChipRamSize
          )
          (ram, ram.io.bus)
        }
  ) extends PlatformParameter(socParameter) {
    val core = VexiiRiscvCoreParameter.realtime(0xa0000000L)
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

    val resetCtrl = parameter.resetCtrl(parameter.resets)
    resetCtrl.io.mainReset := io_plat.reset
    resetCtrl.io.mainClock := io_plat.clock
    resetCtrl.io.trigger := 0

    val clockCtrl = parameter.clockCtrl(parameter.clocks, resetCtrl)
    ClockControllerCtrl.connect(parameter.clocks, clockCtrl, resetCtrl)
    clockCtrl.io.mainReset := io_plat.reset
    clockCtrl.io.mainClock := io_plat.clock

    val core = new ClockingArea(clockCtrl.getClockDomainByName("system")) {
      val cpu = new VexiiRiscvBmb(
        VexiiRiscvBmb.Parameter(
          parameter.core.plugins,
          parameter.core.iBusBmbParam,
          parameter.core.dBusBmbParam
        ),
        clockCtrl.getClockDomainByName("debug")
      )

      clockCtrl.getClockDomainByName("debug") {
        resetCtrl.triggerByNameWithCond("system", RegNext(cpu.ndmreset))
      }

      io_plat.jtag <> cpu.jtag
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
        p = parameter.core.iBusBmbParam,
        mappings = Seq(onChipRam.mapping, spiXipController.mapping),
        capabilities = Seq(onChipRam.bmbParameter, spiXipController.bmbParameter)
      )
      iBusDecoder.io.input << core.cpu.iBus.pipelined(
        cmdValid = true,
        cmdReady = true,
        rspValid = true,
        rspReady = true
      )

      val dBusDecoder = BmbDecoder(
        p = parameter.core.dBusBmbParam,
        mappings = Seq(onChipRam.mapping, spiXipController.mapping, wishboneBridge.mapping),
        capabilities =
          Seq(onChipRam.bmbParameter, spiXipController.bmbParameter, wishboneBridge.bmbParameter)
      )
      dBusDecoder.io.input << core.cpu.dBus.pipelined(
        cmdValid = true,
        cmdReady = true,
        rspValid = true,
        rspReady = true
      )

      val onChipRamArbiter = BmbArbiter(
        inputsParameter = Seq(parameter.core.iBusBmbParam, parameter.core.dBusBmbParam),
        outputParameter = onChipRam.bmbParameter,
        lowerFirstPriority = true
      )
      onChipRam.port << onChipRamArbiter.io.output
      onChipRamArbiter.io.inputs(0) << iBusDecoder.io.outputs(0)
      onChipRamArbiter.io.inputs(1) << dBusDecoder.io.outputs(0)

      val spiXipControllerArbiter = BmbArbiter(
        inputsParameter = Seq(parameter.core.iBusBmbParam, parameter.core.dBusBmbParam),
        outputParameter = spiXipController.bmbParameter,
        lowerFirstPriority = true
      )
      spiXipController.cache.io.input << spiXipControllerArbiter.io.output
      spiXipControllerArbiter.io.inputs(0) << iBusDecoder.io.outputs(1)
      spiXipControllerArbiter.io.inputs(1) << dBusDecoder.io.outputs(1)

      wishboneBridge.bridge.io.input << dBusDecoder.io.outputs(2)

      /* Peripheral IP-Cores */
      val plicCtrl = WishbonePlic(parameter.plic, wishboneConfig)
      core.cpu.globalInterrupt := plicCtrl.io.interrupt
      addPeripheralDevice(plicCtrl.io.bus, 0x800000, 4 MB)

      val mtimerCtrl = WishboneMachineTimer(parameter.mtimer, wishboneConfig)
      core.cpu.mtimerInterrupt := mtimerCtrl.io.interrupt
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
