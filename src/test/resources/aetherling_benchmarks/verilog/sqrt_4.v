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
  wire  n6_valid_up; // @[Top.scala 19:20]
  wire  n6_valid_down; // @[Top.scala 19:20]
  wire [15:0] n6_I0; // @[Top.scala 19:20]
  wire [15:0] n6_I1; // @[Top.scala 19:20]
  wire [15:0] n6_O_t0b; // @[Top.scala 19:20]
  wire [15:0] n6_O_t1b; // @[Top.scala 19:20]
  wire  n7_clock; // @[Top.scala 23:20]
  wire  n7_reset; // @[Top.scala 23:20]
  wire  n7_valid_up; // @[Top.scala 23:20]
  wire  n7_valid_down; // @[Top.scala 23:20]
  wire [15:0] n7_I_t0b; // @[Top.scala 23:20]
  wire [15:0] n7_I_t1b; // @[Top.scala 23:20]
  wire [15:0] n7_O_t0b; // @[Top.scala 23:20]
  wire [15:0] n7_O_t1b; // @[Top.scala 23:20]
  wire  n8_valid_up; // @[Top.scala 26:20]
  wire  n8_valid_down; // @[Top.scala 26:20]
  wire [15:0] n8_I0; // @[Top.scala 26:20]
  wire [15:0] n8_I1_t0b; // @[Top.scala 26:20]
  wire [15:0] n8_I1_t1b; // @[Top.scala 26:20]
  wire [15:0] n8_O_t0b; // @[Top.scala 26:20]
  wire [15:0] n8_O_t1b_t0b; // @[Top.scala 26:20]
  wire [15:0] n8_O_t1b_t1b; // @[Top.scala 26:20]
  AtomTuple n6 ( // @[Top.scala 19:20]
    .valid_up(n6_valid_up),
    .valid_down(n6_valid_down),
    .I0(n6_I0),
    .I1(n6_I1),
    .O_t0b(n6_O_t0b),
    .O_t1b(n6_O_t1b)
  );
  FIFO_1 n7 ( // @[Top.scala 23:20]
    .clock(n7_clock),
    .reset(n7_reset),
    .valid_up(n7_valid_up),
    .valid_down(n7_valid_down),
    .I_t0b(n7_I_t0b),
    .I_t1b(n7_I_t1b),
    .O_t0b(n7_O_t0b),
    .O_t1b(n7_O_t1b)
  );
  AtomTuple_1 n8 ( // @[Top.scala 26:20]
    .valid_up(n8_valid_up),
    .valid_down(n8_valid_down),
    .I0(n8_I0),
    .I1_t0b(n8_I1_t0b),
    .I1_t1b(n8_I1_t1b),
    .O_t0b(n8_O_t0b),
    .O_t1b_t0b(n8_O_t1b_t0b),
    .O_t1b_t1b(n8_O_t1b_t1b)
  );
  assign valid_down = n8_valid_down; // @[Top.scala 31:16]
  assign O_t0b = n8_O_t0b; // @[Top.scala 30:7]
  assign O_t1b_t0b = n8_O_t1b_t0b; // @[Top.scala 30:7]
  assign O_t1b_t1b = n8_O_t1b_t1b; // @[Top.scala 30:7]
  assign n6_valid_up = 1'h1; // @[Top.scala 22:17]
  assign n6_I0 = 16'h0; // @[Top.scala 20:11]
  assign n6_I1 = 16'hff; // @[Top.scala 21:11]
  assign n7_clock = clock;
  assign n7_reset = reset;
  assign n7_valid_up = n6_valid_down; // @[Top.scala 25:17]
  assign n7_I_t0b = n6_O_t0b; // @[Top.scala 24:10]
  assign n7_I_t1b = n6_O_t1b; // @[Top.scala 24:10]
  assign n8_valid_up = valid_up & n7_valid_down; // @[Top.scala 29:17]
  assign n8_I0 = I; // @[Top.scala 27:11]
  assign n8_I1_t0b = n7_O_t0b; // @[Top.scala 28:11]
  assign n8_I1_t1b = n7_O_t1b; // @[Top.scala 28:11]
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
  wire  n13_valid_up; // @[Top.scala 37:21]
  wire  n13_valid_down; // @[Top.scala 37:21]
  wire [15:0] n13_I_t0b; // @[Top.scala 37:21]
  wire [15:0] n13_O; // @[Top.scala 37:21]
  wire  n41_clock; // @[Top.scala 40:21]
  wire  n41_reset; // @[Top.scala 40:21]
  wire  n41_valid_up; // @[Top.scala 40:21]
  wire  n41_valid_down; // @[Top.scala 40:21]
  wire [15:0] n41_I; // @[Top.scala 40:21]
  wire [15:0] n41_O; // @[Top.scala 40:21]
  wire  n29_clock; // @[Top.scala 43:21]
  wire  n29_reset; // @[Top.scala 43:21]
  wire  n29_valid_up; // @[Top.scala 43:21]
  wire  n29_valid_down; // @[Top.scala 43:21]
  wire [15:0] n29_I; // @[Top.scala 43:21]
  wire [15:0] n29_O; // @[Top.scala 43:21]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n15_valid_up; // @[Top.scala 47:21]
  wire  n15_valid_down; // @[Top.scala 47:21]
  wire [15:0] n15_I_t1b_t0b; // @[Top.scala 47:21]
  wire [15:0] n15_I_t1b_t1b; // @[Top.scala 47:21]
  wire [15:0] n15_O_t0b; // @[Top.scala 47:21]
  wire [15:0] n15_O_t1b; // @[Top.scala 47:21]
  wire  n16_valid_up; // @[Top.scala 50:21]
  wire  n16_valid_down; // @[Top.scala 50:21]
  wire [15:0] n16_I_t0b; // @[Top.scala 50:21]
  wire [15:0] n16_O; // @[Top.scala 50:21]
  wire  n17_valid_up; // @[Top.scala 53:21]
  wire  n17_valid_down; // @[Top.scala 53:21]
  wire [15:0] n17_I_t1b; // @[Top.scala 53:21]
  wire [15:0] n17_O; // @[Top.scala 53:21]
  wire  n18_valid_up; // @[Top.scala 56:21]
  wire  n18_valid_down; // @[Top.scala 56:21]
  wire [15:0] n18_I0; // @[Top.scala 56:21]
  wire [15:0] n18_I1; // @[Top.scala 56:21]
  wire [15:0] n18_O_t0b; // @[Top.scala 56:21]
  wire [15:0] n18_O_t1b; // @[Top.scala 56:21]
  wire  n19_valid_up; // @[Top.scala 60:21]
  wire  n19_valid_down; // @[Top.scala 60:21]
  wire [15:0] n19_I_t0b; // @[Top.scala 60:21]
  wire [15:0] n19_I_t1b; // @[Top.scala 60:21]
  wire [15:0] n19_O; // @[Top.scala 60:21]
  wire  n21_valid_up; // @[Top.scala 63:21]
  wire  n21_valid_down; // @[Top.scala 63:21]
  wire [15:0] n21_I0; // @[Top.scala 63:21]
  wire [15:0] n21_I1; // @[Top.scala 63:21]
  wire [15:0] n21_O_t0b; // @[Top.scala 63:21]
  wire [15:0] n21_O_t1b; // @[Top.scala 63:21]
  wire  n22_valid_up; // @[Top.scala 67:21]
  wire  n22_valid_down; // @[Top.scala 67:21]
  wire [15:0] n22_I_t0b; // @[Top.scala 67:21]
  wire [15:0] n22_I_t1b; // @[Top.scala 67:21]
  wire [15:0] n22_O; // @[Top.scala 67:21]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n25_valid_up; // @[Top.scala 71:21]
  wire  n25_valid_down; // @[Top.scala 71:21]
  wire [15:0] n25_I0; // @[Top.scala 71:21]
  wire [15:0] n25_O_t0b; // @[Top.scala 71:21]
  wire  n26_valid_up; // @[Top.scala 75:21]
  wire  n26_valid_down; // @[Top.scala 75:21]
  wire [15:0] n26_I_t0b; // @[Top.scala 75:21]
  wire [15:0] n26_O; // @[Top.scala 75:21]
  wire  n27_valid_up; // @[Top.scala 78:21]
  wire  n27_valid_down; // @[Top.scala 78:21]
  wire [15:0] n27_I0; // @[Top.scala 78:21]
  wire [15:0] n27_I1; // @[Top.scala 78:21]
  wire [15:0] n27_O_t0b; // @[Top.scala 78:21]
  wire [15:0] n27_O_t1b; // @[Top.scala 78:21]
  wire  n28_clock; // @[Top.scala 82:21]
  wire  n28_reset; // @[Top.scala 82:21]
  wire  n28_valid_up; // @[Top.scala 82:21]
  wire  n28_valid_down; // @[Top.scala 82:21]
  wire [15:0] n28_I_t0b; // @[Top.scala 82:21]
  wire [15:0] n28_I_t1b; // @[Top.scala 82:21]
  wire [15:0] n28_O; // @[Top.scala 82:21]
  wire  n30_valid_up; // @[Top.scala 85:21]
  wire  n30_valid_down; // @[Top.scala 85:21]
  wire [15:0] n30_I0; // @[Top.scala 85:21]
  wire [15:0] n30_I1; // @[Top.scala 85:21]
  wire [15:0] n30_O_t0b; // @[Top.scala 85:21]
  wire [15:0] n30_O_t1b; // @[Top.scala 85:21]
  wire  n31_valid_up; // @[Top.scala 89:21]
  wire  n31_valid_down; // @[Top.scala 89:21]
  wire [15:0] n31_I_t0b; // @[Top.scala 89:21]
  wire [15:0] n31_I_t1b; // @[Top.scala 89:21]
  wire [15:0] n31_O; // @[Top.scala 89:21]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n33_valid_up; // @[Top.scala 93:21]
  wire  n33_valid_down; // @[Top.scala 93:21]
  wire [15:0] n33_I0; // @[Top.scala 93:21]
  wire [15:0] n33_I1; // @[Top.scala 93:21]
  wire [15:0] n33_O_t0b; // @[Top.scala 93:21]
  wire [15:0] n33_O_t1b; // @[Top.scala 93:21]
  wire  n34_valid_up; // @[Top.scala 97:21]
  wire  n34_valid_down; // @[Top.scala 97:21]
  wire [15:0] n34_I_t0b; // @[Top.scala 97:21]
  wire [15:0] n34_I_t1b; // @[Top.scala 97:21]
  wire [15:0] n34_O; // @[Top.scala 97:21]
  wire  n35_valid_up; // @[Top.scala 100:21]
  wire  n35_valid_down; // @[Top.scala 100:21]
  wire [15:0] n35_I0; // @[Top.scala 100:21]
  wire [15:0] n35_I1; // @[Top.scala 100:21]
  wire [15:0] n35_O_t0b; // @[Top.scala 100:21]
  wire [15:0] n35_O_t1b; // @[Top.scala 100:21]
  wire  n36_valid_up; // @[Top.scala 104:21]
  wire  n36_valid_down; // @[Top.scala 104:21]
  wire [15:0] n36_I0; // @[Top.scala 104:21]
  wire [15:0] n36_I1; // @[Top.scala 104:21]
  wire [15:0] n36_O_t0b; // @[Top.scala 104:21]
  wire [15:0] n36_O_t1b; // @[Top.scala 104:21]
  wire  n37_valid_up; // @[Top.scala 108:21]
  wire  n37_valid_down; // @[Top.scala 108:21]
  wire [15:0] n37_I0_t0b; // @[Top.scala 108:21]
  wire [15:0] n37_I0_t1b; // @[Top.scala 108:21]
  wire [15:0] n37_I1_t0b; // @[Top.scala 108:21]
  wire [15:0] n37_I1_t1b; // @[Top.scala 108:21]
  wire [15:0] n37_O_t0b_t0b; // @[Top.scala 108:21]
  wire [15:0] n37_O_t0b_t1b; // @[Top.scala 108:21]
  wire [15:0] n37_O_t1b_t0b; // @[Top.scala 108:21]
  wire [15:0] n37_O_t1b_t1b; // @[Top.scala 108:21]
  wire  n38_clock; // @[Top.scala 112:21]
  wire  n38_reset; // @[Top.scala 112:21]
  wire  n38_valid_up; // @[Top.scala 112:21]
  wire  n38_valid_down; // @[Top.scala 112:21]
  wire [15:0] n38_I_t0b_t0b; // @[Top.scala 112:21]
  wire [15:0] n38_I_t0b_t1b; // @[Top.scala 112:21]
  wire [15:0] n38_I_t1b_t0b; // @[Top.scala 112:21]
  wire [15:0] n38_I_t1b_t1b; // @[Top.scala 112:21]
  wire [15:0] n38_O_t0b_t0b; // @[Top.scala 112:21]
  wire [15:0] n38_O_t0b_t1b; // @[Top.scala 112:21]
  wire [15:0] n38_O_t1b_t0b; // @[Top.scala 112:21]
  wire [15:0] n38_O_t1b_t1b; // @[Top.scala 112:21]
  wire  n39_valid_up; // @[Top.scala 115:21]
  wire  n39_valid_down; // @[Top.scala 115:21]
  wire  n39_I0; // @[Top.scala 115:21]
  wire [15:0] n39_I1_t0b_t0b; // @[Top.scala 115:21]
  wire [15:0] n39_I1_t0b_t1b; // @[Top.scala 115:21]
  wire [15:0] n39_I1_t1b_t0b; // @[Top.scala 115:21]
  wire [15:0] n39_I1_t1b_t1b; // @[Top.scala 115:21]
  wire  n39_O_t0b; // @[Top.scala 115:21]
  wire [15:0] n39_O_t1b_t0b_t0b; // @[Top.scala 115:21]
  wire [15:0] n39_O_t1b_t0b_t1b; // @[Top.scala 115:21]
  wire [15:0] n39_O_t1b_t1b_t0b; // @[Top.scala 115:21]
  wire [15:0] n39_O_t1b_t1b_t1b; // @[Top.scala 115:21]
  wire  n40_valid_up; // @[Top.scala 119:21]
  wire  n40_valid_down; // @[Top.scala 119:21]
  wire  n40_I_t0b; // @[Top.scala 119:21]
  wire [15:0] n40_I_t1b_t0b_t0b; // @[Top.scala 119:21]
  wire [15:0] n40_I_t1b_t0b_t1b; // @[Top.scala 119:21]
  wire [15:0] n40_I_t1b_t1b_t0b; // @[Top.scala 119:21]
  wire [15:0] n40_I_t1b_t1b_t1b; // @[Top.scala 119:21]
  wire [15:0] n40_O_t0b; // @[Top.scala 119:21]
  wire [15:0] n40_O_t1b; // @[Top.scala 119:21]
  wire  n42_valid_up; // @[Top.scala 122:21]
  wire  n42_valid_down; // @[Top.scala 122:21]
  wire [15:0] n42_I0; // @[Top.scala 122:21]
  wire [15:0] n42_I1_t0b; // @[Top.scala 122:21]
  wire [15:0] n42_I1_t1b; // @[Top.scala 122:21]
  wire [15:0] n42_O_t0b; // @[Top.scala 122:21]
  wire [15:0] n42_O_t1b_t0b; // @[Top.scala 122:21]
  wire [15:0] n42_O_t1b_t1b; // @[Top.scala 122:21]
  Fst n13 ( // @[Top.scala 37:21]
    .valid_up(n13_valid_up),
    .valid_down(n13_valid_down),
    .I_t0b(n13_I_t0b),
    .O(n13_O)
  );
  FIFO_2 n41 ( // @[Top.scala 40:21]
    .clock(n41_clock),
    .reset(n41_reset),
    .valid_up(n41_valid_up),
    .valid_down(n41_valid_down),
    .I(n41_I),
    .O(n41_O)
  );
  FIFO_2 n29 ( // @[Top.scala 43:21]
    .clock(n29_clock),
    .reset(n29_reset),
    .valid_up(n29_valid_up),
    .valid_down(n29_valid_down),
    .I(n29_I),
    .O(n29_O)
  );
  InitialDelayCounter InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n15 ( // @[Top.scala 47:21]
    .valid_up(n15_valid_up),
    .valid_down(n15_valid_down),
    .I_t1b_t0b(n15_I_t1b_t0b),
    .I_t1b_t1b(n15_I_t1b_t1b),
    .O_t0b(n15_O_t0b),
    .O_t1b(n15_O_t1b)
  );
  Fst_1 n16 ( // @[Top.scala 50:21]
    .valid_up(n16_valid_up),
    .valid_down(n16_valid_down),
    .I_t0b(n16_I_t0b),
    .O(n16_O)
  );
  Snd_1 n17 ( // @[Top.scala 53:21]
    .valid_up(n17_valid_up),
    .valid_down(n17_valid_down),
    .I_t1b(n17_I_t1b),
    .O(n17_O)
  );
  AtomTuple n18 ( // @[Top.scala 56:21]
    .valid_up(n18_valid_up),
    .valid_down(n18_valid_down),
    .I0(n18_I0),
    .I1(n18_I1),
    .O_t0b(n18_O_t0b),
    .O_t1b(n18_O_t1b)
  );
  Add n19 ( // @[Top.scala 60:21]
    .valid_up(n19_valid_up),
    .valid_down(n19_valid_down),
    .I_t0b(n19_I_t0b),
    .I_t1b(n19_I_t1b),
    .O(n19_O)
  );
  AtomTuple n21 ( // @[Top.scala 63:21]
    .valid_up(n21_valid_up),
    .valid_down(n21_valid_down),
    .I0(n21_I0),
    .I1(n21_I1),
    .O_t0b(n21_O_t0b),
    .O_t1b(n21_O_t1b)
  );
  Add n22 ( // @[Top.scala 67:21]
    .valid_up(n22_valid_up),
    .valid_down(n22_valid_down),
    .I_t0b(n22_I_t0b),
    .I_t1b(n22_I_t1b),
    .O(n22_O)
  );
  InitialDelayCounter InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n25 ( // @[Top.scala 71:21]
    .valid_up(n25_valid_up),
    .valid_down(n25_valid_down),
    .I0(n25_I0),
    .O_t0b(n25_O_t0b)
  );
  RShift n26 ( // @[Top.scala 75:21]
    .valid_up(n26_valid_up),
    .valid_down(n26_valid_down),
    .I_t0b(n26_I_t0b),
    .O(n26_O)
  );
  AtomTuple n27 ( // @[Top.scala 78:21]
    .valid_up(n27_valid_up),
    .valid_down(n27_valid_down),
    .I0(n27_I0),
    .I1(n27_I1),
    .O_t0b(n27_O_t0b),
    .O_t1b(n27_O_t1b)
  );
  Mul n28 ( // @[Top.scala 82:21]
    .clock(n28_clock),
    .reset(n28_reset),
    .valid_up(n28_valid_up),
    .valid_down(n28_valid_down),
    .I_t0b(n28_I_t0b),
    .I_t1b(n28_I_t1b),
    .O(n28_O)
  );
  AtomTuple n30 ( // @[Top.scala 85:21]
    .valid_up(n30_valid_up),
    .valid_down(n30_valid_down),
    .I0(n30_I0),
    .I1(n30_I1),
    .O_t0b(n30_O_t0b),
    .O_t1b(n30_O_t1b)
  );
  Lt n31 ( // @[Top.scala 89:21]
    .valid_up(n31_valid_up),
    .valid_down(n31_valid_down),
    .I_t0b(n31_I_t0b),
    .I_t1b(n31_I_t1b),
    .O(n31_O)
  );
  InitialDelayCounter InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n33 ( // @[Top.scala 93:21]
    .valid_up(n33_valid_up),
    .valid_down(n33_valid_down),
    .I0(n33_I0),
    .I1(n33_I1),
    .O_t0b(n33_O_t0b),
    .O_t1b(n33_O_t1b)
  );
  Sub n34 ( // @[Top.scala 97:21]
    .valid_up(n34_valid_up),
    .valid_down(n34_valid_down),
    .I_t0b(n34_I_t0b),
    .I_t1b(n34_I_t1b),
    .O(n34_O)
  );
  AtomTuple n35 ( // @[Top.scala 100:21]
    .valid_up(n35_valid_up),
    .valid_down(n35_valid_down),
    .I0(n35_I0),
    .I1(n35_I1),
    .O_t0b(n35_O_t0b),
    .O_t1b(n35_O_t1b)
  );
  AtomTuple n36 ( // @[Top.scala 104:21]
    .valid_up(n36_valid_up),
    .valid_down(n36_valid_down),
    .I0(n36_I0),
    .I1(n36_I1),
    .O_t0b(n36_O_t0b),
    .O_t1b(n36_O_t1b)
  );
  AtomTuple_10 n37 ( // @[Top.scala 108:21]
    .valid_up(n37_valid_up),
    .valid_down(n37_valid_down),
    .I0_t0b(n37_I0_t0b),
    .I0_t1b(n37_I0_t1b),
    .I1_t0b(n37_I1_t0b),
    .I1_t1b(n37_I1_t1b),
    .O_t0b_t0b(n37_O_t0b_t0b),
    .O_t0b_t1b(n37_O_t0b_t1b),
    .O_t1b_t0b(n37_O_t1b_t0b),
    .O_t1b_t1b(n37_O_t1b_t1b)
  );
  FIFO_4 n38 ( // @[Top.scala 112:21]
    .clock(n38_clock),
    .reset(n38_reset),
    .valid_up(n38_valid_up),
    .valid_down(n38_valid_down),
    .I_t0b_t0b(n38_I_t0b_t0b),
    .I_t0b_t1b(n38_I_t0b_t1b),
    .I_t1b_t0b(n38_I_t1b_t0b),
    .I_t1b_t1b(n38_I_t1b_t1b),
    .O_t0b_t0b(n38_O_t0b_t0b),
    .O_t0b_t1b(n38_O_t0b_t1b),
    .O_t1b_t0b(n38_O_t1b_t0b),
    .O_t1b_t1b(n38_O_t1b_t1b)
  );
  AtomTuple_11 n39 ( // @[Top.scala 115:21]
    .valid_up(n39_valid_up),
    .valid_down(n39_valid_down),
    .I0(n39_I0),
    .I1_t0b_t0b(n39_I1_t0b_t0b),
    .I1_t0b_t1b(n39_I1_t0b_t1b),
    .I1_t1b_t0b(n39_I1_t1b_t0b),
    .I1_t1b_t1b(n39_I1_t1b_t1b),
    .O_t0b(n39_O_t0b),
    .O_t1b_t0b_t0b(n39_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n39_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n39_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n39_O_t1b_t1b_t1b)
  );
  If n40 ( // @[Top.scala 119:21]
    .valid_up(n40_valid_up),
    .valid_down(n40_valid_down),
    .I_t0b(n40_I_t0b),
    .I_t1b_t0b_t0b(n40_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n40_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n40_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n40_I_t1b_t1b_t1b),
    .O_t0b(n40_O_t0b),
    .O_t1b(n40_O_t1b)
  );
  AtomTuple_1 n42 ( // @[Top.scala 122:21]
    .valid_up(n42_valid_up),
    .valid_down(n42_valid_down),
    .I0(n42_I0),
    .I1_t0b(n42_I1_t0b),
    .I1_t1b(n42_I1_t1b),
    .O_t0b(n42_O_t0b),
    .O_t1b_t0b(n42_O_t1b_t0b),
    .O_t1b_t1b(n42_O_t1b_t1b)
  );
  assign valid_down = n42_valid_down; // @[Top.scala 127:16]
  assign O_t0b = n42_O_t0b; // @[Top.scala 126:7]
  assign O_t1b_t0b = n42_O_t1b_t0b; // @[Top.scala 126:7]
  assign O_t1b_t1b = n42_O_t1b_t1b; // @[Top.scala 126:7]
  assign n13_valid_up = valid_up; // @[Top.scala 39:18]
  assign n13_I_t0b = I_t0b; // @[Top.scala 38:11]
  assign n41_clock = clock;
  assign n41_reset = reset;
  assign n41_valid_up = n13_valid_down; // @[Top.scala 42:18]
  assign n41_I = n13_O; // @[Top.scala 41:11]
  assign n29_clock = clock;
  assign n29_reset = reset;
  assign n29_valid_up = n13_valid_down; // @[Top.scala 45:18]
  assign n29_I = n13_O; // @[Top.scala 44:11]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n15_valid_up = valid_up; // @[Top.scala 49:18]
  assign n15_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 48:11]
  assign n15_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 48:11]
  assign n16_valid_up = n15_valid_down; // @[Top.scala 52:18]
  assign n16_I_t0b = n15_O_t0b; // @[Top.scala 51:11]
  assign n17_valid_up = n15_valid_down; // @[Top.scala 55:18]
  assign n17_I_t1b = n15_O_t1b; // @[Top.scala 54:11]
  assign n18_valid_up = n16_valid_down & n17_valid_down; // @[Top.scala 59:18]
  assign n18_I0 = n16_O; // @[Top.scala 57:12]
  assign n18_I1 = n17_O; // @[Top.scala 58:12]
  assign n19_valid_up = n18_valid_down; // @[Top.scala 62:18]
  assign n19_I_t0b = n18_O_t0b; // @[Top.scala 61:11]
  assign n19_I_t1b = n18_O_t1b; // @[Top.scala 61:11]
  assign n21_valid_up = InitialDelayCounter_valid_down & n19_valid_down; // @[Top.scala 66:18]
  assign n21_I0 = 16'h1; // @[Top.scala 64:12]
  assign n21_I1 = n19_O; // @[Top.scala 65:12]
  assign n22_valid_up = n21_valid_down; // @[Top.scala 69:18]
  assign n22_I_t0b = n21_O_t0b; // @[Top.scala 68:11]
  assign n22_I_t1b = n21_O_t1b; // @[Top.scala 68:11]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n25_valid_up = n22_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 74:18]
  assign n25_I0 = n22_O; // @[Top.scala 72:12]
  assign n26_valid_up = n25_valid_down; // @[Top.scala 77:18]
  assign n26_I_t0b = n25_O_t0b; // @[Top.scala 76:11]
  assign n27_valid_up = n26_valid_down; // @[Top.scala 81:18]
  assign n27_I0 = n26_O; // @[Top.scala 79:12]
  assign n27_I1 = n26_O; // @[Top.scala 80:12]
  assign n28_clock = clock;
  assign n28_reset = reset;
  assign n28_valid_up = n27_valid_down; // @[Top.scala 84:18]
  assign n28_I_t0b = n27_O_t0b; // @[Top.scala 83:11]
  assign n28_I_t1b = n27_O_t1b; // @[Top.scala 83:11]
  assign n30_valid_up = n29_valid_down & n28_valid_down; // @[Top.scala 88:18]
  assign n30_I0 = n29_O; // @[Top.scala 86:12]
  assign n30_I1 = n28_O; // @[Top.scala 87:12]
  assign n31_valid_up = n30_valid_down; // @[Top.scala 91:18]
  assign n31_I_t0b = n30_O_t0b; // @[Top.scala 90:11]
  assign n31_I_t1b = n30_O_t1b; // @[Top.scala 90:11]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n33_valid_up = n26_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 96:18]
  assign n33_I0 = n26_O; // @[Top.scala 94:12]
  assign n33_I1 = 16'h1; // @[Top.scala 95:12]
  assign n34_valid_up = n33_valid_down; // @[Top.scala 99:18]
  assign n34_I_t0b = n33_O_t0b; // @[Top.scala 98:11]
  assign n34_I_t1b = n33_O_t1b; // @[Top.scala 98:11]
  assign n35_valid_up = n16_valid_down & n34_valid_down; // @[Top.scala 103:18]
  assign n35_I0 = n16_O; // @[Top.scala 101:12]
  assign n35_I1 = n34_O; // @[Top.scala 102:12]
  assign n36_valid_up = n26_valid_down & n17_valid_down; // @[Top.scala 107:18]
  assign n36_I0 = n26_O; // @[Top.scala 105:12]
  assign n36_I1 = n17_O; // @[Top.scala 106:12]
  assign n37_valid_up = n35_valid_down & n36_valid_down; // @[Top.scala 111:18]
  assign n37_I0_t0b = n35_O_t0b; // @[Top.scala 109:12]
  assign n37_I0_t1b = n35_O_t1b; // @[Top.scala 109:12]
  assign n37_I1_t0b = n36_O_t0b; // @[Top.scala 110:12]
  assign n37_I1_t1b = n36_O_t1b; // @[Top.scala 110:12]
  assign n38_clock = clock;
  assign n38_reset = reset;
  assign n38_valid_up = n37_valid_down; // @[Top.scala 114:18]
  assign n38_I_t0b_t0b = n37_O_t0b_t0b; // @[Top.scala 113:11]
  assign n38_I_t0b_t1b = n37_O_t0b_t1b; // @[Top.scala 113:11]
  assign n38_I_t1b_t0b = n37_O_t1b_t0b; // @[Top.scala 113:11]
  assign n38_I_t1b_t1b = n37_O_t1b_t1b; // @[Top.scala 113:11]
  assign n39_valid_up = n31_valid_down & n38_valid_down; // @[Top.scala 118:18]
  assign n39_I0 = n31_O[0]; // @[Top.scala 116:12]
  assign n39_I1_t0b_t0b = n38_O_t0b_t0b; // @[Top.scala 117:12]
  assign n39_I1_t0b_t1b = n38_O_t0b_t1b; // @[Top.scala 117:12]
  assign n39_I1_t1b_t0b = n38_O_t1b_t0b; // @[Top.scala 117:12]
  assign n39_I1_t1b_t1b = n38_O_t1b_t1b; // @[Top.scala 117:12]
  assign n40_valid_up = n39_valid_down; // @[Top.scala 121:18]
  assign n40_I_t0b = n39_O_t0b; // @[Top.scala 120:11]
  assign n40_I_t1b_t0b_t0b = n39_O_t1b_t0b_t0b; // @[Top.scala 120:11]
  assign n40_I_t1b_t0b_t1b = n39_O_t1b_t0b_t1b; // @[Top.scala 120:11]
  assign n40_I_t1b_t1b_t0b = n39_O_t1b_t1b_t0b; // @[Top.scala 120:11]
  assign n40_I_t1b_t1b_t1b = n39_O_t1b_t1b_t1b; // @[Top.scala 120:11]
  assign n42_valid_up = n41_valid_down & n40_valid_down; // @[Top.scala 125:18]
  assign n42_I0 = n41_O; // @[Top.scala 123:12]
  assign n42_I1_t0b = n40_O_t0b; // @[Top.scala 124:12]
  assign n42_I1_t1b = n40_O_t1b; // @[Top.scala 124:12]
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
  wire  n47_valid_up; // @[Top.scala 133:21]
  wire  n47_valid_down; // @[Top.scala 133:21]
  wire [15:0] n47_I_t0b; // @[Top.scala 133:21]
  wire [15:0] n47_O; // @[Top.scala 133:21]
  wire  n75_clock; // @[Top.scala 136:21]
  wire  n75_reset; // @[Top.scala 136:21]
  wire  n75_valid_up; // @[Top.scala 136:21]
  wire  n75_valid_down; // @[Top.scala 136:21]
  wire [15:0] n75_I; // @[Top.scala 136:21]
  wire [15:0] n75_O; // @[Top.scala 136:21]
  wire  n63_clock; // @[Top.scala 139:21]
  wire  n63_reset; // @[Top.scala 139:21]
  wire  n63_valid_up; // @[Top.scala 139:21]
  wire  n63_valid_down; // @[Top.scala 139:21]
  wire [15:0] n63_I; // @[Top.scala 139:21]
  wire [15:0] n63_O; // @[Top.scala 139:21]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n49_valid_up; // @[Top.scala 143:21]
  wire  n49_valid_down; // @[Top.scala 143:21]
  wire [15:0] n49_I_t1b_t0b; // @[Top.scala 143:21]
  wire [15:0] n49_I_t1b_t1b; // @[Top.scala 143:21]
  wire [15:0] n49_O_t0b; // @[Top.scala 143:21]
  wire [15:0] n49_O_t1b; // @[Top.scala 143:21]
  wire  n50_valid_up; // @[Top.scala 146:21]
  wire  n50_valid_down; // @[Top.scala 146:21]
  wire [15:0] n50_I_t0b; // @[Top.scala 146:21]
  wire [15:0] n50_O; // @[Top.scala 146:21]
  wire  n51_valid_up; // @[Top.scala 149:21]
  wire  n51_valid_down; // @[Top.scala 149:21]
  wire [15:0] n51_I_t1b; // @[Top.scala 149:21]
  wire [15:0] n51_O; // @[Top.scala 149:21]
  wire  n52_valid_up; // @[Top.scala 152:21]
  wire  n52_valid_down; // @[Top.scala 152:21]
  wire [15:0] n52_I0; // @[Top.scala 152:21]
  wire [15:0] n52_I1; // @[Top.scala 152:21]
  wire [15:0] n52_O_t0b; // @[Top.scala 152:21]
  wire [15:0] n52_O_t1b; // @[Top.scala 152:21]
  wire  n53_valid_up; // @[Top.scala 156:21]
  wire  n53_valid_down; // @[Top.scala 156:21]
  wire [15:0] n53_I_t0b; // @[Top.scala 156:21]
  wire [15:0] n53_I_t1b; // @[Top.scala 156:21]
  wire [15:0] n53_O; // @[Top.scala 156:21]
  wire  n55_valid_up; // @[Top.scala 159:21]
  wire  n55_valid_down; // @[Top.scala 159:21]
  wire [15:0] n55_I0; // @[Top.scala 159:21]
  wire [15:0] n55_I1; // @[Top.scala 159:21]
  wire [15:0] n55_O_t0b; // @[Top.scala 159:21]
  wire [15:0] n55_O_t1b; // @[Top.scala 159:21]
  wire  n56_valid_up; // @[Top.scala 163:21]
  wire  n56_valid_down; // @[Top.scala 163:21]
  wire [15:0] n56_I_t0b; // @[Top.scala 163:21]
  wire [15:0] n56_I_t1b; // @[Top.scala 163:21]
  wire [15:0] n56_O; // @[Top.scala 163:21]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n59_valid_up; // @[Top.scala 167:21]
  wire  n59_valid_down; // @[Top.scala 167:21]
  wire [15:0] n59_I0; // @[Top.scala 167:21]
  wire [15:0] n59_O_t0b; // @[Top.scala 167:21]
  wire  n60_valid_up; // @[Top.scala 171:21]
  wire  n60_valid_down; // @[Top.scala 171:21]
  wire [15:0] n60_I_t0b; // @[Top.scala 171:21]
  wire [15:0] n60_O; // @[Top.scala 171:21]
  wire  n61_valid_up; // @[Top.scala 174:21]
  wire  n61_valid_down; // @[Top.scala 174:21]
  wire [15:0] n61_I0; // @[Top.scala 174:21]
  wire [15:0] n61_I1; // @[Top.scala 174:21]
  wire [15:0] n61_O_t0b; // @[Top.scala 174:21]
  wire [15:0] n61_O_t1b; // @[Top.scala 174:21]
  wire  n62_clock; // @[Top.scala 178:21]
  wire  n62_reset; // @[Top.scala 178:21]
  wire  n62_valid_up; // @[Top.scala 178:21]
  wire  n62_valid_down; // @[Top.scala 178:21]
  wire [15:0] n62_I_t0b; // @[Top.scala 178:21]
  wire [15:0] n62_I_t1b; // @[Top.scala 178:21]
  wire [15:0] n62_O; // @[Top.scala 178:21]
  wire  n64_valid_up; // @[Top.scala 181:21]
  wire  n64_valid_down; // @[Top.scala 181:21]
  wire [15:0] n64_I0; // @[Top.scala 181:21]
  wire [15:0] n64_I1; // @[Top.scala 181:21]
  wire [15:0] n64_O_t0b; // @[Top.scala 181:21]
  wire [15:0] n64_O_t1b; // @[Top.scala 181:21]
  wire  n65_valid_up; // @[Top.scala 185:21]
  wire  n65_valid_down; // @[Top.scala 185:21]
  wire [15:0] n65_I_t0b; // @[Top.scala 185:21]
  wire [15:0] n65_I_t1b; // @[Top.scala 185:21]
  wire [15:0] n65_O; // @[Top.scala 185:21]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n67_valid_up; // @[Top.scala 189:21]
  wire  n67_valid_down; // @[Top.scala 189:21]
  wire [15:0] n67_I0; // @[Top.scala 189:21]
  wire [15:0] n67_I1; // @[Top.scala 189:21]
  wire [15:0] n67_O_t0b; // @[Top.scala 189:21]
  wire [15:0] n67_O_t1b; // @[Top.scala 189:21]
  wire  n68_valid_up; // @[Top.scala 193:21]
  wire  n68_valid_down; // @[Top.scala 193:21]
  wire [15:0] n68_I_t0b; // @[Top.scala 193:21]
  wire [15:0] n68_I_t1b; // @[Top.scala 193:21]
  wire [15:0] n68_O; // @[Top.scala 193:21]
  wire  n69_valid_up; // @[Top.scala 196:21]
  wire  n69_valid_down; // @[Top.scala 196:21]
  wire [15:0] n69_I0; // @[Top.scala 196:21]
  wire [15:0] n69_I1; // @[Top.scala 196:21]
  wire [15:0] n69_O_t0b; // @[Top.scala 196:21]
  wire [15:0] n69_O_t1b; // @[Top.scala 196:21]
  wire  n70_valid_up; // @[Top.scala 200:21]
  wire  n70_valid_down; // @[Top.scala 200:21]
  wire [15:0] n70_I0; // @[Top.scala 200:21]
  wire [15:0] n70_I1; // @[Top.scala 200:21]
  wire [15:0] n70_O_t0b; // @[Top.scala 200:21]
  wire [15:0] n70_O_t1b; // @[Top.scala 200:21]
  wire  n71_valid_up; // @[Top.scala 204:21]
  wire  n71_valid_down; // @[Top.scala 204:21]
  wire [15:0] n71_I0_t0b; // @[Top.scala 204:21]
  wire [15:0] n71_I0_t1b; // @[Top.scala 204:21]
  wire [15:0] n71_I1_t0b; // @[Top.scala 204:21]
  wire [15:0] n71_I1_t1b; // @[Top.scala 204:21]
  wire [15:0] n71_O_t0b_t0b; // @[Top.scala 204:21]
  wire [15:0] n71_O_t0b_t1b; // @[Top.scala 204:21]
  wire [15:0] n71_O_t1b_t0b; // @[Top.scala 204:21]
  wire [15:0] n71_O_t1b_t1b; // @[Top.scala 204:21]
  wire  n72_clock; // @[Top.scala 208:21]
  wire  n72_reset; // @[Top.scala 208:21]
  wire  n72_valid_up; // @[Top.scala 208:21]
  wire  n72_valid_down; // @[Top.scala 208:21]
  wire [15:0] n72_I_t0b_t0b; // @[Top.scala 208:21]
  wire [15:0] n72_I_t0b_t1b; // @[Top.scala 208:21]
  wire [15:0] n72_I_t1b_t0b; // @[Top.scala 208:21]
  wire [15:0] n72_I_t1b_t1b; // @[Top.scala 208:21]
  wire [15:0] n72_O_t0b_t0b; // @[Top.scala 208:21]
  wire [15:0] n72_O_t0b_t1b; // @[Top.scala 208:21]
  wire [15:0] n72_O_t1b_t0b; // @[Top.scala 208:21]
  wire [15:0] n72_O_t1b_t1b; // @[Top.scala 208:21]
  wire  n73_valid_up; // @[Top.scala 211:21]
  wire  n73_valid_down; // @[Top.scala 211:21]
  wire  n73_I0; // @[Top.scala 211:21]
  wire [15:0] n73_I1_t0b_t0b; // @[Top.scala 211:21]
  wire [15:0] n73_I1_t0b_t1b; // @[Top.scala 211:21]
  wire [15:0] n73_I1_t1b_t0b; // @[Top.scala 211:21]
  wire [15:0] n73_I1_t1b_t1b; // @[Top.scala 211:21]
  wire  n73_O_t0b; // @[Top.scala 211:21]
  wire [15:0] n73_O_t1b_t0b_t0b; // @[Top.scala 211:21]
  wire [15:0] n73_O_t1b_t0b_t1b; // @[Top.scala 211:21]
  wire [15:0] n73_O_t1b_t1b_t0b; // @[Top.scala 211:21]
  wire [15:0] n73_O_t1b_t1b_t1b; // @[Top.scala 211:21]
  wire  n74_valid_up; // @[Top.scala 215:21]
  wire  n74_valid_down; // @[Top.scala 215:21]
  wire  n74_I_t0b; // @[Top.scala 215:21]
  wire [15:0] n74_I_t1b_t0b_t0b; // @[Top.scala 215:21]
  wire [15:0] n74_I_t1b_t0b_t1b; // @[Top.scala 215:21]
  wire [15:0] n74_I_t1b_t1b_t0b; // @[Top.scala 215:21]
  wire [15:0] n74_I_t1b_t1b_t1b; // @[Top.scala 215:21]
  wire [15:0] n74_O_t0b; // @[Top.scala 215:21]
  wire [15:0] n74_O_t1b; // @[Top.scala 215:21]
  wire  n76_valid_up; // @[Top.scala 218:21]
  wire  n76_valid_down; // @[Top.scala 218:21]
  wire [15:0] n76_I0; // @[Top.scala 218:21]
  wire [15:0] n76_I1_t0b; // @[Top.scala 218:21]
  wire [15:0] n76_I1_t1b; // @[Top.scala 218:21]
  wire [15:0] n76_O_t0b; // @[Top.scala 218:21]
  wire [15:0] n76_O_t1b_t0b; // @[Top.scala 218:21]
  wire [15:0] n76_O_t1b_t1b; // @[Top.scala 218:21]
  Fst n47 ( // @[Top.scala 133:21]
    .valid_up(n47_valid_up),
    .valid_down(n47_valid_down),
    .I_t0b(n47_I_t0b),
    .O(n47_O)
  );
  FIFO_2 n75 ( // @[Top.scala 136:21]
    .clock(n75_clock),
    .reset(n75_reset),
    .valid_up(n75_valid_up),
    .valid_down(n75_valid_down),
    .I(n75_I),
    .O(n75_O)
  );
  FIFO_2 n63 ( // @[Top.scala 139:21]
    .clock(n63_clock),
    .reset(n63_reset),
    .valid_up(n63_valid_up),
    .valid_down(n63_valid_down),
    .I(n63_I),
    .O(n63_O)
  );
  InitialDelayCounter_3 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n49 ( // @[Top.scala 143:21]
    .valid_up(n49_valid_up),
    .valid_down(n49_valid_down),
    .I_t1b_t0b(n49_I_t1b_t0b),
    .I_t1b_t1b(n49_I_t1b_t1b),
    .O_t0b(n49_O_t0b),
    .O_t1b(n49_O_t1b)
  );
  Fst_1 n50 ( // @[Top.scala 146:21]
    .valid_up(n50_valid_up),
    .valid_down(n50_valid_down),
    .I_t0b(n50_I_t0b),
    .O(n50_O)
  );
  Snd_1 n51 ( // @[Top.scala 149:21]
    .valid_up(n51_valid_up),
    .valid_down(n51_valid_down),
    .I_t1b(n51_I_t1b),
    .O(n51_O)
  );
  AtomTuple n52 ( // @[Top.scala 152:21]
    .valid_up(n52_valid_up),
    .valid_down(n52_valid_down),
    .I0(n52_I0),
    .I1(n52_I1),
    .O_t0b(n52_O_t0b),
    .O_t1b(n52_O_t1b)
  );
  Add n53 ( // @[Top.scala 156:21]
    .valid_up(n53_valid_up),
    .valid_down(n53_valid_down),
    .I_t0b(n53_I_t0b),
    .I_t1b(n53_I_t1b),
    .O(n53_O)
  );
  AtomTuple n55 ( // @[Top.scala 159:21]
    .valid_up(n55_valid_up),
    .valid_down(n55_valid_down),
    .I0(n55_I0),
    .I1(n55_I1),
    .O_t0b(n55_O_t0b),
    .O_t1b(n55_O_t1b)
  );
  Add n56 ( // @[Top.scala 163:21]
    .valid_up(n56_valid_up),
    .valid_down(n56_valid_down),
    .I_t0b(n56_I_t0b),
    .I_t1b(n56_I_t1b),
    .O(n56_O)
  );
  InitialDelayCounter_3 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n59 ( // @[Top.scala 167:21]
    .valid_up(n59_valid_up),
    .valid_down(n59_valid_down),
    .I0(n59_I0),
    .O_t0b(n59_O_t0b)
  );
  RShift n60 ( // @[Top.scala 171:21]
    .valid_up(n60_valid_up),
    .valid_down(n60_valid_down),
    .I_t0b(n60_I_t0b),
    .O(n60_O)
  );
  AtomTuple n61 ( // @[Top.scala 174:21]
    .valid_up(n61_valid_up),
    .valid_down(n61_valid_down),
    .I0(n61_I0),
    .I1(n61_I1),
    .O_t0b(n61_O_t0b),
    .O_t1b(n61_O_t1b)
  );
  Mul n62 ( // @[Top.scala 178:21]
    .clock(n62_clock),
    .reset(n62_reset),
    .valid_up(n62_valid_up),
    .valid_down(n62_valid_down),
    .I_t0b(n62_I_t0b),
    .I_t1b(n62_I_t1b),
    .O(n62_O)
  );
  AtomTuple n64 ( // @[Top.scala 181:21]
    .valid_up(n64_valid_up),
    .valid_down(n64_valid_down),
    .I0(n64_I0),
    .I1(n64_I1),
    .O_t0b(n64_O_t0b),
    .O_t1b(n64_O_t1b)
  );
  Lt n65 ( // @[Top.scala 185:21]
    .valid_up(n65_valid_up),
    .valid_down(n65_valid_down),
    .I_t0b(n65_I_t0b),
    .I_t1b(n65_I_t1b),
    .O(n65_O)
  );
  InitialDelayCounter_3 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n67 ( // @[Top.scala 189:21]
    .valid_up(n67_valid_up),
    .valid_down(n67_valid_down),
    .I0(n67_I0),
    .I1(n67_I1),
    .O_t0b(n67_O_t0b),
    .O_t1b(n67_O_t1b)
  );
  Sub n68 ( // @[Top.scala 193:21]
    .valid_up(n68_valid_up),
    .valid_down(n68_valid_down),
    .I_t0b(n68_I_t0b),
    .I_t1b(n68_I_t1b),
    .O(n68_O)
  );
  AtomTuple n69 ( // @[Top.scala 196:21]
    .valid_up(n69_valid_up),
    .valid_down(n69_valid_down),
    .I0(n69_I0),
    .I1(n69_I1),
    .O_t0b(n69_O_t0b),
    .O_t1b(n69_O_t1b)
  );
  AtomTuple n70 ( // @[Top.scala 200:21]
    .valid_up(n70_valid_up),
    .valid_down(n70_valid_down),
    .I0(n70_I0),
    .I1(n70_I1),
    .O_t0b(n70_O_t0b),
    .O_t1b(n70_O_t1b)
  );
  AtomTuple_10 n71 ( // @[Top.scala 204:21]
    .valid_up(n71_valid_up),
    .valid_down(n71_valid_down),
    .I0_t0b(n71_I0_t0b),
    .I0_t1b(n71_I0_t1b),
    .I1_t0b(n71_I1_t0b),
    .I1_t1b(n71_I1_t1b),
    .O_t0b_t0b(n71_O_t0b_t0b),
    .O_t0b_t1b(n71_O_t0b_t1b),
    .O_t1b_t0b(n71_O_t1b_t0b),
    .O_t1b_t1b(n71_O_t1b_t1b)
  );
  FIFO_4 n72 ( // @[Top.scala 208:21]
    .clock(n72_clock),
    .reset(n72_reset),
    .valid_up(n72_valid_up),
    .valid_down(n72_valid_down),
    .I_t0b_t0b(n72_I_t0b_t0b),
    .I_t0b_t1b(n72_I_t0b_t1b),
    .I_t1b_t0b(n72_I_t1b_t0b),
    .I_t1b_t1b(n72_I_t1b_t1b),
    .O_t0b_t0b(n72_O_t0b_t0b),
    .O_t0b_t1b(n72_O_t0b_t1b),
    .O_t1b_t0b(n72_O_t1b_t0b),
    .O_t1b_t1b(n72_O_t1b_t1b)
  );
  AtomTuple_11 n73 ( // @[Top.scala 211:21]
    .valid_up(n73_valid_up),
    .valid_down(n73_valid_down),
    .I0(n73_I0),
    .I1_t0b_t0b(n73_I1_t0b_t0b),
    .I1_t0b_t1b(n73_I1_t0b_t1b),
    .I1_t1b_t0b(n73_I1_t1b_t0b),
    .I1_t1b_t1b(n73_I1_t1b_t1b),
    .O_t0b(n73_O_t0b),
    .O_t1b_t0b_t0b(n73_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n73_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n73_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n73_O_t1b_t1b_t1b)
  );
  If n74 ( // @[Top.scala 215:21]
    .valid_up(n74_valid_up),
    .valid_down(n74_valid_down),
    .I_t0b(n74_I_t0b),
    .I_t1b_t0b_t0b(n74_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n74_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n74_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n74_I_t1b_t1b_t1b),
    .O_t0b(n74_O_t0b),
    .O_t1b(n74_O_t1b)
  );
  AtomTuple_1 n76 ( // @[Top.scala 218:21]
    .valid_up(n76_valid_up),
    .valid_down(n76_valid_down),
    .I0(n76_I0),
    .I1_t0b(n76_I1_t0b),
    .I1_t1b(n76_I1_t1b),
    .O_t0b(n76_O_t0b),
    .O_t1b_t0b(n76_O_t1b_t0b),
    .O_t1b_t1b(n76_O_t1b_t1b)
  );
  assign valid_down = n76_valid_down; // @[Top.scala 223:16]
  assign O_t0b = n76_O_t0b; // @[Top.scala 222:7]
  assign O_t1b_t0b = n76_O_t1b_t0b; // @[Top.scala 222:7]
  assign O_t1b_t1b = n76_O_t1b_t1b; // @[Top.scala 222:7]
  assign n47_valid_up = valid_up; // @[Top.scala 135:18]
  assign n47_I_t0b = I_t0b; // @[Top.scala 134:11]
  assign n75_clock = clock;
  assign n75_reset = reset;
  assign n75_valid_up = n47_valid_down; // @[Top.scala 138:18]
  assign n75_I = n47_O; // @[Top.scala 137:11]
  assign n63_clock = clock;
  assign n63_reset = reset;
  assign n63_valid_up = n47_valid_down; // @[Top.scala 141:18]
  assign n63_I = n47_O; // @[Top.scala 140:11]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n49_valid_up = valid_up; // @[Top.scala 145:18]
  assign n49_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 144:11]
  assign n49_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 144:11]
  assign n50_valid_up = n49_valid_down; // @[Top.scala 148:18]
  assign n50_I_t0b = n49_O_t0b; // @[Top.scala 147:11]
  assign n51_valid_up = n49_valid_down; // @[Top.scala 151:18]
  assign n51_I_t1b = n49_O_t1b; // @[Top.scala 150:11]
  assign n52_valid_up = n50_valid_down & n51_valid_down; // @[Top.scala 155:18]
  assign n52_I0 = n50_O; // @[Top.scala 153:12]
  assign n52_I1 = n51_O; // @[Top.scala 154:12]
  assign n53_valid_up = n52_valid_down; // @[Top.scala 158:18]
  assign n53_I_t0b = n52_O_t0b; // @[Top.scala 157:11]
  assign n53_I_t1b = n52_O_t1b; // @[Top.scala 157:11]
  assign n55_valid_up = InitialDelayCounter_valid_down & n53_valid_down; // @[Top.scala 162:18]
  assign n55_I0 = 16'h1; // @[Top.scala 160:12]
  assign n55_I1 = n53_O; // @[Top.scala 161:12]
  assign n56_valid_up = n55_valid_down; // @[Top.scala 165:18]
  assign n56_I_t0b = n55_O_t0b; // @[Top.scala 164:11]
  assign n56_I_t1b = n55_O_t1b; // @[Top.scala 164:11]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n59_valid_up = n56_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 170:18]
  assign n59_I0 = n56_O; // @[Top.scala 168:12]
  assign n60_valid_up = n59_valid_down; // @[Top.scala 173:18]
  assign n60_I_t0b = n59_O_t0b; // @[Top.scala 172:11]
  assign n61_valid_up = n60_valid_down; // @[Top.scala 177:18]
  assign n61_I0 = n60_O; // @[Top.scala 175:12]
  assign n61_I1 = n60_O; // @[Top.scala 176:12]
  assign n62_clock = clock;
  assign n62_reset = reset;
  assign n62_valid_up = n61_valid_down; // @[Top.scala 180:18]
  assign n62_I_t0b = n61_O_t0b; // @[Top.scala 179:11]
  assign n62_I_t1b = n61_O_t1b; // @[Top.scala 179:11]
  assign n64_valid_up = n63_valid_down & n62_valid_down; // @[Top.scala 184:18]
  assign n64_I0 = n63_O; // @[Top.scala 182:12]
  assign n64_I1 = n62_O; // @[Top.scala 183:12]
  assign n65_valid_up = n64_valid_down; // @[Top.scala 187:18]
  assign n65_I_t0b = n64_O_t0b; // @[Top.scala 186:11]
  assign n65_I_t1b = n64_O_t1b; // @[Top.scala 186:11]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n67_valid_up = n60_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 192:18]
  assign n67_I0 = n60_O; // @[Top.scala 190:12]
  assign n67_I1 = 16'h1; // @[Top.scala 191:12]
  assign n68_valid_up = n67_valid_down; // @[Top.scala 195:18]
  assign n68_I_t0b = n67_O_t0b; // @[Top.scala 194:11]
  assign n68_I_t1b = n67_O_t1b; // @[Top.scala 194:11]
  assign n69_valid_up = n50_valid_down & n68_valid_down; // @[Top.scala 199:18]
  assign n69_I0 = n50_O; // @[Top.scala 197:12]
  assign n69_I1 = n68_O; // @[Top.scala 198:12]
  assign n70_valid_up = n60_valid_down & n51_valid_down; // @[Top.scala 203:18]
  assign n70_I0 = n60_O; // @[Top.scala 201:12]
  assign n70_I1 = n51_O; // @[Top.scala 202:12]
  assign n71_valid_up = n69_valid_down & n70_valid_down; // @[Top.scala 207:18]
  assign n71_I0_t0b = n69_O_t0b; // @[Top.scala 205:12]
  assign n71_I0_t1b = n69_O_t1b; // @[Top.scala 205:12]
  assign n71_I1_t0b = n70_O_t0b; // @[Top.scala 206:12]
  assign n71_I1_t1b = n70_O_t1b; // @[Top.scala 206:12]
  assign n72_clock = clock;
  assign n72_reset = reset;
  assign n72_valid_up = n71_valid_down; // @[Top.scala 210:18]
  assign n72_I_t0b_t0b = n71_O_t0b_t0b; // @[Top.scala 209:11]
  assign n72_I_t0b_t1b = n71_O_t0b_t1b; // @[Top.scala 209:11]
  assign n72_I_t1b_t0b = n71_O_t1b_t0b; // @[Top.scala 209:11]
  assign n72_I_t1b_t1b = n71_O_t1b_t1b; // @[Top.scala 209:11]
  assign n73_valid_up = n65_valid_down & n72_valid_down; // @[Top.scala 214:18]
  assign n73_I0 = n65_O[0]; // @[Top.scala 212:12]
  assign n73_I1_t0b_t0b = n72_O_t0b_t0b; // @[Top.scala 213:12]
  assign n73_I1_t0b_t1b = n72_O_t0b_t1b; // @[Top.scala 213:12]
  assign n73_I1_t1b_t0b = n72_O_t1b_t0b; // @[Top.scala 213:12]
  assign n73_I1_t1b_t1b = n72_O_t1b_t1b; // @[Top.scala 213:12]
  assign n74_valid_up = n73_valid_down; // @[Top.scala 217:18]
  assign n74_I_t0b = n73_O_t0b; // @[Top.scala 216:11]
  assign n74_I_t1b_t0b_t0b = n73_O_t1b_t0b_t0b; // @[Top.scala 216:11]
  assign n74_I_t1b_t0b_t1b = n73_O_t1b_t0b_t1b; // @[Top.scala 216:11]
  assign n74_I_t1b_t1b_t0b = n73_O_t1b_t1b_t0b; // @[Top.scala 216:11]
  assign n74_I_t1b_t1b_t1b = n73_O_t1b_t1b_t1b; // @[Top.scala 216:11]
  assign n76_valid_up = n75_valid_down & n74_valid_down; // @[Top.scala 221:18]
  assign n76_I0 = n75_O; // @[Top.scala 219:12]
  assign n76_I1_t0b = n74_O_t0b; // @[Top.scala 220:12]
  assign n76_I1_t1b = n74_O_t1b; // @[Top.scala 220:12]
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
  wire  n81_valid_up; // @[Top.scala 229:21]
  wire  n81_valid_down; // @[Top.scala 229:21]
  wire [15:0] n81_I_t0b; // @[Top.scala 229:21]
  wire [15:0] n81_O; // @[Top.scala 229:21]
  wire  n109_clock; // @[Top.scala 232:22]
  wire  n109_reset; // @[Top.scala 232:22]
  wire  n109_valid_up; // @[Top.scala 232:22]
  wire  n109_valid_down; // @[Top.scala 232:22]
  wire [15:0] n109_I; // @[Top.scala 232:22]
  wire [15:0] n109_O; // @[Top.scala 232:22]
  wire  n97_clock; // @[Top.scala 235:21]
  wire  n97_reset; // @[Top.scala 235:21]
  wire  n97_valid_up; // @[Top.scala 235:21]
  wire  n97_valid_down; // @[Top.scala 235:21]
  wire [15:0] n97_I; // @[Top.scala 235:21]
  wire [15:0] n97_O; // @[Top.scala 235:21]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n83_valid_up; // @[Top.scala 239:21]
  wire  n83_valid_down; // @[Top.scala 239:21]
  wire [15:0] n83_I_t1b_t0b; // @[Top.scala 239:21]
  wire [15:0] n83_I_t1b_t1b; // @[Top.scala 239:21]
  wire [15:0] n83_O_t0b; // @[Top.scala 239:21]
  wire [15:0] n83_O_t1b; // @[Top.scala 239:21]
  wire  n84_valid_up; // @[Top.scala 242:21]
  wire  n84_valid_down; // @[Top.scala 242:21]
  wire [15:0] n84_I_t0b; // @[Top.scala 242:21]
  wire [15:0] n84_O; // @[Top.scala 242:21]
  wire  n85_valid_up; // @[Top.scala 245:21]
  wire  n85_valid_down; // @[Top.scala 245:21]
  wire [15:0] n85_I_t1b; // @[Top.scala 245:21]
  wire [15:0] n85_O; // @[Top.scala 245:21]
  wire  n86_valid_up; // @[Top.scala 248:21]
  wire  n86_valid_down; // @[Top.scala 248:21]
  wire [15:0] n86_I0; // @[Top.scala 248:21]
  wire [15:0] n86_I1; // @[Top.scala 248:21]
  wire [15:0] n86_O_t0b; // @[Top.scala 248:21]
  wire [15:0] n86_O_t1b; // @[Top.scala 248:21]
  wire  n87_valid_up; // @[Top.scala 252:21]
  wire  n87_valid_down; // @[Top.scala 252:21]
  wire [15:0] n87_I_t0b; // @[Top.scala 252:21]
  wire [15:0] n87_I_t1b; // @[Top.scala 252:21]
  wire [15:0] n87_O; // @[Top.scala 252:21]
  wire  n89_valid_up; // @[Top.scala 255:21]
  wire  n89_valid_down; // @[Top.scala 255:21]
  wire [15:0] n89_I0; // @[Top.scala 255:21]
  wire [15:0] n89_I1; // @[Top.scala 255:21]
  wire [15:0] n89_O_t0b; // @[Top.scala 255:21]
  wire [15:0] n89_O_t1b; // @[Top.scala 255:21]
  wire  n90_valid_up; // @[Top.scala 259:21]
  wire  n90_valid_down; // @[Top.scala 259:21]
  wire [15:0] n90_I_t0b; // @[Top.scala 259:21]
  wire [15:0] n90_I_t1b; // @[Top.scala 259:21]
  wire [15:0] n90_O; // @[Top.scala 259:21]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n93_valid_up; // @[Top.scala 263:21]
  wire  n93_valid_down; // @[Top.scala 263:21]
  wire [15:0] n93_I0; // @[Top.scala 263:21]
  wire [15:0] n93_O_t0b; // @[Top.scala 263:21]
  wire  n94_valid_up; // @[Top.scala 267:21]
  wire  n94_valid_down; // @[Top.scala 267:21]
  wire [15:0] n94_I_t0b; // @[Top.scala 267:21]
  wire [15:0] n94_O; // @[Top.scala 267:21]
  wire  n95_valid_up; // @[Top.scala 270:21]
  wire  n95_valid_down; // @[Top.scala 270:21]
  wire [15:0] n95_I0; // @[Top.scala 270:21]
  wire [15:0] n95_I1; // @[Top.scala 270:21]
  wire [15:0] n95_O_t0b; // @[Top.scala 270:21]
  wire [15:0] n95_O_t1b; // @[Top.scala 270:21]
  wire  n96_clock; // @[Top.scala 274:21]
  wire  n96_reset; // @[Top.scala 274:21]
  wire  n96_valid_up; // @[Top.scala 274:21]
  wire  n96_valid_down; // @[Top.scala 274:21]
  wire [15:0] n96_I_t0b; // @[Top.scala 274:21]
  wire [15:0] n96_I_t1b; // @[Top.scala 274:21]
  wire [15:0] n96_O; // @[Top.scala 274:21]
  wire  n98_valid_up; // @[Top.scala 277:21]
  wire  n98_valid_down; // @[Top.scala 277:21]
  wire [15:0] n98_I0; // @[Top.scala 277:21]
  wire [15:0] n98_I1; // @[Top.scala 277:21]
  wire [15:0] n98_O_t0b; // @[Top.scala 277:21]
  wire [15:0] n98_O_t1b; // @[Top.scala 277:21]
  wire  n99_valid_up; // @[Top.scala 281:21]
  wire  n99_valid_down; // @[Top.scala 281:21]
  wire [15:0] n99_I_t0b; // @[Top.scala 281:21]
  wire [15:0] n99_I_t1b; // @[Top.scala 281:21]
  wire [15:0] n99_O; // @[Top.scala 281:21]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n101_valid_up; // @[Top.scala 285:22]
  wire  n101_valid_down; // @[Top.scala 285:22]
  wire [15:0] n101_I0; // @[Top.scala 285:22]
  wire [15:0] n101_I1; // @[Top.scala 285:22]
  wire [15:0] n101_O_t0b; // @[Top.scala 285:22]
  wire [15:0] n101_O_t1b; // @[Top.scala 285:22]
  wire  n102_valid_up; // @[Top.scala 289:22]
  wire  n102_valid_down; // @[Top.scala 289:22]
  wire [15:0] n102_I_t0b; // @[Top.scala 289:22]
  wire [15:0] n102_I_t1b; // @[Top.scala 289:22]
  wire [15:0] n102_O; // @[Top.scala 289:22]
  wire  n103_valid_up; // @[Top.scala 292:22]
  wire  n103_valid_down; // @[Top.scala 292:22]
  wire [15:0] n103_I0; // @[Top.scala 292:22]
  wire [15:0] n103_I1; // @[Top.scala 292:22]
  wire [15:0] n103_O_t0b; // @[Top.scala 292:22]
  wire [15:0] n103_O_t1b; // @[Top.scala 292:22]
  wire  n104_valid_up; // @[Top.scala 296:22]
  wire  n104_valid_down; // @[Top.scala 296:22]
  wire [15:0] n104_I0; // @[Top.scala 296:22]
  wire [15:0] n104_I1; // @[Top.scala 296:22]
  wire [15:0] n104_O_t0b; // @[Top.scala 296:22]
  wire [15:0] n104_O_t1b; // @[Top.scala 296:22]
  wire  n105_valid_up; // @[Top.scala 300:22]
  wire  n105_valid_down; // @[Top.scala 300:22]
  wire [15:0] n105_I0_t0b; // @[Top.scala 300:22]
  wire [15:0] n105_I0_t1b; // @[Top.scala 300:22]
  wire [15:0] n105_I1_t0b; // @[Top.scala 300:22]
  wire [15:0] n105_I1_t1b; // @[Top.scala 300:22]
  wire [15:0] n105_O_t0b_t0b; // @[Top.scala 300:22]
  wire [15:0] n105_O_t0b_t1b; // @[Top.scala 300:22]
  wire [15:0] n105_O_t1b_t0b; // @[Top.scala 300:22]
  wire [15:0] n105_O_t1b_t1b; // @[Top.scala 300:22]
  wire  n106_clock; // @[Top.scala 304:22]
  wire  n106_reset; // @[Top.scala 304:22]
  wire  n106_valid_up; // @[Top.scala 304:22]
  wire  n106_valid_down; // @[Top.scala 304:22]
  wire [15:0] n106_I_t0b_t0b; // @[Top.scala 304:22]
  wire [15:0] n106_I_t0b_t1b; // @[Top.scala 304:22]
  wire [15:0] n106_I_t1b_t0b; // @[Top.scala 304:22]
  wire [15:0] n106_I_t1b_t1b; // @[Top.scala 304:22]
  wire [15:0] n106_O_t0b_t0b; // @[Top.scala 304:22]
  wire [15:0] n106_O_t0b_t1b; // @[Top.scala 304:22]
  wire [15:0] n106_O_t1b_t0b; // @[Top.scala 304:22]
  wire [15:0] n106_O_t1b_t1b; // @[Top.scala 304:22]
  wire  n107_valid_up; // @[Top.scala 307:22]
  wire  n107_valid_down; // @[Top.scala 307:22]
  wire  n107_I0; // @[Top.scala 307:22]
  wire [15:0] n107_I1_t0b_t0b; // @[Top.scala 307:22]
  wire [15:0] n107_I1_t0b_t1b; // @[Top.scala 307:22]
  wire [15:0] n107_I1_t1b_t0b; // @[Top.scala 307:22]
  wire [15:0] n107_I1_t1b_t1b; // @[Top.scala 307:22]
  wire  n107_O_t0b; // @[Top.scala 307:22]
  wire [15:0] n107_O_t1b_t0b_t0b; // @[Top.scala 307:22]
  wire [15:0] n107_O_t1b_t0b_t1b; // @[Top.scala 307:22]
  wire [15:0] n107_O_t1b_t1b_t0b; // @[Top.scala 307:22]
  wire [15:0] n107_O_t1b_t1b_t1b; // @[Top.scala 307:22]
  wire  n108_valid_up; // @[Top.scala 311:22]
  wire  n108_valid_down; // @[Top.scala 311:22]
  wire  n108_I_t0b; // @[Top.scala 311:22]
  wire [15:0] n108_I_t1b_t0b_t0b; // @[Top.scala 311:22]
  wire [15:0] n108_I_t1b_t0b_t1b; // @[Top.scala 311:22]
  wire [15:0] n108_I_t1b_t1b_t0b; // @[Top.scala 311:22]
  wire [15:0] n108_I_t1b_t1b_t1b; // @[Top.scala 311:22]
  wire [15:0] n108_O_t0b; // @[Top.scala 311:22]
  wire [15:0] n108_O_t1b; // @[Top.scala 311:22]
  wire  n110_valid_up; // @[Top.scala 314:22]
  wire  n110_valid_down; // @[Top.scala 314:22]
  wire [15:0] n110_I0; // @[Top.scala 314:22]
  wire [15:0] n110_I1_t0b; // @[Top.scala 314:22]
  wire [15:0] n110_I1_t1b; // @[Top.scala 314:22]
  wire [15:0] n110_O_t0b; // @[Top.scala 314:22]
  wire [15:0] n110_O_t1b_t0b; // @[Top.scala 314:22]
  wire [15:0] n110_O_t1b_t1b; // @[Top.scala 314:22]
  Fst n81 ( // @[Top.scala 229:21]
    .valid_up(n81_valid_up),
    .valid_down(n81_valid_down),
    .I_t0b(n81_I_t0b),
    .O(n81_O)
  );
  FIFO_2 n109 ( // @[Top.scala 232:22]
    .clock(n109_clock),
    .reset(n109_reset),
    .valid_up(n109_valid_up),
    .valid_down(n109_valid_down),
    .I(n109_I),
    .O(n109_O)
  );
  FIFO_2 n97 ( // @[Top.scala 235:21]
    .clock(n97_clock),
    .reset(n97_reset),
    .valid_up(n97_valid_up),
    .valid_down(n97_valid_down),
    .I(n97_I),
    .O(n97_O)
  );
  InitialDelayCounter_6 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n83 ( // @[Top.scala 239:21]
    .valid_up(n83_valid_up),
    .valid_down(n83_valid_down),
    .I_t1b_t0b(n83_I_t1b_t0b),
    .I_t1b_t1b(n83_I_t1b_t1b),
    .O_t0b(n83_O_t0b),
    .O_t1b(n83_O_t1b)
  );
  Fst_1 n84 ( // @[Top.scala 242:21]
    .valid_up(n84_valid_up),
    .valid_down(n84_valid_down),
    .I_t0b(n84_I_t0b),
    .O(n84_O)
  );
  Snd_1 n85 ( // @[Top.scala 245:21]
    .valid_up(n85_valid_up),
    .valid_down(n85_valid_down),
    .I_t1b(n85_I_t1b),
    .O(n85_O)
  );
  AtomTuple n86 ( // @[Top.scala 248:21]
    .valid_up(n86_valid_up),
    .valid_down(n86_valid_down),
    .I0(n86_I0),
    .I1(n86_I1),
    .O_t0b(n86_O_t0b),
    .O_t1b(n86_O_t1b)
  );
  Add n87 ( // @[Top.scala 252:21]
    .valid_up(n87_valid_up),
    .valid_down(n87_valid_down),
    .I_t0b(n87_I_t0b),
    .I_t1b(n87_I_t1b),
    .O(n87_O)
  );
  AtomTuple n89 ( // @[Top.scala 255:21]
    .valid_up(n89_valid_up),
    .valid_down(n89_valid_down),
    .I0(n89_I0),
    .I1(n89_I1),
    .O_t0b(n89_O_t0b),
    .O_t1b(n89_O_t1b)
  );
  Add n90 ( // @[Top.scala 259:21]
    .valid_up(n90_valid_up),
    .valid_down(n90_valid_down),
    .I_t0b(n90_I_t0b),
    .I_t1b(n90_I_t1b),
    .O(n90_O)
  );
  InitialDelayCounter_6 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n93 ( // @[Top.scala 263:21]
    .valid_up(n93_valid_up),
    .valid_down(n93_valid_down),
    .I0(n93_I0),
    .O_t0b(n93_O_t0b)
  );
  RShift n94 ( // @[Top.scala 267:21]
    .valid_up(n94_valid_up),
    .valid_down(n94_valid_down),
    .I_t0b(n94_I_t0b),
    .O(n94_O)
  );
  AtomTuple n95 ( // @[Top.scala 270:21]
    .valid_up(n95_valid_up),
    .valid_down(n95_valid_down),
    .I0(n95_I0),
    .I1(n95_I1),
    .O_t0b(n95_O_t0b),
    .O_t1b(n95_O_t1b)
  );
  Mul n96 ( // @[Top.scala 274:21]
    .clock(n96_clock),
    .reset(n96_reset),
    .valid_up(n96_valid_up),
    .valid_down(n96_valid_down),
    .I_t0b(n96_I_t0b),
    .I_t1b(n96_I_t1b),
    .O(n96_O)
  );
  AtomTuple n98 ( // @[Top.scala 277:21]
    .valid_up(n98_valid_up),
    .valid_down(n98_valid_down),
    .I0(n98_I0),
    .I1(n98_I1),
    .O_t0b(n98_O_t0b),
    .O_t1b(n98_O_t1b)
  );
  Lt n99 ( // @[Top.scala 281:21]
    .valid_up(n99_valid_up),
    .valid_down(n99_valid_down),
    .I_t0b(n99_I_t0b),
    .I_t1b(n99_I_t1b),
    .O(n99_O)
  );
  InitialDelayCounter_6 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n101 ( // @[Top.scala 285:22]
    .valid_up(n101_valid_up),
    .valid_down(n101_valid_down),
    .I0(n101_I0),
    .I1(n101_I1),
    .O_t0b(n101_O_t0b),
    .O_t1b(n101_O_t1b)
  );
  Sub n102 ( // @[Top.scala 289:22]
    .valid_up(n102_valid_up),
    .valid_down(n102_valid_down),
    .I_t0b(n102_I_t0b),
    .I_t1b(n102_I_t1b),
    .O(n102_O)
  );
  AtomTuple n103 ( // @[Top.scala 292:22]
    .valid_up(n103_valid_up),
    .valid_down(n103_valid_down),
    .I0(n103_I0),
    .I1(n103_I1),
    .O_t0b(n103_O_t0b),
    .O_t1b(n103_O_t1b)
  );
  AtomTuple n104 ( // @[Top.scala 296:22]
    .valid_up(n104_valid_up),
    .valid_down(n104_valid_down),
    .I0(n104_I0),
    .I1(n104_I1),
    .O_t0b(n104_O_t0b),
    .O_t1b(n104_O_t1b)
  );
  AtomTuple_10 n105 ( // @[Top.scala 300:22]
    .valid_up(n105_valid_up),
    .valid_down(n105_valid_down),
    .I0_t0b(n105_I0_t0b),
    .I0_t1b(n105_I0_t1b),
    .I1_t0b(n105_I1_t0b),
    .I1_t1b(n105_I1_t1b),
    .O_t0b_t0b(n105_O_t0b_t0b),
    .O_t0b_t1b(n105_O_t0b_t1b),
    .O_t1b_t0b(n105_O_t1b_t0b),
    .O_t1b_t1b(n105_O_t1b_t1b)
  );
  FIFO_4 n106 ( // @[Top.scala 304:22]
    .clock(n106_clock),
    .reset(n106_reset),
    .valid_up(n106_valid_up),
    .valid_down(n106_valid_down),
    .I_t0b_t0b(n106_I_t0b_t0b),
    .I_t0b_t1b(n106_I_t0b_t1b),
    .I_t1b_t0b(n106_I_t1b_t0b),
    .I_t1b_t1b(n106_I_t1b_t1b),
    .O_t0b_t0b(n106_O_t0b_t0b),
    .O_t0b_t1b(n106_O_t0b_t1b),
    .O_t1b_t0b(n106_O_t1b_t0b),
    .O_t1b_t1b(n106_O_t1b_t1b)
  );
  AtomTuple_11 n107 ( // @[Top.scala 307:22]
    .valid_up(n107_valid_up),
    .valid_down(n107_valid_down),
    .I0(n107_I0),
    .I1_t0b_t0b(n107_I1_t0b_t0b),
    .I1_t0b_t1b(n107_I1_t0b_t1b),
    .I1_t1b_t0b(n107_I1_t1b_t0b),
    .I1_t1b_t1b(n107_I1_t1b_t1b),
    .O_t0b(n107_O_t0b),
    .O_t1b_t0b_t0b(n107_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n107_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n107_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n107_O_t1b_t1b_t1b)
  );
  If n108 ( // @[Top.scala 311:22]
    .valid_up(n108_valid_up),
    .valid_down(n108_valid_down),
    .I_t0b(n108_I_t0b),
    .I_t1b_t0b_t0b(n108_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n108_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n108_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n108_I_t1b_t1b_t1b),
    .O_t0b(n108_O_t0b),
    .O_t1b(n108_O_t1b)
  );
  AtomTuple_1 n110 ( // @[Top.scala 314:22]
    .valid_up(n110_valid_up),
    .valid_down(n110_valid_down),
    .I0(n110_I0),
    .I1_t0b(n110_I1_t0b),
    .I1_t1b(n110_I1_t1b),
    .O_t0b(n110_O_t0b),
    .O_t1b_t0b(n110_O_t1b_t0b),
    .O_t1b_t1b(n110_O_t1b_t1b)
  );
  assign valid_down = n110_valid_down; // @[Top.scala 319:16]
  assign O_t0b = n110_O_t0b; // @[Top.scala 318:7]
  assign O_t1b_t0b = n110_O_t1b_t0b; // @[Top.scala 318:7]
  assign O_t1b_t1b = n110_O_t1b_t1b; // @[Top.scala 318:7]
  assign n81_valid_up = valid_up; // @[Top.scala 231:18]
  assign n81_I_t0b = I_t0b; // @[Top.scala 230:11]
  assign n109_clock = clock;
  assign n109_reset = reset;
  assign n109_valid_up = n81_valid_down; // @[Top.scala 234:19]
  assign n109_I = n81_O; // @[Top.scala 233:12]
  assign n97_clock = clock;
  assign n97_reset = reset;
  assign n97_valid_up = n81_valid_down; // @[Top.scala 237:18]
  assign n97_I = n81_O; // @[Top.scala 236:11]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n83_valid_up = valid_up; // @[Top.scala 241:18]
  assign n83_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 240:11]
  assign n83_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 240:11]
  assign n84_valid_up = n83_valid_down; // @[Top.scala 244:18]
  assign n84_I_t0b = n83_O_t0b; // @[Top.scala 243:11]
  assign n85_valid_up = n83_valid_down; // @[Top.scala 247:18]
  assign n85_I_t1b = n83_O_t1b; // @[Top.scala 246:11]
  assign n86_valid_up = n84_valid_down & n85_valid_down; // @[Top.scala 251:18]
  assign n86_I0 = n84_O; // @[Top.scala 249:12]
  assign n86_I1 = n85_O; // @[Top.scala 250:12]
  assign n87_valid_up = n86_valid_down; // @[Top.scala 254:18]
  assign n87_I_t0b = n86_O_t0b; // @[Top.scala 253:11]
  assign n87_I_t1b = n86_O_t1b; // @[Top.scala 253:11]
  assign n89_valid_up = InitialDelayCounter_valid_down & n87_valid_down; // @[Top.scala 258:18]
  assign n89_I0 = 16'h1; // @[Top.scala 256:12]
  assign n89_I1 = n87_O; // @[Top.scala 257:12]
  assign n90_valid_up = n89_valid_down; // @[Top.scala 261:18]
  assign n90_I_t0b = n89_O_t0b; // @[Top.scala 260:11]
  assign n90_I_t1b = n89_O_t1b; // @[Top.scala 260:11]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n93_valid_up = n90_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 266:18]
  assign n93_I0 = n90_O; // @[Top.scala 264:12]
  assign n94_valid_up = n93_valid_down; // @[Top.scala 269:18]
  assign n94_I_t0b = n93_O_t0b; // @[Top.scala 268:11]
  assign n95_valid_up = n94_valid_down; // @[Top.scala 273:18]
  assign n95_I0 = n94_O; // @[Top.scala 271:12]
  assign n95_I1 = n94_O; // @[Top.scala 272:12]
  assign n96_clock = clock;
  assign n96_reset = reset;
  assign n96_valid_up = n95_valid_down; // @[Top.scala 276:18]
  assign n96_I_t0b = n95_O_t0b; // @[Top.scala 275:11]
  assign n96_I_t1b = n95_O_t1b; // @[Top.scala 275:11]
  assign n98_valid_up = n97_valid_down & n96_valid_down; // @[Top.scala 280:18]
  assign n98_I0 = n97_O; // @[Top.scala 278:12]
  assign n98_I1 = n96_O; // @[Top.scala 279:12]
  assign n99_valid_up = n98_valid_down; // @[Top.scala 283:18]
  assign n99_I_t0b = n98_O_t0b; // @[Top.scala 282:11]
  assign n99_I_t1b = n98_O_t1b; // @[Top.scala 282:11]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n101_valid_up = n94_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 288:19]
  assign n101_I0 = n94_O; // @[Top.scala 286:13]
  assign n101_I1 = 16'h1; // @[Top.scala 287:13]
  assign n102_valid_up = n101_valid_down; // @[Top.scala 291:19]
  assign n102_I_t0b = n101_O_t0b; // @[Top.scala 290:12]
  assign n102_I_t1b = n101_O_t1b; // @[Top.scala 290:12]
  assign n103_valid_up = n84_valid_down & n102_valid_down; // @[Top.scala 295:19]
  assign n103_I0 = n84_O; // @[Top.scala 293:13]
  assign n103_I1 = n102_O; // @[Top.scala 294:13]
  assign n104_valid_up = n94_valid_down & n85_valid_down; // @[Top.scala 299:19]
  assign n104_I0 = n94_O; // @[Top.scala 297:13]
  assign n104_I1 = n85_O; // @[Top.scala 298:13]
  assign n105_valid_up = n103_valid_down & n104_valid_down; // @[Top.scala 303:19]
  assign n105_I0_t0b = n103_O_t0b; // @[Top.scala 301:13]
  assign n105_I0_t1b = n103_O_t1b; // @[Top.scala 301:13]
  assign n105_I1_t0b = n104_O_t0b; // @[Top.scala 302:13]
  assign n105_I1_t1b = n104_O_t1b; // @[Top.scala 302:13]
  assign n106_clock = clock;
  assign n106_reset = reset;
  assign n106_valid_up = n105_valid_down; // @[Top.scala 306:19]
  assign n106_I_t0b_t0b = n105_O_t0b_t0b; // @[Top.scala 305:12]
  assign n106_I_t0b_t1b = n105_O_t0b_t1b; // @[Top.scala 305:12]
  assign n106_I_t1b_t0b = n105_O_t1b_t0b; // @[Top.scala 305:12]
  assign n106_I_t1b_t1b = n105_O_t1b_t1b; // @[Top.scala 305:12]
  assign n107_valid_up = n99_valid_down & n106_valid_down; // @[Top.scala 310:19]
  assign n107_I0 = n99_O[0]; // @[Top.scala 308:13]
  assign n107_I1_t0b_t0b = n106_O_t0b_t0b; // @[Top.scala 309:13]
  assign n107_I1_t0b_t1b = n106_O_t0b_t1b; // @[Top.scala 309:13]
  assign n107_I1_t1b_t0b = n106_O_t1b_t0b; // @[Top.scala 309:13]
  assign n107_I1_t1b_t1b = n106_O_t1b_t1b; // @[Top.scala 309:13]
  assign n108_valid_up = n107_valid_down; // @[Top.scala 313:19]
  assign n108_I_t0b = n107_O_t0b; // @[Top.scala 312:12]
  assign n108_I_t1b_t0b_t0b = n107_O_t1b_t0b_t0b; // @[Top.scala 312:12]
  assign n108_I_t1b_t0b_t1b = n107_O_t1b_t0b_t1b; // @[Top.scala 312:12]
  assign n108_I_t1b_t1b_t0b = n107_O_t1b_t1b_t0b; // @[Top.scala 312:12]
  assign n108_I_t1b_t1b_t1b = n107_O_t1b_t1b_t1b; // @[Top.scala 312:12]
  assign n110_valid_up = n109_valid_down & n108_valid_down; // @[Top.scala 317:19]
  assign n110_I0 = n109_O; // @[Top.scala 315:13]
  assign n110_I1_t0b = n108_O_t0b; // @[Top.scala 316:13]
  assign n110_I1_t1b = n108_O_t1b; // @[Top.scala 316:13]
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
  wire  n115_valid_up; // @[Top.scala 325:22]
  wire  n115_valid_down; // @[Top.scala 325:22]
  wire [15:0] n115_I_t0b; // @[Top.scala 325:22]
  wire [15:0] n115_O; // @[Top.scala 325:22]
  wire  n143_clock; // @[Top.scala 328:22]
  wire  n143_reset; // @[Top.scala 328:22]
  wire  n143_valid_up; // @[Top.scala 328:22]
  wire  n143_valid_down; // @[Top.scala 328:22]
  wire [15:0] n143_I; // @[Top.scala 328:22]
  wire [15:0] n143_O; // @[Top.scala 328:22]
  wire  n131_clock; // @[Top.scala 331:22]
  wire  n131_reset; // @[Top.scala 331:22]
  wire  n131_valid_up; // @[Top.scala 331:22]
  wire  n131_valid_down; // @[Top.scala 331:22]
  wire [15:0] n131_I; // @[Top.scala 331:22]
  wire [15:0] n131_O; // @[Top.scala 331:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n117_valid_up; // @[Top.scala 335:22]
  wire  n117_valid_down; // @[Top.scala 335:22]
  wire [15:0] n117_I_t1b_t0b; // @[Top.scala 335:22]
  wire [15:0] n117_I_t1b_t1b; // @[Top.scala 335:22]
  wire [15:0] n117_O_t0b; // @[Top.scala 335:22]
  wire [15:0] n117_O_t1b; // @[Top.scala 335:22]
  wire  n118_valid_up; // @[Top.scala 338:22]
  wire  n118_valid_down; // @[Top.scala 338:22]
  wire [15:0] n118_I_t0b; // @[Top.scala 338:22]
  wire [15:0] n118_O; // @[Top.scala 338:22]
  wire  n119_valid_up; // @[Top.scala 341:22]
  wire  n119_valid_down; // @[Top.scala 341:22]
  wire [15:0] n119_I_t1b; // @[Top.scala 341:22]
  wire [15:0] n119_O; // @[Top.scala 341:22]
  wire  n120_valid_up; // @[Top.scala 344:22]
  wire  n120_valid_down; // @[Top.scala 344:22]
  wire [15:0] n120_I0; // @[Top.scala 344:22]
  wire [15:0] n120_I1; // @[Top.scala 344:22]
  wire [15:0] n120_O_t0b; // @[Top.scala 344:22]
  wire [15:0] n120_O_t1b; // @[Top.scala 344:22]
  wire  n121_valid_up; // @[Top.scala 348:22]
  wire  n121_valid_down; // @[Top.scala 348:22]
  wire [15:0] n121_I_t0b; // @[Top.scala 348:22]
  wire [15:0] n121_I_t1b; // @[Top.scala 348:22]
  wire [15:0] n121_O; // @[Top.scala 348:22]
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
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n127_valid_up; // @[Top.scala 359:22]
  wire  n127_valid_down; // @[Top.scala 359:22]
  wire [15:0] n127_I0; // @[Top.scala 359:22]
  wire [15:0] n127_O_t0b; // @[Top.scala 359:22]
  wire  n128_valid_up; // @[Top.scala 363:22]
  wire  n128_valid_down; // @[Top.scala 363:22]
  wire [15:0] n128_I_t0b; // @[Top.scala 363:22]
  wire [15:0] n128_O; // @[Top.scala 363:22]
  wire  n129_valid_up; // @[Top.scala 366:22]
  wire  n129_valid_down; // @[Top.scala 366:22]
  wire [15:0] n129_I0; // @[Top.scala 366:22]
  wire [15:0] n129_I1; // @[Top.scala 366:22]
  wire [15:0] n129_O_t0b; // @[Top.scala 366:22]
  wire [15:0] n129_O_t1b; // @[Top.scala 366:22]
  wire  n130_clock; // @[Top.scala 370:22]
  wire  n130_reset; // @[Top.scala 370:22]
  wire  n130_valid_up; // @[Top.scala 370:22]
  wire  n130_valid_down; // @[Top.scala 370:22]
  wire [15:0] n130_I_t0b; // @[Top.scala 370:22]
  wire [15:0] n130_I_t1b; // @[Top.scala 370:22]
  wire [15:0] n130_O; // @[Top.scala 370:22]
  wire  n132_valid_up; // @[Top.scala 373:22]
  wire  n132_valid_down; // @[Top.scala 373:22]
  wire [15:0] n132_I0; // @[Top.scala 373:22]
  wire [15:0] n132_I1; // @[Top.scala 373:22]
  wire [15:0] n132_O_t0b; // @[Top.scala 373:22]
  wire [15:0] n132_O_t1b; // @[Top.scala 373:22]
  wire  n133_valid_up; // @[Top.scala 377:22]
  wire  n133_valid_down; // @[Top.scala 377:22]
  wire [15:0] n133_I_t0b; // @[Top.scala 377:22]
  wire [15:0] n133_I_t1b; // @[Top.scala 377:22]
  wire [15:0] n133_O; // @[Top.scala 377:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n135_valid_up; // @[Top.scala 381:22]
  wire  n135_valid_down; // @[Top.scala 381:22]
  wire [15:0] n135_I0; // @[Top.scala 381:22]
  wire [15:0] n135_I1; // @[Top.scala 381:22]
  wire [15:0] n135_O_t0b; // @[Top.scala 381:22]
  wire [15:0] n135_O_t1b; // @[Top.scala 381:22]
  wire  n136_valid_up; // @[Top.scala 385:22]
  wire  n136_valid_down; // @[Top.scala 385:22]
  wire [15:0] n136_I_t0b; // @[Top.scala 385:22]
  wire [15:0] n136_I_t1b; // @[Top.scala 385:22]
  wire [15:0] n136_O; // @[Top.scala 385:22]
  wire  n137_valid_up; // @[Top.scala 388:22]
  wire  n137_valid_down; // @[Top.scala 388:22]
  wire [15:0] n137_I0; // @[Top.scala 388:22]
  wire [15:0] n137_I1; // @[Top.scala 388:22]
  wire [15:0] n137_O_t0b; // @[Top.scala 388:22]
  wire [15:0] n137_O_t1b; // @[Top.scala 388:22]
  wire  n138_valid_up; // @[Top.scala 392:22]
  wire  n138_valid_down; // @[Top.scala 392:22]
  wire [15:0] n138_I0; // @[Top.scala 392:22]
  wire [15:0] n138_I1; // @[Top.scala 392:22]
  wire [15:0] n138_O_t0b; // @[Top.scala 392:22]
  wire [15:0] n138_O_t1b; // @[Top.scala 392:22]
  wire  n139_valid_up; // @[Top.scala 396:22]
  wire  n139_valid_down; // @[Top.scala 396:22]
  wire [15:0] n139_I0_t0b; // @[Top.scala 396:22]
  wire [15:0] n139_I0_t1b; // @[Top.scala 396:22]
  wire [15:0] n139_I1_t0b; // @[Top.scala 396:22]
  wire [15:0] n139_I1_t1b; // @[Top.scala 396:22]
  wire [15:0] n139_O_t0b_t0b; // @[Top.scala 396:22]
  wire [15:0] n139_O_t0b_t1b; // @[Top.scala 396:22]
  wire [15:0] n139_O_t1b_t0b; // @[Top.scala 396:22]
  wire [15:0] n139_O_t1b_t1b; // @[Top.scala 396:22]
  wire  n140_clock; // @[Top.scala 400:22]
  wire  n140_reset; // @[Top.scala 400:22]
  wire  n140_valid_up; // @[Top.scala 400:22]
  wire  n140_valid_down; // @[Top.scala 400:22]
  wire [15:0] n140_I_t0b_t0b; // @[Top.scala 400:22]
  wire [15:0] n140_I_t0b_t1b; // @[Top.scala 400:22]
  wire [15:0] n140_I_t1b_t0b; // @[Top.scala 400:22]
  wire [15:0] n140_I_t1b_t1b; // @[Top.scala 400:22]
  wire [15:0] n140_O_t0b_t0b; // @[Top.scala 400:22]
  wire [15:0] n140_O_t0b_t1b; // @[Top.scala 400:22]
  wire [15:0] n140_O_t1b_t0b; // @[Top.scala 400:22]
  wire [15:0] n140_O_t1b_t1b; // @[Top.scala 400:22]
  wire  n141_valid_up; // @[Top.scala 403:22]
  wire  n141_valid_down; // @[Top.scala 403:22]
  wire  n141_I0; // @[Top.scala 403:22]
  wire [15:0] n141_I1_t0b_t0b; // @[Top.scala 403:22]
  wire [15:0] n141_I1_t0b_t1b; // @[Top.scala 403:22]
  wire [15:0] n141_I1_t1b_t0b; // @[Top.scala 403:22]
  wire [15:0] n141_I1_t1b_t1b; // @[Top.scala 403:22]
  wire  n141_O_t0b; // @[Top.scala 403:22]
  wire [15:0] n141_O_t1b_t0b_t0b; // @[Top.scala 403:22]
  wire [15:0] n141_O_t1b_t0b_t1b; // @[Top.scala 403:22]
  wire [15:0] n141_O_t1b_t1b_t0b; // @[Top.scala 403:22]
  wire [15:0] n141_O_t1b_t1b_t1b; // @[Top.scala 403:22]
  wire  n142_valid_up; // @[Top.scala 407:22]
  wire  n142_valid_down; // @[Top.scala 407:22]
  wire  n142_I_t0b; // @[Top.scala 407:22]
  wire [15:0] n142_I_t1b_t0b_t0b; // @[Top.scala 407:22]
  wire [15:0] n142_I_t1b_t0b_t1b; // @[Top.scala 407:22]
  wire [15:0] n142_I_t1b_t1b_t0b; // @[Top.scala 407:22]
  wire [15:0] n142_I_t1b_t1b_t1b; // @[Top.scala 407:22]
  wire [15:0] n142_O_t0b; // @[Top.scala 407:22]
  wire [15:0] n142_O_t1b; // @[Top.scala 407:22]
  wire  n144_valid_up; // @[Top.scala 410:22]
  wire  n144_valid_down; // @[Top.scala 410:22]
  wire [15:0] n144_I0; // @[Top.scala 410:22]
  wire [15:0] n144_I1_t0b; // @[Top.scala 410:22]
  wire [15:0] n144_I1_t1b; // @[Top.scala 410:22]
  wire [15:0] n144_O_t0b; // @[Top.scala 410:22]
  wire [15:0] n144_O_t1b_t0b; // @[Top.scala 410:22]
  wire [15:0] n144_O_t1b_t1b; // @[Top.scala 410:22]
  Fst n115 ( // @[Top.scala 325:22]
    .valid_up(n115_valid_up),
    .valid_down(n115_valid_down),
    .I_t0b(n115_I_t0b),
    .O(n115_O)
  );
  FIFO_2 n143 ( // @[Top.scala 328:22]
    .clock(n143_clock),
    .reset(n143_reset),
    .valid_up(n143_valid_up),
    .valid_down(n143_valid_down),
    .I(n143_I),
    .O(n143_O)
  );
  FIFO_2 n131 ( // @[Top.scala 331:22]
    .clock(n131_clock),
    .reset(n131_reset),
    .valid_up(n131_valid_up),
    .valid_down(n131_valid_down),
    .I(n131_I),
    .O(n131_O)
  );
  InitialDelayCounter_9 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n117 ( // @[Top.scala 335:22]
    .valid_up(n117_valid_up),
    .valid_down(n117_valid_down),
    .I_t1b_t0b(n117_I_t1b_t0b),
    .I_t1b_t1b(n117_I_t1b_t1b),
    .O_t0b(n117_O_t0b),
    .O_t1b(n117_O_t1b)
  );
  Fst_1 n118 ( // @[Top.scala 338:22]
    .valid_up(n118_valid_up),
    .valid_down(n118_valid_down),
    .I_t0b(n118_I_t0b),
    .O(n118_O)
  );
  Snd_1 n119 ( // @[Top.scala 341:22]
    .valid_up(n119_valid_up),
    .valid_down(n119_valid_down),
    .I_t1b(n119_I_t1b),
    .O(n119_O)
  );
  AtomTuple n120 ( // @[Top.scala 344:22]
    .valid_up(n120_valid_up),
    .valid_down(n120_valid_down),
    .I0(n120_I0),
    .I1(n120_I1),
    .O_t0b(n120_O_t0b),
    .O_t1b(n120_O_t1b)
  );
  Add n121 ( // @[Top.scala 348:22]
    .valid_up(n121_valid_up),
    .valid_down(n121_valid_down),
    .I_t0b(n121_I_t0b),
    .I_t1b(n121_I_t1b),
    .O(n121_O)
  );
  AtomTuple n123 ( // @[Top.scala 351:22]
    .valid_up(n123_valid_up),
    .valid_down(n123_valid_down),
    .I0(n123_I0),
    .I1(n123_I1),
    .O_t0b(n123_O_t0b),
    .O_t1b(n123_O_t1b)
  );
  Add n124 ( // @[Top.scala 355:22]
    .valid_up(n124_valid_up),
    .valid_down(n124_valid_down),
    .I_t0b(n124_I_t0b),
    .I_t1b(n124_I_t1b),
    .O(n124_O)
  );
  InitialDelayCounter_9 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n127 ( // @[Top.scala 359:22]
    .valid_up(n127_valid_up),
    .valid_down(n127_valid_down),
    .I0(n127_I0),
    .O_t0b(n127_O_t0b)
  );
  RShift n128 ( // @[Top.scala 363:22]
    .valid_up(n128_valid_up),
    .valid_down(n128_valid_down),
    .I_t0b(n128_I_t0b),
    .O(n128_O)
  );
  AtomTuple n129 ( // @[Top.scala 366:22]
    .valid_up(n129_valid_up),
    .valid_down(n129_valid_down),
    .I0(n129_I0),
    .I1(n129_I1),
    .O_t0b(n129_O_t0b),
    .O_t1b(n129_O_t1b)
  );
  Mul n130 ( // @[Top.scala 370:22]
    .clock(n130_clock),
    .reset(n130_reset),
    .valid_up(n130_valid_up),
    .valid_down(n130_valid_down),
    .I_t0b(n130_I_t0b),
    .I_t1b(n130_I_t1b),
    .O(n130_O)
  );
  AtomTuple n132 ( // @[Top.scala 373:22]
    .valid_up(n132_valid_up),
    .valid_down(n132_valid_down),
    .I0(n132_I0),
    .I1(n132_I1),
    .O_t0b(n132_O_t0b),
    .O_t1b(n132_O_t1b)
  );
  Lt n133 ( // @[Top.scala 377:22]
    .valid_up(n133_valid_up),
    .valid_down(n133_valid_down),
    .I_t0b(n133_I_t0b),
    .I_t1b(n133_I_t1b),
    .O(n133_O)
  );
  InitialDelayCounter_9 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n135 ( // @[Top.scala 381:22]
    .valid_up(n135_valid_up),
    .valid_down(n135_valid_down),
    .I0(n135_I0),
    .I1(n135_I1),
    .O_t0b(n135_O_t0b),
    .O_t1b(n135_O_t1b)
  );
  Sub n136 ( // @[Top.scala 385:22]
    .valid_up(n136_valid_up),
    .valid_down(n136_valid_down),
    .I_t0b(n136_I_t0b),
    .I_t1b(n136_I_t1b),
    .O(n136_O)
  );
  AtomTuple n137 ( // @[Top.scala 388:22]
    .valid_up(n137_valid_up),
    .valid_down(n137_valid_down),
    .I0(n137_I0),
    .I1(n137_I1),
    .O_t0b(n137_O_t0b),
    .O_t1b(n137_O_t1b)
  );
  AtomTuple n138 ( // @[Top.scala 392:22]
    .valid_up(n138_valid_up),
    .valid_down(n138_valid_down),
    .I0(n138_I0),
    .I1(n138_I1),
    .O_t0b(n138_O_t0b),
    .O_t1b(n138_O_t1b)
  );
  AtomTuple_10 n139 ( // @[Top.scala 396:22]
    .valid_up(n139_valid_up),
    .valid_down(n139_valid_down),
    .I0_t0b(n139_I0_t0b),
    .I0_t1b(n139_I0_t1b),
    .I1_t0b(n139_I1_t0b),
    .I1_t1b(n139_I1_t1b),
    .O_t0b_t0b(n139_O_t0b_t0b),
    .O_t0b_t1b(n139_O_t0b_t1b),
    .O_t1b_t0b(n139_O_t1b_t0b),
    .O_t1b_t1b(n139_O_t1b_t1b)
  );
  FIFO_4 n140 ( // @[Top.scala 400:22]
    .clock(n140_clock),
    .reset(n140_reset),
    .valid_up(n140_valid_up),
    .valid_down(n140_valid_down),
    .I_t0b_t0b(n140_I_t0b_t0b),
    .I_t0b_t1b(n140_I_t0b_t1b),
    .I_t1b_t0b(n140_I_t1b_t0b),
    .I_t1b_t1b(n140_I_t1b_t1b),
    .O_t0b_t0b(n140_O_t0b_t0b),
    .O_t0b_t1b(n140_O_t0b_t1b),
    .O_t1b_t0b(n140_O_t1b_t0b),
    .O_t1b_t1b(n140_O_t1b_t1b)
  );
  AtomTuple_11 n141 ( // @[Top.scala 403:22]
    .valid_up(n141_valid_up),
    .valid_down(n141_valid_down),
    .I0(n141_I0),
    .I1_t0b_t0b(n141_I1_t0b_t0b),
    .I1_t0b_t1b(n141_I1_t0b_t1b),
    .I1_t1b_t0b(n141_I1_t1b_t0b),
    .I1_t1b_t1b(n141_I1_t1b_t1b),
    .O_t0b(n141_O_t0b),
    .O_t1b_t0b_t0b(n141_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n141_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n141_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n141_O_t1b_t1b_t1b)
  );
  If n142 ( // @[Top.scala 407:22]
    .valid_up(n142_valid_up),
    .valid_down(n142_valid_down),
    .I_t0b(n142_I_t0b),
    .I_t1b_t0b_t0b(n142_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n142_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n142_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n142_I_t1b_t1b_t1b),
    .O_t0b(n142_O_t0b),
    .O_t1b(n142_O_t1b)
  );
  AtomTuple_1 n144 ( // @[Top.scala 410:22]
    .valid_up(n144_valid_up),
    .valid_down(n144_valid_down),
    .I0(n144_I0),
    .I1_t0b(n144_I1_t0b),
    .I1_t1b(n144_I1_t1b),
    .O_t0b(n144_O_t0b),
    .O_t1b_t0b(n144_O_t1b_t0b),
    .O_t1b_t1b(n144_O_t1b_t1b)
  );
  assign valid_down = n144_valid_down; // @[Top.scala 415:16]
  assign O_t0b = n144_O_t0b; // @[Top.scala 414:7]
  assign O_t1b_t0b = n144_O_t1b_t0b; // @[Top.scala 414:7]
  assign O_t1b_t1b = n144_O_t1b_t1b; // @[Top.scala 414:7]
  assign n115_valid_up = valid_up; // @[Top.scala 327:19]
  assign n115_I_t0b = I_t0b; // @[Top.scala 326:12]
  assign n143_clock = clock;
  assign n143_reset = reset;
  assign n143_valid_up = n115_valid_down; // @[Top.scala 330:19]
  assign n143_I = n115_O; // @[Top.scala 329:12]
  assign n131_clock = clock;
  assign n131_reset = reset;
  assign n131_valid_up = n115_valid_down; // @[Top.scala 333:19]
  assign n131_I = n115_O; // @[Top.scala 332:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n117_valid_up = valid_up; // @[Top.scala 337:19]
  assign n117_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 336:12]
  assign n117_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 336:12]
  assign n118_valid_up = n117_valid_down; // @[Top.scala 340:19]
  assign n118_I_t0b = n117_O_t0b; // @[Top.scala 339:12]
  assign n119_valid_up = n117_valid_down; // @[Top.scala 343:19]
  assign n119_I_t1b = n117_O_t1b; // @[Top.scala 342:12]
  assign n120_valid_up = n118_valid_down & n119_valid_down; // @[Top.scala 347:19]
  assign n120_I0 = n118_O; // @[Top.scala 345:13]
  assign n120_I1 = n119_O; // @[Top.scala 346:13]
  assign n121_valid_up = n120_valid_down; // @[Top.scala 350:19]
  assign n121_I_t0b = n120_O_t0b; // @[Top.scala 349:12]
  assign n121_I_t1b = n120_O_t1b; // @[Top.scala 349:12]
  assign n123_valid_up = InitialDelayCounter_valid_down & n121_valid_down; // @[Top.scala 354:19]
  assign n123_I0 = 16'h1; // @[Top.scala 352:13]
  assign n123_I1 = n121_O; // @[Top.scala 353:13]
  assign n124_valid_up = n123_valid_down; // @[Top.scala 357:19]
  assign n124_I_t0b = n123_O_t0b; // @[Top.scala 356:12]
  assign n124_I_t1b = n123_O_t1b; // @[Top.scala 356:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n127_valid_up = n124_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 362:19]
  assign n127_I0 = n124_O; // @[Top.scala 360:13]
  assign n128_valid_up = n127_valid_down; // @[Top.scala 365:19]
  assign n128_I_t0b = n127_O_t0b; // @[Top.scala 364:12]
  assign n129_valid_up = n128_valid_down; // @[Top.scala 369:19]
  assign n129_I0 = n128_O; // @[Top.scala 367:13]
  assign n129_I1 = n128_O; // @[Top.scala 368:13]
  assign n130_clock = clock;
  assign n130_reset = reset;
  assign n130_valid_up = n129_valid_down; // @[Top.scala 372:19]
  assign n130_I_t0b = n129_O_t0b; // @[Top.scala 371:12]
  assign n130_I_t1b = n129_O_t1b; // @[Top.scala 371:12]
  assign n132_valid_up = n131_valid_down & n130_valid_down; // @[Top.scala 376:19]
  assign n132_I0 = n131_O; // @[Top.scala 374:13]
  assign n132_I1 = n130_O; // @[Top.scala 375:13]
  assign n133_valid_up = n132_valid_down; // @[Top.scala 379:19]
  assign n133_I_t0b = n132_O_t0b; // @[Top.scala 378:12]
  assign n133_I_t1b = n132_O_t1b; // @[Top.scala 378:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n135_valid_up = n128_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 384:19]
  assign n135_I0 = n128_O; // @[Top.scala 382:13]
  assign n135_I1 = 16'h1; // @[Top.scala 383:13]
  assign n136_valid_up = n135_valid_down; // @[Top.scala 387:19]
  assign n136_I_t0b = n135_O_t0b; // @[Top.scala 386:12]
  assign n136_I_t1b = n135_O_t1b; // @[Top.scala 386:12]
  assign n137_valid_up = n118_valid_down & n136_valid_down; // @[Top.scala 391:19]
  assign n137_I0 = n118_O; // @[Top.scala 389:13]
  assign n137_I1 = n136_O; // @[Top.scala 390:13]
  assign n138_valid_up = n128_valid_down & n119_valid_down; // @[Top.scala 395:19]
  assign n138_I0 = n128_O; // @[Top.scala 393:13]
  assign n138_I1 = n119_O; // @[Top.scala 394:13]
  assign n139_valid_up = n137_valid_down & n138_valid_down; // @[Top.scala 399:19]
  assign n139_I0_t0b = n137_O_t0b; // @[Top.scala 397:13]
  assign n139_I0_t1b = n137_O_t1b; // @[Top.scala 397:13]
  assign n139_I1_t0b = n138_O_t0b; // @[Top.scala 398:13]
  assign n139_I1_t1b = n138_O_t1b; // @[Top.scala 398:13]
  assign n140_clock = clock;
  assign n140_reset = reset;
  assign n140_valid_up = n139_valid_down; // @[Top.scala 402:19]
  assign n140_I_t0b_t0b = n139_O_t0b_t0b; // @[Top.scala 401:12]
  assign n140_I_t0b_t1b = n139_O_t0b_t1b; // @[Top.scala 401:12]
  assign n140_I_t1b_t0b = n139_O_t1b_t0b; // @[Top.scala 401:12]
  assign n140_I_t1b_t1b = n139_O_t1b_t1b; // @[Top.scala 401:12]
  assign n141_valid_up = n133_valid_down & n140_valid_down; // @[Top.scala 406:19]
  assign n141_I0 = n133_O[0]; // @[Top.scala 404:13]
  assign n141_I1_t0b_t0b = n140_O_t0b_t0b; // @[Top.scala 405:13]
  assign n141_I1_t0b_t1b = n140_O_t0b_t1b; // @[Top.scala 405:13]
  assign n141_I1_t1b_t0b = n140_O_t1b_t0b; // @[Top.scala 405:13]
  assign n141_I1_t1b_t1b = n140_O_t1b_t1b; // @[Top.scala 405:13]
  assign n142_valid_up = n141_valid_down; // @[Top.scala 409:19]
  assign n142_I_t0b = n141_O_t0b; // @[Top.scala 408:12]
  assign n142_I_t1b_t0b_t0b = n141_O_t1b_t0b_t0b; // @[Top.scala 408:12]
  assign n142_I_t1b_t0b_t1b = n141_O_t1b_t0b_t1b; // @[Top.scala 408:12]
  assign n142_I_t1b_t1b_t0b = n141_O_t1b_t1b_t0b; // @[Top.scala 408:12]
  assign n142_I_t1b_t1b_t1b = n141_O_t1b_t1b_t1b; // @[Top.scala 408:12]
  assign n144_valid_up = n143_valid_down & n142_valid_down; // @[Top.scala 413:19]
  assign n144_I0 = n143_O; // @[Top.scala 411:13]
  assign n144_I1_t0b = n142_O_t0b; // @[Top.scala 412:13]
  assign n144_I1_t1b = n142_O_t1b; // @[Top.scala 412:13]
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
  wire  n149_valid_up; // @[Top.scala 421:22]
  wire  n149_valid_down; // @[Top.scala 421:22]
  wire [15:0] n149_I_t0b; // @[Top.scala 421:22]
  wire [15:0] n149_O; // @[Top.scala 421:22]
  wire  n177_clock; // @[Top.scala 424:22]
  wire  n177_reset; // @[Top.scala 424:22]
  wire  n177_valid_up; // @[Top.scala 424:22]
  wire  n177_valid_down; // @[Top.scala 424:22]
  wire [15:0] n177_I; // @[Top.scala 424:22]
  wire [15:0] n177_O; // @[Top.scala 424:22]
  wire  n165_clock; // @[Top.scala 427:22]
  wire  n165_reset; // @[Top.scala 427:22]
  wire  n165_valid_up; // @[Top.scala 427:22]
  wire  n165_valid_down; // @[Top.scala 427:22]
  wire [15:0] n165_I; // @[Top.scala 427:22]
  wire [15:0] n165_O; // @[Top.scala 427:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n151_valid_up; // @[Top.scala 431:22]
  wire  n151_valid_down; // @[Top.scala 431:22]
  wire [15:0] n151_I_t1b_t0b; // @[Top.scala 431:22]
  wire [15:0] n151_I_t1b_t1b; // @[Top.scala 431:22]
  wire [15:0] n151_O_t0b; // @[Top.scala 431:22]
  wire [15:0] n151_O_t1b; // @[Top.scala 431:22]
  wire  n152_valid_up; // @[Top.scala 434:22]
  wire  n152_valid_down; // @[Top.scala 434:22]
  wire [15:0] n152_I_t0b; // @[Top.scala 434:22]
  wire [15:0] n152_O; // @[Top.scala 434:22]
  wire  n153_valid_up; // @[Top.scala 437:22]
  wire  n153_valid_down; // @[Top.scala 437:22]
  wire [15:0] n153_I_t1b; // @[Top.scala 437:22]
  wire [15:0] n153_O; // @[Top.scala 437:22]
  wire  n154_valid_up; // @[Top.scala 440:22]
  wire  n154_valid_down; // @[Top.scala 440:22]
  wire [15:0] n154_I0; // @[Top.scala 440:22]
  wire [15:0] n154_I1; // @[Top.scala 440:22]
  wire [15:0] n154_O_t0b; // @[Top.scala 440:22]
  wire [15:0] n154_O_t1b; // @[Top.scala 440:22]
  wire  n155_valid_up; // @[Top.scala 444:22]
  wire  n155_valid_down; // @[Top.scala 444:22]
  wire [15:0] n155_I_t0b; // @[Top.scala 444:22]
  wire [15:0] n155_I_t1b; // @[Top.scala 444:22]
  wire [15:0] n155_O; // @[Top.scala 444:22]
  wire  n157_valid_up; // @[Top.scala 447:22]
  wire  n157_valid_down; // @[Top.scala 447:22]
  wire [15:0] n157_I0; // @[Top.scala 447:22]
  wire [15:0] n157_I1; // @[Top.scala 447:22]
  wire [15:0] n157_O_t0b; // @[Top.scala 447:22]
  wire [15:0] n157_O_t1b; // @[Top.scala 447:22]
  wire  n158_valid_up; // @[Top.scala 451:22]
  wire  n158_valid_down; // @[Top.scala 451:22]
  wire [15:0] n158_I_t0b; // @[Top.scala 451:22]
  wire [15:0] n158_I_t1b; // @[Top.scala 451:22]
  wire [15:0] n158_O; // @[Top.scala 451:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n161_valid_up; // @[Top.scala 455:22]
  wire  n161_valid_down; // @[Top.scala 455:22]
  wire [15:0] n161_I0; // @[Top.scala 455:22]
  wire [15:0] n161_O_t0b; // @[Top.scala 455:22]
  wire  n162_valid_up; // @[Top.scala 459:22]
  wire  n162_valid_down; // @[Top.scala 459:22]
  wire [15:0] n162_I_t0b; // @[Top.scala 459:22]
  wire [15:0] n162_O; // @[Top.scala 459:22]
  wire  n163_valid_up; // @[Top.scala 462:22]
  wire  n163_valid_down; // @[Top.scala 462:22]
  wire [15:0] n163_I0; // @[Top.scala 462:22]
  wire [15:0] n163_I1; // @[Top.scala 462:22]
  wire [15:0] n163_O_t0b; // @[Top.scala 462:22]
  wire [15:0] n163_O_t1b; // @[Top.scala 462:22]
  wire  n164_clock; // @[Top.scala 466:22]
  wire  n164_reset; // @[Top.scala 466:22]
  wire  n164_valid_up; // @[Top.scala 466:22]
  wire  n164_valid_down; // @[Top.scala 466:22]
  wire [15:0] n164_I_t0b; // @[Top.scala 466:22]
  wire [15:0] n164_I_t1b; // @[Top.scala 466:22]
  wire [15:0] n164_O; // @[Top.scala 466:22]
  wire  n166_valid_up; // @[Top.scala 469:22]
  wire  n166_valid_down; // @[Top.scala 469:22]
  wire [15:0] n166_I0; // @[Top.scala 469:22]
  wire [15:0] n166_I1; // @[Top.scala 469:22]
  wire [15:0] n166_O_t0b; // @[Top.scala 469:22]
  wire [15:0] n166_O_t1b; // @[Top.scala 469:22]
  wire  n167_valid_up; // @[Top.scala 473:22]
  wire  n167_valid_down; // @[Top.scala 473:22]
  wire [15:0] n167_I_t0b; // @[Top.scala 473:22]
  wire [15:0] n167_I_t1b; // @[Top.scala 473:22]
  wire [15:0] n167_O; // @[Top.scala 473:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n169_valid_up; // @[Top.scala 477:22]
  wire  n169_valid_down; // @[Top.scala 477:22]
  wire [15:0] n169_I0; // @[Top.scala 477:22]
  wire [15:0] n169_I1; // @[Top.scala 477:22]
  wire [15:0] n169_O_t0b; // @[Top.scala 477:22]
  wire [15:0] n169_O_t1b; // @[Top.scala 477:22]
  wire  n170_valid_up; // @[Top.scala 481:22]
  wire  n170_valid_down; // @[Top.scala 481:22]
  wire [15:0] n170_I_t0b; // @[Top.scala 481:22]
  wire [15:0] n170_I_t1b; // @[Top.scala 481:22]
  wire [15:0] n170_O; // @[Top.scala 481:22]
  wire  n171_valid_up; // @[Top.scala 484:22]
  wire  n171_valid_down; // @[Top.scala 484:22]
  wire [15:0] n171_I0; // @[Top.scala 484:22]
  wire [15:0] n171_I1; // @[Top.scala 484:22]
  wire [15:0] n171_O_t0b; // @[Top.scala 484:22]
  wire [15:0] n171_O_t1b; // @[Top.scala 484:22]
  wire  n172_valid_up; // @[Top.scala 488:22]
  wire  n172_valid_down; // @[Top.scala 488:22]
  wire [15:0] n172_I0; // @[Top.scala 488:22]
  wire [15:0] n172_I1; // @[Top.scala 488:22]
  wire [15:0] n172_O_t0b; // @[Top.scala 488:22]
  wire [15:0] n172_O_t1b; // @[Top.scala 488:22]
  wire  n173_valid_up; // @[Top.scala 492:22]
  wire  n173_valid_down; // @[Top.scala 492:22]
  wire [15:0] n173_I0_t0b; // @[Top.scala 492:22]
  wire [15:0] n173_I0_t1b; // @[Top.scala 492:22]
  wire [15:0] n173_I1_t0b; // @[Top.scala 492:22]
  wire [15:0] n173_I1_t1b; // @[Top.scala 492:22]
  wire [15:0] n173_O_t0b_t0b; // @[Top.scala 492:22]
  wire [15:0] n173_O_t0b_t1b; // @[Top.scala 492:22]
  wire [15:0] n173_O_t1b_t0b; // @[Top.scala 492:22]
  wire [15:0] n173_O_t1b_t1b; // @[Top.scala 492:22]
  wire  n174_clock; // @[Top.scala 496:22]
  wire  n174_reset; // @[Top.scala 496:22]
  wire  n174_valid_up; // @[Top.scala 496:22]
  wire  n174_valid_down; // @[Top.scala 496:22]
  wire [15:0] n174_I_t0b_t0b; // @[Top.scala 496:22]
  wire [15:0] n174_I_t0b_t1b; // @[Top.scala 496:22]
  wire [15:0] n174_I_t1b_t0b; // @[Top.scala 496:22]
  wire [15:0] n174_I_t1b_t1b; // @[Top.scala 496:22]
  wire [15:0] n174_O_t0b_t0b; // @[Top.scala 496:22]
  wire [15:0] n174_O_t0b_t1b; // @[Top.scala 496:22]
  wire [15:0] n174_O_t1b_t0b; // @[Top.scala 496:22]
  wire [15:0] n174_O_t1b_t1b; // @[Top.scala 496:22]
  wire  n175_valid_up; // @[Top.scala 499:22]
  wire  n175_valid_down; // @[Top.scala 499:22]
  wire  n175_I0; // @[Top.scala 499:22]
  wire [15:0] n175_I1_t0b_t0b; // @[Top.scala 499:22]
  wire [15:0] n175_I1_t0b_t1b; // @[Top.scala 499:22]
  wire [15:0] n175_I1_t1b_t0b; // @[Top.scala 499:22]
  wire [15:0] n175_I1_t1b_t1b; // @[Top.scala 499:22]
  wire  n175_O_t0b; // @[Top.scala 499:22]
  wire [15:0] n175_O_t1b_t0b_t0b; // @[Top.scala 499:22]
  wire [15:0] n175_O_t1b_t0b_t1b; // @[Top.scala 499:22]
  wire [15:0] n175_O_t1b_t1b_t0b; // @[Top.scala 499:22]
  wire [15:0] n175_O_t1b_t1b_t1b; // @[Top.scala 499:22]
  wire  n176_valid_up; // @[Top.scala 503:22]
  wire  n176_valid_down; // @[Top.scala 503:22]
  wire  n176_I_t0b; // @[Top.scala 503:22]
  wire [15:0] n176_I_t1b_t0b_t0b; // @[Top.scala 503:22]
  wire [15:0] n176_I_t1b_t0b_t1b; // @[Top.scala 503:22]
  wire [15:0] n176_I_t1b_t1b_t0b; // @[Top.scala 503:22]
  wire [15:0] n176_I_t1b_t1b_t1b; // @[Top.scala 503:22]
  wire [15:0] n176_O_t0b; // @[Top.scala 503:22]
  wire [15:0] n176_O_t1b; // @[Top.scala 503:22]
  wire  n178_valid_up; // @[Top.scala 506:22]
  wire  n178_valid_down; // @[Top.scala 506:22]
  wire [15:0] n178_I0; // @[Top.scala 506:22]
  wire [15:0] n178_I1_t0b; // @[Top.scala 506:22]
  wire [15:0] n178_I1_t1b; // @[Top.scala 506:22]
  wire [15:0] n178_O_t0b; // @[Top.scala 506:22]
  wire [15:0] n178_O_t1b_t0b; // @[Top.scala 506:22]
  wire [15:0] n178_O_t1b_t1b; // @[Top.scala 506:22]
  Fst n149 ( // @[Top.scala 421:22]
    .valid_up(n149_valid_up),
    .valid_down(n149_valid_down),
    .I_t0b(n149_I_t0b),
    .O(n149_O)
  );
  FIFO_2 n177 ( // @[Top.scala 424:22]
    .clock(n177_clock),
    .reset(n177_reset),
    .valid_up(n177_valid_up),
    .valid_down(n177_valid_down),
    .I(n177_I),
    .O(n177_O)
  );
  FIFO_2 n165 ( // @[Top.scala 427:22]
    .clock(n165_clock),
    .reset(n165_reset),
    .valid_up(n165_valid_up),
    .valid_down(n165_valid_down),
    .I(n165_I),
    .O(n165_O)
  );
  InitialDelayCounter_12 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n151 ( // @[Top.scala 431:22]
    .valid_up(n151_valid_up),
    .valid_down(n151_valid_down),
    .I_t1b_t0b(n151_I_t1b_t0b),
    .I_t1b_t1b(n151_I_t1b_t1b),
    .O_t0b(n151_O_t0b),
    .O_t1b(n151_O_t1b)
  );
  Fst_1 n152 ( // @[Top.scala 434:22]
    .valid_up(n152_valid_up),
    .valid_down(n152_valid_down),
    .I_t0b(n152_I_t0b),
    .O(n152_O)
  );
  Snd_1 n153 ( // @[Top.scala 437:22]
    .valid_up(n153_valid_up),
    .valid_down(n153_valid_down),
    .I_t1b(n153_I_t1b),
    .O(n153_O)
  );
  AtomTuple n154 ( // @[Top.scala 440:22]
    .valid_up(n154_valid_up),
    .valid_down(n154_valid_down),
    .I0(n154_I0),
    .I1(n154_I1),
    .O_t0b(n154_O_t0b),
    .O_t1b(n154_O_t1b)
  );
  Add n155 ( // @[Top.scala 444:22]
    .valid_up(n155_valid_up),
    .valid_down(n155_valid_down),
    .I_t0b(n155_I_t0b),
    .I_t1b(n155_I_t1b),
    .O(n155_O)
  );
  AtomTuple n157 ( // @[Top.scala 447:22]
    .valid_up(n157_valid_up),
    .valid_down(n157_valid_down),
    .I0(n157_I0),
    .I1(n157_I1),
    .O_t0b(n157_O_t0b),
    .O_t1b(n157_O_t1b)
  );
  Add n158 ( // @[Top.scala 451:22]
    .valid_up(n158_valid_up),
    .valid_down(n158_valid_down),
    .I_t0b(n158_I_t0b),
    .I_t1b(n158_I_t1b),
    .O(n158_O)
  );
  InitialDelayCounter_12 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n161 ( // @[Top.scala 455:22]
    .valid_up(n161_valid_up),
    .valid_down(n161_valid_down),
    .I0(n161_I0),
    .O_t0b(n161_O_t0b)
  );
  RShift n162 ( // @[Top.scala 459:22]
    .valid_up(n162_valid_up),
    .valid_down(n162_valid_down),
    .I_t0b(n162_I_t0b),
    .O(n162_O)
  );
  AtomTuple n163 ( // @[Top.scala 462:22]
    .valid_up(n163_valid_up),
    .valid_down(n163_valid_down),
    .I0(n163_I0),
    .I1(n163_I1),
    .O_t0b(n163_O_t0b),
    .O_t1b(n163_O_t1b)
  );
  Mul n164 ( // @[Top.scala 466:22]
    .clock(n164_clock),
    .reset(n164_reset),
    .valid_up(n164_valid_up),
    .valid_down(n164_valid_down),
    .I_t0b(n164_I_t0b),
    .I_t1b(n164_I_t1b),
    .O(n164_O)
  );
  AtomTuple n166 ( // @[Top.scala 469:22]
    .valid_up(n166_valid_up),
    .valid_down(n166_valid_down),
    .I0(n166_I0),
    .I1(n166_I1),
    .O_t0b(n166_O_t0b),
    .O_t1b(n166_O_t1b)
  );
  Lt n167 ( // @[Top.scala 473:22]
    .valid_up(n167_valid_up),
    .valid_down(n167_valid_down),
    .I_t0b(n167_I_t0b),
    .I_t1b(n167_I_t1b),
    .O(n167_O)
  );
  InitialDelayCounter_12 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n169 ( // @[Top.scala 477:22]
    .valid_up(n169_valid_up),
    .valid_down(n169_valid_down),
    .I0(n169_I0),
    .I1(n169_I1),
    .O_t0b(n169_O_t0b),
    .O_t1b(n169_O_t1b)
  );
  Sub n170 ( // @[Top.scala 481:22]
    .valid_up(n170_valid_up),
    .valid_down(n170_valid_down),
    .I_t0b(n170_I_t0b),
    .I_t1b(n170_I_t1b),
    .O(n170_O)
  );
  AtomTuple n171 ( // @[Top.scala 484:22]
    .valid_up(n171_valid_up),
    .valid_down(n171_valid_down),
    .I0(n171_I0),
    .I1(n171_I1),
    .O_t0b(n171_O_t0b),
    .O_t1b(n171_O_t1b)
  );
  AtomTuple n172 ( // @[Top.scala 488:22]
    .valid_up(n172_valid_up),
    .valid_down(n172_valid_down),
    .I0(n172_I0),
    .I1(n172_I1),
    .O_t0b(n172_O_t0b),
    .O_t1b(n172_O_t1b)
  );
  AtomTuple_10 n173 ( // @[Top.scala 492:22]
    .valid_up(n173_valid_up),
    .valid_down(n173_valid_down),
    .I0_t0b(n173_I0_t0b),
    .I0_t1b(n173_I0_t1b),
    .I1_t0b(n173_I1_t0b),
    .I1_t1b(n173_I1_t1b),
    .O_t0b_t0b(n173_O_t0b_t0b),
    .O_t0b_t1b(n173_O_t0b_t1b),
    .O_t1b_t0b(n173_O_t1b_t0b),
    .O_t1b_t1b(n173_O_t1b_t1b)
  );
  FIFO_4 n174 ( // @[Top.scala 496:22]
    .clock(n174_clock),
    .reset(n174_reset),
    .valid_up(n174_valid_up),
    .valid_down(n174_valid_down),
    .I_t0b_t0b(n174_I_t0b_t0b),
    .I_t0b_t1b(n174_I_t0b_t1b),
    .I_t1b_t0b(n174_I_t1b_t0b),
    .I_t1b_t1b(n174_I_t1b_t1b),
    .O_t0b_t0b(n174_O_t0b_t0b),
    .O_t0b_t1b(n174_O_t0b_t1b),
    .O_t1b_t0b(n174_O_t1b_t0b),
    .O_t1b_t1b(n174_O_t1b_t1b)
  );
  AtomTuple_11 n175 ( // @[Top.scala 499:22]
    .valid_up(n175_valid_up),
    .valid_down(n175_valid_down),
    .I0(n175_I0),
    .I1_t0b_t0b(n175_I1_t0b_t0b),
    .I1_t0b_t1b(n175_I1_t0b_t1b),
    .I1_t1b_t0b(n175_I1_t1b_t0b),
    .I1_t1b_t1b(n175_I1_t1b_t1b),
    .O_t0b(n175_O_t0b),
    .O_t1b_t0b_t0b(n175_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n175_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n175_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n175_O_t1b_t1b_t1b)
  );
  If n176 ( // @[Top.scala 503:22]
    .valid_up(n176_valid_up),
    .valid_down(n176_valid_down),
    .I_t0b(n176_I_t0b),
    .I_t1b_t0b_t0b(n176_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n176_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n176_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n176_I_t1b_t1b_t1b),
    .O_t0b(n176_O_t0b),
    .O_t1b(n176_O_t1b)
  );
  AtomTuple_1 n178 ( // @[Top.scala 506:22]
    .valid_up(n178_valid_up),
    .valid_down(n178_valid_down),
    .I0(n178_I0),
    .I1_t0b(n178_I1_t0b),
    .I1_t1b(n178_I1_t1b),
    .O_t0b(n178_O_t0b),
    .O_t1b_t0b(n178_O_t1b_t0b),
    .O_t1b_t1b(n178_O_t1b_t1b)
  );
  assign valid_down = n178_valid_down; // @[Top.scala 511:16]
  assign O_t0b = n178_O_t0b; // @[Top.scala 510:7]
  assign O_t1b_t0b = n178_O_t1b_t0b; // @[Top.scala 510:7]
  assign O_t1b_t1b = n178_O_t1b_t1b; // @[Top.scala 510:7]
  assign n149_valid_up = valid_up; // @[Top.scala 423:19]
  assign n149_I_t0b = I_t0b; // @[Top.scala 422:12]
  assign n177_clock = clock;
  assign n177_reset = reset;
  assign n177_valid_up = n149_valid_down; // @[Top.scala 426:19]
  assign n177_I = n149_O; // @[Top.scala 425:12]
  assign n165_clock = clock;
  assign n165_reset = reset;
  assign n165_valid_up = n149_valid_down; // @[Top.scala 429:19]
  assign n165_I = n149_O; // @[Top.scala 428:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n151_valid_up = valid_up; // @[Top.scala 433:19]
  assign n151_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 432:12]
  assign n151_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 432:12]
  assign n152_valid_up = n151_valid_down; // @[Top.scala 436:19]
  assign n152_I_t0b = n151_O_t0b; // @[Top.scala 435:12]
  assign n153_valid_up = n151_valid_down; // @[Top.scala 439:19]
  assign n153_I_t1b = n151_O_t1b; // @[Top.scala 438:12]
  assign n154_valid_up = n152_valid_down & n153_valid_down; // @[Top.scala 443:19]
  assign n154_I0 = n152_O; // @[Top.scala 441:13]
  assign n154_I1 = n153_O; // @[Top.scala 442:13]
  assign n155_valid_up = n154_valid_down; // @[Top.scala 446:19]
  assign n155_I_t0b = n154_O_t0b; // @[Top.scala 445:12]
  assign n155_I_t1b = n154_O_t1b; // @[Top.scala 445:12]
  assign n157_valid_up = InitialDelayCounter_valid_down & n155_valid_down; // @[Top.scala 450:19]
  assign n157_I0 = 16'h1; // @[Top.scala 448:13]
  assign n157_I1 = n155_O; // @[Top.scala 449:13]
  assign n158_valid_up = n157_valid_down; // @[Top.scala 453:19]
  assign n158_I_t0b = n157_O_t0b; // @[Top.scala 452:12]
  assign n158_I_t1b = n157_O_t1b; // @[Top.scala 452:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n161_valid_up = n158_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 458:19]
  assign n161_I0 = n158_O; // @[Top.scala 456:13]
  assign n162_valid_up = n161_valid_down; // @[Top.scala 461:19]
  assign n162_I_t0b = n161_O_t0b; // @[Top.scala 460:12]
  assign n163_valid_up = n162_valid_down; // @[Top.scala 465:19]
  assign n163_I0 = n162_O; // @[Top.scala 463:13]
  assign n163_I1 = n162_O; // @[Top.scala 464:13]
  assign n164_clock = clock;
  assign n164_reset = reset;
  assign n164_valid_up = n163_valid_down; // @[Top.scala 468:19]
  assign n164_I_t0b = n163_O_t0b; // @[Top.scala 467:12]
  assign n164_I_t1b = n163_O_t1b; // @[Top.scala 467:12]
  assign n166_valid_up = n165_valid_down & n164_valid_down; // @[Top.scala 472:19]
  assign n166_I0 = n165_O; // @[Top.scala 470:13]
  assign n166_I1 = n164_O; // @[Top.scala 471:13]
  assign n167_valid_up = n166_valid_down; // @[Top.scala 475:19]
  assign n167_I_t0b = n166_O_t0b; // @[Top.scala 474:12]
  assign n167_I_t1b = n166_O_t1b; // @[Top.scala 474:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n169_valid_up = n162_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 480:19]
  assign n169_I0 = n162_O; // @[Top.scala 478:13]
  assign n169_I1 = 16'h1; // @[Top.scala 479:13]
  assign n170_valid_up = n169_valid_down; // @[Top.scala 483:19]
  assign n170_I_t0b = n169_O_t0b; // @[Top.scala 482:12]
  assign n170_I_t1b = n169_O_t1b; // @[Top.scala 482:12]
  assign n171_valid_up = n152_valid_down & n170_valid_down; // @[Top.scala 487:19]
  assign n171_I0 = n152_O; // @[Top.scala 485:13]
  assign n171_I1 = n170_O; // @[Top.scala 486:13]
  assign n172_valid_up = n162_valid_down & n153_valid_down; // @[Top.scala 491:19]
  assign n172_I0 = n162_O; // @[Top.scala 489:13]
  assign n172_I1 = n153_O; // @[Top.scala 490:13]
  assign n173_valid_up = n171_valid_down & n172_valid_down; // @[Top.scala 495:19]
  assign n173_I0_t0b = n171_O_t0b; // @[Top.scala 493:13]
  assign n173_I0_t1b = n171_O_t1b; // @[Top.scala 493:13]
  assign n173_I1_t0b = n172_O_t0b; // @[Top.scala 494:13]
  assign n173_I1_t1b = n172_O_t1b; // @[Top.scala 494:13]
  assign n174_clock = clock;
  assign n174_reset = reset;
  assign n174_valid_up = n173_valid_down; // @[Top.scala 498:19]
  assign n174_I_t0b_t0b = n173_O_t0b_t0b; // @[Top.scala 497:12]
  assign n174_I_t0b_t1b = n173_O_t0b_t1b; // @[Top.scala 497:12]
  assign n174_I_t1b_t0b = n173_O_t1b_t0b; // @[Top.scala 497:12]
  assign n174_I_t1b_t1b = n173_O_t1b_t1b; // @[Top.scala 497:12]
  assign n175_valid_up = n167_valid_down & n174_valid_down; // @[Top.scala 502:19]
  assign n175_I0 = n167_O[0]; // @[Top.scala 500:13]
  assign n175_I1_t0b_t0b = n174_O_t0b_t0b; // @[Top.scala 501:13]
  assign n175_I1_t0b_t1b = n174_O_t0b_t1b; // @[Top.scala 501:13]
  assign n175_I1_t1b_t0b = n174_O_t1b_t0b; // @[Top.scala 501:13]
  assign n175_I1_t1b_t1b = n174_O_t1b_t1b; // @[Top.scala 501:13]
  assign n176_valid_up = n175_valid_down; // @[Top.scala 505:19]
  assign n176_I_t0b = n175_O_t0b; // @[Top.scala 504:12]
  assign n176_I_t1b_t0b_t0b = n175_O_t1b_t0b_t0b; // @[Top.scala 504:12]
  assign n176_I_t1b_t0b_t1b = n175_O_t1b_t0b_t1b; // @[Top.scala 504:12]
  assign n176_I_t1b_t1b_t0b = n175_O_t1b_t1b_t0b; // @[Top.scala 504:12]
  assign n176_I_t1b_t1b_t1b = n175_O_t1b_t1b_t1b; // @[Top.scala 504:12]
  assign n178_valid_up = n177_valid_down & n176_valid_down; // @[Top.scala 509:19]
  assign n178_I0 = n177_O; // @[Top.scala 507:13]
  assign n178_I1_t0b = n176_O_t0b; // @[Top.scala 508:13]
  assign n178_I1_t1b = n176_O_t1b; // @[Top.scala 508:13]
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
  wire  n183_valid_up; // @[Top.scala 517:22]
  wire  n183_valid_down; // @[Top.scala 517:22]
  wire [15:0] n183_I_t0b; // @[Top.scala 517:22]
  wire [15:0] n183_O; // @[Top.scala 517:22]
  wire  n211_clock; // @[Top.scala 520:22]
  wire  n211_reset; // @[Top.scala 520:22]
  wire  n211_valid_up; // @[Top.scala 520:22]
  wire  n211_valid_down; // @[Top.scala 520:22]
  wire [15:0] n211_I; // @[Top.scala 520:22]
  wire [15:0] n211_O; // @[Top.scala 520:22]
  wire  n199_clock; // @[Top.scala 523:22]
  wire  n199_reset; // @[Top.scala 523:22]
  wire  n199_valid_up; // @[Top.scala 523:22]
  wire  n199_valid_down; // @[Top.scala 523:22]
  wire [15:0] n199_I; // @[Top.scala 523:22]
  wire [15:0] n199_O; // @[Top.scala 523:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n185_valid_up; // @[Top.scala 527:22]
  wire  n185_valid_down; // @[Top.scala 527:22]
  wire [15:0] n185_I_t1b_t0b; // @[Top.scala 527:22]
  wire [15:0] n185_I_t1b_t1b; // @[Top.scala 527:22]
  wire [15:0] n185_O_t0b; // @[Top.scala 527:22]
  wire [15:0] n185_O_t1b; // @[Top.scala 527:22]
  wire  n186_valid_up; // @[Top.scala 530:22]
  wire  n186_valid_down; // @[Top.scala 530:22]
  wire [15:0] n186_I_t0b; // @[Top.scala 530:22]
  wire [15:0] n186_O; // @[Top.scala 530:22]
  wire  n187_valid_up; // @[Top.scala 533:22]
  wire  n187_valid_down; // @[Top.scala 533:22]
  wire [15:0] n187_I_t1b; // @[Top.scala 533:22]
  wire [15:0] n187_O; // @[Top.scala 533:22]
  wire  n188_valid_up; // @[Top.scala 536:22]
  wire  n188_valid_down; // @[Top.scala 536:22]
  wire [15:0] n188_I0; // @[Top.scala 536:22]
  wire [15:0] n188_I1; // @[Top.scala 536:22]
  wire [15:0] n188_O_t0b; // @[Top.scala 536:22]
  wire [15:0] n188_O_t1b; // @[Top.scala 536:22]
  wire  n189_valid_up; // @[Top.scala 540:22]
  wire  n189_valid_down; // @[Top.scala 540:22]
  wire [15:0] n189_I_t0b; // @[Top.scala 540:22]
  wire [15:0] n189_I_t1b; // @[Top.scala 540:22]
  wire [15:0] n189_O; // @[Top.scala 540:22]
  wire  n191_valid_up; // @[Top.scala 543:22]
  wire  n191_valid_down; // @[Top.scala 543:22]
  wire [15:0] n191_I0; // @[Top.scala 543:22]
  wire [15:0] n191_I1; // @[Top.scala 543:22]
  wire [15:0] n191_O_t0b; // @[Top.scala 543:22]
  wire [15:0] n191_O_t1b; // @[Top.scala 543:22]
  wire  n192_valid_up; // @[Top.scala 547:22]
  wire  n192_valid_down; // @[Top.scala 547:22]
  wire [15:0] n192_I_t0b; // @[Top.scala 547:22]
  wire [15:0] n192_I_t1b; // @[Top.scala 547:22]
  wire [15:0] n192_O; // @[Top.scala 547:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n195_valid_up; // @[Top.scala 551:22]
  wire  n195_valid_down; // @[Top.scala 551:22]
  wire [15:0] n195_I0; // @[Top.scala 551:22]
  wire [15:0] n195_O_t0b; // @[Top.scala 551:22]
  wire  n196_valid_up; // @[Top.scala 555:22]
  wire  n196_valid_down; // @[Top.scala 555:22]
  wire [15:0] n196_I_t0b; // @[Top.scala 555:22]
  wire [15:0] n196_O; // @[Top.scala 555:22]
  wire  n197_valid_up; // @[Top.scala 558:22]
  wire  n197_valid_down; // @[Top.scala 558:22]
  wire [15:0] n197_I0; // @[Top.scala 558:22]
  wire [15:0] n197_I1; // @[Top.scala 558:22]
  wire [15:0] n197_O_t0b; // @[Top.scala 558:22]
  wire [15:0] n197_O_t1b; // @[Top.scala 558:22]
  wire  n198_clock; // @[Top.scala 562:22]
  wire  n198_reset; // @[Top.scala 562:22]
  wire  n198_valid_up; // @[Top.scala 562:22]
  wire  n198_valid_down; // @[Top.scala 562:22]
  wire [15:0] n198_I_t0b; // @[Top.scala 562:22]
  wire [15:0] n198_I_t1b; // @[Top.scala 562:22]
  wire [15:0] n198_O; // @[Top.scala 562:22]
  wire  n200_valid_up; // @[Top.scala 565:22]
  wire  n200_valid_down; // @[Top.scala 565:22]
  wire [15:0] n200_I0; // @[Top.scala 565:22]
  wire [15:0] n200_I1; // @[Top.scala 565:22]
  wire [15:0] n200_O_t0b; // @[Top.scala 565:22]
  wire [15:0] n200_O_t1b; // @[Top.scala 565:22]
  wire  n201_valid_up; // @[Top.scala 569:22]
  wire  n201_valid_down; // @[Top.scala 569:22]
  wire [15:0] n201_I_t0b; // @[Top.scala 569:22]
  wire [15:0] n201_I_t1b; // @[Top.scala 569:22]
  wire [15:0] n201_O; // @[Top.scala 569:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n203_valid_up; // @[Top.scala 573:22]
  wire  n203_valid_down; // @[Top.scala 573:22]
  wire [15:0] n203_I0; // @[Top.scala 573:22]
  wire [15:0] n203_I1; // @[Top.scala 573:22]
  wire [15:0] n203_O_t0b; // @[Top.scala 573:22]
  wire [15:0] n203_O_t1b; // @[Top.scala 573:22]
  wire  n204_valid_up; // @[Top.scala 577:22]
  wire  n204_valid_down; // @[Top.scala 577:22]
  wire [15:0] n204_I_t0b; // @[Top.scala 577:22]
  wire [15:0] n204_I_t1b; // @[Top.scala 577:22]
  wire [15:0] n204_O; // @[Top.scala 577:22]
  wire  n205_valid_up; // @[Top.scala 580:22]
  wire  n205_valid_down; // @[Top.scala 580:22]
  wire [15:0] n205_I0; // @[Top.scala 580:22]
  wire [15:0] n205_I1; // @[Top.scala 580:22]
  wire [15:0] n205_O_t0b; // @[Top.scala 580:22]
  wire [15:0] n205_O_t1b; // @[Top.scala 580:22]
  wire  n206_valid_up; // @[Top.scala 584:22]
  wire  n206_valid_down; // @[Top.scala 584:22]
  wire [15:0] n206_I0; // @[Top.scala 584:22]
  wire [15:0] n206_I1; // @[Top.scala 584:22]
  wire [15:0] n206_O_t0b; // @[Top.scala 584:22]
  wire [15:0] n206_O_t1b; // @[Top.scala 584:22]
  wire  n207_valid_up; // @[Top.scala 588:22]
  wire  n207_valid_down; // @[Top.scala 588:22]
  wire [15:0] n207_I0_t0b; // @[Top.scala 588:22]
  wire [15:0] n207_I0_t1b; // @[Top.scala 588:22]
  wire [15:0] n207_I1_t0b; // @[Top.scala 588:22]
  wire [15:0] n207_I1_t1b; // @[Top.scala 588:22]
  wire [15:0] n207_O_t0b_t0b; // @[Top.scala 588:22]
  wire [15:0] n207_O_t0b_t1b; // @[Top.scala 588:22]
  wire [15:0] n207_O_t1b_t0b; // @[Top.scala 588:22]
  wire [15:0] n207_O_t1b_t1b; // @[Top.scala 588:22]
  wire  n208_clock; // @[Top.scala 592:22]
  wire  n208_reset; // @[Top.scala 592:22]
  wire  n208_valid_up; // @[Top.scala 592:22]
  wire  n208_valid_down; // @[Top.scala 592:22]
  wire [15:0] n208_I_t0b_t0b; // @[Top.scala 592:22]
  wire [15:0] n208_I_t0b_t1b; // @[Top.scala 592:22]
  wire [15:0] n208_I_t1b_t0b; // @[Top.scala 592:22]
  wire [15:0] n208_I_t1b_t1b; // @[Top.scala 592:22]
  wire [15:0] n208_O_t0b_t0b; // @[Top.scala 592:22]
  wire [15:0] n208_O_t0b_t1b; // @[Top.scala 592:22]
  wire [15:0] n208_O_t1b_t0b; // @[Top.scala 592:22]
  wire [15:0] n208_O_t1b_t1b; // @[Top.scala 592:22]
  wire  n209_valid_up; // @[Top.scala 595:22]
  wire  n209_valid_down; // @[Top.scala 595:22]
  wire  n209_I0; // @[Top.scala 595:22]
  wire [15:0] n209_I1_t0b_t0b; // @[Top.scala 595:22]
  wire [15:0] n209_I1_t0b_t1b; // @[Top.scala 595:22]
  wire [15:0] n209_I1_t1b_t0b; // @[Top.scala 595:22]
  wire [15:0] n209_I1_t1b_t1b; // @[Top.scala 595:22]
  wire  n209_O_t0b; // @[Top.scala 595:22]
  wire [15:0] n209_O_t1b_t0b_t0b; // @[Top.scala 595:22]
  wire [15:0] n209_O_t1b_t0b_t1b; // @[Top.scala 595:22]
  wire [15:0] n209_O_t1b_t1b_t0b; // @[Top.scala 595:22]
  wire [15:0] n209_O_t1b_t1b_t1b; // @[Top.scala 595:22]
  wire  n210_valid_up; // @[Top.scala 599:22]
  wire  n210_valid_down; // @[Top.scala 599:22]
  wire  n210_I_t0b; // @[Top.scala 599:22]
  wire [15:0] n210_I_t1b_t0b_t0b; // @[Top.scala 599:22]
  wire [15:0] n210_I_t1b_t0b_t1b; // @[Top.scala 599:22]
  wire [15:0] n210_I_t1b_t1b_t0b; // @[Top.scala 599:22]
  wire [15:0] n210_I_t1b_t1b_t1b; // @[Top.scala 599:22]
  wire [15:0] n210_O_t0b; // @[Top.scala 599:22]
  wire [15:0] n210_O_t1b; // @[Top.scala 599:22]
  wire  n212_valid_up; // @[Top.scala 602:22]
  wire  n212_valid_down; // @[Top.scala 602:22]
  wire [15:0] n212_I0; // @[Top.scala 602:22]
  wire [15:0] n212_I1_t0b; // @[Top.scala 602:22]
  wire [15:0] n212_I1_t1b; // @[Top.scala 602:22]
  wire [15:0] n212_O_t0b; // @[Top.scala 602:22]
  wire [15:0] n212_O_t1b_t0b; // @[Top.scala 602:22]
  wire [15:0] n212_O_t1b_t1b; // @[Top.scala 602:22]
  Fst n183 ( // @[Top.scala 517:22]
    .valid_up(n183_valid_up),
    .valid_down(n183_valid_down),
    .I_t0b(n183_I_t0b),
    .O(n183_O)
  );
  FIFO_2 n211 ( // @[Top.scala 520:22]
    .clock(n211_clock),
    .reset(n211_reset),
    .valid_up(n211_valid_up),
    .valid_down(n211_valid_down),
    .I(n211_I),
    .O(n211_O)
  );
  FIFO_2 n199 ( // @[Top.scala 523:22]
    .clock(n199_clock),
    .reset(n199_reset),
    .valid_up(n199_valid_up),
    .valid_down(n199_valid_down),
    .I(n199_I),
    .O(n199_O)
  );
  InitialDelayCounter_15 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n185 ( // @[Top.scala 527:22]
    .valid_up(n185_valid_up),
    .valid_down(n185_valid_down),
    .I_t1b_t0b(n185_I_t1b_t0b),
    .I_t1b_t1b(n185_I_t1b_t1b),
    .O_t0b(n185_O_t0b),
    .O_t1b(n185_O_t1b)
  );
  Fst_1 n186 ( // @[Top.scala 530:22]
    .valid_up(n186_valid_up),
    .valid_down(n186_valid_down),
    .I_t0b(n186_I_t0b),
    .O(n186_O)
  );
  Snd_1 n187 ( // @[Top.scala 533:22]
    .valid_up(n187_valid_up),
    .valid_down(n187_valid_down),
    .I_t1b(n187_I_t1b),
    .O(n187_O)
  );
  AtomTuple n188 ( // @[Top.scala 536:22]
    .valid_up(n188_valid_up),
    .valid_down(n188_valid_down),
    .I0(n188_I0),
    .I1(n188_I1),
    .O_t0b(n188_O_t0b),
    .O_t1b(n188_O_t1b)
  );
  Add n189 ( // @[Top.scala 540:22]
    .valid_up(n189_valid_up),
    .valid_down(n189_valid_down),
    .I_t0b(n189_I_t0b),
    .I_t1b(n189_I_t1b),
    .O(n189_O)
  );
  AtomTuple n191 ( // @[Top.scala 543:22]
    .valid_up(n191_valid_up),
    .valid_down(n191_valid_down),
    .I0(n191_I0),
    .I1(n191_I1),
    .O_t0b(n191_O_t0b),
    .O_t1b(n191_O_t1b)
  );
  Add n192 ( // @[Top.scala 547:22]
    .valid_up(n192_valid_up),
    .valid_down(n192_valid_down),
    .I_t0b(n192_I_t0b),
    .I_t1b(n192_I_t1b),
    .O(n192_O)
  );
  InitialDelayCounter_15 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n195 ( // @[Top.scala 551:22]
    .valid_up(n195_valid_up),
    .valid_down(n195_valid_down),
    .I0(n195_I0),
    .O_t0b(n195_O_t0b)
  );
  RShift n196 ( // @[Top.scala 555:22]
    .valid_up(n196_valid_up),
    .valid_down(n196_valid_down),
    .I_t0b(n196_I_t0b),
    .O(n196_O)
  );
  AtomTuple n197 ( // @[Top.scala 558:22]
    .valid_up(n197_valid_up),
    .valid_down(n197_valid_down),
    .I0(n197_I0),
    .I1(n197_I1),
    .O_t0b(n197_O_t0b),
    .O_t1b(n197_O_t1b)
  );
  Mul n198 ( // @[Top.scala 562:22]
    .clock(n198_clock),
    .reset(n198_reset),
    .valid_up(n198_valid_up),
    .valid_down(n198_valid_down),
    .I_t0b(n198_I_t0b),
    .I_t1b(n198_I_t1b),
    .O(n198_O)
  );
  AtomTuple n200 ( // @[Top.scala 565:22]
    .valid_up(n200_valid_up),
    .valid_down(n200_valid_down),
    .I0(n200_I0),
    .I1(n200_I1),
    .O_t0b(n200_O_t0b),
    .O_t1b(n200_O_t1b)
  );
  Lt n201 ( // @[Top.scala 569:22]
    .valid_up(n201_valid_up),
    .valid_down(n201_valid_down),
    .I_t0b(n201_I_t0b),
    .I_t1b(n201_I_t1b),
    .O(n201_O)
  );
  InitialDelayCounter_15 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n203 ( // @[Top.scala 573:22]
    .valid_up(n203_valid_up),
    .valid_down(n203_valid_down),
    .I0(n203_I0),
    .I1(n203_I1),
    .O_t0b(n203_O_t0b),
    .O_t1b(n203_O_t1b)
  );
  Sub n204 ( // @[Top.scala 577:22]
    .valid_up(n204_valid_up),
    .valid_down(n204_valid_down),
    .I_t0b(n204_I_t0b),
    .I_t1b(n204_I_t1b),
    .O(n204_O)
  );
  AtomTuple n205 ( // @[Top.scala 580:22]
    .valid_up(n205_valid_up),
    .valid_down(n205_valid_down),
    .I0(n205_I0),
    .I1(n205_I1),
    .O_t0b(n205_O_t0b),
    .O_t1b(n205_O_t1b)
  );
  AtomTuple n206 ( // @[Top.scala 584:22]
    .valid_up(n206_valid_up),
    .valid_down(n206_valid_down),
    .I0(n206_I0),
    .I1(n206_I1),
    .O_t0b(n206_O_t0b),
    .O_t1b(n206_O_t1b)
  );
  AtomTuple_10 n207 ( // @[Top.scala 588:22]
    .valid_up(n207_valid_up),
    .valid_down(n207_valid_down),
    .I0_t0b(n207_I0_t0b),
    .I0_t1b(n207_I0_t1b),
    .I1_t0b(n207_I1_t0b),
    .I1_t1b(n207_I1_t1b),
    .O_t0b_t0b(n207_O_t0b_t0b),
    .O_t0b_t1b(n207_O_t0b_t1b),
    .O_t1b_t0b(n207_O_t1b_t0b),
    .O_t1b_t1b(n207_O_t1b_t1b)
  );
  FIFO_4 n208 ( // @[Top.scala 592:22]
    .clock(n208_clock),
    .reset(n208_reset),
    .valid_up(n208_valid_up),
    .valid_down(n208_valid_down),
    .I_t0b_t0b(n208_I_t0b_t0b),
    .I_t0b_t1b(n208_I_t0b_t1b),
    .I_t1b_t0b(n208_I_t1b_t0b),
    .I_t1b_t1b(n208_I_t1b_t1b),
    .O_t0b_t0b(n208_O_t0b_t0b),
    .O_t0b_t1b(n208_O_t0b_t1b),
    .O_t1b_t0b(n208_O_t1b_t0b),
    .O_t1b_t1b(n208_O_t1b_t1b)
  );
  AtomTuple_11 n209 ( // @[Top.scala 595:22]
    .valid_up(n209_valid_up),
    .valid_down(n209_valid_down),
    .I0(n209_I0),
    .I1_t0b_t0b(n209_I1_t0b_t0b),
    .I1_t0b_t1b(n209_I1_t0b_t1b),
    .I1_t1b_t0b(n209_I1_t1b_t0b),
    .I1_t1b_t1b(n209_I1_t1b_t1b),
    .O_t0b(n209_O_t0b),
    .O_t1b_t0b_t0b(n209_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n209_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n209_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n209_O_t1b_t1b_t1b)
  );
  If n210 ( // @[Top.scala 599:22]
    .valid_up(n210_valid_up),
    .valid_down(n210_valid_down),
    .I_t0b(n210_I_t0b),
    .I_t1b_t0b_t0b(n210_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n210_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n210_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n210_I_t1b_t1b_t1b),
    .O_t0b(n210_O_t0b),
    .O_t1b(n210_O_t1b)
  );
  AtomTuple_1 n212 ( // @[Top.scala 602:22]
    .valid_up(n212_valid_up),
    .valid_down(n212_valid_down),
    .I0(n212_I0),
    .I1_t0b(n212_I1_t0b),
    .I1_t1b(n212_I1_t1b),
    .O_t0b(n212_O_t0b),
    .O_t1b_t0b(n212_O_t1b_t0b),
    .O_t1b_t1b(n212_O_t1b_t1b)
  );
  assign valid_down = n212_valid_down; // @[Top.scala 607:16]
  assign O_t0b = n212_O_t0b; // @[Top.scala 606:7]
  assign O_t1b_t0b = n212_O_t1b_t0b; // @[Top.scala 606:7]
  assign O_t1b_t1b = n212_O_t1b_t1b; // @[Top.scala 606:7]
  assign n183_valid_up = valid_up; // @[Top.scala 519:19]
  assign n183_I_t0b = I_t0b; // @[Top.scala 518:12]
  assign n211_clock = clock;
  assign n211_reset = reset;
  assign n211_valid_up = n183_valid_down; // @[Top.scala 522:19]
  assign n211_I = n183_O; // @[Top.scala 521:12]
  assign n199_clock = clock;
  assign n199_reset = reset;
  assign n199_valid_up = n183_valid_down; // @[Top.scala 525:19]
  assign n199_I = n183_O; // @[Top.scala 524:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n185_valid_up = valid_up; // @[Top.scala 529:19]
  assign n185_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 528:12]
  assign n185_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 528:12]
  assign n186_valid_up = n185_valid_down; // @[Top.scala 532:19]
  assign n186_I_t0b = n185_O_t0b; // @[Top.scala 531:12]
  assign n187_valid_up = n185_valid_down; // @[Top.scala 535:19]
  assign n187_I_t1b = n185_O_t1b; // @[Top.scala 534:12]
  assign n188_valid_up = n186_valid_down & n187_valid_down; // @[Top.scala 539:19]
  assign n188_I0 = n186_O; // @[Top.scala 537:13]
  assign n188_I1 = n187_O; // @[Top.scala 538:13]
  assign n189_valid_up = n188_valid_down; // @[Top.scala 542:19]
  assign n189_I_t0b = n188_O_t0b; // @[Top.scala 541:12]
  assign n189_I_t1b = n188_O_t1b; // @[Top.scala 541:12]
  assign n191_valid_up = InitialDelayCounter_valid_down & n189_valid_down; // @[Top.scala 546:19]
  assign n191_I0 = 16'h1; // @[Top.scala 544:13]
  assign n191_I1 = n189_O; // @[Top.scala 545:13]
  assign n192_valid_up = n191_valid_down; // @[Top.scala 549:19]
  assign n192_I_t0b = n191_O_t0b; // @[Top.scala 548:12]
  assign n192_I_t1b = n191_O_t1b; // @[Top.scala 548:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n195_valid_up = n192_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 554:19]
  assign n195_I0 = n192_O; // @[Top.scala 552:13]
  assign n196_valid_up = n195_valid_down; // @[Top.scala 557:19]
  assign n196_I_t0b = n195_O_t0b; // @[Top.scala 556:12]
  assign n197_valid_up = n196_valid_down; // @[Top.scala 561:19]
  assign n197_I0 = n196_O; // @[Top.scala 559:13]
  assign n197_I1 = n196_O; // @[Top.scala 560:13]
  assign n198_clock = clock;
  assign n198_reset = reset;
  assign n198_valid_up = n197_valid_down; // @[Top.scala 564:19]
  assign n198_I_t0b = n197_O_t0b; // @[Top.scala 563:12]
  assign n198_I_t1b = n197_O_t1b; // @[Top.scala 563:12]
  assign n200_valid_up = n199_valid_down & n198_valid_down; // @[Top.scala 568:19]
  assign n200_I0 = n199_O; // @[Top.scala 566:13]
  assign n200_I1 = n198_O; // @[Top.scala 567:13]
  assign n201_valid_up = n200_valid_down; // @[Top.scala 571:19]
  assign n201_I_t0b = n200_O_t0b; // @[Top.scala 570:12]
  assign n201_I_t1b = n200_O_t1b; // @[Top.scala 570:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n203_valid_up = n196_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 576:19]
  assign n203_I0 = n196_O; // @[Top.scala 574:13]
  assign n203_I1 = 16'h1; // @[Top.scala 575:13]
  assign n204_valid_up = n203_valid_down; // @[Top.scala 579:19]
  assign n204_I_t0b = n203_O_t0b; // @[Top.scala 578:12]
  assign n204_I_t1b = n203_O_t1b; // @[Top.scala 578:12]
  assign n205_valid_up = n186_valid_down & n204_valid_down; // @[Top.scala 583:19]
  assign n205_I0 = n186_O; // @[Top.scala 581:13]
  assign n205_I1 = n204_O; // @[Top.scala 582:13]
  assign n206_valid_up = n196_valid_down & n187_valid_down; // @[Top.scala 587:19]
  assign n206_I0 = n196_O; // @[Top.scala 585:13]
  assign n206_I1 = n187_O; // @[Top.scala 586:13]
  assign n207_valid_up = n205_valid_down & n206_valid_down; // @[Top.scala 591:19]
  assign n207_I0_t0b = n205_O_t0b; // @[Top.scala 589:13]
  assign n207_I0_t1b = n205_O_t1b; // @[Top.scala 589:13]
  assign n207_I1_t0b = n206_O_t0b; // @[Top.scala 590:13]
  assign n207_I1_t1b = n206_O_t1b; // @[Top.scala 590:13]
  assign n208_clock = clock;
  assign n208_reset = reset;
  assign n208_valid_up = n207_valid_down; // @[Top.scala 594:19]
  assign n208_I_t0b_t0b = n207_O_t0b_t0b; // @[Top.scala 593:12]
  assign n208_I_t0b_t1b = n207_O_t0b_t1b; // @[Top.scala 593:12]
  assign n208_I_t1b_t0b = n207_O_t1b_t0b; // @[Top.scala 593:12]
  assign n208_I_t1b_t1b = n207_O_t1b_t1b; // @[Top.scala 593:12]
  assign n209_valid_up = n201_valid_down & n208_valid_down; // @[Top.scala 598:19]
  assign n209_I0 = n201_O[0]; // @[Top.scala 596:13]
  assign n209_I1_t0b_t0b = n208_O_t0b_t0b; // @[Top.scala 597:13]
  assign n209_I1_t0b_t1b = n208_O_t0b_t1b; // @[Top.scala 597:13]
  assign n209_I1_t1b_t0b = n208_O_t1b_t0b; // @[Top.scala 597:13]
  assign n209_I1_t1b_t1b = n208_O_t1b_t1b; // @[Top.scala 597:13]
  assign n210_valid_up = n209_valid_down; // @[Top.scala 601:19]
  assign n210_I_t0b = n209_O_t0b; // @[Top.scala 600:12]
  assign n210_I_t1b_t0b_t0b = n209_O_t1b_t0b_t0b; // @[Top.scala 600:12]
  assign n210_I_t1b_t0b_t1b = n209_O_t1b_t0b_t1b; // @[Top.scala 600:12]
  assign n210_I_t1b_t1b_t0b = n209_O_t1b_t1b_t0b; // @[Top.scala 600:12]
  assign n210_I_t1b_t1b_t1b = n209_O_t1b_t1b_t1b; // @[Top.scala 600:12]
  assign n212_valid_up = n211_valid_down & n210_valid_down; // @[Top.scala 605:19]
  assign n212_I0 = n211_O; // @[Top.scala 603:13]
  assign n212_I1_t0b = n210_O_t0b; // @[Top.scala 604:13]
  assign n212_I1_t1b = n210_O_t1b; // @[Top.scala 604:13]
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
  wire  n217_valid_up; // @[Top.scala 613:22]
  wire  n217_valid_down; // @[Top.scala 613:22]
  wire [15:0] n217_I_t0b; // @[Top.scala 613:22]
  wire [15:0] n217_O; // @[Top.scala 613:22]
  wire  n245_clock; // @[Top.scala 616:22]
  wire  n245_reset; // @[Top.scala 616:22]
  wire  n245_valid_up; // @[Top.scala 616:22]
  wire  n245_valid_down; // @[Top.scala 616:22]
  wire [15:0] n245_I; // @[Top.scala 616:22]
  wire [15:0] n245_O; // @[Top.scala 616:22]
  wire  n233_clock; // @[Top.scala 619:22]
  wire  n233_reset; // @[Top.scala 619:22]
  wire  n233_valid_up; // @[Top.scala 619:22]
  wire  n233_valid_down; // @[Top.scala 619:22]
  wire [15:0] n233_I; // @[Top.scala 619:22]
  wire [15:0] n233_O; // @[Top.scala 619:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n219_valid_up; // @[Top.scala 623:22]
  wire  n219_valid_down; // @[Top.scala 623:22]
  wire [15:0] n219_I_t1b_t0b; // @[Top.scala 623:22]
  wire [15:0] n219_I_t1b_t1b; // @[Top.scala 623:22]
  wire [15:0] n219_O_t0b; // @[Top.scala 623:22]
  wire [15:0] n219_O_t1b; // @[Top.scala 623:22]
  wire  n220_valid_up; // @[Top.scala 626:22]
  wire  n220_valid_down; // @[Top.scala 626:22]
  wire [15:0] n220_I_t0b; // @[Top.scala 626:22]
  wire [15:0] n220_O; // @[Top.scala 626:22]
  wire  n221_valid_up; // @[Top.scala 629:22]
  wire  n221_valid_down; // @[Top.scala 629:22]
  wire [15:0] n221_I_t1b; // @[Top.scala 629:22]
  wire [15:0] n221_O; // @[Top.scala 629:22]
  wire  n222_valid_up; // @[Top.scala 632:22]
  wire  n222_valid_down; // @[Top.scala 632:22]
  wire [15:0] n222_I0; // @[Top.scala 632:22]
  wire [15:0] n222_I1; // @[Top.scala 632:22]
  wire [15:0] n222_O_t0b; // @[Top.scala 632:22]
  wire [15:0] n222_O_t1b; // @[Top.scala 632:22]
  wire  n223_valid_up; // @[Top.scala 636:22]
  wire  n223_valid_down; // @[Top.scala 636:22]
  wire [15:0] n223_I_t0b; // @[Top.scala 636:22]
  wire [15:0] n223_I_t1b; // @[Top.scala 636:22]
  wire [15:0] n223_O; // @[Top.scala 636:22]
  wire  n225_valid_up; // @[Top.scala 639:22]
  wire  n225_valid_down; // @[Top.scala 639:22]
  wire [15:0] n225_I0; // @[Top.scala 639:22]
  wire [15:0] n225_I1; // @[Top.scala 639:22]
  wire [15:0] n225_O_t0b; // @[Top.scala 639:22]
  wire [15:0] n225_O_t1b; // @[Top.scala 639:22]
  wire  n226_valid_up; // @[Top.scala 643:22]
  wire  n226_valid_down; // @[Top.scala 643:22]
  wire [15:0] n226_I_t0b; // @[Top.scala 643:22]
  wire [15:0] n226_I_t1b; // @[Top.scala 643:22]
  wire [15:0] n226_O; // @[Top.scala 643:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n229_valid_up; // @[Top.scala 647:22]
  wire  n229_valid_down; // @[Top.scala 647:22]
  wire [15:0] n229_I0; // @[Top.scala 647:22]
  wire [15:0] n229_O_t0b; // @[Top.scala 647:22]
  wire  n230_valid_up; // @[Top.scala 651:22]
  wire  n230_valid_down; // @[Top.scala 651:22]
  wire [15:0] n230_I_t0b; // @[Top.scala 651:22]
  wire [15:0] n230_O; // @[Top.scala 651:22]
  wire  n231_valid_up; // @[Top.scala 654:22]
  wire  n231_valid_down; // @[Top.scala 654:22]
  wire [15:0] n231_I0; // @[Top.scala 654:22]
  wire [15:0] n231_I1; // @[Top.scala 654:22]
  wire [15:0] n231_O_t0b; // @[Top.scala 654:22]
  wire [15:0] n231_O_t1b; // @[Top.scala 654:22]
  wire  n232_clock; // @[Top.scala 658:22]
  wire  n232_reset; // @[Top.scala 658:22]
  wire  n232_valid_up; // @[Top.scala 658:22]
  wire  n232_valid_down; // @[Top.scala 658:22]
  wire [15:0] n232_I_t0b; // @[Top.scala 658:22]
  wire [15:0] n232_I_t1b; // @[Top.scala 658:22]
  wire [15:0] n232_O; // @[Top.scala 658:22]
  wire  n234_valid_up; // @[Top.scala 661:22]
  wire  n234_valid_down; // @[Top.scala 661:22]
  wire [15:0] n234_I0; // @[Top.scala 661:22]
  wire [15:0] n234_I1; // @[Top.scala 661:22]
  wire [15:0] n234_O_t0b; // @[Top.scala 661:22]
  wire [15:0] n234_O_t1b; // @[Top.scala 661:22]
  wire  n235_valid_up; // @[Top.scala 665:22]
  wire  n235_valid_down; // @[Top.scala 665:22]
  wire [15:0] n235_I_t0b; // @[Top.scala 665:22]
  wire [15:0] n235_I_t1b; // @[Top.scala 665:22]
  wire [15:0] n235_O; // @[Top.scala 665:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n237_valid_up; // @[Top.scala 669:22]
  wire  n237_valid_down; // @[Top.scala 669:22]
  wire [15:0] n237_I0; // @[Top.scala 669:22]
  wire [15:0] n237_I1; // @[Top.scala 669:22]
  wire [15:0] n237_O_t0b; // @[Top.scala 669:22]
  wire [15:0] n237_O_t1b; // @[Top.scala 669:22]
  wire  n238_valid_up; // @[Top.scala 673:22]
  wire  n238_valid_down; // @[Top.scala 673:22]
  wire [15:0] n238_I_t0b; // @[Top.scala 673:22]
  wire [15:0] n238_I_t1b; // @[Top.scala 673:22]
  wire [15:0] n238_O; // @[Top.scala 673:22]
  wire  n239_valid_up; // @[Top.scala 676:22]
  wire  n239_valid_down; // @[Top.scala 676:22]
  wire [15:0] n239_I0; // @[Top.scala 676:22]
  wire [15:0] n239_I1; // @[Top.scala 676:22]
  wire [15:0] n239_O_t0b; // @[Top.scala 676:22]
  wire [15:0] n239_O_t1b; // @[Top.scala 676:22]
  wire  n240_valid_up; // @[Top.scala 680:22]
  wire  n240_valid_down; // @[Top.scala 680:22]
  wire [15:0] n240_I0; // @[Top.scala 680:22]
  wire [15:0] n240_I1; // @[Top.scala 680:22]
  wire [15:0] n240_O_t0b; // @[Top.scala 680:22]
  wire [15:0] n240_O_t1b; // @[Top.scala 680:22]
  wire  n241_valid_up; // @[Top.scala 684:22]
  wire  n241_valid_down; // @[Top.scala 684:22]
  wire [15:0] n241_I0_t0b; // @[Top.scala 684:22]
  wire [15:0] n241_I0_t1b; // @[Top.scala 684:22]
  wire [15:0] n241_I1_t0b; // @[Top.scala 684:22]
  wire [15:0] n241_I1_t1b; // @[Top.scala 684:22]
  wire [15:0] n241_O_t0b_t0b; // @[Top.scala 684:22]
  wire [15:0] n241_O_t0b_t1b; // @[Top.scala 684:22]
  wire [15:0] n241_O_t1b_t0b; // @[Top.scala 684:22]
  wire [15:0] n241_O_t1b_t1b; // @[Top.scala 684:22]
  wire  n242_clock; // @[Top.scala 688:22]
  wire  n242_reset; // @[Top.scala 688:22]
  wire  n242_valid_up; // @[Top.scala 688:22]
  wire  n242_valid_down; // @[Top.scala 688:22]
  wire [15:0] n242_I_t0b_t0b; // @[Top.scala 688:22]
  wire [15:0] n242_I_t0b_t1b; // @[Top.scala 688:22]
  wire [15:0] n242_I_t1b_t0b; // @[Top.scala 688:22]
  wire [15:0] n242_I_t1b_t1b; // @[Top.scala 688:22]
  wire [15:0] n242_O_t0b_t0b; // @[Top.scala 688:22]
  wire [15:0] n242_O_t0b_t1b; // @[Top.scala 688:22]
  wire [15:0] n242_O_t1b_t0b; // @[Top.scala 688:22]
  wire [15:0] n242_O_t1b_t1b; // @[Top.scala 688:22]
  wire  n243_valid_up; // @[Top.scala 691:22]
  wire  n243_valid_down; // @[Top.scala 691:22]
  wire  n243_I0; // @[Top.scala 691:22]
  wire [15:0] n243_I1_t0b_t0b; // @[Top.scala 691:22]
  wire [15:0] n243_I1_t0b_t1b; // @[Top.scala 691:22]
  wire [15:0] n243_I1_t1b_t0b; // @[Top.scala 691:22]
  wire [15:0] n243_I1_t1b_t1b; // @[Top.scala 691:22]
  wire  n243_O_t0b; // @[Top.scala 691:22]
  wire [15:0] n243_O_t1b_t0b_t0b; // @[Top.scala 691:22]
  wire [15:0] n243_O_t1b_t0b_t1b; // @[Top.scala 691:22]
  wire [15:0] n243_O_t1b_t1b_t0b; // @[Top.scala 691:22]
  wire [15:0] n243_O_t1b_t1b_t1b; // @[Top.scala 691:22]
  wire  n244_valid_up; // @[Top.scala 695:22]
  wire  n244_valid_down; // @[Top.scala 695:22]
  wire  n244_I_t0b; // @[Top.scala 695:22]
  wire [15:0] n244_I_t1b_t0b_t0b; // @[Top.scala 695:22]
  wire [15:0] n244_I_t1b_t0b_t1b; // @[Top.scala 695:22]
  wire [15:0] n244_I_t1b_t1b_t0b; // @[Top.scala 695:22]
  wire [15:0] n244_I_t1b_t1b_t1b; // @[Top.scala 695:22]
  wire [15:0] n244_O_t0b; // @[Top.scala 695:22]
  wire [15:0] n244_O_t1b; // @[Top.scala 695:22]
  wire  n246_valid_up; // @[Top.scala 698:22]
  wire  n246_valid_down; // @[Top.scala 698:22]
  wire [15:0] n246_I0; // @[Top.scala 698:22]
  wire [15:0] n246_I1_t0b; // @[Top.scala 698:22]
  wire [15:0] n246_I1_t1b; // @[Top.scala 698:22]
  wire [15:0] n246_O_t0b; // @[Top.scala 698:22]
  wire [15:0] n246_O_t1b_t0b; // @[Top.scala 698:22]
  wire [15:0] n246_O_t1b_t1b; // @[Top.scala 698:22]
  Fst n217 ( // @[Top.scala 613:22]
    .valid_up(n217_valid_up),
    .valid_down(n217_valid_down),
    .I_t0b(n217_I_t0b),
    .O(n217_O)
  );
  FIFO_2 n245 ( // @[Top.scala 616:22]
    .clock(n245_clock),
    .reset(n245_reset),
    .valid_up(n245_valid_up),
    .valid_down(n245_valid_down),
    .I(n245_I),
    .O(n245_O)
  );
  FIFO_2 n233 ( // @[Top.scala 619:22]
    .clock(n233_clock),
    .reset(n233_reset),
    .valid_up(n233_valid_up),
    .valid_down(n233_valid_down),
    .I(n233_I),
    .O(n233_O)
  );
  InitialDelayCounter_18 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n219 ( // @[Top.scala 623:22]
    .valid_up(n219_valid_up),
    .valid_down(n219_valid_down),
    .I_t1b_t0b(n219_I_t1b_t0b),
    .I_t1b_t1b(n219_I_t1b_t1b),
    .O_t0b(n219_O_t0b),
    .O_t1b(n219_O_t1b)
  );
  Fst_1 n220 ( // @[Top.scala 626:22]
    .valid_up(n220_valid_up),
    .valid_down(n220_valid_down),
    .I_t0b(n220_I_t0b),
    .O(n220_O)
  );
  Snd_1 n221 ( // @[Top.scala 629:22]
    .valid_up(n221_valid_up),
    .valid_down(n221_valid_down),
    .I_t1b(n221_I_t1b),
    .O(n221_O)
  );
  AtomTuple n222 ( // @[Top.scala 632:22]
    .valid_up(n222_valid_up),
    .valid_down(n222_valid_down),
    .I0(n222_I0),
    .I1(n222_I1),
    .O_t0b(n222_O_t0b),
    .O_t1b(n222_O_t1b)
  );
  Add n223 ( // @[Top.scala 636:22]
    .valid_up(n223_valid_up),
    .valid_down(n223_valid_down),
    .I_t0b(n223_I_t0b),
    .I_t1b(n223_I_t1b),
    .O(n223_O)
  );
  AtomTuple n225 ( // @[Top.scala 639:22]
    .valid_up(n225_valid_up),
    .valid_down(n225_valid_down),
    .I0(n225_I0),
    .I1(n225_I1),
    .O_t0b(n225_O_t0b),
    .O_t1b(n225_O_t1b)
  );
  Add n226 ( // @[Top.scala 643:22]
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
  AtomTuple_4 n229 ( // @[Top.scala 647:22]
    .valid_up(n229_valid_up),
    .valid_down(n229_valid_down),
    .I0(n229_I0),
    .O_t0b(n229_O_t0b)
  );
  RShift n230 ( // @[Top.scala 651:22]
    .valid_up(n230_valid_up),
    .valid_down(n230_valid_down),
    .I_t0b(n230_I_t0b),
    .O(n230_O)
  );
  AtomTuple n231 ( // @[Top.scala 654:22]
    .valid_up(n231_valid_up),
    .valid_down(n231_valid_down),
    .I0(n231_I0),
    .I1(n231_I1),
    .O_t0b(n231_O_t0b),
    .O_t1b(n231_O_t1b)
  );
  Mul n232 ( // @[Top.scala 658:22]
    .clock(n232_clock),
    .reset(n232_reset),
    .valid_up(n232_valid_up),
    .valid_down(n232_valid_down),
    .I_t0b(n232_I_t0b),
    .I_t1b(n232_I_t1b),
    .O(n232_O)
  );
  AtomTuple n234 ( // @[Top.scala 661:22]
    .valid_up(n234_valid_up),
    .valid_down(n234_valid_down),
    .I0(n234_I0),
    .I1(n234_I1),
    .O_t0b(n234_O_t0b),
    .O_t1b(n234_O_t1b)
  );
  Lt n235 ( // @[Top.scala 665:22]
    .valid_up(n235_valid_up),
    .valid_down(n235_valid_down),
    .I_t0b(n235_I_t0b),
    .I_t1b(n235_I_t1b),
    .O(n235_O)
  );
  InitialDelayCounter_18 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n237 ( // @[Top.scala 669:22]
    .valid_up(n237_valid_up),
    .valid_down(n237_valid_down),
    .I0(n237_I0),
    .I1(n237_I1),
    .O_t0b(n237_O_t0b),
    .O_t1b(n237_O_t1b)
  );
  Sub n238 ( // @[Top.scala 673:22]
    .valid_up(n238_valid_up),
    .valid_down(n238_valid_down),
    .I_t0b(n238_I_t0b),
    .I_t1b(n238_I_t1b),
    .O(n238_O)
  );
  AtomTuple n239 ( // @[Top.scala 676:22]
    .valid_up(n239_valid_up),
    .valid_down(n239_valid_down),
    .I0(n239_I0),
    .I1(n239_I1),
    .O_t0b(n239_O_t0b),
    .O_t1b(n239_O_t1b)
  );
  AtomTuple n240 ( // @[Top.scala 680:22]
    .valid_up(n240_valid_up),
    .valid_down(n240_valid_down),
    .I0(n240_I0),
    .I1(n240_I1),
    .O_t0b(n240_O_t0b),
    .O_t1b(n240_O_t1b)
  );
  AtomTuple_10 n241 ( // @[Top.scala 684:22]
    .valid_up(n241_valid_up),
    .valid_down(n241_valid_down),
    .I0_t0b(n241_I0_t0b),
    .I0_t1b(n241_I0_t1b),
    .I1_t0b(n241_I1_t0b),
    .I1_t1b(n241_I1_t1b),
    .O_t0b_t0b(n241_O_t0b_t0b),
    .O_t0b_t1b(n241_O_t0b_t1b),
    .O_t1b_t0b(n241_O_t1b_t0b),
    .O_t1b_t1b(n241_O_t1b_t1b)
  );
  FIFO_4 n242 ( // @[Top.scala 688:22]
    .clock(n242_clock),
    .reset(n242_reset),
    .valid_up(n242_valid_up),
    .valid_down(n242_valid_down),
    .I_t0b_t0b(n242_I_t0b_t0b),
    .I_t0b_t1b(n242_I_t0b_t1b),
    .I_t1b_t0b(n242_I_t1b_t0b),
    .I_t1b_t1b(n242_I_t1b_t1b),
    .O_t0b_t0b(n242_O_t0b_t0b),
    .O_t0b_t1b(n242_O_t0b_t1b),
    .O_t1b_t0b(n242_O_t1b_t0b),
    .O_t1b_t1b(n242_O_t1b_t1b)
  );
  AtomTuple_11 n243 ( // @[Top.scala 691:22]
    .valid_up(n243_valid_up),
    .valid_down(n243_valid_down),
    .I0(n243_I0),
    .I1_t0b_t0b(n243_I1_t0b_t0b),
    .I1_t0b_t1b(n243_I1_t0b_t1b),
    .I1_t1b_t0b(n243_I1_t1b_t0b),
    .I1_t1b_t1b(n243_I1_t1b_t1b),
    .O_t0b(n243_O_t0b),
    .O_t1b_t0b_t0b(n243_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n243_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n243_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n243_O_t1b_t1b_t1b)
  );
  If n244 ( // @[Top.scala 695:22]
    .valid_up(n244_valid_up),
    .valid_down(n244_valid_down),
    .I_t0b(n244_I_t0b),
    .I_t1b_t0b_t0b(n244_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n244_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n244_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n244_I_t1b_t1b_t1b),
    .O_t0b(n244_O_t0b),
    .O_t1b(n244_O_t1b)
  );
  AtomTuple_1 n246 ( // @[Top.scala 698:22]
    .valid_up(n246_valid_up),
    .valid_down(n246_valid_down),
    .I0(n246_I0),
    .I1_t0b(n246_I1_t0b),
    .I1_t1b(n246_I1_t1b),
    .O_t0b(n246_O_t0b),
    .O_t1b_t0b(n246_O_t1b_t0b),
    .O_t1b_t1b(n246_O_t1b_t1b)
  );
  assign valid_down = n246_valid_down; // @[Top.scala 703:16]
  assign O_t0b = n246_O_t0b; // @[Top.scala 702:7]
  assign O_t1b_t0b = n246_O_t1b_t0b; // @[Top.scala 702:7]
  assign O_t1b_t1b = n246_O_t1b_t1b; // @[Top.scala 702:7]
  assign n217_valid_up = valid_up; // @[Top.scala 615:19]
  assign n217_I_t0b = I_t0b; // @[Top.scala 614:12]
  assign n245_clock = clock;
  assign n245_reset = reset;
  assign n245_valid_up = n217_valid_down; // @[Top.scala 618:19]
  assign n245_I = n217_O; // @[Top.scala 617:12]
  assign n233_clock = clock;
  assign n233_reset = reset;
  assign n233_valid_up = n217_valid_down; // @[Top.scala 621:19]
  assign n233_I = n217_O; // @[Top.scala 620:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n219_valid_up = valid_up; // @[Top.scala 625:19]
  assign n219_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 624:12]
  assign n219_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 624:12]
  assign n220_valid_up = n219_valid_down; // @[Top.scala 628:19]
  assign n220_I_t0b = n219_O_t0b; // @[Top.scala 627:12]
  assign n221_valid_up = n219_valid_down; // @[Top.scala 631:19]
  assign n221_I_t1b = n219_O_t1b; // @[Top.scala 630:12]
  assign n222_valid_up = n220_valid_down & n221_valid_down; // @[Top.scala 635:19]
  assign n222_I0 = n220_O; // @[Top.scala 633:13]
  assign n222_I1 = n221_O; // @[Top.scala 634:13]
  assign n223_valid_up = n222_valid_down; // @[Top.scala 638:19]
  assign n223_I_t0b = n222_O_t0b; // @[Top.scala 637:12]
  assign n223_I_t1b = n222_O_t1b; // @[Top.scala 637:12]
  assign n225_valid_up = InitialDelayCounter_valid_down & n223_valid_down; // @[Top.scala 642:19]
  assign n225_I0 = 16'h1; // @[Top.scala 640:13]
  assign n225_I1 = n223_O; // @[Top.scala 641:13]
  assign n226_valid_up = n225_valid_down; // @[Top.scala 645:19]
  assign n226_I_t0b = n225_O_t0b; // @[Top.scala 644:12]
  assign n226_I_t1b = n225_O_t1b; // @[Top.scala 644:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n229_valid_up = n226_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 650:19]
  assign n229_I0 = n226_O; // @[Top.scala 648:13]
  assign n230_valid_up = n229_valid_down; // @[Top.scala 653:19]
  assign n230_I_t0b = n229_O_t0b; // @[Top.scala 652:12]
  assign n231_valid_up = n230_valid_down; // @[Top.scala 657:19]
  assign n231_I0 = n230_O; // @[Top.scala 655:13]
  assign n231_I1 = n230_O; // @[Top.scala 656:13]
  assign n232_clock = clock;
  assign n232_reset = reset;
  assign n232_valid_up = n231_valid_down; // @[Top.scala 660:19]
  assign n232_I_t0b = n231_O_t0b; // @[Top.scala 659:12]
  assign n232_I_t1b = n231_O_t1b; // @[Top.scala 659:12]
  assign n234_valid_up = n233_valid_down & n232_valid_down; // @[Top.scala 664:19]
  assign n234_I0 = n233_O; // @[Top.scala 662:13]
  assign n234_I1 = n232_O; // @[Top.scala 663:13]
  assign n235_valid_up = n234_valid_down; // @[Top.scala 667:19]
  assign n235_I_t0b = n234_O_t0b; // @[Top.scala 666:12]
  assign n235_I_t1b = n234_O_t1b; // @[Top.scala 666:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n237_valid_up = n230_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 672:19]
  assign n237_I0 = n230_O; // @[Top.scala 670:13]
  assign n237_I1 = 16'h1; // @[Top.scala 671:13]
  assign n238_valid_up = n237_valid_down; // @[Top.scala 675:19]
  assign n238_I_t0b = n237_O_t0b; // @[Top.scala 674:12]
  assign n238_I_t1b = n237_O_t1b; // @[Top.scala 674:12]
  assign n239_valid_up = n220_valid_down & n238_valid_down; // @[Top.scala 679:19]
  assign n239_I0 = n220_O; // @[Top.scala 677:13]
  assign n239_I1 = n238_O; // @[Top.scala 678:13]
  assign n240_valid_up = n230_valid_down & n221_valid_down; // @[Top.scala 683:19]
  assign n240_I0 = n230_O; // @[Top.scala 681:13]
  assign n240_I1 = n221_O; // @[Top.scala 682:13]
  assign n241_valid_up = n239_valid_down & n240_valid_down; // @[Top.scala 687:19]
  assign n241_I0_t0b = n239_O_t0b; // @[Top.scala 685:13]
  assign n241_I0_t1b = n239_O_t1b; // @[Top.scala 685:13]
  assign n241_I1_t0b = n240_O_t0b; // @[Top.scala 686:13]
  assign n241_I1_t1b = n240_O_t1b; // @[Top.scala 686:13]
  assign n242_clock = clock;
  assign n242_reset = reset;
  assign n242_valid_up = n241_valid_down; // @[Top.scala 690:19]
  assign n242_I_t0b_t0b = n241_O_t0b_t0b; // @[Top.scala 689:12]
  assign n242_I_t0b_t1b = n241_O_t0b_t1b; // @[Top.scala 689:12]
  assign n242_I_t1b_t0b = n241_O_t1b_t0b; // @[Top.scala 689:12]
  assign n242_I_t1b_t1b = n241_O_t1b_t1b; // @[Top.scala 689:12]
  assign n243_valid_up = n235_valid_down & n242_valid_down; // @[Top.scala 694:19]
  assign n243_I0 = n235_O[0]; // @[Top.scala 692:13]
  assign n243_I1_t0b_t0b = n242_O_t0b_t0b; // @[Top.scala 693:13]
  assign n243_I1_t0b_t1b = n242_O_t0b_t1b; // @[Top.scala 693:13]
  assign n243_I1_t1b_t0b = n242_O_t1b_t0b; // @[Top.scala 693:13]
  assign n243_I1_t1b_t1b = n242_O_t1b_t1b; // @[Top.scala 693:13]
  assign n244_valid_up = n243_valid_down; // @[Top.scala 697:19]
  assign n244_I_t0b = n243_O_t0b; // @[Top.scala 696:12]
  assign n244_I_t1b_t0b_t0b = n243_O_t1b_t0b_t0b; // @[Top.scala 696:12]
  assign n244_I_t1b_t0b_t1b = n243_O_t1b_t0b_t1b; // @[Top.scala 696:12]
  assign n244_I_t1b_t1b_t0b = n243_O_t1b_t1b_t0b; // @[Top.scala 696:12]
  assign n244_I_t1b_t1b_t1b = n243_O_t1b_t1b_t1b; // @[Top.scala 696:12]
  assign n246_valid_up = n245_valid_down & n244_valid_down; // @[Top.scala 701:19]
  assign n246_I0 = n245_O; // @[Top.scala 699:13]
  assign n246_I1_t0b = n244_O_t0b; // @[Top.scala 700:13]
  assign n246_I1_t1b = n244_O_t1b; // @[Top.scala 700:13]
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
  wire  n251_valid_up; // @[Top.scala 709:22]
  wire  n251_valid_down; // @[Top.scala 709:22]
  wire [15:0] n251_I_t0b; // @[Top.scala 709:22]
  wire [15:0] n251_O; // @[Top.scala 709:22]
  wire  n279_clock; // @[Top.scala 712:22]
  wire  n279_reset; // @[Top.scala 712:22]
  wire  n279_valid_up; // @[Top.scala 712:22]
  wire  n279_valid_down; // @[Top.scala 712:22]
  wire [15:0] n279_I; // @[Top.scala 712:22]
  wire [15:0] n279_O; // @[Top.scala 712:22]
  wire  n267_clock; // @[Top.scala 715:22]
  wire  n267_reset; // @[Top.scala 715:22]
  wire  n267_valid_up; // @[Top.scala 715:22]
  wire  n267_valid_down; // @[Top.scala 715:22]
  wire [15:0] n267_I; // @[Top.scala 715:22]
  wire [15:0] n267_O; // @[Top.scala 715:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n253_valid_up; // @[Top.scala 719:22]
  wire  n253_valid_down; // @[Top.scala 719:22]
  wire [15:0] n253_I_t1b_t0b; // @[Top.scala 719:22]
  wire [15:0] n253_I_t1b_t1b; // @[Top.scala 719:22]
  wire [15:0] n253_O_t0b; // @[Top.scala 719:22]
  wire [15:0] n253_O_t1b; // @[Top.scala 719:22]
  wire  n254_valid_up; // @[Top.scala 722:22]
  wire  n254_valid_down; // @[Top.scala 722:22]
  wire [15:0] n254_I_t0b; // @[Top.scala 722:22]
  wire [15:0] n254_O; // @[Top.scala 722:22]
  wire  n255_valid_up; // @[Top.scala 725:22]
  wire  n255_valid_down; // @[Top.scala 725:22]
  wire [15:0] n255_I_t1b; // @[Top.scala 725:22]
  wire [15:0] n255_O; // @[Top.scala 725:22]
  wire  n256_valid_up; // @[Top.scala 728:22]
  wire  n256_valid_down; // @[Top.scala 728:22]
  wire [15:0] n256_I0; // @[Top.scala 728:22]
  wire [15:0] n256_I1; // @[Top.scala 728:22]
  wire [15:0] n256_O_t0b; // @[Top.scala 728:22]
  wire [15:0] n256_O_t1b; // @[Top.scala 728:22]
  wire  n257_valid_up; // @[Top.scala 732:22]
  wire  n257_valid_down; // @[Top.scala 732:22]
  wire [15:0] n257_I_t0b; // @[Top.scala 732:22]
  wire [15:0] n257_I_t1b; // @[Top.scala 732:22]
  wire [15:0] n257_O; // @[Top.scala 732:22]
  wire  n259_valid_up; // @[Top.scala 735:22]
  wire  n259_valid_down; // @[Top.scala 735:22]
  wire [15:0] n259_I0; // @[Top.scala 735:22]
  wire [15:0] n259_I1; // @[Top.scala 735:22]
  wire [15:0] n259_O_t0b; // @[Top.scala 735:22]
  wire [15:0] n259_O_t1b; // @[Top.scala 735:22]
  wire  n260_valid_up; // @[Top.scala 739:22]
  wire  n260_valid_down; // @[Top.scala 739:22]
  wire [15:0] n260_I_t0b; // @[Top.scala 739:22]
  wire [15:0] n260_I_t1b; // @[Top.scala 739:22]
  wire [15:0] n260_O; // @[Top.scala 739:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n263_valid_up; // @[Top.scala 743:22]
  wire  n263_valid_down; // @[Top.scala 743:22]
  wire [15:0] n263_I0; // @[Top.scala 743:22]
  wire [15:0] n263_O_t0b; // @[Top.scala 743:22]
  wire  n264_valid_up; // @[Top.scala 747:22]
  wire  n264_valid_down; // @[Top.scala 747:22]
  wire [15:0] n264_I_t0b; // @[Top.scala 747:22]
  wire [15:0] n264_O; // @[Top.scala 747:22]
  wire  n265_valid_up; // @[Top.scala 750:22]
  wire  n265_valid_down; // @[Top.scala 750:22]
  wire [15:0] n265_I0; // @[Top.scala 750:22]
  wire [15:0] n265_I1; // @[Top.scala 750:22]
  wire [15:0] n265_O_t0b; // @[Top.scala 750:22]
  wire [15:0] n265_O_t1b; // @[Top.scala 750:22]
  wire  n266_clock; // @[Top.scala 754:22]
  wire  n266_reset; // @[Top.scala 754:22]
  wire  n266_valid_up; // @[Top.scala 754:22]
  wire  n266_valid_down; // @[Top.scala 754:22]
  wire [15:0] n266_I_t0b; // @[Top.scala 754:22]
  wire [15:0] n266_I_t1b; // @[Top.scala 754:22]
  wire [15:0] n266_O; // @[Top.scala 754:22]
  wire  n268_valid_up; // @[Top.scala 757:22]
  wire  n268_valid_down; // @[Top.scala 757:22]
  wire [15:0] n268_I0; // @[Top.scala 757:22]
  wire [15:0] n268_I1; // @[Top.scala 757:22]
  wire [15:0] n268_O_t0b; // @[Top.scala 757:22]
  wire [15:0] n268_O_t1b; // @[Top.scala 757:22]
  wire  n269_valid_up; // @[Top.scala 761:22]
  wire  n269_valid_down; // @[Top.scala 761:22]
  wire [15:0] n269_I_t0b; // @[Top.scala 761:22]
  wire [15:0] n269_I_t1b; // @[Top.scala 761:22]
  wire [15:0] n269_O; // @[Top.scala 761:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n271_valid_up; // @[Top.scala 765:22]
  wire  n271_valid_down; // @[Top.scala 765:22]
  wire [15:0] n271_I0; // @[Top.scala 765:22]
  wire [15:0] n271_I1; // @[Top.scala 765:22]
  wire [15:0] n271_O_t0b; // @[Top.scala 765:22]
  wire [15:0] n271_O_t1b; // @[Top.scala 765:22]
  wire  n272_valid_up; // @[Top.scala 769:22]
  wire  n272_valid_down; // @[Top.scala 769:22]
  wire [15:0] n272_I_t0b; // @[Top.scala 769:22]
  wire [15:0] n272_I_t1b; // @[Top.scala 769:22]
  wire [15:0] n272_O; // @[Top.scala 769:22]
  wire  n273_valid_up; // @[Top.scala 772:22]
  wire  n273_valid_down; // @[Top.scala 772:22]
  wire [15:0] n273_I0; // @[Top.scala 772:22]
  wire [15:0] n273_I1; // @[Top.scala 772:22]
  wire [15:0] n273_O_t0b; // @[Top.scala 772:22]
  wire [15:0] n273_O_t1b; // @[Top.scala 772:22]
  wire  n274_valid_up; // @[Top.scala 776:22]
  wire  n274_valid_down; // @[Top.scala 776:22]
  wire [15:0] n274_I0; // @[Top.scala 776:22]
  wire [15:0] n274_I1; // @[Top.scala 776:22]
  wire [15:0] n274_O_t0b; // @[Top.scala 776:22]
  wire [15:0] n274_O_t1b; // @[Top.scala 776:22]
  wire  n275_valid_up; // @[Top.scala 780:22]
  wire  n275_valid_down; // @[Top.scala 780:22]
  wire [15:0] n275_I0_t0b; // @[Top.scala 780:22]
  wire [15:0] n275_I0_t1b; // @[Top.scala 780:22]
  wire [15:0] n275_I1_t0b; // @[Top.scala 780:22]
  wire [15:0] n275_I1_t1b; // @[Top.scala 780:22]
  wire [15:0] n275_O_t0b_t0b; // @[Top.scala 780:22]
  wire [15:0] n275_O_t0b_t1b; // @[Top.scala 780:22]
  wire [15:0] n275_O_t1b_t0b; // @[Top.scala 780:22]
  wire [15:0] n275_O_t1b_t1b; // @[Top.scala 780:22]
  wire  n276_clock; // @[Top.scala 784:22]
  wire  n276_reset; // @[Top.scala 784:22]
  wire  n276_valid_up; // @[Top.scala 784:22]
  wire  n276_valid_down; // @[Top.scala 784:22]
  wire [15:0] n276_I_t0b_t0b; // @[Top.scala 784:22]
  wire [15:0] n276_I_t0b_t1b; // @[Top.scala 784:22]
  wire [15:0] n276_I_t1b_t0b; // @[Top.scala 784:22]
  wire [15:0] n276_I_t1b_t1b; // @[Top.scala 784:22]
  wire [15:0] n276_O_t0b_t0b; // @[Top.scala 784:22]
  wire [15:0] n276_O_t0b_t1b; // @[Top.scala 784:22]
  wire [15:0] n276_O_t1b_t0b; // @[Top.scala 784:22]
  wire [15:0] n276_O_t1b_t1b; // @[Top.scala 784:22]
  wire  n277_valid_up; // @[Top.scala 787:22]
  wire  n277_valid_down; // @[Top.scala 787:22]
  wire  n277_I0; // @[Top.scala 787:22]
  wire [15:0] n277_I1_t0b_t0b; // @[Top.scala 787:22]
  wire [15:0] n277_I1_t0b_t1b; // @[Top.scala 787:22]
  wire [15:0] n277_I1_t1b_t0b; // @[Top.scala 787:22]
  wire [15:0] n277_I1_t1b_t1b; // @[Top.scala 787:22]
  wire  n277_O_t0b; // @[Top.scala 787:22]
  wire [15:0] n277_O_t1b_t0b_t0b; // @[Top.scala 787:22]
  wire [15:0] n277_O_t1b_t0b_t1b; // @[Top.scala 787:22]
  wire [15:0] n277_O_t1b_t1b_t0b; // @[Top.scala 787:22]
  wire [15:0] n277_O_t1b_t1b_t1b; // @[Top.scala 787:22]
  wire  n278_valid_up; // @[Top.scala 791:22]
  wire  n278_valid_down; // @[Top.scala 791:22]
  wire  n278_I_t0b; // @[Top.scala 791:22]
  wire [15:0] n278_I_t1b_t0b_t0b; // @[Top.scala 791:22]
  wire [15:0] n278_I_t1b_t0b_t1b; // @[Top.scala 791:22]
  wire [15:0] n278_I_t1b_t1b_t0b; // @[Top.scala 791:22]
  wire [15:0] n278_I_t1b_t1b_t1b; // @[Top.scala 791:22]
  wire [15:0] n278_O_t0b; // @[Top.scala 791:22]
  wire [15:0] n278_O_t1b; // @[Top.scala 791:22]
  wire  n280_valid_up; // @[Top.scala 794:22]
  wire  n280_valid_down; // @[Top.scala 794:22]
  wire [15:0] n280_I0; // @[Top.scala 794:22]
  wire [15:0] n280_I1_t0b; // @[Top.scala 794:22]
  wire [15:0] n280_I1_t1b; // @[Top.scala 794:22]
  wire [15:0] n280_O_t0b; // @[Top.scala 794:22]
  wire [15:0] n280_O_t1b_t0b; // @[Top.scala 794:22]
  wire [15:0] n280_O_t1b_t1b; // @[Top.scala 794:22]
  Fst n251 ( // @[Top.scala 709:22]
    .valid_up(n251_valid_up),
    .valid_down(n251_valid_down),
    .I_t0b(n251_I_t0b),
    .O(n251_O)
  );
  FIFO_2 n279 ( // @[Top.scala 712:22]
    .clock(n279_clock),
    .reset(n279_reset),
    .valid_up(n279_valid_up),
    .valid_down(n279_valid_down),
    .I(n279_I),
    .O(n279_O)
  );
  FIFO_2 n267 ( // @[Top.scala 715:22]
    .clock(n267_clock),
    .reset(n267_reset),
    .valid_up(n267_valid_up),
    .valid_down(n267_valid_down),
    .I(n267_I),
    .O(n267_O)
  );
  InitialDelayCounter_21 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n253 ( // @[Top.scala 719:22]
    .valid_up(n253_valid_up),
    .valid_down(n253_valid_down),
    .I_t1b_t0b(n253_I_t1b_t0b),
    .I_t1b_t1b(n253_I_t1b_t1b),
    .O_t0b(n253_O_t0b),
    .O_t1b(n253_O_t1b)
  );
  Fst_1 n254 ( // @[Top.scala 722:22]
    .valid_up(n254_valid_up),
    .valid_down(n254_valid_down),
    .I_t0b(n254_I_t0b),
    .O(n254_O)
  );
  Snd_1 n255 ( // @[Top.scala 725:22]
    .valid_up(n255_valid_up),
    .valid_down(n255_valid_down),
    .I_t1b(n255_I_t1b),
    .O(n255_O)
  );
  AtomTuple n256 ( // @[Top.scala 728:22]
    .valid_up(n256_valid_up),
    .valid_down(n256_valid_down),
    .I0(n256_I0),
    .I1(n256_I1),
    .O_t0b(n256_O_t0b),
    .O_t1b(n256_O_t1b)
  );
  Add n257 ( // @[Top.scala 732:22]
    .valid_up(n257_valid_up),
    .valid_down(n257_valid_down),
    .I_t0b(n257_I_t0b),
    .I_t1b(n257_I_t1b),
    .O(n257_O)
  );
  AtomTuple n259 ( // @[Top.scala 735:22]
    .valid_up(n259_valid_up),
    .valid_down(n259_valid_down),
    .I0(n259_I0),
    .I1(n259_I1),
    .O_t0b(n259_O_t0b),
    .O_t1b(n259_O_t1b)
  );
  Add n260 ( // @[Top.scala 739:22]
    .valid_up(n260_valid_up),
    .valid_down(n260_valid_down),
    .I_t0b(n260_I_t0b),
    .I_t1b(n260_I_t1b),
    .O(n260_O)
  );
  InitialDelayCounter_21 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n263 ( // @[Top.scala 743:22]
    .valid_up(n263_valid_up),
    .valid_down(n263_valid_down),
    .I0(n263_I0),
    .O_t0b(n263_O_t0b)
  );
  RShift n264 ( // @[Top.scala 747:22]
    .valid_up(n264_valid_up),
    .valid_down(n264_valid_down),
    .I_t0b(n264_I_t0b),
    .O(n264_O)
  );
  AtomTuple n265 ( // @[Top.scala 750:22]
    .valid_up(n265_valid_up),
    .valid_down(n265_valid_down),
    .I0(n265_I0),
    .I1(n265_I1),
    .O_t0b(n265_O_t0b),
    .O_t1b(n265_O_t1b)
  );
  Mul n266 ( // @[Top.scala 754:22]
    .clock(n266_clock),
    .reset(n266_reset),
    .valid_up(n266_valid_up),
    .valid_down(n266_valid_down),
    .I_t0b(n266_I_t0b),
    .I_t1b(n266_I_t1b),
    .O(n266_O)
  );
  AtomTuple n268 ( // @[Top.scala 757:22]
    .valid_up(n268_valid_up),
    .valid_down(n268_valid_down),
    .I0(n268_I0),
    .I1(n268_I1),
    .O_t0b(n268_O_t0b),
    .O_t1b(n268_O_t1b)
  );
  Lt n269 ( // @[Top.scala 761:22]
    .valid_up(n269_valid_up),
    .valid_down(n269_valid_down),
    .I_t0b(n269_I_t0b),
    .I_t1b(n269_I_t1b),
    .O(n269_O)
  );
  InitialDelayCounter_21 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n271 ( // @[Top.scala 765:22]
    .valid_up(n271_valid_up),
    .valid_down(n271_valid_down),
    .I0(n271_I0),
    .I1(n271_I1),
    .O_t0b(n271_O_t0b),
    .O_t1b(n271_O_t1b)
  );
  Sub n272 ( // @[Top.scala 769:22]
    .valid_up(n272_valid_up),
    .valid_down(n272_valid_down),
    .I_t0b(n272_I_t0b),
    .I_t1b(n272_I_t1b),
    .O(n272_O)
  );
  AtomTuple n273 ( // @[Top.scala 772:22]
    .valid_up(n273_valid_up),
    .valid_down(n273_valid_down),
    .I0(n273_I0),
    .I1(n273_I1),
    .O_t0b(n273_O_t0b),
    .O_t1b(n273_O_t1b)
  );
  AtomTuple n274 ( // @[Top.scala 776:22]
    .valid_up(n274_valid_up),
    .valid_down(n274_valid_down),
    .I0(n274_I0),
    .I1(n274_I1),
    .O_t0b(n274_O_t0b),
    .O_t1b(n274_O_t1b)
  );
  AtomTuple_10 n275 ( // @[Top.scala 780:22]
    .valid_up(n275_valid_up),
    .valid_down(n275_valid_down),
    .I0_t0b(n275_I0_t0b),
    .I0_t1b(n275_I0_t1b),
    .I1_t0b(n275_I1_t0b),
    .I1_t1b(n275_I1_t1b),
    .O_t0b_t0b(n275_O_t0b_t0b),
    .O_t0b_t1b(n275_O_t0b_t1b),
    .O_t1b_t0b(n275_O_t1b_t0b),
    .O_t1b_t1b(n275_O_t1b_t1b)
  );
  FIFO_4 n276 ( // @[Top.scala 784:22]
    .clock(n276_clock),
    .reset(n276_reset),
    .valid_up(n276_valid_up),
    .valid_down(n276_valid_down),
    .I_t0b_t0b(n276_I_t0b_t0b),
    .I_t0b_t1b(n276_I_t0b_t1b),
    .I_t1b_t0b(n276_I_t1b_t0b),
    .I_t1b_t1b(n276_I_t1b_t1b),
    .O_t0b_t0b(n276_O_t0b_t0b),
    .O_t0b_t1b(n276_O_t0b_t1b),
    .O_t1b_t0b(n276_O_t1b_t0b),
    .O_t1b_t1b(n276_O_t1b_t1b)
  );
  AtomTuple_11 n277 ( // @[Top.scala 787:22]
    .valid_up(n277_valid_up),
    .valid_down(n277_valid_down),
    .I0(n277_I0),
    .I1_t0b_t0b(n277_I1_t0b_t0b),
    .I1_t0b_t1b(n277_I1_t0b_t1b),
    .I1_t1b_t0b(n277_I1_t1b_t0b),
    .I1_t1b_t1b(n277_I1_t1b_t1b),
    .O_t0b(n277_O_t0b),
    .O_t1b_t0b_t0b(n277_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n277_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n277_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n277_O_t1b_t1b_t1b)
  );
  If n278 ( // @[Top.scala 791:22]
    .valid_up(n278_valid_up),
    .valid_down(n278_valid_down),
    .I_t0b(n278_I_t0b),
    .I_t1b_t0b_t0b(n278_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n278_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n278_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n278_I_t1b_t1b_t1b),
    .O_t0b(n278_O_t0b),
    .O_t1b(n278_O_t1b)
  );
  AtomTuple_1 n280 ( // @[Top.scala 794:22]
    .valid_up(n280_valid_up),
    .valid_down(n280_valid_down),
    .I0(n280_I0),
    .I1_t0b(n280_I1_t0b),
    .I1_t1b(n280_I1_t1b),
    .O_t0b(n280_O_t0b),
    .O_t1b_t0b(n280_O_t1b_t0b),
    .O_t1b_t1b(n280_O_t1b_t1b)
  );
  assign valid_down = n280_valid_down; // @[Top.scala 799:16]
  assign O_t0b = n280_O_t0b; // @[Top.scala 798:7]
  assign O_t1b_t0b = n280_O_t1b_t0b; // @[Top.scala 798:7]
  assign O_t1b_t1b = n280_O_t1b_t1b; // @[Top.scala 798:7]
  assign n251_valid_up = valid_up; // @[Top.scala 711:19]
  assign n251_I_t0b = I_t0b; // @[Top.scala 710:12]
  assign n279_clock = clock;
  assign n279_reset = reset;
  assign n279_valid_up = n251_valid_down; // @[Top.scala 714:19]
  assign n279_I = n251_O; // @[Top.scala 713:12]
  assign n267_clock = clock;
  assign n267_reset = reset;
  assign n267_valid_up = n251_valid_down; // @[Top.scala 717:19]
  assign n267_I = n251_O; // @[Top.scala 716:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n253_valid_up = valid_up; // @[Top.scala 721:19]
  assign n253_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 720:12]
  assign n253_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 720:12]
  assign n254_valid_up = n253_valid_down; // @[Top.scala 724:19]
  assign n254_I_t0b = n253_O_t0b; // @[Top.scala 723:12]
  assign n255_valid_up = n253_valid_down; // @[Top.scala 727:19]
  assign n255_I_t1b = n253_O_t1b; // @[Top.scala 726:12]
  assign n256_valid_up = n254_valid_down & n255_valid_down; // @[Top.scala 731:19]
  assign n256_I0 = n254_O; // @[Top.scala 729:13]
  assign n256_I1 = n255_O; // @[Top.scala 730:13]
  assign n257_valid_up = n256_valid_down; // @[Top.scala 734:19]
  assign n257_I_t0b = n256_O_t0b; // @[Top.scala 733:12]
  assign n257_I_t1b = n256_O_t1b; // @[Top.scala 733:12]
  assign n259_valid_up = InitialDelayCounter_valid_down & n257_valid_down; // @[Top.scala 738:19]
  assign n259_I0 = 16'h1; // @[Top.scala 736:13]
  assign n259_I1 = n257_O; // @[Top.scala 737:13]
  assign n260_valid_up = n259_valid_down; // @[Top.scala 741:19]
  assign n260_I_t0b = n259_O_t0b; // @[Top.scala 740:12]
  assign n260_I_t1b = n259_O_t1b; // @[Top.scala 740:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n263_valid_up = n260_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 746:19]
  assign n263_I0 = n260_O; // @[Top.scala 744:13]
  assign n264_valid_up = n263_valid_down; // @[Top.scala 749:19]
  assign n264_I_t0b = n263_O_t0b; // @[Top.scala 748:12]
  assign n265_valid_up = n264_valid_down; // @[Top.scala 753:19]
  assign n265_I0 = n264_O; // @[Top.scala 751:13]
  assign n265_I1 = n264_O; // @[Top.scala 752:13]
  assign n266_clock = clock;
  assign n266_reset = reset;
  assign n266_valid_up = n265_valid_down; // @[Top.scala 756:19]
  assign n266_I_t0b = n265_O_t0b; // @[Top.scala 755:12]
  assign n266_I_t1b = n265_O_t1b; // @[Top.scala 755:12]
  assign n268_valid_up = n267_valid_down & n266_valid_down; // @[Top.scala 760:19]
  assign n268_I0 = n267_O; // @[Top.scala 758:13]
  assign n268_I1 = n266_O; // @[Top.scala 759:13]
  assign n269_valid_up = n268_valid_down; // @[Top.scala 763:19]
  assign n269_I_t0b = n268_O_t0b; // @[Top.scala 762:12]
  assign n269_I_t1b = n268_O_t1b; // @[Top.scala 762:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n271_valid_up = n264_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 768:19]
  assign n271_I0 = n264_O; // @[Top.scala 766:13]
  assign n271_I1 = 16'h1; // @[Top.scala 767:13]
  assign n272_valid_up = n271_valid_down; // @[Top.scala 771:19]
  assign n272_I_t0b = n271_O_t0b; // @[Top.scala 770:12]
  assign n272_I_t1b = n271_O_t1b; // @[Top.scala 770:12]
  assign n273_valid_up = n254_valid_down & n272_valid_down; // @[Top.scala 775:19]
  assign n273_I0 = n254_O; // @[Top.scala 773:13]
  assign n273_I1 = n272_O; // @[Top.scala 774:13]
  assign n274_valid_up = n264_valid_down & n255_valid_down; // @[Top.scala 779:19]
  assign n274_I0 = n264_O; // @[Top.scala 777:13]
  assign n274_I1 = n255_O; // @[Top.scala 778:13]
  assign n275_valid_up = n273_valid_down & n274_valid_down; // @[Top.scala 783:19]
  assign n275_I0_t0b = n273_O_t0b; // @[Top.scala 781:13]
  assign n275_I0_t1b = n273_O_t1b; // @[Top.scala 781:13]
  assign n275_I1_t0b = n274_O_t0b; // @[Top.scala 782:13]
  assign n275_I1_t1b = n274_O_t1b; // @[Top.scala 782:13]
  assign n276_clock = clock;
  assign n276_reset = reset;
  assign n276_valid_up = n275_valid_down; // @[Top.scala 786:19]
  assign n276_I_t0b_t0b = n275_O_t0b_t0b; // @[Top.scala 785:12]
  assign n276_I_t0b_t1b = n275_O_t0b_t1b; // @[Top.scala 785:12]
  assign n276_I_t1b_t0b = n275_O_t1b_t0b; // @[Top.scala 785:12]
  assign n276_I_t1b_t1b = n275_O_t1b_t1b; // @[Top.scala 785:12]
  assign n277_valid_up = n269_valid_down & n276_valid_down; // @[Top.scala 790:19]
  assign n277_I0 = n269_O[0]; // @[Top.scala 788:13]
  assign n277_I1_t0b_t0b = n276_O_t0b_t0b; // @[Top.scala 789:13]
  assign n277_I1_t0b_t1b = n276_O_t0b_t1b; // @[Top.scala 789:13]
  assign n277_I1_t1b_t0b = n276_O_t1b_t0b; // @[Top.scala 789:13]
  assign n277_I1_t1b_t1b = n276_O_t1b_t1b; // @[Top.scala 789:13]
  assign n278_valid_up = n277_valid_down; // @[Top.scala 793:19]
  assign n278_I_t0b = n277_O_t0b; // @[Top.scala 792:12]
  assign n278_I_t1b_t0b_t0b = n277_O_t1b_t0b_t0b; // @[Top.scala 792:12]
  assign n278_I_t1b_t0b_t1b = n277_O_t1b_t0b_t1b; // @[Top.scala 792:12]
  assign n278_I_t1b_t1b_t0b = n277_O_t1b_t1b_t0b; // @[Top.scala 792:12]
  assign n278_I_t1b_t1b_t1b = n277_O_t1b_t1b_t1b; // @[Top.scala 792:12]
  assign n280_valid_up = n279_valid_down & n278_valid_down; // @[Top.scala 797:19]
  assign n280_I0 = n279_O; // @[Top.scala 795:13]
  assign n280_I1_t0b = n278_O_t0b; // @[Top.scala 796:13]
  assign n280_I1_t1b = n278_O_t1b; // @[Top.scala 796:13]
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
  wire  n285_valid_up; // @[Top.scala 805:22]
  wire  n285_valid_down; // @[Top.scala 805:22]
  wire [15:0] n285_I_t0b; // @[Top.scala 805:22]
  wire [15:0] n285_O; // @[Top.scala 805:22]
  wire  n313_clock; // @[Top.scala 808:22]
  wire  n313_reset; // @[Top.scala 808:22]
  wire  n313_valid_up; // @[Top.scala 808:22]
  wire  n313_valid_down; // @[Top.scala 808:22]
  wire [15:0] n313_I; // @[Top.scala 808:22]
  wire [15:0] n313_O; // @[Top.scala 808:22]
  wire  n301_clock; // @[Top.scala 811:22]
  wire  n301_reset; // @[Top.scala 811:22]
  wire  n301_valid_up; // @[Top.scala 811:22]
  wire  n301_valid_down; // @[Top.scala 811:22]
  wire [15:0] n301_I; // @[Top.scala 811:22]
  wire [15:0] n301_O; // @[Top.scala 811:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n287_valid_up; // @[Top.scala 815:22]
  wire  n287_valid_down; // @[Top.scala 815:22]
  wire [15:0] n287_I_t1b_t0b; // @[Top.scala 815:22]
  wire [15:0] n287_I_t1b_t1b; // @[Top.scala 815:22]
  wire [15:0] n287_O_t0b; // @[Top.scala 815:22]
  wire [15:0] n287_O_t1b; // @[Top.scala 815:22]
  wire  n288_valid_up; // @[Top.scala 818:22]
  wire  n288_valid_down; // @[Top.scala 818:22]
  wire [15:0] n288_I_t0b; // @[Top.scala 818:22]
  wire [15:0] n288_O; // @[Top.scala 818:22]
  wire  n289_valid_up; // @[Top.scala 821:22]
  wire  n289_valid_down; // @[Top.scala 821:22]
  wire [15:0] n289_I_t1b; // @[Top.scala 821:22]
  wire [15:0] n289_O; // @[Top.scala 821:22]
  wire  n290_valid_up; // @[Top.scala 824:22]
  wire  n290_valid_down; // @[Top.scala 824:22]
  wire [15:0] n290_I0; // @[Top.scala 824:22]
  wire [15:0] n290_I1; // @[Top.scala 824:22]
  wire [15:0] n290_O_t0b; // @[Top.scala 824:22]
  wire [15:0] n290_O_t1b; // @[Top.scala 824:22]
  wire  n291_valid_up; // @[Top.scala 828:22]
  wire  n291_valid_down; // @[Top.scala 828:22]
  wire [15:0] n291_I_t0b; // @[Top.scala 828:22]
  wire [15:0] n291_I_t1b; // @[Top.scala 828:22]
  wire [15:0] n291_O; // @[Top.scala 828:22]
  wire  n293_valid_up; // @[Top.scala 831:22]
  wire  n293_valid_down; // @[Top.scala 831:22]
  wire [15:0] n293_I0; // @[Top.scala 831:22]
  wire [15:0] n293_I1; // @[Top.scala 831:22]
  wire [15:0] n293_O_t0b; // @[Top.scala 831:22]
  wire [15:0] n293_O_t1b; // @[Top.scala 831:22]
  wire  n294_valid_up; // @[Top.scala 835:22]
  wire  n294_valid_down; // @[Top.scala 835:22]
  wire [15:0] n294_I_t0b; // @[Top.scala 835:22]
  wire [15:0] n294_I_t1b; // @[Top.scala 835:22]
  wire [15:0] n294_O; // @[Top.scala 835:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n297_valid_up; // @[Top.scala 839:22]
  wire  n297_valid_down; // @[Top.scala 839:22]
  wire [15:0] n297_I0; // @[Top.scala 839:22]
  wire [15:0] n297_O_t0b; // @[Top.scala 839:22]
  wire  n298_valid_up; // @[Top.scala 843:22]
  wire  n298_valid_down; // @[Top.scala 843:22]
  wire [15:0] n298_I_t0b; // @[Top.scala 843:22]
  wire [15:0] n298_O; // @[Top.scala 843:22]
  wire  n299_valid_up; // @[Top.scala 846:22]
  wire  n299_valid_down; // @[Top.scala 846:22]
  wire [15:0] n299_I0; // @[Top.scala 846:22]
  wire [15:0] n299_I1; // @[Top.scala 846:22]
  wire [15:0] n299_O_t0b; // @[Top.scala 846:22]
  wire [15:0] n299_O_t1b; // @[Top.scala 846:22]
  wire  n300_clock; // @[Top.scala 850:22]
  wire  n300_reset; // @[Top.scala 850:22]
  wire  n300_valid_up; // @[Top.scala 850:22]
  wire  n300_valid_down; // @[Top.scala 850:22]
  wire [15:0] n300_I_t0b; // @[Top.scala 850:22]
  wire [15:0] n300_I_t1b; // @[Top.scala 850:22]
  wire [15:0] n300_O; // @[Top.scala 850:22]
  wire  n302_valid_up; // @[Top.scala 853:22]
  wire  n302_valid_down; // @[Top.scala 853:22]
  wire [15:0] n302_I0; // @[Top.scala 853:22]
  wire [15:0] n302_I1; // @[Top.scala 853:22]
  wire [15:0] n302_O_t0b; // @[Top.scala 853:22]
  wire [15:0] n302_O_t1b; // @[Top.scala 853:22]
  wire  n303_valid_up; // @[Top.scala 857:22]
  wire  n303_valid_down; // @[Top.scala 857:22]
  wire [15:0] n303_I_t0b; // @[Top.scala 857:22]
  wire [15:0] n303_I_t1b; // @[Top.scala 857:22]
  wire [15:0] n303_O; // @[Top.scala 857:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n305_valid_up; // @[Top.scala 861:22]
  wire  n305_valid_down; // @[Top.scala 861:22]
  wire [15:0] n305_I0; // @[Top.scala 861:22]
  wire [15:0] n305_I1; // @[Top.scala 861:22]
  wire [15:0] n305_O_t0b; // @[Top.scala 861:22]
  wire [15:0] n305_O_t1b; // @[Top.scala 861:22]
  wire  n306_valid_up; // @[Top.scala 865:22]
  wire  n306_valid_down; // @[Top.scala 865:22]
  wire [15:0] n306_I_t0b; // @[Top.scala 865:22]
  wire [15:0] n306_I_t1b; // @[Top.scala 865:22]
  wire [15:0] n306_O; // @[Top.scala 865:22]
  wire  n307_valid_up; // @[Top.scala 868:22]
  wire  n307_valid_down; // @[Top.scala 868:22]
  wire [15:0] n307_I0; // @[Top.scala 868:22]
  wire [15:0] n307_I1; // @[Top.scala 868:22]
  wire [15:0] n307_O_t0b; // @[Top.scala 868:22]
  wire [15:0] n307_O_t1b; // @[Top.scala 868:22]
  wire  n308_valid_up; // @[Top.scala 872:22]
  wire  n308_valid_down; // @[Top.scala 872:22]
  wire [15:0] n308_I0; // @[Top.scala 872:22]
  wire [15:0] n308_I1; // @[Top.scala 872:22]
  wire [15:0] n308_O_t0b; // @[Top.scala 872:22]
  wire [15:0] n308_O_t1b; // @[Top.scala 872:22]
  wire  n309_valid_up; // @[Top.scala 876:22]
  wire  n309_valid_down; // @[Top.scala 876:22]
  wire [15:0] n309_I0_t0b; // @[Top.scala 876:22]
  wire [15:0] n309_I0_t1b; // @[Top.scala 876:22]
  wire [15:0] n309_I1_t0b; // @[Top.scala 876:22]
  wire [15:0] n309_I1_t1b; // @[Top.scala 876:22]
  wire [15:0] n309_O_t0b_t0b; // @[Top.scala 876:22]
  wire [15:0] n309_O_t0b_t1b; // @[Top.scala 876:22]
  wire [15:0] n309_O_t1b_t0b; // @[Top.scala 876:22]
  wire [15:0] n309_O_t1b_t1b; // @[Top.scala 876:22]
  wire  n310_clock; // @[Top.scala 880:22]
  wire  n310_reset; // @[Top.scala 880:22]
  wire  n310_valid_up; // @[Top.scala 880:22]
  wire  n310_valid_down; // @[Top.scala 880:22]
  wire [15:0] n310_I_t0b_t0b; // @[Top.scala 880:22]
  wire [15:0] n310_I_t0b_t1b; // @[Top.scala 880:22]
  wire [15:0] n310_I_t1b_t0b; // @[Top.scala 880:22]
  wire [15:0] n310_I_t1b_t1b; // @[Top.scala 880:22]
  wire [15:0] n310_O_t0b_t0b; // @[Top.scala 880:22]
  wire [15:0] n310_O_t0b_t1b; // @[Top.scala 880:22]
  wire [15:0] n310_O_t1b_t0b; // @[Top.scala 880:22]
  wire [15:0] n310_O_t1b_t1b; // @[Top.scala 880:22]
  wire  n311_valid_up; // @[Top.scala 883:22]
  wire  n311_valid_down; // @[Top.scala 883:22]
  wire  n311_I0; // @[Top.scala 883:22]
  wire [15:0] n311_I1_t0b_t0b; // @[Top.scala 883:22]
  wire [15:0] n311_I1_t0b_t1b; // @[Top.scala 883:22]
  wire [15:0] n311_I1_t1b_t0b; // @[Top.scala 883:22]
  wire [15:0] n311_I1_t1b_t1b; // @[Top.scala 883:22]
  wire  n311_O_t0b; // @[Top.scala 883:22]
  wire [15:0] n311_O_t1b_t0b_t0b; // @[Top.scala 883:22]
  wire [15:0] n311_O_t1b_t0b_t1b; // @[Top.scala 883:22]
  wire [15:0] n311_O_t1b_t1b_t0b; // @[Top.scala 883:22]
  wire [15:0] n311_O_t1b_t1b_t1b; // @[Top.scala 883:22]
  wire  n312_valid_up; // @[Top.scala 887:22]
  wire  n312_valid_down; // @[Top.scala 887:22]
  wire  n312_I_t0b; // @[Top.scala 887:22]
  wire [15:0] n312_I_t1b_t0b_t0b; // @[Top.scala 887:22]
  wire [15:0] n312_I_t1b_t0b_t1b; // @[Top.scala 887:22]
  wire [15:0] n312_I_t1b_t1b_t0b; // @[Top.scala 887:22]
  wire [15:0] n312_I_t1b_t1b_t1b; // @[Top.scala 887:22]
  wire [15:0] n312_O_t0b; // @[Top.scala 887:22]
  wire [15:0] n312_O_t1b; // @[Top.scala 887:22]
  wire  n314_valid_up; // @[Top.scala 890:22]
  wire  n314_valid_down; // @[Top.scala 890:22]
  wire [15:0] n314_I0; // @[Top.scala 890:22]
  wire [15:0] n314_I1_t0b; // @[Top.scala 890:22]
  wire [15:0] n314_I1_t1b; // @[Top.scala 890:22]
  wire [15:0] n314_O_t0b; // @[Top.scala 890:22]
  wire [15:0] n314_O_t1b_t0b; // @[Top.scala 890:22]
  wire [15:0] n314_O_t1b_t1b; // @[Top.scala 890:22]
  Fst n285 ( // @[Top.scala 805:22]
    .valid_up(n285_valid_up),
    .valid_down(n285_valid_down),
    .I_t0b(n285_I_t0b),
    .O(n285_O)
  );
  FIFO_2 n313 ( // @[Top.scala 808:22]
    .clock(n313_clock),
    .reset(n313_reset),
    .valid_up(n313_valid_up),
    .valid_down(n313_valid_down),
    .I(n313_I),
    .O(n313_O)
  );
  FIFO_2 n301 ( // @[Top.scala 811:22]
    .clock(n301_clock),
    .reset(n301_reset),
    .valid_up(n301_valid_up),
    .valid_down(n301_valid_down),
    .I(n301_I),
    .O(n301_O)
  );
  InitialDelayCounter_24 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n287 ( // @[Top.scala 815:22]
    .valid_up(n287_valid_up),
    .valid_down(n287_valid_down),
    .I_t1b_t0b(n287_I_t1b_t0b),
    .I_t1b_t1b(n287_I_t1b_t1b),
    .O_t0b(n287_O_t0b),
    .O_t1b(n287_O_t1b)
  );
  Fst_1 n288 ( // @[Top.scala 818:22]
    .valid_up(n288_valid_up),
    .valid_down(n288_valid_down),
    .I_t0b(n288_I_t0b),
    .O(n288_O)
  );
  Snd_1 n289 ( // @[Top.scala 821:22]
    .valid_up(n289_valid_up),
    .valid_down(n289_valid_down),
    .I_t1b(n289_I_t1b),
    .O(n289_O)
  );
  AtomTuple n290 ( // @[Top.scala 824:22]
    .valid_up(n290_valid_up),
    .valid_down(n290_valid_down),
    .I0(n290_I0),
    .I1(n290_I1),
    .O_t0b(n290_O_t0b),
    .O_t1b(n290_O_t1b)
  );
  Add n291 ( // @[Top.scala 828:22]
    .valid_up(n291_valid_up),
    .valid_down(n291_valid_down),
    .I_t0b(n291_I_t0b),
    .I_t1b(n291_I_t1b),
    .O(n291_O)
  );
  AtomTuple n293 ( // @[Top.scala 831:22]
    .valid_up(n293_valid_up),
    .valid_down(n293_valid_down),
    .I0(n293_I0),
    .I1(n293_I1),
    .O_t0b(n293_O_t0b),
    .O_t1b(n293_O_t1b)
  );
  Add n294 ( // @[Top.scala 835:22]
    .valid_up(n294_valid_up),
    .valid_down(n294_valid_down),
    .I_t0b(n294_I_t0b),
    .I_t1b(n294_I_t1b),
    .O(n294_O)
  );
  InitialDelayCounter_24 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n297 ( // @[Top.scala 839:22]
    .valid_up(n297_valid_up),
    .valid_down(n297_valid_down),
    .I0(n297_I0),
    .O_t0b(n297_O_t0b)
  );
  RShift n298 ( // @[Top.scala 843:22]
    .valid_up(n298_valid_up),
    .valid_down(n298_valid_down),
    .I_t0b(n298_I_t0b),
    .O(n298_O)
  );
  AtomTuple n299 ( // @[Top.scala 846:22]
    .valid_up(n299_valid_up),
    .valid_down(n299_valid_down),
    .I0(n299_I0),
    .I1(n299_I1),
    .O_t0b(n299_O_t0b),
    .O_t1b(n299_O_t1b)
  );
  Mul n300 ( // @[Top.scala 850:22]
    .clock(n300_clock),
    .reset(n300_reset),
    .valid_up(n300_valid_up),
    .valid_down(n300_valid_down),
    .I_t0b(n300_I_t0b),
    .I_t1b(n300_I_t1b),
    .O(n300_O)
  );
  AtomTuple n302 ( // @[Top.scala 853:22]
    .valid_up(n302_valid_up),
    .valid_down(n302_valid_down),
    .I0(n302_I0),
    .I1(n302_I1),
    .O_t0b(n302_O_t0b),
    .O_t1b(n302_O_t1b)
  );
  Lt n303 ( // @[Top.scala 857:22]
    .valid_up(n303_valid_up),
    .valid_down(n303_valid_down),
    .I_t0b(n303_I_t0b),
    .I_t1b(n303_I_t1b),
    .O(n303_O)
  );
  InitialDelayCounter_24 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n305 ( // @[Top.scala 861:22]
    .valid_up(n305_valid_up),
    .valid_down(n305_valid_down),
    .I0(n305_I0),
    .I1(n305_I1),
    .O_t0b(n305_O_t0b),
    .O_t1b(n305_O_t1b)
  );
  Sub n306 ( // @[Top.scala 865:22]
    .valid_up(n306_valid_up),
    .valid_down(n306_valid_down),
    .I_t0b(n306_I_t0b),
    .I_t1b(n306_I_t1b),
    .O(n306_O)
  );
  AtomTuple n307 ( // @[Top.scala 868:22]
    .valid_up(n307_valid_up),
    .valid_down(n307_valid_down),
    .I0(n307_I0),
    .I1(n307_I1),
    .O_t0b(n307_O_t0b),
    .O_t1b(n307_O_t1b)
  );
  AtomTuple n308 ( // @[Top.scala 872:22]
    .valid_up(n308_valid_up),
    .valid_down(n308_valid_down),
    .I0(n308_I0),
    .I1(n308_I1),
    .O_t0b(n308_O_t0b),
    .O_t1b(n308_O_t1b)
  );
  AtomTuple_10 n309 ( // @[Top.scala 876:22]
    .valid_up(n309_valid_up),
    .valid_down(n309_valid_down),
    .I0_t0b(n309_I0_t0b),
    .I0_t1b(n309_I0_t1b),
    .I1_t0b(n309_I1_t0b),
    .I1_t1b(n309_I1_t1b),
    .O_t0b_t0b(n309_O_t0b_t0b),
    .O_t0b_t1b(n309_O_t0b_t1b),
    .O_t1b_t0b(n309_O_t1b_t0b),
    .O_t1b_t1b(n309_O_t1b_t1b)
  );
  FIFO_4 n310 ( // @[Top.scala 880:22]
    .clock(n310_clock),
    .reset(n310_reset),
    .valid_up(n310_valid_up),
    .valid_down(n310_valid_down),
    .I_t0b_t0b(n310_I_t0b_t0b),
    .I_t0b_t1b(n310_I_t0b_t1b),
    .I_t1b_t0b(n310_I_t1b_t0b),
    .I_t1b_t1b(n310_I_t1b_t1b),
    .O_t0b_t0b(n310_O_t0b_t0b),
    .O_t0b_t1b(n310_O_t0b_t1b),
    .O_t1b_t0b(n310_O_t1b_t0b),
    .O_t1b_t1b(n310_O_t1b_t1b)
  );
  AtomTuple_11 n311 ( // @[Top.scala 883:22]
    .valid_up(n311_valid_up),
    .valid_down(n311_valid_down),
    .I0(n311_I0),
    .I1_t0b_t0b(n311_I1_t0b_t0b),
    .I1_t0b_t1b(n311_I1_t0b_t1b),
    .I1_t1b_t0b(n311_I1_t1b_t0b),
    .I1_t1b_t1b(n311_I1_t1b_t1b),
    .O_t0b(n311_O_t0b),
    .O_t1b_t0b_t0b(n311_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n311_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n311_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n311_O_t1b_t1b_t1b)
  );
  If n312 ( // @[Top.scala 887:22]
    .valid_up(n312_valid_up),
    .valid_down(n312_valid_down),
    .I_t0b(n312_I_t0b),
    .I_t1b_t0b_t0b(n312_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n312_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n312_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n312_I_t1b_t1b_t1b),
    .O_t0b(n312_O_t0b),
    .O_t1b(n312_O_t1b)
  );
  AtomTuple_1 n314 ( // @[Top.scala 890:22]
    .valid_up(n314_valid_up),
    .valid_down(n314_valid_down),
    .I0(n314_I0),
    .I1_t0b(n314_I1_t0b),
    .I1_t1b(n314_I1_t1b),
    .O_t0b(n314_O_t0b),
    .O_t1b_t0b(n314_O_t1b_t0b),
    .O_t1b_t1b(n314_O_t1b_t1b)
  );
  assign valid_down = n314_valid_down; // @[Top.scala 895:16]
  assign O_t0b = n314_O_t0b; // @[Top.scala 894:7]
  assign O_t1b_t0b = n314_O_t1b_t0b; // @[Top.scala 894:7]
  assign O_t1b_t1b = n314_O_t1b_t1b; // @[Top.scala 894:7]
  assign n285_valid_up = valid_up; // @[Top.scala 807:19]
  assign n285_I_t0b = I_t0b; // @[Top.scala 806:12]
  assign n313_clock = clock;
  assign n313_reset = reset;
  assign n313_valid_up = n285_valid_down; // @[Top.scala 810:19]
  assign n313_I = n285_O; // @[Top.scala 809:12]
  assign n301_clock = clock;
  assign n301_reset = reset;
  assign n301_valid_up = n285_valid_down; // @[Top.scala 813:19]
  assign n301_I = n285_O; // @[Top.scala 812:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n287_valid_up = valid_up; // @[Top.scala 817:19]
  assign n287_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 816:12]
  assign n287_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 816:12]
  assign n288_valid_up = n287_valid_down; // @[Top.scala 820:19]
  assign n288_I_t0b = n287_O_t0b; // @[Top.scala 819:12]
  assign n289_valid_up = n287_valid_down; // @[Top.scala 823:19]
  assign n289_I_t1b = n287_O_t1b; // @[Top.scala 822:12]
  assign n290_valid_up = n288_valid_down & n289_valid_down; // @[Top.scala 827:19]
  assign n290_I0 = n288_O; // @[Top.scala 825:13]
  assign n290_I1 = n289_O; // @[Top.scala 826:13]
  assign n291_valid_up = n290_valid_down; // @[Top.scala 830:19]
  assign n291_I_t0b = n290_O_t0b; // @[Top.scala 829:12]
  assign n291_I_t1b = n290_O_t1b; // @[Top.scala 829:12]
  assign n293_valid_up = InitialDelayCounter_valid_down & n291_valid_down; // @[Top.scala 834:19]
  assign n293_I0 = 16'h1; // @[Top.scala 832:13]
  assign n293_I1 = n291_O; // @[Top.scala 833:13]
  assign n294_valid_up = n293_valid_down; // @[Top.scala 837:19]
  assign n294_I_t0b = n293_O_t0b; // @[Top.scala 836:12]
  assign n294_I_t1b = n293_O_t1b; // @[Top.scala 836:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n297_valid_up = n294_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 842:19]
  assign n297_I0 = n294_O; // @[Top.scala 840:13]
  assign n298_valid_up = n297_valid_down; // @[Top.scala 845:19]
  assign n298_I_t0b = n297_O_t0b; // @[Top.scala 844:12]
  assign n299_valid_up = n298_valid_down; // @[Top.scala 849:19]
  assign n299_I0 = n298_O; // @[Top.scala 847:13]
  assign n299_I1 = n298_O; // @[Top.scala 848:13]
  assign n300_clock = clock;
  assign n300_reset = reset;
  assign n300_valid_up = n299_valid_down; // @[Top.scala 852:19]
  assign n300_I_t0b = n299_O_t0b; // @[Top.scala 851:12]
  assign n300_I_t1b = n299_O_t1b; // @[Top.scala 851:12]
  assign n302_valid_up = n301_valid_down & n300_valid_down; // @[Top.scala 856:19]
  assign n302_I0 = n301_O; // @[Top.scala 854:13]
  assign n302_I1 = n300_O; // @[Top.scala 855:13]
  assign n303_valid_up = n302_valid_down; // @[Top.scala 859:19]
  assign n303_I_t0b = n302_O_t0b; // @[Top.scala 858:12]
  assign n303_I_t1b = n302_O_t1b; // @[Top.scala 858:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n305_valid_up = n298_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 864:19]
  assign n305_I0 = n298_O; // @[Top.scala 862:13]
  assign n305_I1 = 16'h1; // @[Top.scala 863:13]
  assign n306_valid_up = n305_valid_down; // @[Top.scala 867:19]
  assign n306_I_t0b = n305_O_t0b; // @[Top.scala 866:12]
  assign n306_I_t1b = n305_O_t1b; // @[Top.scala 866:12]
  assign n307_valid_up = n288_valid_down & n306_valid_down; // @[Top.scala 871:19]
  assign n307_I0 = n288_O; // @[Top.scala 869:13]
  assign n307_I1 = n306_O; // @[Top.scala 870:13]
  assign n308_valid_up = n298_valid_down & n289_valid_down; // @[Top.scala 875:19]
  assign n308_I0 = n298_O; // @[Top.scala 873:13]
  assign n308_I1 = n289_O; // @[Top.scala 874:13]
  assign n309_valid_up = n307_valid_down & n308_valid_down; // @[Top.scala 879:19]
  assign n309_I0_t0b = n307_O_t0b; // @[Top.scala 877:13]
  assign n309_I0_t1b = n307_O_t1b; // @[Top.scala 877:13]
  assign n309_I1_t0b = n308_O_t0b; // @[Top.scala 878:13]
  assign n309_I1_t1b = n308_O_t1b; // @[Top.scala 878:13]
  assign n310_clock = clock;
  assign n310_reset = reset;
  assign n310_valid_up = n309_valid_down; // @[Top.scala 882:19]
  assign n310_I_t0b_t0b = n309_O_t0b_t0b; // @[Top.scala 881:12]
  assign n310_I_t0b_t1b = n309_O_t0b_t1b; // @[Top.scala 881:12]
  assign n310_I_t1b_t0b = n309_O_t1b_t0b; // @[Top.scala 881:12]
  assign n310_I_t1b_t1b = n309_O_t1b_t1b; // @[Top.scala 881:12]
  assign n311_valid_up = n303_valid_down & n310_valid_down; // @[Top.scala 886:19]
  assign n311_I0 = n303_O[0]; // @[Top.scala 884:13]
  assign n311_I1_t0b_t0b = n310_O_t0b_t0b; // @[Top.scala 885:13]
  assign n311_I1_t0b_t1b = n310_O_t0b_t1b; // @[Top.scala 885:13]
  assign n311_I1_t1b_t0b = n310_O_t1b_t0b; // @[Top.scala 885:13]
  assign n311_I1_t1b_t1b = n310_O_t1b_t1b; // @[Top.scala 885:13]
  assign n312_valid_up = n311_valid_down; // @[Top.scala 889:19]
  assign n312_I_t0b = n311_O_t0b; // @[Top.scala 888:12]
  assign n312_I_t1b_t0b_t0b = n311_O_t1b_t0b_t0b; // @[Top.scala 888:12]
  assign n312_I_t1b_t0b_t1b = n311_O_t1b_t0b_t1b; // @[Top.scala 888:12]
  assign n312_I_t1b_t1b_t0b = n311_O_t1b_t1b_t0b; // @[Top.scala 888:12]
  assign n312_I_t1b_t1b_t1b = n311_O_t1b_t1b_t1b; // @[Top.scala 888:12]
  assign n314_valid_up = n313_valid_down & n312_valid_down; // @[Top.scala 893:19]
  assign n314_I0 = n313_O; // @[Top.scala 891:13]
  assign n314_I1_t0b = n312_O_t0b; // @[Top.scala 892:13]
  assign n314_I1_t1b = n312_O_t1b; // @[Top.scala 892:13]
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
  wire  n319_valid_up; // @[Top.scala 901:22]
  wire  n319_valid_down; // @[Top.scala 901:22]
  wire [15:0] n319_I_t0b; // @[Top.scala 901:22]
  wire [15:0] n319_O; // @[Top.scala 901:22]
  wire  n347_clock; // @[Top.scala 904:22]
  wire  n347_reset; // @[Top.scala 904:22]
  wire  n347_valid_up; // @[Top.scala 904:22]
  wire  n347_valid_down; // @[Top.scala 904:22]
  wire [15:0] n347_I; // @[Top.scala 904:22]
  wire [15:0] n347_O; // @[Top.scala 904:22]
  wire  n335_clock; // @[Top.scala 907:22]
  wire  n335_reset; // @[Top.scala 907:22]
  wire  n335_valid_up; // @[Top.scala 907:22]
  wire  n335_valid_down; // @[Top.scala 907:22]
  wire [15:0] n335_I; // @[Top.scala 907:22]
  wire [15:0] n335_O; // @[Top.scala 907:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n321_valid_up; // @[Top.scala 911:22]
  wire  n321_valid_down; // @[Top.scala 911:22]
  wire [15:0] n321_I_t1b_t0b; // @[Top.scala 911:22]
  wire [15:0] n321_I_t1b_t1b; // @[Top.scala 911:22]
  wire [15:0] n321_O_t0b; // @[Top.scala 911:22]
  wire [15:0] n321_O_t1b; // @[Top.scala 911:22]
  wire  n322_valid_up; // @[Top.scala 914:22]
  wire  n322_valid_down; // @[Top.scala 914:22]
  wire [15:0] n322_I_t0b; // @[Top.scala 914:22]
  wire [15:0] n322_O; // @[Top.scala 914:22]
  wire  n323_valid_up; // @[Top.scala 917:22]
  wire  n323_valid_down; // @[Top.scala 917:22]
  wire [15:0] n323_I_t1b; // @[Top.scala 917:22]
  wire [15:0] n323_O; // @[Top.scala 917:22]
  wire  n324_valid_up; // @[Top.scala 920:22]
  wire  n324_valid_down; // @[Top.scala 920:22]
  wire [15:0] n324_I0; // @[Top.scala 920:22]
  wire [15:0] n324_I1; // @[Top.scala 920:22]
  wire [15:0] n324_O_t0b; // @[Top.scala 920:22]
  wire [15:0] n324_O_t1b; // @[Top.scala 920:22]
  wire  n325_valid_up; // @[Top.scala 924:22]
  wire  n325_valid_down; // @[Top.scala 924:22]
  wire [15:0] n325_I_t0b; // @[Top.scala 924:22]
  wire [15:0] n325_I_t1b; // @[Top.scala 924:22]
  wire [15:0] n325_O; // @[Top.scala 924:22]
  wire  n327_valid_up; // @[Top.scala 927:22]
  wire  n327_valid_down; // @[Top.scala 927:22]
  wire [15:0] n327_I0; // @[Top.scala 927:22]
  wire [15:0] n327_I1; // @[Top.scala 927:22]
  wire [15:0] n327_O_t0b; // @[Top.scala 927:22]
  wire [15:0] n327_O_t1b; // @[Top.scala 927:22]
  wire  n328_valid_up; // @[Top.scala 931:22]
  wire  n328_valid_down; // @[Top.scala 931:22]
  wire [15:0] n328_I_t0b; // @[Top.scala 931:22]
  wire [15:0] n328_I_t1b; // @[Top.scala 931:22]
  wire [15:0] n328_O; // @[Top.scala 931:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n331_valid_up; // @[Top.scala 935:22]
  wire  n331_valid_down; // @[Top.scala 935:22]
  wire [15:0] n331_I0; // @[Top.scala 935:22]
  wire [15:0] n331_O_t0b; // @[Top.scala 935:22]
  wire  n332_valid_up; // @[Top.scala 939:22]
  wire  n332_valid_down; // @[Top.scala 939:22]
  wire [15:0] n332_I_t0b; // @[Top.scala 939:22]
  wire [15:0] n332_O; // @[Top.scala 939:22]
  wire  n333_valid_up; // @[Top.scala 942:22]
  wire  n333_valid_down; // @[Top.scala 942:22]
  wire [15:0] n333_I0; // @[Top.scala 942:22]
  wire [15:0] n333_I1; // @[Top.scala 942:22]
  wire [15:0] n333_O_t0b; // @[Top.scala 942:22]
  wire [15:0] n333_O_t1b; // @[Top.scala 942:22]
  wire  n334_clock; // @[Top.scala 946:22]
  wire  n334_reset; // @[Top.scala 946:22]
  wire  n334_valid_up; // @[Top.scala 946:22]
  wire  n334_valid_down; // @[Top.scala 946:22]
  wire [15:0] n334_I_t0b; // @[Top.scala 946:22]
  wire [15:0] n334_I_t1b; // @[Top.scala 946:22]
  wire [15:0] n334_O; // @[Top.scala 946:22]
  wire  n336_valid_up; // @[Top.scala 949:22]
  wire  n336_valid_down; // @[Top.scala 949:22]
  wire [15:0] n336_I0; // @[Top.scala 949:22]
  wire [15:0] n336_I1; // @[Top.scala 949:22]
  wire [15:0] n336_O_t0b; // @[Top.scala 949:22]
  wire [15:0] n336_O_t1b; // @[Top.scala 949:22]
  wire  n337_valid_up; // @[Top.scala 953:22]
  wire  n337_valid_down; // @[Top.scala 953:22]
  wire [15:0] n337_I_t0b; // @[Top.scala 953:22]
  wire [15:0] n337_I_t1b; // @[Top.scala 953:22]
  wire [15:0] n337_O; // @[Top.scala 953:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n339_valid_up; // @[Top.scala 957:22]
  wire  n339_valid_down; // @[Top.scala 957:22]
  wire [15:0] n339_I0; // @[Top.scala 957:22]
  wire [15:0] n339_I1; // @[Top.scala 957:22]
  wire [15:0] n339_O_t0b; // @[Top.scala 957:22]
  wire [15:0] n339_O_t1b; // @[Top.scala 957:22]
  wire  n340_valid_up; // @[Top.scala 961:22]
  wire  n340_valid_down; // @[Top.scala 961:22]
  wire [15:0] n340_I_t0b; // @[Top.scala 961:22]
  wire [15:0] n340_I_t1b; // @[Top.scala 961:22]
  wire [15:0] n340_O; // @[Top.scala 961:22]
  wire  n341_valid_up; // @[Top.scala 964:22]
  wire  n341_valid_down; // @[Top.scala 964:22]
  wire [15:0] n341_I0; // @[Top.scala 964:22]
  wire [15:0] n341_I1; // @[Top.scala 964:22]
  wire [15:0] n341_O_t0b; // @[Top.scala 964:22]
  wire [15:0] n341_O_t1b; // @[Top.scala 964:22]
  wire  n342_valid_up; // @[Top.scala 968:22]
  wire  n342_valid_down; // @[Top.scala 968:22]
  wire [15:0] n342_I0; // @[Top.scala 968:22]
  wire [15:0] n342_I1; // @[Top.scala 968:22]
  wire [15:0] n342_O_t0b; // @[Top.scala 968:22]
  wire [15:0] n342_O_t1b; // @[Top.scala 968:22]
  wire  n343_valid_up; // @[Top.scala 972:22]
  wire  n343_valid_down; // @[Top.scala 972:22]
  wire [15:0] n343_I0_t0b; // @[Top.scala 972:22]
  wire [15:0] n343_I0_t1b; // @[Top.scala 972:22]
  wire [15:0] n343_I1_t0b; // @[Top.scala 972:22]
  wire [15:0] n343_I1_t1b; // @[Top.scala 972:22]
  wire [15:0] n343_O_t0b_t0b; // @[Top.scala 972:22]
  wire [15:0] n343_O_t0b_t1b; // @[Top.scala 972:22]
  wire [15:0] n343_O_t1b_t0b; // @[Top.scala 972:22]
  wire [15:0] n343_O_t1b_t1b; // @[Top.scala 972:22]
  wire  n344_clock; // @[Top.scala 976:22]
  wire  n344_reset; // @[Top.scala 976:22]
  wire  n344_valid_up; // @[Top.scala 976:22]
  wire  n344_valid_down; // @[Top.scala 976:22]
  wire [15:0] n344_I_t0b_t0b; // @[Top.scala 976:22]
  wire [15:0] n344_I_t0b_t1b; // @[Top.scala 976:22]
  wire [15:0] n344_I_t1b_t0b; // @[Top.scala 976:22]
  wire [15:0] n344_I_t1b_t1b; // @[Top.scala 976:22]
  wire [15:0] n344_O_t0b_t0b; // @[Top.scala 976:22]
  wire [15:0] n344_O_t0b_t1b; // @[Top.scala 976:22]
  wire [15:0] n344_O_t1b_t0b; // @[Top.scala 976:22]
  wire [15:0] n344_O_t1b_t1b; // @[Top.scala 976:22]
  wire  n345_valid_up; // @[Top.scala 979:22]
  wire  n345_valid_down; // @[Top.scala 979:22]
  wire  n345_I0; // @[Top.scala 979:22]
  wire [15:0] n345_I1_t0b_t0b; // @[Top.scala 979:22]
  wire [15:0] n345_I1_t0b_t1b; // @[Top.scala 979:22]
  wire [15:0] n345_I1_t1b_t0b; // @[Top.scala 979:22]
  wire [15:0] n345_I1_t1b_t1b; // @[Top.scala 979:22]
  wire  n345_O_t0b; // @[Top.scala 979:22]
  wire [15:0] n345_O_t1b_t0b_t0b; // @[Top.scala 979:22]
  wire [15:0] n345_O_t1b_t0b_t1b; // @[Top.scala 979:22]
  wire [15:0] n345_O_t1b_t1b_t0b; // @[Top.scala 979:22]
  wire [15:0] n345_O_t1b_t1b_t1b; // @[Top.scala 979:22]
  wire  n346_valid_up; // @[Top.scala 983:22]
  wire  n346_valid_down; // @[Top.scala 983:22]
  wire  n346_I_t0b; // @[Top.scala 983:22]
  wire [15:0] n346_I_t1b_t0b_t0b; // @[Top.scala 983:22]
  wire [15:0] n346_I_t1b_t0b_t1b; // @[Top.scala 983:22]
  wire [15:0] n346_I_t1b_t1b_t0b; // @[Top.scala 983:22]
  wire [15:0] n346_I_t1b_t1b_t1b; // @[Top.scala 983:22]
  wire [15:0] n346_O_t0b; // @[Top.scala 983:22]
  wire [15:0] n346_O_t1b; // @[Top.scala 983:22]
  wire  n348_valid_up; // @[Top.scala 986:22]
  wire  n348_valid_down; // @[Top.scala 986:22]
  wire [15:0] n348_I0; // @[Top.scala 986:22]
  wire [15:0] n348_I1_t0b; // @[Top.scala 986:22]
  wire [15:0] n348_I1_t1b; // @[Top.scala 986:22]
  wire [15:0] n348_O_t0b; // @[Top.scala 986:22]
  wire [15:0] n348_O_t1b_t0b; // @[Top.scala 986:22]
  wire [15:0] n348_O_t1b_t1b; // @[Top.scala 986:22]
  Fst n319 ( // @[Top.scala 901:22]
    .valid_up(n319_valid_up),
    .valid_down(n319_valid_down),
    .I_t0b(n319_I_t0b),
    .O(n319_O)
  );
  FIFO_2 n347 ( // @[Top.scala 904:22]
    .clock(n347_clock),
    .reset(n347_reset),
    .valid_up(n347_valid_up),
    .valid_down(n347_valid_down),
    .I(n347_I),
    .O(n347_O)
  );
  FIFO_2 n335 ( // @[Top.scala 907:22]
    .clock(n335_clock),
    .reset(n335_reset),
    .valid_up(n335_valid_up),
    .valid_down(n335_valid_down),
    .I(n335_I),
    .O(n335_O)
  );
  InitialDelayCounter_27 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n321 ( // @[Top.scala 911:22]
    .valid_up(n321_valid_up),
    .valid_down(n321_valid_down),
    .I_t1b_t0b(n321_I_t1b_t0b),
    .I_t1b_t1b(n321_I_t1b_t1b),
    .O_t0b(n321_O_t0b),
    .O_t1b(n321_O_t1b)
  );
  Fst_1 n322 ( // @[Top.scala 914:22]
    .valid_up(n322_valid_up),
    .valid_down(n322_valid_down),
    .I_t0b(n322_I_t0b),
    .O(n322_O)
  );
  Snd_1 n323 ( // @[Top.scala 917:22]
    .valid_up(n323_valid_up),
    .valid_down(n323_valid_down),
    .I_t1b(n323_I_t1b),
    .O(n323_O)
  );
  AtomTuple n324 ( // @[Top.scala 920:22]
    .valid_up(n324_valid_up),
    .valid_down(n324_valid_down),
    .I0(n324_I0),
    .I1(n324_I1),
    .O_t0b(n324_O_t0b),
    .O_t1b(n324_O_t1b)
  );
  Add n325 ( // @[Top.scala 924:22]
    .valid_up(n325_valid_up),
    .valid_down(n325_valid_down),
    .I_t0b(n325_I_t0b),
    .I_t1b(n325_I_t1b),
    .O(n325_O)
  );
  AtomTuple n327 ( // @[Top.scala 927:22]
    .valid_up(n327_valid_up),
    .valid_down(n327_valid_down),
    .I0(n327_I0),
    .I1(n327_I1),
    .O_t0b(n327_O_t0b),
    .O_t1b(n327_O_t1b)
  );
  Add n328 ( // @[Top.scala 931:22]
    .valid_up(n328_valid_up),
    .valid_down(n328_valid_down),
    .I_t0b(n328_I_t0b),
    .I_t1b(n328_I_t1b),
    .O(n328_O)
  );
  InitialDelayCounter_27 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n331 ( // @[Top.scala 935:22]
    .valid_up(n331_valid_up),
    .valid_down(n331_valid_down),
    .I0(n331_I0),
    .O_t0b(n331_O_t0b)
  );
  RShift n332 ( // @[Top.scala 939:22]
    .valid_up(n332_valid_up),
    .valid_down(n332_valid_down),
    .I_t0b(n332_I_t0b),
    .O(n332_O)
  );
  AtomTuple n333 ( // @[Top.scala 942:22]
    .valid_up(n333_valid_up),
    .valid_down(n333_valid_down),
    .I0(n333_I0),
    .I1(n333_I1),
    .O_t0b(n333_O_t0b),
    .O_t1b(n333_O_t1b)
  );
  Mul n334 ( // @[Top.scala 946:22]
    .clock(n334_clock),
    .reset(n334_reset),
    .valid_up(n334_valid_up),
    .valid_down(n334_valid_down),
    .I_t0b(n334_I_t0b),
    .I_t1b(n334_I_t1b),
    .O(n334_O)
  );
  AtomTuple n336 ( // @[Top.scala 949:22]
    .valid_up(n336_valid_up),
    .valid_down(n336_valid_down),
    .I0(n336_I0),
    .I1(n336_I1),
    .O_t0b(n336_O_t0b),
    .O_t1b(n336_O_t1b)
  );
  Lt n337 ( // @[Top.scala 953:22]
    .valid_up(n337_valid_up),
    .valid_down(n337_valid_down),
    .I_t0b(n337_I_t0b),
    .I_t1b(n337_I_t1b),
    .O(n337_O)
  );
  InitialDelayCounter_27 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n339 ( // @[Top.scala 957:22]
    .valid_up(n339_valid_up),
    .valid_down(n339_valid_down),
    .I0(n339_I0),
    .I1(n339_I1),
    .O_t0b(n339_O_t0b),
    .O_t1b(n339_O_t1b)
  );
  Sub n340 ( // @[Top.scala 961:22]
    .valid_up(n340_valid_up),
    .valid_down(n340_valid_down),
    .I_t0b(n340_I_t0b),
    .I_t1b(n340_I_t1b),
    .O(n340_O)
  );
  AtomTuple n341 ( // @[Top.scala 964:22]
    .valid_up(n341_valid_up),
    .valid_down(n341_valid_down),
    .I0(n341_I0),
    .I1(n341_I1),
    .O_t0b(n341_O_t0b),
    .O_t1b(n341_O_t1b)
  );
  AtomTuple n342 ( // @[Top.scala 968:22]
    .valid_up(n342_valid_up),
    .valid_down(n342_valid_down),
    .I0(n342_I0),
    .I1(n342_I1),
    .O_t0b(n342_O_t0b),
    .O_t1b(n342_O_t1b)
  );
  AtomTuple_10 n343 ( // @[Top.scala 972:22]
    .valid_up(n343_valid_up),
    .valid_down(n343_valid_down),
    .I0_t0b(n343_I0_t0b),
    .I0_t1b(n343_I0_t1b),
    .I1_t0b(n343_I1_t0b),
    .I1_t1b(n343_I1_t1b),
    .O_t0b_t0b(n343_O_t0b_t0b),
    .O_t0b_t1b(n343_O_t0b_t1b),
    .O_t1b_t0b(n343_O_t1b_t0b),
    .O_t1b_t1b(n343_O_t1b_t1b)
  );
  FIFO_4 n344 ( // @[Top.scala 976:22]
    .clock(n344_clock),
    .reset(n344_reset),
    .valid_up(n344_valid_up),
    .valid_down(n344_valid_down),
    .I_t0b_t0b(n344_I_t0b_t0b),
    .I_t0b_t1b(n344_I_t0b_t1b),
    .I_t1b_t0b(n344_I_t1b_t0b),
    .I_t1b_t1b(n344_I_t1b_t1b),
    .O_t0b_t0b(n344_O_t0b_t0b),
    .O_t0b_t1b(n344_O_t0b_t1b),
    .O_t1b_t0b(n344_O_t1b_t0b),
    .O_t1b_t1b(n344_O_t1b_t1b)
  );
  AtomTuple_11 n345 ( // @[Top.scala 979:22]
    .valid_up(n345_valid_up),
    .valid_down(n345_valid_down),
    .I0(n345_I0),
    .I1_t0b_t0b(n345_I1_t0b_t0b),
    .I1_t0b_t1b(n345_I1_t0b_t1b),
    .I1_t1b_t0b(n345_I1_t1b_t0b),
    .I1_t1b_t1b(n345_I1_t1b_t1b),
    .O_t0b(n345_O_t0b),
    .O_t1b_t0b_t0b(n345_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n345_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n345_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n345_O_t1b_t1b_t1b)
  );
  If n346 ( // @[Top.scala 983:22]
    .valid_up(n346_valid_up),
    .valid_down(n346_valid_down),
    .I_t0b(n346_I_t0b),
    .I_t1b_t0b_t0b(n346_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n346_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n346_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n346_I_t1b_t1b_t1b),
    .O_t0b(n346_O_t0b),
    .O_t1b(n346_O_t1b)
  );
  AtomTuple_1 n348 ( // @[Top.scala 986:22]
    .valid_up(n348_valid_up),
    .valid_down(n348_valid_down),
    .I0(n348_I0),
    .I1_t0b(n348_I1_t0b),
    .I1_t1b(n348_I1_t1b),
    .O_t0b(n348_O_t0b),
    .O_t1b_t0b(n348_O_t1b_t0b),
    .O_t1b_t1b(n348_O_t1b_t1b)
  );
  assign valid_down = n348_valid_down; // @[Top.scala 991:16]
  assign O_t0b = n348_O_t0b; // @[Top.scala 990:7]
  assign O_t1b_t0b = n348_O_t1b_t0b; // @[Top.scala 990:7]
  assign O_t1b_t1b = n348_O_t1b_t1b; // @[Top.scala 990:7]
  assign n319_valid_up = valid_up; // @[Top.scala 903:19]
  assign n319_I_t0b = I_t0b; // @[Top.scala 902:12]
  assign n347_clock = clock;
  assign n347_reset = reset;
  assign n347_valid_up = n319_valid_down; // @[Top.scala 906:19]
  assign n347_I = n319_O; // @[Top.scala 905:12]
  assign n335_clock = clock;
  assign n335_reset = reset;
  assign n335_valid_up = n319_valid_down; // @[Top.scala 909:19]
  assign n335_I = n319_O; // @[Top.scala 908:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n321_valid_up = valid_up; // @[Top.scala 913:19]
  assign n321_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 912:12]
  assign n321_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 912:12]
  assign n322_valid_up = n321_valid_down; // @[Top.scala 916:19]
  assign n322_I_t0b = n321_O_t0b; // @[Top.scala 915:12]
  assign n323_valid_up = n321_valid_down; // @[Top.scala 919:19]
  assign n323_I_t1b = n321_O_t1b; // @[Top.scala 918:12]
  assign n324_valid_up = n322_valid_down & n323_valid_down; // @[Top.scala 923:19]
  assign n324_I0 = n322_O; // @[Top.scala 921:13]
  assign n324_I1 = n323_O; // @[Top.scala 922:13]
  assign n325_valid_up = n324_valid_down; // @[Top.scala 926:19]
  assign n325_I_t0b = n324_O_t0b; // @[Top.scala 925:12]
  assign n325_I_t1b = n324_O_t1b; // @[Top.scala 925:12]
  assign n327_valid_up = InitialDelayCounter_valid_down & n325_valid_down; // @[Top.scala 930:19]
  assign n327_I0 = 16'h1; // @[Top.scala 928:13]
  assign n327_I1 = n325_O; // @[Top.scala 929:13]
  assign n328_valid_up = n327_valid_down; // @[Top.scala 933:19]
  assign n328_I_t0b = n327_O_t0b; // @[Top.scala 932:12]
  assign n328_I_t1b = n327_O_t1b; // @[Top.scala 932:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n331_valid_up = n328_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 938:19]
  assign n331_I0 = n328_O; // @[Top.scala 936:13]
  assign n332_valid_up = n331_valid_down; // @[Top.scala 941:19]
  assign n332_I_t0b = n331_O_t0b; // @[Top.scala 940:12]
  assign n333_valid_up = n332_valid_down; // @[Top.scala 945:19]
  assign n333_I0 = n332_O; // @[Top.scala 943:13]
  assign n333_I1 = n332_O; // @[Top.scala 944:13]
  assign n334_clock = clock;
  assign n334_reset = reset;
  assign n334_valid_up = n333_valid_down; // @[Top.scala 948:19]
  assign n334_I_t0b = n333_O_t0b; // @[Top.scala 947:12]
  assign n334_I_t1b = n333_O_t1b; // @[Top.scala 947:12]
  assign n336_valid_up = n335_valid_down & n334_valid_down; // @[Top.scala 952:19]
  assign n336_I0 = n335_O; // @[Top.scala 950:13]
  assign n336_I1 = n334_O; // @[Top.scala 951:13]
  assign n337_valid_up = n336_valid_down; // @[Top.scala 955:19]
  assign n337_I_t0b = n336_O_t0b; // @[Top.scala 954:12]
  assign n337_I_t1b = n336_O_t1b; // @[Top.scala 954:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n339_valid_up = n332_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 960:19]
  assign n339_I0 = n332_O; // @[Top.scala 958:13]
  assign n339_I1 = 16'h1; // @[Top.scala 959:13]
  assign n340_valid_up = n339_valid_down; // @[Top.scala 963:19]
  assign n340_I_t0b = n339_O_t0b; // @[Top.scala 962:12]
  assign n340_I_t1b = n339_O_t1b; // @[Top.scala 962:12]
  assign n341_valid_up = n322_valid_down & n340_valid_down; // @[Top.scala 967:19]
  assign n341_I0 = n322_O; // @[Top.scala 965:13]
  assign n341_I1 = n340_O; // @[Top.scala 966:13]
  assign n342_valid_up = n332_valid_down & n323_valid_down; // @[Top.scala 971:19]
  assign n342_I0 = n332_O; // @[Top.scala 969:13]
  assign n342_I1 = n323_O; // @[Top.scala 970:13]
  assign n343_valid_up = n341_valid_down & n342_valid_down; // @[Top.scala 975:19]
  assign n343_I0_t0b = n341_O_t0b; // @[Top.scala 973:13]
  assign n343_I0_t1b = n341_O_t1b; // @[Top.scala 973:13]
  assign n343_I1_t0b = n342_O_t0b; // @[Top.scala 974:13]
  assign n343_I1_t1b = n342_O_t1b; // @[Top.scala 974:13]
  assign n344_clock = clock;
  assign n344_reset = reset;
  assign n344_valid_up = n343_valid_down; // @[Top.scala 978:19]
  assign n344_I_t0b_t0b = n343_O_t0b_t0b; // @[Top.scala 977:12]
  assign n344_I_t0b_t1b = n343_O_t0b_t1b; // @[Top.scala 977:12]
  assign n344_I_t1b_t0b = n343_O_t1b_t0b; // @[Top.scala 977:12]
  assign n344_I_t1b_t1b = n343_O_t1b_t1b; // @[Top.scala 977:12]
  assign n345_valid_up = n337_valid_down & n344_valid_down; // @[Top.scala 982:19]
  assign n345_I0 = n337_O[0]; // @[Top.scala 980:13]
  assign n345_I1_t0b_t0b = n344_O_t0b_t0b; // @[Top.scala 981:13]
  assign n345_I1_t0b_t1b = n344_O_t0b_t1b; // @[Top.scala 981:13]
  assign n345_I1_t1b_t0b = n344_O_t1b_t0b; // @[Top.scala 981:13]
  assign n345_I1_t1b_t1b = n344_O_t1b_t1b; // @[Top.scala 981:13]
  assign n346_valid_up = n345_valid_down; // @[Top.scala 985:19]
  assign n346_I_t0b = n345_O_t0b; // @[Top.scala 984:12]
  assign n346_I_t1b_t0b_t0b = n345_O_t1b_t0b_t0b; // @[Top.scala 984:12]
  assign n346_I_t1b_t0b_t1b = n345_O_t1b_t0b_t1b; // @[Top.scala 984:12]
  assign n346_I_t1b_t1b_t0b = n345_O_t1b_t1b_t0b; // @[Top.scala 984:12]
  assign n346_I_t1b_t1b_t1b = n345_O_t1b_t1b_t1b; // @[Top.scala 984:12]
  assign n348_valid_up = n347_valid_down & n346_valid_down; // @[Top.scala 989:19]
  assign n348_I0 = n347_O; // @[Top.scala 987:13]
  assign n348_I1_t0b = n346_O_t0b; // @[Top.scala 988:13]
  assign n348_I1_t1b = n346_O_t1b; // @[Top.scala 988:13]
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
  wire  n353_valid_up; // @[Top.scala 997:22]
  wire  n353_valid_down; // @[Top.scala 997:22]
  wire [15:0] n353_I_t0b; // @[Top.scala 997:22]
  wire [15:0] n353_O; // @[Top.scala 997:22]
  wire  n381_clock; // @[Top.scala 1000:22]
  wire  n381_reset; // @[Top.scala 1000:22]
  wire  n381_valid_up; // @[Top.scala 1000:22]
  wire  n381_valid_down; // @[Top.scala 1000:22]
  wire [15:0] n381_I; // @[Top.scala 1000:22]
  wire [15:0] n381_O; // @[Top.scala 1000:22]
  wire  n369_clock; // @[Top.scala 1003:22]
  wire  n369_reset; // @[Top.scala 1003:22]
  wire  n369_valid_up; // @[Top.scala 1003:22]
  wire  n369_valid_down; // @[Top.scala 1003:22]
  wire [15:0] n369_I; // @[Top.scala 1003:22]
  wire [15:0] n369_O; // @[Top.scala 1003:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n355_valid_up; // @[Top.scala 1007:22]
  wire  n355_valid_down; // @[Top.scala 1007:22]
  wire [15:0] n355_I_t1b_t0b; // @[Top.scala 1007:22]
  wire [15:0] n355_I_t1b_t1b; // @[Top.scala 1007:22]
  wire [15:0] n355_O_t0b; // @[Top.scala 1007:22]
  wire [15:0] n355_O_t1b; // @[Top.scala 1007:22]
  wire  n356_valid_up; // @[Top.scala 1010:22]
  wire  n356_valid_down; // @[Top.scala 1010:22]
  wire [15:0] n356_I_t0b; // @[Top.scala 1010:22]
  wire [15:0] n356_O; // @[Top.scala 1010:22]
  wire  n357_valid_up; // @[Top.scala 1013:22]
  wire  n357_valid_down; // @[Top.scala 1013:22]
  wire [15:0] n357_I_t1b; // @[Top.scala 1013:22]
  wire [15:0] n357_O; // @[Top.scala 1013:22]
  wire  n358_valid_up; // @[Top.scala 1016:22]
  wire  n358_valid_down; // @[Top.scala 1016:22]
  wire [15:0] n358_I0; // @[Top.scala 1016:22]
  wire [15:0] n358_I1; // @[Top.scala 1016:22]
  wire [15:0] n358_O_t0b; // @[Top.scala 1016:22]
  wire [15:0] n358_O_t1b; // @[Top.scala 1016:22]
  wire  n359_valid_up; // @[Top.scala 1020:22]
  wire  n359_valid_down; // @[Top.scala 1020:22]
  wire [15:0] n359_I_t0b; // @[Top.scala 1020:22]
  wire [15:0] n359_I_t1b; // @[Top.scala 1020:22]
  wire [15:0] n359_O; // @[Top.scala 1020:22]
  wire  n361_valid_up; // @[Top.scala 1023:22]
  wire  n361_valid_down; // @[Top.scala 1023:22]
  wire [15:0] n361_I0; // @[Top.scala 1023:22]
  wire [15:0] n361_I1; // @[Top.scala 1023:22]
  wire [15:0] n361_O_t0b; // @[Top.scala 1023:22]
  wire [15:0] n361_O_t1b; // @[Top.scala 1023:22]
  wire  n362_valid_up; // @[Top.scala 1027:22]
  wire  n362_valid_down; // @[Top.scala 1027:22]
  wire [15:0] n362_I_t0b; // @[Top.scala 1027:22]
  wire [15:0] n362_I_t1b; // @[Top.scala 1027:22]
  wire [15:0] n362_O; // @[Top.scala 1027:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n365_valid_up; // @[Top.scala 1031:22]
  wire  n365_valid_down; // @[Top.scala 1031:22]
  wire [15:0] n365_I0; // @[Top.scala 1031:22]
  wire [15:0] n365_O_t0b; // @[Top.scala 1031:22]
  wire  n366_valid_up; // @[Top.scala 1035:22]
  wire  n366_valid_down; // @[Top.scala 1035:22]
  wire [15:0] n366_I_t0b; // @[Top.scala 1035:22]
  wire [15:0] n366_O; // @[Top.scala 1035:22]
  wire  n367_valid_up; // @[Top.scala 1038:22]
  wire  n367_valid_down; // @[Top.scala 1038:22]
  wire [15:0] n367_I0; // @[Top.scala 1038:22]
  wire [15:0] n367_I1; // @[Top.scala 1038:22]
  wire [15:0] n367_O_t0b; // @[Top.scala 1038:22]
  wire [15:0] n367_O_t1b; // @[Top.scala 1038:22]
  wire  n368_clock; // @[Top.scala 1042:22]
  wire  n368_reset; // @[Top.scala 1042:22]
  wire  n368_valid_up; // @[Top.scala 1042:22]
  wire  n368_valid_down; // @[Top.scala 1042:22]
  wire [15:0] n368_I_t0b; // @[Top.scala 1042:22]
  wire [15:0] n368_I_t1b; // @[Top.scala 1042:22]
  wire [15:0] n368_O; // @[Top.scala 1042:22]
  wire  n370_valid_up; // @[Top.scala 1045:22]
  wire  n370_valid_down; // @[Top.scala 1045:22]
  wire [15:0] n370_I0; // @[Top.scala 1045:22]
  wire [15:0] n370_I1; // @[Top.scala 1045:22]
  wire [15:0] n370_O_t0b; // @[Top.scala 1045:22]
  wire [15:0] n370_O_t1b; // @[Top.scala 1045:22]
  wire  n371_valid_up; // @[Top.scala 1049:22]
  wire  n371_valid_down; // @[Top.scala 1049:22]
  wire [15:0] n371_I_t0b; // @[Top.scala 1049:22]
  wire [15:0] n371_I_t1b; // @[Top.scala 1049:22]
  wire [15:0] n371_O; // @[Top.scala 1049:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n373_valid_up; // @[Top.scala 1053:22]
  wire  n373_valid_down; // @[Top.scala 1053:22]
  wire [15:0] n373_I0; // @[Top.scala 1053:22]
  wire [15:0] n373_I1; // @[Top.scala 1053:22]
  wire [15:0] n373_O_t0b; // @[Top.scala 1053:22]
  wire [15:0] n373_O_t1b; // @[Top.scala 1053:22]
  wire  n374_valid_up; // @[Top.scala 1057:22]
  wire  n374_valid_down; // @[Top.scala 1057:22]
  wire [15:0] n374_I_t0b; // @[Top.scala 1057:22]
  wire [15:0] n374_I_t1b; // @[Top.scala 1057:22]
  wire [15:0] n374_O; // @[Top.scala 1057:22]
  wire  n375_valid_up; // @[Top.scala 1060:22]
  wire  n375_valid_down; // @[Top.scala 1060:22]
  wire [15:0] n375_I0; // @[Top.scala 1060:22]
  wire [15:0] n375_I1; // @[Top.scala 1060:22]
  wire [15:0] n375_O_t0b; // @[Top.scala 1060:22]
  wire [15:0] n375_O_t1b; // @[Top.scala 1060:22]
  wire  n376_valid_up; // @[Top.scala 1064:22]
  wire  n376_valid_down; // @[Top.scala 1064:22]
  wire [15:0] n376_I0; // @[Top.scala 1064:22]
  wire [15:0] n376_I1; // @[Top.scala 1064:22]
  wire [15:0] n376_O_t0b; // @[Top.scala 1064:22]
  wire [15:0] n376_O_t1b; // @[Top.scala 1064:22]
  wire  n377_valid_up; // @[Top.scala 1068:22]
  wire  n377_valid_down; // @[Top.scala 1068:22]
  wire [15:0] n377_I0_t0b; // @[Top.scala 1068:22]
  wire [15:0] n377_I0_t1b; // @[Top.scala 1068:22]
  wire [15:0] n377_I1_t0b; // @[Top.scala 1068:22]
  wire [15:0] n377_I1_t1b; // @[Top.scala 1068:22]
  wire [15:0] n377_O_t0b_t0b; // @[Top.scala 1068:22]
  wire [15:0] n377_O_t0b_t1b; // @[Top.scala 1068:22]
  wire [15:0] n377_O_t1b_t0b; // @[Top.scala 1068:22]
  wire [15:0] n377_O_t1b_t1b; // @[Top.scala 1068:22]
  wire  n378_clock; // @[Top.scala 1072:22]
  wire  n378_reset; // @[Top.scala 1072:22]
  wire  n378_valid_up; // @[Top.scala 1072:22]
  wire  n378_valid_down; // @[Top.scala 1072:22]
  wire [15:0] n378_I_t0b_t0b; // @[Top.scala 1072:22]
  wire [15:0] n378_I_t0b_t1b; // @[Top.scala 1072:22]
  wire [15:0] n378_I_t1b_t0b; // @[Top.scala 1072:22]
  wire [15:0] n378_I_t1b_t1b; // @[Top.scala 1072:22]
  wire [15:0] n378_O_t0b_t0b; // @[Top.scala 1072:22]
  wire [15:0] n378_O_t0b_t1b; // @[Top.scala 1072:22]
  wire [15:0] n378_O_t1b_t0b; // @[Top.scala 1072:22]
  wire [15:0] n378_O_t1b_t1b; // @[Top.scala 1072:22]
  wire  n379_valid_up; // @[Top.scala 1075:22]
  wire  n379_valid_down; // @[Top.scala 1075:22]
  wire  n379_I0; // @[Top.scala 1075:22]
  wire [15:0] n379_I1_t0b_t0b; // @[Top.scala 1075:22]
  wire [15:0] n379_I1_t0b_t1b; // @[Top.scala 1075:22]
  wire [15:0] n379_I1_t1b_t0b; // @[Top.scala 1075:22]
  wire [15:0] n379_I1_t1b_t1b; // @[Top.scala 1075:22]
  wire  n379_O_t0b; // @[Top.scala 1075:22]
  wire [15:0] n379_O_t1b_t0b_t0b; // @[Top.scala 1075:22]
  wire [15:0] n379_O_t1b_t0b_t1b; // @[Top.scala 1075:22]
  wire [15:0] n379_O_t1b_t1b_t0b; // @[Top.scala 1075:22]
  wire [15:0] n379_O_t1b_t1b_t1b; // @[Top.scala 1075:22]
  wire  n380_valid_up; // @[Top.scala 1079:22]
  wire  n380_valid_down; // @[Top.scala 1079:22]
  wire  n380_I_t0b; // @[Top.scala 1079:22]
  wire [15:0] n380_I_t1b_t0b_t0b; // @[Top.scala 1079:22]
  wire [15:0] n380_I_t1b_t0b_t1b; // @[Top.scala 1079:22]
  wire [15:0] n380_I_t1b_t1b_t0b; // @[Top.scala 1079:22]
  wire [15:0] n380_I_t1b_t1b_t1b; // @[Top.scala 1079:22]
  wire [15:0] n380_O_t0b; // @[Top.scala 1079:22]
  wire [15:0] n380_O_t1b; // @[Top.scala 1079:22]
  wire  n382_valid_up; // @[Top.scala 1082:22]
  wire  n382_valid_down; // @[Top.scala 1082:22]
  wire [15:0] n382_I0; // @[Top.scala 1082:22]
  wire [15:0] n382_I1_t0b; // @[Top.scala 1082:22]
  wire [15:0] n382_I1_t1b; // @[Top.scala 1082:22]
  wire [15:0] n382_O_t0b; // @[Top.scala 1082:22]
  wire [15:0] n382_O_t1b_t0b; // @[Top.scala 1082:22]
  wire [15:0] n382_O_t1b_t1b; // @[Top.scala 1082:22]
  Fst n353 ( // @[Top.scala 997:22]
    .valid_up(n353_valid_up),
    .valid_down(n353_valid_down),
    .I_t0b(n353_I_t0b),
    .O(n353_O)
  );
  FIFO_2 n381 ( // @[Top.scala 1000:22]
    .clock(n381_clock),
    .reset(n381_reset),
    .valid_up(n381_valid_up),
    .valid_down(n381_valid_down),
    .I(n381_I),
    .O(n381_O)
  );
  FIFO_2 n369 ( // @[Top.scala 1003:22]
    .clock(n369_clock),
    .reset(n369_reset),
    .valid_up(n369_valid_up),
    .valid_down(n369_valid_down),
    .I(n369_I),
    .O(n369_O)
  );
  InitialDelayCounter_30 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n355 ( // @[Top.scala 1007:22]
    .valid_up(n355_valid_up),
    .valid_down(n355_valid_down),
    .I_t1b_t0b(n355_I_t1b_t0b),
    .I_t1b_t1b(n355_I_t1b_t1b),
    .O_t0b(n355_O_t0b),
    .O_t1b(n355_O_t1b)
  );
  Fst_1 n356 ( // @[Top.scala 1010:22]
    .valid_up(n356_valid_up),
    .valid_down(n356_valid_down),
    .I_t0b(n356_I_t0b),
    .O(n356_O)
  );
  Snd_1 n357 ( // @[Top.scala 1013:22]
    .valid_up(n357_valid_up),
    .valid_down(n357_valid_down),
    .I_t1b(n357_I_t1b),
    .O(n357_O)
  );
  AtomTuple n358 ( // @[Top.scala 1016:22]
    .valid_up(n358_valid_up),
    .valid_down(n358_valid_down),
    .I0(n358_I0),
    .I1(n358_I1),
    .O_t0b(n358_O_t0b),
    .O_t1b(n358_O_t1b)
  );
  Add n359 ( // @[Top.scala 1020:22]
    .valid_up(n359_valid_up),
    .valid_down(n359_valid_down),
    .I_t0b(n359_I_t0b),
    .I_t1b(n359_I_t1b),
    .O(n359_O)
  );
  AtomTuple n361 ( // @[Top.scala 1023:22]
    .valid_up(n361_valid_up),
    .valid_down(n361_valid_down),
    .I0(n361_I0),
    .I1(n361_I1),
    .O_t0b(n361_O_t0b),
    .O_t1b(n361_O_t1b)
  );
  Add n362 ( // @[Top.scala 1027:22]
    .valid_up(n362_valid_up),
    .valid_down(n362_valid_down),
    .I_t0b(n362_I_t0b),
    .I_t1b(n362_I_t1b),
    .O(n362_O)
  );
  InitialDelayCounter_30 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n365 ( // @[Top.scala 1031:22]
    .valid_up(n365_valid_up),
    .valid_down(n365_valid_down),
    .I0(n365_I0),
    .O_t0b(n365_O_t0b)
  );
  RShift n366 ( // @[Top.scala 1035:22]
    .valid_up(n366_valid_up),
    .valid_down(n366_valid_down),
    .I_t0b(n366_I_t0b),
    .O(n366_O)
  );
  AtomTuple n367 ( // @[Top.scala 1038:22]
    .valid_up(n367_valid_up),
    .valid_down(n367_valid_down),
    .I0(n367_I0),
    .I1(n367_I1),
    .O_t0b(n367_O_t0b),
    .O_t1b(n367_O_t1b)
  );
  Mul n368 ( // @[Top.scala 1042:22]
    .clock(n368_clock),
    .reset(n368_reset),
    .valid_up(n368_valid_up),
    .valid_down(n368_valid_down),
    .I_t0b(n368_I_t0b),
    .I_t1b(n368_I_t1b),
    .O(n368_O)
  );
  AtomTuple n370 ( // @[Top.scala 1045:22]
    .valid_up(n370_valid_up),
    .valid_down(n370_valid_down),
    .I0(n370_I0),
    .I1(n370_I1),
    .O_t0b(n370_O_t0b),
    .O_t1b(n370_O_t1b)
  );
  Lt n371 ( // @[Top.scala 1049:22]
    .valid_up(n371_valid_up),
    .valid_down(n371_valid_down),
    .I_t0b(n371_I_t0b),
    .I_t1b(n371_I_t1b),
    .O(n371_O)
  );
  InitialDelayCounter_30 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n373 ( // @[Top.scala 1053:22]
    .valid_up(n373_valid_up),
    .valid_down(n373_valid_down),
    .I0(n373_I0),
    .I1(n373_I1),
    .O_t0b(n373_O_t0b),
    .O_t1b(n373_O_t1b)
  );
  Sub n374 ( // @[Top.scala 1057:22]
    .valid_up(n374_valid_up),
    .valid_down(n374_valid_down),
    .I_t0b(n374_I_t0b),
    .I_t1b(n374_I_t1b),
    .O(n374_O)
  );
  AtomTuple n375 ( // @[Top.scala 1060:22]
    .valid_up(n375_valid_up),
    .valid_down(n375_valid_down),
    .I0(n375_I0),
    .I1(n375_I1),
    .O_t0b(n375_O_t0b),
    .O_t1b(n375_O_t1b)
  );
  AtomTuple n376 ( // @[Top.scala 1064:22]
    .valid_up(n376_valid_up),
    .valid_down(n376_valid_down),
    .I0(n376_I0),
    .I1(n376_I1),
    .O_t0b(n376_O_t0b),
    .O_t1b(n376_O_t1b)
  );
  AtomTuple_10 n377 ( // @[Top.scala 1068:22]
    .valid_up(n377_valid_up),
    .valid_down(n377_valid_down),
    .I0_t0b(n377_I0_t0b),
    .I0_t1b(n377_I0_t1b),
    .I1_t0b(n377_I1_t0b),
    .I1_t1b(n377_I1_t1b),
    .O_t0b_t0b(n377_O_t0b_t0b),
    .O_t0b_t1b(n377_O_t0b_t1b),
    .O_t1b_t0b(n377_O_t1b_t0b),
    .O_t1b_t1b(n377_O_t1b_t1b)
  );
  FIFO_4 n378 ( // @[Top.scala 1072:22]
    .clock(n378_clock),
    .reset(n378_reset),
    .valid_up(n378_valid_up),
    .valid_down(n378_valid_down),
    .I_t0b_t0b(n378_I_t0b_t0b),
    .I_t0b_t1b(n378_I_t0b_t1b),
    .I_t1b_t0b(n378_I_t1b_t0b),
    .I_t1b_t1b(n378_I_t1b_t1b),
    .O_t0b_t0b(n378_O_t0b_t0b),
    .O_t0b_t1b(n378_O_t0b_t1b),
    .O_t1b_t0b(n378_O_t1b_t0b),
    .O_t1b_t1b(n378_O_t1b_t1b)
  );
  AtomTuple_11 n379 ( // @[Top.scala 1075:22]
    .valid_up(n379_valid_up),
    .valid_down(n379_valid_down),
    .I0(n379_I0),
    .I1_t0b_t0b(n379_I1_t0b_t0b),
    .I1_t0b_t1b(n379_I1_t0b_t1b),
    .I1_t1b_t0b(n379_I1_t1b_t0b),
    .I1_t1b_t1b(n379_I1_t1b_t1b),
    .O_t0b(n379_O_t0b),
    .O_t1b_t0b_t0b(n379_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n379_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n379_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n379_O_t1b_t1b_t1b)
  );
  If n380 ( // @[Top.scala 1079:22]
    .valid_up(n380_valid_up),
    .valid_down(n380_valid_down),
    .I_t0b(n380_I_t0b),
    .I_t1b_t0b_t0b(n380_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n380_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n380_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n380_I_t1b_t1b_t1b),
    .O_t0b(n380_O_t0b),
    .O_t1b(n380_O_t1b)
  );
  AtomTuple_1 n382 ( // @[Top.scala 1082:22]
    .valid_up(n382_valid_up),
    .valid_down(n382_valid_down),
    .I0(n382_I0),
    .I1_t0b(n382_I1_t0b),
    .I1_t1b(n382_I1_t1b),
    .O_t0b(n382_O_t0b),
    .O_t1b_t0b(n382_O_t1b_t0b),
    .O_t1b_t1b(n382_O_t1b_t1b)
  );
  assign valid_down = n382_valid_down; // @[Top.scala 1087:16]
  assign O_t0b = n382_O_t0b; // @[Top.scala 1086:7]
  assign O_t1b_t0b = n382_O_t1b_t0b; // @[Top.scala 1086:7]
  assign O_t1b_t1b = n382_O_t1b_t1b; // @[Top.scala 1086:7]
  assign n353_valid_up = valid_up; // @[Top.scala 999:19]
  assign n353_I_t0b = I_t0b; // @[Top.scala 998:12]
  assign n381_clock = clock;
  assign n381_reset = reset;
  assign n381_valid_up = n353_valid_down; // @[Top.scala 1002:19]
  assign n381_I = n353_O; // @[Top.scala 1001:12]
  assign n369_clock = clock;
  assign n369_reset = reset;
  assign n369_valid_up = n353_valid_down; // @[Top.scala 1005:19]
  assign n369_I = n353_O; // @[Top.scala 1004:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n355_valid_up = valid_up; // @[Top.scala 1009:19]
  assign n355_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1008:12]
  assign n355_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1008:12]
  assign n356_valid_up = n355_valid_down; // @[Top.scala 1012:19]
  assign n356_I_t0b = n355_O_t0b; // @[Top.scala 1011:12]
  assign n357_valid_up = n355_valid_down; // @[Top.scala 1015:19]
  assign n357_I_t1b = n355_O_t1b; // @[Top.scala 1014:12]
  assign n358_valid_up = n356_valid_down & n357_valid_down; // @[Top.scala 1019:19]
  assign n358_I0 = n356_O; // @[Top.scala 1017:13]
  assign n358_I1 = n357_O; // @[Top.scala 1018:13]
  assign n359_valid_up = n358_valid_down; // @[Top.scala 1022:19]
  assign n359_I_t0b = n358_O_t0b; // @[Top.scala 1021:12]
  assign n359_I_t1b = n358_O_t1b; // @[Top.scala 1021:12]
  assign n361_valid_up = InitialDelayCounter_valid_down & n359_valid_down; // @[Top.scala 1026:19]
  assign n361_I0 = 16'h1; // @[Top.scala 1024:13]
  assign n361_I1 = n359_O; // @[Top.scala 1025:13]
  assign n362_valid_up = n361_valid_down; // @[Top.scala 1029:19]
  assign n362_I_t0b = n361_O_t0b; // @[Top.scala 1028:12]
  assign n362_I_t1b = n361_O_t1b; // @[Top.scala 1028:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n365_valid_up = n362_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1034:19]
  assign n365_I0 = n362_O; // @[Top.scala 1032:13]
  assign n366_valid_up = n365_valid_down; // @[Top.scala 1037:19]
  assign n366_I_t0b = n365_O_t0b; // @[Top.scala 1036:12]
  assign n367_valid_up = n366_valid_down; // @[Top.scala 1041:19]
  assign n367_I0 = n366_O; // @[Top.scala 1039:13]
  assign n367_I1 = n366_O; // @[Top.scala 1040:13]
  assign n368_clock = clock;
  assign n368_reset = reset;
  assign n368_valid_up = n367_valid_down; // @[Top.scala 1044:19]
  assign n368_I_t0b = n367_O_t0b; // @[Top.scala 1043:12]
  assign n368_I_t1b = n367_O_t1b; // @[Top.scala 1043:12]
  assign n370_valid_up = n369_valid_down & n368_valid_down; // @[Top.scala 1048:19]
  assign n370_I0 = n369_O; // @[Top.scala 1046:13]
  assign n370_I1 = n368_O; // @[Top.scala 1047:13]
  assign n371_valid_up = n370_valid_down; // @[Top.scala 1051:19]
  assign n371_I_t0b = n370_O_t0b; // @[Top.scala 1050:12]
  assign n371_I_t1b = n370_O_t1b; // @[Top.scala 1050:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n373_valid_up = n366_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1056:19]
  assign n373_I0 = n366_O; // @[Top.scala 1054:13]
  assign n373_I1 = 16'h1; // @[Top.scala 1055:13]
  assign n374_valid_up = n373_valid_down; // @[Top.scala 1059:19]
  assign n374_I_t0b = n373_O_t0b; // @[Top.scala 1058:12]
  assign n374_I_t1b = n373_O_t1b; // @[Top.scala 1058:12]
  assign n375_valid_up = n356_valid_down & n374_valid_down; // @[Top.scala 1063:19]
  assign n375_I0 = n356_O; // @[Top.scala 1061:13]
  assign n375_I1 = n374_O; // @[Top.scala 1062:13]
  assign n376_valid_up = n366_valid_down & n357_valid_down; // @[Top.scala 1067:19]
  assign n376_I0 = n366_O; // @[Top.scala 1065:13]
  assign n376_I1 = n357_O; // @[Top.scala 1066:13]
  assign n377_valid_up = n375_valid_down & n376_valid_down; // @[Top.scala 1071:19]
  assign n377_I0_t0b = n375_O_t0b; // @[Top.scala 1069:13]
  assign n377_I0_t1b = n375_O_t1b; // @[Top.scala 1069:13]
  assign n377_I1_t0b = n376_O_t0b; // @[Top.scala 1070:13]
  assign n377_I1_t1b = n376_O_t1b; // @[Top.scala 1070:13]
  assign n378_clock = clock;
  assign n378_reset = reset;
  assign n378_valid_up = n377_valid_down; // @[Top.scala 1074:19]
  assign n378_I_t0b_t0b = n377_O_t0b_t0b; // @[Top.scala 1073:12]
  assign n378_I_t0b_t1b = n377_O_t0b_t1b; // @[Top.scala 1073:12]
  assign n378_I_t1b_t0b = n377_O_t1b_t0b; // @[Top.scala 1073:12]
  assign n378_I_t1b_t1b = n377_O_t1b_t1b; // @[Top.scala 1073:12]
  assign n379_valid_up = n371_valid_down & n378_valid_down; // @[Top.scala 1078:19]
  assign n379_I0 = n371_O[0]; // @[Top.scala 1076:13]
  assign n379_I1_t0b_t0b = n378_O_t0b_t0b; // @[Top.scala 1077:13]
  assign n379_I1_t0b_t1b = n378_O_t0b_t1b; // @[Top.scala 1077:13]
  assign n379_I1_t1b_t0b = n378_O_t1b_t0b; // @[Top.scala 1077:13]
  assign n379_I1_t1b_t1b = n378_O_t1b_t1b; // @[Top.scala 1077:13]
  assign n380_valid_up = n379_valid_down; // @[Top.scala 1081:19]
  assign n380_I_t0b = n379_O_t0b; // @[Top.scala 1080:12]
  assign n380_I_t1b_t0b_t0b = n379_O_t1b_t0b_t0b; // @[Top.scala 1080:12]
  assign n380_I_t1b_t0b_t1b = n379_O_t1b_t0b_t1b; // @[Top.scala 1080:12]
  assign n380_I_t1b_t1b_t0b = n379_O_t1b_t1b_t0b; // @[Top.scala 1080:12]
  assign n380_I_t1b_t1b_t1b = n379_O_t1b_t1b_t1b; // @[Top.scala 1080:12]
  assign n382_valid_up = n381_valid_down & n380_valid_down; // @[Top.scala 1085:19]
  assign n382_I0 = n381_O; // @[Top.scala 1083:13]
  assign n382_I1_t0b = n380_O_t0b; // @[Top.scala 1084:13]
  assign n382_I1_t1b = n380_O_t1b; // @[Top.scala 1084:13]
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
  wire  n387_valid_up; // @[Top.scala 1093:22]
  wire  n387_valid_down; // @[Top.scala 1093:22]
  wire [15:0] n387_I_t0b; // @[Top.scala 1093:22]
  wire [15:0] n387_O; // @[Top.scala 1093:22]
  wire  n415_clock; // @[Top.scala 1096:22]
  wire  n415_reset; // @[Top.scala 1096:22]
  wire  n415_valid_up; // @[Top.scala 1096:22]
  wire  n415_valid_down; // @[Top.scala 1096:22]
  wire [15:0] n415_I; // @[Top.scala 1096:22]
  wire [15:0] n415_O; // @[Top.scala 1096:22]
  wire  n403_clock; // @[Top.scala 1099:22]
  wire  n403_reset; // @[Top.scala 1099:22]
  wire  n403_valid_up; // @[Top.scala 1099:22]
  wire  n403_valid_down; // @[Top.scala 1099:22]
  wire [15:0] n403_I; // @[Top.scala 1099:22]
  wire [15:0] n403_O; // @[Top.scala 1099:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n389_valid_up; // @[Top.scala 1103:22]
  wire  n389_valid_down; // @[Top.scala 1103:22]
  wire [15:0] n389_I_t1b_t0b; // @[Top.scala 1103:22]
  wire [15:0] n389_I_t1b_t1b; // @[Top.scala 1103:22]
  wire [15:0] n389_O_t0b; // @[Top.scala 1103:22]
  wire [15:0] n389_O_t1b; // @[Top.scala 1103:22]
  wire  n390_valid_up; // @[Top.scala 1106:22]
  wire  n390_valid_down; // @[Top.scala 1106:22]
  wire [15:0] n390_I_t0b; // @[Top.scala 1106:22]
  wire [15:0] n390_O; // @[Top.scala 1106:22]
  wire  n391_valid_up; // @[Top.scala 1109:22]
  wire  n391_valid_down; // @[Top.scala 1109:22]
  wire [15:0] n391_I_t1b; // @[Top.scala 1109:22]
  wire [15:0] n391_O; // @[Top.scala 1109:22]
  wire  n392_valid_up; // @[Top.scala 1112:22]
  wire  n392_valid_down; // @[Top.scala 1112:22]
  wire [15:0] n392_I0; // @[Top.scala 1112:22]
  wire [15:0] n392_I1; // @[Top.scala 1112:22]
  wire [15:0] n392_O_t0b; // @[Top.scala 1112:22]
  wire [15:0] n392_O_t1b; // @[Top.scala 1112:22]
  wire  n393_valid_up; // @[Top.scala 1116:22]
  wire  n393_valid_down; // @[Top.scala 1116:22]
  wire [15:0] n393_I_t0b; // @[Top.scala 1116:22]
  wire [15:0] n393_I_t1b; // @[Top.scala 1116:22]
  wire [15:0] n393_O; // @[Top.scala 1116:22]
  wire  n395_valid_up; // @[Top.scala 1119:22]
  wire  n395_valid_down; // @[Top.scala 1119:22]
  wire [15:0] n395_I0; // @[Top.scala 1119:22]
  wire [15:0] n395_I1; // @[Top.scala 1119:22]
  wire [15:0] n395_O_t0b; // @[Top.scala 1119:22]
  wire [15:0] n395_O_t1b; // @[Top.scala 1119:22]
  wire  n396_valid_up; // @[Top.scala 1123:22]
  wire  n396_valid_down; // @[Top.scala 1123:22]
  wire [15:0] n396_I_t0b; // @[Top.scala 1123:22]
  wire [15:0] n396_I_t1b; // @[Top.scala 1123:22]
  wire [15:0] n396_O; // @[Top.scala 1123:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n399_valid_up; // @[Top.scala 1127:22]
  wire  n399_valid_down; // @[Top.scala 1127:22]
  wire [15:0] n399_I0; // @[Top.scala 1127:22]
  wire [15:0] n399_O_t0b; // @[Top.scala 1127:22]
  wire  n400_valid_up; // @[Top.scala 1131:22]
  wire  n400_valid_down; // @[Top.scala 1131:22]
  wire [15:0] n400_I_t0b; // @[Top.scala 1131:22]
  wire [15:0] n400_O; // @[Top.scala 1131:22]
  wire  n401_valid_up; // @[Top.scala 1134:22]
  wire  n401_valid_down; // @[Top.scala 1134:22]
  wire [15:0] n401_I0; // @[Top.scala 1134:22]
  wire [15:0] n401_I1; // @[Top.scala 1134:22]
  wire [15:0] n401_O_t0b; // @[Top.scala 1134:22]
  wire [15:0] n401_O_t1b; // @[Top.scala 1134:22]
  wire  n402_clock; // @[Top.scala 1138:22]
  wire  n402_reset; // @[Top.scala 1138:22]
  wire  n402_valid_up; // @[Top.scala 1138:22]
  wire  n402_valid_down; // @[Top.scala 1138:22]
  wire [15:0] n402_I_t0b; // @[Top.scala 1138:22]
  wire [15:0] n402_I_t1b; // @[Top.scala 1138:22]
  wire [15:0] n402_O; // @[Top.scala 1138:22]
  wire  n404_valid_up; // @[Top.scala 1141:22]
  wire  n404_valid_down; // @[Top.scala 1141:22]
  wire [15:0] n404_I0; // @[Top.scala 1141:22]
  wire [15:0] n404_I1; // @[Top.scala 1141:22]
  wire [15:0] n404_O_t0b; // @[Top.scala 1141:22]
  wire [15:0] n404_O_t1b; // @[Top.scala 1141:22]
  wire  n405_valid_up; // @[Top.scala 1145:22]
  wire  n405_valid_down; // @[Top.scala 1145:22]
  wire [15:0] n405_I_t0b; // @[Top.scala 1145:22]
  wire [15:0] n405_I_t1b; // @[Top.scala 1145:22]
  wire [15:0] n405_O; // @[Top.scala 1145:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n407_valid_up; // @[Top.scala 1149:22]
  wire  n407_valid_down; // @[Top.scala 1149:22]
  wire [15:0] n407_I0; // @[Top.scala 1149:22]
  wire [15:0] n407_I1; // @[Top.scala 1149:22]
  wire [15:0] n407_O_t0b; // @[Top.scala 1149:22]
  wire [15:0] n407_O_t1b; // @[Top.scala 1149:22]
  wire  n408_valid_up; // @[Top.scala 1153:22]
  wire  n408_valid_down; // @[Top.scala 1153:22]
  wire [15:0] n408_I_t0b; // @[Top.scala 1153:22]
  wire [15:0] n408_I_t1b; // @[Top.scala 1153:22]
  wire [15:0] n408_O; // @[Top.scala 1153:22]
  wire  n409_valid_up; // @[Top.scala 1156:22]
  wire  n409_valid_down; // @[Top.scala 1156:22]
  wire [15:0] n409_I0; // @[Top.scala 1156:22]
  wire [15:0] n409_I1; // @[Top.scala 1156:22]
  wire [15:0] n409_O_t0b; // @[Top.scala 1156:22]
  wire [15:0] n409_O_t1b; // @[Top.scala 1156:22]
  wire  n410_valid_up; // @[Top.scala 1160:22]
  wire  n410_valid_down; // @[Top.scala 1160:22]
  wire [15:0] n410_I0; // @[Top.scala 1160:22]
  wire [15:0] n410_I1; // @[Top.scala 1160:22]
  wire [15:0] n410_O_t0b; // @[Top.scala 1160:22]
  wire [15:0] n410_O_t1b; // @[Top.scala 1160:22]
  wire  n411_valid_up; // @[Top.scala 1164:22]
  wire  n411_valid_down; // @[Top.scala 1164:22]
  wire [15:0] n411_I0_t0b; // @[Top.scala 1164:22]
  wire [15:0] n411_I0_t1b; // @[Top.scala 1164:22]
  wire [15:0] n411_I1_t0b; // @[Top.scala 1164:22]
  wire [15:0] n411_I1_t1b; // @[Top.scala 1164:22]
  wire [15:0] n411_O_t0b_t0b; // @[Top.scala 1164:22]
  wire [15:0] n411_O_t0b_t1b; // @[Top.scala 1164:22]
  wire [15:0] n411_O_t1b_t0b; // @[Top.scala 1164:22]
  wire [15:0] n411_O_t1b_t1b; // @[Top.scala 1164:22]
  wire  n412_clock; // @[Top.scala 1168:22]
  wire  n412_reset; // @[Top.scala 1168:22]
  wire  n412_valid_up; // @[Top.scala 1168:22]
  wire  n412_valid_down; // @[Top.scala 1168:22]
  wire [15:0] n412_I_t0b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n412_I_t0b_t1b; // @[Top.scala 1168:22]
  wire [15:0] n412_I_t1b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n412_I_t1b_t1b; // @[Top.scala 1168:22]
  wire [15:0] n412_O_t0b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n412_O_t0b_t1b; // @[Top.scala 1168:22]
  wire [15:0] n412_O_t1b_t0b; // @[Top.scala 1168:22]
  wire [15:0] n412_O_t1b_t1b; // @[Top.scala 1168:22]
  wire  n413_valid_up; // @[Top.scala 1171:22]
  wire  n413_valid_down; // @[Top.scala 1171:22]
  wire  n413_I0; // @[Top.scala 1171:22]
  wire [15:0] n413_I1_t0b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n413_I1_t0b_t1b; // @[Top.scala 1171:22]
  wire [15:0] n413_I1_t1b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n413_I1_t1b_t1b; // @[Top.scala 1171:22]
  wire  n413_O_t0b; // @[Top.scala 1171:22]
  wire [15:0] n413_O_t1b_t0b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n413_O_t1b_t0b_t1b; // @[Top.scala 1171:22]
  wire [15:0] n413_O_t1b_t1b_t0b; // @[Top.scala 1171:22]
  wire [15:0] n413_O_t1b_t1b_t1b; // @[Top.scala 1171:22]
  wire  n414_valid_up; // @[Top.scala 1175:22]
  wire  n414_valid_down; // @[Top.scala 1175:22]
  wire  n414_I_t0b; // @[Top.scala 1175:22]
  wire [15:0] n414_I_t1b_t0b_t0b; // @[Top.scala 1175:22]
  wire [15:0] n414_I_t1b_t0b_t1b; // @[Top.scala 1175:22]
  wire [15:0] n414_I_t1b_t1b_t0b; // @[Top.scala 1175:22]
  wire [15:0] n414_I_t1b_t1b_t1b; // @[Top.scala 1175:22]
  wire [15:0] n414_O_t0b; // @[Top.scala 1175:22]
  wire [15:0] n414_O_t1b; // @[Top.scala 1175:22]
  wire  n416_valid_up; // @[Top.scala 1178:22]
  wire  n416_valid_down; // @[Top.scala 1178:22]
  wire [15:0] n416_I0; // @[Top.scala 1178:22]
  wire [15:0] n416_I1_t0b; // @[Top.scala 1178:22]
  wire [15:0] n416_I1_t1b; // @[Top.scala 1178:22]
  wire [15:0] n416_O_t0b; // @[Top.scala 1178:22]
  wire [15:0] n416_O_t1b_t0b; // @[Top.scala 1178:22]
  wire [15:0] n416_O_t1b_t1b; // @[Top.scala 1178:22]
  Fst n387 ( // @[Top.scala 1093:22]
    .valid_up(n387_valid_up),
    .valid_down(n387_valid_down),
    .I_t0b(n387_I_t0b),
    .O(n387_O)
  );
  FIFO_2 n415 ( // @[Top.scala 1096:22]
    .clock(n415_clock),
    .reset(n415_reset),
    .valid_up(n415_valid_up),
    .valid_down(n415_valid_down),
    .I(n415_I),
    .O(n415_O)
  );
  FIFO_2 n403 ( // @[Top.scala 1099:22]
    .clock(n403_clock),
    .reset(n403_reset),
    .valid_up(n403_valid_up),
    .valid_down(n403_valid_down),
    .I(n403_I),
    .O(n403_O)
  );
  InitialDelayCounter_33 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n389 ( // @[Top.scala 1103:22]
    .valid_up(n389_valid_up),
    .valid_down(n389_valid_down),
    .I_t1b_t0b(n389_I_t1b_t0b),
    .I_t1b_t1b(n389_I_t1b_t1b),
    .O_t0b(n389_O_t0b),
    .O_t1b(n389_O_t1b)
  );
  Fst_1 n390 ( // @[Top.scala 1106:22]
    .valid_up(n390_valid_up),
    .valid_down(n390_valid_down),
    .I_t0b(n390_I_t0b),
    .O(n390_O)
  );
  Snd_1 n391 ( // @[Top.scala 1109:22]
    .valid_up(n391_valid_up),
    .valid_down(n391_valid_down),
    .I_t1b(n391_I_t1b),
    .O(n391_O)
  );
  AtomTuple n392 ( // @[Top.scala 1112:22]
    .valid_up(n392_valid_up),
    .valid_down(n392_valid_down),
    .I0(n392_I0),
    .I1(n392_I1),
    .O_t0b(n392_O_t0b),
    .O_t1b(n392_O_t1b)
  );
  Add n393 ( // @[Top.scala 1116:22]
    .valid_up(n393_valid_up),
    .valid_down(n393_valid_down),
    .I_t0b(n393_I_t0b),
    .I_t1b(n393_I_t1b),
    .O(n393_O)
  );
  AtomTuple n395 ( // @[Top.scala 1119:22]
    .valid_up(n395_valid_up),
    .valid_down(n395_valid_down),
    .I0(n395_I0),
    .I1(n395_I1),
    .O_t0b(n395_O_t0b),
    .O_t1b(n395_O_t1b)
  );
  Add n396 ( // @[Top.scala 1123:22]
    .valid_up(n396_valid_up),
    .valid_down(n396_valid_down),
    .I_t0b(n396_I_t0b),
    .I_t1b(n396_I_t1b),
    .O(n396_O)
  );
  InitialDelayCounter_33 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n399 ( // @[Top.scala 1127:22]
    .valid_up(n399_valid_up),
    .valid_down(n399_valid_down),
    .I0(n399_I0),
    .O_t0b(n399_O_t0b)
  );
  RShift n400 ( // @[Top.scala 1131:22]
    .valid_up(n400_valid_up),
    .valid_down(n400_valid_down),
    .I_t0b(n400_I_t0b),
    .O(n400_O)
  );
  AtomTuple n401 ( // @[Top.scala 1134:22]
    .valid_up(n401_valid_up),
    .valid_down(n401_valid_down),
    .I0(n401_I0),
    .I1(n401_I1),
    .O_t0b(n401_O_t0b),
    .O_t1b(n401_O_t1b)
  );
  Mul n402 ( // @[Top.scala 1138:22]
    .clock(n402_clock),
    .reset(n402_reset),
    .valid_up(n402_valid_up),
    .valid_down(n402_valid_down),
    .I_t0b(n402_I_t0b),
    .I_t1b(n402_I_t1b),
    .O(n402_O)
  );
  AtomTuple n404 ( // @[Top.scala 1141:22]
    .valid_up(n404_valid_up),
    .valid_down(n404_valid_down),
    .I0(n404_I0),
    .I1(n404_I1),
    .O_t0b(n404_O_t0b),
    .O_t1b(n404_O_t1b)
  );
  Lt n405 ( // @[Top.scala 1145:22]
    .valid_up(n405_valid_up),
    .valid_down(n405_valid_down),
    .I_t0b(n405_I_t0b),
    .I_t1b(n405_I_t1b),
    .O(n405_O)
  );
  InitialDelayCounter_33 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n407 ( // @[Top.scala 1149:22]
    .valid_up(n407_valid_up),
    .valid_down(n407_valid_down),
    .I0(n407_I0),
    .I1(n407_I1),
    .O_t0b(n407_O_t0b),
    .O_t1b(n407_O_t1b)
  );
  Sub n408 ( // @[Top.scala 1153:22]
    .valid_up(n408_valid_up),
    .valid_down(n408_valid_down),
    .I_t0b(n408_I_t0b),
    .I_t1b(n408_I_t1b),
    .O(n408_O)
  );
  AtomTuple n409 ( // @[Top.scala 1156:22]
    .valid_up(n409_valid_up),
    .valid_down(n409_valid_down),
    .I0(n409_I0),
    .I1(n409_I1),
    .O_t0b(n409_O_t0b),
    .O_t1b(n409_O_t1b)
  );
  AtomTuple n410 ( // @[Top.scala 1160:22]
    .valid_up(n410_valid_up),
    .valid_down(n410_valid_down),
    .I0(n410_I0),
    .I1(n410_I1),
    .O_t0b(n410_O_t0b),
    .O_t1b(n410_O_t1b)
  );
  AtomTuple_10 n411 ( // @[Top.scala 1164:22]
    .valid_up(n411_valid_up),
    .valid_down(n411_valid_down),
    .I0_t0b(n411_I0_t0b),
    .I0_t1b(n411_I0_t1b),
    .I1_t0b(n411_I1_t0b),
    .I1_t1b(n411_I1_t1b),
    .O_t0b_t0b(n411_O_t0b_t0b),
    .O_t0b_t1b(n411_O_t0b_t1b),
    .O_t1b_t0b(n411_O_t1b_t0b),
    .O_t1b_t1b(n411_O_t1b_t1b)
  );
  FIFO_4 n412 ( // @[Top.scala 1168:22]
    .clock(n412_clock),
    .reset(n412_reset),
    .valid_up(n412_valid_up),
    .valid_down(n412_valid_down),
    .I_t0b_t0b(n412_I_t0b_t0b),
    .I_t0b_t1b(n412_I_t0b_t1b),
    .I_t1b_t0b(n412_I_t1b_t0b),
    .I_t1b_t1b(n412_I_t1b_t1b),
    .O_t0b_t0b(n412_O_t0b_t0b),
    .O_t0b_t1b(n412_O_t0b_t1b),
    .O_t1b_t0b(n412_O_t1b_t0b),
    .O_t1b_t1b(n412_O_t1b_t1b)
  );
  AtomTuple_11 n413 ( // @[Top.scala 1171:22]
    .valid_up(n413_valid_up),
    .valid_down(n413_valid_down),
    .I0(n413_I0),
    .I1_t0b_t0b(n413_I1_t0b_t0b),
    .I1_t0b_t1b(n413_I1_t0b_t1b),
    .I1_t1b_t0b(n413_I1_t1b_t0b),
    .I1_t1b_t1b(n413_I1_t1b_t1b),
    .O_t0b(n413_O_t0b),
    .O_t1b_t0b_t0b(n413_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n413_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n413_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n413_O_t1b_t1b_t1b)
  );
  If n414 ( // @[Top.scala 1175:22]
    .valid_up(n414_valid_up),
    .valid_down(n414_valid_down),
    .I_t0b(n414_I_t0b),
    .I_t1b_t0b_t0b(n414_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n414_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n414_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n414_I_t1b_t1b_t1b),
    .O_t0b(n414_O_t0b),
    .O_t1b(n414_O_t1b)
  );
  AtomTuple_1 n416 ( // @[Top.scala 1178:22]
    .valid_up(n416_valid_up),
    .valid_down(n416_valid_down),
    .I0(n416_I0),
    .I1_t0b(n416_I1_t0b),
    .I1_t1b(n416_I1_t1b),
    .O_t0b(n416_O_t0b),
    .O_t1b_t0b(n416_O_t1b_t0b),
    .O_t1b_t1b(n416_O_t1b_t1b)
  );
  assign valid_down = n416_valid_down; // @[Top.scala 1183:16]
  assign O_t0b = n416_O_t0b; // @[Top.scala 1182:7]
  assign O_t1b_t0b = n416_O_t1b_t0b; // @[Top.scala 1182:7]
  assign O_t1b_t1b = n416_O_t1b_t1b; // @[Top.scala 1182:7]
  assign n387_valid_up = valid_up; // @[Top.scala 1095:19]
  assign n387_I_t0b = I_t0b; // @[Top.scala 1094:12]
  assign n415_clock = clock;
  assign n415_reset = reset;
  assign n415_valid_up = n387_valid_down; // @[Top.scala 1098:19]
  assign n415_I = n387_O; // @[Top.scala 1097:12]
  assign n403_clock = clock;
  assign n403_reset = reset;
  assign n403_valid_up = n387_valid_down; // @[Top.scala 1101:19]
  assign n403_I = n387_O; // @[Top.scala 1100:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n389_valid_up = valid_up; // @[Top.scala 1105:19]
  assign n389_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1104:12]
  assign n389_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1104:12]
  assign n390_valid_up = n389_valid_down; // @[Top.scala 1108:19]
  assign n390_I_t0b = n389_O_t0b; // @[Top.scala 1107:12]
  assign n391_valid_up = n389_valid_down; // @[Top.scala 1111:19]
  assign n391_I_t1b = n389_O_t1b; // @[Top.scala 1110:12]
  assign n392_valid_up = n390_valid_down & n391_valid_down; // @[Top.scala 1115:19]
  assign n392_I0 = n390_O; // @[Top.scala 1113:13]
  assign n392_I1 = n391_O; // @[Top.scala 1114:13]
  assign n393_valid_up = n392_valid_down; // @[Top.scala 1118:19]
  assign n393_I_t0b = n392_O_t0b; // @[Top.scala 1117:12]
  assign n393_I_t1b = n392_O_t1b; // @[Top.scala 1117:12]
  assign n395_valid_up = InitialDelayCounter_valid_down & n393_valid_down; // @[Top.scala 1122:19]
  assign n395_I0 = 16'h1; // @[Top.scala 1120:13]
  assign n395_I1 = n393_O; // @[Top.scala 1121:13]
  assign n396_valid_up = n395_valid_down; // @[Top.scala 1125:19]
  assign n396_I_t0b = n395_O_t0b; // @[Top.scala 1124:12]
  assign n396_I_t1b = n395_O_t1b; // @[Top.scala 1124:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n399_valid_up = n396_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1130:19]
  assign n399_I0 = n396_O; // @[Top.scala 1128:13]
  assign n400_valid_up = n399_valid_down; // @[Top.scala 1133:19]
  assign n400_I_t0b = n399_O_t0b; // @[Top.scala 1132:12]
  assign n401_valid_up = n400_valid_down; // @[Top.scala 1137:19]
  assign n401_I0 = n400_O; // @[Top.scala 1135:13]
  assign n401_I1 = n400_O; // @[Top.scala 1136:13]
  assign n402_clock = clock;
  assign n402_reset = reset;
  assign n402_valid_up = n401_valid_down; // @[Top.scala 1140:19]
  assign n402_I_t0b = n401_O_t0b; // @[Top.scala 1139:12]
  assign n402_I_t1b = n401_O_t1b; // @[Top.scala 1139:12]
  assign n404_valid_up = n403_valid_down & n402_valid_down; // @[Top.scala 1144:19]
  assign n404_I0 = n403_O; // @[Top.scala 1142:13]
  assign n404_I1 = n402_O; // @[Top.scala 1143:13]
  assign n405_valid_up = n404_valid_down; // @[Top.scala 1147:19]
  assign n405_I_t0b = n404_O_t0b; // @[Top.scala 1146:12]
  assign n405_I_t1b = n404_O_t1b; // @[Top.scala 1146:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n407_valid_up = n400_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1152:19]
  assign n407_I0 = n400_O; // @[Top.scala 1150:13]
  assign n407_I1 = 16'h1; // @[Top.scala 1151:13]
  assign n408_valid_up = n407_valid_down; // @[Top.scala 1155:19]
  assign n408_I_t0b = n407_O_t0b; // @[Top.scala 1154:12]
  assign n408_I_t1b = n407_O_t1b; // @[Top.scala 1154:12]
  assign n409_valid_up = n390_valid_down & n408_valid_down; // @[Top.scala 1159:19]
  assign n409_I0 = n390_O; // @[Top.scala 1157:13]
  assign n409_I1 = n408_O; // @[Top.scala 1158:13]
  assign n410_valid_up = n400_valid_down & n391_valid_down; // @[Top.scala 1163:19]
  assign n410_I0 = n400_O; // @[Top.scala 1161:13]
  assign n410_I1 = n391_O; // @[Top.scala 1162:13]
  assign n411_valid_up = n409_valid_down & n410_valid_down; // @[Top.scala 1167:19]
  assign n411_I0_t0b = n409_O_t0b; // @[Top.scala 1165:13]
  assign n411_I0_t1b = n409_O_t1b; // @[Top.scala 1165:13]
  assign n411_I1_t0b = n410_O_t0b; // @[Top.scala 1166:13]
  assign n411_I1_t1b = n410_O_t1b; // @[Top.scala 1166:13]
  assign n412_clock = clock;
  assign n412_reset = reset;
  assign n412_valid_up = n411_valid_down; // @[Top.scala 1170:19]
  assign n412_I_t0b_t0b = n411_O_t0b_t0b; // @[Top.scala 1169:12]
  assign n412_I_t0b_t1b = n411_O_t0b_t1b; // @[Top.scala 1169:12]
  assign n412_I_t1b_t0b = n411_O_t1b_t0b; // @[Top.scala 1169:12]
  assign n412_I_t1b_t1b = n411_O_t1b_t1b; // @[Top.scala 1169:12]
  assign n413_valid_up = n405_valid_down & n412_valid_down; // @[Top.scala 1174:19]
  assign n413_I0 = n405_O[0]; // @[Top.scala 1172:13]
  assign n413_I1_t0b_t0b = n412_O_t0b_t0b; // @[Top.scala 1173:13]
  assign n413_I1_t0b_t1b = n412_O_t0b_t1b; // @[Top.scala 1173:13]
  assign n413_I1_t1b_t0b = n412_O_t1b_t0b; // @[Top.scala 1173:13]
  assign n413_I1_t1b_t1b = n412_O_t1b_t1b; // @[Top.scala 1173:13]
  assign n414_valid_up = n413_valid_down; // @[Top.scala 1177:19]
  assign n414_I_t0b = n413_O_t0b; // @[Top.scala 1176:12]
  assign n414_I_t1b_t0b_t0b = n413_O_t1b_t0b_t0b; // @[Top.scala 1176:12]
  assign n414_I_t1b_t0b_t1b = n413_O_t1b_t0b_t1b; // @[Top.scala 1176:12]
  assign n414_I_t1b_t1b_t0b = n413_O_t1b_t1b_t0b; // @[Top.scala 1176:12]
  assign n414_I_t1b_t1b_t1b = n413_O_t1b_t1b_t1b; // @[Top.scala 1176:12]
  assign n416_valid_up = n415_valid_down & n414_valid_down; // @[Top.scala 1181:19]
  assign n416_I0 = n415_O; // @[Top.scala 1179:13]
  assign n416_I1_t0b = n414_O_t0b; // @[Top.scala 1180:13]
  assign n416_I1_t1b = n414_O_t1b; // @[Top.scala 1180:13]
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
  wire  n421_valid_up; // @[Top.scala 1189:22]
  wire  n421_valid_down; // @[Top.scala 1189:22]
  wire [15:0] n421_I_t0b; // @[Top.scala 1189:22]
  wire [15:0] n421_O; // @[Top.scala 1189:22]
  wire  n449_clock; // @[Top.scala 1192:22]
  wire  n449_reset; // @[Top.scala 1192:22]
  wire  n449_valid_up; // @[Top.scala 1192:22]
  wire  n449_valid_down; // @[Top.scala 1192:22]
  wire [15:0] n449_I; // @[Top.scala 1192:22]
  wire [15:0] n449_O; // @[Top.scala 1192:22]
  wire  n437_clock; // @[Top.scala 1195:22]
  wire  n437_reset; // @[Top.scala 1195:22]
  wire  n437_valid_up; // @[Top.scala 1195:22]
  wire  n437_valid_down; // @[Top.scala 1195:22]
  wire [15:0] n437_I; // @[Top.scala 1195:22]
  wire [15:0] n437_O; // @[Top.scala 1195:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n423_valid_up; // @[Top.scala 1199:22]
  wire  n423_valid_down; // @[Top.scala 1199:22]
  wire [15:0] n423_I_t1b_t0b; // @[Top.scala 1199:22]
  wire [15:0] n423_I_t1b_t1b; // @[Top.scala 1199:22]
  wire [15:0] n423_O_t0b; // @[Top.scala 1199:22]
  wire [15:0] n423_O_t1b; // @[Top.scala 1199:22]
  wire  n424_valid_up; // @[Top.scala 1202:22]
  wire  n424_valid_down; // @[Top.scala 1202:22]
  wire [15:0] n424_I_t0b; // @[Top.scala 1202:22]
  wire [15:0] n424_O; // @[Top.scala 1202:22]
  wire  n425_valid_up; // @[Top.scala 1205:22]
  wire  n425_valid_down; // @[Top.scala 1205:22]
  wire [15:0] n425_I_t1b; // @[Top.scala 1205:22]
  wire [15:0] n425_O; // @[Top.scala 1205:22]
  wire  n426_valid_up; // @[Top.scala 1208:22]
  wire  n426_valid_down; // @[Top.scala 1208:22]
  wire [15:0] n426_I0; // @[Top.scala 1208:22]
  wire [15:0] n426_I1; // @[Top.scala 1208:22]
  wire [15:0] n426_O_t0b; // @[Top.scala 1208:22]
  wire [15:0] n426_O_t1b; // @[Top.scala 1208:22]
  wire  n427_valid_up; // @[Top.scala 1212:22]
  wire  n427_valid_down; // @[Top.scala 1212:22]
  wire [15:0] n427_I_t0b; // @[Top.scala 1212:22]
  wire [15:0] n427_I_t1b; // @[Top.scala 1212:22]
  wire [15:0] n427_O; // @[Top.scala 1212:22]
  wire  n429_valid_up; // @[Top.scala 1215:22]
  wire  n429_valid_down; // @[Top.scala 1215:22]
  wire [15:0] n429_I0; // @[Top.scala 1215:22]
  wire [15:0] n429_I1; // @[Top.scala 1215:22]
  wire [15:0] n429_O_t0b; // @[Top.scala 1215:22]
  wire [15:0] n429_O_t1b; // @[Top.scala 1215:22]
  wire  n430_valid_up; // @[Top.scala 1219:22]
  wire  n430_valid_down; // @[Top.scala 1219:22]
  wire [15:0] n430_I_t0b; // @[Top.scala 1219:22]
  wire [15:0] n430_I_t1b; // @[Top.scala 1219:22]
  wire [15:0] n430_O; // @[Top.scala 1219:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n433_valid_up; // @[Top.scala 1223:22]
  wire  n433_valid_down; // @[Top.scala 1223:22]
  wire [15:0] n433_I0; // @[Top.scala 1223:22]
  wire [15:0] n433_O_t0b; // @[Top.scala 1223:22]
  wire  n434_valid_up; // @[Top.scala 1227:22]
  wire  n434_valid_down; // @[Top.scala 1227:22]
  wire [15:0] n434_I_t0b; // @[Top.scala 1227:22]
  wire [15:0] n434_O; // @[Top.scala 1227:22]
  wire  n435_valid_up; // @[Top.scala 1230:22]
  wire  n435_valid_down; // @[Top.scala 1230:22]
  wire [15:0] n435_I0; // @[Top.scala 1230:22]
  wire [15:0] n435_I1; // @[Top.scala 1230:22]
  wire [15:0] n435_O_t0b; // @[Top.scala 1230:22]
  wire [15:0] n435_O_t1b; // @[Top.scala 1230:22]
  wire  n436_clock; // @[Top.scala 1234:22]
  wire  n436_reset; // @[Top.scala 1234:22]
  wire  n436_valid_up; // @[Top.scala 1234:22]
  wire  n436_valid_down; // @[Top.scala 1234:22]
  wire [15:0] n436_I_t0b; // @[Top.scala 1234:22]
  wire [15:0] n436_I_t1b; // @[Top.scala 1234:22]
  wire [15:0] n436_O; // @[Top.scala 1234:22]
  wire  n438_valid_up; // @[Top.scala 1237:22]
  wire  n438_valid_down; // @[Top.scala 1237:22]
  wire [15:0] n438_I0; // @[Top.scala 1237:22]
  wire [15:0] n438_I1; // @[Top.scala 1237:22]
  wire [15:0] n438_O_t0b; // @[Top.scala 1237:22]
  wire [15:0] n438_O_t1b; // @[Top.scala 1237:22]
  wire  n439_valid_up; // @[Top.scala 1241:22]
  wire  n439_valid_down; // @[Top.scala 1241:22]
  wire [15:0] n439_I_t0b; // @[Top.scala 1241:22]
  wire [15:0] n439_I_t1b; // @[Top.scala 1241:22]
  wire [15:0] n439_O; // @[Top.scala 1241:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n441_valid_up; // @[Top.scala 1245:22]
  wire  n441_valid_down; // @[Top.scala 1245:22]
  wire [15:0] n441_I0; // @[Top.scala 1245:22]
  wire [15:0] n441_I1; // @[Top.scala 1245:22]
  wire [15:0] n441_O_t0b; // @[Top.scala 1245:22]
  wire [15:0] n441_O_t1b; // @[Top.scala 1245:22]
  wire  n442_valid_up; // @[Top.scala 1249:22]
  wire  n442_valid_down; // @[Top.scala 1249:22]
  wire [15:0] n442_I_t0b; // @[Top.scala 1249:22]
  wire [15:0] n442_I_t1b; // @[Top.scala 1249:22]
  wire [15:0] n442_O; // @[Top.scala 1249:22]
  wire  n443_valid_up; // @[Top.scala 1252:22]
  wire  n443_valid_down; // @[Top.scala 1252:22]
  wire [15:0] n443_I0; // @[Top.scala 1252:22]
  wire [15:0] n443_I1; // @[Top.scala 1252:22]
  wire [15:0] n443_O_t0b; // @[Top.scala 1252:22]
  wire [15:0] n443_O_t1b; // @[Top.scala 1252:22]
  wire  n444_valid_up; // @[Top.scala 1256:22]
  wire  n444_valid_down; // @[Top.scala 1256:22]
  wire [15:0] n444_I0; // @[Top.scala 1256:22]
  wire [15:0] n444_I1; // @[Top.scala 1256:22]
  wire [15:0] n444_O_t0b; // @[Top.scala 1256:22]
  wire [15:0] n444_O_t1b; // @[Top.scala 1256:22]
  wire  n445_valid_up; // @[Top.scala 1260:22]
  wire  n445_valid_down; // @[Top.scala 1260:22]
  wire [15:0] n445_I0_t0b; // @[Top.scala 1260:22]
  wire [15:0] n445_I0_t1b; // @[Top.scala 1260:22]
  wire [15:0] n445_I1_t0b; // @[Top.scala 1260:22]
  wire [15:0] n445_I1_t1b; // @[Top.scala 1260:22]
  wire [15:0] n445_O_t0b_t0b; // @[Top.scala 1260:22]
  wire [15:0] n445_O_t0b_t1b; // @[Top.scala 1260:22]
  wire [15:0] n445_O_t1b_t0b; // @[Top.scala 1260:22]
  wire [15:0] n445_O_t1b_t1b; // @[Top.scala 1260:22]
  wire  n446_clock; // @[Top.scala 1264:22]
  wire  n446_reset; // @[Top.scala 1264:22]
  wire  n446_valid_up; // @[Top.scala 1264:22]
  wire  n446_valid_down; // @[Top.scala 1264:22]
  wire [15:0] n446_I_t0b_t0b; // @[Top.scala 1264:22]
  wire [15:0] n446_I_t0b_t1b; // @[Top.scala 1264:22]
  wire [15:0] n446_I_t1b_t0b; // @[Top.scala 1264:22]
  wire [15:0] n446_I_t1b_t1b; // @[Top.scala 1264:22]
  wire [15:0] n446_O_t0b_t0b; // @[Top.scala 1264:22]
  wire [15:0] n446_O_t0b_t1b; // @[Top.scala 1264:22]
  wire [15:0] n446_O_t1b_t0b; // @[Top.scala 1264:22]
  wire [15:0] n446_O_t1b_t1b; // @[Top.scala 1264:22]
  wire  n447_valid_up; // @[Top.scala 1267:22]
  wire  n447_valid_down; // @[Top.scala 1267:22]
  wire  n447_I0; // @[Top.scala 1267:22]
  wire [15:0] n447_I1_t0b_t0b; // @[Top.scala 1267:22]
  wire [15:0] n447_I1_t0b_t1b; // @[Top.scala 1267:22]
  wire [15:0] n447_I1_t1b_t0b; // @[Top.scala 1267:22]
  wire [15:0] n447_I1_t1b_t1b; // @[Top.scala 1267:22]
  wire  n447_O_t0b; // @[Top.scala 1267:22]
  wire [15:0] n447_O_t1b_t0b_t0b; // @[Top.scala 1267:22]
  wire [15:0] n447_O_t1b_t0b_t1b; // @[Top.scala 1267:22]
  wire [15:0] n447_O_t1b_t1b_t0b; // @[Top.scala 1267:22]
  wire [15:0] n447_O_t1b_t1b_t1b; // @[Top.scala 1267:22]
  wire  n448_valid_up; // @[Top.scala 1271:22]
  wire  n448_valid_down; // @[Top.scala 1271:22]
  wire  n448_I_t0b; // @[Top.scala 1271:22]
  wire [15:0] n448_I_t1b_t0b_t0b; // @[Top.scala 1271:22]
  wire [15:0] n448_I_t1b_t0b_t1b; // @[Top.scala 1271:22]
  wire [15:0] n448_I_t1b_t1b_t0b; // @[Top.scala 1271:22]
  wire [15:0] n448_I_t1b_t1b_t1b; // @[Top.scala 1271:22]
  wire [15:0] n448_O_t0b; // @[Top.scala 1271:22]
  wire [15:0] n448_O_t1b; // @[Top.scala 1271:22]
  wire  n450_valid_up; // @[Top.scala 1274:22]
  wire  n450_valid_down; // @[Top.scala 1274:22]
  wire [15:0] n450_I0; // @[Top.scala 1274:22]
  wire [15:0] n450_I1_t0b; // @[Top.scala 1274:22]
  wire [15:0] n450_I1_t1b; // @[Top.scala 1274:22]
  wire [15:0] n450_O_t0b; // @[Top.scala 1274:22]
  wire [15:0] n450_O_t1b_t0b; // @[Top.scala 1274:22]
  wire [15:0] n450_O_t1b_t1b; // @[Top.scala 1274:22]
  Fst n421 ( // @[Top.scala 1189:22]
    .valid_up(n421_valid_up),
    .valid_down(n421_valid_down),
    .I_t0b(n421_I_t0b),
    .O(n421_O)
  );
  FIFO_2 n449 ( // @[Top.scala 1192:22]
    .clock(n449_clock),
    .reset(n449_reset),
    .valid_up(n449_valid_up),
    .valid_down(n449_valid_down),
    .I(n449_I),
    .O(n449_O)
  );
  FIFO_2 n437 ( // @[Top.scala 1195:22]
    .clock(n437_clock),
    .reset(n437_reset),
    .valid_up(n437_valid_up),
    .valid_down(n437_valid_down),
    .I(n437_I),
    .O(n437_O)
  );
  InitialDelayCounter_36 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n423 ( // @[Top.scala 1199:22]
    .valid_up(n423_valid_up),
    .valid_down(n423_valid_down),
    .I_t1b_t0b(n423_I_t1b_t0b),
    .I_t1b_t1b(n423_I_t1b_t1b),
    .O_t0b(n423_O_t0b),
    .O_t1b(n423_O_t1b)
  );
  Fst_1 n424 ( // @[Top.scala 1202:22]
    .valid_up(n424_valid_up),
    .valid_down(n424_valid_down),
    .I_t0b(n424_I_t0b),
    .O(n424_O)
  );
  Snd_1 n425 ( // @[Top.scala 1205:22]
    .valid_up(n425_valid_up),
    .valid_down(n425_valid_down),
    .I_t1b(n425_I_t1b),
    .O(n425_O)
  );
  AtomTuple n426 ( // @[Top.scala 1208:22]
    .valid_up(n426_valid_up),
    .valid_down(n426_valid_down),
    .I0(n426_I0),
    .I1(n426_I1),
    .O_t0b(n426_O_t0b),
    .O_t1b(n426_O_t1b)
  );
  Add n427 ( // @[Top.scala 1212:22]
    .valid_up(n427_valid_up),
    .valid_down(n427_valid_down),
    .I_t0b(n427_I_t0b),
    .I_t1b(n427_I_t1b),
    .O(n427_O)
  );
  AtomTuple n429 ( // @[Top.scala 1215:22]
    .valid_up(n429_valid_up),
    .valid_down(n429_valid_down),
    .I0(n429_I0),
    .I1(n429_I1),
    .O_t0b(n429_O_t0b),
    .O_t1b(n429_O_t1b)
  );
  Add n430 ( // @[Top.scala 1219:22]
    .valid_up(n430_valid_up),
    .valid_down(n430_valid_down),
    .I_t0b(n430_I_t0b),
    .I_t1b(n430_I_t1b),
    .O(n430_O)
  );
  InitialDelayCounter_36 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n433 ( // @[Top.scala 1223:22]
    .valid_up(n433_valid_up),
    .valid_down(n433_valid_down),
    .I0(n433_I0),
    .O_t0b(n433_O_t0b)
  );
  RShift n434 ( // @[Top.scala 1227:22]
    .valid_up(n434_valid_up),
    .valid_down(n434_valid_down),
    .I_t0b(n434_I_t0b),
    .O(n434_O)
  );
  AtomTuple n435 ( // @[Top.scala 1230:22]
    .valid_up(n435_valid_up),
    .valid_down(n435_valid_down),
    .I0(n435_I0),
    .I1(n435_I1),
    .O_t0b(n435_O_t0b),
    .O_t1b(n435_O_t1b)
  );
  Mul n436 ( // @[Top.scala 1234:22]
    .clock(n436_clock),
    .reset(n436_reset),
    .valid_up(n436_valid_up),
    .valid_down(n436_valid_down),
    .I_t0b(n436_I_t0b),
    .I_t1b(n436_I_t1b),
    .O(n436_O)
  );
  AtomTuple n438 ( // @[Top.scala 1237:22]
    .valid_up(n438_valid_up),
    .valid_down(n438_valid_down),
    .I0(n438_I0),
    .I1(n438_I1),
    .O_t0b(n438_O_t0b),
    .O_t1b(n438_O_t1b)
  );
  Lt n439 ( // @[Top.scala 1241:22]
    .valid_up(n439_valid_up),
    .valid_down(n439_valid_down),
    .I_t0b(n439_I_t0b),
    .I_t1b(n439_I_t1b),
    .O(n439_O)
  );
  InitialDelayCounter_36 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n441 ( // @[Top.scala 1245:22]
    .valid_up(n441_valid_up),
    .valid_down(n441_valid_down),
    .I0(n441_I0),
    .I1(n441_I1),
    .O_t0b(n441_O_t0b),
    .O_t1b(n441_O_t1b)
  );
  Sub n442 ( // @[Top.scala 1249:22]
    .valid_up(n442_valid_up),
    .valid_down(n442_valid_down),
    .I_t0b(n442_I_t0b),
    .I_t1b(n442_I_t1b),
    .O(n442_O)
  );
  AtomTuple n443 ( // @[Top.scala 1252:22]
    .valid_up(n443_valid_up),
    .valid_down(n443_valid_down),
    .I0(n443_I0),
    .I1(n443_I1),
    .O_t0b(n443_O_t0b),
    .O_t1b(n443_O_t1b)
  );
  AtomTuple n444 ( // @[Top.scala 1256:22]
    .valid_up(n444_valid_up),
    .valid_down(n444_valid_down),
    .I0(n444_I0),
    .I1(n444_I1),
    .O_t0b(n444_O_t0b),
    .O_t1b(n444_O_t1b)
  );
  AtomTuple_10 n445 ( // @[Top.scala 1260:22]
    .valid_up(n445_valid_up),
    .valid_down(n445_valid_down),
    .I0_t0b(n445_I0_t0b),
    .I0_t1b(n445_I0_t1b),
    .I1_t0b(n445_I1_t0b),
    .I1_t1b(n445_I1_t1b),
    .O_t0b_t0b(n445_O_t0b_t0b),
    .O_t0b_t1b(n445_O_t0b_t1b),
    .O_t1b_t0b(n445_O_t1b_t0b),
    .O_t1b_t1b(n445_O_t1b_t1b)
  );
  FIFO_4 n446 ( // @[Top.scala 1264:22]
    .clock(n446_clock),
    .reset(n446_reset),
    .valid_up(n446_valid_up),
    .valid_down(n446_valid_down),
    .I_t0b_t0b(n446_I_t0b_t0b),
    .I_t0b_t1b(n446_I_t0b_t1b),
    .I_t1b_t0b(n446_I_t1b_t0b),
    .I_t1b_t1b(n446_I_t1b_t1b),
    .O_t0b_t0b(n446_O_t0b_t0b),
    .O_t0b_t1b(n446_O_t0b_t1b),
    .O_t1b_t0b(n446_O_t1b_t0b),
    .O_t1b_t1b(n446_O_t1b_t1b)
  );
  AtomTuple_11 n447 ( // @[Top.scala 1267:22]
    .valid_up(n447_valid_up),
    .valid_down(n447_valid_down),
    .I0(n447_I0),
    .I1_t0b_t0b(n447_I1_t0b_t0b),
    .I1_t0b_t1b(n447_I1_t0b_t1b),
    .I1_t1b_t0b(n447_I1_t1b_t0b),
    .I1_t1b_t1b(n447_I1_t1b_t1b),
    .O_t0b(n447_O_t0b),
    .O_t1b_t0b_t0b(n447_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n447_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n447_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n447_O_t1b_t1b_t1b)
  );
  If n448 ( // @[Top.scala 1271:22]
    .valid_up(n448_valid_up),
    .valid_down(n448_valid_down),
    .I_t0b(n448_I_t0b),
    .I_t1b_t0b_t0b(n448_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n448_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n448_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n448_I_t1b_t1b_t1b),
    .O_t0b(n448_O_t0b),
    .O_t1b(n448_O_t1b)
  );
  AtomTuple_1 n450 ( // @[Top.scala 1274:22]
    .valid_up(n450_valid_up),
    .valid_down(n450_valid_down),
    .I0(n450_I0),
    .I1_t0b(n450_I1_t0b),
    .I1_t1b(n450_I1_t1b),
    .O_t0b(n450_O_t0b),
    .O_t1b_t0b(n450_O_t1b_t0b),
    .O_t1b_t1b(n450_O_t1b_t1b)
  );
  assign valid_down = n450_valid_down; // @[Top.scala 1279:16]
  assign O_t0b = n450_O_t0b; // @[Top.scala 1278:7]
  assign O_t1b_t0b = n450_O_t1b_t0b; // @[Top.scala 1278:7]
  assign O_t1b_t1b = n450_O_t1b_t1b; // @[Top.scala 1278:7]
  assign n421_valid_up = valid_up; // @[Top.scala 1191:19]
  assign n421_I_t0b = I_t0b; // @[Top.scala 1190:12]
  assign n449_clock = clock;
  assign n449_reset = reset;
  assign n449_valid_up = n421_valid_down; // @[Top.scala 1194:19]
  assign n449_I = n421_O; // @[Top.scala 1193:12]
  assign n437_clock = clock;
  assign n437_reset = reset;
  assign n437_valid_up = n421_valid_down; // @[Top.scala 1197:19]
  assign n437_I = n421_O; // @[Top.scala 1196:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n423_valid_up = valid_up; // @[Top.scala 1201:19]
  assign n423_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1200:12]
  assign n423_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1200:12]
  assign n424_valid_up = n423_valid_down; // @[Top.scala 1204:19]
  assign n424_I_t0b = n423_O_t0b; // @[Top.scala 1203:12]
  assign n425_valid_up = n423_valid_down; // @[Top.scala 1207:19]
  assign n425_I_t1b = n423_O_t1b; // @[Top.scala 1206:12]
  assign n426_valid_up = n424_valid_down & n425_valid_down; // @[Top.scala 1211:19]
  assign n426_I0 = n424_O; // @[Top.scala 1209:13]
  assign n426_I1 = n425_O; // @[Top.scala 1210:13]
  assign n427_valid_up = n426_valid_down; // @[Top.scala 1214:19]
  assign n427_I_t0b = n426_O_t0b; // @[Top.scala 1213:12]
  assign n427_I_t1b = n426_O_t1b; // @[Top.scala 1213:12]
  assign n429_valid_up = InitialDelayCounter_valid_down & n427_valid_down; // @[Top.scala 1218:19]
  assign n429_I0 = 16'h1; // @[Top.scala 1216:13]
  assign n429_I1 = n427_O; // @[Top.scala 1217:13]
  assign n430_valid_up = n429_valid_down; // @[Top.scala 1221:19]
  assign n430_I_t0b = n429_O_t0b; // @[Top.scala 1220:12]
  assign n430_I_t1b = n429_O_t1b; // @[Top.scala 1220:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n433_valid_up = n430_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1226:19]
  assign n433_I0 = n430_O; // @[Top.scala 1224:13]
  assign n434_valid_up = n433_valid_down; // @[Top.scala 1229:19]
  assign n434_I_t0b = n433_O_t0b; // @[Top.scala 1228:12]
  assign n435_valid_up = n434_valid_down; // @[Top.scala 1233:19]
  assign n435_I0 = n434_O; // @[Top.scala 1231:13]
  assign n435_I1 = n434_O; // @[Top.scala 1232:13]
  assign n436_clock = clock;
  assign n436_reset = reset;
  assign n436_valid_up = n435_valid_down; // @[Top.scala 1236:19]
  assign n436_I_t0b = n435_O_t0b; // @[Top.scala 1235:12]
  assign n436_I_t1b = n435_O_t1b; // @[Top.scala 1235:12]
  assign n438_valid_up = n437_valid_down & n436_valid_down; // @[Top.scala 1240:19]
  assign n438_I0 = n437_O; // @[Top.scala 1238:13]
  assign n438_I1 = n436_O; // @[Top.scala 1239:13]
  assign n439_valid_up = n438_valid_down; // @[Top.scala 1243:19]
  assign n439_I_t0b = n438_O_t0b; // @[Top.scala 1242:12]
  assign n439_I_t1b = n438_O_t1b; // @[Top.scala 1242:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n441_valid_up = n434_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1248:19]
  assign n441_I0 = n434_O; // @[Top.scala 1246:13]
  assign n441_I1 = 16'h1; // @[Top.scala 1247:13]
  assign n442_valid_up = n441_valid_down; // @[Top.scala 1251:19]
  assign n442_I_t0b = n441_O_t0b; // @[Top.scala 1250:12]
  assign n442_I_t1b = n441_O_t1b; // @[Top.scala 1250:12]
  assign n443_valid_up = n424_valid_down & n442_valid_down; // @[Top.scala 1255:19]
  assign n443_I0 = n424_O; // @[Top.scala 1253:13]
  assign n443_I1 = n442_O; // @[Top.scala 1254:13]
  assign n444_valid_up = n434_valid_down & n425_valid_down; // @[Top.scala 1259:19]
  assign n444_I0 = n434_O; // @[Top.scala 1257:13]
  assign n444_I1 = n425_O; // @[Top.scala 1258:13]
  assign n445_valid_up = n443_valid_down & n444_valid_down; // @[Top.scala 1263:19]
  assign n445_I0_t0b = n443_O_t0b; // @[Top.scala 1261:13]
  assign n445_I0_t1b = n443_O_t1b; // @[Top.scala 1261:13]
  assign n445_I1_t0b = n444_O_t0b; // @[Top.scala 1262:13]
  assign n445_I1_t1b = n444_O_t1b; // @[Top.scala 1262:13]
  assign n446_clock = clock;
  assign n446_reset = reset;
  assign n446_valid_up = n445_valid_down; // @[Top.scala 1266:19]
  assign n446_I_t0b_t0b = n445_O_t0b_t0b; // @[Top.scala 1265:12]
  assign n446_I_t0b_t1b = n445_O_t0b_t1b; // @[Top.scala 1265:12]
  assign n446_I_t1b_t0b = n445_O_t1b_t0b; // @[Top.scala 1265:12]
  assign n446_I_t1b_t1b = n445_O_t1b_t1b; // @[Top.scala 1265:12]
  assign n447_valid_up = n439_valid_down & n446_valid_down; // @[Top.scala 1270:19]
  assign n447_I0 = n439_O[0]; // @[Top.scala 1268:13]
  assign n447_I1_t0b_t0b = n446_O_t0b_t0b; // @[Top.scala 1269:13]
  assign n447_I1_t0b_t1b = n446_O_t0b_t1b; // @[Top.scala 1269:13]
  assign n447_I1_t1b_t0b = n446_O_t1b_t0b; // @[Top.scala 1269:13]
  assign n447_I1_t1b_t1b = n446_O_t1b_t1b; // @[Top.scala 1269:13]
  assign n448_valid_up = n447_valid_down; // @[Top.scala 1273:19]
  assign n448_I_t0b = n447_O_t0b; // @[Top.scala 1272:12]
  assign n448_I_t1b_t0b_t0b = n447_O_t1b_t0b_t0b; // @[Top.scala 1272:12]
  assign n448_I_t1b_t0b_t1b = n447_O_t1b_t0b_t1b; // @[Top.scala 1272:12]
  assign n448_I_t1b_t1b_t0b = n447_O_t1b_t1b_t0b; // @[Top.scala 1272:12]
  assign n448_I_t1b_t1b_t1b = n447_O_t1b_t1b_t1b; // @[Top.scala 1272:12]
  assign n450_valid_up = n449_valid_down & n448_valid_down; // @[Top.scala 1277:19]
  assign n450_I0 = n449_O; // @[Top.scala 1275:13]
  assign n450_I1_t0b = n448_O_t0b; // @[Top.scala 1276:13]
  assign n450_I1_t1b = n448_O_t1b; // @[Top.scala 1276:13]
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
  wire  n455_valid_up; // @[Top.scala 1285:22]
  wire  n455_valid_down; // @[Top.scala 1285:22]
  wire [15:0] n455_I_t0b; // @[Top.scala 1285:22]
  wire [15:0] n455_O; // @[Top.scala 1285:22]
  wire  n483_clock; // @[Top.scala 1288:22]
  wire  n483_reset; // @[Top.scala 1288:22]
  wire  n483_valid_up; // @[Top.scala 1288:22]
  wire  n483_valid_down; // @[Top.scala 1288:22]
  wire [15:0] n483_I; // @[Top.scala 1288:22]
  wire [15:0] n483_O; // @[Top.scala 1288:22]
  wire  n471_clock; // @[Top.scala 1291:22]
  wire  n471_reset; // @[Top.scala 1291:22]
  wire  n471_valid_up; // @[Top.scala 1291:22]
  wire  n471_valid_down; // @[Top.scala 1291:22]
  wire [15:0] n471_I; // @[Top.scala 1291:22]
  wire [15:0] n471_O; // @[Top.scala 1291:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n457_valid_up; // @[Top.scala 1295:22]
  wire  n457_valid_down; // @[Top.scala 1295:22]
  wire [15:0] n457_I_t1b_t0b; // @[Top.scala 1295:22]
  wire [15:0] n457_I_t1b_t1b; // @[Top.scala 1295:22]
  wire [15:0] n457_O_t0b; // @[Top.scala 1295:22]
  wire [15:0] n457_O_t1b; // @[Top.scala 1295:22]
  wire  n458_valid_up; // @[Top.scala 1298:22]
  wire  n458_valid_down; // @[Top.scala 1298:22]
  wire [15:0] n458_I_t0b; // @[Top.scala 1298:22]
  wire [15:0] n458_O; // @[Top.scala 1298:22]
  wire  n459_valid_up; // @[Top.scala 1301:22]
  wire  n459_valid_down; // @[Top.scala 1301:22]
  wire [15:0] n459_I_t1b; // @[Top.scala 1301:22]
  wire [15:0] n459_O; // @[Top.scala 1301:22]
  wire  n460_valid_up; // @[Top.scala 1304:22]
  wire  n460_valid_down; // @[Top.scala 1304:22]
  wire [15:0] n460_I0; // @[Top.scala 1304:22]
  wire [15:0] n460_I1; // @[Top.scala 1304:22]
  wire [15:0] n460_O_t0b; // @[Top.scala 1304:22]
  wire [15:0] n460_O_t1b; // @[Top.scala 1304:22]
  wire  n461_valid_up; // @[Top.scala 1308:22]
  wire  n461_valid_down; // @[Top.scala 1308:22]
  wire [15:0] n461_I_t0b; // @[Top.scala 1308:22]
  wire [15:0] n461_I_t1b; // @[Top.scala 1308:22]
  wire [15:0] n461_O; // @[Top.scala 1308:22]
  wire  n463_valid_up; // @[Top.scala 1311:22]
  wire  n463_valid_down; // @[Top.scala 1311:22]
  wire [15:0] n463_I0; // @[Top.scala 1311:22]
  wire [15:0] n463_I1; // @[Top.scala 1311:22]
  wire [15:0] n463_O_t0b; // @[Top.scala 1311:22]
  wire [15:0] n463_O_t1b; // @[Top.scala 1311:22]
  wire  n464_valid_up; // @[Top.scala 1315:22]
  wire  n464_valid_down; // @[Top.scala 1315:22]
  wire [15:0] n464_I_t0b; // @[Top.scala 1315:22]
  wire [15:0] n464_I_t1b; // @[Top.scala 1315:22]
  wire [15:0] n464_O; // @[Top.scala 1315:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n467_valid_up; // @[Top.scala 1319:22]
  wire  n467_valid_down; // @[Top.scala 1319:22]
  wire [15:0] n467_I0; // @[Top.scala 1319:22]
  wire [15:0] n467_O_t0b; // @[Top.scala 1319:22]
  wire  n468_valid_up; // @[Top.scala 1323:22]
  wire  n468_valid_down; // @[Top.scala 1323:22]
  wire [15:0] n468_I_t0b; // @[Top.scala 1323:22]
  wire [15:0] n468_O; // @[Top.scala 1323:22]
  wire  n469_valid_up; // @[Top.scala 1326:22]
  wire  n469_valid_down; // @[Top.scala 1326:22]
  wire [15:0] n469_I0; // @[Top.scala 1326:22]
  wire [15:0] n469_I1; // @[Top.scala 1326:22]
  wire [15:0] n469_O_t0b; // @[Top.scala 1326:22]
  wire [15:0] n469_O_t1b; // @[Top.scala 1326:22]
  wire  n470_clock; // @[Top.scala 1330:22]
  wire  n470_reset; // @[Top.scala 1330:22]
  wire  n470_valid_up; // @[Top.scala 1330:22]
  wire  n470_valid_down; // @[Top.scala 1330:22]
  wire [15:0] n470_I_t0b; // @[Top.scala 1330:22]
  wire [15:0] n470_I_t1b; // @[Top.scala 1330:22]
  wire [15:0] n470_O; // @[Top.scala 1330:22]
  wire  n472_valid_up; // @[Top.scala 1333:22]
  wire  n472_valid_down; // @[Top.scala 1333:22]
  wire [15:0] n472_I0; // @[Top.scala 1333:22]
  wire [15:0] n472_I1; // @[Top.scala 1333:22]
  wire [15:0] n472_O_t0b; // @[Top.scala 1333:22]
  wire [15:0] n472_O_t1b; // @[Top.scala 1333:22]
  wire  n473_valid_up; // @[Top.scala 1337:22]
  wire  n473_valid_down; // @[Top.scala 1337:22]
  wire [15:0] n473_I_t0b; // @[Top.scala 1337:22]
  wire [15:0] n473_I_t1b; // @[Top.scala 1337:22]
  wire [15:0] n473_O; // @[Top.scala 1337:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n475_valid_up; // @[Top.scala 1341:22]
  wire  n475_valid_down; // @[Top.scala 1341:22]
  wire [15:0] n475_I0; // @[Top.scala 1341:22]
  wire [15:0] n475_I1; // @[Top.scala 1341:22]
  wire [15:0] n475_O_t0b; // @[Top.scala 1341:22]
  wire [15:0] n475_O_t1b; // @[Top.scala 1341:22]
  wire  n476_valid_up; // @[Top.scala 1345:22]
  wire  n476_valid_down; // @[Top.scala 1345:22]
  wire [15:0] n476_I_t0b; // @[Top.scala 1345:22]
  wire [15:0] n476_I_t1b; // @[Top.scala 1345:22]
  wire [15:0] n476_O; // @[Top.scala 1345:22]
  wire  n477_valid_up; // @[Top.scala 1348:22]
  wire  n477_valid_down; // @[Top.scala 1348:22]
  wire [15:0] n477_I0; // @[Top.scala 1348:22]
  wire [15:0] n477_I1; // @[Top.scala 1348:22]
  wire [15:0] n477_O_t0b; // @[Top.scala 1348:22]
  wire [15:0] n477_O_t1b; // @[Top.scala 1348:22]
  wire  n478_valid_up; // @[Top.scala 1352:22]
  wire  n478_valid_down; // @[Top.scala 1352:22]
  wire [15:0] n478_I0; // @[Top.scala 1352:22]
  wire [15:0] n478_I1; // @[Top.scala 1352:22]
  wire [15:0] n478_O_t0b; // @[Top.scala 1352:22]
  wire [15:0] n478_O_t1b; // @[Top.scala 1352:22]
  wire  n479_valid_up; // @[Top.scala 1356:22]
  wire  n479_valid_down; // @[Top.scala 1356:22]
  wire [15:0] n479_I0_t0b; // @[Top.scala 1356:22]
  wire [15:0] n479_I0_t1b; // @[Top.scala 1356:22]
  wire [15:0] n479_I1_t0b; // @[Top.scala 1356:22]
  wire [15:0] n479_I1_t1b; // @[Top.scala 1356:22]
  wire [15:0] n479_O_t0b_t0b; // @[Top.scala 1356:22]
  wire [15:0] n479_O_t0b_t1b; // @[Top.scala 1356:22]
  wire [15:0] n479_O_t1b_t0b; // @[Top.scala 1356:22]
  wire [15:0] n479_O_t1b_t1b; // @[Top.scala 1356:22]
  wire  n480_clock; // @[Top.scala 1360:22]
  wire  n480_reset; // @[Top.scala 1360:22]
  wire  n480_valid_up; // @[Top.scala 1360:22]
  wire  n480_valid_down; // @[Top.scala 1360:22]
  wire [15:0] n480_I_t0b_t0b; // @[Top.scala 1360:22]
  wire [15:0] n480_I_t0b_t1b; // @[Top.scala 1360:22]
  wire [15:0] n480_I_t1b_t0b; // @[Top.scala 1360:22]
  wire [15:0] n480_I_t1b_t1b; // @[Top.scala 1360:22]
  wire [15:0] n480_O_t0b_t0b; // @[Top.scala 1360:22]
  wire [15:0] n480_O_t0b_t1b; // @[Top.scala 1360:22]
  wire [15:0] n480_O_t1b_t0b; // @[Top.scala 1360:22]
  wire [15:0] n480_O_t1b_t1b; // @[Top.scala 1360:22]
  wire  n481_valid_up; // @[Top.scala 1363:22]
  wire  n481_valid_down; // @[Top.scala 1363:22]
  wire  n481_I0; // @[Top.scala 1363:22]
  wire [15:0] n481_I1_t0b_t0b; // @[Top.scala 1363:22]
  wire [15:0] n481_I1_t0b_t1b; // @[Top.scala 1363:22]
  wire [15:0] n481_I1_t1b_t0b; // @[Top.scala 1363:22]
  wire [15:0] n481_I1_t1b_t1b; // @[Top.scala 1363:22]
  wire  n481_O_t0b; // @[Top.scala 1363:22]
  wire [15:0] n481_O_t1b_t0b_t0b; // @[Top.scala 1363:22]
  wire [15:0] n481_O_t1b_t0b_t1b; // @[Top.scala 1363:22]
  wire [15:0] n481_O_t1b_t1b_t0b; // @[Top.scala 1363:22]
  wire [15:0] n481_O_t1b_t1b_t1b; // @[Top.scala 1363:22]
  wire  n482_valid_up; // @[Top.scala 1367:22]
  wire  n482_valid_down; // @[Top.scala 1367:22]
  wire  n482_I_t0b; // @[Top.scala 1367:22]
  wire [15:0] n482_I_t1b_t0b_t0b; // @[Top.scala 1367:22]
  wire [15:0] n482_I_t1b_t0b_t1b; // @[Top.scala 1367:22]
  wire [15:0] n482_I_t1b_t1b_t0b; // @[Top.scala 1367:22]
  wire [15:0] n482_I_t1b_t1b_t1b; // @[Top.scala 1367:22]
  wire [15:0] n482_O_t0b; // @[Top.scala 1367:22]
  wire [15:0] n482_O_t1b; // @[Top.scala 1367:22]
  wire  n484_valid_up; // @[Top.scala 1370:22]
  wire  n484_valid_down; // @[Top.scala 1370:22]
  wire [15:0] n484_I0; // @[Top.scala 1370:22]
  wire [15:0] n484_I1_t0b; // @[Top.scala 1370:22]
  wire [15:0] n484_I1_t1b; // @[Top.scala 1370:22]
  wire [15:0] n484_O_t0b; // @[Top.scala 1370:22]
  wire [15:0] n484_O_t1b_t0b; // @[Top.scala 1370:22]
  wire [15:0] n484_O_t1b_t1b; // @[Top.scala 1370:22]
  Fst n455 ( // @[Top.scala 1285:22]
    .valid_up(n455_valid_up),
    .valid_down(n455_valid_down),
    .I_t0b(n455_I_t0b),
    .O(n455_O)
  );
  FIFO_2 n483 ( // @[Top.scala 1288:22]
    .clock(n483_clock),
    .reset(n483_reset),
    .valid_up(n483_valid_up),
    .valid_down(n483_valid_down),
    .I(n483_I),
    .O(n483_O)
  );
  FIFO_2 n471 ( // @[Top.scala 1291:22]
    .clock(n471_clock),
    .reset(n471_reset),
    .valid_up(n471_valid_up),
    .valid_down(n471_valid_down),
    .I(n471_I),
    .O(n471_O)
  );
  InitialDelayCounter_39 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n457 ( // @[Top.scala 1295:22]
    .valid_up(n457_valid_up),
    .valid_down(n457_valid_down),
    .I_t1b_t0b(n457_I_t1b_t0b),
    .I_t1b_t1b(n457_I_t1b_t1b),
    .O_t0b(n457_O_t0b),
    .O_t1b(n457_O_t1b)
  );
  Fst_1 n458 ( // @[Top.scala 1298:22]
    .valid_up(n458_valid_up),
    .valid_down(n458_valid_down),
    .I_t0b(n458_I_t0b),
    .O(n458_O)
  );
  Snd_1 n459 ( // @[Top.scala 1301:22]
    .valid_up(n459_valid_up),
    .valid_down(n459_valid_down),
    .I_t1b(n459_I_t1b),
    .O(n459_O)
  );
  AtomTuple n460 ( // @[Top.scala 1304:22]
    .valid_up(n460_valid_up),
    .valid_down(n460_valid_down),
    .I0(n460_I0),
    .I1(n460_I1),
    .O_t0b(n460_O_t0b),
    .O_t1b(n460_O_t1b)
  );
  Add n461 ( // @[Top.scala 1308:22]
    .valid_up(n461_valid_up),
    .valid_down(n461_valid_down),
    .I_t0b(n461_I_t0b),
    .I_t1b(n461_I_t1b),
    .O(n461_O)
  );
  AtomTuple n463 ( // @[Top.scala 1311:22]
    .valid_up(n463_valid_up),
    .valid_down(n463_valid_down),
    .I0(n463_I0),
    .I1(n463_I1),
    .O_t0b(n463_O_t0b),
    .O_t1b(n463_O_t1b)
  );
  Add n464 ( // @[Top.scala 1315:22]
    .valid_up(n464_valid_up),
    .valid_down(n464_valid_down),
    .I_t0b(n464_I_t0b),
    .I_t1b(n464_I_t1b),
    .O(n464_O)
  );
  InitialDelayCounter_39 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n467 ( // @[Top.scala 1319:22]
    .valid_up(n467_valid_up),
    .valid_down(n467_valid_down),
    .I0(n467_I0),
    .O_t0b(n467_O_t0b)
  );
  RShift n468 ( // @[Top.scala 1323:22]
    .valid_up(n468_valid_up),
    .valid_down(n468_valid_down),
    .I_t0b(n468_I_t0b),
    .O(n468_O)
  );
  AtomTuple n469 ( // @[Top.scala 1326:22]
    .valid_up(n469_valid_up),
    .valid_down(n469_valid_down),
    .I0(n469_I0),
    .I1(n469_I1),
    .O_t0b(n469_O_t0b),
    .O_t1b(n469_O_t1b)
  );
  Mul n470 ( // @[Top.scala 1330:22]
    .clock(n470_clock),
    .reset(n470_reset),
    .valid_up(n470_valid_up),
    .valid_down(n470_valid_down),
    .I_t0b(n470_I_t0b),
    .I_t1b(n470_I_t1b),
    .O(n470_O)
  );
  AtomTuple n472 ( // @[Top.scala 1333:22]
    .valid_up(n472_valid_up),
    .valid_down(n472_valid_down),
    .I0(n472_I0),
    .I1(n472_I1),
    .O_t0b(n472_O_t0b),
    .O_t1b(n472_O_t1b)
  );
  Lt n473 ( // @[Top.scala 1337:22]
    .valid_up(n473_valid_up),
    .valid_down(n473_valid_down),
    .I_t0b(n473_I_t0b),
    .I_t1b(n473_I_t1b),
    .O(n473_O)
  );
  InitialDelayCounter_39 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n475 ( // @[Top.scala 1341:22]
    .valid_up(n475_valid_up),
    .valid_down(n475_valid_down),
    .I0(n475_I0),
    .I1(n475_I1),
    .O_t0b(n475_O_t0b),
    .O_t1b(n475_O_t1b)
  );
  Sub n476 ( // @[Top.scala 1345:22]
    .valid_up(n476_valid_up),
    .valid_down(n476_valid_down),
    .I_t0b(n476_I_t0b),
    .I_t1b(n476_I_t1b),
    .O(n476_O)
  );
  AtomTuple n477 ( // @[Top.scala 1348:22]
    .valid_up(n477_valid_up),
    .valid_down(n477_valid_down),
    .I0(n477_I0),
    .I1(n477_I1),
    .O_t0b(n477_O_t0b),
    .O_t1b(n477_O_t1b)
  );
  AtomTuple n478 ( // @[Top.scala 1352:22]
    .valid_up(n478_valid_up),
    .valid_down(n478_valid_down),
    .I0(n478_I0),
    .I1(n478_I1),
    .O_t0b(n478_O_t0b),
    .O_t1b(n478_O_t1b)
  );
  AtomTuple_10 n479 ( // @[Top.scala 1356:22]
    .valid_up(n479_valid_up),
    .valid_down(n479_valid_down),
    .I0_t0b(n479_I0_t0b),
    .I0_t1b(n479_I0_t1b),
    .I1_t0b(n479_I1_t0b),
    .I1_t1b(n479_I1_t1b),
    .O_t0b_t0b(n479_O_t0b_t0b),
    .O_t0b_t1b(n479_O_t0b_t1b),
    .O_t1b_t0b(n479_O_t1b_t0b),
    .O_t1b_t1b(n479_O_t1b_t1b)
  );
  FIFO_4 n480 ( // @[Top.scala 1360:22]
    .clock(n480_clock),
    .reset(n480_reset),
    .valid_up(n480_valid_up),
    .valid_down(n480_valid_down),
    .I_t0b_t0b(n480_I_t0b_t0b),
    .I_t0b_t1b(n480_I_t0b_t1b),
    .I_t1b_t0b(n480_I_t1b_t0b),
    .I_t1b_t1b(n480_I_t1b_t1b),
    .O_t0b_t0b(n480_O_t0b_t0b),
    .O_t0b_t1b(n480_O_t0b_t1b),
    .O_t1b_t0b(n480_O_t1b_t0b),
    .O_t1b_t1b(n480_O_t1b_t1b)
  );
  AtomTuple_11 n481 ( // @[Top.scala 1363:22]
    .valid_up(n481_valid_up),
    .valid_down(n481_valid_down),
    .I0(n481_I0),
    .I1_t0b_t0b(n481_I1_t0b_t0b),
    .I1_t0b_t1b(n481_I1_t0b_t1b),
    .I1_t1b_t0b(n481_I1_t1b_t0b),
    .I1_t1b_t1b(n481_I1_t1b_t1b),
    .O_t0b(n481_O_t0b),
    .O_t1b_t0b_t0b(n481_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n481_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n481_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n481_O_t1b_t1b_t1b)
  );
  If n482 ( // @[Top.scala 1367:22]
    .valid_up(n482_valid_up),
    .valid_down(n482_valid_down),
    .I_t0b(n482_I_t0b),
    .I_t1b_t0b_t0b(n482_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n482_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n482_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n482_I_t1b_t1b_t1b),
    .O_t0b(n482_O_t0b),
    .O_t1b(n482_O_t1b)
  );
  AtomTuple_1 n484 ( // @[Top.scala 1370:22]
    .valid_up(n484_valid_up),
    .valid_down(n484_valid_down),
    .I0(n484_I0),
    .I1_t0b(n484_I1_t0b),
    .I1_t1b(n484_I1_t1b),
    .O_t0b(n484_O_t0b),
    .O_t1b_t0b(n484_O_t1b_t0b),
    .O_t1b_t1b(n484_O_t1b_t1b)
  );
  assign valid_down = n484_valid_down; // @[Top.scala 1375:16]
  assign O_t0b = n484_O_t0b; // @[Top.scala 1374:7]
  assign O_t1b_t0b = n484_O_t1b_t0b; // @[Top.scala 1374:7]
  assign O_t1b_t1b = n484_O_t1b_t1b; // @[Top.scala 1374:7]
  assign n455_valid_up = valid_up; // @[Top.scala 1287:19]
  assign n455_I_t0b = I_t0b; // @[Top.scala 1286:12]
  assign n483_clock = clock;
  assign n483_reset = reset;
  assign n483_valid_up = n455_valid_down; // @[Top.scala 1290:19]
  assign n483_I = n455_O; // @[Top.scala 1289:12]
  assign n471_clock = clock;
  assign n471_reset = reset;
  assign n471_valid_up = n455_valid_down; // @[Top.scala 1293:19]
  assign n471_I = n455_O; // @[Top.scala 1292:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n457_valid_up = valid_up; // @[Top.scala 1297:19]
  assign n457_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1296:12]
  assign n457_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1296:12]
  assign n458_valid_up = n457_valid_down; // @[Top.scala 1300:19]
  assign n458_I_t0b = n457_O_t0b; // @[Top.scala 1299:12]
  assign n459_valid_up = n457_valid_down; // @[Top.scala 1303:19]
  assign n459_I_t1b = n457_O_t1b; // @[Top.scala 1302:12]
  assign n460_valid_up = n458_valid_down & n459_valid_down; // @[Top.scala 1307:19]
  assign n460_I0 = n458_O; // @[Top.scala 1305:13]
  assign n460_I1 = n459_O; // @[Top.scala 1306:13]
  assign n461_valid_up = n460_valid_down; // @[Top.scala 1310:19]
  assign n461_I_t0b = n460_O_t0b; // @[Top.scala 1309:12]
  assign n461_I_t1b = n460_O_t1b; // @[Top.scala 1309:12]
  assign n463_valid_up = InitialDelayCounter_valid_down & n461_valid_down; // @[Top.scala 1314:19]
  assign n463_I0 = 16'h1; // @[Top.scala 1312:13]
  assign n463_I1 = n461_O; // @[Top.scala 1313:13]
  assign n464_valid_up = n463_valid_down; // @[Top.scala 1317:19]
  assign n464_I_t0b = n463_O_t0b; // @[Top.scala 1316:12]
  assign n464_I_t1b = n463_O_t1b; // @[Top.scala 1316:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n467_valid_up = n464_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1322:19]
  assign n467_I0 = n464_O; // @[Top.scala 1320:13]
  assign n468_valid_up = n467_valid_down; // @[Top.scala 1325:19]
  assign n468_I_t0b = n467_O_t0b; // @[Top.scala 1324:12]
  assign n469_valid_up = n468_valid_down; // @[Top.scala 1329:19]
  assign n469_I0 = n468_O; // @[Top.scala 1327:13]
  assign n469_I1 = n468_O; // @[Top.scala 1328:13]
  assign n470_clock = clock;
  assign n470_reset = reset;
  assign n470_valid_up = n469_valid_down; // @[Top.scala 1332:19]
  assign n470_I_t0b = n469_O_t0b; // @[Top.scala 1331:12]
  assign n470_I_t1b = n469_O_t1b; // @[Top.scala 1331:12]
  assign n472_valid_up = n471_valid_down & n470_valid_down; // @[Top.scala 1336:19]
  assign n472_I0 = n471_O; // @[Top.scala 1334:13]
  assign n472_I1 = n470_O; // @[Top.scala 1335:13]
  assign n473_valid_up = n472_valid_down; // @[Top.scala 1339:19]
  assign n473_I_t0b = n472_O_t0b; // @[Top.scala 1338:12]
  assign n473_I_t1b = n472_O_t1b; // @[Top.scala 1338:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n475_valid_up = n468_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1344:19]
  assign n475_I0 = n468_O; // @[Top.scala 1342:13]
  assign n475_I1 = 16'h1; // @[Top.scala 1343:13]
  assign n476_valid_up = n475_valid_down; // @[Top.scala 1347:19]
  assign n476_I_t0b = n475_O_t0b; // @[Top.scala 1346:12]
  assign n476_I_t1b = n475_O_t1b; // @[Top.scala 1346:12]
  assign n477_valid_up = n458_valid_down & n476_valid_down; // @[Top.scala 1351:19]
  assign n477_I0 = n458_O; // @[Top.scala 1349:13]
  assign n477_I1 = n476_O; // @[Top.scala 1350:13]
  assign n478_valid_up = n468_valid_down & n459_valid_down; // @[Top.scala 1355:19]
  assign n478_I0 = n468_O; // @[Top.scala 1353:13]
  assign n478_I1 = n459_O; // @[Top.scala 1354:13]
  assign n479_valid_up = n477_valid_down & n478_valid_down; // @[Top.scala 1359:19]
  assign n479_I0_t0b = n477_O_t0b; // @[Top.scala 1357:13]
  assign n479_I0_t1b = n477_O_t1b; // @[Top.scala 1357:13]
  assign n479_I1_t0b = n478_O_t0b; // @[Top.scala 1358:13]
  assign n479_I1_t1b = n478_O_t1b; // @[Top.scala 1358:13]
  assign n480_clock = clock;
  assign n480_reset = reset;
  assign n480_valid_up = n479_valid_down; // @[Top.scala 1362:19]
  assign n480_I_t0b_t0b = n479_O_t0b_t0b; // @[Top.scala 1361:12]
  assign n480_I_t0b_t1b = n479_O_t0b_t1b; // @[Top.scala 1361:12]
  assign n480_I_t1b_t0b = n479_O_t1b_t0b; // @[Top.scala 1361:12]
  assign n480_I_t1b_t1b = n479_O_t1b_t1b; // @[Top.scala 1361:12]
  assign n481_valid_up = n473_valid_down & n480_valid_down; // @[Top.scala 1366:19]
  assign n481_I0 = n473_O[0]; // @[Top.scala 1364:13]
  assign n481_I1_t0b_t0b = n480_O_t0b_t0b; // @[Top.scala 1365:13]
  assign n481_I1_t0b_t1b = n480_O_t0b_t1b; // @[Top.scala 1365:13]
  assign n481_I1_t1b_t0b = n480_O_t1b_t0b; // @[Top.scala 1365:13]
  assign n481_I1_t1b_t1b = n480_O_t1b_t1b; // @[Top.scala 1365:13]
  assign n482_valid_up = n481_valid_down; // @[Top.scala 1369:19]
  assign n482_I_t0b = n481_O_t0b; // @[Top.scala 1368:12]
  assign n482_I_t1b_t0b_t0b = n481_O_t1b_t0b_t0b; // @[Top.scala 1368:12]
  assign n482_I_t1b_t0b_t1b = n481_O_t1b_t0b_t1b; // @[Top.scala 1368:12]
  assign n482_I_t1b_t1b_t0b = n481_O_t1b_t1b_t0b; // @[Top.scala 1368:12]
  assign n482_I_t1b_t1b_t1b = n481_O_t1b_t1b_t1b; // @[Top.scala 1368:12]
  assign n484_valid_up = n483_valid_down & n482_valid_down; // @[Top.scala 1373:19]
  assign n484_I0 = n483_O; // @[Top.scala 1371:13]
  assign n484_I1_t0b = n482_O_t0b; // @[Top.scala 1372:13]
  assign n484_I1_t1b = n482_O_t1b; // @[Top.scala 1372:13]
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
  wire  n489_valid_up; // @[Top.scala 1381:22]
  wire  n489_valid_down; // @[Top.scala 1381:22]
  wire [15:0] n489_I_t0b; // @[Top.scala 1381:22]
  wire [15:0] n489_O; // @[Top.scala 1381:22]
  wire  n517_clock; // @[Top.scala 1384:22]
  wire  n517_reset; // @[Top.scala 1384:22]
  wire  n517_valid_up; // @[Top.scala 1384:22]
  wire  n517_valid_down; // @[Top.scala 1384:22]
  wire [15:0] n517_I; // @[Top.scala 1384:22]
  wire [15:0] n517_O; // @[Top.scala 1384:22]
  wire  n505_clock; // @[Top.scala 1387:22]
  wire  n505_reset; // @[Top.scala 1387:22]
  wire  n505_valid_up; // @[Top.scala 1387:22]
  wire  n505_valid_down; // @[Top.scala 1387:22]
  wire [15:0] n505_I; // @[Top.scala 1387:22]
  wire [15:0] n505_O; // @[Top.scala 1387:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n491_valid_up; // @[Top.scala 1391:22]
  wire  n491_valid_down; // @[Top.scala 1391:22]
  wire [15:0] n491_I_t1b_t0b; // @[Top.scala 1391:22]
  wire [15:0] n491_I_t1b_t1b; // @[Top.scala 1391:22]
  wire [15:0] n491_O_t0b; // @[Top.scala 1391:22]
  wire [15:0] n491_O_t1b; // @[Top.scala 1391:22]
  wire  n492_valid_up; // @[Top.scala 1394:22]
  wire  n492_valid_down; // @[Top.scala 1394:22]
  wire [15:0] n492_I_t0b; // @[Top.scala 1394:22]
  wire [15:0] n492_O; // @[Top.scala 1394:22]
  wire  n493_valid_up; // @[Top.scala 1397:22]
  wire  n493_valid_down; // @[Top.scala 1397:22]
  wire [15:0] n493_I_t1b; // @[Top.scala 1397:22]
  wire [15:0] n493_O; // @[Top.scala 1397:22]
  wire  n494_valid_up; // @[Top.scala 1400:22]
  wire  n494_valid_down; // @[Top.scala 1400:22]
  wire [15:0] n494_I0; // @[Top.scala 1400:22]
  wire [15:0] n494_I1; // @[Top.scala 1400:22]
  wire [15:0] n494_O_t0b; // @[Top.scala 1400:22]
  wire [15:0] n494_O_t1b; // @[Top.scala 1400:22]
  wire  n495_valid_up; // @[Top.scala 1404:22]
  wire  n495_valid_down; // @[Top.scala 1404:22]
  wire [15:0] n495_I_t0b; // @[Top.scala 1404:22]
  wire [15:0] n495_I_t1b; // @[Top.scala 1404:22]
  wire [15:0] n495_O; // @[Top.scala 1404:22]
  wire  n497_valid_up; // @[Top.scala 1407:22]
  wire  n497_valid_down; // @[Top.scala 1407:22]
  wire [15:0] n497_I0; // @[Top.scala 1407:22]
  wire [15:0] n497_I1; // @[Top.scala 1407:22]
  wire [15:0] n497_O_t0b; // @[Top.scala 1407:22]
  wire [15:0] n497_O_t1b; // @[Top.scala 1407:22]
  wire  n498_valid_up; // @[Top.scala 1411:22]
  wire  n498_valid_down; // @[Top.scala 1411:22]
  wire [15:0] n498_I_t0b; // @[Top.scala 1411:22]
  wire [15:0] n498_I_t1b; // @[Top.scala 1411:22]
  wire [15:0] n498_O; // @[Top.scala 1411:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n501_valid_up; // @[Top.scala 1415:22]
  wire  n501_valid_down; // @[Top.scala 1415:22]
  wire [15:0] n501_I0; // @[Top.scala 1415:22]
  wire [15:0] n501_O_t0b; // @[Top.scala 1415:22]
  wire  n502_valid_up; // @[Top.scala 1419:22]
  wire  n502_valid_down; // @[Top.scala 1419:22]
  wire [15:0] n502_I_t0b; // @[Top.scala 1419:22]
  wire [15:0] n502_O; // @[Top.scala 1419:22]
  wire  n503_valid_up; // @[Top.scala 1422:22]
  wire  n503_valid_down; // @[Top.scala 1422:22]
  wire [15:0] n503_I0; // @[Top.scala 1422:22]
  wire [15:0] n503_I1; // @[Top.scala 1422:22]
  wire [15:0] n503_O_t0b; // @[Top.scala 1422:22]
  wire [15:0] n503_O_t1b; // @[Top.scala 1422:22]
  wire  n504_clock; // @[Top.scala 1426:22]
  wire  n504_reset; // @[Top.scala 1426:22]
  wire  n504_valid_up; // @[Top.scala 1426:22]
  wire  n504_valid_down; // @[Top.scala 1426:22]
  wire [15:0] n504_I_t0b; // @[Top.scala 1426:22]
  wire [15:0] n504_I_t1b; // @[Top.scala 1426:22]
  wire [15:0] n504_O; // @[Top.scala 1426:22]
  wire  n506_valid_up; // @[Top.scala 1429:22]
  wire  n506_valid_down; // @[Top.scala 1429:22]
  wire [15:0] n506_I0; // @[Top.scala 1429:22]
  wire [15:0] n506_I1; // @[Top.scala 1429:22]
  wire [15:0] n506_O_t0b; // @[Top.scala 1429:22]
  wire [15:0] n506_O_t1b; // @[Top.scala 1429:22]
  wire  n507_valid_up; // @[Top.scala 1433:22]
  wire  n507_valid_down; // @[Top.scala 1433:22]
  wire [15:0] n507_I_t0b; // @[Top.scala 1433:22]
  wire [15:0] n507_I_t1b; // @[Top.scala 1433:22]
  wire [15:0] n507_O; // @[Top.scala 1433:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n509_valid_up; // @[Top.scala 1437:22]
  wire  n509_valid_down; // @[Top.scala 1437:22]
  wire [15:0] n509_I0; // @[Top.scala 1437:22]
  wire [15:0] n509_I1; // @[Top.scala 1437:22]
  wire [15:0] n509_O_t0b; // @[Top.scala 1437:22]
  wire [15:0] n509_O_t1b; // @[Top.scala 1437:22]
  wire  n510_valid_up; // @[Top.scala 1441:22]
  wire  n510_valid_down; // @[Top.scala 1441:22]
  wire [15:0] n510_I_t0b; // @[Top.scala 1441:22]
  wire [15:0] n510_I_t1b; // @[Top.scala 1441:22]
  wire [15:0] n510_O; // @[Top.scala 1441:22]
  wire  n511_valid_up; // @[Top.scala 1444:22]
  wire  n511_valid_down; // @[Top.scala 1444:22]
  wire [15:0] n511_I0; // @[Top.scala 1444:22]
  wire [15:0] n511_I1; // @[Top.scala 1444:22]
  wire [15:0] n511_O_t0b; // @[Top.scala 1444:22]
  wire [15:0] n511_O_t1b; // @[Top.scala 1444:22]
  wire  n512_valid_up; // @[Top.scala 1448:22]
  wire  n512_valid_down; // @[Top.scala 1448:22]
  wire [15:0] n512_I0; // @[Top.scala 1448:22]
  wire [15:0] n512_I1; // @[Top.scala 1448:22]
  wire [15:0] n512_O_t0b; // @[Top.scala 1448:22]
  wire [15:0] n512_O_t1b; // @[Top.scala 1448:22]
  wire  n513_valid_up; // @[Top.scala 1452:22]
  wire  n513_valid_down; // @[Top.scala 1452:22]
  wire [15:0] n513_I0_t0b; // @[Top.scala 1452:22]
  wire [15:0] n513_I0_t1b; // @[Top.scala 1452:22]
  wire [15:0] n513_I1_t0b; // @[Top.scala 1452:22]
  wire [15:0] n513_I1_t1b; // @[Top.scala 1452:22]
  wire [15:0] n513_O_t0b_t0b; // @[Top.scala 1452:22]
  wire [15:0] n513_O_t0b_t1b; // @[Top.scala 1452:22]
  wire [15:0] n513_O_t1b_t0b; // @[Top.scala 1452:22]
  wire [15:0] n513_O_t1b_t1b; // @[Top.scala 1452:22]
  wire  n514_clock; // @[Top.scala 1456:22]
  wire  n514_reset; // @[Top.scala 1456:22]
  wire  n514_valid_up; // @[Top.scala 1456:22]
  wire  n514_valid_down; // @[Top.scala 1456:22]
  wire [15:0] n514_I_t0b_t0b; // @[Top.scala 1456:22]
  wire [15:0] n514_I_t0b_t1b; // @[Top.scala 1456:22]
  wire [15:0] n514_I_t1b_t0b; // @[Top.scala 1456:22]
  wire [15:0] n514_I_t1b_t1b; // @[Top.scala 1456:22]
  wire [15:0] n514_O_t0b_t0b; // @[Top.scala 1456:22]
  wire [15:0] n514_O_t0b_t1b; // @[Top.scala 1456:22]
  wire [15:0] n514_O_t1b_t0b; // @[Top.scala 1456:22]
  wire [15:0] n514_O_t1b_t1b; // @[Top.scala 1456:22]
  wire  n515_valid_up; // @[Top.scala 1459:22]
  wire  n515_valid_down; // @[Top.scala 1459:22]
  wire  n515_I0; // @[Top.scala 1459:22]
  wire [15:0] n515_I1_t0b_t0b; // @[Top.scala 1459:22]
  wire [15:0] n515_I1_t0b_t1b; // @[Top.scala 1459:22]
  wire [15:0] n515_I1_t1b_t0b; // @[Top.scala 1459:22]
  wire [15:0] n515_I1_t1b_t1b; // @[Top.scala 1459:22]
  wire  n515_O_t0b; // @[Top.scala 1459:22]
  wire [15:0] n515_O_t1b_t0b_t0b; // @[Top.scala 1459:22]
  wire [15:0] n515_O_t1b_t0b_t1b; // @[Top.scala 1459:22]
  wire [15:0] n515_O_t1b_t1b_t0b; // @[Top.scala 1459:22]
  wire [15:0] n515_O_t1b_t1b_t1b; // @[Top.scala 1459:22]
  wire  n516_valid_up; // @[Top.scala 1463:22]
  wire  n516_valid_down; // @[Top.scala 1463:22]
  wire  n516_I_t0b; // @[Top.scala 1463:22]
  wire [15:0] n516_I_t1b_t0b_t0b; // @[Top.scala 1463:22]
  wire [15:0] n516_I_t1b_t0b_t1b; // @[Top.scala 1463:22]
  wire [15:0] n516_I_t1b_t1b_t0b; // @[Top.scala 1463:22]
  wire [15:0] n516_I_t1b_t1b_t1b; // @[Top.scala 1463:22]
  wire [15:0] n516_O_t0b; // @[Top.scala 1463:22]
  wire [15:0] n516_O_t1b; // @[Top.scala 1463:22]
  wire  n518_valid_up; // @[Top.scala 1466:22]
  wire  n518_valid_down; // @[Top.scala 1466:22]
  wire [15:0] n518_I0; // @[Top.scala 1466:22]
  wire [15:0] n518_I1_t0b; // @[Top.scala 1466:22]
  wire [15:0] n518_I1_t1b; // @[Top.scala 1466:22]
  wire [15:0] n518_O_t0b; // @[Top.scala 1466:22]
  wire [15:0] n518_O_t1b_t0b; // @[Top.scala 1466:22]
  wire [15:0] n518_O_t1b_t1b; // @[Top.scala 1466:22]
  Fst n489 ( // @[Top.scala 1381:22]
    .valid_up(n489_valid_up),
    .valid_down(n489_valid_down),
    .I_t0b(n489_I_t0b),
    .O(n489_O)
  );
  FIFO_2 n517 ( // @[Top.scala 1384:22]
    .clock(n517_clock),
    .reset(n517_reset),
    .valid_up(n517_valid_up),
    .valid_down(n517_valid_down),
    .I(n517_I),
    .O(n517_O)
  );
  FIFO_2 n505 ( // @[Top.scala 1387:22]
    .clock(n505_clock),
    .reset(n505_reset),
    .valid_up(n505_valid_up),
    .valid_down(n505_valid_down),
    .I(n505_I),
    .O(n505_O)
  );
  InitialDelayCounter_42 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n491 ( // @[Top.scala 1391:22]
    .valid_up(n491_valid_up),
    .valid_down(n491_valid_down),
    .I_t1b_t0b(n491_I_t1b_t0b),
    .I_t1b_t1b(n491_I_t1b_t1b),
    .O_t0b(n491_O_t0b),
    .O_t1b(n491_O_t1b)
  );
  Fst_1 n492 ( // @[Top.scala 1394:22]
    .valid_up(n492_valid_up),
    .valid_down(n492_valid_down),
    .I_t0b(n492_I_t0b),
    .O(n492_O)
  );
  Snd_1 n493 ( // @[Top.scala 1397:22]
    .valid_up(n493_valid_up),
    .valid_down(n493_valid_down),
    .I_t1b(n493_I_t1b),
    .O(n493_O)
  );
  AtomTuple n494 ( // @[Top.scala 1400:22]
    .valid_up(n494_valid_up),
    .valid_down(n494_valid_down),
    .I0(n494_I0),
    .I1(n494_I1),
    .O_t0b(n494_O_t0b),
    .O_t1b(n494_O_t1b)
  );
  Add n495 ( // @[Top.scala 1404:22]
    .valid_up(n495_valid_up),
    .valid_down(n495_valid_down),
    .I_t0b(n495_I_t0b),
    .I_t1b(n495_I_t1b),
    .O(n495_O)
  );
  AtomTuple n497 ( // @[Top.scala 1407:22]
    .valid_up(n497_valid_up),
    .valid_down(n497_valid_down),
    .I0(n497_I0),
    .I1(n497_I1),
    .O_t0b(n497_O_t0b),
    .O_t1b(n497_O_t1b)
  );
  Add n498 ( // @[Top.scala 1411:22]
    .valid_up(n498_valid_up),
    .valid_down(n498_valid_down),
    .I_t0b(n498_I_t0b),
    .I_t1b(n498_I_t1b),
    .O(n498_O)
  );
  InitialDelayCounter_42 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n501 ( // @[Top.scala 1415:22]
    .valid_up(n501_valid_up),
    .valid_down(n501_valid_down),
    .I0(n501_I0),
    .O_t0b(n501_O_t0b)
  );
  RShift n502 ( // @[Top.scala 1419:22]
    .valid_up(n502_valid_up),
    .valid_down(n502_valid_down),
    .I_t0b(n502_I_t0b),
    .O(n502_O)
  );
  AtomTuple n503 ( // @[Top.scala 1422:22]
    .valid_up(n503_valid_up),
    .valid_down(n503_valid_down),
    .I0(n503_I0),
    .I1(n503_I1),
    .O_t0b(n503_O_t0b),
    .O_t1b(n503_O_t1b)
  );
  Mul n504 ( // @[Top.scala 1426:22]
    .clock(n504_clock),
    .reset(n504_reset),
    .valid_up(n504_valid_up),
    .valid_down(n504_valid_down),
    .I_t0b(n504_I_t0b),
    .I_t1b(n504_I_t1b),
    .O(n504_O)
  );
  AtomTuple n506 ( // @[Top.scala 1429:22]
    .valid_up(n506_valid_up),
    .valid_down(n506_valid_down),
    .I0(n506_I0),
    .I1(n506_I1),
    .O_t0b(n506_O_t0b),
    .O_t1b(n506_O_t1b)
  );
  Lt n507 ( // @[Top.scala 1433:22]
    .valid_up(n507_valid_up),
    .valid_down(n507_valid_down),
    .I_t0b(n507_I_t0b),
    .I_t1b(n507_I_t1b),
    .O(n507_O)
  );
  InitialDelayCounter_42 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n509 ( // @[Top.scala 1437:22]
    .valid_up(n509_valid_up),
    .valid_down(n509_valid_down),
    .I0(n509_I0),
    .I1(n509_I1),
    .O_t0b(n509_O_t0b),
    .O_t1b(n509_O_t1b)
  );
  Sub n510 ( // @[Top.scala 1441:22]
    .valid_up(n510_valid_up),
    .valid_down(n510_valid_down),
    .I_t0b(n510_I_t0b),
    .I_t1b(n510_I_t1b),
    .O(n510_O)
  );
  AtomTuple n511 ( // @[Top.scala 1444:22]
    .valid_up(n511_valid_up),
    .valid_down(n511_valid_down),
    .I0(n511_I0),
    .I1(n511_I1),
    .O_t0b(n511_O_t0b),
    .O_t1b(n511_O_t1b)
  );
  AtomTuple n512 ( // @[Top.scala 1448:22]
    .valid_up(n512_valid_up),
    .valid_down(n512_valid_down),
    .I0(n512_I0),
    .I1(n512_I1),
    .O_t0b(n512_O_t0b),
    .O_t1b(n512_O_t1b)
  );
  AtomTuple_10 n513 ( // @[Top.scala 1452:22]
    .valid_up(n513_valid_up),
    .valid_down(n513_valid_down),
    .I0_t0b(n513_I0_t0b),
    .I0_t1b(n513_I0_t1b),
    .I1_t0b(n513_I1_t0b),
    .I1_t1b(n513_I1_t1b),
    .O_t0b_t0b(n513_O_t0b_t0b),
    .O_t0b_t1b(n513_O_t0b_t1b),
    .O_t1b_t0b(n513_O_t1b_t0b),
    .O_t1b_t1b(n513_O_t1b_t1b)
  );
  FIFO_4 n514 ( // @[Top.scala 1456:22]
    .clock(n514_clock),
    .reset(n514_reset),
    .valid_up(n514_valid_up),
    .valid_down(n514_valid_down),
    .I_t0b_t0b(n514_I_t0b_t0b),
    .I_t0b_t1b(n514_I_t0b_t1b),
    .I_t1b_t0b(n514_I_t1b_t0b),
    .I_t1b_t1b(n514_I_t1b_t1b),
    .O_t0b_t0b(n514_O_t0b_t0b),
    .O_t0b_t1b(n514_O_t0b_t1b),
    .O_t1b_t0b(n514_O_t1b_t0b),
    .O_t1b_t1b(n514_O_t1b_t1b)
  );
  AtomTuple_11 n515 ( // @[Top.scala 1459:22]
    .valid_up(n515_valid_up),
    .valid_down(n515_valid_down),
    .I0(n515_I0),
    .I1_t0b_t0b(n515_I1_t0b_t0b),
    .I1_t0b_t1b(n515_I1_t0b_t1b),
    .I1_t1b_t0b(n515_I1_t1b_t0b),
    .I1_t1b_t1b(n515_I1_t1b_t1b),
    .O_t0b(n515_O_t0b),
    .O_t1b_t0b_t0b(n515_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n515_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n515_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n515_O_t1b_t1b_t1b)
  );
  If n516 ( // @[Top.scala 1463:22]
    .valid_up(n516_valid_up),
    .valid_down(n516_valid_down),
    .I_t0b(n516_I_t0b),
    .I_t1b_t0b_t0b(n516_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n516_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n516_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n516_I_t1b_t1b_t1b),
    .O_t0b(n516_O_t0b),
    .O_t1b(n516_O_t1b)
  );
  AtomTuple_1 n518 ( // @[Top.scala 1466:22]
    .valid_up(n518_valid_up),
    .valid_down(n518_valid_down),
    .I0(n518_I0),
    .I1_t0b(n518_I1_t0b),
    .I1_t1b(n518_I1_t1b),
    .O_t0b(n518_O_t0b),
    .O_t1b_t0b(n518_O_t1b_t0b),
    .O_t1b_t1b(n518_O_t1b_t1b)
  );
  assign valid_down = n518_valid_down; // @[Top.scala 1471:16]
  assign O_t0b = n518_O_t0b; // @[Top.scala 1470:7]
  assign O_t1b_t0b = n518_O_t1b_t0b; // @[Top.scala 1470:7]
  assign O_t1b_t1b = n518_O_t1b_t1b; // @[Top.scala 1470:7]
  assign n489_valid_up = valid_up; // @[Top.scala 1383:19]
  assign n489_I_t0b = I_t0b; // @[Top.scala 1382:12]
  assign n517_clock = clock;
  assign n517_reset = reset;
  assign n517_valid_up = n489_valid_down; // @[Top.scala 1386:19]
  assign n517_I = n489_O; // @[Top.scala 1385:12]
  assign n505_clock = clock;
  assign n505_reset = reset;
  assign n505_valid_up = n489_valid_down; // @[Top.scala 1389:19]
  assign n505_I = n489_O; // @[Top.scala 1388:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n491_valid_up = valid_up; // @[Top.scala 1393:19]
  assign n491_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1392:12]
  assign n491_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1392:12]
  assign n492_valid_up = n491_valid_down; // @[Top.scala 1396:19]
  assign n492_I_t0b = n491_O_t0b; // @[Top.scala 1395:12]
  assign n493_valid_up = n491_valid_down; // @[Top.scala 1399:19]
  assign n493_I_t1b = n491_O_t1b; // @[Top.scala 1398:12]
  assign n494_valid_up = n492_valid_down & n493_valid_down; // @[Top.scala 1403:19]
  assign n494_I0 = n492_O; // @[Top.scala 1401:13]
  assign n494_I1 = n493_O; // @[Top.scala 1402:13]
  assign n495_valid_up = n494_valid_down; // @[Top.scala 1406:19]
  assign n495_I_t0b = n494_O_t0b; // @[Top.scala 1405:12]
  assign n495_I_t1b = n494_O_t1b; // @[Top.scala 1405:12]
  assign n497_valid_up = InitialDelayCounter_valid_down & n495_valid_down; // @[Top.scala 1410:19]
  assign n497_I0 = 16'h1; // @[Top.scala 1408:13]
  assign n497_I1 = n495_O; // @[Top.scala 1409:13]
  assign n498_valid_up = n497_valid_down; // @[Top.scala 1413:19]
  assign n498_I_t0b = n497_O_t0b; // @[Top.scala 1412:12]
  assign n498_I_t1b = n497_O_t1b; // @[Top.scala 1412:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n501_valid_up = n498_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1418:19]
  assign n501_I0 = n498_O; // @[Top.scala 1416:13]
  assign n502_valid_up = n501_valid_down; // @[Top.scala 1421:19]
  assign n502_I_t0b = n501_O_t0b; // @[Top.scala 1420:12]
  assign n503_valid_up = n502_valid_down; // @[Top.scala 1425:19]
  assign n503_I0 = n502_O; // @[Top.scala 1423:13]
  assign n503_I1 = n502_O; // @[Top.scala 1424:13]
  assign n504_clock = clock;
  assign n504_reset = reset;
  assign n504_valid_up = n503_valid_down; // @[Top.scala 1428:19]
  assign n504_I_t0b = n503_O_t0b; // @[Top.scala 1427:12]
  assign n504_I_t1b = n503_O_t1b; // @[Top.scala 1427:12]
  assign n506_valid_up = n505_valid_down & n504_valid_down; // @[Top.scala 1432:19]
  assign n506_I0 = n505_O; // @[Top.scala 1430:13]
  assign n506_I1 = n504_O; // @[Top.scala 1431:13]
  assign n507_valid_up = n506_valid_down; // @[Top.scala 1435:19]
  assign n507_I_t0b = n506_O_t0b; // @[Top.scala 1434:12]
  assign n507_I_t1b = n506_O_t1b; // @[Top.scala 1434:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n509_valid_up = n502_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1440:19]
  assign n509_I0 = n502_O; // @[Top.scala 1438:13]
  assign n509_I1 = 16'h1; // @[Top.scala 1439:13]
  assign n510_valid_up = n509_valid_down; // @[Top.scala 1443:19]
  assign n510_I_t0b = n509_O_t0b; // @[Top.scala 1442:12]
  assign n510_I_t1b = n509_O_t1b; // @[Top.scala 1442:12]
  assign n511_valid_up = n492_valid_down & n510_valid_down; // @[Top.scala 1447:19]
  assign n511_I0 = n492_O; // @[Top.scala 1445:13]
  assign n511_I1 = n510_O; // @[Top.scala 1446:13]
  assign n512_valid_up = n502_valid_down & n493_valid_down; // @[Top.scala 1451:19]
  assign n512_I0 = n502_O; // @[Top.scala 1449:13]
  assign n512_I1 = n493_O; // @[Top.scala 1450:13]
  assign n513_valid_up = n511_valid_down & n512_valid_down; // @[Top.scala 1455:19]
  assign n513_I0_t0b = n511_O_t0b; // @[Top.scala 1453:13]
  assign n513_I0_t1b = n511_O_t1b; // @[Top.scala 1453:13]
  assign n513_I1_t0b = n512_O_t0b; // @[Top.scala 1454:13]
  assign n513_I1_t1b = n512_O_t1b; // @[Top.scala 1454:13]
  assign n514_clock = clock;
  assign n514_reset = reset;
  assign n514_valid_up = n513_valid_down; // @[Top.scala 1458:19]
  assign n514_I_t0b_t0b = n513_O_t0b_t0b; // @[Top.scala 1457:12]
  assign n514_I_t0b_t1b = n513_O_t0b_t1b; // @[Top.scala 1457:12]
  assign n514_I_t1b_t0b = n513_O_t1b_t0b; // @[Top.scala 1457:12]
  assign n514_I_t1b_t1b = n513_O_t1b_t1b; // @[Top.scala 1457:12]
  assign n515_valid_up = n507_valid_down & n514_valid_down; // @[Top.scala 1462:19]
  assign n515_I0 = n507_O[0]; // @[Top.scala 1460:13]
  assign n515_I1_t0b_t0b = n514_O_t0b_t0b; // @[Top.scala 1461:13]
  assign n515_I1_t0b_t1b = n514_O_t0b_t1b; // @[Top.scala 1461:13]
  assign n515_I1_t1b_t0b = n514_O_t1b_t0b; // @[Top.scala 1461:13]
  assign n515_I1_t1b_t1b = n514_O_t1b_t1b; // @[Top.scala 1461:13]
  assign n516_valid_up = n515_valid_down; // @[Top.scala 1465:19]
  assign n516_I_t0b = n515_O_t0b; // @[Top.scala 1464:12]
  assign n516_I_t1b_t0b_t0b = n515_O_t1b_t0b_t0b; // @[Top.scala 1464:12]
  assign n516_I_t1b_t0b_t1b = n515_O_t1b_t0b_t1b; // @[Top.scala 1464:12]
  assign n516_I_t1b_t1b_t0b = n515_O_t1b_t1b_t0b; // @[Top.scala 1464:12]
  assign n516_I_t1b_t1b_t1b = n515_O_t1b_t1b_t1b; // @[Top.scala 1464:12]
  assign n518_valid_up = n517_valid_down & n516_valid_down; // @[Top.scala 1469:19]
  assign n518_I0 = n517_O; // @[Top.scala 1467:13]
  assign n518_I1_t0b = n516_O_t0b; // @[Top.scala 1468:13]
  assign n518_I1_t1b = n516_O_t1b; // @[Top.scala 1468:13]
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
  wire  n523_valid_up; // @[Top.scala 1477:22]
  wire  n523_valid_down; // @[Top.scala 1477:22]
  wire [15:0] n523_I_t0b; // @[Top.scala 1477:22]
  wire [15:0] n523_O; // @[Top.scala 1477:22]
  wire  n551_clock; // @[Top.scala 1480:22]
  wire  n551_reset; // @[Top.scala 1480:22]
  wire  n551_valid_up; // @[Top.scala 1480:22]
  wire  n551_valid_down; // @[Top.scala 1480:22]
  wire [15:0] n551_I; // @[Top.scala 1480:22]
  wire [15:0] n551_O; // @[Top.scala 1480:22]
  wire  n539_clock; // @[Top.scala 1483:22]
  wire  n539_reset; // @[Top.scala 1483:22]
  wire  n539_valid_up; // @[Top.scala 1483:22]
  wire  n539_valid_down; // @[Top.scala 1483:22]
  wire [15:0] n539_I; // @[Top.scala 1483:22]
  wire [15:0] n539_O; // @[Top.scala 1483:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n525_valid_up; // @[Top.scala 1487:22]
  wire  n525_valid_down; // @[Top.scala 1487:22]
  wire [15:0] n525_I_t1b_t0b; // @[Top.scala 1487:22]
  wire [15:0] n525_I_t1b_t1b; // @[Top.scala 1487:22]
  wire [15:0] n525_O_t0b; // @[Top.scala 1487:22]
  wire [15:0] n525_O_t1b; // @[Top.scala 1487:22]
  wire  n526_valid_up; // @[Top.scala 1490:22]
  wire  n526_valid_down; // @[Top.scala 1490:22]
  wire [15:0] n526_I_t0b; // @[Top.scala 1490:22]
  wire [15:0] n526_O; // @[Top.scala 1490:22]
  wire  n527_valid_up; // @[Top.scala 1493:22]
  wire  n527_valid_down; // @[Top.scala 1493:22]
  wire [15:0] n527_I_t1b; // @[Top.scala 1493:22]
  wire [15:0] n527_O; // @[Top.scala 1493:22]
  wire  n528_valid_up; // @[Top.scala 1496:22]
  wire  n528_valid_down; // @[Top.scala 1496:22]
  wire [15:0] n528_I0; // @[Top.scala 1496:22]
  wire [15:0] n528_I1; // @[Top.scala 1496:22]
  wire [15:0] n528_O_t0b; // @[Top.scala 1496:22]
  wire [15:0] n528_O_t1b; // @[Top.scala 1496:22]
  wire  n529_valid_up; // @[Top.scala 1500:22]
  wire  n529_valid_down; // @[Top.scala 1500:22]
  wire [15:0] n529_I_t0b; // @[Top.scala 1500:22]
  wire [15:0] n529_I_t1b; // @[Top.scala 1500:22]
  wire [15:0] n529_O; // @[Top.scala 1500:22]
  wire  n531_valid_up; // @[Top.scala 1503:22]
  wire  n531_valid_down; // @[Top.scala 1503:22]
  wire [15:0] n531_I0; // @[Top.scala 1503:22]
  wire [15:0] n531_I1; // @[Top.scala 1503:22]
  wire [15:0] n531_O_t0b; // @[Top.scala 1503:22]
  wire [15:0] n531_O_t1b; // @[Top.scala 1503:22]
  wire  n532_valid_up; // @[Top.scala 1507:22]
  wire  n532_valid_down; // @[Top.scala 1507:22]
  wire [15:0] n532_I_t0b; // @[Top.scala 1507:22]
  wire [15:0] n532_I_t1b; // @[Top.scala 1507:22]
  wire [15:0] n532_O; // @[Top.scala 1507:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n535_valid_up; // @[Top.scala 1511:22]
  wire  n535_valid_down; // @[Top.scala 1511:22]
  wire [15:0] n535_I0; // @[Top.scala 1511:22]
  wire [15:0] n535_O_t0b; // @[Top.scala 1511:22]
  wire  n536_valid_up; // @[Top.scala 1515:22]
  wire  n536_valid_down; // @[Top.scala 1515:22]
  wire [15:0] n536_I_t0b; // @[Top.scala 1515:22]
  wire [15:0] n536_O; // @[Top.scala 1515:22]
  wire  n537_valid_up; // @[Top.scala 1518:22]
  wire  n537_valid_down; // @[Top.scala 1518:22]
  wire [15:0] n537_I0; // @[Top.scala 1518:22]
  wire [15:0] n537_I1; // @[Top.scala 1518:22]
  wire [15:0] n537_O_t0b; // @[Top.scala 1518:22]
  wire [15:0] n537_O_t1b; // @[Top.scala 1518:22]
  wire  n538_clock; // @[Top.scala 1522:22]
  wire  n538_reset; // @[Top.scala 1522:22]
  wire  n538_valid_up; // @[Top.scala 1522:22]
  wire  n538_valid_down; // @[Top.scala 1522:22]
  wire [15:0] n538_I_t0b; // @[Top.scala 1522:22]
  wire [15:0] n538_I_t1b; // @[Top.scala 1522:22]
  wire [15:0] n538_O; // @[Top.scala 1522:22]
  wire  n540_valid_up; // @[Top.scala 1525:22]
  wire  n540_valid_down; // @[Top.scala 1525:22]
  wire [15:0] n540_I0; // @[Top.scala 1525:22]
  wire [15:0] n540_I1; // @[Top.scala 1525:22]
  wire [15:0] n540_O_t0b; // @[Top.scala 1525:22]
  wire [15:0] n540_O_t1b; // @[Top.scala 1525:22]
  wire  n541_valid_up; // @[Top.scala 1529:22]
  wire  n541_valid_down; // @[Top.scala 1529:22]
  wire [15:0] n541_I_t0b; // @[Top.scala 1529:22]
  wire [15:0] n541_I_t1b; // @[Top.scala 1529:22]
  wire [15:0] n541_O; // @[Top.scala 1529:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n543_valid_up; // @[Top.scala 1533:22]
  wire  n543_valid_down; // @[Top.scala 1533:22]
  wire [15:0] n543_I0; // @[Top.scala 1533:22]
  wire [15:0] n543_I1; // @[Top.scala 1533:22]
  wire [15:0] n543_O_t0b; // @[Top.scala 1533:22]
  wire [15:0] n543_O_t1b; // @[Top.scala 1533:22]
  wire  n544_valid_up; // @[Top.scala 1537:22]
  wire  n544_valid_down; // @[Top.scala 1537:22]
  wire [15:0] n544_I_t0b; // @[Top.scala 1537:22]
  wire [15:0] n544_I_t1b; // @[Top.scala 1537:22]
  wire [15:0] n544_O; // @[Top.scala 1537:22]
  wire  n545_valid_up; // @[Top.scala 1540:22]
  wire  n545_valid_down; // @[Top.scala 1540:22]
  wire [15:0] n545_I0; // @[Top.scala 1540:22]
  wire [15:0] n545_I1; // @[Top.scala 1540:22]
  wire [15:0] n545_O_t0b; // @[Top.scala 1540:22]
  wire [15:0] n545_O_t1b; // @[Top.scala 1540:22]
  wire  n546_valid_up; // @[Top.scala 1544:22]
  wire  n546_valid_down; // @[Top.scala 1544:22]
  wire [15:0] n546_I0; // @[Top.scala 1544:22]
  wire [15:0] n546_I1; // @[Top.scala 1544:22]
  wire [15:0] n546_O_t0b; // @[Top.scala 1544:22]
  wire [15:0] n546_O_t1b; // @[Top.scala 1544:22]
  wire  n547_valid_up; // @[Top.scala 1548:22]
  wire  n547_valid_down; // @[Top.scala 1548:22]
  wire [15:0] n547_I0_t0b; // @[Top.scala 1548:22]
  wire [15:0] n547_I0_t1b; // @[Top.scala 1548:22]
  wire [15:0] n547_I1_t0b; // @[Top.scala 1548:22]
  wire [15:0] n547_I1_t1b; // @[Top.scala 1548:22]
  wire [15:0] n547_O_t0b_t0b; // @[Top.scala 1548:22]
  wire [15:0] n547_O_t0b_t1b; // @[Top.scala 1548:22]
  wire [15:0] n547_O_t1b_t0b; // @[Top.scala 1548:22]
  wire [15:0] n547_O_t1b_t1b; // @[Top.scala 1548:22]
  wire  n548_clock; // @[Top.scala 1552:22]
  wire  n548_reset; // @[Top.scala 1552:22]
  wire  n548_valid_up; // @[Top.scala 1552:22]
  wire  n548_valid_down; // @[Top.scala 1552:22]
  wire [15:0] n548_I_t0b_t0b; // @[Top.scala 1552:22]
  wire [15:0] n548_I_t0b_t1b; // @[Top.scala 1552:22]
  wire [15:0] n548_I_t1b_t0b; // @[Top.scala 1552:22]
  wire [15:0] n548_I_t1b_t1b; // @[Top.scala 1552:22]
  wire [15:0] n548_O_t0b_t0b; // @[Top.scala 1552:22]
  wire [15:0] n548_O_t0b_t1b; // @[Top.scala 1552:22]
  wire [15:0] n548_O_t1b_t0b; // @[Top.scala 1552:22]
  wire [15:0] n548_O_t1b_t1b; // @[Top.scala 1552:22]
  wire  n549_valid_up; // @[Top.scala 1555:22]
  wire  n549_valid_down; // @[Top.scala 1555:22]
  wire  n549_I0; // @[Top.scala 1555:22]
  wire [15:0] n549_I1_t0b_t0b; // @[Top.scala 1555:22]
  wire [15:0] n549_I1_t0b_t1b; // @[Top.scala 1555:22]
  wire [15:0] n549_I1_t1b_t0b; // @[Top.scala 1555:22]
  wire [15:0] n549_I1_t1b_t1b; // @[Top.scala 1555:22]
  wire  n549_O_t0b; // @[Top.scala 1555:22]
  wire [15:0] n549_O_t1b_t0b_t0b; // @[Top.scala 1555:22]
  wire [15:0] n549_O_t1b_t0b_t1b; // @[Top.scala 1555:22]
  wire [15:0] n549_O_t1b_t1b_t0b; // @[Top.scala 1555:22]
  wire [15:0] n549_O_t1b_t1b_t1b; // @[Top.scala 1555:22]
  wire  n550_valid_up; // @[Top.scala 1559:22]
  wire  n550_valid_down; // @[Top.scala 1559:22]
  wire  n550_I_t0b; // @[Top.scala 1559:22]
  wire [15:0] n550_I_t1b_t0b_t0b; // @[Top.scala 1559:22]
  wire [15:0] n550_I_t1b_t0b_t1b; // @[Top.scala 1559:22]
  wire [15:0] n550_I_t1b_t1b_t0b; // @[Top.scala 1559:22]
  wire [15:0] n550_I_t1b_t1b_t1b; // @[Top.scala 1559:22]
  wire [15:0] n550_O_t0b; // @[Top.scala 1559:22]
  wire [15:0] n550_O_t1b; // @[Top.scala 1559:22]
  wire  n552_valid_up; // @[Top.scala 1562:22]
  wire  n552_valid_down; // @[Top.scala 1562:22]
  wire [15:0] n552_I0; // @[Top.scala 1562:22]
  wire [15:0] n552_I1_t0b; // @[Top.scala 1562:22]
  wire [15:0] n552_I1_t1b; // @[Top.scala 1562:22]
  wire [15:0] n552_O_t0b; // @[Top.scala 1562:22]
  wire [15:0] n552_O_t1b_t0b; // @[Top.scala 1562:22]
  wire [15:0] n552_O_t1b_t1b; // @[Top.scala 1562:22]
  Fst n523 ( // @[Top.scala 1477:22]
    .valid_up(n523_valid_up),
    .valid_down(n523_valid_down),
    .I_t0b(n523_I_t0b),
    .O(n523_O)
  );
  FIFO_2 n551 ( // @[Top.scala 1480:22]
    .clock(n551_clock),
    .reset(n551_reset),
    .valid_up(n551_valid_up),
    .valid_down(n551_valid_down),
    .I(n551_I),
    .O(n551_O)
  );
  FIFO_2 n539 ( // @[Top.scala 1483:22]
    .clock(n539_clock),
    .reset(n539_reset),
    .valid_up(n539_valid_up),
    .valid_down(n539_valid_down),
    .I(n539_I),
    .O(n539_O)
  );
  InitialDelayCounter_45 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Snd n525 ( // @[Top.scala 1487:22]
    .valid_up(n525_valid_up),
    .valid_down(n525_valid_down),
    .I_t1b_t0b(n525_I_t1b_t0b),
    .I_t1b_t1b(n525_I_t1b_t1b),
    .O_t0b(n525_O_t0b),
    .O_t1b(n525_O_t1b)
  );
  Fst_1 n526 ( // @[Top.scala 1490:22]
    .valid_up(n526_valid_up),
    .valid_down(n526_valid_down),
    .I_t0b(n526_I_t0b),
    .O(n526_O)
  );
  Snd_1 n527 ( // @[Top.scala 1493:22]
    .valid_up(n527_valid_up),
    .valid_down(n527_valid_down),
    .I_t1b(n527_I_t1b),
    .O(n527_O)
  );
  AtomTuple n528 ( // @[Top.scala 1496:22]
    .valid_up(n528_valid_up),
    .valid_down(n528_valid_down),
    .I0(n528_I0),
    .I1(n528_I1),
    .O_t0b(n528_O_t0b),
    .O_t1b(n528_O_t1b)
  );
  Add n529 ( // @[Top.scala 1500:22]
    .valid_up(n529_valid_up),
    .valid_down(n529_valid_down),
    .I_t0b(n529_I_t0b),
    .I_t1b(n529_I_t1b),
    .O(n529_O)
  );
  AtomTuple n531 ( // @[Top.scala 1503:22]
    .valid_up(n531_valid_up),
    .valid_down(n531_valid_down),
    .I0(n531_I0),
    .I1(n531_I1),
    .O_t0b(n531_O_t0b),
    .O_t1b(n531_O_t1b)
  );
  Add n532 ( // @[Top.scala 1507:22]
    .valid_up(n532_valid_up),
    .valid_down(n532_valid_down),
    .I_t0b(n532_I_t0b),
    .I_t1b(n532_I_t1b),
    .O(n532_O)
  );
  InitialDelayCounter_45 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple_4 n535 ( // @[Top.scala 1511:22]
    .valid_up(n535_valid_up),
    .valid_down(n535_valid_down),
    .I0(n535_I0),
    .O_t0b(n535_O_t0b)
  );
  RShift n536 ( // @[Top.scala 1515:22]
    .valid_up(n536_valid_up),
    .valid_down(n536_valid_down),
    .I_t0b(n536_I_t0b),
    .O(n536_O)
  );
  AtomTuple n537 ( // @[Top.scala 1518:22]
    .valid_up(n537_valid_up),
    .valid_down(n537_valid_down),
    .I0(n537_I0),
    .I1(n537_I1),
    .O_t0b(n537_O_t0b),
    .O_t1b(n537_O_t1b)
  );
  Mul n538 ( // @[Top.scala 1522:22]
    .clock(n538_clock),
    .reset(n538_reset),
    .valid_up(n538_valid_up),
    .valid_down(n538_valid_down),
    .I_t0b(n538_I_t0b),
    .I_t1b(n538_I_t1b),
    .O(n538_O)
  );
  AtomTuple n540 ( // @[Top.scala 1525:22]
    .valid_up(n540_valid_up),
    .valid_down(n540_valid_down),
    .I0(n540_I0),
    .I1(n540_I1),
    .O_t0b(n540_O_t0b),
    .O_t1b(n540_O_t1b)
  );
  Lt n541 ( // @[Top.scala 1529:22]
    .valid_up(n541_valid_up),
    .valid_down(n541_valid_down),
    .I_t0b(n541_I_t0b),
    .I_t1b(n541_I_t1b),
    .O(n541_O)
  );
  InitialDelayCounter_45 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n543 ( // @[Top.scala 1533:22]
    .valid_up(n543_valid_up),
    .valid_down(n543_valid_down),
    .I0(n543_I0),
    .I1(n543_I1),
    .O_t0b(n543_O_t0b),
    .O_t1b(n543_O_t1b)
  );
  Sub n544 ( // @[Top.scala 1537:22]
    .valid_up(n544_valid_up),
    .valid_down(n544_valid_down),
    .I_t0b(n544_I_t0b),
    .I_t1b(n544_I_t1b),
    .O(n544_O)
  );
  AtomTuple n545 ( // @[Top.scala 1540:22]
    .valid_up(n545_valid_up),
    .valid_down(n545_valid_down),
    .I0(n545_I0),
    .I1(n545_I1),
    .O_t0b(n545_O_t0b),
    .O_t1b(n545_O_t1b)
  );
  AtomTuple n546 ( // @[Top.scala 1544:22]
    .valid_up(n546_valid_up),
    .valid_down(n546_valid_down),
    .I0(n546_I0),
    .I1(n546_I1),
    .O_t0b(n546_O_t0b),
    .O_t1b(n546_O_t1b)
  );
  AtomTuple_10 n547 ( // @[Top.scala 1548:22]
    .valid_up(n547_valid_up),
    .valid_down(n547_valid_down),
    .I0_t0b(n547_I0_t0b),
    .I0_t1b(n547_I0_t1b),
    .I1_t0b(n547_I1_t0b),
    .I1_t1b(n547_I1_t1b),
    .O_t0b_t0b(n547_O_t0b_t0b),
    .O_t0b_t1b(n547_O_t0b_t1b),
    .O_t1b_t0b(n547_O_t1b_t0b),
    .O_t1b_t1b(n547_O_t1b_t1b)
  );
  FIFO_4 n548 ( // @[Top.scala 1552:22]
    .clock(n548_clock),
    .reset(n548_reset),
    .valid_up(n548_valid_up),
    .valid_down(n548_valid_down),
    .I_t0b_t0b(n548_I_t0b_t0b),
    .I_t0b_t1b(n548_I_t0b_t1b),
    .I_t1b_t0b(n548_I_t1b_t0b),
    .I_t1b_t1b(n548_I_t1b_t1b),
    .O_t0b_t0b(n548_O_t0b_t0b),
    .O_t0b_t1b(n548_O_t0b_t1b),
    .O_t1b_t0b(n548_O_t1b_t0b),
    .O_t1b_t1b(n548_O_t1b_t1b)
  );
  AtomTuple_11 n549 ( // @[Top.scala 1555:22]
    .valid_up(n549_valid_up),
    .valid_down(n549_valid_down),
    .I0(n549_I0),
    .I1_t0b_t0b(n549_I1_t0b_t0b),
    .I1_t0b_t1b(n549_I1_t0b_t1b),
    .I1_t1b_t0b(n549_I1_t1b_t0b),
    .I1_t1b_t1b(n549_I1_t1b_t1b),
    .O_t0b(n549_O_t0b),
    .O_t1b_t0b_t0b(n549_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b(n549_O_t1b_t0b_t1b),
    .O_t1b_t1b_t0b(n549_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n549_O_t1b_t1b_t1b)
  );
  If n550 ( // @[Top.scala 1559:22]
    .valid_up(n550_valid_up),
    .valid_down(n550_valid_down),
    .I_t0b(n550_I_t0b),
    .I_t1b_t0b_t0b(n550_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b(n550_I_t1b_t0b_t1b),
    .I_t1b_t1b_t0b(n550_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b(n550_I_t1b_t1b_t1b),
    .O_t0b(n550_O_t0b),
    .O_t1b(n550_O_t1b)
  );
  AtomTuple_1 n552 ( // @[Top.scala 1562:22]
    .valid_up(n552_valid_up),
    .valid_down(n552_valid_down),
    .I0(n552_I0),
    .I1_t0b(n552_I1_t0b),
    .I1_t1b(n552_I1_t1b),
    .O_t0b(n552_O_t0b),
    .O_t1b_t0b(n552_O_t1b_t0b),
    .O_t1b_t1b(n552_O_t1b_t1b)
  );
  assign valid_down = n552_valid_down; // @[Top.scala 1567:16]
  assign O_t1b_t0b = n552_O_t1b_t0b; // @[Top.scala 1566:7]
  assign O_t1b_t1b = n552_O_t1b_t1b; // @[Top.scala 1566:7]
  assign n523_valid_up = valid_up; // @[Top.scala 1479:19]
  assign n523_I_t0b = I_t0b; // @[Top.scala 1478:12]
  assign n551_clock = clock;
  assign n551_reset = reset;
  assign n551_valid_up = n523_valid_down; // @[Top.scala 1482:19]
  assign n551_I = n523_O; // @[Top.scala 1481:12]
  assign n539_clock = clock;
  assign n539_reset = reset;
  assign n539_valid_up = n523_valid_down; // @[Top.scala 1485:19]
  assign n539_I = n523_O; // @[Top.scala 1484:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n525_valid_up = valid_up; // @[Top.scala 1489:19]
  assign n525_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1488:12]
  assign n525_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1488:12]
  assign n526_valid_up = n525_valid_down; // @[Top.scala 1492:19]
  assign n526_I_t0b = n525_O_t0b; // @[Top.scala 1491:12]
  assign n527_valid_up = n525_valid_down; // @[Top.scala 1495:19]
  assign n527_I_t1b = n525_O_t1b; // @[Top.scala 1494:12]
  assign n528_valid_up = n526_valid_down & n527_valid_down; // @[Top.scala 1499:19]
  assign n528_I0 = n526_O; // @[Top.scala 1497:13]
  assign n528_I1 = n527_O; // @[Top.scala 1498:13]
  assign n529_valid_up = n528_valid_down; // @[Top.scala 1502:19]
  assign n529_I_t0b = n528_O_t0b; // @[Top.scala 1501:12]
  assign n529_I_t1b = n528_O_t1b; // @[Top.scala 1501:12]
  assign n531_valid_up = InitialDelayCounter_valid_down & n529_valid_down; // @[Top.scala 1506:19]
  assign n531_I0 = 16'h1; // @[Top.scala 1504:13]
  assign n531_I1 = n529_O; // @[Top.scala 1505:13]
  assign n532_valid_up = n531_valid_down; // @[Top.scala 1509:19]
  assign n532_I_t0b = n531_O_t0b; // @[Top.scala 1508:12]
  assign n532_I_t1b = n531_O_t1b; // @[Top.scala 1508:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n535_valid_up = n532_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 1514:19]
  assign n535_I0 = n532_O; // @[Top.scala 1512:13]
  assign n536_valid_up = n535_valid_down; // @[Top.scala 1517:19]
  assign n536_I_t0b = n535_O_t0b; // @[Top.scala 1516:12]
  assign n537_valid_up = n536_valid_down; // @[Top.scala 1521:19]
  assign n537_I0 = n536_O; // @[Top.scala 1519:13]
  assign n537_I1 = n536_O; // @[Top.scala 1520:13]
  assign n538_clock = clock;
  assign n538_reset = reset;
  assign n538_valid_up = n537_valid_down; // @[Top.scala 1524:19]
  assign n538_I_t0b = n537_O_t0b; // @[Top.scala 1523:12]
  assign n538_I_t1b = n537_O_t1b; // @[Top.scala 1523:12]
  assign n540_valid_up = n539_valid_down & n538_valid_down; // @[Top.scala 1528:19]
  assign n540_I0 = n539_O; // @[Top.scala 1526:13]
  assign n540_I1 = n538_O; // @[Top.scala 1527:13]
  assign n541_valid_up = n540_valid_down; // @[Top.scala 1531:19]
  assign n541_I_t0b = n540_O_t0b; // @[Top.scala 1530:12]
  assign n541_I_t1b = n540_O_t1b; // @[Top.scala 1530:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n543_valid_up = n536_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 1536:19]
  assign n543_I0 = n536_O; // @[Top.scala 1534:13]
  assign n543_I1 = 16'h1; // @[Top.scala 1535:13]
  assign n544_valid_up = n543_valid_down; // @[Top.scala 1539:19]
  assign n544_I_t0b = n543_O_t0b; // @[Top.scala 1538:12]
  assign n544_I_t1b = n543_O_t1b; // @[Top.scala 1538:12]
  assign n545_valid_up = n526_valid_down & n544_valid_down; // @[Top.scala 1543:19]
  assign n545_I0 = n526_O; // @[Top.scala 1541:13]
  assign n545_I1 = n544_O; // @[Top.scala 1542:13]
  assign n546_valid_up = n536_valid_down & n527_valid_down; // @[Top.scala 1547:19]
  assign n546_I0 = n536_O; // @[Top.scala 1545:13]
  assign n546_I1 = n527_O; // @[Top.scala 1546:13]
  assign n547_valid_up = n545_valid_down & n546_valid_down; // @[Top.scala 1551:19]
  assign n547_I0_t0b = n545_O_t0b; // @[Top.scala 1549:13]
  assign n547_I0_t1b = n545_O_t1b; // @[Top.scala 1549:13]
  assign n547_I1_t0b = n546_O_t0b; // @[Top.scala 1550:13]
  assign n547_I1_t1b = n546_O_t1b; // @[Top.scala 1550:13]
  assign n548_clock = clock;
  assign n548_reset = reset;
  assign n548_valid_up = n547_valid_down; // @[Top.scala 1554:19]
  assign n548_I_t0b_t0b = n547_O_t0b_t0b; // @[Top.scala 1553:12]
  assign n548_I_t0b_t1b = n547_O_t0b_t1b; // @[Top.scala 1553:12]
  assign n548_I_t1b_t0b = n547_O_t1b_t0b; // @[Top.scala 1553:12]
  assign n548_I_t1b_t1b = n547_O_t1b_t1b; // @[Top.scala 1553:12]
  assign n549_valid_up = n541_valid_down & n548_valid_down; // @[Top.scala 1558:19]
  assign n549_I0 = n541_O[0]; // @[Top.scala 1556:13]
  assign n549_I1_t0b_t0b = n548_O_t0b_t0b; // @[Top.scala 1557:13]
  assign n549_I1_t0b_t1b = n548_O_t0b_t1b; // @[Top.scala 1557:13]
  assign n549_I1_t1b_t0b = n548_O_t1b_t0b; // @[Top.scala 1557:13]
  assign n549_I1_t1b_t1b = n548_O_t1b_t1b; // @[Top.scala 1557:13]
  assign n550_valid_up = n549_valid_down; // @[Top.scala 1561:19]
  assign n550_I_t0b = n549_O_t0b; // @[Top.scala 1560:12]
  assign n550_I_t1b_t0b_t0b = n549_O_t1b_t0b_t0b; // @[Top.scala 1560:12]
  assign n550_I_t1b_t0b_t1b = n549_O_t1b_t0b_t1b; // @[Top.scala 1560:12]
  assign n550_I_t1b_t1b_t0b = n549_O_t1b_t1b_t0b; // @[Top.scala 1560:12]
  assign n550_I_t1b_t1b_t1b = n549_O_t1b_t1b_t1b; // @[Top.scala 1560:12]
  assign n552_valid_up = n551_valid_down & n550_valid_down; // @[Top.scala 1565:19]
  assign n552_I0 = n551_O; // @[Top.scala 1563:13]
  assign n552_I1_t0b = n550_O_t0b; // @[Top.scala 1564:13]
  assign n552_I1_t1b = n550_O_t1b; // @[Top.scala 1564:13]
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
  wire  n557_valid_up; // @[Top.scala 1573:22]
  wire  n557_valid_down; // @[Top.scala 1573:22]
  wire [15:0] n557_I_t1b_t0b; // @[Top.scala 1573:22]
  wire [15:0] n557_I_t1b_t1b; // @[Top.scala 1573:22]
  wire [15:0] n557_O_t0b; // @[Top.scala 1573:22]
  wire [15:0] n557_O_t1b; // @[Top.scala 1573:22]
  wire  n558_valid_up; // @[Top.scala 1576:22]
  wire  n558_valid_down; // @[Top.scala 1576:22]
  wire [15:0] n558_I_t0b; // @[Top.scala 1576:22]
  wire [15:0] n558_O; // @[Top.scala 1576:22]
  Snd n557 ( // @[Top.scala 1573:22]
    .valid_up(n557_valid_up),
    .valid_down(n557_valid_down),
    .I_t1b_t0b(n557_I_t1b_t0b),
    .I_t1b_t1b(n557_I_t1b_t1b),
    .O_t0b(n557_O_t0b),
    .O_t1b(n557_O_t1b)
  );
  Fst_1 n558 ( // @[Top.scala 1576:22]
    .valid_up(n558_valid_up),
    .valid_down(n558_valid_down),
    .I_t0b(n558_I_t0b),
    .O(n558_O)
  );
  assign valid_down = n558_valid_down; // @[Top.scala 1580:16]
  assign O = n558_O; // @[Top.scala 1579:7]
  assign n557_valid_up = valid_up; // @[Top.scala 1575:19]
  assign n557_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 1574:12]
  assign n557_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 1574:12]
  assign n558_valid_up = n557_valid_down; // @[Top.scala 1578:19]
  assign n558_I_t0b = n557_O_t0b; // @[Top.scala 1577:12]
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
  wire  n1_clock; // @[Top.scala 1586:20]
  wire  n1_reset; // @[Top.scala 1586:20]
  wire  n1_valid_up; // @[Top.scala 1586:20]
  wire  n1_valid_down; // @[Top.scala 1586:20]
  wire [15:0] n1_I_0; // @[Top.scala 1586:20]
  wire [15:0] n1_I_1; // @[Top.scala 1586:20]
  wire [15:0] n1_I_2; // @[Top.scala 1586:20]
  wire [15:0] n1_I_3; // @[Top.scala 1586:20]
  wire [15:0] n1_O_0; // @[Top.scala 1586:20]
  wire [15:0] n1_O_1; // @[Top.scala 1586:20]
  wire [15:0] n1_O_2; // @[Top.scala 1586:20]
  wire [15:0] n1_O_3; // @[Top.scala 1586:20]
  wire  n10_clock; // @[Top.scala 1589:21]
  wire  n10_reset; // @[Top.scala 1589:21]
  wire  n10_valid_up; // @[Top.scala 1589:21]
  wire  n10_valid_down; // @[Top.scala 1589:21]
  wire [15:0] n10_I_0; // @[Top.scala 1589:21]
  wire [15:0] n10_I_1; // @[Top.scala 1589:21]
  wire [15:0] n10_I_2; // @[Top.scala 1589:21]
  wire [15:0] n10_I_3; // @[Top.scala 1589:21]
  wire [15:0] n10_O_0_t0b; // @[Top.scala 1589:21]
  wire [15:0] n10_O_0_t1b_t0b; // @[Top.scala 1589:21]
  wire [15:0] n10_O_0_t1b_t1b; // @[Top.scala 1589:21]
  wire [15:0] n10_O_1_t0b; // @[Top.scala 1589:21]
  wire [15:0] n10_O_1_t1b_t0b; // @[Top.scala 1589:21]
  wire [15:0] n10_O_1_t1b_t1b; // @[Top.scala 1589:21]
  wire [15:0] n10_O_2_t0b; // @[Top.scala 1589:21]
  wire [15:0] n10_O_2_t1b_t0b; // @[Top.scala 1589:21]
  wire [15:0] n10_O_2_t1b_t1b; // @[Top.scala 1589:21]
  wire [15:0] n10_O_3_t0b; // @[Top.scala 1589:21]
  wire [15:0] n10_O_3_t1b_t0b; // @[Top.scala 1589:21]
  wire [15:0] n10_O_3_t1b_t1b; // @[Top.scala 1589:21]
  wire  n44_clock; // @[Top.scala 1592:21]
  wire  n44_reset; // @[Top.scala 1592:21]
  wire  n44_valid_up; // @[Top.scala 1592:21]
  wire  n44_valid_down; // @[Top.scala 1592:21]
  wire [15:0] n44_I_0_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_I_0_t1b_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_I_0_t1b_t1b; // @[Top.scala 1592:21]
  wire [15:0] n44_I_1_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_I_1_t1b_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_I_1_t1b_t1b; // @[Top.scala 1592:21]
  wire [15:0] n44_I_2_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_I_2_t1b_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_I_2_t1b_t1b; // @[Top.scala 1592:21]
  wire [15:0] n44_I_3_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_I_3_t1b_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_I_3_t1b_t1b; // @[Top.scala 1592:21]
  wire [15:0] n44_O_0_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_O_0_t1b_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_O_0_t1b_t1b; // @[Top.scala 1592:21]
  wire [15:0] n44_O_1_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_O_1_t1b_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_O_1_t1b_t1b; // @[Top.scala 1592:21]
  wire [15:0] n44_O_2_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_O_2_t1b_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_O_2_t1b_t1b; // @[Top.scala 1592:21]
  wire [15:0] n44_O_3_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_O_3_t1b_t0b; // @[Top.scala 1592:21]
  wire [15:0] n44_O_3_t1b_t1b; // @[Top.scala 1592:21]
  wire  n78_clock; // @[Top.scala 1595:21]
  wire  n78_reset; // @[Top.scala 1595:21]
  wire  n78_valid_up; // @[Top.scala 1595:21]
  wire  n78_valid_down; // @[Top.scala 1595:21]
  wire [15:0] n78_I_0_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_I_0_t1b_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_I_0_t1b_t1b; // @[Top.scala 1595:21]
  wire [15:0] n78_I_1_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_I_1_t1b_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_I_1_t1b_t1b; // @[Top.scala 1595:21]
  wire [15:0] n78_I_2_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_I_2_t1b_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_I_2_t1b_t1b; // @[Top.scala 1595:21]
  wire [15:0] n78_I_3_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_I_3_t1b_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_I_3_t1b_t1b; // @[Top.scala 1595:21]
  wire [15:0] n78_O_0_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_O_0_t1b_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_O_0_t1b_t1b; // @[Top.scala 1595:21]
  wire [15:0] n78_O_1_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_O_1_t1b_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_O_1_t1b_t1b; // @[Top.scala 1595:21]
  wire [15:0] n78_O_2_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_O_2_t1b_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_O_2_t1b_t1b; // @[Top.scala 1595:21]
  wire [15:0] n78_O_3_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_O_3_t1b_t0b; // @[Top.scala 1595:21]
  wire [15:0] n78_O_3_t1b_t1b; // @[Top.scala 1595:21]
  wire  n112_clock; // @[Top.scala 1598:22]
  wire  n112_reset; // @[Top.scala 1598:22]
  wire  n112_valid_up; // @[Top.scala 1598:22]
  wire  n112_valid_down; // @[Top.scala 1598:22]
  wire [15:0] n112_I_0_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_I_0_t1b_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_I_0_t1b_t1b; // @[Top.scala 1598:22]
  wire [15:0] n112_I_1_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_I_1_t1b_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_I_1_t1b_t1b; // @[Top.scala 1598:22]
  wire [15:0] n112_I_2_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_I_2_t1b_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_I_2_t1b_t1b; // @[Top.scala 1598:22]
  wire [15:0] n112_I_3_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_I_3_t1b_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_I_3_t1b_t1b; // @[Top.scala 1598:22]
  wire [15:0] n112_O_0_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_O_0_t1b_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_O_0_t1b_t1b; // @[Top.scala 1598:22]
  wire [15:0] n112_O_1_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_O_1_t1b_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_O_1_t1b_t1b; // @[Top.scala 1598:22]
  wire [15:0] n112_O_2_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_O_2_t1b_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_O_2_t1b_t1b; // @[Top.scala 1598:22]
  wire [15:0] n112_O_3_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_O_3_t1b_t0b; // @[Top.scala 1598:22]
  wire [15:0] n112_O_3_t1b_t1b; // @[Top.scala 1598:22]
  wire  n146_clock; // @[Top.scala 1601:22]
  wire  n146_reset; // @[Top.scala 1601:22]
  wire  n146_valid_up; // @[Top.scala 1601:22]
  wire  n146_valid_down; // @[Top.scala 1601:22]
  wire [15:0] n146_I_0_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_I_0_t1b_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_I_0_t1b_t1b; // @[Top.scala 1601:22]
  wire [15:0] n146_I_1_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_I_1_t1b_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_I_1_t1b_t1b; // @[Top.scala 1601:22]
  wire [15:0] n146_I_2_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_I_2_t1b_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_I_2_t1b_t1b; // @[Top.scala 1601:22]
  wire [15:0] n146_I_3_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_I_3_t1b_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_I_3_t1b_t1b; // @[Top.scala 1601:22]
  wire [15:0] n146_O_0_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_O_0_t1b_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_O_0_t1b_t1b; // @[Top.scala 1601:22]
  wire [15:0] n146_O_1_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_O_1_t1b_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_O_1_t1b_t1b; // @[Top.scala 1601:22]
  wire [15:0] n146_O_2_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_O_2_t1b_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_O_2_t1b_t1b; // @[Top.scala 1601:22]
  wire [15:0] n146_O_3_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_O_3_t1b_t0b; // @[Top.scala 1601:22]
  wire [15:0] n146_O_3_t1b_t1b; // @[Top.scala 1601:22]
  wire  n180_clock; // @[Top.scala 1604:22]
  wire  n180_reset; // @[Top.scala 1604:22]
  wire  n180_valid_up; // @[Top.scala 1604:22]
  wire  n180_valid_down; // @[Top.scala 1604:22]
  wire [15:0] n180_I_0_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_I_0_t1b_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_I_0_t1b_t1b; // @[Top.scala 1604:22]
  wire [15:0] n180_I_1_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_I_1_t1b_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_I_1_t1b_t1b; // @[Top.scala 1604:22]
  wire [15:0] n180_I_2_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_I_2_t1b_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_I_2_t1b_t1b; // @[Top.scala 1604:22]
  wire [15:0] n180_I_3_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_I_3_t1b_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_I_3_t1b_t1b; // @[Top.scala 1604:22]
  wire [15:0] n180_O_0_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_O_0_t1b_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_O_0_t1b_t1b; // @[Top.scala 1604:22]
  wire [15:0] n180_O_1_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_O_1_t1b_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_O_1_t1b_t1b; // @[Top.scala 1604:22]
  wire [15:0] n180_O_2_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_O_2_t1b_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_O_2_t1b_t1b; // @[Top.scala 1604:22]
  wire [15:0] n180_O_3_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_O_3_t1b_t0b; // @[Top.scala 1604:22]
  wire [15:0] n180_O_3_t1b_t1b; // @[Top.scala 1604:22]
  wire  n214_clock; // @[Top.scala 1607:22]
  wire  n214_reset; // @[Top.scala 1607:22]
  wire  n214_valid_up; // @[Top.scala 1607:22]
  wire  n214_valid_down; // @[Top.scala 1607:22]
  wire [15:0] n214_I_0_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_I_0_t1b_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_I_0_t1b_t1b; // @[Top.scala 1607:22]
  wire [15:0] n214_I_1_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_I_1_t1b_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_I_1_t1b_t1b; // @[Top.scala 1607:22]
  wire [15:0] n214_I_2_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_I_2_t1b_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_I_2_t1b_t1b; // @[Top.scala 1607:22]
  wire [15:0] n214_I_3_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_I_3_t1b_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_I_3_t1b_t1b; // @[Top.scala 1607:22]
  wire [15:0] n214_O_0_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_O_0_t1b_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_O_0_t1b_t1b; // @[Top.scala 1607:22]
  wire [15:0] n214_O_1_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_O_1_t1b_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_O_1_t1b_t1b; // @[Top.scala 1607:22]
  wire [15:0] n214_O_2_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_O_2_t1b_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_O_2_t1b_t1b; // @[Top.scala 1607:22]
  wire [15:0] n214_O_3_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_O_3_t1b_t0b; // @[Top.scala 1607:22]
  wire [15:0] n214_O_3_t1b_t1b; // @[Top.scala 1607:22]
  wire  n248_clock; // @[Top.scala 1610:22]
  wire  n248_reset; // @[Top.scala 1610:22]
  wire  n248_valid_up; // @[Top.scala 1610:22]
  wire  n248_valid_down; // @[Top.scala 1610:22]
  wire [15:0] n248_I_0_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_I_0_t1b_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_I_0_t1b_t1b; // @[Top.scala 1610:22]
  wire [15:0] n248_I_1_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_I_1_t1b_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_I_1_t1b_t1b; // @[Top.scala 1610:22]
  wire [15:0] n248_I_2_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_I_2_t1b_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_I_2_t1b_t1b; // @[Top.scala 1610:22]
  wire [15:0] n248_I_3_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_I_3_t1b_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_I_3_t1b_t1b; // @[Top.scala 1610:22]
  wire [15:0] n248_O_0_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_O_0_t1b_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_O_0_t1b_t1b; // @[Top.scala 1610:22]
  wire [15:0] n248_O_1_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_O_1_t1b_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_O_1_t1b_t1b; // @[Top.scala 1610:22]
  wire [15:0] n248_O_2_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_O_2_t1b_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_O_2_t1b_t1b; // @[Top.scala 1610:22]
  wire [15:0] n248_O_3_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_O_3_t1b_t0b; // @[Top.scala 1610:22]
  wire [15:0] n248_O_3_t1b_t1b; // @[Top.scala 1610:22]
  wire  n282_clock; // @[Top.scala 1613:22]
  wire  n282_reset; // @[Top.scala 1613:22]
  wire  n282_valid_up; // @[Top.scala 1613:22]
  wire  n282_valid_down; // @[Top.scala 1613:22]
  wire [15:0] n282_I_0_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_I_0_t1b_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_I_0_t1b_t1b; // @[Top.scala 1613:22]
  wire [15:0] n282_I_1_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_I_1_t1b_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_I_1_t1b_t1b; // @[Top.scala 1613:22]
  wire [15:0] n282_I_2_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_I_2_t1b_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_I_2_t1b_t1b; // @[Top.scala 1613:22]
  wire [15:0] n282_I_3_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_I_3_t1b_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_I_3_t1b_t1b; // @[Top.scala 1613:22]
  wire [15:0] n282_O_0_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_O_0_t1b_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_O_0_t1b_t1b; // @[Top.scala 1613:22]
  wire [15:0] n282_O_1_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_O_1_t1b_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_O_1_t1b_t1b; // @[Top.scala 1613:22]
  wire [15:0] n282_O_2_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_O_2_t1b_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_O_2_t1b_t1b; // @[Top.scala 1613:22]
  wire [15:0] n282_O_3_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_O_3_t1b_t0b; // @[Top.scala 1613:22]
  wire [15:0] n282_O_3_t1b_t1b; // @[Top.scala 1613:22]
  wire  n316_clock; // @[Top.scala 1616:22]
  wire  n316_reset; // @[Top.scala 1616:22]
  wire  n316_valid_up; // @[Top.scala 1616:22]
  wire  n316_valid_down; // @[Top.scala 1616:22]
  wire [15:0] n316_I_0_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_I_0_t1b_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_I_0_t1b_t1b; // @[Top.scala 1616:22]
  wire [15:0] n316_I_1_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_I_1_t1b_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_I_1_t1b_t1b; // @[Top.scala 1616:22]
  wire [15:0] n316_I_2_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_I_2_t1b_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_I_2_t1b_t1b; // @[Top.scala 1616:22]
  wire [15:0] n316_I_3_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_I_3_t1b_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_I_3_t1b_t1b; // @[Top.scala 1616:22]
  wire [15:0] n316_O_0_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_O_0_t1b_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_O_0_t1b_t1b; // @[Top.scala 1616:22]
  wire [15:0] n316_O_1_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_O_1_t1b_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_O_1_t1b_t1b; // @[Top.scala 1616:22]
  wire [15:0] n316_O_2_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_O_2_t1b_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_O_2_t1b_t1b; // @[Top.scala 1616:22]
  wire [15:0] n316_O_3_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_O_3_t1b_t0b; // @[Top.scala 1616:22]
  wire [15:0] n316_O_3_t1b_t1b; // @[Top.scala 1616:22]
  wire  n350_clock; // @[Top.scala 1619:22]
  wire  n350_reset; // @[Top.scala 1619:22]
  wire  n350_valid_up; // @[Top.scala 1619:22]
  wire  n350_valid_down; // @[Top.scala 1619:22]
  wire [15:0] n350_I_0_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_I_0_t1b_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_I_0_t1b_t1b; // @[Top.scala 1619:22]
  wire [15:0] n350_I_1_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_I_1_t1b_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_I_1_t1b_t1b; // @[Top.scala 1619:22]
  wire [15:0] n350_I_2_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_I_2_t1b_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_I_2_t1b_t1b; // @[Top.scala 1619:22]
  wire [15:0] n350_I_3_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_I_3_t1b_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_I_3_t1b_t1b; // @[Top.scala 1619:22]
  wire [15:0] n350_O_0_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_O_0_t1b_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_O_0_t1b_t1b; // @[Top.scala 1619:22]
  wire [15:0] n350_O_1_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_O_1_t1b_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_O_1_t1b_t1b; // @[Top.scala 1619:22]
  wire [15:0] n350_O_2_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_O_2_t1b_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_O_2_t1b_t1b; // @[Top.scala 1619:22]
  wire [15:0] n350_O_3_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_O_3_t1b_t0b; // @[Top.scala 1619:22]
  wire [15:0] n350_O_3_t1b_t1b; // @[Top.scala 1619:22]
  wire  n384_clock; // @[Top.scala 1622:22]
  wire  n384_reset; // @[Top.scala 1622:22]
  wire  n384_valid_up; // @[Top.scala 1622:22]
  wire  n384_valid_down; // @[Top.scala 1622:22]
  wire [15:0] n384_I_0_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_I_0_t1b_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_I_0_t1b_t1b; // @[Top.scala 1622:22]
  wire [15:0] n384_I_1_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_I_1_t1b_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_I_1_t1b_t1b; // @[Top.scala 1622:22]
  wire [15:0] n384_I_2_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_I_2_t1b_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_I_2_t1b_t1b; // @[Top.scala 1622:22]
  wire [15:0] n384_I_3_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_I_3_t1b_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_I_3_t1b_t1b; // @[Top.scala 1622:22]
  wire [15:0] n384_O_0_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_O_0_t1b_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_O_0_t1b_t1b; // @[Top.scala 1622:22]
  wire [15:0] n384_O_1_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_O_1_t1b_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_O_1_t1b_t1b; // @[Top.scala 1622:22]
  wire [15:0] n384_O_2_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_O_2_t1b_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_O_2_t1b_t1b; // @[Top.scala 1622:22]
  wire [15:0] n384_O_3_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_O_3_t1b_t0b; // @[Top.scala 1622:22]
  wire [15:0] n384_O_3_t1b_t1b; // @[Top.scala 1622:22]
  wire  n418_clock; // @[Top.scala 1625:22]
  wire  n418_reset; // @[Top.scala 1625:22]
  wire  n418_valid_up; // @[Top.scala 1625:22]
  wire  n418_valid_down; // @[Top.scala 1625:22]
  wire [15:0] n418_I_0_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_I_0_t1b_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_I_0_t1b_t1b; // @[Top.scala 1625:22]
  wire [15:0] n418_I_1_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_I_1_t1b_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_I_1_t1b_t1b; // @[Top.scala 1625:22]
  wire [15:0] n418_I_2_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_I_2_t1b_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_I_2_t1b_t1b; // @[Top.scala 1625:22]
  wire [15:0] n418_I_3_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_I_3_t1b_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_I_3_t1b_t1b; // @[Top.scala 1625:22]
  wire [15:0] n418_O_0_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_O_0_t1b_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_O_0_t1b_t1b; // @[Top.scala 1625:22]
  wire [15:0] n418_O_1_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_O_1_t1b_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_O_1_t1b_t1b; // @[Top.scala 1625:22]
  wire [15:0] n418_O_2_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_O_2_t1b_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_O_2_t1b_t1b; // @[Top.scala 1625:22]
  wire [15:0] n418_O_3_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_O_3_t1b_t0b; // @[Top.scala 1625:22]
  wire [15:0] n418_O_3_t1b_t1b; // @[Top.scala 1625:22]
  wire  n452_clock; // @[Top.scala 1628:22]
  wire  n452_reset; // @[Top.scala 1628:22]
  wire  n452_valid_up; // @[Top.scala 1628:22]
  wire  n452_valid_down; // @[Top.scala 1628:22]
  wire [15:0] n452_I_0_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_I_0_t1b_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_I_0_t1b_t1b; // @[Top.scala 1628:22]
  wire [15:0] n452_I_1_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_I_1_t1b_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_I_1_t1b_t1b; // @[Top.scala 1628:22]
  wire [15:0] n452_I_2_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_I_2_t1b_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_I_2_t1b_t1b; // @[Top.scala 1628:22]
  wire [15:0] n452_I_3_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_I_3_t1b_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_I_3_t1b_t1b; // @[Top.scala 1628:22]
  wire [15:0] n452_O_0_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_O_0_t1b_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_O_0_t1b_t1b; // @[Top.scala 1628:22]
  wire [15:0] n452_O_1_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_O_1_t1b_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_O_1_t1b_t1b; // @[Top.scala 1628:22]
  wire [15:0] n452_O_2_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_O_2_t1b_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_O_2_t1b_t1b; // @[Top.scala 1628:22]
  wire [15:0] n452_O_3_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_O_3_t1b_t0b; // @[Top.scala 1628:22]
  wire [15:0] n452_O_3_t1b_t1b; // @[Top.scala 1628:22]
  wire  n486_clock; // @[Top.scala 1631:22]
  wire  n486_reset; // @[Top.scala 1631:22]
  wire  n486_valid_up; // @[Top.scala 1631:22]
  wire  n486_valid_down; // @[Top.scala 1631:22]
  wire [15:0] n486_I_0_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_I_0_t1b_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_I_0_t1b_t1b; // @[Top.scala 1631:22]
  wire [15:0] n486_I_1_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_I_1_t1b_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_I_1_t1b_t1b; // @[Top.scala 1631:22]
  wire [15:0] n486_I_2_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_I_2_t1b_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_I_2_t1b_t1b; // @[Top.scala 1631:22]
  wire [15:0] n486_I_3_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_I_3_t1b_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_I_3_t1b_t1b; // @[Top.scala 1631:22]
  wire [15:0] n486_O_0_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_O_0_t1b_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_O_0_t1b_t1b; // @[Top.scala 1631:22]
  wire [15:0] n486_O_1_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_O_1_t1b_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_O_1_t1b_t1b; // @[Top.scala 1631:22]
  wire [15:0] n486_O_2_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_O_2_t1b_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_O_2_t1b_t1b; // @[Top.scala 1631:22]
  wire [15:0] n486_O_3_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_O_3_t1b_t0b; // @[Top.scala 1631:22]
  wire [15:0] n486_O_3_t1b_t1b; // @[Top.scala 1631:22]
  wire  n520_clock; // @[Top.scala 1634:22]
  wire  n520_reset; // @[Top.scala 1634:22]
  wire  n520_valid_up; // @[Top.scala 1634:22]
  wire  n520_valid_down; // @[Top.scala 1634:22]
  wire [15:0] n520_I_0_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_I_0_t1b_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_I_0_t1b_t1b; // @[Top.scala 1634:22]
  wire [15:0] n520_I_1_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_I_1_t1b_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_I_1_t1b_t1b; // @[Top.scala 1634:22]
  wire [15:0] n520_I_2_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_I_2_t1b_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_I_2_t1b_t1b; // @[Top.scala 1634:22]
  wire [15:0] n520_I_3_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_I_3_t1b_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_I_3_t1b_t1b; // @[Top.scala 1634:22]
  wire [15:0] n520_O_0_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_O_0_t1b_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_O_0_t1b_t1b; // @[Top.scala 1634:22]
  wire [15:0] n520_O_1_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_O_1_t1b_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_O_1_t1b_t1b; // @[Top.scala 1634:22]
  wire [15:0] n520_O_2_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_O_2_t1b_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_O_2_t1b_t1b; // @[Top.scala 1634:22]
  wire [15:0] n520_O_3_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_O_3_t1b_t0b; // @[Top.scala 1634:22]
  wire [15:0] n520_O_3_t1b_t1b; // @[Top.scala 1634:22]
  wire  n554_clock; // @[Top.scala 1637:22]
  wire  n554_reset; // @[Top.scala 1637:22]
  wire  n554_valid_up; // @[Top.scala 1637:22]
  wire  n554_valid_down; // @[Top.scala 1637:22]
  wire [15:0] n554_I_0_t0b; // @[Top.scala 1637:22]
  wire [15:0] n554_I_0_t1b_t0b; // @[Top.scala 1637:22]
  wire [15:0] n554_I_0_t1b_t1b; // @[Top.scala 1637:22]
  wire [15:0] n554_I_1_t0b; // @[Top.scala 1637:22]
  wire [15:0] n554_I_1_t1b_t0b; // @[Top.scala 1637:22]
  wire [15:0] n554_I_1_t1b_t1b; // @[Top.scala 1637:22]
  wire [15:0] n554_I_2_t0b; // @[Top.scala 1637:22]
  wire [15:0] n554_I_2_t1b_t0b; // @[Top.scala 1637:22]
  wire [15:0] n554_I_2_t1b_t1b; // @[Top.scala 1637:22]
  wire [15:0] n554_I_3_t0b; // @[Top.scala 1637:22]
  wire [15:0] n554_I_3_t1b_t0b; // @[Top.scala 1637:22]
  wire [15:0] n554_I_3_t1b_t1b; // @[Top.scala 1637:22]
  wire [15:0] n554_O_0_t1b_t0b; // @[Top.scala 1637:22]
  wire [15:0] n554_O_0_t1b_t1b; // @[Top.scala 1637:22]
  wire [15:0] n554_O_1_t1b_t0b; // @[Top.scala 1637:22]
  wire [15:0] n554_O_1_t1b_t1b; // @[Top.scala 1637:22]
  wire [15:0] n554_O_2_t1b_t0b; // @[Top.scala 1637:22]
  wire [15:0] n554_O_2_t1b_t1b; // @[Top.scala 1637:22]
  wire [15:0] n554_O_3_t1b_t0b; // @[Top.scala 1637:22]
  wire [15:0] n554_O_3_t1b_t1b; // @[Top.scala 1637:22]
  wire  n560_valid_up; // @[Top.scala 1640:22]
  wire  n560_valid_down; // @[Top.scala 1640:22]
  wire [15:0] n560_I_0_t1b_t0b; // @[Top.scala 1640:22]
  wire [15:0] n560_I_0_t1b_t1b; // @[Top.scala 1640:22]
  wire [15:0] n560_I_1_t1b_t0b; // @[Top.scala 1640:22]
  wire [15:0] n560_I_1_t1b_t1b; // @[Top.scala 1640:22]
  wire [15:0] n560_I_2_t1b_t0b; // @[Top.scala 1640:22]
  wire [15:0] n560_I_2_t1b_t1b; // @[Top.scala 1640:22]
  wire [15:0] n560_I_3_t1b_t0b; // @[Top.scala 1640:22]
  wire [15:0] n560_I_3_t1b_t1b; // @[Top.scala 1640:22]
  wire [15:0] n560_O_0; // @[Top.scala 1640:22]
  wire [15:0] n560_O_1; // @[Top.scala 1640:22]
  wire [15:0] n560_O_2; // @[Top.scala 1640:22]
  wire [15:0] n560_O_3; // @[Top.scala 1640:22]
  wire  n561_clock; // @[Top.scala 1643:22]
  wire  n561_reset; // @[Top.scala 1643:22]
  wire  n561_valid_up; // @[Top.scala 1643:22]
  wire  n561_valid_down; // @[Top.scala 1643:22]
  wire [15:0] n561_I_0; // @[Top.scala 1643:22]
  wire [15:0] n561_I_1; // @[Top.scala 1643:22]
  wire [15:0] n561_I_2; // @[Top.scala 1643:22]
  wire [15:0] n561_I_3; // @[Top.scala 1643:22]
  wire [15:0] n561_O_0; // @[Top.scala 1643:22]
  wire [15:0] n561_O_1; // @[Top.scala 1643:22]
  wire [15:0] n561_O_2; // @[Top.scala 1643:22]
  wire [15:0] n561_O_3; // @[Top.scala 1643:22]
  wire  n562_clock; // @[Top.scala 1646:22]
  wire  n562_reset; // @[Top.scala 1646:22]
  wire  n562_valid_up; // @[Top.scala 1646:22]
  wire  n562_valid_down; // @[Top.scala 1646:22]
  wire [15:0] n562_I_0; // @[Top.scala 1646:22]
  wire [15:0] n562_I_1; // @[Top.scala 1646:22]
  wire [15:0] n562_I_2; // @[Top.scala 1646:22]
  wire [15:0] n562_I_3; // @[Top.scala 1646:22]
  wire [15:0] n562_O_0; // @[Top.scala 1646:22]
  wire [15:0] n562_O_1; // @[Top.scala 1646:22]
  wire [15:0] n562_O_2; // @[Top.scala 1646:22]
  wire [15:0] n562_O_3; // @[Top.scala 1646:22]
  wire  n563_clock; // @[Top.scala 1649:22]
  wire  n563_reset; // @[Top.scala 1649:22]
  wire  n563_valid_up; // @[Top.scala 1649:22]
  wire  n563_valid_down; // @[Top.scala 1649:22]
  wire [15:0] n563_I_0; // @[Top.scala 1649:22]
  wire [15:0] n563_I_1; // @[Top.scala 1649:22]
  wire [15:0] n563_I_2; // @[Top.scala 1649:22]
  wire [15:0] n563_I_3; // @[Top.scala 1649:22]
  wire [15:0] n563_O_0; // @[Top.scala 1649:22]
  wire [15:0] n563_O_1; // @[Top.scala 1649:22]
  wire [15:0] n563_O_2; // @[Top.scala 1649:22]
  wire [15:0] n563_O_3; // @[Top.scala 1649:22]
  FIFO n1 ( // @[Top.scala 1586:20]
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
  MapT n10 ( // @[Top.scala 1589:21]
    .clock(n10_clock),
    .reset(n10_reset),
    .valid_up(n10_valid_up),
    .valid_down(n10_valid_down),
    .I_0(n10_I_0),
    .I_1(n10_I_1),
    .I_2(n10_I_2),
    .I_3(n10_I_3),
    .O_0_t0b(n10_O_0_t0b),
    .O_0_t1b_t0b(n10_O_0_t1b_t0b),
    .O_0_t1b_t1b(n10_O_0_t1b_t1b),
    .O_1_t0b(n10_O_1_t0b),
    .O_1_t1b_t0b(n10_O_1_t1b_t0b),
    .O_1_t1b_t1b(n10_O_1_t1b_t1b),
    .O_2_t0b(n10_O_2_t0b),
    .O_2_t1b_t0b(n10_O_2_t1b_t0b),
    .O_2_t1b_t1b(n10_O_2_t1b_t1b),
    .O_3_t0b(n10_O_3_t0b),
    .O_3_t1b_t0b(n10_O_3_t1b_t0b),
    .O_3_t1b_t1b(n10_O_3_t1b_t1b)
  );
  MapT_1 n44 ( // @[Top.scala 1592:21]
    .clock(n44_clock),
    .reset(n44_reset),
    .valid_up(n44_valid_up),
    .valid_down(n44_valid_down),
    .I_0_t0b(n44_I_0_t0b),
    .I_0_t1b_t0b(n44_I_0_t1b_t0b),
    .I_0_t1b_t1b(n44_I_0_t1b_t1b),
    .I_1_t0b(n44_I_1_t0b),
    .I_1_t1b_t0b(n44_I_1_t1b_t0b),
    .I_1_t1b_t1b(n44_I_1_t1b_t1b),
    .I_2_t0b(n44_I_2_t0b),
    .I_2_t1b_t0b(n44_I_2_t1b_t0b),
    .I_2_t1b_t1b(n44_I_2_t1b_t1b),
    .I_3_t0b(n44_I_3_t0b),
    .I_3_t1b_t0b(n44_I_3_t1b_t0b),
    .I_3_t1b_t1b(n44_I_3_t1b_t1b),
    .O_0_t0b(n44_O_0_t0b),
    .O_0_t1b_t0b(n44_O_0_t1b_t0b),
    .O_0_t1b_t1b(n44_O_0_t1b_t1b),
    .O_1_t0b(n44_O_1_t0b),
    .O_1_t1b_t0b(n44_O_1_t1b_t0b),
    .O_1_t1b_t1b(n44_O_1_t1b_t1b),
    .O_2_t0b(n44_O_2_t0b),
    .O_2_t1b_t0b(n44_O_2_t1b_t0b),
    .O_2_t1b_t1b(n44_O_2_t1b_t1b),
    .O_3_t0b(n44_O_3_t0b),
    .O_3_t1b_t0b(n44_O_3_t1b_t0b),
    .O_3_t1b_t1b(n44_O_3_t1b_t1b)
  );
  MapT_2 n78 ( // @[Top.scala 1595:21]
    .clock(n78_clock),
    .reset(n78_reset),
    .valid_up(n78_valid_up),
    .valid_down(n78_valid_down),
    .I_0_t0b(n78_I_0_t0b),
    .I_0_t1b_t0b(n78_I_0_t1b_t0b),
    .I_0_t1b_t1b(n78_I_0_t1b_t1b),
    .I_1_t0b(n78_I_1_t0b),
    .I_1_t1b_t0b(n78_I_1_t1b_t0b),
    .I_1_t1b_t1b(n78_I_1_t1b_t1b),
    .I_2_t0b(n78_I_2_t0b),
    .I_2_t1b_t0b(n78_I_2_t1b_t0b),
    .I_2_t1b_t1b(n78_I_2_t1b_t1b),
    .I_3_t0b(n78_I_3_t0b),
    .I_3_t1b_t0b(n78_I_3_t1b_t0b),
    .I_3_t1b_t1b(n78_I_3_t1b_t1b),
    .O_0_t0b(n78_O_0_t0b),
    .O_0_t1b_t0b(n78_O_0_t1b_t0b),
    .O_0_t1b_t1b(n78_O_0_t1b_t1b),
    .O_1_t0b(n78_O_1_t0b),
    .O_1_t1b_t0b(n78_O_1_t1b_t0b),
    .O_1_t1b_t1b(n78_O_1_t1b_t1b),
    .O_2_t0b(n78_O_2_t0b),
    .O_2_t1b_t0b(n78_O_2_t1b_t0b),
    .O_2_t1b_t1b(n78_O_2_t1b_t1b),
    .O_3_t0b(n78_O_3_t0b),
    .O_3_t1b_t0b(n78_O_3_t1b_t0b),
    .O_3_t1b_t1b(n78_O_3_t1b_t1b)
  );
  MapT_3 n112 ( // @[Top.scala 1598:22]
    .clock(n112_clock),
    .reset(n112_reset),
    .valid_up(n112_valid_up),
    .valid_down(n112_valid_down),
    .I_0_t0b(n112_I_0_t0b),
    .I_0_t1b_t0b(n112_I_0_t1b_t0b),
    .I_0_t1b_t1b(n112_I_0_t1b_t1b),
    .I_1_t0b(n112_I_1_t0b),
    .I_1_t1b_t0b(n112_I_1_t1b_t0b),
    .I_1_t1b_t1b(n112_I_1_t1b_t1b),
    .I_2_t0b(n112_I_2_t0b),
    .I_2_t1b_t0b(n112_I_2_t1b_t0b),
    .I_2_t1b_t1b(n112_I_2_t1b_t1b),
    .I_3_t0b(n112_I_3_t0b),
    .I_3_t1b_t0b(n112_I_3_t1b_t0b),
    .I_3_t1b_t1b(n112_I_3_t1b_t1b),
    .O_0_t0b(n112_O_0_t0b),
    .O_0_t1b_t0b(n112_O_0_t1b_t0b),
    .O_0_t1b_t1b(n112_O_0_t1b_t1b),
    .O_1_t0b(n112_O_1_t0b),
    .O_1_t1b_t0b(n112_O_1_t1b_t0b),
    .O_1_t1b_t1b(n112_O_1_t1b_t1b),
    .O_2_t0b(n112_O_2_t0b),
    .O_2_t1b_t0b(n112_O_2_t1b_t0b),
    .O_2_t1b_t1b(n112_O_2_t1b_t1b),
    .O_3_t0b(n112_O_3_t0b),
    .O_3_t1b_t0b(n112_O_3_t1b_t0b),
    .O_3_t1b_t1b(n112_O_3_t1b_t1b)
  );
  MapT_4 n146 ( // @[Top.scala 1601:22]
    .clock(n146_clock),
    .reset(n146_reset),
    .valid_up(n146_valid_up),
    .valid_down(n146_valid_down),
    .I_0_t0b(n146_I_0_t0b),
    .I_0_t1b_t0b(n146_I_0_t1b_t0b),
    .I_0_t1b_t1b(n146_I_0_t1b_t1b),
    .I_1_t0b(n146_I_1_t0b),
    .I_1_t1b_t0b(n146_I_1_t1b_t0b),
    .I_1_t1b_t1b(n146_I_1_t1b_t1b),
    .I_2_t0b(n146_I_2_t0b),
    .I_2_t1b_t0b(n146_I_2_t1b_t0b),
    .I_2_t1b_t1b(n146_I_2_t1b_t1b),
    .I_3_t0b(n146_I_3_t0b),
    .I_3_t1b_t0b(n146_I_3_t1b_t0b),
    .I_3_t1b_t1b(n146_I_3_t1b_t1b),
    .O_0_t0b(n146_O_0_t0b),
    .O_0_t1b_t0b(n146_O_0_t1b_t0b),
    .O_0_t1b_t1b(n146_O_0_t1b_t1b),
    .O_1_t0b(n146_O_1_t0b),
    .O_1_t1b_t0b(n146_O_1_t1b_t0b),
    .O_1_t1b_t1b(n146_O_1_t1b_t1b),
    .O_2_t0b(n146_O_2_t0b),
    .O_2_t1b_t0b(n146_O_2_t1b_t0b),
    .O_2_t1b_t1b(n146_O_2_t1b_t1b),
    .O_3_t0b(n146_O_3_t0b),
    .O_3_t1b_t0b(n146_O_3_t1b_t0b),
    .O_3_t1b_t1b(n146_O_3_t1b_t1b)
  );
  MapT_5 n180 ( // @[Top.scala 1604:22]
    .clock(n180_clock),
    .reset(n180_reset),
    .valid_up(n180_valid_up),
    .valid_down(n180_valid_down),
    .I_0_t0b(n180_I_0_t0b),
    .I_0_t1b_t0b(n180_I_0_t1b_t0b),
    .I_0_t1b_t1b(n180_I_0_t1b_t1b),
    .I_1_t0b(n180_I_1_t0b),
    .I_1_t1b_t0b(n180_I_1_t1b_t0b),
    .I_1_t1b_t1b(n180_I_1_t1b_t1b),
    .I_2_t0b(n180_I_2_t0b),
    .I_2_t1b_t0b(n180_I_2_t1b_t0b),
    .I_2_t1b_t1b(n180_I_2_t1b_t1b),
    .I_3_t0b(n180_I_3_t0b),
    .I_3_t1b_t0b(n180_I_3_t1b_t0b),
    .I_3_t1b_t1b(n180_I_3_t1b_t1b),
    .O_0_t0b(n180_O_0_t0b),
    .O_0_t1b_t0b(n180_O_0_t1b_t0b),
    .O_0_t1b_t1b(n180_O_0_t1b_t1b),
    .O_1_t0b(n180_O_1_t0b),
    .O_1_t1b_t0b(n180_O_1_t1b_t0b),
    .O_1_t1b_t1b(n180_O_1_t1b_t1b),
    .O_2_t0b(n180_O_2_t0b),
    .O_2_t1b_t0b(n180_O_2_t1b_t0b),
    .O_2_t1b_t1b(n180_O_2_t1b_t1b),
    .O_3_t0b(n180_O_3_t0b),
    .O_3_t1b_t0b(n180_O_3_t1b_t0b),
    .O_3_t1b_t1b(n180_O_3_t1b_t1b)
  );
  MapT_6 n214 ( // @[Top.scala 1607:22]
    .clock(n214_clock),
    .reset(n214_reset),
    .valid_up(n214_valid_up),
    .valid_down(n214_valid_down),
    .I_0_t0b(n214_I_0_t0b),
    .I_0_t1b_t0b(n214_I_0_t1b_t0b),
    .I_0_t1b_t1b(n214_I_0_t1b_t1b),
    .I_1_t0b(n214_I_1_t0b),
    .I_1_t1b_t0b(n214_I_1_t1b_t0b),
    .I_1_t1b_t1b(n214_I_1_t1b_t1b),
    .I_2_t0b(n214_I_2_t0b),
    .I_2_t1b_t0b(n214_I_2_t1b_t0b),
    .I_2_t1b_t1b(n214_I_2_t1b_t1b),
    .I_3_t0b(n214_I_3_t0b),
    .I_3_t1b_t0b(n214_I_3_t1b_t0b),
    .I_3_t1b_t1b(n214_I_3_t1b_t1b),
    .O_0_t0b(n214_O_0_t0b),
    .O_0_t1b_t0b(n214_O_0_t1b_t0b),
    .O_0_t1b_t1b(n214_O_0_t1b_t1b),
    .O_1_t0b(n214_O_1_t0b),
    .O_1_t1b_t0b(n214_O_1_t1b_t0b),
    .O_1_t1b_t1b(n214_O_1_t1b_t1b),
    .O_2_t0b(n214_O_2_t0b),
    .O_2_t1b_t0b(n214_O_2_t1b_t0b),
    .O_2_t1b_t1b(n214_O_2_t1b_t1b),
    .O_3_t0b(n214_O_3_t0b),
    .O_3_t1b_t0b(n214_O_3_t1b_t0b),
    .O_3_t1b_t1b(n214_O_3_t1b_t1b)
  );
  MapT_7 n248 ( // @[Top.scala 1610:22]
    .clock(n248_clock),
    .reset(n248_reset),
    .valid_up(n248_valid_up),
    .valid_down(n248_valid_down),
    .I_0_t0b(n248_I_0_t0b),
    .I_0_t1b_t0b(n248_I_0_t1b_t0b),
    .I_0_t1b_t1b(n248_I_0_t1b_t1b),
    .I_1_t0b(n248_I_1_t0b),
    .I_1_t1b_t0b(n248_I_1_t1b_t0b),
    .I_1_t1b_t1b(n248_I_1_t1b_t1b),
    .I_2_t0b(n248_I_2_t0b),
    .I_2_t1b_t0b(n248_I_2_t1b_t0b),
    .I_2_t1b_t1b(n248_I_2_t1b_t1b),
    .I_3_t0b(n248_I_3_t0b),
    .I_3_t1b_t0b(n248_I_3_t1b_t0b),
    .I_3_t1b_t1b(n248_I_3_t1b_t1b),
    .O_0_t0b(n248_O_0_t0b),
    .O_0_t1b_t0b(n248_O_0_t1b_t0b),
    .O_0_t1b_t1b(n248_O_0_t1b_t1b),
    .O_1_t0b(n248_O_1_t0b),
    .O_1_t1b_t0b(n248_O_1_t1b_t0b),
    .O_1_t1b_t1b(n248_O_1_t1b_t1b),
    .O_2_t0b(n248_O_2_t0b),
    .O_2_t1b_t0b(n248_O_2_t1b_t0b),
    .O_2_t1b_t1b(n248_O_2_t1b_t1b),
    .O_3_t0b(n248_O_3_t0b),
    .O_3_t1b_t0b(n248_O_3_t1b_t0b),
    .O_3_t1b_t1b(n248_O_3_t1b_t1b)
  );
  MapT_8 n282 ( // @[Top.scala 1613:22]
    .clock(n282_clock),
    .reset(n282_reset),
    .valid_up(n282_valid_up),
    .valid_down(n282_valid_down),
    .I_0_t0b(n282_I_0_t0b),
    .I_0_t1b_t0b(n282_I_0_t1b_t0b),
    .I_0_t1b_t1b(n282_I_0_t1b_t1b),
    .I_1_t0b(n282_I_1_t0b),
    .I_1_t1b_t0b(n282_I_1_t1b_t0b),
    .I_1_t1b_t1b(n282_I_1_t1b_t1b),
    .I_2_t0b(n282_I_2_t0b),
    .I_2_t1b_t0b(n282_I_2_t1b_t0b),
    .I_2_t1b_t1b(n282_I_2_t1b_t1b),
    .I_3_t0b(n282_I_3_t0b),
    .I_3_t1b_t0b(n282_I_3_t1b_t0b),
    .I_3_t1b_t1b(n282_I_3_t1b_t1b),
    .O_0_t0b(n282_O_0_t0b),
    .O_0_t1b_t0b(n282_O_0_t1b_t0b),
    .O_0_t1b_t1b(n282_O_0_t1b_t1b),
    .O_1_t0b(n282_O_1_t0b),
    .O_1_t1b_t0b(n282_O_1_t1b_t0b),
    .O_1_t1b_t1b(n282_O_1_t1b_t1b),
    .O_2_t0b(n282_O_2_t0b),
    .O_2_t1b_t0b(n282_O_2_t1b_t0b),
    .O_2_t1b_t1b(n282_O_2_t1b_t1b),
    .O_3_t0b(n282_O_3_t0b),
    .O_3_t1b_t0b(n282_O_3_t1b_t0b),
    .O_3_t1b_t1b(n282_O_3_t1b_t1b)
  );
  MapT_9 n316 ( // @[Top.scala 1616:22]
    .clock(n316_clock),
    .reset(n316_reset),
    .valid_up(n316_valid_up),
    .valid_down(n316_valid_down),
    .I_0_t0b(n316_I_0_t0b),
    .I_0_t1b_t0b(n316_I_0_t1b_t0b),
    .I_0_t1b_t1b(n316_I_0_t1b_t1b),
    .I_1_t0b(n316_I_1_t0b),
    .I_1_t1b_t0b(n316_I_1_t1b_t0b),
    .I_1_t1b_t1b(n316_I_1_t1b_t1b),
    .I_2_t0b(n316_I_2_t0b),
    .I_2_t1b_t0b(n316_I_2_t1b_t0b),
    .I_2_t1b_t1b(n316_I_2_t1b_t1b),
    .I_3_t0b(n316_I_3_t0b),
    .I_3_t1b_t0b(n316_I_3_t1b_t0b),
    .I_3_t1b_t1b(n316_I_3_t1b_t1b),
    .O_0_t0b(n316_O_0_t0b),
    .O_0_t1b_t0b(n316_O_0_t1b_t0b),
    .O_0_t1b_t1b(n316_O_0_t1b_t1b),
    .O_1_t0b(n316_O_1_t0b),
    .O_1_t1b_t0b(n316_O_1_t1b_t0b),
    .O_1_t1b_t1b(n316_O_1_t1b_t1b),
    .O_2_t0b(n316_O_2_t0b),
    .O_2_t1b_t0b(n316_O_2_t1b_t0b),
    .O_2_t1b_t1b(n316_O_2_t1b_t1b),
    .O_3_t0b(n316_O_3_t0b),
    .O_3_t1b_t0b(n316_O_3_t1b_t0b),
    .O_3_t1b_t1b(n316_O_3_t1b_t1b)
  );
  MapT_10 n350 ( // @[Top.scala 1619:22]
    .clock(n350_clock),
    .reset(n350_reset),
    .valid_up(n350_valid_up),
    .valid_down(n350_valid_down),
    .I_0_t0b(n350_I_0_t0b),
    .I_0_t1b_t0b(n350_I_0_t1b_t0b),
    .I_0_t1b_t1b(n350_I_0_t1b_t1b),
    .I_1_t0b(n350_I_1_t0b),
    .I_1_t1b_t0b(n350_I_1_t1b_t0b),
    .I_1_t1b_t1b(n350_I_1_t1b_t1b),
    .I_2_t0b(n350_I_2_t0b),
    .I_2_t1b_t0b(n350_I_2_t1b_t0b),
    .I_2_t1b_t1b(n350_I_2_t1b_t1b),
    .I_3_t0b(n350_I_3_t0b),
    .I_3_t1b_t0b(n350_I_3_t1b_t0b),
    .I_3_t1b_t1b(n350_I_3_t1b_t1b),
    .O_0_t0b(n350_O_0_t0b),
    .O_0_t1b_t0b(n350_O_0_t1b_t0b),
    .O_0_t1b_t1b(n350_O_0_t1b_t1b),
    .O_1_t0b(n350_O_1_t0b),
    .O_1_t1b_t0b(n350_O_1_t1b_t0b),
    .O_1_t1b_t1b(n350_O_1_t1b_t1b),
    .O_2_t0b(n350_O_2_t0b),
    .O_2_t1b_t0b(n350_O_2_t1b_t0b),
    .O_2_t1b_t1b(n350_O_2_t1b_t1b),
    .O_3_t0b(n350_O_3_t0b),
    .O_3_t1b_t0b(n350_O_3_t1b_t0b),
    .O_3_t1b_t1b(n350_O_3_t1b_t1b)
  );
  MapT_11 n384 ( // @[Top.scala 1622:22]
    .clock(n384_clock),
    .reset(n384_reset),
    .valid_up(n384_valid_up),
    .valid_down(n384_valid_down),
    .I_0_t0b(n384_I_0_t0b),
    .I_0_t1b_t0b(n384_I_0_t1b_t0b),
    .I_0_t1b_t1b(n384_I_0_t1b_t1b),
    .I_1_t0b(n384_I_1_t0b),
    .I_1_t1b_t0b(n384_I_1_t1b_t0b),
    .I_1_t1b_t1b(n384_I_1_t1b_t1b),
    .I_2_t0b(n384_I_2_t0b),
    .I_2_t1b_t0b(n384_I_2_t1b_t0b),
    .I_2_t1b_t1b(n384_I_2_t1b_t1b),
    .I_3_t0b(n384_I_3_t0b),
    .I_3_t1b_t0b(n384_I_3_t1b_t0b),
    .I_3_t1b_t1b(n384_I_3_t1b_t1b),
    .O_0_t0b(n384_O_0_t0b),
    .O_0_t1b_t0b(n384_O_0_t1b_t0b),
    .O_0_t1b_t1b(n384_O_0_t1b_t1b),
    .O_1_t0b(n384_O_1_t0b),
    .O_1_t1b_t0b(n384_O_1_t1b_t0b),
    .O_1_t1b_t1b(n384_O_1_t1b_t1b),
    .O_2_t0b(n384_O_2_t0b),
    .O_2_t1b_t0b(n384_O_2_t1b_t0b),
    .O_2_t1b_t1b(n384_O_2_t1b_t1b),
    .O_3_t0b(n384_O_3_t0b),
    .O_3_t1b_t0b(n384_O_3_t1b_t0b),
    .O_3_t1b_t1b(n384_O_3_t1b_t1b)
  );
  MapT_12 n418 ( // @[Top.scala 1625:22]
    .clock(n418_clock),
    .reset(n418_reset),
    .valid_up(n418_valid_up),
    .valid_down(n418_valid_down),
    .I_0_t0b(n418_I_0_t0b),
    .I_0_t1b_t0b(n418_I_0_t1b_t0b),
    .I_0_t1b_t1b(n418_I_0_t1b_t1b),
    .I_1_t0b(n418_I_1_t0b),
    .I_1_t1b_t0b(n418_I_1_t1b_t0b),
    .I_1_t1b_t1b(n418_I_1_t1b_t1b),
    .I_2_t0b(n418_I_2_t0b),
    .I_2_t1b_t0b(n418_I_2_t1b_t0b),
    .I_2_t1b_t1b(n418_I_2_t1b_t1b),
    .I_3_t0b(n418_I_3_t0b),
    .I_3_t1b_t0b(n418_I_3_t1b_t0b),
    .I_3_t1b_t1b(n418_I_3_t1b_t1b),
    .O_0_t0b(n418_O_0_t0b),
    .O_0_t1b_t0b(n418_O_0_t1b_t0b),
    .O_0_t1b_t1b(n418_O_0_t1b_t1b),
    .O_1_t0b(n418_O_1_t0b),
    .O_1_t1b_t0b(n418_O_1_t1b_t0b),
    .O_1_t1b_t1b(n418_O_1_t1b_t1b),
    .O_2_t0b(n418_O_2_t0b),
    .O_2_t1b_t0b(n418_O_2_t1b_t0b),
    .O_2_t1b_t1b(n418_O_2_t1b_t1b),
    .O_3_t0b(n418_O_3_t0b),
    .O_3_t1b_t0b(n418_O_3_t1b_t0b),
    .O_3_t1b_t1b(n418_O_3_t1b_t1b)
  );
  MapT_13 n452 ( // @[Top.scala 1628:22]
    .clock(n452_clock),
    .reset(n452_reset),
    .valid_up(n452_valid_up),
    .valid_down(n452_valid_down),
    .I_0_t0b(n452_I_0_t0b),
    .I_0_t1b_t0b(n452_I_0_t1b_t0b),
    .I_0_t1b_t1b(n452_I_0_t1b_t1b),
    .I_1_t0b(n452_I_1_t0b),
    .I_1_t1b_t0b(n452_I_1_t1b_t0b),
    .I_1_t1b_t1b(n452_I_1_t1b_t1b),
    .I_2_t0b(n452_I_2_t0b),
    .I_2_t1b_t0b(n452_I_2_t1b_t0b),
    .I_2_t1b_t1b(n452_I_2_t1b_t1b),
    .I_3_t0b(n452_I_3_t0b),
    .I_3_t1b_t0b(n452_I_3_t1b_t0b),
    .I_3_t1b_t1b(n452_I_3_t1b_t1b),
    .O_0_t0b(n452_O_0_t0b),
    .O_0_t1b_t0b(n452_O_0_t1b_t0b),
    .O_0_t1b_t1b(n452_O_0_t1b_t1b),
    .O_1_t0b(n452_O_1_t0b),
    .O_1_t1b_t0b(n452_O_1_t1b_t0b),
    .O_1_t1b_t1b(n452_O_1_t1b_t1b),
    .O_2_t0b(n452_O_2_t0b),
    .O_2_t1b_t0b(n452_O_2_t1b_t0b),
    .O_2_t1b_t1b(n452_O_2_t1b_t1b),
    .O_3_t0b(n452_O_3_t0b),
    .O_3_t1b_t0b(n452_O_3_t1b_t0b),
    .O_3_t1b_t1b(n452_O_3_t1b_t1b)
  );
  MapT_14 n486 ( // @[Top.scala 1631:22]
    .clock(n486_clock),
    .reset(n486_reset),
    .valid_up(n486_valid_up),
    .valid_down(n486_valid_down),
    .I_0_t0b(n486_I_0_t0b),
    .I_0_t1b_t0b(n486_I_0_t1b_t0b),
    .I_0_t1b_t1b(n486_I_0_t1b_t1b),
    .I_1_t0b(n486_I_1_t0b),
    .I_1_t1b_t0b(n486_I_1_t1b_t0b),
    .I_1_t1b_t1b(n486_I_1_t1b_t1b),
    .I_2_t0b(n486_I_2_t0b),
    .I_2_t1b_t0b(n486_I_2_t1b_t0b),
    .I_2_t1b_t1b(n486_I_2_t1b_t1b),
    .I_3_t0b(n486_I_3_t0b),
    .I_3_t1b_t0b(n486_I_3_t1b_t0b),
    .I_3_t1b_t1b(n486_I_3_t1b_t1b),
    .O_0_t0b(n486_O_0_t0b),
    .O_0_t1b_t0b(n486_O_0_t1b_t0b),
    .O_0_t1b_t1b(n486_O_0_t1b_t1b),
    .O_1_t0b(n486_O_1_t0b),
    .O_1_t1b_t0b(n486_O_1_t1b_t0b),
    .O_1_t1b_t1b(n486_O_1_t1b_t1b),
    .O_2_t0b(n486_O_2_t0b),
    .O_2_t1b_t0b(n486_O_2_t1b_t0b),
    .O_2_t1b_t1b(n486_O_2_t1b_t1b),
    .O_3_t0b(n486_O_3_t0b),
    .O_3_t1b_t0b(n486_O_3_t1b_t0b),
    .O_3_t1b_t1b(n486_O_3_t1b_t1b)
  );
  MapT_15 n520 ( // @[Top.scala 1634:22]
    .clock(n520_clock),
    .reset(n520_reset),
    .valid_up(n520_valid_up),
    .valid_down(n520_valid_down),
    .I_0_t0b(n520_I_0_t0b),
    .I_0_t1b_t0b(n520_I_0_t1b_t0b),
    .I_0_t1b_t1b(n520_I_0_t1b_t1b),
    .I_1_t0b(n520_I_1_t0b),
    .I_1_t1b_t0b(n520_I_1_t1b_t0b),
    .I_1_t1b_t1b(n520_I_1_t1b_t1b),
    .I_2_t0b(n520_I_2_t0b),
    .I_2_t1b_t0b(n520_I_2_t1b_t0b),
    .I_2_t1b_t1b(n520_I_2_t1b_t1b),
    .I_3_t0b(n520_I_3_t0b),
    .I_3_t1b_t0b(n520_I_3_t1b_t0b),
    .I_3_t1b_t1b(n520_I_3_t1b_t1b),
    .O_0_t0b(n520_O_0_t0b),
    .O_0_t1b_t0b(n520_O_0_t1b_t0b),
    .O_0_t1b_t1b(n520_O_0_t1b_t1b),
    .O_1_t0b(n520_O_1_t0b),
    .O_1_t1b_t0b(n520_O_1_t1b_t0b),
    .O_1_t1b_t1b(n520_O_1_t1b_t1b),
    .O_2_t0b(n520_O_2_t0b),
    .O_2_t1b_t0b(n520_O_2_t1b_t0b),
    .O_2_t1b_t1b(n520_O_2_t1b_t1b),
    .O_3_t0b(n520_O_3_t0b),
    .O_3_t1b_t0b(n520_O_3_t1b_t0b),
    .O_3_t1b_t1b(n520_O_3_t1b_t1b)
  );
  MapT_16 n554 ( // @[Top.scala 1637:22]
    .clock(n554_clock),
    .reset(n554_reset),
    .valid_up(n554_valid_up),
    .valid_down(n554_valid_down),
    .I_0_t0b(n554_I_0_t0b),
    .I_0_t1b_t0b(n554_I_0_t1b_t0b),
    .I_0_t1b_t1b(n554_I_0_t1b_t1b),
    .I_1_t0b(n554_I_1_t0b),
    .I_1_t1b_t0b(n554_I_1_t1b_t0b),
    .I_1_t1b_t1b(n554_I_1_t1b_t1b),
    .I_2_t0b(n554_I_2_t0b),
    .I_2_t1b_t0b(n554_I_2_t1b_t0b),
    .I_2_t1b_t1b(n554_I_2_t1b_t1b),
    .I_3_t0b(n554_I_3_t0b),
    .I_3_t1b_t0b(n554_I_3_t1b_t0b),
    .I_3_t1b_t1b(n554_I_3_t1b_t1b),
    .O_0_t1b_t0b(n554_O_0_t1b_t0b),
    .O_0_t1b_t1b(n554_O_0_t1b_t1b),
    .O_1_t1b_t0b(n554_O_1_t1b_t0b),
    .O_1_t1b_t1b(n554_O_1_t1b_t1b),
    .O_2_t1b_t0b(n554_O_2_t1b_t0b),
    .O_2_t1b_t1b(n554_O_2_t1b_t1b),
    .O_3_t1b_t0b(n554_O_3_t1b_t0b),
    .O_3_t1b_t1b(n554_O_3_t1b_t1b)
  );
  MapT_17 n560 ( // @[Top.scala 1640:22]
    .valid_up(n560_valid_up),
    .valid_down(n560_valid_down),
    .I_0_t1b_t0b(n560_I_0_t1b_t0b),
    .I_0_t1b_t1b(n560_I_0_t1b_t1b),
    .I_1_t1b_t0b(n560_I_1_t1b_t0b),
    .I_1_t1b_t1b(n560_I_1_t1b_t1b),
    .I_2_t1b_t0b(n560_I_2_t1b_t0b),
    .I_2_t1b_t1b(n560_I_2_t1b_t1b),
    .I_3_t1b_t0b(n560_I_3_t1b_t0b),
    .I_3_t1b_t1b(n560_I_3_t1b_t1b),
    .O_0(n560_O_0),
    .O_1(n560_O_1),
    .O_2(n560_O_2),
    .O_3(n560_O_3)
  );
  FIFO n561 ( // @[Top.scala 1643:22]
    .clock(n561_clock),
    .reset(n561_reset),
    .valid_up(n561_valid_up),
    .valid_down(n561_valid_down),
    .I_0(n561_I_0),
    .I_1(n561_I_1),
    .I_2(n561_I_2),
    .I_3(n561_I_3),
    .O_0(n561_O_0),
    .O_1(n561_O_1),
    .O_2(n561_O_2),
    .O_3(n561_O_3)
  );
  FIFO n562 ( // @[Top.scala 1646:22]
    .clock(n562_clock),
    .reset(n562_reset),
    .valid_up(n562_valid_up),
    .valid_down(n562_valid_down),
    .I_0(n562_I_0),
    .I_1(n562_I_1),
    .I_2(n562_I_2),
    .I_3(n562_I_3),
    .O_0(n562_O_0),
    .O_1(n562_O_1),
    .O_2(n562_O_2),
    .O_3(n562_O_3)
  );
  FIFO n563 ( // @[Top.scala 1649:22]
    .clock(n563_clock),
    .reset(n563_reset),
    .valid_up(n563_valid_up),
    .valid_down(n563_valid_down),
    .I_0(n563_I_0),
    .I_1(n563_I_1),
    .I_2(n563_I_2),
    .I_3(n563_I_3),
    .O_0(n563_O_0),
    .O_1(n563_O_1),
    .O_2(n563_O_2),
    .O_3(n563_O_3)
  );
  assign valid_down = n563_valid_down; // @[Top.scala 1653:16]
  assign O_0 = n563_O_0; // @[Top.scala 1652:7]
  assign O_1 = n563_O_1; // @[Top.scala 1652:7]
  assign O_2 = n563_O_2; // @[Top.scala 1652:7]
  assign O_3 = n563_O_3; // @[Top.scala 1652:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 1588:17]
  assign n1_I_0 = I_0; // @[Top.scala 1587:10]
  assign n1_I_1 = I_1; // @[Top.scala 1587:10]
  assign n1_I_2 = I_2; // @[Top.scala 1587:10]
  assign n1_I_3 = I_3; // @[Top.scala 1587:10]
  assign n10_clock = clock;
  assign n10_reset = reset;
  assign n10_valid_up = n1_valid_down; // @[Top.scala 1591:18]
  assign n10_I_0 = n1_O_0; // @[Top.scala 1590:11]
  assign n10_I_1 = n1_O_1; // @[Top.scala 1590:11]
  assign n10_I_2 = n1_O_2; // @[Top.scala 1590:11]
  assign n10_I_3 = n1_O_3; // @[Top.scala 1590:11]
  assign n44_clock = clock;
  assign n44_reset = reset;
  assign n44_valid_up = n10_valid_down; // @[Top.scala 1594:18]
  assign n44_I_0_t0b = n10_O_0_t0b; // @[Top.scala 1593:11]
  assign n44_I_0_t1b_t0b = n10_O_0_t1b_t0b; // @[Top.scala 1593:11]
  assign n44_I_0_t1b_t1b = n10_O_0_t1b_t1b; // @[Top.scala 1593:11]
  assign n44_I_1_t0b = n10_O_1_t0b; // @[Top.scala 1593:11]
  assign n44_I_1_t1b_t0b = n10_O_1_t1b_t0b; // @[Top.scala 1593:11]
  assign n44_I_1_t1b_t1b = n10_O_1_t1b_t1b; // @[Top.scala 1593:11]
  assign n44_I_2_t0b = n10_O_2_t0b; // @[Top.scala 1593:11]
  assign n44_I_2_t1b_t0b = n10_O_2_t1b_t0b; // @[Top.scala 1593:11]
  assign n44_I_2_t1b_t1b = n10_O_2_t1b_t1b; // @[Top.scala 1593:11]
  assign n44_I_3_t0b = n10_O_3_t0b; // @[Top.scala 1593:11]
  assign n44_I_3_t1b_t0b = n10_O_3_t1b_t0b; // @[Top.scala 1593:11]
  assign n44_I_3_t1b_t1b = n10_O_3_t1b_t1b; // @[Top.scala 1593:11]
  assign n78_clock = clock;
  assign n78_reset = reset;
  assign n78_valid_up = n44_valid_down; // @[Top.scala 1597:18]
  assign n78_I_0_t0b = n44_O_0_t0b; // @[Top.scala 1596:11]
  assign n78_I_0_t1b_t0b = n44_O_0_t1b_t0b; // @[Top.scala 1596:11]
  assign n78_I_0_t1b_t1b = n44_O_0_t1b_t1b; // @[Top.scala 1596:11]
  assign n78_I_1_t0b = n44_O_1_t0b; // @[Top.scala 1596:11]
  assign n78_I_1_t1b_t0b = n44_O_1_t1b_t0b; // @[Top.scala 1596:11]
  assign n78_I_1_t1b_t1b = n44_O_1_t1b_t1b; // @[Top.scala 1596:11]
  assign n78_I_2_t0b = n44_O_2_t0b; // @[Top.scala 1596:11]
  assign n78_I_2_t1b_t0b = n44_O_2_t1b_t0b; // @[Top.scala 1596:11]
  assign n78_I_2_t1b_t1b = n44_O_2_t1b_t1b; // @[Top.scala 1596:11]
  assign n78_I_3_t0b = n44_O_3_t0b; // @[Top.scala 1596:11]
  assign n78_I_3_t1b_t0b = n44_O_3_t1b_t0b; // @[Top.scala 1596:11]
  assign n78_I_3_t1b_t1b = n44_O_3_t1b_t1b; // @[Top.scala 1596:11]
  assign n112_clock = clock;
  assign n112_reset = reset;
  assign n112_valid_up = n78_valid_down; // @[Top.scala 1600:19]
  assign n112_I_0_t0b = n78_O_0_t0b; // @[Top.scala 1599:12]
  assign n112_I_0_t1b_t0b = n78_O_0_t1b_t0b; // @[Top.scala 1599:12]
  assign n112_I_0_t1b_t1b = n78_O_0_t1b_t1b; // @[Top.scala 1599:12]
  assign n112_I_1_t0b = n78_O_1_t0b; // @[Top.scala 1599:12]
  assign n112_I_1_t1b_t0b = n78_O_1_t1b_t0b; // @[Top.scala 1599:12]
  assign n112_I_1_t1b_t1b = n78_O_1_t1b_t1b; // @[Top.scala 1599:12]
  assign n112_I_2_t0b = n78_O_2_t0b; // @[Top.scala 1599:12]
  assign n112_I_2_t1b_t0b = n78_O_2_t1b_t0b; // @[Top.scala 1599:12]
  assign n112_I_2_t1b_t1b = n78_O_2_t1b_t1b; // @[Top.scala 1599:12]
  assign n112_I_3_t0b = n78_O_3_t0b; // @[Top.scala 1599:12]
  assign n112_I_3_t1b_t0b = n78_O_3_t1b_t0b; // @[Top.scala 1599:12]
  assign n112_I_3_t1b_t1b = n78_O_3_t1b_t1b; // @[Top.scala 1599:12]
  assign n146_clock = clock;
  assign n146_reset = reset;
  assign n146_valid_up = n112_valid_down; // @[Top.scala 1603:19]
  assign n146_I_0_t0b = n112_O_0_t0b; // @[Top.scala 1602:12]
  assign n146_I_0_t1b_t0b = n112_O_0_t1b_t0b; // @[Top.scala 1602:12]
  assign n146_I_0_t1b_t1b = n112_O_0_t1b_t1b; // @[Top.scala 1602:12]
  assign n146_I_1_t0b = n112_O_1_t0b; // @[Top.scala 1602:12]
  assign n146_I_1_t1b_t0b = n112_O_1_t1b_t0b; // @[Top.scala 1602:12]
  assign n146_I_1_t1b_t1b = n112_O_1_t1b_t1b; // @[Top.scala 1602:12]
  assign n146_I_2_t0b = n112_O_2_t0b; // @[Top.scala 1602:12]
  assign n146_I_2_t1b_t0b = n112_O_2_t1b_t0b; // @[Top.scala 1602:12]
  assign n146_I_2_t1b_t1b = n112_O_2_t1b_t1b; // @[Top.scala 1602:12]
  assign n146_I_3_t0b = n112_O_3_t0b; // @[Top.scala 1602:12]
  assign n146_I_3_t1b_t0b = n112_O_3_t1b_t0b; // @[Top.scala 1602:12]
  assign n146_I_3_t1b_t1b = n112_O_3_t1b_t1b; // @[Top.scala 1602:12]
  assign n180_clock = clock;
  assign n180_reset = reset;
  assign n180_valid_up = n146_valid_down; // @[Top.scala 1606:19]
  assign n180_I_0_t0b = n146_O_0_t0b; // @[Top.scala 1605:12]
  assign n180_I_0_t1b_t0b = n146_O_0_t1b_t0b; // @[Top.scala 1605:12]
  assign n180_I_0_t1b_t1b = n146_O_0_t1b_t1b; // @[Top.scala 1605:12]
  assign n180_I_1_t0b = n146_O_1_t0b; // @[Top.scala 1605:12]
  assign n180_I_1_t1b_t0b = n146_O_1_t1b_t0b; // @[Top.scala 1605:12]
  assign n180_I_1_t1b_t1b = n146_O_1_t1b_t1b; // @[Top.scala 1605:12]
  assign n180_I_2_t0b = n146_O_2_t0b; // @[Top.scala 1605:12]
  assign n180_I_2_t1b_t0b = n146_O_2_t1b_t0b; // @[Top.scala 1605:12]
  assign n180_I_2_t1b_t1b = n146_O_2_t1b_t1b; // @[Top.scala 1605:12]
  assign n180_I_3_t0b = n146_O_3_t0b; // @[Top.scala 1605:12]
  assign n180_I_3_t1b_t0b = n146_O_3_t1b_t0b; // @[Top.scala 1605:12]
  assign n180_I_3_t1b_t1b = n146_O_3_t1b_t1b; // @[Top.scala 1605:12]
  assign n214_clock = clock;
  assign n214_reset = reset;
  assign n214_valid_up = n180_valid_down; // @[Top.scala 1609:19]
  assign n214_I_0_t0b = n180_O_0_t0b; // @[Top.scala 1608:12]
  assign n214_I_0_t1b_t0b = n180_O_0_t1b_t0b; // @[Top.scala 1608:12]
  assign n214_I_0_t1b_t1b = n180_O_0_t1b_t1b; // @[Top.scala 1608:12]
  assign n214_I_1_t0b = n180_O_1_t0b; // @[Top.scala 1608:12]
  assign n214_I_1_t1b_t0b = n180_O_1_t1b_t0b; // @[Top.scala 1608:12]
  assign n214_I_1_t1b_t1b = n180_O_1_t1b_t1b; // @[Top.scala 1608:12]
  assign n214_I_2_t0b = n180_O_2_t0b; // @[Top.scala 1608:12]
  assign n214_I_2_t1b_t0b = n180_O_2_t1b_t0b; // @[Top.scala 1608:12]
  assign n214_I_2_t1b_t1b = n180_O_2_t1b_t1b; // @[Top.scala 1608:12]
  assign n214_I_3_t0b = n180_O_3_t0b; // @[Top.scala 1608:12]
  assign n214_I_3_t1b_t0b = n180_O_3_t1b_t0b; // @[Top.scala 1608:12]
  assign n214_I_3_t1b_t1b = n180_O_3_t1b_t1b; // @[Top.scala 1608:12]
  assign n248_clock = clock;
  assign n248_reset = reset;
  assign n248_valid_up = n214_valid_down; // @[Top.scala 1612:19]
  assign n248_I_0_t0b = n214_O_0_t0b; // @[Top.scala 1611:12]
  assign n248_I_0_t1b_t0b = n214_O_0_t1b_t0b; // @[Top.scala 1611:12]
  assign n248_I_0_t1b_t1b = n214_O_0_t1b_t1b; // @[Top.scala 1611:12]
  assign n248_I_1_t0b = n214_O_1_t0b; // @[Top.scala 1611:12]
  assign n248_I_1_t1b_t0b = n214_O_1_t1b_t0b; // @[Top.scala 1611:12]
  assign n248_I_1_t1b_t1b = n214_O_1_t1b_t1b; // @[Top.scala 1611:12]
  assign n248_I_2_t0b = n214_O_2_t0b; // @[Top.scala 1611:12]
  assign n248_I_2_t1b_t0b = n214_O_2_t1b_t0b; // @[Top.scala 1611:12]
  assign n248_I_2_t1b_t1b = n214_O_2_t1b_t1b; // @[Top.scala 1611:12]
  assign n248_I_3_t0b = n214_O_3_t0b; // @[Top.scala 1611:12]
  assign n248_I_3_t1b_t0b = n214_O_3_t1b_t0b; // @[Top.scala 1611:12]
  assign n248_I_3_t1b_t1b = n214_O_3_t1b_t1b; // @[Top.scala 1611:12]
  assign n282_clock = clock;
  assign n282_reset = reset;
  assign n282_valid_up = n248_valid_down; // @[Top.scala 1615:19]
  assign n282_I_0_t0b = n248_O_0_t0b; // @[Top.scala 1614:12]
  assign n282_I_0_t1b_t0b = n248_O_0_t1b_t0b; // @[Top.scala 1614:12]
  assign n282_I_0_t1b_t1b = n248_O_0_t1b_t1b; // @[Top.scala 1614:12]
  assign n282_I_1_t0b = n248_O_1_t0b; // @[Top.scala 1614:12]
  assign n282_I_1_t1b_t0b = n248_O_1_t1b_t0b; // @[Top.scala 1614:12]
  assign n282_I_1_t1b_t1b = n248_O_1_t1b_t1b; // @[Top.scala 1614:12]
  assign n282_I_2_t0b = n248_O_2_t0b; // @[Top.scala 1614:12]
  assign n282_I_2_t1b_t0b = n248_O_2_t1b_t0b; // @[Top.scala 1614:12]
  assign n282_I_2_t1b_t1b = n248_O_2_t1b_t1b; // @[Top.scala 1614:12]
  assign n282_I_3_t0b = n248_O_3_t0b; // @[Top.scala 1614:12]
  assign n282_I_3_t1b_t0b = n248_O_3_t1b_t0b; // @[Top.scala 1614:12]
  assign n282_I_3_t1b_t1b = n248_O_3_t1b_t1b; // @[Top.scala 1614:12]
  assign n316_clock = clock;
  assign n316_reset = reset;
  assign n316_valid_up = n282_valid_down; // @[Top.scala 1618:19]
  assign n316_I_0_t0b = n282_O_0_t0b; // @[Top.scala 1617:12]
  assign n316_I_0_t1b_t0b = n282_O_0_t1b_t0b; // @[Top.scala 1617:12]
  assign n316_I_0_t1b_t1b = n282_O_0_t1b_t1b; // @[Top.scala 1617:12]
  assign n316_I_1_t0b = n282_O_1_t0b; // @[Top.scala 1617:12]
  assign n316_I_1_t1b_t0b = n282_O_1_t1b_t0b; // @[Top.scala 1617:12]
  assign n316_I_1_t1b_t1b = n282_O_1_t1b_t1b; // @[Top.scala 1617:12]
  assign n316_I_2_t0b = n282_O_2_t0b; // @[Top.scala 1617:12]
  assign n316_I_2_t1b_t0b = n282_O_2_t1b_t0b; // @[Top.scala 1617:12]
  assign n316_I_2_t1b_t1b = n282_O_2_t1b_t1b; // @[Top.scala 1617:12]
  assign n316_I_3_t0b = n282_O_3_t0b; // @[Top.scala 1617:12]
  assign n316_I_3_t1b_t0b = n282_O_3_t1b_t0b; // @[Top.scala 1617:12]
  assign n316_I_3_t1b_t1b = n282_O_3_t1b_t1b; // @[Top.scala 1617:12]
  assign n350_clock = clock;
  assign n350_reset = reset;
  assign n350_valid_up = n316_valid_down; // @[Top.scala 1621:19]
  assign n350_I_0_t0b = n316_O_0_t0b; // @[Top.scala 1620:12]
  assign n350_I_0_t1b_t0b = n316_O_0_t1b_t0b; // @[Top.scala 1620:12]
  assign n350_I_0_t1b_t1b = n316_O_0_t1b_t1b; // @[Top.scala 1620:12]
  assign n350_I_1_t0b = n316_O_1_t0b; // @[Top.scala 1620:12]
  assign n350_I_1_t1b_t0b = n316_O_1_t1b_t0b; // @[Top.scala 1620:12]
  assign n350_I_1_t1b_t1b = n316_O_1_t1b_t1b; // @[Top.scala 1620:12]
  assign n350_I_2_t0b = n316_O_2_t0b; // @[Top.scala 1620:12]
  assign n350_I_2_t1b_t0b = n316_O_2_t1b_t0b; // @[Top.scala 1620:12]
  assign n350_I_2_t1b_t1b = n316_O_2_t1b_t1b; // @[Top.scala 1620:12]
  assign n350_I_3_t0b = n316_O_3_t0b; // @[Top.scala 1620:12]
  assign n350_I_3_t1b_t0b = n316_O_3_t1b_t0b; // @[Top.scala 1620:12]
  assign n350_I_3_t1b_t1b = n316_O_3_t1b_t1b; // @[Top.scala 1620:12]
  assign n384_clock = clock;
  assign n384_reset = reset;
  assign n384_valid_up = n350_valid_down; // @[Top.scala 1624:19]
  assign n384_I_0_t0b = n350_O_0_t0b; // @[Top.scala 1623:12]
  assign n384_I_0_t1b_t0b = n350_O_0_t1b_t0b; // @[Top.scala 1623:12]
  assign n384_I_0_t1b_t1b = n350_O_0_t1b_t1b; // @[Top.scala 1623:12]
  assign n384_I_1_t0b = n350_O_1_t0b; // @[Top.scala 1623:12]
  assign n384_I_1_t1b_t0b = n350_O_1_t1b_t0b; // @[Top.scala 1623:12]
  assign n384_I_1_t1b_t1b = n350_O_1_t1b_t1b; // @[Top.scala 1623:12]
  assign n384_I_2_t0b = n350_O_2_t0b; // @[Top.scala 1623:12]
  assign n384_I_2_t1b_t0b = n350_O_2_t1b_t0b; // @[Top.scala 1623:12]
  assign n384_I_2_t1b_t1b = n350_O_2_t1b_t1b; // @[Top.scala 1623:12]
  assign n384_I_3_t0b = n350_O_3_t0b; // @[Top.scala 1623:12]
  assign n384_I_3_t1b_t0b = n350_O_3_t1b_t0b; // @[Top.scala 1623:12]
  assign n384_I_3_t1b_t1b = n350_O_3_t1b_t1b; // @[Top.scala 1623:12]
  assign n418_clock = clock;
  assign n418_reset = reset;
  assign n418_valid_up = n384_valid_down; // @[Top.scala 1627:19]
  assign n418_I_0_t0b = n384_O_0_t0b; // @[Top.scala 1626:12]
  assign n418_I_0_t1b_t0b = n384_O_0_t1b_t0b; // @[Top.scala 1626:12]
  assign n418_I_0_t1b_t1b = n384_O_0_t1b_t1b; // @[Top.scala 1626:12]
  assign n418_I_1_t0b = n384_O_1_t0b; // @[Top.scala 1626:12]
  assign n418_I_1_t1b_t0b = n384_O_1_t1b_t0b; // @[Top.scala 1626:12]
  assign n418_I_1_t1b_t1b = n384_O_1_t1b_t1b; // @[Top.scala 1626:12]
  assign n418_I_2_t0b = n384_O_2_t0b; // @[Top.scala 1626:12]
  assign n418_I_2_t1b_t0b = n384_O_2_t1b_t0b; // @[Top.scala 1626:12]
  assign n418_I_2_t1b_t1b = n384_O_2_t1b_t1b; // @[Top.scala 1626:12]
  assign n418_I_3_t0b = n384_O_3_t0b; // @[Top.scala 1626:12]
  assign n418_I_3_t1b_t0b = n384_O_3_t1b_t0b; // @[Top.scala 1626:12]
  assign n418_I_3_t1b_t1b = n384_O_3_t1b_t1b; // @[Top.scala 1626:12]
  assign n452_clock = clock;
  assign n452_reset = reset;
  assign n452_valid_up = n418_valid_down; // @[Top.scala 1630:19]
  assign n452_I_0_t0b = n418_O_0_t0b; // @[Top.scala 1629:12]
  assign n452_I_0_t1b_t0b = n418_O_0_t1b_t0b; // @[Top.scala 1629:12]
  assign n452_I_0_t1b_t1b = n418_O_0_t1b_t1b; // @[Top.scala 1629:12]
  assign n452_I_1_t0b = n418_O_1_t0b; // @[Top.scala 1629:12]
  assign n452_I_1_t1b_t0b = n418_O_1_t1b_t0b; // @[Top.scala 1629:12]
  assign n452_I_1_t1b_t1b = n418_O_1_t1b_t1b; // @[Top.scala 1629:12]
  assign n452_I_2_t0b = n418_O_2_t0b; // @[Top.scala 1629:12]
  assign n452_I_2_t1b_t0b = n418_O_2_t1b_t0b; // @[Top.scala 1629:12]
  assign n452_I_2_t1b_t1b = n418_O_2_t1b_t1b; // @[Top.scala 1629:12]
  assign n452_I_3_t0b = n418_O_3_t0b; // @[Top.scala 1629:12]
  assign n452_I_3_t1b_t0b = n418_O_3_t1b_t0b; // @[Top.scala 1629:12]
  assign n452_I_3_t1b_t1b = n418_O_3_t1b_t1b; // @[Top.scala 1629:12]
  assign n486_clock = clock;
  assign n486_reset = reset;
  assign n486_valid_up = n452_valid_down; // @[Top.scala 1633:19]
  assign n486_I_0_t0b = n452_O_0_t0b; // @[Top.scala 1632:12]
  assign n486_I_0_t1b_t0b = n452_O_0_t1b_t0b; // @[Top.scala 1632:12]
  assign n486_I_0_t1b_t1b = n452_O_0_t1b_t1b; // @[Top.scala 1632:12]
  assign n486_I_1_t0b = n452_O_1_t0b; // @[Top.scala 1632:12]
  assign n486_I_1_t1b_t0b = n452_O_1_t1b_t0b; // @[Top.scala 1632:12]
  assign n486_I_1_t1b_t1b = n452_O_1_t1b_t1b; // @[Top.scala 1632:12]
  assign n486_I_2_t0b = n452_O_2_t0b; // @[Top.scala 1632:12]
  assign n486_I_2_t1b_t0b = n452_O_2_t1b_t0b; // @[Top.scala 1632:12]
  assign n486_I_2_t1b_t1b = n452_O_2_t1b_t1b; // @[Top.scala 1632:12]
  assign n486_I_3_t0b = n452_O_3_t0b; // @[Top.scala 1632:12]
  assign n486_I_3_t1b_t0b = n452_O_3_t1b_t0b; // @[Top.scala 1632:12]
  assign n486_I_3_t1b_t1b = n452_O_3_t1b_t1b; // @[Top.scala 1632:12]
  assign n520_clock = clock;
  assign n520_reset = reset;
  assign n520_valid_up = n486_valid_down; // @[Top.scala 1636:19]
  assign n520_I_0_t0b = n486_O_0_t0b; // @[Top.scala 1635:12]
  assign n520_I_0_t1b_t0b = n486_O_0_t1b_t0b; // @[Top.scala 1635:12]
  assign n520_I_0_t1b_t1b = n486_O_0_t1b_t1b; // @[Top.scala 1635:12]
  assign n520_I_1_t0b = n486_O_1_t0b; // @[Top.scala 1635:12]
  assign n520_I_1_t1b_t0b = n486_O_1_t1b_t0b; // @[Top.scala 1635:12]
  assign n520_I_1_t1b_t1b = n486_O_1_t1b_t1b; // @[Top.scala 1635:12]
  assign n520_I_2_t0b = n486_O_2_t0b; // @[Top.scala 1635:12]
  assign n520_I_2_t1b_t0b = n486_O_2_t1b_t0b; // @[Top.scala 1635:12]
  assign n520_I_2_t1b_t1b = n486_O_2_t1b_t1b; // @[Top.scala 1635:12]
  assign n520_I_3_t0b = n486_O_3_t0b; // @[Top.scala 1635:12]
  assign n520_I_3_t1b_t0b = n486_O_3_t1b_t0b; // @[Top.scala 1635:12]
  assign n520_I_3_t1b_t1b = n486_O_3_t1b_t1b; // @[Top.scala 1635:12]
  assign n554_clock = clock;
  assign n554_reset = reset;
  assign n554_valid_up = n520_valid_down; // @[Top.scala 1639:19]
  assign n554_I_0_t0b = n520_O_0_t0b; // @[Top.scala 1638:12]
  assign n554_I_0_t1b_t0b = n520_O_0_t1b_t0b; // @[Top.scala 1638:12]
  assign n554_I_0_t1b_t1b = n520_O_0_t1b_t1b; // @[Top.scala 1638:12]
  assign n554_I_1_t0b = n520_O_1_t0b; // @[Top.scala 1638:12]
  assign n554_I_1_t1b_t0b = n520_O_1_t1b_t0b; // @[Top.scala 1638:12]
  assign n554_I_1_t1b_t1b = n520_O_1_t1b_t1b; // @[Top.scala 1638:12]
  assign n554_I_2_t0b = n520_O_2_t0b; // @[Top.scala 1638:12]
  assign n554_I_2_t1b_t0b = n520_O_2_t1b_t0b; // @[Top.scala 1638:12]
  assign n554_I_2_t1b_t1b = n520_O_2_t1b_t1b; // @[Top.scala 1638:12]
  assign n554_I_3_t0b = n520_O_3_t0b; // @[Top.scala 1638:12]
  assign n554_I_3_t1b_t0b = n520_O_3_t1b_t0b; // @[Top.scala 1638:12]
  assign n554_I_3_t1b_t1b = n520_O_3_t1b_t1b; // @[Top.scala 1638:12]
  assign n560_valid_up = n554_valid_down; // @[Top.scala 1642:19]
  assign n560_I_0_t1b_t0b = n554_O_0_t1b_t0b; // @[Top.scala 1641:12]
  assign n560_I_0_t1b_t1b = n554_O_0_t1b_t1b; // @[Top.scala 1641:12]
  assign n560_I_1_t1b_t0b = n554_O_1_t1b_t0b; // @[Top.scala 1641:12]
  assign n560_I_1_t1b_t1b = n554_O_1_t1b_t1b; // @[Top.scala 1641:12]
  assign n560_I_2_t1b_t0b = n554_O_2_t1b_t0b; // @[Top.scala 1641:12]
  assign n560_I_2_t1b_t1b = n554_O_2_t1b_t1b; // @[Top.scala 1641:12]
  assign n560_I_3_t1b_t0b = n554_O_3_t1b_t0b; // @[Top.scala 1641:12]
  assign n560_I_3_t1b_t1b = n554_O_3_t1b_t1b; // @[Top.scala 1641:12]
  assign n561_clock = clock;
  assign n561_reset = reset;
  assign n561_valid_up = n560_valid_down; // @[Top.scala 1645:19]
  assign n561_I_0 = n560_O_0; // @[Top.scala 1644:12]
  assign n561_I_1 = n560_O_1; // @[Top.scala 1644:12]
  assign n561_I_2 = n560_O_2; // @[Top.scala 1644:12]
  assign n561_I_3 = n560_O_3; // @[Top.scala 1644:12]
  assign n562_clock = clock;
  assign n562_reset = reset;
  assign n562_valid_up = n561_valid_down; // @[Top.scala 1648:19]
  assign n562_I_0 = n561_O_0; // @[Top.scala 1647:12]
  assign n562_I_1 = n561_O_1; // @[Top.scala 1647:12]
  assign n562_I_2 = n561_O_2; // @[Top.scala 1647:12]
  assign n562_I_3 = n561_O_3; // @[Top.scala 1647:12]
  assign n563_clock = clock;
  assign n563_reset = reset;
  assign n563_valid_up = n562_valid_down; // @[Top.scala 1651:19]
  assign n563_I_0 = n562_O_0; // @[Top.scala 1650:12]
  assign n563_I_1 = n562_O_1; // @[Top.scala 1650:12]
  assign n563_I_2 = n562_O_2; // @[Top.scala 1650:12]
  assign n563_I_3 = n562_O_3; // @[Top.scala 1650:12]
endmodule
