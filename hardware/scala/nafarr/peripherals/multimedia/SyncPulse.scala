package nafarr.peripherals.multimedia

import spinal.core._
import spinal.lib._

import nafarr.multimedia.TimingsConfig

object SyncPulse {

  case class Engine(timings: TimingsConfig, enable: Bool) extends Area {
    val counter = Reg(UInt(timings.width bit)).init(0)

    val syncStart = counter === U(timings.syncStart(), timings.width bits)
    val syncEnd = counter === U(timings.syncEnd(), timings.width bits)
    val dataStart = counter === U(timings.dataStart(), timings.width bits)
    val dataEnd = counter === U(timings.dataEnd(), timings.width bits)
    val polarity = Bool(timings.polarity)

    when(enable) {
      counter := counter + 1
      when(dataEnd) {
        counter := 0
      }
    }

    val syncInt = RegInit(False) setWhen (syncStart) clearWhen (syncEnd)
    val sync = syncInt ^ !polarity
    val dataEn = RegInit(False) setWhen (dataStart) clearWhen (dataEnd)
  }
}
