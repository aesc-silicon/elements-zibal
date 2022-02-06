package zibal.peripherals.misc.reset

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

  case class Parameter(domains: List[(String, Int)]) {
    for ((name, delay) <- domains)
      require(delay > 0, s"Delay for reset domain $name must at least 1 cycle!")
  }

  case class BuildConnection(parameter: Parameter) extends Bundle {
    val resets = in UInt(parameter.domains.length bits)
    val trigger = out UInt(parameter.domains.length bits)
  }

  case class Config(parameter: Parameter) extends Bundle {
    val enable = UInt(parameter.domains.length bits)
    val trigger = UInt(parameter.domains.length bits)
    val acknowledge = Bool
  }

  case class ResetControllerCtrl(parameter: Parameter) extends Component {
    val io = new Bundle {
      val resets = out UInt(parameter.domains.length bits)
      val trigger = in UInt(parameter.domains.length bits)
      val buildConnection = BuildConnection(parameter)
      val config = in(Config(parameter))
    }
    io.resets := io.buildConnection.resets
    val ctrlTrigger = U(0, parameter.domains.length bits)
    when (io.config.acknowledge) {
      ctrlTrigger := io.config.trigger
    }
    io.buildConnection.trigger := io.config.enable & (io.trigger | ctrlTrigger)

    var resetDict = Map[String, Bool]()
    var triggerDict = Map[String, Bool]()
    for (((name, delay), index) <- parameter.domains.zipWithIndex) {
      resetDict += name -> io.resets(index)
      triggerDict += name -> io.trigger(index)
    }
    def getResetByName(name: String): Bool = resetDict.get(name).get
    def triggerByNameWithCond(name: String, cond: Bool) {
      triggerDict.get(name).get.setWhen(cond)
    }

    def buildFPGA(clock: Bool, buildConnection: BuildConnection) {
      val resetCtrlClockDomain = ClockDomain(
        clock = clock,
        config = ClockDomainConfig(
          resetKind = BOOT
        )
      )

      val resetCtrl = new ClockingArea(resetCtrlClockDomain) {
        for (((name, delay), index) <- parameter.domains.zipWithIndex) {
          val resetUnbuffered = True
          val counter = Reg(UInt(log2Up(delay) bits)) init(0)
          when (counter =/= U(delay - 1)) {
            counter := counter + 1
            resetUnbuffered := False
          }
          when (counter === U(delay - 1) && BufferCC(buildConnection.trigger(index))) {
            counter := 0
          }
          buildConnection.resets(index) := RegNext(resetUnbuffered)
        }
      }
    }
  }
}
