module FIFO(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  input  [31:0] I_3,
  input  [31:0] I_4,
  input  [31:0] I_5,
  input  [31:0] I_6,
  output [31:0] O_0,
  output [31:0] O_1,
  output [31:0] O_2,
  output [31:0] O_3,
  output [31:0] O_4,
  output [31:0] O_5,
  output [31:0] O_6
);
  reg [31:0] _T__0; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_0;
  reg [31:0] _T__1; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_1;
  reg [31:0] _T__2; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_2;
  reg [31:0] _T__3; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_3;
  reg [31:0] _T__4; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_4;
  reg [31:0] _T__5; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_5;
  reg [31:0] _T__6; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_6;
  reg  _T_1; // @[FIFO.scala 15:27]
  reg [31:0] _RAND_7;
  assign valid_down = _T_1; // @[FIFO.scala 16:16]
  assign O_0 = _T__0; // @[FIFO.scala 14:7]
  assign O_1 = _T__1; // @[FIFO.scala 14:7]
  assign O_2 = _T__2; // @[FIFO.scala 14:7]
  assign O_3 = _T__3; // @[FIFO.scala 14:7]
  assign O_4 = _T__4; // @[FIFO.scala 14:7]
  assign O_5 = _T__5; // @[FIFO.scala 14:7]
  assign O_6 = _T__6; // @[FIFO.scala 14:7]
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
  _T__0 = _RAND_0[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T__1 = _RAND_1[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T__2 = _RAND_2[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T__3 = _RAND_3[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T__4 = _RAND_4[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T__5 = _RAND_5[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T__6 = _RAND_6[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_1 = _RAND_7[0:0];
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
    _T__5 <= I_5;
    _T__6 <= I_6;
    if (reset) begin
      _T_1 <= 1'h0;
    end else begin
      _T_1 <= valid_up;
    end
  end
endmodule
module AddNoValid(
  input  [31:0] I_t0b,
  input  [31:0] I_t1b,
  output [31:0] O
);
  assign O = I_t0b + I_t1b; // @[Arithmetic.scala 122:7]
endmodule
module ReduceS(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  input  [31:0] I_3,
  input  [31:0] I_4,
  input  [31:0] I_5,
  input  [31:0] I_6,
  output [31:0] O_0
);
  wire [31:0] AddNoValid_I_t0b; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_I_t1b; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_O; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_1_I_t0b; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_1_I_t1b; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_1_O; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_2_I_t0b; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_2_I_t1b; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_2_O; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_3_I_t0b; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_3_I_t1b; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_3_O; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_4_I_t0b; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_4_I_t1b; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_4_O; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_5_I_t0b; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_5_I_t1b; // @[ReduceS.scala 20:43]
  wire [31:0] AddNoValid_5_O; // @[ReduceS.scala 20:43]
  reg [31:0] _T; // @[ReduceS.scala 27:24]
  reg [31:0] _RAND_0;
  reg [31:0] _T_1; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_1;
  reg [31:0] _T_2; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_2;
  reg [31:0] _T_3; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_3;
  reg [31:0] _T_4; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_4;
  reg [31:0] _T_5; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_5;
  reg [31:0] _T_6; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_6;
  reg [31:0] _T_7; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_7;
  reg  _T_8; // @[ReduceS.scala 47:32]
  reg [31:0] _RAND_8;
  reg  _T_9; // @[ReduceS.scala 47:24]
  reg [31:0] _RAND_9;
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
  AddNoValid AddNoValid_4 ( // @[ReduceS.scala 20:43]
    .I_t0b(AddNoValid_4_I_t0b),
    .I_t1b(AddNoValid_4_I_t1b),
    .O(AddNoValid_4_O)
  );
  AddNoValid AddNoValid_5 ( // @[ReduceS.scala 20:43]
    .I_t0b(AddNoValid_5_I_t0b),
    .I_t1b(AddNoValid_5_I_t1b),
    .O(AddNoValid_5_O)
  );
  assign valid_down = _T_9; // @[ReduceS.scala 47:14]
  assign O_0 = _T; // @[ReduceS.scala 27:14]
  assign AddNoValid_I_t0b = _T_4; // @[ReduceS.scala 43:18]
  assign AddNoValid_I_t1b = AddNoValid_1_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_1_I_t0b = AddNoValid_2_O; // @[ReduceS.scala 31:18]
  assign AddNoValid_1_I_t1b = AddNoValid_3_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_2_I_t0b = _T_5; // @[ReduceS.scala 43:18]
  assign AddNoValid_2_I_t1b = AddNoValid_5_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_3_I_t0b = AddNoValid_4_O; // @[ReduceS.scala 31:18]
  assign AddNoValid_3_I_t1b = _T_3; // @[ReduceS.scala 43:18]
  assign AddNoValid_4_I_t0b = _T_1; // @[ReduceS.scala 43:18]
  assign AddNoValid_4_I_t1b = _T_6; // @[ReduceS.scala 43:18]
  assign AddNoValid_5_I_t0b = _T_2; // @[ReduceS.scala 43:18]
  assign AddNoValid_5_I_t1b = _T_7; // @[ReduceS.scala 43:18]
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
  _T = _RAND_0[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_1 = _RAND_1[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_2 = _RAND_2[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_3 = _RAND_3[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_4 = _RAND_4[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_5 = _RAND_5[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_6 = _RAND_6[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_7 = _RAND_7[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{`RANDOM}};
  _T_8 = _RAND_8[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_9 = {1{`RANDOM}};
  _T_9 = _RAND_9[0:0];
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
    _T_6 <= I_5;
    _T_7 <= I_6;
    if (reset) begin
      _T_8 <= 1'h0;
    end else begin
      _T_8 <= valid_up;
    end
    _T_9 <= _T_8;
  end
endmodule
module MapT(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  input  [31:0] I_3,
  input  [31:0] I_4,
  input  [31:0] I_5,
  input  [31:0] I_6,
  output [31:0] O_0
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_0; // @[MapT.scala 8:20]
  wire [31:0] op_I_1; // @[MapT.scala 8:20]
  wire [31:0] op_I_2; // @[MapT.scala 8:20]
  wire [31:0] op_I_3; // @[MapT.scala 8:20]
  wire [31:0] op_I_4; // @[MapT.scala 8:20]
  wire [31:0] op_I_5; // @[MapT.scala 8:20]
  wire [31:0] op_I_6; // @[MapT.scala 8:20]
  wire [31:0] op_O_0; // @[MapT.scala 8:20]
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
    .I_5(op_I_5),
    .I_6(op_I_6),
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
  assign op_I_5 = I_5; // @[MapT.scala 14:10]
  assign op_I_6 = I_6; // @[MapT.scala 14:10]
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
  input  [31:0] I_0_t0b,
  input  [31:0] I_0_t1b,
  output [31:0] O_0
);
  wire [31:0] fst_op_I_t0b; // @[MapS.scala 28:22]
  wire [31:0] fst_op_I_t1b; // @[MapS.scala 28:22]
  wire [31:0] fst_op_O; // @[MapS.scala 28:22]
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
  input  [31:0] I_0,
  output [31:0] O_0
);
  wire  NestedCountersWithNumValid_CE; // @[ReduceT.scala 22:34]
  wire  NestedCountersWithNumValid_valid; // @[ReduceT.scala 22:34]
  wire [31:0] MapSNoValid_I_0_t0b; // @[ReduceT.scala 25:25]
  wire [31:0] MapSNoValid_I_0_t1b; // @[ReduceT.scala 25:25]
  wire [31:0] MapSNoValid_O_0; // @[ReduceT.scala 25:25]
  reg  _T; // @[ReduceT.scala 26:50]
  reg [31:0] _RAND_0;
  reg [6:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire  _T_3 = value == 7'h77; // @[Counter.scala 38:24]
  wire [6:0] _T_5 = value + 7'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value == 7'h0; // @[ReduceT.scala 34:60]
  reg [31:0] _T_7_0; // @[ReduceT.scala 34:76]
  reg [31:0] _RAND_2;
  reg [31:0] _T_9_0; // @[ReduceT.scala 35:24]
  reg [31:0] _RAND_3;
  reg  _T_10; // @[ReduceT.scala 37:35]
  reg [31:0] _RAND_4;
  reg [31:0] _T_11_0; // @[ReduceT.scala 44:83]
  reg [31:0] _RAND_5;
  reg  _T_12; // @[ReduceT.scala 51:28]
  reg [31:0] _RAND_6;
  wire  _T_14 = _T_12 | _T_3; // @[ReduceT.scala 52:28]
  reg [31:0] _T_15_0; // @[ReduceT.scala 56:15]
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
  value = _RAND_1[6:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_7_0 = _RAND_2[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_9_0 = _RAND_3[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_10 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_11_0 = _RAND_5[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_12 = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_15_0 = _RAND_7[31:0];
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
      value <= 7'h0;
    end else if (_T) begin
      if (_T_3) begin
        value <= 7'h0;
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
  input  [31:0] I_0,
  output [31:0] O
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O = I_0; // @[Passthrough.scala 17:68]
endmodule
module FIFO_1(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  reg [31:0] _T; // @[FIFO.scala 13:26]
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
  _T = _RAND_0[31:0];
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
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  input  [31:0] I_3,
  input  [31:0] I_4,
  input  [31:0] I_5,
  input  [31:0] I_6,
  output [31:0] O
);
  wire  n1_clock; // @[Top.scala 17:20]
  wire  n1_reset; // @[Top.scala 17:20]
  wire  n1_valid_up; // @[Top.scala 17:20]
  wire  n1_valid_down; // @[Top.scala 17:20]
  wire [31:0] n1_I_0; // @[Top.scala 17:20]
  wire [31:0] n1_I_1; // @[Top.scala 17:20]
  wire [31:0] n1_I_2; // @[Top.scala 17:20]
  wire [31:0] n1_I_3; // @[Top.scala 17:20]
  wire [31:0] n1_I_4; // @[Top.scala 17:20]
  wire [31:0] n1_I_5; // @[Top.scala 17:20]
  wire [31:0] n1_I_6; // @[Top.scala 17:20]
  wire [31:0] n1_O_0; // @[Top.scala 17:20]
  wire [31:0] n1_O_1; // @[Top.scala 17:20]
  wire [31:0] n1_O_2; // @[Top.scala 17:20]
  wire [31:0] n1_O_3; // @[Top.scala 17:20]
  wire [31:0] n1_O_4; // @[Top.scala 17:20]
  wire [31:0] n1_O_5; // @[Top.scala 17:20]
  wire [31:0] n1_O_6; // @[Top.scala 17:20]
  wire  n6_clock; // @[Top.scala 20:20]
  wire  n6_reset; // @[Top.scala 20:20]
  wire  n6_valid_up; // @[Top.scala 20:20]
  wire  n6_valid_down; // @[Top.scala 20:20]
  wire [31:0] n6_I_0; // @[Top.scala 20:20]
  wire [31:0] n6_I_1; // @[Top.scala 20:20]
  wire [31:0] n6_I_2; // @[Top.scala 20:20]
  wire [31:0] n6_I_3; // @[Top.scala 20:20]
  wire [31:0] n6_I_4; // @[Top.scala 20:20]
  wire [31:0] n6_I_5; // @[Top.scala 20:20]
  wire [31:0] n6_I_6; // @[Top.scala 20:20]
  wire [31:0] n6_O_0; // @[Top.scala 20:20]
  wire  n9_clock; // @[Top.scala 23:20]
  wire  n9_reset; // @[Top.scala 23:20]
  wire  n9_valid_up; // @[Top.scala 23:20]
  wire  n9_valid_down; // @[Top.scala 23:20]
  wire [31:0] n9_I_0; // @[Top.scala 23:20]
  wire [31:0] n9_O_0; // @[Top.scala 23:20]
  wire  n10_valid_up; // @[Top.scala 26:21]
  wire  n10_valid_down; // @[Top.scala 26:21]
  wire [31:0] n10_I_0; // @[Top.scala 26:21]
  wire [31:0] n10_O; // @[Top.scala 26:21]
  wire  n11_clock; // @[Top.scala 29:21]
  wire  n11_reset; // @[Top.scala 29:21]
  wire  n11_valid_up; // @[Top.scala 29:21]
  wire  n11_valid_down; // @[Top.scala 29:21]
  wire [31:0] n11_I; // @[Top.scala 29:21]
  wire [31:0] n11_O; // @[Top.scala 29:21]
  wire  n12_clock; // @[Top.scala 32:21]
  wire  n12_reset; // @[Top.scala 32:21]
  wire  n12_valid_up; // @[Top.scala 32:21]
  wire  n12_valid_down; // @[Top.scala 32:21]
  wire [31:0] n12_I; // @[Top.scala 32:21]
  wire [31:0] n12_O; // @[Top.scala 32:21]
  wire  n13_clock; // @[Top.scala 35:21]
  wire  n13_reset; // @[Top.scala 35:21]
  wire  n13_valid_up; // @[Top.scala 35:21]
  wire  n13_valid_down; // @[Top.scala 35:21]
  wire [31:0] n13_I; // @[Top.scala 35:21]
  wire [31:0] n13_O; // @[Top.scala 35:21]
  FIFO n1 ( // @[Top.scala 17:20]
    .clock(n1_clock),
    .reset(n1_reset),
    .valid_up(n1_valid_up),
    .valid_down(n1_valid_down),
    .I_0(n1_I_0),
    .I_1(n1_I_1),
    .I_2(n1_I_2),
    .I_3(n1_I_3),
    .I_4(n1_I_4),
    .I_5(n1_I_5),
    .I_6(n1_I_6),
    .O_0(n1_O_0),
    .O_1(n1_O_1),
    .O_2(n1_O_2),
    .O_3(n1_O_3),
    .O_4(n1_O_4),
    .O_5(n1_O_5),
    .O_6(n1_O_6)
  );
  MapT n6 ( // @[Top.scala 20:20]
    .clock(n6_clock),
    .reset(n6_reset),
    .valid_up(n6_valid_up),
    .valid_down(n6_valid_down),
    .I_0(n6_I_0),
    .I_1(n6_I_1),
    .I_2(n6_I_2),
    .I_3(n6_I_3),
    .I_4(n6_I_4),
    .I_5(n6_I_5),
    .I_6(n6_I_6),
    .O_0(n6_O_0)
  );
  ReduceT n9 ( // @[Top.scala 23:20]
    .clock(n9_clock),
    .reset(n9_reset),
    .valid_up(n9_valid_up),
    .valid_down(n9_valid_down),
    .I_0(n9_I_0),
    .O_0(n9_O_0)
  );
  Passthrough n10 ( // @[Top.scala 26:21]
    .valid_up(n10_valid_up),
    .valid_down(n10_valid_down),
    .I_0(n10_I_0),
    .O(n10_O)
  );
  FIFO_1 n11 ( // @[Top.scala 29:21]
    .clock(n11_clock),
    .reset(n11_reset),
    .valid_up(n11_valid_up),
    .valid_down(n11_valid_down),
    .I(n11_I),
    .O(n11_O)
  );
  FIFO_1 n12 ( // @[Top.scala 32:21]
    .clock(n12_clock),
    .reset(n12_reset),
    .valid_up(n12_valid_up),
    .valid_down(n12_valid_down),
    .I(n12_I),
    .O(n12_O)
  );
  FIFO_1 n13 ( // @[Top.scala 35:21]
    .clock(n13_clock),
    .reset(n13_reset),
    .valid_up(n13_valid_up),
    .valid_down(n13_valid_down),
    .I(n13_I),
    .O(n13_O)
  );
  assign valid_down = n13_valid_down; // @[Top.scala 39:16]
  assign O = n13_O; // @[Top.scala 38:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 19:17]
  assign n1_I_0 = I_0; // @[Top.scala 18:10]
  assign n1_I_1 = I_1; // @[Top.scala 18:10]
  assign n1_I_2 = I_2; // @[Top.scala 18:10]
  assign n1_I_3 = I_3; // @[Top.scala 18:10]
  assign n1_I_4 = I_4; // @[Top.scala 18:10]
  assign n1_I_5 = I_5; // @[Top.scala 18:10]
  assign n1_I_6 = I_6; // @[Top.scala 18:10]
  assign n6_clock = clock;
  assign n6_reset = reset;
  assign n6_valid_up = n1_valid_down; // @[Top.scala 22:17]
  assign n6_I_0 = n1_O_0; // @[Top.scala 21:10]
  assign n6_I_1 = n1_O_1; // @[Top.scala 21:10]
  assign n6_I_2 = n1_O_2; // @[Top.scala 21:10]
  assign n6_I_3 = n1_O_3; // @[Top.scala 21:10]
  assign n6_I_4 = n1_O_4; // @[Top.scala 21:10]
  assign n6_I_5 = n1_O_5; // @[Top.scala 21:10]
  assign n6_I_6 = n1_O_6; // @[Top.scala 21:10]
  assign n9_clock = clock;
  assign n9_reset = reset;
  assign n9_valid_up = n6_valid_down; // @[Top.scala 25:17]
  assign n9_I_0 = n6_O_0; // @[Top.scala 24:10]
  assign n10_valid_up = n9_valid_down; // @[Top.scala 28:18]
  assign n10_I_0 = n9_O_0; // @[Top.scala 27:11]
  assign n11_clock = clock;
  assign n11_reset = reset;
  assign n11_valid_up = n10_valid_down; // @[Top.scala 31:18]
  assign n11_I = n10_O; // @[Top.scala 30:11]
  assign n12_clock = clock;
  assign n12_reset = reset;
  assign n12_valid_up = n11_valid_down; // @[Top.scala 34:18]
  assign n12_I = n11_O; // @[Top.scala 33:11]
  assign n13_clock = clock;
  assign n13_reset = reset;
  assign n13_valid_up = n12_valid_down; // @[Top.scala 37:18]
  assign n13_I = n12_O; // @[Top.scala 36:11]
endmodule
