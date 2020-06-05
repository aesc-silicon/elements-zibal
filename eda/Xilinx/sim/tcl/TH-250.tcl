source ../config.tcl

#enable source blocks
set board TH-250
set SOC "Hydrogen"
set top_module TH250_top
set test_bench TH250_tb
set wcfg_file sim.wcfg

source tcl/sim.tcl
