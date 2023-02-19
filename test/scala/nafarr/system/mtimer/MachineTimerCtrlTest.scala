package nafarr.system.mtimer

import org.scalatest.funsuite.AnyFunSuite

import spinal.sim._
import spinal.core._
import spinal.core.sim._

class MachineTimerCtrlTest extends AnyFunSuite {
  test("basic") {
    val compiled = SimConfig.withWave.compile(
      MachineTimerCtrl(MachineTimerCtrl.Parameter.default)
    )

    compiled.doSim{ dut =>
      dut.clockDomain.forkStimulus(10)

      fork{
        dut.clockDomain.fallingEdge()
        sleep(10)
        while(true){
          dut.clockDomain.clockToggle()
          sleep(5)
        }
      }

      /* Init */
      dut.io.config.compare #= 0
      dut.io.clear #= false
      /* Wait for reset and check initialized state */
      dut.clockDomain.waitSampling(2)
      dut.clockDomain.waitFallingEdge()
      assert(dut.io.interrupt.toBoolean == false, "Interrupt is pending")
      assert(dut.io.counter.toBigInt == 1, "Counter is not 1")
      /* Wait some cycles */
      dut.clockDomain.waitFallingEdge(10)
      /* Write value and provoke clear */
      dut.io.config.compare #= dut.io.config.compare.toBigInt + 10
      dut.io.clear #= true
      dut.clockDomain.waitFallingEdge(1)
      /* Interrupt should only rise after 10 cycles */
      for (_ <- 0 to 9) {
        assert(dut.io.interrupt.toBoolean == false)
        dut.io.clear #= false
        dut.clockDomain.waitFallingEdge(1)
      }
      assert(dut.io.interrupt.toBoolean == true)

      dut.clockDomain.waitFallingEdge(1)

      /* Write value and provoke clear */
      dut.io.config.compare #= dut.io.config.compare.toBigInt + 10 + 2
      dut.io.clear #= true
      dut.clockDomain.waitFallingEdge(1)
      /* Interrupt should only rise after 10 cycles */
      for (_ <- 0 to 9) {
        assert(dut.io.interrupt.toBoolean == false)
        dut.io.clear #= false
        dut.clockDomain.waitFallingEdge(1)
      }
      assert(dut.io.interrupt.toBoolean == true)
    }
  }
}
