package zibal.misc

import java.io._
import spinal.core._
import nafarr.blackboxes.skywater.sky130._
import nafarr.blackboxes.ihp.sg13g2._
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

object OpenROADTools {

  object PDKs {
    object Skywater {
      val sky130hd = "sky130hd"
      val sky130hs = "sky130hs"
    }
    object IHP {
      val sg13g2 = "sg13g2"
    }
  }

  object IHP {
    case class Config(config: ElementsConfig.ElementsConfig) {
      def generate(
          platform: String,
          dieArea: Tuple4[Double, Double, Double, Double],
          coreArea: Tuple4[Double, Double, Double, Double],
          placeDensity: Double = 0.75,
          hasIoRing: Boolean = true,
          useFill: Boolean = false
      ) = {
        val filename = s"${config.className}.mk"
        val file = s"${config.zibalBuildPath}${filename}"
        val writer = new PrintWriter(new File(file))
        SpinalInfo(s"Generate ${filename}")

        writer.write(s"""export DESIGN_NAME=${config.className}
export PLATFORM=ihp-${platform}

export VERILOG_FILES= ${config.zibalBuildPath}${config.className}.v
export SDC_FILE=${config.zibalBuildPath}${config.className}.sdc

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

export FOOTPRINT_TCL = ${config.zibalBuildPath}${config.className}.pad.tcl
export PDN_TCL = ${config.zibalBuildPath}${config.className}.pdn.tcl\n""")

        writer.write("""
export LOAD_ADDITIONAL_FILES =
export TECH_LEF = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_stdcell/lef/sg13g2_tech.lef
export SC_LEF = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_stdcell/lef/sg13g2_stdcell.lef
export ADDITIONAL_LEFS = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_io/lef/sg13g2_io.lef \
                         $(PLATFORM_DIR)/lef/bondpad_70x70.lef
export LIB_FILES = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_stdcell/lib/sg13g2_stdcell_typ_1p20V_25C.lib \
                   $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_io/lib/sg13g2_io_typ_1p2V_3p3V_25C.lib
export GDS_FILES = $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_stdcell/gds/sg13g2_stdcell.gds \
                   $(PDK_ROOT)/$(PDK)/libs.ref/sg13g2_io/gds/sg13g2_io.gds \
                   $(PLATFORM_DIR)/gds/bondpad_70x70.gds
""")

        writer.close()

        val sealringFilename = s"${config.className}.sealring.txt"
        val sealringFile = s"${config.zibalBuildPath}${sealringFilename}"
        val sealringWriter = new PrintWriter(new File(sealringFile))
        SpinalInfo(s"Generate ${sealringFilename}")

        sealringWriter.write(s"${dieArea._3}\n${dieArea._4}\n")
        sealringWriter.close()
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
        SpinalInfo(s"Generate ${filename}")

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
        SpinalInfo(s"Generate ${config.className}.pad.tcl")

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

# Place Pads\n""")

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
        SpinalInfo(s"Generate ${config.className}.pdn.tcl")

        writer.write("""
# stdcell power pins
add_global_connection -net {VDD} -pin_pattern {^VDD$} -power
add_global_connection -net {VDD} -pin_pattern {^VDDPE$}
add_global_connection -net {VDD} -pin_pattern {^VDDCE$}

add_global_connection -net {VSS} -pin_pattern {^VSS$} -ground
add_global_connection -net {VSS} -pin_pattern {^VSSE$}

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
""")
        writer.close()
      }
    }
  }

