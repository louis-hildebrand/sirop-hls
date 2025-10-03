module FIFO(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  input  [15:0] I_1,
  input  [15:0] I_2,
  input  [15:0] I_3,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3
);
  reg [15:0] _T__0; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_0;
  reg [15:0] _T__1; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_1;
  reg [15:0] _T__2; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_2;
  reg [15:0] _T__3; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_3;
  reg  _T_1; // @[FIFO.scala 15:27]
  reg [31:0] _RAND_4;
  assign valid_down = _T_1; // @[FIFO.scala 16:16]
  assign O_0 = _T__0; // @[FIFO.scala 14:7]
  assign O_1 = _T__1; // @[FIFO.scala 14:7]
  assign O_2 = _T__2; // @[FIFO.scala 14:7]
  assign O_3 = _T__3; // @[FIFO.scala 14:7]
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
  _T__0 = _RAND_0[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T__1 = _RAND_1[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T__2 = _RAND_2[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T__3 = _RAND_3[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_1 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    _T__0 <= I_0;
    _T__1 <= I_1;
    _T__2 <= I_2;
    _T__3 <= I_3;
    if (reset) begin
      _T_1 <= 1'h0;
    end else begin
      _T_1 <= valid_up;
    end
  end
endmodule
module InitialDelayCounter(
  input   clock,
  input   reset,
  output  valid_down
);
  reg  value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 1'h1; // @[InitialDelayCounter.scala 17:17]
  wire  _T_4 = value + 1'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 1'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module AtomTuple(
  input         valid_up,
  output        valid_down,
  input  [15:0] I0,
  output [15:0] O_t0b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b = I0; // @[Tuple.scala 49:9]
endmodule
module RShift(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  output [15:0] O
);
  assign valid_down = valid_up; // @[Arithmetic.scala 405:14]
  assign O = {{1'd0}, I_t0b[15:1]}; // @[Arithmetic.scala 403:7]
endmodule
module AtomTuple_1(
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
module Add(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b,
  output [15:0] O
);
  assign valid_down = valid_up; // @[Arithmetic.scala 111:14]
  assign O = I_t0b + I_t1b; // @[Arithmetic.scala 109:7]
endmodule
module AtomTuple_3(
  input         valid_up,
  output        valid_down,
  input  [15:0] I0,
  input  [15:0] I1_t0b,
  input  [15:0] I1_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b = I0; // @[Tuple.scala 49:9]
  assign O_t1b_t0b = I1_t0b; // @[Tuple.scala 50:9]
  assign O_t1b_t1b = I1_t1b; // @[Tuple.scala 50:9]
endmodule
module Module_0(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n7_valid_up; // @[Top.scala 19:20]
  wire  n7_valid_down; // @[Top.scala 19:20]
  wire [15:0] n7_I0; // @[Top.scala 19:20]
  wire [15:0] n7_O_t0b; // @[Top.scala 19:20]
  wire  n8_valid_up; // @[Top.scala 23:20]
  wire  n8_valid_down; // @[Top.scala 23:20]
  wire [15:0] n8_I_t0b; // @[Top.scala 23:20]
  wire [15:0] n8_O; // @[Top.scala 23:20]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n11_valid_up; // @[Top.scala 27:21]
  wire  n11_valid_down; // @[Top.scala 27:21]
  wire [15:0] n11_I0; // @[Top.scala 27:21]
  wire [15:0] n11_I1; // @[Top.scala 27:21]
  wire [15:0] n11_O_t0b; // @[Top.scala 27:21]
  wire [15:0] n11_O_t1b; // @[Top.scala 27:21]
  wire  n12_valid_up; // @[Top.scala 31:21]
  wire  n12_valid_down; // @[Top.scala 31:21]
  wire [15:0] n12_I_t0b; // @[Top.scala 31:21]
  wire [15:0] n12_I_t1b; // @[Top.scala 31:21]
  wire [15:0] n12_O; // @[Top.scala 31:21]
  wire  n14_valid_up; // @[Top.scala 34:21]
  wire  n14_valid_down; // @[Top.scala 34:21]
  wire [15:0] n14_I0; // @[Top.scala 34:21]
  wire [15:0] n14_I1; // @[Top.scala 34:21]
  wire [15:0] n14_O_t0b; // @[Top.scala 34:21]
  wire [15:0] n14_O_t1b; // @[Top.scala 34:21]
  wire  n15_valid_up; // @[Top.scala 38:21]
  wire  n15_valid_down; // @[Top.scala 38:21]
  wire [15:0] n15_I0; // @[Top.scala 38:21]
  wire [15:0] n15_I1_t0b; // @[Top.scala 38:21]
  wire [15:0] n15_I1_t1b; // @[Top.scala 38:21]
  wire [15:0] n15_O_t0b; // @[Top.scala 38:21]
  wire [15:0] n15_O_t1b_t0b; // @[Top.scala 38:21]
  wire [15:0] n15_O_t1b_t1b; // @[Top.scala 38:21]
  InitialDelayCounter InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  InitialDelayCounter InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple n7 ( // @[Top.scala 19:20]
    .valid_up(n7_valid_up),
    .valid_down(n7_valid_down),
    .I0(n7_I0),
    .O_t0b(n7_O_t0b)
  );
  RShift n8 ( // @[Top.scala 23:20]
    .valid_up(n8_valid_up),
    .valid_down(n8_valid_down),
    .I_t0b(n8_I_t0b),
    .O(n8_O)
  );
  InitialDelayCounter InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n11 ( // @[Top.scala 27:21]
    .valid_up(n11_valid_up),
    .valid_down(n11_valid_down),
    .I0(n11_I0),
    .I1(n11_I1),
    .O_t0b(n11_O_t0b),
    .O_t1b(n11_O_t1b)
  );
  Add n12 ( // @[Top.scala 31:21]
    .valid_up(n12_valid_up),
    .valid_down(n12_valid_down),
    .I_t0b(n12_I_t0b),
    .I_t1b(n12_I_t1b),
    .O(n12_O)
  );
  AtomTuple_1 n14 ( // @[Top.scala 34:21]
    .valid_up(n14_valid_up),
    .valid_down(n14_valid_down),
    .I0(n14_I0),
    .I1(n14_I1),
    .O_t0b(n14_O_t0b),
    .O_t1b(n14_O_t1b)
  );
  AtomTuple_3 n15 ( // @[Top.scala 38:21]
    .valid_up(n15_valid_up),
    .valid_down(n15_valid_down),
    .I0(n15_I0),
    .I1_t0b(n15_I1_t0b),
    .I1_t1b(n15_I1_t1b),
    .O_t0b(n15_O_t0b),
    .O_t1b_t0b(n15_O_t1b_t0b),
    .O_t1b_t1b(n15_O_t1b_t1b)
  );
  assign valid_down = n15_valid_down; // @[Top.scala 43:16]
  assign O_t0b = n15_O_t0b; // @[Top.scala 42:7]
  assign O_t1b_t0b = n15_O_t1b_t0b; // @[Top.scala 42:7]
  assign O_t1b_t1b = n15_O_t1b_t1b; // @[Top.scala 42:7]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n7_valid_up = valid_up & InitialDelayCounter_1_valid_down; // @[Top.scala 22:17]
  assign n7_I0 = I; // @[Top.scala 20:11]
  assign n8_valid_up = n7_valid_down; // @[Top.scala 25:17]
  assign n8_I_t0b = n7_O_t0b; // @[Top.scala 24:10]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n11_valid_up = n8_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 30:18]
  assign n11_I0 = n8_O; // @[Top.scala 28:12]
  assign n11_I1 = 16'h1; // @[Top.scala 29:12]
  assign n12_valid_up = n11_valid_down; // @[Top.scala 33:18]
  assign n12_I_t0b = n11_O_t0b; // @[Top.scala 32:11]
  assign n12_I_t1b = n11_O_t1b; // @[Top.scala 32:11]
  assign n14_valid_up = InitialDelayCounter_valid_down & n12_valid_down; // @[Top.scala 37:18]
  assign n14_I0 = 16'h0; // @[Top.scala 35:12]
  assign n14_I1 = n12_O; // @[Top.scala 36:12]
  assign n15_valid_up = valid_up & n14_valid_down; // @[Top.scala 41:18]
  assign n15_I0 = I; // @[Top.scala 39:12]
  assign n15_I1_t0b = n14_O_t0b; // @[Top.scala 40:12]
  assign n15_I1_t1b = n14_O_t1b; // @[Top.scala 40:12]
endmodule
module MapS(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  input  [15:0] I_1,
  input  [15:0] I_2,
  input  [15:0] I_3,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_0 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I(fst_op_I),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_0 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I(other_ops_0_I),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_0 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I(other_ops_1_I),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_0 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I(other_ops_2_I),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I = I_0; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I = I_1; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I = I_2; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I = I_3; // @[MapS.scala 20:41]
endmodule
module MapT(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  input  [15:0] I_1,
  input  [15:0] I_2,
  input  [15:0] I_3,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0; // @[MapT.scala 8:20]
  wire [15:0] op_I_1; // @[MapT.scala 8:20]
  wire [15:0] op_I_2; // @[MapT.scala 8:20]
  wire [15:0] op_I_3; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0(op_I_0),
    .I_1(op_I_1),
    .I_2(op_I_2),
    .I_3(op_I_3),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0 = I_0; // @[MapT.scala 14:10]
  assign op_I_1 = I_1; // @[MapT.scala 14:10]
  assign op_I_2 = I_2; // @[MapT.scala 14:10]
  assign op_I_3 = I_3; // @[MapT.scala 14:10]
endmodule
module Fst(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  output [15:0] O
);
  assign valid_down = valid_up; // @[Tuple.scala 59:14]
  assign O = I_t0b; // @[Tuple.scala 58:5]
endmodule
module FIFO_1(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I,
  output [15:0] O
);
  reg [15:0] _T [0:3]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_0;
  wire [15:0] _T__T_15_data; // @[FIFO.scala 23:33]
  wire [1:0] _T__T_15_addr; // @[FIFO.scala 23:33]
  wire [15:0] _T__T_5_data; // @[FIFO.scala 23:33]
  wire [1:0] _T__T_5_addr; // @[FIFO.scala 23:33]
  wire  _T__T_5_mask; // @[FIFO.scala 23:33]
  wire  _T__T_5_en; // @[FIFO.scala 23:33]
  reg  _T__T_15_en_pipe_0;
  reg [31:0] _RAND_1;
  reg [1:0] _T__T_15_addr_pipe_0;
  reg [31:0] _RAND_2;
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_3;
  reg [1:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_4;
  reg [1:0] value_2; // @[Counter.scala 29:33]
  reg [31:0] _RAND_5;
  wire [1:0] _T_4 = value_2 + 2'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value < 2'h3; // @[FIFO.scala 38:39]
  wire [1:0] _T_9 = value + 2'h1; // @[Counter.scala 39:22]
  wire  _T_10 = value >= 2'h2; // @[FIFO.scala 42:39]
  wire [1:0] _T_18 = value_1 + 2'h1; // @[Counter.scala 39:22]
  wire  _GEN_5 = _T_10 & _T_10; // @[FIFO.scala 42:57]
  assign _T__T_15_addr = _T__T_15_addr_pipe_0;
  assign _T__T_15_data = _T[_T__T_15_addr]; // @[FIFO.scala 23:33]
  assign _T__T_5_data = I;
  assign _T__T_5_addr = value_2;
  assign _T__T_5_mask = 1'h1;
  assign _T__T_5_en = valid_up;
  assign valid_down = value == 2'h3; // @[FIFO.scala 33:16]
  assign O = _T__T_15_data; // @[FIFO.scala 43:11]
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
  _RAND_0 = {1{`RANDOM}};
  `ifdef RANDOMIZE_MEM_INIT
  for (initvar = 0; initvar < 4; initvar = initvar+1)
    _T[initvar] = _RAND_0[15:0];
  `endif // RANDOMIZE_MEM_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T__T_15_en_pipe_0 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T__T_15_addr_pipe_0 = _RAND_2[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  value = _RAND_3[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  value_1 = _RAND_4[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  value_2 = _RAND_5[1:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if(_T__T_5_en & _T__T_5_mask) begin
      _T[_T__T_5_addr] <= _T__T_5_data; // @[FIFO.scala 23:33]
    end
    _T__T_15_en_pipe_0 <= valid_up & _GEN_5;
    if (valid_up & _GEN_5) begin
      _T__T_15_addr_pipe_0 <= value_1;
    end
    if (reset) begin
      value <= 2'h0;
    end else if (valid_up) begin
      if (_T_6) begin
        value <= _T_9;
      end
    end
    if (reset) begin
      value_1 <= 2'h0;
    end else if (valid_up) begin
      if (_T_10) begin
        value_1 <= _T_18;
      end
    end
    if (reset) begin
      value_2 <= 2'h0;
    end else if (valid_up) begin
      value_2 <= _T_4;
    end
  end
endmodule
module Snd(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 67:14]
  assign O_t0b = I_t1b_t0b; // @[Tuple.scala 66:5]
  assign O_t1b = I_t1b_t1b; // @[Tuple.scala 66:5]
endmodule
module Fst_1(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  output [15:0] O
);
  assign valid_down = valid_up; // @[Tuple.scala 59:14]
  assign O = I_t0b; // @[Tuple.scala 58:5]
endmodule
module Snd_1(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t1b,
  output [15:0] O
);
  assign valid_down = valid_up; // @[Tuple.scala 67:14]
  assign O = I_t1b; // @[Tuple.scala 66:5]
endmodule
module Eq(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b,
  output [15:0] O
);
  wire  _T = I_t0b == I_t1b; // @[Arithmetic.scala 497:25]
  assign valid_down = valid_up; // @[Arithmetic.scala 499:14]
  assign O = {{15'd0}, _T}; // @[Arithmetic.scala 497:7]
endmodule
module AtomTuple_9(
  input         valid_up,
  output        valid_down,
  input         I0,
  input  [15:0] I1_t0b,
  input  [15:0] I1_t1b,
  output        O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b = I0; // @[Tuple.scala 49:9]
  assign O_t1b_t0b = I1_t0b; // @[Tuple.scala 50:9]
  assign O_t1b_t1b = I1_t1b; // @[Tuple.scala 50:9]
endmodule
module If(
  input         valid_up,
  output        valid_down,
  input         I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O
);
  assign valid_down = valid_up; // @[Arithmetic.scala 528:14]
  assign O = I_t0b ? I_t1b_t0b : I_t1b_t1b; // @[Arithmetic.scala 526:9 Arithmetic.scala 527:20]
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
  wire [15:0] BlackBoxMulUInt16_I0; // @[Arithmetic.scala 183:27]
  wire [15:0] BlackBoxMulUInt16_I1; // @[Arithmetic.scala 183:27]
  wire [31:0] BlackBoxMulUInt16_O; // @[Arithmetic.scala 183:27]
  wire  BlackBoxMulUInt16_clock; // @[Arithmetic.scala 183:27]
  reg  _T_1; // @[Arithmetic.scala 220:42]
  reg [31:0] _RAND_0;
  reg  _T_2; // @[Arithmetic.scala 220:34]
  reg [31:0] _RAND_1;
  reg  _T_3; // @[Arithmetic.scala 220:26]
  reg [31:0] _RAND_2;
  BlackBoxMulUInt16 BlackBoxMulUInt16 ( // @[Arithmetic.scala 183:27]
    .I0(BlackBoxMulUInt16_I0),
    .I1(BlackBoxMulUInt16_I1),
    .O(BlackBoxMulUInt16_O),
    .clock(BlackBoxMulUInt16_clock)
  );
  assign valid_down = _T_3; // @[Arithmetic.scala 220:16]
  assign O = BlackBoxMulUInt16_O[15:0]; // @[Arithmetic.scala 186:7]
  assign BlackBoxMulUInt16_I0 = I_t0b; // @[Arithmetic.scala 184:21]
  assign BlackBoxMulUInt16_I1 = I_t1b; // @[Arithmetic.scala 185:21]
  assign BlackBoxMulUInt16_clock = clock; // @[Arithmetic.scala 187:24]
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
module Lt(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b,
  output [15:0] O
);
  wire  _T = I_t0b < I_t1b; // @[Arithmetic.scala 465:25]
  assign valid_down = valid_up; // @[Arithmetic.scala 467:14]
  assign O = {{15'd0}, _T}; // @[Arithmetic.scala 465:7]
endmodule
module Sub(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b,
  output [15:0] O
);
  assign valid_down = valid_up; // @[Arithmetic.scala 142:14]
  assign O = I_t0b - I_t1b; // @[Arithmetic.scala 140:7]
endmodule
module AtomTuple_15(
  input         valid_up,
  output        valid_down,
  input  [15:0] I0_t0b,
  input  [15:0] I0_t1b,
  input  [15:0] I1_t0b,
  input  [15:0] I1_t1b,
  output [15:0] O_t0b_t0b,
  output [15:0] O_t0b_t1b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b_t0b = I0_t0b; // @[Tuple.scala 49:9]
  assign O_t0b_t1b = I0_t1b; // @[Tuple.scala 49:9]
  assign O_t1b_t0b = I1_t0b; // @[Tuple.scala 50:9]
  assign O_t1b_t1b = I1_t1b; // @[Tuple.scala 50:9]
endmodule
module FIFO_3(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b_t0b,
  input  [15:0] I_t0b_t1b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b_t0b,
  output [15:0] O_t0b_t1b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  reg [15:0] _T_t0b_t0b [0:3]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_0;
  wire [15:0] _T_t0b_t0b__T_15_data; // @[FIFO.scala 23:33]
  wire [1:0] _T_t0b_t0b__T_15_addr; // @[FIFO.scala 23:33]
  wire [15:0] _T_t0b_t0b__T_5_data; // @[FIFO.scala 23:33]
  wire [1:0] _T_t0b_t0b__T_5_addr; // @[FIFO.scala 23:33]
  wire  _T_t0b_t0b__T_5_mask; // @[FIFO.scala 23:33]
  wire  _T_t0b_t0b__T_5_en; // @[FIFO.scala 23:33]
  reg  _T_t0b_t0b__T_15_en_pipe_0;
  reg [31:0] _RAND_1;
  reg [1:0] _T_t0b_t0b__T_15_addr_pipe_0;
  reg [31:0] _RAND_2;
  reg [15:0] _T_t0b_t1b [0:3]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_3;
  wire [15:0] _T_t0b_t1b__T_15_data; // @[FIFO.scala 23:33]
  wire [1:0] _T_t0b_t1b__T_15_addr; // @[FIFO.scala 23:33]
  wire [15:0] _T_t0b_t1b__T_5_data; // @[FIFO.scala 23:33]
  wire [1:0] _T_t0b_t1b__T_5_addr; // @[FIFO.scala 23:33]
  wire  _T_t0b_t1b__T_5_mask; // @[FIFO.scala 23:33]
  wire  _T_t0b_t1b__T_5_en; // @[FIFO.scala 23:33]
  reg  _T_t0b_t1b__T_15_en_pipe_0;
  reg [31:0] _RAND_4;
  reg [1:0] _T_t0b_t1b__T_15_addr_pipe_0;
  reg [31:0] _RAND_5;
  reg [15:0] _T_t1b_t0b [0:3]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_6;
  wire [15:0] _T_t1b_t0b__T_15_data; // @[FIFO.scala 23:33]
  wire [1:0] _T_t1b_t0b__T_15_addr; // @[FIFO.scala 23:33]
  wire [15:0] _T_t1b_t0b__T_5_data; // @[FIFO.scala 23:33]
  wire [1:0] _T_t1b_t0b__T_5_addr; // @[FIFO.scala 23:33]
  wire  _T_t1b_t0b__T_5_mask; // @[FIFO.scala 23:33]
  wire  _T_t1b_t0b__T_5_en; // @[FIFO.scala 23:33]
  reg  _T_t1b_t0b__T_15_en_pipe_0;
  reg [31:0] _RAND_7;
  reg [1:0] _T_t1b_t0b__T_15_addr_pipe_0;
  reg [31:0] _RAND_8;
  reg [15:0] _T_t1b_t1b [0:3]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_9;
  wire [15:0] _T_t1b_t1b__T_15_data; // @[FIFO.scala 23:33]
  wire [1:0] _T_t1b_t1b__T_15_addr; // @[FIFO.scala 23:33]
  wire [15:0] _T_t1b_t1b__T_5_data; // @[FIFO.scala 23:33]
  wire [1:0] _T_t1b_t1b__T_5_addr; // @[FIFO.scala 23:33]
  wire  _T_t1b_t1b__T_5_mask; // @[FIFO.scala 23:33]
  wire  _T_t1b_t1b__T_5_en; // @[FIFO.scala 23:33]
  reg  _T_t1b_t1b__T_15_en_pipe_0;
  reg [31:0] _RAND_10;
  reg [1:0] _T_t1b_t1b__T_15_addr_pipe_0;
  reg [31:0] _RAND_11;
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_12;
  reg [1:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_13;
  reg [1:0] value_2; // @[Counter.scala 29:33]
  reg [31:0] _RAND_14;
  wire [1:0] _T_4 = value_2 + 2'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value < 2'h3; // @[FIFO.scala 38:39]
  wire [1:0] _T_9 = value + 2'h1; // @[Counter.scala 39:22]
  wire  _T_10 = value >= 2'h2; // @[FIFO.scala 42:39]
  wire [1:0] _T_18 = value_1 + 2'h1; // @[Counter.scala 39:22]
  wire  _GEN_5 = _T_10 & _T_10; // @[FIFO.scala 42:57]
  assign _T_t0b_t0b__T_15_addr = _T_t0b_t0b__T_15_addr_pipe_0;
  assign _T_t0b_t0b__T_15_data = _T_t0b_t0b[_T_t0b_t0b__T_15_addr]; // @[FIFO.scala 23:33]
  assign _T_t0b_t0b__T_5_data = I_t0b_t0b;
  assign _T_t0b_t0b__T_5_addr = value_2;
  assign _T_t0b_t0b__T_5_mask = 1'h1;
  assign _T_t0b_t0b__T_5_en = valid_up;
  assign _T_t0b_t1b__T_15_addr = _T_t0b_t1b__T_15_addr_pipe_0;
  assign _T_t0b_t1b__T_15_data = _T_t0b_t1b[_T_t0b_t1b__T_15_addr]; // @[FIFO.scala 23:33]
  assign _T_t0b_t1b__T_5_data = I_t0b_t1b;
  assign _T_t0b_t1b__T_5_addr = value_2;
  assign _T_t0b_t1b__T_5_mask = 1'h1;
  assign _T_t0b_t1b__T_5_en = valid_up;
  assign _T_t1b_t0b__T_15_addr = _T_t1b_t0b__T_15_addr_pipe_0;
  assign _T_t1b_t0b__T_15_data = _T_t1b_t0b[_T_t1b_t0b__T_15_addr]; // @[FIFO.scala 23:33]
  assign _T_t1b_t0b__T_5_data = I_t1b_t0b;
  assign _T_t1b_t0b__T_5_addr = value_2;
  assign _T_t1b_t0b__T_5_mask = 1'h1;
  assign _T_t1b_t0b__T_5_en = valid_up;
  assign _T_t1b_t1b__T_15_addr = _T_t1b_t1b__T_15_addr_pipe_0;
  assign _T_t1b_t1b__T_15_data = _T_t1b_t1b[_T_t1b_t1b__T_15_addr]; // @[FIFO.scala 23:33]
  assign _T_t1b_t1b__T_5_data = I_t1b_t1b;
  assign _T_t1b_t1b__T_5_addr = value_2;
  assign _T_t1b_t1b__T_5_mask = 1'h1;
  assign _T_t1b_t1b__T_5_en = valid_up;
  assign valid_down = value == 2'h3; // @[FIFO.scala 33:16]
  assign O_t0b_t0b = _T_t0b_t0b__T_15_data; // @[FIFO.scala 43:11]
  assign O_t0b_t1b = _T_t0b_t1b__T_15_data; // @[FIFO.scala 43:11]
  assign O_t1b_t0b = _T_t1b_t0b__T_15_data; // @[FIFO.scala 43:11]
  assign O_t1b_t1b = _T_t1b_t1b__T_15_data; // @[FIFO.scala 43:11]
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
  _RAND_0 = {1{`RANDOM}};
  `ifdef RANDOMIZE_MEM_INIT
  for (initvar = 0; initvar < 4; initvar = initvar+1)
    _T_t0b_t0b[initvar] = _RAND_0[15:0];
  `endif // RANDOMIZE_MEM_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_t0b_t0b__T_15_en_pipe_0 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_t0b_t0b__T_15_addr_pipe_0 = _RAND_2[1:0];
  `endif // RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  `ifdef RANDOMIZE_MEM_INIT
  for (initvar = 0; initvar < 4; initvar = initvar+1)
    _T_t0b_t1b[initvar] = _RAND_3[15:0];
  `endif // RANDOMIZE_MEM_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_t0b_t1b__T_15_en_pipe_0 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_t0b_t1b__T_15_addr_pipe_0 = _RAND_5[1:0];
  `endif // RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  `ifdef RANDOMIZE_MEM_INIT
  for (initvar = 0; initvar < 4; initvar = initvar+1)
    _T_t1b_t0b[initvar] = _RAND_6[15:0];
  `endif // RANDOMIZE_MEM_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_t1b_t0b__T_15_en_pipe_0 = _RAND_7[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{`RANDOM}};
  _T_t1b_t0b__T_15_addr_pipe_0 = _RAND_8[1:0];
  `endif // RANDOMIZE_REG_INIT
  _RAND_9 = {1{`RANDOM}};
  `ifdef RANDOMIZE_MEM_INIT
  for (initvar = 0; initvar < 4; initvar = initvar+1)
    _T_t1b_t1b[initvar] = _RAND_9[15:0];
  `endif // RANDOMIZE_MEM_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_10 = {1{`RANDOM}};
  _T_t1b_t1b__T_15_en_pipe_0 = _RAND_10[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_11 = {1{`RANDOM}};
  _T_t1b_t1b__T_15_addr_pipe_0 = _RAND_11[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_12 = {1{`RANDOM}};
  value = _RAND_12[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_13 = {1{`RANDOM}};
  value_1 = _RAND_13[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_14 = {1{`RANDOM}};
  value_2 = _RAND_14[1:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if(_T_t0b_t0b__T_5_en & _T_t0b_t0b__T_5_mask) begin
      _T_t0b_t0b[_T_t0b_t0b__T_5_addr] <= _T_t0b_t0b__T_5_data; // @[FIFO.scala 23:33]
    end
    _T_t0b_t0b__T_15_en_pipe_0 <= valid_up & _GEN_5;
    if (valid_up & _GEN_5) begin
      _T_t0b_t0b__T_15_addr_pipe_0 <= value_1;
    end
    if(_T_t0b_t1b__T_5_en & _T_t0b_t1b__T_5_mask) begin
      _T_t0b_t1b[_T_t0b_t1b__T_5_addr] <= _T_t0b_t1b__T_5_data; // @[FIFO.scala 23:33]
    end
    _T_t0b_t1b__T_15_en_pipe_0 <= valid_up & _GEN_5;
    if (valid_up & _GEN_5) begin
      _T_t0b_t1b__T_15_addr_pipe_0 <= value_1;
    end
    if(_T_t1b_t0b__T_5_en & _T_t1b_t0b__T_5_mask) begin
      _T_t1b_t0b[_T_t1b_t0b__T_5_addr] <= _T_t1b_t0b__T_5_data; // @[FIFO.scala 23:33]
    end
    _T_t1b_t0b__T_15_en_pipe_0 <= valid_up & _GEN_5;
    if (valid_up & _GEN_5) begin
      _T_t1b_t0b__T_15_addr_pipe_0 <= value_1;
    end
    if(_T_t1b_t1b__T_5_en & _T_t1b_t1b__T_5_mask) begin
      _T_t1b_t1b[_T_t1b_t1b__T_5_addr] <= _T_t1b_t1b__T_5_data; // @[FIFO.scala 23:33]
    end
    _T_t1b_t1b__T_15_en_pipe_0 <= valid_up & _GEN_5;
    if (valid_up & _GEN_5) begin
      _T_t1b_t1b__T_15_addr_pipe_0 <= value_1;
    end
    if (reset) begin
      value <= 2'h0;
    end else if (valid_up) begin
      if (_T_6) begin
        value <= _T_9;
      end
    end
    if (reset) begin
      value_1 <= 2'h0;
    end else if (valid_up) begin
      if (_T_10) begin
        value_1 <= _T_18;
      end
    end
    if (reset) begin
      value_2 <= 2'h0;
    end else if (valid_up) begin
      value_2 <= _T_4;
    end
  end
endmodule
module AtomTuple_16(
  input         valid_up,
  output        valid_down,
  input         I0,
  input  [15:0] I1_t0b_t0b,
  input  [15:0] I1_t0b_t1b,
  input  [15:0] I1_t1b_t0b,
  input  [15:0] I1_t1b_t1b,
  output        O_t0b,
  output [15:0] O_t1b_t0b_t0b,
  output [15:0] O_t1b_t0b_t1b,
  output [15:0] O_t1b_t1b_t0b,
  output [15:0] O_t1b_t1b_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b = I0; // @[Tuple.scala 49:9]
  assign O_t1b_t0b_t0b = I1_t0b_t0b; // @[Tuple.scala 50:9]
  assign O_t1b_t0b_t1b = I1_t0b_t1b; // @[Tuple.scala 50:9]
  assign O_t1b_t1b_t0b = I1_t1b_t0b; // @[Tuple.scala 50:9]
  assign O_t1b_t1b_t1b = I1_t1b_t1b; // @[Tuple.scala 50:9]
endmodule
module If_1(
  input         valid_up,
  output        valid_down,
  input         I_t0b,
  input  [15:0] I_t1b_t0b_t0b,
  input  [15:0] I_t1b_t0b_t1b,
  input  [15:0] I_t1b_t1b_t0b,
  input  [15:0] I_t1b_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b
);
  assign valid_down = valid_up; // @[Arithmetic.scala 528:14]
  assign O_t0b = I_t0b ? I_t1b_t0b_t0b : I_t1b_t1b_t0b; // @[Arithmetic.scala 526:9 Arithmetic.scala 527:20]
  assign O_t1b = I_t0b ? I_t1b_t0b_t1b : I_t1b_t1b_t1b; // @[Arithmetic.scala 526:9 Arithmetic.scala 527:20]
endmodule
module Module_1(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n20_valid_up; // @[Top.scala 49:21]
  wire  n20_valid_down; // @[Top.scala 49:21]
  wire [15:0] n20_I_t0b; // @[Top.scala 49:21]
  wire [15:0] n20_O; // @[Top.scala 49:21]
  wire  n53_clock; // @[Top.scala 52:21]
  wire  n53_reset; // @[Top.scala 52:21]
  wire  n53_valid_up; // @[Top.scala 52:21]
  wire  n53_valid_down; // @[Top.scala 52:21]
  wire [15:0] n53_I; // @[Top.scala 52:21]
  wire [15:0] n53_O; // @[Top.scala 52:21]
  wire  n41_clock; // @[Top.scala 55:21]
  wire  n41_reset; // @[Top.scala 55:21]
  wire  n41_valid_up; // @[Top.scala 55:21]
  wire  n41_valid_down; // @[Top.scala 55:21]
  wire [15:0] n41_I; // @[Top.scala 55:21]
  wire [15:0] n41_O; // @[Top.scala 55:21]
  wire  n21_valid_up; // @[Top.scala 58:21]
  wire  n21_valid_down; // @[Top.scala 58:21]
  wire [15:0] n21_I_t1b_t0b; // @[Top.scala 58:21]
  wire [15:0] n21_I_t1b_t1b; // @[Top.scala 58:21]
  wire [15:0] n21_O_t0b; // @[Top.scala 58:21]
  wire [15:0] n21_O_t1b; // @[Top.scala 58:21]
  wire  n22_valid_up; // @[Top.scala 61:21]
  wire  n22_valid_down; // @[Top.scala 61:21]
  wire [15:0] n22_I_t0b; // @[Top.scala 61:21]
  wire [15:0] n22_O; // @[Top.scala 61:21]
  wire  n23_valid_up; // @[Top.scala 64:21]
  wire  n23_valid_down; // @[Top.scala 64:21]
  wire [15:0] n23_I_t1b; // @[Top.scala 64:21]
  wire [15:0] n23_O; // @[Top.scala 64:21]
  wire  n24_valid_up; // @[Top.scala 67:21]
  wire  n24_valid_down; // @[Top.scala 67:21]
  wire [15:0] n24_I0; // @[Top.scala 67:21]
  wire [15:0] n24_I1; // @[Top.scala 67:21]
  wire [15:0] n24_O_t0b; // @[Top.scala 67:21]
  wire [15:0] n24_O_t1b; // @[Top.scala 67:21]
  wire  n25_valid_up; // @[Top.scala 71:21]
  wire  n25_valid_down; // @[Top.scala 71:21]
  wire [15:0] n25_I_t0b; // @[Top.scala 71:21]
  wire [15:0] n25_I_t1b; // @[Top.scala 71:21]
  wire [15:0] n25_O; // @[Top.scala 71:21]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n28_valid_up; // @[Top.scala 75:21]
  wire  n28_valid_down; // @[Top.scala 75:21]
  wire [15:0] n28_I0; // @[Top.scala 75:21]
  wire [15:0] n28_O_t0b; // @[Top.scala 75:21]
  wire  n29_valid_up; // @[Top.scala 79:21]
  wire  n29_valid_down; // @[Top.scala 79:21]
  wire [15:0] n29_I_t0b; // @[Top.scala 79:21]
  wire [15:0] n29_O; // @[Top.scala 79:21]
  wire  n30_valid_up; // @[Top.scala 82:21]
  wire  n30_valid_down; // @[Top.scala 82:21]
  wire [15:0] n30_I0; // @[Top.scala 82:21]
  wire [15:0] n30_I1; // @[Top.scala 82:21]
  wire [15:0] n30_O_t0b; // @[Top.scala 82:21]
  wire [15:0] n30_O_t1b; // @[Top.scala 82:21]
  wire  n31_valid_up; // @[Top.scala 86:21]
  wire  n31_valid_down; // @[Top.scala 86:21]
  wire [15:0] n31_I_t0b; // @[Top.scala 86:21]
  wire [15:0] n31_I_t1b; // @[Top.scala 86:21]
  wire [15:0] n31_O; // @[Top.scala 86:21]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n34_valid_up; // @[Top.scala 90:21]
  wire  n34_valid_down; // @[Top.scala 90:21]
  wire [15:0] n34_I0; // @[Top.scala 90:21]
  wire [15:0] n34_I1; // @[Top.scala 90:21]
  wire [15:0] n34_O_t0b; // @[Top.scala 90:21]
  wire [15:0] n34_O_t1b; // @[Top.scala 90:21]
  wire  n35_valid_up; // @[Top.scala 94:21]
  wire  n35_valid_down; // @[Top.scala 94:21]
  wire [15:0] n35_I_t0b; // @[Top.scala 94:21]
  wire [15:0] n35_I_t1b; // @[Top.scala 94:21]
  wire [15:0] n35_O; // @[Top.scala 94:21]
  wire  n36_valid_up; // @[Top.scala 97:21]
  wire  n36_valid_down; // @[Top.scala 97:21]
  wire [15:0] n36_I0; // @[Top.scala 97:21]
  wire [15:0] n36_I1; // @[Top.scala 97:21]
  wire [15:0] n36_O_t0b; // @[Top.scala 97:21]
  wire [15:0] n36_O_t1b; // @[Top.scala 97:21]
  wire  n37_valid_up; // @[Top.scala 101:21]
  wire  n37_valid_down; // @[Top.scala 101:21]
  wire  n37_I0; // @[Top.scala 101:21]
  wire [15:0] n37_I1_t0b; // @[Top.scala 101:21]
  wire [15:0] n37_I1_t1b; // @[Top.scala 101:21]
  wire  n37_O_t0b; // @[Top.scala 101:21]
  wire [15:0] n37_O_t1b_t0b; // @[Top.scala 101:21]
  wire [15:0] n37_O_t1b_t1b; // @[Top.scala 101:21]
  wire  n38_valid_up; // @[Top.scala 105:21]
  wire  n38_valid_down; // @[Top.scala 105:21]
  wire  n38_I_t0b; // @[Top.scala 105:21]
  wire [15:0] n38_I_t1b_t0b; // @[Top.scala 105:21]
  wire [15:0] n38_I_t1b_t1b; // @[Top.scala 105:21]
  wire [15:0] n38_O; // @[Top.scala 105:21]
  wire  n39_valid_up; // @[Top.scala 108:21]
  wire  n39_valid_down; // @[Top.scala 108:21]
  wire [15:0] n39_I0; // @[Top.scala 108:21]
  wire [15:0] n39_I1; // @[Top.scala 108:21]
  wire [15:0] n39_O_t0b; // @[Top.scala 108:21]
  wire [15:0] n39_O_t1b; // @[Top.scala 108:21]
  wire  n40_clock; // @[Top.scala 112:21]
  wire  n40_reset; // @[Top.scala 112:21]
  wire  n40_valid_up; // @[Top.scala 112:21]
  wire  n40_valid_down; // @[Top.scala 112:21]
  wire [15:0] n40_I_t0b; // @[Top.scala 112:21]
  wire [15:0] n40_I_t1b; // @[Top.scala 112:21]
  wire [15:0] n40_O; // @[Top.scala 112:21]
  wire  n42_valid_up; // @[Top.scala 115:21]
  wire  n42_valid_down; // @[Top.scala 115:21]
  wire [15:0] n42_I0; // @[Top.scala 115:21]
  wire [15:0] n42_I1; // @[Top.scala 115:21]
  wire [15:0] n42_O_t0b; // @[Top.scala 115:21]
  wire [15:0] n42_O_t1b; // @[Top.scala 115:21]
  wire  n43_valid_up; // @[Top.scala 119:21]
  wire  n43_valid_down; // @[Top.scala 119:21]
  wire [15:0] n43_I_t0b; // @[Top.scala 119:21]
  wire [15:0] n43_I_t1b; // @[Top.scala 119:21]
  wire [15:0] n43_O; // @[Top.scala 119:21]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n45_valid_up; // @[Top.scala 123:21]
  wire  n45_valid_down; // @[Top.scala 123:21]
  wire [15:0] n45_I0; // @[Top.scala 123:21]
  wire [15:0] n45_I1; // @[Top.scala 123:21]
  wire [15:0] n45_O_t0b; // @[Top.scala 123:21]
  wire [15:0] n45_O_t1b; // @[Top.scala 123:21]
  wire  n46_valid_up; // @[Top.scala 127:21]
  wire  n46_valid_down; // @[Top.scala 127:21]
  wire [15:0] n46_I_t0b; // @[Top.scala 127:21]
  wire [15:0] n46_I_t1b; // @[Top.scala 127:21]
  wire [15:0] n46_O; // @[Top.scala 127:21]
  wire  n47_valid_up; // @[Top.scala 130:21]
  wire  n47_valid_down; // @[Top.scala 130:21]
  wire [15:0] n47_I0; // @[Top.scala 130:21]
  wire [15:0] n47_I1; // @[Top.scala 130:21]
  wire [15:0] n47_O_t0b; // @[Top.scala 130:21]
  wire [15:0] n47_O_t1b; // @[Top.scala 130:21]
  wire  n48_valid_up; // @[Top.scala 134:21]
  wire  n48_valid_down; // @[Top.scala 134:21]
  wire [15:0] n48_I0; // @[Top.scala 134:21]
  wire [15:0] n48_I1; // @[Top.scala 134:21]
  wire [15:0] n48_O_t0b; // @[Top.scala 134:21]
  wire [15:0] n48_O_t1b; // @[Top.scala 134:21]
  wire  n49_valid_up; // @[Top.scala 138:21]
  wire  n49_valid_down; // @[Top.scala 138:21]
  wire [15:0] n49_I0_t0b; // @[Top.scala 138:21]
  wire [15:0] n49_I0_t1b; // @[Top.scala 138:21]
  wire [15:0] n49_I1_t0b; // @[Top.scala 138:21]
  wire [15:0] n49_I1_t1b; // @[Top.scala 138:21]
  wire [15:0] n49_O_t0b_t0b; // @[Top.scala 138:21]
  wire [15:0] n49_O_t0b_t1b; // @[Top.scala 138:21]
  wire [15:0] n49_O_t1b_t0b; // @[Top.scala 138:21]
  wire [15:0] n49_O_t1b_t1b; // @[Top.scala 138:21]
  wire  n50_clock; // @[Top.scala 142:21]
  wire  n50_reset; // @[Top.scala 142:21]
  wire  n50_valid_up; // @[Top.scala 142:21]
  wire  n50_valid_down; // @[Top.scala 142:21]
  wire [15:0] n50_I_t0b_t0b; // @[Top.scala 142:21]
  wire [15:0] n50_I_t0b_t1b; // @[Top.scala 142:21]
  wire [15:0] n50_I_t1b_t0b; // @[Top.scala 142:21]
  wire [15:0] n50_I_t1b_t1b; // @[Top.scala 142:21]
  wire [15:0] n50_O_t0b_t0b; // @[Top.scala 142:21]
  wire [15:0] n50_O_t0b_t1b; // @[Top.scala 142:21]
  wire [15:0] n50_O_t1b_t0b; // @[Top.scala 142:21]
  wire [15:0] n50_O_t1b_t1b; // @[Top.scala 142:21]
  wire  n51_valid_up; // @[Top.scala 145:21]
  wire  n51_valid_down; // @[Top.scala 145:21]
  wire  n51_I0; // @[Top.scala 145:21]
  wire [15:0] n51_I1_t0b_t0b; // @[Top.scala 145:21]
  wire [15:0] n51_I1_t0b_t1b; // @[Top.scala 145:21]
  wire [15:0] n51_I1_t1b_t0b; // @[Top.scala 145:21]
  wire [15:0] n51_I1_t1b_t1b; // @[Top.scala 145:21]
  wire  n51_O_t0b; // @[Top.scala 145:21]
  wire [15:0] n51_O_t1b_t0b_t0b; // @[Top.scala 145:21]
  wire [15:0] n51_O_t1b_t0b_t1b; // @[Top.scala 145:21]
  wire [15:0] n51_O_t1b_t1b_t0b; // @[Top.scala 145:21]
  wire [15:0] n51_O_t1b_t1b_t1b; // @[Top.scala 145:21]
  wire  n52_valid_up; // @[Top.scala 149:21]
  wire  n52_valid_down; // @[Top.scala 149:21]
  wire  n52_I_t0b; // @[Top.scala 149:21]
  wire [15:0] n52_I_t1b_t0b_t0b; // @[Top.scala 149:21]
  wire [15:0] n52_I_t1b_t0b_t1b; // @[Top.scala 149:21]
  wire [15:0] n52_I_t1b_t1b_t0b; // @[Top.scala 149:21]
  wire [15:0] n52_I_t1b_t1b_t1b; // @[Top.scala 149:21]
  wire [15:0] n52_O_t0b; // @[Top.scala 149:21]
  wire [15:0] n52_O_t1b; // @[Top.scala 149:21]
  wire  n54_valid_up; // @[Top.scala 152:21]
  wire  n54_valid_down; // @[Top.scala 152:21]
  wire [15:0] n54_I0; // @[Top.scala 152:21]
  wire [15:0] n54_I1_t0b; // @[Top.scala 152:21]
  wire [15:0] n54_I1_t1b; // @[Top.scala 152:21]
  wire [15:0] n54_O_t0b; // @[Top.scala 152:21]
  wire [15:0] n54_O_t1b_t0b; // @[Top.scala 152:21]
  wire [15:0] n54_O_t1b_t1b; // @[Top.scala 152:21]
  Fst n20 ( // @[Top.scala 49:21]
    .valid_up(n20_valid_up),
    .valid_down(n20_valid_down),
    .I_t0b(n20_I_t0b),
    .O(n20_O)
  );
  FIFO_1 n53 ( // @[Top.scala 52:21]
    .clock(n53_clock),
    .reset(n53_reset),
    .valid_up(n53_valid_up),
    .valid_down(n53_valid_down),
    .I(n53_I),
    .O(n53_O)
  );
  FIFO_1 n41 ( // @[Top.scala 55:21]
    .clock(n41_clock),
    .reset(n41_reset),
    .valid_up(n41_valid_up),
    .valid_down(n41_valid_down),
    .I(n41_I),
    .O(n41_O)
  );
  Snd n21 ( // @[Top.scala 58:21]
    .valid_up(n21_valid_up),
    .valid_down(n21_valid_down),
    .I_t1b_t0b(n21_I_t1b_t0b),
    .I_t1b_t1b(n21_I_t1b_t1b),
    .O_t0b(n21_O_t0b),
    .O_t1b(n21_O_t1b)
  );
  Fst_1 n22 ( // @[Top.scala 61:21]
    .valid_up(n22_valid_up),
    .valid_down(n22_valid_down),
    .I_t0b(n22_I_t0b),
    .O(n22_O)
  );
  Snd_1 n23 ( // @[Top.scala 64:21]
    .valid_up(n23_valid_up),
    .valid_down(n23_valid_down),
    .I_t1b(n23_I_t1b),
    .O(n23_O)
  );
  AtomTuple_1 n24 ( // @[Top.scala 67:21]
    .valid_up(n24_valid_up),
    .valid_down(n24_valid_down),
    .I0(n24_I0),
    .I1(n24_I1),
    .O_t0b(n24_O_t0b),
    .O_t1b(n24_O_t1b)
  );
  Add n25 ( // @[Top.scala 71:21]
    .valid_up(n25_valid_up),
    .valid_down(n25_valid_down),
    .I_t0b(n25_I_t0b),
    .I_t1b(n25_I_t1b),
    .O(n25_O)
  );
  InitialDelayCounter InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n28 ( // @[Top.scala 75:21]
    .valid_up(n28_valid_up),
    .valid_down(n28_valid_down),
    .I0(n28_I0),
    .O_t0b(n28_O_t0b)
  );
  RShift n29 ( // @[Top.scala 79:21]
    .valid_up(n29_valid_up),
    .valid_down(n29_valid_down),
    .I_t0b(n29_I_t0b),
    .O(n29_O)
  );
  AtomTuple_1 n30 ( // @[Top.scala 82:21]
    .valid_up(n30_valid_up),
    .valid_down(n30_valid_down),
    .I0(n30_I0),
    .I1(n30_I1),
    .O_t0b(n30_O_t0b),
    .O_t1b(n30_O_t1b)
  );
  Eq n31 ( // @[Top.scala 86:21]
    .valid_up(n31_valid_up),
    .valid_down(n31_valid_down),
    .I_t0b(n31_I_t0b),
    .I_t1b(n31_I_t1b),
    .O(n31_O)
  );
  InitialDelayCounter InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n34 ( // @[Top.scala 90:21]
    .valid_up(n34_valid_up),
    .valid_down(n34_valid_down),
    .I0(n34_I0),
    .I1(n34_I1),
    .O_t0b(n34_O_t0b),
    .O_t1b(n34_O_t1b)
  );
  Add n35 ( // @[Top.scala 94:21]
    .valid_up(n35_valid_up),
    .valid_down(n35_valid_down),
    .I_t0b(n35_I_t0b),
    .I_t1b(n35_I_t1b),
    .O(n35_O)
  );
  AtomTuple_1 n36 ( // @[Top.scala 97:21]
    .valid_up(n36_valid_up),
    .valid_down(n36_valid_down),
    .I0(n36_I0),
    .I1(n36_I1),
    .O_t0b(n36_O_t0b),
    .O_t1b(n36_O_t1b)
  );
  AtomTuple_9 n37 ( // @[Top.scala 101:21]
    .valid_up(n37_valid_up),
    .valid_down(n37_valid_down),
    .I0(n37_I0),
    .I1_t0b(n37_I1_t0b),
    .I1_t1b(n37_I1_t1b),
    .O_t0b(n37_O_t0b),
    .O_t1b_t0b(n37_O_t1b_t0b),
    .O_t1b_t1b(n37_O_t1b_t1b)
  );
  If n38 ( // @[Top.scala 105:21]
    .valid_up(n38_valid_up),
    .valid_down(n38_valid_down),
    .I_t0b(n38_I_t0b),
    .I_t1b_t0b(n38_I_t1b_t0b),
    .I_t1b_t1b(n38_I_t1b_t1b),
    .O(n38_O)
  );
  AtomTuple_1 n39 ( // @[Top.scala 108:21]
    .valid_up(n39_valid_up),
    .valid_down(n39_valid_down),
    .I0(n39_I0),
    .I1(n39_I1),
    .O_t0b(n39_O_t0b),
    .O_t1b(n39_O_t1b)
  );
  Mul n40 ( // @[Top.scala 112:21]
    .clock(n40_clock),
    .reset(n40_reset),
    .valid_up(n40_valid_up),
    .valid_down(n40_valid_down),
    .I_t0b(n40_I_t0b),
    .I_t1b(n40_I_t1b),
    .O(n40_O)
  );
  AtomTuple_1 n42 ( // @[Top.scala 115:21]
    .valid_up(n42_valid_up),
    .valid_down(n42_valid_down),
    .I0(n42_I0),
    .I1(n42_I1),
    .O_t0b(n42_O_t0b),
    .O_t1b(n42_O_t1b)
  );
  Lt n43 ( // @[Top.scala 119:21]
    .valid_up(n43_valid_up),
    .valid_down(n43_valid_down),
    .I_t0b(n43_I_t0b),
    .I_t1b(n43_I_t1b),
    .O(n43_O)
  );
  InitialDelayCounter InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n45 ( // @[Top.scala 123:21]
    .valid_up(n45_valid_up),
    .valid_down(n45_valid_down),
    .I0(n45_I0),
    .I1(n45_I1),
    .O_t0b(n45_O_t0b),
    .O_t1b(n45_O_t1b)
  );
  Sub n46 ( // @[Top.scala 127:21]
    .valid_up(n46_valid_up),
    .valid_down(n46_valid_down),
    .I_t0b(n46_I_t0b),
    .I_t1b(n46_I_t1b),
    .O(n46_O)
  );
  AtomTuple_1 n47 ( // @[Top.scala 130:21]
    .valid_up(n47_valid_up),
    .valid_down(n47_valid_down),
    .I0(n47_I0),
    .I1(n47_I1),
    .O_t0b(n47_O_t0b),
    .O_t1b(n47_O_t1b)
  );
  AtomTuple_1 n48 ( // @[Top.scala 134:21]
    .valid_up(n48_valid_up),
    .valid_down(n48_valid_down),
    .I0(n48_I0),
    .I1(n48_I1),
    .O_t0b(n48_O_t0b),
    .O_t1b(n48_O_t1b)
  );
  AtomTuple_15 n49 ( // @[Top.scala 138:21]
    .valid_up(n49_valid_up),
    .valid_down(n49_valid_down),
    .I0_t0b(n49_I0_t0b),
    .I0_t1b(n49_I0_t1b),
    .I1_t0b(n49_I1_t0b),
    .I1_t1b(n49_I1_t1b),
    .O_t0b_t0b(n49_O_t0b_t0b),
    .O_t0b_t1b(n49_O_t0b_t1b),
    .O_t1b_t0b(n49_O_t1b_t0b),
    .O_t1b_t1b(n49_O_t1b_t1b)
  );
  FIFO_3 n50 ( // @[Top.scala 142:21]
    .clock(n50_clock),
    .reset(n50_reset),
    .valid_up(n50_valid_up),
    .valid_down(n50_valid_down),
    .I_t0b_t0b(n50_I_t0b_t0b),
    .I_t0b_t1b(n50_I_t0b_t1b),
    .I_t1b_t0b(n50_I_t1b_t0b),
    .I_t1b_t1b(n50_I_t1b_t1b),
    .O_t0b_t0b(n50_O_t0b_t0b),
    .O_t0b_t1b(n50_O_t0b_t1b),
    .O_t1b_t0b(n50_O_t1b_t0b),
    .O_t1b_t1b(n50_O_t1b_t1b)
  );
  AtomTuple_16 n51 ( // @[Top.scala 145:21]
    .valid_up(n51_valid_up),
    .valid_down(n51_valid_down),
    .I0(n51_I0),
    .I1_t0b_t0b(n51_I1_t0b_t0b),
    .I1_t0b_t1b(n51_I1_t0b_t1b),
    .I1_t1b_t0b(n51_I1_t1b_t0b),
    .I1_t1b_t1b(n51_I1_t1b_t1b),
    .O_t0b(n51_O_t0b),
    .O_t1b_t0b_t0b(n51_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n51_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n51_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n51_O_t1b_t1b_t1b)
  );
  If_1 n52 ( // @[Top.scala 149:21]
    .valid_up(n52_valid_up),
    .valid_down(n52_valid_down),
    .I_t0b(n52_I_t0b),
    .I_t1b_t0b_t0b(n52_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n52_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n52_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n52_I_t1b_t1b_t1b),
    .O_t0b(n52_O_t0b),
    .O_t1b(n52_O_t1b)
  );
  AtomTuple_3 n54 ( // @[Top.scala 152:21]
    .valid_up(n54_valid_up),
    .valid_down(n54_valid_down),
    .I0(n54_I0),
    .I1_t0b(n54_I1_t0b),
    .I1_t1b(n54_I1_t1b),
    .O_t0b(n54_O_t0b),
    .O_t1b_t0b(n54_O_t1b_t0b),
    .O_t1b_t1b(n54_O_t1b_t1b)
  );
  assign valid_down = n54_valid_down; // @[Top.scala 157:16]
  assign O_t0b = n54_O_t0b; // @[Top.scala 156:7]
  assign O_t1b_t0b = n54_O_t1b_t0b; // @[Top.scala 156:7]
  assign O_t1b_t1b = n54_O_t1b_t1b; // @[Top.scala 156:7]
  assign n20_valid_up = valid_up; // @[Top.scala 51:18]
  assign n20_I_t0b = I_t0b; // @[Top.scala 50:11]
  assign n53_clock = clock;
  assign n53_reset = reset;
  assign n53_valid_up = n20_valid_down; // @[Top.scala 54:18]
  assign n53_I = n20_O; // @[Top.scala 53:11]
  assign n41_clock = clock;
  assign n41_reset = reset;
  assign n41_valid_up = n20_valid_down; // @[Top.scala 57:18]
  assign n41_I = n20_O; // @[Top.scala 56:11]
  assign n21_valid_up = valid_up; // @[Top.scala 60:18]
  assign n21_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 59:11]
  assign n21_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 59:11]
  assign n22_valid_up = n21_valid_down; // @[Top.scala 63:18]
  assign n22_I_t0b = n21_O_t0b; // @[Top.scala 62:11]
  assign n23_valid_up = n21_valid_down; // @[Top.scala 66:18]
  assign n23_I_t1b = n21_O_t1b; // @[Top.scala 65:11]
  assign n24_valid_up = n22_valid_down & n23_valid_down; // @[Top.scala 70:18]
  assign n24_I0 = n22_O; // @[Top.scala 68:12]
  assign n24_I1 = n23_O; // @[Top.scala 69:12]
  assign n25_valid_up = n24_valid_down; // @[Top.scala 73:18]
  assign n25_I_t0b = n24_O_t0b; // @[Top.scala 72:11]
  assign n25_I_t1b = n24_O_t1b; // @[Top.scala 72:11]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n28_valid_up = n25_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 78:18]
  assign n28_I0 = n25_O; // @[Top.scala 76:12]
  assign n29_valid_up = n28_valid_down; // @[Top.scala 81:18]
  assign n29_I_t0b = n28_O_t0b; // @[Top.scala 80:11]
  assign n30_valid_up = n29_valid_down & n22_valid_down; // @[Top.scala 85:18]
  assign n30_I0 = n29_O; // @[Top.scala 83:12]
  assign n30_I1 = n22_O; // @[Top.scala 84:12]
  assign n31_valid_up = n30_valid_down; // @[Top.scala 88:18]
  assign n31_I_t0b = n30_O_t0b; // @[Top.scala 87:11]
  assign n31_I_t1b = n30_O_t1b; // @[Top.scala 87:11]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n34_valid_up = n29_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 93:18]
  assign n34_I0 = n29_O; // @[Top.scala 91:12]
  assign n34_I1 = 16'h1; // @[Top.scala 92:12]
  assign n35_valid_up = n34_valid_down; // @[Top.scala 96:18]
  assign n35_I_t0b = n34_O_t0b; // @[Top.scala 95:11]
  assign n35_I_t1b = n34_O_t1b; // @[Top.scala 95:11]
  assign n36_valid_up = n35_valid_down & n29_valid_down; // @[Top.scala 100:18]
  assign n36_I0 = n35_O; // @[Top.scala 98:12]
  assign n36_I1 = n29_O; // @[Top.scala 99:12]
  assign n37_valid_up = n31_valid_down & n36_valid_down; // @[Top.scala 104:18]
  assign n37_I0 = n31_O[0]; // @[Top.scala 102:12]
  assign n37_I1_t0b = n36_O_t0b; // @[Top.scala 103:12]
  assign n37_I1_t1b = n36_O_t1b; // @[Top.scala 103:12]
  assign n38_valid_up = n37_valid_down; // @[Top.scala 107:18]
  assign n38_I_t0b = n37_O_t0b; // @[Top.scala 106:11]
  assign n38_I_t1b_t0b = n37_O_t1b_t0b; // @[Top.scala 106:11]
  assign n38_I_t1b_t1b = n37_O_t1b_t1b; // @[Top.scala 106:11]
  assign n39_valid_up = n38_valid_down; // @[Top.scala 111:18]
  assign n39_I0 = n38_O; // @[Top.scala 109:12]
  assign n39_I1 = n38_O; // @[Top.scala 110:12]
  assign n40_clock = clock;
  assign n40_reset = reset;
  assign n40_valid_up = n39_valid_down; // @[Top.scala 114:18]
  assign n40_I_t0b = n39_O_t0b; // @[Top.scala 113:11]
  assign n40_I_t1b = n39_O_t1b; // @[Top.scala 113:11]
  assign n42_valid_up = n41_valid_down & n40_valid_down; // @[Top.scala 118:18]
  assign n42_I0 = n41_O; // @[Top.scala 116:12]
  assign n42_I1 = n40_O; // @[Top.scala 117:12]
  assign n43_valid_up = n42_valid_down; // @[Top.scala 121:18]
  assign n43_I_t0b = n42_O_t0b; // @[Top.scala 120:11]
  assign n43_I_t1b = n42_O_t1b; // @[Top.scala 120:11]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n45_valid_up = n38_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 126:18]
  assign n45_I0 = n38_O; // @[Top.scala 124:12]
  assign n45_I1 = 16'h1; // @[Top.scala 125:12]
  assign n46_valid_up = n45_valid_down; // @[Top.scala 129:18]
  assign n46_I_t0b = n45_O_t0b; // @[Top.scala 128:11]
  assign n46_I_t1b = n45_O_t1b; // @[Top.scala 128:11]
  assign n47_valid_up = n22_valid_down & n46_valid_down; // @[Top.scala 133:18]
  assign n47_I0 = n22_O; // @[Top.scala 131:12]
  assign n47_I1 = n46_O; // @[Top.scala 132:12]
  assign n48_valid_up = n38_valid_down & n23_valid_down; // @[Top.scala 137:18]
  assign n48_I0 = n38_O; // @[Top.scala 135:12]
  assign n48_I1 = n23_O; // @[Top.scala 136:12]
  assign n49_valid_up = n47_valid_down & n48_valid_down; // @[Top.scala 141:18]
  assign n49_I0_t0b = n47_O_t0b; // @[Top.scala 139:12]
  assign n49_I0_t1b = n47_O_t1b; // @[Top.scala 139:12]
  assign n49_I1_t0b = n48_O_t0b; // @[Top.scala 140:12]
  assign n49_I1_t1b = n48_O_t1b; // @[Top.scala 140:12]
  assign n50_clock = clock;
  assign n50_reset = reset;
  assign n50_valid_up = n49_valid_down; // @[Top.scala 144:18]
  assign n50_I_t0b_t0b = n49_O_t0b_t0b; // @[Top.scala 143:11]
  assign n50_I_t0b_t1b = n49_O_t0b_t1b; // @[Top.scala 143:11]
  assign n50_I_t1b_t0b = n49_O_t1b_t0b; // @[Top.scala 143:11]
  assign n50_I_t1b_t1b = n49_O_t1b_t1b; // @[Top.scala 143:11]
  assign n51_valid_up = n43_valid_down & n50_valid_down; // @[Top.scala 148:18]
  assign n51_I0 = n43_O[0]; // @[Top.scala 146:12]
  assign n51_I1_t0b_t0b = n50_O_t0b_t0b; // @[Top.scala 147:12]
  assign n51_I1_t0b_t1b = n50_O_t0b_t1b; // @[Top.scala 147:12]
  assign n51_I1_t1b_t0b = n50_O_t1b_t0b; // @[Top.scala 147:12]
  assign n51_I1_t1b_t1b = n50_O_t1b_t1b; // @[Top.scala 147:12]
  assign n52_valid_up = n51_valid_down; // @[Top.scala 151:18]
  assign n52_I_t0b = n51_O_t0b; // @[Top.scala 150:11]
  assign n52_I_t1b_t0b_t0b = n51_O_t1b_t0b_t0b; // @[Top.scala 150:11]
  assign n52_I_t1b_t0b_t1b = n51_O_t1b_t0b_t1b; // @[Top.scala 150:11]
  assign n52_I_t1b_t1b_t0b = n51_O_t1b_t1b_t0b; // @[Top.scala 150:11]
  assign n52_I_t1b_t1b_t1b = n51_O_t1b_t1b_t1b; // @[Top.scala 150:11]
  assign n54_valid_up = n53_valid_down & n52_valid_down; // @[Top.scala 155:18]
  assign n54_I0 = n53_O; // @[Top.scala 153:12]
  assign n54_I1_t0b = n52_O_t0b; // @[Top.scala 154:12]
  assign n54_I1_t1b = n52_O_t1b; // @[Top.scala 154:12]
endmodule
module MapS_1(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_1 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_1 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_1 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_1 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_1(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_1 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_6(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [2:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 3'h4; // @[InitialDelayCounter.scala 17:17]
  wire [2:0] _T_4 = value + 3'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 3'h4; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[2:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 3'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Module_2(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n59_valid_up; // @[Top.scala 163:21]
  wire  n59_valid_down; // @[Top.scala 163:21]
  wire [15:0] n59_I_t0b; // @[Top.scala 163:21]
  wire [15:0] n59_O; // @[Top.scala 163:21]
  wire  n92_clock; // @[Top.scala 166:21]
  wire  n92_reset; // @[Top.scala 166:21]
  wire  n92_valid_up; // @[Top.scala 166:21]
  wire  n92_valid_down; // @[Top.scala 166:21]
  wire [15:0] n92_I; // @[Top.scala 166:21]
  wire [15:0] n92_O; // @[Top.scala 166:21]
  wire  n80_clock; // @[Top.scala 169:21]
  wire  n80_reset; // @[Top.scala 169:21]
  wire  n80_valid_up; // @[Top.scala 169:21]
  wire  n80_valid_down; // @[Top.scala 169:21]
  wire [15:0] n80_I; // @[Top.scala 169:21]
  wire [15:0] n80_O; // @[Top.scala 169:21]
  wire  n60_valid_up; // @[Top.scala 172:21]
  wire  n60_valid_down; // @[Top.scala 172:21]
  wire [15:0] n60_I_t1b_t0b; // @[Top.scala 172:21]
  wire [15:0] n60_I_t1b_t1b; // @[Top.scala 172:21]
  wire [15:0] n60_O_t0b; // @[Top.scala 172:21]
  wire [15:0] n60_O_t1b; // @[Top.scala 172:21]
  wire  n61_valid_up; // @[Top.scala 175:21]
  wire  n61_valid_down; // @[Top.scala 175:21]
  wire [15:0] n61_I_t0b; // @[Top.scala 175:21]
  wire [15:0] n61_O; // @[Top.scala 175:21]
  wire  n62_valid_up; // @[Top.scala 178:21]
  wire  n62_valid_down; // @[Top.scala 178:21]
  wire [15:0] n62_I_t1b; // @[Top.scala 178:21]
  wire [15:0] n62_O; // @[Top.scala 178:21]
  wire  n63_valid_up; // @[Top.scala 181:21]
  wire  n63_valid_down; // @[Top.scala 181:21]
  wire [15:0] n63_I0; // @[Top.scala 181:21]
  wire [15:0] n63_I1; // @[Top.scala 181:21]
  wire [15:0] n63_O_t0b; // @[Top.scala 181:21]
  wire [15:0] n63_O_t1b; // @[Top.scala 181:21]
  wire  n64_valid_up; // @[Top.scala 185:21]
  wire  n64_valid_down; // @[Top.scala 185:21]
  wire [15:0] n64_I_t0b; // @[Top.scala 185:21]
  wire [15:0] n64_I_t1b; // @[Top.scala 185:21]
  wire [15:0] n64_O; // @[Top.scala 185:21]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n67_valid_up; // @[Top.scala 189:21]
  wire  n67_valid_down; // @[Top.scala 189:21]
  wire [15:0] n67_I0; // @[Top.scala 189:21]
  wire [15:0] n67_O_t0b; // @[Top.scala 189:21]
  wire  n68_valid_up; // @[Top.scala 193:21]
  wire  n68_valid_down; // @[Top.scala 193:21]
  wire [15:0] n68_I_t0b; // @[Top.scala 193:21]
  wire [15:0] n68_O; // @[Top.scala 193:21]
  wire  n69_valid_up; // @[Top.scala 196:21]
  wire  n69_valid_down; // @[Top.scala 196:21]
  wire [15:0] n69_I0; // @[Top.scala 196:21]
  wire [15:0] n69_I1; // @[Top.scala 196:21]
  wire [15:0] n69_O_t0b; // @[Top.scala 196:21]
  wire [15:0] n69_O_t1b; // @[Top.scala 196:21]
  wire  n70_valid_up; // @[Top.scala 200:21]
  wire  n70_valid_down; // @[Top.scala 200:21]
  wire [15:0] n70_I_t0b; // @[Top.scala 200:21]
  wire [15:0] n70_I_t1b; // @[Top.scala 200:21]
  wire [15:0] n70_O; // @[Top.scala 200:21]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n73_valid_up; // @[Top.scala 204:21]
  wire  n73_valid_down; // @[Top.scala 204:21]
  wire [15:0] n73_I0; // @[Top.scala 204:21]
  wire [15:0] n73_I1; // @[Top.scala 204:21]
  wire [15:0] n73_O_t0b; // @[Top.scala 204:21]
  wire [15:0] n73_O_t1b; // @[Top.scala 204:21]
  wire  n74_valid_up; // @[Top.scala 208:21]
  wire  n74_valid_down; // @[Top.scala 208:21]
  wire [15:0] n74_I_t0b; // @[Top.scala 208:21]
  wire [15:0] n74_I_t1b; // @[Top.scala 208:21]
  wire [15:0] n74_O; // @[Top.scala 208:21]
  wire  n75_valid_up; // @[Top.scala 211:21]
  wire  n75_valid_down; // @[Top.scala 211:21]
  wire [15:0] n75_I0; // @[Top.scala 211:21]
  wire [15:0] n75_I1; // @[Top.scala 211:21]
  wire [15:0] n75_O_t0b; // @[Top.scala 211:21]
  wire [15:0] n75_O_t1b; // @[Top.scala 211:21]
  wire  n76_valid_up; // @[Top.scala 215:21]
  wire  n76_valid_down; // @[Top.scala 215:21]
  wire  n76_I0; // @[Top.scala 215:21]
  wire [15:0] n76_I1_t0b; // @[Top.scala 215:21]
  wire [15:0] n76_I1_t1b; // @[Top.scala 215:21]
  wire  n76_O_t0b; // @[Top.scala 215:21]
  wire [15:0] n76_O_t1b_t0b; // @[Top.scala 215:21]
  wire [15:0] n76_O_t1b_t1b; // @[Top.scala 215:21]
  wire  n77_valid_up; // @[Top.scala 219:21]
  wire  n77_valid_down; // @[Top.scala 219:21]
  wire  n77_I_t0b; // @[Top.scala 219:21]
  wire [15:0] n77_I_t1b_t0b; // @[Top.scala 219:21]
  wire [15:0] n77_I_t1b_t1b; // @[Top.scala 219:21]
  wire [15:0] n77_O; // @[Top.scala 219:21]
  wire  n78_valid_up; // @[Top.scala 222:21]
  wire  n78_valid_down; // @[Top.scala 222:21]
  wire [15:0] n78_I0; // @[Top.scala 222:21]
  wire [15:0] n78_I1; // @[Top.scala 222:21]
  wire [15:0] n78_O_t0b; // @[Top.scala 222:21]
  wire [15:0] n78_O_t1b; // @[Top.scala 222:21]
  wire  n79_clock; // @[Top.scala 226:21]
  wire  n79_reset; // @[Top.scala 226:21]
  wire  n79_valid_up; // @[Top.scala 226:21]
  wire  n79_valid_down; // @[Top.scala 226:21]
  wire [15:0] n79_I_t0b; // @[Top.scala 226:21]
  wire [15:0] n79_I_t1b; // @[Top.scala 226:21]
  wire [15:0] n79_O; // @[Top.scala 226:21]
  wire  n81_valid_up; // @[Top.scala 229:21]
  wire  n81_valid_down; // @[Top.scala 229:21]
  wire [15:0] n81_I0; // @[Top.scala 229:21]
  wire [15:0] n81_I1; // @[Top.scala 229:21]
  wire [15:0] n81_O_t0b; // @[Top.scala 229:21]
  wire [15:0] n81_O_t1b; // @[Top.scala 229:21]
  wire  n82_valid_up; // @[Top.scala 233:21]
  wire  n82_valid_down; // @[Top.scala 233:21]
  wire [15:0] n82_I_t0b; // @[Top.scala 233:21]
  wire [15:0] n82_I_t1b; // @[Top.scala 233:21]
  wire [15:0] n82_O; // @[Top.scala 233:21]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n84_valid_up; // @[Top.scala 237:21]
  wire  n84_valid_down; // @[Top.scala 237:21]
  wire [15:0] n84_I0; // @[Top.scala 237:21]
  wire [15:0] n84_I1; // @[Top.scala 237:21]
  wire [15:0] n84_O_t0b; // @[Top.scala 237:21]
  wire [15:0] n84_O_t1b; // @[Top.scala 237:21]
  wire  n85_valid_up; // @[Top.scala 241:21]
  wire  n85_valid_down; // @[Top.scala 241:21]
  wire [15:0] n85_I_t0b; // @[Top.scala 241:21]
  wire [15:0] n85_I_t1b; // @[Top.scala 241:21]
  wire [15:0] n85_O; // @[Top.scala 241:21]
  wire  n86_valid_up; // @[Top.scala 244:21]
  wire  n86_valid_down; // @[Top.scala 244:21]
  wire [15:0] n86_I0; // @[Top.scala 244:21]
  wire [15:0] n86_I1; // @[Top.scala 244:21]
  wire [15:0] n86_O_t0b; // @[Top.scala 244:21]
  wire [15:0] n86_O_t1b; // @[Top.scala 244:21]
  wire  n87_valid_up; // @[Top.scala 248:21]
  wire  n87_valid_down; // @[Top.scala 248:21]
  wire [15:0] n87_I0; // @[Top.scala 248:21]
  wire [15:0] n87_I1; // @[Top.scala 248:21]
  wire [15:0] n87_O_t0b; // @[Top.scala 248:21]
  wire [15:0] n87_O_t1b; // @[Top.scala 248:21]
  wire  n88_valid_up; // @[Top.scala 252:21]
  wire  n88_valid_down; // @[Top.scala 252:21]
  wire [15:0] n88_I0_t0b; // @[Top.scala 252:21]
  wire [15:0] n88_I0_t1b; // @[Top.scala 252:21]
  wire [15:0] n88_I1_t0b; // @[Top.scala 252:21]
  wire [15:0] n88_I1_t1b; // @[Top.scala 252:21]
  wire [15:0] n88_O_t0b_t0b; // @[Top.scala 252:21]
  wire [15:0] n88_O_t0b_t1b; // @[Top.scala 252:21]
  wire [15:0] n88_O_t1b_t0b; // @[Top.scala 252:21]
  wire [15:0] n88_O_t1b_t1b; // @[Top.scala 252:21]
  wire  n89_clock; // @[Top.scala 256:21]
  wire  n89_reset; // @[Top.scala 256:21]
  wire  n89_valid_up; // @[Top.scala 256:21]
  wire  n89_valid_down; // @[Top.scala 256:21]
  wire [15:0] n89_I_t0b_t0b; // @[Top.scala 256:21]
  wire [15:0] n89_I_t0b_t1b; // @[Top.scala 256:21]
  wire [15:0] n89_I_t1b_t0b; // @[Top.scala 256:21]
  wire [15:0] n89_I_t1b_t1b; // @[Top.scala 256:21]
  wire [15:0] n89_O_t0b_t0b; // @[Top.scala 256:21]
  wire [15:0] n89_O_t0b_t1b; // @[Top.scala 256:21]
  wire [15:0] n89_O_t1b_t0b; // @[Top.scala 256:21]
  wire [15:0] n89_O_t1b_t1b; // @[Top.scala 256:21]
  wire  n90_valid_up; // @[Top.scala 259:21]
  wire  n90_valid_down; // @[Top.scala 259:21]
  wire  n90_I0; // @[Top.scala 259:21]
  wire [15:0] n90_I1_t0b_t0b; // @[Top.scala 259:21]
  wire [15:0] n90_I1_t0b_t1b; // @[Top.scala 259:21]
  wire [15:0] n90_I1_t1b_t0b; // @[Top.scala 259:21]
  wire [15:0] n90_I1_t1b_t1b; // @[Top.scala 259:21]
  wire  n90_O_t0b; // @[Top.scala 259:21]
  wire [15:0] n90_O_t1b_t0b_t0b; // @[Top.scala 259:21]
  wire [15:0] n90_O_t1b_t0b_t1b; // @[Top.scala 259:21]
  wire [15:0] n90_O_t1b_t1b_t0b; // @[Top.scala 259:21]
  wire [15:0] n90_O_t1b_t1b_t1b; // @[Top.scala 259:21]
  wire  n91_valid_up; // @[Top.scala 263:21]
  wire  n91_valid_down; // @[Top.scala 263:21]
  wire  n91_I_t0b; // @[Top.scala 263:21]
  wire [15:0] n91_I_t1b_t0b_t0b; // @[Top.scala 263:21]
  wire [15:0] n91_I_t1b_t0b_t1b; // @[Top.scala 263:21]
  wire [15:0] n91_I_t1b_t1b_t0b; // @[Top.scala 263:21]
  wire [15:0] n91_I_t1b_t1b_t1b; // @[Top.scala 263:21]
  wire [15:0] n91_O_t0b; // @[Top.scala 263:21]
  wire [15:0] n91_O_t1b; // @[Top.scala 263:21]
  wire  n93_valid_up; // @[Top.scala 266:21]
  wire  n93_valid_down; // @[Top.scala 266:21]
  wire [15:0] n93_I0; // @[Top.scala 266:21]
  wire [15:0] n93_I1_t0b; // @[Top.scala 266:21]
  wire [15:0] n93_I1_t1b; // @[Top.scala 266:21]
  wire [15:0] n93_O_t0b; // @[Top.scala 266:21]
  wire [15:0] n93_O_t1b_t0b; // @[Top.scala 266:21]
  wire [15:0] n93_O_t1b_t1b; // @[Top.scala 266:21]
  Fst n59 ( // @[Top.scala 163:21]
    .valid_up(n59_valid_up),
    .valid_down(n59_valid_down),
    .I_t0b(n59_I_t0b),
    .O(n59_O)
  );
  FIFO_1 n92 ( // @[Top.scala 166:21]
    .clock(n92_clock),
    .reset(n92_reset),
    .valid_up(n92_valid_up),
    .valid_down(n92_valid_down),
    .I(n92_I),
    .O(n92_O)
  );
  FIFO_1 n80 ( // @[Top.scala 169:21]
    .clock(n80_clock),
    .reset(n80_reset),
    .valid_up(n80_valid_up),
    .valid_down(n80_valid_down),
    .I(n80_I),
    .O(n80_O)
  );
  Snd n60 ( // @[Top.scala 172:21]
    .valid_up(n60_valid_up),
    .valid_down(n60_valid_down),
    .I_t1b_t0b(n60_I_t1b_t0b),
    .I_t1b_t1b(n60_I_t1b_t1b),
    .O_t0b(n60_O_t0b),
    .O_t1b(n60_O_t1b)
  );
  Fst_1 n61 ( // @[Top.scala 175:21]
    .valid_up(n61_valid_up),
    .valid_down(n61_valid_down),
    .I_t0b(n61_I_t0b),
    .O(n61_O)
  );
  Snd_1 n62 ( // @[Top.scala 178:21]
    .valid_up(n62_valid_up),
    .valid_down(n62_valid_down),
    .I_t1b(n62_I_t1b),
    .O(n62_O)
  );
  AtomTuple_1 n63 ( // @[Top.scala 181:21]
    .valid_up(n63_valid_up),
    .valid_down(n63_valid_down),
    .I0(n63_I0),
    .I1(n63_I1),
    .O_t0b(n63_O_t0b),
    .O_t1b(n63_O_t1b)
  );
  Add n64 ( // @[Top.scala 185:21]
    .valid_up(n64_valid_up),
    .valid_down(n64_valid_down),
    .I_t0b(n64_I_t0b),
    .I_t1b(n64_I_t1b),
    .O(n64_O)
  );
  InitialDelayCounter_6 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n67 ( // @[Top.scala 189:21]
    .valid_up(n67_valid_up),
    .valid_down(n67_valid_down),
    .I0(n67_I0),
    .O_t0b(n67_O_t0b)
  );
  RShift n68 ( // @[Top.scala 193:21]
    .valid_up(n68_valid_up),
    .valid_down(n68_valid_down),
    .I_t0b(n68_I_t0b),
    .O(n68_O)
  );
  AtomTuple_1 n69 ( // @[Top.scala 196:21]
    .valid_up(n69_valid_up),
    .valid_down(n69_valid_down),
    .I0(n69_I0),
    .I1(n69_I1),
    .O_t0b(n69_O_t0b),
    .O_t1b(n69_O_t1b)
  );
  Eq n70 ( // @[Top.scala 200:21]
    .valid_up(n70_valid_up),
    .valid_down(n70_valid_down),
    .I_t0b(n70_I_t0b),
    .I_t1b(n70_I_t1b),
    .O(n70_O)
  );
  InitialDelayCounter_6 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n73 ( // @[Top.scala 204:21]
    .valid_up(n73_valid_up),
    .valid_down(n73_valid_down),
    .I0(n73_I0),
    .I1(n73_I1),
    .O_t0b(n73_O_t0b),
    .O_t1b(n73_O_t1b)
  );
  Add n74 ( // @[Top.scala 208:21]
    .valid_up(n74_valid_up),
    .valid_down(n74_valid_down),
    .I_t0b(n74_I_t0b),
    .I_t1b(n74_I_t1b),
    .O(n74_O)
  );
  AtomTuple_1 n75 ( // @[Top.scala 211:21]
    .valid_up(n75_valid_up),
    .valid_down(n75_valid_down),
    .I0(n75_I0),
    .I1(n75_I1),
    .O_t0b(n75_O_t0b),
    .O_t1b(n75_O_t1b)
  );
  AtomTuple_9 n76 ( // @[Top.scala 215:21]
    .valid_up(n76_valid_up),
    .valid_down(n76_valid_down),
    .I0(n76_I0),
    .I1_t0b(n76_I1_t0b),
    .I1_t1b(n76_I1_t1b),
    .O_t0b(n76_O_t0b),
    .O_t1b_t0b(n76_O_t1b_t0b),
    .O_t1b_t1b(n76_O_t1b_t1b)
  );
  If n77 ( // @[Top.scala 219:21]
    .valid_up(n77_valid_up),
    .valid_down(n77_valid_down),
    .I_t0b(n77_I_t0b),
    .I_t1b_t0b(n77_I_t1b_t0b),
    .I_t1b_t1b(n77_I_t1b_t1b),
    .O(n77_O)
  );
  AtomTuple_1 n78 ( // @[Top.scala 222:21]
    .valid_up(n78_valid_up),
    .valid_down(n78_valid_down),
    .I0(n78_I0),
    .I1(n78_I1),
    .O_t0b(n78_O_t0b),
    .O_t1b(n78_O_t1b)
  );
  Mul n79 ( // @[Top.scala 226:21]
    .clock(n79_clock),
    .reset(n79_reset),
    .valid_up(n79_valid_up),
    .valid_down(n79_valid_down),
    .I_t0b(n79_I_t0b),
    .I_t1b(n79_I_t1b),
    .O(n79_O)
  );
  AtomTuple_1 n81 ( // @[Top.scala 229:21]
    .valid_up(n81_valid_up),
    .valid_down(n81_valid_down),
    .I0(n81_I0),
    .I1(n81_I1),
    .O_t0b(n81_O_t0b),
    .O_t1b(n81_O_t1b)
  );
  Lt n82 ( // @[Top.scala 233:21]
    .valid_up(n82_valid_up),
    .valid_down(n82_valid_down),
    .I_t0b(n82_I_t0b),
    .I_t1b(n82_I_t1b),
    .O(n82_O)
  );
  InitialDelayCounter_6 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n84 ( // @[Top.scala 237:21]
    .valid_up(n84_valid_up),
    .valid_down(n84_valid_down),
    .I0(n84_I0),
    .I1(n84_I1),
    .O_t0b(n84_O_t0b),
    .O_t1b(n84_O_t1b)
  );
  Sub n85 ( // @[Top.scala 241:21]
    .valid_up(n85_valid_up),
    .valid_down(n85_valid_down),
    .I_t0b(n85_I_t0b),
    .I_t1b(n85_I_t1b),
    .O(n85_O)
  );
  AtomTuple_1 n86 ( // @[Top.scala 244:21]
    .valid_up(n86_valid_up),
    .valid_down(n86_valid_down),
    .I0(n86_I0),
    .I1(n86_I1),
    .O_t0b(n86_O_t0b),
    .O_t1b(n86_O_t1b)
  );
  AtomTuple_1 n87 ( // @[Top.scala 248:21]
    .valid_up(n87_valid_up),
    .valid_down(n87_valid_down),
    .I0(n87_I0),
    .I1(n87_I1),
    .O_t0b(n87_O_t0b),
    .O_t1b(n87_O_t1b)
  );
  AtomTuple_15 n88 ( // @[Top.scala 252:21]
    .valid_up(n88_valid_up),
    .valid_down(n88_valid_down),
    .I0_t0b(n88_I0_t0b),
    .I0_t1b(n88_I0_t1b),
    .I1_t0b(n88_I1_t0b),
    .I1_t1b(n88_I1_t1b),
    .O_t0b_t0b(n88_O_t0b_t0b),
    .O_t0b_t1b(n88_O_t0b_t1b),
    .O_t1b_t0b(n88_O_t1b_t0b),
    .O_t1b_t1b(n88_O_t1b_t1b)
  );
  FIFO_3 n89 ( // @[Top.scala 256:21]
    .clock(n89_clock),
    .reset(n89_reset),
    .valid_up(n89_valid_up),
    .valid_down(n89_valid_down),
    .I_t0b_t0b(n89_I_t0b_t0b),
    .I_t0b_t1b(n89_I_t0b_t1b),
    .I_t1b_t0b(n89_I_t1b_t0b),
    .I_t1b_t1b(n89_I_t1b_t1b),
    .O_t0b_t0b(n89_O_t0b_t0b),
    .O_t0b_t1b(n89_O_t0b_t1b),
    .O_t1b_t0b(n89_O_t1b_t0b),
    .O_t1b_t1b(n89_O_t1b_t1b)
  );
  AtomTuple_16 n90 ( // @[Top.scala 259:21]
    .valid_up(n90_valid_up),
    .valid_down(n90_valid_down),
    .I0(n90_I0),
    .I1_t0b_t0b(n90_I1_t0b_t0b),
    .I1_t0b_t1b(n90_I1_t0b_t1b),
    .I1_t1b_t0b(n90_I1_t1b_t0b),
    .I1_t1b_t1b(n90_I1_t1b_t1b),
    .O_t0b(n90_O_t0b),
    .O_t1b_t0b_t0b(n90_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n90_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n90_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n90_O_t1b_t1b_t1b)
  );
  If_1 n91 ( // @[Top.scala 263:21]
    .valid_up(n91_valid_up),
    .valid_down(n91_valid_down),
    .I_t0b(n91_I_t0b),
    .I_t1b_t0b_t0b(n91_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n91_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n91_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n91_I_t1b_t1b_t1b),
    .O_t0b(n91_O_t0b),
    .O_t1b(n91_O_t1b)
  );
  AtomTuple_3 n93 ( // @[Top.scala 266:21]
    .valid_up(n93_valid_up),
    .valid_down(n93_valid_down),
    .I0(n93_I0),
    .I1_t0b(n93_I1_t0b),
    .I1_t1b(n93_I1_t1b),
    .O_t0b(n93_O_t0b),
    .O_t1b_t0b(n93_O_t1b_t0b),
    .O_t1b_t1b(n93_O_t1b_t1b)
  );
  assign valid_down = n93_valid_down; // @[Top.scala 271:16]
  assign O_t0b = n93_O_t0b; // @[Top.scala 270:7]
  assign O_t1b_t0b = n93_O_t1b_t0b; // @[Top.scala 270:7]
  assign O_t1b_t1b = n93_O_t1b_t1b; // @[Top.scala 270:7]
  assign n59_valid_up = valid_up; // @[Top.scala 165:18]
  assign n59_I_t0b = I_t0b; // @[Top.scala 164:11]
  assign n92_clock = clock;
  assign n92_reset = reset;
  assign n92_valid_up = n59_valid_down; // @[Top.scala 168:18]
  assign n92_I = n59_O; // @[Top.scala 167:11]
  assign n80_clock = clock;
  assign n80_reset = reset;
  assign n80_valid_up = n59_valid_down; // @[Top.scala 171:18]
  assign n80_I = n59_O; // @[Top.scala 170:11]
  assign n60_valid_up = valid_up; // @[Top.scala 174:18]
  assign n60_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 173:11]
  assign n60_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 173:11]
  assign n61_valid_up = n60_valid_down; // @[Top.scala 177:18]
  assign n61_I_t0b = n60_O_t0b; // @[Top.scala 176:11]
  assign n62_valid_up = n60_valid_down; // @[Top.scala 180:18]
  assign n62_I_t1b = n60_O_t1b; // @[Top.scala 179:11]
  assign n63_valid_up = n61_valid_down & n62_valid_down; // @[Top.scala 184:18]
  assign n63_I0 = n61_O; // @[Top.scala 182:12]
  assign n63_I1 = n62_O; // @[Top.scala 183:12]
  assign n64_valid_up = n63_valid_down; // @[Top.scala 187:18]
  assign n64_I_t0b = n63_O_t0b; // @[Top.scala 186:11]
  assign n64_I_t1b = n63_O_t1b; // @[Top.scala 186:11]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n67_valid_up = n64_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 192:18]
  assign n67_I0 = n64_O; // @[Top.scala 190:12]
  assign n68_valid_up = n67_valid_down; // @[Top.scala 195:18]
  assign n68_I_t0b = n67_O_t0b; // @[Top.scala 194:11]
  assign n69_valid_up = n68_valid_down & n61_valid_down; // @[Top.scala 199:18]
  assign n69_I0 = n68_O; // @[Top.scala 197:12]
  assign n69_I1 = n61_O; // @[Top.scala 198:12]
  assign n70_valid_up = n69_valid_down; // @[Top.scala 202:18]
  assign n70_I_t0b = n69_O_t0b; // @[Top.scala 201:11]
  assign n70_I_t1b = n69_O_t1b; // @[Top.scala 201:11]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n73_valid_up = n68_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 207:18]
  assign n73_I0 = n68_O; // @[Top.scala 205:12]
  assign n73_I1 = 16'h1; // @[Top.scala 206:12]
  assign n74_valid_up = n73_valid_down; // @[Top.scala 210:18]
  assign n74_I_t0b = n73_O_t0b; // @[Top.scala 209:11]
  assign n74_I_t1b = n73_O_t1b; // @[Top.scala 209:11]
  assign n75_valid_up = n74_valid_down & n68_valid_down; // @[Top.scala 214:18]
  assign n75_I0 = n74_O; // @[Top.scala 212:12]
  assign n75_I1 = n68_O; // @[Top.scala 213:12]
  assign n76_valid_up = n70_valid_down & n75_valid_down; // @[Top.scala 218:18]
  assign n76_I0 = n70_O[0]; // @[Top.scala 216:12]
  assign n76_I1_t0b = n75_O_t0b; // @[Top.scala 217:12]
  assign n76_I1_t1b = n75_O_t1b; // @[Top.scala 217:12]
  assign n77_valid_up = n76_valid_down; // @[Top.scala 221:18]
  assign n77_I_t0b = n76_O_t0b; // @[Top.scala 220:11]
  assign n77_I_t1b_t0b = n76_O_t1b_t0b; // @[Top.scala 220:11]
  assign n77_I_t1b_t1b = n76_O_t1b_t1b; // @[Top.scala 220:11]
  assign n78_valid_up = n77_valid_down; // @[Top.scala 225:18]
  assign n78_I0 = n77_O; // @[Top.scala 223:12]
  assign n78_I1 = n77_O; // @[Top.scala 224:12]
  assign n79_clock = clock;
  assign n79_reset = reset;
  assign n79_valid_up = n78_valid_down; // @[Top.scala 228:18]
  assign n79_I_t0b = n78_O_t0b; // @[Top.scala 227:11]
  assign n79_I_t1b = n78_O_t1b; // @[Top.scala 227:11]
  assign n81_valid_up = n80_valid_down & n79_valid_down; // @[Top.scala 232:18]
  assign n81_I0 = n80_O; // @[Top.scala 230:12]
  assign n81_I1 = n79_O; // @[Top.scala 231:12]
  assign n82_valid_up = n81_valid_down; // @[Top.scala 235:18]
  assign n82_I_t0b = n81_O_t0b; // @[Top.scala 234:11]
  assign n82_I_t1b = n81_O_t1b; // @[Top.scala 234:11]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n84_valid_up = n77_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 240:18]
  assign n84_I0 = n77_O; // @[Top.scala 238:12]
  assign n84_I1 = 16'h1; // @[Top.scala 239:12]
  assign n85_valid_up = n84_valid_down; // @[Top.scala 243:18]
  assign n85_I_t0b = n84_O_t0b; // @[Top.scala 242:11]
  assign n85_I_t1b = n84_O_t1b; // @[Top.scala 242:11]
  assign n86_valid_up = n61_valid_down & n85_valid_down; // @[Top.scala 247:18]
  assign n86_I0 = n61_O; // @[Top.scala 245:12]
  assign n86_I1 = n85_O; // @[Top.scala 246:12]
  assign n87_valid_up = n77_valid_down & n62_valid_down; // @[Top.scala 251:18]
  assign n87_I0 = n77_O; // @[Top.scala 249:12]
  assign n87_I1 = n62_O; // @[Top.scala 250:12]
  assign n88_valid_up = n86_valid_down & n87_valid_down; // @[Top.scala 255:18]
  assign n88_I0_t0b = n86_O_t0b; // @[Top.scala 253:12]
  assign n88_I0_t1b = n86_O_t1b; // @[Top.scala 253:12]
  assign n88_I1_t0b = n87_O_t0b; // @[Top.scala 254:12]
  assign n88_I1_t1b = n87_O_t1b; // @[Top.scala 254:12]
  assign n89_clock = clock;
  assign n89_reset = reset;
  assign n89_valid_up = n88_valid_down; // @[Top.scala 258:18]
  assign n89_I_t0b_t0b = n88_O_t0b_t0b; // @[Top.scala 257:11]
  assign n89_I_t0b_t1b = n88_O_t0b_t1b; // @[Top.scala 257:11]
  assign n89_I_t1b_t0b = n88_O_t1b_t0b; // @[Top.scala 257:11]
  assign n89_I_t1b_t1b = n88_O_t1b_t1b; // @[Top.scala 257:11]
  assign n90_valid_up = n82_valid_down & n89_valid_down; // @[Top.scala 262:18]
  assign n90_I0 = n82_O[0]; // @[Top.scala 260:12]
  assign n90_I1_t0b_t0b = n89_O_t0b_t0b; // @[Top.scala 261:12]
  assign n90_I1_t0b_t1b = n89_O_t0b_t1b; // @[Top.scala 261:12]
  assign n90_I1_t1b_t0b = n89_O_t1b_t0b; // @[Top.scala 261:12]
  assign n90_I1_t1b_t1b = n89_O_t1b_t1b; // @[Top.scala 261:12]
  assign n91_valid_up = n90_valid_down; // @[Top.scala 265:18]
  assign n91_I_t0b = n90_O_t0b; // @[Top.scala 264:11]
  assign n91_I_t1b_t0b_t0b = n90_O_t1b_t0b_t0b; // @[Top.scala 264:11]
  assign n91_I_t1b_t0b_t1b = n90_O_t1b_t0b_t1b; // @[Top.scala 264:11]
  assign n91_I_t1b_t1b_t0b = n90_O_t1b_t1b_t0b; // @[Top.scala 264:11]
  assign n91_I_t1b_t1b_t1b = n90_O_t1b_t1b_t1b; // @[Top.scala 264:11]
  assign n93_valid_up = n92_valid_down & n91_valid_down; // @[Top.scala 269:18]
  assign n93_I0 = n92_O; // @[Top.scala 267:12]
  assign n93_I1_t0b = n91_O_t0b; // @[Top.scala 268:12]
  assign n93_I1_t1b = n91_O_t1b; // @[Top.scala 268:12]
endmodule
module MapS_2(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_2 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_2 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_2 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_2 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_2(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_2 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_9(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [2:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 3'h7; // @[InitialDelayCounter.scala 17:17]
  wire [2:0] _T_4 = value + 3'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 3'h7; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[2:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 3'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Module_3(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n98_valid_up; // @[Top.scala 277:21]
  wire  n98_valid_down; // @[Top.scala 277:21]
  wire [15:0] n98_I_t0b; // @[Top.scala 277:21]
  wire [15:0] n98_O; // @[Top.scala 277:21]
  wire  n131_clock; // @[Top.scala 280:22]
  wire  n131_reset; // @[Top.scala 280:22]
  wire  n131_valid_up; // @[Top.scala 280:22]
  wire  n131_valid_down; // @[Top.scala 280:22]
  wire [15:0] n131_I; // @[Top.scala 280:22]
  wire [15:0] n131_O; // @[Top.scala 280:22]
  wire  n119_clock; // @[Top.scala 283:22]
  wire  n119_reset; // @[Top.scala 283:22]
  wire  n119_valid_up; // @[Top.scala 283:22]
  wire  n119_valid_down; // @[Top.scala 283:22]
  wire [15:0] n119_I; // @[Top.scala 283:22]
  wire [15:0] n119_O; // @[Top.scala 283:22]
  wire  n99_valid_up; // @[Top.scala 286:21]
  wire  n99_valid_down; // @[Top.scala 286:21]
  wire [15:0] n99_I_t1b_t0b; // @[Top.scala 286:21]
  wire [15:0] n99_I_t1b_t1b; // @[Top.scala 286:21]
  wire [15:0] n99_O_t0b; // @[Top.scala 286:21]
  wire [15:0] n99_O_t1b; // @[Top.scala 286:21]
  wire  n100_valid_up; // @[Top.scala 289:22]
  wire  n100_valid_down; // @[Top.scala 289:22]
  wire [15:0] n100_I_t0b; // @[Top.scala 289:22]
  wire [15:0] n100_O; // @[Top.scala 289:22]
  wire  n101_valid_up; // @[Top.scala 292:22]
  wire  n101_valid_down; // @[Top.scala 292:22]
  wire [15:0] n101_I_t1b; // @[Top.scala 292:22]
  wire [15:0] n101_O; // @[Top.scala 292:22]
  wire  n102_valid_up; // @[Top.scala 295:22]
  wire  n102_valid_down; // @[Top.scala 295:22]
  wire [15:0] n102_I0; // @[Top.scala 295:22]
  wire [15:0] n102_I1; // @[Top.scala 295:22]
  wire [15:0] n102_O_t0b; // @[Top.scala 295:22]
  wire [15:0] n102_O_t1b; // @[Top.scala 295:22]
  wire  n103_valid_up; // @[Top.scala 299:22]
  wire  n103_valid_down; // @[Top.scala 299:22]
  wire [15:0] n103_I_t0b; // @[Top.scala 299:22]
  wire [15:0] n103_I_t1b; // @[Top.scala 299:22]
  wire [15:0] n103_O; // @[Top.scala 299:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n106_valid_up; // @[Top.scala 303:22]
  wire  n106_valid_down; // @[Top.scala 303:22]
  wire [15:0] n106_I0; // @[Top.scala 303:22]
  wire [15:0] n106_O_t0b; // @[Top.scala 303:22]
  wire  n107_valid_up; // @[Top.scala 307:22]
  wire  n107_valid_down; // @[Top.scala 307:22]
  wire [15:0] n107_I_t0b; // @[Top.scala 307:22]
  wire [15:0] n107_O; // @[Top.scala 307:22]
  wire  n108_valid_up; // @[Top.scala 310:22]
  wire  n108_valid_down; // @[Top.scala 310:22]
  wire [15:0] n108_I0; // @[Top.scala 310:22]
  wire [15:0] n108_I1; // @[Top.scala 310:22]
  wire [15:0] n108_O_t0b; // @[Top.scala 310:22]
  wire [15:0] n108_O_t1b; // @[Top.scala 310:22]
  wire  n109_valid_up; // @[Top.scala 314:22]
  wire  n109_valid_down; // @[Top.scala 314:22]
  wire [15:0] n109_I_t0b; // @[Top.scala 314:22]
  wire [15:0] n109_I_t1b; // @[Top.scala 314:22]
  wire [15:0] n109_O; // @[Top.scala 314:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n112_valid_up; // @[Top.scala 318:22]
  wire  n112_valid_down; // @[Top.scala 318:22]
  wire [15:0] n112_I0; // @[Top.scala 318:22]
  wire [15:0] n112_I1; // @[Top.scala 318:22]
  wire [15:0] n112_O_t0b; // @[Top.scala 318:22]
  wire [15:0] n112_O_t1b; // @[Top.scala 318:22]
  wire  n113_valid_up; // @[Top.scala 322:22]
  wire  n113_valid_down; // @[Top.scala 322:22]
  wire [15:0] n113_I_t0b; // @[Top.scala 322:22]
  wire [15:0] n113_I_t1b; // @[Top.scala 322:22]
  wire [15:0] n113_O; // @[Top.scala 322:22]
  wire  n114_valid_up; // @[Top.scala 325:22]
  wire  n114_valid_down; // @[Top.scala 325:22]
  wire [15:0] n114_I0; // @[Top.scala 325:22]
  wire [15:0] n114_I1; // @[Top.scala 325:22]
  wire [15:0] n114_O_t0b; // @[Top.scala 325:22]
  wire [15:0] n114_O_t1b; // @[Top.scala 325:22]
  wire  n115_valid_up; // @[Top.scala 329:22]
  wire  n115_valid_down; // @[Top.scala 329:22]
  wire  n115_I0; // @[Top.scala 329:22]
  wire [15:0] n115_I1_t0b; // @[Top.scala 329:22]
  wire [15:0] n115_I1_t1b; // @[Top.scala 329:22]
  wire  n115_O_t0b; // @[Top.scala 329:22]
  wire [15:0] n115_O_t1b_t0b; // @[Top.scala 329:22]
  wire [15:0] n115_O_t1b_t1b; // @[Top.scala 329:22]
  wire  n116_valid_up; // @[Top.scala 333:22]
  wire  n116_valid_down; // @[Top.scala 333:22]
  wire  n116_I_t0b; // @[Top.scala 333:22]
  wire [15:0] n116_I_t1b_t0b; // @[Top.scala 333:22]
  wire [15:0] n116_I_t1b_t1b; // @[Top.scala 333:22]
  wire [15:0] n116_O; // @[Top.scala 333:22]
  wire  n117_valid_up; // @[Top.scala 336:22]
  wire  n117_valid_down; // @[Top.scala 336:22]
  wire [15:0] n117_I0; // @[Top.scala 336:22]
  wire [15:0] n117_I1; // @[Top.scala 336:22]
  wire [15:0] n117_O_t0b; // @[Top.scala 336:22]
  wire [15:0] n117_O_t1b; // @[Top.scala 336:22]
  wire  n118_clock; // @[Top.scala 340:22]
  wire  n118_reset; // @[Top.scala 340:22]
  wire  n118_valid_up; // @[Top.scala 340:22]
  wire  n118_valid_down; // @[Top.scala 340:22]
  wire [15:0] n118_I_t0b; // @[Top.scala 340:22]
  wire [15:0] n118_I_t1b; // @[Top.scala 340:22]
  wire [15:0] n118_O; // @[Top.scala 340:22]
  wire  n120_valid_up; // @[Top.scala 343:22]
  wire  n120_valid_down; // @[Top.scala 343:22]
  wire [15:0] n120_I0; // @[Top.scala 343:22]
  wire [15:0] n120_I1; // @[Top.scala 343:22]
  wire [15:0] n120_O_t0b; // @[Top.scala 343:22]
  wire [15:0] n120_O_t1b; // @[Top.scala 343:22]
  wire  n121_valid_up; // @[Top.scala 347:22]
  wire  n121_valid_down; // @[Top.scala 347:22]
  wire [15:0] n121_I_t0b; // @[Top.scala 347:22]
  wire [15:0] n121_I_t1b; // @[Top.scala 347:22]
  wire [15:0] n121_O; // @[Top.scala 347:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n123_valid_up; // @[Top.scala 351:22]
  wire  n123_valid_down; // @[Top.scala 351:22]
  wire [15:0] n123_I0; // @[Top.scala 351:22]
  wire [15:0] n123_I1; // @[Top.scala 351:22]
  wire [15:0] n123_O_t0b; // @[Top.scala 351:22]
  wire [15:0] n123_O_t1b; // @[Top.scala 351:22]
  wire  n124_valid_up; // @[Top.scala 355:22]
  wire  n124_valid_down; // @[Top.scala 355:22]
  wire [15:0] n124_I_t0b; // @[Top.scala 355:22]
  wire [15:0] n124_I_t1b; // @[Top.scala 355:22]
  wire [15:0] n124_O; // @[Top.scala 355:22]
  wire  n125_valid_up; // @[Top.scala 358:22]
  wire  n125_valid_down; // @[Top.scala 358:22]
  wire [15:0] n125_I0; // @[Top.scala 358:22]
  wire [15:0] n125_I1; // @[Top.scala 358:22]
  wire [15:0] n125_O_t0b; // @[Top.scala 358:22]
  wire [15:0] n125_O_t1b; // @[Top.scala 358:22]
  wire  n126_valid_up; // @[Top.scala 362:22]
  wire  n126_valid_down; // @[Top.scala 362:22]
  wire [15:0] n126_I0; // @[Top.scala 362:22]
  wire [15:0] n126_I1; // @[Top.scala 362:22]
  wire [15:0] n126_O_t0b; // @[Top.scala 362:22]
  wire [15:0] n126_O_t1b; // @[Top.scala 362:22]
  wire  n127_valid_up; // @[Top.scala 366:22]
  wire  n127_valid_down; // @[Top.scala 366:22]
  wire [15:0] n127_I0_t0b; // @[Top.scala 366:22]
  wire [15:0] n127_I0_t1b; // @[Top.scala 366:22]
  wire [15:0] n127_I1_t0b; // @[Top.scala 366:22]
  wire [15:0] n127_I1_t1b; // @[Top.scala 366:22]
  wire [15:0] n127_O_t0b_t0b; // @[Top.scala 366:22]
  wire [15:0] n127_O_t0b_t1b; // @[Top.scala 366:22]
  wire [15:0] n127_O_t1b_t0b; // @[Top.scala 366:22]
  wire [15:0] n127_O_t1b_t1b; // @[Top.scala 366:22]
  wire  n128_clock; // @[Top.scala 370:22]
  wire  n128_reset; // @[Top.scala 370:22]
  wire  n128_valid_up; // @[Top.scala 370:22]
  wire  n128_valid_down; // @[Top.scala 370:22]
  wire [15:0] n128_I_t0b_t0b; // @[Top.scala 370:22]
  wire [15:0] n128_I_t0b_t1b; // @[Top.scala 370:22]
  wire [15:0] n128_I_t1b_t0b; // @[Top.scala 370:22]
  wire [15:0] n128_I_t1b_t1b; // @[Top.scala 370:22]
  wire [15:0] n128_O_t0b_t0b; // @[Top.scala 370:22]
  wire [15:0] n128_O_t0b_t1b; // @[Top.scala 370:22]
  wire [15:0] n128_O_t1b_t0b; // @[Top.scala 370:22]
  wire [15:0] n128_O_t1b_t1b; // @[Top.scala 370:22]
  wire  n129_valid_up; // @[Top.scala 373:22]
  wire  n129_valid_down; // @[Top.scala 373:22]
  wire  n129_I0; // @[Top.scala 373:22]
  wire [15:0] n129_I1_t0b_t0b; // @[Top.scala 373:22]
  wire [15:0] n129_I1_t0b_t1b; // @[Top.scala 373:22]
  wire [15:0] n129_I1_t1b_t0b; // @[Top.scala 373:22]
  wire [15:0] n129_I1_t1b_t1b; // @[Top.scala 373:22]
  wire  n129_O_t0b; // @[Top.scala 373:22]
  wire [15:0] n129_O_t1b_t0b_t0b; // @[Top.scala 373:22]
  wire [15:0] n129_O_t1b_t0b_t1b; // @[Top.scala 373:22]
  wire [15:0] n129_O_t1b_t1b_t0b; // @[Top.scala 373:22]
  wire [15:0] n129_O_t1b_t1b_t1b; // @[Top.scala 373:22]
  wire  n130_valid_up; // @[Top.scala 377:22]
  wire  n130_valid_down; // @[Top.scala 377:22]
  wire  n130_I_t0b; // @[Top.scala 377:22]
  wire [15:0] n130_I_t1b_t0b_t0b; // @[Top.scala 377:22]
  wire [15:0] n130_I_t1b_t0b_t1b; // @[Top.scala 377:22]
  wire [15:0] n130_I_t1b_t1b_t0b; // @[Top.scala 377:22]
  wire [15:0] n130_I_t1b_t1b_t1b; // @[Top.scala 377:22]
  wire [15:0] n130_O_t0b; // @[Top.scala 377:22]
  wire [15:0] n130_O_t1b; // @[Top.scala 377:22]
  wire  n132_valid_up; // @[Top.scala 380:22]
  wire  n132_valid_down; // @[Top.scala 380:22]
  wire [15:0] n132_I0; // @[Top.scala 380:22]
  wire [15:0] n132_I1_t0b; // @[Top.scala 380:22]
  wire [15:0] n132_I1_t1b; // @[Top.scala 380:22]
  wire [15:0] n132_O_t0b; // @[Top.scala 380:22]
  wire [15:0] n132_O_t1b_t0b; // @[Top.scala 380:22]
  wire [15:0] n132_O_t1b_t1b; // @[Top.scala 380:22]
  Fst n98 ( // @[Top.scala 277:21]
    .valid_up(n98_valid_up),
    .valid_down(n98_valid_down),
    .I_t0b(n98_I_t0b),
    .O(n98_O)
  );
  FIFO_1 n131 ( // @[Top.scala 280:22]
    .clock(n131_clock),
    .reset(n131_reset),
    .valid_up(n131_valid_up),
    .valid_down(n131_valid_down),
    .I(n131_I),
    .O(n131_O)
  );
  FIFO_1 n119 ( // @[Top.scala 283:22]
    .clock(n119_clock),
    .reset(n119_reset),
    .valid_up(n119_valid_up),
    .valid_down(n119_valid_down),
    .I(n119_I),
    .O(n119_O)
  );
  Snd n99 ( // @[Top.scala 286:21]
    .valid_up(n99_valid_up),
    .valid_down(n99_valid_down),
    .I_t1b_t0b(n99_I_t1b_t0b),
    .I_t1b_t1b(n99_I_t1b_t1b),
    .O_t0b(n99_O_t0b),
    .O_t1b(n99_O_t1b)
  );
  Fst_1 n100 ( // @[Top.scala 289:22]
    .valid_up(n100_valid_up),
    .valid_down(n100_valid_down),
    .I_t0b(n100_I_t0b),
    .O(n100_O)
  );
  Snd_1 n101 ( // @[Top.scala 292:22]
    .valid_up(n101_valid_up),
    .valid_down(n101_valid_down),
    .I_t1b(n101_I_t1b),
    .O(n101_O)
  );
  AtomTuple_1 n102 ( // @[Top.scala 295:22]
    .valid_up(n102_valid_up),
    .valid_down(n102_valid_down),
    .I0(n102_I0),
    .I1(n102_I1),
    .O_t0b(n102_O_t0b),
    .O_t1b(n102_O_t1b)
  );
  Add n103 ( // @[Top.scala 299:22]
    .valid_up(n103_valid_up),
    .valid_down(n103_valid_down),
    .I_t0b(n103_I_t0b),
    .I_t1b(n103_I_t1b),
    .O(n103_O)
  );
  InitialDelayCounter_9 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n106 ( // @[Top.scala 303:22]
    .valid_up(n106_valid_up),
    .valid_down(n106_valid_down),
    .I0(n106_I0),
    .O_t0b(n106_O_t0b)
  );
  RShift n107 ( // @[Top.scala 307:22]
    .valid_up(n107_valid_up),
    .valid_down(n107_valid_down),
    .I_t0b(n107_I_t0b),
    .O(n107_O)
  );
  AtomTuple_1 n108 ( // @[Top.scala 310:22]
    .valid_up(n108_valid_up),
    .valid_down(n108_valid_down),
    .I0(n108_I0),
    .I1(n108_I1),
    .O_t0b(n108_O_t0b),
    .O_t1b(n108_O_t1b)
  );
  Eq n109 ( // @[Top.scala 314:22]
    .valid_up(n109_valid_up),
    .valid_down(n109_valid_down),
    .I_t0b(n109_I_t0b),
    .I_t1b(n109_I_t1b),
    .O(n109_O)
  );
  InitialDelayCounter_9 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n112 ( // @[Top.scala 318:22]
    .valid_up(n112_valid_up),
    .valid_down(n112_valid_down),
    .I0(n112_I0),
    .I1(n112_I1),
    .O_t0b(n112_O_t0b),
    .O_t1b(n112_O_t1b)
  );
  Add n113 ( // @[Top.scala 322:22]
    .valid_up(n113_valid_up),
    .valid_down(n113_valid_down),
    .I_t0b(n113_I_t0b),
    .I_t1b(n113_I_t1b),
    .O(n113_O)
  );
  AtomTuple_1 n114 ( // @[Top.scala 325:22]
    .valid_up(n114_valid_up),
    .valid_down(n114_valid_down),
    .I0(n114_I0),
    .I1(n114_I1),
    .O_t0b(n114_O_t0b),
    .O_t1b(n114_O_t1b)
  );
  AtomTuple_9 n115 ( // @[Top.scala 329:22]
    .valid_up(n115_valid_up),
    .valid_down(n115_valid_down),
    .I0(n115_I0),
    .I1_t0b(n115_I1_t0b),
    .I1_t1b(n115_I1_t1b),
    .O_t0b(n115_O_t0b),
    .O_t1b_t0b(n115_O_t1b_t0b),
    .O_t1b_t1b(n115_O_t1b_t1b)
  );
  If n116 ( // @[Top.scala 333:22]
    .valid_up(n116_valid_up),
    .valid_down(n116_valid_down),
    .I_t0b(n116_I_t0b),
    .I_t1b_t0b(n116_I_t1b_t0b),
    .I_t1b_t1b(n116_I_t1b_t1b),
    .O(n116_O)
  );
  AtomTuple_1 n117 ( // @[Top.scala 336:22]
    .valid_up(n117_valid_up),
    .valid_down(n117_valid_down),
    .I0(n117_I0),
    .I1(n117_I1),
    .O_t0b(n117_O_t0b),
    .O_t1b(n117_O_t1b)
  );
  Mul n118 ( // @[Top.scala 340:22]
    .clock(n118_clock),
    .reset(n118_reset),
    .valid_up(n118_valid_up),
    .valid_down(n118_valid_down),
    .I_t0b(n118_I_t0b),
    .I_t1b(n118_I_t1b),
    .O(n118_O)
  );
  AtomTuple_1 n120 ( // @[Top.scala 343:22]
    .valid_up(n120_valid_up),
    .valid_down(n120_valid_down),
    .I0(n120_I0),
    .I1(n120_I1),
    .O_t0b(n120_O_t0b),
    .O_t1b(n120_O_t1b)
  );
  Lt n121 ( // @[Top.scala 347:22]
    .valid_up(n121_valid_up),
    .valid_down(n121_valid_down),
    .I_t0b(n121_I_t0b),
    .I_t1b(n121_I_t1b),
    .O(n121_O)
  );
  InitialDelayCounter_9 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n123 ( // @[Top.scala 351:22]
    .valid_up(n123_valid_up),
    .valid_down(n123_valid_down),
    .I0(n123_I0),
    .I1(n123_I1),
    .O_t0b(n123_O_t0b),
    .O_t1b(n123_O_t1b)
  );
  Sub n124 ( // @[Top.scala 355:22]
    .valid_up(n124_valid_up),
    .valid_down(n124_valid_down),
    .I_t0b(n124_I_t0b),
    .I_t1b(n124_I_t1b),
    .O(n124_O)
  );
  AtomTuple_1 n125 ( // @[Top.scala 358:22]
    .valid_up(n125_valid_up),
    .valid_down(n125_valid_down),
    .I0(n125_I0),
    .I1(n125_I1),
    .O_t0b(n125_O_t0b),
    .O_t1b(n125_O_t1b)
  );
  AtomTuple_1 n126 ( // @[Top.scala 362:22]
    .valid_up(n126_valid_up),
    .valid_down(n126_valid_down),
    .I0(n126_I0),
    .I1(n126_I1),
    .O_t0b(n126_O_t0b),
    .O_t1b(n126_O_t1b)
  );
  AtomTuple_15 n127 ( // @[Top.scala 366:22]
    .valid_up(n127_valid_up),
    .valid_down(n127_valid_down),
    .I0_t0b(n127_I0_t0b),
    .I0_t1b(n127_I0_t1b),
    .I1_t0b(n127_I1_t0b),
    .I1_t1b(n127_I1_t1b),
    .O_t0b_t0b(n127_O_t0b_t0b),
    .O_t0b_t1b(n127_O_t0b_t1b),
    .O_t1b_t0b(n127_O_t1b_t0b),
    .O_t1b_t1b(n127_O_t1b_t1b)
  );
  FIFO_3 n128 ( // @[Top.scala 370:22]
    .clock(n128_clock),
    .reset(n128_reset),
    .valid_up(n128_valid_up),
    .valid_down(n128_valid_down),
    .I_t0b_t0b(n128_I_t0b_t0b),
    .I_t0b_t1b(n128_I_t0b_t1b),
    .I_t1b_t0b(n128_I_t1b_t0b),
    .I_t1b_t1b(n128_I_t1b_t1b),
    .O_t0b_t0b(n128_O_t0b_t0b),
    .O_t0b_t1b(n128_O_t0b_t1b),
    .O_t1b_t0b(n128_O_t1b_t0b),
    .O_t1b_t1b(n128_O_t1b_t1b)
  );
  AtomTuple_16 n129 ( // @[Top.scala 373:22]
    .valid_up(n129_valid_up),
    .valid_down(n129_valid_down),
    .I0(n129_I0),
    .I1_t0b_t0b(n129_I1_t0b_t0b),
    .I1_t0b_t1b(n129_I1_t0b_t1b),
    .I1_t1b_t0b(n129_I1_t1b_t0b),
    .I1_t1b_t1b(n129_I1_t1b_t1b),
    .O_t0b(n129_O_t0b),
    .O_t1b_t0b_t0b(n129_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n129_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n129_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n129_O_t1b_t1b_t1b)
  );
  If_1 n130 ( // @[Top.scala 377:22]
    .valid_up(n130_valid_up),
    .valid_down(n130_valid_down),
    .I_t0b(n130_I_t0b),
    .I_t1b_t0b_t0b(n130_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n130_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n130_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n130_I_t1b_t1b_t1b),
    .O_t0b(n130_O_t0b),
    .O_t1b(n130_O_t1b)
  );
  AtomTuple_3 n132 ( // @[Top.scala 380:22]
    .valid_up(n132_valid_up),
    .valid_down(n132_valid_down),
    .I0(n132_I0),
    .I1_t0b(n132_I1_t0b),
    .I1_t1b(n132_I1_t1b),
    .O_t0b(n132_O_t0b),
    .O_t1b_t0b(n132_O_t1b_t0b),
    .O_t1b_t1b(n132_O_t1b_t1b)
  );
  assign valid_down = n132_valid_down; // @[Top.scala 385:16]
  assign O_t0b = n132_O_t0b; // @[Top.scala 384:7]
  assign O_t1b_t0b = n132_O_t1b_t0b; // @[Top.scala 384:7]
  assign O_t1b_t1b = n132_O_t1b_t1b; // @[Top.scala 384:7]
  assign n98_valid_up = valid_up; // @[Top.scala 279:18]
  assign n98_I_t0b = I_t0b; // @[Top.scala 278:11]
  assign n131_clock = clock;
  assign n131_reset = reset;
  assign n131_valid_up = n98_valid_down; // @[Top.scala 282:19]
  assign n131_I = n98_O; // @[Top.scala 281:12]
  assign n119_clock = clock;
  assign n119_reset = reset;
  assign n119_valid_up = n98_valid_down; // @[Top.scala 285:19]
  assign n119_I = n98_O; // @[Top.scala 284:12]
  assign n99_valid_up = valid_up; // @[Top.scala 288:18]
  assign n99_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 287:11]
  assign n99_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 287:11]
  assign n100_valid_up = n99_valid_down; // @[Top.scala 291:19]
  assign n100_I_t0b = n99_O_t0b; // @[Top.scala 290:12]
  assign n101_valid_up = n99_valid_down; // @[Top.scala 294:19]
  assign n101_I_t1b = n99_O_t1b; // @[Top.scala 293:12]
  assign n102_valid_up = n100_valid_down & n101_valid_down; // @[Top.scala 298:19]
  assign n102_I0 = n100_O; // @[Top.scala 296:13]
  assign n102_I1 = n101_O; // @[Top.scala 297:13]
  assign n103_valid_up = n102_valid_down; // @[Top.scala 301:19]
  assign n103_I_t0b = n102_O_t0b; // @[Top.scala 300:12]
  assign n103_I_t1b = n102_O_t1b; // @[Top.scala 300:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n106_valid_up = n103_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 306:19]
  assign n106_I0 = n103_O; // @[Top.scala 304:13]
  assign n107_valid_up = n106_valid_down; // @[Top.scala 309:19]
  assign n107_I_t0b = n106_O_t0b; // @[Top.scala 308:12]
  assign n108_valid_up = n107_valid_down & n100_valid_down; // @[Top.scala 313:19]
  assign n108_I0 = n107_O; // @[Top.scala 311:13]
  assign n108_I1 = n100_O; // @[Top.scala 312:13]
  assign n109_valid_up = n108_valid_down; // @[Top.scala 316:19]
  assign n109_I_t0b = n108_O_t0b; // @[Top.scala 315:12]
  assign n109_I_t1b = n108_O_t1b; // @[Top.scala 315:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n112_valid_up = n107_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 321:19]
  assign n112_I0 = n107_O; // @[Top.scala 319:13]
  assign n112_I1 = 16'h1; // @[Top.scala 320:13]
  assign n113_valid_up = n112_valid_down; // @[Top.scala 324:19]
  assign n113_I_t0b = n112_O_t0b; // @[Top.scala 323:12]
  assign n113_I_t1b = n112_O_t1b; // @[Top.scala 323:12]
  assign n114_valid_up = n113_valid_down & n107_valid_down; // @[Top.scala 328:19]
  assign n114_I0 = n113_O; // @[Top.scala 326:13]
  assign n114_I1 = n107_O; // @[Top.scala 327:13]
  assign n115_valid_up = n109_valid_down & n114_valid_down; // @[Top.scala 332:19]
  assign n115_I0 = n109_O[0]; // @[Top.scala 330:13]
  assign n115_I1_t0b = n114_O_t0b; // @[Top.scala 331:13]
  assign n115_I1_t1b = n114_O_t1b; // @[Top.scala 331:13]
  assign n116_valid_up = n115_valid_down; // @[Top.scala 335:19]
  assign n116_I_t0b = n115_O_t0b; // @[Top.scala 334:12]
  assign n116_I_t1b_t0b = n115_O_t1b_t0b; // @[Top.scala 334:12]
  assign n116_I_t1b_t1b = n115_O_t1b_t1b; // @[Top.scala 334:12]
  assign n117_valid_up = n116_valid_down; // @[Top.scala 339:19]
  assign n117_I0 = n116_O; // @[Top.scala 337:13]
  assign n117_I1 = n116_O; // @[Top.scala 338:13]
  assign n118_clock = clock;
  assign n118_reset = reset;
  assign n118_valid_up = n117_valid_down; // @[Top.scala 342:19]
  assign n118_I_t0b = n117_O_t0b; // @[Top.scala 341:12]
  assign n118_I_t1b = n117_O_t1b; // @[Top.scala 341:12]
  assign n120_valid_up = n119_valid_down & n118_valid_down; // @[Top.scala 346:19]
  assign n120_I0 = n119_O; // @[Top.scala 344:13]
  assign n120_I1 = n118_O; // @[Top.scala 345:13]
  assign n121_valid_up = n120_valid_down; // @[Top.scala 349:19]
  assign n121_I_t0b = n120_O_t0b; // @[Top.scala 348:12]
  assign n121_I_t1b = n120_O_t1b; // @[Top.scala 348:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n123_valid_up = n116_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 354:19]
  assign n123_I0 = n116_O; // @[Top.scala 352:13]
  assign n123_I1 = 16'h1; // @[Top.scala 353:13]
  assign n124_valid_up = n123_valid_down; // @[Top.scala 357:19]
  assign n124_I_t0b = n123_O_t0b; // @[Top.scala 356:12]
  assign n124_I_t1b = n123_O_t1b; // @[Top.scala 356:12]
  assign n125_valid_up = n100_valid_down & n124_valid_down; // @[Top.scala 361:19]
  assign n125_I0 = n100_O; // @[Top.scala 359:13]
  assign n125_I1 = n124_O; // @[Top.scala 360:13]
  assign n126_valid_up = n116_valid_down & n101_valid_down; // @[Top.scala 365:19]
  assign n126_I0 = n116_O; // @[Top.scala 363:13]
  assign n126_I1 = n101_O; // @[Top.scala 364:13]
  assign n127_valid_up = n125_valid_down & n126_valid_down; // @[Top.scala 369:19]
  assign n127_I0_t0b = n125_O_t0b; // @[Top.scala 367:13]
  assign n127_I0_t1b = n125_O_t1b; // @[Top.scala 367:13]
  assign n127_I1_t0b = n126_O_t0b; // @[Top.scala 368:13]
  assign n127_I1_t1b = n126_O_t1b; // @[Top.scala 368:13]
  assign n128_clock = clock;
  assign n128_reset = reset;
  assign n128_valid_up = n127_valid_down; // @[Top.scala 372:19]
  assign n128_I_t0b_t0b = n127_O_t0b_t0b; // @[Top.scala 371:12]
  assign n128_I_t0b_t1b = n127_O_t0b_t1b; // @[Top.scala 371:12]
  assign n128_I_t1b_t0b = n127_O_t1b_t0b; // @[Top.scala 371:12]
  assign n128_I_t1b_t1b = n127_O_t1b_t1b; // @[Top.scala 371:12]
  assign n129_valid_up = n121_valid_down & n128_valid_down; // @[Top.scala 376:19]
  assign n129_I0 = n121_O[0]; // @[Top.scala 374:13]
  assign n129_I1_t0b_t0b = n128_O_t0b_t0b; // @[Top.scala 375:13]
  assign n129_I1_t0b_t1b = n128_O_t0b_t1b; // @[Top.scala 375:13]
  assign n129_I1_t1b_t0b = n128_O_t1b_t0b; // @[Top.scala 375:13]
  assign n129_I1_t1b_t1b = n128_O_t1b_t1b; // @[Top.scala 375:13]
  assign n130_valid_up = n129_valid_down; // @[Top.scala 379:19]
  assign n130_I_t0b = n129_O_t0b; // @[Top.scala 378:12]
  assign n130_I_t1b_t0b_t0b = n129_O_t1b_t0b_t0b; // @[Top.scala 378:12]
  assign n130_I_t1b_t0b_t1b = n129_O_t1b_t0b_t1b; // @[Top.scala 378:12]
  assign n130_I_t1b_t1b_t0b = n129_O_t1b_t1b_t0b; // @[Top.scala 378:12]
  assign n130_I_t1b_t1b_t1b = n129_O_t1b_t1b_t1b; // @[Top.scala 378:12]
  assign n132_valid_up = n131_valid_down & n130_valid_down; // @[Top.scala 383:19]
  assign n132_I0 = n131_O; // @[Top.scala 381:13]
  assign n132_I1_t0b = n130_O_t0b; // @[Top.scala 382:13]
  assign n132_I1_t1b = n130_O_t1b; // @[Top.scala 382:13]
endmodule
module MapS_3(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_3 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_3 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_3 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_3 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_3(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_3 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_12(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [3:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 4'ha; // @[InitialDelayCounter.scala 17:17]
  wire [3:0] _T_4 = value + 4'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 4'ha; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[3:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 4'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Module_4(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n137_valid_up; // @[Top.scala 391:22]
  wire  n137_valid_down; // @[Top.scala 391:22]
  wire [15:0] n137_I_t0b; // @[Top.scala 391:22]
  wire [15:0] n137_O; // @[Top.scala 391:22]
  wire  n170_clock; // @[Top.scala 394:22]
  wire  n170_reset; // @[Top.scala 394:22]
  wire  n170_valid_up; // @[Top.scala 394:22]
  wire  n170_valid_down; // @[Top.scala 394:22]
  wire [15:0] n170_I; // @[Top.scala 394:22]
  wire [15:0] n170_O; // @[Top.scala 394:22]
  wire  n158_clock; // @[Top.scala 397:22]
  wire  n158_reset; // @[Top.scala 397:22]
  wire  n158_valid_up; // @[Top.scala 397:22]
  wire  n158_valid_down; // @[Top.scala 397:22]
  wire [15:0] n158_I; // @[Top.scala 397:22]
  wire [15:0] n158_O; // @[Top.scala 397:22]
  wire  n138_valid_up; // @[Top.scala 400:22]
  wire  n138_valid_down; // @[Top.scala 400:22]
  wire [15:0] n138_I_t1b_t0b; // @[Top.scala 400:22]
  wire [15:0] n138_I_t1b_t1b; // @[Top.scala 400:22]
  wire [15:0] n138_O_t0b; // @[Top.scala 400:22]
  wire [15:0] n138_O_t1b; // @[Top.scala 400:22]
  wire  n139_valid_up; // @[Top.scala 403:22]
  wire  n139_valid_down; // @[Top.scala 403:22]
  wire [15:0] n139_I_t0b; // @[Top.scala 403:22]
  wire [15:0] n139_O; // @[Top.scala 403:22]
  wire  n140_valid_up; // @[Top.scala 406:22]
  wire  n140_valid_down; // @[Top.scala 406:22]
  wire [15:0] n140_I_t1b; // @[Top.scala 406:22]
  wire [15:0] n140_O; // @[Top.scala 406:22]
  wire  n141_valid_up; // @[Top.scala 409:22]
  wire  n141_valid_down; // @[Top.scala 409:22]
  wire [15:0] n141_I0; // @[Top.scala 409:22]
  wire [15:0] n141_I1; // @[Top.scala 409:22]
  wire [15:0] n141_O_t0b; // @[Top.scala 409:22]
  wire [15:0] n141_O_t1b; // @[Top.scala 409:22]
  wire  n142_valid_up; // @[Top.scala 413:22]
  wire  n142_valid_down; // @[Top.scala 413:22]
  wire [15:0] n142_I_t0b; // @[Top.scala 413:22]
  wire [15:0] n142_I_t1b; // @[Top.scala 413:22]
  wire [15:0] n142_O; // @[Top.scala 413:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n145_valid_up; // @[Top.scala 417:22]
  wire  n145_valid_down; // @[Top.scala 417:22]
  wire [15:0] n145_I0; // @[Top.scala 417:22]
  wire [15:0] n145_O_t0b; // @[Top.scala 417:22]
  wire  n146_valid_up; // @[Top.scala 421:22]
  wire  n146_valid_down; // @[Top.scala 421:22]
  wire [15:0] n146_I_t0b; // @[Top.scala 421:22]
  wire [15:0] n146_O; // @[Top.scala 421:22]
  wire  n147_valid_up; // @[Top.scala 424:22]
  wire  n147_valid_down; // @[Top.scala 424:22]
  wire [15:0] n147_I0; // @[Top.scala 424:22]
  wire [15:0] n147_I1; // @[Top.scala 424:22]
  wire [15:0] n147_O_t0b; // @[Top.scala 424:22]
  wire [15:0] n147_O_t1b; // @[Top.scala 424:22]
  wire  n148_valid_up; // @[Top.scala 428:22]
  wire  n148_valid_down; // @[Top.scala 428:22]
  wire [15:0] n148_I_t0b; // @[Top.scala 428:22]
  wire [15:0] n148_I_t1b; // @[Top.scala 428:22]
  wire [15:0] n148_O; // @[Top.scala 428:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n151_valid_up; // @[Top.scala 432:22]
  wire  n151_valid_down; // @[Top.scala 432:22]
  wire [15:0] n151_I0; // @[Top.scala 432:22]
  wire [15:0] n151_I1; // @[Top.scala 432:22]
  wire [15:0] n151_O_t0b; // @[Top.scala 432:22]
  wire [15:0] n151_O_t1b; // @[Top.scala 432:22]
  wire  n152_valid_up; // @[Top.scala 436:22]
  wire  n152_valid_down; // @[Top.scala 436:22]
  wire [15:0] n152_I_t0b; // @[Top.scala 436:22]
  wire [15:0] n152_I_t1b; // @[Top.scala 436:22]
  wire [15:0] n152_O; // @[Top.scala 436:22]
  wire  n153_valid_up; // @[Top.scala 439:22]
  wire  n153_valid_down; // @[Top.scala 439:22]
  wire [15:0] n153_I0; // @[Top.scala 439:22]
  wire [15:0] n153_I1; // @[Top.scala 439:22]
  wire [15:0] n153_O_t0b; // @[Top.scala 439:22]
  wire [15:0] n153_O_t1b; // @[Top.scala 439:22]
  wire  n154_valid_up; // @[Top.scala 443:22]
  wire  n154_valid_down; // @[Top.scala 443:22]
  wire  n154_I0; // @[Top.scala 443:22]
  wire [15:0] n154_I1_t0b; // @[Top.scala 443:22]
  wire [15:0] n154_I1_t1b; // @[Top.scala 443:22]
  wire  n154_O_t0b; // @[Top.scala 443:22]
  wire [15:0] n154_O_t1b_t0b; // @[Top.scala 443:22]
  wire [15:0] n154_O_t1b_t1b; // @[Top.scala 443:22]
  wire  n155_valid_up; // @[Top.scala 447:22]
  wire  n155_valid_down; // @[Top.scala 447:22]
  wire  n155_I_t0b; // @[Top.scala 447:22]
  wire [15:0] n155_I_t1b_t0b; // @[Top.scala 447:22]
  wire [15:0] n155_I_t1b_t1b; // @[Top.scala 447:22]
  wire [15:0] n155_O; // @[Top.scala 447:22]
  wire  n156_valid_up; // @[Top.scala 450:22]
  wire  n156_valid_down; // @[Top.scala 450:22]
  wire [15:0] n156_I0; // @[Top.scala 450:22]
  wire [15:0] n156_I1; // @[Top.scala 450:22]
  wire [15:0] n156_O_t0b; // @[Top.scala 450:22]
  wire [15:0] n156_O_t1b; // @[Top.scala 450:22]
  wire  n157_clock; // @[Top.scala 454:22]
  wire  n157_reset; // @[Top.scala 454:22]
  wire  n157_valid_up; // @[Top.scala 454:22]
  wire  n157_valid_down; // @[Top.scala 454:22]
  wire [15:0] n157_I_t0b; // @[Top.scala 454:22]
  wire [15:0] n157_I_t1b; // @[Top.scala 454:22]
  wire [15:0] n157_O; // @[Top.scala 454:22]
  wire  n159_valid_up; // @[Top.scala 457:22]
  wire  n159_valid_down; // @[Top.scala 457:22]
  wire [15:0] n159_I0; // @[Top.scala 457:22]
  wire [15:0] n159_I1; // @[Top.scala 457:22]
  wire [15:0] n159_O_t0b; // @[Top.scala 457:22]
  wire [15:0] n159_O_t1b; // @[Top.scala 457:22]
  wire  n160_valid_up; // @[Top.scala 461:22]
  wire  n160_valid_down; // @[Top.scala 461:22]
  wire [15:0] n160_I_t0b; // @[Top.scala 461:22]
  wire [15:0] n160_I_t1b; // @[Top.scala 461:22]
  wire [15:0] n160_O; // @[Top.scala 461:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n162_valid_up; // @[Top.scala 465:22]
  wire  n162_valid_down; // @[Top.scala 465:22]
  wire [15:0] n162_I0; // @[Top.scala 465:22]
  wire [15:0] n162_I1; // @[Top.scala 465:22]
  wire [15:0] n162_O_t0b; // @[Top.scala 465:22]
  wire [15:0] n162_O_t1b; // @[Top.scala 465:22]
  wire  n163_valid_up; // @[Top.scala 469:22]
  wire  n163_valid_down; // @[Top.scala 469:22]
  wire [15:0] n163_I_t0b; // @[Top.scala 469:22]
  wire [15:0] n163_I_t1b; // @[Top.scala 469:22]
  wire [15:0] n163_O; // @[Top.scala 469:22]
  wire  n164_valid_up; // @[Top.scala 472:22]
  wire  n164_valid_down; // @[Top.scala 472:22]
  wire [15:0] n164_I0; // @[Top.scala 472:22]
  wire [15:0] n164_I1; // @[Top.scala 472:22]
  wire [15:0] n164_O_t0b; // @[Top.scala 472:22]
  wire [15:0] n164_O_t1b; // @[Top.scala 472:22]
  wire  n165_valid_up; // @[Top.scala 476:22]
  wire  n165_valid_down; // @[Top.scala 476:22]
  wire [15:0] n165_I0; // @[Top.scala 476:22]
  wire [15:0] n165_I1; // @[Top.scala 476:22]
  wire [15:0] n165_O_t0b; // @[Top.scala 476:22]
  wire [15:0] n165_O_t1b; // @[Top.scala 476:22]
  wire  n166_valid_up; // @[Top.scala 480:22]
  wire  n166_valid_down; // @[Top.scala 480:22]
  wire [15:0] n166_I0_t0b; // @[Top.scala 480:22]
  wire [15:0] n166_I0_t1b; // @[Top.scala 480:22]
  wire [15:0] n166_I1_t0b; // @[Top.scala 480:22]
  wire [15:0] n166_I1_t1b; // @[Top.scala 480:22]
  wire [15:0] n166_O_t0b_t0b; // @[Top.scala 480:22]
  wire [15:0] n166_O_t0b_t1b; // @[Top.scala 480:22]
  wire [15:0] n166_O_t1b_t0b; // @[Top.scala 480:22]
  wire [15:0] n166_O_t1b_t1b; // @[Top.scala 480:22]
  wire  n167_clock; // @[Top.scala 484:22]
  wire  n167_reset; // @[Top.scala 484:22]
  wire  n167_valid_up; // @[Top.scala 484:22]
  wire  n167_valid_down; // @[Top.scala 484:22]
  wire [15:0] n167_I_t0b_t0b; // @[Top.scala 484:22]
  wire [15:0] n167_I_t0b_t1b; // @[Top.scala 484:22]
  wire [15:0] n167_I_t1b_t0b; // @[Top.scala 484:22]
  wire [15:0] n167_I_t1b_t1b; // @[Top.scala 484:22]
  wire [15:0] n167_O_t0b_t0b; // @[Top.scala 484:22]
  wire [15:0] n167_O_t0b_t1b; // @[Top.scala 484:22]
  wire [15:0] n167_O_t1b_t0b; // @[Top.scala 484:22]
  wire [15:0] n167_O_t1b_t1b; // @[Top.scala 484:22]
  wire  n168_valid_up; // @[Top.scala 487:22]
  wire  n168_valid_down; // @[Top.scala 487:22]
  wire  n168_I0; // @[Top.scala 487:22]
  wire [15:0] n168_I1_t0b_t0b; // @[Top.scala 487:22]
  wire [15:0] n168_I1_t0b_t1b; // @[Top.scala 487:22]
  wire [15:0] n168_I1_t1b_t0b; // @[Top.scala 487:22]
  wire [15:0] n168_I1_t1b_t1b; // @[Top.scala 487:22]
  wire  n168_O_t0b; // @[Top.scala 487:22]
  wire [15:0] n168_O_t1b_t0b_t0b; // @[Top.scala 487:22]
  wire [15:0] n168_O_t1b_t0b_t1b; // @[Top.scala 487:22]
  wire [15:0] n168_O_t1b_t1b_t0b; // @[Top.scala 487:22]
  wire [15:0] n168_O_t1b_t1b_t1b; // @[Top.scala 487:22]
  wire  n169_valid_up; // @[Top.scala 491:22]
  wire  n169_valid_down; // @[Top.scala 491:22]
  wire  n169_I_t0b; // @[Top.scala 491:22]
  wire [15:0] n169_I_t1b_t0b_t0b; // @[Top.scala 491:22]
  wire [15:0] n169_I_t1b_t0b_t1b; // @[Top.scala 491:22]
  wire [15:0] n169_I_t1b_t1b_t0b; // @[Top.scala 491:22]
  wire [15:0] n169_I_t1b_t1b_t1b; // @[Top.scala 491:22]
  wire [15:0] n169_O_t0b; // @[Top.scala 491:22]
  wire [15:0] n169_O_t1b; // @[Top.scala 491:22]
  wire  n171_valid_up; // @[Top.scala 494:22]
  wire  n171_valid_down; // @[Top.scala 494:22]
  wire [15:0] n171_I0; // @[Top.scala 494:22]
  wire [15:0] n171_I1_t0b; // @[Top.scala 494:22]
  wire [15:0] n171_I1_t1b; // @[Top.scala 494:22]
  wire [15:0] n171_O_t0b; // @[Top.scala 494:22]
  wire [15:0] n171_O_t1b_t0b; // @[Top.scala 494:22]
  wire [15:0] n171_O_t1b_t1b; // @[Top.scala 494:22]
  Fst n137 ( // @[Top.scala 391:22]
    .valid_up(n137_valid_up),
    .valid_down(n137_valid_down),
    .I_t0b(n137_I_t0b),
    .O(n137_O)
  );
  FIFO_1 n170 ( // @[Top.scala 394:22]
    .clock(n170_clock),
    .reset(n170_reset),
    .valid_up(n170_valid_up),
    .valid_down(n170_valid_down),
    .I(n170_I),
    .O(n170_O)
  );
  FIFO_1 n158 ( // @[Top.scala 397:22]
    .clock(n158_clock),
    .reset(n158_reset),
    .valid_up(n158_valid_up),
    .valid_down(n158_valid_down),
    .I(n158_I),
    .O(n158_O)
  );
  Snd n138 ( // @[Top.scala 400:22]
    .valid_up(n138_valid_up),
    .valid_down(n138_valid_down),
    .I_t1b_t0b(n138_I_t1b_t0b),
    .I_t1b_t1b(n138_I_t1b_t1b),
    .O_t0b(n138_O_t0b),
    .O_t1b(n138_O_t1b)
  );
  Fst_1 n139 ( // @[Top.scala 403:22]
    .valid_up(n139_valid_up),
    .valid_down(n139_valid_down),
    .I_t0b(n139_I_t0b),
    .O(n139_O)
  );
  Snd_1 n140 ( // @[Top.scala 406:22]
    .valid_up(n140_valid_up),
    .valid_down(n140_valid_down),
    .I_t1b(n140_I_t1b),
    .O(n140_O)
  );
  AtomTuple_1 n141 ( // @[Top.scala 409:22]
    .valid_up(n141_valid_up),
    .valid_down(n141_valid_down),
    .I0(n141_I0),
    .I1(n141_I1),
    .O_t0b(n141_O_t0b),
    .O_t1b(n141_O_t1b)
  );
  Add n142 ( // @[Top.scala 413:22]
    .valid_up(n142_valid_up),
    .valid_down(n142_valid_down),
    .I_t0b(n142_I_t0b),
    .I_t1b(n142_I_t1b),
    .O(n142_O)
  );
  InitialDelayCounter_12 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n145 ( // @[Top.scala 417:22]
    .valid_up(n145_valid_up),
    .valid_down(n145_valid_down),
    .I0(n145_I0),
    .O_t0b(n145_O_t0b)
  );
  RShift n146 ( // @[Top.scala 421:22]
    .valid_up(n146_valid_up),
    .valid_down(n146_valid_down),
    .I_t0b(n146_I_t0b),
    .O(n146_O)
  );
  AtomTuple_1 n147 ( // @[Top.scala 424:22]
    .valid_up(n147_valid_up),
    .valid_down(n147_valid_down),
    .I0(n147_I0),
    .I1(n147_I1),
    .O_t0b(n147_O_t0b),
    .O_t1b(n147_O_t1b)
  );
  Eq n148 ( // @[Top.scala 428:22]
    .valid_up(n148_valid_up),
    .valid_down(n148_valid_down),
    .I_t0b(n148_I_t0b),
    .I_t1b(n148_I_t1b),
    .O(n148_O)
  );
  InitialDelayCounter_12 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n151 ( // @[Top.scala 432:22]
    .valid_up(n151_valid_up),
    .valid_down(n151_valid_down),
    .I0(n151_I0),
    .I1(n151_I1),
    .O_t0b(n151_O_t0b),
    .O_t1b(n151_O_t1b)
  );
  Add n152 ( // @[Top.scala 436:22]
    .valid_up(n152_valid_up),
    .valid_down(n152_valid_down),
    .I_t0b(n152_I_t0b),
    .I_t1b(n152_I_t1b),
    .O(n152_O)
  );
  AtomTuple_1 n153 ( // @[Top.scala 439:22]
    .valid_up(n153_valid_up),
    .valid_down(n153_valid_down),
    .I0(n153_I0),
    .I1(n153_I1),
    .O_t0b(n153_O_t0b),
    .O_t1b(n153_O_t1b)
  );
  AtomTuple_9 n154 ( // @[Top.scala 443:22]
    .valid_up(n154_valid_up),
    .valid_down(n154_valid_down),
    .I0(n154_I0),
    .I1_t0b(n154_I1_t0b),
    .I1_t1b(n154_I1_t1b),
    .O_t0b(n154_O_t0b),
    .O_t1b_t0b(n154_O_t1b_t0b),
    .O_t1b_t1b(n154_O_t1b_t1b)
  );
  If n155 ( // @[Top.scala 447:22]
    .valid_up(n155_valid_up),
    .valid_down(n155_valid_down),
    .I_t0b(n155_I_t0b),
    .I_t1b_t0b(n155_I_t1b_t0b),
    .I_t1b_t1b(n155_I_t1b_t1b),
    .O(n155_O)
  );
  AtomTuple_1 n156 ( // @[Top.scala 450:22]
    .valid_up(n156_valid_up),
    .valid_down(n156_valid_down),
    .I0(n156_I0),
    .I1(n156_I1),
    .O_t0b(n156_O_t0b),
    .O_t1b(n156_O_t1b)
  );
  Mul n157 ( // @[Top.scala 454:22]
    .clock(n157_clock),
    .reset(n157_reset),
    .valid_up(n157_valid_up),
    .valid_down(n157_valid_down),
    .I_t0b(n157_I_t0b),
    .I_t1b(n157_I_t1b),
    .O(n157_O)
  );
  AtomTuple_1 n159 ( // @[Top.scala 457:22]
    .valid_up(n159_valid_up),
    .valid_down(n159_valid_down),
    .I0(n159_I0),
    .I1(n159_I1),
    .O_t0b(n159_O_t0b),
    .O_t1b(n159_O_t1b)
  );
  Lt n160 ( // @[Top.scala 461:22]
    .valid_up(n160_valid_up),
    .valid_down(n160_valid_down),
    .I_t0b(n160_I_t0b),
    .I_t1b(n160_I_t1b),
    .O(n160_O)
  );
  InitialDelayCounter_12 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n162 ( // @[Top.scala 465:22]
    .valid_up(n162_valid_up),
    .valid_down(n162_valid_down),
    .I0(n162_I0),
    .I1(n162_I1),
    .O_t0b(n162_O_t0b),
    .O_t1b(n162_O_t1b)
  );
  Sub n163 ( // @[Top.scala 469:22]
    .valid_up(n163_valid_up),
    .valid_down(n163_valid_down),
    .I_t0b(n163_I_t0b),
    .I_t1b(n163_I_t1b),
    .O(n163_O)
  );
  AtomTuple_1 n164 ( // @[Top.scala 472:22]
    .valid_up(n164_valid_up),
    .valid_down(n164_valid_down),
    .I0(n164_I0),
    .I1(n164_I1),
    .O_t0b(n164_O_t0b),
    .O_t1b(n164_O_t1b)
  );
  AtomTuple_1 n165 ( // @[Top.scala 476:22]
    .valid_up(n165_valid_up),
    .valid_down(n165_valid_down),
    .I0(n165_I0),
    .I1(n165_I1),
    .O_t0b(n165_O_t0b),
    .O_t1b(n165_O_t1b)
  );
  AtomTuple_15 n166 ( // @[Top.scala 480:22]
    .valid_up(n166_valid_up),
    .valid_down(n166_valid_down),
    .I0_t0b(n166_I0_t0b),
    .I0_t1b(n166_I0_t1b),
    .I1_t0b(n166_I1_t0b),
    .I1_t1b(n166_I1_t1b),
    .O_t0b_t0b(n166_O_t0b_t0b),
    .O_t0b_t1b(n166_O_t0b_t1b),
    .O_t1b_t0b(n166_O_t1b_t0b),
    .O_t1b_t1b(n166_O_t1b_t1b)
  );
  FIFO_3 n167 ( // @[Top.scala 484:22]
    .clock(n167_clock),
    .reset(n167_reset),
    .valid_up(n167_valid_up),
    .valid_down(n167_valid_down),
    .I_t0b_t0b(n167_I_t0b_t0b),
    .I_t0b_t1b(n167_I_t0b_t1b),
    .I_t1b_t0b(n167_I_t1b_t0b),
    .I_t1b_t1b(n167_I_t1b_t1b),
    .O_t0b_t0b(n167_O_t0b_t0b),
    .O_t0b_t1b(n167_O_t0b_t1b),
    .O_t1b_t0b(n167_O_t1b_t0b),
    .O_t1b_t1b(n167_O_t1b_t1b)
  );
  AtomTuple_16 n168 ( // @[Top.scala 487:22]
    .valid_up(n168_valid_up),
    .valid_down(n168_valid_down),
    .I0(n168_I0),
    .I1_t0b_t0b(n168_I1_t0b_t0b),
    .I1_t0b_t1b(n168_I1_t0b_t1b),
    .I1_t1b_t0b(n168_I1_t1b_t0b),
    .I1_t1b_t1b(n168_I1_t1b_t1b),
    .O_t0b(n168_O_t0b),
    .O_t1b_t0b_t0b(n168_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n168_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n168_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n168_O_t1b_t1b_t1b)
  );
  If_1 n169 ( // @[Top.scala 491:22]
    .valid_up(n169_valid_up),
    .valid_down(n169_valid_down),
    .I_t0b(n169_I_t0b),
    .I_t1b_t0b_t0b(n169_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n169_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n169_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n169_I_t1b_t1b_t1b),
    .O_t0b(n169_O_t0b),
    .O_t1b(n169_O_t1b)
  );
  AtomTuple_3 n171 ( // @[Top.scala 494:22]
    .valid_up(n171_valid_up),
    .valid_down(n171_valid_down),
    .I0(n171_I0),
    .I1_t0b(n171_I1_t0b),
    .I1_t1b(n171_I1_t1b),
    .O_t0b(n171_O_t0b),
    .O_t1b_t0b(n171_O_t1b_t0b),
    .O_t1b_t1b(n171_O_t1b_t1b)
  );
  assign valid_down = n171_valid_down; // @[Top.scala 499:16]
  assign O_t0b = n171_O_t0b; // @[Top.scala 498:7]
  assign O_t1b_t0b = n171_O_t1b_t0b; // @[Top.scala 498:7]
  assign O_t1b_t1b = n171_O_t1b_t1b; // @[Top.scala 498:7]
  assign n137_valid_up = valid_up; // @[Top.scala 393:19]
  assign n137_I_t0b = I_t0b; // @[Top.scala 392:12]
  assign n170_clock = clock;
  assign n170_reset = reset;
  assign n170_valid_up = n137_valid_down; // @[Top.scala 396:19]
  assign n170_I = n137_O; // @[Top.scala 395:12]
  assign n158_clock = clock;
  assign n158_reset = reset;
  assign n158_valid_up = n137_valid_down; // @[Top.scala 399:19]
  assign n158_I = n137_O; // @[Top.scala 398:12]
  assign n138_valid_up = valid_up; // @[Top.scala 402:19]
  assign n138_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 401:12]
  assign n138_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 401:12]
  assign n139_valid_up = n138_valid_down; // @[Top.scala 405:19]
  assign n139_I_t0b = n138_O_t0b; // @[Top.scala 404:12]
  assign n140_valid_up = n138_valid_down; // @[Top.scala 408:19]
  assign n140_I_t1b = n138_O_t1b; // @[Top.scala 407:12]
  assign n141_valid_up = n139_valid_down & n140_valid_down; // @[Top.scala 412:19]
  assign n141_I0 = n139_O; // @[Top.scala 410:13]
  assign n141_I1 = n140_O; // @[Top.scala 411:13]
  assign n142_valid_up = n141_valid_down; // @[Top.scala 415:19]
  assign n142_I_t0b = n141_O_t0b; // @[Top.scala 414:12]
  assign n142_I_t1b = n141_O_t1b; // @[Top.scala 414:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n145_valid_up = n142_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 420:19]
  assign n145_I0 = n142_O; // @[Top.scala 418:13]
  assign n146_valid_up = n145_valid_down; // @[Top.scala 423:19]
  assign n146_I_t0b = n145_O_t0b; // @[Top.scala 422:12]
  assign n147_valid_up = n146_valid_down & n139_valid_down; // @[Top.scala 427:19]
  assign n147_I0 = n146_O; // @[Top.scala 425:13]
  assign n147_I1 = n139_O; // @[Top.scala 426:13]
  assign n148_valid_up = n147_valid_down; // @[Top.scala 430:19]
  assign n148_I_t0b = n147_O_t0b; // @[Top.scala 429:12]
  assign n148_I_t1b = n147_O_t1b; // @[Top.scala 429:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n151_valid_up = n146_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 435:19]
  assign n151_I0 = n146_O; // @[Top.scala 433:13]
  assign n151_I1 = 16'h1; // @[Top.scala 434:13]
  assign n152_valid_up = n151_valid_down; // @[Top.scala 438:19]
  assign n152_I_t0b = n151_O_t0b; // @[Top.scala 437:12]
  assign n152_I_t1b = n151_O_t1b; // @[Top.scala 437:12]
  assign n153_valid_up = n152_valid_down & n146_valid_down; // @[Top.scala 442:19]
  assign n153_I0 = n152_O; // @[Top.scala 440:13]
  assign n153_I1 = n146_O; // @[Top.scala 441:13]
  assign n154_valid_up = n148_valid_down & n153_valid_down; // @[Top.scala 446:19]
  assign n154_I0 = n148_O[0]; // @[Top.scala 444:13]
  assign n154_I1_t0b = n153_O_t0b; // @[Top.scala 445:13]
  assign n154_I1_t1b = n153_O_t1b; // @[Top.scala 445:13]
  assign n155_valid_up = n154_valid_down; // @[Top.scala 449:19]
  assign n155_I_t0b = n154_O_t0b; // @[Top.scala 448:12]
  assign n155_I_t1b_t0b = n154_O_t1b_t0b; // @[Top.scala 448:12]
  assign n155_I_t1b_t1b = n154_O_t1b_t1b; // @[Top.scala 448:12]
  assign n156_valid_up = n155_valid_down; // @[Top.scala 453:19]
  assign n156_I0 = n155_O; // @[Top.scala 451:13]
  assign n156_I1 = n155_O; // @[Top.scala 452:13]
  assign n157_clock = clock;
  assign n157_reset = reset;
  assign n157_valid_up = n156_valid_down; // @[Top.scala 456:19]
  assign n157_I_t0b = n156_O_t0b; // @[Top.scala 455:12]
  assign n157_I_t1b = n156_O_t1b; // @[Top.scala 455:12]
  assign n159_valid_up = n158_valid_down & n157_valid_down; // @[Top.scala 460:19]
  assign n159_I0 = n158_O; // @[Top.scala 458:13]
  assign n159_I1 = n157_O; // @[Top.scala 459:13]
  assign n160_valid_up = n159_valid_down; // @[Top.scala 463:19]
  assign n160_I_t0b = n159_O_t0b; // @[Top.scala 462:12]
  assign n160_I_t1b = n159_O_t1b; // @[Top.scala 462:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n162_valid_up = n155_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 468:19]
  assign n162_I0 = n155_O; // @[Top.scala 466:13]
  assign n162_I1 = 16'h1; // @[Top.scala 467:13]
  assign n163_valid_up = n162_valid_down; // @[Top.scala 471:19]
  assign n163_I_t0b = n162_O_t0b; // @[Top.scala 470:12]
  assign n163_I_t1b = n162_O_t1b; // @[Top.scala 470:12]
  assign n164_valid_up = n139_valid_down & n163_valid_down; // @[Top.scala 475:19]
  assign n164_I0 = n139_O; // @[Top.scala 473:13]
  assign n164_I1 = n163_O; // @[Top.scala 474:13]
  assign n165_valid_up = n155_valid_down & n140_valid_down; // @[Top.scala 479:19]
  assign n165_I0 = n155_O; // @[Top.scala 477:13]
  assign n165_I1 = n140_O; // @[Top.scala 478:13]
  assign n166_valid_up = n164_valid_down & n165_valid_down; // @[Top.scala 483:19]
  assign n166_I0_t0b = n164_O_t0b; // @[Top.scala 481:13]
  assign n166_I0_t1b = n164_O_t1b; // @[Top.scala 481:13]
  assign n166_I1_t0b = n165_O_t0b; // @[Top.scala 482:13]
  assign n166_I1_t1b = n165_O_t1b; // @[Top.scala 482:13]
  assign n167_clock = clock;
  assign n167_reset = reset;
  assign n167_valid_up = n166_valid_down; // @[Top.scala 486:19]
  assign n167_I_t0b_t0b = n166_O_t0b_t0b; // @[Top.scala 485:12]
  assign n167_I_t0b_t1b = n166_O_t0b_t1b; // @[Top.scala 485:12]
  assign n167_I_t1b_t0b = n166_O_t1b_t0b; // @[Top.scala 485:12]
  assign n167_I_t1b_t1b = n166_O_t1b_t1b; // @[Top.scala 485:12]
  assign n168_valid_up = n160_valid_down & n167_valid_down; // @[Top.scala 490:19]
  assign n168_I0 = n160_O[0]; // @[Top.scala 488:13]
  assign n168_I1_t0b_t0b = n167_O_t0b_t0b; // @[Top.scala 489:13]
  assign n168_I1_t0b_t1b = n167_O_t0b_t1b; // @[Top.scala 489:13]
  assign n168_I1_t1b_t0b = n167_O_t1b_t0b; // @[Top.scala 489:13]
  assign n168_I1_t1b_t1b = n167_O_t1b_t1b; // @[Top.scala 489:13]
  assign n169_valid_up = n168_valid_down; // @[Top.scala 493:19]
  assign n169_I_t0b = n168_O_t0b; // @[Top.scala 492:12]
  assign n169_I_t1b_t0b_t0b = n168_O_t1b_t0b_t0b; // @[Top.scala 492:12]
  assign n169_I_t1b_t0b_t1b = n168_O_t1b_t0b_t1b; // @[Top.scala 492:12]
  assign n169_I_t1b_t1b_t0b = n168_O_t1b_t1b_t0b; // @[Top.scala 492:12]
  assign n169_I_t1b_t1b_t1b = n168_O_t1b_t1b_t1b; // @[Top.scala 492:12]
  assign n171_valid_up = n170_valid_down & n169_valid_down; // @[Top.scala 497:19]
  assign n171_I0 = n170_O; // @[Top.scala 495:13]
  assign n171_I1_t0b = n169_O_t0b; // @[Top.scala 496:13]
  assign n171_I1_t1b = n169_O_t1b; // @[Top.scala 496:13]
endmodule
module MapS_4(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_4 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_4 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_4 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_4 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_4(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_4 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_15(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [3:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 4'hd; // @[InitialDelayCounter.scala 17:17]
  wire [3:0] _T_4 = value + 4'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 4'hd; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[3:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 4'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Module_5(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n176_valid_up; // @[Top.scala 505:22]
  wire  n176_valid_down; // @[Top.scala 505:22]
  wire [15:0] n176_I_t0b; // @[Top.scala 505:22]
  wire [15:0] n176_O; // @[Top.scala 505:22]
  wire  n209_clock; // @[Top.scala 508:22]
  wire  n209_reset; // @[Top.scala 508:22]
  wire  n209_valid_up; // @[Top.scala 508:22]
  wire  n209_valid_down; // @[Top.scala 508:22]
  wire [15:0] n209_I; // @[Top.scala 508:22]
  wire [15:0] n209_O; // @[Top.scala 508:22]
  wire  n197_clock; // @[Top.scala 511:22]
  wire  n197_reset; // @[Top.scala 511:22]
  wire  n197_valid_up; // @[Top.scala 511:22]
  wire  n197_valid_down; // @[Top.scala 511:22]
  wire [15:0] n197_I; // @[Top.scala 511:22]
  wire [15:0] n197_O; // @[Top.scala 511:22]
  wire  n177_valid_up; // @[Top.scala 514:22]
  wire  n177_valid_down; // @[Top.scala 514:22]
  wire [15:0] n177_I_t1b_t0b; // @[Top.scala 514:22]
  wire [15:0] n177_I_t1b_t1b; // @[Top.scala 514:22]
  wire [15:0] n177_O_t0b; // @[Top.scala 514:22]
  wire [15:0] n177_O_t1b; // @[Top.scala 514:22]
  wire  n178_valid_up; // @[Top.scala 517:22]
  wire  n178_valid_down; // @[Top.scala 517:22]
  wire [15:0] n178_I_t0b; // @[Top.scala 517:22]
  wire [15:0] n178_O; // @[Top.scala 517:22]
  wire  n179_valid_up; // @[Top.scala 520:22]
  wire  n179_valid_down; // @[Top.scala 520:22]
  wire [15:0] n179_I_t1b; // @[Top.scala 520:22]
  wire [15:0] n179_O; // @[Top.scala 520:22]
  wire  n180_valid_up; // @[Top.scala 523:22]
  wire  n180_valid_down; // @[Top.scala 523:22]
  wire [15:0] n180_I0; // @[Top.scala 523:22]
  wire [15:0] n180_I1; // @[Top.scala 523:22]
  wire [15:0] n180_O_t0b; // @[Top.scala 523:22]
  wire [15:0] n180_O_t1b; // @[Top.scala 523:22]
  wire  n181_valid_up; // @[Top.scala 527:22]
  wire  n181_valid_down; // @[Top.scala 527:22]
  wire [15:0] n181_I_t0b; // @[Top.scala 527:22]
  wire [15:0] n181_I_t1b; // @[Top.scala 527:22]
  wire [15:0] n181_O; // @[Top.scala 527:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n184_valid_up; // @[Top.scala 531:22]
  wire  n184_valid_down; // @[Top.scala 531:22]
  wire [15:0] n184_I0; // @[Top.scala 531:22]
  wire [15:0] n184_O_t0b; // @[Top.scala 531:22]
  wire  n185_valid_up; // @[Top.scala 535:22]
  wire  n185_valid_down; // @[Top.scala 535:22]
  wire [15:0] n185_I_t0b; // @[Top.scala 535:22]
  wire [15:0] n185_O; // @[Top.scala 535:22]
  wire  n186_valid_up; // @[Top.scala 538:22]
  wire  n186_valid_down; // @[Top.scala 538:22]
  wire [15:0] n186_I0; // @[Top.scala 538:22]
  wire [15:0] n186_I1; // @[Top.scala 538:22]
  wire [15:0] n186_O_t0b; // @[Top.scala 538:22]
  wire [15:0] n186_O_t1b; // @[Top.scala 538:22]
  wire  n187_valid_up; // @[Top.scala 542:22]
  wire  n187_valid_down; // @[Top.scala 542:22]
  wire [15:0] n187_I_t0b; // @[Top.scala 542:22]
  wire [15:0] n187_I_t1b; // @[Top.scala 542:22]
  wire [15:0] n187_O; // @[Top.scala 542:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n190_valid_up; // @[Top.scala 546:22]
  wire  n190_valid_down; // @[Top.scala 546:22]
  wire [15:0] n190_I0; // @[Top.scala 546:22]
  wire [15:0] n190_I1; // @[Top.scala 546:22]
  wire [15:0] n190_O_t0b; // @[Top.scala 546:22]
  wire [15:0] n190_O_t1b; // @[Top.scala 546:22]
  wire  n191_valid_up; // @[Top.scala 550:22]
  wire  n191_valid_down; // @[Top.scala 550:22]
  wire [15:0] n191_I_t0b; // @[Top.scala 550:22]
  wire [15:0] n191_I_t1b; // @[Top.scala 550:22]
  wire [15:0] n191_O; // @[Top.scala 550:22]
  wire  n192_valid_up; // @[Top.scala 553:22]
  wire  n192_valid_down; // @[Top.scala 553:22]
  wire [15:0] n192_I0; // @[Top.scala 553:22]
  wire [15:0] n192_I1; // @[Top.scala 553:22]
  wire [15:0] n192_O_t0b; // @[Top.scala 553:22]
  wire [15:0] n192_O_t1b; // @[Top.scala 553:22]
  wire  n193_valid_up; // @[Top.scala 557:22]
  wire  n193_valid_down; // @[Top.scala 557:22]
  wire  n193_I0; // @[Top.scala 557:22]
  wire [15:0] n193_I1_t0b; // @[Top.scala 557:22]
  wire [15:0] n193_I1_t1b; // @[Top.scala 557:22]
  wire  n193_O_t0b; // @[Top.scala 557:22]
  wire [15:0] n193_O_t1b_t0b; // @[Top.scala 557:22]
  wire [15:0] n193_O_t1b_t1b; // @[Top.scala 557:22]
  wire  n194_valid_up; // @[Top.scala 561:22]
  wire  n194_valid_down; // @[Top.scala 561:22]
  wire  n194_I_t0b; // @[Top.scala 561:22]
  wire [15:0] n194_I_t1b_t0b; // @[Top.scala 561:22]
  wire [15:0] n194_I_t1b_t1b; // @[Top.scala 561:22]
  wire [15:0] n194_O; // @[Top.scala 561:22]
  wire  n195_valid_up; // @[Top.scala 564:22]
  wire  n195_valid_down; // @[Top.scala 564:22]
  wire [15:0] n195_I0; // @[Top.scala 564:22]
  wire [15:0] n195_I1; // @[Top.scala 564:22]
  wire [15:0] n195_O_t0b; // @[Top.scala 564:22]
  wire [15:0] n195_O_t1b; // @[Top.scala 564:22]
  wire  n196_clock; // @[Top.scala 568:22]
  wire  n196_reset; // @[Top.scala 568:22]
  wire  n196_valid_up; // @[Top.scala 568:22]
  wire  n196_valid_down; // @[Top.scala 568:22]
  wire [15:0] n196_I_t0b; // @[Top.scala 568:22]
  wire [15:0] n196_I_t1b; // @[Top.scala 568:22]
  wire [15:0] n196_O; // @[Top.scala 568:22]
  wire  n198_valid_up; // @[Top.scala 571:22]
  wire  n198_valid_down; // @[Top.scala 571:22]
  wire [15:0] n198_I0; // @[Top.scala 571:22]
  wire [15:0] n198_I1; // @[Top.scala 571:22]
  wire [15:0] n198_O_t0b; // @[Top.scala 571:22]
  wire [15:0] n198_O_t1b; // @[Top.scala 571:22]
  wire  n199_valid_up; // @[Top.scala 575:22]
  wire  n199_valid_down; // @[Top.scala 575:22]
  wire [15:0] n199_I_t0b; // @[Top.scala 575:22]
  wire [15:0] n199_I_t1b; // @[Top.scala 575:22]
  wire [15:0] n199_O; // @[Top.scala 575:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n201_valid_up; // @[Top.scala 579:22]
  wire  n201_valid_down; // @[Top.scala 579:22]
  wire [15:0] n201_I0; // @[Top.scala 579:22]
  wire [15:0] n201_I1; // @[Top.scala 579:22]
  wire [15:0] n201_O_t0b; // @[Top.scala 579:22]
  wire [15:0] n201_O_t1b; // @[Top.scala 579:22]
  wire  n202_valid_up; // @[Top.scala 583:22]
  wire  n202_valid_down; // @[Top.scala 583:22]
  wire [15:0] n202_I_t0b; // @[Top.scala 583:22]
  wire [15:0] n202_I_t1b; // @[Top.scala 583:22]
  wire [15:0] n202_O; // @[Top.scala 583:22]
  wire  n203_valid_up; // @[Top.scala 586:22]
  wire  n203_valid_down; // @[Top.scala 586:22]
  wire [15:0] n203_I0; // @[Top.scala 586:22]
  wire [15:0] n203_I1; // @[Top.scala 586:22]
  wire [15:0] n203_O_t0b; // @[Top.scala 586:22]
  wire [15:0] n203_O_t1b; // @[Top.scala 586:22]
  wire  n204_valid_up; // @[Top.scala 590:22]
  wire  n204_valid_down; // @[Top.scala 590:22]
  wire [15:0] n204_I0; // @[Top.scala 590:22]
  wire [15:0] n204_I1; // @[Top.scala 590:22]
  wire [15:0] n204_O_t0b; // @[Top.scala 590:22]
  wire [15:0] n204_O_t1b; // @[Top.scala 590:22]
  wire  n205_valid_up; // @[Top.scala 594:22]
  wire  n205_valid_down; // @[Top.scala 594:22]
  wire [15:0] n205_I0_t0b; // @[Top.scala 594:22]
  wire [15:0] n205_I0_t1b; // @[Top.scala 594:22]
  wire [15:0] n205_I1_t0b; // @[Top.scala 594:22]
  wire [15:0] n205_I1_t1b; // @[Top.scala 594:22]
  wire [15:0] n205_O_t0b_t0b; // @[Top.scala 594:22]
  wire [15:0] n205_O_t0b_t1b; // @[Top.scala 594:22]
  wire [15:0] n205_O_t1b_t0b; // @[Top.scala 594:22]
  wire [15:0] n205_O_t1b_t1b; // @[Top.scala 594:22]
  wire  n206_clock; // @[Top.scala 598:22]
  wire  n206_reset; // @[Top.scala 598:22]
  wire  n206_valid_up; // @[Top.scala 598:22]
  wire  n206_valid_down; // @[Top.scala 598:22]
  wire [15:0] n206_I_t0b_t0b; // @[Top.scala 598:22]
  wire [15:0] n206_I_t0b_t1b; // @[Top.scala 598:22]
  wire [15:0] n206_I_t1b_t0b; // @[Top.scala 598:22]
  wire [15:0] n206_I_t1b_t1b; // @[Top.scala 598:22]
  wire [15:0] n206_O_t0b_t0b; // @[Top.scala 598:22]
  wire [15:0] n206_O_t0b_t1b; // @[Top.scala 598:22]
  wire [15:0] n206_O_t1b_t0b; // @[Top.scala 598:22]
  wire [15:0] n206_O_t1b_t1b; // @[Top.scala 598:22]
  wire  n207_valid_up; // @[Top.scala 601:22]
  wire  n207_valid_down; // @[Top.scala 601:22]
  wire  n207_I0; // @[Top.scala 601:22]
  wire [15:0] n207_I1_t0b_t0b; // @[Top.scala 601:22]
  wire [15:0] n207_I1_t0b_t1b; // @[Top.scala 601:22]
  wire [15:0] n207_I1_t1b_t0b; // @[Top.scala 601:22]
  wire [15:0] n207_I1_t1b_t1b; // @[Top.scala 601:22]
  wire  n207_O_t0b; // @[Top.scala 601:22]
  wire [15:0] n207_O_t1b_t0b_t0b; // @[Top.scala 601:22]
  wire [15:0] n207_O_t1b_t0b_t1b; // @[Top.scala 601:22]
  wire [15:0] n207_O_t1b_t1b_t0b; // @[Top.scala 601:22]
  wire [15:0] n207_O_t1b_t1b_t1b; // @[Top.scala 601:22]
  wire  n208_valid_up; // @[Top.scala 605:22]
  wire  n208_valid_down; // @[Top.scala 605:22]
  wire  n208_I_t0b; // @[Top.scala 605:22]
  wire [15:0] n208_I_t1b_t0b_t0b; // @[Top.scala 605:22]
  wire [15:0] n208_I_t1b_t0b_t1b; // @[Top.scala 605:22]
  wire [15:0] n208_I_t1b_t1b_t0b; // @[Top.scala 605:22]
  wire [15:0] n208_I_t1b_t1b_t1b; // @[Top.scala 605:22]
  wire [15:0] n208_O_t0b; // @[Top.scala 605:22]
  wire [15:0] n208_O_t1b; // @[Top.scala 605:22]
  wire  n210_valid_up; // @[Top.scala 608:22]
  wire  n210_valid_down; // @[Top.scala 608:22]
  wire [15:0] n210_I0; // @[Top.scala 608:22]
  wire [15:0] n210_I1_t0b; // @[Top.scala 608:22]
  wire [15:0] n210_I1_t1b; // @[Top.scala 608:22]
  wire [15:0] n210_O_t0b; // @[Top.scala 608:22]
  wire [15:0] n210_O_t1b_t0b; // @[Top.scala 608:22]
  wire [15:0] n210_O_t1b_t1b; // @[Top.scala 608:22]
  Fst n176 ( // @[Top.scala 505:22]
    .valid_up(n176_valid_up),
    .valid_down(n176_valid_down),
    .I_t0b(n176_I_t0b),
    .O(n176_O)
  );
  FIFO_1 n209 ( // @[Top.scala 508:22]
    .clock(n209_clock),
    .reset(n209_reset),
    .valid_up(n209_valid_up),
    .valid_down(n209_valid_down),
    .I(n209_I),
    .O(n209_O)
  );
  FIFO_1 n197 ( // @[Top.scala 511:22]
    .clock(n197_clock),
    .reset(n197_reset),
    .valid_up(n197_valid_up),
    .valid_down(n197_valid_down),
    .I(n197_I),
    .O(n197_O)
  );
  Snd n177 ( // @[Top.scala 514:22]
    .valid_up(n177_valid_up),
    .valid_down(n177_valid_down),
    .I_t1b_t0b(n177_I_t1b_t0b),
    .I_t1b_t1b(n177_I_t1b_t1b),
    .O_t0b(n177_O_t0b),
    .O_t1b(n177_O_t1b)
  );
  Fst_1 n178 ( // @[Top.scala 517:22]
    .valid_up(n178_valid_up),
    .valid_down(n178_valid_down),
    .I_t0b(n178_I_t0b),
    .O(n178_O)
  );
  Snd_1 n179 ( // @[Top.scala 520:22]
    .valid_up(n179_valid_up),
    .valid_down(n179_valid_down),
    .I_t1b(n179_I_t1b),
    .O(n179_O)
  );
  AtomTuple_1 n180 ( // @[Top.scala 523:22]
    .valid_up(n180_valid_up),
    .valid_down(n180_valid_down),
    .I0(n180_I0),
    .I1(n180_I1),
    .O_t0b(n180_O_t0b),
    .O_t1b(n180_O_t1b)
  );
  Add n181 ( // @[Top.scala 527:22]
    .valid_up(n181_valid_up),
    .valid_down(n181_valid_down),
    .I_t0b(n181_I_t0b),
    .I_t1b(n181_I_t1b),
    .O(n181_O)
  );
  InitialDelayCounter_15 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n184 ( // @[Top.scala 531:22]
    .valid_up(n184_valid_up),
    .valid_down(n184_valid_down),
    .I0(n184_I0),
    .O_t0b(n184_O_t0b)
  );
  RShift n185 ( // @[Top.scala 535:22]
    .valid_up(n185_valid_up),
    .valid_down(n185_valid_down),
    .I_t0b(n185_I_t0b),
    .O(n185_O)
  );
  AtomTuple_1 n186 ( // @[Top.scala 538:22]
    .valid_up(n186_valid_up),
    .valid_down(n186_valid_down),
    .I0(n186_I0),
    .I1(n186_I1),
    .O_t0b(n186_O_t0b),
    .O_t1b(n186_O_t1b)
  );
  Eq n187 ( // @[Top.scala 542:22]
    .valid_up(n187_valid_up),
    .valid_down(n187_valid_down),
    .I_t0b(n187_I_t0b),
    .I_t1b(n187_I_t1b),
    .O(n187_O)
  );
  InitialDelayCounter_15 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n190 ( // @[Top.scala 546:22]
    .valid_up(n190_valid_up),
    .valid_down(n190_valid_down),
    .I0(n190_I0),
    .I1(n190_I1),
    .O_t0b(n190_O_t0b),
    .O_t1b(n190_O_t1b)
  );
  Add n191 ( // @[Top.scala 550:22]
    .valid_up(n191_valid_up),
    .valid_down(n191_valid_down),
    .I_t0b(n191_I_t0b),
    .I_t1b(n191_I_t1b),
    .O(n191_O)
  );
  AtomTuple_1 n192 ( // @[Top.scala 553:22]
    .valid_up(n192_valid_up),
    .valid_down(n192_valid_down),
    .I0(n192_I0),
    .I1(n192_I1),
    .O_t0b(n192_O_t0b),
    .O_t1b(n192_O_t1b)
  );
  AtomTuple_9 n193 ( // @[Top.scala 557:22]
    .valid_up(n193_valid_up),
    .valid_down(n193_valid_down),
    .I0(n193_I0),
    .I1_t0b(n193_I1_t0b),
    .I1_t1b(n193_I1_t1b),
    .O_t0b(n193_O_t0b),
    .O_t1b_t0b(n193_O_t1b_t0b),
    .O_t1b_t1b(n193_O_t1b_t1b)
  );
  If n194 ( // @[Top.scala 561:22]
    .valid_up(n194_valid_up),
    .valid_down(n194_valid_down),
    .I_t0b(n194_I_t0b),
    .I_t1b_t0b(n194_I_t1b_t0b),
    .I_t1b_t1b(n194_I_t1b_t1b),
    .O(n194_O)
  );
  AtomTuple_1 n195 ( // @[Top.scala 564:22]
    .valid_up(n195_valid_up),
    .valid_down(n195_valid_down),
    .I0(n195_I0),
    .I1(n195_I1),
    .O_t0b(n195_O_t0b),
    .O_t1b(n195_O_t1b)
  );
  Mul n196 ( // @[Top.scala 568:22]
    .clock(n196_clock),
    .reset(n196_reset),
    .valid_up(n196_valid_up),
    .valid_down(n196_valid_down),
    .I_t0b(n196_I_t0b),
    .I_t1b(n196_I_t1b),
    .O(n196_O)
  );
  AtomTuple_1 n198 ( // @[Top.scala 571:22]
    .valid_up(n198_valid_up),
    .valid_down(n198_valid_down),
    .I0(n198_I0),
    .I1(n198_I1),
    .O_t0b(n198_O_t0b),
    .O_t1b(n198_O_t1b)
  );
  Lt n199 ( // @[Top.scala 575:22]
    .valid_up(n199_valid_up),
    .valid_down(n199_valid_down),
    .I_t0b(n199_I_t0b),
    .I_t1b(n199_I_t1b),
    .O(n199_O)
  );
  InitialDelayCounter_15 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n201 ( // @[Top.scala 579:22]
    .valid_up(n201_valid_up),
    .valid_down(n201_valid_down),
    .I0(n201_I0),
    .I1(n201_I1),
    .O_t0b(n201_O_t0b),
    .O_t1b(n201_O_t1b)
  );
  Sub n202 ( // @[Top.scala 583:22]
    .valid_up(n202_valid_up),
    .valid_down(n202_valid_down),
    .I_t0b(n202_I_t0b),
    .I_t1b(n202_I_t1b),
    .O(n202_O)
  );
  AtomTuple_1 n203 ( // @[Top.scala 586:22]
    .valid_up(n203_valid_up),
    .valid_down(n203_valid_down),
    .I0(n203_I0),
    .I1(n203_I1),
    .O_t0b(n203_O_t0b),
    .O_t1b(n203_O_t1b)
  );
  AtomTuple_1 n204 ( // @[Top.scala 590:22]
    .valid_up(n204_valid_up),
    .valid_down(n204_valid_down),
    .I0(n204_I0),
    .I1(n204_I1),
    .O_t0b(n204_O_t0b),
    .O_t1b(n204_O_t1b)
  );
  AtomTuple_15 n205 ( // @[Top.scala 594:22]
    .valid_up(n205_valid_up),
    .valid_down(n205_valid_down),
    .I0_t0b(n205_I0_t0b),
    .I0_t1b(n205_I0_t1b),
    .I1_t0b(n205_I1_t0b),
    .I1_t1b(n205_I1_t1b),
    .O_t0b_t0b(n205_O_t0b_t0b),
    .O_t0b_t1b(n205_O_t0b_t1b),
    .O_t1b_t0b(n205_O_t1b_t0b),
    .O_t1b_t1b(n205_O_t1b_t1b)
  );
  FIFO_3 n206 ( // @[Top.scala 598:22]
    .clock(n206_clock),
    .reset(n206_reset),
    .valid_up(n206_valid_up),
    .valid_down(n206_valid_down),
    .I_t0b_t0b(n206_I_t0b_t0b),
    .I_t0b_t1b(n206_I_t0b_t1b),
    .I_t1b_t0b(n206_I_t1b_t0b),
    .I_t1b_t1b(n206_I_t1b_t1b),
    .O_t0b_t0b(n206_O_t0b_t0b),
    .O_t0b_t1b(n206_O_t0b_t1b),
    .O_t1b_t0b(n206_O_t1b_t0b),
    .O_t1b_t1b(n206_O_t1b_t1b)
  );
  AtomTuple_16 n207 ( // @[Top.scala 601:22]
    .valid_up(n207_valid_up),
    .valid_down(n207_valid_down),
    .I0(n207_I0),
    .I1_t0b_t0b(n207_I1_t0b_t0b),
    .I1_t0b_t1b(n207_I1_t0b_t1b),
    .I1_t1b_t0b(n207_I1_t1b_t0b),
    .I1_t1b_t1b(n207_I1_t1b_t1b),
    .O_t0b(n207_O_t0b),
    .O_t1b_t0b_t0b(n207_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n207_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n207_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n207_O_t1b_t1b_t1b)
  );
  If_1 n208 ( // @[Top.scala 605:22]
    .valid_up(n208_valid_up),
    .valid_down(n208_valid_down),
    .I_t0b(n208_I_t0b),
    .I_t1b_t0b_t0b(n208_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n208_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n208_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n208_I_t1b_t1b_t1b),
    .O_t0b(n208_O_t0b),
    .O_t1b(n208_O_t1b)
  );
  AtomTuple_3 n210 ( // @[Top.scala 608:22]
    .valid_up(n210_valid_up),
    .valid_down(n210_valid_down),
    .I0(n210_I0),
    .I1_t0b(n210_I1_t0b),
    .I1_t1b(n210_I1_t1b),
    .O_t0b(n210_O_t0b),
    .O_t1b_t0b(n210_O_t1b_t0b),
    .O_t1b_t1b(n210_O_t1b_t1b)
  );
  assign valid_down = n210_valid_down; // @[Top.scala 613:16]
  assign O_t0b = n210_O_t0b; // @[Top.scala 612:7]
  assign O_t1b_t0b = n210_O_t1b_t0b; // @[Top.scala 612:7]
  assign O_t1b_t1b = n210_O_t1b_t1b; // @[Top.scala 612:7]
  assign n176_valid_up = valid_up; // @[Top.scala 507:19]
  assign n176_I_t0b = I_t0b; // @[Top.scala 506:12]
  assign n209_clock = clock;
  assign n209_reset = reset;
  assign n209_valid_up = n176_valid_down; // @[Top.scala 510:19]
  assign n209_I = n176_O; // @[Top.scala 509:12]
  assign n197_clock = clock;
  assign n197_reset = reset;
  assign n197_valid_up = n176_valid_down; // @[Top.scala 513:19]
  assign n197_I = n176_O; // @[Top.scala 512:12]
  assign n177_valid_up = valid_up; // @[Top.scala 516:19]
  assign n177_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 515:12]
  assign n177_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 515:12]
  assign n178_valid_up = n177_valid_down; // @[Top.scala 519:19]
  assign n178_I_t0b = n177_O_t0b; // @[Top.scala 518:12]
  assign n179_valid_up = n177_valid_down; // @[Top.scala 522:19]
  assign n179_I_t1b = n177_O_t1b; // @[Top.scala 521:12]
  assign n180_valid_up = n178_valid_down & n179_valid_down; // @[Top.scala 526:19]
  assign n180_I0 = n178_O; // @[Top.scala 524:13]
  assign n180_I1 = n179_O; // @[Top.scala 525:13]
  assign n181_valid_up = n180_valid_down; // @[Top.scala 529:19]
  assign n181_I_t0b = n180_O_t0b; // @[Top.scala 528:12]
  assign n181_I_t1b = n180_O_t1b; // @[Top.scala 528:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n184_valid_up = n181_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 534:19]
  assign n184_I0 = n181_O; // @[Top.scala 532:13]
  assign n185_valid_up = n184_valid_down; // @[Top.scala 537:19]
  assign n185_I_t0b = n184_O_t0b; // @[Top.scala 536:12]
  assign n186_valid_up = n185_valid_down & n178_valid_down; // @[Top.scala 541:19]
  assign n186_I0 = n185_O; // @[Top.scala 539:13]
  assign n186_I1 = n178_O; // @[Top.scala 540:13]
  assign n187_valid_up = n186_valid_down; // @[Top.scala 544:19]
  assign n187_I_t0b = n186_O_t0b; // @[Top.scala 543:12]
  assign n187_I_t1b = n186_O_t1b; // @[Top.scala 543:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n190_valid_up = n185_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 549:19]
  assign n190_I0 = n185_O; // @[Top.scala 547:13]
  assign n190_I1 = 16'h1; // @[Top.scala 548:13]
  assign n191_valid_up = n190_valid_down; // @[Top.scala 552:19]
  assign n191_I_t0b = n190_O_t0b; // @[Top.scala 551:12]
  assign n191_I_t1b = n190_O_t1b; // @[Top.scala 551:12]
  assign n192_valid_up = n191_valid_down & n185_valid_down; // @[Top.scala 556:19]
  assign n192_I0 = n191_O; // @[Top.scala 554:13]
  assign n192_I1 = n185_O; // @[Top.scala 555:13]
  assign n193_valid_up = n187_valid_down & n192_valid_down; // @[Top.scala 560:19]
  assign n193_I0 = n187_O[0]; // @[Top.scala 558:13]
  assign n193_I1_t0b = n192_O_t0b; // @[Top.scala 559:13]
  assign n193_I1_t1b = n192_O_t1b; // @[Top.scala 559:13]
  assign n194_valid_up = n193_valid_down; // @[Top.scala 563:19]
  assign n194_I_t0b = n193_O_t0b; // @[Top.scala 562:12]
  assign n194_I_t1b_t0b = n193_O_t1b_t0b; // @[Top.scala 562:12]
  assign n194_I_t1b_t1b = n193_O_t1b_t1b; // @[Top.scala 562:12]
  assign n195_valid_up = n194_valid_down; // @[Top.scala 567:19]
  assign n195_I0 = n194_O; // @[Top.scala 565:13]
  assign n195_I1 = n194_O; // @[Top.scala 566:13]
  assign n196_clock = clock;
  assign n196_reset = reset;
  assign n196_valid_up = n195_valid_down; // @[Top.scala 570:19]
  assign n196_I_t0b = n195_O_t0b; // @[Top.scala 569:12]
  assign n196_I_t1b = n195_O_t1b; // @[Top.scala 569:12]
  assign n198_valid_up = n197_valid_down & n196_valid_down; // @[Top.scala 574:19]
  assign n198_I0 = n197_O; // @[Top.scala 572:13]
  assign n198_I1 = n196_O; // @[Top.scala 573:13]
  assign n199_valid_up = n198_valid_down; // @[Top.scala 577:19]
  assign n199_I_t0b = n198_O_t0b; // @[Top.scala 576:12]
  assign n199_I_t1b = n198_O_t1b; // @[Top.scala 576:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n201_valid_up = n194_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 582:19]
  assign n201_I0 = n194_O; // @[Top.scala 580:13]
  assign n201_I1 = 16'h1; // @[Top.scala 581:13]
  assign n202_valid_up = n201_valid_down; // @[Top.scala 585:19]
  assign n202_I_t0b = n201_O_t0b; // @[Top.scala 584:12]
  assign n202_I_t1b = n201_O_t1b; // @[Top.scala 584:12]
  assign n203_valid_up = n178_valid_down & n202_valid_down; // @[Top.scala 589:19]
  assign n203_I0 = n178_O; // @[Top.scala 587:13]
  assign n203_I1 = n202_O; // @[Top.scala 588:13]
  assign n204_valid_up = n194_valid_down & n179_valid_down; // @[Top.scala 593:19]
  assign n204_I0 = n194_O; // @[Top.scala 591:13]
  assign n204_I1 = n179_O; // @[Top.scala 592:13]
  assign n205_valid_up = n203_valid_down & n204_valid_down; // @[Top.scala 597:19]
  assign n205_I0_t0b = n203_O_t0b; // @[Top.scala 595:13]
  assign n205_I0_t1b = n203_O_t1b; // @[Top.scala 595:13]
  assign n205_I1_t0b = n204_O_t0b; // @[Top.scala 596:13]
  assign n205_I1_t1b = n204_O_t1b; // @[Top.scala 596:13]
  assign n206_clock = clock;
  assign n206_reset = reset;
  assign n206_valid_up = n205_valid_down; // @[Top.scala 600:19]
  assign n206_I_t0b_t0b = n205_O_t0b_t0b; // @[Top.scala 599:12]
  assign n206_I_t0b_t1b = n205_O_t0b_t1b; // @[Top.scala 599:12]
  assign n206_I_t1b_t0b = n205_O_t1b_t0b; // @[Top.scala 599:12]
  assign n206_I_t1b_t1b = n205_O_t1b_t1b; // @[Top.scala 599:12]
  assign n207_valid_up = n199_valid_down & n206_valid_down; // @[Top.scala 604:19]
  assign n207_I0 = n199_O[0]; // @[Top.scala 602:13]
  assign n207_I1_t0b_t0b = n206_O_t0b_t0b; // @[Top.scala 603:13]
  assign n207_I1_t0b_t1b = n206_O_t0b_t1b; // @[Top.scala 603:13]
  assign n207_I1_t1b_t0b = n206_O_t1b_t0b; // @[Top.scala 603:13]
  assign n207_I1_t1b_t1b = n206_O_t1b_t1b; // @[Top.scala 603:13]
  assign n208_valid_up = n207_valid_down; // @[Top.scala 607:19]
  assign n208_I_t0b = n207_O_t0b; // @[Top.scala 606:12]
  assign n208_I_t1b_t0b_t0b = n207_O_t1b_t0b_t0b; // @[Top.scala 606:12]
  assign n208_I_t1b_t0b_t1b = n207_O_t1b_t0b_t1b; // @[Top.scala 606:12]
  assign n208_I_t1b_t1b_t0b = n207_O_t1b_t1b_t0b; // @[Top.scala 606:12]
  assign n208_I_t1b_t1b_t1b = n207_O_t1b_t1b_t1b; // @[Top.scala 606:12]
  assign n210_valid_up = n209_valid_down & n208_valid_down; // @[Top.scala 611:19]
  assign n210_I0 = n209_O; // @[Top.scala 609:13]
  assign n210_I1_t0b = n208_O_t0b; // @[Top.scala 610:13]
  assign n210_I1_t1b = n208_O_t1b; // @[Top.scala 610:13]
endmodule
module MapS_5(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_5 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_5 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_5 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_5 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_5(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_5 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_18(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [4:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 5'h10; // @[InitialDelayCounter.scala 17:17]
  wire [4:0] _T_4 = value + 5'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 5'h10; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[4:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 5'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Module_6(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n215_valid_up; // @[Top.scala 619:22]
  wire  n215_valid_down; // @[Top.scala 619:22]
  wire [15:0] n215_I_t0b; // @[Top.scala 619:22]
  wire [15:0] n215_O; // @[Top.scala 619:22]
  wire  n248_clock; // @[Top.scala 622:22]
  wire  n248_reset; // @[Top.scala 622:22]
  wire  n248_valid_up; // @[Top.scala 622:22]
  wire  n248_valid_down; // @[Top.scala 622:22]
  wire [15:0] n248_I; // @[Top.scala 622:22]
  wire [15:0] n248_O; // @[Top.scala 622:22]
  wire  n236_clock; // @[Top.scala 625:22]
  wire  n236_reset; // @[Top.scala 625:22]
  wire  n236_valid_up; // @[Top.scala 625:22]
  wire  n236_valid_down; // @[Top.scala 625:22]
  wire [15:0] n236_I; // @[Top.scala 625:22]
  wire [15:0] n236_O; // @[Top.scala 625:22]
  wire  n216_valid_up; // @[Top.scala 628:22]
  wire  n216_valid_down; // @[Top.scala 628:22]
  wire [15:0] n216_I_t1b_t0b; // @[Top.scala 628:22]
  wire [15:0] n216_I_t1b_t1b; // @[Top.scala 628:22]
  wire [15:0] n216_O_t0b; // @[Top.scala 628:22]
  wire [15:0] n216_O_t1b; // @[Top.scala 628:22]
  wire  n217_valid_up; // @[Top.scala 631:22]
  wire  n217_valid_down; // @[Top.scala 631:22]
  wire [15:0] n217_I_t0b; // @[Top.scala 631:22]
  wire [15:0] n217_O; // @[Top.scala 631:22]
  wire  n218_valid_up; // @[Top.scala 634:22]
  wire  n218_valid_down; // @[Top.scala 634:22]
  wire [15:0] n218_I_t1b; // @[Top.scala 634:22]
  wire [15:0] n218_O; // @[Top.scala 634:22]
  wire  n219_valid_up; // @[Top.scala 637:22]
  wire  n219_valid_down; // @[Top.scala 637:22]
  wire [15:0] n219_I0; // @[Top.scala 637:22]
  wire [15:0] n219_I1; // @[Top.scala 637:22]
  wire [15:0] n219_O_t0b; // @[Top.scala 637:22]
  wire [15:0] n219_O_t1b; // @[Top.scala 637:22]
  wire  n220_valid_up; // @[Top.scala 641:22]
  wire  n220_valid_down; // @[Top.scala 641:22]
  wire [15:0] n220_I_t0b; // @[Top.scala 641:22]
  wire [15:0] n220_I_t1b; // @[Top.scala 641:22]
  wire [15:0] n220_O; // @[Top.scala 641:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n223_valid_up; // @[Top.scala 645:22]
  wire  n223_valid_down; // @[Top.scala 645:22]
  wire [15:0] n223_I0; // @[Top.scala 645:22]
  wire [15:0] n223_O_t0b; // @[Top.scala 645:22]
  wire  n224_valid_up; // @[Top.scala 649:22]
  wire  n224_valid_down; // @[Top.scala 649:22]
  wire [15:0] n224_I_t0b; // @[Top.scala 649:22]
  wire [15:0] n224_O; // @[Top.scala 649:22]
  wire  n225_valid_up; // @[Top.scala 652:22]
  wire  n225_valid_down; // @[Top.scala 652:22]
  wire [15:0] n225_I0; // @[Top.scala 652:22]
  wire [15:0] n225_I1; // @[Top.scala 652:22]
  wire [15:0] n225_O_t0b; // @[Top.scala 652:22]
  wire [15:0] n225_O_t1b; // @[Top.scala 652:22]
  wire  n226_valid_up; // @[Top.scala 656:22]
  wire  n226_valid_down; // @[Top.scala 656:22]
  wire [15:0] n226_I_t0b; // @[Top.scala 656:22]
  wire [15:0] n226_I_t1b; // @[Top.scala 656:22]
  wire [15:0] n226_O; // @[Top.scala 656:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n229_valid_up; // @[Top.scala 660:22]
  wire  n229_valid_down; // @[Top.scala 660:22]
  wire [15:0] n229_I0; // @[Top.scala 660:22]
  wire [15:0] n229_I1; // @[Top.scala 660:22]
  wire [15:0] n229_O_t0b; // @[Top.scala 660:22]
  wire [15:0] n229_O_t1b; // @[Top.scala 660:22]
  wire  n230_valid_up; // @[Top.scala 664:22]
  wire  n230_valid_down; // @[Top.scala 664:22]
  wire [15:0] n230_I_t0b; // @[Top.scala 664:22]
  wire [15:0] n230_I_t1b; // @[Top.scala 664:22]
  wire [15:0] n230_O; // @[Top.scala 664:22]
  wire  n231_valid_up; // @[Top.scala 667:22]
  wire  n231_valid_down; // @[Top.scala 667:22]
  wire [15:0] n231_I0; // @[Top.scala 667:22]
  wire [15:0] n231_I1; // @[Top.scala 667:22]
  wire [15:0] n231_O_t0b; // @[Top.scala 667:22]
  wire [15:0] n231_O_t1b; // @[Top.scala 667:22]
  wire  n232_valid_up; // @[Top.scala 671:22]
  wire  n232_valid_down; // @[Top.scala 671:22]
  wire  n232_I0; // @[Top.scala 671:22]
  wire [15:0] n232_I1_t0b; // @[Top.scala 671:22]
  wire [15:0] n232_I1_t1b; // @[Top.scala 671:22]
  wire  n232_O_t0b; // @[Top.scala 671:22]
  wire [15:0] n232_O_t1b_t0b; // @[Top.scala 671:22]
  wire [15:0] n232_O_t1b_t1b; // @[Top.scala 671:22]
  wire  n233_valid_up; // @[Top.scala 675:22]
  wire  n233_valid_down; // @[Top.scala 675:22]
  wire  n233_I_t0b; // @[Top.scala 675:22]
  wire [15:0] n233_I_t1b_t0b; // @[Top.scala 675:22]
  wire [15:0] n233_I_t1b_t1b; // @[Top.scala 675:22]
  wire [15:0] n233_O; // @[Top.scala 675:22]
  wire  n234_valid_up; // @[Top.scala 678:22]
  wire  n234_valid_down; // @[Top.scala 678:22]
  wire [15:0] n234_I0; // @[Top.scala 678:22]
  wire [15:0] n234_I1; // @[Top.scala 678:22]
  wire [15:0] n234_O_t0b; // @[Top.scala 678:22]
  wire [15:0] n234_O_t1b; // @[Top.scala 678:22]
  wire  n235_clock; // @[Top.scala 682:22]
  wire  n235_reset; // @[Top.scala 682:22]
  wire  n235_valid_up; // @[Top.scala 682:22]
  wire  n235_valid_down; // @[Top.scala 682:22]
  wire [15:0] n235_I_t0b; // @[Top.scala 682:22]
  wire [15:0] n235_I_t1b; // @[Top.scala 682:22]
  wire [15:0] n235_O; // @[Top.scala 682:22]
  wire  n237_valid_up; // @[Top.scala 685:22]
  wire  n237_valid_down; // @[Top.scala 685:22]
  wire [15:0] n237_I0; // @[Top.scala 685:22]
  wire [15:0] n237_I1; // @[Top.scala 685:22]
  wire [15:0] n237_O_t0b; // @[Top.scala 685:22]
  wire [15:0] n237_O_t1b; // @[Top.scala 685:22]
  wire  n238_valid_up; // @[Top.scala 689:22]
  wire  n238_valid_down; // @[Top.scala 689:22]
  wire [15:0] n238_I_t0b; // @[Top.scala 689:22]
  wire [15:0] n238_I_t1b; // @[Top.scala 689:22]
  wire [15:0] n238_O; // @[Top.scala 689:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n240_valid_up; // @[Top.scala 693:22]
  wire  n240_valid_down; // @[Top.scala 693:22]
  wire [15:0] n240_I0; // @[Top.scala 693:22]
  wire [15:0] n240_I1; // @[Top.scala 693:22]
  wire [15:0] n240_O_t0b; // @[Top.scala 693:22]
  wire [15:0] n240_O_t1b; // @[Top.scala 693:22]
  wire  n241_valid_up; // @[Top.scala 697:22]
  wire  n241_valid_down; // @[Top.scala 697:22]
  wire [15:0] n241_I_t0b; // @[Top.scala 697:22]
  wire [15:0] n241_I_t1b; // @[Top.scala 697:22]
  wire [15:0] n241_O; // @[Top.scala 697:22]
  wire  n242_valid_up; // @[Top.scala 700:22]
  wire  n242_valid_down; // @[Top.scala 700:22]
  wire [15:0] n242_I0; // @[Top.scala 700:22]
  wire [15:0] n242_I1; // @[Top.scala 700:22]
  wire [15:0] n242_O_t0b; // @[Top.scala 700:22]
  wire [15:0] n242_O_t1b; // @[Top.scala 700:22]
  wire  n243_valid_up; // @[Top.scala 704:22]
  wire  n243_valid_down; // @[Top.scala 704:22]
  wire [15:0] n243_I0; // @[Top.scala 704:22]
  wire [15:0] n243_I1; // @[Top.scala 704:22]
  wire [15:0] n243_O_t0b; // @[Top.scala 704:22]
  wire [15:0] n243_O_t1b; // @[Top.scala 704:22]
  wire  n244_valid_up; // @[Top.scala 708:22]
  wire  n244_valid_down; // @[Top.scala 708:22]
  wire [15:0] n244_I0_t0b; // @[Top.scala 708:22]
  wire [15:0] n244_I0_t1b; // @[Top.scala 708:22]
  wire [15:0] n244_I1_t0b; // @[Top.scala 708:22]
  wire [15:0] n244_I1_t1b; // @[Top.scala 708:22]
  wire [15:0] n244_O_t0b_t0b; // @[Top.scala 708:22]
  wire [15:0] n244_O_t0b_t1b; // @[Top.scala 708:22]
  wire [15:0] n244_O_t1b_t0b; // @[Top.scala 708:22]
  wire [15:0] n244_O_t1b_t1b; // @[Top.scala 708:22]
  wire  n245_clock; // @[Top.scala 712:22]
  wire  n245_reset; // @[Top.scala 712:22]
  wire  n245_valid_up; // @[Top.scala 712:22]
  wire  n245_valid_down; // @[Top.scala 712:22]
  wire [15:0] n245_I_t0b_t0b; // @[Top.scala 712:22]
  wire [15:0] n245_I_t0b_t1b; // @[Top.scala 712:22]
  wire [15:0] n245_I_t1b_t0b; // @[Top.scala 712:22]
  wire [15:0] n245_I_t1b_t1b; // @[Top.scala 712:22]
  wire [15:0] n245_O_t0b_t0b; // @[Top.scala 712:22]
  wire [15:0] n245_O_t0b_t1b; // @[Top.scala 712:22]
  wire [15:0] n245_O_t1b_t0b; // @[Top.scala 712:22]
  wire [15:0] n245_O_t1b_t1b; // @[Top.scala 712:22]
  wire  n246_valid_up; // @[Top.scala 715:22]
  wire  n246_valid_down; // @[Top.scala 715:22]
  wire  n246_I0; // @[Top.scala 715:22]
  wire [15:0] n246_I1_t0b_t0b; // @[Top.scala 715:22]
  wire [15:0] n246_I1_t0b_t1b; // @[Top.scala 715:22]
  wire [15:0] n246_I1_t1b_t0b; // @[Top.scala 715:22]
  wire [15:0] n246_I1_t1b_t1b; // @[Top.scala 715:22]
  wire  n246_O_t0b; // @[Top.scala 715:22]
  wire [15:0] n246_O_t1b_t0b_t0b; // @[Top.scala 715:22]
  wire [15:0] n246_O_t1b_t0b_t1b; // @[Top.scala 715:22]
  wire [15:0] n246_O_t1b_t1b_t0b; // @[Top.scala 715:22]
  wire [15:0] n246_O_t1b_t1b_t1b; // @[Top.scala 715:22]
  wire  n247_valid_up; // @[Top.scala 719:22]
  wire  n247_valid_down; // @[Top.scala 719:22]
  wire  n247_I_t0b; // @[Top.scala 719:22]
  wire [15:0] n247_I_t1b_t0b_t0b; // @[Top.scala 719:22]
  wire [15:0] n247_I_t1b_t0b_t1b; // @[Top.scala 719:22]
  wire [15:0] n247_I_t1b_t1b_t0b; // @[Top.scala 719:22]
  wire [15:0] n247_I_t1b_t1b_t1b; // @[Top.scala 719:22]
  wire [15:0] n247_O_t0b; // @[Top.scala 719:22]
  wire [15:0] n247_O_t1b; // @[Top.scala 719:22]
  wire  n249_valid_up; // @[Top.scala 722:22]
  wire  n249_valid_down; // @[Top.scala 722:22]
  wire [15:0] n249_I0; // @[Top.scala 722:22]
  wire [15:0] n249_I1_t0b; // @[Top.scala 722:22]
  wire [15:0] n249_I1_t1b; // @[Top.scala 722:22]
  wire [15:0] n249_O_t0b; // @[Top.scala 722:22]
  wire [15:0] n249_O_t1b_t0b; // @[Top.scala 722:22]
  wire [15:0] n249_O_t1b_t1b; // @[Top.scala 722:22]
  Fst n215 ( // @[Top.scala 619:22]
    .valid_up(n215_valid_up),
    .valid_down(n215_valid_down),
    .I_t0b(n215_I_t0b),
    .O(n215_O)
  );
  FIFO_1 n248 ( // @[Top.scala 622:22]
    .clock(n248_clock),
    .reset(n248_reset),
    .valid_up(n248_valid_up),
    .valid_down(n248_valid_down),
    .I(n248_I),
    .O(n248_O)
  );
  FIFO_1 n236 ( // @[Top.scala 625:22]
    .clock(n236_clock),
    .reset(n236_reset),
    .valid_up(n236_valid_up),
    .valid_down(n236_valid_down),
    .I(n236_I),
    .O(n236_O)
  );
  Snd n216 ( // @[Top.scala 628:22]
    .valid_up(n216_valid_up),
    .valid_down(n216_valid_down),
    .I_t1b_t0b(n216_I_t1b_t0b),
    .I_t1b_t1b(n216_I_t1b_t1b),
    .O_t0b(n216_O_t0b),
    .O_t1b(n216_O_t1b)
  );
  Fst_1 n217 ( // @[Top.scala 631:22]
    .valid_up(n217_valid_up),
    .valid_down(n217_valid_down),
    .I_t0b(n217_I_t0b),
    .O(n217_O)
  );
  Snd_1 n218 ( // @[Top.scala 634:22]
    .valid_up(n218_valid_up),
    .valid_down(n218_valid_down),
    .I_t1b(n218_I_t1b),
    .O(n218_O)
  );
  AtomTuple_1 n219 ( // @[Top.scala 637:22]
    .valid_up(n219_valid_up),
    .valid_down(n219_valid_down),
    .I0(n219_I0),
    .I1(n219_I1),
    .O_t0b(n219_O_t0b),
    .O_t1b(n219_O_t1b)
  );
  Add n220 ( // @[Top.scala 641:22]
    .valid_up(n220_valid_up),
    .valid_down(n220_valid_down),
    .I_t0b(n220_I_t0b),
    .I_t1b(n220_I_t1b),
    .O(n220_O)
  );
  InitialDelayCounter_18 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n223 ( // @[Top.scala 645:22]
    .valid_up(n223_valid_up),
    .valid_down(n223_valid_down),
    .I0(n223_I0),
    .O_t0b(n223_O_t0b)
  );
  RShift n224 ( // @[Top.scala 649:22]
    .valid_up(n224_valid_up),
    .valid_down(n224_valid_down),
    .I_t0b(n224_I_t0b),
    .O(n224_O)
  );
  AtomTuple_1 n225 ( // @[Top.scala 652:22]
    .valid_up(n225_valid_up),
    .valid_down(n225_valid_down),
    .I0(n225_I0),
    .I1(n225_I1),
    .O_t0b(n225_O_t0b),
    .O_t1b(n225_O_t1b)
  );
  Eq n226 ( // @[Top.scala 656:22]
    .valid_up(n226_valid_up),
    .valid_down(n226_valid_down),
    .I_t0b(n226_I_t0b),
    .I_t1b(n226_I_t1b),
    .O(n226_O)
  );
  InitialDelayCounter_18 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n229 ( // @[Top.scala 660:22]
    .valid_up(n229_valid_up),
    .valid_down(n229_valid_down),
    .I0(n229_I0),
    .I1(n229_I1),
    .O_t0b(n229_O_t0b),
    .O_t1b(n229_O_t1b)
  );
  Add n230 ( // @[Top.scala 664:22]
    .valid_up(n230_valid_up),
    .valid_down(n230_valid_down),
    .I_t0b(n230_I_t0b),
    .I_t1b(n230_I_t1b),
    .O(n230_O)
  );
  AtomTuple_1 n231 ( // @[Top.scala 667:22]
    .valid_up(n231_valid_up),
    .valid_down(n231_valid_down),
    .I0(n231_I0),
    .I1(n231_I1),
    .O_t0b(n231_O_t0b),
    .O_t1b(n231_O_t1b)
  );
  AtomTuple_9 n232 ( // @[Top.scala 671:22]
    .valid_up(n232_valid_up),
    .valid_down(n232_valid_down),
    .I0(n232_I0),
    .I1_t0b(n232_I1_t0b),
    .I1_t1b(n232_I1_t1b),
    .O_t0b(n232_O_t0b),
    .O_t1b_t0b(n232_O_t1b_t0b),
    .O_t1b_t1b(n232_O_t1b_t1b)
  );
  If n233 ( // @[Top.scala 675:22]
    .valid_up(n233_valid_up),
    .valid_down(n233_valid_down),
    .I_t0b(n233_I_t0b),
    .I_t1b_t0b(n233_I_t1b_t0b),
    .I_t1b_t1b(n233_I_t1b_t1b),
    .O(n233_O)
  );
  AtomTuple_1 n234 ( // @[Top.scala 678:22]
    .valid_up(n234_valid_up),
    .valid_down(n234_valid_down),
    .I0(n234_I0),
    .I1(n234_I1),
    .O_t0b(n234_O_t0b),
    .O_t1b(n234_O_t1b)
  );
  Mul n235 ( // @[Top.scala 682:22]
    .clock(n235_clock),
    .reset(n235_reset),
    .valid_up(n235_valid_up),
    .valid_down(n235_valid_down),
    .I_t0b(n235_I_t0b),
    .I_t1b(n235_I_t1b),
    .O(n235_O)
  );
  AtomTuple_1 n237 ( // @[Top.scala 685:22]
    .valid_up(n237_valid_up),
    .valid_down(n237_valid_down),
    .I0(n237_I0),
    .I1(n237_I1),
    .O_t0b(n237_O_t0b),
    .O_t1b(n237_O_t1b)
  );
  Lt n238 ( // @[Top.scala 689:22]
    .valid_up(n238_valid_up),
    .valid_down(n238_valid_down),
    .I_t0b(n238_I_t0b),
    .I_t1b(n238_I_t1b),
    .O(n238_O)
  );
  InitialDelayCounter_18 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n240 ( // @[Top.scala 693:22]
    .valid_up(n240_valid_up),
    .valid_down(n240_valid_down),
    .I0(n240_I0),
    .I1(n240_I1),
    .O_t0b(n240_O_t0b),
    .O_t1b(n240_O_t1b)
  );
  Sub n241 ( // @[Top.scala 697:22]
    .valid_up(n241_valid_up),
    .valid_down(n241_valid_down),
    .I_t0b(n241_I_t0b),
    .I_t1b(n241_I_t1b),
    .O(n241_O)
  );
  AtomTuple_1 n242 ( // @[Top.scala 700:22]
    .valid_up(n242_valid_up),
    .valid_down(n242_valid_down),
    .I0(n242_I0),
    .I1(n242_I1),
    .O_t0b(n242_O_t0b),
    .O_t1b(n242_O_t1b)
  );
  AtomTuple_1 n243 ( // @[Top.scala 704:22]
    .valid_up(n243_valid_up),
    .valid_down(n243_valid_down),
    .I0(n243_I0),
    .I1(n243_I1),
    .O_t0b(n243_O_t0b),
    .O_t1b(n243_O_t1b)
  );
  AtomTuple_15 n244 ( // @[Top.scala 708:22]
    .valid_up(n244_valid_up),
    .valid_down(n244_valid_down),
    .I0_t0b(n244_I0_t0b),
    .I0_t1b(n244_I0_t1b),
    .I1_t0b(n244_I1_t0b),
    .I1_t1b(n244_I1_t1b),
    .O_t0b_t0b(n244_O_t0b_t0b),
    .O_t0b_t1b(n244_O_t0b_t1b),
    .O_t1b_t0b(n244_O_t1b_t0b),
    .O_t1b_t1b(n244_O_t1b_t1b)
  );
  FIFO_3 n245 ( // @[Top.scala 712:22]
    .clock(n245_clock),
    .reset(n245_reset),
    .valid_up(n245_valid_up),
    .valid_down(n245_valid_down),
    .I_t0b_t0b(n245_I_t0b_t0b),
    .I_t0b_t1b(n245_I_t0b_t1b),
    .I_t1b_t0b(n245_I_t1b_t0b),
    .I_t1b_t1b(n245_I_t1b_t1b),
    .O_t0b_t0b(n245_O_t0b_t0b),
    .O_t0b_t1b(n245_O_t0b_t1b),
    .O_t1b_t0b(n245_O_t1b_t0b),
    .O_t1b_t1b(n245_O_t1b_t1b)
  );
  AtomTuple_16 n246 ( // @[Top.scala 715:22]
    .valid_up(n246_valid_up),
    .valid_down(n246_valid_down),
    .I0(n246_I0),
    .I1_t0b_t0b(n246_I1_t0b_t0b),
    .I1_t0b_t1b(n246_I1_t0b_t1b),
    .I1_t1b_t0b(n246_I1_t1b_t0b),
    .I1_t1b_t1b(n246_I1_t1b_t1b),
    .O_t0b(n246_O_t0b),
    .O_t1b_t0b_t0b(n246_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n246_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n246_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n246_O_t1b_t1b_t1b)
  );
  If_1 n247 ( // @[Top.scala 719:22]
    .valid_up(n247_valid_up),
    .valid_down(n247_valid_down),
    .I_t0b(n247_I_t0b),
    .I_t1b_t0b_t0b(n247_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n247_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n247_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n247_I_t1b_t1b_t1b),
    .O_t0b(n247_O_t0b),
    .O_t1b(n247_O_t1b)
  );
  AtomTuple_3 n249 ( // @[Top.scala 722:22]
    .valid_up(n249_valid_up),
    .valid_down(n249_valid_down),
    .I0(n249_I0),
    .I1_t0b(n249_I1_t0b),
    .I1_t1b(n249_I1_t1b),
    .O_t0b(n249_O_t0b),
    .O_t1b_t0b(n249_O_t1b_t0b),
    .O_t1b_t1b(n249_O_t1b_t1b)
  );
  assign valid_down = n249_valid_down; // @[Top.scala 727:16]
  assign O_t0b = n249_O_t0b; // @[Top.scala 726:7]
  assign O_t1b_t0b = n249_O_t1b_t0b; // @[Top.scala 726:7]
  assign O_t1b_t1b = n249_O_t1b_t1b; // @[Top.scala 726:7]
  assign n215_valid_up = valid_up; // @[Top.scala 621:19]
  assign n215_I_t0b = I_t0b; // @[Top.scala 620:12]
  assign n248_clock = clock;
  assign n248_reset = reset;
  assign n248_valid_up = n215_valid_down; // @[Top.scala 624:19]
  assign n248_I = n215_O; // @[Top.scala 623:12]
  assign n236_clock = clock;
  assign n236_reset = reset;
  assign n236_valid_up = n215_valid_down; // @[Top.scala 627:19]
  assign n236_I = n215_O; // @[Top.scala 626:12]
  assign n216_valid_up = valid_up; // @[Top.scala 630:19]
  assign n216_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 629:12]
  assign n216_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 629:12]
  assign n217_valid_up = n216_valid_down; // @[Top.scala 633:19]
  assign n217_I_t0b = n216_O_t0b; // @[Top.scala 632:12]
  assign n218_valid_up = n216_valid_down; // @[Top.scala 636:19]
  assign n218_I_t1b = n216_O_t1b; // @[Top.scala 635:12]
  assign n219_valid_up = n217_valid_down & n218_valid_down; // @[Top.scala 640:19]
  assign n219_I0 = n217_O; // @[Top.scala 638:13]
  assign n219_I1 = n218_O; // @[Top.scala 639:13]
  assign n220_valid_up = n219_valid_down; // @[Top.scala 643:19]
  assign n220_I_t0b = n219_O_t0b; // @[Top.scala 642:12]
  assign n220_I_t1b = n219_O_t1b; // @[Top.scala 642:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n223_valid_up = n220_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 648:19]
  assign n223_I0 = n220_O; // @[Top.scala 646:13]
  assign n224_valid_up = n223_valid_down; // @[Top.scala 651:19]
  assign n224_I_t0b = n223_O_t0b; // @[Top.scala 650:12]
  assign n225_valid_up = n224_valid_down & n217_valid_down; // @[Top.scala 655:19]
  assign n225_I0 = n224_O; // @[Top.scala 653:13]
  assign n225_I1 = n217_O; // @[Top.scala 654:13]
  assign n226_valid_up = n225_valid_down; // @[Top.scala 658:19]
  assign n226_I_t0b = n225_O_t0b; // @[Top.scala 657:12]
  assign n226_I_t1b = n225_O_t1b; // @[Top.scala 657:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n229_valid_up = n224_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 663:19]
  assign n229_I0 = n224_O; // @[Top.scala 661:13]
  assign n229_I1 = 16'h1; // @[Top.scala 662:13]
  assign n230_valid_up = n229_valid_down; // @[Top.scala 666:19]
  assign n230_I_t0b = n229_O_t0b; // @[Top.scala 665:12]
  assign n230_I_t1b = n229_O_t1b; // @[Top.scala 665:12]
  assign n231_valid_up = n230_valid_down & n224_valid_down; // @[Top.scala 670:19]
  assign n231_I0 = n230_O; // @[Top.scala 668:13]
  assign n231_I1 = n224_O; // @[Top.scala 669:13]
  assign n232_valid_up = n226_valid_down & n231_valid_down; // @[Top.scala 674:19]
  assign n232_I0 = n226_O[0]; // @[Top.scala 672:13]
  assign n232_I1_t0b = n231_O_t0b; // @[Top.scala 673:13]
  assign n232_I1_t1b = n231_O_t1b; // @[Top.scala 673:13]
  assign n233_valid_up = n232_valid_down; // @[Top.scala 677:19]
  assign n233_I_t0b = n232_O_t0b; // @[Top.scala 676:12]
  assign n233_I_t1b_t0b = n232_O_t1b_t0b; // @[Top.scala 676:12]
  assign n233_I_t1b_t1b = n232_O_t1b_t1b; // @[Top.scala 676:12]
  assign n234_valid_up = n233_valid_down; // @[Top.scala 681:19]
  assign n234_I0 = n233_O; // @[Top.scala 679:13]
  assign n234_I1 = n233_O; // @[Top.scala 680:13]
  assign n235_clock = clock;
  assign n235_reset = reset;
  assign n235_valid_up = n234_valid_down; // @[Top.scala 684:19]
  assign n235_I_t0b = n234_O_t0b; // @[Top.scala 683:12]
  assign n235_I_t1b = n234_O_t1b; // @[Top.scala 683:12]
  assign n237_valid_up = n236_valid_down & n235_valid_down; // @[Top.scala 688:19]
  assign n237_I0 = n236_O; // @[Top.scala 686:13]
  assign n237_I1 = n235_O; // @[Top.scala 687:13]
  assign n238_valid_up = n237_valid_down; // @[Top.scala 691:19]
  assign n238_I_t0b = n237_O_t0b; // @[Top.scala 690:12]
  assign n238_I_t1b = n237_O_t1b; // @[Top.scala 690:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n240_valid_up = n233_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 696:19]
  assign n240_I0 = n233_O; // @[Top.scala 694:13]
  assign n240_I1 = 16'h1; // @[Top.scala 695:13]
  assign n241_valid_up = n240_valid_down; // @[Top.scala 699:19]
  assign n241_I_t0b = n240_O_t0b; // @[Top.scala 698:12]
  assign n241_I_t1b = n240_O_t1b; // @[Top.scala 698:12]
  assign n242_valid_up = n217_valid_down & n241_valid_down; // @[Top.scala 703:19]
  assign n242_I0 = n217_O; // @[Top.scala 701:13]
  assign n242_I1 = n241_O; // @[Top.scala 702:13]
  assign n243_valid_up = n233_valid_down & n218_valid_down; // @[Top.scala 707:19]
  assign n243_I0 = n233_O; // @[Top.scala 705:13]
  assign n243_I1 = n218_O; // @[Top.scala 706:13]
  assign n244_valid_up = n242_valid_down & n243_valid_down; // @[Top.scala 711:19]
  assign n244_I0_t0b = n242_O_t0b; // @[Top.scala 709:13]
  assign n244_I0_t1b = n242_O_t1b; // @[Top.scala 709:13]
  assign n244_I1_t0b = n243_O_t0b; // @[Top.scala 710:13]
  assign n244_I1_t1b = n243_O_t1b; // @[Top.scala 710:13]
  assign n245_clock = clock;
  assign n245_reset = reset;
  assign n245_valid_up = n244_valid_down; // @[Top.scala 714:19]
  assign n245_I_t0b_t0b = n244_O_t0b_t0b; // @[Top.scala 713:12]
  assign n245_I_t0b_t1b = n244_O_t0b_t1b; // @[Top.scala 713:12]
  assign n245_I_t1b_t0b = n244_O_t1b_t0b; // @[Top.scala 713:12]
  assign n245_I_t1b_t1b = n244_O_t1b_t1b; // @[Top.scala 713:12]
  assign n246_valid_up = n238_valid_down & n245_valid_down; // @[Top.scala 718:19]
  assign n246_I0 = n238_O[0]; // @[Top.scala 716:13]
  assign n246_I1_t0b_t0b = n245_O_t0b_t0b; // @[Top.scala 717:13]
  assign n246_I1_t0b_t1b = n245_O_t0b_t1b; // @[Top.scala 717:13]
  assign n246_I1_t1b_t0b = n245_O_t1b_t0b; // @[Top.scala 717:13]
  assign n246_I1_t1b_t1b = n245_O_t1b_t1b; // @[Top.scala 717:13]
  assign n247_valid_up = n246_valid_down; // @[Top.scala 721:19]
  assign n247_I_t0b = n246_O_t0b; // @[Top.scala 720:12]
  assign n247_I_t1b_t0b_t0b = n246_O_t1b_t0b_t0b; // @[Top.scala 720:12]
  assign n247_I_t1b_t0b_t1b = n246_O_t1b_t0b_t1b; // @[Top.scala 720:12]
  assign n247_I_t1b_t1b_t0b = n246_O_t1b_t1b_t0b; // @[Top.scala 720:12]
  assign n247_I_t1b_t1b_t1b = n246_O_t1b_t1b_t1b; // @[Top.scala 720:12]
  assign n249_valid_up = n248_valid_down & n247_valid_down; // @[Top.scala 725:19]
  assign n249_I0 = n248_O; // @[Top.scala 723:13]
  assign n249_I1_t0b = n247_O_t0b; // @[Top.scala 724:13]
  assign n249_I1_t1b = n247_O_t1b; // @[Top.scala 724:13]
endmodule
module MapS_6(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_6 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_6 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_6 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_6 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_6(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_6 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_21(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [4:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 5'h13; // @[InitialDelayCounter.scala 17:17]
  wire [4:0] _T_4 = value + 5'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 5'h13; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[4:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 5'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Module_7(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n254_valid_up; // @[Top.scala 733:22]
  wire  n254_valid_down; // @[Top.scala 733:22]
  wire [15:0] n254_I_t0b; // @[Top.scala 733:22]
  wire [15:0] n254_O; // @[Top.scala 733:22]
  wire  n287_clock; // @[Top.scala 736:22]
  wire  n287_reset; // @[Top.scala 736:22]
  wire  n287_valid_up; // @[Top.scala 736:22]
  wire  n287_valid_down; // @[Top.scala 736:22]
  wire [15:0] n287_I; // @[Top.scala 736:22]
  wire [15:0] n287_O; // @[Top.scala 736:22]
  wire  n275_clock; // @[Top.scala 739:22]
  wire  n275_reset; // @[Top.scala 739:22]
  wire  n275_valid_up; // @[Top.scala 739:22]
  wire  n275_valid_down; // @[Top.scala 739:22]
  wire [15:0] n275_I; // @[Top.scala 739:22]
  wire [15:0] n275_O; // @[Top.scala 739:22]
  wire  n255_valid_up; // @[Top.scala 742:22]
  wire  n255_valid_down; // @[Top.scala 742:22]
  wire [15:0] n255_I_t1b_t0b; // @[Top.scala 742:22]
  wire [15:0] n255_I_t1b_t1b; // @[Top.scala 742:22]
  wire [15:0] n255_O_t0b; // @[Top.scala 742:22]
  wire [15:0] n255_O_t1b; // @[Top.scala 742:22]
  wire  n256_valid_up; // @[Top.scala 745:22]
  wire  n256_valid_down; // @[Top.scala 745:22]
  wire [15:0] n256_I_t0b; // @[Top.scala 745:22]
  wire [15:0] n256_O; // @[Top.scala 745:22]
  wire  n257_valid_up; // @[Top.scala 748:22]
  wire  n257_valid_down; // @[Top.scala 748:22]
  wire [15:0] n257_I_t1b; // @[Top.scala 748:22]
  wire [15:0] n257_O; // @[Top.scala 748:22]
  wire  n258_valid_up; // @[Top.scala 751:22]
  wire  n258_valid_down; // @[Top.scala 751:22]
  wire [15:0] n258_I0; // @[Top.scala 751:22]
  wire [15:0] n258_I1; // @[Top.scala 751:22]
  wire [15:0] n258_O_t0b; // @[Top.scala 751:22]
  wire [15:0] n258_O_t1b; // @[Top.scala 751:22]
  wire  n259_valid_up; // @[Top.scala 755:22]
  wire  n259_valid_down; // @[Top.scala 755:22]
  wire [15:0] n259_I_t0b; // @[Top.scala 755:22]
  wire [15:0] n259_I_t1b; // @[Top.scala 755:22]
  wire [15:0] n259_O; // @[Top.scala 755:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n262_valid_up; // @[Top.scala 759:22]
  wire  n262_valid_down; // @[Top.scala 759:22]
  wire [15:0] n262_I0; // @[Top.scala 759:22]
  wire [15:0] n262_O_t0b; // @[Top.scala 759:22]
  wire  n263_valid_up; // @[Top.scala 763:22]
  wire  n263_valid_down; // @[Top.scala 763:22]
  wire [15:0] n263_I_t0b; // @[Top.scala 763:22]
  wire [15:0] n263_O; // @[Top.scala 763:22]
  wire  n264_valid_up; // @[Top.scala 766:22]
  wire  n264_valid_down; // @[Top.scala 766:22]
  wire [15:0] n264_I0; // @[Top.scala 766:22]
  wire [15:0] n264_I1; // @[Top.scala 766:22]
  wire [15:0] n264_O_t0b; // @[Top.scala 766:22]
  wire [15:0] n264_O_t1b; // @[Top.scala 766:22]
  wire  n265_valid_up; // @[Top.scala 770:22]
  wire  n265_valid_down; // @[Top.scala 770:22]
  wire [15:0] n265_I_t0b; // @[Top.scala 770:22]
  wire [15:0] n265_I_t1b; // @[Top.scala 770:22]
  wire [15:0] n265_O; // @[Top.scala 770:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n268_valid_up; // @[Top.scala 774:22]
  wire  n268_valid_down; // @[Top.scala 774:22]
  wire [15:0] n268_I0; // @[Top.scala 774:22]
  wire [15:0] n268_I1; // @[Top.scala 774:22]
  wire [15:0] n268_O_t0b; // @[Top.scala 774:22]
  wire [15:0] n268_O_t1b; // @[Top.scala 774:22]
  wire  n269_valid_up; // @[Top.scala 778:22]
  wire  n269_valid_down; // @[Top.scala 778:22]
  wire [15:0] n269_I_t0b; // @[Top.scala 778:22]
  wire [15:0] n269_I_t1b; // @[Top.scala 778:22]
  wire [15:0] n269_O; // @[Top.scala 778:22]
  wire  n270_valid_up; // @[Top.scala 781:22]
  wire  n270_valid_down; // @[Top.scala 781:22]
  wire [15:0] n270_I0; // @[Top.scala 781:22]
  wire [15:0] n270_I1; // @[Top.scala 781:22]
  wire [15:0] n270_O_t0b; // @[Top.scala 781:22]
  wire [15:0] n270_O_t1b; // @[Top.scala 781:22]
  wire  n271_valid_up; // @[Top.scala 785:22]
  wire  n271_valid_down; // @[Top.scala 785:22]
  wire  n271_I0; // @[Top.scala 785:22]
  wire [15:0] n271_I1_t0b; // @[Top.scala 785:22]
  wire [15:0] n271_I1_t1b; // @[Top.scala 785:22]
  wire  n271_O_t0b; // @[Top.scala 785:22]
  wire [15:0] n271_O_t1b_t0b; // @[Top.scala 785:22]
  wire [15:0] n271_O_t1b_t1b; // @[Top.scala 785:22]
  wire  n272_valid_up; // @[Top.scala 789:22]
  wire  n272_valid_down; // @[Top.scala 789:22]
  wire  n272_I_t0b; // @[Top.scala 789:22]
  wire [15:0] n272_I_t1b_t0b; // @[Top.scala 789:22]
  wire [15:0] n272_I_t1b_t1b; // @[Top.scala 789:22]
  wire [15:0] n272_O; // @[Top.scala 789:22]
  wire  n273_valid_up; // @[Top.scala 792:22]
  wire  n273_valid_down; // @[Top.scala 792:22]
  wire [15:0] n273_I0; // @[Top.scala 792:22]
  wire [15:0] n273_I1; // @[Top.scala 792:22]
  wire [15:0] n273_O_t0b; // @[Top.scala 792:22]
  wire [15:0] n273_O_t1b; // @[Top.scala 792:22]
  wire  n274_clock; // @[Top.scala 796:22]
  wire  n274_reset; // @[Top.scala 796:22]
  wire  n274_valid_up; // @[Top.scala 796:22]
  wire  n274_valid_down; // @[Top.scala 796:22]
  wire [15:0] n274_I_t0b; // @[Top.scala 796:22]
  wire [15:0] n274_I_t1b; // @[Top.scala 796:22]
  wire [15:0] n274_O; // @[Top.scala 796:22]
  wire  n276_valid_up; // @[Top.scala 799:22]
  wire  n276_valid_down; // @[Top.scala 799:22]
  wire [15:0] n276_I0; // @[Top.scala 799:22]
  wire [15:0] n276_I1; // @[Top.scala 799:22]
  wire [15:0] n276_O_t0b; // @[Top.scala 799:22]
  wire [15:0] n276_O_t1b; // @[Top.scala 799:22]
  wire  n277_valid_up; // @[Top.scala 803:22]
  wire  n277_valid_down; // @[Top.scala 803:22]
  wire [15:0] n277_I_t0b; // @[Top.scala 803:22]
  wire [15:0] n277_I_t1b; // @[Top.scala 803:22]
  wire [15:0] n277_O; // @[Top.scala 803:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n279_valid_up; // @[Top.scala 807:22]
  wire  n279_valid_down; // @[Top.scala 807:22]
  wire [15:0] n279_I0; // @[Top.scala 807:22]
  wire [15:0] n279_I1; // @[Top.scala 807:22]
  wire [15:0] n279_O_t0b; // @[Top.scala 807:22]
  wire [15:0] n279_O_t1b; // @[Top.scala 807:22]
  wire  n280_valid_up; // @[Top.scala 811:22]
  wire  n280_valid_down; // @[Top.scala 811:22]
  wire [15:0] n280_I_t0b; // @[Top.scala 811:22]
  wire [15:0] n280_I_t1b; // @[Top.scala 811:22]
  wire [15:0] n280_O; // @[Top.scala 811:22]
  wire  n281_valid_up; // @[Top.scala 814:22]
  wire  n281_valid_down; // @[Top.scala 814:22]
  wire [15:0] n281_I0; // @[Top.scala 814:22]
  wire [15:0] n281_I1; // @[Top.scala 814:22]
  wire [15:0] n281_O_t0b; // @[Top.scala 814:22]
  wire [15:0] n281_O_t1b; // @[Top.scala 814:22]
  wire  n282_valid_up; // @[Top.scala 818:22]
  wire  n282_valid_down; // @[Top.scala 818:22]
  wire [15:0] n282_I0; // @[Top.scala 818:22]
  wire [15:0] n282_I1; // @[Top.scala 818:22]
  wire [15:0] n282_O_t0b; // @[Top.scala 818:22]
  wire [15:0] n282_O_t1b; // @[Top.scala 818:22]
  wire  n283_valid_up; // @[Top.scala 822:22]
  wire  n283_valid_down; // @[Top.scala 822:22]
  wire [15:0] n283_I0_t0b; // @[Top.scala 822:22]
  wire [15:0] n283_I0_t1b; // @[Top.scala 822:22]
  wire [15:0] n283_I1_t0b; // @[Top.scala 822:22]
  wire [15:0] n283_I1_t1b; // @[Top.scala 822:22]
  wire [15:0] n283_O_t0b_t0b; // @[Top.scala 822:22]
  wire [15:0] n283_O_t0b_t1b; // @[Top.scala 822:22]
  wire [15:0] n283_O_t1b_t0b; // @[Top.scala 822:22]
  wire [15:0] n283_O_t1b_t1b; // @[Top.scala 822:22]
  wire  n284_clock; // @[Top.scala 826:22]
  wire  n284_reset; // @[Top.scala 826:22]
  wire  n284_valid_up; // @[Top.scala 826:22]
  wire  n284_valid_down; // @[Top.scala 826:22]
  wire [15:0] n284_I_t0b_t0b; // @[Top.scala 826:22]
  wire [15:0] n284_I_t0b_t1b; // @[Top.scala 826:22]
  wire [15:0] n284_I_t1b_t0b; // @[Top.scala 826:22]
  wire [15:0] n284_I_t1b_t1b; // @[Top.scala 826:22]
  wire [15:0] n284_O_t0b_t0b; // @[Top.scala 826:22]
  wire [15:0] n284_O_t0b_t1b; // @[Top.scala 826:22]
  wire [15:0] n284_O_t1b_t0b; // @[Top.scala 826:22]
  wire [15:0] n284_O_t1b_t1b; // @[Top.scala 826:22]
  wire  n285_valid_up; // @[Top.scala 829:22]
  wire  n285_valid_down; // @[Top.scala 829:22]
  wire  n285_I0; // @[Top.scala 829:22]
  wire [15:0] n285_I1_t0b_t0b; // @[Top.scala 829:22]
  wire [15:0] n285_I1_t0b_t1b; // @[Top.scala 829:22]
  wire [15:0] n285_I1_t1b_t0b; // @[Top.scala 829:22]
  wire [15:0] n285_I1_t1b_t1b; // @[Top.scala 829:22]
  wire  n285_O_t0b; // @[Top.scala 829:22]
  wire [15:0] n285_O_t1b_t0b_t0b; // @[Top.scala 829:22]
  wire [15:0] n285_O_t1b_t0b_t1b; // @[Top.scala 829:22]
  wire [15:0] n285_O_t1b_t1b_t0b; // @[Top.scala 829:22]
  wire [15:0] n285_O_t1b_t1b_t1b; // @[Top.scala 829:22]
  wire  n286_valid_up; // @[Top.scala 833:22]
  wire  n286_valid_down; // @[Top.scala 833:22]
  wire  n286_I_t0b; // @[Top.scala 833:22]
  wire [15:0] n286_I_t1b_t0b_t0b; // @[Top.scala 833:22]
  wire [15:0] n286_I_t1b_t0b_t1b; // @[Top.scala 833:22]
  wire [15:0] n286_I_t1b_t1b_t0b; // @[Top.scala 833:22]
  wire [15:0] n286_I_t1b_t1b_t1b; // @[Top.scala 833:22]
  wire [15:0] n286_O_t0b; // @[Top.scala 833:22]
  wire [15:0] n286_O_t1b; // @[Top.scala 833:22]
  wire  n288_valid_up; // @[Top.scala 836:22]
  wire  n288_valid_down; // @[Top.scala 836:22]
  wire [15:0] n288_I0; // @[Top.scala 836:22]
  wire [15:0] n288_I1_t0b; // @[Top.scala 836:22]
  wire [15:0] n288_I1_t1b; // @[Top.scala 836:22]
  wire [15:0] n288_O_t0b; // @[Top.scala 836:22]
  wire [15:0] n288_O_t1b_t0b; // @[Top.scala 836:22]
  wire [15:0] n288_O_t1b_t1b; // @[Top.scala 836:22]
  Fst n254 ( // @[Top.scala 733:22]
    .valid_up(n254_valid_up),
    .valid_down(n254_valid_down),
    .I_t0b(n254_I_t0b),
    .O(n254_O)
  );
  FIFO_1 n287 ( // @[Top.scala 736:22]
    .clock(n287_clock),
    .reset(n287_reset),
    .valid_up(n287_valid_up),
    .valid_down(n287_valid_down),
    .I(n287_I),
    .O(n287_O)
  );
  FIFO_1 n275 ( // @[Top.scala 739:22]
    .clock(n275_clock),
    .reset(n275_reset),
    .valid_up(n275_valid_up),
    .valid_down(n275_valid_down),
    .I(n275_I),
    .O(n275_O)
  );
  Snd n255 ( // @[Top.scala 742:22]
    .valid_up(n255_valid_up),
    .valid_down(n255_valid_down),
    .I_t1b_t0b(n255_I_t1b_t0b),
    .I_t1b_t1b(n255_I_t1b_t1b),
    .O_t0b(n255_O_t0b),
    .O_t1b(n255_O_t1b)
  );
  Fst_1 n256 ( // @[Top.scala 745:22]
    .valid_up(n256_valid_up),
    .valid_down(n256_valid_down),
    .I_t0b(n256_I_t0b),
    .O(n256_O)
  );
  Snd_1 n257 ( // @[Top.scala 748:22]
    .valid_up(n257_valid_up),
    .valid_down(n257_valid_down),
    .I_t1b(n257_I_t1b),
    .O(n257_O)
  );
  AtomTuple_1 n258 ( // @[Top.scala 751:22]
    .valid_up(n258_valid_up),
    .valid_down(n258_valid_down),
    .I0(n258_I0),
    .I1(n258_I1),
    .O_t0b(n258_O_t0b),
    .O_t1b(n258_O_t1b)
  );
  Add n259 ( // @[Top.scala 755:22]
    .valid_up(n259_valid_up),
    .valid_down(n259_valid_down),
    .I_t0b(n259_I_t0b),
    .I_t1b(n259_I_t1b),
    .O(n259_O)
  );
  InitialDelayCounter_21 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n262 ( // @[Top.scala 759:22]
    .valid_up(n262_valid_up),
    .valid_down(n262_valid_down),
    .I0(n262_I0),
    .O_t0b(n262_O_t0b)
  );
  RShift n263 ( // @[Top.scala 763:22]
    .valid_up(n263_valid_up),
    .valid_down(n263_valid_down),
    .I_t0b(n263_I_t0b),
    .O(n263_O)
  );
  AtomTuple_1 n264 ( // @[Top.scala 766:22]
    .valid_up(n264_valid_up),
    .valid_down(n264_valid_down),
    .I0(n264_I0),
    .I1(n264_I1),
    .O_t0b(n264_O_t0b),
    .O_t1b(n264_O_t1b)
  );
  Eq n265 ( // @[Top.scala 770:22]
    .valid_up(n265_valid_up),
    .valid_down(n265_valid_down),
    .I_t0b(n265_I_t0b),
    .I_t1b(n265_I_t1b),
    .O(n265_O)
  );
  InitialDelayCounter_21 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n268 ( // @[Top.scala 774:22]
    .valid_up(n268_valid_up),
    .valid_down(n268_valid_down),
    .I0(n268_I0),
    .I1(n268_I1),
    .O_t0b(n268_O_t0b),
    .O_t1b(n268_O_t1b)
  );
  Add n269 ( // @[Top.scala 778:22]
    .valid_up(n269_valid_up),
    .valid_down(n269_valid_down),
    .I_t0b(n269_I_t0b),
    .I_t1b(n269_I_t1b),
    .O(n269_O)
  );
  AtomTuple_1 n270 ( // @[Top.scala 781:22]
    .valid_up(n270_valid_up),
    .valid_down(n270_valid_down),
    .I0(n270_I0),
    .I1(n270_I1),
    .O_t0b(n270_O_t0b),
    .O_t1b(n270_O_t1b)
  );
  AtomTuple_9 n271 ( // @[Top.scala 785:22]
    .valid_up(n271_valid_up),
    .valid_down(n271_valid_down),
    .I0(n271_I0),
    .I1_t0b(n271_I1_t0b),
    .I1_t1b(n271_I1_t1b),
    .O_t0b(n271_O_t0b),
    .O_t1b_t0b(n271_O_t1b_t0b),
    .O_t1b_t1b(n271_O_t1b_t1b)
  );
  If n272 ( // @[Top.scala 789:22]
    .valid_up(n272_valid_up),
    .valid_down(n272_valid_down),
    .I_t0b(n272_I_t0b),
    .I_t1b_t0b(n272_I_t1b_t0b),
    .I_t1b_t1b(n272_I_t1b_t1b),
    .O(n272_O)
  );
  AtomTuple_1 n273 ( // @[Top.scala 792:22]
    .valid_up(n273_valid_up),
    .valid_down(n273_valid_down),
    .I0(n273_I0),
    .I1(n273_I1),
    .O_t0b(n273_O_t0b),
    .O_t1b(n273_O_t1b)
  );
  Mul n274 ( // @[Top.scala 796:22]
    .clock(n274_clock),
    .reset(n274_reset),
    .valid_up(n274_valid_up),
    .valid_down(n274_valid_down),
    .I_t0b(n274_I_t0b),
    .I_t1b(n274_I_t1b),
    .O(n274_O)
  );
  AtomTuple_1 n276 ( // @[Top.scala 799:22]
    .valid_up(n276_valid_up),
    .valid_down(n276_valid_down),
    .I0(n276_I0),
    .I1(n276_I1),
    .O_t0b(n276_O_t0b),
    .O_t1b(n276_O_t1b)
  );
  Lt n277 ( // @[Top.scala 803:22]
    .valid_up(n277_valid_up),
    .valid_down(n277_valid_down),
    .I_t0b(n277_I_t0b),
    .I_t1b(n277_I_t1b),
    .O(n277_O)
  );
  InitialDelayCounter_21 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n279 ( // @[Top.scala 807:22]
    .valid_up(n279_valid_up),
    .valid_down(n279_valid_down),
    .I0(n279_I0),
    .I1(n279_I1),
    .O_t0b(n279_O_t0b),
    .O_t1b(n279_O_t1b)
  );
  Sub n280 ( // @[Top.scala 811:22]
    .valid_up(n280_valid_up),
    .valid_down(n280_valid_down),
    .I_t0b(n280_I_t0b),
    .I_t1b(n280_I_t1b),
    .O(n280_O)
  );
  AtomTuple_1 n281 ( // @[Top.scala 814:22]
    .valid_up(n281_valid_up),
    .valid_down(n281_valid_down),
    .I0(n281_I0),
    .I1(n281_I1),
    .O_t0b(n281_O_t0b),
    .O_t1b(n281_O_t1b)
  );
  AtomTuple_1 n282 ( // @[Top.scala 818:22]
    .valid_up(n282_valid_up),
    .valid_down(n282_valid_down),
    .I0(n282_I0),
    .I1(n282_I1),
    .O_t0b(n282_O_t0b),
    .O_t1b(n282_O_t1b)
  );
  AtomTuple_15 n283 ( // @[Top.scala 822:22]
    .valid_up(n283_valid_up),
    .valid_down(n283_valid_down),
    .I0_t0b(n283_I0_t0b),
    .I0_t1b(n283_I0_t1b),
    .I1_t0b(n283_I1_t0b),
    .I1_t1b(n283_I1_t1b),
    .O_t0b_t0b(n283_O_t0b_t0b),
    .O_t0b_t1b(n283_O_t0b_t1b),
    .O_t1b_t0b(n283_O_t1b_t0b),
    .O_t1b_t1b(n283_O_t1b_t1b)
  );
  FIFO_3 n284 ( // @[Top.scala 826:22]
    .clock(n284_clock),
    .reset(n284_reset),
    .valid_up(n284_valid_up),
    .valid_down(n284_valid_down),
    .I_t0b_t0b(n284_I_t0b_t0b),
    .I_t0b_t1b(n284_I_t0b_t1b),
    .I_t1b_t0b(n284_I_t1b_t0b),
    .I_t1b_t1b(n284_I_t1b_t1b),
    .O_t0b_t0b(n284_O_t0b_t0b),
    .O_t0b_t1b(n284_O_t0b_t1b),
    .O_t1b_t0b(n284_O_t1b_t0b),
    .O_t1b_t1b(n284_O_t1b_t1b)
  );
  AtomTuple_16 n285 ( // @[Top.scala 829:22]
    .valid_up(n285_valid_up),
    .valid_down(n285_valid_down),
    .I0(n285_I0),
    .I1_t0b_t0b(n285_I1_t0b_t0b),
    .I1_t0b_t1b(n285_I1_t0b_t1b),
    .I1_t1b_t0b(n285_I1_t1b_t0b),
    .I1_t1b_t1b(n285_I1_t1b_t1b),
    .O_t0b(n285_O_t0b),
    .O_t1b_t0b_t0b(n285_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n285_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n285_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n285_O_t1b_t1b_t1b)
  );
  If_1 n286 ( // @[Top.scala 833:22]
    .valid_up(n286_valid_up),
    .valid_down(n286_valid_down),
    .I_t0b(n286_I_t0b),
    .I_t1b_t0b_t0b(n286_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n286_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n286_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n286_I_t1b_t1b_t1b),
    .O_t0b(n286_O_t0b),
    .O_t1b(n286_O_t1b)
  );
  AtomTuple_3 n288 ( // @[Top.scala 836:22]
    .valid_up(n288_valid_up),
    .valid_down(n288_valid_down),
    .I0(n288_I0),
    .I1_t0b(n288_I1_t0b),
    .I1_t1b(n288_I1_t1b),
    .O_t0b(n288_O_t0b),
    .O_t1b_t0b(n288_O_t1b_t0b),
    .O_t1b_t1b(n288_O_t1b_t1b)
  );
  assign valid_down = n288_valid_down; // @[Top.scala 841:16]
  assign O_t0b = n288_O_t0b; // @[Top.scala 840:7]
  assign O_t1b_t0b = n288_O_t1b_t0b; // @[Top.scala 840:7]
  assign O_t1b_t1b = n288_O_t1b_t1b; // @[Top.scala 840:7]
  assign n254_valid_up = valid_up; // @[Top.scala 735:19]
  assign n254_I_t0b = I_t0b; // @[Top.scala 734:12]
  assign n287_clock = clock;
  assign n287_reset = reset;
  assign n287_valid_up = n254_valid_down; // @[Top.scala 738:19]
  assign n287_I = n254_O; // @[Top.scala 737:12]
  assign n275_clock = clock;
  assign n275_reset = reset;
  assign n275_valid_up = n254_valid_down; // @[Top.scala 741:19]
  assign n275_I = n254_O; // @[Top.scala 740:12]
  assign n255_valid_up = valid_up; // @[Top.scala 744:19]
  assign n255_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 743:12]
  assign n255_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 743:12]
  assign n256_valid_up = n255_valid_down; // @[Top.scala 747:19]
  assign n256_I_t0b = n255_O_t0b; // @[Top.scala 746:12]
  assign n257_valid_up = n255_valid_down; // @[Top.scala 750:19]
  assign n257_I_t1b = n255_O_t1b; // @[Top.scala 749:12]
  assign n258_valid_up = n256_valid_down & n257_valid_down; // @[Top.scala 754:19]
  assign n258_I0 = n256_O; // @[Top.scala 752:13]
  assign n258_I1 = n257_O; // @[Top.scala 753:13]
  assign n259_valid_up = n258_valid_down; // @[Top.scala 757:19]
  assign n259_I_t0b = n258_O_t0b; // @[Top.scala 756:12]
  assign n259_I_t1b = n258_O_t1b; // @[Top.scala 756:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n262_valid_up = n259_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 762:19]
  assign n262_I0 = n259_O; // @[Top.scala 760:13]
  assign n263_valid_up = n262_valid_down; // @[Top.scala 765:19]
  assign n263_I_t0b = n262_O_t0b; // @[Top.scala 764:12]
  assign n264_valid_up = n263_valid_down & n256_valid_down; // @[Top.scala 769:19]
  assign n264_I0 = n263_O; // @[Top.scala 767:13]
  assign n264_I1 = n256_O; // @[Top.scala 768:13]
  assign n265_valid_up = n264_valid_down; // @[Top.scala 772:19]
  assign n265_I_t0b = n264_O_t0b; // @[Top.scala 771:12]
  assign n265_I_t1b = n264_O_t1b; // @[Top.scala 771:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n268_valid_up = n263_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 777:19]
  assign n268_I0 = n263_O; // @[Top.scala 775:13]
  assign n268_I1 = 16'h1; // @[Top.scala 776:13]
  assign n269_valid_up = n268_valid_down; // @[Top.scala 780:19]
  assign n269_I_t0b = n268_O_t0b; // @[Top.scala 779:12]
  assign n269_I_t1b = n268_O_t1b; // @[Top.scala 779:12]
  assign n270_valid_up = n269_valid_down & n263_valid_down; // @[Top.scala 784:19]
  assign n270_I0 = n269_O; // @[Top.scala 782:13]
  assign n270_I1 = n263_O; // @[Top.scala 783:13]
  assign n271_valid_up = n265_valid_down & n270_valid_down; // @[Top.scala 788:19]
  assign n271_I0 = n265_O[0]; // @[Top.scala 786:13]
  assign n271_I1_t0b = n270_O_t0b; // @[Top.scala 787:13]
  assign n271_I1_t1b = n270_O_t1b; // @[Top.scala 787:13]
  assign n272_valid_up = n271_valid_down; // @[Top.scala 791:19]
  assign n272_I_t0b = n271_O_t0b; // @[Top.scala 790:12]
  assign n272_I_t1b_t0b = n271_O_t1b_t0b; // @[Top.scala 790:12]
  assign n272_I_t1b_t1b = n271_O_t1b_t1b; // @[Top.scala 790:12]
  assign n273_valid_up = n272_valid_down; // @[Top.scala 795:19]
  assign n273_I0 = n272_O; // @[Top.scala 793:13]
  assign n273_I1 = n272_O; // @[Top.scala 794:13]
  assign n274_clock = clock;
  assign n274_reset = reset;
  assign n274_valid_up = n273_valid_down; // @[Top.scala 798:19]
  assign n274_I_t0b = n273_O_t0b; // @[Top.scala 797:12]
  assign n274_I_t1b = n273_O_t1b; // @[Top.scala 797:12]
  assign n276_valid_up = n275_valid_down & n274_valid_down; // @[Top.scala 802:19]
  assign n276_I0 = n275_O; // @[Top.scala 800:13]
  assign n276_I1 = n274_O; // @[Top.scala 801:13]
  assign n277_valid_up = n276_valid_down; // @[Top.scala 805:19]
  assign n277_I_t0b = n276_O_t0b; // @[Top.scala 804:12]
  assign n277_I_t1b = n276_O_t1b; // @[Top.scala 804:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n279_valid_up = n272_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 810:19]
  assign n279_I0 = n272_O; // @[Top.scala 808:13]
  assign n279_I1 = 16'h1; // @[Top.scala 809:13]
  assign n280_valid_up = n279_valid_down; // @[Top.scala 813:19]
  assign n280_I_t0b = n279_O_t0b; // @[Top.scala 812:12]
  assign n280_I_t1b = n279_O_t1b; // @[Top.scala 812:12]
  assign n281_valid_up = n256_valid_down & n280_valid_down; // @[Top.scala 817:19]
  assign n281_I0 = n256_O; // @[Top.scala 815:13]
  assign n281_I1 = n280_O; // @[Top.scala 816:13]
  assign n282_valid_up = n272_valid_down & n257_valid_down; // @[Top.scala 821:19]
  assign n282_I0 = n272_O; // @[Top.scala 819:13]
  assign n282_I1 = n257_O; // @[Top.scala 820:13]
  assign n283_valid_up = n281_valid_down & n282_valid_down; // @[Top.scala 825:19]
  assign n283_I0_t0b = n281_O_t0b; // @[Top.scala 823:13]
  assign n283_I0_t1b = n281_O_t1b; // @[Top.scala 823:13]
  assign n283_I1_t0b = n282_O_t0b; // @[Top.scala 824:13]
  assign n283_I1_t1b = n282_O_t1b; // @[Top.scala 824:13]
  assign n284_clock = clock;
  assign n284_reset = reset;
  assign n284_valid_up = n283_valid_down; // @[Top.scala 828:19]
  assign n284_I_t0b_t0b = n283_O_t0b_t0b; // @[Top.scala 827:12]
  assign n284_I_t0b_t1b = n283_O_t0b_t1b; // @[Top.scala 827:12]
  assign n284_I_t1b_t0b = n283_O_t1b_t0b; // @[Top.scala 827:12]
  assign n284_I_t1b_t1b = n283_O_t1b_t1b; // @[Top.scala 827:12]
  assign n285_valid_up = n277_valid_down & n284_valid_down; // @[Top.scala 832:19]
  assign n285_I0 = n277_O[0]; // @[Top.scala 830:13]
  assign n285_I1_t0b_t0b = n284_O_t0b_t0b; // @[Top.scala 831:13]
  assign n285_I1_t0b_t1b = n284_O_t0b_t1b; // @[Top.scala 831:13]
  assign n285_I1_t1b_t0b = n284_O_t1b_t0b; // @[Top.scala 831:13]
  assign n285_I1_t1b_t1b = n284_O_t1b_t1b; // @[Top.scala 831:13]
  assign n286_valid_up = n285_valid_down; // @[Top.scala 835:19]
  assign n286_I_t0b = n285_O_t0b; // @[Top.scala 834:12]
  assign n286_I_t1b_t0b_t0b = n285_O_t1b_t0b_t0b; // @[Top.scala 834:12]
  assign n286_I_t1b_t0b_t1b = n285_O_t1b_t0b_t1b; // @[Top.scala 834:12]
  assign n286_I_t1b_t1b_t0b = n285_O_t1b_t1b_t0b; // @[Top.scala 834:12]
  assign n286_I_t1b_t1b_t1b = n285_O_t1b_t1b_t1b; // @[Top.scala 834:12]
  assign n288_valid_up = n287_valid_down & n286_valid_down; // @[Top.scala 839:19]
  assign n288_I0 = n287_O; // @[Top.scala 837:13]
  assign n288_I1_t0b = n286_O_t0b; // @[Top.scala 838:13]
  assign n288_I1_t1b = n286_O_t1b; // @[Top.scala 838:13]
endmodule
module MapS_7(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_7 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_7 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_7 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_7 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_7(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_7 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_24(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [4:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 5'h16; // @[InitialDelayCounter.scala 17:17]
  wire [4:0] _T_4 = value + 5'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 5'h16; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[4:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 5'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Module_8(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n293_valid_up; // @[Top.scala 847:22]
  wire  n293_valid_down; // @[Top.scala 847:22]
  wire [15:0] n293_I_t0b; // @[Top.scala 847:22]
  wire [15:0] n293_O; // @[Top.scala 847:22]
  wire  n326_clock; // @[Top.scala 850:22]
  wire  n326_reset; // @[Top.scala 850:22]
  wire  n326_valid_up; // @[Top.scala 850:22]
  wire  n326_valid_down; // @[Top.scala 850:22]
  wire [15:0] n326_I; // @[Top.scala 850:22]
  wire [15:0] n326_O; // @[Top.scala 850:22]
  wire  n314_clock; // @[Top.scala 853:22]
  wire  n314_reset; // @[Top.scala 853:22]
  wire  n314_valid_up; // @[Top.scala 853:22]
  wire  n314_valid_down; // @[Top.scala 853:22]
  wire [15:0] n314_I; // @[Top.scala 853:22]
  wire [15:0] n314_O; // @[Top.scala 853:22]
  wire  n294_valid_up; // @[Top.scala 856:22]
  wire  n294_valid_down; // @[Top.scala 856:22]
  wire [15:0] n294_I_t1b_t0b; // @[Top.scala 856:22]
  wire [15:0] n294_I_t1b_t1b; // @[Top.scala 856:22]
  wire [15:0] n294_O_t0b; // @[Top.scala 856:22]
  wire [15:0] n294_O_t1b; // @[Top.scala 856:22]
  wire  n295_valid_up; // @[Top.scala 859:22]
  wire  n295_valid_down; // @[Top.scala 859:22]
  wire [15:0] n295_I_t0b; // @[Top.scala 859:22]
  wire [15:0] n295_O; // @[Top.scala 859:22]
  wire  n296_valid_up; // @[Top.scala 862:22]
  wire  n296_valid_down; // @[Top.scala 862:22]
  wire [15:0] n296_I_t1b; // @[Top.scala 862:22]
  wire [15:0] n296_O; // @[Top.scala 862:22]
  wire  n297_valid_up; // @[Top.scala 865:22]
  wire  n297_valid_down; // @[Top.scala 865:22]
  wire [15:0] n297_I0; // @[Top.scala 865:22]
  wire [15:0] n297_I1; // @[Top.scala 865:22]
  wire [15:0] n297_O_t0b; // @[Top.scala 865:22]
  wire [15:0] n297_O_t1b; // @[Top.scala 865:22]
  wire  n298_valid_up; // @[Top.scala 869:22]
  wire  n298_valid_down; // @[Top.scala 869:22]
  wire [15:0] n298_I_t0b; // @[Top.scala 869:22]
  wire [15:0] n298_I_t1b; // @[Top.scala 869:22]
  wire [15:0] n298_O; // @[Top.scala 869:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n301_valid_up; // @[Top.scala 873:22]
  wire  n301_valid_down; // @[Top.scala 873:22]
  wire [15:0] n301_I0; // @[Top.scala 873:22]
  wire [15:0] n301_O_t0b; // @[Top.scala 873:22]
  wire  n302_valid_up; // @[Top.scala 877:22]
  wire  n302_valid_down; // @[Top.scala 877:22]
  wire [15:0] n302_I_t0b; // @[Top.scala 877:22]
  wire [15:0] n302_O; // @[Top.scala 877:22]
  wire  n303_valid_up; // @[Top.scala 880:22]
  wire  n303_valid_down; // @[Top.scala 880:22]
  wire [15:0] n303_I0; // @[Top.scala 880:22]
  wire [15:0] n303_I1; // @[Top.scala 880:22]
  wire [15:0] n303_O_t0b; // @[Top.scala 880:22]
  wire [15:0] n303_O_t1b; // @[Top.scala 880:22]
  wire  n304_valid_up; // @[Top.scala 884:22]
  wire  n304_valid_down; // @[Top.scala 884:22]
  wire [15:0] n304_I_t0b; // @[Top.scala 884:22]
  wire [15:0] n304_I_t1b; // @[Top.scala 884:22]
  wire [15:0] n304_O; // @[Top.scala 884:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n307_valid_up; // @[Top.scala 888:22]
  wire  n307_valid_down; // @[Top.scala 888:22]
  wire [15:0] n307_I0; // @[Top.scala 888:22]
  wire [15:0] n307_I1; // @[Top.scala 888:22]
  wire [15:0] n307_O_t0b; // @[Top.scala 888:22]
  wire [15:0] n307_O_t1b; // @[Top.scala 888:22]
  wire  n308_valid_up; // @[Top.scala 892:22]
  wire  n308_valid_down; // @[Top.scala 892:22]
  wire [15:0] n308_I_t0b; // @[Top.scala 892:22]
  wire [15:0] n308_I_t1b; // @[Top.scala 892:22]
  wire [15:0] n308_O; // @[Top.scala 892:22]
  wire  n309_valid_up; // @[Top.scala 895:22]
  wire  n309_valid_down; // @[Top.scala 895:22]
  wire [15:0] n309_I0; // @[Top.scala 895:22]
  wire [15:0] n309_I1; // @[Top.scala 895:22]
  wire [15:0] n309_O_t0b; // @[Top.scala 895:22]
  wire [15:0] n309_O_t1b; // @[Top.scala 895:22]
  wire  n310_valid_up; // @[Top.scala 899:22]
  wire  n310_valid_down; // @[Top.scala 899:22]
  wire  n310_I0; // @[Top.scala 899:22]
  wire [15:0] n310_I1_t0b; // @[Top.scala 899:22]
  wire [15:0] n310_I1_t1b; // @[Top.scala 899:22]
  wire  n310_O_t0b; // @[Top.scala 899:22]
  wire [15:0] n310_O_t1b_t0b; // @[Top.scala 899:22]
  wire [15:0] n310_O_t1b_t1b; // @[Top.scala 899:22]
  wire  n311_valid_up; // @[Top.scala 903:22]
  wire  n311_valid_down; // @[Top.scala 903:22]
  wire  n311_I_t0b; // @[Top.scala 903:22]
  wire [15:0] n311_I_t1b_t0b; // @[Top.scala 903:22]
  wire [15:0] n311_I_t1b_t1b; // @[Top.scala 903:22]
  wire [15:0] n311_O; // @[Top.scala 903:22]
  wire  n312_valid_up; // @[Top.scala 906:22]
  wire  n312_valid_down; // @[Top.scala 906:22]
  wire [15:0] n312_I0; // @[Top.scala 906:22]
  wire [15:0] n312_I1; // @[Top.scala 906:22]
  wire [15:0] n312_O_t0b; // @[Top.scala 906:22]
  wire [15:0] n312_O_t1b; // @[Top.scala 906:22]
  wire  n313_clock; // @[Top.scala 910:22]
  wire  n313_reset; // @[Top.scala 910:22]
  wire  n313_valid_up; // @[Top.scala 910:22]
  wire  n313_valid_down; // @[Top.scala 910:22]
  wire [15:0] n313_I_t0b; // @[Top.scala 910:22]
  wire [15:0] n313_I_t1b; // @[Top.scala 910:22]
  wire [15:0] n313_O; // @[Top.scala 910:22]
  wire  n315_valid_up; // @[Top.scala 913:22]
  wire  n315_valid_down; // @[Top.scala 913:22]
  wire [15:0] n315_I0; // @[Top.scala 913:22]
  wire [15:0] n315_I1; // @[Top.scala 913:22]
  wire [15:0] n315_O_t0b; // @[Top.scala 913:22]
  wire [15:0] n315_O_t1b; // @[Top.scala 913:22]
  wire  n316_valid_up; // @[Top.scala 917:22]
  wire  n316_valid_down; // @[Top.scala 917:22]
  wire [15:0] n316_I_t0b; // @[Top.scala 917:22]
  wire [15:0] n316_I_t1b; // @[Top.scala 917:22]
  wire [15:0] n316_O; // @[Top.scala 917:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n318_valid_up; // @[Top.scala 921:22]
  wire  n318_valid_down; // @[Top.scala 921:22]
  wire [15:0] n318_I0; // @[Top.scala 921:22]
  wire [15:0] n318_I1; // @[Top.scala 921:22]
  wire [15:0] n318_O_t0b; // @[Top.scala 921:22]
  wire [15:0] n318_O_t1b; // @[Top.scala 921:22]
  wire  n319_valid_up; // @[Top.scala 925:22]
  wire  n319_valid_down; // @[Top.scala 925:22]
  wire [15:0] n319_I_t0b; // @[Top.scala 925:22]
  wire [15:0] n319_I_t1b; // @[Top.scala 925:22]
  wire [15:0] n319_O; // @[Top.scala 925:22]
  wire  n320_valid_up; // @[Top.scala 928:22]
  wire  n320_valid_down; // @[Top.scala 928:22]
  wire [15:0] n320_I0; // @[Top.scala 928:22]
  wire [15:0] n320_I1; // @[Top.scala 928:22]
  wire [15:0] n320_O_t0b; // @[Top.scala 928:22]
  wire [15:0] n320_O_t1b; // @[Top.scala 928:22]
  wire  n321_valid_up; // @[Top.scala 932:22]
  wire  n321_valid_down; // @[Top.scala 932:22]
  wire [15:0] n321_I0; // @[Top.scala 932:22]
  wire [15:0] n321_I1; // @[Top.scala 932:22]
  wire [15:0] n321_O_t0b; // @[Top.scala 932:22]
  wire [15:0] n321_O_t1b; // @[Top.scala 932:22]
  wire  n322_valid_up; // @[Top.scala 936:22]
  wire  n322_valid_down; // @[Top.scala 936:22]
  wire [15:0] n322_I0_t0b; // @[Top.scala 936:22]
  wire [15:0] n322_I0_t1b; // @[Top.scala 936:22]
  wire [15:0] n322_I1_t0b; // @[Top.scala 936:22]
  wire [15:0] n322_I1_t1b; // @[Top.scala 936:22]
  wire [15:0] n322_O_t0b_t0b; // @[Top.scala 936:22]
  wire [15:0] n322_O_t0b_t1b; // @[Top.scala 936:22]
  wire [15:0] n322_O_t1b_t0b; // @[Top.scala 936:22]
  wire [15:0] n322_O_t1b_t1b; // @[Top.scala 936:22]
  wire  n323_clock; // @[Top.scala 940:22]
  wire  n323_reset; // @[Top.scala 940:22]
  wire  n323_valid_up; // @[Top.scala 940:22]
  wire  n323_valid_down; // @[Top.scala 940:22]
  wire [15:0] n323_I_t0b_t0b; // @[Top.scala 940:22]
  wire [15:0] n323_I_t0b_t1b; // @[Top.scala 940:22]
  wire [15:0] n323_I_t1b_t0b; // @[Top.scala 940:22]
  wire [15:0] n323_I_t1b_t1b; // @[Top.scala 940:22]
  wire [15:0] n323_O_t0b_t0b; // @[Top.scala 940:22]
  wire [15:0] n323_O_t0b_t1b; // @[Top.scala 940:22]
  wire [15:0] n323_O_t1b_t0b; // @[Top.scala 940:22]
  wire [15:0] n323_O_t1b_t1b; // @[Top.scala 940:22]
  wire  n324_valid_up; // @[Top.scala 943:22]
  wire  n324_valid_down; // @[Top.scala 943:22]
  wire  n324_I0; // @[Top.scala 943:22]
  wire [15:0] n324_I1_t0b_t0b; // @[Top.scala 943:22]
  wire [15:0] n324_I1_t0b_t1b; // @[Top.scala 943:22]
  wire [15:0] n324_I1_t1b_t0b; // @[Top.scala 943:22]
  wire [15:0] n324_I1_t1b_t1b; // @[Top.scala 943:22]
  wire  n324_O_t0b; // @[Top.scala 943:22]
  wire [15:0] n324_O_t1b_t0b_t0b; // @[Top.scala 943:22]
  wire [15:0] n324_O_t1b_t0b_t1b; // @[Top.scala 943:22]
  wire [15:0] n324_O_t1b_t1b_t0b; // @[Top.scala 943:22]
  wire [15:0] n324_O_t1b_t1b_t1b; // @[Top.scala 943:22]
  wire  n325_valid_up; // @[Top.scala 947:22]
  wire  n325_valid_down; // @[Top.scala 947:22]
  wire  n325_I_t0b; // @[Top.scala 947:22]
  wire [15:0] n325_I_t1b_t0b_t0b; // @[Top.scala 947:22]
  wire [15:0] n325_I_t1b_t0b_t1b; // @[Top.scala 947:22]
  wire [15:0] n325_I_t1b_t1b_t0b; // @[Top.scala 947:22]
  wire [15:0] n325_I_t1b_t1b_t1b; // @[Top.scala 947:22]
  wire [15:0] n325_O_t0b; // @[Top.scala 947:22]
  wire [15:0] n325_O_t1b; // @[Top.scala 947:22]
  wire  n327_valid_up; // @[Top.scala 950:22]
  wire  n327_valid_down; // @[Top.scala 950:22]
  wire [15:0] n327_I0; // @[Top.scala 950:22]
  wire [15:0] n327_I1_t0b; // @[Top.scala 950:22]
  wire [15:0] n327_I1_t1b; // @[Top.scala 950:22]
  wire [15:0] n327_O_t0b; // @[Top.scala 950:22]
  wire [15:0] n327_O_t1b_t0b; // @[Top.scala 950:22]
  wire [15:0] n327_O_t1b_t1b; // @[Top.scala 950:22]
  Fst n293 ( // @[Top.scala 847:22]
    .valid_up(n293_valid_up),
    .valid_down(n293_valid_down),
    .I_t0b(n293_I_t0b),
    .O(n293_O)
  );
  FIFO_1 n326 ( // @[Top.scala 850:22]
    .clock(n326_clock),
    .reset(n326_reset),
    .valid_up(n326_valid_up),
    .valid_down(n326_valid_down),
    .I(n326_I),
    .O(n326_O)
  );
  FIFO_1 n314 ( // @[Top.scala 853:22]
    .clock(n314_clock),
    .reset(n314_reset),
    .valid_up(n314_valid_up),
    .valid_down(n314_valid_down),
    .I(n314_I),
    .O(n314_O)
  );
  Snd n294 ( // @[Top.scala 856:22]
    .valid_up(n294_valid_up),
    .valid_down(n294_valid_down),
    .I_t1b_t0b(n294_I_t1b_t0b),
    .I_t1b_t1b(n294_I_t1b_t1b),
    .O_t0b(n294_O_t0b),
    .O_t1b(n294_O_t1b)
  );
  Fst_1 n295 ( // @[Top.scala 859:22]
    .valid_up(n295_valid_up),
    .valid_down(n295_valid_down),
    .I_t0b(n295_I_t0b),
    .O(n295_O)
  );
  Snd_1 n296 ( // @[Top.scala 862:22]
    .valid_up(n296_valid_up),
    .valid_down(n296_valid_down),
    .I_t1b(n296_I_t1b),
    .O(n296_O)
  );
  AtomTuple_1 n297 ( // @[Top.scala 865:22]
    .valid_up(n297_valid_up),
    .valid_down(n297_valid_down),
    .I0(n297_I0),
    .I1(n297_I1),
    .O_t0b(n297_O_t0b),
    .O_t1b(n297_O_t1b)
  );
  Add n298 ( // @[Top.scala 869:22]
    .valid_up(n298_valid_up),
    .valid_down(n298_valid_down),
    .I_t0b(n298_I_t0b),
    .I_t1b(n298_I_t1b),
    .O(n298_O)
  );
  InitialDelayCounter_24 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n301 ( // @[Top.scala 873:22]
    .valid_up(n301_valid_up),
    .valid_down(n301_valid_down),
    .I0(n301_I0),
    .O_t0b(n301_O_t0b)
  );
  RShift n302 ( // @[Top.scala 877:22]
    .valid_up(n302_valid_up),
    .valid_down(n302_valid_down),
    .I_t0b(n302_I_t0b),
    .O(n302_O)
  );
  AtomTuple_1 n303 ( // @[Top.scala 880:22]
    .valid_up(n303_valid_up),
    .valid_down(n303_valid_down),
    .I0(n303_I0),
    .I1(n303_I1),
    .O_t0b(n303_O_t0b),
    .O_t1b(n303_O_t1b)
  );
  Eq n304 ( // @[Top.scala 884:22]
    .valid_up(n304_valid_up),
    .valid_down(n304_valid_down),
    .I_t0b(n304_I_t0b),
    .I_t1b(n304_I_t1b),
    .O(n304_O)
  );
  InitialDelayCounter_24 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n307 ( // @[Top.scala 888:22]
    .valid_up(n307_valid_up),
    .valid_down(n307_valid_down),
    .I0(n307_I0),
    .I1(n307_I1),
    .O_t0b(n307_O_t0b),
    .O_t1b(n307_O_t1b)
  );
  Add n308 ( // @[Top.scala 892:22]
    .valid_up(n308_valid_up),
    .valid_down(n308_valid_down),
    .I_t0b(n308_I_t0b),
    .I_t1b(n308_I_t1b),
    .O(n308_O)
  );
  AtomTuple_1 n309 ( // @[Top.scala 895:22]
    .valid_up(n309_valid_up),
    .valid_down(n309_valid_down),
    .I0(n309_I0),
    .I1(n309_I1),
    .O_t0b(n309_O_t0b),
    .O_t1b(n309_O_t1b)
  );
  AtomTuple_9 n310 ( // @[Top.scala 899:22]
    .valid_up(n310_valid_up),
    .valid_down(n310_valid_down),
    .I0(n310_I0),
    .I1_t0b(n310_I1_t0b),
    .I1_t1b(n310_I1_t1b),
    .O_t0b(n310_O_t0b),
    .O_t1b_t0b(n310_O_t1b_t0b),
    .O_t1b_t1b(n310_O_t1b_t1b)
  );
  If n311 ( // @[Top.scala 903:22]
    .valid_up(n311_valid_up),
    .valid_down(n311_valid_down),
    .I_t0b(n311_I_t0b),
    .I_t1b_t0b(n311_I_t1b_t0b),
    .I_t1b_t1b(n311_I_t1b_t1b),
    .O(n311_O)
  );
  AtomTuple_1 n312 ( // @[Top.scala 906:22]
    .valid_up(n312_valid_up),
    .valid_down(n312_valid_down),
    .I0(n312_I0),
    .I1(n312_I1),
    .O_t0b(n312_O_t0b),
    .O_t1b(n312_O_t1b)
  );
  Mul n313 ( // @[Top.scala 910:22]
    .clock(n313_clock),
    .reset(n313_reset),
    .valid_up(n313_valid_up),
    .valid_down(n313_valid_down),
    .I_t0b(n313_I_t0b),
    .I_t1b(n313_I_t1b),
    .O(n313_O)
  );
  AtomTuple_1 n315 ( // @[Top.scala 913:22]
    .valid_up(n315_valid_up),
    .valid_down(n315_valid_down),
    .I0(n315_I0),
    .I1(n315_I1),
    .O_t0b(n315_O_t0b),
    .O_t1b(n315_O_t1b)
  );
  Lt n316 ( // @[Top.scala 917:22]
    .valid_up(n316_valid_up),
    .valid_down(n316_valid_down),
    .I_t0b(n316_I_t0b),
    .I_t1b(n316_I_t1b),
    .O(n316_O)
  );
  InitialDelayCounter_24 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n318 ( // @[Top.scala 921:22]
    .valid_up(n318_valid_up),
    .valid_down(n318_valid_down),
    .I0(n318_I0),
    .I1(n318_I1),
    .O_t0b(n318_O_t0b),
    .O_t1b(n318_O_t1b)
  );
  Sub n319 ( // @[Top.scala 925:22]
    .valid_up(n319_valid_up),
    .valid_down(n319_valid_down),
    .I_t0b(n319_I_t0b),
    .I_t1b(n319_I_t1b),
    .O(n319_O)
  );
  AtomTuple_1 n320 ( // @[Top.scala 928:22]
    .valid_up(n320_valid_up),
    .valid_down(n320_valid_down),
    .I0(n320_I0),
    .I1(n320_I1),
    .O_t0b(n320_O_t0b),
    .O_t1b(n320_O_t1b)
  );
  AtomTuple_1 n321 ( // @[Top.scala 932:22]
    .valid_up(n321_valid_up),
    .valid_down(n321_valid_down),
    .I0(n321_I0),
    .I1(n321_I1),
    .O_t0b(n321_O_t0b),
    .O_t1b(n321_O_t1b)
  );
  AtomTuple_15 n322 ( // @[Top.scala 936:22]
    .valid_up(n322_valid_up),
    .valid_down(n322_valid_down),
    .I0_t0b(n322_I0_t0b),
    .I0_t1b(n322_I0_t1b),
    .I1_t0b(n322_I1_t0b),
    .I1_t1b(n322_I1_t1b),
    .O_t0b_t0b(n322_O_t0b_t0b),
    .O_t0b_t1b(n322_O_t0b_t1b),
    .O_t1b_t0b(n322_O_t1b_t0b),
    .O_t1b_t1b(n322_O_t1b_t1b)
  );
  FIFO_3 n323 ( // @[Top.scala 940:22]
    .clock(n323_clock),
    .reset(n323_reset),
    .valid_up(n323_valid_up),
    .valid_down(n323_valid_down),
    .I_t0b_t0b(n323_I_t0b_t0b),
    .I_t0b_t1b(n323_I_t0b_t1b),
    .I_t1b_t0b(n323_I_t1b_t0b),
    .I_t1b_t1b(n323_I_t1b_t1b),
    .O_t0b_t0b(n323_O_t0b_t0b),
    .O_t0b_t1b(n323_O_t0b_t1b),
    .O_t1b_t0b(n323_O_t1b_t0b),
    .O_t1b_t1b(n323_O_t1b_t1b)
  );
  AtomTuple_16 n324 ( // @[Top.scala 943:22]
    .valid_up(n324_valid_up),
    .valid_down(n324_valid_down),
    .I0(n324_I0),
    .I1_t0b_t0b(n324_I1_t0b_t0b),
    .I1_t0b_t1b(n324_I1_t0b_t1b),
    .I1_t1b_t0b(n324_I1_t1b_t0b),
    .I1_t1b_t1b(n324_I1_t1b_t1b),
    .O_t0b(n324_O_t0b),
    .O_t1b_t0b_t0b(n324_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n324_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n324_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n324_O_t1b_t1b_t1b)
  );
  If_1 n325 ( // @[Top.scala 947:22]
    .valid_up(n325_valid_up),
    .valid_down(n325_valid_down),
    .I_t0b(n325_I_t0b),
    .I_t1b_t0b_t0b(n325_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n325_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n325_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n325_I_t1b_t1b_t1b),
    .O_t0b(n325_O_t0b),
    .O_t1b(n325_O_t1b)
  );
  AtomTuple_3 n327 ( // @[Top.scala 950:22]
    .valid_up(n327_valid_up),
    .valid_down(n327_valid_down),
    .I0(n327_I0),
    .I1_t0b(n327_I1_t0b),
    .I1_t1b(n327_I1_t1b),
    .O_t0b(n327_O_t0b),
    .O_t1b_t0b(n327_O_t1b_t0b),
    .O_t1b_t1b(n327_O_t1b_t1b)
  );
  assign valid_down = n327_valid_down; // @[Top.scala 955:16]
  assign O_t0b = n327_O_t0b; // @[Top.scala 954:7]
  assign O_t1b_t0b = n327_O_t1b_t0b; // @[Top.scala 954:7]
  assign O_t1b_t1b = n327_O_t1b_t1b; // @[Top.scala 954:7]
  assign n293_valid_up = valid_up; // @[Top.scala 849:19]
  assign n293_I_t0b = I_t0b; // @[Top.scala 848:12]
  assign n326_clock = clock;
  assign n326_reset = reset;
  assign n326_valid_up = n293_valid_down; // @[Top.scala 852:19]
  assign n326_I = n293_O; // @[Top.scala 851:12]
  assign n314_clock = clock;
  assign n314_reset = reset;
  assign n314_valid_up = n293_valid_down; // @[Top.scala 855:19]
  assign n314_I = n293_O; // @[Top.scala 854:12]
  assign n294_valid_up = valid_up; // @[Top.scala 858:19]
  assign n294_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 857:12]
  assign n294_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 857:12]
  assign n295_valid_up = n294_valid_down; // @[Top.scala 861:19]
  assign n295_I_t0b = n294_O_t0b; // @[Top.scala 860:12]
  assign n296_valid_up = n294_valid_down; // @[Top.scala 864:19]
  assign n296_I_t1b = n294_O_t1b; // @[Top.scala 863:12]
  assign n297_valid_up = n295_valid_down & n296_valid_down; // @[Top.scala 868:19]
  assign n297_I0 = n295_O; // @[Top.scala 866:13]
  assign n297_I1 = n296_O; // @[Top.scala 867:13]
  assign n298_valid_up = n297_valid_down; // @[Top.scala 871:19]
  assign n298_I_t0b = n297_O_t0b; // @[Top.scala 870:12]
  assign n298_I_t1b = n297_O_t1b; // @[Top.scala 870:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n301_valid_up = n298_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 876:19]
  assign n301_I0 = n298_O; // @[Top.scala 874:13]
  assign n302_valid_up = n301_valid_down; // @[Top.scala 879:19]
  assign n302_I_t0b = n301_O_t0b; // @[Top.scala 878:12]
  assign n303_valid_up = n302_valid_down & n295_valid_down; // @[Top.scala 883:19]
  assign n303_I0 = n302_O; // @[Top.scala 881:13]
  assign n303_I1 = n295_O; // @[Top.scala 882:13]
  assign n304_valid_up = n303_valid_down; // @[Top.scala 886:19]
  assign n304_I_t0b = n303_O_t0b; // @[Top.scala 885:12]
  assign n304_I_t1b = n303_O_t1b; // @[Top.scala 885:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n307_valid_up = n302_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 891:19]
  assign n307_I0 = n302_O; // @[Top.scala 889:13]
  assign n307_I1 = 16'h1; // @[Top.scala 890:13]
  assign n308_valid_up = n307_valid_down; // @[Top.scala 894:19]
  assign n308_I_t0b = n307_O_t0b; // @[Top.scala 893:12]
  assign n308_I_t1b = n307_O_t1b; // @[Top.scala 893:12]
  assign n309_valid_up = n308_valid_down & n302_valid_down; // @[Top.scala 898:19]
  assign n309_I0 = n308_O; // @[Top.scala 896:13]
  assign n309_I1 = n302_O; // @[Top.scala 897:13]
  assign n310_valid_up = n304_valid_down & n309_valid_down; // @[Top.scala 902:19]
  assign n310_I0 = n304_O[0]; // @[Top.scala 900:13]
  assign n310_I1_t0b = n309_O_t0b; // @[Top.scala 901:13]
  assign n310_I1_t1b = n309_O_t1b; // @[Top.scala 901:13]
  assign n311_valid_up = n310_valid_down; // @[Top.scala 905:19]
  assign n311_I_t0b = n310_O_t0b; // @[Top.scala 904:12]
  assign n311_I_t1b_t0b = n310_O_t1b_t0b; // @[Top.scala 904:12]
  assign n311_I_t1b_t1b = n310_O_t1b_t1b; // @[Top.scala 904:12]
  assign n312_valid_up = n311_valid_down; // @[Top.scala 909:19]
  assign n312_I0 = n311_O; // @[Top.scala 907:13]
  assign n312_I1 = n311_O; // @[Top.scala 908:13]
  assign n313_clock = clock;
  assign n313_reset = reset;
  assign n313_valid_up = n312_valid_down; // @[Top.scala 912:19]
  assign n313_I_t0b = n312_O_t0b; // @[Top.scala 911:12]
  assign n313_I_t1b = n312_O_t1b; // @[Top.scala 911:12]
  assign n315_valid_up = n314_valid_down & n313_valid_down; // @[Top.scala 916:19]
  assign n315_I0 = n314_O; // @[Top.scala 914:13]
  assign n315_I1 = n313_O; // @[Top.scala 915:13]
  assign n316_valid_up = n315_valid_down; // @[Top.scala 919:19]
  assign n316_I_t0b = n315_O_t0b; // @[Top.scala 918:12]
  assign n316_I_t1b = n315_O_t1b; // @[Top.scala 918:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n318_valid_up = n311_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 924:19]
  assign n318_I0 = n311_O; // @[Top.scala 922:13]
  assign n318_I1 = 16'h1; // @[Top.scala 923:13]
  assign n319_valid_up = n318_valid_down; // @[Top.scala 927:19]
  assign n319_I_t0b = n318_O_t0b; // @[Top.scala 926:12]
  assign n319_I_t1b = n318_O_t1b; // @[Top.scala 926:12]
  assign n320_valid_up = n295_valid_down & n319_valid_down; // @[Top.scala 931:19]
  assign n320_I0 = n295_O; // @[Top.scala 929:13]
  assign n320_I1 = n319_O; // @[Top.scala 930:13]
  assign n321_valid_up = n311_valid_down & n296_valid_down; // @[Top.scala 935:19]
  assign n321_I0 = n311_O; // @[Top.scala 933:13]
  assign n321_I1 = n296_O; // @[Top.scala 934:13]
  assign n322_valid_up = n320_valid_down & n321_valid_down; // @[Top.scala 939:19]
  assign n322_I0_t0b = n320_O_t0b; // @[Top.scala 937:13]
  assign n322_I0_t1b = n320_O_t1b; // @[Top.scala 937:13]
  assign n322_I1_t0b = n321_O_t0b; // @[Top.scala 938:13]
  assign n322_I1_t1b = n321_O_t1b; // @[Top.scala 938:13]
  assign n323_clock = clock;
  assign n323_reset = reset;
  assign n323_valid_up = n322_valid_down; // @[Top.scala 942:19]
  assign n323_I_t0b_t0b = n322_O_t0b_t0b; // @[Top.scala 941:12]
  assign n323_I_t0b_t1b = n322_O_t0b_t1b; // @[Top.scala 941:12]
  assign n323_I_t1b_t0b = n322_O_t1b_t0b; // @[Top.scala 941:12]
  assign n323_I_t1b_t1b = n322_O_t1b_t1b; // @[Top.scala 941:12]
  assign n324_valid_up = n316_valid_down & n323_valid_down; // @[Top.scala 946:19]
  assign n324_I0 = n316_O[0]; // @[Top.scala 944:13]
  assign n324_I1_t0b_t0b = n323_O_t0b_t0b; // @[Top.scala 945:13]
  assign n324_I1_t0b_t1b = n323_O_t0b_t1b; // @[Top.scala 945:13]
  assign n324_I1_t1b_t0b = n323_O_t1b_t0b; // @[Top.scala 945:13]
  assign n324_I1_t1b_t1b = n323_O_t1b_t1b; // @[Top.scala 945:13]
  assign n325_valid_up = n324_valid_down; // @[Top.scala 949:19]
  assign n325_I_t0b = n324_O_t0b; // @[Top.scala 948:12]
  assign n325_I_t1b_t0b_t0b = n324_O_t1b_t0b_t0b; // @[Top.scala 948:12]
  assign n325_I_t1b_t0b_t1b = n324_O_t1b_t0b_t1b; // @[Top.scala 948:12]
  assign n325_I_t1b_t1b_t0b = n324_O_t1b_t1b_t0b; // @[Top.scala 948:12]
  assign n325_I_t1b_t1b_t1b = n324_O_t1b_t1b_t1b; // @[Top.scala 948:12]
  assign n327_valid_up = n326_valid_down & n325_valid_down; // @[Top.scala 953:19]
  assign n327_I0 = n326_O; // @[Top.scala 951:13]
  assign n327_I1_t0b = n325_O_t0b; // @[Top.scala 952:13]
  assign n327_I1_t1b = n325_O_t1b; // @[Top.scala 952:13]
endmodule
module MapS_8(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_8 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_8 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_8 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_8 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_8(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_8 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_27(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [4:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 5'h19; // @[InitialDelayCounter.scala 17:17]
  wire [4:0] _T_4 = value + 5'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 5'h19; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[4:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 5'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Module_9(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n332_valid_up; // @[Top.scala 961:22]
  wire  n332_valid_down; // @[Top.scala 961:22]
  wire [15:0] n332_I_t0b; // @[Top.scala 961:22]
  wire [15:0] n332_O; // @[Top.scala 961:22]
  wire  n365_clock; // @[Top.scala 964:22]
  wire  n365_reset; // @[Top.scala 964:22]
  wire  n365_valid_up; // @[Top.scala 964:22]
  wire  n365_valid_down; // @[Top.scala 964:22]
  wire [15:0] n365_I; // @[Top.scala 964:22]
  wire [15:0] n365_O; // @[Top.scala 964:22]
  wire  n353_clock; // @[Top.scala 967:22]
  wire  n353_reset; // @[Top.scala 967:22]
  wire  n353_valid_up; // @[Top.scala 967:22]
  wire  n353_valid_down; // @[Top.scala 967:22]
  wire [15:0] n353_I; // @[Top.scala 967:22]
  wire [15:0] n353_O; // @[Top.scala 967:22]
  wire  n333_valid_up; // @[Top.scala 970:22]
  wire  n333_valid_down; // @[Top.scala 970:22]
  wire [15:0] n333_I_t1b_t0b; // @[Top.scala 970:22]
  wire [15:0] n333_I_t1b_t1b; // @[Top.scala 970:22]
  wire [15:0] n333_O_t0b; // @[Top.scala 970:22]
  wire [15:0] n333_O_t1b; // @[Top.scala 970:22]
  wire  n334_valid_up; // @[Top.scala 973:22]
  wire  n334_valid_down; // @[Top.scala 973:22]
  wire [15:0] n334_I_t0b; // @[Top.scala 973:22]
  wire [15:0] n334_O; // @[Top.scala 973:22]
  wire  n335_valid_up; // @[Top.scala 976:22]
  wire  n335_valid_down; // @[Top.scala 976:22]
  wire [15:0] n335_I_t1b; // @[Top.scala 976:22]
  wire [15:0] n335_O; // @[Top.scala 976:22]
  wire  n336_valid_up; // @[Top.scala 979:22]
  wire  n336_valid_down; // @[Top.scala 979:22]
  wire [15:0] n336_I0; // @[Top.scala 979:22]
  wire [15:0] n336_I1; // @[Top.scala 979:22]
  wire [15:0] n336_O_t0b; // @[Top.scala 979:22]
  wire [15:0] n336_O_t1b; // @[Top.scala 979:22]
  wire  n337_valid_up; // @[Top.scala 983:22]
  wire  n337_valid_down; // @[Top.scala 983:22]
  wire [15:0] n337_I_t0b; // @[Top.scala 983:22]
  wire [15:0] n337_I_t1b; // @[Top.scala 983:22]
  wire [15:0] n337_O; // @[Top.scala 983:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n340_valid_up; // @[Top.scala 987:22]
  wire  n340_valid_down; // @[Top.scala 987:22]
  wire [15:0] n340_I0; // @[Top.scala 987:22]
  wire [15:0] n340_O_t0b; // @[Top.scala 987:22]
  wire  n341_valid_up; // @[Top.scala 991:22]
  wire  n341_valid_down; // @[Top.scala 991:22]
  wire [15:0] n341_I_t0b; // @[Top.scala 991:22]
  wire [15:0] n341_O; // @[Top.scala 991:22]
  wire  n342_valid_up; // @[Top.scala 994:22]
  wire  n342_valid_down; // @[Top.scala 994:22]
  wire [15:0] n342_I0; // @[Top.scala 994:22]
  wire [15:0] n342_I1; // @[Top.scala 994:22]
  wire [15:0] n342_O_t0b; // @[Top.scala 994:22]
  wire [15:0] n342_O_t1b; // @[Top.scala 994:22]
  wire  n343_valid_up; // @[Top.scala 998:22]
  wire  n343_valid_down; // @[Top.scala 998:22]
  wire [15:0] n343_I_t0b; // @[Top.scala 998:22]
  wire [15:0] n343_I_t1b; // @[Top.scala 998:22]
  wire [15:0] n343_O; // @[Top.scala 998:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n346_valid_up; // @[Top.scala 1002:22]
  wire  n346_valid_down; // @[Top.scala 1002:22]
  wire [15:0] n346_I0; // @[Top.scala 1002:22]
  wire [15:0] n346_I1; // @[Top.scala 1002:22]
  wire [15:0] n346_O_t0b; // @[Top.scala 1002:22]
  wire [15:0] n346_O_t1b; // @[Top.scala 1002:22]
  wire  n347_valid_up; // @[Top.scala 1006:22]
  wire  n347_valid_down; // @[Top.scala 1006:22]
  wire [15:0] n347_I_t0b; // @[Top.scala 1006:22]
  wire [15:0] n347_I_t1b; // @[Top.scala 1006:22]
  wire [15:0] n347_O; // @[Top.scala 1006:22]
  wire  n348_valid_up; // @[Top.scala 1009:22]
  wire  n348_valid_down; // @[Top.scala 1009:22]
  wire [15:0] n348_I0; // @[Top.scala 1009:22]
  wire [15:0] n348_I1; // @[Top.scala 1009:22]
  wire [15:0] n348_O_t0b; // @[Top.scala 1009:22]
  wire [15:0] n348_O_t1b; // @[Top.scala 1009:22]
  wire  n349_valid_up; // @[Top.scala 1013:22]
  wire  n349_valid_down; // @[Top.scala 1013:22]
  wire  n349_I0; // @[Top.scala 1013:22]
  wire [15:0] n349_I1_t0b; // @[Top.scala 1013:22]
  wire [15:0] n349_I1_t1b; // @[Top.scala 1013:22]
  wire  n349_O_t0b; // @[Top.scala 1013:22]
  wire [15:0] n349_O_t1b_t0b; // @[Top.scala 1013:22]
  wire [15:0] n349_O_t1b_t1b; // @[Top.scala 1013:22]
  wire  n350_valid_up; // @[Top.scala 1017:22]
  wire  n350_valid_down; // @[Top.scala 1017:22]
  wire  n350_I_t0b; // @[Top.scala 1017:22]
  wire [15:0] n350_I_t1b_t0b; // @[Top.scala 1017:22]
  wire [15:0] n350_I_t1b_t1b; // @[Top.scala 1017:22]
  wire [15:0] n350_O; // @[Top.scala 1017:22]
  wire  n351_valid_up; // @[Top.scala 1020:22]
  wire  n351_valid_down; // @[Top.scala 1020:22]
  wire [15:0] n351_I0; // @[Top.scala 1020:22]
  wire [15:0] n351_I1; // @[Top.scala 1020:22]
  wire [15:0] n351_O_t0b; // @[Top.scala 1020:22]
  wire [15:0] n351_O_t1b; // @[Top.scala 1020:22]
  wire  n352_clock; // @[Top.scala 1024:22]
  wire  n352_reset; // @[Top.scala 1024:22]
  wire  n352_valid_up; // @[Top.scala 1024:22]
  wire  n352_valid_down; // @[Top.scala 1024:22]
  wire [15:0] n352_I_t0b; // @[Top.scala 1024:22]
  wire [15:0] n352_I_t1b; // @[Top.scala 1024:22]
  wire [15:0] n352_O; // @[Top.scala 1024:22]
  wire  n354_valid_up; // @[Top.scala 1027:22]
  wire  n354_valid_down; // @[Top.scala 1027:22]
  wire [15:0] n354_I0; // @[Top.scala 1027:22]
  wire [15:0] n354_I1; // @[Top.scala 1027:22]
  wire [15:0] n354_O_t0b; // @[Top.scala 1027:22]
  wire [15:0] n354_O_t1b; // @[Top.scala 1027:22]
  wire  n355_valid_up; // @[Top.scala 1031:22]
  wire  n355_valid_down; // @[Top.scala 1031:22]
  wire [15:0] n355_I_t0b; // @[Top.scala 1031:22]
  wire [15:0] n355_I_t1b; // @[Top.scala 1031:22]
  wire [15:0] n355_O; // @[Top.scala 1031:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n357_valid_up; // @[Top.scala 1035:22]
  wire  n357_valid_down; // @[Top.scala 1035:22]
  wire [15:0] n357_I0; // @[Top.scala 1035:22]
  wire [15:0] n357_I1; // @[Top.scala 1035:22]
  wire [15:0] n357_O_t0b; // @[Top.scala 1035:22]
  wire [15:0] n357_O_t1b; // @[Top.scala 1035:22]
  wire  n358_valid_up; // @[Top.scala 1039:22]
  wire  n358_valid_down; // @[Top.scala 1039:22]
  wire [15:0] n358_I_t0b; // @[Top.scala 1039:22]
  wire [15:0] n358_I_t1b; // @[Top.scala 1039:22]
  wire [15:0] n358_O; // @[Top.scala 1039:22]
  wire  n359_valid_up; // @[Top.scala 1042:22]
  wire  n359_valid_down; // @[Top.scala 1042:22]
  wire [15:0] n359_I0; // @[Top.scala 1042:22]
  wire [15:0] n359_I1; // @[Top.scala 1042:22]
  wire [15:0] n359_O_t0b; // @[Top.scala 1042:22]
  wire [15:0] n359_O_t1b; // @[Top.scala 1042:22]
  wire  n360_valid_up; // @[Top.scala 1046:22]
  wire  n360_valid_down; // @[Top.scala 1046:22]
  wire [15:0] n360_I0; // @[Top.scala 1046:22]
  wire [15:0] n360_I1; // @[Top.scala 1046:22]
  wire [15:0] n360_O_t0b; // @[Top.scala 1046:22]
  wire [15:0] n360_O_t1b; // @[Top.scala 1046:22]
  wire  n361_valid_up; // @[Top.scala 1050:22]
  wire  n361_valid_down; // @[Top.scala 1050:22]
  wire [15:0] n361_I0_t0b; // @[Top.scala 1050:22]
  wire [15:0] n361_I0_t1b; // @[Top.scala 1050:22]
  wire [15:0] n361_I1_t0b; // @[Top.scala 1050:22]
  wire [15:0] n361_I1_t1b; // @[Top.scala 1050:22]
  wire [15:0] n361_O_t0b_t0b; // @[Top.scala 1050:22]
  wire [15:0] n361_O_t0b_t1b; // @[Top.scala 1050:22]
  wire [15:0] n361_O_t1b_t0b; // @[Top.scala 1050:22]
  wire [15:0] n361_O_t1b_t1b; // @[Top.scala 1050:22]
  wire  n362_clock; // @[Top.scala 1054:22]
  wire  n362_reset; // @[Top.scala 1054:22]
  wire  n362_valid_up; // @[Top.scala 1054:22]
  wire  n362_valid_down; // @[Top.scala 1054:22]
  wire [15:0] n362_I_t0b_t0b; // @[Top.scala 1054:22]
  wire [15:0] n362_I_t0b_t1b; // @[Top.scala 1054:22]
  wire [15:0] n362_I_t1b_t0b; // @[Top.scala 1054:22]
  wire [15:0] n362_I_t1b_t1b; // @[Top.scala 1054:22]
  wire [15:0] n362_O_t0b_t0b; // @[Top.scala 1054:22]
  wire [15:0] n362_O_t0b_t1b; // @[Top.scala 1054:22]
  wire [15:0] n362_O_t1b_t0b; // @[Top.scala 1054:22]
  wire [15:0] n362_O_t1b_t1b; // @[Top.scala 1054:22]
  wire  n363_valid_up; // @[Top.scala 1057:22]
  wire  n363_valid_down; // @[Top.scala 1057:22]
  wire  n363_I0; // @[Top.scala 1057:22]
  wire [15:0] n363_I1_t0b_t0b; // @[Top.scala 1057:22]
  wire [15:0] n363_I1_t0b_t1b; // @[Top.scala 1057:22]
  wire [15:0] n363_I1_t1b_t0b; // @[Top.scala 1057:22]
  wire [15:0] n363_I1_t1b_t1b; // @[Top.scala 1057:22]
  wire  n363_O_t0b; // @[Top.scala 1057:22]
  wire [15:0] n363_O_t1b_t0b_t0b; // @[Top.scala 1057:22]
  wire [15:0] n363_O_t1b_t0b_t1b; // @[Top.scala 1057:22]
  wire [15:0] n363_O_t1b_t1b_t0b; // @[Top.scala 1057:22]
  wire [15:0] n363_O_t1b_t1b_t1b; // @[Top.scala 1057:22]
  wire  n364_valid_up; // @[Top.scala 1061:22]
  wire  n364_valid_down; // @[Top.scala 1061:22]
  wire  n364_I_t0b; // @[Top.scala 1061:22]
  wire [15:0] n364_I_t1b_t0b_t0b; // @[Top.scala 1061:22]
  wire [15:0] n364_I_t1b_t0b_t1b; // @[Top.scala 1061:22]
  wire [15:0] n364_I_t1b_t1b_t0b; // @[Top.scala 1061:22]
  wire [15:0] n364_I_t1b_t1b_t1b; // @[Top.scala 1061:22]
  wire [15:0] n364_O_t0b; // @[Top.scala 1061:22]
  wire [15:0] n364_O_t1b; // @[Top.scala 1061:22]
  wire  n366_valid_up; // @[Top.scala 1064:22]
  wire  n366_valid_down; // @[Top.scala 1064:22]
  wire [15:0] n366_I0; // @[Top.scala 1064:22]
  wire [15:0] n366_I1_t0b; // @[Top.scala 1064:22]
  wire [15:0] n366_I1_t1b; // @[Top.scala 1064:22]
  wire [15:0] n366_O_t0b; // @[Top.scala 1064:22]
  wire [15:0] n366_O_t1b_t0b; // @[Top.scala 1064:22]
  wire [15:0] n366_O_t1b_t1b; // @[Top.scala 1064:22]
  Fst n332 ( // @[Top.scala 961:22]
    .valid_up(n332_valid_up),
    .valid_down(n332_valid_down),
    .I_t0b(n332_I_t0b),
    .O(n332_O)
  );
  FIFO_1 n365 ( // @[Top.scala 964:22]
    .clock(n365_clock),
    .reset(n365_reset),
    .valid_up(n365_valid_up),
    .valid_down(n365_valid_down),
    .I(n365_I),
    .O(n365_O)
  );
  FIFO_1 n353 ( // @[Top.scala 967:22]
    .clock(n353_clock),
    .reset(n353_reset),
    .valid_up(n353_valid_up),
    .valid_down(n353_valid_down),
    .I(n353_I),
    .O(n353_O)
  );
  Snd n333 ( // @[Top.scala 970:22]
    .valid_up(n333_valid_up),
    .valid_down(n333_valid_down),
    .I_t1b_t0b(n333_I_t1b_t0b),
    .I_t1b_t1b(n333_I_t1b_t1b),
    .O_t0b(n333_O_t0b),
    .O_t1b(n333_O_t1b)
  );
  Fst_1 n334 ( // @[Top.scala 973:22]
    .valid_up(n334_valid_up),
    .valid_down(n334_valid_down),
    .I_t0b(n334_I_t0b),
    .O(n334_O)
  );
  Snd_1 n335 ( // @[Top.scala 976:22]
    .valid_up(n335_valid_up),
    .valid_down(n335_valid_down),
    .I_t1b(n335_I_t1b),
    .O(n335_O)
  );
  AtomTuple_1 n336 ( // @[Top.scala 979:22]
    .valid_up(n336_valid_up),
    .valid_down(n336_valid_down),
    .I0(n336_I0),
    .I1(n336_I1),
    .O_t0b(n336_O_t0b),
    .O_t1b(n336_O_t1b)
  );
  Add n337 ( // @[Top.scala 983:22]
    .valid_up(n337_valid_up),
    .valid_down(n337_valid_down),
    .I_t0b(n337_I_t0b),
    .I_t1b(n337_I_t1b),
    .O(n337_O)
  );
  InitialDelayCounter_27 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n340 ( // @[Top.scala 987:22]
    .valid_up(n340_valid_up),
    .valid_down(n340_valid_down),
    .I0(n340_I0),
    .O_t0b(n340_O_t0b)
  );
  RShift n341 ( // @[Top.scala 991:22]
    .valid_up(n341_valid_up),
    .valid_down(n341_valid_down),
    .I_t0b(n341_I_t0b),
    .O(n341_O)
  );
  AtomTuple_1 n342 ( // @[Top.scala 994:22]
    .valid_up(n342_valid_up),
    .valid_down(n342_valid_down),
    .I0(n342_I0),
    .I1(n342_I1),
    .O_t0b(n342_O_t0b),
    .O_t1b(n342_O_t1b)
  );
  Eq n343 ( // @[Top.scala 998:22]
    .valid_up(n343_valid_up),
    .valid_down(n343_valid_down),
    .I_t0b(n343_I_t0b),
    .I_t1b(n343_I_t1b),
    .O(n343_O)
  );
  InitialDelayCounter_27 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n346 ( // @[Top.scala 1002:22]
    .valid_up(n346_valid_up),
    .valid_down(n346_valid_down),
    .I0(n346_I0),
    .I1(n346_I1),
    .O_t0b(n346_O_t0b),
    .O_t1b(n346_O_t1b)
  );
  Add n347 ( // @[Top.scala 1006:22]
    .valid_up(n347_valid_up),
    .valid_down(n347_valid_down),
    .I_t0b(n347_I_t0b),
    .I_t1b(n347_I_t1b),
    .O(n347_O)
  );
  AtomTuple_1 n348 ( // @[Top.scala 1009:22]
    .valid_up(n348_valid_up),
    .valid_down(n348_valid_down),
    .I0(n348_I0),
    .I1(n348_I1),
    .O_t0b(n348_O_t0b),
    .O_t1b(n348_O_t1b)
  );
  AtomTuple_9 n349 ( // @[Top.scala 1013:22]
    .valid_up(n349_valid_up),
    .valid_down(n349_valid_down),
    .I0(n349_I0),
    .I1_t0b(n349_I1_t0b),
    .I1_t1b(n349_I1_t1b),
    .O_t0b(n349_O_t0b),
    .O_t1b_t0b(n349_O_t1b_t0b),
    .O_t1b_t1b(n349_O_t1b_t1b)
  );
  If n350 ( // @[Top.scala 1017:22]
    .valid_up(n350_valid_up),
    .valid_down(n350_valid_down),
    .I_t0b(n350_I_t0b),
    .I_t1b_t0b(n350_I_t1b_t0b),
    .I_t1b_t1b(n350_I_t1b_t1b),
    .O(n350_O)
  );
  AtomTuple_1 n351 ( // @[Top.scala 1020:22]
    .valid_up(n351_valid_up),
    .valid_down(n351_valid_down),
    .I0(n351_I0),
    .I1(n351_I1),
    .O_t0b(n351_O_t0b),
    .O_t1b(n351_O_t1b)
  );
  Mul n352 ( // @[Top.scala 1024:22]
    .clock(n352_clock),
    .reset(n352_reset),
    .valid_up(n352_valid_up),
    .valid_down(n352_valid_down),
    .I_t0b(n352_I_t0b),
    .I_t1b(n352_I_t1b),
    .O(n352_O)
  );
  AtomTuple_1 n354 ( // @[Top.scala 1027:22]
    .valid_up(n354_valid_up),
    .valid_down(n354_valid_down),
    .I0(n354_I0),
    .I1(n354_I1),
    .O_t0b(n354_O_t0b),
    .O_t1b(n354_O_t1b)
  );
  Lt n355 ( // @[Top.scala 1031:22]
    .valid_up(n355_valid_up),
    .valid_down(n355_valid_down),
    .I_t0b(n355_I_t0b),
    .I_t1b(n355_I_t1b),
    .O(n355_O)
  );
  InitialDelayCounter_27 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n357 ( // @[Top.scala 1035:22]
    .valid_up(n357_valid_up),
    .valid_down(n357_valid_down),
    .I0(n357_I0),
    .I1(n357_I1),
    .O_t0b(n357_O_t0b),
    .O_t1b(n357_O_t1b)
  );
  Sub n358 ( // @[Top.scala 1039:22]
    .valid_up(n358_valid_up),
    .valid_down(n358_valid_down),
    .I_t0b(n358_I_t0b),
    .I_t1b(n358_I_t1b),
    .O(n358_O)
  );
  AtomTuple_1 n359 ( // @[Top.scala 1042:22]
    .valid_up(n359_valid_up),
    .valid_down(n359_valid_down),
    .I0(n359_I0),
    .I1(n359_I1),
    .O_t0b(n359_O_t0b),
    .O_t1b(n359_O_t1b)
  );
  AtomTuple_1 n360 ( // @[Top.scala 1046:22]
    .valid_up(n360_valid_up),
    .valid_down(n360_valid_down),
    .I0(n360_I0),
    .I1(n360_I1),
    .O_t0b(n360_O_t0b),
    .O_t1b(n360_O_t1b)
  );
  AtomTuple_15 n361 ( // @[Top.scala 1050:22]
    .valid_up(n361_valid_up),
    .valid_down(n361_valid_down),
    .I0_t0b(n361_I0_t0b),
    .I0_t1b(n361_I0_t1b),
    .I1_t0b(n361_I1_t0b),
    .I1_t1b(n361_I1_t1b),
    .O_t0b_t0b(n361_O_t0b_t0b),
    .O_t0b_t1b(n361_O_t0b_t1b),
    .O_t1b_t0b(n361_O_t1b_t0b),
    .O_t1b_t1b(n361_O_t1b_t1b)
  );
  FIFO_3 n362 ( // @[Top.scala 1054:22]
    .clock(n362_clock),
    .reset(n362_reset),
    .valid_up(n362_valid_up),
    .valid_down(n362_valid_down),
    .I_t0b_t0b(n362_I_t0b_t0b),
    .I_t0b_t1b(n362_I_t0b_t1b),
    .I_t1b_t0b(n362_I_t1b_t0b),
    .I_t1b_t1b(n362_I_t1b_t1b),
    .O_t0b_t0b(n362_O_t0b_t0b),
    .O_t0b_t1b(n362_O_t0b_t1b),
    .O_t1b_t0b(n362_O_t1b_t0b),
    .O_t1b_t1b(n362_O_t1b_t1b)
  );
  AtomTuple_16 n363 ( // @[Top.scala 1057:22]
    .valid_up(n363_valid_up),
    .valid_down(n363_valid_down),
    .I0(n363_I0),
    .I1_t0b_t0b(n363_I1_t0b_t0b),
    .I1_t0b_t1b(n363_I1_t0b_t1b),
    .I1_t1b_t0b(n363_I1_t1b_t0b),
    .I1_t1b_t1b(n363_I1_t1b_t1b),
    .O_t0b(n363_O_t0b),
    .O_t1b_t0b_t0b(n363_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n363_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n363_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n363_O_t1b_t1b_t1b)
  );
  If_1 n364 ( // @[Top.scala 1061:22]
    .valid_up(n364_valid_up),
    .valid_down(n364_valid_down),
    .I_t0b(n364_I_t0b),
    .I_t1b_t0b_t0b(n364_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n364_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n364_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n364_I_t1b_t1b_t1b),
    .O_t0b(n364_O_t0b),
    .O_t1b(n364_O_t1b)
  );
  AtomTuple_3 n366 ( // @[Top.scala 1064:22]
    .valid_up(n366_valid_up),
    .valid_down(n366_valid_down),
    .I0(n366_I0),
    .I1_t0b(n366_I1_t0b),
    .I1_t1b(n366_I1_t1b),
    .O_t0b(n366_O_t0b),
    .O_t1b_t0b(n366_O_t1b_t0b),
    .O_t1b_t1b(n366_O_t1b_t1b)
  );
  assign valid_down = n366_valid_down; // @[Top.scala 1069:16]
  assign O_t0b = n366_O_t0b; // @[Top.scala 1068:7]
  assign O_t1b_t0b = n366_O_t1b_t0b; // @[Top.scala 1068:7]
  assign O_t1b_t1b = n366_O_t1b_t1b; // @[Top.scala 1068:7]
  assign n332_valid_up = valid_up; // @[Top.scala 963:19]
  assign n332_I_t0b = I_t0b; // @[Top.scala 962:12]
  assign n365_clock = clock;
  assign n365_reset = reset;
  assign n365_valid_up = n332_valid_down; // @[Top.scala 966:19]
  assign n365_I = n332_O; // @[Top.scala 965:12]
  assign n353_clock = clock;
  assign n353_reset = reset;
  assign n353_valid_up = n332_valid_down; // @[Top.scala 969:19]
  assign n353_I = n332_O; // @[Top.scala 968:12]
  assign n333_valid_up = valid_up; // @[Top.scala 972:19]
  assign n333_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 971:12]
  assign n333_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 971:12]
  assign n334_valid_up = n333_valid_down; // @[Top.scala 975:19]
  assign n334_I_t0b = n333_O_t0b; // @[Top.scala 974:12]
  assign n335_valid_up = n333_valid_down; // @[Top.scala 978:19]
  assign n335_I_t1b = n333_O_t1b; // @[Top.scala 977:12]
  assign n336_valid_up = n334_valid_down & n335_valid_down; // @[Top.scala 982:19]
  assign n336_I0 = n334_O; // @[Top.scala 980:13]
  assign n336_I1 = n335_O; // @[Top.scala 981:13]
  assign n337_valid_up = n336_valid_down; // @[Top.scala 985:19]
  assign n337_I_t0b = n336_O_t0b; // @[Top.scala 984:12]
  assign n337_I_t1b = n336_O_t1b; // @[Top.scala 984:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n340_valid_up = n337_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 990:19]
  assign n340_I0 = n337_O; // @[Top.scala 988:13]
  assign n341_valid_up = n340_valid_down; // @[Top.scala 993:19]
  assign n341_I_t0b = n340_O_t0b; // @[Top.scala 992:12]
  assign n342_valid_up = n341_valid_down & n334_valid_down; // @[Top.scala 997:19]
  assign n342_I0 = n341_O; // @[Top.scala 995:13]
  assign n342_I1 = n334_O; // @[Top.scala 996:13]
  assign n343_valid_up = n342_valid_down; // @[Top.scala 1000:19]
  assign n343_I_t0b = n342_O_t0b; // @[Top.scala 999:12]
  assign n343_I_t1b = n342_O_t1b; // @[Top.scala 999:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n346_valid_up = n341_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1005:19]
  assign n346_I0 = n341_O; // @[Top.scala 1003:13]
  assign n346_I1 = 16'h1; // @[Top.scala 1004:13]
  assign n347_valid_up = n346_valid_down; // @[Top.scala 1008:19]
  assign n347_I_t0b = n346_O_t0b; // @[Top.scala 1007:12]
  assign n347_I_t1b = n346_O_t1b; // @[Top.scala 1007:12]
  assign n348_valid_up = n347_valid_down & n341_valid_down; // @[Top.scala 1012:19]
  assign n348_I0 = n347_O; // @[Top.scala 1010:13]
  assign n348_I1 = n341_O; // @[Top.scala 1011:13]
  assign n349_valid_up = n343_valid_down & n348_valid_down; // @[Top.scala 1016:19]
  assign n349_I0 = n343_O[0]; // @[Top.scala 1014:13]
  assign n349_I1_t0b = n348_O_t0b; // @[Top.scala 1015:13]
  assign n349_I1_t1b = n348_O_t1b; // @[Top.scala 1015:13]
  assign n350_valid_up = n349_valid_down; // @[Top.scala 1019:19]
  assign n350_I_t0b = n349_O_t0b; // @[Top.scala 1018:12]
  assign n350_I_t1b_t0b = n349_O_t1b_t0b; // @[Top.scala 1018:12]
  assign n350_I_t1b_t1b = n349_O_t1b_t1b; // @[Top.scala 1018:12]
  assign n351_valid_up = n350_valid_down; // @[Top.scala 1023:19]
  assign n351_I0 = n350_O; // @[Top.scala 1021:13]
  assign n351_I1 = n350_O; // @[Top.scala 1022:13]
  assign n352_clock = clock;
  assign n352_reset = reset;
  assign n352_valid_up = n351_valid_down; // @[Top.scala 1026:19]
  assign n352_I_t0b = n351_O_t0b; // @[Top.scala 1025:12]
  assign n352_I_t1b = n351_O_t1b; // @[Top.scala 1025:12]
  assign n354_valid_up = n353_valid_down & n352_valid_down; // @[Top.scala 1030:19]
  assign n354_I0 = n353_O; // @[Top.scala 1028:13]
  assign n354_I1 = n352_O; // @[Top.scala 1029:13]
  assign n355_valid_up = n354_valid_down; // @[Top.scala 1033:19]
  assign n355_I_t0b = n354_O_t0b; // @[Top.scala 1032:12]
  assign n355_I_t1b = n354_O_t1b; // @[Top.scala 1032:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n357_valid_up = n350_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1038:19]
  assign n357_I0 = n350_O; // @[Top.scala 1036:13]
  assign n357_I1 = 16'h1; // @[Top.scala 1037:13]
  assign n358_valid_up = n357_valid_down; // @[Top.scala 1041:19]
  assign n358_I_t0b = n357_O_t0b; // @[Top.scala 1040:12]
  assign n358_I_t1b = n357_O_t1b; // @[Top.scala 1040:12]
  assign n359_valid_up = n334_valid_down & n358_valid_down; // @[Top.scala 1045:19]
  assign n359_I0 = n334_O; // @[Top.scala 1043:13]
  assign n359_I1 = n358_O; // @[Top.scala 1044:13]
  assign n360_valid_up = n350_valid_down & n335_valid_down; // @[Top.scala 1049:19]
  assign n360_I0 = n350_O; // @[Top.scala 1047:13]
  assign n360_I1 = n335_O; // @[Top.scala 1048:13]
  assign n361_valid_up = n359_valid_down & n360_valid_down; // @[Top.scala 1053:19]
  assign n361_I0_t0b = n359_O_t0b; // @[Top.scala 1051:13]
  assign n361_I0_t1b = n359_O_t1b; // @[Top.scala 1051:13]
  assign n361_I1_t0b = n360_O_t0b; // @[Top.scala 1052:13]
  assign n361_I1_t1b = n360_O_t1b; // @[Top.scala 1052:13]
  assign n362_clock = clock;
  assign n362_reset = reset;
  assign n362_valid_up = n361_valid_down; // @[Top.scala 1056:19]
  assign n362_I_t0b_t0b = n361_O_t0b_t0b; // @[Top.scala 1055:12]
  assign n362_I_t0b_t1b = n361_O_t0b_t1b; // @[Top.scala 1055:12]
  assign n362_I_t1b_t0b = n361_O_t1b_t0b; // @[Top.scala 1055:12]
  assign n362_I_t1b_t1b = n361_O_t1b_t1b; // @[Top.scala 1055:12]
  assign n363_valid_up = n355_valid_down & n362_valid_down; // @[Top.scala 1060:19]
  assign n363_I0 = n355_O[0]; // @[Top.scala 1058:13]
  assign n363_I1_t0b_t0b = n362_O_t0b_t0b; // @[Top.scala 1059:13]
  assign n363_I1_t0b_t1b = n362_O_t0b_t1b; // @[Top.scala 1059:13]
  assign n363_I1_t1b_t0b = n362_O_t1b_t0b; // @[Top.scala 1059:13]
  assign n363_I1_t1b_t1b = n362_O_t1b_t1b; // @[Top.scala 1059:13]
  assign n364_valid_up = n363_valid_down; // @[Top.scala 1063:19]
  assign n364_I_t0b = n363_O_t0b; // @[Top.scala 1062:12]
  assign n364_I_t1b_t0b_t0b = n363_O_t1b_t0b_t0b; // @[Top.scala 1062:12]
  assign n364_I_t1b_t0b_t1b = n363_O_t1b_t0b_t1b; // @[Top.scala 1062:12]
  assign n364_I_t1b_t1b_t0b = n363_O_t1b_t1b_t0b; // @[Top.scala 1062:12]
  assign n364_I_t1b_t1b_t1b = n363_O_t1b_t1b_t1b; // @[Top.scala 1062:12]
  assign n366_valid_up = n365_valid_down & n364_valid_down; // @[Top.scala 1067:19]
  assign n366_I0 = n365_O; // @[Top.scala 1065:13]
  assign n366_I1_t0b = n364_O_t0b; // @[Top.scala 1066:13]
  assign n366_I1_t1b = n364_O_t1b; // @[Top.scala 1066:13]
endmodule
module MapS_9(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_9 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_9 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_9 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_9 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_9(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_9 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_30(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [4:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 5'h1c; // @[InitialDelayCounter.scala 17:17]
  wire [4:0] _T_4 = value + 5'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 5'h1c; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[4:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 5'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Module_10(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n371_valid_up; // @[Top.scala 1075:22]
  wire  n371_valid_down; // @[Top.scala 1075:22]
  wire [15:0] n371_I_t0b; // @[Top.scala 1075:22]
  wire [15:0] n371_O; // @[Top.scala 1075:22]
  wire  n404_clock; // @[Top.scala 1078:22]
  wire  n404_reset; // @[Top.scala 1078:22]
  wire  n404_valid_up; // @[Top.scala 1078:22]
  wire  n404_valid_down; // @[Top.scala 1078:22]
  wire [15:0] n404_I; // @[Top.scala 1078:22]
  wire [15:0] n404_O; // @[Top.scala 1078:22]
  wire  n392_clock; // @[Top.scala 1081:22]
  wire  n392_reset; // @[Top.scala 1081:22]
  wire  n392_valid_up; // @[Top.scala 1081:22]
  wire  n392_valid_down; // @[Top.scala 1081:22]
  wire [15:0] n392_I; // @[Top.scala 1081:22]
  wire [15:0] n392_O; // @[Top.scala 1081:22]
  wire  n372_valid_up; // @[Top.scala 1084:22]
  wire  n372_valid_down; // @[Top.scala 1084:22]
  wire [15:0] n372_I_t1b_t0b; // @[Top.scala 1084:22]
  wire [15:0] n372_I_t1b_t1b; // @[Top.scala 1084:22]
  wire [15:0] n372_O_t0b; // @[Top.scala 1084:22]
  wire [15:0] n372_O_t1b; // @[Top.scala 1084:22]
  wire  n373_valid_up; // @[Top.scala 1087:22]
  wire  n373_valid_down; // @[Top.scala 1087:22]
  wire [15:0] n373_I_t0b; // @[Top.scala 1087:22]
  wire [15:0] n373_O; // @[Top.scala 1087:22]
  wire  n374_valid_up; // @[Top.scala 1090:22]
  wire  n374_valid_down; // @[Top.scala 1090:22]
  wire [15:0] n374_I_t1b; // @[Top.scala 1090:22]
  wire [15:0] n374_O; // @[Top.scala 1090:22]
  wire  n375_valid_up; // @[Top.scala 1093:22]
  wire  n375_valid_down; // @[Top.scala 1093:22]
  wire [15:0] n375_I0; // @[Top.scala 1093:22]
  wire [15:0] n375_I1; // @[Top.scala 1093:22]
  wire [15:0] n375_O_t0b; // @[Top.scala 1093:22]
  wire [15:0] n375_O_t1b; // @[Top.scala 1093:22]
  wire  n376_valid_up; // @[Top.scala 1097:22]
  wire  n376_valid_down; // @[Top.scala 1097:22]
  wire [15:0] n376_I_t0b; // @[Top.scala 1097:22]
  wire [15:0] n376_I_t1b; // @[Top.scala 1097:22]
  wire [15:0] n376_O; // @[Top.scala 1097:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n379_valid_up; // @[Top.scala 1101:22]
  wire  n379_valid_down; // @[Top.scala 1101:22]
  wire [15:0] n379_I0; // @[Top.scala 1101:22]
  wire [15:0] n379_O_t0b; // @[Top.scala 1101:22]
  wire  n380_valid_up; // @[Top.scala 1105:22]
  wire  n380_valid_down; // @[Top.scala 1105:22]
  wire [15:0] n380_I_t0b; // @[Top.scala 1105:22]
  wire [15:0] n380_O; // @[Top.scala 1105:22]
  wire  n381_valid_up; // @[Top.scala 1108:22]
  wire  n381_valid_down; // @[Top.scala 1108:22]
  wire [15:0] n381_I0; // @[Top.scala 1108:22]
  wire [15:0] n381_I1; // @[Top.scala 1108:22]
  wire [15:0] n381_O_t0b; // @[Top.scala 1108:22]
  wire [15:0] n381_O_t1b; // @[Top.scala 1108:22]
  wire  n382_valid_up; // @[Top.scala 1112:22]
  wire  n382_valid_down; // @[Top.scala 1112:22]
  wire [15:0] n382_I_t0b; // @[Top.scala 1112:22]
  wire [15:0] n382_I_t1b; // @[Top.scala 1112:22]
  wire [15:0] n382_O; // @[Top.scala 1112:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n385_valid_up; // @[Top.scala 1116:22]
  wire  n385_valid_down; // @[Top.scala 1116:22]
  wire [15:0] n385_I0; // @[Top.scala 1116:22]
  wire [15:0] n385_I1; // @[Top.scala 1116:22]
  wire [15:0] n385_O_t0b; // @[Top.scala 1116:22]
  wire [15:0] n385_O_t1b; // @[Top.scala 1116:22]
  wire  n386_valid_up; // @[Top.scala 1120:22]
  wire  n386_valid_down; // @[Top.scala 1120:22]
  wire [15:0] n386_I_t0b; // @[Top.scala 1120:22]
  wire [15:0] n386_I_t1b; // @[Top.scala 1120:22]
  wire [15:0] n386_O; // @[Top.scala 1120:22]
  wire  n387_valid_up; // @[Top.scala 1123:22]
  wire  n387_valid_down; // @[Top.scala 1123:22]
  wire [15:0] n387_I0; // @[Top.scala 1123:22]
  wire [15:0] n387_I1; // @[Top.scala 1123:22]
  wire [15:0] n387_O_t0b; // @[Top.scala 1123:22]
  wire [15:0] n387_O_t1b; // @[Top.scala 1123:22]
  wire  n388_valid_up; // @[Top.scala 1127:22]
  wire  n388_valid_down; // @[Top.scala 1127:22]
  wire  n388_I0; // @[Top.scala 1127:22]
  wire [15:0] n388_I1_t0b; // @[Top.scala 1127:22]
  wire [15:0] n388_I1_t1b; // @[Top.scala 1127:22]
  wire  n388_O_t0b; // @[Top.scala 1127:22]
  wire [15:0] n388_O_t1b_t0b; // @[Top.scala 1127:22]
  wire [15:0] n388_O_t1b_t1b; // @[Top.scala 1127:22]
  wire  n389_valid_up; // @[Top.scala 1131:22]
  wire  n389_valid_down; // @[Top.scala 1131:22]
  wire  n389_I_t0b; // @[Top.scala 1131:22]
  wire [15:0] n389_I_t1b_t0b; // @[Top.scala 1131:22]
  wire [15:0] n389_I_t1b_t1b; // @[Top.scala 1131:22]
  wire [15:0] n389_O; // @[Top.scala 1131:22]
  wire  n390_valid_up; // @[Top.scala 1134:22]
  wire  n390_valid_down; // @[Top.scala 1134:22]
  wire [15:0] n390_I0; // @[Top.scala 1134:22]
  wire [15:0] n390_I1; // @[Top.scala 1134:22]
  wire [15:0] n390_O_t0b; // @[Top.scala 1134:22]
  wire [15:0] n390_O_t1b; // @[Top.scala 1134:22]
  wire  n391_clock; // @[Top.scala 1138:22]
  wire  n391_reset; // @[Top.scala 1138:22]
  wire  n391_valid_up; // @[Top.scala 1138:22]
  wire  n391_valid_down; // @[Top.scala 1138:22]
  wire [15:0] n391_I_t0b; // @[Top.scala 1138:22]
  wire [15:0] n391_I_t1b; // @[Top.scala 1138:22]
  wire [15:0] n391_O; // @[Top.scala 1138:22]
  wire  n393_valid_up; // @[Top.scala 1141:22]
  wire  n393_valid_down; // @[Top.scala 1141:22]
  wire [15:0] n393_I0; // @[Top.scala 1141:22]
  wire [15:0] n393_I1; // @[Top.scala 1141:22]
  wire [15:0] n393_O_t0b; // @[Top.scala 1141:22]
  wire [15:0] n393_O_t1b; // @[Top.scala 1141:22]
  wire  n394_valid_up; // @[Top.scala 1145:22]
  wire  n394_valid_down; // @[Top.scala 1145:22]
  wire [15:0] n394_I_t0b; // @[Top.scala 1145:22]
  wire [15:0] n394_I_t1b; // @[Top.scala 1145:22]
  wire [15:0] n394_O; // @[Top.scala 1145:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n396_valid_up; // @[Top.scala 1149:22]
  wire  n396_valid_down; // @[Top.scala 1149:22]
  wire [15:0] n396_I0; // @[Top.scala 1149:22]
  wire [15:0] n396_I1; // @[Top.scala 1149:22]
  wire [15:0] n396_O_t0b; // @[Top.scala 1149:22]
  wire [15:0] n396_O_t1b; // @[Top.scala 1149:22]
  wire  n397_valid_up; // @[Top.scala 1153:22]
  wire  n397_valid_down; // @[Top.scala 1153:22]
  wire [15:0] n397_I_t0b; // @[Top.scala 1153:22]
  wire [15:0] n397_I_t1b; // @[Top.scala 1153:22]
  wire [15:0] n397_O; // @[Top.scala 1153:22]
  wire  n398_valid_up; // @[Top.scala 1156:22]
  wire  n398_valid_down; // @[Top.scala 1156:22]
  wire [15:0] n398_I0; // @[Top.scala 1156:22]
  wire [15:0] n398_I1; // @[Top.scala 1156:22]
  wire [15:0] n398_O_t0b; // @[Top.scala 1156:22]
  wire [15:0] n398_O_t1b; // @[Top.scala 1156:22]
  wire  n399_valid_up; // @[Top.scala 1160:22]
  wire  n399_valid_down; // @[Top.scala 1160:22]
  wire [15:0] n399_I0; // @[Top.scala 1160:22]
  wire [15:0] n399_I1; // @[Top.scala 1160:22]
  wire [15:0] n399_O_t0b; // @[Top.scala 1160:22]
  wire [15:0] n399_O_t1b; // @[Top.scala 1160:22]
  wire  n400_valid_up; // @[Top.scala 1164:22]
  wire  n400_valid_down; // @[Top.scala 1164:22]
  wire [15:0] n400_I0_t0b; // @[Top.scala 1164:22]
  wire [15:0] n400_I0_t1b; // @[Top.scala 1164:22]
  wire [15:0] n400_I1_t0b; // @[Top.scala 1164:22]
  wire [15:0] n400_I1_t1b; // @[Top.scala 1164:22]
  wire [15:0] n400_O_t0b_t0b; // @[Top.scala 1164:22]
  wire [15:0] n400_O_t0b_t1b; // @[Top.scala 1164:22]
  wire [15:0] n400_O_t1b_t0b; // @[Top.scala 1164:22]
  wire [15:0] n400_O_t1b_t1b; // @[Top.scala 1164:22]
  wire  n401_clock; // @[Top.scala 1168:22]
  wire  n401_reset; // @[Top.scala 1168:22]
  wire  n401_valid_up; // @[Top.scala 1168:22]
  wire  n401_valid_down; // @[Top.scala 1168:22]
  wire [15:0] n401_I_t0b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n401_I_t0b_t1b; // @[Top.scala 1168:22]
  wire [15:0] n401_I_t1b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n401_I_t1b_t1b; // @[Top.scala 1168:22]
  wire [15:0] n401_O_t0b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n401_O_t0b_t1b; // @[Top.scala 1168:22]
  wire [15:0] n401_O_t1b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n401_O_t1b_t1b; // @[Top.scala 1168:22]
  wire  n402_valid_up; // @[Top.scala 1171:22]
  wire  n402_valid_down; // @[Top.scala 1171:22]
  wire  n402_I0; // @[Top.scala 1171:22]
  wire [15:0] n402_I1_t0b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n402_I1_t0b_t1b; // @[Top.scala 1171:22]
  wire [15:0] n402_I1_t1b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n402_I1_t1b_t1b; // @[Top.scala 1171:22]
  wire  n402_O_t0b; // @[Top.scala 1171:22]
  wire [15:0] n402_O_t1b_t0b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n402_O_t1b_t0b_t1b; // @[Top.scala 1171:22]
  wire [15:0] n402_O_t1b_t1b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n402_O_t1b_t1b_t1b; // @[Top.scala 1171:22]
  wire  n403_valid_up; // @[Top.scala 1175:22]
  wire  n403_valid_down; // @[Top.scala 1175:22]
  wire  n403_I_t0b; // @[Top.scala 1175:22]
  wire [15:0] n403_I_t1b_t0b_t0b; // @[Top.scala 1175:22]
  wire [15:0] n403_I_t1b_t0b_t1b; // @[Top.scala 1175:22]
  wire [15:0] n403_I_t1b_t1b_t0b; // @[Top.scala 1175:22]
  wire [15:0] n403_I_t1b_t1b_t1b; // @[Top.scala 1175:22]
  wire [15:0] n403_O_t0b; // @[Top.scala 1175:22]
  wire [15:0] n403_O_t1b; // @[Top.scala 1175:22]
  wire  n405_valid_up; // @[Top.scala 1178:22]
  wire  n405_valid_down; // @[Top.scala 1178:22]
  wire [15:0] n405_I0; // @[Top.scala 1178:22]
  wire [15:0] n405_I1_t0b; // @[Top.scala 1178:22]
  wire [15:0] n405_I1_t1b; // @[Top.scala 1178:22]
  wire [15:0] n405_O_t0b; // @[Top.scala 1178:22]
  wire [15:0] n405_O_t1b_t0b; // @[Top.scala 1178:22]
  wire [15:0] n405_O_t1b_t1b; // @[Top.scala 1178:22]
  Fst n371 ( // @[Top.scala 1075:22]
    .valid_up(n371_valid_up),
    .valid_down(n371_valid_down),
    .I_t0b(n371_I_t0b),
    .O(n371_O)
  );
  FIFO_1 n404 ( // @[Top.scala 1078:22]
    .clock(n404_clock),
    .reset(n404_reset),
    .valid_up(n404_valid_up),
    .valid_down(n404_valid_down),
    .I(n404_I),
    .O(n404_O)
  );
  FIFO_1 n392 ( // @[Top.scala 1081:22]
    .clock(n392_clock),
    .reset(n392_reset),
    .valid_up(n392_valid_up),
    .valid_down(n392_valid_down),
    .I(n392_I),
    .O(n392_O)
  );
  Snd n372 ( // @[Top.scala 1084:22]
    .valid_up(n372_valid_up),
    .valid_down(n372_valid_down),
    .I_t1b_t0b(n372_I_t1b_t0b),
    .I_t1b_t1b(n372_I_t1b_t1b),
    .O_t0b(n372_O_t0b),
    .O_t1b(n372_O_t1b)
  );
  Fst_1 n373 ( // @[Top.scala 1087:22]
    .valid_up(n373_valid_up),
    .valid_down(n373_valid_down),
    .I_t0b(n373_I_t0b),
    .O(n373_O)
  );
  Snd_1 n374 ( // @[Top.scala 1090:22]
    .valid_up(n374_valid_up),
    .valid_down(n374_valid_down),
    .I_t1b(n374_I_t1b),
    .O(n374_O)
  );
  AtomTuple_1 n375 ( // @[Top.scala 1093:22]
    .valid_up(n375_valid_up),
    .valid_down(n375_valid_down),
    .I0(n375_I0),
    .I1(n375_I1),
    .O_t0b(n375_O_t0b),
    .O_t1b(n375_O_t1b)
  );
  Add n376 ( // @[Top.scala 1097:22]
    .valid_up(n376_valid_up),
    .valid_down(n376_valid_down),
    .I_t0b(n376_I_t0b),
    .I_t1b(n376_I_t1b),
    .O(n376_O)
  );
  InitialDelayCounter_30 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n379 ( // @[Top.scala 1101:22]
    .valid_up(n379_valid_up),
    .valid_down(n379_valid_down),
    .I0(n379_I0),
    .O_t0b(n379_O_t0b)
  );
  RShift n380 ( // @[Top.scala 1105:22]
    .valid_up(n380_valid_up),
    .valid_down(n380_valid_down),
    .I_t0b(n380_I_t0b),
    .O(n380_O)
  );
  AtomTuple_1 n381 ( // @[Top.scala 1108:22]
    .valid_up(n381_valid_up),
    .valid_down(n381_valid_down),
    .I0(n381_I0),
    .I1(n381_I1),
    .O_t0b(n381_O_t0b),
    .O_t1b(n381_O_t1b)
  );
  Eq n382 ( // @[Top.scala 1112:22]
    .valid_up(n382_valid_up),
    .valid_down(n382_valid_down),
    .I_t0b(n382_I_t0b),
    .I_t1b(n382_I_t1b),
    .O(n382_O)
  );
  InitialDelayCounter_30 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n385 ( // @[Top.scala 1116:22]
    .valid_up(n385_valid_up),
    .valid_down(n385_valid_down),
    .I0(n385_I0),
    .I1(n385_I1),
    .O_t0b(n385_O_t0b),
    .O_t1b(n385_O_t1b)
  );
  Add n386 ( // @[Top.scala 1120:22]
    .valid_up(n386_valid_up),
    .valid_down(n386_valid_down),
    .I_t0b(n386_I_t0b),
    .I_t1b(n386_I_t1b),
    .O(n386_O)
  );
  AtomTuple_1 n387 ( // @[Top.scala 1123:22]
    .valid_up(n387_valid_up),
    .valid_down(n387_valid_down),
    .I0(n387_I0),
    .I1(n387_I1),
    .O_t0b(n387_O_t0b),
    .O_t1b(n387_O_t1b)
  );
  AtomTuple_9 n388 ( // @[Top.scala 1127:22]
    .valid_up(n388_valid_up),
    .valid_down(n388_valid_down),
    .I0(n388_I0),
    .I1_t0b(n388_I1_t0b),
    .I1_t1b(n388_I1_t1b),
    .O_t0b(n388_O_t0b),
    .O_t1b_t0b(n388_O_t1b_t0b),
    .O_t1b_t1b(n388_O_t1b_t1b)
  );
  If n389 ( // @[Top.scala 1131:22]
    .valid_up(n389_valid_up),
    .valid_down(n389_valid_down),
    .I_t0b(n389_I_t0b),
    .I_t1b_t0b(n389_I_t1b_t0b),
    .I_t1b_t1b(n389_I_t1b_t1b),
    .O(n389_O)
  );
  AtomTuple_1 n390 ( // @[Top.scala 1134:22]
    .valid_up(n390_valid_up),
    .valid_down(n390_valid_down),
    .I0(n390_I0),
    .I1(n390_I1),
    .O_t0b(n390_O_t0b),
    .O_t1b(n390_O_t1b)
  );
  Mul n391 ( // @[Top.scala 1138:22]
    .clock(n391_clock),
    .reset(n391_reset),
    .valid_up(n391_valid_up),
    .valid_down(n391_valid_down),
    .I_t0b(n391_I_t0b),
    .I_t1b(n391_I_t1b),
    .O(n391_O)
  );
  AtomTuple_1 n393 ( // @[Top.scala 1141:22]
    .valid_up(n393_valid_up),
    .valid_down(n393_valid_down),
    .I0(n393_I0),
    .I1(n393_I1),
    .O_t0b(n393_O_t0b),
    .O_t1b(n393_O_t1b)
  );
  Lt n394 ( // @[Top.scala 1145:22]
    .valid_up(n394_valid_up),
    .valid_down(n394_valid_down),
    .I_t0b(n394_I_t0b),
    .I_t1b(n394_I_t1b),
    .O(n394_O)
  );
  InitialDelayCounter_30 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n396 ( // @[Top.scala 1149:22]
    .valid_up(n396_valid_up),
    .valid_down(n396_valid_down),
    .I0(n396_I0),
    .I1(n396_I1),
    .O_t0b(n396_O_t0b),
    .O_t1b(n396_O_t1b)
  );
  Sub n397 ( // @[Top.scala 1153:22]
    .valid_up(n397_valid_up),
    .valid_down(n397_valid_down),
    .I_t0b(n397_I_t0b),
    .I_t1b(n397_I_t1b),
    .O(n397_O)
  );
  AtomTuple_1 n398 ( // @[Top.scala 1156:22]
    .valid_up(n398_valid_up),
    .valid_down(n398_valid_down),
    .I0(n398_I0),
    .I1(n398_I1),
    .O_t0b(n398_O_t0b),
    .O_t1b(n398_O_t1b)
  );
  AtomTuple_1 n399 ( // @[Top.scala 1160:22]
    .valid_up(n399_valid_up),
    .valid_down(n399_valid_down),
    .I0(n399_I0),
    .I1(n399_I1),
    .O_t0b(n399_O_t0b),
    .O_t1b(n399_O_t1b)
  );
  AtomTuple_15 n400 ( // @[Top.scala 1164:22]
    .valid_up(n400_valid_up),
    .valid_down(n400_valid_down),
    .I0_t0b(n400_I0_t0b),
    .I0_t1b(n400_I0_t1b),
    .I1_t0b(n400_I1_t0b),
    .I1_t1b(n400_I1_t1b),
    .O_t0b_t0b(n400_O_t0b_t0b),
    .O_t0b_t1b(n400_O_t0b_t1b),
    .O_t1b_t0b(n400_O_t1b_t0b),
    .O_t1b_t1b(n400_O_t1b_t1b)
  );
  FIFO_3 n401 ( // @[Top.scala 1168:22]
    .clock(n401_clock),
    .reset(n401_reset),
    .valid_up(n401_valid_up),
    .valid_down(n401_valid_down),
    .I_t0b_t0b(n401_I_t0b_t0b),
    .I_t0b_t1b(n401_I_t0b_t1b),
    .I_t1b_t0b(n401_I_t1b_t0b),
    .I_t1b_t1b(n401_I_t1b_t1b),
    .O_t0b_t0b(n401_O_t0b_t0b),
    .O_t0b_t1b(n401_O_t0b_t1b),
    .O_t1b_t0b(n401_O_t1b_t0b),
    .O_t1b_t1b(n401_O_t1b_t1b)
  );
  AtomTuple_16 n402 ( // @[Top.scala 1171:22]
    .valid_up(n402_valid_up),
    .valid_down(n402_valid_down),
    .I0(n402_I0),
    .I1_t0b_t0b(n402_I1_t0b_t0b),
    .I1_t0b_t1b(n402_I1_t0b_t1b),
    .I1_t1b_t0b(n402_I1_t1b_t0b),
    .I1_t1b_t1b(n402_I1_t1b_t1b),
    .O_t0b(n402_O_t0b),
    .O_t1b_t0b_t0b(n402_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n402_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n402_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n402_O_t1b_t1b_t1b)
  );
  If_1 n403 ( // @[Top.scala 1175:22]
    .valid_up(n403_valid_up),
    .valid_down(n403_valid_down),
    .I_t0b(n403_I_t0b),
    .I_t1b_t0b_t0b(n403_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n403_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n403_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n403_I_t1b_t1b_t1b),
    .O_t0b(n403_O_t0b),
    .O_t1b(n403_O_t1b)
  );
  AtomTuple_3 n405 ( // @[Top.scala 1178:22]
    .valid_up(n405_valid_up),
    .valid_down(n405_valid_down),
    .I0(n405_I0),
    .I1_t0b(n405_I1_t0b),
    .I1_t1b(n405_I1_t1b),
    .O_t0b(n405_O_t0b),
    .O_t1b_t0b(n405_O_t1b_t0b),
    .O_t1b_t1b(n405_O_t1b_t1b)
  );
  assign valid_down = n405_valid_down; // @[Top.scala 1183:16]
  assign O_t0b = n405_O_t0b; // @[Top.scala 1182:7]
  assign O_t1b_t0b = n405_O_t1b_t0b; // @[Top.scala 1182:7]
  assign O_t1b_t1b = n405_O_t1b_t1b; // @[Top.scala 1182:7]
  assign n371_valid_up = valid_up; // @[Top.scala 1077:19]
  assign n371_I_t0b = I_t0b; // @[Top.scala 1076:12]
  assign n404_clock = clock;
  assign n404_reset = reset;
  assign n404_valid_up = n371_valid_down; // @[Top.scala 1080:19]
  assign n404_I = n371_O; // @[Top.scala 1079:12]
  assign n392_clock = clock;
  assign n392_reset = reset;
  assign n392_valid_up = n371_valid_down; // @[Top.scala 1083:19]
  assign n392_I = n371_O; // @[Top.scala 1082:12]
  assign n372_valid_up = valid_up; // @[Top.scala 1086:19]
  assign n372_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1085:12]
  assign n372_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1085:12]
  assign n373_valid_up = n372_valid_down; // @[Top.scala 1089:19]
  assign n373_I_t0b = n372_O_t0b; // @[Top.scala 1088:12]
  assign n374_valid_up = n372_valid_down; // @[Top.scala 1092:19]
  assign n374_I_t1b = n372_O_t1b; // @[Top.scala 1091:12]
  assign n375_valid_up = n373_valid_down & n374_valid_down; // @[Top.scala 1096:19]
  assign n375_I0 = n373_O; // @[Top.scala 1094:13]
  assign n375_I1 = n374_O; // @[Top.scala 1095:13]
  assign n376_valid_up = n375_valid_down; // @[Top.scala 1099:19]
  assign n376_I_t0b = n375_O_t0b; // @[Top.scala 1098:12]
  assign n376_I_t1b = n375_O_t1b; // @[Top.scala 1098:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n379_valid_up = n376_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 1104:19]
  assign n379_I0 = n376_O; // @[Top.scala 1102:13]
  assign n380_valid_up = n379_valid_down; // @[Top.scala 1107:19]
  assign n380_I_t0b = n379_O_t0b; // @[Top.scala 1106:12]
  assign n381_valid_up = n380_valid_down & n373_valid_down; // @[Top.scala 1111:19]
  assign n381_I0 = n380_O; // @[Top.scala 1109:13]
  assign n381_I1 = n373_O; // @[Top.scala 1110:13]
  assign n382_valid_up = n381_valid_down; // @[Top.scala 1114:19]
  assign n382_I_t0b = n381_O_t0b; // @[Top.scala 1113:12]
  assign n382_I_t1b = n381_O_t1b; // @[Top.scala 1113:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n385_valid_up = n380_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1119:19]
  assign n385_I0 = n380_O; // @[Top.scala 1117:13]
  assign n385_I1 = 16'h1; // @[Top.scala 1118:13]
  assign n386_valid_up = n385_valid_down; // @[Top.scala 1122:19]
  assign n386_I_t0b = n385_O_t0b; // @[Top.scala 1121:12]
  assign n386_I_t1b = n385_O_t1b; // @[Top.scala 1121:12]
  assign n387_valid_up = n386_valid_down & n380_valid_down; // @[Top.scala 1126:19]
  assign n387_I0 = n386_O; // @[Top.scala 1124:13]
  assign n387_I1 = n380_O; // @[Top.scala 1125:13]
  assign n388_valid_up = n382_valid_down & n387_valid_down; // @[Top.scala 1130:19]
  assign n388_I0 = n382_O[0]; // @[Top.scala 1128:13]
  assign n388_I1_t0b = n387_O_t0b; // @[Top.scala 1129:13]
  assign n388_I1_t1b = n387_O_t1b; // @[Top.scala 1129:13]
  assign n389_valid_up = n388_valid_down; // @[Top.scala 1133:19]
  assign n389_I_t0b = n388_O_t0b; // @[Top.scala 1132:12]
  assign n389_I_t1b_t0b = n388_O_t1b_t0b; // @[Top.scala 1132:12]
  assign n389_I_t1b_t1b = n388_O_t1b_t1b; // @[Top.scala 1132:12]
  assign n390_valid_up = n389_valid_down; // @[Top.scala 1137:19]
  assign n390_I0 = n389_O; // @[Top.scala 1135:13]
  assign n390_I1 = n389_O; // @[Top.scala 1136:13]
  assign n391_clock = clock;
  assign n391_reset = reset;
  assign n391_valid_up = n390_valid_down; // @[Top.scala 1140:19]
  assign n391_I_t0b = n390_O_t0b; // @[Top.scala 1139:12]
  assign n391_I_t1b = n390_O_t1b; // @[Top.scala 1139:12]
  assign n393_valid_up = n392_valid_down & n391_valid_down; // @[Top.scala 1144:19]
  assign n393_I0 = n392_O; // @[Top.scala 1142:13]
  assign n393_I1 = n391_O; // @[Top.scala 1143:13]
  assign n394_valid_up = n393_valid_down; // @[Top.scala 1147:19]
  assign n394_I_t0b = n393_O_t0b; // @[Top.scala 1146:12]
  assign n394_I_t1b = n393_O_t1b; // @[Top.scala 1146:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n396_valid_up = n389_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1152:19]
  assign n396_I0 = n389_O; // @[Top.scala 1150:13]
  assign n396_I1 = 16'h1; // @[Top.scala 1151:13]
  assign n397_valid_up = n396_valid_down; // @[Top.scala 1155:19]
  assign n397_I_t0b = n396_O_t0b; // @[Top.scala 1154:12]
  assign n397_I_t1b = n396_O_t1b; // @[Top.scala 1154:12]
  assign n398_valid_up = n373_valid_down & n397_valid_down; // @[Top.scala 1159:19]
  assign n398_I0 = n373_O; // @[Top.scala 1157:13]
  assign n398_I1 = n397_O; // @[Top.scala 1158:13]
  assign n399_valid_up = n389_valid_down & n374_valid_down; // @[Top.scala 1163:19]
  assign n399_I0 = n389_O; // @[Top.scala 1161:13]
  assign n399_I1 = n374_O; // @[Top.scala 1162:13]
  assign n400_valid_up = n398_valid_down & n399_valid_down; // @[Top.scala 1167:19]
  assign n400_I0_t0b = n398_O_t0b; // @[Top.scala 1165:13]
  assign n400_I0_t1b = n398_O_t1b; // @[Top.scala 1165:13]
  assign n400_I1_t0b = n399_O_t0b; // @[Top.scala 1166:13]
  assign n400_I1_t1b = n399_O_t1b; // @[Top.scala 1166:13]
  assign n401_clock = clock;
  assign n401_reset = reset;
  assign n401_valid_up = n400_valid_down; // @[Top.scala 1170:19]
  assign n401_I_t0b_t0b = n400_O_t0b_t0b; // @[Top.scala 1169:12]
  assign n401_I_t0b_t1b = n400_O_t0b_t1b; // @[Top.scala 1169:12]
  assign n401_I_t1b_t0b = n400_O_t1b_t0b; // @[Top.scala 1169:12]
  assign n401_I_t1b_t1b = n400_O_t1b_t1b; // @[Top.scala 1169:12]
  assign n402_valid_up = n394_valid_down & n401_valid_down; // @[Top.scala 1174:19]
  assign n402_I0 = n394_O[0]; // @[Top.scala 1172:13]
  assign n402_I1_t0b_t0b = n401_O_t0b_t0b; // @[Top.scala 1173:13]
  assign n402_I1_t0b_t1b = n401_O_t0b_t1b; // @[Top.scala 1173:13]
  assign n402_I1_t1b_t0b = n401_O_t1b_t0b; // @[Top.scala 1173:13]
  assign n402_I1_t1b_t1b = n401_O_t1b_t1b; // @[Top.scala 1173:13]
  assign n403_valid_up = n402_valid_down; // @[Top.scala 1177:19]
  assign n403_I_t0b = n402_O_t0b; // @[Top.scala 1176:12]
  assign n403_I_t1b_t0b_t0b = n402_O_t1b_t0b_t0b; // @[Top.scala 1176:12]
  assign n403_I_t1b_t0b_t1b = n402_O_t1b_t0b_t1b; // @[Top.scala 1176:12]
  assign n403_I_t1b_t1b_t0b = n402_O_t1b_t1b_t0b; // @[Top.scala 1176:12]
  assign n403_I_t1b_t1b_t1b = n402_O_t1b_t1b_t1b; // @[Top.scala 1176:12]
  assign n405_valid_up = n404_valid_down & n403_valid_down; // @[Top.scala 1181:19]
  assign n405_I0 = n404_O; // @[Top.scala 1179:13]
  assign n405_I1_t0b = n403_O_t0b; // @[Top.scala 1180:13]
  assign n405_I1_t1b = n403_O_t1b; // @[Top.scala 1180:13]
endmodule
module MapS_10(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_10 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_10 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_10 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_10 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_10(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_10 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_33(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [4:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 5'h1f; // @[InitialDelayCounter.scala 17:17]
  wire [4:0] _T_4 = value + 5'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 5'h1f; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[4:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 5'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Module_11(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n410_valid_up; // @[Top.scala 1189:22]
  wire  n410_valid_down; // @[Top.scala 1189:22]
  wire [15:0] n410_I_t0b; // @[Top.scala 1189:22]
  wire [15:0] n410_O; // @[Top.scala 1189:22]
  wire  n443_clock; // @[Top.scala 1192:22]
  wire  n443_reset; // @[Top.scala 1192:22]
  wire  n443_valid_up; // @[Top.scala 1192:22]
  wire  n443_valid_down; // @[Top.scala 1192:22]
  wire [15:0] n443_I; // @[Top.scala 1192:22]
  wire [15:0] n443_O; // @[Top.scala 1192:22]
  wire  n431_clock; // @[Top.scala 1195:22]
  wire  n431_reset; // @[Top.scala 1195:22]
  wire  n431_valid_up; // @[Top.scala 1195:22]
  wire  n431_valid_down; // @[Top.scala 1195:22]
  wire [15:0] n431_I; // @[Top.scala 1195:22]
  wire [15:0] n431_O; // @[Top.scala 1195:22]
  wire  n411_valid_up; // @[Top.scala 1198:22]
  wire  n411_valid_down; // @[Top.scala 1198:22]
  wire [15:0] n411_I_t1b_t0b; // @[Top.scala 1198:22]
  wire [15:0] n411_I_t1b_t1b; // @[Top.scala 1198:22]
  wire [15:0] n411_O_t0b; // @[Top.scala 1198:22]
  wire [15:0] n411_O_t1b; // @[Top.scala 1198:22]
  wire  n412_valid_up; // @[Top.scala 1201:22]
  wire  n412_valid_down; // @[Top.scala 1201:22]
  wire [15:0] n412_I_t0b; // @[Top.scala 1201:22]
  wire [15:0] n412_O; // @[Top.scala 1201:22]
  wire  n413_valid_up; // @[Top.scala 1204:22]
  wire  n413_valid_down; // @[Top.scala 1204:22]
  wire [15:0] n413_I_t1b; // @[Top.scala 1204:22]
  wire [15:0] n413_O; // @[Top.scala 1204:22]
  wire  n414_valid_up; // @[Top.scala 1207:22]
  wire  n414_valid_down; // @[Top.scala 1207:22]
  wire [15:0] n414_I0; // @[Top.scala 1207:22]
  wire [15:0] n414_I1; // @[Top.scala 1207:22]
  wire [15:0] n414_O_t0b; // @[Top.scala 1207:22]
  wire [15:0] n414_O_t1b; // @[Top.scala 1207:22]
  wire  n415_valid_up; // @[Top.scala 1211:22]
  wire  n415_valid_down; // @[Top.scala 1211:22]
  wire [15:0] n415_I_t0b; // @[Top.scala 1211:22]
  wire [15:0] n415_I_t1b; // @[Top.scala 1211:22]
  wire [15:0] n415_O; // @[Top.scala 1211:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n418_valid_up; // @[Top.scala 1215:22]
  wire  n418_valid_down; // @[Top.scala 1215:22]
  wire [15:0] n418_I0; // @[Top.scala 1215:22]
  wire [15:0] n418_O_t0b; // @[Top.scala 1215:22]
  wire  n419_valid_up; // @[Top.scala 1219:22]
  wire  n419_valid_down; // @[Top.scala 1219:22]
  wire [15:0] n419_I_t0b; // @[Top.scala 1219:22]
  wire [15:0] n419_O; // @[Top.scala 1219:22]
  wire  n420_valid_up; // @[Top.scala 1222:22]
  wire  n420_valid_down; // @[Top.scala 1222:22]
  wire [15:0] n420_I0; // @[Top.scala 1222:22]
  wire [15:0] n420_I1; // @[Top.scala 1222:22]
  wire [15:0] n420_O_t0b; // @[Top.scala 1222:22]
  wire [15:0] n420_O_t1b; // @[Top.scala 1222:22]
  wire  n421_valid_up; // @[Top.scala 1226:22]
  wire  n421_valid_down; // @[Top.scala 1226:22]
  wire [15:0] n421_I_t0b; // @[Top.scala 1226:22]
  wire [15:0] n421_I_t1b; // @[Top.scala 1226:22]
  wire [15:0] n421_O; // @[Top.scala 1226:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n424_valid_up; // @[Top.scala 1230:22]
  wire  n424_valid_down; // @[Top.scala 1230:22]
  wire [15:0] n424_I0; // @[Top.scala 1230:22]
  wire [15:0] n424_I1; // @[Top.scala 1230:22]
  wire [15:0] n424_O_t0b; // @[Top.scala 1230:22]
  wire [15:0] n424_O_t1b; // @[Top.scala 1230:22]
  wire  n425_valid_up; // @[Top.scala 1234:22]
  wire  n425_valid_down; // @[Top.scala 1234:22]
  wire [15:0] n425_I_t0b; // @[Top.scala 1234:22]
  wire [15:0] n425_I_t1b; // @[Top.scala 1234:22]
  wire [15:0] n425_O; // @[Top.scala 1234:22]
  wire  n426_valid_up; // @[Top.scala 1237:22]
  wire  n426_valid_down; // @[Top.scala 1237:22]
  wire [15:0] n426_I0; // @[Top.scala 1237:22]
  wire [15:0] n426_I1; // @[Top.scala 1237:22]
  wire [15:0] n426_O_t0b; // @[Top.scala 1237:22]
  wire [15:0] n426_O_t1b; // @[Top.scala 1237:22]
  wire  n427_valid_up; // @[Top.scala 1241:22]
  wire  n427_valid_down; // @[Top.scala 1241:22]
  wire  n427_I0; // @[Top.scala 1241:22]
  wire [15:0] n427_I1_t0b; // @[Top.scala 1241:22]
  wire [15:0] n427_I1_t1b; // @[Top.scala 1241:22]
  wire  n427_O_t0b; // @[Top.scala 1241:22]
  wire [15:0] n427_O_t1b_t0b; // @[Top.scala 1241:22]
  wire [15:0] n427_O_t1b_t1b; // @[Top.scala 1241:22]
  wire  n428_valid_up; // @[Top.scala 1245:22]
  wire  n428_valid_down; // @[Top.scala 1245:22]
  wire  n428_I_t0b; // @[Top.scala 1245:22]
  wire [15:0] n428_I_t1b_t0b; // @[Top.scala 1245:22]
  wire [15:0] n428_I_t1b_t1b; // @[Top.scala 1245:22]
  wire [15:0] n428_O; // @[Top.scala 1245:22]
  wire  n429_valid_up; // @[Top.scala 1248:22]
  wire  n429_valid_down; // @[Top.scala 1248:22]
  wire [15:0] n429_I0; // @[Top.scala 1248:22]
  wire [15:0] n429_I1; // @[Top.scala 1248:22]
  wire [15:0] n429_O_t0b; // @[Top.scala 1248:22]
  wire [15:0] n429_O_t1b; // @[Top.scala 1248:22]
  wire  n430_clock; // @[Top.scala 1252:22]
  wire  n430_reset; // @[Top.scala 1252:22]
  wire  n430_valid_up; // @[Top.scala 1252:22]
  wire  n430_valid_down; // @[Top.scala 1252:22]
  wire [15:0] n430_I_t0b; // @[Top.scala 1252:22]
  wire [15:0] n430_I_t1b; // @[Top.scala 1252:22]
  wire [15:0] n430_O; // @[Top.scala 1252:22]
  wire  n432_valid_up; // @[Top.scala 1255:22]
  wire  n432_valid_down; // @[Top.scala 1255:22]
  wire [15:0] n432_I0; // @[Top.scala 1255:22]
  wire [15:0] n432_I1; // @[Top.scala 1255:22]
  wire [15:0] n432_O_t0b; // @[Top.scala 1255:22]
  wire [15:0] n432_O_t1b; // @[Top.scala 1255:22]
  wire  n433_valid_up; // @[Top.scala 1259:22]
  wire  n433_valid_down; // @[Top.scala 1259:22]
  wire [15:0] n433_I_t0b; // @[Top.scala 1259:22]
  wire [15:0] n433_I_t1b; // @[Top.scala 1259:22]
  wire [15:0] n433_O; // @[Top.scala 1259:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n435_valid_up; // @[Top.scala 1263:22]
  wire  n435_valid_down; // @[Top.scala 1263:22]
  wire [15:0] n435_I0; // @[Top.scala 1263:22]
  wire [15:0] n435_I1; // @[Top.scala 1263:22]
  wire [15:0] n435_O_t0b; // @[Top.scala 1263:22]
  wire [15:0] n435_O_t1b; // @[Top.scala 1263:22]
  wire  n436_valid_up; // @[Top.scala 1267:22]
  wire  n436_valid_down; // @[Top.scala 1267:22]
  wire [15:0] n436_I_t0b; // @[Top.scala 1267:22]
  wire [15:0] n436_I_t1b; // @[Top.scala 1267:22]
  wire [15:0] n436_O; // @[Top.scala 1267:22]
  wire  n437_valid_up; // @[Top.scala 1270:22]
  wire  n437_valid_down; // @[Top.scala 1270:22]
  wire [15:0] n437_I0; // @[Top.scala 1270:22]
  wire [15:0] n437_I1; // @[Top.scala 1270:22]
  wire [15:0] n437_O_t0b; // @[Top.scala 1270:22]
  wire [15:0] n437_O_t1b; // @[Top.scala 1270:22]
  wire  n438_valid_up; // @[Top.scala 1274:22]
  wire  n438_valid_down; // @[Top.scala 1274:22]
  wire [15:0] n438_I0; // @[Top.scala 1274:22]
  wire [15:0] n438_I1; // @[Top.scala 1274:22]
  wire [15:0] n438_O_t0b; // @[Top.scala 1274:22]
  wire [15:0] n438_O_t1b; // @[Top.scala 1274:22]
  wire  n439_valid_up; // @[Top.scala 1278:22]
  wire  n439_valid_down; // @[Top.scala 1278:22]
  wire [15:0] n439_I0_t0b; // @[Top.scala 1278:22]
  wire [15:0] n439_I0_t1b; // @[Top.scala 1278:22]
  wire [15:0] n439_I1_t0b; // @[Top.scala 1278:22]
  wire [15:0] n439_I1_t1b; // @[Top.scala 1278:22]
  wire [15:0] n439_O_t0b_t0b; // @[Top.scala 1278:22]
  wire [15:0] n439_O_t0b_t1b; // @[Top.scala 1278:22]
  wire [15:0] n439_O_t1b_t0b; // @[Top.scala 1278:22]
  wire [15:0] n439_O_t1b_t1b; // @[Top.scala 1278:22]
  wire  n440_clock; // @[Top.scala 1282:22]
  wire  n440_reset; // @[Top.scala 1282:22]
  wire  n440_valid_up; // @[Top.scala 1282:22]
  wire  n440_valid_down; // @[Top.scala 1282:22]
  wire [15:0] n440_I_t0b_t0b; // @[Top.scala 1282:22]
  wire [15:0] n440_I_t0b_t1b; // @[Top.scala 1282:22]
  wire [15:0] n440_I_t1b_t0b; // @[Top.scala 1282:22]
  wire [15:0] n440_I_t1b_t1b; // @[Top.scala 1282:22]
  wire [15:0] n440_O_t0b_t0b; // @[Top.scala 1282:22]
  wire [15:0] n440_O_t0b_t1b; // @[Top.scala 1282:22]
  wire [15:0] n440_O_t1b_t0b; // @[Top.scala 1282:22]
  wire [15:0] n440_O_t1b_t1b; // @[Top.scala 1282:22]
  wire  n441_valid_up; // @[Top.scala 1285:22]
  wire  n441_valid_down; // @[Top.scala 1285:22]
  wire  n441_I0; // @[Top.scala 1285:22]
  wire [15:0] n441_I1_t0b_t0b; // @[Top.scala 1285:22]
  wire [15:0] n441_I1_t0b_t1b; // @[Top.scala 1285:22]
  wire [15:0] n441_I1_t1b_t0b; // @[Top.scala 1285:22]
  wire [15:0] n441_I1_t1b_t1b; // @[Top.scala 1285:22]
  wire  n441_O_t0b; // @[Top.scala 1285:22]
  wire [15:0] n441_O_t1b_t0b_t0b; // @[Top.scala 1285:22]
  wire [15:0] n441_O_t1b_t0b_t1b; // @[Top.scala 1285:22]
  wire [15:0] n441_O_t1b_t1b_t0b; // @[Top.scala 1285:22]
  wire [15:0] n441_O_t1b_t1b_t1b; // @[Top.scala 1285:22]
  wire  n442_valid_up; // @[Top.scala 1289:22]
  wire  n442_valid_down; // @[Top.scala 1289:22]
  wire  n442_I_t0b; // @[Top.scala 1289:22]
  wire [15:0] n442_I_t1b_t0b_t0b; // @[Top.scala 1289:22]
  wire [15:0] n442_I_t1b_t0b_t1b; // @[Top.scala 1289:22]
  wire [15:0] n442_I_t1b_t1b_t0b; // @[Top.scala 1289:22]
  wire [15:0] n442_I_t1b_t1b_t1b; // @[Top.scala 1289:22]
  wire [15:0] n442_O_t0b; // @[Top.scala 1289:22]
  wire [15:0] n442_O_t1b; // @[Top.scala 1289:22]
  wire  n444_valid_up; // @[Top.scala 1292:22]
  wire  n444_valid_down; // @[Top.scala 1292:22]
  wire [15:0] n444_I0; // @[Top.scala 1292:22]
  wire [15:0] n444_I1_t0b; // @[Top.scala 1292:22]
  wire [15:0] n444_I1_t1b; // @[Top.scala 1292:22]
  wire [15:0] n444_O_t0b; // @[Top.scala 1292:22]
  wire [15:0] n444_O_t1b_t0b; // @[Top.scala 1292:22]
  wire [15:0] n444_O_t1b_t1b; // @[Top.scala 1292:22]
  Fst n410 ( // @[Top.scala 1189:22]
    .valid_up(n410_valid_up),
    .valid_down(n410_valid_down),
    .I_t0b(n410_I_t0b),
    .O(n410_O)
  );
  FIFO_1 n443 ( // @[Top.scala 1192:22]
    .clock(n443_clock),
    .reset(n443_reset),
    .valid_up(n443_valid_up),
    .valid_down(n443_valid_down),
    .I(n443_I),
    .O(n443_O)
  );
  FIFO_1 n431 ( // @[Top.scala 1195:22]
    .clock(n431_clock),
    .reset(n431_reset),
    .valid_up(n431_valid_up),
    .valid_down(n431_valid_down),
    .I(n431_I),
    .O(n431_O)
  );
  Snd n411 ( // @[Top.scala 1198:22]
    .valid_up(n411_valid_up),
    .valid_down(n411_valid_down),
    .I_t1b_t0b(n411_I_t1b_t0b),
    .I_t1b_t1b(n411_I_t1b_t1b),
    .O_t0b(n411_O_t0b),
    .O_t1b(n411_O_t1b)
  );
  Fst_1 n412 ( // @[Top.scala 1201:22]
    .valid_up(n412_valid_up),
    .valid_down(n412_valid_down),
    .I_t0b(n412_I_t0b),
    .O(n412_O)
  );
  Snd_1 n413 ( // @[Top.scala 1204:22]
    .valid_up(n413_valid_up),
    .valid_down(n413_valid_down),
    .I_t1b(n413_I_t1b),
    .O(n413_O)
  );
  AtomTuple_1 n414 ( // @[Top.scala 1207:22]
    .valid_up(n414_valid_up),
    .valid_down(n414_valid_down),
    .I0(n414_I0),
    .I1(n414_I1),
    .O_t0b(n414_O_t0b),
    .O_t1b(n414_O_t1b)
  );
  Add n415 ( // @[Top.scala 1211:22]
    .valid_up(n415_valid_up),
    .valid_down(n415_valid_down),
    .I_t0b(n415_I_t0b),
    .I_t1b(n415_I_t1b),
    .O(n415_O)
  );
  InitialDelayCounter_33 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n418 ( // @[Top.scala 1215:22]
    .valid_up(n418_valid_up),
    .valid_down(n418_valid_down),
    .I0(n418_I0),
    .O_t0b(n418_O_t0b)
  );
  RShift n419 ( // @[Top.scala 1219:22]
    .valid_up(n419_valid_up),
    .valid_down(n419_valid_down),
    .I_t0b(n419_I_t0b),
    .O(n419_O)
  );
  AtomTuple_1 n420 ( // @[Top.scala 1222:22]
    .valid_up(n420_valid_up),
    .valid_down(n420_valid_down),
    .I0(n420_I0),
    .I1(n420_I1),
    .O_t0b(n420_O_t0b),
    .O_t1b(n420_O_t1b)
  );
  Eq n421 ( // @[Top.scala 1226:22]
    .valid_up(n421_valid_up),
    .valid_down(n421_valid_down),
    .I_t0b(n421_I_t0b),
    .I_t1b(n421_I_t1b),
    .O(n421_O)
  );
  InitialDelayCounter_33 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n424 ( // @[Top.scala 1230:22]
    .valid_up(n424_valid_up),
    .valid_down(n424_valid_down),
    .I0(n424_I0),
    .I1(n424_I1),
    .O_t0b(n424_O_t0b),
    .O_t1b(n424_O_t1b)
  );
  Add n425 ( // @[Top.scala 1234:22]
    .valid_up(n425_valid_up),
    .valid_down(n425_valid_down),
    .I_t0b(n425_I_t0b),
    .I_t1b(n425_I_t1b),
    .O(n425_O)
  );
  AtomTuple_1 n426 ( // @[Top.scala 1237:22]
    .valid_up(n426_valid_up),
    .valid_down(n426_valid_down),
    .I0(n426_I0),
    .I1(n426_I1),
    .O_t0b(n426_O_t0b),
    .O_t1b(n426_O_t1b)
  );
  AtomTuple_9 n427 ( // @[Top.scala 1241:22]
    .valid_up(n427_valid_up),
    .valid_down(n427_valid_down),
    .I0(n427_I0),
    .I1_t0b(n427_I1_t0b),
    .I1_t1b(n427_I1_t1b),
    .O_t0b(n427_O_t0b),
    .O_t1b_t0b(n427_O_t1b_t0b),
    .O_t1b_t1b(n427_O_t1b_t1b)
  );
  If n428 ( // @[Top.scala 1245:22]
    .valid_up(n428_valid_up),
    .valid_down(n428_valid_down),
    .I_t0b(n428_I_t0b),
    .I_t1b_t0b(n428_I_t1b_t0b),
    .I_t1b_t1b(n428_I_t1b_t1b),
    .O(n428_O)
  );
  AtomTuple_1 n429 ( // @[Top.scala 1248:22]
    .valid_up(n429_valid_up),
    .valid_down(n429_valid_down),
    .I0(n429_I0),
    .I1(n429_I1),
    .O_t0b(n429_O_t0b),
    .O_t1b(n429_O_t1b)
  );
  Mul n430 ( // @[Top.scala 1252:22]
    .clock(n430_clock),
    .reset(n430_reset),
    .valid_up(n430_valid_up),
    .valid_down(n430_valid_down),
    .I_t0b(n430_I_t0b),
    .I_t1b(n430_I_t1b),
    .O(n430_O)
  );
  AtomTuple_1 n432 ( // @[Top.scala 1255:22]
    .valid_up(n432_valid_up),
    .valid_down(n432_valid_down),
    .I0(n432_I0),
    .I1(n432_I1),
    .O_t0b(n432_O_t0b),
    .O_t1b(n432_O_t1b)
  );
  Lt n433 ( // @[Top.scala 1259:22]
    .valid_up(n433_valid_up),
    .valid_down(n433_valid_down),
    .I_t0b(n433_I_t0b),
    .I_t1b(n433_I_t1b),
    .O(n433_O)
  );
  InitialDelayCounter_33 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n435 ( // @[Top.scala 1263:22]
    .valid_up(n435_valid_up),
    .valid_down(n435_valid_down),
    .I0(n435_I0),
    .I1(n435_I1),
    .O_t0b(n435_O_t0b),
    .O_t1b(n435_O_t1b)
  );
  Sub n436 ( // @[Top.scala 1267:22]
    .valid_up(n436_valid_up),
    .valid_down(n436_valid_down),
    .I_t0b(n436_I_t0b),
    .I_t1b(n436_I_t1b),
    .O(n436_O)
  );
  AtomTuple_1 n437 ( // @[Top.scala 1270:22]
    .valid_up(n437_valid_up),
    .valid_down(n437_valid_down),
    .I0(n437_I0),
    .I1(n437_I1),
    .O_t0b(n437_O_t0b),
    .O_t1b(n437_O_t1b)
  );
  AtomTuple_1 n438 ( // @[Top.scala 1274:22]
    .valid_up(n438_valid_up),
    .valid_down(n438_valid_down),
    .I0(n438_I0),
    .I1(n438_I1),
    .O_t0b(n438_O_t0b),
    .O_t1b(n438_O_t1b)
  );
  AtomTuple_15 n439 ( // @[Top.scala 1278:22]
    .valid_up(n439_valid_up),
    .valid_down(n439_valid_down),
    .I0_t0b(n439_I0_t0b),
    .I0_t1b(n439_I0_t1b),
    .I1_t0b(n439_I1_t0b),
    .I1_t1b(n439_I1_t1b),
    .O_t0b_t0b(n439_O_t0b_t0b),
    .O_t0b_t1b(n439_O_t0b_t1b),
    .O_t1b_t0b(n439_O_t1b_t0b),
    .O_t1b_t1b(n439_O_t1b_t1b)
  );
  FIFO_3 n440 ( // @[Top.scala 1282:22]
    .clock(n440_clock),
    .reset(n440_reset),
    .valid_up(n440_valid_up),
    .valid_down(n440_valid_down),
    .I_t0b_t0b(n440_I_t0b_t0b),
    .I_t0b_t1b(n440_I_t0b_t1b),
    .I_t1b_t0b(n440_I_t1b_t0b),
    .I_t1b_t1b(n440_I_t1b_t1b),
    .O_t0b_t0b(n440_O_t0b_t0b),
    .O_t0b_t1b(n440_O_t0b_t1b),
    .O_t1b_t0b(n440_O_t1b_t0b),
    .O_t1b_t1b(n440_O_t1b_t1b)
  );
  AtomTuple_16 n441 ( // @[Top.scala 1285:22]
    .valid_up(n441_valid_up),
    .valid_down(n441_valid_down),
    .I0(n441_I0),
    .I1_t0b_t0b(n441_I1_t0b_t0b),
    .I1_t0b_t1b(n441_I1_t0b_t1b),
    .I1_t1b_t0b(n441_I1_t1b_t0b),
    .I1_t1b_t1b(n441_I1_t1b_t1b),
    .O_t0b(n441_O_t0b),
    .O_t1b_t0b_t0b(n441_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n441_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n441_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n441_O_t1b_t1b_t1b)
  );
  If_1 n442 ( // @[Top.scala 1289:22]
    .valid_up(n442_valid_up),
    .valid_down(n442_valid_down),
    .I_t0b(n442_I_t0b),
    .I_t1b_t0b_t0b(n442_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n442_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n442_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n442_I_t1b_t1b_t1b),
    .O_t0b(n442_O_t0b),
    .O_t1b(n442_O_t1b)
  );
  AtomTuple_3 n444 ( // @[Top.scala 1292:22]
    .valid_up(n444_valid_up),
    .valid_down(n444_valid_down),
    .I0(n444_I0),
    .I1_t0b(n444_I1_t0b),
    .I1_t1b(n444_I1_t1b),
    .O_t0b(n444_O_t0b),
    .O_t1b_t0b(n444_O_t1b_t0b),
    .O_t1b_t1b(n444_O_t1b_t1b)
  );
  assign valid_down = n444_valid_down; // @[Top.scala 1297:16]
  assign O_t0b = n444_O_t0b; // @[Top.scala 1296:7]
  assign O_t1b_t0b = n444_O_t1b_t0b; // @[Top.scala 1296:7]
  assign O_t1b_t1b = n444_O_t1b_t1b; // @[Top.scala 1296:7]
  assign n410_valid_up = valid_up; // @[Top.scala 1191:19]
  assign n410_I_t0b = I_t0b; // @[Top.scala 1190:12]
  assign n443_clock = clock;
  assign n443_reset = reset;
  assign n443_valid_up = n410_valid_down; // @[Top.scala 1194:19]
  assign n443_I = n410_O; // @[Top.scala 1193:12]
  assign n431_clock = clock;
  assign n431_reset = reset;
  assign n431_valid_up = n410_valid_down; // @[Top.scala 1197:19]
  assign n431_I = n410_O; // @[Top.scala 1196:12]
  assign n411_valid_up = valid_up; // @[Top.scala 1200:19]
  assign n411_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1199:12]
  assign n411_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1199:12]
  assign n412_valid_up = n411_valid_down; // @[Top.scala 1203:19]
  assign n412_I_t0b = n411_O_t0b; // @[Top.scala 1202:12]
  assign n413_valid_up = n411_valid_down; // @[Top.scala 1206:19]
  assign n413_I_t1b = n411_O_t1b; // @[Top.scala 1205:12]
  assign n414_valid_up = n412_valid_down & n413_valid_down; // @[Top.scala 1210:19]
  assign n414_I0 = n412_O; // @[Top.scala 1208:13]
  assign n414_I1 = n413_O; // @[Top.scala 1209:13]
  assign n415_valid_up = n414_valid_down; // @[Top.scala 1213:19]
  assign n415_I_t0b = n414_O_t0b; // @[Top.scala 1212:12]
  assign n415_I_t1b = n414_O_t1b; // @[Top.scala 1212:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n418_valid_up = n415_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 1218:19]
  assign n418_I0 = n415_O; // @[Top.scala 1216:13]
  assign n419_valid_up = n418_valid_down; // @[Top.scala 1221:19]
  assign n419_I_t0b = n418_O_t0b; // @[Top.scala 1220:12]
  assign n420_valid_up = n419_valid_down & n412_valid_down; // @[Top.scala 1225:19]
  assign n420_I0 = n419_O; // @[Top.scala 1223:13]
  assign n420_I1 = n412_O; // @[Top.scala 1224:13]
  assign n421_valid_up = n420_valid_down; // @[Top.scala 1228:19]
  assign n421_I_t0b = n420_O_t0b; // @[Top.scala 1227:12]
  assign n421_I_t1b = n420_O_t1b; // @[Top.scala 1227:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n424_valid_up = n419_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1233:19]
  assign n424_I0 = n419_O; // @[Top.scala 1231:13]
  assign n424_I1 = 16'h1; // @[Top.scala 1232:13]
  assign n425_valid_up = n424_valid_down; // @[Top.scala 1236:19]
  assign n425_I_t0b = n424_O_t0b; // @[Top.scala 1235:12]
  assign n425_I_t1b = n424_O_t1b; // @[Top.scala 1235:12]
  assign n426_valid_up = n425_valid_down & n419_valid_down; // @[Top.scala 1240:19]
  assign n426_I0 = n425_O; // @[Top.scala 1238:13]
  assign n426_I1 = n419_O; // @[Top.scala 1239:13]
  assign n427_valid_up = n421_valid_down & n426_valid_down; // @[Top.scala 1244:19]
  assign n427_I0 = n421_O[0]; // @[Top.scala 1242:13]
  assign n427_I1_t0b = n426_O_t0b; // @[Top.scala 1243:13]
  assign n427_I1_t1b = n426_O_t1b; // @[Top.scala 1243:13]
  assign n428_valid_up = n427_valid_down; // @[Top.scala 1247:19]
  assign n428_I_t0b = n427_O_t0b; // @[Top.scala 1246:12]
  assign n428_I_t1b_t0b = n427_O_t1b_t0b; // @[Top.scala 1246:12]
  assign n428_I_t1b_t1b = n427_O_t1b_t1b; // @[Top.scala 1246:12]
  assign n429_valid_up = n428_valid_down; // @[Top.scala 1251:19]
  assign n429_I0 = n428_O; // @[Top.scala 1249:13]
  assign n429_I1 = n428_O; // @[Top.scala 1250:13]
  assign n430_clock = clock;
  assign n430_reset = reset;
  assign n430_valid_up = n429_valid_down; // @[Top.scala 1254:19]
  assign n430_I_t0b = n429_O_t0b; // @[Top.scala 1253:12]
  assign n430_I_t1b = n429_O_t1b; // @[Top.scala 1253:12]
  assign n432_valid_up = n431_valid_down & n430_valid_down; // @[Top.scala 1258:19]
  assign n432_I0 = n431_O; // @[Top.scala 1256:13]
  assign n432_I1 = n430_O; // @[Top.scala 1257:13]
  assign n433_valid_up = n432_valid_down; // @[Top.scala 1261:19]
  assign n433_I_t0b = n432_O_t0b; // @[Top.scala 1260:12]
  assign n433_I_t1b = n432_O_t1b; // @[Top.scala 1260:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n435_valid_up = n428_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1266:19]
  assign n435_I0 = n428_O; // @[Top.scala 1264:13]
  assign n435_I1 = 16'h1; // @[Top.scala 1265:13]
  assign n436_valid_up = n435_valid_down; // @[Top.scala 1269:19]
  assign n436_I_t0b = n435_O_t0b; // @[Top.scala 1268:12]
  assign n436_I_t1b = n435_O_t1b; // @[Top.scala 1268:12]
  assign n437_valid_up = n412_valid_down & n436_valid_down; // @[Top.scala 1273:19]
  assign n437_I0 = n412_O; // @[Top.scala 1271:13]
  assign n437_I1 = n436_O; // @[Top.scala 1272:13]
  assign n438_valid_up = n428_valid_down & n413_valid_down; // @[Top.scala 1277:19]
  assign n438_I0 = n428_O; // @[Top.scala 1275:13]
  assign n438_I1 = n413_O; // @[Top.scala 1276:13]
  assign n439_valid_up = n437_valid_down & n438_valid_down; // @[Top.scala 1281:19]
  assign n439_I0_t0b = n437_O_t0b; // @[Top.scala 1279:13]
  assign n439_I0_t1b = n437_O_t1b; // @[Top.scala 1279:13]
  assign n439_I1_t0b = n438_O_t0b; // @[Top.scala 1280:13]
  assign n439_I1_t1b = n438_O_t1b; // @[Top.scala 1280:13]
  assign n440_clock = clock;
  assign n440_reset = reset;
  assign n440_valid_up = n439_valid_down; // @[Top.scala 1284:19]
  assign n440_I_t0b_t0b = n439_O_t0b_t0b; // @[Top.scala 1283:12]
  assign n440_I_t0b_t1b = n439_O_t0b_t1b; // @[Top.scala 1283:12]
  assign n440_I_t1b_t0b = n439_O_t1b_t0b; // @[Top.scala 1283:12]
  assign n440_I_t1b_t1b = n439_O_t1b_t1b; // @[Top.scala 1283:12]
  assign n441_valid_up = n433_valid_down & n440_valid_down; // @[Top.scala 1288:19]
  assign n441_I0 = n433_O[0]; // @[Top.scala 1286:13]
  assign n441_I1_t0b_t0b = n440_O_t0b_t0b; // @[Top.scala 1287:13]
  assign n441_I1_t0b_t1b = n440_O_t0b_t1b; // @[Top.scala 1287:13]
  assign n441_I1_t1b_t0b = n440_O_t1b_t0b; // @[Top.scala 1287:13]
  assign n441_I1_t1b_t1b = n440_O_t1b_t1b; // @[Top.scala 1287:13]
  assign n442_valid_up = n441_valid_down; // @[Top.scala 1291:19]
  assign n442_I_t0b = n441_O_t0b; // @[Top.scala 1290:12]
  assign n442_I_t1b_t0b_t0b = n441_O_t1b_t0b_t0b; // @[Top.scala 1290:12]
  assign n442_I_t1b_t0b_t1b = n441_O_t1b_t0b_t1b; // @[Top.scala 1290:12]
  assign n442_I_t1b_t1b_t0b = n441_O_t1b_t1b_t0b; // @[Top.scala 1290:12]
  assign n442_I_t1b_t1b_t1b = n441_O_t1b_t1b_t1b; // @[Top.scala 1290:12]
  assign n444_valid_up = n443_valid_down & n442_valid_down; // @[Top.scala 1295:19]
  assign n444_I0 = n443_O; // @[Top.scala 1293:13]
  assign n444_I1_t0b = n442_O_t0b; // @[Top.scala 1294:13]
  assign n444_I1_t1b = n442_O_t1b; // @[Top.scala 1294:13]
endmodule
module MapS_11(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_11 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_11 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_11 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_11 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_11(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_11 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_36(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [5:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 6'h22; // @[InitialDelayCounter.scala 17:17]
  wire [5:0] _T_4 = value + 6'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 6'h22; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[5:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 6'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Module_12(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n449_valid_up; // @[Top.scala 1303:22]
  wire  n449_valid_down; // @[Top.scala 1303:22]
  wire [15:0] n449_I_t0b; // @[Top.scala 1303:22]
  wire [15:0] n449_O; // @[Top.scala 1303:22]
  wire  n482_clock; // @[Top.scala 1306:22]
  wire  n482_reset; // @[Top.scala 1306:22]
  wire  n482_valid_up; // @[Top.scala 1306:22]
  wire  n482_valid_down; // @[Top.scala 1306:22]
  wire [15:0] n482_I; // @[Top.scala 1306:22]
  wire [15:0] n482_O; // @[Top.scala 1306:22]
  wire  n470_clock; // @[Top.scala 1309:22]
  wire  n470_reset; // @[Top.scala 1309:22]
  wire  n470_valid_up; // @[Top.scala 1309:22]
  wire  n470_valid_down; // @[Top.scala 1309:22]
  wire [15:0] n470_I; // @[Top.scala 1309:22]
  wire [15:0] n470_O; // @[Top.scala 1309:22]
  wire  n450_valid_up; // @[Top.scala 1312:22]
  wire  n450_valid_down; // @[Top.scala 1312:22]
  wire [15:0] n450_I_t1b_t0b; // @[Top.scala 1312:22]
  wire [15:0] n450_I_t1b_t1b; // @[Top.scala 1312:22]
  wire [15:0] n450_O_t0b; // @[Top.scala 1312:22]
  wire [15:0] n450_O_t1b; // @[Top.scala 1312:22]
  wire  n451_valid_up; // @[Top.scala 1315:22]
  wire  n451_valid_down; // @[Top.scala 1315:22]
  wire [15:0] n451_I_t0b; // @[Top.scala 1315:22]
  wire [15:0] n451_O; // @[Top.scala 1315:22]
  wire  n452_valid_up; // @[Top.scala 1318:22]
  wire  n452_valid_down; // @[Top.scala 1318:22]
  wire [15:0] n452_I_t1b; // @[Top.scala 1318:22]
  wire [15:0] n452_O; // @[Top.scala 1318:22]
  wire  n453_valid_up; // @[Top.scala 1321:22]
  wire  n453_valid_down; // @[Top.scala 1321:22]
  wire [15:0] n453_I0; // @[Top.scala 1321:22]
  wire [15:0] n453_I1; // @[Top.scala 1321:22]
  wire [15:0] n453_O_t0b; // @[Top.scala 1321:22]
  wire [15:0] n453_O_t1b; // @[Top.scala 1321:22]
  wire  n454_valid_up; // @[Top.scala 1325:22]
  wire  n454_valid_down; // @[Top.scala 1325:22]
  wire [15:0] n454_I_t0b; // @[Top.scala 1325:22]
  wire [15:0] n454_I_t1b; // @[Top.scala 1325:22]
  wire [15:0] n454_O; // @[Top.scala 1325:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n457_valid_up; // @[Top.scala 1329:22]
  wire  n457_valid_down; // @[Top.scala 1329:22]
  wire [15:0] n457_I0; // @[Top.scala 1329:22]
  wire [15:0] n457_O_t0b; // @[Top.scala 1329:22]
  wire  n458_valid_up; // @[Top.scala 1333:22]
  wire  n458_valid_down; // @[Top.scala 1333:22]
  wire [15:0] n458_I_t0b; // @[Top.scala 1333:22]
  wire [15:0] n458_O; // @[Top.scala 1333:22]
  wire  n459_valid_up; // @[Top.scala 1336:22]
  wire  n459_valid_down; // @[Top.scala 1336:22]
  wire [15:0] n459_I0; // @[Top.scala 1336:22]
  wire [15:0] n459_I1; // @[Top.scala 1336:22]
  wire [15:0] n459_O_t0b; // @[Top.scala 1336:22]
  wire [15:0] n459_O_t1b; // @[Top.scala 1336:22]
  wire  n460_valid_up; // @[Top.scala 1340:22]
  wire  n460_valid_down; // @[Top.scala 1340:22]
  wire [15:0] n460_I_t0b; // @[Top.scala 1340:22]
  wire [15:0] n460_I_t1b; // @[Top.scala 1340:22]
  wire [15:0] n460_O; // @[Top.scala 1340:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n463_valid_up; // @[Top.scala 1344:22]
  wire  n463_valid_down; // @[Top.scala 1344:22]
  wire [15:0] n463_I0; // @[Top.scala 1344:22]
  wire [15:0] n463_I1; // @[Top.scala 1344:22]
  wire [15:0] n463_O_t0b; // @[Top.scala 1344:22]
  wire [15:0] n463_O_t1b; // @[Top.scala 1344:22]
  wire  n464_valid_up; // @[Top.scala 1348:22]
  wire  n464_valid_down; // @[Top.scala 1348:22]
  wire [15:0] n464_I_t0b; // @[Top.scala 1348:22]
  wire [15:0] n464_I_t1b; // @[Top.scala 1348:22]
  wire [15:0] n464_O; // @[Top.scala 1348:22]
  wire  n465_valid_up; // @[Top.scala 1351:22]
  wire  n465_valid_down; // @[Top.scala 1351:22]
  wire [15:0] n465_I0; // @[Top.scala 1351:22]
  wire [15:0] n465_I1; // @[Top.scala 1351:22]
  wire [15:0] n465_O_t0b; // @[Top.scala 1351:22]
  wire [15:0] n465_O_t1b; // @[Top.scala 1351:22]
  wire  n466_valid_up; // @[Top.scala 1355:22]
  wire  n466_valid_down; // @[Top.scala 1355:22]
  wire  n466_I0; // @[Top.scala 1355:22]
  wire [15:0] n466_I1_t0b; // @[Top.scala 1355:22]
  wire [15:0] n466_I1_t1b; // @[Top.scala 1355:22]
  wire  n466_O_t0b; // @[Top.scala 1355:22]
  wire [15:0] n466_O_t1b_t0b; // @[Top.scala 1355:22]
  wire [15:0] n466_O_t1b_t1b; // @[Top.scala 1355:22]
  wire  n467_valid_up; // @[Top.scala 1359:22]
  wire  n467_valid_down; // @[Top.scala 1359:22]
  wire  n467_I_t0b; // @[Top.scala 1359:22]
  wire [15:0] n467_I_t1b_t0b; // @[Top.scala 1359:22]
  wire [15:0] n467_I_t1b_t1b; // @[Top.scala 1359:22]
  wire [15:0] n467_O; // @[Top.scala 1359:22]
  wire  n468_valid_up; // @[Top.scala 1362:22]
  wire  n468_valid_down; // @[Top.scala 1362:22]
  wire [15:0] n468_I0; // @[Top.scala 1362:22]
  wire [15:0] n468_I1; // @[Top.scala 1362:22]
  wire [15:0] n468_O_t0b; // @[Top.scala 1362:22]
  wire [15:0] n468_O_t1b; // @[Top.scala 1362:22]
  wire  n469_clock; // @[Top.scala 1366:22]
  wire  n469_reset; // @[Top.scala 1366:22]
  wire  n469_valid_up; // @[Top.scala 1366:22]
  wire  n469_valid_down; // @[Top.scala 1366:22]
  wire [15:0] n469_I_t0b; // @[Top.scala 1366:22]
  wire [15:0] n469_I_t1b; // @[Top.scala 1366:22]
  wire [15:0] n469_O; // @[Top.scala 1366:22]
  wire  n471_valid_up; // @[Top.scala 1369:22]
  wire  n471_valid_down; // @[Top.scala 1369:22]
  wire [15:0] n471_I0; // @[Top.scala 1369:22]
  wire [15:0] n471_I1; // @[Top.scala 1369:22]
  wire [15:0] n471_O_t0b; // @[Top.scala 1369:22]
  wire [15:0] n471_O_t1b; // @[Top.scala 1369:22]
  wire  n472_valid_up; // @[Top.scala 1373:22]
  wire  n472_valid_down; // @[Top.scala 1373:22]
  wire [15:0] n472_I_t0b; // @[Top.scala 1373:22]
  wire [15:0] n472_I_t1b; // @[Top.scala 1373:22]
  wire [15:0] n472_O; // @[Top.scala 1373:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n474_valid_up; // @[Top.scala 1377:22]
  wire  n474_valid_down; // @[Top.scala 1377:22]
  wire [15:0] n474_I0; // @[Top.scala 1377:22]
  wire [15:0] n474_I1; // @[Top.scala 1377:22]
  wire [15:0] n474_O_t0b; // @[Top.scala 1377:22]
  wire [15:0] n474_O_t1b; // @[Top.scala 1377:22]
  wire  n475_valid_up; // @[Top.scala 1381:22]
  wire  n475_valid_down; // @[Top.scala 1381:22]
  wire [15:0] n475_I_t0b; // @[Top.scala 1381:22]
  wire [15:0] n475_I_t1b; // @[Top.scala 1381:22]
  wire [15:0] n475_O; // @[Top.scala 1381:22]
  wire  n476_valid_up; // @[Top.scala 1384:22]
  wire  n476_valid_down; // @[Top.scala 1384:22]
  wire [15:0] n476_I0; // @[Top.scala 1384:22]
  wire [15:0] n476_I1; // @[Top.scala 1384:22]
  wire [15:0] n476_O_t0b; // @[Top.scala 1384:22]
  wire [15:0] n476_O_t1b; // @[Top.scala 1384:22]
  wire  n477_valid_up; // @[Top.scala 1388:22]
  wire  n477_valid_down; // @[Top.scala 1388:22]
  wire [15:0] n477_I0; // @[Top.scala 1388:22]
  wire [15:0] n477_I1; // @[Top.scala 1388:22]
  wire [15:0] n477_O_t0b; // @[Top.scala 1388:22]
  wire [15:0] n477_O_t1b; // @[Top.scala 1388:22]
  wire  n478_valid_up; // @[Top.scala 1392:22]
  wire  n478_valid_down; // @[Top.scala 1392:22]
  wire [15:0] n478_I0_t0b; // @[Top.scala 1392:22]
  wire [15:0] n478_I0_t1b; // @[Top.scala 1392:22]
  wire [15:0] n478_I1_t0b; // @[Top.scala 1392:22]
  wire [15:0] n478_I1_t1b; // @[Top.scala 1392:22]
  wire [15:0] n478_O_t0b_t0b; // @[Top.scala 1392:22]
  wire [15:0] n478_O_t0b_t1b; // @[Top.scala 1392:22]
  wire [15:0] n478_O_t1b_t0b; // @[Top.scala 1392:22]
  wire [15:0] n478_O_t1b_t1b; // @[Top.scala 1392:22]
  wire  n479_clock; // @[Top.scala 1396:22]
  wire  n479_reset; // @[Top.scala 1396:22]
  wire  n479_valid_up; // @[Top.scala 1396:22]
  wire  n479_valid_down; // @[Top.scala 1396:22]
  wire [15:0] n479_I_t0b_t0b; // @[Top.scala 1396:22]
  wire [15:0] n479_I_t0b_t1b; // @[Top.scala 1396:22]
  wire [15:0] n479_I_t1b_t0b; // @[Top.scala 1396:22]
  wire [15:0] n479_I_t1b_t1b; // @[Top.scala 1396:22]
  wire [15:0] n479_O_t0b_t0b; // @[Top.scala 1396:22]
  wire [15:0] n479_O_t0b_t1b; // @[Top.scala 1396:22]
  wire [15:0] n479_O_t1b_t0b; // @[Top.scala 1396:22]
  wire [15:0] n479_O_t1b_t1b; // @[Top.scala 1396:22]
  wire  n480_valid_up; // @[Top.scala 1399:22]
  wire  n480_valid_down; // @[Top.scala 1399:22]
  wire  n480_I0; // @[Top.scala 1399:22]
  wire [15:0] n480_I1_t0b_t0b; // @[Top.scala 1399:22]
  wire [15:0] n480_I1_t0b_t1b; // @[Top.scala 1399:22]
  wire [15:0] n480_I1_t1b_t0b; // @[Top.scala 1399:22]
  wire [15:0] n480_I1_t1b_t1b; // @[Top.scala 1399:22]
  wire  n480_O_t0b; // @[Top.scala 1399:22]
  wire [15:0] n480_O_t1b_t0b_t0b; // @[Top.scala 1399:22]
  wire [15:0] n480_O_t1b_t0b_t1b; // @[Top.scala 1399:22]
  wire [15:0] n480_O_t1b_t1b_t0b; // @[Top.scala 1399:22]
  wire [15:0] n480_O_t1b_t1b_t1b; // @[Top.scala 1399:22]
  wire  n481_valid_up; // @[Top.scala 1403:22]
  wire  n481_valid_down; // @[Top.scala 1403:22]
  wire  n481_I_t0b; // @[Top.scala 1403:22]
  wire [15:0] n481_I_t1b_t0b_t0b; // @[Top.scala 1403:22]
  wire [15:0] n481_I_t1b_t0b_t1b; // @[Top.scala 1403:22]
  wire [15:0] n481_I_t1b_t1b_t0b; // @[Top.scala 1403:22]
  wire [15:0] n481_I_t1b_t1b_t1b; // @[Top.scala 1403:22]
  wire [15:0] n481_O_t0b; // @[Top.scala 1403:22]
  wire [15:0] n481_O_t1b; // @[Top.scala 1403:22]
  wire  n483_valid_up; // @[Top.scala 1406:22]
  wire  n483_valid_down; // @[Top.scala 1406:22]
  wire [15:0] n483_I0; // @[Top.scala 1406:22]
  wire [15:0] n483_I1_t0b; // @[Top.scala 1406:22]
  wire [15:0] n483_I1_t1b; // @[Top.scala 1406:22]
  wire [15:0] n483_O_t0b; // @[Top.scala 1406:22]
  wire [15:0] n483_O_t1b_t0b; // @[Top.scala 1406:22]
  wire [15:0] n483_O_t1b_t1b; // @[Top.scala 1406:22]
  Fst n449 ( // @[Top.scala 1303:22]
    .valid_up(n449_valid_up),
    .valid_down(n449_valid_down),
    .I_t0b(n449_I_t0b),
    .O(n449_O)
  );
  FIFO_1 n482 ( // @[Top.scala 1306:22]
    .clock(n482_clock),
    .reset(n482_reset),
    .valid_up(n482_valid_up),
    .valid_down(n482_valid_down),
    .I(n482_I),
    .O(n482_O)
  );
  FIFO_1 n470 ( // @[Top.scala 1309:22]
    .clock(n470_clock),
    .reset(n470_reset),
    .valid_up(n470_valid_up),
    .valid_down(n470_valid_down),
    .I(n470_I),
    .O(n470_O)
  );
  Snd n450 ( // @[Top.scala 1312:22]
    .valid_up(n450_valid_up),
    .valid_down(n450_valid_down),
    .I_t1b_t0b(n450_I_t1b_t0b),
    .I_t1b_t1b(n450_I_t1b_t1b),
    .O_t0b(n450_O_t0b),
    .O_t1b(n450_O_t1b)
  );
  Fst_1 n451 ( // @[Top.scala 1315:22]
    .valid_up(n451_valid_up),
    .valid_down(n451_valid_down),
    .I_t0b(n451_I_t0b),
    .O(n451_O)
  );
  Snd_1 n452 ( // @[Top.scala 1318:22]
    .valid_up(n452_valid_up),
    .valid_down(n452_valid_down),
    .I_t1b(n452_I_t1b),
    .O(n452_O)
  );
  AtomTuple_1 n453 ( // @[Top.scala 1321:22]
    .valid_up(n453_valid_up),
    .valid_down(n453_valid_down),
    .I0(n453_I0),
    .I1(n453_I1),
    .O_t0b(n453_O_t0b),
    .O_t1b(n453_O_t1b)
  );
  Add n454 ( // @[Top.scala 1325:22]
    .valid_up(n454_valid_up),
    .valid_down(n454_valid_down),
    .I_t0b(n454_I_t0b),
    .I_t1b(n454_I_t1b),
    .O(n454_O)
  );
  InitialDelayCounter_36 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n457 ( // @[Top.scala 1329:22]
    .valid_up(n457_valid_up),
    .valid_down(n457_valid_down),
    .I0(n457_I0),
    .O_t0b(n457_O_t0b)
  );
  RShift n458 ( // @[Top.scala 1333:22]
    .valid_up(n458_valid_up),
    .valid_down(n458_valid_down),
    .I_t0b(n458_I_t0b),
    .O(n458_O)
  );
  AtomTuple_1 n459 ( // @[Top.scala 1336:22]
    .valid_up(n459_valid_up),
    .valid_down(n459_valid_down),
    .I0(n459_I0),
    .I1(n459_I1),
    .O_t0b(n459_O_t0b),
    .O_t1b(n459_O_t1b)
  );
  Eq n460 ( // @[Top.scala 1340:22]
    .valid_up(n460_valid_up),
    .valid_down(n460_valid_down),
    .I_t0b(n460_I_t0b),
    .I_t1b(n460_I_t1b),
    .O(n460_O)
  );
  InitialDelayCounter_36 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n463 ( // @[Top.scala 1344:22]
    .valid_up(n463_valid_up),
    .valid_down(n463_valid_down),
    .I0(n463_I0),
    .I1(n463_I1),
    .O_t0b(n463_O_t0b),
    .O_t1b(n463_O_t1b)
  );
  Add n464 ( // @[Top.scala 1348:22]
    .valid_up(n464_valid_up),
    .valid_down(n464_valid_down),
    .I_t0b(n464_I_t0b),
    .I_t1b(n464_I_t1b),
    .O(n464_O)
  );
  AtomTuple_1 n465 ( // @[Top.scala 1351:22]
    .valid_up(n465_valid_up),
    .valid_down(n465_valid_down),
    .I0(n465_I0),
    .I1(n465_I1),
    .O_t0b(n465_O_t0b),
    .O_t1b(n465_O_t1b)
  );
  AtomTuple_9 n466 ( // @[Top.scala 1355:22]
    .valid_up(n466_valid_up),
    .valid_down(n466_valid_down),
    .I0(n466_I0),
    .I1_t0b(n466_I1_t0b),
    .I1_t1b(n466_I1_t1b),
    .O_t0b(n466_O_t0b),
    .O_t1b_t0b(n466_O_t1b_t0b),
    .O_t1b_t1b(n466_O_t1b_t1b)
  );
  If n467 ( // @[Top.scala 1359:22]
    .valid_up(n467_valid_up),
    .valid_down(n467_valid_down),
    .I_t0b(n467_I_t0b),
    .I_t1b_t0b(n467_I_t1b_t0b),
    .I_t1b_t1b(n467_I_t1b_t1b),
    .O(n467_O)
  );
  AtomTuple_1 n468 ( // @[Top.scala 1362:22]
    .valid_up(n468_valid_up),
    .valid_down(n468_valid_down),
    .I0(n468_I0),
    .I1(n468_I1),
    .O_t0b(n468_O_t0b),
    .O_t1b(n468_O_t1b)
  );
  Mul n469 ( // @[Top.scala 1366:22]
    .clock(n469_clock),
    .reset(n469_reset),
    .valid_up(n469_valid_up),
    .valid_down(n469_valid_down),
    .I_t0b(n469_I_t0b),
    .I_t1b(n469_I_t1b),
    .O(n469_O)
  );
  AtomTuple_1 n471 ( // @[Top.scala 1369:22]
    .valid_up(n471_valid_up),
    .valid_down(n471_valid_down),
    .I0(n471_I0),
    .I1(n471_I1),
    .O_t0b(n471_O_t0b),
    .O_t1b(n471_O_t1b)
  );
  Lt n472 ( // @[Top.scala 1373:22]
    .valid_up(n472_valid_up),
    .valid_down(n472_valid_down),
    .I_t0b(n472_I_t0b),
    .I_t1b(n472_I_t1b),
    .O(n472_O)
  );
  InitialDelayCounter_36 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n474 ( // @[Top.scala 1377:22]
    .valid_up(n474_valid_up),
    .valid_down(n474_valid_down),
    .I0(n474_I0),
    .I1(n474_I1),
    .O_t0b(n474_O_t0b),
    .O_t1b(n474_O_t1b)
  );
  Sub n475 ( // @[Top.scala 1381:22]
    .valid_up(n475_valid_up),
    .valid_down(n475_valid_down),
    .I_t0b(n475_I_t0b),
    .I_t1b(n475_I_t1b),
    .O(n475_O)
  );
  AtomTuple_1 n476 ( // @[Top.scala 1384:22]
    .valid_up(n476_valid_up),
    .valid_down(n476_valid_down),
    .I0(n476_I0),
    .I1(n476_I1),
    .O_t0b(n476_O_t0b),
    .O_t1b(n476_O_t1b)
  );
  AtomTuple_1 n477 ( // @[Top.scala 1388:22]
    .valid_up(n477_valid_up),
    .valid_down(n477_valid_down),
    .I0(n477_I0),
    .I1(n477_I1),
    .O_t0b(n477_O_t0b),
    .O_t1b(n477_O_t1b)
  );
  AtomTuple_15 n478 ( // @[Top.scala 1392:22]
    .valid_up(n478_valid_up),
    .valid_down(n478_valid_down),
    .I0_t0b(n478_I0_t0b),
    .I0_t1b(n478_I0_t1b),
    .I1_t0b(n478_I1_t0b),
    .I1_t1b(n478_I1_t1b),
    .O_t0b_t0b(n478_O_t0b_t0b),
    .O_t0b_t1b(n478_O_t0b_t1b),
    .O_t1b_t0b(n478_O_t1b_t0b),
    .O_t1b_t1b(n478_O_t1b_t1b)
  );
  FIFO_3 n479 ( // @[Top.scala 1396:22]
    .clock(n479_clock),
    .reset(n479_reset),
    .valid_up(n479_valid_up),
    .valid_down(n479_valid_down),
    .I_t0b_t0b(n479_I_t0b_t0b),
    .I_t0b_t1b(n479_I_t0b_t1b),
    .I_t1b_t0b(n479_I_t1b_t0b),
    .I_t1b_t1b(n479_I_t1b_t1b),
    .O_t0b_t0b(n479_O_t0b_t0b),
    .O_t0b_t1b(n479_O_t0b_t1b),
    .O_t1b_t0b(n479_O_t1b_t0b),
    .O_t1b_t1b(n479_O_t1b_t1b)
  );
  AtomTuple_16 n480 ( // @[Top.scala 1399:22]
    .valid_up(n480_valid_up),
    .valid_down(n480_valid_down),
    .I0(n480_I0),
    .I1_t0b_t0b(n480_I1_t0b_t0b),
    .I1_t0b_t1b(n480_I1_t0b_t1b),
    .I1_t1b_t0b(n480_I1_t1b_t0b),
    .I1_t1b_t1b(n480_I1_t1b_t1b),
    .O_t0b(n480_O_t0b),
    .O_t1b_t0b_t0b(n480_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n480_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n480_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n480_O_t1b_t1b_t1b)
  );
  If_1 n481 ( // @[Top.scala 1403:22]
    .valid_up(n481_valid_up),
    .valid_down(n481_valid_down),
    .I_t0b(n481_I_t0b),
    .I_t1b_t0b_t0b(n481_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n481_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n481_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n481_I_t1b_t1b_t1b),
    .O_t0b(n481_O_t0b),
    .O_t1b(n481_O_t1b)
  );
  AtomTuple_3 n483 ( // @[Top.scala 1406:22]
    .valid_up(n483_valid_up),
    .valid_down(n483_valid_down),
    .I0(n483_I0),
    .I1_t0b(n483_I1_t0b),
    .I1_t1b(n483_I1_t1b),
    .O_t0b(n483_O_t0b),
    .O_t1b_t0b(n483_O_t1b_t0b),
    .O_t1b_t1b(n483_O_t1b_t1b)
  );
  assign valid_down = n483_valid_down; // @[Top.scala 1411:16]
  assign O_t0b = n483_O_t0b; // @[Top.scala 1410:7]
  assign O_t1b_t0b = n483_O_t1b_t0b; // @[Top.scala 1410:7]
  assign O_t1b_t1b = n483_O_t1b_t1b; // @[Top.scala 1410:7]
  assign n449_valid_up = valid_up; // @[Top.scala 1305:19]
  assign n449_I_t0b = I_t0b; // @[Top.scala 1304:12]
  assign n482_clock = clock;
  assign n482_reset = reset;
  assign n482_valid_up = n449_valid_down; // @[Top.scala 1308:19]
  assign n482_I = n449_O; // @[Top.scala 1307:12]
  assign n470_clock = clock;
  assign n470_reset = reset;
  assign n470_valid_up = n449_valid_down; // @[Top.scala 1311:19]
  assign n470_I = n449_O; // @[Top.scala 1310:12]
  assign n450_valid_up = valid_up; // @[Top.scala 1314:19]
  assign n450_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1313:12]
  assign n450_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1313:12]
  assign n451_valid_up = n450_valid_down; // @[Top.scala 1317:19]
  assign n451_I_t0b = n450_O_t0b; // @[Top.scala 1316:12]
  assign n452_valid_up = n450_valid_down; // @[Top.scala 1320:19]
  assign n452_I_t1b = n450_O_t1b; // @[Top.scala 1319:12]
  assign n453_valid_up = n451_valid_down & n452_valid_down; // @[Top.scala 1324:19]
  assign n453_I0 = n451_O; // @[Top.scala 1322:13]
  assign n453_I1 = n452_O; // @[Top.scala 1323:13]
  assign n454_valid_up = n453_valid_down; // @[Top.scala 1327:19]
  assign n454_I_t0b = n453_O_t0b; // @[Top.scala 1326:12]
  assign n454_I_t1b = n453_O_t1b; // @[Top.scala 1326:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n457_valid_up = n454_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 1332:19]
  assign n457_I0 = n454_O; // @[Top.scala 1330:13]
  assign n458_valid_up = n457_valid_down; // @[Top.scala 1335:19]
  assign n458_I_t0b = n457_O_t0b; // @[Top.scala 1334:12]
  assign n459_valid_up = n458_valid_down & n451_valid_down; // @[Top.scala 1339:19]
  assign n459_I0 = n458_O; // @[Top.scala 1337:13]
  assign n459_I1 = n451_O; // @[Top.scala 1338:13]
  assign n460_valid_up = n459_valid_down; // @[Top.scala 1342:19]
  assign n460_I_t0b = n459_O_t0b; // @[Top.scala 1341:12]
  assign n460_I_t1b = n459_O_t1b; // @[Top.scala 1341:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n463_valid_up = n458_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1347:19]
  assign n463_I0 = n458_O; // @[Top.scala 1345:13]
  assign n463_I1 = 16'h1; // @[Top.scala 1346:13]
  assign n464_valid_up = n463_valid_down; // @[Top.scala 1350:19]
  assign n464_I_t0b = n463_O_t0b; // @[Top.scala 1349:12]
  assign n464_I_t1b = n463_O_t1b; // @[Top.scala 1349:12]
  assign n465_valid_up = n464_valid_down & n458_valid_down; // @[Top.scala 1354:19]
  assign n465_I0 = n464_O; // @[Top.scala 1352:13]
  assign n465_I1 = n458_O; // @[Top.scala 1353:13]
  assign n466_valid_up = n460_valid_down & n465_valid_down; // @[Top.scala 1358:19]
  assign n466_I0 = n460_O[0]; // @[Top.scala 1356:13]
  assign n466_I1_t0b = n465_O_t0b; // @[Top.scala 1357:13]
  assign n466_I1_t1b = n465_O_t1b; // @[Top.scala 1357:13]
  assign n467_valid_up = n466_valid_down; // @[Top.scala 1361:19]
  assign n467_I_t0b = n466_O_t0b; // @[Top.scala 1360:12]
  assign n467_I_t1b_t0b = n466_O_t1b_t0b; // @[Top.scala 1360:12]
  assign n467_I_t1b_t1b = n466_O_t1b_t1b; // @[Top.scala 1360:12]
  assign n468_valid_up = n467_valid_down; // @[Top.scala 1365:19]
  assign n468_I0 = n467_O; // @[Top.scala 1363:13]
  assign n468_I1 = n467_O; // @[Top.scala 1364:13]
  assign n469_clock = clock;
  assign n469_reset = reset;
  assign n469_valid_up = n468_valid_down; // @[Top.scala 1368:19]
  assign n469_I_t0b = n468_O_t0b; // @[Top.scala 1367:12]
  assign n469_I_t1b = n468_O_t1b; // @[Top.scala 1367:12]
  assign n471_valid_up = n470_valid_down & n469_valid_down; // @[Top.scala 1372:19]
  assign n471_I0 = n470_O; // @[Top.scala 1370:13]
  assign n471_I1 = n469_O; // @[Top.scala 1371:13]
  assign n472_valid_up = n471_valid_down; // @[Top.scala 1375:19]
  assign n472_I_t0b = n471_O_t0b; // @[Top.scala 1374:12]
  assign n472_I_t1b = n471_O_t1b; // @[Top.scala 1374:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n474_valid_up = n467_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1380:19]
  assign n474_I0 = n467_O; // @[Top.scala 1378:13]
  assign n474_I1 = 16'h1; // @[Top.scala 1379:13]
  assign n475_valid_up = n474_valid_down; // @[Top.scala 1383:19]
  assign n475_I_t0b = n474_O_t0b; // @[Top.scala 1382:12]
  assign n475_I_t1b = n474_O_t1b; // @[Top.scala 1382:12]
  assign n476_valid_up = n451_valid_down & n475_valid_down; // @[Top.scala 1387:19]
  assign n476_I0 = n451_O; // @[Top.scala 1385:13]
  assign n476_I1 = n475_O; // @[Top.scala 1386:13]
  assign n477_valid_up = n467_valid_down & n452_valid_down; // @[Top.scala 1391:19]
  assign n477_I0 = n467_O; // @[Top.scala 1389:13]
  assign n477_I1 = n452_O; // @[Top.scala 1390:13]
  assign n478_valid_up = n476_valid_down & n477_valid_down; // @[Top.scala 1395:19]
  assign n478_I0_t0b = n476_O_t0b; // @[Top.scala 1393:13]
  assign n478_I0_t1b = n476_O_t1b; // @[Top.scala 1393:13]
  assign n478_I1_t0b = n477_O_t0b; // @[Top.scala 1394:13]
  assign n478_I1_t1b = n477_O_t1b; // @[Top.scala 1394:13]
  assign n479_clock = clock;
  assign n479_reset = reset;
  assign n479_valid_up = n478_valid_down; // @[Top.scala 1398:19]
  assign n479_I_t0b_t0b = n478_O_t0b_t0b; // @[Top.scala 1397:12]
  assign n479_I_t0b_t1b = n478_O_t0b_t1b; // @[Top.scala 1397:12]
  assign n479_I_t1b_t0b = n478_O_t1b_t0b; // @[Top.scala 1397:12]
  assign n479_I_t1b_t1b = n478_O_t1b_t1b; // @[Top.scala 1397:12]
  assign n480_valid_up = n472_valid_down & n479_valid_down; // @[Top.scala 1402:19]
  assign n480_I0 = n472_O[0]; // @[Top.scala 1400:13]
  assign n480_I1_t0b_t0b = n479_O_t0b_t0b; // @[Top.scala 1401:13]
  assign n480_I1_t0b_t1b = n479_O_t0b_t1b; // @[Top.scala 1401:13]
  assign n480_I1_t1b_t0b = n479_O_t1b_t0b; // @[Top.scala 1401:13]
  assign n480_I1_t1b_t1b = n479_O_t1b_t1b; // @[Top.scala 1401:13]
  assign n481_valid_up = n480_valid_down; // @[Top.scala 1405:19]
  assign n481_I_t0b = n480_O_t0b; // @[Top.scala 1404:12]
  assign n481_I_t1b_t0b_t0b = n480_O_t1b_t0b_t0b; // @[Top.scala 1404:12]
  assign n481_I_t1b_t0b_t1b = n480_O_t1b_t0b_t1b; // @[Top.scala 1404:12]
  assign n481_I_t1b_t1b_t0b = n480_O_t1b_t1b_t0b; // @[Top.scala 1404:12]
  assign n481_I_t1b_t1b_t1b = n480_O_t1b_t1b_t1b; // @[Top.scala 1404:12]
  assign n483_valid_up = n482_valid_down & n481_valid_down; // @[Top.scala 1409:19]
  assign n483_I0 = n482_O; // @[Top.scala 1407:13]
  assign n483_I1_t0b = n481_O_t0b; // @[Top.scala 1408:13]
  assign n483_I1_t1b = n481_O_t1b; // @[Top.scala 1408:13]
endmodule
module MapS_12(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_12 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_12 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_12 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_12 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_12(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_12 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_39(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [5:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 6'h25; // @[InitialDelayCounter.scala 17:17]
  wire [5:0] _T_4 = value + 6'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 6'h25; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[5:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 6'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Module_13(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n488_valid_up; // @[Top.scala 1417:22]
  wire  n488_valid_down; // @[Top.scala 1417:22]
  wire [15:0] n488_I_t0b; // @[Top.scala 1417:22]
  wire [15:0] n488_O; // @[Top.scala 1417:22]
  wire  n521_clock; // @[Top.scala 1420:22]
  wire  n521_reset; // @[Top.scala 1420:22]
  wire  n521_valid_up; // @[Top.scala 1420:22]
  wire  n521_valid_down; // @[Top.scala 1420:22]
  wire [15:0] n521_I; // @[Top.scala 1420:22]
  wire [15:0] n521_O; // @[Top.scala 1420:22]
  wire  n509_clock; // @[Top.scala 1423:22]
  wire  n509_reset; // @[Top.scala 1423:22]
  wire  n509_valid_up; // @[Top.scala 1423:22]
  wire  n509_valid_down; // @[Top.scala 1423:22]
  wire [15:0] n509_I; // @[Top.scala 1423:22]
  wire [15:0] n509_O; // @[Top.scala 1423:22]
  wire  n489_valid_up; // @[Top.scala 1426:22]
  wire  n489_valid_down; // @[Top.scala 1426:22]
  wire [15:0] n489_I_t1b_t0b; // @[Top.scala 1426:22]
  wire [15:0] n489_I_t1b_t1b; // @[Top.scala 1426:22]
  wire [15:0] n489_O_t0b; // @[Top.scala 1426:22]
  wire [15:0] n489_O_t1b; // @[Top.scala 1426:22]
  wire  n490_valid_up; // @[Top.scala 1429:22]
  wire  n490_valid_down; // @[Top.scala 1429:22]
  wire [15:0] n490_I_t0b; // @[Top.scala 1429:22]
  wire [15:0] n490_O; // @[Top.scala 1429:22]
  wire  n491_valid_up; // @[Top.scala 1432:22]
  wire  n491_valid_down; // @[Top.scala 1432:22]
  wire [15:0] n491_I_t1b; // @[Top.scala 1432:22]
  wire [15:0] n491_O; // @[Top.scala 1432:22]
  wire  n492_valid_up; // @[Top.scala 1435:22]
  wire  n492_valid_down; // @[Top.scala 1435:22]
  wire [15:0] n492_I0; // @[Top.scala 1435:22]
  wire [15:0] n492_I1; // @[Top.scala 1435:22]
  wire [15:0] n492_O_t0b; // @[Top.scala 1435:22]
  wire [15:0] n492_O_t1b; // @[Top.scala 1435:22]
  wire  n493_valid_up; // @[Top.scala 1439:22]
  wire  n493_valid_down; // @[Top.scala 1439:22]
  wire [15:0] n493_I_t0b; // @[Top.scala 1439:22]
  wire [15:0] n493_I_t1b; // @[Top.scala 1439:22]
  wire [15:0] n493_O; // @[Top.scala 1439:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n496_valid_up; // @[Top.scala 1443:22]
  wire  n496_valid_down; // @[Top.scala 1443:22]
  wire [15:0] n496_I0; // @[Top.scala 1443:22]
  wire [15:0] n496_O_t0b; // @[Top.scala 1443:22]
  wire  n497_valid_up; // @[Top.scala 1447:22]
  wire  n497_valid_down; // @[Top.scala 1447:22]
  wire [15:0] n497_I_t0b; // @[Top.scala 1447:22]
  wire [15:0] n497_O; // @[Top.scala 1447:22]
  wire  n498_valid_up; // @[Top.scala 1450:22]
  wire  n498_valid_down; // @[Top.scala 1450:22]
  wire [15:0] n498_I0; // @[Top.scala 1450:22]
  wire [15:0] n498_I1; // @[Top.scala 1450:22]
  wire [15:0] n498_O_t0b; // @[Top.scala 1450:22]
  wire [15:0] n498_O_t1b; // @[Top.scala 1450:22]
  wire  n499_valid_up; // @[Top.scala 1454:22]
  wire  n499_valid_down; // @[Top.scala 1454:22]
  wire [15:0] n499_I_t0b; // @[Top.scala 1454:22]
  wire [15:0] n499_I_t1b; // @[Top.scala 1454:22]
  wire [15:0] n499_O; // @[Top.scala 1454:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n502_valid_up; // @[Top.scala 1458:22]
  wire  n502_valid_down; // @[Top.scala 1458:22]
  wire [15:0] n502_I0; // @[Top.scala 1458:22]
  wire [15:0] n502_I1; // @[Top.scala 1458:22]
  wire [15:0] n502_O_t0b; // @[Top.scala 1458:22]
  wire [15:0] n502_O_t1b; // @[Top.scala 1458:22]
  wire  n503_valid_up; // @[Top.scala 1462:22]
  wire  n503_valid_down; // @[Top.scala 1462:22]
  wire [15:0] n503_I_t0b; // @[Top.scala 1462:22]
  wire [15:0] n503_I_t1b; // @[Top.scala 1462:22]
  wire [15:0] n503_O; // @[Top.scala 1462:22]
  wire  n504_valid_up; // @[Top.scala 1465:22]
  wire  n504_valid_down; // @[Top.scala 1465:22]
  wire [15:0] n504_I0; // @[Top.scala 1465:22]
  wire [15:0] n504_I1; // @[Top.scala 1465:22]
  wire [15:0] n504_O_t0b; // @[Top.scala 1465:22]
  wire [15:0] n504_O_t1b; // @[Top.scala 1465:22]
  wire  n505_valid_up; // @[Top.scala 1469:22]
  wire  n505_valid_down; // @[Top.scala 1469:22]
  wire  n505_I0; // @[Top.scala 1469:22]
  wire [15:0] n505_I1_t0b; // @[Top.scala 1469:22]
  wire [15:0] n505_I1_t1b; // @[Top.scala 1469:22]
  wire  n505_O_t0b; // @[Top.scala 1469:22]
  wire [15:0] n505_O_t1b_t0b; // @[Top.scala 1469:22]
  wire [15:0] n505_O_t1b_t1b; // @[Top.scala 1469:22]
  wire  n506_valid_up; // @[Top.scala 1473:22]
  wire  n506_valid_down; // @[Top.scala 1473:22]
  wire  n506_I_t0b; // @[Top.scala 1473:22]
  wire [15:0] n506_I_t1b_t0b; // @[Top.scala 1473:22]
  wire [15:0] n506_I_t1b_t1b; // @[Top.scala 1473:22]
  wire [15:0] n506_O; // @[Top.scala 1473:22]
  wire  n507_valid_up; // @[Top.scala 1476:22]
  wire  n507_valid_down; // @[Top.scala 1476:22]
  wire [15:0] n507_I0; // @[Top.scala 1476:22]
  wire [15:0] n507_I1; // @[Top.scala 1476:22]
  wire [15:0] n507_O_t0b; // @[Top.scala 1476:22]
  wire [15:0] n507_O_t1b; // @[Top.scala 1476:22]
  wire  n508_clock; // @[Top.scala 1480:22]
  wire  n508_reset; // @[Top.scala 1480:22]
  wire  n508_valid_up; // @[Top.scala 1480:22]
  wire  n508_valid_down; // @[Top.scala 1480:22]
  wire [15:0] n508_I_t0b; // @[Top.scala 1480:22]
  wire [15:0] n508_I_t1b; // @[Top.scala 1480:22]
  wire [15:0] n508_O; // @[Top.scala 1480:22]
  wire  n510_valid_up; // @[Top.scala 1483:22]
  wire  n510_valid_down; // @[Top.scala 1483:22]
  wire [15:0] n510_I0; // @[Top.scala 1483:22]
  wire [15:0] n510_I1; // @[Top.scala 1483:22]
  wire [15:0] n510_O_t0b; // @[Top.scala 1483:22]
  wire [15:0] n510_O_t1b; // @[Top.scala 1483:22]
  wire  n511_valid_up; // @[Top.scala 1487:22]
  wire  n511_valid_down; // @[Top.scala 1487:22]
  wire [15:0] n511_I_t0b; // @[Top.scala 1487:22]
  wire [15:0] n511_I_t1b; // @[Top.scala 1487:22]
  wire [15:0] n511_O; // @[Top.scala 1487:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n513_valid_up; // @[Top.scala 1491:22]
  wire  n513_valid_down; // @[Top.scala 1491:22]
  wire [15:0] n513_I0; // @[Top.scala 1491:22]
  wire [15:0] n513_I1; // @[Top.scala 1491:22]
  wire [15:0] n513_O_t0b; // @[Top.scala 1491:22]
  wire [15:0] n513_O_t1b; // @[Top.scala 1491:22]
  wire  n514_valid_up; // @[Top.scala 1495:22]
  wire  n514_valid_down; // @[Top.scala 1495:22]
  wire [15:0] n514_I_t0b; // @[Top.scala 1495:22]
  wire [15:0] n514_I_t1b; // @[Top.scala 1495:22]
  wire [15:0] n514_O; // @[Top.scala 1495:22]
  wire  n515_valid_up; // @[Top.scala 1498:22]
  wire  n515_valid_down; // @[Top.scala 1498:22]
  wire [15:0] n515_I0; // @[Top.scala 1498:22]
  wire [15:0] n515_I1; // @[Top.scala 1498:22]
  wire [15:0] n515_O_t0b; // @[Top.scala 1498:22]
  wire [15:0] n515_O_t1b; // @[Top.scala 1498:22]
  wire  n516_valid_up; // @[Top.scala 1502:22]
  wire  n516_valid_down; // @[Top.scala 1502:22]
  wire [15:0] n516_I0; // @[Top.scala 1502:22]
  wire [15:0] n516_I1; // @[Top.scala 1502:22]
  wire [15:0] n516_O_t0b; // @[Top.scala 1502:22]
  wire [15:0] n516_O_t1b; // @[Top.scala 1502:22]
  wire  n517_valid_up; // @[Top.scala 1506:22]
  wire  n517_valid_down; // @[Top.scala 1506:22]
  wire [15:0] n517_I0_t0b; // @[Top.scala 1506:22]
  wire [15:0] n517_I0_t1b; // @[Top.scala 1506:22]
  wire [15:0] n517_I1_t0b; // @[Top.scala 1506:22]
  wire [15:0] n517_I1_t1b; // @[Top.scala 1506:22]
  wire [15:0] n517_O_t0b_t0b; // @[Top.scala 1506:22]
  wire [15:0] n517_O_t0b_t1b; // @[Top.scala 1506:22]
  wire [15:0] n517_O_t1b_t0b; // @[Top.scala 1506:22]
  wire [15:0] n517_O_t1b_t1b; // @[Top.scala 1506:22]
  wire  n518_clock; // @[Top.scala 1510:22]
  wire  n518_reset; // @[Top.scala 1510:22]
  wire  n518_valid_up; // @[Top.scala 1510:22]
  wire  n518_valid_down; // @[Top.scala 1510:22]
  wire [15:0] n518_I_t0b_t0b; // @[Top.scala 1510:22]
  wire [15:0] n518_I_t0b_t1b; // @[Top.scala 1510:22]
  wire [15:0] n518_I_t1b_t0b; // @[Top.scala 1510:22]
  wire [15:0] n518_I_t1b_t1b; // @[Top.scala 1510:22]
  wire [15:0] n518_O_t0b_t0b; // @[Top.scala 1510:22]
  wire [15:0] n518_O_t0b_t1b; // @[Top.scala 1510:22]
  wire [15:0] n518_O_t1b_t0b; // @[Top.scala 1510:22]
  wire [15:0] n518_O_t1b_t1b; // @[Top.scala 1510:22]
  wire  n519_valid_up; // @[Top.scala 1513:22]
  wire  n519_valid_down; // @[Top.scala 1513:22]
  wire  n519_I0; // @[Top.scala 1513:22]
  wire [15:0] n519_I1_t0b_t0b; // @[Top.scala 1513:22]
  wire [15:0] n519_I1_t0b_t1b; // @[Top.scala 1513:22]
  wire [15:0] n519_I1_t1b_t0b; // @[Top.scala 1513:22]
  wire [15:0] n519_I1_t1b_t1b; // @[Top.scala 1513:22]
  wire  n519_O_t0b; // @[Top.scala 1513:22]
  wire [15:0] n519_O_t1b_t0b_t0b; // @[Top.scala 1513:22]
  wire [15:0] n519_O_t1b_t0b_t1b; // @[Top.scala 1513:22]
  wire [15:0] n519_O_t1b_t1b_t0b; // @[Top.scala 1513:22]
  wire [15:0] n519_O_t1b_t1b_t1b; // @[Top.scala 1513:22]
  wire  n520_valid_up; // @[Top.scala 1517:22]
  wire  n520_valid_down; // @[Top.scala 1517:22]
  wire  n520_I_t0b; // @[Top.scala 1517:22]
  wire [15:0] n520_I_t1b_t0b_t0b; // @[Top.scala 1517:22]
  wire [15:0] n520_I_t1b_t0b_t1b; // @[Top.scala 1517:22]
  wire [15:0] n520_I_t1b_t1b_t0b; // @[Top.scala 1517:22]
  wire [15:0] n520_I_t1b_t1b_t1b; // @[Top.scala 1517:22]
  wire [15:0] n520_O_t0b; // @[Top.scala 1517:22]
  wire [15:0] n520_O_t1b; // @[Top.scala 1517:22]
  wire  n522_valid_up; // @[Top.scala 1520:22]
  wire  n522_valid_down; // @[Top.scala 1520:22]
  wire [15:0] n522_I0; // @[Top.scala 1520:22]
  wire [15:0] n522_I1_t0b; // @[Top.scala 1520:22]
  wire [15:0] n522_I1_t1b; // @[Top.scala 1520:22]
  wire [15:0] n522_O_t0b; // @[Top.scala 1520:22]
  wire [15:0] n522_O_t1b_t0b; // @[Top.scala 1520:22]
  wire [15:0] n522_O_t1b_t1b; // @[Top.scala 1520:22]
  Fst n488 ( // @[Top.scala 1417:22]
    .valid_up(n488_valid_up),
    .valid_down(n488_valid_down),
    .I_t0b(n488_I_t0b),
    .O(n488_O)
  );
  FIFO_1 n521 ( // @[Top.scala 1420:22]
    .clock(n521_clock),
    .reset(n521_reset),
    .valid_up(n521_valid_up),
    .valid_down(n521_valid_down),
    .I(n521_I),
    .O(n521_O)
  );
  FIFO_1 n509 ( // @[Top.scala 1423:22]
    .clock(n509_clock),
    .reset(n509_reset),
    .valid_up(n509_valid_up),
    .valid_down(n509_valid_down),
    .I(n509_I),
    .O(n509_O)
  );
  Snd n489 ( // @[Top.scala 1426:22]
    .valid_up(n489_valid_up),
    .valid_down(n489_valid_down),
    .I_t1b_t0b(n489_I_t1b_t0b),
    .I_t1b_t1b(n489_I_t1b_t1b),
    .O_t0b(n489_O_t0b),
    .O_t1b(n489_O_t1b)
  );
  Fst_1 n490 ( // @[Top.scala 1429:22]
    .valid_up(n490_valid_up),
    .valid_down(n490_valid_down),
    .I_t0b(n490_I_t0b),
    .O(n490_O)
  );
  Snd_1 n491 ( // @[Top.scala 1432:22]
    .valid_up(n491_valid_up),
    .valid_down(n491_valid_down),
    .I_t1b(n491_I_t1b),
    .O(n491_O)
  );
  AtomTuple_1 n492 ( // @[Top.scala 1435:22]
    .valid_up(n492_valid_up),
    .valid_down(n492_valid_down),
    .I0(n492_I0),
    .I1(n492_I1),
    .O_t0b(n492_O_t0b),
    .O_t1b(n492_O_t1b)
  );
  Add n493 ( // @[Top.scala 1439:22]
    .valid_up(n493_valid_up),
    .valid_down(n493_valid_down),
    .I_t0b(n493_I_t0b),
    .I_t1b(n493_I_t1b),
    .O(n493_O)
  );
  InitialDelayCounter_39 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n496 ( // @[Top.scala 1443:22]
    .valid_up(n496_valid_up),
    .valid_down(n496_valid_down),
    .I0(n496_I0),
    .O_t0b(n496_O_t0b)
  );
  RShift n497 ( // @[Top.scala 1447:22]
    .valid_up(n497_valid_up),
    .valid_down(n497_valid_down),
    .I_t0b(n497_I_t0b),
    .O(n497_O)
  );
  AtomTuple_1 n498 ( // @[Top.scala 1450:22]
    .valid_up(n498_valid_up),
    .valid_down(n498_valid_down),
    .I0(n498_I0),
    .I1(n498_I1),
    .O_t0b(n498_O_t0b),
    .O_t1b(n498_O_t1b)
  );
  Eq n499 ( // @[Top.scala 1454:22]
    .valid_up(n499_valid_up),
    .valid_down(n499_valid_down),
    .I_t0b(n499_I_t0b),
    .I_t1b(n499_I_t1b),
    .O(n499_O)
  );
  InitialDelayCounter_39 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n502 ( // @[Top.scala 1458:22]
    .valid_up(n502_valid_up),
    .valid_down(n502_valid_down),
    .I0(n502_I0),
    .I1(n502_I1),
    .O_t0b(n502_O_t0b),
    .O_t1b(n502_O_t1b)
  );
  Add n503 ( // @[Top.scala 1462:22]
    .valid_up(n503_valid_up),
    .valid_down(n503_valid_down),
    .I_t0b(n503_I_t0b),
    .I_t1b(n503_I_t1b),
    .O(n503_O)
  );
  AtomTuple_1 n504 ( // @[Top.scala 1465:22]
    .valid_up(n504_valid_up),
    .valid_down(n504_valid_down),
    .I0(n504_I0),
    .I1(n504_I1),
    .O_t0b(n504_O_t0b),
    .O_t1b(n504_O_t1b)
  );
  AtomTuple_9 n505 ( // @[Top.scala 1469:22]
    .valid_up(n505_valid_up),
    .valid_down(n505_valid_down),
    .I0(n505_I0),
    .I1_t0b(n505_I1_t0b),
    .I1_t1b(n505_I1_t1b),
    .O_t0b(n505_O_t0b),
    .O_t1b_t0b(n505_O_t1b_t0b),
    .O_t1b_t1b(n505_O_t1b_t1b)
  );
  If n506 ( // @[Top.scala 1473:22]
    .valid_up(n506_valid_up),
    .valid_down(n506_valid_down),
    .I_t0b(n506_I_t0b),
    .I_t1b_t0b(n506_I_t1b_t0b),
    .I_t1b_t1b(n506_I_t1b_t1b),
    .O(n506_O)
  );
  AtomTuple_1 n507 ( // @[Top.scala 1476:22]
    .valid_up(n507_valid_up),
    .valid_down(n507_valid_down),
    .I0(n507_I0),
    .I1(n507_I1),
    .O_t0b(n507_O_t0b),
    .O_t1b(n507_O_t1b)
  );
  Mul n508 ( // @[Top.scala 1480:22]
    .clock(n508_clock),
    .reset(n508_reset),
    .valid_up(n508_valid_up),
    .valid_down(n508_valid_down),
    .I_t0b(n508_I_t0b),
    .I_t1b(n508_I_t1b),
    .O(n508_O)
  );
  AtomTuple_1 n510 ( // @[Top.scala 1483:22]
    .valid_up(n510_valid_up),
    .valid_down(n510_valid_down),
    .I0(n510_I0),
    .I1(n510_I1),
    .O_t0b(n510_O_t0b),
    .O_t1b(n510_O_t1b)
  );
  Lt n511 ( // @[Top.scala 1487:22]
    .valid_up(n511_valid_up),
    .valid_down(n511_valid_down),
    .I_t0b(n511_I_t0b),
    .I_t1b(n511_I_t1b),
    .O(n511_O)
  );
  InitialDelayCounter_39 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n513 ( // @[Top.scala 1491:22]
    .valid_up(n513_valid_up),
    .valid_down(n513_valid_down),
    .I0(n513_I0),
    .I1(n513_I1),
    .O_t0b(n513_O_t0b),
    .O_t1b(n513_O_t1b)
  );
  Sub n514 ( // @[Top.scala 1495:22]
    .valid_up(n514_valid_up),
    .valid_down(n514_valid_down),
    .I_t0b(n514_I_t0b),
    .I_t1b(n514_I_t1b),
    .O(n514_O)
  );
  AtomTuple_1 n515 ( // @[Top.scala 1498:22]
    .valid_up(n515_valid_up),
    .valid_down(n515_valid_down),
    .I0(n515_I0),
    .I1(n515_I1),
    .O_t0b(n515_O_t0b),
    .O_t1b(n515_O_t1b)
  );
  AtomTuple_1 n516 ( // @[Top.scala 1502:22]
    .valid_up(n516_valid_up),
    .valid_down(n516_valid_down),
    .I0(n516_I0),
    .I1(n516_I1),
    .O_t0b(n516_O_t0b),
    .O_t1b(n516_O_t1b)
  );
  AtomTuple_15 n517 ( // @[Top.scala 1506:22]
    .valid_up(n517_valid_up),
    .valid_down(n517_valid_down),
    .I0_t0b(n517_I0_t0b),
    .I0_t1b(n517_I0_t1b),
    .I1_t0b(n517_I1_t0b),
    .I1_t1b(n517_I1_t1b),
    .O_t0b_t0b(n517_O_t0b_t0b),
    .O_t0b_t1b(n517_O_t0b_t1b),
    .O_t1b_t0b(n517_O_t1b_t0b),
    .O_t1b_t1b(n517_O_t1b_t1b)
  );
  FIFO_3 n518 ( // @[Top.scala 1510:22]
    .clock(n518_clock),
    .reset(n518_reset),
    .valid_up(n518_valid_up),
    .valid_down(n518_valid_down),
    .I_t0b_t0b(n518_I_t0b_t0b),
    .I_t0b_t1b(n518_I_t0b_t1b),
    .I_t1b_t0b(n518_I_t1b_t0b),
    .I_t1b_t1b(n518_I_t1b_t1b),
    .O_t0b_t0b(n518_O_t0b_t0b),
    .O_t0b_t1b(n518_O_t0b_t1b),
    .O_t1b_t0b(n518_O_t1b_t0b),
    .O_t1b_t1b(n518_O_t1b_t1b)
  );
  AtomTuple_16 n519 ( // @[Top.scala 1513:22]
    .valid_up(n519_valid_up),
    .valid_down(n519_valid_down),
    .I0(n519_I0),
    .I1_t0b_t0b(n519_I1_t0b_t0b),
    .I1_t0b_t1b(n519_I1_t0b_t1b),
    .I1_t1b_t0b(n519_I1_t1b_t0b),
    .I1_t1b_t1b(n519_I1_t1b_t1b),
    .O_t0b(n519_O_t0b),
    .O_t1b_t0b_t0b(n519_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n519_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n519_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n519_O_t1b_t1b_t1b)
  );
  If_1 n520 ( // @[Top.scala 1517:22]
    .valid_up(n520_valid_up),
    .valid_down(n520_valid_down),
    .I_t0b(n520_I_t0b),
    .I_t1b_t0b_t0b(n520_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n520_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n520_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n520_I_t1b_t1b_t1b),
    .O_t0b(n520_O_t0b),
    .O_t1b(n520_O_t1b)
  );
  AtomTuple_3 n522 ( // @[Top.scala 1520:22]
    .valid_up(n522_valid_up),
    .valid_down(n522_valid_down),
    .I0(n522_I0),
    .I1_t0b(n522_I1_t0b),
    .I1_t1b(n522_I1_t1b),
    .O_t0b(n522_O_t0b),
    .O_t1b_t0b(n522_O_t1b_t0b),
    .O_t1b_t1b(n522_O_t1b_t1b)
  );
  assign valid_down = n522_valid_down; // @[Top.scala 1525:16]
  assign O_t0b = n522_O_t0b; // @[Top.scala 1524:7]
  assign O_t1b_t0b = n522_O_t1b_t0b; // @[Top.scala 1524:7]
  assign O_t1b_t1b = n522_O_t1b_t1b; // @[Top.scala 1524:7]
  assign n488_valid_up = valid_up; // @[Top.scala 1419:19]
  assign n488_I_t0b = I_t0b; // @[Top.scala 1418:12]
  assign n521_clock = clock;
  assign n521_reset = reset;
  assign n521_valid_up = n488_valid_down; // @[Top.scala 1422:19]
  assign n521_I = n488_O; // @[Top.scala 1421:12]
  assign n509_clock = clock;
  assign n509_reset = reset;
  assign n509_valid_up = n488_valid_down; // @[Top.scala 1425:19]
  assign n509_I = n488_O; // @[Top.scala 1424:12]
  assign n489_valid_up = valid_up; // @[Top.scala 1428:19]
  assign n489_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1427:12]
  assign n489_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1427:12]
  assign n490_valid_up = n489_valid_down; // @[Top.scala 1431:19]
  assign n490_I_t0b = n489_O_t0b; // @[Top.scala 1430:12]
  assign n491_valid_up = n489_valid_down; // @[Top.scala 1434:19]
  assign n491_I_t1b = n489_O_t1b; // @[Top.scala 1433:12]
  assign n492_valid_up = n490_valid_down & n491_valid_down; // @[Top.scala 1438:19]
  assign n492_I0 = n490_O; // @[Top.scala 1436:13]
  assign n492_I1 = n491_O; // @[Top.scala 1437:13]
  assign n493_valid_up = n492_valid_down; // @[Top.scala 1441:19]
  assign n493_I_t0b = n492_O_t0b; // @[Top.scala 1440:12]
  assign n493_I_t1b = n492_O_t1b; // @[Top.scala 1440:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n496_valid_up = n493_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 1446:19]
  assign n496_I0 = n493_O; // @[Top.scala 1444:13]
  assign n497_valid_up = n496_valid_down; // @[Top.scala 1449:19]
  assign n497_I_t0b = n496_O_t0b; // @[Top.scala 1448:12]
  assign n498_valid_up = n497_valid_down & n490_valid_down; // @[Top.scala 1453:19]
  assign n498_I0 = n497_O; // @[Top.scala 1451:13]
  assign n498_I1 = n490_O; // @[Top.scala 1452:13]
  assign n499_valid_up = n498_valid_down; // @[Top.scala 1456:19]
  assign n499_I_t0b = n498_O_t0b; // @[Top.scala 1455:12]
  assign n499_I_t1b = n498_O_t1b; // @[Top.scala 1455:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n502_valid_up = n497_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1461:19]
  assign n502_I0 = n497_O; // @[Top.scala 1459:13]
  assign n502_I1 = 16'h1; // @[Top.scala 1460:13]
  assign n503_valid_up = n502_valid_down; // @[Top.scala 1464:19]
  assign n503_I_t0b = n502_O_t0b; // @[Top.scala 1463:12]
  assign n503_I_t1b = n502_O_t1b; // @[Top.scala 1463:12]
  assign n504_valid_up = n503_valid_down & n497_valid_down; // @[Top.scala 1468:19]
  assign n504_I0 = n503_O; // @[Top.scala 1466:13]
  assign n504_I1 = n497_O; // @[Top.scala 1467:13]
  assign n505_valid_up = n499_valid_down & n504_valid_down; // @[Top.scala 1472:19]
  assign n505_I0 = n499_O[0]; // @[Top.scala 1470:13]
  assign n505_I1_t0b = n504_O_t0b; // @[Top.scala 1471:13]
  assign n505_I1_t1b = n504_O_t1b; // @[Top.scala 1471:13]
  assign n506_valid_up = n505_valid_down; // @[Top.scala 1475:19]
  assign n506_I_t0b = n505_O_t0b; // @[Top.scala 1474:12]
  assign n506_I_t1b_t0b = n505_O_t1b_t0b; // @[Top.scala 1474:12]
  assign n506_I_t1b_t1b = n505_O_t1b_t1b; // @[Top.scala 1474:12]
  assign n507_valid_up = n506_valid_down; // @[Top.scala 1479:19]
  assign n507_I0 = n506_O; // @[Top.scala 1477:13]
  assign n507_I1 = n506_O; // @[Top.scala 1478:13]
  assign n508_clock = clock;
  assign n508_reset = reset;
  assign n508_valid_up = n507_valid_down; // @[Top.scala 1482:19]
  assign n508_I_t0b = n507_O_t0b; // @[Top.scala 1481:12]
  assign n508_I_t1b = n507_O_t1b; // @[Top.scala 1481:12]
  assign n510_valid_up = n509_valid_down & n508_valid_down; // @[Top.scala 1486:19]
  assign n510_I0 = n509_O; // @[Top.scala 1484:13]
  assign n510_I1 = n508_O; // @[Top.scala 1485:13]
  assign n511_valid_up = n510_valid_down; // @[Top.scala 1489:19]
  assign n511_I_t0b = n510_O_t0b; // @[Top.scala 1488:12]
  assign n511_I_t1b = n510_O_t1b; // @[Top.scala 1488:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n513_valid_up = n506_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1494:19]
  assign n513_I0 = n506_O; // @[Top.scala 1492:13]
  assign n513_I1 = 16'h1; // @[Top.scala 1493:13]
  assign n514_valid_up = n513_valid_down; // @[Top.scala 1497:19]
  assign n514_I_t0b = n513_O_t0b; // @[Top.scala 1496:12]
  assign n514_I_t1b = n513_O_t1b; // @[Top.scala 1496:12]
  assign n515_valid_up = n490_valid_down & n514_valid_down; // @[Top.scala 1501:19]
  assign n515_I0 = n490_O; // @[Top.scala 1499:13]
  assign n515_I1 = n514_O; // @[Top.scala 1500:13]
  assign n516_valid_up = n506_valid_down & n491_valid_down; // @[Top.scala 1505:19]
  assign n516_I0 = n506_O; // @[Top.scala 1503:13]
  assign n516_I1 = n491_O; // @[Top.scala 1504:13]
  assign n517_valid_up = n515_valid_down & n516_valid_down; // @[Top.scala 1509:19]
  assign n517_I0_t0b = n515_O_t0b; // @[Top.scala 1507:13]
  assign n517_I0_t1b = n515_O_t1b; // @[Top.scala 1507:13]
  assign n517_I1_t0b = n516_O_t0b; // @[Top.scala 1508:13]
  assign n517_I1_t1b = n516_O_t1b; // @[Top.scala 1508:13]
  assign n518_clock = clock;
  assign n518_reset = reset;
  assign n518_valid_up = n517_valid_down; // @[Top.scala 1512:19]
  assign n518_I_t0b_t0b = n517_O_t0b_t0b; // @[Top.scala 1511:12]
  assign n518_I_t0b_t1b = n517_O_t0b_t1b; // @[Top.scala 1511:12]
  assign n518_I_t1b_t0b = n517_O_t1b_t0b; // @[Top.scala 1511:12]
  assign n518_I_t1b_t1b = n517_O_t1b_t1b; // @[Top.scala 1511:12]
  assign n519_valid_up = n511_valid_down & n518_valid_down; // @[Top.scala 1516:19]
  assign n519_I0 = n511_O[0]; // @[Top.scala 1514:13]
  assign n519_I1_t0b_t0b = n518_O_t0b_t0b; // @[Top.scala 1515:13]
  assign n519_I1_t0b_t1b = n518_O_t0b_t1b; // @[Top.scala 1515:13]
  assign n519_I1_t1b_t0b = n518_O_t1b_t0b; // @[Top.scala 1515:13]
  assign n519_I1_t1b_t1b = n518_O_t1b_t1b; // @[Top.scala 1515:13]
  assign n520_valid_up = n519_valid_down; // @[Top.scala 1519:19]
  assign n520_I_t0b = n519_O_t0b; // @[Top.scala 1518:12]
  assign n520_I_t1b_t0b_t0b = n519_O_t1b_t0b_t0b; // @[Top.scala 1518:12]
  assign n520_I_t1b_t0b_t1b = n519_O_t1b_t0b_t1b; // @[Top.scala 1518:12]
  assign n520_I_t1b_t1b_t0b = n519_O_t1b_t1b_t0b; // @[Top.scala 1518:12]
  assign n520_I_t1b_t1b_t1b = n519_O_t1b_t1b_t1b; // @[Top.scala 1518:12]
  assign n522_valid_up = n521_valid_down & n520_valid_down; // @[Top.scala 1523:19]
  assign n522_I0 = n521_O; // @[Top.scala 1521:13]
  assign n522_I1_t0b = n520_O_t0b; // @[Top.scala 1522:13]
  assign n522_I1_t1b = n520_O_t1b; // @[Top.scala 1522:13]
endmodule
module MapS_13(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_13 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_13 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_13 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_13 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_13(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_13 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_42(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [5:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 6'h28; // @[InitialDelayCounter.scala 17:17]
  wire [5:0] _T_4 = value + 6'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 6'h28; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[5:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 6'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Module_14(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n527_valid_up; // @[Top.scala 1531:22]
  wire  n527_valid_down; // @[Top.scala 1531:22]
  wire [15:0] n527_I_t0b; // @[Top.scala 1531:22]
  wire [15:0] n527_O; // @[Top.scala 1531:22]
  wire  n560_clock; // @[Top.scala 1534:22]
  wire  n560_reset; // @[Top.scala 1534:22]
  wire  n560_valid_up; // @[Top.scala 1534:22]
  wire  n560_valid_down; // @[Top.scala 1534:22]
  wire [15:0] n560_I; // @[Top.scala 1534:22]
  wire [15:0] n560_O; // @[Top.scala 1534:22]
  wire  n548_clock; // @[Top.scala 1537:22]
  wire  n548_reset; // @[Top.scala 1537:22]
  wire  n548_valid_up; // @[Top.scala 1537:22]
  wire  n548_valid_down; // @[Top.scala 1537:22]
  wire [15:0] n548_I; // @[Top.scala 1537:22]
  wire [15:0] n548_O; // @[Top.scala 1537:22]
  wire  n528_valid_up; // @[Top.scala 1540:22]
  wire  n528_valid_down; // @[Top.scala 1540:22]
  wire [15:0] n528_I_t1b_t0b; // @[Top.scala 1540:22]
  wire [15:0] n528_I_t1b_t1b; // @[Top.scala 1540:22]
  wire [15:0] n528_O_t0b; // @[Top.scala 1540:22]
  wire [15:0] n528_O_t1b; // @[Top.scala 1540:22]
  wire  n529_valid_up; // @[Top.scala 1543:22]
  wire  n529_valid_down; // @[Top.scala 1543:22]
  wire [15:0] n529_I_t0b; // @[Top.scala 1543:22]
  wire [15:0] n529_O; // @[Top.scala 1543:22]
  wire  n530_valid_up; // @[Top.scala 1546:22]
  wire  n530_valid_down; // @[Top.scala 1546:22]
  wire [15:0] n530_I_t1b; // @[Top.scala 1546:22]
  wire [15:0] n530_O; // @[Top.scala 1546:22]
  wire  n531_valid_up; // @[Top.scala 1549:22]
  wire  n531_valid_down; // @[Top.scala 1549:22]
  wire [15:0] n531_I0; // @[Top.scala 1549:22]
  wire [15:0] n531_I1; // @[Top.scala 1549:22]
  wire [15:0] n531_O_t0b; // @[Top.scala 1549:22]
  wire [15:0] n531_O_t1b; // @[Top.scala 1549:22]
  wire  n532_valid_up; // @[Top.scala 1553:22]
  wire  n532_valid_down; // @[Top.scala 1553:22]
  wire [15:0] n532_I_t0b; // @[Top.scala 1553:22]
  wire [15:0] n532_I_t1b; // @[Top.scala 1553:22]
  wire [15:0] n532_O; // @[Top.scala 1553:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n535_valid_up; // @[Top.scala 1557:22]
  wire  n535_valid_down; // @[Top.scala 1557:22]
  wire [15:0] n535_I0; // @[Top.scala 1557:22]
  wire [15:0] n535_O_t0b; // @[Top.scala 1557:22]
  wire  n536_valid_up; // @[Top.scala 1561:22]
  wire  n536_valid_down; // @[Top.scala 1561:22]
  wire [15:0] n536_I_t0b; // @[Top.scala 1561:22]
  wire [15:0] n536_O; // @[Top.scala 1561:22]
  wire  n537_valid_up; // @[Top.scala 1564:22]
  wire  n537_valid_down; // @[Top.scala 1564:22]
  wire [15:0] n537_I0; // @[Top.scala 1564:22]
  wire [15:0] n537_I1; // @[Top.scala 1564:22]
  wire [15:0] n537_O_t0b; // @[Top.scala 1564:22]
  wire [15:0] n537_O_t1b; // @[Top.scala 1564:22]
  wire  n538_valid_up; // @[Top.scala 1568:22]
  wire  n538_valid_down; // @[Top.scala 1568:22]
  wire [15:0] n538_I_t0b; // @[Top.scala 1568:22]
  wire [15:0] n538_I_t1b; // @[Top.scala 1568:22]
  wire [15:0] n538_O; // @[Top.scala 1568:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n541_valid_up; // @[Top.scala 1572:22]
  wire  n541_valid_down; // @[Top.scala 1572:22]
  wire [15:0] n541_I0; // @[Top.scala 1572:22]
  wire [15:0] n541_I1; // @[Top.scala 1572:22]
  wire [15:0] n541_O_t0b; // @[Top.scala 1572:22]
  wire [15:0] n541_O_t1b; // @[Top.scala 1572:22]
  wire  n542_valid_up; // @[Top.scala 1576:22]
  wire  n542_valid_down; // @[Top.scala 1576:22]
  wire [15:0] n542_I_t0b; // @[Top.scala 1576:22]
  wire [15:0] n542_I_t1b; // @[Top.scala 1576:22]
  wire [15:0] n542_O; // @[Top.scala 1576:22]
  wire  n543_valid_up; // @[Top.scala 1579:22]
  wire  n543_valid_down; // @[Top.scala 1579:22]
  wire [15:0] n543_I0; // @[Top.scala 1579:22]
  wire [15:0] n543_I1; // @[Top.scala 1579:22]
  wire [15:0] n543_O_t0b; // @[Top.scala 1579:22]
  wire [15:0] n543_O_t1b; // @[Top.scala 1579:22]
  wire  n544_valid_up; // @[Top.scala 1583:22]
  wire  n544_valid_down; // @[Top.scala 1583:22]
  wire  n544_I0; // @[Top.scala 1583:22]
  wire [15:0] n544_I1_t0b; // @[Top.scala 1583:22]
  wire [15:0] n544_I1_t1b; // @[Top.scala 1583:22]
  wire  n544_O_t0b; // @[Top.scala 1583:22]
  wire [15:0] n544_O_t1b_t0b; // @[Top.scala 1583:22]
  wire [15:0] n544_O_t1b_t1b; // @[Top.scala 1583:22]
  wire  n545_valid_up; // @[Top.scala 1587:22]
  wire  n545_valid_down; // @[Top.scala 1587:22]
  wire  n545_I_t0b; // @[Top.scala 1587:22]
  wire [15:0] n545_I_t1b_t0b; // @[Top.scala 1587:22]
  wire [15:0] n545_I_t1b_t1b; // @[Top.scala 1587:22]
  wire [15:0] n545_O; // @[Top.scala 1587:22]
  wire  n546_valid_up; // @[Top.scala 1590:22]
  wire  n546_valid_down; // @[Top.scala 1590:22]
  wire [15:0] n546_I0; // @[Top.scala 1590:22]
  wire [15:0] n546_I1; // @[Top.scala 1590:22]
  wire [15:0] n546_O_t0b; // @[Top.scala 1590:22]
  wire [15:0] n546_O_t1b; // @[Top.scala 1590:22]
  wire  n547_clock; // @[Top.scala 1594:22]
  wire  n547_reset; // @[Top.scala 1594:22]
  wire  n547_valid_up; // @[Top.scala 1594:22]
  wire  n547_valid_down; // @[Top.scala 1594:22]
  wire [15:0] n547_I_t0b; // @[Top.scala 1594:22]
  wire [15:0] n547_I_t1b; // @[Top.scala 1594:22]
  wire [15:0] n547_O; // @[Top.scala 1594:22]
  wire  n549_valid_up; // @[Top.scala 1597:22]
  wire  n549_valid_down; // @[Top.scala 1597:22]
  wire [15:0] n549_I0; // @[Top.scala 1597:22]
  wire [15:0] n549_I1; // @[Top.scala 1597:22]
  wire [15:0] n549_O_t0b; // @[Top.scala 1597:22]
  wire [15:0] n549_O_t1b; // @[Top.scala 1597:22]
  wire  n550_valid_up; // @[Top.scala 1601:22]
  wire  n550_valid_down; // @[Top.scala 1601:22]
  wire [15:0] n550_I_t0b; // @[Top.scala 1601:22]
  wire [15:0] n550_I_t1b; // @[Top.scala 1601:22]
  wire [15:0] n550_O; // @[Top.scala 1601:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n552_valid_up; // @[Top.scala 1605:22]
  wire  n552_valid_down; // @[Top.scala 1605:22]
  wire [15:0] n552_I0; // @[Top.scala 1605:22]
  wire [15:0] n552_I1; // @[Top.scala 1605:22]
  wire [15:0] n552_O_t0b; // @[Top.scala 1605:22]
  wire [15:0] n552_O_t1b; // @[Top.scala 1605:22]
  wire  n553_valid_up; // @[Top.scala 1609:22]
  wire  n553_valid_down; // @[Top.scala 1609:22]
  wire [15:0] n553_I_t0b; // @[Top.scala 1609:22]
  wire [15:0] n553_I_t1b; // @[Top.scala 1609:22]
  wire [15:0] n553_O; // @[Top.scala 1609:22]
  wire  n554_valid_up; // @[Top.scala 1612:22]
  wire  n554_valid_down; // @[Top.scala 1612:22]
  wire [15:0] n554_I0; // @[Top.scala 1612:22]
  wire [15:0] n554_I1; // @[Top.scala 1612:22]
  wire [15:0] n554_O_t0b; // @[Top.scala 1612:22]
  wire [15:0] n554_O_t1b; // @[Top.scala 1612:22]
  wire  n555_valid_up; // @[Top.scala 1616:22]
  wire  n555_valid_down; // @[Top.scala 1616:22]
  wire [15:0] n555_I0; // @[Top.scala 1616:22]
  wire [15:0] n555_I1; // @[Top.scala 1616:22]
  wire [15:0] n555_O_t0b; // @[Top.scala 1616:22]
  wire [15:0] n555_O_t1b; // @[Top.scala 1616:22]
  wire  n556_valid_up; // @[Top.scala 1620:22]
  wire  n556_valid_down; // @[Top.scala 1620:22]
  wire [15:0] n556_I0_t0b; // @[Top.scala 1620:22]
  wire [15:0] n556_I0_t1b; // @[Top.scala 1620:22]
  wire [15:0] n556_I1_t0b; // @[Top.scala 1620:22]
  wire [15:0] n556_I1_t1b; // @[Top.scala 1620:22]
  wire [15:0] n556_O_t0b_t0b; // @[Top.scala 1620:22]
  wire [15:0] n556_O_t0b_t1b; // @[Top.scala 1620:22]
  wire [15:0] n556_O_t1b_t0b; // @[Top.scala 1620:22]
  wire [15:0] n556_O_t1b_t1b; // @[Top.scala 1620:22]
  wire  n557_clock; // @[Top.scala 1624:22]
  wire  n557_reset; // @[Top.scala 1624:22]
  wire  n557_valid_up; // @[Top.scala 1624:22]
  wire  n557_valid_down; // @[Top.scala 1624:22]
  wire [15:0] n557_I_t0b_t0b; // @[Top.scala 1624:22]
  wire [15:0] n557_I_t0b_t1b; // @[Top.scala 1624:22]
  wire [15:0] n557_I_t1b_t0b; // @[Top.scala 1624:22]
  wire [15:0] n557_I_t1b_t1b; // @[Top.scala 1624:22]
  wire [15:0] n557_O_t0b_t0b; // @[Top.scala 1624:22]
  wire [15:0] n557_O_t0b_t1b; // @[Top.scala 1624:22]
  wire [15:0] n557_O_t1b_t0b; // @[Top.scala 1624:22]
  wire [15:0] n557_O_t1b_t1b; // @[Top.scala 1624:22]
  wire  n558_valid_up; // @[Top.scala 1627:22]
  wire  n558_valid_down; // @[Top.scala 1627:22]
  wire  n558_I0; // @[Top.scala 1627:22]
  wire [15:0] n558_I1_t0b_t0b; // @[Top.scala 1627:22]
  wire [15:0] n558_I1_t0b_t1b; // @[Top.scala 1627:22]
  wire [15:0] n558_I1_t1b_t0b; // @[Top.scala 1627:22]
  wire [15:0] n558_I1_t1b_t1b; // @[Top.scala 1627:22]
  wire  n558_O_t0b; // @[Top.scala 1627:22]
  wire [15:0] n558_O_t1b_t0b_t0b; // @[Top.scala 1627:22]
  wire [15:0] n558_O_t1b_t0b_t1b; // @[Top.scala 1627:22]
  wire [15:0] n558_O_t1b_t1b_t0b; // @[Top.scala 1627:22]
  wire [15:0] n558_O_t1b_t1b_t1b; // @[Top.scala 1627:22]
  wire  n559_valid_up; // @[Top.scala 1631:22]
  wire  n559_valid_down; // @[Top.scala 1631:22]
  wire  n559_I_t0b; // @[Top.scala 1631:22]
  wire [15:0] n559_I_t1b_t0b_t0b; // @[Top.scala 1631:22]
  wire [15:0] n559_I_t1b_t0b_t1b; // @[Top.scala 1631:22]
  wire [15:0] n559_I_t1b_t1b_t0b; // @[Top.scala 1631:22]
  wire [15:0] n559_I_t1b_t1b_t1b; // @[Top.scala 1631:22]
  wire [15:0] n559_O_t0b; // @[Top.scala 1631:22]
  wire [15:0] n559_O_t1b; // @[Top.scala 1631:22]
  wire  n561_valid_up; // @[Top.scala 1634:22]
  wire  n561_valid_down; // @[Top.scala 1634:22]
  wire [15:0] n561_I0; // @[Top.scala 1634:22]
  wire [15:0] n561_I1_t0b; // @[Top.scala 1634:22]
  wire [15:0] n561_I1_t1b; // @[Top.scala 1634:22]
  wire [15:0] n561_O_t0b; // @[Top.scala 1634:22]
  wire [15:0] n561_O_t1b_t0b; // @[Top.scala 1634:22]
  wire [15:0] n561_O_t1b_t1b; // @[Top.scala 1634:22]
  Fst n527 ( // @[Top.scala 1531:22]
    .valid_up(n527_valid_up),
    .valid_down(n527_valid_down),
    .I_t0b(n527_I_t0b),
    .O(n527_O)
  );
  FIFO_1 n560 ( // @[Top.scala 1534:22]
    .clock(n560_clock),
    .reset(n560_reset),
    .valid_up(n560_valid_up),
    .valid_down(n560_valid_down),
    .I(n560_I),
    .O(n560_O)
  );
  FIFO_1 n548 ( // @[Top.scala 1537:22]
    .clock(n548_clock),
    .reset(n548_reset),
    .valid_up(n548_valid_up),
    .valid_down(n548_valid_down),
    .I(n548_I),
    .O(n548_O)
  );
  Snd n528 ( // @[Top.scala 1540:22]
    .valid_up(n528_valid_up),
    .valid_down(n528_valid_down),
    .I_t1b_t0b(n528_I_t1b_t0b),
    .I_t1b_t1b(n528_I_t1b_t1b),
    .O_t0b(n528_O_t0b),
    .O_t1b(n528_O_t1b)
  );
  Fst_1 n529 ( // @[Top.scala 1543:22]
    .valid_up(n529_valid_up),
    .valid_down(n529_valid_down),
    .I_t0b(n529_I_t0b),
    .O(n529_O)
  );
  Snd_1 n530 ( // @[Top.scala 1546:22]
    .valid_up(n530_valid_up),
    .valid_down(n530_valid_down),
    .I_t1b(n530_I_t1b),
    .O(n530_O)
  );
  AtomTuple_1 n531 ( // @[Top.scala 1549:22]
    .valid_up(n531_valid_up),
    .valid_down(n531_valid_down),
    .I0(n531_I0),
    .I1(n531_I1),
    .O_t0b(n531_O_t0b),
    .O_t1b(n531_O_t1b)
  );
  Add n532 ( // @[Top.scala 1553:22]
    .valid_up(n532_valid_up),
    .valid_down(n532_valid_down),
    .I_t0b(n532_I_t0b),
    .I_t1b(n532_I_t1b),
    .O(n532_O)
  );
  InitialDelayCounter_42 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n535 ( // @[Top.scala 1557:22]
    .valid_up(n535_valid_up),
    .valid_down(n535_valid_down),
    .I0(n535_I0),
    .O_t0b(n535_O_t0b)
  );
  RShift n536 ( // @[Top.scala 1561:22]
    .valid_up(n536_valid_up),
    .valid_down(n536_valid_down),
    .I_t0b(n536_I_t0b),
    .O(n536_O)
  );
  AtomTuple_1 n537 ( // @[Top.scala 1564:22]
    .valid_up(n537_valid_up),
    .valid_down(n537_valid_down),
    .I0(n537_I0),
    .I1(n537_I1),
    .O_t0b(n537_O_t0b),
    .O_t1b(n537_O_t1b)
  );
  Eq n538 ( // @[Top.scala 1568:22]
    .valid_up(n538_valid_up),
    .valid_down(n538_valid_down),
    .I_t0b(n538_I_t0b),
    .I_t1b(n538_I_t1b),
    .O(n538_O)
  );
  InitialDelayCounter_42 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n541 ( // @[Top.scala 1572:22]
    .valid_up(n541_valid_up),
    .valid_down(n541_valid_down),
    .I0(n541_I0),
    .I1(n541_I1),
    .O_t0b(n541_O_t0b),
    .O_t1b(n541_O_t1b)
  );
  Add n542 ( // @[Top.scala 1576:22]
    .valid_up(n542_valid_up),
    .valid_down(n542_valid_down),
    .I_t0b(n542_I_t0b),
    .I_t1b(n542_I_t1b),
    .O(n542_O)
  );
  AtomTuple_1 n543 ( // @[Top.scala 1579:22]
    .valid_up(n543_valid_up),
    .valid_down(n543_valid_down),
    .I0(n543_I0),
    .I1(n543_I1),
    .O_t0b(n543_O_t0b),
    .O_t1b(n543_O_t1b)
  );
  AtomTuple_9 n544 ( // @[Top.scala 1583:22]
    .valid_up(n544_valid_up),
    .valid_down(n544_valid_down),
    .I0(n544_I0),
    .I1_t0b(n544_I1_t0b),
    .I1_t1b(n544_I1_t1b),
    .O_t0b(n544_O_t0b),
    .O_t1b_t0b(n544_O_t1b_t0b),
    .O_t1b_t1b(n544_O_t1b_t1b)
  );
  If n545 ( // @[Top.scala 1587:22]
    .valid_up(n545_valid_up),
    .valid_down(n545_valid_down),
    .I_t0b(n545_I_t0b),
    .I_t1b_t0b(n545_I_t1b_t0b),
    .I_t1b_t1b(n545_I_t1b_t1b),
    .O(n545_O)
  );
  AtomTuple_1 n546 ( // @[Top.scala 1590:22]
    .valid_up(n546_valid_up),
    .valid_down(n546_valid_down),
    .I0(n546_I0),
    .I1(n546_I1),
    .O_t0b(n546_O_t0b),
    .O_t1b(n546_O_t1b)
  );
  Mul n547 ( // @[Top.scala 1594:22]
    .clock(n547_clock),
    .reset(n547_reset),
    .valid_up(n547_valid_up),
    .valid_down(n547_valid_down),
    .I_t0b(n547_I_t0b),
    .I_t1b(n547_I_t1b),
    .O(n547_O)
  );
  AtomTuple_1 n549 ( // @[Top.scala 1597:22]
    .valid_up(n549_valid_up),
    .valid_down(n549_valid_down),
    .I0(n549_I0),
    .I1(n549_I1),
    .O_t0b(n549_O_t0b),
    .O_t1b(n549_O_t1b)
  );
  Lt n550 ( // @[Top.scala 1601:22]
    .valid_up(n550_valid_up),
    .valid_down(n550_valid_down),
    .I_t0b(n550_I_t0b),
    .I_t1b(n550_I_t1b),
    .O(n550_O)
  );
  InitialDelayCounter_42 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n552 ( // @[Top.scala 1605:22]
    .valid_up(n552_valid_up),
    .valid_down(n552_valid_down),
    .I0(n552_I0),
    .I1(n552_I1),
    .O_t0b(n552_O_t0b),
    .O_t1b(n552_O_t1b)
  );
  Sub n553 ( // @[Top.scala 1609:22]
    .valid_up(n553_valid_up),
    .valid_down(n553_valid_down),
    .I_t0b(n553_I_t0b),
    .I_t1b(n553_I_t1b),
    .O(n553_O)
  );
  AtomTuple_1 n554 ( // @[Top.scala 1612:22]
    .valid_up(n554_valid_up),
    .valid_down(n554_valid_down),
    .I0(n554_I0),
    .I1(n554_I1),
    .O_t0b(n554_O_t0b),
    .O_t1b(n554_O_t1b)
  );
  AtomTuple_1 n555 ( // @[Top.scala 1616:22]
    .valid_up(n555_valid_up),
    .valid_down(n555_valid_down),
    .I0(n555_I0),
    .I1(n555_I1),
    .O_t0b(n555_O_t0b),
    .O_t1b(n555_O_t1b)
  );
  AtomTuple_15 n556 ( // @[Top.scala 1620:22]
    .valid_up(n556_valid_up),
    .valid_down(n556_valid_down),
    .I0_t0b(n556_I0_t0b),
    .I0_t1b(n556_I0_t1b),
    .I1_t0b(n556_I1_t0b),
    .I1_t1b(n556_I1_t1b),
    .O_t0b_t0b(n556_O_t0b_t0b),
    .O_t0b_t1b(n556_O_t0b_t1b),
    .O_t1b_t0b(n556_O_t1b_t0b),
    .O_t1b_t1b(n556_O_t1b_t1b)
  );
  FIFO_3 n557 ( // @[Top.scala 1624:22]
    .clock(n557_clock),
    .reset(n557_reset),
    .valid_up(n557_valid_up),
    .valid_down(n557_valid_down),
    .I_t0b_t0b(n557_I_t0b_t0b),
    .I_t0b_t1b(n557_I_t0b_t1b),
    .I_t1b_t0b(n557_I_t1b_t0b),
    .I_t1b_t1b(n557_I_t1b_t1b),
    .O_t0b_t0b(n557_O_t0b_t0b),
    .O_t0b_t1b(n557_O_t0b_t1b),
    .O_t1b_t0b(n557_O_t1b_t0b),
    .O_t1b_t1b(n557_O_t1b_t1b)
  );
  AtomTuple_16 n558 ( // @[Top.scala 1627:22]
    .valid_up(n558_valid_up),
    .valid_down(n558_valid_down),
    .I0(n558_I0),
    .I1_t0b_t0b(n558_I1_t0b_t0b),
    .I1_t0b_t1b(n558_I1_t0b_t1b),
    .I1_t1b_t0b(n558_I1_t1b_t0b),
    .I1_t1b_t1b(n558_I1_t1b_t1b),
    .O_t0b(n558_O_t0b),
    .O_t1b_t0b_t0b(n558_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n558_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n558_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n558_O_t1b_t1b_t1b)
  );
  If_1 n559 ( // @[Top.scala 1631:22]
    .valid_up(n559_valid_up),
    .valid_down(n559_valid_down),
    .I_t0b(n559_I_t0b),
    .I_t1b_t0b_t0b(n559_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n559_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n559_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n559_I_t1b_t1b_t1b),
    .O_t0b(n559_O_t0b),
    .O_t1b(n559_O_t1b)
  );
  AtomTuple_3 n561 ( // @[Top.scala 1634:22]
    .valid_up(n561_valid_up),
    .valid_down(n561_valid_down),
    .I0(n561_I0),
    .I1_t0b(n561_I1_t0b),
    .I1_t1b(n561_I1_t1b),
    .O_t0b(n561_O_t0b),
    .O_t1b_t0b(n561_O_t1b_t0b),
    .O_t1b_t1b(n561_O_t1b_t1b)
  );
  assign valid_down = n561_valid_down; // @[Top.scala 1639:16]
  assign O_t0b = n561_O_t0b; // @[Top.scala 1638:7]
  assign O_t1b_t0b = n561_O_t1b_t0b; // @[Top.scala 1638:7]
  assign O_t1b_t1b = n561_O_t1b_t1b; // @[Top.scala 1638:7]
  assign n527_valid_up = valid_up; // @[Top.scala 1533:19]
  assign n527_I_t0b = I_t0b; // @[Top.scala 1532:12]
  assign n560_clock = clock;
  assign n560_reset = reset;
  assign n560_valid_up = n527_valid_down; // @[Top.scala 1536:19]
  assign n560_I = n527_O; // @[Top.scala 1535:12]
  assign n548_clock = clock;
  assign n548_reset = reset;
  assign n548_valid_up = n527_valid_down; // @[Top.scala 1539:19]
  assign n548_I = n527_O; // @[Top.scala 1538:12]
  assign n528_valid_up = valid_up; // @[Top.scala 1542:19]
  assign n528_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1541:12]
  assign n528_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1541:12]
  assign n529_valid_up = n528_valid_down; // @[Top.scala 1545:19]
  assign n529_I_t0b = n528_O_t0b; // @[Top.scala 1544:12]
  assign n530_valid_up = n528_valid_down; // @[Top.scala 1548:19]
  assign n530_I_t1b = n528_O_t1b; // @[Top.scala 1547:12]
  assign n531_valid_up = n529_valid_down & n530_valid_down; // @[Top.scala 1552:19]
  assign n531_I0 = n529_O; // @[Top.scala 1550:13]
  assign n531_I1 = n530_O; // @[Top.scala 1551:13]
  assign n532_valid_up = n531_valid_down; // @[Top.scala 1555:19]
  assign n532_I_t0b = n531_O_t0b; // @[Top.scala 1554:12]
  assign n532_I_t1b = n531_O_t1b; // @[Top.scala 1554:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n535_valid_up = n532_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 1560:19]
  assign n535_I0 = n532_O; // @[Top.scala 1558:13]
  assign n536_valid_up = n535_valid_down; // @[Top.scala 1563:19]
  assign n536_I_t0b = n535_O_t0b; // @[Top.scala 1562:12]
  assign n537_valid_up = n536_valid_down & n529_valid_down; // @[Top.scala 1567:19]
  assign n537_I0 = n536_O; // @[Top.scala 1565:13]
  assign n537_I1 = n529_O; // @[Top.scala 1566:13]
  assign n538_valid_up = n537_valid_down; // @[Top.scala 1570:19]
  assign n538_I_t0b = n537_O_t0b; // @[Top.scala 1569:12]
  assign n538_I_t1b = n537_O_t1b; // @[Top.scala 1569:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n541_valid_up = n536_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1575:19]
  assign n541_I0 = n536_O; // @[Top.scala 1573:13]
  assign n541_I1 = 16'h1; // @[Top.scala 1574:13]
  assign n542_valid_up = n541_valid_down; // @[Top.scala 1578:19]
  assign n542_I_t0b = n541_O_t0b; // @[Top.scala 1577:12]
  assign n542_I_t1b = n541_O_t1b; // @[Top.scala 1577:12]
  assign n543_valid_up = n542_valid_down & n536_valid_down; // @[Top.scala 1582:19]
  assign n543_I0 = n542_O; // @[Top.scala 1580:13]
  assign n543_I1 = n536_O; // @[Top.scala 1581:13]
  assign n544_valid_up = n538_valid_down & n543_valid_down; // @[Top.scala 1586:19]
  assign n544_I0 = n538_O[0]; // @[Top.scala 1584:13]
  assign n544_I1_t0b = n543_O_t0b; // @[Top.scala 1585:13]
  assign n544_I1_t1b = n543_O_t1b; // @[Top.scala 1585:13]
  assign n545_valid_up = n544_valid_down; // @[Top.scala 1589:19]
  assign n545_I_t0b = n544_O_t0b; // @[Top.scala 1588:12]
  assign n545_I_t1b_t0b = n544_O_t1b_t0b; // @[Top.scala 1588:12]
  assign n545_I_t1b_t1b = n544_O_t1b_t1b; // @[Top.scala 1588:12]
  assign n546_valid_up = n545_valid_down; // @[Top.scala 1593:19]
  assign n546_I0 = n545_O; // @[Top.scala 1591:13]
  assign n546_I1 = n545_O; // @[Top.scala 1592:13]
  assign n547_clock = clock;
  assign n547_reset = reset;
  assign n547_valid_up = n546_valid_down; // @[Top.scala 1596:19]
  assign n547_I_t0b = n546_O_t0b; // @[Top.scala 1595:12]
  assign n547_I_t1b = n546_O_t1b; // @[Top.scala 1595:12]
  assign n549_valid_up = n548_valid_down & n547_valid_down; // @[Top.scala 1600:19]
  assign n549_I0 = n548_O; // @[Top.scala 1598:13]
  assign n549_I1 = n547_O; // @[Top.scala 1599:13]
  assign n550_valid_up = n549_valid_down; // @[Top.scala 1603:19]
  assign n550_I_t0b = n549_O_t0b; // @[Top.scala 1602:12]
  assign n550_I_t1b = n549_O_t1b; // @[Top.scala 1602:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n552_valid_up = n545_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1608:19]
  assign n552_I0 = n545_O; // @[Top.scala 1606:13]
  assign n552_I1 = 16'h1; // @[Top.scala 1607:13]
  assign n553_valid_up = n552_valid_down; // @[Top.scala 1611:19]
  assign n553_I_t0b = n552_O_t0b; // @[Top.scala 1610:12]
  assign n553_I_t1b = n552_O_t1b; // @[Top.scala 1610:12]
  assign n554_valid_up = n529_valid_down & n553_valid_down; // @[Top.scala 1615:19]
  assign n554_I0 = n529_O; // @[Top.scala 1613:13]
  assign n554_I1 = n553_O; // @[Top.scala 1614:13]
  assign n555_valid_up = n545_valid_down & n530_valid_down; // @[Top.scala 1619:19]
  assign n555_I0 = n545_O; // @[Top.scala 1617:13]
  assign n555_I1 = n530_O; // @[Top.scala 1618:13]
  assign n556_valid_up = n554_valid_down & n555_valid_down; // @[Top.scala 1623:19]
  assign n556_I0_t0b = n554_O_t0b; // @[Top.scala 1621:13]
  assign n556_I0_t1b = n554_O_t1b; // @[Top.scala 1621:13]
  assign n556_I1_t0b = n555_O_t0b; // @[Top.scala 1622:13]
  assign n556_I1_t1b = n555_O_t1b; // @[Top.scala 1622:13]
  assign n557_clock = clock;
  assign n557_reset = reset;
  assign n557_valid_up = n556_valid_down; // @[Top.scala 1626:19]
  assign n557_I_t0b_t0b = n556_O_t0b_t0b; // @[Top.scala 1625:12]
  assign n557_I_t0b_t1b = n556_O_t0b_t1b; // @[Top.scala 1625:12]
  assign n557_I_t1b_t0b = n556_O_t1b_t0b; // @[Top.scala 1625:12]
  assign n557_I_t1b_t1b = n556_O_t1b_t1b; // @[Top.scala 1625:12]
  assign n558_valid_up = n550_valid_down & n557_valid_down; // @[Top.scala 1630:19]
  assign n558_I0 = n550_O[0]; // @[Top.scala 1628:13]
  assign n558_I1_t0b_t0b = n557_O_t0b_t0b; // @[Top.scala 1629:13]
  assign n558_I1_t0b_t1b = n557_O_t0b_t1b; // @[Top.scala 1629:13]
  assign n558_I1_t1b_t0b = n557_O_t1b_t0b; // @[Top.scala 1629:13]
  assign n558_I1_t1b_t1b = n557_O_t1b_t1b; // @[Top.scala 1629:13]
  assign n559_valid_up = n558_valid_down; // @[Top.scala 1633:19]
  assign n559_I_t0b = n558_O_t0b; // @[Top.scala 1632:12]
  assign n559_I_t1b_t0b_t0b = n558_O_t1b_t0b_t0b; // @[Top.scala 1632:12]
  assign n559_I_t1b_t0b_t1b = n558_O_t1b_t0b_t1b; // @[Top.scala 1632:12]
  assign n559_I_t1b_t1b_t0b = n558_O_t1b_t1b_t0b; // @[Top.scala 1632:12]
  assign n559_I_t1b_t1b_t1b = n558_O_t1b_t1b_t1b; // @[Top.scala 1632:12]
  assign n561_valid_up = n560_valid_down & n559_valid_down; // @[Top.scala 1637:19]
  assign n561_I0 = n560_O; // @[Top.scala 1635:13]
  assign n561_I1_t0b = n559_O_t0b; // @[Top.scala 1636:13]
  assign n561_I1_t1b = n559_O_t1b; // @[Top.scala 1636:13]
endmodule
module MapS_14(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_14 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_14 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_14 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_14 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_14(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_14 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_45(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [5:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 6'h2b; // @[InitialDelayCounter.scala 17:17]
  wire [5:0] _T_4 = value + 6'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 6'h2b; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[5:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 6'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Module_15(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n566_valid_up; // @[Top.scala 1645:22]
  wire  n566_valid_down; // @[Top.scala 1645:22]
  wire [15:0] n566_I_t0b; // @[Top.scala 1645:22]
  wire [15:0] n566_O; // @[Top.scala 1645:22]
  wire  n599_clock; // @[Top.scala 1648:22]
  wire  n599_reset; // @[Top.scala 1648:22]
  wire  n599_valid_up; // @[Top.scala 1648:22]
  wire  n599_valid_down; // @[Top.scala 1648:22]
  wire [15:0] n599_I; // @[Top.scala 1648:22]
  wire [15:0] n599_O; // @[Top.scala 1648:22]
  wire  n587_clock; // @[Top.scala 1651:22]
  wire  n587_reset; // @[Top.scala 1651:22]
  wire  n587_valid_up; // @[Top.scala 1651:22]
  wire  n587_valid_down; // @[Top.scala 1651:22]
  wire [15:0] n587_I; // @[Top.scala 1651:22]
  wire [15:0] n587_O; // @[Top.scala 1651:22]
  wire  n567_valid_up; // @[Top.scala 1654:22]
  wire  n567_valid_down; // @[Top.scala 1654:22]
  wire [15:0] n567_I_t1b_t0b; // @[Top.scala 1654:22]
  wire [15:0] n567_I_t1b_t1b; // @[Top.scala 1654:22]
  wire [15:0] n567_O_t0b; // @[Top.scala 1654:22]
  wire [15:0] n567_O_t1b; // @[Top.scala 1654:22]
  wire  n568_valid_up; // @[Top.scala 1657:22]
  wire  n568_valid_down; // @[Top.scala 1657:22]
  wire [15:0] n568_I_t0b; // @[Top.scala 1657:22]
  wire [15:0] n568_O; // @[Top.scala 1657:22]
  wire  n569_valid_up; // @[Top.scala 1660:22]
  wire  n569_valid_down; // @[Top.scala 1660:22]
  wire [15:0] n569_I_t1b; // @[Top.scala 1660:22]
  wire [15:0] n569_O; // @[Top.scala 1660:22]
  wire  n570_valid_up; // @[Top.scala 1663:22]
  wire  n570_valid_down; // @[Top.scala 1663:22]
  wire [15:0] n570_I0; // @[Top.scala 1663:22]
  wire [15:0] n570_I1; // @[Top.scala 1663:22]
  wire [15:0] n570_O_t0b; // @[Top.scala 1663:22]
  wire [15:0] n570_O_t1b; // @[Top.scala 1663:22]
  wire  n571_valid_up; // @[Top.scala 1667:22]
  wire  n571_valid_down; // @[Top.scala 1667:22]
  wire [15:0] n571_I_t0b; // @[Top.scala 1667:22]
  wire [15:0] n571_I_t1b; // @[Top.scala 1667:22]
  wire [15:0] n571_O; // @[Top.scala 1667:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n574_valid_up; // @[Top.scala 1671:22]
  wire  n574_valid_down; // @[Top.scala 1671:22]
  wire [15:0] n574_I0; // @[Top.scala 1671:22]
  wire [15:0] n574_O_t0b; // @[Top.scala 1671:22]
  wire  n575_valid_up; // @[Top.scala 1675:22]
  wire  n575_valid_down; // @[Top.scala 1675:22]
  wire [15:0] n575_I_t0b; // @[Top.scala 1675:22]
  wire [15:0] n575_O; // @[Top.scala 1675:22]
  wire  n576_valid_up; // @[Top.scala 1678:22]
  wire  n576_valid_down; // @[Top.scala 1678:22]
  wire [15:0] n576_I0; // @[Top.scala 1678:22]
  wire [15:0] n576_I1; // @[Top.scala 1678:22]
  wire [15:0] n576_O_t0b; // @[Top.scala 1678:22]
  wire [15:0] n576_O_t1b; // @[Top.scala 1678:22]
  wire  n577_valid_up; // @[Top.scala 1682:22]
  wire  n577_valid_down; // @[Top.scala 1682:22]
  wire [15:0] n577_I_t0b; // @[Top.scala 1682:22]
  wire [15:0] n577_I_t1b; // @[Top.scala 1682:22]
  wire [15:0] n577_O; // @[Top.scala 1682:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n580_valid_up; // @[Top.scala 1686:22]
  wire  n580_valid_down; // @[Top.scala 1686:22]
  wire [15:0] n580_I0; // @[Top.scala 1686:22]
  wire [15:0] n580_I1; // @[Top.scala 1686:22]
  wire [15:0] n580_O_t0b; // @[Top.scala 1686:22]
  wire [15:0] n580_O_t1b; // @[Top.scala 1686:22]
  wire  n581_valid_up; // @[Top.scala 1690:22]
  wire  n581_valid_down; // @[Top.scala 1690:22]
  wire [15:0] n581_I_t0b; // @[Top.scala 1690:22]
  wire [15:0] n581_I_t1b; // @[Top.scala 1690:22]
  wire [15:0] n581_O; // @[Top.scala 1690:22]
  wire  n582_valid_up; // @[Top.scala 1693:22]
  wire  n582_valid_down; // @[Top.scala 1693:22]
  wire [15:0] n582_I0; // @[Top.scala 1693:22]
  wire [15:0] n582_I1; // @[Top.scala 1693:22]
  wire [15:0] n582_O_t0b; // @[Top.scala 1693:22]
  wire [15:0] n582_O_t1b; // @[Top.scala 1693:22]
  wire  n583_valid_up; // @[Top.scala 1697:22]
  wire  n583_valid_down; // @[Top.scala 1697:22]
  wire  n583_I0; // @[Top.scala 1697:22]
  wire [15:0] n583_I1_t0b; // @[Top.scala 1697:22]
  wire [15:0] n583_I1_t1b; // @[Top.scala 1697:22]
  wire  n583_O_t0b; // @[Top.scala 1697:22]
  wire [15:0] n583_O_t1b_t0b; // @[Top.scala 1697:22]
  wire [15:0] n583_O_t1b_t1b; // @[Top.scala 1697:22]
  wire  n584_valid_up; // @[Top.scala 1701:22]
  wire  n584_valid_down; // @[Top.scala 1701:22]
  wire  n584_I_t0b; // @[Top.scala 1701:22]
  wire [15:0] n584_I_t1b_t0b; // @[Top.scala 1701:22]
  wire [15:0] n584_I_t1b_t1b; // @[Top.scala 1701:22]
  wire [15:0] n584_O; // @[Top.scala 1701:22]
  wire  n585_valid_up; // @[Top.scala 1704:22]
  wire  n585_valid_down; // @[Top.scala 1704:22]
  wire [15:0] n585_I0; // @[Top.scala 1704:22]
  wire [15:0] n585_I1; // @[Top.scala 1704:22]
  wire [15:0] n585_O_t0b; // @[Top.scala 1704:22]
  wire [15:0] n585_O_t1b; // @[Top.scala 1704:22]
  wire  n586_clock; // @[Top.scala 1708:22]
  wire  n586_reset; // @[Top.scala 1708:22]
  wire  n586_valid_up; // @[Top.scala 1708:22]
  wire  n586_valid_down; // @[Top.scala 1708:22]
  wire [15:0] n586_I_t0b; // @[Top.scala 1708:22]
  wire [15:0] n586_I_t1b; // @[Top.scala 1708:22]
  wire [15:0] n586_O; // @[Top.scala 1708:22]
  wire  n588_valid_up; // @[Top.scala 1711:22]
  wire  n588_valid_down; // @[Top.scala 1711:22]
  wire [15:0] n588_I0; // @[Top.scala 1711:22]
  wire [15:0] n588_I1; // @[Top.scala 1711:22]
  wire [15:0] n588_O_t0b; // @[Top.scala 1711:22]
  wire [15:0] n588_O_t1b; // @[Top.scala 1711:22]
  wire  n589_valid_up; // @[Top.scala 1715:22]
  wire  n589_valid_down; // @[Top.scala 1715:22]
  wire [15:0] n589_I_t0b; // @[Top.scala 1715:22]
  wire [15:0] n589_I_t1b; // @[Top.scala 1715:22]
  wire [15:0] n589_O; // @[Top.scala 1715:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n591_valid_up; // @[Top.scala 1719:22]
  wire  n591_valid_down; // @[Top.scala 1719:22]
  wire [15:0] n591_I0; // @[Top.scala 1719:22]
  wire [15:0] n591_I1; // @[Top.scala 1719:22]
  wire [15:0] n591_O_t0b; // @[Top.scala 1719:22]
  wire [15:0] n591_O_t1b; // @[Top.scala 1719:22]
  wire  n592_valid_up; // @[Top.scala 1723:22]
  wire  n592_valid_down; // @[Top.scala 1723:22]
  wire [15:0] n592_I_t0b; // @[Top.scala 1723:22]
  wire [15:0] n592_I_t1b; // @[Top.scala 1723:22]
  wire [15:0] n592_O; // @[Top.scala 1723:22]
  wire  n593_valid_up; // @[Top.scala 1726:22]
  wire  n593_valid_down; // @[Top.scala 1726:22]
  wire [15:0] n593_I0; // @[Top.scala 1726:22]
  wire [15:0] n593_I1; // @[Top.scala 1726:22]
  wire [15:0] n593_O_t0b; // @[Top.scala 1726:22]
  wire [15:0] n593_O_t1b; // @[Top.scala 1726:22]
  wire  n594_valid_up; // @[Top.scala 1730:22]
  wire  n594_valid_down; // @[Top.scala 1730:22]
  wire [15:0] n594_I0; // @[Top.scala 1730:22]
  wire [15:0] n594_I1; // @[Top.scala 1730:22]
  wire [15:0] n594_O_t0b; // @[Top.scala 1730:22]
  wire [15:0] n594_O_t1b; // @[Top.scala 1730:22]
  wire  n595_valid_up; // @[Top.scala 1734:22]
  wire  n595_valid_down; // @[Top.scala 1734:22]
  wire [15:0] n595_I0_t0b; // @[Top.scala 1734:22]
  wire [15:0] n595_I0_t1b; // @[Top.scala 1734:22]
  wire [15:0] n595_I1_t0b; // @[Top.scala 1734:22]
  wire [15:0] n595_I1_t1b; // @[Top.scala 1734:22]
  wire [15:0] n595_O_t0b_t0b; // @[Top.scala 1734:22]
  wire [15:0] n595_O_t0b_t1b; // @[Top.scala 1734:22]
  wire [15:0] n595_O_t1b_t0b; // @[Top.scala 1734:22]
  wire [15:0] n595_O_t1b_t1b; // @[Top.scala 1734:22]
  wire  n596_clock; // @[Top.scala 1738:22]
  wire  n596_reset; // @[Top.scala 1738:22]
  wire  n596_valid_up; // @[Top.scala 1738:22]
  wire  n596_valid_down; // @[Top.scala 1738:22]
  wire [15:0] n596_I_t0b_t0b; // @[Top.scala 1738:22]
  wire [15:0] n596_I_t0b_t1b; // @[Top.scala 1738:22]
  wire [15:0] n596_I_t1b_t0b; // @[Top.scala 1738:22]
  wire [15:0] n596_I_t1b_t1b; // @[Top.scala 1738:22]
  wire [15:0] n596_O_t0b_t0b; // @[Top.scala 1738:22]
  wire [15:0] n596_O_t0b_t1b; // @[Top.scala 1738:22]
  wire [15:0] n596_O_t1b_t0b; // @[Top.scala 1738:22]
  wire [15:0] n596_O_t1b_t1b; // @[Top.scala 1738:22]
  wire  n597_valid_up; // @[Top.scala 1741:22]
  wire  n597_valid_down; // @[Top.scala 1741:22]
  wire  n597_I0; // @[Top.scala 1741:22]
  wire [15:0] n597_I1_t0b_t0b; // @[Top.scala 1741:22]
  wire [15:0] n597_I1_t0b_t1b; // @[Top.scala 1741:22]
  wire [15:0] n597_I1_t1b_t0b; // @[Top.scala 1741:22]
  wire [15:0] n597_I1_t1b_t1b; // @[Top.scala 1741:22]
  wire  n597_O_t0b; // @[Top.scala 1741:22]
  wire [15:0] n597_O_t1b_t0b_t0b; // @[Top.scala 1741:22]
  wire [15:0] n597_O_t1b_t0b_t1b; // @[Top.scala 1741:22]
  wire [15:0] n597_O_t1b_t1b_t0b; // @[Top.scala 1741:22]
  wire [15:0] n597_O_t1b_t1b_t1b; // @[Top.scala 1741:22]
  wire  n598_valid_up; // @[Top.scala 1745:22]
  wire  n598_valid_down; // @[Top.scala 1745:22]
  wire  n598_I_t0b; // @[Top.scala 1745:22]
  wire [15:0] n598_I_t1b_t0b_t0b; // @[Top.scala 1745:22]
  wire [15:0] n598_I_t1b_t0b_t1b; // @[Top.scala 1745:22]
  wire [15:0] n598_I_t1b_t1b_t0b; // @[Top.scala 1745:22]
  wire [15:0] n598_I_t1b_t1b_t1b; // @[Top.scala 1745:22]
  wire [15:0] n598_O_t0b; // @[Top.scala 1745:22]
  wire [15:0] n598_O_t1b; // @[Top.scala 1745:22]
  wire  n600_valid_up; // @[Top.scala 1748:22]
  wire  n600_valid_down; // @[Top.scala 1748:22]
  wire [15:0] n600_I0; // @[Top.scala 1748:22]
  wire [15:0] n600_I1_t0b; // @[Top.scala 1748:22]
  wire [15:0] n600_I1_t1b; // @[Top.scala 1748:22]
  wire [15:0] n600_O_t0b; // @[Top.scala 1748:22]
  wire [15:0] n600_O_t1b_t0b; // @[Top.scala 1748:22]
  wire [15:0] n600_O_t1b_t1b; // @[Top.scala 1748:22]
  Fst n566 ( // @[Top.scala 1645:22]
    .valid_up(n566_valid_up),
    .valid_down(n566_valid_down),
    .I_t0b(n566_I_t0b),
    .O(n566_O)
  );
  FIFO_1 n599 ( // @[Top.scala 1648:22]
    .clock(n599_clock),
    .reset(n599_reset),
    .valid_up(n599_valid_up),
    .valid_down(n599_valid_down),
    .I(n599_I),
    .O(n599_O)
  );
  FIFO_1 n587 ( // @[Top.scala 1651:22]
    .clock(n587_clock),
    .reset(n587_reset),
    .valid_up(n587_valid_up),
    .valid_down(n587_valid_down),
    .I(n587_I),
    .O(n587_O)
  );
  Snd n567 ( // @[Top.scala 1654:22]
    .valid_up(n567_valid_up),
    .valid_down(n567_valid_down),
    .I_t1b_t0b(n567_I_t1b_t0b),
    .I_t1b_t1b(n567_I_t1b_t1b),
    .O_t0b(n567_O_t0b),
    .O_t1b(n567_O_t1b)
  );
  Fst_1 n568 ( // @[Top.scala 1657:22]
    .valid_up(n568_valid_up),
    .valid_down(n568_valid_down),
    .I_t0b(n568_I_t0b),
    .O(n568_O)
  );
  Snd_1 n569 ( // @[Top.scala 1660:22]
    .valid_up(n569_valid_up),
    .valid_down(n569_valid_down),
    .I_t1b(n569_I_t1b),
    .O(n569_O)
  );
  AtomTuple_1 n570 ( // @[Top.scala 1663:22]
    .valid_up(n570_valid_up),
    .valid_down(n570_valid_down),
    .I0(n570_I0),
    .I1(n570_I1),
    .O_t0b(n570_O_t0b),
    .O_t1b(n570_O_t1b)
  );
  Add n571 ( // @[Top.scala 1667:22]
    .valid_up(n571_valid_up),
    .valid_down(n571_valid_down),
    .I_t0b(n571_I_t0b),
    .I_t1b(n571_I_t1b),
    .O(n571_O)
  );
  InitialDelayCounter_45 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n574 ( // @[Top.scala 1671:22]
    .valid_up(n574_valid_up),
    .valid_down(n574_valid_down),
    .I0(n574_I0),
    .O_t0b(n574_O_t0b)
  );
  RShift n575 ( // @[Top.scala 1675:22]
    .valid_up(n575_valid_up),
    .valid_down(n575_valid_down),
    .I_t0b(n575_I_t0b),
    .O(n575_O)
  );
  AtomTuple_1 n576 ( // @[Top.scala 1678:22]
    .valid_up(n576_valid_up),
    .valid_down(n576_valid_down),
    .I0(n576_I0),
    .I1(n576_I1),
    .O_t0b(n576_O_t0b),
    .O_t1b(n576_O_t1b)
  );
  Eq n577 ( // @[Top.scala 1682:22]
    .valid_up(n577_valid_up),
    .valid_down(n577_valid_down),
    .I_t0b(n577_I_t0b),
    .I_t1b(n577_I_t1b),
    .O(n577_O)
  );
  InitialDelayCounter_45 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n580 ( // @[Top.scala 1686:22]
    .valid_up(n580_valid_up),
    .valid_down(n580_valid_down),
    .I0(n580_I0),
    .I1(n580_I1),
    .O_t0b(n580_O_t0b),
    .O_t1b(n580_O_t1b)
  );
  Add n581 ( // @[Top.scala 1690:22]
    .valid_up(n581_valid_up),
    .valid_down(n581_valid_down),
    .I_t0b(n581_I_t0b),
    .I_t1b(n581_I_t1b),
    .O(n581_O)
  );
  AtomTuple_1 n582 ( // @[Top.scala 1693:22]
    .valid_up(n582_valid_up),
    .valid_down(n582_valid_down),
    .I0(n582_I0),
    .I1(n582_I1),
    .O_t0b(n582_O_t0b),
    .O_t1b(n582_O_t1b)
  );
  AtomTuple_9 n583 ( // @[Top.scala 1697:22]
    .valid_up(n583_valid_up),
    .valid_down(n583_valid_down),
    .I0(n583_I0),
    .I1_t0b(n583_I1_t0b),
    .I1_t1b(n583_I1_t1b),
    .O_t0b(n583_O_t0b),
    .O_t1b_t0b(n583_O_t1b_t0b),
    .O_t1b_t1b(n583_O_t1b_t1b)
  );
  If n584 ( // @[Top.scala 1701:22]
    .valid_up(n584_valid_up),
    .valid_down(n584_valid_down),
    .I_t0b(n584_I_t0b),
    .I_t1b_t0b(n584_I_t1b_t0b),
    .I_t1b_t1b(n584_I_t1b_t1b),
    .O(n584_O)
  );
  AtomTuple_1 n585 ( // @[Top.scala 1704:22]
    .valid_up(n585_valid_up),
    .valid_down(n585_valid_down),
    .I0(n585_I0),
    .I1(n585_I1),
    .O_t0b(n585_O_t0b),
    .O_t1b(n585_O_t1b)
  );
  Mul n586 ( // @[Top.scala 1708:22]
    .clock(n586_clock),
    .reset(n586_reset),
    .valid_up(n586_valid_up),
    .valid_down(n586_valid_down),
    .I_t0b(n586_I_t0b),
    .I_t1b(n586_I_t1b),
    .O(n586_O)
  );
  AtomTuple_1 n588 ( // @[Top.scala 1711:22]
    .valid_up(n588_valid_up),
    .valid_down(n588_valid_down),
    .I0(n588_I0),
    .I1(n588_I1),
    .O_t0b(n588_O_t0b),
    .O_t1b(n588_O_t1b)
  );
  Lt n589 ( // @[Top.scala 1715:22]
    .valid_up(n589_valid_up),
    .valid_down(n589_valid_down),
    .I_t0b(n589_I_t0b),
    .I_t1b(n589_I_t1b),
    .O(n589_O)
  );
  InitialDelayCounter_45 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n591 ( // @[Top.scala 1719:22]
    .valid_up(n591_valid_up),
    .valid_down(n591_valid_down),
    .I0(n591_I0),
    .I1(n591_I1),
    .O_t0b(n591_O_t0b),
    .O_t1b(n591_O_t1b)
  );
  Sub n592 ( // @[Top.scala 1723:22]
    .valid_up(n592_valid_up),
    .valid_down(n592_valid_down),
    .I_t0b(n592_I_t0b),
    .I_t1b(n592_I_t1b),
    .O(n592_O)
  );
  AtomTuple_1 n593 ( // @[Top.scala 1726:22]
    .valid_up(n593_valid_up),
    .valid_down(n593_valid_down),
    .I0(n593_I0),
    .I1(n593_I1),
    .O_t0b(n593_O_t0b),
    .O_t1b(n593_O_t1b)
  );
  AtomTuple_1 n594 ( // @[Top.scala 1730:22]
    .valid_up(n594_valid_up),
    .valid_down(n594_valid_down),
    .I0(n594_I0),
    .I1(n594_I1),
    .O_t0b(n594_O_t0b),
    .O_t1b(n594_O_t1b)
  );
  AtomTuple_15 n595 ( // @[Top.scala 1734:22]
    .valid_up(n595_valid_up),
    .valid_down(n595_valid_down),
    .I0_t0b(n595_I0_t0b),
    .I0_t1b(n595_I0_t1b),
    .I1_t0b(n595_I1_t0b),
    .I1_t1b(n595_I1_t1b),
    .O_t0b_t0b(n595_O_t0b_t0b),
    .O_t0b_t1b(n595_O_t0b_t1b),
    .O_t1b_t0b(n595_O_t1b_t0b),
    .O_t1b_t1b(n595_O_t1b_t1b)
  );
  FIFO_3 n596 ( // @[Top.scala 1738:22]
    .clock(n596_clock),
    .reset(n596_reset),
    .valid_up(n596_valid_up),
    .valid_down(n596_valid_down),
    .I_t0b_t0b(n596_I_t0b_t0b),
    .I_t0b_t1b(n596_I_t0b_t1b),
    .I_t1b_t0b(n596_I_t1b_t0b),
    .I_t1b_t1b(n596_I_t1b_t1b),
    .O_t0b_t0b(n596_O_t0b_t0b),
    .O_t0b_t1b(n596_O_t0b_t1b),
    .O_t1b_t0b(n596_O_t1b_t0b),
    .O_t1b_t1b(n596_O_t1b_t1b)
  );
  AtomTuple_16 n597 ( // @[Top.scala 1741:22]
    .valid_up(n597_valid_up),
    .valid_down(n597_valid_down),
    .I0(n597_I0),
    .I1_t0b_t0b(n597_I1_t0b_t0b),
    .I1_t0b_t1b(n597_I1_t0b_t1b),
    .I1_t1b_t0b(n597_I1_t1b_t0b),
    .I1_t1b_t1b(n597_I1_t1b_t1b),
    .O_t0b(n597_O_t0b),
    .O_t1b_t0b_t0b(n597_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n597_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n597_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n597_O_t1b_t1b_t1b)
  );
  If_1 n598 ( // @[Top.scala 1745:22]
    .valid_up(n598_valid_up),
    .valid_down(n598_valid_down),
    .I_t0b(n598_I_t0b),
    .I_t1b_t0b_t0b(n598_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n598_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n598_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n598_I_t1b_t1b_t1b),
    .O_t0b(n598_O_t0b),
    .O_t1b(n598_O_t1b)
  );
  AtomTuple_3 n600 ( // @[Top.scala 1748:22]
    .valid_up(n600_valid_up),
    .valid_down(n600_valid_down),
    .I0(n600_I0),
    .I1_t0b(n600_I1_t0b),
    .I1_t1b(n600_I1_t1b),
    .O_t0b(n600_O_t0b),
    .O_t1b_t0b(n600_O_t1b_t0b),
    .O_t1b_t1b(n600_O_t1b_t1b)
  );
  assign valid_down = n600_valid_down; // @[Top.scala 1753:16]
  assign O_t0b = n600_O_t0b; // @[Top.scala 1752:7]
  assign O_t1b_t0b = n600_O_t1b_t0b; // @[Top.scala 1752:7]
  assign O_t1b_t1b = n600_O_t1b_t1b; // @[Top.scala 1752:7]
  assign n566_valid_up = valid_up; // @[Top.scala 1647:19]
  assign n566_I_t0b = I_t0b; // @[Top.scala 1646:12]
  assign n599_clock = clock;
  assign n599_reset = reset;
  assign n599_valid_up = n566_valid_down; // @[Top.scala 1650:19]
  assign n599_I = n566_O; // @[Top.scala 1649:12]
  assign n587_clock = clock;
  assign n587_reset = reset;
  assign n587_valid_up = n566_valid_down; // @[Top.scala 1653:19]
  assign n587_I = n566_O; // @[Top.scala 1652:12]
  assign n567_valid_up = valid_up; // @[Top.scala 1656:19]
  assign n567_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1655:12]
  assign n567_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1655:12]
  assign n568_valid_up = n567_valid_down; // @[Top.scala 1659:19]
  assign n568_I_t0b = n567_O_t0b; // @[Top.scala 1658:12]
  assign n569_valid_up = n567_valid_down; // @[Top.scala 1662:19]
  assign n569_I_t1b = n567_O_t1b; // @[Top.scala 1661:12]
  assign n570_valid_up = n568_valid_down & n569_valid_down; // @[Top.scala 1666:19]
  assign n570_I0 = n568_O; // @[Top.scala 1664:13]
  assign n570_I1 = n569_O; // @[Top.scala 1665:13]
  assign n571_valid_up = n570_valid_down; // @[Top.scala 1669:19]
  assign n571_I_t0b = n570_O_t0b; // @[Top.scala 1668:12]
  assign n571_I_t1b = n570_O_t1b; // @[Top.scala 1668:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n574_valid_up = n571_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 1674:19]
  assign n574_I0 = n571_O; // @[Top.scala 1672:13]
  assign n575_valid_up = n574_valid_down; // @[Top.scala 1677:19]
  assign n575_I_t0b = n574_O_t0b; // @[Top.scala 1676:12]
  assign n576_valid_up = n575_valid_down & n568_valid_down; // @[Top.scala 1681:19]
  assign n576_I0 = n575_O; // @[Top.scala 1679:13]
  assign n576_I1 = n568_O; // @[Top.scala 1680:13]
  assign n577_valid_up = n576_valid_down; // @[Top.scala 1684:19]
  assign n577_I_t0b = n576_O_t0b; // @[Top.scala 1683:12]
  assign n577_I_t1b = n576_O_t1b; // @[Top.scala 1683:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n580_valid_up = n575_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1689:19]
  assign n580_I0 = n575_O; // @[Top.scala 1687:13]
  assign n580_I1 = 16'h1; // @[Top.scala 1688:13]
  assign n581_valid_up = n580_valid_down; // @[Top.scala 1692:19]
  assign n581_I_t0b = n580_O_t0b; // @[Top.scala 1691:12]
  assign n581_I_t1b = n580_O_t1b; // @[Top.scala 1691:12]
  assign n582_valid_up = n581_valid_down & n575_valid_down; // @[Top.scala 1696:19]
  assign n582_I0 = n581_O; // @[Top.scala 1694:13]
  assign n582_I1 = n575_O; // @[Top.scala 1695:13]
  assign n583_valid_up = n577_valid_down & n582_valid_down; // @[Top.scala 1700:19]
  assign n583_I0 = n577_O[0]; // @[Top.scala 1698:13]
  assign n583_I1_t0b = n582_O_t0b; // @[Top.scala 1699:13]
  assign n583_I1_t1b = n582_O_t1b; // @[Top.scala 1699:13]
  assign n584_valid_up = n583_valid_down; // @[Top.scala 1703:19]
  assign n584_I_t0b = n583_O_t0b; // @[Top.scala 1702:12]
  assign n584_I_t1b_t0b = n583_O_t1b_t0b; // @[Top.scala 1702:12]
  assign n584_I_t1b_t1b = n583_O_t1b_t1b; // @[Top.scala 1702:12]
  assign n585_valid_up = n584_valid_down; // @[Top.scala 1707:19]
  assign n585_I0 = n584_O; // @[Top.scala 1705:13]
  assign n585_I1 = n584_O; // @[Top.scala 1706:13]
  assign n586_clock = clock;
  assign n586_reset = reset;
  assign n586_valid_up = n585_valid_down; // @[Top.scala 1710:19]
  assign n586_I_t0b = n585_O_t0b; // @[Top.scala 1709:12]
  assign n586_I_t1b = n585_O_t1b; // @[Top.scala 1709:12]
  assign n588_valid_up = n587_valid_down & n586_valid_down; // @[Top.scala 1714:19]
  assign n588_I0 = n587_O; // @[Top.scala 1712:13]
  assign n588_I1 = n586_O; // @[Top.scala 1713:13]
  assign n589_valid_up = n588_valid_down; // @[Top.scala 1717:19]
  assign n589_I_t0b = n588_O_t0b; // @[Top.scala 1716:12]
  assign n589_I_t1b = n588_O_t1b; // @[Top.scala 1716:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n591_valid_up = n584_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1722:19]
  assign n591_I0 = n584_O; // @[Top.scala 1720:13]
  assign n591_I1 = 16'h1; // @[Top.scala 1721:13]
  assign n592_valid_up = n591_valid_down; // @[Top.scala 1725:19]
  assign n592_I_t0b = n591_O_t0b; // @[Top.scala 1724:12]
  assign n592_I_t1b = n591_O_t1b; // @[Top.scala 1724:12]
  assign n593_valid_up = n568_valid_down & n592_valid_down; // @[Top.scala 1729:19]
  assign n593_I0 = n568_O; // @[Top.scala 1727:13]
  assign n593_I1 = n592_O; // @[Top.scala 1728:13]
  assign n594_valid_up = n584_valid_down & n569_valid_down; // @[Top.scala 1733:19]
  assign n594_I0 = n584_O; // @[Top.scala 1731:13]
  assign n594_I1 = n569_O; // @[Top.scala 1732:13]
  assign n595_valid_up = n593_valid_down & n594_valid_down; // @[Top.scala 1737:19]
  assign n595_I0_t0b = n593_O_t0b; // @[Top.scala 1735:13]
  assign n595_I0_t1b = n593_O_t1b; // @[Top.scala 1735:13]
  assign n595_I1_t0b = n594_O_t0b; // @[Top.scala 1736:13]
  assign n595_I1_t1b = n594_O_t1b; // @[Top.scala 1736:13]
  assign n596_clock = clock;
  assign n596_reset = reset;
  assign n596_valid_up = n595_valid_down; // @[Top.scala 1740:19]
  assign n596_I_t0b_t0b = n595_O_t0b_t0b; // @[Top.scala 1739:12]
  assign n596_I_t0b_t1b = n595_O_t0b_t1b; // @[Top.scala 1739:12]
  assign n596_I_t1b_t0b = n595_O_t1b_t0b; // @[Top.scala 1739:12]
  assign n596_I_t1b_t1b = n595_O_t1b_t1b; // @[Top.scala 1739:12]
  assign n597_valid_up = n589_valid_down & n596_valid_down; // @[Top.scala 1744:19]
  assign n597_I0 = n589_O[0]; // @[Top.scala 1742:13]
  assign n597_I1_t0b_t0b = n596_O_t0b_t0b; // @[Top.scala 1743:13]
  assign n597_I1_t0b_t1b = n596_O_t0b_t1b; // @[Top.scala 1743:13]
  assign n597_I1_t1b_t0b = n596_O_t1b_t0b; // @[Top.scala 1743:13]
  assign n597_I1_t1b_t1b = n596_O_t1b_t1b; // @[Top.scala 1743:13]
  assign n598_valid_up = n597_valid_down; // @[Top.scala 1747:19]
  assign n598_I_t0b = n597_O_t0b; // @[Top.scala 1746:12]
  assign n598_I_t1b_t0b_t0b = n597_O_t1b_t0b_t0b; // @[Top.scala 1746:12]
  assign n598_I_t1b_t0b_t1b = n597_O_t1b_t0b_t1b; // @[Top.scala 1746:12]
  assign n598_I_t1b_t1b_t0b = n597_O_t1b_t1b_t0b; // @[Top.scala 1746:12]
  assign n598_I_t1b_t1b_t1b = n597_O_t1b_t1b_t1b; // @[Top.scala 1746:12]
  assign n600_valid_up = n599_valid_down & n598_valid_down; // @[Top.scala 1751:19]
  assign n600_I0 = n599_O; // @[Top.scala 1749:13]
  assign n600_I1_t0b = n598_O_t0b; // @[Top.scala 1750:13]
  assign n600_I1_t1b = n598_O_t1b; // @[Top.scala 1750:13]
endmodule
module MapS_15(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_15 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_15 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_15 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_15 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t0b(other_ops_2_O_t0b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t0b = fst_op_O_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t0b = other_ops_2_O_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_15(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t0b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t0b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t0b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t0b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_15 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t0b(op_O_3_t0b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t0b = op_O_1_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t0b = op_O_2_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t0b = op_O_3_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_48(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [5:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 6'h2e; // @[InitialDelayCounter.scala 17:17]
  wire [5:0] _T_4 = value + 6'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 6'h2e; // @[InitialDelayCounter.scala 16:16]
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
  value = _RAND_0[5:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 6'h0;
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Module_16(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  n605_valid_up; // @[Top.scala 1759:22]
  wire  n605_valid_down; // @[Top.scala 1759:22]
  wire [15:0] n605_I_t0b; // @[Top.scala 1759:22]
  wire [15:0] n605_O; // @[Top.scala 1759:22]
  wire  n638_clock; // @[Top.scala 1762:22]
  wire  n638_reset; // @[Top.scala 1762:22]
  wire  n638_valid_up; // @[Top.scala 1762:22]
  wire  n638_valid_down; // @[Top.scala 1762:22]
  wire [15:0] n638_I; // @[Top.scala 1762:22]
  wire [15:0] n638_O; // @[Top.scala 1762:22]
  wire  n626_clock; // @[Top.scala 1765:22]
  wire  n626_reset; // @[Top.scala 1765:22]
  wire  n626_valid_up; // @[Top.scala 1765:22]
  wire  n626_valid_down; // @[Top.scala 1765:22]
  wire [15:0] n626_I; // @[Top.scala 1765:22]
  wire [15:0] n626_O; // @[Top.scala 1765:22]
  wire  n606_valid_up; // @[Top.scala 1768:22]
  wire  n606_valid_down; // @[Top.scala 1768:22]
  wire [15:0] n606_I_t1b_t0b; // @[Top.scala 1768:22]
  wire [15:0] n606_I_t1b_t1b; // @[Top.scala 1768:22]
  wire [15:0] n606_O_t0b; // @[Top.scala 1768:22]
  wire [15:0] n606_O_t1b; // @[Top.scala 1768:22]
  wire  n607_valid_up; // @[Top.scala 1771:22]
  wire  n607_valid_down; // @[Top.scala 1771:22]
  wire [15:0] n607_I_t0b; // @[Top.scala 1771:22]
  wire [15:0] n607_O; // @[Top.scala 1771:22]
  wire  n608_valid_up; // @[Top.scala 1774:22]
  wire  n608_valid_down; // @[Top.scala 1774:22]
  wire [15:0] n608_I_t1b; // @[Top.scala 1774:22]
  wire [15:0] n608_O; // @[Top.scala 1774:22]
  wire  n609_valid_up; // @[Top.scala 1777:22]
  wire  n609_valid_down; // @[Top.scala 1777:22]
  wire [15:0] n609_I0; // @[Top.scala 1777:22]
  wire [15:0] n609_I1; // @[Top.scala 1777:22]
  wire [15:0] n609_O_t0b; // @[Top.scala 1777:22]
  wire [15:0] n609_O_t1b; // @[Top.scala 1777:22]
  wire  n610_valid_up; // @[Top.scala 1781:22]
  wire  n610_valid_down; // @[Top.scala 1781:22]
  wire [15:0] n610_I_t0b; // @[Top.scala 1781:22]
  wire [15:0] n610_I_t1b; // @[Top.scala 1781:22]
  wire [15:0] n610_O; // @[Top.scala 1781:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n613_valid_up; // @[Top.scala 1785:22]
  wire  n613_valid_down; // @[Top.scala 1785:22]
  wire [15:0] n613_I0; // @[Top.scala 1785:22]
  wire [15:0] n613_O_t0b; // @[Top.scala 1785:22]
  wire  n614_valid_up; // @[Top.scala 1789:22]
  wire  n614_valid_down; // @[Top.scala 1789:22]
  wire [15:0] n614_I_t0b; // @[Top.scala 1789:22]
  wire [15:0] n614_O; // @[Top.scala 1789:22]
  wire  n615_valid_up; // @[Top.scala 1792:22]
  wire  n615_valid_down; // @[Top.scala 1792:22]
  wire [15:0] n615_I0; // @[Top.scala 1792:22]
  wire [15:0] n615_I1; // @[Top.scala 1792:22]
  wire [15:0] n615_O_t0b; // @[Top.scala 1792:22]
  wire [15:0] n615_O_t1b; // @[Top.scala 1792:22]
  wire  n616_valid_up; // @[Top.scala 1796:22]
  wire  n616_valid_down; // @[Top.scala 1796:22]
  wire [15:0] n616_I_t0b; // @[Top.scala 1796:22]
  wire [15:0] n616_I_t1b; // @[Top.scala 1796:22]
  wire [15:0] n616_O; // @[Top.scala 1796:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n619_valid_up; // @[Top.scala 1800:22]
  wire  n619_valid_down; // @[Top.scala 1800:22]
  wire [15:0] n619_I0; // @[Top.scala 1800:22]
  wire [15:0] n619_I1; // @[Top.scala 1800:22]
  wire [15:0] n619_O_t0b; // @[Top.scala 1800:22]
  wire [15:0] n619_O_t1b; // @[Top.scala 1800:22]
  wire  n620_valid_up; // @[Top.scala 1804:22]
  wire  n620_valid_down; // @[Top.scala 1804:22]
  wire [15:0] n620_I_t0b; // @[Top.scala 1804:22]
  wire [15:0] n620_I_t1b; // @[Top.scala 1804:22]
  wire [15:0] n620_O; // @[Top.scala 1804:22]
  wire  n621_valid_up; // @[Top.scala 1807:22]
  wire  n621_valid_down; // @[Top.scala 1807:22]
  wire [15:0] n621_I0; // @[Top.scala 1807:22]
  wire [15:0] n621_I1; // @[Top.scala 1807:22]
  wire [15:0] n621_O_t0b; // @[Top.scala 1807:22]
  wire [15:0] n621_O_t1b; // @[Top.scala 1807:22]
  wire  n622_valid_up; // @[Top.scala 1811:22]
  wire  n622_valid_down; // @[Top.scala 1811:22]
  wire  n622_I0; // @[Top.scala 1811:22]
  wire [15:0] n622_I1_t0b; // @[Top.scala 1811:22]
  wire [15:0] n622_I1_t1b; // @[Top.scala 1811:22]
  wire  n622_O_t0b; // @[Top.scala 1811:22]
  wire [15:0] n622_O_t1b_t0b; // @[Top.scala 1811:22]
  wire [15:0] n622_O_t1b_t1b; // @[Top.scala 1811:22]
  wire  n623_valid_up; // @[Top.scala 1815:22]
  wire  n623_valid_down; // @[Top.scala 1815:22]
  wire  n623_I_t0b; // @[Top.scala 1815:22]
  wire [15:0] n623_I_t1b_t0b; // @[Top.scala 1815:22]
  wire [15:0] n623_I_t1b_t1b; // @[Top.scala 1815:22]
  wire [15:0] n623_O; // @[Top.scala 1815:22]
  wire  n624_valid_up; // @[Top.scala 1818:22]
  wire  n624_valid_down; // @[Top.scala 1818:22]
  wire [15:0] n624_I0; // @[Top.scala 1818:22]
  wire [15:0] n624_I1; // @[Top.scala 1818:22]
  wire [15:0] n624_O_t0b; // @[Top.scala 1818:22]
  wire [15:0] n624_O_t1b; // @[Top.scala 1818:22]
  wire  n625_clock; // @[Top.scala 1822:22]
  wire  n625_reset; // @[Top.scala 1822:22]
  wire  n625_valid_up; // @[Top.scala 1822:22]
  wire  n625_valid_down; // @[Top.scala 1822:22]
  wire [15:0] n625_I_t0b; // @[Top.scala 1822:22]
  wire [15:0] n625_I_t1b; // @[Top.scala 1822:22]
  wire [15:0] n625_O; // @[Top.scala 1822:22]
  wire  n627_valid_up; // @[Top.scala 1825:22]
  wire  n627_valid_down; // @[Top.scala 1825:22]
  wire [15:0] n627_I0; // @[Top.scala 1825:22]
  wire [15:0] n627_I1; // @[Top.scala 1825:22]
  wire [15:0] n627_O_t0b; // @[Top.scala 1825:22]
  wire [15:0] n627_O_t1b; // @[Top.scala 1825:22]
  wire  n628_valid_up; // @[Top.scala 1829:22]
  wire  n628_valid_down; // @[Top.scala 1829:22]
  wire [15:0] n628_I_t0b; // @[Top.scala 1829:22]
  wire [15:0] n628_I_t1b; // @[Top.scala 1829:22]
  wire [15:0] n628_O; // @[Top.scala 1829:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n630_valid_up; // @[Top.scala 1833:22]
  wire  n630_valid_down; // @[Top.scala 1833:22]
  wire [15:0] n630_I0; // @[Top.scala 1833:22]
  wire [15:0] n630_I1; // @[Top.scala 1833:22]
  wire [15:0] n630_O_t0b; // @[Top.scala 1833:22]
  wire [15:0] n630_O_t1b; // @[Top.scala 1833:22]
  wire  n631_valid_up; // @[Top.scala 1837:22]
  wire  n631_valid_down; // @[Top.scala 1837:22]
  wire [15:0] n631_I_t0b; // @[Top.scala 1837:22]
  wire [15:0] n631_I_t1b; // @[Top.scala 1837:22]
  wire [15:0] n631_O; // @[Top.scala 1837:22]
  wire  n632_valid_up; // @[Top.scala 1840:22]
  wire  n632_valid_down; // @[Top.scala 1840:22]
  wire [15:0] n632_I0; // @[Top.scala 1840:22]
  wire [15:0] n632_I1; // @[Top.scala 1840:22]
  wire [15:0] n632_O_t0b; // @[Top.scala 1840:22]
  wire [15:0] n632_O_t1b; // @[Top.scala 1840:22]
  wire  n633_valid_up; // @[Top.scala 1844:22]
  wire  n633_valid_down; // @[Top.scala 1844:22]
  wire [15:0] n633_I0; // @[Top.scala 1844:22]
  wire [15:0] n633_I1; // @[Top.scala 1844:22]
  wire [15:0] n633_O_t0b; // @[Top.scala 1844:22]
  wire [15:0] n633_O_t1b; // @[Top.scala 1844:22]
  wire  n634_valid_up; // @[Top.scala 1848:22]
  wire  n634_valid_down; // @[Top.scala 1848:22]
  wire [15:0] n634_I0_t0b; // @[Top.scala 1848:22]
  wire [15:0] n634_I0_t1b; // @[Top.scala 1848:22]
  wire [15:0] n634_I1_t0b; // @[Top.scala 1848:22]
  wire [15:0] n634_I1_t1b; // @[Top.scala 1848:22]
  wire [15:0] n634_O_t0b_t0b; // @[Top.scala 1848:22]
  wire [15:0] n634_O_t0b_t1b; // @[Top.scala 1848:22]
  wire [15:0] n634_O_t1b_t0b; // @[Top.scala 1848:22]
  wire [15:0] n634_O_t1b_t1b; // @[Top.scala 1848:22]
  wire  n635_clock; // @[Top.scala 1852:22]
  wire  n635_reset; // @[Top.scala 1852:22]
  wire  n635_valid_up; // @[Top.scala 1852:22]
  wire  n635_valid_down; // @[Top.scala 1852:22]
  wire [15:0] n635_I_t0b_t0b; // @[Top.scala 1852:22]
  wire [15:0] n635_I_t0b_t1b; // @[Top.scala 1852:22]
  wire [15:0] n635_I_t1b_t0b; // @[Top.scala 1852:22]
  wire [15:0] n635_I_t1b_t1b; // @[Top.scala 1852:22]
  wire [15:0] n635_O_t0b_t0b; // @[Top.scala 1852:22]
  wire [15:0] n635_O_t0b_t1b; // @[Top.scala 1852:22]
  wire [15:0] n635_O_t1b_t0b; // @[Top.scala 1852:22]
  wire [15:0] n635_O_t1b_t1b; // @[Top.scala 1852:22]
  wire  n636_valid_up; // @[Top.scala 1855:22]
  wire  n636_valid_down; // @[Top.scala 1855:22]
  wire  n636_I0; // @[Top.scala 1855:22]
  wire [15:0] n636_I1_t0b_t0b; // @[Top.scala 1855:22]
  wire [15:0] n636_I1_t0b_t1b; // @[Top.scala 1855:22]
  wire [15:0] n636_I1_t1b_t0b; // @[Top.scala 1855:22]
  wire [15:0] n636_I1_t1b_t1b; // @[Top.scala 1855:22]
  wire  n636_O_t0b; // @[Top.scala 1855:22]
  wire [15:0] n636_O_t1b_t0b_t0b; // @[Top.scala 1855:22]
  wire [15:0] n636_O_t1b_t0b_t1b; // @[Top.scala 1855:22]
  wire [15:0] n636_O_t1b_t1b_t0b; // @[Top.scala 1855:22]
  wire [15:0] n636_O_t1b_t1b_t1b; // @[Top.scala 1855:22]
  wire  n637_valid_up; // @[Top.scala 1859:22]
  wire  n637_valid_down; // @[Top.scala 1859:22]
  wire  n637_I_t0b; // @[Top.scala 1859:22]
  wire [15:0] n637_I_t1b_t0b_t0b; // @[Top.scala 1859:22]
  wire [15:0] n637_I_t1b_t0b_t1b; // @[Top.scala 1859:22]
  wire [15:0] n637_I_t1b_t1b_t0b; // @[Top.scala 1859:22]
  wire [15:0] n637_I_t1b_t1b_t1b; // @[Top.scala 1859:22]
  wire [15:0] n637_O_t0b; // @[Top.scala 1859:22]
  wire [15:0] n637_O_t1b; // @[Top.scala 1859:22]
  wire  n639_valid_up; // @[Top.scala 1862:22]
  wire  n639_valid_down; // @[Top.scala 1862:22]
  wire [15:0] n639_I0; // @[Top.scala 1862:22]
  wire [15:0] n639_I1_t0b; // @[Top.scala 1862:22]
  wire [15:0] n639_I1_t1b; // @[Top.scala 1862:22]
  wire [15:0] n639_O_t0b; // @[Top.scala 1862:22]
  wire [15:0] n639_O_t1b_t0b; // @[Top.scala 1862:22]
  wire [15:0] n639_O_t1b_t1b; // @[Top.scala 1862:22]
  Fst n605 ( // @[Top.scala 1759:22]
    .valid_up(n605_valid_up),
    .valid_down(n605_valid_down),
    .I_t0b(n605_I_t0b),
    .O(n605_O)
  );
  FIFO_1 n638 ( // @[Top.scala 1762:22]
    .clock(n638_clock),
    .reset(n638_reset),
    .valid_up(n638_valid_up),
    .valid_down(n638_valid_down),
    .I(n638_I),
    .O(n638_O)
  );
  FIFO_1 n626 ( // @[Top.scala 1765:22]
    .clock(n626_clock),
    .reset(n626_reset),
    .valid_up(n626_valid_up),
    .valid_down(n626_valid_down),
    .I(n626_I),
    .O(n626_O)
  );
  Snd n606 ( // @[Top.scala 1768:22]
    .valid_up(n606_valid_up),
    .valid_down(n606_valid_down),
    .I_t1b_t0b(n606_I_t1b_t0b),
    .I_t1b_t1b(n606_I_t1b_t1b),
    .O_t0b(n606_O_t0b),
    .O_t1b(n606_O_t1b)
  );
  Fst_1 n607 ( // @[Top.scala 1771:22]
    .valid_up(n607_valid_up),
    .valid_down(n607_valid_down),
    .I_t0b(n607_I_t0b),
    .O(n607_O)
  );
  Snd_1 n608 ( // @[Top.scala 1774:22]
    .valid_up(n608_valid_up),
    .valid_down(n608_valid_down),
    .I_t1b(n608_I_t1b),
    .O(n608_O)
  );
  AtomTuple_1 n609 ( // @[Top.scala 1777:22]
    .valid_up(n609_valid_up),
    .valid_down(n609_valid_down),
    .I0(n609_I0),
    .I1(n609_I1),
    .O_t0b(n609_O_t0b),
    .O_t1b(n609_O_t1b)
  );
  Add n610 ( // @[Top.scala 1781:22]
    .valid_up(n610_valid_up),
    .valid_down(n610_valid_down),
    .I_t0b(n610_I_t0b),
    .I_t1b(n610_I_t1b),
    .O(n610_O)
  );
  InitialDelayCounter_48 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n613 ( // @[Top.scala 1785:22]
    .valid_up(n613_valid_up),
    .valid_down(n613_valid_down),
    .I0(n613_I0),
    .O_t0b(n613_O_t0b)
  );
  RShift n614 ( // @[Top.scala 1789:22]
    .valid_up(n614_valid_up),
    .valid_down(n614_valid_down),
    .I_t0b(n614_I_t0b),
    .O(n614_O)
  );
  AtomTuple_1 n615 ( // @[Top.scala 1792:22]
    .valid_up(n615_valid_up),
    .valid_down(n615_valid_down),
    .I0(n615_I0),
    .I1(n615_I1),
    .O_t0b(n615_O_t0b),
    .O_t1b(n615_O_t1b)
  );
  Eq n616 ( // @[Top.scala 1796:22]
    .valid_up(n616_valid_up),
    .valid_down(n616_valid_down),
    .I_t0b(n616_I_t0b),
    .I_t1b(n616_I_t1b),
    .O(n616_O)
  );
  InitialDelayCounter_48 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n619 ( // @[Top.scala 1800:22]
    .valid_up(n619_valid_up),
    .valid_down(n619_valid_down),
    .I0(n619_I0),
    .I1(n619_I1),
    .O_t0b(n619_O_t0b),
    .O_t1b(n619_O_t1b)
  );
  Add n620 ( // @[Top.scala 1804:22]
    .valid_up(n620_valid_up),
    .valid_down(n620_valid_down),
    .I_t0b(n620_I_t0b),
    .I_t1b(n620_I_t1b),
    .O(n620_O)
  );
  AtomTuple_1 n621 ( // @[Top.scala 1807:22]
    .valid_up(n621_valid_up),
    .valid_down(n621_valid_down),
    .I0(n621_I0),
    .I1(n621_I1),
    .O_t0b(n621_O_t0b),
    .O_t1b(n621_O_t1b)
  );
  AtomTuple_9 n622 ( // @[Top.scala 1811:22]
    .valid_up(n622_valid_up),
    .valid_down(n622_valid_down),
    .I0(n622_I0),
    .I1_t0b(n622_I1_t0b),
    .I1_t1b(n622_I1_t1b),
    .O_t0b(n622_O_t0b),
    .O_t1b_t0b(n622_O_t1b_t0b),
    .O_t1b_t1b(n622_O_t1b_t1b)
  );
  If n623 ( // @[Top.scala 1815:22]
    .valid_up(n623_valid_up),
    .valid_down(n623_valid_down),
    .I_t0b(n623_I_t0b),
    .I_t1b_t0b(n623_I_t1b_t0b),
    .I_t1b_t1b(n623_I_t1b_t1b),
    .O(n623_O)
  );
  AtomTuple_1 n624 ( // @[Top.scala 1818:22]
    .valid_up(n624_valid_up),
    .valid_down(n624_valid_down),
    .I0(n624_I0),
    .I1(n624_I1),
    .O_t0b(n624_O_t0b),
    .O_t1b(n624_O_t1b)
  );
  Mul n625 ( // @[Top.scala 1822:22]
    .clock(n625_clock),
    .reset(n625_reset),
    .valid_up(n625_valid_up),
    .valid_down(n625_valid_down),
    .I_t0b(n625_I_t0b),
    .I_t1b(n625_I_t1b),
    .O(n625_O)
  );
  AtomTuple_1 n627 ( // @[Top.scala 1825:22]
    .valid_up(n627_valid_up),
    .valid_down(n627_valid_down),
    .I0(n627_I0),
    .I1(n627_I1),
    .O_t0b(n627_O_t0b),
    .O_t1b(n627_O_t1b)
  );
  Lt n628 ( // @[Top.scala 1829:22]
    .valid_up(n628_valid_up),
    .valid_down(n628_valid_down),
    .I_t0b(n628_I_t0b),
    .I_t1b(n628_I_t1b),
    .O(n628_O)
  );
  InitialDelayCounter_48 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n630 ( // @[Top.scala 1833:22]
    .valid_up(n630_valid_up),
    .valid_down(n630_valid_down),
    .I0(n630_I0),
    .I1(n630_I1),
    .O_t0b(n630_O_t0b),
    .O_t1b(n630_O_t1b)
  );
  Sub n631 ( // @[Top.scala 1837:22]
    .valid_up(n631_valid_up),
    .valid_down(n631_valid_down),
    .I_t0b(n631_I_t0b),
    .I_t1b(n631_I_t1b),
    .O(n631_O)
  );
  AtomTuple_1 n632 ( // @[Top.scala 1840:22]
    .valid_up(n632_valid_up),
    .valid_down(n632_valid_down),
    .I0(n632_I0),
    .I1(n632_I1),
    .O_t0b(n632_O_t0b),
    .O_t1b(n632_O_t1b)
  );
  AtomTuple_1 n633 ( // @[Top.scala 1844:22]
    .valid_up(n633_valid_up),
    .valid_down(n633_valid_down),
    .I0(n633_I0),
    .I1(n633_I1),
    .O_t0b(n633_O_t0b),
    .O_t1b(n633_O_t1b)
  );
  AtomTuple_15 n634 ( // @[Top.scala 1848:22]
    .valid_up(n634_valid_up),
    .valid_down(n634_valid_down),
    .I0_t0b(n634_I0_t0b),
    .I0_t1b(n634_I0_t1b),
    .I1_t0b(n634_I1_t0b),
    .I1_t1b(n634_I1_t1b),
    .O_t0b_t0b(n634_O_t0b_t0b),
    .O_t0b_t1b(n634_O_t0b_t1b),
    .O_t1b_t0b(n634_O_t1b_t0b),
    .O_t1b_t1b(n634_O_t1b_t1b)
  );
  FIFO_3 n635 ( // @[Top.scala 1852:22]
    .clock(n635_clock),
    .reset(n635_reset),
    .valid_up(n635_valid_up),
    .valid_down(n635_valid_down),
    .I_t0b_t0b(n635_I_t0b_t0b),
    .I_t0b_t1b(n635_I_t0b_t1b),
    .I_t1b_t0b(n635_I_t1b_t0b),
    .I_t1b_t1b(n635_I_t1b_t1b),
    .O_t0b_t0b(n635_O_t0b_t0b),
    .O_t0b_t1b(n635_O_t0b_t1b),
    .O_t1b_t0b(n635_O_t1b_t0b),
    .O_t1b_t1b(n635_O_t1b_t1b)
  );
  AtomTuple_16 n636 ( // @[Top.scala 1855:22]
    .valid_up(n636_valid_up),
    .valid_down(n636_valid_down),
    .I0(n636_I0),
    .I1_t0b_t0b(n636_I1_t0b_t0b),
    .I1_t0b_t1b(n636_I1_t0b_t1b),
    .I1_t1b_t0b(n636_I1_t1b_t0b),
    .I1_t1b_t1b(n636_I1_t1b_t1b),
    .O_t0b(n636_O_t0b),
    .O_t1b_t0b_t0b(n636_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n636_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n636_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n636_O_t1b_t1b_t1b)
  );
  If_1 n637 ( // @[Top.scala 1859:22]
    .valid_up(n637_valid_up),
    .valid_down(n637_valid_down),
    .I_t0b(n637_I_t0b),
    .I_t1b_t0b_t0b(n637_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n637_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n637_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n637_I_t1b_t1b_t1b),
    .O_t0b(n637_O_t0b),
    .O_t1b(n637_O_t1b)
  );
  AtomTuple_3 n639 ( // @[Top.scala 1862:22]
    .valid_up(n639_valid_up),
    .valid_down(n639_valid_down),
    .I0(n639_I0),
    .I1_t0b(n639_I1_t0b),
    .I1_t1b(n639_I1_t1b),
    .O_t0b(n639_O_t0b),
    .O_t1b_t0b(n639_O_t1b_t0b),
    .O_t1b_t1b(n639_O_t1b_t1b)
  );
  assign valid_down = n639_valid_down; // @[Top.scala 1867:16]
  assign O_t1b_t0b = n639_O_t1b_t0b; // @[Top.scala 1866:7]
  assign O_t1b_t1b = n639_O_t1b_t1b; // @[Top.scala 1866:7]
  assign n605_valid_up = valid_up; // @[Top.scala 1761:19]
  assign n605_I_t0b = I_t0b; // @[Top.scala 1760:12]
  assign n638_clock = clock;
  assign n638_reset = reset;
  assign n638_valid_up = n605_valid_down; // @[Top.scala 1764:19]
  assign n638_I = n605_O; // @[Top.scala 1763:12]
  assign n626_clock = clock;
  assign n626_reset = reset;
  assign n626_valid_up = n605_valid_down; // @[Top.scala 1767:19]
  assign n626_I = n605_O; // @[Top.scala 1766:12]
  assign n606_valid_up = valid_up; // @[Top.scala 1770:19]
  assign n606_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1769:12]
  assign n606_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1769:12]
  assign n607_valid_up = n606_valid_down; // @[Top.scala 1773:19]
  assign n607_I_t0b = n606_O_t0b; // @[Top.scala 1772:12]
  assign n608_valid_up = n606_valid_down; // @[Top.scala 1776:19]
  assign n608_I_t1b = n606_O_t1b; // @[Top.scala 1775:12]
  assign n609_valid_up = n607_valid_down & n608_valid_down; // @[Top.scala 1780:19]
  assign n609_I0 = n607_O; // @[Top.scala 1778:13]
  assign n609_I1 = n608_O; // @[Top.scala 1779:13]
  assign n610_valid_up = n609_valid_down; // @[Top.scala 1783:19]
  assign n610_I_t0b = n609_O_t0b; // @[Top.scala 1782:12]
  assign n610_I_t1b = n609_O_t1b; // @[Top.scala 1782:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n613_valid_up = n610_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 1788:19]
  assign n613_I0 = n610_O; // @[Top.scala 1786:13]
  assign n614_valid_up = n613_valid_down; // @[Top.scala 1791:19]
  assign n614_I_t0b = n613_O_t0b; // @[Top.scala 1790:12]
  assign n615_valid_up = n614_valid_down & n607_valid_down; // @[Top.scala 1795:19]
  assign n615_I0 = n614_O; // @[Top.scala 1793:13]
  assign n615_I1 = n607_O; // @[Top.scala 1794:13]
  assign n616_valid_up = n615_valid_down; // @[Top.scala 1798:19]
  assign n616_I_t0b = n615_O_t0b; // @[Top.scala 1797:12]
  assign n616_I_t1b = n615_O_t1b; // @[Top.scala 1797:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n619_valid_up = n614_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1803:19]
  assign n619_I0 = n614_O; // @[Top.scala 1801:13]
  assign n619_I1 = 16'h1; // @[Top.scala 1802:13]
  assign n620_valid_up = n619_valid_down; // @[Top.scala 1806:19]
  assign n620_I_t0b = n619_O_t0b; // @[Top.scala 1805:12]
  assign n620_I_t1b = n619_O_t1b; // @[Top.scala 1805:12]
  assign n621_valid_up = n620_valid_down & n614_valid_down; // @[Top.scala 1810:19]
  assign n621_I0 = n620_O; // @[Top.scala 1808:13]
  assign n621_I1 = n614_O; // @[Top.scala 1809:13]
  assign n622_valid_up = n616_valid_down & n621_valid_down; // @[Top.scala 1814:19]
  assign n622_I0 = n616_O[0]; // @[Top.scala 1812:13]
  assign n622_I1_t0b = n621_O_t0b; // @[Top.scala 1813:13]
  assign n622_I1_t1b = n621_O_t1b; // @[Top.scala 1813:13]
  assign n623_valid_up = n622_valid_down; // @[Top.scala 1817:19]
  assign n623_I_t0b = n622_O_t0b; // @[Top.scala 1816:12]
  assign n623_I_t1b_t0b = n622_O_t1b_t0b; // @[Top.scala 1816:12]
  assign n623_I_t1b_t1b = n622_O_t1b_t1b; // @[Top.scala 1816:12]
  assign n624_valid_up = n623_valid_down; // @[Top.scala 1821:19]
  assign n624_I0 = n623_O; // @[Top.scala 1819:13]
  assign n624_I1 = n623_O; // @[Top.scala 1820:13]
  assign n625_clock = clock;
  assign n625_reset = reset;
  assign n625_valid_up = n624_valid_down; // @[Top.scala 1824:19]
  assign n625_I_t0b = n624_O_t0b; // @[Top.scala 1823:12]
  assign n625_I_t1b = n624_O_t1b; // @[Top.scala 1823:12]
  assign n627_valid_up = n626_valid_down & n625_valid_down; // @[Top.scala 1828:19]
  assign n627_I0 = n626_O; // @[Top.scala 1826:13]
  assign n627_I1 = n625_O; // @[Top.scala 1827:13]
  assign n628_valid_up = n627_valid_down; // @[Top.scala 1831:19]
  assign n628_I_t0b = n627_O_t0b; // @[Top.scala 1830:12]
  assign n628_I_t1b = n627_O_t1b; // @[Top.scala 1830:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n630_valid_up = n623_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1836:19]
  assign n630_I0 = n623_O; // @[Top.scala 1834:13]
  assign n630_I1 = 16'h1; // @[Top.scala 1835:13]
  assign n631_valid_up = n630_valid_down; // @[Top.scala 1839:19]
  assign n631_I_t0b = n630_O_t0b; // @[Top.scala 1838:12]
  assign n631_I_t1b = n630_O_t1b; // @[Top.scala 1838:12]
  assign n632_valid_up = n607_valid_down & n631_valid_down; // @[Top.scala 1843:19]
  assign n632_I0 = n607_O; // @[Top.scala 1841:13]
  assign n632_I1 = n631_O; // @[Top.scala 1842:13]
  assign n633_valid_up = n623_valid_down & n608_valid_down; // @[Top.scala 1847:19]
  assign n633_I0 = n623_O; // @[Top.scala 1845:13]
  assign n633_I1 = n608_O; // @[Top.scala 1846:13]
  assign n634_valid_up = n632_valid_down & n633_valid_down; // @[Top.scala 1851:19]
  assign n634_I0_t0b = n632_O_t0b; // @[Top.scala 1849:13]
  assign n634_I0_t1b = n632_O_t1b; // @[Top.scala 1849:13]
  assign n634_I1_t0b = n633_O_t0b; // @[Top.scala 1850:13]
  assign n634_I1_t1b = n633_O_t1b; // @[Top.scala 1850:13]
  assign n635_clock = clock;
  assign n635_reset = reset;
  assign n635_valid_up = n634_valid_down; // @[Top.scala 1854:19]
  assign n635_I_t0b_t0b = n634_O_t0b_t0b; // @[Top.scala 1853:12]
  assign n635_I_t0b_t1b = n634_O_t0b_t1b; // @[Top.scala 1853:12]
  assign n635_I_t1b_t0b = n634_O_t1b_t0b; // @[Top.scala 1853:12]
  assign n635_I_t1b_t1b = n634_O_t1b_t1b; // @[Top.scala 1853:12]
  assign n636_valid_up = n628_valid_down & n635_valid_down; // @[Top.scala 1858:19]
  assign n636_I0 = n628_O[0]; // @[Top.scala 1856:13]
  assign n636_I1_t0b_t0b = n635_O_t0b_t0b; // @[Top.scala 1857:13]
  assign n636_I1_t0b_t1b = n635_O_t0b_t1b; // @[Top.scala 1857:13]
  assign n636_I1_t1b_t0b = n635_O_t1b_t0b; // @[Top.scala 1857:13]
  assign n636_I1_t1b_t1b = n635_O_t1b_t1b; // @[Top.scala 1857:13]
  assign n637_valid_up = n636_valid_down; // @[Top.scala 1861:19]
  assign n637_I_t0b = n636_O_t0b; // @[Top.scala 1860:12]
  assign n637_I_t1b_t0b_t0b = n636_O_t1b_t0b_t0b; // @[Top.scala 1860:12]
  assign n637_I_t1b_t0b_t1b = n636_O_t1b_t0b_t1b; // @[Top.scala 1860:12]
  assign n637_I_t1b_t1b_t0b = n636_O_t1b_t1b_t0b; // @[Top.scala 1860:12]
  assign n637_I_t1b_t1b_t1b = n636_O_t1b_t1b_t1b; // @[Top.scala 1860:12]
  assign n639_valid_up = n638_valid_down & n637_valid_down; // @[Top.scala 1865:19]
  assign n639_I0 = n638_O; // @[Top.scala 1863:13]
  assign n639_I1_t0b = n637_O_t0b; // @[Top.scala 1864:13]
  assign n639_I1_t1b = n637_O_t1b; // @[Top.scala 1864:13]
endmodule
module MapS_16(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O_t1b_t1b; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O_t1b_t1b; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_16 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  Module_16 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O_t1b_t0b(other_ops_0_O_t1b_t0b),
    .O_t1b_t1b(other_ops_0_O_t1b_t1b)
  );
  Module_16 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O_t1b_t0b(other_ops_1_O_t1b_t0b),
    .O_t1b_t1b(other_ops_1_O_t1b_t1b)
  );
  Module_16 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t0b(other_ops_2_I_t0b),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O_t1b_t0b(other_ops_2_O_t1b_t0b),
    .O_t1b_t1b(other_ops_2_O_t1b_t1b)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[MapS.scala 17:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[MapS.scala 17:8]
  assign O_1_t1b_t0b = other_ops_0_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_1_t1b_t1b = other_ops_0_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_2_t1b_t0b = other_ops_1_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_2_t1b_t1b = other_ops_1_O_t1b_t1b; // @[MapS.scala 21:12]
  assign O_3_t1b_t0b = other_ops_2_O_t1b_t0b; // @[MapS.scala 21:12]
  assign O_3_t1b_t1b = other_ops_2_O_t1b_t1b; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t0b = I_3_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_16(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t0b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t0b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t0b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0_t1b_t0b,
  output [15:0] O_0_t1b_t1b,
  output [15:0] O_1_t1b_t0b,
  output [15:0] O_1_t1b_t1b,
  output [15:0] O_2_t1b_t0b,
  output [15:0] O_2_t1b_t1b,
  output [15:0] O_3_t1b_t0b,
  output [15:0] O_3_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_3_t1b_t1b; // @[MapT.scala 8:20]
  MapS_16 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t0b(op_I_3_t0b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b),
    .O_1_t1b_t0b(op_O_1_t1b_t0b),
    .O_1_t1b_t1b(op_O_1_t1b_t1b),
    .O_2_t1b_t0b(op_O_2_t1b_t0b),
    .O_2_t1b_t1b(op_O_2_t1b_t1b),
    .O_3_t1b_t0b(op_O_3_t1b_t0b),
    .O_3_t1b_t1b(op_O_3_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign O_1_t1b_t0b = op_O_1_t1b_t0b; // @[MapT.scala 15:7]
  assign O_1_t1b_t1b = op_O_1_t1b_t1b; // @[MapT.scala 15:7]
  assign O_2_t1b_t0b = op_O_2_t1b_t0b; // @[MapT.scala 15:7]
  assign O_2_t1b_t1b = op_O_2_t1b_t1b; // @[MapT.scala 15:7]
  assign O_3_t1b_t0b = op_O_3_t1b_t0b; // @[MapT.scala 15:7]
  assign O_3_t1b_t1b = op_O_3_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t0b = I_3_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module Module_17(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O
);
  wire  n644_valid_up; // @[Top.scala 1873:22]
  wire  n644_valid_down; // @[Top.scala 1873:22]
  wire [15:0] n644_I_t1b_t0b; // @[Top.scala 1873:22]
  wire [15:0] n644_I_t1b_t1b; // @[Top.scala 1873:22]
  wire [15:0] n644_O_t0b; // @[Top.scala 1873:22]
  wire [15:0] n644_O_t1b; // @[Top.scala 1873:22]
  wire  n645_valid_up; // @[Top.scala 1876:22]
  wire  n645_valid_down; // @[Top.scala 1876:22]
  wire [15:0] n645_I_t0b; // @[Top.scala 1876:22]
  wire [15:0] n645_O; // @[Top.scala 1876:22]
  Snd n644 ( // @[Top.scala 1873:22]
    .valid_up(n644_valid_up),
    .valid_down(n644_valid_down),
    .I_t1b_t0b(n644_I_t1b_t0b),
    .I_t1b_t1b(n644_I_t1b_t1b),
    .O_t0b(n644_O_t0b),
    .O_t1b(n644_O_t1b)
  );
  Fst_1 n645 ( // @[Top.scala 1876:22]
    .valid_up(n645_valid_up),
    .valid_down(n645_valid_down),
    .I_t0b(n645_I_t0b),
    .O(n645_O)
  );
  assign valid_down = n645_valid_down; // @[Top.scala 1880:16]
  assign O = n645_O; // @[Top.scala 1879:7]
  assign n644_valid_up = valid_up; // @[Top.scala 1875:19]
  assign n644_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1874:12]
  assign n644_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1874:12]
  assign n645_valid_up = n644_valid_down; // @[Top.scala 1878:19]
  assign n645_I_t0b = n644_O_t0b; // @[Top.scala 1877:12]
endmodule
module MapS_17(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3
);
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t0b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_I_t1b_t1b; // @[MapS.scala 9:22]
  wire [15:0] fst_op_O; // @[MapS.scala 9:22]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_0_O; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_1_O; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t0b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_I_t1b_t1b; // @[MapS.scala 10:86]
  wire [15:0] other_ops_2_O; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_17 fst_op ( // @[MapS.scala 9:22]
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t1b_t0b(fst_op_I_t1b_t0b),
    .I_t1b_t1b(fst_op_I_t1b_t1b),
    .O(fst_op_O)
  );
  Module_17 other_ops_0 ( // @[MapS.scala 10:86]
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t1b_t0b(other_ops_0_I_t1b_t0b),
    .I_t1b_t1b(other_ops_0_I_t1b_t1b),
    .O(other_ops_0_O)
  );
  Module_17 other_ops_1 ( // @[MapS.scala 10:86]
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t1b_t0b(other_ops_1_I_t1b_t0b),
    .I_t1b_t1b(other_ops_1_I_t1b_t1b),
    .O(other_ops_1_O)
  );
  Module_17 other_ops_2 ( // @[MapS.scala 10:86]
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_t1b_t0b(other_ops_2_I_t1b_t0b),
    .I_t1b_t1b(other_ops_2_I_t1b_t1b),
    .O(other_ops_2_O)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0 = fst_op_O; // @[MapS.scala 17:8]
  assign O_1 = other_ops_0_O; // @[MapS.scala 21:12]
  assign O_2 = other_ops_1_O; // @[MapS.scala 21:12]
  assign O_3 = other_ops_2_O; // @[MapS.scala 21:12]
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t1b_t0b = I_0_t1b_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b_t1b = I_0_t1b_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t1b_t0b = I_1_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b_t1b = I_1_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t1b_t0b = I_2_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b_t1b = I_2_t1b_t1b; // @[MapS.scala 20:41]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_t1b_t0b = I_3_t1b_t0b; // @[MapS.scala 20:41]
  assign other_ops_2_I_t1b_t1b = I_3_t1b_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_17(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0_t1b_t0b,
  input  [15:0] I_0_t1b_t1b,
  input  [15:0] I_1_t1b_t0b,
  input  [15:0] I_1_t1b_t1b,
  input  [15:0] I_2_t1b_t0b,
  input  [15:0] I_2_t1b_t1b,
  input  [15:0] I_3_t1b_t0b,
  input  [15:0] I_3_t1b_t1b,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_0_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_1_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_2_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_3_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_0; // @[MapT.scala 8:20]
  wire [15:0] op_O_1; // @[MapT.scala 8:20]
  wire [15:0] op_O_2; // @[MapT.scala 8:20]
  wire [15:0] op_O_3; // @[MapT.scala 8:20]
  MapS_17 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t1b_t0b(op_I_0_t1b_t0b),
    .I_0_t1b_t1b(op_I_0_t1b_t1b),
    .I_1_t1b_t0b(op_I_1_t1b_t0b),
    .I_1_t1b_t1b(op_I_1_t1b_t1b),
    .I_2_t1b_t0b(op_I_2_t1b_t0b),
    .I_2_t1b_t1b(op_I_2_t1b_t1b),
    .I_3_t1b_t0b(op_I_3_t1b_t0b),
    .I_3_t1b_t1b(op_I_3_t1b_t1b),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2),
    .O_3(op_O_3)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0 = op_O_0; // @[MapT.scala 15:7]
  assign O_1 = op_O_1; // @[MapT.scala 15:7]
  assign O_2 = op_O_2; // @[MapT.scala 15:7]
  assign O_3 = op_O_3; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t1b_t0b = I_0_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b_t1b = I_0_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t0b = I_1_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b_t1b = I_1_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t0b = I_2_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b_t1b = I_2_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t0b = I_3_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_3_t1b_t1b = I_3_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module Top(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  input  [15:0] I_1,
  input  [15:0] I_2,
  input  [15:0] I_3,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3
);
  wire  n1_clock; // @[Top.scala 1886:20]
  wire  n1_reset; // @[Top.scala 1886:20]
  wire  n1_valid_up; // @[Top.scala 1886:20]
  wire  n1_valid_down; // @[Top.scala 1886:20]
  wire [15:0] n1_I_0; // @[Top.scala 1886:20]
  wire [15:0] n1_I_1; // @[Top.scala 1886:20]
  wire [15:0] n1_I_2; // @[Top.scala 1886:20]
  wire [15:0] n1_I_3; // @[Top.scala 1886:20]
  wire [15:0] n1_O_0; // @[Top.scala 1886:20]
  wire [15:0] n1_O_1; // @[Top.scala 1886:20]
  wire [15:0] n1_O_2; // @[Top.scala 1886:20]
  wire [15:0] n1_O_3; // @[Top.scala 1886:20]
  wire  n17_clock; // @[Top.scala 1889:21]
  wire  n17_reset; // @[Top.scala 1889:21]
  wire  n17_valid_up; // @[Top.scala 1889:21]
  wire  n17_valid_down; // @[Top.scala 1889:21]
  wire [15:0] n17_I_0; // @[Top.scala 1889:21]
  wire [15:0] n17_I_1; // @[Top.scala 1889:21]
  wire [15:0] n17_I_2; // @[Top.scala 1889:21]
  wire [15:0] n17_I_3; // @[Top.scala 1889:21]
  wire [15:0] n17_O_0_t0b; // @[Top.scala 1889:21]
  wire [15:0] n17_O_0_t1b_t0b; // @[Top.scala 1889:21]
  wire [15:0] n17_O_0_t1b_t1b; // @[Top.scala 1889:21]
  wire [15:0] n17_O_1_t0b; // @[Top.scala 1889:21]
  wire [15:0] n17_O_1_t1b_t0b; // @[Top.scala 1889:21]
  wire [15:0] n17_O_1_t1b_t1b; // @[Top.scala 1889:21]
  wire [15:0] n17_O_2_t0b; // @[Top.scala 1889:21]
  wire [15:0] n17_O_2_t1b_t0b; // @[Top.scala 1889:21]
  wire [15:0] n17_O_2_t1b_t1b; // @[Top.scala 1889:21]
  wire [15:0] n17_O_3_t0b; // @[Top.scala 1889:21]
  wire [15:0] n17_O_3_t1b_t0b; // @[Top.scala 1889:21]
  wire [15:0] n17_O_3_t1b_t1b; // @[Top.scala 1889:21]
  wire  n56_clock; // @[Top.scala 1892:21]
  wire  n56_reset; // @[Top.scala 1892:21]
  wire  n56_valid_up; // @[Top.scala 1892:21]
  wire  n56_valid_down; // @[Top.scala 1892:21]
  wire [15:0] n56_I_0_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_I_0_t1b_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_I_0_t1b_t1b; // @[Top.scala 1892:21]
  wire [15:0] n56_I_1_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_I_1_t1b_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_I_1_t1b_t1b; // @[Top.scala 1892:21]
  wire [15:0] n56_I_2_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_I_2_t1b_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_I_2_t1b_t1b; // @[Top.scala 1892:21]
  wire [15:0] n56_I_3_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_I_3_t1b_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_I_3_t1b_t1b; // @[Top.scala 1892:21]
  wire [15:0] n56_O_0_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_O_0_t1b_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_O_0_t1b_t1b; // @[Top.scala 1892:21]
  wire [15:0] n56_O_1_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_O_1_t1b_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_O_1_t1b_t1b; // @[Top.scala 1892:21]
  wire [15:0] n56_O_2_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_O_2_t1b_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_O_2_t1b_t1b; // @[Top.scala 1892:21]
  wire [15:0] n56_O_3_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_O_3_t1b_t0b; // @[Top.scala 1892:21]
  wire [15:0] n56_O_3_t1b_t1b; // @[Top.scala 1892:21]
  wire  n95_clock; // @[Top.scala 1895:21]
  wire  n95_reset; // @[Top.scala 1895:21]
  wire  n95_valid_up; // @[Top.scala 1895:21]
  wire  n95_valid_down; // @[Top.scala 1895:21]
  wire [15:0] n95_I_0_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_I_0_t1b_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_I_0_t1b_t1b; // @[Top.scala 1895:21]
  wire [15:0] n95_I_1_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_I_1_t1b_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_I_1_t1b_t1b; // @[Top.scala 1895:21]
  wire [15:0] n95_I_2_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_I_2_t1b_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_I_2_t1b_t1b; // @[Top.scala 1895:21]
  wire [15:0] n95_I_3_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_I_3_t1b_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_I_3_t1b_t1b; // @[Top.scala 1895:21]
  wire [15:0] n95_O_0_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_O_0_t1b_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_O_0_t1b_t1b; // @[Top.scala 1895:21]
  wire [15:0] n95_O_1_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_O_1_t1b_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_O_1_t1b_t1b; // @[Top.scala 1895:21]
  wire [15:0] n95_O_2_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_O_2_t1b_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_O_2_t1b_t1b; // @[Top.scala 1895:21]
  wire [15:0] n95_O_3_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_O_3_t1b_t0b; // @[Top.scala 1895:21]
  wire [15:0] n95_O_3_t1b_t1b; // @[Top.scala 1895:21]
  wire  n134_clock; // @[Top.scala 1898:22]
  wire  n134_reset; // @[Top.scala 1898:22]
  wire  n134_valid_up; // @[Top.scala 1898:22]
  wire  n134_valid_down; // @[Top.scala 1898:22]
  wire [15:0] n134_I_0_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_I_0_t1b_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_I_0_t1b_t1b; // @[Top.scala 1898:22]
  wire [15:0] n134_I_1_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_I_1_t1b_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_I_1_t1b_t1b; // @[Top.scala 1898:22]
  wire [15:0] n134_I_2_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_I_2_t1b_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_I_2_t1b_t1b; // @[Top.scala 1898:22]
  wire [15:0] n134_I_3_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_I_3_t1b_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_I_3_t1b_t1b; // @[Top.scala 1898:22]
  wire [15:0] n134_O_0_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_O_0_t1b_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_O_0_t1b_t1b; // @[Top.scala 1898:22]
  wire [15:0] n134_O_1_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_O_1_t1b_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_O_1_t1b_t1b; // @[Top.scala 1898:22]
  wire [15:0] n134_O_2_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_O_2_t1b_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_O_2_t1b_t1b; // @[Top.scala 1898:22]
  wire [15:0] n134_O_3_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_O_3_t1b_t0b; // @[Top.scala 1898:22]
  wire [15:0] n134_O_3_t1b_t1b; // @[Top.scala 1898:22]
  wire  n173_clock; // @[Top.scala 1901:22]
  wire  n173_reset; // @[Top.scala 1901:22]
  wire  n173_valid_up; // @[Top.scala 1901:22]
  wire  n173_valid_down; // @[Top.scala 1901:22]
  wire [15:0] n173_I_0_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_I_0_t1b_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_I_0_t1b_t1b; // @[Top.scala 1901:22]
  wire [15:0] n173_I_1_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_I_1_t1b_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_I_1_t1b_t1b; // @[Top.scala 1901:22]
  wire [15:0] n173_I_2_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_I_2_t1b_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_I_2_t1b_t1b; // @[Top.scala 1901:22]
  wire [15:0] n173_I_3_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_I_3_t1b_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_I_3_t1b_t1b; // @[Top.scala 1901:22]
  wire [15:0] n173_O_0_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_O_0_t1b_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_O_0_t1b_t1b; // @[Top.scala 1901:22]
  wire [15:0] n173_O_1_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_O_1_t1b_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_O_1_t1b_t1b; // @[Top.scala 1901:22]
  wire [15:0] n173_O_2_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_O_2_t1b_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_O_2_t1b_t1b; // @[Top.scala 1901:22]
  wire [15:0] n173_O_3_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_O_3_t1b_t0b; // @[Top.scala 1901:22]
  wire [15:0] n173_O_3_t1b_t1b; // @[Top.scala 1901:22]
  wire  n212_clock; // @[Top.scala 1904:22]
  wire  n212_reset; // @[Top.scala 1904:22]
  wire  n212_valid_up; // @[Top.scala 1904:22]
  wire  n212_valid_down; // @[Top.scala 1904:22]
  wire [15:0] n212_I_0_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_I_0_t1b_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_I_0_t1b_t1b; // @[Top.scala 1904:22]
  wire [15:0] n212_I_1_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_I_1_t1b_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_I_1_t1b_t1b; // @[Top.scala 1904:22]
  wire [15:0] n212_I_2_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_I_2_t1b_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_I_2_t1b_t1b; // @[Top.scala 1904:22]
  wire [15:0] n212_I_3_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_I_3_t1b_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_I_3_t1b_t1b; // @[Top.scala 1904:22]
  wire [15:0] n212_O_0_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_O_0_t1b_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_O_0_t1b_t1b; // @[Top.scala 1904:22]
  wire [15:0] n212_O_1_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_O_1_t1b_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_O_1_t1b_t1b; // @[Top.scala 1904:22]
  wire [15:0] n212_O_2_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_O_2_t1b_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_O_2_t1b_t1b; // @[Top.scala 1904:22]
  wire [15:0] n212_O_3_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_O_3_t1b_t0b; // @[Top.scala 1904:22]
  wire [15:0] n212_O_3_t1b_t1b; // @[Top.scala 1904:22]
  wire  n251_clock; // @[Top.scala 1907:22]
  wire  n251_reset; // @[Top.scala 1907:22]
  wire  n251_valid_up; // @[Top.scala 1907:22]
  wire  n251_valid_down; // @[Top.scala 1907:22]
  wire [15:0] n251_I_0_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_I_0_t1b_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_I_0_t1b_t1b; // @[Top.scala 1907:22]
  wire [15:0] n251_I_1_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_I_1_t1b_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_I_1_t1b_t1b; // @[Top.scala 1907:22]
  wire [15:0] n251_I_2_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_I_2_t1b_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_I_2_t1b_t1b; // @[Top.scala 1907:22]
  wire [15:0] n251_I_3_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_I_3_t1b_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_I_3_t1b_t1b; // @[Top.scala 1907:22]
  wire [15:0] n251_O_0_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_O_0_t1b_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_O_0_t1b_t1b; // @[Top.scala 1907:22]
  wire [15:0] n251_O_1_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_O_1_t1b_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_O_1_t1b_t1b; // @[Top.scala 1907:22]
  wire [15:0] n251_O_2_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_O_2_t1b_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_O_2_t1b_t1b; // @[Top.scala 1907:22]
  wire [15:0] n251_O_3_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_O_3_t1b_t0b; // @[Top.scala 1907:22]
  wire [15:0] n251_O_3_t1b_t1b; // @[Top.scala 1907:22]
  wire  n290_clock; // @[Top.scala 1910:22]
  wire  n290_reset; // @[Top.scala 1910:22]
  wire  n290_valid_up; // @[Top.scala 1910:22]
  wire  n290_valid_down; // @[Top.scala 1910:22]
  wire [15:0] n290_I_0_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_I_0_t1b_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_I_0_t1b_t1b; // @[Top.scala 1910:22]
  wire [15:0] n290_I_1_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_I_1_t1b_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_I_1_t1b_t1b; // @[Top.scala 1910:22]
  wire [15:0] n290_I_2_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_I_2_t1b_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_I_2_t1b_t1b; // @[Top.scala 1910:22]
  wire [15:0] n290_I_3_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_I_3_t1b_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_I_3_t1b_t1b; // @[Top.scala 1910:22]
  wire [15:0] n290_O_0_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_O_0_t1b_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_O_0_t1b_t1b; // @[Top.scala 1910:22]
  wire [15:0] n290_O_1_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_O_1_t1b_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_O_1_t1b_t1b; // @[Top.scala 1910:22]
  wire [15:0] n290_O_2_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_O_2_t1b_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_O_2_t1b_t1b; // @[Top.scala 1910:22]
  wire [15:0] n290_O_3_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_O_3_t1b_t0b; // @[Top.scala 1910:22]
  wire [15:0] n290_O_3_t1b_t1b; // @[Top.scala 1910:22]
  wire  n329_clock; // @[Top.scala 1913:22]
  wire  n329_reset; // @[Top.scala 1913:22]
  wire  n329_valid_up; // @[Top.scala 1913:22]
  wire  n329_valid_down; // @[Top.scala 1913:22]
  wire [15:0] n329_I_0_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_I_0_t1b_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_I_0_t1b_t1b; // @[Top.scala 1913:22]
  wire [15:0] n329_I_1_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_I_1_t1b_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_I_1_t1b_t1b; // @[Top.scala 1913:22]
  wire [15:0] n329_I_2_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_I_2_t1b_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_I_2_t1b_t1b; // @[Top.scala 1913:22]
  wire [15:0] n329_I_3_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_I_3_t1b_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_I_3_t1b_t1b; // @[Top.scala 1913:22]
  wire [15:0] n329_O_0_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_O_0_t1b_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_O_0_t1b_t1b; // @[Top.scala 1913:22]
  wire [15:0] n329_O_1_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_O_1_t1b_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_O_1_t1b_t1b; // @[Top.scala 1913:22]
  wire [15:0] n329_O_2_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_O_2_t1b_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_O_2_t1b_t1b; // @[Top.scala 1913:22]
  wire [15:0] n329_O_3_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_O_3_t1b_t0b; // @[Top.scala 1913:22]
  wire [15:0] n329_O_3_t1b_t1b; // @[Top.scala 1913:22]
  wire  n368_clock; // @[Top.scala 1916:22]
  wire  n368_reset; // @[Top.scala 1916:22]
  wire  n368_valid_up; // @[Top.scala 1916:22]
  wire  n368_valid_down; // @[Top.scala 1916:22]
  wire [15:0] n368_I_0_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_I_0_t1b_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_I_0_t1b_t1b; // @[Top.scala 1916:22]
  wire [15:0] n368_I_1_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_I_1_t1b_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_I_1_t1b_t1b; // @[Top.scala 1916:22]
  wire [15:0] n368_I_2_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_I_2_t1b_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_I_2_t1b_t1b; // @[Top.scala 1916:22]
  wire [15:0] n368_I_3_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_I_3_t1b_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_I_3_t1b_t1b; // @[Top.scala 1916:22]
  wire [15:0] n368_O_0_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_O_0_t1b_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_O_0_t1b_t1b; // @[Top.scala 1916:22]
  wire [15:0] n368_O_1_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_O_1_t1b_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_O_1_t1b_t1b; // @[Top.scala 1916:22]
  wire [15:0] n368_O_2_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_O_2_t1b_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_O_2_t1b_t1b; // @[Top.scala 1916:22]
  wire [15:0] n368_O_3_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_O_3_t1b_t0b; // @[Top.scala 1916:22]
  wire [15:0] n368_O_3_t1b_t1b; // @[Top.scala 1916:22]
  wire  n407_clock; // @[Top.scala 1919:22]
  wire  n407_reset; // @[Top.scala 1919:22]
  wire  n407_valid_up; // @[Top.scala 1919:22]
  wire  n407_valid_down; // @[Top.scala 1919:22]
  wire [15:0] n407_I_0_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_I_0_t1b_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_I_0_t1b_t1b; // @[Top.scala 1919:22]
  wire [15:0] n407_I_1_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_I_1_t1b_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_I_1_t1b_t1b; // @[Top.scala 1919:22]
  wire [15:0] n407_I_2_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_I_2_t1b_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_I_2_t1b_t1b; // @[Top.scala 1919:22]
  wire [15:0] n407_I_3_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_I_3_t1b_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_I_3_t1b_t1b; // @[Top.scala 1919:22]
  wire [15:0] n407_O_0_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_O_0_t1b_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_O_0_t1b_t1b; // @[Top.scala 1919:22]
  wire [15:0] n407_O_1_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_O_1_t1b_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_O_1_t1b_t1b; // @[Top.scala 1919:22]
  wire [15:0] n407_O_2_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_O_2_t1b_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_O_2_t1b_t1b; // @[Top.scala 1919:22]
  wire [15:0] n407_O_3_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_O_3_t1b_t0b; // @[Top.scala 1919:22]
  wire [15:0] n407_O_3_t1b_t1b; // @[Top.scala 1919:22]
  wire  n446_clock; // @[Top.scala 1922:22]
  wire  n446_reset; // @[Top.scala 1922:22]
  wire  n446_valid_up; // @[Top.scala 1922:22]
  wire  n446_valid_down; // @[Top.scala 1922:22]
  wire [15:0] n446_I_0_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_I_0_t1b_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_I_0_t1b_t1b; // @[Top.scala 1922:22]
  wire [15:0] n446_I_1_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_I_1_t1b_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_I_1_t1b_t1b; // @[Top.scala 1922:22]
  wire [15:0] n446_I_2_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_I_2_t1b_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_I_2_t1b_t1b; // @[Top.scala 1922:22]
  wire [15:0] n446_I_3_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_I_3_t1b_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_I_3_t1b_t1b; // @[Top.scala 1922:22]
  wire [15:0] n446_O_0_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_O_0_t1b_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_O_0_t1b_t1b; // @[Top.scala 1922:22]
  wire [15:0] n446_O_1_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_O_1_t1b_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_O_1_t1b_t1b; // @[Top.scala 1922:22]
  wire [15:0] n446_O_2_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_O_2_t1b_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_O_2_t1b_t1b; // @[Top.scala 1922:22]
  wire [15:0] n446_O_3_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_O_3_t1b_t0b; // @[Top.scala 1922:22]
  wire [15:0] n446_O_3_t1b_t1b; // @[Top.scala 1922:22]
  wire  n485_clock; // @[Top.scala 1925:22]
  wire  n485_reset; // @[Top.scala 1925:22]
  wire  n485_valid_up; // @[Top.scala 1925:22]
  wire  n485_valid_down; // @[Top.scala 1925:22]
  wire [15:0] n485_I_0_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_I_0_t1b_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_I_0_t1b_t1b; // @[Top.scala 1925:22]
  wire [15:0] n485_I_1_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_I_1_t1b_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_I_1_t1b_t1b; // @[Top.scala 1925:22]
  wire [15:0] n485_I_2_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_I_2_t1b_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_I_2_t1b_t1b; // @[Top.scala 1925:22]
  wire [15:0] n485_I_3_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_I_3_t1b_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_I_3_t1b_t1b; // @[Top.scala 1925:22]
  wire [15:0] n485_O_0_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_O_0_t1b_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_O_0_t1b_t1b; // @[Top.scala 1925:22]
  wire [15:0] n485_O_1_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_O_1_t1b_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_O_1_t1b_t1b; // @[Top.scala 1925:22]
  wire [15:0] n485_O_2_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_O_2_t1b_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_O_2_t1b_t1b; // @[Top.scala 1925:22]
  wire [15:0] n485_O_3_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_O_3_t1b_t0b; // @[Top.scala 1925:22]
  wire [15:0] n485_O_3_t1b_t1b; // @[Top.scala 1925:22]
  wire  n524_clock; // @[Top.scala 1928:22]
  wire  n524_reset; // @[Top.scala 1928:22]
  wire  n524_valid_up; // @[Top.scala 1928:22]
  wire  n524_valid_down; // @[Top.scala 1928:22]
  wire [15:0] n524_I_0_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_I_0_t1b_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_I_0_t1b_t1b; // @[Top.scala 1928:22]
  wire [15:0] n524_I_1_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_I_1_t1b_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_I_1_t1b_t1b; // @[Top.scala 1928:22]
  wire [15:0] n524_I_2_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_I_2_t1b_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_I_2_t1b_t1b; // @[Top.scala 1928:22]
  wire [15:0] n524_I_3_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_I_3_t1b_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_I_3_t1b_t1b; // @[Top.scala 1928:22]
  wire [15:0] n524_O_0_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_O_0_t1b_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_O_0_t1b_t1b; // @[Top.scala 1928:22]
  wire [15:0] n524_O_1_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_O_1_t1b_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_O_1_t1b_t1b; // @[Top.scala 1928:22]
  wire [15:0] n524_O_2_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_O_2_t1b_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_O_2_t1b_t1b; // @[Top.scala 1928:22]
  wire [15:0] n524_O_3_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_O_3_t1b_t0b; // @[Top.scala 1928:22]
  wire [15:0] n524_O_3_t1b_t1b; // @[Top.scala 1928:22]
  wire  n563_clock; // @[Top.scala 1931:22]
  wire  n563_reset; // @[Top.scala 1931:22]
  wire  n563_valid_up; // @[Top.scala 1931:22]
  wire  n563_valid_down; // @[Top.scala 1931:22]
  wire [15:0] n563_I_0_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_I_0_t1b_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_I_0_t1b_t1b; // @[Top.scala 1931:22]
  wire [15:0] n563_I_1_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_I_1_t1b_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_I_1_t1b_t1b; // @[Top.scala 1931:22]
  wire [15:0] n563_I_2_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_I_2_t1b_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_I_2_t1b_t1b; // @[Top.scala 1931:22]
  wire [15:0] n563_I_3_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_I_3_t1b_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_I_3_t1b_t1b; // @[Top.scala 1931:22]
  wire [15:0] n563_O_0_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_O_0_t1b_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_O_0_t1b_t1b; // @[Top.scala 1931:22]
  wire [15:0] n563_O_1_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_O_1_t1b_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_O_1_t1b_t1b; // @[Top.scala 1931:22]
  wire [15:0] n563_O_2_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_O_2_t1b_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_O_2_t1b_t1b; // @[Top.scala 1931:22]
  wire [15:0] n563_O_3_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_O_3_t1b_t0b; // @[Top.scala 1931:22]
  wire [15:0] n563_O_3_t1b_t1b; // @[Top.scala 1931:22]
  wire  n602_clock; // @[Top.scala 1934:22]
  wire  n602_reset; // @[Top.scala 1934:22]
  wire  n602_valid_up; // @[Top.scala 1934:22]
  wire  n602_valid_down; // @[Top.scala 1934:22]
  wire [15:0] n602_I_0_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_I_0_t1b_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_I_0_t1b_t1b; // @[Top.scala 1934:22]
  wire [15:0] n602_I_1_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_I_1_t1b_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_I_1_t1b_t1b; // @[Top.scala 1934:22]
  wire [15:0] n602_I_2_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_I_2_t1b_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_I_2_t1b_t1b; // @[Top.scala 1934:22]
  wire [15:0] n602_I_3_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_I_3_t1b_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_I_3_t1b_t1b; // @[Top.scala 1934:22]
  wire [15:0] n602_O_0_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_O_0_t1b_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_O_0_t1b_t1b; // @[Top.scala 1934:22]
  wire [15:0] n602_O_1_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_O_1_t1b_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_O_1_t1b_t1b; // @[Top.scala 1934:22]
  wire [15:0] n602_O_2_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_O_2_t1b_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_O_2_t1b_t1b; // @[Top.scala 1934:22]
  wire [15:0] n602_O_3_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_O_3_t1b_t0b; // @[Top.scala 1934:22]
  wire [15:0] n602_O_3_t1b_t1b; // @[Top.scala 1934:22]
  wire  n641_clock; // @[Top.scala 1937:22]
  wire  n641_reset; // @[Top.scala 1937:22]
  wire  n641_valid_up; // @[Top.scala 1937:22]
  wire  n641_valid_down; // @[Top.scala 1937:22]
  wire [15:0] n641_I_0_t0b; // @[Top.scala 1937:22]
  wire [15:0] n641_I_0_t1b_t0b; // @[Top.scala 1937:22]
  wire [15:0] n641_I_0_t1b_t1b; // @[Top.scala 1937:22]
  wire [15:0] n641_I_1_t0b; // @[Top.scala 1937:22]
  wire [15:0] n641_I_1_t1b_t0b; // @[Top.scala 1937:22]
  wire [15:0] n641_I_1_t1b_t1b; // @[Top.scala 1937:22]
  wire [15:0] n641_I_2_t0b; // @[Top.scala 1937:22]
  wire [15:0] n641_I_2_t1b_t0b; // @[Top.scala 1937:22]
  wire [15:0] n641_I_2_t1b_t1b; // @[Top.scala 1937:22]
  wire [15:0] n641_I_3_t0b; // @[Top.scala 1937:22]
  wire [15:0] n641_I_3_t1b_t0b; // @[Top.scala 1937:22]
  wire [15:0] n641_I_3_t1b_t1b; // @[Top.scala 1937:22]
  wire [15:0] n641_O_0_t1b_t0b; // @[Top.scala 1937:22]
  wire [15:0] n641_O_0_t1b_t1b; // @[Top.scala 1937:22]
  wire [15:0] n641_O_1_t1b_t0b; // @[Top.scala 1937:22]
  wire [15:0] n641_O_1_t1b_t1b; // @[Top.scala 1937:22]
  wire [15:0] n641_O_2_t1b_t0b; // @[Top.scala 1937:22]
  wire [15:0] n641_O_2_t1b_t1b; // @[Top.scala 1937:22]
  wire [15:0] n641_O_3_t1b_t0b; // @[Top.scala 1937:22]
  wire [15:0] n641_O_3_t1b_t1b; // @[Top.scala 1937:22]
  wire  n647_valid_up; // @[Top.scala 1940:22]
  wire  n647_valid_down; // @[Top.scala 1940:22]
  wire [15:0] n647_I_0_t1b_t0b; // @[Top.scala 1940:22]
  wire [15:0] n647_I_0_t1b_t1b; // @[Top.scala 1940:22]
  wire [15:0] n647_I_1_t1b_t0b; // @[Top.scala 1940:22]
  wire [15:0] n647_I_1_t1b_t1b; // @[Top.scala 1940:22]
  wire [15:0] n647_I_2_t1b_t0b; // @[Top.scala 1940:22]
  wire [15:0] n647_I_2_t1b_t1b; // @[Top.scala 1940:22]
  wire [15:0] n647_I_3_t1b_t0b; // @[Top.scala 1940:22]
  wire [15:0] n647_I_3_t1b_t1b; // @[Top.scala 1940:22]
  wire [15:0] n647_O_0; // @[Top.scala 1940:22]
  wire [15:0] n647_O_1; // @[Top.scala 1940:22]
  wire [15:0] n647_O_2; // @[Top.scala 1940:22]
  wire [15:0] n647_O_3; // @[Top.scala 1940:22]
  wire  n648_clock; // @[Top.scala 1943:22]
  wire  n648_reset; // @[Top.scala 1943:22]
  wire  n648_valid_up; // @[Top.scala 1943:22]
  wire  n648_valid_down; // @[Top.scala 1943:22]
  wire [15:0] n648_I_0; // @[Top.scala 1943:22]
  wire [15:0] n648_I_1; // @[Top.scala 1943:22]
  wire [15:0] n648_I_2; // @[Top.scala 1943:22]
  wire [15:0] n648_I_3; // @[Top.scala 1943:22]
  wire [15:0] n648_O_0; // @[Top.scala 1943:22]
  wire [15:0] n648_O_1; // @[Top.scala 1943:22]
  wire [15:0] n648_O_2; // @[Top.scala 1943:22]
  wire [15:0] n648_O_3; // @[Top.scala 1943:22]
  wire  n649_clock; // @[Top.scala 1946:22]
  wire  n649_reset; // @[Top.scala 1946:22]
  wire  n649_valid_up; // @[Top.scala 1946:22]
  wire  n649_valid_down; // @[Top.scala 1946:22]
  wire [15:0] n649_I_0; // @[Top.scala 1946:22]
  wire [15:0] n649_I_1; // @[Top.scala 1946:22]
  wire [15:0] n649_I_2; // @[Top.scala 1946:22]
  wire [15:0] n649_I_3; // @[Top.scala 1946:22]
  wire [15:0] n649_O_0; // @[Top.scala 1946:22]
  wire [15:0] n649_O_1; // @[Top.scala 1946:22]
  wire [15:0] n649_O_2; // @[Top.scala 1946:22]
  wire [15:0] n649_O_3; // @[Top.scala 1946:22]
  wire  n650_clock; // @[Top.scala 1949:22]
  wire  n650_reset; // @[Top.scala 1949:22]
  wire  n650_valid_up; // @[Top.scala 1949:22]
  wire  n650_valid_down; // @[Top.scala 1949:22]
  wire [15:0] n650_I_0; // @[Top.scala 1949:22]
  wire [15:0] n650_I_1; // @[Top.scala 1949:22]
  wire [15:0] n650_I_2; // @[Top.scala 1949:22]
  wire [15:0] n650_I_3; // @[Top.scala 1949:22]
  wire [15:0] n650_O_0; // @[Top.scala 1949:22]
  wire [15:0] n650_O_1; // @[Top.scala 1949:22]
  wire [15:0] n650_O_2; // @[Top.scala 1949:22]
  wire [15:0] n650_O_3; // @[Top.scala 1949:22]
  FIFO n1 ( // @[Top.scala 1886:20]
    .clock(n1_clock),
    .reset(n1_reset),
    .valid_up(n1_valid_up),
    .valid_down(n1_valid_down),
    .I_0(n1_I_0),
    .I_1(n1_I_1),
    .I_2(n1_I_2),
    .I_3(n1_I_3),
    .O_0(n1_O_0),
    .O_1(n1_O_1),
    .O_2(n1_O_2),
    .O_3(n1_O_3)
  );
  MapT n17 ( // @[Top.scala 1889:21]
    .clock(n17_clock),
    .reset(n17_reset),
    .valid_up(n17_valid_up),
    .valid_down(n17_valid_down),
    .I_0(n17_I_0),
    .I_1(n17_I_1),
    .I_2(n17_I_2),
    .I_3(n17_I_3),
    .O_0_t0b(n17_O_0_t0b),
    .O_0_t1b_t0b(n17_O_0_t1b_t0b),
    .O_0_t1b_t1b(n17_O_0_t1b_t1b),
    .O_1_t0b(n17_O_1_t0b),
    .O_1_t1b_t0b(n17_O_1_t1b_t0b),
    .O_1_t1b_t1b(n17_O_1_t1b_t1b),
    .O_2_t0b(n17_O_2_t0b),
    .O_2_t1b_t0b(n17_O_2_t1b_t0b),
    .O_2_t1b_t1b(n17_O_2_t1b_t1b),
    .O_3_t0b(n17_O_3_t0b),
    .O_3_t1b_t0b(n17_O_3_t1b_t0b),
    .O_3_t1b_t1b(n17_O_3_t1b_t1b)
  );
  MapT_1 n56 ( // @[Top.scala 1892:21]
    .clock(n56_clock),
    .reset(n56_reset),
    .valid_up(n56_valid_up),
    .valid_down(n56_valid_down),
    .I_0_t0b(n56_I_0_t0b),
    .I_0_t1b_t0b(n56_I_0_t1b_t0b),
    .I_0_t1b_t1b(n56_I_0_t1b_t1b),
    .I_1_t0b(n56_I_1_t0b),
    .I_1_t1b_t0b(n56_I_1_t1b_t0b),
    .I_1_t1b_t1b(n56_I_1_t1b_t1b),
    .I_2_t0b(n56_I_2_t0b),
    .I_2_t1b_t0b(n56_I_2_t1b_t0b),
    .I_2_t1b_t1b(n56_I_2_t1b_t1b),
    .I_3_t0b(n56_I_3_t0b),
    .I_3_t1b_t0b(n56_I_3_t1b_t0b),
    .I_3_t1b_t1b(n56_I_3_t1b_t1b),
    .O_0_t0b(n56_O_0_t0b),
    .O_0_t1b_t0b(n56_O_0_t1b_t0b),
    .O_0_t1b_t1b(n56_O_0_t1b_t1b),
    .O_1_t0b(n56_O_1_t0b),
    .O_1_t1b_t0b(n56_O_1_t1b_t0b),
    .O_1_t1b_t1b(n56_O_1_t1b_t1b),
    .O_2_t0b(n56_O_2_t0b),
    .O_2_t1b_t0b(n56_O_2_t1b_t0b),
    .O_2_t1b_t1b(n56_O_2_t1b_t1b),
    .O_3_t0b(n56_O_3_t0b),
    .O_3_t1b_t0b(n56_O_3_t1b_t0b),
    .O_3_t1b_t1b(n56_O_3_t1b_t1b)
  );
  MapT_2 n95 ( // @[Top.scala 1895:21]
    .clock(n95_clock),
    .reset(n95_reset),
    .valid_up(n95_valid_up),
    .valid_down(n95_valid_down),
    .I_0_t0b(n95_I_0_t0b),
    .I_0_t1b_t0b(n95_I_0_t1b_t0b),
    .I_0_t1b_t1b(n95_I_0_t1b_t1b),
    .I_1_t0b(n95_I_1_t0b),
    .I_1_t1b_t0b(n95_I_1_t1b_t0b),
    .I_1_t1b_t1b(n95_I_1_t1b_t1b),
    .I_2_t0b(n95_I_2_t0b),
    .I_2_t1b_t0b(n95_I_2_t1b_t0b),
    .I_2_t1b_t1b(n95_I_2_t1b_t1b),
    .I_3_t0b(n95_I_3_t0b),
    .I_3_t1b_t0b(n95_I_3_t1b_t0b),
    .I_3_t1b_t1b(n95_I_3_t1b_t1b),
    .O_0_t0b(n95_O_0_t0b),
    .O_0_t1b_t0b(n95_O_0_t1b_t0b),
    .O_0_t1b_t1b(n95_O_0_t1b_t1b),
    .O_1_t0b(n95_O_1_t0b),
    .O_1_t1b_t0b(n95_O_1_t1b_t0b),
    .O_1_t1b_t1b(n95_O_1_t1b_t1b),
    .O_2_t0b(n95_O_2_t0b),
    .O_2_t1b_t0b(n95_O_2_t1b_t0b),
    .O_2_t1b_t1b(n95_O_2_t1b_t1b),
    .O_3_t0b(n95_O_3_t0b),
    .O_3_t1b_t0b(n95_O_3_t1b_t0b),
    .O_3_t1b_t1b(n95_O_3_t1b_t1b)
  );
  MapT_3 n134 ( // @[Top.scala 1898:22]
    .clock(n134_clock),
    .reset(n134_reset),
    .valid_up(n134_valid_up),
    .valid_down(n134_valid_down),
    .I_0_t0b(n134_I_0_t0b),
    .I_0_t1b_t0b(n134_I_0_t1b_t0b),
    .I_0_t1b_t1b(n134_I_0_t1b_t1b),
    .I_1_t0b(n134_I_1_t0b),
    .I_1_t1b_t0b(n134_I_1_t1b_t0b),
    .I_1_t1b_t1b(n134_I_1_t1b_t1b),
    .I_2_t0b(n134_I_2_t0b),
    .I_2_t1b_t0b(n134_I_2_t1b_t0b),
    .I_2_t1b_t1b(n134_I_2_t1b_t1b),
    .I_3_t0b(n134_I_3_t0b),
    .I_3_t1b_t0b(n134_I_3_t1b_t0b),
    .I_3_t1b_t1b(n134_I_3_t1b_t1b),
    .O_0_t0b(n134_O_0_t0b),
    .O_0_t1b_t0b(n134_O_0_t1b_t0b),
    .O_0_t1b_t1b(n134_O_0_t1b_t1b),
    .O_1_t0b(n134_O_1_t0b),
    .O_1_t1b_t0b(n134_O_1_t1b_t0b),
    .O_1_t1b_t1b(n134_O_1_t1b_t1b),
    .O_2_t0b(n134_O_2_t0b),
    .O_2_t1b_t0b(n134_O_2_t1b_t0b),
    .O_2_t1b_t1b(n134_O_2_t1b_t1b),
    .O_3_t0b(n134_O_3_t0b),
    .O_3_t1b_t0b(n134_O_3_t1b_t0b),
    .O_3_t1b_t1b(n134_O_3_t1b_t1b)
  );
  MapT_4 n173 ( // @[Top.scala 1901:22]
    .clock(n173_clock),
    .reset(n173_reset),
    .valid_up(n173_valid_up),
    .valid_down(n173_valid_down),
    .I_0_t0b(n173_I_0_t0b),
    .I_0_t1b_t0b(n173_I_0_t1b_t0b),
    .I_0_t1b_t1b(n173_I_0_t1b_t1b),
    .I_1_t0b(n173_I_1_t0b),
    .I_1_t1b_t0b(n173_I_1_t1b_t0b),
    .I_1_t1b_t1b(n173_I_1_t1b_t1b),
    .I_2_t0b(n173_I_2_t0b),
    .I_2_t1b_t0b(n173_I_2_t1b_t0b),
    .I_2_t1b_t1b(n173_I_2_t1b_t1b),
    .I_3_t0b(n173_I_3_t0b),
    .I_3_t1b_t0b(n173_I_3_t1b_t0b),
    .I_3_t1b_t1b(n173_I_3_t1b_t1b),
    .O_0_t0b(n173_O_0_t0b),
    .O_0_t1b_t0b(n173_O_0_t1b_t0b),
    .O_0_t1b_t1b(n173_O_0_t1b_t1b),
    .O_1_t0b(n173_O_1_t0b),
    .O_1_t1b_t0b(n173_O_1_t1b_t0b),
    .O_1_t1b_t1b(n173_O_1_t1b_t1b),
    .O_2_t0b(n173_O_2_t0b),
    .O_2_t1b_t0b(n173_O_2_t1b_t0b),
    .O_2_t1b_t1b(n173_O_2_t1b_t1b),
    .O_3_t0b(n173_O_3_t0b),
    .O_3_t1b_t0b(n173_O_3_t1b_t0b),
    .O_3_t1b_t1b(n173_O_3_t1b_t1b)
  );
  MapT_5 n212 ( // @[Top.scala 1904:22]
    .clock(n212_clock),
    .reset(n212_reset),
    .valid_up(n212_valid_up),
    .valid_down(n212_valid_down),
    .I_0_t0b(n212_I_0_t0b),
    .I_0_t1b_t0b(n212_I_0_t1b_t0b),
    .I_0_t1b_t1b(n212_I_0_t1b_t1b),
    .I_1_t0b(n212_I_1_t0b),
    .I_1_t1b_t0b(n212_I_1_t1b_t0b),
    .I_1_t1b_t1b(n212_I_1_t1b_t1b),
    .I_2_t0b(n212_I_2_t0b),
    .I_2_t1b_t0b(n212_I_2_t1b_t0b),
    .I_2_t1b_t1b(n212_I_2_t1b_t1b),
    .I_3_t0b(n212_I_3_t0b),
    .I_3_t1b_t0b(n212_I_3_t1b_t0b),
    .I_3_t1b_t1b(n212_I_3_t1b_t1b),
    .O_0_t0b(n212_O_0_t0b),
    .O_0_t1b_t0b(n212_O_0_t1b_t0b),
    .O_0_t1b_t1b(n212_O_0_t1b_t1b),
    .O_1_t0b(n212_O_1_t0b),
    .O_1_t1b_t0b(n212_O_1_t1b_t0b),
    .O_1_t1b_t1b(n212_O_1_t1b_t1b),
    .O_2_t0b(n212_O_2_t0b),
    .O_2_t1b_t0b(n212_O_2_t1b_t0b),
    .O_2_t1b_t1b(n212_O_2_t1b_t1b),
    .O_3_t0b(n212_O_3_t0b),
    .O_3_t1b_t0b(n212_O_3_t1b_t0b),
    .O_3_t1b_t1b(n212_O_3_t1b_t1b)
  );
  MapT_6 n251 ( // @[Top.scala 1907:22]
    .clock(n251_clock),
    .reset(n251_reset),
    .valid_up(n251_valid_up),
    .valid_down(n251_valid_down),
    .I_0_t0b(n251_I_0_t0b),
    .I_0_t1b_t0b(n251_I_0_t1b_t0b),
    .I_0_t1b_t1b(n251_I_0_t1b_t1b),
    .I_1_t0b(n251_I_1_t0b),
    .I_1_t1b_t0b(n251_I_1_t1b_t0b),
    .I_1_t1b_t1b(n251_I_1_t1b_t1b),
    .I_2_t0b(n251_I_2_t0b),
    .I_2_t1b_t0b(n251_I_2_t1b_t0b),
    .I_2_t1b_t1b(n251_I_2_t1b_t1b),
    .I_3_t0b(n251_I_3_t0b),
    .I_3_t1b_t0b(n251_I_3_t1b_t0b),
    .I_3_t1b_t1b(n251_I_3_t1b_t1b),
    .O_0_t0b(n251_O_0_t0b),
    .O_0_t1b_t0b(n251_O_0_t1b_t0b),
    .O_0_t1b_t1b(n251_O_0_t1b_t1b),
    .O_1_t0b(n251_O_1_t0b),
    .O_1_t1b_t0b(n251_O_1_t1b_t0b),
    .O_1_t1b_t1b(n251_O_1_t1b_t1b),
    .O_2_t0b(n251_O_2_t0b),
    .O_2_t1b_t0b(n251_O_2_t1b_t0b),
    .O_2_t1b_t1b(n251_O_2_t1b_t1b),
    .O_3_t0b(n251_O_3_t0b),
    .O_3_t1b_t0b(n251_O_3_t1b_t0b),
    .O_3_t1b_t1b(n251_O_3_t1b_t1b)
  );
  MapT_7 n290 ( // @[Top.scala 1910:22]
    .clock(n290_clock),
    .reset(n290_reset),
    .valid_up(n290_valid_up),
    .valid_down(n290_valid_down),
    .I_0_t0b(n290_I_0_t0b),
    .I_0_t1b_t0b(n290_I_0_t1b_t0b),
    .I_0_t1b_t1b(n290_I_0_t1b_t1b),
    .I_1_t0b(n290_I_1_t0b),
    .I_1_t1b_t0b(n290_I_1_t1b_t0b),
    .I_1_t1b_t1b(n290_I_1_t1b_t1b),
    .I_2_t0b(n290_I_2_t0b),
    .I_2_t1b_t0b(n290_I_2_t1b_t0b),
    .I_2_t1b_t1b(n290_I_2_t1b_t1b),
    .I_3_t0b(n290_I_3_t0b),
    .I_3_t1b_t0b(n290_I_3_t1b_t0b),
    .I_3_t1b_t1b(n290_I_3_t1b_t1b),
    .O_0_t0b(n290_O_0_t0b),
    .O_0_t1b_t0b(n290_O_0_t1b_t0b),
    .O_0_t1b_t1b(n290_O_0_t1b_t1b),
    .O_1_t0b(n290_O_1_t0b),
    .O_1_t1b_t0b(n290_O_1_t1b_t0b),
    .O_1_t1b_t1b(n290_O_1_t1b_t1b),
    .O_2_t0b(n290_O_2_t0b),
    .O_2_t1b_t0b(n290_O_2_t1b_t0b),
    .O_2_t1b_t1b(n290_O_2_t1b_t1b),
    .O_3_t0b(n290_O_3_t0b),
    .O_3_t1b_t0b(n290_O_3_t1b_t0b),
    .O_3_t1b_t1b(n290_O_3_t1b_t1b)
  );
  MapT_8 n329 ( // @[Top.scala 1913:22]
    .clock(n329_clock),
    .reset(n329_reset),
    .valid_up(n329_valid_up),
    .valid_down(n329_valid_down),
    .I_0_t0b(n329_I_0_t0b),
    .I_0_t1b_t0b(n329_I_0_t1b_t0b),
    .I_0_t1b_t1b(n329_I_0_t1b_t1b),
    .I_1_t0b(n329_I_1_t0b),
    .I_1_t1b_t0b(n329_I_1_t1b_t0b),
    .I_1_t1b_t1b(n329_I_1_t1b_t1b),
    .I_2_t0b(n329_I_2_t0b),
    .I_2_t1b_t0b(n329_I_2_t1b_t0b),
    .I_2_t1b_t1b(n329_I_2_t1b_t1b),
    .I_3_t0b(n329_I_3_t0b),
    .I_3_t1b_t0b(n329_I_3_t1b_t0b),
    .I_3_t1b_t1b(n329_I_3_t1b_t1b),
    .O_0_t0b(n329_O_0_t0b),
    .O_0_t1b_t0b(n329_O_0_t1b_t0b),
    .O_0_t1b_t1b(n329_O_0_t1b_t1b),
    .O_1_t0b(n329_O_1_t0b),
    .O_1_t1b_t0b(n329_O_1_t1b_t0b),
    .O_1_t1b_t1b(n329_O_1_t1b_t1b),
    .O_2_t0b(n329_O_2_t0b),
    .O_2_t1b_t0b(n329_O_2_t1b_t0b),
    .O_2_t1b_t1b(n329_O_2_t1b_t1b),
    .O_3_t0b(n329_O_3_t0b),
    .O_3_t1b_t0b(n329_O_3_t1b_t0b),
    .O_3_t1b_t1b(n329_O_3_t1b_t1b)
  );
  MapT_9 n368 ( // @[Top.scala 1916:22]
    .clock(n368_clock),
    .reset(n368_reset),
    .valid_up(n368_valid_up),
    .valid_down(n368_valid_down),
    .I_0_t0b(n368_I_0_t0b),
    .I_0_t1b_t0b(n368_I_0_t1b_t0b),
    .I_0_t1b_t1b(n368_I_0_t1b_t1b),
    .I_1_t0b(n368_I_1_t0b),
    .I_1_t1b_t0b(n368_I_1_t1b_t0b),
    .I_1_t1b_t1b(n368_I_1_t1b_t1b),
    .I_2_t0b(n368_I_2_t0b),
    .I_2_t1b_t0b(n368_I_2_t1b_t0b),
    .I_2_t1b_t1b(n368_I_2_t1b_t1b),
    .I_3_t0b(n368_I_3_t0b),
    .I_3_t1b_t0b(n368_I_3_t1b_t0b),
    .I_3_t1b_t1b(n368_I_3_t1b_t1b),
    .O_0_t0b(n368_O_0_t0b),
    .O_0_t1b_t0b(n368_O_0_t1b_t0b),
    .O_0_t1b_t1b(n368_O_0_t1b_t1b),
    .O_1_t0b(n368_O_1_t0b),
    .O_1_t1b_t0b(n368_O_1_t1b_t0b),
    .O_1_t1b_t1b(n368_O_1_t1b_t1b),
    .O_2_t0b(n368_O_2_t0b),
    .O_2_t1b_t0b(n368_O_2_t1b_t0b),
    .O_2_t1b_t1b(n368_O_2_t1b_t1b),
    .O_3_t0b(n368_O_3_t0b),
    .O_3_t1b_t0b(n368_O_3_t1b_t0b),
    .O_3_t1b_t1b(n368_O_3_t1b_t1b)
  );
  MapT_10 n407 ( // @[Top.scala 1919:22]
    .clock(n407_clock),
    .reset(n407_reset),
    .valid_up(n407_valid_up),
    .valid_down(n407_valid_down),
    .I_0_t0b(n407_I_0_t0b),
    .I_0_t1b_t0b(n407_I_0_t1b_t0b),
    .I_0_t1b_t1b(n407_I_0_t1b_t1b),
    .I_1_t0b(n407_I_1_t0b),
    .I_1_t1b_t0b(n407_I_1_t1b_t0b),
    .I_1_t1b_t1b(n407_I_1_t1b_t1b),
    .I_2_t0b(n407_I_2_t0b),
    .I_2_t1b_t0b(n407_I_2_t1b_t0b),
    .I_2_t1b_t1b(n407_I_2_t1b_t1b),
    .I_3_t0b(n407_I_3_t0b),
    .I_3_t1b_t0b(n407_I_3_t1b_t0b),
    .I_3_t1b_t1b(n407_I_3_t1b_t1b),
    .O_0_t0b(n407_O_0_t0b),
    .O_0_t1b_t0b(n407_O_0_t1b_t0b),
    .O_0_t1b_t1b(n407_O_0_t1b_t1b),
    .O_1_t0b(n407_O_1_t0b),
    .O_1_t1b_t0b(n407_O_1_t1b_t0b),
    .O_1_t1b_t1b(n407_O_1_t1b_t1b),
    .O_2_t0b(n407_O_2_t0b),
    .O_2_t1b_t0b(n407_O_2_t1b_t0b),
    .O_2_t1b_t1b(n407_O_2_t1b_t1b),
    .O_3_t0b(n407_O_3_t0b),
    .O_3_t1b_t0b(n407_O_3_t1b_t0b),
    .O_3_t1b_t1b(n407_O_3_t1b_t1b)
  );
  MapT_11 n446 ( // @[Top.scala 1922:22]
    .clock(n446_clock),
    .reset(n446_reset),
    .valid_up(n446_valid_up),
    .valid_down(n446_valid_down),
    .I_0_t0b(n446_I_0_t0b),
    .I_0_t1b_t0b(n446_I_0_t1b_t0b),
    .I_0_t1b_t1b(n446_I_0_t1b_t1b),
    .I_1_t0b(n446_I_1_t0b),
    .I_1_t1b_t0b(n446_I_1_t1b_t0b),
    .I_1_t1b_t1b(n446_I_1_t1b_t1b),
    .I_2_t0b(n446_I_2_t0b),
    .I_2_t1b_t0b(n446_I_2_t1b_t0b),
    .I_2_t1b_t1b(n446_I_2_t1b_t1b),
    .I_3_t0b(n446_I_3_t0b),
    .I_3_t1b_t0b(n446_I_3_t1b_t0b),
    .I_3_t1b_t1b(n446_I_3_t1b_t1b),
    .O_0_t0b(n446_O_0_t0b),
    .O_0_t1b_t0b(n446_O_0_t1b_t0b),
    .O_0_t1b_t1b(n446_O_0_t1b_t1b),
    .O_1_t0b(n446_O_1_t0b),
    .O_1_t1b_t0b(n446_O_1_t1b_t0b),
    .O_1_t1b_t1b(n446_O_1_t1b_t1b),
    .O_2_t0b(n446_O_2_t0b),
    .O_2_t1b_t0b(n446_O_2_t1b_t0b),
    .O_2_t1b_t1b(n446_O_2_t1b_t1b),
    .O_3_t0b(n446_O_3_t0b),
    .O_3_t1b_t0b(n446_O_3_t1b_t0b),
    .O_3_t1b_t1b(n446_O_3_t1b_t1b)
  );
  MapT_12 n485 ( // @[Top.scala 1925:22]
    .clock(n485_clock),
    .reset(n485_reset),
    .valid_up(n485_valid_up),
    .valid_down(n485_valid_down),
    .I_0_t0b(n485_I_0_t0b),
    .I_0_t1b_t0b(n485_I_0_t1b_t0b),
    .I_0_t1b_t1b(n485_I_0_t1b_t1b),
    .I_1_t0b(n485_I_1_t0b),
    .I_1_t1b_t0b(n485_I_1_t1b_t0b),
    .I_1_t1b_t1b(n485_I_1_t1b_t1b),
    .I_2_t0b(n485_I_2_t0b),
    .I_2_t1b_t0b(n485_I_2_t1b_t0b),
    .I_2_t1b_t1b(n485_I_2_t1b_t1b),
    .I_3_t0b(n485_I_3_t0b),
    .I_3_t1b_t0b(n485_I_3_t1b_t0b),
    .I_3_t1b_t1b(n485_I_3_t1b_t1b),
    .O_0_t0b(n485_O_0_t0b),
    .O_0_t1b_t0b(n485_O_0_t1b_t0b),
    .O_0_t1b_t1b(n485_O_0_t1b_t1b),
    .O_1_t0b(n485_O_1_t0b),
    .O_1_t1b_t0b(n485_O_1_t1b_t0b),
    .O_1_t1b_t1b(n485_O_1_t1b_t1b),
    .O_2_t0b(n485_O_2_t0b),
    .O_2_t1b_t0b(n485_O_2_t1b_t0b),
    .O_2_t1b_t1b(n485_O_2_t1b_t1b),
    .O_3_t0b(n485_O_3_t0b),
    .O_3_t1b_t0b(n485_O_3_t1b_t0b),
    .O_3_t1b_t1b(n485_O_3_t1b_t1b)
  );
  MapT_13 n524 ( // @[Top.scala 1928:22]
    .clock(n524_clock),
    .reset(n524_reset),
    .valid_up(n524_valid_up),
    .valid_down(n524_valid_down),
    .I_0_t0b(n524_I_0_t0b),
    .I_0_t1b_t0b(n524_I_0_t1b_t0b),
    .I_0_t1b_t1b(n524_I_0_t1b_t1b),
    .I_1_t0b(n524_I_1_t0b),
    .I_1_t1b_t0b(n524_I_1_t1b_t0b),
    .I_1_t1b_t1b(n524_I_1_t1b_t1b),
    .I_2_t0b(n524_I_2_t0b),
    .I_2_t1b_t0b(n524_I_2_t1b_t0b),
    .I_2_t1b_t1b(n524_I_2_t1b_t1b),
    .I_3_t0b(n524_I_3_t0b),
    .I_3_t1b_t0b(n524_I_3_t1b_t0b),
    .I_3_t1b_t1b(n524_I_3_t1b_t1b),
    .O_0_t0b(n524_O_0_t0b),
    .O_0_t1b_t0b(n524_O_0_t1b_t0b),
    .O_0_t1b_t1b(n524_O_0_t1b_t1b),
    .O_1_t0b(n524_O_1_t0b),
    .O_1_t1b_t0b(n524_O_1_t1b_t0b),
    .O_1_t1b_t1b(n524_O_1_t1b_t1b),
    .O_2_t0b(n524_O_2_t0b),
    .O_2_t1b_t0b(n524_O_2_t1b_t0b),
    .O_2_t1b_t1b(n524_O_2_t1b_t1b),
    .O_3_t0b(n524_O_3_t0b),
    .O_3_t1b_t0b(n524_O_3_t1b_t0b),
    .O_3_t1b_t1b(n524_O_3_t1b_t1b)
  );
  MapT_14 n563 ( // @[Top.scala 1931:22]
    .clock(n563_clock),
    .reset(n563_reset),
    .valid_up(n563_valid_up),
    .valid_down(n563_valid_down),
    .I_0_t0b(n563_I_0_t0b),
    .I_0_t1b_t0b(n563_I_0_t1b_t0b),
    .I_0_t1b_t1b(n563_I_0_t1b_t1b),
    .I_1_t0b(n563_I_1_t0b),
    .I_1_t1b_t0b(n563_I_1_t1b_t0b),
    .I_1_t1b_t1b(n563_I_1_t1b_t1b),
    .I_2_t0b(n563_I_2_t0b),
    .I_2_t1b_t0b(n563_I_2_t1b_t0b),
    .I_2_t1b_t1b(n563_I_2_t1b_t1b),
    .I_3_t0b(n563_I_3_t0b),
    .I_3_t1b_t0b(n563_I_3_t1b_t0b),
    .I_3_t1b_t1b(n563_I_3_t1b_t1b),
    .O_0_t0b(n563_O_0_t0b),
    .O_0_t1b_t0b(n563_O_0_t1b_t0b),
    .O_0_t1b_t1b(n563_O_0_t1b_t1b),
    .O_1_t0b(n563_O_1_t0b),
    .O_1_t1b_t0b(n563_O_1_t1b_t0b),
    .O_1_t1b_t1b(n563_O_1_t1b_t1b),
    .O_2_t0b(n563_O_2_t0b),
    .O_2_t1b_t0b(n563_O_2_t1b_t0b),
    .O_2_t1b_t1b(n563_O_2_t1b_t1b),
    .O_3_t0b(n563_O_3_t0b),
    .O_3_t1b_t0b(n563_O_3_t1b_t0b),
    .O_3_t1b_t1b(n563_O_3_t1b_t1b)
  );
  MapT_15 n602 ( // @[Top.scala 1934:22]
    .clock(n602_clock),
    .reset(n602_reset),
    .valid_up(n602_valid_up),
    .valid_down(n602_valid_down),
    .I_0_t0b(n602_I_0_t0b),
    .I_0_t1b_t0b(n602_I_0_t1b_t0b),
    .I_0_t1b_t1b(n602_I_0_t1b_t1b),
    .I_1_t0b(n602_I_1_t0b),
    .I_1_t1b_t0b(n602_I_1_t1b_t0b),
    .I_1_t1b_t1b(n602_I_1_t1b_t1b),
    .I_2_t0b(n602_I_2_t0b),
    .I_2_t1b_t0b(n602_I_2_t1b_t0b),
    .I_2_t1b_t1b(n602_I_2_t1b_t1b),
    .I_3_t0b(n602_I_3_t0b),
    .I_3_t1b_t0b(n602_I_3_t1b_t0b),
    .I_3_t1b_t1b(n602_I_3_t1b_t1b),
    .O_0_t0b(n602_O_0_t0b),
    .O_0_t1b_t0b(n602_O_0_t1b_t0b),
    .O_0_t1b_t1b(n602_O_0_t1b_t1b),
    .O_1_t0b(n602_O_1_t0b),
    .O_1_t1b_t0b(n602_O_1_t1b_t0b),
    .O_1_t1b_t1b(n602_O_1_t1b_t1b),
    .O_2_t0b(n602_O_2_t0b),
    .O_2_t1b_t0b(n602_O_2_t1b_t0b),
    .O_2_t1b_t1b(n602_O_2_t1b_t1b),
    .O_3_t0b(n602_O_3_t0b),
    .O_3_t1b_t0b(n602_O_3_t1b_t0b),
    .O_3_t1b_t1b(n602_O_3_t1b_t1b)
  );
  MapT_16 n641 ( // @[Top.scala 1937:22]
    .clock(n641_clock),
    .reset(n641_reset),
    .valid_up(n641_valid_up),
    .valid_down(n641_valid_down),
    .I_0_t0b(n641_I_0_t0b),
    .I_0_t1b_t0b(n641_I_0_t1b_t0b),
    .I_0_t1b_t1b(n641_I_0_t1b_t1b),
    .I_1_t0b(n641_I_1_t0b),
    .I_1_t1b_t0b(n641_I_1_t1b_t0b),
    .I_1_t1b_t1b(n641_I_1_t1b_t1b),
    .I_2_t0b(n641_I_2_t0b),
    .I_2_t1b_t0b(n641_I_2_t1b_t0b),
    .I_2_t1b_t1b(n641_I_2_t1b_t1b),
    .I_3_t0b(n641_I_3_t0b),
    .I_3_t1b_t0b(n641_I_3_t1b_t0b),
    .I_3_t1b_t1b(n641_I_3_t1b_t1b),
    .O_0_t1b_t0b(n641_O_0_t1b_t0b),
    .O_0_t1b_t1b(n641_O_0_t1b_t1b),
    .O_1_t1b_t0b(n641_O_1_t1b_t0b),
    .O_1_t1b_t1b(n641_O_1_t1b_t1b),
    .O_2_t1b_t0b(n641_O_2_t1b_t0b),
    .O_2_t1b_t1b(n641_O_2_t1b_t1b),
    .O_3_t1b_t0b(n641_O_3_t1b_t0b),
    .O_3_t1b_t1b(n641_O_3_t1b_t1b)
  );
  MapT_17 n647 ( // @[Top.scala 1940:22]
    .valid_up(n647_valid_up),
    .valid_down(n647_valid_down),
    .I_0_t1b_t0b(n647_I_0_t1b_t0b),
    .I_0_t1b_t1b(n647_I_0_t1b_t1b),
    .I_1_t1b_t0b(n647_I_1_t1b_t0b),
    .I_1_t1b_t1b(n647_I_1_t1b_t1b),
    .I_2_t1b_t0b(n647_I_2_t1b_t0b),
    .I_2_t1b_t1b(n647_I_2_t1b_t1b),
    .I_3_t1b_t0b(n647_I_3_t1b_t0b),
    .I_3_t1b_t1b(n647_I_3_t1b_t1b),
    .O_0(n647_O_0),
    .O_1(n647_O_1),
    .O_2(n647_O_2),
    .O_3(n647_O_3)
  );
  FIFO n648 ( // @[Top.scala 1943:22]
    .clock(n648_clock),
    .reset(n648_reset),
    .valid_up(n648_valid_up),
    .valid_down(n648_valid_down),
    .I_0(n648_I_0),
    .I_1(n648_I_1),
    .I_2(n648_I_2),
    .I_3(n648_I_3),
    .O_0(n648_O_0),
    .O_1(n648_O_1),
    .O_2(n648_O_2),
    .O_3(n648_O_3)
  );
  FIFO n649 ( // @[Top.scala 1946:22]
    .clock(n649_clock),
    .reset(n649_reset),
    .valid_up(n649_valid_up),
    .valid_down(n649_valid_down),
    .I_0(n649_I_0),
    .I_1(n649_I_1),
    .I_2(n649_I_2),
    .I_3(n649_I_3),
    .O_0(n649_O_0),
    .O_1(n649_O_1),
    .O_2(n649_O_2),
    .O_3(n649_O_3)
  );
  FIFO n650 ( // @[Top.scala 1949:22]
    .clock(n650_clock),
    .reset(n650_reset),
    .valid_up(n650_valid_up),
    .valid_down(n650_valid_down),
    .I_0(n650_I_0),
    .I_1(n650_I_1),
    .I_2(n650_I_2),
    .I_3(n650_I_3),
    .O_0(n650_O_0),
    .O_1(n650_O_1),
    .O_2(n650_O_2),
    .O_3(n650_O_3)
  );
  assign valid_down = n650_valid_down; // @[Top.scala 1953:16]
  assign O_0 = n650_O_0; // @[Top.scala 1952:7]
  assign O_1 = n650_O_1; // @[Top.scala 1952:7]
  assign O_2 = n650_O_2; // @[Top.scala 1952:7]
  assign O_3 = n650_O_3; // @[Top.scala 1952:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 1888:17]
  assign n1_I_0 = I_0; // @[Top.scala 1887:10]
  assign n1_I_1 = I_1; // @[Top.scala 1887:10]
  assign n1_I_2 = I_2; // @[Top.scala 1887:10]
  assign n1_I_3 = I_3; // @[Top.scala 1887:10]
  assign n17_clock = clock;
  assign n17_reset = reset;
  assign n17_valid_up = n1_valid_down; // @[Top.scala 1891:18]
  assign n17_I_0 = n1_O_0; // @[Top.scala 1890:11]
  assign n17_I_1 = n1_O_1; // @[Top.scala 1890:11]
  assign n17_I_2 = n1_O_2; // @[Top.scala 1890:11]
  assign n17_I_3 = n1_O_3; // @[Top.scala 1890:11]
  assign n56_clock = clock;
  assign n56_reset = reset;
  assign n56_valid_up = n17_valid_down; // @[Top.scala 1894:18]
  assign n56_I_0_t0b = n17_O_0_t0b; // @[Top.scala 1893:11]
  assign n56_I_0_t1b_t0b = n17_O_0_t1b_t0b; // @[Top.scala 1893:11]
  assign n56_I_0_t1b_t1b = n17_O_0_t1b_t1b; // @[Top.scala 1893:11]
  assign n56_I_1_t0b = n17_O_1_t0b; // @[Top.scala 1893:11]
  assign n56_I_1_t1b_t0b = n17_O_1_t1b_t0b; // @[Top.scala 1893:11]
  assign n56_I_1_t1b_t1b = n17_O_1_t1b_t1b; // @[Top.scala 1893:11]
  assign n56_I_2_t0b = n17_O_2_t0b; // @[Top.scala 1893:11]
  assign n56_I_2_t1b_t0b = n17_O_2_t1b_t0b; // @[Top.scala 1893:11]
  assign n56_I_2_t1b_t1b = n17_O_2_t1b_t1b; // @[Top.scala 1893:11]
  assign n56_I_3_t0b = n17_O_3_t0b; // @[Top.scala 1893:11]
  assign n56_I_3_t1b_t0b = n17_O_3_t1b_t0b; // @[Top.scala 1893:11]
  assign n56_I_3_t1b_t1b = n17_O_3_t1b_t1b; // @[Top.scala 1893:11]
  assign n95_clock = clock;
  assign n95_reset = reset;
  assign n95_valid_up = n56_valid_down; // @[Top.scala 1897:18]
  assign n95_I_0_t0b = n56_O_0_t0b; // @[Top.scala 1896:11]
  assign n95_I_0_t1b_t0b = n56_O_0_t1b_t0b; // @[Top.scala 1896:11]
  assign n95_I_0_t1b_t1b = n56_O_0_t1b_t1b; // @[Top.scala 1896:11]
  assign n95_I_1_t0b = n56_O_1_t0b; // @[Top.scala 1896:11]
  assign n95_I_1_t1b_t0b = n56_O_1_t1b_t0b; // @[Top.scala 1896:11]
  assign n95_I_1_t1b_t1b = n56_O_1_t1b_t1b; // @[Top.scala 1896:11]
  assign n95_I_2_t0b = n56_O_2_t0b; // @[Top.scala 1896:11]
  assign n95_I_2_t1b_t0b = n56_O_2_t1b_t0b; // @[Top.scala 1896:11]
  assign n95_I_2_t1b_t1b = n56_O_2_t1b_t1b; // @[Top.scala 1896:11]
  assign n95_I_3_t0b = n56_O_3_t0b; // @[Top.scala 1896:11]
  assign n95_I_3_t1b_t0b = n56_O_3_t1b_t0b; // @[Top.scala 1896:11]
  assign n95_I_3_t1b_t1b = n56_O_3_t1b_t1b; // @[Top.scala 1896:11]
  assign n134_clock = clock;
  assign n134_reset = reset;
  assign n134_valid_up = n95_valid_down; // @[Top.scala 1900:19]
  assign n134_I_0_t0b = n95_O_0_t0b; // @[Top.scala 1899:12]
  assign n134_I_0_t1b_t0b = n95_O_0_t1b_t0b; // @[Top.scala 1899:12]
  assign n134_I_0_t1b_t1b = n95_O_0_t1b_t1b; // @[Top.scala 1899:12]
  assign n134_I_1_t0b = n95_O_1_t0b; // @[Top.scala 1899:12]
  assign n134_I_1_t1b_t0b = n95_O_1_t1b_t0b; // @[Top.scala 1899:12]
  assign n134_I_1_t1b_t1b = n95_O_1_t1b_t1b; // @[Top.scala 1899:12]
  assign n134_I_2_t0b = n95_O_2_t0b; // @[Top.scala 1899:12]
  assign n134_I_2_t1b_t0b = n95_O_2_t1b_t0b; // @[Top.scala 1899:12]
  assign n134_I_2_t1b_t1b = n95_O_2_t1b_t1b; // @[Top.scala 1899:12]
  assign n134_I_3_t0b = n95_O_3_t0b; // @[Top.scala 1899:12]
  assign n134_I_3_t1b_t0b = n95_O_3_t1b_t0b; // @[Top.scala 1899:12]
  assign n134_I_3_t1b_t1b = n95_O_3_t1b_t1b; // @[Top.scala 1899:12]
  assign n173_clock = clock;
  assign n173_reset = reset;
  assign n173_valid_up = n134_valid_down; // @[Top.scala 1903:19]
  assign n173_I_0_t0b = n134_O_0_t0b; // @[Top.scala 1902:12]
  assign n173_I_0_t1b_t0b = n134_O_0_t1b_t0b; // @[Top.scala 1902:12]
  assign n173_I_0_t1b_t1b = n134_O_0_t1b_t1b; // @[Top.scala 1902:12]
  assign n173_I_1_t0b = n134_O_1_t0b; // @[Top.scala 1902:12]
  assign n173_I_1_t1b_t0b = n134_O_1_t1b_t0b; // @[Top.scala 1902:12]
  assign n173_I_1_t1b_t1b = n134_O_1_t1b_t1b; // @[Top.scala 1902:12]
  assign n173_I_2_t0b = n134_O_2_t0b; // @[Top.scala 1902:12]
  assign n173_I_2_t1b_t0b = n134_O_2_t1b_t0b; // @[Top.scala 1902:12]
  assign n173_I_2_t1b_t1b = n134_O_2_t1b_t1b; // @[Top.scala 1902:12]
  assign n173_I_3_t0b = n134_O_3_t0b; // @[Top.scala 1902:12]
  assign n173_I_3_t1b_t0b = n134_O_3_t1b_t0b; // @[Top.scala 1902:12]
  assign n173_I_3_t1b_t1b = n134_O_3_t1b_t1b; // @[Top.scala 1902:12]
  assign n212_clock = clock;
  assign n212_reset = reset;
  assign n212_valid_up = n173_valid_down; // @[Top.scala 1906:19]
  assign n212_I_0_t0b = n173_O_0_t0b; // @[Top.scala 1905:12]
  assign n212_I_0_t1b_t0b = n173_O_0_t1b_t0b; // @[Top.scala 1905:12]
  assign n212_I_0_t1b_t1b = n173_O_0_t1b_t1b; // @[Top.scala 1905:12]
  assign n212_I_1_t0b = n173_O_1_t0b; // @[Top.scala 1905:12]
  assign n212_I_1_t1b_t0b = n173_O_1_t1b_t0b; // @[Top.scala 1905:12]
  assign n212_I_1_t1b_t1b = n173_O_1_t1b_t1b; // @[Top.scala 1905:12]
  assign n212_I_2_t0b = n173_O_2_t0b; // @[Top.scala 1905:12]
  assign n212_I_2_t1b_t0b = n173_O_2_t1b_t0b; // @[Top.scala 1905:12]
  assign n212_I_2_t1b_t1b = n173_O_2_t1b_t1b; // @[Top.scala 1905:12]
  assign n212_I_3_t0b = n173_O_3_t0b; // @[Top.scala 1905:12]
  assign n212_I_3_t1b_t0b = n173_O_3_t1b_t0b; // @[Top.scala 1905:12]
  assign n212_I_3_t1b_t1b = n173_O_3_t1b_t1b; // @[Top.scala 1905:12]
  assign n251_clock = clock;
  assign n251_reset = reset;
  assign n251_valid_up = n212_valid_down; // @[Top.scala 1909:19]
  assign n251_I_0_t0b = n212_O_0_t0b; // @[Top.scala 1908:12]
  assign n251_I_0_t1b_t0b = n212_O_0_t1b_t0b; // @[Top.scala 1908:12]
  assign n251_I_0_t1b_t1b = n212_O_0_t1b_t1b; // @[Top.scala 1908:12]
  assign n251_I_1_t0b = n212_O_1_t0b; // @[Top.scala 1908:12]
  assign n251_I_1_t1b_t0b = n212_O_1_t1b_t0b; // @[Top.scala 1908:12]
  assign n251_I_1_t1b_t1b = n212_O_1_t1b_t1b; // @[Top.scala 1908:12]
  assign n251_I_2_t0b = n212_O_2_t0b; // @[Top.scala 1908:12]
  assign n251_I_2_t1b_t0b = n212_O_2_t1b_t0b; // @[Top.scala 1908:12]
  assign n251_I_2_t1b_t1b = n212_O_2_t1b_t1b; // @[Top.scala 1908:12]
  assign n251_I_3_t0b = n212_O_3_t0b; // @[Top.scala 1908:12]
  assign n251_I_3_t1b_t0b = n212_O_3_t1b_t0b; // @[Top.scala 1908:12]
  assign n251_I_3_t1b_t1b = n212_O_3_t1b_t1b; // @[Top.scala 1908:12]
  assign n290_clock = clock;
  assign n290_reset = reset;
  assign n290_valid_up = n251_valid_down; // @[Top.scala 1912:19]
  assign n290_I_0_t0b = n251_O_0_t0b; // @[Top.scala 1911:12]
  assign n290_I_0_t1b_t0b = n251_O_0_t1b_t0b; // @[Top.scala 1911:12]
  assign n290_I_0_t1b_t1b = n251_O_0_t1b_t1b; // @[Top.scala 1911:12]
  assign n290_I_1_t0b = n251_O_1_t0b; // @[Top.scala 1911:12]
  assign n290_I_1_t1b_t0b = n251_O_1_t1b_t0b; // @[Top.scala 1911:12]
  assign n290_I_1_t1b_t1b = n251_O_1_t1b_t1b; // @[Top.scala 1911:12]
  assign n290_I_2_t0b = n251_O_2_t0b; // @[Top.scala 1911:12]
  assign n290_I_2_t1b_t0b = n251_O_2_t1b_t0b; // @[Top.scala 1911:12]
  assign n290_I_2_t1b_t1b = n251_O_2_t1b_t1b; // @[Top.scala 1911:12]
  assign n290_I_3_t0b = n251_O_3_t0b; // @[Top.scala 1911:12]
  assign n290_I_3_t1b_t0b = n251_O_3_t1b_t0b; // @[Top.scala 1911:12]
  assign n290_I_3_t1b_t1b = n251_O_3_t1b_t1b; // @[Top.scala 1911:12]
  assign n329_clock = clock;
  assign n329_reset = reset;
  assign n329_valid_up = n290_valid_down; // @[Top.scala 1915:19]
  assign n329_I_0_t0b = n290_O_0_t0b; // @[Top.scala 1914:12]
  assign n329_I_0_t1b_t0b = n290_O_0_t1b_t0b; // @[Top.scala 1914:12]
  assign n329_I_0_t1b_t1b = n290_O_0_t1b_t1b; // @[Top.scala 1914:12]
  assign n329_I_1_t0b = n290_O_1_t0b; // @[Top.scala 1914:12]
  assign n329_I_1_t1b_t0b = n290_O_1_t1b_t0b; // @[Top.scala 1914:12]
  assign n329_I_1_t1b_t1b = n290_O_1_t1b_t1b; // @[Top.scala 1914:12]
  assign n329_I_2_t0b = n290_O_2_t0b; // @[Top.scala 1914:12]
  assign n329_I_2_t1b_t0b = n290_O_2_t1b_t0b; // @[Top.scala 1914:12]
  assign n329_I_2_t1b_t1b = n290_O_2_t1b_t1b; // @[Top.scala 1914:12]
  assign n329_I_3_t0b = n290_O_3_t0b; // @[Top.scala 1914:12]
  assign n329_I_3_t1b_t0b = n290_O_3_t1b_t0b; // @[Top.scala 1914:12]
  assign n329_I_3_t1b_t1b = n290_O_3_t1b_t1b; // @[Top.scala 1914:12]
  assign n368_clock = clock;
  assign n368_reset = reset;
  assign n368_valid_up = n329_valid_down; // @[Top.scala 1918:19]
  assign n368_I_0_t0b = n329_O_0_t0b; // @[Top.scala 1917:12]
  assign n368_I_0_t1b_t0b = n329_O_0_t1b_t0b; // @[Top.scala 1917:12]
  assign n368_I_0_t1b_t1b = n329_O_0_t1b_t1b; // @[Top.scala 1917:12]
  assign n368_I_1_t0b = n329_O_1_t0b; // @[Top.scala 1917:12]
  assign n368_I_1_t1b_t0b = n329_O_1_t1b_t0b; // @[Top.scala 1917:12]
  assign n368_I_1_t1b_t1b = n329_O_1_t1b_t1b; // @[Top.scala 1917:12]
  assign n368_I_2_t0b = n329_O_2_t0b; // @[Top.scala 1917:12]
  assign n368_I_2_t1b_t0b = n329_O_2_t1b_t0b; // @[Top.scala 1917:12]
  assign n368_I_2_t1b_t1b = n329_O_2_t1b_t1b; // @[Top.scala 1917:12]
  assign n368_I_3_t0b = n329_O_3_t0b; // @[Top.scala 1917:12]
  assign n368_I_3_t1b_t0b = n329_O_3_t1b_t0b; // @[Top.scala 1917:12]
  assign n368_I_3_t1b_t1b = n329_O_3_t1b_t1b; // @[Top.scala 1917:12]
  assign n407_clock = clock;
  assign n407_reset = reset;
  assign n407_valid_up = n368_valid_down; // @[Top.scala 1921:19]
  assign n407_I_0_t0b = n368_O_0_t0b; // @[Top.scala 1920:12]
  assign n407_I_0_t1b_t0b = n368_O_0_t1b_t0b; // @[Top.scala 1920:12]
  assign n407_I_0_t1b_t1b = n368_O_0_t1b_t1b; // @[Top.scala 1920:12]
  assign n407_I_1_t0b = n368_O_1_t0b; // @[Top.scala 1920:12]
  assign n407_I_1_t1b_t0b = n368_O_1_t1b_t0b; // @[Top.scala 1920:12]
  assign n407_I_1_t1b_t1b = n368_O_1_t1b_t1b; // @[Top.scala 1920:12]
  assign n407_I_2_t0b = n368_O_2_t0b; // @[Top.scala 1920:12]
  assign n407_I_2_t1b_t0b = n368_O_2_t1b_t0b; // @[Top.scala 1920:12]
  assign n407_I_2_t1b_t1b = n368_O_2_t1b_t1b; // @[Top.scala 1920:12]
  assign n407_I_3_t0b = n368_O_3_t0b; // @[Top.scala 1920:12]
  assign n407_I_3_t1b_t0b = n368_O_3_t1b_t0b; // @[Top.scala 1920:12]
  assign n407_I_3_t1b_t1b = n368_O_3_t1b_t1b; // @[Top.scala 1920:12]
  assign n446_clock = clock;
  assign n446_reset = reset;
  assign n446_valid_up = n407_valid_down; // @[Top.scala 1924:19]
  assign n446_I_0_t0b = n407_O_0_t0b; // @[Top.scala 1923:12]
  assign n446_I_0_t1b_t0b = n407_O_0_t1b_t0b; // @[Top.scala 1923:12]
  assign n446_I_0_t1b_t1b = n407_O_0_t1b_t1b; // @[Top.scala 1923:12]
  assign n446_I_1_t0b = n407_O_1_t0b; // @[Top.scala 1923:12]
  assign n446_I_1_t1b_t0b = n407_O_1_t1b_t0b; // @[Top.scala 1923:12]
  assign n446_I_1_t1b_t1b = n407_O_1_t1b_t1b; // @[Top.scala 1923:12]
  assign n446_I_2_t0b = n407_O_2_t0b; // @[Top.scala 1923:12]
  assign n446_I_2_t1b_t0b = n407_O_2_t1b_t0b; // @[Top.scala 1923:12]
  assign n446_I_2_t1b_t1b = n407_O_2_t1b_t1b; // @[Top.scala 1923:12]
  assign n446_I_3_t0b = n407_O_3_t0b; // @[Top.scala 1923:12]
  assign n446_I_3_t1b_t0b = n407_O_3_t1b_t0b; // @[Top.scala 1923:12]
  assign n446_I_3_t1b_t1b = n407_O_3_t1b_t1b; // @[Top.scala 1923:12]
  assign n485_clock = clock;
  assign n485_reset = reset;
  assign n485_valid_up = n446_valid_down; // @[Top.scala 1927:19]
  assign n485_I_0_t0b = n446_O_0_t0b; // @[Top.scala 1926:12]
  assign n485_I_0_t1b_t0b = n446_O_0_t1b_t0b; // @[Top.scala 1926:12]
  assign n485_I_0_t1b_t1b = n446_O_0_t1b_t1b; // @[Top.scala 1926:12]
  assign n485_I_1_t0b = n446_O_1_t0b; // @[Top.scala 1926:12]
  assign n485_I_1_t1b_t0b = n446_O_1_t1b_t0b; // @[Top.scala 1926:12]
  assign n485_I_1_t1b_t1b = n446_O_1_t1b_t1b; // @[Top.scala 1926:12]
  assign n485_I_2_t0b = n446_O_2_t0b; // @[Top.scala 1926:12]
  assign n485_I_2_t1b_t0b = n446_O_2_t1b_t0b; // @[Top.scala 1926:12]
  assign n485_I_2_t1b_t1b = n446_O_2_t1b_t1b; // @[Top.scala 1926:12]
  assign n485_I_3_t0b = n446_O_3_t0b; // @[Top.scala 1926:12]
  assign n485_I_3_t1b_t0b = n446_O_3_t1b_t0b; // @[Top.scala 1926:12]
  assign n485_I_3_t1b_t1b = n446_O_3_t1b_t1b; // @[Top.scala 1926:12]
  assign n524_clock = clock;
  assign n524_reset = reset;
  assign n524_valid_up = n485_valid_down; // @[Top.scala 1930:19]
  assign n524_I_0_t0b = n485_O_0_t0b; // @[Top.scala 1929:12]
  assign n524_I_0_t1b_t0b = n485_O_0_t1b_t0b; // @[Top.scala 1929:12]
  assign n524_I_0_t1b_t1b = n485_O_0_t1b_t1b; // @[Top.scala 1929:12]
  assign n524_I_1_t0b = n485_O_1_t0b; // @[Top.scala 1929:12]
  assign n524_I_1_t1b_t0b = n485_O_1_t1b_t0b; // @[Top.scala 1929:12]
  assign n524_I_1_t1b_t1b = n485_O_1_t1b_t1b; // @[Top.scala 1929:12]
  assign n524_I_2_t0b = n485_O_2_t0b; // @[Top.scala 1929:12]
  assign n524_I_2_t1b_t0b = n485_O_2_t1b_t0b; // @[Top.scala 1929:12]
  assign n524_I_2_t1b_t1b = n485_O_2_t1b_t1b; // @[Top.scala 1929:12]
  assign n524_I_3_t0b = n485_O_3_t0b; // @[Top.scala 1929:12]
  assign n524_I_3_t1b_t0b = n485_O_3_t1b_t0b; // @[Top.scala 1929:12]
  assign n524_I_3_t1b_t1b = n485_O_3_t1b_t1b; // @[Top.scala 1929:12]
  assign n563_clock = clock;
  assign n563_reset = reset;
  assign n563_valid_up = n524_valid_down; // @[Top.scala 1933:19]
  assign n563_I_0_t0b = n524_O_0_t0b; // @[Top.scala 1932:12]
  assign n563_I_0_t1b_t0b = n524_O_0_t1b_t0b; // @[Top.scala 1932:12]
  assign n563_I_0_t1b_t1b = n524_O_0_t1b_t1b; // @[Top.scala 1932:12]
  assign n563_I_1_t0b = n524_O_1_t0b; // @[Top.scala 1932:12]
  assign n563_I_1_t1b_t0b = n524_O_1_t1b_t0b; // @[Top.scala 1932:12]
  assign n563_I_1_t1b_t1b = n524_O_1_t1b_t1b; // @[Top.scala 1932:12]
  assign n563_I_2_t0b = n524_O_2_t0b; // @[Top.scala 1932:12]
  assign n563_I_2_t1b_t0b = n524_O_2_t1b_t0b; // @[Top.scala 1932:12]
  assign n563_I_2_t1b_t1b = n524_O_2_t1b_t1b; // @[Top.scala 1932:12]
  assign n563_I_3_t0b = n524_O_3_t0b; // @[Top.scala 1932:12]
  assign n563_I_3_t1b_t0b = n524_O_3_t1b_t0b; // @[Top.scala 1932:12]
  assign n563_I_3_t1b_t1b = n524_O_3_t1b_t1b; // @[Top.scala 1932:12]
  assign n602_clock = clock;
  assign n602_reset = reset;
  assign n602_valid_up = n563_valid_down; // @[Top.scala 1936:19]
  assign n602_I_0_t0b = n563_O_0_t0b; // @[Top.scala 1935:12]
  assign n602_I_0_t1b_t0b = n563_O_0_t1b_t0b; // @[Top.scala 1935:12]
  assign n602_I_0_t1b_t1b = n563_O_0_t1b_t1b; // @[Top.scala 1935:12]
  assign n602_I_1_t0b = n563_O_1_t0b; // @[Top.scala 1935:12]
  assign n602_I_1_t1b_t0b = n563_O_1_t1b_t0b; // @[Top.scala 1935:12]
  assign n602_I_1_t1b_t1b = n563_O_1_t1b_t1b; // @[Top.scala 1935:12]
  assign n602_I_2_t0b = n563_O_2_t0b; // @[Top.scala 1935:12]
  assign n602_I_2_t1b_t0b = n563_O_2_t1b_t0b; // @[Top.scala 1935:12]
  assign n602_I_2_t1b_t1b = n563_O_2_t1b_t1b; // @[Top.scala 1935:12]
  assign n602_I_3_t0b = n563_O_3_t0b; // @[Top.scala 1935:12]
  assign n602_I_3_t1b_t0b = n563_O_3_t1b_t0b; // @[Top.scala 1935:12]
  assign n602_I_3_t1b_t1b = n563_O_3_t1b_t1b; // @[Top.scala 1935:12]
  assign n641_clock = clock;
  assign n641_reset = reset;
  assign n641_valid_up = n602_valid_down; // @[Top.scala 1939:19]
  assign n641_I_0_t0b = n602_O_0_t0b; // @[Top.scala 1938:12]
  assign n641_I_0_t1b_t0b = n602_O_0_t1b_t0b; // @[Top.scala 1938:12]
  assign n641_I_0_t1b_t1b = n602_O_0_t1b_t1b; // @[Top.scala 1938:12]
  assign n641_I_1_t0b = n602_O_1_t0b; // @[Top.scala 1938:12]
  assign n641_I_1_t1b_t0b = n602_O_1_t1b_t0b; // @[Top.scala 1938:12]
  assign n641_I_1_t1b_t1b = n602_O_1_t1b_t1b; // @[Top.scala 1938:12]
  assign n641_I_2_t0b = n602_O_2_t0b; // @[Top.scala 1938:12]
  assign n641_I_2_t1b_t0b = n602_O_2_t1b_t0b; // @[Top.scala 1938:12]
  assign n641_I_2_t1b_t1b = n602_O_2_t1b_t1b; // @[Top.scala 1938:12]
  assign n641_I_3_t0b = n602_O_3_t0b; // @[Top.scala 1938:12]
  assign n641_I_3_t1b_t0b = n602_O_3_t1b_t0b; // @[Top.scala 1938:12]
  assign n641_I_3_t1b_t1b = n602_O_3_t1b_t1b; // @[Top.scala 1938:12]
  assign n647_valid_up = n641_valid_down; // @[Top.scala 1942:19]
  assign n647_I_0_t1b_t0b = n641_O_0_t1b_t0b; // @[Top.scala 1941:12]
  assign n647_I_0_t1b_t1b = n641_O_0_t1b_t1b; // @[Top.scala 1941:12]
  assign n647_I_1_t1b_t0b = n641_O_1_t1b_t0b; // @[Top.scala 1941:12]
  assign n647_I_1_t1b_t1b = n641_O_1_t1b_t1b; // @[Top.scala 1941:12]
  assign n647_I_2_t1b_t0b = n641_O_2_t1b_t0b; // @[Top.scala 1941:12]
  assign n647_I_2_t1b_t1b = n641_O_2_t1b_t1b; // @[Top.scala 1941:12]
  assign n647_I_3_t1b_t0b = n641_O_3_t1b_t0b; // @[Top.scala 1941:12]
  assign n647_I_3_t1b_t1b = n641_O_3_t1b_t1b; // @[Top.scala 1941:12]
  assign n648_clock = clock;
  assign n648_reset = reset;
  assign n648_valid_up = n647_valid_down; // @[Top.scala 1945:19]
  assign n648_I_0 = n647_O_0; // @[Top.scala 1944:12]
  assign n648_I_1 = n647_O_1; // @[Top.scala 1944:12]
  assign n648_I_2 = n647_O_2; // @[Top.scala 1944:12]
  assign n648_I_3 = n647_O_3; // @[Top.scala 1944:12]
  assign n649_clock = clock;
  assign n649_reset = reset;
  assign n649_valid_up = n648_valid_down; // @[Top.scala 1948:19]
  assign n649_I_0 = n648_O_0; // @[Top.scala 1947:12]
  assign n649_I_1 = n648_O_1; // @[Top.scala 1947:12]
  assign n649_I_2 = n648_O_2; // @[Top.scala 1947:12]
  assign n649_I_3 = n648_O_3; // @[Top.scala 1947:12]
  assign n650_clock = clock;
  assign n650_reset = reset;
  assign n650_valid_up = n649_valid_down; // @[Top.scala 1951:19]
  assign n650_I_0 = n649_O_0; // @[Top.scala 1950:12]
  assign n650_I_1 = n649_O_1; // @[Top.scala 1950:12]
  assign n650_I_2 = n649_O_2; // @[Top.scala 1950:12]
  assign n650_I_3 = n649_O_3; // @[Top.scala 1950:12]
endmodule
