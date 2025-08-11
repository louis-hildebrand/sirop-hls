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
module RAM_ST(
  input        clock,
  input        RE,
  output [7:0] RDATA,
  input        WE,
  input  [7:0] WDATA
);
  wire  write_elem_counter_CE; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_valid; // @[RAM_ST.scala 20:34]
  wire  read_elem_counter_CE; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_valid; // @[RAM_ST.scala 21:33]
  reg [7:0] ram [0:0]; // @[RAM_ST.scala 29:24]
  reg [31:0] _RAND_0;
  wire [7:0] ram__T_7_data; // @[RAM_ST.scala 29:24]
  wire  ram__T_7_addr; // @[RAM_ST.scala 29:24]
  wire [7:0] ram__T_3_data; // @[RAM_ST.scala 29:24]
  wire  ram__T_3_addr; // @[RAM_ST.scala 29:24]
  wire  ram__T_3_mask; // @[RAM_ST.scala 29:24]
  wire  ram__T_3_en; // @[RAM_ST.scala 29:24]
  reg  ram__T_7_en_pipe_0;
  reg [31:0] _RAND_1;
  reg  ram__T_7_addr_pipe_0;
  reg [31:0] _RAND_2;
  NestedCountersWithNumValid write_elem_counter ( // @[RAM_ST.scala 20:34]
    .CE(write_elem_counter_CE),
    .valid(write_elem_counter_valid)
  );
  NestedCountersWithNumValid read_elem_counter ( // @[RAM_ST.scala 21:33]
    .CE(read_elem_counter_CE),
    .valid(read_elem_counter_valid)
  );
  assign ram__T_7_addr = ram__T_7_addr_pipe_0;
  assign ram__T_7_data = ram[ram__T_7_addr]; // @[RAM_ST.scala 29:24]
  assign ram__T_3_data = WDATA;
  assign ram__T_3_addr = 1'h0;
  assign ram__T_3_mask = 1'h1;
  assign ram__T_3_en = write_elem_counter_valid;
  assign RDATA = ram__T_7_data; // @[RAM_ST.scala 32:9]
  assign write_elem_counter_CE = WE; // @[RAM_ST.scala 23:25]
  assign read_elem_counter_CE = RE; // @[RAM_ST.scala 24:24]
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
  for (initvar = 0; initvar < 1; initvar = initvar+1)
    ram[initvar] = _RAND_0[7:0];
  `endif // RANDOMIZE_MEM_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  ram__T_7_en_pipe_0 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  ram__T_7_addr_pipe_0 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if(ram__T_3_en & ram__T_3_mask) begin
      ram[ram__T_3_addr] <= ram__T_3_data; // @[RAM_ST.scala 29:24]
    end
    ram__T_7_en_pipe_0 <= read_elem_counter_valid;
    if (read_elem_counter_valid) begin
      ram__T_7_addr_pipe_0 <= 1'h0;
    end
  end
endmodule
module NestedCounters_4(
  input   clock,
  input   reset,
  input   CE,
  output  valid
);
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_2 = value == 2'h2; // @[Counter.scala 38:24]
  wire [1:0] _T_4 = value + 2'h1; // @[Counter.scala 39:22]
  assign valid = value < 2'h1; // @[NestedCounters.scala 48:11]
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
    end else if (CE) begin
      if (_T_2) begin
        value <= 2'h0;
      end else begin
        value <= _T_4;
      end
    end
  end
endmodule
module ShiftTN(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  wire  value_store_clock; // @[ShiftTN.scala 42:27]
  wire  value_store_RE; // @[ShiftTN.scala 42:27]
  wire [7:0] value_store_RDATA; // @[ShiftTN.scala 42:27]
  wire  value_store_WE; // @[ShiftTN.scala 42:27]
  wire [7:0] value_store_WDATA; // @[ShiftTN.scala 42:27]
  wire  next_ram_addr_CE; // @[ShiftTN.scala 44:29]
  wire  next_ram_addr_valid; // @[ShiftTN.scala 44:29]
  wire  inner_valid_clock; // @[ShiftTN.scala 56:27]
  wire  inner_valid_reset; // @[ShiftTN.scala 56:27]
  wire  inner_valid_CE; // @[ShiftTN.scala 56:27]
  wire  inner_valid_valid; // @[ShiftTN.scala 56:27]
  RAM_ST value_store ( // @[ShiftTN.scala 42:27]
    .clock(value_store_clock),
    .RE(value_store_RE),
    .RDATA(value_store_RDATA),
    .WE(value_store_WE),
    .WDATA(value_store_WDATA)
  );
  NestedCounters next_ram_addr ( // @[ShiftTN.scala 44:29]
    .CE(next_ram_addr_CE),
    .valid(next_ram_addr_valid)
  );
  NestedCounters_4 inner_valid ( // @[ShiftTN.scala 56:27]
    .clock(inner_valid_clock),
    .reset(inner_valid_reset),
    .CE(inner_valid_CE),
    .valid(inner_valid_valid)
  );
  assign valid_down = valid_up; // @[ShiftTN.scala 69:14]
  assign O = value_store_RDATA; // @[ShiftTN.scala 66:5]
  assign value_store_clock = clock;
  assign value_store_RE = valid_up & inner_valid_valid; // @[ShiftTN.scala 64:18]
  assign value_store_WE = valid_up & inner_valid_valid; // @[ShiftTN.scala 63:18]
  assign value_store_WDATA = I; // @[ShiftTN.scala 65:21]
  assign next_ram_addr_CE = valid_up; // @[ShiftTN.scala 45:20]
  assign inner_valid_clock = clock;
  assign inner_valid_reset = reset;
  assign inner_valid_CE = valid_up; // @[ShiftTN.scala 57:18]
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
module Map2T(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O_0,
  output [7:0] O_1
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1; // @[Map2T.scala 8:20]
  SSeqTupleCreator op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0(op_I0),
    .I1(op_I1),
    .O_0(op_O_0),
    .O_1(op_O_1)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0 = op_O_0; // @[Map2T.scala 17:7]
  assign O_1 = op_O_1; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0 = I0; // @[Map2T.scala 15:11]
  assign op_I1 = I1; // @[Map2T.scala 16:11]
endmodule
module Map2T_1(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O_0,
  output [7:0] O_1
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1; // @[Map2T.scala 8:20]
  Map2T op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0(op_I0),
    .I1(op_I1),
    .O_0(op_O_0),
    .O_1(op_O_1)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0 = op_O_0; // @[Map2T.scala 17:7]
  assign O_1 = op_O_1; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0 = I0; // @[Map2T.scala 15:11]
  assign op_I1 = I1; // @[Map2T.scala 16:11]
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
module Map2T_2(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I0_1,
  input  [7:0] I1,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_2; // @[Map2T.scala 8:20]
  SSeqTupleAppender op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0(op_I0_0),
    .I0_1(op_I0_1),
    .I1(op_I1),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0 = op_O_0; // @[Map2T.scala 17:7]
  assign O_1 = op_O_1; // @[Map2T.scala 17:7]
  assign O_2 = op_O_2; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0 = I0_0; // @[Map2T.scala 15:11]
  assign op_I0_1 = I0_1; // @[Map2T.scala 15:11]
  assign op_I1 = I1; // @[Map2T.scala 16:11]
endmodule
module Map2T_3(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I0_1,
  input  [7:0] I1,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_2; // @[Map2T.scala 8:20]
  Map2T_2 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0(op_I0_0),
    .I0_1(op_I0_1),
    .I1(op_I1),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0 = op_O_0; // @[Map2T.scala 17:7]
  assign O_1 = op_O_1; // @[Map2T.scala 17:7]
  assign O_2 = op_O_2; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0 = I0_0; // @[Map2T.scala 15:11]
  assign op_I0_1 = I0_1; // @[Map2T.scala 15:11]
  assign op_I1 = I1; // @[Map2T.scala 16:11]
endmodule
module Serialize(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O
);
  wire  elem_counter_CE; // @[Serialize.scala 14:28]
  wire  elem_counter_valid; // @[Serialize.scala 14:28]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_2 = value == 2'h2; // @[Counter.scala 38:24]
  wire [1:0] _T_4 = value + 2'h1; // @[Counter.scala 39:22]
  reg [7:0] mux_input_wire_1; // @[Serialize.scala 39:47]
  reg [31:0] _RAND_1;
  reg [7:0] mux_input_wire_2; // @[Serialize.scala 39:47]
  reg [31:0] _RAND_2;
  wire  _T_7 = value == 2'h0; // @[Serialize.scala 42:34]
  wire  _T_8 = _T_7 & valid_up; // @[Serialize.scala 42:42]
  wire  _T_11 = 2'h2 == value; // @[Mux.scala 68:19]
  wire  _T_13 = 2'h1 == value; // @[Mux.scala 68:19]
  wire  _T_15 = 2'h0 == value; // @[Mux.scala 68:19]
  reg [7:0] _T_17; // @[Serialize.scala 53:15]
  reg [31:0] _RAND_3;
  reg  _T_18; // @[Serialize.scala 54:24]
  reg [31:0] _RAND_4;
  NestedCountersWithNumValid elem_counter ( // @[Serialize.scala 14:28]
    .CE(elem_counter_CE),
    .valid(elem_counter_valid)
  );
  assign valid_down = _T_18; // @[Serialize.scala 54:14]
  assign O = _T_17; // @[Serialize.scala 53:5]
  assign elem_counter_CE = valid_up; // @[Serialize.scala 16:19]
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
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  mux_input_wire_1 = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  mux_input_wire_2 = _RAND_2[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_17 = _RAND_3[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_18 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 2'h0;
    end else if (valid_up) begin
      if (_T_2) begin
        value <= 2'h0;
      end else begin
        value <= _T_4;
      end
    end
    if (_T_8) begin
      mux_input_wire_1 <= I_1;
    end
    if (_T_8) begin
      mux_input_wire_2 <= I_2;
    end
    if (_T_15) begin
      _T_17 <= I_0;
    end else if (_T_13) begin
      _T_17 <= mux_input_wire_1;
    end else if (_T_11) begin
      _T_17 <= mux_input_wire_2;
    end else begin
      _T_17 <= I_0;
    end
    if (reset) begin
      _T_18 <= 1'h0;
    end else begin
      _T_18 <= valid_up;
    end
  end
endmodule
module MapT(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_2; // @[MapT.scala 8:20]
  wire [7:0] op_O; // @[MapT.scala 8:20]
  Serialize op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0(op_I_0),
    .I_1(op_I_1),
    .I_2(op_I_2),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0 = I_0; // @[MapT.scala 14:10]
  assign op_I_1 = I_1; // @[MapT.scala 14:10]
  assign op_I_2 = I_2; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [1:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 2'h2; // @[InitialDelayCounter.scala 17:17]
  wire [1:0] _T_4 = value + 2'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 2'h2; // @[InitialDelayCounter.scala 16:16]
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
  wire  n27_valid_up; // @[Top.scala 18:21]
  wire  n27_valid_down; // @[Top.scala 18:21]
  wire [7:0] n27_I0; // @[Top.scala 18:21]
  wire [7:0] n27_I1; // @[Top.scala 18:21]
  wire [7:0] n27_O_t0b; // @[Top.scala 18:21]
  wire [7:0] n27_O_t1b; // @[Top.scala 18:21]
  wire  n28_clock; // @[Top.scala 22:21]
  wire  n28_reset; // @[Top.scala 22:21]
  wire  n28_valid_up; // @[Top.scala 22:21]
  wire  n28_valid_down; // @[Top.scala 22:21]
  wire [7:0] n28_I_t0b; // @[Top.scala 22:21]
  wire [7:0] n28_I_t1b; // @[Top.scala 22:21]
  wire [7:0] n28_O; // @[Top.scala 22:21]
  AtomTuple n27 ( // @[Top.scala 18:21]
    .valid_up(n27_valid_up),
    .valid_down(n27_valid_down),
    .I0(n27_I0),
    .I1(n27_I1),
    .O_t0b(n27_O_t0b),
    .O_t1b(n27_O_t1b)
  );
  Mul n28 ( // @[Top.scala 22:21]
    .clock(n28_clock),
    .reset(n28_reset),
    .valid_up(n28_valid_up),
    .valid_down(n28_valid_down),
    .I_t0b(n28_I_t0b),
    .I_t1b(n28_I_t1b),
    .O(n28_O)
  );
  assign valid_down = n28_valid_down; // @[Top.scala 26:16]
  assign O = n28_O; // @[Top.scala 25:7]
  assign n27_valid_up = valid_up; // @[Top.scala 21:18]
  assign n27_I0 = I0; // @[Top.scala 19:12]
  assign n27_I1 = I1; // @[Top.scala 20:12]
  assign n28_clock = clock;
  assign n28_reset = reset;
  assign n28_valid_up = n27_valid_down; // @[Top.scala 24:18]
  assign n28_I_t0b = n27_O_t0b; // @[Top.scala 23:11]
  assign n28_I_t1b = n27_O_t1b; // @[Top.scala 23:11]
endmodule
module Map2T_4(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O
);
  wire  op_clock; // @[Map2T.scala 8:20]
  wire  op_reset; // @[Map2T.scala 8:20]
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1; // @[Map2T.scala 8:20]
  wire [7:0] op_O; // @[Map2T.scala 8:20]
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
module AddNoValid(
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  assign O = $signed(I_t0b) + $signed(I_t1b); // @[Arithmetic.scala 119:7]
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
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire  _T_3 = value == 2'h2; // @[Counter.scala 38:24]
  wire [1:0] _T_5 = value + 2'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value == 2'h0; // @[ReduceT.scala 34:60]
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
  value = _RAND_1[1:0];
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
      value <= 2'h0;
    end else if (_T) begin
      if (_T_3) begin
        value <= 2'h0;
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
module Module_1(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n24_clock; // @[Top.scala 33:21]
  wire  n24_reset; // @[Top.scala 33:21]
  wire  n24_valid_up; // @[Top.scala 33:21]
  wire  n24_valid_down; // @[Top.scala 33:21]
  wire [7:0] n24_I0; // @[Top.scala 33:21]
  wire [7:0] n24_I1; // @[Top.scala 33:21]
  wire [7:0] n24_O; // @[Top.scala 33:21]
  wire  n31_clock; // @[Top.scala 37:21]
  wire  n31_reset; // @[Top.scala 37:21]
  wire  n31_valid_up; // @[Top.scala 37:21]
  wire  n31_valid_down; // @[Top.scala 37:21]
  wire [7:0] n31_I; // @[Top.scala 37:21]
  wire [7:0] n31_O; // @[Top.scala 37:21]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_2 = value == 2'h2; // @[Counter.scala 38:24]
  wire [1:0] _T_4 = value + 2'h1; // @[Counter.scala 39:22]
  wire [1:0] _GEN_4 = 2'h1 == value ? $signed(2'sh0) : $signed(-2'sh1); // @[Const.scala 25:72]
  wire [1:0] _GEN_5 = 2'h2 == value ? $signed(2'sh1) : $signed(_GEN_4); // @[Const.scala 25:72]
  InitialDelayCounter InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Map2T_4 n24 ( // @[Top.scala 33:21]
    .clock(n24_clock),
    .reset(n24_reset),
    .valid_up(n24_valid_up),
    .valid_down(n24_valid_down),
    .I0(n24_I0),
    .I1(n24_I1),
    .O(n24_O)
  );
  ReduceT n31 ( // @[Top.scala 37:21]
    .clock(n31_clock),
    .reset(n31_reset),
    .valid_up(n31_valid_up),
    .valid_down(n31_valid_down),
    .I(n31_I),
    .O(n31_O)
  );
  assign valid_down = n31_valid_down; // @[Top.scala 41:16]
  assign O = n31_O; // @[Top.scala 40:7]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n24_clock = clock;
  assign n24_reset = reset;
  assign n24_valid_up = valid_up & InitialDelayCounter_valid_down; // @[Top.scala 36:18]
  assign n24_I0 = I; // @[Top.scala 34:12]
  assign n24_I1 = {{6{_GEN_5[1]}},_GEN_5}; // @[Top.scala 35:12]
  assign n31_clock = clock;
  assign n31_reset = reset;
  assign n31_valid_up = n24_valid_down; // @[Top.scala 39:18]
  assign n31_I = n24_O; // @[Top.scala 38:11]
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
    end else if (InitialDelayCounter_valid_down) begin
      if (_T_2) begin
        value <= 2'h0;
      end else begin
        value <= _T_4;
      end
    end
  end
endmodule
module MapT_1(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I; // @[MapT.scala 8:20]
  wire [7:0] op_O; // @[MapT.scala 8:20]
  Module_1 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I(op_I),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I = I; // @[MapT.scala 14:10]
endmodule
module Top(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  wire  n1_clock; // @[Top.scala 47:20]
  wire  n1_reset; // @[Top.scala 47:20]
  wire  n1_valid_up; // @[Top.scala 47:20]
  wire  n1_valid_down; // @[Top.scala 47:20]
  wire [7:0] n1_I; // @[Top.scala 47:20]
  wire [7:0] n1_O; // @[Top.scala 47:20]
  wire  n2_clock; // @[Top.scala 50:20]
  wire  n2_reset; // @[Top.scala 50:20]
  wire  n2_valid_up; // @[Top.scala 50:20]
  wire  n2_valid_down; // @[Top.scala 50:20]
  wire [7:0] n2_I; // @[Top.scala 50:20]
  wire [7:0] n2_O; // @[Top.scala 50:20]
  wire  n3_clock; // @[Top.scala 53:20]
  wire  n3_reset; // @[Top.scala 53:20]
  wire  n3_valid_up; // @[Top.scala 53:20]
  wire  n3_valid_down; // @[Top.scala 53:20]
  wire [7:0] n3_I; // @[Top.scala 53:20]
  wire [7:0] n3_O; // @[Top.scala 53:20]
  wire  n4_valid_up; // @[Top.scala 56:20]
  wire  n4_valid_down; // @[Top.scala 56:20]
  wire [7:0] n4_I0; // @[Top.scala 56:20]
  wire [7:0] n4_I1; // @[Top.scala 56:20]
  wire [7:0] n4_O_0; // @[Top.scala 56:20]
  wire [7:0] n4_O_1; // @[Top.scala 56:20]
  wire  n11_valid_up; // @[Top.scala 60:21]
  wire  n11_valid_down; // @[Top.scala 60:21]
  wire [7:0] n11_I0_0; // @[Top.scala 60:21]
  wire [7:0] n11_I0_1; // @[Top.scala 60:21]
  wire [7:0] n11_I1; // @[Top.scala 60:21]
  wire [7:0] n11_O_0; // @[Top.scala 60:21]
  wire [7:0] n11_O_1; // @[Top.scala 60:21]
  wire [7:0] n11_O_2; // @[Top.scala 60:21]
  wire  n20_clock; // @[Top.scala 64:21]
  wire  n20_reset; // @[Top.scala 64:21]
  wire  n20_valid_up; // @[Top.scala 64:21]
  wire  n20_valid_down; // @[Top.scala 64:21]
  wire [7:0] n20_I_0; // @[Top.scala 64:21]
  wire [7:0] n20_I_1; // @[Top.scala 64:21]
  wire [7:0] n20_I_2; // @[Top.scala 64:21]
  wire [7:0] n20_O; // @[Top.scala 64:21]
  wire  n32_clock; // @[Top.scala 67:21]
  wire  n32_reset; // @[Top.scala 67:21]
  wire  n32_valid_up; // @[Top.scala 67:21]
  wire  n32_valid_down; // @[Top.scala 67:21]
  wire [7:0] n32_I; // @[Top.scala 67:21]
  wire [7:0] n32_O; // @[Top.scala 67:21]
  wire  n33_clock; // @[Top.scala 70:21]
  wire  n33_reset; // @[Top.scala 70:21]
  wire  n33_valid_up; // @[Top.scala 70:21]
  wire  n33_valid_down; // @[Top.scala 70:21]
  wire [7:0] n33_I; // @[Top.scala 70:21]
  wire [7:0] n33_O; // @[Top.scala 70:21]
  wire  n34_clock; // @[Top.scala 73:21]
  wire  n34_reset; // @[Top.scala 73:21]
  wire  n34_valid_up; // @[Top.scala 73:21]
  wire  n34_valid_down; // @[Top.scala 73:21]
  wire [7:0] n34_I; // @[Top.scala 73:21]
  wire [7:0] n34_O; // @[Top.scala 73:21]
  wire  n35_clock; // @[Top.scala 76:21]
  wire  n35_reset; // @[Top.scala 76:21]
  wire  n35_valid_up; // @[Top.scala 76:21]
  wire  n35_valid_down; // @[Top.scala 76:21]
  wire [7:0] n35_I; // @[Top.scala 76:21]
  wire [7:0] n35_O; // @[Top.scala 76:21]
  FIFO n1 ( // @[Top.scala 47:20]
    .clock(n1_clock),
    .reset(n1_reset),
    .valid_up(n1_valid_up),
    .valid_down(n1_valid_down),
    .I(n1_I),
    .O(n1_O)
  );
  ShiftTN n2 ( // @[Top.scala 50:20]
    .clock(n2_clock),
    .reset(n2_reset),
    .valid_up(n2_valid_up),
    .valid_down(n2_valid_down),
    .I(n2_I),
    .O(n2_O)
  );
  ShiftTN n3 ( // @[Top.scala 53:20]
    .clock(n3_clock),
    .reset(n3_reset),
    .valid_up(n3_valid_up),
    .valid_down(n3_valid_down),
    .I(n3_I),
    .O(n3_O)
  );
  Map2T_1 n4 ( // @[Top.scala 56:20]
    .valid_up(n4_valid_up),
    .valid_down(n4_valid_down),
    .I0(n4_I0),
    .I1(n4_I1),
    .O_0(n4_O_0),
    .O_1(n4_O_1)
  );
  Map2T_3 n11 ( // @[Top.scala 60:21]
    .valid_up(n11_valid_up),
    .valid_down(n11_valid_down),
    .I0_0(n11_I0_0),
    .I0_1(n11_I0_1),
    .I1(n11_I1),
    .O_0(n11_O_0),
    .O_1(n11_O_1),
    .O_2(n11_O_2)
  );
  MapT n20 ( // @[Top.scala 64:21]
    .clock(n20_clock),
    .reset(n20_reset),
    .valid_up(n20_valid_up),
    .valid_down(n20_valid_down),
    .I_0(n20_I_0),
    .I_1(n20_I_1),
    .I_2(n20_I_2),
    .O(n20_O)
  );
  MapT_1 n32 ( // @[Top.scala 67:21]
    .clock(n32_clock),
    .reset(n32_reset),
    .valid_up(n32_valid_up),
    .valid_down(n32_valid_down),
    .I(n32_I),
    .O(n32_O)
  );
  FIFO n33 ( // @[Top.scala 70:21]
    .clock(n33_clock),
    .reset(n33_reset),
    .valid_up(n33_valid_up),
    .valid_down(n33_valid_down),
    .I(n33_I),
    .O(n33_O)
  );
  FIFO n34 ( // @[Top.scala 73:21]
    .clock(n34_clock),
    .reset(n34_reset),
    .valid_up(n34_valid_up),
    .valid_down(n34_valid_down),
    .I(n34_I),
    .O(n34_O)
  );
  FIFO n35 ( // @[Top.scala 76:21]
    .clock(n35_clock),
    .reset(n35_reset),
    .valid_up(n35_valid_up),
    .valid_down(n35_valid_down),
    .I(n35_I),
    .O(n35_O)
  );
  assign valid_down = n35_valid_down; // @[Top.scala 80:16]
  assign O = n35_O; // @[Top.scala 79:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 49:17]
  assign n1_I = I; // @[Top.scala 48:10]
  assign n2_clock = clock;
  assign n2_reset = reset;
  assign n2_valid_up = n1_valid_down; // @[Top.scala 52:17]
  assign n2_I = n1_O; // @[Top.scala 51:10]
  assign n3_clock = clock;
  assign n3_reset = reset;
  assign n3_valid_up = n2_valid_down; // @[Top.scala 55:17]
  assign n3_I = n2_O; // @[Top.scala 54:10]
  assign n4_valid_up = n3_valid_down & n2_valid_down; // @[Top.scala 59:17]
  assign n4_I0 = n3_O; // @[Top.scala 57:11]
  assign n4_I1 = n2_O; // @[Top.scala 58:11]
  assign n11_valid_up = n4_valid_down & n1_valid_down; // @[Top.scala 63:18]
  assign n11_I0_0 = n4_O_0; // @[Top.scala 61:12]
  assign n11_I0_1 = n4_O_1; // @[Top.scala 61:12]
  assign n11_I1 = n1_O; // @[Top.scala 62:12]
  assign n20_clock = clock;
  assign n20_reset = reset;
  assign n20_valid_up = n11_valid_down; // @[Top.scala 66:18]
  assign n20_I_0 = n11_O_0; // @[Top.scala 65:11]
  assign n20_I_1 = n11_O_1; // @[Top.scala 65:11]
  assign n20_I_2 = n11_O_2; // @[Top.scala 65:11]
  assign n32_clock = clock;
  assign n32_reset = reset;
  assign n32_valid_up = n20_valid_down; // @[Top.scala 69:18]
  assign n32_I = n20_O; // @[Top.scala 68:11]
  assign n33_clock = clock;
  assign n33_reset = reset;
  assign n33_valid_up = n32_valid_down; // @[Top.scala 72:18]
  assign n33_I = n32_O; // @[Top.scala 71:11]
  assign n34_clock = clock;
  assign n34_reset = reset;
  assign n34_valid_up = n33_valid_down; // @[Top.scala 75:18]
  assign n34_I = n33_O; // @[Top.scala 74:11]
  assign n35_clock = clock;
  assign n35_reset = reset;
  assign n35_valid_up = n34_valid_down; // @[Top.scala 78:18]
  assign n35_I = n34_O; // @[Top.scala 77:11]
endmodule