  case class Config(config: ElementsConfig.ElementsConfig) {

    var speedOptimization = false
    var areaOptimization = false
    var fixViolations: Option[Int] = None

    def fixTimingViolations(percentage: Int) = {
      fixViolations = Some(percentage)
    }

    def optimizeSpeed() = {
      speedOptimization = true
      areaOptimization = false
    }
    def optimizeArea() = {
      speedOptimization = false
      areaOptimization = true
    }

    def generate(
        platform: String,
        dieArea: Tuple4[Double, Double, Double, Double],
        ioMargin: Double
    ) = {
      val file = s"${config.zibalBuildPath}${config.className}.mk"
      val writer = new PrintWriter(new File(file))
      SpinalInfo(s"Generate ${config.className}.mk")

      val coreArea =
        s"${dieArea._1 + ioMargin} ${dieArea._2 + ioMargin} ${dieArea._3 - ioMargin} ${dieArea._4 - ioMargin}"

      writer.write(s"""export PLATFORM=${platform}
export DESIGN_NAME=${config.className}
export SDC_FILE=${config.zibalBuildPath}${config.className}.sdc
export VERILOG_FILES= ${config.zibalBuildPath}${config.className}.v

export DIE_AREA = ${dieArea._1} ${dieArea._2} ${dieArea._3} ${dieArea._4}
export CORE_AREA = ${coreArea}
export RESYNTH_TIMING_RECOVER=1

export FOOTPRINT_TCL = ${config.zibalBuildPath}${config.className}.pad.tcl
export PDN_TCL = ${config.zibalBuildPath}${config.className}.pdn.tcl\n""")

      if (fixViolations.isDefined) {
        writer.write(s"""export TNS_END_PERCENT=${fixViolations.get}\n""")
      }
      if (speedOptimization) {
        writer.write("export ABC_SPEED=1\n")
      }
      if (areaOptimization) {
        writer.write("export ABC_AREA=1\n")
      }
      if (platform == PDKs.Skywater.sky130hd) {
        writer.write(s"""
export VERILOG_FILES= \\
    ${config.zibalBuildPath}${config.className}.v \\
    $$(PDK_SKY130_IO_DIR)/verilog/sky130_ef_io.v \\
    $$(PDK_SKY130_IO_DIR)/verilog/sky130_fd_io__blackbox.v
export ADDITIONAL_GDS = \\
    $$(PDK_SKY130_IO_DIR)/gds/sky130_ef_io.gds \\
    $$(PDK_SKY130_IO_DIR)/gds/sky130_fd_io.gds
export ADDITIONAL_LEFS = \\
    $$(PDK_SKY130_IO_DIR)/lef/sky130_ef_io.lef \\
    $$(PDK_SKY130_IO_DIR)/lef/sky130_fd_io.lef\n""")
      }

      writer.close()
    }
  }

  case class Sdc(config: ElementsConfig.ElementsConfig) {
    val clocks = Map[String, (Float, Double)]()

    def addClock(pin: Bool, frequency: HertzNumber, delay: Double) = {
      val time = (frequency.toTime.toBigDecimal / 1.0e-9).floatValue()
      val name = pin.getName()
      clocks += name -> (time, delay)
    }

    def generate() = {
      val file = s"${config.zibalBuildPath}${config.className}.sdc"
      val writer = new PrintWriter(new File(file))
      SpinalInfo(s"Generate ${config.className}.sdc")

      writer.write(s"""current_design ${config.className}\n\n""")

      clocks.foreach { case (name, (period, delay)) =>
        writer.write(s"""create_clock -name "${name}" -period ${period} [get_ports ${name}]
set_clock_latency -source 0  [get_clocks ${name}]
set_clock_uncertainty 0.03  [get_clocks ${name}]
set_clock_transition -min -fall 0.069 [get_clocks ${name}]
set_clock_transition -min -rise 0.069 [get_clocks ${name}]
set_clock_transition -max -fall 0.069 [get_clocks ${name}]
set_clock_transition -max -rise 0.069 [get_clocks ${name}]\n""")
        // Only one clock input is supported right now.
        writer.write(
          s"""set_input_delay [expr ${period} * ${delay}] -clock ${name} [lsearch -inline -all -not -exact [all_inputs] [get_ports ${name}]]\n"""
        )
        writer.write(
          s"""set_input_delay [expr ${period} * ${delay}] -clock ${name} [all_outputs]\n"""
        )
      }

      writer.close()
    }
  }

