module Abs(
  input        clock,
  input        reset,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  reg [7:0] out_reg; // @[Arithmetic.scala 18:20]
  reg [31:0] _RAND_0;
  wire  _T = $signed(I) < 8'sh0; // @[Arithmetic.scala 19:29]
  wire [7:0] _T_3 = 8'sh0 - $signed(I); // @[Arithmetic.scala 19:53]
  reg  _T_4; // @[Arithmetic.scala 22:24]
  reg [31:0] _RAND_1;
  assign valid_down = _T_4; // @[Arithmetic.scala 22:14]
  assign O = out_reg; // @[Arithmetic.scala 21:5]
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
  out_reg = _RAND_0[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_4 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (_T) begin
      out_reg <= _T_3;
    end else begin
      out_reg <= I;
    end
    if (reset) begin
      _T_4 <= 1'h0;
    end else begin
      _T_4 <= 1'h1;
    end
  end
endmodule
module MapT(
  input        clock,
  input        reset,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I; // @[MapT.scala 8:20]
  wire [7:0] op_O; // @[MapT.scala 8:20]
  Abs op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_down(op_valid_down),
    .I(op_I),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_I = I; // @[MapT.scala 14:10]
endmodule
module Remove10T(
  input        clock,
  input        reset,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  wire  op_inst_clock; // @[Remove10T.scala 9:23]
  wire  op_inst_reset; // @[Remove10T.scala 9:23]
  wire  op_inst_valid_down; // @[Remove10T.scala 9:23]
  wire [7:0] op_inst_I; // @[Remove10T.scala 9:23]
  wire [7:0] op_inst_O; // @[Remove10T.scala 9:23]
  MapT op_inst ( // @[Remove10T.scala 9:23]
    .clock(op_inst_clock),
    .reset(op_inst_reset),
    .valid_down(op_inst_valid_down),
    .I(op_inst_I),
    .O(op_inst_O)
  );
  assign valid_down = op_inst_valid_down; // @[Remove10T.scala 16:14]
  assign O = op_inst_O; // @[Remove10T.scala 14:5]
  assign op_inst_clock = clock;
  assign op_inst_reset = reset;
  assign op_inst_I = I; // @[Remove10T.scala 13:13]
endmodule
module Top(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  output [7:0] O
);
  wire  n6_clock; // @[Top.scala 17:20]
  wire  n6_reset; // @[Top.scala 17:20]
  wire  n6_valid_down; // @[Top.scala 17:20]
  wire [7:0] n6_I; // @[Top.scala 17:20]
  wire [7:0] n6_O; // @[Top.scala 17:20]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_2 = value == 2'h2; // @[Counter.scala 38:24]
  wire [1:0] _T_4 = value + 2'h1; // @[Counter.scala 39:22]
  wire [6:0] _GEN_4 = 2'h1 == value ? $signed(7'sh0) : $signed(-7'sh2a); // @[Const.scala 25:72]
  wire [6:0] _GEN_5 = 2'h2 == value ? $signed(7'sh9) : $signed(_GEN_4); // @[Const.scala 25:72]
  Remove10T n6 ( // @[Top.scala 17:20]
    .clock(n6_clock),
    .reset(n6_reset),
    .valid_down(n6_valid_down),
    .I(n6_I),
    .O(n6_O)
  );
  assign valid_down = n6_valid_down; // @[Top.scala 21:16]
  assign O = n6_O; // @[Top.scala 20:7]
  assign n6_clock = clock;
  assign n6_reset = reset;
  assign n6_I = {{1{_GEN_5[6]}},_GEN_5}; // @[Top.scala 18:10]
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
  value = _RAND_0[1:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 2'h0;
    end else if (_T_2) begin
      value <= 2'h0;
    end else begin
      value <= _T_4;
    end
  end
endmodule
