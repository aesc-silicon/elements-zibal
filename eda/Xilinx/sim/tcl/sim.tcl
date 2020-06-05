source sources.tcl

#exec xvlog -sv -work work ../../M25P.sv -log ./output/logs/M25P_xvhdl.log
exec xvlog -sv -work work ../../${board}_tb.sv -log ./output/logs/${test_bench}_xvhdl.log

exec xelab -debug typical --lib unisims -s ${top_module}_func -log ./output/logs/${test_bench}_xelab.log ${test_bench}
if {[file exists ${wcfg_file}]} {
	exec xsim ${top_module}_func -gui -wdb simulate_xsim.wdb -view ${wcfg_file} -log ./output/logs/${top_module}_xsim.log
} else {
	exec xsim ${top_module}_func -gui -wdb simulate_xsim.wdb -log ./output/logs/${top_module}_xsim.log
}
