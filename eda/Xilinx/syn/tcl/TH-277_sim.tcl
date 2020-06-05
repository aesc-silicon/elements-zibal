source ../config.tcl

set board TH-277
set SOC "Hydrogen"
set top_module TH277_top
set libs ${board}
set test_bench ${board}_tb

source tcl/sim.tcl
