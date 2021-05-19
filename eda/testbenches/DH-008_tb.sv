`include "lib/clock.sv"
`include "lib/reset.sv"

module DH008_tb;


bit io_clock;
// 10ns => 100 MHz
clock #(.CLK_PERIOD(10)) gen_io_clock (io_clock);
bit   io_jtag_tms;
bit   io_jtag_tdi;
wire  io_jtag_tdo;
bit   io_jtag_tck;
wire  io_uartStd_txd;
bit   io_uartStd_rxd;
wire  io_uartStd_rts;
bit   io_uartStd_cts;
wire  [3:0] io_gpioStatus;
wire  [23:0] io_gpioController;
wire  io_spi0_sclk;
wire  io_spi0_ss;
wire  io_spi0_mosi;
bit   io_spi0_miso;
wire  io_i2c0_scl;
wire  io_i2c0_sda;
wire  [3:0] io_vga0_pixels_r;
wire  [3:0] io_vga0_pixels_g;
wire  [3:0] io_vga0_pixels_b;
wire  io_vga0_hSync;
wire  io_vga0_vSync;

assign io_gpioController = 0;

Hydrogen4_top TOP (
	.io_clock(io_clock),
	.io_jtag_tms(io_jtag_tms),
	.io_jtag_tdi(io_jtag_tdi),
	.io_jtag_tdo(io_jtag_tdo),
	.io_jtag_tck(io_jtag_tck),
	.io_uartStd_txd(io_uartStd_txd),
	.io_uartStd_rxd(io_uartStd_rxd),
	.io_uartStd_rts(io_uartStd_rts),
	.io_uartStd_cts(io_uartStd_cts),
	.io_gpioStatus(io_gpioStatus),
	.io_gpioController(io_gpioController),
	.io_spi0_sclk(io_spi0_sclk),
	.io_spi0_ss(io_spi0_ss),
	.io_spi0_mosi(io_spi0_mosi),
	.io_spi0_miso(io_spi0_miso),
	.io_i2c0_scl(io_i2c0_scl),
	.io_i2c0_sda(io_i2c0_sda),
	.io_vga0_pixels_r(io_vga0_pixels_r),
	.io_vga0_pixels_g(io_vga0_pixels_g),
	.io_vga0_pixels_b(io_vga0_pixels_b),
	.io_vga0_hSync(io_vga0_hSync),
	.io_vga0_vSync(io_vga0_vSync)
);

endmodule
