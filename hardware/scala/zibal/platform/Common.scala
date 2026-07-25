// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal.platform

import spinal.core._
import spinal.lib._

import zibal.soc.SocParameter

import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

import spinal.lib.bus.misc.{SizeMapping, AddressMapping}
import spinal.lib.bus.tilelink.{Bus => TileLinkBus, BusParameter => TileLinkParameter, Opcode}
import spinal.lib.bus.wishbone._
import spinal.lib.bus.bmb._
import spinal.lib.io.{TriStateArray, TriState}
import spinal.lib.io.ReadableOpenDrain

import nafarr.system.plic.{TileLinkPlic, WishbonePlic}
import nafarr.system.esm.Esm
import nafarr.peripherals.pinmux.{TileLinkPinmux, Pinmux}
import nafarr.peripherals.PeripheralsComponent
import nafarr.Feature
import nafarr.bus.wishbone._

abstract class PlatformParameter(socParameter: SocParameter) {
  def getKitParameter = socParameter.getKitParameter
  def getBoardParameter = socParameter.getBoardParameter
  def getSocParameter = socParameter
}

abstract class PlatformComponent(parameter: PlatformParameter) extends Component {
  val tileLinkMapping = ArrayBuffer[(TileLinkBus, SizeMapping)]()
  val wishboneMapping = ArrayBuffer[(Wishbone, SizeMapping)]()
  val irqMapping = ArrayBuffer[Bool]()
  val errorMapping = ArrayBuffer[Bool]()
  val pinmuxInputs = Map[String, (Int, TriState[Bool])]()
  val pinmuxMapping = ArrayBuffer[(Int, List[Int])]()

  class PeripheralDomain(val syncCd: ClockDomain) {
    var bus: TileLinkBus = null
    var base: BigInt = 0
    val devices = ArrayBuffer[(TileLinkBus, SizeMapping)]()
    val irqs = ArrayBuffer[Bool]()
    val errors = ArrayBuffer[Bool]()
  }
  val peripheralDomains = scala.collection.mutable.LinkedHashMap[String, PeripheralDomain]()

  var periphBus: TileLinkBus = null
  var periphBase: BigInt = 0
  var plicCtrl: TileLinkPlic = null
  var esmCtrl: Esm.Core[_] = null
  var wishboneBridge: BmbToWishbone = null
  var wishbonePlic: WishbonePlic = null

  def baremetalGroups: Seq[(BigInt, Seq[(TileLinkBus, SizeMapping)])] =
    (periphBase, tileLinkMapping.toSeq) +:
      peripheralDomains.values.map(d => (d.base, d.devices.toSeq)).toSeq

  // Bus-agnostic device list for header generation: (component, base, size).
  def baremetalDevices: Seq[(Component, BigInt, BigInt)] =
    baremetalGroups.flatMap { case (base, mapping) =>
      mapping.map { case (bus, size) => (bus.parent.component, base + size.base, size.size) }
    }
  def baremetalIrqs: Seq[Bool] =
    (irqMapping ++ peripheralDomains.values.flatMap(_.irqs)).toSeq
  def baremetalErrors: Seq[Bool] =
    (errorMapping ++ peripheralDomains.values.flatMap(_.errors)).toSeq

  def publishPeripheralComponents(
      bus: TileLinkBus,
      base: BigInt,
      plic: TileLinkPlic
  ) {
    periphBus = bus
    periphBase = base
    plicCtrl = plic
  }

  def publishPeripheralComponents(bridge: BmbToWishbone, plic: WishbonePlic) {
    wishboneBridge = bridge
    wishbonePlic = plic
  }

  def publishEsm(esm: Esm.Core[_]) {
    esmCtrl = esm
  }

  def connectPeripherals() {
    if (wishboneBridge != null) {
      WishboneDecoder2(
        master = wishboneBridge.io.output,
        slaves = wishboneMapping,
        2
      )
      for ((interrupt, index) <- irqMapping.zipWithIndex) {
        wishbonePlic.io.sources(index) := interrupt
      }
      return
    }

    decodePeripheralBus(periphBus, periphBase, tileLinkMapping)
    for ((_, domain) <- peripheralDomains) {
      decodePeripheralBus(domain.bus, domain.base, domain.devices)
    }

    val crossedIrqs =
      peripheralDomains.values.flatMap(d => d.irqs.map(pin => d.syncCd(BufferCC(pin, False))))
    for ((interrupt, index) <- (irqMapping ++ crossedIrqs).zipWithIndex) {
      plicCtrl.io.sources(index) := interrupt
    }

    if (esmCtrl != null) {
      val crossedErrors =
        peripheralDomains.values.flatMap(d => d.errors.map(pin => d.syncCd(BufferCC(pin, False))))
      val allErrors = errorMapping ++ crossedErrors
      val n = esmCtrl.io.inputs.getBitsWidth
      val inputs = (0 until n).map(i => if (i < allErrors.size) allErrors(i) else False)
      esmCtrl.io.inputs := Cat(inputs.reverse)
    }
  }

  def getSysconFeatures(): List[Feature.E] = {
    tileLinkMapping
      .flatMap { case (bus, _) =>
        bus.parent.component match {
          case p: PeripheralsComponent => p.sysconFeatures.getOrElse(Nil)
          case _ => Nil
        }
      }
      .distinct
      .toList
  }

