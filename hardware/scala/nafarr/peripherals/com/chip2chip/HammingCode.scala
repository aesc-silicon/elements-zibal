package nafarr.peripherals.com.chip2chip

import spinal.core._
import spinal.lib._

/*
 * [15,11] Hamming code with SECDED ([16,11]) to detect two bit erros and correct one.
 *
 * This class is modified to a reduced input size of 8 bit to fit the chip2chip requirements
 * and an optimized xor tree due 0 constants.
 */
object HammingCode1611 {

  def calculateParity(dw: Bits): Bits = {
    val parity = Bits(4 bits)

    parity(0) := dw(0) ^ dw(1) ^ dw(3) ^ dw(4) ^ dw(6)
    parity(1) := dw(0) ^ dw(2) ^ dw(3) ^ dw(5) ^ dw(6)
    parity(2) := dw(1) ^ dw(2) ^ dw(3) ^ dw(7)
    parity(3) := dw(4) ^ dw(5) ^ dw(6) ^ dw(7)

    parity
  }

  def calculateExtendedParity(dw: Bits, parity: Bits): Bool = {
    return (dw ## parity).xorR
  }

  case class Encoder() extends Component {
    val io = new Bundle {
      val dataword = in Bits (8 bits)
      val codeword = out Bits (13 bits)
    }

    val parity = calculateParity(io.dataword)
    val extendedParity = calculateExtendedParity(io.dataword, parity)

    io.codeword := io.dataword(7 downto 4) ## parity(3) ## io.dataword(3 downto 1) ##
      parity(2) ## io.dataword(0) ## parity(1 downto 0) ## extendedParity
  }

  case class Decoder() extends Component {
    val io = new Bundle {
      val dataword = out Bits (8 bits)
      val codeword = in Bits (13 bits)
      val multiBitError = out(Bool())
    }
    var dataword = io.codeword(12 downto 9) ## io.codeword(7 downto 5) ## io.codeword(3)
    val codewordParity = io.codeword(8) ## io.codeword(4) ## io.codeword(2 downto 1)
    val codewordExtendedParity = io.codeword(0)
    val parity = calculateParity(dataword)
    val extendedParity = calculateExtendedParity(dataword, codewordParity)

    val extendedParityError = codewordExtendedParity =/= extendedParity
    val parityError = codewordParity =/= parity
    val bitPosition = (codewordParity ^ parity)

    def flipBit(d: Bits, position: Int): Bits = {
      val dWidth = widthOf(d) - 1
      if (position == 0) {
        return d(dWidth downto 1) ## !d(0)
      }
      if (position == dWidth) {
        return !d(dWidth) ## d(dWidth - 1 downto 0)
      }
      return d(dWidth downto position + 1) ## !d(position) ## d(position - 1 downto 0)
    }

    when(!extendedParityError && parityError) {
      io.multiBitError := True
    } otherwise {
      io.multiBitError := False
    }

    when(extendedParityError) {
      switch(bitPosition) {
        /* Wrong parity bits. Dataword is correct */
        is(B"0000") { io.dataword := dataword }
        is(B"0001") { io.dataword := dataword }
        is(B"0010") { io.dataword := dataword }
        is(B"0100") { io.dataword := dataword }
        is(B"1000") { io.dataword := dataword }
        /* Flip wrong data word bit */
        is(B"0011") { io.dataword := flipBit(dataword, 0) }
        is(B"0101") { io.dataword := flipBit(dataword, 1) }
        is(B"0110") { io.dataword := flipBit(dataword, 2) }
        is(B"0111") { io.dataword := flipBit(dataword, 3) }
        is(B"1001") { io.dataword := flipBit(dataword, 4) }
        is(B"1010") { io.dataword := flipBit(dataword, 5) }
        is(B"1011") { io.dataword := flipBit(dataword, 6) }
        is(B"1100") { io.dataword := flipBit(dataword, 7) }
        /* Only 8 data bits used. Following statements will never reached.
         * They are kept to avoid latch error
         */
        is(B"1101") { io.dataword := dataword }
        is(B"1110") { io.dataword := dataword }
        is(B"1111") { io.dataword := dataword }
      }
    } otherwise {
      io.dataword := dataword
    }
  }
}
