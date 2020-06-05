puts "Read VHDL files"

exec xvlog $::env(XILINX)/../../data/verilog/src/glbl.v

puts " - SoC"
exec xvlog ${path_rtl}/${SOC}.v

puts " - SoC (Top level file)"
exec xvlog ../${board}_top.v

puts "Done"
