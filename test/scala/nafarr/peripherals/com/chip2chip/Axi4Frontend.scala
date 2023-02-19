package nafarr.peripherals.com.chip2chip

import org.scalatest.funsuite.AnyFunSuite
import scala.util.Random
import scala.collection.mutable

import spinal.sim._
import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.misc.SizeMapping
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba4.axi.sim._
import nafarr.CheckTester._

class Axi4FrontendTest extends AnyFunSuite {

  def inputLogic(dut: Frontend.Axi4Frontend, dataBlocks: Int) {
    dut.io.toLinkLayer.ready #= false
    dut.io.axiIn.aw.valid #= false
    dut.io.axiIn.ar.valid #= false
    dut.io.axiIn.w.valid #= false
    dut.io.axiOut.r.valid #= false
    dut.io.axiOut.b.valid #= false

    dut.clockDomain.waitSampling(5)
    dut.io.axiIn.w.valid #= true
    dut.io.toLinkLayer.ready #= true

    var data = ""
    val chars = 'A' to 'F'
    for (index <- 0 until dataBlocks) {
      data = (chars(index).toString * 32) + data
    }

    dut.io.axiIn.w.data #= BigInt(data, 16)
    dut.io.axiIn.w.strb #= BigInt(0)
    dut.io.axiIn.w.last #= false
    assert(dut.io.toLinkLayer.valid.toBoolean == false)

    dut.clockDomain.waitSampling(1)
    sleep(1)

    assert(dut.io.axiIn.w.ready.toBoolean == true)
    assert(dut.io.toLinkLayer.valid.toBoolean == true)
    assert(dut.input.decider.lockChannel.toBigInt == BigInt("011", 2))
    assert(dut.io.toLinkLayer.payload(0).toBigInt == BigInt("16" + "0" * 31, 16))
    for (index <- 0 until dataBlocks) {
      val character = "1" + (chars(index).toString * 32)
      val data = BigInt(character, 16)
      assert(dut.io.toLinkLayer.payload(index + 1).toBigInt == data)
    }

    dut.clockDomain.waitSampling(1)
    sleep(1)

    dut.io.toLinkLayer.ready #= false
    dut.io.axiIn.w.valid #= false
  }

  def outputLogic(dut: Frontend.Axi4Frontend, dataBlocks: Int) {
    dut.io.fromLinkLayer.valid #= false
    dut.io.axiOut.aw.ready #= false
    dut.io.axiOut.ar.ready #= false
    dut.io.axiOut.w.ready #= false
    dut.io.axiIn.r.ready #= false
    dut.io.axiIn.b.ready #= false

    dut.clockDomain.waitSampling(5)
    dut.io.axiIn.w.ready #= true
    dut.io.fromLinkLayer.valid #= true

    dut.io.fromLinkLayer.payload(0) #= BigInt("011" + ("0" * 109) + "10" + ("1" * 14) , 2)
    val chars = 'A' to 'F'
    for (index <- 0 until dataBlocks) {
      dut.io.fromLinkLayer.payload(index + 1) #= BigInt((chars(index).toString * 32) , 16)
    }

    sleep(1)
    assert(dut.io.axiOut.w.valid.toBoolean == true)

    var data = ""
    for (index <- 0 until dataBlocks) {
      data = (chars(index).toString * 32) + data
    }
    assert(dut.io.axiOut.w.payload.data.toBigInt == BigInt(data, 16))

    dut.io.axiOut.w.ready #= true
  }

