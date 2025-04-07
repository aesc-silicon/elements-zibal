package zibal.platform

import spinal.core._
import spinal.lib._

import zibal.soc.SocParameter

import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.misc.SizeMapping
import spinal.lib.io.{TriStateArray, TriState}
import spinal.lib.io.ReadableOpenDrain

import nafarr.system.plic.Apb3Plic
import nafarr.peripherals.pinmux.Apb3Pinmux

abstract class PlatformParameter(socParameter: SocParameter) {
  def getKitParameter = socParameter.getKitParameter
  def getBoardParameter = socParameter.getBoardParameter
  def getSocParameter = socParameter
}

abstract class PlatformComponent(parameter: PlatformParameter) extends Component {
  val apbMapping = ArrayBuffer[(Apb3, SizeMapping)]()
  val irqMapping = ArrayBuffer[(Int, Bool)]()
  val pinmuxInputs = Map[String, (Int, TriState[Bool])]()
  val pinmuxMapping = ArrayBuffer[(Int, List[Int])]()

  var apbBridge: Axi4SharedToApb3Bridge = null
  var plicCtrl: Apb3Plic = null

  def publishApbComponents(bridge: Axi4SharedToApb3Bridge, plic: Apb3Plic) {
    apbBridge = bridge
    plicCtrl = plic
  }

  def connectPeripherals() {
    val apbDecoder = Apb3Decoder(
      master = apbBridge.io.apb,
      slaves = apbMapping
    )

    for ((index, interrupt) <- irqMapping) {
      plicCtrl.io.sources(index) := interrupt
    }
  }

  def addApbDevice(port: Apb3, address: BigInt, size: BigInt) {
    apbMapping += port -> (address, size)
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

  def connectPinmuxInputs(pinmux: Apb3Pinmux) {
    for ((key, (index, pin)) <- pinmuxInputs) {
      pinmux.io.inputs(index).write := pin.write
      pinmux.io.inputs(index).writeEnable := pin.writeEnable
      pin.read := pinmux.io.inputs(index).read
    }
  }

  def initOnChipRam(path: String)
}
