`define GPIO_STATUS_NO		4

module Carbon1_top (
	input  io_clock,
	input  io_reset,
	output io_sysReset_out,
	input  io_jtag_tms,
	input  io_jtag_tdi,
	output io_jtag_tdo,
	input  io_jtag_tck,
	output io_uartStd_txd,
	input  io_uartStd_rxd,
	input  io_uartStd_cts,
	output io_uartStd_rts,
	inout  [`GPIO_STATUS_NO - 1:0] io_gpioStatus,
	output [0:0] io_spi0_ss,
	output io_spi0_sclk,
	output io_spi0_mosi,
	input  io_spi0_miso
);


wire [`GPIO_STATUS_NO - 1:0] gpioStatus_pins_read;
wire [`GPIO_STATUS_NO - 1:0] gpioStatus_pins_write;
wire [`GPIO_STATUS_NO - 1:0] gpioStatus_pins_writeEnable;

wire [`GPIO_STATUS_NO - 1:0] gpioStatus;

generate
	genvar i;
	for (i = 0; i < `GPIO_STATUS_NO; i = i + 1) begin
		assign gpioStatus[i] = gpioStatus_pins_writeEnable[i] ? gpioStatus_pins_write[i] : 1'bZ;
		assign gpioStatus_pins_read[i] = gpioStatus[i];
	end
endgenerate

initial begin
	$dumpfile(`VCD);
	$dumpvars(0, Carbon1_top);
end


Carbon1 SOC (
	.io_sys_clock(io_clock),
	.io_sys_reset(io_reset),
	.io_sys_sysReset_out(io_sysReset_out),
	.io_sys_jtag_tms(io_jtag_tms),
	.io_sys_jtag_tdi(io_jtag_tdi),
	.io_sys_jtag_tdo(io_jtag_tdo),
	.io_sys_jtag_tck(io_jtag_tck),
	.io_per_uartStd_txd(io_uartStd_txd),
	.io_per_uartStd_rxd(io_uartStd_rxd),
	.io_per_uartStd_cts(io_uartStd_cts),
	.io_per_uartStd_rts(io_uartStd_rts),
	.io_per_gpioStatus_pins_read(gpioStatus_pins_read),
	.io_per_gpioStatus_pins_write(gpioStatus_pins_write),
	.io_per_gpioStatus_pins_writeEnable(gpioStatus_pins_writeEnable),
	.io_per_spi0_ss(io_spi0_ss),
	.io_per_spi0_sclk(io_spi0_sclk),
	.io_per_spi0_mosi(io_spi0_mosi),
	.io_per_spi0_miso(io_spi0_miso)
);

endmodule
