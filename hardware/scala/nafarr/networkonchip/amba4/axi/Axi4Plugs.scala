package nafarr.networkonchip.amba4.axi

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba3.apb._

object Axi4Plugs {

  case class Manager(config: Axi4Config) extends Component {
    val io = new Bundle {
      val input = slave(Axi4(config))
    }

    io.input.ar.ready := False
    io.input.r.valid := False
    io.input.r.data := B(0, config.dataWidth bits)
    if (config.useId) {
      io.input.r.id := U(0, config.idWidth bits)
    }
    if (config.useResp) {
      io.input.r.resp := B(0, 2 bits)
    }
    io.input.r.last := False
    if (config.useRUser) {
      io.input.r.user := B(0, config.rUserWidth bits)
    }

    io.input.aw.ready := False
    io.input.w.ready := False
    io.input.b.valid := False
    if (config.useId) {
      io.input.b.id := U(0, config.idWidth bits)
    }
    if (config.useResp) {
      io.input.b.resp := B(0, 2 bits)
    }
    if (config.useBUser) {
      io.input.b.user := B(0, config.bUserWidth bits)
    }
  }

  case class Subordinate(config: Axi4Config) extends Component {
    val io = new Bundle {
      val output = master(Axi4(config))
    }

    io.output.ar.valid := False
    io.output.ar.addr := U(0, config.addressWidth bits)
    io.output.ar.id := U(0, config.idWidth bits)
    if (config.useRegion) {
      io.output.ar.region := B(0, 4 bits)
    }
    if (config.useLen) {
      io.output.ar.len := U(0, 8 bits)
    }
    if (config.useSize) {
      io.output.ar.size := U(0, 3 bits)
    }
    if (config.useBurst) {
      io.output.ar.burst := B(0, 2 bits)
    }
    if (config.useLock) {
      io.output.ar.lock := B(0, 1 bits)
    }
    if (config.useCache) {
      io.output.ar.cache := B(0, 4 bits)
    }
    if (config.useQos) {
      io.output.ar.qos := B(0, 4 bits)
    }
    if (config.arUserWidth > 0) {
      io.output.ar.user := B(0, config.arUserWidth bits)
    }
    if (config.useProt) {
      io.output.ar.prot := B(0, 3 bits)
    }

    io.output.r.ready := False

    io.output.aw.valid := False
    io.output.aw.addr := U(0, config.addressWidth bits)
    io.output.aw.id := U(0, config.idWidth bits)
    if (config.useRegion) {
      io.output.aw.region := B(0, 4 bits)
    }
    if (config.useLen) {
      io.output.aw.len := U(0, 8 bits)
    }
    if (config.useSize) {
      io.output.aw.size := U(0, 3 bits)
    }
    if (config.useBurst) {
      io.output.aw.burst := B(0, 2 bits)
    }
    if (config.useLock) {
      io.output.aw.lock := B(0, 1 bits)
    }
    if (config.useCache) {
      io.output.aw.cache := B(0, 4 bits)
    }
    if (config.useQos) {
      io.output.aw.qos := B(0, 4 bits)
    }
    if (config.awUserWidth > 0) {
      io.output.aw.user := B(0, config.awUserWidth bits)
    }
    if (config.useProt) {
      io.output.aw.prot := B(0, 3 bits)
    }

    io.output.w.valid := False
    io.output.w.data := B(0, config.dataWidth bits)
    if (config.useStrb) {
      io.output.w.strb := B(0, config.bytePerWord bits)
    }
    if (config.useWUser) {
      io.output.w.user := B(0, config.wUserWidth bits)
    }
    if (config.useLast) {
      io.output.w.last := False
    }

    io.output.b.ready := False
  }

  object Router {
    def apply(config: Axi4Config) = new Router(config)
    def apply(config: Axi4Config, input: Axi4, output: Axi4) = {
      val plug = Router(config)
      plug.io.input <> output
      plug.io.output <> input
    }

    case class Router(config: Axi4Config) extends Component {
      val io = new Bundle {
        val input = slave(Axi4(config))
        val output = master(Axi4(config))
      }

      val manager = Manager(config)
      manager.io.input <> io.input

      val subordinate = Subordinate(config)
      subordinate.io.output <> io.output
    }
  }
}
