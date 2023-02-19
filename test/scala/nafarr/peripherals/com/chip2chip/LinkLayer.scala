package nafarr.peripherals.com.chip2chip

import org.scalatest.funsuite.AnyFunSuite

import spinal.sim._
import spinal.core._
import spinal.core.sim._
import nafarr.CheckTester._

class LinkLayerTest extends AnyFunSuite {

  test("LinkLayer outgoing") {
    val compiled = SimConfig.withWave.compile {
      val dut = LinkLayer.LinkLayer(2, 2)
      dut.outgoing.outputForSim.simPublic()
      dut
    }
    compiled.doSim("Pipeline") { dut =>
      dut.clockDomain.forkStimulus(period = 10)
      dut.io.fromFrontend.payload.data #= BigInt("00000000" * 2, 2)
      dut.io.fromFrontend.payload.kWord #= false
      dut.io.fromFrontend.payload.fec #= false
      dut.io.fromFrontend.valid #= false

      dut.io.toPhy.ready #= false

      dut.clockDomain.waitSampling(5)

      dut.io.fromFrontend.valid #= true
      dut.io.fromFrontend.payload.data #= BigInt("00111111" * 2, 2)
      dut.io.fromFrontend.payload.kWord #= true
      dut.io.fromFrontend.payload.fec #= true
      assert(dut.io.fromFrontend.ready.toBoolean == true)
      dut.clockDomain.waitSampling(1)
      dut.io.fromFrontend.valid #= false
      dut.clockDomain.waitSampling(2)
      sleep(1)
      assert(dut.io.toPhy.valid.toBoolean == false)
      dut.clockDomain.waitSampling(2)
      sleep(1)
      assert(dut.io.toPhy.valid.toBoolean == true)
      assert(dut.outgoing.outputForSim.toBigInt == BigInt("1100101000", 2))
      dut.clockDomain.waitSampling(1)
      sleep(1)
      dut.io.toPhy.ready #= true
      assert(dut.io.toPhy.valid.toBoolean == true)
      dut.clockDomain.waitSampling(2)
      sleep(1)
      assert(dut.io.toPhy.valid.toBoolean == false)
    }

    compiled.doSim("sync") { dut =>
      dut.clockDomain.forkStimulus(period = 10)
      dut.io.fromFrontend.payload.data #= BigInt("00000000" * 2, 2)
      dut.io.fromFrontend.payload.kWord #= true
      dut.io.fromFrontend.payload.fec #= false
      dut.io.fromFrontend.valid #= false

      dut.io.toPhy.ready #= false

      dut.clockDomain.waitSampling(5)

      dut.io.fromFrontend.valid #= true
      dut.io.fromFrontend.payload.data #= BigInt("00111100" * 2, 2)
      assert(dut.io.fromFrontend.ready.toBoolean == true)
      dut.clockDomain.waitSampling(2)
      dut.io.fromFrontend.valid #= false
      dut.clockDomain.waitSampling(3)
      sleep(1)
      dut.io.toPhy.ready #= true
      assert(dut.io.toPhy.valid.toBoolean == true)
      assert(dut.outgoing.outputForSim.toBigInt == BigInt("0011111001", 2))
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.toPhy.valid.toBoolean == true)
      assert(dut.outgoing.outputForSim.toBigInt == BigInt("1100000110", 2))
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.toPhy.valid.toBoolean == false)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("scramble") { dut =>
      dut.clockDomain.forkStimulus(period = 10)
      dut.io.fromFrontend.payload.data #= BigInt("00000000" * 2, 2)
      dut.io.fromFrontend.payload.kWord #= true
      dut.io.fromFrontend.payload.fec #= true
      dut.io.fromFrontend.valid #= false

      dut.io.toPhy.ready #= false

      dut.clockDomain.waitSampling(5)

      fork {
        dut.io.fromFrontend.valid #= true
        dut.io.fromFrontend.payload.data #= BigInt("00000001" * 2, 2)
        assert(dut.io.fromFrontend.ready.toBoolean == true)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        dut.io.fromFrontend.payload.data #= BigInt("00000010" * 2, 2)
        assert(dut.io.fromFrontend.ready.toBoolean == true)
        dut.clockDomain.waitSampling(2)
        sleep(1)
        dut.io.fromFrontend.payload.data #= BigInt("00000011" * 2, 2)
        assert(dut.io.fromFrontend.ready.toBoolean == true)
        dut.clockDomain.waitSampling(2)
        sleep(1)
        dut.io.fromFrontend.payload.data #= BigInt("00000100" * 2, 2)
        assert(dut.io.fromFrontend.ready.toBoolean == true)
        dut.clockDomain.waitSampling(2)
        sleep(1)
        dut.io.fromFrontend.payload.data #= BigInt("00000101" * 2, 2)
        assert(dut.io.fromFrontend.ready.toBoolean == true)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        dut.io.fromFrontend.valid #= false
      }

      fork {
        dut.io.toPhy.ready #= true
        dut.clockDomain.waitSampling(5)
        sleep(1)

        val data = List("1010010100", "1001110100", "0110011001", "1001111011", "0001011011", "1001110110", "1100010100", "1001111001", "0001010111", "1001111011")
        data foreach { str =>
          assert(dut.io.toPhy.valid.toBoolean == true)
          assert(dut.outgoing.outputForSim.toBigInt == BigInt(str, 2))
          dut.clockDomain.waitSampling(1)
          sleep(1)
        }
      }

      dut.clockDomain.waitSampling(30)
    }
  }

  test("LinkLayer incoming") {
    val compiled = SimConfig.withWave.compile {
      val dut = LinkLayer.LinkLayer(2, 2)
      dut.outgoing.outputForSim.simPublic()
      dut
    }
    compiled.doSim("Pipeline") { dut =>
      dut.clockDomain.forkStimulus(period = 10)

      for (index <- 0 until dut.rxIoPins) {
        dut.io.fromPhy.payload.data(index) #= BigInt("0000000000", 2)
      }
      dut.io.fromPhy.payload.aux #= false
      dut.io.fromPhy.payload.fec #= false
      dut.io.fromPhy.valid #= false

      dut.io.toFrontend.ready #= false

      dut.clockDomain.waitSampling(5)

      dut.io.fromPhy.valid #= true
      for (index <- 0 until dut.rxIoPins) {
        dut.io.fromPhy.payload.data(index) #= BigInt("1100101000", 2)
      }
      dut.io.fromPhy.payload.aux #= true
      dut.io.fromPhy.payload.fec #= true
      assert(dut.io.fromPhy.ready.toBoolean == true)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      for (index <- 0 until dut.rxIoPins) {
        dut.io.fromPhy.payload.data(index) #= BigInt("0110010100", 2)
      }

      dut.clockDomain.waitSampling(1)
      dut.io.fromPhy.valid #= false
      dut.clockDomain.waitSampling(3)
      sleep(1)
      assert(dut.io.toFrontend.valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.toFrontend.valid.toBoolean == true)
      assert(dut.io.toFrontend.payload.data.toBigInt == BigInt("0011111000111111", 2))
      dut.io.toFrontend.ready #= true
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.toFrontend.valid.toBoolean == false)

      dut.clockDomain.waitSampling(10)
    }
  }
}
