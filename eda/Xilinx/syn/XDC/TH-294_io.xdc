# Board Constraints file for TH-294

## Clock
set_property -dict { PACKAGE_PIN E12  IOSTANDARD LVCMOS33 } [get_ports { io_clock }];

## Reset
set_property -dict { PACKAGE_PIN L5   IOSTANDARD LVCMOS33 } [get_ports { io_sysReset_out }];

## Status
set_property -dict { PACKAGE_PIN K12  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[0] }];
set_property -dict { PACKAGE_PIN L13  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[1] }];
set_property -dict { PACKAGE_PIN K13  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[2] }];
set_property -dict { PACKAGE_PIN L5   IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[3] }];

## Stdout
set_property -dict { PACKAGE_PIN R3   IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_rxd }];
set_property -dict { PACKAGE_PIN T2   IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_txd }];
set_property -dict { PACKAGE_PIN R1   IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_rts }];
set_property -dict { PACKAGE_PIN R2   IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_cts }];

## JTAG
set_property -dict { PACKAGE_PIN R13  IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tms }];
set_property -dict { PACKAGE_PIN N13  IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tdi }];
set_property -dict { PACKAGE_PIN P13  IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tdo }];
set_property -dict { PACKAGE_PIN N14  IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tck }];

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

## GPIO 1 - GPIO1 - GPIO11
set_property -dict { PACKAGE_PIN F5   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[0] }];
set_property -dict { PACKAGE_PIN E5   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[1] }];
set_property -dict { PACKAGE_PIN F4   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[2] }];

set_property -dict { PACKAGE_PIN F3   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[3] }];
set_property -dict { PACKAGE_PIN F2   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[4] }];
set_property -dict { PACKAGE_PIN E1   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[5] }];

set_property -dict { PACKAGE_PIN G5   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[6] }];
set_property -dict { PACKAGE_PIN G4   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[7] }];
set_property -dict { PACKAGE_PIN G2   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[8] }];

set_property -dict { PACKAGE_PIN G1   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[9] }];
set_property -dict { PACKAGE_PIN H5   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[10] }];
set_property -dict { PACKAGE_PIN H4   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[11] }];

set_property -dict { PACKAGE_PIN T4   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[12] }];
set_property -dict { PACKAGE_PIN T3   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[13] }];
set_property -dict { PACKAGE_PIN R5   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[14] }];

set_property -dict { PACKAGE_PIN T5   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[15] }];
set_property -dict { PACKAGE_PIN T7   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[16] }];
set_property -dict { PACKAGE_PIN T8   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[17] }];

set_property -dict { PACKAGE_PIN T9   IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[18] }];
set_property -dict { PACKAGE_PIN T10  IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[19] }];
set_property -dict { PACKAGE_PIN T14  IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[20] }];

set_property -dict { PACKAGE_PIN T15  IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[21] }];
set_property -dict { PACKAGE_PIN R15  IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[22] }];
set_property -dict { PACKAGE_PIN R16  IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[23] }];

set_property -dict { PACKAGE_PIN P15  IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[24] }];
set_property -dict { PACKAGE_PIN P16  IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[25] }];
set_property -dict { PACKAGE_PIN N16  IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[26] }];

set_property -dict { PACKAGE_PIN M16  IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[27] }];
set_property -dict { PACKAGE_PIN G11  IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[28] }];
set_property -dict { PACKAGE_PIN G15  IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[29] }];

set_property -dict { PACKAGE_PIN H14  IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[30] }];
set_property -dict { PACKAGE_PIN J16  IOSTANDARD LVCMOS33 } [get_ports { io_gpio1[31] }];

## GPIO 2 - GPIO11 - GPIO22
set_property -dict { PACKAGE_PIN J15  IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[0] }];

set_property -dict { PACKAGE_PIN G16  IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[1] }];
set_property -dict { PACKAGE_PIN H16  IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[2] }];
set_property -dict { PACKAGE_PIN P6   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[3] }];

set_property -dict { PACKAGE_PIN R11  IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[4] }];
set_property -dict { PACKAGE_PIN R10  IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[5] }];
set_property -dict { PACKAGE_PIN F14  IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[6] }];

set_property -dict { PACKAGE_PIN G14  IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[7] }];
set_property -dict { PACKAGE_PIN H13  IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[8] }];
set_property -dict { PACKAGE_PIN H12  IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[9] }];

set_property -dict { PACKAGE_PIN G12  IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[10] }];
set_property -dict { PACKAGE_PIN H11  IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[11] }];
set_property -dict { PACKAGE_PIN R7   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[12] }];

set_property -dict { PACKAGE_PIN R6   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[13] }];
set_property -dict { PACKAGE_PIN J5   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[14] }];
set_property -dict { PACKAGE_PIN J4   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[15] }];

set_property -dict { PACKAGE_PIN P5   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[16] }];
set_property -dict { PACKAGE_PIN P10  IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[17] }];
set_property -dict { PACKAGE_PIN P11  IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[18] }];

set_property -dict { PACKAGE_PIN P3   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[19] }];
set_property -dict { PACKAGE_PIN P4   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[20] }];
set_property -dict { PACKAGE_PIN N4   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[21] }];

set_property -dict { PACKAGE_PIN M5   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[22] }];
set_property -dict { PACKAGE_PIN K5   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[23] }];
set_property -dict { PACKAGE_PIN K2   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[24] }];

set_property -dict { PACKAGE_PIN K3   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[25] }];
set_property -dict { PACKAGE_PIN L2   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[26] }];
set_property -dict { PACKAGE_PIN L3   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[27] }];

set_property -dict { PACKAGE_PIN J1   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[28] }];
set_property -dict { PACKAGE_PIN K1   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[29] }];
set_property -dict { PACKAGE_PIN H3   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[30] }];

set_property -dict { PACKAGE_PIN J3   IOSTANDARD LVCMOS33 } [get_ports { io_gpio2[31] }];
## GPIO 3 - GPIO22
set_property -dict { PACKAGE_PIN H1   IOSTANDARD LVCMOS33 } [get_ports { io_gpio3[0] }];
set_property -dict { PACKAGE_PIN H2   IOSTANDARD LVCMOS33 } [get_ports { io_gpio3[1] }];
