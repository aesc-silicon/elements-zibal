module reset (
	input  bit clk,
	output bit rst_n
);

parameter RST_DELAY = 10;

initial
begin
	rst_n <= 0;
	@(posedge clk);
//	@(negedge clk) rst_n = 1;
//	@(posedge clk) rst_n = 0;
	repeat (RST_DELAY) @(posedge clk);
	@(negedge clk) rst_n = 1;
end

endmodule
