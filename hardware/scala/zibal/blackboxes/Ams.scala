/*
package zibal.blackboxes

import spinal.core._
import spinal.lib._

class dirom1024x32(wordWidth: Int, wordCount: BigInt) extends BlackBox {
  val io = new Bundle {
    val CS   = in  Bool
    val AD   = in  Bits (log2Up(wordCount) - 2 bit)
    val NRST = in  Bool
    val EN = in  Bool
    val DO = out Bits (wordWidth bit)
  }

  noIoPrefix()
  mapClockDomain(clock=io.CS, reset=io.NRST)
}

class sram2kx8_ctrl(wordWidth: Int, wordCount: BigInt, simpleBusConfig: SimpleBusConfig) extends BlackBox {
  val io = new Bundle {
    val clk   = in  Bool
    val rst_n = in  Bool
    val bus   = slave(SimpleBus(simpleBusConfig))
  }

  noIoPrefix()
  mapClockDomain(clock=io.clk, reset=io.rst_n)
}
*/
