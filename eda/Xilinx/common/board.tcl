set board $::env(BOARD)
set board_name $::env(BOARD_NAME)
set SOC $::env(SOC)
set top_module $::env(TOP)
set top_module_name $::env(TOP_NAME)
set test_bench $::env(TESTBENCH)
set test_bench_name $::env(TESTBENCH_NAME)
set wcfg_file ${board}.wcfg
set libs ${board}

set part $::env(PART)

puts "Loaded the board config file"
