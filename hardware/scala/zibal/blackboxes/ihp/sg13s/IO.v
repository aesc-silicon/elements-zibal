module ixc013_b16m (
  input  DIN,
  input  OEN,
  output DOUT,
  inout  PAD
);

assign PAD = (!OEN) ? DIN : 1'bz;
assign DOUT = PAD;

endmodule


module ixc013_b16mpup (
  input  DIN,
  input  OEN,
  output DOUT,
  inout  PAD
);

assign PAD = (!OEN) ? DIN : 1'bz;
assign DOUT = PAD;

endmodule


module ixc013_i16x (
  input  PAD,
  output DOUT
);

assign DOUT = PAD;

endmodule

module INVJILTX2 (
  input  A,
  output Q
);

assign Q = !A;

endmodule

module INVJIX12 (
  input  A,
  output Q
);

assign Q = !A;

endmodule
