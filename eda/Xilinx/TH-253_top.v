module TH253_top (
	input  io_clock,
	inout  [2:0] io_gpioStatus,
	output io_uartStd_txd,
	input  io_uartStd_rxd
);

/* GPIO Tri-State buffer */
/*
wire [0:11] gpio0_rd;
wire [0:11] gpio0_wr;
wire [0:11] gpio0_wrEn;

genvar i_gpio0;
generate
	for (i_gpio0 = 0; i_gpio0 < 12; i_gpio0 = i_gpio0 + 1) begin
		assign io_gpio0[i_gpio0] = gpio0_wrEn[i_gpio0] ? gpio0_wr[i_gpio0] : 1'bZ;
		assign gpio0_rd[i_gpio0] = io_gpio0[i_gpio0];
	end
endgenerate

wire [4:0] gpioStatus_rd;
wire [4:0] gpioStatus_wr;
wire [4:0] gpioStatus_wrEn;
*/
genvar i_gpioStatus;
generate
	for (i_gpioStatus = 0; i_gpioStatus < 5; i_gpioStatus = i_gpioStatus + 1) begin
		assign io_gpioStatus[i_gpioStatus] = gpioStatus_wrEn[i_gpioStatus] ? gpioStatus_wr[i_gpioStatus] : 1'bZ;
		assign gpioStatus_rd[i_gpioStatus] = io_gpioStatus[i_gpioStatus];
	end
endgenerate

Hydrogen SOC (
	.io_clock(io_clock),
	.io_reset(io_reset),
	.io_jtag_tms(io_jtag_tms),
	.io_jtag_tdi(io_jtag_tdi),
	.io_jtag_tdo(io_jtag_tdo),
	.io_jtag_tck(io_jtag_tck),
	.io_gpio0_pins_read(gpio0_rd),
	.io_gpio0_pins_write(gpio0_wr),
	.io_gpio0_pins_writeEnable(gpio0_wrEn),
	.io_gpioStatus_pins_read(gpioStatus_rd),
	.io_gpioStatus_pins_write(gpioStatus_wr),
	.io_gpioStatus_pins_writeEnable(gpioStatus_wrEn),
	.io_uartStd_txd(io_uartStd_txd),
	.io_uartStd_rxd(io_uartStd_rxd),
	.io_uartCom_txd(io_uartCom_txd),
	.io_uartCom_rxd(io_uartCom_rxd),
	.io_uartRS232_txd(io_uartRS232_txd),
	.io_uartRS232_rxd(io_uartRS232_rxd)
);

endmodule
