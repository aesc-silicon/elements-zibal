set SOC $::env(SOC)
set BOARD $::env(BOARD)
set TOP "$::env(BOARD)Top"

#set test_bench $::env(TESTBENCH)
#set test_bench_name $::env(TESTBENCH_NAME)
#set wcfg_file ${board}.wcfg
#set libs ${board}

set PART $::env(PART)

puts "Loaded the board config file"
