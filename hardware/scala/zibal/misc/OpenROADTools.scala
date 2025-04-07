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
    case class Config(config: ElementsConfig.ElementsConfig) {
      def generate(
          platform: PDKTech,
          dieArea: Tuple4[Double, Double, Double, Double],
          coreArea: Tuple4[Double, Double, Double, Double],
          placeDensity: Double = 0.75,
          hasIoRing: Boolean = true,
          useFill: Boolean = false
      ) = {
        assert(
          divisible(dieArea._1, platform.x),
          f"Die area lower left coordinate not on Core Site (${platform.x})."
        )
        assert(
          divisible(dieArea._2, platform.y),
          f"Die area lower left coordinate not on Core Site (${platform.y})."
        )
        assert(
          divisible(dieArea._3, platform.x),
          f"Die area upper right coordinate not on Core Site (${platform.x})."
        )
        assert(
          divisible(dieArea._4, platform.y),
          f"Die area upper right coordinate not on Core Site (${platform.y})."
        )

        assert(
          divisible(coreArea._1, platform.x),
          f"Core area lower left coordinate not on Core Site (${platform.x})."
        )
        assert(
          divisible(coreArea._2, platform.y),
          f"Core area lower left coordinate not on Core Site (${platform.y})."
        )
        assert(
          divisible(coreArea._3, platform.x),
          f"Core area upper right coordinate not on Core Site (${platform.x})."
        )
        assert(
          divisible(coreArea._4, platform.y),
          f"Core area upper right coordinate not on Core Site (${platform.y})."
        )

        val filename = s"${config.className}.mk"
        val file = s"${config.zibalBuildPath}${filename}"
        val writer = new PrintWriter(new File(file))
        SpinalInfo(s"Generating ${filename}")

        writer.write(s"""export DESIGN_NAME=${config.className}
export PLATFORM=ihp-${platform.platform}
export SDC_FILE=${config.zibalBuildPath}${config.className}.sdc\n""")
        writer.write(s"""export VERILOG_FILES=${config.zibalBuildPath}*.v\n""")
        writer.write(s"""
export SEAL_GDS = ${config.zibalBuildPath}/macros/sealring/sealring.gds.gz

export DIE_AREA = ${dieArea._1} ${dieArea._2} ${dieArea._3} ${dieArea._4}
export CORE_AREA = ${coreArea._1} ${coreArea._2} ${coreArea._3} ${coreArea._4}

export MAX_ROUTING_LAYER = TopMetal2\n""")
        if (hasIoRing)
          writer.write("export HAS_IO_RING = 1\n")
        if (useFill)
          writer.write("export USE_FILL = 1\n")
        writer.write(s"""
export TNS_END_PERCENT = 100
export PLACE_DENSITY = ${placeDensity}
export GDS_ALLOW_EMPTY = RM_IHPSG13_1P_BITKIT_16x2_*

export FOOTPRINT_TCL = ${config.zibalBuildPath}${config.className}.pad.tcl
export PDN_TCL = ${config.zibalBuildPath}${config.className}.pdn.tcl\n
export MACRO_PLACEMENT_TCL = ${config.zibalBuildPath}${config.className}.macro.tcl\n""")

        writer.write("""
export LOAD_ADDITIONAL_FILES =
export TECH_LEF = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_stdcell/lef/sg13g2_tech.lef
export SC_LEF = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_stdcell/lef/sg13g2_stdcell.lef
export ADDITIONAL_LEFS = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_io/lef/sg13g2_io.lef \
                         $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_sram/lef/RM_IHPSG13_1P_512x32_c2_bm_bist.lef \
                         $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_sram/lef/RM_IHPSG13_1P_1024x8_c2_bm_bist.lef \
                         $(PLATFORM_DIR)/lef/bondpad_70x70.lef
export LIB_FILES = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_stdcell/lib/sg13g2_stdcell_typ_1p20V_25C.lib \
                   $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_io/lib/sg13g2_io_typ_1p2V_3p3V_25C.lib \
                   $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_sram/lib/RM_IHPSG13_1P_512x32_c2_bm_bist_typ_1p20V_25C.lib \
                   $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_sram/lib/RM_IHPSG13_1P_1024x8_c2_bm_bist_typ_1p20V_25C.lib
export GDS_FILES = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_stdcell/gds/sg13g2_stdcell.gds \
                   $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_io/gds/sg13g2_io.gds \
                   $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_sram/gds/RM_IHPSG13_1P_512x32_c2_bm_bist.gds \
                   $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_sram/gds/RM_IHPSG13_1P_1024x8_c2_bm_bist.gds \
                   $(PLATFORM_DIR)/gds/bondpad_70x70.gds
""")

        writer.close()

        val sealringFilename = s"${config.className}.sealring.txt"
        val sealringFile = s"${config.zibalBuildPath}${sealringFilename}"
        val sealringWriter = new PrintWriter(new File(sealringFile))
        SpinalInfo(s"Generating ${sealringFilename}")

        sealringWriter.write(s"${dieArea._3}\n${dieArea._4}\n")
        sealringWriter.close()
      }
    }

    case class Macros(config: ElementsConfig.ElementsConfig) {

      val macros = ArrayBuffer[(String, Double, Double, String)]()

      def getName(comp: Component): String = {
        if (comp.getName().equals("toplevel"))
          return ""
        return getName(comp.parent) + "." + comp.getName()
      }

      def addMacro(macroName: Component, x: Double, y: Double, orientation: String = "R0") = {
        macros += ((getName(macroName).substring(1), x, y, orientation))
      }

      def generate() = {
        val file = s"${config.zibalBuildPath}${config.className}.macro.tcl"
        val writer = new PrintWriter(new File(file))
        SpinalInfo(s"Generating ${config.className}.macro.tcl")

        for ((macroName, x, y, orientation) <- macros) {
          writer.write(
            s"""place_macro -macro_name ${macroName} -location {${x} ${y}} -orientation ${orientation}\n"""
          )
        }
        writer.close()
      }
    }

    case class Sdc(config: ElementsConfig.ElementsConfig) {
      // booleans represent (input, output)
      val clockGroups = Map[String, (String, Boolean, Boolean)](
        "input_ports" -> ("sg13g2_IOPadIn", true, false),
        "output_4mA_ports" -> ("sg13g2_IOPadOut4mA", false, true),
        "output_16mA_ports" -> ("sg13g2_IOPadOut16mA", false, true),
        "output_30mA_ports" -> ("sg13g2_IOPadOut30mA", false, true),
        "inout_4mA_ports" -> ("sg13g2_IOPadInOut4mA", true, true),
        "inout_16mA_ports" -> ("sg13g2_IOPadInOut16mA", true, true),
        "inout_30mA_ports" -> ("sg13g2_IOPadInOut30mA", true, true)
      )
      val clocks = Map[String, (String, String, Float, ArrayBuffer[(String, String)])]()
      val falsePath = ArrayBuffer[(String, String)]()

      def addClock(pin: Bool, frequency: HertzNumber, group: String) = {
        val time = (frequency.toTime.toBigDecimal / 1.0e-9).floatValue()
        val instance = pin.parent.asInstanceOf[IhpCmosIo.IhpCmosIo]
        clocks += group -> (pin.getName(), instance.cellName, time, ArrayBuffer())
      }

      def setFalsePath(from: String, to: String, inverse: Boolean = true) = {
        falsePath += ((from, to))
        if (inverse)
          falsePath += ((to, from))
      }

      def generate(io: Data) = {
        val filename = s"${config.className}.sdc"
        val file = s"${config.zibalBuildPath}${filename}"
        val writer = new PrintWriter(new File(file))
        SpinalInfo(s"Generating ${filename}")

        io.component.getOrdredNodeIo.foreach { baseType =>
          val instance = baseType.parent.asInstanceOf[IhpCmosIo.IhpCmosIo]
          if (!instance.clockGroup.equals("")) {
            clocks(instance.clockGroup)._4 += ((instance.getName(), instance.clockPort))
          }
        }

        writer.write(s"current_design ${config.className}\n")

        writer.write(s"""set_units -time ns -resistance kOhm -capacitance pF -voltage V -current uA
set_max_fanout 8 [current_design]
set_max_capacitance 0.5 [current_design]
set_max_transition 3 [current_design]
set_max_area 0\n\n""")

        clocks.foreach { clock =>
          writer.write(s"""set_ideal_network [get_pins ${clock._2._2}/p2c]
create_clock [get_pins ${clock._2._2}/p2c] -name ${clock._1} -period ${clock._2._3} -waveform {0 ${clock._2._3 / 2}}
set_clock_uncertainty 0.15 [get_clocks ${clock._1}]
set_clock_transition 0.25 [get_clocks ${clock._1}]\n\n""")
        }

        falsePath.foreach { falsePath =>
          writer.write(
            s"set_false_path -from [get_clocks ${falsePath._1}] -to [get_clocks ${falsePath._2}]\n"
          )
        }

        writer.write(s"set clock_ports [get_ports { \n")
        clocks.foreach { clock =>
          writer.write(s"\t${clock._2._1} \n")
        }
        writer.write(s"}]\n")
        writer.write(s"set_driving_cell -lib_cell sg13g2_IOPadIn -pin pad $$clock_ports\n\n")

        clocks.foreach { clock =>
          val clockName = clock._1
          clockGroups.foreach { group =>
            val groupName = group._1
            val signals = clock._2._4.toSeq.filter(_._2.equals(groupName))
            if (signals.length > 0) {
              writer.write(s"set ${clockName}_${groupName} [get_ports { \n")
              signals.foreach { sig =>
                writer.write(s"\t${sig._1}_PAD \n")
              }
              writer.write(s"}] \n")

              writer.write(
                s"set_driving_cell -lib_cell ${group._2._1} -pin pad $$${clockName}_${groupName}\n"
              )
              if (group._2._2) {
                writer.write(s"set_input_delay 8 -clock clk_core $$${clockName}_${groupName}\n")
              }
              if (group._2._3) {
                writer.write(s"set_output_delay 8 -clock clk_core $$${clockName}_${groupName}\n")
              }
              writer.write(s"\n")
            }
          }
        }

        writer.write(s"""set_load -pin_load 5 [all_inputs]
set_load -pin_load 5 [all_outputs]\n""")

        writer.close()
      }
    }

    case class Io(config: ElementsConfig.ElementsConfig) {

      val pads = Map(
        "north" -> Map[Int, (String, String, String)](),
        "west" -> Map[Int, (String, String, String)](),
        "south" -> Map[Int, (String, String, String)](),
        "east" -> Map[Int, (String, String, String)]()
      )

      def addPad(edge: String, number: Int, cell: String) = {
        pads(edge) += (number -> (s"${cell}_${edge}_${number}", cell, ""))
      }

      def generate(io: Data) = {
        val file = s"${config.zibalBuildPath}${config.className}.pad.tcl"
        val writer = new PrintWriter(new File(file))
        SpinalInfo(s"Generating ${config.className}.pad.tcl")

        io.component.getOrdredNodeIo.foreach { baseType =>
          val instance = baseType.parent.asInstanceOf[IhpCmosIo.IhpCmosIo]
          pads(instance.edge) += (instance.number -> (
            (
              instance.cellName,
              instance.cell,
              instance.getName()
            )
          ))
        }

        writer.write("""
set IO_LENGTH 180
set IO_WIDTH 80
set BONDPAD_SIZE 70
set SEALRING_OFFSET 70

proc calc_horizontal_pad_location {index total} {
    global IO_LENGTH
    global IO_WIDTH
    global BONDPAD_SIZE
    global SEALRING_OFFSET

    set DIE_WIDTH [expr {[lindex $::env(DIE_AREA) 2] - [lindex $::env(DIE_AREA) 0]}]
    set PAD_OFFSET [expr {$IO_LENGTH + $BONDPAD_SIZE + $SEALRING_OFFSET}]
    set PAD_AREA_WIDTH [expr {$DIE_WIDTH - ($PAD_OFFSET * 2)}]
    set HORIZONTAL_PAD_DISTANCE [expr {($PAD_AREA_WIDTH / $total) - $IO_WIDTH}]

    return [expr {$PAD_OFFSET + (($IO_WIDTH + $HORIZONTAL_PAD_DISTANCE) * $index) + ($HORIZONTAL_PAD_DISTANCE / 2)}]
}

proc calc_vertical_pad_location {index total} {
    global IO_LENGTH
    global IO_WIDTH
    global BONDPAD_SIZE
    global SEALRING_OFFSET

    set DIE_HEIGHT [expr {[lindex $::env(DIE_AREA) 3] - [lindex $::env(DIE_AREA) 1]}]
    set PAD_OFFSET [expr {$IO_LENGTH + $BONDPAD_SIZE + $SEALRING_OFFSET}]
    set PAD_AREA_HEIGHT [expr {$DIE_HEIGHT - ($PAD_OFFSET * 2)}]
    set VERTICAL_PAD_DISTANCE [expr {($PAD_AREA_HEIGHT / $total) - $IO_WIDTH}]

    return [expr {$PAD_OFFSET + (($IO_WIDTH + $VERTICAL_PAD_DISTANCE) * $index) + ($VERTICAL_PAD_DISTANCE / 2)}]
}

make_fake_io_site -name IOLibSite -width 1 -height $IO_LENGTH
make_fake_io_site -name IOLibCSite -width $IO_LENGTH -height $IO_LENGTH

set IO_OFFSET [expr {$BONDPAD_SIZE + $SEALRING_OFFSET}]
# Create IO Rows
make_io_sites \
    -horizontal_site IOLibSite \
    -vertical_site IOLibSite \
    -corner_site IOLibCSite \
    -offset $IO_OFFSET

# Place Pads
""")

        pads("south").toSeq.sortBy(_._1).foreach { pad =>
          val total = pads("south").toSeq.length
          if (!pad._2._3.equals(""))
            writer.write(s"# IO pin ${pad._2._3}\n")
          writer.write(
            s"place_pad -row IO_SOUTH -location [calc_horizontal_pad_location ${pad._1} ${total}] {${pad._2._1}} -master ${pad._2._2}\n"
          )
        }
        pads("east").toSeq.sortBy(_._1).foreach { pad =>
          val total = pads("east").toSeq.length
          if (!pad._2._3.equals(""))
            writer.write(s"# IO pin ${pad._2._3}\n")
          writer.write(
            s"place_pad -row IO_EAST -location [calc_vertical_pad_location ${pad._1} ${total}] {${pad._2._1}} -master ${pad._2._2}\n"
          )
        }
        pads("north").toSeq.sortBy(_._1).foreach { pad =>
          val total = pads("north").toSeq.length
          if (!pad._2._3.equals(""))
            writer.write(s"# IO pin ${pad._2._3}\n")
          writer.write(
            s"place_pad -row IO_NORTH -location [calc_horizontal_pad_location ${pad._1} ${total}] {${pad._2._1}} -master ${pad._2._2}\n"
          )
        }
        pads("west").toSeq.sortBy(_._1).foreach { pad =>
          val total = pads("west").toSeq.length
          if (!pad._2._3.equals(""))
            writer.write(s"# IO pin ${pad._2._3}\n")
          writer.write(
            s"place_pad -row IO_WEST -location [calc_vertical_pad_location ${pad._1} ${total}] {${pad._2._1}} -master ${pad._2._2}\n"
          )
        }

        writer.write(s"""# Place Corner Cells and Filler
place_corners sg13g2_Corner

set iofill {
    sg13g2_Filler10000
    sg13g2_Filler4000
    sg13g2_Filler2000
    sg13g2_Filler1000
    sg13g2_Filler400
    sg13g2_Filler200
}

place_io_fill -row IO_NORTH {*}$$iofill
place_io_fill -row IO_SOUTH {*}$$iofill
place_io_fill -row IO_WEST {*}$$iofill
place_io_fill -row IO_EAST {*}$$iofill

connect_by_abutment

place_bondpad -bond bondpad_70x70 sg13g2_IOPad* -offset {5.0 -70.0}

remove_io_rows\n""")
        writer.close()
      }
    }

    case class Pdn(config: ElementsConfig.ElementsConfig) {
      def generate() = {
        val file = s"${config.zibalBuildPath}${config.className}.pdn.tcl"
        val writer = new PrintWriter(new File(file))
        SpinalInfo(s"Generating ${config.className}.pdn.tcl")

        writer.write("""
# stdcell power pins
add_global_connection -net {VDD} -pin_pattern {^VDD$} -power
add_global_connection -net {VDD} -pin_pattern {^VDDPE$}
add_global_connection -net {VDD} -pin_pattern {^VDDCE$}
add_global_connection -net {VSS} -pin_pattern {^VSS$} -ground
add_global_connection -net {VSS} -pin_pattern {^VSSE$}

# rams
add_global_connection -net {VDD} -inst_pattern {.*} -pin_pattern {VDDARRAY} -power
add_global_connection -net {VDD} -inst_pattern {.*} -pin_pattern {VDDARRAY!} -power
add_global_connection -net {VDD} -inst_pattern {.*} -pin_pattern {VDD!} -power
add_global_connection -net {VSS} -inst_pattern {.*} -pin_pattern {VSS!} -ground

# padframe core power pins
add_global_connection -net {VDD} -pin_pattern {^vdd$} -power
add_global_connection -net {VSS} -pin_pattern {^vss$} -ground

# padframe io power pins
add_global_connection -net {IOVDD} -pin_pattern {^iovdd$} -power
add_global_connection -net {IOVSS} -pin_pattern {^iovss$} -ground

global_connect

# core voltage domain
set_voltage_domain -name {CORE} -power {VDD} -ground {VSS}

# stdcell grid
define_pdn_grid -name {grid} -voltage_domains {CORE}

add_pdn_ring \
	-grid {grid} \
	-layers {Metal5 TopMetal1} \
	-widths {30.0} \
	-spacings {5.0} \
	-core_offsets {4.5} \
	-connect_to_pads
add_pdn_stripe \
	-grid {grid} \
	-layer {Metal1} \
	-width {0.44} \
	-pitch {7.56} \
	-offset {0} \
	-followpins \
	-extend_to_core_ring
add_pdn_stripe \
	-grid {grid} \
	-layer {Metal5} \
	-width {2.200} \
	-pitch {75.6} \
	-offset {13.600} \
	-extend_to_core_ring
add_pdn_stripe \
	-grid {grid} \
	-layer {TopMetal1} \
	-width {2.200} \
	-pitch {75.6} \
	-offset {13.600} \
	-extend_to_core_ring
add_pdn_stripe \
	-grid {grid} \
	-layer {TopMetal2} \
	-width {2.200} \
	-pitch {75.6} \
	-offset {13.600} \
	-extend_to_core_ring
add_pdn_connect -grid {grid} -layers {Metal1 Metal5}
add_pdn_connect -grid {grid} -layers {Metal5 TopMetal1}
add_pdn_connect -grid {grid} -layers {Metal5 TopMetal2}
add_pdn_connect -grid {grid} -layers {TopMetal1 TopMetal2}

# pdn for sram macros
define_pdn_grid \
	-name {sram_grid} \
	-voltage_domains {CORE} \
	-macro -cells {RM_IHPSG13_1P_512x32_c2_bm_bist RM_IHPSG13_1P_1024x8_c2_bm_bist} \
	-grid_over_boundary
add_pdn_ring \
	-grid {sram_grid} \
	-layer {Metal3 Metal4} \
	-widths {8.0} \
	-spacings {4.0} \
	-core_offsets {16.0} \
	-add_connect \
	-connect_to_pads

add_pdn_stripe \
	-grid {sram_grid} \
	-layer {TopMetal1} \
	-width {8.0} \
	-pitch {75.6} \
	-offset {10.0} \
	-extend_to_core_ring
add_pdn_connect -grid sram_grid -layers {TopMetal1 Metal3}
add_pdn_connect -grid sram_grid -layers {TopMetal1 Metal4}
add_pdn_connect -grid sram_grid -layers {TopMetal1 TopMetal2}
""")
        writer.close()
      }
    }
  }
}
