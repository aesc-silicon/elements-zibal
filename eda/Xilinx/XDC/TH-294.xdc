# Board Constraints file for TH-294

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
set_property PACKAGE_PIN L5 [get_ports {io_sysReset_out}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_sysReset_out}]

## Status
set_property PACKAGE_PIN K12 [get_ports {io_gpioStatus[0]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpioStatus[0]}]
set_property PACKAGE_PIN L13 [get_ports {io_gpioStatus[1]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpioStatus[1]}]
set_property PACKAGE_PIN K13 [get_ports {io_gpioStatus[2]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpioStatus[2]}]
set_property PACKAGE_PIN L5 [get_ports {io_gpioStatus[3]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpioStatus[3]}]

## Stdout
set_property PACKAGE_PIN T2 [get_ports {io_uartStd_txd}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_uartStd_txd}]
set_property PACKAGE_PIN R3 [get_ports {io_uartStd_rxd}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_uartStd_rxd}]
set_property PACKAGE_PIN R1 [get_ports {io_uartStd_rts}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_uartStd_rts}]
set_property PACKAGE_PIN R2 [get_ports {io_uartStd_cts}]
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
set_property PACKAGE_PIN N11 [get_ports {io_spi0_sclk}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_spi0_sclk}]
set_property PACKAGE_PIN M6 [get_ports {io_spi0_ss}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_spi0_ss}]
set_property PACKAGE_PIN N6 [get_ports {io_spi0_rst}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_spi0_rst}]
set_property PACKAGE_PIN N9 [get_ports {io_spi0_mosi}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_spi0_mosi}]
set_property PACKAGE_PIN P9 [get_ports {io_spi0_miso}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_spi0_miso}]
set_property PACKAGE_PIN P8 [get_ports {io_spi0_wp}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_spi0_wp}]
set_property PACKAGE_PIN R8 [get_ports {io_spi0_hold}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_spi0_hold}]

## GPIO 1 - GPIO1 - GPIO11
set_property PACKAGE_PIN F5 [get_ports {io_gpio1[0]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[0]}]
set_property PACKAGE_PIN E5 [get_ports {io_gpio1[1]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[1]}]
set_property PACKAGE_PIN F4 [get_ports {io_gpio1[2]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[2]}]

set_property PACKAGE_PIN F3 [get_ports {io_gpio1[3]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[3]}]
set_property PACKAGE_PIN F2 [get_ports {io_gpio1[4]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[4]}]
set_property PACKAGE_PIN E1 [get_ports {io_gpio1[5]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[5]}]

set_property PACKAGE_PIN G5 [get_ports {io_gpio1[6]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[6]}]
set_property PACKAGE_PIN G4 [get_ports {io_gpio1[7]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[7]}]
set_property PACKAGE_PIN G2 [get_ports {io_gpio1[8]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[8]}]

set_property PACKAGE_PIN G1 [get_ports {io_gpio1[9]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[9]}]
set_property PACKAGE_PIN H5 [get_ports {io_gpio1[10]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[10]}]
set_property PACKAGE_PIN H4 [get_ports {io_gpio1[11]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[11]}]

set_property PACKAGE_PIN T4 [get_ports {io_gpio1[12]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[12]}]
set_property PACKAGE_PIN T3 [get_ports {io_gpio1[13]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[13]}]
set_property PACKAGE_PIN R5 [get_ports {io_gpio1[14]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[14]}]

set_property PACKAGE_PIN T5 [get_ports {io_gpio1[15]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[15]}]
set_property PACKAGE_PIN T7 [get_ports {io_gpio1[16]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[16]}]
set_property PACKAGE_PIN T8 [get_ports {io_gpio1[17]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[17]}]

set_property PACKAGE_PIN T9 [get_ports {io_gpio1[18]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[18]}]
set_property PACKAGE_PIN T10 [get_ports {io_gpio1[19]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[19]}]
set_property PACKAGE_PIN T14 [get_ports {io_gpio1[20]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[20]}]

set_property PACKAGE_PIN T15 [get_ports {io_gpio1[21]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[21]}]
set_property PACKAGE_PIN R15 [get_ports {io_gpio1[22]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[22]}]
set_property PACKAGE_PIN R16 [get_ports {io_gpio1[23]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[23]}]

