// SPDX-FileCopyrightText: 2025 aesc silicon
//
// SPDX-License-Identifier: CERN-OHL-W-2.0

package zibal.misc

import java.io._
import spinal.core._
import nafarr.blackboxes.skywater.sky130._
import nafarr.blackboxes.ihp.sg13g2._
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

object OpenROADTools {

  case class PDKTech(platform: String, x: Double, y: Double)

  object PDKs {
    object IHP {
      val sg13g2 = PDKTech("sg13g2", 0.48, 3.78)
    }
  }

  def divisible(n: Double, p: Int): Double = { val s = math pow (10, p); (math floor n * s) / s }
  def divisible(a: Double, b: Double): Boolean = {
    return ((a / b) % 1) < 0.0001
  }

  object IHP {
    case class Config(
        config: ElementsConfig.ElementsConfig,
        platform: PDKTech,
        isBlock: Boolean = false
    ) {

      private var _dieArea: Tuple4[Double, Double, Double, Double] = (0, 0, 0, 0)
      private var _coreArea: Tuple4[Double, Double, Double, Double] = (0, 0, 0, 0)
      var hasPdn: Boolean = true
      var hasSdc: Boolean = true
      var multiCornerEnabled: Boolean = true
      var hasIoRing: Boolean = false
      var io: Option[Data] = null
      var usePdkFiles: Boolean = true
      var placeDensity: Double = 0.75
      var maxFanout: Int = 8
      var maxCapacitance: Double = 0.5
      var maxTransition: Int = 3
      var maxArea: Int = 0
      var ioPercentage: Double = 0.2
      var pdnRingWidth: Double = 5.0
      var pdnRingSpace: Double = 2.0
      var pdnRingCoreOffset: Double = 4.5

      val pads = Map(
        "north" -> Map[Int, (String, String, String)](),
        "west" -> Map[Int, (String, String, String)](),
        "south" -> Map[Int, (String, String, String)](),
        "east" -> Map[Int, (String, String, String)]()
      )
      val macros = ArrayBuffer[(String, String, Double, Double, String)]()
      val blocks = ArrayBuffer[String]()
      // booleans represent (input, output)
      val ioClockGroups = Map[String, (String, Boolean, Boolean)](
        "input_ports" -> ("sg13g2_IOPadIn", true, false),
        "output_4mA_ports" -> ("sg13g2_IOPadOut4mA", false, true),
        "output_16mA_ports" -> ("sg13g2_IOPadOut16mA", false, true),
        "output_30mA_ports" -> ("sg13g2_IOPadOut30mA", false, true),
        "inout_4mA_ports" -> ("sg13g2_IOPadInOut4mA", true, true),
        "inout_16mA_ports" -> ("sg13g2_IOPadInOut16mA", true, true),
        "inout_30mA_ports" -> ("sg13g2_IOPadInOut30mA", true, true)
      )
      val clocks = Map[String, (String, Float, String, ArrayBuffer[(String, String)])]()
      val falsePath = ArrayBuffer[(String, String)]()

      def addPad(edge: String, number: Int, cell: String) = {
        pads(edge) += (number -> (s"${cell}_${edge}_${number}", cell, ""))
      }

      def getCompName(comp: Component): String = {
        if (comp.getName().equals("toplevel"))
          return ""
        return getCompName(comp.parent) + "." + comp.getName()
      }

      def addMacro(macroComp: Component, x: Double, y: Double, orientation: String = "R0") = {
        val macroName = macroComp.getClass.toString().split('$')(1)
        macros += ((getCompName(macroComp).substring(1), macroName, x, y, orientation))
      }

      def addBlock(blockName: String) = {
        blocks += blockName
      }

      def addClock(pin: Bool, frequency: HertzNumber, group: String = "") = {
        val time = (frequency.toTime.toBigDecimal / 1.0e-9).floatValue()
        if (pin.parent != null) {
          val instance = pin.parent.asInstanceOf[IhpCmosIo.IhpCmosIo]
          clocks += group -> (pin.getName(), time, instance.cellName, ArrayBuffer())
        } else {
          clocks += pin.getName() -> (pin.getName(), time, "", ArrayBuffer())
        }
      }

      def setFalsePath(from: String, to: String, inverse: Boolean = true) = {
        falsePath += ((from, to))
        if (inverse)
          falsePath += ((to, from))
      }

      def generateSealring(designName: String) = {
        val filename = s"${designName}.sealring.txt"
        val file = s"${config.zibalBuildPath}${filename}"
        val writer = new PrintWriter(new File(file))
        SpinalInfo(s"Generating ${filename}")

        writer.write(s"${dieArea._3}\n${dieArea._4}\n")
        writer.close()
      }

      def generateMacros(designName: String) = {
        val filename = s"${designName}.macros.tcl"
        val file = s"${config.zibalBuildPath}${filename}"
        val writer = new PrintWriter(new File(file))
        SpinalInfo(s"Generating ${filename}")

        for ((macroName, instance, x, y, orientation) <- macros) {
          writer.write(
            s"place_macro -macro_name ${macroName} -location {${x} ${y}} -orientation ${orientation} -exact\n"
          )
        }

        writer.close()
      }

      def generateFootprint(designName: String) = {
        val filename = s"${designName}.pdn.tcl"
        val file = s"${config.zibalBuildPath}${filename}"
        val writer = new PrintWriter(new File(file))
        SpinalInfo(s"Generating ${filename}")

        writer.write("# standard cells\n")
        writer.write("add_global_connection -net {VDD} -pin_pattern {^VDD$} -power\n")
        writer.write("add_global_connection -net {VDD} -pin_pattern {^VDDPE$}\n")
        writer.write("add_global_connection -net {VDD} -pin_pattern {^VDDCE$}\n")
        writer.write("add_global_connection -net {VSS} -pin_pattern {^VSS$} -ground\n")
        writer.write("add_global_connection -net {VSS} -pin_pattern {^VSSE$}\n")
        writer.write("# memory macros\n")
        writer.write(
          "add_global_connection -net {VDD} -inst_pattern {.*} -pin_pattern {VDDARRAY} -power\n"
        )
        writer.write(
          "add_global_connection -net {VDD} -inst_pattern {.*} -pin_pattern {VDDARRAY!} -power\n"
        )
        writer.write(
          "add_global_connection -net {VDD} -inst_pattern {.*} -pin_pattern {VDD!} -power\n"
        )
        writer.write(
          "add_global_connection -net {VSS} -inst_pattern {.*} -pin_pattern {VSS!} -ground\n"
        )
        writer.write(
          "add_global_connection -net {VDD} -inst_pattern {.*} -pin_pattern {^VDD$} -power\n"
        )
        writer.write(
          "add_global_connection -net {VSS} -inst_pattern {.*} -pin_pattern {^VSS$} -ground\n"
        )
        writer.write("# padframe core power pins\n")
        writer.write("add_global_connection -net {VDD} -pin_pattern {^vdd$} -power\n")
        writer.write("add_global_connection -net {VSS} -pin_pattern {^vss$} -ground\n")
        writer.write("# padframe io power pins\n")
        writer.write("add_global_connection -net {IOVDD} -pin_pattern {^iovdd$} -power\n")
        writer.write("add_global_connection -net {IOVSS} -pin_pattern {^iovss$} -ground\n")
        writer.write("global_connect\n")
        writer.write("# core voltage domain\n")
        writer.write("set_voltage_domain -name {CORE} -power {VDD} -ground {VSS}\n")
        writer.write("# stdcell grid\n")
        if (isBlock) {
          writer.write(
            "define_pdn_grid -name {grid} -voltage_domains {CORE} -pins {Metal4 Metal5}\n"
          )
          writer.write(
            "add_pdn_stripe -grid {grid} -layer {Metal1} -width {0.44} -pitch {7.56} -offset {0} -followpins\n"
          )
          writer.write(
            s"add_pdn_ring -grid {grid} -layers {Metal4 Metal5} -widths {${pdnRingWidth}} -spacings {${pdnRingSpace}} -core_offsets {${pdnRingCoreOffset}} -connect_to_pads\n"
          )
          writer.write(
            "add_pdn_stripe -grid {grid} -layer {Metal4} -width {1.840} -pitch {75.6} -offset {13.6} -extend_to_core_ring\n"
          )
          writer.write(
            "add_pdn_stripe -grid {grid} -layer {Metal5} -width {1.840} -pitch {75.6} -offset {13.6} -extend_to_core_ring\n"
          )
          writer.write("add_pdn_connect -grid {grid} -layers {Metal1 Metal4}\n")
          writer.write("add_pdn_connect -grid {grid} -layers {Metal4 Metal5}\n")
        } else {
          writer.write(
            "define_pdn_grid -name {grid} -voltage_domains {CORE} -pins {TopMetal1 TopMetal2}\n"
          )
          writer.write(
            "add_pdn_stripe -grid {grid} -layer {Metal1} -width {0.44} -pitch {7.56} -offset {0} -followpins -extend_to_core_ring\n"
          )
          writer.write(
            s"add_pdn_ring -grid {grid} -layers {TopMetal1 Metal5} -widths {${pdnRingWidth}} -spacings {${pdnRingSpace}} -core_offsets {${pdnRingCoreOffset}} -connect_to_pads\n"
          )
          writer.write(
            "add_pdn_stripe -grid {grid} -layer {TopMetal1} -width {2.200} -pitch {75.6} -offset {13.600} -extend_to_core_ring\n"
          )
          writer.write(
            "add_pdn_stripe -grid {grid} -layer {TopMetal2} -width {2.200} -pitch {75.6} -offset {13.600} -extend_to_core_ring\n"
          )
          writer.write("add_pdn_connect -grid {grid} -layers {Metal1 TopMetal1}\n")
          writer.write("add_pdn_connect -grid {grid} -layers {Metal5 TopMetal1}\n")
          writer.write("add_pdn_connect -grid {grid} -layers {Metal5 TopMetal2}\n")
          writer.write("add_pdn_connect -grid {grid} -layers {TopMetal1 TopMetal2}\n")
          if (macros.length > 0) {
            val macroNames = macros.map(t => t._2).mkString(" ")
            writer.write(
              s"define_pdn_grid -name {sram_grid} -voltage_domains {CORE} -macro -cells {${macroNames}} -grid_over_boundary\n"
            )
            writer.write(
              "add_pdn_ring -grid {sram_grid} -layer {Metal4 Metal5} -widths {8.0} -spacings {4.0} -core_offsets {16.0} -add_connect -connect_to_pads\n"
            )
            writer.write(
              "add_pdn_stripe -grid {sram_grid} -layer {Metal5} -width {2.2} -pitch {20.0} -offset {10.0} -extend_to_core_ring\n"
            )
            writer.write("add_pdn_connect -grid {sram_grid} -layers {Metal4 TopMetal1}\n")
            writer.write("add_pdn_connect -grid {sram_grid} -layers {Metal5 TopMetal1}\n")
          }
          if (blocks.length > 0) {
            val blockNames = blocks.mkString(" ")
            writer.write(
              s"define_pdn_grid -name {CORE_macro_grid_1} -voltage_domains {CORE} -macro -cells {${blockNames}} -grid_over_boundary\n"
            )
            writer.write("add_pdn_connect -grid {CORE_macro_grid_1} -layers {Metal4 TopMetal1}\n")
            writer.write("add_pdn_connect -grid {CORE_macro_grid_1} -layers {Metal5 TopMetal1}\n")
          }
        }

        writer.close()
      }

      def generateIo(designName: String) = {
        val filename = s"${designName}.pad.tcl"
        val file = s"${config.zibalBuildPath}${filename}"
        val writer = new PrintWriter(new File(file))
        SpinalInfo(s"Generating ${filename}")

        io.get.component.getOrdredNodeIo.foreach { baseType =>
          val instance = baseType.parent.asInstanceOf[IhpCmosIo.IhpCmosIo]
          pads(instance.edge) += (instance.number -> (
            (
              instance.cellName,
              instance.cell,
              instance.getName()
            )
          ))
        }

        writer.write("set IO_LENGTH 180\n")
        writer.write("set IO_WIDTH 80\n")
        writer.write("set BONDPAD_SIZE 70\n")
        writer.write("set SEALRING_OFFSET 70\n")
        writer.write("set IO_OFFSET [expr {$BONDPAD_SIZE + $SEALRING_OFFSET}]\n")
        writer.write(
          "proc calc_horizontal_pad_location {index total IO_LENGTH IO_WIDTH BONDPAD_SIZE SEALRING_OFFSET } {\n"
        )
        writer.write(
          "    set DIE_WIDTH [expr {[lindex $::env(DIE_AREA) 2] - [lindex $::env(DIE_AREA) 0]}]\n"
        )
        writer.write("    set PAD_OFFSET [expr {$IO_LENGTH + $BONDPAD_SIZE + $SEALRING_OFFSET}]\n")
        writer.write("    set PAD_AREA_WIDTH [expr {$DIE_WIDTH - ($PAD_OFFSET * 2)}]\n")
        writer.write(
          "    set HORIZONTAL_PAD_DISTANCE [expr {($PAD_AREA_WIDTH / $total) - $IO_WIDTH}]\n"
        )
        writer.write(
          "    return [expr {$PAD_OFFSET + (($IO_WIDTH + $HORIZONTAL_PAD_DISTANCE) * $index) + ($HORIZONTAL_PAD_DISTANCE / 2)}]\n"
        )
        writer.write("}\n")
        writer.write(
          "proc calc_vertical_pad_location {index total IO_LENGTH IO_WIDTH BONDPAD_SIZE SEALRING_OFFSET } {\n"
        )
        writer.write(
          "    set DIE_HEIGHT [expr {[lindex $::env(DIE_AREA) 3] - [lindex $::env(DIE_AREA) 1]}]\n"
        )
        writer.write("    set PAD_OFFSET [expr {$IO_LENGTH + $BONDPAD_SIZE + $SEALRING_OFFSET}]\n")
        writer.write("    set PAD_AREA_HEIGHT [expr {$DIE_HEIGHT - ($PAD_OFFSET * 2)}]\n")
        writer.write(
          "    set VERTICAL_PAD_DISTANCE [expr {($PAD_AREA_HEIGHT / $total) - $IO_WIDTH}]\n"
        )
        writer.write(
          "    return [expr {$PAD_OFFSET + (($IO_WIDTH + $VERTICAL_PAD_DISTANCE) * $index) + ($VERTICAL_PAD_DISTANCE / 2)}]\n"
        )
        writer.write("}\n")
        writer.write("make_fake_io_site -name IOLibSite -width 1 -height $IO_LENGTH\n")
        writer.write("make_fake_io_site -name IOLibCSite -width $IO_LENGTH -height $IO_LENGTH\n")
        writer.write("# Create IO Rows\n")
        writer.write(
          "make_io_sites -horizontal_site IOLibSite -vertical_site IOLibSite -corner_site IOLibCSite -offset $IO_OFFSET\n"
        )
        writer.write("# Place Pads\n")

        pads("south").toSeq.sortBy(_._1).foreach { pad =>
          val total = pads("south").toSeq.length
          if (!pad._2._3.equals(""))
            writer.write(s"# IO pin ${pad._2._3}\n")
          writer.write(
            s"place_pad -row IO_SOUTH -location [calc_horizontal_pad_location ${pad._1} ${total} $$IO_LENGTH $$IO_WIDTH $$BONDPAD_SIZE $$SEALRING_OFFSET] {${pad._2._1}} -master ${pad._2._2}\n"
          )
        }
        pads("east").toSeq.sortBy(_._1).foreach { pad =>
          val total = pads("east").toSeq.length
          if (!pad._2._3.equals(""))
            writer.write(s"# IO pin ${pad._2._3}\n")
          writer.write(
            s"place_pad -row IO_EAST -location [calc_vertical_pad_location ${pad._1} ${total} $$IO_LENGTH $$IO_WIDTH $$BONDPAD_SIZE $$SEALRING_OFFSET] {${pad._2._1}} -master ${pad._2._2}\n"
          )
        }
        pads("north").toSeq.sortBy(_._1).foreach { pad =>
          val total = pads("north").toSeq.length
          if (!pad._2._3.equals(""))
            writer.write(s"# IO pin ${pad._2._3}\n")
          writer.write(
            s"place_pad -row IO_NORTH -location [calc_horizontal_pad_location ${pad._1} ${total} $$IO_LENGTH $$IO_WIDTH $$BONDPAD_SIZE $$SEALRING_OFFSET] {${pad._2._1}} -master ${pad._2._2}\n"
          )
        }
        pads("west").toSeq.sortBy(_._1).foreach { pad =>
          val total = pads("west").toSeq.length
          if (!pad._2._3.equals(""))
            writer.write(s"# IO pin ${pad._2._3}\n")
          writer.write(
            s"place_pad -row IO_WEST -location [calc_vertical_pad_location ${pad._1} ${total} $$IO_LENGTH $$IO_WIDTH $$BONDPAD_SIZE $$SEALRING_OFFSET] {${pad._2._1}} -master ${pad._2._2}\n"
          )
        }

        writer.write("# Place Corner Cells and Filler\n")
        writer.write("place_corners sg13g2_Corner\n")
        writer.write("set iofill {\n")
        writer.write("    sg13g2_Filler10000\n")
        writer.write("    sg13g2_Filler4000\n")
        writer.write("    sg13g2_Filler2000\n")
        writer.write("    sg13g2_Filler1000\n")
        writer.write("    sg13g2_Filler400\n")
        writer.write("    sg13g2_Filler200\n")
        writer.write("}\n")
        writer.write("place_io_fill -row IO_NORTH {*}$iofill\n")
        writer.write("place_io_fill -row IO_SOUTH {*}$iofill\n")
        writer.write("place_io_fill -row IO_WEST {*}$iofill\n")
        writer.write("place_io_fill -row IO_EAST {*}$iofill\n")
        writer.write("connect_by_abutment\n")
        writer.write("place_bondpad -bond bondpad_70x70 sg13g2_IOPad* -offset {5.0 -70.0}\n")
        writer.write("remove_io_rows\n")

        writer.close()
      }

      def generateSdc(designName: String) = {
        val filename = s"${designName}.sdc"
        val file = s"${config.zibalBuildPath}${filename}"
        val writer = new PrintWriter(new File(file))
        SpinalInfo(s"Generating ${filename}")

        if (hasIoRing) {
          io.get.component.getOrdredNodeIo.foreach { baseType =>
            val instance = baseType.parent.asInstanceOf[IhpCmosIo.IhpCmosIo]
            if (!instance.clockGroup.equals("")) {
              clocks(instance.clockGroup)._4 += ((instance.getName(), instance.clockPort))
            }
          }
        }

        writer.write(s"current_design ${designName}\n")
        writer.write("set_units -time ns -resistance kOhm -capacitance pF -voltage V -current uA\n")
        writer.write(s"set_max_fanout ${maxFanout} [current_design]\n")
        writer.write(s"set_max_capacitance ${maxCapacitance} [current_design]\n")
        writer.write(s"set_max_transition ${maxTransition} [current_design]\n")
        writer.write(s"set_max_area ${maxArea}\n\n")

        clocks.foreach { clock =>
          val clockDef =
            if (hasIoRing) s"[get_pins ${clock._2._3}/p2c]" else s"[get_ports ${clock._2._1}]"

          writer.write(
            s"create_clock ${clockDef} -name ${clock._1} -period ${clock._2._2} -waveform {0 ${clock._2._2 / 2}}\n"
          )
          writer.write(s"set_ideal_network ${clockDef}\n")
          writer.write(s"set_clock_uncertainty 0.15 [get_clocks ${clock._1}]\n")
          writer.write(s"set_clock_transition 0.25 [get_clocks ${clock._1}]\n")
          writer.write(s"set input_delay_value_${clock._1} ${clock._2._2 * ioPercentage}\n")
          writer.write(s"set output_delay_value_${clock._1} ${clock._2._2 * ioPercentage}\n")

          if (hasIoRing) {
            val clockName = clock._1
            ioClockGroups.foreach { group =>
              val groupName = group._1
              val signals = clock._2._4.toSeq.filter(_._2.equals(groupName))
              if (signals.length > 0) {
                writer.write(s"set ${clockName}_${groupName} [get_ports {\n")
                signals.foreach { sig =>
                  writer.write(s"\t${sig._1}_PAD\n")
                }
                writer.write(s"}]\n")

                writer.write(
                  s"set_driving_cell -lib_cell ${group._2._1} -pin pad $$${clockName}_${groupName}\n"
                )
                if (group._2._2) {
                  writer.write(
                    s"set_input_delay $$input_delay_value_${clock._1} -clock ${clock._1} $$${clockName}_${groupName}\n"
                  )
                }
                if (group._2._3) {
                  writer.write(
                    s"set_output_delay $$output_delay_value_${clock._1} -clock ${clock._1} $$${clockName}_${groupName}\n"
                  )
                }
              }
            }
          } else {
            writer.write(
              s"set clk_indx_${clock._1} [lsearch [all_inputs] [get_port ${clock._2._1}]]\n"
            )
            writer.write(
              s"""set all_inputs_wo_clk_rst_${clock._1} [lreplace [all_inputs] $$clk_indx_${clock._1} $$clk_indx_${clock._1} ""]\n"""
            )
            writer.write(
              s"set_input_delay $$input_delay_value_${clock._1} -clock [get_clocks ${clock._2._1}] $$all_inputs_wo_clk_rst_${clock._1}\n"
            )
            writer.write(
              s"set_output_delay $$output_delay_value_${clock._1} -clock [get_clocks ${clock._2._1}] [all_outputs]\n"
            )
          }
          writer.write(s"\n")
        }

        falsePath.foreach { falsePath =>
          writer.write(
            s"set_false_path -from [get_clocks ${falsePath._1}] -to [get_clocks ${falsePath._2}]\n"
          )
        }

        if (hasIoRing) {
          writer.write(s"set clock_ports [get_ports {\n")
          clocks.foreach { clock =>
            writer.write(s"\t${clock._2._1}\n")
          }
          writer.write(s"}]\n")
          writer.write("set_driving_cell -lib_cell sg13g2_IOPadIn -pin pad $clock_ports\n")

          writer.write(s"set_load -pin_load 5 [all_inputs]\n")
          writer.write(s"set_load -pin_load 5 [all_outputs]\n")
        }

        writer.write(s"set_timing_derate -early 0.95\n")
        writer.write(s"set_timing_derate -late 1.05\n")

        writer.close()
      }

      def generate: Unit = generate(config.className)
      def generate(designName: String): Unit = {
        val design = if (isBlock) s"${designName}/${designName}" else designName
        val filename = if (isBlock) "config.mk" else s"${designName}.mk"
        val path =
          if (isBlock) s"${config.zibalBuildPath}${designName}/" else s"${config.zibalBuildPath}"
        val directory = new File(path);
        if (!directory.exists()) {
          directory.mkdirs();
          SpinalInfo(s"Creating path ${path}")
        }
        val writer = new PrintWriter(new File(s"${path}${filename}"))
        SpinalInfo(s"Generating ${filename}")

        writer.write(s"export DESIGN_NAME=${designName}\n");
        if (isBlock) {
          writer.write(s"export DESIGN_NICKNAME=${config.className}_${designName}\n");
        } else {
          writer.write(s"export DESIGN_NICKNAME=${designName}\n");
        }
        writer.write(s"export PLATFORM=ihp-${platform.platform}\n");
        writer.write(s"export VERILOG_FILES=${config.zibalBuildPath}*.v\n")
        writer.write(s"export DIE_AREA = ${dieArea._1} ${dieArea._2} ${dieArea._3} ${dieArea._4}\n")
        writer.write(
          s"export CORE_AREA = ${coreArea._1} ${coreArea._2} ${coreArea._3} ${coreArea._4}\n"
        )
        if (isBlock) {
          writer.write("export MAX_ROUTING_LAYER = TopMetal1\n")
        } else {
          writer.write("export MAX_ROUTING_LAYER = TopMetal2\n")
        }
        writer.write("export TNS_END_PERCENT = 100\n")
        writer.write(s"export PLACE_DENSITY = ${placeDensity}\n")
        writer.write("export MACRO_PLACE_HALO = 20 20\n")
        if (multiCornerEnabled) {
          if (isBlock) {
            writer.write("export CORNERS = slow typ fast\n")
          } else {
            writer.write("export CORNERS = slow fast\n")
          }
        }
        if (hasSdc) {
          writer.write(s"export SDC_FILE=${config.zibalBuildPath}${design}.sdc\n")
        }
        if (hasPdn) {
          writer.write(s"export PDN_TCL = ${config.zibalBuildPath}${design}.pdn.tcl\n")
        }
        if (hasIoRing) {
          writer.write("export HAS_IO_RING = 1\n")
          writer.write(
            s"export SEAL_GDS = ${config.zibalBuildPath}/macros/sealring/sealring.gds.gz\n"
          )
          writer.write(s"export FOOTPRINT_TCL = ${config.zibalBuildPath}${design}.pad.tcl\n")
        }
        if (macros.length > 0) {
          writer.write(
            s"export MACRO_PLACEMENT_TCL = ${config.zibalBuildPath}${design}.macros.tcl\n"
          )
        }
        if (blocks.length > 0) {
          writer.write(s"export BLOCKS = ${blocks.mkString(" ")}\n")
        }
        if (usePdkFiles) {
          writer.write("export LOAD_ADDITIONAL_FILES = 0\n")
          writer.write(
            "export TECH_LEF = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_stdcell/lef/sg13g2_tech.lef\n"
          )
          writer.write(
            "export SC_LEF = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_stdcell/lef/sg13g2_stdcell.lef\n"
          )
          // LEF Files
          if (hasIoRing) {
            writer.write(
              "export ADDITIONAL_LEFS += $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_io/lef/sg13g2_io.lef\n"
            )
            writer.write("export ADDITIONAL_LEFS += $(PLATFORM_DIR)/lef/bondpad_70x70.lef\n")
          }
          for (instance: String <- macros.map(t => t._2).toSet) {
            writer.write("export ADDITIONAL_LEFS += $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_sram/lef/")
            writer.write(s"${instance}.lef\n")
          }
          // Lib Files
          writer.write(
            "export TYP_LIB_FILES = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_stdcell/lib/sg13g2_stdcell_typ_1p20V_25C.lib\n"
          )
          writer.write(
            "export SLOW_LIB_FILES = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_stdcell/lib/sg13g2_stdcell_slow_1p08V_125C.lib\n"
          )
          writer.write(
            "export FAST_LIB_FILES = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_stdcell/lib/sg13g2_stdcell_fast_1p32V_m40C.lib\n"
          )
          if (hasIoRing) {
            writer.write(
              "export TYP_LIB_FILES += $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_io/lib/sg13g2_io_typ_1p2V_3p3V_25C.lib\n"
            )
            writer.write(
              "export SLOW_LIB_FILES += $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_io/lib/sg13g2_io_slow_1p08V_3p0V_125C.lib\n"
            )
            writer.write(
              "export FAST_LIB_FILES += $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_io/lib/sg13g2_io_fast_1p32V_3p6V_m40C.lib\n"
            )
          }
          for (instance: String <- macros.map(t => t._2).toSet) {
            writer.write("export TYP_LIB_FILES += $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_sram/lib/")
            writer.write(s"${instance}_typ_1p20V_25C.lib\n")
            writer.write("export SLOW_LIB_FILES += $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_sram/lib/")
            writer.write(s"${instance}_slow_1p08V_125C.lib\n")
            writer.write("export FAST_LIB_FILES += $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_sram/lib/")
            writer.write(s"${instance}_fast_1p32V_m55C.lib\n")
          }
          writer.write("export TYP_LIB_FILES += $(ADDITIONAL_LIBS)\n")
          writer.write("export SLOW_LIB_FILES += $(ADDITIONAL_SLOW_LIBS)\n")
          writer.write("export FAST_LIB_FILES += $(ADDITIONAL_FAST_LIBS)\n")
          // GDS Files
          writer.write(
            "export GDS_FILES = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_stdcell/gds/sg13g2_stdcell.gds\n"
          )
          if (hasIoRing) {
            writer.write(
              "export GDS_FILES += $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_io/gds/sg13g2_io.gds\n"
            )
            writer.write("export GDS_FILES += $(PLATFORM_DIR)/gds/bondpad_70x70.gds\n")
          }
          for (instance: String <- macros.map(t => t._2).toSet) {
            writer.write("export GDS_FILES += $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_sram/gds/")
            writer.write(s"${instance}.gds\n")
          }
          writer.write("export GDS_FILES += $(ADDITIONAL_GDS)\n")
        }
        writer.close()

        if (hasIoRing) {
          generateSealring(design)
          generateIo(design)
        }
        if (macros.length > 0) {
          generateMacros(design)
        }
        if (hasSdc) {
          generateSdc(design)
        }
        if (hasPdn) {
          generateFootprint(design)
        }
      }

      def dieArea: Tuple4[Double, Double, Double, Double] = _dieArea
      def dieArea_=(area: (Double, Double, Double, Double)) = {
        assert(
          divisible(area._1, platform.x),
          f"Die area lower left coordinate not on Core Site (${platform.x})."
        )
        assert(
          divisible(area._2, platform.y),
          f"Die area lower left coordinate not on Core Site (${platform.y})."
        )
        assert(
          divisible(area._3, platform.x),
          f"Die area upper right coordinate not on Core Site (${platform.x})."
        )
        assert(
          divisible(area._4, platform.y),
          f"Die area upper right coordinate not on Core Site (${platform.y})."
        )
        _dieArea = area
      }

      def coreArea: Tuple4[Double, Double, Double, Double] = _coreArea
      def coreArea_=(area: (Double, Double, Double, Double)) = {
        assert(
          divisible(area._1, platform.x),
          f"Core area lower left coordinate not on Core Site (${platform.x})."
        )
        assert(
          divisible(area._2, platform.y),
          f"Core area lower left coordinate not on Core Site (${platform.y})."
        )
        assert(
          divisible(area._3, platform.x),
          f"Core area upper right coordinate not on Core Site (${platform.x})."
        )
        assert(
          divisible(area._4, platform.y),
          f"Core area upper right coordinate not on Core Site (${platform.y})."
        )
        _coreArea = area
      }
    }
  }
}
