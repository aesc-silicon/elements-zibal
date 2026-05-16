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
import spinal.lib.io.{TriStateArray, TriState}
import spinal.lib.io.ReadableOpenDrain

import nafarr.system.plic.TileLinkPlic
import nafarr.peripherals.pinmux.{TileLinkPinmux, Pinmux}

abstract class PlatformParameter(socParameter: SocParameter) {
  def getKitParameter = socParameter.getKitParameter
  def getBoardParameter = socParameter.getBoardParameter
  def getSocParameter = socParameter
}

abstract class PlatformComponent(parameter: PlatformParameter) extends Component {
  val tileLinkMapping = ArrayBuffer[(TileLinkBus, SizeMapping)]()
  val irqMapping      = ArrayBuffer[(Int, Bool)]()
  val pinmuxInputs    = Map[String, (Int, TriState[Bool])]()
  val pinmuxMapping   = ArrayBuffer[(Int, List[Int])]()

  var periphBus:  TileLinkBus  = null
  var periphBase: BigInt       = 0
  var plicCtrl:   TileLinkPlic = null

  def publishPeripheralComponents(
      bus: TileLinkBus,
      base: BigInt,
      plic: TileLinkPlic
  ) {
    periphBus  = bus
    periphBase = base
    plicCtrl   = plic
  }

  def connectPeripherals() {
    // Inline 1-to-N TileLink address decoder for the peripheral bus.
    // Addresses in tileLinkMapping are local offsets from periphBase.
    val n = tileLinkMapping.size

    for ((bus, localMapping) <- tileLinkMapping) {
      val absMapping = SizeMapping(periphBase + localMapping.base, localMapping.size)
      val hit = absMapping.hit(periphBus.a.address)
      bus.a.valid   := periphBus.a.valid && hit
      bus.a.opcode  := periphBus.a.opcode
      bus.a.param   := periphBus.a.param
      bus.a.size    := periphBus.a.size.resize(bus.p.sizeWidth)
      bus.a.source  := periphBus.a.source.resize(bus.p.sourceWidth)
      bus.a.address := periphBus.a.address.resize(bus.p.addressWidth)
      bus.a.mask    := periphBus.a.mask
      bus.a.data    := periphBus.a.data
      bus.a.corrupt := periphBus.a.corrupt
    }
    periphBus.a.ready := Vec(tileLinkMapping.map { case (bus, localMapping) =>
      val absMapping = SizeMapping(periphBase + localMapping.base, localMapping.size)
      bus.a.ready && absMapping.hit(periphBus.a.address)
    }).orR

    // D channel: priority merge back to peripheral bus master.
    val dValids = Vec(tileLinkMapping.map(_._1.d.valid))
    val dChosen = OHMasking.first(dValids.asBits)
    val buses   = tileLinkMapping.map(_._1).toSeq

    val sw = periphBus.p.sizeWidth
    val srcw = periphBus.p.sourceWidth

    periphBus.d.valid   := dValids.orR
    periphBus.d.opcode  := MuxOH(dChosen, buses.map(_.d.opcode))
    periphBus.d.param   := MuxOH(dChosen, buses.map(_.d.param))
    periphBus.d.size    := MuxOH(dChosen, buses.map(_.d.size.resize(sw)))
    periphBus.d.source  := MuxOH(dChosen, buses.map(_.d.source.resize(srcw)))
    periphBus.d.sink    := 0
    periphBus.d.denied  := MuxOH(dChosen, buses.map(_.d.denied))
    periphBus.d.data    := MuxOH(dChosen, buses.map(_.d.data))
    periphBus.d.corrupt := MuxOH(dChosen, buses.map(_.d.corrupt))

    for ((bus, i) <- buses.zipWithIndex) {
      bus.d.ready := periphBus.d.ready && dChosen(i)
    }

    // Connect IRQ sources to the PLIC.
    for ((index, interrupt) <- irqMapping) {
      plicCtrl.io.sources(index) := interrupt
    }
  }

  def addPeripheralDevice(port: TileLinkBus, address: BigInt, size: BigInt) {
    tileLinkMapping += port -> SizeMapping(address, size)
  }

  var nextInterruptNumber = 0
  def addInterrupt(pin: Bool) {
    irqMapping += nextInterruptNumber -> pin
    nextInterruptNumber += 1
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
