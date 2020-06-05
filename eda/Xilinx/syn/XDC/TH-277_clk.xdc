# Board Clocks for TH-253

# Main clock with 100MHz
create_clock -add -name sys_clk_pin -period 10 -waveform {0 5} [get_ports io_clock];

# JTAG clock with 10MHz
#create_clock -add -name jtag_clk_pin -period 100 -waveform {0 50} [get_ports io_jtag_tck];
