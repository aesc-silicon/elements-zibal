set TOOL "synthesize"

source common/board.tcl
source common/pathes.tcl
source pdks/${PDK}.tcl

set_db syn_generic_effort ${EFFORT}
set_db syn_map_effort ${EFFORT}
set_db syn_opt_effort ${EFFORT}

set_db lp_power_analysis_effort ${EFFORT}
set_db leakage_power_effort ${EFFORT}
set_db lp_power_unit mW
set_db lp_toggle_rate_unit /ns

set_db hdl_track_filename_row_col true
set_db hdl_zero_replicate_is_null true

set_db design_process_node ${PROCESS}

set_db max_cpus_per_server 8
set_db information_level 7

set_db auto_ungroup none

foreach {suppress_message} [get_suppress_messages "genus"] {
	suppress_messages "$suppress_message"
}

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
setup_power

# Load design
set_db hdl_language v2001
read_hdl ${TOP}.v

puts "#---------- Elaborate ----------"
elaborate ${TOP}
time_info Elaboration

check_design -unresolved
report_timing -lint

# Constrains setup
read_sdc ${TOP}.sdc
check_timing_intent

# DFT
if {[llength [all_registers]] > 0} {
  define_cost_group -name I2C -design ${TOP}
  define_cost_group -name C2O -design ${TOP}
  define_cost_group -name C2C -design ${TOP}
  path_group -from [all_registers] -to [all_registers] -group C2C -name C2C
  path_group -from [all_registers] -to [all_outputs] -group C2O -name C2O
  path_group -from [all_inputs]  -to [all_registers] -group I2C -name I2C
}

define_cost_group -name I2O -design ${TOP}
path_group -from [all_inputs]  -to [all_outputs] -group I2O -name I2O
foreach cg [vfind / -cost_group *] {
  report_timing -group [list $cg]
}

set_db dft_scan_style muxed_scan
set_db dft_prefix DFT_
set_db dft_identify_top_level_test_clocks true
set_db dft_identify_test_signals true
set_db dft_identify_internal_test_clocks false
set_db use_scan_seqs_for_non_dft false
set_db "design:${TOP}" .dft_scan_map_mode tdrc_pass
set_db "design:${TOP}" .dft_connect_shift_enable_during_mapping tie_off
set_db "design:${TOP}" .dft_connect_scan_data_pins_during_mapping loopback
set_db "design:${TOP}" .dft_scan_output_preference auto
set_db "design:${TOP}" .dft_lockup_element_type preferred_level_sensitive

define_shift_enable -name dft_shift_enable -active high -create_port dtfShftEnable

# Run the DFT rule checks
check_dft_rules > ${PATH_REPORT}/${TOP}_dft_check.rpt
report dft_registers > ${PATH_REPORT}/${TOP}_dft_registers.rpt
report dft_setup > ${PATH_REPORT}/${TOP}_dft_setup.rpt

check_design -multiple_driver
check_dft_rules -advanced  > ${PATH_REPORT}/${TOP}_dft_check_advanced.rpt
report dft_violations > ${PATH_REPORT}/${TOP}_dft_violations.rpt

# Synthesize to generic
puts "#---------- Synthesizing to generic ----------"
syn_generic ${TOP}
time_info GENERIC
write_reports -directory ${PATH_REPORT} -tag generic

# Synthesize to gates
puts "#---------- Synthesizing to gates ----------"
syn_map ${TOP}
time_info MAPPED
write_reports -directory ${PATH_REPORT} -tag map

# To keep modules even after optimization
#set_dont_touch module:Murax/VexRiscv/

# Incremental synthesis
puts "#---------- Incrmental synthesis (opt) ----------"
syn_opt ${TOP}
time_info OPT
write_reports -directory ${PATH_REPORT} -tag opt

# Build the full scan chanins
check_dft_rules -advanced
connect_scan_chains -auto_create_chains
report dft_chains > ${PATH_REPORT}/${TOP}_dft_chains.rpt
compress_scan_chains -ratio 5 -compressor xor -auto_create
report dft_chains > ${PATH_REPORT}/${TOP}_dft_chains_compress.rpt

syn_opt -incremental ${TOP}
time_info INCREMENTAL
write_reports -directory ${PATH_REPORT} -tag incremental
summary_table -directory ${PATH_REPORT}


report dft_setup > ${PATH_REPORT}/${TOP}_dft_setup_final.rpt
write_scandef > ${PATH_OUTPUT}/${TOP}_scanDef
write_dft_abstract_model > ${PATH_OUTPUT}/${TOP}_scanAbstract
write_hdl -abstract > ${PATH_OUTPUT}/${TOP}_logicAbstract
write_script -analyze_all_scan_chains > ${PATH_OUTPUT}/${TOP}_analyzeAllScanChains
# check_atpg_rules -library <Verilog simulation library files> -compression -directory <Encounter Test workdir directory>
# write_dft_atpg -library <Verilog structural library files> -compression -directory $ET_WORKDIR

# Write design
write_design -innovus -basename ${PATH_OUTPUT}/${TOP}
write_design -innovus -basename ${PATH_LATEST}/${TOP}

time_info FINAL

file copy [get_db stdout_log] ${PATH_LOG}/
