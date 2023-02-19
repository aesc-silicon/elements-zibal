package nafarr.networkonchip.amba4

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba3.apb._

package object axi {
  val axi4Config = new Area {
    val coreUpsized = Axi4Config(32, 128, 4)
    val coreSmall = Axi4Config(32, 32, 4)
    val core = Axi4Config(64, 128, 4)
    val noc = Axi4Config(64, 128, 14, rUserWidth = 10, wUserWidth = 10, bUserWidth = 10)
  }
  val apb3Config = Apb3Config(12, 32)

  val chipletIdWidth = 10

  case class MemoryTranslationLookupRow(
      physicalWidth: Int,
      virtualWidth: Int,
      pageTypes: Int = 0
  ) extends Bundle {
    val physical = Bits(physicalWidth bits)
    val virtual = Bits(virtualWidth bits)
    val pageType = if (pageTypes > 0) UInt(log2Up(pageTypes - 1) bits) else null
    val valid = Bool()
  }

  case class MemoryPartitionRow(boundaryWidth: Int) extends Bundle {
    val lowerBoundary = UInt(boundaryWidth bits)
    val upperBoundary = UInt(boundaryWidth bits)
    val chipletId = UInt(chipletIdWidth bits)
    val rwxPermission = Bits(3 bits)
    val valid = Bool()

    def hasReadPermission(): Bool = this.rwxPermission(2) === True
    def hasWritePermission(): Bool = this.rwxPermission(1) === True
  }

  case class PermissionError(config: Axi4Config) extends Bundle {
    val id = UInt(config.idWidth bits)
  }
  case class WriteApproval() extends Bundle {
    val approved = Bool()
  }
  case class RoutingDirections() extends Bundle {
    val directions = Bits(4 bits)

    def isDisabled = directions === B"0000"
    def northEnabled = directions(3) === True
    def eastEnabled = directions(2) === True
    def southEnabled = directions(1) === True
    def westEnabled = directions(0) === True
  }
}
