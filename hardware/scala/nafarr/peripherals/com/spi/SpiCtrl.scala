package nafarr.peripherals.com.spi

import spinal.core._

object SpiCtrl {
  case class InitParameter(
      cpol: Boolean = false,
      cpha: Boolean = false,
      frequency: HertzNumber = 1 Hz
  )
  object InitParameter {
    def default = InitParameter(false, false, 100 kHz)
    def xip = InitParameter(false, false, 1 MHz)
  }

  case class PermissionParameter(
      busCanWriteModeConfig: Boolean,
      busCanWriteClockDividerConfig: Boolean
  ) {
    require(busCanWriteModeConfig)
    require(busCanWriteClockDividerConfig)
  }
  object PermissionParameter {
    def full = PermissionParameter(true, true)
    def restricted = PermissionParameter(false, false)
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
      init: InitParameter = null,
      ssWidth: Int = 1,
      timerWidth: Int = 16,
      dataWidth: Int = 8
  ) {
    require(ssWidth > 0)
    require(timerWidth > 1)
    require(dataWidth > 0)
  }

  object Parameter {
    def lightweight = Parameter(
      permission = PermissionParameter.full,
      memory = MemoryMappedParameter.lightweight,
      init = InitParameter.default
    )
    def default = Parameter(
      permission = PermissionParameter.full,
      memory = MemoryMappedParameter.default,
      init = InitParameter.default
    )
    def xip = Parameter(
      permission = PermissionParameter.full,
      memory = MemoryMappedParameter.default,
      init = InitParameter.xip
    )
    def full = Parameter(
      permission = PermissionParameter.full,
      memory = MemoryMappedParameter.full,
      init = InitParameter.default
    )
  }
}
