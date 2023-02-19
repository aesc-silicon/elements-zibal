package nafarr.peripherals.com.chip2chip

import org.scalatest.funsuite.AnyFunSuite
import scala.collection.mutable.ListBuffer

import spinal.sim._
import spinal.core._
import spinal.core.sim._
import spinal.lib._
import spinal.lib.bus.misc.SizeMapping
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba4.axi.sim._
import nafarr.CheckTester._

class Axi4InterfaceTest extends AnyFunSuite {

  test("Interface send out") {
    val compiled = SimConfig.withWave.compile {
      val config = Axi4Config(64, 128, 14)
      case class Chip2Chip(config: Axi4Config) extends Component {
        val io = new Bundle {
          val sender = new Bundle {
            val axiIn = slave(Axi4(config))
            val axiOut = master(Axi4(config))
          }
          val receiver = new Bundle {
            val axiIn = slave(Axi4(config))
            val axiOut = master(Axi4(config))
          }
        }

        val sender = Interface.Axi4Interface(config)
        sender.io.axiIn <> io.sender.axiIn
        io.sender.axiOut <> sender.io.axiOut
        val receiver = Interface.Axi4Interface(config)
        receiver.io.axiIn <> io.receiver.axiIn
        io.receiver.axiOut <> receiver.io.axiOut

        sender.io.txPhy(0) <> receiver.io.rxPhy(0)
        receiver.io.txPhy(0) <> sender.io.rxPhy(0)
      }

      Chip2Chip(config)
    }
    compiled.doSim("AXI4AR") { dut =>
      dut.sender.clockDomain.forkStimulus(period = 10)

      dut.io.sender.axiIn.aw.valid #= false
      dut.io.sender.axiIn.ar.valid #= false
      dut.io.sender.axiIn.w.valid #= false
      dut.io.sender.axiOut.r.valid #= false
      dut.io.sender.axiOut.b.valid #= false
      dut.io.sender.axiOut.aw.ready #= false
      dut.io.sender.axiOut.ar.ready #= false
      dut.io.sender.axiOut.w.ready #= false
      dut.io.sender.axiIn.r.ready #= false
      dut.io.sender.axiIn.b.ready #= false

      dut.io.receiver.axiIn.aw.valid #= false
      dut.io.receiver.axiIn.ar.valid #= false
      dut.io.receiver.axiIn.w.valid #= false
      dut.io.receiver.axiOut.r.valid #= false
      dut.io.receiver.axiOut.b.valid #= false
      dut.io.receiver.axiOut.aw.ready #= false
      dut.io.receiver.axiOut.ar.ready #= false
      dut.io.receiver.axiOut.w.ready #= false
      dut.io.receiver.axiIn.r.ready #= false
      dut.io.receiver.axiIn.b.ready #= false

      dut.sender.clockDomain.waitSampling(5)
      dut.io.sender.axiIn.ar.valid #= true
      dut.io.sender.axiIn.ar.addr #= BigInt("F" * 16, 16)
      dut.io.sender.axiIn.ar.id #= BigInt(0)
      dut.io.sender.axiIn.ar.region #= BigInt(0)
      dut.io.sender.axiIn.ar.len #= BigInt(3)
      dut.io.sender.axiIn.ar.size #= BigInt(5)
      dut.io.sender.axiIn.ar.burst #= BigInt(0)
      dut.io.sender.axiIn.ar.lock #= BigInt(0)
      dut.io.sender.axiIn.ar.cache #= BigInt(0)
      dut.io.sender.axiIn.ar.qos #= BigInt(0)
      dut.io.sender.axiIn.ar.prot #= BigInt(0)

      dut.sender.clockDomain.waitSampling(600)
      sleep(1)
      assert(dut.io.receiver.axiOut.ar.valid.toBoolean == true)
      assert(dut.io.receiver.axiOut.ar.addr.toBigInt == BigInt("F" * 16, 16))
      assert(dut.io.receiver.axiOut.ar.id.toBigInt == BigInt(0))
      assert(dut.io.receiver.axiOut.ar.region.toBigInt == BigInt(0))
      assert(dut.io.receiver.axiOut.ar.len.toBigInt == BigInt(3))
      assert(dut.io.receiver.axiOut.ar.size.toBigInt == BigInt(5))
      assert(dut.io.receiver.axiOut.ar.burst.toBigInt == BigInt(0))
      assert(dut.io.receiver.axiOut.ar.lock.toBigInt == BigInt(0))
      assert(dut.io.receiver.axiOut.ar.cache.toBigInt == BigInt(0))
      assert(dut.io.receiver.axiOut.ar.qos.toBigInt == BigInt(0))
      assert(dut.io.receiver.axiOut.ar.prot.toBigInt == BigInt(0))

      dut.sender.clockDomain.waitSampling(100)
    }

    compiled.doSim("AXI4AW") { dut =>
      dut.sender.clockDomain.forkStimulus(period = 10)

      dut.io.sender.axiIn.aw.valid #= false
      dut.io.sender.axiIn.ar.valid #= false
      dut.io.sender.axiIn.w.valid #= false
      dut.io.sender.axiOut.r.valid #= false
      dut.io.sender.axiOut.b.valid #= false
      dut.io.sender.axiOut.aw.ready #= false
      dut.io.sender.axiOut.ar.ready #= false
      dut.io.sender.axiOut.w.ready #= false
      dut.io.sender.axiIn.r.ready #= false
      dut.io.sender.axiIn.b.ready #= false

      dut.io.receiver.axiIn.aw.valid #= false
      dut.io.receiver.axiIn.ar.valid #= false
      dut.io.receiver.axiIn.w.valid #= false
      dut.io.receiver.axiOut.r.valid #= false
      dut.io.receiver.axiOut.b.valid #= false
      dut.io.receiver.axiOut.aw.ready #= false
      dut.io.receiver.axiOut.ar.ready #= false
      dut.io.receiver.axiOut.w.ready #= false
      dut.io.receiver.axiIn.r.ready #= false
      dut.io.receiver.axiIn.b.ready #= false

      dut.sender.clockDomain.waitSampling(5)
      dut.io.sender.axiIn.aw.valid #= true
      dut.io.sender.axiIn.aw.addr #= BigInt("F" * 16, 16)
      dut.io.sender.axiIn.aw.id #= BigInt(0)
      dut.io.sender.axiIn.aw.region #= BigInt(0)
      dut.io.sender.axiIn.aw.len #= BigInt(3)
      dut.io.sender.axiIn.aw.size #= BigInt(5)
      dut.io.sender.axiIn.aw.burst #= BigInt(0)
      dut.io.sender.axiIn.aw.lock #= BigInt(0)
      dut.io.sender.axiIn.aw.cache #= BigInt(0)
      dut.io.sender.axiIn.aw.qos #= BigInt(0)
      dut.io.sender.axiIn.aw.prot #= BigInt(0)

      dut.sender.clockDomain.waitSampling(600)
      sleep(1)
      assert(dut.io.receiver.axiOut.aw.valid.toBoolean == true)
      assert(dut.io.receiver.axiOut.aw.addr.toBigInt == BigInt("F" * 16, 16))
      assert(dut.io.receiver.axiOut.aw.id.toBigInt == BigInt(0))
      assert(dut.io.receiver.axiOut.aw.region.toBigInt == BigInt(0))
      assert(dut.io.receiver.axiOut.aw.len.toBigInt == BigInt(3))
      assert(dut.io.receiver.axiOut.aw.size.toBigInt == BigInt(5))
      assert(dut.io.receiver.axiOut.aw.burst.toBigInt == BigInt(0))
      assert(dut.io.receiver.axiOut.aw.lock.toBigInt == BigInt(0))
      assert(dut.io.receiver.axiOut.aw.cache.toBigInt == BigInt(0))
      assert(dut.io.receiver.axiOut.aw.qos.toBigInt == BigInt(0))
      assert(dut.io.receiver.axiOut.aw.prot.toBigInt == BigInt(0))

      dut.sender.clockDomain.waitSampling(100)
    }

    compiled.doSim("AXI4W") { dut =>
      dut.sender.clockDomain.forkStimulus(period = 10)

      dut.io.sender.axiIn.aw.valid #= false
      dut.io.sender.axiIn.ar.valid #= false
      dut.io.sender.axiIn.w.valid #= false
      dut.io.sender.axiOut.r.valid #= false
      dut.io.sender.axiOut.b.valid #= false
      dut.io.sender.axiOut.aw.ready #= false
      dut.io.sender.axiOut.ar.ready #= false
      dut.io.sender.axiOut.w.ready #= false
      dut.io.sender.axiIn.r.ready #= false
      dut.io.sender.axiIn.b.ready #= false

      dut.io.receiver.axiIn.aw.valid #= false
      dut.io.receiver.axiIn.ar.valid #= false
      dut.io.receiver.axiIn.w.valid #= false
      dut.io.receiver.axiOut.r.valid #= false
      dut.io.receiver.axiOut.b.valid #= false
      dut.io.receiver.axiOut.aw.ready #= false
      dut.io.receiver.axiOut.ar.ready #= false
      dut.io.receiver.axiOut.w.ready #= false
      dut.io.receiver.axiIn.r.ready #= false
      dut.io.receiver.axiIn.b.ready #= false

      dut.sender.clockDomain.waitSampling(5)
      dut.io.sender.axiIn.w.valid #= true
      dut.io.sender.axiIn.w.data #= BigInt("F" * 32, 16)
      dut.io.sender.axiIn.w.strb #= BigInt(0)
      dut.io.sender.axiIn.w.last #= false

      dut.sender.clockDomain.waitSampling(600)
      sleep(1)
      assert(dut.io.receiver.axiOut.w.valid.toBoolean == true)
      assert(dut.io.receiver.axiOut.w.data.toBigInt == BigInt("F" * 32, 16))
      assert(dut.io.receiver.axiOut.w.strb.toBigInt == BigInt(0))
      assert(dut.io.receiver.axiOut.w.last.toBoolean == false)

      dut.sender.clockDomain.waitSampling(100)
    }

    compiled.doSim("AXI4R") { dut =>
      dut.sender.clockDomain.forkStimulus(period = 10)

      dut.io.sender.axiIn.aw.valid #= false
      dut.io.sender.axiIn.ar.valid #= false
      dut.io.sender.axiIn.w.valid #= false
      dut.io.sender.axiOut.r.valid #= false
      dut.io.sender.axiOut.b.valid #= false
      dut.io.sender.axiOut.aw.ready #= false
      dut.io.sender.axiOut.ar.ready #= false
      dut.io.sender.axiOut.w.ready #= false
      dut.io.sender.axiIn.r.ready #= false
      dut.io.sender.axiIn.b.ready #= false

      dut.io.receiver.axiIn.aw.valid #= false
      dut.io.receiver.axiIn.ar.valid #= false
      dut.io.receiver.axiIn.w.valid #= false
      dut.io.receiver.axiOut.r.valid #= false
      dut.io.receiver.axiOut.b.valid #= false
      dut.io.receiver.axiOut.aw.ready #= false
      dut.io.receiver.axiOut.ar.ready #= false
      dut.io.receiver.axiOut.w.ready #= false
      dut.io.receiver.axiIn.r.ready #= false
      dut.io.receiver.axiIn.b.ready #= false

      dut.sender.clockDomain.waitSampling(5)
      dut.io.sender.axiOut.r.valid #= true
      dut.io.sender.axiOut.r.data #= BigInt("F" * 32, 16)
      dut.io.sender.axiOut.r.resp #= BigInt(0)
      dut.io.sender.axiOut.r.last #= false

      dut.sender.clockDomain.waitSampling(600)
      sleep(1)
      assert(dut.io.receiver.axiIn.r.valid.toBoolean == true)
      assert(dut.io.receiver.axiIn.r.data.toBigInt == BigInt("F" * 32, 16))
      assert(dut.io.receiver.axiIn.r.resp.toBigInt == BigInt(0))
      assert(dut.io.receiver.axiIn.r.last.toBoolean == false)

      dut.sender.clockDomain.waitSampling(100)
    }

    compiled.doSim("AXI4B") { dut =>
      dut.sender.clockDomain.forkStimulus(period = 10)

      dut.io.sender.axiIn.aw.valid #= false
      dut.io.sender.axiIn.ar.valid #= false
      dut.io.sender.axiIn.w.valid #= false
      dut.io.sender.axiOut.r.valid #= false
      dut.io.sender.axiOut.b.valid #= false
      dut.io.sender.axiOut.aw.ready #= false
      dut.io.sender.axiOut.ar.ready #= false
      dut.io.sender.axiOut.w.ready #= false
      dut.io.sender.axiIn.r.ready #= false
      dut.io.sender.axiIn.b.ready #= false

      dut.io.receiver.axiIn.aw.valid #= false
      dut.io.receiver.axiIn.ar.valid #= false
      dut.io.receiver.axiIn.w.valid #= false
      dut.io.receiver.axiOut.r.valid #= false
      dut.io.receiver.axiOut.b.valid #= false
      dut.io.receiver.axiOut.aw.ready #= false
      dut.io.receiver.axiOut.ar.ready #= false
      dut.io.receiver.axiOut.w.ready #= false
      dut.io.receiver.axiIn.r.ready #= false
      dut.io.receiver.axiIn.b.ready #= false

      dut.sender.clockDomain.waitSampling(5)
      dut.io.sender.axiOut.b.valid #= true
      dut.io.sender.axiOut.b.id #= BigInt(0)
      dut.io.sender.axiOut.b.resp #= BigInt(0)

      dut.sender.clockDomain.waitSampling(600)
      sleep(1)
      assert(dut.io.receiver.axiIn.b.valid.toBoolean == true)
      assert(dut.io.receiver.axiIn.b.id.toBigInt == BigInt(0))
      assert(dut.io.receiver.axiIn.b.resp.toBigInt == BigInt(0))

      dut.sender.clockDomain.waitSampling(100)
    }
  }

