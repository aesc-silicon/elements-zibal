# Board Clocks for DH-006

# Main clock with 100MHz
create_clock -add -name sys_clk_pin -period 10 -waveform {0 5} [get_ports io_clock];

# JTAG clock with 10MHz
create_clock -add -name jtag_clk_pin -period 100 -waveform {0 50} [get_ports io_jtag_tck];
set_property CLOCK_DEDICATED_ROUTE FALSE [get_nets {io_jtag_tck_IBUF}]
