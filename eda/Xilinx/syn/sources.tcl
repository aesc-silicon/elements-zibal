puts "Read Verilog files"

puts " - SOC"
read_verilog ${path_rtl}/${SOC}.v

puts " - SoC (top level file)"
read_verilog ../${board}_top.v

puts "Done"
