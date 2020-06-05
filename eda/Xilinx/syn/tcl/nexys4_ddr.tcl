source ../config.tcl

set top_module ${SoC_name}_top
set libs ${SoC_name}
set test_bench ${SoC_name}_tb
set board nexys4_DDR

source tcl/syn.tcl
