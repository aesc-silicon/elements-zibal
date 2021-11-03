/*
 * Copyright (c) 2021 Phytec Messtechnik GmbH
 */

package zibal.soc.hydrogentest

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import zibal.soc.HydrogenTest
import zibal.misc.{ElementsConfig, BinTools, XilinxTools, SimulationHelper}
import zibal.blackboxes.xilinx.a7._

import sys.process._


object Nexys4DDR {
  case class Io() extends Bundle {
    val clock = XilinxCmosIo("E3").clock(100 MHz)
    val jtag = new Bundle {
      val tms = XilinxCmosIo("H2")
      val tdi = XilinxCmosIo("G4")
      val tdo = XilinxCmosIo("G2")
      val tck = XilinxCmosIo("F3").clock(10 MHz).
        comment("set_property CLOCK_DEDICATED_ROUTE FALSE [get_nets {iBUF_4_O}]")
    }
    val uartStd = new Bundle {
      val txd = XilinxCmosIo("D4")
      val rxd = XilinxCmosIo("C4")
      val rts = XilinxCmosIo("D3")
      val cts = XilinxCmosIo("E5")
    }
    val gpioStatus = Vec(
      XilinxCmosIo("R12"),
      XilinxCmosIo("R11"),
      XilinxCmosIo("G14"),
      XilinxCmosIo("N16")
    )
    val gpioA = Vec(
      XilinxCmosIo("J15"),
      XilinxCmosIo("L16"),
      XilinxCmosIo("M13"),
      XilinxCmosIo("R15"),
      XilinxCmosIo("R17"),
      XilinxCmosIo("T18"),
      XilinxCmosIo("U18"),
      XilinxCmosIo("R13"),
      XilinxCmosIo("T8"),
      XilinxCmosIo("U8"),
      XilinxCmosIo("R16"),
      XilinxCmosIo("T13"),
      XilinxCmosIo("H6"),
      XilinxCmosIo("U12"),
      XilinxCmosIo("U11"),
      XilinxCmosIo("V10"),
      XilinxCmosIo("H17"),
      XilinxCmosIo("K15"),
      XilinxCmosIo("J13"),
      XilinxCmosIo("N14"),
      XilinxCmosIo("R18"),
      XilinxCmosIo("V17"),
      XilinxCmosIo("U17"),
      XilinxCmosIo("U16"),
      XilinxCmosIo("V16"),
      XilinxCmosIo("T15"),
      XilinxCmosIo("U14"),
      XilinxCmosIo("T16"),
      XilinxCmosIo("V15"),
      XilinxCmosIo("V14"),
      XilinxCmosIo("V12"),
      XilinxCmosIo("V11")
    )
    val spiA = new Bundle {
      val ss = XilinxCmosIo("D14")
      val sclk = XilinxCmosIo("F16")
      val mosi = XilinxCmosIo("G16")
      val miso = XilinxCmosIo("H14")
    }
    val i2cA = new Bundle {
      val scl = XilinxCmosIo("C14")
      val sda = XilinxCmosIo("C15")
    }
    val freqCounterA = XilinxCmosIo("C17").clock(100 MHz).
        comment("set_property CLOCK_DEDICATED_ROUTE FALSE [get_nets {iBUF_10_O}]")
  }
}


object Nexys4DDRBoard {
  def apply(source: String) = Nexys4DDRBoard(source)

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig()

    var baudPeriod: Int = 0
    var clockPeriod: Int = 0

