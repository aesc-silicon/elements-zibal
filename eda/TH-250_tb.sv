`include "lib/clock.sv"
`include "lib/reset.sv"
`include "lib/M25P.sv"
`include "lib/intrGPIO.sv"

module TH250_tb;

// 10ns => 100 MHz

bit io_clock;
clock #(.CLK_PERIOD(4)) gen_io_clock (io_clock);

bit io_reset;
//reset gen_io_reset (io_clock, io_reset);

wire  [11:0] io_gpio0; 

wire  [2:0] io_gpioStatus;



bit   io_jtag_tms;
bit   io_jtag_tdi;
wire  io_jtag_tdo;
bit   io_jtag_tck;
wire  io_uartStd_txd;
bit   io_uartStd_rxd;
wire  io_uartCom_txd;
bit   io_uartCom_rxd;
wire  io_uartRS232_txd;
bit   io_uartRS232_rxd;

// wire io_slotSelect[3:0];

wire io_spi1_sclk;
wire io_spi1_ss;
wire io_spi1_mosi;
bit io_spi1_miso;


intrGPIO #(.DATA_WIDTH(12)) intrGPIO_send (
    .clk(io_clock),
    .gpio0(io_gpio0)
);

TH250_top TOP (
	.io_clock(io_clock),
	.io_gpioStatus(io_gpioStatus),
	.io_uartStd_txd(io_uartStd_txd),
	.io_uartStd_rxd(io_uartStd_rxd),
	.io_spi1_sclk(io_spi1_sclk),
	.io_spi1_ss(io_spi1_ss),
	.io_spi1_mosi(io_spi1_mosi),
	.io_spi1_miso(io_spi1_miso),
	.io_gpio0(io_gpio0)
);

M25P m25p_tb (
	.io_rst_n(io_reset),
	.io_spi_sclk(io_spi1_sclk),
	.io_spi_mosi(io_spi1_mosi),
	.io_spi_miso(io_spi1_miso),
	.io_spi_ss(io_spi1_ss)
);


initial 
begin
	io_reset = 0;
end



endmodule
