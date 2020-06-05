`include "lib/clock.sv"
`include "lib/reset.sv"

module TH253_tb;

// 10ns => 100 MHz

bit io_clock;
clock #(.CLK_PERIOD(10)) gen_io_clock (io_clock);

bit io_reset;
//reset gen_io_reset (io_clock, io_reset);

initial begin
	#10 io_reset = 0;
end

bit   io_jtag_tms;
bit   io_jtag_tdi;
wire  io_jtag_tdo;
bit   io_jtag_tck;
wire  [0:11] io_gpio0;
wire  [2:0] io_gpioStatus;
wire  io_uartStd_txd;
bit   io_uartStd_rxd;
wire  io_uartCom_txd;
bit   io_uartCom_rxd;
wire  io_uartRS232_txd;
bit   io_uartRS232_rxd;

TH253_top TOP (
	.io_clock(io_clock),
	.io_gpioStatus(io_gpioStatus),
	.io_uartStd_txd(io_uartStd_txd),
	.io_uartStd_rxd(io_uartStd_rxd)
);
/*
M25P m25p_tb (
	.io_rst_n(io_systemReset),
	.io_spi_sclk(io_spiA_sclk),
	.io_spi_mosi(io_spiA_mosi),
	.io_spi_miso(io_spiA_miso),
	.io_spi_ss(io_spiA_ss)
);
*/
endmodule
