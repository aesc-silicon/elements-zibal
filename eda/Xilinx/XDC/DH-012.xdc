# Board Constraints file for TH-283

## Clock Constraints
# Main clock with 100MHz
create_clock -name sys_clk_pin -period 10.0 [get_ports {io_clock}];
# JTAG clock with 10MHz
create_clock -name jtag_clk_pin -period 100.0 [get_ports {io_jtag_tck}];
set_property CLOCK_DEDICATED_ROUTE FALSE [get_nets {io_jtag_tck_IBUF}]

## Clock
set_property PACKAGE_PIN E12 [get_ports {io_clock}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_clock}]

## Reset
#set_property PACKAGE_PIN G11 [get_ports {io_reset}]
#set_property IOSTANDARD LVCMOS33 [get_ports {io_reset}]
set_property PACKAGE_PIN L5 [get_ports {io_sysReset_out}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_sysReset_out}]

## Status
set_property PACKAGE_PIN K12 [get_ports {io_gpioStatus[0]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpioStatus[0]}]
set_property PACKAGE_PIN L13 [get_ports {io_gpioStatus[1]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpioStatus[1]}]
set_property PACKAGE_PIN K13 [get_ports {io_gpioStatus[2]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpioStatus[2]}]
set_property PACKAGE_PIN K5 [get_ports {io_gpioStatus[3]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpioStatus[3]}]

## Stdout
set_property PACKAGE_PIN M4 [get_ports {io_uartStd_txd}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_uartStd_txd}]
set_property PACKAGE_PIN L4 [get_ports {io_uartStd_rxd}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_uartStd_rxd}]
set_property PACKAGE_PIN M2 [get_ports {io_uartStd_rts}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_uartStd_rts}]
set_property PACKAGE_PIN M1 [get_ports {io_uartStd_cts}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_uartStd_cts}]

## JTAG
set_property PACKAGE_PIN R13 [get_ports {io_jtag_tms}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_jtag_tms}]
set_property PACKAGE_PIN N13 [get_ports {io_jtag_tdi}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_jtag_tdi}]
set_property PACKAGE_PIN P13 [get_ports {io_jtag_tdo}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_jtag_tdo}]
set_property PACKAGE_PIN N14 [get_ports {io_jtag_tck}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_jtag_tck}]

## SPI FLASH
set_property PACKAGE_PIN N11 [get_ports {io_spiXip_sclk}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_spiXip_sclk}]
set_property PACKAGE_PIN M6 [get_ports {io_spiXip_ss}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_spiXip_ss}]
set_property PACKAGE_PIN N6 [get_ports {io_spiXip_rst}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_spiXip_rst}]
set_property PACKAGE_PIN N9 [get_ports {io_spiXip_mosi}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_spiXip_mosi}]
set_property PACKAGE_PIN P9 [get_ports {io_spiXip_miso}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_spiXip_miso}]
set_property PACKAGE_PIN P8 [get_ports {io_spiXip_wp}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_spiXip_wp}]
set_property PACKAGE_PIN R8 [get_ports {io_spiXip_hold}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_spiXip_hold}]

## I2C
set_property PACKAGE_PIN G15 [get_ports {io_i2c0_scl}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_i2c0_scl}]
set_property PACKAGE_PIN H14 [get_ports {io_i2c0_sda}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_i2c0_sda}]


## SPI DEBUG
set_property PACKAGE_PIN M5  [get_ports {debug_spiXip_sclk}]
set_property IOSTANDARD LVCMOS33 [get_ports {debug_spiXip_sclk}]
set_property PACKAGE_PIN P5  [get_ports {debug_spiXip_ss}]
set_property IOSTANDARD LVCMOS33 [get_ports {debug_spiXip_ss}]
set_property PACKAGE_PIN J16 [get_ports {debug_spiXip_mosi}]
set_property IOSTANDARD LVCMOS33 [get_ports {debug_spiXip_mosi}]
set_property PACKAGE_PIN T14 [get_ports {debug_spiXip_miso}]
set_property IOSTANDARD LVCMOS33 [get_ports {debug_spiXip_miso}]