set_property PACKAGE_PIN P15 [get_ports {io_gpio1[24]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[24]}]
set_property PACKAGE_PIN P16 [get_ports {io_gpio1[25]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[25]}]
set_property PACKAGE_PIN N16 [get_ports {io_gpio1[26]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[26]}]

set_property PACKAGE_PIN M16 [get_ports {io_gpio1[27]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[27]}]
set_property PACKAGE_PIN G11 [get_ports {io_gpio1[28]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[28]}]
set_property PACKAGE_PIN G15 [get_ports {io_gpio1[29]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[29]}]

set_property PACKAGE_PIN H14 [get_ports {io_gpio1[30]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[30]}]
set_property PACKAGE_PIN J16 [get_ports {io_gpio1[31]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio1[31]}]

## GPIO 2 - GPIO11 - GPIO22
set_property PACKAGE_PIN J15 [get_ports {io_gpio2[0]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[0]}]

set_property PACKAGE_PIN G16 [get_ports {io_gpio2[1]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[1]}]
set_property PACKAGE_PIN H16 [get_ports {io_gpio2[2]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[2]}]
set_property PACKAGE_PIN P6 [get_ports {io_gpio2[3]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[3]}]

set_property PACKAGE_PIN R11 [get_ports {io_gpio2[4]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[4]}]
set_property PACKAGE_PIN R10 [get_ports {io_gpio2[5]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[5]}]
set_property PACKAGE_PIN F14 [get_ports {io_gpio2[6]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[6]}]

set_property PACKAGE_PIN G14 [get_ports {io_gpio2[7]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[7]}]
set_property PACKAGE_PIN H13 [get_ports {io_gpio2[8]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[8]}]
set_property PACKAGE_PIN H12 [get_ports {io_gpio2[9]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[9]}]

set_property PACKAGE_PIN G12 [get_ports {io_gpio2[10]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[10]}]
set_property PACKAGE_PIN H11 [get_ports {io_gpio2[11]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[11]}]
set_property PACKAGE_PIN R7 [get_ports {io_gpio2[12]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[12]}]

set_property PACKAGE_PIN R6 [get_ports {io_gpio2[13]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[13]}]
set_property PACKAGE_PIN J5 [get_ports {io_gpio2[14]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[14]}]
set_property PACKAGE_PIN J4 [get_ports {io_gpio2[15]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[15]}]

set_property PACKAGE_PIN P5 [get_ports {io_gpio2[16]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[16]}]
set_property PACKAGE_PIN P10 [get_ports {io_gpio2[17]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[17]}]
set_property PACKAGE_PIN P11 [get_ports {io_gpio2[18]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[18]}]

set_property PACKAGE_PIN P3 [get_ports {io_gpio2[19]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[19]}]
set_property PACKAGE_PIN P4 [get_ports {io_gpio2[20]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[20]}]
set_property PACKAGE_PIN N4 [get_ports {io_gpio2[21]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[21]}]

set_property PACKAGE_PIN M5 [get_ports {io_gpio2[22]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[22]}]
set_property PACKAGE_PIN K5 [get_ports {io_gpio2[23]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[23]}]
set_property PACKAGE_PIN K2 [get_ports {io_gpio2[24]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[24]}]

set_property PACKAGE_PIN K3 [get_ports {io_gpio2[25]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[25]}]
set_property PACKAGE_PIN L2 [get_ports {io_gpio2[26]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[26]}]
set_property PACKAGE_PIN L3 [get_ports {io_gpio2[27]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[27]}]

set_property PACKAGE_PIN J1 [get_ports {io_gpio2[28]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[28]}]
set_property PACKAGE_PIN K1 [get_ports {io_gpio2[29]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[29]}]
set_property PACKAGE_PIN H3 [get_ports {io_gpio2[30]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[30]}]

set_property PACKAGE_PIN J3 [get_ports {io_gpio2[31]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio2[31]}]

## GPIO 3 - GPIO22
set_property PACKAGE_PIN H1 [get_ports {io_gpio3[0]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio3[0]}]
set_property PACKAGE_PIN H2 [get_ports {io_gpio3[1]}]
set_property IOSTANDARD LVCMOS33 [get_ports {io_gpio3[1]}]
