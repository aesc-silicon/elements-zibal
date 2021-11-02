module IOBUF (
  output O,
  inout  IO,
  input  I,
  input  T
);

parameter integer DRIVE = 12;
parameter IBUF_LOW_PWR = "TRUE";
parameter IOSTANDARD = "DEFAULT";
parameter SLEW = "SLOW";

assign IO = (!T) ? I : 1'bz;
assign O = IO;

endmodule


module IBUF (
  output O,
  input  I
);

parameter CAPACITANCE = "DONT_CARE";
parameter IBUF_DELAY_VALUE = "0";
parameter IBUF_LOW_PWR = "TRUE";
parameter IFD_DELAY_VALUE = "AUTO";
parameter IOSTANDARD = "DEFAULT";

assign O = I;

endmodule


module OBUF (
  output O,
  input  I
);

parameter CAPACITANCE = "DONT_CARE";
parameter integer DRIVE = 12;
parameter IOSTANDARD = "DEFAULT";

assign O = I;

endmodule


module OBUFT (
  output O,
  input  I,
  input  T
);

parameter CAPACITANCE = "DONT_CARE";
parameter integer DRIVE = 12;
parameter IOSTANDARD = "DEFAULT";

assign O = (!T) ? I : 1'bz;

endmodule


module PULLUP (
  output O
);

assign O = 1'b1;

endmodule


module PULLDOWN (
  output O
);

assign O = 1'b1;

endmodule


module IBUFDS (
  output O,
  input  I,
  input  IB
);

parameter CAPACITANCE = "DONT_CARE";
parameter DIFF_TERM = "FALSE";
parameter DQS_BIAS = "FALSE";
parameter IBUF_DELAY_VALUE = "0";
parameter IBUF_LOW_PWR = "TRUE";
parameter IFD_DELAY_VALUE = "AUTO";
parameter IOSTANDARD = "DEFAULT";

assign O = I;

endmodule
