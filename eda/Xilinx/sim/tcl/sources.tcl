exec xvlog $::env(VIVADO_PATH)/../data/verilog/src/glbl.v

exec xvlog ${path_rtl}/${SOC}.v
exec xvlog ../${top_module}.v

puts "Loaded all RTL files"
