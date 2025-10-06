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
module FIFO_1(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_t0b,
  input  [15:0] I_t1b,
  output [15:0] O_t0b,
  output [15:0] O_t1b
);
  reg [15:0] _T_t0b; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_0;
  reg [15:0] _T_t1b; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_1;
  reg  _T_1; // @[FIFO.scala 15:27]
  reg [31:0] _RAND_2;
  assign valid_down = _T_1; // @[FIFO.scala 16:16]
  assign O_t0b = _T_t0b; // @[FIFO.scala 14:7]
  assign O_t1b = _T_t1b; // @[FIFO.scala 14:7]
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
  _T_t0b = _RAND_0[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_t1b = _RAND_1[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_1 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    _T_t0b <= I_t0b;
    _T_t1b <= I_t1b;
    if (reset) begin
      _T_1 <= 1'h0;
    end else begin
      _T_1 <= valid_up;
    end
  end
endmodule
module AtomTuple_1(
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
  wire  n5_valid_up; // @[Top.scala 19:20]
  wire  n5_valid_down; // @[Top.scala 19:20]
  wire [15:0] n5_I0; // @[Top.scala 19:20]
  wire [15:0] n5_I1; // @[Top.scala 19:20]
  wire [15:0] n5_O_t0b; // @[Top.scala 19:20]
  wire [15:0] n5_O_t1b; // @[Top.scala 19:20]
  wire  n6_clock; // @[Top.scala 23:20]
  wire  n6_reset; // @[Top.scala 23:20]
  wire  n6_valid_up; // @[Top.scala 23:20]
  wire  n6_valid_down; // @[Top.scala 23:20]
  wire [15:0] n6_I_t0b; // @[Top.scala 23:20]
  wire [15:0] n6_I_t1b; // @[Top.scala 23:20]
  wire [15:0] n6_O_t0b; // @[Top.scala 23:20]
  wire [15:0] n6_O_t1b; // @[Top.scala 23:20]
  wire  n7_valid_up; // @[Top.scala 26:20]
  wire  n7_valid_down; // @[Top.scala 26:20]
  wire [15:0] n7_I0; // @[Top.scala 26:20]
  wire [15:0] n7_I1_t0b; // @[Top.scala 26:20]
  wire [15:0] n7_I1_t1b; // @[Top.scala 26:20]
  wire [15:0] n7_O_t0b; // @[Top.scala 26:20]
  wire [15:0] n7_O_t1b_t0b; // @[Top.scala 26:20]
  wire [15:0] n7_O_t1b_t1b; // @[Top.scala 26:20]
  AtomTuple n5 ( // @[Top.scala 19:20]
    .valid_up(n5_valid_up),
    .valid_down(n5_valid_down),
    .I0(n5_I0),
    .I1(n5_I1),
    .O_t0b(n5_O_t0b),
    .O_t1b(n5_O_t1b)
  );
  FIFO_1 n6 ( // @[Top.scala 23:20]
    .clock(n6_clock),
    .reset(n6_reset),
    .valid_up(n6_valid_up),
    .valid_down(n6_valid_down),
    .I_t0b(n6_I_t0b),
    .I_t1b(n6_I_t1b),
    .O_t0b(n6_O_t0b),
    .O_t1b(n6_O_t1b)
  );
  AtomTuple_1 n7 ( // @[Top.scala 26:20]
    .valid_up(n7_valid_up),
    .valid_down(n7_valid_down),
    .I0(n7_I0),
    .I1_t0b(n7_I1_t0b),
    .I1_t1b(n7_I1_t1b),
    .O_t0b(n7_O_t0b),
    .O_t1b_t0b(n7_O_t1b_t0b),
    .O_t1b_t1b(n7_O_t1b_t1b)
  );
  assign valid_down = n7_valid_down; // @[Top.scala 31:16]
  assign O_t0b = n7_O_t0b; // @[Top.scala 30:7]
  assign O_t1b_t0b = n7_O_t1b_t0b; // @[Top.scala 30:7]
  assign O_t1b_t1b = n7_O_t1b_t1b; // @[Top.scala 30:7]
  assign n5_valid_up = 1'h1; // @[Top.scala 22:17]
  assign n5_I0 = 16'h0; // @[Top.scala 20:11]
  assign n5_I1 = 16'hff; // @[Top.scala 21:11]
  assign n6_clock = clock;
  assign n6_reset = reset;
  assign n6_valid_up = n5_valid_down; // @[Top.scala 25:17]
  assign n6_I_t0b = n5_O_t0b; // @[Top.scala 24:10]
  assign n6_I_t1b = n5_O_t1b; // @[Top.scala 24:10]
  assign n7_valid_up = valid_up & n6_valid_down; // @[Top.scala 29:17]
  assign n7_I0 = I; // @[Top.scala 27:11]
  assign n7_I1_t0b = n6_O_t0b; // @[Top.scala 28:11]
  assign n7_I1_t1b = n6_O_t1b; // @[Top.scala 28:11]
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
module FIFO_2(
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
module AtomTuple_4(
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
module AtomTuple_10(
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
module FIFO_4(
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
module AtomTuple_11(
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
module If(
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
  wire  n10_valid_up; // @[Top.scala 37:21]
  wire  n10_valid_down; // @[Top.scala 37:21]
  wire [15:0] n10_I_t0b; // @[Top.scala 37:21]
  wire [15:0] n10_O; // @[Top.scala 37:21]
  wire  n38_clock; // @[Top.scala 40:21]
  wire  n38_reset; // @[Top.scala 40:21]
  wire  n38_valid_up; // @[Top.scala 40:21]
  wire  n38_valid_down; // @[Top.scala 40:21]
  wire [15:0] n38_I; // @[Top.scala 40:21]
  wire [15:0] n38_O; // @[Top.scala 40:21]
  wire  n26_clock; // @[Top.scala 43:21]
  wire  n26_reset; // @[Top.scala 43:21]
  wire  n26_valid_up; // @[Top.scala 43:21]
  wire  n26_valid_down; // @[Top.scala 43:21]
  wire [15:0] n26_I; // @[Top.scala 43:21]
  wire [15:0] n26_O; // @[Top.scala 43:21]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n12_valid_up; // @[Top.scala 47:21]
  wire  n12_valid_down; // @[Top.scala 47:21]
  wire [15:0] n12_I_t1b_t0b; // @[Top.scala 47:21]
  wire [15:0] n12_I_t1b_t1b; // @[Top.scala 47:21]
  wire [15:0] n12_O_t0b; // @[Top.scala 47:21]
  wire [15:0] n12_O_t1b; // @[Top.scala 47:21]
  wire  n13_valid_up; // @[Top.scala 50:21]
  wire  n13_valid_down; // @[Top.scala 50:21]
  wire [15:0] n13_I_t0b; // @[Top.scala 50:21]
  wire [15:0] n13_O; // @[Top.scala 50:21]
  wire  n14_valid_up; // @[Top.scala 53:21]
  wire  n14_valid_down; // @[Top.scala 53:21]
  wire [15:0] n14_I_t1b; // @[Top.scala 53:21]
  wire [15:0] n14_O; // @[Top.scala 53:21]
  wire  n15_valid_up; // @[Top.scala 56:21]
  wire  n15_valid_down; // @[Top.scala 56:21]
  wire [15:0] n15_I0; // @[Top.scala 56:21]
  wire [15:0] n15_I1; // @[Top.scala 56:21]
  wire [15:0] n15_O_t0b; // @[Top.scala 56:21]
  wire [15:0] n15_O_t1b; // @[Top.scala 56:21]
  wire  n16_valid_up; // @[Top.scala 60:21]
  wire  n16_valid_down; // @[Top.scala 60:21]
  wire [15:0] n16_I_t0b; // @[Top.scala 60:21]
  wire [15:0] n16_I_t1b; // @[Top.scala 60:21]
  wire [15:0] n16_O; // @[Top.scala 60:21]
  wire  n18_valid_up; // @[Top.scala 63:21]
  wire  n18_valid_down; // @[Top.scala 63:21]
  wire [15:0] n18_I0; // @[Top.scala 63:21]
  wire [15:0] n18_I1; // @[Top.scala 63:21]
  wire [15:0] n18_O_t0b; // @[Top.scala 63:21]
  wire [15:0] n18_O_t1b; // @[Top.scala 63:21]
  wire  n19_valid_up; // @[Top.scala 67:21]
  wire  n19_valid_down; // @[Top.scala 67:21]
  wire [15:0] n19_I_t0b; // @[Top.scala 67:21]
  wire [15:0] n19_I_t1b; // @[Top.scala 67:21]
  wire [15:0] n19_O; // @[Top.scala 67:21]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n22_valid_up; // @[Top.scala 71:21]
  wire  n22_valid_down; // @[Top.scala 71:21]
  wire [15:0] n22_I0; // @[Top.scala 71:21]
  wire [15:0] n22_O_t0b; // @[Top.scala 71:21]
  wire  n23_valid_up; // @[Top.scala 75:21]
  wire  n23_valid_down; // @[Top.scala 75:21]
  wire [15:0] n23_I_t0b; // @[Top.scala 75:21]
  wire [15:0] n23_O; // @[Top.scala 75:21]
  wire  n24_valid_up; // @[Top.scala 78:21]
  wire  n24_valid_down; // @[Top.scala 78:21]
  wire [15:0] n24_I0; // @[Top.scala 78:21]
  wire [15:0] n24_I1; // @[Top.scala 78:21]
  wire [15:0] n24_O_t0b; // @[Top.scala 78:21]
  wire [15:0] n24_O_t1b; // @[Top.scala 78:21]
  wire  n25_clock; // @[Top.scala 82:21]
  wire  n25_reset; // @[Top.scala 82:21]
  wire  n25_valid_up; // @[Top.scala 82:21]
  wire  n25_valid_down; // @[Top.scala 82:21]
  wire [15:0] n25_I_t0b; // @[Top.scala 82:21]
  wire [15:0] n25_I_t1b; // @[Top.scala 82:21]
  wire [15:0] n25_O; // @[Top.scala 82:21]
  wire  n27_valid_up; // @[Top.scala 85:21]
  wire  n27_valid_down; // @[Top.scala 85:21]
  wire [15:0] n27_I0; // @[Top.scala 85:21]
  wire [15:0] n27_I1; // @[Top.scala 85:21]
  wire [15:0] n27_O_t0b; // @[Top.scala 85:21]
  wire [15:0] n27_O_t1b; // @[Top.scala 85:21]
  wire  n28_valid_up; // @[Top.scala 89:21]
  wire  n28_valid_down; // @[Top.scala 89:21]
  wire [15:0] n28_I_t0b; // @[Top.scala 89:21]
  wire [15:0] n28_I_t1b; // @[Top.scala 89:21]
  wire [15:0] n28_O; // @[Top.scala 89:21]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n30_valid_up; // @[Top.scala 93:21]
  wire  n30_valid_down; // @[Top.scala 93:21]
  wire [15:0] n30_I0; // @[Top.scala 93:21]
  wire [15:0] n30_I1; // @[Top.scala 93:21]
  wire [15:0] n30_O_t0b; // @[Top.scala 93:21]
  wire [15:0] n30_O_t1b; // @[Top.scala 93:21]
  wire  n31_valid_up; // @[Top.scala 97:21]
  wire  n31_valid_down; // @[Top.scala 97:21]
  wire [15:0] n31_I_t0b; // @[Top.scala 97:21]
  wire [15:0] n31_I_t1b; // @[Top.scala 97:21]
  wire [15:0] n31_O; // @[Top.scala 97:21]
  wire  n32_valid_up; // @[Top.scala 100:21]
  wire  n32_valid_down; // @[Top.scala 100:21]
  wire [15:0] n32_I0; // @[Top.scala 100:21]
  wire [15:0] n32_I1; // @[Top.scala 100:21]
  wire [15:0] n32_O_t0b; // @[Top.scala 100:21]
  wire [15:0] n32_O_t1b; // @[Top.scala 100:21]
  wire  n33_valid_up; // @[Top.scala 104:21]
  wire  n33_valid_down; // @[Top.scala 104:21]
  wire [15:0] n33_I0; // @[Top.scala 104:21]
  wire [15:0] n33_I1; // @[Top.scala 104:21]
  wire [15:0] n33_O_t0b; // @[Top.scala 104:21]
  wire [15:0] n33_O_t1b; // @[Top.scala 104:21]
  wire  n34_valid_up; // @[Top.scala 108:21]
  wire  n34_valid_down; // @[Top.scala 108:21]
  wire [15:0] n34_I0_t0b; // @[Top.scala 108:21]
  wire [15:0] n34_I0_t1b; // @[Top.scala 108:21]
  wire [15:0] n34_I1_t0b; // @[Top.scala 108:21]
  wire [15:0] n34_I1_t1b; // @[Top.scala 108:21]
  wire [15:0] n34_O_t0b_t0b; // @[Top.scala 108:21]
  wire [15:0] n34_O_t0b_t1b; // @[Top.scala 108:21]
  wire [15:0] n34_O_t1b_t0b; // @[Top.scala 108:21]
  wire [15:0] n34_O_t1b_t1b; // @[Top.scala 108:21]
  wire  n35_clock; // @[Top.scala 112:21]
  wire  n35_reset; // @[Top.scala 112:21]
  wire  n35_valid_up; // @[Top.scala 112:21]
  wire  n35_valid_down; // @[Top.scala 112:21]
  wire [15:0] n35_I_t0b_t0b; // @[Top.scala 112:21]
  wire [15:0] n35_I_t0b_t1b; // @[Top.scala 112:21]
  wire [15:0] n35_I_t1b_t0b; // @[Top.scala 112:21]
  wire [15:0] n35_I_t1b_t1b; // @[Top.scala 112:21]
  wire [15:0] n35_O_t0b_t0b; // @[Top.scala 112:21]
  wire [15:0] n35_O_t0b_t1b; // @[Top.scala 112:21]
  wire [15:0] n35_O_t1b_t0b; // @[Top.scala 112:21]
  wire [15:0] n35_O_t1b_t1b; // @[Top.scala 112:21]
  wire  n36_valid_up; // @[Top.scala 115:21]
  wire  n36_valid_down; // @[Top.scala 115:21]
  wire  n36_I0; // @[Top.scala 115:21]
  wire [15:0] n36_I1_t0b_t0b; // @[Top.scala 115:21]
  wire [15:0] n36_I1_t0b_t1b; // @[Top.scala 115:21]
  wire [15:0] n36_I1_t1b_t0b; // @[Top.scala 115:21]
  wire [15:0] n36_I1_t1b_t1b; // @[Top.scala 115:21]
  wire  n36_O_t0b; // @[Top.scala 115:21]
  wire [15:0] n36_O_t1b_t0b_t0b; // @[Top.scala 115:21]
  wire [15:0] n36_O_t1b_t0b_t1b; // @[Top.scala 115:21]
  wire [15:0] n36_O_t1b_t1b_t0b; // @[Top.scala 115:21]
  wire [15:0] n36_O_t1b_t1b_t1b; // @[Top.scala 115:21]
  wire  n37_valid_up; // @[Top.scala 119:21]
  wire  n37_valid_down; // @[Top.scala 119:21]
  wire  n37_I_t0b; // @[Top.scala 119:21]
  wire [15:0] n37_I_t1b_t0b_t0b; // @[Top.scala 119:21]
  wire [15:0] n37_I_t1b_t0b_t1b; // @[Top.scala 119:21]
  wire [15:0] n37_I_t1b_t1b_t0b; // @[Top.scala 119:21]
  wire [15:0] n37_I_t1b_t1b_t1b; // @[Top.scala 119:21]
  wire [15:0] n37_O_t0b; // @[Top.scala 119:21]
  wire [15:0] n37_O_t1b; // @[Top.scala 119:21]
  wire  n39_valid_up; // @[Top.scala 122:21]
  wire  n39_valid_down; // @[Top.scala 122:21]
  wire [15:0] n39_I0; // @[Top.scala 122:21]
  wire [15:0] n39_I1_t0b; // @[Top.scala 122:21]
  wire [15:0] n39_I1_t1b; // @[Top.scala 122:21]
  wire [15:0] n39_O_t0b; // @[Top.scala 122:21]
  wire [15:0] n39_O_t1b_t0b; // @[Top.scala 122:21]
  wire [15:0] n39_O_t1b_t1b; // @[Top.scala 122:21]
  Fst n10 ( // @[Top.scala 37:21]
    .valid_up(n10_valid_up),
    .valid_down(n10_valid_down),
    .I_t0b(n10_I_t0b),
    .O(n10_O)
  );
  FIFO_2 n38 ( // @[Top.scala 40:21]
    .clock(n38_clock),
    .reset(n38_reset),
    .valid_up(n38_valid_up),
    .valid_down(n38_valid_down),
    .I(n38_I),
    .O(n38_O)
  );
  FIFO_2 n26 ( // @[Top.scala 43:21]
    .clock(n26_clock),
    .reset(n26_reset),
    .valid_up(n26_valid_up),
    .valid_down(n26_valid_down),
    .I(n26_I),
    .O(n26_O)
  );
  InitialDelayCounter InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n12 ( // @[Top.scala 47:21]
    .valid_up(n12_valid_up),
    .valid_down(n12_valid_down),
    .I_t1b_t0b(n12_I_t1b_t0b),
    .I_t1b_t1b(n12_I_t1b_t1b),
    .O_t0b(n12_O_t0b),
    .O_t1b(n12_O_t1b)
  );
  Fst_1 n13 ( // @[Top.scala 50:21]
    .valid_up(n13_valid_up),
    .valid_down(n13_valid_down),
    .I_t0b(n13_I_t0b),
    .O(n13_O)
  );
  Snd_1 n14 ( // @[Top.scala 53:21]
    .valid_up(n14_valid_up),
    .valid_down(n14_valid_down),
    .I_t1b(n14_I_t1b),
    .O(n14_O)
  );
  AtomTuple n15 ( // @[Top.scala 56:21]
    .valid_up(n15_valid_up),
    .valid_down(n15_valid_down),
    .I0(n15_I0),
    .I1(n15_I1),
    .O_t0b(n15_O_t0b),
    .O_t1b(n15_O_t1b)
  );
  Add n16 ( // @[Top.scala 60:21]
    .valid_up(n16_valid_up),
    .valid_down(n16_valid_down),
    .I_t0b(n16_I_t0b),
    .I_t1b(n16_I_t1b),
    .O(n16_O)
  );
  AtomTuple n18 ( // @[Top.scala 63:21]
    .valid_up(n18_valid_up),
    .valid_down(n18_valid_down),
    .I0(n18_I0),
    .I1(n18_I1),
    .O_t0b(n18_O_t0b),
    .O_t1b(n18_O_t1b)
  );
  Add n19 ( // @[Top.scala 67:21]
    .valid_up(n19_valid_up),
    .valid_down(n19_valid_down),
    .I_t0b(n19_I_t0b),
    .I_t1b(n19_I_t1b),
    .O(n19_O)
  );
  InitialDelayCounter InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n22 ( // @[Top.scala 71:21]
    .valid_up(n22_valid_up),
    .valid_down(n22_valid_down),
    .I0(n22_I0),
    .O_t0b(n22_O_t0b)
  );
  RShift n23 ( // @[Top.scala 75:21]
    .valid_up(n23_valid_up),
    .valid_down(n23_valid_down),
    .I_t0b(n23_I_t0b),
    .O(n23_O)
  );
  AtomTuple n24 ( // @[Top.scala 78:21]
    .valid_up(n24_valid_up),
    .valid_down(n24_valid_down),
    .I0(n24_I0),
    .I1(n24_I1),
    .O_t0b(n24_O_t0b),
    .O_t1b(n24_O_t1b)
  );
  Mul n25 ( // @[Top.scala 82:21]
    .clock(n25_clock),
    .reset(n25_reset),
    .valid_up(n25_valid_up),
    .valid_down(n25_valid_down),
    .I_t0b(n25_I_t0b),
    .I_t1b(n25_I_t1b),
    .O(n25_O)
  );
  AtomTuple n27 ( // @[Top.scala 85:21]
    .valid_up(n27_valid_up),
    .valid_down(n27_valid_down),
    .I0(n27_I0),
    .I1(n27_I1),
    .O_t0b(n27_O_t0b),
    .O_t1b(n27_O_t1b)
  );
  Lt n28 ( // @[Top.scala 89:21]
    .valid_up(n28_valid_up),
    .valid_down(n28_valid_down),
    .I_t0b(n28_I_t0b),
    .I_t1b(n28_I_t1b),
    .O(n28_O)
  );
  InitialDelayCounter InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n30 ( // @[Top.scala 93:21]
    .valid_up(n30_valid_up),
    .valid_down(n30_valid_down),
    .I0(n30_I0),
    .I1(n30_I1),
    .O_t0b(n30_O_t0b),
    .O_t1b(n30_O_t1b)
  );
  Sub n31 ( // @[Top.scala 97:21]
    .valid_up(n31_valid_up),
    .valid_down(n31_valid_down),
    .I_t0b(n31_I_t0b),
    .I_t1b(n31_I_t1b),
    .O(n31_O)
  );
  AtomTuple n32 ( // @[Top.scala 100:21]
    .valid_up(n32_valid_up),
    .valid_down(n32_valid_down),
    .I0(n32_I0),
    .I1(n32_I1),
    .O_t0b(n32_O_t0b),
    .O_t1b(n32_O_t1b)
  );
  AtomTuple n33 ( // @[Top.scala 104:21]
    .valid_up(n33_valid_up),
    .valid_down(n33_valid_down),
    .I0(n33_I0),
    .I1(n33_I1),
    .O_t0b(n33_O_t0b),
    .O_t1b(n33_O_t1b)
  );
  AtomTuple_10 n34 ( // @[Top.scala 108:21]
    .valid_up(n34_valid_up),
    .valid_down(n34_valid_down),
    .I0_t0b(n34_I0_t0b),
    .I0_t1b(n34_I0_t1b),
    .I1_t0b(n34_I1_t0b),
    .I1_t1b(n34_I1_t1b),
    .O_t0b_t0b(n34_O_t0b_t0b),
    .O_t0b_t1b(n34_O_t0b_t1b),
    .O_t1b_t0b(n34_O_t1b_t0b),
    .O_t1b_t1b(n34_O_t1b_t1b)
  );
  FIFO_4 n35 ( // @[Top.scala 112:21]
    .clock(n35_clock),
    .reset(n35_reset),
    .valid_up(n35_valid_up),
    .valid_down(n35_valid_down),
    .I_t0b_t0b(n35_I_t0b_t0b),
    .I_t0b_t1b(n35_I_t0b_t1b),
    .I_t1b_t0b(n35_I_t1b_t0b),
    .I_t1b_t1b(n35_I_t1b_t1b),
    .O_t0b_t0b(n35_O_t0b_t0b),
    .O_t0b_t1b(n35_O_t0b_t1b),
    .O_t1b_t0b(n35_O_t1b_t0b),
    .O_t1b_t1b(n35_O_t1b_t1b)
  );
  AtomTuple_11 n36 ( // @[Top.scala 115:21]
    .valid_up(n36_valid_up),
    .valid_down(n36_valid_down),
    .I0(n36_I0),
    .I1_t0b_t0b(n36_I1_t0b_t0b),
    .I1_t0b_t1b(n36_I1_t0b_t1b),
    .I1_t1b_t0b(n36_I1_t1b_t0b),
    .I1_t1b_t1b(n36_I1_t1b_t1b),
    .O_t0b(n36_O_t0b),
    .O_t1b_t0b_t0b(n36_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n36_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n36_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n36_O_t1b_t1b_t1b)
  );
  If n37 ( // @[Top.scala 119:21]
    .valid_up(n37_valid_up),
    .valid_down(n37_valid_down),
    .I_t0b(n37_I_t0b),
    .I_t1b_t0b_t0b(n37_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n37_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n37_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n37_I_t1b_t1b_t1b),
    .O_t0b(n37_O_t0b),
    .O_t1b(n37_O_t1b)
  );
  AtomTuple_1 n39 ( // @[Top.scala 122:21]
    .valid_up(n39_valid_up),
    .valid_down(n39_valid_down),
    .I0(n39_I0),
    .I1_t0b(n39_I1_t0b),
    .I1_t1b(n39_I1_t1b),
    .O_t0b(n39_O_t0b),
    .O_t1b_t0b(n39_O_t1b_t0b),
    .O_t1b_t1b(n39_O_t1b_t1b)
  );
  assign valid_down = n39_valid_down; // @[Top.scala 127:16]
  assign O_t0b = n39_O_t0b; // @[Top.scala 126:7]
  assign O_t1b_t0b = n39_O_t1b_t0b; // @[Top.scala 126:7]
  assign O_t1b_t1b = n39_O_t1b_t1b; // @[Top.scala 126:7]
  assign n10_valid_up = valid_up; // @[Top.scala 39:18]
  assign n10_I_t0b = I_t0b; // @[Top.scala 38:11]
  assign n38_clock = clock;
  assign n38_reset = reset;
  assign n38_valid_up = n10_valid_down; // @[Top.scala 42:18]
  assign n38_I = n10_O; // @[Top.scala 41:11]
  assign n26_clock = clock;
  assign n26_reset = reset;
  assign n26_valid_up = n10_valid_down; // @[Top.scala 45:18]
  assign n26_I = n10_O; // @[Top.scala 44:11]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n12_valid_up = valid_up; // @[Top.scala 49:18]
  assign n12_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 48:11]
  assign n12_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 48:11]
  assign n13_valid_up = n12_valid_down; // @[Top.scala 52:18]
  assign n13_I_t0b = n12_O_t0b; // @[Top.scala 51:11]
  assign n14_valid_up = n12_valid_down; // @[Top.scala 55:18]
  assign n14_I_t1b = n12_O_t1b; // @[Top.scala 54:11]
  assign n15_valid_up = n13_valid_down & n14_valid_down; // @[Top.scala 59:18]
  assign n15_I0 = n13_O; // @[Top.scala 57:12]
  assign n15_I1 = n14_O; // @[Top.scala 58:12]
  assign n16_valid_up = n15_valid_down; // @[Top.scala 62:18]
  assign n16_I_t0b = n15_O_t0b; // @[Top.scala 61:11]
  assign n16_I_t1b = n15_O_t1b; // @[Top.scala 61:11]
  assign n18_valid_up = InitialDelayCounter_valid_down & n16_valid_down; // @[Top.scala 66:18]
  assign n18_I0 = 16'h1; // @[Top.scala 64:12]
  assign n18_I1 = n16_O; // @[Top.scala 65:12]
  assign n19_valid_up = n18_valid_down; // @[Top.scala 69:18]
  assign n19_I_t0b = n18_O_t0b; // @[Top.scala 68:11]
  assign n19_I_t1b = n18_O_t1b; // @[Top.scala 68:11]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n22_valid_up = n19_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 74:18]
  assign n22_I0 = n19_O; // @[Top.scala 72:12]
  assign n23_valid_up = n22_valid_down; // @[Top.scala 77:18]
  assign n23_I_t0b = n22_O_t0b; // @[Top.scala 76:11]
  assign n24_valid_up = n23_valid_down; // @[Top.scala 81:18]
  assign n24_I0 = n23_O; // @[Top.scala 79:12]
  assign n24_I1 = n23_O; // @[Top.scala 80:12]
  assign n25_clock = clock;
  assign n25_reset = reset;
  assign n25_valid_up = n24_valid_down; // @[Top.scala 84:18]
  assign n25_I_t0b = n24_O_t0b; // @[Top.scala 83:11]
  assign n25_I_t1b = n24_O_t1b; // @[Top.scala 83:11]
  assign n27_valid_up = n26_valid_down & n25_valid_down; // @[Top.scala 88:18]
  assign n27_I0 = n26_O; // @[Top.scala 86:12]
  assign n27_I1 = n25_O; // @[Top.scala 87:12]
  assign n28_valid_up = n27_valid_down; // @[Top.scala 91:18]
  assign n28_I_t0b = n27_O_t0b; // @[Top.scala 90:11]
  assign n28_I_t1b = n27_O_t1b; // @[Top.scala 90:11]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n30_valid_up = n23_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 96:18]
  assign n30_I0 = n23_O; // @[Top.scala 94:12]
  assign n30_I1 = 16'h1; // @[Top.scala 95:12]
  assign n31_valid_up = n30_valid_down; // @[Top.scala 99:18]
  assign n31_I_t0b = n30_O_t0b; // @[Top.scala 98:11]
  assign n31_I_t1b = n30_O_t1b; // @[Top.scala 98:11]
  assign n32_valid_up = n13_valid_down & n31_valid_down; // @[Top.scala 103:18]
  assign n32_I0 = n13_O; // @[Top.scala 101:12]
  assign n32_I1 = n31_O; // @[Top.scala 102:12]
  assign n33_valid_up = n23_valid_down & n14_valid_down; // @[Top.scala 107:18]
  assign n33_I0 = n23_O; // @[Top.scala 105:12]
  assign n33_I1 = n14_O; // @[Top.scala 106:12]
  assign n34_valid_up = n32_valid_down & n33_valid_down; // @[Top.scala 111:18]
  assign n34_I0_t0b = n32_O_t0b; // @[Top.scala 109:12]
  assign n34_I0_t1b = n32_O_t1b; // @[Top.scala 109:12]
  assign n34_I1_t0b = n33_O_t0b; // @[Top.scala 110:12]
  assign n34_I1_t1b = n33_O_t1b; // @[Top.scala 110:12]
  assign n35_clock = clock;
  assign n35_reset = reset;
  assign n35_valid_up = n34_valid_down; // @[Top.scala 114:18]
  assign n35_I_t0b_t0b = n34_O_t0b_t0b; // @[Top.scala 113:11]
  assign n35_I_t0b_t1b = n34_O_t0b_t1b; // @[Top.scala 113:11]
  assign n35_I_t1b_t0b = n34_O_t1b_t0b; // @[Top.scala 113:11]
  assign n35_I_t1b_t1b = n34_O_t1b_t1b; // @[Top.scala 113:11]
  assign n36_valid_up = n28_valid_down & n35_valid_down; // @[Top.scala 118:18]
  assign n36_I0 = n28_O[0]; // @[Top.scala 116:12]
  assign n36_I1_t0b_t0b = n35_O_t0b_t0b; // @[Top.scala 117:12]
  assign n36_I1_t0b_t1b = n35_O_t0b_t1b; // @[Top.scala 117:12]
  assign n36_I1_t1b_t0b = n35_O_t1b_t0b; // @[Top.scala 117:12]
  assign n36_I1_t1b_t1b = n35_O_t1b_t1b; // @[Top.scala 117:12]
  assign n37_valid_up = n36_valid_down; // @[Top.scala 121:18]
  assign n37_I_t0b = n36_O_t0b; // @[Top.scala 120:11]
  assign n37_I_t1b_t0b_t0b = n36_O_t1b_t0b_t0b; // @[Top.scala 120:11]
  assign n37_I_t1b_t0b_t1b = n36_O_t1b_t0b_t1b; // @[Top.scala 120:11]
  assign n37_I_t1b_t1b_t0b = n36_O_t1b_t1b_t0b; // @[Top.scala 120:11]
  assign n37_I_t1b_t1b_t1b = n36_O_t1b_t1b_t1b; // @[Top.scala 120:11]
  assign n39_valid_up = n38_valid_down & n37_valid_down; // @[Top.scala 125:18]
  assign n39_I0 = n38_O; // @[Top.scala 123:12]
  assign n39_I1_t0b = n37_O_t0b; // @[Top.scala 124:12]
  assign n39_I1_t1b = n37_O_t1b; // @[Top.scala 124:12]
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
module InitialDelayCounter_3(
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
  wire  n42_valid_up; // @[Top.scala 133:21]
  wire  n42_valid_down; // @[Top.scala 133:21]
  wire [15:0] n42_I_t0b; // @[Top.scala 133:21]
  wire [15:0] n42_O; // @[Top.scala 133:21]
  wire  n70_clock; // @[Top.scala 136:21]
  wire  n70_reset; // @[Top.scala 136:21]
  wire  n70_valid_up; // @[Top.scala 136:21]
  wire  n70_valid_down; // @[Top.scala 136:21]
  wire [15:0] n70_I; // @[Top.scala 136:21]
  wire [15:0] n70_O; // @[Top.scala 136:21]
  wire  n58_clock; // @[Top.scala 139:21]
  wire  n58_reset; // @[Top.scala 139:21]
  wire  n58_valid_up; // @[Top.scala 139:21]
  wire  n58_valid_down; // @[Top.scala 139:21]
  wire [15:0] n58_I; // @[Top.scala 139:21]
  wire [15:0] n58_O; // @[Top.scala 139:21]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n44_valid_up; // @[Top.scala 143:21]
  wire  n44_valid_down; // @[Top.scala 143:21]
  wire [15:0] n44_I_t1b_t0b; // @[Top.scala 143:21]
  wire [15:0] n44_I_t1b_t1b; // @[Top.scala 143:21]
  wire [15:0] n44_O_t0b; // @[Top.scala 143:21]
  wire [15:0] n44_O_t1b; // @[Top.scala 143:21]
  wire  n45_valid_up; // @[Top.scala 146:21]
  wire  n45_valid_down; // @[Top.scala 146:21]
  wire [15:0] n45_I_t0b; // @[Top.scala 146:21]
  wire [15:0] n45_O; // @[Top.scala 146:21]
  wire  n46_valid_up; // @[Top.scala 149:21]
  wire  n46_valid_down; // @[Top.scala 149:21]
  wire [15:0] n46_I_t1b; // @[Top.scala 149:21]
  wire [15:0] n46_O; // @[Top.scala 149:21]
  wire  n47_valid_up; // @[Top.scala 152:21]
  wire  n47_valid_down; // @[Top.scala 152:21]
  wire [15:0] n47_I0; // @[Top.scala 152:21]
  wire [15:0] n47_I1; // @[Top.scala 152:21]
  wire [15:0] n47_O_t0b; // @[Top.scala 152:21]
  wire [15:0] n47_O_t1b; // @[Top.scala 152:21]
  wire  n48_valid_up; // @[Top.scala 156:21]
  wire  n48_valid_down; // @[Top.scala 156:21]
  wire [15:0] n48_I_t0b; // @[Top.scala 156:21]
  wire [15:0] n48_I_t1b; // @[Top.scala 156:21]
  wire [15:0] n48_O; // @[Top.scala 156:21]
  wire  n50_valid_up; // @[Top.scala 159:21]
  wire  n50_valid_down; // @[Top.scala 159:21]
  wire [15:0] n50_I0; // @[Top.scala 159:21]
  wire [15:0] n50_I1; // @[Top.scala 159:21]
  wire [15:0] n50_O_t0b; // @[Top.scala 159:21]
  wire [15:0] n50_O_t1b; // @[Top.scala 159:21]
  wire  n51_valid_up; // @[Top.scala 163:21]
  wire  n51_valid_down; // @[Top.scala 163:21]
  wire [15:0] n51_I_t0b; // @[Top.scala 163:21]
  wire [15:0] n51_I_t1b; // @[Top.scala 163:21]
  wire [15:0] n51_O; // @[Top.scala 163:21]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n54_valid_up; // @[Top.scala 167:21]
  wire  n54_valid_down; // @[Top.scala 167:21]
  wire [15:0] n54_I0; // @[Top.scala 167:21]
  wire [15:0] n54_O_t0b; // @[Top.scala 167:21]
  wire  n55_valid_up; // @[Top.scala 171:21]
  wire  n55_valid_down; // @[Top.scala 171:21]
  wire [15:0] n55_I_t0b; // @[Top.scala 171:21]
  wire [15:0] n55_O; // @[Top.scala 171:21]
  wire  n56_valid_up; // @[Top.scala 174:21]
  wire  n56_valid_down; // @[Top.scala 174:21]
  wire [15:0] n56_I0; // @[Top.scala 174:21]
  wire [15:0] n56_I1; // @[Top.scala 174:21]
  wire [15:0] n56_O_t0b; // @[Top.scala 174:21]
  wire [15:0] n56_O_t1b; // @[Top.scala 174:21]
  wire  n57_clock; // @[Top.scala 178:21]
  wire  n57_reset; // @[Top.scala 178:21]
  wire  n57_valid_up; // @[Top.scala 178:21]
  wire  n57_valid_down; // @[Top.scala 178:21]
  wire [15:0] n57_I_t0b; // @[Top.scala 178:21]
  wire [15:0] n57_I_t1b; // @[Top.scala 178:21]
  wire [15:0] n57_O; // @[Top.scala 178:21]
  wire  n59_valid_up; // @[Top.scala 181:21]
  wire  n59_valid_down; // @[Top.scala 181:21]
  wire [15:0] n59_I0; // @[Top.scala 181:21]
  wire [15:0] n59_I1; // @[Top.scala 181:21]
  wire [15:0] n59_O_t0b; // @[Top.scala 181:21]
  wire [15:0] n59_O_t1b; // @[Top.scala 181:21]
  wire  n60_valid_up; // @[Top.scala 185:21]
  wire  n60_valid_down; // @[Top.scala 185:21]
  wire [15:0] n60_I_t0b; // @[Top.scala 185:21]
  wire [15:0] n60_I_t1b; // @[Top.scala 185:21]
  wire [15:0] n60_O; // @[Top.scala 185:21]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n62_valid_up; // @[Top.scala 189:21]
  wire  n62_valid_down; // @[Top.scala 189:21]
  wire [15:0] n62_I0; // @[Top.scala 189:21]
  wire [15:0] n62_I1; // @[Top.scala 189:21]
  wire [15:0] n62_O_t0b; // @[Top.scala 189:21]
  wire [15:0] n62_O_t1b; // @[Top.scala 189:21]
  wire  n63_valid_up; // @[Top.scala 193:21]
  wire  n63_valid_down; // @[Top.scala 193:21]
  wire [15:0] n63_I_t0b; // @[Top.scala 193:21]
  wire [15:0] n63_I_t1b; // @[Top.scala 193:21]
  wire [15:0] n63_O; // @[Top.scala 193:21]
  wire  n64_valid_up; // @[Top.scala 196:21]
  wire  n64_valid_down; // @[Top.scala 196:21]
  wire [15:0] n64_I0; // @[Top.scala 196:21]
  wire [15:0] n64_I1; // @[Top.scala 196:21]
  wire [15:0] n64_O_t0b; // @[Top.scala 196:21]
  wire [15:0] n64_O_t1b; // @[Top.scala 196:21]
  wire  n65_valid_up; // @[Top.scala 200:21]
  wire  n65_valid_down; // @[Top.scala 200:21]
  wire [15:0] n65_I0; // @[Top.scala 200:21]
  wire [15:0] n65_I1; // @[Top.scala 200:21]
  wire [15:0] n65_O_t0b; // @[Top.scala 200:21]
  wire [15:0] n65_O_t1b; // @[Top.scala 200:21]
  wire  n66_valid_up; // @[Top.scala 204:21]
  wire  n66_valid_down; // @[Top.scala 204:21]
  wire [15:0] n66_I0_t0b; // @[Top.scala 204:21]
  wire [15:0] n66_I0_t1b; // @[Top.scala 204:21]
  wire [15:0] n66_I1_t0b; // @[Top.scala 204:21]
  wire [15:0] n66_I1_t1b; // @[Top.scala 204:21]
  wire [15:0] n66_O_t0b_t0b; // @[Top.scala 204:21]
  wire [15:0] n66_O_t0b_t1b; // @[Top.scala 204:21]
  wire [15:0] n66_O_t1b_t0b; // @[Top.scala 204:21]
  wire [15:0] n66_O_t1b_t1b; // @[Top.scala 204:21]
  wire  n67_clock; // @[Top.scala 208:21]
  wire  n67_reset; // @[Top.scala 208:21]
  wire  n67_valid_up; // @[Top.scala 208:21]
  wire  n67_valid_down; // @[Top.scala 208:21]
  wire [15:0] n67_I_t0b_t0b; // @[Top.scala 208:21]
  wire [15:0] n67_I_t0b_t1b; // @[Top.scala 208:21]
  wire [15:0] n67_I_t1b_t0b; // @[Top.scala 208:21]
  wire [15:0] n67_I_t1b_t1b; // @[Top.scala 208:21]
  wire [15:0] n67_O_t0b_t0b; // @[Top.scala 208:21]
  wire [15:0] n67_O_t0b_t1b; // @[Top.scala 208:21]
  wire [15:0] n67_O_t1b_t0b; // @[Top.scala 208:21]
  wire [15:0] n67_O_t1b_t1b; // @[Top.scala 208:21]
  wire  n68_valid_up; // @[Top.scala 211:21]
  wire  n68_valid_down; // @[Top.scala 211:21]
  wire  n68_I0; // @[Top.scala 211:21]
  wire [15:0] n68_I1_t0b_t0b; // @[Top.scala 211:21]
  wire [15:0] n68_I1_t0b_t1b; // @[Top.scala 211:21]
  wire [15:0] n68_I1_t1b_t0b; // @[Top.scala 211:21]
  wire [15:0] n68_I1_t1b_t1b; // @[Top.scala 211:21]
  wire  n68_O_t0b; // @[Top.scala 211:21]
  wire [15:0] n68_O_t1b_t0b_t0b; // @[Top.scala 211:21]
  wire [15:0] n68_O_t1b_t0b_t1b; // @[Top.scala 211:21]
  wire [15:0] n68_O_t1b_t1b_t0b; // @[Top.scala 211:21]
  wire [15:0] n68_O_t1b_t1b_t1b; // @[Top.scala 211:21]
  wire  n69_valid_up; // @[Top.scala 215:21]
  wire  n69_valid_down; // @[Top.scala 215:21]
  wire  n69_I_t0b; // @[Top.scala 215:21]
  wire [15:0] n69_I_t1b_t0b_t0b; // @[Top.scala 215:21]
  wire [15:0] n69_I_t1b_t0b_t1b; // @[Top.scala 215:21]
  wire [15:0] n69_I_t1b_t1b_t0b; // @[Top.scala 215:21]
  wire [15:0] n69_I_t1b_t1b_t1b; // @[Top.scala 215:21]
  wire [15:0] n69_O_t0b; // @[Top.scala 215:21]
  wire [15:0] n69_O_t1b; // @[Top.scala 215:21]
  wire  n71_valid_up; // @[Top.scala 218:21]
  wire  n71_valid_down; // @[Top.scala 218:21]
  wire [15:0] n71_I0; // @[Top.scala 218:21]
  wire [15:0] n71_I1_t0b; // @[Top.scala 218:21]
  wire [15:0] n71_I1_t1b; // @[Top.scala 218:21]
  wire [15:0] n71_O_t0b; // @[Top.scala 218:21]
  wire [15:0] n71_O_t1b_t0b; // @[Top.scala 218:21]
  wire [15:0] n71_O_t1b_t1b; // @[Top.scala 218:21]
  Fst n42 ( // @[Top.scala 133:21]
    .valid_up(n42_valid_up),
    .valid_down(n42_valid_down),
    .I_t0b(n42_I_t0b),
    .O(n42_O)
  );
  FIFO_2 n70 ( // @[Top.scala 136:21]
    .clock(n70_clock),
    .reset(n70_reset),
    .valid_up(n70_valid_up),
    .valid_down(n70_valid_down),
    .I(n70_I),
    .O(n70_O)
  );
  FIFO_2 n58 ( // @[Top.scala 139:21]
    .clock(n58_clock),
    .reset(n58_reset),
    .valid_up(n58_valid_up),
    .valid_down(n58_valid_down),
    .I(n58_I),
    .O(n58_O)
  );
  InitialDelayCounter_3 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n44 ( // @[Top.scala 143:21]
    .valid_up(n44_valid_up),
    .valid_down(n44_valid_down),
    .I_t1b_t0b(n44_I_t1b_t0b),
    .I_t1b_t1b(n44_I_t1b_t1b),
    .O_t0b(n44_O_t0b),
    .O_t1b(n44_O_t1b)
  );
  Fst_1 n45 ( // @[Top.scala 146:21]
    .valid_up(n45_valid_up),
    .valid_down(n45_valid_down),
    .I_t0b(n45_I_t0b),
    .O(n45_O)
  );
  Snd_1 n46 ( // @[Top.scala 149:21]
    .valid_up(n46_valid_up),
    .valid_down(n46_valid_down),
    .I_t1b(n46_I_t1b),
    .O(n46_O)
  );
  AtomTuple n47 ( // @[Top.scala 152:21]
    .valid_up(n47_valid_up),
    .valid_down(n47_valid_down),
    .I0(n47_I0),
    .I1(n47_I1),
    .O_t0b(n47_O_t0b),
    .O_t1b(n47_O_t1b)
  );
  Add n48 ( // @[Top.scala 156:21]
    .valid_up(n48_valid_up),
    .valid_down(n48_valid_down),
    .I_t0b(n48_I_t0b),
    .I_t1b(n48_I_t1b),
    .O(n48_O)
  );
  AtomTuple n50 ( // @[Top.scala 159:21]
    .valid_up(n50_valid_up),
    .valid_down(n50_valid_down),
    .I0(n50_I0),
    .I1(n50_I1),
    .O_t0b(n50_O_t0b),
    .O_t1b(n50_O_t1b)
  );
  Add n51 ( // @[Top.scala 163:21]
    .valid_up(n51_valid_up),
    .valid_down(n51_valid_down),
    .I_t0b(n51_I_t0b),
    .I_t1b(n51_I_t1b),
    .O(n51_O)
  );
  InitialDelayCounter_3 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n54 ( // @[Top.scala 167:21]
    .valid_up(n54_valid_up),
    .valid_down(n54_valid_down),
    .I0(n54_I0),
    .O_t0b(n54_O_t0b)
  );
  RShift n55 ( // @[Top.scala 171:21]
    .valid_up(n55_valid_up),
    .valid_down(n55_valid_down),
    .I_t0b(n55_I_t0b),
    .O(n55_O)
  );
  AtomTuple n56 ( // @[Top.scala 174:21]
    .valid_up(n56_valid_up),
    .valid_down(n56_valid_down),
    .I0(n56_I0),
    .I1(n56_I1),
    .O_t0b(n56_O_t0b),
    .O_t1b(n56_O_t1b)
  );
  Mul n57 ( // @[Top.scala 178:21]
    .clock(n57_clock),
    .reset(n57_reset),
    .valid_up(n57_valid_up),
    .valid_down(n57_valid_down),
    .I_t0b(n57_I_t0b),
    .I_t1b(n57_I_t1b),
    .O(n57_O)
  );
  AtomTuple n59 ( // @[Top.scala 181:21]
    .valid_up(n59_valid_up),
    .valid_down(n59_valid_down),
    .I0(n59_I0),
    .I1(n59_I1),
    .O_t0b(n59_O_t0b),
    .O_t1b(n59_O_t1b)
  );
  Lt n60 ( // @[Top.scala 185:21]
    .valid_up(n60_valid_up),
    .valid_down(n60_valid_down),
    .I_t0b(n60_I_t0b),
    .I_t1b(n60_I_t1b),
    .O(n60_O)
  );
  InitialDelayCounter_3 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n62 ( // @[Top.scala 189:21]
    .valid_up(n62_valid_up),
    .valid_down(n62_valid_down),
    .I0(n62_I0),
    .I1(n62_I1),
    .O_t0b(n62_O_t0b),
    .O_t1b(n62_O_t1b)
  );
  Sub n63 ( // @[Top.scala 193:21]
    .valid_up(n63_valid_up),
    .valid_down(n63_valid_down),
    .I_t0b(n63_I_t0b),
    .I_t1b(n63_I_t1b),
    .O(n63_O)
  );
  AtomTuple n64 ( // @[Top.scala 196:21]
    .valid_up(n64_valid_up),
    .valid_down(n64_valid_down),
    .I0(n64_I0),
    .I1(n64_I1),
    .O_t0b(n64_O_t0b),
    .O_t1b(n64_O_t1b)
  );
  AtomTuple n65 ( // @[Top.scala 200:21]
    .valid_up(n65_valid_up),
    .valid_down(n65_valid_down),
    .I0(n65_I0),
    .I1(n65_I1),
    .O_t0b(n65_O_t0b),
    .O_t1b(n65_O_t1b)
  );
  AtomTuple_10 n66 ( // @[Top.scala 204:21]
    .valid_up(n66_valid_up),
    .valid_down(n66_valid_down),
    .I0_t0b(n66_I0_t0b),
    .I0_t1b(n66_I0_t1b),
    .I1_t0b(n66_I1_t0b),
    .I1_t1b(n66_I1_t1b),
    .O_t0b_t0b(n66_O_t0b_t0b),
    .O_t0b_t1b(n66_O_t0b_t1b),
    .O_t1b_t0b(n66_O_t1b_t0b),
    .O_t1b_t1b(n66_O_t1b_t1b)
  );
  FIFO_4 n67 ( // @[Top.scala 208:21]
    .clock(n67_clock),
    .reset(n67_reset),
    .valid_up(n67_valid_up),
    .valid_down(n67_valid_down),
    .I_t0b_t0b(n67_I_t0b_t0b),
    .I_t0b_t1b(n67_I_t0b_t1b),
    .I_t1b_t0b(n67_I_t1b_t0b),
    .I_t1b_t1b(n67_I_t1b_t1b),
    .O_t0b_t0b(n67_O_t0b_t0b),
    .O_t0b_t1b(n67_O_t0b_t1b),
    .O_t1b_t0b(n67_O_t1b_t0b),
    .O_t1b_t1b(n67_O_t1b_t1b)
  );
  AtomTuple_11 n68 ( // @[Top.scala 211:21]
    .valid_up(n68_valid_up),
    .valid_down(n68_valid_down),
    .I0(n68_I0),
    .I1_t0b_t0b(n68_I1_t0b_t0b),
    .I1_t0b_t1b(n68_I1_t0b_t1b),
    .I1_t1b_t0b(n68_I1_t1b_t0b),
    .I1_t1b_t1b(n68_I1_t1b_t1b),
    .O_t0b(n68_O_t0b),
    .O_t1b_t0b_t0b(n68_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n68_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n68_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n68_O_t1b_t1b_t1b)
  );
  If n69 ( // @[Top.scala 215:21]
    .valid_up(n69_valid_up),
    .valid_down(n69_valid_down),
    .I_t0b(n69_I_t0b),
    .I_t1b_t0b_t0b(n69_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n69_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n69_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n69_I_t1b_t1b_t1b),
    .O_t0b(n69_O_t0b),
    .O_t1b(n69_O_t1b)
  );
  AtomTuple_1 n71 ( // @[Top.scala 218:21]
    .valid_up(n71_valid_up),
    .valid_down(n71_valid_down),
    .I0(n71_I0),
    .I1_t0b(n71_I1_t0b),
    .I1_t1b(n71_I1_t1b),
    .O_t0b(n71_O_t0b),
    .O_t1b_t0b(n71_O_t1b_t0b),
    .O_t1b_t1b(n71_O_t1b_t1b)
  );
  assign valid_down = n71_valid_down; // @[Top.scala 223:16]
  assign O_t0b = n71_O_t0b; // @[Top.scala 222:7]
  assign O_t1b_t0b = n71_O_t1b_t0b; // @[Top.scala 222:7]
  assign O_t1b_t1b = n71_O_t1b_t1b; // @[Top.scala 222:7]
  assign n42_valid_up = valid_up; // @[Top.scala 135:18]
  assign n42_I_t0b = I_t0b; // @[Top.scala 134:11]
  assign n70_clock = clock;
  assign n70_reset = reset;
  assign n70_valid_up = n42_valid_down; // @[Top.scala 138:18]
  assign n70_I = n42_O; // @[Top.scala 137:11]
  assign n58_clock = clock;
  assign n58_reset = reset;
  assign n58_valid_up = n42_valid_down; // @[Top.scala 141:18]
  assign n58_I = n42_O; // @[Top.scala 140:11]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n44_valid_up = valid_up; // @[Top.scala 145:18]
  assign n44_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 144:11]
  assign n44_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 144:11]
  assign n45_valid_up = n44_valid_down; // @[Top.scala 148:18]
  assign n45_I_t0b = n44_O_t0b; // @[Top.scala 147:11]
  assign n46_valid_up = n44_valid_down; // @[Top.scala 151:18]
  assign n46_I_t1b = n44_O_t1b; // @[Top.scala 150:11]
  assign n47_valid_up = n45_valid_down & n46_valid_down; // @[Top.scala 155:18]
  assign n47_I0 = n45_O; // @[Top.scala 153:12]
  assign n47_I1 = n46_O; // @[Top.scala 154:12]
  assign n48_valid_up = n47_valid_down; // @[Top.scala 158:18]
  assign n48_I_t0b = n47_O_t0b; // @[Top.scala 157:11]
  assign n48_I_t1b = n47_O_t1b; // @[Top.scala 157:11]
  assign n50_valid_up = InitialDelayCounter_valid_down & n48_valid_down; // @[Top.scala 162:18]
  assign n50_I0 = 16'h1; // @[Top.scala 160:12]
  assign n50_I1 = n48_O; // @[Top.scala 161:12]
  assign n51_valid_up = n50_valid_down; // @[Top.scala 165:18]
  assign n51_I_t0b = n50_O_t0b; // @[Top.scala 164:11]
  assign n51_I_t1b = n50_O_t1b; // @[Top.scala 164:11]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n54_valid_up = n51_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 170:18]
  assign n54_I0 = n51_O; // @[Top.scala 168:12]
  assign n55_valid_up = n54_valid_down; // @[Top.scala 173:18]
  assign n55_I_t0b = n54_O_t0b; // @[Top.scala 172:11]
  assign n56_valid_up = n55_valid_down; // @[Top.scala 177:18]
  assign n56_I0 = n55_O; // @[Top.scala 175:12]
  assign n56_I1 = n55_O; // @[Top.scala 176:12]
  assign n57_clock = clock;
  assign n57_reset = reset;
  assign n57_valid_up = n56_valid_down; // @[Top.scala 180:18]
  assign n57_I_t0b = n56_O_t0b; // @[Top.scala 179:11]
  assign n57_I_t1b = n56_O_t1b; // @[Top.scala 179:11]
  assign n59_valid_up = n58_valid_down & n57_valid_down; // @[Top.scala 184:18]
  assign n59_I0 = n58_O; // @[Top.scala 182:12]
  assign n59_I1 = n57_O; // @[Top.scala 183:12]
  assign n60_valid_up = n59_valid_down; // @[Top.scala 187:18]
  assign n60_I_t0b = n59_O_t0b; // @[Top.scala 186:11]
  assign n60_I_t1b = n59_O_t1b; // @[Top.scala 186:11]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n62_valid_up = n55_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 192:18]
  assign n62_I0 = n55_O; // @[Top.scala 190:12]
  assign n62_I1 = 16'h1; // @[Top.scala 191:12]
  assign n63_valid_up = n62_valid_down; // @[Top.scala 195:18]
  assign n63_I_t0b = n62_O_t0b; // @[Top.scala 194:11]
  assign n63_I_t1b = n62_O_t1b; // @[Top.scala 194:11]
  assign n64_valid_up = n45_valid_down & n63_valid_down; // @[Top.scala 199:18]
  assign n64_I0 = n45_O; // @[Top.scala 197:12]
  assign n64_I1 = n63_O; // @[Top.scala 198:12]
  assign n65_valid_up = n55_valid_down & n46_valid_down; // @[Top.scala 203:18]
  assign n65_I0 = n55_O; // @[Top.scala 201:12]
  assign n65_I1 = n46_O; // @[Top.scala 202:12]
  assign n66_valid_up = n64_valid_down & n65_valid_down; // @[Top.scala 207:18]
  assign n66_I0_t0b = n64_O_t0b; // @[Top.scala 205:12]
  assign n66_I0_t1b = n64_O_t1b; // @[Top.scala 205:12]
  assign n66_I1_t0b = n65_O_t0b; // @[Top.scala 206:12]
  assign n66_I1_t1b = n65_O_t1b; // @[Top.scala 206:12]
  assign n67_clock = clock;
  assign n67_reset = reset;
  assign n67_valid_up = n66_valid_down; // @[Top.scala 210:18]
  assign n67_I_t0b_t0b = n66_O_t0b_t0b; // @[Top.scala 209:11]
  assign n67_I_t0b_t1b = n66_O_t0b_t1b; // @[Top.scala 209:11]
  assign n67_I_t1b_t0b = n66_O_t1b_t0b; // @[Top.scala 209:11]
  assign n67_I_t1b_t1b = n66_O_t1b_t1b; // @[Top.scala 209:11]
  assign n68_valid_up = n60_valid_down & n67_valid_down; // @[Top.scala 214:18]
  assign n68_I0 = n60_O[0]; // @[Top.scala 212:12]
  assign n68_I1_t0b_t0b = n67_O_t0b_t0b; // @[Top.scala 213:12]
  assign n68_I1_t0b_t1b = n67_O_t0b_t1b; // @[Top.scala 213:12]
  assign n68_I1_t1b_t0b = n67_O_t1b_t0b; // @[Top.scala 213:12]
  assign n68_I1_t1b_t1b = n67_O_t1b_t1b; // @[Top.scala 213:12]
  assign n69_valid_up = n68_valid_down; // @[Top.scala 217:18]
  assign n69_I_t0b = n68_O_t0b; // @[Top.scala 216:11]
  assign n69_I_t1b_t0b_t0b = n68_O_t1b_t0b_t0b; // @[Top.scala 216:11]
  assign n69_I_t1b_t0b_t1b = n68_O_t1b_t0b_t1b; // @[Top.scala 216:11]
  assign n69_I_t1b_t1b_t0b = n68_O_t1b_t1b_t0b; // @[Top.scala 216:11]
  assign n69_I_t1b_t1b_t1b = n68_O_t1b_t1b_t1b; // @[Top.scala 216:11]
  assign n71_valid_up = n70_valid_down & n69_valid_down; // @[Top.scala 221:18]
  assign n71_I0 = n70_O; // @[Top.scala 219:12]
  assign n71_I1_t0b = n69_O_t0b; // @[Top.scala 220:12]
  assign n71_I1_t1b = n69_O_t1b; // @[Top.scala 220:12]
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
module InitialDelayCounter_6(
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
  wire  n74_valid_up; // @[Top.scala 229:21]
  wire  n74_valid_down; // @[Top.scala 229:21]
  wire [15:0] n74_I_t0b; // @[Top.scala 229:21]
  wire [15:0] n74_O; // @[Top.scala 229:21]
  wire  n102_clock; // @[Top.scala 232:22]
  wire  n102_reset; // @[Top.scala 232:22]
  wire  n102_valid_up; // @[Top.scala 232:22]
  wire  n102_valid_down; // @[Top.scala 232:22]
  wire [15:0] n102_I; // @[Top.scala 232:22]
  wire [15:0] n102_O; // @[Top.scala 232:22]
  wire  n90_clock; // @[Top.scala 235:21]
  wire  n90_reset; // @[Top.scala 235:21]
  wire  n90_valid_up; // @[Top.scala 235:21]
  wire  n90_valid_down; // @[Top.scala 235:21]
  wire [15:0] n90_I; // @[Top.scala 235:21]
  wire [15:0] n90_O; // @[Top.scala 235:21]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n76_valid_up; // @[Top.scala 239:21]
  wire  n76_valid_down; // @[Top.scala 239:21]
  wire [15:0] n76_I_t1b_t0b; // @[Top.scala 239:21]
  wire [15:0] n76_I_t1b_t1b; // @[Top.scala 239:21]
  wire [15:0] n76_O_t0b; // @[Top.scala 239:21]
  wire [15:0] n76_O_t1b; // @[Top.scala 239:21]
  wire  n77_valid_up; // @[Top.scala 242:21]
  wire  n77_valid_down; // @[Top.scala 242:21]
  wire [15:0] n77_I_t0b; // @[Top.scala 242:21]
  wire [15:0] n77_O; // @[Top.scala 242:21]
  wire  n78_valid_up; // @[Top.scala 245:21]
  wire  n78_valid_down; // @[Top.scala 245:21]
  wire [15:0] n78_I_t1b; // @[Top.scala 245:21]
  wire [15:0] n78_O; // @[Top.scala 245:21]
  wire  n79_valid_up; // @[Top.scala 248:21]
  wire  n79_valid_down; // @[Top.scala 248:21]
  wire [15:0] n79_I0; // @[Top.scala 248:21]
  wire [15:0] n79_I1; // @[Top.scala 248:21]
  wire [15:0] n79_O_t0b; // @[Top.scala 248:21]
  wire [15:0] n79_O_t1b; // @[Top.scala 248:21]
  wire  n80_valid_up; // @[Top.scala 252:21]
  wire  n80_valid_down; // @[Top.scala 252:21]
  wire [15:0] n80_I_t0b; // @[Top.scala 252:21]
  wire [15:0] n80_I_t1b; // @[Top.scala 252:21]
  wire [15:0] n80_O; // @[Top.scala 252:21]
  wire  n82_valid_up; // @[Top.scala 255:21]
  wire  n82_valid_down; // @[Top.scala 255:21]
  wire [15:0] n82_I0; // @[Top.scala 255:21]
  wire [15:0] n82_I1; // @[Top.scala 255:21]
  wire [15:0] n82_O_t0b; // @[Top.scala 255:21]
  wire [15:0] n82_O_t1b; // @[Top.scala 255:21]
  wire  n83_valid_up; // @[Top.scala 259:21]
  wire  n83_valid_down; // @[Top.scala 259:21]
  wire [15:0] n83_I_t0b; // @[Top.scala 259:21]
  wire [15:0] n83_I_t1b; // @[Top.scala 259:21]
  wire [15:0] n83_O; // @[Top.scala 259:21]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n86_valid_up; // @[Top.scala 263:21]
  wire  n86_valid_down; // @[Top.scala 263:21]
  wire [15:0] n86_I0; // @[Top.scala 263:21]
  wire [15:0] n86_O_t0b; // @[Top.scala 263:21]
  wire  n87_valid_up; // @[Top.scala 267:21]
  wire  n87_valid_down; // @[Top.scala 267:21]
  wire [15:0] n87_I_t0b; // @[Top.scala 267:21]
  wire [15:0] n87_O; // @[Top.scala 267:21]
  wire  n88_valid_up; // @[Top.scala 270:21]
  wire  n88_valid_down; // @[Top.scala 270:21]
  wire [15:0] n88_I0; // @[Top.scala 270:21]
  wire [15:0] n88_I1; // @[Top.scala 270:21]
  wire [15:0] n88_O_t0b; // @[Top.scala 270:21]
  wire [15:0] n88_O_t1b; // @[Top.scala 270:21]
  wire  n89_clock; // @[Top.scala 274:21]
  wire  n89_reset; // @[Top.scala 274:21]
  wire  n89_valid_up; // @[Top.scala 274:21]
  wire  n89_valid_down; // @[Top.scala 274:21]
  wire [15:0] n89_I_t0b; // @[Top.scala 274:21]
  wire [15:0] n89_I_t1b; // @[Top.scala 274:21]
  wire [15:0] n89_O; // @[Top.scala 274:21]
  wire  n91_valid_up; // @[Top.scala 277:21]
  wire  n91_valid_down; // @[Top.scala 277:21]
  wire [15:0] n91_I0; // @[Top.scala 277:21]
  wire [15:0] n91_I1; // @[Top.scala 277:21]
  wire [15:0] n91_O_t0b; // @[Top.scala 277:21]
  wire [15:0] n91_O_t1b; // @[Top.scala 277:21]
  wire  n92_valid_up; // @[Top.scala 281:21]
  wire  n92_valid_down; // @[Top.scala 281:21]
  wire [15:0] n92_I_t0b; // @[Top.scala 281:21]
  wire [15:0] n92_I_t1b; // @[Top.scala 281:21]
  wire [15:0] n92_O; // @[Top.scala 281:21]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n94_valid_up; // @[Top.scala 285:21]
  wire  n94_valid_down; // @[Top.scala 285:21]
  wire [15:0] n94_I0; // @[Top.scala 285:21]
  wire [15:0] n94_I1; // @[Top.scala 285:21]
  wire [15:0] n94_O_t0b; // @[Top.scala 285:21]
  wire [15:0] n94_O_t1b; // @[Top.scala 285:21]
  wire  n95_valid_up; // @[Top.scala 289:21]
  wire  n95_valid_down; // @[Top.scala 289:21]
  wire [15:0] n95_I_t0b; // @[Top.scala 289:21]
  wire [15:0] n95_I_t1b; // @[Top.scala 289:21]
  wire [15:0] n95_O; // @[Top.scala 289:21]
  wire  n96_valid_up; // @[Top.scala 292:21]
  wire  n96_valid_down; // @[Top.scala 292:21]
  wire [15:0] n96_I0; // @[Top.scala 292:21]
  wire [15:0] n96_I1; // @[Top.scala 292:21]
  wire [15:0] n96_O_t0b; // @[Top.scala 292:21]
  wire [15:0] n96_O_t1b; // @[Top.scala 292:21]
  wire  n97_valid_up; // @[Top.scala 296:21]
  wire  n97_valid_down; // @[Top.scala 296:21]
  wire [15:0] n97_I0; // @[Top.scala 296:21]
  wire [15:0] n97_I1; // @[Top.scala 296:21]
  wire [15:0] n97_O_t0b; // @[Top.scala 296:21]
  wire [15:0] n97_O_t1b; // @[Top.scala 296:21]
  wire  n98_valid_up; // @[Top.scala 300:21]
  wire  n98_valid_down; // @[Top.scala 300:21]
  wire [15:0] n98_I0_t0b; // @[Top.scala 300:21]
  wire [15:0] n98_I0_t1b; // @[Top.scala 300:21]
  wire [15:0] n98_I1_t0b; // @[Top.scala 300:21]
  wire [15:0] n98_I1_t1b; // @[Top.scala 300:21]
  wire [15:0] n98_O_t0b_t0b; // @[Top.scala 300:21]
  wire [15:0] n98_O_t0b_t1b; // @[Top.scala 300:21]
  wire [15:0] n98_O_t1b_t0b; // @[Top.scala 300:21]
  wire [15:0] n98_O_t1b_t1b; // @[Top.scala 300:21]
  wire  n99_clock; // @[Top.scala 304:21]
  wire  n99_reset; // @[Top.scala 304:21]
  wire  n99_valid_up; // @[Top.scala 304:21]
  wire  n99_valid_down; // @[Top.scala 304:21]
  wire [15:0] n99_I_t0b_t0b; // @[Top.scala 304:21]
  wire [15:0] n99_I_t0b_t1b; // @[Top.scala 304:21]
  wire [15:0] n99_I_t1b_t0b; // @[Top.scala 304:21]
  wire [15:0] n99_I_t1b_t1b; // @[Top.scala 304:21]
  wire [15:0] n99_O_t0b_t0b; // @[Top.scala 304:21]
  wire [15:0] n99_O_t0b_t1b; // @[Top.scala 304:21]
  wire [15:0] n99_O_t1b_t0b; // @[Top.scala 304:21]
  wire [15:0] n99_O_t1b_t1b; // @[Top.scala 304:21]
  wire  n100_valid_up; // @[Top.scala 307:22]
  wire  n100_valid_down; // @[Top.scala 307:22]
  wire  n100_I0; // @[Top.scala 307:22]
  wire [15:0] n100_I1_t0b_t0b; // @[Top.scala 307:22]
  wire [15:0] n100_I1_t0b_t1b; // @[Top.scala 307:22]
  wire [15:0] n100_I1_t1b_t0b; // @[Top.scala 307:22]
  wire [15:0] n100_I1_t1b_t1b; // @[Top.scala 307:22]
  wire  n100_O_t0b; // @[Top.scala 307:22]
  wire [15:0] n100_O_t1b_t0b_t0b; // @[Top.scala 307:22]
  wire [15:0] n100_O_t1b_t0b_t1b; // @[Top.scala 307:22]
  wire [15:0] n100_O_t1b_t1b_t0b; // @[Top.scala 307:22]
  wire [15:0] n100_O_t1b_t1b_t1b; // @[Top.scala 307:22]
  wire  n101_valid_up; // @[Top.scala 311:22]
  wire  n101_valid_down; // @[Top.scala 311:22]
  wire  n101_I_t0b; // @[Top.scala 311:22]
  wire [15:0] n101_I_t1b_t0b_t0b; // @[Top.scala 311:22]
  wire [15:0] n101_I_t1b_t0b_t1b; // @[Top.scala 311:22]
  wire [15:0] n101_I_t1b_t1b_t0b; // @[Top.scala 311:22]
  wire [15:0] n101_I_t1b_t1b_t1b; // @[Top.scala 311:22]
  wire [15:0] n101_O_t0b; // @[Top.scala 311:22]
  wire [15:0] n101_O_t1b; // @[Top.scala 311:22]
  wire  n103_valid_up; // @[Top.scala 314:22]
  wire  n103_valid_down; // @[Top.scala 314:22]
  wire [15:0] n103_I0; // @[Top.scala 314:22]
  wire [15:0] n103_I1_t0b; // @[Top.scala 314:22]
  wire [15:0] n103_I1_t1b; // @[Top.scala 314:22]
  wire [15:0] n103_O_t0b; // @[Top.scala 314:22]
  wire [15:0] n103_O_t1b_t0b; // @[Top.scala 314:22]
  wire [15:0] n103_O_t1b_t1b; // @[Top.scala 314:22]
  Fst n74 ( // @[Top.scala 229:21]
    .valid_up(n74_valid_up),
    .valid_down(n74_valid_down),
    .I_t0b(n74_I_t0b),
    .O(n74_O)
  );
  FIFO_2 n102 ( // @[Top.scala 232:22]
    .clock(n102_clock),
    .reset(n102_reset),
    .valid_up(n102_valid_up),
    .valid_down(n102_valid_down),
    .I(n102_I),
    .O(n102_O)
  );
  FIFO_2 n90 ( // @[Top.scala 235:21]
    .clock(n90_clock),
    .reset(n90_reset),
    .valid_up(n90_valid_up),
    .valid_down(n90_valid_down),
    .I(n90_I),
    .O(n90_O)
  );
  InitialDelayCounter_6 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n76 ( // @[Top.scala 239:21]
    .valid_up(n76_valid_up),
    .valid_down(n76_valid_down),
    .I_t1b_t0b(n76_I_t1b_t0b),
    .I_t1b_t1b(n76_I_t1b_t1b),
    .O_t0b(n76_O_t0b),
    .O_t1b(n76_O_t1b)
  );
  Fst_1 n77 ( // @[Top.scala 242:21]
    .valid_up(n77_valid_up),
    .valid_down(n77_valid_down),
    .I_t0b(n77_I_t0b),
    .O(n77_O)
  );
  Snd_1 n78 ( // @[Top.scala 245:21]
    .valid_up(n78_valid_up),
    .valid_down(n78_valid_down),
    .I_t1b(n78_I_t1b),
    .O(n78_O)
  );
  AtomTuple n79 ( // @[Top.scala 248:21]
    .valid_up(n79_valid_up),
    .valid_down(n79_valid_down),
    .I0(n79_I0),
    .I1(n79_I1),
    .O_t0b(n79_O_t0b),
    .O_t1b(n79_O_t1b)
  );
  Add n80 ( // @[Top.scala 252:21]
    .valid_up(n80_valid_up),
    .valid_down(n80_valid_down),
    .I_t0b(n80_I_t0b),
    .I_t1b(n80_I_t1b),
    .O(n80_O)
  );
  AtomTuple n82 ( // @[Top.scala 255:21]
    .valid_up(n82_valid_up),
    .valid_down(n82_valid_down),
    .I0(n82_I0),
    .I1(n82_I1),
    .O_t0b(n82_O_t0b),
    .O_t1b(n82_O_t1b)
  );
  Add n83 ( // @[Top.scala 259:21]
    .valid_up(n83_valid_up),
    .valid_down(n83_valid_down),
    .I_t0b(n83_I_t0b),
    .I_t1b(n83_I_t1b),
    .O(n83_O)
  );
  InitialDelayCounter_6 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n86 ( // @[Top.scala 263:21]
    .valid_up(n86_valid_up),
    .valid_down(n86_valid_down),
    .I0(n86_I0),
    .O_t0b(n86_O_t0b)
  );
  RShift n87 ( // @[Top.scala 267:21]
    .valid_up(n87_valid_up),
    .valid_down(n87_valid_down),
    .I_t0b(n87_I_t0b),
    .O(n87_O)
  );
  AtomTuple n88 ( // @[Top.scala 270:21]
    .valid_up(n88_valid_up),
    .valid_down(n88_valid_down),
    .I0(n88_I0),
    .I1(n88_I1),
    .O_t0b(n88_O_t0b),
    .O_t1b(n88_O_t1b)
  );
  Mul n89 ( // @[Top.scala 274:21]
    .clock(n89_clock),
    .reset(n89_reset),
    .valid_up(n89_valid_up),
    .valid_down(n89_valid_down),
    .I_t0b(n89_I_t0b),
    .I_t1b(n89_I_t1b),
    .O(n89_O)
  );
  AtomTuple n91 ( // @[Top.scala 277:21]
    .valid_up(n91_valid_up),
    .valid_down(n91_valid_down),
    .I0(n91_I0),
    .I1(n91_I1),
    .O_t0b(n91_O_t0b),
    .O_t1b(n91_O_t1b)
  );
  Lt n92 ( // @[Top.scala 281:21]
    .valid_up(n92_valid_up),
    .valid_down(n92_valid_down),
    .I_t0b(n92_I_t0b),
    .I_t1b(n92_I_t1b),
    .O(n92_O)
  );
  InitialDelayCounter_6 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n94 ( // @[Top.scala 285:21]
    .valid_up(n94_valid_up),
    .valid_down(n94_valid_down),
    .I0(n94_I0),
    .I1(n94_I1),
    .O_t0b(n94_O_t0b),
    .O_t1b(n94_O_t1b)
  );
  Sub n95 ( // @[Top.scala 289:21]
    .valid_up(n95_valid_up),
    .valid_down(n95_valid_down),
    .I_t0b(n95_I_t0b),
    .I_t1b(n95_I_t1b),
    .O(n95_O)
  );
  AtomTuple n96 ( // @[Top.scala 292:21]
    .valid_up(n96_valid_up),
    .valid_down(n96_valid_down),
    .I0(n96_I0),
    .I1(n96_I1),
    .O_t0b(n96_O_t0b),
    .O_t1b(n96_O_t1b)
  );
  AtomTuple n97 ( // @[Top.scala 296:21]
    .valid_up(n97_valid_up),
    .valid_down(n97_valid_down),
    .I0(n97_I0),
    .I1(n97_I1),
    .O_t0b(n97_O_t0b),
    .O_t1b(n97_O_t1b)
  );
  AtomTuple_10 n98 ( // @[Top.scala 300:21]
    .valid_up(n98_valid_up),
    .valid_down(n98_valid_down),
    .I0_t0b(n98_I0_t0b),
    .I0_t1b(n98_I0_t1b),
    .I1_t0b(n98_I1_t0b),
    .I1_t1b(n98_I1_t1b),
    .O_t0b_t0b(n98_O_t0b_t0b),
    .O_t0b_t1b(n98_O_t0b_t1b),
    .O_t1b_t0b(n98_O_t1b_t0b),
    .O_t1b_t1b(n98_O_t1b_t1b)
  );
  FIFO_4 n99 ( // @[Top.scala 304:21]
    .clock(n99_clock),
    .reset(n99_reset),
    .valid_up(n99_valid_up),
    .valid_down(n99_valid_down),
    .I_t0b_t0b(n99_I_t0b_t0b),
    .I_t0b_t1b(n99_I_t0b_t1b),
    .I_t1b_t0b(n99_I_t1b_t0b),
    .I_t1b_t1b(n99_I_t1b_t1b),
    .O_t0b_t0b(n99_O_t0b_t0b),
    .O_t0b_t1b(n99_O_t0b_t1b),
    .O_t1b_t0b(n99_O_t1b_t0b),
    .O_t1b_t1b(n99_O_t1b_t1b)
  );
  AtomTuple_11 n100 ( // @[Top.scala 307:22]
    .valid_up(n100_valid_up),
    .valid_down(n100_valid_down),
    .I0(n100_I0),
    .I1_t0b_t0b(n100_I1_t0b_t0b),
    .I1_t0b_t1b(n100_I1_t0b_t1b),
    .I1_t1b_t0b(n100_I1_t1b_t0b),
    .I1_t1b_t1b(n100_I1_t1b_t1b),
    .O_t0b(n100_O_t0b),
    .O_t1b_t0b_t0b(n100_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n100_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n100_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n100_O_t1b_t1b_t1b)
  );
  If n101 ( // @[Top.scala 311:22]
    .valid_up(n101_valid_up),
    .valid_down(n101_valid_down),
    .I_t0b(n101_I_t0b),
    .I_t1b_t0b_t0b(n101_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n101_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n101_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n101_I_t1b_t1b_t1b),
    .O_t0b(n101_O_t0b),
    .O_t1b(n101_O_t1b)
  );
  AtomTuple_1 n103 ( // @[Top.scala 314:22]
    .valid_up(n103_valid_up),
    .valid_down(n103_valid_down),
    .I0(n103_I0),
    .I1_t0b(n103_I1_t0b),
    .I1_t1b(n103_I1_t1b),
    .O_t0b(n103_O_t0b),
    .O_t1b_t0b(n103_O_t1b_t0b),
    .O_t1b_t1b(n103_O_t1b_t1b)
  );
  assign valid_down = n103_valid_down; // @[Top.scala 319:16]
  assign O_t0b = n103_O_t0b; // @[Top.scala 318:7]
  assign O_t1b_t0b = n103_O_t1b_t0b; // @[Top.scala 318:7]
  assign O_t1b_t1b = n103_O_t1b_t1b; // @[Top.scala 318:7]
  assign n74_valid_up = valid_up; // @[Top.scala 231:18]
  assign n74_I_t0b = I_t0b; // @[Top.scala 230:11]
  assign n102_clock = clock;
  assign n102_reset = reset;
  assign n102_valid_up = n74_valid_down; // @[Top.scala 234:19]
  assign n102_I = n74_O; // @[Top.scala 233:12]
  assign n90_clock = clock;
  assign n90_reset = reset;
  assign n90_valid_up = n74_valid_down; // @[Top.scala 237:18]
  assign n90_I = n74_O; // @[Top.scala 236:11]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n76_valid_up = valid_up; // @[Top.scala 241:18]
  assign n76_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 240:11]
  assign n76_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 240:11]
  assign n77_valid_up = n76_valid_down; // @[Top.scala 244:18]
  assign n77_I_t0b = n76_O_t0b; // @[Top.scala 243:11]
  assign n78_valid_up = n76_valid_down; // @[Top.scala 247:18]
  assign n78_I_t1b = n76_O_t1b; // @[Top.scala 246:11]
  assign n79_valid_up = n77_valid_down & n78_valid_down; // @[Top.scala 251:18]
  assign n79_I0 = n77_O; // @[Top.scala 249:12]
  assign n79_I1 = n78_O; // @[Top.scala 250:12]
  assign n80_valid_up = n79_valid_down; // @[Top.scala 254:18]
  assign n80_I_t0b = n79_O_t0b; // @[Top.scala 253:11]
  assign n80_I_t1b = n79_O_t1b; // @[Top.scala 253:11]
  assign n82_valid_up = InitialDelayCounter_valid_down & n80_valid_down; // @[Top.scala 258:18]
  assign n82_I0 = 16'h1; // @[Top.scala 256:12]
  assign n82_I1 = n80_O; // @[Top.scala 257:12]
  assign n83_valid_up = n82_valid_down; // @[Top.scala 261:18]
  assign n83_I_t0b = n82_O_t0b; // @[Top.scala 260:11]
  assign n83_I_t1b = n82_O_t1b; // @[Top.scala 260:11]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n86_valid_up = n83_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 266:18]
  assign n86_I0 = n83_O; // @[Top.scala 264:12]
  assign n87_valid_up = n86_valid_down; // @[Top.scala 269:18]
  assign n87_I_t0b = n86_O_t0b; // @[Top.scala 268:11]
  assign n88_valid_up = n87_valid_down; // @[Top.scala 273:18]
  assign n88_I0 = n87_O; // @[Top.scala 271:12]
  assign n88_I1 = n87_O; // @[Top.scala 272:12]
  assign n89_clock = clock;
  assign n89_reset = reset;
  assign n89_valid_up = n88_valid_down; // @[Top.scala 276:18]
  assign n89_I_t0b = n88_O_t0b; // @[Top.scala 275:11]
  assign n89_I_t1b = n88_O_t1b; // @[Top.scala 275:11]
  assign n91_valid_up = n90_valid_down & n89_valid_down; // @[Top.scala 280:18]
  assign n91_I0 = n90_O; // @[Top.scala 278:12]
  assign n91_I1 = n89_O; // @[Top.scala 279:12]
  assign n92_valid_up = n91_valid_down; // @[Top.scala 283:18]
  assign n92_I_t0b = n91_O_t0b; // @[Top.scala 282:11]
  assign n92_I_t1b = n91_O_t1b; // @[Top.scala 282:11]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n94_valid_up = n87_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 288:18]
  assign n94_I0 = n87_O; // @[Top.scala 286:12]
  assign n94_I1 = 16'h1; // @[Top.scala 287:12]
  assign n95_valid_up = n94_valid_down; // @[Top.scala 291:18]
  assign n95_I_t0b = n94_O_t0b; // @[Top.scala 290:11]
  assign n95_I_t1b = n94_O_t1b; // @[Top.scala 290:11]
  assign n96_valid_up = n77_valid_down & n95_valid_down; // @[Top.scala 295:18]
  assign n96_I0 = n77_O; // @[Top.scala 293:12]
  assign n96_I1 = n95_O; // @[Top.scala 294:12]
  assign n97_valid_up = n87_valid_down & n78_valid_down; // @[Top.scala 299:18]
  assign n97_I0 = n87_O; // @[Top.scala 297:12]
  assign n97_I1 = n78_O; // @[Top.scala 298:12]
  assign n98_valid_up = n96_valid_down & n97_valid_down; // @[Top.scala 303:18]
  assign n98_I0_t0b = n96_O_t0b; // @[Top.scala 301:12]
  assign n98_I0_t1b = n96_O_t1b; // @[Top.scala 301:12]
  assign n98_I1_t0b = n97_O_t0b; // @[Top.scala 302:12]
  assign n98_I1_t1b = n97_O_t1b; // @[Top.scala 302:12]
  assign n99_clock = clock;
  assign n99_reset = reset;
  assign n99_valid_up = n98_valid_down; // @[Top.scala 306:18]
  assign n99_I_t0b_t0b = n98_O_t0b_t0b; // @[Top.scala 305:11]
  assign n99_I_t0b_t1b = n98_O_t0b_t1b; // @[Top.scala 305:11]
  assign n99_I_t1b_t0b = n98_O_t1b_t0b; // @[Top.scala 305:11]
  assign n99_I_t1b_t1b = n98_O_t1b_t1b; // @[Top.scala 305:11]
  assign n100_valid_up = n92_valid_down & n99_valid_down; // @[Top.scala 310:19]
  assign n100_I0 = n92_O[0]; // @[Top.scala 308:13]
  assign n100_I1_t0b_t0b = n99_O_t0b_t0b; // @[Top.scala 309:13]
  assign n100_I1_t0b_t1b = n99_O_t0b_t1b; // @[Top.scala 309:13]
  assign n100_I1_t1b_t0b = n99_O_t1b_t0b; // @[Top.scala 309:13]
  assign n100_I1_t1b_t1b = n99_O_t1b_t1b; // @[Top.scala 309:13]
  assign n101_valid_up = n100_valid_down; // @[Top.scala 313:19]
  assign n101_I_t0b = n100_O_t0b; // @[Top.scala 312:12]
  assign n101_I_t1b_t0b_t0b = n100_O_t1b_t0b_t0b; // @[Top.scala 312:12]
  assign n101_I_t1b_t0b_t1b = n100_O_t1b_t0b_t1b; // @[Top.scala 312:12]
  assign n101_I_t1b_t1b_t0b = n100_O_t1b_t1b_t0b; // @[Top.scala 312:12]
  assign n101_I_t1b_t1b_t1b = n100_O_t1b_t1b_t1b; // @[Top.scala 312:12]
  assign n103_valid_up = n102_valid_down & n101_valid_down; // @[Top.scala 317:19]
  assign n103_I0 = n102_O; // @[Top.scala 315:13]
  assign n103_I1_t0b = n101_O_t0b; // @[Top.scala 316:13]
  assign n103_I1_t1b = n101_O_t1b; // @[Top.scala 316:13]
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
module InitialDelayCounter_9(
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
  wire  n106_valid_up; // @[Top.scala 325:22]
  wire  n106_valid_down; // @[Top.scala 325:22]
  wire [15:0] n106_I_t0b; // @[Top.scala 325:22]
  wire [15:0] n106_O; // @[Top.scala 325:22]
  wire  n134_clock; // @[Top.scala 328:22]
  wire  n134_reset; // @[Top.scala 328:22]
  wire  n134_valid_up; // @[Top.scala 328:22]
  wire  n134_valid_down; // @[Top.scala 328:22]
  wire [15:0] n134_I; // @[Top.scala 328:22]
  wire [15:0] n134_O; // @[Top.scala 328:22]
  wire  n122_clock; // @[Top.scala 331:22]
  wire  n122_reset; // @[Top.scala 331:22]
  wire  n122_valid_up; // @[Top.scala 331:22]
  wire  n122_valid_down; // @[Top.scala 331:22]
  wire [15:0] n122_I; // @[Top.scala 331:22]
  wire [15:0] n122_O; // @[Top.scala 331:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n108_valid_up; // @[Top.scala 335:22]
  wire  n108_valid_down; // @[Top.scala 335:22]
  wire [15:0] n108_I_t1b_t0b; // @[Top.scala 335:22]
  wire [15:0] n108_I_t1b_t1b; // @[Top.scala 335:22]
  wire [15:0] n108_O_t0b; // @[Top.scala 335:22]
  wire [15:0] n108_O_t1b; // @[Top.scala 335:22]
  wire  n109_valid_up; // @[Top.scala 338:22]
  wire  n109_valid_down; // @[Top.scala 338:22]
  wire [15:0] n109_I_t0b; // @[Top.scala 338:22]
  wire [15:0] n109_O; // @[Top.scala 338:22]
  wire  n110_valid_up; // @[Top.scala 341:22]
  wire  n110_valid_down; // @[Top.scala 341:22]
  wire [15:0] n110_I_t1b; // @[Top.scala 341:22]
  wire [15:0] n110_O; // @[Top.scala 341:22]
  wire  n111_valid_up; // @[Top.scala 344:22]
  wire  n111_valid_down; // @[Top.scala 344:22]
  wire [15:0] n111_I0; // @[Top.scala 344:22]
  wire [15:0] n111_I1; // @[Top.scala 344:22]
  wire [15:0] n111_O_t0b; // @[Top.scala 344:22]
  wire [15:0] n111_O_t1b; // @[Top.scala 344:22]
  wire  n112_valid_up; // @[Top.scala 348:22]
  wire  n112_valid_down; // @[Top.scala 348:22]
  wire [15:0] n112_I_t0b; // @[Top.scala 348:22]
  wire [15:0] n112_I_t1b; // @[Top.scala 348:22]
  wire [15:0] n112_O; // @[Top.scala 348:22]
  wire  n114_valid_up; // @[Top.scala 351:22]
  wire  n114_valid_down; // @[Top.scala 351:22]
  wire [15:0] n114_I0; // @[Top.scala 351:22]
  wire [15:0] n114_I1; // @[Top.scala 351:22]
  wire [15:0] n114_O_t0b; // @[Top.scala 351:22]
  wire [15:0] n114_O_t1b; // @[Top.scala 351:22]
  wire  n115_valid_up; // @[Top.scala 355:22]
  wire  n115_valid_down; // @[Top.scala 355:22]
  wire [15:0] n115_I_t0b; // @[Top.scala 355:22]
  wire [15:0] n115_I_t1b; // @[Top.scala 355:22]
  wire [15:0] n115_O; // @[Top.scala 355:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n118_valid_up; // @[Top.scala 359:22]
  wire  n118_valid_down; // @[Top.scala 359:22]
  wire [15:0] n118_I0; // @[Top.scala 359:22]
  wire [15:0] n118_O_t0b; // @[Top.scala 359:22]
  wire  n119_valid_up; // @[Top.scala 363:22]
  wire  n119_valid_down; // @[Top.scala 363:22]
  wire [15:0] n119_I_t0b; // @[Top.scala 363:22]
  wire [15:0] n119_O; // @[Top.scala 363:22]
  wire  n120_valid_up; // @[Top.scala 366:22]
  wire  n120_valid_down; // @[Top.scala 366:22]
  wire [15:0] n120_I0; // @[Top.scala 366:22]
  wire [15:0] n120_I1; // @[Top.scala 366:22]
  wire [15:0] n120_O_t0b; // @[Top.scala 366:22]
  wire [15:0] n120_O_t1b; // @[Top.scala 366:22]
  wire  n121_clock; // @[Top.scala 370:22]
  wire  n121_reset; // @[Top.scala 370:22]
  wire  n121_valid_up; // @[Top.scala 370:22]
  wire  n121_valid_down; // @[Top.scala 370:22]
  wire [15:0] n121_I_t0b; // @[Top.scala 370:22]
  wire [15:0] n121_I_t1b; // @[Top.scala 370:22]
  wire [15:0] n121_O; // @[Top.scala 370:22]
  wire  n123_valid_up; // @[Top.scala 373:22]
  wire  n123_valid_down; // @[Top.scala 373:22]
  wire [15:0] n123_I0; // @[Top.scala 373:22]
  wire [15:0] n123_I1; // @[Top.scala 373:22]
  wire [15:0] n123_O_t0b; // @[Top.scala 373:22]
  wire [15:0] n123_O_t1b; // @[Top.scala 373:22]
  wire  n124_valid_up; // @[Top.scala 377:22]
  wire  n124_valid_down; // @[Top.scala 377:22]
  wire [15:0] n124_I_t0b; // @[Top.scala 377:22]
  wire [15:0] n124_I_t1b; // @[Top.scala 377:22]
  wire [15:0] n124_O; // @[Top.scala 377:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n126_valid_up; // @[Top.scala 381:22]
  wire  n126_valid_down; // @[Top.scala 381:22]
  wire [15:0] n126_I0; // @[Top.scala 381:22]
  wire [15:0] n126_I1; // @[Top.scala 381:22]
  wire [15:0] n126_O_t0b; // @[Top.scala 381:22]
  wire [15:0] n126_O_t1b; // @[Top.scala 381:22]
  wire  n127_valid_up; // @[Top.scala 385:22]
  wire  n127_valid_down; // @[Top.scala 385:22]
  wire [15:0] n127_I_t0b; // @[Top.scala 385:22]
  wire [15:0] n127_I_t1b; // @[Top.scala 385:22]
  wire [15:0] n127_O; // @[Top.scala 385:22]
  wire  n128_valid_up; // @[Top.scala 388:22]
  wire  n128_valid_down; // @[Top.scala 388:22]
  wire [15:0] n128_I0; // @[Top.scala 388:22]
  wire [15:0] n128_I1; // @[Top.scala 388:22]
  wire [15:0] n128_O_t0b; // @[Top.scala 388:22]
  wire [15:0] n128_O_t1b; // @[Top.scala 388:22]
  wire  n129_valid_up; // @[Top.scala 392:22]
  wire  n129_valid_down; // @[Top.scala 392:22]
  wire [15:0] n129_I0; // @[Top.scala 392:22]
  wire [15:0] n129_I1; // @[Top.scala 392:22]
  wire [15:0] n129_O_t0b; // @[Top.scala 392:22]
  wire [15:0] n129_O_t1b; // @[Top.scala 392:22]
  wire  n130_valid_up; // @[Top.scala 396:22]
  wire  n130_valid_down; // @[Top.scala 396:22]
  wire [15:0] n130_I0_t0b; // @[Top.scala 396:22]
  wire [15:0] n130_I0_t1b; // @[Top.scala 396:22]
  wire [15:0] n130_I1_t0b; // @[Top.scala 396:22]
  wire [15:0] n130_I1_t1b; // @[Top.scala 396:22]
  wire [15:0] n130_O_t0b_t0b; // @[Top.scala 396:22]
  wire [15:0] n130_O_t0b_t1b; // @[Top.scala 396:22]
  wire [15:0] n130_O_t1b_t0b; // @[Top.scala 396:22]
  wire [15:0] n130_O_t1b_t1b; // @[Top.scala 396:22]
  wire  n131_clock; // @[Top.scala 400:22]
  wire  n131_reset; // @[Top.scala 400:22]
  wire  n131_valid_up; // @[Top.scala 400:22]
  wire  n131_valid_down; // @[Top.scala 400:22]
  wire [15:0] n131_I_t0b_t0b; // @[Top.scala 400:22]
  wire [15:0] n131_I_t0b_t1b; // @[Top.scala 400:22]
  wire [15:0] n131_I_t1b_t0b; // @[Top.scala 400:22]
  wire [15:0] n131_I_t1b_t1b; // @[Top.scala 400:22]
  wire [15:0] n131_O_t0b_t0b; // @[Top.scala 400:22]
  wire [15:0] n131_O_t0b_t1b; // @[Top.scala 400:22]
  wire [15:0] n131_O_t1b_t0b; // @[Top.scala 400:22]
  wire [15:0] n131_O_t1b_t1b; // @[Top.scala 400:22]
  wire  n132_valid_up; // @[Top.scala 403:22]
  wire  n132_valid_down; // @[Top.scala 403:22]
  wire  n132_I0; // @[Top.scala 403:22]
  wire [15:0] n132_I1_t0b_t0b; // @[Top.scala 403:22]
  wire [15:0] n132_I1_t0b_t1b; // @[Top.scala 403:22]
  wire [15:0] n132_I1_t1b_t0b; // @[Top.scala 403:22]
  wire [15:0] n132_I1_t1b_t1b; // @[Top.scala 403:22]
  wire  n132_O_t0b; // @[Top.scala 403:22]
  wire [15:0] n132_O_t1b_t0b_t0b; // @[Top.scala 403:22]
  wire [15:0] n132_O_t1b_t0b_t1b; // @[Top.scala 403:22]
  wire [15:0] n132_O_t1b_t1b_t0b; // @[Top.scala 403:22]
  wire [15:0] n132_O_t1b_t1b_t1b; // @[Top.scala 403:22]
  wire  n133_valid_up; // @[Top.scala 407:22]
  wire  n133_valid_down; // @[Top.scala 407:22]
  wire  n133_I_t0b; // @[Top.scala 407:22]
  wire [15:0] n133_I_t1b_t0b_t0b; // @[Top.scala 407:22]
  wire [15:0] n133_I_t1b_t0b_t1b; // @[Top.scala 407:22]
  wire [15:0] n133_I_t1b_t1b_t0b; // @[Top.scala 407:22]
  wire [15:0] n133_I_t1b_t1b_t1b; // @[Top.scala 407:22]
  wire [15:0] n133_O_t0b; // @[Top.scala 407:22]
  wire [15:0] n133_O_t1b; // @[Top.scala 407:22]
  wire  n135_valid_up; // @[Top.scala 410:22]
  wire  n135_valid_down; // @[Top.scala 410:22]
  wire [15:0] n135_I0; // @[Top.scala 410:22]
  wire [15:0] n135_I1_t0b; // @[Top.scala 410:22]
  wire [15:0] n135_I1_t1b; // @[Top.scala 410:22]
  wire [15:0] n135_O_t0b; // @[Top.scala 410:22]
  wire [15:0] n135_O_t1b_t0b; // @[Top.scala 410:22]
  wire [15:0] n135_O_t1b_t1b; // @[Top.scala 410:22]
  Fst n106 ( // @[Top.scala 325:22]
    .valid_up(n106_valid_up),
    .valid_down(n106_valid_down),
    .I_t0b(n106_I_t0b),
    .O(n106_O)
  );
  FIFO_2 n134 ( // @[Top.scala 328:22]
    .clock(n134_clock),
    .reset(n134_reset),
    .valid_up(n134_valid_up),
    .valid_down(n134_valid_down),
    .I(n134_I),
    .O(n134_O)
  );
  FIFO_2 n122 ( // @[Top.scala 331:22]
    .clock(n122_clock),
    .reset(n122_reset),
    .valid_up(n122_valid_up),
    .valid_down(n122_valid_down),
    .I(n122_I),
    .O(n122_O)
  );
  InitialDelayCounter_9 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n108 ( // @[Top.scala 335:22]
    .valid_up(n108_valid_up),
    .valid_down(n108_valid_down),
    .I_t1b_t0b(n108_I_t1b_t0b),
    .I_t1b_t1b(n108_I_t1b_t1b),
    .O_t0b(n108_O_t0b),
    .O_t1b(n108_O_t1b)
  );
  Fst_1 n109 ( // @[Top.scala 338:22]
    .valid_up(n109_valid_up),
    .valid_down(n109_valid_down),
    .I_t0b(n109_I_t0b),
    .O(n109_O)
  );
  Snd_1 n110 ( // @[Top.scala 341:22]
    .valid_up(n110_valid_up),
    .valid_down(n110_valid_down),
    .I_t1b(n110_I_t1b),
    .O(n110_O)
  );
  AtomTuple n111 ( // @[Top.scala 344:22]
    .valid_up(n111_valid_up),
    .valid_down(n111_valid_down),
    .I0(n111_I0),
    .I1(n111_I1),
    .O_t0b(n111_O_t0b),
    .O_t1b(n111_O_t1b)
  );
  Add n112 ( // @[Top.scala 348:22]
    .valid_up(n112_valid_up),
    .valid_down(n112_valid_down),
    .I_t0b(n112_I_t0b),
    .I_t1b(n112_I_t1b),
    .O(n112_O)
  );
  AtomTuple n114 ( // @[Top.scala 351:22]
    .valid_up(n114_valid_up),
    .valid_down(n114_valid_down),
    .I0(n114_I0),
    .I1(n114_I1),
    .O_t0b(n114_O_t0b),
    .O_t1b(n114_O_t1b)
  );
  Add n115 ( // @[Top.scala 355:22]
    .valid_up(n115_valid_up),
    .valid_down(n115_valid_down),
    .I_t0b(n115_I_t0b),
    .I_t1b(n115_I_t1b),
    .O(n115_O)
  );
  InitialDelayCounter_9 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n118 ( // @[Top.scala 359:22]
    .valid_up(n118_valid_up),
    .valid_down(n118_valid_down),
    .I0(n118_I0),
    .O_t0b(n118_O_t0b)
  );
  RShift n119 ( // @[Top.scala 363:22]
    .valid_up(n119_valid_up),
    .valid_down(n119_valid_down),
    .I_t0b(n119_I_t0b),
    .O(n119_O)
  );
  AtomTuple n120 ( // @[Top.scala 366:22]
    .valid_up(n120_valid_up),
    .valid_down(n120_valid_down),
    .I0(n120_I0),
    .I1(n120_I1),
    .O_t0b(n120_O_t0b),
    .O_t1b(n120_O_t1b)
  );
  Mul n121 ( // @[Top.scala 370:22]
    .clock(n121_clock),
    .reset(n121_reset),
    .valid_up(n121_valid_up),
    .valid_down(n121_valid_down),
    .I_t0b(n121_I_t0b),
    .I_t1b(n121_I_t1b),
    .O(n121_O)
  );
  AtomTuple n123 ( // @[Top.scala 373:22]
    .valid_up(n123_valid_up),
    .valid_down(n123_valid_down),
    .I0(n123_I0),
    .I1(n123_I1),
    .O_t0b(n123_O_t0b),
    .O_t1b(n123_O_t1b)
  );
  Lt n124 ( // @[Top.scala 377:22]
    .valid_up(n124_valid_up),
    .valid_down(n124_valid_down),
    .I_t0b(n124_I_t0b),
    .I_t1b(n124_I_t1b),
    .O(n124_O)
  );
  InitialDelayCounter_9 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n126 ( // @[Top.scala 381:22]
    .valid_up(n126_valid_up),
    .valid_down(n126_valid_down),
    .I0(n126_I0),
    .I1(n126_I1),
    .O_t0b(n126_O_t0b),
    .O_t1b(n126_O_t1b)
  );
  Sub n127 ( // @[Top.scala 385:22]
    .valid_up(n127_valid_up),
    .valid_down(n127_valid_down),
    .I_t0b(n127_I_t0b),
    .I_t1b(n127_I_t1b),
    .O(n127_O)
  );
  AtomTuple n128 ( // @[Top.scala 388:22]
    .valid_up(n128_valid_up),
    .valid_down(n128_valid_down),
    .I0(n128_I0),
    .I1(n128_I1),
    .O_t0b(n128_O_t0b),
    .O_t1b(n128_O_t1b)
  );
  AtomTuple n129 ( // @[Top.scala 392:22]
    .valid_up(n129_valid_up),
    .valid_down(n129_valid_down),
    .I0(n129_I0),
    .I1(n129_I1),
    .O_t0b(n129_O_t0b),
    .O_t1b(n129_O_t1b)
  );
  AtomTuple_10 n130 ( // @[Top.scala 396:22]
    .valid_up(n130_valid_up),
    .valid_down(n130_valid_down),
    .I0_t0b(n130_I0_t0b),
    .I0_t1b(n130_I0_t1b),
    .I1_t0b(n130_I1_t0b),
    .I1_t1b(n130_I1_t1b),
    .O_t0b_t0b(n130_O_t0b_t0b),
    .O_t0b_t1b(n130_O_t0b_t1b),
    .O_t1b_t0b(n130_O_t1b_t0b),
    .O_t1b_t1b(n130_O_t1b_t1b)
  );
  FIFO_4 n131 ( // @[Top.scala 400:22]
    .clock(n131_clock),
    .reset(n131_reset),
    .valid_up(n131_valid_up),
    .valid_down(n131_valid_down),
    .I_t0b_t0b(n131_I_t0b_t0b),
    .I_t0b_t1b(n131_I_t0b_t1b),
    .I_t1b_t0b(n131_I_t1b_t0b),
    .I_t1b_t1b(n131_I_t1b_t1b),
    .O_t0b_t0b(n131_O_t0b_t0b),
    .O_t0b_t1b(n131_O_t0b_t1b),
    .O_t1b_t0b(n131_O_t1b_t0b),
    .O_t1b_t1b(n131_O_t1b_t1b)
  );
  AtomTuple_11 n132 ( // @[Top.scala 403:22]
    .valid_up(n132_valid_up),
    .valid_down(n132_valid_down),
    .I0(n132_I0),
    .I1_t0b_t0b(n132_I1_t0b_t0b),
    .I1_t0b_t1b(n132_I1_t0b_t1b),
    .I1_t1b_t0b(n132_I1_t1b_t0b),
    .I1_t1b_t1b(n132_I1_t1b_t1b),
    .O_t0b(n132_O_t0b),
    .O_t1b_t0b_t0b(n132_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n132_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n132_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n132_O_t1b_t1b_t1b)
  );
  If n133 ( // @[Top.scala 407:22]
    .valid_up(n133_valid_up),
    .valid_down(n133_valid_down),
    .I_t0b(n133_I_t0b),
    .I_t1b_t0b_t0b(n133_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n133_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n133_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n133_I_t1b_t1b_t1b),
    .O_t0b(n133_O_t0b),
    .O_t1b(n133_O_t1b)
  );
  AtomTuple_1 n135 ( // @[Top.scala 410:22]
    .valid_up(n135_valid_up),
    .valid_down(n135_valid_down),
    .I0(n135_I0),
    .I1_t0b(n135_I1_t0b),
    .I1_t1b(n135_I1_t1b),
    .O_t0b(n135_O_t0b),
    .O_t1b_t0b(n135_O_t1b_t0b),
    .O_t1b_t1b(n135_O_t1b_t1b)
  );
  assign valid_down = n135_valid_down; // @[Top.scala 415:16]
  assign O_t0b = n135_O_t0b; // @[Top.scala 414:7]
  assign O_t1b_t0b = n135_O_t1b_t0b; // @[Top.scala 414:7]
  assign O_t1b_t1b = n135_O_t1b_t1b; // @[Top.scala 414:7]
  assign n106_valid_up = valid_up; // @[Top.scala 327:19]
  assign n106_I_t0b = I_t0b; // @[Top.scala 326:12]
  assign n134_clock = clock;
  assign n134_reset = reset;
  assign n134_valid_up = n106_valid_down; // @[Top.scala 330:19]
  assign n134_I = n106_O; // @[Top.scala 329:12]
  assign n122_clock = clock;
  assign n122_reset = reset;
  assign n122_valid_up = n106_valid_down; // @[Top.scala 333:19]
  assign n122_I = n106_O; // @[Top.scala 332:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n108_valid_up = valid_up; // @[Top.scala 337:19]
  assign n108_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 336:12]
  assign n108_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 336:12]
  assign n109_valid_up = n108_valid_down; // @[Top.scala 340:19]
  assign n109_I_t0b = n108_O_t0b; // @[Top.scala 339:12]
  assign n110_valid_up = n108_valid_down; // @[Top.scala 343:19]
  assign n110_I_t1b = n108_O_t1b; // @[Top.scala 342:12]
  assign n111_valid_up = n109_valid_down & n110_valid_down; // @[Top.scala 347:19]
  assign n111_I0 = n109_O; // @[Top.scala 345:13]
  assign n111_I1 = n110_O; // @[Top.scala 346:13]
  assign n112_valid_up = n111_valid_down; // @[Top.scala 350:19]
  assign n112_I_t0b = n111_O_t0b; // @[Top.scala 349:12]
  assign n112_I_t1b = n111_O_t1b; // @[Top.scala 349:12]
  assign n114_valid_up = InitialDelayCounter_valid_down & n112_valid_down; // @[Top.scala 354:19]
  assign n114_I0 = 16'h1; // @[Top.scala 352:13]
  assign n114_I1 = n112_O; // @[Top.scala 353:13]
  assign n115_valid_up = n114_valid_down; // @[Top.scala 357:19]
  assign n115_I_t0b = n114_O_t0b; // @[Top.scala 356:12]
  assign n115_I_t1b = n114_O_t1b; // @[Top.scala 356:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n118_valid_up = n115_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 362:19]
  assign n118_I0 = n115_O; // @[Top.scala 360:13]
  assign n119_valid_up = n118_valid_down; // @[Top.scala 365:19]
  assign n119_I_t0b = n118_O_t0b; // @[Top.scala 364:12]
  assign n120_valid_up = n119_valid_down; // @[Top.scala 369:19]
  assign n120_I0 = n119_O; // @[Top.scala 367:13]
  assign n120_I1 = n119_O; // @[Top.scala 368:13]
  assign n121_clock = clock;
  assign n121_reset = reset;
  assign n121_valid_up = n120_valid_down; // @[Top.scala 372:19]
  assign n121_I_t0b = n120_O_t0b; // @[Top.scala 371:12]
  assign n121_I_t1b = n120_O_t1b; // @[Top.scala 371:12]
  assign n123_valid_up = n122_valid_down & n121_valid_down; // @[Top.scala 376:19]
  assign n123_I0 = n122_O; // @[Top.scala 374:13]
  assign n123_I1 = n121_O; // @[Top.scala 375:13]
  assign n124_valid_up = n123_valid_down; // @[Top.scala 379:19]
  assign n124_I_t0b = n123_O_t0b; // @[Top.scala 378:12]
  assign n124_I_t1b = n123_O_t1b; // @[Top.scala 378:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n126_valid_up = n119_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 384:19]
  assign n126_I0 = n119_O; // @[Top.scala 382:13]
  assign n126_I1 = 16'h1; // @[Top.scala 383:13]
  assign n127_valid_up = n126_valid_down; // @[Top.scala 387:19]
  assign n127_I_t0b = n126_O_t0b; // @[Top.scala 386:12]
  assign n127_I_t1b = n126_O_t1b; // @[Top.scala 386:12]
  assign n128_valid_up = n109_valid_down & n127_valid_down; // @[Top.scala 391:19]
  assign n128_I0 = n109_O; // @[Top.scala 389:13]
  assign n128_I1 = n127_O; // @[Top.scala 390:13]
  assign n129_valid_up = n119_valid_down & n110_valid_down; // @[Top.scala 395:19]
  assign n129_I0 = n119_O; // @[Top.scala 393:13]
  assign n129_I1 = n110_O; // @[Top.scala 394:13]
  assign n130_valid_up = n128_valid_down & n129_valid_down; // @[Top.scala 399:19]
  assign n130_I0_t0b = n128_O_t0b; // @[Top.scala 397:13]
  assign n130_I0_t1b = n128_O_t1b; // @[Top.scala 397:13]
  assign n130_I1_t0b = n129_O_t0b; // @[Top.scala 398:13]
  assign n130_I1_t1b = n129_O_t1b; // @[Top.scala 398:13]
  assign n131_clock = clock;
  assign n131_reset = reset;
  assign n131_valid_up = n130_valid_down; // @[Top.scala 402:19]
  assign n131_I_t0b_t0b = n130_O_t0b_t0b; // @[Top.scala 401:12]
  assign n131_I_t0b_t1b = n130_O_t0b_t1b; // @[Top.scala 401:12]
  assign n131_I_t1b_t0b = n130_O_t1b_t0b; // @[Top.scala 401:12]
  assign n131_I_t1b_t1b = n130_O_t1b_t1b; // @[Top.scala 401:12]
  assign n132_valid_up = n124_valid_down & n131_valid_down; // @[Top.scala 406:19]
  assign n132_I0 = n124_O[0]; // @[Top.scala 404:13]
  assign n132_I1_t0b_t0b = n131_O_t0b_t0b; // @[Top.scala 405:13]
  assign n132_I1_t0b_t1b = n131_O_t0b_t1b; // @[Top.scala 405:13]
  assign n132_I1_t1b_t0b = n131_O_t1b_t0b; // @[Top.scala 405:13]
  assign n132_I1_t1b_t1b = n131_O_t1b_t1b; // @[Top.scala 405:13]
  assign n133_valid_up = n132_valid_down; // @[Top.scala 409:19]
  assign n133_I_t0b = n132_O_t0b; // @[Top.scala 408:12]
  assign n133_I_t1b_t0b_t0b = n132_O_t1b_t0b_t0b; // @[Top.scala 408:12]
  assign n133_I_t1b_t0b_t1b = n132_O_t1b_t0b_t1b; // @[Top.scala 408:12]
  assign n133_I_t1b_t1b_t0b = n132_O_t1b_t1b_t0b; // @[Top.scala 408:12]
  assign n133_I_t1b_t1b_t1b = n132_O_t1b_t1b_t1b; // @[Top.scala 408:12]
  assign n135_valid_up = n134_valid_down & n133_valid_down; // @[Top.scala 413:19]
  assign n135_I0 = n134_O; // @[Top.scala 411:13]
  assign n135_I1_t0b = n133_O_t0b; // @[Top.scala 412:13]
  assign n135_I1_t1b = n133_O_t1b; // @[Top.scala 412:13]
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
module InitialDelayCounter_12(
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
  wire  n138_valid_up; // @[Top.scala 421:22]
  wire  n138_valid_down; // @[Top.scala 421:22]
  wire [15:0] n138_I_t0b; // @[Top.scala 421:22]
  wire [15:0] n138_O; // @[Top.scala 421:22]
  wire  n166_clock; // @[Top.scala 424:22]
  wire  n166_reset; // @[Top.scala 424:22]
  wire  n166_valid_up; // @[Top.scala 424:22]
  wire  n166_valid_down; // @[Top.scala 424:22]
  wire [15:0] n166_I; // @[Top.scala 424:22]
  wire [15:0] n166_O; // @[Top.scala 424:22]
  wire  n154_clock; // @[Top.scala 427:22]
  wire  n154_reset; // @[Top.scala 427:22]
  wire  n154_valid_up; // @[Top.scala 427:22]
  wire  n154_valid_down; // @[Top.scala 427:22]
  wire [15:0] n154_I; // @[Top.scala 427:22]
  wire [15:0] n154_O; // @[Top.scala 427:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n140_valid_up; // @[Top.scala 431:22]
  wire  n140_valid_down; // @[Top.scala 431:22]
  wire [15:0] n140_I_t1b_t0b; // @[Top.scala 431:22]
  wire [15:0] n140_I_t1b_t1b; // @[Top.scala 431:22]
  wire [15:0] n140_O_t0b; // @[Top.scala 431:22]
  wire [15:0] n140_O_t1b; // @[Top.scala 431:22]
  wire  n141_valid_up; // @[Top.scala 434:22]
  wire  n141_valid_down; // @[Top.scala 434:22]
  wire [15:0] n141_I_t0b; // @[Top.scala 434:22]
  wire [15:0] n141_O; // @[Top.scala 434:22]
  wire  n142_valid_up; // @[Top.scala 437:22]
  wire  n142_valid_down; // @[Top.scala 437:22]
  wire [15:0] n142_I_t1b; // @[Top.scala 437:22]
  wire [15:0] n142_O; // @[Top.scala 437:22]
  wire  n143_valid_up; // @[Top.scala 440:22]
  wire  n143_valid_down; // @[Top.scala 440:22]
  wire [15:0] n143_I0; // @[Top.scala 440:22]
  wire [15:0] n143_I1; // @[Top.scala 440:22]
  wire [15:0] n143_O_t0b; // @[Top.scala 440:22]
  wire [15:0] n143_O_t1b; // @[Top.scala 440:22]
  wire  n144_valid_up; // @[Top.scala 444:22]
  wire  n144_valid_down; // @[Top.scala 444:22]
  wire [15:0] n144_I_t0b; // @[Top.scala 444:22]
  wire [15:0] n144_I_t1b; // @[Top.scala 444:22]
  wire [15:0] n144_O; // @[Top.scala 444:22]
  wire  n146_valid_up; // @[Top.scala 447:22]
  wire  n146_valid_down; // @[Top.scala 447:22]
  wire [15:0] n146_I0; // @[Top.scala 447:22]
  wire [15:0] n146_I1; // @[Top.scala 447:22]
  wire [15:0] n146_O_t0b; // @[Top.scala 447:22]
  wire [15:0] n146_O_t1b; // @[Top.scala 447:22]
  wire  n147_valid_up; // @[Top.scala 451:22]
  wire  n147_valid_down; // @[Top.scala 451:22]
  wire [15:0] n147_I_t0b; // @[Top.scala 451:22]
  wire [15:0] n147_I_t1b; // @[Top.scala 451:22]
  wire [15:0] n147_O; // @[Top.scala 451:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n150_valid_up; // @[Top.scala 455:22]
  wire  n150_valid_down; // @[Top.scala 455:22]
  wire [15:0] n150_I0; // @[Top.scala 455:22]
  wire [15:0] n150_O_t0b; // @[Top.scala 455:22]
  wire  n151_valid_up; // @[Top.scala 459:22]
  wire  n151_valid_down; // @[Top.scala 459:22]
  wire [15:0] n151_I_t0b; // @[Top.scala 459:22]
  wire [15:0] n151_O; // @[Top.scala 459:22]
  wire  n152_valid_up; // @[Top.scala 462:22]
  wire  n152_valid_down; // @[Top.scala 462:22]
  wire [15:0] n152_I0; // @[Top.scala 462:22]
  wire [15:0] n152_I1; // @[Top.scala 462:22]
  wire [15:0] n152_O_t0b; // @[Top.scala 462:22]
  wire [15:0] n152_O_t1b; // @[Top.scala 462:22]
  wire  n153_clock; // @[Top.scala 466:22]
  wire  n153_reset; // @[Top.scala 466:22]
  wire  n153_valid_up; // @[Top.scala 466:22]
  wire  n153_valid_down; // @[Top.scala 466:22]
  wire [15:0] n153_I_t0b; // @[Top.scala 466:22]
  wire [15:0] n153_I_t1b; // @[Top.scala 466:22]
  wire [15:0] n153_O; // @[Top.scala 466:22]
  wire  n155_valid_up; // @[Top.scala 469:22]
  wire  n155_valid_down; // @[Top.scala 469:22]
  wire [15:0] n155_I0; // @[Top.scala 469:22]
  wire [15:0] n155_I1; // @[Top.scala 469:22]
  wire [15:0] n155_O_t0b; // @[Top.scala 469:22]
  wire [15:0] n155_O_t1b; // @[Top.scala 469:22]
  wire  n156_valid_up; // @[Top.scala 473:22]
  wire  n156_valid_down; // @[Top.scala 473:22]
  wire [15:0] n156_I_t0b; // @[Top.scala 473:22]
  wire [15:0] n156_I_t1b; // @[Top.scala 473:22]
  wire [15:0] n156_O; // @[Top.scala 473:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n158_valid_up; // @[Top.scala 477:22]
  wire  n158_valid_down; // @[Top.scala 477:22]
  wire [15:0] n158_I0; // @[Top.scala 477:22]
  wire [15:0] n158_I1; // @[Top.scala 477:22]
  wire [15:0] n158_O_t0b; // @[Top.scala 477:22]
  wire [15:0] n158_O_t1b; // @[Top.scala 477:22]
  wire  n159_valid_up; // @[Top.scala 481:22]
  wire  n159_valid_down; // @[Top.scala 481:22]
  wire [15:0] n159_I_t0b; // @[Top.scala 481:22]
  wire [15:0] n159_I_t1b; // @[Top.scala 481:22]
  wire [15:0] n159_O; // @[Top.scala 481:22]
  wire  n160_valid_up; // @[Top.scala 484:22]
  wire  n160_valid_down; // @[Top.scala 484:22]
  wire [15:0] n160_I0; // @[Top.scala 484:22]
  wire [15:0] n160_I1; // @[Top.scala 484:22]
  wire [15:0] n160_O_t0b; // @[Top.scala 484:22]
  wire [15:0] n160_O_t1b; // @[Top.scala 484:22]
  wire  n161_valid_up; // @[Top.scala 488:22]
  wire  n161_valid_down; // @[Top.scala 488:22]
  wire [15:0] n161_I0; // @[Top.scala 488:22]
  wire [15:0] n161_I1; // @[Top.scala 488:22]
  wire [15:0] n161_O_t0b; // @[Top.scala 488:22]
  wire [15:0] n161_O_t1b; // @[Top.scala 488:22]
  wire  n162_valid_up; // @[Top.scala 492:22]
  wire  n162_valid_down; // @[Top.scala 492:22]
  wire [15:0] n162_I0_t0b; // @[Top.scala 492:22]
  wire [15:0] n162_I0_t1b; // @[Top.scala 492:22]
  wire [15:0] n162_I1_t0b; // @[Top.scala 492:22]
  wire [15:0] n162_I1_t1b; // @[Top.scala 492:22]
  wire [15:0] n162_O_t0b_t0b; // @[Top.scala 492:22]
  wire [15:0] n162_O_t0b_t1b; // @[Top.scala 492:22]
  wire [15:0] n162_O_t1b_t0b; // @[Top.scala 492:22]
  wire [15:0] n162_O_t1b_t1b; // @[Top.scala 492:22]
  wire  n163_clock; // @[Top.scala 496:22]
  wire  n163_reset; // @[Top.scala 496:22]
  wire  n163_valid_up; // @[Top.scala 496:22]
  wire  n163_valid_down; // @[Top.scala 496:22]
  wire [15:0] n163_I_t0b_t0b; // @[Top.scala 496:22]
  wire [15:0] n163_I_t0b_t1b; // @[Top.scala 496:22]
  wire [15:0] n163_I_t1b_t0b; // @[Top.scala 496:22]
  wire [15:0] n163_I_t1b_t1b; // @[Top.scala 496:22]
  wire [15:0] n163_O_t0b_t0b; // @[Top.scala 496:22]
  wire [15:0] n163_O_t0b_t1b; // @[Top.scala 496:22]
  wire [15:0] n163_O_t1b_t0b; // @[Top.scala 496:22]
  wire [15:0] n163_O_t1b_t1b; // @[Top.scala 496:22]
  wire  n164_valid_up; // @[Top.scala 499:22]
  wire  n164_valid_down; // @[Top.scala 499:22]
  wire  n164_I0; // @[Top.scala 499:22]
  wire [15:0] n164_I1_t0b_t0b; // @[Top.scala 499:22]
  wire [15:0] n164_I1_t0b_t1b; // @[Top.scala 499:22]
  wire [15:0] n164_I1_t1b_t0b; // @[Top.scala 499:22]
  wire [15:0] n164_I1_t1b_t1b; // @[Top.scala 499:22]
  wire  n164_O_t0b; // @[Top.scala 499:22]
  wire [15:0] n164_O_t1b_t0b_t0b; // @[Top.scala 499:22]
  wire [15:0] n164_O_t1b_t0b_t1b; // @[Top.scala 499:22]
  wire [15:0] n164_O_t1b_t1b_t0b; // @[Top.scala 499:22]
  wire [15:0] n164_O_t1b_t1b_t1b; // @[Top.scala 499:22]
  wire  n165_valid_up; // @[Top.scala 503:22]
  wire  n165_valid_down; // @[Top.scala 503:22]
  wire  n165_I_t0b; // @[Top.scala 503:22]
  wire [15:0] n165_I_t1b_t0b_t0b; // @[Top.scala 503:22]
  wire [15:0] n165_I_t1b_t0b_t1b; // @[Top.scala 503:22]
  wire [15:0] n165_I_t1b_t1b_t0b; // @[Top.scala 503:22]
  wire [15:0] n165_I_t1b_t1b_t1b; // @[Top.scala 503:22]
  wire [15:0] n165_O_t0b; // @[Top.scala 503:22]
  wire [15:0] n165_O_t1b; // @[Top.scala 503:22]
  wire  n167_valid_up; // @[Top.scala 506:22]
  wire  n167_valid_down; // @[Top.scala 506:22]
  wire [15:0] n167_I0; // @[Top.scala 506:22]
  wire [15:0] n167_I1_t0b; // @[Top.scala 506:22]
  wire [15:0] n167_I1_t1b; // @[Top.scala 506:22]
  wire [15:0] n167_O_t0b; // @[Top.scala 506:22]
  wire [15:0] n167_O_t1b_t0b; // @[Top.scala 506:22]
  wire [15:0] n167_O_t1b_t1b; // @[Top.scala 506:22]
  Fst n138 ( // @[Top.scala 421:22]
    .valid_up(n138_valid_up),
    .valid_down(n138_valid_down),
    .I_t0b(n138_I_t0b),
    .O(n138_O)
  );
  FIFO_2 n166 ( // @[Top.scala 424:22]
    .clock(n166_clock),
    .reset(n166_reset),
    .valid_up(n166_valid_up),
    .valid_down(n166_valid_down),
    .I(n166_I),
    .O(n166_O)
  );
  FIFO_2 n154 ( // @[Top.scala 427:22]
    .clock(n154_clock),
    .reset(n154_reset),
    .valid_up(n154_valid_up),
    .valid_down(n154_valid_down),
    .I(n154_I),
    .O(n154_O)
  );
  InitialDelayCounter_12 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n140 ( // @[Top.scala 431:22]
    .valid_up(n140_valid_up),
    .valid_down(n140_valid_down),
    .I_t1b_t0b(n140_I_t1b_t0b),
    .I_t1b_t1b(n140_I_t1b_t1b),
    .O_t0b(n140_O_t0b),
    .O_t1b(n140_O_t1b)
  );
  Fst_1 n141 ( // @[Top.scala 434:22]
    .valid_up(n141_valid_up),
    .valid_down(n141_valid_down),
    .I_t0b(n141_I_t0b),
    .O(n141_O)
  );
  Snd_1 n142 ( // @[Top.scala 437:22]
    .valid_up(n142_valid_up),
    .valid_down(n142_valid_down),
    .I_t1b(n142_I_t1b),
    .O(n142_O)
  );
  AtomTuple n143 ( // @[Top.scala 440:22]
    .valid_up(n143_valid_up),
    .valid_down(n143_valid_down),
    .I0(n143_I0),
    .I1(n143_I1),
    .O_t0b(n143_O_t0b),
    .O_t1b(n143_O_t1b)
  );
  Add n144 ( // @[Top.scala 444:22]
    .valid_up(n144_valid_up),
    .valid_down(n144_valid_down),
    .I_t0b(n144_I_t0b),
    .I_t1b(n144_I_t1b),
    .O(n144_O)
  );
  AtomTuple n146 ( // @[Top.scala 447:22]
    .valid_up(n146_valid_up),
    .valid_down(n146_valid_down),
    .I0(n146_I0),
    .I1(n146_I1),
    .O_t0b(n146_O_t0b),
    .O_t1b(n146_O_t1b)
  );
  Add n147 ( // @[Top.scala 451:22]
    .valid_up(n147_valid_up),
    .valid_down(n147_valid_down),
    .I_t0b(n147_I_t0b),
    .I_t1b(n147_I_t1b),
    .O(n147_O)
  );
  InitialDelayCounter_12 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n150 ( // @[Top.scala 455:22]
    .valid_up(n150_valid_up),
    .valid_down(n150_valid_down),
    .I0(n150_I0),
    .O_t0b(n150_O_t0b)
  );
  RShift n151 ( // @[Top.scala 459:22]
    .valid_up(n151_valid_up),
    .valid_down(n151_valid_down),
    .I_t0b(n151_I_t0b),
    .O(n151_O)
  );
  AtomTuple n152 ( // @[Top.scala 462:22]
    .valid_up(n152_valid_up),
    .valid_down(n152_valid_down),
    .I0(n152_I0),
    .I1(n152_I1),
    .O_t0b(n152_O_t0b),
    .O_t1b(n152_O_t1b)
  );
  Mul n153 ( // @[Top.scala 466:22]
    .clock(n153_clock),
    .reset(n153_reset),
    .valid_up(n153_valid_up),
    .valid_down(n153_valid_down),
    .I_t0b(n153_I_t0b),
    .I_t1b(n153_I_t1b),
    .O(n153_O)
  );
  AtomTuple n155 ( // @[Top.scala 469:22]
    .valid_up(n155_valid_up),
    .valid_down(n155_valid_down),
    .I0(n155_I0),
    .I1(n155_I1),
    .O_t0b(n155_O_t0b),
    .O_t1b(n155_O_t1b)
  );
  Lt n156 ( // @[Top.scala 473:22]
    .valid_up(n156_valid_up),
    .valid_down(n156_valid_down),
    .I_t0b(n156_I_t0b),
    .I_t1b(n156_I_t1b),
    .O(n156_O)
  );
  InitialDelayCounter_12 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n158 ( // @[Top.scala 477:22]
    .valid_up(n158_valid_up),
    .valid_down(n158_valid_down),
    .I0(n158_I0),
    .I1(n158_I1),
    .O_t0b(n158_O_t0b),
    .O_t1b(n158_O_t1b)
  );
  Sub n159 ( // @[Top.scala 481:22]
    .valid_up(n159_valid_up),
    .valid_down(n159_valid_down),
    .I_t0b(n159_I_t0b),
    .I_t1b(n159_I_t1b),
    .O(n159_O)
  );
  AtomTuple n160 ( // @[Top.scala 484:22]
    .valid_up(n160_valid_up),
    .valid_down(n160_valid_down),
    .I0(n160_I0),
    .I1(n160_I1),
    .O_t0b(n160_O_t0b),
    .O_t1b(n160_O_t1b)
  );
  AtomTuple n161 ( // @[Top.scala 488:22]
    .valid_up(n161_valid_up),
    .valid_down(n161_valid_down),
    .I0(n161_I0),
    .I1(n161_I1),
    .O_t0b(n161_O_t0b),
    .O_t1b(n161_O_t1b)
  );
  AtomTuple_10 n162 ( // @[Top.scala 492:22]
    .valid_up(n162_valid_up),
    .valid_down(n162_valid_down),
    .I0_t0b(n162_I0_t0b),
    .I0_t1b(n162_I0_t1b),
    .I1_t0b(n162_I1_t0b),
    .I1_t1b(n162_I1_t1b),
    .O_t0b_t0b(n162_O_t0b_t0b),
    .O_t0b_t1b(n162_O_t0b_t1b),
    .O_t1b_t0b(n162_O_t1b_t0b),
    .O_t1b_t1b(n162_O_t1b_t1b)
  );
  FIFO_4 n163 ( // @[Top.scala 496:22]
    .clock(n163_clock),
    .reset(n163_reset),
    .valid_up(n163_valid_up),
    .valid_down(n163_valid_down),
    .I_t0b_t0b(n163_I_t0b_t0b),
    .I_t0b_t1b(n163_I_t0b_t1b),
    .I_t1b_t0b(n163_I_t1b_t0b),
    .I_t1b_t1b(n163_I_t1b_t1b),
    .O_t0b_t0b(n163_O_t0b_t0b),
    .O_t0b_t1b(n163_O_t0b_t1b),
    .O_t1b_t0b(n163_O_t1b_t0b),
    .O_t1b_t1b(n163_O_t1b_t1b)
  );
  AtomTuple_11 n164 ( // @[Top.scala 499:22]
    .valid_up(n164_valid_up),
    .valid_down(n164_valid_down),
    .I0(n164_I0),
    .I1_t0b_t0b(n164_I1_t0b_t0b),
    .I1_t0b_t1b(n164_I1_t0b_t1b),
    .I1_t1b_t0b(n164_I1_t1b_t0b),
    .I1_t1b_t1b(n164_I1_t1b_t1b),
    .O_t0b(n164_O_t0b),
    .O_t1b_t0b_t0b(n164_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n164_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n164_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n164_O_t1b_t1b_t1b)
  );
  If n165 ( // @[Top.scala 503:22]
    .valid_up(n165_valid_up),
    .valid_down(n165_valid_down),
    .I_t0b(n165_I_t0b),
    .I_t1b_t0b_t0b(n165_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n165_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n165_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n165_I_t1b_t1b_t1b),
    .O_t0b(n165_O_t0b),
    .O_t1b(n165_O_t1b)
  );
  AtomTuple_1 n167 ( // @[Top.scala 506:22]
    .valid_up(n167_valid_up),
    .valid_down(n167_valid_down),
    .I0(n167_I0),
    .I1_t0b(n167_I1_t0b),
    .I1_t1b(n167_I1_t1b),
    .O_t0b(n167_O_t0b),
    .O_t1b_t0b(n167_O_t1b_t0b),
    .O_t1b_t1b(n167_O_t1b_t1b)
  );
  assign valid_down = n167_valid_down; // @[Top.scala 511:16]
  assign O_t0b = n167_O_t0b; // @[Top.scala 510:7]
  assign O_t1b_t0b = n167_O_t1b_t0b; // @[Top.scala 510:7]
  assign O_t1b_t1b = n167_O_t1b_t1b; // @[Top.scala 510:7]
  assign n138_valid_up = valid_up; // @[Top.scala 423:19]
  assign n138_I_t0b = I_t0b; // @[Top.scala 422:12]
  assign n166_clock = clock;
  assign n166_reset = reset;
  assign n166_valid_up = n138_valid_down; // @[Top.scala 426:19]
  assign n166_I = n138_O; // @[Top.scala 425:12]
  assign n154_clock = clock;
  assign n154_reset = reset;
  assign n154_valid_up = n138_valid_down; // @[Top.scala 429:19]
  assign n154_I = n138_O; // @[Top.scala 428:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n140_valid_up = valid_up; // @[Top.scala 433:19]
  assign n140_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 432:12]
  assign n140_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 432:12]
  assign n141_valid_up = n140_valid_down; // @[Top.scala 436:19]
  assign n141_I_t0b = n140_O_t0b; // @[Top.scala 435:12]
  assign n142_valid_up = n140_valid_down; // @[Top.scala 439:19]
  assign n142_I_t1b = n140_O_t1b; // @[Top.scala 438:12]
  assign n143_valid_up = n141_valid_down & n142_valid_down; // @[Top.scala 443:19]
  assign n143_I0 = n141_O; // @[Top.scala 441:13]
  assign n143_I1 = n142_O; // @[Top.scala 442:13]
  assign n144_valid_up = n143_valid_down; // @[Top.scala 446:19]
  assign n144_I_t0b = n143_O_t0b; // @[Top.scala 445:12]
  assign n144_I_t1b = n143_O_t1b; // @[Top.scala 445:12]
  assign n146_valid_up = InitialDelayCounter_valid_down & n144_valid_down; // @[Top.scala 450:19]
  assign n146_I0 = 16'h1; // @[Top.scala 448:13]
  assign n146_I1 = n144_O; // @[Top.scala 449:13]
  assign n147_valid_up = n146_valid_down; // @[Top.scala 453:19]
  assign n147_I_t0b = n146_O_t0b; // @[Top.scala 452:12]
  assign n147_I_t1b = n146_O_t1b; // @[Top.scala 452:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n150_valid_up = n147_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 458:19]
  assign n150_I0 = n147_O; // @[Top.scala 456:13]
  assign n151_valid_up = n150_valid_down; // @[Top.scala 461:19]
  assign n151_I_t0b = n150_O_t0b; // @[Top.scala 460:12]
  assign n152_valid_up = n151_valid_down; // @[Top.scala 465:19]
  assign n152_I0 = n151_O; // @[Top.scala 463:13]
  assign n152_I1 = n151_O; // @[Top.scala 464:13]
  assign n153_clock = clock;
  assign n153_reset = reset;
  assign n153_valid_up = n152_valid_down; // @[Top.scala 468:19]
  assign n153_I_t0b = n152_O_t0b; // @[Top.scala 467:12]
  assign n153_I_t1b = n152_O_t1b; // @[Top.scala 467:12]
  assign n155_valid_up = n154_valid_down & n153_valid_down; // @[Top.scala 472:19]
  assign n155_I0 = n154_O; // @[Top.scala 470:13]
  assign n155_I1 = n153_O; // @[Top.scala 471:13]
  assign n156_valid_up = n155_valid_down; // @[Top.scala 475:19]
  assign n156_I_t0b = n155_O_t0b; // @[Top.scala 474:12]
  assign n156_I_t1b = n155_O_t1b; // @[Top.scala 474:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n158_valid_up = n151_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 480:19]
  assign n158_I0 = n151_O; // @[Top.scala 478:13]
  assign n158_I1 = 16'h1; // @[Top.scala 479:13]
  assign n159_valid_up = n158_valid_down; // @[Top.scala 483:19]
  assign n159_I_t0b = n158_O_t0b; // @[Top.scala 482:12]
  assign n159_I_t1b = n158_O_t1b; // @[Top.scala 482:12]
  assign n160_valid_up = n141_valid_down & n159_valid_down; // @[Top.scala 487:19]
  assign n160_I0 = n141_O; // @[Top.scala 485:13]
  assign n160_I1 = n159_O; // @[Top.scala 486:13]
  assign n161_valid_up = n151_valid_down & n142_valid_down; // @[Top.scala 491:19]
  assign n161_I0 = n151_O; // @[Top.scala 489:13]
  assign n161_I1 = n142_O; // @[Top.scala 490:13]
  assign n162_valid_up = n160_valid_down & n161_valid_down; // @[Top.scala 495:19]
  assign n162_I0_t0b = n160_O_t0b; // @[Top.scala 493:13]
  assign n162_I0_t1b = n160_O_t1b; // @[Top.scala 493:13]
  assign n162_I1_t0b = n161_O_t0b; // @[Top.scala 494:13]
  assign n162_I1_t1b = n161_O_t1b; // @[Top.scala 494:13]
  assign n163_clock = clock;
  assign n163_reset = reset;
  assign n163_valid_up = n162_valid_down; // @[Top.scala 498:19]
  assign n163_I_t0b_t0b = n162_O_t0b_t0b; // @[Top.scala 497:12]
  assign n163_I_t0b_t1b = n162_O_t0b_t1b; // @[Top.scala 497:12]
  assign n163_I_t1b_t0b = n162_O_t1b_t0b; // @[Top.scala 497:12]
  assign n163_I_t1b_t1b = n162_O_t1b_t1b; // @[Top.scala 497:12]
  assign n164_valid_up = n156_valid_down & n163_valid_down; // @[Top.scala 502:19]
  assign n164_I0 = n156_O[0]; // @[Top.scala 500:13]
  assign n164_I1_t0b_t0b = n163_O_t0b_t0b; // @[Top.scala 501:13]
  assign n164_I1_t0b_t1b = n163_O_t0b_t1b; // @[Top.scala 501:13]
  assign n164_I1_t1b_t0b = n163_O_t1b_t0b; // @[Top.scala 501:13]
  assign n164_I1_t1b_t1b = n163_O_t1b_t1b; // @[Top.scala 501:13]
  assign n165_valid_up = n164_valid_down; // @[Top.scala 505:19]
  assign n165_I_t0b = n164_O_t0b; // @[Top.scala 504:12]
  assign n165_I_t1b_t0b_t0b = n164_O_t1b_t0b_t0b; // @[Top.scala 504:12]
  assign n165_I_t1b_t0b_t1b = n164_O_t1b_t0b_t1b; // @[Top.scala 504:12]
  assign n165_I_t1b_t1b_t0b = n164_O_t1b_t1b_t0b; // @[Top.scala 504:12]
  assign n165_I_t1b_t1b_t1b = n164_O_t1b_t1b_t1b; // @[Top.scala 504:12]
  assign n167_valid_up = n166_valid_down & n165_valid_down; // @[Top.scala 509:19]
  assign n167_I0 = n166_O; // @[Top.scala 507:13]
  assign n167_I1_t0b = n165_O_t0b; // @[Top.scala 508:13]
  assign n167_I1_t1b = n165_O_t1b; // @[Top.scala 508:13]
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
module InitialDelayCounter_15(
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
  wire  n170_valid_up; // @[Top.scala 517:22]
  wire  n170_valid_down; // @[Top.scala 517:22]
  wire [15:0] n170_I_t0b; // @[Top.scala 517:22]
  wire [15:0] n170_O; // @[Top.scala 517:22]
  wire  n198_clock; // @[Top.scala 520:22]
  wire  n198_reset; // @[Top.scala 520:22]
  wire  n198_valid_up; // @[Top.scala 520:22]
  wire  n198_valid_down; // @[Top.scala 520:22]
  wire [15:0] n198_I; // @[Top.scala 520:22]
  wire [15:0] n198_O; // @[Top.scala 520:22]
  wire  n186_clock; // @[Top.scala 523:22]
  wire  n186_reset; // @[Top.scala 523:22]
  wire  n186_valid_up; // @[Top.scala 523:22]
  wire  n186_valid_down; // @[Top.scala 523:22]
  wire [15:0] n186_I; // @[Top.scala 523:22]
  wire [15:0] n186_O; // @[Top.scala 523:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n172_valid_up; // @[Top.scala 527:22]
  wire  n172_valid_down; // @[Top.scala 527:22]
  wire [15:0] n172_I_t1b_t0b; // @[Top.scala 527:22]
  wire [15:0] n172_I_t1b_t1b; // @[Top.scala 527:22]
  wire [15:0] n172_O_t0b; // @[Top.scala 527:22]
  wire [15:0] n172_O_t1b; // @[Top.scala 527:22]
  wire  n173_valid_up; // @[Top.scala 530:22]
  wire  n173_valid_down; // @[Top.scala 530:22]
  wire [15:0] n173_I_t0b; // @[Top.scala 530:22]
  wire [15:0] n173_O; // @[Top.scala 530:22]
  wire  n174_valid_up; // @[Top.scala 533:22]
  wire  n174_valid_down; // @[Top.scala 533:22]
  wire [15:0] n174_I_t1b; // @[Top.scala 533:22]
  wire [15:0] n174_O; // @[Top.scala 533:22]
  wire  n175_valid_up; // @[Top.scala 536:22]
  wire  n175_valid_down; // @[Top.scala 536:22]
  wire [15:0] n175_I0; // @[Top.scala 536:22]
  wire [15:0] n175_I1; // @[Top.scala 536:22]
  wire [15:0] n175_O_t0b; // @[Top.scala 536:22]
  wire [15:0] n175_O_t1b; // @[Top.scala 536:22]
  wire  n176_valid_up; // @[Top.scala 540:22]
  wire  n176_valid_down; // @[Top.scala 540:22]
  wire [15:0] n176_I_t0b; // @[Top.scala 540:22]
  wire [15:0] n176_I_t1b; // @[Top.scala 540:22]
  wire [15:0] n176_O; // @[Top.scala 540:22]
  wire  n178_valid_up; // @[Top.scala 543:22]
  wire  n178_valid_down; // @[Top.scala 543:22]
  wire [15:0] n178_I0; // @[Top.scala 543:22]
  wire [15:0] n178_I1; // @[Top.scala 543:22]
  wire [15:0] n178_O_t0b; // @[Top.scala 543:22]
  wire [15:0] n178_O_t1b; // @[Top.scala 543:22]
  wire  n179_valid_up; // @[Top.scala 547:22]
  wire  n179_valid_down; // @[Top.scala 547:22]
  wire [15:0] n179_I_t0b; // @[Top.scala 547:22]
  wire [15:0] n179_I_t1b; // @[Top.scala 547:22]
  wire [15:0] n179_O; // @[Top.scala 547:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n182_valid_up; // @[Top.scala 551:22]
  wire  n182_valid_down; // @[Top.scala 551:22]
  wire [15:0] n182_I0; // @[Top.scala 551:22]
  wire [15:0] n182_O_t0b; // @[Top.scala 551:22]
  wire  n183_valid_up; // @[Top.scala 555:22]
  wire  n183_valid_down; // @[Top.scala 555:22]
  wire [15:0] n183_I_t0b; // @[Top.scala 555:22]
  wire [15:0] n183_O; // @[Top.scala 555:22]
  wire  n184_valid_up; // @[Top.scala 558:22]
  wire  n184_valid_down; // @[Top.scala 558:22]
  wire [15:0] n184_I0; // @[Top.scala 558:22]
  wire [15:0] n184_I1; // @[Top.scala 558:22]
  wire [15:0] n184_O_t0b; // @[Top.scala 558:22]
  wire [15:0] n184_O_t1b; // @[Top.scala 558:22]
  wire  n185_clock; // @[Top.scala 562:22]
  wire  n185_reset; // @[Top.scala 562:22]
  wire  n185_valid_up; // @[Top.scala 562:22]
  wire  n185_valid_down; // @[Top.scala 562:22]
  wire [15:0] n185_I_t0b; // @[Top.scala 562:22]
  wire [15:0] n185_I_t1b; // @[Top.scala 562:22]
  wire [15:0] n185_O; // @[Top.scala 562:22]
  wire  n187_valid_up; // @[Top.scala 565:22]
  wire  n187_valid_down; // @[Top.scala 565:22]
  wire [15:0] n187_I0; // @[Top.scala 565:22]
  wire [15:0] n187_I1; // @[Top.scala 565:22]
  wire [15:0] n187_O_t0b; // @[Top.scala 565:22]
  wire [15:0] n187_O_t1b; // @[Top.scala 565:22]
  wire  n188_valid_up; // @[Top.scala 569:22]
  wire  n188_valid_down; // @[Top.scala 569:22]
  wire [15:0] n188_I_t0b; // @[Top.scala 569:22]
  wire [15:0] n188_I_t1b; // @[Top.scala 569:22]
  wire [15:0] n188_O; // @[Top.scala 569:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n190_valid_up; // @[Top.scala 573:22]
  wire  n190_valid_down; // @[Top.scala 573:22]
  wire [15:0] n190_I0; // @[Top.scala 573:22]
  wire [15:0] n190_I1; // @[Top.scala 573:22]
  wire [15:0] n190_O_t0b; // @[Top.scala 573:22]
  wire [15:0] n190_O_t1b; // @[Top.scala 573:22]
  wire  n191_valid_up; // @[Top.scala 577:22]
  wire  n191_valid_down; // @[Top.scala 577:22]
  wire [15:0] n191_I_t0b; // @[Top.scala 577:22]
  wire [15:0] n191_I_t1b; // @[Top.scala 577:22]
  wire [15:0] n191_O; // @[Top.scala 577:22]
  wire  n192_valid_up; // @[Top.scala 580:22]
  wire  n192_valid_down; // @[Top.scala 580:22]
  wire [15:0] n192_I0; // @[Top.scala 580:22]
  wire [15:0] n192_I1; // @[Top.scala 580:22]
  wire [15:0] n192_O_t0b; // @[Top.scala 580:22]
  wire [15:0] n192_O_t1b; // @[Top.scala 580:22]
  wire  n193_valid_up; // @[Top.scala 584:22]
  wire  n193_valid_down; // @[Top.scala 584:22]
  wire [15:0] n193_I0; // @[Top.scala 584:22]
  wire [15:0] n193_I1; // @[Top.scala 584:22]
  wire [15:0] n193_O_t0b; // @[Top.scala 584:22]
  wire [15:0] n193_O_t1b; // @[Top.scala 584:22]
  wire  n194_valid_up; // @[Top.scala 588:22]
  wire  n194_valid_down; // @[Top.scala 588:22]
  wire [15:0] n194_I0_t0b; // @[Top.scala 588:22]
  wire [15:0] n194_I0_t1b; // @[Top.scala 588:22]
  wire [15:0] n194_I1_t0b; // @[Top.scala 588:22]
  wire [15:0] n194_I1_t1b; // @[Top.scala 588:22]
  wire [15:0] n194_O_t0b_t0b; // @[Top.scala 588:22]
  wire [15:0] n194_O_t0b_t1b; // @[Top.scala 588:22]
  wire [15:0] n194_O_t1b_t0b; // @[Top.scala 588:22]
  wire [15:0] n194_O_t1b_t1b; // @[Top.scala 588:22]
  wire  n195_clock; // @[Top.scala 592:22]
  wire  n195_reset; // @[Top.scala 592:22]
  wire  n195_valid_up; // @[Top.scala 592:22]
  wire  n195_valid_down; // @[Top.scala 592:22]
  wire [15:0] n195_I_t0b_t0b; // @[Top.scala 592:22]
  wire [15:0] n195_I_t0b_t1b; // @[Top.scala 592:22]
  wire [15:0] n195_I_t1b_t0b; // @[Top.scala 592:22]
  wire [15:0] n195_I_t1b_t1b; // @[Top.scala 592:22]
  wire [15:0] n195_O_t0b_t0b; // @[Top.scala 592:22]
  wire [15:0] n195_O_t0b_t1b; // @[Top.scala 592:22]
  wire [15:0] n195_O_t1b_t0b; // @[Top.scala 592:22]
  wire [15:0] n195_O_t1b_t1b; // @[Top.scala 592:22]
  wire  n196_valid_up; // @[Top.scala 595:22]
  wire  n196_valid_down; // @[Top.scala 595:22]
  wire  n196_I0; // @[Top.scala 595:22]
  wire [15:0] n196_I1_t0b_t0b; // @[Top.scala 595:22]
  wire [15:0] n196_I1_t0b_t1b; // @[Top.scala 595:22]
  wire [15:0] n196_I1_t1b_t0b; // @[Top.scala 595:22]
  wire [15:0] n196_I1_t1b_t1b; // @[Top.scala 595:22]
  wire  n196_O_t0b; // @[Top.scala 595:22]
  wire [15:0] n196_O_t1b_t0b_t0b; // @[Top.scala 595:22]
  wire [15:0] n196_O_t1b_t0b_t1b; // @[Top.scala 595:22]
  wire [15:0] n196_O_t1b_t1b_t0b; // @[Top.scala 595:22]
  wire [15:0] n196_O_t1b_t1b_t1b; // @[Top.scala 595:22]
  wire  n197_valid_up; // @[Top.scala 599:22]
  wire  n197_valid_down; // @[Top.scala 599:22]
  wire  n197_I_t0b; // @[Top.scala 599:22]
  wire [15:0] n197_I_t1b_t0b_t0b; // @[Top.scala 599:22]
  wire [15:0] n197_I_t1b_t0b_t1b; // @[Top.scala 599:22]
  wire [15:0] n197_I_t1b_t1b_t0b; // @[Top.scala 599:22]
  wire [15:0] n197_I_t1b_t1b_t1b; // @[Top.scala 599:22]
  wire [15:0] n197_O_t0b; // @[Top.scala 599:22]
  wire [15:0] n197_O_t1b; // @[Top.scala 599:22]
  wire  n199_valid_up; // @[Top.scala 602:22]
  wire  n199_valid_down; // @[Top.scala 602:22]
  wire [15:0] n199_I0; // @[Top.scala 602:22]
  wire [15:0] n199_I1_t0b; // @[Top.scala 602:22]
  wire [15:0] n199_I1_t1b; // @[Top.scala 602:22]
  wire [15:0] n199_O_t0b; // @[Top.scala 602:22]
  wire [15:0] n199_O_t1b_t0b; // @[Top.scala 602:22]
  wire [15:0] n199_O_t1b_t1b; // @[Top.scala 602:22]
  Fst n170 ( // @[Top.scala 517:22]
    .valid_up(n170_valid_up),
    .valid_down(n170_valid_down),
    .I_t0b(n170_I_t0b),
    .O(n170_O)
  );
  FIFO_2 n198 ( // @[Top.scala 520:22]
    .clock(n198_clock),
    .reset(n198_reset),
    .valid_up(n198_valid_up),
    .valid_down(n198_valid_down),
    .I(n198_I),
    .O(n198_O)
  );
  FIFO_2 n186 ( // @[Top.scala 523:22]
    .clock(n186_clock),
    .reset(n186_reset),
    .valid_up(n186_valid_up),
    .valid_down(n186_valid_down),
    .I(n186_I),
    .O(n186_O)
  );
  InitialDelayCounter_15 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n172 ( // @[Top.scala 527:22]
    .valid_up(n172_valid_up),
    .valid_down(n172_valid_down),
    .I_t1b_t0b(n172_I_t1b_t0b),
    .I_t1b_t1b(n172_I_t1b_t1b),
    .O_t0b(n172_O_t0b),
    .O_t1b(n172_O_t1b)
  );
  Fst_1 n173 ( // @[Top.scala 530:22]
    .valid_up(n173_valid_up),
    .valid_down(n173_valid_down),
    .I_t0b(n173_I_t0b),
    .O(n173_O)
  );
  Snd_1 n174 ( // @[Top.scala 533:22]
    .valid_up(n174_valid_up),
    .valid_down(n174_valid_down),
    .I_t1b(n174_I_t1b),
    .O(n174_O)
  );
  AtomTuple n175 ( // @[Top.scala 536:22]
    .valid_up(n175_valid_up),
    .valid_down(n175_valid_down),
    .I0(n175_I0),
    .I1(n175_I1),
    .O_t0b(n175_O_t0b),
    .O_t1b(n175_O_t1b)
  );
  Add n176 ( // @[Top.scala 540:22]
    .valid_up(n176_valid_up),
    .valid_down(n176_valid_down),
    .I_t0b(n176_I_t0b),
    .I_t1b(n176_I_t1b),
    .O(n176_O)
  );
  AtomTuple n178 ( // @[Top.scala 543:22]
    .valid_up(n178_valid_up),
    .valid_down(n178_valid_down),
    .I0(n178_I0),
    .I1(n178_I1),
    .O_t0b(n178_O_t0b),
    .O_t1b(n178_O_t1b)
  );
  Add n179 ( // @[Top.scala 547:22]
    .valid_up(n179_valid_up),
    .valid_down(n179_valid_down),
    .I_t0b(n179_I_t0b),
    .I_t1b(n179_I_t1b),
    .O(n179_O)
  );
  InitialDelayCounter_15 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n182 ( // @[Top.scala 551:22]
    .valid_up(n182_valid_up),
    .valid_down(n182_valid_down),
    .I0(n182_I0),
    .O_t0b(n182_O_t0b)
  );
  RShift n183 ( // @[Top.scala 555:22]
    .valid_up(n183_valid_up),
    .valid_down(n183_valid_down),
    .I_t0b(n183_I_t0b),
    .O(n183_O)
  );
  AtomTuple n184 ( // @[Top.scala 558:22]
    .valid_up(n184_valid_up),
    .valid_down(n184_valid_down),
    .I0(n184_I0),
    .I1(n184_I1),
    .O_t0b(n184_O_t0b),
    .O_t1b(n184_O_t1b)
  );
  Mul n185 ( // @[Top.scala 562:22]
    .clock(n185_clock),
    .reset(n185_reset),
    .valid_up(n185_valid_up),
    .valid_down(n185_valid_down),
    .I_t0b(n185_I_t0b),
    .I_t1b(n185_I_t1b),
    .O(n185_O)
  );
  AtomTuple n187 ( // @[Top.scala 565:22]
    .valid_up(n187_valid_up),
    .valid_down(n187_valid_down),
    .I0(n187_I0),
    .I1(n187_I1),
    .O_t0b(n187_O_t0b),
    .O_t1b(n187_O_t1b)
  );
  Lt n188 ( // @[Top.scala 569:22]
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
  AtomTuple n190 ( // @[Top.scala 573:22]
    .valid_up(n190_valid_up),
    .valid_down(n190_valid_down),
    .I0(n190_I0),
    .I1(n190_I1),
    .O_t0b(n190_O_t0b),
    .O_t1b(n190_O_t1b)
  );
  Sub n191 ( // @[Top.scala 577:22]
    .valid_up(n191_valid_up),
    .valid_down(n191_valid_down),
    .I_t0b(n191_I_t0b),
    .I_t1b(n191_I_t1b),
    .O(n191_O)
  );
  AtomTuple n192 ( // @[Top.scala 580:22]
    .valid_up(n192_valid_up),
    .valid_down(n192_valid_down),
    .I0(n192_I0),
    .I1(n192_I1),
    .O_t0b(n192_O_t0b),
    .O_t1b(n192_O_t1b)
  );
  AtomTuple n193 ( // @[Top.scala 584:22]
    .valid_up(n193_valid_up),
    .valid_down(n193_valid_down),
    .I0(n193_I0),
    .I1(n193_I1),
    .O_t0b(n193_O_t0b),
    .O_t1b(n193_O_t1b)
  );
  AtomTuple_10 n194 ( // @[Top.scala 588:22]
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
  FIFO_4 n195 ( // @[Top.scala 592:22]
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
  AtomTuple_11 n196 ( // @[Top.scala 595:22]
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
  If n197 ( // @[Top.scala 599:22]
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
  AtomTuple_1 n199 ( // @[Top.scala 602:22]
    .valid_up(n199_valid_up),
    .valid_down(n199_valid_down),
    .I0(n199_I0),
    .I1_t0b(n199_I1_t0b),
    .I1_t1b(n199_I1_t1b),
    .O_t0b(n199_O_t0b),
    .O_t1b_t0b(n199_O_t1b_t0b),
    .O_t1b_t1b(n199_O_t1b_t1b)
  );
  assign valid_down = n199_valid_down; // @[Top.scala 607:16]
  assign O_t0b = n199_O_t0b; // @[Top.scala 606:7]
  assign O_t1b_t0b = n199_O_t1b_t0b; // @[Top.scala 606:7]
  assign O_t1b_t1b = n199_O_t1b_t1b; // @[Top.scala 606:7]
  assign n170_valid_up = valid_up; // @[Top.scala 519:19]
  assign n170_I_t0b = I_t0b; // @[Top.scala 518:12]
  assign n198_clock = clock;
  assign n198_reset = reset;
  assign n198_valid_up = n170_valid_down; // @[Top.scala 522:19]
  assign n198_I = n170_O; // @[Top.scala 521:12]
  assign n186_clock = clock;
  assign n186_reset = reset;
  assign n186_valid_up = n170_valid_down; // @[Top.scala 525:19]
  assign n186_I = n170_O; // @[Top.scala 524:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n172_valid_up = valid_up; // @[Top.scala 529:19]
  assign n172_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 528:12]
  assign n172_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 528:12]
  assign n173_valid_up = n172_valid_down; // @[Top.scala 532:19]
  assign n173_I_t0b = n172_O_t0b; // @[Top.scala 531:12]
  assign n174_valid_up = n172_valid_down; // @[Top.scala 535:19]
  assign n174_I_t1b = n172_O_t1b; // @[Top.scala 534:12]
  assign n175_valid_up = n173_valid_down & n174_valid_down; // @[Top.scala 539:19]
  assign n175_I0 = n173_O; // @[Top.scala 537:13]
  assign n175_I1 = n174_O; // @[Top.scala 538:13]
  assign n176_valid_up = n175_valid_down; // @[Top.scala 542:19]
  assign n176_I_t0b = n175_O_t0b; // @[Top.scala 541:12]
  assign n176_I_t1b = n175_O_t1b; // @[Top.scala 541:12]
  assign n178_valid_up = InitialDelayCounter_valid_down & n176_valid_down; // @[Top.scala 546:19]
  assign n178_I0 = 16'h1; // @[Top.scala 544:13]
  assign n178_I1 = n176_O; // @[Top.scala 545:13]
  assign n179_valid_up = n178_valid_down; // @[Top.scala 549:19]
  assign n179_I_t0b = n178_O_t0b; // @[Top.scala 548:12]
  assign n179_I_t1b = n178_O_t1b; // @[Top.scala 548:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n182_valid_up = n179_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 554:19]
  assign n182_I0 = n179_O; // @[Top.scala 552:13]
  assign n183_valid_up = n182_valid_down; // @[Top.scala 557:19]
  assign n183_I_t0b = n182_O_t0b; // @[Top.scala 556:12]
  assign n184_valid_up = n183_valid_down; // @[Top.scala 561:19]
  assign n184_I0 = n183_O; // @[Top.scala 559:13]
  assign n184_I1 = n183_O; // @[Top.scala 560:13]
  assign n185_clock = clock;
  assign n185_reset = reset;
  assign n185_valid_up = n184_valid_down; // @[Top.scala 564:19]
  assign n185_I_t0b = n184_O_t0b; // @[Top.scala 563:12]
  assign n185_I_t1b = n184_O_t1b; // @[Top.scala 563:12]
  assign n187_valid_up = n186_valid_down & n185_valid_down; // @[Top.scala 568:19]
  assign n187_I0 = n186_O; // @[Top.scala 566:13]
  assign n187_I1 = n185_O; // @[Top.scala 567:13]
  assign n188_valid_up = n187_valid_down; // @[Top.scala 571:19]
  assign n188_I_t0b = n187_O_t0b; // @[Top.scala 570:12]
  assign n188_I_t1b = n187_O_t1b; // @[Top.scala 570:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n190_valid_up = n183_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 576:19]
  assign n190_I0 = n183_O; // @[Top.scala 574:13]
  assign n190_I1 = 16'h1; // @[Top.scala 575:13]
  assign n191_valid_up = n190_valid_down; // @[Top.scala 579:19]
  assign n191_I_t0b = n190_O_t0b; // @[Top.scala 578:12]
  assign n191_I_t1b = n190_O_t1b; // @[Top.scala 578:12]
  assign n192_valid_up = n173_valid_down & n191_valid_down; // @[Top.scala 583:19]
  assign n192_I0 = n173_O; // @[Top.scala 581:13]
  assign n192_I1 = n191_O; // @[Top.scala 582:13]
  assign n193_valid_up = n183_valid_down & n174_valid_down; // @[Top.scala 587:19]
  assign n193_I0 = n183_O; // @[Top.scala 585:13]
  assign n193_I1 = n174_O; // @[Top.scala 586:13]
  assign n194_valid_up = n192_valid_down & n193_valid_down; // @[Top.scala 591:19]
  assign n194_I0_t0b = n192_O_t0b; // @[Top.scala 589:13]
  assign n194_I0_t1b = n192_O_t1b; // @[Top.scala 589:13]
  assign n194_I1_t0b = n193_O_t0b; // @[Top.scala 590:13]
  assign n194_I1_t1b = n193_O_t1b; // @[Top.scala 590:13]
  assign n195_clock = clock;
  assign n195_reset = reset;
  assign n195_valid_up = n194_valid_down; // @[Top.scala 594:19]
  assign n195_I_t0b_t0b = n194_O_t0b_t0b; // @[Top.scala 593:12]
  assign n195_I_t0b_t1b = n194_O_t0b_t1b; // @[Top.scala 593:12]
  assign n195_I_t1b_t0b = n194_O_t1b_t0b; // @[Top.scala 593:12]
  assign n195_I_t1b_t1b = n194_O_t1b_t1b; // @[Top.scala 593:12]
  assign n196_valid_up = n188_valid_down & n195_valid_down; // @[Top.scala 598:19]
  assign n196_I0 = n188_O[0]; // @[Top.scala 596:13]
  assign n196_I1_t0b_t0b = n195_O_t0b_t0b; // @[Top.scala 597:13]
  assign n196_I1_t0b_t1b = n195_O_t0b_t1b; // @[Top.scala 597:13]
  assign n196_I1_t1b_t0b = n195_O_t1b_t0b; // @[Top.scala 597:13]
  assign n196_I1_t1b_t1b = n195_O_t1b_t1b; // @[Top.scala 597:13]
  assign n197_valid_up = n196_valid_down; // @[Top.scala 601:19]
  assign n197_I_t0b = n196_O_t0b; // @[Top.scala 600:12]
  assign n197_I_t1b_t0b_t0b = n196_O_t1b_t0b_t0b; // @[Top.scala 600:12]
  assign n197_I_t1b_t0b_t1b = n196_O_t1b_t0b_t1b; // @[Top.scala 600:12]
  assign n197_I_t1b_t1b_t0b = n196_O_t1b_t1b_t0b; // @[Top.scala 600:12]
  assign n197_I_t1b_t1b_t1b = n196_O_t1b_t1b_t1b; // @[Top.scala 600:12]
  assign n199_valid_up = n198_valid_down & n197_valid_down; // @[Top.scala 605:19]
  assign n199_I0 = n198_O; // @[Top.scala 603:13]
  assign n199_I1_t0b = n197_O_t0b; // @[Top.scala 604:13]
  assign n199_I1_t1b = n197_O_t1b; // @[Top.scala 604:13]
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
module InitialDelayCounter_18(
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
  wire  n202_valid_up; // @[Top.scala 613:22]
  wire  n202_valid_down; // @[Top.scala 613:22]
  wire [15:0] n202_I_t0b; // @[Top.scala 613:22]
  wire [15:0] n202_O; // @[Top.scala 613:22]
  wire  n230_clock; // @[Top.scala 616:22]
  wire  n230_reset; // @[Top.scala 616:22]
  wire  n230_valid_up; // @[Top.scala 616:22]
  wire  n230_valid_down; // @[Top.scala 616:22]
  wire [15:0] n230_I; // @[Top.scala 616:22]
  wire [15:0] n230_O; // @[Top.scala 616:22]
  wire  n218_clock; // @[Top.scala 619:22]
  wire  n218_reset; // @[Top.scala 619:22]
  wire  n218_valid_up; // @[Top.scala 619:22]
  wire  n218_valid_down; // @[Top.scala 619:22]
  wire [15:0] n218_I; // @[Top.scala 619:22]
  wire [15:0] n218_O; // @[Top.scala 619:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n204_valid_up; // @[Top.scala 623:22]
  wire  n204_valid_down; // @[Top.scala 623:22]
  wire [15:0] n204_I_t1b_t0b; // @[Top.scala 623:22]
  wire [15:0] n204_I_t1b_t1b; // @[Top.scala 623:22]
  wire [15:0] n204_O_t0b; // @[Top.scala 623:22]
  wire [15:0] n204_O_t1b; // @[Top.scala 623:22]
  wire  n205_valid_up; // @[Top.scala 626:22]
  wire  n205_valid_down; // @[Top.scala 626:22]
  wire [15:0] n205_I_t0b; // @[Top.scala 626:22]
  wire [15:0] n205_O; // @[Top.scala 626:22]
  wire  n206_valid_up; // @[Top.scala 629:22]
  wire  n206_valid_down; // @[Top.scala 629:22]
  wire [15:0] n206_I_t1b; // @[Top.scala 629:22]
  wire [15:0] n206_O; // @[Top.scala 629:22]
  wire  n207_valid_up; // @[Top.scala 632:22]
  wire  n207_valid_down; // @[Top.scala 632:22]
  wire [15:0] n207_I0; // @[Top.scala 632:22]
  wire [15:0] n207_I1; // @[Top.scala 632:22]
  wire [15:0] n207_O_t0b; // @[Top.scala 632:22]
  wire [15:0] n207_O_t1b; // @[Top.scala 632:22]
  wire  n208_valid_up; // @[Top.scala 636:22]
  wire  n208_valid_down; // @[Top.scala 636:22]
  wire [15:0] n208_I_t0b; // @[Top.scala 636:22]
  wire [15:0] n208_I_t1b; // @[Top.scala 636:22]
  wire [15:0] n208_O; // @[Top.scala 636:22]
  wire  n210_valid_up; // @[Top.scala 639:22]
  wire  n210_valid_down; // @[Top.scala 639:22]
  wire [15:0] n210_I0; // @[Top.scala 639:22]
  wire [15:0] n210_I1; // @[Top.scala 639:22]
  wire [15:0] n210_O_t0b; // @[Top.scala 639:22]
  wire [15:0] n210_O_t1b; // @[Top.scala 639:22]
  wire  n211_valid_up; // @[Top.scala 643:22]
  wire  n211_valid_down; // @[Top.scala 643:22]
  wire [15:0] n211_I_t0b; // @[Top.scala 643:22]
  wire [15:0] n211_I_t1b; // @[Top.scala 643:22]
  wire [15:0] n211_O; // @[Top.scala 643:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n214_valid_up; // @[Top.scala 647:22]
  wire  n214_valid_down; // @[Top.scala 647:22]
  wire [15:0] n214_I0; // @[Top.scala 647:22]
  wire [15:0] n214_O_t0b; // @[Top.scala 647:22]
  wire  n215_valid_up; // @[Top.scala 651:22]
  wire  n215_valid_down; // @[Top.scala 651:22]
  wire [15:0] n215_I_t0b; // @[Top.scala 651:22]
  wire [15:0] n215_O; // @[Top.scala 651:22]
  wire  n216_valid_up; // @[Top.scala 654:22]
  wire  n216_valid_down; // @[Top.scala 654:22]
  wire [15:0] n216_I0; // @[Top.scala 654:22]
  wire [15:0] n216_I1; // @[Top.scala 654:22]
  wire [15:0] n216_O_t0b; // @[Top.scala 654:22]
  wire [15:0] n216_O_t1b; // @[Top.scala 654:22]
  wire  n217_clock; // @[Top.scala 658:22]
  wire  n217_reset; // @[Top.scala 658:22]
  wire  n217_valid_up; // @[Top.scala 658:22]
  wire  n217_valid_down; // @[Top.scala 658:22]
  wire [15:0] n217_I_t0b; // @[Top.scala 658:22]
  wire [15:0] n217_I_t1b; // @[Top.scala 658:22]
  wire [15:0] n217_O; // @[Top.scala 658:22]
  wire  n219_valid_up; // @[Top.scala 661:22]
  wire  n219_valid_down; // @[Top.scala 661:22]
  wire [15:0] n219_I0; // @[Top.scala 661:22]
  wire [15:0] n219_I1; // @[Top.scala 661:22]
  wire [15:0] n219_O_t0b; // @[Top.scala 661:22]
  wire [15:0] n219_O_t1b; // @[Top.scala 661:22]
  wire  n220_valid_up; // @[Top.scala 665:22]
  wire  n220_valid_down; // @[Top.scala 665:22]
  wire [15:0] n220_I_t0b; // @[Top.scala 665:22]
  wire [15:0] n220_I_t1b; // @[Top.scala 665:22]
  wire [15:0] n220_O; // @[Top.scala 665:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n222_valid_up; // @[Top.scala 669:22]
  wire  n222_valid_down; // @[Top.scala 669:22]
  wire [15:0] n222_I0; // @[Top.scala 669:22]
  wire [15:0] n222_I1; // @[Top.scala 669:22]
  wire [15:0] n222_O_t0b; // @[Top.scala 669:22]
  wire [15:0] n222_O_t1b; // @[Top.scala 669:22]
  wire  n223_valid_up; // @[Top.scala 673:22]
  wire  n223_valid_down; // @[Top.scala 673:22]
  wire [15:0] n223_I_t0b; // @[Top.scala 673:22]
  wire [15:0] n223_I_t1b; // @[Top.scala 673:22]
  wire [15:0] n223_O; // @[Top.scala 673:22]
  wire  n224_valid_up; // @[Top.scala 676:22]
  wire  n224_valid_down; // @[Top.scala 676:22]
  wire [15:0] n224_I0; // @[Top.scala 676:22]
  wire [15:0] n224_I1; // @[Top.scala 676:22]
  wire [15:0] n224_O_t0b; // @[Top.scala 676:22]
  wire [15:0] n224_O_t1b; // @[Top.scala 676:22]
  wire  n225_valid_up; // @[Top.scala 680:22]
  wire  n225_valid_down; // @[Top.scala 680:22]
  wire [15:0] n225_I0; // @[Top.scala 680:22]
  wire [15:0] n225_I1; // @[Top.scala 680:22]
  wire [15:0] n225_O_t0b; // @[Top.scala 680:22]
  wire [15:0] n225_O_t1b; // @[Top.scala 680:22]
  wire  n226_valid_up; // @[Top.scala 684:22]
  wire  n226_valid_down; // @[Top.scala 684:22]
  wire [15:0] n226_I0_t0b; // @[Top.scala 684:22]
  wire [15:0] n226_I0_t1b; // @[Top.scala 684:22]
  wire [15:0] n226_I1_t0b; // @[Top.scala 684:22]
  wire [15:0] n226_I1_t1b; // @[Top.scala 684:22]
  wire [15:0] n226_O_t0b_t0b; // @[Top.scala 684:22]
  wire [15:0] n226_O_t0b_t1b; // @[Top.scala 684:22]
  wire [15:0] n226_O_t1b_t0b; // @[Top.scala 684:22]
  wire [15:0] n226_O_t1b_t1b; // @[Top.scala 684:22]
  wire  n227_clock; // @[Top.scala 688:22]
  wire  n227_reset; // @[Top.scala 688:22]
  wire  n227_valid_up; // @[Top.scala 688:22]
  wire  n227_valid_down; // @[Top.scala 688:22]
  wire [15:0] n227_I_t0b_t0b; // @[Top.scala 688:22]
  wire [15:0] n227_I_t0b_t1b; // @[Top.scala 688:22]
  wire [15:0] n227_I_t1b_t0b; // @[Top.scala 688:22]
  wire [15:0] n227_I_t1b_t1b; // @[Top.scala 688:22]
  wire [15:0] n227_O_t0b_t0b; // @[Top.scala 688:22]
  wire [15:0] n227_O_t0b_t1b; // @[Top.scala 688:22]
  wire [15:0] n227_O_t1b_t0b; // @[Top.scala 688:22]
  wire [15:0] n227_O_t1b_t1b; // @[Top.scala 688:22]
  wire  n228_valid_up; // @[Top.scala 691:22]
  wire  n228_valid_down; // @[Top.scala 691:22]
  wire  n228_I0; // @[Top.scala 691:22]
  wire [15:0] n228_I1_t0b_t0b; // @[Top.scala 691:22]
  wire [15:0] n228_I1_t0b_t1b; // @[Top.scala 691:22]
  wire [15:0] n228_I1_t1b_t0b; // @[Top.scala 691:22]
  wire [15:0] n228_I1_t1b_t1b; // @[Top.scala 691:22]
  wire  n228_O_t0b; // @[Top.scala 691:22]
  wire [15:0] n228_O_t1b_t0b_t0b; // @[Top.scala 691:22]
  wire [15:0] n228_O_t1b_t0b_t1b; // @[Top.scala 691:22]
  wire [15:0] n228_O_t1b_t1b_t0b; // @[Top.scala 691:22]
  wire [15:0] n228_O_t1b_t1b_t1b; // @[Top.scala 691:22]
  wire  n229_valid_up; // @[Top.scala 695:22]
  wire  n229_valid_down; // @[Top.scala 695:22]
  wire  n229_I_t0b; // @[Top.scala 695:22]
  wire [15:0] n229_I_t1b_t0b_t0b; // @[Top.scala 695:22]
  wire [15:0] n229_I_t1b_t0b_t1b; // @[Top.scala 695:22]
  wire [15:0] n229_I_t1b_t1b_t0b; // @[Top.scala 695:22]
  wire [15:0] n229_I_t1b_t1b_t1b; // @[Top.scala 695:22]
  wire [15:0] n229_O_t0b; // @[Top.scala 695:22]
  wire [15:0] n229_O_t1b; // @[Top.scala 695:22]
  wire  n231_valid_up; // @[Top.scala 698:22]
  wire  n231_valid_down; // @[Top.scala 698:22]
  wire [15:0] n231_I0; // @[Top.scala 698:22]
  wire [15:0] n231_I1_t0b; // @[Top.scala 698:22]
  wire [15:0] n231_I1_t1b; // @[Top.scala 698:22]
  wire [15:0] n231_O_t0b; // @[Top.scala 698:22]
  wire [15:0] n231_O_t1b_t0b; // @[Top.scala 698:22]
  wire [15:0] n231_O_t1b_t1b; // @[Top.scala 698:22]
  Fst n202 ( // @[Top.scala 613:22]
    .valid_up(n202_valid_up),
    .valid_down(n202_valid_down),
    .I_t0b(n202_I_t0b),
    .O(n202_O)
  );
  FIFO_2 n230 ( // @[Top.scala 616:22]
    .clock(n230_clock),
    .reset(n230_reset),
    .valid_up(n230_valid_up),
    .valid_down(n230_valid_down),
    .I(n230_I),
    .O(n230_O)
  );
  FIFO_2 n218 ( // @[Top.scala 619:22]
    .clock(n218_clock),
    .reset(n218_reset),
    .valid_up(n218_valid_up),
    .valid_down(n218_valid_down),
    .I(n218_I),
    .O(n218_O)
  );
  InitialDelayCounter_18 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n204 ( // @[Top.scala 623:22]
    .valid_up(n204_valid_up),
    .valid_down(n204_valid_down),
    .I_t1b_t0b(n204_I_t1b_t0b),
    .I_t1b_t1b(n204_I_t1b_t1b),
    .O_t0b(n204_O_t0b),
    .O_t1b(n204_O_t1b)
  );
  Fst_1 n205 ( // @[Top.scala 626:22]
    .valid_up(n205_valid_up),
    .valid_down(n205_valid_down),
    .I_t0b(n205_I_t0b),
    .O(n205_O)
  );
  Snd_1 n206 ( // @[Top.scala 629:22]
    .valid_up(n206_valid_up),
    .valid_down(n206_valid_down),
    .I_t1b(n206_I_t1b),
    .O(n206_O)
  );
  AtomTuple n207 ( // @[Top.scala 632:22]
    .valid_up(n207_valid_up),
    .valid_down(n207_valid_down),
    .I0(n207_I0),
    .I1(n207_I1),
    .O_t0b(n207_O_t0b),
    .O_t1b(n207_O_t1b)
  );
  Add n208 ( // @[Top.scala 636:22]
    .valid_up(n208_valid_up),
    .valid_down(n208_valid_down),
    .I_t0b(n208_I_t0b),
    .I_t1b(n208_I_t1b),
    .O(n208_O)
  );
  AtomTuple n210 ( // @[Top.scala 639:22]
    .valid_up(n210_valid_up),
    .valid_down(n210_valid_down),
    .I0(n210_I0),
    .I1(n210_I1),
    .O_t0b(n210_O_t0b),
    .O_t1b(n210_O_t1b)
  );
  Add n211 ( // @[Top.scala 643:22]
    .valid_up(n211_valid_up),
    .valid_down(n211_valid_down),
    .I_t0b(n211_I_t0b),
    .I_t1b(n211_I_t1b),
    .O(n211_O)
  );
  InitialDelayCounter_18 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n214 ( // @[Top.scala 647:22]
    .valid_up(n214_valid_up),
    .valid_down(n214_valid_down),
    .I0(n214_I0),
    .O_t0b(n214_O_t0b)
  );
  RShift n215 ( // @[Top.scala 651:22]
    .valid_up(n215_valid_up),
    .valid_down(n215_valid_down),
    .I_t0b(n215_I_t0b),
    .O(n215_O)
  );
  AtomTuple n216 ( // @[Top.scala 654:22]
    .valid_up(n216_valid_up),
    .valid_down(n216_valid_down),
    .I0(n216_I0),
    .I1(n216_I1),
    .O_t0b(n216_O_t0b),
    .O_t1b(n216_O_t1b)
  );
  Mul n217 ( // @[Top.scala 658:22]
    .clock(n217_clock),
    .reset(n217_reset),
    .valid_up(n217_valid_up),
    .valid_down(n217_valid_down),
    .I_t0b(n217_I_t0b),
    .I_t1b(n217_I_t1b),
    .O(n217_O)
  );
  AtomTuple n219 ( // @[Top.scala 661:22]
    .valid_up(n219_valid_up),
    .valid_down(n219_valid_down),
    .I0(n219_I0),
    .I1(n219_I1),
    .O_t0b(n219_O_t0b),
    .O_t1b(n219_O_t1b)
  );
  Lt n220 ( // @[Top.scala 665:22]
    .valid_up(n220_valid_up),
    .valid_down(n220_valid_down),
    .I_t0b(n220_I_t0b),
    .I_t1b(n220_I_t1b),
    .O(n220_O)
  );
  InitialDelayCounter_18 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n222 ( // @[Top.scala 669:22]
    .valid_up(n222_valid_up),
    .valid_down(n222_valid_down),
    .I0(n222_I0),
    .I1(n222_I1),
    .O_t0b(n222_O_t0b),
    .O_t1b(n222_O_t1b)
  );
  Sub n223 ( // @[Top.scala 673:22]
    .valid_up(n223_valid_up),
    .valid_down(n223_valid_down),
    .I_t0b(n223_I_t0b),
    .I_t1b(n223_I_t1b),
    .O(n223_O)
  );
  AtomTuple n224 ( // @[Top.scala 676:22]
    .valid_up(n224_valid_up),
    .valid_down(n224_valid_down),
    .I0(n224_I0),
    .I1(n224_I1),
    .O_t0b(n224_O_t0b),
    .O_t1b(n224_O_t1b)
  );
  AtomTuple n225 ( // @[Top.scala 680:22]
    .valid_up(n225_valid_up),
    .valid_down(n225_valid_down),
    .I0(n225_I0),
    .I1(n225_I1),
    .O_t0b(n225_O_t0b),
    .O_t1b(n225_O_t1b)
  );
  AtomTuple_10 n226 ( // @[Top.scala 684:22]
    .valid_up(n226_valid_up),
    .valid_down(n226_valid_down),
    .I0_t0b(n226_I0_t0b),
    .I0_t1b(n226_I0_t1b),
    .I1_t0b(n226_I1_t0b),
    .I1_t1b(n226_I1_t1b),
    .O_t0b_t0b(n226_O_t0b_t0b),
    .O_t0b_t1b(n226_O_t0b_t1b),
    .O_t1b_t0b(n226_O_t1b_t0b),
    .O_t1b_t1b(n226_O_t1b_t1b)
  );
  FIFO_4 n227 ( // @[Top.scala 688:22]
    .clock(n227_clock),
    .reset(n227_reset),
    .valid_up(n227_valid_up),
    .valid_down(n227_valid_down),
    .I_t0b_t0b(n227_I_t0b_t0b),
    .I_t0b_t1b(n227_I_t0b_t1b),
    .I_t1b_t0b(n227_I_t1b_t0b),
    .I_t1b_t1b(n227_I_t1b_t1b),
    .O_t0b_t0b(n227_O_t0b_t0b),
    .O_t0b_t1b(n227_O_t0b_t1b),
    .O_t1b_t0b(n227_O_t1b_t0b),
    .O_t1b_t1b(n227_O_t1b_t1b)
  );
  AtomTuple_11 n228 ( // @[Top.scala 691:22]
    .valid_up(n228_valid_up),
    .valid_down(n228_valid_down),
    .I0(n228_I0),
    .I1_t0b_t0b(n228_I1_t0b_t0b),
    .I1_t0b_t1b(n228_I1_t0b_t1b),
    .I1_t1b_t0b(n228_I1_t1b_t0b),
    .I1_t1b_t1b(n228_I1_t1b_t1b),
    .O_t0b(n228_O_t0b),
    .O_t1b_t0b_t0b(n228_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n228_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n228_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n228_O_t1b_t1b_t1b)
  );
  If n229 ( // @[Top.scala 695:22]
    .valid_up(n229_valid_up),
    .valid_down(n229_valid_down),
    .I_t0b(n229_I_t0b),
    .I_t1b_t0b_t0b(n229_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n229_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n229_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n229_I_t1b_t1b_t1b),
    .O_t0b(n229_O_t0b),
    .O_t1b(n229_O_t1b)
  );
  AtomTuple_1 n231 ( // @[Top.scala 698:22]
    .valid_up(n231_valid_up),
    .valid_down(n231_valid_down),
    .I0(n231_I0),
    .I1_t0b(n231_I1_t0b),
    .I1_t1b(n231_I1_t1b),
    .O_t0b(n231_O_t0b),
    .O_t1b_t0b(n231_O_t1b_t0b),
    .O_t1b_t1b(n231_O_t1b_t1b)
  );
  assign valid_down = n231_valid_down; // @[Top.scala 703:16]
  assign O_t0b = n231_O_t0b; // @[Top.scala 702:7]
  assign O_t1b_t0b = n231_O_t1b_t0b; // @[Top.scala 702:7]
  assign O_t1b_t1b = n231_O_t1b_t1b; // @[Top.scala 702:7]
  assign n202_valid_up = valid_up; // @[Top.scala 615:19]
  assign n202_I_t0b = I_t0b; // @[Top.scala 614:12]
  assign n230_clock = clock;
  assign n230_reset = reset;
  assign n230_valid_up = n202_valid_down; // @[Top.scala 618:19]
  assign n230_I = n202_O; // @[Top.scala 617:12]
  assign n218_clock = clock;
  assign n218_reset = reset;
  assign n218_valid_up = n202_valid_down; // @[Top.scala 621:19]
  assign n218_I = n202_O; // @[Top.scala 620:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n204_valid_up = valid_up; // @[Top.scala 625:19]
  assign n204_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 624:12]
  assign n204_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 624:12]
  assign n205_valid_up = n204_valid_down; // @[Top.scala 628:19]
  assign n205_I_t0b = n204_O_t0b; // @[Top.scala 627:12]
  assign n206_valid_up = n204_valid_down; // @[Top.scala 631:19]
  assign n206_I_t1b = n204_O_t1b; // @[Top.scala 630:12]
  assign n207_valid_up = n205_valid_down & n206_valid_down; // @[Top.scala 635:19]
  assign n207_I0 = n205_O; // @[Top.scala 633:13]
  assign n207_I1 = n206_O; // @[Top.scala 634:13]
  assign n208_valid_up = n207_valid_down; // @[Top.scala 638:19]
  assign n208_I_t0b = n207_O_t0b; // @[Top.scala 637:12]
  assign n208_I_t1b = n207_O_t1b; // @[Top.scala 637:12]
  assign n210_valid_up = InitialDelayCounter_valid_down & n208_valid_down; // @[Top.scala 642:19]
  assign n210_I0 = 16'h1; // @[Top.scala 640:13]
  assign n210_I1 = n208_O; // @[Top.scala 641:13]
  assign n211_valid_up = n210_valid_down; // @[Top.scala 645:19]
  assign n211_I_t0b = n210_O_t0b; // @[Top.scala 644:12]
  assign n211_I_t1b = n210_O_t1b; // @[Top.scala 644:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n214_valid_up = n211_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 650:19]
  assign n214_I0 = n211_O; // @[Top.scala 648:13]
  assign n215_valid_up = n214_valid_down; // @[Top.scala 653:19]
  assign n215_I_t0b = n214_O_t0b; // @[Top.scala 652:12]
  assign n216_valid_up = n215_valid_down; // @[Top.scala 657:19]
  assign n216_I0 = n215_O; // @[Top.scala 655:13]
  assign n216_I1 = n215_O; // @[Top.scala 656:13]
  assign n217_clock = clock;
  assign n217_reset = reset;
  assign n217_valid_up = n216_valid_down; // @[Top.scala 660:19]
  assign n217_I_t0b = n216_O_t0b; // @[Top.scala 659:12]
  assign n217_I_t1b = n216_O_t1b; // @[Top.scala 659:12]
  assign n219_valid_up = n218_valid_down & n217_valid_down; // @[Top.scala 664:19]
  assign n219_I0 = n218_O; // @[Top.scala 662:13]
  assign n219_I1 = n217_O; // @[Top.scala 663:13]
  assign n220_valid_up = n219_valid_down; // @[Top.scala 667:19]
  assign n220_I_t0b = n219_O_t0b; // @[Top.scala 666:12]
  assign n220_I_t1b = n219_O_t1b; // @[Top.scala 666:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n222_valid_up = n215_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 672:19]
  assign n222_I0 = n215_O; // @[Top.scala 670:13]
  assign n222_I1 = 16'h1; // @[Top.scala 671:13]
  assign n223_valid_up = n222_valid_down; // @[Top.scala 675:19]
  assign n223_I_t0b = n222_O_t0b; // @[Top.scala 674:12]
  assign n223_I_t1b = n222_O_t1b; // @[Top.scala 674:12]
  assign n224_valid_up = n205_valid_down & n223_valid_down; // @[Top.scala 679:19]
  assign n224_I0 = n205_O; // @[Top.scala 677:13]
  assign n224_I1 = n223_O; // @[Top.scala 678:13]
  assign n225_valid_up = n215_valid_down & n206_valid_down; // @[Top.scala 683:19]
  assign n225_I0 = n215_O; // @[Top.scala 681:13]
  assign n225_I1 = n206_O; // @[Top.scala 682:13]
  assign n226_valid_up = n224_valid_down & n225_valid_down; // @[Top.scala 687:19]
  assign n226_I0_t0b = n224_O_t0b; // @[Top.scala 685:13]
  assign n226_I0_t1b = n224_O_t1b; // @[Top.scala 685:13]
  assign n226_I1_t0b = n225_O_t0b; // @[Top.scala 686:13]
  assign n226_I1_t1b = n225_O_t1b; // @[Top.scala 686:13]
  assign n227_clock = clock;
  assign n227_reset = reset;
  assign n227_valid_up = n226_valid_down; // @[Top.scala 690:19]
  assign n227_I_t0b_t0b = n226_O_t0b_t0b; // @[Top.scala 689:12]
  assign n227_I_t0b_t1b = n226_O_t0b_t1b; // @[Top.scala 689:12]
  assign n227_I_t1b_t0b = n226_O_t1b_t0b; // @[Top.scala 689:12]
  assign n227_I_t1b_t1b = n226_O_t1b_t1b; // @[Top.scala 689:12]
  assign n228_valid_up = n220_valid_down & n227_valid_down; // @[Top.scala 694:19]
  assign n228_I0 = n220_O[0]; // @[Top.scala 692:13]
  assign n228_I1_t0b_t0b = n227_O_t0b_t0b; // @[Top.scala 693:13]
  assign n228_I1_t0b_t1b = n227_O_t0b_t1b; // @[Top.scala 693:13]
  assign n228_I1_t1b_t0b = n227_O_t1b_t0b; // @[Top.scala 693:13]
  assign n228_I1_t1b_t1b = n227_O_t1b_t1b; // @[Top.scala 693:13]
  assign n229_valid_up = n228_valid_down; // @[Top.scala 697:19]
  assign n229_I_t0b = n228_O_t0b; // @[Top.scala 696:12]
  assign n229_I_t1b_t0b_t0b = n228_O_t1b_t0b_t0b; // @[Top.scala 696:12]
  assign n229_I_t1b_t0b_t1b = n228_O_t1b_t0b_t1b; // @[Top.scala 696:12]
  assign n229_I_t1b_t1b_t0b = n228_O_t1b_t1b_t0b; // @[Top.scala 696:12]
  assign n229_I_t1b_t1b_t1b = n228_O_t1b_t1b_t1b; // @[Top.scala 696:12]
  assign n231_valid_up = n230_valid_down & n229_valid_down; // @[Top.scala 701:19]
  assign n231_I0 = n230_O; // @[Top.scala 699:13]
  assign n231_I1_t0b = n229_O_t0b; // @[Top.scala 700:13]
  assign n231_I1_t1b = n229_O_t1b; // @[Top.scala 700:13]
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
module InitialDelayCounter_21(
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
  wire  n234_valid_up; // @[Top.scala 709:22]
  wire  n234_valid_down; // @[Top.scala 709:22]
  wire [15:0] n234_I_t0b; // @[Top.scala 709:22]
  wire [15:0] n234_O; // @[Top.scala 709:22]
  wire  n262_clock; // @[Top.scala 712:22]
  wire  n262_reset; // @[Top.scala 712:22]
  wire  n262_valid_up; // @[Top.scala 712:22]
  wire  n262_valid_down; // @[Top.scala 712:22]
  wire [15:0] n262_I; // @[Top.scala 712:22]
  wire [15:0] n262_O; // @[Top.scala 712:22]
  wire  n250_clock; // @[Top.scala 715:22]
  wire  n250_reset; // @[Top.scala 715:22]
  wire  n250_valid_up; // @[Top.scala 715:22]
  wire  n250_valid_down; // @[Top.scala 715:22]
  wire [15:0] n250_I; // @[Top.scala 715:22]
  wire [15:0] n250_O; // @[Top.scala 715:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n236_valid_up; // @[Top.scala 719:22]
  wire  n236_valid_down; // @[Top.scala 719:22]
  wire [15:0] n236_I_t1b_t0b; // @[Top.scala 719:22]
  wire [15:0] n236_I_t1b_t1b; // @[Top.scala 719:22]
  wire [15:0] n236_O_t0b; // @[Top.scala 719:22]
  wire [15:0] n236_O_t1b; // @[Top.scala 719:22]
  wire  n237_valid_up; // @[Top.scala 722:22]
  wire  n237_valid_down; // @[Top.scala 722:22]
  wire [15:0] n237_I_t0b; // @[Top.scala 722:22]
  wire [15:0] n237_O; // @[Top.scala 722:22]
  wire  n238_valid_up; // @[Top.scala 725:22]
  wire  n238_valid_down; // @[Top.scala 725:22]
  wire [15:0] n238_I_t1b; // @[Top.scala 725:22]
  wire [15:0] n238_O; // @[Top.scala 725:22]
  wire  n239_valid_up; // @[Top.scala 728:22]
  wire  n239_valid_down; // @[Top.scala 728:22]
  wire [15:0] n239_I0; // @[Top.scala 728:22]
  wire [15:0] n239_I1; // @[Top.scala 728:22]
  wire [15:0] n239_O_t0b; // @[Top.scala 728:22]
  wire [15:0] n239_O_t1b; // @[Top.scala 728:22]
  wire  n240_valid_up; // @[Top.scala 732:22]
  wire  n240_valid_down; // @[Top.scala 732:22]
  wire [15:0] n240_I_t0b; // @[Top.scala 732:22]
  wire [15:0] n240_I_t1b; // @[Top.scala 732:22]
  wire [15:0] n240_O; // @[Top.scala 732:22]
  wire  n242_valid_up; // @[Top.scala 735:22]
  wire  n242_valid_down; // @[Top.scala 735:22]
  wire [15:0] n242_I0; // @[Top.scala 735:22]
  wire [15:0] n242_I1; // @[Top.scala 735:22]
  wire [15:0] n242_O_t0b; // @[Top.scala 735:22]
  wire [15:0] n242_O_t1b; // @[Top.scala 735:22]
  wire  n243_valid_up; // @[Top.scala 739:22]
  wire  n243_valid_down; // @[Top.scala 739:22]
  wire [15:0] n243_I_t0b; // @[Top.scala 739:22]
  wire [15:0] n243_I_t1b; // @[Top.scala 739:22]
  wire [15:0] n243_O; // @[Top.scala 739:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n246_valid_up; // @[Top.scala 743:22]
  wire  n246_valid_down; // @[Top.scala 743:22]
  wire [15:0] n246_I0; // @[Top.scala 743:22]
  wire [15:0] n246_O_t0b; // @[Top.scala 743:22]
  wire  n247_valid_up; // @[Top.scala 747:22]
  wire  n247_valid_down; // @[Top.scala 747:22]
  wire [15:0] n247_I_t0b; // @[Top.scala 747:22]
  wire [15:0] n247_O; // @[Top.scala 747:22]
  wire  n248_valid_up; // @[Top.scala 750:22]
  wire  n248_valid_down; // @[Top.scala 750:22]
  wire [15:0] n248_I0; // @[Top.scala 750:22]
  wire [15:0] n248_I1; // @[Top.scala 750:22]
  wire [15:0] n248_O_t0b; // @[Top.scala 750:22]
  wire [15:0] n248_O_t1b; // @[Top.scala 750:22]
  wire  n249_clock; // @[Top.scala 754:22]
  wire  n249_reset; // @[Top.scala 754:22]
  wire  n249_valid_up; // @[Top.scala 754:22]
  wire  n249_valid_down; // @[Top.scala 754:22]
  wire [15:0] n249_I_t0b; // @[Top.scala 754:22]
  wire [15:0] n249_I_t1b; // @[Top.scala 754:22]
  wire [15:0] n249_O; // @[Top.scala 754:22]
  wire  n251_valid_up; // @[Top.scala 757:22]
  wire  n251_valid_down; // @[Top.scala 757:22]
  wire [15:0] n251_I0; // @[Top.scala 757:22]
  wire [15:0] n251_I1; // @[Top.scala 757:22]
  wire [15:0] n251_O_t0b; // @[Top.scala 757:22]
  wire [15:0] n251_O_t1b; // @[Top.scala 757:22]
  wire  n252_valid_up; // @[Top.scala 761:22]
  wire  n252_valid_down; // @[Top.scala 761:22]
  wire [15:0] n252_I_t0b; // @[Top.scala 761:22]
  wire [15:0] n252_I_t1b; // @[Top.scala 761:22]
  wire [15:0] n252_O; // @[Top.scala 761:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n254_valid_up; // @[Top.scala 765:22]
  wire  n254_valid_down; // @[Top.scala 765:22]
  wire [15:0] n254_I0; // @[Top.scala 765:22]
  wire [15:0] n254_I1; // @[Top.scala 765:22]
  wire [15:0] n254_O_t0b; // @[Top.scala 765:22]
  wire [15:0] n254_O_t1b; // @[Top.scala 765:22]
  wire  n255_valid_up; // @[Top.scala 769:22]
  wire  n255_valid_down; // @[Top.scala 769:22]
  wire [15:0] n255_I_t0b; // @[Top.scala 769:22]
  wire [15:0] n255_I_t1b; // @[Top.scala 769:22]
  wire [15:0] n255_O; // @[Top.scala 769:22]
  wire  n256_valid_up; // @[Top.scala 772:22]
  wire  n256_valid_down; // @[Top.scala 772:22]
  wire [15:0] n256_I0; // @[Top.scala 772:22]
  wire [15:0] n256_I1; // @[Top.scala 772:22]
  wire [15:0] n256_O_t0b; // @[Top.scala 772:22]
  wire [15:0] n256_O_t1b; // @[Top.scala 772:22]
  wire  n257_valid_up; // @[Top.scala 776:22]
  wire  n257_valid_down; // @[Top.scala 776:22]
  wire [15:0] n257_I0; // @[Top.scala 776:22]
  wire [15:0] n257_I1; // @[Top.scala 776:22]
  wire [15:0] n257_O_t0b; // @[Top.scala 776:22]
  wire [15:0] n257_O_t1b; // @[Top.scala 776:22]
  wire  n258_valid_up; // @[Top.scala 780:22]
  wire  n258_valid_down; // @[Top.scala 780:22]
  wire [15:0] n258_I0_t0b; // @[Top.scala 780:22]
  wire [15:0] n258_I0_t1b; // @[Top.scala 780:22]
  wire [15:0] n258_I1_t0b; // @[Top.scala 780:22]
  wire [15:0] n258_I1_t1b; // @[Top.scala 780:22]
  wire [15:0] n258_O_t0b_t0b; // @[Top.scala 780:22]
  wire [15:0] n258_O_t0b_t1b; // @[Top.scala 780:22]
  wire [15:0] n258_O_t1b_t0b; // @[Top.scala 780:22]
  wire [15:0] n258_O_t1b_t1b; // @[Top.scala 780:22]
  wire  n259_clock; // @[Top.scala 784:22]
  wire  n259_reset; // @[Top.scala 784:22]
  wire  n259_valid_up; // @[Top.scala 784:22]
  wire  n259_valid_down; // @[Top.scala 784:22]
  wire [15:0] n259_I_t0b_t0b; // @[Top.scala 784:22]
  wire [15:0] n259_I_t0b_t1b; // @[Top.scala 784:22]
  wire [15:0] n259_I_t1b_t0b; // @[Top.scala 784:22]
  wire [15:0] n259_I_t1b_t1b; // @[Top.scala 784:22]
  wire [15:0] n259_O_t0b_t0b; // @[Top.scala 784:22]
  wire [15:0] n259_O_t0b_t1b; // @[Top.scala 784:22]
  wire [15:0] n259_O_t1b_t0b; // @[Top.scala 784:22]
  wire [15:0] n259_O_t1b_t1b; // @[Top.scala 784:22]
  wire  n260_valid_up; // @[Top.scala 787:22]
  wire  n260_valid_down; // @[Top.scala 787:22]
  wire  n260_I0; // @[Top.scala 787:22]
  wire [15:0] n260_I1_t0b_t0b; // @[Top.scala 787:22]
  wire [15:0] n260_I1_t0b_t1b; // @[Top.scala 787:22]
  wire [15:0] n260_I1_t1b_t0b; // @[Top.scala 787:22]
  wire [15:0] n260_I1_t1b_t1b; // @[Top.scala 787:22]
  wire  n260_O_t0b; // @[Top.scala 787:22]
  wire [15:0] n260_O_t1b_t0b_t0b; // @[Top.scala 787:22]
  wire [15:0] n260_O_t1b_t0b_t1b; // @[Top.scala 787:22]
  wire [15:0] n260_O_t1b_t1b_t0b; // @[Top.scala 787:22]
  wire [15:0] n260_O_t1b_t1b_t1b; // @[Top.scala 787:22]
  wire  n261_valid_up; // @[Top.scala 791:22]
  wire  n261_valid_down; // @[Top.scala 791:22]
  wire  n261_I_t0b; // @[Top.scala 791:22]
  wire [15:0] n261_I_t1b_t0b_t0b; // @[Top.scala 791:22]
  wire [15:0] n261_I_t1b_t0b_t1b; // @[Top.scala 791:22]
  wire [15:0] n261_I_t1b_t1b_t0b; // @[Top.scala 791:22]
  wire [15:0] n261_I_t1b_t1b_t1b; // @[Top.scala 791:22]
  wire [15:0] n261_O_t0b; // @[Top.scala 791:22]
  wire [15:0] n261_O_t1b; // @[Top.scala 791:22]
  wire  n263_valid_up; // @[Top.scala 794:22]
  wire  n263_valid_down; // @[Top.scala 794:22]
  wire [15:0] n263_I0; // @[Top.scala 794:22]
  wire [15:0] n263_I1_t0b; // @[Top.scala 794:22]
  wire [15:0] n263_I1_t1b; // @[Top.scala 794:22]
  wire [15:0] n263_O_t0b; // @[Top.scala 794:22]
  wire [15:0] n263_O_t1b_t0b; // @[Top.scala 794:22]
  wire [15:0] n263_O_t1b_t1b; // @[Top.scala 794:22]
  Fst n234 ( // @[Top.scala 709:22]
    .valid_up(n234_valid_up),
    .valid_down(n234_valid_down),
    .I_t0b(n234_I_t0b),
    .O(n234_O)
  );
  FIFO_2 n262 ( // @[Top.scala 712:22]
    .clock(n262_clock),
    .reset(n262_reset),
    .valid_up(n262_valid_up),
    .valid_down(n262_valid_down),
    .I(n262_I),
    .O(n262_O)
  );
  FIFO_2 n250 ( // @[Top.scala 715:22]
    .clock(n250_clock),
    .reset(n250_reset),
    .valid_up(n250_valid_up),
    .valid_down(n250_valid_down),
    .I(n250_I),
    .O(n250_O)
  );
  InitialDelayCounter_21 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n236 ( // @[Top.scala 719:22]
    .valid_up(n236_valid_up),
    .valid_down(n236_valid_down),
    .I_t1b_t0b(n236_I_t1b_t0b),
    .I_t1b_t1b(n236_I_t1b_t1b),
    .O_t0b(n236_O_t0b),
    .O_t1b(n236_O_t1b)
  );
  Fst_1 n237 ( // @[Top.scala 722:22]
    .valid_up(n237_valid_up),
    .valid_down(n237_valid_down),
    .I_t0b(n237_I_t0b),
    .O(n237_O)
  );
  Snd_1 n238 ( // @[Top.scala 725:22]
    .valid_up(n238_valid_up),
    .valid_down(n238_valid_down),
    .I_t1b(n238_I_t1b),
    .O(n238_O)
  );
  AtomTuple n239 ( // @[Top.scala 728:22]
    .valid_up(n239_valid_up),
    .valid_down(n239_valid_down),
    .I0(n239_I0),
    .I1(n239_I1),
    .O_t0b(n239_O_t0b),
    .O_t1b(n239_O_t1b)
  );
  Add n240 ( // @[Top.scala 732:22]
    .valid_up(n240_valid_up),
    .valid_down(n240_valid_down),
    .I_t0b(n240_I_t0b),
    .I_t1b(n240_I_t1b),
    .O(n240_O)
  );
  AtomTuple n242 ( // @[Top.scala 735:22]
    .valid_up(n242_valid_up),
    .valid_down(n242_valid_down),
    .I0(n242_I0),
    .I1(n242_I1),
    .O_t0b(n242_O_t0b),
    .O_t1b(n242_O_t1b)
  );
  Add n243 ( // @[Top.scala 739:22]
    .valid_up(n243_valid_up),
    .valid_down(n243_valid_down),
    .I_t0b(n243_I_t0b),
    .I_t1b(n243_I_t1b),
    .O(n243_O)
  );
  InitialDelayCounter_21 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n246 ( // @[Top.scala 743:22]
    .valid_up(n246_valid_up),
    .valid_down(n246_valid_down),
    .I0(n246_I0),
    .O_t0b(n246_O_t0b)
  );
  RShift n247 ( // @[Top.scala 747:22]
    .valid_up(n247_valid_up),
    .valid_down(n247_valid_down),
    .I_t0b(n247_I_t0b),
    .O(n247_O)
  );
  AtomTuple n248 ( // @[Top.scala 750:22]
    .valid_up(n248_valid_up),
    .valid_down(n248_valid_down),
    .I0(n248_I0),
    .I1(n248_I1),
    .O_t0b(n248_O_t0b),
    .O_t1b(n248_O_t1b)
  );
  Mul n249 ( // @[Top.scala 754:22]
    .clock(n249_clock),
    .reset(n249_reset),
    .valid_up(n249_valid_up),
    .valid_down(n249_valid_down),
    .I_t0b(n249_I_t0b),
    .I_t1b(n249_I_t1b),
    .O(n249_O)
  );
  AtomTuple n251 ( // @[Top.scala 757:22]
    .valid_up(n251_valid_up),
    .valid_down(n251_valid_down),
    .I0(n251_I0),
    .I1(n251_I1),
    .O_t0b(n251_O_t0b),
    .O_t1b(n251_O_t1b)
  );
  Lt n252 ( // @[Top.scala 761:22]
    .valid_up(n252_valid_up),
    .valid_down(n252_valid_down),
    .I_t0b(n252_I_t0b),
    .I_t1b(n252_I_t1b),
    .O(n252_O)
  );
  InitialDelayCounter_21 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n254 ( // @[Top.scala 765:22]
    .valid_up(n254_valid_up),
    .valid_down(n254_valid_down),
    .I0(n254_I0),
    .I1(n254_I1),
    .O_t0b(n254_O_t0b),
    .O_t1b(n254_O_t1b)
  );
  Sub n255 ( // @[Top.scala 769:22]
    .valid_up(n255_valid_up),
    .valid_down(n255_valid_down),
    .I_t0b(n255_I_t0b),
    .I_t1b(n255_I_t1b),
    .O(n255_O)
  );
  AtomTuple n256 ( // @[Top.scala 772:22]
    .valid_up(n256_valid_up),
    .valid_down(n256_valid_down),
    .I0(n256_I0),
    .I1(n256_I1),
    .O_t0b(n256_O_t0b),
    .O_t1b(n256_O_t1b)
  );
  AtomTuple n257 ( // @[Top.scala 776:22]
    .valid_up(n257_valid_up),
    .valid_down(n257_valid_down),
    .I0(n257_I0),
    .I1(n257_I1),
    .O_t0b(n257_O_t0b),
    .O_t1b(n257_O_t1b)
  );
  AtomTuple_10 n258 ( // @[Top.scala 780:22]
    .valid_up(n258_valid_up),
    .valid_down(n258_valid_down),
    .I0_t0b(n258_I0_t0b),
    .I0_t1b(n258_I0_t1b),
    .I1_t0b(n258_I1_t0b),
    .I1_t1b(n258_I1_t1b),
    .O_t0b_t0b(n258_O_t0b_t0b),
    .O_t0b_t1b(n258_O_t0b_t1b),
    .O_t1b_t0b(n258_O_t1b_t0b),
    .O_t1b_t1b(n258_O_t1b_t1b)
  );
  FIFO_4 n259 ( // @[Top.scala 784:22]
    .clock(n259_clock),
    .reset(n259_reset),
    .valid_up(n259_valid_up),
    .valid_down(n259_valid_down),
    .I_t0b_t0b(n259_I_t0b_t0b),
    .I_t0b_t1b(n259_I_t0b_t1b),
    .I_t1b_t0b(n259_I_t1b_t0b),
    .I_t1b_t1b(n259_I_t1b_t1b),
    .O_t0b_t0b(n259_O_t0b_t0b),
    .O_t0b_t1b(n259_O_t0b_t1b),
    .O_t1b_t0b(n259_O_t1b_t0b),
    .O_t1b_t1b(n259_O_t1b_t1b)
  );
  AtomTuple_11 n260 ( // @[Top.scala 787:22]
    .valid_up(n260_valid_up),
    .valid_down(n260_valid_down),
    .I0(n260_I0),
    .I1_t0b_t0b(n260_I1_t0b_t0b),
    .I1_t0b_t1b(n260_I1_t0b_t1b),
    .I1_t1b_t0b(n260_I1_t1b_t0b),
    .I1_t1b_t1b(n260_I1_t1b_t1b),
    .O_t0b(n260_O_t0b),
    .O_t1b_t0b_t0b(n260_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n260_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n260_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n260_O_t1b_t1b_t1b)
  );
  If n261 ( // @[Top.scala 791:22]
    .valid_up(n261_valid_up),
    .valid_down(n261_valid_down),
    .I_t0b(n261_I_t0b),
    .I_t1b_t0b_t0b(n261_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n261_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n261_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n261_I_t1b_t1b_t1b),
    .O_t0b(n261_O_t0b),
    .O_t1b(n261_O_t1b)
  );
  AtomTuple_1 n263 ( // @[Top.scala 794:22]
    .valid_up(n263_valid_up),
    .valid_down(n263_valid_down),
    .I0(n263_I0),
    .I1_t0b(n263_I1_t0b),
    .I1_t1b(n263_I1_t1b),
    .O_t0b(n263_O_t0b),
    .O_t1b_t0b(n263_O_t1b_t0b),
    .O_t1b_t1b(n263_O_t1b_t1b)
  );
  assign valid_down = n263_valid_down; // @[Top.scala 799:16]
  assign O_t0b = n263_O_t0b; // @[Top.scala 798:7]
  assign O_t1b_t0b = n263_O_t1b_t0b; // @[Top.scala 798:7]
  assign O_t1b_t1b = n263_O_t1b_t1b; // @[Top.scala 798:7]
  assign n234_valid_up = valid_up; // @[Top.scala 711:19]
  assign n234_I_t0b = I_t0b; // @[Top.scala 710:12]
  assign n262_clock = clock;
  assign n262_reset = reset;
  assign n262_valid_up = n234_valid_down; // @[Top.scala 714:19]
  assign n262_I = n234_O; // @[Top.scala 713:12]
  assign n250_clock = clock;
  assign n250_reset = reset;
  assign n250_valid_up = n234_valid_down; // @[Top.scala 717:19]
  assign n250_I = n234_O; // @[Top.scala 716:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n236_valid_up = valid_up; // @[Top.scala 721:19]
  assign n236_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 720:12]
  assign n236_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 720:12]
  assign n237_valid_up = n236_valid_down; // @[Top.scala 724:19]
  assign n237_I_t0b = n236_O_t0b; // @[Top.scala 723:12]
  assign n238_valid_up = n236_valid_down; // @[Top.scala 727:19]
  assign n238_I_t1b = n236_O_t1b; // @[Top.scala 726:12]
  assign n239_valid_up = n237_valid_down & n238_valid_down; // @[Top.scala 731:19]
  assign n239_I0 = n237_O; // @[Top.scala 729:13]
  assign n239_I1 = n238_O; // @[Top.scala 730:13]
  assign n240_valid_up = n239_valid_down; // @[Top.scala 734:19]
  assign n240_I_t0b = n239_O_t0b; // @[Top.scala 733:12]
  assign n240_I_t1b = n239_O_t1b; // @[Top.scala 733:12]
  assign n242_valid_up = InitialDelayCounter_valid_down & n240_valid_down; // @[Top.scala 738:19]
  assign n242_I0 = 16'h1; // @[Top.scala 736:13]
  assign n242_I1 = n240_O; // @[Top.scala 737:13]
  assign n243_valid_up = n242_valid_down; // @[Top.scala 741:19]
  assign n243_I_t0b = n242_O_t0b; // @[Top.scala 740:12]
  assign n243_I_t1b = n242_O_t1b; // @[Top.scala 740:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n246_valid_up = n243_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 746:19]
  assign n246_I0 = n243_O; // @[Top.scala 744:13]
  assign n247_valid_up = n246_valid_down; // @[Top.scala 749:19]
  assign n247_I_t0b = n246_O_t0b; // @[Top.scala 748:12]
  assign n248_valid_up = n247_valid_down; // @[Top.scala 753:19]
  assign n248_I0 = n247_O; // @[Top.scala 751:13]
  assign n248_I1 = n247_O; // @[Top.scala 752:13]
  assign n249_clock = clock;
  assign n249_reset = reset;
  assign n249_valid_up = n248_valid_down; // @[Top.scala 756:19]
  assign n249_I_t0b = n248_O_t0b; // @[Top.scala 755:12]
  assign n249_I_t1b = n248_O_t1b; // @[Top.scala 755:12]
  assign n251_valid_up = n250_valid_down & n249_valid_down; // @[Top.scala 760:19]
  assign n251_I0 = n250_O; // @[Top.scala 758:13]
  assign n251_I1 = n249_O; // @[Top.scala 759:13]
  assign n252_valid_up = n251_valid_down; // @[Top.scala 763:19]
  assign n252_I_t0b = n251_O_t0b; // @[Top.scala 762:12]
  assign n252_I_t1b = n251_O_t1b; // @[Top.scala 762:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n254_valid_up = n247_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 768:19]
  assign n254_I0 = n247_O; // @[Top.scala 766:13]
  assign n254_I1 = 16'h1; // @[Top.scala 767:13]
  assign n255_valid_up = n254_valid_down; // @[Top.scala 771:19]
  assign n255_I_t0b = n254_O_t0b; // @[Top.scala 770:12]
  assign n255_I_t1b = n254_O_t1b; // @[Top.scala 770:12]
  assign n256_valid_up = n237_valid_down & n255_valid_down; // @[Top.scala 775:19]
  assign n256_I0 = n237_O; // @[Top.scala 773:13]
  assign n256_I1 = n255_O; // @[Top.scala 774:13]
  assign n257_valid_up = n247_valid_down & n238_valid_down; // @[Top.scala 779:19]
  assign n257_I0 = n247_O; // @[Top.scala 777:13]
  assign n257_I1 = n238_O; // @[Top.scala 778:13]
  assign n258_valid_up = n256_valid_down & n257_valid_down; // @[Top.scala 783:19]
  assign n258_I0_t0b = n256_O_t0b; // @[Top.scala 781:13]
  assign n258_I0_t1b = n256_O_t1b; // @[Top.scala 781:13]
  assign n258_I1_t0b = n257_O_t0b; // @[Top.scala 782:13]
  assign n258_I1_t1b = n257_O_t1b; // @[Top.scala 782:13]
  assign n259_clock = clock;
  assign n259_reset = reset;
  assign n259_valid_up = n258_valid_down; // @[Top.scala 786:19]
  assign n259_I_t0b_t0b = n258_O_t0b_t0b; // @[Top.scala 785:12]
  assign n259_I_t0b_t1b = n258_O_t0b_t1b; // @[Top.scala 785:12]
  assign n259_I_t1b_t0b = n258_O_t1b_t0b; // @[Top.scala 785:12]
  assign n259_I_t1b_t1b = n258_O_t1b_t1b; // @[Top.scala 785:12]
  assign n260_valid_up = n252_valid_down & n259_valid_down; // @[Top.scala 790:19]
  assign n260_I0 = n252_O[0]; // @[Top.scala 788:13]
  assign n260_I1_t0b_t0b = n259_O_t0b_t0b; // @[Top.scala 789:13]
  assign n260_I1_t0b_t1b = n259_O_t0b_t1b; // @[Top.scala 789:13]
  assign n260_I1_t1b_t0b = n259_O_t1b_t0b; // @[Top.scala 789:13]
  assign n260_I1_t1b_t1b = n259_O_t1b_t1b; // @[Top.scala 789:13]
  assign n261_valid_up = n260_valid_down; // @[Top.scala 793:19]
  assign n261_I_t0b = n260_O_t0b; // @[Top.scala 792:12]
  assign n261_I_t1b_t0b_t0b = n260_O_t1b_t0b_t0b; // @[Top.scala 792:12]
  assign n261_I_t1b_t0b_t1b = n260_O_t1b_t0b_t1b; // @[Top.scala 792:12]
  assign n261_I_t1b_t1b_t0b = n260_O_t1b_t1b_t0b; // @[Top.scala 792:12]
  assign n261_I_t1b_t1b_t1b = n260_O_t1b_t1b_t1b; // @[Top.scala 792:12]
  assign n263_valid_up = n262_valid_down & n261_valid_down; // @[Top.scala 797:19]
  assign n263_I0 = n262_O; // @[Top.scala 795:13]
  assign n263_I1_t0b = n261_O_t0b; // @[Top.scala 796:13]
  assign n263_I1_t1b = n261_O_t1b; // @[Top.scala 796:13]
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
module InitialDelayCounter_24(
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
  wire  n266_valid_up; // @[Top.scala 805:22]
  wire  n266_valid_down; // @[Top.scala 805:22]
  wire [15:0] n266_I_t0b; // @[Top.scala 805:22]
  wire [15:0] n266_O; // @[Top.scala 805:22]
  wire  n294_clock; // @[Top.scala 808:22]
  wire  n294_reset; // @[Top.scala 808:22]
  wire  n294_valid_up; // @[Top.scala 808:22]
  wire  n294_valid_down; // @[Top.scala 808:22]
  wire [15:0] n294_I; // @[Top.scala 808:22]
  wire [15:0] n294_O; // @[Top.scala 808:22]
  wire  n282_clock; // @[Top.scala 811:22]
  wire  n282_reset; // @[Top.scala 811:22]
  wire  n282_valid_up; // @[Top.scala 811:22]
  wire  n282_valid_down; // @[Top.scala 811:22]
  wire [15:0] n282_I; // @[Top.scala 811:22]
  wire [15:0] n282_O; // @[Top.scala 811:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n268_valid_up; // @[Top.scala 815:22]
  wire  n268_valid_down; // @[Top.scala 815:22]
  wire [15:0] n268_I_t1b_t0b; // @[Top.scala 815:22]
  wire [15:0] n268_I_t1b_t1b; // @[Top.scala 815:22]
  wire [15:0] n268_O_t0b; // @[Top.scala 815:22]
  wire [15:0] n268_O_t1b; // @[Top.scala 815:22]
  wire  n269_valid_up; // @[Top.scala 818:22]
  wire  n269_valid_down; // @[Top.scala 818:22]
  wire [15:0] n269_I_t0b; // @[Top.scala 818:22]
  wire [15:0] n269_O; // @[Top.scala 818:22]
  wire  n270_valid_up; // @[Top.scala 821:22]
  wire  n270_valid_down; // @[Top.scala 821:22]
  wire [15:0] n270_I_t1b; // @[Top.scala 821:22]
  wire [15:0] n270_O; // @[Top.scala 821:22]
  wire  n271_valid_up; // @[Top.scala 824:22]
  wire  n271_valid_down; // @[Top.scala 824:22]
  wire [15:0] n271_I0; // @[Top.scala 824:22]
  wire [15:0] n271_I1; // @[Top.scala 824:22]
  wire [15:0] n271_O_t0b; // @[Top.scala 824:22]
  wire [15:0] n271_O_t1b; // @[Top.scala 824:22]
  wire  n272_valid_up; // @[Top.scala 828:22]
  wire  n272_valid_down; // @[Top.scala 828:22]
  wire [15:0] n272_I_t0b; // @[Top.scala 828:22]
  wire [15:0] n272_I_t1b; // @[Top.scala 828:22]
  wire [15:0] n272_O; // @[Top.scala 828:22]
  wire  n274_valid_up; // @[Top.scala 831:22]
  wire  n274_valid_down; // @[Top.scala 831:22]
  wire [15:0] n274_I0; // @[Top.scala 831:22]
  wire [15:0] n274_I1; // @[Top.scala 831:22]
  wire [15:0] n274_O_t0b; // @[Top.scala 831:22]
  wire [15:0] n274_O_t1b; // @[Top.scala 831:22]
  wire  n275_valid_up; // @[Top.scala 835:22]
  wire  n275_valid_down; // @[Top.scala 835:22]
  wire [15:0] n275_I_t0b; // @[Top.scala 835:22]
  wire [15:0] n275_I_t1b; // @[Top.scala 835:22]
  wire [15:0] n275_O; // @[Top.scala 835:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n278_valid_up; // @[Top.scala 839:22]
  wire  n278_valid_down; // @[Top.scala 839:22]
  wire [15:0] n278_I0; // @[Top.scala 839:22]
  wire [15:0] n278_O_t0b; // @[Top.scala 839:22]
  wire  n279_valid_up; // @[Top.scala 843:22]
  wire  n279_valid_down; // @[Top.scala 843:22]
  wire [15:0] n279_I_t0b; // @[Top.scala 843:22]
  wire [15:0] n279_O; // @[Top.scala 843:22]
  wire  n280_valid_up; // @[Top.scala 846:22]
  wire  n280_valid_down; // @[Top.scala 846:22]
  wire [15:0] n280_I0; // @[Top.scala 846:22]
  wire [15:0] n280_I1; // @[Top.scala 846:22]
  wire [15:0] n280_O_t0b; // @[Top.scala 846:22]
  wire [15:0] n280_O_t1b; // @[Top.scala 846:22]
  wire  n281_clock; // @[Top.scala 850:22]
  wire  n281_reset; // @[Top.scala 850:22]
  wire  n281_valid_up; // @[Top.scala 850:22]
  wire  n281_valid_down; // @[Top.scala 850:22]
  wire [15:0] n281_I_t0b; // @[Top.scala 850:22]
  wire [15:0] n281_I_t1b; // @[Top.scala 850:22]
  wire [15:0] n281_O; // @[Top.scala 850:22]
  wire  n283_valid_up; // @[Top.scala 853:22]
  wire  n283_valid_down; // @[Top.scala 853:22]
  wire [15:0] n283_I0; // @[Top.scala 853:22]
  wire [15:0] n283_I1; // @[Top.scala 853:22]
  wire [15:0] n283_O_t0b; // @[Top.scala 853:22]
  wire [15:0] n283_O_t1b; // @[Top.scala 853:22]
  wire  n284_valid_up; // @[Top.scala 857:22]
  wire  n284_valid_down; // @[Top.scala 857:22]
  wire [15:0] n284_I_t0b; // @[Top.scala 857:22]
  wire [15:0] n284_I_t1b; // @[Top.scala 857:22]
  wire [15:0] n284_O; // @[Top.scala 857:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n286_valid_up; // @[Top.scala 861:22]
  wire  n286_valid_down; // @[Top.scala 861:22]
  wire [15:0] n286_I0; // @[Top.scala 861:22]
  wire [15:0] n286_I1; // @[Top.scala 861:22]
  wire [15:0] n286_O_t0b; // @[Top.scala 861:22]
  wire [15:0] n286_O_t1b; // @[Top.scala 861:22]
  wire  n287_valid_up; // @[Top.scala 865:22]
  wire  n287_valid_down; // @[Top.scala 865:22]
  wire [15:0] n287_I_t0b; // @[Top.scala 865:22]
  wire [15:0] n287_I_t1b; // @[Top.scala 865:22]
  wire [15:0] n287_O; // @[Top.scala 865:22]
  wire  n288_valid_up; // @[Top.scala 868:22]
  wire  n288_valid_down; // @[Top.scala 868:22]
  wire [15:0] n288_I0; // @[Top.scala 868:22]
  wire [15:0] n288_I1; // @[Top.scala 868:22]
  wire [15:0] n288_O_t0b; // @[Top.scala 868:22]
  wire [15:0] n288_O_t1b; // @[Top.scala 868:22]
  wire  n289_valid_up; // @[Top.scala 872:22]
  wire  n289_valid_down; // @[Top.scala 872:22]
  wire [15:0] n289_I0; // @[Top.scala 872:22]
  wire [15:0] n289_I1; // @[Top.scala 872:22]
  wire [15:0] n289_O_t0b; // @[Top.scala 872:22]
  wire [15:0] n289_O_t1b; // @[Top.scala 872:22]
  wire  n290_valid_up; // @[Top.scala 876:22]
  wire  n290_valid_down; // @[Top.scala 876:22]
  wire [15:0] n290_I0_t0b; // @[Top.scala 876:22]
  wire [15:0] n290_I0_t1b; // @[Top.scala 876:22]
  wire [15:0] n290_I1_t0b; // @[Top.scala 876:22]
  wire [15:0] n290_I1_t1b; // @[Top.scala 876:22]
  wire [15:0] n290_O_t0b_t0b; // @[Top.scala 876:22]
  wire [15:0] n290_O_t0b_t1b; // @[Top.scala 876:22]
  wire [15:0] n290_O_t1b_t0b; // @[Top.scala 876:22]
  wire [15:0] n290_O_t1b_t1b; // @[Top.scala 876:22]
  wire  n291_clock; // @[Top.scala 880:22]
  wire  n291_reset; // @[Top.scala 880:22]
  wire  n291_valid_up; // @[Top.scala 880:22]
  wire  n291_valid_down; // @[Top.scala 880:22]
  wire [15:0] n291_I_t0b_t0b; // @[Top.scala 880:22]
  wire [15:0] n291_I_t0b_t1b; // @[Top.scala 880:22]
  wire [15:0] n291_I_t1b_t0b; // @[Top.scala 880:22]
  wire [15:0] n291_I_t1b_t1b; // @[Top.scala 880:22]
  wire [15:0] n291_O_t0b_t0b; // @[Top.scala 880:22]
  wire [15:0] n291_O_t0b_t1b; // @[Top.scala 880:22]
  wire [15:0] n291_O_t1b_t0b; // @[Top.scala 880:22]
  wire [15:0] n291_O_t1b_t1b; // @[Top.scala 880:22]
  wire  n292_valid_up; // @[Top.scala 883:22]
  wire  n292_valid_down; // @[Top.scala 883:22]
  wire  n292_I0; // @[Top.scala 883:22]
  wire [15:0] n292_I1_t0b_t0b; // @[Top.scala 883:22]
  wire [15:0] n292_I1_t0b_t1b; // @[Top.scala 883:22]
  wire [15:0] n292_I1_t1b_t0b; // @[Top.scala 883:22]
  wire [15:0] n292_I1_t1b_t1b; // @[Top.scala 883:22]
  wire  n292_O_t0b; // @[Top.scala 883:22]
  wire [15:0] n292_O_t1b_t0b_t0b; // @[Top.scala 883:22]
  wire [15:0] n292_O_t1b_t0b_t1b; // @[Top.scala 883:22]
  wire [15:0] n292_O_t1b_t1b_t0b; // @[Top.scala 883:22]
  wire [15:0] n292_O_t1b_t1b_t1b; // @[Top.scala 883:22]
  wire  n293_valid_up; // @[Top.scala 887:22]
  wire  n293_valid_down; // @[Top.scala 887:22]
  wire  n293_I_t0b; // @[Top.scala 887:22]
  wire [15:0] n293_I_t1b_t0b_t0b; // @[Top.scala 887:22]
  wire [15:0] n293_I_t1b_t0b_t1b; // @[Top.scala 887:22]
  wire [15:0] n293_I_t1b_t1b_t0b; // @[Top.scala 887:22]
  wire [15:0] n293_I_t1b_t1b_t1b; // @[Top.scala 887:22]
  wire [15:0] n293_O_t0b; // @[Top.scala 887:22]
  wire [15:0] n293_O_t1b; // @[Top.scala 887:22]
  wire  n295_valid_up; // @[Top.scala 890:22]
  wire  n295_valid_down; // @[Top.scala 890:22]
  wire [15:0] n295_I0; // @[Top.scala 890:22]
  wire [15:0] n295_I1_t0b; // @[Top.scala 890:22]
  wire [15:0] n295_I1_t1b; // @[Top.scala 890:22]
  wire [15:0] n295_O_t0b; // @[Top.scala 890:22]
  wire [15:0] n295_O_t1b_t0b; // @[Top.scala 890:22]
  wire [15:0] n295_O_t1b_t1b; // @[Top.scala 890:22]
  Fst n266 ( // @[Top.scala 805:22]
    .valid_up(n266_valid_up),
    .valid_down(n266_valid_down),
    .I_t0b(n266_I_t0b),
    .O(n266_O)
  );
  FIFO_2 n294 ( // @[Top.scala 808:22]
    .clock(n294_clock),
    .reset(n294_reset),
    .valid_up(n294_valid_up),
    .valid_down(n294_valid_down),
    .I(n294_I),
    .O(n294_O)
  );
  FIFO_2 n282 ( // @[Top.scala 811:22]
    .clock(n282_clock),
    .reset(n282_reset),
    .valid_up(n282_valid_up),
    .valid_down(n282_valid_down),
    .I(n282_I),
    .O(n282_O)
  );
  InitialDelayCounter_24 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n268 ( // @[Top.scala 815:22]
    .valid_up(n268_valid_up),
    .valid_down(n268_valid_down),
    .I_t1b_t0b(n268_I_t1b_t0b),
    .I_t1b_t1b(n268_I_t1b_t1b),
    .O_t0b(n268_O_t0b),
    .O_t1b(n268_O_t1b)
  );
  Fst_1 n269 ( // @[Top.scala 818:22]
    .valid_up(n269_valid_up),
    .valid_down(n269_valid_down),
    .I_t0b(n269_I_t0b),
    .O(n269_O)
  );
  Snd_1 n270 ( // @[Top.scala 821:22]
    .valid_up(n270_valid_up),
    .valid_down(n270_valid_down),
    .I_t1b(n270_I_t1b),
    .O(n270_O)
  );
  AtomTuple n271 ( // @[Top.scala 824:22]
    .valid_up(n271_valid_up),
    .valid_down(n271_valid_down),
    .I0(n271_I0),
    .I1(n271_I1),
    .O_t0b(n271_O_t0b),
    .O_t1b(n271_O_t1b)
  );
  Add n272 ( // @[Top.scala 828:22]
    .valid_up(n272_valid_up),
    .valid_down(n272_valid_down),
    .I_t0b(n272_I_t0b),
    .I_t1b(n272_I_t1b),
    .O(n272_O)
  );
  AtomTuple n274 ( // @[Top.scala 831:22]
    .valid_up(n274_valid_up),
    .valid_down(n274_valid_down),
    .I0(n274_I0),
    .I1(n274_I1),
    .O_t0b(n274_O_t0b),
    .O_t1b(n274_O_t1b)
  );
  Add n275 ( // @[Top.scala 835:22]
    .valid_up(n275_valid_up),
    .valid_down(n275_valid_down),
    .I_t0b(n275_I_t0b),
    .I_t1b(n275_I_t1b),
    .O(n275_O)
  );
  InitialDelayCounter_24 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n278 ( // @[Top.scala 839:22]
    .valid_up(n278_valid_up),
    .valid_down(n278_valid_down),
    .I0(n278_I0),
    .O_t0b(n278_O_t0b)
  );
  RShift n279 ( // @[Top.scala 843:22]
    .valid_up(n279_valid_up),
    .valid_down(n279_valid_down),
    .I_t0b(n279_I_t0b),
    .O(n279_O)
  );
  AtomTuple n280 ( // @[Top.scala 846:22]
    .valid_up(n280_valid_up),
    .valid_down(n280_valid_down),
    .I0(n280_I0),
    .I1(n280_I1),
    .O_t0b(n280_O_t0b),
    .O_t1b(n280_O_t1b)
  );
  Mul n281 ( // @[Top.scala 850:22]
    .clock(n281_clock),
    .reset(n281_reset),
    .valid_up(n281_valid_up),
    .valid_down(n281_valid_down),
    .I_t0b(n281_I_t0b),
    .I_t1b(n281_I_t1b),
    .O(n281_O)
  );
  AtomTuple n283 ( // @[Top.scala 853:22]
    .valid_up(n283_valid_up),
    .valid_down(n283_valid_down),
    .I0(n283_I0),
    .I1(n283_I1),
    .O_t0b(n283_O_t0b),
    .O_t1b(n283_O_t1b)
  );
  Lt n284 ( // @[Top.scala 857:22]
    .valid_up(n284_valid_up),
    .valid_down(n284_valid_down),
    .I_t0b(n284_I_t0b),
    .I_t1b(n284_I_t1b),
    .O(n284_O)
  );
  InitialDelayCounter_24 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n286 ( // @[Top.scala 861:22]
    .valid_up(n286_valid_up),
    .valid_down(n286_valid_down),
    .I0(n286_I0),
    .I1(n286_I1),
    .O_t0b(n286_O_t0b),
    .O_t1b(n286_O_t1b)
  );
  Sub n287 ( // @[Top.scala 865:22]
    .valid_up(n287_valid_up),
    .valid_down(n287_valid_down),
    .I_t0b(n287_I_t0b),
    .I_t1b(n287_I_t1b),
    .O(n287_O)
  );
  AtomTuple n288 ( // @[Top.scala 868:22]
    .valid_up(n288_valid_up),
    .valid_down(n288_valid_down),
    .I0(n288_I0),
    .I1(n288_I1),
    .O_t0b(n288_O_t0b),
    .O_t1b(n288_O_t1b)
  );
  AtomTuple n289 ( // @[Top.scala 872:22]
    .valid_up(n289_valid_up),
    .valid_down(n289_valid_down),
    .I0(n289_I0),
    .I1(n289_I1),
    .O_t0b(n289_O_t0b),
    .O_t1b(n289_O_t1b)
  );
  AtomTuple_10 n290 ( // @[Top.scala 876:22]
    .valid_up(n290_valid_up),
    .valid_down(n290_valid_down),
    .I0_t0b(n290_I0_t0b),
    .I0_t1b(n290_I0_t1b),
    .I1_t0b(n290_I1_t0b),
    .I1_t1b(n290_I1_t1b),
    .O_t0b_t0b(n290_O_t0b_t0b),
    .O_t0b_t1b(n290_O_t0b_t1b),
    .O_t1b_t0b(n290_O_t1b_t0b),
    .O_t1b_t1b(n290_O_t1b_t1b)
  );
  FIFO_4 n291 ( // @[Top.scala 880:22]
    .clock(n291_clock),
    .reset(n291_reset),
    .valid_up(n291_valid_up),
    .valid_down(n291_valid_down),
    .I_t0b_t0b(n291_I_t0b_t0b),
    .I_t0b_t1b(n291_I_t0b_t1b),
    .I_t1b_t0b(n291_I_t1b_t0b),
    .I_t1b_t1b(n291_I_t1b_t1b),
    .O_t0b_t0b(n291_O_t0b_t0b),
    .O_t0b_t1b(n291_O_t0b_t1b),
    .O_t1b_t0b(n291_O_t1b_t0b),
    .O_t1b_t1b(n291_O_t1b_t1b)
  );
  AtomTuple_11 n292 ( // @[Top.scala 883:22]
    .valid_up(n292_valid_up),
    .valid_down(n292_valid_down),
    .I0(n292_I0),
    .I1_t0b_t0b(n292_I1_t0b_t0b),
    .I1_t0b_t1b(n292_I1_t0b_t1b),
    .I1_t1b_t0b(n292_I1_t1b_t0b),
    .I1_t1b_t1b(n292_I1_t1b_t1b),
    .O_t0b(n292_O_t0b),
    .O_t1b_t0b_t0b(n292_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n292_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n292_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n292_O_t1b_t1b_t1b)
  );
  If n293 ( // @[Top.scala 887:22]
    .valid_up(n293_valid_up),
    .valid_down(n293_valid_down),
    .I_t0b(n293_I_t0b),
    .I_t1b_t0b_t0b(n293_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n293_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n293_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n293_I_t1b_t1b_t1b),
    .O_t0b(n293_O_t0b),
    .O_t1b(n293_O_t1b)
  );
  AtomTuple_1 n295 ( // @[Top.scala 890:22]
    .valid_up(n295_valid_up),
    .valid_down(n295_valid_down),
    .I0(n295_I0),
    .I1_t0b(n295_I1_t0b),
    .I1_t1b(n295_I1_t1b),
    .O_t0b(n295_O_t0b),
    .O_t1b_t0b(n295_O_t1b_t0b),
    .O_t1b_t1b(n295_O_t1b_t1b)
  );
  assign valid_down = n295_valid_down; // @[Top.scala 895:16]
  assign O_t0b = n295_O_t0b; // @[Top.scala 894:7]
  assign O_t1b_t0b = n295_O_t1b_t0b; // @[Top.scala 894:7]
  assign O_t1b_t1b = n295_O_t1b_t1b; // @[Top.scala 894:7]
  assign n266_valid_up = valid_up; // @[Top.scala 807:19]
  assign n266_I_t0b = I_t0b; // @[Top.scala 806:12]
  assign n294_clock = clock;
  assign n294_reset = reset;
  assign n294_valid_up = n266_valid_down; // @[Top.scala 810:19]
  assign n294_I = n266_O; // @[Top.scala 809:12]
  assign n282_clock = clock;
  assign n282_reset = reset;
  assign n282_valid_up = n266_valid_down; // @[Top.scala 813:19]
  assign n282_I = n266_O; // @[Top.scala 812:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n268_valid_up = valid_up; // @[Top.scala 817:19]
  assign n268_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 816:12]
  assign n268_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 816:12]
  assign n269_valid_up = n268_valid_down; // @[Top.scala 820:19]
  assign n269_I_t0b = n268_O_t0b; // @[Top.scala 819:12]
  assign n270_valid_up = n268_valid_down; // @[Top.scala 823:19]
  assign n270_I_t1b = n268_O_t1b; // @[Top.scala 822:12]
  assign n271_valid_up = n269_valid_down & n270_valid_down; // @[Top.scala 827:19]
  assign n271_I0 = n269_O; // @[Top.scala 825:13]
  assign n271_I1 = n270_O; // @[Top.scala 826:13]
  assign n272_valid_up = n271_valid_down; // @[Top.scala 830:19]
  assign n272_I_t0b = n271_O_t0b; // @[Top.scala 829:12]
  assign n272_I_t1b = n271_O_t1b; // @[Top.scala 829:12]
  assign n274_valid_up = InitialDelayCounter_valid_down & n272_valid_down; // @[Top.scala 834:19]
  assign n274_I0 = 16'h1; // @[Top.scala 832:13]
  assign n274_I1 = n272_O; // @[Top.scala 833:13]
  assign n275_valid_up = n274_valid_down; // @[Top.scala 837:19]
  assign n275_I_t0b = n274_O_t0b; // @[Top.scala 836:12]
  assign n275_I_t1b = n274_O_t1b; // @[Top.scala 836:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n278_valid_up = n275_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 842:19]
  assign n278_I0 = n275_O; // @[Top.scala 840:13]
  assign n279_valid_up = n278_valid_down; // @[Top.scala 845:19]
  assign n279_I_t0b = n278_O_t0b; // @[Top.scala 844:12]
  assign n280_valid_up = n279_valid_down; // @[Top.scala 849:19]
  assign n280_I0 = n279_O; // @[Top.scala 847:13]
  assign n280_I1 = n279_O; // @[Top.scala 848:13]
  assign n281_clock = clock;
  assign n281_reset = reset;
  assign n281_valid_up = n280_valid_down; // @[Top.scala 852:19]
  assign n281_I_t0b = n280_O_t0b; // @[Top.scala 851:12]
  assign n281_I_t1b = n280_O_t1b; // @[Top.scala 851:12]
  assign n283_valid_up = n282_valid_down & n281_valid_down; // @[Top.scala 856:19]
  assign n283_I0 = n282_O; // @[Top.scala 854:13]
  assign n283_I1 = n281_O; // @[Top.scala 855:13]
  assign n284_valid_up = n283_valid_down; // @[Top.scala 859:19]
  assign n284_I_t0b = n283_O_t0b; // @[Top.scala 858:12]
  assign n284_I_t1b = n283_O_t1b; // @[Top.scala 858:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n286_valid_up = n279_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 864:19]
  assign n286_I0 = n279_O; // @[Top.scala 862:13]
  assign n286_I1 = 16'h1; // @[Top.scala 863:13]
  assign n287_valid_up = n286_valid_down; // @[Top.scala 867:19]
  assign n287_I_t0b = n286_O_t0b; // @[Top.scala 866:12]
  assign n287_I_t1b = n286_O_t1b; // @[Top.scala 866:12]
  assign n288_valid_up = n269_valid_down & n287_valid_down; // @[Top.scala 871:19]
  assign n288_I0 = n269_O; // @[Top.scala 869:13]
  assign n288_I1 = n287_O; // @[Top.scala 870:13]
  assign n289_valid_up = n279_valid_down & n270_valid_down; // @[Top.scala 875:19]
  assign n289_I0 = n279_O; // @[Top.scala 873:13]
  assign n289_I1 = n270_O; // @[Top.scala 874:13]
  assign n290_valid_up = n288_valid_down & n289_valid_down; // @[Top.scala 879:19]
  assign n290_I0_t0b = n288_O_t0b; // @[Top.scala 877:13]
  assign n290_I0_t1b = n288_O_t1b; // @[Top.scala 877:13]
  assign n290_I1_t0b = n289_O_t0b; // @[Top.scala 878:13]
  assign n290_I1_t1b = n289_O_t1b; // @[Top.scala 878:13]
  assign n291_clock = clock;
  assign n291_reset = reset;
  assign n291_valid_up = n290_valid_down; // @[Top.scala 882:19]
  assign n291_I_t0b_t0b = n290_O_t0b_t0b; // @[Top.scala 881:12]
  assign n291_I_t0b_t1b = n290_O_t0b_t1b; // @[Top.scala 881:12]
  assign n291_I_t1b_t0b = n290_O_t1b_t0b; // @[Top.scala 881:12]
  assign n291_I_t1b_t1b = n290_O_t1b_t1b; // @[Top.scala 881:12]
  assign n292_valid_up = n284_valid_down & n291_valid_down; // @[Top.scala 886:19]
  assign n292_I0 = n284_O[0]; // @[Top.scala 884:13]
  assign n292_I1_t0b_t0b = n291_O_t0b_t0b; // @[Top.scala 885:13]
  assign n292_I1_t0b_t1b = n291_O_t0b_t1b; // @[Top.scala 885:13]
  assign n292_I1_t1b_t0b = n291_O_t1b_t0b; // @[Top.scala 885:13]
  assign n292_I1_t1b_t1b = n291_O_t1b_t1b; // @[Top.scala 885:13]
  assign n293_valid_up = n292_valid_down; // @[Top.scala 889:19]
  assign n293_I_t0b = n292_O_t0b; // @[Top.scala 888:12]
  assign n293_I_t1b_t0b_t0b = n292_O_t1b_t0b_t0b; // @[Top.scala 888:12]
  assign n293_I_t1b_t0b_t1b = n292_O_t1b_t0b_t1b; // @[Top.scala 888:12]
  assign n293_I_t1b_t1b_t0b = n292_O_t1b_t1b_t0b; // @[Top.scala 888:12]
  assign n293_I_t1b_t1b_t1b = n292_O_t1b_t1b_t1b; // @[Top.scala 888:12]
  assign n295_valid_up = n294_valid_down & n293_valid_down; // @[Top.scala 893:19]
  assign n295_I0 = n294_O; // @[Top.scala 891:13]
  assign n295_I1_t0b = n293_O_t0b; // @[Top.scala 892:13]
  assign n295_I1_t1b = n293_O_t1b; // @[Top.scala 892:13]
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
module InitialDelayCounter_27(
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
  wire  n298_valid_up; // @[Top.scala 901:22]
  wire  n298_valid_down; // @[Top.scala 901:22]
  wire [15:0] n298_I_t0b; // @[Top.scala 901:22]
  wire [15:0] n298_O; // @[Top.scala 901:22]
  wire  n326_clock; // @[Top.scala 904:22]
  wire  n326_reset; // @[Top.scala 904:22]
  wire  n326_valid_up; // @[Top.scala 904:22]
  wire  n326_valid_down; // @[Top.scala 904:22]
  wire [15:0] n326_I; // @[Top.scala 904:22]
  wire [15:0] n326_O; // @[Top.scala 904:22]
  wire  n314_clock; // @[Top.scala 907:22]
  wire  n314_reset; // @[Top.scala 907:22]
  wire  n314_valid_up; // @[Top.scala 907:22]
  wire  n314_valid_down; // @[Top.scala 907:22]
  wire [15:0] n314_I; // @[Top.scala 907:22]
  wire [15:0] n314_O; // @[Top.scala 907:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n300_valid_up; // @[Top.scala 911:22]
  wire  n300_valid_down; // @[Top.scala 911:22]
  wire [15:0] n300_I_t1b_t0b; // @[Top.scala 911:22]
  wire [15:0] n300_I_t1b_t1b; // @[Top.scala 911:22]
  wire [15:0] n300_O_t0b; // @[Top.scala 911:22]
  wire [15:0] n300_O_t1b; // @[Top.scala 911:22]
  wire  n301_valid_up; // @[Top.scala 914:22]
  wire  n301_valid_down; // @[Top.scala 914:22]
  wire [15:0] n301_I_t0b; // @[Top.scala 914:22]
  wire [15:0] n301_O; // @[Top.scala 914:22]
  wire  n302_valid_up; // @[Top.scala 917:22]
  wire  n302_valid_down; // @[Top.scala 917:22]
  wire [15:0] n302_I_t1b; // @[Top.scala 917:22]
  wire [15:0] n302_O; // @[Top.scala 917:22]
  wire  n303_valid_up; // @[Top.scala 920:22]
  wire  n303_valid_down; // @[Top.scala 920:22]
  wire [15:0] n303_I0; // @[Top.scala 920:22]
  wire [15:0] n303_I1; // @[Top.scala 920:22]
  wire [15:0] n303_O_t0b; // @[Top.scala 920:22]
  wire [15:0] n303_O_t1b; // @[Top.scala 920:22]
  wire  n304_valid_up; // @[Top.scala 924:22]
  wire  n304_valid_down; // @[Top.scala 924:22]
  wire [15:0] n304_I_t0b; // @[Top.scala 924:22]
  wire [15:0] n304_I_t1b; // @[Top.scala 924:22]
  wire [15:0] n304_O; // @[Top.scala 924:22]
  wire  n306_valid_up; // @[Top.scala 927:22]
  wire  n306_valid_down; // @[Top.scala 927:22]
  wire [15:0] n306_I0; // @[Top.scala 927:22]
  wire [15:0] n306_I1; // @[Top.scala 927:22]
  wire [15:0] n306_O_t0b; // @[Top.scala 927:22]
  wire [15:0] n306_O_t1b; // @[Top.scala 927:22]
  wire  n307_valid_up; // @[Top.scala 931:22]
  wire  n307_valid_down; // @[Top.scala 931:22]
  wire [15:0] n307_I_t0b; // @[Top.scala 931:22]
  wire [15:0] n307_I_t1b; // @[Top.scala 931:22]
  wire [15:0] n307_O; // @[Top.scala 931:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n310_valid_up; // @[Top.scala 935:22]
  wire  n310_valid_down; // @[Top.scala 935:22]
  wire [15:0] n310_I0; // @[Top.scala 935:22]
  wire [15:0] n310_O_t0b; // @[Top.scala 935:22]
  wire  n311_valid_up; // @[Top.scala 939:22]
  wire  n311_valid_down; // @[Top.scala 939:22]
  wire [15:0] n311_I_t0b; // @[Top.scala 939:22]
  wire [15:0] n311_O; // @[Top.scala 939:22]
  wire  n312_valid_up; // @[Top.scala 942:22]
  wire  n312_valid_down; // @[Top.scala 942:22]
  wire [15:0] n312_I0; // @[Top.scala 942:22]
  wire [15:0] n312_I1; // @[Top.scala 942:22]
  wire [15:0] n312_O_t0b; // @[Top.scala 942:22]
  wire [15:0] n312_O_t1b; // @[Top.scala 942:22]
  wire  n313_clock; // @[Top.scala 946:22]
  wire  n313_reset; // @[Top.scala 946:22]
  wire  n313_valid_up; // @[Top.scala 946:22]
  wire  n313_valid_down; // @[Top.scala 946:22]
  wire [15:0] n313_I_t0b; // @[Top.scala 946:22]
  wire [15:0] n313_I_t1b; // @[Top.scala 946:22]
  wire [15:0] n313_O; // @[Top.scala 946:22]
  wire  n315_valid_up; // @[Top.scala 949:22]
  wire  n315_valid_down; // @[Top.scala 949:22]
  wire [15:0] n315_I0; // @[Top.scala 949:22]
  wire [15:0] n315_I1; // @[Top.scala 949:22]
  wire [15:0] n315_O_t0b; // @[Top.scala 949:22]
  wire [15:0] n315_O_t1b; // @[Top.scala 949:22]
  wire  n316_valid_up; // @[Top.scala 953:22]
  wire  n316_valid_down; // @[Top.scala 953:22]
  wire [15:0] n316_I_t0b; // @[Top.scala 953:22]
  wire [15:0] n316_I_t1b; // @[Top.scala 953:22]
  wire [15:0] n316_O; // @[Top.scala 953:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n318_valid_up; // @[Top.scala 957:22]
  wire  n318_valid_down; // @[Top.scala 957:22]
  wire [15:0] n318_I0; // @[Top.scala 957:22]
  wire [15:0] n318_I1; // @[Top.scala 957:22]
  wire [15:0] n318_O_t0b; // @[Top.scala 957:22]
  wire [15:0] n318_O_t1b; // @[Top.scala 957:22]
  wire  n319_valid_up; // @[Top.scala 961:22]
  wire  n319_valid_down; // @[Top.scala 961:22]
  wire [15:0] n319_I_t0b; // @[Top.scala 961:22]
  wire [15:0] n319_I_t1b; // @[Top.scala 961:22]
  wire [15:0] n319_O; // @[Top.scala 961:22]
  wire  n320_valid_up; // @[Top.scala 964:22]
  wire  n320_valid_down; // @[Top.scala 964:22]
  wire [15:0] n320_I0; // @[Top.scala 964:22]
  wire [15:0] n320_I1; // @[Top.scala 964:22]
  wire [15:0] n320_O_t0b; // @[Top.scala 964:22]
  wire [15:0] n320_O_t1b; // @[Top.scala 964:22]
  wire  n321_valid_up; // @[Top.scala 968:22]
  wire  n321_valid_down; // @[Top.scala 968:22]
  wire [15:0] n321_I0; // @[Top.scala 968:22]
  wire [15:0] n321_I1; // @[Top.scala 968:22]
  wire [15:0] n321_O_t0b; // @[Top.scala 968:22]
  wire [15:0] n321_O_t1b; // @[Top.scala 968:22]
  wire  n322_valid_up; // @[Top.scala 972:22]
  wire  n322_valid_down; // @[Top.scala 972:22]
  wire [15:0] n322_I0_t0b; // @[Top.scala 972:22]
  wire [15:0] n322_I0_t1b; // @[Top.scala 972:22]
  wire [15:0] n322_I1_t0b; // @[Top.scala 972:22]
  wire [15:0] n322_I1_t1b; // @[Top.scala 972:22]
  wire [15:0] n322_O_t0b_t0b; // @[Top.scala 972:22]
  wire [15:0] n322_O_t0b_t1b; // @[Top.scala 972:22]
  wire [15:0] n322_O_t1b_t0b; // @[Top.scala 972:22]
  wire [15:0] n322_O_t1b_t1b; // @[Top.scala 972:22]
  wire  n323_clock; // @[Top.scala 976:22]
  wire  n323_reset; // @[Top.scala 976:22]
  wire  n323_valid_up; // @[Top.scala 976:22]
  wire  n323_valid_down; // @[Top.scala 976:22]
  wire [15:0] n323_I_t0b_t0b; // @[Top.scala 976:22]
  wire [15:0] n323_I_t0b_t1b; // @[Top.scala 976:22]
  wire [15:0] n323_I_t1b_t0b; // @[Top.scala 976:22]
  wire [15:0] n323_I_t1b_t1b; // @[Top.scala 976:22]
  wire [15:0] n323_O_t0b_t0b; // @[Top.scala 976:22]
  wire [15:0] n323_O_t0b_t1b; // @[Top.scala 976:22]
  wire [15:0] n323_O_t1b_t0b; // @[Top.scala 976:22]
  wire [15:0] n323_O_t1b_t1b; // @[Top.scala 976:22]
  wire  n324_valid_up; // @[Top.scala 979:22]
  wire  n324_valid_down; // @[Top.scala 979:22]
  wire  n324_I0; // @[Top.scala 979:22]
  wire [15:0] n324_I1_t0b_t0b; // @[Top.scala 979:22]
  wire [15:0] n324_I1_t0b_t1b; // @[Top.scala 979:22]
  wire [15:0] n324_I1_t1b_t0b; // @[Top.scala 979:22]
  wire [15:0] n324_I1_t1b_t1b; // @[Top.scala 979:22]
  wire  n324_O_t0b; // @[Top.scala 979:22]
  wire [15:0] n324_O_t1b_t0b_t0b; // @[Top.scala 979:22]
  wire [15:0] n324_O_t1b_t0b_t1b; // @[Top.scala 979:22]
  wire [15:0] n324_O_t1b_t1b_t0b; // @[Top.scala 979:22]
  wire [15:0] n324_O_t1b_t1b_t1b; // @[Top.scala 979:22]
  wire  n325_valid_up; // @[Top.scala 983:22]
  wire  n325_valid_down; // @[Top.scala 983:22]
  wire  n325_I_t0b; // @[Top.scala 983:22]
  wire [15:0] n325_I_t1b_t0b_t0b; // @[Top.scala 983:22]
  wire [15:0] n325_I_t1b_t0b_t1b; // @[Top.scala 983:22]
  wire [15:0] n325_I_t1b_t1b_t0b; // @[Top.scala 983:22]
  wire [15:0] n325_I_t1b_t1b_t1b; // @[Top.scala 983:22]
  wire [15:0] n325_O_t0b; // @[Top.scala 983:22]
  wire [15:0] n325_O_t1b; // @[Top.scala 983:22]
  wire  n327_valid_up; // @[Top.scala 986:22]
  wire  n327_valid_down; // @[Top.scala 986:22]
  wire [15:0] n327_I0; // @[Top.scala 986:22]
  wire [15:0] n327_I1_t0b; // @[Top.scala 986:22]
  wire [15:0] n327_I1_t1b; // @[Top.scala 986:22]
  wire [15:0] n327_O_t0b; // @[Top.scala 986:22]
  wire [15:0] n327_O_t1b_t0b; // @[Top.scala 986:22]
  wire [15:0] n327_O_t1b_t1b; // @[Top.scala 986:22]
  Fst n298 ( // @[Top.scala 901:22]
    .valid_up(n298_valid_up),
    .valid_down(n298_valid_down),
    .I_t0b(n298_I_t0b),
    .O(n298_O)
  );
  FIFO_2 n326 ( // @[Top.scala 904:22]
    .clock(n326_clock),
    .reset(n326_reset),
    .valid_up(n326_valid_up),
    .valid_down(n326_valid_down),
    .I(n326_I),
    .O(n326_O)
  );
  FIFO_2 n314 ( // @[Top.scala 907:22]
    .clock(n314_clock),
    .reset(n314_reset),
    .valid_up(n314_valid_up),
    .valid_down(n314_valid_down),
    .I(n314_I),
    .O(n314_O)
  );
  InitialDelayCounter_27 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n300 ( // @[Top.scala 911:22]
    .valid_up(n300_valid_up),
    .valid_down(n300_valid_down),
    .I_t1b_t0b(n300_I_t1b_t0b),
    .I_t1b_t1b(n300_I_t1b_t1b),
    .O_t0b(n300_O_t0b),
    .O_t1b(n300_O_t1b)
  );
  Fst_1 n301 ( // @[Top.scala 914:22]
    .valid_up(n301_valid_up),
    .valid_down(n301_valid_down),
    .I_t0b(n301_I_t0b),
    .O(n301_O)
  );
  Snd_1 n302 ( // @[Top.scala 917:22]
    .valid_up(n302_valid_up),
    .valid_down(n302_valid_down),
    .I_t1b(n302_I_t1b),
    .O(n302_O)
  );
  AtomTuple n303 ( // @[Top.scala 920:22]
    .valid_up(n303_valid_up),
    .valid_down(n303_valid_down),
    .I0(n303_I0),
    .I1(n303_I1),
    .O_t0b(n303_O_t0b),
    .O_t1b(n303_O_t1b)
  );
  Add n304 ( // @[Top.scala 924:22]
    .valid_up(n304_valid_up),
    .valid_down(n304_valid_down),
    .I_t0b(n304_I_t0b),
    .I_t1b(n304_I_t1b),
    .O(n304_O)
  );
  AtomTuple n306 ( // @[Top.scala 927:22]
    .valid_up(n306_valid_up),
    .valid_down(n306_valid_down),
    .I0(n306_I0),
    .I1(n306_I1),
    .O_t0b(n306_O_t0b),
    .O_t1b(n306_O_t1b)
  );
  Add n307 ( // @[Top.scala 931:22]
    .valid_up(n307_valid_up),
    .valid_down(n307_valid_down),
    .I_t0b(n307_I_t0b),
    .I_t1b(n307_I_t1b),
    .O(n307_O)
  );
  InitialDelayCounter_27 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n310 ( // @[Top.scala 935:22]
    .valid_up(n310_valid_up),
    .valid_down(n310_valid_down),
    .I0(n310_I0),
    .O_t0b(n310_O_t0b)
  );
  RShift n311 ( // @[Top.scala 939:22]
    .valid_up(n311_valid_up),
    .valid_down(n311_valid_down),
    .I_t0b(n311_I_t0b),
    .O(n311_O)
  );
  AtomTuple n312 ( // @[Top.scala 942:22]
    .valid_up(n312_valid_up),
    .valid_down(n312_valid_down),
    .I0(n312_I0),
    .I1(n312_I1),
    .O_t0b(n312_O_t0b),
    .O_t1b(n312_O_t1b)
  );
  Mul n313 ( // @[Top.scala 946:22]
    .clock(n313_clock),
    .reset(n313_reset),
    .valid_up(n313_valid_up),
    .valid_down(n313_valid_down),
    .I_t0b(n313_I_t0b),
    .I_t1b(n313_I_t1b),
    .O(n313_O)
  );
  AtomTuple n315 ( // @[Top.scala 949:22]
    .valid_up(n315_valid_up),
    .valid_down(n315_valid_down),
    .I0(n315_I0),
    .I1(n315_I1),
    .O_t0b(n315_O_t0b),
    .O_t1b(n315_O_t1b)
  );
  Lt n316 ( // @[Top.scala 953:22]
    .valid_up(n316_valid_up),
    .valid_down(n316_valid_down),
    .I_t0b(n316_I_t0b),
    .I_t1b(n316_I_t1b),
    .O(n316_O)
  );
  InitialDelayCounter_27 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n318 ( // @[Top.scala 957:22]
    .valid_up(n318_valid_up),
    .valid_down(n318_valid_down),
    .I0(n318_I0),
    .I1(n318_I1),
    .O_t0b(n318_O_t0b),
    .O_t1b(n318_O_t1b)
  );
  Sub n319 ( // @[Top.scala 961:22]
    .valid_up(n319_valid_up),
    .valid_down(n319_valid_down),
    .I_t0b(n319_I_t0b),
    .I_t1b(n319_I_t1b),
    .O(n319_O)
  );
  AtomTuple n320 ( // @[Top.scala 964:22]
    .valid_up(n320_valid_up),
    .valid_down(n320_valid_down),
    .I0(n320_I0),
    .I1(n320_I1),
    .O_t0b(n320_O_t0b),
    .O_t1b(n320_O_t1b)
  );
  AtomTuple n321 ( // @[Top.scala 968:22]
    .valid_up(n321_valid_up),
    .valid_down(n321_valid_down),
    .I0(n321_I0),
    .I1(n321_I1),
    .O_t0b(n321_O_t0b),
    .O_t1b(n321_O_t1b)
  );
  AtomTuple_10 n322 ( // @[Top.scala 972:22]
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
  FIFO_4 n323 ( // @[Top.scala 976:22]
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
  AtomTuple_11 n324 ( // @[Top.scala 979:22]
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
  If n325 ( // @[Top.scala 983:22]
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
  AtomTuple_1 n327 ( // @[Top.scala 986:22]
    .valid_up(n327_valid_up),
    .valid_down(n327_valid_down),
    .I0(n327_I0),
    .I1_t0b(n327_I1_t0b),
    .I1_t1b(n327_I1_t1b),
    .O_t0b(n327_O_t0b),
    .O_t1b_t0b(n327_O_t1b_t0b),
    .O_t1b_t1b(n327_O_t1b_t1b)
  );
  assign valid_down = n327_valid_down; // @[Top.scala 991:16]
  assign O_t0b = n327_O_t0b; // @[Top.scala 990:7]
  assign O_t1b_t0b = n327_O_t1b_t0b; // @[Top.scala 990:7]
  assign O_t1b_t1b = n327_O_t1b_t1b; // @[Top.scala 990:7]
  assign n298_valid_up = valid_up; // @[Top.scala 903:19]
  assign n298_I_t0b = I_t0b; // @[Top.scala 902:12]
  assign n326_clock = clock;
  assign n326_reset = reset;
  assign n326_valid_up = n298_valid_down; // @[Top.scala 906:19]
  assign n326_I = n298_O; // @[Top.scala 905:12]
  assign n314_clock = clock;
  assign n314_reset = reset;
  assign n314_valid_up = n298_valid_down; // @[Top.scala 909:19]
  assign n314_I = n298_O; // @[Top.scala 908:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n300_valid_up = valid_up; // @[Top.scala 913:19]
  assign n300_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 912:12]
  assign n300_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 912:12]
  assign n301_valid_up = n300_valid_down; // @[Top.scala 916:19]
  assign n301_I_t0b = n300_O_t0b; // @[Top.scala 915:12]
  assign n302_valid_up = n300_valid_down; // @[Top.scala 919:19]
  assign n302_I_t1b = n300_O_t1b; // @[Top.scala 918:12]
  assign n303_valid_up = n301_valid_down & n302_valid_down; // @[Top.scala 923:19]
  assign n303_I0 = n301_O; // @[Top.scala 921:13]
  assign n303_I1 = n302_O; // @[Top.scala 922:13]
  assign n304_valid_up = n303_valid_down; // @[Top.scala 926:19]
  assign n304_I_t0b = n303_O_t0b; // @[Top.scala 925:12]
  assign n304_I_t1b = n303_O_t1b; // @[Top.scala 925:12]
  assign n306_valid_up = InitialDelayCounter_valid_down & n304_valid_down; // @[Top.scala 930:19]
  assign n306_I0 = 16'h1; // @[Top.scala 928:13]
  assign n306_I1 = n304_O; // @[Top.scala 929:13]
  assign n307_valid_up = n306_valid_down; // @[Top.scala 933:19]
  assign n307_I_t0b = n306_O_t0b; // @[Top.scala 932:12]
  assign n307_I_t1b = n306_O_t1b; // @[Top.scala 932:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n310_valid_up = n307_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 938:19]
  assign n310_I0 = n307_O; // @[Top.scala 936:13]
  assign n311_valid_up = n310_valid_down; // @[Top.scala 941:19]
  assign n311_I_t0b = n310_O_t0b; // @[Top.scala 940:12]
  assign n312_valid_up = n311_valid_down; // @[Top.scala 945:19]
  assign n312_I0 = n311_O; // @[Top.scala 943:13]
  assign n312_I1 = n311_O; // @[Top.scala 944:13]
  assign n313_clock = clock;
  assign n313_reset = reset;
  assign n313_valid_up = n312_valid_down; // @[Top.scala 948:19]
  assign n313_I_t0b = n312_O_t0b; // @[Top.scala 947:12]
  assign n313_I_t1b = n312_O_t1b; // @[Top.scala 947:12]
  assign n315_valid_up = n314_valid_down & n313_valid_down; // @[Top.scala 952:19]
  assign n315_I0 = n314_O; // @[Top.scala 950:13]
  assign n315_I1 = n313_O; // @[Top.scala 951:13]
  assign n316_valid_up = n315_valid_down; // @[Top.scala 955:19]
  assign n316_I_t0b = n315_O_t0b; // @[Top.scala 954:12]
  assign n316_I_t1b = n315_O_t1b; // @[Top.scala 954:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n318_valid_up = n311_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 960:19]
  assign n318_I0 = n311_O; // @[Top.scala 958:13]
  assign n318_I1 = 16'h1; // @[Top.scala 959:13]
  assign n319_valid_up = n318_valid_down; // @[Top.scala 963:19]
  assign n319_I_t0b = n318_O_t0b; // @[Top.scala 962:12]
  assign n319_I_t1b = n318_O_t1b; // @[Top.scala 962:12]
  assign n320_valid_up = n301_valid_down & n319_valid_down; // @[Top.scala 967:19]
  assign n320_I0 = n301_O; // @[Top.scala 965:13]
  assign n320_I1 = n319_O; // @[Top.scala 966:13]
  assign n321_valid_up = n311_valid_down & n302_valid_down; // @[Top.scala 971:19]
  assign n321_I0 = n311_O; // @[Top.scala 969:13]
  assign n321_I1 = n302_O; // @[Top.scala 970:13]
  assign n322_valid_up = n320_valid_down & n321_valid_down; // @[Top.scala 975:19]
  assign n322_I0_t0b = n320_O_t0b; // @[Top.scala 973:13]
  assign n322_I0_t1b = n320_O_t1b; // @[Top.scala 973:13]
  assign n322_I1_t0b = n321_O_t0b; // @[Top.scala 974:13]
  assign n322_I1_t1b = n321_O_t1b; // @[Top.scala 974:13]
  assign n323_clock = clock;
  assign n323_reset = reset;
  assign n323_valid_up = n322_valid_down; // @[Top.scala 978:19]
  assign n323_I_t0b_t0b = n322_O_t0b_t0b; // @[Top.scala 977:12]
  assign n323_I_t0b_t1b = n322_O_t0b_t1b; // @[Top.scala 977:12]
  assign n323_I_t1b_t0b = n322_O_t1b_t0b; // @[Top.scala 977:12]
  assign n323_I_t1b_t1b = n322_O_t1b_t1b; // @[Top.scala 977:12]
  assign n324_valid_up = n316_valid_down & n323_valid_down; // @[Top.scala 982:19]
  assign n324_I0 = n316_O[0]; // @[Top.scala 980:13]
  assign n324_I1_t0b_t0b = n323_O_t0b_t0b; // @[Top.scala 981:13]
  assign n324_I1_t0b_t1b = n323_O_t0b_t1b; // @[Top.scala 981:13]
  assign n324_I1_t1b_t0b = n323_O_t1b_t0b; // @[Top.scala 981:13]
  assign n324_I1_t1b_t1b = n323_O_t1b_t1b; // @[Top.scala 981:13]
  assign n325_valid_up = n324_valid_down; // @[Top.scala 985:19]
  assign n325_I_t0b = n324_O_t0b; // @[Top.scala 984:12]
  assign n325_I_t1b_t0b_t0b = n324_O_t1b_t0b_t0b; // @[Top.scala 984:12]
  assign n325_I_t1b_t0b_t1b = n324_O_t1b_t0b_t1b; // @[Top.scala 984:12]
  assign n325_I_t1b_t1b_t0b = n324_O_t1b_t1b_t0b; // @[Top.scala 984:12]
  assign n325_I_t1b_t1b_t1b = n324_O_t1b_t1b_t1b; // @[Top.scala 984:12]
  assign n327_valid_up = n326_valid_down & n325_valid_down; // @[Top.scala 989:19]
  assign n327_I0 = n326_O; // @[Top.scala 987:13]
  assign n327_I1_t0b = n325_O_t0b; // @[Top.scala 988:13]
  assign n327_I1_t1b = n325_O_t1b; // @[Top.scala 988:13]
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
module InitialDelayCounter_30(
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
  wire  n330_valid_up; // @[Top.scala 997:22]
  wire  n330_valid_down; // @[Top.scala 997:22]
  wire [15:0] n330_I_t0b; // @[Top.scala 997:22]
  wire [15:0] n330_O; // @[Top.scala 997:22]
  wire  n358_clock; // @[Top.scala 1000:22]
  wire  n358_reset; // @[Top.scala 1000:22]
  wire  n358_valid_up; // @[Top.scala 1000:22]
  wire  n358_valid_down; // @[Top.scala 1000:22]
  wire [15:0] n358_I; // @[Top.scala 1000:22]
  wire [15:0] n358_O; // @[Top.scala 1000:22]
  wire  n346_clock; // @[Top.scala 1003:22]
  wire  n346_reset; // @[Top.scala 1003:22]
  wire  n346_valid_up; // @[Top.scala 1003:22]
  wire  n346_valid_down; // @[Top.scala 1003:22]
  wire [15:0] n346_I; // @[Top.scala 1003:22]
  wire [15:0] n346_O; // @[Top.scala 1003:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n332_valid_up; // @[Top.scala 1007:22]
  wire  n332_valid_down; // @[Top.scala 1007:22]
  wire [15:0] n332_I_t1b_t0b; // @[Top.scala 1007:22]
  wire [15:0] n332_I_t1b_t1b; // @[Top.scala 1007:22]
  wire [15:0] n332_O_t0b; // @[Top.scala 1007:22]
  wire [15:0] n332_O_t1b; // @[Top.scala 1007:22]
  wire  n333_valid_up; // @[Top.scala 1010:22]
  wire  n333_valid_down; // @[Top.scala 1010:22]
  wire [15:0] n333_I_t0b; // @[Top.scala 1010:22]
  wire [15:0] n333_O; // @[Top.scala 1010:22]
  wire  n334_valid_up; // @[Top.scala 1013:22]
  wire  n334_valid_down; // @[Top.scala 1013:22]
  wire [15:0] n334_I_t1b; // @[Top.scala 1013:22]
  wire [15:0] n334_O; // @[Top.scala 1013:22]
  wire  n335_valid_up; // @[Top.scala 1016:22]
  wire  n335_valid_down; // @[Top.scala 1016:22]
  wire [15:0] n335_I0; // @[Top.scala 1016:22]
  wire [15:0] n335_I1; // @[Top.scala 1016:22]
  wire [15:0] n335_O_t0b; // @[Top.scala 1016:22]
  wire [15:0] n335_O_t1b; // @[Top.scala 1016:22]
  wire  n336_valid_up; // @[Top.scala 1020:22]
  wire  n336_valid_down; // @[Top.scala 1020:22]
  wire [15:0] n336_I_t0b; // @[Top.scala 1020:22]
  wire [15:0] n336_I_t1b; // @[Top.scala 1020:22]
  wire [15:0] n336_O; // @[Top.scala 1020:22]
  wire  n338_valid_up; // @[Top.scala 1023:22]
  wire  n338_valid_down; // @[Top.scala 1023:22]
  wire [15:0] n338_I0; // @[Top.scala 1023:22]
  wire [15:0] n338_I1; // @[Top.scala 1023:22]
  wire [15:0] n338_O_t0b; // @[Top.scala 1023:22]
  wire [15:0] n338_O_t1b; // @[Top.scala 1023:22]
  wire  n339_valid_up; // @[Top.scala 1027:22]
  wire  n339_valid_down; // @[Top.scala 1027:22]
  wire [15:0] n339_I_t0b; // @[Top.scala 1027:22]
  wire [15:0] n339_I_t1b; // @[Top.scala 1027:22]
  wire [15:0] n339_O; // @[Top.scala 1027:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n342_valid_up; // @[Top.scala 1031:22]
  wire  n342_valid_down; // @[Top.scala 1031:22]
  wire [15:0] n342_I0; // @[Top.scala 1031:22]
  wire [15:0] n342_O_t0b; // @[Top.scala 1031:22]
  wire  n343_valid_up; // @[Top.scala 1035:22]
  wire  n343_valid_down; // @[Top.scala 1035:22]
  wire [15:0] n343_I_t0b; // @[Top.scala 1035:22]
  wire [15:0] n343_O; // @[Top.scala 1035:22]
  wire  n344_valid_up; // @[Top.scala 1038:22]
  wire  n344_valid_down; // @[Top.scala 1038:22]
  wire [15:0] n344_I0; // @[Top.scala 1038:22]
  wire [15:0] n344_I1; // @[Top.scala 1038:22]
  wire [15:0] n344_O_t0b; // @[Top.scala 1038:22]
  wire [15:0] n344_O_t1b; // @[Top.scala 1038:22]
  wire  n345_clock; // @[Top.scala 1042:22]
  wire  n345_reset; // @[Top.scala 1042:22]
  wire  n345_valid_up; // @[Top.scala 1042:22]
  wire  n345_valid_down; // @[Top.scala 1042:22]
  wire [15:0] n345_I_t0b; // @[Top.scala 1042:22]
  wire [15:0] n345_I_t1b; // @[Top.scala 1042:22]
  wire [15:0] n345_O; // @[Top.scala 1042:22]
  wire  n347_valid_up; // @[Top.scala 1045:22]
  wire  n347_valid_down; // @[Top.scala 1045:22]
  wire [15:0] n347_I0; // @[Top.scala 1045:22]
  wire [15:0] n347_I1; // @[Top.scala 1045:22]
  wire [15:0] n347_O_t0b; // @[Top.scala 1045:22]
  wire [15:0] n347_O_t1b; // @[Top.scala 1045:22]
  wire  n348_valid_up; // @[Top.scala 1049:22]
  wire  n348_valid_down; // @[Top.scala 1049:22]
  wire [15:0] n348_I_t0b; // @[Top.scala 1049:22]
  wire [15:0] n348_I_t1b; // @[Top.scala 1049:22]
  wire [15:0] n348_O; // @[Top.scala 1049:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n350_valid_up; // @[Top.scala 1053:22]
  wire  n350_valid_down; // @[Top.scala 1053:22]
  wire [15:0] n350_I0; // @[Top.scala 1053:22]
  wire [15:0] n350_I1; // @[Top.scala 1053:22]
  wire [15:0] n350_O_t0b; // @[Top.scala 1053:22]
  wire [15:0] n350_O_t1b; // @[Top.scala 1053:22]
  wire  n351_valid_up; // @[Top.scala 1057:22]
  wire  n351_valid_down; // @[Top.scala 1057:22]
  wire [15:0] n351_I_t0b; // @[Top.scala 1057:22]
  wire [15:0] n351_I_t1b; // @[Top.scala 1057:22]
  wire [15:0] n351_O; // @[Top.scala 1057:22]
  wire  n352_valid_up; // @[Top.scala 1060:22]
  wire  n352_valid_down; // @[Top.scala 1060:22]
  wire [15:0] n352_I0; // @[Top.scala 1060:22]
  wire [15:0] n352_I1; // @[Top.scala 1060:22]
  wire [15:0] n352_O_t0b; // @[Top.scala 1060:22]
  wire [15:0] n352_O_t1b; // @[Top.scala 1060:22]
  wire  n353_valid_up; // @[Top.scala 1064:22]
  wire  n353_valid_down; // @[Top.scala 1064:22]
  wire [15:0] n353_I0; // @[Top.scala 1064:22]
  wire [15:0] n353_I1; // @[Top.scala 1064:22]
  wire [15:0] n353_O_t0b; // @[Top.scala 1064:22]
  wire [15:0] n353_O_t1b; // @[Top.scala 1064:22]
  wire  n354_valid_up; // @[Top.scala 1068:22]
  wire  n354_valid_down; // @[Top.scala 1068:22]
  wire [15:0] n354_I0_t0b; // @[Top.scala 1068:22]
  wire [15:0] n354_I0_t1b; // @[Top.scala 1068:22]
  wire [15:0] n354_I1_t0b; // @[Top.scala 1068:22]
  wire [15:0] n354_I1_t1b; // @[Top.scala 1068:22]
  wire [15:0] n354_O_t0b_t0b; // @[Top.scala 1068:22]
  wire [15:0] n354_O_t0b_t1b; // @[Top.scala 1068:22]
  wire [15:0] n354_O_t1b_t0b; // @[Top.scala 1068:22]
  wire [15:0] n354_O_t1b_t1b; // @[Top.scala 1068:22]
  wire  n355_clock; // @[Top.scala 1072:22]
  wire  n355_reset; // @[Top.scala 1072:22]
  wire  n355_valid_up; // @[Top.scala 1072:22]
  wire  n355_valid_down; // @[Top.scala 1072:22]
  wire [15:0] n355_I_t0b_t0b; // @[Top.scala 1072:22]
  wire [15:0] n355_I_t0b_t1b; // @[Top.scala 1072:22]
  wire [15:0] n355_I_t1b_t0b; // @[Top.scala 1072:22]
  wire [15:0] n355_I_t1b_t1b; // @[Top.scala 1072:22]
  wire [15:0] n355_O_t0b_t0b; // @[Top.scala 1072:22]
  wire [15:0] n355_O_t0b_t1b; // @[Top.scala 1072:22]
  wire [15:0] n355_O_t1b_t0b; // @[Top.scala 1072:22]
  wire [15:0] n355_O_t1b_t1b; // @[Top.scala 1072:22]
  wire  n356_valid_up; // @[Top.scala 1075:22]
  wire  n356_valid_down; // @[Top.scala 1075:22]
  wire  n356_I0; // @[Top.scala 1075:22]
  wire [15:0] n356_I1_t0b_t0b; // @[Top.scala 1075:22]
  wire [15:0] n356_I1_t0b_t1b; // @[Top.scala 1075:22]
  wire [15:0] n356_I1_t1b_t0b; // @[Top.scala 1075:22]
  wire [15:0] n356_I1_t1b_t1b; // @[Top.scala 1075:22]
  wire  n356_O_t0b; // @[Top.scala 1075:22]
  wire [15:0] n356_O_t1b_t0b_t0b; // @[Top.scala 1075:22]
  wire [15:0] n356_O_t1b_t0b_t1b; // @[Top.scala 1075:22]
  wire [15:0] n356_O_t1b_t1b_t0b; // @[Top.scala 1075:22]
  wire [15:0] n356_O_t1b_t1b_t1b; // @[Top.scala 1075:22]
  wire  n357_valid_up; // @[Top.scala 1079:22]
  wire  n357_valid_down; // @[Top.scala 1079:22]
  wire  n357_I_t0b; // @[Top.scala 1079:22]
  wire [15:0] n357_I_t1b_t0b_t0b; // @[Top.scala 1079:22]
  wire [15:0] n357_I_t1b_t0b_t1b; // @[Top.scala 1079:22]
  wire [15:0] n357_I_t1b_t1b_t0b; // @[Top.scala 1079:22]
  wire [15:0] n357_I_t1b_t1b_t1b; // @[Top.scala 1079:22]
  wire [15:0] n357_O_t0b; // @[Top.scala 1079:22]
  wire [15:0] n357_O_t1b; // @[Top.scala 1079:22]
  wire  n359_valid_up; // @[Top.scala 1082:22]
  wire  n359_valid_down; // @[Top.scala 1082:22]
  wire [15:0] n359_I0; // @[Top.scala 1082:22]
  wire [15:0] n359_I1_t0b; // @[Top.scala 1082:22]
  wire [15:0] n359_I1_t1b; // @[Top.scala 1082:22]
  wire [15:0] n359_O_t0b; // @[Top.scala 1082:22]
  wire [15:0] n359_O_t1b_t0b; // @[Top.scala 1082:22]
  wire [15:0] n359_O_t1b_t1b; // @[Top.scala 1082:22]
  Fst n330 ( // @[Top.scala 997:22]
    .valid_up(n330_valid_up),
    .valid_down(n330_valid_down),
    .I_t0b(n330_I_t0b),
    .O(n330_O)
  );
  FIFO_2 n358 ( // @[Top.scala 1000:22]
    .clock(n358_clock),
    .reset(n358_reset),
    .valid_up(n358_valid_up),
    .valid_down(n358_valid_down),
    .I(n358_I),
    .O(n358_O)
  );
  FIFO_2 n346 ( // @[Top.scala 1003:22]
    .clock(n346_clock),
    .reset(n346_reset),
    .valid_up(n346_valid_up),
    .valid_down(n346_valid_down),
    .I(n346_I),
    .O(n346_O)
  );
  InitialDelayCounter_30 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n332 ( // @[Top.scala 1007:22]
    .valid_up(n332_valid_up),
    .valid_down(n332_valid_down),
    .I_t1b_t0b(n332_I_t1b_t0b),
    .I_t1b_t1b(n332_I_t1b_t1b),
    .O_t0b(n332_O_t0b),
    .O_t1b(n332_O_t1b)
  );
  Fst_1 n333 ( // @[Top.scala 1010:22]
    .valid_up(n333_valid_up),
    .valid_down(n333_valid_down),
    .I_t0b(n333_I_t0b),
    .O(n333_O)
  );
  Snd_1 n334 ( // @[Top.scala 1013:22]
    .valid_up(n334_valid_up),
    .valid_down(n334_valid_down),
    .I_t1b(n334_I_t1b),
    .O(n334_O)
  );
  AtomTuple n335 ( // @[Top.scala 1016:22]
    .valid_up(n335_valid_up),
    .valid_down(n335_valid_down),
    .I0(n335_I0),
    .I1(n335_I1),
    .O_t0b(n335_O_t0b),
    .O_t1b(n335_O_t1b)
  );
  Add n336 ( // @[Top.scala 1020:22]
    .valid_up(n336_valid_up),
    .valid_down(n336_valid_down),
    .I_t0b(n336_I_t0b),
    .I_t1b(n336_I_t1b),
    .O(n336_O)
  );
  AtomTuple n338 ( // @[Top.scala 1023:22]
    .valid_up(n338_valid_up),
    .valid_down(n338_valid_down),
    .I0(n338_I0),
    .I1(n338_I1),
    .O_t0b(n338_O_t0b),
    .O_t1b(n338_O_t1b)
  );
  Add n339 ( // @[Top.scala 1027:22]
    .valid_up(n339_valid_up),
    .valid_down(n339_valid_down),
    .I_t0b(n339_I_t0b),
    .I_t1b(n339_I_t1b),
    .O(n339_O)
  );
  InitialDelayCounter_30 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n342 ( // @[Top.scala 1031:22]
    .valid_up(n342_valid_up),
    .valid_down(n342_valid_down),
    .I0(n342_I0),
    .O_t0b(n342_O_t0b)
  );
  RShift n343 ( // @[Top.scala 1035:22]
    .valid_up(n343_valid_up),
    .valid_down(n343_valid_down),
    .I_t0b(n343_I_t0b),
    .O(n343_O)
  );
  AtomTuple n344 ( // @[Top.scala 1038:22]
    .valid_up(n344_valid_up),
    .valid_down(n344_valid_down),
    .I0(n344_I0),
    .I1(n344_I1),
    .O_t0b(n344_O_t0b),
    .O_t1b(n344_O_t1b)
  );
  Mul n345 ( // @[Top.scala 1042:22]
    .clock(n345_clock),
    .reset(n345_reset),
    .valid_up(n345_valid_up),
    .valid_down(n345_valid_down),
    .I_t0b(n345_I_t0b),
    .I_t1b(n345_I_t1b),
    .O(n345_O)
  );
  AtomTuple n347 ( // @[Top.scala 1045:22]
    .valid_up(n347_valid_up),
    .valid_down(n347_valid_down),
    .I0(n347_I0),
    .I1(n347_I1),
    .O_t0b(n347_O_t0b),
    .O_t1b(n347_O_t1b)
  );
  Lt n348 ( // @[Top.scala 1049:22]
    .valid_up(n348_valid_up),
    .valid_down(n348_valid_down),
    .I_t0b(n348_I_t0b),
    .I_t1b(n348_I_t1b),
    .O(n348_O)
  );
  InitialDelayCounter_30 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n350 ( // @[Top.scala 1053:22]
    .valid_up(n350_valid_up),
    .valid_down(n350_valid_down),
    .I0(n350_I0),
    .I1(n350_I1),
    .O_t0b(n350_O_t0b),
    .O_t1b(n350_O_t1b)
  );
  Sub n351 ( // @[Top.scala 1057:22]
    .valid_up(n351_valid_up),
    .valid_down(n351_valid_down),
    .I_t0b(n351_I_t0b),
    .I_t1b(n351_I_t1b),
    .O(n351_O)
  );
  AtomTuple n352 ( // @[Top.scala 1060:22]
    .valid_up(n352_valid_up),
    .valid_down(n352_valid_down),
    .I0(n352_I0),
    .I1(n352_I1),
    .O_t0b(n352_O_t0b),
    .O_t1b(n352_O_t1b)
  );
  AtomTuple n353 ( // @[Top.scala 1064:22]
    .valid_up(n353_valid_up),
    .valid_down(n353_valid_down),
    .I0(n353_I0),
    .I1(n353_I1),
    .O_t0b(n353_O_t0b),
    .O_t1b(n353_O_t1b)
  );
  AtomTuple_10 n354 ( // @[Top.scala 1068:22]
    .valid_up(n354_valid_up),
    .valid_down(n354_valid_down),
    .I0_t0b(n354_I0_t0b),
    .I0_t1b(n354_I0_t1b),
    .I1_t0b(n354_I1_t0b),
    .I1_t1b(n354_I1_t1b),
    .O_t0b_t0b(n354_O_t0b_t0b),
    .O_t0b_t1b(n354_O_t0b_t1b),
    .O_t1b_t0b(n354_O_t1b_t0b),
    .O_t1b_t1b(n354_O_t1b_t1b)
  );
  FIFO_4 n355 ( // @[Top.scala 1072:22]
    .clock(n355_clock),
    .reset(n355_reset),
    .valid_up(n355_valid_up),
    .valid_down(n355_valid_down),
    .I_t0b_t0b(n355_I_t0b_t0b),
    .I_t0b_t1b(n355_I_t0b_t1b),
    .I_t1b_t0b(n355_I_t1b_t0b),
    .I_t1b_t1b(n355_I_t1b_t1b),
    .O_t0b_t0b(n355_O_t0b_t0b),
    .O_t0b_t1b(n355_O_t0b_t1b),
    .O_t1b_t0b(n355_O_t1b_t0b),
    .O_t1b_t1b(n355_O_t1b_t1b)
  );
  AtomTuple_11 n356 ( // @[Top.scala 1075:22]
    .valid_up(n356_valid_up),
    .valid_down(n356_valid_down),
    .I0(n356_I0),
    .I1_t0b_t0b(n356_I1_t0b_t0b),
    .I1_t0b_t1b(n356_I1_t0b_t1b),
    .I1_t1b_t0b(n356_I1_t1b_t0b),
    .I1_t1b_t1b(n356_I1_t1b_t1b),
    .O_t0b(n356_O_t0b),
    .O_t1b_t0b_t0b(n356_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n356_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n356_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n356_O_t1b_t1b_t1b)
  );
  If n357 ( // @[Top.scala 1079:22]
    .valid_up(n357_valid_up),
    .valid_down(n357_valid_down),
    .I_t0b(n357_I_t0b),
    .I_t1b_t0b_t0b(n357_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n357_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n357_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n357_I_t1b_t1b_t1b),
    .O_t0b(n357_O_t0b),
    .O_t1b(n357_O_t1b)
  );
  AtomTuple_1 n359 ( // @[Top.scala 1082:22]
    .valid_up(n359_valid_up),
    .valid_down(n359_valid_down),
    .I0(n359_I0),
    .I1_t0b(n359_I1_t0b),
    .I1_t1b(n359_I1_t1b),
    .O_t0b(n359_O_t0b),
    .O_t1b_t0b(n359_O_t1b_t0b),
    .O_t1b_t1b(n359_O_t1b_t1b)
  );
  assign valid_down = n359_valid_down; // @[Top.scala 1087:16]
  assign O_t0b = n359_O_t0b; // @[Top.scala 1086:7]
  assign O_t1b_t0b = n359_O_t1b_t0b; // @[Top.scala 1086:7]
  assign O_t1b_t1b = n359_O_t1b_t1b; // @[Top.scala 1086:7]
  assign n330_valid_up = valid_up; // @[Top.scala 999:19]
  assign n330_I_t0b = I_t0b; // @[Top.scala 998:12]
  assign n358_clock = clock;
  assign n358_reset = reset;
  assign n358_valid_up = n330_valid_down; // @[Top.scala 1002:19]
  assign n358_I = n330_O; // @[Top.scala 1001:12]
  assign n346_clock = clock;
  assign n346_reset = reset;
  assign n346_valid_up = n330_valid_down; // @[Top.scala 1005:19]
  assign n346_I = n330_O; // @[Top.scala 1004:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n332_valid_up = valid_up; // @[Top.scala 1009:19]
  assign n332_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1008:12]
  assign n332_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1008:12]
  assign n333_valid_up = n332_valid_down; // @[Top.scala 1012:19]
  assign n333_I_t0b = n332_O_t0b; // @[Top.scala 1011:12]
  assign n334_valid_up = n332_valid_down; // @[Top.scala 1015:19]
  assign n334_I_t1b = n332_O_t1b; // @[Top.scala 1014:12]
  assign n335_valid_up = n333_valid_down & n334_valid_down; // @[Top.scala 1019:19]
  assign n335_I0 = n333_O; // @[Top.scala 1017:13]
  assign n335_I1 = n334_O; // @[Top.scala 1018:13]
  assign n336_valid_up = n335_valid_down; // @[Top.scala 1022:19]
  assign n336_I_t0b = n335_O_t0b; // @[Top.scala 1021:12]
  assign n336_I_t1b = n335_O_t1b; // @[Top.scala 1021:12]
  assign n338_valid_up = InitialDelayCounter_valid_down & n336_valid_down; // @[Top.scala 1026:19]
  assign n338_I0 = 16'h1; // @[Top.scala 1024:13]
  assign n338_I1 = n336_O; // @[Top.scala 1025:13]
  assign n339_valid_up = n338_valid_down; // @[Top.scala 1029:19]
  assign n339_I_t0b = n338_O_t0b; // @[Top.scala 1028:12]
  assign n339_I_t1b = n338_O_t1b; // @[Top.scala 1028:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n342_valid_up = n339_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1034:19]
  assign n342_I0 = n339_O; // @[Top.scala 1032:13]
  assign n343_valid_up = n342_valid_down; // @[Top.scala 1037:19]
  assign n343_I_t0b = n342_O_t0b; // @[Top.scala 1036:12]
  assign n344_valid_up = n343_valid_down; // @[Top.scala 1041:19]
  assign n344_I0 = n343_O; // @[Top.scala 1039:13]
  assign n344_I1 = n343_O; // @[Top.scala 1040:13]
  assign n345_clock = clock;
  assign n345_reset = reset;
  assign n345_valid_up = n344_valid_down; // @[Top.scala 1044:19]
  assign n345_I_t0b = n344_O_t0b; // @[Top.scala 1043:12]
  assign n345_I_t1b = n344_O_t1b; // @[Top.scala 1043:12]
  assign n347_valid_up = n346_valid_down & n345_valid_down; // @[Top.scala 1048:19]
  assign n347_I0 = n346_O; // @[Top.scala 1046:13]
  assign n347_I1 = n345_O; // @[Top.scala 1047:13]
  assign n348_valid_up = n347_valid_down; // @[Top.scala 1051:19]
  assign n348_I_t0b = n347_O_t0b; // @[Top.scala 1050:12]
  assign n348_I_t1b = n347_O_t1b; // @[Top.scala 1050:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n350_valid_up = n343_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1056:19]
  assign n350_I0 = n343_O; // @[Top.scala 1054:13]
  assign n350_I1 = 16'h1; // @[Top.scala 1055:13]
  assign n351_valid_up = n350_valid_down; // @[Top.scala 1059:19]
  assign n351_I_t0b = n350_O_t0b; // @[Top.scala 1058:12]
  assign n351_I_t1b = n350_O_t1b; // @[Top.scala 1058:12]
  assign n352_valid_up = n333_valid_down & n351_valid_down; // @[Top.scala 1063:19]
  assign n352_I0 = n333_O; // @[Top.scala 1061:13]
  assign n352_I1 = n351_O; // @[Top.scala 1062:13]
  assign n353_valid_up = n343_valid_down & n334_valid_down; // @[Top.scala 1067:19]
  assign n353_I0 = n343_O; // @[Top.scala 1065:13]
  assign n353_I1 = n334_O; // @[Top.scala 1066:13]
  assign n354_valid_up = n352_valid_down & n353_valid_down; // @[Top.scala 1071:19]
  assign n354_I0_t0b = n352_O_t0b; // @[Top.scala 1069:13]
  assign n354_I0_t1b = n352_O_t1b; // @[Top.scala 1069:13]
  assign n354_I1_t0b = n353_O_t0b; // @[Top.scala 1070:13]
  assign n354_I1_t1b = n353_O_t1b; // @[Top.scala 1070:13]
  assign n355_clock = clock;
  assign n355_reset = reset;
  assign n355_valid_up = n354_valid_down; // @[Top.scala 1074:19]
  assign n355_I_t0b_t0b = n354_O_t0b_t0b; // @[Top.scala 1073:12]
  assign n355_I_t0b_t1b = n354_O_t0b_t1b; // @[Top.scala 1073:12]
  assign n355_I_t1b_t0b = n354_O_t1b_t0b; // @[Top.scala 1073:12]
  assign n355_I_t1b_t1b = n354_O_t1b_t1b; // @[Top.scala 1073:12]
  assign n356_valid_up = n348_valid_down & n355_valid_down; // @[Top.scala 1078:19]
  assign n356_I0 = n348_O[0]; // @[Top.scala 1076:13]
  assign n356_I1_t0b_t0b = n355_O_t0b_t0b; // @[Top.scala 1077:13]
  assign n356_I1_t0b_t1b = n355_O_t0b_t1b; // @[Top.scala 1077:13]
  assign n356_I1_t1b_t0b = n355_O_t1b_t0b; // @[Top.scala 1077:13]
  assign n356_I1_t1b_t1b = n355_O_t1b_t1b; // @[Top.scala 1077:13]
  assign n357_valid_up = n356_valid_down; // @[Top.scala 1081:19]
  assign n357_I_t0b = n356_O_t0b; // @[Top.scala 1080:12]
  assign n357_I_t1b_t0b_t0b = n356_O_t1b_t0b_t0b; // @[Top.scala 1080:12]
  assign n357_I_t1b_t0b_t1b = n356_O_t1b_t0b_t1b; // @[Top.scala 1080:12]
  assign n357_I_t1b_t1b_t0b = n356_O_t1b_t1b_t0b; // @[Top.scala 1080:12]
  assign n357_I_t1b_t1b_t1b = n356_O_t1b_t1b_t1b; // @[Top.scala 1080:12]
  assign n359_valid_up = n358_valid_down & n357_valid_down; // @[Top.scala 1085:19]
  assign n359_I0 = n358_O; // @[Top.scala 1083:13]
  assign n359_I1_t0b = n357_O_t0b; // @[Top.scala 1084:13]
  assign n359_I1_t1b = n357_O_t1b; // @[Top.scala 1084:13]
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
module InitialDelayCounter_33(
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
  wire  n362_valid_up; // @[Top.scala 1093:22]
  wire  n362_valid_down; // @[Top.scala 1093:22]
  wire [15:0] n362_I_t0b; // @[Top.scala 1093:22]
  wire [15:0] n362_O; // @[Top.scala 1093:22]
  wire  n390_clock; // @[Top.scala 1096:22]
  wire  n390_reset; // @[Top.scala 1096:22]
  wire  n390_valid_up; // @[Top.scala 1096:22]
  wire  n390_valid_down; // @[Top.scala 1096:22]
  wire [15:0] n390_I; // @[Top.scala 1096:22]
  wire [15:0] n390_O; // @[Top.scala 1096:22]
  wire  n378_clock; // @[Top.scala 1099:22]
  wire  n378_reset; // @[Top.scala 1099:22]
  wire  n378_valid_up; // @[Top.scala 1099:22]
  wire  n378_valid_down; // @[Top.scala 1099:22]
  wire [15:0] n378_I; // @[Top.scala 1099:22]
  wire [15:0] n378_O; // @[Top.scala 1099:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n364_valid_up; // @[Top.scala 1103:22]
  wire  n364_valid_down; // @[Top.scala 1103:22]
  wire [15:0] n364_I_t1b_t0b; // @[Top.scala 1103:22]
  wire [15:0] n364_I_t1b_t1b; // @[Top.scala 1103:22]
  wire [15:0] n364_O_t0b; // @[Top.scala 1103:22]
  wire [15:0] n364_O_t1b; // @[Top.scala 1103:22]
  wire  n365_valid_up; // @[Top.scala 1106:22]
  wire  n365_valid_down; // @[Top.scala 1106:22]
  wire [15:0] n365_I_t0b; // @[Top.scala 1106:22]
  wire [15:0] n365_O; // @[Top.scala 1106:22]
  wire  n366_valid_up; // @[Top.scala 1109:22]
  wire  n366_valid_down; // @[Top.scala 1109:22]
  wire [15:0] n366_I_t1b; // @[Top.scala 1109:22]
  wire [15:0] n366_O; // @[Top.scala 1109:22]
  wire  n367_valid_up; // @[Top.scala 1112:22]
  wire  n367_valid_down; // @[Top.scala 1112:22]
  wire [15:0] n367_I0; // @[Top.scala 1112:22]
  wire [15:0] n367_I1; // @[Top.scala 1112:22]
  wire [15:0] n367_O_t0b; // @[Top.scala 1112:22]
  wire [15:0] n367_O_t1b; // @[Top.scala 1112:22]
  wire  n368_valid_up; // @[Top.scala 1116:22]
  wire  n368_valid_down; // @[Top.scala 1116:22]
  wire [15:0] n368_I_t0b; // @[Top.scala 1116:22]
  wire [15:0] n368_I_t1b; // @[Top.scala 1116:22]
  wire [15:0] n368_O; // @[Top.scala 1116:22]
  wire  n370_valid_up; // @[Top.scala 1119:22]
  wire  n370_valid_down; // @[Top.scala 1119:22]
  wire [15:0] n370_I0; // @[Top.scala 1119:22]
  wire [15:0] n370_I1; // @[Top.scala 1119:22]
  wire [15:0] n370_O_t0b; // @[Top.scala 1119:22]
  wire [15:0] n370_O_t1b; // @[Top.scala 1119:22]
  wire  n371_valid_up; // @[Top.scala 1123:22]
  wire  n371_valid_down; // @[Top.scala 1123:22]
  wire [15:0] n371_I_t0b; // @[Top.scala 1123:22]
  wire [15:0] n371_I_t1b; // @[Top.scala 1123:22]
  wire [15:0] n371_O; // @[Top.scala 1123:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n374_valid_up; // @[Top.scala 1127:22]
  wire  n374_valid_down; // @[Top.scala 1127:22]
  wire [15:0] n374_I0; // @[Top.scala 1127:22]
  wire [15:0] n374_O_t0b; // @[Top.scala 1127:22]
  wire  n375_valid_up; // @[Top.scala 1131:22]
  wire  n375_valid_down; // @[Top.scala 1131:22]
  wire [15:0] n375_I_t0b; // @[Top.scala 1131:22]
  wire [15:0] n375_O; // @[Top.scala 1131:22]
  wire  n376_valid_up; // @[Top.scala 1134:22]
  wire  n376_valid_down; // @[Top.scala 1134:22]
  wire [15:0] n376_I0; // @[Top.scala 1134:22]
  wire [15:0] n376_I1; // @[Top.scala 1134:22]
  wire [15:0] n376_O_t0b; // @[Top.scala 1134:22]
  wire [15:0] n376_O_t1b; // @[Top.scala 1134:22]
  wire  n377_clock; // @[Top.scala 1138:22]
  wire  n377_reset; // @[Top.scala 1138:22]
  wire  n377_valid_up; // @[Top.scala 1138:22]
  wire  n377_valid_down; // @[Top.scala 1138:22]
  wire [15:0] n377_I_t0b; // @[Top.scala 1138:22]
  wire [15:0] n377_I_t1b; // @[Top.scala 1138:22]
  wire [15:0] n377_O; // @[Top.scala 1138:22]
  wire  n379_valid_up; // @[Top.scala 1141:22]
  wire  n379_valid_down; // @[Top.scala 1141:22]
  wire [15:0] n379_I0; // @[Top.scala 1141:22]
  wire [15:0] n379_I1; // @[Top.scala 1141:22]
  wire [15:0] n379_O_t0b; // @[Top.scala 1141:22]
  wire [15:0] n379_O_t1b; // @[Top.scala 1141:22]
  wire  n380_valid_up; // @[Top.scala 1145:22]
  wire  n380_valid_down; // @[Top.scala 1145:22]
  wire [15:0] n380_I_t0b; // @[Top.scala 1145:22]
  wire [15:0] n380_I_t1b; // @[Top.scala 1145:22]
  wire [15:0] n380_O; // @[Top.scala 1145:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n382_valid_up; // @[Top.scala 1149:22]
  wire  n382_valid_down; // @[Top.scala 1149:22]
  wire [15:0] n382_I0; // @[Top.scala 1149:22]
  wire [15:0] n382_I1; // @[Top.scala 1149:22]
  wire [15:0] n382_O_t0b; // @[Top.scala 1149:22]
  wire [15:0] n382_O_t1b; // @[Top.scala 1149:22]
  wire  n383_valid_up; // @[Top.scala 1153:22]
  wire  n383_valid_down; // @[Top.scala 1153:22]
  wire [15:0] n383_I_t0b; // @[Top.scala 1153:22]
  wire [15:0] n383_I_t1b; // @[Top.scala 1153:22]
  wire [15:0] n383_O; // @[Top.scala 1153:22]
  wire  n384_valid_up; // @[Top.scala 1156:22]
  wire  n384_valid_down; // @[Top.scala 1156:22]
  wire [15:0] n384_I0; // @[Top.scala 1156:22]
  wire [15:0] n384_I1; // @[Top.scala 1156:22]
  wire [15:0] n384_O_t0b; // @[Top.scala 1156:22]
  wire [15:0] n384_O_t1b; // @[Top.scala 1156:22]
  wire  n385_valid_up; // @[Top.scala 1160:22]
  wire  n385_valid_down; // @[Top.scala 1160:22]
  wire [15:0] n385_I0; // @[Top.scala 1160:22]
  wire [15:0] n385_I1; // @[Top.scala 1160:22]
  wire [15:0] n385_O_t0b; // @[Top.scala 1160:22]
  wire [15:0] n385_O_t1b; // @[Top.scala 1160:22]
  wire  n386_valid_up; // @[Top.scala 1164:22]
  wire  n386_valid_down; // @[Top.scala 1164:22]
  wire [15:0] n386_I0_t0b; // @[Top.scala 1164:22]
  wire [15:0] n386_I0_t1b; // @[Top.scala 1164:22]
  wire [15:0] n386_I1_t0b; // @[Top.scala 1164:22]
  wire [15:0] n386_I1_t1b; // @[Top.scala 1164:22]
  wire [15:0] n386_O_t0b_t0b; // @[Top.scala 1164:22]
  wire [15:0] n386_O_t0b_t1b; // @[Top.scala 1164:22]
  wire [15:0] n386_O_t1b_t0b; // @[Top.scala 1164:22]
  wire [15:0] n386_O_t1b_t1b; // @[Top.scala 1164:22]
  wire  n387_clock; // @[Top.scala 1168:22]
  wire  n387_reset; // @[Top.scala 1168:22]
  wire  n387_valid_up; // @[Top.scala 1168:22]
  wire  n387_valid_down; // @[Top.scala 1168:22]
  wire [15:0] n387_I_t0b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n387_I_t0b_t1b; // @[Top.scala 1168:22]
  wire [15:0] n387_I_t1b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n387_I_t1b_t1b; // @[Top.scala 1168:22]
  wire [15:0] n387_O_t0b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n387_O_t0b_t1b; // @[Top.scala 1168:22]
  wire [15:0] n387_O_t1b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n387_O_t1b_t1b; // @[Top.scala 1168:22]
  wire  n388_valid_up; // @[Top.scala 1171:22]
  wire  n388_valid_down; // @[Top.scala 1171:22]
  wire  n388_I0; // @[Top.scala 1171:22]
  wire [15:0] n388_I1_t0b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n388_I1_t0b_t1b; // @[Top.scala 1171:22]
  wire [15:0] n388_I1_t1b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n388_I1_t1b_t1b; // @[Top.scala 1171:22]
  wire  n388_O_t0b; // @[Top.scala 1171:22]
  wire [15:0] n388_O_t1b_t0b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n388_O_t1b_t0b_t1b; // @[Top.scala 1171:22]
  wire [15:0] n388_O_t1b_t1b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n388_O_t1b_t1b_t1b; // @[Top.scala 1171:22]
  wire  n389_valid_up; // @[Top.scala 1175:22]
  wire  n389_valid_down; // @[Top.scala 1175:22]
  wire  n389_I_t0b; // @[Top.scala 1175:22]
  wire [15:0] n389_I_t1b_t0b_t0b; // @[Top.scala 1175:22]
  wire [15:0] n389_I_t1b_t0b_t1b; // @[Top.scala 1175:22]
  wire [15:0] n389_I_t1b_t1b_t0b; // @[Top.scala 1175:22]
  wire [15:0] n389_I_t1b_t1b_t1b; // @[Top.scala 1175:22]
  wire [15:0] n389_O_t0b; // @[Top.scala 1175:22]
  wire [15:0] n389_O_t1b; // @[Top.scala 1175:22]
  wire  n391_valid_up; // @[Top.scala 1178:22]
  wire  n391_valid_down; // @[Top.scala 1178:22]
  wire [15:0] n391_I0; // @[Top.scala 1178:22]
  wire [15:0] n391_I1_t0b; // @[Top.scala 1178:22]
  wire [15:0] n391_I1_t1b; // @[Top.scala 1178:22]
  wire [15:0] n391_O_t0b; // @[Top.scala 1178:22]
  wire [15:0] n391_O_t1b_t0b; // @[Top.scala 1178:22]
  wire [15:0] n391_O_t1b_t1b; // @[Top.scala 1178:22]
  Fst n362 ( // @[Top.scala 1093:22]
    .valid_up(n362_valid_up),
    .valid_down(n362_valid_down),
    .I_t0b(n362_I_t0b),
    .O(n362_O)
  );
  FIFO_2 n390 ( // @[Top.scala 1096:22]
    .clock(n390_clock),
    .reset(n390_reset),
    .valid_up(n390_valid_up),
    .valid_down(n390_valid_down),
    .I(n390_I),
    .O(n390_O)
  );
  FIFO_2 n378 ( // @[Top.scala 1099:22]
    .clock(n378_clock),
    .reset(n378_reset),
    .valid_up(n378_valid_up),
    .valid_down(n378_valid_down),
    .I(n378_I),
    .O(n378_O)
  );
  InitialDelayCounter_33 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n364 ( // @[Top.scala 1103:22]
    .valid_up(n364_valid_up),
    .valid_down(n364_valid_down),
    .I_t1b_t0b(n364_I_t1b_t0b),
    .I_t1b_t1b(n364_I_t1b_t1b),
    .O_t0b(n364_O_t0b),
    .O_t1b(n364_O_t1b)
  );
  Fst_1 n365 ( // @[Top.scala 1106:22]
    .valid_up(n365_valid_up),
    .valid_down(n365_valid_down),
    .I_t0b(n365_I_t0b),
    .O(n365_O)
  );
  Snd_1 n366 ( // @[Top.scala 1109:22]
    .valid_up(n366_valid_up),
    .valid_down(n366_valid_down),
    .I_t1b(n366_I_t1b),
    .O(n366_O)
  );
  AtomTuple n367 ( // @[Top.scala 1112:22]
    .valid_up(n367_valid_up),
    .valid_down(n367_valid_down),
    .I0(n367_I0),
    .I1(n367_I1),
    .O_t0b(n367_O_t0b),
    .O_t1b(n367_O_t1b)
  );
  Add n368 ( // @[Top.scala 1116:22]
    .valid_up(n368_valid_up),
    .valid_down(n368_valid_down),
    .I_t0b(n368_I_t0b),
    .I_t1b(n368_I_t1b),
    .O(n368_O)
  );
  AtomTuple n370 ( // @[Top.scala 1119:22]
    .valid_up(n370_valid_up),
    .valid_down(n370_valid_down),
    .I0(n370_I0),
    .I1(n370_I1),
    .O_t0b(n370_O_t0b),
    .O_t1b(n370_O_t1b)
  );
  Add n371 ( // @[Top.scala 1123:22]
    .valid_up(n371_valid_up),
    .valid_down(n371_valid_down),
    .I_t0b(n371_I_t0b),
    .I_t1b(n371_I_t1b),
    .O(n371_O)
  );
  InitialDelayCounter_33 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n374 ( // @[Top.scala 1127:22]
    .valid_up(n374_valid_up),
    .valid_down(n374_valid_down),
    .I0(n374_I0),
    .O_t0b(n374_O_t0b)
  );
  RShift n375 ( // @[Top.scala 1131:22]
    .valid_up(n375_valid_up),
    .valid_down(n375_valid_down),
    .I_t0b(n375_I_t0b),
    .O(n375_O)
  );
  AtomTuple n376 ( // @[Top.scala 1134:22]
    .valid_up(n376_valid_up),
    .valid_down(n376_valid_down),
    .I0(n376_I0),
    .I1(n376_I1),
    .O_t0b(n376_O_t0b),
    .O_t1b(n376_O_t1b)
  );
  Mul n377 ( // @[Top.scala 1138:22]
    .clock(n377_clock),
    .reset(n377_reset),
    .valid_up(n377_valid_up),
    .valid_down(n377_valid_down),
    .I_t0b(n377_I_t0b),
    .I_t1b(n377_I_t1b),
    .O(n377_O)
  );
  AtomTuple n379 ( // @[Top.scala 1141:22]
    .valid_up(n379_valid_up),
    .valid_down(n379_valid_down),
    .I0(n379_I0),
    .I1(n379_I1),
    .O_t0b(n379_O_t0b),
    .O_t1b(n379_O_t1b)
  );
  Lt n380 ( // @[Top.scala 1145:22]
    .valid_up(n380_valid_up),
    .valid_down(n380_valid_down),
    .I_t0b(n380_I_t0b),
    .I_t1b(n380_I_t1b),
    .O(n380_O)
  );
  InitialDelayCounter_33 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n382 ( // @[Top.scala 1149:22]
    .valid_up(n382_valid_up),
    .valid_down(n382_valid_down),
    .I0(n382_I0),
    .I1(n382_I1),
    .O_t0b(n382_O_t0b),
    .O_t1b(n382_O_t1b)
  );
  Sub n383 ( // @[Top.scala 1153:22]
    .valid_up(n383_valid_up),
    .valid_down(n383_valid_down),
    .I_t0b(n383_I_t0b),
    .I_t1b(n383_I_t1b),
    .O(n383_O)
  );
  AtomTuple n384 ( // @[Top.scala 1156:22]
    .valid_up(n384_valid_up),
    .valid_down(n384_valid_down),
    .I0(n384_I0),
    .I1(n384_I1),
    .O_t0b(n384_O_t0b),
    .O_t1b(n384_O_t1b)
  );
  AtomTuple n385 ( // @[Top.scala 1160:22]
    .valid_up(n385_valid_up),
    .valid_down(n385_valid_down),
    .I0(n385_I0),
    .I1(n385_I1),
    .O_t0b(n385_O_t0b),
    .O_t1b(n385_O_t1b)
  );
  AtomTuple_10 n386 ( // @[Top.scala 1164:22]
    .valid_up(n386_valid_up),
    .valid_down(n386_valid_down),
    .I0_t0b(n386_I0_t0b),
    .I0_t1b(n386_I0_t1b),
    .I1_t0b(n386_I1_t0b),
    .I1_t1b(n386_I1_t1b),
    .O_t0b_t0b(n386_O_t0b_t0b),
    .O_t0b_t1b(n386_O_t0b_t1b),
    .O_t1b_t0b(n386_O_t1b_t0b),
    .O_t1b_t1b(n386_O_t1b_t1b)
  );
  FIFO_4 n387 ( // @[Top.scala 1168:22]
    .clock(n387_clock),
    .reset(n387_reset),
    .valid_up(n387_valid_up),
    .valid_down(n387_valid_down),
    .I_t0b_t0b(n387_I_t0b_t0b),
    .I_t0b_t1b(n387_I_t0b_t1b),
    .I_t1b_t0b(n387_I_t1b_t0b),
    .I_t1b_t1b(n387_I_t1b_t1b),
    .O_t0b_t0b(n387_O_t0b_t0b),
    .O_t0b_t1b(n387_O_t0b_t1b),
    .O_t1b_t0b(n387_O_t1b_t0b),
    .O_t1b_t1b(n387_O_t1b_t1b)
  );
  AtomTuple_11 n388 ( // @[Top.scala 1171:22]
    .valid_up(n388_valid_up),
    .valid_down(n388_valid_down),
    .I0(n388_I0),
    .I1_t0b_t0b(n388_I1_t0b_t0b),
    .I1_t0b_t1b(n388_I1_t0b_t1b),
    .I1_t1b_t0b(n388_I1_t1b_t0b),
    .I1_t1b_t1b(n388_I1_t1b_t1b),
    .O_t0b(n388_O_t0b),
    .O_t1b_t0b_t0b(n388_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n388_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n388_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n388_O_t1b_t1b_t1b)
  );
  If n389 ( // @[Top.scala 1175:22]
    .valid_up(n389_valid_up),
    .valid_down(n389_valid_down),
    .I_t0b(n389_I_t0b),
    .I_t1b_t0b_t0b(n389_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n389_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n389_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n389_I_t1b_t1b_t1b),
    .O_t0b(n389_O_t0b),
    .O_t1b(n389_O_t1b)
  );
  AtomTuple_1 n391 ( // @[Top.scala 1178:22]
    .valid_up(n391_valid_up),
    .valid_down(n391_valid_down),
    .I0(n391_I0),
    .I1_t0b(n391_I1_t0b),
    .I1_t1b(n391_I1_t1b),
    .O_t0b(n391_O_t0b),
    .O_t1b_t0b(n391_O_t1b_t0b),
    .O_t1b_t1b(n391_O_t1b_t1b)
  );
  assign valid_down = n391_valid_down; // @[Top.scala 1183:16]
  assign O_t0b = n391_O_t0b; // @[Top.scala 1182:7]
  assign O_t1b_t0b = n391_O_t1b_t0b; // @[Top.scala 1182:7]
  assign O_t1b_t1b = n391_O_t1b_t1b; // @[Top.scala 1182:7]
  assign n362_valid_up = valid_up; // @[Top.scala 1095:19]
  assign n362_I_t0b = I_t0b; // @[Top.scala 1094:12]
  assign n390_clock = clock;
  assign n390_reset = reset;
  assign n390_valid_up = n362_valid_down; // @[Top.scala 1098:19]
  assign n390_I = n362_O; // @[Top.scala 1097:12]
  assign n378_clock = clock;
  assign n378_reset = reset;
  assign n378_valid_up = n362_valid_down; // @[Top.scala 1101:19]
  assign n378_I = n362_O; // @[Top.scala 1100:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n364_valid_up = valid_up; // @[Top.scala 1105:19]
  assign n364_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1104:12]
  assign n364_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1104:12]
  assign n365_valid_up = n364_valid_down; // @[Top.scala 1108:19]
  assign n365_I_t0b = n364_O_t0b; // @[Top.scala 1107:12]
  assign n366_valid_up = n364_valid_down; // @[Top.scala 1111:19]
  assign n366_I_t1b = n364_O_t1b; // @[Top.scala 1110:12]
  assign n367_valid_up = n365_valid_down & n366_valid_down; // @[Top.scala 1115:19]
  assign n367_I0 = n365_O; // @[Top.scala 1113:13]
  assign n367_I1 = n366_O; // @[Top.scala 1114:13]
  assign n368_valid_up = n367_valid_down; // @[Top.scala 1118:19]
  assign n368_I_t0b = n367_O_t0b; // @[Top.scala 1117:12]
  assign n368_I_t1b = n367_O_t1b; // @[Top.scala 1117:12]
  assign n370_valid_up = InitialDelayCounter_valid_down & n368_valid_down; // @[Top.scala 1122:19]
  assign n370_I0 = 16'h1; // @[Top.scala 1120:13]
  assign n370_I1 = n368_O; // @[Top.scala 1121:13]
  assign n371_valid_up = n370_valid_down; // @[Top.scala 1125:19]
  assign n371_I_t0b = n370_O_t0b; // @[Top.scala 1124:12]
  assign n371_I_t1b = n370_O_t1b; // @[Top.scala 1124:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n374_valid_up = n371_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1130:19]
  assign n374_I0 = n371_O; // @[Top.scala 1128:13]
  assign n375_valid_up = n374_valid_down; // @[Top.scala 1133:19]
  assign n375_I_t0b = n374_O_t0b; // @[Top.scala 1132:12]
  assign n376_valid_up = n375_valid_down; // @[Top.scala 1137:19]
  assign n376_I0 = n375_O; // @[Top.scala 1135:13]
  assign n376_I1 = n375_O; // @[Top.scala 1136:13]
  assign n377_clock = clock;
  assign n377_reset = reset;
  assign n377_valid_up = n376_valid_down; // @[Top.scala 1140:19]
  assign n377_I_t0b = n376_O_t0b; // @[Top.scala 1139:12]
  assign n377_I_t1b = n376_O_t1b; // @[Top.scala 1139:12]
  assign n379_valid_up = n378_valid_down & n377_valid_down; // @[Top.scala 1144:19]
  assign n379_I0 = n378_O; // @[Top.scala 1142:13]
  assign n379_I1 = n377_O; // @[Top.scala 1143:13]
  assign n380_valid_up = n379_valid_down; // @[Top.scala 1147:19]
  assign n380_I_t0b = n379_O_t0b; // @[Top.scala 1146:12]
  assign n380_I_t1b = n379_O_t1b; // @[Top.scala 1146:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n382_valid_up = n375_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1152:19]
  assign n382_I0 = n375_O; // @[Top.scala 1150:13]
  assign n382_I1 = 16'h1; // @[Top.scala 1151:13]
  assign n383_valid_up = n382_valid_down; // @[Top.scala 1155:19]
  assign n383_I_t0b = n382_O_t0b; // @[Top.scala 1154:12]
  assign n383_I_t1b = n382_O_t1b; // @[Top.scala 1154:12]
  assign n384_valid_up = n365_valid_down & n383_valid_down; // @[Top.scala 1159:19]
  assign n384_I0 = n365_O; // @[Top.scala 1157:13]
  assign n384_I1 = n383_O; // @[Top.scala 1158:13]
  assign n385_valid_up = n375_valid_down & n366_valid_down; // @[Top.scala 1163:19]
  assign n385_I0 = n375_O; // @[Top.scala 1161:13]
  assign n385_I1 = n366_O; // @[Top.scala 1162:13]
  assign n386_valid_up = n384_valid_down & n385_valid_down; // @[Top.scala 1167:19]
  assign n386_I0_t0b = n384_O_t0b; // @[Top.scala 1165:13]
  assign n386_I0_t1b = n384_O_t1b; // @[Top.scala 1165:13]
  assign n386_I1_t0b = n385_O_t0b; // @[Top.scala 1166:13]
  assign n386_I1_t1b = n385_O_t1b; // @[Top.scala 1166:13]
  assign n387_clock = clock;
  assign n387_reset = reset;
  assign n387_valid_up = n386_valid_down; // @[Top.scala 1170:19]
  assign n387_I_t0b_t0b = n386_O_t0b_t0b; // @[Top.scala 1169:12]
  assign n387_I_t0b_t1b = n386_O_t0b_t1b; // @[Top.scala 1169:12]
  assign n387_I_t1b_t0b = n386_O_t1b_t0b; // @[Top.scala 1169:12]
  assign n387_I_t1b_t1b = n386_O_t1b_t1b; // @[Top.scala 1169:12]
  assign n388_valid_up = n380_valid_down & n387_valid_down; // @[Top.scala 1174:19]
  assign n388_I0 = n380_O[0]; // @[Top.scala 1172:13]
  assign n388_I1_t0b_t0b = n387_O_t0b_t0b; // @[Top.scala 1173:13]
  assign n388_I1_t0b_t1b = n387_O_t0b_t1b; // @[Top.scala 1173:13]
  assign n388_I1_t1b_t0b = n387_O_t1b_t0b; // @[Top.scala 1173:13]
  assign n388_I1_t1b_t1b = n387_O_t1b_t1b; // @[Top.scala 1173:13]
  assign n389_valid_up = n388_valid_down; // @[Top.scala 1177:19]
  assign n389_I_t0b = n388_O_t0b; // @[Top.scala 1176:12]
  assign n389_I_t1b_t0b_t0b = n388_O_t1b_t0b_t0b; // @[Top.scala 1176:12]
  assign n389_I_t1b_t0b_t1b = n388_O_t1b_t0b_t1b; // @[Top.scala 1176:12]
  assign n389_I_t1b_t1b_t0b = n388_O_t1b_t1b_t0b; // @[Top.scala 1176:12]
  assign n389_I_t1b_t1b_t1b = n388_O_t1b_t1b_t1b; // @[Top.scala 1176:12]
  assign n391_valid_up = n390_valid_down & n389_valid_down; // @[Top.scala 1181:19]
  assign n391_I0 = n390_O; // @[Top.scala 1179:13]
  assign n391_I1_t0b = n389_O_t0b; // @[Top.scala 1180:13]
  assign n391_I1_t1b = n389_O_t1b; // @[Top.scala 1180:13]
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
module InitialDelayCounter_36(
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
  wire  n394_valid_up; // @[Top.scala 1189:22]
  wire  n394_valid_down; // @[Top.scala 1189:22]
  wire [15:0] n394_I_t0b; // @[Top.scala 1189:22]
  wire [15:0] n394_O; // @[Top.scala 1189:22]
  wire  n422_clock; // @[Top.scala 1192:22]
  wire  n422_reset; // @[Top.scala 1192:22]
  wire  n422_valid_up; // @[Top.scala 1192:22]
  wire  n422_valid_down; // @[Top.scala 1192:22]
  wire [15:0] n422_I; // @[Top.scala 1192:22]
  wire [15:0] n422_O; // @[Top.scala 1192:22]
  wire  n410_clock; // @[Top.scala 1195:22]
  wire  n410_reset; // @[Top.scala 1195:22]
  wire  n410_valid_up; // @[Top.scala 1195:22]
  wire  n410_valid_down; // @[Top.scala 1195:22]
  wire [15:0] n410_I; // @[Top.scala 1195:22]
  wire [15:0] n410_O; // @[Top.scala 1195:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n396_valid_up; // @[Top.scala 1199:22]
  wire  n396_valid_down; // @[Top.scala 1199:22]
  wire [15:0] n396_I_t1b_t0b; // @[Top.scala 1199:22]
  wire [15:0] n396_I_t1b_t1b; // @[Top.scala 1199:22]
  wire [15:0] n396_O_t0b; // @[Top.scala 1199:22]
  wire [15:0] n396_O_t1b; // @[Top.scala 1199:22]
  wire  n397_valid_up; // @[Top.scala 1202:22]
  wire  n397_valid_down; // @[Top.scala 1202:22]
  wire [15:0] n397_I_t0b; // @[Top.scala 1202:22]
  wire [15:0] n397_O; // @[Top.scala 1202:22]
  wire  n398_valid_up; // @[Top.scala 1205:22]
  wire  n398_valid_down; // @[Top.scala 1205:22]
  wire [15:0] n398_I_t1b; // @[Top.scala 1205:22]
  wire [15:0] n398_O; // @[Top.scala 1205:22]
  wire  n399_valid_up; // @[Top.scala 1208:22]
  wire  n399_valid_down; // @[Top.scala 1208:22]
  wire [15:0] n399_I0; // @[Top.scala 1208:22]
  wire [15:0] n399_I1; // @[Top.scala 1208:22]
  wire [15:0] n399_O_t0b; // @[Top.scala 1208:22]
  wire [15:0] n399_O_t1b; // @[Top.scala 1208:22]
  wire  n400_valid_up; // @[Top.scala 1212:22]
  wire  n400_valid_down; // @[Top.scala 1212:22]
  wire [15:0] n400_I_t0b; // @[Top.scala 1212:22]
  wire [15:0] n400_I_t1b; // @[Top.scala 1212:22]
  wire [15:0] n400_O; // @[Top.scala 1212:22]
  wire  n402_valid_up; // @[Top.scala 1215:22]
  wire  n402_valid_down; // @[Top.scala 1215:22]
  wire [15:0] n402_I0; // @[Top.scala 1215:22]
  wire [15:0] n402_I1; // @[Top.scala 1215:22]
  wire [15:0] n402_O_t0b; // @[Top.scala 1215:22]
  wire [15:0] n402_O_t1b; // @[Top.scala 1215:22]
  wire  n403_valid_up; // @[Top.scala 1219:22]
  wire  n403_valid_down; // @[Top.scala 1219:22]
  wire [15:0] n403_I_t0b; // @[Top.scala 1219:22]
  wire [15:0] n403_I_t1b; // @[Top.scala 1219:22]
  wire [15:0] n403_O; // @[Top.scala 1219:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n406_valid_up; // @[Top.scala 1223:22]
  wire  n406_valid_down; // @[Top.scala 1223:22]
  wire [15:0] n406_I0; // @[Top.scala 1223:22]
  wire [15:0] n406_O_t0b; // @[Top.scala 1223:22]
  wire  n407_valid_up; // @[Top.scala 1227:22]
  wire  n407_valid_down; // @[Top.scala 1227:22]
  wire [15:0] n407_I_t0b; // @[Top.scala 1227:22]
  wire [15:0] n407_O; // @[Top.scala 1227:22]
  wire  n408_valid_up; // @[Top.scala 1230:22]
  wire  n408_valid_down; // @[Top.scala 1230:22]
  wire [15:0] n408_I0; // @[Top.scala 1230:22]
  wire [15:0] n408_I1; // @[Top.scala 1230:22]
  wire [15:0] n408_O_t0b; // @[Top.scala 1230:22]
  wire [15:0] n408_O_t1b; // @[Top.scala 1230:22]
  wire  n409_clock; // @[Top.scala 1234:22]
  wire  n409_reset; // @[Top.scala 1234:22]
  wire  n409_valid_up; // @[Top.scala 1234:22]
  wire  n409_valid_down; // @[Top.scala 1234:22]
  wire [15:0] n409_I_t0b; // @[Top.scala 1234:22]
  wire [15:0] n409_I_t1b; // @[Top.scala 1234:22]
  wire [15:0] n409_O; // @[Top.scala 1234:22]
  wire  n411_valid_up; // @[Top.scala 1237:22]
  wire  n411_valid_down; // @[Top.scala 1237:22]
  wire [15:0] n411_I0; // @[Top.scala 1237:22]
  wire [15:0] n411_I1; // @[Top.scala 1237:22]
  wire [15:0] n411_O_t0b; // @[Top.scala 1237:22]
  wire [15:0] n411_O_t1b; // @[Top.scala 1237:22]
  wire  n412_valid_up; // @[Top.scala 1241:22]
  wire  n412_valid_down; // @[Top.scala 1241:22]
  wire [15:0] n412_I_t0b; // @[Top.scala 1241:22]
  wire [15:0] n412_I_t1b; // @[Top.scala 1241:22]
  wire [15:0] n412_O; // @[Top.scala 1241:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n414_valid_up; // @[Top.scala 1245:22]
  wire  n414_valid_down; // @[Top.scala 1245:22]
  wire [15:0] n414_I0; // @[Top.scala 1245:22]
  wire [15:0] n414_I1; // @[Top.scala 1245:22]
  wire [15:0] n414_O_t0b; // @[Top.scala 1245:22]
  wire [15:0] n414_O_t1b; // @[Top.scala 1245:22]
  wire  n415_valid_up; // @[Top.scala 1249:22]
  wire  n415_valid_down; // @[Top.scala 1249:22]
  wire [15:0] n415_I_t0b; // @[Top.scala 1249:22]
  wire [15:0] n415_I_t1b; // @[Top.scala 1249:22]
  wire [15:0] n415_O; // @[Top.scala 1249:22]
  wire  n416_valid_up; // @[Top.scala 1252:22]
  wire  n416_valid_down; // @[Top.scala 1252:22]
  wire [15:0] n416_I0; // @[Top.scala 1252:22]
  wire [15:0] n416_I1; // @[Top.scala 1252:22]
  wire [15:0] n416_O_t0b; // @[Top.scala 1252:22]
  wire [15:0] n416_O_t1b; // @[Top.scala 1252:22]
  wire  n417_valid_up; // @[Top.scala 1256:22]
  wire  n417_valid_down; // @[Top.scala 1256:22]
  wire [15:0] n417_I0; // @[Top.scala 1256:22]
  wire [15:0] n417_I1; // @[Top.scala 1256:22]
  wire [15:0] n417_O_t0b; // @[Top.scala 1256:22]
  wire [15:0] n417_O_t1b; // @[Top.scala 1256:22]
  wire  n418_valid_up; // @[Top.scala 1260:22]
  wire  n418_valid_down; // @[Top.scala 1260:22]
  wire [15:0] n418_I0_t0b; // @[Top.scala 1260:22]
  wire [15:0] n418_I0_t1b; // @[Top.scala 1260:22]
  wire [15:0] n418_I1_t0b; // @[Top.scala 1260:22]
  wire [15:0] n418_I1_t1b; // @[Top.scala 1260:22]
  wire [15:0] n418_O_t0b_t0b; // @[Top.scala 1260:22]
  wire [15:0] n418_O_t0b_t1b; // @[Top.scala 1260:22]
  wire [15:0] n418_O_t1b_t0b; // @[Top.scala 1260:22]
  wire [15:0] n418_O_t1b_t1b; // @[Top.scala 1260:22]
  wire  n419_clock; // @[Top.scala 1264:22]
  wire  n419_reset; // @[Top.scala 1264:22]
  wire  n419_valid_up; // @[Top.scala 1264:22]
  wire  n419_valid_down; // @[Top.scala 1264:22]
  wire [15:0] n419_I_t0b_t0b; // @[Top.scala 1264:22]
  wire [15:0] n419_I_t0b_t1b; // @[Top.scala 1264:22]
  wire [15:0] n419_I_t1b_t0b; // @[Top.scala 1264:22]
  wire [15:0] n419_I_t1b_t1b; // @[Top.scala 1264:22]
  wire [15:0] n419_O_t0b_t0b; // @[Top.scala 1264:22]
  wire [15:0] n419_O_t0b_t1b; // @[Top.scala 1264:22]
  wire [15:0] n419_O_t1b_t0b; // @[Top.scala 1264:22]
  wire [15:0] n419_O_t1b_t1b; // @[Top.scala 1264:22]
  wire  n420_valid_up; // @[Top.scala 1267:22]
  wire  n420_valid_down; // @[Top.scala 1267:22]
  wire  n420_I0; // @[Top.scala 1267:22]
  wire [15:0] n420_I1_t0b_t0b; // @[Top.scala 1267:22]
  wire [15:0] n420_I1_t0b_t1b; // @[Top.scala 1267:22]
  wire [15:0] n420_I1_t1b_t0b; // @[Top.scala 1267:22]
  wire [15:0] n420_I1_t1b_t1b; // @[Top.scala 1267:22]
  wire  n420_O_t0b; // @[Top.scala 1267:22]
  wire [15:0] n420_O_t1b_t0b_t0b; // @[Top.scala 1267:22]
  wire [15:0] n420_O_t1b_t0b_t1b; // @[Top.scala 1267:22]
  wire [15:0] n420_O_t1b_t1b_t0b; // @[Top.scala 1267:22]
  wire [15:0] n420_O_t1b_t1b_t1b; // @[Top.scala 1267:22]
  wire  n421_valid_up; // @[Top.scala 1271:22]
  wire  n421_valid_down; // @[Top.scala 1271:22]
  wire  n421_I_t0b; // @[Top.scala 1271:22]
  wire [15:0] n421_I_t1b_t0b_t0b; // @[Top.scala 1271:22]
  wire [15:0] n421_I_t1b_t0b_t1b; // @[Top.scala 1271:22]
  wire [15:0] n421_I_t1b_t1b_t0b; // @[Top.scala 1271:22]
  wire [15:0] n421_I_t1b_t1b_t1b; // @[Top.scala 1271:22]
  wire [15:0] n421_O_t0b; // @[Top.scala 1271:22]
  wire [15:0] n421_O_t1b; // @[Top.scala 1271:22]
  wire  n423_valid_up; // @[Top.scala 1274:22]
  wire  n423_valid_down; // @[Top.scala 1274:22]
  wire [15:0] n423_I0; // @[Top.scala 1274:22]
  wire [15:0] n423_I1_t0b; // @[Top.scala 1274:22]
  wire [15:0] n423_I1_t1b; // @[Top.scala 1274:22]
  wire [15:0] n423_O_t0b; // @[Top.scala 1274:22]
  wire [15:0] n423_O_t1b_t0b; // @[Top.scala 1274:22]
  wire [15:0] n423_O_t1b_t1b; // @[Top.scala 1274:22]
  Fst n394 ( // @[Top.scala 1189:22]
    .valid_up(n394_valid_up),
    .valid_down(n394_valid_down),
    .I_t0b(n394_I_t0b),
    .O(n394_O)
  );
  FIFO_2 n422 ( // @[Top.scala 1192:22]
    .clock(n422_clock),
    .reset(n422_reset),
    .valid_up(n422_valid_up),
    .valid_down(n422_valid_down),
    .I(n422_I),
    .O(n422_O)
  );
  FIFO_2 n410 ( // @[Top.scala 1195:22]
    .clock(n410_clock),
    .reset(n410_reset),
    .valid_up(n410_valid_up),
    .valid_down(n410_valid_down),
    .I(n410_I),
    .O(n410_O)
  );
  InitialDelayCounter_36 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n396 ( // @[Top.scala 1199:22]
    .valid_up(n396_valid_up),
    .valid_down(n396_valid_down),
    .I_t1b_t0b(n396_I_t1b_t0b),
    .I_t1b_t1b(n396_I_t1b_t1b),
    .O_t0b(n396_O_t0b),
    .O_t1b(n396_O_t1b)
  );
  Fst_1 n397 ( // @[Top.scala 1202:22]
    .valid_up(n397_valid_up),
    .valid_down(n397_valid_down),
    .I_t0b(n397_I_t0b),
    .O(n397_O)
  );
  Snd_1 n398 ( // @[Top.scala 1205:22]
    .valid_up(n398_valid_up),
    .valid_down(n398_valid_down),
    .I_t1b(n398_I_t1b),
    .O(n398_O)
  );
  AtomTuple n399 ( // @[Top.scala 1208:22]
    .valid_up(n399_valid_up),
    .valid_down(n399_valid_down),
    .I0(n399_I0),
    .I1(n399_I1),
    .O_t0b(n399_O_t0b),
    .O_t1b(n399_O_t1b)
  );
  Add n400 ( // @[Top.scala 1212:22]
    .valid_up(n400_valid_up),
    .valid_down(n400_valid_down),
    .I_t0b(n400_I_t0b),
    .I_t1b(n400_I_t1b),
    .O(n400_O)
  );
  AtomTuple n402 ( // @[Top.scala 1215:22]
    .valid_up(n402_valid_up),
    .valid_down(n402_valid_down),
    .I0(n402_I0),
    .I1(n402_I1),
    .O_t0b(n402_O_t0b),
    .O_t1b(n402_O_t1b)
  );
  Add n403 ( // @[Top.scala 1219:22]
    .valid_up(n403_valid_up),
    .valid_down(n403_valid_down),
    .I_t0b(n403_I_t0b),
    .I_t1b(n403_I_t1b),
    .O(n403_O)
  );
  InitialDelayCounter_36 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n406 ( // @[Top.scala 1223:22]
    .valid_up(n406_valid_up),
    .valid_down(n406_valid_down),
    .I0(n406_I0),
    .O_t0b(n406_O_t0b)
  );
  RShift n407 ( // @[Top.scala 1227:22]
    .valid_up(n407_valid_up),
    .valid_down(n407_valid_down),
    .I_t0b(n407_I_t0b),
    .O(n407_O)
  );
  AtomTuple n408 ( // @[Top.scala 1230:22]
    .valid_up(n408_valid_up),
    .valid_down(n408_valid_down),
    .I0(n408_I0),
    .I1(n408_I1),
    .O_t0b(n408_O_t0b),
    .O_t1b(n408_O_t1b)
  );
  Mul n409 ( // @[Top.scala 1234:22]
    .clock(n409_clock),
    .reset(n409_reset),
    .valid_up(n409_valid_up),
    .valid_down(n409_valid_down),
    .I_t0b(n409_I_t0b),
    .I_t1b(n409_I_t1b),
    .O(n409_O)
  );
  AtomTuple n411 ( // @[Top.scala 1237:22]
    .valid_up(n411_valid_up),
    .valid_down(n411_valid_down),
    .I0(n411_I0),
    .I1(n411_I1),
    .O_t0b(n411_O_t0b),
    .O_t1b(n411_O_t1b)
  );
  Lt n412 ( // @[Top.scala 1241:22]
    .valid_up(n412_valid_up),
    .valid_down(n412_valid_down),
    .I_t0b(n412_I_t0b),
    .I_t1b(n412_I_t1b),
    .O(n412_O)
  );
  InitialDelayCounter_36 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n414 ( // @[Top.scala 1245:22]
    .valid_up(n414_valid_up),
    .valid_down(n414_valid_down),
    .I0(n414_I0),
    .I1(n414_I1),
    .O_t0b(n414_O_t0b),
    .O_t1b(n414_O_t1b)
  );
  Sub n415 ( // @[Top.scala 1249:22]
    .valid_up(n415_valid_up),
    .valid_down(n415_valid_down),
    .I_t0b(n415_I_t0b),
    .I_t1b(n415_I_t1b),
    .O(n415_O)
  );
  AtomTuple n416 ( // @[Top.scala 1252:22]
    .valid_up(n416_valid_up),
    .valid_down(n416_valid_down),
    .I0(n416_I0),
    .I1(n416_I1),
    .O_t0b(n416_O_t0b),
    .O_t1b(n416_O_t1b)
  );
  AtomTuple n417 ( // @[Top.scala 1256:22]
    .valid_up(n417_valid_up),
    .valid_down(n417_valid_down),
    .I0(n417_I0),
    .I1(n417_I1),
    .O_t0b(n417_O_t0b),
    .O_t1b(n417_O_t1b)
  );
  AtomTuple_10 n418 ( // @[Top.scala 1260:22]
    .valid_up(n418_valid_up),
    .valid_down(n418_valid_down),
    .I0_t0b(n418_I0_t0b),
    .I0_t1b(n418_I0_t1b),
    .I1_t0b(n418_I1_t0b),
    .I1_t1b(n418_I1_t1b),
    .O_t0b_t0b(n418_O_t0b_t0b),
    .O_t0b_t1b(n418_O_t0b_t1b),
    .O_t1b_t0b(n418_O_t1b_t0b),
    .O_t1b_t1b(n418_O_t1b_t1b)
  );
  FIFO_4 n419 ( // @[Top.scala 1264:22]
    .clock(n419_clock),
    .reset(n419_reset),
    .valid_up(n419_valid_up),
    .valid_down(n419_valid_down),
    .I_t0b_t0b(n419_I_t0b_t0b),
    .I_t0b_t1b(n419_I_t0b_t1b),
    .I_t1b_t0b(n419_I_t1b_t0b),
    .I_t1b_t1b(n419_I_t1b_t1b),
    .O_t0b_t0b(n419_O_t0b_t0b),
    .O_t0b_t1b(n419_O_t0b_t1b),
    .O_t1b_t0b(n419_O_t1b_t0b),
    .O_t1b_t1b(n419_O_t1b_t1b)
  );
  AtomTuple_11 n420 ( // @[Top.scala 1267:22]
    .valid_up(n420_valid_up),
    .valid_down(n420_valid_down),
    .I0(n420_I0),
    .I1_t0b_t0b(n420_I1_t0b_t0b),
    .I1_t0b_t1b(n420_I1_t0b_t1b),
    .I1_t1b_t0b(n420_I1_t1b_t0b),
    .I1_t1b_t1b(n420_I1_t1b_t1b),
    .O_t0b(n420_O_t0b),
    .O_t1b_t0b_t0b(n420_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n420_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n420_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n420_O_t1b_t1b_t1b)
  );
  If n421 ( // @[Top.scala 1271:22]
    .valid_up(n421_valid_up),
    .valid_down(n421_valid_down),
    .I_t0b(n421_I_t0b),
    .I_t1b_t0b_t0b(n421_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n421_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n421_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n421_I_t1b_t1b_t1b),
    .O_t0b(n421_O_t0b),
    .O_t1b(n421_O_t1b)
  );
  AtomTuple_1 n423 ( // @[Top.scala 1274:22]
    .valid_up(n423_valid_up),
    .valid_down(n423_valid_down),
    .I0(n423_I0),
    .I1_t0b(n423_I1_t0b),
    .I1_t1b(n423_I1_t1b),
    .O_t0b(n423_O_t0b),
    .O_t1b_t0b(n423_O_t1b_t0b),
    .O_t1b_t1b(n423_O_t1b_t1b)
  );
  assign valid_down = n423_valid_down; // @[Top.scala 1279:16]
  assign O_t0b = n423_O_t0b; // @[Top.scala 1278:7]
  assign O_t1b_t0b = n423_O_t1b_t0b; // @[Top.scala 1278:7]
  assign O_t1b_t1b = n423_O_t1b_t1b; // @[Top.scala 1278:7]
  assign n394_valid_up = valid_up; // @[Top.scala 1191:19]
  assign n394_I_t0b = I_t0b; // @[Top.scala 1190:12]
  assign n422_clock = clock;
  assign n422_reset = reset;
  assign n422_valid_up = n394_valid_down; // @[Top.scala 1194:19]
  assign n422_I = n394_O; // @[Top.scala 1193:12]
  assign n410_clock = clock;
  assign n410_reset = reset;
  assign n410_valid_up = n394_valid_down; // @[Top.scala 1197:19]
  assign n410_I = n394_O; // @[Top.scala 1196:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n396_valid_up = valid_up; // @[Top.scala 1201:19]
  assign n396_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1200:12]
  assign n396_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1200:12]
  assign n397_valid_up = n396_valid_down; // @[Top.scala 1204:19]
  assign n397_I_t0b = n396_O_t0b; // @[Top.scala 1203:12]
  assign n398_valid_up = n396_valid_down; // @[Top.scala 1207:19]
  assign n398_I_t1b = n396_O_t1b; // @[Top.scala 1206:12]
  assign n399_valid_up = n397_valid_down & n398_valid_down; // @[Top.scala 1211:19]
  assign n399_I0 = n397_O; // @[Top.scala 1209:13]
  assign n399_I1 = n398_O; // @[Top.scala 1210:13]
  assign n400_valid_up = n399_valid_down; // @[Top.scala 1214:19]
  assign n400_I_t0b = n399_O_t0b; // @[Top.scala 1213:12]
  assign n400_I_t1b = n399_O_t1b; // @[Top.scala 1213:12]
  assign n402_valid_up = InitialDelayCounter_valid_down & n400_valid_down; // @[Top.scala 1218:19]
  assign n402_I0 = 16'h1; // @[Top.scala 1216:13]
  assign n402_I1 = n400_O; // @[Top.scala 1217:13]
  assign n403_valid_up = n402_valid_down; // @[Top.scala 1221:19]
  assign n403_I_t0b = n402_O_t0b; // @[Top.scala 1220:12]
  assign n403_I_t1b = n402_O_t1b; // @[Top.scala 1220:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n406_valid_up = n403_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1226:19]
  assign n406_I0 = n403_O; // @[Top.scala 1224:13]
  assign n407_valid_up = n406_valid_down; // @[Top.scala 1229:19]
  assign n407_I_t0b = n406_O_t0b; // @[Top.scala 1228:12]
  assign n408_valid_up = n407_valid_down; // @[Top.scala 1233:19]
  assign n408_I0 = n407_O; // @[Top.scala 1231:13]
  assign n408_I1 = n407_O; // @[Top.scala 1232:13]
  assign n409_clock = clock;
  assign n409_reset = reset;
  assign n409_valid_up = n408_valid_down; // @[Top.scala 1236:19]
  assign n409_I_t0b = n408_O_t0b; // @[Top.scala 1235:12]
  assign n409_I_t1b = n408_O_t1b; // @[Top.scala 1235:12]
  assign n411_valid_up = n410_valid_down & n409_valid_down; // @[Top.scala 1240:19]
  assign n411_I0 = n410_O; // @[Top.scala 1238:13]
  assign n411_I1 = n409_O; // @[Top.scala 1239:13]
  assign n412_valid_up = n411_valid_down; // @[Top.scala 1243:19]
  assign n412_I_t0b = n411_O_t0b; // @[Top.scala 1242:12]
  assign n412_I_t1b = n411_O_t1b; // @[Top.scala 1242:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n414_valid_up = n407_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1248:19]
  assign n414_I0 = n407_O; // @[Top.scala 1246:13]
  assign n414_I1 = 16'h1; // @[Top.scala 1247:13]
  assign n415_valid_up = n414_valid_down; // @[Top.scala 1251:19]
  assign n415_I_t0b = n414_O_t0b; // @[Top.scala 1250:12]
  assign n415_I_t1b = n414_O_t1b; // @[Top.scala 1250:12]
  assign n416_valid_up = n397_valid_down & n415_valid_down; // @[Top.scala 1255:19]
  assign n416_I0 = n397_O; // @[Top.scala 1253:13]
  assign n416_I1 = n415_O; // @[Top.scala 1254:13]
  assign n417_valid_up = n407_valid_down & n398_valid_down; // @[Top.scala 1259:19]
  assign n417_I0 = n407_O; // @[Top.scala 1257:13]
  assign n417_I1 = n398_O; // @[Top.scala 1258:13]
  assign n418_valid_up = n416_valid_down & n417_valid_down; // @[Top.scala 1263:19]
  assign n418_I0_t0b = n416_O_t0b; // @[Top.scala 1261:13]
  assign n418_I0_t1b = n416_O_t1b; // @[Top.scala 1261:13]
  assign n418_I1_t0b = n417_O_t0b; // @[Top.scala 1262:13]
  assign n418_I1_t1b = n417_O_t1b; // @[Top.scala 1262:13]
  assign n419_clock = clock;
  assign n419_reset = reset;
  assign n419_valid_up = n418_valid_down; // @[Top.scala 1266:19]
  assign n419_I_t0b_t0b = n418_O_t0b_t0b; // @[Top.scala 1265:12]
  assign n419_I_t0b_t1b = n418_O_t0b_t1b; // @[Top.scala 1265:12]
  assign n419_I_t1b_t0b = n418_O_t1b_t0b; // @[Top.scala 1265:12]
  assign n419_I_t1b_t1b = n418_O_t1b_t1b; // @[Top.scala 1265:12]
  assign n420_valid_up = n412_valid_down & n419_valid_down; // @[Top.scala 1270:19]
  assign n420_I0 = n412_O[0]; // @[Top.scala 1268:13]
  assign n420_I1_t0b_t0b = n419_O_t0b_t0b; // @[Top.scala 1269:13]
  assign n420_I1_t0b_t1b = n419_O_t0b_t1b; // @[Top.scala 1269:13]
  assign n420_I1_t1b_t0b = n419_O_t1b_t0b; // @[Top.scala 1269:13]
  assign n420_I1_t1b_t1b = n419_O_t1b_t1b; // @[Top.scala 1269:13]
  assign n421_valid_up = n420_valid_down; // @[Top.scala 1273:19]
  assign n421_I_t0b = n420_O_t0b; // @[Top.scala 1272:12]
  assign n421_I_t1b_t0b_t0b = n420_O_t1b_t0b_t0b; // @[Top.scala 1272:12]
  assign n421_I_t1b_t0b_t1b = n420_O_t1b_t0b_t1b; // @[Top.scala 1272:12]
  assign n421_I_t1b_t1b_t0b = n420_O_t1b_t1b_t0b; // @[Top.scala 1272:12]
  assign n421_I_t1b_t1b_t1b = n420_O_t1b_t1b_t1b; // @[Top.scala 1272:12]
  assign n423_valid_up = n422_valid_down & n421_valid_down; // @[Top.scala 1277:19]
  assign n423_I0 = n422_O; // @[Top.scala 1275:13]
  assign n423_I1_t0b = n421_O_t0b; // @[Top.scala 1276:13]
  assign n423_I1_t1b = n421_O_t1b; // @[Top.scala 1276:13]
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
module InitialDelayCounter_39(
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
  wire  n426_valid_up; // @[Top.scala 1285:22]
  wire  n426_valid_down; // @[Top.scala 1285:22]
  wire [15:0] n426_I_t0b; // @[Top.scala 1285:22]
  wire [15:0] n426_O; // @[Top.scala 1285:22]
  wire  n454_clock; // @[Top.scala 1288:22]
  wire  n454_reset; // @[Top.scala 1288:22]
  wire  n454_valid_up; // @[Top.scala 1288:22]
  wire  n454_valid_down; // @[Top.scala 1288:22]
  wire [15:0] n454_I; // @[Top.scala 1288:22]
  wire [15:0] n454_O; // @[Top.scala 1288:22]
  wire  n442_clock; // @[Top.scala 1291:22]
  wire  n442_reset; // @[Top.scala 1291:22]
  wire  n442_valid_up; // @[Top.scala 1291:22]
  wire  n442_valid_down; // @[Top.scala 1291:22]
  wire [15:0] n442_I; // @[Top.scala 1291:22]
  wire [15:0] n442_O; // @[Top.scala 1291:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n428_valid_up; // @[Top.scala 1295:22]
  wire  n428_valid_down; // @[Top.scala 1295:22]
  wire [15:0] n428_I_t1b_t0b; // @[Top.scala 1295:22]
  wire [15:0] n428_I_t1b_t1b; // @[Top.scala 1295:22]
  wire [15:0] n428_O_t0b; // @[Top.scala 1295:22]
  wire [15:0] n428_O_t1b; // @[Top.scala 1295:22]
  wire  n429_valid_up; // @[Top.scala 1298:22]
  wire  n429_valid_down; // @[Top.scala 1298:22]
  wire [15:0] n429_I_t0b; // @[Top.scala 1298:22]
  wire [15:0] n429_O; // @[Top.scala 1298:22]
  wire  n430_valid_up; // @[Top.scala 1301:22]
  wire  n430_valid_down; // @[Top.scala 1301:22]
  wire [15:0] n430_I_t1b; // @[Top.scala 1301:22]
  wire [15:0] n430_O; // @[Top.scala 1301:22]
  wire  n431_valid_up; // @[Top.scala 1304:22]
  wire  n431_valid_down; // @[Top.scala 1304:22]
  wire [15:0] n431_I0; // @[Top.scala 1304:22]
  wire [15:0] n431_I1; // @[Top.scala 1304:22]
  wire [15:0] n431_O_t0b; // @[Top.scala 1304:22]
  wire [15:0] n431_O_t1b; // @[Top.scala 1304:22]
  wire  n432_valid_up; // @[Top.scala 1308:22]
  wire  n432_valid_down; // @[Top.scala 1308:22]
  wire [15:0] n432_I_t0b; // @[Top.scala 1308:22]
  wire [15:0] n432_I_t1b; // @[Top.scala 1308:22]
  wire [15:0] n432_O; // @[Top.scala 1308:22]
  wire  n434_valid_up; // @[Top.scala 1311:22]
  wire  n434_valid_down; // @[Top.scala 1311:22]
  wire [15:0] n434_I0; // @[Top.scala 1311:22]
  wire [15:0] n434_I1; // @[Top.scala 1311:22]
  wire [15:0] n434_O_t0b; // @[Top.scala 1311:22]
  wire [15:0] n434_O_t1b; // @[Top.scala 1311:22]
  wire  n435_valid_up; // @[Top.scala 1315:22]
  wire  n435_valid_down; // @[Top.scala 1315:22]
  wire [15:0] n435_I_t0b; // @[Top.scala 1315:22]
  wire [15:0] n435_I_t1b; // @[Top.scala 1315:22]
  wire [15:0] n435_O; // @[Top.scala 1315:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n438_valid_up; // @[Top.scala 1319:22]
  wire  n438_valid_down; // @[Top.scala 1319:22]
  wire [15:0] n438_I0; // @[Top.scala 1319:22]
  wire [15:0] n438_O_t0b; // @[Top.scala 1319:22]
  wire  n439_valid_up; // @[Top.scala 1323:22]
  wire  n439_valid_down; // @[Top.scala 1323:22]
  wire [15:0] n439_I_t0b; // @[Top.scala 1323:22]
  wire [15:0] n439_O; // @[Top.scala 1323:22]
  wire  n440_valid_up; // @[Top.scala 1326:22]
  wire  n440_valid_down; // @[Top.scala 1326:22]
  wire [15:0] n440_I0; // @[Top.scala 1326:22]
  wire [15:0] n440_I1; // @[Top.scala 1326:22]
  wire [15:0] n440_O_t0b; // @[Top.scala 1326:22]
  wire [15:0] n440_O_t1b; // @[Top.scala 1326:22]
  wire  n441_clock; // @[Top.scala 1330:22]
  wire  n441_reset; // @[Top.scala 1330:22]
  wire  n441_valid_up; // @[Top.scala 1330:22]
  wire  n441_valid_down; // @[Top.scala 1330:22]
  wire [15:0] n441_I_t0b; // @[Top.scala 1330:22]
  wire [15:0] n441_I_t1b; // @[Top.scala 1330:22]
  wire [15:0] n441_O; // @[Top.scala 1330:22]
  wire  n443_valid_up; // @[Top.scala 1333:22]
  wire  n443_valid_down; // @[Top.scala 1333:22]
  wire [15:0] n443_I0; // @[Top.scala 1333:22]
  wire [15:0] n443_I1; // @[Top.scala 1333:22]
  wire [15:0] n443_O_t0b; // @[Top.scala 1333:22]
  wire [15:0] n443_O_t1b; // @[Top.scala 1333:22]
  wire  n444_valid_up; // @[Top.scala 1337:22]
  wire  n444_valid_down; // @[Top.scala 1337:22]
  wire [15:0] n444_I_t0b; // @[Top.scala 1337:22]
  wire [15:0] n444_I_t1b; // @[Top.scala 1337:22]
  wire [15:0] n444_O; // @[Top.scala 1337:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n446_valid_up; // @[Top.scala 1341:22]
  wire  n446_valid_down; // @[Top.scala 1341:22]
  wire [15:0] n446_I0; // @[Top.scala 1341:22]
  wire [15:0] n446_I1; // @[Top.scala 1341:22]
  wire [15:0] n446_O_t0b; // @[Top.scala 1341:22]
  wire [15:0] n446_O_t1b; // @[Top.scala 1341:22]
  wire  n447_valid_up; // @[Top.scala 1345:22]
  wire  n447_valid_down; // @[Top.scala 1345:22]
  wire [15:0] n447_I_t0b; // @[Top.scala 1345:22]
  wire [15:0] n447_I_t1b; // @[Top.scala 1345:22]
  wire [15:0] n447_O; // @[Top.scala 1345:22]
  wire  n448_valid_up; // @[Top.scala 1348:22]
  wire  n448_valid_down; // @[Top.scala 1348:22]
  wire [15:0] n448_I0; // @[Top.scala 1348:22]
  wire [15:0] n448_I1; // @[Top.scala 1348:22]
  wire [15:0] n448_O_t0b; // @[Top.scala 1348:22]
  wire [15:0] n448_O_t1b; // @[Top.scala 1348:22]
  wire  n449_valid_up; // @[Top.scala 1352:22]
  wire  n449_valid_down; // @[Top.scala 1352:22]
  wire [15:0] n449_I0; // @[Top.scala 1352:22]
  wire [15:0] n449_I1; // @[Top.scala 1352:22]
  wire [15:0] n449_O_t0b; // @[Top.scala 1352:22]
  wire [15:0] n449_O_t1b; // @[Top.scala 1352:22]
  wire  n450_valid_up; // @[Top.scala 1356:22]
  wire  n450_valid_down; // @[Top.scala 1356:22]
  wire [15:0] n450_I0_t0b; // @[Top.scala 1356:22]
  wire [15:0] n450_I0_t1b; // @[Top.scala 1356:22]
  wire [15:0] n450_I1_t0b; // @[Top.scala 1356:22]
  wire [15:0] n450_I1_t1b; // @[Top.scala 1356:22]
  wire [15:0] n450_O_t0b_t0b; // @[Top.scala 1356:22]
  wire [15:0] n450_O_t0b_t1b; // @[Top.scala 1356:22]
  wire [15:0] n450_O_t1b_t0b; // @[Top.scala 1356:22]
  wire [15:0] n450_O_t1b_t1b; // @[Top.scala 1356:22]
  wire  n451_clock; // @[Top.scala 1360:22]
  wire  n451_reset; // @[Top.scala 1360:22]
  wire  n451_valid_up; // @[Top.scala 1360:22]
  wire  n451_valid_down; // @[Top.scala 1360:22]
  wire [15:0] n451_I_t0b_t0b; // @[Top.scala 1360:22]
  wire [15:0] n451_I_t0b_t1b; // @[Top.scala 1360:22]
  wire [15:0] n451_I_t1b_t0b; // @[Top.scala 1360:22]
  wire [15:0] n451_I_t1b_t1b; // @[Top.scala 1360:22]
  wire [15:0] n451_O_t0b_t0b; // @[Top.scala 1360:22]
  wire [15:0] n451_O_t0b_t1b; // @[Top.scala 1360:22]
  wire [15:0] n451_O_t1b_t0b; // @[Top.scala 1360:22]
  wire [15:0] n451_O_t1b_t1b; // @[Top.scala 1360:22]
  wire  n452_valid_up; // @[Top.scala 1363:22]
  wire  n452_valid_down; // @[Top.scala 1363:22]
  wire  n452_I0; // @[Top.scala 1363:22]
  wire [15:0] n452_I1_t0b_t0b; // @[Top.scala 1363:22]
  wire [15:0] n452_I1_t0b_t1b; // @[Top.scala 1363:22]
  wire [15:0] n452_I1_t1b_t0b; // @[Top.scala 1363:22]
  wire [15:0] n452_I1_t1b_t1b; // @[Top.scala 1363:22]
  wire  n452_O_t0b; // @[Top.scala 1363:22]
  wire [15:0] n452_O_t1b_t0b_t0b; // @[Top.scala 1363:22]
  wire [15:0] n452_O_t1b_t0b_t1b; // @[Top.scala 1363:22]
  wire [15:0] n452_O_t1b_t1b_t0b; // @[Top.scala 1363:22]
  wire [15:0] n452_O_t1b_t1b_t1b; // @[Top.scala 1363:22]
  wire  n453_valid_up; // @[Top.scala 1367:22]
  wire  n453_valid_down; // @[Top.scala 1367:22]
  wire  n453_I_t0b; // @[Top.scala 1367:22]
  wire [15:0] n453_I_t1b_t0b_t0b; // @[Top.scala 1367:22]
  wire [15:0] n453_I_t1b_t0b_t1b; // @[Top.scala 1367:22]
  wire [15:0] n453_I_t1b_t1b_t0b; // @[Top.scala 1367:22]
  wire [15:0] n453_I_t1b_t1b_t1b; // @[Top.scala 1367:22]
  wire [15:0] n453_O_t0b; // @[Top.scala 1367:22]
  wire [15:0] n453_O_t1b; // @[Top.scala 1367:22]
  wire  n455_valid_up; // @[Top.scala 1370:22]
  wire  n455_valid_down; // @[Top.scala 1370:22]
  wire [15:0] n455_I0; // @[Top.scala 1370:22]
  wire [15:0] n455_I1_t0b; // @[Top.scala 1370:22]
  wire [15:0] n455_I1_t1b; // @[Top.scala 1370:22]
  wire [15:0] n455_O_t0b; // @[Top.scala 1370:22]
  wire [15:0] n455_O_t1b_t0b; // @[Top.scala 1370:22]
  wire [15:0] n455_O_t1b_t1b; // @[Top.scala 1370:22]
  Fst n426 ( // @[Top.scala 1285:22]
    .valid_up(n426_valid_up),
    .valid_down(n426_valid_down),
    .I_t0b(n426_I_t0b),
    .O(n426_O)
  );
  FIFO_2 n454 ( // @[Top.scala 1288:22]
    .clock(n454_clock),
    .reset(n454_reset),
    .valid_up(n454_valid_up),
    .valid_down(n454_valid_down),
    .I(n454_I),
    .O(n454_O)
  );
  FIFO_2 n442 ( // @[Top.scala 1291:22]
    .clock(n442_clock),
    .reset(n442_reset),
    .valid_up(n442_valid_up),
    .valid_down(n442_valid_down),
    .I(n442_I),
    .O(n442_O)
  );
  InitialDelayCounter_39 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n428 ( // @[Top.scala 1295:22]
    .valid_up(n428_valid_up),
    .valid_down(n428_valid_down),
    .I_t1b_t0b(n428_I_t1b_t0b),
    .I_t1b_t1b(n428_I_t1b_t1b),
    .O_t0b(n428_O_t0b),
    .O_t1b(n428_O_t1b)
  );
  Fst_1 n429 ( // @[Top.scala 1298:22]
    .valid_up(n429_valid_up),
    .valid_down(n429_valid_down),
    .I_t0b(n429_I_t0b),
    .O(n429_O)
  );
  Snd_1 n430 ( // @[Top.scala 1301:22]
    .valid_up(n430_valid_up),
    .valid_down(n430_valid_down),
    .I_t1b(n430_I_t1b),
    .O(n430_O)
  );
  AtomTuple n431 ( // @[Top.scala 1304:22]
    .valid_up(n431_valid_up),
    .valid_down(n431_valid_down),
    .I0(n431_I0),
    .I1(n431_I1),
    .O_t0b(n431_O_t0b),
    .O_t1b(n431_O_t1b)
  );
  Add n432 ( // @[Top.scala 1308:22]
    .valid_up(n432_valid_up),
    .valid_down(n432_valid_down),
    .I_t0b(n432_I_t0b),
    .I_t1b(n432_I_t1b),
    .O(n432_O)
  );
  AtomTuple n434 ( // @[Top.scala 1311:22]
    .valid_up(n434_valid_up),
    .valid_down(n434_valid_down),
    .I0(n434_I0),
    .I1(n434_I1),
    .O_t0b(n434_O_t0b),
    .O_t1b(n434_O_t1b)
  );
  Add n435 ( // @[Top.scala 1315:22]
    .valid_up(n435_valid_up),
    .valid_down(n435_valid_down),
    .I_t0b(n435_I_t0b),
    .I_t1b(n435_I_t1b),
    .O(n435_O)
  );
  InitialDelayCounter_39 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n438 ( // @[Top.scala 1319:22]
    .valid_up(n438_valid_up),
    .valid_down(n438_valid_down),
    .I0(n438_I0),
    .O_t0b(n438_O_t0b)
  );
  RShift n439 ( // @[Top.scala 1323:22]
    .valid_up(n439_valid_up),
    .valid_down(n439_valid_down),
    .I_t0b(n439_I_t0b),
    .O(n439_O)
  );
  AtomTuple n440 ( // @[Top.scala 1326:22]
    .valid_up(n440_valid_up),
    .valid_down(n440_valid_down),
    .I0(n440_I0),
    .I1(n440_I1),
    .O_t0b(n440_O_t0b),
    .O_t1b(n440_O_t1b)
  );
  Mul n441 ( // @[Top.scala 1330:22]
    .clock(n441_clock),
    .reset(n441_reset),
    .valid_up(n441_valid_up),
    .valid_down(n441_valid_down),
    .I_t0b(n441_I_t0b),
    .I_t1b(n441_I_t1b),
    .O(n441_O)
  );
  AtomTuple n443 ( // @[Top.scala 1333:22]
    .valid_up(n443_valid_up),
    .valid_down(n443_valid_down),
    .I0(n443_I0),
    .I1(n443_I1),
    .O_t0b(n443_O_t0b),
    .O_t1b(n443_O_t1b)
  );
  Lt n444 ( // @[Top.scala 1337:22]
    .valid_up(n444_valid_up),
    .valid_down(n444_valid_down),
    .I_t0b(n444_I_t0b),
    .I_t1b(n444_I_t1b),
    .O(n444_O)
  );
  InitialDelayCounter_39 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n446 ( // @[Top.scala 1341:22]
    .valid_up(n446_valid_up),
    .valid_down(n446_valid_down),
    .I0(n446_I0),
    .I1(n446_I1),
    .O_t0b(n446_O_t0b),
    .O_t1b(n446_O_t1b)
  );
  Sub n447 ( // @[Top.scala 1345:22]
    .valid_up(n447_valid_up),
    .valid_down(n447_valid_down),
    .I_t0b(n447_I_t0b),
    .I_t1b(n447_I_t1b),
    .O(n447_O)
  );
  AtomTuple n448 ( // @[Top.scala 1348:22]
    .valid_up(n448_valid_up),
    .valid_down(n448_valid_down),
    .I0(n448_I0),
    .I1(n448_I1),
    .O_t0b(n448_O_t0b),
    .O_t1b(n448_O_t1b)
  );
  AtomTuple n449 ( // @[Top.scala 1352:22]
    .valid_up(n449_valid_up),
    .valid_down(n449_valid_down),
    .I0(n449_I0),
    .I1(n449_I1),
    .O_t0b(n449_O_t0b),
    .O_t1b(n449_O_t1b)
  );
  AtomTuple_10 n450 ( // @[Top.scala 1356:22]
    .valid_up(n450_valid_up),
    .valid_down(n450_valid_down),
    .I0_t0b(n450_I0_t0b),
    .I0_t1b(n450_I0_t1b),
    .I1_t0b(n450_I1_t0b),
    .I1_t1b(n450_I1_t1b),
    .O_t0b_t0b(n450_O_t0b_t0b),
    .O_t0b_t1b(n450_O_t0b_t1b),
    .O_t1b_t0b(n450_O_t1b_t0b),
    .O_t1b_t1b(n450_O_t1b_t1b)
  );
  FIFO_4 n451 ( // @[Top.scala 1360:22]
    .clock(n451_clock),
    .reset(n451_reset),
    .valid_up(n451_valid_up),
    .valid_down(n451_valid_down),
    .I_t0b_t0b(n451_I_t0b_t0b),
    .I_t0b_t1b(n451_I_t0b_t1b),
    .I_t1b_t0b(n451_I_t1b_t0b),
    .I_t1b_t1b(n451_I_t1b_t1b),
    .O_t0b_t0b(n451_O_t0b_t0b),
    .O_t0b_t1b(n451_O_t0b_t1b),
    .O_t1b_t0b(n451_O_t1b_t0b),
    .O_t1b_t1b(n451_O_t1b_t1b)
  );
  AtomTuple_11 n452 ( // @[Top.scala 1363:22]
    .valid_up(n452_valid_up),
    .valid_down(n452_valid_down),
    .I0(n452_I0),
    .I1_t0b_t0b(n452_I1_t0b_t0b),
    .I1_t0b_t1b(n452_I1_t0b_t1b),
    .I1_t1b_t0b(n452_I1_t1b_t0b),
    .I1_t1b_t1b(n452_I1_t1b_t1b),
    .O_t0b(n452_O_t0b),
    .O_t1b_t0b_t0b(n452_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n452_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n452_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n452_O_t1b_t1b_t1b)
  );
  If n453 ( // @[Top.scala 1367:22]
    .valid_up(n453_valid_up),
    .valid_down(n453_valid_down),
    .I_t0b(n453_I_t0b),
    .I_t1b_t0b_t0b(n453_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n453_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n453_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n453_I_t1b_t1b_t1b),
    .O_t0b(n453_O_t0b),
    .O_t1b(n453_O_t1b)
  );
  AtomTuple_1 n455 ( // @[Top.scala 1370:22]
    .valid_up(n455_valid_up),
    .valid_down(n455_valid_down),
    .I0(n455_I0),
    .I1_t0b(n455_I1_t0b),
    .I1_t1b(n455_I1_t1b),
    .O_t0b(n455_O_t0b),
    .O_t1b_t0b(n455_O_t1b_t0b),
    .O_t1b_t1b(n455_O_t1b_t1b)
  );
  assign valid_down = n455_valid_down; // @[Top.scala 1375:16]
  assign O_t0b = n455_O_t0b; // @[Top.scala 1374:7]
  assign O_t1b_t0b = n455_O_t1b_t0b; // @[Top.scala 1374:7]
  assign O_t1b_t1b = n455_O_t1b_t1b; // @[Top.scala 1374:7]
  assign n426_valid_up = valid_up; // @[Top.scala 1287:19]
  assign n426_I_t0b = I_t0b; // @[Top.scala 1286:12]
  assign n454_clock = clock;
  assign n454_reset = reset;
  assign n454_valid_up = n426_valid_down; // @[Top.scala 1290:19]
  assign n454_I = n426_O; // @[Top.scala 1289:12]
  assign n442_clock = clock;
  assign n442_reset = reset;
  assign n442_valid_up = n426_valid_down; // @[Top.scala 1293:19]
  assign n442_I = n426_O; // @[Top.scala 1292:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n428_valid_up = valid_up; // @[Top.scala 1297:19]
  assign n428_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1296:12]
  assign n428_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1296:12]
  assign n429_valid_up = n428_valid_down; // @[Top.scala 1300:19]
  assign n429_I_t0b = n428_O_t0b; // @[Top.scala 1299:12]
  assign n430_valid_up = n428_valid_down; // @[Top.scala 1303:19]
  assign n430_I_t1b = n428_O_t1b; // @[Top.scala 1302:12]
  assign n431_valid_up = n429_valid_down & n430_valid_down; // @[Top.scala 1307:19]
  assign n431_I0 = n429_O; // @[Top.scala 1305:13]
  assign n431_I1 = n430_O; // @[Top.scala 1306:13]
  assign n432_valid_up = n431_valid_down; // @[Top.scala 1310:19]
  assign n432_I_t0b = n431_O_t0b; // @[Top.scala 1309:12]
  assign n432_I_t1b = n431_O_t1b; // @[Top.scala 1309:12]
  assign n434_valid_up = InitialDelayCounter_valid_down & n432_valid_down; // @[Top.scala 1314:19]
  assign n434_I0 = 16'h1; // @[Top.scala 1312:13]
  assign n434_I1 = n432_O; // @[Top.scala 1313:13]
  assign n435_valid_up = n434_valid_down; // @[Top.scala 1317:19]
  assign n435_I_t0b = n434_O_t0b; // @[Top.scala 1316:12]
  assign n435_I_t1b = n434_O_t1b; // @[Top.scala 1316:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n438_valid_up = n435_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1322:19]
  assign n438_I0 = n435_O; // @[Top.scala 1320:13]
  assign n439_valid_up = n438_valid_down; // @[Top.scala 1325:19]
  assign n439_I_t0b = n438_O_t0b; // @[Top.scala 1324:12]
  assign n440_valid_up = n439_valid_down; // @[Top.scala 1329:19]
  assign n440_I0 = n439_O; // @[Top.scala 1327:13]
  assign n440_I1 = n439_O; // @[Top.scala 1328:13]
  assign n441_clock = clock;
  assign n441_reset = reset;
  assign n441_valid_up = n440_valid_down; // @[Top.scala 1332:19]
  assign n441_I_t0b = n440_O_t0b; // @[Top.scala 1331:12]
  assign n441_I_t1b = n440_O_t1b; // @[Top.scala 1331:12]
  assign n443_valid_up = n442_valid_down & n441_valid_down; // @[Top.scala 1336:19]
  assign n443_I0 = n442_O; // @[Top.scala 1334:13]
  assign n443_I1 = n441_O; // @[Top.scala 1335:13]
  assign n444_valid_up = n443_valid_down; // @[Top.scala 1339:19]
  assign n444_I_t0b = n443_O_t0b; // @[Top.scala 1338:12]
  assign n444_I_t1b = n443_O_t1b; // @[Top.scala 1338:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n446_valid_up = n439_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1344:19]
  assign n446_I0 = n439_O; // @[Top.scala 1342:13]
  assign n446_I1 = 16'h1; // @[Top.scala 1343:13]
  assign n447_valid_up = n446_valid_down; // @[Top.scala 1347:19]
  assign n447_I_t0b = n446_O_t0b; // @[Top.scala 1346:12]
  assign n447_I_t1b = n446_O_t1b; // @[Top.scala 1346:12]
  assign n448_valid_up = n429_valid_down & n447_valid_down; // @[Top.scala 1351:19]
  assign n448_I0 = n429_O; // @[Top.scala 1349:13]
  assign n448_I1 = n447_O; // @[Top.scala 1350:13]
  assign n449_valid_up = n439_valid_down & n430_valid_down; // @[Top.scala 1355:19]
  assign n449_I0 = n439_O; // @[Top.scala 1353:13]
  assign n449_I1 = n430_O; // @[Top.scala 1354:13]
  assign n450_valid_up = n448_valid_down & n449_valid_down; // @[Top.scala 1359:19]
  assign n450_I0_t0b = n448_O_t0b; // @[Top.scala 1357:13]
  assign n450_I0_t1b = n448_O_t1b; // @[Top.scala 1357:13]
  assign n450_I1_t0b = n449_O_t0b; // @[Top.scala 1358:13]
  assign n450_I1_t1b = n449_O_t1b; // @[Top.scala 1358:13]
  assign n451_clock = clock;
  assign n451_reset = reset;
  assign n451_valid_up = n450_valid_down; // @[Top.scala 1362:19]
  assign n451_I_t0b_t0b = n450_O_t0b_t0b; // @[Top.scala 1361:12]
  assign n451_I_t0b_t1b = n450_O_t0b_t1b; // @[Top.scala 1361:12]
  assign n451_I_t1b_t0b = n450_O_t1b_t0b; // @[Top.scala 1361:12]
  assign n451_I_t1b_t1b = n450_O_t1b_t1b; // @[Top.scala 1361:12]
  assign n452_valid_up = n444_valid_down & n451_valid_down; // @[Top.scala 1366:19]
  assign n452_I0 = n444_O[0]; // @[Top.scala 1364:13]
  assign n452_I1_t0b_t0b = n451_O_t0b_t0b; // @[Top.scala 1365:13]
  assign n452_I1_t0b_t1b = n451_O_t0b_t1b; // @[Top.scala 1365:13]
  assign n452_I1_t1b_t0b = n451_O_t1b_t0b; // @[Top.scala 1365:13]
  assign n452_I1_t1b_t1b = n451_O_t1b_t1b; // @[Top.scala 1365:13]
  assign n453_valid_up = n452_valid_down; // @[Top.scala 1369:19]
  assign n453_I_t0b = n452_O_t0b; // @[Top.scala 1368:12]
  assign n453_I_t1b_t0b_t0b = n452_O_t1b_t0b_t0b; // @[Top.scala 1368:12]
  assign n453_I_t1b_t0b_t1b = n452_O_t1b_t0b_t1b; // @[Top.scala 1368:12]
  assign n453_I_t1b_t1b_t0b = n452_O_t1b_t1b_t0b; // @[Top.scala 1368:12]
  assign n453_I_t1b_t1b_t1b = n452_O_t1b_t1b_t1b; // @[Top.scala 1368:12]
  assign n455_valid_up = n454_valid_down & n453_valid_down; // @[Top.scala 1373:19]
  assign n455_I0 = n454_O; // @[Top.scala 1371:13]
  assign n455_I1_t0b = n453_O_t0b; // @[Top.scala 1372:13]
  assign n455_I1_t1b = n453_O_t1b; // @[Top.scala 1372:13]
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
module InitialDelayCounter_42(
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
  wire  n458_valid_up; // @[Top.scala 1381:22]
  wire  n458_valid_down; // @[Top.scala 1381:22]
  wire [15:0] n458_I_t0b; // @[Top.scala 1381:22]
  wire [15:0] n458_O; // @[Top.scala 1381:22]
  wire  n486_clock; // @[Top.scala 1384:22]
  wire  n486_reset; // @[Top.scala 1384:22]
  wire  n486_valid_up; // @[Top.scala 1384:22]
  wire  n486_valid_down; // @[Top.scala 1384:22]
  wire [15:0] n486_I; // @[Top.scala 1384:22]
  wire [15:0] n486_O; // @[Top.scala 1384:22]
  wire  n474_clock; // @[Top.scala 1387:22]
  wire  n474_reset; // @[Top.scala 1387:22]
  wire  n474_valid_up; // @[Top.scala 1387:22]
  wire  n474_valid_down; // @[Top.scala 1387:22]
  wire [15:0] n474_I; // @[Top.scala 1387:22]
  wire [15:0] n474_O; // @[Top.scala 1387:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n460_valid_up; // @[Top.scala 1391:22]
  wire  n460_valid_down; // @[Top.scala 1391:22]
  wire [15:0] n460_I_t1b_t0b; // @[Top.scala 1391:22]
  wire [15:0] n460_I_t1b_t1b; // @[Top.scala 1391:22]
  wire [15:0] n460_O_t0b; // @[Top.scala 1391:22]
  wire [15:0] n460_O_t1b; // @[Top.scala 1391:22]
  wire  n461_valid_up; // @[Top.scala 1394:22]
  wire  n461_valid_down; // @[Top.scala 1394:22]
  wire [15:0] n461_I_t0b; // @[Top.scala 1394:22]
  wire [15:0] n461_O; // @[Top.scala 1394:22]
  wire  n462_valid_up; // @[Top.scala 1397:22]
  wire  n462_valid_down; // @[Top.scala 1397:22]
  wire [15:0] n462_I_t1b; // @[Top.scala 1397:22]
  wire [15:0] n462_O; // @[Top.scala 1397:22]
  wire  n463_valid_up; // @[Top.scala 1400:22]
  wire  n463_valid_down; // @[Top.scala 1400:22]
  wire [15:0] n463_I0; // @[Top.scala 1400:22]
  wire [15:0] n463_I1; // @[Top.scala 1400:22]
  wire [15:0] n463_O_t0b; // @[Top.scala 1400:22]
  wire [15:0] n463_O_t1b; // @[Top.scala 1400:22]
  wire  n464_valid_up; // @[Top.scala 1404:22]
  wire  n464_valid_down; // @[Top.scala 1404:22]
  wire [15:0] n464_I_t0b; // @[Top.scala 1404:22]
  wire [15:0] n464_I_t1b; // @[Top.scala 1404:22]
  wire [15:0] n464_O; // @[Top.scala 1404:22]
  wire  n466_valid_up; // @[Top.scala 1407:22]
  wire  n466_valid_down; // @[Top.scala 1407:22]
  wire [15:0] n466_I0; // @[Top.scala 1407:22]
  wire [15:0] n466_I1; // @[Top.scala 1407:22]
  wire [15:0] n466_O_t0b; // @[Top.scala 1407:22]
  wire [15:0] n466_O_t1b; // @[Top.scala 1407:22]
  wire  n467_valid_up; // @[Top.scala 1411:22]
  wire  n467_valid_down; // @[Top.scala 1411:22]
  wire [15:0] n467_I_t0b; // @[Top.scala 1411:22]
  wire [15:0] n467_I_t1b; // @[Top.scala 1411:22]
  wire [15:0] n467_O; // @[Top.scala 1411:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n470_valid_up; // @[Top.scala 1415:22]
  wire  n470_valid_down; // @[Top.scala 1415:22]
  wire [15:0] n470_I0; // @[Top.scala 1415:22]
  wire [15:0] n470_O_t0b; // @[Top.scala 1415:22]
  wire  n471_valid_up; // @[Top.scala 1419:22]
  wire  n471_valid_down; // @[Top.scala 1419:22]
  wire [15:0] n471_I_t0b; // @[Top.scala 1419:22]
  wire [15:0] n471_O; // @[Top.scala 1419:22]
  wire  n472_valid_up; // @[Top.scala 1422:22]
  wire  n472_valid_down; // @[Top.scala 1422:22]
  wire [15:0] n472_I0; // @[Top.scala 1422:22]
  wire [15:0] n472_I1; // @[Top.scala 1422:22]
  wire [15:0] n472_O_t0b; // @[Top.scala 1422:22]
  wire [15:0] n472_O_t1b; // @[Top.scala 1422:22]
  wire  n473_clock; // @[Top.scala 1426:22]
  wire  n473_reset; // @[Top.scala 1426:22]
  wire  n473_valid_up; // @[Top.scala 1426:22]
  wire  n473_valid_down; // @[Top.scala 1426:22]
  wire [15:0] n473_I_t0b; // @[Top.scala 1426:22]
  wire [15:0] n473_I_t1b; // @[Top.scala 1426:22]
  wire [15:0] n473_O; // @[Top.scala 1426:22]
  wire  n475_valid_up; // @[Top.scala 1429:22]
  wire  n475_valid_down; // @[Top.scala 1429:22]
  wire [15:0] n475_I0; // @[Top.scala 1429:22]
  wire [15:0] n475_I1; // @[Top.scala 1429:22]
  wire [15:0] n475_O_t0b; // @[Top.scala 1429:22]
  wire [15:0] n475_O_t1b; // @[Top.scala 1429:22]
  wire  n476_valid_up; // @[Top.scala 1433:22]
  wire  n476_valid_down; // @[Top.scala 1433:22]
  wire [15:0] n476_I_t0b; // @[Top.scala 1433:22]
  wire [15:0] n476_I_t1b; // @[Top.scala 1433:22]
  wire [15:0] n476_O; // @[Top.scala 1433:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n478_valid_up; // @[Top.scala 1437:22]
  wire  n478_valid_down; // @[Top.scala 1437:22]
  wire [15:0] n478_I0; // @[Top.scala 1437:22]
  wire [15:0] n478_I1; // @[Top.scala 1437:22]
  wire [15:0] n478_O_t0b; // @[Top.scala 1437:22]
  wire [15:0] n478_O_t1b; // @[Top.scala 1437:22]
  wire  n479_valid_up; // @[Top.scala 1441:22]
  wire  n479_valid_down; // @[Top.scala 1441:22]
  wire [15:0] n479_I_t0b; // @[Top.scala 1441:22]
  wire [15:0] n479_I_t1b; // @[Top.scala 1441:22]
  wire [15:0] n479_O; // @[Top.scala 1441:22]
  wire  n480_valid_up; // @[Top.scala 1444:22]
  wire  n480_valid_down; // @[Top.scala 1444:22]
  wire [15:0] n480_I0; // @[Top.scala 1444:22]
  wire [15:0] n480_I1; // @[Top.scala 1444:22]
  wire [15:0] n480_O_t0b; // @[Top.scala 1444:22]
  wire [15:0] n480_O_t1b; // @[Top.scala 1444:22]
  wire  n481_valid_up; // @[Top.scala 1448:22]
  wire  n481_valid_down; // @[Top.scala 1448:22]
  wire [15:0] n481_I0; // @[Top.scala 1448:22]
  wire [15:0] n481_I1; // @[Top.scala 1448:22]
  wire [15:0] n481_O_t0b; // @[Top.scala 1448:22]
  wire [15:0] n481_O_t1b; // @[Top.scala 1448:22]
  wire  n482_valid_up; // @[Top.scala 1452:22]
  wire  n482_valid_down; // @[Top.scala 1452:22]
  wire [15:0] n482_I0_t0b; // @[Top.scala 1452:22]
  wire [15:0] n482_I0_t1b; // @[Top.scala 1452:22]
  wire [15:0] n482_I1_t0b; // @[Top.scala 1452:22]
  wire [15:0] n482_I1_t1b; // @[Top.scala 1452:22]
  wire [15:0] n482_O_t0b_t0b; // @[Top.scala 1452:22]
  wire [15:0] n482_O_t0b_t1b; // @[Top.scala 1452:22]
  wire [15:0] n482_O_t1b_t0b; // @[Top.scala 1452:22]
  wire [15:0] n482_O_t1b_t1b; // @[Top.scala 1452:22]
  wire  n483_clock; // @[Top.scala 1456:22]
  wire  n483_reset; // @[Top.scala 1456:22]
  wire  n483_valid_up; // @[Top.scala 1456:22]
  wire  n483_valid_down; // @[Top.scala 1456:22]
  wire [15:0] n483_I_t0b_t0b; // @[Top.scala 1456:22]
  wire [15:0] n483_I_t0b_t1b; // @[Top.scala 1456:22]
  wire [15:0] n483_I_t1b_t0b; // @[Top.scala 1456:22]
  wire [15:0] n483_I_t1b_t1b; // @[Top.scala 1456:22]
  wire [15:0] n483_O_t0b_t0b; // @[Top.scala 1456:22]
  wire [15:0] n483_O_t0b_t1b; // @[Top.scala 1456:22]
  wire [15:0] n483_O_t1b_t0b; // @[Top.scala 1456:22]
  wire [15:0] n483_O_t1b_t1b; // @[Top.scala 1456:22]
  wire  n484_valid_up; // @[Top.scala 1459:22]
  wire  n484_valid_down; // @[Top.scala 1459:22]
  wire  n484_I0; // @[Top.scala 1459:22]
  wire [15:0] n484_I1_t0b_t0b; // @[Top.scala 1459:22]
  wire [15:0] n484_I1_t0b_t1b; // @[Top.scala 1459:22]
  wire [15:0] n484_I1_t1b_t0b; // @[Top.scala 1459:22]
  wire [15:0] n484_I1_t1b_t1b; // @[Top.scala 1459:22]
  wire  n484_O_t0b; // @[Top.scala 1459:22]
  wire [15:0] n484_O_t1b_t0b_t0b; // @[Top.scala 1459:22]
  wire [15:0] n484_O_t1b_t0b_t1b; // @[Top.scala 1459:22]
  wire [15:0] n484_O_t1b_t1b_t0b; // @[Top.scala 1459:22]
  wire [15:0] n484_O_t1b_t1b_t1b; // @[Top.scala 1459:22]
  wire  n485_valid_up; // @[Top.scala 1463:22]
  wire  n485_valid_down; // @[Top.scala 1463:22]
  wire  n485_I_t0b; // @[Top.scala 1463:22]
  wire [15:0] n485_I_t1b_t0b_t0b; // @[Top.scala 1463:22]
  wire [15:0] n485_I_t1b_t0b_t1b; // @[Top.scala 1463:22]
  wire [15:0] n485_I_t1b_t1b_t0b; // @[Top.scala 1463:22]
  wire [15:0] n485_I_t1b_t1b_t1b; // @[Top.scala 1463:22]
  wire [15:0] n485_O_t0b; // @[Top.scala 1463:22]
  wire [15:0] n485_O_t1b; // @[Top.scala 1463:22]
  wire  n487_valid_up; // @[Top.scala 1466:22]
  wire  n487_valid_down; // @[Top.scala 1466:22]
  wire [15:0] n487_I0; // @[Top.scala 1466:22]
  wire [15:0] n487_I1_t0b; // @[Top.scala 1466:22]
  wire [15:0] n487_I1_t1b; // @[Top.scala 1466:22]
  wire [15:0] n487_O_t0b; // @[Top.scala 1466:22]
  wire [15:0] n487_O_t1b_t0b; // @[Top.scala 1466:22]
  wire [15:0] n487_O_t1b_t1b; // @[Top.scala 1466:22]
  Fst n458 ( // @[Top.scala 1381:22]
    .valid_up(n458_valid_up),
    .valid_down(n458_valid_down),
    .I_t0b(n458_I_t0b),
    .O(n458_O)
  );
  FIFO_2 n486 ( // @[Top.scala 1384:22]
    .clock(n486_clock),
    .reset(n486_reset),
    .valid_up(n486_valid_up),
    .valid_down(n486_valid_down),
    .I(n486_I),
    .O(n486_O)
  );
  FIFO_2 n474 ( // @[Top.scala 1387:22]
    .clock(n474_clock),
    .reset(n474_reset),
    .valid_up(n474_valid_up),
    .valid_down(n474_valid_down),
    .I(n474_I),
    .O(n474_O)
  );
  InitialDelayCounter_42 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n460 ( // @[Top.scala 1391:22]
    .valid_up(n460_valid_up),
    .valid_down(n460_valid_down),
    .I_t1b_t0b(n460_I_t1b_t0b),
    .I_t1b_t1b(n460_I_t1b_t1b),
    .O_t0b(n460_O_t0b),
    .O_t1b(n460_O_t1b)
  );
  Fst_1 n461 ( // @[Top.scala 1394:22]
    .valid_up(n461_valid_up),
    .valid_down(n461_valid_down),
    .I_t0b(n461_I_t0b),
    .O(n461_O)
  );
  Snd_1 n462 ( // @[Top.scala 1397:22]
    .valid_up(n462_valid_up),
    .valid_down(n462_valid_down),
    .I_t1b(n462_I_t1b),
    .O(n462_O)
  );
  AtomTuple n463 ( // @[Top.scala 1400:22]
    .valid_up(n463_valid_up),
    .valid_down(n463_valid_down),
    .I0(n463_I0),
    .I1(n463_I1),
    .O_t0b(n463_O_t0b),
    .O_t1b(n463_O_t1b)
  );
  Add n464 ( // @[Top.scala 1404:22]
    .valid_up(n464_valid_up),
    .valid_down(n464_valid_down),
    .I_t0b(n464_I_t0b),
    .I_t1b(n464_I_t1b),
    .O(n464_O)
  );
  AtomTuple n466 ( // @[Top.scala 1407:22]
    .valid_up(n466_valid_up),
    .valid_down(n466_valid_down),
    .I0(n466_I0),
    .I1(n466_I1),
    .O_t0b(n466_O_t0b),
    .O_t1b(n466_O_t1b)
  );
  Add n467 ( // @[Top.scala 1411:22]
    .valid_up(n467_valid_up),
    .valid_down(n467_valid_down),
    .I_t0b(n467_I_t0b),
    .I_t1b(n467_I_t1b),
    .O(n467_O)
  );
  InitialDelayCounter_42 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n470 ( // @[Top.scala 1415:22]
    .valid_up(n470_valid_up),
    .valid_down(n470_valid_down),
    .I0(n470_I0),
    .O_t0b(n470_O_t0b)
  );
  RShift n471 ( // @[Top.scala 1419:22]
    .valid_up(n471_valid_up),
    .valid_down(n471_valid_down),
    .I_t0b(n471_I_t0b),
    .O(n471_O)
  );
  AtomTuple n472 ( // @[Top.scala 1422:22]
    .valid_up(n472_valid_up),
    .valid_down(n472_valid_down),
    .I0(n472_I0),
    .I1(n472_I1),
    .O_t0b(n472_O_t0b),
    .O_t1b(n472_O_t1b)
  );
  Mul n473 ( // @[Top.scala 1426:22]
    .clock(n473_clock),
    .reset(n473_reset),
    .valid_up(n473_valid_up),
    .valid_down(n473_valid_down),
    .I_t0b(n473_I_t0b),
    .I_t1b(n473_I_t1b),
    .O(n473_O)
  );
  AtomTuple n475 ( // @[Top.scala 1429:22]
    .valid_up(n475_valid_up),
    .valid_down(n475_valid_down),
    .I0(n475_I0),
    .I1(n475_I1),
    .O_t0b(n475_O_t0b),
    .O_t1b(n475_O_t1b)
  );
  Lt n476 ( // @[Top.scala 1433:22]
    .valid_up(n476_valid_up),
    .valid_down(n476_valid_down),
    .I_t0b(n476_I_t0b),
    .I_t1b(n476_I_t1b),
    .O(n476_O)
  );
  InitialDelayCounter_42 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n478 ( // @[Top.scala 1437:22]
    .valid_up(n478_valid_up),
    .valid_down(n478_valid_down),
    .I0(n478_I0),
    .I1(n478_I1),
    .O_t0b(n478_O_t0b),
    .O_t1b(n478_O_t1b)
  );
  Sub n479 ( // @[Top.scala 1441:22]
    .valid_up(n479_valid_up),
    .valid_down(n479_valid_down),
    .I_t0b(n479_I_t0b),
    .I_t1b(n479_I_t1b),
    .O(n479_O)
  );
  AtomTuple n480 ( // @[Top.scala 1444:22]
    .valid_up(n480_valid_up),
    .valid_down(n480_valid_down),
    .I0(n480_I0),
    .I1(n480_I1),
    .O_t0b(n480_O_t0b),
    .O_t1b(n480_O_t1b)
  );
  AtomTuple n481 ( // @[Top.scala 1448:22]
    .valid_up(n481_valid_up),
    .valid_down(n481_valid_down),
    .I0(n481_I0),
    .I1(n481_I1),
    .O_t0b(n481_O_t0b),
    .O_t1b(n481_O_t1b)
  );
  AtomTuple_10 n482 ( // @[Top.scala 1452:22]
    .valid_up(n482_valid_up),
    .valid_down(n482_valid_down),
    .I0_t0b(n482_I0_t0b),
    .I0_t1b(n482_I0_t1b),
    .I1_t0b(n482_I1_t0b),
    .I1_t1b(n482_I1_t1b),
    .O_t0b_t0b(n482_O_t0b_t0b),
    .O_t0b_t1b(n482_O_t0b_t1b),
    .O_t1b_t0b(n482_O_t1b_t0b),
    .O_t1b_t1b(n482_O_t1b_t1b)
  );
  FIFO_4 n483 ( // @[Top.scala 1456:22]
    .clock(n483_clock),
    .reset(n483_reset),
    .valid_up(n483_valid_up),
    .valid_down(n483_valid_down),
    .I_t0b_t0b(n483_I_t0b_t0b),
    .I_t0b_t1b(n483_I_t0b_t1b),
    .I_t1b_t0b(n483_I_t1b_t0b),
    .I_t1b_t1b(n483_I_t1b_t1b),
    .O_t0b_t0b(n483_O_t0b_t0b),
    .O_t0b_t1b(n483_O_t0b_t1b),
    .O_t1b_t0b(n483_O_t1b_t0b),
    .O_t1b_t1b(n483_O_t1b_t1b)
  );
  AtomTuple_11 n484 ( // @[Top.scala 1459:22]
    .valid_up(n484_valid_up),
    .valid_down(n484_valid_down),
    .I0(n484_I0),
    .I1_t0b_t0b(n484_I1_t0b_t0b),
    .I1_t0b_t1b(n484_I1_t0b_t1b),
    .I1_t1b_t0b(n484_I1_t1b_t0b),
    .I1_t1b_t1b(n484_I1_t1b_t1b),
    .O_t0b(n484_O_t0b),
    .O_t1b_t0b_t0b(n484_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n484_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n484_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n484_O_t1b_t1b_t1b)
  );
  If n485 ( // @[Top.scala 1463:22]
    .valid_up(n485_valid_up),
    .valid_down(n485_valid_down),
    .I_t0b(n485_I_t0b),
    .I_t1b_t0b_t0b(n485_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n485_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n485_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n485_I_t1b_t1b_t1b),
    .O_t0b(n485_O_t0b),
    .O_t1b(n485_O_t1b)
  );
  AtomTuple_1 n487 ( // @[Top.scala 1466:22]
    .valid_up(n487_valid_up),
    .valid_down(n487_valid_down),
    .I0(n487_I0),
    .I1_t0b(n487_I1_t0b),
    .I1_t1b(n487_I1_t1b),
    .O_t0b(n487_O_t0b),
    .O_t1b_t0b(n487_O_t1b_t0b),
    .O_t1b_t1b(n487_O_t1b_t1b)
  );
  assign valid_down = n487_valid_down; // @[Top.scala 1471:16]
  assign O_t0b = n487_O_t0b; // @[Top.scala 1470:7]
  assign O_t1b_t0b = n487_O_t1b_t0b; // @[Top.scala 1470:7]
  assign O_t1b_t1b = n487_O_t1b_t1b; // @[Top.scala 1470:7]
  assign n458_valid_up = valid_up; // @[Top.scala 1383:19]
  assign n458_I_t0b = I_t0b; // @[Top.scala 1382:12]
  assign n486_clock = clock;
  assign n486_reset = reset;
  assign n486_valid_up = n458_valid_down; // @[Top.scala 1386:19]
  assign n486_I = n458_O; // @[Top.scala 1385:12]
  assign n474_clock = clock;
  assign n474_reset = reset;
  assign n474_valid_up = n458_valid_down; // @[Top.scala 1389:19]
  assign n474_I = n458_O; // @[Top.scala 1388:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n460_valid_up = valid_up; // @[Top.scala 1393:19]
  assign n460_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1392:12]
  assign n460_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1392:12]
  assign n461_valid_up = n460_valid_down; // @[Top.scala 1396:19]
  assign n461_I_t0b = n460_O_t0b; // @[Top.scala 1395:12]
  assign n462_valid_up = n460_valid_down; // @[Top.scala 1399:19]
  assign n462_I_t1b = n460_O_t1b; // @[Top.scala 1398:12]
  assign n463_valid_up = n461_valid_down & n462_valid_down; // @[Top.scala 1403:19]
  assign n463_I0 = n461_O; // @[Top.scala 1401:13]
  assign n463_I1 = n462_O; // @[Top.scala 1402:13]
  assign n464_valid_up = n463_valid_down; // @[Top.scala 1406:19]
  assign n464_I_t0b = n463_O_t0b; // @[Top.scala 1405:12]
  assign n464_I_t1b = n463_O_t1b; // @[Top.scala 1405:12]
  assign n466_valid_up = InitialDelayCounter_valid_down & n464_valid_down; // @[Top.scala 1410:19]
  assign n466_I0 = 16'h1; // @[Top.scala 1408:13]
  assign n466_I1 = n464_O; // @[Top.scala 1409:13]
  assign n467_valid_up = n466_valid_down; // @[Top.scala 1413:19]
  assign n467_I_t0b = n466_O_t0b; // @[Top.scala 1412:12]
  assign n467_I_t1b = n466_O_t1b; // @[Top.scala 1412:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n470_valid_up = n467_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1418:19]
  assign n470_I0 = n467_O; // @[Top.scala 1416:13]
  assign n471_valid_up = n470_valid_down; // @[Top.scala 1421:19]
  assign n471_I_t0b = n470_O_t0b; // @[Top.scala 1420:12]
  assign n472_valid_up = n471_valid_down; // @[Top.scala 1425:19]
  assign n472_I0 = n471_O; // @[Top.scala 1423:13]
  assign n472_I1 = n471_O; // @[Top.scala 1424:13]
  assign n473_clock = clock;
  assign n473_reset = reset;
  assign n473_valid_up = n472_valid_down; // @[Top.scala 1428:19]
  assign n473_I_t0b = n472_O_t0b; // @[Top.scala 1427:12]
  assign n473_I_t1b = n472_O_t1b; // @[Top.scala 1427:12]
  assign n475_valid_up = n474_valid_down & n473_valid_down; // @[Top.scala 1432:19]
  assign n475_I0 = n474_O; // @[Top.scala 1430:13]
  assign n475_I1 = n473_O; // @[Top.scala 1431:13]
  assign n476_valid_up = n475_valid_down; // @[Top.scala 1435:19]
  assign n476_I_t0b = n475_O_t0b; // @[Top.scala 1434:12]
  assign n476_I_t1b = n475_O_t1b; // @[Top.scala 1434:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n478_valid_up = n471_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1440:19]
  assign n478_I0 = n471_O; // @[Top.scala 1438:13]
  assign n478_I1 = 16'h1; // @[Top.scala 1439:13]
  assign n479_valid_up = n478_valid_down; // @[Top.scala 1443:19]
  assign n479_I_t0b = n478_O_t0b; // @[Top.scala 1442:12]
  assign n479_I_t1b = n478_O_t1b; // @[Top.scala 1442:12]
  assign n480_valid_up = n461_valid_down & n479_valid_down; // @[Top.scala 1447:19]
  assign n480_I0 = n461_O; // @[Top.scala 1445:13]
  assign n480_I1 = n479_O; // @[Top.scala 1446:13]
  assign n481_valid_up = n471_valid_down & n462_valid_down; // @[Top.scala 1451:19]
  assign n481_I0 = n471_O; // @[Top.scala 1449:13]
  assign n481_I1 = n462_O; // @[Top.scala 1450:13]
  assign n482_valid_up = n480_valid_down & n481_valid_down; // @[Top.scala 1455:19]
  assign n482_I0_t0b = n480_O_t0b; // @[Top.scala 1453:13]
  assign n482_I0_t1b = n480_O_t1b; // @[Top.scala 1453:13]
  assign n482_I1_t0b = n481_O_t0b; // @[Top.scala 1454:13]
  assign n482_I1_t1b = n481_O_t1b; // @[Top.scala 1454:13]
  assign n483_clock = clock;
  assign n483_reset = reset;
  assign n483_valid_up = n482_valid_down; // @[Top.scala 1458:19]
  assign n483_I_t0b_t0b = n482_O_t0b_t0b; // @[Top.scala 1457:12]
  assign n483_I_t0b_t1b = n482_O_t0b_t1b; // @[Top.scala 1457:12]
  assign n483_I_t1b_t0b = n482_O_t1b_t0b; // @[Top.scala 1457:12]
  assign n483_I_t1b_t1b = n482_O_t1b_t1b; // @[Top.scala 1457:12]
  assign n484_valid_up = n476_valid_down & n483_valid_down; // @[Top.scala 1462:19]
  assign n484_I0 = n476_O[0]; // @[Top.scala 1460:13]
  assign n484_I1_t0b_t0b = n483_O_t0b_t0b; // @[Top.scala 1461:13]
  assign n484_I1_t0b_t1b = n483_O_t0b_t1b; // @[Top.scala 1461:13]
  assign n484_I1_t1b_t0b = n483_O_t1b_t0b; // @[Top.scala 1461:13]
  assign n484_I1_t1b_t1b = n483_O_t1b_t1b; // @[Top.scala 1461:13]
  assign n485_valid_up = n484_valid_down; // @[Top.scala 1465:19]
  assign n485_I_t0b = n484_O_t0b; // @[Top.scala 1464:12]
  assign n485_I_t1b_t0b_t0b = n484_O_t1b_t0b_t0b; // @[Top.scala 1464:12]
  assign n485_I_t1b_t0b_t1b = n484_O_t1b_t0b_t1b; // @[Top.scala 1464:12]
  assign n485_I_t1b_t1b_t0b = n484_O_t1b_t1b_t0b; // @[Top.scala 1464:12]
  assign n485_I_t1b_t1b_t1b = n484_O_t1b_t1b_t1b; // @[Top.scala 1464:12]
  assign n487_valid_up = n486_valid_down & n485_valid_down; // @[Top.scala 1469:19]
  assign n487_I0 = n486_O; // @[Top.scala 1467:13]
  assign n487_I1_t0b = n485_O_t0b; // @[Top.scala 1468:13]
  assign n487_I1_t1b = n485_O_t1b; // @[Top.scala 1468:13]
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
module InitialDelayCounter_45(
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
  wire  n490_valid_up; // @[Top.scala 1477:22]
  wire  n490_valid_down; // @[Top.scala 1477:22]
  wire [15:0] n490_I_t0b; // @[Top.scala 1477:22]
  wire [15:0] n490_O; // @[Top.scala 1477:22]
  wire  n518_clock; // @[Top.scala 1480:22]
  wire  n518_reset; // @[Top.scala 1480:22]
  wire  n518_valid_up; // @[Top.scala 1480:22]
  wire  n518_valid_down; // @[Top.scala 1480:22]
  wire [15:0] n518_I; // @[Top.scala 1480:22]
  wire [15:0] n518_O; // @[Top.scala 1480:22]
  wire  n506_clock; // @[Top.scala 1483:22]
  wire  n506_reset; // @[Top.scala 1483:22]
  wire  n506_valid_up; // @[Top.scala 1483:22]
  wire  n506_valid_down; // @[Top.scala 1483:22]
  wire [15:0] n506_I; // @[Top.scala 1483:22]
  wire [15:0] n506_O; // @[Top.scala 1483:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n492_valid_up; // @[Top.scala 1487:22]
  wire  n492_valid_down; // @[Top.scala 1487:22]
  wire [15:0] n492_I_t1b_t0b; // @[Top.scala 1487:22]
  wire [15:0] n492_I_t1b_t1b; // @[Top.scala 1487:22]
  wire [15:0] n492_O_t0b; // @[Top.scala 1487:22]
  wire [15:0] n492_O_t1b; // @[Top.scala 1487:22]
  wire  n493_valid_up; // @[Top.scala 1490:22]
  wire  n493_valid_down; // @[Top.scala 1490:22]
  wire [15:0] n493_I_t0b; // @[Top.scala 1490:22]
  wire [15:0] n493_O; // @[Top.scala 1490:22]
  wire  n494_valid_up; // @[Top.scala 1493:22]
  wire  n494_valid_down; // @[Top.scala 1493:22]
  wire [15:0] n494_I_t1b; // @[Top.scala 1493:22]
  wire [15:0] n494_O; // @[Top.scala 1493:22]
  wire  n495_valid_up; // @[Top.scala 1496:22]
  wire  n495_valid_down; // @[Top.scala 1496:22]
  wire [15:0] n495_I0; // @[Top.scala 1496:22]
  wire [15:0] n495_I1; // @[Top.scala 1496:22]
  wire [15:0] n495_O_t0b; // @[Top.scala 1496:22]
  wire [15:0] n495_O_t1b; // @[Top.scala 1496:22]
  wire  n496_valid_up; // @[Top.scala 1500:22]
  wire  n496_valid_down; // @[Top.scala 1500:22]
  wire [15:0] n496_I_t0b; // @[Top.scala 1500:22]
  wire [15:0] n496_I_t1b; // @[Top.scala 1500:22]
  wire [15:0] n496_O; // @[Top.scala 1500:22]
  wire  n498_valid_up; // @[Top.scala 1503:22]
  wire  n498_valid_down; // @[Top.scala 1503:22]
  wire [15:0] n498_I0; // @[Top.scala 1503:22]
  wire [15:0] n498_I1; // @[Top.scala 1503:22]
  wire [15:0] n498_O_t0b; // @[Top.scala 1503:22]
  wire [15:0] n498_O_t1b; // @[Top.scala 1503:22]
  wire  n499_valid_up; // @[Top.scala 1507:22]
  wire  n499_valid_down; // @[Top.scala 1507:22]
  wire [15:0] n499_I_t0b; // @[Top.scala 1507:22]
  wire [15:0] n499_I_t1b; // @[Top.scala 1507:22]
  wire [15:0] n499_O; // @[Top.scala 1507:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n502_valid_up; // @[Top.scala 1511:22]
  wire  n502_valid_down; // @[Top.scala 1511:22]
  wire [15:0] n502_I0; // @[Top.scala 1511:22]
  wire [15:0] n502_O_t0b; // @[Top.scala 1511:22]
  wire  n503_valid_up; // @[Top.scala 1515:22]
  wire  n503_valid_down; // @[Top.scala 1515:22]
  wire [15:0] n503_I_t0b; // @[Top.scala 1515:22]
  wire [15:0] n503_O; // @[Top.scala 1515:22]
  wire  n504_valid_up; // @[Top.scala 1518:22]
  wire  n504_valid_down; // @[Top.scala 1518:22]
  wire [15:0] n504_I0; // @[Top.scala 1518:22]
  wire [15:0] n504_I1; // @[Top.scala 1518:22]
  wire [15:0] n504_O_t0b; // @[Top.scala 1518:22]
  wire [15:0] n504_O_t1b; // @[Top.scala 1518:22]
  wire  n505_clock; // @[Top.scala 1522:22]
  wire  n505_reset; // @[Top.scala 1522:22]
  wire  n505_valid_up; // @[Top.scala 1522:22]
  wire  n505_valid_down; // @[Top.scala 1522:22]
  wire [15:0] n505_I_t0b; // @[Top.scala 1522:22]
  wire [15:0] n505_I_t1b; // @[Top.scala 1522:22]
  wire [15:0] n505_O; // @[Top.scala 1522:22]
  wire  n507_valid_up; // @[Top.scala 1525:22]
  wire  n507_valid_down; // @[Top.scala 1525:22]
  wire [15:0] n507_I0; // @[Top.scala 1525:22]
  wire [15:0] n507_I1; // @[Top.scala 1525:22]
  wire [15:0] n507_O_t0b; // @[Top.scala 1525:22]
  wire [15:0] n507_O_t1b; // @[Top.scala 1525:22]
  wire  n508_valid_up; // @[Top.scala 1529:22]
  wire  n508_valid_down; // @[Top.scala 1529:22]
  wire [15:0] n508_I_t0b; // @[Top.scala 1529:22]
  wire [15:0] n508_I_t1b; // @[Top.scala 1529:22]
  wire [15:0] n508_O; // @[Top.scala 1529:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n510_valid_up; // @[Top.scala 1533:22]
  wire  n510_valid_down; // @[Top.scala 1533:22]
  wire [15:0] n510_I0; // @[Top.scala 1533:22]
  wire [15:0] n510_I1; // @[Top.scala 1533:22]
  wire [15:0] n510_O_t0b; // @[Top.scala 1533:22]
  wire [15:0] n510_O_t1b; // @[Top.scala 1533:22]
  wire  n511_valid_up; // @[Top.scala 1537:22]
  wire  n511_valid_down; // @[Top.scala 1537:22]
  wire [15:0] n511_I_t0b; // @[Top.scala 1537:22]
  wire [15:0] n511_I_t1b; // @[Top.scala 1537:22]
  wire [15:0] n511_O; // @[Top.scala 1537:22]
  wire  n512_valid_up; // @[Top.scala 1540:22]
  wire  n512_valid_down; // @[Top.scala 1540:22]
  wire [15:0] n512_I0; // @[Top.scala 1540:22]
  wire [15:0] n512_I1; // @[Top.scala 1540:22]
  wire [15:0] n512_O_t0b; // @[Top.scala 1540:22]
  wire [15:0] n512_O_t1b; // @[Top.scala 1540:22]
  wire  n513_valid_up; // @[Top.scala 1544:22]
  wire  n513_valid_down; // @[Top.scala 1544:22]
  wire [15:0] n513_I0; // @[Top.scala 1544:22]
  wire [15:0] n513_I1; // @[Top.scala 1544:22]
  wire [15:0] n513_O_t0b; // @[Top.scala 1544:22]
  wire [15:0] n513_O_t1b; // @[Top.scala 1544:22]
  wire  n514_valid_up; // @[Top.scala 1548:22]
  wire  n514_valid_down; // @[Top.scala 1548:22]
  wire [15:0] n514_I0_t0b; // @[Top.scala 1548:22]
  wire [15:0] n514_I0_t1b; // @[Top.scala 1548:22]
  wire [15:0] n514_I1_t0b; // @[Top.scala 1548:22]
  wire [15:0] n514_I1_t1b; // @[Top.scala 1548:22]
  wire [15:0] n514_O_t0b_t0b; // @[Top.scala 1548:22]
  wire [15:0] n514_O_t0b_t1b; // @[Top.scala 1548:22]
  wire [15:0] n514_O_t1b_t0b; // @[Top.scala 1548:22]
  wire [15:0] n514_O_t1b_t1b; // @[Top.scala 1548:22]
  wire  n515_clock; // @[Top.scala 1552:22]
  wire  n515_reset; // @[Top.scala 1552:22]
  wire  n515_valid_up; // @[Top.scala 1552:22]
  wire  n515_valid_down; // @[Top.scala 1552:22]
  wire [15:0] n515_I_t0b_t0b; // @[Top.scala 1552:22]
  wire [15:0] n515_I_t0b_t1b; // @[Top.scala 1552:22]
  wire [15:0] n515_I_t1b_t0b; // @[Top.scala 1552:22]
  wire [15:0] n515_I_t1b_t1b; // @[Top.scala 1552:22]
  wire [15:0] n515_O_t0b_t0b; // @[Top.scala 1552:22]
  wire [15:0] n515_O_t0b_t1b; // @[Top.scala 1552:22]
  wire [15:0] n515_O_t1b_t0b; // @[Top.scala 1552:22]
  wire [15:0] n515_O_t1b_t1b; // @[Top.scala 1552:22]
  wire  n516_valid_up; // @[Top.scala 1555:22]
  wire  n516_valid_down; // @[Top.scala 1555:22]
  wire  n516_I0; // @[Top.scala 1555:22]
  wire [15:0] n516_I1_t0b_t0b; // @[Top.scala 1555:22]
  wire [15:0] n516_I1_t0b_t1b; // @[Top.scala 1555:22]
  wire [15:0] n516_I1_t1b_t0b; // @[Top.scala 1555:22]
  wire [15:0] n516_I1_t1b_t1b; // @[Top.scala 1555:22]
  wire  n516_O_t0b; // @[Top.scala 1555:22]
  wire [15:0] n516_O_t1b_t0b_t0b; // @[Top.scala 1555:22]
  wire [15:0] n516_O_t1b_t0b_t1b; // @[Top.scala 1555:22]
  wire [15:0] n516_O_t1b_t1b_t0b; // @[Top.scala 1555:22]
  wire [15:0] n516_O_t1b_t1b_t1b; // @[Top.scala 1555:22]
  wire  n517_valid_up; // @[Top.scala 1559:22]
  wire  n517_valid_down; // @[Top.scala 1559:22]
  wire  n517_I_t0b; // @[Top.scala 1559:22]
  wire [15:0] n517_I_t1b_t0b_t0b; // @[Top.scala 1559:22]
  wire [15:0] n517_I_t1b_t0b_t1b; // @[Top.scala 1559:22]
  wire [15:0] n517_I_t1b_t1b_t0b; // @[Top.scala 1559:22]
  wire [15:0] n517_I_t1b_t1b_t1b; // @[Top.scala 1559:22]
  wire [15:0] n517_O_t0b; // @[Top.scala 1559:22]
  wire [15:0] n517_O_t1b; // @[Top.scala 1559:22]
  wire  n519_valid_up; // @[Top.scala 1562:22]
  wire  n519_valid_down; // @[Top.scala 1562:22]
  wire [15:0] n519_I0; // @[Top.scala 1562:22]
  wire [15:0] n519_I1_t0b; // @[Top.scala 1562:22]
  wire [15:0] n519_I1_t1b; // @[Top.scala 1562:22]
  wire [15:0] n519_O_t0b; // @[Top.scala 1562:22]
  wire [15:0] n519_O_t1b_t0b; // @[Top.scala 1562:22]
  wire [15:0] n519_O_t1b_t1b; // @[Top.scala 1562:22]
  Fst n490 ( // @[Top.scala 1477:22]
    .valid_up(n490_valid_up),
    .valid_down(n490_valid_down),
    .I_t0b(n490_I_t0b),
    .O(n490_O)
  );
  FIFO_2 n518 ( // @[Top.scala 1480:22]
    .clock(n518_clock),
    .reset(n518_reset),
    .valid_up(n518_valid_up),
    .valid_down(n518_valid_down),
    .I(n518_I),
    .O(n518_O)
  );
  FIFO_2 n506 ( // @[Top.scala 1483:22]
    .clock(n506_clock),
    .reset(n506_reset),
    .valid_up(n506_valid_up),
    .valid_down(n506_valid_down),
    .I(n506_I),
    .O(n506_O)
  );
  InitialDelayCounter_45 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n492 ( // @[Top.scala 1487:22]
    .valid_up(n492_valid_up),
    .valid_down(n492_valid_down),
    .I_t1b_t0b(n492_I_t1b_t0b),
    .I_t1b_t1b(n492_I_t1b_t1b),
    .O_t0b(n492_O_t0b),
    .O_t1b(n492_O_t1b)
  );
  Fst_1 n493 ( // @[Top.scala 1490:22]
    .valid_up(n493_valid_up),
    .valid_down(n493_valid_down),
    .I_t0b(n493_I_t0b),
    .O(n493_O)
  );
  Snd_1 n494 ( // @[Top.scala 1493:22]
    .valid_up(n494_valid_up),
    .valid_down(n494_valid_down),
    .I_t1b(n494_I_t1b),
    .O(n494_O)
  );
  AtomTuple n495 ( // @[Top.scala 1496:22]
    .valid_up(n495_valid_up),
    .valid_down(n495_valid_down),
    .I0(n495_I0),
    .I1(n495_I1),
    .O_t0b(n495_O_t0b),
    .O_t1b(n495_O_t1b)
  );
  Add n496 ( // @[Top.scala 1500:22]
    .valid_up(n496_valid_up),
    .valid_down(n496_valid_down),
    .I_t0b(n496_I_t0b),
    .I_t1b(n496_I_t1b),
    .O(n496_O)
  );
  AtomTuple n498 ( // @[Top.scala 1503:22]
    .valid_up(n498_valid_up),
    .valid_down(n498_valid_down),
    .I0(n498_I0),
    .I1(n498_I1),
    .O_t0b(n498_O_t0b),
    .O_t1b(n498_O_t1b)
  );
  Add n499 ( // @[Top.scala 1507:22]
    .valid_up(n499_valid_up),
    .valid_down(n499_valid_down),
    .I_t0b(n499_I_t0b),
    .I_t1b(n499_I_t1b),
    .O(n499_O)
  );
  InitialDelayCounter_45 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n502 ( // @[Top.scala 1511:22]
    .valid_up(n502_valid_up),
    .valid_down(n502_valid_down),
    .I0(n502_I0),
    .O_t0b(n502_O_t0b)
  );
  RShift n503 ( // @[Top.scala 1515:22]
    .valid_up(n503_valid_up),
    .valid_down(n503_valid_down),
    .I_t0b(n503_I_t0b),
    .O(n503_O)
  );
  AtomTuple n504 ( // @[Top.scala 1518:22]
    .valid_up(n504_valid_up),
    .valid_down(n504_valid_down),
    .I0(n504_I0),
    .I1(n504_I1),
    .O_t0b(n504_O_t0b),
    .O_t1b(n504_O_t1b)
  );
  Mul n505 ( // @[Top.scala 1522:22]
    .clock(n505_clock),
    .reset(n505_reset),
    .valid_up(n505_valid_up),
    .valid_down(n505_valid_down),
    .I_t0b(n505_I_t0b),
    .I_t1b(n505_I_t1b),
    .O(n505_O)
  );
  AtomTuple n507 ( // @[Top.scala 1525:22]
    .valid_up(n507_valid_up),
    .valid_down(n507_valid_down),
    .I0(n507_I0),
    .I1(n507_I1),
    .O_t0b(n507_O_t0b),
    .O_t1b(n507_O_t1b)
  );
  Lt n508 ( // @[Top.scala 1529:22]
    .valid_up(n508_valid_up),
    .valid_down(n508_valid_down),
    .I_t0b(n508_I_t0b),
    .I_t1b(n508_I_t1b),
    .O(n508_O)
  );
  InitialDelayCounter_45 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n510 ( // @[Top.scala 1533:22]
    .valid_up(n510_valid_up),
    .valid_down(n510_valid_down),
    .I0(n510_I0),
    .I1(n510_I1),
    .O_t0b(n510_O_t0b),
    .O_t1b(n510_O_t1b)
  );
  Sub n511 ( // @[Top.scala 1537:22]
    .valid_up(n511_valid_up),
    .valid_down(n511_valid_down),
    .I_t0b(n511_I_t0b),
    .I_t1b(n511_I_t1b),
    .O(n511_O)
  );
  AtomTuple n512 ( // @[Top.scala 1540:22]
    .valid_up(n512_valid_up),
    .valid_down(n512_valid_down),
    .I0(n512_I0),
    .I1(n512_I1),
    .O_t0b(n512_O_t0b),
    .O_t1b(n512_O_t1b)
  );
  AtomTuple n513 ( // @[Top.scala 1544:22]
    .valid_up(n513_valid_up),
    .valid_down(n513_valid_down),
    .I0(n513_I0),
    .I1(n513_I1),
    .O_t0b(n513_O_t0b),
    .O_t1b(n513_O_t1b)
  );
  AtomTuple_10 n514 ( // @[Top.scala 1548:22]
    .valid_up(n514_valid_up),
    .valid_down(n514_valid_down),
    .I0_t0b(n514_I0_t0b),
    .I0_t1b(n514_I0_t1b),
    .I1_t0b(n514_I1_t0b),
    .I1_t1b(n514_I1_t1b),
    .O_t0b_t0b(n514_O_t0b_t0b),
    .O_t0b_t1b(n514_O_t0b_t1b),
    .O_t1b_t0b(n514_O_t1b_t0b),
    .O_t1b_t1b(n514_O_t1b_t1b)
  );
  FIFO_4 n515 ( // @[Top.scala 1552:22]
    .clock(n515_clock),
    .reset(n515_reset),
    .valid_up(n515_valid_up),
    .valid_down(n515_valid_down),
    .I_t0b_t0b(n515_I_t0b_t0b),
    .I_t0b_t1b(n515_I_t0b_t1b),
    .I_t1b_t0b(n515_I_t1b_t0b),
    .I_t1b_t1b(n515_I_t1b_t1b),
    .O_t0b_t0b(n515_O_t0b_t0b),
    .O_t0b_t1b(n515_O_t0b_t1b),
    .O_t1b_t0b(n515_O_t1b_t0b),
    .O_t1b_t1b(n515_O_t1b_t1b)
  );
  AtomTuple_11 n516 ( // @[Top.scala 1555:22]
    .valid_up(n516_valid_up),
    .valid_down(n516_valid_down),
    .I0(n516_I0),
    .I1_t0b_t0b(n516_I1_t0b_t0b),
    .I1_t0b_t1b(n516_I1_t0b_t1b),
    .I1_t1b_t0b(n516_I1_t1b_t0b),
    .I1_t1b_t1b(n516_I1_t1b_t1b),
    .O_t0b(n516_O_t0b),
    .O_t1b_t0b_t0b(n516_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n516_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n516_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n516_O_t1b_t1b_t1b)
  );
  If n517 ( // @[Top.scala 1559:22]
    .valid_up(n517_valid_up),
    .valid_down(n517_valid_down),
    .I_t0b(n517_I_t0b),
    .I_t1b_t0b_t0b(n517_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n517_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n517_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n517_I_t1b_t1b_t1b),
    .O_t0b(n517_O_t0b),
    .O_t1b(n517_O_t1b)
  );
  AtomTuple_1 n519 ( // @[Top.scala 1562:22]
    .valid_up(n519_valid_up),
    .valid_down(n519_valid_down),
    .I0(n519_I0),
    .I1_t0b(n519_I1_t0b),
    .I1_t1b(n519_I1_t1b),
    .O_t0b(n519_O_t0b),
    .O_t1b_t0b(n519_O_t1b_t0b),
    .O_t1b_t1b(n519_O_t1b_t1b)
  );
  assign valid_down = n519_valid_down; // @[Top.scala 1567:16]
  assign O_t1b_t0b = n519_O_t1b_t0b; // @[Top.scala 1566:7]
  assign O_t1b_t1b = n519_O_t1b_t1b; // @[Top.scala 1566:7]
  assign n490_valid_up = valid_up; // @[Top.scala 1479:19]
  assign n490_I_t0b = I_t0b; // @[Top.scala 1478:12]
  assign n518_clock = clock;
  assign n518_reset = reset;
  assign n518_valid_up = n490_valid_down; // @[Top.scala 1482:19]
  assign n518_I = n490_O; // @[Top.scala 1481:12]
  assign n506_clock = clock;
  assign n506_reset = reset;
  assign n506_valid_up = n490_valid_down; // @[Top.scala 1485:19]
  assign n506_I = n490_O; // @[Top.scala 1484:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n492_valid_up = valid_up; // @[Top.scala 1489:19]
  assign n492_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1488:12]
  assign n492_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1488:12]
  assign n493_valid_up = n492_valid_down; // @[Top.scala 1492:19]
  assign n493_I_t0b = n492_O_t0b; // @[Top.scala 1491:12]
  assign n494_valid_up = n492_valid_down; // @[Top.scala 1495:19]
  assign n494_I_t1b = n492_O_t1b; // @[Top.scala 1494:12]
  assign n495_valid_up = n493_valid_down & n494_valid_down; // @[Top.scala 1499:19]
  assign n495_I0 = n493_O; // @[Top.scala 1497:13]
  assign n495_I1 = n494_O; // @[Top.scala 1498:13]
  assign n496_valid_up = n495_valid_down; // @[Top.scala 1502:19]
  assign n496_I_t0b = n495_O_t0b; // @[Top.scala 1501:12]
  assign n496_I_t1b = n495_O_t1b; // @[Top.scala 1501:12]
  assign n498_valid_up = InitialDelayCounter_valid_down & n496_valid_down; // @[Top.scala 1506:19]
  assign n498_I0 = 16'h1; // @[Top.scala 1504:13]
  assign n498_I1 = n496_O; // @[Top.scala 1505:13]
  assign n499_valid_up = n498_valid_down; // @[Top.scala 1509:19]
  assign n499_I_t0b = n498_O_t0b; // @[Top.scala 1508:12]
  assign n499_I_t1b = n498_O_t1b; // @[Top.scala 1508:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n502_valid_up = n499_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1514:19]
  assign n502_I0 = n499_O; // @[Top.scala 1512:13]
  assign n503_valid_up = n502_valid_down; // @[Top.scala 1517:19]
  assign n503_I_t0b = n502_O_t0b; // @[Top.scala 1516:12]
  assign n504_valid_up = n503_valid_down; // @[Top.scala 1521:19]
  assign n504_I0 = n503_O; // @[Top.scala 1519:13]
  assign n504_I1 = n503_O; // @[Top.scala 1520:13]
  assign n505_clock = clock;
  assign n505_reset = reset;
  assign n505_valid_up = n504_valid_down; // @[Top.scala 1524:19]
  assign n505_I_t0b = n504_O_t0b; // @[Top.scala 1523:12]
  assign n505_I_t1b = n504_O_t1b; // @[Top.scala 1523:12]
  assign n507_valid_up = n506_valid_down & n505_valid_down; // @[Top.scala 1528:19]
  assign n507_I0 = n506_O; // @[Top.scala 1526:13]
  assign n507_I1 = n505_O; // @[Top.scala 1527:13]
  assign n508_valid_up = n507_valid_down; // @[Top.scala 1531:19]
  assign n508_I_t0b = n507_O_t0b; // @[Top.scala 1530:12]
  assign n508_I_t1b = n507_O_t1b; // @[Top.scala 1530:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n510_valid_up = n503_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1536:19]
  assign n510_I0 = n503_O; // @[Top.scala 1534:13]
  assign n510_I1 = 16'h1; // @[Top.scala 1535:13]
  assign n511_valid_up = n510_valid_down; // @[Top.scala 1539:19]
  assign n511_I_t0b = n510_O_t0b; // @[Top.scala 1538:12]
  assign n511_I_t1b = n510_O_t1b; // @[Top.scala 1538:12]
  assign n512_valid_up = n493_valid_down & n511_valid_down; // @[Top.scala 1543:19]
  assign n512_I0 = n493_O; // @[Top.scala 1541:13]
  assign n512_I1 = n511_O; // @[Top.scala 1542:13]
  assign n513_valid_up = n503_valid_down & n494_valid_down; // @[Top.scala 1547:19]
  assign n513_I0 = n503_O; // @[Top.scala 1545:13]
  assign n513_I1 = n494_O; // @[Top.scala 1546:13]
  assign n514_valid_up = n512_valid_down & n513_valid_down; // @[Top.scala 1551:19]
  assign n514_I0_t0b = n512_O_t0b; // @[Top.scala 1549:13]
  assign n514_I0_t1b = n512_O_t1b; // @[Top.scala 1549:13]
  assign n514_I1_t0b = n513_O_t0b; // @[Top.scala 1550:13]
  assign n514_I1_t1b = n513_O_t1b; // @[Top.scala 1550:13]
  assign n515_clock = clock;
  assign n515_reset = reset;
  assign n515_valid_up = n514_valid_down; // @[Top.scala 1554:19]
  assign n515_I_t0b_t0b = n514_O_t0b_t0b; // @[Top.scala 1553:12]
  assign n515_I_t0b_t1b = n514_O_t0b_t1b; // @[Top.scala 1553:12]
  assign n515_I_t1b_t0b = n514_O_t1b_t0b; // @[Top.scala 1553:12]
  assign n515_I_t1b_t1b = n514_O_t1b_t1b; // @[Top.scala 1553:12]
  assign n516_valid_up = n508_valid_down & n515_valid_down; // @[Top.scala 1558:19]
  assign n516_I0 = n508_O[0]; // @[Top.scala 1556:13]
  assign n516_I1_t0b_t0b = n515_O_t0b_t0b; // @[Top.scala 1557:13]
  assign n516_I1_t0b_t1b = n515_O_t0b_t1b; // @[Top.scala 1557:13]
  assign n516_I1_t1b_t0b = n515_O_t1b_t0b; // @[Top.scala 1557:13]
  assign n516_I1_t1b_t1b = n515_O_t1b_t1b; // @[Top.scala 1557:13]
  assign n517_valid_up = n516_valid_down; // @[Top.scala 1561:19]
  assign n517_I_t0b = n516_O_t0b; // @[Top.scala 1560:12]
  assign n517_I_t1b_t0b_t0b = n516_O_t1b_t0b_t0b; // @[Top.scala 1560:12]
  assign n517_I_t1b_t0b_t1b = n516_O_t1b_t0b_t1b; // @[Top.scala 1560:12]
  assign n517_I_t1b_t1b_t0b = n516_O_t1b_t1b_t0b; // @[Top.scala 1560:12]
  assign n517_I_t1b_t1b_t1b = n516_O_t1b_t1b_t1b; // @[Top.scala 1560:12]
  assign n519_valid_up = n518_valid_down & n517_valid_down; // @[Top.scala 1565:19]
  assign n519_I0 = n518_O; // @[Top.scala 1563:13]
  assign n519_I1_t0b = n517_O_t0b; // @[Top.scala 1564:13]
  assign n519_I1_t1b = n517_O_t1b; // @[Top.scala 1564:13]
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
  wire  n522_valid_up; // @[Top.scala 1573:22]
  wire  n522_valid_down; // @[Top.scala 1573:22]
  wire [15:0] n522_I_t1b_t0b; // @[Top.scala 1573:22]
  wire [15:0] n522_I_t1b_t1b; // @[Top.scala 1573:22]
  wire [15:0] n522_O_t0b; // @[Top.scala 1573:22]
  wire [15:0] n522_O_t1b; // @[Top.scala 1573:22]
  wire  n523_valid_up; // @[Top.scala 1576:22]
  wire  n523_valid_down; // @[Top.scala 1576:22]
  wire [15:0] n523_I_t0b; // @[Top.scala 1576:22]
  wire [15:0] n523_O; // @[Top.scala 1576:22]
  Snd n522 ( // @[Top.scala 1573:22]
    .valid_up(n522_valid_up),
    .valid_down(n522_valid_down),
    .I_t1b_t0b(n522_I_t1b_t0b),
    .I_t1b_t1b(n522_I_t1b_t1b),
    .O_t0b(n522_O_t0b),
    .O_t1b(n522_O_t1b)
  );
  Fst_1 n523 ( // @[Top.scala 1576:22]
    .valid_up(n523_valid_up),
    .valid_down(n523_valid_down),
    .I_t0b(n523_I_t0b),
    .O(n523_O)
  );
  assign valid_down = n523_valid_down; // @[Top.scala 1580:16]
  assign O = n523_O; // @[Top.scala 1579:7]
  assign n522_valid_up = valid_up; // @[Top.scala 1575:19]
  assign n522_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1574:12]
  assign n522_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1574:12]
  assign n523_valid_up = n522_valid_down; // @[Top.scala 1578:19]
  assign n523_I_t0b = n522_O_t0b; // @[Top.scala 1577:12]
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
  wire  n1_clock; // @[Top.scala 1586:20]
  wire  n1_reset; // @[Top.scala 1586:20]
  wire  n1_valid_up; // @[Top.scala 1586:20]
  wire  n1_valid_down; // @[Top.scala 1586:20]
  wire [15:0] n1_I; // @[Top.scala 1586:20]
  wire [15:0] n1_O; // @[Top.scala 1586:20]
  wire  n8_clock; // @[Top.scala 1589:20]
  wire  n8_reset; // @[Top.scala 1589:20]
  wire  n8_valid_up; // @[Top.scala 1589:20]
  wire  n8_valid_down; // @[Top.scala 1589:20]
  wire [15:0] n8_I; // @[Top.scala 1589:20]
  wire [15:0] n8_O_t0b; // @[Top.scala 1589:20]
  wire [15:0] n8_O_t1b_t0b; // @[Top.scala 1589:20]
  wire [15:0] n8_O_t1b_t1b; // @[Top.scala 1589:20]
  wire  n40_clock; // @[Top.scala 1592:21]
  wire  n40_reset; // @[Top.scala 1592:21]
  wire  n40_valid_up; // @[Top.scala 1592:21]
  wire  n40_valid_down; // @[Top.scala 1592:21]
  wire [15:0] n40_I_t0b; // @[Top.scala 1592:21]
  wire [15:0] n40_I_t1b_t0b; // @[Top.scala 1592:21]
  wire [15:0] n40_I_t1b_t1b; // @[Top.scala 1592:21]
  wire [15:0] n40_O_t0b; // @[Top.scala 1592:21]
  wire [15:0] n40_O_t1b_t0b; // @[Top.scala 1592:21]
  wire [15:0] n40_O_t1b_t1b; // @[Top.scala 1592:21]
  wire  n72_clock; // @[Top.scala 1595:21]
  wire  n72_reset; // @[Top.scala 1595:21]
  wire  n72_valid_up; // @[Top.scala 1595:21]
  wire  n72_valid_down; // @[Top.scala 1595:21]
  wire [15:0] n72_I_t0b; // @[Top.scala 1595:21]
  wire [15:0] n72_I_t1b_t0b; // @[Top.scala 1595:21]
  wire [15:0] n72_I_t1b_t1b; // @[Top.scala 1595:21]
  wire [15:0] n72_O_t0b; // @[Top.scala 1595:21]
  wire [15:0] n72_O_t1b_t0b; // @[Top.scala 1595:21]
  wire [15:0] n72_O_t1b_t1b; // @[Top.scala 1595:21]
  wire  n104_clock; // @[Top.scala 1598:22]
  wire  n104_reset; // @[Top.scala 1598:22]
  wire  n104_valid_up; // @[Top.scala 1598:22]
  wire  n104_valid_down; // @[Top.scala 1598:22]
  wire [15:0] n104_I_t0b; // @[Top.scala 1598:22]
  wire [15:0] n104_I_t1b_t0b; // @[Top.scala 1598:22]
  wire [15:0] n104_I_t1b_t1b; // @[Top.scala 1598:22]
  wire [15:0] n104_O_t0b; // @[Top.scala 1598:22]
  wire [15:0] n104_O_t1b_t0b; // @[Top.scala 1598:22]
  wire [15:0] n104_O_t1b_t1b; // @[Top.scala 1598:22]
  wire  n136_clock; // @[Top.scala 1601:22]
  wire  n136_reset; // @[Top.scala 1601:22]
  wire  n136_valid_up; // @[Top.scala 1601:22]
  wire  n136_valid_down; // @[Top.scala 1601:22]
  wire [15:0] n136_I_t0b; // @[Top.scala 1601:22]
  wire [15:0] n136_I_t1b_t0b; // @[Top.scala 1601:22]
  wire [15:0] n136_I_t1b_t1b; // @[Top.scala 1601:22]
  wire [15:0] n136_O_t0b; // @[Top.scala 1601:22]
  wire [15:0] n136_O_t1b_t0b; // @[Top.scala 1601:22]
  wire [15:0] n136_O_t1b_t1b; // @[Top.scala 1601:22]
  wire  n168_clock; // @[Top.scala 1604:22]
  wire  n168_reset; // @[Top.scala 1604:22]
  wire  n168_valid_up; // @[Top.scala 1604:22]
  wire  n168_valid_down; // @[Top.scala 1604:22]
  wire [15:0] n168_I_t0b; // @[Top.scala 1604:22]
  wire [15:0] n168_I_t1b_t0b; // @[Top.scala 1604:22]
  wire [15:0] n168_I_t1b_t1b; // @[Top.scala 1604:22]
  wire [15:0] n168_O_t0b; // @[Top.scala 1604:22]
  wire [15:0] n168_O_t1b_t0b; // @[Top.scala 1604:22]
  wire [15:0] n168_O_t1b_t1b; // @[Top.scala 1604:22]
  wire  n200_clock; // @[Top.scala 1607:22]
  wire  n200_reset; // @[Top.scala 1607:22]
  wire  n200_valid_up; // @[Top.scala 1607:22]
  wire  n200_valid_down; // @[Top.scala 1607:22]
  wire [15:0] n200_I_t0b; // @[Top.scala 1607:22]
  wire [15:0] n200_I_t1b_t0b; // @[Top.scala 1607:22]
  wire [15:0] n200_I_t1b_t1b; // @[Top.scala 1607:22]
  wire [15:0] n200_O_t0b; // @[Top.scala 1607:22]
  wire [15:0] n200_O_t1b_t0b; // @[Top.scala 1607:22]
  wire [15:0] n200_O_t1b_t1b; // @[Top.scala 1607:22]
  wire  n232_clock; // @[Top.scala 1610:22]
  wire  n232_reset; // @[Top.scala 1610:22]
  wire  n232_valid_up; // @[Top.scala 1610:22]
  wire  n232_valid_down; // @[Top.scala 1610:22]
  wire [15:0] n232_I_t0b; // @[Top.scala 1610:22]
  wire [15:0] n232_I_t1b_t0b; // @[Top.scala 1610:22]
  wire [15:0] n232_I_t1b_t1b; // @[Top.scala 1610:22]
  wire [15:0] n232_O_t0b; // @[Top.scala 1610:22]
  wire [15:0] n232_O_t1b_t0b; // @[Top.scala 1610:22]
  wire [15:0] n232_O_t1b_t1b; // @[Top.scala 1610:22]
  wire  n264_clock; // @[Top.scala 1613:22]
  wire  n264_reset; // @[Top.scala 1613:22]
  wire  n264_valid_up; // @[Top.scala 1613:22]
  wire  n264_valid_down; // @[Top.scala 1613:22]
  wire [15:0] n264_I_t0b; // @[Top.scala 1613:22]
  wire [15:0] n264_I_t1b_t0b; // @[Top.scala 1613:22]
  wire [15:0] n264_I_t1b_t1b; // @[Top.scala 1613:22]
  wire [15:0] n264_O_t0b; // @[Top.scala 1613:22]
  wire [15:0] n264_O_t1b_t0b; // @[Top.scala 1613:22]
  wire [15:0] n264_O_t1b_t1b; // @[Top.scala 1613:22]
  wire  n296_clock; // @[Top.scala 1616:22]
  wire  n296_reset; // @[Top.scala 1616:22]
  wire  n296_valid_up; // @[Top.scala 1616:22]
  wire  n296_valid_down; // @[Top.scala 1616:22]
  wire [15:0] n296_I_t0b; // @[Top.scala 1616:22]
  wire [15:0] n296_I_t1b_t0b; // @[Top.scala 1616:22]
  wire [15:0] n296_I_t1b_t1b; // @[Top.scala 1616:22]
  wire [15:0] n296_O_t0b; // @[Top.scala 1616:22]
  wire [15:0] n296_O_t1b_t0b; // @[Top.scala 1616:22]
  wire [15:0] n296_O_t1b_t1b; // @[Top.scala 1616:22]
  wire  n328_clock; // @[Top.scala 1619:22]
  wire  n328_reset; // @[Top.scala 1619:22]
  wire  n328_valid_up; // @[Top.scala 1619:22]
  wire  n328_valid_down; // @[Top.scala 1619:22]
  wire [15:0] n328_I_t0b; // @[Top.scala 1619:22]
  wire [15:0] n328_I_t1b_t0b; // @[Top.scala 1619:22]
  wire [15:0] n328_I_t1b_t1b; // @[Top.scala 1619:22]
  wire [15:0] n328_O_t0b; // @[Top.scala 1619:22]
  wire [15:0] n328_O_t1b_t0b; // @[Top.scala 1619:22]
  wire [15:0] n328_O_t1b_t1b; // @[Top.scala 1619:22]
  wire  n360_clock; // @[Top.scala 1622:22]
  wire  n360_reset; // @[Top.scala 1622:22]
  wire  n360_valid_up; // @[Top.scala 1622:22]
  wire  n360_valid_down; // @[Top.scala 1622:22]
  wire [15:0] n360_I_t0b; // @[Top.scala 1622:22]
  wire [15:0] n360_I_t1b_t0b; // @[Top.scala 1622:22]
  wire [15:0] n360_I_t1b_t1b; // @[Top.scala 1622:22]
  wire [15:0] n360_O_t0b; // @[Top.scala 1622:22]
  wire [15:0] n360_O_t1b_t0b; // @[Top.scala 1622:22]
  wire [15:0] n360_O_t1b_t1b; // @[Top.scala 1622:22]
  wire  n392_clock; // @[Top.scala 1625:22]
  wire  n392_reset; // @[Top.scala 1625:22]
  wire  n392_valid_up; // @[Top.scala 1625:22]
  wire  n392_valid_down; // @[Top.scala 1625:22]
  wire [15:0] n392_I_t0b; // @[Top.scala 1625:22]
  wire [15:0] n392_I_t1b_t0b; // @[Top.scala 1625:22]
  wire [15:0] n392_I_t1b_t1b; // @[Top.scala 1625:22]
  wire [15:0] n392_O_t0b; // @[Top.scala 1625:22]
  wire [15:0] n392_O_t1b_t0b; // @[Top.scala 1625:22]
  wire [15:0] n392_O_t1b_t1b; // @[Top.scala 1625:22]
  wire  n424_clock; // @[Top.scala 1628:22]
  wire  n424_reset; // @[Top.scala 1628:22]
  wire  n424_valid_up; // @[Top.scala 1628:22]
  wire  n424_valid_down; // @[Top.scala 1628:22]
  wire [15:0] n424_I_t0b; // @[Top.scala 1628:22]
  wire [15:0] n424_I_t1b_t0b; // @[Top.scala 1628:22]
  wire [15:0] n424_I_t1b_t1b; // @[Top.scala 1628:22]
  wire [15:0] n424_O_t0b; // @[Top.scala 1628:22]
  wire [15:0] n424_O_t1b_t0b; // @[Top.scala 1628:22]
  wire [15:0] n424_O_t1b_t1b; // @[Top.scala 1628:22]
  wire  n456_clock; // @[Top.scala 1631:22]
  wire  n456_reset; // @[Top.scala 1631:22]
  wire  n456_valid_up; // @[Top.scala 1631:22]
  wire  n456_valid_down; // @[Top.scala 1631:22]
  wire [15:0] n456_I_t0b; // @[Top.scala 1631:22]
  wire [15:0] n456_I_t1b_t0b; // @[Top.scala 1631:22]
  wire [15:0] n456_I_t1b_t1b; // @[Top.scala 1631:22]
  wire [15:0] n456_O_t0b; // @[Top.scala 1631:22]
  wire [15:0] n456_O_t1b_t0b; // @[Top.scala 1631:22]
  wire [15:0] n456_O_t1b_t1b; // @[Top.scala 1631:22]
  wire  n488_clock; // @[Top.scala 1634:22]
  wire  n488_reset; // @[Top.scala 1634:22]
  wire  n488_valid_up; // @[Top.scala 1634:22]
  wire  n488_valid_down; // @[Top.scala 1634:22]
  wire [15:0] n488_I_t0b; // @[Top.scala 1634:22]
  wire [15:0] n488_I_t1b_t0b; // @[Top.scala 1634:22]
  wire [15:0] n488_I_t1b_t1b; // @[Top.scala 1634:22]
  wire [15:0] n488_O_t0b; // @[Top.scala 1634:22]
  wire [15:0] n488_O_t1b_t0b; // @[Top.scala 1634:22]
  wire [15:0] n488_O_t1b_t1b; // @[Top.scala 1634:22]
  wire  n520_clock; // @[Top.scala 1637:22]
  wire  n520_reset; // @[Top.scala 1637:22]
  wire  n520_valid_up; // @[Top.scala 1637:22]
  wire  n520_valid_down; // @[Top.scala 1637:22]
  wire [15:0] n520_I_t0b; // @[Top.scala 1637:22]
  wire [15:0] n520_I_t1b_t0b; // @[Top.scala 1637:22]
  wire [15:0] n520_I_t1b_t1b; // @[Top.scala 1637:22]
  wire [15:0] n520_O_t1b_t0b; // @[Top.scala 1637:22]
  wire [15:0] n520_O_t1b_t1b; // @[Top.scala 1637:22]
  wire  n524_valid_up; // @[Top.scala 1640:22]
  wire  n524_valid_down; // @[Top.scala 1640:22]
  wire [15:0] n524_I_t1b_t0b; // @[Top.scala 1640:22]
  wire [15:0] n524_I_t1b_t1b; // @[Top.scala 1640:22]
  wire [15:0] n524_O; // @[Top.scala 1640:22]
  wire  n525_clock; // @[Top.scala 1643:22]
  wire  n525_reset; // @[Top.scala 1643:22]
  wire  n525_valid_up; // @[Top.scala 1643:22]
  wire  n525_valid_down; // @[Top.scala 1643:22]
  wire [15:0] n525_I; // @[Top.scala 1643:22]
  wire [15:0] n525_O; // @[Top.scala 1643:22]
  wire  n526_clock; // @[Top.scala 1646:22]
  wire  n526_reset; // @[Top.scala 1646:22]
  wire  n526_valid_up; // @[Top.scala 1646:22]
  wire  n526_valid_down; // @[Top.scala 1646:22]
  wire [15:0] n526_I; // @[Top.scala 1646:22]
  wire [15:0] n526_O; // @[Top.scala 1646:22]
  wire  n527_clock; // @[Top.scala 1649:22]
  wire  n527_reset; // @[Top.scala 1649:22]
  wire  n527_valid_up; // @[Top.scala 1649:22]
  wire  n527_valid_down; // @[Top.scala 1649:22]
  wire [15:0] n527_I; // @[Top.scala 1649:22]
  wire [15:0] n527_O; // @[Top.scala 1649:22]
  FIFO n1 ( // @[Top.scala 1586:20]
    .clock(n1_clock),
    .reset(n1_reset),
    .valid_up(n1_valid_up),
    .valid_down(n1_valid_down),
    .I(n1_I),
    .O(n1_O)
  );
  MapT n8 ( // @[Top.scala 1589:20]
    .clock(n8_clock),
    .reset(n8_reset),
    .valid_up(n8_valid_up),
    .valid_down(n8_valid_down),
    .I(n8_I),
    .O_t0b(n8_O_t0b),
    .O_t1b_t0b(n8_O_t1b_t0b),
    .O_t1b_t1b(n8_O_t1b_t1b)
  );
  MapT_1 n40 ( // @[Top.scala 1592:21]
    .clock(n40_clock),
    .reset(n40_reset),
    .valid_up(n40_valid_up),
    .valid_down(n40_valid_down),
    .I_t0b(n40_I_t0b),
    .I_t1b_t0b(n40_I_t1b_t0b),
    .I_t1b_t1b(n40_I_t1b_t1b),
    .O_t0b(n40_O_t0b),
    .O_t1b_t0b(n40_O_t1b_t0b),
    .O_t1b_t1b(n40_O_t1b_t1b)
  );
  MapT_2 n72 ( // @[Top.scala 1595:21]
    .clock(n72_clock),
    .reset(n72_reset),
    .valid_up(n72_valid_up),
    .valid_down(n72_valid_down),
    .I_t0b(n72_I_t0b),
    .I_t1b_t0b(n72_I_t1b_t0b),
    .I_t1b_t1b(n72_I_t1b_t1b),
    .O_t0b(n72_O_t0b),
    .O_t1b_t0b(n72_O_t1b_t0b),
    .O_t1b_t1b(n72_O_t1b_t1b)
  );
  MapT_3 n104 ( // @[Top.scala 1598:22]
    .clock(n104_clock),
    .reset(n104_reset),
    .valid_up(n104_valid_up),
    .valid_down(n104_valid_down),
    .I_t0b(n104_I_t0b),
    .I_t1b_t0b(n104_I_t1b_t0b),
    .I_t1b_t1b(n104_I_t1b_t1b),
    .O_t0b(n104_O_t0b),
    .O_t1b_t0b(n104_O_t1b_t0b),
    .O_t1b_t1b(n104_O_t1b_t1b)
  );
  MapT_4 n136 ( // @[Top.scala 1601:22]
    .clock(n136_clock),
    .reset(n136_reset),
    .valid_up(n136_valid_up),
    .valid_down(n136_valid_down),
    .I_t0b(n136_I_t0b),
    .I_t1b_t0b(n136_I_t1b_t0b),
    .I_t1b_t1b(n136_I_t1b_t1b),
    .O_t0b(n136_O_t0b),
    .O_t1b_t0b(n136_O_t1b_t0b),
    .O_t1b_t1b(n136_O_t1b_t1b)
  );
  MapT_5 n168 ( // @[Top.scala 1604:22]
    .clock(n168_clock),
    .reset(n168_reset),
    .valid_up(n168_valid_up),
    .valid_down(n168_valid_down),
    .I_t0b(n168_I_t0b),
    .I_t1b_t0b(n168_I_t1b_t0b),
    .I_t1b_t1b(n168_I_t1b_t1b),
    .O_t0b(n168_O_t0b),
    .O_t1b_t0b(n168_O_t1b_t0b),
    .O_t1b_t1b(n168_O_t1b_t1b)
  );
  MapT_6 n200 ( // @[Top.scala 1607:22]
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
  MapT_7 n232 ( // @[Top.scala 1610:22]
    .clock(n232_clock),
    .reset(n232_reset),
    .valid_up(n232_valid_up),
    .valid_down(n232_valid_down),
    .I_t0b(n232_I_t0b),
    .I_t1b_t0b(n232_I_t1b_t0b),
    .I_t1b_t1b(n232_I_t1b_t1b),
    .O_t0b(n232_O_t0b),
    .O_t1b_t0b(n232_O_t1b_t0b),
    .O_t1b_t1b(n232_O_t1b_t1b)
  );
  MapT_8 n264 ( // @[Top.scala 1613:22]
    .clock(n264_clock),
    .reset(n264_reset),
    .valid_up(n264_valid_up),
    .valid_down(n264_valid_down),
    .I_t0b(n264_I_t0b),
    .I_t1b_t0b(n264_I_t1b_t0b),
    .I_t1b_t1b(n264_I_t1b_t1b),
    .O_t0b(n264_O_t0b),
    .O_t1b_t0b(n264_O_t1b_t0b),
    .O_t1b_t1b(n264_O_t1b_t1b)
  );
  MapT_9 n296 ( // @[Top.scala 1616:22]
    .clock(n296_clock),
    .reset(n296_reset),
    .valid_up(n296_valid_up),
    .valid_down(n296_valid_down),
    .I_t0b(n296_I_t0b),
    .I_t1b_t0b(n296_I_t1b_t0b),
    .I_t1b_t1b(n296_I_t1b_t1b),
    .O_t0b(n296_O_t0b),
    .O_t1b_t0b(n296_O_t1b_t0b),
    .O_t1b_t1b(n296_O_t1b_t1b)
  );
  MapT_10 n328 ( // @[Top.scala 1619:22]
    .clock(n328_clock),
    .reset(n328_reset),
    .valid_up(n328_valid_up),
    .valid_down(n328_valid_down),
    .I_t0b(n328_I_t0b),
    .I_t1b_t0b(n328_I_t1b_t0b),
    .I_t1b_t1b(n328_I_t1b_t1b),
    .O_t0b(n328_O_t0b),
    .O_t1b_t0b(n328_O_t1b_t0b),
    .O_t1b_t1b(n328_O_t1b_t1b)
  );
  MapT_11 n360 ( // @[Top.scala 1622:22]
    .clock(n360_clock),
    .reset(n360_reset),
    .valid_up(n360_valid_up),
    .valid_down(n360_valid_down),
    .I_t0b(n360_I_t0b),
    .I_t1b_t0b(n360_I_t1b_t0b),
    .I_t1b_t1b(n360_I_t1b_t1b),
    .O_t0b(n360_O_t0b),
    .O_t1b_t0b(n360_O_t1b_t0b),
    .O_t1b_t1b(n360_O_t1b_t1b)
  );
  MapT_12 n392 ( // @[Top.scala 1625:22]
    .clock(n392_clock),
    .reset(n392_reset),
    .valid_up(n392_valid_up),
    .valid_down(n392_valid_down),
    .I_t0b(n392_I_t0b),
    .I_t1b_t0b(n392_I_t1b_t0b),
    .I_t1b_t1b(n392_I_t1b_t1b),
    .O_t0b(n392_O_t0b),
    .O_t1b_t0b(n392_O_t1b_t0b),
    .O_t1b_t1b(n392_O_t1b_t1b)
  );
  MapT_13 n424 ( // @[Top.scala 1628:22]
    .clock(n424_clock),
    .reset(n424_reset),
    .valid_up(n424_valid_up),
    .valid_down(n424_valid_down),
    .I_t0b(n424_I_t0b),
    .I_t1b_t0b(n424_I_t1b_t0b),
    .I_t1b_t1b(n424_I_t1b_t1b),
    .O_t0b(n424_O_t0b),
    .O_t1b_t0b(n424_O_t1b_t0b),
    .O_t1b_t1b(n424_O_t1b_t1b)
  );
  MapT_14 n456 ( // @[Top.scala 1631:22]
    .clock(n456_clock),
    .reset(n456_reset),
    .valid_up(n456_valid_up),
    .valid_down(n456_valid_down),
    .I_t0b(n456_I_t0b),
    .I_t1b_t0b(n456_I_t1b_t0b),
    .I_t1b_t1b(n456_I_t1b_t1b),
    .O_t0b(n456_O_t0b),
    .O_t1b_t0b(n456_O_t1b_t0b),
    .O_t1b_t1b(n456_O_t1b_t1b)
  );
  MapT_15 n488 ( // @[Top.scala 1634:22]
    .clock(n488_clock),
    .reset(n488_reset),
    .valid_up(n488_valid_up),
    .valid_down(n488_valid_down),
    .I_t0b(n488_I_t0b),
    .I_t1b_t0b(n488_I_t1b_t0b),
    .I_t1b_t1b(n488_I_t1b_t1b),
    .O_t0b(n488_O_t0b),
    .O_t1b_t0b(n488_O_t1b_t0b),
    .O_t1b_t1b(n488_O_t1b_t1b)
  );
  MapT_16 n520 ( // @[Top.scala 1637:22]
    .clock(n520_clock),
    .reset(n520_reset),
    .valid_up(n520_valid_up),
    .valid_down(n520_valid_down),
    .I_t0b(n520_I_t0b),
    .I_t1b_t0b(n520_I_t1b_t0b),
    .I_t1b_t1b(n520_I_t1b_t1b),
    .O_t1b_t0b(n520_O_t1b_t0b),
    .O_t1b_t1b(n520_O_t1b_t1b)
  );
  MapT_17 n524 ( // @[Top.scala 1640:22]
    .valid_up(n524_valid_up),
    .valid_down(n524_valid_down),
    .I_t1b_t0b(n524_I_t1b_t0b),
    .I_t1b_t1b(n524_I_t1b_t1b),
    .O(n524_O)
  );
  FIFO n525 ( // @[Top.scala 1643:22]
    .clock(n525_clock),
    .reset(n525_reset),
    .valid_up(n525_valid_up),
    .valid_down(n525_valid_down),
    .I(n525_I),
    .O(n525_O)
  );
  FIFO n526 ( // @[Top.scala 1646:22]
    .clock(n526_clock),
    .reset(n526_reset),
    .valid_up(n526_valid_up),
    .valid_down(n526_valid_down),
    .I(n526_I),
    .O(n526_O)
  );
  FIFO n527 ( // @[Top.scala 1649:22]
    .clock(n527_clock),
    .reset(n527_reset),
    .valid_up(n527_valid_up),
    .valid_down(n527_valid_down),
    .I(n527_I),
    .O(n527_O)
  );
  assign valid_down = n527_valid_down; // @[Top.scala 1653:16]
  assign O = n527_O; // @[Top.scala 1652:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 1588:17]
  assign n1_I = I; // @[Top.scala 1587:10]
  assign n8_clock = clock;
  assign n8_reset = reset;
  assign n8_valid_up = n1_valid_down; // @[Top.scala 1591:17]
  assign n8_I = n1_O; // @[Top.scala 1590:10]
  assign n40_clock = clock;
  assign n40_reset = reset;
  assign n40_valid_up = n8_valid_down; // @[Top.scala 1594:18]
  assign n40_I_t0b = n8_O_t0b; // @[Top.scala 1593:11]
  assign n40_I_t1b_t0b = n8_O_t1b_t0b; // @[Top.scala 1593:11]
  assign n40_I_t1b_t1b = n8_O_t1b_t1b; // @[Top.scala 1593:11]
  assign n72_clock = clock;
  assign n72_reset = reset;
  assign n72_valid_up = n40_valid_down; // @[Top.scala 1597:18]
  assign n72_I_t0b = n40_O_t0b; // @[Top.scala 1596:11]
  assign n72_I_t1b_t0b = n40_O_t1b_t0b; // @[Top.scala 1596:11]
  assign n72_I_t1b_t1b = n40_O_t1b_t1b; // @[Top.scala 1596:11]
  assign n104_clock = clock;
  assign n104_reset = reset;
  assign n104_valid_up = n72_valid_down; // @[Top.scala 1600:19]
  assign n104_I_t0b = n72_O_t0b; // @[Top.scala 1599:12]
  assign n104_I_t1b_t0b = n72_O_t1b_t0b; // @[Top.scala 1599:12]
  assign n104_I_t1b_t1b = n72_O_t1b_t1b; // @[Top.scala 1599:12]
  assign n136_clock = clock;
  assign n136_reset = reset;
  assign n136_valid_up = n104_valid_down; // @[Top.scala 1603:19]
  assign n136_I_t0b = n104_O_t0b; // @[Top.scala 1602:12]
  assign n136_I_t1b_t0b = n104_O_t1b_t0b; // @[Top.scala 1602:12]
  assign n136_I_t1b_t1b = n104_O_t1b_t1b; // @[Top.scala 1602:12]
  assign n168_clock = clock;
  assign n168_reset = reset;
  assign n168_valid_up = n136_valid_down; // @[Top.scala 1606:19]
  assign n168_I_t0b = n136_O_t0b; // @[Top.scala 1605:12]
  assign n168_I_t1b_t0b = n136_O_t1b_t0b; // @[Top.scala 1605:12]
  assign n168_I_t1b_t1b = n136_O_t1b_t1b; // @[Top.scala 1605:12]
  assign n200_clock = clock;
  assign n200_reset = reset;
  assign n200_valid_up = n168_valid_down; // @[Top.scala 1609:19]
  assign n200_I_t0b = n168_O_t0b; // @[Top.scala 1608:12]
  assign n200_I_t1b_t0b = n168_O_t1b_t0b; // @[Top.scala 1608:12]
  assign n200_I_t1b_t1b = n168_O_t1b_t1b; // @[Top.scala 1608:12]
  assign n232_clock = clock;
  assign n232_reset = reset;
  assign n232_valid_up = n200_valid_down; // @[Top.scala 1612:19]
  assign n232_I_t0b = n200_O_t0b; // @[Top.scala 1611:12]
  assign n232_I_t1b_t0b = n200_O_t1b_t0b; // @[Top.scala 1611:12]
  assign n232_I_t1b_t1b = n200_O_t1b_t1b; // @[Top.scala 1611:12]
  assign n264_clock = clock;
  assign n264_reset = reset;
  assign n264_valid_up = n232_valid_down; // @[Top.scala 1615:19]
  assign n264_I_t0b = n232_O_t0b; // @[Top.scala 1614:12]
  assign n264_I_t1b_t0b = n232_O_t1b_t0b; // @[Top.scala 1614:12]
  assign n264_I_t1b_t1b = n232_O_t1b_t1b; // @[Top.scala 1614:12]
  assign n296_clock = clock;
  assign n296_reset = reset;
  assign n296_valid_up = n264_valid_down; // @[Top.scala 1618:19]
  assign n296_I_t0b = n264_O_t0b; // @[Top.scala 1617:12]
  assign n296_I_t1b_t0b = n264_O_t1b_t0b; // @[Top.scala 1617:12]
  assign n296_I_t1b_t1b = n264_O_t1b_t1b; // @[Top.scala 1617:12]
  assign n328_clock = clock;
  assign n328_reset = reset;
  assign n328_valid_up = n296_valid_down; // @[Top.scala 1621:19]
  assign n328_I_t0b = n296_O_t0b; // @[Top.scala 1620:12]
  assign n328_I_t1b_t0b = n296_O_t1b_t0b; // @[Top.scala 1620:12]
  assign n328_I_t1b_t1b = n296_O_t1b_t1b; // @[Top.scala 1620:12]
  assign n360_clock = clock;
  assign n360_reset = reset;
  assign n360_valid_up = n328_valid_down; // @[Top.scala 1624:19]
  assign n360_I_t0b = n328_O_t0b; // @[Top.scala 1623:12]
  assign n360_I_t1b_t0b = n328_O_t1b_t0b; // @[Top.scala 1623:12]
  assign n360_I_t1b_t1b = n328_O_t1b_t1b; // @[Top.scala 1623:12]
  assign n392_clock = clock;
  assign n392_reset = reset;
  assign n392_valid_up = n360_valid_down; // @[Top.scala 1627:19]
  assign n392_I_t0b = n360_O_t0b; // @[Top.scala 1626:12]
  assign n392_I_t1b_t0b = n360_O_t1b_t0b; // @[Top.scala 1626:12]
  assign n392_I_t1b_t1b = n360_O_t1b_t1b; // @[Top.scala 1626:12]
  assign n424_clock = clock;
  assign n424_reset = reset;
  assign n424_valid_up = n392_valid_down; // @[Top.scala 1630:19]
  assign n424_I_t0b = n392_O_t0b; // @[Top.scala 1629:12]
  assign n424_I_t1b_t0b = n392_O_t1b_t0b; // @[Top.scala 1629:12]
  assign n424_I_t1b_t1b = n392_O_t1b_t1b; // @[Top.scala 1629:12]
  assign n456_clock = clock;
  assign n456_reset = reset;
  assign n456_valid_up = n424_valid_down; // @[Top.scala 1633:19]
  assign n456_I_t0b = n424_O_t0b; // @[Top.scala 1632:12]
  assign n456_I_t1b_t0b = n424_O_t1b_t0b; // @[Top.scala 1632:12]
  assign n456_I_t1b_t1b = n424_O_t1b_t1b; // @[Top.scala 1632:12]
  assign n488_clock = clock;
  assign n488_reset = reset;
  assign n488_valid_up = n456_valid_down; // @[Top.scala 1636:19]
  assign n488_I_t0b = n456_O_t0b; // @[Top.scala 1635:12]
  assign n488_I_t1b_t0b = n456_O_t1b_t0b; // @[Top.scala 1635:12]
  assign n488_I_t1b_t1b = n456_O_t1b_t1b; // @[Top.scala 1635:12]
  assign n520_clock = clock;
  assign n520_reset = reset;
  assign n520_valid_up = n488_valid_down; // @[Top.scala 1639:19]
  assign n520_I_t0b = n488_O_t0b; // @[Top.scala 1638:12]
  assign n520_I_t1b_t0b = n488_O_t1b_t0b; // @[Top.scala 1638:12]
  assign n520_I_t1b_t1b = n488_O_t1b_t1b; // @[Top.scala 1638:12]
  assign n524_valid_up = n520_valid_down; // @[Top.scala 1642:19]
  assign n524_I_t1b_t0b = n520_O_t1b_t0b; // @[Top.scala 1641:12]
  assign n524_I_t1b_t1b = n520_O_t1b_t1b; // @[Top.scala 1641:12]
  assign n525_clock = clock;
  assign n525_reset = reset;
  assign n525_valid_up = n524_valid_down; // @[Top.scala 1645:19]
  assign n525_I = n524_O; // @[Top.scala 1644:12]
  assign n526_clock = clock;
  assign n526_reset = reset;
  assign n526_valid_up = n525_valid_down; // @[Top.scala 1648:19]
  assign n526_I = n525_O; // @[Top.scala 1647:12]
  assign n527_clock = clock;
  assign n527_reset = reset;
  assign n527_valid_up = n526_valid_down; // @[Top.scala 1651:19]
  assign n527_I = n526_O; // @[Top.scala 1650:12]
endmodule
