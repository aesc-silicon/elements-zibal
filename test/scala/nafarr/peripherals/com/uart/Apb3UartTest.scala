package nafarr.peripherals.com.uart

import org.scalatest.funsuite.AnyFunSuite

import spinal.sim._
import spinal.core._
import spinal.core.sim._
import nafarr.CheckTester._
import spinal.lib.bus.amba3.apb.sim.Apb3Driver


class Apb3UartTest extends AnyFunSuite {
  test("basic") {
    val compiled = SimConfig.withWave.compile {
      val cd = ClockDomain.current.copy(frequency = FixedFrequency(100 MHz))
      val area = new ClockingArea(cd) {
        val dut = Apb3Uart(UartCtrl.Parameter.default)
      }
      area.dut
    }
    compiled.doSim("testIO") { dut =>
      dut.clockDomain.forkStimulus(10 * 1000)
      fork {
        dut.clockDomain.fallingEdge()
        sleep(10 * 1000)
        while (true) {
          dut.clockDomain.clockToggle()
          sleep(5 * 1000)
        }
      }

      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.io.uart.rxd #= true
      dut.io.uart.cts #= false

      dut.clockDomain.assertReset()
      sleep(100 * 1000)
      dut.clockDomain.deassertReset()

      /* Init IP-Core */
      apb.write(BigInt("08", 16), BigInt("0000006B", 16))
      apb.write(BigInt("0C", 16), BigInt("00000007", 16))

      val receive = UartEncoder(dut.io.uart.rxd, 8640, BigInt("47", 16))
      receive.join()
      assert(
        apb.read(BigInt("00", 16)) == BigInt("00010047", 16),
        "Doesn't received 0x47/'G'"
      )

      /* Transmit 'G' */
      apb.write(BigInt("00", 16), BigInt("00000047", 16))
      val transmit = UartDecoder(dut.io.uart.txd, 8640, BigInt("47", 16))
      transmit.join()

    }
    compiled.doSim("testIRQ") { dut =>
      dut.clockDomain.forkStimulus(10 * 1000)
      fork {
        dut.clockDomain.fallingEdge()
        sleep(10 * 1000)
        while (true) {
          dut.clockDomain.clockToggle()
          sleep(5 * 1000)
        }
      }

      val apb = new Apb3Driver(dut.io.bus, dut.clockDomain)
      dut.io.uart.rxd #= true
      dut.io.uart.cts #= false

      dut.clockDomain.assertReset()
      sleep(100 * 1000)
      dut.clockDomain.deassertReset()

      /* Init IP-Core */
      apb.write(BigInt("08", 16), BigInt("0000006B", 16))
      apb.write(BigInt("0C", 16), BigInt("00000007", 16))

      apb.write(BigInt("10", 16), BigInt("00000002", 16))
      apb.write(BigInt("14", 16), BigInt("00000002", 16))

      val receive = UartEncoder(dut.io.uart.rxd, 8640, BigInt("47", 16))
      receive.join()
      assert(
        apb.read(BigInt("00", 16)) == BigInt("00010047", 16),
        "Doesn't received 0x47/'G'"
      )
      assert(
        apb.read(BigInt("14", 16)) == BigInt("00000002", 16),
        "RX interrupt isn't pending"
      )
      assert(dut.io.interrupt.toBoolean == true, "UART interrupt isn't pending")
      apb.write(BigInt("14", 16), BigInt("00000000", 16))
      apb.write(BigInt("10", 16), BigInt("00000002", 16))
      apb.write(BigInt("14", 16), BigInt("00000002", 16))
      assert(dut.io.interrupt.toBoolean == false, "UART interrupt isn't pending")

    }
  }
}
