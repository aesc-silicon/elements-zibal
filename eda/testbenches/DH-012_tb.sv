`include "lib/clock.sv"
`include "lib/reset.sv"

module DH012_tb;


bit io_clock;
// 20ns => 50 MHz
clock #(.CLK_PERIOD(20)) gen_io_clock (io_clock);
bit io_reset;
reset gen_io_reset (io_clock, io_reset);
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
wire  io_spi0_ss;
wire  io_spi0_sclk;
wire  io_spi0_mosi;
bit   io_spi0_miso;

assign io_uartStd_rxd = 1'b1;

initial begin
	#300000 $finish;
end

Carbon1_top TOP (
	.io_clock(io_clock),
	.io_reset(io_reset),
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
	.io_spi0_sclk(io_spi0_sclk),
	.io_spi0_ss(io_spi0_ss),
	.io_spi0_mosi(io_spi0_mosi),
	.io_spi0_miso(io_spi0_miso)
);

endmodule
