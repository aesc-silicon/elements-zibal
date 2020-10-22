## This file is a general .xdc for the Nexys4 DDR Rev. C
## To use it in a project:
## - uncomment the lines corresponding to used pins
## - rename the used ports (in each line, after get_ports) according to the top level signal names in the project

# Main clock with 100MHz
create_clock -add -name sys_clk_pin -period 10 -waveform {0 5} [get_ports io_clock];

# JTAG clock with 10MHz
create_clock -add -name jtag_clk_pin -period 100 -waveform {0 50} [get_ports io_jtag_tck];
set_property CLOCK_DEDICATED_ROUTE FALSE [get_nets {io_jtag_tck_IBUF}]
