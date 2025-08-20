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
  input  [1:0] RADDR,
  output [7:0] RDATA,
  input        WE,
  input  [1:0] WADDR,
  input  [7:0] WDATA
);
  wire  write_elem_counter_CE; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_valid; // @[RAM_ST.scala 20:34]
  wire  read_elem_counter_CE; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_valid; // @[RAM_ST.scala 21:33]
  reg [7:0] ram [0:3]; // @[RAM_ST.scala 29:24]
  reg [31:0] _RAND_0;
  wire [7:0] ram__T_8_data; // @[RAM_ST.scala 29:24]
  wire [1:0] ram__T_8_addr; // @[RAM_ST.scala 29:24]
  wire [7:0] ram__T_2_data; // @[RAM_ST.scala 29:24]
  wire [1:0] ram__T_2_addr; // @[RAM_ST.scala 29:24]
  wire  ram__T_2_mask; // @[RAM_ST.scala 29:24]
  wire  ram__T_2_en; // @[RAM_ST.scala 29:24]
  reg  ram__T_8_en_pipe_0;
  reg [31:0] _RAND_1;
  reg [1:0] ram__T_8_addr_pipe_0;
  reg [31:0] _RAND_2;
  wire [1:0] _GEN_1 = 2'h1 == WADDR ? 2'h1 : 2'h0; // @[RAM_ST.scala 31:71]
  wire [1:0] _GEN_2 = 2'h2 == WADDR ? 2'h2 : _GEN_1; // @[RAM_ST.scala 31:71]
  wire [1:0] _GEN_3 = 2'h3 == WADDR ? 2'h3 : _GEN_2; // @[RAM_ST.scala 31:71]
  wire [2:0] _T = {{1'd0}, _GEN_3}; // @[RAM_ST.scala 31:71]
  wire [1:0] _GEN_10 = 2'h1 == RADDR ? 2'h1 : 2'h0; // @[RAM_ST.scala 32:46]
  wire [1:0] _GEN_11 = 2'h2 == RADDR ? 2'h2 : _GEN_10; // @[RAM_ST.scala 32:46]
  wire [1:0] _GEN_12 = 2'h3 == RADDR ? 2'h3 : _GEN_11; // @[RAM_ST.scala 32:46]
  wire [2:0] _T_3 = {{1'd0}, _GEN_12}; // @[RAM_ST.scala 32:46]
  NestedCountersWithNumValid write_elem_counter ( // @[RAM_ST.scala 20:34]
    .CE(write_elem_counter_CE),
    .valid(write_elem_counter_valid)
  );
  NestedCountersWithNumValid read_elem_counter ( // @[RAM_ST.scala 21:33]
    .CE(read_elem_counter_CE),
    .valid(read_elem_counter_valid)
  );
  assign ram__T_8_addr = ram__T_8_addr_pipe_0;
  assign ram__T_8_data = ram[ram__T_8_addr]; // @[RAM_ST.scala 29:24]
  assign ram__T_2_data = WDATA;
  assign ram__T_2_addr = _T[1:0];
  assign ram__T_2_mask = 1'h1;
  assign ram__T_2_en = write_elem_counter_valid;
  assign RDATA = ram__T_8_data; // @[RAM_ST.scala 32:9]
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
  for (initvar = 0; initvar < 4; initvar = initvar+1)
    ram[initvar] = _RAND_0[7:0];
  `endif // RANDOMIZE_MEM_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  ram__T_8_en_pipe_0 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  ram__T_8_addr_pipe_0 = _RAND_2[1:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if(ram__T_2_en & ram__T_2_mask) begin
      ram[ram__T_2_addr] <= ram__T_2_data; // @[RAM_ST.scala 29:24]
    end
    ram__T_8_en_pipe_0 <= read_elem_counter_valid;
    if (read_elem_counter_valid) begin
      ram__T_8_addr_pipe_0 <= _T_3[1:0];
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
  wire [1:0] value_store_RADDR; // @[ShiftTN.scala 42:27]
  wire [7:0] value_store_RDATA; // @[ShiftTN.scala 42:27]
  wire  value_store_WE; // @[ShiftTN.scala 42:27]
  wire [1:0] value_store_WADDR; // @[ShiftTN.scala 42:27]
  wire [7:0] value_store_WDATA; // @[ShiftTN.scala 42:27]
  wire  next_ram_addr_CE; // @[ShiftTN.scala 44:29]
  wire  next_ram_addr_valid; // @[ShiftTN.scala 44:29]
  wire  inner_valid_clock; // @[ShiftTN.scala 56:27]
  wire  inner_valid_reset; // @[ShiftTN.scala 56:27]
  wire  inner_valid_CE; // @[ShiftTN.scala 56:27]
  wire  inner_valid_valid; // @[ShiftTN.scala 56:27]
  wire  _T_2 = valid_up & inner_valid_valid; // @[ShiftTN.scala 58:74]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_4 = value == 2'h3; // @[Counter.scala 38:24]
  wire [1:0] _T_6 = value + 2'h1; // @[Counter.scala 39:22]
  RAM_ST value_store ( // @[ShiftTN.scala 42:27]
    .clock(value_store_clock),
    .RE(value_store_RE),
    .RADDR(value_store_RADDR),
    .RDATA(value_store_RDATA),
    .WE(value_store_WE),
    .WADDR(value_store_WADDR),
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
  assign value_store_RADDR = _T_4 ? 2'h0 : _T_6; // @[ShiftTN.scala 61:74 ShiftTN.scala 62:36]
  assign value_store_WE = valid_up & inner_valid_valid; // @[ShiftTN.scala 63:18]
  assign value_store_WADDR = value; // @[ShiftTN.scala 60:21]
  assign value_store_WDATA = I; // @[ShiftTN.scala 65:21]
  assign next_ram_addr_CE = valid_up; // @[ShiftTN.scala 45:20]
  assign inner_valid_clock = clock;
  assign inner_valid_reset = reset;
  assign inner_valid_CE = valid_up; // @[ShiftTN.scala 57:18]
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
      value <= _T_6;
    end
  end
endmodule
module RAM_ST_2(
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
  wire [7:0] ram__T_6_data; // @[RAM_ST.scala 29:24]
  wire  ram__T_6_addr; // @[RAM_ST.scala 29:24]
  wire [7:0] ram__T_2_data; // @[RAM_ST.scala 29:24]
  wire  ram__T_2_addr; // @[RAM_ST.scala 29:24]
  wire  ram__T_2_mask; // @[RAM_ST.scala 29:24]
  wire  ram__T_2_en; // @[RAM_ST.scala 29:24]
  reg  ram__T_6_en_pipe_0;
  reg [31:0] _RAND_1;
  reg  ram__T_6_addr_pipe_0;
  reg [31:0] _RAND_2;
  NestedCountersWithNumValid write_elem_counter ( // @[RAM_ST.scala 20:34]
    .CE(write_elem_counter_CE),
    .valid(write_elem_counter_valid)
  );
  NestedCountersWithNumValid read_elem_counter ( // @[RAM_ST.scala 21:33]
    .CE(read_elem_counter_CE),
    .valid(read_elem_counter_valid)
  );
  assign ram__T_6_addr = ram__T_6_addr_pipe_0;
  assign ram__T_6_data = ram[ram__T_6_addr]; // @[RAM_ST.scala 29:24]
  assign ram__T_2_data = WDATA;
  assign ram__T_2_addr = 1'h0;
  assign ram__T_2_mask = 1'h1;
  assign ram__T_2_en = write_elem_counter_valid;
  assign RDATA = ram__T_6_data; // @[RAM_ST.scala 32:9]
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
  ram__T_6_en_pipe_0 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  ram__T_6_addr_pipe_0 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if(ram__T_2_en & ram__T_2_mask) begin
      ram[ram__T_2_addr] <= ram__T_2_data; // @[RAM_ST.scala 29:24]
    end
    ram__T_6_en_pipe_0 <= read_elem_counter_valid;
    if (read_elem_counter_valid) begin
      ram__T_6_addr_pipe_0 <= 1'h0;
    end
  end
endmodule
module ShiftTN_2(
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
  RAM_ST_2 value_store ( // @[ShiftTN.scala 42:27]
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
module Passthrough(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O_0_0,
  output [7:0] O_0_1,
  output [7:0] O_0_2
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_0_0 = I_0; // @[Passthrough.scala 17:68]
  assign O_0_1 = I_1; // @[Passthrough.scala 17:68]
  assign O_0_2 = I_2; // @[Passthrough.scala 17:68]
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
module MapS(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_0_1,
  input  [7:0] I_0_2,
  output [7:0] O_0
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_0; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_1; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_2; // @[MapS.scala 9:22]
  wire [7:0] fst_op_O; // @[MapS.scala 9:22]
  Serialize fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_0(fst_op_I_0),
    .I_1(fst_op_I_1),
    .I_2(fst_op_I_2),
    .O(fst_op_O)
  );
  assign valid_down = fst_op_valid_down; // @[MapS.scala 23:14]
  assign O_0 = fst_op_O; // @[MapS.scala 17:8]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_0 = I_0_0; // @[MapS.scala 16:12]
  assign fst_op_I_1 = I_0_1; // @[MapS.scala 16:12]
  assign fst_op_I_2 = I_0_2; // @[MapS.scala 16:12]
endmodule
module MapT(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_0_1,
  input  [7:0] I_0_2,
  output [7:0] O_0
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_0; // @[MapT.scala 8:20]
  MapS op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_0(op_I_0_0),
    .I_0_1(op_I_0_1),
    .I_0_2(op_I_0_2),
    .O_0(op_O_0)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0 = op_O_0; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_0 = I_0_0; // @[MapT.scala 14:10]
  assign op_I_0_1 = I_0_1; // @[MapT.scala 14:10]
  assign op_I_0_2 = I_0_2; // @[MapT.scala 14:10]
endmodule
module Map2S(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I1_0,
  output [7:0] O_0_0,
  output [7:0] O_0_1
);
  wire  fst_op_valid_up; // @[Map2S.scala 9:22]
  wire  fst_op_valid_down; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I0; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I1; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_0; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_1; // @[Map2S.scala 9:22]
  SSeqTupleCreator fst_op ( // @[Map2S.scala 9:22]
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I0(fst_op_I0),
    .I1(fst_op_I1),
    .O_0(fst_op_O_0),
    .O_1(fst_op_O_1)
  );
  assign valid_down = fst_op_valid_down; // @[Map2S.scala 26:14]
  assign O_0_0 = fst_op_O_0; // @[Map2S.scala 19:8]
  assign O_0_1 = fst_op_O_1; // @[Map2S.scala 19:8]
  assign fst_op_valid_up = valid_up; // @[Map2S.scala 16:19]
  assign fst_op_I0 = I0_0; // @[Map2S.scala 17:13]
  assign fst_op_I1 = I1_0; // @[Map2S.scala 18:13]
endmodule
module Map2T_8(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I1_0,
  output [7:0] O_0_0,
  output [7:0] O_0_1
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_1; // @[Map2T.scala 8:20]
  Map2S op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0(op_I0_0),
    .I1_0(op_I1_0),
    .O_0_0(op_O_0_0),
    .O_0_1(op_O_0_1)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0_0 = op_O_0_0; // @[Map2T.scala 17:7]
  assign O_0_1 = op_O_0_1; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0 = I0_0; // @[Map2T.scala 15:11]
  assign op_I1_0 = I1_0; // @[Map2T.scala 16:11]
endmodule
module Map2S_1(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0_0,
  input  [7:0] I0_0_1,
  input  [7:0] I1_0,
  output [7:0] O_0_0,
  output [7:0] O_0_1,
  output [7:0] O_0_2
);
  wire  fst_op_valid_up; // @[Map2S.scala 9:22]
  wire  fst_op_valid_down; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I0_0; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I0_1; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I1; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_0; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_1; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_2; // @[Map2S.scala 9:22]
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
  assign valid_down = fst_op_valid_down; // @[Map2S.scala 26:14]
  assign O_0_0 = fst_op_O_0; // @[Map2S.scala 19:8]
  assign O_0_1 = fst_op_O_1; // @[Map2S.scala 19:8]
  assign O_0_2 = fst_op_O_2; // @[Map2S.scala 19:8]
  assign fst_op_valid_up = valid_up; // @[Map2S.scala 16:19]
  assign fst_op_I0_0 = I0_0_0; // @[Map2S.scala 17:13]
  assign fst_op_I0_1 = I0_0_1; // @[Map2S.scala 17:13]
  assign fst_op_I1 = I1_0; // @[Map2S.scala 18:13]
endmodule
module Map2T_13(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0_0,
  input  [7:0] I0_0_1,
  input  [7:0] I1_0,
  output [7:0] O_0_0,
  output [7:0] O_0_1,
  output [7:0] O_0_2
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_0_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_2; // @[Map2T.scala 8:20]
  Map2S_1 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0_0(op_I0_0_0),
    .I0_0_1(op_I0_0_1),
    .I1_0(op_I1_0),
    .O_0_0(op_O_0_0),
    .O_0_1(op_O_0_1),
    .O_0_2(op_O_0_2)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0_0 = op_O_0_0; // @[Map2T.scala 17:7]
  assign O_0_1 = op_O_0_1; // @[Map2T.scala 17:7]
  assign O_0_2 = op_O_0_2; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0_0 = I0_0_0; // @[Map2T.scala 15:11]
  assign op_I0_0_1 = I0_0_1; // @[Map2T.scala 15:11]
  assign op_I1_0 = I1_0; // @[Map2T.scala 16:11]
endmodule
module Passthrough_3(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_0_1,
  input  [7:0] I_0_2,
  output [7:0] O_0_0,
  output [7:0] O_0_1,
  output [7:0] O_0_2
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_0_0 = I_0_0; // @[Passthrough.scala 17:68]
  assign O_0_1 = I_0_1; // @[Passthrough.scala 17:68]
  assign O_0_2 = I_0_2; // @[Passthrough.scala 17:68]
endmodule
module Passthrough_4(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_0_1,
  input  [7:0] I_0_2,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_0 = I_0_0; // @[Passthrough.scala 17:68]
  assign O_1 = I_0_1; // @[Passthrough.scala 17:68]
  assign O_2 = I_0_2; // @[Passthrough.scala 17:68]
endmodule
module MapT_3(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_0_1,
  input  [7:0] I_0_2,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_1; // @[MapT.scala 8:20]
  wire [7:0] op_O_2; // @[MapT.scala 8:20]
  Passthrough_4 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_0(op_I_0_0),
    .I_0_1(op_I_0_1),
    .I_0_2(op_I_0_2),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0 = op_O_0; // @[MapT.scala 15:7]
  assign O_1 = op_O_1; // @[MapT.scala 15:7]
  assign O_2 = op_O_2; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_0 = I_0_0; // @[MapT.scala 14:10]
  assign op_I_0_1 = I_0_1; // @[MapT.scala 14:10]
  assign op_I_0_2 = I_0_2; // @[MapT.scala 14:10]
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
module Map2T_14(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O_t0b,
  output [7:0] O_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_t0b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_t1b; // @[Map2T.scala 8:20]
  AtomTuple op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0(op_I0),
    .I1(op_I1),
    .O_t0b(op_O_t0b),
    .O_t1b(op_O_t1b)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_t0b = op_O_t0b; // @[Map2T.scala 17:7]
  assign O_t1b = op_O_t1b; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0 = I0; // @[Map2T.scala 15:11]
  assign op_I1 = I1; // @[Map2T.scala 16:11]
endmodule
module Map2S_2(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I0_1,
  input  [7:0] I0_2,
  input  [7:0] I1_0,
  input  [7:0] I1_1,
  input  [7:0] I1_2,
  output [7:0] O_0_t0b,
  output [7:0] O_0_t1b,
  output [7:0] O_1_t0b,
  output [7:0] O_1_t1b,
  output [7:0] O_2_t0b,
  output [7:0] O_2_t1b
);
  wire  fst_op_valid_up; // @[Map2S.scala 9:22]
  wire  fst_op_valid_down; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I0; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I1; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_t0b; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_t1b; // @[Map2S.scala 9:22]
  wire  other_ops_0_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_0_valid_down; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_I0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_I1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_O_t0b; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_O_t1b; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_down; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_I0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_I1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_O_t0b; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_O_t1b; // @[Map2S.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[Map2S.scala 26:83]
  Map2T_14 fst_op ( // @[Map2S.scala 9:22]
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I0(fst_op_I0),
    .I1(fst_op_I1),
    .O_t0b(fst_op_O_t0b),
    .O_t1b(fst_op_O_t1b)
  );
  Map2T_14 other_ops_0 ( // @[Map2S.scala 10:86]
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I0(other_ops_0_I0),
    .I1(other_ops_0_I1),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b(other_ops_0_O_t1b)
  );
  Map2T_14 other_ops_1 ( // @[Map2S.scala 10:86]
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I0(other_ops_1_I0),
    .I1(other_ops_1_I1),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b(other_ops_1_O_t1b)
  );
  assign valid_down = _T & other_ops_1_valid_down; // @[Map2S.scala 26:14]
  assign O_0_t0b = fst_op_O_t0b; // @[Map2S.scala 19:8]
  assign O_0_t1b = fst_op_O_t1b; // @[Map2S.scala 19:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[Map2S.scala 24:12]
  assign O_1_t1b = other_ops_0_O_t1b; // @[Map2S.scala 24:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[Map2S.scala 24:12]
  assign O_2_t1b = other_ops_1_O_t1b; // @[Map2S.scala 24:12]
  assign fst_op_valid_up = valid_up; // @[Map2S.scala 16:19]
  assign fst_op_I0 = I0_0; // @[Map2S.scala 17:13]
  assign fst_op_I1 = I1_0; // @[Map2S.scala 18:13]
  assign other_ops_0_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_0_I0 = I0_1; // @[Map2S.scala 22:43]
  assign other_ops_0_I1 = I1_1; // @[Map2S.scala 23:43]
  assign other_ops_1_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_1_I0 = I0_2; // @[Map2S.scala 22:43]
  assign other_ops_1_I1 = I1_2; // @[Map2S.scala 23:43]
endmodule
module Map2T_15(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I0_1,
  input  [7:0] I0_2,
  input  [7:0] I1_0,
  input  [7:0] I1_1,
  input  [7:0] I1_2,
  output [7:0] O_0_t0b,
  output [7:0] O_0_t1b,
  output [7:0] O_1_t0b,
  output [7:0] O_1_t1b,
  output [7:0] O_2_t0b,
  output [7:0] O_2_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_2; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_2; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_t0b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_t1b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1_t0b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1_t1b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_2_t0b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_2_t1b; // @[Map2T.scala 8:20]
  Map2S_2 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0(op_I0_0),
    .I0_1(op_I0_1),
    .I0_2(op_I0_2),
    .I1_0(op_I1_0),
    .I1_1(op_I1_1),
    .I1_2(op_I1_2),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b(op_O_0_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b(op_O_1_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b(op_O_2_t1b)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0_t0b = op_O_0_t0b; // @[Map2T.scala 17:7]
  assign O_0_t1b = op_O_0_t1b; // @[Map2T.scala 17:7]
  assign O_1_t0b = op_O_1_t0b; // @[Map2T.scala 17:7]
  assign O_1_t1b = op_O_1_t1b; // @[Map2T.scala 17:7]
  assign O_2_t0b = op_O_2_t0b; // @[Map2T.scala 17:7]
  assign O_2_t1b = op_O_2_t1b; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0 = I0_0; // @[Map2T.scala 15:11]
  assign op_I0_1 = I0_1; // @[Map2T.scala 15:11]
  assign op_I0_2 = I0_2; // @[Map2T.scala 15:11]
  assign op_I1_0 = I1_0; // @[Map2T.scala 16:11]
  assign op_I1_1 = I1_1; // @[Map2T.scala 16:11]
  assign op_I1_2 = I1_2; // @[Map2T.scala 16:11]
endmodule
module LShift(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  wire [262:0] _GEN_0 = {{255'd0}, I_t0b}; // @[Arithmetic.scala 434:25]
  wire [262:0] _T = _GEN_0 << I_t1b; // @[Arithmetic.scala 434:25]
  assign valid_down = valid_up; // @[Arithmetic.scala 436:14]
  assign O = _T[7:0]; // @[Arithmetic.scala 434:7]
endmodule
module MapT_4(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_t1b; // @[MapT.scala 8:20]
  wire [7:0] op_O; // @[MapT.scala 8:20]
  LShift op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b(op_I_t1b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b = I_t1b; // @[MapT.scala 14:10]
endmodule
module MapS_3(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_t0b,
  input  [7:0] I_0_t1b,
  input  [7:0] I_1_t0b,
  input  [7:0] I_1_t1b,
  input  [7:0] I_2_t0b,
  input  [7:0] I_2_t1b,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_t1b; // @[MapS.scala 9:22]
  wire [7:0] fst_op_O; // @[MapS.scala 9:22]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_I_t1b; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_O; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_I_t1b; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_O; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  MapT_4 fst_op ( // @[MapS.scala 9:22]
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b(fst_op_I_t1b),
    .O(fst_op_O)
  );
  MapT_4 other_ops_0 ( // @[MapS.scala 10:86]
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b(other_ops_0_I_t1b),
    .O(other_ops_0_O)
  );
  MapT_4 other_ops_1 ( // @[MapS.scala 10:86]
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b(other_ops_1_I_t1b),
    .O(other_ops_1_O)
  );
  assign valid_down = _T & other_ops_1_valid_down; // @[MapS.scala 23:14]
  assign O_0 = fst_op_O; // @[MapS.scala 17:8]
  assign O_1 = other_ops_0_O; // @[MapS.scala 21:12]
  assign O_2 = other_ops_1_O; // @[MapS.scala 21:12]
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b = I_0_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b = I_1_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b = I_2_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_5(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_t0b,
  input  [7:0] I_0_t1b,
  input  [7:0] I_1_t0b,
  input  [7:0] I_1_t1b,
  input  [7:0] I_2_t0b,
  input  [7:0] I_2_t1b,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_t1b; // @[MapT.scala 8:20]
  wire [7:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_1_t1b; // @[MapT.scala 8:20]
  wire [7:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_2_t1b; // @[MapT.scala 8:20]
  wire [7:0] op_O_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_1; // @[MapT.scala 8:20]
  wire [7:0] op_O_2; // @[MapT.scala 8:20]
  MapS_3 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b(op_I_0_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b(op_I_1_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b(op_I_2_t1b),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0 = op_O_0; // @[MapT.scala 15:7]
  assign O_1 = op_O_1; // @[MapT.scala 15:7]
  assign O_2 = op_O_2; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b = I_0_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b = I_1_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b = I_2_t1b; // @[MapT.scala 14:10]
endmodule
module AddNoValid(
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  assign O = I_t0b + I_t1b; // @[Arithmetic.scala 125:7]
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
module MapS_4(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I; // @[MapS.scala 9:22]
  wire [7:0] fst_op_O; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_I; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_O; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_I; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_O; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  ReduceT fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I(fst_op_I),
    .O(fst_op_O)
  );
  ReduceT other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I(other_ops_0_I),
    .O(other_ops_0_O)
  );
  ReduceT other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I(other_ops_1_I),
    .O(other_ops_1_O)
  );
  assign valid_down = _T & other_ops_1_valid_down; // @[MapS.scala 23:14]
  assign O_0 = fst_op_O; // @[MapS.scala 17:8]
  assign O_1 = other_ops_0_O; // @[MapS.scala 21:12]
  assign O_2 = other_ops_1_O; // @[MapS.scala 21:12]
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
endmodule
module MapT_6(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_1; // @[MapT.scala 8:20]
  wire [7:0] op_O_2; // @[MapT.scala 8:20]
  MapS_4 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0(op_I_0),
    .I_1(op_I_1),
    .I_2(op_I_2),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0 = op_O_0; // @[MapT.scala 15:7]
  assign O_1 = op_O_1; // @[MapT.scala 15:7]
  assign O_2 = op_O_2; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0 = I_0; // @[MapT.scala 14:10]
  assign op_I_1 = I_1; // @[MapT.scala 14:10]
  assign op_I_2 = I_2; // @[MapT.scala 14:10]
endmodule
module MapTNoValid(
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  wire [7:0] op_I_t0b; // @[MapT.scala 21:20]
  wire [7:0] op_I_t1b; // @[MapT.scala 21:20]
  wire [7:0] op_O; // @[MapT.scala 21:20]
  AddNoValid op ( // @[MapT.scala 21:20]
    .I_t0b(op_I_t0b),
    .I_t1b(op_I_t1b),
    .O(op_O)
  );
  assign O = op_O; // @[MapT.scala 27:7]
  assign op_I_t0b = I_t0b; // @[MapT.scala 26:10]
  assign op_I_t1b = I_t1b; // @[MapT.scala 26:10]
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
  wire [7:0] MapTNoValid_I_t0b; // @[ReduceS.scala 20:43]
  wire [7:0] MapTNoValid_I_t1b; // @[ReduceS.scala 20:43]
  wire [7:0] MapTNoValid_O; // @[ReduceS.scala 20:43]
  wire [7:0] MapTNoValid_1_I_t0b; // @[ReduceS.scala 20:43]
  wire [7:0] MapTNoValid_1_I_t1b; // @[ReduceS.scala 20:43]
  wire [7:0] MapTNoValid_1_O; // @[ReduceS.scala 20:43]
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
  MapTNoValid MapTNoValid ( // @[ReduceS.scala 20:43]
    .I_t0b(MapTNoValid_I_t0b),
    .I_t1b(MapTNoValid_I_t1b),
    .O(MapTNoValid_O)
  );
  MapTNoValid MapTNoValid_1 ( // @[ReduceS.scala 20:43]
    .I_t0b(MapTNoValid_1_I_t0b),
    .I_t1b(MapTNoValid_1_I_t1b),
    .O(MapTNoValid_1_O)
  );
  assign valid_down = _T_5; // @[ReduceS.scala 47:14]
  assign O_0 = _T; // @[ReduceS.scala 27:14]
  assign MapTNoValid_I_t0b = _T_3; // @[ReduceS.scala 43:18]
  assign MapTNoValid_I_t1b = MapTNoValid_1_O; // @[ReduceS.scala 36:18]
  assign MapTNoValid_1_I_t0b = _T_1; // @[ReduceS.scala 43:18]
  assign MapTNoValid_1_I_t1b = _T_2; // @[ReduceS.scala 43:18]
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
    _T <= MapTNoValid_O;
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
module MapT_7(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O_0
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_0; // @[MapT.scala 8:20]
  ReduceS op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0(op_I_0),
    .I_1(op_I_1),
    .I_2(op_I_2),
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
endmodule
module ReduceT_1(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  output [7:0] O_0
);
  reg [7:0] undelayed_out_0; // @[ReduceT.scala 17:29]
  reg [31:0] _RAND_0;
  reg  _T_1; // @[ReduceT.scala 18:34]
  reg [31:0] _RAND_1;
  reg  _T_2; // @[ReduceT.scala 18:26]
  reg [31:0] _RAND_2;
  reg [7:0] _T_3_0; // @[ReduceT.scala 56:15]
  reg [31:0] _RAND_3;
  assign valid_down = _T_2; // @[ReduceT.scala 18:16]
  assign O_0 = _T_3_0; // @[ReduceT.scala 56:5]
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
  undelayed_out_0 = _RAND_0[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_1 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_2 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_3_0 = _RAND_3[7:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    undelayed_out_0 <= I_0;
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
    _T_3_0 <= undelayed_out_0;
  end
endmodule
module Passthrough_5(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  output [7:0] O
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O = I_0; // @[Passthrough.scala 17:68]
endmodule
module InitialDelayCounter_1(
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
module Map2T_17(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O_t0b,
  output [7:0] O_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_t0b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_t1b; // @[Map2T.scala 8:20]
  Map2T_14 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0(op_I0),
    .I1(op_I1),
    .O_t0b(op_O_t0b),
    .O_t1b(op_O_t1b)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_t0b = op_O_t0b; // @[Map2T.scala 17:7]
  assign O_t1b = op_O_t1b; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0 = I0; // @[Map2T.scala 15:11]
  assign op_I1 = I1; // @[Map2T.scala 16:11]
endmodule
module RShift(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  assign valid_down = valid_up; // @[Arithmetic.scala 405:14]
  assign O = I_t0b >> I_t1b; // @[Arithmetic.scala 403:7]
endmodule
module MapT_8(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_t1b; // @[MapT.scala 8:20]
  wire [7:0] op_O; // @[MapT.scala 8:20]
  RShift op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b(op_I_t1b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b = I_t1b; // @[MapT.scala 14:10]
endmodule
module MapT_9(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_t1b; // @[MapT.scala 8:20]
  wire [7:0] op_O; // @[MapT.scala 8:20]
  MapT_8 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b(op_I_t1b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b = I_t1b; // @[MapT.scala 14:10]
endmodule
module Module_0(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O
);
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n91_valid_up; // @[Top.scala 18:21]
  wire  n91_valid_down; // @[Top.scala 18:21]
  wire [7:0] n91_I0_0; // @[Top.scala 18:21]
  wire [7:0] n91_I0_1; // @[Top.scala 18:21]
  wire [7:0] n91_I0_2; // @[Top.scala 18:21]
  wire [7:0] n91_I1_0; // @[Top.scala 18:21]
  wire [7:0] n91_I1_1; // @[Top.scala 18:21]
  wire [7:0] n91_I1_2; // @[Top.scala 18:21]
  wire [7:0] n91_O_0_t0b; // @[Top.scala 18:21]
  wire [7:0] n91_O_0_t1b; // @[Top.scala 18:21]
  wire [7:0] n91_O_1_t0b; // @[Top.scala 18:21]
  wire [7:0] n91_O_1_t1b; // @[Top.scala 18:21]
  wire [7:0] n91_O_2_t0b; // @[Top.scala 18:21]
  wire [7:0] n91_O_2_t1b; // @[Top.scala 18:21]
  wire  n107_valid_up; // @[Top.scala 22:22]
  wire  n107_valid_down; // @[Top.scala 22:22]
  wire [7:0] n107_I_0_t0b; // @[Top.scala 22:22]
  wire [7:0] n107_I_0_t1b; // @[Top.scala 22:22]
  wire [7:0] n107_I_1_t0b; // @[Top.scala 22:22]
  wire [7:0] n107_I_1_t1b; // @[Top.scala 22:22]
  wire [7:0] n107_I_2_t0b; // @[Top.scala 22:22]
  wire [7:0] n107_I_2_t1b; // @[Top.scala 22:22]
  wire [7:0] n107_O_0; // @[Top.scala 22:22]
  wire [7:0] n107_O_1; // @[Top.scala 22:22]
  wire [7:0] n107_O_2; // @[Top.scala 22:22]
  wire  n114_clock; // @[Top.scala 25:22]
  wire  n114_reset; // @[Top.scala 25:22]
  wire  n114_valid_up; // @[Top.scala 25:22]
  wire  n114_valid_down; // @[Top.scala 25:22]
  wire [7:0] n114_I_0; // @[Top.scala 25:22]
  wire [7:0] n114_I_1; // @[Top.scala 25:22]
  wire [7:0] n114_I_2; // @[Top.scala 25:22]
  wire [7:0] n114_O_0; // @[Top.scala 25:22]
  wire [7:0] n114_O_1; // @[Top.scala 25:22]
  wire [7:0] n114_O_2; // @[Top.scala 25:22]
  wire  n121_clock; // @[Top.scala 28:22]
  wire  n121_reset; // @[Top.scala 28:22]
  wire  n121_valid_up; // @[Top.scala 28:22]
  wire  n121_valid_down; // @[Top.scala 28:22]
  wire [7:0] n121_I_0; // @[Top.scala 28:22]
  wire [7:0] n121_I_1; // @[Top.scala 28:22]
  wire [7:0] n121_I_2; // @[Top.scala 28:22]
  wire [7:0] n121_O_0; // @[Top.scala 28:22]
  wire  n124_clock; // @[Top.scala 31:22]
  wire  n124_reset; // @[Top.scala 31:22]
  wire  n124_valid_up; // @[Top.scala 31:22]
  wire  n124_valid_down; // @[Top.scala 31:22]
  wire [7:0] n124_I_0; // @[Top.scala 31:22]
  wire [7:0] n124_O_0; // @[Top.scala 31:22]
  wire  n125_valid_up; // @[Top.scala 34:22]
  wire  n125_valid_down; // @[Top.scala 34:22]
  wire [7:0] n125_I_0; // @[Top.scala 34:22]
  wire [7:0] n125_O; // @[Top.scala 34:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n128_valid_up; // @[Top.scala 38:22]
  wire  n128_valid_down; // @[Top.scala 38:22]
  wire [7:0] n128_I0; // @[Top.scala 38:22]
  wire [7:0] n128_I1; // @[Top.scala 38:22]
  wire [7:0] n128_O_t0b; // @[Top.scala 38:22]
  wire [7:0] n128_O_t1b; // @[Top.scala 38:22]
  wire  n139_valid_up; // @[Top.scala 42:22]
  wire  n139_valid_down; // @[Top.scala 42:22]
  wire [7:0] n139_I_t0b; // @[Top.scala 42:22]
  wire [7:0] n139_I_t1b; // @[Top.scala 42:22]
  wire [7:0] n139_O; // @[Top.scala 42:22]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_5 = value == 2'h2; // @[Counter.scala 38:24]
  wire [1:0] _T_7 = value + 2'h1; // @[Counter.scala 39:22]
  wire [7:0] _GEN_6 = 2'h1 == value ? 8'h1 : 8'h0; // @[Const.scala 25:72]
  wire [7:0] _GEN_7 = 2'h1 == value ? 8'h2 : 8'h1; // @[Const.scala 25:72]
  reg [1:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire  _T_11 = value_1 == 2'h2; // @[Counter.scala 38:24]
  wire [1:0] _T_13 = value_1 + 2'h1; // @[Counter.scala 39:22]
  wire [7:0] _GEN_16 = 2'h1 == value_1 ? 8'h0 : 8'h4; // @[Const.scala 25:72]
  InitialDelayCounter InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Map2T_15 n91 ( // @[Top.scala 18:21]
    .valid_up(n91_valid_up),
    .valid_down(n91_valid_down),
    .I0_0(n91_I0_0),
    .I0_1(n91_I0_1),
    .I0_2(n91_I0_2),
    .I1_0(n91_I1_0),
    .I1_1(n91_I1_1),
    .I1_2(n91_I1_2),
    .O_0_t0b(n91_O_0_t0b),
    .O_0_t1b(n91_O_0_t1b),
    .O_1_t0b(n91_O_1_t0b),
    .O_1_t1b(n91_O_1_t1b),
    .O_2_t0b(n91_O_2_t0b),
    .O_2_t1b(n91_O_2_t1b)
  );
  MapT_5 n107 ( // @[Top.scala 22:22]
    .valid_up(n107_valid_up),
    .valid_down(n107_valid_down),
    .I_0_t0b(n107_I_0_t0b),
    .I_0_t1b(n107_I_0_t1b),
    .I_1_t0b(n107_I_1_t0b),
    .I_1_t1b(n107_I_1_t1b),
    .I_2_t0b(n107_I_2_t0b),
    .I_2_t1b(n107_I_2_t1b),
    .O_0(n107_O_0),
    .O_1(n107_O_1),
    .O_2(n107_O_2)
  );
  MapT_6 n114 ( // @[Top.scala 25:22]
    .clock(n114_clock),
    .reset(n114_reset),
    .valid_up(n114_valid_up),
    .valid_down(n114_valid_down),
    .I_0(n114_I_0),
    .I_1(n114_I_1),
    .I_2(n114_I_2),
    .O_0(n114_O_0),
    .O_1(n114_O_1),
    .O_2(n114_O_2)
  );
  MapT_7 n121 ( // @[Top.scala 28:22]
    .clock(n121_clock),
    .reset(n121_reset),
    .valid_up(n121_valid_up),
    .valid_down(n121_valid_down),
    .I_0(n121_I_0),
    .I_1(n121_I_1),
    .I_2(n121_I_2),
    .O_0(n121_O_0)
  );
  ReduceT_1 n124 ( // @[Top.scala 31:22]
    .clock(n124_clock),
    .reset(n124_reset),
    .valid_up(n124_valid_up),
    .valid_down(n124_valid_down),
    .I_0(n124_I_0),
    .O_0(n124_O_0)
  );
  Passthrough_5 n125 ( // @[Top.scala 34:22]
    .valid_up(n125_valid_up),
    .valid_down(n125_valid_down),
    .I_0(n125_I_0),
    .O(n125_O)
  );
  InitialDelayCounter_1 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  Map2T_17 n128 ( // @[Top.scala 38:22]
    .valid_up(n128_valid_up),
    .valid_down(n128_valid_down),
    .I0(n128_I0),
    .I1(n128_I1),
    .O_t0b(n128_O_t0b),
    .O_t1b(n128_O_t1b)
  );
  MapT_9 n139 ( // @[Top.scala 42:22]
    .valid_up(n139_valid_up),
    .valid_down(n139_valid_down),
    .I_t0b(n139_I_t0b),
    .I_t1b(n139_I_t1b),
    .O(n139_O)
  );
  assign valid_down = n139_valid_down; // @[Top.scala 46:16]
  assign O = n139_O; // @[Top.scala 45:7]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n91_valid_up = valid_up & InitialDelayCounter_valid_down; // @[Top.scala 21:18]
  assign n91_I0_0 = I_0; // @[Top.scala 19:12]
  assign n91_I0_1 = I_1; // @[Top.scala 19:12]
  assign n91_I0_2 = I_2; // @[Top.scala 19:12]
  assign n91_I1_0 = 2'h2 == value ? 8'h0 : _GEN_6; // @[Top.scala 20:12]
  assign n91_I1_1 = 2'h2 == value ? 8'h1 : _GEN_7; // @[Top.scala 20:12]
  assign n91_I1_2 = 2'h2 == value ? 8'h0 : _GEN_6; // @[Top.scala 20:12]
  assign n107_valid_up = n91_valid_down; // @[Top.scala 24:19]
  assign n107_I_0_t0b = n91_O_0_t0b; // @[Top.scala 23:12]
  assign n107_I_0_t1b = n91_O_0_t1b; // @[Top.scala 23:12]
  assign n107_I_1_t0b = n91_O_1_t0b; // @[Top.scala 23:12]
  assign n107_I_1_t1b = n91_O_1_t1b; // @[Top.scala 23:12]
  assign n107_I_2_t0b = n91_O_2_t0b; // @[Top.scala 23:12]
  assign n107_I_2_t1b = n91_O_2_t1b; // @[Top.scala 23:12]
  assign n114_clock = clock;
  assign n114_reset = reset;
  assign n114_valid_up = n107_valid_down; // @[Top.scala 27:19]
  assign n114_I_0 = n107_O_0; // @[Top.scala 26:12]
  assign n114_I_1 = n107_O_1; // @[Top.scala 26:12]
  assign n114_I_2 = n107_O_2; // @[Top.scala 26:12]
  assign n121_clock = clock;
  assign n121_reset = reset;
  assign n121_valid_up = n114_valid_down; // @[Top.scala 30:19]
  assign n121_I_0 = n114_O_0; // @[Top.scala 29:12]
  assign n121_I_1 = n114_O_1; // @[Top.scala 29:12]
  assign n121_I_2 = n114_O_2; // @[Top.scala 29:12]
  assign n124_clock = clock;
  assign n124_reset = reset;
  assign n124_valid_up = n121_valid_down; // @[Top.scala 33:19]
  assign n124_I_0 = n121_O_0; // @[Top.scala 32:12]
  assign n125_valid_up = n124_valid_down; // @[Top.scala 36:19]
  assign n125_I_0 = n124_O_0; // @[Top.scala 35:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n128_valid_up = n125_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 41:19]
  assign n128_I0 = n125_O; // @[Top.scala 39:13]
  assign n128_I1 = 2'h2 == value_1 ? 8'h0 : _GEN_16; // @[Top.scala 40:13]
  assign n139_valid_up = n128_valid_down; // @[Top.scala 44:19]
  assign n139_I_t0b = n128_O_t0b; // @[Top.scala 43:12]
  assign n139_I_t1b = n128_O_t1b; // @[Top.scala 43:12]
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
  value_1 = _RAND_1[1:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 2'h0;
    end else if (InitialDelayCounter_valid_down) begin
      if (_T_5) begin
        value <= 2'h0;
      end else begin
        value <= _T_7;
      end
    end
    if (reset) begin
      value_1 <= 2'h0;
    end else if (InitialDelayCounter_1_valid_down) begin
      if (_T_11) begin
        value_1 <= 2'h0;
      end else begin
        value_1 <= _T_13;
      end
    end
  end
endmodule
module MapT_10(
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
  Module_0 op ( // @[MapT.scala 8:20]
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
module Passthrough_6(
  input        valid_up,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O = I; // @[Passthrough.scala 17:68]
endmodule
module FIFO_1(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  reg [7:0] _T [0:9]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_0;
  wire [7:0] _T__T_15_data; // @[FIFO.scala 23:33]
  wire [3:0] _T__T_15_addr; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_1;
  wire [7:0] _T__T_5_data; // @[FIFO.scala 23:33]
  wire [3:0] _T__T_5_addr; // @[FIFO.scala 23:33]
  wire  _T__T_5_mask; // @[FIFO.scala 23:33]
  wire  _T__T_5_en; // @[FIFO.scala 23:33]
  reg  _T__T_15_en_pipe_0;
  reg [31:0] _RAND_2;
  reg [3:0] _T__T_15_addr_pipe_0;
  reg [31:0] _RAND_3;
  reg [3:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_4;
  reg [3:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_5;
  reg [3:0] value_2; // @[Counter.scala 29:33]
  reg [31:0] _RAND_6;
  wire  _T_1 = value == 4'h9; // @[FIFO.scala 33:46]
  wire  _T_2 = value_2 == 4'h9; // @[Counter.scala 38:24]
  wire [3:0] _T_4 = value_2 + 4'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value < 4'h9; // @[FIFO.scala 38:39]
  wire [3:0] _T_9 = value + 4'h1; // @[Counter.scala 39:22]
  wire  _T_10 = value >= 4'h8; // @[FIFO.scala 42:39]
  wire  _T_16 = value_1 == 4'h9; // @[Counter.scala 38:24]
  wire [3:0] _T_18 = value_1 + 4'h1; // @[Counter.scala 39:22]
  wire  _GEN_8 = _T_10 & _T_10; // @[FIFO.scala 42:57]
  assign _T__T_15_addr = _T__T_15_addr_pipe_0;
  `ifndef RANDOMIZE_GARBAGE_ASSIGN
  assign _T__T_15_data = _T[_T__T_15_addr]; // @[FIFO.scala 23:33]
  `else
  assign _T__T_15_data = _T__T_15_addr >= 4'ha ? _RAND_1[7:0] : _T[_T__T_15_addr]; // @[FIFO.scala 23:33]
  `endif // RANDOMIZE_GARBAGE_ASSIGN
  assign _T__T_5_data = I;
  assign _T__T_5_addr = value_2;
  assign _T__T_5_mask = 1'h1;
  assign _T__T_5_en = valid_up;
  assign valid_down = value == 4'h9; // @[FIFO.scala 33:16]
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
  for (initvar = 0; initvar < 10; initvar = initvar+1)
    _T[initvar] = _RAND_0[7:0];
  `endif // RANDOMIZE_MEM_INIT
  _RAND_1 = {1{`RANDOM}};
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T__T_15_en_pipe_0 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T__T_15_addr_pipe_0 = _RAND_3[3:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  value = _RAND_4[3:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  value_1 = _RAND_5[3:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  value_2 = _RAND_6[3:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if(_T__T_5_en & _T__T_5_mask) begin
      _T[_T__T_5_addr] <= _T__T_5_data; // @[FIFO.scala 23:33]
    end
    _T__T_15_en_pipe_0 <= valid_up & _GEN_8;
    if (valid_up & _GEN_8) begin
      _T__T_15_addr_pipe_0 <= value_1;
    end
    if (reset) begin
      value <= 4'h0;
    end else if (valid_up) begin
      if (_T_6) begin
        if (_T_1) begin
          value <= 4'h0;
        end else begin
          value <= _T_9;
        end
      end
    end
    if (reset) begin
      value_1 <= 4'h0;
    end else if (valid_up) begin
      if (_T_10) begin
        if (_T_16) begin
          value_1 <= 4'h0;
        end else begin
          value_1 <= _T_18;
        end
      end
    end
    if (reset) begin
      value_2 <= 4'h0;
    end else if (valid_up) begin
      if (_T_2) begin
        value_2 <= 4'h0;
      end else begin
        value_2 <= _T_4;
      end
    end
  end
endmodule
module Sub(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  assign valid_down = valid_up; // @[Arithmetic.scala 142:14]
  assign O = I_t0b - I_t1b; // @[Arithmetic.scala 140:7]
endmodule
module Lt(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  wire  _T = I_t0b < I_t1b; // @[Arithmetic.scala 465:25]
  assign valid_down = valid_up; // @[Arithmetic.scala 467:14]
  assign O = {{7'd0}, _T}; // @[Arithmetic.scala 465:7]
endmodule
module AtomTuple_6(
  input   valid_up,
  output  valid_down,
  input   I0,
  input   I1,
  output  O_t0b,
  output  O_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b = I0; // @[Tuple.scala 49:9]
  assign O_t1b = I1; // @[Tuple.scala 50:9]
endmodule
module LogicalOr(
  input   valid_up,
  output  valid_down,
  input   I_t0b,
  input   I_t1b,
  output  O
);
  assign valid_down = valid_up; // @[Arithmetic.scala 86:14]
  assign O = I_t0b | I_t1b; // @[Arithmetic.scala 85:5]
endmodule
module AtomTuple_8(
  input        valid_up,
  output       valid_down,
  input        I0,
  input  [7:0] I1_t0b,
  input  [7:0] I1_t1b,
  output       O_t0b,
  output [7:0] O_t1b_t0b,
  output [7:0] O_t1b_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b = I0; // @[Tuple.scala 49:9]
  assign O_t1b_t0b = I1_t0b; // @[Tuple.scala 50:9]
  assign O_t1b_t1b = I1_t1b; // @[Tuple.scala 50:9]
endmodule
module If(
  input        valid_up,
  output       valid_down,
  input        I_t0b,
  input  [7:0] I_t1b_t0b,
  input  [7:0] I_t1b_t1b,
  output [7:0] O
);
  assign valid_down = valid_up; // @[Arithmetic.scala 528:14]
  assign O = I_t0b ? I_t1b_t0b : I_t1b_t1b; // @[Arithmetic.scala 526:9 Arithmetic.scala 527:20]
endmodule
module Add(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  assign valid_down = valid_up; // @[Arithmetic.scala 111:14]
  assign O = I_t0b + I_t1b; // @[Arithmetic.scala 109:7]
endmodule
module Module_1(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O
);
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n150_valid_up; // @[Top.scala 54:22]
  wire  n150_valid_down; // @[Top.scala 54:22]
  wire [7:0] n150_I0; // @[Top.scala 54:22]
  wire [7:0] n150_I1; // @[Top.scala 54:22]
  wire [7:0] n150_O_t0b; // @[Top.scala 54:22]
  wire [7:0] n150_O_t1b; // @[Top.scala 54:22]
  wire  n151_valid_up; // @[Top.scala 58:22]
  wire  n151_valid_down; // @[Top.scala 58:22]
  wire [7:0] n151_I_t0b; // @[Top.scala 58:22]
  wire [7:0] n151_I_t1b; // @[Top.scala 58:22]
  wire [7:0] n151_O; // @[Top.scala 58:22]
  wire  n153_valid_up; // @[Top.scala 61:22]
  wire  n153_valid_down; // @[Top.scala 61:22]
  wire [7:0] n153_I0; // @[Top.scala 61:22]
  wire [7:0] n153_I1; // @[Top.scala 61:22]
  wire [7:0] n153_O_t0b; // @[Top.scala 61:22]
  wire [7:0] n153_O_t1b; // @[Top.scala 61:22]
  wire  n154_valid_up; // @[Top.scala 65:22]
  wire  n154_valid_down; // @[Top.scala 65:22]
  wire [7:0] n154_I_t0b; // @[Top.scala 65:22]
  wire [7:0] n154_I_t1b; // @[Top.scala 65:22]
  wire [7:0] n154_O; // @[Top.scala 65:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n155_valid_up; // @[Top.scala 69:22]
  wire  n155_valid_down; // @[Top.scala 69:22]
  wire [7:0] n155_I0; // @[Top.scala 69:22]
  wire [7:0] n155_I1; // @[Top.scala 69:22]
  wire [7:0] n155_O_t0b; // @[Top.scala 69:22]
  wire [7:0] n155_O_t1b; // @[Top.scala 69:22]
  wire  n156_valid_up; // @[Top.scala 73:22]
  wire  n156_valid_down; // @[Top.scala 73:22]
  wire [7:0] n156_I_t0b; // @[Top.scala 73:22]
  wire [7:0] n156_I_t1b; // @[Top.scala 73:22]
  wire [7:0] n156_O; // @[Top.scala 73:22]
  wire  n158_valid_up; // @[Top.scala 76:22]
  wire  n158_valid_down; // @[Top.scala 76:22]
  wire [7:0] n158_I0; // @[Top.scala 76:22]
  wire [7:0] n158_I1; // @[Top.scala 76:22]
  wire [7:0] n158_O_t0b; // @[Top.scala 76:22]
  wire [7:0] n158_O_t1b; // @[Top.scala 76:22]
  wire  n159_valid_up; // @[Top.scala 80:22]
  wire  n159_valid_down; // @[Top.scala 80:22]
  wire [7:0] n159_I_t0b; // @[Top.scala 80:22]
  wire [7:0] n159_I_t1b; // @[Top.scala 80:22]
  wire [7:0] n159_O; // @[Top.scala 80:22]
  wire  n160_valid_up; // @[Top.scala 83:22]
  wire  n160_valid_down; // @[Top.scala 83:22]
  wire  n160_I0; // @[Top.scala 83:22]
  wire  n160_I1; // @[Top.scala 83:22]
  wire  n160_O_t0b; // @[Top.scala 83:22]
  wire  n160_O_t1b; // @[Top.scala 83:22]
  wire  n161_valid_up; // @[Top.scala 87:22]
  wire  n161_valid_down; // @[Top.scala 87:22]
  wire  n161_I_t0b; // @[Top.scala 87:22]
  wire  n161_I_t1b; // @[Top.scala 87:22]
  wire  n161_O; // @[Top.scala 87:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n164_valid_up; // @[Top.scala 91:22]
  wire  n164_valid_down; // @[Top.scala 91:22]
  wire [7:0] n164_I0; // @[Top.scala 91:22]
  wire [7:0] n164_I1; // @[Top.scala 91:22]
  wire [7:0] n164_O_t0b; // @[Top.scala 91:22]
  wire [7:0] n164_O_t1b; // @[Top.scala 91:22]
  wire  n165_valid_up; // @[Top.scala 95:22]
  wire  n165_valid_down; // @[Top.scala 95:22]
  wire  n165_I0; // @[Top.scala 95:22]
  wire [7:0] n165_I1_t0b; // @[Top.scala 95:22]
  wire [7:0] n165_I1_t1b; // @[Top.scala 95:22]
  wire  n165_O_t0b; // @[Top.scala 95:22]
  wire [7:0] n165_O_t1b_t0b; // @[Top.scala 95:22]
  wire [7:0] n165_O_t1b_t1b; // @[Top.scala 95:22]
  wire  n166_valid_up; // @[Top.scala 99:22]
  wire  n166_valid_down; // @[Top.scala 99:22]
  wire  n166_I_t0b; // @[Top.scala 99:22]
  wire [7:0] n166_I_t1b_t0b; // @[Top.scala 99:22]
  wire [7:0] n166_I_t1b_t1b; // @[Top.scala 99:22]
  wire [7:0] n166_O; // @[Top.scala 99:22]
  wire  InitialDelayCounter_3_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_3_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_3_valid_down; // @[Const.scala 11:33]
  wire  n169_valid_up; // @[Top.scala 103:22]
  wire  n169_valid_down; // @[Top.scala 103:22]
  wire [7:0] n169_I0; // @[Top.scala 103:22]
  wire [7:0] n169_I1; // @[Top.scala 103:22]
  wire [7:0] n169_O_t0b; // @[Top.scala 103:22]
  wire [7:0] n169_O_t1b; // @[Top.scala 103:22]
  wire  n170_valid_up; // @[Top.scala 107:22]
  wire  n170_valid_down; // @[Top.scala 107:22]
  wire [7:0] n170_I_t0b; // @[Top.scala 107:22]
  wire [7:0] n170_I_t1b; // @[Top.scala 107:22]
  wire [7:0] n170_O; // @[Top.scala 107:22]
  wire  n171_valid_up; // @[Top.scala 110:22]
  wire  n171_valid_down; // @[Top.scala 110:22]
  wire [7:0] n171_I0; // @[Top.scala 110:22]
  wire [7:0] n171_I1; // @[Top.scala 110:22]
  wire [7:0] n171_O_t0b; // @[Top.scala 110:22]
  wire [7:0] n171_O_t1b; // @[Top.scala 110:22]
  wire  n172_valid_up; // @[Top.scala 114:22]
  wire  n172_valid_down; // @[Top.scala 114:22]
  wire [7:0] n172_I_t0b; // @[Top.scala 114:22]
  wire [7:0] n172_I_t1b; // @[Top.scala 114:22]
  wire [7:0] n172_O; // @[Top.scala 114:22]
  InitialDelayCounter_1 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n150 ( // @[Top.scala 54:22]
    .valid_up(n150_valid_up),
    .valid_down(n150_valid_down),
    .I0(n150_I0),
    .I1(n150_I1),
    .O_t0b(n150_O_t0b),
    .O_t1b(n150_O_t1b)
  );
  Sub n151 ( // @[Top.scala 58:22]
    .valid_up(n151_valid_up),
    .valid_down(n151_valid_down),
    .I_t0b(n151_I_t0b),
    .I_t1b(n151_I_t1b),
    .O(n151_O)
  );
  AtomTuple n153 ( // @[Top.scala 61:22]
    .valid_up(n153_valid_up),
    .valid_down(n153_valid_down),
    .I0(n153_I0),
    .I1(n153_I1),
    .O_t0b(n153_O_t0b),
    .O_t1b(n153_O_t1b)
  );
  Lt n154 ( // @[Top.scala 65:22]
    .valid_up(n154_valid_up),
    .valid_down(n154_valid_down),
    .I_t0b(n154_I_t0b),
    .I_t1b(n154_I_t1b),
    .O(n154_O)
  );
  InitialDelayCounter_1 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple n155 ( // @[Top.scala 69:22]
    .valid_up(n155_valid_up),
    .valid_down(n155_valid_down),
    .I0(n155_I0),
    .I1(n155_I1),
    .O_t0b(n155_O_t0b),
    .O_t1b(n155_O_t1b)
  );
  Sub n156 ( // @[Top.scala 73:22]
    .valid_up(n156_valid_up),
    .valid_down(n156_valid_down),
    .I_t0b(n156_I_t0b),
    .I_t1b(n156_I_t1b),
    .O(n156_O)
  );
  AtomTuple n158 ( // @[Top.scala 76:22]
    .valid_up(n158_valid_up),
    .valid_down(n158_valid_down),
    .I0(n158_I0),
    .I1(n158_I1),
    .O_t0b(n158_O_t0b),
    .O_t1b(n158_O_t1b)
  );
  Lt n159 ( // @[Top.scala 80:22]
    .valid_up(n159_valid_up),
    .valid_down(n159_valid_down),
    .I_t0b(n159_I_t0b),
    .I_t1b(n159_I_t1b),
    .O(n159_O)
  );
  AtomTuple_6 n160 ( // @[Top.scala 83:22]
    .valid_up(n160_valid_up),
    .valid_down(n160_valid_down),
    .I0(n160_I0),
    .I1(n160_I1),
    .O_t0b(n160_O_t0b),
    .O_t1b(n160_O_t1b)
  );
  LogicalOr n161 ( // @[Top.scala 87:22]
    .valid_up(n161_valid_up),
    .valid_down(n161_valid_down),
    .I_t0b(n161_I_t0b),
    .I_t1b(n161_I_t1b),
    .O(n161_O)
  );
  InitialDelayCounter_1 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n164 ( // @[Top.scala 91:22]
    .valid_up(n164_valid_up),
    .valid_down(n164_valid_down),
    .I0(n164_I0),
    .I1(n164_I1),
    .O_t0b(n164_O_t0b),
    .O_t1b(n164_O_t1b)
  );
  AtomTuple_8 n165 ( // @[Top.scala 95:22]
    .valid_up(n165_valid_up),
    .valid_down(n165_valid_down),
    .I0(n165_I0),
    .I1_t0b(n165_I1_t0b),
    .I1_t1b(n165_I1_t1b),
    .O_t0b(n165_O_t0b),
    .O_t1b_t0b(n165_O_t1b_t0b),
    .O_t1b_t1b(n165_O_t1b_t1b)
  );
  If n166 ( // @[Top.scala 99:22]
    .valid_up(n166_valid_up),
    .valid_down(n166_valid_down),
    .I_t0b(n166_I_t0b),
    .I_t1b_t0b(n166_I_t1b_t0b),
    .I_t1b_t1b(n166_I_t1b_t1b),
    .O(n166_O)
  );
  InitialDelayCounter_1 InitialDelayCounter_3 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_3_clock),
    .reset(InitialDelayCounter_3_reset),
    .valid_down(InitialDelayCounter_3_valid_down)
  );
  AtomTuple n169 ( // @[Top.scala 103:22]
    .valid_up(n169_valid_up),
    .valid_down(n169_valid_down),
    .I0(n169_I0),
    .I1(n169_I1),
    .O_t0b(n169_O_t0b),
    .O_t1b(n169_O_t1b)
  );
  RShift n170 ( // @[Top.scala 107:22]
    .valid_up(n170_valid_up),
    .valid_down(n170_valid_down),
    .I_t0b(n170_I_t0b),
    .I_t1b(n170_I_t1b),
    .O(n170_O)
  );
  AtomTuple n171 ( // @[Top.scala 110:22]
    .valid_up(n171_valid_up),
    .valid_down(n171_valid_down),
    .I0(n171_I0),
    .I1(n171_I1),
    .O_t0b(n171_O_t0b),
    .O_t1b(n171_O_t1b)
  );
  Add n172 ( // @[Top.scala 114:22]
    .valid_up(n172_valid_up),
    .valid_down(n172_valid_down),
    .I_t0b(n172_I_t0b),
    .I_t1b(n172_I_t1b),
    .O(n172_O)
  );
  assign valid_down = n172_valid_down; // @[Top.scala 118:16]
  assign O = n172_O; // @[Top.scala 117:7]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n150_valid_up = valid_up; // @[Top.scala 57:19]
  assign n150_I0 = I0; // @[Top.scala 55:13]
  assign n150_I1 = I1; // @[Top.scala 56:13]
  assign n151_valid_up = n150_valid_down; // @[Top.scala 60:19]
  assign n151_I_t0b = n150_O_t0b; // @[Top.scala 59:12]
  assign n151_I_t1b = n150_O_t1b; // @[Top.scala 59:12]
  assign n153_valid_up = InitialDelayCounter_valid_down & n151_valid_down; // @[Top.scala 64:19]
  assign n153_I0 = 8'hf; // @[Top.scala 62:13]
  assign n153_I1 = n151_O; // @[Top.scala 63:13]
  assign n154_valid_up = n153_valid_down; // @[Top.scala 67:19]
  assign n154_I_t0b = n153_O_t0b; // @[Top.scala 66:12]
  assign n154_I_t1b = n153_O_t1b; // @[Top.scala 66:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n155_valid_up = valid_up; // @[Top.scala 72:19]
  assign n155_I0 = I1; // @[Top.scala 70:13]
  assign n155_I1 = I0; // @[Top.scala 71:13]
  assign n156_valid_up = n155_valid_down; // @[Top.scala 75:19]
  assign n156_I_t0b = n155_O_t0b; // @[Top.scala 74:12]
  assign n156_I_t1b = n155_O_t1b; // @[Top.scala 74:12]
  assign n158_valid_up = InitialDelayCounter_1_valid_down & n156_valid_down; // @[Top.scala 79:19]
  assign n158_I0 = 8'hf; // @[Top.scala 77:13]
  assign n158_I1 = n156_O; // @[Top.scala 78:13]
  assign n159_valid_up = n158_valid_down; // @[Top.scala 82:19]
  assign n159_I_t0b = n158_O_t0b; // @[Top.scala 81:12]
  assign n159_I_t1b = n158_O_t1b; // @[Top.scala 81:12]
  assign n160_valid_up = n154_valid_down & n159_valid_down; // @[Top.scala 86:19]
  assign n160_I0 = n154_O[0]; // @[Top.scala 84:13]
  assign n160_I1 = n159_O[0]; // @[Top.scala 85:13]
  assign n161_valid_up = n160_valid_down; // @[Top.scala 89:19]
  assign n161_I_t0b = n160_O_t0b; // @[Top.scala 88:12]
  assign n161_I_t1b = n160_O_t1b; // @[Top.scala 88:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n164_valid_up = n156_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 94:19]
  assign n164_I0 = n156_O; // @[Top.scala 92:13]
  assign n164_I1 = 8'h0; // @[Top.scala 93:13]
  assign n165_valid_up = n161_valid_down & n164_valid_down; // @[Top.scala 98:19]
  assign n165_I0 = n161_O; // @[Top.scala 96:13]
  assign n165_I1_t0b = n164_O_t0b; // @[Top.scala 97:13]
  assign n165_I1_t1b = n164_O_t1b; // @[Top.scala 97:13]
  assign n166_valid_up = n165_valid_down; // @[Top.scala 101:19]
  assign n166_I_t0b = n165_O_t0b; // @[Top.scala 100:12]
  assign n166_I_t1b_t0b = n165_O_t1b_t0b; // @[Top.scala 100:12]
  assign n166_I_t1b_t1b = n165_O_t1b_t1b; // @[Top.scala 100:12]
  assign InitialDelayCounter_3_clock = clock;
  assign InitialDelayCounter_3_reset = reset;
  assign n169_valid_up = n166_valid_down & InitialDelayCounter_3_valid_down; // @[Top.scala 106:19]
  assign n169_I0 = n166_O; // @[Top.scala 104:13]
  assign n169_I1 = 8'h2; // @[Top.scala 105:13]
  assign n170_valid_up = n169_valid_down; // @[Top.scala 109:19]
  assign n170_I_t0b = n169_O_t0b; // @[Top.scala 108:12]
  assign n170_I_t1b = n169_O_t1b; // @[Top.scala 108:12]
  assign n171_valid_up = valid_up & n170_valid_down; // @[Top.scala 113:19]
  assign n171_I0 = I1; // @[Top.scala 111:13]
  assign n171_I1 = n170_O; // @[Top.scala 112:13]
  assign n172_valid_up = n171_valid_down; // @[Top.scala 116:19]
  assign n172_I_t0b = n171_O_t0b; // @[Top.scala 115:12]
  assign n172_I_t1b = n171_O_t1b; // @[Top.scala 115:12]
endmodule
module Map2T_18(
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
  Module_1 op ( // @[Map2T.scala 8:20]
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
module Map2T_19(
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
  Map2T_18 op ( // @[Map2T.scala 8:20]
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
module Top(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  wire  n1_clock; // @[Top.scala 124:20]
  wire  n1_reset; // @[Top.scala 124:20]
  wire  n1_valid_up; // @[Top.scala 124:20]
  wire  n1_valid_down; // @[Top.scala 124:20]
  wire [7:0] n1_I; // @[Top.scala 124:20]
  wire [7:0] n1_O; // @[Top.scala 124:20]
  wire  n2_clock; // @[Top.scala 127:20]
  wire  n2_reset; // @[Top.scala 127:20]
  wire  n2_valid_up; // @[Top.scala 127:20]
  wire  n2_valid_down; // @[Top.scala 127:20]
  wire [7:0] n2_I; // @[Top.scala 127:20]
  wire [7:0] n2_O; // @[Top.scala 127:20]
  wire  n3_clock; // @[Top.scala 130:20]
  wire  n3_reset; // @[Top.scala 130:20]
  wire  n3_valid_up; // @[Top.scala 130:20]
  wire  n3_valid_down; // @[Top.scala 130:20]
  wire [7:0] n3_I; // @[Top.scala 130:20]
  wire [7:0] n3_O; // @[Top.scala 130:20]
  wire  n4_clock; // @[Top.scala 133:20]
  wire  n4_reset; // @[Top.scala 133:20]
  wire  n4_valid_up; // @[Top.scala 133:20]
  wire  n4_valid_down; // @[Top.scala 133:20]
  wire [7:0] n4_I; // @[Top.scala 133:20]
  wire [7:0] n4_O; // @[Top.scala 133:20]
  wire  n5_clock; // @[Top.scala 136:20]
  wire  n5_reset; // @[Top.scala 136:20]
  wire  n5_valid_up; // @[Top.scala 136:20]
  wire  n5_valid_down; // @[Top.scala 136:20]
  wire [7:0] n5_I; // @[Top.scala 136:20]
  wire [7:0] n5_O; // @[Top.scala 136:20]
  wire  n6_valid_up; // @[Top.scala 139:20]
  wire  n6_valid_down; // @[Top.scala 139:20]
  wire [7:0] n6_I0; // @[Top.scala 139:20]
  wire [7:0] n6_I1; // @[Top.scala 139:20]
  wire [7:0] n6_O_0; // @[Top.scala 139:20]
  wire [7:0] n6_O_1; // @[Top.scala 139:20]
  wire  n13_valid_up; // @[Top.scala 143:21]
  wire  n13_valid_down; // @[Top.scala 143:21]
  wire [7:0] n13_I0_0; // @[Top.scala 143:21]
  wire [7:0] n13_I0_1; // @[Top.scala 143:21]
  wire [7:0] n13_I1; // @[Top.scala 143:21]
  wire [7:0] n13_O_0; // @[Top.scala 143:21]
  wire [7:0] n13_O_1; // @[Top.scala 143:21]
  wire [7:0] n13_O_2; // @[Top.scala 143:21]
  wire  n20_valid_up; // @[Top.scala 147:21]
  wire  n20_valid_down; // @[Top.scala 147:21]
  wire [7:0] n20_I_0; // @[Top.scala 147:21]
  wire [7:0] n20_I_1; // @[Top.scala 147:21]
  wire [7:0] n20_I_2; // @[Top.scala 147:21]
  wire [7:0] n20_O_0_0; // @[Top.scala 147:21]
  wire [7:0] n20_O_0_1; // @[Top.scala 147:21]
  wire [7:0] n20_O_0_2; // @[Top.scala 147:21]
  wire  n25_clock; // @[Top.scala 150:21]
  wire  n25_reset; // @[Top.scala 150:21]
  wire  n25_valid_up; // @[Top.scala 150:21]
  wire  n25_valid_down; // @[Top.scala 150:21]
  wire [7:0] n25_I_0_0; // @[Top.scala 150:21]
  wire [7:0] n25_I_0_1; // @[Top.scala 150:21]
  wire [7:0] n25_I_0_2; // @[Top.scala 150:21]
  wire [7:0] n25_O_0; // @[Top.scala 150:21]
  wire  n26_clock; // @[Top.scala 153:21]
  wire  n26_reset; // @[Top.scala 153:21]
  wire  n26_valid_up; // @[Top.scala 153:21]
  wire  n26_valid_down; // @[Top.scala 153:21]
  wire [7:0] n26_I; // @[Top.scala 153:21]
  wire [7:0] n26_O; // @[Top.scala 153:21]
  wire  n27_clock; // @[Top.scala 156:21]
  wire  n27_reset; // @[Top.scala 156:21]
  wire  n27_valid_up; // @[Top.scala 156:21]
  wire  n27_valid_down; // @[Top.scala 156:21]
  wire [7:0] n27_I; // @[Top.scala 156:21]
  wire [7:0] n27_O; // @[Top.scala 156:21]
  wire  n28_valid_up; // @[Top.scala 159:21]
  wire  n28_valid_down; // @[Top.scala 159:21]
  wire [7:0] n28_I0; // @[Top.scala 159:21]
  wire [7:0] n28_I1; // @[Top.scala 159:21]
  wire [7:0] n28_O_0; // @[Top.scala 159:21]
  wire [7:0] n28_O_1; // @[Top.scala 159:21]
  wire  n35_valid_up; // @[Top.scala 163:21]
  wire  n35_valid_down; // @[Top.scala 163:21]
  wire [7:0] n35_I0_0; // @[Top.scala 163:21]
  wire [7:0] n35_I0_1; // @[Top.scala 163:21]
  wire [7:0] n35_I1; // @[Top.scala 163:21]
  wire [7:0] n35_O_0; // @[Top.scala 163:21]
  wire [7:0] n35_O_1; // @[Top.scala 163:21]
  wire [7:0] n35_O_2; // @[Top.scala 163:21]
  wire  n42_valid_up; // @[Top.scala 167:21]
  wire  n42_valid_down; // @[Top.scala 167:21]
  wire [7:0] n42_I_0; // @[Top.scala 167:21]
  wire [7:0] n42_I_1; // @[Top.scala 167:21]
  wire [7:0] n42_I_2; // @[Top.scala 167:21]
  wire [7:0] n42_O_0_0; // @[Top.scala 167:21]
  wire [7:0] n42_O_0_1; // @[Top.scala 167:21]
  wire [7:0] n42_O_0_2; // @[Top.scala 167:21]
  wire  n47_clock; // @[Top.scala 170:21]
  wire  n47_reset; // @[Top.scala 170:21]
  wire  n47_valid_up; // @[Top.scala 170:21]
  wire  n47_valid_down; // @[Top.scala 170:21]
  wire [7:0] n47_I_0_0; // @[Top.scala 170:21]
  wire [7:0] n47_I_0_1; // @[Top.scala 170:21]
  wire [7:0] n47_I_0_2; // @[Top.scala 170:21]
  wire [7:0] n47_O_0; // @[Top.scala 170:21]
  wire  n48_valid_up; // @[Top.scala 173:21]
  wire  n48_valid_down; // @[Top.scala 173:21]
  wire [7:0] n48_I0_0; // @[Top.scala 173:21]
  wire [7:0] n48_I1_0; // @[Top.scala 173:21]
  wire [7:0] n48_O_0_0; // @[Top.scala 173:21]
  wire [7:0] n48_O_0_1; // @[Top.scala 173:21]
  wire  n55_clock; // @[Top.scala 177:21]
  wire  n55_reset; // @[Top.scala 177:21]
  wire  n55_valid_up; // @[Top.scala 177:21]
  wire  n55_valid_down; // @[Top.scala 177:21]
  wire [7:0] n55_I; // @[Top.scala 177:21]
  wire [7:0] n55_O; // @[Top.scala 177:21]
  wire  n56_clock; // @[Top.scala 180:21]
  wire  n56_reset; // @[Top.scala 180:21]
  wire  n56_valid_up; // @[Top.scala 180:21]
  wire  n56_valid_down; // @[Top.scala 180:21]
  wire [7:0] n56_I; // @[Top.scala 180:21]
  wire [7:0] n56_O; // @[Top.scala 180:21]
  wire  n57_valid_up; // @[Top.scala 183:21]
  wire  n57_valid_down; // @[Top.scala 183:21]
  wire [7:0] n57_I0; // @[Top.scala 183:21]
  wire [7:0] n57_I1; // @[Top.scala 183:21]
  wire [7:0] n57_O_0; // @[Top.scala 183:21]
  wire [7:0] n57_O_1; // @[Top.scala 183:21]
  wire  n64_valid_up; // @[Top.scala 187:21]
  wire  n64_valid_down; // @[Top.scala 187:21]
  wire [7:0] n64_I0_0; // @[Top.scala 187:21]
  wire [7:0] n64_I0_1; // @[Top.scala 187:21]
  wire [7:0] n64_I1; // @[Top.scala 187:21]
  wire [7:0] n64_O_0; // @[Top.scala 187:21]
  wire [7:0] n64_O_1; // @[Top.scala 187:21]
  wire [7:0] n64_O_2; // @[Top.scala 187:21]
  wire  n71_valid_up; // @[Top.scala 191:21]
  wire  n71_valid_down; // @[Top.scala 191:21]
  wire [7:0] n71_I_0; // @[Top.scala 191:21]
  wire [7:0] n71_I_1; // @[Top.scala 191:21]
  wire [7:0] n71_I_2; // @[Top.scala 191:21]
  wire [7:0] n71_O_0_0; // @[Top.scala 191:21]
  wire [7:0] n71_O_0_1; // @[Top.scala 191:21]
  wire [7:0] n71_O_0_2; // @[Top.scala 191:21]
  wire  n76_clock; // @[Top.scala 194:21]
  wire  n76_reset; // @[Top.scala 194:21]
  wire  n76_valid_up; // @[Top.scala 194:21]
  wire  n76_valid_down; // @[Top.scala 194:21]
  wire [7:0] n76_I_0_0; // @[Top.scala 194:21]
  wire [7:0] n76_I_0_1; // @[Top.scala 194:21]
  wire [7:0] n76_I_0_2; // @[Top.scala 194:21]
  wire [7:0] n76_O_0; // @[Top.scala 194:21]
  wire  n77_valid_up; // @[Top.scala 197:21]
  wire  n77_valid_down; // @[Top.scala 197:21]
  wire [7:0] n77_I0_0_0; // @[Top.scala 197:21]
  wire [7:0] n77_I0_0_1; // @[Top.scala 197:21]
  wire [7:0] n77_I1_0; // @[Top.scala 197:21]
  wire [7:0] n77_O_0_0; // @[Top.scala 197:21]
  wire [7:0] n77_O_0_1; // @[Top.scala 197:21]
  wire [7:0] n77_O_0_2; // @[Top.scala 197:21]
  wire  n84_valid_up; // @[Top.scala 201:21]
  wire  n84_valid_down; // @[Top.scala 201:21]
  wire [7:0] n84_I_0_0; // @[Top.scala 201:21]
  wire [7:0] n84_I_0_1; // @[Top.scala 201:21]
  wire [7:0] n84_I_0_2; // @[Top.scala 201:21]
  wire [7:0] n84_O_0_0; // @[Top.scala 201:21]
  wire [7:0] n84_O_0_1; // @[Top.scala 201:21]
  wire [7:0] n84_O_0_2; // @[Top.scala 201:21]
  wire  n87_valid_up; // @[Top.scala 204:21]
  wire  n87_valid_down; // @[Top.scala 204:21]
  wire [7:0] n87_I_0_0; // @[Top.scala 204:21]
  wire [7:0] n87_I_0_1; // @[Top.scala 204:21]
  wire [7:0] n87_I_0_2; // @[Top.scala 204:21]
  wire [7:0] n87_O_0; // @[Top.scala 204:21]
  wire [7:0] n87_O_1; // @[Top.scala 204:21]
  wire [7:0] n87_O_2; // @[Top.scala 204:21]
  wire  n140_clock; // @[Top.scala 207:22]
  wire  n140_reset; // @[Top.scala 207:22]
  wire  n140_valid_up; // @[Top.scala 207:22]
  wire  n140_valid_down; // @[Top.scala 207:22]
  wire [7:0] n140_I_0; // @[Top.scala 207:22]
  wire [7:0] n140_I_1; // @[Top.scala 207:22]
  wire [7:0] n140_I_2; // @[Top.scala 207:22]
  wire [7:0] n140_O; // @[Top.scala 207:22]
  wire  n141_valid_up; // @[Top.scala 210:22]
  wire  n141_valid_down; // @[Top.scala 210:22]
  wire [7:0] n141_I; // @[Top.scala 210:22]
  wire [7:0] n141_O; // @[Top.scala 210:22]
  wire  n142_clock; // @[Top.scala 213:22]
  wire  n142_reset; // @[Top.scala 213:22]
  wire  n142_valid_up; // @[Top.scala 213:22]
  wire  n142_valid_down; // @[Top.scala 213:22]
  wire [7:0] n142_I; // @[Top.scala 213:22]
  wire [7:0] n142_O; // @[Top.scala 213:22]
  wire  n143_clock; // @[Top.scala 216:22]
  wire  n143_reset; // @[Top.scala 216:22]
  wire  n143_valid_up; // @[Top.scala 216:22]
  wire  n143_valid_down; // @[Top.scala 216:22]
  wire [7:0] n143_I0; // @[Top.scala 216:22]
  wire [7:0] n143_I1; // @[Top.scala 216:22]
  wire [7:0] n143_O; // @[Top.scala 216:22]
  wire  n173_clock; // @[Top.scala 220:22]
  wire  n173_reset; // @[Top.scala 220:22]
  wire  n173_valid_up; // @[Top.scala 220:22]
  wire  n173_valid_down; // @[Top.scala 220:22]
  wire [7:0] n173_I; // @[Top.scala 220:22]
  wire [7:0] n173_O; // @[Top.scala 220:22]
  wire  n174_clock; // @[Top.scala 223:22]
  wire  n174_reset; // @[Top.scala 223:22]
  wire  n174_valid_up; // @[Top.scala 223:22]
  wire  n174_valid_down; // @[Top.scala 223:22]
  wire [7:0] n174_I; // @[Top.scala 223:22]
  wire [7:0] n174_O; // @[Top.scala 223:22]
  wire  n175_clock; // @[Top.scala 226:22]
  wire  n175_reset; // @[Top.scala 226:22]
  wire  n175_valid_up; // @[Top.scala 226:22]
  wire  n175_valid_down; // @[Top.scala 226:22]
  wire [7:0] n175_I; // @[Top.scala 226:22]
  wire [7:0] n175_O; // @[Top.scala 226:22]
  FIFO n1 ( // @[Top.scala 124:20]
    .clock(n1_clock),
    .reset(n1_reset),
    .valid_up(n1_valid_up),
    .valid_down(n1_valid_down),
    .I(n1_I),
    .O(n1_O)
  );
  ShiftTN n2 ( // @[Top.scala 127:20]
    .clock(n2_clock),
    .reset(n2_reset),
    .valid_up(n2_valid_up),
    .valid_down(n2_valid_down),
    .I(n2_I),
    .O(n2_O)
  );
  ShiftTN n3 ( // @[Top.scala 130:20]
    .clock(n3_clock),
    .reset(n3_reset),
    .valid_up(n3_valid_up),
    .valid_down(n3_valid_down),
    .I(n3_I),
    .O(n3_O)
  );
  ShiftTN_2 n4 ( // @[Top.scala 133:20]
    .clock(n4_clock),
    .reset(n4_reset),
    .valid_up(n4_valid_up),
    .valid_down(n4_valid_down),
    .I(n4_I),
    .O(n4_O)
  );
  ShiftTN_2 n5 ( // @[Top.scala 136:20]
    .clock(n5_clock),
    .reset(n5_reset),
    .valid_up(n5_valid_up),
    .valid_down(n5_valid_down),
    .I(n5_I),
    .O(n5_O)
  );
  Map2T_1 n6 ( // @[Top.scala 139:20]
    .valid_up(n6_valid_up),
    .valid_down(n6_valid_down),
    .I0(n6_I0),
    .I1(n6_I1),
    .O_0(n6_O_0),
    .O_1(n6_O_1)
  );
  Map2T_3 n13 ( // @[Top.scala 143:21]
    .valid_up(n13_valid_up),
    .valid_down(n13_valid_down),
    .I0_0(n13_I0_0),
    .I0_1(n13_I0_1),
    .I1(n13_I1),
    .O_0(n13_O_0),
    .O_1(n13_O_1),
    .O_2(n13_O_2)
  );
  Passthrough n20 ( // @[Top.scala 147:21]
    .valid_up(n20_valid_up),
    .valid_down(n20_valid_down),
    .I_0(n20_I_0),
    .I_1(n20_I_1),
    .I_2(n20_I_2),
    .O_0_0(n20_O_0_0),
    .O_0_1(n20_O_0_1),
    .O_0_2(n20_O_0_2)
  );
  MapT n25 ( // @[Top.scala 150:21]
    .clock(n25_clock),
    .reset(n25_reset),
    .valid_up(n25_valid_up),
    .valid_down(n25_valid_down),
    .I_0_0(n25_I_0_0),
    .I_0_1(n25_I_0_1),
    .I_0_2(n25_I_0_2),
    .O_0(n25_O_0)
  );
  ShiftTN_2 n26 ( // @[Top.scala 153:21]
    .clock(n26_clock),
    .reset(n26_reset),
    .valid_up(n26_valid_up),
    .valid_down(n26_valid_down),
    .I(n26_I),
    .O(n26_O)
  );
  ShiftTN_2 n27 ( // @[Top.scala 156:21]
    .clock(n27_clock),
    .reset(n27_reset),
    .valid_up(n27_valid_up),
    .valid_down(n27_valid_down),
    .I(n27_I),
    .O(n27_O)
  );
  Map2T_1 n28 ( // @[Top.scala 159:21]
    .valid_up(n28_valid_up),
    .valid_down(n28_valid_down),
    .I0(n28_I0),
    .I1(n28_I1),
    .O_0(n28_O_0),
    .O_1(n28_O_1)
  );
  Map2T_3 n35 ( // @[Top.scala 163:21]
    .valid_up(n35_valid_up),
    .valid_down(n35_valid_down),
    .I0_0(n35_I0_0),
    .I0_1(n35_I0_1),
    .I1(n35_I1),
    .O_0(n35_O_0),
    .O_1(n35_O_1),
    .O_2(n35_O_2)
  );
  Passthrough n42 ( // @[Top.scala 167:21]
    .valid_up(n42_valid_up),
    .valid_down(n42_valid_down),
    .I_0(n42_I_0),
    .I_1(n42_I_1),
    .I_2(n42_I_2),
    .O_0_0(n42_O_0_0),
    .O_0_1(n42_O_0_1),
    .O_0_2(n42_O_0_2)
  );
  MapT n47 ( // @[Top.scala 170:21]
    .clock(n47_clock),
    .reset(n47_reset),
    .valid_up(n47_valid_up),
    .valid_down(n47_valid_down),
    .I_0_0(n47_I_0_0),
    .I_0_1(n47_I_0_1),
    .I_0_2(n47_I_0_2),
    .O_0(n47_O_0)
  );
  Map2T_8 n48 ( // @[Top.scala 173:21]
    .valid_up(n48_valid_up),
    .valid_down(n48_valid_down),
    .I0_0(n48_I0_0),
    .I1_0(n48_I1_0),
    .O_0_0(n48_O_0_0),
    .O_0_1(n48_O_0_1)
  );
  ShiftTN_2 n55 ( // @[Top.scala 177:21]
    .clock(n55_clock),
    .reset(n55_reset),
    .valid_up(n55_valid_up),
    .valid_down(n55_valid_down),
    .I(n55_I),
    .O(n55_O)
  );
  ShiftTN_2 n56 ( // @[Top.scala 180:21]
    .clock(n56_clock),
    .reset(n56_reset),
    .valid_up(n56_valid_up),
    .valid_down(n56_valid_down),
    .I(n56_I),
    .O(n56_O)
  );
  Map2T_1 n57 ( // @[Top.scala 183:21]
    .valid_up(n57_valid_up),
    .valid_down(n57_valid_down),
    .I0(n57_I0),
    .I1(n57_I1),
    .O_0(n57_O_0),
    .O_1(n57_O_1)
  );
  Map2T_3 n64 ( // @[Top.scala 187:21]
    .valid_up(n64_valid_up),
    .valid_down(n64_valid_down),
    .I0_0(n64_I0_0),
    .I0_1(n64_I0_1),
    .I1(n64_I1),
    .O_0(n64_O_0),
    .O_1(n64_O_1),
    .O_2(n64_O_2)
  );
  Passthrough n71 ( // @[Top.scala 191:21]
    .valid_up(n71_valid_up),
    .valid_down(n71_valid_down),
    .I_0(n71_I_0),
    .I_1(n71_I_1),
    .I_2(n71_I_2),
    .O_0_0(n71_O_0_0),
    .O_0_1(n71_O_0_1),
    .O_0_2(n71_O_0_2)
  );
  MapT n76 ( // @[Top.scala 194:21]
    .clock(n76_clock),
    .reset(n76_reset),
    .valid_up(n76_valid_up),
    .valid_down(n76_valid_down),
    .I_0_0(n76_I_0_0),
    .I_0_1(n76_I_0_1),
    .I_0_2(n76_I_0_2),
    .O_0(n76_O_0)
  );
  Map2T_13 n77 ( // @[Top.scala 197:21]
    .valid_up(n77_valid_up),
    .valid_down(n77_valid_down),
    .I0_0_0(n77_I0_0_0),
    .I0_0_1(n77_I0_0_1),
    .I1_0(n77_I1_0),
    .O_0_0(n77_O_0_0),
    .O_0_1(n77_O_0_1),
    .O_0_2(n77_O_0_2)
  );
  Passthrough_3 n84 ( // @[Top.scala 201:21]
    .valid_up(n84_valid_up),
    .valid_down(n84_valid_down),
    .I_0_0(n84_I_0_0),
    .I_0_1(n84_I_0_1),
    .I_0_2(n84_I_0_2),
    .O_0_0(n84_O_0_0),
    .O_0_1(n84_O_0_1),
    .O_0_2(n84_O_0_2)
  );
  MapT_3 n87 ( // @[Top.scala 204:21]
    .valid_up(n87_valid_up),
    .valid_down(n87_valid_down),
    .I_0_0(n87_I_0_0),
    .I_0_1(n87_I_0_1),
    .I_0_2(n87_I_0_2),
    .O_0(n87_O_0),
    .O_1(n87_O_1),
    .O_2(n87_O_2)
  );
  MapT_10 n140 ( // @[Top.scala 207:22]
    .clock(n140_clock),
    .reset(n140_reset),
    .valid_up(n140_valid_up),
    .valid_down(n140_valid_down),
    .I_0(n140_I_0),
    .I_1(n140_I_1),
    .I_2(n140_I_2),
    .O(n140_O)
  );
  Passthrough_6 n141 ( // @[Top.scala 210:22]
    .valid_up(n141_valid_up),
    .valid_down(n141_valid_down),
    .I(n141_I),
    .O(n141_O)
  );
  FIFO_1 n142 ( // @[Top.scala 213:22]
    .clock(n142_clock),
    .reset(n142_reset),
    .valid_up(n142_valid_up),
    .valid_down(n142_valid_down),
    .I(n142_I),
    .O(n142_O)
  );
  Map2T_19 n143 ( // @[Top.scala 216:22]
    .clock(n143_clock),
    .reset(n143_reset),
    .valid_up(n143_valid_up),
    .valid_down(n143_valid_down),
    .I0(n143_I0),
    .I1(n143_I1),
    .O(n143_O)
  );
  FIFO n173 ( // @[Top.scala 220:22]
    .clock(n173_clock),
    .reset(n173_reset),
    .valid_up(n173_valid_up),
    .valid_down(n173_valid_down),
    .I(n173_I),
    .O(n173_O)
  );
  FIFO n174 ( // @[Top.scala 223:22]
    .clock(n174_clock),
    .reset(n174_reset),
    .valid_up(n174_valid_up),
    .valid_down(n174_valid_down),
    .I(n174_I),
    .O(n174_O)
  );
  FIFO n175 ( // @[Top.scala 226:22]
    .clock(n175_clock),
    .reset(n175_reset),
    .valid_up(n175_valid_up),
    .valid_down(n175_valid_down),
    .I(n175_I),
    .O(n175_O)
  );
  assign valid_down = n175_valid_down; // @[Top.scala 230:16]
  assign O = n175_O; // @[Top.scala 229:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 126:17]
  assign n1_I = I; // @[Top.scala 125:10]
  assign n2_clock = clock;
  assign n2_reset = reset;
  assign n2_valid_up = n1_valid_down; // @[Top.scala 129:17]
  assign n2_I = n1_O; // @[Top.scala 128:10]
  assign n3_clock = clock;
  assign n3_reset = reset;
  assign n3_valid_up = n2_valid_down; // @[Top.scala 132:17]
  assign n3_I = n2_O; // @[Top.scala 131:10]
  assign n4_clock = clock;
  assign n4_reset = reset;
  assign n4_valid_up = n3_valid_down; // @[Top.scala 135:17]
  assign n4_I = n3_O; // @[Top.scala 134:10]
  assign n5_clock = clock;
  assign n5_reset = reset;
  assign n5_valid_up = n4_valid_down; // @[Top.scala 138:17]
  assign n5_I = n4_O; // @[Top.scala 137:10]
  assign n6_valid_up = n5_valid_down & n4_valid_down; // @[Top.scala 142:17]
  assign n6_I0 = n5_O; // @[Top.scala 140:11]
  assign n6_I1 = n4_O; // @[Top.scala 141:11]
  assign n13_valid_up = n6_valid_down & n3_valid_down; // @[Top.scala 146:18]
  assign n13_I0_0 = n6_O_0; // @[Top.scala 144:12]
  assign n13_I0_1 = n6_O_1; // @[Top.scala 144:12]
  assign n13_I1 = n3_O; // @[Top.scala 145:12]
  assign n20_valid_up = n13_valid_down; // @[Top.scala 149:18]
  assign n20_I_0 = n13_O_0; // @[Top.scala 148:11]
  assign n20_I_1 = n13_O_1; // @[Top.scala 148:11]
  assign n20_I_2 = n13_O_2; // @[Top.scala 148:11]
  assign n25_clock = clock;
  assign n25_reset = reset;
  assign n25_valid_up = n20_valid_down; // @[Top.scala 152:18]
  assign n25_I_0_0 = n20_O_0_0; // @[Top.scala 151:11]
  assign n25_I_0_1 = n20_O_0_1; // @[Top.scala 151:11]
  assign n25_I_0_2 = n20_O_0_2; // @[Top.scala 151:11]
  assign n26_clock = clock;
  assign n26_reset = reset;
  assign n26_valid_up = n2_valid_down; // @[Top.scala 155:18]
  assign n26_I = n2_O; // @[Top.scala 154:11]
  assign n27_clock = clock;
  assign n27_reset = reset;
  assign n27_valid_up = n26_valid_down; // @[Top.scala 158:18]
  assign n27_I = n26_O; // @[Top.scala 157:11]
  assign n28_valid_up = n27_valid_down & n26_valid_down; // @[Top.scala 162:18]
  assign n28_I0 = n27_O; // @[Top.scala 160:12]
  assign n28_I1 = n26_O; // @[Top.scala 161:12]
  assign n35_valid_up = n28_valid_down & n2_valid_down; // @[Top.scala 166:18]
  assign n35_I0_0 = n28_O_0; // @[Top.scala 164:12]
  assign n35_I0_1 = n28_O_1; // @[Top.scala 164:12]
  assign n35_I1 = n2_O; // @[Top.scala 165:12]
  assign n42_valid_up = n35_valid_down; // @[Top.scala 169:18]
  assign n42_I_0 = n35_O_0; // @[Top.scala 168:11]
  assign n42_I_1 = n35_O_1; // @[Top.scala 168:11]
  assign n42_I_2 = n35_O_2; // @[Top.scala 168:11]
  assign n47_clock = clock;
  assign n47_reset = reset;
  assign n47_valid_up = n42_valid_down; // @[Top.scala 172:18]
  assign n47_I_0_0 = n42_O_0_0; // @[Top.scala 171:11]
  assign n47_I_0_1 = n42_O_0_1; // @[Top.scala 171:11]
  assign n47_I_0_2 = n42_O_0_2; // @[Top.scala 171:11]
  assign n48_valid_up = n25_valid_down & n47_valid_down; // @[Top.scala 176:18]
  assign n48_I0_0 = n25_O_0; // @[Top.scala 174:12]
  assign n48_I1_0 = n47_O_0; // @[Top.scala 175:12]
  assign n55_clock = clock;
  assign n55_reset = reset;
  assign n55_valid_up = n1_valid_down; // @[Top.scala 179:18]
  assign n55_I = n1_O; // @[Top.scala 178:11]
  assign n56_clock = clock;
  assign n56_reset = reset;
  assign n56_valid_up = n55_valid_down; // @[Top.scala 182:18]
  assign n56_I = n55_O; // @[Top.scala 181:11]
  assign n57_valid_up = n56_valid_down & n55_valid_down; // @[Top.scala 186:18]
  assign n57_I0 = n56_O; // @[Top.scala 184:12]
  assign n57_I1 = n55_O; // @[Top.scala 185:12]
  assign n64_valid_up = n57_valid_down & n1_valid_down; // @[Top.scala 190:18]
  assign n64_I0_0 = n57_O_0; // @[Top.scala 188:12]
  assign n64_I0_1 = n57_O_1; // @[Top.scala 188:12]
  assign n64_I1 = n1_O; // @[Top.scala 189:12]
  assign n71_valid_up = n64_valid_down; // @[Top.scala 193:18]
  assign n71_I_0 = n64_O_0; // @[Top.scala 192:11]
  assign n71_I_1 = n64_O_1; // @[Top.scala 192:11]
  assign n71_I_2 = n64_O_2; // @[Top.scala 192:11]
  assign n76_clock = clock;
  assign n76_reset = reset;
  assign n76_valid_up = n71_valid_down; // @[Top.scala 196:18]
  assign n76_I_0_0 = n71_O_0_0; // @[Top.scala 195:11]
  assign n76_I_0_1 = n71_O_0_1; // @[Top.scala 195:11]
  assign n76_I_0_2 = n71_O_0_2; // @[Top.scala 195:11]
  assign n77_valid_up = n48_valid_down & n76_valid_down; // @[Top.scala 200:18]
  assign n77_I0_0_0 = n48_O_0_0; // @[Top.scala 198:12]
  assign n77_I0_0_1 = n48_O_0_1; // @[Top.scala 198:12]
  assign n77_I1_0 = n76_O_0; // @[Top.scala 199:12]
  assign n84_valid_up = n77_valid_down; // @[Top.scala 203:18]
  assign n84_I_0_0 = n77_O_0_0; // @[Top.scala 202:11]
  assign n84_I_0_1 = n77_O_0_1; // @[Top.scala 202:11]
  assign n84_I_0_2 = n77_O_0_2; // @[Top.scala 202:11]
  assign n87_valid_up = n84_valid_down; // @[Top.scala 206:18]
  assign n87_I_0_0 = n84_O_0_0; // @[Top.scala 205:11]
  assign n87_I_0_1 = n84_O_0_1; // @[Top.scala 205:11]
  assign n87_I_0_2 = n84_O_0_2; // @[Top.scala 205:11]
  assign n140_clock = clock;
  assign n140_reset = reset;
  assign n140_valid_up = n87_valid_down; // @[Top.scala 209:19]
  assign n140_I_0 = n87_O_0; // @[Top.scala 208:12]
  assign n140_I_1 = n87_O_1; // @[Top.scala 208:12]
  assign n140_I_2 = n87_O_2; // @[Top.scala 208:12]
  assign n141_valid_up = n140_valid_down; // @[Top.scala 212:19]
  assign n141_I = n140_O; // @[Top.scala 211:12]
  assign n142_clock = clock;
  assign n142_reset = reset;
  assign n142_valid_up = n1_valid_down; // @[Top.scala 215:19]
  assign n142_I = n1_O; // @[Top.scala 214:12]
  assign n143_clock = clock;
  assign n143_reset = reset;
  assign n143_valid_up = n141_valid_down & n142_valid_down; // @[Top.scala 219:19]
  assign n143_I0 = n141_O; // @[Top.scala 217:13]
  assign n143_I1 = n142_O; // @[Top.scala 218:13]
  assign n173_clock = clock;
  assign n173_reset = reset;
  assign n173_valid_up = n143_valid_down; // @[Top.scala 222:19]
  assign n173_I = n143_O; // @[Top.scala 221:12]
  assign n174_clock = clock;
  assign n174_reset = reset;
  assign n174_valid_up = n173_valid_down; // @[Top.scala 225:19]
  assign n174_I = n173_O; // @[Top.scala 224:12]
  assign n175_clock = clock;
  assign n175_reset = reset;
  assign n175_valid_up = n174_valid_down; // @[Top.scala 228:19]
  assign n175_I = n174_O; // @[Top.scala 227:12]
endmodule
