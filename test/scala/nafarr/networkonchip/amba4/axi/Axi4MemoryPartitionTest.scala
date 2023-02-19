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

class Axi4MemoryPartitionTest extends AnyFunSuite {

  def lockTable(apb: Apb3Driver) {
      assert(apb.read(BigInt("00", 16)) == BigInt("00010000" + "0" * 7 + "0", 2))
      apb.write(BigInt("00", 16), BigInt("1", 2))
      assert(apb.read(BigInt("00", 16)) == BigInt("00010000" + "0" * 7 + "1", 2))
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
      val dut = Axi4MemoryPartition(axi4Config.noc, apb3Config)
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
      readWrite(apb, BigInt(2), BigInt("1" * 10, 2), false)
      readWrite(apb, BigInt(3), BigInt("1" * 32, 2), false)
      readWrite(apb, BigInt(4), BigInt("1" * 10, 2), false)
      readWrite(apb, BigInt(5), BigInt("1100000011" + "00000111" + "0" * 8, 2))
      lockTable(apb)
      readWriteFail(apb, BigInt(2), BigInt("01110010101111", 2))
      readWrite(apb, BigInt(6), BigInt("00000000000000", 2), false)

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Fill lookupTable") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.clockDomain.waitSampling(5)

      for (index <- 0 until dut.partitions) {
        readWrite(apb, BigInt(1 + (index * 5)), BigInt("1" * 32, 2), false)
        readWrite(apb, BigInt(2 + (index * 5)), BigInt("1" * 10, 2), false)
        readWrite(apb, BigInt(3 + (index * 5)), BigInt("1" * 32, 2), false)
        readWrite(apb, BigInt(4 + (index * 5)), BigInt("1" * 10, 2), false)
        readWrite(apb, BigInt(5 + (index * 5)), BigInt("101" + "0" * 8, 2))
        readWrite(apb, BigInt(5 + (index * 5)), BigInt("1100000011" + "00000111" + "0" * 8, 2))
      }

      dut.clockDomain.waitSampling(10)
    }

    compiled.doSim("Read errors fifo occupancy") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      dut.io.input.ar.valid #= false
      dut.io.input.r.ready #= false
      dut.io.output.r.valid #= false
      dut.clockDomain.waitSampling(5)
      sleep(1)

      readWrite(apb, BigInt(0), BigInt("1100011111" + "00010000" + "0" * 8, 2), false)

      val id = BigInt("10101000001101", 2)
      dut.io.input.ar.valid #= true
      dut.io.input.ar.addr #= BigInt(0)
      dut.io.input.ar.id #= id
      sleep(1)

      fork {
        assert(dut.io.input.ar.ready.toBoolean == false)
        dut.clockDomain.waitSampling(2)
        sleep(1)
        for (index <- 0 until 8) {
          assert(dut.io.input.ar.ready.toBoolean == true)
          dut.clockDomain.waitSampling(1)
          sleep(1)
          assert(dut.io.input.ar.ready.toBoolean == false)
          dut.clockDomain.waitSampling(1)
          sleep(1)
          assert(dut.io.input.ar.ready.toBoolean == false)
          dut.clockDomain.waitSampling(1)
          sleep(1)
        }

        assert(dut.io.input.ar.ready.toBoolean == false)
      }

      fork {
        dut.clockDomain.waitSampling(5)
        sleep(1)
        assert(dut.io.input.r.id.toBigInt == BigInt("1100011111" + "1101", 2))
        assert(dut.io.input.r.user.toBigInt == BigInt("1010100000", 2))
        assert(dut.io.input.r.resp.toBigInt == BigInt("11", 2))
        assert(dut.io.input.r.valid.toBoolean == true)
      }

