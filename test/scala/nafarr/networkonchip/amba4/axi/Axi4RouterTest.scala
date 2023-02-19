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

class Axi4RouterTest extends AnyFunSuite {

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
      val read = apb.read(address + 4)
      println(s"Read: ${read}")
      assert(read != data)
  }

  def disableRequests(dut: Axi4Router) {
    for (port <- Array(dut.io.local, dut.io.north, dut.io.east, dut.io.south, dut.io.west)) {
      port.input.ar.valid #= false
      port.input.aw.valid #= false
      port.input.w.valid #= false
      port.input.r.ready #= false
      port.input.b.ready #= false
      port.output.ar.ready #= false
      port.output.aw.ready #= false
      port.output.w.ready #= false
      port.output.r.valid #= false
      port.output.b.valid #= false
    }
  }

  def sendAr(dut: Axi4Router, port: Axi4, address: BigInt, id: BigInt) {
    fork {
      dut.clockDomain.waitSampling(1)
      sleep(1)
      port.ar.valid #= true
      port.ar.addr #= address
      port.ar.id #= id
      sleep(1)
      assert(port.ar.ready.toBoolean == true)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      port.ar.valid #= false
    }
  }

  def expectAr(dut: Axi4Router, port: Axi4, delay: Int, address: BigInt, id: BigInt) {
    fork {
      dut.clockDomain.waitSampling(delay)
      sleep(1)
      assert(port.ar.valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(port.ar.addr.toBigInt == address)
      assert(port.ar.id.toBigInt == id)
      assert(port.ar.valid.toBoolean == true)
      port.ar.ready #= true
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(port.ar.valid.toBoolean == false)
      assert(port.ar.valid.toBoolean == false)
      port.ar.ready #= false
    }
  }

  def expectArError(dut: Axi4Router, port: Axi4, delay: Int, origin: BigInt, errorSource: BigInt) {
    fork {
      dut.clockDomain.waitSampling(delay)
      sleep(1)
      assert(port.r.valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(port.r.valid.toBoolean == true)
      assert(port.r.id.toBigInt == (errorSource << 4))
      assert(port.r.user.toBigInt == (origin >> 4))
      assert(port.r.resp.toBigInt == BigInt("11", 2))
      assert(port.r.valid.toBoolean == true)
      port.r.ready #= true
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(port.r.valid.toBoolean == false)
      port.r.ready #= false
    }
  }

  def sendAw(dut: Axi4Router, port: Axi4, address: BigInt, id: BigInt) {
    fork {
      dut.clockDomain.waitSampling(1)
      sleep(1)
      port.aw.valid #= true
      port.aw.addr #= address
      port.aw.id #= id
      sleep(1)
      assert(port.aw.ready.toBoolean == true)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      port.aw.valid #= false
    }
  }

  def expectAw(dut: Axi4Router, port: Axi4, delay: Int, address: BigInt, id: BigInt) {
    fork {
      dut.clockDomain.waitSampling(delay)
      sleep(1)
      assert(port.aw.valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(port.aw.addr.toBigInt == address)
      assert(port.aw.id.toBigInt == id)
      assert(port.aw.valid.toBoolean == true)
      port.aw.ready #= true
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(port.aw.valid.toBoolean == false)
      assert(port.aw.valid.toBoolean == false)
      port.aw.ready #= false
    }
  }

  def expectAwError(dut: Axi4Router, port: Axi4, delay: Int, origin: BigInt, errorSource: BigInt) {
    fork {
      dut.clockDomain.waitSampling(delay)
      sleep(1)
      assert(port.b.valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(port.b.valid.toBoolean == true)
      assert(port.b.id.toBigInt == (errorSource << 4))
      assert(port.b.user.toBigInt == (origin >> 4))
      assert(port.b.resp.toBigInt == BigInt("11", 2))
      assert(port.b.valid.toBoolean == true)
      port.b.ready #= true
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(port.b.valid.toBoolean == false)
      port.b.ready #= false
    }
  }

  def sendR(dut: Axi4Router, port: Axi4, id: BigInt, address: BigInt, count: Int) {
    fork {
      for (index <- 0 until count) {
        dut.clockDomain.waitSampling(1)
        sleep(1)
        port.r.valid #= true
        port.r.id #= id
        port.r.user #= address
        if (index == count - 1) {
          port.r.last #= true
        } else {
          port.r.last #= false
        }
        sleep(1)
        assert(port.r.ready.toBoolean == true)
      }
      dut.clockDomain.waitSampling(1)
      sleep(1)
      port.r.valid #= false
    }
  }

  def expectR(dut: Axi4Router, port: Axi4, delay: Int, id: BigInt, address: BigInt, count: Int) {
    fork {
      dut.clockDomain.waitSampling(delay)
      sleep(1)
      assert(port.r.valid.toBoolean == false)

      for (index <- 0 until count) {
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(port.r.valid.toBoolean == true)
        assert(port.r.id.toBigInt == id)
        assert(port.r.user.toBigInt == address)
        if (index == count - 1) {
          assert(port.r.last.toBoolean == true)
        } else {
          assert(port.r.last.toBoolean == false)
        }
        port.r.ready #= true
      }
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(port.r.valid.toBoolean == false)
      assert(port.r.valid.toBoolean == false)
      port.r.ready #= false
    }
  }

  def sendW(dut: Axi4Router, port: Axi4, address: BigInt, count: Int) {
    fork {
      for (index <- 0 until count) {
        dut.clockDomain.waitSampling(1)
        sleep(1)
        port.w.valid #= true
        port.w.user #= address
        if (index == count - 1) {
          port.w.last #= true
        } else {
          port.w.last #= false
        }
        sleep(1)
        assert(port.w.ready.toBoolean == true)
      }
      dut.clockDomain.waitSampling(1)
      sleep(1)
      port.w.valid #= false
    }
  }

  def expectW(dut: Axi4Router, port: Axi4, delay: Int, address: BigInt, count: Int) {
    fork {
      dut.clockDomain.waitSampling(delay)
      sleep(1)
      assert(port.w.valid.toBoolean == false)

      for (index <- 0 until count) {
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(port.w.valid.toBoolean == true)
        assert(port.w.user.toBigInt == address)
        if (index == count - 1) {
          assert(port.w.last.toBoolean == true)
        } else {
          assert(port.w.last.toBoolean == false)
        }
        port.w.ready #= true
      }
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(port.w.valid.toBoolean == false)
      assert(port.w.valid.toBoolean == false)
      port.w.ready #= false
    }
  }

  test("Axi4Router - APB3 Interface") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4Router(axi4Config.noc, apb3Config)
      dut
    }
    compiled.doSim("Lock lookup table") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.clockDomain.waitSampling(5)

      lockTable(apb)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Write locked interface") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.clockDomain.waitSampling(5)

      readWrite(apb, BigInt(1), BigInt("11110000001100110011", 2))
      lockTable(apb)
      readWriteFail(apb, BigInt(1), BigInt("11110000001111111111", 2))

      dut.clockDomain.waitSampling(10)
    }
  }

  test("Axi4Router - Disabled by boot") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4Router(axi4Config.noc, apb3Config)
      dut
    }
    compiled.doSim("Read") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      disableRequests(dut)

      dut.clockDomain.waitSampling(1)
      sleep(1)

      val address = BigInt("0001100011" + "101001" * 9, 2)
      val id = BigInt("0000100001" + "0" * 4, 2)
      sendAr(dut, dut.io.north.input, address, id)
      expectAr(dut, dut.io.local.output, 5, address, id)

      dut.clockDomain.waitSampling(20)
    }
    compiled.doSim("write") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      disableRequests(dut)

      dut.clockDomain.waitSampling(1)
      sleep(1)

      val address = BigInt("0001100011" + "101001" * 9, 2)
      val id = BigInt("0000100001" + "0" * 4, 2)
      sendAw(dut, dut.io.north.input, address, id)
      expectAw(dut, dut.io.local.output, 5, address, id)

      dut.clockDomain.waitSampling(20)
    }
  }
  // Only enable north and send from north
  test("Axi4Router - Unroutable request") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4Router(axi4Config.noc, apb3Config)
      dut
    }
    compiled.doSim("Read") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      disableRequests(dut)
      readWrite(apb, BigInt(1), BigInt("1000000000" + "0001000010", 2))

      dut.clockDomain.waitSampling(1)
      sleep(1)

      val address = BigInt("0001100010" + "101001" * 9, 2)
      val id = BigInt("0000100001" + "0" * 4, 2)

      sendAr(dut, dut.io.north.input, address, id)
      expectArError(dut, dut.io.north.input, 7, id, BigInt("0001000010", 2))

      dut.clockDomain.waitSampling(20)
    }
    compiled.doSim("Read 10 times") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      disableRequests(dut)
      readWrite(apb, BigInt(1), BigInt("1000000000" + "0001000010", 2))

      dut.clockDomain.waitSampling(1)
      sleep(1)

      val address = BigInt("0001100010" + "101001" * 9, 2)
      val id = BigInt("0000100001" + "0" * 4, 2)
      for (index <- 0 until 10) {
        val ongoingId = id + (index << 6)

        sendAr(dut, dut.io.north.input, address, ongoingId)
        expectArError(dut, dut.io.north.input, 7, ongoingId, BigInt("0001000010", 2))

        dut.clockDomain.waitSampling(15)
      }
    }
    compiled.doSim("Write") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      disableRequests(dut)
      readWrite(apb, BigInt(1), BigInt("1000000000" + "0001000010", 2))

      dut.clockDomain.waitSampling(1)
      sleep(1)

      val address = BigInt("0001100010" + "101001" * 9, 2)
      val id = BigInt("0000100001" + "0" * 4, 2)

      sendAw(dut, dut.io.north.input, address, id)
      expectAwError(dut, dut.io.north.input, 7, id, BigInt("0001000010", 2))

      dut.clockDomain.waitSampling(20)
    }
    compiled.doSim("Write 10 times") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      disableRequests(dut)
      readWrite(apb, BigInt(1), BigInt("1000000000" + "0001000010", 2))

      dut.clockDomain.waitSampling(1)
      sleep(1)

      val address = BigInt("0001100010" + "101001" * 9, 2)
      val id = BigInt("0000100001" + "0" * 4, 2)
      for (index <- 0 until 10) {
        val ongoingId = id + (index << 6)

        sendAw(dut, dut.io.north.input, address, ongoingId)
        expectAwError(dut, dut.io.north.input, 7, ongoingId, BigInt("0001000010", 2))

        dut.clockDomain.waitSampling(15)
      }
    }
  }
