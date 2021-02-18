# Board Constraints file for Nexys 4 DDR

## Clock
set_property -dict { PACKAGE_PIN E3  IOSTANDARD LVCMOS33 } [get_ports { io_clock }];

## Reset
set_property -dict { PACKAGE_PIN R11  IOSTANDARD LVCMOS33 } [get_ports { io_sysReset_out }];

## Status
set_property -dict { PACKAGE_PIN R12  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[0] }];
set_property -dict { PACKAGE_PIN M16  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[1] }];
set_property -dict { PACKAGE_PIN N15  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[2] }];
set_property -dict { PACKAGE_PIN C12  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[3] }];

## Stdout
set_property -dict { PACKAGE_PIN D4   IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_txd }];
set_property -dict { PACKAGE_PIN C4   IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_rxd }];
set_property -dict { PACKAGE_PIN D3   IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_rts }];
set_property -dict { PACKAGE_PIN E5   IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_cts }];

## JTAG
set_property -dict { PACKAGE_PIN J15  IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tms }];
set_property -dict { PACKAGE_PIN L16  IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tdi }];
set_property -dict { PACKAGE_PIN M13  IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tdo }];
set_property -dict { PACKAGE_PIN R15  IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tck }];
