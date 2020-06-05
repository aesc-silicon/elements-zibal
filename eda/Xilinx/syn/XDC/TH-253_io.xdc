# Board Constraints file for TH-253

# Clock
set_property -dict { PACKAGE_PIN E12 IOSTANDARD LVCMOS33 } [get_ports { io_clock }];

# Status
set_property -dict { PACKAGE_PIN K12  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[0] }];
set_property -dict { PACKAGE_PIN K13  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[1] }];
set_property -dict { PACKAGE_PIN L13  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[2] }];

# JTAG
#set_property -dict { PACKAGE_PIN P16 IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tms }];
#set_property -dict { PACKAGE_PIN N16 IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tdi }];
#set_property -dict { PACKAGE_PIN P15 IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tdo }];
#set_property -dict { PACKAGE_PIN N14 IOSTANDARD LVCMOS33 } [get_ports { io_jtag_tck }];

# Stdout
set_property -dict { PACKAGE_PIN R1  IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_rxd }];
set_property -dict { PACKAGE_PIN R2  IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_txd }];

# Command Protocol
#set_property -dict { PACKAGE_PIN H11 IOSTANDARD LVCMOS33 } [get_ports { io_uartRS232_rxd }];
#set_property -dict { PACKAGE_PIN G12 IOSTANDARD LVCMOS33 } [get_ports { io_uartRS232_txd }];

# Serial UUT
#set_property -dict { PACKAGE_PIN L3  IOSTANDARD LVCMOS33 } [get_ports { io_uartCom_rxd }];
#set_property -dict { PACKAGE_PIN L2  IOSTANDARD LVCMOS33 } [get_ports { io_uartCom_txd }];

# gpio0s
#set_property -dict { PACKAGE_PIN M6  IOSTANDARD LVCMOS33 } [get_ports { io_gpio0[0] }];
#set_property -dict { PACKAGE_PIN N6  IOSTANDARD LVCMOS33 } [get_ports { io_gpio0[1] }];
#set_property -dict { PACKAGE_PIN P8  IOSTANDARD LVCMOS33 } [get_ports { io_gpio0[2] }];
#set_property -dict { PACKAGE_PIN R8  IOSTANDARD LVCMOS33 } [get_ports { io_gpio0[3] }];

#set_property -dict { PACKAGE_PIN T7  IOSTANDARD LVCMOS33 } [get_ports { io_gpio0[4] }];
#set_property -dict { PACKAGE_PIN T8  IOSTANDARD LVCMOS33 } [get_ports { io_gpio0[5] }];
#set_property -dict { PACKAGE_PIN T9  IOSTANDARD LVCMOS33 } [get_ports { io_gpio0[6] }];
#set_property -dict { PACKAGE_PIN T10 IOSTANDARD LVCMOS33 } [get_ports { io_gpio0[7] }];

#set_property -dict { PACKAGE_PIN R5  IOSTANDARD LVCMOS33 } [get_ports { io_gpio0[8] }];
#set_property -dict { PACKAGE_PIN T5  IOSTANDARD LVCMOS33 } [get_ports { io_gpio0[9] }];
#set_property -dict { PACKAGE_PIN R6  IOSTANDARD LVCMOS33 } [get_ports { io_gpio0[10] }];
#set_property -dict { PACKAGE_PIN R7  IOSTANDARD LVCMOS33 } [get_ports { io_gpio0[11] }];

