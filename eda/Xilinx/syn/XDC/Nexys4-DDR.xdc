# Board Constraints file for Nexys 4 DDR

## Clock Constraints
# Main clock with 100MHz
create_clock -name sys_clk_pin -period 10.0 [get_ports {io_clock}]
# JTAG clock with 10MHz
create_clock -name jtag_clk_pin -period 100.0 [get_ports {io_jtag_tck}]
set_property CLOCK_DEDICATED_ROUTE FALSE [get_nets {io_jtag_tck_IBUF}]

## Clock
set_property PACKAGE_PIN E3 [get_ports {io_clock}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_clock}]

## Reset
set_property PACKAGE_PIN R11 [get_ports {io_sysReset_out}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_sysReset_out}]

## Status
set_property PACKAGE_PIN R12 [get_ports {io_gpioStatus[0]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpioStatus[0]}]
set_property PACKAGE_PIN M16 [get_ports {io_gpioStatus[1]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpioStatus[1]}]
set_property PACKAGE_PIN N15 [get_ports {io_gpioStatus[2]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpioStatus[2]}]
set_property PACKAGE_PIN C12 [get_ports {io_gpioStatus[3]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpioStatus[3]}]

## Stdout
set_property PACKAGE_PIN D4 [get_ports {io_uartStd_txd}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_uartStd_txd}]
set_property PACKAGE_PIN C4 [get_ports {io_uartStd_rxd}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_uartStd_rxd}]
set_property PACKAGE_PIN D3 [get_ports {io_uartStd_rts}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_uartStd_rts}]
set_property PACKAGE_PIN E5 [get_ports {io_uartStd_cts}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_uartStd_cts}]

## JTAG
set_property PACKAGE_PIN J15 [get_ports {io_jtag_tms}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_jtag_tms}]
set_property PACKAGE_PIN L16 [get_ports {io_jtag_tdi}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_jtag_tdi}]
set_property PACKAGE_PIN M13 [get_ports {io_jtag_tdo}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_jtag_tdo}]
set_property PACKAGE_PIN R15 [get_ports {io_jtag_tck}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_jtag_tck}]
