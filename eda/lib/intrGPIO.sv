// Interrupt Gpios

module intrGPIO #(parameter DATA_WIDTH = 1) (
	input  bit clk,
	output bit [DATA_WIDTH-1 :0] gpio0
);


initial
begin
		gpio0 = 0;      
		#500000;
		gpio0 = ~gpio0; 
		#500;
		gpio0 = ~gpio0;      
		#500000;
		gpio0 = ~gpio0; 
		#10000;
		gpio0 = ~gpio0;      
		#500000;
		gpio0 = ~gpio0; 
		#1000;
		gpio0 = ~gpio0;      
		#500000;
		gpio0 = ~gpio0; 
		#100;
		gpio0 = ~gpio0;      
end

endmodule
