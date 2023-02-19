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

class Axi4ChipletIDTest extends AnyFunSuite {

  def lockTable(apb: Apb3Driver) {
      assert(apb.read(BigInt("00", 16)) == BigInt("0", 2))
      apb.write(BigInt("00", 16), BigInt("1", 2))
      assert(apb.read(BigInt("00", 16)) == BigInt("1", 2))
  }

  def readWrite(apb: Apb3Driver, address: BigInt, data: BigInt) {
      println(s"Write: ${data}")
      apb.write(address * 4, data)
      val read = apb.read(address * 4)
      println(s"Read: ${read}")
      assert(read == data)
  }
  def readWriteFail(apb: Apb3Driver, address: BigInt, data: BigInt) {
      println(s"Write: ${data}")
      apb.write(address * 4, data)
      val read = apb.read(address * 4)
      println(s"Read: ${read}")
      assert(read != data)
  }

  test("Axi4AddChipletID - APB3 Interface") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4ChipletID(axi4Config.core, axi4Config.noc, apb3Config)
      dut
    }
    compiled.doSim("Lock lookup table") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.clockDomain.waitSampling(5)

      lockTable(apb)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Write locked id") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.clockDomain.waitSampling(5)

      readWrite(apb, BigInt(1), BigInt("1010101010", 2))
      lockTable(apb)
      readWriteFail(apb, BigInt(1), BigInt("1111111111", 2))

      dut.clockDomain.waitSampling(10)
    }
  }
  test("Axi4AddChipletID - Chiplet ID added to AXI4x id signal") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4ChipletID(axi4Config.core, axi4Config.noc, apb3Config)
      dut
    }
    compiled.doSim("fromCore") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.clockDomain.waitSampling(5)

      dut.io.fromCore.input.ar.payload.addr #= BigInt("F" * 16, 16)
      dut.io.fromCore.input.aw.payload.addr #= BigInt("F" * 16, 16)
      dut.io.fromCore.input.w.payload.data #= BigInt("F" * 32, 16)
      dut.io.fromCore.output.r.payload.data #= BigInt("F" * 32, 16)

      readWrite(apb, BigInt(1), BigInt("1111111111", 2))

      dut.io.fromCore.input.ar.id #= BigInt(0)
      dut.io.fromCore.input.aw.id #= BigInt(0)
      dut.io.fromCore.output.r.id #= BigInt("11111111110000", 2)
      dut.io.fromCore.output.b.id #= BigInt("11111111110000", 2)

      sleep(1)

      assert(dut.io.fromCore.output.ar.id.toBigInt == BigInt("11111111110000", 2))
      assert(dut.io.fromCore.output.aw.id.toBigInt == BigInt("11111111110000", 2))
      assert(dut.io.fromCore.input.r.id.toBigInt == BigInt(0))
      assert(dut.io.fromCore.input.b.id.toBigInt == BigInt(0))

      dut.clockDomain.waitSampling(5)
    }

    compiled.doSim("fromNoc") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.clockDomain.waitSampling(5)

      dut.io.fromNoc.input.ar.payload.addr #= BigInt("F" * 16, 16)
      dut.io.fromNoc.input.aw.payload.addr #= BigInt("F" * 16, 16)
      dut.io.fromNoc.input.w.payload.data #= BigInt("F" * 32, 16)
      dut.io.fromNoc.output.r.payload.data #= BigInt("F" * 32, 16)

      readWrite(apb, BigInt(1), BigInt("1001111111", 2))

      dut.io.fromNoc.input.ar.id #= BigInt("10011111110000", 2)
      dut.io.fromNoc.input.aw.id #= BigInt("10011111110000", 2)
      dut.io.fromNoc.output.r.id #= BigInt(0)
      dut.io.fromNoc.output.b.id #= BigInt(0)

      sleep(1)

      assert(dut.io.fromNoc.output.ar.id.toBigInt == BigInt(0))
      assert(dut.io.fromNoc.output.aw.id.toBigInt == BigInt(0))
      assert(dut.io.fromNoc.input.r.id.toBigInt == BigInt("10011111110000", 2))
      assert(dut.io.fromNoc.input.b.id.toBigInt == BigInt("10011111110000", 2))

      dut.clockDomain.waitSampling(5)
    }
  }
  test("Axi4AddChipletID - Chiplet ID added to AXI4W") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4ChipletID(axi4Config.core, axi4Config.noc, apb3Config)
      dut
    }
    compiled.doSim("AXI4AW handshake") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      dut.io.fromCore.input.aw.valid #= false
      dut.io.fromCore.output.aw.ready #= false
      dut.io.fromCore.input.w.valid #= false
      dut.io.fromCore.output.w.ready #= false

      dut.clockDomain.waitSampling(5)
      sleep(1)

      dut.io.fromCore.input.aw.valid #= true
      dut.io.fromCore.output.aw.ready #= true

      for (index <- 0 until 9) {
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(dut.io.fromCore.input.aw.ready.toBoolean == true)
      }
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromCore.input.aw.ready.toBoolean == false)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("AXI4W before AXI4AW") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      dut.io.fromCore.input.aw.valid #= false
      dut.io.fromCore.output.aw.ready #= false
      dut.io.fromCore.input.w.valid #= false
      dut.io.fromCore.output.w.ready #= false

      dut.clockDomain.waitSampling(5)
      sleep(1)

      // No AXI4AW -> block AXI4W
      dut.io.fromCore.input.w.valid #= true
      assert(dut.io.fromCore.output.w.valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromCore.output.w.valid.toBoolean == false)
      dut.clockDomain.waitSampling(5)
      sleep(1)
      assert(dut.io.fromCore.output.w.valid.toBoolean == false)

      dut.io.fromCore.input.aw.valid #= true
      dut.io.fromCore.output.aw.ready #= true

      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromCore.input.aw.ready.toBoolean == true)
      assert(dut.io.fromCore.output.w.valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromCore.output.w.valid.toBoolean == true)

      dut.clockDomain.waitSampling(10)
    }
    compiled.doSim("AXI4W has correct user signal") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      dut.io.fromCore.input.aw.valid #= false
      dut.io.fromCore.output.aw.ready #= false
      dut.io.fromCore.input.w.valid #= false
      dut.io.fromCore.output.w.ready #= false

      dut.clockDomain.waitSampling(5)
      sleep(1)

      dut.io.fromCore.input.aw.valid #= true
      dut.io.fromCore.output.aw.ready #= true
      dut.io.fromCore.input.aw.addr #= BigInt("1010111011" + "1" * 54, 2)

      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromCore.input.aw.ready.toBoolean == true)
      dut.io.fromCore.input.aw.valid #= false
      dut.io.fromCore.output.aw.ready #= false

      dut.clockDomain.waitSampling(1)
      sleep(1)

      dut.io.fromCore.input.w.valid #= true
      dut.io.fromCore.output.w.ready #= true

      sleep(1)
      assert(dut.io.fromCore.output.w.valid.toBoolean == true)
      assert(dut.io.fromCore.output.w.user.toBigInt == BigInt("1010111011", 2))

      dut.clockDomain.waitSampling(1)
      sleep(1)

      dut.io.fromCore.input.w.valid #= false
      dut.io.fromCore.output.w.ready #= false

      dut.clockDomain.waitSampling(10)
    }
  }
  test("Axi4AddChipletID - Chiplet ID added to AXI4R") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4ChipletID(axi4Config.core, axi4Config.noc, apb3Config)
      dut
    }
    compiled.doSim("AXI4AR handshake") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      dut.io.fromNoc.input.ar.valid #= false
      dut.io.fromNoc.output.ar.ready #= false
      dut.io.fromNoc.input.r.valid #= false
      dut.io.fromNoc.output.r.ready #= false

      dut.clockDomain.waitSampling(5)
      sleep(1)

      dut.io.fromNoc.input.ar.valid #= true
      dut.io.fromNoc.output.ar.ready #= true

      for (index <- 0 until 9) {
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(dut.io.fromNoc.input.ar.ready.toBoolean == true)
      }
      dut.clockDomain.waitSampling(1)
      sleep(1)
      dut.io.fromNoc.output.ar.valid #= true
      assert(dut.io.fromNoc.input.ar.ready.toBoolean == false)

      dut.clockDomain.waitSampling(10)
    }
    compiled.doSim("AXI4R before AXI4AR") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      dut.io.fromNoc.input.ar.valid #= false
      dut.io.fromNoc.output.ar.ready #= false
      dut.io.fromNoc.output.r.valid #= false
      dut.io.fromNoc.input.r.ready #= false

      dut.clockDomain.waitSampling(5)
      sleep(1)

      // No AXI4AR -> block AXI4R
      dut.io.fromNoc.output.r.valid #= true
      assert(dut.io.fromNoc.input.r.valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromNoc.input.r.valid.toBoolean == false)
      dut.clockDomain.waitSampling(5)
      sleep(1)
      assert(dut.io.fromNoc.input.r.valid.toBoolean == false)

      dut.io.fromNoc.input.ar.valid #= true
      dut.io.fromNoc.output.ar.ready #= true

      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromNoc.input.ar.ready.toBoolean == true)
      assert(dut.io.fromNoc.input.r.valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromNoc.input.r.valid.toBoolean == true)

      dut.clockDomain.waitSampling(10)
    }
    compiled.doSim("AXI4R has correct user signal") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      dut.io.fromNoc.input.ar.valid #= false
      dut.io.fromNoc.output.ar.ready #= false
      dut.io.fromNoc.output.r.valid #= false
      dut.io.fromNoc.input.r.ready #= false

      dut.clockDomain.waitSampling(5)
      sleep(1)

      dut.io.fromNoc.input.ar.valid #= true
      dut.io.fromNoc.output.ar.ready #= true
      dut.io.fromNoc.input.ar.addr #= BigInt("1010111010" + "1" * 54, 2)
      dut.io.fromNoc.input.ar.id #= BigInt("10101110110000", 2)

      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromNoc.input.ar.ready.toBoolean == true)
      dut.io.fromNoc.input.ar.valid #= false
      dut.io.fromNoc.output.ar.ready #= false

      dut.clockDomain.waitSampling(1)
      sleep(1)

      dut.io.fromNoc.output.r.valid #= true
      dut.io.fromNoc.input.r.ready #= true

      sleep(1)
      assert(dut.io.fromNoc.input.r.valid.toBoolean == true)
      assert(dut.io.fromNoc.input.r.user.toBigInt == BigInt("1010111011", 2))

      dut.clockDomain.waitSampling(1)
      sleep(1)

      dut.io.fromNoc.output.r.valid #= false
      dut.io.fromNoc.input.r.ready #= false

      dut.clockDomain.waitSampling(10)
    }
  }
  test("Axi4AddChipletID - Chiplet ID added to AXI4B") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4ChipletID(axi4Config.core, axi4Config.noc, apb3Config)
      dut
    }
    compiled.doSim("AXI4AW handshake") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      dut.io.fromNoc.input.aw.valid #= false
      dut.io.fromNoc.output.aw.ready #= false
      dut.io.fromNoc.input.w.valid #= false
      dut.io.fromNoc.output.w.ready #= false
      dut.io.fromNoc.input.b.valid #= false
      dut.io.fromNoc.output.b.ready #= false

      dut.clockDomain.waitSampling(5)
      sleep(1)

      dut.io.fromNoc.input.aw.valid #= true
      dut.io.fromNoc.output.aw.ready #= true

      for (index <- 0 until 9) {
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(dut.io.fromNoc.input.aw.ready.toBoolean == true)
      }
      dut.io.fromNoc.output.aw.ready #= false
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromNoc.input.aw.ready.toBoolean == false)

      dut.clockDomain.waitSampling(10)
    }
    compiled.doSim("AXI4B before AXI4AW") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      dut.io.fromNoc.input.aw.valid #= false
      dut.io.fromNoc.output.aw.ready #= false
      dut.io.fromNoc.input.w.valid #= false
      dut.io.fromNoc.output.w.ready #= false
      dut.io.fromNoc.output.b.valid #= false
      dut.io.fromNoc.input.b.ready #= false

      dut.clockDomain.waitSampling(5)
      sleep(1)

      // No AXI4AW -> block AXI4W
      dut.io.fromNoc.output.b.valid #= true
      assert(dut.io.fromNoc.input.b.valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromNoc.input.b.valid.toBoolean == false)
      dut.clockDomain.waitSampling(5)
      sleep(1)
      assert(dut.io.fromNoc.input.b.valid.toBoolean == false)

      dut.io.fromNoc.input.aw.valid #= true
      dut.io.fromNoc.output.aw.ready #= true

      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromNoc.input.aw.ready.toBoolean == true)
      assert(dut.io.fromNoc.input.b.valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromNoc.input.b.valid.toBoolean == true)

      dut.clockDomain.waitSampling(10)
    }
    compiled.doSim("AXI4B has correct user signal") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      dut.io.fromNoc.input.aw.valid #= false
      dut.io.fromNoc.output.aw.ready #= false
      dut.io.fromNoc.output.b.valid #= false
      dut.io.fromNoc.input.b.ready #= false

      dut.clockDomain.waitSampling(5)
      sleep(1)

      dut.io.fromNoc.input.aw.valid #= true
      dut.io.fromNoc.output.aw.ready #= true
      dut.io.fromNoc.input.aw.addr #= BigInt("1010111010" + "1" * 54, 2)
      dut.io.fromNoc.input.aw.id #= BigInt("10101110110000", 2)

      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.fromNoc.input.aw.ready.toBoolean == true)
      dut.io.fromNoc.input.aw.valid #= false
      dut.io.fromNoc.output.aw.ready #= false

      dut.clockDomain.waitSampling(1)
      sleep(1)

      dut.io.fromNoc.output.b.valid #= true
      dut.io.fromNoc.input.b.ready #= true

      sleep(1)
      assert(dut.io.fromNoc.input.b.valid.toBoolean == true)
      assert(dut.io.fromNoc.input.b.user.toBigInt == BigInt("1010111011", 2))

      dut.clockDomain.waitSampling(1)
      sleep(1)

      dut.io.fromNoc.output.b.valid #= false
      dut.io.fromNoc.input.b.ready #= false

      dut.clockDomain.waitSampling(10)
    }
  }
}
