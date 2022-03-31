package zibal.platform

import spinal.core._
import spinal.lib._

import zibal.soc.SocParameter

import scala.collection.mutable.ArrayBuffer

import spinal.lib.bus.amba3.apb._
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.misc.SizeMapping

import nafarr.system.plic.Apb3Plic

abstract class PlatformParameter(socParameter: SocParameter) {
  def getKitParameter = socParameter.getKitParameter
  def getBoardParameter = socParameter.getBoardParameter
  def getSocParameter = socParameter
}

abstract class PlatformComponent(parameter: PlatformParameter) extends Component {
  val apbMapping = ArrayBuffer[(Apb3, SizeMapping)]()
  val irqMapping = ArrayBuffer[(Int, Bool)]()

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

  def initOnChipRam(path: String)
}
