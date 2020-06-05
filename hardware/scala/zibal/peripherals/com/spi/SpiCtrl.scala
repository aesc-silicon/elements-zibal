package zibal.peripherals.com.spi

object SpiCtrl {
  case class Parameter(
    ssWidth: Int = 1,
    timerWidth: Int,
    dataWidth: Int = 8,
    cmdFifoDepth: Int = 16,
    rspFifoDepth: Int = 16
  ) {
    require(ssWidth > 0)
    require(timerWidth > 1) // TODO
    require(dataWidth > 0)
    require(cmdFifoDepth > 0)
    require(rspFifoDepth > 0)
  }

  object Parameter {
    def default = Parameter(1, 16, 8, 16, 16)
  }
}
