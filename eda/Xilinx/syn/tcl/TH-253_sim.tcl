source ../config.tcl

set SoC_name "TH253"
set board TH-253
set SOC "Hydrogen"
set top_module TH253_top
set libs ${board}
set test_bench ${board}_tb

source tcl/sim.tcl
