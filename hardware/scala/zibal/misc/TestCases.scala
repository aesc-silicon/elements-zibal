package zibal.misc

import spinal.core._
import spinal.core.sim._
import spinal.lib._
import scala.util.matching.Regex

object TestCases {
  def apply() = TestCases()

  case class TestCases() {
    def addClock(
      clock: Bool,
      frequency: HertzNumber,
      duration: TimeNumber,
      timeout: Boolean = false
    ) {
      val (clockPeriod, unit) = frequency.toTime.decompose
      require (unit == "ns")
      val clockDuration = (frequency / duration.toHertz).toInt

      println(s"Clock period of ${clockPeriod.toInt} ${unit} for ${clockDuration} cycles")

      SimulationHelper.generateClock(clock, clockPeriod.toInt, clockDuration.toInt,
        timeout=timeout)
    }

    def addClockWithTimeout(clock: Bool, frequency: HertzNumber, duration: TimeNumber) {
      addClock(clock, frequency, duration, true)
    }

    def addReset(reset: Bool, wait: TimeNumber) {
      val waitCycles = (wait.toDouble * 1000000000).toInt
      require(waitCycles > 0)
      SimulationHelper.generateReset(reset, waitCycles)
    }

    def dump(txd: Bool, baudPeriod: Int) {
      SimulationHelper.dumpStdout(txd, baudPeriod)
    }

    def boot(txd: Bool, baudPeriod: Int) {
      dump(txd, baudPeriod)
      SimulationHelper.expectZephyrPrompt(txd, baudPeriod)
    }

    def heartbeat(gpio: Bool) {
      fork {
        SimulationHelper.wait(100 us)
        assert(gpio.toBoolean == false)
        println("Heartbeat LED: OFF")
        SimulationHelper.wait(4 ms)
        assert(gpio.toBoolean == true)
        println("Heartbeat LED: ON")
        SimulationHelper.wait(150 ms)
        assert(gpio.toBoolean == false)
        println("Heartbeat LED: OFF")
        SimulationHelper.wait(50 ms)
        assert(gpio.toBoolean == true)
        println("Heartbeat LED: ON")
        SimulationHelper.wait(150 ms)
        assert(gpio.toBoolean == false)
        println("Heartbeat LED: OFF")
        simSuccess()
      }
    }

    def gpioLoopback(gpio0: Bool, gpio1: Bool) {
      fork {
        SimulationHelper.waitUntilOrFail(gpio0.toBoolean == true, 250 us, 100 ns)
        SimulationHelper.waitUntilOrFail(gpio1.toBoolean == true, 250 us, 100 ns)
        simSuccess
      }
    }

    def uartLoopback(txd: Bool, rxd: Bool, baudPeriod: Int) {
      fork {
        rxd #= true
        // Check at least something is sent
        SimulationHelper.waitUntilOrFail(txd.toBoolean == true, 1 us, 100 ns)
        SimulationHelper.waitUntilOrFail(txd.toBoolean == false, 200 us, 100 ns)
        // Check successful transmission
        val receiveChar = SimulationHelper.uartReceive(txd, baudPeriod)
        assert(receiveChar == 'X')
        // Check loopback
        SimulationHelper.uartTransmit(rxd, baudPeriod, 'G')
        SimulationHelper.waitUntilOrFail(txd.toBoolean == false, 200 us, 100 ns)
        val receiveG = SimulationHelper.uartReceive(txd, baudPeriod)
        assert(receiveG == 'G')
        simSuccess
      }
    }

    def uartIrq(txd: Bool, rxd: Bool, baudPeriod: Int) {
      fork {
        rxd #= true
        // Check at least something is sent
        SimulationHelper.waitUntilOrFail(txd.toBoolean == true, 1 us, 100 ns)
        SimulationHelper.waitUntilOrFail(txd.toBoolean == false, 200 us, 100 ns)
        // Check successful transmission
        val receiveChar = SimulationHelper.uartReceive(txd, baudPeriod)
        assert(receiveChar == 'X')
        // Check loopback
        SimulationHelper.uartTransmit(rxd, baudPeriod, 'G')
        SimulationHelper.waitUntilOrFail(txd.toBoolean == false, 200 us, 100 ns)
        val receiveG = SimulationHelper.uartReceive(txd, baudPeriod)
        assert(receiveG == 'G')
        // IRQ is enabled. Send character. IRQ will send R followed be the sent char (I).
        SimulationHelper.uartTransmit(rxd, baudPeriod, 'I')
        SimulationHelper.waitUntilOrFail(txd.toBoolean == false, 200 us, 100 ns)
        val receiveR = SimulationHelper.uartReceive(txd, baudPeriod)
        assert(receiveR == 'R')
        SimulationHelper.waitUntilOrFail(txd.toBoolean == false, 200 us, 100 ns)
        val receiveI = SimulationHelper.uartReceive(txd, baudPeriod)
        assert(receiveI == 'I')
        simSuccess
      }
    }

    def frequency(frequencyPin: Bool, clock: HertzNumber, txd: Bool, baudPeriod: Int) {

      def decompose(value: BigDecimal): (BigDecimal, String) = value match {
        case d if d > 1.0e18 => (value / 1.0e18, "EHz")
        case d if d > 1.0e15 => (value / 1.0e15, "PHz")
        case d if d > 1.0e12 => (value / 1.0e12, "THz")
        case d if d > 1.0e9  => (value / 1.0e9,  "GHz")
        case d if d > 1.0e6  => (value / 1.0e6,  "MHz")
        case d if d > 1.0e3  => (value / 1.0e3,  "kHz")
        case d if d > 1.0    => (value / 1.0,    "Hz")
        case _               => (value,          "Unknown")
      }

      val (clockPeriod, unit) = clock.toTime.decompose
      require (unit == "ns")
      val clockDuration = (clock / (1 sec).toHertz).toInt

      println(s"Clock period of ${clockPeriod.toInt} ${unit} for ${clockDuration} cycles")

      var stdout = ""
      fork {
        // Wait for reset.
        SimulationHelper.wait(1 us)
        SimulationHelper.generateClock(frequencyPin, clockPeriod.toInt, clockDuration.toInt)

        SimulationHelper.waitUntilOrFail(txd.toBoolean == false, 5 ms, 100 ns)
        while (!stdout.contains("\n")) {
          SimulationHelper.waitUntilOrFail(txd.toBoolean == false, 200 us, 100 ns)
          stdout += SimulationHelper.uartReceive(txd, baudPeriod).toChar
        }
        val frequencyMatch = decompose(clock.toBigDecimal)._1
        // TODO Wait for upstream patch
        //val frequencyMatch = clock.decompose._1
        println(s"Received stdout: ${stdout}")
        f"${frequencyMatch}\\d{6} Hz".r findFirstIn stdout match {
          case Some(_) => simSuccess
          case None => assert(false, s"${frequencyMatch}xxxxxx Hz not found in $stdout")
        }
      }
    }
  }
}
