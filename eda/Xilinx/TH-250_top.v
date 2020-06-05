module TH250_top (
	input  io_clock,
	inout  [2:0] io_gpioStatus,
	output io_uartStd_txd,
	input  io_uartStd_rxd,
	output io_spi1_sclk,
	output io_spi1_ss,
	output io_spi1_mosi, 
	input io_spi1_miso,
	output io_spi1_rst,
	output io_spi1_wp,
	output io_spi1_hold,
	input io_jtag_tms,
	input io_jtag_tdi,
	output io_jtag_tdo,
	input io_jtag_tck,
	inout [11:0] io_gpio0
);

wire [2:0] gpioStatus_rd;
wire [2:0] gpioStatus_wr;
wire [2:0] gpioStatus_wrEn;
wire sysReset;

// assign io_spi1_rst = sysReset;

assign io_spi1_hold = 1'b1;
assign io_spi1_wp = 1'b1;
assign io_spi1_rst = 1'b1;


/* GPIO Tri-State buffer */

wire [0:11] gpio0_rd;
wire [0:11] gpio0_wr;
wire [0:11] gpio0_wrEn;

genvar i_gpio0;
generate
	for (i_gpio0 = 0; i_gpio0 < 12; i_gpio0 = i_gpio0 + 1) begin
		assign io_gpio0[i_gpio0] = gpio0_wrEn[i_gpio0] ? gpio0_wr[i_gpio0] : 1'bZ;
		assign gpio0_rd[i_gpio0] = io_gpio0[i_gpio0];
	end
endgenerate

genvar i_gpioStatus;
generate
	for (i_gpioStatus = 0; i_gpioStatus < 3; i_gpioStatus = i_gpioStatus + 1) begin
		assign io_gpioStatus[i_gpioStatus] = gpioStatus_wrEn[i_gpioStatus] ? gpioStatus_wr[i_gpioStatus] : 1'bZ;
		assign gpioStatus_rd[i_gpioStatus] = io_gpioStatus[i_gpioStatus];
	end
endgenerate

Hydrogen SOC (
	.io_clock(io_clock),
	.io_gpioStatus_pins_read(gpioStatus_rd),
	.io_gpioStatus_pins_write(gpioStatus_wr),
	.io_gpioStatus_pins_writeEnable(gpioStatus_wrEn),
	.io_uartStd_txd(io_uartStd_txd),
	.io_uartStd_rxd(io_uartStd_rxd),
	.io_spi0_ss(io_spi1_ss),
	.io_spi0_sclk(io_spi1_sclk),
	.io_spi0_mosi(io_spi1_mosi),
	.io_spi0_miso(io_spi1_miso),
	.io_sysReset_out(sysReset),
	.io_jtag_tms(io_jtag_tms),
	.io_jtag_tdi(io_jtag_tdi),
	.io_jtag_tdo(io_jtag_tdo),
	.io_jtag_tck(io_jtag_tck),
	.io_gpio0_pins_read(gpio0_rd),
	.io_gpio0_pins_write(gpio0_wr),
	.io_gpio0_pins_writeEnable(gpio0_wrEn)
);

endmodule

