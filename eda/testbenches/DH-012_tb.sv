`include "lib/clock.sv"
`include "lib/reset.sv"
`include "lib/MT25Q.sv"

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
clock #(.CLK_PERIOD(100)) gen_io_jtag_tck (io_jtag_tck);
wire  io_uartStd_txd;
bit   io_uartStd_rxd;
wire  io_uartStd_rts;
bit   io_uartStd_cts;
wire  [3:0] io_gpioStatus;
wire  [6:0] io_gpio1;
wire  io_spiXip_ss;
wire  io_spiXip_sclk;
wire  io_spiXip_mosi;
wire  io_spiXip_miso;
wire  io_i2c0_scl;
wire  io_i2c0_sda;

assign io_uartStd_rxd = 1'b1;

initial begin
	#300000 $stop;
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
	.io_gpio1(io_gpio1),
	.io_spiXip_sclk(io_spiXip_sclk),
	.io_spiXip_ss(io_spiXip_ss),
	.io_spiXip_mosi(io_spiXip_mosi),
	.io_spiXip_miso(io_spiXip_miso),
	.io_i2c0_scl(io_i2c0_scl),
	.io_i2c0_sda(io_i2c0_sda)
);

MT25Q SpiNor (
	.io_rst_n(io_reset),
	.io_spi_sclk(io_spiXip_sclk),
	.io_spi_mosi(io_spiXip_mosi),
	.io_spi_miso(io_spiXip_miso),
	.io_spi_ss(io_spiXip_ss)
);

endmodule
