package nafarr.peripherals.com.chip2chip

import org.scalatest.funsuite.AnyFunSuite

import spinal.sim._
import spinal.core._
import spinal.core.sim._
import nafarr.CheckTester._

class ControllerLayerTest extends AnyFunSuite {

  test("Data width 128") {
    val compiled = SimConfig.withWave.compile {
      val dut = ControllerLayer.ControllerLayer()
      dut
    }
    compiled.doSim("sync") { dut =>
      dut.clockDomain.forkStimulus(period = 10)
      dut.io.fromFrontend.valid #= false
      dut.io.toLinkLayer.ready #= false
      dut.io.fromLinkLayer.valid #= false
      dut.io.toFrontend.ready #= false

      dut.clockDomain.waitSampling(5)

      dut.io.toLinkLayer.ready #= true

      // send scramble
      fork {
        dut.clockDomain.waitSampling(20)
        sleep(1)

        for (index <- 0 until 10) {
          dut.io.fromLinkLayer.payload.data #= BigInt("10101010" * 16, 2)
          dut.io.fromLinkLayer.valid #= true
          assert(dut.io.fromLinkLayer.ready.toBoolean == true)
          dut.clockDomain.waitSampling(1)
          sleep(1)
        }
        dut.io.fromLinkLayer.valid #= false

        dut.clockDomain.waitSampling(5)
        sleep(1)

        dut.io.fromLinkLayer.payload.data #= BigInt("00111000" * 16, 2)
        dut.io.fromLinkLayer.valid #= true
        assert(dut.io.fromLinkLayer.ready.toBoolean == true)
        dut.clockDomain.waitSampling(1)
        dut.io.fromLinkLayer.valid #= false
      }

      // send size
      fork {
        dut.clockDomain.waitSampling(60)
        sleep(1)

        val data = List("01011100", "00001000", "10111100")
        data foreach { str =>
          dut.io.fromLinkLayer.payload.data #= BigInt(str * 16, 2)
          dut.io.fromLinkLayer.valid #= true
          assert(dut.io.fromLinkLayer.ready.toBoolean == true)
          dut.clockDomain.waitSampling(1)
          sleep(1)
        }
        dut.io.fromLinkLayer.valid #= false
      }

      // send message
      fork {
        dut.clockDomain.waitSampling(70)
        sleep(1)

        val data = List("01011010", "10101010", "11000011")
        data foreach { str =>
          dut.io.fromFrontend.payload #= BigInt(str * 16, 2)
          dut.io.fromFrontend.valid #= true
          

//          assert(dut.io.fromFrontend.ready.toBoolean == true)
          dut.clockDomain.waitSampling(1)
          sleep(1)
        }
//        dut.io.fromFrontend.valid #= false
      }

      dut.clockDomain.waitSampling(200)
    }
  }
}
