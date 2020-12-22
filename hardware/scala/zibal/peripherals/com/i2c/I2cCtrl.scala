package zibal.peripherals.com.i2c

object I2cCtrl {
  case class Parameter(
    timerWidth: Int,
    cmdFifoDepth: Int = 16,
    rspFifoDepth: Int = 16,
    interrupts: Int = 0
  ) {
    require(timerWidth > 1) // TODO
    require(cmdFifoDepth > 0)
    require(rspFifoDepth > 0)
    require(interrupts >= 0)
  }

  object Parameter {
    def default = Parameter(16, 16, 16, 0)
  }
}
