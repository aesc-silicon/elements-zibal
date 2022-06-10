# Synthesize
source $::env(TCL_PATH)/../../common/board.tcl
source $::env(TCL_PATH)/../../common/general.tcl
source $::env(TCL_PATH)/sources.tcl

set_property IS_ENABLED 0 [get_drc_checks {REQP-1839}]

read_xdc ${path_rtl}/${TOP}.xdc
synth_design -top ${TOP} -part ${PART} -flatten rebuilt

write_checkpoint -force ./post_synth.dcp
write_edif -force ./${TOP}.edf
report_utilization -file ./logs/post_synth_utilization.txt
report_utilization -hierarchical -file ./logs/post_synth_utilization_hierarchical.txt
report_timing > ./logs/post_synth_timing.txt
report_timing_summary -file ./logs/post_synth_timing.rpt 

# Implemenation
read_edif ${TOP}.edf
link_design -part ${PART} -top ${TOP}

#  opt_design
opt_design -directive Explore
#-directive ExploreSequentialArea 

#  power_opt_design

#  place_design
place_design
write_checkpoint -force ./post_place.dcp
phys_opt_design
route_design

place_design -post_place_opt
phys_opt_design
route_design

#  route_design
place_design -directive Explore
phys_opt_design -directive Explore
route_design -directive Explore

report_utilization -file ./logs/post_route_utilization.rpt
report_utilization -hierachical -file ./logs/post_route_utilization_hierachical.rpt
report_timing > ./logs/post_route_timing.txt
report_timing_summary -file ./logs/post_route_timing.rpt

#  write_bitstream
write_checkpoint -force ./post_route.dcp 

report_timing_summary

write_vhdl -mode funcsim -force ./${TOP}_pr.vhd
write_verilog -mode timesim -sdf_anno true -force ./${TOP}_pr.v
write_sdf -force ./${TOP}_pr.sdf

write_bitstream -bin_file -force ${path_syn}/${TOP}

exit
