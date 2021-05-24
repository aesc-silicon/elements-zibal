module MT25Q (
	input  wire io_rst_n,
	input  wire io_spi_sclk,
	input  wire io_spi_mosi,
	output reg  io_spi_miso,
	input  wire io_spi_ss
);


reg [7:0] spi_data;
reg [23:0] address;
reg [31:0] memory [0:1023];

initial begin
	$readmemb("kernel.rom", memory);
end

localparam SPI_WIDTH = 4;
localparam SPI_CMD = 4'b0001;
localparam SPI_ADR = 4'b0010;
localparam SPI_RSP = 4'b0100;
localparam SPI_STL = 4'b1000;
reg [SPI_WIDTH - 1:0] fsm_state;
reg [SPI_WIDTH - 1:0] fsm_nxt;

always @ (posedge io_spi_sclk, posedge io_spi_ss, io_rst_n)
begin
	if (io_spi_ss || !io_rst_n)
		fsm_state <= SPI_CMD;
	else
		fsm_state <= fsm_nxt;
end

always @ (posedge io_spi_sclk)
begin
	if (!io_spi_ss && fsm_state == SPI_CMD) begin
		spi_data <= {spi_data[6:0], io_spi_mosi};
	end
end

always @ (posedge io_spi_sclk)
begin
	if (!io_spi_ss && fsm_state == SPI_ADR) begin
		address <= {address[22:0], io_spi_mosi};
	end
end

wire adr_counter_tick;
reg  [31:0] spi_rsp;
wire [31:0] memory_ordered;

generate
	genvar i;
	for (i = 0; i < 8; i = i + 1) begin
		assign memory_ordered[i] = memory[address[19:1]][7 - i];
		assign memory_ordered[8 + i] = memory[address[19:1]][15 - i];
		assign memory_ordered[16 + i] = memory[address[19:1]][23 - i];
		assign memory_ordered[24 + i] = memory[address[19:1]][31 - i];
	end
endgenerate

always @ (negedge io_spi_sclk, posedge io_spi_ss, io_rst_n)
begin
	if (io_spi_ss || !io_rst_n) begin
		io_spi_miso <= 0;
		spi_rsp <= 32'hCAFECAFE;
	end else begin
		if (!io_spi_sclk) begin
			if (fsm_state != SPI_RSP) begin
				spi_rsp <= memory_ordered;
			end
			if (fsm_state == SPI_RSP) begin
				io_spi_miso <= spi_rsp[0];
				spi_rsp <= {spi_rsp[0], spi_rsp[31:1]};
			end
		end
	end
end

reg [2:0] cmd_counter;
wire cmd_counter_tick;
always @ (posedge io_spi_sclk, posedge io_spi_ss, io_rst_n)
begin
	if (io_spi_ss || !io_rst_n) begin
		cmd_counter <= 0;
	end else begin
		if (cmd_counter_tick || fsm_state != SPI_CMD)
			cmd_counter <= 0;
		else
			cmd_counter <= cmd_counter + 1;
	end
end
assign cmd_counter_tick = cmd_counter == 7;

reg [4:0] adr_counter;
always @ (posedge io_spi_sclk, posedge io_spi_ss, io_rst_n)
begin
	if (io_spi_ss || !io_rst_n) begin
		adr_counter <= 0;
	end else begin
		if (adr_counter_tick || fsm_state != SPI_ADR)
			adr_counter <= 0;
		else
			adr_counter <= adr_counter + 1;
	end
end
assign adr_counter_tick = adr_counter == 23;

reg [4:0] rsp_counter;
wire rsp_counter_tick;
always @ (posedge io_spi_sclk, posedge io_spi_ss, io_rst_n)
begin
	if (io_spi_ss || !io_rst_n) begin
		rsp_counter <= 0;
	end else begin
		if (rsp_counter_tick || fsm_state != SPI_RSP)
			rsp_counter <= 0;
		else
			rsp_counter <= rsp_counter + 1;
	end
end
assign rsp_counter_tick = rsp_counter == 31;


always @ (cmd_counter_tick, adr_counter_tick, rsp_counter_tick)
begin
	case(fsm_state)
	SPI_CMD: begin
		fsm_nxt <= (cmd_counter_tick) ? SPI_ADR : SPI_CMD;
	end
	SPI_ADR: begin
		fsm_nxt <= (adr_counter_tick) ? SPI_RSP : SPI_ADR;
	end
	SPI_RSP: begin
		fsm_nxt <= (rsp_counter_tick) ? SPI_STL : SPI_RSP;
	end
	SPI_STL: begin
		fsm_nxt <= SPI_CMD;
	end
	endcase
end


endmodule