    val config = SpinalConfig(noRandBoot = false, targetDirectory = elementsConfig.zibalBuildPath)
    val compiled = SimConfig.withConfig(config).withWave.workspacePath(elementsConfig.zibalBuildPath).allOptimisation.compile {
      val parameter = HydrogenTest.Peripherals.default()
      val peripherals = parameter.peripherals.asInstanceOf[HydrogenTest.Peripherals]
      baudPeriod = peripherals.uartStd.init.getBaudPeriod()
      clockPeriod = 1000000000 / parameter.sysFrequency.toInt

      val board = Nexys4DDRBoard(args(0))
      board
    }
    args(1) match {
      case "boot" =>
        compiled.doSimUntilVoid("boot") { dut =>
          SimulationHelper.generateClock(dut.io.clock, clockPeriod, 10000000)
          SimulationHelper.dumpStdout(dut.io.uartStd.txd, baudPeriod)
          SimulationHelper.expectZephyrPrompt(dut.io.uartStd.txd, baudPeriod)
        }
      case "mtimer" =>
        compiled.doSimUntilVoid("mtimer") { dut =>
          SimulationHelper.generateClock(dut.io.clock, clockPeriod, 400000000)
          fork {
            sleep(100000)
            assert(dut.io.gpioStatus(0).toBoolean == false)
            println("Heartbeat LED: OFF")
            sleep(400000)
            assert(dut.io.gpioStatus(0).toBoolean == true)
            println("Heartbeat LED: ON")
            sleep(150500000)
            assert(dut.io.gpioStatus(0).toBoolean == false)
            println("Heartbeat LED: OFF")
            sleep(50000000)
            assert(dut.io.gpioStatus(0).toBoolean == true)
            println("Heartbeat LED: ON")
            sleep(150000000)
            assert(dut.io.gpioStatus(0).toBoolean == false)
            println("Heartbeat LED: OFF")
            simSuccess
          }
        }
      case "gpio" =>
        compiled.doSimUntilVoid("gpio") { dut =>
          SimulationHelper.generateClock(dut.io.clock, clockPeriod, 10000000)
          SimulationHelper.dumpStdout(dut.io.uartStd.txd, baudPeriod)
          fork {
            SimulationHelper.waitUntilOrFail(dut.io.gpioA(0).toBoolean == true,
                                             clockPeriod, 200000)
            SimulationHelper.waitUntilOrFail(dut.io.gpioA(2).toBoolean == true,
                                             clockPeriod, 20000)
            simSuccess
          }
        }
      case "uart" =>
        compiled.doSimUntilVoid("uart") { dut =>
          dut.io.uartStd.rxd #= true
          SimulationHelper.generateClock(dut.io.clock, clockPeriod, 10000000)
          SimulationHelper.dumpCharacters(dut.io.uartStd.txd, baudPeriod)
          fork {
            SimulationHelper.waitUntilOrFail(dut.io.uartStd.txd.toBoolean == true,
                                             clockPeriod, 100000)

            SimulationHelper.waitUntilOrFail(dut.io.uartStd.txd.toBoolean == false,
                                             clockPeriod, 100000)

            val buffer = SimulationHelper.uartReceive(dut.io.uartStd.txd, baudPeriod)
            assert(buffer == 'X')
            SimulationHelper.uartTransmit(dut.io.uartStd.rxd, baudPeriod, 'G')
            val bufferG = SimulationHelper.uartReceive(dut.io.uartStd.txd, baudPeriod)
            assert(bufferG == 'G')
            simSuccess
          }
        }
        compiled.doSimUntilVoid("uart-irq") { dut =>
          dut.io.uartStd.rxd #= true
          SimulationHelper.generateClock(dut.io.clock, clockPeriod, 10000000)
          SimulationHelper.dumpCharacters(dut.io.uartStd.txd, baudPeriod)
          fork {
            SimulationHelper.waitUntilOrFail(dut.io.uartStd.txd.toBoolean == true,
                                             clockPeriod, 100000)

            SimulationHelper.waitUntilOrFail(dut.io.uartStd.txd.toBoolean == false,
                                             clockPeriod, 100000)
            val buffer = SimulationHelper.uartReceive(dut.io.uartStd.txd, baudPeriod)
            assert(buffer == 'X')
            SimulationHelper.uartTransmit(dut.io.uartStd.rxd, baudPeriod, 'G')
            val bufferG = SimulationHelper.uartReceive(dut.io.uartStd.txd, baudPeriod)
            assert(bufferG == 'G')

            SimulationHelper.uartTransmit(dut.io.uartStd.rxd, baudPeriod, 'H')
            SimulationHelper.waitUntilOrFail(dut.io.uartStd.txd.toBoolean == false,
                                             clockPeriod, 100000)
            val bufferR = SimulationHelper.uartReceive(dut.io.uartStd.txd, baudPeriod)
            assert(bufferR == 'R')
            SimulationHelper.waitUntilOrFail(dut.io.uartStd.txd.toBoolean == false,
                                             clockPeriod, 100000)
            val bufferH = SimulationHelper.uartReceive(dut.io.uartStd.txd, baudPeriod)
            assert(bufferH == 'H')
            simSuccess
          }
        }
      case "frequency" =>
        compiled.doSimUntilVoid("100Mhz") { dut =>
          SimulationHelper.generateClock(dut.io.clock, clockPeriod, 10000000)
          SimulationHelper.generateClock(dut.io.freqCounterA, 10, 10000000)
          SimulationHelper.dumpStdout(dut.io.uartStd.txd, baudPeriod)
          fork {
            var stdout = ""
            sleep(1000000)
            while (!stdout.contains("\n")) {
              SimulationHelper.waitUntilOrFail(dut.io.uartStd.txd.toBoolean == false,
                                               clockPeriod, 100000)
              val char = SimulationHelper.uartReceive(dut.io.uartStd.txd, baudPeriod)
              stdout = stdout + char.toChar
            }
            "100000\\d{3} Hz".r findFirstIn stdout match {
              case Some(_) => simSuccess
              case None => assert(false, s"100MHz not found in $stdout")
            }
          }
        }
        compiled.doSimUntilVoid("33Mhz") { dut =>
          SimulationHelper.generateClock(dut.io.clock, clockPeriod, 10000000)
          SimulationHelper.generateClock(dut.io.freqCounterA, 30, 10000000)
          SimulationHelper.dumpStdout(dut.io.uartStd.txd, baudPeriod)
          fork {
            var stdout = ""
            sleep(1000000)
            while (!stdout.contains("\n")) {
              SimulationHelper.waitUntilOrFail(dut.io.uartStd.txd.toBoolean == false,
                                               clockPeriod, 100000)
              val char = SimulationHelper.uartReceive(dut.io.uartStd.txd, baudPeriod)
              stdout = stdout + char.toChar
            }
            "33000\\d{3} Hz".r findFirstIn stdout match {
              case Some(_) => simSuccess
              case None => assert(false, s"33MHz not found in $stdout")
            }
          }
        }
        compiled.doSimUntilVoid("20Mhz") { dut =>
          SimulationHelper.generateClock(dut.io.clock, clockPeriod, 10000000)
          SimulationHelper.generateClock(dut.io.freqCounterA, 50, 10000000)
          SimulationHelper.dumpStdout(dut.io.uartStd.txd, baudPeriod)
          fork {
            var stdout = ""
            sleep(1000000)
            while (!stdout.contains("\n")) {
              SimulationHelper.waitUntilOrFail(dut.io.uartStd.txd.toBoolean == false,
                                               clockPeriod, 100000)
              val char = SimulationHelper.uartReceive(dut.io.uartStd.txd, baudPeriod)
              stdout = stdout + char.toChar
            }
            "20000\\d{3} Hz".r findFirstIn stdout match {
              case Some(_) => simSuccess
              case None => assert(false, s"20MHz not found in $stdout")
            }
          }
        }
      case _ =>
        println(s"Unknown simulation ${args(1)}")
    }
  }

  case class Nexys4DDRTop(source: String) extends BlackBox {
    val io = Nexys4DDR.Io()

    val elementsConfig = ElementsConfig()
    val className = this.getClass().getName().split("\\.").last.split("\\$").last

    addRTLPath("hardware/scala/zibal/blackboxes/xilinx/a7/IO.v")
    if (source.equals("generated")) {
      addRTLPath(elementsConfig.zibalBuildPath + s"${className}.v")
    } else {
      println(s"Unsupported source $source for $className")
    }

    val result = Process(s"ls ${elementsConfig.zibalBuildPath}").!!
    for (line <- result.lines().toArray()) {
      if (line.asInstanceOf[String].endsWith(".bin")) {
        addRTLPath(elementsConfig.zibalBuildPath + line)
      }
    }
  }

  case class Nexys4DDRBoard(source: String) extends Component {
    val io = new Bundle {
      val clock = inout(Analog(Bool))
      val jtag = new Bundle {
        val tdo = inout(Analog(Bool))
      }
      val uartStd = new Bundle {
        val txd = inout(Analog(Bool))
        val rxd = inout(Analog(Bool))
        val rts = inout(Analog(Bool))
        val cts = inout(Analog(Bool))
      }
      val gpioStatus = Vec(inout(Analog(Bool())), 4)
      val spiA = new Bundle {
        val ss = inout(Analog(Bool))
        val sclk = inout(Analog(Bool))
        val mosi = inout(Analog(Bool))
      }
      val gpioA = Vec(inout(Analog(Bool())), 32)
      val freqCounterA = inout(Analog(Bool))
    }
    val top = Nexys4DDRTop(source)
    val analogFalse = Analog(Bool)
    analogFalse := False
    val analogTrue = Analog(Bool)
    analogTrue := True

    top.io.clock.PAD := io.clock

    top.io.jtag.tms.PAD := analogFalse
    top.io.jtag.tdi.PAD := analogFalse
    io.jtag.tdo := top.io.jtag.tdo.PAD
    top.io.jtag.tck.PAD := analogFalse
    top.io.uartStd.rxd.PAD := io.uartStd.rxd
    io.uartStd.txd := top.io.uartStd.txd.PAD
    top.io.uartStd.cts.PAD := io.uartStd.cts
    io.uartStd.rts := top.io.uartStd.rts.PAD

    io.spiA.ss := top.io.spiA.ss.PAD
    io.spiA.sclk := top.io.spiA.sclk.PAD
    io.spiA.mosi := top.io.spiA.mosi.PAD
    top.io.spiA.miso.PAD := analogFalse
    top.io.i2cA.scl.PAD := analogFalse
    top.io.i2cA.sda.PAD := analogFalse
    top.io.freqCounterA.PAD := io.freqCounterA

    for (index <- 0 until 4) {
      io.gpioStatus(index) <> top.io.gpioStatus(index).PAD
    }

    for (index <- 0 until 32) {
      io.gpioA(index) <> top.io.gpioA(index).PAD
    }
    top.io.gpioA(1).PAD := analogTrue
  }
}


