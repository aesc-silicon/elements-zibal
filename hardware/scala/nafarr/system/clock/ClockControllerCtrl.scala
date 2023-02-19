package nafarr.system.clock

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc.BusSlaveFactory
import scala.collection.mutable.Map

import nafarr.blackboxes.xilinx.a7.PLL
import nafarr.system.reset.ResetControllerCtrl.ResetControllerCtrl

object ClockControllerCtrl {
  def apply(parameter: Parameter, resetCtrl: ResetControllerCtrl) =
    ClockControllerCtrl(parameter, resetCtrl)

  case class Parameter(domains: List[ClockParameter]) {
    // TODO check synchronousWith is before

    def getDomainByName(name: String): (Int, ClockParameter) = {
      val domain = domains.find(_.name.equals(name)).get
      (domains.indexOf(domain), domain)
    }
  }

  case class Io(parameter: Parameter) extends Bundle {
    val clocks = in UInt (parameter.domains.length bits)
  }

  case class Config(parameter: Parameter) extends Bundle {
    val enable = UInt(parameter.domains.length bits)
  }

  case class ClockControllerCtrl(
      parameter: Parameter,
      resetCtrl: ResetControllerCtrl
  ) extends Component {
    val io = new Bundle {
      val clocks = out UInt (parameter.domains.length bits)
      val buildConnection = Io(parameter)
      val config = in(Config(parameter))
    }
    io.clocks := io.buildConnection.clocks

    var generatedClocks = List[Bool]()
    var clockDict = Map[String, ClockDomain]()
    for ((domain, index) <- parameter.domains.zipWithIndex) {
      clockDict += domain.name -> {
        val cd = ClockDomain(
          clock = io.buildConnection.clocks(index),
          reset = if (!domain.reset.isEmpty) resetCtrl.getResetByName(domain.reset) else null,
          frequency = FixedFrequency(domain.frequency),
          config = domain.resetConfig
        )
        if (!domain.synchronousWith.isEmpty()) {
          cd.setSynchronousWith(getClockDomainByName(domain.synchronousWith))
        }
        cd
      }
    }
    def getClockDomainByName(name: String): ClockDomain = clockDict.get(name).get

    def buildXilinxPll(
        clock: Bool,
        clockFrequency: HertzNumber,
        clocks: List[String],
        multiply: Int
    ) {
      val clockCtrlClockDomain = ClockDomain(
        clock = clock,
        frequency = FixedFrequency(clockFrequency),
        config = ClockDomainConfig(
          resetKind = BOOT
        )
      )

      val clockCtrl = new ClockingArea(clockCtrlClockDomain) {
        val pll = PLL.PLLE2_BASE(CLKFBOUT_MULT = multiply).connect()
      }

      def addClock(index: Int, frequency: HertzNumber) = index match {
        case 0 => clockCtrl.pll.addClock0(frequency)
        case 1 => clockCtrl.pll.addClock1(frequency)
        case 2 => clockCtrl.pll.addClock2(frequency)
        case 3 => clockCtrl.pll.addClock3(frequency)
        case 4 => clockCtrl.pll.addClock4(frequency)
        case 5 => clockCtrl.pll.addClock5(frequency)
      }
      def getClockPin(index: Int) = index match {
        case 0 => clockCtrl.pll.CLKOUT0
        case 1 => clockCtrl.pll.CLKOUT1
        case 2 => clockCtrl.pll.CLKOUT2
        case 3 => clockCtrl.pll.CLKOUT3
        case 4 => clockCtrl.pll.CLKOUT4
        case 5 => clockCtrl.pll.CLKOUT5
      }

      for ((clock, index) <- clocks.zipWithIndex) {
        val (domainIndex, domain) = parameter.getDomainByName(clock)
        io.buildConnection.clocks(domainIndex) := addClock(index, domain.frequency)
        generatedClocks = generatedClocks :+ getClockPin(index)
      }
    }

    def buildDummy(clock: Bool) {
      for (((domain), index) <- parameter.domains.zipWithIndex) {
        io.buildConnection.clocks(index) := clock
      }
    }
  }
}
