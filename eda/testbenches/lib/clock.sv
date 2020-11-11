module clock (
	output bit clk
);

parameter CLK_PERIOD = 10;

initial clk = 0;

always #(CLK_PERIOD / 2) clk = ~clk;

endmodule
