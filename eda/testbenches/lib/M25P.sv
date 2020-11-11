module M25P (
	input  wire io_rst_n,
	input  wire io_spi_sclk,
	input  wire io_spi_mosi,
	output reg  io_spi_miso,
	input  wire io_spi_ss
);


reg [7:0] spi_cmd;

localparam SPI_WIDTH = 4;
localparam SPI_CMD = 4'b0001;
localparam SPI_ADR = 4'b0010;
localparam SPI_RSP = 4'b0100;
localparam SPI_STL = 4'b1000;
reg [SPI_WIDTH - 1:0] fsm_state;
reg [SPI_WIDTH - 1:0] fsm_nxt;

always @ (posedge io_spi_sclk, posedge io_spi_ss, io_rst_n)
begin
	if (io_spi_ss || io_rst_n)
		fsm_state <= SPI_CMD;
	else
		fsm_state <= fsm_nxt;
end

always @ (posedge io_spi_sclk)
begin
	if (!io_spi_ss && fsm_state == SPI_CMD) begin
		spi_cmd <= {spi_cmd[6:0], io_spi_mosi};
	end
end

reg [7:0] spi_rsp;
always @ (posedge io_spi_sclk, posedge io_spi_ss, io_rst_n)
begin
	if (io_spi_ss || io_rst_n) begin
		io_spi_miso <= 0;
		spi_rsp <= 8'h20;
	end else begin
		if (fsm_state == SPI_RSP) begin
			io_spi_miso <= spi_rsp[7];
			spi_rsp <= {spi_rsp[6:0], spi_rsp[7]};
		end
	end
end


reg [2:0] bit_counter;
wire bit_counter_tick;
always @ (posedge io_spi_sclk, posedge io_spi_ss, io_rst_n)
begin
	if (io_spi_ss || io_rst_n) begin
		bit_counter <= 0;
	end else begin
		if (bit_counter == 7)
			bit_counter <= 0;
		else
			bit_counter <= bit_counter + 1;
	end
end
assign bit_counter_tick = bit_counter == 7;



always @ (bit_counter_tick)
begin
	case(fsm_state)
	SPI_CMD: begin
		fsm_nxt <= (bit_counter_tick) ? SPI_RSP : SPI_CMD;
	end
	SPI_ADR: begin
		fsm_nxt <= SPI_ADR;
	end
	SPI_RSP: begin
		fsm_nxt <= SPI_RSP;
	end
	SPI_STL: begin
		fsm_nxt <= (spi_cmd == 8'h9E) ? SPI_RSP : SPI_ADR;
	end
	endcase
end


endmodule
