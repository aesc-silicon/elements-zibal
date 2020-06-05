# Board Constraints file for TH-277

# Clock
set_property -dict { PACKAGE_PIN E12 IOSTANDARD LVCMOS33 } [get_ports { io_clock }];			#L13P_15

# Status
set_property -dict { PACKAGE_PIN K12  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[0] }];		#IO_0_14
set_property -dict { PACKAGE_PIN K13  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[1] }];		#L5P_14
set_property -dict { PACKAGE_PIN L13  IOSTANDARD LVCMOS33 } [get_ports { io_gpioStatus[2] }];		#L5N_14

# Stdout
set_property -dict { PACKAGE_PIN R1  IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_rxd }];		#L7N_34
set_property -dict { PACKAGE_PIN R2  IOSTANDARD LVCMOS33 } [get_ports { io_uartStd_txd }];		#L7P_34
