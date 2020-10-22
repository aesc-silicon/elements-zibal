#Start post-synthesize simulation
exec xvhdl -work work ./output/${top_module}_pr.vhd
exec xvlog -sv -work work ../../${board}_tb.sv -log ./output/logs/${test_bench}_xvhdl.log

exec xelab -debug typical --lib unisims --lib unisims_ver -s ${top_module}_func \
     -log ./output/logs/${test_bench}_xelab.log --timescale 1ns/1ns ${test_bench} glbl \
     -v 2 --incr

if {[file exists ${wcfg_file}]} {
	exec xsim ${top_module}_func -gui -wdb simulate_xsim.wdb -view ${wcfg_file} \
	     -log ./output/logs/${top_module}_xsim.log
} else {
	exec xsim ${top_module}_func -gui -wdb simulate_xsim.wdb \
	     -log ./output/logs/${top_module}_xsim.log
}
exit
