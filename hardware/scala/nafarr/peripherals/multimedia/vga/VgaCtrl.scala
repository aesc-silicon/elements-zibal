package nafarr.peripherals.multimedia.vga

import spinal.core._
import spinal.lib._
import spinal.lib.bus.misc.BusSlaveFactory
import nafarr.multimedia.{Rgb, MultimediaConfig, MultimediaStream}
import nafarr.peripherals.multimedia.SyncPulse

object VgaCtrl {
  def apply(p: Parameter = Parameter.default) = VgaCtrl(p)

  case class Parameter(
      multimediaConfig: MultimediaConfig
  )
  object Parameter {
    def default = Parameter(MultimediaConfig.default())
    def full = Parameter(MultimediaConfig.full())
  }

  case class Io(p: Parameter) extends Bundle {
    val vga = master(Vga.Io(p))
    val stream = slave(MultimediaStream(p.multimediaConfig))
  }

  case class VgaCtrl(p: Parameter) extends Component {
    val io = Io(p)

    val outputLogic = new SlowArea(2) {
      val pixelX = Reg(UInt(p.multimediaConfig.hTimings.width bit)).init(0)
      val pixelY = Reg(UInt(p.multimediaConfig.vTimings.width bit)).init(0)
      val h = SyncPulse.Engine(p.multimediaConfig.hTimings, True)
      val v = SyncPulse.Engine(p.multimediaConfig.vTimings, h.dataEnd)
      val dataEn = h.dataEn && v.dataEn

      when(h.dataEn) {
        pixelX := pixelX + 1
      } otherwise {
        pixelX := 0
      }
      when(v.dataEn) {
        when(h.dataEnd) {
          pixelY := pixelY + 1
        }
      } otherwise {
        pixelY := 0
      }
      io.stream.pixel.x := pixelX
      io.stream.pixel.y := pixelY

      io.stream.enable := dataEn
      when(dataEn && io.stream.data.valid) {
        io.vga.pixels.r := io.stream.data.r
        io.vga.pixels.g := io.stream.data.g
        io.vga.pixels.b := io.stream.data.b
        io.stream.data.ready := True
      } otherwise {
        io.vga.pixels.r := 0
        io.vga.pixels.g := 0
        io.vga.pixels.b := 0
        io.stream.data.ready := False
      }
      io.vga.hSync := h.sync
      io.vga.vSync := v.sync
    }

  }

  case class Mapper(
      busCtrl: BusSlaveFactory,
      ctrl: Io,
      p: Parameter
  ) extends Area {
    /* Do nothing for now */
  }
}
