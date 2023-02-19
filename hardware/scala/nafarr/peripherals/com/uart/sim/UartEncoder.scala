package nafarr.peripherals.com.uart

import spinal.core.{Bool, Bits}
import spinal.core.sim._
import spinal.sim._

object UartEncoder {
  def apply(uartPin: Bool, baudPeriod: Long, buffer: BigInt) = fork {
    uartPin #= true

    uartPin #= false
    sleep(baudPeriod * 1000)

    (0 to 7).foreach { bitId =>
      uartPin #= ((buffer >> bitId) & 1) != 0
      sleep(baudPeriod * 1000)
    }

    uartPin #= true
    sleep(baudPeriod * 1000)
  }
}
