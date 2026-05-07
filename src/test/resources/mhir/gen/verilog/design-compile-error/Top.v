module Top(
  // Missing input clock
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  wire  n1_clock; // @[Top.scala 32:20]
  wire  n1_reset; // @[Top.scala 32:20]
  wire  n1_valid_up; // @[Top.scala 32:20]
  wire  n1_valid_down; // @[Top.scala 32:20]
  wire [7:0] n1_I; // @[Top.scala 32:20]
  wire [7:0] n1_O; // @[Top.scala 32:20]
  wire  n7_clock; // @[Top.scala 35:20]
  wire  n7_reset; // @[Top.scala 35:20]
  wire  n7_valid_up; // @[Top.scala 35:20]
  wire  n7_valid_down; // @[Top.scala 35:20]
  wire [7:0] n7_I; // @[Top.scala 35:20]
  wire [7:0] n7_O; // @[Top.scala 35:20]
  wire  n8_clock; // @[Top.scala 38:20]
  wire  n8_reset; // @[Top.scala 38:20]
  wire  n8_valid_up; // @[Top.scala 38:20]
  wire  n8_valid_down; // @[Top.scala 38:20]
  wire [7:0] n8_I; // @[Top.scala 38:20]
  wire [7:0] n8_O; // @[Top.scala 38:20]
  wire  n9_clock; // @[Top.scala 41:20]
  wire  n9_reset; // @[Top.scala 41:20]
  wire  n9_valid_up; // @[Top.scala 41:20]
  wire  n9_valid_down; // @[Top.scala 41:20]
  wire [7:0] n9_I; // @[Top.scala 41:20]
  wire [7:0] n9_O; // @[Top.scala 41:20]
  wire  n10_clock; // @[Top.scala 44:21]
  wire  n10_reset; // @[Top.scala 44:21]
  wire  n10_valid_up; // @[Top.scala 44:21]
  wire  n10_valid_down; // @[Top.scala 44:21]
  wire [7:0] n10_I; // @[Top.scala 44:21]
  wire [7:0] n10_O; // @[Top.scala 44:21]
  FIFO n1 ( // @[Top.scala 32:20]
    .clock(n1_clock),
    .reset(n1_reset),
    .valid_up(n1_valid_up),
    .valid_down(n1_valid_down),
    .I(n1_I),
    .O(n1_O)
  );
  MapT n7 ( // @[Top.scala 35:20]
    .clock(n7_clock),
    .reset(n7_reset),
    .valid_up(n7_valid_up),
    .valid_down(n7_valid_down),
    .I(n7_I),
    .O(n7_O)
  );
  FIFO n8 ( // @[Top.scala 38:20]
    .clock(n8_clock),
    .reset(n8_reset),
    .valid_up(n8_valid_up),
    .valid_down(n8_valid_down),
    .I(n8_I),
    .O(n8_O)
  );
  FIFO n9 ( // @[Top.scala 41:20]
    .clock(n9_clock),
    .reset(n9_reset),
    .valid_up(n9_valid_up),
    .valid_down(n9_valid_down),
    .I(n9_I),
    .O(n9_O)
  );
  FIFO n10 ( // @[Top.scala 44:21]
    .clock(n10_clock),
    .reset(n10_reset),
    .valid_up(n10_valid_up),
    .valid_down(n10_valid_down),
    .I(n10_I),
    .O(n10_O)
  );
  assign valid_down = n10_valid_down; // @[Top.scala 48:16]
  assign O = n10_O; // @[Top.scala 47:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 34:17]
  assign n1_I = I; // @[Top.scala 33:10]
  assign n7_clock = clock;
  assign n7_reset = reset;
  assign n7_valid_up = n1_valid_down; // @[Top.scala 37:17]
  assign n7_I = n1_O; // @[Top.scala 36:10]
  assign n8_clock = clock;
  assign n8_reset = reset;
  assign n8_valid_up = n7_valid_down; // @[Top.scala 40:17]
  assign n8_I = n7_O; // @[Top.scala 39:10]
  assign n9_clock = clock;
  assign n9_reset = reset;
  assign n9_valid_up = n8_valid_down; // @[Top.scala 43:17]
  assign n9_I = n8_O; // @[Top.scala 42:10]
  assign n10_clock = clock;
  assign n10_reset = reset;
  assign n10_valid_up = n9_valid_down; // @[Top.scala 46:18]
  assign n10_I = n9_O; // @[Top.scala 45:11]
endmodule
