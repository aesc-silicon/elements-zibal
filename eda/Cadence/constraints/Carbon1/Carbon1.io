(globals
	version = 3
	io_order = default
)

(iopad
	(top
		(inst name="sys_clock_pad" cell="ixc013_i16x")
		(inst name="sys_reset_pad" cell="ixc013_i16x")
		(inst name="gndcore_0" cell="gndcore")
		(inst name="gndcore_1" cell="gndcore")
		(inst name="gndcore_2" cell="gndcore")
		(inst name="sys_sysReset_out_pad" cell="ixc013_b16m")
		(inst name="per_gpioStatus0_pad" cell="ixc013_b16m")
	)
	(topright
		(inst name="CORNER1" cell="corner" orientation=R90)
	)
	(right
		(inst name="sys_jtag_tms_pad" cell="ixc013_b16m")
		(inst name="sys_jtag_tdi_pad" cell="ixc013_b16m")
		(inst name="vddcore_0" cell="vddcore")
		(inst name="vddcore_1" cell="vddcore")
		(inst name="sys_jtag_tdo_pad" cell="ixc013_b16m")
		(inst name="sys_jtag_tck_pad" cell="ixc013_b16m")
		(inst name="per_gpioStatus1_pad" cell="ixc013_b16m")
	)
	(bottomright
		(inst name="CORNER2" cell="corner" orientation=R0)
	)
	(bottom
		(inst name="per_uartStd_txd_pad" cell="ixc013_b16m")
		(inst name="per_uartStd_rxd_pad" cell="ixc013_b16m")
		(inst name="gndpad_0" cell="gndpad")
		(inst name="vddpad_0" cell="vddpad")
		(inst name="per_uartStd_cts_pad" cell="ixc013_b16m")
		(inst name="per_uartStd_rts_pad" cell="ixc013_b16m")
		(inst name="per_gpioStatus2_pad" cell="ixc013_b16m")
	)
	(bottomleft
		(inst name="CORNER3" cell="corner" orientation=R270)
	)
	(left
		(inst name="per_spi0_ss_pad" cell="ixc013_b16m")
		(inst name="per_spi0_sclk_pad" cell="ixc013_b16m")
		(inst name="gndpad_1" cell="gndpad")
		(inst name="vddpad_1" cell="vddpad")
		(inst name="per_spi0_mosi_pad" cell="ixc013_b16m")
		(inst name="per_spi0_miso_pad" cell="ixc013_b16m")
		(inst name="per_gpioStatus3_pad" cell="ixc013_b16m")
	)
	(topleft
		(inst name="CORNER4" cell="corner" orientation=R180)
	)
)
