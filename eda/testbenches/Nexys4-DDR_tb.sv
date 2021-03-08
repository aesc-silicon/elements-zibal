`include "lib/clock.sv"
`include "lib/reset.sv"

module Nexys4DDR_tb;


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
wire  io_uartStd_rts;
bit   io_uartStd_cts;
wire  [3:0] io_gpioStatus;
wire  [4:0] io_gpio1;
wire  [2:0] io_vga0_pixels_r;
wire  [2:0] io_vga0_pixels_g;
wire  [1:0] io_vga0_pixels_b;
wire  io_vga0_hSync;
wire  io_vga0_vSync;


Nexys4DDR_top TOP (
	.io_clock(io_clock),
	.io_sysReset_out(io_sysReset_out),
	.io_jtag_tms(io_jtag_tms),
	.io_jtag_tdi(io_jtag_tdi),
	.io_jtag_tdo(io_jtag_tdo),
	.io_jtag_tck(io_jtag_tck),
	.io_uartStd_txd(io_uartStd_txd),
	.io_uartStd_rxd(io_uartStd_rxd),
	.io_uartStd_rts(io_uartStd_rts),
	.io_uartStd_cts(io_uartStd_cts),
	.io_gpioStatus(io_gpioStatus),
	.io_gpio1(io_gpio1),
	.io_vga0_pixels_r(io_vga0_pixels_r),
	.io_vga0_pixels_g(io_vga0_pixels_g),
	.io_vga0_pixels_b(io_vga0_pixels_b),
	.io_vga0_hSync(io_vga0_hSync),
	.io_vga0_vSync(io_vga0_vSync)
);

endmodule
