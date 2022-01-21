/*
 * Copyright (c) 2021 Phytec Messtechnik GmbH
 */

package zibal.misc

import java.io._
import spinal.core._
import zibal.blackboxes.ihp.sg13s._
import scala.collection.mutable.Map


object CadenceTools {

  case class Sdc(config: ElementsConfig.ElementsConfig) {
    val clocks = Map[String, Float]()

    def addClock(pin: Bool, frequency: HertzNumber) = {
      val time = (frequency.toTime.toBigDecimal / 1.0e-9).floatValue()
      val name = pin.getName()
      clocks += name -> time
    }

    def generate(path: String) = {
      val file = s"${path}${config.className}.sdc"
      val writer = new PrintWriter(new File(file))

      writer.write("""set sdc_version 2.0
set_units -capacitance 1000fF
set_units -time 1000ps
current_design ${TOP}

""")

      clocks.foreach { case (name, period) =>
        val halfPeriod = period / 2
        writer.write(s"""create_clock -period ${period} -name "${name}" -waveform {0.0 ${halfPeriod}} [get_ports ${name}]\n""")
      }

      writer.close()
    }
  }

  case class Io(config: ElementsConfig.ElementsConfig) {

    val pads = Map(
      "top" -> Map[Int, (String, String)](),
      "right" -> Map[Int, (String, String)](),
      "bottom" -> Map[Int, (String, String)](),
      "left" -> Map[Int, (String, String)]()
    )
    val counters = Map[String, Int]()

    val corners = Map[String, (String, String, String)]()

    def addPad(edge: String, number: Int, cell: String) = {
      val name = s"${cell}_${edge}_${number}"
      pads(edge) += (number -> (name, cell))
    }

    def addCorner(corner: String, orientation: Int, cell: String) = {
      val name = s"${cell}_${corner}"
      corners += corner -> (name, cell, s"R$orientation")
    }

    def dumpEdge(writer: PrintWriter, edge: String) = {
      writer.write(s"    ($edge\n")
      if (pads.contains(edge)) {
        pads(edge).toSeq.sortBy(_._1).foreach { pad =>
          val data = pad._2
          writer.write(s"""        (inst name="${data._1}" cell="${data._2}")\n""")
        }
      }
      writer.write("    )\n")
    }

    def dumpCorner(writer: PrintWriter, corner: String) = {
      writer.write(s"    ($corner\n")
      if (corners.contains(corner)) {
        val data = corners(corner)
        writer.write(s"""        (inst name="${data._1}" cell="${data._2}" orientation="${data._3}")\n""")
      }
      writer.write("    )\n")
    }


    def generate(io: Data, path: String) = {
      val file = s"${path}${config.className}.io"
      val writer = new PrintWriter(new File(file))

      io.component.getOrdredNodeIo.foreach { baseType =>
        val instance = baseType.parent.asInstanceOf[IhpCmosIo.IhpCmosIo]
        if (counters.contains(instance.cell)) {
          counters(instance.cell) = counters(instance.cell) + 1
        } else {
          counters(instance.cell) = 1
        }
        val count = counters(instance.cell)
        val name = s"${instance.cell}_${count}"
        pads(instance.edge_) += (instance.number_ -> (name, instance.cell))
      }

      writer.write("""(globals
    version = 3
    io_order = default
)
(iopad
""")

      dumpEdge(writer, "top")
      dumpCorner(writer, "topright")
      dumpEdge(writer, "right")
      dumpCorner(writer, "bottomright")
      dumpEdge(writer, "bottom")
      dumpCorner(writer, "bottomleft")
      dumpEdge(writer, "left")
      dumpCorner(writer, "topleft")

      writer.write(")\n")
      writer.close()
    }
  }
}