  def decodePeripheralBus(
      master: TileLinkBus,
      base: BigInt,
      mapping: ArrayBuffer[(TileLinkBus, SizeMapping)]
  ) {
    for ((bus, localMapping) <- mapping) {
      val absMapping = SizeMapping(base + localMapping.base, localMapping.size)
      val hit = absMapping.hit(master.a.address)
      bus.a.valid := master.a.valid && hit
      bus.a.opcode := master.a.opcode
      bus.a.param := master.a.param
      bus.a.size := master.a.size.resize(bus.p.sizeWidth)
      bus.a.source := master.a.source.resize(bus.p.sourceWidth)
      bus.a.address := master.a.address.resize(bus.p.addressWidth)
      bus.a.mask := master.a.mask
      bus.a.data := master.a.data
      bus.a.corrupt := master.a.corrupt
    }
    master.a.ready := Vec(mapping.map { case (bus, localMapping) =>
      val absMapping = SizeMapping(base + localMapping.base, localMapping.size)
      bus.a.ready && absMapping.hit(master.a.address)
    }).orR

    val dValids = Vec(mapping.map(_._1.d.valid))
    val dChosen = OHMasking.first(dValids.asBits)
    val buses = mapping.map(_._1).toSeq
    val sw = master.p.sizeWidth
    val srcw = master.p.sourceWidth

    master.d.valid := dValids.orR
    master.d.opcode := MuxOH(dChosen, buses.map(_.d.opcode))
    master.d.param := MuxOH(dChosen, buses.map(_.d.param))
    master.d.size := MuxOH(dChosen, buses.map(_.d.size.resize(sw)))
    master.d.source := MuxOH(dChosen, buses.map(_.d.source.resize(srcw)))
    master.d.sink := 0
    master.d.denied := MuxOH(dChosen, buses.map(_.d.denied))
    master.d.data := MuxOH(dChosen, buses.map(_.d.data))
    master.d.corrupt := MuxOH(dChosen, buses.map(_.d.corrupt))

    for ((bus, i) <- buses.zipWithIndex) {
      bus.d.ready := master.d.ready && dChosen(i)
    }
  }

  def publishPeripheralDomain(name: String, bus: TileLinkBus, base: BigInt) {
    val domain = new PeripheralDomain(ClockDomain.current)
    domain.bus = bus
    domain.base = base
    peripheralDomains(name) = domain
  }

  def addPeripheralDevice(
      port: TileLinkBus,
      address: BigInt,
      size: BigInt,
      domain: String = "system"
  ) {
    val mapping = port -> SizeMapping(address, size)
    if (domain == "system") tileLinkMapping += mapping
    else peripheralDomains(domain).devices += mapping
  }

  def addPeripheralDevice(port: Wishbone, address: BigInt, size: BigInt) {
    wishboneMapping += port -> SizeMapping(address, size)
  }

  def addInterrupt(pin: Bool, domain: String = "system") {
    if (domain == "system") irqMapping += pin
    else peripheralDomains(domain).irqs += pin
  }

  def addError(pin: Bool, domain: String = "system") {
    if (domain == "system") errorMapping += pin
    else peripheralDomains(domain).errors += pin
  }

  def addPinmuxInput(pin: Bool, name: String, output: Boolean = true) {
    assert(
      !pinmuxInputs.contains(name),
      s"Unable to assign input with name ${name}. Another input is already using this name."
    )
    val tmp = TriState(Bool)
    if (output) {
      tmp.writeEnable := True
      tmp.write := pin
    } else {
      tmp.writeEnable := False
      tmp.write := False
      pin := tmp.read
    }
    pinmuxInputs += name -> (pinmuxInputs.size, tmp)
  }

  def addPinmuxInput(pin: TriState[Bool], name: String) {
    assert(
      !pinmuxInputs.contains(name),
      s"Unable to assign input with name ${name}. Another input is already using this name."
    )
    val tmp = TriState(Bool)
    pin.read := tmp.read
    tmp.writeEnable := pin.writeEnable
    tmp.write := pin.write
    pinmuxInputs += name -> (pinmuxInputs.size, tmp)
  }

  def addPinmuxInput(pin: ReadableOpenDrain[Bool], name: String) {
    assert(
      !pinmuxInputs.contains(name),
      s"Unable to assign input with name ${name}. Another input is already using this name."
    )
    val tmp = TriState(Bool)
    pin.read := tmp.read
    tmp.writeEnable := False
    tmp.write := pin.write
    pinmuxInputs += name -> (pinmuxInputs.size, tmp)
  }

  def addPinmuxOption(pin: Int, inputs: List[String]) {
    val inputPins = for (input <- inputs) yield (pinmuxInputs(input)._1)
    pinmuxMapping.append((pin, inputPins))
  }

  def getPinmuxMapping() = pinmuxMapping

  def connectPinmuxInputs(pinmux: Pinmux.Core[_]) {
    for ((key, (index, pin)) <- pinmuxInputs) {
      pinmux.io.inputs(index).write := pin.write
      pinmux.io.inputs(index).writeEnable := pin.writeEnable
      pin.read := pinmux.io.inputs(index).read
    }
  }

  def initOnChipRam(path: String)
}