object Nexys4DDRTop {
  def apply() = Nexys4DDRTop()

  def main(args: Array[String]) {
    val elementsConfig = ElementsConfig()
    val className = this.getClass().getName().stripSuffix("$").split("\\.").last

    val config = SpinalConfig(noRandBoot = false, targetDirectory = elementsConfig.zibalBuildPath)

    args(0) match {
      case "prepare" =>
        HydrogenTest.prepare(config, elementsConfig, 100 MHz)
      case _ =>
        config.generateVerilog({
          val top = Nexys4DDRTop()
          val system = top.soc.system
          BinTools.initRam(system.onChipRam.ram, elementsConfig.zephyrBuildPath + "/zephyr.bin")
          XilinxTools.Xdc(elementsConfig).generate(top.io, className)
          top
        })
    }
  }

  case class Nexys4DDRTop() extends Component {
    val io = Nexys4DDR.Io()

    val soc = HydrogenTest()

    io.clock <> IBUF(soc.io_sys.clock)
    soc.io_sys.reset := False

    io.jtag.tms <> IBUF(soc.io_sys.jtag.tms)
    io.jtag.tdi <> IBUF(soc.io_sys.jtag.tdi)
    io.jtag.tdo <> OBUF(soc.io_sys.jtag.tdo)
    io.jtag.tck <> IBUF(soc.io_sys.jtag.tck)

    io.uartStd.txd <> OBUF(soc.io_per.uartStd.txd)
    io.uartStd.rxd <> IBUF(soc.io_per.uartStd.rxd)
    io.uartStd.rts <> OBUF(soc.io_per.uartStd.rts)
    io.uartStd.cts <> IBUF(soc.io_per.uartStd.cts)

    io.spiA.ss <> OBUF(soc.io_per.spiA.ss(0))
    io.spiA.sclk <> OBUF(soc.io_per.spiA.sclk)
    io.spiA.mosi <> OBUF(soc.io_per.spiA.mosi)
    io.spiA.miso <> IBUF(soc.io_per.spiA.miso)

    io.i2cA.scl <> IBUF(soc.io_per.i2cA.scl.read)
    io.i2cA.scl <> OBUFT(False, soc.io_per.i2cA.scl.write)
    io.i2cA.scl <> PULLUP()
    io.i2cA.sda <> IBUF(soc.io_per.i2cA.sda.read)
    io.i2cA.sda <> OBUFT(False, soc.io_per.i2cA.sda.write)
    io.i2cA.sda <> PULLUP()

    io.freqCounterA <> IBUF(soc.io_per.freqCounterA.clock)

    for (index <- 0 until 4) {
      io.gpioStatus(index) <> IOBUF(soc.io_per.gpioStatus.pins(index))
    }

    for (index <- 0 until 32) {
      io.gpioA(index) <> IOBUF(soc.io_per.gpioA.pins(index))
    }
  }
}
