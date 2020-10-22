source ../boards/general.tcl

set board TH-294
set SOC "Hydrogen"
set top_module TH294_top
set test_bench TH294_tb
set wcfg_file TH294.wcfg
set libs ${board}

set part XC7A35TFTG256-1

puts "Loaded the board specific config file"
