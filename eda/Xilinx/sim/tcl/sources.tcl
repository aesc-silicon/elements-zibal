exec xvlog $::env(VIVADO_PATH)/../data/verilog/src/glbl.v

exec xvlog ${path_rtl}/${SOC}.v
exec xvlog ../${board}_top.v

puts "Loaded all RTL files"
