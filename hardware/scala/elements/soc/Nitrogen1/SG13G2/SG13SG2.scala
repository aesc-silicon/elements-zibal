package elements.soc.nitrogen1

import spinal.core._
import spinal.core.sim._
import spinal.lib._

import nafarr.system.reset._
import nafarr.system.reset.ResetControllerCtrl._
import nafarr.system.clock._
import nafarr.system.clock.ClockControllerCtrl._
import nafarr.blackboxes.ihp.sg13g2._

import zibal.misc._
import zibal.platform.Nitrogen
import zibal.board.{KitParameter, BoardParameter}
import zibal.sim.hyperram.W956A8MBYA
import zibal.sim.MT25Q

import elements.sdk.ElementsApp
import elements.board.ECPIX5
import elements.soc.Nitrogen1

case class ElemRVTop() extends Component {
  val resets = List[ResetParameter](ResetParameter("system", 128), ResetParameter("debug", 128))
  val clocks = List[ClockParameter](
    ClockParameter("system", 100 MHz, "system"),
    ClockParameter("debug", 10 MHz, "debug", synchronousWith = "system")
  )
  val hyperbusPartitions = List[(BigInt, Boolean)](
    (8 MB, false)
  )
  val kitParameter = KitParameter(resets, clocks)
  val boardParameter = ECPIX5.Parameter(kitParameter, ECPIX5.SystemClock.frequency)
  val socParameter = Nitrogen1.Parameter(boardParameter)
  val parameter = Nitrogen.Parameter(
    socParameter,
    1 kB,
    8 MB,
    hyperbusPartitions,
    (resetCtrl: ResetControllerCtrl, reset: Bool, _) => {
      resetCtrl.buildDummy(reset)
    },
    (clockCtrl: ClockControllerCtrl, resetCtrl: ResetControllerCtrl, clock: Bool) => {
      clockCtrl.buildDummy(clock, resetCtrl)
    }
  )

  val io = new Bundle {
    val clock = IhpCmosIo("", 0)
    val reset = IhpCmosIo("", 0)
    val uartStd = new Bundle {
      val txd = IhpCmosIo("", 0)
      val rxd = IhpCmosIo("", 0)
      val cts = IhpCmosIo("", 0)
      val rts = IhpCmosIo("", 0)
    }
    val jtag = new Bundle {
      val tms = IhpCmosIo("", 0)
      val tdi = IhpCmosIo("", 0)
      val tdo = IhpCmosIo("", 0)
      val tck = IhpCmosIo("", 0)
    }
    val gpioStatus = Vec(
      IhpCmosIo("", 0),
      IhpCmosIo("", 0),
      IhpCmosIo("", 0),
      IhpCmosIo("", 0)
    )
    val hyperbus = new Bundle {
      val cs = Vec(
        IhpCmosIo("", 0)
      )
      val ck = IhpCmosIo("", 0)
      val reset = IhpCmosIo("", 0)
      val dq = Vec(
        IhpCmosIo("", 0),
        IhpCmosIo("", 0),
        IhpCmosIo("", 0),
        IhpCmosIo("", 0),
        IhpCmosIo("", 0),
        IhpCmosIo("", 0),
        IhpCmosIo("", 0),
        IhpCmosIo("", 0)
      )
      val rwds = IhpCmosIo("", 0)
    }
    val spiFlash = new Bundle {
      val cs = Vec(
        IhpCmosIo("", 0)
      )
      val dq = Vec(
        IhpCmosIo("", 0),
        IhpCmosIo("", 0)
      )
      val sck = IhpCmosIo("", 0)
    }
  }

  val soc = Nitrogen1(parameter)

  io.clock <> IOPadIn(soc.io_plat.clock)
  io.reset <> IOPadIn(soc.io_plat.reset)

  io.jtag.tms <> IOPadIn(soc.io_plat.jtag.tms)
  io.jtag.tdi <> IOPadIn(soc.io_plat.jtag.tdi)
  io.jtag.tdo <> IOPadOut4mA(soc.io_plat.jtag.tdo)
  io.jtag.tck <> IOPadIn(soc.io_plat.jtag.tck)

  io.uartStd.txd <> IOPadOut4mA(soc.io_per.uartStd.txd)
  io.uartStd.rxd <> IOPadIn(soc.io_per.uartStd.rxd)
  io.uartStd.cts <> IOPadIn(soc.io_per.uartStd.cts)
  io.uartStd.rts <> IOPadOut4mA(soc.io_per.uartStd.rts)

  for (index <- 0 until io.gpioStatus.length) {
    io.gpioStatus(index) <> IOPadInOut4mA(soc.io_per.gpioStatus.pins(index))
  }

  for (index <- 0 until io.hyperbus.cs.length) {
    io.hyperbus.cs(index) <> IOPadOut4mA(soc.io_plat.hyperbus.cs(index))
  }
  io.hyperbus.ck <> IOPadOut30mA(soc.io_plat.hyperbus.ck)
  io.hyperbus.reset <> IOPadOut4mA(soc.io_plat.hyperbus.reset)
  for (index <- 0 until io.hyperbus.dq.length) {
    io.hyperbus.dq(index) <> IOPadInOut30mA(soc.io_plat.hyperbus.dq(index))
  }
  io.hyperbus.rwds <> IOPadInOut30mA(soc.io_plat.hyperbus.rwds)

  for (index <- 0 until io.spiFlash.cs.length) {
    io.spiFlash.cs(index) <> IOPadOut4mA(soc.io_plat.spi.cs(index))
  }
  io.spiFlash.sck <> IOPadOut4mA(soc.io_plat.spi.sclk)
  for (index <- 0 until io.spiFlash.dq.length) {
    io.spiFlash.dq(index) <> IOPadInOut4mA(soc.io_plat.spi.dq(index))
  }
}

object SG13G2Generate extends ElementsApp {
  elementsConfig.genASICSpinalConfig.generateVerilog {
    val top = ElemRVTop()

    top
  }
}
