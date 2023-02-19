package nafarr.networkonchip.amba4.axi

import org.scalatest.funsuite.AnyFunSuite

import spinal.sim._
import spinal.core._
import spinal.core.sim._
import spinal.lib.bus.misc.SizeMapping
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba4.axi.sim._
import spinal.lib.bus.amba3.apb.sim.Apb3Driver
import nafarr.CheckTester._

class Axi4MemoryTranslationTest extends AnyFunSuite {

  def lockTable(apb: Apb3Driver) {
      assert(apb.read(BigInt("00", 16)) == BigInt("111" + "00010000" + "0" * 7 + "0", 2))
      apb.write(BigInt("00", 16), BigInt("1", 2))
      assert(apb.read(BigInt("00", 16)) == BigInt("111" + "00010000" + "0" * 7 + "1", 2))
  }

  def readWrite(apb: Apb3Driver, address: BigInt, data: BigInt, validFlag: Boolean = true) {
      println(s"Write: ${data}")
      apb.write(address * 4, data)
      val compare = if (validFlag) BigInt("1", 2) + data else data
      val read = apb.read(address * 4)
      println(s"Read: ${read} compared with: ${compare}")
      assert(read == compare)
  }
  def readWriteFail(apb: Apb3Driver, address: BigInt, data: BigInt, validFlag: Boolean = true) {
      apb.write(address * 4, data)
      val compare = if (validFlag) BigInt("1", 2) + data else data
      assert(apb.read(address * 4) != compare)
  }

  def verifyPageType(dut: Axi4MemoryTranslation, apb: Apb3Driver, page: String, size: Int) {
    dut.io.input.ar.valid #= false
    dut.io.output.ar.ready #= true
    dut.io.input.aw.valid #= false
    dut.io.output.aw.ready #= true
    dut.clockDomain.waitSampling(5)

    readWrite(apb, BigInt(1), BigInt("1" * 32, 2), false)
    readWrite(apb, BigInt(2), BigInt("1" * 20, 2), false)
    readWrite(apb, BigInt(3), BigInt("0" * 32, 2), false)
    readWrite(apb, BigInt(4), BigInt("0" * 20, 2), false)
    readWrite(apb, BigInt(5), BigInt(page + "0" * 8, 2))

    dut.clockDomain.waitSampling(5)
    sleep(1)

    assert(dut.io.output.ar.valid.toBoolean == false)
    dut.io.input.ar.addr #= BigInt("1" * 52 + "0" * 12, 2)
    dut.io.input.ar.valid #= true
    dut.clockDomain.waitSampling(1)
    sleep(1)
    assert(dut.io.output.ar.valid.toBoolean == true)
    assert(dut.io.output.ar.addr.toBigInt == BigInt("0" * size + "1" * (52 - size) + "0" * 12, 2))
    dut.clockDomain.waitSampling(1)
    sleep(1)
    assert(dut.io.output.ar.valid.toBoolean == false)
    dut.io.input.ar.valid #= false
  }

  test("Axi4MemoryExtension") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4MemoryTranslation(axi4Config.core, apb3Config)
      dut
    }
    compiled.doSim("Lock lookup table") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.clockDomain.waitSampling(5)

      lockTable(apb)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Write locked lookup table") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.clockDomain.waitSampling(5)

      readWrite(apb, BigInt(1), BigInt("1" * 32, 2), false)
      readWrite(apb, BigInt(2), BigInt("1" * 20, 2), false)
      readWrite(apb, BigInt(3), BigInt("1" * 32, 2), false)
      readWrite(apb, BigInt(4), BigInt("1" * 20, 2), false)
      readWrite(apb, BigInt(5), BigInt("101" + "0" * 8, 2))
      lockTable(apb)
      readWriteFail(apb, BigInt(2), BigInt("01110010101111", 2))
      readWrite(apb, BigInt(6), BigInt("00000000000000", 2), false)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Fill lookupTable") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.clockDomain.waitSampling(5)

      for (index <- 0 until dut.lookupEntries) {
        readWrite(apb, BigInt(1 + (index * 5)), BigInt("1" * 32, 2), false)
        readWrite(apb, BigInt(2 + (index * 5)), BigInt("1" * 20, 2), false)
        readWrite(apb, BigInt(3 + (index * 5)), BigInt("1" * 32, 2), false)
        readWrite(apb, BigInt(4 + (index * 5)), BigInt("1" * 20, 2), false)
        readWrite(apb, BigInt(5 + (index * 5)), BigInt("101" + "0" * 8, 2))
      }

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Verify memory translation 4k page") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      verifyPageType(dut, apb, "000", 52)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Verify memory translation 64k page") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      verifyPageType(dut, apb, "001", 48)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Verify memory translation 2m page") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      verifyPageType(dut, apb, "010", 43)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Verify memory translation 4m page") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      verifyPageType(dut, apb, "011", 42)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Verify memory translation 1g page") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      verifyPageType(dut, apb, "100", 34)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Verify memory translation 1t page") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      verifyPageType(dut, apb, "101", 24)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Verify memory translation chiplet id page") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      verifyPageType(dut, apb, "110", 10)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("WVALID delayed") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.io.input.ar.valid #= false
      dut.io.output.ar.ready #= true
      dut.io.input.aw.valid #= false
      dut.io.output.aw.ready #= true
      dut.io.input.w.valid #= false
      dut.io.output.w.ready #= true

      dut.clockDomain.waitSampling(5)
      sleep(1)

      assert(dut.io.output.w.valid.toBoolean == false)
      dut.io.input.w.valid #= true
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.output.w.valid.toBoolean == true)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.output.w.valid.toBoolean == false)
      dut.io.input.w.valid #= false
    }

  }
}
