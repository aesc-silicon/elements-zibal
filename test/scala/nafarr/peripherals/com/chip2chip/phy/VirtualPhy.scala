package nafarr.peripherals.com.chip2chip.phy

import org.scalatest.funsuite.AnyFunSuite

import spinal.sim._
import spinal.core._
import spinal.core.sim._
import nafarr.CheckTester._


class VirtualPhyTest extends AnyFunSuite {

  test("tx") {
    val compiled = SimConfig.withWave.compile {
      VirtualPhy.Tx()
    }
    compiled.doSim("not enabled") { dut =>
      dut.clockDomain.forkStimulus(10)

      dut.io.enable #= false
      dut.io.fromLinkLayer.valid #= false

      for (index <- 0 until 8) {
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(dut.io.phy.data.toBigInt == BigInt(0))
        assert(dut.io.phy.aux.toBoolean == false)
        assert(dut.io.phy.fec.toBoolean == false)
      }

      val transmission = "0101011011"
      for (index <- 0 until dut.ioPins) {
        dut.io.fromLinkLayer.payload.data(index) #= BigInt(transmission, 2)
      }
      dut.io.fromLinkLayer.payload.aux #= true
      dut.io.fromLinkLayer.payload.fec #= true
      dut.io.fromLinkLayer.valid #= true

      for (index <- 0 until 8) {
        assert(dut.io.phy.data.toBigInt == BigInt(0))
        assert(dut.io.phy.aux.toBoolean == false)
        assert(dut.io.phy.fec.toBoolean == false)
        dut.clockDomain.waitSampling(1)
        sleep(1)
      }

      dut.io.enable #= true
      dut.clockDomain.waitSampling(5)
      sleep(1)
      assert(dut.io.phy.aux.toBoolean == true)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("0101011011") { dut =>
      dut.clockDomain.forkStimulus(10)

      dut.io.enable #= false
      dut.io.fromLinkLayer.valid #= false

      dut.clockDomain.waitSampling(9)
      sleep(1)

      val transmission = "0101011011"
      for (index <- 0 until dut.ioPins) {
        dut.io.fromLinkLayer.payload.data(index) #= BigInt(transmission, 2)
      }
      dut.io.fromLinkLayer.payload.aux #= true
      dut.io.fromLinkLayer.payload.fec #= true
      dut.io.enable #= true
      dut.io.fromLinkLayer.valid #= true

      dut.clockDomain.waitSampling(1)
      sleep(1)

      for (index <- 0 until 10) {
        dut.clockDomain.waitSampling(1)
        sleep(1)

        val bit = transmission.reverse(index).toString()
        println(s"Expected bit: ${bit}")
        assert(dut.io.phy.data.toBigInt == BigInt(bit * 16, 2))
        if (index == 8) {
          assert(dut.io.fromLinkLayer.ready.toBoolean == true)
        }
      }

      dut.io.fromLinkLayer.valid #= false
      for (index <- 0 until 10) {
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(dut.io.phy.data.toBigInt == BigInt("0" * 16, 2))
        if (index == 9) {
          assert(dut.io.fromLinkLayer.ready.toBoolean == false)
        }
      }

      dut.io.fromLinkLayer.valid #= true
      for (index <- 0 until 10) {
        dut.clockDomain.waitSampling(1)
        sleep(1)
        val bit = transmission.reverse(index).toString()
        println(s"Expected bit: ${bit}")
        assert(dut.io.phy.data.toBigInt == BigInt(bit * 16, 2))
        if (index == 8) {
          assert(dut.io.fromLinkLayer.ready.toBoolean == true)
        }
      }

      dut.clockDomain.waitSampling(10)
    }
  }

  def syncPhy(dut: VirtualPhy.Rx, delay: Int) {
    dut.clockDomain.waitSampling(delay)
    sleep(1)

    val sync0 = "1001111100"
    for (index <- 0 until 10) {
      assert(dut.io.locked.toBoolean == false)
      val bit = sync0(index).toString()
      dut.io.phy.data #= BigInt(bit * 16, 2)
      dut.clockDomain.waitSampling(1)
      sleep(1)
    }
    val sync1 = "0110000011"
    for (index <- 0 until 10) {
      assert(dut.io.locked.toBoolean == false)
      val bit = sync1(index).toString()
      dut.io.phy.data #= BigInt(bit * 16, 2)
      dut.clockDomain.waitSampling(1)
      sleep(1)
    }
    fork {
      assert(dut.io.locked.toBoolean == true, "No lock after two sync transmissions.")
    }
  }

  def sendTransmission(dut: VirtualPhy.Rx, transmission: String) {
    for (index <- 0 until 10) {
      assert(dut.io.locked.toBoolean == true)
      val bit = transmission(index).toString()
      dut.io.phy.data #= BigInt(bit * 16, 2)
      dut.clockDomain.waitSampling(1)
      sleep(1)
    }
    assert(dut.io.fromPhy.valid.toBoolean == true)
    assert(dut.io.fromPhy.payload.data(0).toBigInt == BigInt(transmission.reverse, 2))
  }

  test("rx") {
    val compiled = SimConfig.withWave.compile {
      VirtualPhy.Rx()
    }
    compiled.doSim("lock") { dut =>
      dut.clockDomain.forkStimulus(10)

      dut.io.fromPhy.ready #= false
      dut.io.phy.data #= BigInt(0)
      dut.io.phy.aux #= false
      dut.io.phy.fec #= false

      syncPhy(dut, 6)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("0101011011") { dut =>
      dut.clockDomain.forkStimulus(10)

      dut.io.fromPhy.ready #= false
      dut.io.phy.data #= BigInt(0)
      dut.io.phy.aux #= false
      dut.io.phy.fec #= false

      syncPhy(dut, 6)
      val transmission = "0101011011"
      sendTransmission(dut, transmission)
      sendTransmission(dut, transmission)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("push error") { dut =>
      dut.clockDomain.forkStimulus(10)

      dut.io.fromPhy.ready #= false
      dut.io.phy.data #= BigInt(0)
      dut.io.phy.aux #= false
      dut.io.phy.fec #= false

      syncPhy(dut, 6)
      val transmission = "0101011011"
      sendTransmission(dut, transmission)

      assert(dut.io.fromPhy.valid.toBoolean == true)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.pushError.toBoolean == true)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.pushError.toBoolean == false)

      dut.clockDomain.waitSampling(10)
    }
  }
}
