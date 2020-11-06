source ../boards/general.tcl

set board TH-283
set SOC "Hydrogen1"
set top_module TH283_top
set test_bench TH283_tb
set wcfg_file TH283.wcfg
set libs ${board}

set part XC7A35TFTG256-1

puts "Loaded the board specific config file"
