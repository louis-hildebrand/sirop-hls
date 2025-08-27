module Top(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  output [7:0] O_0
);
  assign valid_down = 1'h1; // @[Top.scala 22:16]
  assign O_0 = 8'h6; // @[Top.scala 21:7]
endmodule
