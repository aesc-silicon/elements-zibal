package zibal.misc

import spinal.core._
import spinal.core.sim._
import spinal.lib._
import scala.util.matching.Regex


object SimulationHelper {

  def generateClock(clock: Bool, period: Int, duration: Int, delay: Int = 0) = {
    val clockDuration = duration / period
    fork {
      clock #= true
      sleep(delay)
      sleep(period/2)
      for (_ <- 0 to clockDuration * 2) {
        clock #= !clock.toBoolean
        sleep(period/2)
      }
    }
  }
  def generateReset(reset: Bool, period: Int, delayCycles: Int) = {
    fork {
      reset #= false
      sleep(period * delayCycles)
      reset #= true
    }
  }
  def waitUntilOrFail(cond: => Boolean, period: Int, until: Int):Boolean = {
    for (_ <- 0 to until) {
      if (cond) {
        return true
      }
      sleep(period)
    }
    assert(false, s"waitUntil failed because condtition not happend after ${until} cycles.")
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
    fork {
      waitUntil(rxd.toBoolean == true)

      while(true) {
        waitUntil(rxd.toBoolean == false)
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
        if (buffer.toChar == '\n') {
          assert(pattern.findFirstIn(log) != None)
        }
        log = log + buffer.toChar
      }
    }
  }
}
