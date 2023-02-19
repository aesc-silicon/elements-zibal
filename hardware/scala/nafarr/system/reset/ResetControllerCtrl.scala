package nafarr.system.reset

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc.BusSlaveFactory
import scala.collection.mutable.Map

object ResetControllerCtrl {
  def apply(parameter: Parameter) = {
    val resetCtrl = ResetControllerCtrl(parameter)
    resetCtrl.io.trigger <> U(0, parameter.domains.length bits)
    resetCtrl
  }

  case class Parameter(domains: List[ResetParameter]) {
    for (domain <- domains)
      require(domain.delay > 0, s"Delay for reset domain ${domain.name} must at least 1 cycle!")
  }

  case class Io(parameter: Parameter) extends Bundle {
    val resets = in UInt (parameter.domains.length bits)
    val trigger = out UInt (parameter.domains.length bits)
  }

  case class Config(parameter: Parameter) extends Bundle {
    val enable = UInt(parameter.domains.length bits)
    val trigger = UInt(parameter.domains.length bits)
    val acknowledge = Bool
  }

  case class ResetControllerCtrl(parameter: Parameter) extends Component {
    val io = new Bundle {
      val resets = out UInt (parameter.domains.length bits)
      val trigger = in UInt (parameter.domains.length bits)
      val buildConnection = Io(parameter)
      val config = in(Config(parameter))
    }
    io.resets := io.buildConnection.resets
    val ctrlTrigger = U(0, parameter.domains.length bits)
    when(io.config.acknowledge) {
      ctrlTrigger := io.config.trigger
    }
    io.buildConnection.trigger := io.config.enable & (io.trigger | ctrlTrigger)

    var resetDict = Map[String, Bool]()
    var triggerDict = Map[String, Bool]()
    for ((domain, index) <- parameter.domains.zipWithIndex) {
      resetDict += domain.name -> io.resets(index)
      triggerDict += domain.name -> io.trigger(index)
    }
    def getResetByName(name: String): Bool = resetDict.get(name).get
    def triggerByNameWithCond(name: String, cond: Bool) {
      triggerDict.get(name).get.setWhen(cond)
    }

    def buildXilinx(clock: Bool) {
      val resetCtrlClockDomain = ClockDomain(
        clock = clock,
        config = ClockDomainConfig(
          resetKind = BOOT
        )
      )

      val resetCtrl = new ClockingArea(resetCtrlClockDomain) {
        for (((domain), index) <- parameter.domains.zipWithIndex) {
          val resetUnbuffered = True
          val counter = Reg(UInt(log2Up(domain.delay) bits)).init(0)
          when(counter =/= U(domain.delay - 1)) {
            counter := counter + 1
            resetUnbuffered := False
          }
          when(counter === U(domain.delay - 1) && BufferCC(io.buildConnection.trigger(index))) {
            counter := 0
          }
          io.buildConnection.resets(index) := RegNext(resetUnbuffered)
        }
      }
    }

    def buildDummy(reset: Bool) {
      for (((domain), index) <- parameter.domains.zipWithIndex) {
        io.buildConnection.resets(index) := reset
      }
    }
  }
}
