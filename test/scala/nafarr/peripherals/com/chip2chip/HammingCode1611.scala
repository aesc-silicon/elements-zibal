package nafarr.peripherals.com.chip2chip

import org.scalatest.funsuite.AnyFunSuite

import spinal.sim._
import spinal.core._
import spinal.core.sim._
import nafarr.CheckTester._

/*
 * See [1] to calculate parity bits
 *
 * Output format: data[7-0]parity[0]parity[5-1]
 *
 * 1: https://www.ecs.umass.edu/ece/koren/FaultTolerantSystems/simulator/Hamming/HammingCodes.html
 */
class HammingCode1611Test extends AnyFunSuite {

  def codeword(d: String, p: String): BigInt = {

    val pr = p.reverse
    val dr = d.reverse

    BigInt(s"${dr(7)}${dr(6)}${dr(5)}${dr(4)}${pr(4)}${dr(3)}${dr(2)}${dr(1)}${pr(3)}${dr(0)}${pr(2)}${pr(1)}${pr(0)}", 2)
  }

  def printCodeword(d: String, p: String) {
    require(d.length == 8, "Dataword must be 8 characters long!")
    require(p.length == 5, "Parity bits must be 5 characters long!")

    val pr = p.reverse
    val dr = d.reverse

    println(s"Codeword: ${dr(7)}${dr(6)}${dr(5)}${dr(4)}${pr(4)}${dr(3)}${dr(2)}${dr(1)}${pr(3)}${dr(0)}${pr(2)}${pr(1)}${pr(0)}")
  }

  def encodeTestCase(dut: HammingCode1611.Encoder, dataword: String, parity: String) {
    require(dataword.length == 8, "Dataword must be 8 characters long!")
    require(parity.length == 5, "Parity bits must be 5 characters long!")

    println(s"Dataword: ${dataword}")
    println(s"Parity: ${parity}")
    printCodeword(dataword, parity)

    dut.io.dataword #= BigInt(dataword, 2)
    sleep(1)
    assert(dut.io.codeword.toBigInt == codeword(dataword, parity))
  }

  test("encode") {
    val compiled = SimConfig.withWave.compile {
      HammingCode1611.Encoder()
    }
    /* High/Low input */
    compiled.doSim("00000000") { dut => encodeTestCase(dut, "00000000", "00000") }
    compiled.doSim("11111111") { dut => encodeTestCase(dut, "11111111", "00110") }
    /* Five random numbers */
    compiled.doSim("11100101") { dut => encodeTestCase(dut, "11100101", "10000") }
    compiled.doSim("11001111") { dut => encodeTestCase(dut, "11001111", "00000") }
    compiled.doSim("10101011") { dut => encodeTestCase(dut, "10101011", "01110") }
    compiled.doSim("00001000") { dut => encodeTestCase(dut, "00001000", "01110") }
    compiled.doSim("10100100") { dut => encodeTestCase(dut, "10100100", "00001") }
  }

  def decodeTestCase(dut: HammingCode1611.Decoder, dataword: String, errorword: String,
                     parity: String, multiBitError: Boolean = false) {
    require(dataword.length == 8, "Dataword must be 8 characters long!")
    require(errorword.length == 8, "Dataword must be 8 characters long!")
    require(parity.length == 5, "Parity bits must be 5 characters long!")

    println(s"Dataword: ${dataword}")
    println(s"Errorword: ${errorword}")
    println(s"Parity: ${parity}")
    printCodeword(errorword, parity)

    dut.io.codeword #= codeword(errorword, parity)
    sleep(1)
    println(s"Extended Parity Error: ${dut.extendedParityError.toBoolean}")
    println(s"Parity Error: ${dut.parityError.toBoolean}")
    println(s"Error at bit position: ${dut.bitPosition.toInt}")
    println(s"Multi bit error: ${dut.io.multiBitError.toBoolean}")
    if (!multiBitError) {
      assert(dut.io.dataword.toBigInt == BigInt(dataword, 2))
    }
    assert(dut.io.multiBitError.toBoolean == multiBitError)
  }

  test("decode") {
    val compiled = SimConfig.withWave.compile {
      val dut = HammingCode1611.Decoder()
      dut.extendedParityError.simPublic()
      dut.parityError.simPublic()
      dut.bitPosition.simPublic()
      dut
    }
    /* High/Low input */
    compiled.doSim("00000000") { dut => decodeTestCase(dut, "00000000", "00000000", "00000") }
    compiled.doSim("11111111") { dut => decodeTestCase(dut, "11111111", "11111111", "00110") }
    compiled.doSim("10101011 - bitflip pos 1") { dut =>
      decodeTestCase(dut, "10101011", "10101010", "01110")
    }
    compiled.doSim("10101011 - bitflip pos 2") { dut =>
      decodeTestCase(dut, "10101011", "10101001", "01110")
    }
    compiled.doSim("10101011 - bitflip pos 3") { dut =>
      decodeTestCase(dut, "10101011", "10101111", "01110")
    }
    compiled.doSim("10101011 - bitflip pos 4") { dut =>
      decodeTestCase(dut, "10101011", "10100011", "01110")
    }
    compiled.doSim("10101011 - bitflip pos 5") { dut =>
      decodeTestCase(dut, "10101011", "10111011", "01110")
    }
    compiled.doSim("10101011 - bitflip pos 6") { dut =>
      decodeTestCase(dut, "10101011", "10001011", "01110")
    }
    compiled.doSim("10101011 - bitflip pos 7") { dut =>
      decodeTestCase(dut, "10101011", "11101011", "01110")
    }
    compiled.doSim("10101011 - bitflip pos 8") { dut =>
      decodeTestCase(dut, "10101011", "00101011", "01110")
    }
    compiled.doSim("10101011 - bitflip parity pos 1") { dut =>
      decodeTestCase(dut, "10101011", "10101011", "01111")
    }
    compiled.doSim("10101011 - bitflip parity pos 2") { dut =>
      decodeTestCase(dut, "10101011", "10101011", "01100")
    }
    compiled.doSim("10101011 - bitflip parity pos 3") { dut =>
      decodeTestCase(dut, "10101011", "10101011", "01010")
    }
    compiled.doSim("10101011 - bitflip parity pos 4") { dut =>
      decodeTestCase(dut, "10101011", "10101011", "00110")
    }
    compiled.doSim("10101011 - bitflip parity pos 5") { dut =>
      decodeTestCase(dut, "10101011", "10101011", "11110")
    }
    compiled.doSim("10101011 - multi bit error") { dut =>
      decodeTestCase(dut, "10101011", "10101111", "11110", true)
    }
  }
}