  test("compile datawidth 128") {
    val compiled = SimConfig.withWave.compile {
      val config = Axi4Config(64, 128, 14)
      val dut = Frontend.Axi4Frontend(config)
      dut.input.decider.lockChannel.simPublic()
      dut
    }
    compiled.doSim("values") { dut =>
      assert(dut.dataWidth == 128)
      assert(dut.dataBlocks == 2)
    }
    compiled.doSim("input logic") { dut =>
      dut.clockDomain.forkStimulus(period = 10)

      inputLogic(dut, 1)

      dut.clockDomain.waitSampling(10)
    }
    compiled.doSim("output logic") { dut =>
      dut.clockDomain.forkStimulus(10)

      outputLogic(dut, 1)

      dut.clockDomain.waitSampling(10)
    }
  }
  test("compile datawidth 256") {
    val compiled = SimConfig.withWave.compile {
      val config = Axi4Config(64, 256, 14)
      val dut = Frontend.Axi4Frontend(config)
      dut.input.decider.lockChannel.simPublic()
      dut
    }
    compiled.doSim("values") { dut =>
      assert(dut.dataWidth == 256)
      assert(dut.dataBlocks == 3)
    }
    compiled.doSim("write logic") { dut =>
      dut.clockDomain.forkStimulus(period = 10)

      inputLogic(dut, 2)

      dut.clockDomain.waitSampling(10)
    }
    compiled.doSim("output logic") { dut =>
      dut.clockDomain.forkStimulus(10)

      outputLogic(dut, 2)

      dut.clockDomain.waitSampling(10)
    }

  }
  test("compile datawidth 512") {
    val compiled = SimConfig.withWave.compile {
      val config = Axi4Config(64, 512, 14)
      val dut = Frontend.Axi4Frontend(config)
      dut.input.decider.lockChannel.simPublic()
      dut
    }
    compiled.doSim("values") { dut =>
      assert(dut.dataWidth == 512)
      assert(dut.dataBlocks == 5)
    }
    compiled.doSim("write logic") { dut => 
      dut.clockDomain.forkStimulus(period = 10)

      inputLogic(dut, 4)

      dut.clockDomain.waitSampling(10)
    }
    compiled.doSim("output logic") { dut =>
      dut.clockDomain.forkStimulus(10)

      outputLogic(dut, 4)

      dut.clockDomain.waitSampling(10)
    }

  }

  test("input") {
    val compiled = SimConfig.withWave.compile {
      val config = Axi4Config(64, 128, 14)
      val dut = Frontend.Axi4Frontend(config)
      dut.input.decider.lockChannel.simPublic()
      dut
    }
    compiled.doSim("decider logic") { dut =>
      dut.clockDomain.forkStimulus(period = 10)

      dut.io.toLinkLayer.ready #= false
      dut.io.axiIn.aw.valid #= false
      dut.io.axiIn.ar.valid #= false
      dut.io.axiIn.w.valid #= false
      dut.io.axiOut.r.valid #= false
      dut.io.axiOut.b.valid #= false

      dut.clockDomain.waitSampling(5)
      dut.io.toLinkLayer.ready #= true
      dut.io.axiIn.ar.valid #= true
      dut.io.axiIn.aw.valid #= true


      assert(dut.io.toLinkLayer.valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.toLinkLayer.valid.toBoolean == true)
      assert(dut.io.axiIn.ar.ready.toBoolean == false)
      assert(dut.io.axiIn.aw.ready.toBoolean == true)
      assert(dut.input.decider.lockChannel.toBigInt == BigInt("010", 2))
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.axiIn.ar.ready.toBoolean == false)


      assert(dut.io.toLinkLayer.valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.toLinkLayer.valid.toBoolean == true)
      assert(dut.io.axiIn.ar.ready.toBoolean == true)
      assert(dut.io.axiIn.aw.ready.toBoolean == false)
      assert(dut.input.decider.lockChannel.toBigInt == BigInt("001", 2))
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.axiIn.ar.ready.toBoolean == false)
      dut.io.axiIn.ar.valid #= false

      dut.clockDomain.waitSampling(10)
    }
  }
  test("output") {
    val compiled = SimConfig.withWave.compile {
      val config = Axi4Config(64, 128, 14)
      val dut = Frontend.Axi4Frontend(config)
      dut.input.decider.lockChannel.simPublic()
      dut
    }
    compiled.doSim("decider logic AXI4B") { dut =>
      dut.clockDomain.forkStimulus(10)

      dut.io.fromLinkLayer.valid #= false
      dut.io.axiOut.aw.ready #= false
      dut.io.axiOut.ar.ready #= false
      dut.io.axiOut.w.ready #= false
      dut.io.axiIn.r.ready #= false
      dut.io.axiIn.b.ready #= false

      dut.clockDomain.waitSampling(5)
      dut.io.fromLinkLayer.payload(0) #= BigInt("101" + ("0" * 109) + "10" + ("1" * 14) , 2)
      dut.io.fromLinkLayer.valid #= true
      sleep(1)
      assert(dut.io.axiIn.b.valid.toBoolean == true)
      dut.io.axiIn.b.ready #= true
      dut.clockDomain.waitSampling(1)
      dut.io.fromLinkLayer.valid #= false
      sleep(1)
      assert(dut.io.axiIn.b.valid.toBoolean == false)

      dut.clockDomain.waitSampling(10)
    }
  }
}
