set TOOL "map"

source common/board.tcl
source common/pathes.tcl
source pdks/${PDK}.tcl

set_db syn_generic_effort ${EFFORT}
set_db syn_map_effort ${EFFORT}
set_db syn_opt_effort ${EFFORT}

set_db hdl_track_filename_row_col true
set_db hdl_zero_replicate_is_null true

set_db design_process_node ${PROCESS}

set_db auto_ungroup none

if {![file exists ${PATH_RTL}]} {
	puts "No RTL directoy found!"
	exit
}

# Specify explicit search paths
set_db script_search_path ${PATH_RTL}/
set_db lib_search_path ${PATH_PDK}/
set_db init_hdl_search_path ${PATH_RTL}/

# Library setup
setup_library

# Load design
set_db hdl_language v2001
read_hdl ${SOC}.v
read_hdl ${TOP}.v

puts "#---------- Elaborate ----------"
elaborate ${TOP}

check_design -unresolved

# Constrains setup
read_sdc ${TOP}.sdc

# Synthesize to generic
puts "#---------- Synthesizing to generic ----------"
syn_generic ${TOP}
write_reports -directory ${PATH_REPORT} -tag generic

# Synthesize to gates
puts "#---------- Synthesizing to gates ----------"
syn_map ${TOP}
write_reports -directory ${PATH_REPORT} -tag map

# To keep modules even after optimization
#set_dont_touch module:Murax/VexRiscv/

# Incremental synthesis
puts "#---------- Incrmental synthesis (opt) ----------"
syn_opt -incremental ${TOP}
write_reports -directory ${PATH_REPORT} -tag incremental
summary_table -directory ${PATH_REPORT}

# Write design
write_design -innovus -basename ${PATH_OUTPUT}/${TOP}
write_design -innovus -basename ${PATH_LATEST}/${TOP}

file copy [get_db stdout_log] ${PATH_LOG}/
