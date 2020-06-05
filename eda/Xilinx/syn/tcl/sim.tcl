source ../config.tcl
source sources.tcl

#set top_module ${board}_top
set libs ${board}
#set test_bench ${board}_tb
set wcfg_file syn.wcfg

#Start post-synthesize simulation
exec xvhdl -work work ./output/${top_module}_pr.vhd
#exec xvlog -sv -work work ../../${test_bench}.sv -log ./output/logs/${test_bench}_xvhdl.log
exec xvlog -sv -work work ../../${board}_tb.sv -log ./output/logs/${test_bench}_xvhdl.log


exec xelab -debug typical -L unisims_ver ${test_bench} -s ${top_module}_func -v 2 --incr -log ./output/logs/${test_bench}_xelab.log
#exec xelab -debug typical --lib unisims -s ${top_module}_func -v 2 --incr -log ./output/logs/${test_bench}_xelab.log ${test_bench}

if {[file exists ${wcfg_file}]} {
	exec xsim ${top_module}_func -gui -wdb simulate_xsim.wdb -view ${wcfg_file} -log ./output/logs/${top_module}_xsim.log
} else {
	exec xsim ${top_module}_func -gui -wdb simulate_xsim.wdb -log ./output/logs/${top_module}_xsim.log
}
