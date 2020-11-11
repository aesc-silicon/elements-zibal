`define GPIO_STATUS_NO		4

module TH283_top (
	input  io_clock,
	output io_sysReset_out,
	input  io_jtag_tms,
	input  io_jtag_tdi,
	output io_jtag_tdo,
	input  io_jtag_tck,
	output io_uartStd_txd,
	input  io_uartStd_rxd,
	inout  [`GPIO_STATUS_NO - 1:0] io_gpioStatus,
	output io_spi0_sclk,
	output [0:0] io_spi0_ss,
	output io_spi0_mosi,
	input  io_spi0_miso,
	output io_spi0_rst,
	output io_spi0_wp,
	output io_spi0_hold
);

assign reset = 1'b0;
assign io_spi0_hold = 1'b1;
assign io_spi0_wp = 1'b1;
assign io_spi0_rst = 1'b1;

wire [`GPIO_STATUS_NO - 1:0] gpioStatus_rd;
wire [`GPIO_STATUS_NO - 1:0] gpioStatus_wr;
wire [`GPIO_STATUS_NO - 1:0] gpioStatus_wrEn;

generate
	genvar i;
	for (i = 0; i < `GPIO_STATUS_NO; i = i + 1) begin
		assign io_gpioStatus[i] = gpioStatus_wrEn[i] ? gpioStatus_wr[i] : 1'bZ;
		assign gpioStatus_rd[i] = io_gpioStatus[i];
	end
endgenerate


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
	.io_per_spi0_ss(io_spi0_ss),
	.io_per_spi0_sclk(io_spi0_sclk),
	.io_per_spi0_mosi(io_spi0_mosi),
	.io_per_spi0_miso(io_spi0_miso)
);

initial begin
	$dumpfile("TH283_top.vcd");
	$dumpvars(0, TH283_top);
	#300000 $finish;
end

endmodule
