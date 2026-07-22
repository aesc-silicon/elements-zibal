// SPDX-FileCopyrightText: 2026 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal.misc

import java.io._
import spinal.core._
import nafarr.blackboxes.ihp.{sg13g2, sg13cmos5l}
import nafarr.blackboxes.ihp.common._
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

object LibreLaneTools {

  type IhpCmosIoSg13g2 = sg13g2.IhpCmosIo.IhpCmosIo
  type IhpCmosIoSg13cmos5l = sg13cmos5l.IhpCmosIo.IhpCmosIo

  case class PDKTech(tech: String, x: Double, y: Double)

  object PDKs {
    object IHP {
      val sg13g2 = PDKTech("sg13g2", 0.48, 3.78)
      val sg13cmos5l = PDKTech("sg13cmos5l", 0.48, 3.78)
    }
  }

  object Flow {
    val Classic = "Classic" // IP block hardening
    val Chip = "Chip" // top-level padframe assembly
    val Both = Set(Classic, Chip)
  }

  case class StepSubstitution(id: String, replacement: String, flows: Set[String])

  def divisible(a: Double, b: Double): Boolean = ((a / b) % 1) < 0.0001

  case class Config(
      config: ElementsConfig.ElementsConfig,
      platform: PDKTech,
      isBlock: Boolean = false
  ) {

    private var _dieArea: Tuple4[Double, Double, Double, Double] = (0, 0, 0, 0)
    private var _coreArea: Tuple4[Double, Double, Double, Double] = (0, 0, 0, 0)
    var hasSdc: Boolean = true
    var hasPdn: Boolean = true
    var hasCustomPdn: Boolean = true
    var hasIoRing: Boolean = false
    var runMagicDrc: Boolean = false
    var io: Option[Data] = None
    var ioPower: Option[Seq[IhpPowerIo.IhpPowerIo]] = None
    var metaVersion: Int = 3
    var placeDensity: Double = 0.75
    var ioPercentage: Double = 0.2
    var maxRoutingLayer: String =
      if (isBlock) "Metal5"
      else if (platform.tech == "sg13g2") "TopMetal2"
      else "TopMetal1"
    var pdnRingWidth: Double = 5.0
    var pdnRingSpace: Double = 2.0
    var pdnRingCoreOffset: Double = 4.5
    var pdnMetal4Pitch: Double = 40.0
    var pdnMetal5Pitch: Double = 40.0
    var pdnTopMetal1Pitch: Double = 60.0
    var pdnTopMetal2Pitch: Double = 60.0
    var pdnVLayer: String = if (isBlock) "Metal4" else "TopMetal1"
    var pdnHLayer: String = if (isBlock) "Metal5" else "TopMetal2"
    val additionalVerilogFiles = ArrayBuffer[String]()

    val clocks = ArrayBuffer[(String, Float, String)]()
    val resetPorts = ArrayBuffer[String]()
    val blocks = ArrayBuffer[(String, String, Double, Double, String)]()
    val lvsIgnoreCells = ArrayBuffer[String]()
    val disabledSteps = ArrayBuffer[String]()
    val substitutingSteps = ArrayBuffer[StepSubstitution](
      StepSubstitution("KLayout.Filler", "Gdsfill.Filler", Set(Flow.Chip))
    )
    val lintDisableWarnings = ArrayBuffer[String]("TIMESCALEMOD")
    val pads = Map(
      Edge.North -> Map[Int, String](),
      Edge.East -> Map[Int, String](),
      Edge.South -> Map[Int, String](),
      Edge.West -> Map[Int, String]()
    )

    def getCompName(comp: Component): String = {
      if (comp.getName().equals("toplevel"))
        return ""
      return getCompName(comp.parent) + "." + comp.getName()
    }

    def addClock(pin: Bool, frequency: HertzNumber, group: String = "") = {
      val time = (frequency.toTime.toBigDecimal / 1.0e-9).floatValue()
      val cell =
        if (pin.parent != null) {
          pin.parent match {
            case instance: IhpCmosIoSg13g2 => instance.cellName
            case instance: IhpCmosIoSg13cmos5l => instance.cellName
            case _ => ""
          }
        } else ""
      clocks += ((pin.getName(), time, cell))
    }

    def addReset(pin: Bool) = {
      resetPorts += pin.getName()
    }

    def addBlock(
        macroComp: Component,
        blockName: String,
        x: Double,
        y: Double,
        orientation: String = "N"
    ) = {
      val compName = getCompName(macroComp).substring(1).split('.').takeRight(2).mkString(".")
      blocks += ((compName, blockName, x, y, orientation))
    }

    private val librelanePath: String = s"${config.zibalBuildPath}librelane/"

    private def blockFinal(blockName: String): String =
      s"${config.buildPath}librelane/${blockName}/final"

    private def libCorners: Seq[String] =
      Seq("nom_typ_1p20V_25C", "nom_fast_1p32V_m40C", "nom_slow_1p08V_125C")

    def generate: Unit = generate(config.className)
    def generate(designName: String): Unit = {
      val directory = new File(librelanePath)
      if (!directory.exists()) {
        directory.mkdirs()
        SpinalInfo(s"Creating path ${librelanePath}")
      }
      generateYaml(designName)
      if (hasSdc) {
        generateSdc(designName)
      }
      if (hasPdn && hasCustomPdn) {
        generatePdn(designName)
      }
      if (!isBlock) {
        generateBuildOrder(designName)
      }
    }

    def generateBuildOrder(designName: String): Unit = {
      val filename = "build-order.txt"
      val writer = new PrintWriter(new File(s"${librelanePath}${filename}"))
      SpinalInfo(s"Generating ${filename}")
      blocks.map(_._2).distinct.foreach { blockName =>
        writer.write(s"${librelanePath}${blockName}.yaml\n")
      }
      writer.write(s"${librelanePath}${designName}.yaml\n")
      writer.close()
    }

    private def sdcPath(designName: String): String =
      s"${librelanePath}${designName}.sdc"

    private def pdnPath(designName: String): String =
      s"${librelanePath}${designName}.pdn.tcl"

    def generatePdn(designName: String): Unit = {
      val writer = new PrintWriter(new File(pdnPath(designName)))
      SpinalInfo(s"Generating ${designName}.pdn.tcl")
      writer.write("add_global_connection -net {VDD} -pin_pattern {^VDD$} -power\n")
      writer.write("add_global_connection -net {VDD} -pin_pattern {^VDDPE$}\n")
      writer.write("add_global_connection -net {VDD} -pin_pattern {^VDDCE$}\n")
      writer.write("add_global_connection -net {VSS} -pin_pattern {^VSS$} -ground\n")
      writer.write("add_global_connection -net {VSS} -pin_pattern {^VSSE$}\n")
      writer.write(
        "add_global_connection -net {VDD} -inst_pattern {.*} -pin_pattern {^VDD$} -power\n"
      )
      writer.write(
        "add_global_connection -net {VSS} -inst_pattern {.*} -pin_pattern {^VSS$} -ground\n"
      )
      writer.write("global_connect\n")
      writer.write("set_voltage_domain -name {CORE} -power {VDD} -ground {VSS}\n")
      if (isBlock) {
        generatePdnBlock(writer)
      } else if (platform.tech == "sg13g2") {
        generatePdnMainG2(writer)
      } else {
        generatePdnMainCMOS5L(writer)
      }
      writer.close()
    }

    private def generatePdnBlock(writer: PrintWriter): Unit = {
      writer.write("define_pdn_grid -name {grid} -voltage_domains {CORE} -pins {Metal4 Metal5}\n")
      writer.write(
        "add_pdn_stripe -grid {grid} -layer {Metal1} -width {0.44} -pitch {7.56} -offset {0} -followpins -extend_to_core_ring\n"
      )
      writer.write(
        s"add_pdn_ring -grid {grid} -layers {Metal4 Metal5} -widths {${pdnRingWidth}} -spacings {${pdnRingSpace}} -core_offsets {${pdnRingCoreOffset}}\n"
      )
      writer.write(
        s"add_pdn_stripe -grid {grid} -layer {Metal4} -width {2.0} -pitch {${pdnMetal4Pitch}} -offset {10.0} -extend_to_core_ring\n"
      )
      writer.write(
        s"add_pdn_stripe -grid {grid} -layer {Metal5} -width {2.0} -pitch {${pdnMetal5Pitch}} -offset {10.0} -extend_to_core_ring\n"
      )
      writer.write("add_pdn_connect -grid {grid} -layers {Metal1 Metal4}\n")
      writer.write("add_pdn_connect -grid {grid} -layers {Metal4 Metal5}\n")
    }

    private def generatePdnMainG2(writer: PrintWriter): Unit = {
      writer.write(
        "define_pdn_grid -name {grid} -voltage_domains {CORE} -pins {TopMetal1 TopMetal2}\n"
      )
      writer.write(
        "add_pdn_stripe -grid {grid} -layer {Metal1} -width {0.44} -pitch {7.56} -offset {0} -followpins -extend_to_core_ring\n"
      )
      writer.write(
        s"add_pdn_ring -grid {grid} -layers {Metal5 TopMetal1} -widths {${pdnRingWidth}} -spacings {${pdnRingSpace}} -core_offsets {${pdnRingCoreOffset}} -connect_to_pads\n"
      )
      writer.write(
        s"add_pdn_stripe -grid {grid} -layer {TopMetal1} -width {4.0} -pitch {${pdnTopMetal1Pitch}} -offset {10.0} -extend_to_core_ring\n"
      )
      writer.write(
        s"add_pdn_stripe -grid {grid} -layer {TopMetal2} -width {4.0} -pitch {${pdnTopMetal2Pitch}} -offset {10.0} -extend_to_core_ring\n"
      )
      writer.write("add_pdn_connect -grid {grid} -layers {Metal1 TopMetal1}\n")
      writer.write("add_pdn_connect -grid {grid} -layers {Metal5 TopMetal1}\n")
      writer.write("add_pdn_connect -grid {grid} -layers {Metal5 TopMetal2}\n")
      writer.write("add_pdn_connect -grid {grid} -layers {TopMetal1 TopMetal2}\n")
      if (blocks.length > 0) {
        val blockNames = blocks.map(_._2).distinct.mkString(" ")
        writer.write(
          s"define_pdn_grid -name {CORE_macro_grid_1} -voltage_domains {CORE} -macro -cells {${blockNames}} -grid_over_boundary\n"
        )
        writer.write("add_pdn_connect -grid {CORE_macro_grid_1} -layers {Metal5 TopMetal1}\n")
      }
    }

    private def generatePdnMainCMOS5L(writer: PrintWriter): Unit = {
      writer.write(
        "define_pdn_grid -name {grid} -voltage_domains {CORE} -pins {Metal4 TopMetal1}\n"
      )
      writer.write(
        "add_pdn_stripe -grid {grid} -layer {Metal1} -width {0.44} -pitch {7.56} -offset {0} -followpins -extend_to_core_ring\n"
      )
      writer.write(
        s"add_pdn_ring -grid {grid} -layers {Metal3 Metal4} -widths {${pdnRingWidth}} -spacings {${pdnRingSpace}} -core_offsets {${pdnRingCoreOffset}} -connect_to_pads\n"
      )
      writer.write(
        "add_pdn_stripe -grid {grid} -layer {Metal4} -width {4.0} -pitch {75.0} -offset {25.0} -extend_to_core_ring\n"
      )
      writer.write(
        "add_pdn_stripe -grid {grid} -layer {TopMetal1} -width {4.0} -pitch {75.0} -offset {25.0} -extend_to_core_ring\n"
      )
      writer.write("add_pdn_connect -grid {grid} -layers {Metal1 Metal4}\n")
      writer.write("add_pdn_connect -grid {grid} -layers {Metal3 Metal4}\n")
      writer.write("add_pdn_connect -grid {grid} -layers {Metal4 TopMetal1}\n")
      writer.write("add_pdn_connect -grid {grid} -layers {Metal3 TopMetal1}\n")
      if (blocks.length > 0) {
        val blockNames = blocks.map(_._2).distinct.mkString(" ")
        writer.write(
          s"define_pdn_grid -name {CORE_macro_grid_1} -voltage_domains {CORE} -macro -cells {${blockNames}} -grid_over_boundary\n"
        )
        writer.write("add_pdn_connect -grid {CORE_macro_grid_1} -layers {Metal4 TopMetal1}\n")
      }
    }

    def generateYaml(designName: String): Unit = {
      val filename = s"${designName}.yaml"
      val writer = new PrintWriter(new File(s"${librelanePath}${filename}"))
      SpinalInfo(s"Generating ${filename}")

      val listedVerilog = Option(new File(config.zibalBuildPath).listFiles())
        .getOrElse(Array.empty[File])
        .filter(_.getName.endsWith(".v"))
        .map(_.getPath)
        .sorted
        .toSeq
      val verilogFiles = listedVerilog ++ additionalVerilogFiles

      writer.write("meta:\n")
      writer.write(s"  version: ${metaVersion}\n")
      val flowName = if (isBlock) Flow.Classic else Flow.Chip
      writer.write(s"  flow: ${flowName}\n")
      val activeSubs = substitutingSteps.filter(_.flows.contains(flowName))
      if (disabledSteps.length > 0 || activeSubs.nonEmpty) {
        writer.write("  substituting_steps:\n")
        disabledSteps.foreach(step => writer.write(s"    ${step}: null\n"))
        activeSubs.foreach(sub => writer.write(s"    ${sub.id}: ${sub.replacement}\n"))
      }
      writer.write("\n")

      writer.write(s"DESIGN_NAME: ${designName}\n")
      writer.write(s"PDK: ihp-${platform.tech}\n")
      writer.write("VERILOG_FILES:\n")
      verilogFiles.foreach(f => writer.write(s"  - ${f}\n"))
      if (lintDisableWarnings.length > 0) {
        writer.write(s"LINTER_DISABLE_WARNINGS: [${lintDisableWarnings.mkString(", ")}]\n")
      }
      writer.write("\n")

      if (clocks.length > 0) {
        if (isBlock) {
          writer.write(s"CLOCK_PORT: ${clocks.map(_._1).mkString(" ")}\n")
        } else {
          writer.write(s"CLOCK_PORT: ${clocks.head._1}\n")
          if (!clocks.head._3.equals(""))
            writer.write(s"CLOCK_NET: ${clocks.head._3}/p2c\n")
        }
        writer.write(f"CLOCK_PERIOD: ${clocks.head._2}%.2f\n")
        writer.write("\n")
      }

      if (hasSdc) {
        writer.write(s"PNR_SDC_FILE: ${sdcPath(designName)}\n")
        writer.write(s"SIGNOFF_SDC_FILE: ${sdcPath(designName)}\n")
        writer.write("\n")
      }

      writer.write("FP_SIZING: absolute\n")
      writer.write(s"DIE_AREA: [${dieArea._1}, ${dieArea._2}, ${dieArea._3}, ${dieArea._4}]\n")
      writer.write(s"CORE_AREA: [${coreArea._1}, ${coreArea._2}, ${coreArea._3}, ${coreArea._4}]\n")
      writer.write(f"PL_TARGET_DENSITY_PCT: ${placeDensity * 100}%.0f\n")
      writer.write(s"RT_MAX_LAYER: ${maxRoutingLayer}\n")
      writer.write("\n")

      writer.write("VDD_NETS: [VDD]\n")
      writer.write("GND_NETS: [VSS]\n")

      if (hasPdn) {
        writer.write("\n")
        if (hasCustomPdn) {
          writer.write(s"PDN_CFG: ${pdnPath(designName)}\n")
        } else {
          writer.write("PDN_CORE_RING: true\n")
          writer.write(f"PDN_CORE_RING_VWIDTH: ${pdnRingWidth}%.1f\n")
          writer.write(f"PDN_CORE_RING_HWIDTH: ${pdnRingWidth}%.1f\n")
          writer.write(f"PDN_CORE_RING_VSPACING: ${pdnRingSpace}%.1f\n")
          writer.write(f"PDN_CORE_RING_HSPACING: ${pdnRingSpace}%.1f\n")
          writer.write(s"PDN_VERTICAL_LAYER: ${pdnVLayer}\n")
          writer.write(s"PDN_HORIZONTAL_LAYER: ${pdnHLayer}\n")
          if (!isBlock && hasIoRing) {
            writer.write("PDN_CORE_RING_CONNECT_TO_PADS: true\n")
          }
        }
      }

      if (!runMagicDrc) {
        writer.write("\nRUN_MAGIC_DRC: false\n")
      }

      writer.write("\nRUN_GDSCHECK_DRC: true\n")

      if (!isBlock && hasIoRing) {
        generateChipExtras(writer)
      }

      writer.close()
    }

    private def generateChipExtras(writer: PrintWriter): Unit = {
      writer.write("\n")
      writer.write("VERILOG_DEFINES: [FUNCTIONAL]\n")
      writer.write("PRIMARY_GDSII_STREAMOUT_TOOL: klayout\n")
      writer.write("GRT_ALLOW_CONGESTION: true\n")
      writer.write("RUN_GDSCHECK_DENSITY: true\n")
      writer.write("RUN_GDSCHECK_ANTENNA: true\n")
      writer.write("\n")

      val ignoreCells = Seq(s"${platform.tech}_Corner") ++ lvsIgnoreCells
      writer.write(s"LVS_IGNORE_CELLS: [${ignoreCells.mkString(", ")}]\n")
      writer.write("\n")

      writer.write("PDN_CONNECT_PADS_TO_GRID: true\n")
      writer.write("\n")

      // Collect the IO ring cells per edge, then emit PAD_<EDGE> lists.
      io.get.component.getOrdredNodeIo.foreach { baseType =>
        baseType.parent match {
          case instance: IhpCmosIoSg13g2 =>
            pads(instance.edge) += (instance.number -> instance.cellName)
          case instance: IhpCmosIoSg13cmos5l =>
            pads(instance.edge) += (instance.number -> instance.cellName)
          case _ =>
        }
      }
      ioPower.get.foreach { instance =>
        pads(instance.edge) += (instance.number -> instance.getName())
      }

      val edgeKey = Map(
        Edge.North -> "PAD_NORTH",
        Edge.East -> "PAD_EAST",
        Edge.South -> "PAD_SOUTH",
        Edge.West -> "PAD_WEST"
      )
      Edge.values.foreach { edge =>
        if (pads(edge).toSeq.length > 0) {
          writer.write(s"${edgeKey(edge)}:\n")
          pads(edge).toSeq.sortBy(_._1).foreach { case (_, cell) =>
            writer.write(s"  - ${cell}\n")
          }
        }
      }
      writer.write("\n")

      writer.write("PAD_BONDPAD_NAME: bondpad_70x70\n")
      writer.write("EXTRA_GDS:\n")
      writer.write(s"  - ${config.zibalBuildPath}macros/bondpad/bondpad_70x70.gds.gz\n")
      writer.write("EXTRA_LEFS:\n")
      writer.write(s"  - ${config.zibalBuildPath}macros/bondpad/bondpad_70x70.lef\n")
      writer.write("IGNORE_DISCONNECTED_MODULES:\n")
      writer.write("  - bondpad_70x70\n")

      if (blocks.length > 0) {
        writer.write("\nMACROS:\n")
        blocks.foreach { case (instPath, blockName, x, y, orientation) =>
          val fin = blockFinal(blockName)
          writer.write(s"  ${blockName}:\n")
          writer.write(s"    gds: [${fin}/gds/${blockName}.gds]\n")
          writer.write(s"    lef: [${fin}/lef/${blockName}.lef]\n")
          writer.write(s"    nl: [${fin}/nl/${blockName}.nl.v]\n")
          writer.write(s"    pnl: [${fin}/pnl/${blockName}.pnl.v]\n")
          writer.write("    spef:\n")
          writer.write(s"""      "*": [${fin}/spef/nom/${blockName}.nom.spef]""" + "\n")
          writer.write("    lib:\n")
          libCorners.foreach { corner =>
            writer.write(
              s"""      "${corner}": [${fin}/lib/${corner}/${blockName}__${corner}.lib]""" + "\n"
            )
          }
          writer.write("    instances:\n")
          writer.write(s"      ${instPath}:\n")
          writer.write(s"        location: [${x}, ${y}]\n")
          writer.write(s"        orientation: ${orientation}\n")
        }
      }
    }

    def generateSdc(designName: String): Unit = {
      val filename = s"${designName}.sdc"
      val writer = new PrintWriter(new File(sdcPath(designName)))
      SpinalInfo(s"Generating ${filename}")

      writer.write(s"current_design ${designName}\n\n")

      clocks.foreach { case (port, period, cell) =>
        val clockDef = if (!isBlock && hasIoRing && !cell.equals("")) {
          s"[get_pins ${cell}/p2c]"
        } else {
          s"[get_ports ${port}]"
        }
        val uncertainty = if (hasIoRing) 0.05 else 0.15

        writer.write(
          s"create_clock ${clockDef} -name ${port} -period ${period} -waveform {0 ${period / 2}}\n"
        )
        writer.write(s"set_clock_uncertainty ${uncertainty} [get_clocks ${port}]\n")
        writer.write(s"set_clock_transition 0.25 [get_clocks ${port}]\n")

        writer.write(f"set input_delay_value ${period * ioPercentage}%.2f\n")
        writer.write(f"set output_delay_value ${period * ioPercentage}%.2f\n")

        val exclude = (Seq(port) ++ resetPorts).mkString(" ")
        writer.write(s"set non_clock_inputs {}\n")
        writer.write(
          s"""foreach in_port [all_inputs] { if {[get_full_name $$in_port] ni {${exclude}}} { lappend non_clock_inputs $$in_port } }""" + "\n"
        )
        writer.write(
          s"set_input_delay -max $$input_delay_value -clock ${port} $$non_clock_inputs\n"
        )
        writer.write(s"set_input_delay -min 0 -clock ${port} $$non_clock_inputs\n")
        writer.write(s"set_output_delay -max $$output_delay_value -clock ${port} [all_outputs]\n")
        writer.write(s"set_output_delay -min 0 -clock ${port} [all_outputs]\n")
        writer.write("\n")
      }

      resetPorts.foreach { reset =>
        writer.write(s"set_false_path -from [get_ports ${reset}] -to [all_registers]\n")
      }

      writer.write("set_timing_derate -early 0.95\n")
      writer.write("set_timing_derate -late 1.05\n")

      writer.close()
    }

    def dieArea: Tuple4[Double, Double, Double, Double] = _dieArea
    def dieArea_=(area: (Double, Double, Double, Double)) = {
      assert(
        divisible(area._1, platform.x),
        f"Die area lower left X not on Core Site (${platform.x})."
      )
      assert(
        divisible(area._2, platform.y),
        f"Die area lower left Y not on Core Site (${platform.y})."
      )
      assert(
        divisible(area._3, platform.x),
        f"Die area upper right X not on Core Site (${platform.x})."
      )
      assert(
        divisible(area._4, platform.y),
        f"Die area upper right Y not on Core Site (${platform.y})."
      )
      _dieArea = area
    }

    def coreArea: Tuple4[Double, Double, Double, Double] = _coreArea
    def coreArea_=(area: (Double, Double, Double, Double)) = {
      assert(
        divisible(area._1, platform.x),
        f"Core area lower left X not on Core Site (${platform.x})."
      )
      assert(
        divisible(area._2, platform.y),
        f"Core area lower left Y not on Core Site (${platform.y})."
      )
      assert(
        divisible(area._3, platform.x),
        f"Core area upper right X not on Core Site (${platform.x})."
      )
      assert(
        divisible(area._4, platform.y),
        f"Core area upper right Y not on Core Site (${platform.y})."
      )
      _coreArea = area
    }
  }
}
