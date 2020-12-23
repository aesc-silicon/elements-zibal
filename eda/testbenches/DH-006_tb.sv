`include "lib/clock.sv"
`include "lib/reset.sv"

module DH006_tb;


bit io_clock;
// 10ns => 100 MHz
clock #(.CLK_PERIOD(10)) gen_io_clock (io_clock);
wire  io_sysReset_out;
bit   io_jtag_tms;
bit   io_jtag_tdi;
wire  io_jtag_tdo;
bit   io_jtag_tck;
wire  io_uartStd_txd;
bit   io_uartStd_rxd;
wire  [3:0] io_gpioStatus;
wire  [14:0] io_gpio1;
wire  io_spi0_sclk;
wire  io_spi0_ss;
wire  io_spi0_mosi;
bit   io_spi0_miso;
wire  io_i2c0_scl;
wire  io_i2c0_sda;


DH006_top TOP (
	.io_clock(io_clock),
	.io_sysReset_out(io_sysReset_out),
	.io_jtag_tms(io_jtag_tms),
	.io_jtag_tdi(io_jtag_tdi),
	.io_jtag_tdo(io_jtag_tdo),
	.io_jtag_tck(io_jtag_tck),
	.io_uartStd_txd(io_uartStd_txd),
	.io_uartStd_rxd(io_uartStd_rxd),
	.io_gpioStatus(io_gpioStatus),
	.io_gpio1(io_gpio1),
	.io_spi0_sclk(io_spi0_sclk),
	.io_spi0_ss(io_spi0_ss),
	.io_spi0_mosi(io_spi0_mosi),
	.io_spi0_miso(io_spi0_miso),
	.io_i2c0_scl(io_i2c0_scl),
	.io_i2c0_sda(io_i2c0_sda)
);

endmodule