  case class Chip2ChipPerf(config: Axi4Config, phyCount: Int = 1) extends Component {
    val io = new Bundle {
      val sender = new Bundle {
        val arValid = in(Bool())
        val awValid = in(Bool())
        val wValid = in(Bool())
        val rValid = in(Bool())
        val bValid = in(Bool())
      }
      val receiver = new Bundle {
        val axiIn = slave(Axi4(config))
        val axiOut = master(Axi4(config))
      }
    }

    val sender = Interface.Axi4Interface(config, phyCount, outputDepth=16, inputDepth=16, transactionsDepth=16)
    sender.io.axiIn.ar.valid := io.sender.arValid
    sender.io.axiIn.aw.valid := io.sender.awValid
    sender.io.axiIn.w.valid := io.sender.wValid
    sender.io.axiOut.r.valid := io.sender.rValid
    sender.io.axiOut.b.valid := io.sender.bValid

    sender.io.axiIn.ar.id := U(0, config.idWidth bits)
    if (config.useRegion) {
      sender.io.axiIn.ar.region := B(0, 4 bits)
    }
    if (config.useLen) {
      sender.io.axiIn.ar.len := U(0, 8 bits)
    }
    if (config.useSize) {
      sender.io.axiIn.ar.size := U(0, 3 bits)
    }
    if (config.useBurst) {
      sender.io.axiIn.ar.burst := B(0, 2 bits)
    }
    if (config.useLock) {
      sender.io.axiIn.ar.lock := B(0, 1 bits)
    }
    if (config.useCache) {
      sender.io.axiIn.ar.cache := B(0, 4 bits)
    }
    if (config.useQos) {
      sender.io.axiIn.ar.qos := B(0, 4 bits)
    }
    if (config.arUserWidth > 0) {
      sender.io.axiIn.ar.user := B(0, config.arUserWidth bits)
    }
    if (config.useProt) {
      sender.io.axiIn.ar.prot := B(0, 3 bits)
    }

    sender.io.axiIn.aw.id := U(0, config.idWidth bits)
    if (config.useRegion) {
      sender.io.axiIn.aw.region := B(0, 4 bits)
    }
    if (config.useLen) {
      sender.io.axiIn.aw.len := U(0, 8 bits)
    }
    if (config.useSize) {
      sender.io.axiIn.aw.size := U(0, 3 bits)
    }
    if (config.useBurst) {
      sender.io.axiIn.aw.burst := B(0, 2 bits)
    }
    if (config.useLock) {
      sender.io.axiIn.aw.lock := B(0, 1 bits)
    }
    if (config.useCache) {
      sender.io.axiIn.aw.cache := B(0, 4 bits)
    }
    if (config.useQos) {
      sender.io.axiIn.aw.qos := B(0, 4 bits)
    }
    if (config.awUserWidth > 0) {
      sender.io.axiIn.aw.user := B(0, config.awUserWidth bits)
    }
    if (config.useProt) {
      sender.io.axiIn.aw.prot := B(0, 3 bits)
    }

    if (config.useStrb) {
      sender.io.axiIn.w.strb := B(0, config.bytePerWord bits)
    }
    if (config.useWUser) {
      sender.io.axiIn.w.user := B(0, config.wUserWidth bits)
    }
    if (config.useLast) {
      sender.io.axiIn.w.last := True
    }

    sender.io.axiIn.r.ready := False
    sender.io.axiOut.aw.ready := False
    sender.io.axiOut.ar.ready := False
    sender.io.axiOut.w.ready := False

    if (config.useId) {
      sender.io.axiOut.r.id := U(0, config.idWidth bits)
    }
    if (config.useResp) {
      sender.io.axiOut.r.resp := B(0, 2 bits)
    }
    sender.io.axiOut.r.last := True
    if (config.useRUser) {
      sender.io.axiOut.r.user := B(0, config.rUserWidth bits)
    }

    sender.io.axiIn.b.ready := False
    if (config.useResp) {
      sender.io.axiOut.b.resp := B(0, 2 bits)
    }
    if (config.useBUser) {
      sender.io.axiOut.b.user := B(0, config.bUserWidth bits)
    }


    val receiver = Interface.Axi4Interface(config, phyCount, outputDepth=16, inputDepth=16, transactionsDepth=16)
    receiver.io.axiIn <> io.receiver.axiIn
    io.receiver.axiOut <> receiver.io.axiOut

    for (index <- 0 until phyCount) {
      sender.io.txPhy(index) <> receiver.io.rxPhy(index)
      receiver.io.txPhy(index) <> sender.io.rxPhy(index)
    }

    val ar = new Area {
      val counter = RegInit(U(0, 32 bits))
      when (sender.io.axiIn.ar.valid && sender.io.axiIn.ar.ready) {
        counter := counter + 1
      }
      sender.io.axiIn.ar.addr := counter.resized
    }
    val aw = new Area {
      val counter = RegInit(U(0, 32 bits))
      when (sender.io.axiIn.aw.valid && sender.io.axiIn.aw.ready) {
        counter := counter + 1
      }
      sender.io.axiIn.aw.addr := counter.resized
    }
    val w = new Area {
      val counter = RegInit(U(0, 32 bits))
      when (sender.io.axiIn.w.valid && sender.io.axiIn.w.ready) {
        counter := counter + 1
      }
      sender.io.axiIn.w.data := counter.asBits.resized
    }
    val r = new Area {
      val counter = RegInit(U(0, 32 bits))
      when (sender.io.axiOut.r.valid && sender.io.axiOut.r.ready) {
        counter := counter + 1
      }
      sender.io.axiOut.r.data := counter.asBits.resized
    }
    val b = new Area {
      val counter = RegInit(U(0, 32 bits))
      when (sender.io.axiOut.b.valid && sender.io.axiOut.b.ready) {
        counter := counter + 1
      }
      sender.io.axiOut.b.id := counter
    }
  }
  def wait(dut: Chip2ChipPerf, cycles: Int): Int = {
    val duration = cycles * 10
    println(s"Duration ${duration} ns / ${duration / 1000} us / ${duration / 1000000} ms")
    dut.sender.clockDomain.waitSampling(cycles)
    duration
  }
  def calc(counts: BigInt, duration: Int): BigInt =  {
    val transactionsPerSec = (counts + 1) * 1000000000 / duration
    println(s"Counted ${counts + 1} transactions")
    println(s"${transactionsPerSec} transactions per second")
    transactionsPerSec
  }
  def init(dut: Chip2ChipPerf) {
    dut.sender.clockDomain.forkStimulus(period = 10)

    dut.io.receiver.axiIn.aw.valid #= false
    dut.io.receiver.axiIn.ar.valid #= false
    dut.io.receiver.axiIn.w.valid #= false
    dut.io.receiver.axiOut.r.valid #= false
    dut.io.receiver.axiOut.b.valid #= false
    dut.io.receiver.axiOut.aw.ready #= false
    dut.io.receiver.axiOut.w.ready #= false
    dut.io.receiver.axiIn.r.ready #= false
    dut.io.receiver.axiIn.b.ready #= false

    dut.io.sender.arValid #= false
    dut.io.sender.awValid #= false
    dut.io.sender.wValid #= false
    dut.io.sender.rValid #= false
    dut.io.sender.bValid #= false
  }

