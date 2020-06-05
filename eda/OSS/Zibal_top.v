module Zibal_top (
	input  io_systemReset,
	input  io_systemClock,
	input  io_jtag_tms,
	input  io_jtag_tdi,
	output io_jtag_tdo,
	input  io_jtag_tck,
	inout  [31:0] io_gpioA,
	inout  [5:0]  io_gpioB,
	output io_uartA_txd,
	input  io_uartA_rxd,
	output io_uartB_txd,
	input  io_uartB_rxd,
	output [7:0] io_sevenSegmentsA_value,
	output [7:0] io_sevenSegmentsA_select,
	output io_spiA_sclk,
	output io_spiA_mosi,
	input  io_spiA_miso,
	output [0:0] io_spiA_ss
);

/* GPIO Tri-State buffer */

wire [31:0] gpioA_rd;
wire [31:0] gpioA_wr;
wire [31:0] gpioA_wrEn;

genvar i_gpioA;
generate
	for (i_gpioA = 0; i_gpioA < 32; i_gpioA = i_gpioA + 1) begin
		assign io_gpioA[i_gpioA] = gpioA_wrEn[i_gpioA] ? gpioA_wr[i_gpioA] : 1'bZ;
		assign gpioA_rd[i_gpioA] = io_gpioA[i_gpioA];
	end
endgenerate

/* Status Tri-State buffer */

wire [5:0] gpioB_rd;
wire [5:0] gpioB_wr;
wire [5:0] gpioB_wrEn;

genvar i_gpioB;
generate
	for (i_gpioB = 0; i_gpioB < 6; i_gpioB = i_gpioB + 1) begin
		assign io_gpioB[i_gpioB] = gpioB_wrEn[i_gpioB] ? gpioB_wr[i_gpioB] : 1'bZ;
		assign gpioB_rd[i_gpioB] = io_gpioB[i_gpioB];
	end
endgenerate

Zibal SoC (
	.io_systemReset(io_systemReset),
	.io_systemClock(io_systemClock),
	.io_jtag_tms(io_jtag_tms),
	.io_jtag_tdi(io_jtag_tdi),
	.io_jtag_tdo(io_jtag_tdo),
	.io_jtag_tck(io_jtag_tck),
	.io_gpioA_pins_read(gpioA_rd),
	.io_gpioA_pins_write(gpioA_wr),
	.io_gpioA_pins_writeEnable(gpioA_wrEn),
	.io_gpioB_pins_read(gpioB_rd),
	.io_gpioB_pins_write(gpioB_wr),
	.io_gpioB_pins_writeEnable(gpioB_wrEn),
	.io_uartA_txd(io_uartA_txd),
	.io_uartA_rxd(io_uartA_rxd),
	.io_uartB_txd(io_uartB_txd),
	.io_uartB_rxd(io_uartB_rxd),
	.io_sevenSegmentsA_value(io_sevenSegmentsA_value),
	.io_sevenSegmentsA_select(io_sevenSegmentsA_select),
	.io_spiA_sclk(io_spiA_sclk),
	.io_spiA_mosi(io_spiA_mosi),
	.io_spiA_miso(io_spiA_miso),
	.io_spiA_ss(io_spiA_ss)
);

initial begin
	$dumpfile("build/Zibal_top.vcd");
	$dumpvars(0, Zibal_top);
	#300000 $finish;
end

endmodule
