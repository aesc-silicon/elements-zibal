package nafarr.peripherals.com.chip2chip

import spinal.core._
import spinal.lib._
import spinal.lib.fsm._
import spinal.lib.bus.amba4.axi._

object Frontend {

  case class Axi4Frontend(config: Axi4Config) extends Component {
    require(config.addressWidth == 64)
    require(config.dataWidth % 128 == 0)
    require(config.dataWidth < 1024, "1024 is not supported")

    val addressWidth = 64
    val dataWidth = if (config.dataWidth < 128) 128 else config.dataWidth
    val dataBlocks = (dataWidth / 128) + 1

    val io = new Bundle {
      val axiIn = slave(Axi4(config))
      val axiOut = master(Axi4(config))
      // +1 for "payload contains valid data" flag
      val toLinkLayer = master(Stream(Vec(Bits(128 + 1 bits), dataBlocks)))
      val fromLinkLayer = slave(Stream(Vec(Bits(128 bits), dataBlocks)))
    }

    object LinkLayerChannels extends SpinalEnum() {
      val NONE, AR, AW, W, R, B = newElement()
    }

    def getARBusWidth(config: Axi4Config): Int = {
      var width = config.addressWidth
      if (config.useId) width += config.idWidth
      if (config.useLen) width += 8
      if (config.useSize) width += 3
      if (config.useBurst) width += 2
      if (config.useLock) width += 1
      if (config.useCache) width += 4
      if (config.useProt) width += 3
      if (config.useQos) width += 4
      if (config.useRegion) width += 4
      if (config.useArUser) width += config.arUserWidth
      width
    }

    def getAWBusWidth(config: Axi4Config): Int = {
      var width = config.addressWidth
      if (config.useId) width += config.idWidth
      if (config.useLen) width += 8
      if (config.useSize) width += 3
      if (config.useBurst) width += 2
      if (config.useLock) width += 1
      if (config.useCache) width += 4
      if (config.useProt) width += 3
      if (config.useQos) width += 4
      if (config.useRegion) width += 4
      if (config.useAwUser) width += config.awUserWidth
      width
    }

    def getWBusWidth(config: Axi4Config): Int = {
      var width = 0
      if (config.useStrb) width += config.bytePerWord
      if (config.useLast) width += 1
      if (config.useWUser) width += config.wUserWidth
      width
    }

    def getRBusWidth(config: Axi4Config): Int = {
      var width = 0
      if (config.useId) width += config.idWidth
      if (config.useResp) width += 2
      if (config.useLast) width += 1
      if (config.useRUser) width += config.rUserWidth
      width
    }

    def getBBusWidth(config: Axi4Config): Int = {
      var width = 0
      if (config.useId) width += config.idWidth
      if (config.useResp) width += 2
      if (config.useBUser) width += config.bUserWidth
      width
    }

    val input = new Area {
      val ar = new Area {
        val id = if (config.useId) io.axiIn.ar.id else B""
        val len = if (config.useLen) io.axiIn.ar.len else B""
        val size = if (config.useSize) io.axiIn.ar.size else B""
        val burst = if (config.useBurst) io.axiIn.ar.burst else B""
        val lock = if (config.useLock) io.axiIn.ar.lock else B""
        val cache = if (config.useCache) io.axiIn.ar.cache else B""
        val prot = if (config.useProt) io.axiIn.ar.prot else B""
        val qos = if (config.useQos) io.axiIn.ar.qos else B""
        val region = if (config.useRegion) io.axiIn.ar.region else B""
        val user = if (config.useArUser) io.axiIn.ar.user else B""

        val fillZeros = B((128 - getARBusWidth(config) - 3) bits, default -> False)
        val concat = LinkLayerChannels.AR ## fillZeros ## user ## region ## qos ## prot ##
          cache ## lock ## burst ## size ## len ## id ## io.axiIn.ar.addr
      }

      val aw = new Area {
        val id = if (config.useId) io.axiIn.aw.id else B""
        val len = if (config.useLen) io.axiIn.aw.len else B""
        val size = if (config.useSize) io.axiIn.aw.size else B""
        val burst = if (config.useBurst) io.axiIn.aw.burst else B""
        val lock = if (config.useLock) io.axiIn.aw.lock else B""
        val cache = if (config.useCache) io.axiIn.aw.cache else B""
        val prot = if (config.useProt) io.axiIn.aw.prot else B""
        val qos = if (config.useQos) io.axiIn.aw.qos else B""
        val region = if (config.useRegion) io.axiIn.aw.region else B""
        val user = if (config.useAwUser) io.axiIn.aw.user else B""

        val fillZeros = B((128 - getAWBusWidth(config) - 3) bits, default -> False)
        val concat = LinkLayerChannels.AW ## fillZeros ## user ## region ## qos ## prot ##
          cache ## lock ## burst ## size ## len ## id ## io.axiIn.aw.addr
      }

      val w = new Area {
        val strb = if (config.useStrb) io.axiIn.w.strb else B""
        val user = if (config.useWUser) io.axiIn.w.user else B""
        val last = if (config.useLast) io.axiIn.w.last else B""

        val fillZeros = B((128 - getWBusWidth(config) - 3) bits, default -> False)
        val concat = LinkLayerChannels.W ## fillZeros ## last ## user ## strb
      }

      val r = new Area {
        val id = if (config.useId) io.axiOut.r.id else B""
        val resp = if (config.useResp) io.axiOut.r.resp else B""
        val user = if (config.useRUser) io.axiOut.r.user else B""
        val last = if (config.useLast) io.axiOut.r.last else B""

        val fillZeros = B((128 - getRBusWidth(config) - 3) bits, default -> False)
        val concat = LinkLayerChannels.R ## fillZeros ## last ## user ## resp ## id
      }

      val b = new Area {
        val id = if (config.useId) io.axiOut.b.id else B""
        val resp = if (config.useResp) io.axiOut.b.resp else B""
        val user = if (config.useBUser) io.axiOut.b.user else B""

        val fillZeros = B((128 - getBBusWidth(config) - 3) bits, default -> False)
        val concat = LinkLayerChannels.B ## fillZeros ## user ## resp ## id
      }

      // Priorities for incoming AXI channels: B > AW > W > R > AR
      val decider = new StateMachine {
        val lockChannel = Reg(LinkLayerChannels()).init(LinkLayerChannels.NONE)
        val payload = Vec(Bits(128 + 1 bits), dataBlocks)

        io.axiIn.ar.ready := False
        io.axiIn.aw.ready := False
        io.axiIn.w.ready := False
        io.axiOut.r.ready := False
        io.axiOut.b.ready := False

        for (index <- 0 until dataBlocks) {
          payload(index) := B(0, 128 + 1 bits)
        }
        io.toLinkLayer.payload := payload
        io.toLinkLayer.valid := False

        val prevAW = RegInit(False)

        val init: State = new State with EntryPoint {
          whenIsActive {
            when(io.axiOut.b.valid) {
              lockChannel := LinkLayerChannels.B
              goto(send)
            } elsewhen (io.axiIn.aw.valid && !prevAW) {
              lockChannel := LinkLayerChannels.AW
              prevAW := True
              goto(send)
            } elsewhen (io.axiIn.w.valid) {
              lockChannel := LinkLayerChannels.W
              prevAW := False
              goto(send)
            } elsewhen (io.axiOut.r.valid) {
              lockChannel := LinkLayerChannels.R
              goto(send)
            } elsewhen (io.axiIn.ar.valid) {
              lockChannel := LinkLayerChannels.AR
              goto(send)
            }
          }
        }
        val send: State = new State {
          whenIsActive {
            io.toLinkLayer.valid := True
            switch(lockChannel) {
              is(LinkLayerChannels.AR) {
                io.axiIn.ar.ready := io.toLinkLayer.ready
                payload(0) := B"1" ## ar.concat
              }
              is(LinkLayerChannels.AW) {
                io.axiIn.aw.ready := io.toLinkLayer.ready
                payload(0) := B"1" ## aw.concat
              }
              is(LinkLayerChannels.B) {
                io.axiOut.b.ready := io.toLinkLayer.ready
                payload(0) := B"1" ## b.concat
              }
              is(LinkLayerChannels.W) {
                io.axiIn.w.ready := io.toLinkLayer.ready
                payload(0) := B"1" ## w.concat
                for (index <- 1 until dataBlocks) {
                  payload(index) := B"1" ## io.axiIn.w.data.subdivideIn(128 bits)(index - 1)
                }
              }
              is(LinkLayerChannels.R) {
                io.axiOut.r.ready := io.toLinkLayer.ready
                payload(0) := B"1" ## r.concat
                for (index <- 1 until dataBlocks) {
                  payload(index) := B"1" ## io.axiOut.r.data.subdivideIn(128 bits)(index - 1)
                }
              }
            }
            when(io.toLinkLayer.ready) {
              goto(init)
            }
          }
        }
      }
    }

    val output = new Area {

      def getChannel(data: Bits) = {
        val channel = LinkLayerChannels()
        switch(data(127 downto 125)) {
          is(LinkLayerChannels.AR.asBits) {
            channel := LinkLayerChannels.AR
          }
          is(LinkLayerChannels.AW.asBits) {
            channel := LinkLayerChannels.AW
          }
          is(LinkLayerChannels.W.asBits) {
            channel := LinkLayerChannels.W
          }
          is(LinkLayerChannels.R.asBits) {
            channel := LinkLayerChannels.R
          }
          is(LinkLayerChannels.B.asBits) {
            channel := LinkLayerChannels.B
          }
          default {
            channel := LinkLayerChannels.NONE
          }
        }
        channel
      }

      def getArChannel(data: Bits): Axi4Ar = {
        val channel = Axi4Ar(config)
        var width = 0
        channel.addr := data(64 - 1 + width downto width).asUInt
        width = width + 64
        if (config.useId) {
          channel.id := data(config.idWidth - 1 + width downto width).asUInt
          width = width + config.idWidth
        }
        if (config.useLen) {
          channel.len := data(8 - 1 + width downto width).asUInt
          width = width + 8
        }
        if (config.useSize) {
          channel.size := data(3 - 1 + width downto width).asUInt
          width = width + 3
        }
        if (config.useBurst) {
          channel.burst := data(2 - 1 + width downto width)
          width = width + 2
        }
        if (config.useLock) {
          channel.lock := data(1 - 1 + width downto width)
          width = width + 1
        }
        if (config.useCache) {
          channel.cache := data(4 - 1 + width downto width)
          width = width + 4
        }
        if (config.useProt) {
          channel.prot := data(3 - 1 + width downto width)
          width = width + 3
        }
        if (config.useQos) {
          channel.qos := data(4 - 1 + width downto width)
          width = width + 4
        }
        if (config.useRegion) {
          channel.region := data(4 - 1 + width downto width)
          width = width + 4
        }
        if (config.useArUser) {
          channel.user := data(config.arUserWidth - 1 + width downto width)
          width = width + config.arUserWidth
        }
        channel
      }

      def getAwChannel(data: Bits): Axi4Aw = {
        val channel = Axi4Aw(config)
        var width = 0
        channel.addr := data(64 - 1 + width downto width).asUInt
        width = width + 64
        if (config.useId) {
          channel.id := data(config.idWidth - 1 + width downto width).asUInt
          width = width + config.idWidth
        }
        if (config.useLen) {
          channel.len := data(8 - 1 + width downto width).asUInt
          width = width + 8
        }
        if (config.useSize) {
          channel.size := data(3 - 1 + width downto width).asUInt
          width = width + 3
        }
        if (config.useBurst) {
          channel.burst := data(2 - 1 + width downto width)
          width = width + 2
        }
        if (config.useLock) {
          channel.lock := data(1 - 1 + width downto width)
          width = width + 1
        }
        if (config.useCache) {
          channel.cache := data(4 - 1 + width downto width)
          width = width + 4
        }
        if (config.useProt) {
          channel.prot := data(3 - 1 + width downto width)
          width = width + 3
        }
        if (config.useQos) {
          channel.qos := data(4 - 1 + width downto width)
          width = width + 4
        }
        if (config.useRegion) {
          channel.region := data(4 - 1 + width downto width)
          width = width + 4
        }
        if (config.useAwUser) {
          channel.user := data(config.awUserWidth - 1 + width downto width)
          width = width + config.awUserWidth
        }
        channel
      }

      def getWChannel(data: Bits, payload: Bits): Axi4W = {
        val channel = Axi4W(config)
        var width = 0
        if (config.useStrb) {
          channel.strb := data(config.bytePerWord - 1 + width downto width)
          width = width + config.bytePerWord
        }
        if (config.useWUser) {
          channel.user := data(config.wUserWidth - 1 + width downto width)
          width = width + config.bUserWidth
        }
        if (config.useLast) {
          channel.last := data(width)
        }
        channel.data := payload
        channel
      }

      def getRChannel(data: Bits, payload: Bits): Axi4R = {
        val channel = Axi4R(config)
        var width = 0
        if (config.useId) {
          channel.id := data(config.idWidth - 1 + width downto width).asUInt
          width = width + config.idWidth
        }
        if (config.useResp) {
          channel.resp := data(2 - 1 + width downto width)
          width = width + 2
        }
        if (config.useRUser) {
          channel.user := data(config.rUserWidth - 1 + width downto width)
          width = width + config.bUserWidth
        }
        if (config.useLast) {
          channel.last := data(width)
        }
        channel.data := payload
        channel
      }

      def getBChannel(data: Bits): Axi4B = {
        val channel = Axi4B(config)
        var width = 0
        if (config.useId) {
          channel.id := data(config.idWidth - 1 + width downto width).asUInt
          width = width + config.idWidth
        }
        if (config.useResp) {
          channel.resp := data(2 - 1 + width downto width)
          width = width + 2
        }
        if (config.useBUser) {
          channel.user := data(config.bUserWidth - 1 + width downto width)
          width = width + config.bUserWidth
        }
        channel
      }

      val decider = new Area {
        val payload = Bits(config.dataWidth bits)
        for (index <- 0 until dataBlocks - 1) {
          payload(128 + (128 * index) - 1 downto (128 * index)) := io.fromLinkLayer.payload(
            index + 1
          )
        }
        val header = io.fromLinkLayer.payload(0)
        val channel = getChannel(header)

        io.axiOut.ar.payload := getArChannel(header)
        io.axiOut.aw.payload := getAwChannel(header)
        io.axiOut.w.payload := getWChannel(header, payload)
        io.axiIn.r.payload := getRChannel(header, payload)
        io.axiIn.b.payload := getBChannel(header)

        io.fromLinkLayer.ready := False
        io.axiOut.ar.valid := False
        io.axiOut.aw.valid := False
        io.axiOut.w.valid := False
        io.axiIn.b.valid := False
        io.axiIn.r.valid := False

        when(io.fromLinkLayer.valid) {
          switch(channel) {
            is(LinkLayerChannels.AR) {
              io.axiOut.ar.valid <> io.fromLinkLayer.valid
              io.fromLinkLayer.ready <> io.axiOut.ar.ready
            }
            is(LinkLayerChannels.R) {
              io.axiIn.r.valid <> io.fromLinkLayer.valid
              io.fromLinkLayer.ready <> io.axiIn.r.ready
            }
            is(LinkLayerChannels.AW) {
              io.axiOut.aw.valid <> io.fromLinkLayer.valid
              io.fromLinkLayer.ready <> io.axiOut.aw.ready
            }
            is(LinkLayerChannels.W) {
              io.axiOut.w.valid <> io.fromLinkLayer.valid
              io.fromLinkLayer.ready <> io.axiOut.w.ready
            }
            is(LinkLayerChannels.B) {
              io.axiIn.b.valid <> io.fromLinkLayer.valid
              io.fromLinkLayer.ready <> io.axiIn.b.ready
            }
          }
        }
      }
    }
  }
}