  case class Io(config: ElementsConfig.ElementsConfig) {

    val pads = Map(
      "north" -> Map[Double, (String, String, String)](),
      "west" -> Map[Double, (String, String, String)](),
      "south" -> Map[Double, (String, String, String)](),
      "east" -> Map[Double, (String, String, String)]()
    )

    def addPad(edge: String, offset: Double, name: String, cell: String) = {
      pads(edge) += (offset -> (name, cell, ""))
    }

    def generate(io: Data) = {
      val file = s"${config.zibalBuildPath}${config.className}.pad.tcl"
      val writer = new PrintWriter(new File(file))
      SpinalInfo(s"Generate ${config.className}.pad.tcl")

      io.component.getOrdredNodeIo.foreach { baseType =>
        val instance = baseType.parent.asInstanceOf[Sky130CmosIo.Sky130CmosIo]
        pads(instance.edge) += (instance.offset -> (instance.getCellName, instance.cell, instance
          .getName()))
      }

      writer.write(s"""make_fake_io_site -name IO_SITE -width 1 -height 200
make_fake_io_site -name IO_CSITE -width 200 -height 204

# Create IO Rows
make_io_sites -horizontal_site IO_SITE \\
    -vertical_site IO_SITE \\
    -corner_site IO_CSITE \\
    -offset 0 \\
    -rotation_horizontal R180 \\
    -rotation_vertical R180 \\
    -rotation_corner R180

# Place Pads\n""")

      pads("south").toSeq.sortBy(_._1).foreach { pad =>
        if (!pad._2._3.equals(""))
          writer.write(s"# IO pin ${pad._2._3}\n")
        writer.write(
          s"place_pad -row IO_SOUTH -location ${pad._1} {${pad._2._1}} -master ${pad._2._2}\n"
        )
      }
      pads("east").toSeq.sortBy(_._1).foreach { pad =>
        if (!pad._2._3.equals(""))
          writer.write(s"# IO pin ${pad._2._3}\n")
        writer.write(
          s"place_pad -row IO_EAST -location ${pad._1} {${pad._2._1}} -master ${pad._2._2}\n"
        )
      }
      pads("north").toSeq.sortBy(_._1).foreach { pad =>
        if (!pad._2._3.equals(""))
          writer.write(s"# IO pin ${pad._2._3}\n")
        writer.write(
          s"place_pad -row IO_NORTH -location ${pad._1} {${pad._2._1}} -master ${pad._2._2}\n"
        )
      }
      pads("west").toSeq.sortBy(_._1).foreach { pad =>
        if (!pad._2._3.equals(""))
          writer.write(s"# IO pin ${pad._2._3}\n")
        writer.write(
          s"place_pad -row IO_WEST -location ${pad._1} {${pad._2._1}} -master ${pad._2._2}\n"
        )
      }

      writer.write(s"""# Place Corner Cells and Filler
place_corners sky130_ef_io__corner_pad

set iofill {
    sky130_ef_io__com_bus_slice_20um
    sky130_ef_io__com_bus_slice_10um
    sky130_ef_io__com_bus_slice_5um
    sky130_ef_io__com_bus_slice_1um
}

place_io_fill -row IO_NORTH {*}$$iofill
place_io_fill -row IO_SOUTH {*}$$iofill
place_io_fill -row IO_WEST {*}$$iofill
place_io_fill -row IO_EAST {*}$$iofill

connect_by_abutment

place_io_terminals sky130_fd_io__top_gpiov2_*/PAD

remove_io_rows""")

      writer.close()
    }
  }

