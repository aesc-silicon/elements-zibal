package zibal.misc

import java.io._
import spinal.core._
import nafarr.blackboxes.skywater.sky130._
import scala.collection.mutable.Map

object OpenROADTools {

  object PDKs {
    object Skywater {
      val sky130hd = "sky130hd"
      val sky130hs = "sky130hs"
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
