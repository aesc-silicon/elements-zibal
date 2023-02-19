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

class Axi4BlockageTest extends AnyFunSuite {

  test("Axi4Blockage") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4Blockage(axi4Config.core)
      dut
    }
    compiled.doSim("blocked value") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.io.input.ar.valid #= false
      dut.io.input.aw.valid #= false
      dut.io.input.w.valid #= false

      val blockedBefore = apb.read(BigInt(0))
      assert(blockedBefore == BigInt("1", 2))

      apb.write(BigInt(0), BigInt("1", 2))

      val blockedAfter = apb.read(BigInt(0))
      assert(blockedAfter == BigInt("0", 2))
  }

    compiled.doSim("verify valid blocked") { dut =>
      dut.clockDomain.forkStimulus(10)
      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.io.input.ar.valid #= false
      dut.io.input.aw.valid #= false
      dut.io.input.w.valid #= false
      dut.clockDomain.waitSampling(5)

      assert(dut.io.output.ar.valid.toBoolean == false)
      assert(dut.io.output.aw.valid.toBoolean == false)
      assert(dut.io.output.w.valid.toBoolean == false)

      sleep(1)

      dut.io.input.ar.valid #= true
      dut.io.input.aw.valid #= true
      dut.io.input.w.valid #= true

      sleep(1)

      assert(dut.io.output.ar.valid.toBoolean == false)
      assert(dut.io.output.aw.valid.toBoolean == false)
      assert(dut.io.output.w.valid.toBoolean == false)

      apb.write(BigInt(0), BigInt("1", 2))

      sleep(1)

      assert(dut.io.output.ar.valid.toBoolean == true)
      assert(dut.io.output.aw.valid.toBoolean == true)
      assert(dut.io.output.w.valid.toBoolean == true)
    }
  }
}
