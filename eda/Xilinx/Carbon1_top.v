`define GPIO_STATUS_NO		4

module Carbon1_top (
	input  io_clock,
	input  io_reset,
	output io_sysReset_out,
	input  io_jtag_tms,
	input  io_jtag_tdi,
	output io_jtag_tdo,
	input  io_jtag_tck,
	output io_uartStd_txd,
	input  io_uartStd_rxd,
	output io_uartStd_rts,
	input  io_uartStd_cts,
	inout  [`GPIO_STATUS_NO - 1:0] io_gpioStatus,
	output io_spi0_sclk,
	output [0:0] io_spi0_ss,
	output io_spi0_mosi,
	input  io_spi0_miso,
	output io_spi0_rst,
	output io_spi0_wp,
	output io_spi0_hold
);

assign io_spi0_hold = 1'b1;
assign io_spi0_wp = 1'b1;
assign io_spi0_rst = 1'b1;

wire [`GPIO_STATUS_NO - 1:0] gpioStatus_pins_read;
wire [`GPIO_STATUS_NO - 1:0] gpioStatus_pins_write;
wire [`GPIO_STATUS_NO - 1:0] gpioStatus_pins_writeEnable;

IOBUF#(
	.DRIVE(12),
	.IBUF_LOW_PWR("TRUE"),
	.IOSTANDARD("DEFAULT"),
	.SLEW("SLOW")
) IOBUF_gpioStatus[`GPIO_STATUS_NO - 1:0] (
	.O(gpioStatus_pins_read),
	.IO(io_gpioStatus),
	.I(gpioStatus_pins_write),
	// high = input, low = output
	.T(!gpioStatus_pins_writeEnable)
);

Carbon1 SOC (
	.io_sys_clock(io_clock),
	.io_sys_reset(io_reset),
	.io_sys_sysReset_out(io_sysReset_out),
	.io_sys_jtag_tms(io_jtag_tms),
	.io_sys_jtag_tdi(io_jtag_tdi),
	.io_sys_jtag_tdo(io_jtag_tdo),
	.io_sys_jtag_tck(io_jtag_tck),
	.io_per_uartStd_txd(io_uartStd_txd),
	.io_per_uartStd_rxd(io_uartStd_rxd),
	.io_per_uartStd_cts(io_uartStd_cts),
	.io_per_uartStd_rts(io_uartStd_rts),
	.io_per_gpioStatus_pins_read(gpioStatus_pins_read),
	.io_per_gpioStatus_pins_write(gpioStatus_pins_write),
	.io_per_gpioStatus_pins_writeEnable(gpioStatus_pins_writeEnable),
	.io_per_spi0_ss(io_spi0_ss),
	.io_per_spi0_sclk(io_spi0_sclk),
	.io_per_spi0_mosi(io_spi0_mosi),
	.io_per_spi0_miso(io_spi0_miso)
);

endmodule
