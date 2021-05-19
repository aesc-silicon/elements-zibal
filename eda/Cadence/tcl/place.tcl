set TOOL "place"

source common/board.tcl
source common/pathes.tcl
source pdks/${PDK}.tcl
source tcl/flow/sourcer.tcl
source constraints/${SOC}/${SOC}.flow.tcl

setMultiCpuUsage -localCpu 8 -remoteHost 1 -cpuPerRemoteHost 8

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

setMaxRouteLayer [get_max_route_layer]

set stages {floorplan place cts route signoff verify save}
if {$::env(STAGE) != "init"} {
	foreach stage $stages {
		puts $stage
		puts $::env(STAGE)
		[$stage]
		if {$stage == $::env(STAGE)} {
			break
		}
	}
}

uiSet main -title "Innovus - ${SOC}"
win
