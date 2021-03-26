# Synthesize
source $::env(TCL_PATH)/../../common/board.tcl
source $::env(TCL_PATH)/../../common/general.tcl
source $::env(TCL_PATH)/sources.tcl

read_xdc $::env(TCL_PATH)/../../XDC/${board}.xdc
synth_design -top ${top_module_name} -part ${part} -flatten rebuilt

write_checkpoint -force ./post_synth.dcp
write_edif -force ./${top_module_name}.edf
report_utilization -file ./logs/post_synth_utilization.txt
report_timing > ./logs/post_synth_timing.txt
report_timing_summary -file ./logs/post_synth_timing.rpt 

# Implemenation
read_edif ${top_module_name}.edf
link_design -part ${part} -top ${top_module_name}

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
report_timing > ./logs/post_route_timing.txt
report_timing_summary -file ./logs/post_route_timing.rpt 

#  write_bitstream
write_checkpoint -force ./post_route.dcp 

report_timing_summary

write_vhdl -mode funcsim -force ./${top_module_name}_pr.vhd
write_verilog -mode timesim -sdf_anno true -force ./${top_module_name}_pr.v
write_sdf -force ./${top_module_name}_pr.sdf

write_bitstream -bin_file -force ${path}/build/${board}/vivado/syn/${top_module_name}

exit
