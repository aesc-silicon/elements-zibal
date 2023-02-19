package nafarr.networkonchip.amba4.axi

import spinal.core._
import spinal.lib._
import spinal.lib.bus.amba4.axi._
import spinal.lib.bus.amba3.apb._

case class Axi4ChipletID(
    coreConfig: Axi4Config,
    nocConfig: Axi4Config,
    apb3Config: Apb3Config
) extends Component {

  val io = new Bundle {
    val fromCore = new Area {
      val input = slave(Axi4(coreConfig))
      val output = master(Axi4(nocConfig))
    }
    val fromNoc = new Area {
      val input = slave(Axi4(nocConfig))
      val output = master(Axi4(coreConfig))
    }
    val bus = slave(Apb3(apb3Config))
  }

  val locked = RegInit(False)
  val id = Reg(UInt(chipletIdWidth bits)).init(U(0, chipletIdWidth bits))
  val busInterface = new Area {
    val factory = Apb3SlaveFactory(io.bus)

    factory.read(locked, 0x0)
    factory.read(id, 0x4)

    factory.onWrite(0x0) {
      locked := True
    }
    val tmpId = UInt(10 bits)
    tmpId := U(0, 10 bits)
    factory.write(tmpId, 0x4)
    factory.onWrite(0x4) {
      when(!locked) {
        id := tmpId
      }
    }
  }

  /* From core to NOC */

  io.fromCore.output.ar.valid <> io.fromCore.input.ar.valid
  io.fromCore.output.ar.ready <> io.fromCore.input.ar.ready
  io.fromCore.output.ar.addr <> io.fromCore.input.ar.addr
  io.fromCore.output.ar.id := (id ## io.fromCore.input.ar.id).asUInt
  if (nocConfig.useRegion) {
    io.fromCore.output.ar.region <> io.fromCore.input.ar.region
  }
  if (nocConfig.useLen) {
    io.fromCore.output.ar.len <> io.fromCore.input.ar.len
  }
  if (nocConfig.useSize) {
    io.fromCore.output.ar.size <> io.fromCore.input.ar.size
  }
  if (nocConfig.useBurst) {
    io.fromCore.output.ar.burst <> io.fromCore.input.ar.burst
  }
  if (nocConfig.useLock) {
    io.fromCore.output.ar.lock <> io.fromCore.input.ar.lock
  }
  if (nocConfig.useCache) {
    io.fromCore.output.ar.cache <> io.fromCore.input.ar.cache
  }
  if (nocConfig.useQos) {
    io.fromCore.output.ar.qos <> io.fromCore.input.ar.qos
  }
  if (nocConfig.arUserWidth > 0) {
    io.fromCore.output.ar.user <> io.fromCore.input.ar.user
  }
  if (nocConfig.useProt) {
    io.fromCore.output.ar.prot <> io.fromCore.input.ar.prot
  }

  val wLogging = new Area {
    val fifo = StreamFifo(Bits(10 bits), 10)
    val fire = io.fromCore.input.aw.valid && io.fromCore.output.aw.ready
    fifo.io.push.valid := fire
    fifo.io.push.payload := io.fromCore.input.aw.addr(63 downto 54).asBits

    io.fromCore.input.aw.ready := io.fromCore.output.aw.ready & fifo.io.push.ready
  }
  io.fromCore.output.aw.valid <> io.fromCore.input.aw.valid

  io.fromCore.output.aw.addr <> io.fromCore.input.aw.addr
  io.fromCore.output.aw.id := (id ## io.fromCore.input.aw.id).asUInt
  if (nocConfig.useRegion) {
    io.fromCore.output.aw.region <> io.fromCore.input.aw.region
  }
  if (nocConfig.useLen) {
    io.fromCore.output.aw.len <> io.fromCore.input.aw.len
  }
  if (nocConfig.useSize) {
    io.fromCore.output.aw.size <> io.fromCore.input.aw.size
  }
  if (nocConfig.useBurst) {
    io.fromCore.output.aw.burst <> io.fromCore.input.aw.burst
  }
  if (nocConfig.useLock) {
    io.fromCore.output.aw.lock <> io.fromCore.input.aw.lock
  }
  if (nocConfig.useCache) {
    io.fromCore.output.aw.cache <> io.fromCore.input.aw.cache
  }
  if (nocConfig.useQos) {
    io.fromCore.output.aw.qos <> io.fromCore.input.aw.qos
  }
  if (nocConfig.awUserWidth > 0) {
    io.fromCore.output.aw.user <> io.fromCore.input.aw.user
  }
  if (nocConfig.useProt) {
    io.fromCore.output.aw.prot <> io.fromCore.input.aw.prot
  }

  // AXI4W has no WID signal
  io.fromCore.output.w.valid := io.fromCore.input.w.valid & wLogging.fifo.io.pop.valid
  io.fromCore.output.w.ready <> io.fromCore.input.w.ready
  wLogging.fifo.io.pop.ready := io.fromCore.input.w.valid && io.fromCore.output.w.ready && io.fromCore.input.w.last
  io.fromCore.output.w.user := wLogging.fifo.io.pop.payload
  io.fromCore.output.w.data <> io.fromCore.input.w.data
  io.fromCore.output.w.last <> io.fromCore.input.w.last
  if (nocConfig.useStrb) {
    io.fromCore.output.w.strb <> io.fromCore.input.w.strb
  }

  io.fromCore.input.b.valid <> io.fromCore.output.b.valid
  io.fromCore.input.b.ready <> io.fromCore.output.b.ready
  io.fromCore.input.b.id <> io.fromCore.output.b.id(coreConfig.idWidth - 1 downto 0)
  if (nocConfig.useResp) {
    io.fromCore.input.b.resp <> io.fromCore.output.b.resp
  }

  io.fromCore.input.r.valid <> io.fromCore.output.r.valid
  io.fromCore.input.r.ready <> io.fromCore.output.r.ready
  io.fromCore.input.r.data <> io.fromCore.output.r.data
  io.fromCore.input.r.id <> io.fromCore.output.r.id(coreConfig.idWidth - 1 downto 0)
  io.fromCore.input.r.last <> io.fromCore.output.r.last
  if (nocConfig.useResp) {
    io.fromCore.input.r.resp <> io.fromCore.output.r.resp
  }

  /* From NOC to core */
  val arLogging = new Area {
    val fifo = StreamFifo(Bits(10 bits), 10)
    val fire = io.fromNoc.input.ar.valid && io.fromNoc.output.ar.ready
    fifo.io.push.valid := fire
    fifo.io.push.payload := io.fromNoc.input.ar
      .id(coreConfig.idWidth + 9 downto coreConfig.idWidth)
      .asBits

    io.fromNoc.input.ar.ready := io.fromNoc.output.ar.ready & fifo.io.push.ready
  }
  io.fromNoc.output.ar.valid <> io.fromNoc.input.ar.valid

  io.fromNoc.output.ar.addr <> io.fromNoc.input.ar.addr
  io.fromNoc.output.ar.id := io.fromNoc.input.ar.id(coreConfig.idWidth - 1 downto 0)
  if (coreConfig.useRegion) {
    io.fromNoc.output.ar.region <> io.fromNoc.input.ar.region
  }
  if (coreConfig.useLen) {
    io.fromNoc.output.ar.len <> io.fromNoc.input.ar.len
  }
  if (coreConfig.useSize) {
    io.fromNoc.output.ar.size <> io.fromNoc.input.ar.size
  }
  if (coreConfig.useBurst) {
    io.fromNoc.output.ar.burst <> io.fromNoc.input.ar.burst
  }
  if (coreConfig.useLock) {
    io.fromNoc.output.ar.lock <> io.fromNoc.input.ar.lock
  }
  if (coreConfig.useCache) {
    io.fromNoc.output.ar.cache <> io.fromNoc.input.ar.cache
  }
  if (coreConfig.useQos) {
    io.fromNoc.output.ar.qos <> io.fromNoc.input.ar.qos
  }
  if (coreConfig.arUserWidth > 0) {
    io.fromNoc.output.ar.user <> io.fromNoc.input.ar.user
  }
  if (coreConfig.useProt) {
    io.fromNoc.output.ar.prot <> io.fromNoc.input.ar.prot
  }

  val awLogging = new Area {
    val fifo = StreamFifo(Bits(10 bits), 10)
    val fire = io.fromNoc.input.aw.valid && io.fromNoc.output.aw.ready
    fifo.io.push.valid := fire
    fifo.io.push.payload := io.fromNoc.input.aw
      .id(coreConfig.idWidth + 9 downto coreConfig.idWidth)
      .asBits

    io.fromNoc.input.aw.ready := io.fromNoc.output.aw.ready & fifo.io.push.ready
  }
  io.fromNoc.output.aw.valid <> io.fromNoc.input.aw.valid

  io.fromNoc.output.aw.addr <> io.fromNoc.input.aw.addr
  io.fromNoc.output.aw.id := io.fromNoc.input.aw.id(coreConfig.idWidth - 1 downto 0)
  if (coreConfig.useRegion) {
    io.fromNoc.output.aw.region <> io.fromNoc.input.aw.region
  }
  if (coreConfig.useLen) {
    io.fromNoc.output.aw.len <> io.fromNoc.input.aw.len
  }
  if (coreConfig.useSize) {
    io.fromNoc.output.aw.size <> io.fromNoc.input.aw.size
  }
  if (coreConfig.useBurst) {
    io.fromNoc.output.aw.burst <> io.fromNoc.input.aw.burst
  }
  if (coreConfig.useLock) {
    io.fromNoc.output.aw.lock <> io.fromNoc.input.aw.lock
  }
  if (coreConfig.useCache) {
    io.fromNoc.output.aw.cache <> io.fromNoc.input.aw.cache
  }
  if (coreConfig.useQos) {
    io.fromNoc.output.aw.qos <> io.fromNoc.input.aw.qos
  }
  if (coreConfig.awUserWidth > 0) {
    io.fromNoc.output.aw.user <> io.fromNoc.input.aw.user
  }
  if (coreConfig.useProt) {
    io.fromNoc.output.aw.prot <> io.fromNoc.input.aw.prot
  }

  // AXI4W has no WID signal
  io.fromNoc.output.w.valid <> io.fromNoc.input.w.valid
  io.fromNoc.output.w.ready <> io.fromNoc.input.w.ready
  io.fromNoc.output.w.data <> io.fromNoc.input.w.data
  io.fromNoc.output.w.last <> io.fromNoc.input.w.last
  if (coreConfig.useStrb) {
    io.fromNoc.output.w.strb <> io.fromNoc.input.w.strb
  }

  io.fromNoc.input.b.valid := io.fromNoc.output.b.valid & awLogging.fifo.io.pop.valid
  io.fromNoc.input.b.ready <> io.fromNoc.output.b.ready
  awLogging.fifo.io.pop.ready := io.fromNoc.output.b.valid && io.fromNoc.input.b.ready
  io.fromNoc.input.b.user := awLogging.fifo.io.pop.payload

  io.fromNoc.input.b.id := (id ## io.fromNoc.output.b.id).asUInt
  if (coreConfig.useResp) {
    io.fromNoc.input.b.resp <> io.fromNoc.output.b.resp
  }

  io.fromNoc.input.r.valid := io.fromNoc.output.r.valid & arLogging.fifo.io.pop.valid
  io.fromNoc.input.r.ready <> io.fromNoc.output.r.ready
  arLogging.fifo.io.pop.ready := io.fromNoc.output.r.valid && io.fromNoc.input.r.ready && io.fromNoc.input.r.last
  io.fromNoc.input.r.user := arLogging.fifo.io.pop.payload

  io.fromNoc.input.r.data <> io.fromNoc.output.r.data
  io.fromNoc.input.r.id := (id ## io.fromNoc.output.r.id).asUInt
  io.fromNoc.input.r.last <> io.fromNoc.output.r.last
  if (coreConfig.useResp) {
    io.fromNoc.input.r.resp <> io.fromNoc.output.r.resp
  }
}
