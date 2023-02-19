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

class Axi4MemoryExtensionTest extends AnyFunSuite {

  def lockTable(apb: Apb3Driver) {
      assert(apb.read(BigInt("00", 16)) == BigInt("10000" + "0" * 7 + "0", 2))
      apb.write(BigInt("00", 16), BigInt("1", 2))
      assert(apb.read(BigInt("00", 16)) == BigInt("10000" + "0" * 7 + "1", 2))
  }

  def readWrite(apb: Apb3Driver, address: BigInt, data: BigInt, validFlag: Boolean = true) {
      apb.write(address * 4, data)
      val compare = if (validFlag) BigInt("100000000000000", 2) + data else data
      assert(apb.read(address * 4) == compare)
  }
  def readWriteFail(apb: Apb3Driver, address: BigInt, data: BigInt, validFlag: Boolean = true) {
      apb.write(address * 4, data)
      val compare = if (validFlag) BigInt("100000000000000", 2) + data else data
      assert(apb.read(address * 4) != compare)
  }

  test("Axi4MemoryExtension") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4MemoryExtension(axi4Config.coreUpsized, axi4Config.core, apb3Config)
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

      readWrite(apb, BigInt(1), BigInt("01110010101111", 2))
      lockTable(apb)
      readWriteFail(apb, BigInt(2), BigInt("01110010101111", 2))
      readWrite(apb, BigInt(2), BigInt("00000000000000", 2), false)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Fill lookupTable") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.clockDomain.waitSampling(5)

      for (index <- 0 until dut.lookupEntries) {
        readWrite(apb, BigInt(1 + index), BigInt(index))
      }

      dut.clockDomain.waitSampling(10)
    }
    compiled.doSim("Verify memory extension from core") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.io.fromCore.input.ar.valid #= false
      dut.io.fromCore.output.ar.ready #= true
      dut.io.fromCore.input.aw.valid #= false
      dut.io.fromCore.output.aw.ready #= true
      dut.clockDomain.waitSampling(5)

      readWrite(apb, BigInt(1), BigInt("11000000111111", 2))
      readWrite(apb, BigInt(10), BigInt("00111111001110", 2))

      dut.clockDomain.waitSampling(5)
      sleep(1)

      assert(dut.io.fromCore.output.ar.valid.toBoolean == false)
      dut.io.fromCore.input.ar.addr #= BigInt("1111" + "0" * 28, 2)
      dut.io.fromCore.input.ar.valid #= true
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromCore.output.ar.valid.toBoolean == true)
      assert(dut.io.fromCore.output.ar.addr.toBigInt == BigInt("1100000011" + "0" * 54, 2))
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromCore.output.ar.valid.toBoolean == false)
      dut.io.fromCore.input.ar.valid #= false

      dut.clockDomain.waitSampling(5)
      sleep(1)
      dut.io.fromCore.output.ar.ready #= false

      assert(dut.io.fromCore.output.ar.valid.toBoolean == false)
      dut.io.fromCore.input.ar.addr #= BigInt("1110" + "0" * 28, 2)
      dut.io.fromCore.input.ar.valid #= true
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromCore.output.ar.valid.toBoolean == true)
      assert(dut.io.fromCore.output.ar.addr.toBigInt == BigInt("0011111100" + "0" * 54, 2))
      dut.clockDomain.waitSampling(3)
      sleep(1)
      assert(dut.io.fromCore.output.ar.valid.toBoolean == true)
      dut.io.fromCore.output.ar.ready #= true
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromCore.output.ar.valid.toBoolean == false)
      dut.io.fromCore.input.ar.valid #= false

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Verify memory extension from core - BURST") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.io.fromCore.input.ar.valid #= false
      dut.io.fromCore.output.ar.ready #= true
      dut.io.fromCore.input.aw.valid #= false
      dut.io.fromCore.output.aw.ready #= true
      dut.clockDomain.waitSampling(5)

      readWrite(apb, BigInt(1), BigInt("11000000111111", 2))
      readWrite(apb, BigInt(10), BigInt("00111111001110", 2))

      dut.clockDomain.waitSampling(5)
      sleep(1)

      assert(dut.io.fromCore.output.ar.valid.toBoolean == false)
      dut.io.fromCore.input.ar.addr #= BigInt("1111" + "0" * 28, 2)
      dut.io.fromCore.input.ar.valid #= true
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromCore.output.ar.valid.toBoolean == true)
      assert(dut.io.fromCore.output.ar.addr.toBigInt == BigInt("1100000011" + "0" * 54, 2))
      dut.clockDomain.waitSampling(1)
      sleep(1)
      dut.io.fromCore.input.ar.addr #= BigInt("1110" + "0" * 28, 2)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromCore.output.ar.valid.toBoolean == true)
      assert(dut.io.fromCore.output.ar.addr.toBigInt == BigInt("0011111100" + "0" * 54, 2))

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Verify memory extension from core - no hit") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.io.fromCore.input.ar.valid #= false
      dut.io.fromCore.output.ar.ready #= true
      dut.io.fromCore.input.aw.valid #= false
      dut.io.fromCore.output.aw.ready #= true

      dut.clockDomain.waitSampling(5)
      sleep(1)

      assert(dut.io.fromCore.output.ar.valid.toBoolean == false)
      dut.io.fromCore.input.ar.addr #= BigInt("1111" + "0" * 28, 2)
      dut.io.fromCore.input.ar.valid #= true
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromCore.output.ar.valid.toBoolean == true)
      assert(dut.io.fromCore.output.ar.addr.toBigInt == BigInt("0001100011" + "0" * 54, 2))
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromCore.output.ar.valid.toBoolean == false)
      dut.io.fromCore.input.ar.valid #= false

      dut.clockDomain.waitSampling(10)
    }
    compiled.doSim("WVALID delayed") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.io.fromCore.input.ar.valid #= false
      dut.io.fromCore.output.ar.ready #= true
      dut.io.fromCore.input.aw.valid #= false
      dut.io.fromCore.output.aw.ready #= true
      dut.io.fromCore.input.w.valid #= false
      dut.io.fromCore.output.w.ready #= true

      dut.clockDomain.waitSampling(5)
      sleep(1)

      assert(dut.io.fromCore.output.w.valid.toBoolean == false)
      dut.io.fromCore.input.w.valid #= true
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromCore.output.w.valid.toBoolean == true)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromCore.output.w.valid.toBoolean == false)
      dut.io.fromCore.input.w.valid #= false
    }
  }
}
