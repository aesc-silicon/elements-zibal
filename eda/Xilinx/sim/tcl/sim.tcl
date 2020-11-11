source ../common/general.tcl
source ../common/board.tcl
source tcl/sources.tcl

exec xvlog -sv -work work ../../testbenches/${test_bench}.sv \
     -log ./output/logs/${test_bench}_xvhdl.log

exec xelab -debug typical --lib unisims --lib unisims_ver -s ${top_module}_func \
     -log ./output/logs/${test_bench}_xelab.log --timescale 1ns/1ns ${test_bench_name} glbl

if {[file exists ${wcfg_file}]} {
	exec xsim ${top_module}_func -gui -wdb simulate_xsim.wdb -view ${wcfg_file} \
	     -log ./output/logs/${top_module}_xsim.log
} else {
	exec xsim ${top_module}_func -gui -wdb simulate_xsim.wdb \
	     -log ./output/logs/${top_module}_xsim.log
}
exit