      dut.clockDomain.waitSampling(30)
    }


    compiled.doSim("Read errors during read responses") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      dut.io.input.ar.valid #= false
      dut.io.input.r.ready #= false
      dut.io.output.r.valid #= false
      dut.io.output.r.last #= false
      dut.clockDomain.waitSampling(5)
      sleep(1)

      readWrite(apb, BigInt(0), BigInt("1100011111" + "00010000" + "0" * 8, 2), false)

      val id = BigInt("10101000001101", 2)
      dut.io.input.ar.valid #= true
      dut.io.input.ar.addr #= BigInt(0)
      dut.io.input.ar.id #= id

      dut.io.output.r.valid #= true
      dut.io.input.r.ready #= true
      dut.io.output.r.id #= BigInt("00001111111111", 2)
      dut.io.output.r.resp #= BigInt("00", 2)

      dut.clockDomain.waitSampling(2)
      sleep(1)
      dut.io.input.ar.valid #= false

      for (index <- 0 until 5) {
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(dut.io.input.r.id.toBigInt == BigInt("00001111111111", 2))
        assert(dut.io.input.r.resp.toBigInt == BigInt("00", 2))
        assert(dut.io.input.r.valid.toBoolean == true)
      }

      dut.io.output.r.last #= true
      dut.clockDomain.waitSampling(1)
      sleep(1)
      assert(dut.io.input.r.valid.toBoolean == false)
      dut.clockDomain.waitSampling(1)
      sleep(1)
      dut.io.output.r.last #= false
      assert(dut.io.input.r.id.toBigInt == BigInt("1100011111" + "1101", 2))
      assert(dut.io.input.r.user.toBigInt == BigInt("1010100000", 2))
      assert(dut.io.input.r.resp.toBigInt == BigInt("11", 2))
      assert(dut.io.input.r.valid.toBoolean == true)

      dut.clockDomain.waitSampling(1)
      sleep(1)

      for (index <- 0 until 5) {
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(dut.io.input.r.id.toBigInt == BigInt("00001111111111", 2))
        assert(dut.io.input.r.resp.toBigInt == BigInt("00", 2))
        assert(dut.io.input.r.valid.toBoolean == true)
      }

      dut.clockDomain.waitSampling(30)
    }

    compiled.doSim("Read check boundaries") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      dut.io.input.ar.valid #= false
      dut.io.output.ar.ready #= false
      dut.io.input.r.ready #= false
      dut.io.output.r.valid #= false
      dut.io.output.r.last #= false
      dut.clockDomain.waitSampling(5)

      readWrite(apb, BigInt(0), BigInt("1100011111" + "00010000" + "0" * 8, 2), false)
      readWrite(apb, BigInt(1), BigInt("00000000", 16), false)
      readWrite(apb, BigInt(2), BigInt("080", 16), false)
      readWrite(apb, BigInt(3), BigInt("00010000", 16), false)
      readWrite(apb, BigInt(4), BigInt("080", 16), false)
      readWrite(apb, BigInt(5), BigInt("00F0700", 16))
      sleep(1)

      fork {
        dut.io.input.ar.valid #= true
        dut.io.output.ar.ready #= true

        dut.io.input.ar.id #= BigInt("F0", 16)
        dut.io.input.ar.addr #= BigInt("08000000000000", 16)

        dut.clockDomain.waitSampling(2)
        sleep(1)
        assert(dut.io.input.ar.ready.toBoolean == true)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        dut.io.input.ar.id #= BigInt("F0", 16)
        dut.io.input.ar.addr #= BigInt("0800000FFFF000", 16)
        dut.clockDomain.waitSampling(2)
        sleep(1)
        assert(dut.io.input.ar.ready.toBoolean == true)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        dut.io.input.ar.id #= BigInt("F0", 16)
        dut.io.input.ar.addr #= BigInt("08000010000000", 16)
        dut.clockDomain.waitSampling(2)
        sleep(1)
        assert(dut.io.input.ar.ready.toBoolean == true)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        dut.io.input.ar.id #= BigInt("F0", 16)
        dut.io.input.ar.addr #= BigInt("07FFFFFFFFF000", 16)
        dut.clockDomain.waitSampling(3)
        sleep(1)
        dut.io.input.ar.valid #= false
      }

      fork {
        dut.clockDomain.waitSampling(10)
        sleep(1)
        assert(dut.io.input.r.valid.toBoolean == false)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(dut.io.input.r.id.toBigInt == BigInt("1100011111" + "0000", 2))
        assert(dut.io.input.r.user.toBigInt == BigInt("0000001111", 2))
        assert(dut.io.input.r.resp.toBigInt == BigInt("11", 2))
        assert(dut.io.input.r.valid.toBoolean == true)
        dut.io.input.r.ready #= true
        dut.clockDomain.waitSampling(3)
        sleep(1)
        assert(dut.io.input.r.id.toBigInt == BigInt("1100011111" + "0000", 2))
        assert(dut.io.input.r.user.toBigInt == BigInt("0000001111", 2))
        assert(dut.io.input.r.resp.toBigInt == BigInt("11", 2))
        assert(dut.io.input.r.valid.toBoolean == true)
        dut.clockDomain.waitSampling(5)
      }

      dut.clockDomain.waitSampling(30)
    }

    compiled.doSim("Read check permission") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      dut.io.input.ar.valid #= false
      dut.io.output.ar.ready #= false
      dut.io.input.r.ready #= false
      dut.io.output.r.valid #= false
      dut.io.output.r.last #= false
      dut.clockDomain.waitSampling(5)

      readWrite(apb, BigInt(0), BigInt("1100011111" + "00010000" + "0" * 8, 2), false)
      readWrite(apb, BigInt(1), BigInt("00000000", 16), false)
      readWrite(apb, BigInt(2), BigInt("080", 16), false)
      readWrite(apb, BigInt(3), BigInt("00010000", 16), false)
      readWrite(apb, BigInt(4), BigInt("080", 16), false)
      readWrite(apb, BigInt(5), BigInt("00F0300", 16))
      sleep(1)

      fork {
        dut.io.input.ar.valid #= true
        dut.io.output.ar.ready #= true

        dut.io.input.ar.id #= BigInt("F0", 16)
        dut.io.input.ar.addr #= BigInt("08000000000000", 16)

        dut.clockDomain.waitSampling(2)
        sleep(1)
        assert(dut.io.input.ar.ready.toBoolean == true)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        dut.io.input.ar.id #= BigInt("F0", 16)
        dut.io.input.ar.addr #= BigInt("0800000FFFF000", 16)
        dut.clockDomain.waitSampling(2)
        sleep(1)
        assert(dut.io.input.ar.ready.toBoolean == true)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        dut.io.input.ar.id #= BigInt("F0", 16)
        dut.io.input.ar.addr #= BigInt("08000010000000", 16)
        dut.clockDomain.waitSampling(2)
        sleep(1)
        assert(dut.io.input.ar.ready.toBoolean == true)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        dut.io.input.ar.id #= BigInt("F0", 16)
        dut.io.input.ar.addr #= BigInt("07FFFFFFFFF000", 16)
        dut.clockDomain.waitSampling(3)
        sleep(1)
        dut.io.input.ar.valid #= false
      }

      fork {
        dut.clockDomain.waitSampling(4)
        sleep(1)
        assert(dut.io.input.r.valid.toBoolean == false)
        for (index <- 0 until 4) {
          dut.clockDomain.waitSampling(1)
          sleep(1)
          assert(dut.io.input.r.id.toBigInt == BigInt("1100011111" + "0000", 2))
          assert(dut.io.input.r.user.toBigInt == BigInt("0000001111", 2))
          assert(dut.io.input.r.resp.toBigInt == BigInt("11", 2))
          assert(dut.io.input.r.valid.toBoolean == true)
          dut.io.input.r.ready #= true
          dut.clockDomain.waitSampling(2)
          sleep(1)
        }
        for (index <- 0 until 10) {
          dut.clockDomain.waitSampling(1)
          sleep(1)
          assert(dut.io.input.r.valid.toBoolean == false)
        }
      }

      dut.clockDomain.waitSampling(30)
    }

    compiled.doSim("Write approved address") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      dut.io.input.aw.valid #= false
      dut.io.output.aw.ready #= false
      dut.io.input.w.valid #= false
      dut.io.output.w.ready #= false
      dut.io.output.b.valid #= false
      dut.io.input.b.ready #= false
      dut.clockDomain.waitSampling(5)

      readWrite(apb, BigInt(0), BigInt("1100011111" + "00010000" + "0" * 8, 2), false)
      readWrite(apb, BigInt(1), BigInt("00000000", 16), false)
      readWrite(apb, BigInt(2), BigInt("080", 16), false)
      readWrite(apb, BigInt(3), BigInt("00010000", 16), false)
      readWrite(apb, BigInt(4), BigInt("080", 16), false)
      readWrite(apb, BigInt(5), BigInt("00F0700", 16))
      sleep(1)

      fork {
        dut.io.input.w.valid #= true
        dut.io.input.w.data #= BigInt("1" * 128, 2)
        dut.io.input.w.last #= false

        sleep(1)
        assert(dut.io.output.w.valid.toBoolean == false)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(dut.io.output.w.valid.toBoolean == false)
        dut.clockDomain.waitSampling(1)
        sleep(1)

        dut.io.input.aw.valid #= true
        dut.io.input.aw.id #= BigInt("F0", 16)
        dut.io.input.aw.addr #= BigInt("08000000000000", 16)
        dut.clockDomain.waitSampling(2)
        sleep(1)
        assert(dut.io.output.aw.valid.toBoolean == true)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(dut.io.output.aw.valid.toBoolean == true)
        dut.io.output.aw.ready #= true
        dut.clockDomain.waitSampling(1)
        sleep(1)
        dut.io.input.aw.valid #= false
        dut.io.output.aw.ready #= false
        assert(dut.io.output.aw.valid.toBoolean == false)

        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(dut.io.output.w.valid.toBoolean == false)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(dut.io.output.w.valid.toBoolean == true)
        dut.clockDomain.waitSampling(3)
        sleep(1)
        assert(dut.io.input.w.ready.toBoolean == false)
        dut.io.output.w.ready #= true
        sleep(1)
        assert(dut.io.input.w.ready.toBoolean == true)
      }

      dut.clockDomain.waitSampling(30)
    }

    compiled.doSim("Write blocked address") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)

      dut.io.input.aw.valid #= false
      dut.io.output.aw.ready #= false
      dut.io.input.w.valid #= false
      dut.io.output.w.ready #= false
      dut.io.output.b.valid #= false
      dut.io.input.b.ready #= false
      dut.clockDomain.waitSampling(5)

      readWrite(apb, BigInt(0), BigInt("1100011111" + "00010000" + "0" * 8, 2), false)
      readWrite(apb, BigInt(1), BigInt("00000000", 16), false)
      readWrite(apb, BigInt(2), BigInt("080", 16), false)
      readWrite(apb, BigInt(3), BigInt("00010000", 16), false)
      readWrite(apb, BigInt(4), BigInt("080", 16), false)
      readWrite(apb, BigInt(5), BigInt("00F0700", 16))
      sleep(1)

      fork {
        dut.io.input.w.valid #= true
        dut.io.input.w.data #= BigInt("1" * 128, 2)
        dut.io.input.w.last #= false

        sleep(1)
        assert(dut.io.output.w.valid.toBoolean == false)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(dut.io.output.w.valid.toBoolean == false)
        dut.clockDomain.waitSampling(1)
        sleep(1)

        dut.io.input.aw.valid #= true
        dut.io.input.aw.id #= BigInt("F0", 16)
        dut.io.input.aw.addr #= BigInt("09000000000000", 16)
        dut.clockDomain.waitSampling(2)
        sleep(1)
        assert(dut.io.output.aw.valid.toBoolean == false)
        assert(dut.io.input.aw.ready.toBoolean == true)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        dut.io.input.aw.valid #= false
        assert(dut.io.output.aw.valid.toBoolean == false)
        assert(dut.io.input.aw.ready.toBoolean == false)

        dut.clockDomain.waitSampling(2)
        sleep(1)

        for (index <- 0 until 5) {
          assert(dut.io.output.w.valid.toBoolean == false)
          assert(dut.io.input.w.ready.toBoolean == true)
          dut.clockDomain.waitSampling(1)
          sleep(1)
        }

        dut.io.input.w.last #= true
        assert(dut.io.output.w.valid.toBoolean == false)
        assert(dut.io.input.w.ready.toBoolean == true)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        dut.io.input.w.last #= false
        assert(dut.io.output.w.valid.toBoolean == false)
        assert(dut.io.input.w.ready.toBoolean == false)
        dut.clockDomain.waitSampling(1)
        sleep(1)
      }

      fork {
        dut.clockDomain.waitSampling(6)
        sleep(1)
        assert(dut.io.input.b.valid.toBoolean == false)
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(dut.io.input.b.id.toBigInt == BigInt("1100011111" + "0000", 2))
        assert(dut.io.input.b.user.toBigInt == BigInt("0000001111", 2))
        assert(dut.io.input.b.resp.toBigInt == BigInt("11", 2))
        assert(dut.io.input.b.valid.toBoolean == true)
        dut.io.input.b.ready #= true
        dut.clockDomain.waitSampling(1)
        sleep(1)
        assert(dut.io.input.b.valid.toBoolean == false)
        dut.io.input.b.ready #= false
      }

      dut.clockDomain.waitSampling(30)
    }
  }
}
