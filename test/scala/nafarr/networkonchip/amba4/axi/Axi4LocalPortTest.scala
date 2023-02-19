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

class Axi4LocalPortTest extends AnyFunSuite {

  test("Axi4LocalRouter / Compile: extenstion -, translation -") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4LocalPort(axi4Config.noc, axi4Config.core, apb3Config, false, false)
      dut
    }
    compiled.doSim("blocked value") { dut => dut.clockDomain.forkStimulus(10) }
  }
  test("Axi4LocalRouter / Compile: extenstion +, translation -") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4LocalPort(axi4Config.noc, axi4Config.core, apb3Config, true, false)
      dut
    }
    compiled.doSim("blocked value") { dut => dut.clockDomain.forkStimulus(10) }
  }
  test("Axi4LocalRouter / Compile: extenstion -, translation +") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4LocalPort(axi4Config.noc, axi4Config.core, apb3Config, false, true)
      dut
    }
    compiled.doSim("blocked value") { dut => dut.clockDomain.forkStimulus(10) }
  }
  test("Axi4LocalRouter / Compile: extenstion +, translation +") {
    val compiled = SimConfig.withWave.compile {
      val dut = Axi4LocalPort(axi4Config.noc, axi4Config.core, apb3Config, true, true)
      dut
    }
    compiled.doSim("blocked value") { dut => dut.clockDomain.forkStimulus(10) }
  }
}
