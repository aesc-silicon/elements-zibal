read_verilog ${path_rtl}/${SOC}.v
read_verilog $::env(TCL_PATH)/../../${top_module}.v

puts "Loaded all RTL files"
