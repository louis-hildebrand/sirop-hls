module FIFO(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I,
  output [15:0] O
);
  reg [15:0] _T; // @[FIFO.scala 13:26]
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
  _T = _RAND_0[15:0];
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
module AtomTuple(
  input         valid_up,
  output        valid_down,
  input  [15:0] I0,
  input  [15:0] I1,
  output [15:0] O_t0b,
  output [15:0] O_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b = I0; // @[Tuple.scala 49:9]
  assign O_t1b = I1; // @[Tuple.scala 50:9]
endmodule
module Mul(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b,
  output [15:0] O
);
  wire [15:0] BlackBoxMulUInt16_I0; // @[Arithmetic.scala 180:27]
  wire [15:0] BlackBoxMulUInt16_I1; // @[Arithmetic.scala 180:27]
  wire [31:0] BlackBoxMulUInt16_O; // @[Arithmetic.scala 180:27]
  wire  BlackBoxMulUInt16_clock; // @[Arithmetic.scala 180:27]
  reg  _T_1; // @[Arithmetic.scala 217:42]
  reg [31:0] _RAND_0;
  reg  _T_2; // @[Arithmetic.scala 217:34]
  reg [31:0] _RAND_1;
  reg  _T_3; // @[Arithmetic.scala 217:26]
  reg [31:0] _RAND_2;
  BlackBoxMulUInt16 BlackBoxMulUInt16 ( // @[Arithmetic.scala 180:27]
    .I0(BlackBoxMulUInt16_I0),
    .I1(BlackBoxMulUInt16_I1),
    .O(BlackBoxMulUInt16_O),
    .clock(BlackBoxMulUInt16_clock)
  );
  assign valid_down = _T_3; // @[Arithmetic.scala 217:16]
  assign O = BlackBoxMulUInt16_O[15:0]; // @[Arithmetic.scala 183:7]
  assign BlackBoxMulUInt16_I0 = I_t0b; // @[Arithmetic.scala 181:21]
  assign BlackBoxMulUInt16_I1 = I_t1b; // @[Arithmetic.scala 182:21]
  assign BlackBoxMulUInt16_clock = clock; // @[Arithmetic.scala 184:24]
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
  _T_1 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_2 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_3 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      _T_1 <= 1'h0;
    end else begin
      _T_1 <= valid_up;
    end
    if (reset) begin
      _T_2 <= 1'h0;
    end else begin
      _T_2 <= _T_1;
    end
    if (reset) begin
      _T_3 <= 1'h0;
    end else begin
      _T_3 <= _T_2;
    end
  end
endmodule
module Module_0(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I0,
  input  [15:0] I1,
  output [15:0] O
);
  wire  n7_valid_up; // @[Top.scala 18:20]
  wire  n7_valid_down; // @[Top.scala 18:20]
  wire [15:0] n7_I0; // @[Top.scala 18:20]
  wire [15:0] n7_I1; // @[Top.scala 18:20]
  wire [15:0] n7_O_t0b; // @[Top.scala 18:20]
  wire [15:0] n7_O_t1b; // @[Top.scala 18:20]
  wire  n8_clock; // @[Top.scala 22:20]
  wire  n8_reset; // @[Top.scala 22:20]
  wire  n8_valid_up; // @[Top.scala 22:20]
  wire  n8_valid_down; // @[Top.scala 22:20]
  wire [15:0] n8_I_t0b; // @[Top.scala 22:20]
  wire [15:0] n8_I_t1b; // @[Top.scala 22:20]
  wire [15:0] n8_O; // @[Top.scala 22:20]
  AtomTuple n7 ( // @[Top.scala 18:20]
    .valid_up(n7_valid_up),
    .valid_down(n7_valid_down),
    .I0(n7_I0),
    .I1(n7_I1),
    .O_t0b(n7_O_t0b),
    .O_t1b(n7_O_t1b)
  );
  Mul n8 ( // @[Top.scala 22:20]
    .clock(n8_clock),
    .reset(n8_reset),
    .valid_up(n8_valid_up),
    .valid_down(n8_valid_down),
    .I_t0b(n8_I_t0b),
    .I_t1b(n8_I_t1b),
    .O(n8_O)
  );
  assign valid_down = n8_valid_down; // @[Top.scala 26:16]
  assign O = n8_O; // @[Top.scala 25:7]
  assign n7_valid_up = valid_up; // @[Top.scala 21:17]
  assign n7_I0 = I0; // @[Top.scala 19:11]
  assign n7_I1 = I1; // @[Top.scala 20:11]
  assign n8_clock = clock;
  assign n8_reset = reset;
  assign n8_valid_up = n7_valid_down; // @[Top.scala 24:17]
  assign n8_I_t0b = n7_O_t0b; // @[Top.scala 23:10]
  assign n8_I_t1b = n7_O_t1b; // @[Top.scala 23:10]
endmodule
module Map2T(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I0,
  input  [15:0] I1,
  output [15:0] O
);
  wire  op_clock; // @[Map2T.scala 8:20]
  wire  op_reset; // @[Map2T.scala 8:20]
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [15:0] op_I0; // @[Map2T.scala 8:20]
  wire [15:0] op_I1; // @[Map2T.scala 8:20]
  wire [15:0] op_O; // @[Map2T.scala 8:20]
  Module_0 op ( // @[Map2T.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0(op_I0),
    .I1(op_I1),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O = op_O; // @[Map2T.scala 17:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0 = I0; // @[Map2T.scala 15:11]
  assign op_I1 = I1; // @[Map2T.scala 16:11]
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
  input  [15:0] I_t0b,
  input  [15:0] I_t1b,
  output [15:0] O
);
  assign O = I_t0b + I_t1b; // @[Arithmetic.scala 122:7]
endmodule
module ReduceT(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I,
  output [15:0] O
);
  wire  NestedCountersWithNumValid_CE; // @[ReduceT.scala 22:34]
  wire  NestedCountersWithNumValid_valid; // @[ReduceT.scala 22:34]
  wire [15:0] AddNoValid_I_t0b; // @[ReduceT.scala 25:25]
  wire [15:0] AddNoValid_I_t1b; // @[ReduceT.scala 25:25]
  wire [15:0] AddNoValid_O; // @[ReduceT.scala 25:25]
  reg  _T; // @[ReduceT.scala 26:50]
  reg [31:0] _RAND_0;
  reg [9:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire  _T_3 = value == 10'h347; // @[Counter.scala 38:24]
  wire [9:0] _T_5 = value + 10'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value == 10'h0; // @[ReduceT.scala 34:60]
  reg [15:0] _T_7; // @[ReduceT.scala 34:76]
  reg [31:0] _RAND_2;
  reg [15:0] _T_9; // @[ReduceT.scala 35:24]
  reg [31:0] _RAND_3;
  reg  _T_10; // @[ReduceT.scala 37:35]
  reg [31:0] _RAND_4;
  reg [15:0] _T_11; // @[ReduceT.scala 44:83]
  reg [31:0] _RAND_5;
  reg  _T_12; // @[ReduceT.scala 51:28]
  reg [31:0] _RAND_6;
  wire  _T_14 = _T_12 | _T_3; // @[ReduceT.scala 52:28]
  reg [15:0] _T_15; // @[ReduceT.scala 56:15]
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
  _T_7 = _RAND_2[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_9 = _RAND_3[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_10 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_11 = _RAND_5[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_12 = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_15 = _RAND_7[15:0];
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
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I0,
  input  [15:0] I1,
  output [15:0] O
);
  wire  n1_clock; // @[Top.scala 33:20]
  wire  n1_reset; // @[Top.scala 33:20]
  wire  n1_valid_up; // @[Top.scala 33:20]
  wire  n1_valid_down; // @[Top.scala 33:20]
  wire [15:0] n1_I; // @[Top.scala 33:20]
  wire [15:0] n1_O; // @[Top.scala 33:20]
  wire  n3_clock; // @[Top.scala 36:20]
  wire  n3_reset; // @[Top.scala 36:20]
  wire  n3_valid_up; // @[Top.scala 36:20]
  wire  n3_valid_down; // @[Top.scala 36:20]
  wire [15:0] n3_I; // @[Top.scala 36:20]
  wire [15:0] n3_O; // @[Top.scala 36:20]
  wire  n4_clock; // @[Top.scala 39:20]
  wire  n4_reset; // @[Top.scala 39:20]
  wire  n4_valid_up; // @[Top.scala 39:20]
  wire  n4_valid_down; // @[Top.scala 39:20]
  wire [15:0] n4_I0; // @[Top.scala 39:20]
  wire [15:0] n4_I1; // @[Top.scala 39:20]
  wire [15:0] n4_O; // @[Top.scala 39:20]
  wire  n11_clock; // @[Top.scala 43:21]
  wire  n11_reset; // @[Top.scala 43:21]
  wire  n11_valid_up; // @[Top.scala 43:21]
  wire  n11_valid_down; // @[Top.scala 43:21]
  wire [15:0] n11_I; // @[Top.scala 43:21]
  wire [15:0] n11_O; // @[Top.scala 43:21]
  wire  n12_clock; // @[Top.scala 46:21]
  wire  n12_reset; // @[Top.scala 46:21]
  wire  n12_valid_up; // @[Top.scala 46:21]
  wire  n12_valid_down; // @[Top.scala 46:21]
  wire [15:0] n12_I; // @[Top.scala 46:21]
  wire [15:0] n12_O; // @[Top.scala 46:21]
  wire  n13_clock; // @[Top.scala 49:21]
  wire  n13_reset; // @[Top.scala 49:21]
  wire  n13_valid_up; // @[Top.scala 49:21]
  wire  n13_valid_down; // @[Top.scala 49:21]
  wire [15:0] n13_I; // @[Top.scala 49:21]
  wire [15:0] n13_O; // @[Top.scala 49:21]
  wire  n14_clock; // @[Top.scala 52:21]
  wire  n14_reset; // @[Top.scala 52:21]
  wire  n14_valid_up; // @[Top.scala 52:21]
  wire  n14_valid_down; // @[Top.scala 52:21]
  wire [15:0] n14_I; // @[Top.scala 52:21]
  wire [15:0] n14_O; // @[Top.scala 52:21]
  FIFO n1 ( // @[Top.scala 33:20]
    .clock(n1_clock),
    .reset(n1_reset),
    .valid_up(n1_valid_up),
    .valid_down(n1_valid_down),
    .I(n1_I),
    .O(n1_O)
  );
  FIFO n3 ( // @[Top.scala 36:20]
    .clock(n3_clock),
    .reset(n3_reset),
    .valid_up(n3_valid_up),
    .valid_down(n3_valid_down),
    .I(n3_I),
    .O(n3_O)
  );
  Map2T n4 ( // @[Top.scala 39:20]
    .clock(n4_clock),
    .reset(n4_reset),
    .valid_up(n4_valid_up),
    .valid_down(n4_valid_down),
    .I0(n4_I0),
    .I1(n4_I1),
    .O(n4_O)
  );
  ReduceT n11 ( // @[Top.scala 43:21]
    .clock(n11_clock),
    .reset(n11_reset),
    .valid_up(n11_valid_up),
    .valid_down(n11_valid_down),
    .I(n11_I),
    .O(n11_O)
  );
  FIFO n12 ( // @[Top.scala 46:21]
    .clock(n12_clock),
    .reset(n12_reset),
    .valid_up(n12_valid_up),
    .valid_down(n12_valid_down),
    .I(n12_I),
    .O(n12_O)
  );
  FIFO n13 ( // @[Top.scala 49:21]
    .clock(n13_clock),
    .reset(n13_reset),
    .valid_up(n13_valid_up),
    .valid_down(n13_valid_down),
    .I(n13_I),
    .O(n13_O)
  );
  FIFO n14 ( // @[Top.scala 52:21]
    .clock(n14_clock),
    .reset(n14_reset),
    .valid_up(n14_valid_up),
    .valid_down(n14_valid_down),
    .I(n14_I),
    .O(n14_O)
  );
  assign valid_down = n14_valid_down; // @[Top.scala 56:16]
  assign O = n14_O; // @[Top.scala 55:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 35:17]
  assign n1_I = I0; // @[Top.scala 34:10]
  assign n3_clock = clock;
  assign n3_reset = reset;
  assign n3_valid_up = valid_up; // @[Top.scala 38:17]
  assign n3_I = I1; // @[Top.scala 37:10]
  assign n4_clock = clock;
  assign n4_reset = reset;
  assign n4_valid_up = n1_valid_down & n3_valid_down; // @[Top.scala 42:17]
  assign n4_I0 = n1_O; // @[Top.scala 40:11]
  assign n4_I1 = n3_O; // @[Top.scala 41:11]
  assign n11_clock = clock;
  assign n11_reset = reset;
  assign n11_valid_up = n4_valid_down; // @[Top.scala 45:18]
  assign n11_I = n4_O; // @[Top.scala 44:11]
  assign n12_clock = clock;
  assign n12_reset = reset;
  assign n12_valid_up = n11_valid_down; // @[Top.scala 48:18]
  assign n12_I = n11_O; // @[Top.scala 47:11]
  assign n13_clock = clock;
  assign n13_reset = reset;
  assign n13_valid_up = n12_valid_down; // @[Top.scala 51:18]
  assign n13_I = n12_O; // @[Top.scala 50:11]
  assign n14_clock = clock;
  assign n14_reset = reset;
  assign n14_valid_up = n13_valid_down; // @[Top.scala 54:18]
  assign n14_I = n13_O; // @[Top.scala 53:11]
endmodule
