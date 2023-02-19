package nafarr.networkonchip.amba4.axi

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba3.apb._

case class Axi4Blockage(config: Axi4Config) extends Component {
  val io = new Bundle {
    val input = slave(Axi4(config))
    val output = master(Axi4(config))
    val bus = slave(Apb3(apb3Config))
  }

  val blocked = RegInit(True)
  val busInterface = new Area {
    val factory = Apb3SlaveFactory(io.bus)

    factory.read(blocked, 0x0)
    factory.onWrite(0x0) {
      blocked := False
    }
  }

  io.output.ar.addr := io.input.ar.addr
  when(!blocked) {
    io.output.ar.valid := io.input.ar.valid
    io.input.ar.ready := io.output.ar.ready
  } otherwise {
    io.output.ar.valid := False
    io.input.ar.ready := False
  }
  io.output.ar.id <> io.input.ar.id
  if (config.useRegion) {
    io.output.ar.region <> io.input.ar.region
  }
  if (config.useLen) {
    io.output.ar.len <> io.input.ar.len
  }
  if (config.useSize) {
    io.output.ar.size <> io.input.ar.size
  }
  if (config.useBurst) {
    io.output.ar.burst <> io.input.ar.burst
  }
  if (config.useLock) {
    io.output.ar.lock <> io.input.ar.lock
  }
  if (config.useCache) {
    io.output.ar.cache <> io.input.ar.cache
  }
  if (config.useQos) {
    io.output.ar.qos <> io.input.ar.qos
  }
  if (config.arUserWidth > 0) {
    io.output.ar.user <> io.input.ar.user
  }
  if (config.useProt) {
    io.output.ar.prot <> io.input.ar.prot
  }

  io.output.aw.addr := io.input.aw.addr
  when(!blocked) {
    io.output.aw.valid := io.input.aw.valid
  } otherwise {
    io.output.aw.valid := False
  }
  io.output.aw.ready <> io.input.aw.ready
  io.output.aw.id <> io.input.aw.id
  if (config.useRegion) {
    io.output.aw.region <> io.input.aw.region
  }
  if (config.useLen) {
    io.output.aw.len <> io.input.aw.len
  }
  if (config.useSize) {
    io.output.aw.size <> io.input.aw.size
  }
  if (config.useBurst) {
    io.output.aw.burst <> io.input.aw.burst
  }
  if (config.useLock) {
    io.output.aw.lock <> io.input.aw.lock
  }
  if (config.useCache) {
    io.output.aw.cache <> io.input.aw.cache
  }
  if (config.useQos) {
    io.output.aw.qos <> io.input.aw.qos
  }
  if (config.arUserWidth > 0) {
    io.output.aw.user <> io.input.aw.user
  }
  if (config.useProt) {
    io.output.aw.prot <> io.input.aw.prot
  }

  when(!blocked) {
    io.output.w.valid := io.input.w.valid
  } otherwise {
    io.output.w.valid := False
  }
  io.output.w.ready <> io.input.w.ready
  io.output.w.data <> io.input.w.data
  if (config.useStrb) {
    io.output.w.strb := io.input.w.strb
  }
  if (config.useWUser) {
    io.output.w.user := io.input.w.user
  }
  if (config.useLast) {
    io.output.w.last := io.input.w.last
  }

  io.input.r <> io.output.r
  io.input.b <> io.output.b
}
