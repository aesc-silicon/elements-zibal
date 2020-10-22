`define GPIO_STATUS_NO		4

module TH283_top (
	input  io_clock,
	output io_sysReset_out,
	input  io_jtag_tms,
	input  io_jtag_tdi,
	output io_jtag_tdo,
	input  io_jtag_tck,
	output io_uartStd_txd,
	input  io_uartStd_rxd,
	inout  [`GPIO_STATUS_NO - 1:0] io_gpioStatus,
	output io_spi0_sclk,
	output [0:0] io_spi0_ss,
	output io_spi0_mosi,
	input  io_spi0_miso,
	output io_spi0_rst,
	output io_spi0_wp,
	output io_spi0_hold
);

assign reset = 1'b0;
assign io_spi0_hold = 1'b1;
assign io_spi0_wp = 1'b1;
assign io_spi0_rst = 1'b1;

wire [`GPIO_STATUS_NO - 1:0] gpioStatus_rd;
wire [`GPIO_STATUS_NO - 1:0] gpioStatus_wr;
wire [`GPIO_STATUS_NO - 1:0] gpioStatus_wrEn;

IOBUF#(
	.DRIVE(12),
	.IBUF_LOW_PWR("TRUE"),
	.IOSTANDARD("DEFAULT"),
	.SLEW("SLOW")
) IOBUF_gpioStatus[`GPIO_STATUS_NO - 1:0] (
	.O(gpioStatus_rd),
	.IO(io_gpioStatus),
	.I(gpioStatus_wr),
	// high = input, low = output
	.T(!gpioStatus_wrEn)
);

Hydrogen SOC (
	.io_clock(io_clock),
	.io_reset(reset),
	.io_sysReset_out(io_sysReset_out),
	.io_jtag_tms(io_jtag_tms),
	.io_jtag_tdi(io_jtag_tdi),
	.io_jtag_tdo(io_jtag_tdo),
	.io_jtag_tck(io_jtag_tck),
	.io_uartStd_txd(io_uartStd_txd),
	.io_uartStd_rxd(io_uartStd_rxd),
	.io_gpioStatus_pins_read(gpioStatus_rd),
	.io_gpioStatus_pins_write(gpioStatus_wr),
	.io_gpioStatus_pins_writeEnable(gpioStatus_wrEn),
	.io_spi0_ss(io_spi0_ss),
	.io_spi0_sclk(io_spi0_sclk),
	.io_spi0_mosi(io_spi0_mosi),
	.io_spi0_miso(io_spi0_miso)
);

endmodule
