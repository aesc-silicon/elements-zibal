package nafarr.peripherals.com.i2c

object I2cCtrl {
  case class PermissionParameter(
      busCanWriteClockDividerConfig: Boolean
  ) {
    require(busCanWriteClockDividerConfig)
  }
  object PermissionParameter {
    def full = PermissionParameter(true)
    def restricted = PermissionParameter(false)
  }

  case class MemoryMappedParameter(
      cmdFifoDepth: Int,
      rspFifoDepth: Int
  ) {
    require(cmdFifoDepth > 0 && cmdFifoDepth < 256)
    require(rspFifoDepth > 0 && rspFifoDepth < 256)
  }
  object MemoryMappedParameter {
    def lightweight = MemoryMappedParameter(4, 4)
    def default = MemoryMappedParameter(16, 16)
    def full = MemoryMappedParameter(64, 64)
  }

  case class Parameter(
      permission: PermissionParameter,
      memory: MemoryMappedParameter,
      timerWidth: Int = 16,
      interrupts: Int = 0
  ) {
    require(timerWidth > 1)
    require(interrupts >= 0)
  }

  object Parameter {
    def lightweight = Parameter(
      permission = PermissionParameter.full,
      memory = MemoryMappedParameter.lightweight
    )
    def default = Parameter(
      permission = PermissionParameter.full,
      memory = MemoryMappedParameter.default
    )
    def full = Parameter(
      permission = PermissionParameter.full,
      memory = MemoryMappedParameter.full
    )
  }
}
