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
  wire  n6_valid_up; // @[Top.scala 19:20]
  wire  n6_valid_down; // @[Top.scala 19:20]
  wire [15:0] n6_I0; // @[Top.scala 19:20]
  wire [15:0] n6_O_t0b; // @[Top.scala 19:20]
  wire  n7_valid_up; // @[Top.scala 23:20]
  wire  n7_valid_down; // @[Top.scala 23:20]
  wire [15:0] n7_I_t0b; // @[Top.scala 23:20]
  wire [15:0] n7_O; // @[Top.scala 23:20]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n10_valid_up; // @[Top.scala 27:21]
  wire  n10_valid_down; // @[Top.scala 27:21]
  wire [15:0] n10_I0; // @[Top.scala 27:21]
  wire [15:0] n10_I1; // @[Top.scala 27:21]
  wire [15:0] n10_O_t0b; // @[Top.scala 27:21]
  wire [15:0] n10_O_t1b; // @[Top.scala 27:21]
  wire  n11_valid_up; // @[Top.scala 31:21]
  wire  n11_valid_down; // @[Top.scala 31:21]
  wire [15:0] n11_I_t0b; // @[Top.scala 31:21]
  wire [15:0] n11_I_t1b; // @[Top.scala 31:21]
  wire [15:0] n11_O; // @[Top.scala 31:21]
  wire  n13_valid_up; // @[Top.scala 34:21]
  wire  n13_valid_down; // @[Top.scala 34:21]
  wire [15:0] n13_I0; // @[Top.scala 34:21]
  wire [15:0] n13_I1; // @[Top.scala 34:21]
  wire [15:0] n13_O_t0b; // @[Top.scala 34:21]
  wire [15:0] n13_O_t1b; // @[Top.scala 34:21]
  wire  n14_valid_up; // @[Top.scala 38:21]
  wire  n14_valid_down; // @[Top.scala 38:21]
  wire [15:0] n14_I0; // @[Top.scala 38:21]
  wire [15:0] n14_I1_t0b; // @[Top.scala 38:21]
  wire [15:0] n14_I1_t1b; // @[Top.scala 38:21]
  wire [15:0] n14_O_t0b; // @[Top.scala 38:21]
  wire [15:0] n14_O_t1b_t0b; // @[Top.scala 38:21]
  wire [15:0] n14_O_t1b_t1b; // @[Top.scala 38:21]
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
  AtomTuple n6 ( // @[Top.scala 19:20]
    .valid_up(n6_valid_up),
    .valid_down(n6_valid_down),
    .I0(n6_I0),
    .O_t0b(n6_O_t0b)
  );
  RShift n7 ( // @[Top.scala 23:20]
    .valid_up(n7_valid_up),
    .valid_down(n7_valid_down),
    .I_t0b(n7_I_t0b),
    .O(n7_O)
  );
  InitialDelayCounter InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n10 ( // @[Top.scala 27:21]
    .valid_up(n10_valid_up),
    .valid_down(n10_valid_down),
    .I0(n10_I0),
    .I1(n10_I1),
    .O_t0b(n10_O_t0b),
    .O_t1b(n10_O_t1b)
  );
  Add n11 ( // @[Top.scala 31:21]
    .valid_up(n11_valid_up),
    .valid_down(n11_valid_down),
    .I_t0b(n11_I_t0b),
    .I_t1b(n11_I_t1b),
    .O(n11_O)
  );
  AtomTuple_1 n13 ( // @[Top.scala 34:21]
    .valid_up(n13_valid_up),
    .valid_down(n13_valid_down),
    .I0(n13_I0),
    .I1(n13_I1),
    .O_t0b(n13_O_t0b),
    .O_t1b(n13_O_t1b)
  );
  AtomTuple_3 n14 ( // @[Top.scala 38:21]
    .valid_up(n14_valid_up),
    .valid_down(n14_valid_down),
    .I0(n14_I0),
    .I1_t0b(n14_I1_t0b),
    .I1_t1b(n14_I1_t1b),
    .O_t0b(n14_O_t0b),
    .O_t1b_t0b(n14_O_t1b_t0b),
    .O_t1b_t1b(n14_O_t1b_t1b)
  );
  assign valid_down = n14_valid_down; // @[Top.scala 43:16]
  assign O_t0b = n14_O_t0b; // @[Top.scala 42:7]
  assign O_t1b_t0b = n14_O_t1b_t0b; // @[Top.scala 42:7]
  assign O_t1b_t1b = n14_O_t1b_t1b; // @[Top.scala 42:7]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n6_valid_up = valid_up & InitialDelayCounter_1_valid_down; // @[Top.scala 22:17]
  assign n6_I0 = I; // @[Top.scala 20:11]
  assign n7_valid_up = n6_valid_down; // @[Top.scala 25:17]
  assign n7_I_t0b = n6_O_t0b; // @[Top.scala 24:10]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n10_valid_up = n7_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 30:18]
  assign n10_I0 = n7_O; // @[Top.scala 28:12]
  assign n10_I1 = 16'h1; // @[Top.scala 29:12]
  assign n11_valid_up = n10_valid_down; // @[Top.scala 33:18]
  assign n11_I_t0b = n10_O_t0b; // @[Top.scala 32:11]
  assign n11_I_t1b = n10_O_t1b; // @[Top.scala 32:11]
  assign n13_valid_up = InitialDelayCounter_valid_down & n11_valid_down; // @[Top.scala 37:18]
  assign n13_I0 = 16'h0; // @[Top.scala 35:12]
  assign n13_I1 = n11_O; // @[Top.scala 36:12]
  assign n14_valid_up = valid_up & n13_valid_down; // @[Top.scala 41:18]
  assign n14_I0 = I; // @[Top.scala 39:12]
  assign n14_I1_t0b = n13_O_t0b; // @[Top.scala 40:12]
  assign n14_I1_t1b = n13_O_t1b; // @[Top.scala 40:12]
endmodule
module MapT(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I,
  output [15:0] O_t0b,
  output [15:0] O_t1b_t0b,
  output [15:0] O_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_0 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I(op_I),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I = I; // @[MapT.scala 14:10]
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
  wire  n17_valid_up; // @[Top.scala 49:21]
  wire  n17_valid_down; // @[Top.scala 49:21]
  wire [15:0] n17_I_t0b; // @[Top.scala 49:21]
  wire [15:0] n17_O; // @[Top.scala 49:21]
  wire  n50_clock; // @[Top.scala 52:21]
  wire  n50_reset; // @[Top.scala 52:21]
  wire  n50_valid_up; // @[Top.scala 52:21]
  wire  n50_valid_down; // @[Top.scala 52:21]
  wire [15:0] n50_I; // @[Top.scala 52:21]
  wire [15:0] n50_O; // @[Top.scala 52:21]
  wire  n38_clock; // @[Top.scala 55:21]
  wire  n38_reset; // @[Top.scala 55:21]
  wire  n38_valid_up; // @[Top.scala 55:21]
  wire  n38_valid_down; // @[Top.scala 55:21]
  wire [15:0] n38_I; // @[Top.scala 55:21]
  wire [15:0] n38_O; // @[Top.scala 55:21]
  wire  n18_valid_up; // @[Top.scala 58:21]
  wire  n18_valid_down; // @[Top.scala 58:21]
  wire [15:0] n18_I_t1b_t0b; // @[Top.scala 58:21]
  wire [15:0] n18_I_t1b_t1b; // @[Top.scala 58:21]
  wire [15:0] n18_O_t0b; // @[Top.scala 58:21]
  wire [15:0] n18_O_t1b; // @[Top.scala 58:21]
  wire  n19_valid_up; // @[Top.scala 61:21]
  wire  n19_valid_down; // @[Top.scala 61:21]
  wire [15:0] n19_I_t0b; // @[Top.scala 61:21]
  wire [15:0] n19_O; // @[Top.scala 61:21]
  wire  n20_valid_up; // @[Top.scala 64:21]
  wire  n20_valid_down; // @[Top.scala 64:21]
  wire [15:0] n20_I_t1b; // @[Top.scala 64:21]
  wire [15:0] n20_O; // @[Top.scala 64:21]
  wire  n21_valid_up; // @[Top.scala 67:21]
  wire  n21_valid_down; // @[Top.scala 67:21]
  wire [15:0] n21_I0; // @[Top.scala 67:21]
  wire [15:0] n21_I1; // @[Top.scala 67:21]
  wire [15:0] n21_O_t0b; // @[Top.scala 67:21]
  wire [15:0] n21_O_t1b; // @[Top.scala 67:21]
  wire  n22_valid_up; // @[Top.scala 71:21]
  wire  n22_valid_down; // @[Top.scala 71:21]
  wire [15:0] n22_I_t0b; // @[Top.scala 71:21]
  wire [15:0] n22_I_t1b; // @[Top.scala 71:21]
  wire [15:0] n22_O; // @[Top.scala 71:21]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n25_valid_up; // @[Top.scala 75:21]
  wire  n25_valid_down; // @[Top.scala 75:21]
  wire [15:0] n25_I0; // @[Top.scala 75:21]
  wire [15:0] n25_O_t0b; // @[Top.scala 75:21]
  wire  n26_valid_up; // @[Top.scala 79:21]
  wire  n26_valid_down; // @[Top.scala 79:21]
  wire [15:0] n26_I_t0b; // @[Top.scala 79:21]
  wire [15:0] n26_O; // @[Top.scala 79:21]
  wire  n27_valid_up; // @[Top.scala 82:21]
  wire  n27_valid_down; // @[Top.scala 82:21]
  wire [15:0] n27_I0; // @[Top.scala 82:21]
  wire [15:0] n27_I1; // @[Top.scala 82:21]
  wire [15:0] n27_O_t0b; // @[Top.scala 82:21]
  wire [15:0] n27_O_t1b; // @[Top.scala 82:21]
  wire  n28_valid_up; // @[Top.scala 86:21]
  wire  n28_valid_down; // @[Top.scala 86:21]
  wire [15:0] n28_I_t0b; // @[Top.scala 86:21]
  wire [15:0] n28_I_t1b; // @[Top.scala 86:21]
  wire [15:0] n28_O; // @[Top.scala 86:21]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n31_valid_up; // @[Top.scala 90:21]
  wire  n31_valid_down; // @[Top.scala 90:21]
  wire [15:0] n31_I0; // @[Top.scala 90:21]
  wire [15:0] n31_I1; // @[Top.scala 90:21]
  wire [15:0] n31_O_t0b; // @[Top.scala 90:21]
  wire [15:0] n31_O_t1b; // @[Top.scala 90:21]
  wire  n32_valid_up; // @[Top.scala 94:21]
  wire  n32_valid_down; // @[Top.scala 94:21]
  wire [15:0] n32_I_t0b; // @[Top.scala 94:21]
  wire [15:0] n32_I_t1b; // @[Top.scala 94:21]
  wire [15:0] n32_O; // @[Top.scala 94:21]
  wire  n33_valid_up; // @[Top.scala 97:21]
  wire  n33_valid_down; // @[Top.scala 97:21]
  wire [15:0] n33_I0; // @[Top.scala 97:21]
  wire [15:0] n33_I1; // @[Top.scala 97:21]
  wire [15:0] n33_O_t0b; // @[Top.scala 97:21]
  wire [15:0] n33_O_t1b; // @[Top.scala 97:21]
  wire  n34_valid_up; // @[Top.scala 101:21]
  wire  n34_valid_down; // @[Top.scala 101:21]
  wire  n34_I0; // @[Top.scala 101:21]
  wire [15:0] n34_I1_t0b; // @[Top.scala 101:21]
  wire [15:0] n34_I1_t1b; // @[Top.scala 101:21]
  wire  n34_O_t0b; // @[Top.scala 101:21]
  wire [15:0] n34_O_t1b_t0b; // @[Top.scala 101:21]
  wire [15:0] n34_O_t1b_t1b; // @[Top.scala 101:21]
  wire  n35_valid_up; // @[Top.scala 105:21]
  wire  n35_valid_down; // @[Top.scala 105:21]
  wire  n35_I_t0b; // @[Top.scala 105:21]
  wire [15:0] n35_I_t1b_t0b; // @[Top.scala 105:21]
  wire [15:0] n35_I_t1b_t1b; // @[Top.scala 105:21]
  wire [15:0] n35_O; // @[Top.scala 105:21]
  wire  n36_valid_up; // @[Top.scala 108:21]
  wire  n36_valid_down; // @[Top.scala 108:21]
  wire [15:0] n36_I0; // @[Top.scala 108:21]
  wire [15:0] n36_I1; // @[Top.scala 108:21]
  wire [15:0] n36_O_t0b; // @[Top.scala 108:21]
  wire [15:0] n36_O_t1b; // @[Top.scala 108:21]
  wire  n37_clock; // @[Top.scala 112:21]
  wire  n37_reset; // @[Top.scala 112:21]
  wire  n37_valid_up; // @[Top.scala 112:21]
  wire  n37_valid_down; // @[Top.scala 112:21]
  wire [15:0] n37_I_t0b; // @[Top.scala 112:21]
  wire [15:0] n37_I_t1b; // @[Top.scala 112:21]
  wire [15:0] n37_O; // @[Top.scala 112:21]
  wire  n39_valid_up; // @[Top.scala 115:21]
  wire  n39_valid_down; // @[Top.scala 115:21]
  wire [15:0] n39_I0; // @[Top.scala 115:21]
  wire [15:0] n39_I1; // @[Top.scala 115:21]
  wire [15:0] n39_O_t0b; // @[Top.scala 115:21]
  wire [15:0] n39_O_t1b; // @[Top.scala 115:21]
  wire  n40_valid_up; // @[Top.scala 119:21]
  wire  n40_valid_down; // @[Top.scala 119:21]
  wire [15:0] n40_I_t0b; // @[Top.scala 119:21]
  wire [15:0] n40_I_t1b; // @[Top.scala 119:21]
  wire [15:0] n40_O; // @[Top.scala 119:21]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n42_valid_up; // @[Top.scala 123:21]
  wire  n42_valid_down; // @[Top.scala 123:21]
  wire [15:0] n42_I0; // @[Top.scala 123:21]
  wire [15:0] n42_I1; // @[Top.scala 123:21]
  wire [15:0] n42_O_t0b; // @[Top.scala 123:21]
  wire [15:0] n42_O_t1b; // @[Top.scala 123:21]
  wire  n43_valid_up; // @[Top.scala 127:21]
  wire  n43_valid_down; // @[Top.scala 127:21]
  wire [15:0] n43_I_t0b; // @[Top.scala 127:21]
  wire [15:0] n43_I_t1b; // @[Top.scala 127:21]
  wire [15:0] n43_O; // @[Top.scala 127:21]
  wire  n44_valid_up; // @[Top.scala 130:21]
  wire  n44_valid_down; // @[Top.scala 130:21]
  wire [15:0] n44_I0; // @[Top.scala 130:21]
  wire [15:0] n44_I1; // @[Top.scala 130:21]
  wire [15:0] n44_O_t0b; // @[Top.scala 130:21]
  wire [15:0] n44_O_t1b; // @[Top.scala 130:21]
  wire  n45_valid_up; // @[Top.scala 134:21]
  wire  n45_valid_down; // @[Top.scala 134:21]
  wire [15:0] n45_I0; // @[Top.scala 134:21]
  wire [15:0] n45_I1; // @[Top.scala 134:21]
  wire [15:0] n45_O_t0b; // @[Top.scala 134:21]
  wire [15:0] n45_O_t1b; // @[Top.scala 134:21]
  wire  n46_valid_up; // @[Top.scala 138:21]
  wire  n46_valid_down; // @[Top.scala 138:21]
  wire [15:0] n46_I0_t0b; // @[Top.scala 138:21]
  wire [15:0] n46_I0_t1b; // @[Top.scala 138:21]
  wire [15:0] n46_I1_t0b; // @[Top.scala 138:21]
  wire [15:0] n46_I1_t1b; // @[Top.scala 138:21]
  wire [15:0] n46_O_t0b_t0b; // @[Top.scala 138:21]
  wire [15:0] n46_O_t0b_t1b; // @[Top.scala 138:21]
  wire [15:0] n46_O_t1b_t0b; // @[Top.scala 138:21]
  wire [15:0] n46_O_t1b_t1b; // @[Top.scala 138:21]
  wire  n47_clock; // @[Top.scala 142:21]
  wire  n47_reset; // @[Top.scala 142:21]
  wire  n47_valid_up; // @[Top.scala 142:21]
  wire  n47_valid_down; // @[Top.scala 142:21]
  wire [15:0] n47_I_t0b_t0b; // @[Top.scala 142:21]
  wire [15:0] n47_I_t0b_t1b; // @[Top.scala 142:21]
  wire [15:0] n47_I_t1b_t0b; // @[Top.scala 142:21]
  wire [15:0] n47_I_t1b_t1b; // @[Top.scala 142:21]
  wire [15:0] n47_O_t0b_t0b; // @[Top.scala 142:21]
  wire [15:0] n47_O_t0b_t1b; // @[Top.scala 142:21]
  wire [15:0] n47_O_t1b_t0b; // @[Top.scala 142:21]
  wire [15:0] n47_O_t1b_t1b; // @[Top.scala 142:21]
  wire  n48_valid_up; // @[Top.scala 145:21]
  wire  n48_valid_down; // @[Top.scala 145:21]
  wire  n48_I0; // @[Top.scala 145:21]
  wire [15:0] n48_I1_t0b_t0b; // @[Top.scala 145:21]
  wire [15:0] n48_I1_t0b_t1b; // @[Top.scala 145:21]
  wire [15:0] n48_I1_t1b_t0b; // @[Top.scala 145:21]
  wire [15:0] n48_I1_t1b_t1b; // @[Top.scala 145:21]
  wire  n48_O_t0b; // @[Top.scala 145:21]
  wire [15:0] n48_O_t1b_t0b_t0b; // @[Top.scala 145:21]
  wire [15:0] n48_O_t1b_t0b_t1b; // @[Top.scala 145:21]
  wire [15:0] n48_O_t1b_t1b_t0b; // @[Top.scala 145:21]
  wire [15:0] n48_O_t1b_t1b_t1b; // @[Top.scala 145:21]
  wire  n49_valid_up; // @[Top.scala 149:21]
  wire  n49_valid_down; // @[Top.scala 149:21]
  wire  n49_I_t0b; // @[Top.scala 149:21]
  wire [15:0] n49_I_t1b_t0b_t0b; // @[Top.scala 149:21]
  wire [15:0] n49_I_t1b_t0b_t1b; // @[Top.scala 149:21]
  wire [15:0] n49_I_t1b_t1b_t0b; // @[Top.scala 149:21]
  wire [15:0] n49_I_t1b_t1b_t1b; // @[Top.scala 149:21]
  wire [15:0] n49_O_t0b; // @[Top.scala 149:21]
  wire [15:0] n49_O_t1b; // @[Top.scala 149:21]
  wire  n51_valid_up; // @[Top.scala 152:21]
  wire  n51_valid_down; // @[Top.scala 152:21]
  wire [15:0] n51_I0; // @[Top.scala 152:21]
  wire [15:0] n51_I1_t0b; // @[Top.scala 152:21]
  wire [15:0] n51_I1_t1b; // @[Top.scala 152:21]
  wire [15:0] n51_O_t0b; // @[Top.scala 152:21]
  wire [15:0] n51_O_t1b_t0b; // @[Top.scala 152:21]
  wire [15:0] n51_O_t1b_t1b; // @[Top.scala 152:21]
  Fst n17 ( // @[Top.scala 49:21]
    .valid_up(n17_valid_up),
    .valid_down(n17_valid_down),
    .I_t0b(n17_I_t0b),
    .O(n17_O)
  );
  FIFO_1 n50 ( // @[Top.scala 52:21]
    .clock(n50_clock),
    .reset(n50_reset),
    .valid_up(n50_valid_up),
    .valid_down(n50_valid_down),
    .I(n50_I),
    .O(n50_O)
  );
  FIFO_1 n38 ( // @[Top.scala 55:21]
    .clock(n38_clock),
    .reset(n38_reset),
    .valid_up(n38_valid_up),
    .valid_down(n38_valid_down),
    .I(n38_I),
    .O(n38_O)
  );
  Snd n18 ( // @[Top.scala 58:21]
    .valid_up(n18_valid_up),
    .valid_down(n18_valid_down),
    .I_t1b_t0b(n18_I_t1b_t0b),
    .I_t1b_t1b(n18_I_t1b_t1b),
    .O_t0b(n18_O_t0b),
    .O_t1b(n18_O_t1b)
  );
  Fst_1 n19 ( // @[Top.scala 61:21]
    .valid_up(n19_valid_up),
    .valid_down(n19_valid_down),
    .I_t0b(n19_I_t0b),
    .O(n19_O)
  );
  Snd_1 n20 ( // @[Top.scala 64:21]
    .valid_up(n20_valid_up),
    .valid_down(n20_valid_down),
    .I_t1b(n20_I_t1b),
    .O(n20_O)
  );
  AtomTuple_1 n21 ( // @[Top.scala 67:21]
    .valid_up(n21_valid_up),
    .valid_down(n21_valid_down),
    .I0(n21_I0),
    .I1(n21_I1),
    .O_t0b(n21_O_t0b),
    .O_t1b(n21_O_t1b)
  );
  Add n22 ( // @[Top.scala 71:21]
    .valid_up(n22_valid_up),
    .valid_down(n22_valid_down),
    .I_t0b(n22_I_t0b),
    .I_t1b(n22_I_t1b),
    .O(n22_O)
  );
  InitialDelayCounter InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n25 ( // @[Top.scala 75:21]
    .valid_up(n25_valid_up),
    .valid_down(n25_valid_down),
    .I0(n25_I0),
    .O_t0b(n25_O_t0b)
  );
  RShift n26 ( // @[Top.scala 79:21]
    .valid_up(n26_valid_up),
    .valid_down(n26_valid_down),
    .I_t0b(n26_I_t0b),
    .O(n26_O)
  );
  AtomTuple_1 n27 ( // @[Top.scala 82:21]
    .valid_up(n27_valid_up),
    .valid_down(n27_valid_down),
    .I0(n27_I0),
    .I1(n27_I1),
    .O_t0b(n27_O_t0b),
    .O_t1b(n27_O_t1b)
  );
  Eq n28 ( // @[Top.scala 86:21]
    .valid_up(n28_valid_up),
    .valid_down(n28_valid_down),
    .I_t0b(n28_I_t0b),
    .I_t1b(n28_I_t1b),
    .O(n28_O)
  );
  InitialDelayCounter InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n31 ( // @[Top.scala 90:21]
    .valid_up(n31_valid_up),
    .valid_down(n31_valid_down),
    .I0(n31_I0),
    .I1(n31_I1),
    .O_t0b(n31_O_t0b),
    .O_t1b(n31_O_t1b)
  );
  Add n32 ( // @[Top.scala 94:21]
    .valid_up(n32_valid_up),
    .valid_down(n32_valid_down),
    .I_t0b(n32_I_t0b),
    .I_t1b(n32_I_t1b),
    .O(n32_O)
  );
  AtomTuple_1 n33 ( // @[Top.scala 97:21]
    .valid_up(n33_valid_up),
    .valid_down(n33_valid_down),
    .I0(n33_I0),
    .I1(n33_I1),
    .O_t0b(n33_O_t0b),
    .O_t1b(n33_O_t1b)
  );
  AtomTuple_9 n34 ( // @[Top.scala 101:21]
    .valid_up(n34_valid_up),
    .valid_down(n34_valid_down),
    .I0(n34_I0),
    .I1_t0b(n34_I1_t0b),
    .I1_t1b(n34_I1_t1b),
    .O_t0b(n34_O_t0b),
    .O_t1b_t0b(n34_O_t1b_t0b),
    .O_t1b_t1b(n34_O_t1b_t1b)
  );
  If n35 ( // @[Top.scala 105:21]
    .valid_up(n35_valid_up),
    .valid_down(n35_valid_down),
    .I_t0b(n35_I_t0b),
    .I_t1b_t0b(n35_I_t1b_t0b),
    .I_t1b_t1b(n35_I_t1b_t1b),
    .O(n35_O)
  );
  AtomTuple_1 n36 ( // @[Top.scala 108:21]
    .valid_up(n36_valid_up),
    .valid_down(n36_valid_down),
    .I0(n36_I0),
    .I1(n36_I1),
    .O_t0b(n36_O_t0b),
    .O_t1b(n36_O_t1b)
  );
  Mul n37 ( // @[Top.scala 112:21]
    .clock(n37_clock),
    .reset(n37_reset),
    .valid_up(n37_valid_up),
    .valid_down(n37_valid_down),
    .I_t0b(n37_I_t0b),
    .I_t1b(n37_I_t1b),
    .O(n37_O)
  );
  AtomTuple_1 n39 ( // @[Top.scala 115:21]
    .valid_up(n39_valid_up),
    .valid_down(n39_valid_down),
    .I0(n39_I0),
    .I1(n39_I1),
    .O_t0b(n39_O_t0b),
    .O_t1b(n39_O_t1b)
  );
  Lt n40 ( // @[Top.scala 119:21]
    .valid_up(n40_valid_up),
    .valid_down(n40_valid_down),
    .I_t0b(n40_I_t0b),
    .I_t1b(n40_I_t1b),
    .O(n40_O)
  );
  InitialDelayCounter InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n42 ( // @[Top.scala 123:21]
    .valid_up(n42_valid_up),
    .valid_down(n42_valid_down),
    .I0(n42_I0),
    .I1(n42_I1),
    .O_t0b(n42_O_t0b),
    .O_t1b(n42_O_t1b)
  );
  Sub n43 ( // @[Top.scala 127:21]
    .valid_up(n43_valid_up),
    .valid_down(n43_valid_down),
    .I_t0b(n43_I_t0b),
    .I_t1b(n43_I_t1b),
    .O(n43_O)
  );
  AtomTuple_1 n44 ( // @[Top.scala 130:21]
    .valid_up(n44_valid_up),
    .valid_down(n44_valid_down),
    .I0(n44_I0),
    .I1(n44_I1),
    .O_t0b(n44_O_t0b),
    .O_t1b(n44_O_t1b)
  );
  AtomTuple_1 n45 ( // @[Top.scala 134:21]
    .valid_up(n45_valid_up),
    .valid_down(n45_valid_down),
    .I0(n45_I0),
    .I1(n45_I1),
    .O_t0b(n45_O_t0b),
    .O_t1b(n45_O_t1b)
  );
  AtomTuple_15 n46 ( // @[Top.scala 138:21]
    .valid_up(n46_valid_up),
    .valid_down(n46_valid_down),
    .I0_t0b(n46_I0_t0b),
    .I0_t1b(n46_I0_t1b),
    .I1_t0b(n46_I1_t0b),
    .I1_t1b(n46_I1_t1b),
    .O_t0b_t0b(n46_O_t0b_t0b),
    .O_t0b_t1b(n46_O_t0b_t1b),
    .O_t1b_t0b(n46_O_t1b_t0b),
    .O_t1b_t1b(n46_O_t1b_t1b)
  );
  FIFO_3 n47 ( // @[Top.scala 142:21]
    .clock(n47_clock),
    .reset(n47_reset),
    .valid_up(n47_valid_up),
    .valid_down(n47_valid_down),
    .I_t0b_t0b(n47_I_t0b_t0b),
    .I_t0b_t1b(n47_I_t0b_t1b),
    .I_t1b_t0b(n47_I_t1b_t0b),
    .I_t1b_t1b(n47_I_t1b_t1b),
    .O_t0b_t0b(n47_O_t0b_t0b),
    .O_t0b_t1b(n47_O_t0b_t1b),
    .O_t1b_t0b(n47_O_t1b_t0b),
    .O_t1b_t1b(n47_O_t1b_t1b)
  );
  AtomTuple_16 n48 ( // @[Top.scala 145:21]
    .valid_up(n48_valid_up),
    .valid_down(n48_valid_down),
    .I0(n48_I0),
    .I1_t0b_t0b(n48_I1_t0b_t0b),
    .I1_t0b_t1b(n48_I1_t0b_t1b),
    .I1_t1b_t0b(n48_I1_t1b_t0b),
    .I1_t1b_t1b(n48_I1_t1b_t1b),
    .O_t0b(n48_O_t0b),
    .O_t1b_t0b_t0b(n48_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n48_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n48_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n48_O_t1b_t1b_t1b)
  );
  If_1 n49 ( // @[Top.scala 149:21]
    .valid_up(n49_valid_up),
    .valid_down(n49_valid_down),
    .I_t0b(n49_I_t0b),
    .I_t1b_t0b_t0b(n49_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n49_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n49_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n49_I_t1b_t1b_t1b),
    .O_t0b(n49_O_t0b),
    .O_t1b(n49_O_t1b)
  );
  AtomTuple_3 n51 ( // @[Top.scala 152:21]
    .valid_up(n51_valid_up),
    .valid_down(n51_valid_down),
    .I0(n51_I0),
    .I1_t0b(n51_I1_t0b),
    .I1_t1b(n51_I1_t1b),
    .O_t0b(n51_O_t0b),
    .O_t1b_t0b(n51_O_t1b_t0b),
    .O_t1b_t1b(n51_O_t1b_t1b)
  );
  assign valid_down = n51_valid_down; // @[Top.scala 157:16]
  assign O_t0b = n51_O_t0b; // @[Top.scala 156:7]
  assign O_t1b_t0b = n51_O_t1b_t0b; // @[Top.scala 156:7]
  assign O_t1b_t1b = n51_O_t1b_t1b; // @[Top.scala 156:7]
  assign n17_valid_up = valid_up; // @[Top.scala 51:18]
  assign n17_I_t0b = I_t0b; // @[Top.scala 50:11]
  assign n50_clock = clock;
  assign n50_reset = reset;
  assign n50_valid_up = n17_valid_down; // @[Top.scala 54:18]
  assign n50_I = n17_O; // @[Top.scala 53:11]
  assign n38_clock = clock;
  assign n38_reset = reset;
  assign n38_valid_up = n17_valid_down; // @[Top.scala 57:18]
  assign n38_I = n17_O; // @[Top.scala 56:11]
  assign n18_valid_up = valid_up; // @[Top.scala 60:18]
  assign n18_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 59:11]
  assign n18_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 59:11]
  assign n19_valid_up = n18_valid_down; // @[Top.scala 63:18]
  assign n19_I_t0b = n18_O_t0b; // @[Top.scala 62:11]
  assign n20_valid_up = n18_valid_down; // @[Top.scala 66:18]
  assign n20_I_t1b = n18_O_t1b; // @[Top.scala 65:11]
  assign n21_valid_up = n19_valid_down & n20_valid_down; // @[Top.scala 70:18]
  assign n21_I0 = n19_O; // @[Top.scala 68:12]
  assign n21_I1 = n20_O; // @[Top.scala 69:12]
  assign n22_valid_up = n21_valid_down; // @[Top.scala 73:18]
  assign n22_I_t0b = n21_O_t0b; // @[Top.scala 72:11]
  assign n22_I_t1b = n21_O_t1b; // @[Top.scala 72:11]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n25_valid_up = n22_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 78:18]
  assign n25_I0 = n22_O; // @[Top.scala 76:12]
  assign n26_valid_up = n25_valid_down; // @[Top.scala 81:18]
  assign n26_I_t0b = n25_O_t0b; // @[Top.scala 80:11]
  assign n27_valid_up = n26_valid_down & n19_valid_down; // @[Top.scala 85:18]
  assign n27_I0 = n26_O; // @[Top.scala 83:12]
  assign n27_I1 = n19_O; // @[Top.scala 84:12]
  assign n28_valid_up = n27_valid_down; // @[Top.scala 88:18]
  assign n28_I_t0b = n27_O_t0b; // @[Top.scala 87:11]
  assign n28_I_t1b = n27_O_t1b; // @[Top.scala 87:11]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n31_valid_up = n26_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 93:18]
  assign n31_I0 = n26_O; // @[Top.scala 91:12]
  assign n31_I1 = 16'h1; // @[Top.scala 92:12]
  assign n32_valid_up = n31_valid_down; // @[Top.scala 96:18]
  assign n32_I_t0b = n31_O_t0b; // @[Top.scala 95:11]
  assign n32_I_t1b = n31_O_t1b; // @[Top.scala 95:11]
  assign n33_valid_up = n32_valid_down & n26_valid_down; // @[Top.scala 100:18]
  assign n33_I0 = n32_O; // @[Top.scala 98:12]
  assign n33_I1 = n26_O; // @[Top.scala 99:12]
  assign n34_valid_up = n28_valid_down & n33_valid_down; // @[Top.scala 104:18]
  assign n34_I0 = n28_O[0]; // @[Top.scala 102:12]
  assign n34_I1_t0b = n33_O_t0b; // @[Top.scala 103:12]
  assign n34_I1_t1b = n33_O_t1b; // @[Top.scala 103:12]
  assign n35_valid_up = n34_valid_down; // @[Top.scala 107:18]
  assign n35_I_t0b = n34_O_t0b; // @[Top.scala 106:11]
  assign n35_I_t1b_t0b = n34_O_t1b_t0b; // @[Top.scala 106:11]
  assign n35_I_t1b_t1b = n34_O_t1b_t1b; // @[Top.scala 106:11]
  assign n36_valid_up = n35_valid_down; // @[Top.scala 111:18]
  assign n36_I0 = n35_O; // @[Top.scala 109:12]
  assign n36_I1 = n35_O; // @[Top.scala 110:12]
  assign n37_clock = clock;
  assign n37_reset = reset;
  assign n37_valid_up = n36_valid_down; // @[Top.scala 114:18]
  assign n37_I_t0b = n36_O_t0b; // @[Top.scala 113:11]
  assign n37_I_t1b = n36_O_t1b; // @[Top.scala 113:11]
  assign n39_valid_up = n38_valid_down & n37_valid_down; // @[Top.scala 118:18]
  assign n39_I0 = n38_O; // @[Top.scala 116:12]
  assign n39_I1 = n37_O; // @[Top.scala 117:12]
  assign n40_valid_up = n39_valid_down; // @[Top.scala 121:18]
  assign n40_I_t0b = n39_O_t0b; // @[Top.scala 120:11]
  assign n40_I_t1b = n39_O_t1b; // @[Top.scala 120:11]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n42_valid_up = n35_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 126:18]
  assign n42_I0 = n35_O; // @[Top.scala 124:12]
  assign n42_I1 = 16'h1; // @[Top.scala 125:12]
  assign n43_valid_up = n42_valid_down; // @[Top.scala 129:18]
  assign n43_I_t0b = n42_O_t0b; // @[Top.scala 128:11]
  assign n43_I_t1b = n42_O_t1b; // @[Top.scala 128:11]
  assign n44_valid_up = n19_valid_down & n43_valid_down; // @[Top.scala 133:18]
  assign n44_I0 = n19_O; // @[Top.scala 131:12]
  assign n44_I1 = n43_O; // @[Top.scala 132:12]
  assign n45_valid_up = n35_valid_down & n20_valid_down; // @[Top.scala 137:18]
  assign n45_I0 = n35_O; // @[Top.scala 135:12]
  assign n45_I1 = n20_O; // @[Top.scala 136:12]
  assign n46_valid_up = n44_valid_down & n45_valid_down; // @[Top.scala 141:18]
  assign n46_I0_t0b = n44_O_t0b; // @[Top.scala 139:12]
  assign n46_I0_t1b = n44_O_t1b; // @[Top.scala 139:12]
  assign n46_I1_t0b = n45_O_t0b; // @[Top.scala 140:12]
  assign n46_I1_t1b = n45_O_t1b; // @[Top.scala 140:12]
  assign n47_clock = clock;
  assign n47_reset = reset;
  assign n47_valid_up = n46_valid_down; // @[Top.scala 144:18]
  assign n47_I_t0b_t0b = n46_O_t0b_t0b; // @[Top.scala 143:11]
  assign n47_I_t0b_t1b = n46_O_t0b_t1b; // @[Top.scala 143:11]
  assign n47_I_t1b_t0b = n46_O_t1b_t0b; // @[Top.scala 143:11]
  assign n47_I_t1b_t1b = n46_O_t1b_t1b; // @[Top.scala 143:11]
  assign n48_valid_up = n40_valid_down & n47_valid_down; // @[Top.scala 148:18]
  assign n48_I0 = n40_O[0]; // @[Top.scala 146:12]
  assign n48_I1_t0b_t0b = n47_O_t0b_t0b; // @[Top.scala 147:12]
  assign n48_I1_t0b_t1b = n47_O_t0b_t1b; // @[Top.scala 147:12]
  assign n48_I1_t1b_t0b = n47_O_t1b_t0b; // @[Top.scala 147:12]
  assign n48_I1_t1b_t1b = n47_O_t1b_t1b; // @[Top.scala 147:12]
  assign n49_valid_up = n48_valid_down; // @[Top.scala 151:18]
  assign n49_I_t0b = n48_O_t0b; // @[Top.scala 150:11]
  assign n49_I_t1b_t0b_t0b = n48_O_t1b_t0b_t0b; // @[Top.scala 150:11]
  assign n49_I_t1b_t0b_t1b = n48_O_t1b_t0b_t1b; // @[Top.scala 150:11]
  assign n49_I_t1b_t1b_t0b = n48_O_t1b_t1b_t0b; // @[Top.scala 150:11]
  assign n49_I_t1b_t1b_t1b = n48_O_t1b_t1b_t1b; // @[Top.scala 150:11]
  assign n51_valid_up = n50_valid_down & n49_valid_down; // @[Top.scala 155:18]
  assign n51_I0 = n50_O; // @[Top.scala 153:12]
  assign n51_I1_t0b = n49_O_t0b; // @[Top.scala 154:12]
  assign n51_I1_t1b = n49_O_t1b; // @[Top.scala 154:12]
endmodule
module MapT_1(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_1 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
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
  wire  n54_valid_up; // @[Top.scala 163:21]
  wire  n54_valid_down; // @[Top.scala 163:21]
  wire [15:0] n54_I_t0b; // @[Top.scala 163:21]
  wire [15:0] n54_O; // @[Top.scala 163:21]
  wire  n87_clock; // @[Top.scala 166:21]
  wire  n87_reset; // @[Top.scala 166:21]
  wire  n87_valid_up; // @[Top.scala 166:21]
  wire  n87_valid_down; // @[Top.scala 166:21]
  wire [15:0] n87_I; // @[Top.scala 166:21]
  wire [15:0] n87_O; // @[Top.scala 166:21]
  wire  n75_clock; // @[Top.scala 169:21]
  wire  n75_reset; // @[Top.scala 169:21]
  wire  n75_valid_up; // @[Top.scala 169:21]
  wire  n75_valid_down; // @[Top.scala 169:21]
  wire [15:0] n75_I; // @[Top.scala 169:21]
  wire [15:0] n75_O; // @[Top.scala 169:21]
  wire  n55_valid_up; // @[Top.scala 172:21]
  wire  n55_valid_down; // @[Top.scala 172:21]
  wire [15:0] n55_I_t1b_t0b; // @[Top.scala 172:21]
  wire [15:0] n55_I_t1b_t1b; // @[Top.scala 172:21]
  wire [15:0] n55_O_t0b; // @[Top.scala 172:21]
  wire [15:0] n55_O_t1b; // @[Top.scala 172:21]
  wire  n56_valid_up; // @[Top.scala 175:21]
  wire  n56_valid_down; // @[Top.scala 175:21]
  wire [15:0] n56_I_t0b; // @[Top.scala 175:21]
  wire [15:0] n56_O; // @[Top.scala 175:21]
  wire  n57_valid_up; // @[Top.scala 178:21]
  wire  n57_valid_down; // @[Top.scala 178:21]
  wire [15:0] n57_I_t1b; // @[Top.scala 178:21]
  wire [15:0] n57_O; // @[Top.scala 178:21]
  wire  n58_valid_up; // @[Top.scala 181:21]
  wire  n58_valid_down; // @[Top.scala 181:21]
  wire [15:0] n58_I0; // @[Top.scala 181:21]
  wire [15:0] n58_I1; // @[Top.scala 181:21]
  wire [15:0] n58_O_t0b; // @[Top.scala 181:21]
  wire [15:0] n58_O_t1b; // @[Top.scala 181:21]
  wire  n59_valid_up; // @[Top.scala 185:21]
  wire  n59_valid_down; // @[Top.scala 185:21]
  wire [15:0] n59_I_t0b; // @[Top.scala 185:21]
  wire [15:0] n59_I_t1b; // @[Top.scala 185:21]
  wire [15:0] n59_O; // @[Top.scala 185:21]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n62_valid_up; // @[Top.scala 189:21]
  wire  n62_valid_down; // @[Top.scala 189:21]
  wire [15:0] n62_I0; // @[Top.scala 189:21]
  wire [15:0] n62_O_t0b; // @[Top.scala 189:21]
  wire  n63_valid_up; // @[Top.scala 193:21]
  wire  n63_valid_down; // @[Top.scala 193:21]
  wire [15:0] n63_I_t0b; // @[Top.scala 193:21]
  wire [15:0] n63_O; // @[Top.scala 193:21]
  wire  n64_valid_up; // @[Top.scala 196:21]
  wire  n64_valid_down; // @[Top.scala 196:21]
  wire [15:0] n64_I0; // @[Top.scala 196:21]
  wire [15:0] n64_I1; // @[Top.scala 196:21]
  wire [15:0] n64_O_t0b; // @[Top.scala 196:21]
  wire [15:0] n64_O_t1b; // @[Top.scala 196:21]
  wire  n65_valid_up; // @[Top.scala 200:21]
  wire  n65_valid_down; // @[Top.scala 200:21]
  wire [15:0] n65_I_t0b; // @[Top.scala 200:21]
  wire [15:0] n65_I_t1b; // @[Top.scala 200:21]
  wire [15:0] n65_O; // @[Top.scala 200:21]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n68_valid_up; // @[Top.scala 204:21]
  wire  n68_valid_down; // @[Top.scala 204:21]
  wire [15:0] n68_I0; // @[Top.scala 204:21]
  wire [15:0] n68_I1; // @[Top.scala 204:21]
  wire [15:0] n68_O_t0b; // @[Top.scala 204:21]
  wire [15:0] n68_O_t1b; // @[Top.scala 204:21]
  wire  n69_valid_up; // @[Top.scala 208:21]
  wire  n69_valid_down; // @[Top.scala 208:21]
  wire [15:0] n69_I_t0b; // @[Top.scala 208:21]
  wire [15:0] n69_I_t1b; // @[Top.scala 208:21]
  wire [15:0] n69_O; // @[Top.scala 208:21]
  wire  n70_valid_up; // @[Top.scala 211:21]
  wire  n70_valid_down; // @[Top.scala 211:21]
  wire [15:0] n70_I0; // @[Top.scala 211:21]
  wire [15:0] n70_I1; // @[Top.scala 211:21]
  wire [15:0] n70_O_t0b; // @[Top.scala 211:21]
  wire [15:0] n70_O_t1b; // @[Top.scala 211:21]
  wire  n71_valid_up; // @[Top.scala 215:21]
  wire  n71_valid_down; // @[Top.scala 215:21]
  wire  n71_I0; // @[Top.scala 215:21]
  wire [15:0] n71_I1_t0b; // @[Top.scala 215:21]
  wire [15:0] n71_I1_t1b; // @[Top.scala 215:21]
  wire  n71_O_t0b; // @[Top.scala 215:21]
  wire [15:0] n71_O_t1b_t0b; // @[Top.scala 215:21]
  wire [15:0] n71_O_t1b_t1b; // @[Top.scala 215:21]
  wire  n72_valid_up; // @[Top.scala 219:21]
  wire  n72_valid_down; // @[Top.scala 219:21]
  wire  n72_I_t0b; // @[Top.scala 219:21]
  wire [15:0] n72_I_t1b_t0b; // @[Top.scala 219:21]
  wire [15:0] n72_I_t1b_t1b; // @[Top.scala 219:21]
  wire [15:0] n72_O; // @[Top.scala 219:21]
  wire  n73_valid_up; // @[Top.scala 222:21]
  wire  n73_valid_down; // @[Top.scala 222:21]
  wire [15:0] n73_I0; // @[Top.scala 222:21]
  wire [15:0] n73_I1; // @[Top.scala 222:21]
  wire [15:0] n73_O_t0b; // @[Top.scala 222:21]
  wire [15:0] n73_O_t1b; // @[Top.scala 222:21]
  wire  n74_clock; // @[Top.scala 226:21]
  wire  n74_reset; // @[Top.scala 226:21]
  wire  n74_valid_up; // @[Top.scala 226:21]
  wire  n74_valid_down; // @[Top.scala 226:21]
  wire [15:0] n74_I_t0b; // @[Top.scala 226:21]
  wire [15:0] n74_I_t1b; // @[Top.scala 226:21]
  wire [15:0] n74_O; // @[Top.scala 226:21]
  wire  n76_valid_up; // @[Top.scala 229:21]
  wire  n76_valid_down; // @[Top.scala 229:21]
  wire [15:0] n76_I0; // @[Top.scala 229:21]
  wire [15:0] n76_I1; // @[Top.scala 229:21]
  wire [15:0] n76_O_t0b; // @[Top.scala 229:21]
  wire [15:0] n76_O_t1b; // @[Top.scala 229:21]
  wire  n77_valid_up; // @[Top.scala 233:21]
  wire  n77_valid_down; // @[Top.scala 233:21]
  wire [15:0] n77_I_t0b; // @[Top.scala 233:21]
  wire [15:0] n77_I_t1b; // @[Top.scala 233:21]
  wire [15:0] n77_O; // @[Top.scala 233:21]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n79_valid_up; // @[Top.scala 237:21]
  wire  n79_valid_down; // @[Top.scala 237:21]
  wire [15:0] n79_I0; // @[Top.scala 237:21]
  wire [15:0] n79_I1; // @[Top.scala 237:21]
  wire [15:0] n79_O_t0b; // @[Top.scala 237:21]
  wire [15:0] n79_O_t1b; // @[Top.scala 237:21]
  wire  n80_valid_up; // @[Top.scala 241:21]
  wire  n80_valid_down; // @[Top.scala 241:21]
  wire [15:0] n80_I_t0b; // @[Top.scala 241:21]
  wire [15:0] n80_I_t1b; // @[Top.scala 241:21]
  wire [15:0] n80_O; // @[Top.scala 241:21]
  wire  n81_valid_up; // @[Top.scala 244:21]
  wire  n81_valid_down; // @[Top.scala 244:21]
  wire [15:0] n81_I0; // @[Top.scala 244:21]
  wire [15:0] n81_I1; // @[Top.scala 244:21]
  wire [15:0] n81_O_t0b; // @[Top.scala 244:21]
  wire [15:0] n81_O_t1b; // @[Top.scala 244:21]
  wire  n82_valid_up; // @[Top.scala 248:21]
  wire  n82_valid_down; // @[Top.scala 248:21]
  wire [15:0] n82_I0; // @[Top.scala 248:21]
  wire [15:0] n82_I1; // @[Top.scala 248:21]
  wire [15:0] n82_O_t0b; // @[Top.scala 248:21]
  wire [15:0] n82_O_t1b; // @[Top.scala 248:21]
  wire  n83_valid_up; // @[Top.scala 252:21]
  wire  n83_valid_down; // @[Top.scala 252:21]
  wire [15:0] n83_I0_t0b; // @[Top.scala 252:21]
  wire [15:0] n83_I0_t1b; // @[Top.scala 252:21]
  wire [15:0] n83_I1_t0b; // @[Top.scala 252:21]
  wire [15:0] n83_I1_t1b; // @[Top.scala 252:21]
  wire [15:0] n83_O_t0b_t0b; // @[Top.scala 252:21]
  wire [15:0] n83_O_t0b_t1b; // @[Top.scala 252:21]
  wire [15:0] n83_O_t1b_t0b; // @[Top.scala 252:21]
  wire [15:0] n83_O_t1b_t1b; // @[Top.scala 252:21]
  wire  n84_clock; // @[Top.scala 256:21]
  wire  n84_reset; // @[Top.scala 256:21]
  wire  n84_valid_up; // @[Top.scala 256:21]
  wire  n84_valid_down; // @[Top.scala 256:21]
  wire [15:0] n84_I_t0b_t0b; // @[Top.scala 256:21]
  wire [15:0] n84_I_t0b_t1b; // @[Top.scala 256:21]
  wire [15:0] n84_I_t1b_t0b; // @[Top.scala 256:21]
  wire [15:0] n84_I_t1b_t1b; // @[Top.scala 256:21]
  wire [15:0] n84_O_t0b_t0b; // @[Top.scala 256:21]
  wire [15:0] n84_O_t0b_t1b; // @[Top.scala 256:21]
  wire [15:0] n84_O_t1b_t0b; // @[Top.scala 256:21]
  wire [15:0] n84_O_t1b_t1b; // @[Top.scala 256:21]
  wire  n85_valid_up; // @[Top.scala 259:21]
  wire  n85_valid_down; // @[Top.scala 259:21]
  wire  n85_I0; // @[Top.scala 259:21]
  wire [15:0] n85_I1_t0b_t0b; // @[Top.scala 259:21]
  wire [15:0] n85_I1_t0b_t1b; // @[Top.scala 259:21]
  wire [15:0] n85_I1_t1b_t0b; // @[Top.scala 259:21]
  wire [15:0] n85_I1_t1b_t1b; // @[Top.scala 259:21]
  wire  n85_O_t0b; // @[Top.scala 259:21]
  wire [15:0] n85_O_t1b_t0b_t0b; // @[Top.scala 259:21]
  wire [15:0] n85_O_t1b_t0b_t1b; // @[Top.scala 259:21]
  wire [15:0] n85_O_t1b_t1b_t0b; // @[Top.scala 259:21]
  wire [15:0] n85_O_t1b_t1b_t1b; // @[Top.scala 259:21]
  wire  n86_valid_up; // @[Top.scala 263:21]
  wire  n86_valid_down; // @[Top.scala 263:21]
  wire  n86_I_t0b; // @[Top.scala 263:21]
  wire [15:0] n86_I_t1b_t0b_t0b; // @[Top.scala 263:21]
  wire [15:0] n86_I_t1b_t0b_t1b; // @[Top.scala 263:21]
  wire [15:0] n86_I_t1b_t1b_t0b; // @[Top.scala 263:21]
  wire [15:0] n86_I_t1b_t1b_t1b; // @[Top.scala 263:21]
  wire [15:0] n86_O_t0b; // @[Top.scala 263:21]
  wire [15:0] n86_O_t1b; // @[Top.scala 263:21]
  wire  n88_valid_up; // @[Top.scala 266:21]
  wire  n88_valid_down; // @[Top.scala 266:21]
  wire [15:0] n88_I0; // @[Top.scala 266:21]
  wire [15:0] n88_I1_t0b; // @[Top.scala 266:21]
  wire [15:0] n88_I1_t1b; // @[Top.scala 266:21]
  wire [15:0] n88_O_t0b; // @[Top.scala 266:21]
  wire [15:0] n88_O_t1b_t0b; // @[Top.scala 266:21]
  wire [15:0] n88_O_t1b_t1b; // @[Top.scala 266:21]
  Fst n54 ( // @[Top.scala 163:21]
    .valid_up(n54_valid_up),
    .valid_down(n54_valid_down),
    .I_t0b(n54_I_t0b),
    .O(n54_O)
  );
  FIFO_1 n87 ( // @[Top.scala 166:21]
    .clock(n87_clock),
    .reset(n87_reset),
    .valid_up(n87_valid_up),
    .valid_down(n87_valid_down),
    .I(n87_I),
    .O(n87_O)
  );
  FIFO_1 n75 ( // @[Top.scala 169:21]
    .clock(n75_clock),
    .reset(n75_reset),
    .valid_up(n75_valid_up),
    .valid_down(n75_valid_down),
    .I(n75_I),
    .O(n75_O)
  );
  Snd n55 ( // @[Top.scala 172:21]
    .valid_up(n55_valid_up),
    .valid_down(n55_valid_down),
    .I_t1b_t0b(n55_I_t1b_t0b),
    .I_t1b_t1b(n55_I_t1b_t1b),
    .O_t0b(n55_O_t0b),
    .O_t1b(n55_O_t1b)
  );
  Fst_1 n56 ( // @[Top.scala 175:21]
    .valid_up(n56_valid_up),
    .valid_down(n56_valid_down),
    .I_t0b(n56_I_t0b),
    .O(n56_O)
  );
  Snd_1 n57 ( // @[Top.scala 178:21]
    .valid_up(n57_valid_up),
    .valid_down(n57_valid_down),
    .I_t1b(n57_I_t1b),
    .O(n57_O)
  );
  AtomTuple_1 n58 ( // @[Top.scala 181:21]
    .valid_up(n58_valid_up),
    .valid_down(n58_valid_down),
    .I0(n58_I0),
    .I1(n58_I1),
    .O_t0b(n58_O_t0b),
    .O_t1b(n58_O_t1b)
  );
  Add n59 ( // @[Top.scala 185:21]
    .valid_up(n59_valid_up),
    .valid_down(n59_valid_down),
    .I_t0b(n59_I_t0b),
    .I_t1b(n59_I_t1b),
    .O(n59_O)
  );
  InitialDelayCounter_6 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n62 ( // @[Top.scala 189:21]
    .valid_up(n62_valid_up),
    .valid_down(n62_valid_down),
    .I0(n62_I0),
    .O_t0b(n62_O_t0b)
  );
  RShift n63 ( // @[Top.scala 193:21]
    .valid_up(n63_valid_up),
    .valid_down(n63_valid_down),
    .I_t0b(n63_I_t0b),
    .O(n63_O)
  );
  AtomTuple_1 n64 ( // @[Top.scala 196:21]
    .valid_up(n64_valid_up),
    .valid_down(n64_valid_down),
    .I0(n64_I0),
    .I1(n64_I1),
    .O_t0b(n64_O_t0b),
    .O_t1b(n64_O_t1b)
  );
  Eq n65 ( // @[Top.scala 200:21]
    .valid_up(n65_valid_up),
    .valid_down(n65_valid_down),
    .I_t0b(n65_I_t0b),
    .I_t1b(n65_I_t1b),
    .O(n65_O)
  );
  InitialDelayCounter_6 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n68 ( // @[Top.scala 204:21]
    .valid_up(n68_valid_up),
    .valid_down(n68_valid_down),
    .I0(n68_I0),
    .I1(n68_I1),
    .O_t0b(n68_O_t0b),
    .O_t1b(n68_O_t1b)
  );
  Add n69 ( // @[Top.scala 208:21]
    .valid_up(n69_valid_up),
    .valid_down(n69_valid_down),
    .I_t0b(n69_I_t0b),
    .I_t1b(n69_I_t1b),
    .O(n69_O)
  );
  AtomTuple_1 n70 ( // @[Top.scala 211:21]
    .valid_up(n70_valid_up),
    .valid_down(n70_valid_down),
    .I0(n70_I0),
    .I1(n70_I1),
    .O_t0b(n70_O_t0b),
    .O_t1b(n70_O_t1b)
  );
  AtomTuple_9 n71 ( // @[Top.scala 215:21]
    .valid_up(n71_valid_up),
    .valid_down(n71_valid_down),
    .I0(n71_I0),
    .I1_t0b(n71_I1_t0b),
    .I1_t1b(n71_I1_t1b),
    .O_t0b(n71_O_t0b),
    .O_t1b_t0b(n71_O_t1b_t0b),
    .O_t1b_t1b(n71_O_t1b_t1b)
  );
  If n72 ( // @[Top.scala 219:21]
    .valid_up(n72_valid_up),
    .valid_down(n72_valid_down),
    .I_t0b(n72_I_t0b),
    .I_t1b_t0b(n72_I_t1b_t0b),
    .I_t1b_t1b(n72_I_t1b_t1b),
    .O(n72_O)
  );
  AtomTuple_1 n73 ( // @[Top.scala 222:21]
    .valid_up(n73_valid_up),
    .valid_down(n73_valid_down),
    .I0(n73_I0),
    .I1(n73_I1),
    .O_t0b(n73_O_t0b),
    .O_t1b(n73_O_t1b)
  );
  Mul n74 ( // @[Top.scala 226:21]
    .clock(n74_clock),
    .reset(n74_reset),
    .valid_up(n74_valid_up),
    .valid_down(n74_valid_down),
    .I_t0b(n74_I_t0b),
    .I_t1b(n74_I_t1b),
    .O(n74_O)
  );
  AtomTuple_1 n76 ( // @[Top.scala 229:21]
    .valid_up(n76_valid_up),
    .valid_down(n76_valid_down),
    .I0(n76_I0),
    .I1(n76_I1),
    .O_t0b(n76_O_t0b),
    .O_t1b(n76_O_t1b)
  );
  Lt n77 ( // @[Top.scala 233:21]
    .valid_up(n77_valid_up),
    .valid_down(n77_valid_down),
    .I_t0b(n77_I_t0b),
    .I_t1b(n77_I_t1b),
    .O(n77_O)
  );
  InitialDelayCounter_6 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n79 ( // @[Top.scala 237:21]
    .valid_up(n79_valid_up),
    .valid_down(n79_valid_down),
    .I0(n79_I0),
    .I1(n79_I1),
    .O_t0b(n79_O_t0b),
    .O_t1b(n79_O_t1b)
  );
  Sub n80 ( // @[Top.scala 241:21]
    .valid_up(n80_valid_up),
    .valid_down(n80_valid_down),
    .I_t0b(n80_I_t0b),
    .I_t1b(n80_I_t1b),
    .O(n80_O)
  );
  AtomTuple_1 n81 ( // @[Top.scala 244:21]
    .valid_up(n81_valid_up),
    .valid_down(n81_valid_down),
    .I0(n81_I0),
    .I1(n81_I1),
    .O_t0b(n81_O_t0b),
    .O_t1b(n81_O_t1b)
  );
  AtomTuple_1 n82 ( // @[Top.scala 248:21]
    .valid_up(n82_valid_up),
    .valid_down(n82_valid_down),
    .I0(n82_I0),
    .I1(n82_I1),
    .O_t0b(n82_O_t0b),
    .O_t1b(n82_O_t1b)
  );
  AtomTuple_15 n83 ( // @[Top.scala 252:21]
    .valid_up(n83_valid_up),
    .valid_down(n83_valid_down),
    .I0_t0b(n83_I0_t0b),
    .I0_t1b(n83_I0_t1b),
    .I1_t0b(n83_I1_t0b),
    .I1_t1b(n83_I1_t1b),
    .O_t0b_t0b(n83_O_t0b_t0b),
    .O_t0b_t1b(n83_O_t0b_t1b),
    .O_t1b_t0b(n83_O_t1b_t0b),
    .O_t1b_t1b(n83_O_t1b_t1b)
  );
  FIFO_3 n84 ( // @[Top.scala 256:21]
    .clock(n84_clock),
    .reset(n84_reset),
    .valid_up(n84_valid_up),
    .valid_down(n84_valid_down),
    .I_t0b_t0b(n84_I_t0b_t0b),
    .I_t0b_t1b(n84_I_t0b_t1b),
    .I_t1b_t0b(n84_I_t1b_t0b),
    .I_t1b_t1b(n84_I_t1b_t1b),
    .O_t0b_t0b(n84_O_t0b_t0b),
    .O_t0b_t1b(n84_O_t0b_t1b),
    .O_t1b_t0b(n84_O_t1b_t0b),
    .O_t1b_t1b(n84_O_t1b_t1b)
  );
  AtomTuple_16 n85 ( // @[Top.scala 259:21]
    .valid_up(n85_valid_up),
    .valid_down(n85_valid_down),
    .I0(n85_I0),
    .I1_t0b_t0b(n85_I1_t0b_t0b),
    .I1_t0b_t1b(n85_I1_t0b_t1b),
    .I1_t1b_t0b(n85_I1_t1b_t0b),
    .I1_t1b_t1b(n85_I1_t1b_t1b),
    .O_t0b(n85_O_t0b),
    .O_t1b_t0b_t0b(n85_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n85_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n85_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n85_O_t1b_t1b_t1b)
  );
  If_1 n86 ( // @[Top.scala 263:21]
    .valid_up(n86_valid_up),
    .valid_down(n86_valid_down),
    .I_t0b(n86_I_t0b),
    .I_t1b_t0b_t0b(n86_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n86_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n86_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n86_I_t1b_t1b_t1b),
    .O_t0b(n86_O_t0b),
    .O_t1b(n86_O_t1b)
  );
  AtomTuple_3 n88 ( // @[Top.scala 266:21]
    .valid_up(n88_valid_up),
    .valid_down(n88_valid_down),
    .I0(n88_I0),
    .I1_t0b(n88_I1_t0b),
    .I1_t1b(n88_I1_t1b),
    .O_t0b(n88_O_t0b),
    .O_t1b_t0b(n88_O_t1b_t0b),
    .O_t1b_t1b(n88_O_t1b_t1b)
  );
  assign valid_down = n88_valid_down; // @[Top.scala 271:16]
  assign O_t0b = n88_O_t0b; // @[Top.scala 270:7]
  assign O_t1b_t0b = n88_O_t1b_t0b; // @[Top.scala 270:7]
  assign O_t1b_t1b = n88_O_t1b_t1b; // @[Top.scala 270:7]
  assign n54_valid_up = valid_up; // @[Top.scala 165:18]
  assign n54_I_t0b = I_t0b; // @[Top.scala 164:11]
  assign n87_clock = clock;
  assign n87_reset = reset;
  assign n87_valid_up = n54_valid_down; // @[Top.scala 168:18]
  assign n87_I = n54_O; // @[Top.scala 167:11]
  assign n75_clock = clock;
  assign n75_reset = reset;
  assign n75_valid_up = n54_valid_down; // @[Top.scala 171:18]
  assign n75_I = n54_O; // @[Top.scala 170:11]
  assign n55_valid_up = valid_up; // @[Top.scala 174:18]
  assign n55_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 173:11]
  assign n55_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 173:11]
  assign n56_valid_up = n55_valid_down; // @[Top.scala 177:18]
  assign n56_I_t0b = n55_O_t0b; // @[Top.scala 176:11]
  assign n57_valid_up = n55_valid_down; // @[Top.scala 180:18]
  assign n57_I_t1b = n55_O_t1b; // @[Top.scala 179:11]
  assign n58_valid_up = n56_valid_down & n57_valid_down; // @[Top.scala 184:18]
  assign n58_I0 = n56_O; // @[Top.scala 182:12]
  assign n58_I1 = n57_O; // @[Top.scala 183:12]
  assign n59_valid_up = n58_valid_down; // @[Top.scala 187:18]
  assign n59_I_t0b = n58_O_t0b; // @[Top.scala 186:11]
  assign n59_I_t1b = n58_O_t1b; // @[Top.scala 186:11]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n62_valid_up = n59_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 192:18]
  assign n62_I0 = n59_O; // @[Top.scala 190:12]
  assign n63_valid_up = n62_valid_down; // @[Top.scala 195:18]
  assign n63_I_t0b = n62_O_t0b; // @[Top.scala 194:11]
  assign n64_valid_up = n63_valid_down & n56_valid_down; // @[Top.scala 199:18]
  assign n64_I0 = n63_O; // @[Top.scala 197:12]
  assign n64_I1 = n56_O; // @[Top.scala 198:12]
  assign n65_valid_up = n64_valid_down; // @[Top.scala 202:18]
  assign n65_I_t0b = n64_O_t0b; // @[Top.scala 201:11]
  assign n65_I_t1b = n64_O_t1b; // @[Top.scala 201:11]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n68_valid_up = n63_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 207:18]
  assign n68_I0 = n63_O; // @[Top.scala 205:12]
  assign n68_I1 = 16'h1; // @[Top.scala 206:12]
  assign n69_valid_up = n68_valid_down; // @[Top.scala 210:18]
  assign n69_I_t0b = n68_O_t0b; // @[Top.scala 209:11]
  assign n69_I_t1b = n68_O_t1b; // @[Top.scala 209:11]
  assign n70_valid_up = n69_valid_down & n63_valid_down; // @[Top.scala 214:18]
  assign n70_I0 = n69_O; // @[Top.scala 212:12]
  assign n70_I1 = n63_O; // @[Top.scala 213:12]
  assign n71_valid_up = n65_valid_down & n70_valid_down; // @[Top.scala 218:18]
  assign n71_I0 = n65_O[0]; // @[Top.scala 216:12]
  assign n71_I1_t0b = n70_O_t0b; // @[Top.scala 217:12]
  assign n71_I1_t1b = n70_O_t1b; // @[Top.scala 217:12]
  assign n72_valid_up = n71_valid_down; // @[Top.scala 221:18]
  assign n72_I_t0b = n71_O_t0b; // @[Top.scala 220:11]
  assign n72_I_t1b_t0b = n71_O_t1b_t0b; // @[Top.scala 220:11]
  assign n72_I_t1b_t1b = n71_O_t1b_t1b; // @[Top.scala 220:11]
  assign n73_valid_up = n72_valid_down; // @[Top.scala 225:18]
  assign n73_I0 = n72_O; // @[Top.scala 223:12]
  assign n73_I1 = n72_O; // @[Top.scala 224:12]
  assign n74_clock = clock;
  assign n74_reset = reset;
  assign n74_valid_up = n73_valid_down; // @[Top.scala 228:18]
  assign n74_I_t0b = n73_O_t0b; // @[Top.scala 227:11]
  assign n74_I_t1b = n73_O_t1b; // @[Top.scala 227:11]
  assign n76_valid_up = n75_valid_down & n74_valid_down; // @[Top.scala 232:18]
  assign n76_I0 = n75_O; // @[Top.scala 230:12]
  assign n76_I1 = n74_O; // @[Top.scala 231:12]
  assign n77_valid_up = n76_valid_down; // @[Top.scala 235:18]
  assign n77_I_t0b = n76_O_t0b; // @[Top.scala 234:11]
  assign n77_I_t1b = n76_O_t1b; // @[Top.scala 234:11]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n79_valid_up = n72_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 240:18]
  assign n79_I0 = n72_O; // @[Top.scala 238:12]
  assign n79_I1 = 16'h1; // @[Top.scala 239:12]
  assign n80_valid_up = n79_valid_down; // @[Top.scala 243:18]
  assign n80_I_t0b = n79_O_t0b; // @[Top.scala 242:11]
  assign n80_I_t1b = n79_O_t1b; // @[Top.scala 242:11]
  assign n81_valid_up = n56_valid_down & n80_valid_down; // @[Top.scala 247:18]
  assign n81_I0 = n56_O; // @[Top.scala 245:12]
  assign n81_I1 = n80_O; // @[Top.scala 246:12]
  assign n82_valid_up = n72_valid_down & n57_valid_down; // @[Top.scala 251:18]
  assign n82_I0 = n72_O; // @[Top.scala 249:12]
  assign n82_I1 = n57_O; // @[Top.scala 250:12]
  assign n83_valid_up = n81_valid_down & n82_valid_down; // @[Top.scala 255:18]
  assign n83_I0_t0b = n81_O_t0b; // @[Top.scala 253:12]
  assign n83_I0_t1b = n81_O_t1b; // @[Top.scala 253:12]
  assign n83_I1_t0b = n82_O_t0b; // @[Top.scala 254:12]
  assign n83_I1_t1b = n82_O_t1b; // @[Top.scala 254:12]
  assign n84_clock = clock;
  assign n84_reset = reset;
  assign n84_valid_up = n83_valid_down; // @[Top.scala 258:18]
  assign n84_I_t0b_t0b = n83_O_t0b_t0b; // @[Top.scala 257:11]
  assign n84_I_t0b_t1b = n83_O_t0b_t1b; // @[Top.scala 257:11]
  assign n84_I_t1b_t0b = n83_O_t1b_t0b; // @[Top.scala 257:11]
  assign n84_I_t1b_t1b = n83_O_t1b_t1b; // @[Top.scala 257:11]
  assign n85_valid_up = n77_valid_down & n84_valid_down; // @[Top.scala 262:18]
  assign n85_I0 = n77_O[0]; // @[Top.scala 260:12]
  assign n85_I1_t0b_t0b = n84_O_t0b_t0b; // @[Top.scala 261:12]
  assign n85_I1_t0b_t1b = n84_O_t0b_t1b; // @[Top.scala 261:12]
  assign n85_I1_t1b_t0b = n84_O_t1b_t0b; // @[Top.scala 261:12]
  assign n85_I1_t1b_t1b = n84_O_t1b_t1b; // @[Top.scala 261:12]
  assign n86_valid_up = n85_valid_down; // @[Top.scala 265:18]
  assign n86_I_t0b = n85_O_t0b; // @[Top.scala 264:11]
  assign n86_I_t1b_t0b_t0b = n85_O_t1b_t0b_t0b; // @[Top.scala 264:11]
  assign n86_I_t1b_t0b_t1b = n85_O_t1b_t0b_t1b; // @[Top.scala 264:11]
  assign n86_I_t1b_t1b_t0b = n85_O_t1b_t1b_t0b; // @[Top.scala 264:11]
  assign n86_I_t1b_t1b_t1b = n85_O_t1b_t1b_t1b; // @[Top.scala 264:11]
  assign n88_valid_up = n87_valid_down & n86_valid_down; // @[Top.scala 269:18]
  assign n88_I0 = n87_O; // @[Top.scala 267:12]
  assign n88_I1_t0b = n86_O_t0b; // @[Top.scala 268:12]
  assign n88_I1_t1b = n86_O_t1b; // @[Top.scala 268:12]
endmodule
module MapT_2(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_2 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
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
  wire  n91_valid_up; // @[Top.scala 277:21]
  wire  n91_valid_down; // @[Top.scala 277:21]
  wire [15:0] n91_I_t0b; // @[Top.scala 277:21]
  wire [15:0] n91_O; // @[Top.scala 277:21]
  wire  n124_clock; // @[Top.scala 280:22]
  wire  n124_reset; // @[Top.scala 280:22]
  wire  n124_valid_up; // @[Top.scala 280:22]
  wire  n124_valid_down; // @[Top.scala 280:22]
  wire [15:0] n124_I; // @[Top.scala 280:22]
  wire [15:0] n124_O; // @[Top.scala 280:22]
  wire  n112_clock; // @[Top.scala 283:22]
  wire  n112_reset; // @[Top.scala 283:22]
  wire  n112_valid_up; // @[Top.scala 283:22]
  wire  n112_valid_down; // @[Top.scala 283:22]
  wire [15:0] n112_I; // @[Top.scala 283:22]
  wire [15:0] n112_O; // @[Top.scala 283:22]
  wire  n92_valid_up; // @[Top.scala 286:21]
  wire  n92_valid_down; // @[Top.scala 286:21]
  wire [15:0] n92_I_t1b_t0b; // @[Top.scala 286:21]
  wire [15:0] n92_I_t1b_t1b; // @[Top.scala 286:21]
  wire [15:0] n92_O_t0b; // @[Top.scala 286:21]
  wire [15:0] n92_O_t1b; // @[Top.scala 286:21]
  wire  n93_valid_up; // @[Top.scala 289:21]
  wire  n93_valid_down; // @[Top.scala 289:21]
  wire [15:0] n93_I_t0b; // @[Top.scala 289:21]
  wire [15:0] n93_O; // @[Top.scala 289:21]
  wire  n94_valid_up; // @[Top.scala 292:21]
  wire  n94_valid_down; // @[Top.scala 292:21]
  wire [15:0] n94_I_t1b; // @[Top.scala 292:21]
  wire [15:0] n94_O; // @[Top.scala 292:21]
  wire  n95_valid_up; // @[Top.scala 295:21]
  wire  n95_valid_down; // @[Top.scala 295:21]
  wire [15:0] n95_I0; // @[Top.scala 295:21]
  wire [15:0] n95_I1; // @[Top.scala 295:21]
  wire [15:0] n95_O_t0b; // @[Top.scala 295:21]
  wire [15:0] n95_O_t1b; // @[Top.scala 295:21]
  wire  n96_valid_up; // @[Top.scala 299:21]
  wire  n96_valid_down; // @[Top.scala 299:21]
  wire [15:0] n96_I_t0b; // @[Top.scala 299:21]
  wire [15:0] n96_I_t1b; // @[Top.scala 299:21]
  wire [15:0] n96_O; // @[Top.scala 299:21]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n99_valid_up; // @[Top.scala 303:21]
  wire  n99_valid_down; // @[Top.scala 303:21]
  wire [15:0] n99_I0; // @[Top.scala 303:21]
  wire [15:0] n99_O_t0b; // @[Top.scala 303:21]
  wire  n100_valid_up; // @[Top.scala 307:22]
  wire  n100_valid_down; // @[Top.scala 307:22]
  wire [15:0] n100_I_t0b; // @[Top.scala 307:22]
  wire [15:0] n100_O; // @[Top.scala 307:22]
  wire  n101_valid_up; // @[Top.scala 310:22]
  wire  n101_valid_down; // @[Top.scala 310:22]
  wire [15:0] n101_I0; // @[Top.scala 310:22]
  wire [15:0] n101_I1; // @[Top.scala 310:22]
  wire [15:0] n101_O_t0b; // @[Top.scala 310:22]
  wire [15:0] n101_O_t1b; // @[Top.scala 310:22]
  wire  n102_valid_up; // @[Top.scala 314:22]
  wire  n102_valid_down; // @[Top.scala 314:22]
  wire [15:0] n102_I_t0b; // @[Top.scala 314:22]
  wire [15:0] n102_I_t1b; // @[Top.scala 314:22]
  wire [15:0] n102_O; // @[Top.scala 314:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n105_valid_up; // @[Top.scala 318:22]
  wire  n105_valid_down; // @[Top.scala 318:22]
  wire [15:0] n105_I0; // @[Top.scala 318:22]
  wire [15:0] n105_I1; // @[Top.scala 318:22]
  wire [15:0] n105_O_t0b; // @[Top.scala 318:22]
  wire [15:0] n105_O_t1b; // @[Top.scala 318:22]
  wire  n106_valid_up; // @[Top.scala 322:22]
  wire  n106_valid_down; // @[Top.scala 322:22]
  wire [15:0] n106_I_t0b; // @[Top.scala 322:22]
  wire [15:0] n106_I_t1b; // @[Top.scala 322:22]
  wire [15:0] n106_O; // @[Top.scala 322:22]
  wire  n107_valid_up; // @[Top.scala 325:22]
  wire  n107_valid_down; // @[Top.scala 325:22]
  wire [15:0] n107_I0; // @[Top.scala 325:22]
  wire [15:0] n107_I1; // @[Top.scala 325:22]
  wire [15:0] n107_O_t0b; // @[Top.scala 325:22]
  wire [15:0] n107_O_t1b; // @[Top.scala 325:22]
  wire  n108_valid_up; // @[Top.scala 329:22]
  wire  n108_valid_down; // @[Top.scala 329:22]
  wire  n108_I0; // @[Top.scala 329:22]
  wire [15:0] n108_I1_t0b; // @[Top.scala 329:22]
  wire [15:0] n108_I1_t1b; // @[Top.scala 329:22]
  wire  n108_O_t0b; // @[Top.scala 329:22]
  wire [15:0] n108_O_t1b_t0b; // @[Top.scala 329:22]
  wire [15:0] n108_O_t1b_t1b; // @[Top.scala 329:22]
  wire  n109_valid_up; // @[Top.scala 333:22]
  wire  n109_valid_down; // @[Top.scala 333:22]
  wire  n109_I_t0b; // @[Top.scala 333:22]
  wire [15:0] n109_I_t1b_t0b; // @[Top.scala 333:22]
  wire [15:0] n109_I_t1b_t1b; // @[Top.scala 333:22]
  wire [15:0] n109_O; // @[Top.scala 333:22]
  wire  n110_valid_up; // @[Top.scala 336:22]
  wire  n110_valid_down; // @[Top.scala 336:22]
  wire [15:0] n110_I0; // @[Top.scala 336:22]
  wire [15:0] n110_I1; // @[Top.scala 336:22]
  wire [15:0] n110_O_t0b; // @[Top.scala 336:22]
  wire [15:0] n110_O_t1b; // @[Top.scala 336:22]
  wire  n111_clock; // @[Top.scala 340:22]
  wire  n111_reset; // @[Top.scala 340:22]
  wire  n111_valid_up; // @[Top.scala 340:22]
  wire  n111_valid_down; // @[Top.scala 340:22]
  wire [15:0] n111_I_t0b; // @[Top.scala 340:22]
  wire [15:0] n111_I_t1b; // @[Top.scala 340:22]
  wire [15:0] n111_O; // @[Top.scala 340:22]
  wire  n113_valid_up; // @[Top.scala 343:22]
  wire  n113_valid_down; // @[Top.scala 343:22]
  wire [15:0] n113_I0; // @[Top.scala 343:22]
  wire [15:0] n113_I1; // @[Top.scala 343:22]
  wire [15:0] n113_O_t0b; // @[Top.scala 343:22]
  wire [15:0] n113_O_t1b; // @[Top.scala 343:22]
  wire  n114_valid_up; // @[Top.scala 347:22]
  wire  n114_valid_down; // @[Top.scala 347:22]
  wire [15:0] n114_I_t0b; // @[Top.scala 347:22]
  wire [15:0] n114_I_t1b; // @[Top.scala 347:22]
  wire [15:0] n114_O; // @[Top.scala 347:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n116_valid_up; // @[Top.scala 351:22]
  wire  n116_valid_down; // @[Top.scala 351:22]
  wire [15:0] n116_I0; // @[Top.scala 351:22]
  wire [15:0] n116_I1; // @[Top.scala 351:22]
  wire [15:0] n116_O_t0b; // @[Top.scala 351:22]
  wire [15:0] n116_O_t1b; // @[Top.scala 351:22]
  wire  n117_valid_up; // @[Top.scala 355:22]
  wire  n117_valid_down; // @[Top.scala 355:22]
  wire [15:0] n117_I_t0b; // @[Top.scala 355:22]
  wire [15:0] n117_I_t1b; // @[Top.scala 355:22]
  wire [15:0] n117_O; // @[Top.scala 355:22]
  wire  n118_valid_up; // @[Top.scala 358:22]
  wire  n118_valid_down; // @[Top.scala 358:22]
  wire [15:0] n118_I0; // @[Top.scala 358:22]
  wire [15:0] n118_I1; // @[Top.scala 358:22]
  wire [15:0] n118_O_t0b; // @[Top.scala 358:22]
  wire [15:0] n118_O_t1b; // @[Top.scala 358:22]
  wire  n119_valid_up; // @[Top.scala 362:22]
  wire  n119_valid_down; // @[Top.scala 362:22]
  wire [15:0] n119_I0; // @[Top.scala 362:22]
  wire [15:0] n119_I1; // @[Top.scala 362:22]
  wire [15:0] n119_O_t0b; // @[Top.scala 362:22]
  wire [15:0] n119_O_t1b; // @[Top.scala 362:22]
  wire  n120_valid_up; // @[Top.scala 366:22]
  wire  n120_valid_down; // @[Top.scala 366:22]
  wire [15:0] n120_I0_t0b; // @[Top.scala 366:22]
  wire [15:0] n120_I0_t1b; // @[Top.scala 366:22]
  wire [15:0] n120_I1_t0b; // @[Top.scala 366:22]
  wire [15:0] n120_I1_t1b; // @[Top.scala 366:22]
  wire [15:0] n120_O_t0b_t0b; // @[Top.scala 366:22]
  wire [15:0] n120_O_t0b_t1b; // @[Top.scala 366:22]
  wire [15:0] n120_O_t1b_t0b; // @[Top.scala 366:22]
  wire [15:0] n120_O_t1b_t1b; // @[Top.scala 366:22]
  wire  n121_clock; // @[Top.scala 370:22]
  wire  n121_reset; // @[Top.scala 370:22]
  wire  n121_valid_up; // @[Top.scala 370:22]
  wire  n121_valid_down; // @[Top.scala 370:22]
  wire [15:0] n121_I_t0b_t0b; // @[Top.scala 370:22]
  wire [15:0] n121_I_t0b_t1b; // @[Top.scala 370:22]
  wire [15:0] n121_I_t1b_t0b; // @[Top.scala 370:22]
  wire [15:0] n121_I_t1b_t1b; // @[Top.scala 370:22]
  wire [15:0] n121_O_t0b_t0b; // @[Top.scala 370:22]
  wire [15:0] n121_O_t0b_t1b; // @[Top.scala 370:22]
  wire [15:0] n121_O_t1b_t0b; // @[Top.scala 370:22]
  wire [15:0] n121_O_t1b_t1b; // @[Top.scala 370:22]
  wire  n122_valid_up; // @[Top.scala 373:22]
  wire  n122_valid_down; // @[Top.scala 373:22]
  wire  n122_I0; // @[Top.scala 373:22]
  wire [15:0] n122_I1_t0b_t0b; // @[Top.scala 373:22]
  wire [15:0] n122_I1_t0b_t1b; // @[Top.scala 373:22]
  wire [15:0] n122_I1_t1b_t0b; // @[Top.scala 373:22]
  wire [15:0] n122_I1_t1b_t1b; // @[Top.scala 373:22]
  wire  n122_O_t0b; // @[Top.scala 373:22]
  wire [15:0] n122_O_t1b_t0b_t0b; // @[Top.scala 373:22]
  wire [15:0] n122_O_t1b_t0b_t1b; // @[Top.scala 373:22]
  wire [15:0] n122_O_t1b_t1b_t0b; // @[Top.scala 373:22]
  wire [15:0] n122_O_t1b_t1b_t1b; // @[Top.scala 373:22]
  wire  n123_valid_up; // @[Top.scala 377:22]
  wire  n123_valid_down; // @[Top.scala 377:22]
  wire  n123_I_t0b; // @[Top.scala 377:22]
  wire [15:0] n123_I_t1b_t0b_t0b; // @[Top.scala 377:22]
  wire [15:0] n123_I_t1b_t0b_t1b; // @[Top.scala 377:22]
  wire [15:0] n123_I_t1b_t1b_t0b; // @[Top.scala 377:22]
  wire [15:0] n123_I_t1b_t1b_t1b; // @[Top.scala 377:22]
  wire [15:0] n123_O_t0b; // @[Top.scala 377:22]
  wire [15:0] n123_O_t1b; // @[Top.scala 377:22]
  wire  n125_valid_up; // @[Top.scala 380:22]
  wire  n125_valid_down; // @[Top.scala 380:22]
  wire [15:0] n125_I0; // @[Top.scala 380:22]
  wire [15:0] n125_I1_t0b; // @[Top.scala 380:22]
  wire [15:0] n125_I1_t1b; // @[Top.scala 380:22]
  wire [15:0] n125_O_t0b; // @[Top.scala 380:22]
  wire [15:0] n125_O_t1b_t0b; // @[Top.scala 380:22]
  wire [15:0] n125_O_t1b_t1b; // @[Top.scala 380:22]
  Fst n91 ( // @[Top.scala 277:21]
    .valid_up(n91_valid_up),
    .valid_down(n91_valid_down),
    .I_t0b(n91_I_t0b),
    .O(n91_O)
  );
  FIFO_1 n124 ( // @[Top.scala 280:22]
    .clock(n124_clock),
    .reset(n124_reset),
    .valid_up(n124_valid_up),
    .valid_down(n124_valid_down),
    .I(n124_I),
    .O(n124_O)
  );
  FIFO_1 n112 ( // @[Top.scala 283:22]
    .clock(n112_clock),
    .reset(n112_reset),
    .valid_up(n112_valid_up),
    .valid_down(n112_valid_down),
    .I(n112_I),
    .O(n112_O)
  );
  Snd n92 ( // @[Top.scala 286:21]
    .valid_up(n92_valid_up),
    .valid_down(n92_valid_down),
    .I_t1b_t0b(n92_I_t1b_t0b),
    .I_t1b_t1b(n92_I_t1b_t1b),
    .O_t0b(n92_O_t0b),
    .O_t1b(n92_O_t1b)
  );
  Fst_1 n93 ( // @[Top.scala 289:21]
    .valid_up(n93_valid_up),
    .valid_down(n93_valid_down),
    .I_t0b(n93_I_t0b),
    .O(n93_O)
  );
  Snd_1 n94 ( // @[Top.scala 292:21]
    .valid_up(n94_valid_up),
    .valid_down(n94_valid_down),
    .I_t1b(n94_I_t1b),
    .O(n94_O)
  );
  AtomTuple_1 n95 ( // @[Top.scala 295:21]
    .valid_up(n95_valid_up),
    .valid_down(n95_valid_down),
    .I0(n95_I0),
    .I1(n95_I1),
    .O_t0b(n95_O_t0b),
    .O_t1b(n95_O_t1b)
  );
  Add n96 ( // @[Top.scala 299:21]
    .valid_up(n96_valid_up),
    .valid_down(n96_valid_down),
    .I_t0b(n96_I_t0b),
    .I_t1b(n96_I_t1b),
    .O(n96_O)
  );
  InitialDelayCounter_9 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n99 ( // @[Top.scala 303:21]
    .valid_up(n99_valid_up),
    .valid_down(n99_valid_down),
    .I0(n99_I0),
    .O_t0b(n99_O_t0b)
  );
  RShift n100 ( // @[Top.scala 307:22]
    .valid_up(n100_valid_up),
    .valid_down(n100_valid_down),
    .I_t0b(n100_I_t0b),
    .O(n100_O)
  );
  AtomTuple_1 n101 ( // @[Top.scala 310:22]
    .valid_up(n101_valid_up),
    .valid_down(n101_valid_down),
    .I0(n101_I0),
    .I1(n101_I1),
    .O_t0b(n101_O_t0b),
    .O_t1b(n101_O_t1b)
  );
  Eq n102 ( // @[Top.scala 314:22]
    .valid_up(n102_valid_up),
    .valid_down(n102_valid_down),
    .I_t0b(n102_I_t0b),
    .I_t1b(n102_I_t1b),
    .O(n102_O)
  );
  InitialDelayCounter_9 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n105 ( // @[Top.scala 318:22]
    .valid_up(n105_valid_up),
    .valid_down(n105_valid_down),
    .I0(n105_I0),
    .I1(n105_I1),
    .O_t0b(n105_O_t0b),
    .O_t1b(n105_O_t1b)
  );
  Add n106 ( // @[Top.scala 322:22]
    .valid_up(n106_valid_up),
    .valid_down(n106_valid_down),
    .I_t0b(n106_I_t0b),
    .I_t1b(n106_I_t1b),
    .O(n106_O)
  );
  AtomTuple_1 n107 ( // @[Top.scala 325:22]
    .valid_up(n107_valid_up),
    .valid_down(n107_valid_down),
    .I0(n107_I0),
    .I1(n107_I1),
    .O_t0b(n107_O_t0b),
    .O_t1b(n107_O_t1b)
  );
  AtomTuple_9 n108 ( // @[Top.scala 329:22]
    .valid_up(n108_valid_up),
    .valid_down(n108_valid_down),
    .I0(n108_I0),
    .I1_t0b(n108_I1_t0b),
    .I1_t1b(n108_I1_t1b),
    .O_t0b(n108_O_t0b),
    .O_t1b_t0b(n108_O_t1b_t0b),
    .O_t1b_t1b(n108_O_t1b_t1b)
  );
  If n109 ( // @[Top.scala 333:22]
    .valid_up(n109_valid_up),
    .valid_down(n109_valid_down),
    .I_t0b(n109_I_t0b),
    .I_t1b_t0b(n109_I_t1b_t0b),
    .I_t1b_t1b(n109_I_t1b_t1b),
    .O(n109_O)
  );
  AtomTuple_1 n110 ( // @[Top.scala 336:22]
    .valid_up(n110_valid_up),
    .valid_down(n110_valid_down),
    .I0(n110_I0),
    .I1(n110_I1),
    .O_t0b(n110_O_t0b),
    .O_t1b(n110_O_t1b)
  );
  Mul n111 ( // @[Top.scala 340:22]
    .clock(n111_clock),
    .reset(n111_reset),
    .valid_up(n111_valid_up),
    .valid_down(n111_valid_down),
    .I_t0b(n111_I_t0b),
    .I_t1b(n111_I_t1b),
    .O(n111_O)
  );
  AtomTuple_1 n113 ( // @[Top.scala 343:22]
    .valid_up(n113_valid_up),
    .valid_down(n113_valid_down),
    .I0(n113_I0),
    .I1(n113_I1),
    .O_t0b(n113_O_t0b),
    .O_t1b(n113_O_t1b)
  );
  Lt n114 ( // @[Top.scala 347:22]
    .valid_up(n114_valid_up),
    .valid_down(n114_valid_down),
    .I_t0b(n114_I_t0b),
    .I_t1b(n114_I_t1b),
    .O(n114_O)
  );
  InitialDelayCounter_9 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n116 ( // @[Top.scala 351:22]
    .valid_up(n116_valid_up),
    .valid_down(n116_valid_down),
    .I0(n116_I0),
    .I1(n116_I1),
    .O_t0b(n116_O_t0b),
    .O_t1b(n116_O_t1b)
  );
  Sub n117 ( // @[Top.scala 355:22]
    .valid_up(n117_valid_up),
    .valid_down(n117_valid_down),
    .I_t0b(n117_I_t0b),
    .I_t1b(n117_I_t1b),
    .O(n117_O)
  );
  AtomTuple_1 n118 ( // @[Top.scala 358:22]
    .valid_up(n118_valid_up),
    .valid_down(n118_valid_down),
    .I0(n118_I0),
    .I1(n118_I1),
    .O_t0b(n118_O_t0b),
    .O_t1b(n118_O_t1b)
  );
  AtomTuple_1 n119 ( // @[Top.scala 362:22]
    .valid_up(n119_valid_up),
    .valid_down(n119_valid_down),
    .I0(n119_I0),
    .I1(n119_I1),
    .O_t0b(n119_O_t0b),
    .O_t1b(n119_O_t1b)
  );
  AtomTuple_15 n120 ( // @[Top.scala 366:22]
    .valid_up(n120_valid_up),
    .valid_down(n120_valid_down),
    .I0_t0b(n120_I0_t0b),
    .I0_t1b(n120_I0_t1b),
    .I1_t0b(n120_I1_t0b),
    .I1_t1b(n120_I1_t1b),
    .O_t0b_t0b(n120_O_t0b_t0b),
    .O_t0b_t1b(n120_O_t0b_t1b),
    .O_t1b_t0b(n120_O_t1b_t0b),
    .O_t1b_t1b(n120_O_t1b_t1b)
  );
  FIFO_3 n121 ( // @[Top.scala 370:22]
    .clock(n121_clock),
    .reset(n121_reset),
    .valid_up(n121_valid_up),
    .valid_down(n121_valid_down),
    .I_t0b_t0b(n121_I_t0b_t0b),
    .I_t0b_t1b(n121_I_t0b_t1b),
    .I_t1b_t0b(n121_I_t1b_t0b),
    .I_t1b_t1b(n121_I_t1b_t1b),
    .O_t0b_t0b(n121_O_t0b_t0b),
    .O_t0b_t1b(n121_O_t0b_t1b),
    .O_t1b_t0b(n121_O_t1b_t0b),
    .O_t1b_t1b(n121_O_t1b_t1b)
  );
  AtomTuple_16 n122 ( // @[Top.scala 373:22]
    .valid_up(n122_valid_up),
    .valid_down(n122_valid_down),
    .I0(n122_I0),
    .I1_t0b_t0b(n122_I1_t0b_t0b),
    .I1_t0b_t1b(n122_I1_t0b_t1b),
    .I1_t1b_t0b(n122_I1_t1b_t0b),
    .I1_t1b_t1b(n122_I1_t1b_t1b),
    .O_t0b(n122_O_t0b),
    .O_t1b_t0b_t0b(n122_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n122_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n122_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n122_O_t1b_t1b_t1b)
  );
  If_1 n123 ( // @[Top.scala 377:22]
    .valid_up(n123_valid_up),
    .valid_down(n123_valid_down),
    .I_t0b(n123_I_t0b),
    .I_t1b_t0b_t0b(n123_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n123_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n123_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n123_I_t1b_t1b_t1b),
    .O_t0b(n123_O_t0b),
    .O_t1b(n123_O_t1b)
  );
  AtomTuple_3 n125 ( // @[Top.scala 380:22]
    .valid_up(n125_valid_up),
    .valid_down(n125_valid_down),
    .I0(n125_I0),
    .I1_t0b(n125_I1_t0b),
    .I1_t1b(n125_I1_t1b),
    .O_t0b(n125_O_t0b),
    .O_t1b_t0b(n125_O_t1b_t0b),
    .O_t1b_t1b(n125_O_t1b_t1b)
  );
  assign valid_down = n125_valid_down; // @[Top.scala 385:16]
  assign O_t0b = n125_O_t0b; // @[Top.scala 384:7]
  assign O_t1b_t0b = n125_O_t1b_t0b; // @[Top.scala 384:7]
  assign O_t1b_t1b = n125_O_t1b_t1b; // @[Top.scala 384:7]
  assign n91_valid_up = valid_up; // @[Top.scala 279:18]
  assign n91_I_t0b = I_t0b; // @[Top.scala 278:11]
  assign n124_clock = clock;
  assign n124_reset = reset;
  assign n124_valid_up = n91_valid_down; // @[Top.scala 282:19]
  assign n124_I = n91_O; // @[Top.scala 281:12]
  assign n112_clock = clock;
  assign n112_reset = reset;
  assign n112_valid_up = n91_valid_down; // @[Top.scala 285:19]
  assign n112_I = n91_O; // @[Top.scala 284:12]
  assign n92_valid_up = valid_up; // @[Top.scala 288:18]
  assign n92_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 287:11]
  assign n92_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 287:11]
  assign n93_valid_up = n92_valid_down; // @[Top.scala 291:18]
  assign n93_I_t0b = n92_O_t0b; // @[Top.scala 290:11]
  assign n94_valid_up = n92_valid_down; // @[Top.scala 294:18]
  assign n94_I_t1b = n92_O_t1b; // @[Top.scala 293:11]
  assign n95_valid_up = n93_valid_down & n94_valid_down; // @[Top.scala 298:18]
  assign n95_I0 = n93_O; // @[Top.scala 296:12]
  assign n95_I1 = n94_O; // @[Top.scala 297:12]
  assign n96_valid_up = n95_valid_down; // @[Top.scala 301:18]
  assign n96_I_t0b = n95_O_t0b; // @[Top.scala 300:11]
  assign n96_I_t1b = n95_O_t1b; // @[Top.scala 300:11]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n99_valid_up = n96_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 306:18]
  assign n99_I0 = n96_O; // @[Top.scala 304:12]
  assign n100_valid_up = n99_valid_down; // @[Top.scala 309:19]
  assign n100_I_t0b = n99_O_t0b; // @[Top.scala 308:12]
  assign n101_valid_up = n100_valid_down & n93_valid_down; // @[Top.scala 313:19]
  assign n101_I0 = n100_O; // @[Top.scala 311:13]
  assign n101_I1 = n93_O; // @[Top.scala 312:13]
  assign n102_valid_up = n101_valid_down; // @[Top.scala 316:19]
  assign n102_I_t0b = n101_O_t0b; // @[Top.scala 315:12]
  assign n102_I_t1b = n101_O_t1b; // @[Top.scala 315:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n105_valid_up = n100_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 321:19]
  assign n105_I0 = n100_O; // @[Top.scala 319:13]
  assign n105_I1 = 16'h1; // @[Top.scala 320:13]
  assign n106_valid_up = n105_valid_down; // @[Top.scala 324:19]
  assign n106_I_t0b = n105_O_t0b; // @[Top.scala 323:12]
  assign n106_I_t1b = n105_O_t1b; // @[Top.scala 323:12]
  assign n107_valid_up = n106_valid_down & n100_valid_down; // @[Top.scala 328:19]
  assign n107_I0 = n106_O; // @[Top.scala 326:13]
  assign n107_I1 = n100_O; // @[Top.scala 327:13]
  assign n108_valid_up = n102_valid_down & n107_valid_down; // @[Top.scala 332:19]
  assign n108_I0 = n102_O[0]; // @[Top.scala 330:13]
  assign n108_I1_t0b = n107_O_t0b; // @[Top.scala 331:13]
  assign n108_I1_t1b = n107_O_t1b; // @[Top.scala 331:13]
  assign n109_valid_up = n108_valid_down; // @[Top.scala 335:19]
  assign n109_I_t0b = n108_O_t0b; // @[Top.scala 334:12]
  assign n109_I_t1b_t0b = n108_O_t1b_t0b; // @[Top.scala 334:12]
  assign n109_I_t1b_t1b = n108_O_t1b_t1b; // @[Top.scala 334:12]
  assign n110_valid_up = n109_valid_down; // @[Top.scala 339:19]
  assign n110_I0 = n109_O; // @[Top.scala 337:13]
  assign n110_I1 = n109_O; // @[Top.scala 338:13]
  assign n111_clock = clock;
  assign n111_reset = reset;
  assign n111_valid_up = n110_valid_down; // @[Top.scala 342:19]
  assign n111_I_t0b = n110_O_t0b; // @[Top.scala 341:12]
  assign n111_I_t1b = n110_O_t1b; // @[Top.scala 341:12]
  assign n113_valid_up = n112_valid_down & n111_valid_down; // @[Top.scala 346:19]
  assign n113_I0 = n112_O; // @[Top.scala 344:13]
  assign n113_I1 = n111_O; // @[Top.scala 345:13]
  assign n114_valid_up = n113_valid_down; // @[Top.scala 349:19]
  assign n114_I_t0b = n113_O_t0b; // @[Top.scala 348:12]
  assign n114_I_t1b = n113_O_t1b; // @[Top.scala 348:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n116_valid_up = n109_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 354:19]
  assign n116_I0 = n109_O; // @[Top.scala 352:13]
  assign n116_I1 = 16'h1; // @[Top.scala 353:13]
  assign n117_valid_up = n116_valid_down; // @[Top.scala 357:19]
  assign n117_I_t0b = n116_O_t0b; // @[Top.scala 356:12]
  assign n117_I_t1b = n116_O_t1b; // @[Top.scala 356:12]
  assign n118_valid_up = n93_valid_down & n117_valid_down; // @[Top.scala 361:19]
  assign n118_I0 = n93_O; // @[Top.scala 359:13]
  assign n118_I1 = n117_O; // @[Top.scala 360:13]
  assign n119_valid_up = n109_valid_down & n94_valid_down; // @[Top.scala 365:19]
  assign n119_I0 = n109_O; // @[Top.scala 363:13]
  assign n119_I1 = n94_O; // @[Top.scala 364:13]
  assign n120_valid_up = n118_valid_down & n119_valid_down; // @[Top.scala 369:19]
  assign n120_I0_t0b = n118_O_t0b; // @[Top.scala 367:13]
  assign n120_I0_t1b = n118_O_t1b; // @[Top.scala 367:13]
  assign n120_I1_t0b = n119_O_t0b; // @[Top.scala 368:13]
  assign n120_I1_t1b = n119_O_t1b; // @[Top.scala 368:13]
  assign n121_clock = clock;
  assign n121_reset = reset;
  assign n121_valid_up = n120_valid_down; // @[Top.scala 372:19]
  assign n121_I_t0b_t0b = n120_O_t0b_t0b; // @[Top.scala 371:12]
  assign n121_I_t0b_t1b = n120_O_t0b_t1b; // @[Top.scala 371:12]
  assign n121_I_t1b_t0b = n120_O_t1b_t0b; // @[Top.scala 371:12]
  assign n121_I_t1b_t1b = n120_O_t1b_t1b; // @[Top.scala 371:12]
  assign n122_valid_up = n114_valid_down & n121_valid_down; // @[Top.scala 376:19]
  assign n122_I0 = n114_O[0]; // @[Top.scala 374:13]
  assign n122_I1_t0b_t0b = n121_O_t0b_t0b; // @[Top.scala 375:13]
  assign n122_I1_t0b_t1b = n121_O_t0b_t1b; // @[Top.scala 375:13]
  assign n122_I1_t1b_t0b = n121_O_t1b_t0b; // @[Top.scala 375:13]
  assign n122_I1_t1b_t1b = n121_O_t1b_t1b; // @[Top.scala 375:13]
  assign n123_valid_up = n122_valid_down; // @[Top.scala 379:19]
  assign n123_I_t0b = n122_O_t0b; // @[Top.scala 378:12]
  assign n123_I_t1b_t0b_t0b = n122_O_t1b_t0b_t0b; // @[Top.scala 378:12]
  assign n123_I_t1b_t0b_t1b = n122_O_t1b_t0b_t1b; // @[Top.scala 378:12]
  assign n123_I_t1b_t1b_t0b = n122_O_t1b_t1b_t0b; // @[Top.scala 378:12]
  assign n123_I_t1b_t1b_t1b = n122_O_t1b_t1b_t1b; // @[Top.scala 378:12]
  assign n125_valid_up = n124_valid_down & n123_valid_down; // @[Top.scala 383:19]
  assign n125_I0 = n124_O; // @[Top.scala 381:13]
  assign n125_I1_t0b = n123_O_t0b; // @[Top.scala 382:13]
  assign n125_I1_t1b = n123_O_t1b; // @[Top.scala 382:13]
endmodule
module MapT_3(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_3 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
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
  wire  n128_valid_up; // @[Top.scala 391:22]
  wire  n128_valid_down; // @[Top.scala 391:22]
  wire [15:0] n128_I_t0b; // @[Top.scala 391:22]
  wire [15:0] n128_O; // @[Top.scala 391:22]
  wire  n161_clock; // @[Top.scala 394:22]
  wire  n161_reset; // @[Top.scala 394:22]
  wire  n161_valid_up; // @[Top.scala 394:22]
  wire  n161_valid_down; // @[Top.scala 394:22]
  wire [15:0] n161_I; // @[Top.scala 394:22]
  wire [15:0] n161_O; // @[Top.scala 394:22]
  wire  n149_clock; // @[Top.scala 397:22]
  wire  n149_reset; // @[Top.scala 397:22]
  wire  n149_valid_up; // @[Top.scala 397:22]
  wire  n149_valid_down; // @[Top.scala 397:22]
  wire [15:0] n149_I; // @[Top.scala 397:22]
  wire [15:0] n149_O; // @[Top.scala 397:22]
  wire  n129_valid_up; // @[Top.scala 400:22]
  wire  n129_valid_down; // @[Top.scala 400:22]
  wire [15:0] n129_I_t1b_t0b; // @[Top.scala 400:22]
  wire [15:0] n129_I_t1b_t1b; // @[Top.scala 400:22]
  wire [15:0] n129_O_t0b; // @[Top.scala 400:22]
  wire [15:0] n129_O_t1b; // @[Top.scala 400:22]
  wire  n130_valid_up; // @[Top.scala 403:22]
  wire  n130_valid_down; // @[Top.scala 403:22]
  wire [15:0] n130_I_t0b; // @[Top.scala 403:22]
  wire [15:0] n130_O; // @[Top.scala 403:22]
  wire  n131_valid_up; // @[Top.scala 406:22]
  wire  n131_valid_down; // @[Top.scala 406:22]
  wire [15:0] n131_I_t1b; // @[Top.scala 406:22]
  wire [15:0] n131_O; // @[Top.scala 406:22]
  wire  n132_valid_up; // @[Top.scala 409:22]
  wire  n132_valid_down; // @[Top.scala 409:22]
  wire [15:0] n132_I0; // @[Top.scala 409:22]
  wire [15:0] n132_I1; // @[Top.scala 409:22]
  wire [15:0] n132_O_t0b; // @[Top.scala 409:22]
  wire [15:0] n132_O_t1b; // @[Top.scala 409:22]
  wire  n133_valid_up; // @[Top.scala 413:22]
  wire  n133_valid_down; // @[Top.scala 413:22]
  wire [15:0] n133_I_t0b; // @[Top.scala 413:22]
  wire [15:0] n133_I_t1b; // @[Top.scala 413:22]
  wire [15:0] n133_O; // @[Top.scala 413:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n136_valid_up; // @[Top.scala 417:22]
  wire  n136_valid_down; // @[Top.scala 417:22]
  wire [15:0] n136_I0; // @[Top.scala 417:22]
  wire [15:0] n136_O_t0b; // @[Top.scala 417:22]
  wire  n137_valid_up; // @[Top.scala 421:22]
  wire  n137_valid_down; // @[Top.scala 421:22]
  wire [15:0] n137_I_t0b; // @[Top.scala 421:22]
  wire [15:0] n137_O; // @[Top.scala 421:22]
  wire  n138_valid_up; // @[Top.scala 424:22]
  wire  n138_valid_down; // @[Top.scala 424:22]
  wire [15:0] n138_I0; // @[Top.scala 424:22]
  wire [15:0] n138_I1; // @[Top.scala 424:22]
  wire [15:0] n138_O_t0b; // @[Top.scala 424:22]
  wire [15:0] n138_O_t1b; // @[Top.scala 424:22]
  wire  n139_valid_up; // @[Top.scala 428:22]
  wire  n139_valid_down; // @[Top.scala 428:22]
  wire [15:0] n139_I_t0b; // @[Top.scala 428:22]
  wire [15:0] n139_I_t1b; // @[Top.scala 428:22]
  wire [15:0] n139_O; // @[Top.scala 428:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n142_valid_up; // @[Top.scala 432:22]
  wire  n142_valid_down; // @[Top.scala 432:22]
  wire [15:0] n142_I0; // @[Top.scala 432:22]
  wire [15:0] n142_I1; // @[Top.scala 432:22]
  wire [15:0] n142_O_t0b; // @[Top.scala 432:22]
  wire [15:0] n142_O_t1b; // @[Top.scala 432:22]
  wire  n143_valid_up; // @[Top.scala 436:22]
  wire  n143_valid_down; // @[Top.scala 436:22]
  wire [15:0] n143_I_t0b; // @[Top.scala 436:22]
  wire [15:0] n143_I_t1b; // @[Top.scala 436:22]
  wire [15:0] n143_O; // @[Top.scala 436:22]
  wire  n144_valid_up; // @[Top.scala 439:22]
  wire  n144_valid_down; // @[Top.scala 439:22]
  wire [15:0] n144_I0; // @[Top.scala 439:22]
  wire [15:0] n144_I1; // @[Top.scala 439:22]
  wire [15:0] n144_O_t0b; // @[Top.scala 439:22]
  wire [15:0] n144_O_t1b; // @[Top.scala 439:22]
  wire  n145_valid_up; // @[Top.scala 443:22]
  wire  n145_valid_down; // @[Top.scala 443:22]
  wire  n145_I0; // @[Top.scala 443:22]
  wire [15:0] n145_I1_t0b; // @[Top.scala 443:22]
  wire [15:0] n145_I1_t1b; // @[Top.scala 443:22]
  wire  n145_O_t0b; // @[Top.scala 443:22]
  wire [15:0] n145_O_t1b_t0b; // @[Top.scala 443:22]
  wire [15:0] n145_O_t1b_t1b; // @[Top.scala 443:22]
  wire  n146_valid_up; // @[Top.scala 447:22]
  wire  n146_valid_down; // @[Top.scala 447:22]
  wire  n146_I_t0b; // @[Top.scala 447:22]
  wire [15:0] n146_I_t1b_t0b; // @[Top.scala 447:22]
  wire [15:0] n146_I_t1b_t1b; // @[Top.scala 447:22]
  wire [15:0] n146_O; // @[Top.scala 447:22]
  wire  n147_valid_up; // @[Top.scala 450:22]
  wire  n147_valid_down; // @[Top.scala 450:22]
  wire [15:0] n147_I0; // @[Top.scala 450:22]
  wire [15:0] n147_I1; // @[Top.scala 450:22]
  wire [15:0] n147_O_t0b; // @[Top.scala 450:22]
  wire [15:0] n147_O_t1b; // @[Top.scala 450:22]
  wire  n148_clock; // @[Top.scala 454:22]
  wire  n148_reset; // @[Top.scala 454:22]
  wire  n148_valid_up; // @[Top.scala 454:22]
  wire  n148_valid_down; // @[Top.scala 454:22]
  wire [15:0] n148_I_t0b; // @[Top.scala 454:22]
  wire [15:0] n148_I_t1b; // @[Top.scala 454:22]
  wire [15:0] n148_O; // @[Top.scala 454:22]
  wire  n150_valid_up; // @[Top.scala 457:22]
  wire  n150_valid_down; // @[Top.scala 457:22]
  wire [15:0] n150_I0; // @[Top.scala 457:22]
  wire [15:0] n150_I1; // @[Top.scala 457:22]
  wire [15:0] n150_O_t0b; // @[Top.scala 457:22]
  wire [15:0] n150_O_t1b; // @[Top.scala 457:22]
  wire  n151_valid_up; // @[Top.scala 461:22]
  wire  n151_valid_down; // @[Top.scala 461:22]
  wire [15:0] n151_I_t0b; // @[Top.scala 461:22]
  wire [15:0] n151_I_t1b; // @[Top.scala 461:22]
  wire [15:0] n151_O; // @[Top.scala 461:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n153_valid_up; // @[Top.scala 465:22]
  wire  n153_valid_down; // @[Top.scala 465:22]
  wire [15:0] n153_I0; // @[Top.scala 465:22]
  wire [15:0] n153_I1; // @[Top.scala 465:22]
  wire [15:0] n153_O_t0b; // @[Top.scala 465:22]
  wire [15:0] n153_O_t1b; // @[Top.scala 465:22]
  wire  n154_valid_up; // @[Top.scala 469:22]
  wire  n154_valid_down; // @[Top.scala 469:22]
  wire [15:0] n154_I_t0b; // @[Top.scala 469:22]
  wire [15:0] n154_I_t1b; // @[Top.scala 469:22]
  wire [15:0] n154_O; // @[Top.scala 469:22]
  wire  n155_valid_up; // @[Top.scala 472:22]
  wire  n155_valid_down; // @[Top.scala 472:22]
  wire [15:0] n155_I0; // @[Top.scala 472:22]
  wire [15:0] n155_I1; // @[Top.scala 472:22]
  wire [15:0] n155_O_t0b; // @[Top.scala 472:22]
  wire [15:0] n155_O_t1b; // @[Top.scala 472:22]
  wire  n156_valid_up; // @[Top.scala 476:22]
  wire  n156_valid_down; // @[Top.scala 476:22]
  wire [15:0] n156_I0; // @[Top.scala 476:22]
  wire [15:0] n156_I1; // @[Top.scala 476:22]
  wire [15:0] n156_O_t0b; // @[Top.scala 476:22]
  wire [15:0] n156_O_t1b; // @[Top.scala 476:22]
  wire  n157_valid_up; // @[Top.scala 480:22]
  wire  n157_valid_down; // @[Top.scala 480:22]
  wire [15:0] n157_I0_t0b; // @[Top.scala 480:22]
  wire [15:0] n157_I0_t1b; // @[Top.scala 480:22]
  wire [15:0] n157_I1_t0b; // @[Top.scala 480:22]
  wire [15:0] n157_I1_t1b; // @[Top.scala 480:22]
  wire [15:0] n157_O_t0b_t0b; // @[Top.scala 480:22]
  wire [15:0] n157_O_t0b_t1b; // @[Top.scala 480:22]
  wire [15:0] n157_O_t1b_t0b; // @[Top.scala 480:22]
  wire [15:0] n157_O_t1b_t1b; // @[Top.scala 480:22]
  wire  n158_clock; // @[Top.scala 484:22]
  wire  n158_reset; // @[Top.scala 484:22]
  wire  n158_valid_up; // @[Top.scala 484:22]
  wire  n158_valid_down; // @[Top.scala 484:22]
  wire [15:0] n158_I_t0b_t0b; // @[Top.scala 484:22]
  wire [15:0] n158_I_t0b_t1b; // @[Top.scala 484:22]
  wire [15:0] n158_I_t1b_t0b; // @[Top.scala 484:22]
  wire [15:0] n158_I_t1b_t1b; // @[Top.scala 484:22]
  wire [15:0] n158_O_t0b_t0b; // @[Top.scala 484:22]
  wire [15:0] n158_O_t0b_t1b; // @[Top.scala 484:22]
  wire [15:0] n158_O_t1b_t0b; // @[Top.scala 484:22]
  wire [15:0] n158_O_t1b_t1b; // @[Top.scala 484:22]
  wire  n159_valid_up; // @[Top.scala 487:22]
  wire  n159_valid_down; // @[Top.scala 487:22]
  wire  n159_I0; // @[Top.scala 487:22]
  wire [15:0] n159_I1_t0b_t0b; // @[Top.scala 487:22]
  wire [15:0] n159_I1_t0b_t1b; // @[Top.scala 487:22]
  wire [15:0] n159_I1_t1b_t0b; // @[Top.scala 487:22]
  wire [15:0] n159_I1_t1b_t1b; // @[Top.scala 487:22]
  wire  n159_O_t0b; // @[Top.scala 487:22]
  wire [15:0] n159_O_t1b_t0b_t0b; // @[Top.scala 487:22]
  wire [15:0] n159_O_t1b_t0b_t1b; // @[Top.scala 487:22]
  wire [15:0] n159_O_t1b_t1b_t0b; // @[Top.scala 487:22]
  wire [15:0] n159_O_t1b_t1b_t1b; // @[Top.scala 487:22]
  wire  n160_valid_up; // @[Top.scala 491:22]
  wire  n160_valid_down; // @[Top.scala 491:22]
  wire  n160_I_t0b; // @[Top.scala 491:22]
  wire [15:0] n160_I_t1b_t0b_t0b; // @[Top.scala 491:22]
  wire [15:0] n160_I_t1b_t0b_t1b; // @[Top.scala 491:22]
  wire [15:0] n160_I_t1b_t1b_t0b; // @[Top.scala 491:22]
  wire [15:0] n160_I_t1b_t1b_t1b; // @[Top.scala 491:22]
  wire [15:0] n160_O_t0b; // @[Top.scala 491:22]
  wire [15:0] n160_O_t1b; // @[Top.scala 491:22]
  wire  n162_valid_up; // @[Top.scala 494:22]
  wire  n162_valid_down; // @[Top.scala 494:22]
  wire [15:0] n162_I0; // @[Top.scala 494:22]
  wire [15:0] n162_I1_t0b; // @[Top.scala 494:22]
  wire [15:0] n162_I1_t1b; // @[Top.scala 494:22]
  wire [15:0] n162_O_t0b; // @[Top.scala 494:22]
  wire [15:0] n162_O_t1b_t0b; // @[Top.scala 494:22]
  wire [15:0] n162_O_t1b_t1b; // @[Top.scala 494:22]
  Fst n128 ( // @[Top.scala 391:22]
    .valid_up(n128_valid_up),
    .valid_down(n128_valid_down),
    .I_t0b(n128_I_t0b),
    .O(n128_O)
  );
  FIFO_1 n161 ( // @[Top.scala 394:22]
    .clock(n161_clock),
    .reset(n161_reset),
    .valid_up(n161_valid_up),
    .valid_down(n161_valid_down),
    .I(n161_I),
    .O(n161_O)
  );
  FIFO_1 n149 ( // @[Top.scala 397:22]
    .clock(n149_clock),
    .reset(n149_reset),
    .valid_up(n149_valid_up),
    .valid_down(n149_valid_down),
    .I(n149_I),
    .O(n149_O)
  );
  Snd n129 ( // @[Top.scala 400:22]
    .valid_up(n129_valid_up),
    .valid_down(n129_valid_down),
    .I_t1b_t0b(n129_I_t1b_t0b),
    .I_t1b_t1b(n129_I_t1b_t1b),
    .O_t0b(n129_O_t0b),
    .O_t1b(n129_O_t1b)
  );
  Fst_1 n130 ( // @[Top.scala 403:22]
    .valid_up(n130_valid_up),
    .valid_down(n130_valid_down),
    .I_t0b(n130_I_t0b),
    .O(n130_O)
  );
  Snd_1 n131 ( // @[Top.scala 406:22]
    .valid_up(n131_valid_up),
    .valid_down(n131_valid_down),
    .I_t1b(n131_I_t1b),
    .O(n131_O)
  );
  AtomTuple_1 n132 ( // @[Top.scala 409:22]
    .valid_up(n132_valid_up),
    .valid_down(n132_valid_down),
    .I0(n132_I0),
    .I1(n132_I1),
    .O_t0b(n132_O_t0b),
    .O_t1b(n132_O_t1b)
  );
  Add n133 ( // @[Top.scala 413:22]
    .valid_up(n133_valid_up),
    .valid_down(n133_valid_down),
    .I_t0b(n133_I_t0b),
    .I_t1b(n133_I_t1b),
    .O(n133_O)
  );
  InitialDelayCounter_12 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n136 ( // @[Top.scala 417:22]
    .valid_up(n136_valid_up),
    .valid_down(n136_valid_down),
    .I0(n136_I0),
    .O_t0b(n136_O_t0b)
  );
  RShift n137 ( // @[Top.scala 421:22]
    .valid_up(n137_valid_up),
    .valid_down(n137_valid_down),
    .I_t0b(n137_I_t0b),
    .O(n137_O)
  );
  AtomTuple_1 n138 ( // @[Top.scala 424:22]
    .valid_up(n138_valid_up),
    .valid_down(n138_valid_down),
    .I0(n138_I0),
    .I1(n138_I1),
    .O_t0b(n138_O_t0b),
    .O_t1b(n138_O_t1b)
  );
  Eq n139 ( // @[Top.scala 428:22]
    .valid_up(n139_valid_up),
    .valid_down(n139_valid_down),
    .I_t0b(n139_I_t0b),
    .I_t1b(n139_I_t1b),
    .O(n139_O)
  );
  InitialDelayCounter_12 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n142 ( // @[Top.scala 432:22]
    .valid_up(n142_valid_up),
    .valid_down(n142_valid_down),
    .I0(n142_I0),
    .I1(n142_I1),
    .O_t0b(n142_O_t0b),
    .O_t1b(n142_O_t1b)
  );
  Add n143 ( // @[Top.scala 436:22]
    .valid_up(n143_valid_up),
    .valid_down(n143_valid_down),
    .I_t0b(n143_I_t0b),
    .I_t1b(n143_I_t1b),
    .O(n143_O)
  );
  AtomTuple_1 n144 ( // @[Top.scala 439:22]
    .valid_up(n144_valid_up),
    .valid_down(n144_valid_down),
    .I0(n144_I0),
    .I1(n144_I1),
    .O_t0b(n144_O_t0b),
    .O_t1b(n144_O_t1b)
  );
  AtomTuple_9 n145 ( // @[Top.scala 443:22]
    .valid_up(n145_valid_up),
    .valid_down(n145_valid_down),
    .I0(n145_I0),
    .I1_t0b(n145_I1_t0b),
    .I1_t1b(n145_I1_t1b),
    .O_t0b(n145_O_t0b),
    .O_t1b_t0b(n145_O_t1b_t0b),
    .O_t1b_t1b(n145_O_t1b_t1b)
  );
  If n146 ( // @[Top.scala 447:22]
    .valid_up(n146_valid_up),
    .valid_down(n146_valid_down),
    .I_t0b(n146_I_t0b),
    .I_t1b_t0b(n146_I_t1b_t0b),
    .I_t1b_t1b(n146_I_t1b_t1b),
    .O(n146_O)
  );
  AtomTuple_1 n147 ( // @[Top.scala 450:22]
    .valid_up(n147_valid_up),
    .valid_down(n147_valid_down),
    .I0(n147_I0),
    .I1(n147_I1),
    .O_t0b(n147_O_t0b),
    .O_t1b(n147_O_t1b)
  );
  Mul n148 ( // @[Top.scala 454:22]
    .clock(n148_clock),
    .reset(n148_reset),
    .valid_up(n148_valid_up),
    .valid_down(n148_valid_down),
    .I_t0b(n148_I_t0b),
    .I_t1b(n148_I_t1b),
    .O(n148_O)
  );
  AtomTuple_1 n150 ( // @[Top.scala 457:22]
    .valid_up(n150_valid_up),
    .valid_down(n150_valid_down),
    .I0(n150_I0),
    .I1(n150_I1),
    .O_t0b(n150_O_t0b),
    .O_t1b(n150_O_t1b)
  );
  Lt n151 ( // @[Top.scala 461:22]
    .valid_up(n151_valid_up),
    .valid_down(n151_valid_down),
    .I_t0b(n151_I_t0b),
    .I_t1b(n151_I_t1b),
    .O(n151_O)
  );
  InitialDelayCounter_12 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n153 ( // @[Top.scala 465:22]
    .valid_up(n153_valid_up),
    .valid_down(n153_valid_down),
    .I0(n153_I0),
    .I1(n153_I1),
    .O_t0b(n153_O_t0b),
    .O_t1b(n153_O_t1b)
  );
  Sub n154 ( // @[Top.scala 469:22]
    .valid_up(n154_valid_up),
    .valid_down(n154_valid_down),
    .I_t0b(n154_I_t0b),
    .I_t1b(n154_I_t1b),
    .O(n154_O)
  );
  AtomTuple_1 n155 ( // @[Top.scala 472:22]
    .valid_up(n155_valid_up),
    .valid_down(n155_valid_down),
    .I0(n155_I0),
    .I1(n155_I1),
    .O_t0b(n155_O_t0b),
    .O_t1b(n155_O_t1b)
  );
  AtomTuple_1 n156 ( // @[Top.scala 476:22]
    .valid_up(n156_valid_up),
    .valid_down(n156_valid_down),
    .I0(n156_I0),
    .I1(n156_I1),
    .O_t0b(n156_O_t0b),
    .O_t1b(n156_O_t1b)
  );
  AtomTuple_15 n157 ( // @[Top.scala 480:22]
    .valid_up(n157_valid_up),
    .valid_down(n157_valid_down),
    .I0_t0b(n157_I0_t0b),
    .I0_t1b(n157_I0_t1b),
    .I1_t0b(n157_I1_t0b),
    .I1_t1b(n157_I1_t1b),
    .O_t0b_t0b(n157_O_t0b_t0b),
    .O_t0b_t1b(n157_O_t0b_t1b),
    .O_t1b_t0b(n157_O_t1b_t0b),
    .O_t1b_t1b(n157_O_t1b_t1b)
  );
  FIFO_3 n158 ( // @[Top.scala 484:22]
    .clock(n158_clock),
    .reset(n158_reset),
    .valid_up(n158_valid_up),
    .valid_down(n158_valid_down),
    .I_t0b_t0b(n158_I_t0b_t0b),
    .I_t0b_t1b(n158_I_t0b_t1b),
    .I_t1b_t0b(n158_I_t1b_t0b),
    .I_t1b_t1b(n158_I_t1b_t1b),
    .O_t0b_t0b(n158_O_t0b_t0b),
    .O_t0b_t1b(n158_O_t0b_t1b),
    .O_t1b_t0b(n158_O_t1b_t0b),
    .O_t1b_t1b(n158_O_t1b_t1b)
  );
  AtomTuple_16 n159 ( // @[Top.scala 487:22]
    .valid_up(n159_valid_up),
    .valid_down(n159_valid_down),
    .I0(n159_I0),
    .I1_t0b_t0b(n159_I1_t0b_t0b),
    .I1_t0b_t1b(n159_I1_t0b_t1b),
    .I1_t1b_t0b(n159_I1_t1b_t0b),
    .I1_t1b_t1b(n159_I1_t1b_t1b),
    .O_t0b(n159_O_t0b),
    .O_t1b_t0b_t0b(n159_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n159_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n159_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n159_O_t1b_t1b_t1b)
  );
  If_1 n160 ( // @[Top.scala 491:22]
    .valid_up(n160_valid_up),
    .valid_down(n160_valid_down),
    .I_t0b(n160_I_t0b),
    .I_t1b_t0b_t0b(n160_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n160_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n160_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n160_I_t1b_t1b_t1b),
    .O_t0b(n160_O_t0b),
    .O_t1b(n160_O_t1b)
  );
  AtomTuple_3 n162 ( // @[Top.scala 494:22]
    .valid_up(n162_valid_up),
    .valid_down(n162_valid_down),
    .I0(n162_I0),
    .I1_t0b(n162_I1_t0b),
    .I1_t1b(n162_I1_t1b),
    .O_t0b(n162_O_t0b),
    .O_t1b_t0b(n162_O_t1b_t0b),
    .O_t1b_t1b(n162_O_t1b_t1b)
  );
  assign valid_down = n162_valid_down; // @[Top.scala 499:16]
  assign O_t0b = n162_O_t0b; // @[Top.scala 498:7]
  assign O_t1b_t0b = n162_O_t1b_t0b; // @[Top.scala 498:7]
  assign O_t1b_t1b = n162_O_t1b_t1b; // @[Top.scala 498:7]
  assign n128_valid_up = valid_up; // @[Top.scala 393:19]
  assign n128_I_t0b = I_t0b; // @[Top.scala 392:12]
  assign n161_clock = clock;
  assign n161_reset = reset;
  assign n161_valid_up = n128_valid_down; // @[Top.scala 396:19]
  assign n161_I = n128_O; // @[Top.scala 395:12]
  assign n149_clock = clock;
  assign n149_reset = reset;
  assign n149_valid_up = n128_valid_down; // @[Top.scala 399:19]
  assign n149_I = n128_O; // @[Top.scala 398:12]
  assign n129_valid_up = valid_up; // @[Top.scala 402:19]
  assign n129_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 401:12]
  assign n129_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 401:12]
  assign n130_valid_up = n129_valid_down; // @[Top.scala 405:19]
  assign n130_I_t0b = n129_O_t0b; // @[Top.scala 404:12]
  assign n131_valid_up = n129_valid_down; // @[Top.scala 408:19]
  assign n131_I_t1b = n129_O_t1b; // @[Top.scala 407:12]
  assign n132_valid_up = n130_valid_down & n131_valid_down; // @[Top.scala 412:19]
  assign n132_I0 = n130_O; // @[Top.scala 410:13]
  assign n132_I1 = n131_O; // @[Top.scala 411:13]
  assign n133_valid_up = n132_valid_down; // @[Top.scala 415:19]
  assign n133_I_t0b = n132_O_t0b; // @[Top.scala 414:12]
  assign n133_I_t1b = n132_O_t1b; // @[Top.scala 414:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n136_valid_up = n133_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 420:19]
  assign n136_I0 = n133_O; // @[Top.scala 418:13]
  assign n137_valid_up = n136_valid_down; // @[Top.scala 423:19]
  assign n137_I_t0b = n136_O_t0b; // @[Top.scala 422:12]
  assign n138_valid_up = n137_valid_down & n130_valid_down; // @[Top.scala 427:19]
  assign n138_I0 = n137_O; // @[Top.scala 425:13]
  assign n138_I1 = n130_O; // @[Top.scala 426:13]
  assign n139_valid_up = n138_valid_down; // @[Top.scala 430:19]
  assign n139_I_t0b = n138_O_t0b; // @[Top.scala 429:12]
  assign n139_I_t1b = n138_O_t1b; // @[Top.scala 429:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n142_valid_up = n137_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 435:19]
  assign n142_I0 = n137_O; // @[Top.scala 433:13]
  assign n142_I1 = 16'h1; // @[Top.scala 434:13]
  assign n143_valid_up = n142_valid_down; // @[Top.scala 438:19]
  assign n143_I_t0b = n142_O_t0b; // @[Top.scala 437:12]
  assign n143_I_t1b = n142_O_t1b; // @[Top.scala 437:12]
  assign n144_valid_up = n143_valid_down & n137_valid_down; // @[Top.scala 442:19]
  assign n144_I0 = n143_O; // @[Top.scala 440:13]
  assign n144_I1 = n137_O; // @[Top.scala 441:13]
  assign n145_valid_up = n139_valid_down & n144_valid_down; // @[Top.scala 446:19]
  assign n145_I0 = n139_O[0]; // @[Top.scala 444:13]
  assign n145_I1_t0b = n144_O_t0b; // @[Top.scala 445:13]
  assign n145_I1_t1b = n144_O_t1b; // @[Top.scala 445:13]
  assign n146_valid_up = n145_valid_down; // @[Top.scala 449:19]
  assign n146_I_t0b = n145_O_t0b; // @[Top.scala 448:12]
  assign n146_I_t1b_t0b = n145_O_t1b_t0b; // @[Top.scala 448:12]
  assign n146_I_t1b_t1b = n145_O_t1b_t1b; // @[Top.scala 448:12]
  assign n147_valid_up = n146_valid_down; // @[Top.scala 453:19]
  assign n147_I0 = n146_O; // @[Top.scala 451:13]
  assign n147_I1 = n146_O; // @[Top.scala 452:13]
  assign n148_clock = clock;
  assign n148_reset = reset;
  assign n148_valid_up = n147_valid_down; // @[Top.scala 456:19]
  assign n148_I_t0b = n147_O_t0b; // @[Top.scala 455:12]
  assign n148_I_t1b = n147_O_t1b; // @[Top.scala 455:12]
  assign n150_valid_up = n149_valid_down & n148_valid_down; // @[Top.scala 460:19]
  assign n150_I0 = n149_O; // @[Top.scala 458:13]
  assign n150_I1 = n148_O; // @[Top.scala 459:13]
  assign n151_valid_up = n150_valid_down; // @[Top.scala 463:19]
  assign n151_I_t0b = n150_O_t0b; // @[Top.scala 462:12]
  assign n151_I_t1b = n150_O_t1b; // @[Top.scala 462:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n153_valid_up = n146_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 468:19]
  assign n153_I0 = n146_O; // @[Top.scala 466:13]
  assign n153_I1 = 16'h1; // @[Top.scala 467:13]
  assign n154_valid_up = n153_valid_down; // @[Top.scala 471:19]
  assign n154_I_t0b = n153_O_t0b; // @[Top.scala 470:12]
  assign n154_I_t1b = n153_O_t1b; // @[Top.scala 470:12]
  assign n155_valid_up = n130_valid_down & n154_valid_down; // @[Top.scala 475:19]
  assign n155_I0 = n130_O; // @[Top.scala 473:13]
  assign n155_I1 = n154_O; // @[Top.scala 474:13]
  assign n156_valid_up = n146_valid_down & n131_valid_down; // @[Top.scala 479:19]
  assign n156_I0 = n146_O; // @[Top.scala 477:13]
  assign n156_I1 = n131_O; // @[Top.scala 478:13]
  assign n157_valid_up = n155_valid_down & n156_valid_down; // @[Top.scala 483:19]
  assign n157_I0_t0b = n155_O_t0b; // @[Top.scala 481:13]
  assign n157_I0_t1b = n155_O_t1b; // @[Top.scala 481:13]
  assign n157_I1_t0b = n156_O_t0b; // @[Top.scala 482:13]
  assign n157_I1_t1b = n156_O_t1b; // @[Top.scala 482:13]
  assign n158_clock = clock;
  assign n158_reset = reset;
  assign n158_valid_up = n157_valid_down; // @[Top.scala 486:19]
  assign n158_I_t0b_t0b = n157_O_t0b_t0b; // @[Top.scala 485:12]
  assign n158_I_t0b_t1b = n157_O_t0b_t1b; // @[Top.scala 485:12]
  assign n158_I_t1b_t0b = n157_O_t1b_t0b; // @[Top.scala 485:12]
  assign n158_I_t1b_t1b = n157_O_t1b_t1b; // @[Top.scala 485:12]
  assign n159_valid_up = n151_valid_down & n158_valid_down; // @[Top.scala 490:19]
  assign n159_I0 = n151_O[0]; // @[Top.scala 488:13]
  assign n159_I1_t0b_t0b = n158_O_t0b_t0b; // @[Top.scala 489:13]
  assign n159_I1_t0b_t1b = n158_O_t0b_t1b; // @[Top.scala 489:13]
  assign n159_I1_t1b_t0b = n158_O_t1b_t0b; // @[Top.scala 489:13]
  assign n159_I1_t1b_t1b = n158_O_t1b_t1b; // @[Top.scala 489:13]
  assign n160_valid_up = n159_valid_down; // @[Top.scala 493:19]
  assign n160_I_t0b = n159_O_t0b; // @[Top.scala 492:12]
  assign n160_I_t1b_t0b_t0b = n159_O_t1b_t0b_t0b; // @[Top.scala 492:12]
  assign n160_I_t1b_t0b_t1b = n159_O_t1b_t0b_t1b; // @[Top.scala 492:12]
  assign n160_I_t1b_t1b_t0b = n159_O_t1b_t1b_t0b; // @[Top.scala 492:12]
  assign n160_I_t1b_t1b_t1b = n159_O_t1b_t1b_t1b; // @[Top.scala 492:12]
  assign n162_valid_up = n161_valid_down & n160_valid_down; // @[Top.scala 497:19]
  assign n162_I0 = n161_O; // @[Top.scala 495:13]
  assign n162_I1_t0b = n160_O_t0b; // @[Top.scala 496:13]
  assign n162_I1_t1b = n160_O_t1b; // @[Top.scala 496:13]
endmodule
module MapT_4(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_4 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
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
  wire  n165_valid_up; // @[Top.scala 505:22]
  wire  n165_valid_down; // @[Top.scala 505:22]
  wire [15:0] n165_I_t0b; // @[Top.scala 505:22]
  wire [15:0] n165_O; // @[Top.scala 505:22]
  wire  n198_clock; // @[Top.scala 508:22]
  wire  n198_reset; // @[Top.scala 508:22]
  wire  n198_valid_up; // @[Top.scala 508:22]
  wire  n198_valid_down; // @[Top.scala 508:22]
  wire [15:0] n198_I; // @[Top.scala 508:22]
  wire [15:0] n198_O; // @[Top.scala 508:22]
  wire  n186_clock; // @[Top.scala 511:22]
  wire  n186_reset; // @[Top.scala 511:22]
  wire  n186_valid_up; // @[Top.scala 511:22]
  wire  n186_valid_down; // @[Top.scala 511:22]
  wire [15:0] n186_I; // @[Top.scala 511:22]
  wire [15:0] n186_O; // @[Top.scala 511:22]
  wire  n166_valid_up; // @[Top.scala 514:22]
  wire  n166_valid_down; // @[Top.scala 514:22]
  wire [15:0] n166_I_t1b_t0b; // @[Top.scala 514:22]
  wire [15:0] n166_I_t1b_t1b; // @[Top.scala 514:22]
  wire [15:0] n166_O_t0b; // @[Top.scala 514:22]
  wire [15:0] n166_O_t1b; // @[Top.scala 514:22]
  wire  n167_valid_up; // @[Top.scala 517:22]
  wire  n167_valid_down; // @[Top.scala 517:22]
  wire [15:0] n167_I_t0b; // @[Top.scala 517:22]
  wire [15:0] n167_O; // @[Top.scala 517:22]
  wire  n168_valid_up; // @[Top.scala 520:22]
  wire  n168_valid_down; // @[Top.scala 520:22]
  wire [15:0] n168_I_t1b; // @[Top.scala 520:22]
  wire [15:0] n168_O; // @[Top.scala 520:22]
  wire  n169_valid_up; // @[Top.scala 523:22]
  wire  n169_valid_down; // @[Top.scala 523:22]
  wire [15:0] n169_I0; // @[Top.scala 523:22]
  wire [15:0] n169_I1; // @[Top.scala 523:22]
  wire [15:0] n169_O_t0b; // @[Top.scala 523:22]
  wire [15:0] n169_O_t1b; // @[Top.scala 523:22]
  wire  n170_valid_up; // @[Top.scala 527:22]
  wire  n170_valid_down; // @[Top.scala 527:22]
  wire [15:0] n170_I_t0b; // @[Top.scala 527:22]
  wire [15:0] n170_I_t1b; // @[Top.scala 527:22]
  wire [15:0] n170_O; // @[Top.scala 527:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n173_valid_up; // @[Top.scala 531:22]
  wire  n173_valid_down; // @[Top.scala 531:22]
  wire [15:0] n173_I0; // @[Top.scala 531:22]
  wire [15:0] n173_O_t0b; // @[Top.scala 531:22]
  wire  n174_valid_up; // @[Top.scala 535:22]
  wire  n174_valid_down; // @[Top.scala 535:22]
  wire [15:0] n174_I_t0b; // @[Top.scala 535:22]
  wire [15:0] n174_O; // @[Top.scala 535:22]
  wire  n175_valid_up; // @[Top.scala 538:22]
  wire  n175_valid_down; // @[Top.scala 538:22]
  wire [15:0] n175_I0; // @[Top.scala 538:22]
  wire [15:0] n175_I1; // @[Top.scala 538:22]
  wire [15:0] n175_O_t0b; // @[Top.scala 538:22]
  wire [15:0] n175_O_t1b; // @[Top.scala 538:22]
  wire  n176_valid_up; // @[Top.scala 542:22]
  wire  n176_valid_down; // @[Top.scala 542:22]
  wire [15:0] n176_I_t0b; // @[Top.scala 542:22]
  wire [15:0] n176_I_t1b; // @[Top.scala 542:22]
  wire [15:0] n176_O; // @[Top.scala 542:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n179_valid_up; // @[Top.scala 546:22]
  wire  n179_valid_down; // @[Top.scala 546:22]
  wire [15:0] n179_I0; // @[Top.scala 546:22]
  wire [15:0] n179_I1; // @[Top.scala 546:22]
  wire [15:0] n179_O_t0b; // @[Top.scala 546:22]
  wire [15:0] n179_O_t1b; // @[Top.scala 546:22]
  wire  n180_valid_up; // @[Top.scala 550:22]
  wire  n180_valid_down; // @[Top.scala 550:22]
  wire [15:0] n180_I_t0b; // @[Top.scala 550:22]
  wire [15:0] n180_I_t1b; // @[Top.scala 550:22]
  wire [15:0] n180_O; // @[Top.scala 550:22]
  wire  n181_valid_up; // @[Top.scala 553:22]
  wire  n181_valid_down; // @[Top.scala 553:22]
  wire [15:0] n181_I0; // @[Top.scala 553:22]
  wire [15:0] n181_I1; // @[Top.scala 553:22]
  wire [15:0] n181_O_t0b; // @[Top.scala 553:22]
  wire [15:0] n181_O_t1b; // @[Top.scala 553:22]
  wire  n182_valid_up; // @[Top.scala 557:22]
  wire  n182_valid_down; // @[Top.scala 557:22]
  wire  n182_I0; // @[Top.scala 557:22]
  wire [15:0] n182_I1_t0b; // @[Top.scala 557:22]
  wire [15:0] n182_I1_t1b; // @[Top.scala 557:22]
  wire  n182_O_t0b; // @[Top.scala 557:22]
  wire [15:0] n182_O_t1b_t0b; // @[Top.scala 557:22]
  wire [15:0] n182_O_t1b_t1b; // @[Top.scala 557:22]
  wire  n183_valid_up; // @[Top.scala 561:22]
  wire  n183_valid_down; // @[Top.scala 561:22]
  wire  n183_I_t0b; // @[Top.scala 561:22]
  wire [15:0] n183_I_t1b_t0b; // @[Top.scala 561:22]
  wire [15:0] n183_I_t1b_t1b; // @[Top.scala 561:22]
  wire [15:0] n183_O; // @[Top.scala 561:22]
  wire  n184_valid_up; // @[Top.scala 564:22]
  wire  n184_valid_down; // @[Top.scala 564:22]
  wire [15:0] n184_I0; // @[Top.scala 564:22]
  wire [15:0] n184_I1; // @[Top.scala 564:22]
  wire [15:0] n184_O_t0b; // @[Top.scala 564:22]
  wire [15:0] n184_O_t1b; // @[Top.scala 564:22]
  wire  n185_clock; // @[Top.scala 568:22]
  wire  n185_reset; // @[Top.scala 568:22]
  wire  n185_valid_up; // @[Top.scala 568:22]
  wire  n185_valid_down; // @[Top.scala 568:22]
  wire [15:0] n185_I_t0b; // @[Top.scala 568:22]
  wire [15:0] n185_I_t1b; // @[Top.scala 568:22]
  wire [15:0] n185_O; // @[Top.scala 568:22]
  wire  n187_valid_up; // @[Top.scala 571:22]
  wire  n187_valid_down; // @[Top.scala 571:22]
  wire [15:0] n187_I0; // @[Top.scala 571:22]
  wire [15:0] n187_I1; // @[Top.scala 571:22]
  wire [15:0] n187_O_t0b; // @[Top.scala 571:22]
  wire [15:0] n187_O_t1b; // @[Top.scala 571:22]
  wire  n188_valid_up; // @[Top.scala 575:22]
  wire  n188_valid_down; // @[Top.scala 575:22]
  wire [15:0] n188_I_t0b; // @[Top.scala 575:22]
  wire [15:0] n188_I_t1b; // @[Top.scala 575:22]
  wire [15:0] n188_O; // @[Top.scala 575:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n190_valid_up; // @[Top.scala 579:22]
  wire  n190_valid_down; // @[Top.scala 579:22]
  wire [15:0] n190_I0; // @[Top.scala 579:22]
  wire [15:0] n190_I1; // @[Top.scala 579:22]
  wire [15:0] n190_O_t0b; // @[Top.scala 579:22]
  wire [15:0] n190_O_t1b; // @[Top.scala 579:22]
  wire  n191_valid_up; // @[Top.scala 583:22]
  wire  n191_valid_down; // @[Top.scala 583:22]
  wire [15:0] n191_I_t0b; // @[Top.scala 583:22]
  wire [15:0] n191_I_t1b; // @[Top.scala 583:22]
  wire [15:0] n191_O; // @[Top.scala 583:22]
  wire  n192_valid_up; // @[Top.scala 586:22]
  wire  n192_valid_down; // @[Top.scala 586:22]
  wire [15:0] n192_I0; // @[Top.scala 586:22]
  wire [15:0] n192_I1; // @[Top.scala 586:22]
  wire [15:0] n192_O_t0b; // @[Top.scala 586:22]
  wire [15:0] n192_O_t1b; // @[Top.scala 586:22]
  wire  n193_valid_up; // @[Top.scala 590:22]
  wire  n193_valid_down; // @[Top.scala 590:22]
  wire [15:0] n193_I0; // @[Top.scala 590:22]
  wire [15:0] n193_I1; // @[Top.scala 590:22]
  wire [15:0] n193_O_t0b; // @[Top.scala 590:22]
  wire [15:0] n193_O_t1b; // @[Top.scala 590:22]
  wire  n194_valid_up; // @[Top.scala 594:22]
  wire  n194_valid_down; // @[Top.scala 594:22]
  wire [15:0] n194_I0_t0b; // @[Top.scala 594:22]
  wire [15:0] n194_I0_t1b; // @[Top.scala 594:22]
  wire [15:0] n194_I1_t0b; // @[Top.scala 594:22]
  wire [15:0] n194_I1_t1b; // @[Top.scala 594:22]
  wire [15:0] n194_O_t0b_t0b; // @[Top.scala 594:22]
  wire [15:0] n194_O_t0b_t1b; // @[Top.scala 594:22]
  wire [15:0] n194_O_t1b_t0b; // @[Top.scala 594:22]
  wire [15:0] n194_O_t1b_t1b; // @[Top.scala 594:22]
  wire  n195_clock; // @[Top.scala 598:22]
  wire  n195_reset; // @[Top.scala 598:22]
  wire  n195_valid_up; // @[Top.scala 598:22]
  wire  n195_valid_down; // @[Top.scala 598:22]
  wire [15:0] n195_I_t0b_t0b; // @[Top.scala 598:22]
  wire [15:0] n195_I_t0b_t1b; // @[Top.scala 598:22]
  wire [15:0] n195_I_t1b_t0b; // @[Top.scala 598:22]
  wire [15:0] n195_I_t1b_t1b; // @[Top.scala 598:22]
  wire [15:0] n195_O_t0b_t0b; // @[Top.scala 598:22]
  wire [15:0] n195_O_t0b_t1b; // @[Top.scala 598:22]
  wire [15:0] n195_O_t1b_t0b; // @[Top.scala 598:22]
  wire [15:0] n195_O_t1b_t1b; // @[Top.scala 598:22]
  wire  n196_valid_up; // @[Top.scala 601:22]
  wire  n196_valid_down; // @[Top.scala 601:22]
  wire  n196_I0; // @[Top.scala 601:22]
  wire [15:0] n196_I1_t0b_t0b; // @[Top.scala 601:22]
  wire [15:0] n196_I1_t0b_t1b; // @[Top.scala 601:22]
  wire [15:0] n196_I1_t1b_t0b; // @[Top.scala 601:22]
  wire [15:0] n196_I1_t1b_t1b; // @[Top.scala 601:22]
  wire  n196_O_t0b; // @[Top.scala 601:22]
  wire [15:0] n196_O_t1b_t0b_t0b; // @[Top.scala 601:22]
  wire [15:0] n196_O_t1b_t0b_t1b; // @[Top.scala 601:22]
  wire [15:0] n196_O_t1b_t1b_t0b; // @[Top.scala 601:22]
  wire [15:0] n196_O_t1b_t1b_t1b; // @[Top.scala 601:22]
  wire  n197_valid_up; // @[Top.scala 605:22]
  wire  n197_valid_down; // @[Top.scala 605:22]
  wire  n197_I_t0b; // @[Top.scala 605:22]
  wire [15:0] n197_I_t1b_t0b_t0b; // @[Top.scala 605:22]
  wire [15:0] n197_I_t1b_t0b_t1b; // @[Top.scala 605:22]
  wire [15:0] n197_I_t1b_t1b_t0b; // @[Top.scala 605:22]
  wire [15:0] n197_I_t1b_t1b_t1b; // @[Top.scala 605:22]
  wire [15:0] n197_O_t0b; // @[Top.scala 605:22]
  wire [15:0] n197_O_t1b; // @[Top.scala 605:22]
  wire  n199_valid_up; // @[Top.scala 608:22]
  wire  n199_valid_down; // @[Top.scala 608:22]
  wire [15:0] n199_I0; // @[Top.scala 608:22]
  wire [15:0] n199_I1_t0b; // @[Top.scala 608:22]
  wire [15:0] n199_I1_t1b; // @[Top.scala 608:22]
  wire [15:0] n199_O_t0b; // @[Top.scala 608:22]
  wire [15:0] n199_O_t1b_t0b; // @[Top.scala 608:22]
  wire [15:0] n199_O_t1b_t1b; // @[Top.scala 608:22]
  Fst n165 ( // @[Top.scala 505:22]
    .valid_up(n165_valid_up),
    .valid_down(n165_valid_down),
    .I_t0b(n165_I_t0b),
    .O(n165_O)
  );
  FIFO_1 n198 ( // @[Top.scala 508:22]
    .clock(n198_clock),
    .reset(n198_reset),
    .valid_up(n198_valid_up),
    .valid_down(n198_valid_down),
    .I(n198_I),
    .O(n198_O)
  );
  FIFO_1 n186 ( // @[Top.scala 511:22]
    .clock(n186_clock),
    .reset(n186_reset),
    .valid_up(n186_valid_up),
    .valid_down(n186_valid_down),
    .I(n186_I),
    .O(n186_O)
  );
  Snd n166 ( // @[Top.scala 514:22]
    .valid_up(n166_valid_up),
    .valid_down(n166_valid_down),
    .I_t1b_t0b(n166_I_t1b_t0b),
    .I_t1b_t1b(n166_I_t1b_t1b),
    .O_t0b(n166_O_t0b),
    .O_t1b(n166_O_t1b)
  );
  Fst_1 n167 ( // @[Top.scala 517:22]
    .valid_up(n167_valid_up),
    .valid_down(n167_valid_down),
    .I_t0b(n167_I_t0b),
    .O(n167_O)
  );
  Snd_1 n168 ( // @[Top.scala 520:22]
    .valid_up(n168_valid_up),
    .valid_down(n168_valid_down),
    .I_t1b(n168_I_t1b),
    .O(n168_O)
  );
  AtomTuple_1 n169 ( // @[Top.scala 523:22]
    .valid_up(n169_valid_up),
    .valid_down(n169_valid_down),
    .I0(n169_I0),
    .I1(n169_I1),
    .O_t0b(n169_O_t0b),
    .O_t1b(n169_O_t1b)
  );
  Add n170 ( // @[Top.scala 527:22]
    .valid_up(n170_valid_up),
    .valid_down(n170_valid_down),
    .I_t0b(n170_I_t0b),
    .I_t1b(n170_I_t1b),
    .O(n170_O)
  );
  InitialDelayCounter_15 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n173 ( // @[Top.scala 531:22]
    .valid_up(n173_valid_up),
    .valid_down(n173_valid_down),
    .I0(n173_I0),
    .O_t0b(n173_O_t0b)
  );
  RShift n174 ( // @[Top.scala 535:22]
    .valid_up(n174_valid_up),
    .valid_down(n174_valid_down),
    .I_t0b(n174_I_t0b),
    .O(n174_O)
  );
  AtomTuple_1 n175 ( // @[Top.scala 538:22]
    .valid_up(n175_valid_up),
    .valid_down(n175_valid_down),
    .I0(n175_I0),
    .I1(n175_I1),
    .O_t0b(n175_O_t0b),
    .O_t1b(n175_O_t1b)
  );
  Eq n176 ( // @[Top.scala 542:22]
    .valid_up(n176_valid_up),
    .valid_down(n176_valid_down),
    .I_t0b(n176_I_t0b),
    .I_t1b(n176_I_t1b),
    .O(n176_O)
  );
  InitialDelayCounter_15 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n179 ( // @[Top.scala 546:22]
    .valid_up(n179_valid_up),
    .valid_down(n179_valid_down),
    .I0(n179_I0),
    .I1(n179_I1),
    .O_t0b(n179_O_t0b),
    .O_t1b(n179_O_t1b)
  );
  Add n180 ( // @[Top.scala 550:22]
    .valid_up(n180_valid_up),
    .valid_down(n180_valid_down),
    .I_t0b(n180_I_t0b),
    .I_t1b(n180_I_t1b),
    .O(n180_O)
  );
  AtomTuple_1 n181 ( // @[Top.scala 553:22]
    .valid_up(n181_valid_up),
    .valid_down(n181_valid_down),
    .I0(n181_I0),
    .I1(n181_I1),
    .O_t0b(n181_O_t0b),
    .O_t1b(n181_O_t1b)
  );
  AtomTuple_9 n182 ( // @[Top.scala 557:22]
    .valid_up(n182_valid_up),
    .valid_down(n182_valid_down),
    .I0(n182_I0),
    .I1_t0b(n182_I1_t0b),
    .I1_t1b(n182_I1_t1b),
    .O_t0b(n182_O_t0b),
    .O_t1b_t0b(n182_O_t1b_t0b),
    .O_t1b_t1b(n182_O_t1b_t1b)
  );
  If n183 ( // @[Top.scala 561:22]
    .valid_up(n183_valid_up),
    .valid_down(n183_valid_down),
    .I_t0b(n183_I_t0b),
    .I_t1b_t0b(n183_I_t1b_t0b),
    .I_t1b_t1b(n183_I_t1b_t1b),
    .O(n183_O)
  );
  AtomTuple_1 n184 ( // @[Top.scala 564:22]
    .valid_up(n184_valid_up),
    .valid_down(n184_valid_down),
    .I0(n184_I0),
    .I1(n184_I1),
    .O_t0b(n184_O_t0b),
    .O_t1b(n184_O_t1b)
  );
  Mul n185 ( // @[Top.scala 568:22]
    .clock(n185_clock),
    .reset(n185_reset),
    .valid_up(n185_valid_up),
    .valid_down(n185_valid_down),
    .I_t0b(n185_I_t0b),
    .I_t1b(n185_I_t1b),
    .O(n185_O)
  );
  AtomTuple_1 n187 ( // @[Top.scala 571:22]
    .valid_up(n187_valid_up),
    .valid_down(n187_valid_down),
    .I0(n187_I0),
    .I1(n187_I1),
    .O_t0b(n187_O_t0b),
    .O_t1b(n187_O_t1b)
  );
  Lt n188 ( // @[Top.scala 575:22]
    .valid_up(n188_valid_up),
    .valid_down(n188_valid_down),
    .I_t0b(n188_I_t0b),
    .I_t1b(n188_I_t1b),
    .O(n188_O)
  );
  InitialDelayCounter_15 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n190 ( // @[Top.scala 579:22]
    .valid_up(n190_valid_up),
    .valid_down(n190_valid_down),
    .I0(n190_I0),
    .I1(n190_I1),
    .O_t0b(n190_O_t0b),
    .O_t1b(n190_O_t1b)
  );
  Sub n191 ( // @[Top.scala 583:22]
    .valid_up(n191_valid_up),
    .valid_down(n191_valid_down),
    .I_t0b(n191_I_t0b),
    .I_t1b(n191_I_t1b),
    .O(n191_O)
  );
  AtomTuple_1 n192 ( // @[Top.scala 586:22]
    .valid_up(n192_valid_up),
    .valid_down(n192_valid_down),
    .I0(n192_I0),
    .I1(n192_I1),
    .O_t0b(n192_O_t0b),
    .O_t1b(n192_O_t1b)
  );
  AtomTuple_1 n193 ( // @[Top.scala 590:22]
    .valid_up(n193_valid_up),
    .valid_down(n193_valid_down),
    .I0(n193_I0),
    .I1(n193_I1),
    .O_t0b(n193_O_t0b),
    .O_t1b(n193_O_t1b)
  );
  AtomTuple_15 n194 ( // @[Top.scala 594:22]
    .valid_up(n194_valid_up),
    .valid_down(n194_valid_down),
    .I0_t0b(n194_I0_t0b),
    .I0_t1b(n194_I0_t1b),
    .I1_t0b(n194_I1_t0b),
    .I1_t1b(n194_I1_t1b),
    .O_t0b_t0b(n194_O_t0b_t0b),
    .O_t0b_t1b(n194_O_t0b_t1b),
    .O_t1b_t0b(n194_O_t1b_t0b),
    .O_t1b_t1b(n194_O_t1b_t1b)
  );
  FIFO_3 n195 ( // @[Top.scala 598:22]
    .clock(n195_clock),
    .reset(n195_reset),
    .valid_up(n195_valid_up),
    .valid_down(n195_valid_down),
    .I_t0b_t0b(n195_I_t0b_t0b),
    .I_t0b_t1b(n195_I_t0b_t1b),
    .I_t1b_t0b(n195_I_t1b_t0b),
    .I_t1b_t1b(n195_I_t1b_t1b),
    .O_t0b_t0b(n195_O_t0b_t0b),
    .O_t0b_t1b(n195_O_t0b_t1b),
    .O_t1b_t0b(n195_O_t1b_t0b),
    .O_t1b_t1b(n195_O_t1b_t1b)
  );
  AtomTuple_16 n196 ( // @[Top.scala 601:22]
    .valid_up(n196_valid_up),
    .valid_down(n196_valid_down),
    .I0(n196_I0),
    .I1_t0b_t0b(n196_I1_t0b_t0b),
    .I1_t0b_t1b(n196_I1_t0b_t1b),
    .I1_t1b_t0b(n196_I1_t1b_t0b),
    .I1_t1b_t1b(n196_I1_t1b_t1b),
    .O_t0b(n196_O_t0b),
    .O_t1b_t0b_t0b(n196_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n196_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n196_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n196_O_t1b_t1b_t1b)
  );
  If_1 n197 ( // @[Top.scala 605:22]
    .valid_up(n197_valid_up),
    .valid_down(n197_valid_down),
    .I_t0b(n197_I_t0b),
    .I_t1b_t0b_t0b(n197_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n197_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n197_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n197_I_t1b_t1b_t1b),
    .O_t0b(n197_O_t0b),
    .O_t1b(n197_O_t1b)
  );
  AtomTuple_3 n199 ( // @[Top.scala 608:22]
    .valid_up(n199_valid_up),
    .valid_down(n199_valid_down),
    .I0(n199_I0),
    .I1_t0b(n199_I1_t0b),
    .I1_t1b(n199_I1_t1b),
    .O_t0b(n199_O_t0b),
    .O_t1b_t0b(n199_O_t1b_t0b),
    .O_t1b_t1b(n199_O_t1b_t1b)
  );
  assign valid_down = n199_valid_down; // @[Top.scala 613:16]
  assign O_t0b = n199_O_t0b; // @[Top.scala 612:7]
  assign O_t1b_t0b = n199_O_t1b_t0b; // @[Top.scala 612:7]
  assign O_t1b_t1b = n199_O_t1b_t1b; // @[Top.scala 612:7]
  assign n165_valid_up = valid_up; // @[Top.scala 507:19]
  assign n165_I_t0b = I_t0b; // @[Top.scala 506:12]
  assign n198_clock = clock;
  assign n198_reset = reset;
  assign n198_valid_up = n165_valid_down; // @[Top.scala 510:19]
  assign n198_I = n165_O; // @[Top.scala 509:12]
  assign n186_clock = clock;
  assign n186_reset = reset;
  assign n186_valid_up = n165_valid_down; // @[Top.scala 513:19]
  assign n186_I = n165_O; // @[Top.scala 512:12]
  assign n166_valid_up = valid_up; // @[Top.scala 516:19]
  assign n166_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 515:12]
  assign n166_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 515:12]
  assign n167_valid_up = n166_valid_down; // @[Top.scala 519:19]
  assign n167_I_t0b = n166_O_t0b; // @[Top.scala 518:12]
  assign n168_valid_up = n166_valid_down; // @[Top.scala 522:19]
  assign n168_I_t1b = n166_O_t1b; // @[Top.scala 521:12]
  assign n169_valid_up = n167_valid_down & n168_valid_down; // @[Top.scala 526:19]
  assign n169_I0 = n167_O; // @[Top.scala 524:13]
  assign n169_I1 = n168_O; // @[Top.scala 525:13]
  assign n170_valid_up = n169_valid_down; // @[Top.scala 529:19]
  assign n170_I_t0b = n169_O_t0b; // @[Top.scala 528:12]
  assign n170_I_t1b = n169_O_t1b; // @[Top.scala 528:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n173_valid_up = n170_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 534:19]
  assign n173_I0 = n170_O; // @[Top.scala 532:13]
  assign n174_valid_up = n173_valid_down; // @[Top.scala 537:19]
  assign n174_I_t0b = n173_O_t0b; // @[Top.scala 536:12]
  assign n175_valid_up = n174_valid_down & n167_valid_down; // @[Top.scala 541:19]
  assign n175_I0 = n174_O; // @[Top.scala 539:13]
  assign n175_I1 = n167_O; // @[Top.scala 540:13]
  assign n176_valid_up = n175_valid_down; // @[Top.scala 544:19]
  assign n176_I_t0b = n175_O_t0b; // @[Top.scala 543:12]
  assign n176_I_t1b = n175_O_t1b; // @[Top.scala 543:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n179_valid_up = n174_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 549:19]
  assign n179_I0 = n174_O; // @[Top.scala 547:13]
  assign n179_I1 = 16'h1; // @[Top.scala 548:13]
  assign n180_valid_up = n179_valid_down; // @[Top.scala 552:19]
  assign n180_I_t0b = n179_O_t0b; // @[Top.scala 551:12]
  assign n180_I_t1b = n179_O_t1b; // @[Top.scala 551:12]
  assign n181_valid_up = n180_valid_down & n174_valid_down; // @[Top.scala 556:19]
  assign n181_I0 = n180_O; // @[Top.scala 554:13]
  assign n181_I1 = n174_O; // @[Top.scala 555:13]
  assign n182_valid_up = n176_valid_down & n181_valid_down; // @[Top.scala 560:19]
  assign n182_I0 = n176_O[0]; // @[Top.scala 558:13]
  assign n182_I1_t0b = n181_O_t0b; // @[Top.scala 559:13]
  assign n182_I1_t1b = n181_O_t1b; // @[Top.scala 559:13]
  assign n183_valid_up = n182_valid_down; // @[Top.scala 563:19]
  assign n183_I_t0b = n182_O_t0b; // @[Top.scala 562:12]
  assign n183_I_t1b_t0b = n182_O_t1b_t0b; // @[Top.scala 562:12]
  assign n183_I_t1b_t1b = n182_O_t1b_t1b; // @[Top.scala 562:12]
  assign n184_valid_up = n183_valid_down; // @[Top.scala 567:19]
  assign n184_I0 = n183_O; // @[Top.scala 565:13]
  assign n184_I1 = n183_O; // @[Top.scala 566:13]
  assign n185_clock = clock;
  assign n185_reset = reset;
  assign n185_valid_up = n184_valid_down; // @[Top.scala 570:19]
  assign n185_I_t0b = n184_O_t0b; // @[Top.scala 569:12]
  assign n185_I_t1b = n184_O_t1b; // @[Top.scala 569:12]
  assign n187_valid_up = n186_valid_down & n185_valid_down; // @[Top.scala 574:19]
  assign n187_I0 = n186_O; // @[Top.scala 572:13]
  assign n187_I1 = n185_O; // @[Top.scala 573:13]
  assign n188_valid_up = n187_valid_down; // @[Top.scala 577:19]
  assign n188_I_t0b = n187_O_t0b; // @[Top.scala 576:12]
  assign n188_I_t1b = n187_O_t1b; // @[Top.scala 576:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n190_valid_up = n183_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 582:19]
  assign n190_I0 = n183_O; // @[Top.scala 580:13]
  assign n190_I1 = 16'h1; // @[Top.scala 581:13]
  assign n191_valid_up = n190_valid_down; // @[Top.scala 585:19]
  assign n191_I_t0b = n190_O_t0b; // @[Top.scala 584:12]
  assign n191_I_t1b = n190_O_t1b; // @[Top.scala 584:12]
  assign n192_valid_up = n167_valid_down & n191_valid_down; // @[Top.scala 589:19]
  assign n192_I0 = n167_O; // @[Top.scala 587:13]
  assign n192_I1 = n191_O; // @[Top.scala 588:13]
  assign n193_valid_up = n183_valid_down & n168_valid_down; // @[Top.scala 593:19]
  assign n193_I0 = n183_O; // @[Top.scala 591:13]
  assign n193_I1 = n168_O; // @[Top.scala 592:13]
  assign n194_valid_up = n192_valid_down & n193_valid_down; // @[Top.scala 597:19]
  assign n194_I0_t0b = n192_O_t0b; // @[Top.scala 595:13]
  assign n194_I0_t1b = n192_O_t1b; // @[Top.scala 595:13]
  assign n194_I1_t0b = n193_O_t0b; // @[Top.scala 596:13]
  assign n194_I1_t1b = n193_O_t1b; // @[Top.scala 596:13]
  assign n195_clock = clock;
  assign n195_reset = reset;
  assign n195_valid_up = n194_valid_down; // @[Top.scala 600:19]
  assign n195_I_t0b_t0b = n194_O_t0b_t0b; // @[Top.scala 599:12]
  assign n195_I_t0b_t1b = n194_O_t0b_t1b; // @[Top.scala 599:12]
  assign n195_I_t1b_t0b = n194_O_t1b_t0b; // @[Top.scala 599:12]
  assign n195_I_t1b_t1b = n194_O_t1b_t1b; // @[Top.scala 599:12]
  assign n196_valid_up = n188_valid_down & n195_valid_down; // @[Top.scala 604:19]
  assign n196_I0 = n188_O[0]; // @[Top.scala 602:13]
  assign n196_I1_t0b_t0b = n195_O_t0b_t0b; // @[Top.scala 603:13]
  assign n196_I1_t0b_t1b = n195_O_t0b_t1b; // @[Top.scala 603:13]
  assign n196_I1_t1b_t0b = n195_O_t1b_t0b; // @[Top.scala 603:13]
  assign n196_I1_t1b_t1b = n195_O_t1b_t1b; // @[Top.scala 603:13]
  assign n197_valid_up = n196_valid_down; // @[Top.scala 607:19]
  assign n197_I_t0b = n196_O_t0b; // @[Top.scala 606:12]
  assign n197_I_t1b_t0b_t0b = n196_O_t1b_t0b_t0b; // @[Top.scala 606:12]
  assign n197_I_t1b_t0b_t1b = n196_O_t1b_t0b_t1b; // @[Top.scala 606:12]
  assign n197_I_t1b_t1b_t0b = n196_O_t1b_t1b_t0b; // @[Top.scala 606:12]
  assign n197_I_t1b_t1b_t1b = n196_O_t1b_t1b_t1b; // @[Top.scala 606:12]
  assign n199_valid_up = n198_valid_down & n197_valid_down; // @[Top.scala 611:19]
  assign n199_I0 = n198_O; // @[Top.scala 609:13]
  assign n199_I1_t0b = n197_O_t0b; // @[Top.scala 610:13]
  assign n199_I1_t1b = n197_O_t1b; // @[Top.scala 610:13]
endmodule
module MapT_5(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_5 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
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
  wire  n202_valid_up; // @[Top.scala 619:22]
  wire  n202_valid_down; // @[Top.scala 619:22]
  wire [15:0] n202_I_t0b; // @[Top.scala 619:22]
  wire [15:0] n202_O; // @[Top.scala 619:22]
  wire  n235_clock; // @[Top.scala 622:22]
  wire  n235_reset; // @[Top.scala 622:22]
  wire  n235_valid_up; // @[Top.scala 622:22]
  wire  n235_valid_down; // @[Top.scala 622:22]
  wire [15:0] n235_I; // @[Top.scala 622:22]
  wire [15:0] n235_O; // @[Top.scala 622:22]
  wire  n223_clock; // @[Top.scala 625:22]
  wire  n223_reset; // @[Top.scala 625:22]
  wire  n223_valid_up; // @[Top.scala 625:22]
  wire  n223_valid_down; // @[Top.scala 625:22]
  wire [15:0] n223_I; // @[Top.scala 625:22]
  wire [15:0] n223_O; // @[Top.scala 625:22]
  wire  n203_valid_up; // @[Top.scala 628:22]
  wire  n203_valid_down; // @[Top.scala 628:22]
  wire [15:0] n203_I_t1b_t0b; // @[Top.scala 628:22]
  wire [15:0] n203_I_t1b_t1b; // @[Top.scala 628:22]
  wire [15:0] n203_O_t0b; // @[Top.scala 628:22]
  wire [15:0] n203_O_t1b; // @[Top.scala 628:22]
  wire  n204_valid_up; // @[Top.scala 631:22]
  wire  n204_valid_down; // @[Top.scala 631:22]
  wire [15:0] n204_I_t0b; // @[Top.scala 631:22]
  wire [15:0] n204_O; // @[Top.scala 631:22]
  wire  n205_valid_up; // @[Top.scala 634:22]
  wire  n205_valid_down; // @[Top.scala 634:22]
  wire [15:0] n205_I_t1b; // @[Top.scala 634:22]
  wire [15:0] n205_O; // @[Top.scala 634:22]
  wire  n206_valid_up; // @[Top.scala 637:22]
  wire  n206_valid_down; // @[Top.scala 637:22]
  wire [15:0] n206_I0; // @[Top.scala 637:22]
  wire [15:0] n206_I1; // @[Top.scala 637:22]
  wire [15:0] n206_O_t0b; // @[Top.scala 637:22]
  wire [15:0] n206_O_t1b; // @[Top.scala 637:22]
  wire  n207_valid_up; // @[Top.scala 641:22]
  wire  n207_valid_down; // @[Top.scala 641:22]
  wire [15:0] n207_I_t0b; // @[Top.scala 641:22]
  wire [15:0] n207_I_t1b; // @[Top.scala 641:22]
  wire [15:0] n207_O; // @[Top.scala 641:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n210_valid_up; // @[Top.scala 645:22]
  wire  n210_valid_down; // @[Top.scala 645:22]
  wire [15:0] n210_I0; // @[Top.scala 645:22]
  wire [15:0] n210_O_t0b; // @[Top.scala 645:22]
  wire  n211_valid_up; // @[Top.scala 649:22]
  wire  n211_valid_down; // @[Top.scala 649:22]
  wire [15:0] n211_I_t0b; // @[Top.scala 649:22]
  wire [15:0] n211_O; // @[Top.scala 649:22]
  wire  n212_valid_up; // @[Top.scala 652:22]
  wire  n212_valid_down; // @[Top.scala 652:22]
  wire [15:0] n212_I0; // @[Top.scala 652:22]
  wire [15:0] n212_I1; // @[Top.scala 652:22]
  wire [15:0] n212_O_t0b; // @[Top.scala 652:22]
  wire [15:0] n212_O_t1b; // @[Top.scala 652:22]
  wire  n213_valid_up; // @[Top.scala 656:22]
  wire  n213_valid_down; // @[Top.scala 656:22]
  wire [15:0] n213_I_t0b; // @[Top.scala 656:22]
  wire [15:0] n213_I_t1b; // @[Top.scala 656:22]
  wire [15:0] n213_O; // @[Top.scala 656:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n216_valid_up; // @[Top.scala 660:22]
  wire  n216_valid_down; // @[Top.scala 660:22]
  wire [15:0] n216_I0; // @[Top.scala 660:22]
  wire [15:0] n216_I1; // @[Top.scala 660:22]
  wire [15:0] n216_O_t0b; // @[Top.scala 660:22]
  wire [15:0] n216_O_t1b; // @[Top.scala 660:22]
  wire  n217_valid_up; // @[Top.scala 664:22]
  wire  n217_valid_down; // @[Top.scala 664:22]
  wire [15:0] n217_I_t0b; // @[Top.scala 664:22]
  wire [15:0] n217_I_t1b; // @[Top.scala 664:22]
  wire [15:0] n217_O; // @[Top.scala 664:22]
  wire  n218_valid_up; // @[Top.scala 667:22]
  wire  n218_valid_down; // @[Top.scala 667:22]
  wire [15:0] n218_I0; // @[Top.scala 667:22]
  wire [15:0] n218_I1; // @[Top.scala 667:22]
  wire [15:0] n218_O_t0b; // @[Top.scala 667:22]
  wire [15:0] n218_O_t1b; // @[Top.scala 667:22]
  wire  n219_valid_up; // @[Top.scala 671:22]
  wire  n219_valid_down; // @[Top.scala 671:22]
  wire  n219_I0; // @[Top.scala 671:22]
  wire [15:0] n219_I1_t0b; // @[Top.scala 671:22]
  wire [15:0] n219_I1_t1b; // @[Top.scala 671:22]
  wire  n219_O_t0b; // @[Top.scala 671:22]
  wire [15:0] n219_O_t1b_t0b; // @[Top.scala 671:22]
  wire [15:0] n219_O_t1b_t1b; // @[Top.scala 671:22]
  wire  n220_valid_up; // @[Top.scala 675:22]
  wire  n220_valid_down; // @[Top.scala 675:22]
  wire  n220_I_t0b; // @[Top.scala 675:22]
  wire [15:0] n220_I_t1b_t0b; // @[Top.scala 675:22]
  wire [15:0] n220_I_t1b_t1b; // @[Top.scala 675:22]
  wire [15:0] n220_O; // @[Top.scala 675:22]
  wire  n221_valid_up; // @[Top.scala 678:22]
  wire  n221_valid_down; // @[Top.scala 678:22]
  wire [15:0] n221_I0; // @[Top.scala 678:22]
  wire [15:0] n221_I1; // @[Top.scala 678:22]
  wire [15:0] n221_O_t0b; // @[Top.scala 678:22]
  wire [15:0] n221_O_t1b; // @[Top.scala 678:22]
  wire  n222_clock; // @[Top.scala 682:22]
  wire  n222_reset; // @[Top.scala 682:22]
  wire  n222_valid_up; // @[Top.scala 682:22]
  wire  n222_valid_down; // @[Top.scala 682:22]
  wire [15:0] n222_I_t0b; // @[Top.scala 682:22]
  wire [15:0] n222_I_t1b; // @[Top.scala 682:22]
  wire [15:0] n222_O; // @[Top.scala 682:22]
  wire  n224_valid_up; // @[Top.scala 685:22]
  wire  n224_valid_down; // @[Top.scala 685:22]
  wire [15:0] n224_I0; // @[Top.scala 685:22]
  wire [15:0] n224_I1; // @[Top.scala 685:22]
  wire [15:0] n224_O_t0b; // @[Top.scala 685:22]
  wire [15:0] n224_O_t1b; // @[Top.scala 685:22]
  wire  n225_valid_up; // @[Top.scala 689:22]
  wire  n225_valid_down; // @[Top.scala 689:22]
  wire [15:0] n225_I_t0b; // @[Top.scala 689:22]
  wire [15:0] n225_I_t1b; // @[Top.scala 689:22]
  wire [15:0] n225_O; // @[Top.scala 689:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n227_valid_up; // @[Top.scala 693:22]
  wire  n227_valid_down; // @[Top.scala 693:22]
  wire [15:0] n227_I0; // @[Top.scala 693:22]
  wire [15:0] n227_I1; // @[Top.scala 693:22]
  wire [15:0] n227_O_t0b; // @[Top.scala 693:22]
  wire [15:0] n227_O_t1b; // @[Top.scala 693:22]
  wire  n228_valid_up; // @[Top.scala 697:22]
  wire  n228_valid_down; // @[Top.scala 697:22]
  wire [15:0] n228_I_t0b; // @[Top.scala 697:22]
  wire [15:0] n228_I_t1b; // @[Top.scala 697:22]
  wire [15:0] n228_O; // @[Top.scala 697:22]
  wire  n229_valid_up; // @[Top.scala 700:22]
  wire  n229_valid_down; // @[Top.scala 700:22]
  wire [15:0] n229_I0; // @[Top.scala 700:22]
  wire [15:0] n229_I1; // @[Top.scala 700:22]
  wire [15:0] n229_O_t0b; // @[Top.scala 700:22]
  wire [15:0] n229_O_t1b; // @[Top.scala 700:22]
  wire  n230_valid_up; // @[Top.scala 704:22]
  wire  n230_valid_down; // @[Top.scala 704:22]
  wire [15:0] n230_I0; // @[Top.scala 704:22]
  wire [15:0] n230_I1; // @[Top.scala 704:22]
  wire [15:0] n230_O_t0b; // @[Top.scala 704:22]
  wire [15:0] n230_O_t1b; // @[Top.scala 704:22]
  wire  n231_valid_up; // @[Top.scala 708:22]
  wire  n231_valid_down; // @[Top.scala 708:22]
  wire [15:0] n231_I0_t0b; // @[Top.scala 708:22]
  wire [15:0] n231_I0_t1b; // @[Top.scala 708:22]
  wire [15:0] n231_I1_t0b; // @[Top.scala 708:22]
  wire [15:0] n231_I1_t1b; // @[Top.scala 708:22]
  wire [15:0] n231_O_t0b_t0b; // @[Top.scala 708:22]
  wire [15:0] n231_O_t0b_t1b; // @[Top.scala 708:22]
  wire [15:0] n231_O_t1b_t0b; // @[Top.scala 708:22]
  wire [15:0] n231_O_t1b_t1b; // @[Top.scala 708:22]
  wire  n232_clock; // @[Top.scala 712:22]
  wire  n232_reset; // @[Top.scala 712:22]
  wire  n232_valid_up; // @[Top.scala 712:22]
  wire  n232_valid_down; // @[Top.scala 712:22]
  wire [15:0] n232_I_t0b_t0b; // @[Top.scala 712:22]
  wire [15:0] n232_I_t0b_t1b; // @[Top.scala 712:22]
  wire [15:0] n232_I_t1b_t0b; // @[Top.scala 712:22]
  wire [15:0] n232_I_t1b_t1b; // @[Top.scala 712:22]
  wire [15:0] n232_O_t0b_t0b; // @[Top.scala 712:22]
  wire [15:0] n232_O_t0b_t1b; // @[Top.scala 712:22]
  wire [15:0] n232_O_t1b_t0b; // @[Top.scala 712:22]
  wire [15:0] n232_O_t1b_t1b; // @[Top.scala 712:22]
  wire  n233_valid_up; // @[Top.scala 715:22]
  wire  n233_valid_down; // @[Top.scala 715:22]
  wire  n233_I0; // @[Top.scala 715:22]
  wire [15:0] n233_I1_t0b_t0b; // @[Top.scala 715:22]
  wire [15:0] n233_I1_t0b_t1b; // @[Top.scala 715:22]
  wire [15:0] n233_I1_t1b_t0b; // @[Top.scala 715:22]
  wire [15:0] n233_I1_t1b_t1b; // @[Top.scala 715:22]
  wire  n233_O_t0b; // @[Top.scala 715:22]
  wire [15:0] n233_O_t1b_t0b_t0b; // @[Top.scala 715:22]
  wire [15:0] n233_O_t1b_t0b_t1b; // @[Top.scala 715:22]
  wire [15:0] n233_O_t1b_t1b_t0b; // @[Top.scala 715:22]
  wire [15:0] n233_O_t1b_t1b_t1b; // @[Top.scala 715:22]
  wire  n234_valid_up; // @[Top.scala 719:22]
  wire  n234_valid_down; // @[Top.scala 719:22]
  wire  n234_I_t0b; // @[Top.scala 719:22]
  wire [15:0] n234_I_t1b_t0b_t0b; // @[Top.scala 719:22]
  wire [15:0] n234_I_t1b_t0b_t1b; // @[Top.scala 719:22]
  wire [15:0] n234_I_t1b_t1b_t0b; // @[Top.scala 719:22]
  wire [15:0] n234_I_t1b_t1b_t1b; // @[Top.scala 719:22]
  wire [15:0] n234_O_t0b; // @[Top.scala 719:22]
  wire [15:0] n234_O_t1b; // @[Top.scala 719:22]
  wire  n236_valid_up; // @[Top.scala 722:22]
  wire  n236_valid_down; // @[Top.scala 722:22]
  wire [15:0] n236_I0; // @[Top.scala 722:22]
  wire [15:0] n236_I1_t0b; // @[Top.scala 722:22]
  wire [15:0] n236_I1_t1b; // @[Top.scala 722:22]
  wire [15:0] n236_O_t0b; // @[Top.scala 722:22]
  wire [15:0] n236_O_t1b_t0b; // @[Top.scala 722:22]
  wire [15:0] n236_O_t1b_t1b; // @[Top.scala 722:22]
  Fst n202 ( // @[Top.scala 619:22]
    .valid_up(n202_valid_up),
    .valid_down(n202_valid_down),
    .I_t0b(n202_I_t0b),
    .O(n202_O)
  );
  FIFO_1 n235 ( // @[Top.scala 622:22]
    .clock(n235_clock),
    .reset(n235_reset),
    .valid_up(n235_valid_up),
    .valid_down(n235_valid_down),
    .I(n235_I),
    .O(n235_O)
  );
  FIFO_1 n223 ( // @[Top.scala 625:22]
    .clock(n223_clock),
    .reset(n223_reset),
    .valid_up(n223_valid_up),
    .valid_down(n223_valid_down),
    .I(n223_I),
    .O(n223_O)
  );
  Snd n203 ( // @[Top.scala 628:22]
    .valid_up(n203_valid_up),
    .valid_down(n203_valid_down),
    .I_t1b_t0b(n203_I_t1b_t0b),
    .I_t1b_t1b(n203_I_t1b_t1b),
    .O_t0b(n203_O_t0b),
    .O_t1b(n203_O_t1b)
  );
  Fst_1 n204 ( // @[Top.scala 631:22]
    .valid_up(n204_valid_up),
    .valid_down(n204_valid_down),
    .I_t0b(n204_I_t0b),
    .O(n204_O)
  );
  Snd_1 n205 ( // @[Top.scala 634:22]
    .valid_up(n205_valid_up),
    .valid_down(n205_valid_down),
    .I_t1b(n205_I_t1b),
    .O(n205_O)
  );
  AtomTuple_1 n206 ( // @[Top.scala 637:22]
    .valid_up(n206_valid_up),
    .valid_down(n206_valid_down),
    .I0(n206_I0),
    .I1(n206_I1),
    .O_t0b(n206_O_t0b),
    .O_t1b(n206_O_t1b)
  );
  Add n207 ( // @[Top.scala 641:22]
    .valid_up(n207_valid_up),
    .valid_down(n207_valid_down),
    .I_t0b(n207_I_t0b),
    .I_t1b(n207_I_t1b),
    .O(n207_O)
  );
  InitialDelayCounter_18 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n210 ( // @[Top.scala 645:22]
    .valid_up(n210_valid_up),
    .valid_down(n210_valid_down),
    .I0(n210_I0),
    .O_t0b(n210_O_t0b)
  );
  RShift n211 ( // @[Top.scala 649:22]
    .valid_up(n211_valid_up),
    .valid_down(n211_valid_down),
    .I_t0b(n211_I_t0b),
    .O(n211_O)
  );
  AtomTuple_1 n212 ( // @[Top.scala 652:22]
    .valid_up(n212_valid_up),
    .valid_down(n212_valid_down),
    .I0(n212_I0),
    .I1(n212_I1),
    .O_t0b(n212_O_t0b),
    .O_t1b(n212_O_t1b)
  );
  Eq n213 ( // @[Top.scala 656:22]
    .valid_up(n213_valid_up),
    .valid_down(n213_valid_down),
    .I_t0b(n213_I_t0b),
    .I_t1b(n213_I_t1b),
    .O(n213_O)
  );
  InitialDelayCounter_18 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n216 ( // @[Top.scala 660:22]
    .valid_up(n216_valid_up),
    .valid_down(n216_valid_down),
    .I0(n216_I0),
    .I1(n216_I1),
    .O_t0b(n216_O_t0b),
    .O_t1b(n216_O_t1b)
  );
  Add n217 ( // @[Top.scala 664:22]
    .valid_up(n217_valid_up),
    .valid_down(n217_valid_down),
    .I_t0b(n217_I_t0b),
    .I_t1b(n217_I_t1b),
    .O(n217_O)
  );
  AtomTuple_1 n218 ( // @[Top.scala 667:22]
    .valid_up(n218_valid_up),
    .valid_down(n218_valid_down),
    .I0(n218_I0),
    .I1(n218_I1),
    .O_t0b(n218_O_t0b),
    .O_t1b(n218_O_t1b)
  );
  AtomTuple_9 n219 ( // @[Top.scala 671:22]
    .valid_up(n219_valid_up),
    .valid_down(n219_valid_down),
    .I0(n219_I0),
    .I1_t0b(n219_I1_t0b),
    .I1_t1b(n219_I1_t1b),
    .O_t0b(n219_O_t0b),
    .O_t1b_t0b(n219_O_t1b_t0b),
    .O_t1b_t1b(n219_O_t1b_t1b)
  );
  If n220 ( // @[Top.scala 675:22]
    .valid_up(n220_valid_up),
    .valid_down(n220_valid_down),
    .I_t0b(n220_I_t0b),
    .I_t1b_t0b(n220_I_t1b_t0b),
    .I_t1b_t1b(n220_I_t1b_t1b),
    .O(n220_O)
  );
  AtomTuple_1 n221 ( // @[Top.scala 678:22]
    .valid_up(n221_valid_up),
    .valid_down(n221_valid_down),
    .I0(n221_I0),
    .I1(n221_I1),
    .O_t0b(n221_O_t0b),
    .O_t1b(n221_O_t1b)
  );
  Mul n222 ( // @[Top.scala 682:22]
    .clock(n222_clock),
    .reset(n222_reset),
    .valid_up(n222_valid_up),
    .valid_down(n222_valid_down),
    .I_t0b(n222_I_t0b),
    .I_t1b(n222_I_t1b),
    .O(n222_O)
  );
  AtomTuple_1 n224 ( // @[Top.scala 685:22]
    .valid_up(n224_valid_up),
    .valid_down(n224_valid_down),
    .I0(n224_I0),
    .I1(n224_I1),
    .O_t0b(n224_O_t0b),
    .O_t1b(n224_O_t1b)
  );
  Lt n225 ( // @[Top.scala 689:22]
    .valid_up(n225_valid_up),
    .valid_down(n225_valid_down),
    .I_t0b(n225_I_t0b),
    .I_t1b(n225_I_t1b),
    .O(n225_O)
  );
  InitialDelayCounter_18 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n227 ( // @[Top.scala 693:22]
    .valid_up(n227_valid_up),
    .valid_down(n227_valid_down),
    .I0(n227_I0),
    .I1(n227_I1),
    .O_t0b(n227_O_t0b),
    .O_t1b(n227_O_t1b)
  );
  Sub n228 ( // @[Top.scala 697:22]
    .valid_up(n228_valid_up),
    .valid_down(n228_valid_down),
    .I_t0b(n228_I_t0b),
    .I_t1b(n228_I_t1b),
    .O(n228_O)
  );
  AtomTuple_1 n229 ( // @[Top.scala 700:22]
    .valid_up(n229_valid_up),
    .valid_down(n229_valid_down),
    .I0(n229_I0),
    .I1(n229_I1),
    .O_t0b(n229_O_t0b),
    .O_t1b(n229_O_t1b)
  );
  AtomTuple_1 n230 ( // @[Top.scala 704:22]
    .valid_up(n230_valid_up),
    .valid_down(n230_valid_down),
    .I0(n230_I0),
    .I1(n230_I1),
    .O_t0b(n230_O_t0b),
    .O_t1b(n230_O_t1b)
  );
  AtomTuple_15 n231 ( // @[Top.scala 708:22]
    .valid_up(n231_valid_up),
    .valid_down(n231_valid_down),
    .I0_t0b(n231_I0_t0b),
    .I0_t1b(n231_I0_t1b),
    .I1_t0b(n231_I1_t0b),
    .I1_t1b(n231_I1_t1b),
    .O_t0b_t0b(n231_O_t0b_t0b),
    .O_t0b_t1b(n231_O_t0b_t1b),
    .O_t1b_t0b(n231_O_t1b_t0b),
    .O_t1b_t1b(n231_O_t1b_t1b)
  );
  FIFO_3 n232 ( // @[Top.scala 712:22]
    .clock(n232_clock),
    .reset(n232_reset),
    .valid_up(n232_valid_up),
    .valid_down(n232_valid_down),
    .I_t0b_t0b(n232_I_t0b_t0b),
    .I_t0b_t1b(n232_I_t0b_t1b),
    .I_t1b_t0b(n232_I_t1b_t0b),
    .I_t1b_t1b(n232_I_t1b_t1b),
    .O_t0b_t0b(n232_O_t0b_t0b),
    .O_t0b_t1b(n232_O_t0b_t1b),
    .O_t1b_t0b(n232_O_t1b_t0b),
    .O_t1b_t1b(n232_O_t1b_t1b)
  );
  AtomTuple_16 n233 ( // @[Top.scala 715:22]
    .valid_up(n233_valid_up),
    .valid_down(n233_valid_down),
    .I0(n233_I0),
    .I1_t0b_t0b(n233_I1_t0b_t0b),
    .I1_t0b_t1b(n233_I1_t0b_t1b),
    .I1_t1b_t0b(n233_I1_t1b_t0b),
    .I1_t1b_t1b(n233_I1_t1b_t1b),
    .O_t0b(n233_O_t0b),
    .O_t1b_t0b_t0b(n233_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n233_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n233_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n233_O_t1b_t1b_t1b)
  );
  If_1 n234 ( // @[Top.scala 719:22]
    .valid_up(n234_valid_up),
    .valid_down(n234_valid_down),
    .I_t0b(n234_I_t0b),
    .I_t1b_t0b_t0b(n234_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n234_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n234_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n234_I_t1b_t1b_t1b),
    .O_t0b(n234_O_t0b),
    .O_t1b(n234_O_t1b)
  );
  AtomTuple_3 n236 ( // @[Top.scala 722:22]
    .valid_up(n236_valid_up),
    .valid_down(n236_valid_down),
    .I0(n236_I0),
    .I1_t0b(n236_I1_t0b),
    .I1_t1b(n236_I1_t1b),
    .O_t0b(n236_O_t0b),
    .O_t1b_t0b(n236_O_t1b_t0b),
    .O_t1b_t1b(n236_O_t1b_t1b)
  );
  assign valid_down = n236_valid_down; // @[Top.scala 727:16]
  assign O_t0b = n236_O_t0b; // @[Top.scala 726:7]
  assign O_t1b_t0b = n236_O_t1b_t0b; // @[Top.scala 726:7]
  assign O_t1b_t1b = n236_O_t1b_t1b; // @[Top.scala 726:7]
  assign n202_valid_up = valid_up; // @[Top.scala 621:19]
  assign n202_I_t0b = I_t0b; // @[Top.scala 620:12]
  assign n235_clock = clock;
  assign n235_reset = reset;
  assign n235_valid_up = n202_valid_down; // @[Top.scala 624:19]
  assign n235_I = n202_O; // @[Top.scala 623:12]
  assign n223_clock = clock;
  assign n223_reset = reset;
  assign n223_valid_up = n202_valid_down; // @[Top.scala 627:19]
  assign n223_I = n202_O; // @[Top.scala 626:12]
  assign n203_valid_up = valid_up; // @[Top.scala 630:19]
  assign n203_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 629:12]
  assign n203_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 629:12]
  assign n204_valid_up = n203_valid_down; // @[Top.scala 633:19]
  assign n204_I_t0b = n203_O_t0b; // @[Top.scala 632:12]
  assign n205_valid_up = n203_valid_down; // @[Top.scala 636:19]
  assign n205_I_t1b = n203_O_t1b; // @[Top.scala 635:12]
  assign n206_valid_up = n204_valid_down & n205_valid_down; // @[Top.scala 640:19]
  assign n206_I0 = n204_O; // @[Top.scala 638:13]
  assign n206_I1 = n205_O; // @[Top.scala 639:13]
  assign n207_valid_up = n206_valid_down; // @[Top.scala 643:19]
  assign n207_I_t0b = n206_O_t0b; // @[Top.scala 642:12]
  assign n207_I_t1b = n206_O_t1b; // @[Top.scala 642:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n210_valid_up = n207_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 648:19]
  assign n210_I0 = n207_O; // @[Top.scala 646:13]
  assign n211_valid_up = n210_valid_down; // @[Top.scala 651:19]
  assign n211_I_t0b = n210_O_t0b; // @[Top.scala 650:12]
  assign n212_valid_up = n211_valid_down & n204_valid_down; // @[Top.scala 655:19]
  assign n212_I0 = n211_O; // @[Top.scala 653:13]
  assign n212_I1 = n204_O; // @[Top.scala 654:13]
  assign n213_valid_up = n212_valid_down; // @[Top.scala 658:19]
  assign n213_I_t0b = n212_O_t0b; // @[Top.scala 657:12]
  assign n213_I_t1b = n212_O_t1b; // @[Top.scala 657:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n216_valid_up = n211_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 663:19]
  assign n216_I0 = n211_O; // @[Top.scala 661:13]
  assign n216_I1 = 16'h1; // @[Top.scala 662:13]
  assign n217_valid_up = n216_valid_down; // @[Top.scala 666:19]
  assign n217_I_t0b = n216_O_t0b; // @[Top.scala 665:12]
  assign n217_I_t1b = n216_O_t1b; // @[Top.scala 665:12]
  assign n218_valid_up = n217_valid_down & n211_valid_down; // @[Top.scala 670:19]
  assign n218_I0 = n217_O; // @[Top.scala 668:13]
  assign n218_I1 = n211_O; // @[Top.scala 669:13]
  assign n219_valid_up = n213_valid_down & n218_valid_down; // @[Top.scala 674:19]
  assign n219_I0 = n213_O[0]; // @[Top.scala 672:13]
  assign n219_I1_t0b = n218_O_t0b; // @[Top.scala 673:13]
  assign n219_I1_t1b = n218_O_t1b; // @[Top.scala 673:13]
  assign n220_valid_up = n219_valid_down; // @[Top.scala 677:19]
  assign n220_I_t0b = n219_O_t0b; // @[Top.scala 676:12]
  assign n220_I_t1b_t0b = n219_O_t1b_t0b; // @[Top.scala 676:12]
  assign n220_I_t1b_t1b = n219_O_t1b_t1b; // @[Top.scala 676:12]
  assign n221_valid_up = n220_valid_down; // @[Top.scala 681:19]
  assign n221_I0 = n220_O; // @[Top.scala 679:13]
  assign n221_I1 = n220_O; // @[Top.scala 680:13]
  assign n222_clock = clock;
  assign n222_reset = reset;
  assign n222_valid_up = n221_valid_down; // @[Top.scala 684:19]
  assign n222_I_t0b = n221_O_t0b; // @[Top.scala 683:12]
  assign n222_I_t1b = n221_O_t1b; // @[Top.scala 683:12]
  assign n224_valid_up = n223_valid_down & n222_valid_down; // @[Top.scala 688:19]
  assign n224_I0 = n223_O; // @[Top.scala 686:13]
  assign n224_I1 = n222_O; // @[Top.scala 687:13]
  assign n225_valid_up = n224_valid_down; // @[Top.scala 691:19]
  assign n225_I_t0b = n224_O_t0b; // @[Top.scala 690:12]
  assign n225_I_t1b = n224_O_t1b; // @[Top.scala 690:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n227_valid_up = n220_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 696:19]
  assign n227_I0 = n220_O; // @[Top.scala 694:13]
  assign n227_I1 = 16'h1; // @[Top.scala 695:13]
  assign n228_valid_up = n227_valid_down; // @[Top.scala 699:19]
  assign n228_I_t0b = n227_O_t0b; // @[Top.scala 698:12]
  assign n228_I_t1b = n227_O_t1b; // @[Top.scala 698:12]
  assign n229_valid_up = n204_valid_down & n228_valid_down; // @[Top.scala 703:19]
  assign n229_I0 = n204_O; // @[Top.scala 701:13]
  assign n229_I1 = n228_O; // @[Top.scala 702:13]
  assign n230_valid_up = n220_valid_down & n205_valid_down; // @[Top.scala 707:19]
  assign n230_I0 = n220_O; // @[Top.scala 705:13]
  assign n230_I1 = n205_O; // @[Top.scala 706:13]
  assign n231_valid_up = n229_valid_down & n230_valid_down; // @[Top.scala 711:19]
  assign n231_I0_t0b = n229_O_t0b; // @[Top.scala 709:13]
  assign n231_I0_t1b = n229_O_t1b; // @[Top.scala 709:13]
  assign n231_I1_t0b = n230_O_t0b; // @[Top.scala 710:13]
  assign n231_I1_t1b = n230_O_t1b; // @[Top.scala 710:13]
  assign n232_clock = clock;
  assign n232_reset = reset;
  assign n232_valid_up = n231_valid_down; // @[Top.scala 714:19]
  assign n232_I_t0b_t0b = n231_O_t0b_t0b; // @[Top.scala 713:12]
  assign n232_I_t0b_t1b = n231_O_t0b_t1b; // @[Top.scala 713:12]
  assign n232_I_t1b_t0b = n231_O_t1b_t0b; // @[Top.scala 713:12]
  assign n232_I_t1b_t1b = n231_O_t1b_t1b; // @[Top.scala 713:12]
  assign n233_valid_up = n225_valid_down & n232_valid_down; // @[Top.scala 718:19]
  assign n233_I0 = n225_O[0]; // @[Top.scala 716:13]
  assign n233_I1_t0b_t0b = n232_O_t0b_t0b; // @[Top.scala 717:13]
  assign n233_I1_t0b_t1b = n232_O_t0b_t1b; // @[Top.scala 717:13]
  assign n233_I1_t1b_t0b = n232_O_t1b_t0b; // @[Top.scala 717:13]
  assign n233_I1_t1b_t1b = n232_O_t1b_t1b; // @[Top.scala 717:13]
  assign n234_valid_up = n233_valid_down; // @[Top.scala 721:19]
  assign n234_I_t0b = n233_O_t0b; // @[Top.scala 720:12]
  assign n234_I_t1b_t0b_t0b = n233_O_t1b_t0b_t0b; // @[Top.scala 720:12]
  assign n234_I_t1b_t0b_t1b = n233_O_t1b_t0b_t1b; // @[Top.scala 720:12]
  assign n234_I_t1b_t1b_t0b = n233_O_t1b_t1b_t0b; // @[Top.scala 720:12]
  assign n234_I_t1b_t1b_t1b = n233_O_t1b_t1b_t1b; // @[Top.scala 720:12]
  assign n236_valid_up = n235_valid_down & n234_valid_down; // @[Top.scala 725:19]
  assign n236_I0 = n235_O; // @[Top.scala 723:13]
  assign n236_I1_t0b = n234_O_t0b; // @[Top.scala 724:13]
  assign n236_I1_t1b = n234_O_t1b; // @[Top.scala 724:13]
endmodule
module MapT_6(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_6 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
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
  wire  n239_valid_up; // @[Top.scala 733:22]
  wire  n239_valid_down; // @[Top.scala 733:22]
  wire [15:0] n239_I_t0b; // @[Top.scala 733:22]
  wire [15:0] n239_O; // @[Top.scala 733:22]
  wire  n272_clock; // @[Top.scala 736:22]
  wire  n272_reset; // @[Top.scala 736:22]
  wire  n272_valid_up; // @[Top.scala 736:22]
  wire  n272_valid_down; // @[Top.scala 736:22]
  wire [15:0] n272_I; // @[Top.scala 736:22]
  wire [15:0] n272_O; // @[Top.scala 736:22]
  wire  n260_clock; // @[Top.scala 739:22]
  wire  n260_reset; // @[Top.scala 739:22]
  wire  n260_valid_up; // @[Top.scala 739:22]
  wire  n260_valid_down; // @[Top.scala 739:22]
  wire [15:0] n260_I; // @[Top.scala 739:22]
  wire [15:0] n260_O; // @[Top.scala 739:22]
  wire  n240_valid_up; // @[Top.scala 742:22]
  wire  n240_valid_down; // @[Top.scala 742:22]
  wire [15:0] n240_I_t1b_t0b; // @[Top.scala 742:22]
  wire [15:0] n240_I_t1b_t1b; // @[Top.scala 742:22]
  wire [15:0] n240_O_t0b; // @[Top.scala 742:22]
  wire [15:0] n240_O_t1b; // @[Top.scala 742:22]
  wire  n241_valid_up; // @[Top.scala 745:22]
  wire  n241_valid_down; // @[Top.scala 745:22]
  wire [15:0] n241_I_t0b; // @[Top.scala 745:22]
  wire [15:0] n241_O; // @[Top.scala 745:22]
  wire  n242_valid_up; // @[Top.scala 748:22]
  wire  n242_valid_down; // @[Top.scala 748:22]
  wire [15:0] n242_I_t1b; // @[Top.scala 748:22]
  wire [15:0] n242_O; // @[Top.scala 748:22]
  wire  n243_valid_up; // @[Top.scala 751:22]
  wire  n243_valid_down; // @[Top.scala 751:22]
  wire [15:0] n243_I0; // @[Top.scala 751:22]
  wire [15:0] n243_I1; // @[Top.scala 751:22]
  wire [15:0] n243_O_t0b; // @[Top.scala 751:22]
  wire [15:0] n243_O_t1b; // @[Top.scala 751:22]
  wire  n244_valid_up; // @[Top.scala 755:22]
  wire  n244_valid_down; // @[Top.scala 755:22]
  wire [15:0] n244_I_t0b; // @[Top.scala 755:22]
  wire [15:0] n244_I_t1b; // @[Top.scala 755:22]
  wire [15:0] n244_O; // @[Top.scala 755:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n247_valid_up; // @[Top.scala 759:22]
  wire  n247_valid_down; // @[Top.scala 759:22]
  wire [15:0] n247_I0; // @[Top.scala 759:22]
  wire [15:0] n247_O_t0b; // @[Top.scala 759:22]
  wire  n248_valid_up; // @[Top.scala 763:22]
  wire  n248_valid_down; // @[Top.scala 763:22]
  wire [15:0] n248_I_t0b; // @[Top.scala 763:22]
  wire [15:0] n248_O; // @[Top.scala 763:22]
  wire  n249_valid_up; // @[Top.scala 766:22]
  wire  n249_valid_down; // @[Top.scala 766:22]
  wire [15:0] n249_I0; // @[Top.scala 766:22]
  wire [15:0] n249_I1; // @[Top.scala 766:22]
  wire [15:0] n249_O_t0b; // @[Top.scala 766:22]
  wire [15:0] n249_O_t1b; // @[Top.scala 766:22]
  wire  n250_valid_up; // @[Top.scala 770:22]
  wire  n250_valid_down; // @[Top.scala 770:22]
  wire [15:0] n250_I_t0b; // @[Top.scala 770:22]
  wire [15:0] n250_I_t1b; // @[Top.scala 770:22]
  wire [15:0] n250_O; // @[Top.scala 770:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n253_valid_up; // @[Top.scala 774:22]
  wire  n253_valid_down; // @[Top.scala 774:22]
  wire [15:0] n253_I0; // @[Top.scala 774:22]
  wire [15:0] n253_I1; // @[Top.scala 774:22]
  wire [15:0] n253_O_t0b; // @[Top.scala 774:22]
  wire [15:0] n253_O_t1b; // @[Top.scala 774:22]
  wire  n254_valid_up; // @[Top.scala 778:22]
  wire  n254_valid_down; // @[Top.scala 778:22]
  wire [15:0] n254_I_t0b; // @[Top.scala 778:22]
  wire [15:0] n254_I_t1b; // @[Top.scala 778:22]
  wire [15:0] n254_O; // @[Top.scala 778:22]
  wire  n255_valid_up; // @[Top.scala 781:22]
  wire  n255_valid_down; // @[Top.scala 781:22]
  wire [15:0] n255_I0; // @[Top.scala 781:22]
  wire [15:0] n255_I1; // @[Top.scala 781:22]
  wire [15:0] n255_O_t0b; // @[Top.scala 781:22]
  wire [15:0] n255_O_t1b; // @[Top.scala 781:22]
  wire  n256_valid_up; // @[Top.scala 785:22]
  wire  n256_valid_down; // @[Top.scala 785:22]
  wire  n256_I0; // @[Top.scala 785:22]
  wire [15:0] n256_I1_t0b; // @[Top.scala 785:22]
  wire [15:0] n256_I1_t1b; // @[Top.scala 785:22]
  wire  n256_O_t0b; // @[Top.scala 785:22]
  wire [15:0] n256_O_t1b_t0b; // @[Top.scala 785:22]
  wire [15:0] n256_O_t1b_t1b; // @[Top.scala 785:22]
  wire  n257_valid_up; // @[Top.scala 789:22]
  wire  n257_valid_down; // @[Top.scala 789:22]
  wire  n257_I_t0b; // @[Top.scala 789:22]
  wire [15:0] n257_I_t1b_t0b; // @[Top.scala 789:22]
  wire [15:0] n257_I_t1b_t1b; // @[Top.scala 789:22]
  wire [15:0] n257_O; // @[Top.scala 789:22]
  wire  n258_valid_up; // @[Top.scala 792:22]
  wire  n258_valid_down; // @[Top.scala 792:22]
  wire [15:0] n258_I0; // @[Top.scala 792:22]
  wire [15:0] n258_I1; // @[Top.scala 792:22]
  wire [15:0] n258_O_t0b; // @[Top.scala 792:22]
  wire [15:0] n258_O_t1b; // @[Top.scala 792:22]
  wire  n259_clock; // @[Top.scala 796:22]
  wire  n259_reset; // @[Top.scala 796:22]
  wire  n259_valid_up; // @[Top.scala 796:22]
  wire  n259_valid_down; // @[Top.scala 796:22]
  wire [15:0] n259_I_t0b; // @[Top.scala 796:22]
  wire [15:0] n259_I_t1b; // @[Top.scala 796:22]
  wire [15:0] n259_O; // @[Top.scala 796:22]
  wire  n261_valid_up; // @[Top.scala 799:22]
  wire  n261_valid_down; // @[Top.scala 799:22]
  wire [15:0] n261_I0; // @[Top.scala 799:22]
  wire [15:0] n261_I1; // @[Top.scala 799:22]
  wire [15:0] n261_O_t0b; // @[Top.scala 799:22]
  wire [15:0] n261_O_t1b; // @[Top.scala 799:22]
  wire  n262_valid_up; // @[Top.scala 803:22]
  wire  n262_valid_down; // @[Top.scala 803:22]
  wire [15:0] n262_I_t0b; // @[Top.scala 803:22]
  wire [15:0] n262_I_t1b; // @[Top.scala 803:22]
  wire [15:0] n262_O; // @[Top.scala 803:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n264_valid_up; // @[Top.scala 807:22]
  wire  n264_valid_down; // @[Top.scala 807:22]
  wire [15:0] n264_I0; // @[Top.scala 807:22]
  wire [15:0] n264_I1; // @[Top.scala 807:22]
  wire [15:0] n264_O_t0b; // @[Top.scala 807:22]
  wire [15:0] n264_O_t1b; // @[Top.scala 807:22]
  wire  n265_valid_up; // @[Top.scala 811:22]
  wire  n265_valid_down; // @[Top.scala 811:22]
  wire [15:0] n265_I_t0b; // @[Top.scala 811:22]
  wire [15:0] n265_I_t1b; // @[Top.scala 811:22]
  wire [15:0] n265_O; // @[Top.scala 811:22]
  wire  n266_valid_up; // @[Top.scala 814:22]
  wire  n266_valid_down; // @[Top.scala 814:22]
  wire [15:0] n266_I0; // @[Top.scala 814:22]
  wire [15:0] n266_I1; // @[Top.scala 814:22]
  wire [15:0] n266_O_t0b; // @[Top.scala 814:22]
  wire [15:0] n266_O_t1b; // @[Top.scala 814:22]
  wire  n267_valid_up; // @[Top.scala 818:22]
  wire  n267_valid_down; // @[Top.scala 818:22]
  wire [15:0] n267_I0; // @[Top.scala 818:22]
  wire [15:0] n267_I1; // @[Top.scala 818:22]
  wire [15:0] n267_O_t0b; // @[Top.scala 818:22]
  wire [15:0] n267_O_t1b; // @[Top.scala 818:22]
  wire  n268_valid_up; // @[Top.scala 822:22]
  wire  n268_valid_down; // @[Top.scala 822:22]
  wire [15:0] n268_I0_t0b; // @[Top.scala 822:22]
  wire [15:0] n268_I0_t1b; // @[Top.scala 822:22]
  wire [15:0] n268_I1_t0b; // @[Top.scala 822:22]
  wire [15:0] n268_I1_t1b; // @[Top.scala 822:22]
  wire [15:0] n268_O_t0b_t0b; // @[Top.scala 822:22]
  wire [15:0] n268_O_t0b_t1b; // @[Top.scala 822:22]
  wire [15:0] n268_O_t1b_t0b; // @[Top.scala 822:22]
  wire [15:0] n268_O_t1b_t1b; // @[Top.scala 822:22]
  wire  n269_clock; // @[Top.scala 826:22]
  wire  n269_reset; // @[Top.scala 826:22]
  wire  n269_valid_up; // @[Top.scala 826:22]
  wire  n269_valid_down; // @[Top.scala 826:22]
  wire [15:0] n269_I_t0b_t0b; // @[Top.scala 826:22]
  wire [15:0] n269_I_t0b_t1b; // @[Top.scala 826:22]
  wire [15:0] n269_I_t1b_t0b; // @[Top.scala 826:22]
  wire [15:0] n269_I_t1b_t1b; // @[Top.scala 826:22]
  wire [15:0] n269_O_t0b_t0b; // @[Top.scala 826:22]
  wire [15:0] n269_O_t0b_t1b; // @[Top.scala 826:22]
  wire [15:0] n269_O_t1b_t0b; // @[Top.scala 826:22]
  wire [15:0] n269_O_t1b_t1b; // @[Top.scala 826:22]
  wire  n270_valid_up; // @[Top.scala 829:22]
  wire  n270_valid_down; // @[Top.scala 829:22]
  wire  n270_I0; // @[Top.scala 829:22]
  wire [15:0] n270_I1_t0b_t0b; // @[Top.scala 829:22]
  wire [15:0] n270_I1_t0b_t1b; // @[Top.scala 829:22]
  wire [15:0] n270_I1_t1b_t0b; // @[Top.scala 829:22]
  wire [15:0] n270_I1_t1b_t1b; // @[Top.scala 829:22]
  wire  n270_O_t0b; // @[Top.scala 829:22]
  wire [15:0] n270_O_t1b_t0b_t0b; // @[Top.scala 829:22]
  wire [15:0] n270_O_t1b_t0b_t1b; // @[Top.scala 829:22]
  wire [15:0] n270_O_t1b_t1b_t0b; // @[Top.scala 829:22]
  wire [15:0] n270_O_t1b_t1b_t1b; // @[Top.scala 829:22]
  wire  n271_valid_up; // @[Top.scala 833:22]
  wire  n271_valid_down; // @[Top.scala 833:22]
  wire  n271_I_t0b; // @[Top.scala 833:22]
  wire [15:0] n271_I_t1b_t0b_t0b; // @[Top.scala 833:22]
  wire [15:0] n271_I_t1b_t0b_t1b; // @[Top.scala 833:22]
  wire [15:0] n271_I_t1b_t1b_t0b; // @[Top.scala 833:22]
  wire [15:0] n271_I_t1b_t1b_t1b; // @[Top.scala 833:22]
  wire [15:0] n271_O_t0b; // @[Top.scala 833:22]
  wire [15:0] n271_O_t1b; // @[Top.scala 833:22]
  wire  n273_valid_up; // @[Top.scala 836:22]
  wire  n273_valid_down; // @[Top.scala 836:22]
  wire [15:0] n273_I0; // @[Top.scala 836:22]
  wire [15:0] n273_I1_t0b; // @[Top.scala 836:22]
  wire [15:0] n273_I1_t1b; // @[Top.scala 836:22]
  wire [15:0] n273_O_t0b; // @[Top.scala 836:22]
  wire [15:0] n273_O_t1b_t0b; // @[Top.scala 836:22]
  wire [15:0] n273_O_t1b_t1b; // @[Top.scala 836:22]
  Fst n239 ( // @[Top.scala 733:22]
    .valid_up(n239_valid_up),
    .valid_down(n239_valid_down),
    .I_t0b(n239_I_t0b),
    .O(n239_O)
  );
  FIFO_1 n272 ( // @[Top.scala 736:22]
    .clock(n272_clock),
    .reset(n272_reset),
    .valid_up(n272_valid_up),
    .valid_down(n272_valid_down),
    .I(n272_I),
    .O(n272_O)
  );
  FIFO_1 n260 ( // @[Top.scala 739:22]
    .clock(n260_clock),
    .reset(n260_reset),
    .valid_up(n260_valid_up),
    .valid_down(n260_valid_down),
    .I(n260_I),
    .O(n260_O)
  );
  Snd n240 ( // @[Top.scala 742:22]
    .valid_up(n240_valid_up),
    .valid_down(n240_valid_down),
    .I_t1b_t0b(n240_I_t1b_t0b),
    .I_t1b_t1b(n240_I_t1b_t1b),
    .O_t0b(n240_O_t0b),
    .O_t1b(n240_O_t1b)
  );
  Fst_1 n241 ( // @[Top.scala 745:22]
    .valid_up(n241_valid_up),
    .valid_down(n241_valid_down),
    .I_t0b(n241_I_t0b),
    .O(n241_O)
  );
  Snd_1 n242 ( // @[Top.scala 748:22]
    .valid_up(n242_valid_up),
    .valid_down(n242_valid_down),
    .I_t1b(n242_I_t1b),
    .O(n242_O)
  );
  AtomTuple_1 n243 ( // @[Top.scala 751:22]
    .valid_up(n243_valid_up),
    .valid_down(n243_valid_down),
    .I0(n243_I0),
    .I1(n243_I1),
    .O_t0b(n243_O_t0b),
    .O_t1b(n243_O_t1b)
  );
  Add n244 ( // @[Top.scala 755:22]
    .valid_up(n244_valid_up),
    .valid_down(n244_valid_down),
    .I_t0b(n244_I_t0b),
    .I_t1b(n244_I_t1b),
    .O(n244_O)
  );
  InitialDelayCounter_21 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n247 ( // @[Top.scala 759:22]
    .valid_up(n247_valid_up),
    .valid_down(n247_valid_down),
    .I0(n247_I0),
    .O_t0b(n247_O_t0b)
  );
  RShift n248 ( // @[Top.scala 763:22]
    .valid_up(n248_valid_up),
    .valid_down(n248_valid_down),
    .I_t0b(n248_I_t0b),
    .O(n248_O)
  );
  AtomTuple_1 n249 ( // @[Top.scala 766:22]
    .valid_up(n249_valid_up),
    .valid_down(n249_valid_down),
    .I0(n249_I0),
    .I1(n249_I1),
    .O_t0b(n249_O_t0b),
    .O_t1b(n249_O_t1b)
  );
  Eq n250 ( // @[Top.scala 770:22]
    .valid_up(n250_valid_up),
    .valid_down(n250_valid_down),
    .I_t0b(n250_I_t0b),
    .I_t1b(n250_I_t1b),
    .O(n250_O)
  );
  InitialDelayCounter_21 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n253 ( // @[Top.scala 774:22]
    .valid_up(n253_valid_up),
    .valid_down(n253_valid_down),
    .I0(n253_I0),
    .I1(n253_I1),
    .O_t0b(n253_O_t0b),
    .O_t1b(n253_O_t1b)
  );
  Add n254 ( // @[Top.scala 778:22]
    .valid_up(n254_valid_up),
    .valid_down(n254_valid_down),
    .I_t0b(n254_I_t0b),
    .I_t1b(n254_I_t1b),
    .O(n254_O)
  );
  AtomTuple_1 n255 ( // @[Top.scala 781:22]
    .valid_up(n255_valid_up),
    .valid_down(n255_valid_down),
    .I0(n255_I0),
    .I1(n255_I1),
    .O_t0b(n255_O_t0b),
    .O_t1b(n255_O_t1b)
  );
  AtomTuple_9 n256 ( // @[Top.scala 785:22]
    .valid_up(n256_valid_up),
    .valid_down(n256_valid_down),
    .I0(n256_I0),
    .I1_t0b(n256_I1_t0b),
    .I1_t1b(n256_I1_t1b),
    .O_t0b(n256_O_t0b),
    .O_t1b_t0b(n256_O_t1b_t0b),
    .O_t1b_t1b(n256_O_t1b_t1b)
  );
  If n257 ( // @[Top.scala 789:22]
    .valid_up(n257_valid_up),
    .valid_down(n257_valid_down),
    .I_t0b(n257_I_t0b),
    .I_t1b_t0b(n257_I_t1b_t0b),
    .I_t1b_t1b(n257_I_t1b_t1b),
    .O(n257_O)
  );
  AtomTuple_1 n258 ( // @[Top.scala 792:22]
    .valid_up(n258_valid_up),
    .valid_down(n258_valid_down),
    .I0(n258_I0),
    .I1(n258_I1),
    .O_t0b(n258_O_t0b),
    .O_t1b(n258_O_t1b)
  );
  Mul n259 ( // @[Top.scala 796:22]
    .clock(n259_clock),
    .reset(n259_reset),
    .valid_up(n259_valid_up),
    .valid_down(n259_valid_down),
    .I_t0b(n259_I_t0b),
    .I_t1b(n259_I_t1b),
    .O(n259_O)
  );
  AtomTuple_1 n261 ( // @[Top.scala 799:22]
    .valid_up(n261_valid_up),
    .valid_down(n261_valid_down),
    .I0(n261_I0),
    .I1(n261_I1),
    .O_t0b(n261_O_t0b),
    .O_t1b(n261_O_t1b)
  );
  Lt n262 ( // @[Top.scala 803:22]
    .valid_up(n262_valid_up),
    .valid_down(n262_valid_down),
    .I_t0b(n262_I_t0b),
    .I_t1b(n262_I_t1b),
    .O(n262_O)
  );
  InitialDelayCounter_21 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n264 ( // @[Top.scala 807:22]
    .valid_up(n264_valid_up),
    .valid_down(n264_valid_down),
    .I0(n264_I0),
    .I1(n264_I1),
    .O_t0b(n264_O_t0b),
    .O_t1b(n264_O_t1b)
  );
  Sub n265 ( // @[Top.scala 811:22]
    .valid_up(n265_valid_up),
    .valid_down(n265_valid_down),
    .I_t0b(n265_I_t0b),
    .I_t1b(n265_I_t1b),
    .O(n265_O)
  );
  AtomTuple_1 n266 ( // @[Top.scala 814:22]
    .valid_up(n266_valid_up),
    .valid_down(n266_valid_down),
    .I0(n266_I0),
    .I1(n266_I1),
    .O_t0b(n266_O_t0b),
    .O_t1b(n266_O_t1b)
  );
  AtomTuple_1 n267 ( // @[Top.scala 818:22]
    .valid_up(n267_valid_up),
    .valid_down(n267_valid_down),
    .I0(n267_I0),
    .I1(n267_I1),
    .O_t0b(n267_O_t0b),
    .O_t1b(n267_O_t1b)
  );
  AtomTuple_15 n268 ( // @[Top.scala 822:22]
    .valid_up(n268_valid_up),
    .valid_down(n268_valid_down),
    .I0_t0b(n268_I0_t0b),
    .I0_t1b(n268_I0_t1b),
    .I1_t0b(n268_I1_t0b),
    .I1_t1b(n268_I1_t1b),
    .O_t0b_t0b(n268_O_t0b_t0b),
    .O_t0b_t1b(n268_O_t0b_t1b),
    .O_t1b_t0b(n268_O_t1b_t0b),
    .O_t1b_t1b(n268_O_t1b_t1b)
  );
  FIFO_3 n269 ( // @[Top.scala 826:22]
    .clock(n269_clock),
    .reset(n269_reset),
    .valid_up(n269_valid_up),
    .valid_down(n269_valid_down),
    .I_t0b_t0b(n269_I_t0b_t0b),
    .I_t0b_t1b(n269_I_t0b_t1b),
    .I_t1b_t0b(n269_I_t1b_t0b),
    .I_t1b_t1b(n269_I_t1b_t1b),
    .O_t0b_t0b(n269_O_t0b_t0b),
    .O_t0b_t1b(n269_O_t0b_t1b),
    .O_t1b_t0b(n269_O_t1b_t0b),
    .O_t1b_t1b(n269_O_t1b_t1b)
  );
  AtomTuple_16 n270 ( // @[Top.scala 829:22]
    .valid_up(n270_valid_up),
    .valid_down(n270_valid_down),
    .I0(n270_I0),
    .I1_t0b_t0b(n270_I1_t0b_t0b),
    .I1_t0b_t1b(n270_I1_t0b_t1b),
    .I1_t1b_t0b(n270_I1_t1b_t0b),
    .I1_t1b_t1b(n270_I1_t1b_t1b),
    .O_t0b(n270_O_t0b),
    .O_t1b_t0b_t0b(n270_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n270_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n270_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n270_O_t1b_t1b_t1b)
  );
  If_1 n271 ( // @[Top.scala 833:22]
    .valid_up(n271_valid_up),
    .valid_down(n271_valid_down),
    .I_t0b(n271_I_t0b),
    .I_t1b_t0b_t0b(n271_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n271_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n271_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n271_I_t1b_t1b_t1b),
    .O_t0b(n271_O_t0b),
    .O_t1b(n271_O_t1b)
  );
  AtomTuple_3 n273 ( // @[Top.scala 836:22]
    .valid_up(n273_valid_up),
    .valid_down(n273_valid_down),
    .I0(n273_I0),
    .I1_t0b(n273_I1_t0b),
    .I1_t1b(n273_I1_t1b),
    .O_t0b(n273_O_t0b),
    .O_t1b_t0b(n273_O_t1b_t0b),
    .O_t1b_t1b(n273_O_t1b_t1b)
  );
  assign valid_down = n273_valid_down; // @[Top.scala 841:16]
  assign O_t0b = n273_O_t0b; // @[Top.scala 840:7]
  assign O_t1b_t0b = n273_O_t1b_t0b; // @[Top.scala 840:7]
  assign O_t1b_t1b = n273_O_t1b_t1b; // @[Top.scala 840:7]
  assign n239_valid_up = valid_up; // @[Top.scala 735:19]
  assign n239_I_t0b = I_t0b; // @[Top.scala 734:12]
  assign n272_clock = clock;
  assign n272_reset = reset;
  assign n272_valid_up = n239_valid_down; // @[Top.scala 738:19]
  assign n272_I = n239_O; // @[Top.scala 737:12]
  assign n260_clock = clock;
  assign n260_reset = reset;
  assign n260_valid_up = n239_valid_down; // @[Top.scala 741:19]
  assign n260_I = n239_O; // @[Top.scala 740:12]
  assign n240_valid_up = valid_up; // @[Top.scala 744:19]
  assign n240_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 743:12]
  assign n240_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 743:12]
  assign n241_valid_up = n240_valid_down; // @[Top.scala 747:19]
  assign n241_I_t0b = n240_O_t0b; // @[Top.scala 746:12]
  assign n242_valid_up = n240_valid_down; // @[Top.scala 750:19]
  assign n242_I_t1b = n240_O_t1b; // @[Top.scala 749:12]
  assign n243_valid_up = n241_valid_down & n242_valid_down; // @[Top.scala 754:19]
  assign n243_I0 = n241_O; // @[Top.scala 752:13]
  assign n243_I1 = n242_O; // @[Top.scala 753:13]
  assign n244_valid_up = n243_valid_down; // @[Top.scala 757:19]
  assign n244_I_t0b = n243_O_t0b; // @[Top.scala 756:12]
  assign n244_I_t1b = n243_O_t1b; // @[Top.scala 756:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n247_valid_up = n244_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 762:19]
  assign n247_I0 = n244_O; // @[Top.scala 760:13]
  assign n248_valid_up = n247_valid_down; // @[Top.scala 765:19]
  assign n248_I_t0b = n247_O_t0b; // @[Top.scala 764:12]
  assign n249_valid_up = n248_valid_down & n241_valid_down; // @[Top.scala 769:19]
  assign n249_I0 = n248_O; // @[Top.scala 767:13]
  assign n249_I1 = n241_O; // @[Top.scala 768:13]
  assign n250_valid_up = n249_valid_down; // @[Top.scala 772:19]
  assign n250_I_t0b = n249_O_t0b; // @[Top.scala 771:12]
  assign n250_I_t1b = n249_O_t1b; // @[Top.scala 771:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n253_valid_up = n248_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 777:19]
  assign n253_I0 = n248_O; // @[Top.scala 775:13]
  assign n253_I1 = 16'h1; // @[Top.scala 776:13]
  assign n254_valid_up = n253_valid_down; // @[Top.scala 780:19]
  assign n254_I_t0b = n253_O_t0b; // @[Top.scala 779:12]
  assign n254_I_t1b = n253_O_t1b; // @[Top.scala 779:12]
  assign n255_valid_up = n254_valid_down & n248_valid_down; // @[Top.scala 784:19]
  assign n255_I0 = n254_O; // @[Top.scala 782:13]
  assign n255_I1 = n248_O; // @[Top.scala 783:13]
  assign n256_valid_up = n250_valid_down & n255_valid_down; // @[Top.scala 788:19]
  assign n256_I0 = n250_O[0]; // @[Top.scala 786:13]
  assign n256_I1_t0b = n255_O_t0b; // @[Top.scala 787:13]
  assign n256_I1_t1b = n255_O_t1b; // @[Top.scala 787:13]
  assign n257_valid_up = n256_valid_down; // @[Top.scala 791:19]
  assign n257_I_t0b = n256_O_t0b; // @[Top.scala 790:12]
  assign n257_I_t1b_t0b = n256_O_t1b_t0b; // @[Top.scala 790:12]
  assign n257_I_t1b_t1b = n256_O_t1b_t1b; // @[Top.scala 790:12]
  assign n258_valid_up = n257_valid_down; // @[Top.scala 795:19]
  assign n258_I0 = n257_O; // @[Top.scala 793:13]
  assign n258_I1 = n257_O; // @[Top.scala 794:13]
  assign n259_clock = clock;
  assign n259_reset = reset;
  assign n259_valid_up = n258_valid_down; // @[Top.scala 798:19]
  assign n259_I_t0b = n258_O_t0b; // @[Top.scala 797:12]
  assign n259_I_t1b = n258_O_t1b; // @[Top.scala 797:12]
  assign n261_valid_up = n260_valid_down & n259_valid_down; // @[Top.scala 802:19]
  assign n261_I0 = n260_O; // @[Top.scala 800:13]
  assign n261_I1 = n259_O; // @[Top.scala 801:13]
  assign n262_valid_up = n261_valid_down; // @[Top.scala 805:19]
  assign n262_I_t0b = n261_O_t0b; // @[Top.scala 804:12]
  assign n262_I_t1b = n261_O_t1b; // @[Top.scala 804:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n264_valid_up = n257_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 810:19]
  assign n264_I0 = n257_O; // @[Top.scala 808:13]
  assign n264_I1 = 16'h1; // @[Top.scala 809:13]
  assign n265_valid_up = n264_valid_down; // @[Top.scala 813:19]
  assign n265_I_t0b = n264_O_t0b; // @[Top.scala 812:12]
  assign n265_I_t1b = n264_O_t1b; // @[Top.scala 812:12]
  assign n266_valid_up = n241_valid_down & n265_valid_down; // @[Top.scala 817:19]
  assign n266_I0 = n241_O; // @[Top.scala 815:13]
  assign n266_I1 = n265_O; // @[Top.scala 816:13]
  assign n267_valid_up = n257_valid_down & n242_valid_down; // @[Top.scala 821:19]
  assign n267_I0 = n257_O; // @[Top.scala 819:13]
  assign n267_I1 = n242_O; // @[Top.scala 820:13]
  assign n268_valid_up = n266_valid_down & n267_valid_down; // @[Top.scala 825:19]
  assign n268_I0_t0b = n266_O_t0b; // @[Top.scala 823:13]
  assign n268_I0_t1b = n266_O_t1b; // @[Top.scala 823:13]
  assign n268_I1_t0b = n267_O_t0b; // @[Top.scala 824:13]
  assign n268_I1_t1b = n267_O_t1b; // @[Top.scala 824:13]
  assign n269_clock = clock;
  assign n269_reset = reset;
  assign n269_valid_up = n268_valid_down; // @[Top.scala 828:19]
  assign n269_I_t0b_t0b = n268_O_t0b_t0b; // @[Top.scala 827:12]
  assign n269_I_t0b_t1b = n268_O_t0b_t1b; // @[Top.scala 827:12]
  assign n269_I_t1b_t0b = n268_O_t1b_t0b; // @[Top.scala 827:12]
  assign n269_I_t1b_t1b = n268_O_t1b_t1b; // @[Top.scala 827:12]
  assign n270_valid_up = n262_valid_down & n269_valid_down; // @[Top.scala 832:19]
  assign n270_I0 = n262_O[0]; // @[Top.scala 830:13]
  assign n270_I1_t0b_t0b = n269_O_t0b_t0b; // @[Top.scala 831:13]
  assign n270_I1_t0b_t1b = n269_O_t0b_t1b; // @[Top.scala 831:13]
  assign n270_I1_t1b_t0b = n269_O_t1b_t0b; // @[Top.scala 831:13]
  assign n270_I1_t1b_t1b = n269_O_t1b_t1b; // @[Top.scala 831:13]
  assign n271_valid_up = n270_valid_down; // @[Top.scala 835:19]
  assign n271_I_t0b = n270_O_t0b; // @[Top.scala 834:12]
  assign n271_I_t1b_t0b_t0b = n270_O_t1b_t0b_t0b; // @[Top.scala 834:12]
  assign n271_I_t1b_t0b_t1b = n270_O_t1b_t0b_t1b; // @[Top.scala 834:12]
  assign n271_I_t1b_t1b_t0b = n270_O_t1b_t1b_t0b; // @[Top.scala 834:12]
  assign n271_I_t1b_t1b_t1b = n270_O_t1b_t1b_t1b; // @[Top.scala 834:12]
  assign n273_valid_up = n272_valid_down & n271_valid_down; // @[Top.scala 839:19]
  assign n273_I0 = n272_O; // @[Top.scala 837:13]
  assign n273_I1_t0b = n271_O_t0b; // @[Top.scala 838:13]
  assign n273_I1_t1b = n271_O_t1b; // @[Top.scala 838:13]
endmodule
module MapT_7(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_7 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
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
  wire  n276_valid_up; // @[Top.scala 847:22]
  wire  n276_valid_down; // @[Top.scala 847:22]
  wire [15:0] n276_I_t0b; // @[Top.scala 847:22]
  wire [15:0] n276_O; // @[Top.scala 847:22]
  wire  n309_clock; // @[Top.scala 850:22]
  wire  n309_reset; // @[Top.scala 850:22]
  wire  n309_valid_up; // @[Top.scala 850:22]
  wire  n309_valid_down; // @[Top.scala 850:22]
  wire [15:0] n309_I; // @[Top.scala 850:22]
  wire [15:0] n309_O; // @[Top.scala 850:22]
  wire  n297_clock; // @[Top.scala 853:22]
  wire  n297_reset; // @[Top.scala 853:22]
  wire  n297_valid_up; // @[Top.scala 853:22]
  wire  n297_valid_down; // @[Top.scala 853:22]
  wire [15:0] n297_I; // @[Top.scala 853:22]
  wire [15:0] n297_O; // @[Top.scala 853:22]
  wire  n277_valid_up; // @[Top.scala 856:22]
  wire  n277_valid_down; // @[Top.scala 856:22]
  wire [15:0] n277_I_t1b_t0b; // @[Top.scala 856:22]
  wire [15:0] n277_I_t1b_t1b; // @[Top.scala 856:22]
  wire [15:0] n277_O_t0b; // @[Top.scala 856:22]
  wire [15:0] n277_O_t1b; // @[Top.scala 856:22]
  wire  n278_valid_up; // @[Top.scala 859:22]
  wire  n278_valid_down; // @[Top.scala 859:22]
  wire [15:0] n278_I_t0b; // @[Top.scala 859:22]
  wire [15:0] n278_O; // @[Top.scala 859:22]
  wire  n279_valid_up; // @[Top.scala 862:22]
  wire  n279_valid_down; // @[Top.scala 862:22]
  wire [15:0] n279_I_t1b; // @[Top.scala 862:22]
  wire [15:0] n279_O; // @[Top.scala 862:22]
  wire  n280_valid_up; // @[Top.scala 865:22]
  wire  n280_valid_down; // @[Top.scala 865:22]
  wire [15:0] n280_I0; // @[Top.scala 865:22]
  wire [15:0] n280_I1; // @[Top.scala 865:22]
  wire [15:0] n280_O_t0b; // @[Top.scala 865:22]
  wire [15:0] n280_O_t1b; // @[Top.scala 865:22]
  wire  n281_valid_up; // @[Top.scala 869:22]
  wire  n281_valid_down; // @[Top.scala 869:22]
  wire [15:0] n281_I_t0b; // @[Top.scala 869:22]
  wire [15:0] n281_I_t1b; // @[Top.scala 869:22]
  wire [15:0] n281_O; // @[Top.scala 869:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n284_valid_up; // @[Top.scala 873:22]
  wire  n284_valid_down; // @[Top.scala 873:22]
  wire [15:0] n284_I0; // @[Top.scala 873:22]
  wire [15:0] n284_O_t0b; // @[Top.scala 873:22]
  wire  n285_valid_up; // @[Top.scala 877:22]
  wire  n285_valid_down; // @[Top.scala 877:22]
  wire [15:0] n285_I_t0b; // @[Top.scala 877:22]
  wire [15:0] n285_O; // @[Top.scala 877:22]
  wire  n286_valid_up; // @[Top.scala 880:22]
  wire  n286_valid_down; // @[Top.scala 880:22]
  wire [15:0] n286_I0; // @[Top.scala 880:22]
  wire [15:0] n286_I1; // @[Top.scala 880:22]
  wire [15:0] n286_O_t0b; // @[Top.scala 880:22]
  wire [15:0] n286_O_t1b; // @[Top.scala 880:22]
  wire  n287_valid_up; // @[Top.scala 884:22]
  wire  n287_valid_down; // @[Top.scala 884:22]
  wire [15:0] n287_I_t0b; // @[Top.scala 884:22]
  wire [15:0] n287_I_t1b; // @[Top.scala 884:22]
  wire [15:0] n287_O; // @[Top.scala 884:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n290_valid_up; // @[Top.scala 888:22]
  wire  n290_valid_down; // @[Top.scala 888:22]
  wire [15:0] n290_I0; // @[Top.scala 888:22]
  wire [15:0] n290_I1; // @[Top.scala 888:22]
  wire [15:0] n290_O_t0b; // @[Top.scala 888:22]
  wire [15:0] n290_O_t1b; // @[Top.scala 888:22]
  wire  n291_valid_up; // @[Top.scala 892:22]
  wire  n291_valid_down; // @[Top.scala 892:22]
  wire [15:0] n291_I_t0b; // @[Top.scala 892:22]
  wire [15:0] n291_I_t1b; // @[Top.scala 892:22]
  wire [15:0] n291_O; // @[Top.scala 892:22]
  wire  n292_valid_up; // @[Top.scala 895:22]
  wire  n292_valid_down; // @[Top.scala 895:22]
  wire [15:0] n292_I0; // @[Top.scala 895:22]
  wire [15:0] n292_I1; // @[Top.scala 895:22]
  wire [15:0] n292_O_t0b; // @[Top.scala 895:22]
  wire [15:0] n292_O_t1b; // @[Top.scala 895:22]
  wire  n293_valid_up; // @[Top.scala 899:22]
  wire  n293_valid_down; // @[Top.scala 899:22]
  wire  n293_I0; // @[Top.scala 899:22]
  wire [15:0] n293_I1_t0b; // @[Top.scala 899:22]
  wire [15:0] n293_I1_t1b; // @[Top.scala 899:22]
  wire  n293_O_t0b; // @[Top.scala 899:22]
  wire [15:0] n293_O_t1b_t0b; // @[Top.scala 899:22]
  wire [15:0] n293_O_t1b_t1b; // @[Top.scala 899:22]
  wire  n294_valid_up; // @[Top.scala 903:22]
  wire  n294_valid_down; // @[Top.scala 903:22]
  wire  n294_I_t0b; // @[Top.scala 903:22]
  wire [15:0] n294_I_t1b_t0b; // @[Top.scala 903:22]
  wire [15:0] n294_I_t1b_t1b; // @[Top.scala 903:22]
  wire [15:0] n294_O; // @[Top.scala 903:22]
  wire  n295_valid_up; // @[Top.scala 906:22]
  wire  n295_valid_down; // @[Top.scala 906:22]
  wire [15:0] n295_I0; // @[Top.scala 906:22]
  wire [15:0] n295_I1; // @[Top.scala 906:22]
  wire [15:0] n295_O_t0b; // @[Top.scala 906:22]
  wire [15:0] n295_O_t1b; // @[Top.scala 906:22]
  wire  n296_clock; // @[Top.scala 910:22]
  wire  n296_reset; // @[Top.scala 910:22]
  wire  n296_valid_up; // @[Top.scala 910:22]
  wire  n296_valid_down; // @[Top.scala 910:22]
  wire [15:0] n296_I_t0b; // @[Top.scala 910:22]
  wire [15:0] n296_I_t1b; // @[Top.scala 910:22]
  wire [15:0] n296_O; // @[Top.scala 910:22]
  wire  n298_valid_up; // @[Top.scala 913:22]
  wire  n298_valid_down; // @[Top.scala 913:22]
  wire [15:0] n298_I0; // @[Top.scala 913:22]
  wire [15:0] n298_I1; // @[Top.scala 913:22]
  wire [15:0] n298_O_t0b; // @[Top.scala 913:22]
  wire [15:0] n298_O_t1b; // @[Top.scala 913:22]
  wire  n299_valid_up; // @[Top.scala 917:22]
  wire  n299_valid_down; // @[Top.scala 917:22]
  wire [15:0] n299_I_t0b; // @[Top.scala 917:22]
  wire [15:0] n299_I_t1b; // @[Top.scala 917:22]
  wire [15:0] n299_O; // @[Top.scala 917:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n301_valid_up; // @[Top.scala 921:22]
  wire  n301_valid_down; // @[Top.scala 921:22]
  wire [15:0] n301_I0; // @[Top.scala 921:22]
  wire [15:0] n301_I1; // @[Top.scala 921:22]
  wire [15:0] n301_O_t0b; // @[Top.scala 921:22]
  wire [15:0] n301_O_t1b; // @[Top.scala 921:22]
  wire  n302_valid_up; // @[Top.scala 925:22]
  wire  n302_valid_down; // @[Top.scala 925:22]
  wire [15:0] n302_I_t0b; // @[Top.scala 925:22]
  wire [15:0] n302_I_t1b; // @[Top.scala 925:22]
  wire [15:0] n302_O; // @[Top.scala 925:22]
  wire  n303_valid_up; // @[Top.scala 928:22]
  wire  n303_valid_down; // @[Top.scala 928:22]
  wire [15:0] n303_I0; // @[Top.scala 928:22]
  wire [15:0] n303_I1; // @[Top.scala 928:22]
  wire [15:0] n303_O_t0b; // @[Top.scala 928:22]
  wire [15:0] n303_O_t1b; // @[Top.scala 928:22]
  wire  n304_valid_up; // @[Top.scala 932:22]
  wire  n304_valid_down; // @[Top.scala 932:22]
  wire [15:0] n304_I0; // @[Top.scala 932:22]
  wire [15:0] n304_I1; // @[Top.scala 932:22]
  wire [15:0] n304_O_t0b; // @[Top.scala 932:22]
  wire [15:0] n304_O_t1b; // @[Top.scala 932:22]
  wire  n305_valid_up; // @[Top.scala 936:22]
  wire  n305_valid_down; // @[Top.scala 936:22]
  wire [15:0] n305_I0_t0b; // @[Top.scala 936:22]
  wire [15:0] n305_I0_t1b; // @[Top.scala 936:22]
  wire [15:0] n305_I1_t0b; // @[Top.scala 936:22]
  wire [15:0] n305_I1_t1b; // @[Top.scala 936:22]
  wire [15:0] n305_O_t0b_t0b; // @[Top.scala 936:22]
  wire [15:0] n305_O_t0b_t1b; // @[Top.scala 936:22]
  wire [15:0] n305_O_t1b_t0b; // @[Top.scala 936:22]
  wire [15:0] n305_O_t1b_t1b; // @[Top.scala 936:22]
  wire  n306_clock; // @[Top.scala 940:22]
  wire  n306_reset; // @[Top.scala 940:22]
  wire  n306_valid_up; // @[Top.scala 940:22]
  wire  n306_valid_down; // @[Top.scala 940:22]
  wire [15:0] n306_I_t0b_t0b; // @[Top.scala 940:22]
  wire [15:0] n306_I_t0b_t1b; // @[Top.scala 940:22]
  wire [15:0] n306_I_t1b_t0b; // @[Top.scala 940:22]
  wire [15:0] n306_I_t1b_t1b; // @[Top.scala 940:22]
  wire [15:0] n306_O_t0b_t0b; // @[Top.scala 940:22]
  wire [15:0] n306_O_t0b_t1b; // @[Top.scala 940:22]
  wire [15:0] n306_O_t1b_t0b; // @[Top.scala 940:22]
  wire [15:0] n306_O_t1b_t1b; // @[Top.scala 940:22]
  wire  n307_valid_up; // @[Top.scala 943:22]
  wire  n307_valid_down; // @[Top.scala 943:22]
  wire  n307_I0; // @[Top.scala 943:22]
  wire [15:0] n307_I1_t0b_t0b; // @[Top.scala 943:22]
  wire [15:0] n307_I1_t0b_t1b; // @[Top.scala 943:22]
  wire [15:0] n307_I1_t1b_t0b; // @[Top.scala 943:22]
  wire [15:0] n307_I1_t1b_t1b; // @[Top.scala 943:22]
  wire  n307_O_t0b; // @[Top.scala 943:22]
  wire [15:0] n307_O_t1b_t0b_t0b; // @[Top.scala 943:22]
  wire [15:0] n307_O_t1b_t0b_t1b; // @[Top.scala 943:22]
  wire [15:0] n307_O_t1b_t1b_t0b; // @[Top.scala 943:22]
  wire [15:0] n307_O_t1b_t1b_t1b; // @[Top.scala 943:22]
  wire  n308_valid_up; // @[Top.scala 947:22]
  wire  n308_valid_down; // @[Top.scala 947:22]
  wire  n308_I_t0b; // @[Top.scala 947:22]
  wire [15:0] n308_I_t1b_t0b_t0b; // @[Top.scala 947:22]
  wire [15:0] n308_I_t1b_t0b_t1b; // @[Top.scala 947:22]
  wire [15:0] n308_I_t1b_t1b_t0b; // @[Top.scala 947:22]
  wire [15:0] n308_I_t1b_t1b_t1b; // @[Top.scala 947:22]
  wire [15:0] n308_O_t0b; // @[Top.scala 947:22]
  wire [15:0] n308_O_t1b; // @[Top.scala 947:22]
  wire  n310_valid_up; // @[Top.scala 950:22]
  wire  n310_valid_down; // @[Top.scala 950:22]
  wire [15:0] n310_I0; // @[Top.scala 950:22]
  wire [15:0] n310_I1_t0b; // @[Top.scala 950:22]
  wire [15:0] n310_I1_t1b; // @[Top.scala 950:22]
  wire [15:0] n310_O_t0b; // @[Top.scala 950:22]
  wire [15:0] n310_O_t1b_t0b; // @[Top.scala 950:22]
  wire [15:0] n310_O_t1b_t1b; // @[Top.scala 950:22]
  Fst n276 ( // @[Top.scala 847:22]
    .valid_up(n276_valid_up),
    .valid_down(n276_valid_down),
    .I_t0b(n276_I_t0b),
    .O(n276_O)
  );
  FIFO_1 n309 ( // @[Top.scala 850:22]
    .clock(n309_clock),
    .reset(n309_reset),
    .valid_up(n309_valid_up),
    .valid_down(n309_valid_down),
    .I(n309_I),
    .O(n309_O)
  );
  FIFO_1 n297 ( // @[Top.scala 853:22]
    .clock(n297_clock),
    .reset(n297_reset),
    .valid_up(n297_valid_up),
    .valid_down(n297_valid_down),
    .I(n297_I),
    .O(n297_O)
  );
  Snd n277 ( // @[Top.scala 856:22]
    .valid_up(n277_valid_up),
    .valid_down(n277_valid_down),
    .I_t1b_t0b(n277_I_t1b_t0b),
    .I_t1b_t1b(n277_I_t1b_t1b),
    .O_t0b(n277_O_t0b),
    .O_t1b(n277_O_t1b)
  );
  Fst_1 n278 ( // @[Top.scala 859:22]
    .valid_up(n278_valid_up),
    .valid_down(n278_valid_down),
    .I_t0b(n278_I_t0b),
    .O(n278_O)
  );
  Snd_1 n279 ( // @[Top.scala 862:22]
    .valid_up(n279_valid_up),
    .valid_down(n279_valid_down),
    .I_t1b(n279_I_t1b),
    .O(n279_O)
  );
  AtomTuple_1 n280 ( // @[Top.scala 865:22]
    .valid_up(n280_valid_up),
    .valid_down(n280_valid_down),
    .I0(n280_I0),
    .I1(n280_I1),
    .O_t0b(n280_O_t0b),
    .O_t1b(n280_O_t1b)
  );
  Add n281 ( // @[Top.scala 869:22]
    .valid_up(n281_valid_up),
    .valid_down(n281_valid_down),
    .I_t0b(n281_I_t0b),
    .I_t1b(n281_I_t1b),
    .O(n281_O)
  );
  InitialDelayCounter_24 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n284 ( // @[Top.scala 873:22]
    .valid_up(n284_valid_up),
    .valid_down(n284_valid_down),
    .I0(n284_I0),
    .O_t0b(n284_O_t0b)
  );
  RShift n285 ( // @[Top.scala 877:22]
    .valid_up(n285_valid_up),
    .valid_down(n285_valid_down),
    .I_t0b(n285_I_t0b),
    .O(n285_O)
  );
  AtomTuple_1 n286 ( // @[Top.scala 880:22]
    .valid_up(n286_valid_up),
    .valid_down(n286_valid_down),
    .I0(n286_I0),
    .I1(n286_I1),
    .O_t0b(n286_O_t0b),
    .O_t1b(n286_O_t1b)
  );
  Eq n287 ( // @[Top.scala 884:22]
    .valid_up(n287_valid_up),
    .valid_down(n287_valid_down),
    .I_t0b(n287_I_t0b),
    .I_t1b(n287_I_t1b),
    .O(n287_O)
  );
  InitialDelayCounter_24 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n290 ( // @[Top.scala 888:22]
    .valid_up(n290_valid_up),
    .valid_down(n290_valid_down),
    .I0(n290_I0),
    .I1(n290_I1),
    .O_t0b(n290_O_t0b),
    .O_t1b(n290_O_t1b)
  );
  Add n291 ( // @[Top.scala 892:22]
    .valid_up(n291_valid_up),
    .valid_down(n291_valid_down),
    .I_t0b(n291_I_t0b),
    .I_t1b(n291_I_t1b),
    .O(n291_O)
  );
  AtomTuple_1 n292 ( // @[Top.scala 895:22]
    .valid_up(n292_valid_up),
    .valid_down(n292_valid_down),
    .I0(n292_I0),
    .I1(n292_I1),
    .O_t0b(n292_O_t0b),
    .O_t1b(n292_O_t1b)
  );
  AtomTuple_9 n293 ( // @[Top.scala 899:22]
    .valid_up(n293_valid_up),
    .valid_down(n293_valid_down),
    .I0(n293_I0),
    .I1_t0b(n293_I1_t0b),
    .I1_t1b(n293_I1_t1b),
    .O_t0b(n293_O_t0b),
    .O_t1b_t0b(n293_O_t1b_t0b),
    .O_t1b_t1b(n293_O_t1b_t1b)
  );
  If n294 ( // @[Top.scala 903:22]
    .valid_up(n294_valid_up),
    .valid_down(n294_valid_down),
    .I_t0b(n294_I_t0b),
    .I_t1b_t0b(n294_I_t1b_t0b),
    .I_t1b_t1b(n294_I_t1b_t1b),
    .O(n294_O)
  );
  AtomTuple_1 n295 ( // @[Top.scala 906:22]
    .valid_up(n295_valid_up),
    .valid_down(n295_valid_down),
    .I0(n295_I0),
    .I1(n295_I1),
    .O_t0b(n295_O_t0b),
    .O_t1b(n295_O_t1b)
  );
  Mul n296 ( // @[Top.scala 910:22]
    .clock(n296_clock),
    .reset(n296_reset),
    .valid_up(n296_valid_up),
    .valid_down(n296_valid_down),
    .I_t0b(n296_I_t0b),
    .I_t1b(n296_I_t1b),
    .O(n296_O)
  );
  AtomTuple_1 n298 ( // @[Top.scala 913:22]
    .valid_up(n298_valid_up),
    .valid_down(n298_valid_down),
    .I0(n298_I0),
    .I1(n298_I1),
    .O_t0b(n298_O_t0b),
    .O_t1b(n298_O_t1b)
  );
  Lt n299 ( // @[Top.scala 917:22]
    .valid_up(n299_valid_up),
    .valid_down(n299_valid_down),
    .I_t0b(n299_I_t0b),
    .I_t1b(n299_I_t1b),
    .O(n299_O)
  );
  InitialDelayCounter_24 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n301 ( // @[Top.scala 921:22]
    .valid_up(n301_valid_up),
    .valid_down(n301_valid_down),
    .I0(n301_I0),
    .I1(n301_I1),
    .O_t0b(n301_O_t0b),
    .O_t1b(n301_O_t1b)
  );
  Sub n302 ( // @[Top.scala 925:22]
    .valid_up(n302_valid_up),
    .valid_down(n302_valid_down),
    .I_t0b(n302_I_t0b),
    .I_t1b(n302_I_t1b),
    .O(n302_O)
  );
  AtomTuple_1 n303 ( // @[Top.scala 928:22]
    .valid_up(n303_valid_up),
    .valid_down(n303_valid_down),
    .I0(n303_I0),
    .I1(n303_I1),
    .O_t0b(n303_O_t0b),
    .O_t1b(n303_O_t1b)
  );
  AtomTuple_1 n304 ( // @[Top.scala 932:22]
    .valid_up(n304_valid_up),
    .valid_down(n304_valid_down),
    .I0(n304_I0),
    .I1(n304_I1),
    .O_t0b(n304_O_t0b),
    .O_t1b(n304_O_t1b)
  );
  AtomTuple_15 n305 ( // @[Top.scala 936:22]
    .valid_up(n305_valid_up),
    .valid_down(n305_valid_down),
    .I0_t0b(n305_I0_t0b),
    .I0_t1b(n305_I0_t1b),
    .I1_t0b(n305_I1_t0b),
    .I1_t1b(n305_I1_t1b),
    .O_t0b_t0b(n305_O_t0b_t0b),
    .O_t0b_t1b(n305_O_t0b_t1b),
    .O_t1b_t0b(n305_O_t1b_t0b),
    .O_t1b_t1b(n305_O_t1b_t1b)
  );
  FIFO_3 n306 ( // @[Top.scala 940:22]
    .clock(n306_clock),
    .reset(n306_reset),
    .valid_up(n306_valid_up),
    .valid_down(n306_valid_down),
    .I_t0b_t0b(n306_I_t0b_t0b),
    .I_t0b_t1b(n306_I_t0b_t1b),
    .I_t1b_t0b(n306_I_t1b_t0b),
    .I_t1b_t1b(n306_I_t1b_t1b),
    .O_t0b_t0b(n306_O_t0b_t0b),
    .O_t0b_t1b(n306_O_t0b_t1b),
    .O_t1b_t0b(n306_O_t1b_t0b),
    .O_t1b_t1b(n306_O_t1b_t1b)
  );
  AtomTuple_16 n307 ( // @[Top.scala 943:22]
    .valid_up(n307_valid_up),
    .valid_down(n307_valid_down),
    .I0(n307_I0),
    .I1_t0b_t0b(n307_I1_t0b_t0b),
    .I1_t0b_t1b(n307_I1_t0b_t1b),
    .I1_t1b_t0b(n307_I1_t1b_t0b),
    .I1_t1b_t1b(n307_I1_t1b_t1b),
    .O_t0b(n307_O_t0b),
    .O_t1b_t0b_t0b(n307_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n307_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n307_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n307_O_t1b_t1b_t1b)
  );
  If_1 n308 ( // @[Top.scala 947:22]
    .valid_up(n308_valid_up),
    .valid_down(n308_valid_down),
    .I_t0b(n308_I_t0b),
    .I_t1b_t0b_t0b(n308_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n308_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n308_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n308_I_t1b_t1b_t1b),
    .O_t0b(n308_O_t0b),
    .O_t1b(n308_O_t1b)
  );
  AtomTuple_3 n310 ( // @[Top.scala 950:22]
    .valid_up(n310_valid_up),
    .valid_down(n310_valid_down),
    .I0(n310_I0),
    .I1_t0b(n310_I1_t0b),
    .I1_t1b(n310_I1_t1b),
    .O_t0b(n310_O_t0b),
    .O_t1b_t0b(n310_O_t1b_t0b),
    .O_t1b_t1b(n310_O_t1b_t1b)
  );
  assign valid_down = n310_valid_down; // @[Top.scala 955:16]
  assign O_t0b = n310_O_t0b; // @[Top.scala 954:7]
  assign O_t1b_t0b = n310_O_t1b_t0b; // @[Top.scala 954:7]
  assign O_t1b_t1b = n310_O_t1b_t1b; // @[Top.scala 954:7]
  assign n276_valid_up = valid_up; // @[Top.scala 849:19]
  assign n276_I_t0b = I_t0b; // @[Top.scala 848:12]
  assign n309_clock = clock;
  assign n309_reset = reset;
  assign n309_valid_up = n276_valid_down; // @[Top.scala 852:19]
  assign n309_I = n276_O; // @[Top.scala 851:12]
  assign n297_clock = clock;
  assign n297_reset = reset;
  assign n297_valid_up = n276_valid_down; // @[Top.scala 855:19]
  assign n297_I = n276_O; // @[Top.scala 854:12]
  assign n277_valid_up = valid_up; // @[Top.scala 858:19]
  assign n277_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 857:12]
  assign n277_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 857:12]
  assign n278_valid_up = n277_valid_down; // @[Top.scala 861:19]
  assign n278_I_t0b = n277_O_t0b; // @[Top.scala 860:12]
  assign n279_valid_up = n277_valid_down; // @[Top.scala 864:19]
  assign n279_I_t1b = n277_O_t1b; // @[Top.scala 863:12]
  assign n280_valid_up = n278_valid_down & n279_valid_down; // @[Top.scala 868:19]
  assign n280_I0 = n278_O; // @[Top.scala 866:13]
  assign n280_I1 = n279_O; // @[Top.scala 867:13]
  assign n281_valid_up = n280_valid_down; // @[Top.scala 871:19]
  assign n281_I_t0b = n280_O_t0b; // @[Top.scala 870:12]
  assign n281_I_t1b = n280_O_t1b; // @[Top.scala 870:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n284_valid_up = n281_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 876:19]
  assign n284_I0 = n281_O; // @[Top.scala 874:13]
  assign n285_valid_up = n284_valid_down; // @[Top.scala 879:19]
  assign n285_I_t0b = n284_O_t0b; // @[Top.scala 878:12]
  assign n286_valid_up = n285_valid_down & n278_valid_down; // @[Top.scala 883:19]
  assign n286_I0 = n285_O; // @[Top.scala 881:13]
  assign n286_I1 = n278_O; // @[Top.scala 882:13]
  assign n287_valid_up = n286_valid_down; // @[Top.scala 886:19]
  assign n287_I_t0b = n286_O_t0b; // @[Top.scala 885:12]
  assign n287_I_t1b = n286_O_t1b; // @[Top.scala 885:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n290_valid_up = n285_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 891:19]
  assign n290_I0 = n285_O; // @[Top.scala 889:13]
  assign n290_I1 = 16'h1; // @[Top.scala 890:13]
  assign n291_valid_up = n290_valid_down; // @[Top.scala 894:19]
  assign n291_I_t0b = n290_O_t0b; // @[Top.scala 893:12]
  assign n291_I_t1b = n290_O_t1b; // @[Top.scala 893:12]
  assign n292_valid_up = n291_valid_down & n285_valid_down; // @[Top.scala 898:19]
  assign n292_I0 = n291_O; // @[Top.scala 896:13]
  assign n292_I1 = n285_O; // @[Top.scala 897:13]
  assign n293_valid_up = n287_valid_down & n292_valid_down; // @[Top.scala 902:19]
  assign n293_I0 = n287_O[0]; // @[Top.scala 900:13]
  assign n293_I1_t0b = n292_O_t0b; // @[Top.scala 901:13]
  assign n293_I1_t1b = n292_O_t1b; // @[Top.scala 901:13]
  assign n294_valid_up = n293_valid_down; // @[Top.scala 905:19]
  assign n294_I_t0b = n293_O_t0b; // @[Top.scala 904:12]
  assign n294_I_t1b_t0b = n293_O_t1b_t0b; // @[Top.scala 904:12]
  assign n294_I_t1b_t1b = n293_O_t1b_t1b; // @[Top.scala 904:12]
  assign n295_valid_up = n294_valid_down; // @[Top.scala 909:19]
  assign n295_I0 = n294_O; // @[Top.scala 907:13]
  assign n295_I1 = n294_O; // @[Top.scala 908:13]
  assign n296_clock = clock;
  assign n296_reset = reset;
  assign n296_valid_up = n295_valid_down; // @[Top.scala 912:19]
  assign n296_I_t0b = n295_O_t0b; // @[Top.scala 911:12]
  assign n296_I_t1b = n295_O_t1b; // @[Top.scala 911:12]
  assign n298_valid_up = n297_valid_down & n296_valid_down; // @[Top.scala 916:19]
  assign n298_I0 = n297_O; // @[Top.scala 914:13]
  assign n298_I1 = n296_O; // @[Top.scala 915:13]
  assign n299_valid_up = n298_valid_down; // @[Top.scala 919:19]
  assign n299_I_t0b = n298_O_t0b; // @[Top.scala 918:12]
  assign n299_I_t1b = n298_O_t1b; // @[Top.scala 918:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n301_valid_up = n294_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 924:19]
  assign n301_I0 = n294_O; // @[Top.scala 922:13]
  assign n301_I1 = 16'h1; // @[Top.scala 923:13]
  assign n302_valid_up = n301_valid_down; // @[Top.scala 927:19]
  assign n302_I_t0b = n301_O_t0b; // @[Top.scala 926:12]
  assign n302_I_t1b = n301_O_t1b; // @[Top.scala 926:12]
  assign n303_valid_up = n278_valid_down & n302_valid_down; // @[Top.scala 931:19]
  assign n303_I0 = n278_O; // @[Top.scala 929:13]
  assign n303_I1 = n302_O; // @[Top.scala 930:13]
  assign n304_valid_up = n294_valid_down & n279_valid_down; // @[Top.scala 935:19]
  assign n304_I0 = n294_O; // @[Top.scala 933:13]
  assign n304_I1 = n279_O; // @[Top.scala 934:13]
  assign n305_valid_up = n303_valid_down & n304_valid_down; // @[Top.scala 939:19]
  assign n305_I0_t0b = n303_O_t0b; // @[Top.scala 937:13]
  assign n305_I0_t1b = n303_O_t1b; // @[Top.scala 937:13]
  assign n305_I1_t0b = n304_O_t0b; // @[Top.scala 938:13]
  assign n305_I1_t1b = n304_O_t1b; // @[Top.scala 938:13]
  assign n306_clock = clock;
  assign n306_reset = reset;
  assign n306_valid_up = n305_valid_down; // @[Top.scala 942:19]
  assign n306_I_t0b_t0b = n305_O_t0b_t0b; // @[Top.scala 941:12]
  assign n306_I_t0b_t1b = n305_O_t0b_t1b; // @[Top.scala 941:12]
  assign n306_I_t1b_t0b = n305_O_t1b_t0b; // @[Top.scala 941:12]
  assign n306_I_t1b_t1b = n305_O_t1b_t1b; // @[Top.scala 941:12]
  assign n307_valid_up = n299_valid_down & n306_valid_down; // @[Top.scala 946:19]
  assign n307_I0 = n299_O[0]; // @[Top.scala 944:13]
  assign n307_I1_t0b_t0b = n306_O_t0b_t0b; // @[Top.scala 945:13]
  assign n307_I1_t0b_t1b = n306_O_t0b_t1b; // @[Top.scala 945:13]
  assign n307_I1_t1b_t0b = n306_O_t1b_t0b; // @[Top.scala 945:13]
  assign n307_I1_t1b_t1b = n306_O_t1b_t1b; // @[Top.scala 945:13]
  assign n308_valid_up = n307_valid_down; // @[Top.scala 949:19]
  assign n308_I_t0b = n307_O_t0b; // @[Top.scala 948:12]
  assign n308_I_t1b_t0b_t0b = n307_O_t1b_t0b_t0b; // @[Top.scala 948:12]
  assign n308_I_t1b_t0b_t1b = n307_O_t1b_t0b_t1b; // @[Top.scala 948:12]
  assign n308_I_t1b_t1b_t0b = n307_O_t1b_t1b_t0b; // @[Top.scala 948:12]
  assign n308_I_t1b_t1b_t1b = n307_O_t1b_t1b_t1b; // @[Top.scala 948:12]
  assign n310_valid_up = n309_valid_down & n308_valid_down; // @[Top.scala 953:19]
  assign n310_I0 = n309_O; // @[Top.scala 951:13]
  assign n310_I1_t0b = n308_O_t0b; // @[Top.scala 952:13]
  assign n310_I1_t1b = n308_O_t1b; // @[Top.scala 952:13]
endmodule
module MapT_8(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_8 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
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
  wire  n313_valid_up; // @[Top.scala 961:22]
  wire  n313_valid_down; // @[Top.scala 961:22]
  wire [15:0] n313_I_t0b; // @[Top.scala 961:22]
  wire [15:0] n313_O; // @[Top.scala 961:22]
  wire  n346_clock; // @[Top.scala 964:22]
  wire  n346_reset; // @[Top.scala 964:22]
  wire  n346_valid_up; // @[Top.scala 964:22]
  wire  n346_valid_down; // @[Top.scala 964:22]
  wire [15:0] n346_I; // @[Top.scala 964:22]
  wire [15:0] n346_O; // @[Top.scala 964:22]
  wire  n334_clock; // @[Top.scala 967:22]
  wire  n334_reset; // @[Top.scala 967:22]
  wire  n334_valid_up; // @[Top.scala 967:22]
  wire  n334_valid_down; // @[Top.scala 967:22]
  wire [15:0] n334_I; // @[Top.scala 967:22]
  wire [15:0] n334_O; // @[Top.scala 967:22]
  wire  n314_valid_up; // @[Top.scala 970:22]
  wire  n314_valid_down; // @[Top.scala 970:22]
  wire [15:0] n314_I_t1b_t0b; // @[Top.scala 970:22]
  wire [15:0] n314_I_t1b_t1b; // @[Top.scala 970:22]
  wire [15:0] n314_O_t0b; // @[Top.scala 970:22]
  wire [15:0] n314_O_t1b; // @[Top.scala 970:22]
  wire  n315_valid_up; // @[Top.scala 973:22]
  wire  n315_valid_down; // @[Top.scala 973:22]
  wire [15:0] n315_I_t0b; // @[Top.scala 973:22]
  wire [15:0] n315_O; // @[Top.scala 973:22]
  wire  n316_valid_up; // @[Top.scala 976:22]
  wire  n316_valid_down; // @[Top.scala 976:22]
  wire [15:0] n316_I_t1b; // @[Top.scala 976:22]
  wire [15:0] n316_O; // @[Top.scala 976:22]
  wire  n317_valid_up; // @[Top.scala 979:22]
  wire  n317_valid_down; // @[Top.scala 979:22]
  wire [15:0] n317_I0; // @[Top.scala 979:22]
  wire [15:0] n317_I1; // @[Top.scala 979:22]
  wire [15:0] n317_O_t0b; // @[Top.scala 979:22]
  wire [15:0] n317_O_t1b; // @[Top.scala 979:22]
  wire  n318_valid_up; // @[Top.scala 983:22]
  wire  n318_valid_down; // @[Top.scala 983:22]
  wire [15:0] n318_I_t0b; // @[Top.scala 983:22]
  wire [15:0] n318_I_t1b; // @[Top.scala 983:22]
  wire [15:0] n318_O; // @[Top.scala 983:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n321_valid_up; // @[Top.scala 987:22]
  wire  n321_valid_down; // @[Top.scala 987:22]
  wire [15:0] n321_I0; // @[Top.scala 987:22]
  wire [15:0] n321_O_t0b; // @[Top.scala 987:22]
  wire  n322_valid_up; // @[Top.scala 991:22]
  wire  n322_valid_down; // @[Top.scala 991:22]
  wire [15:0] n322_I_t0b; // @[Top.scala 991:22]
  wire [15:0] n322_O; // @[Top.scala 991:22]
  wire  n323_valid_up; // @[Top.scala 994:22]
  wire  n323_valid_down; // @[Top.scala 994:22]
  wire [15:0] n323_I0; // @[Top.scala 994:22]
  wire [15:0] n323_I1; // @[Top.scala 994:22]
  wire [15:0] n323_O_t0b; // @[Top.scala 994:22]
  wire [15:0] n323_O_t1b; // @[Top.scala 994:22]
  wire  n324_valid_up; // @[Top.scala 998:22]
  wire  n324_valid_down; // @[Top.scala 998:22]
  wire [15:0] n324_I_t0b; // @[Top.scala 998:22]
  wire [15:0] n324_I_t1b; // @[Top.scala 998:22]
  wire [15:0] n324_O; // @[Top.scala 998:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n327_valid_up; // @[Top.scala 1002:22]
  wire  n327_valid_down; // @[Top.scala 1002:22]
  wire [15:0] n327_I0; // @[Top.scala 1002:22]
  wire [15:0] n327_I1; // @[Top.scala 1002:22]
  wire [15:0] n327_O_t0b; // @[Top.scala 1002:22]
  wire [15:0] n327_O_t1b; // @[Top.scala 1002:22]
  wire  n328_valid_up; // @[Top.scala 1006:22]
  wire  n328_valid_down; // @[Top.scala 1006:22]
  wire [15:0] n328_I_t0b; // @[Top.scala 1006:22]
  wire [15:0] n328_I_t1b; // @[Top.scala 1006:22]
  wire [15:0] n328_O; // @[Top.scala 1006:22]
  wire  n329_valid_up; // @[Top.scala 1009:22]
  wire  n329_valid_down; // @[Top.scala 1009:22]
  wire [15:0] n329_I0; // @[Top.scala 1009:22]
  wire [15:0] n329_I1; // @[Top.scala 1009:22]
  wire [15:0] n329_O_t0b; // @[Top.scala 1009:22]
  wire [15:0] n329_O_t1b; // @[Top.scala 1009:22]
  wire  n330_valid_up; // @[Top.scala 1013:22]
  wire  n330_valid_down; // @[Top.scala 1013:22]
  wire  n330_I0; // @[Top.scala 1013:22]
  wire [15:0] n330_I1_t0b; // @[Top.scala 1013:22]
  wire [15:0] n330_I1_t1b; // @[Top.scala 1013:22]
  wire  n330_O_t0b; // @[Top.scala 1013:22]
  wire [15:0] n330_O_t1b_t0b; // @[Top.scala 1013:22]
  wire [15:0] n330_O_t1b_t1b; // @[Top.scala 1013:22]
  wire  n331_valid_up; // @[Top.scala 1017:22]
  wire  n331_valid_down; // @[Top.scala 1017:22]
  wire  n331_I_t0b; // @[Top.scala 1017:22]
  wire [15:0] n331_I_t1b_t0b; // @[Top.scala 1017:22]
  wire [15:0] n331_I_t1b_t1b; // @[Top.scala 1017:22]
  wire [15:0] n331_O; // @[Top.scala 1017:22]
  wire  n332_valid_up; // @[Top.scala 1020:22]
  wire  n332_valid_down; // @[Top.scala 1020:22]
  wire [15:0] n332_I0; // @[Top.scala 1020:22]
  wire [15:0] n332_I1; // @[Top.scala 1020:22]
  wire [15:0] n332_O_t0b; // @[Top.scala 1020:22]
  wire [15:0] n332_O_t1b; // @[Top.scala 1020:22]
  wire  n333_clock; // @[Top.scala 1024:22]
  wire  n333_reset; // @[Top.scala 1024:22]
  wire  n333_valid_up; // @[Top.scala 1024:22]
  wire  n333_valid_down; // @[Top.scala 1024:22]
  wire [15:0] n333_I_t0b; // @[Top.scala 1024:22]
  wire [15:0] n333_I_t1b; // @[Top.scala 1024:22]
  wire [15:0] n333_O; // @[Top.scala 1024:22]
  wire  n335_valid_up; // @[Top.scala 1027:22]
  wire  n335_valid_down; // @[Top.scala 1027:22]
  wire [15:0] n335_I0; // @[Top.scala 1027:22]
  wire [15:0] n335_I1; // @[Top.scala 1027:22]
  wire [15:0] n335_O_t0b; // @[Top.scala 1027:22]
  wire [15:0] n335_O_t1b; // @[Top.scala 1027:22]
  wire  n336_valid_up; // @[Top.scala 1031:22]
  wire  n336_valid_down; // @[Top.scala 1031:22]
  wire [15:0] n336_I_t0b; // @[Top.scala 1031:22]
  wire [15:0] n336_I_t1b; // @[Top.scala 1031:22]
  wire [15:0] n336_O; // @[Top.scala 1031:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n338_valid_up; // @[Top.scala 1035:22]
  wire  n338_valid_down; // @[Top.scala 1035:22]
  wire [15:0] n338_I0; // @[Top.scala 1035:22]
  wire [15:0] n338_I1; // @[Top.scala 1035:22]
  wire [15:0] n338_O_t0b; // @[Top.scala 1035:22]
  wire [15:0] n338_O_t1b; // @[Top.scala 1035:22]
  wire  n339_valid_up; // @[Top.scala 1039:22]
  wire  n339_valid_down; // @[Top.scala 1039:22]
  wire [15:0] n339_I_t0b; // @[Top.scala 1039:22]
  wire [15:0] n339_I_t1b; // @[Top.scala 1039:22]
  wire [15:0] n339_O; // @[Top.scala 1039:22]
  wire  n340_valid_up; // @[Top.scala 1042:22]
  wire  n340_valid_down; // @[Top.scala 1042:22]
  wire [15:0] n340_I0; // @[Top.scala 1042:22]
  wire [15:0] n340_I1; // @[Top.scala 1042:22]
  wire [15:0] n340_O_t0b; // @[Top.scala 1042:22]
  wire [15:0] n340_O_t1b; // @[Top.scala 1042:22]
  wire  n341_valid_up; // @[Top.scala 1046:22]
  wire  n341_valid_down; // @[Top.scala 1046:22]
  wire [15:0] n341_I0; // @[Top.scala 1046:22]
  wire [15:0] n341_I1; // @[Top.scala 1046:22]
  wire [15:0] n341_O_t0b; // @[Top.scala 1046:22]
  wire [15:0] n341_O_t1b; // @[Top.scala 1046:22]
  wire  n342_valid_up; // @[Top.scala 1050:22]
  wire  n342_valid_down; // @[Top.scala 1050:22]
  wire [15:0] n342_I0_t0b; // @[Top.scala 1050:22]
  wire [15:0] n342_I0_t1b; // @[Top.scala 1050:22]
  wire [15:0] n342_I1_t0b; // @[Top.scala 1050:22]
  wire [15:0] n342_I1_t1b; // @[Top.scala 1050:22]
  wire [15:0] n342_O_t0b_t0b; // @[Top.scala 1050:22]
  wire [15:0] n342_O_t0b_t1b; // @[Top.scala 1050:22]
  wire [15:0] n342_O_t1b_t0b; // @[Top.scala 1050:22]
  wire [15:0] n342_O_t1b_t1b; // @[Top.scala 1050:22]
  wire  n343_clock; // @[Top.scala 1054:22]
  wire  n343_reset; // @[Top.scala 1054:22]
  wire  n343_valid_up; // @[Top.scala 1054:22]
  wire  n343_valid_down; // @[Top.scala 1054:22]
  wire [15:0] n343_I_t0b_t0b; // @[Top.scala 1054:22]
  wire [15:0] n343_I_t0b_t1b; // @[Top.scala 1054:22]
  wire [15:0] n343_I_t1b_t0b; // @[Top.scala 1054:22]
  wire [15:0] n343_I_t1b_t1b; // @[Top.scala 1054:22]
  wire [15:0] n343_O_t0b_t0b; // @[Top.scala 1054:22]
  wire [15:0] n343_O_t0b_t1b; // @[Top.scala 1054:22]
  wire [15:0] n343_O_t1b_t0b; // @[Top.scala 1054:22]
  wire [15:0] n343_O_t1b_t1b; // @[Top.scala 1054:22]
  wire  n344_valid_up; // @[Top.scala 1057:22]
  wire  n344_valid_down; // @[Top.scala 1057:22]
  wire  n344_I0; // @[Top.scala 1057:22]
  wire [15:0] n344_I1_t0b_t0b; // @[Top.scala 1057:22]
  wire [15:0] n344_I1_t0b_t1b; // @[Top.scala 1057:22]
  wire [15:0] n344_I1_t1b_t0b; // @[Top.scala 1057:22]
  wire [15:0] n344_I1_t1b_t1b; // @[Top.scala 1057:22]
  wire  n344_O_t0b; // @[Top.scala 1057:22]
  wire [15:0] n344_O_t1b_t0b_t0b; // @[Top.scala 1057:22]
  wire [15:0] n344_O_t1b_t0b_t1b; // @[Top.scala 1057:22]
  wire [15:0] n344_O_t1b_t1b_t0b; // @[Top.scala 1057:22]
  wire [15:0] n344_O_t1b_t1b_t1b; // @[Top.scala 1057:22]
  wire  n345_valid_up; // @[Top.scala 1061:22]
  wire  n345_valid_down; // @[Top.scala 1061:22]
  wire  n345_I_t0b; // @[Top.scala 1061:22]
  wire [15:0] n345_I_t1b_t0b_t0b; // @[Top.scala 1061:22]
  wire [15:0] n345_I_t1b_t0b_t1b; // @[Top.scala 1061:22]
  wire [15:0] n345_I_t1b_t1b_t0b; // @[Top.scala 1061:22]
  wire [15:0] n345_I_t1b_t1b_t1b; // @[Top.scala 1061:22]
  wire [15:0] n345_O_t0b; // @[Top.scala 1061:22]
  wire [15:0] n345_O_t1b; // @[Top.scala 1061:22]
  wire  n347_valid_up; // @[Top.scala 1064:22]
  wire  n347_valid_down; // @[Top.scala 1064:22]
  wire [15:0] n347_I0; // @[Top.scala 1064:22]
  wire [15:0] n347_I1_t0b; // @[Top.scala 1064:22]
  wire [15:0] n347_I1_t1b; // @[Top.scala 1064:22]
  wire [15:0] n347_O_t0b; // @[Top.scala 1064:22]
  wire [15:0] n347_O_t1b_t0b; // @[Top.scala 1064:22]
  wire [15:0] n347_O_t1b_t1b; // @[Top.scala 1064:22]
  Fst n313 ( // @[Top.scala 961:22]
    .valid_up(n313_valid_up),
    .valid_down(n313_valid_down),
    .I_t0b(n313_I_t0b),
    .O(n313_O)
  );
  FIFO_1 n346 ( // @[Top.scala 964:22]
    .clock(n346_clock),
    .reset(n346_reset),
    .valid_up(n346_valid_up),
    .valid_down(n346_valid_down),
    .I(n346_I),
    .O(n346_O)
  );
  FIFO_1 n334 ( // @[Top.scala 967:22]
    .clock(n334_clock),
    .reset(n334_reset),
    .valid_up(n334_valid_up),
    .valid_down(n334_valid_down),
    .I(n334_I),
    .O(n334_O)
  );
  Snd n314 ( // @[Top.scala 970:22]
    .valid_up(n314_valid_up),
    .valid_down(n314_valid_down),
    .I_t1b_t0b(n314_I_t1b_t0b),
    .I_t1b_t1b(n314_I_t1b_t1b),
    .O_t0b(n314_O_t0b),
    .O_t1b(n314_O_t1b)
  );
  Fst_1 n315 ( // @[Top.scala 973:22]
    .valid_up(n315_valid_up),
    .valid_down(n315_valid_down),
    .I_t0b(n315_I_t0b),
    .O(n315_O)
  );
  Snd_1 n316 ( // @[Top.scala 976:22]
    .valid_up(n316_valid_up),
    .valid_down(n316_valid_down),
    .I_t1b(n316_I_t1b),
    .O(n316_O)
  );
  AtomTuple_1 n317 ( // @[Top.scala 979:22]
    .valid_up(n317_valid_up),
    .valid_down(n317_valid_down),
    .I0(n317_I0),
    .I1(n317_I1),
    .O_t0b(n317_O_t0b),
    .O_t1b(n317_O_t1b)
  );
  Add n318 ( // @[Top.scala 983:22]
    .valid_up(n318_valid_up),
    .valid_down(n318_valid_down),
    .I_t0b(n318_I_t0b),
    .I_t1b(n318_I_t1b),
    .O(n318_O)
  );
  InitialDelayCounter_27 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n321 ( // @[Top.scala 987:22]
    .valid_up(n321_valid_up),
    .valid_down(n321_valid_down),
    .I0(n321_I0),
    .O_t0b(n321_O_t0b)
  );
  RShift n322 ( // @[Top.scala 991:22]
    .valid_up(n322_valid_up),
    .valid_down(n322_valid_down),
    .I_t0b(n322_I_t0b),
    .O(n322_O)
  );
  AtomTuple_1 n323 ( // @[Top.scala 994:22]
    .valid_up(n323_valid_up),
    .valid_down(n323_valid_down),
    .I0(n323_I0),
    .I1(n323_I1),
    .O_t0b(n323_O_t0b),
    .O_t1b(n323_O_t1b)
  );
  Eq n324 ( // @[Top.scala 998:22]
    .valid_up(n324_valid_up),
    .valid_down(n324_valid_down),
    .I_t0b(n324_I_t0b),
    .I_t1b(n324_I_t1b),
    .O(n324_O)
  );
  InitialDelayCounter_27 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n327 ( // @[Top.scala 1002:22]
    .valid_up(n327_valid_up),
    .valid_down(n327_valid_down),
    .I0(n327_I0),
    .I1(n327_I1),
    .O_t0b(n327_O_t0b),
    .O_t1b(n327_O_t1b)
  );
  Add n328 ( // @[Top.scala 1006:22]
    .valid_up(n328_valid_up),
    .valid_down(n328_valid_down),
    .I_t0b(n328_I_t0b),
    .I_t1b(n328_I_t1b),
    .O(n328_O)
  );
  AtomTuple_1 n329 ( // @[Top.scala 1009:22]
    .valid_up(n329_valid_up),
    .valid_down(n329_valid_down),
    .I0(n329_I0),
    .I1(n329_I1),
    .O_t0b(n329_O_t0b),
    .O_t1b(n329_O_t1b)
  );
  AtomTuple_9 n330 ( // @[Top.scala 1013:22]
    .valid_up(n330_valid_up),
    .valid_down(n330_valid_down),
    .I0(n330_I0),
    .I1_t0b(n330_I1_t0b),
    .I1_t1b(n330_I1_t1b),
    .O_t0b(n330_O_t0b),
    .O_t1b_t0b(n330_O_t1b_t0b),
    .O_t1b_t1b(n330_O_t1b_t1b)
  );
  If n331 ( // @[Top.scala 1017:22]
    .valid_up(n331_valid_up),
    .valid_down(n331_valid_down),
    .I_t0b(n331_I_t0b),
    .I_t1b_t0b(n331_I_t1b_t0b),
    .I_t1b_t1b(n331_I_t1b_t1b),
    .O(n331_O)
  );
  AtomTuple_1 n332 ( // @[Top.scala 1020:22]
    .valid_up(n332_valid_up),
    .valid_down(n332_valid_down),
    .I0(n332_I0),
    .I1(n332_I1),
    .O_t0b(n332_O_t0b),
    .O_t1b(n332_O_t1b)
  );
  Mul n333 ( // @[Top.scala 1024:22]
    .clock(n333_clock),
    .reset(n333_reset),
    .valid_up(n333_valid_up),
    .valid_down(n333_valid_down),
    .I_t0b(n333_I_t0b),
    .I_t1b(n333_I_t1b),
    .O(n333_O)
  );
  AtomTuple_1 n335 ( // @[Top.scala 1027:22]
    .valid_up(n335_valid_up),
    .valid_down(n335_valid_down),
    .I0(n335_I0),
    .I1(n335_I1),
    .O_t0b(n335_O_t0b),
    .O_t1b(n335_O_t1b)
  );
  Lt n336 ( // @[Top.scala 1031:22]
    .valid_up(n336_valid_up),
    .valid_down(n336_valid_down),
    .I_t0b(n336_I_t0b),
    .I_t1b(n336_I_t1b),
    .O(n336_O)
  );
  InitialDelayCounter_27 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n338 ( // @[Top.scala 1035:22]
    .valid_up(n338_valid_up),
    .valid_down(n338_valid_down),
    .I0(n338_I0),
    .I1(n338_I1),
    .O_t0b(n338_O_t0b),
    .O_t1b(n338_O_t1b)
  );
  Sub n339 ( // @[Top.scala 1039:22]
    .valid_up(n339_valid_up),
    .valid_down(n339_valid_down),
    .I_t0b(n339_I_t0b),
    .I_t1b(n339_I_t1b),
    .O(n339_O)
  );
  AtomTuple_1 n340 ( // @[Top.scala 1042:22]
    .valid_up(n340_valid_up),
    .valid_down(n340_valid_down),
    .I0(n340_I0),
    .I1(n340_I1),
    .O_t0b(n340_O_t0b),
    .O_t1b(n340_O_t1b)
  );
  AtomTuple_1 n341 ( // @[Top.scala 1046:22]
    .valid_up(n341_valid_up),
    .valid_down(n341_valid_down),
    .I0(n341_I0),
    .I1(n341_I1),
    .O_t0b(n341_O_t0b),
    .O_t1b(n341_O_t1b)
  );
  AtomTuple_15 n342 ( // @[Top.scala 1050:22]
    .valid_up(n342_valid_up),
    .valid_down(n342_valid_down),
    .I0_t0b(n342_I0_t0b),
    .I0_t1b(n342_I0_t1b),
    .I1_t0b(n342_I1_t0b),
    .I1_t1b(n342_I1_t1b),
    .O_t0b_t0b(n342_O_t0b_t0b),
    .O_t0b_t1b(n342_O_t0b_t1b),
    .O_t1b_t0b(n342_O_t1b_t0b),
    .O_t1b_t1b(n342_O_t1b_t1b)
  );
  FIFO_3 n343 ( // @[Top.scala 1054:22]
    .clock(n343_clock),
    .reset(n343_reset),
    .valid_up(n343_valid_up),
    .valid_down(n343_valid_down),
    .I_t0b_t0b(n343_I_t0b_t0b),
    .I_t0b_t1b(n343_I_t0b_t1b),
    .I_t1b_t0b(n343_I_t1b_t0b),
    .I_t1b_t1b(n343_I_t1b_t1b),
    .O_t0b_t0b(n343_O_t0b_t0b),
    .O_t0b_t1b(n343_O_t0b_t1b),
    .O_t1b_t0b(n343_O_t1b_t0b),
    .O_t1b_t1b(n343_O_t1b_t1b)
  );
  AtomTuple_16 n344 ( // @[Top.scala 1057:22]
    .valid_up(n344_valid_up),
    .valid_down(n344_valid_down),
    .I0(n344_I0),
    .I1_t0b_t0b(n344_I1_t0b_t0b),
    .I1_t0b_t1b(n344_I1_t0b_t1b),
    .I1_t1b_t0b(n344_I1_t1b_t0b),
    .I1_t1b_t1b(n344_I1_t1b_t1b),
    .O_t0b(n344_O_t0b),
    .O_t1b_t0b_t0b(n344_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n344_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n344_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n344_O_t1b_t1b_t1b)
  );
  If_1 n345 ( // @[Top.scala 1061:22]
    .valid_up(n345_valid_up),
    .valid_down(n345_valid_down),
    .I_t0b(n345_I_t0b),
    .I_t1b_t0b_t0b(n345_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n345_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n345_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n345_I_t1b_t1b_t1b),
    .O_t0b(n345_O_t0b),
    .O_t1b(n345_O_t1b)
  );
  AtomTuple_3 n347 ( // @[Top.scala 1064:22]
    .valid_up(n347_valid_up),
    .valid_down(n347_valid_down),
    .I0(n347_I0),
    .I1_t0b(n347_I1_t0b),
    .I1_t1b(n347_I1_t1b),
    .O_t0b(n347_O_t0b),
    .O_t1b_t0b(n347_O_t1b_t0b),
    .O_t1b_t1b(n347_O_t1b_t1b)
  );
  assign valid_down = n347_valid_down; // @[Top.scala 1069:16]
  assign O_t0b = n347_O_t0b; // @[Top.scala 1068:7]
  assign O_t1b_t0b = n347_O_t1b_t0b; // @[Top.scala 1068:7]
  assign O_t1b_t1b = n347_O_t1b_t1b; // @[Top.scala 1068:7]
  assign n313_valid_up = valid_up; // @[Top.scala 963:19]
  assign n313_I_t0b = I_t0b; // @[Top.scala 962:12]
  assign n346_clock = clock;
  assign n346_reset = reset;
  assign n346_valid_up = n313_valid_down; // @[Top.scala 966:19]
  assign n346_I = n313_O; // @[Top.scala 965:12]
  assign n334_clock = clock;
  assign n334_reset = reset;
  assign n334_valid_up = n313_valid_down; // @[Top.scala 969:19]
  assign n334_I = n313_O; // @[Top.scala 968:12]
  assign n314_valid_up = valid_up; // @[Top.scala 972:19]
  assign n314_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 971:12]
  assign n314_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 971:12]
  assign n315_valid_up = n314_valid_down; // @[Top.scala 975:19]
  assign n315_I_t0b = n314_O_t0b; // @[Top.scala 974:12]
  assign n316_valid_up = n314_valid_down; // @[Top.scala 978:19]
  assign n316_I_t1b = n314_O_t1b; // @[Top.scala 977:12]
  assign n317_valid_up = n315_valid_down & n316_valid_down; // @[Top.scala 982:19]
  assign n317_I0 = n315_O; // @[Top.scala 980:13]
  assign n317_I1 = n316_O; // @[Top.scala 981:13]
  assign n318_valid_up = n317_valid_down; // @[Top.scala 985:19]
  assign n318_I_t0b = n317_O_t0b; // @[Top.scala 984:12]
  assign n318_I_t1b = n317_O_t1b; // @[Top.scala 984:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n321_valid_up = n318_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 990:19]
  assign n321_I0 = n318_O; // @[Top.scala 988:13]
  assign n322_valid_up = n321_valid_down; // @[Top.scala 993:19]
  assign n322_I_t0b = n321_O_t0b; // @[Top.scala 992:12]
  assign n323_valid_up = n322_valid_down & n315_valid_down; // @[Top.scala 997:19]
  assign n323_I0 = n322_O; // @[Top.scala 995:13]
  assign n323_I1 = n315_O; // @[Top.scala 996:13]
  assign n324_valid_up = n323_valid_down; // @[Top.scala 1000:19]
  assign n324_I_t0b = n323_O_t0b; // @[Top.scala 999:12]
  assign n324_I_t1b = n323_O_t1b; // @[Top.scala 999:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n327_valid_up = n322_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1005:19]
  assign n327_I0 = n322_O; // @[Top.scala 1003:13]
  assign n327_I1 = 16'h1; // @[Top.scala 1004:13]
  assign n328_valid_up = n327_valid_down; // @[Top.scala 1008:19]
  assign n328_I_t0b = n327_O_t0b; // @[Top.scala 1007:12]
  assign n328_I_t1b = n327_O_t1b; // @[Top.scala 1007:12]
  assign n329_valid_up = n328_valid_down & n322_valid_down; // @[Top.scala 1012:19]
  assign n329_I0 = n328_O; // @[Top.scala 1010:13]
  assign n329_I1 = n322_O; // @[Top.scala 1011:13]
  assign n330_valid_up = n324_valid_down & n329_valid_down; // @[Top.scala 1016:19]
  assign n330_I0 = n324_O[0]; // @[Top.scala 1014:13]
  assign n330_I1_t0b = n329_O_t0b; // @[Top.scala 1015:13]
  assign n330_I1_t1b = n329_O_t1b; // @[Top.scala 1015:13]
  assign n331_valid_up = n330_valid_down; // @[Top.scala 1019:19]
  assign n331_I_t0b = n330_O_t0b; // @[Top.scala 1018:12]
  assign n331_I_t1b_t0b = n330_O_t1b_t0b; // @[Top.scala 1018:12]
  assign n331_I_t1b_t1b = n330_O_t1b_t1b; // @[Top.scala 1018:12]
  assign n332_valid_up = n331_valid_down; // @[Top.scala 1023:19]
  assign n332_I0 = n331_O; // @[Top.scala 1021:13]
  assign n332_I1 = n331_O; // @[Top.scala 1022:13]
  assign n333_clock = clock;
  assign n333_reset = reset;
  assign n333_valid_up = n332_valid_down; // @[Top.scala 1026:19]
  assign n333_I_t0b = n332_O_t0b; // @[Top.scala 1025:12]
  assign n333_I_t1b = n332_O_t1b; // @[Top.scala 1025:12]
  assign n335_valid_up = n334_valid_down & n333_valid_down; // @[Top.scala 1030:19]
  assign n335_I0 = n334_O; // @[Top.scala 1028:13]
  assign n335_I1 = n333_O; // @[Top.scala 1029:13]
  assign n336_valid_up = n335_valid_down; // @[Top.scala 1033:19]
  assign n336_I_t0b = n335_O_t0b; // @[Top.scala 1032:12]
  assign n336_I_t1b = n335_O_t1b; // @[Top.scala 1032:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n338_valid_up = n331_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1038:19]
  assign n338_I0 = n331_O; // @[Top.scala 1036:13]
  assign n338_I1 = 16'h1; // @[Top.scala 1037:13]
  assign n339_valid_up = n338_valid_down; // @[Top.scala 1041:19]
  assign n339_I_t0b = n338_O_t0b; // @[Top.scala 1040:12]
  assign n339_I_t1b = n338_O_t1b; // @[Top.scala 1040:12]
  assign n340_valid_up = n315_valid_down & n339_valid_down; // @[Top.scala 1045:19]
  assign n340_I0 = n315_O; // @[Top.scala 1043:13]
  assign n340_I1 = n339_O; // @[Top.scala 1044:13]
  assign n341_valid_up = n331_valid_down & n316_valid_down; // @[Top.scala 1049:19]
  assign n341_I0 = n331_O; // @[Top.scala 1047:13]
  assign n341_I1 = n316_O; // @[Top.scala 1048:13]
  assign n342_valid_up = n340_valid_down & n341_valid_down; // @[Top.scala 1053:19]
  assign n342_I0_t0b = n340_O_t0b; // @[Top.scala 1051:13]
  assign n342_I0_t1b = n340_O_t1b; // @[Top.scala 1051:13]
  assign n342_I1_t0b = n341_O_t0b; // @[Top.scala 1052:13]
  assign n342_I1_t1b = n341_O_t1b; // @[Top.scala 1052:13]
  assign n343_clock = clock;
  assign n343_reset = reset;
  assign n343_valid_up = n342_valid_down; // @[Top.scala 1056:19]
  assign n343_I_t0b_t0b = n342_O_t0b_t0b; // @[Top.scala 1055:12]
  assign n343_I_t0b_t1b = n342_O_t0b_t1b; // @[Top.scala 1055:12]
  assign n343_I_t1b_t0b = n342_O_t1b_t0b; // @[Top.scala 1055:12]
  assign n343_I_t1b_t1b = n342_O_t1b_t1b; // @[Top.scala 1055:12]
  assign n344_valid_up = n336_valid_down & n343_valid_down; // @[Top.scala 1060:19]
  assign n344_I0 = n336_O[0]; // @[Top.scala 1058:13]
  assign n344_I1_t0b_t0b = n343_O_t0b_t0b; // @[Top.scala 1059:13]
  assign n344_I1_t0b_t1b = n343_O_t0b_t1b; // @[Top.scala 1059:13]
  assign n344_I1_t1b_t0b = n343_O_t1b_t0b; // @[Top.scala 1059:13]
  assign n344_I1_t1b_t1b = n343_O_t1b_t1b; // @[Top.scala 1059:13]
  assign n345_valid_up = n344_valid_down; // @[Top.scala 1063:19]
  assign n345_I_t0b = n344_O_t0b; // @[Top.scala 1062:12]
  assign n345_I_t1b_t0b_t0b = n344_O_t1b_t0b_t0b; // @[Top.scala 1062:12]
  assign n345_I_t1b_t0b_t1b = n344_O_t1b_t0b_t1b; // @[Top.scala 1062:12]
  assign n345_I_t1b_t1b_t0b = n344_O_t1b_t1b_t0b; // @[Top.scala 1062:12]
  assign n345_I_t1b_t1b_t1b = n344_O_t1b_t1b_t1b; // @[Top.scala 1062:12]
  assign n347_valid_up = n346_valid_down & n345_valid_down; // @[Top.scala 1067:19]
  assign n347_I0 = n346_O; // @[Top.scala 1065:13]
  assign n347_I1_t0b = n345_O_t0b; // @[Top.scala 1066:13]
  assign n347_I1_t1b = n345_O_t1b; // @[Top.scala 1066:13]
endmodule
module MapT_9(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_9 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
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
  wire  n350_valid_up; // @[Top.scala 1075:22]
  wire  n350_valid_down; // @[Top.scala 1075:22]
  wire [15:0] n350_I_t0b; // @[Top.scala 1075:22]
  wire [15:0] n350_O; // @[Top.scala 1075:22]
  wire  n383_clock; // @[Top.scala 1078:22]
  wire  n383_reset; // @[Top.scala 1078:22]
  wire  n383_valid_up; // @[Top.scala 1078:22]
  wire  n383_valid_down; // @[Top.scala 1078:22]
  wire [15:0] n383_I; // @[Top.scala 1078:22]
  wire [15:0] n383_O; // @[Top.scala 1078:22]
  wire  n371_clock; // @[Top.scala 1081:22]
  wire  n371_reset; // @[Top.scala 1081:22]
  wire  n371_valid_up; // @[Top.scala 1081:22]
  wire  n371_valid_down; // @[Top.scala 1081:22]
  wire [15:0] n371_I; // @[Top.scala 1081:22]
  wire [15:0] n371_O; // @[Top.scala 1081:22]
  wire  n351_valid_up; // @[Top.scala 1084:22]
  wire  n351_valid_down; // @[Top.scala 1084:22]
  wire [15:0] n351_I_t1b_t0b; // @[Top.scala 1084:22]
  wire [15:0] n351_I_t1b_t1b; // @[Top.scala 1084:22]
  wire [15:0] n351_O_t0b; // @[Top.scala 1084:22]
  wire [15:0] n351_O_t1b; // @[Top.scala 1084:22]
  wire  n352_valid_up; // @[Top.scala 1087:22]
  wire  n352_valid_down; // @[Top.scala 1087:22]
  wire [15:0] n352_I_t0b; // @[Top.scala 1087:22]
  wire [15:0] n352_O; // @[Top.scala 1087:22]
  wire  n353_valid_up; // @[Top.scala 1090:22]
  wire  n353_valid_down; // @[Top.scala 1090:22]
  wire [15:0] n353_I_t1b; // @[Top.scala 1090:22]
  wire [15:0] n353_O; // @[Top.scala 1090:22]
  wire  n354_valid_up; // @[Top.scala 1093:22]
  wire  n354_valid_down; // @[Top.scala 1093:22]
  wire [15:0] n354_I0; // @[Top.scala 1093:22]
  wire [15:0] n354_I1; // @[Top.scala 1093:22]
  wire [15:0] n354_O_t0b; // @[Top.scala 1093:22]
  wire [15:0] n354_O_t1b; // @[Top.scala 1093:22]
  wire  n355_valid_up; // @[Top.scala 1097:22]
  wire  n355_valid_down; // @[Top.scala 1097:22]
  wire [15:0] n355_I_t0b; // @[Top.scala 1097:22]
  wire [15:0] n355_I_t1b; // @[Top.scala 1097:22]
  wire [15:0] n355_O; // @[Top.scala 1097:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n358_valid_up; // @[Top.scala 1101:22]
  wire  n358_valid_down; // @[Top.scala 1101:22]
  wire [15:0] n358_I0; // @[Top.scala 1101:22]
  wire [15:0] n358_O_t0b; // @[Top.scala 1101:22]
  wire  n359_valid_up; // @[Top.scala 1105:22]
  wire  n359_valid_down; // @[Top.scala 1105:22]
  wire [15:0] n359_I_t0b; // @[Top.scala 1105:22]
  wire [15:0] n359_O; // @[Top.scala 1105:22]
  wire  n360_valid_up; // @[Top.scala 1108:22]
  wire  n360_valid_down; // @[Top.scala 1108:22]
  wire [15:0] n360_I0; // @[Top.scala 1108:22]
  wire [15:0] n360_I1; // @[Top.scala 1108:22]
  wire [15:0] n360_O_t0b; // @[Top.scala 1108:22]
  wire [15:0] n360_O_t1b; // @[Top.scala 1108:22]
  wire  n361_valid_up; // @[Top.scala 1112:22]
  wire  n361_valid_down; // @[Top.scala 1112:22]
  wire [15:0] n361_I_t0b; // @[Top.scala 1112:22]
  wire [15:0] n361_I_t1b; // @[Top.scala 1112:22]
  wire [15:0] n361_O; // @[Top.scala 1112:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n364_valid_up; // @[Top.scala 1116:22]
  wire  n364_valid_down; // @[Top.scala 1116:22]
  wire [15:0] n364_I0; // @[Top.scala 1116:22]
  wire [15:0] n364_I1; // @[Top.scala 1116:22]
  wire [15:0] n364_O_t0b; // @[Top.scala 1116:22]
  wire [15:0] n364_O_t1b; // @[Top.scala 1116:22]
  wire  n365_valid_up; // @[Top.scala 1120:22]
  wire  n365_valid_down; // @[Top.scala 1120:22]
  wire [15:0] n365_I_t0b; // @[Top.scala 1120:22]
  wire [15:0] n365_I_t1b; // @[Top.scala 1120:22]
  wire [15:0] n365_O; // @[Top.scala 1120:22]
  wire  n366_valid_up; // @[Top.scala 1123:22]
  wire  n366_valid_down; // @[Top.scala 1123:22]
  wire [15:0] n366_I0; // @[Top.scala 1123:22]
  wire [15:0] n366_I1; // @[Top.scala 1123:22]
  wire [15:0] n366_O_t0b; // @[Top.scala 1123:22]
  wire [15:0] n366_O_t1b; // @[Top.scala 1123:22]
  wire  n367_valid_up; // @[Top.scala 1127:22]
  wire  n367_valid_down; // @[Top.scala 1127:22]
  wire  n367_I0; // @[Top.scala 1127:22]
  wire [15:0] n367_I1_t0b; // @[Top.scala 1127:22]
  wire [15:0] n367_I1_t1b; // @[Top.scala 1127:22]
  wire  n367_O_t0b; // @[Top.scala 1127:22]
  wire [15:0] n367_O_t1b_t0b; // @[Top.scala 1127:22]
  wire [15:0] n367_O_t1b_t1b; // @[Top.scala 1127:22]
  wire  n368_valid_up; // @[Top.scala 1131:22]
  wire  n368_valid_down; // @[Top.scala 1131:22]
  wire  n368_I_t0b; // @[Top.scala 1131:22]
  wire [15:0] n368_I_t1b_t0b; // @[Top.scala 1131:22]
  wire [15:0] n368_I_t1b_t1b; // @[Top.scala 1131:22]
  wire [15:0] n368_O; // @[Top.scala 1131:22]
  wire  n369_valid_up; // @[Top.scala 1134:22]
  wire  n369_valid_down; // @[Top.scala 1134:22]
  wire [15:0] n369_I0; // @[Top.scala 1134:22]
  wire [15:0] n369_I1; // @[Top.scala 1134:22]
  wire [15:0] n369_O_t0b; // @[Top.scala 1134:22]
  wire [15:0] n369_O_t1b; // @[Top.scala 1134:22]
  wire  n370_clock; // @[Top.scala 1138:22]
  wire  n370_reset; // @[Top.scala 1138:22]
  wire  n370_valid_up; // @[Top.scala 1138:22]
  wire  n370_valid_down; // @[Top.scala 1138:22]
  wire [15:0] n370_I_t0b; // @[Top.scala 1138:22]
  wire [15:0] n370_I_t1b; // @[Top.scala 1138:22]
  wire [15:0] n370_O; // @[Top.scala 1138:22]
  wire  n372_valid_up; // @[Top.scala 1141:22]
  wire  n372_valid_down; // @[Top.scala 1141:22]
  wire [15:0] n372_I0; // @[Top.scala 1141:22]
  wire [15:0] n372_I1; // @[Top.scala 1141:22]
  wire [15:0] n372_O_t0b; // @[Top.scala 1141:22]
  wire [15:0] n372_O_t1b; // @[Top.scala 1141:22]
  wire  n373_valid_up; // @[Top.scala 1145:22]
  wire  n373_valid_down; // @[Top.scala 1145:22]
  wire [15:0] n373_I_t0b; // @[Top.scala 1145:22]
  wire [15:0] n373_I_t1b; // @[Top.scala 1145:22]
  wire [15:0] n373_O; // @[Top.scala 1145:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n375_valid_up; // @[Top.scala 1149:22]
  wire  n375_valid_down; // @[Top.scala 1149:22]
  wire [15:0] n375_I0; // @[Top.scala 1149:22]
  wire [15:0] n375_I1; // @[Top.scala 1149:22]
  wire [15:0] n375_O_t0b; // @[Top.scala 1149:22]
  wire [15:0] n375_O_t1b; // @[Top.scala 1149:22]
  wire  n376_valid_up; // @[Top.scala 1153:22]
  wire  n376_valid_down; // @[Top.scala 1153:22]
  wire [15:0] n376_I_t0b; // @[Top.scala 1153:22]
  wire [15:0] n376_I_t1b; // @[Top.scala 1153:22]
  wire [15:0] n376_O; // @[Top.scala 1153:22]
  wire  n377_valid_up; // @[Top.scala 1156:22]
  wire  n377_valid_down; // @[Top.scala 1156:22]
  wire [15:0] n377_I0; // @[Top.scala 1156:22]
  wire [15:0] n377_I1; // @[Top.scala 1156:22]
  wire [15:0] n377_O_t0b; // @[Top.scala 1156:22]
  wire [15:0] n377_O_t1b; // @[Top.scala 1156:22]
  wire  n378_valid_up; // @[Top.scala 1160:22]
  wire  n378_valid_down; // @[Top.scala 1160:22]
  wire [15:0] n378_I0; // @[Top.scala 1160:22]
  wire [15:0] n378_I1; // @[Top.scala 1160:22]
  wire [15:0] n378_O_t0b; // @[Top.scala 1160:22]
  wire [15:0] n378_O_t1b; // @[Top.scala 1160:22]
  wire  n379_valid_up; // @[Top.scala 1164:22]
  wire  n379_valid_down; // @[Top.scala 1164:22]
  wire [15:0] n379_I0_t0b; // @[Top.scala 1164:22]
  wire [15:0] n379_I0_t1b; // @[Top.scala 1164:22]
  wire [15:0] n379_I1_t0b; // @[Top.scala 1164:22]
  wire [15:0] n379_I1_t1b; // @[Top.scala 1164:22]
  wire [15:0] n379_O_t0b_t0b; // @[Top.scala 1164:22]
  wire [15:0] n379_O_t0b_t1b; // @[Top.scala 1164:22]
  wire [15:0] n379_O_t1b_t0b; // @[Top.scala 1164:22]
  wire [15:0] n379_O_t1b_t1b; // @[Top.scala 1164:22]
  wire  n380_clock; // @[Top.scala 1168:22]
  wire  n380_reset; // @[Top.scala 1168:22]
  wire  n380_valid_up; // @[Top.scala 1168:22]
  wire  n380_valid_down; // @[Top.scala 1168:22]
  wire [15:0] n380_I_t0b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n380_I_t0b_t1b; // @[Top.scala 1168:22]
  wire [15:0] n380_I_t1b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n380_I_t1b_t1b; // @[Top.scala 1168:22]
  wire [15:0] n380_O_t0b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n380_O_t0b_t1b; // @[Top.scala 1168:22]
  wire [15:0] n380_O_t1b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n380_O_t1b_t1b; // @[Top.scala 1168:22]
  wire  n381_valid_up; // @[Top.scala 1171:22]
  wire  n381_valid_down; // @[Top.scala 1171:22]
  wire  n381_I0; // @[Top.scala 1171:22]
  wire [15:0] n381_I1_t0b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n381_I1_t0b_t1b; // @[Top.scala 1171:22]
  wire [15:0] n381_I1_t1b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n381_I1_t1b_t1b; // @[Top.scala 1171:22]
  wire  n381_O_t0b; // @[Top.scala 1171:22]
  wire [15:0] n381_O_t1b_t0b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n381_O_t1b_t0b_t1b; // @[Top.scala 1171:22]
  wire [15:0] n381_O_t1b_t1b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n381_O_t1b_t1b_t1b; // @[Top.scala 1171:22]
  wire  n382_valid_up; // @[Top.scala 1175:22]
  wire  n382_valid_down; // @[Top.scala 1175:22]
  wire  n382_I_t0b; // @[Top.scala 1175:22]
  wire [15:0] n382_I_t1b_t0b_t0b; // @[Top.scala 1175:22]
  wire [15:0] n382_I_t1b_t0b_t1b; // @[Top.scala 1175:22]
  wire [15:0] n382_I_t1b_t1b_t0b; // @[Top.scala 1175:22]
  wire [15:0] n382_I_t1b_t1b_t1b; // @[Top.scala 1175:22]
  wire [15:0] n382_O_t0b; // @[Top.scala 1175:22]
  wire [15:0] n382_O_t1b; // @[Top.scala 1175:22]
  wire  n384_valid_up; // @[Top.scala 1178:22]
  wire  n384_valid_down; // @[Top.scala 1178:22]
  wire [15:0] n384_I0; // @[Top.scala 1178:22]
  wire [15:0] n384_I1_t0b; // @[Top.scala 1178:22]
  wire [15:0] n384_I1_t1b; // @[Top.scala 1178:22]
  wire [15:0] n384_O_t0b; // @[Top.scala 1178:22]
  wire [15:0] n384_O_t1b_t0b; // @[Top.scala 1178:22]
  wire [15:0] n384_O_t1b_t1b; // @[Top.scala 1178:22]
  Fst n350 ( // @[Top.scala 1075:22]
    .valid_up(n350_valid_up),
    .valid_down(n350_valid_down),
    .I_t0b(n350_I_t0b),
    .O(n350_O)
  );
  FIFO_1 n383 ( // @[Top.scala 1078:22]
    .clock(n383_clock),
    .reset(n383_reset),
    .valid_up(n383_valid_up),
    .valid_down(n383_valid_down),
    .I(n383_I),
    .O(n383_O)
  );
  FIFO_1 n371 ( // @[Top.scala 1081:22]
    .clock(n371_clock),
    .reset(n371_reset),
    .valid_up(n371_valid_up),
    .valid_down(n371_valid_down),
    .I(n371_I),
    .O(n371_O)
  );
  Snd n351 ( // @[Top.scala 1084:22]
    .valid_up(n351_valid_up),
    .valid_down(n351_valid_down),
    .I_t1b_t0b(n351_I_t1b_t0b),
    .I_t1b_t1b(n351_I_t1b_t1b),
    .O_t0b(n351_O_t0b),
    .O_t1b(n351_O_t1b)
  );
  Fst_1 n352 ( // @[Top.scala 1087:22]
    .valid_up(n352_valid_up),
    .valid_down(n352_valid_down),
    .I_t0b(n352_I_t0b),
    .O(n352_O)
  );
  Snd_1 n353 ( // @[Top.scala 1090:22]
    .valid_up(n353_valid_up),
    .valid_down(n353_valid_down),
    .I_t1b(n353_I_t1b),
    .O(n353_O)
  );
  AtomTuple_1 n354 ( // @[Top.scala 1093:22]
    .valid_up(n354_valid_up),
    .valid_down(n354_valid_down),
    .I0(n354_I0),
    .I1(n354_I1),
    .O_t0b(n354_O_t0b),
    .O_t1b(n354_O_t1b)
  );
  Add n355 ( // @[Top.scala 1097:22]
    .valid_up(n355_valid_up),
    .valid_down(n355_valid_down),
    .I_t0b(n355_I_t0b),
    .I_t1b(n355_I_t1b),
    .O(n355_O)
  );
  InitialDelayCounter_30 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n358 ( // @[Top.scala 1101:22]
    .valid_up(n358_valid_up),
    .valid_down(n358_valid_down),
    .I0(n358_I0),
    .O_t0b(n358_O_t0b)
  );
  RShift n359 ( // @[Top.scala 1105:22]
    .valid_up(n359_valid_up),
    .valid_down(n359_valid_down),
    .I_t0b(n359_I_t0b),
    .O(n359_O)
  );
  AtomTuple_1 n360 ( // @[Top.scala 1108:22]
    .valid_up(n360_valid_up),
    .valid_down(n360_valid_down),
    .I0(n360_I0),
    .I1(n360_I1),
    .O_t0b(n360_O_t0b),
    .O_t1b(n360_O_t1b)
  );
  Eq n361 ( // @[Top.scala 1112:22]
    .valid_up(n361_valid_up),
    .valid_down(n361_valid_down),
    .I_t0b(n361_I_t0b),
    .I_t1b(n361_I_t1b),
    .O(n361_O)
  );
  InitialDelayCounter_30 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n364 ( // @[Top.scala 1116:22]
    .valid_up(n364_valid_up),
    .valid_down(n364_valid_down),
    .I0(n364_I0),
    .I1(n364_I1),
    .O_t0b(n364_O_t0b),
    .O_t1b(n364_O_t1b)
  );
  Add n365 ( // @[Top.scala 1120:22]
    .valid_up(n365_valid_up),
    .valid_down(n365_valid_down),
    .I_t0b(n365_I_t0b),
    .I_t1b(n365_I_t1b),
    .O(n365_O)
  );
  AtomTuple_1 n366 ( // @[Top.scala 1123:22]
    .valid_up(n366_valid_up),
    .valid_down(n366_valid_down),
    .I0(n366_I0),
    .I1(n366_I1),
    .O_t0b(n366_O_t0b),
    .O_t1b(n366_O_t1b)
  );
  AtomTuple_9 n367 ( // @[Top.scala 1127:22]
    .valid_up(n367_valid_up),
    .valid_down(n367_valid_down),
    .I0(n367_I0),
    .I1_t0b(n367_I1_t0b),
    .I1_t1b(n367_I1_t1b),
    .O_t0b(n367_O_t0b),
    .O_t1b_t0b(n367_O_t1b_t0b),
    .O_t1b_t1b(n367_O_t1b_t1b)
  );
  If n368 ( // @[Top.scala 1131:22]
    .valid_up(n368_valid_up),
    .valid_down(n368_valid_down),
    .I_t0b(n368_I_t0b),
    .I_t1b_t0b(n368_I_t1b_t0b),
    .I_t1b_t1b(n368_I_t1b_t1b),
    .O(n368_O)
  );
  AtomTuple_1 n369 ( // @[Top.scala 1134:22]
    .valid_up(n369_valid_up),
    .valid_down(n369_valid_down),
    .I0(n369_I0),
    .I1(n369_I1),
    .O_t0b(n369_O_t0b),
    .O_t1b(n369_O_t1b)
  );
  Mul n370 ( // @[Top.scala 1138:22]
    .clock(n370_clock),
    .reset(n370_reset),
    .valid_up(n370_valid_up),
    .valid_down(n370_valid_down),
    .I_t0b(n370_I_t0b),
    .I_t1b(n370_I_t1b),
    .O(n370_O)
  );
  AtomTuple_1 n372 ( // @[Top.scala 1141:22]
    .valid_up(n372_valid_up),
    .valid_down(n372_valid_down),
    .I0(n372_I0),
    .I1(n372_I1),
    .O_t0b(n372_O_t0b),
    .O_t1b(n372_O_t1b)
  );
  Lt n373 ( // @[Top.scala 1145:22]
    .valid_up(n373_valid_up),
    .valid_down(n373_valid_down),
    .I_t0b(n373_I_t0b),
    .I_t1b(n373_I_t1b),
    .O(n373_O)
  );
  InitialDelayCounter_30 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n375 ( // @[Top.scala 1149:22]
    .valid_up(n375_valid_up),
    .valid_down(n375_valid_down),
    .I0(n375_I0),
    .I1(n375_I1),
    .O_t0b(n375_O_t0b),
    .O_t1b(n375_O_t1b)
  );
  Sub n376 ( // @[Top.scala 1153:22]
    .valid_up(n376_valid_up),
    .valid_down(n376_valid_down),
    .I_t0b(n376_I_t0b),
    .I_t1b(n376_I_t1b),
    .O(n376_O)
  );
  AtomTuple_1 n377 ( // @[Top.scala 1156:22]
    .valid_up(n377_valid_up),
    .valid_down(n377_valid_down),
    .I0(n377_I0),
    .I1(n377_I1),
    .O_t0b(n377_O_t0b),
    .O_t1b(n377_O_t1b)
  );
  AtomTuple_1 n378 ( // @[Top.scala 1160:22]
    .valid_up(n378_valid_up),
    .valid_down(n378_valid_down),
    .I0(n378_I0),
    .I1(n378_I1),
    .O_t0b(n378_O_t0b),
    .O_t1b(n378_O_t1b)
  );
  AtomTuple_15 n379 ( // @[Top.scala 1164:22]
    .valid_up(n379_valid_up),
    .valid_down(n379_valid_down),
    .I0_t0b(n379_I0_t0b),
    .I0_t1b(n379_I0_t1b),
    .I1_t0b(n379_I1_t0b),
    .I1_t1b(n379_I1_t1b),
    .O_t0b_t0b(n379_O_t0b_t0b),
    .O_t0b_t1b(n379_O_t0b_t1b),
    .O_t1b_t0b(n379_O_t1b_t0b),
    .O_t1b_t1b(n379_O_t1b_t1b)
  );
  FIFO_3 n380 ( // @[Top.scala 1168:22]
    .clock(n380_clock),
    .reset(n380_reset),
    .valid_up(n380_valid_up),
    .valid_down(n380_valid_down),
    .I_t0b_t0b(n380_I_t0b_t0b),
    .I_t0b_t1b(n380_I_t0b_t1b),
    .I_t1b_t0b(n380_I_t1b_t0b),
    .I_t1b_t1b(n380_I_t1b_t1b),
    .O_t0b_t0b(n380_O_t0b_t0b),
    .O_t0b_t1b(n380_O_t0b_t1b),
    .O_t1b_t0b(n380_O_t1b_t0b),
    .O_t1b_t1b(n380_O_t1b_t1b)
  );
  AtomTuple_16 n381 ( // @[Top.scala 1171:22]
    .valid_up(n381_valid_up),
    .valid_down(n381_valid_down),
    .I0(n381_I0),
    .I1_t0b_t0b(n381_I1_t0b_t0b),
    .I1_t0b_t1b(n381_I1_t0b_t1b),
    .I1_t1b_t0b(n381_I1_t1b_t0b),
    .I1_t1b_t1b(n381_I1_t1b_t1b),
    .O_t0b(n381_O_t0b),
    .O_t1b_t0b_t0b(n381_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n381_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n381_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n381_O_t1b_t1b_t1b)
  );
  If_1 n382 ( // @[Top.scala 1175:22]
    .valid_up(n382_valid_up),
    .valid_down(n382_valid_down),
    .I_t0b(n382_I_t0b),
    .I_t1b_t0b_t0b(n382_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n382_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n382_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n382_I_t1b_t1b_t1b),
    .O_t0b(n382_O_t0b),
    .O_t1b(n382_O_t1b)
  );
  AtomTuple_3 n384 ( // @[Top.scala 1178:22]
    .valid_up(n384_valid_up),
    .valid_down(n384_valid_down),
    .I0(n384_I0),
    .I1_t0b(n384_I1_t0b),
    .I1_t1b(n384_I1_t1b),
    .O_t0b(n384_O_t0b),
    .O_t1b_t0b(n384_O_t1b_t0b),
    .O_t1b_t1b(n384_O_t1b_t1b)
  );
  assign valid_down = n384_valid_down; // @[Top.scala 1183:16]
  assign O_t0b = n384_O_t0b; // @[Top.scala 1182:7]
  assign O_t1b_t0b = n384_O_t1b_t0b; // @[Top.scala 1182:7]
  assign O_t1b_t1b = n384_O_t1b_t1b; // @[Top.scala 1182:7]
  assign n350_valid_up = valid_up; // @[Top.scala 1077:19]
  assign n350_I_t0b = I_t0b; // @[Top.scala 1076:12]
  assign n383_clock = clock;
  assign n383_reset = reset;
  assign n383_valid_up = n350_valid_down; // @[Top.scala 1080:19]
  assign n383_I = n350_O; // @[Top.scala 1079:12]
  assign n371_clock = clock;
  assign n371_reset = reset;
  assign n371_valid_up = n350_valid_down; // @[Top.scala 1083:19]
  assign n371_I = n350_O; // @[Top.scala 1082:12]
  assign n351_valid_up = valid_up; // @[Top.scala 1086:19]
  assign n351_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1085:12]
  assign n351_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1085:12]
  assign n352_valid_up = n351_valid_down; // @[Top.scala 1089:19]
  assign n352_I_t0b = n351_O_t0b; // @[Top.scala 1088:12]
  assign n353_valid_up = n351_valid_down; // @[Top.scala 1092:19]
  assign n353_I_t1b = n351_O_t1b; // @[Top.scala 1091:12]
  assign n354_valid_up = n352_valid_down & n353_valid_down; // @[Top.scala 1096:19]
  assign n354_I0 = n352_O; // @[Top.scala 1094:13]
  assign n354_I1 = n353_O; // @[Top.scala 1095:13]
  assign n355_valid_up = n354_valid_down; // @[Top.scala 1099:19]
  assign n355_I_t0b = n354_O_t0b; // @[Top.scala 1098:12]
  assign n355_I_t1b = n354_O_t1b; // @[Top.scala 1098:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n358_valid_up = n355_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 1104:19]
  assign n358_I0 = n355_O; // @[Top.scala 1102:13]
  assign n359_valid_up = n358_valid_down; // @[Top.scala 1107:19]
  assign n359_I_t0b = n358_O_t0b; // @[Top.scala 1106:12]
  assign n360_valid_up = n359_valid_down & n352_valid_down; // @[Top.scala 1111:19]
  assign n360_I0 = n359_O; // @[Top.scala 1109:13]
  assign n360_I1 = n352_O; // @[Top.scala 1110:13]
  assign n361_valid_up = n360_valid_down; // @[Top.scala 1114:19]
  assign n361_I_t0b = n360_O_t0b; // @[Top.scala 1113:12]
  assign n361_I_t1b = n360_O_t1b; // @[Top.scala 1113:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n364_valid_up = n359_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1119:19]
  assign n364_I0 = n359_O; // @[Top.scala 1117:13]
  assign n364_I1 = 16'h1; // @[Top.scala 1118:13]
  assign n365_valid_up = n364_valid_down; // @[Top.scala 1122:19]
  assign n365_I_t0b = n364_O_t0b; // @[Top.scala 1121:12]
  assign n365_I_t1b = n364_O_t1b; // @[Top.scala 1121:12]
  assign n366_valid_up = n365_valid_down & n359_valid_down; // @[Top.scala 1126:19]
  assign n366_I0 = n365_O; // @[Top.scala 1124:13]
  assign n366_I1 = n359_O; // @[Top.scala 1125:13]
  assign n367_valid_up = n361_valid_down & n366_valid_down; // @[Top.scala 1130:19]
  assign n367_I0 = n361_O[0]; // @[Top.scala 1128:13]
  assign n367_I1_t0b = n366_O_t0b; // @[Top.scala 1129:13]
  assign n367_I1_t1b = n366_O_t1b; // @[Top.scala 1129:13]
  assign n368_valid_up = n367_valid_down; // @[Top.scala 1133:19]
  assign n368_I_t0b = n367_O_t0b; // @[Top.scala 1132:12]
  assign n368_I_t1b_t0b = n367_O_t1b_t0b; // @[Top.scala 1132:12]
  assign n368_I_t1b_t1b = n367_O_t1b_t1b; // @[Top.scala 1132:12]
  assign n369_valid_up = n368_valid_down; // @[Top.scala 1137:19]
  assign n369_I0 = n368_O; // @[Top.scala 1135:13]
  assign n369_I1 = n368_O; // @[Top.scala 1136:13]
  assign n370_clock = clock;
  assign n370_reset = reset;
  assign n370_valid_up = n369_valid_down; // @[Top.scala 1140:19]
  assign n370_I_t0b = n369_O_t0b; // @[Top.scala 1139:12]
  assign n370_I_t1b = n369_O_t1b; // @[Top.scala 1139:12]
  assign n372_valid_up = n371_valid_down & n370_valid_down; // @[Top.scala 1144:19]
  assign n372_I0 = n371_O; // @[Top.scala 1142:13]
  assign n372_I1 = n370_O; // @[Top.scala 1143:13]
  assign n373_valid_up = n372_valid_down; // @[Top.scala 1147:19]
  assign n373_I_t0b = n372_O_t0b; // @[Top.scala 1146:12]
  assign n373_I_t1b = n372_O_t1b; // @[Top.scala 1146:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n375_valid_up = n368_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1152:19]
  assign n375_I0 = n368_O; // @[Top.scala 1150:13]
  assign n375_I1 = 16'h1; // @[Top.scala 1151:13]
  assign n376_valid_up = n375_valid_down; // @[Top.scala 1155:19]
  assign n376_I_t0b = n375_O_t0b; // @[Top.scala 1154:12]
  assign n376_I_t1b = n375_O_t1b; // @[Top.scala 1154:12]
  assign n377_valid_up = n352_valid_down & n376_valid_down; // @[Top.scala 1159:19]
  assign n377_I0 = n352_O; // @[Top.scala 1157:13]
  assign n377_I1 = n376_O; // @[Top.scala 1158:13]
  assign n378_valid_up = n368_valid_down & n353_valid_down; // @[Top.scala 1163:19]
  assign n378_I0 = n368_O; // @[Top.scala 1161:13]
  assign n378_I1 = n353_O; // @[Top.scala 1162:13]
  assign n379_valid_up = n377_valid_down & n378_valid_down; // @[Top.scala 1167:19]
  assign n379_I0_t0b = n377_O_t0b; // @[Top.scala 1165:13]
  assign n379_I0_t1b = n377_O_t1b; // @[Top.scala 1165:13]
  assign n379_I1_t0b = n378_O_t0b; // @[Top.scala 1166:13]
  assign n379_I1_t1b = n378_O_t1b; // @[Top.scala 1166:13]
  assign n380_clock = clock;
  assign n380_reset = reset;
  assign n380_valid_up = n379_valid_down; // @[Top.scala 1170:19]
  assign n380_I_t0b_t0b = n379_O_t0b_t0b; // @[Top.scala 1169:12]
  assign n380_I_t0b_t1b = n379_O_t0b_t1b; // @[Top.scala 1169:12]
  assign n380_I_t1b_t0b = n379_O_t1b_t0b; // @[Top.scala 1169:12]
  assign n380_I_t1b_t1b = n379_O_t1b_t1b; // @[Top.scala 1169:12]
  assign n381_valid_up = n373_valid_down & n380_valid_down; // @[Top.scala 1174:19]
  assign n381_I0 = n373_O[0]; // @[Top.scala 1172:13]
  assign n381_I1_t0b_t0b = n380_O_t0b_t0b; // @[Top.scala 1173:13]
  assign n381_I1_t0b_t1b = n380_O_t0b_t1b; // @[Top.scala 1173:13]
  assign n381_I1_t1b_t0b = n380_O_t1b_t0b; // @[Top.scala 1173:13]
  assign n381_I1_t1b_t1b = n380_O_t1b_t1b; // @[Top.scala 1173:13]
  assign n382_valid_up = n381_valid_down; // @[Top.scala 1177:19]
  assign n382_I_t0b = n381_O_t0b; // @[Top.scala 1176:12]
  assign n382_I_t1b_t0b_t0b = n381_O_t1b_t0b_t0b; // @[Top.scala 1176:12]
  assign n382_I_t1b_t0b_t1b = n381_O_t1b_t0b_t1b; // @[Top.scala 1176:12]
  assign n382_I_t1b_t1b_t0b = n381_O_t1b_t1b_t0b; // @[Top.scala 1176:12]
  assign n382_I_t1b_t1b_t1b = n381_O_t1b_t1b_t1b; // @[Top.scala 1176:12]
  assign n384_valid_up = n383_valid_down & n382_valid_down; // @[Top.scala 1181:19]
  assign n384_I0 = n383_O; // @[Top.scala 1179:13]
  assign n384_I1_t0b = n382_O_t0b; // @[Top.scala 1180:13]
  assign n384_I1_t1b = n382_O_t1b; // @[Top.scala 1180:13]
endmodule
module MapT_10(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_10 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
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
  wire  n387_valid_up; // @[Top.scala 1189:22]
  wire  n387_valid_down; // @[Top.scala 1189:22]
  wire [15:0] n387_I_t0b; // @[Top.scala 1189:22]
  wire [15:0] n387_O; // @[Top.scala 1189:22]
  wire  n420_clock; // @[Top.scala 1192:22]
  wire  n420_reset; // @[Top.scala 1192:22]
  wire  n420_valid_up; // @[Top.scala 1192:22]
  wire  n420_valid_down; // @[Top.scala 1192:22]
  wire [15:0] n420_I; // @[Top.scala 1192:22]
  wire [15:0] n420_O; // @[Top.scala 1192:22]
  wire  n408_clock; // @[Top.scala 1195:22]
  wire  n408_reset; // @[Top.scala 1195:22]
  wire  n408_valid_up; // @[Top.scala 1195:22]
  wire  n408_valid_down; // @[Top.scala 1195:22]
  wire [15:0] n408_I; // @[Top.scala 1195:22]
  wire [15:0] n408_O; // @[Top.scala 1195:22]
  wire  n388_valid_up; // @[Top.scala 1198:22]
  wire  n388_valid_down; // @[Top.scala 1198:22]
  wire [15:0] n388_I_t1b_t0b; // @[Top.scala 1198:22]
  wire [15:0] n388_I_t1b_t1b; // @[Top.scala 1198:22]
  wire [15:0] n388_O_t0b; // @[Top.scala 1198:22]
  wire [15:0] n388_O_t1b; // @[Top.scala 1198:22]
  wire  n389_valid_up; // @[Top.scala 1201:22]
  wire  n389_valid_down; // @[Top.scala 1201:22]
  wire [15:0] n389_I_t0b; // @[Top.scala 1201:22]
  wire [15:0] n389_O; // @[Top.scala 1201:22]
  wire  n390_valid_up; // @[Top.scala 1204:22]
  wire  n390_valid_down; // @[Top.scala 1204:22]
  wire [15:0] n390_I_t1b; // @[Top.scala 1204:22]
  wire [15:0] n390_O; // @[Top.scala 1204:22]
  wire  n391_valid_up; // @[Top.scala 1207:22]
  wire  n391_valid_down; // @[Top.scala 1207:22]
  wire [15:0] n391_I0; // @[Top.scala 1207:22]
  wire [15:0] n391_I1; // @[Top.scala 1207:22]
  wire [15:0] n391_O_t0b; // @[Top.scala 1207:22]
  wire [15:0] n391_O_t1b; // @[Top.scala 1207:22]
  wire  n392_valid_up; // @[Top.scala 1211:22]
  wire  n392_valid_down; // @[Top.scala 1211:22]
  wire [15:0] n392_I_t0b; // @[Top.scala 1211:22]
  wire [15:0] n392_I_t1b; // @[Top.scala 1211:22]
  wire [15:0] n392_O; // @[Top.scala 1211:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n395_valid_up; // @[Top.scala 1215:22]
  wire  n395_valid_down; // @[Top.scala 1215:22]
  wire [15:0] n395_I0; // @[Top.scala 1215:22]
  wire [15:0] n395_O_t0b; // @[Top.scala 1215:22]
  wire  n396_valid_up; // @[Top.scala 1219:22]
  wire  n396_valid_down; // @[Top.scala 1219:22]
  wire [15:0] n396_I_t0b; // @[Top.scala 1219:22]
  wire [15:0] n396_O; // @[Top.scala 1219:22]
  wire  n397_valid_up; // @[Top.scala 1222:22]
  wire  n397_valid_down; // @[Top.scala 1222:22]
  wire [15:0] n397_I0; // @[Top.scala 1222:22]
  wire [15:0] n397_I1; // @[Top.scala 1222:22]
  wire [15:0] n397_O_t0b; // @[Top.scala 1222:22]
  wire [15:0] n397_O_t1b; // @[Top.scala 1222:22]
  wire  n398_valid_up; // @[Top.scala 1226:22]
  wire  n398_valid_down; // @[Top.scala 1226:22]
  wire [15:0] n398_I_t0b; // @[Top.scala 1226:22]
  wire [15:0] n398_I_t1b; // @[Top.scala 1226:22]
  wire [15:0] n398_O; // @[Top.scala 1226:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n401_valid_up; // @[Top.scala 1230:22]
  wire  n401_valid_down; // @[Top.scala 1230:22]
  wire [15:0] n401_I0; // @[Top.scala 1230:22]
  wire [15:0] n401_I1; // @[Top.scala 1230:22]
  wire [15:0] n401_O_t0b; // @[Top.scala 1230:22]
  wire [15:0] n401_O_t1b; // @[Top.scala 1230:22]
  wire  n402_valid_up; // @[Top.scala 1234:22]
  wire  n402_valid_down; // @[Top.scala 1234:22]
  wire [15:0] n402_I_t0b; // @[Top.scala 1234:22]
  wire [15:0] n402_I_t1b; // @[Top.scala 1234:22]
  wire [15:0] n402_O; // @[Top.scala 1234:22]
  wire  n403_valid_up; // @[Top.scala 1237:22]
  wire  n403_valid_down; // @[Top.scala 1237:22]
  wire [15:0] n403_I0; // @[Top.scala 1237:22]
  wire [15:0] n403_I1; // @[Top.scala 1237:22]
  wire [15:0] n403_O_t0b; // @[Top.scala 1237:22]
  wire [15:0] n403_O_t1b; // @[Top.scala 1237:22]
  wire  n404_valid_up; // @[Top.scala 1241:22]
  wire  n404_valid_down; // @[Top.scala 1241:22]
  wire  n404_I0; // @[Top.scala 1241:22]
  wire [15:0] n404_I1_t0b; // @[Top.scala 1241:22]
  wire [15:0] n404_I1_t1b; // @[Top.scala 1241:22]
  wire  n404_O_t0b; // @[Top.scala 1241:22]
  wire [15:0] n404_O_t1b_t0b; // @[Top.scala 1241:22]
  wire [15:0] n404_O_t1b_t1b; // @[Top.scala 1241:22]
  wire  n405_valid_up; // @[Top.scala 1245:22]
  wire  n405_valid_down; // @[Top.scala 1245:22]
  wire  n405_I_t0b; // @[Top.scala 1245:22]
  wire [15:0] n405_I_t1b_t0b; // @[Top.scala 1245:22]
  wire [15:0] n405_I_t1b_t1b; // @[Top.scala 1245:22]
  wire [15:0] n405_O; // @[Top.scala 1245:22]
  wire  n406_valid_up; // @[Top.scala 1248:22]
  wire  n406_valid_down; // @[Top.scala 1248:22]
  wire [15:0] n406_I0; // @[Top.scala 1248:22]
  wire [15:0] n406_I1; // @[Top.scala 1248:22]
  wire [15:0] n406_O_t0b; // @[Top.scala 1248:22]
  wire [15:0] n406_O_t1b; // @[Top.scala 1248:22]
  wire  n407_clock; // @[Top.scala 1252:22]
  wire  n407_reset; // @[Top.scala 1252:22]
  wire  n407_valid_up; // @[Top.scala 1252:22]
  wire  n407_valid_down; // @[Top.scala 1252:22]
  wire [15:0] n407_I_t0b; // @[Top.scala 1252:22]
  wire [15:0] n407_I_t1b; // @[Top.scala 1252:22]
  wire [15:0] n407_O; // @[Top.scala 1252:22]
  wire  n409_valid_up; // @[Top.scala 1255:22]
  wire  n409_valid_down; // @[Top.scala 1255:22]
  wire [15:0] n409_I0; // @[Top.scala 1255:22]
  wire [15:0] n409_I1; // @[Top.scala 1255:22]
  wire [15:0] n409_O_t0b; // @[Top.scala 1255:22]
  wire [15:0] n409_O_t1b; // @[Top.scala 1255:22]
  wire  n410_valid_up; // @[Top.scala 1259:22]
  wire  n410_valid_down; // @[Top.scala 1259:22]
  wire [15:0] n410_I_t0b; // @[Top.scala 1259:22]
  wire [15:0] n410_I_t1b; // @[Top.scala 1259:22]
  wire [15:0] n410_O; // @[Top.scala 1259:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n412_valid_up; // @[Top.scala 1263:22]
  wire  n412_valid_down; // @[Top.scala 1263:22]
  wire [15:0] n412_I0; // @[Top.scala 1263:22]
  wire [15:0] n412_I1; // @[Top.scala 1263:22]
  wire [15:0] n412_O_t0b; // @[Top.scala 1263:22]
  wire [15:0] n412_O_t1b; // @[Top.scala 1263:22]
  wire  n413_valid_up; // @[Top.scala 1267:22]
  wire  n413_valid_down; // @[Top.scala 1267:22]
  wire [15:0] n413_I_t0b; // @[Top.scala 1267:22]
  wire [15:0] n413_I_t1b; // @[Top.scala 1267:22]
  wire [15:0] n413_O; // @[Top.scala 1267:22]
  wire  n414_valid_up; // @[Top.scala 1270:22]
  wire  n414_valid_down; // @[Top.scala 1270:22]
  wire [15:0] n414_I0; // @[Top.scala 1270:22]
  wire [15:0] n414_I1; // @[Top.scala 1270:22]
  wire [15:0] n414_O_t0b; // @[Top.scala 1270:22]
  wire [15:0] n414_O_t1b; // @[Top.scala 1270:22]
  wire  n415_valid_up; // @[Top.scala 1274:22]
  wire  n415_valid_down; // @[Top.scala 1274:22]
  wire [15:0] n415_I0; // @[Top.scala 1274:22]
  wire [15:0] n415_I1; // @[Top.scala 1274:22]
  wire [15:0] n415_O_t0b; // @[Top.scala 1274:22]
  wire [15:0] n415_O_t1b; // @[Top.scala 1274:22]
  wire  n416_valid_up; // @[Top.scala 1278:22]
  wire  n416_valid_down; // @[Top.scala 1278:22]
  wire [15:0] n416_I0_t0b; // @[Top.scala 1278:22]
  wire [15:0] n416_I0_t1b; // @[Top.scala 1278:22]
  wire [15:0] n416_I1_t0b; // @[Top.scala 1278:22]
  wire [15:0] n416_I1_t1b; // @[Top.scala 1278:22]
  wire [15:0] n416_O_t0b_t0b; // @[Top.scala 1278:22]
  wire [15:0] n416_O_t0b_t1b; // @[Top.scala 1278:22]
  wire [15:0] n416_O_t1b_t0b; // @[Top.scala 1278:22]
  wire [15:0] n416_O_t1b_t1b; // @[Top.scala 1278:22]
  wire  n417_clock; // @[Top.scala 1282:22]
  wire  n417_reset; // @[Top.scala 1282:22]
  wire  n417_valid_up; // @[Top.scala 1282:22]
  wire  n417_valid_down; // @[Top.scala 1282:22]
  wire [15:0] n417_I_t0b_t0b; // @[Top.scala 1282:22]
  wire [15:0] n417_I_t0b_t1b; // @[Top.scala 1282:22]
  wire [15:0] n417_I_t1b_t0b; // @[Top.scala 1282:22]
  wire [15:0] n417_I_t1b_t1b; // @[Top.scala 1282:22]
  wire [15:0] n417_O_t0b_t0b; // @[Top.scala 1282:22]
  wire [15:0] n417_O_t0b_t1b; // @[Top.scala 1282:22]
  wire [15:0] n417_O_t1b_t0b; // @[Top.scala 1282:22]
  wire [15:0] n417_O_t1b_t1b; // @[Top.scala 1282:22]
  wire  n418_valid_up; // @[Top.scala 1285:22]
  wire  n418_valid_down; // @[Top.scala 1285:22]
  wire  n418_I0; // @[Top.scala 1285:22]
  wire [15:0] n418_I1_t0b_t0b; // @[Top.scala 1285:22]
  wire [15:0] n418_I1_t0b_t1b; // @[Top.scala 1285:22]
  wire [15:0] n418_I1_t1b_t0b; // @[Top.scala 1285:22]
  wire [15:0] n418_I1_t1b_t1b; // @[Top.scala 1285:22]
  wire  n418_O_t0b; // @[Top.scala 1285:22]
  wire [15:0] n418_O_t1b_t0b_t0b; // @[Top.scala 1285:22]
  wire [15:0] n418_O_t1b_t0b_t1b; // @[Top.scala 1285:22]
  wire [15:0] n418_O_t1b_t1b_t0b; // @[Top.scala 1285:22]
  wire [15:0] n418_O_t1b_t1b_t1b; // @[Top.scala 1285:22]
  wire  n419_valid_up; // @[Top.scala 1289:22]
  wire  n419_valid_down; // @[Top.scala 1289:22]
  wire  n419_I_t0b; // @[Top.scala 1289:22]
  wire [15:0] n419_I_t1b_t0b_t0b; // @[Top.scala 1289:22]
  wire [15:0] n419_I_t1b_t0b_t1b; // @[Top.scala 1289:22]
  wire [15:0] n419_I_t1b_t1b_t0b; // @[Top.scala 1289:22]
  wire [15:0] n419_I_t1b_t1b_t1b; // @[Top.scala 1289:22]
  wire [15:0] n419_O_t0b; // @[Top.scala 1289:22]
  wire [15:0] n419_O_t1b; // @[Top.scala 1289:22]
  wire  n421_valid_up; // @[Top.scala 1292:22]
  wire  n421_valid_down; // @[Top.scala 1292:22]
  wire [15:0] n421_I0; // @[Top.scala 1292:22]
  wire [15:0] n421_I1_t0b; // @[Top.scala 1292:22]
  wire [15:0] n421_I1_t1b; // @[Top.scala 1292:22]
  wire [15:0] n421_O_t0b; // @[Top.scala 1292:22]
  wire [15:0] n421_O_t1b_t0b; // @[Top.scala 1292:22]
  wire [15:0] n421_O_t1b_t1b; // @[Top.scala 1292:22]
  Fst n387 ( // @[Top.scala 1189:22]
    .valid_up(n387_valid_up),
    .valid_down(n387_valid_down),
    .I_t0b(n387_I_t0b),
    .O(n387_O)
  );
  FIFO_1 n420 ( // @[Top.scala 1192:22]
    .clock(n420_clock),
    .reset(n420_reset),
    .valid_up(n420_valid_up),
    .valid_down(n420_valid_down),
    .I(n420_I),
    .O(n420_O)
  );
  FIFO_1 n408 ( // @[Top.scala 1195:22]
    .clock(n408_clock),
    .reset(n408_reset),
    .valid_up(n408_valid_up),
    .valid_down(n408_valid_down),
    .I(n408_I),
    .O(n408_O)
  );
  Snd n388 ( // @[Top.scala 1198:22]
    .valid_up(n388_valid_up),
    .valid_down(n388_valid_down),
    .I_t1b_t0b(n388_I_t1b_t0b),
    .I_t1b_t1b(n388_I_t1b_t1b),
    .O_t0b(n388_O_t0b),
    .O_t1b(n388_O_t1b)
  );
  Fst_1 n389 ( // @[Top.scala 1201:22]
    .valid_up(n389_valid_up),
    .valid_down(n389_valid_down),
    .I_t0b(n389_I_t0b),
    .O(n389_O)
  );
  Snd_1 n390 ( // @[Top.scala 1204:22]
    .valid_up(n390_valid_up),
    .valid_down(n390_valid_down),
    .I_t1b(n390_I_t1b),
    .O(n390_O)
  );
  AtomTuple_1 n391 ( // @[Top.scala 1207:22]
    .valid_up(n391_valid_up),
    .valid_down(n391_valid_down),
    .I0(n391_I0),
    .I1(n391_I1),
    .O_t0b(n391_O_t0b),
    .O_t1b(n391_O_t1b)
  );
  Add n392 ( // @[Top.scala 1211:22]
    .valid_up(n392_valid_up),
    .valid_down(n392_valid_down),
    .I_t0b(n392_I_t0b),
    .I_t1b(n392_I_t1b),
    .O(n392_O)
  );
  InitialDelayCounter_33 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n395 ( // @[Top.scala 1215:22]
    .valid_up(n395_valid_up),
    .valid_down(n395_valid_down),
    .I0(n395_I0),
    .O_t0b(n395_O_t0b)
  );
  RShift n396 ( // @[Top.scala 1219:22]
    .valid_up(n396_valid_up),
    .valid_down(n396_valid_down),
    .I_t0b(n396_I_t0b),
    .O(n396_O)
  );
  AtomTuple_1 n397 ( // @[Top.scala 1222:22]
    .valid_up(n397_valid_up),
    .valid_down(n397_valid_down),
    .I0(n397_I0),
    .I1(n397_I1),
    .O_t0b(n397_O_t0b),
    .O_t1b(n397_O_t1b)
  );
  Eq n398 ( // @[Top.scala 1226:22]
    .valid_up(n398_valid_up),
    .valid_down(n398_valid_down),
    .I_t0b(n398_I_t0b),
    .I_t1b(n398_I_t1b),
    .O(n398_O)
  );
  InitialDelayCounter_33 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n401 ( // @[Top.scala 1230:22]
    .valid_up(n401_valid_up),
    .valid_down(n401_valid_down),
    .I0(n401_I0),
    .I1(n401_I1),
    .O_t0b(n401_O_t0b),
    .O_t1b(n401_O_t1b)
  );
  Add n402 ( // @[Top.scala 1234:22]
    .valid_up(n402_valid_up),
    .valid_down(n402_valid_down),
    .I_t0b(n402_I_t0b),
    .I_t1b(n402_I_t1b),
    .O(n402_O)
  );
  AtomTuple_1 n403 ( // @[Top.scala 1237:22]
    .valid_up(n403_valid_up),
    .valid_down(n403_valid_down),
    .I0(n403_I0),
    .I1(n403_I1),
    .O_t0b(n403_O_t0b),
    .O_t1b(n403_O_t1b)
  );
  AtomTuple_9 n404 ( // @[Top.scala 1241:22]
    .valid_up(n404_valid_up),
    .valid_down(n404_valid_down),
    .I0(n404_I0),
    .I1_t0b(n404_I1_t0b),
    .I1_t1b(n404_I1_t1b),
    .O_t0b(n404_O_t0b),
    .O_t1b_t0b(n404_O_t1b_t0b),
    .O_t1b_t1b(n404_O_t1b_t1b)
  );
  If n405 ( // @[Top.scala 1245:22]
    .valid_up(n405_valid_up),
    .valid_down(n405_valid_down),
    .I_t0b(n405_I_t0b),
    .I_t1b_t0b(n405_I_t1b_t0b),
    .I_t1b_t1b(n405_I_t1b_t1b),
    .O(n405_O)
  );
  AtomTuple_1 n406 ( // @[Top.scala 1248:22]
    .valid_up(n406_valid_up),
    .valid_down(n406_valid_down),
    .I0(n406_I0),
    .I1(n406_I1),
    .O_t0b(n406_O_t0b),
    .O_t1b(n406_O_t1b)
  );
  Mul n407 ( // @[Top.scala 1252:22]
    .clock(n407_clock),
    .reset(n407_reset),
    .valid_up(n407_valid_up),
    .valid_down(n407_valid_down),
    .I_t0b(n407_I_t0b),
    .I_t1b(n407_I_t1b),
    .O(n407_O)
  );
  AtomTuple_1 n409 ( // @[Top.scala 1255:22]
    .valid_up(n409_valid_up),
    .valid_down(n409_valid_down),
    .I0(n409_I0),
    .I1(n409_I1),
    .O_t0b(n409_O_t0b),
    .O_t1b(n409_O_t1b)
  );
  Lt n410 ( // @[Top.scala 1259:22]
    .valid_up(n410_valid_up),
    .valid_down(n410_valid_down),
    .I_t0b(n410_I_t0b),
    .I_t1b(n410_I_t1b),
    .O(n410_O)
  );
  InitialDelayCounter_33 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n412 ( // @[Top.scala 1263:22]
    .valid_up(n412_valid_up),
    .valid_down(n412_valid_down),
    .I0(n412_I0),
    .I1(n412_I1),
    .O_t0b(n412_O_t0b),
    .O_t1b(n412_O_t1b)
  );
  Sub n413 ( // @[Top.scala 1267:22]
    .valid_up(n413_valid_up),
    .valid_down(n413_valid_down),
    .I_t0b(n413_I_t0b),
    .I_t1b(n413_I_t1b),
    .O(n413_O)
  );
  AtomTuple_1 n414 ( // @[Top.scala 1270:22]
    .valid_up(n414_valid_up),
    .valid_down(n414_valid_down),
    .I0(n414_I0),
    .I1(n414_I1),
    .O_t0b(n414_O_t0b),
    .O_t1b(n414_O_t1b)
  );
  AtomTuple_1 n415 ( // @[Top.scala 1274:22]
    .valid_up(n415_valid_up),
    .valid_down(n415_valid_down),
    .I0(n415_I0),
    .I1(n415_I1),
    .O_t0b(n415_O_t0b),
    .O_t1b(n415_O_t1b)
  );
  AtomTuple_15 n416 ( // @[Top.scala 1278:22]
    .valid_up(n416_valid_up),
    .valid_down(n416_valid_down),
    .I0_t0b(n416_I0_t0b),
    .I0_t1b(n416_I0_t1b),
    .I1_t0b(n416_I1_t0b),
    .I1_t1b(n416_I1_t1b),
    .O_t0b_t0b(n416_O_t0b_t0b),
    .O_t0b_t1b(n416_O_t0b_t1b),
    .O_t1b_t0b(n416_O_t1b_t0b),
    .O_t1b_t1b(n416_O_t1b_t1b)
  );
  FIFO_3 n417 ( // @[Top.scala 1282:22]
    .clock(n417_clock),
    .reset(n417_reset),
    .valid_up(n417_valid_up),
    .valid_down(n417_valid_down),
    .I_t0b_t0b(n417_I_t0b_t0b),
    .I_t0b_t1b(n417_I_t0b_t1b),
    .I_t1b_t0b(n417_I_t1b_t0b),
    .I_t1b_t1b(n417_I_t1b_t1b),
    .O_t0b_t0b(n417_O_t0b_t0b),
    .O_t0b_t1b(n417_O_t0b_t1b),
    .O_t1b_t0b(n417_O_t1b_t0b),
    .O_t1b_t1b(n417_O_t1b_t1b)
  );
  AtomTuple_16 n418 ( // @[Top.scala 1285:22]
    .valid_up(n418_valid_up),
    .valid_down(n418_valid_down),
    .I0(n418_I0),
    .I1_t0b_t0b(n418_I1_t0b_t0b),
    .I1_t0b_t1b(n418_I1_t0b_t1b),
    .I1_t1b_t0b(n418_I1_t1b_t0b),
    .I1_t1b_t1b(n418_I1_t1b_t1b),
    .O_t0b(n418_O_t0b),
    .O_t1b_t0b_t0b(n418_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n418_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n418_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n418_O_t1b_t1b_t1b)
  );
  If_1 n419 ( // @[Top.scala 1289:22]
    .valid_up(n419_valid_up),
    .valid_down(n419_valid_down),
    .I_t0b(n419_I_t0b),
    .I_t1b_t0b_t0b(n419_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n419_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n419_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n419_I_t1b_t1b_t1b),
    .O_t0b(n419_O_t0b),
    .O_t1b(n419_O_t1b)
  );
  AtomTuple_3 n421 ( // @[Top.scala 1292:22]
    .valid_up(n421_valid_up),
    .valid_down(n421_valid_down),
    .I0(n421_I0),
    .I1_t0b(n421_I1_t0b),
    .I1_t1b(n421_I1_t1b),
    .O_t0b(n421_O_t0b),
    .O_t1b_t0b(n421_O_t1b_t0b),
    .O_t1b_t1b(n421_O_t1b_t1b)
  );
  assign valid_down = n421_valid_down; // @[Top.scala 1297:16]
  assign O_t0b = n421_O_t0b; // @[Top.scala 1296:7]
  assign O_t1b_t0b = n421_O_t1b_t0b; // @[Top.scala 1296:7]
  assign O_t1b_t1b = n421_O_t1b_t1b; // @[Top.scala 1296:7]
  assign n387_valid_up = valid_up; // @[Top.scala 1191:19]
  assign n387_I_t0b = I_t0b; // @[Top.scala 1190:12]
  assign n420_clock = clock;
  assign n420_reset = reset;
  assign n420_valid_up = n387_valid_down; // @[Top.scala 1194:19]
  assign n420_I = n387_O; // @[Top.scala 1193:12]
  assign n408_clock = clock;
  assign n408_reset = reset;
  assign n408_valid_up = n387_valid_down; // @[Top.scala 1197:19]
  assign n408_I = n387_O; // @[Top.scala 1196:12]
  assign n388_valid_up = valid_up; // @[Top.scala 1200:19]
  assign n388_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1199:12]
  assign n388_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1199:12]
  assign n389_valid_up = n388_valid_down; // @[Top.scala 1203:19]
  assign n389_I_t0b = n388_O_t0b; // @[Top.scala 1202:12]
  assign n390_valid_up = n388_valid_down; // @[Top.scala 1206:19]
  assign n390_I_t1b = n388_O_t1b; // @[Top.scala 1205:12]
  assign n391_valid_up = n389_valid_down & n390_valid_down; // @[Top.scala 1210:19]
  assign n391_I0 = n389_O; // @[Top.scala 1208:13]
  assign n391_I1 = n390_O; // @[Top.scala 1209:13]
  assign n392_valid_up = n391_valid_down; // @[Top.scala 1213:19]
  assign n392_I_t0b = n391_O_t0b; // @[Top.scala 1212:12]
  assign n392_I_t1b = n391_O_t1b; // @[Top.scala 1212:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n395_valid_up = n392_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 1218:19]
  assign n395_I0 = n392_O; // @[Top.scala 1216:13]
  assign n396_valid_up = n395_valid_down; // @[Top.scala 1221:19]
  assign n396_I_t0b = n395_O_t0b; // @[Top.scala 1220:12]
  assign n397_valid_up = n396_valid_down & n389_valid_down; // @[Top.scala 1225:19]
  assign n397_I0 = n396_O; // @[Top.scala 1223:13]
  assign n397_I1 = n389_O; // @[Top.scala 1224:13]
  assign n398_valid_up = n397_valid_down; // @[Top.scala 1228:19]
  assign n398_I_t0b = n397_O_t0b; // @[Top.scala 1227:12]
  assign n398_I_t1b = n397_O_t1b; // @[Top.scala 1227:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n401_valid_up = n396_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1233:19]
  assign n401_I0 = n396_O; // @[Top.scala 1231:13]
  assign n401_I1 = 16'h1; // @[Top.scala 1232:13]
  assign n402_valid_up = n401_valid_down; // @[Top.scala 1236:19]
  assign n402_I_t0b = n401_O_t0b; // @[Top.scala 1235:12]
  assign n402_I_t1b = n401_O_t1b; // @[Top.scala 1235:12]
  assign n403_valid_up = n402_valid_down & n396_valid_down; // @[Top.scala 1240:19]
  assign n403_I0 = n402_O; // @[Top.scala 1238:13]
  assign n403_I1 = n396_O; // @[Top.scala 1239:13]
  assign n404_valid_up = n398_valid_down & n403_valid_down; // @[Top.scala 1244:19]
  assign n404_I0 = n398_O[0]; // @[Top.scala 1242:13]
  assign n404_I1_t0b = n403_O_t0b; // @[Top.scala 1243:13]
  assign n404_I1_t1b = n403_O_t1b; // @[Top.scala 1243:13]
  assign n405_valid_up = n404_valid_down; // @[Top.scala 1247:19]
  assign n405_I_t0b = n404_O_t0b; // @[Top.scala 1246:12]
  assign n405_I_t1b_t0b = n404_O_t1b_t0b; // @[Top.scala 1246:12]
  assign n405_I_t1b_t1b = n404_O_t1b_t1b; // @[Top.scala 1246:12]
  assign n406_valid_up = n405_valid_down; // @[Top.scala 1251:19]
  assign n406_I0 = n405_O; // @[Top.scala 1249:13]
  assign n406_I1 = n405_O; // @[Top.scala 1250:13]
  assign n407_clock = clock;
  assign n407_reset = reset;
  assign n407_valid_up = n406_valid_down; // @[Top.scala 1254:19]
  assign n407_I_t0b = n406_O_t0b; // @[Top.scala 1253:12]
  assign n407_I_t1b = n406_O_t1b; // @[Top.scala 1253:12]
  assign n409_valid_up = n408_valid_down & n407_valid_down; // @[Top.scala 1258:19]
  assign n409_I0 = n408_O; // @[Top.scala 1256:13]
  assign n409_I1 = n407_O; // @[Top.scala 1257:13]
  assign n410_valid_up = n409_valid_down; // @[Top.scala 1261:19]
  assign n410_I_t0b = n409_O_t0b; // @[Top.scala 1260:12]
  assign n410_I_t1b = n409_O_t1b; // @[Top.scala 1260:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n412_valid_up = n405_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1266:19]
  assign n412_I0 = n405_O; // @[Top.scala 1264:13]
  assign n412_I1 = 16'h1; // @[Top.scala 1265:13]
  assign n413_valid_up = n412_valid_down; // @[Top.scala 1269:19]
  assign n413_I_t0b = n412_O_t0b; // @[Top.scala 1268:12]
  assign n413_I_t1b = n412_O_t1b; // @[Top.scala 1268:12]
  assign n414_valid_up = n389_valid_down & n413_valid_down; // @[Top.scala 1273:19]
  assign n414_I0 = n389_O; // @[Top.scala 1271:13]
  assign n414_I1 = n413_O; // @[Top.scala 1272:13]
  assign n415_valid_up = n405_valid_down & n390_valid_down; // @[Top.scala 1277:19]
  assign n415_I0 = n405_O; // @[Top.scala 1275:13]
  assign n415_I1 = n390_O; // @[Top.scala 1276:13]
  assign n416_valid_up = n414_valid_down & n415_valid_down; // @[Top.scala 1281:19]
  assign n416_I0_t0b = n414_O_t0b; // @[Top.scala 1279:13]
  assign n416_I0_t1b = n414_O_t1b; // @[Top.scala 1279:13]
  assign n416_I1_t0b = n415_O_t0b; // @[Top.scala 1280:13]
  assign n416_I1_t1b = n415_O_t1b; // @[Top.scala 1280:13]
  assign n417_clock = clock;
  assign n417_reset = reset;
  assign n417_valid_up = n416_valid_down; // @[Top.scala 1284:19]
  assign n417_I_t0b_t0b = n416_O_t0b_t0b; // @[Top.scala 1283:12]
  assign n417_I_t0b_t1b = n416_O_t0b_t1b; // @[Top.scala 1283:12]
  assign n417_I_t1b_t0b = n416_O_t1b_t0b; // @[Top.scala 1283:12]
  assign n417_I_t1b_t1b = n416_O_t1b_t1b; // @[Top.scala 1283:12]
  assign n418_valid_up = n410_valid_down & n417_valid_down; // @[Top.scala 1288:19]
  assign n418_I0 = n410_O[0]; // @[Top.scala 1286:13]
  assign n418_I1_t0b_t0b = n417_O_t0b_t0b; // @[Top.scala 1287:13]
  assign n418_I1_t0b_t1b = n417_O_t0b_t1b; // @[Top.scala 1287:13]
  assign n418_I1_t1b_t0b = n417_O_t1b_t0b; // @[Top.scala 1287:13]
  assign n418_I1_t1b_t1b = n417_O_t1b_t1b; // @[Top.scala 1287:13]
  assign n419_valid_up = n418_valid_down; // @[Top.scala 1291:19]
  assign n419_I_t0b = n418_O_t0b; // @[Top.scala 1290:12]
  assign n419_I_t1b_t0b_t0b = n418_O_t1b_t0b_t0b; // @[Top.scala 1290:12]
  assign n419_I_t1b_t0b_t1b = n418_O_t1b_t0b_t1b; // @[Top.scala 1290:12]
  assign n419_I_t1b_t1b_t0b = n418_O_t1b_t1b_t0b; // @[Top.scala 1290:12]
  assign n419_I_t1b_t1b_t1b = n418_O_t1b_t1b_t1b; // @[Top.scala 1290:12]
  assign n421_valid_up = n420_valid_down & n419_valid_down; // @[Top.scala 1295:19]
  assign n421_I0 = n420_O; // @[Top.scala 1293:13]
  assign n421_I1_t0b = n419_O_t0b; // @[Top.scala 1294:13]
  assign n421_I1_t1b = n419_O_t1b; // @[Top.scala 1294:13]
endmodule
module MapT_11(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_11 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
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
  wire  n424_valid_up; // @[Top.scala 1303:22]
  wire  n424_valid_down; // @[Top.scala 1303:22]
  wire [15:0] n424_I_t0b; // @[Top.scala 1303:22]
  wire [15:0] n424_O; // @[Top.scala 1303:22]
  wire  n457_clock; // @[Top.scala 1306:22]
  wire  n457_reset; // @[Top.scala 1306:22]
  wire  n457_valid_up; // @[Top.scala 1306:22]
  wire  n457_valid_down; // @[Top.scala 1306:22]
  wire [15:0] n457_I; // @[Top.scala 1306:22]
  wire [15:0] n457_O; // @[Top.scala 1306:22]
  wire  n445_clock; // @[Top.scala 1309:22]
  wire  n445_reset; // @[Top.scala 1309:22]
  wire  n445_valid_up; // @[Top.scala 1309:22]
  wire  n445_valid_down; // @[Top.scala 1309:22]
  wire [15:0] n445_I; // @[Top.scala 1309:22]
  wire [15:0] n445_O; // @[Top.scala 1309:22]
  wire  n425_valid_up; // @[Top.scala 1312:22]
  wire  n425_valid_down; // @[Top.scala 1312:22]
  wire [15:0] n425_I_t1b_t0b; // @[Top.scala 1312:22]
  wire [15:0] n425_I_t1b_t1b; // @[Top.scala 1312:22]
  wire [15:0] n425_O_t0b; // @[Top.scala 1312:22]
  wire [15:0] n425_O_t1b; // @[Top.scala 1312:22]
  wire  n426_valid_up; // @[Top.scala 1315:22]
  wire  n426_valid_down; // @[Top.scala 1315:22]
  wire [15:0] n426_I_t0b; // @[Top.scala 1315:22]
  wire [15:0] n426_O; // @[Top.scala 1315:22]
  wire  n427_valid_up; // @[Top.scala 1318:22]
  wire  n427_valid_down; // @[Top.scala 1318:22]
  wire [15:0] n427_I_t1b; // @[Top.scala 1318:22]
  wire [15:0] n427_O; // @[Top.scala 1318:22]
  wire  n428_valid_up; // @[Top.scala 1321:22]
  wire  n428_valid_down; // @[Top.scala 1321:22]
  wire [15:0] n428_I0; // @[Top.scala 1321:22]
  wire [15:0] n428_I1; // @[Top.scala 1321:22]
  wire [15:0] n428_O_t0b; // @[Top.scala 1321:22]
  wire [15:0] n428_O_t1b; // @[Top.scala 1321:22]
  wire  n429_valid_up; // @[Top.scala 1325:22]
  wire  n429_valid_down; // @[Top.scala 1325:22]
  wire [15:0] n429_I_t0b; // @[Top.scala 1325:22]
  wire [15:0] n429_I_t1b; // @[Top.scala 1325:22]
  wire [15:0] n429_O; // @[Top.scala 1325:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n432_valid_up; // @[Top.scala 1329:22]
  wire  n432_valid_down; // @[Top.scala 1329:22]
  wire [15:0] n432_I0; // @[Top.scala 1329:22]
  wire [15:0] n432_O_t0b; // @[Top.scala 1329:22]
  wire  n433_valid_up; // @[Top.scala 1333:22]
  wire  n433_valid_down; // @[Top.scala 1333:22]
  wire [15:0] n433_I_t0b; // @[Top.scala 1333:22]
  wire [15:0] n433_O; // @[Top.scala 1333:22]
  wire  n434_valid_up; // @[Top.scala 1336:22]
  wire  n434_valid_down; // @[Top.scala 1336:22]
  wire [15:0] n434_I0; // @[Top.scala 1336:22]
  wire [15:0] n434_I1; // @[Top.scala 1336:22]
  wire [15:0] n434_O_t0b; // @[Top.scala 1336:22]
  wire [15:0] n434_O_t1b; // @[Top.scala 1336:22]
  wire  n435_valid_up; // @[Top.scala 1340:22]
  wire  n435_valid_down; // @[Top.scala 1340:22]
  wire [15:0] n435_I_t0b; // @[Top.scala 1340:22]
  wire [15:0] n435_I_t1b; // @[Top.scala 1340:22]
  wire [15:0] n435_O; // @[Top.scala 1340:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n438_valid_up; // @[Top.scala 1344:22]
  wire  n438_valid_down; // @[Top.scala 1344:22]
  wire [15:0] n438_I0; // @[Top.scala 1344:22]
  wire [15:0] n438_I1; // @[Top.scala 1344:22]
  wire [15:0] n438_O_t0b; // @[Top.scala 1344:22]
  wire [15:0] n438_O_t1b; // @[Top.scala 1344:22]
  wire  n439_valid_up; // @[Top.scala 1348:22]
  wire  n439_valid_down; // @[Top.scala 1348:22]
  wire [15:0] n439_I_t0b; // @[Top.scala 1348:22]
  wire [15:0] n439_I_t1b; // @[Top.scala 1348:22]
  wire [15:0] n439_O; // @[Top.scala 1348:22]
  wire  n440_valid_up; // @[Top.scala 1351:22]
  wire  n440_valid_down; // @[Top.scala 1351:22]
  wire [15:0] n440_I0; // @[Top.scala 1351:22]
  wire [15:0] n440_I1; // @[Top.scala 1351:22]
  wire [15:0] n440_O_t0b; // @[Top.scala 1351:22]
  wire [15:0] n440_O_t1b; // @[Top.scala 1351:22]
  wire  n441_valid_up; // @[Top.scala 1355:22]
  wire  n441_valid_down; // @[Top.scala 1355:22]
  wire  n441_I0; // @[Top.scala 1355:22]
  wire [15:0] n441_I1_t0b; // @[Top.scala 1355:22]
  wire [15:0] n441_I1_t1b; // @[Top.scala 1355:22]
  wire  n441_O_t0b; // @[Top.scala 1355:22]
  wire [15:0] n441_O_t1b_t0b; // @[Top.scala 1355:22]
  wire [15:0] n441_O_t1b_t1b; // @[Top.scala 1355:22]
  wire  n442_valid_up; // @[Top.scala 1359:22]
  wire  n442_valid_down; // @[Top.scala 1359:22]
  wire  n442_I_t0b; // @[Top.scala 1359:22]
  wire [15:0] n442_I_t1b_t0b; // @[Top.scala 1359:22]
  wire [15:0] n442_I_t1b_t1b; // @[Top.scala 1359:22]
  wire [15:0] n442_O; // @[Top.scala 1359:22]
  wire  n443_valid_up; // @[Top.scala 1362:22]
  wire  n443_valid_down; // @[Top.scala 1362:22]
  wire [15:0] n443_I0; // @[Top.scala 1362:22]
  wire [15:0] n443_I1; // @[Top.scala 1362:22]
  wire [15:0] n443_O_t0b; // @[Top.scala 1362:22]
  wire [15:0] n443_O_t1b; // @[Top.scala 1362:22]
  wire  n444_clock; // @[Top.scala 1366:22]
  wire  n444_reset; // @[Top.scala 1366:22]
  wire  n444_valid_up; // @[Top.scala 1366:22]
  wire  n444_valid_down; // @[Top.scala 1366:22]
  wire [15:0] n444_I_t0b; // @[Top.scala 1366:22]
  wire [15:0] n444_I_t1b; // @[Top.scala 1366:22]
  wire [15:0] n444_O; // @[Top.scala 1366:22]
  wire  n446_valid_up; // @[Top.scala 1369:22]
  wire  n446_valid_down; // @[Top.scala 1369:22]
  wire [15:0] n446_I0; // @[Top.scala 1369:22]
  wire [15:0] n446_I1; // @[Top.scala 1369:22]
  wire [15:0] n446_O_t0b; // @[Top.scala 1369:22]
  wire [15:0] n446_O_t1b; // @[Top.scala 1369:22]
  wire  n447_valid_up; // @[Top.scala 1373:22]
  wire  n447_valid_down; // @[Top.scala 1373:22]
  wire [15:0] n447_I_t0b; // @[Top.scala 1373:22]
  wire [15:0] n447_I_t1b; // @[Top.scala 1373:22]
  wire [15:0] n447_O; // @[Top.scala 1373:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n449_valid_up; // @[Top.scala 1377:22]
  wire  n449_valid_down; // @[Top.scala 1377:22]
  wire [15:0] n449_I0; // @[Top.scala 1377:22]
  wire [15:0] n449_I1; // @[Top.scala 1377:22]
  wire [15:0] n449_O_t0b; // @[Top.scala 1377:22]
  wire [15:0] n449_O_t1b; // @[Top.scala 1377:22]
  wire  n450_valid_up; // @[Top.scala 1381:22]
  wire  n450_valid_down; // @[Top.scala 1381:22]
  wire [15:0] n450_I_t0b; // @[Top.scala 1381:22]
  wire [15:0] n450_I_t1b; // @[Top.scala 1381:22]
  wire [15:0] n450_O; // @[Top.scala 1381:22]
  wire  n451_valid_up; // @[Top.scala 1384:22]
  wire  n451_valid_down; // @[Top.scala 1384:22]
  wire [15:0] n451_I0; // @[Top.scala 1384:22]
  wire [15:0] n451_I1; // @[Top.scala 1384:22]
  wire [15:0] n451_O_t0b; // @[Top.scala 1384:22]
  wire [15:0] n451_O_t1b; // @[Top.scala 1384:22]
  wire  n452_valid_up; // @[Top.scala 1388:22]
  wire  n452_valid_down; // @[Top.scala 1388:22]
  wire [15:0] n452_I0; // @[Top.scala 1388:22]
  wire [15:0] n452_I1; // @[Top.scala 1388:22]
  wire [15:0] n452_O_t0b; // @[Top.scala 1388:22]
  wire [15:0] n452_O_t1b; // @[Top.scala 1388:22]
  wire  n453_valid_up; // @[Top.scala 1392:22]
  wire  n453_valid_down; // @[Top.scala 1392:22]
  wire [15:0] n453_I0_t0b; // @[Top.scala 1392:22]
  wire [15:0] n453_I0_t1b; // @[Top.scala 1392:22]
  wire [15:0] n453_I1_t0b; // @[Top.scala 1392:22]
  wire [15:0] n453_I1_t1b; // @[Top.scala 1392:22]
  wire [15:0] n453_O_t0b_t0b; // @[Top.scala 1392:22]
  wire [15:0] n453_O_t0b_t1b; // @[Top.scala 1392:22]
  wire [15:0] n453_O_t1b_t0b; // @[Top.scala 1392:22]
  wire [15:0] n453_O_t1b_t1b; // @[Top.scala 1392:22]
  wire  n454_clock; // @[Top.scala 1396:22]
  wire  n454_reset; // @[Top.scala 1396:22]
  wire  n454_valid_up; // @[Top.scala 1396:22]
  wire  n454_valid_down; // @[Top.scala 1396:22]
  wire [15:0] n454_I_t0b_t0b; // @[Top.scala 1396:22]
  wire [15:0] n454_I_t0b_t1b; // @[Top.scala 1396:22]
  wire [15:0] n454_I_t1b_t0b; // @[Top.scala 1396:22]
  wire [15:0] n454_I_t1b_t1b; // @[Top.scala 1396:22]
  wire [15:0] n454_O_t0b_t0b; // @[Top.scala 1396:22]
  wire [15:0] n454_O_t0b_t1b; // @[Top.scala 1396:22]
  wire [15:0] n454_O_t1b_t0b; // @[Top.scala 1396:22]
  wire [15:0] n454_O_t1b_t1b; // @[Top.scala 1396:22]
  wire  n455_valid_up; // @[Top.scala 1399:22]
  wire  n455_valid_down; // @[Top.scala 1399:22]
  wire  n455_I0; // @[Top.scala 1399:22]
  wire [15:0] n455_I1_t0b_t0b; // @[Top.scala 1399:22]
  wire [15:0] n455_I1_t0b_t1b; // @[Top.scala 1399:22]
  wire [15:0] n455_I1_t1b_t0b; // @[Top.scala 1399:22]
  wire [15:0] n455_I1_t1b_t1b; // @[Top.scala 1399:22]
  wire  n455_O_t0b; // @[Top.scala 1399:22]
  wire [15:0] n455_O_t1b_t0b_t0b; // @[Top.scala 1399:22]
  wire [15:0] n455_O_t1b_t0b_t1b; // @[Top.scala 1399:22]
  wire [15:0] n455_O_t1b_t1b_t0b; // @[Top.scala 1399:22]
  wire [15:0] n455_O_t1b_t1b_t1b; // @[Top.scala 1399:22]
  wire  n456_valid_up; // @[Top.scala 1403:22]
  wire  n456_valid_down; // @[Top.scala 1403:22]
  wire  n456_I_t0b; // @[Top.scala 1403:22]
  wire [15:0] n456_I_t1b_t0b_t0b; // @[Top.scala 1403:22]
  wire [15:0] n456_I_t1b_t0b_t1b; // @[Top.scala 1403:22]
  wire [15:0] n456_I_t1b_t1b_t0b; // @[Top.scala 1403:22]
  wire [15:0] n456_I_t1b_t1b_t1b; // @[Top.scala 1403:22]
  wire [15:0] n456_O_t0b; // @[Top.scala 1403:22]
  wire [15:0] n456_O_t1b; // @[Top.scala 1403:22]
  wire  n458_valid_up; // @[Top.scala 1406:22]
  wire  n458_valid_down; // @[Top.scala 1406:22]
  wire [15:0] n458_I0; // @[Top.scala 1406:22]
  wire [15:0] n458_I1_t0b; // @[Top.scala 1406:22]
  wire [15:0] n458_I1_t1b; // @[Top.scala 1406:22]
  wire [15:0] n458_O_t0b; // @[Top.scala 1406:22]
  wire [15:0] n458_O_t1b_t0b; // @[Top.scala 1406:22]
  wire [15:0] n458_O_t1b_t1b; // @[Top.scala 1406:22]
  Fst n424 ( // @[Top.scala 1303:22]
    .valid_up(n424_valid_up),
    .valid_down(n424_valid_down),
    .I_t0b(n424_I_t0b),
    .O(n424_O)
  );
  FIFO_1 n457 ( // @[Top.scala 1306:22]
    .clock(n457_clock),
    .reset(n457_reset),
    .valid_up(n457_valid_up),
    .valid_down(n457_valid_down),
    .I(n457_I),
    .O(n457_O)
  );
  FIFO_1 n445 ( // @[Top.scala 1309:22]
    .clock(n445_clock),
    .reset(n445_reset),
    .valid_up(n445_valid_up),
    .valid_down(n445_valid_down),
    .I(n445_I),
    .O(n445_O)
  );
  Snd n425 ( // @[Top.scala 1312:22]
    .valid_up(n425_valid_up),
    .valid_down(n425_valid_down),
    .I_t1b_t0b(n425_I_t1b_t0b),
    .I_t1b_t1b(n425_I_t1b_t1b),
    .O_t0b(n425_O_t0b),
    .O_t1b(n425_O_t1b)
  );
  Fst_1 n426 ( // @[Top.scala 1315:22]
    .valid_up(n426_valid_up),
    .valid_down(n426_valid_down),
    .I_t0b(n426_I_t0b),
    .O(n426_O)
  );
  Snd_1 n427 ( // @[Top.scala 1318:22]
    .valid_up(n427_valid_up),
    .valid_down(n427_valid_down),
    .I_t1b(n427_I_t1b),
    .O(n427_O)
  );
  AtomTuple_1 n428 ( // @[Top.scala 1321:22]
    .valid_up(n428_valid_up),
    .valid_down(n428_valid_down),
    .I0(n428_I0),
    .I1(n428_I1),
    .O_t0b(n428_O_t0b),
    .O_t1b(n428_O_t1b)
  );
  Add n429 ( // @[Top.scala 1325:22]
    .valid_up(n429_valid_up),
    .valid_down(n429_valid_down),
    .I_t0b(n429_I_t0b),
    .I_t1b(n429_I_t1b),
    .O(n429_O)
  );
  InitialDelayCounter_36 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n432 ( // @[Top.scala 1329:22]
    .valid_up(n432_valid_up),
    .valid_down(n432_valid_down),
    .I0(n432_I0),
    .O_t0b(n432_O_t0b)
  );
  RShift n433 ( // @[Top.scala 1333:22]
    .valid_up(n433_valid_up),
    .valid_down(n433_valid_down),
    .I_t0b(n433_I_t0b),
    .O(n433_O)
  );
  AtomTuple_1 n434 ( // @[Top.scala 1336:22]
    .valid_up(n434_valid_up),
    .valid_down(n434_valid_down),
    .I0(n434_I0),
    .I1(n434_I1),
    .O_t0b(n434_O_t0b),
    .O_t1b(n434_O_t1b)
  );
  Eq n435 ( // @[Top.scala 1340:22]
    .valid_up(n435_valid_up),
    .valid_down(n435_valid_down),
    .I_t0b(n435_I_t0b),
    .I_t1b(n435_I_t1b),
    .O(n435_O)
  );
  InitialDelayCounter_36 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n438 ( // @[Top.scala 1344:22]
    .valid_up(n438_valid_up),
    .valid_down(n438_valid_down),
    .I0(n438_I0),
    .I1(n438_I1),
    .O_t0b(n438_O_t0b),
    .O_t1b(n438_O_t1b)
  );
  Add n439 ( // @[Top.scala 1348:22]
    .valid_up(n439_valid_up),
    .valid_down(n439_valid_down),
    .I_t0b(n439_I_t0b),
    .I_t1b(n439_I_t1b),
    .O(n439_O)
  );
  AtomTuple_1 n440 ( // @[Top.scala 1351:22]
    .valid_up(n440_valid_up),
    .valid_down(n440_valid_down),
    .I0(n440_I0),
    .I1(n440_I1),
    .O_t0b(n440_O_t0b),
    .O_t1b(n440_O_t1b)
  );
  AtomTuple_9 n441 ( // @[Top.scala 1355:22]
    .valid_up(n441_valid_up),
    .valid_down(n441_valid_down),
    .I0(n441_I0),
    .I1_t0b(n441_I1_t0b),
    .I1_t1b(n441_I1_t1b),
    .O_t0b(n441_O_t0b),
    .O_t1b_t0b(n441_O_t1b_t0b),
    .O_t1b_t1b(n441_O_t1b_t1b)
  );
  If n442 ( // @[Top.scala 1359:22]
    .valid_up(n442_valid_up),
    .valid_down(n442_valid_down),
    .I_t0b(n442_I_t0b),
    .I_t1b_t0b(n442_I_t1b_t0b),
    .I_t1b_t1b(n442_I_t1b_t1b),
    .O(n442_O)
  );
  AtomTuple_1 n443 ( // @[Top.scala 1362:22]
    .valid_up(n443_valid_up),
    .valid_down(n443_valid_down),
    .I0(n443_I0),
    .I1(n443_I1),
    .O_t0b(n443_O_t0b),
    .O_t1b(n443_O_t1b)
  );
  Mul n444 ( // @[Top.scala 1366:22]
    .clock(n444_clock),
    .reset(n444_reset),
    .valid_up(n444_valid_up),
    .valid_down(n444_valid_down),
    .I_t0b(n444_I_t0b),
    .I_t1b(n444_I_t1b),
    .O(n444_O)
  );
  AtomTuple_1 n446 ( // @[Top.scala 1369:22]
    .valid_up(n446_valid_up),
    .valid_down(n446_valid_down),
    .I0(n446_I0),
    .I1(n446_I1),
    .O_t0b(n446_O_t0b),
    .O_t1b(n446_O_t1b)
  );
  Lt n447 ( // @[Top.scala 1373:22]
    .valid_up(n447_valid_up),
    .valid_down(n447_valid_down),
    .I_t0b(n447_I_t0b),
    .I_t1b(n447_I_t1b),
    .O(n447_O)
  );
  InitialDelayCounter_36 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n449 ( // @[Top.scala 1377:22]
    .valid_up(n449_valid_up),
    .valid_down(n449_valid_down),
    .I0(n449_I0),
    .I1(n449_I1),
    .O_t0b(n449_O_t0b),
    .O_t1b(n449_O_t1b)
  );
  Sub n450 ( // @[Top.scala 1381:22]
    .valid_up(n450_valid_up),
    .valid_down(n450_valid_down),
    .I_t0b(n450_I_t0b),
    .I_t1b(n450_I_t1b),
    .O(n450_O)
  );
  AtomTuple_1 n451 ( // @[Top.scala 1384:22]
    .valid_up(n451_valid_up),
    .valid_down(n451_valid_down),
    .I0(n451_I0),
    .I1(n451_I1),
    .O_t0b(n451_O_t0b),
    .O_t1b(n451_O_t1b)
  );
  AtomTuple_1 n452 ( // @[Top.scala 1388:22]
    .valid_up(n452_valid_up),
    .valid_down(n452_valid_down),
    .I0(n452_I0),
    .I1(n452_I1),
    .O_t0b(n452_O_t0b),
    .O_t1b(n452_O_t1b)
  );
  AtomTuple_15 n453 ( // @[Top.scala 1392:22]
    .valid_up(n453_valid_up),
    .valid_down(n453_valid_down),
    .I0_t0b(n453_I0_t0b),
    .I0_t1b(n453_I0_t1b),
    .I1_t0b(n453_I1_t0b),
    .I1_t1b(n453_I1_t1b),
    .O_t0b_t0b(n453_O_t0b_t0b),
    .O_t0b_t1b(n453_O_t0b_t1b),
    .O_t1b_t0b(n453_O_t1b_t0b),
    .O_t1b_t1b(n453_O_t1b_t1b)
  );
  FIFO_3 n454 ( // @[Top.scala 1396:22]
    .clock(n454_clock),
    .reset(n454_reset),
    .valid_up(n454_valid_up),
    .valid_down(n454_valid_down),
    .I_t0b_t0b(n454_I_t0b_t0b),
    .I_t0b_t1b(n454_I_t0b_t1b),
    .I_t1b_t0b(n454_I_t1b_t0b),
    .I_t1b_t1b(n454_I_t1b_t1b),
    .O_t0b_t0b(n454_O_t0b_t0b),
    .O_t0b_t1b(n454_O_t0b_t1b),
    .O_t1b_t0b(n454_O_t1b_t0b),
    .O_t1b_t1b(n454_O_t1b_t1b)
  );
  AtomTuple_16 n455 ( // @[Top.scala 1399:22]
    .valid_up(n455_valid_up),
    .valid_down(n455_valid_down),
    .I0(n455_I0),
    .I1_t0b_t0b(n455_I1_t0b_t0b),
    .I1_t0b_t1b(n455_I1_t0b_t1b),
    .I1_t1b_t0b(n455_I1_t1b_t0b),
    .I1_t1b_t1b(n455_I1_t1b_t1b),
    .O_t0b(n455_O_t0b),
    .O_t1b_t0b_t0b(n455_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n455_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n455_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n455_O_t1b_t1b_t1b)
  );
  If_1 n456 ( // @[Top.scala 1403:22]
    .valid_up(n456_valid_up),
    .valid_down(n456_valid_down),
    .I_t0b(n456_I_t0b),
    .I_t1b_t0b_t0b(n456_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n456_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n456_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n456_I_t1b_t1b_t1b),
    .O_t0b(n456_O_t0b),
    .O_t1b(n456_O_t1b)
  );
  AtomTuple_3 n458 ( // @[Top.scala 1406:22]
    .valid_up(n458_valid_up),
    .valid_down(n458_valid_down),
    .I0(n458_I0),
    .I1_t0b(n458_I1_t0b),
    .I1_t1b(n458_I1_t1b),
    .O_t0b(n458_O_t0b),
    .O_t1b_t0b(n458_O_t1b_t0b),
    .O_t1b_t1b(n458_O_t1b_t1b)
  );
  assign valid_down = n458_valid_down; // @[Top.scala 1411:16]
  assign O_t0b = n458_O_t0b; // @[Top.scala 1410:7]
  assign O_t1b_t0b = n458_O_t1b_t0b; // @[Top.scala 1410:7]
  assign O_t1b_t1b = n458_O_t1b_t1b; // @[Top.scala 1410:7]
  assign n424_valid_up = valid_up; // @[Top.scala 1305:19]
  assign n424_I_t0b = I_t0b; // @[Top.scala 1304:12]
  assign n457_clock = clock;
  assign n457_reset = reset;
  assign n457_valid_up = n424_valid_down; // @[Top.scala 1308:19]
  assign n457_I = n424_O; // @[Top.scala 1307:12]
  assign n445_clock = clock;
  assign n445_reset = reset;
  assign n445_valid_up = n424_valid_down; // @[Top.scala 1311:19]
  assign n445_I = n424_O; // @[Top.scala 1310:12]
  assign n425_valid_up = valid_up; // @[Top.scala 1314:19]
  assign n425_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1313:12]
  assign n425_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1313:12]
  assign n426_valid_up = n425_valid_down; // @[Top.scala 1317:19]
  assign n426_I_t0b = n425_O_t0b; // @[Top.scala 1316:12]
  assign n427_valid_up = n425_valid_down; // @[Top.scala 1320:19]
  assign n427_I_t1b = n425_O_t1b; // @[Top.scala 1319:12]
  assign n428_valid_up = n426_valid_down & n427_valid_down; // @[Top.scala 1324:19]
  assign n428_I0 = n426_O; // @[Top.scala 1322:13]
  assign n428_I1 = n427_O; // @[Top.scala 1323:13]
  assign n429_valid_up = n428_valid_down; // @[Top.scala 1327:19]
  assign n429_I_t0b = n428_O_t0b; // @[Top.scala 1326:12]
  assign n429_I_t1b = n428_O_t1b; // @[Top.scala 1326:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n432_valid_up = n429_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 1332:19]
  assign n432_I0 = n429_O; // @[Top.scala 1330:13]
  assign n433_valid_up = n432_valid_down; // @[Top.scala 1335:19]
  assign n433_I_t0b = n432_O_t0b; // @[Top.scala 1334:12]
  assign n434_valid_up = n433_valid_down & n426_valid_down; // @[Top.scala 1339:19]
  assign n434_I0 = n433_O; // @[Top.scala 1337:13]
  assign n434_I1 = n426_O; // @[Top.scala 1338:13]
  assign n435_valid_up = n434_valid_down; // @[Top.scala 1342:19]
  assign n435_I_t0b = n434_O_t0b; // @[Top.scala 1341:12]
  assign n435_I_t1b = n434_O_t1b; // @[Top.scala 1341:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n438_valid_up = n433_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1347:19]
  assign n438_I0 = n433_O; // @[Top.scala 1345:13]
  assign n438_I1 = 16'h1; // @[Top.scala 1346:13]
  assign n439_valid_up = n438_valid_down; // @[Top.scala 1350:19]
  assign n439_I_t0b = n438_O_t0b; // @[Top.scala 1349:12]
  assign n439_I_t1b = n438_O_t1b; // @[Top.scala 1349:12]
  assign n440_valid_up = n439_valid_down & n433_valid_down; // @[Top.scala 1354:19]
  assign n440_I0 = n439_O; // @[Top.scala 1352:13]
  assign n440_I1 = n433_O; // @[Top.scala 1353:13]
  assign n441_valid_up = n435_valid_down & n440_valid_down; // @[Top.scala 1358:19]
  assign n441_I0 = n435_O[0]; // @[Top.scala 1356:13]
  assign n441_I1_t0b = n440_O_t0b; // @[Top.scala 1357:13]
  assign n441_I1_t1b = n440_O_t1b; // @[Top.scala 1357:13]
  assign n442_valid_up = n441_valid_down; // @[Top.scala 1361:19]
  assign n442_I_t0b = n441_O_t0b; // @[Top.scala 1360:12]
  assign n442_I_t1b_t0b = n441_O_t1b_t0b; // @[Top.scala 1360:12]
  assign n442_I_t1b_t1b = n441_O_t1b_t1b; // @[Top.scala 1360:12]
  assign n443_valid_up = n442_valid_down; // @[Top.scala 1365:19]
  assign n443_I0 = n442_O; // @[Top.scala 1363:13]
  assign n443_I1 = n442_O; // @[Top.scala 1364:13]
  assign n444_clock = clock;
  assign n444_reset = reset;
  assign n444_valid_up = n443_valid_down; // @[Top.scala 1368:19]
  assign n444_I_t0b = n443_O_t0b; // @[Top.scala 1367:12]
  assign n444_I_t1b = n443_O_t1b; // @[Top.scala 1367:12]
  assign n446_valid_up = n445_valid_down & n444_valid_down; // @[Top.scala 1372:19]
  assign n446_I0 = n445_O; // @[Top.scala 1370:13]
  assign n446_I1 = n444_O; // @[Top.scala 1371:13]
  assign n447_valid_up = n446_valid_down; // @[Top.scala 1375:19]
  assign n447_I_t0b = n446_O_t0b; // @[Top.scala 1374:12]
  assign n447_I_t1b = n446_O_t1b; // @[Top.scala 1374:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n449_valid_up = n442_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1380:19]
  assign n449_I0 = n442_O; // @[Top.scala 1378:13]
  assign n449_I1 = 16'h1; // @[Top.scala 1379:13]
  assign n450_valid_up = n449_valid_down; // @[Top.scala 1383:19]
  assign n450_I_t0b = n449_O_t0b; // @[Top.scala 1382:12]
  assign n450_I_t1b = n449_O_t1b; // @[Top.scala 1382:12]
  assign n451_valid_up = n426_valid_down & n450_valid_down; // @[Top.scala 1387:19]
  assign n451_I0 = n426_O; // @[Top.scala 1385:13]
  assign n451_I1 = n450_O; // @[Top.scala 1386:13]
  assign n452_valid_up = n442_valid_down & n427_valid_down; // @[Top.scala 1391:19]
  assign n452_I0 = n442_O; // @[Top.scala 1389:13]
  assign n452_I1 = n427_O; // @[Top.scala 1390:13]
  assign n453_valid_up = n451_valid_down & n452_valid_down; // @[Top.scala 1395:19]
  assign n453_I0_t0b = n451_O_t0b; // @[Top.scala 1393:13]
  assign n453_I0_t1b = n451_O_t1b; // @[Top.scala 1393:13]
  assign n453_I1_t0b = n452_O_t0b; // @[Top.scala 1394:13]
  assign n453_I1_t1b = n452_O_t1b; // @[Top.scala 1394:13]
  assign n454_clock = clock;
  assign n454_reset = reset;
  assign n454_valid_up = n453_valid_down; // @[Top.scala 1398:19]
  assign n454_I_t0b_t0b = n453_O_t0b_t0b; // @[Top.scala 1397:12]
  assign n454_I_t0b_t1b = n453_O_t0b_t1b; // @[Top.scala 1397:12]
  assign n454_I_t1b_t0b = n453_O_t1b_t0b; // @[Top.scala 1397:12]
  assign n454_I_t1b_t1b = n453_O_t1b_t1b; // @[Top.scala 1397:12]
  assign n455_valid_up = n447_valid_down & n454_valid_down; // @[Top.scala 1402:19]
  assign n455_I0 = n447_O[0]; // @[Top.scala 1400:13]
  assign n455_I1_t0b_t0b = n454_O_t0b_t0b; // @[Top.scala 1401:13]
  assign n455_I1_t0b_t1b = n454_O_t0b_t1b; // @[Top.scala 1401:13]
  assign n455_I1_t1b_t0b = n454_O_t1b_t0b; // @[Top.scala 1401:13]
  assign n455_I1_t1b_t1b = n454_O_t1b_t1b; // @[Top.scala 1401:13]
  assign n456_valid_up = n455_valid_down; // @[Top.scala 1405:19]
  assign n456_I_t0b = n455_O_t0b; // @[Top.scala 1404:12]
  assign n456_I_t1b_t0b_t0b = n455_O_t1b_t0b_t0b; // @[Top.scala 1404:12]
  assign n456_I_t1b_t0b_t1b = n455_O_t1b_t0b_t1b; // @[Top.scala 1404:12]
  assign n456_I_t1b_t1b_t0b = n455_O_t1b_t1b_t0b; // @[Top.scala 1404:12]
  assign n456_I_t1b_t1b_t1b = n455_O_t1b_t1b_t1b; // @[Top.scala 1404:12]
  assign n458_valid_up = n457_valid_down & n456_valid_down; // @[Top.scala 1409:19]
  assign n458_I0 = n457_O; // @[Top.scala 1407:13]
  assign n458_I1_t0b = n456_O_t0b; // @[Top.scala 1408:13]
  assign n458_I1_t1b = n456_O_t1b; // @[Top.scala 1408:13]
endmodule
module MapT_12(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_12 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
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
  wire  n461_valid_up; // @[Top.scala 1417:22]
  wire  n461_valid_down; // @[Top.scala 1417:22]
  wire [15:0] n461_I_t0b; // @[Top.scala 1417:22]
  wire [15:0] n461_O; // @[Top.scala 1417:22]
  wire  n494_clock; // @[Top.scala 1420:22]
  wire  n494_reset; // @[Top.scala 1420:22]
  wire  n494_valid_up; // @[Top.scala 1420:22]
  wire  n494_valid_down; // @[Top.scala 1420:22]
  wire [15:0] n494_I; // @[Top.scala 1420:22]
  wire [15:0] n494_O; // @[Top.scala 1420:22]
  wire  n482_clock; // @[Top.scala 1423:22]
  wire  n482_reset; // @[Top.scala 1423:22]
  wire  n482_valid_up; // @[Top.scala 1423:22]
  wire  n482_valid_down; // @[Top.scala 1423:22]
  wire [15:0] n482_I; // @[Top.scala 1423:22]
  wire [15:0] n482_O; // @[Top.scala 1423:22]
  wire  n462_valid_up; // @[Top.scala 1426:22]
  wire  n462_valid_down; // @[Top.scala 1426:22]
  wire [15:0] n462_I_t1b_t0b; // @[Top.scala 1426:22]
  wire [15:0] n462_I_t1b_t1b; // @[Top.scala 1426:22]
  wire [15:0] n462_O_t0b; // @[Top.scala 1426:22]
  wire [15:0] n462_O_t1b; // @[Top.scala 1426:22]
  wire  n463_valid_up; // @[Top.scala 1429:22]
  wire  n463_valid_down; // @[Top.scala 1429:22]
  wire [15:0] n463_I_t0b; // @[Top.scala 1429:22]
  wire [15:0] n463_O; // @[Top.scala 1429:22]
  wire  n464_valid_up; // @[Top.scala 1432:22]
  wire  n464_valid_down; // @[Top.scala 1432:22]
  wire [15:0] n464_I_t1b; // @[Top.scala 1432:22]
  wire [15:0] n464_O; // @[Top.scala 1432:22]
  wire  n465_valid_up; // @[Top.scala 1435:22]
  wire  n465_valid_down; // @[Top.scala 1435:22]
  wire [15:0] n465_I0; // @[Top.scala 1435:22]
  wire [15:0] n465_I1; // @[Top.scala 1435:22]
  wire [15:0] n465_O_t0b; // @[Top.scala 1435:22]
  wire [15:0] n465_O_t1b; // @[Top.scala 1435:22]
  wire  n466_valid_up; // @[Top.scala 1439:22]
  wire  n466_valid_down; // @[Top.scala 1439:22]
  wire [15:0] n466_I_t0b; // @[Top.scala 1439:22]
  wire [15:0] n466_I_t1b; // @[Top.scala 1439:22]
  wire [15:0] n466_O; // @[Top.scala 1439:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n469_valid_up; // @[Top.scala 1443:22]
  wire  n469_valid_down; // @[Top.scala 1443:22]
  wire [15:0] n469_I0; // @[Top.scala 1443:22]
  wire [15:0] n469_O_t0b; // @[Top.scala 1443:22]
  wire  n470_valid_up; // @[Top.scala 1447:22]
  wire  n470_valid_down; // @[Top.scala 1447:22]
  wire [15:0] n470_I_t0b; // @[Top.scala 1447:22]
  wire [15:0] n470_O; // @[Top.scala 1447:22]
  wire  n471_valid_up; // @[Top.scala 1450:22]
  wire  n471_valid_down; // @[Top.scala 1450:22]
  wire [15:0] n471_I0; // @[Top.scala 1450:22]
  wire [15:0] n471_I1; // @[Top.scala 1450:22]
  wire [15:0] n471_O_t0b; // @[Top.scala 1450:22]
  wire [15:0] n471_O_t1b; // @[Top.scala 1450:22]
  wire  n472_valid_up; // @[Top.scala 1454:22]
  wire  n472_valid_down; // @[Top.scala 1454:22]
  wire [15:0] n472_I_t0b; // @[Top.scala 1454:22]
  wire [15:0] n472_I_t1b; // @[Top.scala 1454:22]
  wire [15:0] n472_O; // @[Top.scala 1454:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n475_valid_up; // @[Top.scala 1458:22]
  wire  n475_valid_down; // @[Top.scala 1458:22]
  wire [15:0] n475_I0; // @[Top.scala 1458:22]
  wire [15:0] n475_I1; // @[Top.scala 1458:22]
  wire [15:0] n475_O_t0b; // @[Top.scala 1458:22]
  wire [15:0] n475_O_t1b; // @[Top.scala 1458:22]
  wire  n476_valid_up; // @[Top.scala 1462:22]
  wire  n476_valid_down; // @[Top.scala 1462:22]
  wire [15:0] n476_I_t0b; // @[Top.scala 1462:22]
  wire [15:0] n476_I_t1b; // @[Top.scala 1462:22]
  wire [15:0] n476_O; // @[Top.scala 1462:22]
  wire  n477_valid_up; // @[Top.scala 1465:22]
  wire  n477_valid_down; // @[Top.scala 1465:22]
  wire [15:0] n477_I0; // @[Top.scala 1465:22]
  wire [15:0] n477_I1; // @[Top.scala 1465:22]
  wire [15:0] n477_O_t0b; // @[Top.scala 1465:22]
  wire [15:0] n477_O_t1b; // @[Top.scala 1465:22]
  wire  n478_valid_up; // @[Top.scala 1469:22]
  wire  n478_valid_down; // @[Top.scala 1469:22]
  wire  n478_I0; // @[Top.scala 1469:22]
  wire [15:0] n478_I1_t0b; // @[Top.scala 1469:22]
  wire [15:0] n478_I1_t1b; // @[Top.scala 1469:22]
  wire  n478_O_t0b; // @[Top.scala 1469:22]
  wire [15:0] n478_O_t1b_t0b; // @[Top.scala 1469:22]
  wire [15:0] n478_O_t1b_t1b; // @[Top.scala 1469:22]
  wire  n479_valid_up; // @[Top.scala 1473:22]
  wire  n479_valid_down; // @[Top.scala 1473:22]
  wire  n479_I_t0b; // @[Top.scala 1473:22]
  wire [15:0] n479_I_t1b_t0b; // @[Top.scala 1473:22]
  wire [15:0] n479_I_t1b_t1b; // @[Top.scala 1473:22]
  wire [15:0] n479_O; // @[Top.scala 1473:22]
  wire  n480_valid_up; // @[Top.scala 1476:22]
  wire  n480_valid_down; // @[Top.scala 1476:22]
  wire [15:0] n480_I0; // @[Top.scala 1476:22]
  wire [15:0] n480_I1; // @[Top.scala 1476:22]
  wire [15:0] n480_O_t0b; // @[Top.scala 1476:22]
  wire [15:0] n480_O_t1b; // @[Top.scala 1476:22]
  wire  n481_clock; // @[Top.scala 1480:22]
  wire  n481_reset; // @[Top.scala 1480:22]
  wire  n481_valid_up; // @[Top.scala 1480:22]
  wire  n481_valid_down; // @[Top.scala 1480:22]
  wire [15:0] n481_I_t0b; // @[Top.scala 1480:22]
  wire [15:0] n481_I_t1b; // @[Top.scala 1480:22]
  wire [15:0] n481_O; // @[Top.scala 1480:22]
  wire  n483_valid_up; // @[Top.scala 1483:22]
  wire  n483_valid_down; // @[Top.scala 1483:22]
  wire [15:0] n483_I0; // @[Top.scala 1483:22]
  wire [15:0] n483_I1; // @[Top.scala 1483:22]
  wire [15:0] n483_O_t0b; // @[Top.scala 1483:22]
  wire [15:0] n483_O_t1b; // @[Top.scala 1483:22]
  wire  n484_valid_up; // @[Top.scala 1487:22]
  wire  n484_valid_down; // @[Top.scala 1487:22]
  wire [15:0] n484_I_t0b; // @[Top.scala 1487:22]
  wire [15:0] n484_I_t1b; // @[Top.scala 1487:22]
  wire [15:0] n484_O; // @[Top.scala 1487:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n486_valid_up; // @[Top.scala 1491:22]
  wire  n486_valid_down; // @[Top.scala 1491:22]
  wire [15:0] n486_I0; // @[Top.scala 1491:22]
  wire [15:0] n486_I1; // @[Top.scala 1491:22]
  wire [15:0] n486_O_t0b; // @[Top.scala 1491:22]
  wire [15:0] n486_O_t1b; // @[Top.scala 1491:22]
  wire  n487_valid_up; // @[Top.scala 1495:22]
  wire  n487_valid_down; // @[Top.scala 1495:22]
  wire [15:0] n487_I_t0b; // @[Top.scala 1495:22]
  wire [15:0] n487_I_t1b; // @[Top.scala 1495:22]
  wire [15:0] n487_O; // @[Top.scala 1495:22]
  wire  n488_valid_up; // @[Top.scala 1498:22]
  wire  n488_valid_down; // @[Top.scala 1498:22]
  wire [15:0] n488_I0; // @[Top.scala 1498:22]
  wire [15:0] n488_I1; // @[Top.scala 1498:22]
  wire [15:0] n488_O_t0b; // @[Top.scala 1498:22]
  wire [15:0] n488_O_t1b; // @[Top.scala 1498:22]
  wire  n489_valid_up; // @[Top.scala 1502:22]
  wire  n489_valid_down; // @[Top.scala 1502:22]
  wire [15:0] n489_I0; // @[Top.scala 1502:22]
  wire [15:0] n489_I1; // @[Top.scala 1502:22]
  wire [15:0] n489_O_t0b; // @[Top.scala 1502:22]
  wire [15:0] n489_O_t1b; // @[Top.scala 1502:22]
  wire  n490_valid_up; // @[Top.scala 1506:22]
  wire  n490_valid_down; // @[Top.scala 1506:22]
  wire [15:0] n490_I0_t0b; // @[Top.scala 1506:22]
  wire [15:0] n490_I0_t1b; // @[Top.scala 1506:22]
  wire [15:0] n490_I1_t0b; // @[Top.scala 1506:22]
  wire [15:0] n490_I1_t1b; // @[Top.scala 1506:22]
  wire [15:0] n490_O_t0b_t0b; // @[Top.scala 1506:22]
  wire [15:0] n490_O_t0b_t1b; // @[Top.scala 1506:22]
  wire [15:0] n490_O_t1b_t0b; // @[Top.scala 1506:22]
  wire [15:0] n490_O_t1b_t1b; // @[Top.scala 1506:22]
  wire  n491_clock; // @[Top.scala 1510:22]
  wire  n491_reset; // @[Top.scala 1510:22]
  wire  n491_valid_up; // @[Top.scala 1510:22]
  wire  n491_valid_down; // @[Top.scala 1510:22]
  wire [15:0] n491_I_t0b_t0b; // @[Top.scala 1510:22]
  wire [15:0] n491_I_t0b_t1b; // @[Top.scala 1510:22]
  wire [15:0] n491_I_t1b_t0b; // @[Top.scala 1510:22]
  wire [15:0] n491_I_t1b_t1b; // @[Top.scala 1510:22]
  wire [15:0] n491_O_t0b_t0b; // @[Top.scala 1510:22]
  wire [15:0] n491_O_t0b_t1b; // @[Top.scala 1510:22]
  wire [15:0] n491_O_t1b_t0b; // @[Top.scala 1510:22]
  wire [15:0] n491_O_t1b_t1b; // @[Top.scala 1510:22]
  wire  n492_valid_up; // @[Top.scala 1513:22]
  wire  n492_valid_down; // @[Top.scala 1513:22]
  wire  n492_I0; // @[Top.scala 1513:22]
  wire [15:0] n492_I1_t0b_t0b; // @[Top.scala 1513:22]
  wire [15:0] n492_I1_t0b_t1b; // @[Top.scala 1513:22]
  wire [15:0] n492_I1_t1b_t0b; // @[Top.scala 1513:22]
  wire [15:0] n492_I1_t1b_t1b; // @[Top.scala 1513:22]
  wire  n492_O_t0b; // @[Top.scala 1513:22]
  wire [15:0] n492_O_t1b_t0b_t0b; // @[Top.scala 1513:22]
  wire [15:0] n492_O_t1b_t0b_t1b; // @[Top.scala 1513:22]
  wire [15:0] n492_O_t1b_t1b_t0b; // @[Top.scala 1513:22]
  wire [15:0] n492_O_t1b_t1b_t1b; // @[Top.scala 1513:22]
  wire  n493_valid_up; // @[Top.scala 1517:22]
  wire  n493_valid_down; // @[Top.scala 1517:22]
  wire  n493_I_t0b; // @[Top.scala 1517:22]
  wire [15:0] n493_I_t1b_t0b_t0b; // @[Top.scala 1517:22]
  wire [15:0] n493_I_t1b_t0b_t1b; // @[Top.scala 1517:22]
  wire [15:0] n493_I_t1b_t1b_t0b; // @[Top.scala 1517:22]
  wire [15:0] n493_I_t1b_t1b_t1b; // @[Top.scala 1517:22]
  wire [15:0] n493_O_t0b; // @[Top.scala 1517:22]
  wire [15:0] n493_O_t1b; // @[Top.scala 1517:22]
  wire  n495_valid_up; // @[Top.scala 1520:22]
  wire  n495_valid_down; // @[Top.scala 1520:22]
  wire [15:0] n495_I0; // @[Top.scala 1520:22]
  wire [15:0] n495_I1_t0b; // @[Top.scala 1520:22]
  wire [15:0] n495_I1_t1b; // @[Top.scala 1520:22]
  wire [15:0] n495_O_t0b; // @[Top.scala 1520:22]
  wire [15:0] n495_O_t1b_t0b; // @[Top.scala 1520:22]
  wire [15:0] n495_O_t1b_t1b; // @[Top.scala 1520:22]
  Fst n461 ( // @[Top.scala 1417:22]
    .valid_up(n461_valid_up),
    .valid_down(n461_valid_down),
    .I_t0b(n461_I_t0b),
    .O(n461_O)
  );
  FIFO_1 n494 ( // @[Top.scala 1420:22]
    .clock(n494_clock),
    .reset(n494_reset),
    .valid_up(n494_valid_up),
    .valid_down(n494_valid_down),
    .I(n494_I),
    .O(n494_O)
  );
  FIFO_1 n482 ( // @[Top.scala 1423:22]
    .clock(n482_clock),
    .reset(n482_reset),
    .valid_up(n482_valid_up),
    .valid_down(n482_valid_down),
    .I(n482_I),
    .O(n482_O)
  );
  Snd n462 ( // @[Top.scala 1426:22]
    .valid_up(n462_valid_up),
    .valid_down(n462_valid_down),
    .I_t1b_t0b(n462_I_t1b_t0b),
    .I_t1b_t1b(n462_I_t1b_t1b),
    .O_t0b(n462_O_t0b),
    .O_t1b(n462_O_t1b)
  );
  Fst_1 n463 ( // @[Top.scala 1429:22]
    .valid_up(n463_valid_up),
    .valid_down(n463_valid_down),
    .I_t0b(n463_I_t0b),
    .O(n463_O)
  );
  Snd_1 n464 ( // @[Top.scala 1432:22]
    .valid_up(n464_valid_up),
    .valid_down(n464_valid_down),
    .I_t1b(n464_I_t1b),
    .O(n464_O)
  );
  AtomTuple_1 n465 ( // @[Top.scala 1435:22]
    .valid_up(n465_valid_up),
    .valid_down(n465_valid_down),
    .I0(n465_I0),
    .I1(n465_I1),
    .O_t0b(n465_O_t0b),
    .O_t1b(n465_O_t1b)
  );
  Add n466 ( // @[Top.scala 1439:22]
    .valid_up(n466_valid_up),
    .valid_down(n466_valid_down),
    .I_t0b(n466_I_t0b),
    .I_t1b(n466_I_t1b),
    .O(n466_O)
  );
  InitialDelayCounter_39 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n469 ( // @[Top.scala 1443:22]
    .valid_up(n469_valid_up),
    .valid_down(n469_valid_down),
    .I0(n469_I0),
    .O_t0b(n469_O_t0b)
  );
  RShift n470 ( // @[Top.scala 1447:22]
    .valid_up(n470_valid_up),
    .valid_down(n470_valid_down),
    .I_t0b(n470_I_t0b),
    .O(n470_O)
  );
  AtomTuple_1 n471 ( // @[Top.scala 1450:22]
    .valid_up(n471_valid_up),
    .valid_down(n471_valid_down),
    .I0(n471_I0),
    .I1(n471_I1),
    .O_t0b(n471_O_t0b),
    .O_t1b(n471_O_t1b)
  );
  Eq n472 ( // @[Top.scala 1454:22]
    .valid_up(n472_valid_up),
    .valid_down(n472_valid_down),
    .I_t0b(n472_I_t0b),
    .I_t1b(n472_I_t1b),
    .O(n472_O)
  );
  InitialDelayCounter_39 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n475 ( // @[Top.scala 1458:22]
    .valid_up(n475_valid_up),
    .valid_down(n475_valid_down),
    .I0(n475_I0),
    .I1(n475_I1),
    .O_t0b(n475_O_t0b),
    .O_t1b(n475_O_t1b)
  );
  Add n476 ( // @[Top.scala 1462:22]
    .valid_up(n476_valid_up),
    .valid_down(n476_valid_down),
    .I_t0b(n476_I_t0b),
    .I_t1b(n476_I_t1b),
    .O(n476_O)
  );
  AtomTuple_1 n477 ( // @[Top.scala 1465:22]
    .valid_up(n477_valid_up),
    .valid_down(n477_valid_down),
    .I0(n477_I0),
    .I1(n477_I1),
    .O_t0b(n477_O_t0b),
    .O_t1b(n477_O_t1b)
  );
  AtomTuple_9 n478 ( // @[Top.scala 1469:22]
    .valid_up(n478_valid_up),
    .valid_down(n478_valid_down),
    .I0(n478_I0),
    .I1_t0b(n478_I1_t0b),
    .I1_t1b(n478_I1_t1b),
    .O_t0b(n478_O_t0b),
    .O_t1b_t0b(n478_O_t1b_t0b),
    .O_t1b_t1b(n478_O_t1b_t1b)
  );
  If n479 ( // @[Top.scala 1473:22]
    .valid_up(n479_valid_up),
    .valid_down(n479_valid_down),
    .I_t0b(n479_I_t0b),
    .I_t1b_t0b(n479_I_t1b_t0b),
    .I_t1b_t1b(n479_I_t1b_t1b),
    .O(n479_O)
  );
  AtomTuple_1 n480 ( // @[Top.scala 1476:22]
    .valid_up(n480_valid_up),
    .valid_down(n480_valid_down),
    .I0(n480_I0),
    .I1(n480_I1),
    .O_t0b(n480_O_t0b),
    .O_t1b(n480_O_t1b)
  );
  Mul n481 ( // @[Top.scala 1480:22]
    .clock(n481_clock),
    .reset(n481_reset),
    .valid_up(n481_valid_up),
    .valid_down(n481_valid_down),
    .I_t0b(n481_I_t0b),
    .I_t1b(n481_I_t1b),
    .O(n481_O)
  );
  AtomTuple_1 n483 ( // @[Top.scala 1483:22]
    .valid_up(n483_valid_up),
    .valid_down(n483_valid_down),
    .I0(n483_I0),
    .I1(n483_I1),
    .O_t0b(n483_O_t0b),
    .O_t1b(n483_O_t1b)
  );
  Lt n484 ( // @[Top.scala 1487:22]
    .valid_up(n484_valid_up),
    .valid_down(n484_valid_down),
    .I_t0b(n484_I_t0b),
    .I_t1b(n484_I_t1b),
    .O(n484_O)
  );
  InitialDelayCounter_39 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n486 ( // @[Top.scala 1491:22]
    .valid_up(n486_valid_up),
    .valid_down(n486_valid_down),
    .I0(n486_I0),
    .I1(n486_I1),
    .O_t0b(n486_O_t0b),
    .O_t1b(n486_O_t1b)
  );
  Sub n487 ( // @[Top.scala 1495:22]
    .valid_up(n487_valid_up),
    .valid_down(n487_valid_down),
    .I_t0b(n487_I_t0b),
    .I_t1b(n487_I_t1b),
    .O(n487_O)
  );
  AtomTuple_1 n488 ( // @[Top.scala 1498:22]
    .valid_up(n488_valid_up),
    .valid_down(n488_valid_down),
    .I0(n488_I0),
    .I1(n488_I1),
    .O_t0b(n488_O_t0b),
    .O_t1b(n488_O_t1b)
  );
  AtomTuple_1 n489 ( // @[Top.scala 1502:22]
    .valid_up(n489_valid_up),
    .valid_down(n489_valid_down),
    .I0(n489_I0),
    .I1(n489_I1),
    .O_t0b(n489_O_t0b),
    .O_t1b(n489_O_t1b)
  );
  AtomTuple_15 n490 ( // @[Top.scala 1506:22]
    .valid_up(n490_valid_up),
    .valid_down(n490_valid_down),
    .I0_t0b(n490_I0_t0b),
    .I0_t1b(n490_I0_t1b),
    .I1_t0b(n490_I1_t0b),
    .I1_t1b(n490_I1_t1b),
    .O_t0b_t0b(n490_O_t0b_t0b),
    .O_t0b_t1b(n490_O_t0b_t1b),
    .O_t1b_t0b(n490_O_t1b_t0b),
    .O_t1b_t1b(n490_O_t1b_t1b)
  );
  FIFO_3 n491 ( // @[Top.scala 1510:22]
    .clock(n491_clock),
    .reset(n491_reset),
    .valid_up(n491_valid_up),
    .valid_down(n491_valid_down),
    .I_t0b_t0b(n491_I_t0b_t0b),
    .I_t0b_t1b(n491_I_t0b_t1b),
    .I_t1b_t0b(n491_I_t1b_t0b),
    .I_t1b_t1b(n491_I_t1b_t1b),
    .O_t0b_t0b(n491_O_t0b_t0b),
    .O_t0b_t1b(n491_O_t0b_t1b),
    .O_t1b_t0b(n491_O_t1b_t0b),
    .O_t1b_t1b(n491_O_t1b_t1b)
  );
  AtomTuple_16 n492 ( // @[Top.scala 1513:22]
    .valid_up(n492_valid_up),
    .valid_down(n492_valid_down),
    .I0(n492_I0),
    .I1_t0b_t0b(n492_I1_t0b_t0b),
    .I1_t0b_t1b(n492_I1_t0b_t1b),
    .I1_t1b_t0b(n492_I1_t1b_t0b),
    .I1_t1b_t1b(n492_I1_t1b_t1b),
    .O_t0b(n492_O_t0b),
    .O_t1b_t0b_t0b(n492_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n492_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n492_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n492_O_t1b_t1b_t1b)
  );
  If_1 n493 ( // @[Top.scala 1517:22]
    .valid_up(n493_valid_up),
    .valid_down(n493_valid_down),
    .I_t0b(n493_I_t0b),
    .I_t1b_t0b_t0b(n493_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n493_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n493_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n493_I_t1b_t1b_t1b),
    .O_t0b(n493_O_t0b),
    .O_t1b(n493_O_t1b)
  );
  AtomTuple_3 n495 ( // @[Top.scala 1520:22]
    .valid_up(n495_valid_up),
    .valid_down(n495_valid_down),
    .I0(n495_I0),
    .I1_t0b(n495_I1_t0b),
    .I1_t1b(n495_I1_t1b),
    .O_t0b(n495_O_t0b),
    .O_t1b_t0b(n495_O_t1b_t0b),
    .O_t1b_t1b(n495_O_t1b_t1b)
  );
  assign valid_down = n495_valid_down; // @[Top.scala 1525:16]
  assign O_t0b = n495_O_t0b; // @[Top.scala 1524:7]
  assign O_t1b_t0b = n495_O_t1b_t0b; // @[Top.scala 1524:7]
  assign O_t1b_t1b = n495_O_t1b_t1b; // @[Top.scala 1524:7]
  assign n461_valid_up = valid_up; // @[Top.scala 1419:19]
  assign n461_I_t0b = I_t0b; // @[Top.scala 1418:12]
  assign n494_clock = clock;
  assign n494_reset = reset;
  assign n494_valid_up = n461_valid_down; // @[Top.scala 1422:19]
  assign n494_I = n461_O; // @[Top.scala 1421:12]
  assign n482_clock = clock;
  assign n482_reset = reset;
  assign n482_valid_up = n461_valid_down; // @[Top.scala 1425:19]
  assign n482_I = n461_O; // @[Top.scala 1424:12]
  assign n462_valid_up = valid_up; // @[Top.scala 1428:19]
  assign n462_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1427:12]
  assign n462_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1427:12]
  assign n463_valid_up = n462_valid_down; // @[Top.scala 1431:19]
  assign n463_I_t0b = n462_O_t0b; // @[Top.scala 1430:12]
  assign n464_valid_up = n462_valid_down; // @[Top.scala 1434:19]
  assign n464_I_t1b = n462_O_t1b; // @[Top.scala 1433:12]
  assign n465_valid_up = n463_valid_down & n464_valid_down; // @[Top.scala 1438:19]
  assign n465_I0 = n463_O; // @[Top.scala 1436:13]
  assign n465_I1 = n464_O; // @[Top.scala 1437:13]
  assign n466_valid_up = n465_valid_down; // @[Top.scala 1441:19]
  assign n466_I_t0b = n465_O_t0b; // @[Top.scala 1440:12]
  assign n466_I_t1b = n465_O_t1b; // @[Top.scala 1440:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n469_valid_up = n466_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 1446:19]
  assign n469_I0 = n466_O; // @[Top.scala 1444:13]
  assign n470_valid_up = n469_valid_down; // @[Top.scala 1449:19]
  assign n470_I_t0b = n469_O_t0b; // @[Top.scala 1448:12]
  assign n471_valid_up = n470_valid_down & n463_valid_down; // @[Top.scala 1453:19]
  assign n471_I0 = n470_O; // @[Top.scala 1451:13]
  assign n471_I1 = n463_O; // @[Top.scala 1452:13]
  assign n472_valid_up = n471_valid_down; // @[Top.scala 1456:19]
  assign n472_I_t0b = n471_O_t0b; // @[Top.scala 1455:12]
  assign n472_I_t1b = n471_O_t1b; // @[Top.scala 1455:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n475_valid_up = n470_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1461:19]
  assign n475_I0 = n470_O; // @[Top.scala 1459:13]
  assign n475_I1 = 16'h1; // @[Top.scala 1460:13]
  assign n476_valid_up = n475_valid_down; // @[Top.scala 1464:19]
  assign n476_I_t0b = n475_O_t0b; // @[Top.scala 1463:12]
  assign n476_I_t1b = n475_O_t1b; // @[Top.scala 1463:12]
  assign n477_valid_up = n476_valid_down & n470_valid_down; // @[Top.scala 1468:19]
  assign n477_I0 = n476_O; // @[Top.scala 1466:13]
  assign n477_I1 = n470_O; // @[Top.scala 1467:13]
  assign n478_valid_up = n472_valid_down & n477_valid_down; // @[Top.scala 1472:19]
  assign n478_I0 = n472_O[0]; // @[Top.scala 1470:13]
  assign n478_I1_t0b = n477_O_t0b; // @[Top.scala 1471:13]
  assign n478_I1_t1b = n477_O_t1b; // @[Top.scala 1471:13]
  assign n479_valid_up = n478_valid_down; // @[Top.scala 1475:19]
  assign n479_I_t0b = n478_O_t0b; // @[Top.scala 1474:12]
  assign n479_I_t1b_t0b = n478_O_t1b_t0b; // @[Top.scala 1474:12]
  assign n479_I_t1b_t1b = n478_O_t1b_t1b; // @[Top.scala 1474:12]
  assign n480_valid_up = n479_valid_down; // @[Top.scala 1479:19]
  assign n480_I0 = n479_O; // @[Top.scala 1477:13]
  assign n480_I1 = n479_O; // @[Top.scala 1478:13]
  assign n481_clock = clock;
  assign n481_reset = reset;
  assign n481_valid_up = n480_valid_down; // @[Top.scala 1482:19]
  assign n481_I_t0b = n480_O_t0b; // @[Top.scala 1481:12]
  assign n481_I_t1b = n480_O_t1b; // @[Top.scala 1481:12]
  assign n483_valid_up = n482_valid_down & n481_valid_down; // @[Top.scala 1486:19]
  assign n483_I0 = n482_O; // @[Top.scala 1484:13]
  assign n483_I1 = n481_O; // @[Top.scala 1485:13]
  assign n484_valid_up = n483_valid_down; // @[Top.scala 1489:19]
  assign n484_I_t0b = n483_O_t0b; // @[Top.scala 1488:12]
  assign n484_I_t1b = n483_O_t1b; // @[Top.scala 1488:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n486_valid_up = n479_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1494:19]
  assign n486_I0 = n479_O; // @[Top.scala 1492:13]
  assign n486_I1 = 16'h1; // @[Top.scala 1493:13]
  assign n487_valid_up = n486_valid_down; // @[Top.scala 1497:19]
  assign n487_I_t0b = n486_O_t0b; // @[Top.scala 1496:12]
  assign n487_I_t1b = n486_O_t1b; // @[Top.scala 1496:12]
  assign n488_valid_up = n463_valid_down & n487_valid_down; // @[Top.scala 1501:19]
  assign n488_I0 = n463_O; // @[Top.scala 1499:13]
  assign n488_I1 = n487_O; // @[Top.scala 1500:13]
  assign n489_valid_up = n479_valid_down & n464_valid_down; // @[Top.scala 1505:19]
  assign n489_I0 = n479_O; // @[Top.scala 1503:13]
  assign n489_I1 = n464_O; // @[Top.scala 1504:13]
  assign n490_valid_up = n488_valid_down & n489_valid_down; // @[Top.scala 1509:19]
  assign n490_I0_t0b = n488_O_t0b; // @[Top.scala 1507:13]
  assign n490_I0_t1b = n488_O_t1b; // @[Top.scala 1507:13]
  assign n490_I1_t0b = n489_O_t0b; // @[Top.scala 1508:13]
  assign n490_I1_t1b = n489_O_t1b; // @[Top.scala 1508:13]
  assign n491_clock = clock;
  assign n491_reset = reset;
  assign n491_valid_up = n490_valid_down; // @[Top.scala 1512:19]
  assign n491_I_t0b_t0b = n490_O_t0b_t0b; // @[Top.scala 1511:12]
  assign n491_I_t0b_t1b = n490_O_t0b_t1b; // @[Top.scala 1511:12]
  assign n491_I_t1b_t0b = n490_O_t1b_t0b; // @[Top.scala 1511:12]
  assign n491_I_t1b_t1b = n490_O_t1b_t1b; // @[Top.scala 1511:12]
  assign n492_valid_up = n484_valid_down & n491_valid_down; // @[Top.scala 1516:19]
  assign n492_I0 = n484_O[0]; // @[Top.scala 1514:13]
  assign n492_I1_t0b_t0b = n491_O_t0b_t0b; // @[Top.scala 1515:13]
  assign n492_I1_t0b_t1b = n491_O_t0b_t1b; // @[Top.scala 1515:13]
  assign n492_I1_t1b_t0b = n491_O_t1b_t0b; // @[Top.scala 1515:13]
  assign n492_I1_t1b_t1b = n491_O_t1b_t1b; // @[Top.scala 1515:13]
  assign n493_valid_up = n492_valid_down; // @[Top.scala 1519:19]
  assign n493_I_t0b = n492_O_t0b; // @[Top.scala 1518:12]
  assign n493_I_t1b_t0b_t0b = n492_O_t1b_t0b_t0b; // @[Top.scala 1518:12]
  assign n493_I_t1b_t0b_t1b = n492_O_t1b_t0b_t1b; // @[Top.scala 1518:12]
  assign n493_I_t1b_t1b_t0b = n492_O_t1b_t1b_t0b; // @[Top.scala 1518:12]
  assign n493_I_t1b_t1b_t1b = n492_O_t1b_t1b_t1b; // @[Top.scala 1518:12]
  assign n495_valid_up = n494_valid_down & n493_valid_down; // @[Top.scala 1523:19]
  assign n495_I0 = n494_O; // @[Top.scala 1521:13]
  assign n495_I1_t0b = n493_O_t0b; // @[Top.scala 1522:13]
  assign n495_I1_t1b = n493_O_t1b; // @[Top.scala 1522:13]
endmodule
module MapT_13(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_13 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
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
  wire  n498_valid_up; // @[Top.scala 1531:22]
  wire  n498_valid_down; // @[Top.scala 1531:22]
  wire [15:0] n498_I_t0b; // @[Top.scala 1531:22]
  wire [15:0] n498_O; // @[Top.scala 1531:22]
  wire  n531_clock; // @[Top.scala 1534:22]
  wire  n531_reset; // @[Top.scala 1534:22]
  wire  n531_valid_up; // @[Top.scala 1534:22]
  wire  n531_valid_down; // @[Top.scala 1534:22]
  wire [15:0] n531_I; // @[Top.scala 1534:22]
  wire [15:0] n531_O; // @[Top.scala 1534:22]
  wire  n519_clock; // @[Top.scala 1537:22]
  wire  n519_reset; // @[Top.scala 1537:22]
  wire  n519_valid_up; // @[Top.scala 1537:22]
  wire  n519_valid_down; // @[Top.scala 1537:22]
  wire [15:0] n519_I; // @[Top.scala 1537:22]
  wire [15:0] n519_O; // @[Top.scala 1537:22]
  wire  n499_valid_up; // @[Top.scala 1540:22]
  wire  n499_valid_down; // @[Top.scala 1540:22]
  wire [15:0] n499_I_t1b_t0b; // @[Top.scala 1540:22]
  wire [15:0] n499_I_t1b_t1b; // @[Top.scala 1540:22]
  wire [15:0] n499_O_t0b; // @[Top.scala 1540:22]
  wire [15:0] n499_O_t1b; // @[Top.scala 1540:22]
  wire  n500_valid_up; // @[Top.scala 1543:22]
  wire  n500_valid_down; // @[Top.scala 1543:22]
  wire [15:0] n500_I_t0b; // @[Top.scala 1543:22]
  wire [15:0] n500_O; // @[Top.scala 1543:22]
  wire  n501_valid_up; // @[Top.scala 1546:22]
  wire  n501_valid_down; // @[Top.scala 1546:22]
  wire [15:0] n501_I_t1b; // @[Top.scala 1546:22]
  wire [15:0] n501_O; // @[Top.scala 1546:22]
  wire  n502_valid_up; // @[Top.scala 1549:22]
  wire  n502_valid_down; // @[Top.scala 1549:22]
  wire [15:0] n502_I0; // @[Top.scala 1549:22]
  wire [15:0] n502_I1; // @[Top.scala 1549:22]
  wire [15:0] n502_O_t0b; // @[Top.scala 1549:22]
  wire [15:0] n502_O_t1b; // @[Top.scala 1549:22]
  wire  n503_valid_up; // @[Top.scala 1553:22]
  wire  n503_valid_down; // @[Top.scala 1553:22]
  wire [15:0] n503_I_t0b; // @[Top.scala 1553:22]
  wire [15:0] n503_I_t1b; // @[Top.scala 1553:22]
  wire [15:0] n503_O; // @[Top.scala 1553:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n506_valid_up; // @[Top.scala 1557:22]
  wire  n506_valid_down; // @[Top.scala 1557:22]
  wire [15:0] n506_I0; // @[Top.scala 1557:22]
  wire [15:0] n506_O_t0b; // @[Top.scala 1557:22]
  wire  n507_valid_up; // @[Top.scala 1561:22]
  wire  n507_valid_down; // @[Top.scala 1561:22]
  wire [15:0] n507_I_t0b; // @[Top.scala 1561:22]
  wire [15:0] n507_O; // @[Top.scala 1561:22]
  wire  n508_valid_up; // @[Top.scala 1564:22]
  wire  n508_valid_down; // @[Top.scala 1564:22]
  wire [15:0] n508_I0; // @[Top.scala 1564:22]
  wire [15:0] n508_I1; // @[Top.scala 1564:22]
  wire [15:0] n508_O_t0b; // @[Top.scala 1564:22]
  wire [15:0] n508_O_t1b; // @[Top.scala 1564:22]
  wire  n509_valid_up; // @[Top.scala 1568:22]
  wire  n509_valid_down; // @[Top.scala 1568:22]
  wire [15:0] n509_I_t0b; // @[Top.scala 1568:22]
  wire [15:0] n509_I_t1b; // @[Top.scala 1568:22]
  wire [15:0] n509_O; // @[Top.scala 1568:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n512_valid_up; // @[Top.scala 1572:22]
  wire  n512_valid_down; // @[Top.scala 1572:22]
  wire [15:0] n512_I0; // @[Top.scala 1572:22]
  wire [15:0] n512_I1; // @[Top.scala 1572:22]
  wire [15:0] n512_O_t0b; // @[Top.scala 1572:22]
  wire [15:0] n512_O_t1b; // @[Top.scala 1572:22]
  wire  n513_valid_up; // @[Top.scala 1576:22]
  wire  n513_valid_down; // @[Top.scala 1576:22]
  wire [15:0] n513_I_t0b; // @[Top.scala 1576:22]
  wire [15:0] n513_I_t1b; // @[Top.scala 1576:22]
  wire [15:0] n513_O; // @[Top.scala 1576:22]
  wire  n514_valid_up; // @[Top.scala 1579:22]
  wire  n514_valid_down; // @[Top.scala 1579:22]
  wire [15:0] n514_I0; // @[Top.scala 1579:22]
  wire [15:0] n514_I1; // @[Top.scala 1579:22]
  wire [15:0] n514_O_t0b; // @[Top.scala 1579:22]
  wire [15:0] n514_O_t1b; // @[Top.scala 1579:22]
  wire  n515_valid_up; // @[Top.scala 1583:22]
  wire  n515_valid_down; // @[Top.scala 1583:22]
  wire  n515_I0; // @[Top.scala 1583:22]
  wire [15:0] n515_I1_t0b; // @[Top.scala 1583:22]
  wire [15:0] n515_I1_t1b; // @[Top.scala 1583:22]
  wire  n515_O_t0b; // @[Top.scala 1583:22]
  wire [15:0] n515_O_t1b_t0b; // @[Top.scala 1583:22]
  wire [15:0] n515_O_t1b_t1b; // @[Top.scala 1583:22]
  wire  n516_valid_up; // @[Top.scala 1587:22]
  wire  n516_valid_down; // @[Top.scala 1587:22]
  wire  n516_I_t0b; // @[Top.scala 1587:22]
  wire [15:0] n516_I_t1b_t0b; // @[Top.scala 1587:22]
  wire [15:0] n516_I_t1b_t1b; // @[Top.scala 1587:22]
  wire [15:0] n516_O; // @[Top.scala 1587:22]
  wire  n517_valid_up; // @[Top.scala 1590:22]
  wire  n517_valid_down; // @[Top.scala 1590:22]
  wire [15:0] n517_I0; // @[Top.scala 1590:22]
  wire [15:0] n517_I1; // @[Top.scala 1590:22]
  wire [15:0] n517_O_t0b; // @[Top.scala 1590:22]
  wire [15:0] n517_O_t1b; // @[Top.scala 1590:22]
  wire  n518_clock; // @[Top.scala 1594:22]
  wire  n518_reset; // @[Top.scala 1594:22]
  wire  n518_valid_up; // @[Top.scala 1594:22]
  wire  n518_valid_down; // @[Top.scala 1594:22]
  wire [15:0] n518_I_t0b; // @[Top.scala 1594:22]
  wire [15:0] n518_I_t1b; // @[Top.scala 1594:22]
  wire [15:0] n518_O; // @[Top.scala 1594:22]
  wire  n520_valid_up; // @[Top.scala 1597:22]
  wire  n520_valid_down; // @[Top.scala 1597:22]
  wire [15:0] n520_I0; // @[Top.scala 1597:22]
  wire [15:0] n520_I1; // @[Top.scala 1597:22]
  wire [15:0] n520_O_t0b; // @[Top.scala 1597:22]
  wire [15:0] n520_O_t1b; // @[Top.scala 1597:22]
  wire  n521_valid_up; // @[Top.scala 1601:22]
  wire  n521_valid_down; // @[Top.scala 1601:22]
  wire [15:0] n521_I_t0b; // @[Top.scala 1601:22]
  wire [15:0] n521_I_t1b; // @[Top.scala 1601:22]
  wire [15:0] n521_O; // @[Top.scala 1601:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n523_valid_up; // @[Top.scala 1605:22]
  wire  n523_valid_down; // @[Top.scala 1605:22]
  wire [15:0] n523_I0; // @[Top.scala 1605:22]
  wire [15:0] n523_I1; // @[Top.scala 1605:22]
  wire [15:0] n523_O_t0b; // @[Top.scala 1605:22]
  wire [15:0] n523_O_t1b; // @[Top.scala 1605:22]
  wire  n524_valid_up; // @[Top.scala 1609:22]
  wire  n524_valid_down; // @[Top.scala 1609:22]
  wire [15:0] n524_I_t0b; // @[Top.scala 1609:22]
  wire [15:0] n524_I_t1b; // @[Top.scala 1609:22]
  wire [15:0] n524_O; // @[Top.scala 1609:22]
  wire  n525_valid_up; // @[Top.scala 1612:22]
  wire  n525_valid_down; // @[Top.scala 1612:22]
  wire [15:0] n525_I0; // @[Top.scala 1612:22]
  wire [15:0] n525_I1; // @[Top.scala 1612:22]
  wire [15:0] n525_O_t0b; // @[Top.scala 1612:22]
  wire [15:0] n525_O_t1b; // @[Top.scala 1612:22]
  wire  n526_valid_up; // @[Top.scala 1616:22]
  wire  n526_valid_down; // @[Top.scala 1616:22]
  wire [15:0] n526_I0; // @[Top.scala 1616:22]
  wire [15:0] n526_I1; // @[Top.scala 1616:22]
  wire [15:0] n526_O_t0b; // @[Top.scala 1616:22]
  wire [15:0] n526_O_t1b; // @[Top.scala 1616:22]
  wire  n527_valid_up; // @[Top.scala 1620:22]
  wire  n527_valid_down; // @[Top.scala 1620:22]
  wire [15:0] n527_I0_t0b; // @[Top.scala 1620:22]
  wire [15:0] n527_I0_t1b; // @[Top.scala 1620:22]
  wire [15:0] n527_I1_t0b; // @[Top.scala 1620:22]
  wire [15:0] n527_I1_t1b; // @[Top.scala 1620:22]
  wire [15:0] n527_O_t0b_t0b; // @[Top.scala 1620:22]
  wire [15:0] n527_O_t0b_t1b; // @[Top.scala 1620:22]
  wire [15:0] n527_O_t1b_t0b; // @[Top.scala 1620:22]
  wire [15:0] n527_O_t1b_t1b; // @[Top.scala 1620:22]
  wire  n528_clock; // @[Top.scala 1624:22]
  wire  n528_reset; // @[Top.scala 1624:22]
  wire  n528_valid_up; // @[Top.scala 1624:22]
  wire  n528_valid_down; // @[Top.scala 1624:22]
  wire [15:0] n528_I_t0b_t0b; // @[Top.scala 1624:22]
  wire [15:0] n528_I_t0b_t1b; // @[Top.scala 1624:22]
  wire [15:0] n528_I_t1b_t0b; // @[Top.scala 1624:22]
  wire [15:0] n528_I_t1b_t1b; // @[Top.scala 1624:22]
  wire [15:0] n528_O_t0b_t0b; // @[Top.scala 1624:22]
  wire [15:0] n528_O_t0b_t1b; // @[Top.scala 1624:22]
  wire [15:0] n528_O_t1b_t0b; // @[Top.scala 1624:22]
  wire [15:0] n528_O_t1b_t1b; // @[Top.scala 1624:22]
  wire  n529_valid_up; // @[Top.scala 1627:22]
  wire  n529_valid_down; // @[Top.scala 1627:22]
  wire  n529_I0; // @[Top.scala 1627:22]
  wire [15:0] n529_I1_t0b_t0b; // @[Top.scala 1627:22]
  wire [15:0] n529_I1_t0b_t1b; // @[Top.scala 1627:22]
  wire [15:0] n529_I1_t1b_t0b; // @[Top.scala 1627:22]
  wire [15:0] n529_I1_t1b_t1b; // @[Top.scala 1627:22]
  wire  n529_O_t0b; // @[Top.scala 1627:22]
  wire [15:0] n529_O_t1b_t0b_t0b; // @[Top.scala 1627:22]
  wire [15:0] n529_O_t1b_t0b_t1b; // @[Top.scala 1627:22]
  wire [15:0] n529_O_t1b_t1b_t0b; // @[Top.scala 1627:22]
  wire [15:0] n529_O_t1b_t1b_t1b; // @[Top.scala 1627:22]
  wire  n530_valid_up; // @[Top.scala 1631:22]
  wire  n530_valid_down; // @[Top.scala 1631:22]
  wire  n530_I_t0b; // @[Top.scala 1631:22]
  wire [15:0] n530_I_t1b_t0b_t0b; // @[Top.scala 1631:22]
  wire [15:0] n530_I_t1b_t0b_t1b; // @[Top.scala 1631:22]
  wire [15:0] n530_I_t1b_t1b_t0b; // @[Top.scala 1631:22]
  wire [15:0] n530_I_t1b_t1b_t1b; // @[Top.scala 1631:22]
  wire [15:0] n530_O_t0b; // @[Top.scala 1631:22]
  wire [15:0] n530_O_t1b; // @[Top.scala 1631:22]
  wire  n532_valid_up; // @[Top.scala 1634:22]
  wire  n532_valid_down; // @[Top.scala 1634:22]
  wire [15:0] n532_I0; // @[Top.scala 1634:22]
  wire [15:0] n532_I1_t0b; // @[Top.scala 1634:22]
  wire [15:0] n532_I1_t1b; // @[Top.scala 1634:22]
  wire [15:0] n532_O_t0b; // @[Top.scala 1634:22]
  wire [15:0] n532_O_t1b_t0b; // @[Top.scala 1634:22]
  wire [15:0] n532_O_t1b_t1b; // @[Top.scala 1634:22]
  Fst n498 ( // @[Top.scala 1531:22]
    .valid_up(n498_valid_up),
    .valid_down(n498_valid_down),
    .I_t0b(n498_I_t0b),
    .O(n498_O)
  );
  FIFO_1 n531 ( // @[Top.scala 1534:22]
    .clock(n531_clock),
    .reset(n531_reset),
    .valid_up(n531_valid_up),
    .valid_down(n531_valid_down),
    .I(n531_I),
    .O(n531_O)
  );
  FIFO_1 n519 ( // @[Top.scala 1537:22]
    .clock(n519_clock),
    .reset(n519_reset),
    .valid_up(n519_valid_up),
    .valid_down(n519_valid_down),
    .I(n519_I),
    .O(n519_O)
  );
  Snd n499 ( // @[Top.scala 1540:22]
    .valid_up(n499_valid_up),
    .valid_down(n499_valid_down),
    .I_t1b_t0b(n499_I_t1b_t0b),
    .I_t1b_t1b(n499_I_t1b_t1b),
    .O_t0b(n499_O_t0b),
    .O_t1b(n499_O_t1b)
  );
  Fst_1 n500 ( // @[Top.scala 1543:22]
    .valid_up(n500_valid_up),
    .valid_down(n500_valid_down),
    .I_t0b(n500_I_t0b),
    .O(n500_O)
  );
  Snd_1 n501 ( // @[Top.scala 1546:22]
    .valid_up(n501_valid_up),
    .valid_down(n501_valid_down),
    .I_t1b(n501_I_t1b),
    .O(n501_O)
  );
  AtomTuple_1 n502 ( // @[Top.scala 1549:22]
    .valid_up(n502_valid_up),
    .valid_down(n502_valid_down),
    .I0(n502_I0),
    .I1(n502_I1),
    .O_t0b(n502_O_t0b),
    .O_t1b(n502_O_t1b)
  );
  Add n503 ( // @[Top.scala 1553:22]
    .valid_up(n503_valid_up),
    .valid_down(n503_valid_down),
    .I_t0b(n503_I_t0b),
    .I_t1b(n503_I_t1b),
    .O(n503_O)
  );
  InitialDelayCounter_42 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n506 ( // @[Top.scala 1557:22]
    .valid_up(n506_valid_up),
    .valid_down(n506_valid_down),
    .I0(n506_I0),
    .O_t0b(n506_O_t0b)
  );
  RShift n507 ( // @[Top.scala 1561:22]
    .valid_up(n507_valid_up),
    .valid_down(n507_valid_down),
    .I_t0b(n507_I_t0b),
    .O(n507_O)
  );
  AtomTuple_1 n508 ( // @[Top.scala 1564:22]
    .valid_up(n508_valid_up),
    .valid_down(n508_valid_down),
    .I0(n508_I0),
    .I1(n508_I1),
    .O_t0b(n508_O_t0b),
    .O_t1b(n508_O_t1b)
  );
  Eq n509 ( // @[Top.scala 1568:22]
    .valid_up(n509_valid_up),
    .valid_down(n509_valid_down),
    .I_t0b(n509_I_t0b),
    .I_t1b(n509_I_t1b),
    .O(n509_O)
  );
  InitialDelayCounter_42 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n512 ( // @[Top.scala 1572:22]
    .valid_up(n512_valid_up),
    .valid_down(n512_valid_down),
    .I0(n512_I0),
    .I1(n512_I1),
    .O_t0b(n512_O_t0b),
    .O_t1b(n512_O_t1b)
  );
  Add n513 ( // @[Top.scala 1576:22]
    .valid_up(n513_valid_up),
    .valid_down(n513_valid_down),
    .I_t0b(n513_I_t0b),
    .I_t1b(n513_I_t1b),
    .O(n513_O)
  );
  AtomTuple_1 n514 ( // @[Top.scala 1579:22]
    .valid_up(n514_valid_up),
    .valid_down(n514_valid_down),
    .I0(n514_I0),
    .I1(n514_I1),
    .O_t0b(n514_O_t0b),
    .O_t1b(n514_O_t1b)
  );
  AtomTuple_9 n515 ( // @[Top.scala 1583:22]
    .valid_up(n515_valid_up),
    .valid_down(n515_valid_down),
    .I0(n515_I0),
    .I1_t0b(n515_I1_t0b),
    .I1_t1b(n515_I1_t1b),
    .O_t0b(n515_O_t0b),
    .O_t1b_t0b(n515_O_t1b_t0b),
    .O_t1b_t1b(n515_O_t1b_t1b)
  );
  If n516 ( // @[Top.scala 1587:22]
    .valid_up(n516_valid_up),
    .valid_down(n516_valid_down),
    .I_t0b(n516_I_t0b),
    .I_t1b_t0b(n516_I_t1b_t0b),
    .I_t1b_t1b(n516_I_t1b_t1b),
    .O(n516_O)
  );
  AtomTuple_1 n517 ( // @[Top.scala 1590:22]
    .valid_up(n517_valid_up),
    .valid_down(n517_valid_down),
    .I0(n517_I0),
    .I1(n517_I1),
    .O_t0b(n517_O_t0b),
    .O_t1b(n517_O_t1b)
  );
  Mul n518 ( // @[Top.scala 1594:22]
    .clock(n518_clock),
    .reset(n518_reset),
    .valid_up(n518_valid_up),
    .valid_down(n518_valid_down),
    .I_t0b(n518_I_t0b),
    .I_t1b(n518_I_t1b),
    .O(n518_O)
  );
  AtomTuple_1 n520 ( // @[Top.scala 1597:22]
    .valid_up(n520_valid_up),
    .valid_down(n520_valid_down),
    .I0(n520_I0),
    .I1(n520_I1),
    .O_t0b(n520_O_t0b),
    .O_t1b(n520_O_t1b)
  );
  Lt n521 ( // @[Top.scala 1601:22]
    .valid_up(n521_valid_up),
    .valid_down(n521_valid_down),
    .I_t0b(n521_I_t0b),
    .I_t1b(n521_I_t1b),
    .O(n521_O)
  );
  InitialDelayCounter_42 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n523 ( // @[Top.scala 1605:22]
    .valid_up(n523_valid_up),
    .valid_down(n523_valid_down),
    .I0(n523_I0),
    .I1(n523_I1),
    .O_t0b(n523_O_t0b),
    .O_t1b(n523_O_t1b)
  );
  Sub n524 ( // @[Top.scala 1609:22]
    .valid_up(n524_valid_up),
    .valid_down(n524_valid_down),
    .I_t0b(n524_I_t0b),
    .I_t1b(n524_I_t1b),
    .O(n524_O)
  );
  AtomTuple_1 n525 ( // @[Top.scala 1612:22]
    .valid_up(n525_valid_up),
    .valid_down(n525_valid_down),
    .I0(n525_I0),
    .I1(n525_I1),
    .O_t0b(n525_O_t0b),
    .O_t1b(n525_O_t1b)
  );
  AtomTuple_1 n526 ( // @[Top.scala 1616:22]
    .valid_up(n526_valid_up),
    .valid_down(n526_valid_down),
    .I0(n526_I0),
    .I1(n526_I1),
    .O_t0b(n526_O_t0b),
    .O_t1b(n526_O_t1b)
  );
  AtomTuple_15 n527 ( // @[Top.scala 1620:22]
    .valid_up(n527_valid_up),
    .valid_down(n527_valid_down),
    .I0_t0b(n527_I0_t0b),
    .I0_t1b(n527_I0_t1b),
    .I1_t0b(n527_I1_t0b),
    .I1_t1b(n527_I1_t1b),
    .O_t0b_t0b(n527_O_t0b_t0b),
    .O_t0b_t1b(n527_O_t0b_t1b),
    .O_t1b_t0b(n527_O_t1b_t0b),
    .O_t1b_t1b(n527_O_t1b_t1b)
  );
  FIFO_3 n528 ( // @[Top.scala 1624:22]
    .clock(n528_clock),
    .reset(n528_reset),
    .valid_up(n528_valid_up),
    .valid_down(n528_valid_down),
    .I_t0b_t0b(n528_I_t0b_t0b),
    .I_t0b_t1b(n528_I_t0b_t1b),
    .I_t1b_t0b(n528_I_t1b_t0b),
    .I_t1b_t1b(n528_I_t1b_t1b),
    .O_t0b_t0b(n528_O_t0b_t0b),
    .O_t0b_t1b(n528_O_t0b_t1b),
    .O_t1b_t0b(n528_O_t1b_t0b),
    .O_t1b_t1b(n528_O_t1b_t1b)
  );
  AtomTuple_16 n529 ( // @[Top.scala 1627:22]
    .valid_up(n529_valid_up),
    .valid_down(n529_valid_down),
    .I0(n529_I0),
    .I1_t0b_t0b(n529_I1_t0b_t0b),
    .I1_t0b_t1b(n529_I1_t0b_t1b),
    .I1_t1b_t0b(n529_I1_t1b_t0b),
    .I1_t1b_t1b(n529_I1_t1b_t1b),
    .O_t0b(n529_O_t0b),
    .O_t1b_t0b_t0b(n529_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n529_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n529_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n529_O_t1b_t1b_t1b)
  );
  If_1 n530 ( // @[Top.scala 1631:22]
    .valid_up(n530_valid_up),
    .valid_down(n530_valid_down),
    .I_t0b(n530_I_t0b),
    .I_t1b_t0b_t0b(n530_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n530_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n530_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n530_I_t1b_t1b_t1b),
    .O_t0b(n530_O_t0b),
    .O_t1b(n530_O_t1b)
  );
  AtomTuple_3 n532 ( // @[Top.scala 1634:22]
    .valid_up(n532_valid_up),
    .valid_down(n532_valid_down),
    .I0(n532_I0),
    .I1_t0b(n532_I1_t0b),
    .I1_t1b(n532_I1_t1b),
    .O_t0b(n532_O_t0b),
    .O_t1b_t0b(n532_O_t1b_t0b),
    .O_t1b_t1b(n532_O_t1b_t1b)
  );
  assign valid_down = n532_valid_down; // @[Top.scala 1639:16]
  assign O_t0b = n532_O_t0b; // @[Top.scala 1638:7]
  assign O_t1b_t0b = n532_O_t1b_t0b; // @[Top.scala 1638:7]
  assign O_t1b_t1b = n532_O_t1b_t1b; // @[Top.scala 1638:7]
  assign n498_valid_up = valid_up; // @[Top.scala 1533:19]
  assign n498_I_t0b = I_t0b; // @[Top.scala 1532:12]
  assign n531_clock = clock;
  assign n531_reset = reset;
  assign n531_valid_up = n498_valid_down; // @[Top.scala 1536:19]
  assign n531_I = n498_O; // @[Top.scala 1535:12]
  assign n519_clock = clock;
  assign n519_reset = reset;
  assign n519_valid_up = n498_valid_down; // @[Top.scala 1539:19]
  assign n519_I = n498_O; // @[Top.scala 1538:12]
  assign n499_valid_up = valid_up; // @[Top.scala 1542:19]
  assign n499_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1541:12]
  assign n499_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1541:12]
  assign n500_valid_up = n499_valid_down; // @[Top.scala 1545:19]
  assign n500_I_t0b = n499_O_t0b; // @[Top.scala 1544:12]
  assign n501_valid_up = n499_valid_down; // @[Top.scala 1548:19]
  assign n501_I_t1b = n499_O_t1b; // @[Top.scala 1547:12]
  assign n502_valid_up = n500_valid_down & n501_valid_down; // @[Top.scala 1552:19]
  assign n502_I0 = n500_O; // @[Top.scala 1550:13]
  assign n502_I1 = n501_O; // @[Top.scala 1551:13]
  assign n503_valid_up = n502_valid_down; // @[Top.scala 1555:19]
  assign n503_I_t0b = n502_O_t0b; // @[Top.scala 1554:12]
  assign n503_I_t1b = n502_O_t1b; // @[Top.scala 1554:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n506_valid_up = n503_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 1560:19]
  assign n506_I0 = n503_O; // @[Top.scala 1558:13]
  assign n507_valid_up = n506_valid_down; // @[Top.scala 1563:19]
  assign n507_I_t0b = n506_O_t0b; // @[Top.scala 1562:12]
  assign n508_valid_up = n507_valid_down & n500_valid_down; // @[Top.scala 1567:19]
  assign n508_I0 = n507_O; // @[Top.scala 1565:13]
  assign n508_I1 = n500_O; // @[Top.scala 1566:13]
  assign n509_valid_up = n508_valid_down; // @[Top.scala 1570:19]
  assign n509_I_t0b = n508_O_t0b; // @[Top.scala 1569:12]
  assign n509_I_t1b = n508_O_t1b; // @[Top.scala 1569:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n512_valid_up = n507_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1575:19]
  assign n512_I0 = n507_O; // @[Top.scala 1573:13]
  assign n512_I1 = 16'h1; // @[Top.scala 1574:13]
  assign n513_valid_up = n512_valid_down; // @[Top.scala 1578:19]
  assign n513_I_t0b = n512_O_t0b; // @[Top.scala 1577:12]
  assign n513_I_t1b = n512_O_t1b; // @[Top.scala 1577:12]
  assign n514_valid_up = n513_valid_down & n507_valid_down; // @[Top.scala 1582:19]
  assign n514_I0 = n513_O; // @[Top.scala 1580:13]
  assign n514_I1 = n507_O; // @[Top.scala 1581:13]
  assign n515_valid_up = n509_valid_down & n514_valid_down; // @[Top.scala 1586:19]
  assign n515_I0 = n509_O[0]; // @[Top.scala 1584:13]
  assign n515_I1_t0b = n514_O_t0b; // @[Top.scala 1585:13]
  assign n515_I1_t1b = n514_O_t1b; // @[Top.scala 1585:13]
  assign n516_valid_up = n515_valid_down; // @[Top.scala 1589:19]
  assign n516_I_t0b = n515_O_t0b; // @[Top.scala 1588:12]
  assign n516_I_t1b_t0b = n515_O_t1b_t0b; // @[Top.scala 1588:12]
  assign n516_I_t1b_t1b = n515_O_t1b_t1b; // @[Top.scala 1588:12]
  assign n517_valid_up = n516_valid_down; // @[Top.scala 1593:19]
  assign n517_I0 = n516_O; // @[Top.scala 1591:13]
  assign n517_I1 = n516_O; // @[Top.scala 1592:13]
  assign n518_clock = clock;
  assign n518_reset = reset;
  assign n518_valid_up = n517_valid_down; // @[Top.scala 1596:19]
  assign n518_I_t0b = n517_O_t0b; // @[Top.scala 1595:12]
  assign n518_I_t1b = n517_O_t1b; // @[Top.scala 1595:12]
  assign n520_valid_up = n519_valid_down & n518_valid_down; // @[Top.scala 1600:19]
  assign n520_I0 = n519_O; // @[Top.scala 1598:13]
  assign n520_I1 = n518_O; // @[Top.scala 1599:13]
  assign n521_valid_up = n520_valid_down; // @[Top.scala 1603:19]
  assign n521_I_t0b = n520_O_t0b; // @[Top.scala 1602:12]
  assign n521_I_t1b = n520_O_t1b; // @[Top.scala 1602:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n523_valid_up = n516_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1608:19]
  assign n523_I0 = n516_O; // @[Top.scala 1606:13]
  assign n523_I1 = 16'h1; // @[Top.scala 1607:13]
  assign n524_valid_up = n523_valid_down; // @[Top.scala 1611:19]
  assign n524_I_t0b = n523_O_t0b; // @[Top.scala 1610:12]
  assign n524_I_t1b = n523_O_t1b; // @[Top.scala 1610:12]
  assign n525_valid_up = n500_valid_down & n524_valid_down; // @[Top.scala 1615:19]
  assign n525_I0 = n500_O; // @[Top.scala 1613:13]
  assign n525_I1 = n524_O; // @[Top.scala 1614:13]
  assign n526_valid_up = n516_valid_down & n501_valid_down; // @[Top.scala 1619:19]
  assign n526_I0 = n516_O; // @[Top.scala 1617:13]
  assign n526_I1 = n501_O; // @[Top.scala 1618:13]
  assign n527_valid_up = n525_valid_down & n526_valid_down; // @[Top.scala 1623:19]
  assign n527_I0_t0b = n525_O_t0b; // @[Top.scala 1621:13]
  assign n527_I0_t1b = n525_O_t1b; // @[Top.scala 1621:13]
  assign n527_I1_t0b = n526_O_t0b; // @[Top.scala 1622:13]
  assign n527_I1_t1b = n526_O_t1b; // @[Top.scala 1622:13]
  assign n528_clock = clock;
  assign n528_reset = reset;
  assign n528_valid_up = n527_valid_down; // @[Top.scala 1626:19]
  assign n528_I_t0b_t0b = n527_O_t0b_t0b; // @[Top.scala 1625:12]
  assign n528_I_t0b_t1b = n527_O_t0b_t1b; // @[Top.scala 1625:12]
  assign n528_I_t1b_t0b = n527_O_t1b_t0b; // @[Top.scala 1625:12]
  assign n528_I_t1b_t1b = n527_O_t1b_t1b; // @[Top.scala 1625:12]
  assign n529_valid_up = n521_valid_down & n528_valid_down; // @[Top.scala 1630:19]
  assign n529_I0 = n521_O[0]; // @[Top.scala 1628:13]
  assign n529_I1_t0b_t0b = n528_O_t0b_t0b; // @[Top.scala 1629:13]
  assign n529_I1_t0b_t1b = n528_O_t0b_t1b; // @[Top.scala 1629:13]
  assign n529_I1_t1b_t0b = n528_O_t1b_t0b; // @[Top.scala 1629:13]
  assign n529_I1_t1b_t1b = n528_O_t1b_t1b; // @[Top.scala 1629:13]
  assign n530_valid_up = n529_valid_down; // @[Top.scala 1633:19]
  assign n530_I_t0b = n529_O_t0b; // @[Top.scala 1632:12]
  assign n530_I_t1b_t0b_t0b = n529_O_t1b_t0b_t0b; // @[Top.scala 1632:12]
  assign n530_I_t1b_t0b_t1b = n529_O_t1b_t0b_t1b; // @[Top.scala 1632:12]
  assign n530_I_t1b_t1b_t0b = n529_O_t1b_t1b_t0b; // @[Top.scala 1632:12]
  assign n530_I_t1b_t1b_t1b = n529_O_t1b_t1b_t1b; // @[Top.scala 1632:12]
  assign n532_valid_up = n531_valid_down & n530_valid_down; // @[Top.scala 1637:19]
  assign n532_I0 = n531_O; // @[Top.scala 1635:13]
  assign n532_I1_t0b = n530_O_t0b; // @[Top.scala 1636:13]
  assign n532_I1_t1b = n530_O_t1b; // @[Top.scala 1636:13]
endmodule
module MapT_14(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_14 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
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
  wire  n535_valid_up; // @[Top.scala 1645:22]
  wire  n535_valid_down; // @[Top.scala 1645:22]
  wire [15:0] n535_I_t0b; // @[Top.scala 1645:22]
  wire [15:0] n535_O; // @[Top.scala 1645:22]
  wire  n568_clock; // @[Top.scala 1648:22]
  wire  n568_reset; // @[Top.scala 1648:22]
  wire  n568_valid_up; // @[Top.scala 1648:22]
  wire  n568_valid_down; // @[Top.scala 1648:22]
  wire [15:0] n568_I; // @[Top.scala 1648:22]
  wire [15:0] n568_O; // @[Top.scala 1648:22]
  wire  n556_clock; // @[Top.scala 1651:22]
  wire  n556_reset; // @[Top.scala 1651:22]
  wire  n556_valid_up; // @[Top.scala 1651:22]
  wire  n556_valid_down; // @[Top.scala 1651:22]
  wire [15:0] n556_I; // @[Top.scala 1651:22]
  wire [15:0] n556_O; // @[Top.scala 1651:22]
  wire  n536_valid_up; // @[Top.scala 1654:22]
  wire  n536_valid_down; // @[Top.scala 1654:22]
  wire [15:0] n536_I_t1b_t0b; // @[Top.scala 1654:22]
  wire [15:0] n536_I_t1b_t1b; // @[Top.scala 1654:22]
  wire [15:0] n536_O_t0b; // @[Top.scala 1654:22]
  wire [15:0] n536_O_t1b; // @[Top.scala 1654:22]
  wire  n537_valid_up; // @[Top.scala 1657:22]
  wire  n537_valid_down; // @[Top.scala 1657:22]
  wire [15:0] n537_I_t0b; // @[Top.scala 1657:22]
  wire [15:0] n537_O; // @[Top.scala 1657:22]
  wire  n538_valid_up; // @[Top.scala 1660:22]
  wire  n538_valid_down; // @[Top.scala 1660:22]
  wire [15:0] n538_I_t1b; // @[Top.scala 1660:22]
  wire [15:0] n538_O; // @[Top.scala 1660:22]
  wire  n539_valid_up; // @[Top.scala 1663:22]
  wire  n539_valid_down; // @[Top.scala 1663:22]
  wire [15:0] n539_I0; // @[Top.scala 1663:22]
  wire [15:0] n539_I1; // @[Top.scala 1663:22]
  wire [15:0] n539_O_t0b; // @[Top.scala 1663:22]
  wire [15:0] n539_O_t1b; // @[Top.scala 1663:22]
  wire  n540_valid_up; // @[Top.scala 1667:22]
  wire  n540_valid_down; // @[Top.scala 1667:22]
  wire [15:0] n540_I_t0b; // @[Top.scala 1667:22]
  wire [15:0] n540_I_t1b; // @[Top.scala 1667:22]
  wire [15:0] n540_O; // @[Top.scala 1667:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n543_valid_up; // @[Top.scala 1671:22]
  wire  n543_valid_down; // @[Top.scala 1671:22]
  wire [15:0] n543_I0; // @[Top.scala 1671:22]
  wire [15:0] n543_O_t0b; // @[Top.scala 1671:22]
  wire  n544_valid_up; // @[Top.scala 1675:22]
  wire  n544_valid_down; // @[Top.scala 1675:22]
  wire [15:0] n544_I_t0b; // @[Top.scala 1675:22]
  wire [15:0] n544_O; // @[Top.scala 1675:22]
  wire  n545_valid_up; // @[Top.scala 1678:22]
  wire  n545_valid_down; // @[Top.scala 1678:22]
  wire [15:0] n545_I0; // @[Top.scala 1678:22]
  wire [15:0] n545_I1; // @[Top.scala 1678:22]
  wire [15:0] n545_O_t0b; // @[Top.scala 1678:22]
  wire [15:0] n545_O_t1b; // @[Top.scala 1678:22]
  wire  n546_valid_up; // @[Top.scala 1682:22]
  wire  n546_valid_down; // @[Top.scala 1682:22]
  wire [15:0] n546_I_t0b; // @[Top.scala 1682:22]
  wire [15:0] n546_I_t1b; // @[Top.scala 1682:22]
  wire [15:0] n546_O; // @[Top.scala 1682:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n549_valid_up; // @[Top.scala 1686:22]
  wire  n549_valid_down; // @[Top.scala 1686:22]
  wire [15:0] n549_I0; // @[Top.scala 1686:22]
  wire [15:0] n549_I1; // @[Top.scala 1686:22]
  wire [15:0] n549_O_t0b; // @[Top.scala 1686:22]
  wire [15:0] n549_O_t1b; // @[Top.scala 1686:22]
  wire  n550_valid_up; // @[Top.scala 1690:22]
  wire  n550_valid_down; // @[Top.scala 1690:22]
  wire [15:0] n550_I_t0b; // @[Top.scala 1690:22]
  wire [15:0] n550_I_t1b; // @[Top.scala 1690:22]
  wire [15:0] n550_O; // @[Top.scala 1690:22]
  wire  n551_valid_up; // @[Top.scala 1693:22]
  wire  n551_valid_down; // @[Top.scala 1693:22]
  wire [15:0] n551_I0; // @[Top.scala 1693:22]
  wire [15:0] n551_I1; // @[Top.scala 1693:22]
  wire [15:0] n551_O_t0b; // @[Top.scala 1693:22]
  wire [15:0] n551_O_t1b; // @[Top.scala 1693:22]
  wire  n552_valid_up; // @[Top.scala 1697:22]
  wire  n552_valid_down; // @[Top.scala 1697:22]
  wire  n552_I0; // @[Top.scala 1697:22]
  wire [15:0] n552_I1_t0b; // @[Top.scala 1697:22]
  wire [15:0] n552_I1_t1b; // @[Top.scala 1697:22]
  wire  n552_O_t0b; // @[Top.scala 1697:22]
  wire [15:0] n552_O_t1b_t0b; // @[Top.scala 1697:22]
  wire [15:0] n552_O_t1b_t1b; // @[Top.scala 1697:22]
  wire  n553_valid_up; // @[Top.scala 1701:22]
  wire  n553_valid_down; // @[Top.scala 1701:22]
  wire  n553_I_t0b; // @[Top.scala 1701:22]
  wire [15:0] n553_I_t1b_t0b; // @[Top.scala 1701:22]
  wire [15:0] n553_I_t1b_t1b; // @[Top.scala 1701:22]
  wire [15:0] n553_O; // @[Top.scala 1701:22]
  wire  n554_valid_up; // @[Top.scala 1704:22]
  wire  n554_valid_down; // @[Top.scala 1704:22]
  wire [15:0] n554_I0; // @[Top.scala 1704:22]
  wire [15:0] n554_I1; // @[Top.scala 1704:22]
  wire [15:0] n554_O_t0b; // @[Top.scala 1704:22]
  wire [15:0] n554_O_t1b; // @[Top.scala 1704:22]
  wire  n555_clock; // @[Top.scala 1708:22]
  wire  n555_reset; // @[Top.scala 1708:22]
  wire  n555_valid_up; // @[Top.scala 1708:22]
  wire  n555_valid_down; // @[Top.scala 1708:22]
  wire [15:0] n555_I_t0b; // @[Top.scala 1708:22]
  wire [15:0] n555_I_t1b; // @[Top.scala 1708:22]
  wire [15:0] n555_O; // @[Top.scala 1708:22]
  wire  n557_valid_up; // @[Top.scala 1711:22]
  wire  n557_valid_down; // @[Top.scala 1711:22]
  wire [15:0] n557_I0; // @[Top.scala 1711:22]
  wire [15:0] n557_I1; // @[Top.scala 1711:22]
  wire [15:0] n557_O_t0b; // @[Top.scala 1711:22]
  wire [15:0] n557_O_t1b; // @[Top.scala 1711:22]
  wire  n558_valid_up; // @[Top.scala 1715:22]
  wire  n558_valid_down; // @[Top.scala 1715:22]
  wire [15:0] n558_I_t0b; // @[Top.scala 1715:22]
  wire [15:0] n558_I_t1b; // @[Top.scala 1715:22]
  wire [15:0] n558_O; // @[Top.scala 1715:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n560_valid_up; // @[Top.scala 1719:22]
  wire  n560_valid_down; // @[Top.scala 1719:22]
  wire [15:0] n560_I0; // @[Top.scala 1719:22]
  wire [15:0] n560_I1; // @[Top.scala 1719:22]
  wire [15:0] n560_O_t0b; // @[Top.scala 1719:22]
  wire [15:0] n560_O_t1b; // @[Top.scala 1719:22]
  wire  n561_valid_up; // @[Top.scala 1723:22]
  wire  n561_valid_down; // @[Top.scala 1723:22]
  wire [15:0] n561_I_t0b; // @[Top.scala 1723:22]
  wire [15:0] n561_I_t1b; // @[Top.scala 1723:22]
  wire [15:0] n561_O; // @[Top.scala 1723:22]
  wire  n562_valid_up; // @[Top.scala 1726:22]
  wire  n562_valid_down; // @[Top.scala 1726:22]
  wire [15:0] n562_I0; // @[Top.scala 1726:22]
  wire [15:0] n562_I1; // @[Top.scala 1726:22]
  wire [15:0] n562_O_t0b; // @[Top.scala 1726:22]
  wire [15:0] n562_O_t1b; // @[Top.scala 1726:22]
  wire  n563_valid_up; // @[Top.scala 1730:22]
  wire  n563_valid_down; // @[Top.scala 1730:22]
  wire [15:0] n563_I0; // @[Top.scala 1730:22]
  wire [15:0] n563_I1; // @[Top.scala 1730:22]
  wire [15:0] n563_O_t0b; // @[Top.scala 1730:22]
  wire [15:0] n563_O_t1b; // @[Top.scala 1730:22]
  wire  n564_valid_up; // @[Top.scala 1734:22]
  wire  n564_valid_down; // @[Top.scala 1734:22]
  wire [15:0] n564_I0_t0b; // @[Top.scala 1734:22]
  wire [15:0] n564_I0_t1b; // @[Top.scala 1734:22]
  wire [15:0] n564_I1_t0b; // @[Top.scala 1734:22]
  wire [15:0] n564_I1_t1b; // @[Top.scala 1734:22]
  wire [15:0] n564_O_t0b_t0b; // @[Top.scala 1734:22]
  wire [15:0] n564_O_t0b_t1b; // @[Top.scala 1734:22]
  wire [15:0] n564_O_t1b_t0b; // @[Top.scala 1734:22]
  wire [15:0] n564_O_t1b_t1b; // @[Top.scala 1734:22]
  wire  n565_clock; // @[Top.scala 1738:22]
  wire  n565_reset; // @[Top.scala 1738:22]
  wire  n565_valid_up; // @[Top.scala 1738:22]
  wire  n565_valid_down; // @[Top.scala 1738:22]
  wire [15:0] n565_I_t0b_t0b; // @[Top.scala 1738:22]
  wire [15:0] n565_I_t0b_t1b; // @[Top.scala 1738:22]
  wire [15:0] n565_I_t1b_t0b; // @[Top.scala 1738:22]
  wire [15:0] n565_I_t1b_t1b; // @[Top.scala 1738:22]
  wire [15:0] n565_O_t0b_t0b; // @[Top.scala 1738:22]
  wire [15:0] n565_O_t0b_t1b; // @[Top.scala 1738:22]
  wire [15:0] n565_O_t1b_t0b; // @[Top.scala 1738:22]
  wire [15:0] n565_O_t1b_t1b; // @[Top.scala 1738:22]
  wire  n566_valid_up; // @[Top.scala 1741:22]
  wire  n566_valid_down; // @[Top.scala 1741:22]
  wire  n566_I0; // @[Top.scala 1741:22]
  wire [15:0] n566_I1_t0b_t0b; // @[Top.scala 1741:22]
  wire [15:0] n566_I1_t0b_t1b; // @[Top.scala 1741:22]
  wire [15:0] n566_I1_t1b_t0b; // @[Top.scala 1741:22]
  wire [15:0] n566_I1_t1b_t1b; // @[Top.scala 1741:22]
  wire  n566_O_t0b; // @[Top.scala 1741:22]
  wire [15:0] n566_O_t1b_t0b_t0b; // @[Top.scala 1741:22]
  wire [15:0] n566_O_t1b_t0b_t1b; // @[Top.scala 1741:22]
  wire [15:0] n566_O_t1b_t1b_t0b; // @[Top.scala 1741:22]
  wire [15:0] n566_O_t1b_t1b_t1b; // @[Top.scala 1741:22]
  wire  n567_valid_up; // @[Top.scala 1745:22]
  wire  n567_valid_down; // @[Top.scala 1745:22]
  wire  n567_I_t0b; // @[Top.scala 1745:22]
  wire [15:0] n567_I_t1b_t0b_t0b; // @[Top.scala 1745:22]
  wire [15:0] n567_I_t1b_t0b_t1b; // @[Top.scala 1745:22]
  wire [15:0] n567_I_t1b_t1b_t0b; // @[Top.scala 1745:22]
  wire [15:0] n567_I_t1b_t1b_t1b; // @[Top.scala 1745:22]
  wire [15:0] n567_O_t0b; // @[Top.scala 1745:22]
  wire [15:0] n567_O_t1b; // @[Top.scala 1745:22]
  wire  n569_valid_up; // @[Top.scala 1748:22]
  wire  n569_valid_down; // @[Top.scala 1748:22]
  wire [15:0] n569_I0; // @[Top.scala 1748:22]
  wire [15:0] n569_I1_t0b; // @[Top.scala 1748:22]
  wire [15:0] n569_I1_t1b; // @[Top.scala 1748:22]
  wire [15:0] n569_O_t0b; // @[Top.scala 1748:22]
  wire [15:0] n569_O_t1b_t0b; // @[Top.scala 1748:22]
  wire [15:0] n569_O_t1b_t1b; // @[Top.scala 1748:22]
  Fst n535 ( // @[Top.scala 1645:22]
    .valid_up(n535_valid_up),
    .valid_down(n535_valid_down),
    .I_t0b(n535_I_t0b),
    .O(n535_O)
  );
  FIFO_1 n568 ( // @[Top.scala 1648:22]
    .clock(n568_clock),
    .reset(n568_reset),
    .valid_up(n568_valid_up),
    .valid_down(n568_valid_down),
    .I(n568_I),
    .O(n568_O)
  );
  FIFO_1 n556 ( // @[Top.scala 1651:22]
    .clock(n556_clock),
    .reset(n556_reset),
    .valid_up(n556_valid_up),
    .valid_down(n556_valid_down),
    .I(n556_I),
    .O(n556_O)
  );
  Snd n536 ( // @[Top.scala 1654:22]
    .valid_up(n536_valid_up),
    .valid_down(n536_valid_down),
    .I_t1b_t0b(n536_I_t1b_t0b),
    .I_t1b_t1b(n536_I_t1b_t1b),
    .O_t0b(n536_O_t0b),
    .O_t1b(n536_O_t1b)
  );
  Fst_1 n537 ( // @[Top.scala 1657:22]
    .valid_up(n537_valid_up),
    .valid_down(n537_valid_down),
    .I_t0b(n537_I_t0b),
    .O(n537_O)
  );
  Snd_1 n538 ( // @[Top.scala 1660:22]
    .valid_up(n538_valid_up),
    .valid_down(n538_valid_down),
    .I_t1b(n538_I_t1b),
    .O(n538_O)
  );
  AtomTuple_1 n539 ( // @[Top.scala 1663:22]
    .valid_up(n539_valid_up),
    .valid_down(n539_valid_down),
    .I0(n539_I0),
    .I1(n539_I1),
    .O_t0b(n539_O_t0b),
    .O_t1b(n539_O_t1b)
  );
  Add n540 ( // @[Top.scala 1667:22]
    .valid_up(n540_valid_up),
    .valid_down(n540_valid_down),
    .I_t0b(n540_I_t0b),
    .I_t1b(n540_I_t1b),
    .O(n540_O)
  );
  InitialDelayCounter_45 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n543 ( // @[Top.scala 1671:22]
    .valid_up(n543_valid_up),
    .valid_down(n543_valid_down),
    .I0(n543_I0),
    .O_t0b(n543_O_t0b)
  );
  RShift n544 ( // @[Top.scala 1675:22]
    .valid_up(n544_valid_up),
    .valid_down(n544_valid_down),
    .I_t0b(n544_I_t0b),
    .O(n544_O)
  );
  AtomTuple_1 n545 ( // @[Top.scala 1678:22]
    .valid_up(n545_valid_up),
    .valid_down(n545_valid_down),
    .I0(n545_I0),
    .I1(n545_I1),
    .O_t0b(n545_O_t0b),
    .O_t1b(n545_O_t1b)
  );
  Eq n546 ( // @[Top.scala 1682:22]
    .valid_up(n546_valid_up),
    .valid_down(n546_valid_down),
    .I_t0b(n546_I_t0b),
    .I_t1b(n546_I_t1b),
    .O(n546_O)
  );
  InitialDelayCounter_45 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n549 ( // @[Top.scala 1686:22]
    .valid_up(n549_valid_up),
    .valid_down(n549_valid_down),
    .I0(n549_I0),
    .I1(n549_I1),
    .O_t0b(n549_O_t0b),
    .O_t1b(n549_O_t1b)
  );
  Add n550 ( // @[Top.scala 1690:22]
    .valid_up(n550_valid_up),
    .valid_down(n550_valid_down),
    .I_t0b(n550_I_t0b),
    .I_t1b(n550_I_t1b),
    .O(n550_O)
  );
  AtomTuple_1 n551 ( // @[Top.scala 1693:22]
    .valid_up(n551_valid_up),
    .valid_down(n551_valid_down),
    .I0(n551_I0),
    .I1(n551_I1),
    .O_t0b(n551_O_t0b),
    .O_t1b(n551_O_t1b)
  );
  AtomTuple_9 n552 ( // @[Top.scala 1697:22]
    .valid_up(n552_valid_up),
    .valid_down(n552_valid_down),
    .I0(n552_I0),
    .I1_t0b(n552_I1_t0b),
    .I1_t1b(n552_I1_t1b),
    .O_t0b(n552_O_t0b),
    .O_t1b_t0b(n552_O_t1b_t0b),
    .O_t1b_t1b(n552_O_t1b_t1b)
  );
  If n553 ( // @[Top.scala 1701:22]
    .valid_up(n553_valid_up),
    .valid_down(n553_valid_down),
    .I_t0b(n553_I_t0b),
    .I_t1b_t0b(n553_I_t1b_t0b),
    .I_t1b_t1b(n553_I_t1b_t1b),
    .O(n553_O)
  );
  AtomTuple_1 n554 ( // @[Top.scala 1704:22]
    .valid_up(n554_valid_up),
    .valid_down(n554_valid_down),
    .I0(n554_I0),
    .I1(n554_I1),
    .O_t0b(n554_O_t0b),
    .O_t1b(n554_O_t1b)
  );
  Mul n555 ( // @[Top.scala 1708:22]
    .clock(n555_clock),
    .reset(n555_reset),
    .valid_up(n555_valid_up),
    .valid_down(n555_valid_down),
    .I_t0b(n555_I_t0b),
    .I_t1b(n555_I_t1b),
    .O(n555_O)
  );
  AtomTuple_1 n557 ( // @[Top.scala 1711:22]
    .valid_up(n557_valid_up),
    .valid_down(n557_valid_down),
    .I0(n557_I0),
    .I1(n557_I1),
    .O_t0b(n557_O_t0b),
    .O_t1b(n557_O_t1b)
  );
  Lt n558 ( // @[Top.scala 1715:22]
    .valid_up(n558_valid_up),
    .valid_down(n558_valid_down),
    .I_t0b(n558_I_t0b),
    .I_t1b(n558_I_t1b),
    .O(n558_O)
  );
  InitialDelayCounter_45 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n560 ( // @[Top.scala 1719:22]
    .valid_up(n560_valid_up),
    .valid_down(n560_valid_down),
    .I0(n560_I0),
    .I1(n560_I1),
    .O_t0b(n560_O_t0b),
    .O_t1b(n560_O_t1b)
  );
  Sub n561 ( // @[Top.scala 1723:22]
    .valid_up(n561_valid_up),
    .valid_down(n561_valid_down),
    .I_t0b(n561_I_t0b),
    .I_t1b(n561_I_t1b),
    .O(n561_O)
  );
  AtomTuple_1 n562 ( // @[Top.scala 1726:22]
    .valid_up(n562_valid_up),
    .valid_down(n562_valid_down),
    .I0(n562_I0),
    .I1(n562_I1),
    .O_t0b(n562_O_t0b),
    .O_t1b(n562_O_t1b)
  );
  AtomTuple_1 n563 ( // @[Top.scala 1730:22]
    .valid_up(n563_valid_up),
    .valid_down(n563_valid_down),
    .I0(n563_I0),
    .I1(n563_I1),
    .O_t0b(n563_O_t0b),
    .O_t1b(n563_O_t1b)
  );
  AtomTuple_15 n564 ( // @[Top.scala 1734:22]
    .valid_up(n564_valid_up),
    .valid_down(n564_valid_down),
    .I0_t0b(n564_I0_t0b),
    .I0_t1b(n564_I0_t1b),
    .I1_t0b(n564_I1_t0b),
    .I1_t1b(n564_I1_t1b),
    .O_t0b_t0b(n564_O_t0b_t0b),
    .O_t0b_t1b(n564_O_t0b_t1b),
    .O_t1b_t0b(n564_O_t1b_t0b),
    .O_t1b_t1b(n564_O_t1b_t1b)
  );
  FIFO_3 n565 ( // @[Top.scala 1738:22]
    .clock(n565_clock),
    .reset(n565_reset),
    .valid_up(n565_valid_up),
    .valid_down(n565_valid_down),
    .I_t0b_t0b(n565_I_t0b_t0b),
    .I_t0b_t1b(n565_I_t0b_t1b),
    .I_t1b_t0b(n565_I_t1b_t0b),
    .I_t1b_t1b(n565_I_t1b_t1b),
    .O_t0b_t0b(n565_O_t0b_t0b),
    .O_t0b_t1b(n565_O_t0b_t1b),
    .O_t1b_t0b(n565_O_t1b_t0b),
    .O_t1b_t1b(n565_O_t1b_t1b)
  );
  AtomTuple_16 n566 ( // @[Top.scala 1741:22]
    .valid_up(n566_valid_up),
    .valid_down(n566_valid_down),
    .I0(n566_I0),
    .I1_t0b_t0b(n566_I1_t0b_t0b),
    .I1_t0b_t1b(n566_I1_t0b_t1b),
    .I1_t1b_t0b(n566_I1_t1b_t0b),
    .I1_t1b_t1b(n566_I1_t1b_t1b),
    .O_t0b(n566_O_t0b),
    .O_t1b_t0b_t0b(n566_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n566_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n566_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n566_O_t1b_t1b_t1b)
  );
  If_1 n567 ( // @[Top.scala 1745:22]
    .valid_up(n567_valid_up),
    .valid_down(n567_valid_down),
    .I_t0b(n567_I_t0b),
    .I_t1b_t0b_t0b(n567_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n567_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n567_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n567_I_t1b_t1b_t1b),
    .O_t0b(n567_O_t0b),
    .O_t1b(n567_O_t1b)
  );
  AtomTuple_3 n569 ( // @[Top.scala 1748:22]
    .valid_up(n569_valid_up),
    .valid_down(n569_valid_down),
    .I0(n569_I0),
    .I1_t0b(n569_I1_t0b),
    .I1_t1b(n569_I1_t1b),
    .O_t0b(n569_O_t0b),
    .O_t1b_t0b(n569_O_t1b_t0b),
    .O_t1b_t1b(n569_O_t1b_t1b)
  );
  assign valid_down = n569_valid_down; // @[Top.scala 1753:16]
  assign O_t0b = n569_O_t0b; // @[Top.scala 1752:7]
  assign O_t1b_t0b = n569_O_t1b_t0b; // @[Top.scala 1752:7]
  assign O_t1b_t1b = n569_O_t1b_t1b; // @[Top.scala 1752:7]
  assign n535_valid_up = valid_up; // @[Top.scala 1647:19]
  assign n535_I_t0b = I_t0b; // @[Top.scala 1646:12]
  assign n568_clock = clock;
  assign n568_reset = reset;
  assign n568_valid_up = n535_valid_down; // @[Top.scala 1650:19]
  assign n568_I = n535_O; // @[Top.scala 1649:12]
  assign n556_clock = clock;
  assign n556_reset = reset;
  assign n556_valid_up = n535_valid_down; // @[Top.scala 1653:19]
  assign n556_I = n535_O; // @[Top.scala 1652:12]
  assign n536_valid_up = valid_up; // @[Top.scala 1656:19]
  assign n536_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1655:12]
  assign n536_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1655:12]
  assign n537_valid_up = n536_valid_down; // @[Top.scala 1659:19]
  assign n537_I_t0b = n536_O_t0b; // @[Top.scala 1658:12]
  assign n538_valid_up = n536_valid_down; // @[Top.scala 1662:19]
  assign n538_I_t1b = n536_O_t1b; // @[Top.scala 1661:12]
  assign n539_valid_up = n537_valid_down & n538_valid_down; // @[Top.scala 1666:19]
  assign n539_I0 = n537_O; // @[Top.scala 1664:13]
  assign n539_I1 = n538_O; // @[Top.scala 1665:13]
  assign n540_valid_up = n539_valid_down; // @[Top.scala 1669:19]
  assign n540_I_t0b = n539_O_t0b; // @[Top.scala 1668:12]
  assign n540_I_t1b = n539_O_t1b; // @[Top.scala 1668:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n543_valid_up = n540_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 1674:19]
  assign n543_I0 = n540_O; // @[Top.scala 1672:13]
  assign n544_valid_up = n543_valid_down; // @[Top.scala 1677:19]
  assign n544_I_t0b = n543_O_t0b; // @[Top.scala 1676:12]
  assign n545_valid_up = n544_valid_down & n537_valid_down; // @[Top.scala 1681:19]
  assign n545_I0 = n544_O; // @[Top.scala 1679:13]
  assign n545_I1 = n537_O; // @[Top.scala 1680:13]
  assign n546_valid_up = n545_valid_down; // @[Top.scala 1684:19]
  assign n546_I_t0b = n545_O_t0b; // @[Top.scala 1683:12]
  assign n546_I_t1b = n545_O_t1b; // @[Top.scala 1683:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n549_valid_up = n544_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1689:19]
  assign n549_I0 = n544_O; // @[Top.scala 1687:13]
  assign n549_I1 = 16'h1; // @[Top.scala 1688:13]
  assign n550_valid_up = n549_valid_down; // @[Top.scala 1692:19]
  assign n550_I_t0b = n549_O_t0b; // @[Top.scala 1691:12]
  assign n550_I_t1b = n549_O_t1b; // @[Top.scala 1691:12]
  assign n551_valid_up = n550_valid_down & n544_valid_down; // @[Top.scala 1696:19]
  assign n551_I0 = n550_O; // @[Top.scala 1694:13]
  assign n551_I1 = n544_O; // @[Top.scala 1695:13]
  assign n552_valid_up = n546_valid_down & n551_valid_down; // @[Top.scala 1700:19]
  assign n552_I0 = n546_O[0]; // @[Top.scala 1698:13]
  assign n552_I1_t0b = n551_O_t0b; // @[Top.scala 1699:13]
  assign n552_I1_t1b = n551_O_t1b; // @[Top.scala 1699:13]
  assign n553_valid_up = n552_valid_down; // @[Top.scala 1703:19]
  assign n553_I_t0b = n552_O_t0b; // @[Top.scala 1702:12]
  assign n553_I_t1b_t0b = n552_O_t1b_t0b; // @[Top.scala 1702:12]
  assign n553_I_t1b_t1b = n552_O_t1b_t1b; // @[Top.scala 1702:12]
  assign n554_valid_up = n553_valid_down; // @[Top.scala 1707:19]
  assign n554_I0 = n553_O; // @[Top.scala 1705:13]
  assign n554_I1 = n553_O; // @[Top.scala 1706:13]
  assign n555_clock = clock;
  assign n555_reset = reset;
  assign n555_valid_up = n554_valid_down; // @[Top.scala 1710:19]
  assign n555_I_t0b = n554_O_t0b; // @[Top.scala 1709:12]
  assign n555_I_t1b = n554_O_t1b; // @[Top.scala 1709:12]
  assign n557_valid_up = n556_valid_down & n555_valid_down; // @[Top.scala 1714:19]
  assign n557_I0 = n556_O; // @[Top.scala 1712:13]
  assign n557_I1 = n555_O; // @[Top.scala 1713:13]
  assign n558_valid_up = n557_valid_down; // @[Top.scala 1717:19]
  assign n558_I_t0b = n557_O_t0b; // @[Top.scala 1716:12]
  assign n558_I_t1b = n557_O_t1b; // @[Top.scala 1716:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n560_valid_up = n553_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1722:19]
  assign n560_I0 = n553_O; // @[Top.scala 1720:13]
  assign n560_I1 = 16'h1; // @[Top.scala 1721:13]
  assign n561_valid_up = n560_valid_down; // @[Top.scala 1725:19]
  assign n561_I_t0b = n560_O_t0b; // @[Top.scala 1724:12]
  assign n561_I_t1b = n560_O_t1b; // @[Top.scala 1724:12]
  assign n562_valid_up = n537_valid_down & n561_valid_down; // @[Top.scala 1729:19]
  assign n562_I0 = n537_O; // @[Top.scala 1727:13]
  assign n562_I1 = n561_O; // @[Top.scala 1728:13]
  assign n563_valid_up = n553_valid_down & n538_valid_down; // @[Top.scala 1733:19]
  assign n563_I0 = n553_O; // @[Top.scala 1731:13]
  assign n563_I1 = n538_O; // @[Top.scala 1732:13]
  assign n564_valid_up = n562_valid_down & n563_valid_down; // @[Top.scala 1737:19]
  assign n564_I0_t0b = n562_O_t0b; // @[Top.scala 1735:13]
  assign n564_I0_t1b = n562_O_t1b; // @[Top.scala 1735:13]
  assign n564_I1_t0b = n563_O_t0b; // @[Top.scala 1736:13]
  assign n564_I1_t1b = n563_O_t1b; // @[Top.scala 1736:13]
  assign n565_clock = clock;
  assign n565_reset = reset;
  assign n565_valid_up = n564_valid_down; // @[Top.scala 1740:19]
  assign n565_I_t0b_t0b = n564_O_t0b_t0b; // @[Top.scala 1739:12]
  assign n565_I_t0b_t1b = n564_O_t0b_t1b; // @[Top.scala 1739:12]
  assign n565_I_t1b_t0b = n564_O_t1b_t0b; // @[Top.scala 1739:12]
  assign n565_I_t1b_t1b = n564_O_t1b_t1b; // @[Top.scala 1739:12]
  assign n566_valid_up = n558_valid_down & n565_valid_down; // @[Top.scala 1744:19]
  assign n566_I0 = n558_O[0]; // @[Top.scala 1742:13]
  assign n566_I1_t0b_t0b = n565_O_t0b_t0b; // @[Top.scala 1743:13]
  assign n566_I1_t0b_t1b = n565_O_t0b_t1b; // @[Top.scala 1743:13]
  assign n566_I1_t1b_t0b = n565_O_t1b_t0b; // @[Top.scala 1743:13]
  assign n566_I1_t1b_t1b = n565_O_t1b_t1b; // @[Top.scala 1743:13]
  assign n567_valid_up = n566_valid_down; // @[Top.scala 1747:19]
  assign n567_I_t0b = n566_O_t0b; // @[Top.scala 1746:12]
  assign n567_I_t1b_t0b_t0b = n566_O_t1b_t0b_t0b; // @[Top.scala 1746:12]
  assign n567_I_t1b_t0b_t1b = n566_O_t1b_t0b_t1b; // @[Top.scala 1746:12]
  assign n567_I_t1b_t1b_t0b = n566_O_t1b_t1b_t0b; // @[Top.scala 1746:12]
  assign n567_I_t1b_t1b_t1b = n566_O_t1b_t1b_t1b; // @[Top.scala 1746:12]
  assign n569_valid_up = n568_valid_down & n567_valid_down; // @[Top.scala 1751:19]
  assign n569_I0 = n568_O; // @[Top.scala 1749:13]
  assign n569_I1_t0b = n567_O_t0b; // @[Top.scala 1750:13]
  assign n569_I1_t1b = n567_O_t1b; // @[Top.scala 1750:13]
endmodule
module MapT_15(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_15 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
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
  wire  n572_valid_up; // @[Top.scala 1759:22]
  wire  n572_valid_down; // @[Top.scala 1759:22]
  wire [15:0] n572_I_t0b; // @[Top.scala 1759:22]
  wire [15:0] n572_O; // @[Top.scala 1759:22]
  wire  n605_clock; // @[Top.scala 1762:22]
  wire  n605_reset; // @[Top.scala 1762:22]
  wire  n605_valid_up; // @[Top.scala 1762:22]
  wire  n605_valid_down; // @[Top.scala 1762:22]
  wire [15:0] n605_I; // @[Top.scala 1762:22]
  wire [15:0] n605_O; // @[Top.scala 1762:22]
  wire  n593_clock; // @[Top.scala 1765:22]
  wire  n593_reset; // @[Top.scala 1765:22]
  wire  n593_valid_up; // @[Top.scala 1765:22]
  wire  n593_valid_down; // @[Top.scala 1765:22]
  wire [15:0] n593_I; // @[Top.scala 1765:22]
  wire [15:0] n593_O; // @[Top.scala 1765:22]
  wire  n573_valid_up; // @[Top.scala 1768:22]
  wire  n573_valid_down; // @[Top.scala 1768:22]
  wire [15:0] n573_I_t1b_t0b; // @[Top.scala 1768:22]
  wire [15:0] n573_I_t1b_t1b; // @[Top.scala 1768:22]
  wire [15:0] n573_O_t0b; // @[Top.scala 1768:22]
  wire [15:0] n573_O_t1b; // @[Top.scala 1768:22]
  wire  n574_valid_up; // @[Top.scala 1771:22]
  wire  n574_valid_down; // @[Top.scala 1771:22]
  wire [15:0] n574_I_t0b; // @[Top.scala 1771:22]
  wire [15:0] n574_O; // @[Top.scala 1771:22]
  wire  n575_valid_up; // @[Top.scala 1774:22]
  wire  n575_valid_down; // @[Top.scala 1774:22]
  wire [15:0] n575_I_t1b; // @[Top.scala 1774:22]
  wire [15:0] n575_O; // @[Top.scala 1774:22]
  wire  n576_valid_up; // @[Top.scala 1777:22]
  wire  n576_valid_down; // @[Top.scala 1777:22]
  wire [15:0] n576_I0; // @[Top.scala 1777:22]
  wire [15:0] n576_I1; // @[Top.scala 1777:22]
  wire [15:0] n576_O_t0b; // @[Top.scala 1777:22]
  wire [15:0] n576_O_t1b; // @[Top.scala 1777:22]
  wire  n577_valid_up; // @[Top.scala 1781:22]
  wire  n577_valid_down; // @[Top.scala 1781:22]
  wire [15:0] n577_I_t0b; // @[Top.scala 1781:22]
  wire [15:0] n577_I_t1b; // @[Top.scala 1781:22]
  wire [15:0] n577_O; // @[Top.scala 1781:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n580_valid_up; // @[Top.scala 1785:22]
  wire  n580_valid_down; // @[Top.scala 1785:22]
  wire [15:0] n580_I0; // @[Top.scala 1785:22]
  wire [15:0] n580_O_t0b; // @[Top.scala 1785:22]
  wire  n581_valid_up; // @[Top.scala 1789:22]
  wire  n581_valid_down; // @[Top.scala 1789:22]
  wire [15:0] n581_I_t0b; // @[Top.scala 1789:22]
  wire [15:0] n581_O; // @[Top.scala 1789:22]
  wire  n582_valid_up; // @[Top.scala 1792:22]
  wire  n582_valid_down; // @[Top.scala 1792:22]
  wire [15:0] n582_I0; // @[Top.scala 1792:22]
  wire [15:0] n582_I1; // @[Top.scala 1792:22]
  wire [15:0] n582_O_t0b; // @[Top.scala 1792:22]
  wire [15:0] n582_O_t1b; // @[Top.scala 1792:22]
  wire  n583_valid_up; // @[Top.scala 1796:22]
  wire  n583_valid_down; // @[Top.scala 1796:22]
  wire [15:0] n583_I_t0b; // @[Top.scala 1796:22]
  wire [15:0] n583_I_t1b; // @[Top.scala 1796:22]
  wire [15:0] n583_O; // @[Top.scala 1796:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n586_valid_up; // @[Top.scala 1800:22]
  wire  n586_valid_down; // @[Top.scala 1800:22]
  wire [15:0] n586_I0; // @[Top.scala 1800:22]
  wire [15:0] n586_I1; // @[Top.scala 1800:22]
  wire [15:0] n586_O_t0b; // @[Top.scala 1800:22]
  wire [15:0] n586_O_t1b; // @[Top.scala 1800:22]
  wire  n587_valid_up; // @[Top.scala 1804:22]
  wire  n587_valid_down; // @[Top.scala 1804:22]
  wire [15:0] n587_I_t0b; // @[Top.scala 1804:22]
  wire [15:0] n587_I_t1b; // @[Top.scala 1804:22]
  wire [15:0] n587_O; // @[Top.scala 1804:22]
  wire  n588_valid_up; // @[Top.scala 1807:22]
  wire  n588_valid_down; // @[Top.scala 1807:22]
  wire [15:0] n588_I0; // @[Top.scala 1807:22]
  wire [15:0] n588_I1; // @[Top.scala 1807:22]
  wire [15:0] n588_O_t0b; // @[Top.scala 1807:22]
  wire [15:0] n588_O_t1b; // @[Top.scala 1807:22]
  wire  n589_valid_up; // @[Top.scala 1811:22]
  wire  n589_valid_down; // @[Top.scala 1811:22]
  wire  n589_I0; // @[Top.scala 1811:22]
  wire [15:0] n589_I1_t0b; // @[Top.scala 1811:22]
  wire [15:0] n589_I1_t1b; // @[Top.scala 1811:22]
  wire  n589_O_t0b; // @[Top.scala 1811:22]
  wire [15:0] n589_O_t1b_t0b; // @[Top.scala 1811:22]
  wire [15:0] n589_O_t1b_t1b; // @[Top.scala 1811:22]
  wire  n590_valid_up; // @[Top.scala 1815:22]
  wire  n590_valid_down; // @[Top.scala 1815:22]
  wire  n590_I_t0b; // @[Top.scala 1815:22]
  wire [15:0] n590_I_t1b_t0b; // @[Top.scala 1815:22]
  wire [15:0] n590_I_t1b_t1b; // @[Top.scala 1815:22]
  wire [15:0] n590_O; // @[Top.scala 1815:22]
  wire  n591_valid_up; // @[Top.scala 1818:22]
  wire  n591_valid_down; // @[Top.scala 1818:22]
  wire [15:0] n591_I0; // @[Top.scala 1818:22]
  wire [15:0] n591_I1; // @[Top.scala 1818:22]
  wire [15:0] n591_O_t0b; // @[Top.scala 1818:22]
  wire [15:0] n591_O_t1b; // @[Top.scala 1818:22]
  wire  n592_clock; // @[Top.scala 1822:22]
  wire  n592_reset; // @[Top.scala 1822:22]
  wire  n592_valid_up; // @[Top.scala 1822:22]
  wire  n592_valid_down; // @[Top.scala 1822:22]
  wire [15:0] n592_I_t0b; // @[Top.scala 1822:22]
  wire [15:0] n592_I_t1b; // @[Top.scala 1822:22]
  wire [15:0] n592_O; // @[Top.scala 1822:22]
  wire  n594_valid_up; // @[Top.scala 1825:22]
  wire  n594_valid_down; // @[Top.scala 1825:22]
  wire [15:0] n594_I0; // @[Top.scala 1825:22]
  wire [15:0] n594_I1; // @[Top.scala 1825:22]
  wire [15:0] n594_O_t0b; // @[Top.scala 1825:22]
  wire [15:0] n594_O_t1b; // @[Top.scala 1825:22]
  wire  n595_valid_up; // @[Top.scala 1829:22]
  wire  n595_valid_down; // @[Top.scala 1829:22]
  wire [15:0] n595_I_t0b; // @[Top.scala 1829:22]
  wire [15:0] n595_I_t1b; // @[Top.scala 1829:22]
  wire [15:0] n595_O; // @[Top.scala 1829:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n597_valid_up; // @[Top.scala 1833:22]
  wire  n597_valid_down; // @[Top.scala 1833:22]
  wire [15:0] n597_I0; // @[Top.scala 1833:22]
  wire [15:0] n597_I1; // @[Top.scala 1833:22]
  wire [15:0] n597_O_t0b; // @[Top.scala 1833:22]
  wire [15:0] n597_O_t1b; // @[Top.scala 1833:22]
  wire  n598_valid_up; // @[Top.scala 1837:22]
  wire  n598_valid_down; // @[Top.scala 1837:22]
  wire [15:0] n598_I_t0b; // @[Top.scala 1837:22]
  wire [15:0] n598_I_t1b; // @[Top.scala 1837:22]
  wire [15:0] n598_O; // @[Top.scala 1837:22]
  wire  n599_valid_up; // @[Top.scala 1840:22]
  wire  n599_valid_down; // @[Top.scala 1840:22]
  wire [15:0] n599_I0; // @[Top.scala 1840:22]
  wire [15:0] n599_I1; // @[Top.scala 1840:22]
  wire [15:0] n599_O_t0b; // @[Top.scala 1840:22]
  wire [15:0] n599_O_t1b; // @[Top.scala 1840:22]
  wire  n600_valid_up; // @[Top.scala 1844:22]
  wire  n600_valid_down; // @[Top.scala 1844:22]
  wire [15:0] n600_I0; // @[Top.scala 1844:22]
  wire [15:0] n600_I1; // @[Top.scala 1844:22]
  wire [15:0] n600_O_t0b; // @[Top.scala 1844:22]
  wire [15:0] n600_O_t1b; // @[Top.scala 1844:22]
  wire  n601_valid_up; // @[Top.scala 1848:22]
  wire  n601_valid_down; // @[Top.scala 1848:22]
  wire [15:0] n601_I0_t0b; // @[Top.scala 1848:22]
  wire [15:0] n601_I0_t1b; // @[Top.scala 1848:22]
  wire [15:0] n601_I1_t0b; // @[Top.scala 1848:22]
  wire [15:0] n601_I1_t1b; // @[Top.scala 1848:22]
  wire [15:0] n601_O_t0b_t0b; // @[Top.scala 1848:22]
  wire [15:0] n601_O_t0b_t1b; // @[Top.scala 1848:22]
  wire [15:0] n601_O_t1b_t0b; // @[Top.scala 1848:22]
  wire [15:0] n601_O_t1b_t1b; // @[Top.scala 1848:22]
  wire  n602_clock; // @[Top.scala 1852:22]
  wire  n602_reset; // @[Top.scala 1852:22]
  wire  n602_valid_up; // @[Top.scala 1852:22]
  wire  n602_valid_down; // @[Top.scala 1852:22]
  wire [15:0] n602_I_t0b_t0b; // @[Top.scala 1852:22]
  wire [15:0] n602_I_t0b_t1b; // @[Top.scala 1852:22]
  wire [15:0] n602_I_t1b_t0b; // @[Top.scala 1852:22]
  wire [15:0] n602_I_t1b_t1b; // @[Top.scala 1852:22]
  wire [15:0] n602_O_t0b_t0b; // @[Top.scala 1852:22]
  wire [15:0] n602_O_t0b_t1b; // @[Top.scala 1852:22]
  wire [15:0] n602_O_t1b_t0b; // @[Top.scala 1852:22]
  wire [15:0] n602_O_t1b_t1b; // @[Top.scala 1852:22]
  wire  n603_valid_up; // @[Top.scala 1855:22]
  wire  n603_valid_down; // @[Top.scala 1855:22]
  wire  n603_I0; // @[Top.scala 1855:22]
  wire [15:0] n603_I1_t0b_t0b; // @[Top.scala 1855:22]
  wire [15:0] n603_I1_t0b_t1b; // @[Top.scala 1855:22]
  wire [15:0] n603_I1_t1b_t0b; // @[Top.scala 1855:22]
  wire [15:0] n603_I1_t1b_t1b; // @[Top.scala 1855:22]
  wire  n603_O_t0b; // @[Top.scala 1855:22]
  wire [15:0] n603_O_t1b_t0b_t0b; // @[Top.scala 1855:22]
  wire [15:0] n603_O_t1b_t0b_t1b; // @[Top.scala 1855:22]
  wire [15:0] n603_O_t1b_t1b_t0b; // @[Top.scala 1855:22]
  wire [15:0] n603_O_t1b_t1b_t1b; // @[Top.scala 1855:22]
  wire  n604_valid_up; // @[Top.scala 1859:22]
  wire  n604_valid_down; // @[Top.scala 1859:22]
  wire  n604_I_t0b; // @[Top.scala 1859:22]
  wire [15:0] n604_I_t1b_t0b_t0b; // @[Top.scala 1859:22]
  wire [15:0] n604_I_t1b_t0b_t1b; // @[Top.scala 1859:22]
  wire [15:0] n604_I_t1b_t1b_t0b; // @[Top.scala 1859:22]
  wire [15:0] n604_I_t1b_t1b_t1b; // @[Top.scala 1859:22]
  wire [15:0] n604_O_t0b; // @[Top.scala 1859:22]
  wire [15:0] n604_O_t1b; // @[Top.scala 1859:22]
  wire  n606_valid_up; // @[Top.scala 1862:22]
  wire  n606_valid_down; // @[Top.scala 1862:22]
  wire [15:0] n606_I0; // @[Top.scala 1862:22]
  wire [15:0] n606_I1_t0b; // @[Top.scala 1862:22]
  wire [15:0] n606_I1_t1b; // @[Top.scala 1862:22]
  wire [15:0] n606_O_t0b; // @[Top.scala 1862:22]
  wire [15:0] n606_O_t1b_t0b; // @[Top.scala 1862:22]
  wire [15:0] n606_O_t1b_t1b; // @[Top.scala 1862:22]
  Fst n572 ( // @[Top.scala 1759:22]
    .valid_up(n572_valid_up),
    .valid_down(n572_valid_down),
    .I_t0b(n572_I_t0b),
    .O(n572_O)
  );
  FIFO_1 n605 ( // @[Top.scala 1762:22]
    .clock(n605_clock),
    .reset(n605_reset),
    .valid_up(n605_valid_up),
    .valid_down(n605_valid_down),
    .I(n605_I),
    .O(n605_O)
  );
  FIFO_1 n593 ( // @[Top.scala 1765:22]
    .clock(n593_clock),
    .reset(n593_reset),
    .valid_up(n593_valid_up),
    .valid_down(n593_valid_down),
    .I(n593_I),
    .O(n593_O)
  );
  Snd n573 ( // @[Top.scala 1768:22]
    .valid_up(n573_valid_up),
    .valid_down(n573_valid_down),
    .I_t1b_t0b(n573_I_t1b_t0b),
    .I_t1b_t1b(n573_I_t1b_t1b),
    .O_t0b(n573_O_t0b),
    .O_t1b(n573_O_t1b)
  );
  Fst_1 n574 ( // @[Top.scala 1771:22]
    .valid_up(n574_valid_up),
    .valid_down(n574_valid_down),
    .I_t0b(n574_I_t0b),
    .O(n574_O)
  );
  Snd_1 n575 ( // @[Top.scala 1774:22]
    .valid_up(n575_valid_up),
    .valid_down(n575_valid_down),
    .I_t1b(n575_I_t1b),
    .O(n575_O)
  );
  AtomTuple_1 n576 ( // @[Top.scala 1777:22]
    .valid_up(n576_valid_up),
    .valid_down(n576_valid_down),
    .I0(n576_I0),
    .I1(n576_I1),
    .O_t0b(n576_O_t0b),
    .O_t1b(n576_O_t1b)
  );
  Add n577 ( // @[Top.scala 1781:22]
    .valid_up(n577_valid_up),
    .valid_down(n577_valid_down),
    .I_t0b(n577_I_t0b),
    .I_t1b(n577_I_t1b),
    .O(n577_O)
  );
  InitialDelayCounter_48 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n580 ( // @[Top.scala 1785:22]
    .valid_up(n580_valid_up),
    .valid_down(n580_valid_down),
    .I0(n580_I0),
    .O_t0b(n580_O_t0b)
  );
  RShift n581 ( // @[Top.scala 1789:22]
    .valid_up(n581_valid_up),
    .valid_down(n581_valid_down),
    .I_t0b(n581_I_t0b),
    .O(n581_O)
  );
  AtomTuple_1 n582 ( // @[Top.scala 1792:22]
    .valid_up(n582_valid_up),
    .valid_down(n582_valid_down),
    .I0(n582_I0),
    .I1(n582_I1),
    .O_t0b(n582_O_t0b),
    .O_t1b(n582_O_t1b)
  );
  Eq n583 ( // @[Top.scala 1796:22]
    .valid_up(n583_valid_up),
    .valid_down(n583_valid_down),
    .I_t0b(n583_I_t0b),
    .I_t1b(n583_I_t1b),
    .O(n583_O)
  );
  InitialDelayCounter_48 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_1 n586 ( // @[Top.scala 1800:22]
    .valid_up(n586_valid_up),
    .valid_down(n586_valid_down),
    .I0(n586_I0),
    .I1(n586_I1),
    .O_t0b(n586_O_t0b),
    .O_t1b(n586_O_t1b)
  );
  Add n587 ( // @[Top.scala 1804:22]
    .valid_up(n587_valid_up),
    .valid_down(n587_valid_down),
    .I_t0b(n587_I_t0b),
    .I_t1b(n587_I_t1b),
    .O(n587_O)
  );
  AtomTuple_1 n588 ( // @[Top.scala 1807:22]
    .valid_up(n588_valid_up),
    .valid_down(n588_valid_down),
    .I0(n588_I0),
    .I1(n588_I1),
    .O_t0b(n588_O_t0b),
    .O_t1b(n588_O_t1b)
  );
  AtomTuple_9 n589 ( // @[Top.scala 1811:22]
    .valid_up(n589_valid_up),
    .valid_down(n589_valid_down),
    .I0(n589_I0),
    .I1_t0b(n589_I1_t0b),
    .I1_t1b(n589_I1_t1b),
    .O_t0b(n589_O_t0b),
    .O_t1b_t0b(n589_O_t1b_t0b),
    .O_t1b_t1b(n589_O_t1b_t1b)
  );
  If n590 ( // @[Top.scala 1815:22]
    .valid_up(n590_valid_up),
    .valid_down(n590_valid_down),
    .I_t0b(n590_I_t0b),
    .I_t1b_t0b(n590_I_t1b_t0b),
    .I_t1b_t1b(n590_I_t1b_t1b),
    .O(n590_O)
  );
  AtomTuple_1 n591 ( // @[Top.scala 1818:22]
    .valid_up(n591_valid_up),
    .valid_down(n591_valid_down),
    .I0(n591_I0),
    .I1(n591_I1),
    .O_t0b(n591_O_t0b),
    .O_t1b(n591_O_t1b)
  );
  Mul n592 ( // @[Top.scala 1822:22]
    .clock(n592_clock),
    .reset(n592_reset),
    .valid_up(n592_valid_up),
    .valid_down(n592_valid_down),
    .I_t0b(n592_I_t0b),
    .I_t1b(n592_I_t1b),
    .O(n592_O)
  );
  AtomTuple_1 n594 ( // @[Top.scala 1825:22]
    .valid_up(n594_valid_up),
    .valid_down(n594_valid_down),
    .I0(n594_I0),
    .I1(n594_I1),
    .O_t0b(n594_O_t0b),
    .O_t1b(n594_O_t1b)
  );
  Lt n595 ( // @[Top.scala 1829:22]
    .valid_up(n595_valid_up),
    .valid_down(n595_valid_down),
    .I_t0b(n595_I_t0b),
    .I_t1b(n595_I_t1b),
    .O(n595_O)
  );
  InitialDelayCounter_48 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple_1 n597 ( // @[Top.scala 1833:22]
    .valid_up(n597_valid_up),
    .valid_down(n597_valid_down),
    .I0(n597_I0),
    .I1(n597_I1),
    .O_t0b(n597_O_t0b),
    .O_t1b(n597_O_t1b)
  );
  Sub n598 ( // @[Top.scala 1837:22]
    .valid_up(n598_valid_up),
    .valid_down(n598_valid_down),
    .I_t0b(n598_I_t0b),
    .I_t1b(n598_I_t1b),
    .O(n598_O)
  );
  AtomTuple_1 n599 ( // @[Top.scala 1840:22]
    .valid_up(n599_valid_up),
    .valid_down(n599_valid_down),
    .I0(n599_I0),
    .I1(n599_I1),
    .O_t0b(n599_O_t0b),
    .O_t1b(n599_O_t1b)
  );
  AtomTuple_1 n600 ( // @[Top.scala 1844:22]
    .valid_up(n600_valid_up),
    .valid_down(n600_valid_down),
    .I0(n600_I0),
    .I1(n600_I1),
    .O_t0b(n600_O_t0b),
    .O_t1b(n600_O_t1b)
  );
  AtomTuple_15 n601 ( // @[Top.scala 1848:22]
    .valid_up(n601_valid_up),
    .valid_down(n601_valid_down),
    .I0_t0b(n601_I0_t0b),
    .I0_t1b(n601_I0_t1b),
    .I1_t0b(n601_I1_t0b),
    .I1_t1b(n601_I1_t1b),
    .O_t0b_t0b(n601_O_t0b_t0b),
    .O_t0b_t1b(n601_O_t0b_t1b),
    .O_t1b_t0b(n601_O_t1b_t0b),
    .O_t1b_t1b(n601_O_t1b_t1b)
  );
  FIFO_3 n602 ( // @[Top.scala 1852:22]
    .clock(n602_clock),
    .reset(n602_reset),
    .valid_up(n602_valid_up),
    .valid_down(n602_valid_down),
    .I_t0b_t0b(n602_I_t0b_t0b),
    .I_t0b_t1b(n602_I_t0b_t1b),
    .I_t1b_t0b(n602_I_t1b_t0b),
    .I_t1b_t1b(n602_I_t1b_t1b),
    .O_t0b_t0b(n602_O_t0b_t0b),
    .O_t0b_t1b(n602_O_t0b_t1b),
    .O_t1b_t0b(n602_O_t1b_t0b),
    .O_t1b_t1b(n602_O_t1b_t1b)
  );
  AtomTuple_16 n603 ( // @[Top.scala 1855:22]
    .valid_up(n603_valid_up),
    .valid_down(n603_valid_down),
    .I0(n603_I0),
    .I1_t0b_t0b(n603_I1_t0b_t0b),
    .I1_t0b_t1b(n603_I1_t0b_t1b),
    .I1_t1b_t0b(n603_I1_t1b_t0b),
    .I1_t1b_t1b(n603_I1_t1b_t1b),
    .O_t0b(n603_O_t0b),
    .O_t1b_t0b_t0b(n603_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n603_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n603_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n603_O_t1b_t1b_t1b)
  );
  If_1 n604 ( // @[Top.scala 1859:22]
    .valid_up(n604_valid_up),
    .valid_down(n604_valid_down),
    .I_t0b(n604_I_t0b),
    .I_t1b_t0b_t0b(n604_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n604_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n604_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n604_I_t1b_t1b_t1b),
    .O_t0b(n604_O_t0b),
    .O_t1b(n604_O_t1b)
  );
  AtomTuple_3 n606 ( // @[Top.scala 1862:22]
    .valid_up(n606_valid_up),
    .valid_down(n606_valid_down),
    .I0(n606_I0),
    .I1_t0b(n606_I1_t0b),
    .I1_t1b(n606_I1_t1b),
    .O_t0b(n606_O_t0b),
    .O_t1b_t0b(n606_O_t1b_t0b),
    .O_t1b_t1b(n606_O_t1b_t1b)
  );
  assign valid_down = n606_valid_down; // @[Top.scala 1867:16]
  assign O_t1b_t0b = n606_O_t1b_t0b; // @[Top.scala 1866:7]
  assign O_t1b_t1b = n606_O_t1b_t1b; // @[Top.scala 1866:7]
  assign n572_valid_up = valid_up; // @[Top.scala 1761:19]
  assign n572_I_t0b = I_t0b; // @[Top.scala 1760:12]
  assign n605_clock = clock;
  assign n605_reset = reset;
  assign n605_valid_up = n572_valid_down; // @[Top.scala 1764:19]
  assign n605_I = n572_O; // @[Top.scala 1763:12]
  assign n593_clock = clock;
  assign n593_reset = reset;
  assign n593_valid_up = n572_valid_down; // @[Top.scala 1767:19]
  assign n593_I = n572_O; // @[Top.scala 1766:12]
  assign n573_valid_up = valid_up; // @[Top.scala 1770:19]
  assign n573_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1769:12]
  assign n573_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1769:12]
  assign n574_valid_up = n573_valid_down; // @[Top.scala 1773:19]
  assign n574_I_t0b = n573_O_t0b; // @[Top.scala 1772:12]
  assign n575_valid_up = n573_valid_down; // @[Top.scala 1776:19]
  assign n575_I_t1b = n573_O_t1b; // @[Top.scala 1775:12]
  assign n576_valid_up = n574_valid_down & n575_valid_down; // @[Top.scala 1780:19]
  assign n576_I0 = n574_O; // @[Top.scala 1778:13]
  assign n576_I1 = n575_O; // @[Top.scala 1779:13]
  assign n577_valid_up = n576_valid_down; // @[Top.scala 1783:19]
  assign n577_I_t0b = n576_O_t0b; // @[Top.scala 1782:12]
  assign n577_I_t1b = n576_O_t1b; // @[Top.scala 1782:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n580_valid_up = n577_valid_down & InitialDelayCounter_valid_down; // @[Top.scala 1788:19]
  assign n580_I0 = n577_O; // @[Top.scala 1786:13]
  assign n581_valid_up = n580_valid_down; // @[Top.scala 1791:19]
  assign n581_I_t0b = n580_O_t0b; // @[Top.scala 1790:12]
  assign n582_valid_up = n581_valid_down & n574_valid_down; // @[Top.scala 1795:19]
  assign n582_I0 = n581_O; // @[Top.scala 1793:13]
  assign n582_I1 = n574_O; // @[Top.scala 1794:13]
  assign n583_valid_up = n582_valid_down; // @[Top.scala 1798:19]
  assign n583_I_t0b = n582_O_t0b; // @[Top.scala 1797:12]
  assign n583_I_t1b = n582_O_t1b; // @[Top.scala 1797:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n586_valid_up = n581_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1803:19]
  assign n586_I0 = n581_O; // @[Top.scala 1801:13]
  assign n586_I1 = 16'h1; // @[Top.scala 1802:13]
  assign n587_valid_up = n586_valid_down; // @[Top.scala 1806:19]
  assign n587_I_t0b = n586_O_t0b; // @[Top.scala 1805:12]
  assign n587_I_t1b = n586_O_t1b; // @[Top.scala 1805:12]
  assign n588_valid_up = n587_valid_down & n581_valid_down; // @[Top.scala 1810:19]
  assign n588_I0 = n587_O; // @[Top.scala 1808:13]
  assign n588_I1 = n581_O; // @[Top.scala 1809:13]
  assign n589_valid_up = n583_valid_down & n588_valid_down; // @[Top.scala 1814:19]
  assign n589_I0 = n583_O[0]; // @[Top.scala 1812:13]
  assign n589_I1_t0b = n588_O_t0b; // @[Top.scala 1813:13]
  assign n589_I1_t1b = n588_O_t1b; // @[Top.scala 1813:13]
  assign n590_valid_up = n589_valid_down; // @[Top.scala 1817:19]
  assign n590_I_t0b = n589_O_t0b; // @[Top.scala 1816:12]
  assign n590_I_t1b_t0b = n589_O_t1b_t0b; // @[Top.scala 1816:12]
  assign n590_I_t1b_t1b = n589_O_t1b_t1b; // @[Top.scala 1816:12]
  assign n591_valid_up = n590_valid_down; // @[Top.scala 1821:19]
  assign n591_I0 = n590_O; // @[Top.scala 1819:13]
  assign n591_I1 = n590_O; // @[Top.scala 1820:13]
  assign n592_clock = clock;
  assign n592_reset = reset;
  assign n592_valid_up = n591_valid_down; // @[Top.scala 1824:19]
  assign n592_I_t0b = n591_O_t0b; // @[Top.scala 1823:12]
  assign n592_I_t1b = n591_O_t1b; // @[Top.scala 1823:12]
  assign n594_valid_up = n593_valid_down & n592_valid_down; // @[Top.scala 1828:19]
  assign n594_I0 = n593_O; // @[Top.scala 1826:13]
  assign n594_I1 = n592_O; // @[Top.scala 1827:13]
  assign n595_valid_up = n594_valid_down; // @[Top.scala 1831:19]
  assign n595_I_t0b = n594_O_t0b; // @[Top.scala 1830:12]
  assign n595_I_t1b = n594_O_t1b; // @[Top.scala 1830:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n597_valid_up = n590_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1836:19]
  assign n597_I0 = n590_O; // @[Top.scala 1834:13]
  assign n597_I1 = 16'h1; // @[Top.scala 1835:13]
  assign n598_valid_up = n597_valid_down; // @[Top.scala 1839:19]
  assign n598_I_t0b = n597_O_t0b; // @[Top.scala 1838:12]
  assign n598_I_t1b = n597_O_t1b; // @[Top.scala 1838:12]
  assign n599_valid_up = n574_valid_down & n598_valid_down; // @[Top.scala 1843:19]
  assign n599_I0 = n574_O; // @[Top.scala 1841:13]
  assign n599_I1 = n598_O; // @[Top.scala 1842:13]
  assign n600_valid_up = n590_valid_down & n575_valid_down; // @[Top.scala 1847:19]
  assign n600_I0 = n590_O; // @[Top.scala 1845:13]
  assign n600_I1 = n575_O; // @[Top.scala 1846:13]
  assign n601_valid_up = n599_valid_down & n600_valid_down; // @[Top.scala 1851:19]
  assign n601_I0_t0b = n599_O_t0b; // @[Top.scala 1849:13]
  assign n601_I0_t1b = n599_O_t1b; // @[Top.scala 1849:13]
  assign n601_I1_t0b = n600_O_t0b; // @[Top.scala 1850:13]
  assign n601_I1_t1b = n600_O_t1b; // @[Top.scala 1850:13]
  assign n602_clock = clock;
  assign n602_reset = reset;
  assign n602_valid_up = n601_valid_down; // @[Top.scala 1854:19]
  assign n602_I_t0b_t0b = n601_O_t0b_t0b; // @[Top.scala 1853:12]
  assign n602_I_t0b_t1b = n601_O_t0b_t1b; // @[Top.scala 1853:12]
  assign n602_I_t1b_t0b = n601_O_t1b_t0b; // @[Top.scala 1853:12]
  assign n602_I_t1b_t1b = n601_O_t1b_t1b; // @[Top.scala 1853:12]
  assign n603_valid_up = n595_valid_down & n602_valid_down; // @[Top.scala 1858:19]
  assign n603_I0 = n595_O[0]; // @[Top.scala 1856:13]
  assign n603_I1_t0b_t0b = n602_O_t0b_t0b; // @[Top.scala 1857:13]
  assign n603_I1_t0b_t1b = n602_O_t0b_t1b; // @[Top.scala 1857:13]
  assign n603_I1_t1b_t0b = n602_O_t1b_t0b; // @[Top.scala 1857:13]
  assign n603_I1_t1b_t1b = n602_O_t1b_t1b; // @[Top.scala 1857:13]
  assign n604_valid_up = n603_valid_down; // @[Top.scala 1861:19]
  assign n604_I_t0b = n603_O_t0b; // @[Top.scala 1860:12]
  assign n604_I_t1b_t0b_t0b = n603_O_t1b_t0b_t0b; // @[Top.scala 1860:12]
  assign n604_I_t1b_t0b_t1b = n603_O_t1b_t0b_t1b; // @[Top.scala 1860:12]
  assign n604_I_t1b_t1b_t0b = n603_O_t1b_t1b_t0b; // @[Top.scala 1860:12]
  assign n604_I_t1b_t1b_t1b = n603_O_t1b_t1b_t1b; // @[Top.scala 1860:12]
  assign n606_valid_up = n605_valid_down & n604_valid_down; // @[Top.scala 1865:19]
  assign n606_I0 = n605_O; // @[Top.scala 1863:13]
  assign n606_I1_t0b = n604_O_t0b; // @[Top.scala 1864:13]
  assign n606_I1_t1b = n604_O_t1b; // @[Top.scala 1864:13]
endmodule
module MapT_16(
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
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  Module_16 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module Module_17(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O
);
  wire  n609_valid_up; // @[Top.scala 1873:22]
  wire  n609_valid_down; // @[Top.scala 1873:22]
  wire [15:0] n609_I_t1b_t0b; // @[Top.scala 1873:22]
  wire [15:0] n609_I_t1b_t1b; // @[Top.scala 1873:22]
  wire [15:0] n609_O_t0b; // @[Top.scala 1873:22]
  wire [15:0] n609_O_t1b; // @[Top.scala 1873:22]
  wire  n610_valid_up; // @[Top.scala 1876:22]
  wire  n610_valid_down; // @[Top.scala 1876:22]
  wire [15:0] n610_I_t0b; // @[Top.scala 1876:22]
  wire [15:0] n610_O; // @[Top.scala 1876:22]
  Snd n609 ( // @[Top.scala 1873:22]
    .valid_up(n609_valid_up),
    .valid_down(n609_valid_down),
    .I_t1b_t0b(n609_I_t1b_t0b),
    .I_t1b_t1b(n609_I_t1b_t1b),
    .O_t0b(n609_O_t0b),
    .O_t1b(n609_O_t1b)
  );
  Fst_1 n610 ( // @[Top.scala 1876:22]
    .valid_up(n610_valid_up),
    .valid_down(n610_valid_down),
    .I_t0b(n610_I_t0b),
    .O(n610_O)
  );
  assign valid_down = n610_valid_down; // @[Top.scala 1880:16]
  assign O = n610_O; // @[Top.scala 1879:7]
  assign n609_valid_up = valid_up; // @[Top.scala 1875:19]
  assign n609_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1874:12]
  assign n609_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1874:12]
  assign n610_valid_up = n609_valid_down; // @[Top.scala 1878:19]
  assign n610_I_t0b = n609_O_t0b; // @[Top.scala 1877:12]
endmodule
module MapT_17(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t1b_t0b,
  input  [15:0] I_t1b_t1b,
  output [15:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [15:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [15:0] op_O; // @[MapT.scala 8:20]
  Module_17 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t1b_t0b(op_I_t1b_t0b),
    .I_t1b_t1b(op_I_t1b_t1b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t1b_t0b = I_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b = I_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module Top(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I,
  output [15:0] O
);
  wire  n1_clock; // @[Top.scala 1886:20]
  wire  n1_reset; // @[Top.scala 1886:20]
  wire  n1_valid_up; // @[Top.scala 1886:20]
  wire  n1_valid_down; // @[Top.scala 1886:20]
  wire [15:0] n1_I; // @[Top.scala 1886:20]
  wire [15:0] n1_O; // @[Top.scala 1886:20]
  wire  n15_clock; // @[Top.scala 1889:21]
  wire  n15_reset; // @[Top.scala 1889:21]
  wire  n15_valid_up; // @[Top.scala 1889:21]
  wire  n15_valid_down; // @[Top.scala 1889:21]
  wire [15:0] n15_I; // @[Top.scala 1889:21]
  wire [15:0] n15_O_t0b; // @[Top.scala 1889:21]
  wire [15:0] n15_O_t1b_t0b; // @[Top.scala 1889:21]
  wire [15:0] n15_O_t1b_t1b; // @[Top.scala 1889:21]
  wire  n52_clock; // @[Top.scala 1892:21]
  wire  n52_reset; // @[Top.scala 1892:21]
  wire  n52_valid_up; // @[Top.scala 1892:21]
  wire  n52_valid_down; // @[Top.scala 1892:21]
  wire [15:0] n52_I_t0b; // @[Top.scala 1892:21]
  wire [15:0] n52_I_t1b_t0b; // @[Top.scala 1892:21]
  wire [15:0] n52_I_t1b_t1b; // @[Top.scala 1892:21]
  wire [15:0] n52_O_t0b; // @[Top.scala 1892:21]
  wire [15:0] n52_O_t1b_t0b; // @[Top.scala 1892:21]
  wire [15:0] n52_O_t1b_t1b; // @[Top.scala 1892:21]
  wire  n89_clock; // @[Top.scala 1895:21]
  wire  n89_reset; // @[Top.scala 1895:21]
  wire  n89_valid_up; // @[Top.scala 1895:21]
  wire  n89_valid_down; // @[Top.scala 1895:21]
  wire [15:0] n89_I_t0b; // @[Top.scala 1895:21]
  wire [15:0] n89_I_t1b_t0b; // @[Top.scala 1895:21]
  wire [15:0] n89_I_t1b_t1b; // @[Top.scala 1895:21]
  wire [15:0] n89_O_t0b; // @[Top.scala 1895:21]
  wire [15:0] n89_O_t1b_t0b; // @[Top.scala 1895:21]
  wire [15:0] n89_O_t1b_t1b; // @[Top.scala 1895:21]
  wire  n126_clock; // @[Top.scala 1898:22]
  wire  n126_reset; // @[Top.scala 1898:22]
  wire  n126_valid_up; // @[Top.scala 1898:22]
  wire  n126_valid_down; // @[Top.scala 1898:22]
  wire [15:0] n126_I_t0b; // @[Top.scala 1898:22]
  wire [15:0] n126_I_t1b_t0b; // @[Top.scala 1898:22]
  wire [15:0] n126_I_t1b_t1b; // @[Top.scala 1898:22]
  wire [15:0] n126_O_t0b; // @[Top.scala 1898:22]
  wire [15:0] n126_O_t1b_t0b; // @[Top.scala 1898:22]
  wire [15:0] n126_O_t1b_t1b; // @[Top.scala 1898:22]
  wire  n163_clock; // @[Top.scala 1901:22]
  wire  n163_reset; // @[Top.scala 1901:22]
  wire  n163_valid_up; // @[Top.scala 1901:22]
  wire  n163_valid_down; // @[Top.scala 1901:22]
  wire [15:0] n163_I_t0b; // @[Top.scala 1901:22]
  wire [15:0] n163_I_t1b_t0b; // @[Top.scala 1901:22]
  wire [15:0] n163_I_t1b_t1b; // @[Top.scala 1901:22]
  wire [15:0] n163_O_t0b; // @[Top.scala 1901:22]
  wire [15:0] n163_O_t1b_t0b; // @[Top.scala 1901:22]
  wire [15:0] n163_O_t1b_t1b; // @[Top.scala 1901:22]
  wire  n200_clock; // @[Top.scala 1904:22]
  wire  n200_reset; // @[Top.scala 1904:22]
  wire  n200_valid_up; // @[Top.scala 1904:22]
  wire  n200_valid_down; // @[Top.scala 1904:22]
  wire [15:0] n200_I_t0b; // @[Top.scala 1904:22]
  wire [15:0] n200_I_t1b_t0b; // @[Top.scala 1904:22]
  wire [15:0] n200_I_t1b_t1b; // @[Top.scala 1904:22]
  wire [15:0] n200_O_t0b; // @[Top.scala 1904:22]
  wire [15:0] n200_O_t1b_t0b; // @[Top.scala 1904:22]
  wire [15:0] n200_O_t1b_t1b; // @[Top.scala 1904:22]
  wire  n237_clock; // @[Top.scala 1907:22]
  wire  n237_reset; // @[Top.scala 1907:22]
  wire  n237_valid_up; // @[Top.scala 1907:22]
  wire  n237_valid_down; // @[Top.scala 1907:22]
  wire [15:0] n237_I_t0b; // @[Top.scala 1907:22]
  wire [15:0] n237_I_t1b_t0b; // @[Top.scala 1907:22]
  wire [15:0] n237_I_t1b_t1b; // @[Top.scala 1907:22]
  wire [15:0] n237_O_t0b; // @[Top.scala 1907:22]
  wire [15:0] n237_O_t1b_t0b; // @[Top.scala 1907:22]
  wire [15:0] n237_O_t1b_t1b; // @[Top.scala 1907:22]
  wire  n274_clock; // @[Top.scala 1910:22]
  wire  n274_reset; // @[Top.scala 1910:22]
  wire  n274_valid_up; // @[Top.scala 1910:22]
  wire  n274_valid_down; // @[Top.scala 1910:22]
  wire [15:0] n274_I_t0b; // @[Top.scala 1910:22]
  wire [15:0] n274_I_t1b_t0b; // @[Top.scala 1910:22]
  wire [15:0] n274_I_t1b_t1b; // @[Top.scala 1910:22]
  wire [15:0] n274_O_t0b; // @[Top.scala 1910:22]
  wire [15:0] n274_O_t1b_t0b; // @[Top.scala 1910:22]
  wire [15:0] n274_O_t1b_t1b; // @[Top.scala 1910:22]
  wire  n311_clock; // @[Top.scala 1913:22]
  wire  n311_reset; // @[Top.scala 1913:22]
  wire  n311_valid_up; // @[Top.scala 1913:22]
  wire  n311_valid_down; // @[Top.scala 1913:22]
  wire [15:0] n311_I_t0b; // @[Top.scala 1913:22]
  wire [15:0] n311_I_t1b_t0b; // @[Top.scala 1913:22]
  wire [15:0] n311_I_t1b_t1b; // @[Top.scala 1913:22]
  wire [15:0] n311_O_t0b; // @[Top.scala 1913:22]
  wire [15:0] n311_O_t1b_t0b; // @[Top.scala 1913:22]
  wire [15:0] n311_O_t1b_t1b; // @[Top.scala 1913:22]
  wire  n348_clock; // @[Top.scala 1916:22]
  wire  n348_reset; // @[Top.scala 1916:22]
  wire  n348_valid_up; // @[Top.scala 1916:22]
  wire  n348_valid_down; // @[Top.scala 1916:22]
  wire [15:0] n348_I_t0b; // @[Top.scala 1916:22]
  wire [15:0] n348_I_t1b_t0b; // @[Top.scala 1916:22]
  wire [15:0] n348_I_t1b_t1b; // @[Top.scala 1916:22]
  wire [15:0] n348_O_t0b; // @[Top.scala 1916:22]
  wire [15:0] n348_O_t1b_t0b; // @[Top.scala 1916:22]
  wire [15:0] n348_O_t1b_t1b; // @[Top.scala 1916:22]
  wire  n385_clock; // @[Top.scala 1919:22]
  wire  n385_reset; // @[Top.scala 1919:22]
  wire  n385_valid_up; // @[Top.scala 1919:22]
  wire  n385_valid_down; // @[Top.scala 1919:22]
  wire [15:0] n385_I_t0b; // @[Top.scala 1919:22]
  wire [15:0] n385_I_t1b_t0b; // @[Top.scala 1919:22]
  wire [15:0] n385_I_t1b_t1b; // @[Top.scala 1919:22]
  wire [15:0] n385_O_t0b; // @[Top.scala 1919:22]
  wire [15:0] n385_O_t1b_t0b; // @[Top.scala 1919:22]
  wire [15:0] n385_O_t1b_t1b; // @[Top.scala 1919:22]
  wire  n422_clock; // @[Top.scala 1922:22]
  wire  n422_reset; // @[Top.scala 1922:22]
  wire  n422_valid_up; // @[Top.scala 1922:22]
  wire  n422_valid_down; // @[Top.scala 1922:22]
  wire [15:0] n422_I_t0b; // @[Top.scala 1922:22]
  wire [15:0] n422_I_t1b_t0b; // @[Top.scala 1922:22]
  wire [15:0] n422_I_t1b_t1b; // @[Top.scala 1922:22]
  wire [15:0] n422_O_t0b; // @[Top.scala 1922:22]
  wire [15:0] n422_O_t1b_t0b; // @[Top.scala 1922:22]
  wire [15:0] n422_O_t1b_t1b; // @[Top.scala 1922:22]
  wire  n459_clock; // @[Top.scala 1925:22]
  wire  n459_reset; // @[Top.scala 1925:22]
  wire  n459_valid_up; // @[Top.scala 1925:22]
  wire  n459_valid_down; // @[Top.scala 1925:22]
  wire [15:0] n459_I_t0b; // @[Top.scala 1925:22]
  wire [15:0] n459_I_t1b_t0b; // @[Top.scala 1925:22]
  wire [15:0] n459_I_t1b_t1b; // @[Top.scala 1925:22]
  wire [15:0] n459_O_t0b; // @[Top.scala 1925:22]
  wire [15:0] n459_O_t1b_t0b; // @[Top.scala 1925:22]
  wire [15:0] n459_O_t1b_t1b; // @[Top.scala 1925:22]
  wire  n496_clock; // @[Top.scala 1928:22]
  wire  n496_reset; // @[Top.scala 1928:22]
  wire  n496_valid_up; // @[Top.scala 1928:22]
  wire  n496_valid_down; // @[Top.scala 1928:22]
  wire [15:0] n496_I_t0b; // @[Top.scala 1928:22]
  wire [15:0] n496_I_t1b_t0b; // @[Top.scala 1928:22]
  wire [15:0] n496_I_t1b_t1b; // @[Top.scala 1928:22]
  wire [15:0] n496_O_t0b; // @[Top.scala 1928:22]
  wire [15:0] n496_O_t1b_t0b; // @[Top.scala 1928:22]
  wire [15:0] n496_O_t1b_t1b; // @[Top.scala 1928:22]
  wire  n533_clock; // @[Top.scala 1931:22]
  wire  n533_reset; // @[Top.scala 1931:22]
  wire  n533_valid_up; // @[Top.scala 1931:22]
  wire  n533_valid_down; // @[Top.scala 1931:22]
  wire [15:0] n533_I_t0b; // @[Top.scala 1931:22]
  wire [15:0] n533_I_t1b_t0b; // @[Top.scala 1931:22]
  wire [15:0] n533_I_t1b_t1b; // @[Top.scala 1931:22]
  wire [15:0] n533_O_t0b; // @[Top.scala 1931:22]
  wire [15:0] n533_O_t1b_t0b; // @[Top.scala 1931:22]
  wire [15:0] n533_O_t1b_t1b; // @[Top.scala 1931:22]
  wire  n570_clock; // @[Top.scala 1934:22]
  wire  n570_reset; // @[Top.scala 1934:22]
  wire  n570_valid_up; // @[Top.scala 1934:22]
  wire  n570_valid_down; // @[Top.scala 1934:22]
  wire [15:0] n570_I_t0b; // @[Top.scala 1934:22]
  wire [15:0] n570_I_t1b_t0b; // @[Top.scala 1934:22]
  wire [15:0] n570_I_t1b_t1b; // @[Top.scala 1934:22]
  wire [15:0] n570_O_t0b; // @[Top.scala 1934:22]
  wire [15:0] n570_O_t1b_t0b; // @[Top.scala 1934:22]
  wire [15:0] n570_O_t1b_t1b; // @[Top.scala 1934:22]
  wire  n607_clock; // @[Top.scala 1937:22]
  wire  n607_reset; // @[Top.scala 1937:22]
  wire  n607_valid_up; // @[Top.scala 1937:22]
  wire  n607_valid_down; // @[Top.scala 1937:22]
  wire [15:0] n607_I_t0b; // @[Top.scala 1937:22]
  wire [15:0] n607_I_t1b_t0b; // @[Top.scala 1937:22]
  wire [15:0] n607_I_t1b_t1b; // @[Top.scala 1937:22]
  wire [15:0] n607_O_t1b_t0b; // @[Top.scala 1937:22]
  wire [15:0] n607_O_t1b_t1b; // @[Top.scala 1937:22]
  wire  n611_valid_up; // @[Top.scala 1940:22]
  wire  n611_valid_down; // @[Top.scala 1940:22]
  wire [15:0] n611_I_t1b_t0b; // @[Top.scala 1940:22]
  wire [15:0] n611_I_t1b_t1b; // @[Top.scala 1940:22]
  wire [15:0] n611_O; // @[Top.scala 1940:22]
  wire  n612_clock; // @[Top.scala 1943:22]
  wire  n612_reset; // @[Top.scala 1943:22]
  wire  n612_valid_up; // @[Top.scala 1943:22]
  wire  n612_valid_down; // @[Top.scala 1943:22]
  wire [15:0] n612_I; // @[Top.scala 1943:22]
  wire [15:0] n612_O; // @[Top.scala 1943:22]
  wire  n613_clock; // @[Top.scala 1946:22]
  wire  n613_reset; // @[Top.scala 1946:22]
  wire  n613_valid_up; // @[Top.scala 1946:22]
  wire  n613_valid_down; // @[Top.scala 1946:22]
  wire [15:0] n613_I; // @[Top.scala 1946:22]
  wire [15:0] n613_O; // @[Top.scala 1946:22]
  wire  n614_clock; // @[Top.scala 1949:22]
  wire  n614_reset; // @[Top.scala 1949:22]
  wire  n614_valid_up; // @[Top.scala 1949:22]
  wire  n614_valid_down; // @[Top.scala 1949:22]
  wire [15:0] n614_I; // @[Top.scala 1949:22]
  wire [15:0] n614_O; // @[Top.scala 1949:22]
  FIFO n1 ( // @[Top.scala 1886:20]
    .clock(n1_clock),
    .reset(n1_reset),
    .valid_up(n1_valid_up),
    .valid_down(n1_valid_down),
    .I(n1_I),
    .O(n1_O)
  );
  MapT n15 ( // @[Top.scala 1889:21]
    .clock(n15_clock),
    .reset(n15_reset),
    .valid_up(n15_valid_up),
    .valid_down(n15_valid_down),
    .I(n15_I),
    .O_t0b(n15_O_t0b),
    .O_t1b_t0b(n15_O_t1b_t0b),
    .O_t1b_t1b(n15_O_t1b_t1b)
  );
  MapT_1 n52 ( // @[Top.scala 1892:21]
    .clock(n52_clock),
    .reset(n52_reset),
    .valid_up(n52_valid_up),
    .valid_down(n52_valid_down),
    .I_t0b(n52_I_t0b),
    .I_t1b_t0b(n52_I_t1b_t0b),
    .I_t1b_t1b(n52_I_t1b_t1b),
    .O_t0b(n52_O_t0b),
    .O_t1b_t0b(n52_O_t1b_t0b),
    .O_t1b_t1b(n52_O_t1b_t1b)
  );
  MapT_2 n89 ( // @[Top.scala 1895:21]
    .clock(n89_clock),
    .reset(n89_reset),
    .valid_up(n89_valid_up),
    .valid_down(n89_valid_down),
    .I_t0b(n89_I_t0b),
    .I_t1b_t0b(n89_I_t1b_t0b),
    .I_t1b_t1b(n89_I_t1b_t1b),
    .O_t0b(n89_O_t0b),
    .O_t1b_t0b(n89_O_t1b_t0b),
    .O_t1b_t1b(n89_O_t1b_t1b)
  );
  MapT_3 n126 ( // @[Top.scala 1898:22]
    .clock(n126_clock),
    .reset(n126_reset),
    .valid_up(n126_valid_up),
    .valid_down(n126_valid_down),
    .I_t0b(n126_I_t0b),
    .I_t1b_t0b(n126_I_t1b_t0b),
    .I_t1b_t1b(n126_I_t1b_t1b),
    .O_t0b(n126_O_t0b),
    .O_t1b_t0b(n126_O_t1b_t0b),
    .O_t1b_t1b(n126_O_t1b_t1b)
  );
  MapT_4 n163 ( // @[Top.scala 1901:22]
    .clock(n163_clock),
    .reset(n163_reset),
    .valid_up(n163_valid_up),
    .valid_down(n163_valid_down),
    .I_t0b(n163_I_t0b),
    .I_t1b_t0b(n163_I_t1b_t0b),
    .I_t1b_t1b(n163_I_t1b_t1b),
    .O_t0b(n163_O_t0b),
    .O_t1b_t0b(n163_O_t1b_t0b),
    .O_t1b_t1b(n163_O_t1b_t1b)
  );
  MapT_5 n200 ( // @[Top.scala 1904:22]
    .clock(n200_clock),
    .reset(n200_reset),
    .valid_up(n200_valid_up),
    .valid_down(n200_valid_down),
    .I_t0b(n200_I_t0b),
    .I_t1b_t0b(n200_I_t1b_t0b),
    .I_t1b_t1b(n200_I_t1b_t1b),
    .O_t0b(n200_O_t0b),
    .O_t1b_t0b(n200_O_t1b_t0b),
    .O_t1b_t1b(n200_O_t1b_t1b)
  );
  MapT_6 n237 ( // @[Top.scala 1907:22]
    .clock(n237_clock),
    .reset(n237_reset),
    .valid_up(n237_valid_up),
    .valid_down(n237_valid_down),
    .I_t0b(n237_I_t0b),
    .I_t1b_t0b(n237_I_t1b_t0b),
    .I_t1b_t1b(n237_I_t1b_t1b),
    .O_t0b(n237_O_t0b),
    .O_t1b_t0b(n237_O_t1b_t0b),
    .O_t1b_t1b(n237_O_t1b_t1b)
  );
  MapT_7 n274 ( // @[Top.scala 1910:22]
    .clock(n274_clock),
    .reset(n274_reset),
    .valid_up(n274_valid_up),
    .valid_down(n274_valid_down),
    .I_t0b(n274_I_t0b),
    .I_t1b_t0b(n274_I_t1b_t0b),
    .I_t1b_t1b(n274_I_t1b_t1b),
    .O_t0b(n274_O_t0b),
    .O_t1b_t0b(n274_O_t1b_t0b),
    .O_t1b_t1b(n274_O_t1b_t1b)
  );
  MapT_8 n311 ( // @[Top.scala 1913:22]
    .clock(n311_clock),
    .reset(n311_reset),
    .valid_up(n311_valid_up),
    .valid_down(n311_valid_down),
    .I_t0b(n311_I_t0b),
    .I_t1b_t0b(n311_I_t1b_t0b),
    .I_t1b_t1b(n311_I_t1b_t1b),
    .O_t0b(n311_O_t0b),
    .O_t1b_t0b(n311_O_t1b_t0b),
    .O_t1b_t1b(n311_O_t1b_t1b)
  );
  MapT_9 n348 ( // @[Top.scala 1916:22]
    .clock(n348_clock),
    .reset(n348_reset),
    .valid_up(n348_valid_up),
    .valid_down(n348_valid_down),
    .I_t0b(n348_I_t0b),
    .I_t1b_t0b(n348_I_t1b_t0b),
    .I_t1b_t1b(n348_I_t1b_t1b),
    .O_t0b(n348_O_t0b),
    .O_t1b_t0b(n348_O_t1b_t0b),
    .O_t1b_t1b(n348_O_t1b_t1b)
  );
  MapT_10 n385 ( // @[Top.scala 1919:22]
    .clock(n385_clock),
    .reset(n385_reset),
    .valid_up(n385_valid_up),
    .valid_down(n385_valid_down),
    .I_t0b(n385_I_t0b),
    .I_t1b_t0b(n385_I_t1b_t0b),
    .I_t1b_t1b(n385_I_t1b_t1b),
    .O_t0b(n385_O_t0b),
    .O_t1b_t0b(n385_O_t1b_t0b),
    .O_t1b_t1b(n385_O_t1b_t1b)
  );
  MapT_11 n422 ( // @[Top.scala 1922:22]
    .clock(n422_clock),
    .reset(n422_reset),
    .valid_up(n422_valid_up),
    .valid_down(n422_valid_down),
    .I_t0b(n422_I_t0b),
    .I_t1b_t0b(n422_I_t1b_t0b),
    .I_t1b_t1b(n422_I_t1b_t1b),
    .O_t0b(n422_O_t0b),
    .O_t1b_t0b(n422_O_t1b_t0b),
    .O_t1b_t1b(n422_O_t1b_t1b)
  );
  MapT_12 n459 ( // @[Top.scala 1925:22]
    .clock(n459_clock),
    .reset(n459_reset),
    .valid_up(n459_valid_up),
    .valid_down(n459_valid_down),
    .I_t0b(n459_I_t0b),
    .I_t1b_t0b(n459_I_t1b_t0b),
    .I_t1b_t1b(n459_I_t1b_t1b),
    .O_t0b(n459_O_t0b),
    .O_t1b_t0b(n459_O_t1b_t0b),
    .O_t1b_t1b(n459_O_t1b_t1b)
  );
  MapT_13 n496 ( // @[Top.scala 1928:22]
    .clock(n496_clock),
    .reset(n496_reset),
    .valid_up(n496_valid_up),
    .valid_down(n496_valid_down),
    .I_t0b(n496_I_t0b),
    .I_t1b_t0b(n496_I_t1b_t0b),
    .I_t1b_t1b(n496_I_t1b_t1b),
    .O_t0b(n496_O_t0b),
    .O_t1b_t0b(n496_O_t1b_t0b),
    .O_t1b_t1b(n496_O_t1b_t1b)
  );
  MapT_14 n533 ( // @[Top.scala 1931:22]
    .clock(n533_clock),
    .reset(n533_reset),
    .valid_up(n533_valid_up),
    .valid_down(n533_valid_down),
    .I_t0b(n533_I_t0b),
    .I_t1b_t0b(n533_I_t1b_t0b),
    .I_t1b_t1b(n533_I_t1b_t1b),
    .O_t0b(n533_O_t0b),
    .O_t1b_t0b(n533_O_t1b_t0b),
    .O_t1b_t1b(n533_O_t1b_t1b)
  );
  MapT_15 n570 ( // @[Top.scala 1934:22]
    .clock(n570_clock),
    .reset(n570_reset),
    .valid_up(n570_valid_up),
    .valid_down(n570_valid_down),
    .I_t0b(n570_I_t0b),
    .I_t1b_t0b(n570_I_t1b_t0b),
    .I_t1b_t1b(n570_I_t1b_t1b),
    .O_t0b(n570_O_t0b),
    .O_t1b_t0b(n570_O_t1b_t0b),
    .O_t1b_t1b(n570_O_t1b_t1b)
  );
  MapT_16 n607 ( // @[Top.scala 1937:22]
    .clock(n607_clock),
    .reset(n607_reset),
    .valid_up(n607_valid_up),
    .valid_down(n607_valid_down),
    .I_t0b(n607_I_t0b),
    .I_t1b_t0b(n607_I_t1b_t0b),
    .I_t1b_t1b(n607_I_t1b_t1b),
    .O_t1b_t0b(n607_O_t1b_t0b),
    .O_t1b_t1b(n607_O_t1b_t1b)
  );
  MapT_17 n611 ( // @[Top.scala 1940:22]
    .valid_up(n611_valid_up),
    .valid_down(n611_valid_down),
    .I_t1b_t0b(n611_I_t1b_t0b),
    .I_t1b_t1b(n611_I_t1b_t1b),
    .O(n611_O)
  );
  FIFO n612 ( // @[Top.scala 1943:22]
    .clock(n612_clock),
    .reset(n612_reset),
    .valid_up(n612_valid_up),
    .valid_down(n612_valid_down),
    .I(n612_I),
    .O(n612_O)
  );
  FIFO n613 ( // @[Top.scala 1946:22]
    .clock(n613_clock),
    .reset(n613_reset),
    .valid_up(n613_valid_up),
    .valid_down(n613_valid_down),
    .I(n613_I),
    .O(n613_O)
  );
  FIFO n614 ( // @[Top.scala 1949:22]
    .clock(n614_clock),
    .reset(n614_reset),
    .valid_up(n614_valid_up),
    .valid_down(n614_valid_down),
    .I(n614_I),
    .O(n614_O)
  );
  assign valid_down = n614_valid_down; // @[Top.scala 1953:16]
  assign O = n614_O; // @[Top.scala 1952:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 1888:17]
  assign n1_I = I; // @[Top.scala 1887:10]
  assign n15_clock = clock;
  assign n15_reset = reset;
  assign n15_valid_up = n1_valid_down; // @[Top.scala 1891:18]
  assign n15_I = n1_O; // @[Top.scala 1890:11]
  assign n52_clock = clock;
  assign n52_reset = reset;
  assign n52_valid_up = n15_valid_down; // @[Top.scala 1894:18]
  assign n52_I_t0b = n15_O_t0b; // @[Top.scala 1893:11]
  assign n52_I_t1b_t0b = n15_O_t1b_t0b; // @[Top.scala 1893:11]
  assign n52_I_t1b_t1b = n15_O_t1b_t1b; // @[Top.scala 1893:11]
  assign n89_clock = clock;
  assign n89_reset = reset;
  assign n89_valid_up = n52_valid_down; // @[Top.scala 1897:18]
  assign n89_I_t0b = n52_O_t0b; // @[Top.scala 1896:11]
  assign n89_I_t1b_t0b = n52_O_t1b_t0b; // @[Top.scala 1896:11]
  assign n89_I_t1b_t1b = n52_O_t1b_t1b; // @[Top.scala 1896:11]
  assign n126_clock = clock;
  assign n126_reset = reset;
  assign n126_valid_up = n89_valid_down; // @[Top.scala 1900:19]
  assign n126_I_t0b = n89_O_t0b; // @[Top.scala 1899:12]
  assign n126_I_t1b_t0b = n89_O_t1b_t0b; // @[Top.scala 1899:12]
  assign n126_I_t1b_t1b = n89_O_t1b_t1b; // @[Top.scala 1899:12]
  assign n163_clock = clock;
  assign n163_reset = reset;
  assign n163_valid_up = n126_valid_down; // @[Top.scala 1903:19]
  assign n163_I_t0b = n126_O_t0b; // @[Top.scala 1902:12]
  assign n163_I_t1b_t0b = n126_O_t1b_t0b; // @[Top.scala 1902:12]
  assign n163_I_t1b_t1b = n126_O_t1b_t1b; // @[Top.scala 1902:12]
  assign n200_clock = clock;
  assign n200_reset = reset;
  assign n200_valid_up = n163_valid_down; // @[Top.scala 1906:19]
  assign n200_I_t0b = n163_O_t0b; // @[Top.scala 1905:12]
  assign n200_I_t1b_t0b = n163_O_t1b_t0b; // @[Top.scala 1905:12]
  assign n200_I_t1b_t1b = n163_O_t1b_t1b; // @[Top.scala 1905:12]
  assign n237_clock = clock;
  assign n237_reset = reset;
  assign n237_valid_up = n200_valid_down; // @[Top.scala 1909:19]
  assign n237_I_t0b = n200_O_t0b; // @[Top.scala 1908:12]
  assign n237_I_t1b_t0b = n200_O_t1b_t0b; // @[Top.scala 1908:12]
  assign n237_I_t1b_t1b = n200_O_t1b_t1b; // @[Top.scala 1908:12]
  assign n274_clock = clock;
  assign n274_reset = reset;
  assign n274_valid_up = n237_valid_down; // @[Top.scala 1912:19]
  assign n274_I_t0b = n237_O_t0b; // @[Top.scala 1911:12]
  assign n274_I_t1b_t0b = n237_O_t1b_t0b; // @[Top.scala 1911:12]
  assign n274_I_t1b_t1b = n237_O_t1b_t1b; // @[Top.scala 1911:12]
  assign n311_clock = clock;
  assign n311_reset = reset;
  assign n311_valid_up = n274_valid_down; // @[Top.scala 1915:19]
  assign n311_I_t0b = n274_O_t0b; // @[Top.scala 1914:12]
  assign n311_I_t1b_t0b = n274_O_t1b_t0b; // @[Top.scala 1914:12]
  assign n311_I_t1b_t1b = n274_O_t1b_t1b; // @[Top.scala 1914:12]
  assign n348_clock = clock;
  assign n348_reset = reset;
  assign n348_valid_up = n311_valid_down; // @[Top.scala 1918:19]
  assign n348_I_t0b = n311_O_t0b; // @[Top.scala 1917:12]
  assign n348_I_t1b_t0b = n311_O_t1b_t0b; // @[Top.scala 1917:12]
  assign n348_I_t1b_t1b = n311_O_t1b_t1b; // @[Top.scala 1917:12]
  assign n385_clock = clock;
  assign n385_reset = reset;
  assign n385_valid_up = n348_valid_down; // @[Top.scala 1921:19]
  assign n385_I_t0b = n348_O_t0b; // @[Top.scala 1920:12]
  assign n385_I_t1b_t0b = n348_O_t1b_t0b; // @[Top.scala 1920:12]
  assign n385_I_t1b_t1b = n348_O_t1b_t1b; // @[Top.scala 1920:12]
  assign n422_clock = clock;
  assign n422_reset = reset;
  assign n422_valid_up = n385_valid_down; // @[Top.scala 1924:19]
  assign n422_I_t0b = n385_O_t0b; // @[Top.scala 1923:12]
  assign n422_I_t1b_t0b = n385_O_t1b_t0b; // @[Top.scala 1923:12]
  assign n422_I_t1b_t1b = n385_O_t1b_t1b; // @[Top.scala 1923:12]
  assign n459_clock = clock;
  assign n459_reset = reset;
  assign n459_valid_up = n422_valid_down; // @[Top.scala 1927:19]
  assign n459_I_t0b = n422_O_t0b; // @[Top.scala 1926:12]
  assign n459_I_t1b_t0b = n422_O_t1b_t0b; // @[Top.scala 1926:12]
  assign n459_I_t1b_t1b = n422_O_t1b_t1b; // @[Top.scala 1926:12]
  assign n496_clock = clock;
  assign n496_reset = reset;
  assign n496_valid_up = n459_valid_down; // @[Top.scala 1930:19]
  assign n496_I_t0b = n459_O_t0b; // @[Top.scala 1929:12]
  assign n496_I_t1b_t0b = n459_O_t1b_t0b; // @[Top.scala 1929:12]
  assign n496_I_t1b_t1b = n459_O_t1b_t1b; // @[Top.scala 1929:12]
  assign n533_clock = clock;
  assign n533_reset = reset;
  assign n533_valid_up = n496_valid_down; // @[Top.scala 1933:19]
  assign n533_I_t0b = n496_O_t0b; // @[Top.scala 1932:12]
  assign n533_I_t1b_t0b = n496_O_t1b_t0b; // @[Top.scala 1932:12]
  assign n533_I_t1b_t1b = n496_O_t1b_t1b; // @[Top.scala 1932:12]
  assign n570_clock = clock;
  assign n570_reset = reset;
  assign n570_valid_up = n533_valid_down; // @[Top.scala 1936:19]
  assign n570_I_t0b = n533_O_t0b; // @[Top.scala 1935:12]
  assign n570_I_t1b_t0b = n533_O_t1b_t0b; // @[Top.scala 1935:12]
  assign n570_I_t1b_t1b = n533_O_t1b_t1b; // @[Top.scala 1935:12]
  assign n607_clock = clock;
  assign n607_reset = reset;
  assign n607_valid_up = n570_valid_down; // @[Top.scala 1939:19]
  assign n607_I_t0b = n570_O_t0b; // @[Top.scala 1938:12]
  assign n607_I_t1b_t0b = n570_O_t1b_t0b; // @[Top.scala 1938:12]
  assign n607_I_t1b_t1b = n570_O_t1b_t1b; // @[Top.scala 1938:12]
  assign n611_valid_up = n607_valid_down; // @[Top.scala 1942:19]
  assign n611_I_t1b_t0b = n607_O_t1b_t0b; // @[Top.scala 1941:12]
  assign n611_I_t1b_t1b = n607_O_t1b_t1b; // @[Top.scala 1941:12]
  assign n612_clock = clock;
  assign n612_reset = reset;
  assign n612_valid_up = n611_valid_down; // @[Top.scala 1945:19]
  assign n612_I = n611_O; // @[Top.scala 1944:12]
  assign n613_clock = clock;
  assign n613_reset = reset;
  assign n613_valid_up = n612_valid_down; // @[Top.scala 1948:19]
  assign n613_I = n612_O; // @[Top.scala 1947:12]
  assign n614_clock = clock;
  assign n614_reset = reset;
  assign n614_valid_up = n613_valid_down; // @[Top.scala 1951:19]
  assign n614_I = n613_O; // @[Top.scala 1950:12]
endmodule
