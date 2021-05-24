`define GPIO_STATUS_NO		4

module Carbon1_top (
	input  io_clock,
	output io_sysReset_out,
	input  io_jtag_tms,
	input  io_jtag_tdi,
	output io_jtag_tdo,
	input  io_jtag_tck,
	output io_spiXip_sclk,
	output [0:0] io_spiXip_ss,
	output io_spiXip_mosi,
	input  io_spiXip_miso,
	output io_spiXip_rst,
	output io_spiXip_wp,
	output io_spiXip_hold,
	output io_uartStd_txd,
	input  io_uartStd_rxd,
	output io_uartStd_rts,
	input  io_uartStd_cts,
	inout  [`GPIO_STATUS_NO - 1:0] io_gpioStatus,
	inout  io_i2c0_scl,
	inout  io_i2c0_sda,

	output debug_spiXip_sclk,
	output debug_spiXip_ss,
	output debug_spiXip_mosi,
	output degbu_spiXip_miso
);

// Make SPI pins available for debugging
assign debug_spiXip_sclk = io_spiXip_sclk;
assign debug_spiXip_ss = io_spiXip_ss;
assign debug_spiXip_mosi = io_spiXip_mosi;
assign debug_spiXip_miso = io_spiXip_miso;

// Design has only 50 MHz
reg clock = 0;
always @ (posedge io_clock)
begin
	clock <= !clock;
end

// Generate active-low reset signal
reg io_reset = 0;
reg [5:0] counter = ~0;
always @ (posedge io_clock)
begin
	counter <= counter - 1;
	if (counter == 0) begin
		io_reset <= 1'b1;
	end
end


PULLUP PU_io_spiXip_ss (
	.O(io_spiXip_ss)
);

assign io_spiXip_hold = 1'b1;
assign io_spiXip_wp = 1'b1;
assign io_spiXip_rst = io_reset;

wire [`GPIO_STATUS_NO - 1:0] gpioStatus_pins_read;
wire [`GPIO_STATUS_NO - 1:0] gpioStatus_pins_write;
wire [`GPIO_STATUS_NO - 1:0] gpioStatus_pins_writeEnable;

IOBUF#(
	.DRIVE(12),
	.IBUF_LOW_PWR("TRUE"),
	.IOSTANDARD("DEFAULT"),
	.SLEW("SLOW")
) IOBUF_gpioStatus[`GPIO_STATUS_NO - 1:0] (
	.O(gpioStatus_pins_read),
	.IO(io_gpioStatus),
	.I(gpioStatus_pins_write),
	// high = input, low = output
	.T(!gpioStatus_pins_writeEnable)
);

wire i2c0_scl_read;
wire i2c0_sda_read;
wire i2c0_scl_write;
wire i2c0_sda_write;

PULLUP PU_i2c0_scl (
	.O(io_i2c0_scl)
);
PULLUP PU_i2c0_sda (
	.O(io_i2c0_sda)
);

IBUF IBUF_i2c0_scl_read (
	.O(i2c0_scl_read),
	.I(io_i2c0_scl)
);
IBUF IBUF_i2c0_sda_read (
	.O(i2c0_sda_read),
	.I(io_i2c0_sda)
);

OBUFT OBUFT_i2c0_scl_write (
	.O(io_i2c0_scl),
	.I(1'b0),
	.T(!i2c0_scl_write)
);
OBUFT OBUFT_i2c0_sda_write (
	.O(io_i2c0_sda),
	.I(1'b0),
	.T(!i2c0_sda_write)
);

Carbon1 SOC (
	.io_sys_clock(clock),
	.io_sys_reset(io_reset),
	.io_sys_sysReset_out(io_sysReset_out),
	.io_sys_jtag_tms(io_jtag_tms),
	.io_sys_jtag_tdi(io_jtag_tdi),
	.io_sys_jtag_tdo(io_jtag_tdo),
	.io_sys_jtag_tck(io_jtag_tck),
	.io_sys_spiXip_ss(io_spiXip_ss),
	.io_sys_spiXip_sclk(io_spiXip_sclk),
	.io_sys_spiXip_mosi(io_spiXip_mosi),
	.io_sys_spiXip_miso(io_spiXip_miso),
	.io_per_uartStd_txd(io_uartStd_txd),
	.io_per_uartStd_rxd(io_uartStd_rxd),
	.io_per_uartStd_cts(io_uartStd_cts),
	.io_per_uartStd_rts(io_uartStd_rts),
	.io_per_gpioStatus_pins_read(gpioStatus_pins_read),
	.io_per_gpioStatus_pins_write(gpioStatus_pins_write),
	.io_per_gpioStatus_pins_writeEnable(gpioStatus_pins_writeEnable)
);

endmodule
