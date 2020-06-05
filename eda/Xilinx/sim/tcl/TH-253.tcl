source ../config.tcl

#enable source blocks
set board TH-253
set SOC "Hydrogen"
set top_module TH253_top
set test_bench TH253_tb
set wcfg_file sim.wcfg

source tcl/sim.tcl
