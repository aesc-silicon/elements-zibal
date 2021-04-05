set TOOL "place"

source common/board.tcl
source common/pathes.tcl
source pdks/${PDK}.tcl
source tcl/flow/sourcer.tcl

setMultiCpuUsage -localCpu 8

set defHierChar {/}
set init_design_settop 1
set init_top_cell ${top_module}

set init_mmmc_file constraints/${SOC}/mmmc.tcl
set init_verilog ${PATH_BUILD_ROOT}/map/${SOC}.v
lappend init_verilog constraints/${SOC}/${top_module}.v

set init_io_file constraints/${SOC}/${SOC}.io

set init_lef_file [get_lef_files]

set init_pwr_net [get_power_nets]
set init_gnd_net [get_ground_nets]

foreach {suppress_message} [get_suppress_messages] {
	suppressMessage "$suppress_message"
}

elements_load_design

elements_check_design all
elements_analysis_timing prePlace true
elements_check_design place
elements_floorplan core 0.8 50
elements_filler per
elements_connect_PG per
elements_connect_PG core
elements_power_route {{VDDCORE 20} {VSSCORE 20}}
elements_check_design power

# TODO scan chain
elements_place ntd
elements_check_design pastPlace
elements_check_design floorplan
elements_opt_design preCTS
elements_analysis_timing preCTS true
# TODO warnings
elements_cts
elements_opt_design postCTS
elements_analysis_timing postCTS true

elements_filler core
elements_route nano
elements_opt_design postRoute
elements_opt_design postRouteHold
elements_analysis_timing postRoute true
elements_filler metal

elements_verify
elements_save final
elements_write final

uiSet main -title "Innovus - ${SOC}"
win
