module FIFO(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  input  [7:0] I_3,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2,
  output [7:0] O_3
);
  reg [7:0] _T__0; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_0;
  reg [7:0] _T__1; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_1;
  reg [7:0] _T__2; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_2;
  reg [7:0] _T__3; // @[FIFO.scala 13:26]
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
  _T__0 = _RAND_0[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T__1 = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T__2 = _RAND_2[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T__3 = _RAND_3[7:0];
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
module ShiftT(
  input        clock,
  input  [7:0] I_0,
  output [7:0] O_0
);
  reg [7:0] _T_0; // @[ShiftT.scala 24:82]
  reg [31:0] _RAND_0;
  assign O_0 = _T_0; // @[ShiftT.scala 24:7]
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
  _T_0 = _RAND_0[7:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    _T_0 <= I_0;
  end
endmodule
module ShiftTS(
  input        clock,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  input  [7:0] I_3,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2,
  output [7:0] O_3
);
  wire  ShiftT_clock; // @[ShiftTS.scala 32:26]
  wire [7:0] ShiftT_I_0; // @[ShiftTS.scala 32:26]
  wire [7:0] ShiftT_O_0; // @[ShiftTS.scala 32:26]
  ShiftT ShiftT ( // @[ShiftTS.scala 32:26]
    .clock(ShiftT_clock),
    .I_0(ShiftT_I_0),
    .O_0(ShiftT_O_0)
  );
  assign valid_down = valid_up; // @[ShiftTS.scala 58:14]
  assign O_0 = ShiftT_O_0; // @[ShiftTS.scala 51:36]
  assign O_1 = I_0; // @[ShiftTS.scala 40:36]
  assign O_2 = I_1; // @[ShiftTS.scala 40:36]
  assign O_3 = I_2; // @[ShiftTS.scala 40:36]
  assign ShiftT_clock = clock;
  assign ShiftT_I_0 = I_3; // @[ShiftTS.scala 50:25]
endmodule
module SSeqTupleCreator(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O_0,
  output [7:0] O_1
);
  assign valid_down = valid_up; // @[Tuple.scala 15:14]
  assign O_0 = I0; // @[Tuple.scala 12:32]
  assign O_1 = I1; // @[Tuple.scala 13:32]
endmodule
module Map2S(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I0_1,
  input  [7:0] I0_2,
  input  [7:0] I0_3,
  input  [7:0] I1_0,
  input  [7:0] I1_1,
  input  [7:0] I1_2,
  input  [7:0] I1_3,
  output [7:0] O_0_0,
  output [7:0] O_0_1,
  output [7:0] O_1_0,
  output [7:0] O_1_1,
  output [7:0] O_2_0,
  output [7:0] O_2_1,
  output [7:0] O_3_0,
  output [7:0] O_3_1
);
  wire  fst_op_valid_up; // @[Map2S.scala 9:22]
  wire  fst_op_valid_down; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I0; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I1; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_0; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_1; // @[Map2S.scala 9:22]
  wire  other_ops_0_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_0_valid_down; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_I0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_I1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_O_0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_O_1; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_down; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_I0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_I1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_O_0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_O_1; // @[Map2S.scala 10:86]
  wire  other_ops_2_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_2_valid_down; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_2_I0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_2_I1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_2_O_0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_2_O_1; // @[Map2S.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[Map2S.scala 26:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[Map2S.scala 26:83]
  SSeqTupleCreator fst_op ( // @[Map2S.scala 9:22]
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I0(fst_op_I0),
    .I1(fst_op_I1),
    .O_0(fst_op_O_0),
    .O_1(fst_op_O_1)
  );
  SSeqTupleCreator other_ops_0 ( // @[Map2S.scala 10:86]
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I0(other_ops_0_I0),
    .I1(other_ops_0_I1),
    .O_0(other_ops_0_O_0),
    .O_1(other_ops_0_O_1)
  );
  SSeqTupleCreator other_ops_1 ( // @[Map2S.scala 10:86]
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I0(other_ops_1_I0),
    .I1(other_ops_1_I1),
    .O_0(other_ops_1_O_0),
    .O_1(other_ops_1_O_1)
  );
  SSeqTupleCreator other_ops_2 ( // @[Map2S.scala 10:86]
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I0(other_ops_2_I0),
    .I1(other_ops_2_I1),
    .O_0(other_ops_2_O_0),
    .O_1(other_ops_2_O_1)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[Map2S.scala 26:14]
  assign O_0_0 = fst_op_O_0; // @[Map2S.scala 19:8]
  assign O_0_1 = fst_op_O_1; // @[Map2S.scala 19:8]
  assign O_1_0 = other_ops_0_O_0; // @[Map2S.scala 24:12]
  assign O_1_1 = other_ops_0_O_1; // @[Map2S.scala 24:12]
  assign O_2_0 = other_ops_1_O_0; // @[Map2S.scala 24:12]
  assign O_2_1 = other_ops_1_O_1; // @[Map2S.scala 24:12]
  assign O_3_0 = other_ops_2_O_0; // @[Map2S.scala 24:12]
  assign O_3_1 = other_ops_2_O_1; // @[Map2S.scala 24:12]
  assign fst_op_valid_up = valid_up; // @[Map2S.scala 16:19]
  assign fst_op_I0 = I0_0; // @[Map2S.scala 17:13]
  assign fst_op_I1 = I1_0; // @[Map2S.scala 18:13]
  assign other_ops_0_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_0_I0 = I0_1; // @[Map2S.scala 22:43]
  assign other_ops_0_I1 = I1_1; // @[Map2S.scala 23:43]
  assign other_ops_1_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_1_I0 = I0_2; // @[Map2S.scala 22:43]
  assign other_ops_1_I1 = I1_2; // @[Map2S.scala 23:43]
  assign other_ops_2_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_2_I0 = I0_3; // @[Map2S.scala 22:43]
  assign other_ops_2_I1 = I1_3; // @[Map2S.scala 23:43]
endmodule
module Map2T(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I0_1,
  input  [7:0] I0_2,
  input  [7:0] I0_3,
  input  [7:0] I1_0,
  input  [7:0] I1_1,
  input  [7:0] I1_2,
  input  [7:0] I1_3,
  output [7:0] O_0_0,
  output [7:0] O_0_1,
  output [7:0] O_1_0,
  output [7:0] O_1_1,
  output [7:0] O_2_0,
  output [7:0] O_2_1,
  output [7:0] O_3_0,
  output [7:0] O_3_1
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_2; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_3; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_2; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_3; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1_1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_2_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_2_1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_3_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_3_1; // @[Map2T.scala 8:20]
  Map2S op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0(op_I0_0),
    .I0_1(op_I0_1),
    .I0_2(op_I0_2),
    .I0_3(op_I0_3),
    .I1_0(op_I1_0),
    .I1_1(op_I1_1),
    .I1_2(op_I1_2),
    .I1_3(op_I1_3),
    .O_0_0(op_O_0_0),
    .O_0_1(op_O_0_1),
    .O_1_0(op_O_1_0),
    .O_1_1(op_O_1_1),
    .O_2_0(op_O_2_0),
    .O_2_1(op_O_2_1),
    .O_3_0(op_O_3_0),
    .O_3_1(op_O_3_1)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0_0 = op_O_0_0; // @[Map2T.scala 17:7]
  assign O_0_1 = op_O_0_1; // @[Map2T.scala 17:7]
  assign O_1_0 = op_O_1_0; // @[Map2T.scala 17:7]
  assign O_1_1 = op_O_1_1; // @[Map2T.scala 17:7]
  assign O_2_0 = op_O_2_0; // @[Map2T.scala 17:7]
  assign O_2_1 = op_O_2_1; // @[Map2T.scala 17:7]
  assign O_3_0 = op_O_3_0; // @[Map2T.scala 17:7]
  assign O_3_1 = op_O_3_1; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0 = I0_0; // @[Map2T.scala 15:11]
  assign op_I0_1 = I0_1; // @[Map2T.scala 15:11]
  assign op_I0_2 = I0_2; // @[Map2T.scala 15:11]
  assign op_I0_3 = I0_3; // @[Map2T.scala 15:11]
  assign op_I1_0 = I1_0; // @[Map2T.scala 16:11]
  assign op_I1_1 = I1_1; // @[Map2T.scala 16:11]
  assign op_I1_2 = I1_2; // @[Map2T.scala 16:11]
  assign op_I1_3 = I1_3; // @[Map2T.scala 16:11]
endmodule
module SSeqTupleAppender(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I0_1,
  input  [7:0] I1,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  assign valid_down = valid_up; // @[Tuple.scala 28:14]
  assign O_0 = I0_0; // @[Tuple.scala 24:34]
  assign O_1 = I0_1; // @[Tuple.scala 24:34]
  assign O_2 = I1; // @[Tuple.scala 26:32]
endmodule
module Map2S_1(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0_0,
  input  [7:0] I0_0_1,
  input  [7:0] I0_1_0,
  input  [7:0] I0_1_1,
  input  [7:0] I0_2_0,
  input  [7:0] I0_2_1,
  input  [7:0] I0_3_0,
  input  [7:0] I0_3_1,
  input  [7:0] I1_0,
  input  [7:0] I1_1,
  input  [7:0] I1_2,
  input  [7:0] I1_3,
  output [7:0] O_0_0,
  output [7:0] O_0_1,
  output [7:0] O_0_2,
  output [7:0] O_1_0,
  output [7:0] O_1_1,
  output [7:0] O_1_2,
  output [7:0] O_2_0,
  output [7:0] O_2_1,
  output [7:0] O_2_2,
  output [7:0] O_3_0,
  output [7:0] O_3_1,
  output [7:0] O_3_2
);
  wire  fst_op_valid_up; // @[Map2S.scala 9:22]
  wire  fst_op_valid_down; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I0_0; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I0_1; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I1; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_0; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_1; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_2; // @[Map2S.scala 9:22]
  wire  other_ops_0_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_0_valid_down; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_I0_0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_I0_1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_I1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_O_0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_O_1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_O_2; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_down; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_I0_0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_I0_1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_I1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_O_0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_O_1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_O_2; // @[Map2S.scala 10:86]
  wire  other_ops_2_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_2_valid_down; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_2_I0_0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_2_I0_1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_2_I1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_2_O_0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_2_O_1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_2_O_2; // @[Map2S.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[Map2S.scala 26:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[Map2S.scala 26:83]
  SSeqTupleAppender fst_op ( // @[Map2S.scala 9:22]
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I0_0(fst_op_I0_0),
    .I0_1(fst_op_I0_1),
    .I1(fst_op_I1),
    .O_0(fst_op_O_0),
    .O_1(fst_op_O_1),
    .O_2(fst_op_O_2)
  );
  SSeqTupleAppender other_ops_0 ( // @[Map2S.scala 10:86]
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I0_0(other_ops_0_I0_0),
    .I0_1(other_ops_0_I0_1),
    .I1(other_ops_0_I1),
    .O_0(other_ops_0_O_0),
    .O_1(other_ops_0_O_1),
    .O_2(other_ops_0_O_2)
  );
  SSeqTupleAppender other_ops_1 ( // @[Map2S.scala 10:86]
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I0_0(other_ops_1_I0_0),
    .I0_1(other_ops_1_I0_1),
    .I1(other_ops_1_I1),
    .O_0(other_ops_1_O_0),
    .O_1(other_ops_1_O_1),
    .O_2(other_ops_1_O_2)
  );
  SSeqTupleAppender other_ops_2 ( // @[Map2S.scala 10:86]
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I0_0(other_ops_2_I0_0),
    .I0_1(other_ops_2_I0_1),
    .I1(other_ops_2_I1),
    .O_0(other_ops_2_O_0),
    .O_1(other_ops_2_O_1),
    .O_2(other_ops_2_O_2)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[Map2S.scala 26:14]
  assign O_0_0 = fst_op_O_0; // @[Map2S.scala 19:8]
  assign O_0_1 = fst_op_O_1; // @[Map2S.scala 19:8]
  assign O_0_2 = fst_op_O_2; // @[Map2S.scala 19:8]
  assign O_1_0 = other_ops_0_O_0; // @[Map2S.scala 24:12]
  assign O_1_1 = other_ops_0_O_1; // @[Map2S.scala 24:12]
  assign O_1_2 = other_ops_0_O_2; // @[Map2S.scala 24:12]
  assign O_2_0 = other_ops_1_O_0; // @[Map2S.scala 24:12]
  assign O_2_1 = other_ops_1_O_1; // @[Map2S.scala 24:12]
  assign O_2_2 = other_ops_1_O_2; // @[Map2S.scala 24:12]
  assign O_3_0 = other_ops_2_O_0; // @[Map2S.scala 24:12]
  assign O_3_1 = other_ops_2_O_1; // @[Map2S.scala 24:12]
  assign O_3_2 = other_ops_2_O_2; // @[Map2S.scala 24:12]
  assign fst_op_valid_up = valid_up; // @[Map2S.scala 16:19]
  assign fst_op_I0_0 = I0_0_0; // @[Map2S.scala 17:13]
  assign fst_op_I0_1 = I0_0_1; // @[Map2S.scala 17:13]
  assign fst_op_I1 = I1_0; // @[Map2S.scala 18:13]
  assign other_ops_0_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_0_I0_0 = I0_1_0; // @[Map2S.scala 22:43]
  assign other_ops_0_I0_1 = I0_1_1; // @[Map2S.scala 22:43]
  assign other_ops_0_I1 = I1_1; // @[Map2S.scala 23:43]
  assign other_ops_1_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_1_I0_0 = I0_2_0; // @[Map2S.scala 22:43]
  assign other_ops_1_I0_1 = I0_2_1; // @[Map2S.scala 22:43]
  assign other_ops_1_I1 = I1_2; // @[Map2S.scala 23:43]
  assign other_ops_2_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_2_I0_0 = I0_3_0; // @[Map2S.scala 22:43]
  assign other_ops_2_I0_1 = I0_3_1; // @[Map2S.scala 22:43]
  assign other_ops_2_I1 = I1_3; // @[Map2S.scala 23:43]
endmodule
module Map2T_1(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0_0,
  input  [7:0] I0_0_1,
  input  [7:0] I0_1_0,
  input  [7:0] I0_1_1,
  input  [7:0] I0_2_0,
  input  [7:0] I0_2_1,
  input  [7:0] I0_3_0,
  input  [7:0] I0_3_1,
  input  [7:0] I1_0,
  input  [7:0] I1_1,
  input  [7:0] I1_2,
  input  [7:0] I1_3,
  output [7:0] O_0_0,
  output [7:0] O_0_1,
  output [7:0] O_0_2,
  output [7:0] O_1_0,
  output [7:0] O_1_1,
  output [7:0] O_1_2,
  output [7:0] O_2_0,
  output [7:0] O_2_1,
  output [7:0] O_2_2,
  output [7:0] O_3_0,
  output [7:0] O_3_1,
  output [7:0] O_3_2
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_0_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_1_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_1_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_2_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_2_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_3_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_3_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_2; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_3; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_2; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1_1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1_2; // @[Map2T.scala 8:20]
  wire [7:0] op_O_2_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_2_1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_2_2; // @[Map2T.scala 8:20]
  wire [7:0] op_O_3_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_3_1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_3_2; // @[Map2T.scala 8:20]
  Map2S_1 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0_0(op_I0_0_0),
    .I0_0_1(op_I0_0_1),
    .I0_1_0(op_I0_1_0),
    .I0_1_1(op_I0_1_1),
    .I0_2_0(op_I0_2_0),
    .I0_2_1(op_I0_2_1),
    .I0_3_0(op_I0_3_0),
    .I0_3_1(op_I0_3_1),
    .I1_0(op_I1_0),
    .I1_1(op_I1_1),
    .I1_2(op_I1_2),
    .I1_3(op_I1_3),
    .O_0_0(op_O_0_0),
    .O_0_1(op_O_0_1),
    .O_0_2(op_O_0_2),
    .O_1_0(op_O_1_0),
    .O_1_1(op_O_1_1),
    .O_1_2(op_O_1_2),
    .O_2_0(op_O_2_0),
    .O_2_1(op_O_2_1),
    .O_2_2(op_O_2_2),
    .O_3_0(op_O_3_0),
    .O_3_1(op_O_3_1),
    .O_3_2(op_O_3_2)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0_0 = op_O_0_0; // @[Map2T.scala 17:7]
  assign O_0_1 = op_O_0_1; // @[Map2T.scala 17:7]
  assign O_0_2 = op_O_0_2; // @[Map2T.scala 17:7]
  assign O_1_0 = op_O_1_0; // @[Map2T.scala 17:7]
  assign O_1_1 = op_O_1_1; // @[Map2T.scala 17:7]
  assign O_1_2 = op_O_1_2; // @[Map2T.scala 17:7]
  assign O_2_0 = op_O_2_0; // @[Map2T.scala 17:7]
  assign O_2_1 = op_O_2_1; // @[Map2T.scala 17:7]
  assign O_2_2 = op_O_2_2; // @[Map2T.scala 17:7]
  assign O_3_0 = op_O_3_0; // @[Map2T.scala 17:7]
  assign O_3_1 = op_O_3_1; // @[Map2T.scala 17:7]
  assign O_3_2 = op_O_3_2; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0_0 = I0_0_0; // @[Map2T.scala 15:11]
  assign op_I0_0_1 = I0_0_1; // @[Map2T.scala 15:11]
  assign op_I0_1_0 = I0_1_0; // @[Map2T.scala 15:11]
  assign op_I0_1_1 = I0_1_1; // @[Map2T.scala 15:11]
  assign op_I0_2_0 = I0_2_0; // @[Map2T.scala 15:11]
  assign op_I0_2_1 = I0_2_1; // @[Map2T.scala 15:11]
  assign op_I0_3_0 = I0_3_0; // @[Map2T.scala 15:11]
  assign op_I0_3_1 = I0_3_1; // @[Map2T.scala 15:11]
  assign op_I1_0 = I1_0; // @[Map2T.scala 16:11]
  assign op_I1_1 = I1_1; // @[Map2T.scala 16:11]
  assign op_I1_2 = I1_2; // @[Map2T.scala 16:11]
  assign op_I1_3 = I1_3; // @[Map2T.scala 16:11]
endmodule
module PartitionS(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_0_1,
  input  [7:0] I_0_2,
  input  [7:0] I_1_0,
  input  [7:0] I_1_1,
  input  [7:0] I_1_2,
  input  [7:0] I_2_0,
  input  [7:0] I_2_1,
  input  [7:0] I_2_2,
  input  [7:0] I_3_0,
  input  [7:0] I_3_1,
  input  [7:0] I_3_2,
  output [7:0] O_0_0_0,
  output [7:0] O_0_0_1,
  output [7:0] O_0_0_2,
  output [7:0] O_1_0_0,
  output [7:0] O_1_0_1,
  output [7:0] O_1_0_2,
  output [7:0] O_2_0_0,
  output [7:0] O_2_0_1,
  output [7:0] O_2_0_2,
  output [7:0] O_3_0_0,
  output [7:0] O_3_0_1,
  output [7:0] O_3_0_2
);
  assign valid_down = valid_up; // @[Partition.scala 18:14]
  assign O_0_0_0 = I_0_0; // @[Partition.scala 15:39]
  assign O_0_0_1 = I_0_1; // @[Partition.scala 15:39]
  assign O_0_0_2 = I_0_2; // @[Partition.scala 15:39]
  assign O_1_0_0 = I_1_0; // @[Partition.scala 15:39]
  assign O_1_0_1 = I_1_1; // @[Partition.scala 15:39]
  assign O_1_0_2 = I_1_2; // @[Partition.scala 15:39]
  assign O_2_0_0 = I_2_0; // @[Partition.scala 15:39]
  assign O_2_0_1 = I_2_1; // @[Partition.scala 15:39]
  assign O_2_0_2 = I_2_2; // @[Partition.scala 15:39]
  assign O_3_0_0 = I_3_0; // @[Partition.scala 15:39]
  assign O_3_0_1 = I_3_1; // @[Partition.scala 15:39]
  assign O_3_0_2 = I_3_2; // @[Partition.scala 15:39]
endmodule
module MapT(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_0_1,
  input  [7:0] I_0_2,
  input  [7:0] I_1_0,
  input  [7:0] I_1_1,
  input  [7:0] I_1_2,
  input  [7:0] I_2_0,
  input  [7:0] I_2_1,
  input  [7:0] I_2_2,
  input  [7:0] I_3_0,
  input  [7:0] I_3_1,
  input  [7:0] I_3_2,
  output [7:0] O_0_0_0,
  output [7:0] O_0_0_1,
  output [7:0] O_0_0_2,
  output [7:0] O_1_0_0,
  output [7:0] O_1_0_1,
  output [7:0] O_1_0_2,
  output [7:0] O_2_0_0,
  output [7:0] O_2_0_1,
  output [7:0] O_2_0_2,
  output [7:0] O_3_0_0,
  output [7:0] O_3_0_1,
  output [7:0] O_3_0_2
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_2; // @[MapT.scala 8:20]
  wire [7:0] op_I_1_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_1_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_1_2; // @[MapT.scala 8:20]
  wire [7:0] op_I_2_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_2_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_2_2; // @[MapT.scala 8:20]
  wire [7:0] op_I_3_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_3_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_3_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_0_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_0_0_1; // @[MapT.scala 8:20]
  wire [7:0] op_O_0_0_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_1_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_1_0_1; // @[MapT.scala 8:20]
  wire [7:0] op_O_1_0_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_2_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_2_0_1; // @[MapT.scala 8:20]
  wire [7:0] op_O_2_0_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_3_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_3_0_1; // @[MapT.scala 8:20]
  wire [7:0] op_O_3_0_2; // @[MapT.scala 8:20]
  PartitionS op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_0(op_I_0_0),
    .I_0_1(op_I_0_1),
    .I_0_2(op_I_0_2),
    .I_1_0(op_I_1_0),
    .I_1_1(op_I_1_1),
    .I_1_2(op_I_1_2),
    .I_2_0(op_I_2_0),
    .I_2_1(op_I_2_1),
    .I_2_2(op_I_2_2),
    .I_3_0(op_I_3_0),
    .I_3_1(op_I_3_1),
    .I_3_2(op_I_3_2),
    .O_0_0_0(op_O_0_0_0),
    .O_0_0_1(op_O_0_0_1),
    .O_0_0_2(op_O_0_0_2),
    .O_1_0_0(op_O_1_0_0),
    .O_1_0_1(op_O_1_0_1),
    .O_1_0_2(op_O_1_0_2),
    .O_2_0_0(op_O_2_0_0),
    .O_2_0_1(op_O_2_0_1),
    .O_2_0_2(op_O_2_0_2),
    .O_3_0_0(op_O_3_0_0),
    .O_3_0_1(op_O_3_0_1),
    .O_3_0_2(op_O_3_0_2)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_0_0 = op_O_0_0_0; // @[MapT.scala 15:7]
  assign O_0_0_1 = op_O_0_0_1; // @[MapT.scala 15:7]
  assign O_0_0_2 = op_O_0_0_2; // @[MapT.scala 15:7]
  assign O_1_0_0 = op_O_1_0_0; // @[MapT.scala 15:7]
  assign O_1_0_1 = op_O_1_0_1; // @[MapT.scala 15:7]
  assign O_1_0_2 = op_O_1_0_2; // @[MapT.scala 15:7]
  assign O_2_0_0 = op_O_2_0_0; // @[MapT.scala 15:7]
  assign O_2_0_1 = op_O_2_0_1; // @[MapT.scala 15:7]
  assign O_2_0_2 = op_O_2_0_2; // @[MapT.scala 15:7]
  assign O_3_0_0 = op_O_3_0_0; // @[MapT.scala 15:7]
  assign O_3_0_1 = op_O_3_0_1; // @[MapT.scala 15:7]
  assign O_3_0_2 = op_O_3_0_2; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_0 = I_0_0; // @[MapT.scala 14:10]
  assign op_I_0_1 = I_0_1; // @[MapT.scala 14:10]
  assign op_I_0_2 = I_0_2; // @[MapT.scala 14:10]
  assign op_I_1_0 = I_1_0; // @[MapT.scala 14:10]
  assign op_I_1_1 = I_1_1; // @[MapT.scala 14:10]
  assign op_I_1_2 = I_1_2; // @[MapT.scala 14:10]
  assign op_I_2_0 = I_2_0; // @[MapT.scala 14:10]
  assign op_I_2_1 = I_2_1; // @[MapT.scala 14:10]
  assign op_I_2_2 = I_2_2; // @[MapT.scala 14:10]
  assign op_I_3_0 = I_3_0; // @[MapT.scala 14:10]
  assign op_I_3_1 = I_3_1; // @[MapT.scala 14:10]
  assign op_I_3_2 = I_3_2; // @[MapT.scala 14:10]
endmodule
module SSeqTupleToSSeq(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  assign valid_down = valid_up; // @[Tuple.scala 42:14]
  assign O_0 = I_0; // @[Tuple.scala 41:5]
  assign O_1 = I_1; // @[Tuple.scala 41:5]
  assign O_2 = I_2; // @[Tuple.scala 41:5]
endmodule
module Remove1S(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_0_1,
  input  [7:0] I_0_2,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  op_inst_valid_up; // @[Remove1S.scala 9:23]
  wire  op_inst_valid_down; // @[Remove1S.scala 9:23]
  wire [7:0] op_inst_I_0; // @[Remove1S.scala 9:23]
  wire [7:0] op_inst_I_1; // @[Remove1S.scala 9:23]
  wire [7:0] op_inst_I_2; // @[Remove1S.scala 9:23]
  wire [7:0] op_inst_O_0; // @[Remove1S.scala 9:23]
  wire [7:0] op_inst_O_1; // @[Remove1S.scala 9:23]
  wire [7:0] op_inst_O_2; // @[Remove1S.scala 9:23]
  SSeqTupleToSSeq op_inst ( // @[Remove1S.scala 9:23]
    .valid_up(op_inst_valid_up),
    .valid_down(op_inst_valid_down),
    .I_0(op_inst_I_0),
    .I_1(op_inst_I_1),
    .I_2(op_inst_I_2),
    .O_0(op_inst_O_0),
    .O_1(op_inst_O_1),
    .O_2(op_inst_O_2)
  );
  assign valid_down = op_inst_valid_down; // @[Remove1S.scala 16:14]
  assign O_0 = op_inst_O_0; // @[Remove1S.scala 14:5]
  assign O_1 = op_inst_O_1; // @[Remove1S.scala 14:5]
  assign O_2 = op_inst_O_2; // @[Remove1S.scala 14:5]
  assign op_inst_valid_up = valid_up; // @[Remove1S.scala 15:20]
  assign op_inst_I_0 = I_0_0; // @[Remove1S.scala 13:13]
  assign op_inst_I_1 = I_0_1; // @[Remove1S.scala 13:13]
  assign op_inst_I_2 = I_0_2; // @[Remove1S.scala 13:13]
endmodule
module MapS(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0_0,
  input  [7:0] I_0_0_1,
  input  [7:0] I_0_0_2,
  input  [7:0] I_1_0_0,
  input  [7:0] I_1_0_1,
  input  [7:0] I_1_0_2,
  input  [7:0] I_2_0_0,
  input  [7:0] I_2_0_1,
  input  [7:0] I_2_0_2,
  input  [7:0] I_3_0_0,
  input  [7:0] I_3_0_1,
  input  [7:0] I_3_0_2,
  output [7:0] O_0_0,
  output [7:0] O_0_1,
  output [7:0] O_0_2,
  output [7:0] O_1_0,
  output [7:0] O_1_1,
  output [7:0] O_1_2,
  output [7:0] O_2_0,
  output [7:0] O_2_1,
  output [7:0] O_2_2,
  output [7:0] O_3_0,
  output [7:0] O_3_1,
  output [7:0] O_3_2
);
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_0_0; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_0_1; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_0_2; // @[MapS.scala 9:22]
  wire [7:0] fst_op_O_0; // @[MapS.scala 9:22]
  wire [7:0] fst_op_O_1; // @[MapS.scala 9:22]
  wire [7:0] fst_op_O_2; // @[MapS.scala 9:22]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_I_0_0; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_I_0_1; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_I_0_2; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_O_0; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_O_1; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_O_2; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_I_0_0; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_I_0_1; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_I_0_2; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_O_0; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_O_1; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_O_2; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [7:0] other_ops_2_I_0_0; // @[MapS.scala 10:86]
  wire [7:0] other_ops_2_I_0_1; // @[MapS.scala 10:86]
  wire [7:0] other_ops_2_I_0_2; // @[MapS.scala 10:86]
  wire [7:0] other_ops_2_O_0; // @[MapS.scala 10:86]
  wire [7:0] other_ops_2_O_1; // @[MapS.scala 10:86]
  wire [7:0] other_ops_2_O_2; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Remove1S fst_op ( // @[MapS.scala 9:22]
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_0_0(fst_op_I_0_0),
    .I_0_1(fst_op_I_0_1),
    .I_0_2(fst_op_I_0_2),
    .O_0(fst_op_O_0),
    .O_1(fst_op_O_1),
    .O_2(fst_op_O_2)
  );
  Remove1S other_ops_0 ( // @[MapS.scala 10:86]
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_0_0(other_ops_0_I_0_0),
    .I_0_1(other_ops_0_I_0_1),
    .I_0_2(other_ops_0_I_0_2),
    .O_0(other_ops_0_O_0),
    .O_1(other_ops_0_O_1),
    .O_2(other_ops_0_O_2)
  );
  Remove1S other_ops_1 ( // @[MapS.scala 10:86]
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_0_0(other_ops_1_I_0_0),
    .I_0_1(other_ops_1_I_0_1),
    .I_0_2(other_ops_1_I_0_2),
    .O_0(other_ops_1_O_0),
    .O_1(other_ops_1_O_1),
    .O_2(other_ops_1_O_2)
  );
  Remove1S other_ops_2 ( // @[MapS.scala 10:86]
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_0_0(other_ops_2_I_0_0),
    .I_0_1(other_ops_2_I_0_1),
    .I_0_2(other_ops_2_I_0_2),
    .O_0(other_ops_2_O_0),
    .O_1(other_ops_2_O_1),
    .O_2(other_ops_2_O_2)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_0 = fst_op_O_0; // @[MapS.scala 17:8]
  assign O_0_1 = fst_op_O_1; // @[MapS.scala 17:8]
  assign O_0_2 = fst_op_O_2; // @[MapS.scala 17:8]
  assign O_1_0 = other_ops_0_O_0; // @[MapS.scala 21:12]
  assign O_1_1 = other_ops_0_O_1; // @[MapS.scala 21:12]
  assign O_1_2 = other_ops_0_O_2; // @[MapS.scala 21:12]
  assign O_2_0 = other_ops_1_O_0; // @[MapS.scala 21:12]
  assign O_2_1 = other_ops_1_O_1; // @[MapS.scala 21:12]
  assign O_2_2 = other_ops_1_O_2; // @[MapS.scala 21:12]
  assign O_3_0 = other_ops_2_O_0; // @[MapS.scala 21:12]
  assign O_3_1 = other_ops_2_O_1; // @[MapS.scala 21:12]
  assign O_3_2 = other_ops_2_O_2; // @[MapS.scala 21:12]
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_0_0 = I_0_0_0; // @[MapS.scala 16:12]
  assign fst_op_I_0_1 = I_0_0_1; // @[MapS.scala 16:12]
  assign fst_op_I_0_2 = I_0_0_2; // @[MapS.scala 16:12]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_0_0 = I_1_0_0; // @[MapS.scala 20:41]
  assign other_ops_0_I_0_1 = I_1_0_1; // @[MapS.scala 20:41]
  assign other_ops_0_I_0_2 = I_1_0_2; // @[MapS.scala 20:41]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_0_0 = I_2_0_0; // @[MapS.scala 20:41]
  assign other_ops_1_I_0_1 = I_2_0_1; // @[MapS.scala 20:41]
  assign other_ops_1_I_0_2 = I_2_0_2; // @[MapS.scala 20:41]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_0_0 = I_3_0_0; // @[MapS.scala 20:41]
  assign other_ops_2_I_0_1 = I_3_0_1; // @[MapS.scala 20:41]
  assign other_ops_2_I_0_2 = I_3_0_2; // @[MapS.scala 20:41]
endmodule
module MapT_1(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0_0,
  input  [7:0] I_0_0_1,
  input  [7:0] I_0_0_2,
  input  [7:0] I_1_0_0,
  input  [7:0] I_1_0_1,
  input  [7:0] I_1_0_2,
  input  [7:0] I_2_0_0,
  input  [7:0] I_2_0_1,
  input  [7:0] I_2_0_2,
  input  [7:0] I_3_0_0,
  input  [7:0] I_3_0_1,
  input  [7:0] I_3_0_2,
  output [7:0] O_0_0,
  output [7:0] O_0_1,
  output [7:0] O_0_2,
  output [7:0] O_1_0,
  output [7:0] O_1_1,
  output [7:0] O_1_2,
  output [7:0] O_2_0,
  output [7:0] O_2_1,
  output [7:0] O_2_2,
  output [7:0] O_3_0,
  output [7:0] O_3_1,
  output [7:0] O_3_2
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_0_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_0_2; // @[MapT.scala 8:20]
  wire [7:0] op_I_1_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_1_0_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_1_0_2; // @[MapT.scala 8:20]
  wire [7:0] op_I_2_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_2_0_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_2_0_2; // @[MapT.scala 8:20]
  wire [7:0] op_I_3_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_3_0_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_3_0_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_0_1; // @[MapT.scala 8:20]
  wire [7:0] op_O_0_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_1_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_1_1; // @[MapT.scala 8:20]
  wire [7:0] op_O_1_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_2_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_2_1; // @[MapT.scala 8:20]
  wire [7:0] op_O_2_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_3_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_3_1; // @[MapT.scala 8:20]
  wire [7:0] op_O_3_2; // @[MapT.scala 8:20]
  MapS op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_0_0(op_I_0_0_0),
    .I_0_0_1(op_I_0_0_1),
    .I_0_0_2(op_I_0_0_2),
    .I_1_0_0(op_I_1_0_0),
    .I_1_0_1(op_I_1_0_1),
    .I_1_0_2(op_I_1_0_2),
    .I_2_0_0(op_I_2_0_0),
    .I_2_0_1(op_I_2_0_1),
    .I_2_0_2(op_I_2_0_2),
    .I_3_0_0(op_I_3_0_0),
    .I_3_0_1(op_I_3_0_1),
    .I_3_0_2(op_I_3_0_2),
    .O_0_0(op_O_0_0),
    .O_0_1(op_O_0_1),
    .O_0_2(op_O_0_2),
    .O_1_0(op_O_1_0),
    .O_1_1(op_O_1_1),
    .O_1_2(op_O_1_2),
    .O_2_0(op_O_2_0),
    .O_2_1(op_O_2_1),
    .O_2_2(op_O_2_2),
    .O_3_0(op_O_3_0),
    .O_3_1(op_O_3_1),
    .O_3_2(op_O_3_2)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_0 = op_O_0_0; // @[MapT.scala 15:7]
  assign O_0_1 = op_O_0_1; // @[MapT.scala 15:7]
  assign O_0_2 = op_O_0_2; // @[MapT.scala 15:7]
  assign O_1_0 = op_O_1_0; // @[MapT.scala 15:7]
  assign O_1_1 = op_O_1_1; // @[MapT.scala 15:7]
  assign O_1_2 = op_O_1_2; // @[MapT.scala 15:7]
  assign O_2_0 = op_O_2_0; // @[MapT.scala 15:7]
  assign O_2_1 = op_O_2_1; // @[MapT.scala 15:7]
  assign O_2_2 = op_O_2_2; // @[MapT.scala 15:7]
  assign O_3_0 = op_O_3_0; // @[MapT.scala 15:7]
  assign O_3_1 = op_O_3_1; // @[MapT.scala 15:7]
  assign O_3_2 = op_O_3_2; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_0_0 = I_0_0_0; // @[MapT.scala 14:10]
  assign op_I_0_0_1 = I_0_0_1; // @[MapT.scala 14:10]
  assign op_I_0_0_2 = I_0_0_2; // @[MapT.scala 14:10]
  assign op_I_1_0_0 = I_1_0_0; // @[MapT.scala 14:10]
  assign op_I_1_0_1 = I_1_0_1; // @[MapT.scala 14:10]
  assign op_I_1_0_2 = I_1_0_2; // @[MapT.scala 14:10]
  assign op_I_2_0_0 = I_2_0_0; // @[MapT.scala 14:10]
  assign op_I_2_0_1 = I_2_0_1; // @[MapT.scala 14:10]
  assign op_I_2_0_2 = I_2_0_2; // @[MapT.scala 14:10]
  assign op_I_3_0_0 = I_3_0_0; // @[MapT.scala 14:10]
  assign op_I_3_0_1 = I_3_0_1; // @[MapT.scala 14:10]
  assign op_I_3_0_2 = I_3_0_2; // @[MapT.scala 14:10]
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
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O_t0b,
  output [7:0] O_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b = I0; // @[Tuple.scala 49:9]
  assign O_t1b = I1; // @[Tuple.scala 50:9]
endmodule
module Mul(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  wire [7:0] BlackBoxMulInt8_I0; // @[Arithmetic.scala 172:11]
  wire [7:0] BlackBoxMulInt8_I1; // @[Arithmetic.scala 172:11]
  wire [15:0] BlackBoxMulInt8_O; // @[Arithmetic.scala 172:11]
  wire  BlackBoxMulInt8_clock; // @[Arithmetic.scala 172:11]
  wire [7:0] BlackBoxMulInt8_1_I0; // @[Arithmetic.scala 173:27]
  wire [7:0] BlackBoxMulInt8_1_I1; // @[Arithmetic.scala 173:27]
  wire [15:0] BlackBoxMulInt8_1_O; // @[Arithmetic.scala 173:27]
  wire  BlackBoxMulInt8_1_clock; // @[Arithmetic.scala 173:27]
  reg  _T_2; // @[Arithmetic.scala 217:42]
  reg [31:0] _RAND_0;
  reg  _T_3; // @[Arithmetic.scala 217:34]
  reg [31:0] _RAND_1;
  reg  _T_4; // @[Arithmetic.scala 217:26]
  reg [31:0] _RAND_2;
  BlackBoxMulInt8 BlackBoxMulInt8 ( // @[Arithmetic.scala 172:11]
    .I0(BlackBoxMulInt8_I0),
    .I1(BlackBoxMulInt8_I1),
    .O(BlackBoxMulInt8_O),
    .clock(BlackBoxMulInt8_clock)
  );
  BlackBoxMulInt8 BlackBoxMulInt8_1 ( // @[Arithmetic.scala 173:27]
    .I0(BlackBoxMulInt8_1_I0),
    .I1(BlackBoxMulInt8_1_I1),
    .O(BlackBoxMulInt8_1_O),
    .clock(BlackBoxMulInt8_1_clock)
  );
  assign valid_down = _T_4; // @[Arithmetic.scala 217:16]
  assign O = BlackBoxMulInt8_1_O[7:0]; // @[Arithmetic.scala 176:7]
  assign BlackBoxMulInt8_I0 = 8'sh0;
  assign BlackBoxMulInt8_I1 = 8'sh0;
  assign BlackBoxMulInt8_clock = 1'h0;
  assign BlackBoxMulInt8_1_I0 = I_t0b; // @[Arithmetic.scala 174:21]
  assign BlackBoxMulInt8_1_I1 = I_t1b; // @[Arithmetic.scala 175:21]
  assign BlackBoxMulInt8_1_clock = clock; // @[Arithmetic.scala 177:24]
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
  _T_2 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_3 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_4 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      _T_2 <= 1'h0;
    end else begin
      _T_2 <= valid_up;
    end
    if (reset) begin
      _T_3 <= 1'h0;
    end else begin
      _T_3 <= _T_2;
    end
    if (reset) begin
      _T_4 <= 1'h0;
    end else begin
      _T_4 <= _T_3;
    end
  end
endmodule
module Module_0(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O
);
  wire  n35_valid_up; // @[Top.scala 18:21]
  wire  n35_valid_down; // @[Top.scala 18:21]
  wire [7:0] n35_I0; // @[Top.scala 18:21]
  wire [7:0] n35_I1; // @[Top.scala 18:21]
  wire [7:0] n35_O_t0b; // @[Top.scala 18:21]
  wire [7:0] n35_O_t1b; // @[Top.scala 18:21]
  wire  n36_clock; // @[Top.scala 22:21]
  wire  n36_reset; // @[Top.scala 22:21]
  wire  n36_valid_up; // @[Top.scala 22:21]
  wire  n36_valid_down; // @[Top.scala 22:21]
  wire [7:0] n36_I_t0b; // @[Top.scala 22:21]
  wire [7:0] n36_I_t1b; // @[Top.scala 22:21]
  wire [7:0] n36_O; // @[Top.scala 22:21]
  AtomTuple n35 ( // @[Top.scala 18:21]
    .valid_up(n35_valid_up),
    .valid_down(n35_valid_down),
    .I0(n35_I0),
    .I1(n35_I1),
    .O_t0b(n35_O_t0b),
    .O_t1b(n35_O_t1b)
  );
  Mul n36 ( // @[Top.scala 22:21]
    .clock(n36_clock),
    .reset(n36_reset),
    .valid_up(n36_valid_up),
    .valid_down(n36_valid_down),
    .I_t0b(n36_I_t0b),
    .I_t1b(n36_I_t1b),
    .O(n36_O)
  );
  assign valid_down = n36_valid_down; // @[Top.scala 26:16]
  assign O = n36_O; // @[Top.scala 25:7]
  assign n35_valid_up = valid_up; // @[Top.scala 21:18]
  assign n35_I0 = I0; // @[Top.scala 19:12]
  assign n35_I1 = I1; // @[Top.scala 20:12]
  assign n36_clock = clock;
  assign n36_reset = reset;
  assign n36_valid_up = n35_valid_down; // @[Top.scala 24:18]
  assign n36_I_t0b = n35_O_t0b; // @[Top.scala 23:11]
  assign n36_I_t1b = n35_O_t1b; // @[Top.scala 23:11]
endmodule
module Map2S_2(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I0_1,
  input  [7:0] I0_2,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  fst_op_clock; // @[Map2S.scala 9:22]
  wire  fst_op_reset; // @[Map2S.scala 9:22]
  wire  fst_op_valid_up; // @[Map2S.scala 9:22]
  wire  fst_op_valid_down; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I0; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I1; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O; // @[Map2S.scala 9:22]
  wire  other_ops_0_clock; // @[Map2S.scala 10:86]
  wire  other_ops_0_reset; // @[Map2S.scala 10:86]
  wire  other_ops_0_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_0_valid_down; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_I0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_I1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_O; // @[Map2S.scala 10:86]
  wire  other_ops_1_clock; // @[Map2S.scala 10:86]
  wire  other_ops_1_reset; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_down; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_I0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_I1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_O; // @[Map2S.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[Map2S.scala 26:83]
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
  assign valid_down = _T & other_ops_1_valid_down; // @[Map2S.scala 26:14]
  assign O_0 = fst_op_O; // @[Map2S.scala 19:8]
  assign O_1 = other_ops_0_O; // @[Map2S.scala 24:12]
  assign O_2 = other_ops_1_O; // @[Map2S.scala 24:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[Map2S.scala 16:19]
  assign fst_op_I0 = I0_0; // @[Map2S.scala 17:13]
  assign fst_op_I1 = -8'sh1; // @[Map2S.scala 18:13]
  assign other_ops_0_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_0_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_0_I0 = I0_1; // @[Map2S.scala 22:43]
  assign other_ops_0_I1 = 8'sh0; // @[Map2S.scala 23:43]
  assign other_ops_1_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_1_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_1_I0 = I0_2; // @[Map2S.scala 22:43]
  assign other_ops_1_I1 = 8'sh1; // @[Map2S.scala 23:43]
endmodule
module AddNoValid(
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  assign O = $signed(I_t0b) + $signed(I_t1b); // @[Arithmetic.scala 119:7]
endmodule
module ReduceS(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O_0
);
  wire [7:0] AddNoValid_I_t0b; // @[ReduceS.scala 20:43]
  wire [7:0] AddNoValid_I_t1b; // @[ReduceS.scala 20:43]
  wire [7:0] AddNoValid_O; // @[ReduceS.scala 20:43]
  wire [7:0] AddNoValid_1_I_t0b; // @[ReduceS.scala 20:43]
  wire [7:0] AddNoValid_1_I_t1b; // @[ReduceS.scala 20:43]
  wire [7:0] AddNoValid_1_O; // @[ReduceS.scala 20:43]
  reg [7:0] _T; // @[ReduceS.scala 27:24]
  reg [31:0] _RAND_0;
  reg [7:0] _T_1; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_1;
  reg [7:0] _T_2; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_2;
  reg [7:0] _T_3; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_3;
  reg  _T_4; // @[ReduceS.scala 47:32]
  reg [31:0] _RAND_4;
  reg  _T_5; // @[ReduceS.scala 47:24]
  reg [31:0] _RAND_5;
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
  assign valid_down = _T_5; // @[ReduceS.scala 47:14]
  assign O_0 = _T; // @[ReduceS.scala 27:14]
  assign AddNoValid_I_t0b = _T_2; // @[ReduceS.scala 43:18]
  assign AddNoValid_I_t1b = AddNoValid_1_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_1_I_t0b = _T_1; // @[ReduceS.scala 43:18]
  assign AddNoValid_1_I_t1b = _T_3; // @[ReduceS.scala 43:18]
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
  _T_1 = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_2 = _RAND_2[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_3 = _RAND_3[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_4 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_5 = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    _T <= AddNoValid_O;
    _T_1 <= I_0;
    _T_2 <= I_1;
    _T_3 <= I_2;
    if (reset) begin
      _T_4 <= 1'h0;
    end else begin
      _T_4 <= valid_up;
    end
    _T_5 <= _T_4;
  end
endmodule
module Module_1(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O_0
);
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n32_clock; // @[Top.scala 33:21]
  wire  n32_reset; // @[Top.scala 33:21]
  wire  n32_valid_up; // @[Top.scala 33:21]
  wire  n32_valid_down; // @[Top.scala 33:21]
  wire [7:0] n32_I0_0; // @[Top.scala 33:21]
  wire [7:0] n32_I0_1; // @[Top.scala 33:21]
  wire [7:0] n32_I0_2; // @[Top.scala 33:21]
  wire [7:0] n32_O_0; // @[Top.scala 33:21]
  wire [7:0] n32_O_1; // @[Top.scala 33:21]
  wire [7:0] n32_O_2; // @[Top.scala 33:21]
  wire  n39_clock; // @[Top.scala 37:21]
  wire  n39_reset; // @[Top.scala 37:21]
  wire  n39_valid_up; // @[Top.scala 37:21]
  wire  n39_valid_down; // @[Top.scala 37:21]
  wire [7:0] n39_I_0; // @[Top.scala 37:21]
  wire [7:0] n39_I_1; // @[Top.scala 37:21]
  wire [7:0] n39_I_2; // @[Top.scala 37:21]
  wire [7:0] n39_O_0; // @[Top.scala 37:21]
  InitialDelayCounter InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Map2S_2 n32 ( // @[Top.scala 33:21]
    .clock(n32_clock),
    .reset(n32_reset),
    .valid_up(n32_valid_up),
    .valid_down(n32_valid_down),
    .I0_0(n32_I0_0),
    .I0_1(n32_I0_1),
    .I0_2(n32_I0_2),
    .O_0(n32_O_0),
    .O_1(n32_O_1),
    .O_2(n32_O_2)
  );
  ReduceS n39 ( // @[Top.scala 37:21]
    .clock(n39_clock),
    .reset(n39_reset),
    .valid_up(n39_valid_up),
    .valid_down(n39_valid_down),
    .I_0(n39_I_0),
    .I_1(n39_I_1),
    .I_2(n39_I_2),
    .O_0(n39_O_0)
  );
  assign valid_down = n39_valid_down; // @[Top.scala 41:16]
  assign O_0 = n39_O_0; // @[Top.scala 40:7]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n32_clock = clock;
  assign n32_reset = reset;
  assign n32_valid_up = valid_up & InitialDelayCounter_valid_down; // @[Top.scala 36:18]
  assign n32_I0_0 = I_0; // @[Top.scala 34:12]
  assign n32_I0_1 = I_1; // @[Top.scala 34:12]
  assign n32_I0_2 = I_2; // @[Top.scala 34:12]
  assign n39_clock = clock;
  assign n39_reset = reset;
  assign n39_valid_up = n32_valid_down; // @[Top.scala 39:18]
  assign n39_I_0 = n32_O_0; // @[Top.scala 38:11]
  assign n39_I_1 = n32_O_1; // @[Top.scala 38:11]
  assign n39_I_2 = n32_O_2; // @[Top.scala 38:11]
endmodule
module MapS_1(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_0_1,
  input  [7:0] I_0_2,
  input  [7:0] I_1_0,
  input  [7:0] I_1_1,
  input  [7:0] I_1_2,
  input  [7:0] I_2_0,
  input  [7:0] I_2_1,
  input  [7:0] I_2_2,
  input  [7:0] I_3_0,
  input  [7:0] I_3_1,
  input  [7:0] I_3_2,
  output [7:0] O_0_0,
  output [7:0] O_1_0,
  output [7:0] O_2_0,
  output [7:0] O_3_0
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_0; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_1; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_2; // @[MapS.scala 9:22]
  wire [7:0] fst_op_O_0; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_I_0; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_I_1; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_I_2; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_O_0; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_I_0; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_I_1; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_I_2; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_O_0; // @[MapS.scala 10:86]
  wire  other_ops_2_clock; // @[MapS.scala 10:86]
  wire  other_ops_2_reset; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_2_valid_down; // @[MapS.scala 10:86]
  wire [7:0] other_ops_2_I_0; // @[MapS.scala 10:86]
  wire [7:0] other_ops_2_I_1; // @[MapS.scala 10:86]
  wire [7:0] other_ops_2_I_2; // @[MapS.scala 10:86]
  wire [7:0] other_ops_2_O_0; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[MapS.scala 23:83]
  Module_1 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_0(fst_op_I_0),
    .I_1(fst_op_I_1),
    .I_2(fst_op_I_2),
    .O_0(fst_op_O_0)
  );
  Module_1 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_0(other_ops_0_I_0),
    .I_1(other_ops_0_I_1),
    .I_2(other_ops_0_I_2),
    .O_0(other_ops_0_O_0)
  );
  Module_1 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_0(other_ops_1_I_0),
    .I_1(other_ops_1_I_1),
    .I_2(other_ops_1_I_2),
    .O_0(other_ops_1_O_0)
  );
  Module_1 other_ops_2 ( // @[MapS.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I_0(other_ops_2_I_0),
    .I_1(other_ops_2_I_1),
    .I_2(other_ops_2_I_2),
    .O_0(other_ops_2_O_0)
  );
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[MapS.scala 23:14]
  assign O_0_0 = fst_op_O_0; // @[MapS.scala 17:8]
  assign O_1_0 = other_ops_0_O_0; // @[MapS.scala 21:12]
  assign O_2_0 = other_ops_1_O_0; // @[MapS.scala 21:12]
  assign O_3_0 = other_ops_2_O_0; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_0 = I_0_0; // @[MapS.scala 16:12]
  assign fst_op_I_1 = I_0_1; // @[MapS.scala 16:12]
  assign fst_op_I_2 = I_0_2; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_0 = I_1_0; // @[MapS.scala 20:41]
  assign other_ops_0_I_1 = I_1_1; // @[MapS.scala 20:41]
  assign other_ops_0_I_2 = I_1_2; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_0 = I_2_0; // @[MapS.scala 20:41]
  assign other_ops_1_I_1 = I_2_1; // @[MapS.scala 20:41]
  assign other_ops_1_I_2 = I_2_2; // @[MapS.scala 20:41]
  assign other_ops_2_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_2_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_2_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_2_I_0 = I_3_0; // @[MapS.scala 20:41]
  assign other_ops_2_I_1 = I_3_1; // @[MapS.scala 20:41]
  assign other_ops_2_I_2 = I_3_2; // @[MapS.scala 20:41]
endmodule
module MapT_2(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_0_1,
  input  [7:0] I_0_2,
  input  [7:0] I_1_0,
  input  [7:0] I_1_1,
  input  [7:0] I_1_2,
  input  [7:0] I_2_0,
  input  [7:0] I_2_1,
  input  [7:0] I_2_2,
  input  [7:0] I_3_0,
  input  [7:0] I_3_1,
  input  [7:0] I_3_2,
  output [7:0] O_0_0,
  output [7:0] O_1_0,
  output [7:0] O_2_0,
  output [7:0] O_3_0
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_2; // @[MapT.scala 8:20]
  wire [7:0] op_I_1_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_1_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_1_2; // @[MapT.scala 8:20]
  wire [7:0] op_I_2_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_2_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_2_2; // @[MapT.scala 8:20]
  wire [7:0] op_I_3_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_3_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_3_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_1_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_2_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_3_0; // @[MapT.scala 8:20]
  MapS_1 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_0(op_I_0_0),
    .I_0_1(op_I_0_1),
    .I_0_2(op_I_0_2),
    .I_1_0(op_I_1_0),
    .I_1_1(op_I_1_1),
    .I_1_2(op_I_1_2),
    .I_2_0(op_I_2_0),
    .I_2_1(op_I_2_1),
    .I_2_2(op_I_2_2),
    .I_3_0(op_I_3_0),
    .I_3_1(op_I_3_1),
    .I_3_2(op_I_3_2),
    .O_0_0(op_O_0_0),
    .O_1_0(op_O_1_0),
    .O_2_0(op_O_2_0),
    .O_3_0(op_O_3_0)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_0 = op_O_0_0; // @[MapT.scala 15:7]
  assign O_1_0 = op_O_1_0; // @[MapT.scala 15:7]
  assign O_2_0 = op_O_2_0; // @[MapT.scala 15:7]
  assign O_3_0 = op_O_3_0; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_0 = I_0_0; // @[MapT.scala 14:10]
  assign op_I_0_1 = I_0_1; // @[MapT.scala 14:10]
  assign op_I_0_2 = I_0_2; // @[MapT.scala 14:10]
  assign op_I_1_0 = I_1_0; // @[MapT.scala 14:10]
  assign op_I_1_1 = I_1_1; // @[MapT.scala 14:10]
  assign op_I_1_2 = I_1_2; // @[MapT.scala 14:10]
  assign op_I_2_0 = I_2_0; // @[MapT.scala 14:10]
  assign op_I_2_1 = I_2_1; // @[MapT.scala 14:10]
  assign op_I_2_2 = I_2_2; // @[MapT.scala 14:10]
  assign op_I_3_0 = I_3_0; // @[MapT.scala 14:10]
  assign op_I_3_1 = I_3_1; // @[MapT.scala 14:10]
  assign op_I_3_2 = I_3_2; // @[MapT.scala 14:10]
endmodule
module FIFO_1(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_1_0,
  input  [7:0] I_2_0,
  input  [7:0] I_3_0,
  output [7:0] O_0_0,
  output [7:0] O_1_0,
  output [7:0] O_2_0,
  output [7:0] O_3_0
);
  reg [7:0] _T__0_0; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_0;
  reg [7:0] _T__1_0; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_1;
  reg [7:0] _T__2_0; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_2;
  reg [7:0] _T__3_0; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_3;
  reg  _T_1; // @[FIFO.scala 15:27]
  reg [31:0] _RAND_4;
  assign valid_down = _T_1; // @[FIFO.scala 16:16]
  assign O_0_0 = _T__0_0; // @[FIFO.scala 14:7]
  assign O_1_0 = _T__1_0; // @[FIFO.scala 14:7]
  assign O_2_0 = _T__2_0; // @[FIFO.scala 14:7]
  assign O_3_0 = _T__3_0; // @[FIFO.scala 14:7]
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
  _T__0_0 = _RAND_0[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T__1_0 = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T__2_0 = _RAND_2[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T__3_0 = _RAND_3[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_1 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    _T__0_0 <= I_0_0;
    _T__1_0 <= I_1_0;
    _T__2_0 <= I_2_0;
    _T__3_0 <= I_3_0;
    if (reset) begin
      _T_1 <= 1'h0;
    end else begin
      _T_1 <= valid_up;
    end
  end
endmodule
module Top(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  input  [7:0] I_3,
  output [7:0] O_0_0,
  output [7:0] O_1_0,
  output [7:0] O_2_0,
  output [7:0] O_3_0
);
  wire  n1_clock; // @[Top.scala 47:20]
  wire  n1_reset; // @[Top.scala 47:20]
  wire  n1_valid_up; // @[Top.scala 47:20]
  wire  n1_valid_down; // @[Top.scala 47:20]
  wire [7:0] n1_I_0; // @[Top.scala 47:20]
  wire [7:0] n1_I_1; // @[Top.scala 47:20]
  wire [7:0] n1_I_2; // @[Top.scala 47:20]
  wire [7:0] n1_I_3; // @[Top.scala 47:20]
  wire [7:0] n1_O_0; // @[Top.scala 47:20]
  wire [7:0] n1_O_1; // @[Top.scala 47:20]
  wire [7:0] n1_O_2; // @[Top.scala 47:20]
  wire [7:0] n1_O_3; // @[Top.scala 47:20]
  wire  n2_clock; // @[Top.scala 50:20]
  wire  n2_valid_up; // @[Top.scala 50:20]
  wire  n2_valid_down; // @[Top.scala 50:20]
  wire [7:0] n2_I_0; // @[Top.scala 50:20]
  wire [7:0] n2_I_1; // @[Top.scala 50:20]
  wire [7:0] n2_I_2; // @[Top.scala 50:20]
  wire [7:0] n2_I_3; // @[Top.scala 50:20]
  wire [7:0] n2_O_0; // @[Top.scala 50:20]
  wire [7:0] n2_O_1; // @[Top.scala 50:20]
  wire [7:0] n2_O_2; // @[Top.scala 50:20]
  wire [7:0] n2_O_3; // @[Top.scala 50:20]
  wire  n3_clock; // @[Top.scala 53:20]
  wire  n3_valid_up; // @[Top.scala 53:20]
  wire  n3_valid_down; // @[Top.scala 53:20]
  wire [7:0] n3_I_0; // @[Top.scala 53:20]
  wire [7:0] n3_I_1; // @[Top.scala 53:20]
  wire [7:0] n3_I_2; // @[Top.scala 53:20]
  wire [7:0] n3_I_3; // @[Top.scala 53:20]
  wire [7:0] n3_O_0; // @[Top.scala 53:20]
  wire [7:0] n3_O_1; // @[Top.scala 53:20]
  wire [7:0] n3_O_2; // @[Top.scala 53:20]
  wire [7:0] n3_O_3; // @[Top.scala 53:20]
  wire  n4_valid_up; // @[Top.scala 56:20]
  wire  n4_valid_down; // @[Top.scala 56:20]
  wire [7:0] n4_I0_0; // @[Top.scala 56:20]
  wire [7:0] n4_I0_1; // @[Top.scala 56:20]
  wire [7:0] n4_I0_2; // @[Top.scala 56:20]
  wire [7:0] n4_I0_3; // @[Top.scala 56:20]
  wire [7:0] n4_I1_0; // @[Top.scala 56:20]
  wire [7:0] n4_I1_1; // @[Top.scala 56:20]
  wire [7:0] n4_I1_2; // @[Top.scala 56:20]
  wire [7:0] n4_I1_3; // @[Top.scala 56:20]
  wire [7:0] n4_O_0_0; // @[Top.scala 56:20]
  wire [7:0] n4_O_0_1; // @[Top.scala 56:20]
  wire [7:0] n4_O_1_0; // @[Top.scala 56:20]
  wire [7:0] n4_O_1_1; // @[Top.scala 56:20]
  wire [7:0] n4_O_2_0; // @[Top.scala 56:20]
  wire [7:0] n4_O_2_1; // @[Top.scala 56:20]
  wire [7:0] n4_O_3_0; // @[Top.scala 56:20]
  wire [7:0] n4_O_3_1; // @[Top.scala 56:20]
  wire  n11_valid_up; // @[Top.scala 60:21]
  wire  n11_valid_down; // @[Top.scala 60:21]
  wire [7:0] n11_I0_0_0; // @[Top.scala 60:21]
  wire [7:0] n11_I0_0_1; // @[Top.scala 60:21]
  wire [7:0] n11_I0_1_0; // @[Top.scala 60:21]
  wire [7:0] n11_I0_1_1; // @[Top.scala 60:21]
  wire [7:0] n11_I0_2_0; // @[Top.scala 60:21]
  wire [7:0] n11_I0_2_1; // @[Top.scala 60:21]
  wire [7:0] n11_I0_3_0; // @[Top.scala 60:21]
  wire [7:0] n11_I0_3_1; // @[Top.scala 60:21]
  wire [7:0] n11_I1_0; // @[Top.scala 60:21]
  wire [7:0] n11_I1_1; // @[Top.scala 60:21]
  wire [7:0] n11_I1_2; // @[Top.scala 60:21]
  wire [7:0] n11_I1_3; // @[Top.scala 60:21]
  wire [7:0] n11_O_0_0; // @[Top.scala 60:21]
  wire [7:0] n11_O_0_1; // @[Top.scala 60:21]
  wire [7:0] n11_O_0_2; // @[Top.scala 60:21]
  wire [7:0] n11_O_1_0; // @[Top.scala 60:21]
  wire [7:0] n11_O_1_1; // @[Top.scala 60:21]
  wire [7:0] n11_O_1_2; // @[Top.scala 60:21]
  wire [7:0] n11_O_2_0; // @[Top.scala 60:21]
  wire [7:0] n11_O_2_1; // @[Top.scala 60:21]
  wire [7:0] n11_O_2_2; // @[Top.scala 60:21]
  wire [7:0] n11_O_3_0; // @[Top.scala 60:21]
  wire [7:0] n11_O_3_1; // @[Top.scala 60:21]
  wire [7:0] n11_O_3_2; // @[Top.scala 60:21]
  wire  n20_valid_up; // @[Top.scala 64:21]
  wire  n20_valid_down; // @[Top.scala 64:21]
  wire [7:0] n20_I_0_0; // @[Top.scala 64:21]
  wire [7:0] n20_I_0_1; // @[Top.scala 64:21]
  wire [7:0] n20_I_0_2; // @[Top.scala 64:21]
  wire [7:0] n20_I_1_0; // @[Top.scala 64:21]
  wire [7:0] n20_I_1_1; // @[Top.scala 64:21]
  wire [7:0] n20_I_1_2; // @[Top.scala 64:21]
  wire [7:0] n20_I_2_0; // @[Top.scala 64:21]
  wire [7:0] n20_I_2_1; // @[Top.scala 64:21]
  wire [7:0] n20_I_2_2; // @[Top.scala 64:21]
  wire [7:0] n20_I_3_0; // @[Top.scala 64:21]
  wire [7:0] n20_I_3_1; // @[Top.scala 64:21]
  wire [7:0] n20_I_3_2; // @[Top.scala 64:21]
  wire [7:0] n20_O_0_0_0; // @[Top.scala 64:21]
  wire [7:0] n20_O_0_0_1; // @[Top.scala 64:21]
  wire [7:0] n20_O_0_0_2; // @[Top.scala 64:21]
  wire [7:0] n20_O_1_0_0; // @[Top.scala 64:21]
  wire [7:0] n20_O_1_0_1; // @[Top.scala 64:21]
  wire [7:0] n20_O_1_0_2; // @[Top.scala 64:21]
  wire [7:0] n20_O_2_0_0; // @[Top.scala 64:21]
  wire [7:0] n20_O_2_0_1; // @[Top.scala 64:21]
  wire [7:0] n20_O_2_0_2; // @[Top.scala 64:21]
  wire [7:0] n20_O_3_0_0; // @[Top.scala 64:21]
  wire [7:0] n20_O_3_0_1; // @[Top.scala 64:21]
  wire [7:0] n20_O_3_0_2; // @[Top.scala 64:21]
  wire  n27_valid_up; // @[Top.scala 67:21]
  wire  n27_valid_down; // @[Top.scala 67:21]
  wire [7:0] n27_I_0_0_0; // @[Top.scala 67:21]
  wire [7:0] n27_I_0_0_1; // @[Top.scala 67:21]
  wire [7:0] n27_I_0_0_2; // @[Top.scala 67:21]
  wire [7:0] n27_I_1_0_0; // @[Top.scala 67:21]
  wire [7:0] n27_I_1_0_1; // @[Top.scala 67:21]
  wire [7:0] n27_I_1_0_2; // @[Top.scala 67:21]
  wire [7:0] n27_I_2_0_0; // @[Top.scala 67:21]
  wire [7:0] n27_I_2_0_1; // @[Top.scala 67:21]
  wire [7:0] n27_I_2_0_2; // @[Top.scala 67:21]
  wire [7:0] n27_I_3_0_0; // @[Top.scala 67:21]
  wire [7:0] n27_I_3_0_1; // @[Top.scala 67:21]
  wire [7:0] n27_I_3_0_2; // @[Top.scala 67:21]
  wire [7:0] n27_O_0_0; // @[Top.scala 67:21]
  wire [7:0] n27_O_0_1; // @[Top.scala 67:21]
  wire [7:0] n27_O_0_2; // @[Top.scala 67:21]
  wire [7:0] n27_O_1_0; // @[Top.scala 67:21]
  wire [7:0] n27_O_1_1; // @[Top.scala 67:21]
  wire [7:0] n27_O_1_2; // @[Top.scala 67:21]
  wire [7:0] n27_O_2_0; // @[Top.scala 67:21]
  wire [7:0] n27_O_2_1; // @[Top.scala 67:21]
  wire [7:0] n27_O_2_2; // @[Top.scala 67:21]
  wire [7:0] n27_O_3_0; // @[Top.scala 67:21]
  wire [7:0] n27_O_3_1; // @[Top.scala 67:21]
  wire [7:0] n27_O_3_2; // @[Top.scala 67:21]
  wire  n41_clock; // @[Top.scala 70:21]
  wire  n41_reset; // @[Top.scala 70:21]
  wire  n41_valid_up; // @[Top.scala 70:21]
  wire  n41_valid_down; // @[Top.scala 70:21]
  wire [7:0] n41_I_0_0; // @[Top.scala 70:21]
  wire [7:0] n41_I_0_1; // @[Top.scala 70:21]
  wire [7:0] n41_I_0_2; // @[Top.scala 70:21]
  wire [7:0] n41_I_1_0; // @[Top.scala 70:21]
  wire [7:0] n41_I_1_1; // @[Top.scala 70:21]
  wire [7:0] n41_I_1_2; // @[Top.scala 70:21]
  wire [7:0] n41_I_2_0; // @[Top.scala 70:21]
  wire [7:0] n41_I_2_1; // @[Top.scala 70:21]
  wire [7:0] n41_I_2_2; // @[Top.scala 70:21]
  wire [7:0] n41_I_3_0; // @[Top.scala 70:21]
  wire [7:0] n41_I_3_1; // @[Top.scala 70:21]
  wire [7:0] n41_I_3_2; // @[Top.scala 70:21]
  wire [7:0] n41_O_0_0; // @[Top.scala 70:21]
  wire [7:0] n41_O_1_0; // @[Top.scala 70:21]
  wire [7:0] n41_O_2_0; // @[Top.scala 70:21]
  wire [7:0] n41_O_3_0; // @[Top.scala 70:21]
  wire  n42_clock; // @[Top.scala 73:21]
  wire  n42_reset; // @[Top.scala 73:21]
  wire  n42_valid_up; // @[Top.scala 73:21]
  wire  n42_valid_down; // @[Top.scala 73:21]
  wire [7:0] n42_I_0_0; // @[Top.scala 73:21]
  wire [7:0] n42_I_1_0; // @[Top.scala 73:21]
  wire [7:0] n42_I_2_0; // @[Top.scala 73:21]
  wire [7:0] n42_I_3_0; // @[Top.scala 73:21]
  wire [7:0] n42_O_0_0; // @[Top.scala 73:21]
  wire [7:0] n42_O_1_0; // @[Top.scala 73:21]
  wire [7:0] n42_O_2_0; // @[Top.scala 73:21]
  wire [7:0] n42_O_3_0; // @[Top.scala 73:21]
  wire  n43_clock; // @[Top.scala 76:21]
  wire  n43_reset; // @[Top.scala 76:21]
  wire  n43_valid_up; // @[Top.scala 76:21]
  wire  n43_valid_down; // @[Top.scala 76:21]
  wire [7:0] n43_I_0_0; // @[Top.scala 76:21]
  wire [7:0] n43_I_1_0; // @[Top.scala 76:21]
  wire [7:0] n43_I_2_0; // @[Top.scala 76:21]
  wire [7:0] n43_I_3_0; // @[Top.scala 76:21]
  wire [7:0] n43_O_0_0; // @[Top.scala 76:21]
  wire [7:0] n43_O_1_0; // @[Top.scala 76:21]
  wire [7:0] n43_O_2_0; // @[Top.scala 76:21]
  wire [7:0] n43_O_3_0; // @[Top.scala 76:21]
  wire  n44_clock; // @[Top.scala 79:21]
  wire  n44_reset; // @[Top.scala 79:21]
  wire  n44_valid_up; // @[Top.scala 79:21]
  wire  n44_valid_down; // @[Top.scala 79:21]
  wire [7:0] n44_I_0_0; // @[Top.scala 79:21]
  wire [7:0] n44_I_1_0; // @[Top.scala 79:21]
  wire [7:0] n44_I_2_0; // @[Top.scala 79:21]
  wire [7:0] n44_I_3_0; // @[Top.scala 79:21]
  wire [7:0] n44_O_0_0; // @[Top.scala 79:21]
  wire [7:0] n44_O_1_0; // @[Top.scala 79:21]
  wire [7:0] n44_O_2_0; // @[Top.scala 79:21]
  wire [7:0] n44_O_3_0; // @[Top.scala 79:21]
  FIFO n1 ( // @[Top.scala 47:20]
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
  ShiftTS n2 ( // @[Top.scala 50:20]
    .clock(n2_clock),
    .valid_up(n2_valid_up),
    .valid_down(n2_valid_down),
    .I_0(n2_I_0),
    .I_1(n2_I_1),
    .I_2(n2_I_2),
    .I_3(n2_I_3),
    .O_0(n2_O_0),
    .O_1(n2_O_1),
    .O_2(n2_O_2),
    .O_3(n2_O_3)
  );
  ShiftTS n3 ( // @[Top.scala 53:20]
    .clock(n3_clock),
    .valid_up(n3_valid_up),
    .valid_down(n3_valid_down),
    .I_0(n3_I_0),
    .I_1(n3_I_1),
    .I_2(n3_I_2),
    .I_3(n3_I_3),
    .O_0(n3_O_0),
    .O_1(n3_O_1),
    .O_2(n3_O_2),
    .O_3(n3_O_3)
  );
  Map2T n4 ( // @[Top.scala 56:20]
    .valid_up(n4_valid_up),
    .valid_down(n4_valid_down),
    .I0_0(n4_I0_0),
    .I0_1(n4_I0_1),
    .I0_2(n4_I0_2),
    .I0_3(n4_I0_3),
    .I1_0(n4_I1_0),
    .I1_1(n4_I1_1),
    .I1_2(n4_I1_2),
    .I1_3(n4_I1_3),
    .O_0_0(n4_O_0_0),
    .O_0_1(n4_O_0_1),
    .O_1_0(n4_O_1_0),
    .O_1_1(n4_O_1_1),
    .O_2_0(n4_O_2_0),
    .O_2_1(n4_O_2_1),
    .O_3_0(n4_O_3_0),
    .O_3_1(n4_O_3_1)
  );
  Map2T_1 n11 ( // @[Top.scala 60:21]
    .valid_up(n11_valid_up),
    .valid_down(n11_valid_down),
    .I0_0_0(n11_I0_0_0),
    .I0_0_1(n11_I0_0_1),
    .I0_1_0(n11_I0_1_0),
    .I0_1_1(n11_I0_1_1),
    .I0_2_0(n11_I0_2_0),
    .I0_2_1(n11_I0_2_1),
    .I0_3_0(n11_I0_3_0),
    .I0_3_1(n11_I0_3_1),
    .I1_0(n11_I1_0),
    .I1_1(n11_I1_1),
    .I1_2(n11_I1_2),
    .I1_3(n11_I1_3),
    .O_0_0(n11_O_0_0),
    .O_0_1(n11_O_0_1),
    .O_0_2(n11_O_0_2),
    .O_1_0(n11_O_1_0),
    .O_1_1(n11_O_1_1),
    .O_1_2(n11_O_1_2),
    .O_2_0(n11_O_2_0),
    .O_2_1(n11_O_2_1),
    .O_2_2(n11_O_2_2),
    .O_3_0(n11_O_3_0),
    .O_3_1(n11_O_3_1),
    .O_3_2(n11_O_3_2)
  );
  MapT n20 ( // @[Top.scala 64:21]
    .valid_up(n20_valid_up),
    .valid_down(n20_valid_down),
    .I_0_0(n20_I_0_0),
    .I_0_1(n20_I_0_1),
    .I_0_2(n20_I_0_2),
    .I_1_0(n20_I_1_0),
    .I_1_1(n20_I_1_1),
    .I_1_2(n20_I_1_2),
    .I_2_0(n20_I_2_0),
    .I_2_1(n20_I_2_1),
    .I_2_2(n20_I_2_2),
    .I_3_0(n20_I_3_0),
    .I_3_1(n20_I_3_1),
    .I_3_2(n20_I_3_2),
    .O_0_0_0(n20_O_0_0_0),
    .O_0_0_1(n20_O_0_0_1),
    .O_0_0_2(n20_O_0_0_2),
    .O_1_0_0(n20_O_1_0_0),
    .O_1_0_1(n20_O_1_0_1),
    .O_1_0_2(n20_O_1_0_2),
    .O_2_0_0(n20_O_2_0_0),
    .O_2_0_1(n20_O_2_0_1),
    .O_2_0_2(n20_O_2_0_2),
    .O_3_0_0(n20_O_3_0_0),
    .O_3_0_1(n20_O_3_0_1),
    .O_3_0_2(n20_O_3_0_2)
  );
  MapT_1 n27 ( // @[Top.scala 67:21]
    .valid_up(n27_valid_up),
    .valid_down(n27_valid_down),
    .I_0_0_0(n27_I_0_0_0),
    .I_0_0_1(n27_I_0_0_1),
    .I_0_0_2(n27_I_0_0_2),
    .I_1_0_0(n27_I_1_0_0),
    .I_1_0_1(n27_I_1_0_1),
    .I_1_0_2(n27_I_1_0_2),
    .I_2_0_0(n27_I_2_0_0),
    .I_2_0_1(n27_I_2_0_1),
    .I_2_0_2(n27_I_2_0_2),
    .I_3_0_0(n27_I_3_0_0),
    .I_3_0_1(n27_I_3_0_1),
    .I_3_0_2(n27_I_3_0_2),
    .O_0_0(n27_O_0_0),
    .O_0_1(n27_O_0_1),
    .O_0_2(n27_O_0_2),
    .O_1_0(n27_O_1_0),
    .O_1_1(n27_O_1_1),
    .O_1_2(n27_O_1_2),
    .O_2_0(n27_O_2_0),
    .O_2_1(n27_O_2_1),
    .O_2_2(n27_O_2_2),
    .O_3_0(n27_O_3_0),
    .O_3_1(n27_O_3_1),
    .O_3_2(n27_O_3_2)
  );
  MapT_2 n41 ( // @[Top.scala 70:21]
    .clock(n41_clock),
    .reset(n41_reset),
    .valid_up(n41_valid_up),
    .valid_down(n41_valid_down),
    .I_0_0(n41_I_0_0),
    .I_0_1(n41_I_0_1),
    .I_0_2(n41_I_0_2),
    .I_1_0(n41_I_1_0),
    .I_1_1(n41_I_1_1),
    .I_1_2(n41_I_1_2),
    .I_2_0(n41_I_2_0),
    .I_2_1(n41_I_2_1),
    .I_2_2(n41_I_2_2),
    .I_3_0(n41_I_3_0),
    .I_3_1(n41_I_3_1),
    .I_3_2(n41_I_3_2),
    .O_0_0(n41_O_0_0),
    .O_1_0(n41_O_1_0),
    .O_2_0(n41_O_2_0),
    .O_3_0(n41_O_3_0)
  );
  FIFO_1 n42 ( // @[Top.scala 73:21]
    .clock(n42_clock),
    .reset(n42_reset),
    .valid_up(n42_valid_up),
    .valid_down(n42_valid_down),
    .I_0_0(n42_I_0_0),
    .I_1_0(n42_I_1_0),
    .I_2_0(n42_I_2_0),
    .I_3_0(n42_I_3_0),
    .O_0_0(n42_O_0_0),
    .O_1_0(n42_O_1_0),
    .O_2_0(n42_O_2_0),
    .O_3_0(n42_O_3_0)
  );
  FIFO_1 n43 ( // @[Top.scala 76:21]
    .clock(n43_clock),
    .reset(n43_reset),
    .valid_up(n43_valid_up),
    .valid_down(n43_valid_down),
    .I_0_0(n43_I_0_0),
    .I_1_0(n43_I_1_0),
    .I_2_0(n43_I_2_0),
    .I_3_0(n43_I_3_0),
    .O_0_0(n43_O_0_0),
    .O_1_0(n43_O_1_0),
    .O_2_0(n43_O_2_0),
    .O_3_0(n43_O_3_0)
  );
  FIFO_1 n44 ( // @[Top.scala 79:21]
    .clock(n44_clock),
    .reset(n44_reset),
    .valid_up(n44_valid_up),
    .valid_down(n44_valid_down),
    .I_0_0(n44_I_0_0),
    .I_1_0(n44_I_1_0),
    .I_2_0(n44_I_2_0),
    .I_3_0(n44_I_3_0),
    .O_0_0(n44_O_0_0),
    .O_1_0(n44_O_1_0),
    .O_2_0(n44_O_2_0),
    .O_3_0(n44_O_3_0)
  );
  assign valid_down = n44_valid_down; // @[Top.scala 83:16]
  assign O_0_0 = n44_O_0_0; // @[Top.scala 82:7]
  assign O_1_0 = n44_O_1_0; // @[Top.scala 82:7]
  assign O_2_0 = n44_O_2_0; // @[Top.scala 82:7]
  assign O_3_0 = n44_O_3_0; // @[Top.scala 82:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 49:17]
  assign n1_I_0 = I_0; // @[Top.scala 48:10]
  assign n1_I_1 = I_1; // @[Top.scala 48:10]
  assign n1_I_2 = I_2; // @[Top.scala 48:10]
  assign n1_I_3 = I_3; // @[Top.scala 48:10]
  assign n2_clock = clock;
  assign n2_valid_up = n1_valid_down; // @[Top.scala 52:17]
  assign n2_I_0 = n1_O_0; // @[Top.scala 51:10]
  assign n2_I_1 = n1_O_1; // @[Top.scala 51:10]
  assign n2_I_2 = n1_O_2; // @[Top.scala 51:10]
  assign n2_I_3 = n1_O_3; // @[Top.scala 51:10]
  assign n3_clock = clock;
  assign n3_valid_up = n2_valid_down; // @[Top.scala 55:17]
  assign n3_I_0 = n2_O_0; // @[Top.scala 54:10]
  assign n3_I_1 = n2_O_1; // @[Top.scala 54:10]
  assign n3_I_2 = n2_O_2; // @[Top.scala 54:10]
  assign n3_I_3 = n2_O_3; // @[Top.scala 54:10]
  assign n4_valid_up = n3_valid_down & n2_valid_down; // @[Top.scala 59:17]
  assign n4_I0_0 = n3_O_0; // @[Top.scala 57:11]
  assign n4_I0_1 = n3_O_1; // @[Top.scala 57:11]
  assign n4_I0_2 = n3_O_2; // @[Top.scala 57:11]
  assign n4_I0_3 = n3_O_3; // @[Top.scala 57:11]
  assign n4_I1_0 = n2_O_0; // @[Top.scala 58:11]
  assign n4_I1_1 = n2_O_1; // @[Top.scala 58:11]
  assign n4_I1_2 = n2_O_2; // @[Top.scala 58:11]
  assign n4_I1_3 = n2_O_3; // @[Top.scala 58:11]
  assign n11_valid_up = n4_valid_down & n1_valid_down; // @[Top.scala 63:18]
  assign n11_I0_0_0 = n4_O_0_0; // @[Top.scala 61:12]
  assign n11_I0_0_1 = n4_O_0_1; // @[Top.scala 61:12]
  assign n11_I0_1_0 = n4_O_1_0; // @[Top.scala 61:12]
  assign n11_I0_1_1 = n4_O_1_1; // @[Top.scala 61:12]
  assign n11_I0_2_0 = n4_O_2_0; // @[Top.scala 61:12]
  assign n11_I0_2_1 = n4_O_2_1; // @[Top.scala 61:12]
  assign n11_I0_3_0 = n4_O_3_0; // @[Top.scala 61:12]
  assign n11_I0_3_1 = n4_O_3_1; // @[Top.scala 61:12]
  assign n11_I1_0 = n1_O_0; // @[Top.scala 62:12]
  assign n11_I1_1 = n1_O_1; // @[Top.scala 62:12]
  assign n11_I1_2 = n1_O_2; // @[Top.scala 62:12]
  assign n11_I1_3 = n1_O_3; // @[Top.scala 62:12]
  assign n20_valid_up = n11_valid_down; // @[Top.scala 66:18]
  assign n20_I_0_0 = n11_O_0_0; // @[Top.scala 65:11]
  assign n20_I_0_1 = n11_O_0_1; // @[Top.scala 65:11]
  assign n20_I_0_2 = n11_O_0_2; // @[Top.scala 65:11]
  assign n20_I_1_0 = n11_O_1_0; // @[Top.scala 65:11]
  assign n20_I_1_1 = n11_O_1_1; // @[Top.scala 65:11]
  assign n20_I_1_2 = n11_O_1_2; // @[Top.scala 65:11]
  assign n20_I_2_0 = n11_O_2_0; // @[Top.scala 65:11]
  assign n20_I_2_1 = n11_O_2_1; // @[Top.scala 65:11]
  assign n20_I_2_2 = n11_O_2_2; // @[Top.scala 65:11]
  assign n20_I_3_0 = n11_O_3_0; // @[Top.scala 65:11]
  assign n20_I_3_1 = n11_O_3_1; // @[Top.scala 65:11]
  assign n20_I_3_2 = n11_O_3_2; // @[Top.scala 65:11]
  assign n27_valid_up = n20_valid_down; // @[Top.scala 69:18]
  assign n27_I_0_0_0 = n20_O_0_0_0; // @[Top.scala 68:11]
  assign n27_I_0_0_1 = n20_O_0_0_1; // @[Top.scala 68:11]
  assign n27_I_0_0_2 = n20_O_0_0_2; // @[Top.scala 68:11]
  assign n27_I_1_0_0 = n20_O_1_0_0; // @[Top.scala 68:11]
  assign n27_I_1_0_1 = n20_O_1_0_1; // @[Top.scala 68:11]
  assign n27_I_1_0_2 = n20_O_1_0_2; // @[Top.scala 68:11]
  assign n27_I_2_0_0 = n20_O_2_0_0; // @[Top.scala 68:11]
  assign n27_I_2_0_1 = n20_O_2_0_1; // @[Top.scala 68:11]
  assign n27_I_2_0_2 = n20_O_2_0_2; // @[Top.scala 68:11]
  assign n27_I_3_0_0 = n20_O_3_0_0; // @[Top.scala 68:11]
  assign n27_I_3_0_1 = n20_O_3_0_1; // @[Top.scala 68:11]
  assign n27_I_3_0_2 = n20_O_3_0_2; // @[Top.scala 68:11]
  assign n41_clock = clock;
  assign n41_reset = reset;
  assign n41_valid_up = n27_valid_down; // @[Top.scala 72:18]
  assign n41_I_0_0 = n27_O_0_0; // @[Top.scala 71:11]
  assign n41_I_0_1 = n27_O_0_1; // @[Top.scala 71:11]
  assign n41_I_0_2 = n27_O_0_2; // @[Top.scala 71:11]
  assign n41_I_1_0 = n27_O_1_0; // @[Top.scala 71:11]
  assign n41_I_1_1 = n27_O_1_1; // @[Top.scala 71:11]
  assign n41_I_1_2 = n27_O_1_2; // @[Top.scala 71:11]
  assign n41_I_2_0 = n27_O_2_0; // @[Top.scala 71:11]
  assign n41_I_2_1 = n27_O_2_1; // @[Top.scala 71:11]
  assign n41_I_2_2 = n27_O_2_2; // @[Top.scala 71:11]
  assign n41_I_3_0 = n27_O_3_0; // @[Top.scala 71:11]
  assign n41_I_3_1 = n27_O_3_1; // @[Top.scala 71:11]
  assign n41_I_3_2 = n27_O_3_2; // @[Top.scala 71:11]
  assign n42_clock = clock;
  assign n42_reset = reset;
  assign n42_valid_up = n41_valid_down; // @[Top.scala 75:18]
  assign n42_I_0_0 = n41_O_0_0; // @[Top.scala 74:11]
  assign n42_I_1_0 = n41_O_1_0; // @[Top.scala 74:11]
  assign n42_I_2_0 = n41_O_2_0; // @[Top.scala 74:11]
  assign n42_I_3_0 = n41_O_3_0; // @[Top.scala 74:11]
  assign n43_clock = clock;
  assign n43_reset = reset;
  assign n43_valid_up = n42_valid_down; // @[Top.scala 78:18]
  assign n43_I_0_0 = n42_O_0_0; // @[Top.scala 77:11]
  assign n43_I_1_0 = n42_O_1_0; // @[Top.scala 77:11]
  assign n43_I_2_0 = n42_O_2_0; // @[Top.scala 77:11]
  assign n43_I_3_0 = n42_O_3_0; // @[Top.scala 77:11]
  assign n44_clock = clock;
  assign n44_reset = reset;
  assign n44_valid_up = n43_valid_down; // @[Top.scala 81:18]
  assign n44_I_0_0 = n43_O_0_0; // @[Top.scala 80:11]
  assign n44_I_1_0 = n43_O_1_0; // @[Top.scala 80:11]
  assign n44_I_2_0 = n43_O_2_0; // @[Top.scala 80:11]
  assign n44_I_3_0 = n43_O_3_0; // @[Top.scala 80:11]
endmodule
