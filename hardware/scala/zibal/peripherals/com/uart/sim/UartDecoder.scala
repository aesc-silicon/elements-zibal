package zibal.peripherals.com.uart

import spinal.core.sim._
import spinal.core.{Bool, assert}
import spinal.sim._

object UartDecoder {
  def apply(uartPin: Bool, baudPeriod: Long, compare: BigInt) = fork{
    sleep(1) //Wait boot signals propagation
    waitUntil(uartPin.toBoolean == true)

    waitUntil(uartPin.toBoolean == false)
    sleep(baudPeriod/2)

    assert(uartPin.toBoolean == false, "UART frame error on start bit")
    sleep(baudPeriod)

    var buffer = 0
    (0 to 7).foreach{ bitId =>
      if(uartPin.toBoolean)
        buffer |= 1 << bitId
      sleep(baudPeriod)
    }

    assert(uartPin.toBoolean == true, "UART frame error after stop bit")
    assert(buffer == compare, s"Transmitted ${buffer} but expected ${compare}")
    println(s"Transmitted ${buffer.toChar}")
  }
}
