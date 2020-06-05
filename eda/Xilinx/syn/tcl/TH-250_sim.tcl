source ../config.tcl

set SoC_name "TH250"
set board TH-250
set SOC "Hydrogen"
set top_module TH250_top
set libs ${board}
set test_bench TH250_tb

source tcl/sim.tcl
