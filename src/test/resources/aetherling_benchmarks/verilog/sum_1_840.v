module FIFO(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  reg [7:0] _T; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_0;
  reg  _T_1; // @[FIFO.scala 15:27]
  reg [31:0] _RAND_1;
  assign valid_down = _T_1; // @[FIFO.scala 16:16]
  assign O = _T; // @[FIFO.scala 14:7]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  _T = _RAND_0[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_1 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    _T <= I;
    if (reset) begin
      _T_1 <= 1'h0;
    end else begin
      _T_1 <= valid_up;
    end
  end
endmodule
module NestedCounters(
  input   CE,
  output  valid
);
  assign valid = CE; // @[NestedCounters.scala 65:13]
endmodule
module NestedCountersWithNumValid(
  input   CE,
  output  valid
);
  wire  NestedCounters_CE; // @[NestedCounters.scala 20:44]
  wire  NestedCounters_valid; // @[NestedCounters.scala 20:44]
  NestedCounters NestedCounters ( // @[NestedCounters.scala 20:44]
    .CE(NestedCounters_CE),
    .valid(NestedCounters_valid)
  );
  assign valid = NestedCounters_valid; // @[NestedCounters.scala 22:9]
  assign NestedCounters_CE = CE; // @[NestedCounters.scala 21:27]
endmodule
module AddNoValid(
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  assign O = I_t0b + I_t1b; // @[Arithmetic.scala 122:7]
endmodule
module ReduceT(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  wire  NestedCountersWithNumValid_CE; // @[ReduceT.scala 22:34]
  wire  NestedCountersWithNumValid_valid; // @[ReduceT.scala 22:34]
  wire [7:0] AddNoValid_I_t0b; // @[ReduceT.scala 25:25]
  wire [7:0] AddNoValid_I_t1b; // @[ReduceT.scala 25:25]
  wire [7:0] AddNoValid_O; // @[ReduceT.scala 25:25]
  reg  _T; // @[ReduceT.scala 26:50]
  reg [31:0] _RAND_0;
  reg [9:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire  _T_3 = value == 10'h347; // @[Counter.scala 38:24]
  wire [9:0] _T_5 = value + 10'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value == 10'h0; // @[ReduceT.scala 34:60]
  reg [7:0] _T_7; // @[ReduceT.scala 34:76]
  reg [31:0] _RAND_2;
  reg [7:0] _T_9; // @[ReduceT.scala 35:24]
  reg [31:0] _RAND_3;
  reg  _T_10; // @[ReduceT.scala 37:35]
  reg [31:0] _RAND_4;
  reg [7:0] _T_11; // @[ReduceT.scala 44:83]
  reg [31:0] _RAND_5;
  reg  _T_12; // @[ReduceT.scala 51:28]
  reg [31:0] _RAND_6;
  wire  _T_14 = _T_12 | _T_3; // @[ReduceT.scala 52:28]
  reg [7:0] _T_15; // @[ReduceT.scala 56:15]
  reg [31:0] _RAND_7;
  NestedCountersWithNumValid NestedCountersWithNumValid ( // @[ReduceT.scala 22:34]
    .CE(NestedCountersWithNumValid_CE),
    .valid(NestedCountersWithNumValid_valid)
  );
  AddNoValid AddNoValid ( // @[ReduceT.scala 25:25]
    .I_t0b(AddNoValid_I_t0b),
    .I_t1b(AddNoValid_I_t1b),
    .O(AddNoValid_O)
  );
  assign valid_down = _T_12; // @[ReduceT.scala 53:16]
  assign O = _T_15; // @[ReduceT.scala 56:5]
  assign NestedCountersWithNumValid_CE = _T_10; // @[ReduceT.scala 37:25]
  assign AddNoValid_I_t0b = _T_11; // @[ReduceT.scala 44:55]
  assign AddNoValid_I_t1b = _T_9; // @[ReduceT.scala 45:55]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  _T = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  value = _RAND_1[9:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_7 = _RAND_2[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_9 = _RAND_3[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_10 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_11 = _RAND_5[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_12 = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_15 = _RAND_7[7:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      _T <= 1'h0;
    end else begin
      _T <= valid_up;
    end
    if (reset) begin
      value <= 10'h0;
    end else if (_T) begin
      if (_T_3) begin
        value <= 10'h0;
      end else begin
        value <= _T_5;
      end
    end
    _T_7 <= I;
    if (NestedCountersWithNumValid_valid) begin
      if (_T_6) begin
        _T_9 <= _T_7;
      end else begin
        _T_9 <= AddNoValid_O;
      end
    end
    _T_10 <= valid_up;
    _T_11 <= I;
    if (reset) begin
      _T_12 <= 1'h0;
    end else begin
      _T_12 <= _T_14;
    end
    _T_15 <= AddNoValid_O;
  end
endmodule
module Top(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  wire  n1_clock; // @[Top.scala 17:20]
  wire  n1_reset; // @[Top.scala 17:20]
  wire  n1_valid_up; // @[Top.scala 17:20]
  wire  n1_valid_down; // @[Top.scala 17:20]
  wire [7:0] n1_I; // @[Top.scala 17:20]
  wire [7:0] n1_O; // @[Top.scala 17:20]
  wire  n4_clock; // @[Top.scala 20:20]
  wire  n4_reset; // @[Top.scala 20:20]
  wire  n4_valid_up; // @[Top.scala 20:20]
  wire  n4_valid_down; // @[Top.scala 20:20]
  wire [7:0] n4_I; // @[Top.scala 20:20]
  wire [7:0] n4_O; // @[Top.scala 20:20]
  wire  n5_clock; // @[Top.scala 23:20]
  wire  n5_reset; // @[Top.scala 23:20]
  wire  n5_valid_up; // @[Top.scala 23:20]
  wire  n5_valid_down; // @[Top.scala 23:20]
  wire [7:0] n5_I; // @[Top.scala 23:20]
  wire [7:0] n5_O; // @[Top.scala 23:20]
  wire  n6_clock; // @[Top.scala 26:20]
  wire  n6_reset; // @[Top.scala 26:20]
  wire  n6_valid_up; // @[Top.scala 26:20]
  wire  n6_valid_down; // @[Top.scala 26:20]
  wire [7:0] n6_I; // @[Top.scala 26:20]
  wire [7:0] n6_O; // @[Top.scala 26:20]
  wire  n7_clock; // @[Top.scala 29:20]
  wire  n7_reset; // @[Top.scala 29:20]
  wire  n7_valid_up; // @[Top.scala 29:20]
  wire  n7_valid_down; // @[Top.scala 29:20]
  wire [7:0] n7_I; // @[Top.scala 29:20]
  wire [7:0] n7_O; // @[Top.scala 29:20]
  FIFO n1 ( // @[Top.scala 17:20]
    .clock(n1_clock),
    .reset(n1_reset),
    .valid_up(n1_valid_up),
    .valid_down(n1_valid_down),
    .I(n1_I),
    .O(n1_O)
  );
  ReduceT n4 ( // @[Top.scala 20:20]
    .clock(n4_clock),
    .reset(n4_reset),
    .valid_up(n4_valid_up),
    .valid_down(n4_valid_down),
    .I(n4_I),
    .O(n4_O)
  );
  FIFO n5 ( // @[Top.scala 23:20]
    .clock(n5_clock),
    .reset(n5_reset),
    .valid_up(n5_valid_up),
    .valid_down(n5_valid_down),
    .I(n5_I),
    .O(n5_O)
  );
  FIFO n6 ( // @[Top.scala 26:20]
    .clock(n6_clock),
    .reset(n6_reset),
    .valid_up(n6_valid_up),
    .valid_down(n6_valid_down),
    .I(n6_I),
    .O(n6_O)
  );
  FIFO n7 ( // @[Top.scala 29:20]
    .clock(n7_clock),
    .reset(n7_reset),
    .valid_up(n7_valid_up),
    .valid_down(n7_valid_down),
    .I(n7_I),
    .O(n7_O)
  );
  assign valid_down = n7_valid_down; // @[Top.scala 33:16]
  assign O = n7_O; // @[Top.scala 32:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 19:17]
  assign n1_I = I; // @[Top.scala 18:10]
  assign n4_clock = clock;
  assign n4_reset = reset;
  assign n4_valid_up = n1_valid_down; // @[Top.scala 22:17]
  assign n4_I = n1_O; // @[Top.scala 21:10]
  assign n5_clock = clock;
  assign n5_reset = reset;
  assign n5_valid_up = n4_valid_down; // @[Top.scala 25:17]
  assign n5_I = n4_O; // @[Top.scala 24:10]
  assign n6_clock = clock;
  assign n6_reset = reset;
  assign n6_valid_up = n5_valid_down; // @[Top.scala 28:17]
  assign n6_I = n5_O; // @[Top.scala 27:10]
  assign n7_clock = clock;
  assign n7_reset = reset;
  assign n7_valid_up = n6_valid_down; // @[Top.scala 31:17]
  assign n7_I = n6_O; // @[Top.scala 30:10]
endmodule
