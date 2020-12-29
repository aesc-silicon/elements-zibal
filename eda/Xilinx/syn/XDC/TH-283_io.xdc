# Board Constraints file for TH-283

## Clock
set_property -dict { PACKAGE_PIN E12 IOSTANDARD LVCMOS33 } [get_ports { io_clock }];

## Reset
set_property -dict { PACKAGE_PIN L5   IOSTANDARD LVCMOS33 } [get_ports { io_sysReset_out }];

## Status
set_property -dict { PACKAGE_PIN K12  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[0] }];
set_property -dict { PACKAGE_PIN L13  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[1] }];
set_property -dict { PACKAGE_PIN K13  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[2] }];
set_property -dict { PACKAGE_PIN K5   IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[3] }];

## Stdout
set_property -dict { PACKAGE_PIN M4   IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_txd }];
set_property -dict { PACKAGE_PIN L4   IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_rxd }];
set_property -dict { PACKAGE_PIN M2   IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_rts }];
set_property -dict { PACKAGE_PIN M1   IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_cts }];

## JTAG
set_property -dict { PACKAGE_PIN R13 IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tms }];
set_property -dict { PACKAGE_PIN N13 IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tdi }];
set_property -dict { PACKAGE_PIN P13 IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tdo }];
set_property -dict { PACKAGE_PIN N14 IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tck }];

## SPI FLASH
 # SPI1_CLK L13P_14 -> SCK
set_property -dict { PACKAGE_PIN N11 IOSTANDARD LVCMOS33 } [get_ports { io_spi0_sclk }];
 # SPI1_nCS L19P_14 -> CS#
set_property -dict { PACKAGE_PIN M6  IOSTANDARD LVCMOS33 } [get_ports { io_spi0_ss }];
 # SPI1_RST L19N_14 -> RST#/RFU
set_property -dict { PACKAGE_PIN N6  IOSTANDARD LVCMOS33 } [get_ports { io_spi0_rst }];
 # SPI1_D00 L18P_14 -> DQ0/SDI --> MOSI
set_property -dict { PACKAGE_PIN N9  IOSTANDARD LVCMOS33 } [get_ports { io_spi0_mosi }];
 # SPI1_D01 L18N_14 -> DQ1/SD0 --> MISO
set_property -dict { PACKAGE_PIN P9  IOSTANDARD LVCMOS33 } [get_ports { io_spi0_miso }];
 # SPI1_D02 L20P_14 -> WP#/DQ2 --> Write Protect 
set_property -dict { PACKAGE_PIN P8  IOSTANDARD LVCMOS33 } [get_ports { io_spi0_wp }];
 # SPI1_D03 L20N_14 -> DQ3/HOLD#/RST# --> HOLD
set_property -dict { PACKAGE_PIN R8  IOSTANDARD LVCMOS33 } [get_ports { io_spi0_hold }];

## I2C
set_property -dict { PACKAGE_PIN G14  IOSTANDARD LVCMOS33 } [get_ports { io_i2c0_scl }];
set_property -dict { PACKAGE_PIN F14  IOSTANDARD LVCMOS33 } [get_ports { io_i2c0_sda }];
