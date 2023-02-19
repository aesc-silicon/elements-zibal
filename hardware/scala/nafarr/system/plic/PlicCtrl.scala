package nafarr.system.plic

import spinal.core._
import spinal.lib._

object PlicCtrl {

  case class Parameter(
      sources: Int,
      priorityWidth: Int
  )
  object Parameter {
    def default(sources: Int) = Parameter(sources, 1)
  }

}
