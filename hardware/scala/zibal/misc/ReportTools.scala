// SPDX-FileCopyrightText: 2026 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal.misc

import java.io._
import spinal.core._
import scala.collection.mutable.ArrayBuffer
import spinal.lib.bus.tilelink.{Bus => TileLinkBus}
import spinal.lib.bus.misc.SizeMapping
import spinal.lib.io.TriState

import nafarr.peripherals.PeripheralsComponent
import nafarr.blackboxes.ihp.common.{IhpPowerIo, Edge}

import zibal.platform.PlatformComponent

object ReportTools {

  case class PadEntry(
      edge: String,
      number: Int,
      name: String,
      padType: String,
      clockGroup: String
  )

  case class Report(
      platform: PlatformComponent,
      elementsConfig: ElementsConfig.ElementsConfig
  ) {
    val buildPath = elementsConfig.zibalBuildPath + "reports/"
    var pads = Seq[PadEntry]()

    def extractPads(io: Data, power: Seq[Any] = Nil): Unit = {
      val result = ArrayBuffer[PadEntry]()

      io.component.getOrdredNodeIo.foreach { baseType =>
        val name = baseType.getName()
        baseType.parent match {
          case inst: nafarr.blackboxes.ihp.sg13g2.IhpCmosIo.IhpCmosIo =>
            result += PadEntry(inst.edge.toString, inst.number, name, inst.cell, inst.clockGroup)
          case inst: nafarr.blackboxes.ihp.sg13cmos5l.IhpCmosIo.IhpCmosIo =>
            result += PadEntry(inst.edge.toString, inst.number, name, inst.cell, inst.clockGroup)
          case _ =>
        }
      }

      for (p <- power) {
        p match {
          case inst: IhpPowerIo.IhpPowerIo =>
            result += PadEntry(
              inst.edge.toString,
              inst.number,
              inst.definitionName,
              inst.cell.cellType.toString,
              ""
            )
          case _ =>
        }
      }

      pads = result.toSeq
    }

    def pinmuxRst(): String = {
      val inputs = platform.pinmuxInputs
      val mapping = platform.pinmuxMapping

      val nameByIndex = inputs.map { case (name, (index, _)) => index -> name }.toMap

      val maxOptions = if (mapping.nonEmpty) mapping.map(_._2.size).max else 0

      val header = Seq("Pin") ++ (0 until maxOptions).map(i => s"Option $i")
      val rows = mapping.sortBy(_._1).map { case (pin, indices) =>
        val options = indices.map(i => nameByIndex.getOrElse(i, s"?($i)"))
        Seq(pin.toString) ++ options ++ Seq.fill(maxOptions - options.size)("")
      }

      rstTable(header, rows)
    }

    def interruptRst(): String = {
      val periphBase = platform.tileLinkMapping
      val irqs = platform.irqMapping

      val rows = irqs.zipWithIndex.map { case (signal, index) =>
        val name = findPeripheralName(signal, periphBase)
        Seq(index.toString, name)
      }

      rstTable(Seq("IRQ", "Source"), rows)
    }

    def errorRst(): String = {
      val periphBase = platform.tileLinkMapping
      val errors = platform.errorMapping

      val rows = errors.zipWithIndex.map { case (signal, index) =>
        val name = findPeripheralName(signal, periphBase)
        Seq(index.toString, name)
      }

      rstTable(Seq("Error", "Source"), rows)
    }

    def padRst(pads: Seq[PadEntry]): String = {
      val sorted = pads.sortBy(p => (p.edge, p.number))
      val rows = sorted.map { p =>
        Seq(p.edge, p.number.toString, p.name, p.padType, p.clockGroup)
      }

      rstTable(Seq("Edge", "Number", "Name", "Type", "Clock Group"), rows)
    }

    def generateAll(): Unit = {
      new File(buildPath).mkdirs()

      writeFile("pinmux.rst", pinmuxRst())
      writeFile("interrupts.rst", interruptRst())
      writeFile("errors.rst", errorRst())
      if (pads.nonEmpty) {
        writeFile("pinout.rst", padRst(pads))
      }

    }

    def generateJson(): Unit = {
      new File(buildPath).mkdirs()

      val inputs = platform.pinmuxInputs
      val mapping = platform.pinmuxMapping
      val nameByIndex = inputs.map { case (name, (index, _)) => index -> name }.toMap

      val sb = new StringBuilder
      sb.append("{\n")

      // Pinmux
      sb.append("  \"pinmux\": [\n")
      val pinmuxEntries = mapping.sortBy(_._1).map { case (pin, indices) =>
        val options = indices.map(i => s""""${nameByIndex.getOrElse(i, s"?($i)")}"""")
        s"""    {"pin": $pin, "options": [${options.mkString(", ")}]}"""
      }
      sb.append(pinmuxEntries.mkString(",\n"))
      sb.append("\n  ],\n")

      // Interrupts
      sb.append("  \"interrupts\": [\n")
      val irqEntries = platform.irqMapping.zipWithIndex.map { case (signal, index) =>
        val name = findPeripheralName(signal, platform.tileLinkMapping)
        s"""    {"irq": $index, "source": "$name"}"""
      }
      sb.append(irqEntries.mkString(",\n"))
      sb.append("\n  ],\n")

      // Errors
      sb.append("  \"errors\": [\n")
      val errorEntries = platform.errorMapping.zipWithIndex.map { case (signal, index) =>
        val name = findPeripheralName(signal, platform.tileLinkMapping)
        s"""    {"error": $index, "source": "$name"}"""
      }
      sb.append(errorEntries.mkString(",\n"))
      sb.append("\n  ]")

      // Pads
      if (pads.nonEmpty) {
        sb.append(",\n  \"pads\": [\n")
        val padEntries = pads.sortBy(p => (p.edge, p.number)).map { p =>
          s"""    {"edge": "${p.edge}", "number": ${p.number}, "name": "${p.name}", "type": "${p.padType}", "clock_group": "${p.clockGroup}"}"""
        }
        sb.append(padEntries.mkString(",\n"))
        sb.append("\n  ]")
      }

      sb.append("\n}\n")

      writeFile("report.json", sb.toString)
    }

    private def findPeripheralName(
        signal: Bool,
        mapping: ArrayBuffer[(TileLinkBus, SizeMapping)]
    ): String = {
      val component = signal.component
      walkParents(component, mapping)
    }

    private def walkParents(
        component: Component,
        mapping: ArrayBuffer[(TileLinkBus, SizeMapping)]
    ): String = {
      for ((bus, _) <- mapping) {
        val parent = bus.parent.component
        if (parent == component) {
          return componentName(parent)
        }
      }
      val parent = component.parent
      if (parent != null) {
        for ((bus, _) <- mapping) {
          val busParent = bus.parent.component
          if (busParent == parent) {
            return componentName(parent)
          }
        }
      }
      componentName(component)
    }

    private def componentName(c: Component): String = {
      val raw = Option(c.getName()).filter(_.nonEmpty).getOrElse(c.getClass.getSimpleName)
      val idx = raw.indexOf('_')
      if (idx >= 0) raw.substring(idx + 1) else raw
    }

    private def writeFile(filename: String, content: String): Unit = {
      val writer = new PrintWriter(new File(s"$buildPath$filename"))
      writer.write(content)
      writer.close()
      SpinalInfo(s"Generating $filename")
    }

    private def rstTable(header: Seq[String], rows: Seq[Seq[String]]): String = {
      val allRows = Seq(header) ++ rows
      val colCount = header.size
      val widths = (0 until colCount).map { col =>
        allRows.map(row => if (col < row.size) row(col).length else 0).max
      }

      val separator = widths.map(w => "=" * math.max(w, 1)).mkString("  ")
      val sb = new StringBuilder

      sb.append(separator).append("\n")
      sb.append(formatRow(header, widths)).append("\n")
      sb.append(separator).append("\n")
      for (row <- rows) {
        sb.append(formatRow(row, widths)).append("\n")
      }
      sb.append(separator).append("\n")

      sb.toString
    }

    private def formatRow(row: Seq[String], widths: Seq[Int]): String = {
      row.zipWithIndex
        .map { case (cell, i) =>
          val w = if (i < widths.size) widths(i) else cell.length
          if (i == row.size - 1) cell else cell.padTo(w, ' ')
        }
        .mkString("  ")
    }
  }
}
