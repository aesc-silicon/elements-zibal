package nafarr.peripherals.com.chip2chip

import org.scalatest.funsuite.AnyFunSuite
import scala.util.Random
import scala.collection.mutable
import scala.math._

import spinal.sim._
import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.misc.SizeMapping
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba4.axi.sim._
import nafarr.CheckTester._

class MultiplexerTest extends AnyFunSuite {

  def pushFromFrontend(dut: Multiplexer, inputs: Int, wait: Int, delay: Int = 1) {
    fork {
      dut.io.fromFrontend.valid #= false

      dut.clockDomain.waitSampling(delay)
      sleep(1)

      val chars = 'A' to 'F'
      for (index <- 0 until inputs) {
        dut.io.fromFrontend.payload(index) #= BigInt("1" + (chars(index).toString * 32) , 16)
      }
      dut.io.fromFrontend.valid #= true

      dut.clockDomain.waitSampling(wait - 1)
      sleep(1)
      assert(dut.io.fromFrontend.ready.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromFrontend.ready.toBoolean == true)
      dut.io.fromFrontend.valid #= false
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromFrontend.ready.toBoolean == false)
    }
  }

  def popToLinkLayer(dut: Multiplexer, inputs: Int, outputs: Int, delay: Int = 1) {
    fork {
      for (index <- 0 until outputs) {
        dut.io.toLinkLayer(index).ready #= true
      }

      dut.clockDomain.waitSampling(delay)
      for (outputIndex <- 0 until outputs) {
        assert(dut.io.toLinkLayer(outputIndex).valid.toBoolean == false)
      }
      dut.clockDomain.waitSampling(1)
      sleep(2)
      val chars = 'A' to 'F'
      for (inputIndex <- 0 until ceil(inputs / outputs.toFloat).toInt) {
        for (outputIndex <- 0 until outputs) {
          assert(dut.io.toLinkLayer(outputIndex).valid.toBoolean == true)
          val count = (inputIndex * outputs) + outputIndex
          val char = chars(count).toString * 32
          if (count == inputs) {
            assert(dut.io.toLinkLayer(outputIndex).payload.toBigInt == BigInt(0))
          } else {
            assert(dut.io.toLinkLayer(outputIndex).payload.toBigInt == BigInt(char , 16))
          }
        }
        dut.clockDomain.waitSampling(1)
        sleep(1)
      }
      for (outputIndex <- 0 until outputs) {
        assert(dut.io.toLinkLayer(outputIndex).valid.toBoolean == false)
      }
    }
  }

  def verifyAckHandling(dut: Multiplexer, inputs: Int, outputs: Int, delay: Int = 1) {
    fork {
      for (index <- 1 until outputs) {
        dut.io.toLinkLayer(index).ready #= true
      }

      dut.clockDomain.waitSampling(delay)
      for (outputIndex <- 1 until outputs) {
        assert(dut.io.toLinkLayer(outputIndex).valid.toBoolean == false)
      }
      dut.clockDomain.waitSampling(1)
      sleep(2)
      for (inputIndex <- 0 until ceil(inputs / outputs.toFloat).toInt) {
        for (outputIndex <- 1 until outputs) {
          assert(dut.io.toLinkLayer(outputIndex).valid.toBoolean == true)
        }
        dut.clockDomain.waitSampling(1)
        sleep(1)
      }
      for (outputIndex <- 1 until outputs) {
        assert(dut.io.toLinkLayer(outputIndex).valid.toBoolean == false)
      }
    }
    fork {
      dut.io.toLinkLayer(0).ready #= false

      dut.clockDomain.waitSampling(delay)
      assert(dut.io.toLinkLayer(0).valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(2)
      for (inputIndex <- 0 until ceil(inputs / outputs.toFloat).toInt) {
        assert(dut.io.toLinkLayer(0).valid.toBoolean == true)
        dut.clockDomain.waitSampling(3)
        sleep(1)
        dut.io.toLinkLayer(0).ready #= true
        dut.clockDomain.waitSampling(1)
        sleep(1)
        dut.io.toLinkLayer(0).ready #= false
      }
      assert(dut.io.toLinkLayer(0).valid.toBoolean == false)
    }
  }

  def pushFromLinkLayer(dut: Multiplexer, inputs: Int, outputs: Int, delay: Int = 1) {
    fork {
      for (index <- 0 until outputs) {
        dut.io.fromLinkLayer(index).valid #= false
      }

      dut.clockDomain.waitSampling(delay)
      sleep(1)

      val chars = 'A' to 'F'
      for (inputIndex <- 0 until ceil(inputs / outputs.toFloat).toInt) {
        for (outputIndex <- 0 until outputs) {
          val count = (inputIndex * outputs) + outputIndex
          val char = chars(count).toString * 32
          dut.io.fromLinkLayer(outputIndex).payload #= BigInt(char , 16)
          dut.io.fromLinkLayer(outputIndex).valid #= true
          sleep(1)
          assert(dut.io.fromLinkLayer(outputIndex).ready.toBoolean == true)
        }
        dut.clockDomain.waitSampling(1)
        sleep(1)
      }
      for (outputIndex <- 0 until outputs) {
        dut.io.fromLinkLayer(outputIndex).valid #= false
      }
    }
  }

  def popToFrontend(dut: Multiplexer, inputs: Int, wait: Int, delay: Int = 1) {
    fork {
      dut.io.toFrontend.ready #= false

      dut.clockDomain.waitSampling(delay)
      sleep(1)

      dut.clockDomain.waitSampling(wait - 1)
      sleep(1)
      assert(dut.io.toFrontend.valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.toFrontend.valid.toBoolean == true)
      val chars = 'A' to 'F'
      for (index <- 0 until inputs) {
        dut.io.toFrontend.payload(index) #= BigInt(chars(index).toString * 32 , 16)
      }
      dut.io.toFrontend.ready #= true
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.toFrontend.valid.toBoolean == false)
    }
  }

  test("Inputs: 2, outputs: 1") {
    val compiled = SimConfig.withWave.compile {
      val inputs = 2
      val outputs = 1
      val dut = Multiplexer(inputs, outputs)
      dut
    }
    compiled.doSim("frontend to linklayer") { dut =>
      dut.clockDomain.forkStimulus(10)

      val wait = ceil(dut.inputs / dut.outputs.toFloat).toInt
      pushFromFrontend(dut, dut.inputs, wait, 5)
      popToLinkLayer(dut, dut.inputs, dut.outputs, 4)

      dut.clockDomain.waitSampling(30)
    }
    compiled.doSim("frontend to linklayer - delay ACK") { dut =>
      dut.clockDomain.forkStimulus(10)

      val wait = ceil(dut.inputs / dut.outputs.toFloat).toInt + 6
      pushFromFrontend(dut, dut.inputs, wait, 5)
      verifyAckHandling(dut, dut.inputs, dut.outputs, 4)

      dut.clockDomain.waitSampling(30)
    }
    compiled.doSim("linklayer to frontend") { dut =>
      dut.clockDomain.forkStimulus(10)

      val wait = ceil(dut.inputs / dut.outputs.toFloat).toInt
      pushFromLinkLayer(dut, dut.inputs, dut.outputs, 5)
      popToFrontend(dut, dut.inputs, wait, 5)

      dut.clockDomain.waitSampling(30)
    }
  }
  test("Inputs: 6, outputs: 2") {
    val compiled = SimConfig.withWave.compile {
      val inputs = 6
      val outputs = 2
      val dut = Multiplexer(inputs, outputs)
      dut
    }
    compiled.doSim("frontend to linklayer") { dut =>
      dut.clockDomain.forkStimulus(10)

      val wait = ceil(dut.inputs / dut.outputs.toFloat).toInt
      pushFromFrontend(dut, dut.inputs, wait, 5)
      popToLinkLayer(dut, dut.inputs, dut.outputs, 4)

      dut.clockDomain.waitSampling(30)
    }
    compiled.doSim("frontend to linklayer - delay ACK") { dut =>
      dut.clockDomain.forkStimulus(10)

      val wait = ceil(dut.inputs / dut.outputs.toFloat).toInt + 9
      pushFromFrontend(dut, dut.inputs, wait, 5)
      verifyAckHandling(dut, dut.inputs, dut.outputs, 4)

      dut.clockDomain.waitSampling(30)
    }
    compiled.doSim("linklayer to frontend") { dut =>
      dut.clockDomain.forkStimulus(10)

      val wait = ceil(dut.inputs / dut.outputs.toFloat).toInt
      pushFromLinkLayer(dut, dut.inputs, dut.outputs, 5)
      popToFrontend(dut, dut.inputs, wait, 5)

      dut.clockDomain.waitSampling(30)
    }
  }
  test("Inputs: 5, outputs: 3") {
    val compiled = SimConfig.withWave.compile {
      val inputs = 5
      val outputs = 3
      val dut = Multiplexer(inputs, outputs)
      dut
    }
    compiled.doSim("frontend to linklayer") { dut =>
      dut.clockDomain.forkStimulus(10)

      val wait = ceil(dut.inputs / dut.outputs.toFloat).toInt
      pushFromFrontend(dut, dut.inputs, wait, 5)
      popToLinkLayer(dut, dut.inputs, dut.outputs, 4)

      dut.clockDomain.waitSampling(30)
    }
    compiled.doSim("frontend to linklayer - delay ACK") { dut =>
      dut.clockDomain.forkStimulus(10)

      val wait = ceil(dut.inputs / dut.outputs.toFloat).toInt + 6
      pushFromFrontend(dut, dut.inputs, wait, 5)
      verifyAckHandling(dut, dut.inputs, dut.outputs, 4)

      dut.clockDomain.waitSampling(30)
    }
    compiled.doSim("linklayer to frontend") { dut =>
      dut.clockDomain.forkStimulus(10)

      val wait = ceil(dut.inputs / dut.outputs.toFloat).toInt
      pushFromLinkLayer(dut, dut.inputs, dut.outputs, 5)
      popToFrontend(dut, dut.inputs, wait, 5)

      dut.clockDomain.waitSampling(30)
    }
  }
}
