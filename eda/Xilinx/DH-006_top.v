`define GPIO_STATUS_NO		4
`define GPIO_1_NO		15

module DH006_top (
	input  io_clock,
	output io_sysReset_out,
	input  io_jtag_tms,
	input  io_jtag_tdi,
	output io_jtag_tdo,
	input  io_jtag_tck,
	output io_uartStd_txd,
	input  io_uartStd_rxd,
	inout  [`GPIO_STATUS_NO - 1:0] io_gpioStatus,
	inout  [`GPIO_1_NO - 1:0] io_gpio1,
	output io_spi0_sclk,
	output [0:0] io_spi0_ss,
	output io_spi0_mosi,
	input  io_spi0_miso,
	output io_spi0_rst,
	output io_spi0_wp,
	output io_spi0_hold,
	inout  io_i2c0_scl,
	inout  io_i2c0_sda
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


wire i2c0_scl_read;
wire i2c0_sda_read;
wire i2c0_scl_write;
wire i2c0_sda_write;

PULLUP PU_i2c0_scl (
	.O(io_i2c0_scl)
);
PULLUP PU_i2c0_sda (
	.O(io_i2c0_sda)
);

IBUF IBUF_i2c0_scl_read (
	.O(i2c0_scl_read),
	.I(io_i2c0_scl)
);
IBUF IBUF_i2c0_sda_read (
	.O(i2c0_sda_read),
	.I(io_i2c0_sda)
);

OBUFT OBUFT_i2c0_scl_write (
	.O(io_i2c0_scl),
	.I(1'b0),
	.T(!i2c0_scl_write)
);
OBUFT OBUFT_i2c0_sda_write (
	.O(io_i2c0_sda),
	.I(1'b0),
	.T(!i2c0_sda_write)
);

Hydrogen1 SOC (
	.io_sys_clock(io_clock),
	.io_sys_reset(reset),
	.io_sys_sysReset_out(io_sysReset_out),
	.io_sys_jtag_tms(io_jtag_tms),
	.io_sys_jtag_tdi(io_jtag_tdi),
	.io_sys_jtag_tdo(io_jtag_tdo),
	.io_sys_jtag_tck(io_jtag_tck),
	.io_per_uartStd_txd(io_uartStd_txd),
	.io_per_uartStd_rxd(io_uartStd_rxd),
	.io_per_gpioStatus_pins_read(gpioStatus_rd),
	.io_per_gpioStatus_pins_write(gpioStatus_wr),
	.io_per_gpioStatus_pins_writeEnable(gpioStatus_wrEn),
	.io_per_gpio1_pins_read(gpio1_rd),
	.io_per_gpio1_pins_write(gpio1_wr),
	.io_per_gpio1_pins_writeEnable(gpio1_wrEn),
	.io_per_spi0_ss(io_spi0_ss),
	.io_per_spi0_sclk(io_spi0_sclk),
	.io_per_spi0_mosi(io_spi0_mosi),
	.io_per_spi0_miso(io_spi0_miso),
	.io_per_i2c0_scl_write(i2c0_scl_write),
	.io_per_i2c0_scl_read(i2c0_scl_read),
	.io_per_i2c0_sda_write(i2c0_sda_write),
	.io_per_i2c0_sda_read(i2c0_sda_read)
);

endmodule
