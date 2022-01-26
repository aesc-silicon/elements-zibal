package zibal.misc

import spinal.core._
import spinal.core.sim._
import spinal.lib._
import scala.util.matching.Regex


object SimulationHelper {

  def generateClock(
    clock: Bool,
    period: Int,
    duration: Int,
    delay: Int = 0,
    timeout: Boolean = false
  ) = {
    fork {
      clock #= true
      sleep(delay)
      sleep(period/2)
      for (_ <- 0 to duration * 2) {
        clock #= !clock.toBoolean
        sleep(period/2)
      }
      if (timeout)
        simFailure("Clock Timeout")
      else
        simSuccess
    }
  }
  def generateReset(reset: Bool, delay: Int) = {
    fork {
      reset #= false
      sleep(delay)
      reset #= true
    }
  }
  def wait(duration: TimeNumber) {
    val durationCycles = (duration.toDouble * 1000000000).toInt
    println(s"Sleep for ${durationCycles} cycles")
    sleep(durationCycles)
  }
  def waitUntilOrFail(cond: => Boolean, duration: TimeNumber, checks: TimeNumber): Boolean = {
    val durationCycles = (duration.toDouble * 1000000000).toInt
    val checkCycles = (checks.toDouble * 1000000000).toInt
    val sleepDuration = (durationCycles / checkCycles).toInt
    println(s"Sleep for ${checkCycles} cycles and retry ${sleepDuration}")
    for (_ <- 0 to sleepDuration) {
      if (cond) {
        return true
      }
      sleep(checkCycles)
    }
    assert(false, s"waitUntil failed because condtition not happend after ${sleepDuration} checks.")
    false
  }
  def uartReceive(rxd: Bool, baudPeriod: Int) = {
    sleep(baudPeriod/2)

    assert(rxd.toBoolean == false)
    sleep(baudPeriod)

    var buffer = 0
    for(bitId <- 0 to 7) {
      if(rxd.toBoolean)
        buffer |= 1 << bitId
      sleep(baudPeriod)
    }
    assert(rxd.toBoolean == true)
    buffer
  }
  def uartTransmit(txd: Bool, baudPeriod: Int, character: Char) = {
    txd #= false
    sleep(baudPeriod)

    for(bitId <- 0 to 7) {
      txd #= ((character >> bitId) & 1) != 0
      sleep(baudPeriod)
    }
    txd #= true
    sleep(baudPeriod)
  }
  def dumpStdout(rxd: Bool, baudPeriod: Int) = {
    fork {
      waitUntil(rxd.toBoolean == true)
      while(true) {
        waitUntil(rxd.toBoolean == false)
        val buffer = uartReceive(rxd, baudPeriod)
        print(buffer.toChar)
      }
    }
  }
  def dumpCharacters(rxd: Bool, baudPeriod: Int) = {
    fork {
      waitUntil(rxd.toBoolean == true)
      while(true) {
        waitUntil(rxd.toBoolean == false)
        val buffer = uartReceive(rxd, baudPeriod)
        println(buffer.toChar)
      }
    }
  }

  def expectZephyrPrompt(rxd: Bool, baudPeriod: Int) = {
    val pattern = "\\*\\*\\* Booting Zephyr OS build (.*)  \\*\\*\\*".r
    var log = ""
    var run = true
    fork {
      waitUntil(rxd.toBoolean == true)

      while(run) {
        waitUntil(rxd.toBoolean == false)
        val buffer = uartReceive(rxd, baudPeriod)
        assert(rxd.toBoolean == true)
        if (buffer.toChar == '\n') {
          assert(pattern.findFirstIn(log) != None)
          run = false
        }
        log = log + buffer.toChar
      }
      simSuccess
    }
  }
}
