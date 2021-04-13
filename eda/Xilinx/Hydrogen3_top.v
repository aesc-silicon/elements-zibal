`define GPIO_STATUS_NO		4
`define GPIO_1_NO		5

module Hydrogen3_top (
	input  io_clock,
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
	inout  [`GPIO_1_NO - 1:0] io_gpio1,
	output [2:0] io_vga0_pixels_r,
	output [2:0] io_vga0_pixels_g,
	output [1:0] io_vga0_pixels_b,
	output io_vga0_hSync,
	output io_vga0_vSync
);

assign reset = 1'b0;

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

wire [`GPIO_1_NO - 1:0] gpio1_rd;
wire [`GPIO_1_NO - 1:0] gpio1_wr;
wire [`GPIO_1_NO - 1:0] gpio1_wrEn;

IOBUF#(
	.DRIVE(12),
	.IBUF_LOW_PWR("TRUE"),
	.IOSTANDARD("DEFAULT"),
	.SLEW("SLOW")
) IOBUF_gpio1[`GPIO_1_NO - 1:0] (
	.O(gpio1_rd),
	.IO(io_gpio1),
	.I(gpio1_wr),
	// high = input, low = output
	.T(!gpio1_wrEn)
);

Hydrogen3 SOC (
	.io_sys_clock(io_clock),
	.io_sys_reset(reset),
	.io_sys_sysReset_out(io_sysReset_out),
	.io_sys_jtag_tms(io_jtag_tms),
	.io_sys_jtag_tdi(io_jtag_tdi),
	.io_sys_jtag_tdo(io_jtag_tdo),
	.io_sys_jtag_tck(io_jtag_tck),
	.io_per_uartStd_txd(io_uartStd_txd),
	.io_per_uartStd_rxd(io_uartStd_rxd),
	.io_per_uartStd_rts(io_uartStd_rts),
	.io_per_uartStd_cts(io_uartStd_cts),
	.io_per_gpioStatus_pins_read(gpioStatus_rd),
	.io_per_gpioStatus_pins_write(gpioStatus_wr),
	.io_per_gpioStatus_pins_writeEnable(gpioStatus_wrEn),
	.io_per_gpio1_pins_read(gpio1_rd),
	.io_per_gpio1_pins_write(gpio1_wr),
	.io_per_gpio1_pins_writeEnable(gpio1_wrEn),
	.io_per_vga0_pixels_r(io_vga0_pixels_r),
	.io_per_vga0_pixels_g(io_vga0_pixels_g),
	.io_per_vga0_pixels_b(io_vga0_pixels_b),
	.io_per_vga0_hSync(io_vga0_hSync),
	.io_per_vga0_vSync(io_vga0_vSync)
);

endmodule