/*
  test("Axi4Router - Diagonal with flip") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4Router(axi4Config.noc, apb3Config)
      dut
    }
    compiled.doSim("Read") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      disableRequests(dut)
      readWrite(apb, BigInt(1), BigInt("1111000000" + "0001000010", 2))

      dut.clockDomain.waitSampling(1)
      sleep(1)

      val address = BigInt("0001100011" + "101001" * 9, 2)
      val id = BigInt("0000100001" + "0" * 4, 2)

      sendAr(dut, dut.io.west.input, address, id)
      expectAr(dut, dut.io.east.output, 5, address, id)

      dut.clockDomain.waitSampling(10)

      sendAr(dut, dut.io.west.input, address, id)
      expectAr(dut, dut.io.north.output, 5, address, id)

      dut.clockDomain.waitSampling(10)

      sendAr(dut, dut.io.west.input, address, id)
      expectAr(dut, dut.io.east.output, 5, address, id)

      dut.clockDomain.waitSampling(20)
    }
    compiled.doSim("Write") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      disableRequests(dut)
      readWrite(apb, BigInt(1), BigInt("1111000000" + "0001000010", 2))

      dut.clockDomain.waitSampling(1)
      sleep(1)

      val address = BigInt("0001100011" + "101001" * 9, 2)
      val id = BigInt("0000100001" + "0" * 4, 2)

      sendAw(dut, dut.io.west.input, address, id)
      expectAw(dut, dut.io.east.output, 5, address, id)

      dut.clockDomain.waitSampling(10)

      sendAw(dut, dut.io.west.input, address, id)
      expectAw(dut, dut.io.north.output, 5, address, id)

      dut.clockDomain.waitSampling(10)

      sendAw(dut, dut.io.west.input, address, id)
      expectAw(dut, dut.io.east.output, 5, address, id)

      dut.clockDomain.waitSampling(20)
    }
  }
  test("Axi4Router - Lock with last flag") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4Router(axi4Config.noc, apb3Config, 10, 10)
      dut
    }
    compiled.doSim("Read") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      disableRequests(dut)
      readWrite(apb, BigInt(1), BigInt("1111000000" + "0001000010", 2))

      dut.clockDomain.waitSampling(1)
      sleep(1)

      val address = BigInt("0001100011", 2)

      val id0 = BigInt("0000100001" + "0" * 4, 2)
      sendR(dut, dut.io.west.output, id0, address, 10)
      expectR(dut, dut.io.east.input, 6, id0, address, 10)

      dut.clockDomain.waitSampling(3)

      val id1 = BigInt("0000000001" + "0" * 4, 2)
      sendR(dut, dut.io.south.output, id1, address, 4)
      expectR(dut, dut.io.east.input, 15, id1, address, 4)

      dut.clockDomain.waitSampling(50)
    }
    compiled.doSim("Write") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      disableRequests(dut)
      readWrite(apb, BigInt(1), BigInt("1111000000" + "0001000010", 2))

      dut.clockDomain.waitSampling(1)
      sleep(1)

      val address = BigInt("0001100011", 2)

      sendW(dut, dut.io.west.input, address, 10)
      expectW(dut, dut.io.east.output, 5, address, 10)

      dut.clockDomain.waitSampling(3)

      sendW(dut, dut.io.south.input, address, 4)
      expectW(dut, dut.io.east.output, 13, address, 4)

      dut.clockDomain.waitSampling(50)
    }
  }
*/
}
