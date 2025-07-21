module FIFO(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  input  [15:0] I_1,
  input  [15:0] I_2,
  input  [15:0] I_3,
  input  [15:0] I_4,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4
);
  reg [15:0] _T__0; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_0;
  reg [15:0] _T__1; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_1;
  reg [15:0] _T__2; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_2;
  reg [15:0] _T__3; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_3;
  reg [15:0] _T__4; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_4;
  reg  _T_1; // @[FIFO.scala 15:27]
  reg [31:0] _RAND_5;
  assign valid_down = _T_1; // @[FIFO.scala 16:16]
  assign O_0 = _T__0; // @[FIFO.scala 14:7]
  assign O_1 = _T__1; // @[FIFO.scala 14:7]
  assign O_2 = _T__2; // @[FIFO.scala 14:7]
  assign O_3 = _T__3; // @[FIFO.scala 14:7]
  assign O_4 = _T__4; // @[FIFO.scala 14:7]
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
  _T__4 = _RAND_4[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_1 = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    _T__0 <= I_0;
    _T__1 <= I_1;
    _T__2 <= I_2;
    _T__3 <= I_3;
    _T__4 <= I_4;
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
  wire  n10_valid_up; // @[Top.scala 18:21]
  wire  n10_valid_down; // @[Top.scala 18:21]
  wire [15:0] n10_I0; // @[Top.scala 18:21]
  wire [15:0] n10_I1; // @[Top.scala 18:21]
  wire [15:0] n10_O_t0b; // @[Top.scala 18:21]
  wire [15:0] n10_O_t1b; // @[Top.scala 18:21]
  wire  n11_clock; // @[Top.scala 22:21]
  wire  n11_reset; // @[Top.scala 22:21]
  wire  n11_valid_up; // @[Top.scala 22:21]
  wire  n11_valid_down; // @[Top.scala 22:21]
  wire [15:0] n11_I_t0b; // @[Top.scala 22:21]
  wire [15:0] n11_I_t1b; // @[Top.scala 22:21]
  wire [15:0] n11_O; // @[Top.scala 22:21]
  AtomTuple n10 ( // @[Top.scala 18:21]
    .valid_up(n10_valid_up),
    .valid_down(n10_valid_down),
    .I0(n10_I0),
    .I1(n10_I1),
    .O_t0b(n10_O_t0b),
    .O_t1b(n10_O_t1b)
  );
  Mul n11 ( // @[Top.scala 22:21]
    .clock(n11_clock),
    .reset(n11_reset),
    .valid_up(n11_valid_up),
    .valid_down(n11_valid_down),
    .I_t0b(n11_I_t0b),
    .I_t1b(n11_I_t1b),
    .O(n11_O)
  );
  assign valid_down = n11_valid_down; // @[Top.scala 26:16]
  assign O = n11_O; // @[Top.scala 25:7]
  assign n10_valid_up = valid_up; // @[Top.scala 21:18]
  assign n10_I0 = I0; // @[Top.scala 19:12]
  assign n10_I1 = I1; // @[Top.scala 20:12]
  assign n11_clock = clock;
  assign n11_reset = reset;
  assign n11_valid_up = n10_valid_down; // @[Top.scala 24:18]
  assign n11_I_t0b = n10_O_t0b; // @[Top.scala 23:11]
  assign n11_I_t1b = n10_O_t1b; // @[Top.scala 23:11]
endmodule
module Map2S(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I0_0,
  input  [15:0] I0_1,
  input  [15:0] I0_2,
  input  [15:0] I0_3,
  input  [15:0] I0_4,
  input  [15:0] I1_0,
  input  [15:0] I1_1,
  input  [15:0] I1_2,
  input  [15:0] I1_3,
  input  [15:0] I1_4,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4
);
  wire  fst_op_clock; // @[Map2S.scala 9:22]
  wire  fst_op_reset; // @[Map2S.scala 9:22]
  wire  fst_op_valid_up; // @[Map2S.scala 9:22]
  wire  fst_op_valid_down; // @[Map2S.scala 9:22]
  wire [15:0] fst_op_I0; // @[Map2S.scala 9:22]
  wire [15:0] fst_op_I1; // @[Map2S.scala 9:22]
  wire [15:0] fst_op_O; // @[Map2S.scala 9:22]
  wire  other_ops_0_clock; // @[Map2S.scala 10:86]
  wire  other_ops_0_reset; // @[Map2S.scala 10:86]
  wire  other_ops_0_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_0_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_0_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_0_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_0_O; // @[Map2S.scala 10:86]
  wire  other_ops_1_clock; // @[Map2S.scala 10:86]
  wire  other_ops_1_reset; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_1_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_1_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_1_O; // @[Map2S.scala 10:86]
  wire  other_ops_2_clock; // @[Map2S.scala 10:86]
  wire  other_ops_2_reset; // @[Map2S.scala 10:86]
  wire  other_ops_2_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_2_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_2_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_2_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_2_O; // @[Map2S.scala 10:86]
  wire  other_ops_3_clock; // @[Map2S.scala 10:86]
  wire  other_ops_3_reset; // @[Map2S.scala 10:86]
  wire  other_ops_3_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_3_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_3_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_3_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_3_O; // @[Map2S.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[Map2S.scala 26:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[Map2S.scala 26:83]
  wire  _T_2 = _T_1 & other_ops_2_valid_down; // @[Map2S.scala 26:83]
  Module_0 fst_op ( // @[Map2S.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I0(fst_op_I0),
    .I1(fst_op_I1),
    .O(fst_op_O)
  );
  Module_0 other_ops_0 ( // @[Map2S.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I0(other_ops_0_I0),
    .I1(other_ops_0_I1),
    .O(other_ops_0_O)
  );
  Module_0 other_ops_1 ( // @[Map2S.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I0(other_ops_1_I0),
    .I1(other_ops_1_I1),
    .O(other_ops_1_O)
  );
  Module_0 other_ops_2 ( // @[Map2S.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I0(other_ops_2_I0),
    .I1(other_ops_2_I1),
    .O(other_ops_2_O)
  );
  Module_0 other_ops_3 ( // @[Map2S.scala 10:86]
    .clock(other_ops_3_clock),
    .reset(other_ops_3_reset),
    .valid_up(other_ops_3_valid_up),
    .valid_down(other_ops_3_valid_down),
    .I0(other_ops_3_I0),
    .I1(other_ops_3_I1),
    .O(other_ops_3_O)
  );
  assign valid_down = _T_2 & other_ops_3_valid_down; // @[Map2S.scala 26:14]
  assign O_0 = fst_op_O; // @[Map2S.scala 19:8]
  assign O_1 = other_ops_0_O; // @[Map2S.scala 24:12]
  assign O_2 = other_ops_1_O; // @[Map2S.scala 24:12]
  assign O_3 = other_ops_2_O; // @[Map2S.scala 24:12]
  assign O_4 = other_ops_3_O; // @[Map2S.scala 24:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[Map2S.scala 16:19]
  assign fst_op_I0 = I0_0; // @[Map2S.scala 17:13]
  assign fst_op_I1 = I1_0; // @[Map2S.scala 18:13]
  assign other_ops_0_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_0_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_0_I0 = I0_1; // @[Map2S.scala 22:43]
  assign other_ops_0_I1 = I1_1; // @[Map2S.scala 23:43]
  assign other_ops_1_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_1_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_1_I0 = I0_2; // @[Map2S.scala 22:43]
  assign other_ops_1_I1 = I1_2; // @[Map2S.scala 23:43]
  assign other_ops_2_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_2_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_2_I0 = I0_3; // @[Map2S.scala 22:43]
  assign other_ops_2_I1 = I1_3; // @[Map2S.scala 23:43]
  assign other_ops_3_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_3_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_3_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_3_I0 = I0_4; // @[Map2S.scala 22:43]
  assign other_ops_3_I1 = I1_4; // @[Map2S.scala 23:43]
endmodule
module Map2T(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I0_0,
  input  [15:0] I0_1,
  input  [15:0] I0_2,
  input  [15:0] I0_3,
  input  [15:0] I0_4,
  input  [15:0] I1_0,
  input  [15:0] I1_1,
  input  [15:0] I1_2,
  input  [15:0] I1_3,
  input  [15:0] I1_4,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4
);
  wire  op_clock; // @[Map2T.scala 8:20]
  wire  op_reset; // @[Map2T.scala 8:20]
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_1; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_2; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_3; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_4; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_0; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_1; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_2; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_3; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_4; // @[Map2T.scala 8:20]
  wire [15:0] op_O_0; // @[Map2T.scala 8:20]
  wire [15:0] op_O_1; // @[Map2T.scala 8:20]
  wire [15:0] op_O_2; // @[Map2T.scala 8:20]
  wire [15:0] op_O_3; // @[Map2T.scala 8:20]
  wire [15:0] op_O_4; // @[Map2T.scala 8:20]
  Map2S op ( // @[Map2T.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0(op_I0_0),
    .I0_1(op_I0_1),
    .I0_2(op_I0_2),
    .I0_3(op_I0_3),
    .I0_4(op_I0_4),
    .I1_0(op_I1_0),
    .I1_1(op_I1_1),
    .I1_2(op_I1_2),
    .I1_3(op_I1_3),
    .I1_4(op_I1_4),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2),
    .O_3(op_O_3),
    .O_4(op_O_4)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0 = op_O_0; // @[Map2T.scala 17:7]
  assign O_1 = op_O_1; // @[Map2T.scala 17:7]
  assign O_2 = op_O_2; // @[Map2T.scala 17:7]
  assign O_3 = op_O_3; // @[Map2T.scala 17:7]
  assign O_4 = op_O_4; // @[Map2T.scala 17:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0 = I0_0; // @[Map2T.scala 15:11]
  assign op_I0_1 = I0_1; // @[Map2T.scala 15:11]
  assign op_I0_2 = I0_2; // @[Map2T.scala 15:11]
  assign op_I0_3 = I0_3; // @[Map2T.scala 15:11]
  assign op_I0_4 = I0_4; // @[Map2T.scala 15:11]
  assign op_I1_0 = I1_0; // @[Map2T.scala 16:11]
  assign op_I1_1 = I1_1; // @[Map2T.scala 16:11]
  assign op_I1_2 = I1_2; // @[Map2T.scala 16:11]
  assign op_I1_3 = I1_3; // @[Map2T.scala 16:11]
  assign op_I1_4 = I1_4; // @[Map2T.scala 16:11]
endmodule
module AddNoValid(
  input  [15:0] I_t0b,
  input  [15:0] I_t1b,
  output [15:0] O
);
  assign O = I_t0b + I_t1b; // @[Arithmetic.scala 122:7]
endmodule
module ReduceS(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  input  [15:0] I_1,
  input  [15:0] I_2,
  input  [15:0] I_3,
  input  [15:0] I_4,
  output [15:0] O_0
);
  wire [15:0] AddNoValid_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_O; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_1_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_1_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_1_O; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_2_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_2_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_2_O; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_3_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_3_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_3_O; // @[ReduceS.scala 20:43]
  reg [15:0] _T; // @[ReduceS.scala 27:24]
  reg [31:0] _RAND_0;
  reg [15:0] _T_1; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_1;
  reg [15:0] _T_2; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_2;
  reg [15:0] _T_3; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_3;
  reg [15:0] _T_4; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_4;
  reg [15:0] _T_5; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_5;
  reg  _T_6; // @[ReduceS.scala 47:32]
  reg [31:0] _RAND_6;
  reg  _T_7; // @[ReduceS.scala 47:24]
  reg [31:0] _RAND_7;
  AddNoValid AddNoValid ( // @[ReduceS.scala 20:43]
    .I_t0b(AddNoValid_I_t0b),
    .I_t1b(AddNoValid_I_t1b),
    .O(AddNoValid_O)
  );
  AddNoValid AddNoValid_1 ( // @[ReduceS.scala 20:43]
    .I_t0b(AddNoValid_1_I_t0b),
    .I_t1b(AddNoValid_1_I_t1b),
    .O(AddNoValid_1_O)
  );
  AddNoValid AddNoValid_2 ( // @[ReduceS.scala 20:43]
    .I_t0b(AddNoValid_2_I_t0b),
    .I_t1b(AddNoValid_2_I_t1b),
    .O(AddNoValid_2_O)
  );
  AddNoValid AddNoValid_3 ( // @[ReduceS.scala 20:43]
    .I_t0b(AddNoValid_3_I_t0b),
    .I_t1b(AddNoValid_3_I_t1b),
    .O(AddNoValid_3_O)
  );
  assign valid_down = _T_7; // @[ReduceS.scala 47:14]
  assign O_0 = _T; // @[ReduceS.scala 27:14]
  assign AddNoValid_I_t0b = _T_2; // @[ReduceS.scala 43:18]
  assign AddNoValid_I_t1b = AddNoValid_1_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_1_I_t0b = AddNoValid_2_O; // @[ReduceS.scala 31:18]
  assign AddNoValid_1_I_t1b = AddNoValid_3_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_2_I_t0b = _T_3; // @[ReduceS.scala 43:18]
  assign AddNoValid_2_I_t1b = _T_1; // @[ReduceS.scala 43:18]
  assign AddNoValid_3_I_t0b = _T_5; // @[ReduceS.scala 43:18]
  assign AddNoValid_3_I_t1b = _T_4; // @[ReduceS.scala 43:18]
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
  _T_1 = _RAND_1[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_2 = _RAND_2[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_3 = _RAND_3[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_4 = _RAND_4[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_5 = _RAND_5[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_6 = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_7 = _RAND_7[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    _T <= AddNoValid_O;
    _T_1 <= I_0;
    _T_2 <= I_1;
    _T_3 <= I_2;
    _T_4 <= I_3;
    _T_5 <= I_4;
    if (reset) begin
      _T_6 <= 1'h0;
    end else begin
      _T_6 <= valid_up;
    end
    _T_7 <= _T_6;
  end
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
  input  [15:0] I_4,
  output [15:0] O_0
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0; // @[MapT.scala 8:20]
  wire [15:0] op_I_1; // @[MapT.scala 8:20]
  wire [15:0] op_I_2; // @[MapT.scala 8:20]
  wire [15:0] op_I_3; // @[MapT.scala 8:20]
  wire [15:0] op_I_4; // @[MapT.scala 8:20]
  wire [15:0] op_O_0; // @[MapT.scala 8:20]
  ReduceS op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0(op_I_0),
    .I_1(op_I_1),
    .I_2(op_I_2),
    .I_3(op_I_3),
    .I_4(op_I_4),
    .O_0(op_O_0)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0 = op_O_0; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0 = I_0; // @[MapT.scala 14:10]
  assign op_I_1 = I_1; // @[MapT.scala 14:10]
  assign op_I_2 = I_2; // @[MapT.scala 14:10]
  assign op_I_3 = I_3; // @[MapT.scala 14:10]
  assign op_I_4 = I_4; // @[MapT.scala 14:10]
endmodule
module NestedCounters(
  input   CE,
  output  valid
);
  assign valid = CE; // @[NestedCounters.scala 65:13]
endmodule
module NestedCounters_1(
  input   CE,
  output  valid
);
  wire  NestedCounters_CE; // @[NestedCounters.scala 53:31]
  wire  NestedCounters_valid; // @[NestedCounters.scala 53:31]
  NestedCounters NestedCounters ( // @[NestedCounters.scala 53:31]
    .CE(NestedCounters_CE),
    .valid(NestedCounters_valid)
  );
  assign valid = NestedCounters_valid; // @[NestedCounters.scala 56:11]
  assign NestedCounters_CE = CE; // @[NestedCounters.scala 57:22]
endmodule
module NestedCountersWithNumValid(
  input   CE,
  output  valid
);
  wire  NestedCounters_CE; // @[NestedCounters.scala 20:44]
  wire  NestedCounters_valid; // @[NestedCounters.scala 20:44]
  NestedCounters_1 NestedCounters ( // @[NestedCounters.scala 20:44]
    .CE(NestedCounters_CE),
    .valid(NestedCounters_valid)
  );
  assign valid = NestedCounters_valid; // @[NestedCounters.scala 22:9]
  assign NestedCounters_CE = CE; // @[NestedCounters.scala 21:27]
endmodule
module MapSNoValid(
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b,
  output [15:0] O_0
);
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 28:22]
  wire [15:0] fst_op_I_t1b; // @[MapS.scala 28:22]
  wire [15:0] fst_op_O; // @[MapS.scala 28:22]
  AddNoValid fst_op ( // @[MapS.scala 28:22]
    .I_t0b(fst_op_I_t0b),
    .I_t1b(fst_op_I_t1b),
    .O(fst_op_O)
  );
  assign O_0 = fst_op_O; // @[MapS.scala 35:8]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 34:12]
  assign fst_op_I_t1b = I_0_t1b; // @[MapS.scala 34:12]
endmodule
module ReduceT(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  output [15:0] O_0
);
  wire  NestedCountersWithNumValid_CE; // @[ReduceT.scala 22:34]
  wire  NestedCountersWithNumValid_valid; // @[ReduceT.scala 22:34]
  wire [15:0] MapSNoValid_I_0_t0b; // @[ReduceT.scala 25:25]
  wire [15:0] MapSNoValid_I_0_t1b; // @[ReduceT.scala 25:25]
  wire [15:0] MapSNoValid_O_0; // @[ReduceT.scala 25:25]
  reg  _T; // @[ReduceT.scala 26:50]
  reg [31:0] _RAND_0;
  reg [7:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire  _T_3 = value == 8'ha7; // @[Counter.scala 38:24]
  wire [7:0] _T_5 = value + 8'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value == 8'h0; // @[ReduceT.scala 34:60]
  reg [15:0] _T_7_0; // @[ReduceT.scala 34:76]
  reg [31:0] _RAND_2;
  reg [15:0] _T_9_0; // @[ReduceT.scala 35:24]
  reg [31:0] _RAND_3;
  reg  _T_10; // @[ReduceT.scala 37:35]
  reg [31:0] _RAND_4;
  reg [15:0] _T_11_0; // @[ReduceT.scala 44:83]
  reg [31:0] _RAND_5;
  reg  _T_12; // @[ReduceT.scala 51:28]
  reg [31:0] _RAND_6;
  wire  _T_14 = _T_12 | _T_3; // @[ReduceT.scala 52:28]
  reg [15:0] _T_15_0; // @[ReduceT.scala 56:15]
  reg [31:0] _RAND_7;
  NestedCountersWithNumValid NestedCountersWithNumValid ( // @[ReduceT.scala 22:34]
    .CE(NestedCountersWithNumValid_CE),
    .valid(NestedCountersWithNumValid_valid)
  );
  MapSNoValid MapSNoValid ( // @[ReduceT.scala 25:25]
    .I_0_t0b(MapSNoValid_I_0_t0b),
    .I_0_t1b(MapSNoValid_I_0_t1b),
    .O_0(MapSNoValid_O_0)
  );
  assign valid_down = _T_12; // @[ReduceT.scala 53:16]
  assign O_0 = _T_15_0; // @[ReduceT.scala 56:5]
  assign NestedCountersWithNumValid_CE = _T_10; // @[ReduceT.scala 37:25]
  assign MapSNoValid_I_0_t0b = _T_11_0; // @[ReduceT.scala 44:55]
  assign MapSNoValid_I_0_t1b = _T_9_0; // @[ReduceT.scala 45:55]
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
  value = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_7_0 = _RAND_2[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_9_0 = _RAND_3[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_10 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_11_0 = _RAND_5[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_12 = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_15_0 = _RAND_7[15:0];
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
      value <= 8'h0;
    end else if (_T) begin
      if (_T_3) begin
        value <= 8'h0;
      end else begin
        value <= _T_5;
      end
    end
    _T_7_0 <= I_0;
    if (NestedCountersWithNumValid_valid) begin
      if (_T_6) begin
        _T_9_0 <= _T_7_0;
      end else begin
        _T_9_0 <= MapSNoValid_O_0;
      end
    end
    _T_10 <= valid_up;
    _T_11_0 <= I_0;
    if (reset) begin
      _T_12 <= 1'h0;
    end else begin
      _T_12 <= _T_14;
    end
    _T_15_0 <= MapSNoValid_O_0;
  end
endmodule
module Passthrough(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  output [15:0] O
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O = I_0; // @[Passthrough.scala 17:68]
endmodule
module FIFO_2(
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
module Top(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I0_0,
  input  [15:0] I0_1,
  input  [15:0] I0_2,
  input  [15:0] I0_3,
  input  [15:0] I0_4,
  input  [15:0] I1_0,
  input  [15:0] I1_1,
  input  [15:0] I1_2,
  input  [15:0] I1_3,
  input  [15:0] I1_4,
  output [15:0] O
);
  wire  n1_clock; // @[Top.scala 33:20]
  wire  n1_reset; // @[Top.scala 33:20]
  wire  n1_valid_up; // @[Top.scala 33:20]
  wire  n1_valid_down; // @[Top.scala 33:20]
  wire [15:0] n1_I_0; // @[Top.scala 33:20]
  wire [15:0] n1_I_1; // @[Top.scala 33:20]
  wire [15:0] n1_I_2; // @[Top.scala 33:20]
  wire [15:0] n1_I_3; // @[Top.scala 33:20]
  wire [15:0] n1_I_4; // @[Top.scala 33:20]
  wire [15:0] n1_O_0; // @[Top.scala 33:20]
  wire [15:0] n1_O_1; // @[Top.scala 33:20]
  wire [15:0] n1_O_2; // @[Top.scala 33:20]
  wire [15:0] n1_O_3; // @[Top.scala 33:20]
  wire [15:0] n1_O_4; // @[Top.scala 33:20]
  wire  n3_clock; // @[Top.scala 36:20]
  wire  n3_reset; // @[Top.scala 36:20]
  wire  n3_valid_up; // @[Top.scala 36:20]
  wire  n3_valid_down; // @[Top.scala 36:20]
  wire [15:0] n3_I_0; // @[Top.scala 36:20]
  wire [15:0] n3_I_1; // @[Top.scala 36:20]
  wire [15:0] n3_I_2; // @[Top.scala 36:20]
  wire [15:0] n3_I_3; // @[Top.scala 36:20]
  wire [15:0] n3_I_4; // @[Top.scala 36:20]
  wire [15:0] n3_O_0; // @[Top.scala 36:20]
  wire [15:0] n3_O_1; // @[Top.scala 36:20]
  wire [15:0] n3_O_2; // @[Top.scala 36:20]
  wire [15:0] n3_O_3; // @[Top.scala 36:20]
  wire [15:0] n3_O_4; // @[Top.scala 36:20]
  wire  n4_clock; // @[Top.scala 39:20]
  wire  n4_reset; // @[Top.scala 39:20]
  wire  n4_valid_up; // @[Top.scala 39:20]
  wire  n4_valid_down; // @[Top.scala 39:20]
  wire [15:0] n4_I0_0; // @[Top.scala 39:20]
  wire [15:0] n4_I0_1; // @[Top.scala 39:20]
  wire [15:0] n4_I0_2; // @[Top.scala 39:20]
  wire [15:0] n4_I0_3; // @[Top.scala 39:20]
  wire [15:0] n4_I0_4; // @[Top.scala 39:20]
  wire [15:0] n4_I1_0; // @[Top.scala 39:20]
  wire [15:0] n4_I1_1; // @[Top.scala 39:20]
  wire [15:0] n4_I1_2; // @[Top.scala 39:20]
  wire [15:0] n4_I1_3; // @[Top.scala 39:20]
  wire [15:0] n4_I1_4; // @[Top.scala 39:20]
  wire [15:0] n4_O_0; // @[Top.scala 39:20]
  wire [15:0] n4_O_1; // @[Top.scala 39:20]
  wire [15:0] n4_O_2; // @[Top.scala 39:20]
  wire [15:0] n4_O_3; // @[Top.scala 39:20]
  wire [15:0] n4_O_4; // @[Top.scala 39:20]
  wire  n16_clock; // @[Top.scala 43:21]
  wire  n16_reset; // @[Top.scala 43:21]
  wire  n16_valid_up; // @[Top.scala 43:21]
  wire  n16_valid_down; // @[Top.scala 43:21]
  wire [15:0] n16_I_0; // @[Top.scala 43:21]
  wire [15:0] n16_I_1; // @[Top.scala 43:21]
  wire [15:0] n16_I_2; // @[Top.scala 43:21]
  wire [15:0] n16_I_3; // @[Top.scala 43:21]
  wire [15:0] n16_I_4; // @[Top.scala 43:21]
  wire [15:0] n16_O_0; // @[Top.scala 43:21]
  wire  n19_clock; // @[Top.scala 46:21]
  wire  n19_reset; // @[Top.scala 46:21]
  wire  n19_valid_up; // @[Top.scala 46:21]
  wire  n19_valid_down; // @[Top.scala 46:21]
  wire [15:0] n19_I_0; // @[Top.scala 46:21]
  wire [15:0] n19_O_0; // @[Top.scala 46:21]
  wire  n20_valid_up; // @[Top.scala 49:21]
  wire  n20_valid_down; // @[Top.scala 49:21]
  wire [15:0] n20_I_0; // @[Top.scala 49:21]
  wire [15:0] n20_O; // @[Top.scala 49:21]
  wire  n21_clock; // @[Top.scala 52:21]
  wire  n21_reset; // @[Top.scala 52:21]
  wire  n21_valid_up; // @[Top.scala 52:21]
  wire  n21_valid_down; // @[Top.scala 52:21]
  wire [15:0] n21_I; // @[Top.scala 52:21]
  wire [15:0] n21_O; // @[Top.scala 52:21]
  wire  n22_clock; // @[Top.scala 55:21]
  wire  n22_reset; // @[Top.scala 55:21]
  wire  n22_valid_up; // @[Top.scala 55:21]
  wire  n22_valid_down; // @[Top.scala 55:21]
  wire [15:0] n22_I; // @[Top.scala 55:21]
  wire [15:0] n22_O; // @[Top.scala 55:21]
  wire  n23_clock; // @[Top.scala 58:21]
  wire  n23_reset; // @[Top.scala 58:21]
  wire  n23_valid_up; // @[Top.scala 58:21]
  wire  n23_valid_down; // @[Top.scala 58:21]
  wire [15:0] n23_I; // @[Top.scala 58:21]
  wire [15:0] n23_O; // @[Top.scala 58:21]
  FIFO n1 ( // @[Top.scala 33:20]
    .clock(n1_clock),
    .reset(n1_reset),
    .valid_up(n1_valid_up),
    .valid_down(n1_valid_down),
    .I_0(n1_I_0),
    .I_1(n1_I_1),
    .I_2(n1_I_2),
    .I_3(n1_I_3),
    .I_4(n1_I_4),
    .O_0(n1_O_0),
    .O_1(n1_O_1),
    .O_2(n1_O_2),
    .O_3(n1_O_3),
    .O_4(n1_O_4)
  );
  FIFO n3 ( // @[Top.scala 36:20]
    .clock(n3_clock),
    .reset(n3_reset),
    .valid_up(n3_valid_up),
    .valid_down(n3_valid_down),
    .I_0(n3_I_0),
    .I_1(n3_I_1),
    .I_2(n3_I_2),
    .I_3(n3_I_3),
    .I_4(n3_I_4),
    .O_0(n3_O_0),
    .O_1(n3_O_1),
    .O_2(n3_O_2),
    .O_3(n3_O_3),
    .O_4(n3_O_4)
  );
  Map2T n4 ( // @[Top.scala 39:20]
    .clock(n4_clock),
    .reset(n4_reset),
    .valid_up(n4_valid_up),
    .valid_down(n4_valid_down),
    .I0_0(n4_I0_0),
    .I0_1(n4_I0_1),
    .I0_2(n4_I0_2),
    .I0_3(n4_I0_3),
    .I0_4(n4_I0_4),
    .I1_0(n4_I1_0),
    .I1_1(n4_I1_1),
    .I1_2(n4_I1_2),
    .I1_3(n4_I1_3),
    .I1_4(n4_I1_4),
    .O_0(n4_O_0),
    .O_1(n4_O_1),
    .O_2(n4_O_2),
    .O_3(n4_O_3),
    .O_4(n4_O_4)
  );
  MapT n16 ( // @[Top.scala 43:21]
    .clock(n16_clock),
    .reset(n16_reset),
    .valid_up(n16_valid_up),
    .valid_down(n16_valid_down),
    .I_0(n16_I_0),
    .I_1(n16_I_1),
    .I_2(n16_I_2),
    .I_3(n16_I_3),
    .I_4(n16_I_4),
    .O_0(n16_O_0)
  );
  ReduceT n19 ( // @[Top.scala 46:21]
    .clock(n19_clock),
    .reset(n19_reset),
    .valid_up(n19_valid_up),
    .valid_down(n19_valid_down),
    .I_0(n19_I_0),
    .O_0(n19_O_0)
  );
  Passthrough n20 ( // @[Top.scala 49:21]
    .valid_up(n20_valid_up),
    .valid_down(n20_valid_down),
    .I_0(n20_I_0),
    .O(n20_O)
  );
  FIFO_2 n21 ( // @[Top.scala 52:21]
    .clock(n21_clock),
    .reset(n21_reset),
    .valid_up(n21_valid_up),
    .valid_down(n21_valid_down),
    .I(n21_I),
    .O(n21_O)
  );
  FIFO_2 n22 ( // @[Top.scala 55:21]
    .clock(n22_clock),
    .reset(n22_reset),
    .valid_up(n22_valid_up),
    .valid_down(n22_valid_down),
    .I(n22_I),
    .O(n22_O)
  );
  FIFO_2 n23 ( // @[Top.scala 58:21]
    .clock(n23_clock),
    .reset(n23_reset),
    .valid_up(n23_valid_up),
    .valid_down(n23_valid_down),
    .I(n23_I),
    .O(n23_O)
  );
  assign valid_down = n23_valid_down; // @[Top.scala 62:16]
  assign O = n23_O; // @[Top.scala 61:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 35:17]
  assign n1_I_0 = I0_0; // @[Top.scala 34:10]
  assign n1_I_1 = I0_1; // @[Top.scala 34:10]
  assign n1_I_2 = I0_2; // @[Top.scala 34:10]
  assign n1_I_3 = I0_3; // @[Top.scala 34:10]
  assign n1_I_4 = I0_4; // @[Top.scala 34:10]
  assign n3_clock = clock;
  assign n3_reset = reset;
  assign n3_valid_up = valid_up; // @[Top.scala 38:17]
  assign n3_I_0 = I1_0; // @[Top.scala 37:10]
  assign n3_I_1 = I1_1; // @[Top.scala 37:10]
  assign n3_I_2 = I1_2; // @[Top.scala 37:10]
  assign n3_I_3 = I1_3; // @[Top.scala 37:10]
  assign n3_I_4 = I1_4; // @[Top.scala 37:10]
  assign n4_clock = clock;
  assign n4_reset = reset;
  assign n4_valid_up = n1_valid_down & n3_valid_down; // @[Top.scala 42:17]
  assign n4_I0_0 = n1_O_0; // @[Top.scala 40:11]
  assign n4_I0_1 = n1_O_1; // @[Top.scala 40:11]
  assign n4_I0_2 = n1_O_2; // @[Top.scala 40:11]
  assign n4_I0_3 = n1_O_3; // @[Top.scala 40:11]
  assign n4_I0_4 = n1_O_4; // @[Top.scala 40:11]
  assign n4_I1_0 = n3_O_0; // @[Top.scala 41:11]
  assign n4_I1_1 = n3_O_1; // @[Top.scala 41:11]
  assign n4_I1_2 = n3_O_2; // @[Top.scala 41:11]
  assign n4_I1_3 = n3_O_3; // @[Top.scala 41:11]
  assign n4_I1_4 = n3_O_4; // @[Top.scala 41:11]
  assign n16_clock = clock;
  assign n16_reset = reset;
  assign n16_valid_up = n4_valid_down; // @[Top.scala 45:18]
  assign n16_I_0 = n4_O_0; // @[Top.scala 44:11]
  assign n16_I_1 = n4_O_1; // @[Top.scala 44:11]
  assign n16_I_2 = n4_O_2; // @[Top.scala 44:11]
  assign n16_I_3 = n4_O_3; // @[Top.scala 44:11]
  assign n16_I_4 = n4_O_4; // @[Top.scala 44:11]
  assign n19_clock = clock;
  assign n19_reset = reset;
  assign n19_valid_up = n16_valid_down; // @[Top.scala 48:18]
  assign n19_I_0 = n16_O_0; // @[Top.scala 47:11]
  assign n20_valid_up = n19_valid_down; // @[Top.scala 51:18]
  assign n20_I_0 = n19_O_0; // @[Top.scala 50:11]
  assign n21_clock = clock;
  assign n21_reset = reset;
  assign n21_valid_up = n20_valid_down; // @[Top.scala 54:18]
  assign n21_I = n20_O; // @[Top.scala 53:11]
  assign n22_clock = clock;
  assign n22_reset = reset;
  assign n22_valid_up = n21_valid_down; // @[Top.scala 57:18]
  assign n22_I = n21_O; // @[Top.scala 56:11]
  assign n23_clock = clock;
  assign n23_reset = reset;
  assign n23_valid_up = n22_valid_down; // @[Top.scala 60:18]
  assign n23_I = n22_O; // @[Top.scala 59:11]
endmodule
