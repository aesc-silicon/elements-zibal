module TH277_top (
	input  io_clock,
	inout  [2:0] io_gpioStatus,
	output io_uartStd_txd,
	input  io_uartStd_rxd
);

wire [2:0] gpioStatus_rd;
wire [2:0] gpioStatus_wr;
wire [2:0] gpioStatus_wrEn;

genvar i_gpioStatus;
generate
	for (i_gpioStatus = 0; i_gpioStatus < 3; i_gpioStatus = i_gpioStatus + 1) begin
		gpioStatus_wrEn[i_gpioStatus] ? gpioStatus_wr[i_gpioStatus] : 1'bZ;
		assign gpioStatus_rd[i_gpioStatus] = io_gpioStatus[i_gpioStatus];
	end
endgenerate

Hydrogen SOC (
	.io_clock(io_clock),
	.io_gpioStatus_pins_read(gpioStatus_rd),
	.io_gpioStatus_pins_write(gpioStatus_wr),
	.io_gpioStatus_pins_writeEnable(gpioStatus_wrEn),
	.io_uartStd_txd(io_uartStd_txd),
	.io_uartStd_rxd(io_uartStd_rxd)
);

endmodule