  // IO handling is missing in current PDN file
  case class Pdn(config: ElementsConfig.ElementsConfig) {
    def generate() = {
      val file = s"${config.zibalBuildPath}${config.className}.pdn.tcl"
      val writer = new PrintWriter(new File(file))
      SpinalInfo(s"Generate ${config.className}.pdn.tcl")

      writer.write(s"""####################################
# global connections
####################################
add_global_connection -net {VDD} -inst_pattern {.*} -pin_pattern {^VDD$$} -power
add_global_connection -net {VDD} -inst_pattern {.*} -pin_pattern {^VDDPE$$}
add_global_connection -net {VDD} -inst_pattern {.*} -pin_pattern {^VDDCE$$}
add_global_connection -net {VDD} -inst_pattern {.*} -pin_pattern {VPWR}
add_global_connection -net {VDD} -inst_pattern {.*} -pin_pattern {VPB}
add_global_connection -net {VSS} -inst_pattern {.*} -pin_pattern {^VSS$$} -ground
add_global_connection -net {VSS} -inst_pattern {.*} -pin_pattern {^VSSE$$}
add_global_connection -net {VSS} -inst_pattern {.*} -pin_pattern {VGND}
add_global_connection -net {VSS} -inst_pattern {.*} -pin_pattern {VNB}

add_global_connection -net {VDD} -inst_pattern {pwr_core.*} -pin_pattern {P_CORE} -power
add_global_connection -net {VSS} -inst_pattern {gnd_core.*} -pin_pattern {G_CORE} -ground

global_connect
####################################
# voltage domains
####################################
set_voltage_domain -name {CORE} -power {VDD} -ground {VSS}
####################################
# standard cell grid
####################################
define_pdn_grid -name {grid} -voltage_domains {CORE}

add_pdn_stripe -grid {grid} -layer {met1} -width {0.49} -pitch {5.44} -offset {0} -followpins
add_pdn_ring -grid {grid} -layers {met4 met5} -widths {15 15} -spacings {2 2} \\
    -pad_offsets {10 10} -connect_to_pads -starts_with POWER
add_pdn_stripe -grid {grid} -layer {met4} -width {1.600} -pitch {27.140} -offset {13.570} \\
    -extend_to_core_ring
add_pdn_stripe -grid {grid} -layer {met5} -width {1.600} -pitch {27.200} -offset {13.600} \\
    -extend_to_core_ring

add_pdn_connect -grid {grid} -layers {met1 met4}
add_pdn_connect -grid {grid} -layers {met3 met4}
add_pdn_connect -grid {grid} -layers {met4 met5}

####################################
# macro grids
####################################
define_pdn_grid -name {pads} -voltage_domains {CORE} -macro \\
    -halo {0.0 0.0 0.0 0.0} \\
    -cells {
        sky130_fd_io__top_gpiov2
        sky130_fd_io__top_power_lvc_wpad
        sky130_fd_io__top_ground_lvc_wpad
        sky130_fd_io__top_power_hvc_wpadv2
        sky130_fd_io__top_ground_hvc_wpad
        sky130_ef_io__com_bus_slice_10um
        sky130_ef_io__com_bus_slice_1um
        sky130_ef_io__com_bus_slice_20um
        sky130_ef_io__com_bus_slice_5um
        sky130_ef_io__corner_pad
    } \\
    -grid_over_boundary
add_pdn_connect -grid {pads} -layers {met4 met5}
####################################
# grid for: CORE_macro_grid_1
####################################
define_pdn_grid -name {CORE_macro_grid_1} -voltage_domains {CORE} -macro -orient {R0 R180 MX MY} -halo {2.0 2.0 2.0 2.0} -default -grid_over_boundary
add_pdn_connect -grid {CORE_macro_grid_1} -layers {met4 met5}
####################################
# grid for: CORE_macro_grid_2
####################################
define_pdn_grid -name {CORE_macro_grid_2} -voltage_domains {CORE} -macro -orient {R90 R270 MXR90 MYR90} -halo {2.0 2.0 2.0 2.0} -default -grid_over_boundary
add_pdn_connect -grid {CORE_macro_grid_2} -layers {met4 met5}""")
      writer.close()
    }
  }
}
