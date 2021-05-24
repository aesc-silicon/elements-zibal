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
	inout  [3:0] io_gpioStatus,
	inout  [6:0] io_gpio1,
	output [0:0] io_spiXip_ss,
	output io_spiXip_sclk,
	output io_spiXip_mosi,
	input  io_spiXip_miso,
	inout  io_i2c0_scl,
	inout  io_i2c0_sda
);

wire not_connected;

ixc013_i16x sys_clock_pad (
	.PAD(io_clock),
	.DOUT(sys_clock)
);

ixc013_i16x sys_reset_pad (
	.PAD(io_reset),
	.DOUT(sys_reset)
);

ixc013_b16m sys_sysReset_out_pad (
	.PAD(io_sysReset_out),
	.DOUT(not_connected),
	.OEN(1'b0),
	.DIN(sys_sysReset_out)
);

ixc013_b16m sys_jtag_tms_pad (
	.PAD(io_jtag_tms),
	.DOUT(sys_jtag_tms),
	.OEN(1'b1),
	.DIN(not_connected)
);

ixc013_b16m sys_jtag_tdi_pad (
	.PAD(io_jtag_tdi),
	.DOUT(sys_jtag_tdi),
	.OEN(1'b1),
	.DIN(not_connected)
);

ixc013_b16m sys_jtag_tdo_pad (
	.PAD(io_jtag_tdo),
	.DOUT(not_connected),
	.OEN(1'b0),
	.DIN(sys_jtag_tdo)
);
ixc013_b16m sys_jtag_tck_pad (
	.PAD(io_jtag_tck),
	.DOUT(sys_jtag_tck),
	.OEN(1'b1),
	.DIN(not_connected)
);

ixc013_b16m per_uartStd_txd_pad (
	.PAD(io_uartStd_txd),
	.DOUT(not_connected),
	.OEN(1'b0),
	.DIN(per_uartStd_txd)
);

ixc013_b16m per_uartStd_rxd_pad (
	.PAD(io_uartStd_rxd),
	.DOUT(per_uartStd_rxd),
	.OEN(1'b1),
	.DIN(not_connected)
);
ixc013_b16m per_uartStd_cts_pad (
	.PAD(io_uartStd_cts),
	.DOUT(per_uartStd_cts),
	.OEN(1'b1),
	.DIN(not_connected)
);

ixc013_b16m per_uartStd_rts_pad (
	.PAD(io_uartStd_rts),
	.DOUT(not_connected),
	.OEN(1'b0),
	.DIN(per_uartStd_rts)
);

wire [3:0] per_gpioStatus_pins_read;
wire [3:0] per_gpioStatus_pins_writeEnable;
wire [3:0] per_gpioStatus_pins_write;

ixc013_b16m per_gpioStatus0_pad (
	.PAD(io_gpioStatus[0]),
	.DOUT(per_gpioStatus_pins_read[0]),
	.OEN(per_gpioStatus_pins_writeEnable[0]),
	.DIN(per_gpioStatus_pins_write[0])
);

ixc013_b16m per_gpioStatus1_pad (
	.PAD(io_gpioStatus[1]),
	.DOUT(per_gpioStatus_pins_read[1]),
	.OEN(per_gpioStatus_pins_writeEnable[1]),
	.DIN(per_gpioStatus_pins_write[1])
);

ixc013_b16m per_gpioStatus2_pad (
	.PAD(io_gpioStatus[2]),
	.DOUT(per_gpioStatus_pins_read[2]),
	.OEN(per_gpioStatus_pins_writeEnable[2]),
	.DIN(per_gpioStatus_pins_write[2])
);

ixc013_b16m per_gpioStatus3_pad (
	.PAD(io_gpioStatus[3]),
	.DOUT(per_gpioStatus_pins_read[3]),
	.OEN(per_gpioStatus_pins_writeEnable[3]),
	.DIN(per_gpioStatus_pins_write[3])
);

ixc013_b16m per_spiXip_ss_pad (
	.PAD(io_spiXip_ss),
	.DOUT(not_connected),
	.OEN(1'b0),
	.DIN(per_spiXip_ss)
);

ixc013_b16m per_spiXip_sclk_pad (
	.PAD(io_spiXip_sclk),
	.DOUT(not_connected),
	.OEN(1'b0),
	.DIN(per_spiXip_sclk)
);

ixc013_b16m per_spiXip_mosi_pad (
	.PAD(io_spiXip_mosi),
	.DOUT(not_connected),
	.OEN(1'b0),
	.DIN(per_spiXip_mosi)
);

ixc013_b16m per_spiXip_miso_pad (
	.PAD(io_spiXip_miso),
	.DOUT(per_spiXip_miso),
	.OEN(1'b1),
	.DIN(not_connected)
);

ixc013_b16mpup per_i2c0_scl_pad (
	.PAD(io_i2c0_scl),
	.DOUT(per_i2c0_scl_read),
	.OEN(per_i2c0_scl_write),
	.DIN(1'b0)
);

ixc013_b16mpup per_i2c0_sda_pad (
	.PAD(io_i2c0_sda),
	.DOUT(per_i2c0_sda_read),
	.OEN(per_i2c0_sda_write),
	.DIN(1'b0)
);

Carbon1 SOC (
	.io_sys_clock(sys_clock),
	.io_sys_reset(sys_reset),
	.io_sys_sysReset_out(sys_sysReset_out),
	.io_sys_jtag_tms(sys_jtag_tms),
	.io_sys_jtag_tdi(sys_jtag_tdi),
	.io_sys_jtag_tdo(sys_jtag_tdo),
	.io_sys_jtag_tck(sys_jtag_tck),
	.io_sys_spiXip_ss(per_spiXip_ss),
	.io_sys_spiXip_sclk(per_spiXip_sclk),
	.io_sys_spiXip_mosi(per_spiXip_mosi),
	.io_sys_spiXip_miso(per_spiXip_miso),
	.io_per_uartStd_txd(per_uartStd_txd),
	.io_per_uartStd_rxd(per_uartStd_rxd),
	.io_per_uartStd_cts(per_uartStd_cts),
	.io_per_uartStd_rts(per_uartStd_rts),
	.io_per_gpioStatus_pins_read(per_gpioStatus_pins_read),
	.io_per_gpioStatus_pins_write(per_gpioStatus_pins_write),
	.io_per_gpioStatus_pins_writeEnable(per_gpioStatus_pins_writeEnable),
	.io_per_i2c0_scl_write(per_i2c0_scl_write),
	.io_per_i2c0_scl_read(per_i2c0_scl_read),
	.io_per_i2c0_sda_write(per_i2c0_sda_write),
	.io_per_i2c0_sda_read(per_i2c0_sda_read)
);

endmodule