  test("Performance // 1 Link") {
    val compiled = SimConfig.withWave.compile {
      Chip2ChipPerf(Axi4Config(64, 128, 32), 1)
    }

    compiled.doSim("AXI4AR") { dut =>
      init(dut)

      dut.io.sender.arValid #= true
      dut.io.receiver.axiOut.ar.ready #= true

      val duration = wait(dut, 100000)
      calc(dut.io.receiver.axiOut.ar.addr.toBigInt, duration)
    }

    compiled.doSim("AXI4W") { dut =>
      init(dut)

      dut.io.sender.wValid #= true
      dut.io.receiver.axiOut.w.ready #= true

      val duration = wait(dut, 100000)
      calc(dut.io.receiver.axiOut.w.data.toBigInt, duration)
    }

    compiled.doSim("AXI4R") { dut =>
      init(dut)

      dut.io.sender.rValid #= true
      dut.io.receiver.axiIn.r.ready #= true

      val duration = wait(dut, 100000)
      calc(dut.io.receiver.axiIn.r.data.toBigInt, duration)
    }

    compiled.doSim("AXI4B") { dut =>
      init(dut)

      dut.io.sender.bValid #= true
      dut.io.receiver.axiIn.b.ready #= true

      val duration = wait(dut, 100000)
      calc(dut.io.receiver.axiIn.b.id.toBigInt, duration)
    }
  }

