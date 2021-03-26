#Start post-synthesize simulation
source $::env(TCL_PATH)/../../common/board.tcl
source $::env(TCL_PATH)/../../common/general.tcl

exec xvlog $::env(VIVADO_PATH)/../data/verilog/src/glbl.v
exec xvhdl -work work ./${top_module_name}_pr.vhd
exec xvlog -sv -work work $::env(TCL_PATH)/../../../testbenches/${test_bench}.sv \
     -log ./logs/${test_bench}_xvhdl.log

exec xelab -debug typical --lib unisims --lib unisims_ver -s ${top_module}_func \
     -log ./logs/${test_bench}_xelab.log --timescale 1ns/1ns ${test_bench_name} glbl \
     -v 2 --incr

if {[file exists ${wcfg_file}]} {
	exec xsim ${top_module}_func -gui -wdb simulate_xsim.wdb -view ${wcfg_file} \
	     -log ./logs/${top_module}_xsim.log
} else {
	exec xsim ${top_module}_func -gui -wdb simulate_xsim.wdb \
	     -log ./logs/${top_module}_xsim.log
}
exit