  test("Performance // 2 Link") {
    val compiled = SimConfig.withWave.compile {
      Chip2ChipPerf(Axi4Config(64, 128, 32), 2)
    }

    compiled.doSim("AXI4AR") { dut =>
      init(dut)

      dut.io.sender.arValid #= true
      dut.io.receiver.axiOut.ar.ready #= true

      val duration = wait(dut, 100000)
      calc(dut.io.receiver.axiOut.ar.addr.toBigInt, duration)
    }

    compiled.doSim("AXI4W") { dut =>
      init(dut)

      dut.io.sender.wValid #= true
      dut.io.receiver.axiOut.w.ready #= true

      val duration = wait(dut, 100000)
      calc(dut.io.receiver.axiOut.w.data.toBigInt, duration)
    }

    compiled.doSim("AXI4R") { dut =>
      init(dut)

      dut.io.sender.rValid #= true
      dut.io.receiver.axiIn.r.ready #= true

      val duration = wait(dut, 100000)
      calc(dut.io.receiver.axiIn.r.data.toBigInt, duration)
    }

    compiled.doSim("AXI4B") { dut =>
      init(dut)

      dut.io.sender.bValid #= true
      dut.io.receiver.axiIn.b.ready #= true

      val duration = wait(dut, 100000)
      calc(dut.io.receiver.axiIn.b.id.toBigInt, duration)
    }
  }

}
