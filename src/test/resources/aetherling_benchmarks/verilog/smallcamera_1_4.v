module FIFO(
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
  input         clock,
  input         RE,
  input  [2:0]  RADDR,
  output [31:0] RDATA,
  input         WE,
  input  [2:0]  WADDR,
  input  [31:0] WDATA
);
  wire  write_elem_counter_CE; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_valid; // @[RAM_ST.scala 20:34]
  wire  read_elem_counter_CE; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_valid; // @[RAM_ST.scala 21:33]
  reg [31:0] ram [0:7]; // @[RAM_ST.scala 29:24]
  reg [31:0] _RAND_0;
  wire [31:0] ram__T_8_data; // @[RAM_ST.scala 29:24]
  wire [2:0] ram__T_8_addr; // @[RAM_ST.scala 29:24]
  wire [31:0] ram__T_2_data; // @[RAM_ST.scala 29:24]
  wire [2:0] ram__T_2_addr; // @[RAM_ST.scala 29:24]
  wire  ram__T_2_mask; // @[RAM_ST.scala 29:24]
  wire  ram__T_2_en; // @[RAM_ST.scala 29:24]
  reg  ram__T_8_en_pipe_0;
  reg [31:0] _RAND_1;
  reg [2:0] ram__T_8_addr_pipe_0;
  reg [31:0] _RAND_2;
  wire [2:0] _GEN_1 = 3'h1 == WADDR ? 3'h1 : 3'h0; // @[RAM_ST.scala 31:71]
  wire [2:0] _GEN_2 = 3'h2 == WADDR ? 3'h2 : _GEN_1; // @[RAM_ST.scala 31:71]
  wire [2:0] _GEN_3 = 3'h3 == WADDR ? 3'h3 : _GEN_2; // @[RAM_ST.scala 31:71]
  wire [2:0] _GEN_4 = 3'h4 == WADDR ? 3'h4 : _GEN_3; // @[RAM_ST.scala 31:71]
  wire [2:0] _GEN_5 = 3'h5 == WADDR ? 3'h5 : _GEN_4; // @[RAM_ST.scala 31:71]
  wire [2:0] _GEN_6 = 3'h6 == WADDR ? 3'h6 : _GEN_5; // @[RAM_ST.scala 31:71]
  wire [2:0] _GEN_7 = 3'h7 == WADDR ? 3'h7 : _GEN_6; // @[RAM_ST.scala 31:71]
  wire [3:0] _T = {{1'd0}, _GEN_7}; // @[RAM_ST.scala 31:71]
  wire [2:0] _GEN_14 = 3'h1 == RADDR ? 3'h1 : 3'h0; // @[RAM_ST.scala 32:46]
  wire [2:0] _GEN_15 = 3'h2 == RADDR ? 3'h2 : _GEN_14; // @[RAM_ST.scala 32:46]
  wire [2:0] _GEN_16 = 3'h3 == RADDR ? 3'h3 : _GEN_15; // @[RAM_ST.scala 32:46]
  wire [2:0] _GEN_17 = 3'h4 == RADDR ? 3'h4 : _GEN_16; // @[RAM_ST.scala 32:46]
  wire [2:0] _GEN_18 = 3'h5 == RADDR ? 3'h5 : _GEN_17; // @[RAM_ST.scala 32:46]
  wire [2:0] _GEN_19 = 3'h6 == RADDR ? 3'h6 : _GEN_18; // @[RAM_ST.scala 32:46]
  wire [2:0] _GEN_20 = 3'h7 == RADDR ? 3'h7 : _GEN_19; // @[RAM_ST.scala 32:46]
  wire [3:0] _T_3 = {{1'd0}, _GEN_20}; // @[RAM_ST.scala 32:46]
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
  assign ram__T_2_addr = _T[2:0];
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
  for (initvar = 0; initvar < 8; initvar = initvar+1)
    ram[initvar] = _RAND_0[31:0];
  `endif // RANDOMIZE_MEM_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  ram__T_8_en_pipe_0 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  ram__T_8_addr_pipe_0 = _RAND_2[2:0];
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
      ram__T_8_addr_pipe_0 <= _T_3[2:0];
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
      value <= _T_4;
    end
  end
endmodule
module ShiftTN(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  wire  value_store_clock; // @[ShiftTN.scala 42:27]
  wire  value_store_RE; // @[ShiftTN.scala 42:27]
  wire [2:0] value_store_RADDR; // @[ShiftTN.scala 42:27]
  wire [31:0] value_store_RDATA; // @[ShiftTN.scala 42:27]
  wire  value_store_WE; // @[ShiftTN.scala 42:27]
  wire [2:0] value_store_WADDR; // @[ShiftTN.scala 42:27]
  wire [31:0] value_store_WDATA; // @[ShiftTN.scala 42:27]
  wire  next_ram_addr_CE; // @[ShiftTN.scala 44:29]
  wire  next_ram_addr_valid; // @[ShiftTN.scala 44:29]
  wire  inner_valid_clock; // @[ShiftTN.scala 56:27]
  wire  inner_valid_reset; // @[ShiftTN.scala 56:27]
  wire  inner_valid_CE; // @[ShiftTN.scala 56:27]
  wire  inner_valid_valid; // @[ShiftTN.scala 56:27]
  wire  _T_2 = valid_up & inner_valid_valid; // @[ShiftTN.scala 58:74]
  reg [2:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_4 = value == 3'h7; // @[Counter.scala 38:24]
  wire [2:0] _T_6 = value + 3'h1; // @[Counter.scala 39:22]
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
  assign value_store_RADDR = _T_4 ? 3'h0 : _T_6; // @[ShiftTN.scala 61:74 ShiftTN.scala 62:36]
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
  value = _RAND_0[2:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 3'h0;
    end else if (_T_2) begin
      value <= _T_6;
    end
  end
endmodule
module RAM_ST_2(
  input         clock,
  input         RE,
  output [31:0] RDATA,
  input         WE,
  input  [31:0] WDATA
);
  wire  write_elem_counter_CE; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_valid; // @[RAM_ST.scala 20:34]
  wire  read_elem_counter_CE; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_valid; // @[RAM_ST.scala 21:33]
  reg [31:0] ram [0:0]; // @[RAM_ST.scala 29:24]
  reg [31:0] _RAND_0;
  wire [31:0] ram__T_6_data; // @[RAM_ST.scala 29:24]
  wire  ram__T_6_addr; // @[RAM_ST.scala 29:24]
  wire [31:0] ram__T_2_data; // @[RAM_ST.scala 29:24]
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
    ram[initvar] = _RAND_0[31:0];
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
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  wire  value_store_clock; // @[ShiftTN.scala 42:27]
  wire  value_store_RE; // @[ShiftTN.scala 42:27]
  wire [31:0] value_store_RDATA; // @[ShiftTN.scala 42:27]
  wire  value_store_WE; // @[ShiftTN.scala 42:27]
  wire [31:0] value_store_WDATA; // @[ShiftTN.scala 42:27]
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
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [31:0] I1,
  output [31:0] O_0,
  output [31:0] O_1
);
  assign valid_down = valid_up; // @[Tuple.scala 15:14]
  assign O_0 = I0; // @[Tuple.scala 12:32]
  assign O_1 = I1; // @[Tuple.scala 13:32]
endmodule
module Map2T(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [31:0] I1,
  output [31:0] O_0,
  output [31:0] O_1
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0; // @[Map2T.scala 8:20]
  wire [31:0] op_I1; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0; // @[Map2T.scala 8:20]
  wire [31:0] op_O_1; // @[Map2T.scala 8:20]
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
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [31:0] I1,
  output [31:0] O_0,
  output [31:0] O_1
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0; // @[Map2T.scala 8:20]
  wire [31:0] op_I1; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0; // @[Map2T.scala 8:20]
  wire [31:0] op_O_1; // @[Map2T.scala 8:20]
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
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_0,
  input  [31:0] I0_1,
  input  [31:0] I1,
  output [31:0] O_0,
  output [31:0] O_1,
  output [31:0] O_2
);
  assign valid_down = valid_up; // @[Tuple.scala 28:14]
  assign O_0 = I0_0; // @[Tuple.scala 24:34]
  assign O_1 = I0_1; // @[Tuple.scala 24:34]
  assign O_2 = I1; // @[Tuple.scala 26:32]
endmodule
module Map2T_2(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_0,
  input  [31:0] I0_1,
  input  [31:0] I1,
  output [31:0] O_0,
  output [31:0] O_1,
  output [31:0] O_2
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_1; // @[Map2T.scala 8:20]
  wire [31:0] op_I1; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0; // @[Map2T.scala 8:20]
  wire [31:0] op_O_1; // @[Map2T.scala 8:20]
  wire [31:0] op_O_2; // @[Map2T.scala 8:20]
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
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_0,
  input  [31:0] I0_1,
  input  [31:0] I1,
  output [31:0] O_0,
  output [31:0] O_1,
  output [31:0] O_2
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_1; // @[Map2T.scala 8:20]
  wire [31:0] op_I1; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0; // @[Map2T.scala 8:20]
  wire [31:0] op_O_1; // @[Map2T.scala 8:20]
  wire [31:0] op_O_2; // @[Map2T.scala 8:20]
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
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O_0_0,
  output [31:0] O_0_1,
  output [31:0] O_0_2
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_0_0 = I_0; // @[Passthrough.scala 17:68]
  assign O_0_1 = I_1; // @[Passthrough.scala 17:68]
  assign O_0_2 = I_2; // @[Passthrough.scala 17:68]
endmodule
module Serialize(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O
);
  wire  elem_counter_CE; // @[Serialize.scala 14:28]
  wire  elem_counter_valid; // @[Serialize.scala 14:28]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire [1:0] _T_4 = value + 2'h1; // @[Counter.scala 39:22]
  reg [31:0] mux_input_wire_1; // @[Serialize.scala 39:47]
  reg [31:0] _RAND_1;
  reg [31:0] mux_input_wire_2; // @[Serialize.scala 39:47]
  reg [31:0] _RAND_2;
  wire  _T_7 = value == 2'h0; // @[Serialize.scala 42:34]
  wire  _T_8 = _T_7 & valid_up; // @[Serialize.scala 42:42]
  wire  _T_11 = 2'h2 == value; // @[Mux.scala 68:19]
  wire  _T_13 = 2'h1 == value; // @[Mux.scala 68:19]
  wire  _T_15 = 2'h0 == value; // @[Mux.scala 68:19]
  reg [31:0] _T_17; // @[Serialize.scala 53:15]
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
  mux_input_wire_1 = _RAND_1[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  mux_input_wire_2 = _RAND_2[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_17 = _RAND_3[31:0];
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
      value <= _T_4;
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
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0_0,
  input  [31:0] I_0_1,
  input  [31:0] I_0_2,
  output [31:0] O_0
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [31:0] fst_op_I_0; // @[MapS.scala 9:22]
  wire [31:0] fst_op_I_1; // @[MapS.scala 9:22]
  wire [31:0] fst_op_I_2; // @[MapS.scala 9:22]
  wire [31:0] fst_op_O; // @[MapS.scala 9:22]
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
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0_0,
  input  [31:0] I_0_1,
  input  [31:0] I_0_2,
  output [31:0] O_0
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_0_0; // @[MapT.scala 8:20]
  wire [31:0] op_I_0_1; // @[MapT.scala 8:20]
  wire [31:0] op_I_0_2; // @[MapT.scala 8:20]
  wire [31:0] op_O_0; // @[MapT.scala 8:20]
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
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_0,
  input  [31:0] I1_0,
  output [31:0] O_0_0,
  output [31:0] O_0_1
);
  wire  fst_op_valid_up; // @[Map2S.scala 9:22]
  wire  fst_op_valid_down; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_I0; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_I1; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_O_0; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_O_1; // @[Map2S.scala 9:22]
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
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_0,
  input  [31:0] I1_0,
  output [31:0] O_0_0,
  output [31:0] O_0_1
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_0; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0_0; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0_1; // @[Map2T.scala 8:20]
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
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_0_0,
  input  [31:0] I0_0_1,
  input  [31:0] I1_0,
  output [31:0] O_0_0,
  output [31:0] O_0_1,
  output [31:0] O_0_2
);
  wire  fst_op_valid_up; // @[Map2S.scala 9:22]
  wire  fst_op_valid_down; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_I0_0; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_I0_1; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_I1; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_O_0; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_O_1; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_O_2; // @[Map2S.scala 9:22]
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
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_0_0,
  input  [31:0] I0_0_1,
  input  [31:0] I1_0,
  output [31:0] O_0_0,
  output [31:0] O_0_1,
  output [31:0] O_0_2
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_0_0; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_0_1; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_0; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0_0; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0_1; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0_2; // @[Map2T.scala 8:20]
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
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0_0,
  input  [31:0] I_0_1,
  input  [31:0] I_0_2,
  output [31:0] O_0_0_0,
  output [31:0] O_0_0_1,
  output [31:0] O_0_0_2
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_0_0_0 = I_0_0; // @[Passthrough.scala 17:68]
  assign O_0_0_1 = I_0_1; // @[Passthrough.scala 17:68]
  assign O_0_0_2 = I_0_2; // @[Passthrough.scala 17:68]
endmodule
module Passthrough_4(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0_0,
  input  [31:0] I_0_1,
  input  [31:0] I_0_2,
  output [31:0] O_0,
  output [31:0] O_1,
  output [31:0] O_2
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_0 = I_0_0; // @[Passthrough.scala 17:68]
  assign O_1 = I_0_1; // @[Passthrough.scala 17:68]
  assign O_2 = I_0_2; // @[Passthrough.scala 17:68]
endmodule
module MapS_3(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0_0_0,
  input  [31:0] I_0_0_1,
  input  [31:0] I_0_0_2,
  output [31:0] O_0_0,
  output [31:0] O_0_1,
  output [31:0] O_0_2
);
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [31:0] fst_op_I_0_0; // @[MapS.scala 9:22]
  wire [31:0] fst_op_I_0_1; // @[MapS.scala 9:22]
  wire [31:0] fst_op_I_0_2; // @[MapS.scala 9:22]
  wire [31:0] fst_op_O_0; // @[MapS.scala 9:22]
  wire [31:0] fst_op_O_1; // @[MapS.scala 9:22]
  wire [31:0] fst_op_O_2; // @[MapS.scala 9:22]
  Passthrough_4 fst_op ( // @[MapS.scala 9:22]
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_0_0(fst_op_I_0_0),
    .I_0_1(fst_op_I_0_1),
    .I_0_2(fst_op_I_0_2),
    .O_0(fst_op_O_0),
    .O_1(fst_op_O_1),
    .O_2(fst_op_O_2)
  );
  assign valid_down = fst_op_valid_down; // @[MapS.scala 23:14]
  assign O_0_0 = fst_op_O_0; // @[MapS.scala 17:8]
  assign O_0_1 = fst_op_O_1; // @[MapS.scala 17:8]
  assign O_0_2 = fst_op_O_2; // @[MapS.scala 17:8]
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_0_0 = I_0_0_0; // @[MapS.scala 16:12]
  assign fst_op_I_0_1 = I_0_0_1; // @[MapS.scala 16:12]
  assign fst_op_I_0_2 = I_0_0_2; // @[MapS.scala 16:12]
endmodule
module MapT_3(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0_0_0,
  input  [31:0] I_0_0_1,
  input  [31:0] I_0_0_2,
  output [31:0] O_0_0,
  output [31:0] O_0_1,
  output [31:0] O_0_2
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_0_0_0; // @[MapT.scala 8:20]
  wire [31:0] op_I_0_0_1; // @[MapT.scala 8:20]
  wire [31:0] op_I_0_0_2; // @[MapT.scala 8:20]
  wire [31:0] op_O_0_0; // @[MapT.scala 8:20]
  wire [31:0] op_O_0_1; // @[MapT.scala 8:20]
  wire [31:0] op_O_0_2; // @[MapT.scala 8:20]
  MapS_3 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_0_0(op_I_0_0_0),
    .I_0_0_1(op_I_0_0_1),
    .I_0_0_2(op_I_0_0_2),
    .O_0_0(op_O_0_0),
    .O_0_1(op_O_0_1),
    .O_0_2(op_O_0_2)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_0 = op_O_0_0; // @[MapT.scala 15:7]
  assign O_0_1 = op_O_0_1; // @[MapT.scala 15:7]
  assign O_0_2 = op_O_0_2; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_0_0 = I_0_0_0; // @[MapT.scala 14:10]
  assign op_I_0_0_1 = I_0_0_1; // @[MapT.scala 14:10]
  assign op_I_0_0_2 = I_0_0_2; // @[MapT.scala 14:10]
endmodule
module Passthrough_5(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0_0,
  input  [31:0] I_0_1,
  input  [31:0] I_0_2,
  output [31:0] O_0_0,
  output [31:0] O_0_1,
  output [31:0] O_0_2
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_0_0 = I_0_0; // @[Passthrough.scala 17:68]
  assign O_0_1 = I_0_1; // @[Passthrough.scala 17:68]
  assign O_0_2 = I_0_2; // @[Passthrough.scala 17:68]
endmodule
module NestedCounters_44(
  input   clock,
  input   reset,
  output  valid,
  output  last
);
  wire  NestedCounters_CE; // @[NestedCounters.scala 43:31]
  wire  NestedCounters_valid; // @[NestedCounters.scala 43:31]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire [1:0] _T_4 = value + 2'h1; // @[Counter.scala 39:22]
  wire  _T_7 = value < 2'h1; // @[NestedCounters.scala 48:35]
  NestedCounters NestedCounters ( // @[NestedCounters.scala 43:31]
    .CE(NestedCounters_CE),
    .valid(NestedCounters_valid)
  );
  assign valid = _T_7 & NestedCounters_valid; // @[NestedCounters.scala 48:11]
  assign last = value == 2'h3; // @[NestedCounters.scala 47:10]
  assign NestedCounters_CE = 1'h1; // @[NestedCounters.scala 49:22]
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
    end else begin
      value <= _T_4;
    end
  end
endmodule
module NestedCounters_45(
  input   clock,
  input   reset,
  output  valid,
  output  last
);
  wire  NestedCounters_clock; // @[NestedCounters.scala 43:31]
  wire  NestedCounters_reset; // @[NestedCounters.scala 43:31]
  wire  NestedCounters_valid; // @[NestedCounters.scala 43:31]
  wire  NestedCounters_last; // @[NestedCounters.scala 43:31]
  wire  _T = NestedCounters_last; // @[NestedCounters.scala 45:47]
  reg [3:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_2 = value == 4'hf; // @[Counter.scala 38:24]
  wire [3:0] _T_4 = value + 4'h1; // @[Counter.scala 39:22]
  NestedCounters_44 NestedCounters ( // @[NestedCounters.scala 43:31]
    .clock(NestedCounters_clock),
    .reset(NestedCounters_reset),
    .valid(NestedCounters_valid),
    .last(NestedCounters_last)
  );
  assign valid = NestedCounters_valid; // @[NestedCounters.scala 48:11]
  assign last = _T_2 & NestedCounters_last; // @[NestedCounters.scala 47:10]
  assign NestedCounters_clock = clock;
  assign NestedCounters_reset = reset;
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
    end else if (_T) begin
      value <= _T_4;
    end
  end
endmodule
module Counter_TN(
  input         clock,
  input         reset,
  output [31:0] O
);
  wire  nested_counters_clock; // @[Counter.scala 118:31]
  wire  nested_counters_reset; // @[Counter.scala 118:31]
  wire  nested_counters_valid; // @[Counter.scala 118:31]
  wire  nested_counters_last; // @[Counter.scala 118:31]
  reg [31:0] counter_value; // @[Counter.scala 134:30]
  reg [31:0] _RAND_0;
  wire  _T = nested_counters_last; // @[Counter.scala 141:19]
  wire  _T_1 = nested_counters_valid; // @[Counter.scala 142:25]
  wire [31:0] _T_3 = counter_value + 32'h1; // @[Counter.scala 142:97]
  NestedCounters_45 nested_counters ( // @[Counter.scala 118:31]
    .clock(nested_counters_clock),
    .reset(nested_counters_reset),
    .valid(nested_counters_valid),
    .last(nested_counters_last)
  );
  assign O = counter_value; // @[Counter.scala 146:5]
  assign nested_counters_clock = clock;
  assign nested_counters_reset = reset;
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
  counter_value = _RAND_0[31:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      counter_value <= 32'h0;
    end else if (_T) begin
      counter_value <= 32'h0;
    end else if (_T_1) begin
      counter_value <= _T_3;
    end
  end
endmodule
module AtomTuple(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [31:0] I1,
  output [31:0] O_t0b,
  output [31:0] O_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b = I0; // @[Tuple.scala 49:9]
  assign O_t1b = I1; // @[Tuple.scala 50:9]
endmodule
module Lt(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [31:0] I_t1b,
  output [31:0] O
);
  wire  _T = I_t0b < I_t1b; // @[Arithmetic.scala 465:25]
  assign valid_down = valid_up; // @[Arithmetic.scala 467:14]
  assign O = {{31'd0}, _T}; // @[Arithmetic.scala 465:7]
endmodule
module LogicalNot(
  input   valid_up,
  output  valid_down,
  input   I,
  output  O
);
  assign valid_down = valid_up; // @[Arithmetic.scala 46:14]
  assign O = ~I; // @[Arithmetic.scala 45:5]
endmodule
module Module_0(
  output        valid_down,
  input  [31:0] I,
  output        O
);
  wire  n96_valid_up; // @[Top.scala 18:21]
  wire  n96_valid_down; // @[Top.scala 18:21]
  wire [31:0] n96_I0; // @[Top.scala 18:21]
  wire [31:0] n96_I1; // @[Top.scala 18:21]
  wire [31:0] n96_O_t0b; // @[Top.scala 18:21]
  wire [31:0] n96_O_t1b; // @[Top.scala 18:21]
  wire  n97_valid_up; // @[Top.scala 22:21]
  wire  n97_valid_down; // @[Top.scala 22:21]
  wire [31:0] n97_I_t0b; // @[Top.scala 22:21]
  wire [31:0] n97_I_t1b; // @[Top.scala 22:21]
  wire [31:0] n97_O; // @[Top.scala 22:21]
  wire  n98_valid_up; // @[Top.scala 25:21]
  wire  n98_valid_down; // @[Top.scala 25:21]
  wire  n98_I; // @[Top.scala 25:21]
  wire  n98_O; // @[Top.scala 25:21]
  AtomTuple n96 ( // @[Top.scala 18:21]
    .valid_up(n96_valid_up),
    .valid_down(n96_valid_down),
    .I0(n96_I0),
    .I1(n96_I1),
    .O_t0b(n96_O_t0b),
    .O_t1b(n96_O_t1b)
  );
  Lt n97 ( // @[Top.scala 22:21]
    .valid_up(n97_valid_up),
    .valid_down(n97_valid_down),
    .I_t0b(n97_I_t0b),
    .I_t1b(n97_I_t1b),
    .O(n97_O)
  );
  LogicalNot n98 ( // @[Top.scala 25:21]
    .valid_up(n98_valid_up),
    .valid_down(n98_valid_down),
    .I(n98_I),
    .O(n98_O)
  );
  assign valid_down = n98_valid_down; // @[Top.scala 29:16]
  assign O = n98_O; // @[Top.scala 28:7]
  assign n96_valid_up = 1'h1; // @[Top.scala 21:18]
  assign n96_I0 = I; // @[Top.scala 19:12]
  assign n96_I1 = 32'h8; // @[Top.scala 20:12]
  assign n97_valid_up = n96_valid_down; // @[Top.scala 24:18]
  assign n97_I_t0b = n96_O_t0b; // @[Top.scala 23:11]
  assign n97_I_t1b = n96_O_t1b; // @[Top.scala 23:11]
  assign n98_valid_up = n97_valid_down; // @[Top.scala 27:18]
  assign n98_I = n97_O[0]; // @[Top.scala 26:11]
endmodule
module MapT_4(
  output        valid_down,
  input  [31:0] I,
  output        O
);
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I; // @[MapT.scala 8:20]
  wire  op_O; // @[MapT.scala 8:20]
  Module_0 op ( // @[MapT.scala 8:20]
    .valid_down(op_valid_down),
    .I(op_I),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_I = I; // @[MapT.scala 14:10]
endmodule
module MapT_5(
  output        valid_down,
  input  [31:0] I,
  output        O
);
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I; // @[MapT.scala 8:20]
  wire  op_O; // @[MapT.scala 8:20]
  MapT_4 op ( // @[MapT.scala 8:20]
    .valid_down(op_valid_down),
    .I(op_I),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_I = I; // @[MapT.scala 14:10]
endmodule
module AtomTuple_1(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [7:0]  I1,
  output [31:0] O_t0b,
  output [7:0]  O_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b = I0; // @[Tuple.scala 49:9]
  assign O_t1b = I1; // @[Tuple.scala 50:9]
endmodule
module RShift(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [7:0]  I_t1b,
  output [31:0] O
);
  assign valid_down = valid_up; // @[Arithmetic.scala 405:14]
  assign O = I_t0b >> I_t1b; // @[Arithmetic.scala 403:7]
endmodule
module LShift(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [7:0]  I_t1b,
  output [31:0] O
);
  wire [286:0] _GEN_0 = {{255'd0}, I_t0b}; // @[Arithmetic.scala 434:25]
  wire [286:0] _T = _GEN_0 << I_t1b; // @[Arithmetic.scala 434:25]
  assign valid_down = valid_up; // @[Arithmetic.scala 436:14]
  assign O = _T[31:0]; // @[Arithmetic.scala 434:7]
endmodule
module Eq(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [31:0] I_t1b,
  output [31:0] O
);
  wire  _T = I_t0b == I_t1b; // @[Arithmetic.scala 497:25]
  assign valid_down = valid_up; // @[Arithmetic.scala 499:14]
  assign O = {{31'd0}, _T}; // @[Arithmetic.scala 497:7]
endmodule
module Module_1(
  output        valid_down,
  input  [31:0] I,
  output        O
);
  wire  n104_valid_up; // @[Top.scala 36:22]
  wire  n104_valid_down; // @[Top.scala 36:22]
  wire [31:0] n104_I0; // @[Top.scala 36:22]
  wire [7:0] n104_I1; // @[Top.scala 36:22]
  wire [31:0] n104_O_t0b; // @[Top.scala 36:22]
  wire [7:0] n104_O_t1b; // @[Top.scala 36:22]
  wire  n105_valid_up; // @[Top.scala 40:22]
  wire  n105_valid_down; // @[Top.scala 40:22]
  wire [31:0] n105_I_t0b; // @[Top.scala 40:22]
  wire [7:0] n105_I_t1b; // @[Top.scala 40:22]
  wire [31:0] n105_O; // @[Top.scala 40:22]
  wire  n106_valid_up; // @[Top.scala 43:22]
  wire  n106_valid_down; // @[Top.scala 43:22]
  wire [31:0] n106_I0; // @[Top.scala 43:22]
  wire [7:0] n106_I1; // @[Top.scala 43:22]
  wire [31:0] n106_O_t0b; // @[Top.scala 43:22]
  wire [7:0] n106_O_t1b; // @[Top.scala 43:22]
  wire  n107_valid_up; // @[Top.scala 47:22]
  wire  n107_valid_down; // @[Top.scala 47:22]
  wire [31:0] n107_I_t0b; // @[Top.scala 47:22]
  wire [7:0] n107_I_t1b; // @[Top.scala 47:22]
  wire [31:0] n107_O; // @[Top.scala 47:22]
  wire  n108_valid_up; // @[Top.scala 50:22]
  wire  n108_valid_down; // @[Top.scala 50:22]
  wire [31:0] n108_I0; // @[Top.scala 50:22]
  wire [31:0] n108_I1; // @[Top.scala 50:22]
  wire [31:0] n108_O_t0b; // @[Top.scala 50:22]
  wire [31:0] n108_O_t1b; // @[Top.scala 50:22]
  wire  n109_valid_up; // @[Top.scala 54:22]
  wire  n109_valid_down; // @[Top.scala 54:22]
  wire [31:0] n109_I_t0b; // @[Top.scala 54:22]
  wire [31:0] n109_I_t1b; // @[Top.scala 54:22]
  wire [31:0] n109_O; // @[Top.scala 54:22]
  wire  n110_valid_up; // @[Top.scala 57:22]
  wire  n110_valid_down; // @[Top.scala 57:22]
  wire  n110_I; // @[Top.scala 57:22]
  wire  n110_O; // @[Top.scala 57:22]
  AtomTuple_1 n104 ( // @[Top.scala 36:22]
    .valid_up(n104_valid_up),
    .valid_down(n104_valid_down),
    .I0(n104_I0),
    .I1(n104_I1),
    .O_t0b(n104_O_t0b),
    .O_t1b(n104_O_t1b)
  );
  RShift n105 ( // @[Top.scala 40:22]
    .valid_up(n105_valid_up),
    .valid_down(n105_valid_down),
    .I_t0b(n105_I_t0b),
    .I_t1b(n105_I_t1b),
    .O(n105_O)
  );
  AtomTuple_1 n106 ( // @[Top.scala 43:22]
    .valid_up(n106_valid_up),
    .valid_down(n106_valid_down),
    .I0(n106_I0),
    .I1(n106_I1),
    .O_t0b(n106_O_t0b),
    .O_t1b(n106_O_t1b)
  );
  LShift n107 ( // @[Top.scala 47:22]
    .valid_up(n107_valid_up),
    .valid_down(n107_valid_down),
    .I_t0b(n107_I_t0b),
    .I_t1b(n107_I_t1b),
    .O(n107_O)
  );
  AtomTuple n108 ( // @[Top.scala 50:22]
    .valid_up(n108_valid_up),
    .valid_down(n108_valid_down),
    .I0(n108_I0),
    .I1(n108_I1),
    .O_t0b(n108_O_t0b),
    .O_t1b(n108_O_t1b)
  );
  Eq n109 ( // @[Top.scala 54:22]
    .valid_up(n109_valid_up),
    .valid_down(n109_valid_down),
    .I_t0b(n109_I_t0b),
    .I_t1b(n109_I_t1b),
    .O(n109_O)
  );
  LogicalNot n110 ( // @[Top.scala 57:22]
    .valid_up(n110_valid_up),
    .valid_down(n110_valid_down),
    .I(n110_I),
    .O(n110_O)
  );
  assign valid_down = n110_valid_down; // @[Top.scala 61:16]
  assign O = n110_O; // @[Top.scala 60:7]
  assign n104_valid_up = 1'h1; // @[Top.scala 39:19]
  assign n104_I0 = I; // @[Top.scala 37:13]
  assign n104_I1 = 8'h1; // @[Top.scala 38:13]
  assign n105_valid_up = n104_valid_down; // @[Top.scala 42:19]
  assign n105_I_t0b = n104_O_t0b; // @[Top.scala 41:12]
  assign n105_I_t1b = n104_O_t1b; // @[Top.scala 41:12]
  assign n106_valid_up = n105_valid_down; // @[Top.scala 46:19]
  assign n106_I0 = n105_O; // @[Top.scala 44:13]
  assign n106_I1 = 8'h1; // @[Top.scala 45:13]
  assign n107_valid_up = n106_valid_down; // @[Top.scala 49:19]
  assign n107_I_t0b = n106_O_t0b; // @[Top.scala 48:12]
  assign n107_I_t1b = n106_O_t1b; // @[Top.scala 48:12]
  assign n108_valid_up = n107_valid_down; // @[Top.scala 53:19]
  assign n108_I0 = I; // @[Top.scala 51:13]
  assign n108_I1 = n107_O; // @[Top.scala 52:13]
  assign n109_valid_up = n108_valid_down; // @[Top.scala 56:19]
  assign n109_I_t0b = n108_O_t0b; // @[Top.scala 55:12]
  assign n109_I_t1b = n108_O_t1b; // @[Top.scala 55:12]
  assign n110_valid_up = n109_valid_down; // @[Top.scala 59:19]
  assign n110_I = n109_O[0]; // @[Top.scala 58:12]
endmodule
module MapT_6(
  output        valid_down,
  input  [31:0] I,
  output        O
);
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I; // @[MapT.scala 8:20]
  wire  op_O; // @[MapT.scala 8:20]
  Module_1 op ( // @[MapT.scala 8:20]
    .valid_down(op_valid_down),
    .I(op_I),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_I = I; // @[MapT.scala 14:10]
endmodule
module MapT_7(
  output        valid_down,
  input  [31:0] I,
  output        O
);
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I; // @[MapT.scala 8:20]
  wire  op_O; // @[MapT.scala 8:20]
  MapT_6 op ( // @[MapT.scala 8:20]
    .valid_down(op_valid_down),
    .I(op_I),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_I = I; // @[MapT.scala 14:10]
endmodule
module AtomTuple_4(
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
module Map2T_14(
  input   valid_up,
  output  valid_down,
  input   I0,
  input   I1,
  output  O_t0b,
  output  O_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire  op_I0; // @[Map2T.scala 8:20]
  wire  op_I1; // @[Map2T.scala 8:20]
  wire  op_O_t0b; // @[Map2T.scala 8:20]
  wire  op_O_t1b; // @[Map2T.scala 8:20]
  AtomTuple_4 op ( // @[Map2T.scala 8:20]
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
module Map2T_15(
  input   valid_up,
  output  valid_down,
  input   I0,
  input   I1,
  output  O_t0b,
  output  O_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire  op_I0; // @[Map2T.scala 8:20]
  wire  op_I1; // @[Map2T.scala 8:20]
  wire  op_O_t0b; // @[Map2T.scala 8:20]
  wire  op_O_t1b; // @[Map2T.scala 8:20]
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
module Passthrough_6(
  input   valid_up,
  output  valid_down,
  input   I_t0b,
  input   I_t1b,
  output  O_t0b,
  output  O_t1b
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_t0b = I_t0b; // @[Passthrough.scala 17:68]
  assign O_t1b = I_t1b; // @[Passthrough.scala 17:68]
endmodule
module Passthrough_7(
  input   valid_up,
  output  valid_down,
  input   I_t0b,
  input   I_t1b,
  output  O_0_t0b,
  output  O_0_t1b
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_0_t0b = I_t0b; // @[Passthrough.scala 17:68]
  assign O_0_t1b = I_t1b; // @[Passthrough.scala 17:68]
endmodule
module FIFO_1(
  input   clock,
  input   reset,
  input   valid_up,
  output  valid_down,
  input   I_0_t0b,
  input   I_0_t1b,
  output  O_0_t0b,
  output  O_0_t1b
);
  reg  _T_0_t0b [0:2]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_0;
  wire  _T_0_t0b__T_15_data; // @[FIFO.scala 23:33]
  wire [1:0] _T_0_t0b__T_15_addr; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_1;
  wire  _T_0_t0b__T_5_data; // @[FIFO.scala 23:33]
  wire [1:0] _T_0_t0b__T_5_addr; // @[FIFO.scala 23:33]
  wire  _T_0_t0b__T_5_mask; // @[FIFO.scala 23:33]
  wire  _T_0_t0b__T_5_en; // @[FIFO.scala 23:33]
  reg  _T_0_t0b__T_15_en_pipe_0;
  reg [31:0] _RAND_2;
  reg [1:0] _T_0_t0b__T_15_addr_pipe_0;
  reg [31:0] _RAND_3;
  reg  _T_0_t1b [0:2]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_4;
  wire  _T_0_t1b__T_15_data; // @[FIFO.scala 23:33]
  wire [1:0] _T_0_t1b__T_15_addr; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_5;
  wire  _T_0_t1b__T_5_data; // @[FIFO.scala 23:33]
  wire [1:0] _T_0_t1b__T_5_addr; // @[FIFO.scala 23:33]
  wire  _T_0_t1b__T_5_mask; // @[FIFO.scala 23:33]
  wire  _T_0_t1b__T_5_en; // @[FIFO.scala 23:33]
  reg  _T_0_t1b__T_15_en_pipe_0;
  reg [31:0] _RAND_6;
  reg [1:0] _T_0_t1b__T_15_addr_pipe_0;
  reg [31:0] _RAND_7;
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_8;
  reg [1:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_9;
  reg [1:0] value_2; // @[Counter.scala 29:33]
  reg [31:0] _RAND_10;
  wire  _T_1 = value == 2'h2; // @[FIFO.scala 33:46]
  wire  _T_2 = value_2 == 2'h2; // @[Counter.scala 38:24]
  wire [1:0] _T_4 = value_2 + 2'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value < 2'h2; // @[FIFO.scala 38:39]
  wire [1:0] _T_9 = value + 2'h1; // @[Counter.scala 39:22]
  wire  _T_10 = value >= 2'h1; // @[FIFO.scala 42:39]
  wire  _T_16 = value_1 == 2'h2; // @[Counter.scala 38:24]
  wire [1:0] _T_18 = value_1 + 2'h1; // @[Counter.scala 39:22]
  wire  _GEN_8 = _T_10 & _T_10; // @[FIFO.scala 42:57]
  assign _T_0_t0b__T_15_addr = _T_0_t0b__T_15_addr_pipe_0;
  `ifndef RANDOMIZE_GARBAGE_ASSIGN
  assign _T_0_t0b__T_15_data = _T_0_t0b[_T_0_t0b__T_15_addr]; // @[FIFO.scala 23:33]
  `else
  assign _T_0_t0b__T_15_data = _T_0_t0b__T_15_addr >= 2'h3 ? _RAND_1[0:0] : _T_0_t0b[_T_0_t0b__T_15_addr]; // @[FIFO.scala 23:33]
  `endif // RANDOMIZE_GARBAGE_ASSIGN
  assign _T_0_t0b__T_5_data = I_0_t0b;
  assign _T_0_t0b__T_5_addr = value_2;
  assign _T_0_t0b__T_5_mask = 1'h1;
  assign _T_0_t0b__T_5_en = valid_up;
  assign _T_0_t1b__T_15_addr = _T_0_t1b__T_15_addr_pipe_0;
  `ifndef RANDOMIZE_GARBAGE_ASSIGN
  assign _T_0_t1b__T_15_data = _T_0_t1b[_T_0_t1b__T_15_addr]; // @[FIFO.scala 23:33]
  `else
  assign _T_0_t1b__T_15_data = _T_0_t1b__T_15_addr >= 2'h3 ? _RAND_5[0:0] : _T_0_t1b[_T_0_t1b__T_15_addr]; // @[FIFO.scala 23:33]
  `endif // RANDOMIZE_GARBAGE_ASSIGN
  assign _T_0_t1b__T_5_data = I_0_t1b;
  assign _T_0_t1b__T_5_addr = value_2;
  assign _T_0_t1b__T_5_mask = 1'h1;
  assign _T_0_t1b__T_5_en = valid_up;
  assign valid_down = value == 2'h2; // @[FIFO.scala 33:16]
  assign O_0_t0b = _T_0_t0b__T_15_data; // @[FIFO.scala 43:11]
  assign O_0_t1b = _T_0_t1b__T_15_data; // @[FIFO.scala 43:11]
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
  for (initvar = 0; initvar < 3; initvar = initvar+1)
    _T_0_t0b[initvar] = _RAND_0[0:0];
  `endif // RANDOMIZE_MEM_INIT
  _RAND_1 = {1{`RANDOM}};
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_0_t0b__T_15_en_pipe_0 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_0_t0b__T_15_addr_pipe_0 = _RAND_3[1:0];
  `endif // RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  `ifdef RANDOMIZE_MEM_INIT
  for (initvar = 0; initvar < 3; initvar = initvar+1)
    _T_0_t1b[initvar] = _RAND_4[0:0];
  `endif // RANDOMIZE_MEM_INIT
  _RAND_5 = {1{`RANDOM}};
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_0_t1b__T_15_en_pipe_0 = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_0_t1b__T_15_addr_pipe_0 = _RAND_7[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{`RANDOM}};
  value = _RAND_8[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_9 = {1{`RANDOM}};
  value_1 = _RAND_9[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_10 = {1{`RANDOM}};
  value_2 = _RAND_10[1:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if(_T_0_t0b__T_5_en & _T_0_t0b__T_5_mask) begin
      _T_0_t0b[_T_0_t0b__T_5_addr] <= _T_0_t0b__T_5_data; // @[FIFO.scala 23:33]
    end
    _T_0_t0b__T_15_en_pipe_0 <= valid_up & _GEN_8;
    if (valid_up & _GEN_8) begin
      _T_0_t0b__T_15_addr_pipe_0 <= value_1;
    end
    if(_T_0_t1b__T_5_en & _T_0_t1b__T_5_mask) begin
      _T_0_t1b[_T_0_t1b__T_5_addr] <= _T_0_t1b__T_5_data; // @[FIFO.scala 23:33]
    end
    _T_0_t1b__T_15_en_pipe_0 <= valid_up & _GEN_8;
    if (valid_up & _GEN_8) begin
      _T_0_t1b__T_15_addr_pipe_0 <= value_1;
    end
    if (reset) begin
      value <= 2'h0;
    end else if (valid_up) begin
      if (_T_6) begin
        if (_T_1) begin
          value <= 2'h0;
        end else begin
          value <= _T_9;
        end
      end
    end
    if (reset) begin
      value_1 <= 2'h0;
    end else if (valid_up) begin
      if (_T_10) begin
        if (_T_16) begin
          value_1 <= 2'h0;
        end else begin
          value_1 <= _T_18;
        end
      end
    end
    if (reset) begin
      value_2 <= 2'h0;
    end else if (valid_up) begin
      if (_T_2) begin
        value_2 <= 2'h0;
      end else begin
        value_2 <= _T_4;
      end
    end
  end
endmodule
module Fst(
  input   valid_up,
  output  valid_down,
  input   I_t0b,
  output  O
);
  assign valid_down = valid_up; // @[Tuple.scala 59:14]
  assign O = I_t0b; // @[Tuple.scala 58:5]
endmodule
module MapT_8(
  input   valid_up,
  output  valid_down,
  input   I_t0b,
  output  O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire  op_I_t0b; // @[MapT.scala 8:20]
  wire  op_O; // @[MapT.scala 8:20]
  Fst op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
endmodule
module MapT_9(
  input   valid_up,
  output  valid_down,
  input   I_t0b,
  output  O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire  op_I_t0b; // @[MapT.scala 8:20]
  wire  op_O; // @[MapT.scala 8:20]
  MapT_8 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
endmodule
module FIFO_2(
  input   clock,
  input   reset,
  input   valid_up,
  output  valid_down,
  input   I,
  output  O
);
  reg  _T [0:8]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_0;
  wire  _T__T_15_data; // @[FIFO.scala 23:33]
  wire [3:0] _T__T_15_addr; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_1;
  wire  _T__T_5_data; // @[FIFO.scala 23:33]
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
  wire  _T_1 = value == 4'h8; // @[FIFO.scala 33:46]
  wire  _T_2 = value_2 == 4'h8; // @[Counter.scala 38:24]
  wire [3:0] _T_4 = value_2 + 4'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value < 4'h8; // @[FIFO.scala 38:39]
  wire [3:0] _T_9 = value + 4'h1; // @[Counter.scala 39:22]
  wire  _T_10 = value >= 4'h7; // @[FIFO.scala 42:39]
  wire  _T_16 = value_1 == 4'h8; // @[Counter.scala 38:24]
  wire [3:0] _T_18 = value_1 + 4'h1; // @[Counter.scala 39:22]
  wire  _GEN_8 = _T_10 & _T_10; // @[FIFO.scala 42:57]
  assign _T__T_15_addr = _T__T_15_addr_pipe_0;
  `ifndef RANDOMIZE_GARBAGE_ASSIGN
  assign _T__T_15_data = _T[_T__T_15_addr]; // @[FIFO.scala 23:33]
  `else
  assign _T__T_15_data = _T__T_15_addr >= 4'h9 ? _RAND_1[0:0] : _T[_T__T_15_addr]; // @[FIFO.scala 23:33]
  `endif // RANDOMIZE_GARBAGE_ASSIGN
  assign _T__T_5_data = I;
  assign _T__T_5_addr = value_2;
  assign _T__T_5_mask = 1'h1;
  assign _T__T_5_en = valid_up;
  assign valid_down = value == 4'h8; // @[FIFO.scala 33:16]
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
  for (initvar = 0; initvar < 9; initvar = initvar+1)
    _T[initvar] = _RAND_0[0:0];
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
module Snd(
  input   valid_up,
  output  valid_down,
  input   I_t1b,
  output  O
);
  assign valid_down = valid_up; // @[Tuple.scala 67:14]
  assign O = I_t1b; // @[Tuple.scala 66:5]
endmodule
module MapT_10(
  input   valid_up,
  output  valid_down,
  input   I_t1b,
  output  O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire  op_I_t1b; // @[MapT.scala 8:20]
  wire  op_O; // @[MapT.scala 8:20]
  Snd op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t1b(op_I_t1b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t1b = I_t1b; // @[MapT.scala 14:10]
endmodule
module MapT_11(
  input   valid_up,
  output  valid_down,
  input   I_t1b,
  output  O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire  op_I_t1b; // @[MapT.scala 8:20]
  wire  op_O; // @[MapT.scala 8:20]
  MapT_10 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t1b(op_I_t1b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t1b = I_t1b; // @[MapT.scala 14:10]
endmodule
module DownT(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O_0,
  output [31:0] O_1,
  output [31:0] O_2
);
  assign valid_down = valid_up; // @[Downsample.scala 22:16]
  assign O_0 = I_0; // @[Downsample.scala 19:5]
  assign O_1 = I_1; // @[Downsample.scala 19:5]
  assign O_2 = I_2; // @[Downsample.scala 19:5]
endmodule
module DownS(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_1,
  output [31:0] O_0
);
  assign valid_down = valid_up; // @[Downsample.scala 13:14]
  assign O_0 = I_1; // @[Downsample.scala 12:8]
endmodule
module MapT_12(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_1,
  output [31:0] O_0
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_1; // @[MapT.scala 8:20]
  wire [31:0] op_O_0; // @[MapT.scala 8:20]
  DownS op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_1(op_I_1),
    .O_0(op_O_0)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0 = op_O_0; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_1 = I_1; // @[MapT.scala 14:10]
endmodule
module Passthrough_8(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  output [31:0] O
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O = I_0; // @[Passthrough.scala 17:68]
endmodule
module DownT_1(
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  assign valid_down = valid_up; // @[Downsample.scala 22:16]
  assign O = I; // @[Downsample.scala 19:5]
endmodule
module MapT_13(
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  DownT_1 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I(op_I),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I = I; // @[MapT.scala 14:10]
endmodule
module FIFO_4(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  reg [31:0] _T [0:2]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_0;
  wire [31:0] _T__T_15_data; // @[FIFO.scala 23:33]
  wire [1:0] _T__T_15_addr; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_1;
  wire [31:0] _T__T_5_data; // @[FIFO.scala 23:33]
  wire [1:0] _T__T_5_addr; // @[FIFO.scala 23:33]
  wire  _T__T_5_mask; // @[FIFO.scala 23:33]
  wire  _T__T_5_en; // @[FIFO.scala 23:33]
  reg  _T__T_15_en_pipe_0;
  reg [31:0] _RAND_2;
  reg [1:0] _T__T_15_addr_pipe_0;
  reg [31:0] _RAND_3;
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_4;
  reg [1:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_5;
  reg [1:0] value_2; // @[Counter.scala 29:33]
  reg [31:0] _RAND_6;
  wire  _T_1 = value == 2'h2; // @[FIFO.scala 33:46]
  wire  _T_2 = value_2 == 2'h2; // @[Counter.scala 38:24]
  wire [1:0] _T_4 = value_2 + 2'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value < 2'h2; // @[FIFO.scala 38:39]
  wire [1:0] _T_9 = value + 2'h1; // @[Counter.scala 39:22]
  wire  _T_10 = value >= 2'h1; // @[FIFO.scala 42:39]
  wire  _T_16 = value_1 == 2'h2; // @[Counter.scala 38:24]
  wire [1:0] _T_18 = value_1 + 2'h1; // @[Counter.scala 39:22]
  wire  _GEN_8 = _T_10 & _T_10; // @[FIFO.scala 42:57]
  assign _T__T_15_addr = _T__T_15_addr_pipe_0;
  `ifndef RANDOMIZE_GARBAGE_ASSIGN
  assign _T__T_15_data = _T[_T__T_15_addr]; // @[FIFO.scala 23:33]
  `else
  assign _T__T_15_data = _T__T_15_addr >= 2'h3 ? _RAND_1[31:0] : _T[_T__T_15_addr]; // @[FIFO.scala 23:33]
  `endif // RANDOMIZE_GARBAGE_ASSIGN
  assign _T__T_5_data = I;
  assign _T__T_5_addr = value_2;
  assign _T__T_5_mask = 1'h1;
  assign _T__T_5_en = valid_up;
  assign valid_down = value == 2'h2; // @[FIFO.scala 33:16]
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
  for (initvar = 0; initvar < 3; initvar = initvar+1)
    _T[initvar] = _RAND_0[31:0];
  `endif // RANDOMIZE_MEM_INIT
  _RAND_1 = {1{`RANDOM}};
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T__T_15_en_pipe_0 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T__T_15_addr_pipe_0 = _RAND_3[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  value = _RAND_4[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  value_1 = _RAND_5[1:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  value_2 = _RAND_6[1:0];
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
      value <= 2'h0;
    end else if (valid_up) begin
      if (_T_6) begin
        if (_T_1) begin
          value <= 2'h0;
        end else begin
          value <= _T_9;
        end
      end
    end
    if (reset) begin
      value_1 <= 2'h0;
    end else if (valid_up) begin
      if (_T_10) begin
        if (_T_16) begin
          value_1 <= 2'h0;
        end else begin
          value_1 <= _T_18;
        end
      end
    end
    if (reset) begin
      value_2 <= 2'h0;
    end else if (valid_up) begin
      if (_T_2) begin
        value_2 <= 2'h0;
      end else begin
        value_2 <= _T_4;
      end
    end
  end
endmodule
module InitialDelayCounter(
  input   clock,
  input   reset,
  input   valid_up,
  output  valid_down
);
  reg [1:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 2'h2; // @[InitialDelayCounter.scala 17:17]
  wire  _T_2 = _T_1 & valid_up; // @[InitialDelayCounter.scala 17:23]
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
    end else if (_T_2) begin
      value <= _T_4;
    end
  end
endmodule
module DownT_2(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  wire  InitialDelayCounter_clock; // @[Downsample.scala 25:38]
  wire  InitialDelayCounter_reset; // @[Downsample.scala 25:38]
  wire  InitialDelayCounter_valid_up; // @[Downsample.scala 25:38]
  wire  InitialDelayCounter_valid_down; // @[Downsample.scala 25:38]
  InitialDelayCounter InitialDelayCounter ( // @[Downsample.scala 25:38]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_up(InitialDelayCounter_valid_up),
    .valid_down(InitialDelayCounter_valid_down)
  );
  assign valid_down = InitialDelayCounter_valid_down; // @[Downsample.scala 27:16]
  assign O = I; // @[Downsample.scala 19:5]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign InitialDelayCounter_valid_up = valid_up; // @[Downsample.scala 26:34]
endmodule
module MapT_14(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  DownT_2 op ( // @[MapT.scala 8:20]
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
module Map2T_16(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [31:0] I1,
  output [31:0] O_t0b,
  output [31:0] O_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0; // @[Map2T.scala 8:20]
  wire [31:0] op_I1; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b; // @[Map2T.scala 8:20]
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
module Map2T_17(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [31:0] I1,
  output [31:0] O_t0b,
  output [31:0] O_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0; // @[Map2T.scala 8:20]
  wire [31:0] op_I1; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b; // @[Map2T.scala 8:20]
  Map2T_16 op ( // @[Map2T.scala 8:20]
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
module Add(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [31:0] I_t1b,
  output [31:0] O
);
  assign valid_down = valid_up; // @[Arithmetic.scala 111:14]
  assign O = I_t0b + I_t1b; // @[Arithmetic.scala 109:7]
endmodule
module MapT_15(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [31:0] I_t1b,
  output [31:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  Add op ( // @[MapT.scala 8:20]
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
module MapT_16(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [31:0] I_t1b,
  output [31:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  MapT_15 op ( // @[MapT.scala 8:20]
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
module InitialDelayCounter_1(
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
  input  [31:0] I,
  output [31:0] O_t0b,
  output [7:0]  O_t1b
);
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n167_valid_up; // @[Top.scala 68:22]
  wire  n167_valid_down; // @[Top.scala 68:22]
  wire [31:0] n167_I0; // @[Top.scala 68:22]
  wire [7:0] n167_I1; // @[Top.scala 68:22]
  wire [31:0] n167_O_t0b; // @[Top.scala 68:22]
  wire [7:0] n167_O_t1b; // @[Top.scala 68:22]
  InitialDelayCounter_1 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple_1 n167 ( // @[Top.scala 68:22]
    .valid_up(n167_valid_up),
    .valid_down(n167_valid_down),
    .I0(n167_I0),
    .I1(n167_I1),
    .O_t0b(n167_O_t0b),
    .O_t1b(n167_O_t1b)
  );
  assign valid_down = n167_valid_down; // @[Top.scala 73:16]
  assign O_t0b = n167_O_t0b; // @[Top.scala 72:7]
  assign O_t1b = n167_O_t1b; // @[Top.scala 72:7]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n167_valid_up = valid_up & InitialDelayCounter_valid_down; // @[Top.scala 71:19]
  assign n167_I0 = I; // @[Top.scala 69:13]
  assign n167_I1 = 8'h1; // @[Top.scala 70:13]
endmodule
module MapT_17(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O_t0b,
  output [7:0]  O_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I; // @[MapT.scala 8:20]
  wire [31:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_O_t1b; // @[MapT.scala 8:20]
  Module_2 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I(op_I),
    .O_t0b(op_O_t0b),
    .O_t1b(op_O_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b = op_O_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I = I; // @[MapT.scala 14:10]
endmodule
module MapT_18(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O_t0b,
  output [7:0]  O_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I; // @[MapT.scala 8:20]
  wire [31:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_O_t1b; // @[MapT.scala 8:20]
  MapT_17 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I(op_I),
    .O_t0b(op_O_t0b),
    .O_t1b(op_O_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b = op_O_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I = I; // @[MapT.scala 14:10]
endmodule
module MapT_19(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [7:0]  I_t1b,
  output [31:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
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
module MapT_20(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [7:0]  I_t1b,
  output [31:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  MapT_19 op ( // @[MapT.scala 8:20]
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
module InitialDelayCounter_2(
  input   clock,
  input   reset,
  input   valid_up,
  output  valid_down
);
  reg  value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 1'h1; // @[InitialDelayCounter.scala 17:17]
  wire  _T_2 = _T_1 & valid_up; // @[InitialDelayCounter.scala 17:23]
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
    end else if (_T_2) begin
      value <= _T_4;
    end
  end
endmodule
module DownT_3(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  wire  InitialDelayCounter_clock; // @[Downsample.scala 25:38]
  wire  InitialDelayCounter_reset; // @[Downsample.scala 25:38]
  wire  InitialDelayCounter_valid_up; // @[Downsample.scala 25:38]
  wire  InitialDelayCounter_valid_down; // @[Downsample.scala 25:38]
  InitialDelayCounter_2 InitialDelayCounter ( // @[Downsample.scala 25:38]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_up(InitialDelayCounter_valid_up),
    .valid_down(InitialDelayCounter_valid_down)
  );
  assign valid_down = InitialDelayCounter_valid_down; // @[Downsample.scala 27:16]
  assign O = I; // @[Downsample.scala 19:5]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign InitialDelayCounter_valid_up = valid_up; // @[Downsample.scala 26:34]
endmodule
module MapT_21(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  DownT_3 op ( // @[MapT.scala 8:20]
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
module DownS_1(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  output [31:0] O_0
);
  assign valid_down = valid_up; // @[Downsample.scala 13:14]
  assign O_0 = I_0; // @[Downsample.scala 12:8]
endmodule
module MapT_22(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  output [31:0] O_0
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_0; // @[MapT.scala 8:20]
  wire [31:0] op_O_0; // @[MapT.scala 8:20]
  DownS_1 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0(op_I_0),
    .O_0(op_O_0)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0 = op_O_0; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0 = I_0; // @[MapT.scala 14:10]
endmodule
module DownS_2(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_2,
  output [31:0] O_0
);
  assign valid_down = valid_up; // @[Downsample.scala 13:14]
  assign O_0 = I_2; // @[Downsample.scala 12:8]
endmodule
module MapT_24(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_2,
  output [31:0] O_0
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_2; // @[MapT.scala 8:20]
  wire [31:0] op_O_0; // @[MapT.scala 8:20]
  DownS_2 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_2(op_I_2),
    .O_0(op_O_0)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0 = op_O_0; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_2 = I_2; // @[MapT.scala 14:10]
endmodule
module InitialDelayCounter_5(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [1:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 2'h3; // @[InitialDelayCounter.scala 17:17]
  wire [1:0] _T_4 = value + 2'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 2'h3; // @[InitialDelayCounter.scala 16:16]
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
module Module_3(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O_t0b,
  output [7:0]  O_t1b
);
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n210_valid_up; // @[Top.scala 80:22]
  wire  n210_valid_down; // @[Top.scala 80:22]
  wire [31:0] n210_I0; // @[Top.scala 80:22]
  wire [7:0] n210_I1; // @[Top.scala 80:22]
  wire [31:0] n210_O_t0b; // @[Top.scala 80:22]
  wire [7:0] n210_O_t1b; // @[Top.scala 80:22]
  InitialDelayCounter_5 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple_1 n210 ( // @[Top.scala 80:22]
    .valid_up(n210_valid_up),
    .valid_down(n210_valid_down),
    .I0(n210_I0),
    .I1(n210_I1),
    .O_t0b(n210_O_t0b),
    .O_t1b(n210_O_t1b)
  );
  assign valid_down = n210_valid_down; // @[Top.scala 85:16]
  assign O_t0b = n210_O_t0b; // @[Top.scala 84:7]
  assign O_t1b = n210_O_t1b; // @[Top.scala 84:7]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n210_valid_up = valid_up & InitialDelayCounter_valid_down; // @[Top.scala 83:19]
  assign n210_I0 = I; // @[Top.scala 81:13]
  assign n210_I1 = 8'h1; // @[Top.scala 82:13]
endmodule
module MapT_28(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O_t0b,
  output [7:0]  O_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I; // @[MapT.scala 8:20]
  wire [31:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_O_t1b; // @[MapT.scala 8:20]
  Module_3 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I(op_I),
    .O_t0b(op_O_t0b),
    .O_t1b(op_O_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b = op_O_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I = I; // @[MapT.scala 14:10]
endmodule
module MapT_29(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O_t0b,
  output [7:0]  O_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I; // @[MapT.scala 8:20]
  wire [31:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_O_t1b; // @[MapT.scala 8:20]
  MapT_28 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I(op_I),
    .O_t0b(op_O_t0b),
    .O_t1b(op_O_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b = op_O_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I = I; // @[MapT.scala 14:10]
endmodule
module FIFO_5(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [31:0] I_t1b,
  output [31:0] O_t0b,
  output [31:0] O_t1b
);
  reg [31:0] _T_t0b; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_0;
  reg [31:0] _T_t1b; // @[FIFO.scala 13:26]
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
  _T_t0b = _RAND_0[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_t1b = _RAND_1[31:0];
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
module AtomTuple_10(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [31:0] I1_t0b,
  input  [31:0] I1_t1b,
  output [31:0] O_t0b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b = I0; // @[Tuple.scala 49:9]
  assign O_t1b_t0b = I1_t0b; // @[Tuple.scala 50:9]
  assign O_t1b_t1b = I1_t1b; // @[Tuple.scala 50:9]
endmodule
module Map2T_22(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [31:0] I1_t0b,
  input  [31:0] I1_t1b,
  output [31:0] O_t0b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t1b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t1b; // @[Map2T.scala 8:20]
  AtomTuple_10 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0(op_I0),
    .I1_t0b(op_I1_t0b),
    .I1_t1b(op_I1_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_t0b = op_O_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0 = I0; // @[Map2T.scala 15:11]
  assign op_I1_t0b = I1_t0b; // @[Map2T.scala 16:11]
  assign op_I1_t1b = I1_t1b; // @[Map2T.scala 16:11]
endmodule
module Map2T_23(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [31:0] I1_t0b,
  input  [31:0] I1_t1b,
  output [31:0] O_t0b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t1b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t1b; // @[Map2T.scala 8:20]
  Map2T_22 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0(op_I0),
    .I1_t0b(op_I1_t0b),
    .I1_t1b(op_I1_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_t0b = op_O_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0 = I0; // @[Map2T.scala 15:11]
  assign op_I1_t0b = I1_t0b; // @[Map2T.scala 16:11]
  assign op_I1_t1b = I1_t1b; // @[Map2T.scala 16:11]
endmodule
module FIFO_6(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [31:0] I_t1b_t0b,
  input  [31:0] I_t1b_t1b,
  output [31:0] O_t0b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b
);
  reg [31:0] _T_t0b [0:6]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_0;
  wire [31:0] _T_t0b__T_15_data; // @[FIFO.scala 23:33]
  wire [2:0] _T_t0b__T_15_addr; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_1;
  wire [31:0] _T_t0b__T_5_data; // @[FIFO.scala 23:33]
  wire [2:0] _T_t0b__T_5_addr; // @[FIFO.scala 23:33]
  wire  _T_t0b__T_5_mask; // @[FIFO.scala 23:33]
  wire  _T_t0b__T_5_en; // @[FIFO.scala 23:33]
  reg  _T_t0b__T_15_en_pipe_0;
  reg [31:0] _RAND_2;
  reg [2:0] _T_t0b__T_15_addr_pipe_0;
  reg [31:0] _RAND_3;
  reg [31:0] _T_t1b_t0b [0:6]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_4;
  wire [31:0] _T_t1b_t0b__T_15_data; // @[FIFO.scala 23:33]
  wire [2:0] _T_t1b_t0b__T_15_addr; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_5;
  wire [31:0] _T_t1b_t0b__T_5_data; // @[FIFO.scala 23:33]
  wire [2:0] _T_t1b_t0b__T_5_addr; // @[FIFO.scala 23:33]
  wire  _T_t1b_t0b__T_5_mask; // @[FIFO.scala 23:33]
  wire  _T_t1b_t0b__T_5_en; // @[FIFO.scala 23:33]
  reg  _T_t1b_t0b__T_15_en_pipe_0;
  reg [31:0] _RAND_6;
  reg [2:0] _T_t1b_t0b__T_15_addr_pipe_0;
  reg [31:0] _RAND_7;
  reg [31:0] _T_t1b_t1b [0:6]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_8;
  wire [31:0] _T_t1b_t1b__T_15_data; // @[FIFO.scala 23:33]
  wire [2:0] _T_t1b_t1b__T_15_addr; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_9;
  wire [31:0] _T_t1b_t1b__T_5_data; // @[FIFO.scala 23:33]
  wire [2:0] _T_t1b_t1b__T_5_addr; // @[FIFO.scala 23:33]
  wire  _T_t1b_t1b__T_5_mask; // @[FIFO.scala 23:33]
  wire  _T_t1b_t1b__T_5_en; // @[FIFO.scala 23:33]
  reg  _T_t1b_t1b__T_15_en_pipe_0;
  reg [31:0] _RAND_10;
  reg [2:0] _T_t1b_t1b__T_15_addr_pipe_0;
  reg [31:0] _RAND_11;
  reg [2:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_12;
  reg [2:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_13;
  reg [2:0] value_2; // @[Counter.scala 29:33]
  reg [31:0] _RAND_14;
  wire  _T_1 = value == 3'h6; // @[FIFO.scala 33:46]
  wire  _T_2 = value_2 == 3'h6; // @[Counter.scala 38:24]
  wire [2:0] _T_4 = value_2 + 3'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value < 3'h6; // @[FIFO.scala 38:39]
  wire [2:0] _T_9 = value + 3'h1; // @[Counter.scala 39:22]
  wire  _T_10 = value >= 3'h5; // @[FIFO.scala 42:39]
  wire  _T_16 = value_1 == 3'h6; // @[Counter.scala 38:24]
  wire [2:0] _T_18 = value_1 + 3'h1; // @[Counter.scala 39:22]
  wire  _GEN_8 = _T_10 & _T_10; // @[FIFO.scala 42:57]
  assign _T_t0b__T_15_addr = _T_t0b__T_15_addr_pipe_0;
  `ifndef RANDOMIZE_GARBAGE_ASSIGN
  assign _T_t0b__T_15_data = _T_t0b[_T_t0b__T_15_addr]; // @[FIFO.scala 23:33]
  `else
  assign _T_t0b__T_15_data = _T_t0b__T_15_addr >= 3'h7 ? _RAND_1[31:0] : _T_t0b[_T_t0b__T_15_addr]; // @[FIFO.scala 23:33]
  `endif // RANDOMIZE_GARBAGE_ASSIGN
  assign _T_t0b__T_5_data = I_t0b;
  assign _T_t0b__T_5_addr = value_2;
  assign _T_t0b__T_5_mask = 1'h1;
  assign _T_t0b__T_5_en = valid_up;
  assign _T_t1b_t0b__T_15_addr = _T_t1b_t0b__T_15_addr_pipe_0;
  `ifndef RANDOMIZE_GARBAGE_ASSIGN
  assign _T_t1b_t0b__T_15_data = _T_t1b_t0b[_T_t1b_t0b__T_15_addr]; // @[FIFO.scala 23:33]
  `else
  assign _T_t1b_t0b__T_15_data = _T_t1b_t0b__T_15_addr >= 3'h7 ? _RAND_5[31:0] : _T_t1b_t0b[_T_t1b_t0b__T_15_addr]; // @[FIFO.scala 23:33]
  `endif // RANDOMIZE_GARBAGE_ASSIGN
  assign _T_t1b_t0b__T_5_data = I_t1b_t0b;
  assign _T_t1b_t0b__T_5_addr = value_2;
  assign _T_t1b_t0b__T_5_mask = 1'h1;
  assign _T_t1b_t0b__T_5_en = valid_up;
  assign _T_t1b_t1b__T_15_addr = _T_t1b_t1b__T_15_addr_pipe_0;
  `ifndef RANDOMIZE_GARBAGE_ASSIGN
  assign _T_t1b_t1b__T_15_data = _T_t1b_t1b[_T_t1b_t1b__T_15_addr]; // @[FIFO.scala 23:33]
  `else
  assign _T_t1b_t1b__T_15_data = _T_t1b_t1b__T_15_addr >= 3'h7 ? _RAND_9[31:0] : _T_t1b_t1b[_T_t1b_t1b__T_15_addr]; // @[FIFO.scala 23:33]
  `endif // RANDOMIZE_GARBAGE_ASSIGN
  assign _T_t1b_t1b__T_5_data = I_t1b_t1b;
  assign _T_t1b_t1b__T_5_addr = value_2;
  assign _T_t1b_t1b__T_5_mask = 1'h1;
  assign _T_t1b_t1b__T_5_en = valid_up;
  assign valid_down = value == 3'h6; // @[FIFO.scala 33:16]
  assign O_t0b = _T_t0b__T_15_data; // @[FIFO.scala 43:11]
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
  for (initvar = 0; initvar < 7; initvar = initvar+1)
    _T_t0b[initvar] = _RAND_0[31:0];
  `endif // RANDOMIZE_MEM_INIT
  _RAND_1 = {1{`RANDOM}};
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_t0b__T_15_en_pipe_0 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_t0b__T_15_addr_pipe_0 = _RAND_3[2:0];
  `endif // RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  `ifdef RANDOMIZE_MEM_INIT
  for (initvar = 0; initvar < 7; initvar = initvar+1)
    _T_t1b_t0b[initvar] = _RAND_4[31:0];
  `endif // RANDOMIZE_MEM_INIT
  _RAND_5 = {1{`RANDOM}};
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_t1b_t0b__T_15_en_pipe_0 = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_t1b_t0b__T_15_addr_pipe_0 = _RAND_7[2:0];
  `endif // RANDOMIZE_REG_INIT
  _RAND_8 = {1{`RANDOM}};
  `ifdef RANDOMIZE_MEM_INIT
  for (initvar = 0; initvar < 7; initvar = initvar+1)
    _T_t1b_t1b[initvar] = _RAND_8[31:0];
  `endif // RANDOMIZE_MEM_INIT
  _RAND_9 = {1{`RANDOM}};
  `ifdef RANDOMIZE_REG_INIT
  _RAND_10 = {1{`RANDOM}};
  _T_t1b_t1b__T_15_en_pipe_0 = _RAND_10[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_11 = {1{`RANDOM}};
  _T_t1b_t1b__T_15_addr_pipe_0 = _RAND_11[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_12 = {1{`RANDOM}};
  value = _RAND_12[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_13 = {1{`RANDOM}};
  value_1 = _RAND_13[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_14 = {1{`RANDOM}};
  value_2 = _RAND_14[2:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if(_T_t0b__T_5_en & _T_t0b__T_5_mask) begin
      _T_t0b[_T_t0b__T_5_addr] <= _T_t0b__T_5_data; // @[FIFO.scala 23:33]
    end
    _T_t0b__T_15_en_pipe_0 <= valid_up & _GEN_8;
    if (valid_up & _GEN_8) begin
      _T_t0b__T_15_addr_pipe_0 <= value_1;
    end
    if(_T_t1b_t0b__T_5_en & _T_t1b_t0b__T_5_mask) begin
      _T_t1b_t0b[_T_t1b_t0b__T_5_addr] <= _T_t1b_t0b__T_5_data; // @[FIFO.scala 23:33]
    end
    _T_t1b_t0b__T_15_en_pipe_0 <= valid_up & _GEN_8;
    if (valid_up & _GEN_8) begin
      _T_t1b_t0b__T_15_addr_pipe_0 <= value_1;
    end
    if(_T_t1b_t1b__T_5_en & _T_t1b_t1b__T_5_mask) begin
      _T_t1b_t1b[_T_t1b_t1b__T_5_addr] <= _T_t1b_t1b__T_5_data; // @[FIFO.scala 23:33]
    end
    _T_t1b_t1b__T_15_en_pipe_0 <= valid_up & _GEN_8;
    if (valid_up & _GEN_8) begin
      _T_t1b_t1b__T_15_addr_pipe_0 <= value_1;
    end
    if (reset) begin
      value <= 3'h0;
    end else if (valid_up) begin
      if (_T_6) begin
        if (_T_1) begin
          value <= 3'h0;
        end else begin
          value <= _T_9;
        end
      end
    end
    if (reset) begin
      value_1 <= 3'h0;
    end else if (valid_up) begin
      if (_T_10) begin
        if (_T_16) begin
          value_1 <= 3'h0;
        end else begin
          value_1 <= _T_18;
        end
      end
    end
    if (reset) begin
      value_2 <= 3'h0;
    end else if (valid_up) begin
      if (_T_2) begin
        value_2 <= 3'h0;
      end else begin
        value_2 <= _T_4;
      end
    end
  end
endmodule
module FIFO_7(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  reg [31:0] _T [0:7]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_0;
  wire [31:0] _T__T_15_data; // @[FIFO.scala 23:33]
  wire [2:0] _T__T_15_addr; // @[FIFO.scala 23:33]
  wire [31:0] _T__T_5_data; // @[FIFO.scala 23:33]
  wire [2:0] _T__T_5_addr; // @[FIFO.scala 23:33]
  wire  _T__T_5_mask; // @[FIFO.scala 23:33]
  wire  _T__T_5_en; // @[FIFO.scala 23:33]
  reg  _T__T_15_en_pipe_0;
  reg [31:0] _RAND_1;
  reg [2:0] _T__T_15_addr_pipe_0;
  reg [31:0] _RAND_2;
  reg [2:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_3;
  reg [2:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_4;
  reg [2:0] value_2; // @[Counter.scala 29:33]
  reg [31:0] _RAND_5;
  wire [2:0] _T_4 = value_2 + 3'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value < 3'h7; // @[FIFO.scala 38:39]
  wire [2:0] _T_9 = value + 3'h1; // @[Counter.scala 39:22]
  wire  _T_10 = value >= 3'h6; // @[FIFO.scala 42:39]
  wire [2:0] _T_18 = value_1 + 3'h1; // @[Counter.scala 39:22]
  wire  _GEN_5 = _T_10 & _T_10; // @[FIFO.scala 42:57]
  assign _T__T_15_addr = _T__T_15_addr_pipe_0;
  assign _T__T_15_data = _T[_T__T_15_addr]; // @[FIFO.scala 23:33]
  assign _T__T_5_data = I;
  assign _T__T_5_addr = value_2;
  assign _T__T_5_mask = 1'h1;
  assign _T__T_5_en = valid_up;
  assign valid_down = value == 3'h7; // @[FIFO.scala 33:16]
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
  for (initvar = 0; initvar < 8; initvar = initvar+1)
    _T[initvar] = _RAND_0[31:0];
  `endif // RANDOMIZE_MEM_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T__T_15_en_pipe_0 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T__T_15_addr_pipe_0 = _RAND_2[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  value = _RAND_3[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  value_1 = _RAND_4[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  value_2 = _RAND_5[2:0];
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
      value <= 3'h0;
    end else if (valid_up) begin
      if (_T_6) begin
        value <= _T_9;
      end
    end
    if (reset) begin
      value_1 <= 3'h0;
    end else if (valid_up) begin
      if (_T_10) begin
        value_1 <= _T_18;
      end
    end
    if (reset) begin
      value_2 <= 3'h0;
    end else if (valid_up) begin
      value_2 <= _T_4;
    end
  end
endmodule
module FIFO_9(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  output [31:0] O_0,
  output [31:0] O_1
);
  reg [31:0] _T__0; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_0;
  reg [31:0] _T__1; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_1;
  reg  _T_1; // @[FIFO.scala 15:27]
  reg [31:0] _RAND_2;
  assign valid_down = _T_1; // @[FIFO.scala 16:16]
  assign O_0 = _T__0; // @[FIFO.scala 14:7]
  assign O_1 = _T__1; // @[FIFO.scala 14:7]
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
  _T_1 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    _T__0 <= I_0;
    _T__1 <= I_1;
    if (reset) begin
      _T_1 <= 1'h0;
    end else begin
      _T_1 <= valid_up;
    end
  end
endmodule
module SSeqTupleAppender_5(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_0,
  input  [31:0] I0_1,
  input  [31:0] I0_2,
  input  [31:0] I1,
  output [31:0] O_0,
  output [31:0] O_1,
  output [31:0] O_2,
  output [31:0] O_3
);
  assign valid_down = valid_up; // @[Tuple.scala 28:14]
  assign O_0 = I0_0; // @[Tuple.scala 24:34]
  assign O_1 = I0_1; // @[Tuple.scala 24:34]
  assign O_2 = I0_2; // @[Tuple.scala 24:34]
  assign O_3 = I1; // @[Tuple.scala 26:32]
endmodule
module Map2T_28(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_0,
  input  [31:0] I0_1,
  input  [31:0] I0_2,
  input  [31:0] I1,
  output [31:0] O_0,
  output [31:0] O_1,
  output [31:0] O_2,
  output [31:0] O_3
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_1; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_2; // @[Map2T.scala 8:20]
  wire [31:0] op_I1; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0; // @[Map2T.scala 8:20]
  wire [31:0] op_O_1; // @[Map2T.scala 8:20]
  wire [31:0] op_O_2; // @[Map2T.scala 8:20]
  wire [31:0] op_O_3; // @[Map2T.scala 8:20]
  SSeqTupleAppender_5 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0(op_I0_0),
    .I0_1(op_I0_1),
    .I0_2(op_I0_2),
    .I1(op_I1),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2),
    .O_3(op_O_3)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0 = op_O_0; // @[Map2T.scala 17:7]
  assign O_1 = op_O_1; // @[Map2T.scala 17:7]
  assign O_2 = op_O_2; // @[Map2T.scala 17:7]
  assign O_3 = op_O_3; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0 = I0_0; // @[Map2T.scala 15:11]
  assign op_I0_1 = I0_1; // @[Map2T.scala 15:11]
  assign op_I0_2 = I0_2; // @[Map2T.scala 15:11]
  assign op_I1 = I1; // @[Map2T.scala 16:11]
endmodule
module Map2T_29(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_0,
  input  [31:0] I0_1,
  input  [31:0] I0_2,
  input  [31:0] I1,
  output [31:0] O_0,
  output [31:0] O_1,
  output [31:0] O_2,
  output [31:0] O_3
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_1; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_2; // @[Map2T.scala 8:20]
  wire [31:0] op_I1; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0; // @[Map2T.scala 8:20]
  wire [31:0] op_O_1; // @[Map2T.scala 8:20]
  wire [31:0] op_O_2; // @[Map2T.scala 8:20]
  wire [31:0] op_O_3; // @[Map2T.scala 8:20]
  Map2T_28 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0(op_I0_0),
    .I0_1(op_I0_1),
    .I0_2(op_I0_2),
    .I1(op_I1),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2),
    .O_3(op_O_3)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0 = op_O_0; // @[Map2T.scala 17:7]
  assign O_1 = op_O_1; // @[Map2T.scala 17:7]
  assign O_2 = op_O_2; // @[Map2T.scala 17:7]
  assign O_3 = op_O_3; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0 = I0_0; // @[Map2T.scala 15:11]
  assign op_I0_1 = I0_1; // @[Map2T.scala 15:11]
  assign op_I0_2 = I0_2; // @[Map2T.scala 15:11]
  assign op_I1 = I1; // @[Map2T.scala 16:11]
endmodule
module Serialize_3(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  input  [31:0] I_3,
  output [31:0] O
);
  wire  elem_counter_CE; // @[Serialize.scala 14:28]
  wire  elem_counter_valid; // @[Serialize.scala 14:28]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire [1:0] _T_4 = value + 2'h1; // @[Counter.scala 39:22]
  reg [31:0] mux_input_wire_1; // @[Serialize.scala 39:47]
  reg [31:0] _RAND_1;
  reg [31:0] mux_input_wire_2; // @[Serialize.scala 39:47]
  reg [31:0] _RAND_2;
  reg [31:0] mux_input_wire_3; // @[Serialize.scala 39:47]
  reg [31:0] _RAND_3;
  wire  _T_8 = value == 2'h0; // @[Serialize.scala 42:34]
  wire  _T_9 = _T_8 & valid_up; // @[Serialize.scala 42:42]
  wire  _T_14 = 2'h3 == value; // @[Mux.scala 68:19]
  wire  _T_16 = 2'h2 == value; // @[Mux.scala 68:19]
  wire  _T_18 = 2'h1 == value; // @[Mux.scala 68:19]
  wire  _T_20 = 2'h0 == value; // @[Mux.scala 68:19]
  reg [31:0] _T_22; // @[Serialize.scala 53:15]
  reg [31:0] _RAND_4;
  reg  _T_23; // @[Serialize.scala 54:24]
  reg [31:0] _RAND_5;
  NestedCountersWithNumValid elem_counter ( // @[Serialize.scala 14:28]
    .CE(elem_counter_CE),
    .valid(elem_counter_valid)
  );
  assign valid_down = _T_23; // @[Serialize.scala 54:14]
  assign O = _T_22; // @[Serialize.scala 53:5]
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
  mux_input_wire_1 = _RAND_1[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  mux_input_wire_2 = _RAND_2[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  mux_input_wire_3 = _RAND_3[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_22 = _RAND_4[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_23 = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 2'h0;
    end else if (valid_up) begin
      value <= _T_4;
    end
    if (_T_9) begin
      mux_input_wire_1 <= I_1;
    end
    if (_T_9) begin
      mux_input_wire_2 <= I_2;
    end
    if (_T_9) begin
      mux_input_wire_3 <= I_3;
    end
    if (_T_20) begin
      _T_22 <= I_0;
    end else if (_T_18) begin
      _T_22 <= mux_input_wire_1;
    end else if (_T_16) begin
      _T_22 <= mux_input_wire_2;
    end else if (_T_14) begin
      _T_22 <= mux_input_wire_3;
    end else begin
      _T_22 <= I_0;
    end
    if (reset) begin
      _T_23 <= 1'h0;
    end else begin
      _T_23 <= valid_up;
    end
  end
endmodule
module MapT_32(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  input  [31:0] I_3,
  output [31:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_0; // @[MapT.scala 8:20]
  wire [31:0] op_I_1; // @[MapT.scala 8:20]
  wire [31:0] op_I_2; // @[MapT.scala 8:20]
  wire [31:0] op_I_3; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  Serialize_3 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0(op_I_0),
    .I_1(op_I_1),
    .I_2(op_I_2),
    .I_3(op_I_3),
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
  assign op_I_3 = I_3; // @[MapT.scala 14:10]
endmodule
module AddNoValid(
  input  [31:0] I_t0b,
  input  [31:0] I_t1b,
  output [31:0] O
);
  assign O = I_t0b + I_t1b; // @[Arithmetic.scala 125:7]
endmodule
module ReduceT(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  wire  NestedCountersWithNumValid_CE; // @[ReduceT.scala 22:34]
  wire  NestedCountersWithNumValid_valid; // @[ReduceT.scala 22:34]
  wire [31:0] AddNoValid_I_t0b; // @[ReduceT.scala 25:25]
  wire [31:0] AddNoValid_I_t1b; // @[ReduceT.scala 25:25]
  wire [31:0] AddNoValid_O; // @[ReduceT.scala 25:25]
  reg  _T; // @[ReduceT.scala 26:50]
  reg [31:0] _RAND_0;
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire  _T_3 = value == 2'h3; // @[Counter.scala 38:24]
  wire [1:0] _T_5 = value + 2'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value == 2'h0; // @[ReduceT.scala 34:60]
  reg [31:0] _T_7; // @[ReduceT.scala 34:76]
  reg [31:0] _RAND_2;
  reg [31:0] _T_9; // @[ReduceT.scala 35:24]
  reg [31:0] _RAND_3;
  reg  _T_10; // @[ReduceT.scala 37:35]
  reg [31:0] _RAND_4;
  reg [31:0] _T_11; // @[ReduceT.scala 44:83]
  reg [31:0] _RAND_5;
  reg  _T_12; // @[ReduceT.scala 51:28]
  reg [31:0] _RAND_6;
  wire  _T_14 = _T_12 | _T_3; // @[ReduceT.scala 52:28]
  reg [31:0] _T_15; // @[ReduceT.scala 56:15]
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
  _T_7 = _RAND_2[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_9 = _RAND_3[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_10 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_11 = _RAND_5[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_12 = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_15 = _RAND_7[31:0];
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
      value <= _T_5;
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
module MapT_33(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  ReduceT op ( // @[MapT.scala 8:20]
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
module InitialDelayCounter_6(
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
  input  [31:0] I,
  output [31:0] O_t0b,
  output [7:0]  O_t1b
);
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n269_valid_up; // @[Top.scala 92:22]
  wire  n269_valid_down; // @[Top.scala 92:22]
  wire [31:0] n269_I0; // @[Top.scala 92:22]
  wire [7:0] n269_I1; // @[Top.scala 92:22]
  wire [31:0] n269_O_t0b; // @[Top.scala 92:22]
  wire [7:0] n269_O_t1b; // @[Top.scala 92:22]
  InitialDelayCounter_6 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple_1 n269 ( // @[Top.scala 92:22]
    .valid_up(n269_valid_up),
    .valid_down(n269_valid_down),
    .I0(n269_I0),
    .I1(n269_I1),
    .O_t0b(n269_O_t0b),
    .O_t1b(n269_O_t1b)
  );
  assign valid_down = n269_valid_down; // @[Top.scala 97:16]
  assign O_t0b = n269_O_t0b; // @[Top.scala 96:7]
  assign O_t1b = n269_O_t1b; // @[Top.scala 96:7]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n269_valid_up = valid_up & InitialDelayCounter_valid_down; // @[Top.scala 95:19]
  assign n269_I0 = I; // @[Top.scala 93:13]
  assign n269_I1 = 8'h2; // @[Top.scala 94:13]
endmodule
module MapT_34(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O_t0b,
  output [7:0]  O_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I; // @[MapT.scala 8:20]
  wire [31:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_O_t1b; // @[MapT.scala 8:20]
  Module_4 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I(op_I),
    .O_t0b(op_O_t0b),
    .O_t1b(op_O_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b = op_O_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I = I; // @[MapT.scala 14:10]
endmodule
module MapT_35(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O_t0b,
  output [7:0]  O_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I; // @[MapT.scala 8:20]
  wire [31:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_O_t1b; // @[MapT.scala 8:20]
  MapT_34 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I(op_I),
    .O_t0b(op_O_t0b),
    .O_t1b(op_O_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b = op_O_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I = I; // @[MapT.scala 14:10]
endmodule
module AtomTuple_15(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_t0b,
  input  [31:0] I0_t1b_t0b,
  input  [31:0] I0_t1b_t1b,
  input  [31:0] I1_t0b,
  input  [31:0] I1_t1b_t0b,
  input  [31:0] I1_t1b_t1b,
  output [31:0] O_t0b_t0b,
  output [31:0] O_t0b_t1b_t0b,
  output [31:0] O_t0b_t1b_t1b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b_t0b,
  output [31:0] O_t1b_t1b_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b_t0b = I0_t0b; // @[Tuple.scala 49:9]
  assign O_t0b_t1b_t0b = I0_t1b_t0b; // @[Tuple.scala 49:9]
  assign O_t0b_t1b_t1b = I0_t1b_t1b; // @[Tuple.scala 49:9]
  assign O_t1b_t0b = I1_t0b; // @[Tuple.scala 50:9]
  assign O_t1b_t1b_t0b = I1_t1b_t0b; // @[Tuple.scala 50:9]
  assign O_t1b_t1b_t1b = I1_t1b_t1b; // @[Tuple.scala 50:9]
endmodule
module Map2T_40(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_t0b,
  input  [31:0] I0_t1b_t0b,
  input  [31:0] I0_t1b_t1b,
  input  [31:0] I1_t0b,
  input  [31:0] I1_t1b_t0b,
  input  [31:0] I1_t1b_t1b,
  output [31:0] O_t0b_t0b,
  output [31:0] O_t0b_t1b_t0b,
  output [31:0] O_t0b_t1b_t1b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b_t0b,
  output [31:0] O_t1b_t1b_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_t1b_t1b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t1b_t1b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t0b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t0b_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t0b_t1b_t1b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t1b_t1b; // @[Map2T.scala 8:20]
  AtomTuple_15 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_t0b(op_I0_t0b),
    .I0_t1b_t0b(op_I0_t1b_t0b),
    .I0_t1b_t1b(op_I0_t1b_t1b),
    .I1_t0b(op_I1_t0b),
    .I1_t1b_t0b(op_I1_t1b_t0b),
    .I1_t1b_t1b(op_I1_t1b_t1b),
    .O_t0b_t0b(op_O_t0b_t0b),
    .O_t0b_t1b_t0b(op_O_t0b_t1b_t0b),
    .O_t0b_t1b_t1b(op_O_t0b_t1b_t1b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b_t0b(op_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(op_O_t1b_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_t0b_t0b = op_O_t0b_t0b; // @[Map2T.scala 17:7]
  assign O_t0b_t1b_t0b = op_O_t0b_t1b_t0b; // @[Map2T.scala 17:7]
  assign O_t0b_t1b_t1b = op_O_t0b_t1b_t1b; // @[Map2T.scala 17:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t1b_t0b = op_O_t1b_t1b_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t1b_t1b = op_O_t1b_t1b_t1b; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_t0b = I0_t0b; // @[Map2T.scala 15:11]
  assign op_I0_t1b_t0b = I0_t1b_t0b; // @[Map2T.scala 15:11]
  assign op_I0_t1b_t1b = I0_t1b_t1b; // @[Map2T.scala 15:11]
  assign op_I1_t0b = I1_t0b; // @[Map2T.scala 16:11]
  assign op_I1_t1b_t0b = I1_t1b_t0b; // @[Map2T.scala 16:11]
  assign op_I1_t1b_t1b = I1_t1b_t1b; // @[Map2T.scala 16:11]
endmodule
module Map2T_41(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_t0b,
  input  [31:0] I0_t1b_t0b,
  input  [31:0] I0_t1b_t1b,
  input  [31:0] I1_t0b,
  input  [31:0] I1_t1b_t0b,
  input  [31:0] I1_t1b_t1b,
  output [31:0] O_t0b_t0b,
  output [31:0] O_t0b_t1b_t0b,
  output [31:0] O_t0b_t1b_t1b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b_t0b,
  output [31:0] O_t1b_t1b_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_t1b_t1b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t1b_t1b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t0b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t0b_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t0b_t1b_t1b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t1b_t1b; // @[Map2T.scala 8:20]
  Map2T_40 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_t0b(op_I0_t0b),
    .I0_t1b_t0b(op_I0_t1b_t0b),
    .I0_t1b_t1b(op_I0_t1b_t1b),
    .I1_t0b(op_I1_t0b),
    .I1_t1b_t0b(op_I1_t1b_t0b),
    .I1_t1b_t1b(op_I1_t1b_t1b),
    .O_t0b_t0b(op_O_t0b_t0b),
    .O_t0b_t1b_t0b(op_O_t0b_t1b_t0b),
    .O_t0b_t1b_t1b(op_O_t0b_t1b_t1b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b_t0b(op_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(op_O_t1b_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_t0b_t0b = op_O_t0b_t0b; // @[Map2T.scala 17:7]
  assign O_t0b_t1b_t0b = op_O_t0b_t1b_t0b; // @[Map2T.scala 17:7]
  assign O_t0b_t1b_t1b = op_O_t0b_t1b_t1b; // @[Map2T.scala 17:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t1b_t0b = op_O_t1b_t1b_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t1b_t1b = op_O_t1b_t1b_t1b; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_t0b = I0_t0b; // @[Map2T.scala 15:11]
  assign op_I0_t1b_t0b = I0_t1b_t0b; // @[Map2T.scala 15:11]
  assign op_I0_t1b_t1b = I0_t1b_t1b; // @[Map2T.scala 15:11]
  assign op_I1_t0b = I1_t0b; // @[Map2T.scala 16:11]
  assign op_I1_t1b_t0b = I1_t1b_t0b; // @[Map2T.scala 16:11]
  assign op_I1_t1b_t1b = I1_t1b_t1b; // @[Map2T.scala 16:11]
endmodule
module AtomTuple_16(
  input         valid_up,
  output        valid_down,
  input         I0,
  input  [31:0] I1_t0b_t0b,
  input  [31:0] I1_t0b_t1b_t0b,
  input  [31:0] I1_t0b_t1b_t1b,
  input  [31:0] I1_t1b_t0b,
  input  [31:0] I1_t1b_t1b_t0b,
  input  [31:0] I1_t1b_t1b_t1b,
  output        O_t0b,
  output [31:0] O_t1b_t0b_t0b,
  output [31:0] O_t1b_t0b_t1b_t0b,
  output [31:0] O_t1b_t0b_t1b_t1b,
  output [31:0] O_t1b_t1b_t0b,
  output [31:0] O_t1b_t1b_t1b_t0b,
  output [31:0] O_t1b_t1b_t1b_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b = I0; // @[Tuple.scala 49:9]
  assign O_t1b_t0b_t0b = I1_t0b_t0b; // @[Tuple.scala 50:9]
  assign O_t1b_t0b_t1b_t0b = I1_t0b_t1b_t0b; // @[Tuple.scala 50:9]
  assign O_t1b_t0b_t1b_t1b = I1_t0b_t1b_t1b; // @[Tuple.scala 50:9]
  assign O_t1b_t1b_t0b = I1_t1b_t0b; // @[Tuple.scala 50:9]
  assign O_t1b_t1b_t1b_t0b = I1_t1b_t1b_t0b; // @[Tuple.scala 50:9]
  assign O_t1b_t1b_t1b_t1b = I1_t1b_t1b_t1b; // @[Tuple.scala 50:9]
endmodule
module Map2T_42(
  input         valid_up,
  output        valid_down,
  input         I0,
  input  [31:0] I1_t0b_t0b,
  input  [31:0] I1_t0b_t1b_t0b,
  input  [31:0] I1_t0b_t1b_t1b,
  input  [31:0] I1_t1b_t0b,
  input  [31:0] I1_t1b_t1b_t0b,
  input  [31:0] I1_t1b_t1b_t1b,
  output        O_t0b,
  output [31:0] O_t1b_t0b_t0b,
  output [31:0] O_t1b_t0b_t1b_t0b,
  output [31:0] O_t1b_t0b_t1b_t1b,
  output [31:0] O_t1b_t1b_t0b,
  output [31:0] O_t1b_t1b_t1b_t0b,
  output [31:0] O_t1b_t1b_t1b_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire  op_I0; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t0b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t0b_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t0b_t1b_t1b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t1b_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t1b_t1b_t1b; // @[Map2T.scala 8:20]
  wire  op_O_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t0b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t0b_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t0b_t1b_t1b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t1b_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t1b_t1b_t1b; // @[Map2T.scala 8:20]
  AtomTuple_16 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0(op_I0),
    .I1_t0b_t0b(op_I1_t0b_t0b),
    .I1_t0b_t1b_t0b(op_I1_t0b_t1b_t0b),
    .I1_t0b_t1b_t1b(op_I1_t0b_t1b_t1b),
    .I1_t1b_t0b(op_I1_t1b_t0b),
    .I1_t1b_t1b_t0b(op_I1_t1b_t1b_t0b),
    .I1_t1b_t1b_t1b(op_I1_t1b_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b_t0b(op_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b_t0b(op_O_t1b_t0b_t1b_t0b),
    .O_t1b_t0b_t1b_t1b(op_O_t1b_t0b_t1b_t1b),
    .O_t1b_t1b_t0b(op_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b_t0b(op_O_t1b_t1b_t1b_t0b),
    .O_t1b_t1b_t1b_t1b(op_O_t1b_t1b_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_t0b = op_O_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t0b_t0b = op_O_t1b_t0b_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t0b_t1b_t0b = op_O_t1b_t0b_t1b_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t0b_t1b_t1b = op_O_t1b_t0b_t1b_t1b; // @[Map2T.scala 17:7]
  assign O_t1b_t1b_t0b = op_O_t1b_t1b_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t1b_t1b_t0b = op_O_t1b_t1b_t1b_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t1b_t1b_t1b = op_O_t1b_t1b_t1b_t1b; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0 = I0; // @[Map2T.scala 15:11]
  assign op_I1_t0b_t0b = I1_t0b_t0b; // @[Map2T.scala 16:11]
  assign op_I1_t0b_t1b_t0b = I1_t0b_t1b_t0b; // @[Map2T.scala 16:11]
  assign op_I1_t0b_t1b_t1b = I1_t0b_t1b_t1b; // @[Map2T.scala 16:11]
  assign op_I1_t1b_t0b = I1_t1b_t0b; // @[Map2T.scala 16:11]
  assign op_I1_t1b_t1b_t0b = I1_t1b_t1b_t0b; // @[Map2T.scala 16:11]
  assign op_I1_t1b_t1b_t1b = I1_t1b_t1b_t1b; // @[Map2T.scala 16:11]
endmodule
module Map2T_43(
  input         valid_up,
  output        valid_down,
  input         I0,
  input  [31:0] I1_t0b_t0b,
  input  [31:0] I1_t0b_t1b_t0b,
  input  [31:0] I1_t0b_t1b_t1b,
  input  [31:0] I1_t1b_t0b,
  input  [31:0] I1_t1b_t1b_t0b,
  input  [31:0] I1_t1b_t1b_t1b,
  output        O_t0b,
  output [31:0] O_t1b_t0b_t0b,
  output [31:0] O_t1b_t0b_t1b_t0b,
  output [31:0] O_t1b_t0b_t1b_t1b,
  output [31:0] O_t1b_t1b_t0b,
  output [31:0] O_t1b_t1b_t1b_t0b,
  output [31:0] O_t1b_t1b_t1b_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire  op_I0; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t0b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t0b_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t0b_t1b_t1b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t1b_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_t1b_t1b_t1b; // @[Map2T.scala 8:20]
  wire  op_O_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t0b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t0b_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t0b_t1b_t1b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t1b_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t1b_t1b_t1b_t1b; // @[Map2T.scala 8:20]
  Map2T_42 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0(op_I0),
    .I1_t0b_t0b(op_I1_t0b_t0b),
    .I1_t0b_t1b_t0b(op_I1_t0b_t1b_t0b),
    .I1_t0b_t1b_t1b(op_I1_t0b_t1b_t1b),
    .I1_t1b_t0b(op_I1_t1b_t0b),
    .I1_t1b_t1b_t0b(op_I1_t1b_t1b_t0b),
    .I1_t1b_t1b_t1b(op_I1_t1b_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b_t0b(op_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b_t0b(op_O_t1b_t0b_t1b_t0b),
    .O_t1b_t0b_t1b_t1b(op_O_t1b_t0b_t1b_t1b),
    .O_t1b_t1b_t0b(op_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b_t0b(op_O_t1b_t1b_t1b_t0b),
    .O_t1b_t1b_t1b_t1b(op_O_t1b_t1b_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_t0b = op_O_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t0b_t0b = op_O_t1b_t0b_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t0b_t1b_t0b = op_O_t1b_t0b_t1b_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t0b_t1b_t1b = op_O_t1b_t0b_t1b_t1b; // @[Map2T.scala 17:7]
  assign O_t1b_t1b_t0b = op_O_t1b_t1b_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t1b_t1b_t0b = op_O_t1b_t1b_t1b_t0b; // @[Map2T.scala 17:7]
  assign O_t1b_t1b_t1b_t1b = op_O_t1b_t1b_t1b_t1b; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0 = I0; // @[Map2T.scala 15:11]
  assign op_I1_t0b_t0b = I1_t0b_t0b; // @[Map2T.scala 16:11]
  assign op_I1_t0b_t1b_t0b = I1_t0b_t1b_t0b; // @[Map2T.scala 16:11]
  assign op_I1_t0b_t1b_t1b = I1_t0b_t1b_t1b; // @[Map2T.scala 16:11]
  assign op_I1_t1b_t0b = I1_t1b_t0b; // @[Map2T.scala 16:11]
  assign op_I1_t1b_t1b_t0b = I1_t1b_t1b_t0b; // @[Map2T.scala 16:11]
  assign op_I1_t1b_t1b_t1b = I1_t1b_t1b_t1b; // @[Map2T.scala 16:11]
endmodule
module If(
  input         valid_up,
  output        valid_down,
  input         I_t0b,
  input  [31:0] I_t1b_t0b_t0b,
  input  [31:0] I_t1b_t0b_t1b_t0b,
  input  [31:0] I_t1b_t0b_t1b_t1b,
  input  [31:0] I_t1b_t1b_t0b,
  input  [31:0] I_t1b_t1b_t1b_t0b,
  input  [31:0] I_t1b_t1b_t1b_t1b,
  output [31:0] O_t0b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b
);
  assign valid_down = valid_up; // @[Arithmetic.scala 528:14]
  assign O_t0b = I_t0b ? I_t1b_t0b_t0b : I_t1b_t1b_t0b; // @[Arithmetic.scala 526:9 Arithmetic.scala 527:20]
  assign O_t1b_t0b = I_t0b ? I_t1b_t0b_t1b_t0b : I_t1b_t1b_t1b_t0b; // @[Arithmetic.scala 526:9 Arithmetic.scala 527:20]
  assign O_t1b_t1b = I_t0b ? I_t1b_t0b_t1b_t1b : I_t1b_t1b_t1b_t1b; // @[Arithmetic.scala 526:9 Arithmetic.scala 527:20]
endmodule
module MapT_48(
  input         valid_up,
  output        valid_down,
  input         I_t0b,
  input  [31:0] I_t1b_t0b_t0b,
  input  [31:0] I_t1b_t0b_t1b_t0b,
  input  [31:0] I_t1b_t0b_t1b_t1b,
  input  [31:0] I_t1b_t1b_t0b,
  input  [31:0] I_t1b_t1b_t1b_t0b,
  input  [31:0] I_t1b_t1b_t1b_t1b,
  output [31:0] O_t0b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire  op_I_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t0b_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t0b_t1b_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t0b_t1b_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t1b_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t1b_t1b_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t1b_t1b_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  If op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b_t0b(op_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b_t0b(op_I_t1b_t0b_t1b_t0b),
    .I_t1b_t0b_t1b_t1b(op_I_t1b_t0b_t1b_t1b),
    .I_t1b_t1b_t0b(op_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b_t0b(op_I_t1b_t1b_t1b_t0b),
    .I_t1b_t1b_t1b_t1b(op_I_t1b_t1b_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b_t0b = I_t1b_t0b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b_t1b_t0b = I_t1b_t0b_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b_t1b_t1b = I_t1b_t0b_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b_t0b = I_t1b_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b_t1b_t0b = I_t1b_t1b_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b_t1b_t1b = I_t1b_t1b_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module MapT_49(
  input         valid_up,
  output        valid_down,
  input         I_t0b,
  input  [31:0] I_t1b_t0b_t0b,
  input  [31:0] I_t1b_t0b_t1b_t0b,
  input  [31:0] I_t1b_t0b_t1b_t1b,
  input  [31:0] I_t1b_t1b_t0b,
  input  [31:0] I_t1b_t1b_t1b_t0b,
  input  [31:0] I_t1b_t1b_t1b_t1b,
  output [31:0] O_t0b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire  op_I_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t0b_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t0b_t1b_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t0b_t1b_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t1b_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t1b_t1b_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t1b_t1b_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_O_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_O_t1b_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_O_t1b_t1b; // @[MapT.scala 8:20]
  MapT_48 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b_t0b_t0b(op_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b_t0b(op_I_t1b_t0b_t1b_t0b),
    .I_t1b_t0b_t1b_t1b(op_I_t1b_t0b_t1b_t1b),
    .I_t1b_t1b_t0b(op_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b_t0b(op_I_t1b_t1b_t1b_t0b),
    .I_t1b_t1b_t1b_t1b(op_I_t1b_t1b_t1b_t1b),
    .O_t0b(op_O_t0b),
    .O_t1b_t0b(op_O_t1b_t0b),
    .O_t1b_t1b(op_O_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_t0b = op_O_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t0b = op_O_t1b_t0b; // @[MapT.scala 15:7]
  assign O_t1b_t1b = op_O_t1b_t1b; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b_t0b = I_t1b_t0b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b_t1b_t0b = I_t1b_t0b_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t0b_t1b_t1b = I_t1b_t0b_t1b_t1b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b_t0b = I_t1b_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b_t1b_t0b = I_t1b_t1b_t1b_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b_t1b_t1b_t1b = I_t1b_t1b_t1b_t1b; // @[MapT.scala 14:10]
endmodule
module Module_6(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_0,
  input  [31:0] I0_1,
  input  [31:0] I0_2,
  input         I1_t0b,
  input         I1_t1b,
  output [31:0] O_t0b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b
);
  wire  n132_valid_up; // @[Top.scala 116:22]
  wire  n132_valid_down; // @[Top.scala 116:22]
  wire  n132_I_t0b; // @[Top.scala 116:22]
  wire  n132_O; // @[Top.scala 116:22]
  wire  n427_clock; // @[Top.scala 119:22]
  wire  n427_reset; // @[Top.scala 119:22]
  wire  n427_valid_up; // @[Top.scala 119:22]
  wire  n427_valid_down; // @[Top.scala 119:22]
  wire  n427_I; // @[Top.scala 119:22]
  wire  n427_O; // @[Top.scala 119:22]
  wire  n137_valid_up; // @[Top.scala 122:22]
  wire  n137_valid_down; // @[Top.scala 122:22]
  wire  n137_I_t1b; // @[Top.scala 122:22]
  wire  n137_O; // @[Top.scala 122:22]
  wire  n355_clock; // @[Top.scala 125:22]
  wire  n355_reset; // @[Top.scala 125:22]
  wire  n355_valid_up; // @[Top.scala 125:22]
  wire  n355_valid_down; // @[Top.scala 125:22]
  wire  n355_I; // @[Top.scala 125:22]
  wire  n355_O; // @[Top.scala 125:22]
  wire  n139_valid_up; // @[Top.scala 128:22]
  wire  n139_valid_down; // @[Top.scala 128:22]
  wire [31:0] n139_I_0; // @[Top.scala 128:22]
  wire [31:0] n139_I_1; // @[Top.scala 128:22]
  wire [31:0] n139_I_2; // @[Top.scala 128:22]
  wire [31:0] n139_O_0; // @[Top.scala 128:22]
  wire [31:0] n139_O_1; // @[Top.scala 128:22]
  wire [31:0] n139_O_2; // @[Top.scala 128:22]
  wire  n142_valid_up; // @[Top.scala 131:22]
  wire  n142_valid_down; // @[Top.scala 131:22]
  wire [31:0] n142_I_1; // @[Top.scala 131:22]
  wire [31:0] n142_O_0; // @[Top.scala 131:22]
  wire  n143_valid_up; // @[Top.scala 134:22]
  wire  n143_valid_down; // @[Top.scala 134:22]
  wire [31:0] n143_I_0; // @[Top.scala 134:22]
  wire [31:0] n143_O; // @[Top.scala 134:22]
  wire  n146_valid_up; // @[Top.scala 137:22]
  wire  n146_valid_down; // @[Top.scala 137:22]
  wire [31:0] n146_I; // @[Top.scala 137:22]
  wire [31:0] n146_O; // @[Top.scala 137:22]
  wire  n150_clock; // @[Top.scala 140:22]
  wire  n150_reset; // @[Top.scala 140:22]
  wire  n150_valid_up; // @[Top.scala 140:22]
  wire  n150_valid_down; // @[Top.scala 140:22]
  wire [31:0] n150_I; // @[Top.scala 140:22]
  wire [31:0] n150_O; // @[Top.scala 140:22]
  wire  n149_clock; // @[Top.scala 143:22]
  wire  n149_reset; // @[Top.scala 143:22]
  wire  n149_valid_up; // @[Top.scala 143:22]
  wire  n149_valid_down; // @[Top.scala 143:22]
  wire [31:0] n149_I; // @[Top.scala 143:22]
  wire [31:0] n149_O; // @[Top.scala 143:22]
  wire  n151_valid_up; // @[Top.scala 146:22]
  wire  n151_valid_down; // @[Top.scala 146:22]
  wire [31:0] n151_I0; // @[Top.scala 146:22]
  wire [31:0] n151_I1; // @[Top.scala 146:22]
  wire [31:0] n151_O_t0b; // @[Top.scala 146:22]
  wire [31:0] n151_O_t1b; // @[Top.scala 146:22]
  wire  n162_valid_up; // @[Top.scala 150:22]
  wire  n162_valid_down; // @[Top.scala 150:22]
  wire [31:0] n162_I_t0b; // @[Top.scala 150:22]
  wire [31:0] n162_I_t1b; // @[Top.scala 150:22]
  wire [31:0] n162_O; // @[Top.scala 150:22]
  wire  n169_clock; // @[Top.scala 153:22]
  wire  n169_reset; // @[Top.scala 153:22]
  wire  n169_valid_up; // @[Top.scala 153:22]
  wire  n169_valid_down; // @[Top.scala 153:22]
  wire [31:0] n169_I; // @[Top.scala 153:22]
  wire [31:0] n169_O_t0b; // @[Top.scala 153:22]
  wire [7:0] n169_O_t1b; // @[Top.scala 153:22]
  wire  n174_valid_up; // @[Top.scala 156:22]
  wire  n174_valid_down; // @[Top.scala 156:22]
  wire [31:0] n174_I_t0b; // @[Top.scala 156:22]
  wire [7:0] n174_I_t1b; // @[Top.scala 156:22]
  wire [31:0] n174_O; // @[Top.scala 156:22]
  wire  n177_clock; // @[Top.scala 159:22]
  wire  n177_reset; // @[Top.scala 159:22]
  wire  n177_valid_up; // @[Top.scala 159:22]
  wire  n177_valid_down; // @[Top.scala 159:22]
  wire [31:0] n177_I; // @[Top.scala 159:22]
  wire [31:0] n177_O; // @[Top.scala 159:22]
  wire  n178_valid_up; // @[Top.scala 162:22]
  wire  n178_valid_down; // @[Top.scala 162:22]
  wire [31:0] n178_I_0; // @[Top.scala 162:22]
  wire [31:0] n178_I_1; // @[Top.scala 162:22]
  wire [31:0] n178_I_2; // @[Top.scala 162:22]
  wire [31:0] n178_O_0; // @[Top.scala 162:22]
  wire [31:0] n178_O_1; // @[Top.scala 162:22]
  wire [31:0] n178_O_2; // @[Top.scala 162:22]
  wire  n181_valid_up; // @[Top.scala 165:22]
  wire  n181_valid_down; // @[Top.scala 165:22]
  wire [31:0] n181_I_0; // @[Top.scala 165:22]
  wire [31:0] n181_O_0; // @[Top.scala 165:22]
  wire  n182_valid_up; // @[Top.scala 168:22]
  wire  n182_valid_down; // @[Top.scala 168:22]
  wire [31:0] n182_I_0; // @[Top.scala 168:22]
  wire [31:0] n182_O; // @[Top.scala 168:22]
  wire  n185_clock; // @[Top.scala 171:22]
  wire  n185_reset; // @[Top.scala 171:22]
  wire  n185_valid_up; // @[Top.scala 171:22]
  wire  n185_valid_down; // @[Top.scala 171:22]
  wire [31:0] n185_I; // @[Top.scala 171:22]
  wire [31:0] n185_O; // @[Top.scala 171:22]
  wire  n186_valid_up; // @[Top.scala 174:22]
  wire  n186_valid_down; // @[Top.scala 174:22]
  wire [31:0] n186_I_0; // @[Top.scala 174:22]
  wire [31:0] n186_I_1; // @[Top.scala 174:22]
  wire [31:0] n186_I_2; // @[Top.scala 174:22]
  wire [31:0] n186_O_0; // @[Top.scala 174:22]
  wire [31:0] n186_O_1; // @[Top.scala 174:22]
  wire [31:0] n186_O_2; // @[Top.scala 174:22]
  wire  n189_valid_up; // @[Top.scala 177:22]
  wire  n189_valid_down; // @[Top.scala 177:22]
  wire [31:0] n189_I_2; // @[Top.scala 177:22]
  wire [31:0] n189_O_0; // @[Top.scala 177:22]
  wire  n190_valid_up; // @[Top.scala 180:22]
  wire  n190_valid_down; // @[Top.scala 180:22]
  wire [31:0] n190_I_0; // @[Top.scala 180:22]
  wire [31:0] n190_O; // @[Top.scala 180:22]
  wire  n193_clock; // @[Top.scala 183:22]
  wire  n193_reset; // @[Top.scala 183:22]
  wire  n193_valid_up; // @[Top.scala 183:22]
  wire  n193_valid_down; // @[Top.scala 183:22]
  wire [31:0] n193_I; // @[Top.scala 183:22]
  wire [31:0] n193_O; // @[Top.scala 183:22]
  wire  n194_valid_up; // @[Top.scala 186:22]
  wire  n194_valid_down; // @[Top.scala 186:22]
  wire [31:0] n194_I0; // @[Top.scala 186:22]
  wire [31:0] n194_I1; // @[Top.scala 186:22]
  wire [31:0] n194_O_t0b; // @[Top.scala 186:22]
  wire [31:0] n194_O_t1b; // @[Top.scala 186:22]
  wire  n205_valid_up; // @[Top.scala 190:22]
  wire  n205_valid_down; // @[Top.scala 190:22]
  wire [31:0] n205_I_t0b; // @[Top.scala 190:22]
  wire [31:0] n205_I_t1b; // @[Top.scala 190:22]
  wire [31:0] n205_O; // @[Top.scala 190:22]
  wire  n212_clock; // @[Top.scala 193:22]
  wire  n212_reset; // @[Top.scala 193:22]
  wire  n212_valid_up; // @[Top.scala 193:22]
  wire  n212_valid_down; // @[Top.scala 193:22]
  wire [31:0] n212_I; // @[Top.scala 193:22]
  wire [31:0] n212_O_t0b; // @[Top.scala 193:22]
  wire [7:0] n212_O_t1b; // @[Top.scala 193:22]
  wire  n217_valid_up; // @[Top.scala 196:22]
  wire  n217_valid_down; // @[Top.scala 196:22]
  wire [31:0] n217_I_t0b; // @[Top.scala 196:22]
  wire [7:0] n217_I_t1b; // @[Top.scala 196:22]
  wire [31:0] n217_O; // @[Top.scala 196:22]
  wire  n218_valid_up; // @[Top.scala 199:22]
  wire  n218_valid_down; // @[Top.scala 199:22]
  wire [31:0] n218_I0; // @[Top.scala 199:22]
  wire [31:0] n218_I1; // @[Top.scala 199:22]
  wire [31:0] n218_O_t0b; // @[Top.scala 199:22]
  wire [31:0] n218_O_t1b; // @[Top.scala 199:22]
  wire  n225_clock; // @[Top.scala 203:22]
  wire  n225_reset; // @[Top.scala 203:22]
  wire  n225_valid_up; // @[Top.scala 203:22]
  wire  n225_valid_down; // @[Top.scala 203:22]
  wire [31:0] n225_I_t0b; // @[Top.scala 203:22]
  wire [31:0] n225_I_t1b; // @[Top.scala 203:22]
  wire [31:0] n225_O_t0b; // @[Top.scala 203:22]
  wire [31:0] n225_O_t1b; // @[Top.scala 203:22]
  wire  n226_valid_up; // @[Top.scala 206:22]
  wire  n226_valid_down; // @[Top.scala 206:22]
  wire [31:0] n226_I0; // @[Top.scala 206:22]
  wire [31:0] n226_I1_t0b; // @[Top.scala 206:22]
  wire [31:0] n226_I1_t1b; // @[Top.scala 206:22]
  wire [31:0] n226_O_t0b; // @[Top.scala 206:22]
  wire [31:0] n226_O_t1b_t0b; // @[Top.scala 206:22]
  wire [31:0] n226_O_t1b_t1b; // @[Top.scala 206:22]
  wire  n347_clock; // @[Top.scala 210:22]
  wire  n347_reset; // @[Top.scala 210:22]
  wire  n347_valid_up; // @[Top.scala 210:22]
  wire  n347_valid_down; // @[Top.scala 210:22]
  wire [31:0] n347_I_t0b; // @[Top.scala 210:22]
  wire [31:0] n347_I_t1b_t0b; // @[Top.scala 210:22]
  wire [31:0] n347_I_t1b_t1b; // @[Top.scala 210:22]
  wire [31:0] n347_O_t0b; // @[Top.scala 210:22]
  wire [31:0] n347_O_t1b_t0b; // @[Top.scala 210:22]
  wire [31:0] n347_O_t1b_t1b; // @[Top.scala 210:22]
  wire  n339_clock; // @[Top.scala 213:22]
  wire  n339_reset; // @[Top.scala 213:22]
  wire  n339_valid_up; // @[Top.scala 213:22]
  wire  n339_valid_down; // @[Top.scala 213:22]
  wire [31:0] n339_I; // @[Top.scala 213:22]
  wire [31:0] n339_O; // @[Top.scala 213:22]
  wire  n233_clock; // @[Top.scala 216:22]
  wire  n233_reset; // @[Top.scala 216:22]
  wire  n233_valid_up; // @[Top.scala 216:22]
  wire  n233_valid_down; // @[Top.scala 216:22]
  wire [31:0] n233_I; // @[Top.scala 216:22]
  wire [31:0] n233_O; // @[Top.scala 216:22]
  wire  n234_valid_up; // @[Top.scala 219:22]
  wire  n234_valid_down; // @[Top.scala 219:22]
  wire [31:0] n234_I0; // @[Top.scala 219:22]
  wire [31:0] n234_I1; // @[Top.scala 219:22]
  wire [31:0] n234_O_0; // @[Top.scala 219:22]
  wire [31:0] n234_O_1; // @[Top.scala 219:22]
  wire  n241_clock; // @[Top.scala 223:22]
  wire  n241_reset; // @[Top.scala 223:22]
  wire  n241_valid_up; // @[Top.scala 223:22]
  wire  n241_valid_down; // @[Top.scala 223:22]
  wire [31:0] n241_I_0; // @[Top.scala 223:22]
  wire [31:0] n241_I_1; // @[Top.scala 223:22]
  wire [31:0] n241_O_0; // @[Top.scala 223:22]
  wire [31:0] n241_O_1; // @[Top.scala 223:22]
  wire  n242_valid_up; // @[Top.scala 226:22]
  wire  n242_valid_down; // @[Top.scala 226:22]
  wire [31:0] n242_I0_0; // @[Top.scala 226:22]
  wire [31:0] n242_I0_1; // @[Top.scala 226:22]
  wire [31:0] n242_I1; // @[Top.scala 226:22]
  wire [31:0] n242_O_0; // @[Top.scala 226:22]
  wire [31:0] n242_O_1; // @[Top.scala 226:22]
  wire [31:0] n242_O_2; // @[Top.scala 226:22]
  wire  n249_clock; // @[Top.scala 230:22]
  wire  n249_reset; // @[Top.scala 230:22]
  wire  n249_valid_up; // @[Top.scala 230:22]
  wire  n249_valid_down; // @[Top.scala 230:22]
  wire [31:0] n249_I; // @[Top.scala 230:22]
  wire [31:0] n249_O; // @[Top.scala 230:22]
  wire  n250_valid_up; // @[Top.scala 233:22]
  wire  n250_valid_down; // @[Top.scala 233:22]
  wire [31:0] n250_I0_0; // @[Top.scala 233:22]
  wire [31:0] n250_I0_1; // @[Top.scala 233:22]
  wire [31:0] n250_I0_2; // @[Top.scala 233:22]
  wire [31:0] n250_I1; // @[Top.scala 233:22]
  wire [31:0] n250_O_0; // @[Top.scala 233:22]
  wire [31:0] n250_O_1; // @[Top.scala 233:22]
  wire [31:0] n250_O_2; // @[Top.scala 233:22]
  wire [31:0] n250_O_3; // @[Top.scala 233:22]
  wire  n259_clock; // @[Top.scala 237:22]
  wire  n259_reset; // @[Top.scala 237:22]
  wire  n259_valid_up; // @[Top.scala 237:22]
  wire  n259_valid_down; // @[Top.scala 237:22]
  wire [31:0] n259_I_0; // @[Top.scala 237:22]
  wire [31:0] n259_I_1; // @[Top.scala 237:22]
  wire [31:0] n259_I_2; // @[Top.scala 237:22]
  wire [31:0] n259_I_3; // @[Top.scala 237:22]
  wire [31:0] n259_O; // @[Top.scala 237:22]
  wire  n264_clock; // @[Top.scala 240:22]
  wire  n264_reset; // @[Top.scala 240:22]
  wire  n264_valid_up; // @[Top.scala 240:22]
  wire  n264_valid_down; // @[Top.scala 240:22]
  wire [31:0] n264_I; // @[Top.scala 240:22]
  wire [31:0] n264_O; // @[Top.scala 240:22]
  wire  n271_clock; // @[Top.scala 243:22]
  wire  n271_reset; // @[Top.scala 243:22]
  wire  n271_valid_up; // @[Top.scala 243:22]
  wire  n271_valid_down; // @[Top.scala 243:22]
  wire [31:0] n271_I; // @[Top.scala 243:22]
  wire [31:0] n271_O_t0b; // @[Top.scala 243:22]
  wire [7:0] n271_O_t1b; // @[Top.scala 243:22]
  wire  n276_valid_up; // @[Top.scala 246:22]
  wire  n276_valid_down; // @[Top.scala 246:22]
  wire [31:0] n276_I_t0b; // @[Top.scala 246:22]
  wire [7:0] n276_I_t1b; // @[Top.scala 246:22]
  wire [31:0] n276_O; // @[Top.scala 246:22]
  wire  n279_valid_up; // @[Top.scala 249:22]
  wire  n279_valid_down; // @[Top.scala 249:22]
  wire [31:0] n279_I; // @[Top.scala 249:22]
  wire [31:0] n279_O; // @[Top.scala 249:22]
  wire  n283_clock; // @[Top.scala 252:22]
  wire  n283_reset; // @[Top.scala 252:22]
  wire  n283_valid_up; // @[Top.scala 252:22]
  wire  n283_valid_down; // @[Top.scala 252:22]
  wire [31:0] n283_I; // @[Top.scala 252:22]
  wire [31:0] n283_O; // @[Top.scala 252:22]
  wire  n282_clock; // @[Top.scala 255:22]
  wire  n282_reset; // @[Top.scala 255:22]
  wire  n282_valid_up; // @[Top.scala 255:22]
  wire  n282_valid_down; // @[Top.scala 255:22]
  wire [31:0] n282_I; // @[Top.scala 255:22]
  wire [31:0] n282_O; // @[Top.scala 255:22]
  wire  n284_valid_up; // @[Top.scala 258:22]
  wire  n284_valid_down; // @[Top.scala 258:22]
  wire [31:0] n284_I0; // @[Top.scala 258:22]
  wire [31:0] n284_I1; // @[Top.scala 258:22]
  wire [31:0] n284_O_0; // @[Top.scala 258:22]
  wire [31:0] n284_O_1; // @[Top.scala 258:22]
  wire  n293_valid_up; // @[Top.scala 262:22]
  wire  n293_valid_down; // @[Top.scala 262:22]
  wire [31:0] n293_I; // @[Top.scala 262:22]
  wire [31:0] n293_O; // @[Top.scala 262:22]
  wire  n294_clock; // @[Top.scala 265:22]
  wire  n294_reset; // @[Top.scala 265:22]
  wire  n294_valid_up; // @[Top.scala 265:22]
  wire  n294_valid_down; // @[Top.scala 265:22]
  wire [31:0] n294_I; // @[Top.scala 265:22]
  wire [31:0] n294_O; // @[Top.scala 265:22]
  wire  n295_valid_up; // @[Top.scala 268:22]
  wire  n295_valid_down; // @[Top.scala 268:22]
  wire [31:0] n295_I0_0; // @[Top.scala 268:22]
  wire [31:0] n295_I0_1; // @[Top.scala 268:22]
  wire [31:0] n295_I1; // @[Top.scala 268:22]
  wire [31:0] n295_O_0; // @[Top.scala 268:22]
  wire [31:0] n295_O_1; // @[Top.scala 268:22]
  wire [31:0] n295_O_2; // @[Top.scala 268:22]
  wire  n304_clock; // @[Top.scala 272:22]
  wire  n304_reset; // @[Top.scala 272:22]
  wire  n304_valid_up; // @[Top.scala 272:22]
  wire  n304_valid_down; // @[Top.scala 272:22]
  wire [31:0] n304_I; // @[Top.scala 272:22]
  wire [31:0] n304_O; // @[Top.scala 272:22]
  wire  n305_valid_up; // @[Top.scala 275:22]
  wire  n305_valid_down; // @[Top.scala 275:22]
  wire [31:0] n305_I0_0; // @[Top.scala 275:22]
  wire [31:0] n305_I0_1; // @[Top.scala 275:22]
  wire [31:0] n305_I0_2; // @[Top.scala 275:22]
  wire [31:0] n305_I1; // @[Top.scala 275:22]
  wire [31:0] n305_O_0; // @[Top.scala 275:22]
  wire [31:0] n305_O_1; // @[Top.scala 275:22]
  wire [31:0] n305_O_2; // @[Top.scala 275:22]
  wire [31:0] n305_O_3; // @[Top.scala 275:22]
  wire  n314_clock; // @[Top.scala 279:22]
  wire  n314_reset; // @[Top.scala 279:22]
  wire  n314_valid_up; // @[Top.scala 279:22]
  wire  n314_valid_down; // @[Top.scala 279:22]
  wire [31:0] n314_I_0; // @[Top.scala 279:22]
  wire [31:0] n314_I_1; // @[Top.scala 279:22]
  wire [31:0] n314_I_2; // @[Top.scala 279:22]
  wire [31:0] n314_I_3; // @[Top.scala 279:22]
  wire [31:0] n314_O; // @[Top.scala 279:22]
  wire  n319_clock; // @[Top.scala 282:22]
  wire  n319_reset; // @[Top.scala 282:22]
  wire  n319_valid_up; // @[Top.scala 282:22]
  wire  n319_valid_down; // @[Top.scala 282:22]
  wire [31:0] n319_I; // @[Top.scala 282:22]
  wire [31:0] n319_O; // @[Top.scala 282:22]
  wire  n326_clock; // @[Top.scala 285:22]
  wire  n326_reset; // @[Top.scala 285:22]
  wire  n326_valid_up; // @[Top.scala 285:22]
  wire  n326_valid_down; // @[Top.scala 285:22]
  wire [31:0] n326_I; // @[Top.scala 285:22]
  wire [31:0] n326_O_t0b; // @[Top.scala 285:22]
  wire [7:0] n326_O_t1b; // @[Top.scala 285:22]
  wire  n331_valid_up; // @[Top.scala 288:22]
  wire  n331_valid_down; // @[Top.scala 288:22]
  wire [31:0] n331_I_t0b; // @[Top.scala 288:22]
  wire [7:0] n331_I_t1b; // @[Top.scala 288:22]
  wire [31:0] n331_O; // @[Top.scala 288:22]
  wire  n332_valid_up; // @[Top.scala 291:22]
  wire  n332_valid_down; // @[Top.scala 291:22]
  wire [31:0] n332_I0; // @[Top.scala 291:22]
  wire [31:0] n332_I1; // @[Top.scala 291:22]
  wire [31:0] n332_O_t0b; // @[Top.scala 291:22]
  wire [31:0] n332_O_t1b; // @[Top.scala 291:22]
  wire  n340_valid_up; // @[Top.scala 295:22]
  wire  n340_valid_down; // @[Top.scala 295:22]
  wire [31:0] n340_I0; // @[Top.scala 295:22]
  wire [31:0] n340_I1_t0b; // @[Top.scala 295:22]
  wire [31:0] n340_I1_t1b; // @[Top.scala 295:22]
  wire [31:0] n340_O_t0b; // @[Top.scala 295:22]
  wire [31:0] n340_O_t1b_t0b; // @[Top.scala 295:22]
  wire [31:0] n340_O_t1b_t1b; // @[Top.scala 295:22]
  wire  n348_valid_up; // @[Top.scala 299:22]
  wire  n348_valid_down; // @[Top.scala 299:22]
  wire [31:0] n348_I0_t0b; // @[Top.scala 299:22]
  wire [31:0] n348_I0_t1b_t0b; // @[Top.scala 299:22]
  wire [31:0] n348_I0_t1b_t1b; // @[Top.scala 299:22]
  wire [31:0] n348_I1_t0b; // @[Top.scala 299:22]
  wire [31:0] n348_I1_t1b_t0b; // @[Top.scala 299:22]
  wire [31:0] n348_I1_t1b_t1b; // @[Top.scala 299:22]
  wire [31:0] n348_O_t0b_t0b; // @[Top.scala 299:22]
  wire [31:0] n348_O_t0b_t1b_t0b; // @[Top.scala 299:22]
  wire [31:0] n348_O_t0b_t1b_t1b; // @[Top.scala 299:22]
  wire [31:0] n348_O_t1b_t0b; // @[Top.scala 299:22]
  wire [31:0] n348_O_t1b_t1b_t0b; // @[Top.scala 299:22]
  wire [31:0] n348_O_t1b_t1b_t1b; // @[Top.scala 299:22]
  wire  n356_valid_up; // @[Top.scala 303:22]
  wire  n356_valid_down; // @[Top.scala 303:22]
  wire  n356_I0; // @[Top.scala 303:22]
  wire [31:0] n356_I1_t0b_t0b; // @[Top.scala 303:22]
  wire [31:0] n356_I1_t0b_t1b_t0b; // @[Top.scala 303:22]
  wire [31:0] n356_I1_t0b_t1b_t1b; // @[Top.scala 303:22]
  wire [31:0] n356_I1_t1b_t0b; // @[Top.scala 303:22]
  wire [31:0] n356_I1_t1b_t1b_t0b; // @[Top.scala 303:22]
  wire [31:0] n356_I1_t1b_t1b_t1b; // @[Top.scala 303:22]
  wire  n356_O_t0b; // @[Top.scala 303:22]
  wire [31:0] n356_O_t1b_t0b_t0b; // @[Top.scala 303:22]
  wire [31:0] n356_O_t1b_t0b_t1b_t0b; // @[Top.scala 303:22]
  wire [31:0] n356_O_t1b_t0b_t1b_t1b; // @[Top.scala 303:22]
  wire [31:0] n356_O_t1b_t1b_t0b; // @[Top.scala 303:22]
  wire [31:0] n356_O_t1b_t1b_t1b_t0b; // @[Top.scala 303:22]
  wire [31:0] n356_O_t1b_t1b_t1b_t1b; // @[Top.scala 303:22]
  wire  n367_valid_up; // @[Top.scala 307:22]
  wire  n367_valid_down; // @[Top.scala 307:22]
  wire  n367_I_t0b; // @[Top.scala 307:22]
  wire [31:0] n367_I_t1b_t0b_t0b; // @[Top.scala 307:22]
  wire [31:0] n367_I_t1b_t0b_t1b_t0b; // @[Top.scala 307:22]
  wire [31:0] n367_I_t1b_t0b_t1b_t1b; // @[Top.scala 307:22]
  wire [31:0] n367_I_t1b_t1b_t0b; // @[Top.scala 307:22]
  wire [31:0] n367_I_t1b_t1b_t1b_t0b; // @[Top.scala 307:22]
  wire [31:0] n367_I_t1b_t1b_t1b_t1b; // @[Top.scala 307:22]
  wire [31:0] n367_O_t0b; // @[Top.scala 307:22]
  wire [31:0] n367_O_t1b_t0b; // @[Top.scala 307:22]
  wire [31:0] n367_O_t1b_t1b; // @[Top.scala 307:22]
  wire  n407_clock; // @[Top.scala 310:22]
  wire  n407_reset; // @[Top.scala 310:22]
  wire  n407_valid_up; // @[Top.scala 310:22]
  wire  n407_valid_down; // @[Top.scala 310:22]
  wire  n407_I; // @[Top.scala 310:22]
  wire  n407_O; // @[Top.scala 310:22]
  wire  n368_clock; // @[Top.scala 313:22]
  wire  n368_reset; // @[Top.scala 313:22]
  wire  n368_valid_up; // @[Top.scala 313:22]
  wire  n368_valid_down; // @[Top.scala 313:22]
  wire [31:0] n368_I; // @[Top.scala 313:22]
  wire [31:0] n368_O; // @[Top.scala 313:22]
  wire  n369_valid_up; // @[Top.scala 316:22]
  wire  n369_valid_down; // @[Top.scala 316:22]
  wire [31:0] n369_I0; // @[Top.scala 316:22]
  wire [31:0] n369_I1; // @[Top.scala 316:22]
  wire [31:0] n369_O_t0b; // @[Top.scala 316:22]
  wire [31:0] n369_O_t1b; // @[Top.scala 316:22]
  wire  n376_valid_up; // @[Top.scala 320:22]
  wire  n376_valid_down; // @[Top.scala 320:22]
  wire [31:0] n376_I0; // @[Top.scala 320:22]
  wire [31:0] n376_I1_t0b; // @[Top.scala 320:22]
  wire [31:0] n376_I1_t1b; // @[Top.scala 320:22]
  wire [31:0] n376_O_t0b; // @[Top.scala 320:22]
  wire [31:0] n376_O_t1b_t0b; // @[Top.scala 320:22]
  wire [31:0] n376_O_t1b_t1b; // @[Top.scala 320:22]
  wire  n391_clock; // @[Top.scala 324:22]
  wire  n391_reset; // @[Top.scala 324:22]
  wire  n391_valid_up; // @[Top.scala 324:22]
  wire  n391_valid_down; // @[Top.scala 324:22]
  wire [31:0] n391_I; // @[Top.scala 324:22]
  wire [31:0] n391_O; // @[Top.scala 324:22]
  wire  n383_clock; // @[Top.scala 327:22]
  wire  n383_reset; // @[Top.scala 327:22]
  wire  n383_valid_up; // @[Top.scala 327:22]
  wire  n383_valid_down; // @[Top.scala 327:22]
  wire [31:0] n383_I; // @[Top.scala 327:22]
  wire [31:0] n383_O; // @[Top.scala 327:22]
  wire  n384_valid_up; // @[Top.scala 330:22]
  wire  n384_valid_down; // @[Top.scala 330:22]
  wire [31:0] n384_I0; // @[Top.scala 330:22]
  wire [31:0] n384_I1; // @[Top.scala 330:22]
  wire [31:0] n384_O_t0b; // @[Top.scala 330:22]
  wire [31:0] n384_O_t1b; // @[Top.scala 330:22]
  wire  n392_valid_up; // @[Top.scala 334:22]
  wire  n392_valid_down; // @[Top.scala 334:22]
  wire [31:0] n392_I0; // @[Top.scala 334:22]
  wire [31:0] n392_I1_t0b; // @[Top.scala 334:22]
  wire [31:0] n392_I1_t1b; // @[Top.scala 334:22]
  wire [31:0] n392_O_t0b; // @[Top.scala 334:22]
  wire [31:0] n392_O_t1b_t0b; // @[Top.scala 334:22]
  wire [31:0] n392_O_t1b_t1b; // @[Top.scala 334:22]
  wire  n399_clock; // @[Top.scala 338:22]
  wire  n399_reset; // @[Top.scala 338:22]
  wire  n399_valid_up; // @[Top.scala 338:22]
  wire  n399_valid_down; // @[Top.scala 338:22]
  wire [31:0] n399_I_t0b; // @[Top.scala 338:22]
  wire [31:0] n399_I_t1b_t0b; // @[Top.scala 338:22]
  wire [31:0] n399_I_t1b_t1b; // @[Top.scala 338:22]
  wire [31:0] n399_O_t0b; // @[Top.scala 338:22]
  wire [31:0] n399_O_t1b_t0b; // @[Top.scala 338:22]
  wire [31:0] n399_O_t1b_t1b; // @[Top.scala 338:22]
  wire  n400_valid_up; // @[Top.scala 341:22]
  wire  n400_valid_down; // @[Top.scala 341:22]
  wire [31:0] n400_I0_t0b; // @[Top.scala 341:22]
  wire [31:0] n400_I0_t1b_t0b; // @[Top.scala 341:22]
  wire [31:0] n400_I0_t1b_t1b; // @[Top.scala 341:22]
  wire [31:0] n400_I1_t0b; // @[Top.scala 341:22]
  wire [31:0] n400_I1_t1b_t0b; // @[Top.scala 341:22]
  wire [31:0] n400_I1_t1b_t1b; // @[Top.scala 341:22]
  wire [31:0] n400_O_t0b_t0b; // @[Top.scala 341:22]
  wire [31:0] n400_O_t0b_t1b_t0b; // @[Top.scala 341:22]
  wire [31:0] n400_O_t0b_t1b_t1b; // @[Top.scala 341:22]
  wire [31:0] n400_O_t1b_t0b; // @[Top.scala 341:22]
  wire [31:0] n400_O_t1b_t1b_t0b; // @[Top.scala 341:22]
  wire [31:0] n400_O_t1b_t1b_t1b; // @[Top.scala 341:22]
  wire  n408_valid_up; // @[Top.scala 345:22]
  wire  n408_valid_down; // @[Top.scala 345:22]
  wire  n408_I0; // @[Top.scala 345:22]
  wire [31:0] n408_I1_t0b_t0b; // @[Top.scala 345:22]
  wire [31:0] n408_I1_t0b_t1b_t0b; // @[Top.scala 345:22]
  wire [31:0] n408_I1_t0b_t1b_t1b; // @[Top.scala 345:22]
  wire [31:0] n408_I1_t1b_t0b; // @[Top.scala 345:22]
  wire [31:0] n408_I1_t1b_t1b_t0b; // @[Top.scala 345:22]
  wire [31:0] n408_I1_t1b_t1b_t1b; // @[Top.scala 345:22]
  wire  n408_O_t0b; // @[Top.scala 345:22]
  wire [31:0] n408_O_t1b_t0b_t0b; // @[Top.scala 345:22]
  wire [31:0] n408_O_t1b_t0b_t1b_t0b; // @[Top.scala 345:22]
  wire [31:0] n408_O_t1b_t0b_t1b_t1b; // @[Top.scala 345:22]
  wire [31:0] n408_O_t1b_t1b_t0b; // @[Top.scala 345:22]
  wire [31:0] n408_O_t1b_t1b_t1b_t0b; // @[Top.scala 345:22]
  wire [31:0] n408_O_t1b_t1b_t1b_t1b; // @[Top.scala 345:22]
  wire  n419_valid_up; // @[Top.scala 349:22]
  wire  n419_valid_down; // @[Top.scala 349:22]
  wire  n419_I_t0b; // @[Top.scala 349:22]
  wire [31:0] n419_I_t1b_t0b_t0b; // @[Top.scala 349:22]
  wire [31:0] n419_I_t1b_t0b_t1b_t0b; // @[Top.scala 349:22]
  wire [31:0] n419_I_t1b_t0b_t1b_t1b; // @[Top.scala 349:22]
  wire [31:0] n419_I_t1b_t1b_t0b; // @[Top.scala 349:22]
  wire [31:0] n419_I_t1b_t1b_t1b_t0b; // @[Top.scala 349:22]
  wire [31:0] n419_I_t1b_t1b_t1b_t1b; // @[Top.scala 349:22]
  wire [31:0] n419_O_t0b; // @[Top.scala 349:22]
  wire [31:0] n419_O_t1b_t0b; // @[Top.scala 349:22]
  wire [31:0] n419_O_t1b_t1b; // @[Top.scala 349:22]
  wire  n420_valid_up; // @[Top.scala 352:22]
  wire  n420_valid_down; // @[Top.scala 352:22]
  wire [31:0] n420_I0_t0b; // @[Top.scala 352:22]
  wire [31:0] n420_I0_t1b_t0b; // @[Top.scala 352:22]
  wire [31:0] n420_I0_t1b_t1b; // @[Top.scala 352:22]
  wire [31:0] n420_I1_t0b; // @[Top.scala 352:22]
  wire [31:0] n420_I1_t1b_t0b; // @[Top.scala 352:22]
  wire [31:0] n420_I1_t1b_t1b; // @[Top.scala 352:22]
  wire [31:0] n420_O_t0b_t0b; // @[Top.scala 352:22]
  wire [31:0] n420_O_t0b_t1b_t0b; // @[Top.scala 352:22]
  wire [31:0] n420_O_t0b_t1b_t1b; // @[Top.scala 352:22]
  wire [31:0] n420_O_t1b_t0b; // @[Top.scala 352:22]
  wire [31:0] n420_O_t1b_t1b_t0b; // @[Top.scala 352:22]
  wire [31:0] n420_O_t1b_t1b_t1b; // @[Top.scala 352:22]
  wire  n428_valid_up; // @[Top.scala 356:22]
  wire  n428_valid_down; // @[Top.scala 356:22]
  wire  n428_I0; // @[Top.scala 356:22]
  wire [31:0] n428_I1_t0b_t0b; // @[Top.scala 356:22]
  wire [31:0] n428_I1_t0b_t1b_t0b; // @[Top.scala 356:22]
  wire [31:0] n428_I1_t0b_t1b_t1b; // @[Top.scala 356:22]
  wire [31:0] n428_I1_t1b_t0b; // @[Top.scala 356:22]
  wire [31:0] n428_I1_t1b_t1b_t0b; // @[Top.scala 356:22]
  wire [31:0] n428_I1_t1b_t1b_t1b; // @[Top.scala 356:22]
  wire  n428_O_t0b; // @[Top.scala 356:22]
  wire [31:0] n428_O_t1b_t0b_t0b; // @[Top.scala 356:22]
  wire [31:0] n428_O_t1b_t0b_t1b_t0b; // @[Top.scala 356:22]
  wire [31:0] n428_O_t1b_t0b_t1b_t1b; // @[Top.scala 356:22]
  wire [31:0] n428_O_t1b_t1b_t0b; // @[Top.scala 356:22]
  wire [31:0] n428_O_t1b_t1b_t1b_t0b; // @[Top.scala 356:22]
  wire [31:0] n428_O_t1b_t1b_t1b_t1b; // @[Top.scala 356:22]
  wire  n439_valid_up; // @[Top.scala 360:22]
  wire  n439_valid_down; // @[Top.scala 360:22]
  wire  n439_I_t0b; // @[Top.scala 360:22]
  wire [31:0] n439_I_t1b_t0b_t0b; // @[Top.scala 360:22]
  wire [31:0] n439_I_t1b_t0b_t1b_t0b; // @[Top.scala 360:22]
  wire [31:0] n439_I_t1b_t0b_t1b_t1b; // @[Top.scala 360:22]
  wire [31:0] n439_I_t1b_t1b_t0b; // @[Top.scala 360:22]
  wire [31:0] n439_I_t1b_t1b_t1b_t0b; // @[Top.scala 360:22]
  wire [31:0] n439_I_t1b_t1b_t1b_t1b; // @[Top.scala 360:22]
  wire [31:0] n439_O_t0b; // @[Top.scala 360:22]
  wire [31:0] n439_O_t1b_t0b; // @[Top.scala 360:22]
  wire [31:0] n439_O_t1b_t1b; // @[Top.scala 360:22]
  MapT_9 n132 ( // @[Top.scala 116:22]
    .valid_up(n132_valid_up),
    .valid_down(n132_valid_down),
    .I_t0b(n132_I_t0b),
    .O(n132_O)
  );
  FIFO_2 n427 ( // @[Top.scala 119:22]
    .clock(n427_clock),
    .reset(n427_reset),
    .valid_up(n427_valid_up),
    .valid_down(n427_valid_down),
    .I(n427_I),
    .O(n427_O)
  );
  MapT_11 n137 ( // @[Top.scala 122:22]
    .valid_up(n137_valid_up),
    .valid_down(n137_valid_down),
    .I_t1b(n137_I_t1b),
    .O(n137_O)
  );
  FIFO_2 n355 ( // @[Top.scala 125:22]
    .clock(n355_clock),
    .reset(n355_reset),
    .valid_up(n355_valid_up),
    .valid_down(n355_valid_down),
    .I(n355_I),
    .O(n355_O)
  );
  DownT n139 ( // @[Top.scala 128:22]
    .valid_up(n139_valid_up),
    .valid_down(n139_valid_down),
    .I_0(n139_I_0),
    .I_1(n139_I_1),
    .I_2(n139_I_2),
    .O_0(n139_O_0),
    .O_1(n139_O_1),
    .O_2(n139_O_2)
  );
  MapT_12 n142 ( // @[Top.scala 131:22]
    .valid_up(n142_valid_up),
    .valid_down(n142_valid_down),
    .I_1(n142_I_1),
    .O_0(n142_O_0)
  );
  Passthrough_8 n143 ( // @[Top.scala 134:22]
    .valid_up(n143_valid_up),
    .valid_down(n143_valid_down),
    .I_0(n143_I_0),
    .O(n143_O)
  );
  MapT_13 n146 ( // @[Top.scala 137:22]
    .valid_up(n146_valid_up),
    .valid_down(n146_valid_down),
    .I(n146_I),
    .O(n146_O)
  );
  FIFO_4 n150 ( // @[Top.scala 140:22]
    .clock(n150_clock),
    .reset(n150_reset),
    .valid_up(n150_valid_up),
    .valid_down(n150_valid_down),
    .I(n150_I),
    .O(n150_O)
  );
  MapT_14 n149 ( // @[Top.scala 143:22]
    .clock(n149_clock),
    .reset(n149_reset),
    .valid_up(n149_valid_up),
    .valid_down(n149_valid_down),
    .I(n149_I),
    .O(n149_O)
  );
  Map2T_17 n151 ( // @[Top.scala 146:22]
    .valid_up(n151_valid_up),
    .valid_down(n151_valid_down),
    .I0(n151_I0),
    .I1(n151_I1),
    .O_t0b(n151_O_t0b),
    .O_t1b(n151_O_t1b)
  );
  MapT_16 n162 ( // @[Top.scala 150:22]
    .valid_up(n162_valid_up),
    .valid_down(n162_valid_down),
    .I_t0b(n162_I_t0b),
    .I_t1b(n162_I_t1b),
    .O(n162_O)
  );
  MapT_18 n169 ( // @[Top.scala 153:22]
    .clock(n169_clock),
    .reset(n169_reset),
    .valid_up(n169_valid_up),
    .valid_down(n169_valid_down),
    .I(n169_I),
    .O_t0b(n169_O_t0b),
    .O_t1b(n169_O_t1b)
  );
  MapT_20 n174 ( // @[Top.scala 156:22]
    .valid_up(n174_valid_up),
    .valid_down(n174_valid_down),
    .I_t0b(n174_I_t0b),
    .I_t1b(n174_I_t1b),
    .O(n174_O)
  );
  MapT_21 n177 ( // @[Top.scala 159:22]
    .clock(n177_clock),
    .reset(n177_reset),
    .valid_up(n177_valid_up),
    .valid_down(n177_valid_down),
    .I(n177_I),
    .O(n177_O)
  );
  DownT n178 ( // @[Top.scala 162:22]
    .valid_up(n178_valid_up),
    .valid_down(n178_valid_down),
    .I_0(n178_I_0),
    .I_1(n178_I_1),
    .I_2(n178_I_2),
    .O_0(n178_O_0),
    .O_1(n178_O_1),
    .O_2(n178_O_2)
  );
  MapT_22 n181 ( // @[Top.scala 165:22]
    .valid_up(n181_valid_up),
    .valid_down(n181_valid_down),
    .I_0(n181_I_0),
    .O_0(n181_O_0)
  );
  Passthrough_8 n182 ( // @[Top.scala 168:22]
    .valid_up(n182_valid_up),
    .valid_down(n182_valid_down),
    .I_0(n182_I_0),
    .O(n182_O)
  );
  MapT_21 n185 ( // @[Top.scala 171:22]
    .clock(n185_clock),
    .reset(n185_reset),
    .valid_up(n185_valid_up),
    .valid_down(n185_valid_down),
    .I(n185_I),
    .O(n185_O)
  );
  DownT n186 ( // @[Top.scala 174:22]
    .valid_up(n186_valid_up),
    .valid_down(n186_valid_down),
    .I_0(n186_I_0),
    .I_1(n186_I_1),
    .I_2(n186_I_2),
    .O_0(n186_O_0),
    .O_1(n186_O_1),
    .O_2(n186_O_2)
  );
  MapT_24 n189 ( // @[Top.scala 177:22]
    .valid_up(n189_valid_up),
    .valid_down(n189_valid_down),
    .I_2(n189_I_2),
    .O_0(n189_O_0)
  );
  Passthrough_8 n190 ( // @[Top.scala 180:22]
    .valid_up(n190_valid_up),
    .valid_down(n190_valid_down),
    .I_0(n190_I_0),
    .O(n190_O)
  );
  MapT_21 n193 ( // @[Top.scala 183:22]
    .clock(n193_clock),
    .reset(n193_reset),
    .valid_up(n193_valid_up),
    .valid_down(n193_valid_down),
    .I(n193_I),
    .O(n193_O)
  );
  Map2T_17 n194 ( // @[Top.scala 186:22]
    .valid_up(n194_valid_up),
    .valid_down(n194_valid_down),
    .I0(n194_I0),
    .I1(n194_I1),
    .O_t0b(n194_O_t0b),
    .O_t1b(n194_O_t1b)
  );
  MapT_16 n205 ( // @[Top.scala 190:22]
    .valid_up(n205_valid_up),
    .valid_down(n205_valid_down),
    .I_t0b(n205_I_t0b),
    .I_t1b(n205_I_t1b),
    .O(n205_O)
  );
  MapT_29 n212 ( // @[Top.scala 193:22]
    .clock(n212_clock),
    .reset(n212_reset),
    .valid_up(n212_valid_up),
    .valid_down(n212_valid_down),
    .I(n212_I),
    .O_t0b(n212_O_t0b),
    .O_t1b(n212_O_t1b)
  );
  MapT_20 n217 ( // @[Top.scala 196:22]
    .valid_up(n217_valid_up),
    .valid_down(n217_valid_down),
    .I_t0b(n217_I_t0b),
    .I_t1b(n217_I_t1b),
    .O(n217_O)
  );
  Map2T_17 n218 ( // @[Top.scala 199:22]
    .valid_up(n218_valid_up),
    .valid_down(n218_valid_down),
    .I0(n218_I0),
    .I1(n218_I1),
    .O_t0b(n218_O_t0b),
    .O_t1b(n218_O_t1b)
  );
  FIFO_5 n225 ( // @[Top.scala 203:22]
    .clock(n225_clock),
    .reset(n225_reset),
    .valid_up(n225_valid_up),
    .valid_down(n225_valid_down),
    .I_t0b(n225_I_t0b),
    .I_t1b(n225_I_t1b),
    .O_t0b(n225_O_t0b),
    .O_t1b(n225_O_t1b)
  );
  Map2T_23 n226 ( // @[Top.scala 206:22]
    .valid_up(n226_valid_up),
    .valid_down(n226_valid_down),
    .I0(n226_I0),
    .I1_t0b(n226_I1_t0b),
    .I1_t1b(n226_I1_t1b),
    .O_t0b(n226_O_t0b),
    .O_t1b_t0b(n226_O_t1b_t0b),
    .O_t1b_t1b(n226_O_t1b_t1b)
  );
  FIFO_6 n347 ( // @[Top.scala 210:22]
    .clock(n347_clock),
    .reset(n347_reset),
    .valid_up(n347_valid_up),
    .valid_down(n347_valid_down),
    .I_t0b(n347_I_t0b),
    .I_t1b_t0b(n347_I_t1b_t0b),
    .I_t1b_t1b(n347_I_t1b_t1b),
    .O_t0b(n347_O_t0b),
    .O_t1b_t0b(n347_O_t1b_t0b),
    .O_t1b_t1b(n347_O_t1b_t1b)
  );
  FIFO_7 n339 ( // @[Top.scala 213:22]
    .clock(n339_clock),
    .reset(n339_reset),
    .valid_up(n339_valid_up),
    .valid_down(n339_valid_down),
    .I(n339_I),
    .O(n339_O)
  );
  FIFO n233 ( // @[Top.scala 216:22]
    .clock(n233_clock),
    .reset(n233_reset),
    .valid_up(n233_valid_up),
    .valid_down(n233_valid_down),
    .I(n233_I),
    .O(n233_O)
  );
  Map2T_1 n234 ( // @[Top.scala 219:22]
    .valid_up(n234_valid_up),
    .valid_down(n234_valid_down),
    .I0(n234_I0),
    .I1(n234_I1),
    .O_0(n234_O_0),
    .O_1(n234_O_1)
  );
  FIFO_9 n241 ( // @[Top.scala 223:22]
    .clock(n241_clock),
    .reset(n241_reset),
    .valid_up(n241_valid_up),
    .valid_down(n241_valid_down),
    .I_0(n241_I_0),
    .I_1(n241_I_1),
    .O_0(n241_O_0),
    .O_1(n241_O_1)
  );
  Map2T_3 n242 ( // @[Top.scala 226:22]
    .valid_up(n242_valid_up),
    .valid_down(n242_valid_down),
    .I0_0(n242_I0_0),
    .I0_1(n242_I0_1),
    .I1(n242_I1),
    .O_0(n242_O_0),
    .O_1(n242_O_1),
    .O_2(n242_O_2)
  );
  FIFO n249 ( // @[Top.scala 230:22]
    .clock(n249_clock),
    .reset(n249_reset),
    .valid_up(n249_valid_up),
    .valid_down(n249_valid_down),
    .I(n249_I),
    .O(n249_O)
  );
  Map2T_29 n250 ( // @[Top.scala 233:22]
    .valid_up(n250_valid_up),
    .valid_down(n250_valid_down),
    .I0_0(n250_I0_0),
    .I0_1(n250_I0_1),
    .I0_2(n250_I0_2),
    .I1(n250_I1),
    .O_0(n250_O_0),
    .O_1(n250_O_1),
    .O_2(n250_O_2),
    .O_3(n250_O_3)
  );
  MapT_32 n259 ( // @[Top.scala 237:22]
    .clock(n259_clock),
    .reset(n259_reset),
    .valid_up(n259_valid_up),
    .valid_down(n259_valid_down),
    .I_0(n259_I_0),
    .I_1(n259_I_1),
    .I_2(n259_I_2),
    .I_3(n259_I_3),
    .O(n259_O)
  );
  MapT_33 n264 ( // @[Top.scala 240:22]
    .clock(n264_clock),
    .reset(n264_reset),
    .valid_up(n264_valid_up),
    .valid_down(n264_valid_down),
    .I(n264_I),
    .O(n264_O)
  );
  MapT_35 n271 ( // @[Top.scala 243:22]
    .clock(n271_clock),
    .reset(n271_reset),
    .valid_up(n271_valid_up),
    .valid_down(n271_valid_down),
    .I(n271_I),
    .O_t0b(n271_O_t0b),
    .O_t1b(n271_O_t1b)
  );
  MapT_20 n276 ( // @[Top.scala 246:22]
    .valid_up(n276_valid_up),
    .valid_down(n276_valid_down),
    .I_t0b(n276_I_t0b),
    .I_t1b(n276_I_t1b),
    .O(n276_O)
  );
  MapT_13 n279 ( // @[Top.scala 249:22]
    .valid_up(n279_valid_up),
    .valid_down(n279_valid_down),
    .I(n279_I),
    .O(n279_O)
  );
  FIFO_4 n283 ( // @[Top.scala 252:22]
    .clock(n283_clock),
    .reset(n283_reset),
    .valid_up(n283_valid_up),
    .valid_down(n283_valid_down),
    .I(n283_I),
    .O(n283_O)
  );
  MapT_14 n282 ( // @[Top.scala 255:22]
    .clock(n282_clock),
    .reset(n282_reset),
    .valid_up(n282_valid_up),
    .valid_down(n282_valid_down),
    .I(n282_I),
    .O(n282_O)
  );
  Map2T_1 n284 ( // @[Top.scala 258:22]
    .valid_up(n284_valid_up),
    .valid_down(n284_valid_down),
    .I0(n284_I0),
    .I1(n284_I1),
    .O_0(n284_O_0),
    .O_1(n284_O_1)
  );
  MapT_13 n293 ( // @[Top.scala 262:22]
    .valid_up(n293_valid_up),
    .valid_down(n293_valid_down),
    .I(n293_I),
    .O(n293_O)
  );
  FIFO_4 n294 ( // @[Top.scala 265:22]
    .clock(n294_clock),
    .reset(n294_reset),
    .valid_up(n294_valid_up),
    .valid_down(n294_valid_down),
    .I(n294_I),
    .O(n294_O)
  );
  Map2T_3 n295 ( // @[Top.scala 268:22]
    .valid_up(n295_valid_up),
    .valid_down(n295_valid_down),
    .I0_0(n295_I0_0),
    .I0_1(n295_I0_1),
    .I1(n295_I1),
    .O_0(n295_O_0),
    .O_1(n295_O_1),
    .O_2(n295_O_2)
  );
  MapT_14 n304 ( // @[Top.scala 272:22]
    .clock(n304_clock),
    .reset(n304_reset),
    .valid_up(n304_valid_up),
    .valid_down(n304_valid_down),
    .I(n304_I),
    .O(n304_O)
  );
  Map2T_29 n305 ( // @[Top.scala 275:22]
    .valid_up(n305_valid_up),
    .valid_down(n305_valid_down),
    .I0_0(n305_I0_0),
    .I0_1(n305_I0_1),
    .I0_2(n305_I0_2),
    .I1(n305_I1),
    .O_0(n305_O_0),
    .O_1(n305_O_1),
    .O_2(n305_O_2),
    .O_3(n305_O_3)
  );
  MapT_32 n314 ( // @[Top.scala 279:22]
    .clock(n314_clock),
    .reset(n314_reset),
    .valid_up(n314_valid_up),
    .valid_down(n314_valid_down),
    .I_0(n314_I_0),
    .I_1(n314_I_1),
    .I_2(n314_I_2),
    .I_3(n314_I_3),
    .O(n314_O)
  );
  MapT_33 n319 ( // @[Top.scala 282:22]
    .clock(n319_clock),
    .reset(n319_reset),
    .valid_up(n319_valid_up),
    .valid_down(n319_valid_down),
    .I(n319_I),
    .O(n319_O)
  );
  MapT_35 n326 ( // @[Top.scala 285:22]
    .clock(n326_clock),
    .reset(n326_reset),
    .valid_up(n326_valid_up),
    .valid_down(n326_valid_down),
    .I(n326_I),
    .O_t0b(n326_O_t0b),
    .O_t1b(n326_O_t1b)
  );
  MapT_20 n331 ( // @[Top.scala 288:22]
    .valid_up(n331_valid_up),
    .valid_down(n331_valid_down),
    .I_t0b(n331_I_t0b),
    .I_t1b(n331_I_t1b),
    .O(n331_O)
  );
  Map2T_17 n332 ( // @[Top.scala 291:22]
    .valid_up(n332_valid_up),
    .valid_down(n332_valid_down),
    .I0(n332_I0),
    .I1(n332_I1),
    .O_t0b(n332_O_t0b),
    .O_t1b(n332_O_t1b)
  );
  Map2T_23 n340 ( // @[Top.scala 295:22]
    .valid_up(n340_valid_up),
    .valid_down(n340_valid_down),
    .I0(n340_I0),
    .I1_t0b(n340_I1_t0b),
    .I1_t1b(n340_I1_t1b),
    .O_t0b(n340_O_t0b),
    .O_t1b_t0b(n340_O_t1b_t0b),
    .O_t1b_t1b(n340_O_t1b_t1b)
  );
  Map2T_41 n348 ( // @[Top.scala 299:22]
    .valid_up(n348_valid_up),
    .valid_down(n348_valid_down),
    .I0_t0b(n348_I0_t0b),
    .I0_t1b_t0b(n348_I0_t1b_t0b),
    .I0_t1b_t1b(n348_I0_t1b_t1b),
    .I1_t0b(n348_I1_t0b),
    .I1_t1b_t0b(n348_I1_t1b_t0b),
    .I1_t1b_t1b(n348_I1_t1b_t1b),
    .O_t0b_t0b(n348_O_t0b_t0b),
    .O_t0b_t1b_t0b(n348_O_t0b_t1b_t0b),
    .O_t0b_t1b_t1b(n348_O_t0b_t1b_t1b),
    .O_t1b_t0b(n348_O_t1b_t0b),
    .O_t1b_t1b_t0b(n348_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n348_O_t1b_t1b_t1b)
  );
  Map2T_43 n356 ( // @[Top.scala 303:22]
    .valid_up(n356_valid_up),
    .valid_down(n356_valid_down),
    .I0(n356_I0),
    .I1_t0b_t0b(n356_I1_t0b_t0b),
    .I1_t0b_t1b_t0b(n356_I1_t0b_t1b_t0b),
    .I1_t0b_t1b_t1b(n356_I1_t0b_t1b_t1b),
    .I1_t1b_t0b(n356_I1_t1b_t0b),
    .I1_t1b_t1b_t0b(n356_I1_t1b_t1b_t0b),
    .I1_t1b_t1b_t1b(n356_I1_t1b_t1b_t1b),
    .O_t0b(n356_O_t0b),
    .O_t1b_t0b_t0b(n356_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b_t0b(n356_O_t1b_t0b_t1b_t0b),
    .O_t1b_t0b_t1b_t1b(n356_O_t1b_t0b_t1b_t1b),
    .O_t1b_t1b_t0b(n356_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b_t0b(n356_O_t1b_t1b_t1b_t0b),
    .O_t1b_t1b_t1b_t1b(n356_O_t1b_t1b_t1b_t1b)
  );
  MapT_49 n367 ( // @[Top.scala 307:22]
    .valid_up(n367_valid_up),
    .valid_down(n367_valid_down),
    .I_t0b(n367_I_t0b),
    .I_t1b_t0b_t0b(n367_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b_t0b(n367_I_t1b_t0b_t1b_t0b),
    .I_t1b_t0b_t1b_t1b(n367_I_t1b_t0b_t1b_t1b),
    .I_t1b_t1b_t0b(n367_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b_t0b(n367_I_t1b_t1b_t1b_t0b),
    .I_t1b_t1b_t1b_t1b(n367_I_t1b_t1b_t1b_t1b),
    .O_t0b(n367_O_t0b),
    .O_t1b_t0b(n367_O_t1b_t0b),
    .O_t1b_t1b(n367_O_t1b_t1b)
  );
  FIFO_2 n407 ( // @[Top.scala 310:22]
    .clock(n407_clock),
    .reset(n407_reset),
    .valid_up(n407_valid_up),
    .valid_down(n407_valid_down),
    .I(n407_I),
    .O(n407_O)
  );
  FIFO_7 n368 ( // @[Top.scala 313:22]
    .clock(n368_clock),
    .reset(n368_reset),
    .valid_up(n368_valid_up),
    .valid_down(n368_valid_down),
    .I(n368_I),
    .O(n368_O)
  );
  Map2T_17 n369 ( // @[Top.scala 316:22]
    .valid_up(n369_valid_up),
    .valid_down(n369_valid_down),
    .I0(n369_I0),
    .I1(n369_I1),
    .O_t0b(n369_O_t0b),
    .O_t1b(n369_O_t1b)
  );
  Map2T_23 n376 ( // @[Top.scala 320:22]
    .valid_up(n376_valid_up),
    .valid_down(n376_valid_down),
    .I0(n376_I0),
    .I1_t0b(n376_I1_t0b),
    .I1_t1b(n376_I1_t1b),
    .O_t0b(n376_O_t0b),
    .O_t1b_t0b(n376_O_t1b_t0b),
    .O_t1b_t1b(n376_O_t1b_t1b)
  );
  FIFO n391 ( // @[Top.scala 324:22]
    .clock(n391_clock),
    .reset(n391_reset),
    .valid_up(n391_valid_up),
    .valid_down(n391_valid_down),
    .I(n391_I),
    .O(n391_O)
  );
  FIFO n383 ( // @[Top.scala 327:22]
    .clock(n383_clock),
    .reset(n383_reset),
    .valid_up(n383_valid_up),
    .valid_down(n383_valid_down),
    .I(n383_I),
    .O(n383_O)
  );
  Map2T_17 n384 ( // @[Top.scala 330:22]
    .valid_up(n384_valid_up),
    .valid_down(n384_valid_down),
    .I0(n384_I0),
    .I1(n384_I1),
    .O_t0b(n384_O_t0b),
    .O_t1b(n384_O_t1b)
  );
  Map2T_23 n392 ( // @[Top.scala 334:22]
    .valid_up(n392_valid_up),
    .valid_down(n392_valid_down),
    .I0(n392_I0),
    .I1_t0b(n392_I1_t0b),
    .I1_t1b(n392_I1_t1b),
    .O_t0b(n392_O_t0b),
    .O_t1b_t0b(n392_O_t1b_t0b),
    .O_t1b_t1b(n392_O_t1b_t1b)
  );
  FIFO_6 n399 ( // @[Top.scala 338:22]
    .clock(n399_clock),
    .reset(n399_reset),
    .valid_up(n399_valid_up),
    .valid_down(n399_valid_down),
    .I_t0b(n399_I_t0b),
    .I_t1b_t0b(n399_I_t1b_t0b),
    .I_t1b_t1b(n399_I_t1b_t1b),
    .O_t0b(n399_O_t0b),
    .O_t1b_t0b(n399_O_t1b_t0b),
    .O_t1b_t1b(n399_O_t1b_t1b)
  );
  Map2T_41 n400 ( // @[Top.scala 341:22]
    .valid_up(n400_valid_up),
    .valid_down(n400_valid_down),
    .I0_t0b(n400_I0_t0b),
    .I0_t1b_t0b(n400_I0_t1b_t0b),
    .I0_t1b_t1b(n400_I0_t1b_t1b),
    .I1_t0b(n400_I1_t0b),
    .I1_t1b_t0b(n400_I1_t1b_t0b),
    .I1_t1b_t1b(n400_I1_t1b_t1b),
    .O_t0b_t0b(n400_O_t0b_t0b),
    .O_t0b_t1b_t0b(n400_O_t0b_t1b_t0b),
    .O_t0b_t1b_t1b(n400_O_t0b_t1b_t1b),
    .O_t1b_t0b(n400_O_t1b_t0b),
    .O_t1b_t1b_t0b(n400_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n400_O_t1b_t1b_t1b)
  );
  Map2T_43 n408 ( // @[Top.scala 345:22]
    .valid_up(n408_valid_up),
    .valid_down(n408_valid_down),
    .I0(n408_I0),
    .I1_t0b_t0b(n408_I1_t0b_t0b),
    .I1_t0b_t1b_t0b(n408_I1_t0b_t1b_t0b),
    .I1_t0b_t1b_t1b(n408_I1_t0b_t1b_t1b),
    .I1_t1b_t0b(n408_I1_t1b_t0b),
    .I1_t1b_t1b_t0b(n408_I1_t1b_t1b_t0b),
    .I1_t1b_t1b_t1b(n408_I1_t1b_t1b_t1b),
    .O_t0b(n408_O_t0b),
    .O_t1b_t0b_t0b(n408_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b_t0b(n408_O_t1b_t0b_t1b_t0b),
    .O_t1b_t0b_t1b_t1b(n408_O_t1b_t0b_t1b_t1b),
    .O_t1b_t1b_t0b(n408_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b_t0b(n408_O_t1b_t1b_t1b_t0b),
    .O_t1b_t1b_t1b_t1b(n408_O_t1b_t1b_t1b_t1b)
  );
  MapT_49 n419 ( // @[Top.scala 349:22]
    .valid_up(n419_valid_up),
    .valid_down(n419_valid_down),
    .I_t0b(n419_I_t0b),
    .I_t1b_t0b_t0b(n419_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b_t0b(n419_I_t1b_t0b_t1b_t0b),
    .I_t1b_t0b_t1b_t1b(n419_I_t1b_t0b_t1b_t1b),
    .I_t1b_t1b_t0b(n419_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b_t0b(n419_I_t1b_t1b_t1b_t0b),
    .I_t1b_t1b_t1b_t1b(n419_I_t1b_t1b_t1b_t1b),
    .O_t0b(n419_O_t0b),
    .O_t1b_t0b(n419_O_t1b_t0b),
    .O_t1b_t1b(n419_O_t1b_t1b)
  );
  Map2T_41 n420 ( // @[Top.scala 352:22]
    .valid_up(n420_valid_up),
    .valid_down(n420_valid_down),
    .I0_t0b(n420_I0_t0b),
    .I0_t1b_t0b(n420_I0_t1b_t0b),
    .I0_t1b_t1b(n420_I0_t1b_t1b),
    .I1_t0b(n420_I1_t0b),
    .I1_t1b_t0b(n420_I1_t1b_t0b),
    .I1_t1b_t1b(n420_I1_t1b_t1b),
    .O_t0b_t0b(n420_O_t0b_t0b),
    .O_t0b_t1b_t0b(n420_O_t0b_t1b_t0b),
    .O_t0b_t1b_t1b(n420_O_t0b_t1b_t1b),
    .O_t1b_t0b(n420_O_t1b_t0b),
    .O_t1b_t1b_t0b(n420_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b(n420_O_t1b_t1b_t1b)
  );
  Map2T_43 n428 ( // @[Top.scala 356:22]
    .valid_up(n428_valid_up),
    .valid_down(n428_valid_down),
    .I0(n428_I0),
    .I1_t0b_t0b(n428_I1_t0b_t0b),
    .I1_t0b_t1b_t0b(n428_I1_t0b_t1b_t0b),
    .I1_t0b_t1b_t1b(n428_I1_t0b_t1b_t1b),
    .I1_t1b_t0b(n428_I1_t1b_t0b),
    .I1_t1b_t1b_t0b(n428_I1_t1b_t1b_t0b),
    .I1_t1b_t1b_t1b(n428_I1_t1b_t1b_t1b),
    .O_t0b(n428_O_t0b),
    .O_t1b_t0b_t0b(n428_O_t1b_t0b_t0b),
    .O_t1b_t0b_t1b_t0b(n428_O_t1b_t0b_t1b_t0b),
    .O_t1b_t0b_t1b_t1b(n428_O_t1b_t0b_t1b_t1b),
    .O_t1b_t1b_t0b(n428_O_t1b_t1b_t0b),
    .O_t1b_t1b_t1b_t0b(n428_O_t1b_t1b_t1b_t0b),
    .O_t1b_t1b_t1b_t1b(n428_O_t1b_t1b_t1b_t1b)
  );
  MapT_49 n439 ( // @[Top.scala 360:22]
    .valid_up(n439_valid_up),
    .valid_down(n439_valid_down),
    .I_t0b(n439_I_t0b),
    .I_t1b_t0b_t0b(n439_I_t1b_t0b_t0b),
    .I_t1b_t0b_t1b_t0b(n439_I_t1b_t0b_t1b_t0b),
    .I_t1b_t0b_t1b_t1b(n439_I_t1b_t0b_t1b_t1b),
    .I_t1b_t1b_t0b(n439_I_t1b_t1b_t0b),
    .I_t1b_t1b_t1b_t0b(n439_I_t1b_t1b_t1b_t0b),
    .I_t1b_t1b_t1b_t1b(n439_I_t1b_t1b_t1b_t1b),
    .O_t0b(n439_O_t0b),
    .O_t1b_t0b(n439_O_t1b_t0b),
    .O_t1b_t1b(n439_O_t1b_t1b)
  );
  assign valid_down = n439_valid_down; // @[Top.scala 364:16]
  assign O_t0b = n439_O_t0b; // @[Top.scala 363:7]
  assign O_t1b_t0b = n439_O_t1b_t0b; // @[Top.scala 363:7]
  assign O_t1b_t1b = n439_O_t1b_t1b; // @[Top.scala 363:7]
  assign n132_valid_up = valid_up; // @[Top.scala 118:19]
  assign n132_I_t0b = I1_t0b; // @[Top.scala 117:12]
  assign n427_clock = clock;
  assign n427_reset = reset;
  assign n427_valid_up = n132_valid_down; // @[Top.scala 121:19]
  assign n427_I = n132_O; // @[Top.scala 120:12]
  assign n137_valid_up = valid_up; // @[Top.scala 124:19]
  assign n137_I_t1b = I1_t1b; // @[Top.scala 123:12]
  assign n355_clock = clock;
  assign n355_reset = reset;
  assign n355_valid_up = n137_valid_down; // @[Top.scala 127:19]
  assign n355_I = n137_O; // @[Top.scala 126:12]
  assign n139_valid_up = valid_up; // @[Top.scala 130:19]
  assign n139_I_0 = I0_0; // @[Top.scala 129:12]
  assign n139_I_1 = I0_1; // @[Top.scala 129:12]
  assign n139_I_2 = I0_2; // @[Top.scala 129:12]
  assign n142_valid_up = n139_valid_down; // @[Top.scala 133:19]
  assign n142_I_1 = n139_O_1; // @[Top.scala 132:12]
  assign n143_valid_up = n142_valid_down; // @[Top.scala 136:19]
  assign n143_I_0 = n142_O_0; // @[Top.scala 135:12]
  assign n146_valid_up = n143_valid_down; // @[Top.scala 139:19]
  assign n146_I = n143_O; // @[Top.scala 138:12]
  assign n150_clock = clock;
  assign n150_reset = reset;
  assign n150_valid_up = n146_valid_down; // @[Top.scala 142:19]
  assign n150_I = n146_O; // @[Top.scala 141:12]
  assign n149_clock = clock;
  assign n149_reset = reset;
  assign n149_valid_up = n143_valid_down; // @[Top.scala 145:19]
  assign n149_I = n143_O; // @[Top.scala 144:12]
  assign n151_valid_up = n150_valid_down & n149_valid_down; // @[Top.scala 149:19]
  assign n151_I0 = n150_O; // @[Top.scala 147:13]
  assign n151_I1 = n149_O; // @[Top.scala 148:13]
  assign n162_valid_up = n151_valid_down; // @[Top.scala 152:19]
  assign n162_I_t0b = n151_O_t0b; // @[Top.scala 151:12]
  assign n162_I_t1b = n151_O_t1b; // @[Top.scala 151:12]
  assign n169_clock = clock;
  assign n169_reset = reset;
  assign n169_valid_up = n162_valid_down; // @[Top.scala 155:19]
  assign n169_I = n162_O; // @[Top.scala 154:12]
  assign n174_valid_up = n169_valid_down; // @[Top.scala 158:19]
  assign n174_I_t0b = n169_O_t0b; // @[Top.scala 157:12]
  assign n174_I_t1b = n169_O_t1b; // @[Top.scala 157:12]
  assign n177_clock = clock;
  assign n177_reset = reset;
  assign n177_valid_up = n143_valid_down; // @[Top.scala 161:19]
  assign n177_I = n143_O; // @[Top.scala 160:12]
  assign n178_valid_up = valid_up; // @[Top.scala 164:19]
  assign n178_I_0 = I0_0; // @[Top.scala 163:12]
  assign n178_I_1 = I0_1; // @[Top.scala 163:12]
  assign n178_I_2 = I0_2; // @[Top.scala 163:12]
  assign n181_valid_up = n178_valid_down; // @[Top.scala 167:19]
  assign n181_I_0 = n178_O_0; // @[Top.scala 166:12]
  assign n182_valid_up = n181_valid_down; // @[Top.scala 170:19]
  assign n182_I_0 = n181_O_0; // @[Top.scala 169:12]
  assign n185_clock = clock;
  assign n185_reset = reset;
  assign n185_valid_up = n182_valid_down; // @[Top.scala 173:19]
  assign n185_I = n182_O; // @[Top.scala 172:12]
  assign n186_valid_up = valid_up; // @[Top.scala 176:19]
  assign n186_I_0 = I0_0; // @[Top.scala 175:12]
  assign n186_I_1 = I0_1; // @[Top.scala 175:12]
  assign n186_I_2 = I0_2; // @[Top.scala 175:12]
  assign n189_valid_up = n186_valid_down; // @[Top.scala 179:19]
  assign n189_I_2 = n186_O_2; // @[Top.scala 178:12]
  assign n190_valid_up = n189_valid_down; // @[Top.scala 182:19]
  assign n190_I_0 = n189_O_0; // @[Top.scala 181:12]
  assign n193_clock = clock;
  assign n193_reset = reset;
  assign n193_valid_up = n190_valid_down; // @[Top.scala 185:19]
  assign n193_I = n190_O; // @[Top.scala 184:12]
  assign n194_valid_up = n185_valid_down & n193_valid_down; // @[Top.scala 189:19]
  assign n194_I0 = n185_O; // @[Top.scala 187:13]
  assign n194_I1 = n193_O; // @[Top.scala 188:13]
  assign n205_valid_up = n194_valid_down; // @[Top.scala 192:19]
  assign n205_I_t0b = n194_O_t0b; // @[Top.scala 191:12]
  assign n205_I_t1b = n194_O_t1b; // @[Top.scala 191:12]
  assign n212_clock = clock;
  assign n212_reset = reset;
  assign n212_valid_up = n205_valid_down; // @[Top.scala 195:19]
  assign n212_I = n205_O; // @[Top.scala 194:12]
  assign n217_valid_up = n212_valid_down; // @[Top.scala 198:19]
  assign n217_I_t0b = n212_O_t0b; // @[Top.scala 197:12]
  assign n217_I_t1b = n212_O_t1b; // @[Top.scala 197:12]
  assign n218_valid_up = n177_valid_down & n217_valid_down; // @[Top.scala 202:19]
  assign n218_I0 = n177_O; // @[Top.scala 200:13]
  assign n218_I1 = n217_O; // @[Top.scala 201:13]
  assign n225_clock = clock;
  assign n225_reset = reset;
  assign n225_valid_up = n218_valid_down; // @[Top.scala 205:19]
  assign n225_I_t0b = n218_O_t0b; // @[Top.scala 204:12]
  assign n225_I_t1b = n218_O_t1b; // @[Top.scala 204:12]
  assign n226_valid_up = n174_valid_down & n225_valid_down; // @[Top.scala 209:19]
  assign n226_I0 = n174_O; // @[Top.scala 207:13]
  assign n226_I1_t0b = n225_O_t0b; // @[Top.scala 208:13]
  assign n226_I1_t1b = n225_O_t1b; // @[Top.scala 208:13]
  assign n347_clock = clock;
  assign n347_reset = reset;
  assign n347_valid_up = n226_valid_down; // @[Top.scala 212:19]
  assign n347_I_t0b = n226_O_t0b; // @[Top.scala 211:12]
  assign n347_I_t1b_t0b = n226_O_t1b_t0b; // @[Top.scala 211:12]
  assign n347_I_t1b_t1b = n226_O_t1b_t1b; // @[Top.scala 211:12]
  assign n339_clock = clock;
  assign n339_reset = reset;
  assign n339_valid_up = n177_valid_down; // @[Top.scala 215:19]
  assign n339_I = n177_O; // @[Top.scala 214:12]
  assign n233_clock = clock;
  assign n233_reset = reset;
  assign n233_valid_up = n146_valid_down; // @[Top.scala 218:19]
  assign n233_I = n146_O; // @[Top.scala 217:12]
  assign n234_valid_up = n233_valid_down & n185_valid_down; // @[Top.scala 222:19]
  assign n234_I0 = n233_O; // @[Top.scala 220:13]
  assign n234_I1 = n185_O; // @[Top.scala 221:13]
  assign n241_clock = clock;
  assign n241_reset = reset;
  assign n241_valid_up = n234_valid_down; // @[Top.scala 225:19]
  assign n241_I_0 = n234_O_0; // @[Top.scala 224:12]
  assign n241_I_1 = n234_O_1; // @[Top.scala 224:12]
  assign n242_valid_up = n241_valid_down & n149_valid_down; // @[Top.scala 229:19]
  assign n242_I0_0 = n241_O_0; // @[Top.scala 227:13]
  assign n242_I0_1 = n241_O_1; // @[Top.scala 227:13]
  assign n242_I1 = n149_O; // @[Top.scala 228:13]
  assign n249_clock = clock;
  assign n249_reset = reset;
  assign n249_valid_up = n193_valid_down; // @[Top.scala 232:19]
  assign n249_I = n193_O; // @[Top.scala 231:12]
  assign n250_valid_up = n242_valid_down & n249_valid_down; // @[Top.scala 236:19]
  assign n250_I0_0 = n242_O_0; // @[Top.scala 234:13]
  assign n250_I0_1 = n242_O_1; // @[Top.scala 234:13]
  assign n250_I0_2 = n242_O_2; // @[Top.scala 234:13]
  assign n250_I1 = n249_O; // @[Top.scala 235:13]
  assign n259_clock = clock;
  assign n259_reset = reset;
  assign n259_valid_up = n250_valid_down; // @[Top.scala 239:19]
  assign n259_I_0 = n250_O_0; // @[Top.scala 238:12]
  assign n259_I_1 = n250_O_1; // @[Top.scala 238:12]
  assign n259_I_2 = n250_O_2; // @[Top.scala 238:12]
  assign n259_I_3 = n250_O_3; // @[Top.scala 238:12]
  assign n264_clock = clock;
  assign n264_reset = reset;
  assign n264_valid_up = n259_valid_down; // @[Top.scala 242:19]
  assign n264_I = n259_O; // @[Top.scala 241:12]
  assign n271_clock = clock;
  assign n271_reset = reset;
  assign n271_valid_up = n264_valid_down; // @[Top.scala 245:19]
  assign n271_I = n264_O; // @[Top.scala 244:12]
  assign n276_valid_up = n271_valid_down; // @[Top.scala 248:19]
  assign n276_I_t0b = n271_O_t0b; // @[Top.scala 247:12]
  assign n276_I_t1b = n271_O_t1b; // @[Top.scala 247:12]
  assign n279_valid_up = n182_valid_down; // @[Top.scala 251:19]
  assign n279_I = n182_O; // @[Top.scala 250:12]
  assign n283_clock = clock;
  assign n283_reset = reset;
  assign n283_valid_up = n279_valid_down; // @[Top.scala 254:19]
  assign n283_I = n279_O; // @[Top.scala 253:12]
  assign n282_clock = clock;
  assign n282_reset = reset;
  assign n282_valid_up = n182_valid_down; // @[Top.scala 257:19]
  assign n282_I = n182_O; // @[Top.scala 256:12]
  assign n284_valid_up = n283_valid_down & n282_valid_down; // @[Top.scala 261:19]
  assign n284_I0 = n283_O; // @[Top.scala 259:13]
  assign n284_I1 = n282_O; // @[Top.scala 260:13]
  assign n293_valid_up = n190_valid_down; // @[Top.scala 264:19]
  assign n293_I = n190_O; // @[Top.scala 263:12]
  assign n294_clock = clock;
  assign n294_reset = reset;
  assign n294_valid_up = n293_valid_down; // @[Top.scala 267:19]
  assign n294_I = n293_O; // @[Top.scala 266:12]
  assign n295_valid_up = n284_valid_down & n294_valid_down; // @[Top.scala 271:19]
  assign n295_I0_0 = n284_O_0; // @[Top.scala 269:13]
  assign n295_I0_1 = n284_O_1; // @[Top.scala 269:13]
  assign n295_I1 = n294_O; // @[Top.scala 270:13]
  assign n304_clock = clock;
  assign n304_reset = reset;
  assign n304_valid_up = n190_valid_down; // @[Top.scala 274:19]
  assign n304_I = n190_O; // @[Top.scala 273:12]
  assign n305_valid_up = n295_valid_down & n304_valid_down; // @[Top.scala 278:19]
  assign n305_I0_0 = n295_O_0; // @[Top.scala 276:13]
  assign n305_I0_1 = n295_O_1; // @[Top.scala 276:13]
  assign n305_I0_2 = n295_O_2; // @[Top.scala 276:13]
  assign n305_I1 = n304_O; // @[Top.scala 277:13]
  assign n314_clock = clock;
  assign n314_reset = reset;
  assign n314_valid_up = n305_valid_down; // @[Top.scala 281:19]
  assign n314_I_0 = n305_O_0; // @[Top.scala 280:12]
  assign n314_I_1 = n305_O_1; // @[Top.scala 280:12]
  assign n314_I_2 = n305_O_2; // @[Top.scala 280:12]
  assign n314_I_3 = n305_O_3; // @[Top.scala 280:12]
  assign n319_clock = clock;
  assign n319_reset = reset;
  assign n319_valid_up = n314_valid_down; // @[Top.scala 284:19]
  assign n319_I = n314_O; // @[Top.scala 283:12]
  assign n326_clock = clock;
  assign n326_reset = reset;
  assign n326_valid_up = n319_valid_down; // @[Top.scala 287:19]
  assign n326_I = n319_O; // @[Top.scala 286:12]
  assign n331_valid_up = n326_valid_down; // @[Top.scala 290:19]
  assign n331_I_t0b = n326_O_t0b; // @[Top.scala 289:12]
  assign n331_I_t1b = n326_O_t1b; // @[Top.scala 289:12]
  assign n332_valid_up = n276_valid_down & n331_valid_down; // @[Top.scala 294:19]
  assign n332_I0 = n276_O; // @[Top.scala 292:13]
  assign n332_I1 = n331_O; // @[Top.scala 293:13]
  assign n340_valid_up = n339_valid_down & n332_valid_down; // @[Top.scala 298:19]
  assign n340_I0 = n339_O; // @[Top.scala 296:13]
  assign n340_I1_t0b = n332_O_t0b; // @[Top.scala 297:13]
  assign n340_I1_t1b = n332_O_t1b; // @[Top.scala 297:13]
  assign n348_valid_up = n347_valid_down & n340_valid_down; // @[Top.scala 302:19]
  assign n348_I0_t0b = n347_O_t0b; // @[Top.scala 300:13]
  assign n348_I0_t1b_t0b = n347_O_t1b_t0b; // @[Top.scala 300:13]
  assign n348_I0_t1b_t1b = n347_O_t1b_t1b; // @[Top.scala 300:13]
  assign n348_I1_t0b = n340_O_t0b; // @[Top.scala 301:13]
  assign n348_I1_t1b_t0b = n340_O_t1b_t0b; // @[Top.scala 301:13]
  assign n348_I1_t1b_t1b = n340_O_t1b_t1b; // @[Top.scala 301:13]
  assign n356_valid_up = n355_valid_down & n348_valid_down; // @[Top.scala 306:19]
  assign n356_I0 = n355_O; // @[Top.scala 304:13]
  assign n356_I1_t0b_t0b = n348_O_t0b_t0b; // @[Top.scala 305:13]
  assign n356_I1_t0b_t1b_t0b = n348_O_t0b_t1b_t0b; // @[Top.scala 305:13]
  assign n356_I1_t0b_t1b_t1b = n348_O_t0b_t1b_t1b; // @[Top.scala 305:13]
  assign n356_I1_t1b_t0b = n348_O_t1b_t0b; // @[Top.scala 305:13]
  assign n356_I1_t1b_t1b_t0b = n348_O_t1b_t1b_t0b; // @[Top.scala 305:13]
  assign n356_I1_t1b_t1b_t1b = n348_O_t1b_t1b_t1b; // @[Top.scala 305:13]
  assign n367_valid_up = n356_valid_down; // @[Top.scala 309:19]
  assign n367_I_t0b = n356_O_t0b; // @[Top.scala 308:12]
  assign n367_I_t1b_t0b_t0b = n356_O_t1b_t0b_t0b; // @[Top.scala 308:12]
  assign n367_I_t1b_t0b_t1b_t0b = n356_O_t1b_t0b_t1b_t0b; // @[Top.scala 308:12]
  assign n367_I_t1b_t0b_t1b_t1b = n356_O_t1b_t0b_t1b_t1b; // @[Top.scala 308:12]
  assign n367_I_t1b_t1b_t0b = n356_O_t1b_t1b_t0b; // @[Top.scala 308:12]
  assign n367_I_t1b_t1b_t1b_t0b = n356_O_t1b_t1b_t1b_t0b; // @[Top.scala 308:12]
  assign n367_I_t1b_t1b_t1b_t1b = n356_O_t1b_t1b_t1b_t1b; // @[Top.scala 308:12]
  assign n407_clock = clock;
  assign n407_reset = reset;
  assign n407_valid_up = n137_valid_down; // @[Top.scala 312:19]
  assign n407_I = n137_O; // @[Top.scala 311:12]
  assign n368_clock = clock;
  assign n368_reset = reset;
  assign n368_valid_up = n177_valid_down; // @[Top.scala 315:19]
  assign n368_I = n177_O; // @[Top.scala 314:12]
  assign n369_valid_up = n276_valid_down & n368_valid_down; // @[Top.scala 319:19]
  assign n369_I0 = n276_O; // @[Top.scala 317:13]
  assign n369_I1 = n368_O; // @[Top.scala 318:13]
  assign n376_valid_up = n331_valid_down & n369_valid_down; // @[Top.scala 323:19]
  assign n376_I0 = n331_O; // @[Top.scala 321:13]
  assign n376_I1_t0b = n369_O_t0b; // @[Top.scala 322:13]
  assign n376_I1_t1b = n369_O_t1b; // @[Top.scala 322:13]
  assign n391_clock = clock;
  assign n391_reset = reset;
  assign n391_valid_up = n217_valid_down; // @[Top.scala 326:19]
  assign n391_I = n217_O; // @[Top.scala 325:12]
  assign n383_clock = clock;
  assign n383_reset = reset;
  assign n383_valid_up = n177_valid_down; // @[Top.scala 329:19]
  assign n383_I = n177_O; // @[Top.scala 328:12]
  assign n384_valid_up = n383_valid_down & n174_valid_down; // @[Top.scala 333:19]
  assign n384_I0 = n383_O; // @[Top.scala 331:13]
  assign n384_I1 = n174_O; // @[Top.scala 332:13]
  assign n392_valid_up = n391_valid_down & n384_valid_down; // @[Top.scala 337:19]
  assign n392_I0 = n391_O; // @[Top.scala 335:13]
  assign n392_I1_t0b = n384_O_t0b; // @[Top.scala 336:13]
  assign n392_I1_t1b = n384_O_t1b; // @[Top.scala 336:13]
  assign n399_clock = clock;
  assign n399_reset = reset;
  assign n399_valid_up = n392_valid_down; // @[Top.scala 340:19]
  assign n399_I_t0b = n392_O_t0b; // @[Top.scala 339:12]
  assign n399_I_t1b_t0b = n392_O_t1b_t0b; // @[Top.scala 339:12]
  assign n399_I_t1b_t1b = n392_O_t1b_t1b; // @[Top.scala 339:12]
  assign n400_valid_up = n376_valid_down & n399_valid_down; // @[Top.scala 344:19]
  assign n400_I0_t0b = n376_O_t0b; // @[Top.scala 342:13]
  assign n400_I0_t1b_t0b = n376_O_t1b_t0b; // @[Top.scala 342:13]
  assign n400_I0_t1b_t1b = n376_O_t1b_t1b; // @[Top.scala 342:13]
  assign n400_I1_t0b = n399_O_t0b; // @[Top.scala 343:13]
  assign n400_I1_t1b_t0b = n399_O_t1b_t0b; // @[Top.scala 343:13]
  assign n400_I1_t1b_t1b = n399_O_t1b_t1b; // @[Top.scala 343:13]
  assign n408_valid_up = n407_valid_down & n400_valid_down; // @[Top.scala 348:19]
  assign n408_I0 = n407_O; // @[Top.scala 346:13]
  assign n408_I1_t0b_t0b = n400_O_t0b_t0b; // @[Top.scala 347:13]
  assign n408_I1_t0b_t1b_t0b = n400_O_t0b_t1b_t0b; // @[Top.scala 347:13]
  assign n408_I1_t0b_t1b_t1b = n400_O_t0b_t1b_t1b; // @[Top.scala 347:13]
  assign n408_I1_t1b_t0b = n400_O_t1b_t0b; // @[Top.scala 347:13]
  assign n408_I1_t1b_t1b_t0b = n400_O_t1b_t1b_t0b; // @[Top.scala 347:13]
  assign n408_I1_t1b_t1b_t1b = n400_O_t1b_t1b_t1b; // @[Top.scala 347:13]
  assign n419_valid_up = n408_valid_down; // @[Top.scala 351:19]
  assign n419_I_t0b = n408_O_t0b; // @[Top.scala 350:12]
  assign n419_I_t1b_t0b_t0b = n408_O_t1b_t0b_t0b; // @[Top.scala 350:12]
  assign n419_I_t1b_t0b_t1b_t0b = n408_O_t1b_t0b_t1b_t0b; // @[Top.scala 350:12]
  assign n419_I_t1b_t0b_t1b_t1b = n408_O_t1b_t0b_t1b_t1b; // @[Top.scala 350:12]
  assign n419_I_t1b_t1b_t0b = n408_O_t1b_t1b_t0b; // @[Top.scala 350:12]
  assign n419_I_t1b_t1b_t1b_t0b = n408_O_t1b_t1b_t1b_t0b; // @[Top.scala 350:12]
  assign n419_I_t1b_t1b_t1b_t1b = n408_O_t1b_t1b_t1b_t1b; // @[Top.scala 350:12]
  assign n420_valid_up = n367_valid_down & n419_valid_down; // @[Top.scala 355:19]
  assign n420_I0_t0b = n367_O_t0b; // @[Top.scala 353:13]
  assign n420_I0_t1b_t0b = n367_O_t1b_t0b; // @[Top.scala 353:13]
  assign n420_I0_t1b_t1b = n367_O_t1b_t1b; // @[Top.scala 353:13]
  assign n420_I1_t0b = n419_O_t0b; // @[Top.scala 354:13]
  assign n420_I1_t1b_t0b = n419_O_t1b_t0b; // @[Top.scala 354:13]
  assign n420_I1_t1b_t1b = n419_O_t1b_t1b; // @[Top.scala 354:13]
  assign n428_valid_up = n427_valid_down & n420_valid_down; // @[Top.scala 359:19]
  assign n428_I0 = n427_O; // @[Top.scala 357:13]
  assign n428_I1_t0b_t0b = n420_O_t0b_t0b; // @[Top.scala 358:13]
  assign n428_I1_t0b_t1b_t0b = n420_O_t0b_t1b_t0b; // @[Top.scala 358:13]
  assign n428_I1_t0b_t1b_t1b = n420_O_t0b_t1b_t1b; // @[Top.scala 358:13]
  assign n428_I1_t1b_t0b = n420_O_t1b_t0b; // @[Top.scala 358:13]
  assign n428_I1_t1b_t1b_t0b = n420_O_t1b_t1b_t0b; // @[Top.scala 358:13]
  assign n428_I1_t1b_t1b_t1b = n420_O_t1b_t1b_t1b; // @[Top.scala 358:13]
  assign n439_valid_up = n428_valid_down; // @[Top.scala 362:19]
  assign n439_I_t0b = n428_O_t0b; // @[Top.scala 361:12]
  assign n439_I_t1b_t0b_t0b = n428_O_t1b_t0b_t0b; // @[Top.scala 361:12]
  assign n439_I_t1b_t0b_t1b_t0b = n428_O_t1b_t0b_t1b_t0b; // @[Top.scala 361:12]
  assign n439_I_t1b_t0b_t1b_t1b = n428_O_t1b_t0b_t1b_t1b; // @[Top.scala 361:12]
  assign n439_I_t1b_t1b_t0b = n428_O_t1b_t1b_t0b; // @[Top.scala 361:12]
  assign n439_I_t1b_t1b_t1b_t0b = n428_O_t1b_t1b_t1b_t0b; // @[Top.scala 361:12]
  assign n439_I_t1b_t1b_t1b_t1b = n428_O_t1b_t1b_t1b_t1b; // @[Top.scala 361:12]
endmodule
module Map2S_2(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_0_0,
  input  [31:0] I0_0_1,
  input  [31:0] I0_0_2,
  input         I1_0_t0b,
  input         I1_0_t1b,
  output [31:0] O_0_t0b,
  output [31:0] O_0_t1b_t0b,
  output [31:0] O_0_t1b_t1b
);
  wire  fst_op_clock; // @[Map2S.scala 9:22]
  wire  fst_op_reset; // @[Map2S.scala 9:22]
  wire  fst_op_valid_up; // @[Map2S.scala 9:22]
  wire  fst_op_valid_down; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_I0_0; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_I0_1; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_I0_2; // @[Map2S.scala 9:22]
  wire  fst_op_I1_t0b; // @[Map2S.scala 9:22]
  wire  fst_op_I1_t1b; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_O_t0b; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_O_t1b_t0b; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_O_t1b_t1b; // @[Map2S.scala 9:22]
  Module_6 fst_op ( // @[Map2S.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I0_0(fst_op_I0_0),
    .I0_1(fst_op_I0_1),
    .I0_2(fst_op_I0_2),
    .I1_t0b(fst_op_I1_t0b),
    .I1_t1b(fst_op_I1_t1b),
    .O_t0b(fst_op_O_t0b),
    .O_t1b_t0b(fst_op_O_t1b_t0b),
    .O_t1b_t1b(fst_op_O_t1b_t1b)
  );
  assign valid_down = fst_op_valid_down; // @[Map2S.scala 26:14]
  assign O_0_t0b = fst_op_O_t0b; // @[Map2S.scala 19:8]
  assign O_0_t1b_t0b = fst_op_O_t1b_t0b; // @[Map2S.scala 19:8]
  assign O_0_t1b_t1b = fst_op_O_t1b_t1b; // @[Map2S.scala 19:8]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[Map2S.scala 16:19]
  assign fst_op_I0_0 = I0_0_0; // @[Map2S.scala 17:13]
  assign fst_op_I0_1 = I0_0_1; // @[Map2S.scala 17:13]
  assign fst_op_I0_2 = I0_0_2; // @[Map2S.scala 17:13]
  assign fst_op_I1_t0b = I1_0_t0b; // @[Map2S.scala 18:13]
  assign fst_op_I1_t1b = I1_0_t1b; // @[Map2S.scala 18:13]
endmodule
module Map2T_60(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_0_0,
  input  [31:0] I0_0_1,
  input  [31:0] I0_0_2,
  input         I1_0_t0b,
  input         I1_0_t1b,
  output [31:0] O_0_t0b,
  output [31:0] O_0_t1b_t0b,
  output [31:0] O_0_t1b_t1b
);
  wire  op_clock; // @[Map2T.scala 8:20]
  wire  op_reset; // @[Map2T.scala 8:20]
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_0_0; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_0_1; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_0_2; // @[Map2T.scala 8:20]
  wire  op_I1_0_t0b; // @[Map2T.scala 8:20]
  wire  op_I1_0_t1b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0_t1b_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0_t1b_t1b; // @[Map2T.scala 8:20]
  Map2S_2 op ( // @[Map2T.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0_0(op_I0_0_0),
    .I0_0_1(op_I0_0_1),
    .I0_0_2(op_I0_0_2),
    .I1_0_t0b(op_I1_0_t0b),
    .I1_0_t1b(op_I1_0_t1b),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0_t0b = op_O_0_t0b; // @[Map2T.scala 17:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[Map2T.scala 17:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[Map2T.scala 17:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0_0 = I0_0_0; // @[Map2T.scala 15:11]
  assign op_I0_0_1 = I0_0_1; // @[Map2T.scala 15:11]
  assign op_I0_0_2 = I0_0_2; // @[Map2T.scala 15:11]
  assign op_I1_0_t0b = I1_0_t0b; // @[Map2T.scala 16:11]
  assign op_I1_0_t1b = I1_0_t1b; // @[Map2T.scala 16:11]
endmodule
module Module_7(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0_0,
  input  [31:0] I_0_1,
  input  [31:0] I_0_2,
  output [31:0] O_0_t0b,
  output [31:0] O_0_t1b_t0b,
  output [31:0] O_0_t1b_t1b
);
  wire  counter92_clock; // @[Top.scala 370:27]
  wire  counter92_reset; // @[Top.scala 370:27]
  wire [31:0] counter92_O; // @[Top.scala 370:27]
  wire  n100_valid_down; // @[Top.scala 372:22]
  wire [31:0] n100_I; // @[Top.scala 372:22]
  wire  n100_O; // @[Top.scala 372:22]
  wire  n112_valid_down; // @[Top.scala 375:22]
  wire [31:0] n112_I; // @[Top.scala 375:22]
  wire  n112_O; // @[Top.scala 375:22]
  wire  n113_valid_up; // @[Top.scala 378:22]
  wire  n113_valid_down; // @[Top.scala 378:22]
  wire  n113_I0; // @[Top.scala 378:22]
  wire  n113_I1; // @[Top.scala 378:22]
  wire  n113_O_t0b; // @[Top.scala 378:22]
  wire  n113_O_t1b; // @[Top.scala 378:22]
  wire  n120_valid_up; // @[Top.scala 382:22]
  wire  n120_valid_down; // @[Top.scala 382:22]
  wire  n120_I_t0b; // @[Top.scala 382:22]
  wire  n120_I_t1b; // @[Top.scala 382:22]
  wire  n120_O_t0b; // @[Top.scala 382:22]
  wire  n120_O_t1b; // @[Top.scala 382:22]
  wire  n121_valid_up; // @[Top.scala 385:22]
  wire  n121_valid_down; // @[Top.scala 385:22]
  wire  n121_I_t0b; // @[Top.scala 385:22]
  wire  n121_I_t1b; // @[Top.scala 385:22]
  wire  n121_O_0_t0b; // @[Top.scala 385:22]
  wire  n121_O_0_t1b; // @[Top.scala 385:22]
  wire  n122_clock; // @[Top.scala 388:22]
  wire  n122_reset; // @[Top.scala 388:22]
  wire  n122_valid_up; // @[Top.scala 388:22]
  wire  n122_valid_down; // @[Top.scala 388:22]
  wire  n122_I_0_t0b; // @[Top.scala 388:22]
  wire  n122_I_0_t1b; // @[Top.scala 388:22]
  wire  n122_O_0_t0b; // @[Top.scala 388:22]
  wire  n122_O_0_t1b; // @[Top.scala 388:22]
  wire  n123_clock; // @[Top.scala 391:22]
  wire  n123_reset; // @[Top.scala 391:22]
  wire  n123_valid_up; // @[Top.scala 391:22]
  wire  n123_valid_down; // @[Top.scala 391:22]
  wire [31:0] n123_I0_0_0; // @[Top.scala 391:22]
  wire [31:0] n123_I0_0_1; // @[Top.scala 391:22]
  wire [31:0] n123_I0_0_2; // @[Top.scala 391:22]
  wire  n123_I1_0_t0b; // @[Top.scala 391:22]
  wire  n123_I1_0_t1b; // @[Top.scala 391:22]
  wire [31:0] n123_O_0_t0b; // @[Top.scala 391:22]
  wire [31:0] n123_O_0_t1b_t0b; // @[Top.scala 391:22]
  wire [31:0] n123_O_0_t1b_t1b; // @[Top.scala 391:22]
  Counter_TN counter92 ( // @[Top.scala 370:27]
    .clock(counter92_clock),
    .reset(counter92_reset),
    .O(counter92_O)
  );
  MapT_5 n100 ( // @[Top.scala 372:22]
    .valid_down(n100_valid_down),
    .I(n100_I),
    .O(n100_O)
  );
  MapT_7 n112 ( // @[Top.scala 375:22]
    .valid_down(n112_valid_down),
    .I(n112_I),
    .O(n112_O)
  );
  Map2T_15 n113 ( // @[Top.scala 378:22]
    .valid_up(n113_valid_up),
    .valid_down(n113_valid_down),
    .I0(n113_I0),
    .I1(n113_I1),
    .O_t0b(n113_O_t0b),
    .O_t1b(n113_O_t1b)
  );
  Passthrough_6 n120 ( // @[Top.scala 382:22]
    .valid_up(n120_valid_up),
    .valid_down(n120_valid_down),
    .I_t0b(n120_I_t0b),
    .I_t1b(n120_I_t1b),
    .O_t0b(n120_O_t0b),
    .O_t1b(n120_O_t1b)
  );
  Passthrough_7 n121 ( // @[Top.scala 385:22]
    .valid_up(n121_valid_up),
    .valid_down(n121_valid_down),
    .I_t0b(n121_I_t0b),
    .I_t1b(n121_I_t1b),
    .O_0_t0b(n121_O_0_t0b),
    .O_0_t1b(n121_O_0_t1b)
  );
  FIFO_1 n122 ( // @[Top.scala 388:22]
    .clock(n122_clock),
    .reset(n122_reset),
    .valid_up(n122_valid_up),
    .valid_down(n122_valid_down),
    .I_0_t0b(n122_I_0_t0b),
    .I_0_t1b(n122_I_0_t1b),
    .O_0_t0b(n122_O_0_t0b),
    .O_0_t1b(n122_O_0_t1b)
  );
  Map2T_60 n123 ( // @[Top.scala 391:22]
    .clock(n123_clock),
    .reset(n123_reset),
    .valid_up(n123_valid_up),
    .valid_down(n123_valid_down),
    .I0_0_0(n123_I0_0_0),
    .I0_0_1(n123_I0_0_1),
    .I0_0_2(n123_I0_0_2),
    .I1_0_t0b(n123_I1_0_t0b),
    .I1_0_t1b(n123_I1_0_t1b),
    .O_0_t0b(n123_O_0_t0b),
    .O_0_t1b_t0b(n123_O_0_t1b_t0b),
    .O_0_t1b_t1b(n123_O_0_t1b_t1b)
  );
  assign valid_down = n123_valid_down; // @[Top.scala 396:16]
  assign O_0_t0b = n123_O_0_t0b; // @[Top.scala 395:7]
  assign O_0_t1b_t0b = n123_O_0_t1b_t0b; // @[Top.scala 395:7]
  assign O_0_t1b_t1b = n123_O_0_t1b_t1b; // @[Top.scala 395:7]
  assign counter92_clock = clock;
  assign counter92_reset = reset;
  assign n100_I = counter92_O; // @[Top.scala 373:12]
  assign n112_I = counter92_O; // @[Top.scala 376:12]
  assign n113_valid_up = n100_valid_down & n112_valid_down; // @[Top.scala 381:19]
  assign n113_I0 = n100_O; // @[Top.scala 379:13]
  assign n113_I1 = n112_O; // @[Top.scala 380:13]
  assign n120_valid_up = n113_valid_down; // @[Top.scala 384:19]
  assign n120_I_t0b = n113_O_t0b; // @[Top.scala 383:12]
  assign n120_I_t1b = n113_O_t1b; // @[Top.scala 383:12]
  assign n121_valid_up = n120_valid_down; // @[Top.scala 387:19]
  assign n121_I_t0b = n120_O_t0b; // @[Top.scala 386:12]
  assign n121_I_t1b = n120_O_t1b; // @[Top.scala 386:12]
  assign n122_clock = clock;
  assign n122_reset = reset;
  assign n122_valid_up = n121_valid_down; // @[Top.scala 390:19]
  assign n122_I_0_t0b = n121_O_0_t0b; // @[Top.scala 389:12]
  assign n122_I_0_t1b = n121_O_0_t1b; // @[Top.scala 389:12]
  assign n123_clock = clock;
  assign n123_reset = reset;
  assign n123_valid_up = valid_up & n122_valid_down; // @[Top.scala 394:19]
  assign n123_I0_0_0 = I_0_0; // @[Top.scala 392:13]
  assign n123_I0_0_1 = I_0_1; // @[Top.scala 392:13]
  assign n123_I0_0_2 = I_0_2; // @[Top.scala 392:13]
  assign n123_I1_0_t0b = n122_O_0_t0b; // @[Top.scala 393:13]
  assign n123_I1_0_t1b = n122_O_0_t1b; // @[Top.scala 393:13]
endmodule
module MapT_54(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0_0,
  input  [31:0] I_0_1,
  input  [31:0] I_0_2,
  output [31:0] O_0_t0b,
  output [31:0] O_0_t1b_t0b,
  output [31:0] O_0_t1b_t1b
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_0_0; // @[MapT.scala 8:20]
  wire [31:0] op_I_0_1; // @[MapT.scala 8:20]
  wire [31:0] op_I_0_2; // @[MapT.scala 8:20]
  wire [31:0] op_O_0_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_O_0_t1b_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_O_0_t1b_t1b; // @[MapT.scala 8:20]
  Module_7 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_0(op_I_0_0),
    .I_0_1(op_I_0_1),
    .I_0_2(op_I_0_2),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b_t0b(op_O_0_t1b_t0b),
    .O_0_t1b_t1b(op_O_0_t1b_t1b)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0_t0b = op_O_0_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t0b = op_O_0_t1b_t0b; // @[MapT.scala 15:7]
  assign O_0_t1b_t1b = op_O_0_t1b_t1b; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_0 = I_0_0; // @[MapT.scala 14:10]
  assign op_I_0_1 = I_0_1; // @[MapT.scala 14:10]
  assign op_I_0_2 = I_0_2; // @[MapT.scala 14:10]
endmodule
module Passthrough_11(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0_t0b,
  input  [31:0] I_0_t1b_t0b,
  input  [31:0] I_0_t1b_t1b,
  output [31:0] O_t0b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_t0b = I_0_t0b; // @[Passthrough.scala 17:68]
  assign O_t1b_t0b = I_0_t1b_t0b; // @[Passthrough.scala 17:68]
  assign O_t1b_t1b = I_0_t1b_t1b; // @[Passthrough.scala 17:68]
endmodule
module Passthrough_12(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [31:0] I_t1b_t0b,
  input  [31:0] I_t1b_t1b,
  output [31:0] O_t0b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_t0b = I_t0b; // @[Passthrough.scala 17:68]
  assign O_t1b_t0b = I_t1b_t0b; // @[Passthrough.scala 17:68]
  assign O_t1b_t1b = I_t1b_t1b; // @[Passthrough.scala 17:68]
endmodule
module Fst_1(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  output [31:0] O
);
  assign valid_down = valid_up; // @[Tuple.scala 59:14]
  assign O = I_t0b; // @[Tuple.scala 58:5]
endmodule
module MapT_55(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  output [31:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  Fst_1 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
endmodule
module MapT_56(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  output [31:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  MapT_55 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
endmodule
module MapT_60(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0_0,
  input  [31:0] I_0_1,
  input  [31:0] I_0_2,
  output [31:0] O_0,
  output [31:0] O_1,
  output [31:0] O_2
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_0_0; // @[MapT.scala 8:20]
  wire [31:0] op_I_0_1; // @[MapT.scala 8:20]
  wire [31:0] op_I_0_2; // @[MapT.scala 8:20]
  wire [31:0] op_O_0; // @[MapT.scala 8:20]
  wire [31:0] op_O_1; // @[MapT.scala 8:20]
  wire [31:0] op_O_2; // @[MapT.scala 8:20]
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
module InitialDelayCounter_10(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [3:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 4'hb; // @[InitialDelayCounter.scala 17:17]
  wire [3:0] _T_4 = value + 4'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 4'hb; // @[InitialDelayCounter.scala 16:16]
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
module Map2S_5(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_0,
  input  [31:0] I0_1,
  input  [31:0] I0_2,
  input  [31:0] I1_0,
  input  [31:0] I1_1,
  input  [31:0] I1_2,
  output [31:0] O_0_t0b,
  output [31:0] O_0_t1b,
  output [31:0] O_1_t0b,
  output [31:0] O_1_t1b,
  output [31:0] O_2_t0b,
  output [31:0] O_2_t1b
);
  wire  fst_op_valid_up; // @[Map2S.scala 9:22]
  wire  fst_op_valid_down; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_I0; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_I1; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_O_t0b; // @[Map2S.scala 9:22]
  wire [31:0] fst_op_O_t1b; // @[Map2S.scala 9:22]
  wire  other_ops_0_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_0_valid_down; // @[Map2S.scala 10:86]
  wire [31:0] other_ops_0_I0; // @[Map2S.scala 10:86]
  wire [31:0] other_ops_0_I1; // @[Map2S.scala 10:86]
  wire [31:0] other_ops_0_O_t0b; // @[Map2S.scala 10:86]
  wire [31:0] other_ops_0_O_t1b; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_down; // @[Map2S.scala 10:86]
  wire [31:0] other_ops_1_I0; // @[Map2S.scala 10:86]
  wire [31:0] other_ops_1_I1; // @[Map2S.scala 10:86]
  wire [31:0] other_ops_1_O_t0b; // @[Map2S.scala 10:86]
  wire [31:0] other_ops_1_O_t1b; // @[Map2S.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[Map2S.scala 26:83]
  Map2T_16 fst_op ( // @[Map2S.scala 9:22]
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I0(fst_op_I0),
    .I1(fst_op_I1),
    .O_t0b(fst_op_O_t0b),
    .O_t1b(fst_op_O_t1b)
  );
  Map2T_16 other_ops_0 ( // @[Map2S.scala 10:86]
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I0(other_ops_0_I0),
    .I1(other_ops_0_I1),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b(other_ops_0_O_t1b)
  );
  Map2T_16 other_ops_1 ( // @[Map2S.scala 10:86]
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
module Map2T_76(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0_0,
  input  [31:0] I0_1,
  input  [31:0] I0_2,
  input  [31:0] I1_0,
  input  [31:0] I1_1,
  input  [31:0] I1_2,
  output [31:0] O_0_t0b,
  output [31:0] O_0_t1b,
  output [31:0] O_1_t0b,
  output [31:0] O_1_t1b,
  output [31:0] O_2_t0b,
  output [31:0] O_2_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_1; // @[Map2T.scala 8:20]
  wire [31:0] op_I0_2; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_0; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_1; // @[Map2T.scala 8:20]
  wire [31:0] op_I1_2; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_0_t1b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_1_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_1_t1b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_2_t0b; // @[Map2T.scala 8:20]
  wire [31:0] op_O_2_t1b; // @[Map2T.scala 8:20]
  Map2S_5 op ( // @[Map2T.scala 8:20]
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
module Mul(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [31:0] I_t1b,
  output [31:0] O
);
  wire [31:0] BlackBoxMulUInt32_I0; // @[Arithmetic.scala 198:27]
  wire [31:0] BlackBoxMulUInt32_I1; // @[Arithmetic.scala 198:27]
  wire [63:0] BlackBoxMulUInt32_O; // @[Arithmetic.scala 198:27]
  wire  BlackBoxMulUInt32_clock; // @[Arithmetic.scala 198:27]
  reg  _T_1; // @[Arithmetic.scala 217:66]
  reg [31:0] _RAND_0;
  reg  _T_2; // @[Arithmetic.scala 217:58]
  reg [31:0] _RAND_1;
  reg  _T_3; // @[Arithmetic.scala 217:50]
  reg [31:0] _RAND_2;
  reg  _T_4; // @[Arithmetic.scala 217:42]
  reg [31:0] _RAND_3;
  reg  _T_5; // @[Arithmetic.scala 217:34]
  reg [31:0] _RAND_4;
  reg  _T_6; // @[Arithmetic.scala 217:26]
  reg [31:0] _RAND_5;
  BlackBoxMulUInt32 BlackBoxMulUInt32 ( // @[Arithmetic.scala 198:27]
    .I0(BlackBoxMulUInt32_I0),
    .I1(BlackBoxMulUInt32_I1),
    .O(BlackBoxMulUInt32_O),
    .clock(BlackBoxMulUInt32_clock)
  );
  assign valid_down = _T_6; // @[Arithmetic.scala 217:16]
  assign O = BlackBoxMulUInt32_O[31:0]; // @[Arithmetic.scala 201:7]
  assign BlackBoxMulUInt32_I0 = I_t0b; // @[Arithmetic.scala 199:21]
  assign BlackBoxMulUInt32_I1 = I_t1b; // @[Arithmetic.scala 200:21]
  assign BlackBoxMulUInt32_clock = clock; // @[Arithmetic.scala 202:24]
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
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_4 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_5 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_6 = _RAND_5[0:0];
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
    _T_4 <= _T_3;
    _T_5 <= _T_4;
    _T_6 <= _T_5;
  end
endmodule
module MapT_61(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [31:0] I_t1b,
  output [31:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  Mul op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b(op_I_t1b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b = I_t1b; // @[MapT.scala 14:10]
endmodule
module MapS_7(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0_t0b,
  input  [31:0] I_0_t1b,
  input  [31:0] I_1_t0b,
  input  [31:0] I_1_t1b,
  input  [31:0] I_2_t0b,
  input  [31:0] I_2_t1b,
  output [31:0] O_0,
  output [31:0] O_1,
  output [31:0] O_2
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [31:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [31:0] fst_op_I_t1b; // @[MapS.scala 9:22]
  wire [31:0] fst_op_O; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [31:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [31:0] other_ops_0_I_t1b; // @[MapS.scala 10:86]
  wire [31:0] other_ops_0_O; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [31:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [31:0] other_ops_1_I_t1b; // @[MapS.scala 10:86]
  wire [31:0] other_ops_1_O; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  MapT_61 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b(fst_op_I_t1b),
    .O(fst_op_O)
  );
  MapT_61 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b(other_ops_0_I_t1b),
    .O(other_ops_0_O)
  );
  MapT_61 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
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
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b = I_0_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b = I_1_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b = I_2_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_62(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0_t0b,
  input  [31:0] I_0_t1b,
  input  [31:0] I_1_t0b,
  input  [31:0] I_1_t1b,
  input  [31:0] I_2_t0b,
  input  [31:0] I_2_t1b,
  output [31:0] O_0,
  output [31:0] O_1,
  output [31:0] O_2
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_0_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_1_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_2_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_O_0; // @[MapT.scala 8:20]
  wire [31:0] op_O_1; // @[MapT.scala 8:20]
  wire [31:0] op_O_2; // @[MapT.scala 8:20]
  MapS_7 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
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
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b = I_0_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b = I_1_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b = I_2_t1b; // @[MapT.scala 14:10]
endmodule
module ReduceT_2(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  wire  NestedCountersWithNumValid_CE; // @[ReduceT.scala 22:34]
  wire  NestedCountersWithNumValid_valid; // @[ReduceT.scala 22:34]
  wire [31:0] AddNoValid_I_t0b; // @[ReduceT.scala 25:25]
  wire [31:0] AddNoValid_I_t1b; // @[ReduceT.scala 25:25]
  wire [31:0] AddNoValid_O; // @[ReduceT.scala 25:25]
  reg  _T; // @[ReduceT.scala 26:50]
  reg [31:0] _RAND_0;
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire [1:0] _T_5 = value + 2'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value == 2'h0; // @[ReduceT.scala 34:60]
  reg [31:0] _T_7; // @[ReduceT.scala 34:76]
  reg [31:0] _RAND_2;
  reg [31:0] _T_9; // @[ReduceT.scala 35:24]
  reg [31:0] _RAND_3;
  reg  _T_10; // @[ReduceT.scala 37:35]
  reg [31:0] _RAND_4;
  reg [31:0] _T_11; // @[ReduceT.scala 44:83]
  reg [31:0] _RAND_5;
  reg  _T_12; // @[ReduceT.scala 51:28]
  reg [31:0] _RAND_6;
  wire  _T_13 = value == 2'h2; // @[ReduceT.scala 52:51]
  wire  _T_14 = _T_12 | _T_13; // @[ReduceT.scala 52:28]
  reg [31:0] _T_15; // @[ReduceT.scala 56:15]
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
  _T_7 = _RAND_2[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_9 = _RAND_3[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_10 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_11 = _RAND_5[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_12 = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_15 = _RAND_7[31:0];
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
      value <= _T_5;
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
module MapS_8(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O_0,
  output [31:0] O_1,
  output [31:0] O_2
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [31:0] fst_op_I; // @[MapS.scala 9:22]
  wire [31:0] fst_op_O; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [31:0] other_ops_0_I; // @[MapS.scala 10:86]
  wire [31:0] other_ops_0_O; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [31:0] other_ops_1_I; // @[MapS.scala 10:86]
  wire [31:0] other_ops_1_O; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  ReduceT_2 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I(fst_op_I),
    .O(fst_op_O)
  );
  ReduceT_2 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I(other_ops_0_I),
    .O(other_ops_0_O)
  );
  ReduceT_2 other_ops_1 ( // @[MapS.scala 10:86]
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
module MapT_63(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O_0,
  output [31:0] O_1,
  output [31:0] O_2
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_0; // @[MapT.scala 8:20]
  wire [31:0] op_I_1; // @[MapT.scala 8:20]
  wire [31:0] op_I_2; // @[MapT.scala 8:20]
  wire [31:0] op_O_0; // @[MapT.scala 8:20]
  wire [31:0] op_O_1; // @[MapT.scala 8:20]
  wire [31:0] op_O_2; // @[MapT.scala 8:20]
  MapS_8 op ( // @[MapT.scala 8:20]
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
  input  [31:0] I_t0b,
  input  [31:0] I_t1b,
  output [31:0] O
);
  wire [31:0] op_I_t0b; // @[MapT.scala 21:20]
  wire [31:0] op_I_t1b; // @[MapT.scala 21:20]
  wire [31:0] op_O; // @[MapT.scala 21:20]
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
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O_0
);
  wire [31:0] MapTNoValid_I_t0b; // @[ReduceS.scala 20:43]
  wire [31:0] MapTNoValid_I_t1b; // @[ReduceS.scala 20:43]
  wire [31:0] MapTNoValid_O; // @[ReduceS.scala 20:43]
  wire [31:0] MapTNoValid_1_I_t0b; // @[ReduceS.scala 20:43]
  wire [31:0] MapTNoValid_1_I_t1b; // @[ReduceS.scala 20:43]
  wire [31:0] MapTNoValid_1_O; // @[ReduceS.scala 20:43]
  reg [31:0] _T; // @[ReduceS.scala 27:24]
  reg [31:0] _RAND_0;
  reg [31:0] _T_1; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_1;
  reg [31:0] _T_2; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_2;
  reg [31:0] _T_3; // @[ReduceS.scala 43:46]
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
  assign MapTNoValid_I_t0b = _T_2; // @[ReduceS.scala 43:18]
  assign MapTNoValid_I_t1b = MapTNoValid_1_O; // @[ReduceS.scala 36:18]
  assign MapTNoValid_1_I_t0b = _T_1; // @[ReduceS.scala 43:18]
  assign MapTNoValid_1_I_t1b = _T_3; // @[ReduceS.scala 43:18]
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
module MapT_64(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O_0
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_0; // @[MapT.scala 8:20]
  wire [31:0] op_I_1; // @[MapT.scala 8:20]
  wire [31:0] op_I_2; // @[MapT.scala 8:20]
  wire [31:0] op_O_0; // @[MapT.scala 8:20]
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
module ReduceT_3(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  output [31:0] O_0
);
  reg [31:0] undelayed_out_0; // @[ReduceT.scala 17:29]
  reg [31:0] _RAND_0;
  reg  _T_1; // @[ReduceT.scala 18:34]
  reg [31:0] _RAND_1;
  reg  _T_2; // @[ReduceT.scala 18:26]
  reg [31:0] _RAND_2;
  reg [31:0] _T_3_0; // @[ReduceT.scala 56:15]
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
  undelayed_out_0 = _RAND_0[31:0];
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
  _T_3_0 = _RAND_3[31:0];
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
module InitialDelayCounter_11(
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
module AtomTuple_26(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [7:0]  I1,
  output [31:0] O_t0b,
  output [7:0]  O_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b = I0; // @[Tuple.scala 49:9]
  assign O_t1b = I1; // @[Tuple.scala 50:9]
endmodule
module Map2T_77(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [7:0]  I1,
  output [31:0] O_t0b,
  output [7:0]  O_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t0b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_t1b; // @[Map2T.scala 8:20]
  AtomTuple_26 op ( // @[Map2T.scala 8:20]
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
module Map2T_78(
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [7:0]  I1,
  output [31:0] O_t0b,
  output [7:0]  O_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1; // @[Map2T.scala 8:20]
  wire [31:0] op_O_t0b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_t1b; // @[Map2T.scala 8:20]
  Map2T_77 op ( // @[Map2T.scala 8:20]
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
module Div(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [7:0]  I_t1b,
  output [31:0] O
);
  wire [31:0] BlackBoxMulUInt32_I0; // @[Arithmetic.scala 359:27]
  wire [31:0] BlackBoxMulUInt32_I1; // @[Arithmetic.scala 359:27]
  wire [63:0] BlackBoxMulUInt32_O; // @[Arithmetic.scala 359:27]
  wire  BlackBoxMulUInt32_clock; // @[Arithmetic.scala 359:27]
  wire [8:0] _T_1 = {1'h0,I_t1b}; // @[Cat.scala 30:58]
  reg  _T_3; // @[Arithmetic.scala 370:66]
  reg [31:0] _RAND_0;
  reg  _T_4; // @[Arithmetic.scala 370:58]
  reg [31:0] _RAND_1;
  reg  _T_5; // @[Arithmetic.scala 370:50]
  reg [31:0] _RAND_2;
  reg  _T_6; // @[Arithmetic.scala 370:42]
  reg [31:0] _RAND_3;
  reg  _T_7; // @[Arithmetic.scala 370:34]
  reg [31:0] _RAND_4;
  reg  _T_8; // @[Arithmetic.scala 370:26]
  reg [31:0] _RAND_5;
  BlackBoxMulUInt32 BlackBoxMulUInt32 ( // @[Arithmetic.scala 359:27]
    .I0(BlackBoxMulUInt32_I0),
    .I1(BlackBoxMulUInt32_I1),
    .O(BlackBoxMulUInt32_O),
    .clock(BlackBoxMulUInt32_clock)
  );
  assign valid_down = _T_8; // @[Arithmetic.scala 370:16]
  assign O = BlackBoxMulUInt32_O[38:7]; // @[Arithmetic.scala 362:7]
  assign BlackBoxMulUInt32_I0 = I_t0b; // @[Arithmetic.scala 360:21]
  assign BlackBoxMulUInt32_I1 = {{23'd0}, _T_1}; // @[Arithmetic.scala 361:21]
  assign BlackBoxMulUInt32_clock = clock; // @[Arithmetic.scala 363:24]
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
  _T_3 = _RAND_0[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_4 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_5 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_6 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_7 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_8 = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      _T_3 <= 1'h0;
    end else begin
      _T_3 <= valid_up;
    end
    if (reset) begin
      _T_4 <= 1'h0;
    end else begin
      _T_4 <= _T_3;
    end
    if (reset) begin
      _T_5 <= 1'h0;
    end else begin
      _T_5 <= _T_4;
    end
    _T_6 <= _T_5;
    _T_7 <= _T_6;
    _T_8 <= _T_7;
  end
endmodule
module MapT_65(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [7:0]  I_t1b,
  output [31:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  Div op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b(op_I_t1b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b = I_t1b; // @[MapT.scala 14:10]
endmodule
module MapT_66(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [7:0]  I_t1b,
  output [31:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  MapT_65 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b(op_I_t1b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b = I_t1b; // @[MapT.scala 14:10]
endmodule
module Module_8(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O
);
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n537_valid_up; // @[Top.scala 403:22]
  wire  n537_valid_down; // @[Top.scala 403:22]
  wire [31:0] n537_I0_0; // @[Top.scala 403:22]
  wire [31:0] n537_I0_1; // @[Top.scala 403:22]
  wire [31:0] n537_I0_2; // @[Top.scala 403:22]
  wire [31:0] n537_I1_0; // @[Top.scala 403:22]
  wire [31:0] n537_I1_1; // @[Top.scala 403:22]
  wire [31:0] n537_I1_2; // @[Top.scala 403:22]
  wire [31:0] n537_O_0_t0b; // @[Top.scala 403:22]
  wire [31:0] n537_O_0_t1b; // @[Top.scala 403:22]
  wire [31:0] n537_O_1_t0b; // @[Top.scala 403:22]
  wire [31:0] n537_O_1_t1b; // @[Top.scala 403:22]
  wire [31:0] n537_O_2_t0b; // @[Top.scala 403:22]
  wire [31:0] n537_O_2_t1b; // @[Top.scala 403:22]
  wire  n553_clock; // @[Top.scala 407:22]
  wire  n553_reset; // @[Top.scala 407:22]
  wire  n553_valid_up; // @[Top.scala 407:22]
  wire  n553_valid_down; // @[Top.scala 407:22]
  wire [31:0] n553_I_0_t0b; // @[Top.scala 407:22]
  wire [31:0] n553_I_0_t1b; // @[Top.scala 407:22]
  wire [31:0] n553_I_1_t0b; // @[Top.scala 407:22]
  wire [31:0] n553_I_1_t1b; // @[Top.scala 407:22]
  wire [31:0] n553_I_2_t0b; // @[Top.scala 407:22]
  wire [31:0] n553_I_2_t1b; // @[Top.scala 407:22]
  wire [31:0] n553_O_0; // @[Top.scala 407:22]
  wire [31:0] n553_O_1; // @[Top.scala 407:22]
  wire [31:0] n553_O_2; // @[Top.scala 407:22]
  wire  n560_clock; // @[Top.scala 410:22]
  wire  n560_reset; // @[Top.scala 410:22]
  wire  n560_valid_up; // @[Top.scala 410:22]
  wire  n560_valid_down; // @[Top.scala 410:22]
  wire [31:0] n560_I_0; // @[Top.scala 410:22]
  wire [31:0] n560_I_1; // @[Top.scala 410:22]
  wire [31:0] n560_I_2; // @[Top.scala 410:22]
  wire [31:0] n560_O_0; // @[Top.scala 410:22]
  wire [31:0] n560_O_1; // @[Top.scala 410:22]
  wire [31:0] n560_O_2; // @[Top.scala 410:22]
  wire  n567_clock; // @[Top.scala 413:22]
  wire  n567_reset; // @[Top.scala 413:22]
  wire  n567_valid_up; // @[Top.scala 413:22]
  wire  n567_valid_down; // @[Top.scala 413:22]
  wire [31:0] n567_I_0; // @[Top.scala 413:22]
  wire [31:0] n567_I_1; // @[Top.scala 413:22]
  wire [31:0] n567_I_2; // @[Top.scala 413:22]
  wire [31:0] n567_O_0; // @[Top.scala 413:22]
  wire  n570_clock; // @[Top.scala 416:22]
  wire  n570_reset; // @[Top.scala 416:22]
  wire  n570_valid_up; // @[Top.scala 416:22]
  wire  n570_valid_down; // @[Top.scala 416:22]
  wire [31:0] n570_I_0; // @[Top.scala 416:22]
  wire [31:0] n570_O_0; // @[Top.scala 416:22]
  wire  n571_valid_up; // @[Top.scala 419:22]
  wire  n571_valid_down; // @[Top.scala 419:22]
  wire [31:0] n571_I_0; // @[Top.scala 419:22]
  wire [31:0] n571_O; // @[Top.scala 419:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n574_valid_up; // @[Top.scala 423:22]
  wire  n574_valid_down; // @[Top.scala 423:22]
  wire [31:0] n574_I0; // @[Top.scala 423:22]
  wire [7:0] n574_I1; // @[Top.scala 423:22]
  wire [31:0] n574_O_t0b; // @[Top.scala 423:22]
  wire [7:0] n574_O_t1b; // @[Top.scala 423:22]
  wire  n585_clock; // @[Top.scala 427:22]
  wire  n585_reset; // @[Top.scala 427:22]
  wire  n585_valid_up; // @[Top.scala 427:22]
  wire  n585_valid_down; // @[Top.scala 427:22]
  wire [31:0] n585_I_t0b; // @[Top.scala 427:22]
  wire [7:0] n585_I_t1b; // @[Top.scala 427:22]
  wire [31:0] n585_O; // @[Top.scala 427:22]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire [1:0] _T_8 = value + 2'h1; // @[Counter.scala 39:22]
  wire [31:0] _GEN_5 = 2'h1 == value ? 32'h2 : 32'h1; // @[Const.scala 25:72]
  wire [31:0] _GEN_6 = 2'h1 == value ? 32'h4 : 32'h2; // @[Const.scala 25:72]
  wire [31:0] _GEN_8 = 2'h2 == value ? 32'h1 : _GEN_5; // @[Const.scala 25:72]
  wire [31:0] _GEN_9 = 2'h2 == value ? 32'h2 : _GEN_6; // @[Const.scala 25:72]
  reg [1:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire [1:0] _T_14 = value_1 + 2'h1; // @[Counter.scala 39:22]
  wire [7:0] _GEN_17 = 2'h1 == value_1 ? 8'h0 : $signed(8'sh8); // @[Const.scala 25:72]
  wire [7:0] _GEN_18 = 2'h2 == value_1 ? 8'h0 : $signed(_GEN_17); // @[Const.scala 25:72]
  InitialDelayCounter_10 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Map2T_76 n537 ( // @[Top.scala 403:22]
    .valid_up(n537_valid_up),
    .valid_down(n537_valid_down),
    .I0_0(n537_I0_0),
    .I0_1(n537_I0_1),
    .I0_2(n537_I0_2),
    .I1_0(n537_I1_0),
    .I1_1(n537_I1_1),
    .I1_2(n537_I1_2),
    .O_0_t0b(n537_O_0_t0b),
    .O_0_t1b(n537_O_0_t1b),
    .O_1_t0b(n537_O_1_t0b),
    .O_1_t1b(n537_O_1_t1b),
    .O_2_t0b(n537_O_2_t0b),
    .O_2_t1b(n537_O_2_t1b)
  );
  MapT_62 n553 ( // @[Top.scala 407:22]
    .clock(n553_clock),
    .reset(n553_reset),
    .valid_up(n553_valid_up),
    .valid_down(n553_valid_down),
    .I_0_t0b(n553_I_0_t0b),
    .I_0_t1b(n553_I_0_t1b),
    .I_1_t0b(n553_I_1_t0b),
    .I_1_t1b(n553_I_1_t1b),
    .I_2_t0b(n553_I_2_t0b),
    .I_2_t1b(n553_I_2_t1b),
    .O_0(n553_O_0),
    .O_1(n553_O_1),
    .O_2(n553_O_2)
  );
  MapT_63 n560 ( // @[Top.scala 410:22]
    .clock(n560_clock),
    .reset(n560_reset),
    .valid_up(n560_valid_up),
    .valid_down(n560_valid_down),
    .I_0(n560_I_0),
    .I_1(n560_I_1),
    .I_2(n560_I_2),
    .O_0(n560_O_0),
    .O_1(n560_O_1),
    .O_2(n560_O_2)
  );
  MapT_64 n567 ( // @[Top.scala 413:22]
    .clock(n567_clock),
    .reset(n567_reset),
    .valid_up(n567_valid_up),
    .valid_down(n567_valid_down),
    .I_0(n567_I_0),
    .I_1(n567_I_1),
    .I_2(n567_I_2),
    .O_0(n567_O_0)
  );
  ReduceT_3 n570 ( // @[Top.scala 416:22]
    .clock(n570_clock),
    .reset(n570_reset),
    .valid_up(n570_valid_up),
    .valid_down(n570_valid_down),
    .I_0(n570_I_0),
    .O_0(n570_O_0)
  );
  Passthrough_8 n571 ( // @[Top.scala 419:22]
    .valid_up(n571_valid_up),
    .valid_down(n571_valid_down),
    .I_0(n571_I_0),
    .O(n571_O)
  );
  InitialDelayCounter_11 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  Map2T_78 n574 ( // @[Top.scala 423:22]
    .valid_up(n574_valid_up),
    .valid_down(n574_valid_down),
    .I0(n574_I0),
    .I1(n574_I1),
    .O_t0b(n574_O_t0b),
    .O_t1b(n574_O_t1b)
  );
  MapT_66 n585 ( // @[Top.scala 427:22]
    .clock(n585_clock),
    .reset(n585_reset),
    .valid_up(n585_valid_up),
    .valid_down(n585_valid_down),
    .I_t0b(n585_I_t0b),
    .I_t1b(n585_I_t1b),
    .O(n585_O)
  );
  assign valid_down = n585_valid_down; // @[Top.scala 431:16]
  assign O = n585_O; // @[Top.scala 430:7]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n537_valid_up = valid_up & InitialDelayCounter_valid_down; // @[Top.scala 406:19]
  assign n537_I0_0 = I_0; // @[Top.scala 404:13]
  assign n537_I0_1 = I_1; // @[Top.scala 404:13]
  assign n537_I0_2 = I_2; // @[Top.scala 404:13]
  assign n537_I1_0 = 2'h3 == value ? 32'h2 : _GEN_8; // @[Top.scala 405:13]
  assign n537_I1_1 = 2'h3 == value ? 32'h1 : _GEN_9; // @[Top.scala 405:13]
  assign n537_I1_2 = 2'h3 == value ? 32'h0 : _GEN_8; // @[Top.scala 405:13]
  assign n553_clock = clock;
  assign n553_reset = reset;
  assign n553_valid_up = n537_valid_down; // @[Top.scala 409:19]
  assign n553_I_0_t0b = n537_O_0_t0b; // @[Top.scala 408:12]
  assign n553_I_0_t1b = n537_O_0_t1b; // @[Top.scala 408:12]
  assign n553_I_1_t0b = n537_O_1_t0b; // @[Top.scala 408:12]
  assign n553_I_1_t1b = n537_O_1_t1b; // @[Top.scala 408:12]
  assign n553_I_2_t0b = n537_O_2_t0b; // @[Top.scala 408:12]
  assign n553_I_2_t1b = n537_O_2_t1b; // @[Top.scala 408:12]
  assign n560_clock = clock;
  assign n560_reset = reset;
  assign n560_valid_up = n553_valid_down; // @[Top.scala 412:19]
  assign n560_I_0 = n553_O_0; // @[Top.scala 411:12]
  assign n560_I_1 = n553_O_1; // @[Top.scala 411:12]
  assign n560_I_2 = n553_O_2; // @[Top.scala 411:12]
  assign n567_clock = clock;
  assign n567_reset = reset;
  assign n567_valid_up = n560_valid_down; // @[Top.scala 415:19]
  assign n567_I_0 = n560_O_0; // @[Top.scala 414:12]
  assign n567_I_1 = n560_O_1; // @[Top.scala 414:12]
  assign n567_I_2 = n560_O_2; // @[Top.scala 414:12]
  assign n570_clock = clock;
  assign n570_reset = reset;
  assign n570_valid_up = n567_valid_down; // @[Top.scala 418:19]
  assign n570_I_0 = n567_O_0; // @[Top.scala 417:12]
  assign n571_valid_up = n570_valid_down; // @[Top.scala 421:19]
  assign n571_I_0 = n570_O_0; // @[Top.scala 420:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n574_valid_up = n571_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 426:19]
  assign n574_I0 = n571_O; // @[Top.scala 424:13]
  assign n574_I1 = 2'h3 == value_1 ? 8'h0 : $signed(_GEN_18); // @[Top.scala 425:13]
  assign n585_clock = clock;
  assign n585_reset = reset;
  assign n585_valid_up = n574_valid_down; // @[Top.scala 429:19]
  assign n585_I_t0b = n574_O_t0b; // @[Top.scala 428:12]
  assign n585_I_t1b = n574_O_t1b; // @[Top.scala 428:12]
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
      value <= _T_8;
    end
    if (reset) begin
      value_1 <= 2'h0;
    end else if (InitialDelayCounter_1_valid_down) begin
      value_1 <= _T_14;
    end
  end
endmodule
module MapT_67(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_0; // @[MapT.scala 8:20]
  wire [31:0] op_I_1; // @[MapT.scala 8:20]
  wire [31:0] op_I_2; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  Module_8 op ( // @[MapT.scala 8:20]
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
module FIFO_18(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  reg [31:0] _T [0:21]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_0;
  wire [31:0] _T__T_15_data; // @[FIFO.scala 23:33]
  wire [4:0] _T__T_15_addr; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_1;
  wire [31:0] _T__T_5_data; // @[FIFO.scala 23:33]
  wire [4:0] _T__T_5_addr; // @[FIFO.scala 23:33]
  wire  _T__T_5_mask; // @[FIFO.scala 23:33]
  wire  _T__T_5_en; // @[FIFO.scala 23:33]
  reg  _T__T_15_en_pipe_0;
  reg [31:0] _RAND_2;
  reg [4:0] _T__T_15_addr_pipe_0;
  reg [31:0] _RAND_3;
  reg [4:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_4;
  reg [4:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_5;
  reg [4:0] value_2; // @[Counter.scala 29:33]
  reg [31:0] _RAND_6;
  wire  _T_1 = value == 5'h15; // @[FIFO.scala 33:46]
  wire  _T_2 = value_2 == 5'h15; // @[Counter.scala 38:24]
  wire [4:0] _T_4 = value_2 + 5'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value < 5'h15; // @[FIFO.scala 38:39]
  wire [4:0] _T_9 = value + 5'h1; // @[Counter.scala 39:22]
  wire  _T_10 = value >= 5'h14; // @[FIFO.scala 42:39]
  wire  _T_16 = value_1 == 5'h15; // @[Counter.scala 38:24]
  wire [4:0] _T_18 = value_1 + 5'h1; // @[Counter.scala 39:22]
  wire  _GEN_8 = _T_10 & _T_10; // @[FIFO.scala 42:57]
  assign _T__T_15_addr = _T__T_15_addr_pipe_0;
  `ifndef RANDOMIZE_GARBAGE_ASSIGN
  assign _T__T_15_data = _T[_T__T_15_addr]; // @[FIFO.scala 23:33]
  `else
  assign _T__T_15_data = _T__T_15_addr >= 5'h16 ? _RAND_1[31:0] : _T[_T__T_15_addr]; // @[FIFO.scala 23:33]
  `endif // RANDOMIZE_GARBAGE_ASSIGN
  assign _T__T_5_data = I;
  assign _T__T_5_addr = value_2;
  assign _T__T_5_mask = 1'h1;
  assign _T__T_5_en = valid_up;
  assign valid_down = value == 5'h15; // @[FIFO.scala 33:16]
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
  for (initvar = 0; initvar < 22; initvar = initvar+1)
    _T[initvar] = _RAND_0[31:0];
  `endif // RANDOMIZE_MEM_INIT
  _RAND_1 = {1{`RANDOM}};
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T__T_15_en_pipe_0 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T__T_15_addr_pipe_0 = _RAND_3[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  value = _RAND_4[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  value_1 = _RAND_5[4:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  value_2 = _RAND_6[4:0];
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
      value <= 5'h0;
    end else if (valid_up) begin
      if (_T_6) begin
        if (_T_1) begin
          value <= 5'h0;
        end else begin
          value <= _T_9;
        end
      end
    end
    if (reset) begin
      value_1 <= 5'h0;
    end else if (valid_up) begin
      if (_T_10) begin
        if (_T_16) begin
          value_1 <= 5'h0;
        end else begin
          value_1 <= _T_18;
        end
      end
    end
    if (reset) begin
      value_2 <= 5'h0;
    end else if (valid_up) begin
      if (_T_2) begin
        value_2 <= 5'h0;
      end else begin
        value_2 <= _T_4;
      end
    end
  end
endmodule
module FIFO_19(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I,
  output [31:0] O
);
  reg [31:0] _T [0:6]; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_0;
  wire [31:0] _T__T_15_data; // @[FIFO.scala 23:33]
  wire [2:0] _T__T_15_addr; // @[FIFO.scala 23:33]
  reg [31:0] _RAND_1;
  wire [31:0] _T__T_5_data; // @[FIFO.scala 23:33]
  wire [2:0] _T__T_5_addr; // @[FIFO.scala 23:33]
  wire  _T__T_5_mask; // @[FIFO.scala 23:33]
  wire  _T__T_5_en; // @[FIFO.scala 23:33]
  reg  _T__T_15_en_pipe_0;
  reg [31:0] _RAND_2;
  reg [2:0] _T__T_15_addr_pipe_0;
  reg [31:0] _RAND_3;
  reg [2:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_4;
  reg [2:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_5;
  reg [2:0] value_2; // @[Counter.scala 29:33]
  reg [31:0] _RAND_6;
  wire  _T_1 = value == 3'h6; // @[FIFO.scala 33:46]
  wire  _T_2 = value_2 == 3'h6; // @[Counter.scala 38:24]
  wire [2:0] _T_4 = value_2 + 3'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value < 3'h6; // @[FIFO.scala 38:39]
  wire [2:0] _T_9 = value + 3'h1; // @[Counter.scala 39:22]
  wire  _T_10 = value >= 3'h5; // @[FIFO.scala 42:39]
  wire  _T_16 = value_1 == 3'h6; // @[Counter.scala 38:24]
  wire [2:0] _T_18 = value_1 + 3'h1; // @[Counter.scala 39:22]
  wire  _GEN_8 = _T_10 & _T_10; // @[FIFO.scala 42:57]
  assign _T__T_15_addr = _T__T_15_addr_pipe_0;
  `ifndef RANDOMIZE_GARBAGE_ASSIGN
  assign _T__T_15_data = _T[_T__T_15_addr]; // @[FIFO.scala 23:33]
  `else
  assign _T__T_15_data = _T__T_15_addr >= 3'h7 ? _RAND_1[31:0] : _T[_T__T_15_addr]; // @[FIFO.scala 23:33]
  `endif // RANDOMIZE_GARBAGE_ASSIGN
  assign _T__T_5_data = I;
  assign _T__T_5_addr = value_2;
  assign _T__T_5_mask = 1'h1;
  assign _T__T_5_en = valid_up;
  assign valid_down = value == 3'h6; // @[FIFO.scala 33:16]
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
  for (initvar = 0; initvar < 7; initvar = initvar+1)
    _T[initvar] = _RAND_0[31:0];
  `endif // RANDOMIZE_MEM_INIT
  _RAND_1 = {1{`RANDOM}};
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T__T_15_en_pipe_0 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T__T_15_addr_pipe_0 = _RAND_3[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  value = _RAND_4[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  value_1 = _RAND_5[2:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  value_2 = _RAND_6[2:0];
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
      value <= 3'h0;
    end else if (valid_up) begin
      if (_T_6) begin
        if (_T_1) begin
          value <= 3'h0;
        end else begin
          value <= _T_9;
        end
      end
    end
    if (reset) begin
      value_1 <= 3'h0;
    end else if (valid_up) begin
      if (_T_10) begin
        if (_T_16) begin
          value_1 <= 3'h0;
        end else begin
          value_1 <= _T_18;
        end
      end
    end
    if (reset) begin
      value_2 <= 3'h0;
    end else if (valid_up) begin
      if (_T_2) begin
        value_2 <= 3'h0;
      end else begin
        value_2 <= _T_4;
      end
    end
  end
endmodule
module InitialDelayCounter_12(
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
module Sub(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [31:0] I_t1b,
  output [31:0] O
);
  assign valid_down = valid_up; // @[Arithmetic.scala 142:14]
  assign O = I_t0b - I_t1b; // @[Arithmetic.scala 140:7]
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
module AtomTuple_33(
  input         valid_up,
  output        valid_down,
  input         I0,
  input  [31:0] I1_t0b,
  input  [31:0] I1_t1b,
  output        O_t0b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 51:14]
  assign O_t0b = I0; // @[Tuple.scala 49:9]
  assign O_t1b_t0b = I1_t0b; // @[Tuple.scala 50:9]
  assign O_t1b_t1b = I1_t1b; // @[Tuple.scala 50:9]
endmodule
module If_3(
  input         valid_up,
  output        valid_down,
  input         I_t0b,
  input  [31:0] I_t1b_t0b,
  input  [31:0] I_t1b_t1b,
  output [31:0] O
);
  assign valid_down = valid_up; // @[Arithmetic.scala 528:14]
  assign O = I_t0b ? I_t1b_t0b : I_t1b_t1b; // @[Arithmetic.scala 526:9 Arithmetic.scala 527:20]
endmodule
module Module_9(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [31:0] I1,
  output [31:0] O
);
  wire  n617_clock; // @[Top.scala 438:22]
  wire  n617_reset; // @[Top.scala 438:22]
  wire  n617_valid_up; // @[Top.scala 438:22]
  wire  n617_valid_down; // @[Top.scala 438:22]
  wire [31:0] n617_I; // @[Top.scala 438:22]
  wire [31:0] n617_O; // @[Top.scala 438:22]
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n596_valid_up; // @[Top.scala 442:22]
  wire  n596_valid_down; // @[Top.scala 442:22]
  wire [31:0] n596_I0; // @[Top.scala 442:22]
  wire [31:0] n596_I1; // @[Top.scala 442:22]
  wire [31:0] n596_O_t0b; // @[Top.scala 442:22]
  wire [31:0] n596_O_t1b; // @[Top.scala 442:22]
  wire  n597_valid_up; // @[Top.scala 446:22]
  wire  n597_valid_down; // @[Top.scala 446:22]
  wire [31:0] n597_I_t0b; // @[Top.scala 446:22]
  wire [31:0] n597_I_t1b; // @[Top.scala 446:22]
  wire [31:0] n597_O; // @[Top.scala 446:22]
  wire  n599_valid_up; // @[Top.scala 449:22]
  wire  n599_valid_down; // @[Top.scala 449:22]
  wire [31:0] n599_I0; // @[Top.scala 449:22]
  wire [31:0] n599_I1; // @[Top.scala 449:22]
  wire [31:0] n599_O_t0b; // @[Top.scala 449:22]
  wire [31:0] n599_O_t1b; // @[Top.scala 449:22]
  wire  n600_valid_up; // @[Top.scala 453:22]
  wire  n600_valid_down; // @[Top.scala 453:22]
  wire [31:0] n600_I_t0b; // @[Top.scala 453:22]
  wire [31:0] n600_I_t1b; // @[Top.scala 453:22]
  wire [31:0] n600_O; // @[Top.scala 453:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n601_valid_up; // @[Top.scala 457:22]
  wire  n601_valid_down; // @[Top.scala 457:22]
  wire [31:0] n601_I0; // @[Top.scala 457:22]
  wire [31:0] n601_I1; // @[Top.scala 457:22]
  wire [31:0] n601_O_t0b; // @[Top.scala 457:22]
  wire [31:0] n601_O_t1b; // @[Top.scala 457:22]
  wire  n602_valid_up; // @[Top.scala 461:22]
  wire  n602_valid_down; // @[Top.scala 461:22]
  wire [31:0] n602_I_t0b; // @[Top.scala 461:22]
  wire [31:0] n602_I_t1b; // @[Top.scala 461:22]
  wire [31:0] n602_O; // @[Top.scala 461:22]
  wire  n604_valid_up; // @[Top.scala 464:22]
  wire  n604_valid_down; // @[Top.scala 464:22]
  wire [31:0] n604_I0; // @[Top.scala 464:22]
  wire [31:0] n604_I1; // @[Top.scala 464:22]
  wire [31:0] n604_O_t0b; // @[Top.scala 464:22]
  wire [31:0] n604_O_t1b; // @[Top.scala 464:22]
  wire  n605_valid_up; // @[Top.scala 468:22]
  wire  n605_valid_down; // @[Top.scala 468:22]
  wire [31:0] n605_I_t0b; // @[Top.scala 468:22]
  wire [31:0] n605_I_t1b; // @[Top.scala 468:22]
  wire [31:0] n605_O; // @[Top.scala 468:22]
  wire  n606_valid_up; // @[Top.scala 471:22]
  wire  n606_valid_down; // @[Top.scala 471:22]
  wire  n606_I0; // @[Top.scala 471:22]
  wire  n606_I1; // @[Top.scala 471:22]
  wire  n606_O_t0b; // @[Top.scala 471:22]
  wire  n606_O_t1b; // @[Top.scala 471:22]
  wire  n607_valid_up; // @[Top.scala 475:22]
  wire  n607_valid_down; // @[Top.scala 475:22]
  wire  n607_I_t0b; // @[Top.scala 475:22]
  wire  n607_I_t1b; // @[Top.scala 475:22]
  wire  n607_O; // @[Top.scala 475:22]
  wire  InitialDelayCounter_2_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_2_valid_down; // @[Const.scala 11:33]
  wire  n610_valid_up; // @[Top.scala 479:22]
  wire  n610_valid_down; // @[Top.scala 479:22]
  wire [31:0] n610_I0; // @[Top.scala 479:22]
  wire [31:0] n610_I1; // @[Top.scala 479:22]
  wire [31:0] n610_O_t0b; // @[Top.scala 479:22]
  wire [31:0] n610_O_t1b; // @[Top.scala 479:22]
  wire  n611_valid_up; // @[Top.scala 483:22]
  wire  n611_valid_down; // @[Top.scala 483:22]
  wire  n611_I0; // @[Top.scala 483:22]
  wire [31:0] n611_I1_t0b; // @[Top.scala 483:22]
  wire [31:0] n611_I1_t1b; // @[Top.scala 483:22]
  wire  n611_O_t0b; // @[Top.scala 483:22]
  wire [31:0] n611_O_t1b_t0b; // @[Top.scala 483:22]
  wire [31:0] n611_O_t1b_t1b; // @[Top.scala 483:22]
  wire  n612_valid_up; // @[Top.scala 487:22]
  wire  n612_valid_down; // @[Top.scala 487:22]
  wire  n612_I_t0b; // @[Top.scala 487:22]
  wire [31:0] n612_I_t1b_t0b; // @[Top.scala 487:22]
  wire [31:0] n612_I_t1b_t1b; // @[Top.scala 487:22]
  wire [31:0] n612_O; // @[Top.scala 487:22]
  wire  InitialDelayCounter_3_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_3_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_3_valid_down; // @[Const.scala 11:33]
  wire  n615_valid_up; // @[Top.scala 491:22]
  wire  n615_valid_down; // @[Top.scala 491:22]
  wire [31:0] n615_I0; // @[Top.scala 491:22]
  wire [7:0] n615_I1; // @[Top.scala 491:22]
  wire [31:0] n615_O_t0b; // @[Top.scala 491:22]
  wire [7:0] n615_O_t1b; // @[Top.scala 491:22]
  wire  n616_clock; // @[Top.scala 495:22]
  wire  n616_reset; // @[Top.scala 495:22]
  wire  n616_valid_up; // @[Top.scala 495:22]
  wire  n616_valid_down; // @[Top.scala 495:22]
  wire [31:0] n616_I_t0b; // @[Top.scala 495:22]
  wire [7:0] n616_I_t1b; // @[Top.scala 495:22]
  wire [31:0] n616_O; // @[Top.scala 495:22]
  wire  n618_valid_up; // @[Top.scala 498:22]
  wire  n618_valid_down; // @[Top.scala 498:22]
  wire [31:0] n618_I0; // @[Top.scala 498:22]
  wire [31:0] n618_I1; // @[Top.scala 498:22]
  wire [31:0] n618_O_t0b; // @[Top.scala 498:22]
  wire [31:0] n618_O_t1b; // @[Top.scala 498:22]
  wire  n619_valid_up; // @[Top.scala 502:22]
  wire  n619_valid_down; // @[Top.scala 502:22]
  wire [31:0] n619_I_t0b; // @[Top.scala 502:22]
  wire [31:0] n619_I_t1b; // @[Top.scala 502:22]
  wire [31:0] n619_O; // @[Top.scala 502:22]
  FIFO_19 n617 ( // @[Top.scala 438:22]
    .clock(n617_clock),
    .reset(n617_reset),
    .valid_up(n617_valid_up),
    .valid_down(n617_valid_down),
    .I(n617_I),
    .O(n617_O)
  );
  InitialDelayCounter_12 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  AtomTuple n596 ( // @[Top.scala 442:22]
    .valid_up(n596_valid_up),
    .valid_down(n596_valid_down),
    .I0(n596_I0),
    .I1(n596_I1),
    .O_t0b(n596_O_t0b),
    .O_t1b(n596_O_t1b)
  );
  Sub n597 ( // @[Top.scala 446:22]
    .valid_up(n597_valid_up),
    .valid_down(n597_valid_down),
    .I_t0b(n597_I_t0b),
    .I_t1b(n597_I_t1b),
    .O(n597_O)
  );
  AtomTuple n599 ( // @[Top.scala 449:22]
    .valid_up(n599_valid_up),
    .valid_down(n599_valid_down),
    .I0(n599_I0),
    .I1(n599_I1),
    .O_t0b(n599_O_t0b),
    .O_t1b(n599_O_t1b)
  );
  Lt n600 ( // @[Top.scala 453:22]
    .valid_up(n600_valid_up),
    .valid_down(n600_valid_down),
    .I_t0b(n600_I_t0b),
    .I_t1b(n600_I_t1b),
    .O(n600_O)
  );
  InitialDelayCounter_12 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  AtomTuple n601 ( // @[Top.scala 457:22]
    .valid_up(n601_valid_up),
    .valid_down(n601_valid_down),
    .I0(n601_I0),
    .I1(n601_I1),
    .O_t0b(n601_O_t0b),
    .O_t1b(n601_O_t1b)
  );
  Sub n602 ( // @[Top.scala 461:22]
    .valid_up(n602_valid_up),
    .valid_down(n602_valid_down),
    .I_t0b(n602_I_t0b),
    .I_t1b(n602_I_t1b),
    .O(n602_O)
  );
  AtomTuple n604 ( // @[Top.scala 464:22]
    .valid_up(n604_valid_up),
    .valid_down(n604_valid_down),
    .I0(n604_I0),
    .I1(n604_I1),
    .O_t0b(n604_O_t0b),
    .O_t1b(n604_O_t1b)
  );
  Lt n605 ( // @[Top.scala 468:22]
    .valid_up(n605_valid_up),
    .valid_down(n605_valid_down),
    .I_t0b(n605_I_t0b),
    .I_t1b(n605_I_t1b),
    .O(n605_O)
  );
  AtomTuple_4 n606 ( // @[Top.scala 471:22]
    .valid_up(n606_valid_up),
    .valid_down(n606_valid_down),
    .I0(n606_I0),
    .I1(n606_I1),
    .O_t0b(n606_O_t0b),
    .O_t1b(n606_O_t1b)
  );
  LogicalOr n607 ( // @[Top.scala 475:22]
    .valid_up(n607_valid_up),
    .valid_down(n607_valid_down),
    .I_t0b(n607_I_t0b),
    .I_t1b(n607_I_t1b),
    .O(n607_O)
  );
  InitialDelayCounter_12 InitialDelayCounter_2 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_2_clock),
    .reset(InitialDelayCounter_2_reset),
    .valid_down(InitialDelayCounter_2_valid_down)
  );
  AtomTuple n610 ( // @[Top.scala 479:22]
    .valid_up(n610_valid_up),
    .valid_down(n610_valid_down),
    .I0(n610_I0),
    .I1(n610_I1),
    .O_t0b(n610_O_t0b),
    .O_t1b(n610_O_t1b)
  );
  AtomTuple_33 n611 ( // @[Top.scala 483:22]
    .valid_up(n611_valid_up),
    .valid_down(n611_valid_down),
    .I0(n611_I0),
    .I1_t0b(n611_I1_t0b),
    .I1_t1b(n611_I1_t1b),
    .O_t0b(n611_O_t0b),
    .O_t1b_t0b(n611_O_t1b_t0b),
    .O_t1b_t1b(n611_O_t1b_t1b)
  );
  If_3 n612 ( // @[Top.scala 487:22]
    .valid_up(n612_valid_up),
    .valid_down(n612_valid_down),
    .I_t0b(n612_I_t0b),
    .I_t1b_t0b(n612_I_t1b_t0b),
    .I_t1b_t1b(n612_I_t1b_t1b),
    .O(n612_O)
  );
  InitialDelayCounter_12 InitialDelayCounter_3 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_3_clock),
    .reset(InitialDelayCounter_3_reset),
    .valid_down(InitialDelayCounter_3_valid_down)
  );
  AtomTuple_26 n615 ( // @[Top.scala 491:22]
    .valid_up(n615_valid_up),
    .valid_down(n615_valid_down),
    .I0(n615_I0),
    .I1(n615_I1),
    .O_t0b(n615_O_t0b),
    .O_t1b(n615_O_t1b)
  );
  Div n616 ( // @[Top.scala 495:22]
    .clock(n616_clock),
    .reset(n616_reset),
    .valid_up(n616_valid_up),
    .valid_down(n616_valid_down),
    .I_t0b(n616_I_t0b),
    .I_t1b(n616_I_t1b),
    .O(n616_O)
  );
  AtomTuple n618 ( // @[Top.scala 498:22]
    .valid_up(n618_valid_up),
    .valid_down(n618_valid_down),
    .I0(n618_I0),
    .I1(n618_I1),
    .O_t0b(n618_O_t0b),
    .O_t1b(n618_O_t1b)
  );
  Add n619 ( // @[Top.scala 502:22]
    .valid_up(n619_valid_up),
    .valid_down(n619_valid_down),
    .I_t0b(n619_I_t0b),
    .I_t1b(n619_I_t1b),
    .O(n619_O)
  );
  assign valid_down = n619_valid_down; // @[Top.scala 506:16]
  assign O = n619_O; // @[Top.scala 505:7]
  assign n617_clock = clock;
  assign n617_reset = reset;
  assign n617_valid_up = valid_up; // @[Top.scala 440:19]
  assign n617_I = I1; // @[Top.scala 439:12]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n596_valid_up = valid_up; // @[Top.scala 445:19]
  assign n596_I0 = I0; // @[Top.scala 443:13]
  assign n596_I1 = I1; // @[Top.scala 444:13]
  assign n597_valid_up = n596_valid_down; // @[Top.scala 448:19]
  assign n597_I_t0b = n596_O_t0b; // @[Top.scala 447:12]
  assign n597_I_t1b = n596_O_t1b; // @[Top.scala 447:12]
  assign n599_valid_up = InitialDelayCounter_valid_down & n597_valid_down; // @[Top.scala 452:19]
  assign n599_I0 = 32'hf; // @[Top.scala 450:13]
  assign n599_I1 = n597_O; // @[Top.scala 451:13]
  assign n600_valid_up = n599_valid_down; // @[Top.scala 455:19]
  assign n600_I_t0b = n599_O_t0b; // @[Top.scala 454:12]
  assign n600_I_t1b = n599_O_t1b; // @[Top.scala 454:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n601_valid_up = valid_up; // @[Top.scala 460:19]
  assign n601_I0 = I1; // @[Top.scala 458:13]
  assign n601_I1 = I0; // @[Top.scala 459:13]
  assign n602_valid_up = n601_valid_down; // @[Top.scala 463:19]
  assign n602_I_t0b = n601_O_t0b; // @[Top.scala 462:12]
  assign n602_I_t1b = n601_O_t1b; // @[Top.scala 462:12]
  assign n604_valid_up = InitialDelayCounter_1_valid_down & n602_valid_down; // @[Top.scala 467:19]
  assign n604_I0 = 32'hf; // @[Top.scala 465:13]
  assign n604_I1 = n602_O; // @[Top.scala 466:13]
  assign n605_valid_up = n604_valid_down; // @[Top.scala 470:19]
  assign n605_I_t0b = n604_O_t0b; // @[Top.scala 469:12]
  assign n605_I_t1b = n604_O_t1b; // @[Top.scala 469:12]
  assign n606_valid_up = n600_valid_down & n605_valid_down; // @[Top.scala 474:19]
  assign n606_I0 = n600_O[0]; // @[Top.scala 472:13]
  assign n606_I1 = n605_O[0]; // @[Top.scala 473:13]
  assign n607_valid_up = n606_valid_down; // @[Top.scala 477:19]
  assign n607_I_t0b = n606_O_t0b; // @[Top.scala 476:12]
  assign n607_I_t1b = n606_O_t1b; // @[Top.scala 476:12]
  assign InitialDelayCounter_2_clock = clock;
  assign InitialDelayCounter_2_reset = reset;
  assign n610_valid_up = n602_valid_down & InitialDelayCounter_2_valid_down; // @[Top.scala 482:19]
  assign n610_I0 = n602_O; // @[Top.scala 480:13]
  assign n610_I1 = 32'h0; // @[Top.scala 481:13]
  assign n611_valid_up = n607_valid_down & n610_valid_down; // @[Top.scala 486:19]
  assign n611_I0 = n607_O; // @[Top.scala 484:13]
  assign n611_I1_t0b = n610_O_t0b; // @[Top.scala 485:13]
  assign n611_I1_t1b = n610_O_t1b; // @[Top.scala 485:13]
  assign n612_valid_up = n611_valid_down; // @[Top.scala 489:19]
  assign n612_I_t0b = n611_O_t0b; // @[Top.scala 488:12]
  assign n612_I_t1b_t0b = n611_O_t1b_t0b; // @[Top.scala 488:12]
  assign n612_I_t1b_t1b = n611_O_t1b_t1b; // @[Top.scala 488:12]
  assign InitialDelayCounter_3_clock = clock;
  assign InitialDelayCounter_3_reset = reset;
  assign n615_valid_up = n612_valid_down & InitialDelayCounter_3_valid_down; // @[Top.scala 494:19]
  assign n615_I0 = n612_O; // @[Top.scala 492:13]
  assign n615_I1 = 8'sh20; // @[Top.scala 493:13]
  assign n616_clock = clock;
  assign n616_reset = reset;
  assign n616_valid_up = n615_valid_down; // @[Top.scala 497:19]
  assign n616_I_t0b = n615_O_t0b; // @[Top.scala 496:12]
  assign n616_I_t1b = n615_O_t1b; // @[Top.scala 496:12]
  assign n618_valid_up = n617_valid_down & n616_valid_down; // @[Top.scala 501:19]
  assign n618_I0 = n617_O; // @[Top.scala 499:13]
  assign n618_I1 = n616_O; // @[Top.scala 500:13]
  assign n619_valid_up = n618_valid_down; // @[Top.scala 504:19]
  assign n619_I_t0b = n618_O_t0b; // @[Top.scala 503:12]
  assign n619_I_t1b = n618_O_t1b; // @[Top.scala 503:12]
endmodule
module Map2T_79(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [31:0] I1,
  output [31:0] O
);
  wire  op_clock; // @[Map2T.scala 8:20]
  wire  op_reset; // @[Map2T.scala 8:20]
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0; // @[Map2T.scala 8:20]
  wire [31:0] op_I1; // @[Map2T.scala 8:20]
  wire [31:0] op_O; // @[Map2T.scala 8:20]
  Module_9 op ( // @[Map2T.scala 8:20]
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
module Map2T_80(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I0,
  input  [31:0] I1,
  output [31:0] O
);
  wire  op_clock; // @[Map2T.scala 8:20]
  wire  op_reset; // @[Map2T.scala 8:20]
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [31:0] op_I0; // @[Map2T.scala 8:20]
  wire [31:0] op_I1; // @[Map2T.scala 8:20]
  wire [31:0] op_O; // @[Map2T.scala 8:20]
  Map2T_79 op ( // @[Map2T.scala 8:20]
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
module Snd_1(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t1b_t0b,
  input  [31:0] I_t1b_t1b,
  output [31:0] O_t0b,
  output [31:0] O_t1b
);
  assign valid_down = valid_up; // @[Tuple.scala 67:14]
  assign O_t0b = I_t1b_t0b; // @[Tuple.scala 66:5]
  assign O_t1b = I_t1b_t1b; // @[Tuple.scala 66:5]
endmodule
module Fst_2(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  output [31:0] O
);
  assign valid_down = valid_up; // @[Tuple.scala 59:14]
  assign O = I_t0b; // @[Tuple.scala 58:5]
endmodule
module Module_10(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t1b_t0b,
  input  [31:0] I_t1b_t1b,
  output [31:0] O
);
  wire  n622_valid_up; // @[Top.scala 512:22]
  wire  n622_valid_down; // @[Top.scala 512:22]
  wire [31:0] n622_I_t1b_t0b; // @[Top.scala 512:22]
  wire [31:0] n622_I_t1b_t1b; // @[Top.scala 512:22]
  wire [31:0] n622_O_t0b; // @[Top.scala 512:22]
  wire [31:0] n622_O_t1b; // @[Top.scala 512:22]
  wire  n623_valid_up; // @[Top.scala 515:22]
  wire  n623_valid_down; // @[Top.scala 515:22]
  wire [31:0] n623_I_t0b; // @[Top.scala 515:22]
  wire [31:0] n623_O; // @[Top.scala 515:22]
  Snd_1 n622 ( // @[Top.scala 512:22]
    .valid_up(n622_valid_up),
    .valid_down(n622_valid_down),
    .I_t1b_t0b(n622_I_t1b_t0b),
    .I_t1b_t1b(n622_I_t1b_t1b),
    .O_t0b(n622_O_t0b),
    .O_t1b(n622_O_t1b)
  );
  Fst_2 n623 ( // @[Top.scala 515:22]
    .valid_up(n623_valid_up),
    .valid_down(n623_valid_down),
    .I_t0b(n623_I_t0b),
    .O(n623_O)
  );
  assign valid_down = n623_valid_down; // @[Top.scala 519:16]
  assign O = n623_O; // @[Top.scala 518:7]
  assign n622_valid_up = valid_up; // @[Top.scala 514:19]
  assign n622_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 513:12]
  assign n622_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 513:12]
  assign n623_valid_up = n622_valid_down; // @[Top.scala 517:19]
  assign n623_I_t0b = n622_O_t0b; // @[Top.scala 516:12]
endmodule
module MapT_68(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t1b_t0b,
  input  [31:0] I_t1b_t1b,
  output [31:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  Module_10 op ( // @[MapT.scala 8:20]
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
module MapT_69(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t1b_t0b,
  input  [31:0] I_t1b_t1b,
  output [31:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  MapT_68 op ( // @[MapT.scala 8:20]
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
module ReduceS_1(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O_0
);
  wire [31:0] MapTNoValid_I_t0b; // @[ReduceS.scala 20:43]
  wire [31:0] MapTNoValid_I_t1b; // @[ReduceS.scala 20:43]
  wire [31:0] MapTNoValid_O; // @[ReduceS.scala 20:43]
  wire [31:0] MapTNoValid_1_I_t0b; // @[ReduceS.scala 20:43]
  wire [31:0] MapTNoValid_1_I_t1b; // @[ReduceS.scala 20:43]
  wire [31:0] MapTNoValid_1_O; // @[ReduceS.scala 20:43]
  reg [31:0] _T; // @[ReduceS.scala 27:24]
  reg [31:0] _RAND_0;
  reg [31:0] _T_1; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_1;
  reg [31:0] _T_2; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_2;
  reg [31:0] _T_3; // @[ReduceS.scala 43:46]
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
  assign MapTNoValid_I_t0b = _T_1; // @[ReduceS.scala 43:18]
  assign MapTNoValid_I_t1b = MapTNoValid_1_O; // @[ReduceS.scala 36:18]
  assign MapTNoValid_1_I_t0b = _T_3; // @[ReduceS.scala 43:18]
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
module MapT_77(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O_0
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_0; // @[MapT.scala 8:20]
  wire [31:0] op_I_1; // @[MapT.scala 8:20]
  wire [31:0] op_I_2; // @[MapT.scala 8:20]
  wire [31:0] op_O_0; // @[MapT.scala 8:20]
  ReduceS_1 op ( // @[MapT.scala 8:20]
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
module Module_11(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O
);
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n715_valid_up; // @[Top.scala 526:22]
  wire  n715_valid_down; // @[Top.scala 526:22]
  wire [31:0] n715_I0_0; // @[Top.scala 526:22]
  wire [31:0] n715_I0_1; // @[Top.scala 526:22]
  wire [31:0] n715_I0_2; // @[Top.scala 526:22]
  wire [31:0] n715_I1_0; // @[Top.scala 526:22]
  wire [31:0] n715_I1_1; // @[Top.scala 526:22]
  wire [31:0] n715_I1_2; // @[Top.scala 526:22]
  wire [31:0] n715_O_0_t0b; // @[Top.scala 526:22]
  wire [31:0] n715_O_0_t1b; // @[Top.scala 526:22]
  wire [31:0] n715_O_1_t0b; // @[Top.scala 526:22]
  wire [31:0] n715_O_1_t1b; // @[Top.scala 526:22]
  wire [31:0] n715_O_2_t0b; // @[Top.scala 526:22]
  wire [31:0] n715_O_2_t1b; // @[Top.scala 526:22]
  wire  n731_clock; // @[Top.scala 530:22]
  wire  n731_reset; // @[Top.scala 530:22]
  wire  n731_valid_up; // @[Top.scala 530:22]
  wire  n731_valid_down; // @[Top.scala 530:22]
  wire [31:0] n731_I_0_t0b; // @[Top.scala 530:22]
  wire [31:0] n731_I_0_t1b; // @[Top.scala 530:22]
  wire [31:0] n731_I_1_t0b; // @[Top.scala 530:22]
  wire [31:0] n731_I_1_t1b; // @[Top.scala 530:22]
  wire [31:0] n731_I_2_t0b; // @[Top.scala 530:22]
  wire [31:0] n731_I_2_t1b; // @[Top.scala 530:22]
  wire [31:0] n731_O_0; // @[Top.scala 530:22]
  wire [31:0] n731_O_1; // @[Top.scala 530:22]
  wire [31:0] n731_O_2; // @[Top.scala 530:22]
  wire  n738_clock; // @[Top.scala 533:22]
  wire  n738_reset; // @[Top.scala 533:22]
  wire  n738_valid_up; // @[Top.scala 533:22]
  wire  n738_valid_down; // @[Top.scala 533:22]
  wire [31:0] n738_I_0; // @[Top.scala 533:22]
  wire [31:0] n738_I_1; // @[Top.scala 533:22]
  wire [31:0] n738_I_2; // @[Top.scala 533:22]
  wire [31:0] n738_O_0; // @[Top.scala 533:22]
  wire [31:0] n738_O_1; // @[Top.scala 533:22]
  wire [31:0] n738_O_2; // @[Top.scala 533:22]
  wire  n745_clock; // @[Top.scala 536:22]
  wire  n745_reset; // @[Top.scala 536:22]
  wire  n745_valid_up; // @[Top.scala 536:22]
  wire  n745_valid_down; // @[Top.scala 536:22]
  wire [31:0] n745_I_0; // @[Top.scala 536:22]
  wire [31:0] n745_I_1; // @[Top.scala 536:22]
  wire [31:0] n745_I_2; // @[Top.scala 536:22]
  wire [31:0] n745_O_0; // @[Top.scala 536:22]
  wire  n748_clock; // @[Top.scala 539:22]
  wire  n748_reset; // @[Top.scala 539:22]
  wire  n748_valid_up; // @[Top.scala 539:22]
  wire  n748_valid_down; // @[Top.scala 539:22]
  wire [31:0] n748_I_0; // @[Top.scala 539:22]
  wire [31:0] n748_O_0; // @[Top.scala 539:22]
  wire  n749_valid_up; // @[Top.scala 542:22]
  wire  n749_valid_down; // @[Top.scala 542:22]
  wire [31:0] n749_I_0; // @[Top.scala 542:22]
  wire [31:0] n749_O; // @[Top.scala 542:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n752_valid_up; // @[Top.scala 546:22]
  wire  n752_valid_down; // @[Top.scala 546:22]
  wire [31:0] n752_I0; // @[Top.scala 546:22]
  wire [7:0] n752_I1; // @[Top.scala 546:22]
  wire [31:0] n752_O_t0b; // @[Top.scala 546:22]
  wire [7:0] n752_O_t1b; // @[Top.scala 546:22]
  wire  n763_clock; // @[Top.scala 550:22]
  wire  n763_reset; // @[Top.scala 550:22]
  wire  n763_valid_up; // @[Top.scala 550:22]
  wire  n763_valid_down; // @[Top.scala 550:22]
  wire [31:0] n763_I_t0b; // @[Top.scala 550:22]
  wire [7:0] n763_I_t1b; // @[Top.scala 550:22]
  wire [31:0] n763_O; // @[Top.scala 550:22]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire [1:0] _T_8 = value + 2'h1; // @[Counter.scala 39:22]
  wire [31:0] _GEN_5 = 2'h1 == value ? 32'h2 : 32'h1; // @[Const.scala 25:72]
  wire [31:0] _GEN_6 = 2'h1 == value ? 32'h4 : 32'h2; // @[Const.scala 25:72]
  wire [31:0] _GEN_8 = 2'h2 == value ? 32'h1 : _GEN_5; // @[Const.scala 25:72]
  wire [31:0] _GEN_9 = 2'h2 == value ? 32'h2 : _GEN_6; // @[Const.scala 25:72]
  reg [1:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire [1:0] _T_14 = value_1 + 2'h1; // @[Counter.scala 39:22]
  wire [7:0] _GEN_17 = 2'h1 == value_1 ? 8'h0 : $signed(8'sh8); // @[Const.scala 25:72]
  wire [7:0] _GEN_18 = 2'h2 == value_1 ? 8'h0 : $signed(_GEN_17); // @[Const.scala 25:72]
  InitialDelayCounter_10 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Map2T_76 n715 ( // @[Top.scala 526:22]
    .valid_up(n715_valid_up),
    .valid_down(n715_valid_down),
    .I0_0(n715_I0_0),
    .I0_1(n715_I0_1),
    .I0_2(n715_I0_2),
    .I1_0(n715_I1_0),
    .I1_1(n715_I1_1),
    .I1_2(n715_I1_2),
    .O_0_t0b(n715_O_0_t0b),
    .O_0_t1b(n715_O_0_t1b),
    .O_1_t0b(n715_O_1_t0b),
    .O_1_t1b(n715_O_1_t1b),
    .O_2_t0b(n715_O_2_t0b),
    .O_2_t1b(n715_O_2_t1b)
  );
  MapT_62 n731 ( // @[Top.scala 530:22]
    .clock(n731_clock),
    .reset(n731_reset),
    .valid_up(n731_valid_up),
    .valid_down(n731_valid_down),
    .I_0_t0b(n731_I_0_t0b),
    .I_0_t1b(n731_I_0_t1b),
    .I_1_t0b(n731_I_1_t0b),
    .I_1_t1b(n731_I_1_t1b),
    .I_2_t0b(n731_I_2_t0b),
    .I_2_t1b(n731_I_2_t1b),
    .O_0(n731_O_0),
    .O_1(n731_O_1),
    .O_2(n731_O_2)
  );
  MapT_63 n738 ( // @[Top.scala 533:22]
    .clock(n738_clock),
    .reset(n738_reset),
    .valid_up(n738_valid_up),
    .valid_down(n738_valid_down),
    .I_0(n738_I_0),
    .I_1(n738_I_1),
    .I_2(n738_I_2),
    .O_0(n738_O_0),
    .O_1(n738_O_1),
    .O_2(n738_O_2)
  );
  MapT_77 n745 ( // @[Top.scala 536:22]
    .clock(n745_clock),
    .reset(n745_reset),
    .valid_up(n745_valid_up),
    .valid_down(n745_valid_down),
    .I_0(n745_I_0),
    .I_1(n745_I_1),
    .I_2(n745_I_2),
    .O_0(n745_O_0)
  );
  ReduceT_3 n748 ( // @[Top.scala 539:22]
    .clock(n748_clock),
    .reset(n748_reset),
    .valid_up(n748_valid_up),
    .valid_down(n748_valid_down),
    .I_0(n748_I_0),
    .O_0(n748_O_0)
  );
  Passthrough_8 n749 ( // @[Top.scala 542:22]
    .valid_up(n749_valid_up),
    .valid_down(n749_valid_down),
    .I_0(n749_I_0),
    .O(n749_O)
  );
  InitialDelayCounter_11 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  Map2T_78 n752 ( // @[Top.scala 546:22]
    .valid_up(n752_valid_up),
    .valid_down(n752_valid_down),
    .I0(n752_I0),
    .I1(n752_I1),
    .O_t0b(n752_O_t0b),
    .O_t1b(n752_O_t1b)
  );
  MapT_66 n763 ( // @[Top.scala 550:22]
    .clock(n763_clock),
    .reset(n763_reset),
    .valid_up(n763_valid_up),
    .valid_down(n763_valid_down),
    .I_t0b(n763_I_t0b),
    .I_t1b(n763_I_t1b),
    .O(n763_O)
  );
  assign valid_down = n763_valid_down; // @[Top.scala 554:16]
  assign O = n763_O; // @[Top.scala 553:7]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n715_valid_up = valid_up & InitialDelayCounter_valid_down; // @[Top.scala 529:19]
  assign n715_I0_0 = I_0; // @[Top.scala 527:13]
  assign n715_I0_1 = I_1; // @[Top.scala 527:13]
  assign n715_I0_2 = I_2; // @[Top.scala 527:13]
  assign n715_I1_0 = 2'h3 == value ? 32'h2 : _GEN_8; // @[Top.scala 528:13]
  assign n715_I1_1 = 2'h3 == value ? 32'h1 : _GEN_9; // @[Top.scala 528:13]
  assign n715_I1_2 = 2'h3 == value ? 32'h0 : _GEN_8; // @[Top.scala 528:13]
  assign n731_clock = clock;
  assign n731_reset = reset;
  assign n731_valid_up = n715_valid_down; // @[Top.scala 532:19]
  assign n731_I_0_t0b = n715_O_0_t0b; // @[Top.scala 531:12]
  assign n731_I_0_t1b = n715_O_0_t1b; // @[Top.scala 531:12]
  assign n731_I_1_t0b = n715_O_1_t0b; // @[Top.scala 531:12]
  assign n731_I_1_t1b = n715_O_1_t1b; // @[Top.scala 531:12]
  assign n731_I_2_t0b = n715_O_2_t0b; // @[Top.scala 531:12]
  assign n731_I_2_t1b = n715_O_2_t1b; // @[Top.scala 531:12]
  assign n738_clock = clock;
  assign n738_reset = reset;
  assign n738_valid_up = n731_valid_down; // @[Top.scala 535:19]
  assign n738_I_0 = n731_O_0; // @[Top.scala 534:12]
  assign n738_I_1 = n731_O_1; // @[Top.scala 534:12]
  assign n738_I_2 = n731_O_2; // @[Top.scala 534:12]
  assign n745_clock = clock;
  assign n745_reset = reset;
  assign n745_valid_up = n738_valid_down; // @[Top.scala 538:19]
  assign n745_I_0 = n738_O_0; // @[Top.scala 537:12]
  assign n745_I_1 = n738_O_1; // @[Top.scala 537:12]
  assign n745_I_2 = n738_O_2; // @[Top.scala 537:12]
  assign n748_clock = clock;
  assign n748_reset = reset;
  assign n748_valid_up = n745_valid_down; // @[Top.scala 541:19]
  assign n748_I_0 = n745_O_0; // @[Top.scala 540:12]
  assign n749_valid_up = n748_valid_down; // @[Top.scala 544:19]
  assign n749_I_0 = n748_O_0; // @[Top.scala 543:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n752_valid_up = n749_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 549:19]
  assign n752_I0 = n749_O; // @[Top.scala 547:13]
  assign n752_I1 = 2'h3 == value_1 ? 8'h0 : $signed(_GEN_18); // @[Top.scala 548:13]
  assign n763_clock = clock;
  assign n763_reset = reset;
  assign n763_valid_up = n752_valid_down; // @[Top.scala 552:19]
  assign n763_I_t0b = n752_O_t0b; // @[Top.scala 551:12]
  assign n763_I_t1b = n752_O_t1b; // @[Top.scala 551:12]
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
      value <= _T_8;
    end
    if (reset) begin
      value_1 <= 2'h0;
    end else if (InitialDelayCounter_1_valid_down) begin
      value_1 <= _T_14;
    end
  end
endmodule
module MapT_80(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_0; // @[MapT.scala 8:20]
  wire [31:0] op_I_1; // @[MapT.scala 8:20]
  wire [31:0] op_I_2; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  Module_11 op ( // @[MapT.scala 8:20]
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
module Snd_3(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t1b,
  output [31:0] O
);
  assign valid_down = valid_up; // @[Tuple.scala 67:14]
  assign O = I_t1b; // @[Tuple.scala 66:5]
endmodule
module Module_13(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t1b_t0b,
  input  [31:0] I_t1b_t1b,
  output [31:0] O
);
  wire  n800_valid_up; // @[Top.scala 635:22]
  wire  n800_valid_down; // @[Top.scala 635:22]
  wire [31:0] n800_I_t1b_t0b; // @[Top.scala 635:22]
  wire [31:0] n800_I_t1b_t1b; // @[Top.scala 635:22]
  wire [31:0] n800_O_t0b; // @[Top.scala 635:22]
  wire [31:0] n800_O_t1b; // @[Top.scala 635:22]
  wire  n801_valid_up; // @[Top.scala 638:22]
  wire  n801_valid_down; // @[Top.scala 638:22]
  wire [31:0] n801_I_t1b; // @[Top.scala 638:22]
  wire [31:0] n801_O; // @[Top.scala 638:22]
  Snd_1 n800 ( // @[Top.scala 635:22]
    .valid_up(n800_valid_up),
    .valid_down(n800_valid_down),
    .I_t1b_t0b(n800_I_t1b_t0b),
    .I_t1b_t1b(n800_I_t1b_t1b),
    .O_t0b(n800_O_t0b),
    .O_t1b(n800_O_t1b)
  );
  Snd_3 n801 ( // @[Top.scala 638:22]
    .valid_up(n801_valid_up),
    .valid_down(n801_valid_down),
    .I_t1b(n801_I_t1b),
    .O(n801_O)
  );
  assign valid_down = n801_valid_down; // @[Top.scala 642:16]
  assign O = n801_O; // @[Top.scala 641:7]
  assign n800_valid_up = valid_up; // @[Top.scala 637:19]
  assign n800_I_t1b_t0b = I_t1b_t0b; // @[Top.scala 636:12]
  assign n800_I_t1b_t1b = I_t1b_t1b; // @[Top.scala 636:12]
  assign n801_valid_up = n800_valid_down; // @[Top.scala 640:19]
  assign n801_I_t1b = n800_O_t1b; // @[Top.scala 639:12]
endmodule
module MapT_81(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t1b_t0b,
  input  [31:0] I_t1b_t1b,
  output [31:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  Module_13 op ( // @[MapT.scala 8:20]
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
module MapT_82(
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t1b_t0b,
  input  [31:0] I_t1b_t1b,
  output [31:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t0b; // @[MapT.scala 8:20]
  wire [31:0] op_I_t1b_t1b; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  MapT_81 op ( // @[MapT.scala 8:20]
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
module ReduceS_2(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O_0
);
  wire [31:0] MapTNoValid_I_t0b; // @[ReduceS.scala 20:43]
  wire [31:0] MapTNoValid_I_t1b; // @[ReduceS.scala 20:43]
  wire [31:0] MapTNoValid_O; // @[ReduceS.scala 20:43]
  wire [31:0] MapTNoValid_1_I_t0b; // @[ReduceS.scala 20:43]
  wire [31:0] MapTNoValid_1_I_t1b; // @[ReduceS.scala 20:43]
  wire [31:0] MapTNoValid_1_O; // @[ReduceS.scala 20:43]
  reg [31:0] _T; // @[ReduceS.scala 27:24]
  reg [31:0] _RAND_0;
  reg [31:0] _T_1; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_1;
  reg [31:0] _T_2; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_2;
  reg [31:0] _T_3; // @[ReduceS.scala 43:46]
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
module MapT_90(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O_0
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_0; // @[MapT.scala 8:20]
  wire [31:0] op_I_1; // @[MapT.scala 8:20]
  wire [31:0] op_I_2; // @[MapT.scala 8:20]
  wire [31:0] op_O_0; // @[MapT.scala 8:20]
  ReduceS_2 op ( // @[MapT.scala 8:20]
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
module Module_14(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O
);
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n893_valid_up; // @[Top.scala 649:22]
  wire  n893_valid_down; // @[Top.scala 649:22]
  wire [31:0] n893_I0_0; // @[Top.scala 649:22]
  wire [31:0] n893_I0_1; // @[Top.scala 649:22]
  wire [31:0] n893_I0_2; // @[Top.scala 649:22]
  wire [31:0] n893_I1_0; // @[Top.scala 649:22]
  wire [31:0] n893_I1_1; // @[Top.scala 649:22]
  wire [31:0] n893_I1_2; // @[Top.scala 649:22]
  wire [31:0] n893_O_0_t0b; // @[Top.scala 649:22]
  wire [31:0] n893_O_0_t1b; // @[Top.scala 649:22]
  wire [31:0] n893_O_1_t0b; // @[Top.scala 649:22]
  wire [31:0] n893_O_1_t1b; // @[Top.scala 649:22]
  wire [31:0] n893_O_2_t0b; // @[Top.scala 649:22]
  wire [31:0] n893_O_2_t1b; // @[Top.scala 649:22]
  wire  n909_clock; // @[Top.scala 653:22]
  wire  n909_reset; // @[Top.scala 653:22]
  wire  n909_valid_up; // @[Top.scala 653:22]
  wire  n909_valid_down; // @[Top.scala 653:22]
  wire [31:0] n909_I_0_t0b; // @[Top.scala 653:22]
  wire [31:0] n909_I_0_t1b; // @[Top.scala 653:22]
  wire [31:0] n909_I_1_t0b; // @[Top.scala 653:22]
  wire [31:0] n909_I_1_t1b; // @[Top.scala 653:22]
  wire [31:0] n909_I_2_t0b; // @[Top.scala 653:22]
  wire [31:0] n909_I_2_t1b; // @[Top.scala 653:22]
  wire [31:0] n909_O_0; // @[Top.scala 653:22]
  wire [31:0] n909_O_1; // @[Top.scala 653:22]
  wire [31:0] n909_O_2; // @[Top.scala 653:22]
  wire  n916_clock; // @[Top.scala 656:22]
  wire  n916_reset; // @[Top.scala 656:22]
  wire  n916_valid_up; // @[Top.scala 656:22]
  wire  n916_valid_down; // @[Top.scala 656:22]
  wire [31:0] n916_I_0; // @[Top.scala 656:22]
  wire [31:0] n916_I_1; // @[Top.scala 656:22]
  wire [31:0] n916_I_2; // @[Top.scala 656:22]
  wire [31:0] n916_O_0; // @[Top.scala 656:22]
  wire [31:0] n916_O_1; // @[Top.scala 656:22]
  wire [31:0] n916_O_2; // @[Top.scala 656:22]
  wire  n923_clock; // @[Top.scala 659:22]
  wire  n923_reset; // @[Top.scala 659:22]
  wire  n923_valid_up; // @[Top.scala 659:22]
  wire  n923_valid_down; // @[Top.scala 659:22]
  wire [31:0] n923_I_0; // @[Top.scala 659:22]
  wire [31:0] n923_I_1; // @[Top.scala 659:22]
  wire [31:0] n923_I_2; // @[Top.scala 659:22]
  wire [31:0] n923_O_0; // @[Top.scala 659:22]
  wire  n926_clock; // @[Top.scala 662:22]
  wire  n926_reset; // @[Top.scala 662:22]
  wire  n926_valid_up; // @[Top.scala 662:22]
  wire  n926_valid_down; // @[Top.scala 662:22]
  wire [31:0] n926_I_0; // @[Top.scala 662:22]
  wire [31:0] n926_O_0; // @[Top.scala 662:22]
  wire  n927_valid_up; // @[Top.scala 665:22]
  wire  n927_valid_down; // @[Top.scala 665:22]
  wire [31:0] n927_I_0; // @[Top.scala 665:22]
  wire [31:0] n927_O; // @[Top.scala 665:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n930_valid_up; // @[Top.scala 669:22]
  wire  n930_valid_down; // @[Top.scala 669:22]
  wire [31:0] n930_I0; // @[Top.scala 669:22]
  wire [7:0] n930_I1; // @[Top.scala 669:22]
  wire [31:0] n930_O_t0b; // @[Top.scala 669:22]
  wire [7:0] n930_O_t1b; // @[Top.scala 669:22]
  wire  n941_clock; // @[Top.scala 673:22]
  wire  n941_reset; // @[Top.scala 673:22]
  wire  n941_valid_up; // @[Top.scala 673:22]
  wire  n941_valid_down; // @[Top.scala 673:22]
  wire [31:0] n941_I_t0b; // @[Top.scala 673:22]
  wire [7:0] n941_I_t1b; // @[Top.scala 673:22]
  wire [31:0] n941_O; // @[Top.scala 673:22]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire [1:0] _T_8 = value + 2'h1; // @[Counter.scala 39:22]
  wire [31:0] _GEN_5 = 2'h1 == value ? 32'h2 : 32'h1; // @[Const.scala 25:72]
  wire [31:0] _GEN_6 = 2'h1 == value ? 32'h4 : 32'h2; // @[Const.scala 25:72]
  wire [31:0] _GEN_8 = 2'h2 == value ? 32'h1 : _GEN_5; // @[Const.scala 25:72]
  wire [31:0] _GEN_9 = 2'h2 == value ? 32'h2 : _GEN_6; // @[Const.scala 25:72]
  reg [1:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire [1:0] _T_14 = value_1 + 2'h1; // @[Counter.scala 39:22]
  wire [7:0] _GEN_17 = 2'h1 == value_1 ? 8'h0 : $signed(8'sh8); // @[Const.scala 25:72]
  wire [7:0] _GEN_18 = 2'h2 == value_1 ? 8'h0 : $signed(_GEN_17); // @[Const.scala 25:72]
  InitialDelayCounter_10 InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Map2T_76 n893 ( // @[Top.scala 649:22]
    .valid_up(n893_valid_up),
    .valid_down(n893_valid_down),
    .I0_0(n893_I0_0),
    .I0_1(n893_I0_1),
    .I0_2(n893_I0_2),
    .I1_0(n893_I1_0),
    .I1_1(n893_I1_1),
    .I1_2(n893_I1_2),
    .O_0_t0b(n893_O_0_t0b),
    .O_0_t1b(n893_O_0_t1b),
    .O_1_t0b(n893_O_1_t0b),
    .O_1_t1b(n893_O_1_t1b),
    .O_2_t0b(n893_O_2_t0b),
    .O_2_t1b(n893_O_2_t1b)
  );
  MapT_62 n909 ( // @[Top.scala 653:22]
    .clock(n909_clock),
    .reset(n909_reset),
    .valid_up(n909_valid_up),
    .valid_down(n909_valid_down),
    .I_0_t0b(n909_I_0_t0b),
    .I_0_t1b(n909_I_0_t1b),
    .I_1_t0b(n909_I_1_t0b),
    .I_1_t1b(n909_I_1_t1b),
    .I_2_t0b(n909_I_2_t0b),
    .I_2_t1b(n909_I_2_t1b),
    .O_0(n909_O_0),
    .O_1(n909_O_1),
    .O_2(n909_O_2)
  );
  MapT_63 n916 ( // @[Top.scala 656:22]
    .clock(n916_clock),
    .reset(n916_reset),
    .valid_up(n916_valid_up),
    .valid_down(n916_valid_down),
    .I_0(n916_I_0),
    .I_1(n916_I_1),
    .I_2(n916_I_2),
    .O_0(n916_O_0),
    .O_1(n916_O_1),
    .O_2(n916_O_2)
  );
  MapT_90 n923 ( // @[Top.scala 659:22]
    .clock(n923_clock),
    .reset(n923_reset),
    .valid_up(n923_valid_up),
    .valid_down(n923_valid_down),
    .I_0(n923_I_0),
    .I_1(n923_I_1),
    .I_2(n923_I_2),
    .O_0(n923_O_0)
  );
  ReduceT_3 n926 ( // @[Top.scala 662:22]
    .clock(n926_clock),
    .reset(n926_reset),
    .valid_up(n926_valid_up),
    .valid_down(n926_valid_down),
    .I_0(n926_I_0),
    .O_0(n926_O_0)
  );
  Passthrough_8 n927 ( // @[Top.scala 665:22]
    .valid_up(n927_valid_up),
    .valid_down(n927_valid_down),
    .I_0(n927_I_0),
    .O(n927_O)
  );
  InitialDelayCounter_11 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  Map2T_78 n930 ( // @[Top.scala 669:22]
    .valid_up(n930_valid_up),
    .valid_down(n930_valid_down),
    .I0(n930_I0),
    .I1(n930_I1),
    .O_t0b(n930_O_t0b),
    .O_t1b(n930_O_t1b)
  );
  MapT_66 n941 ( // @[Top.scala 673:22]
    .clock(n941_clock),
    .reset(n941_reset),
    .valid_up(n941_valid_up),
    .valid_down(n941_valid_down),
    .I_t0b(n941_I_t0b),
    .I_t1b(n941_I_t1b),
    .O(n941_O)
  );
  assign valid_down = n941_valid_down; // @[Top.scala 677:16]
  assign O = n941_O; // @[Top.scala 676:7]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n893_valid_up = valid_up & InitialDelayCounter_valid_down; // @[Top.scala 652:19]
  assign n893_I0_0 = I_0; // @[Top.scala 650:13]
  assign n893_I0_1 = I_1; // @[Top.scala 650:13]
  assign n893_I0_2 = I_2; // @[Top.scala 650:13]
  assign n893_I1_0 = 2'h3 == value ? 32'h2 : _GEN_8; // @[Top.scala 651:13]
  assign n893_I1_1 = 2'h3 == value ? 32'h1 : _GEN_9; // @[Top.scala 651:13]
  assign n893_I1_2 = 2'h3 == value ? 32'h0 : _GEN_8; // @[Top.scala 651:13]
  assign n909_clock = clock;
  assign n909_reset = reset;
  assign n909_valid_up = n893_valid_down; // @[Top.scala 655:19]
  assign n909_I_0_t0b = n893_O_0_t0b; // @[Top.scala 654:12]
  assign n909_I_0_t1b = n893_O_0_t1b; // @[Top.scala 654:12]
  assign n909_I_1_t0b = n893_O_1_t0b; // @[Top.scala 654:12]
  assign n909_I_1_t1b = n893_O_1_t1b; // @[Top.scala 654:12]
  assign n909_I_2_t0b = n893_O_2_t0b; // @[Top.scala 654:12]
  assign n909_I_2_t1b = n893_O_2_t1b; // @[Top.scala 654:12]
  assign n916_clock = clock;
  assign n916_reset = reset;
  assign n916_valid_up = n909_valid_down; // @[Top.scala 658:19]
  assign n916_I_0 = n909_O_0; // @[Top.scala 657:12]
  assign n916_I_1 = n909_O_1; // @[Top.scala 657:12]
  assign n916_I_2 = n909_O_2; // @[Top.scala 657:12]
  assign n923_clock = clock;
  assign n923_reset = reset;
  assign n923_valid_up = n916_valid_down; // @[Top.scala 661:19]
  assign n923_I_0 = n916_O_0; // @[Top.scala 660:12]
  assign n923_I_1 = n916_O_1; // @[Top.scala 660:12]
  assign n923_I_2 = n916_O_2; // @[Top.scala 660:12]
  assign n926_clock = clock;
  assign n926_reset = reset;
  assign n926_valid_up = n923_valid_down; // @[Top.scala 664:19]
  assign n926_I_0 = n923_O_0; // @[Top.scala 663:12]
  assign n927_valid_up = n926_valid_down; // @[Top.scala 667:19]
  assign n927_I_0 = n926_O_0; // @[Top.scala 666:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n930_valid_up = n927_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 672:19]
  assign n930_I0 = n927_O; // @[Top.scala 670:13]
  assign n930_I1 = 2'h3 == value_1 ? 8'h0 : $signed(_GEN_18); // @[Top.scala 671:13]
  assign n941_clock = clock;
  assign n941_reset = reset;
  assign n941_valid_up = n930_valid_down; // @[Top.scala 675:19]
  assign n941_I_t0b = n930_O_t0b; // @[Top.scala 674:12]
  assign n941_I_t1b = n930_O_t1b; // @[Top.scala 674:12]
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
      value <= _T_8;
    end
    if (reset) begin
      value_1 <= 2'h0;
    end else if (InitialDelayCounter_1_valid_down) begin
      value_1 <= _T_14;
    end
  end
endmodule
module MapT_93(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_0,
  input  [31:0] I_1,
  input  [31:0] I_2,
  output [31:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [31:0] op_I_0; // @[MapT.scala 8:20]
  wire [31:0] op_I_1; // @[MapT.scala 8:20]
  wire [31:0] op_I_2; // @[MapT.scala 8:20]
  wire [31:0] op_O; // @[MapT.scala 8:20]
  Module_14 op ( // @[MapT.scala 8:20]
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
module FIFO_24(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [31:0] I_t0b,
  input  [31:0] I_t1b_t0b,
  input  [31:0] I_t1b_t1b,
  output [31:0] O_t0b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b
);
  reg [31:0] _T_t0b; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_0;
  reg [31:0] _T_t1b_t0b; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_1;
  reg [31:0] _T_t1b_t1b; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_2;
  reg  _T_1; // @[FIFO.scala 15:27]
  reg [31:0] _RAND_3;
  assign valid_down = _T_1; // @[FIFO.scala 16:16]
  assign O_t0b = _T_t0b; // @[FIFO.scala 14:7]
  assign O_t1b_t0b = _T_t1b_t0b; // @[FIFO.scala 14:7]
  assign O_t1b_t1b = _T_t1b_t1b; // @[FIFO.scala 14:7]
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
  _T_t0b = _RAND_0[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_t1b_t0b = _RAND_1[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_t1b_t1b = _RAND_2[31:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_1 = _RAND_3[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    _T_t0b <= I_t0b;
    _T_t1b_t0b <= I_t1b_t0b;
    _T_t1b_t1b <= I_t1b_t1b;
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
  input  [31:0] I,
  output [31:0] O_t0b,
  output [31:0] O_t1b_t0b,
  output [31:0] O_t1b_t1b
);
  wire  n1_clock; // @[Top.scala 758:20]
  wire  n1_reset; // @[Top.scala 758:20]
  wire  n1_valid_up; // @[Top.scala 758:20]
  wire  n1_valid_down; // @[Top.scala 758:20]
  wire [31:0] n1_I; // @[Top.scala 758:20]
  wire [31:0] n1_O; // @[Top.scala 758:20]
  wire  n2_clock; // @[Top.scala 761:20]
  wire  n2_reset; // @[Top.scala 761:20]
  wire  n2_valid_up; // @[Top.scala 761:20]
  wire  n2_valid_down; // @[Top.scala 761:20]
  wire [31:0] n2_I; // @[Top.scala 761:20]
  wire [31:0] n2_O; // @[Top.scala 761:20]
  wire  n3_clock; // @[Top.scala 764:20]
  wire  n3_reset; // @[Top.scala 764:20]
  wire  n3_valid_up; // @[Top.scala 764:20]
  wire  n3_valid_down; // @[Top.scala 764:20]
  wire [31:0] n3_I; // @[Top.scala 764:20]
  wire [31:0] n3_O; // @[Top.scala 764:20]
  wire  n4_clock; // @[Top.scala 767:20]
  wire  n4_reset; // @[Top.scala 767:20]
  wire  n4_valid_up; // @[Top.scala 767:20]
  wire  n4_valid_down; // @[Top.scala 767:20]
  wire [31:0] n4_I; // @[Top.scala 767:20]
  wire [31:0] n4_O; // @[Top.scala 767:20]
  wire  n5_clock; // @[Top.scala 770:20]
  wire  n5_reset; // @[Top.scala 770:20]
  wire  n5_valid_up; // @[Top.scala 770:20]
  wire  n5_valid_down; // @[Top.scala 770:20]
  wire [31:0] n5_I; // @[Top.scala 770:20]
  wire [31:0] n5_O; // @[Top.scala 770:20]
  wire  n6_valid_up; // @[Top.scala 773:20]
  wire  n6_valid_down; // @[Top.scala 773:20]
  wire [31:0] n6_I0; // @[Top.scala 773:20]
  wire [31:0] n6_I1; // @[Top.scala 773:20]
  wire [31:0] n6_O_0; // @[Top.scala 773:20]
  wire [31:0] n6_O_1; // @[Top.scala 773:20]
  wire  n13_valid_up; // @[Top.scala 777:21]
  wire  n13_valid_down; // @[Top.scala 777:21]
  wire [31:0] n13_I0_0; // @[Top.scala 777:21]
  wire [31:0] n13_I0_1; // @[Top.scala 777:21]
  wire [31:0] n13_I1; // @[Top.scala 777:21]
  wire [31:0] n13_O_0; // @[Top.scala 777:21]
  wire [31:0] n13_O_1; // @[Top.scala 777:21]
  wire [31:0] n13_O_2; // @[Top.scala 777:21]
  wire  n20_valid_up; // @[Top.scala 781:21]
  wire  n20_valid_down; // @[Top.scala 781:21]
  wire [31:0] n20_I_0; // @[Top.scala 781:21]
  wire [31:0] n20_I_1; // @[Top.scala 781:21]
  wire [31:0] n20_I_2; // @[Top.scala 781:21]
  wire [31:0] n20_O_0_0; // @[Top.scala 781:21]
  wire [31:0] n20_O_0_1; // @[Top.scala 781:21]
  wire [31:0] n20_O_0_2; // @[Top.scala 781:21]
  wire  n25_clock; // @[Top.scala 784:21]
  wire  n25_reset; // @[Top.scala 784:21]
  wire  n25_valid_up; // @[Top.scala 784:21]
  wire  n25_valid_down; // @[Top.scala 784:21]
  wire [31:0] n25_I_0_0; // @[Top.scala 784:21]
  wire [31:0] n25_I_0_1; // @[Top.scala 784:21]
  wire [31:0] n25_I_0_2; // @[Top.scala 784:21]
  wire [31:0] n25_O_0; // @[Top.scala 784:21]
  wire  n26_clock; // @[Top.scala 787:21]
  wire  n26_reset; // @[Top.scala 787:21]
  wire  n26_valid_up; // @[Top.scala 787:21]
  wire  n26_valid_down; // @[Top.scala 787:21]
  wire [31:0] n26_I; // @[Top.scala 787:21]
  wire [31:0] n26_O; // @[Top.scala 787:21]
  wire  n27_clock; // @[Top.scala 790:21]
  wire  n27_reset; // @[Top.scala 790:21]
  wire  n27_valid_up; // @[Top.scala 790:21]
  wire  n27_valid_down; // @[Top.scala 790:21]
  wire [31:0] n27_I; // @[Top.scala 790:21]
  wire [31:0] n27_O; // @[Top.scala 790:21]
  wire  n28_valid_up; // @[Top.scala 793:21]
  wire  n28_valid_down; // @[Top.scala 793:21]
  wire [31:0] n28_I0; // @[Top.scala 793:21]
  wire [31:0] n28_I1; // @[Top.scala 793:21]
  wire [31:0] n28_O_0; // @[Top.scala 793:21]
  wire [31:0] n28_O_1; // @[Top.scala 793:21]
  wire  n35_valid_up; // @[Top.scala 797:21]
  wire  n35_valid_down; // @[Top.scala 797:21]
  wire [31:0] n35_I0_0; // @[Top.scala 797:21]
  wire [31:0] n35_I0_1; // @[Top.scala 797:21]
  wire [31:0] n35_I1; // @[Top.scala 797:21]
  wire [31:0] n35_O_0; // @[Top.scala 797:21]
  wire [31:0] n35_O_1; // @[Top.scala 797:21]
  wire [31:0] n35_O_2; // @[Top.scala 797:21]
  wire  n42_valid_up; // @[Top.scala 801:21]
  wire  n42_valid_down; // @[Top.scala 801:21]
  wire [31:0] n42_I_0; // @[Top.scala 801:21]
  wire [31:0] n42_I_1; // @[Top.scala 801:21]
  wire [31:0] n42_I_2; // @[Top.scala 801:21]
  wire [31:0] n42_O_0_0; // @[Top.scala 801:21]
  wire [31:0] n42_O_0_1; // @[Top.scala 801:21]
  wire [31:0] n42_O_0_2; // @[Top.scala 801:21]
  wire  n47_clock; // @[Top.scala 804:21]
  wire  n47_reset; // @[Top.scala 804:21]
  wire  n47_valid_up; // @[Top.scala 804:21]
  wire  n47_valid_down; // @[Top.scala 804:21]
  wire [31:0] n47_I_0_0; // @[Top.scala 804:21]
  wire [31:0] n47_I_0_1; // @[Top.scala 804:21]
  wire [31:0] n47_I_0_2; // @[Top.scala 804:21]
  wire [31:0] n47_O_0; // @[Top.scala 804:21]
  wire  n48_valid_up; // @[Top.scala 807:21]
  wire  n48_valid_down; // @[Top.scala 807:21]
  wire [31:0] n48_I0_0; // @[Top.scala 807:21]
  wire [31:0] n48_I1_0; // @[Top.scala 807:21]
  wire [31:0] n48_O_0_0; // @[Top.scala 807:21]
  wire [31:0] n48_O_0_1; // @[Top.scala 807:21]
  wire  n55_clock; // @[Top.scala 811:21]
  wire  n55_reset; // @[Top.scala 811:21]
  wire  n55_valid_up; // @[Top.scala 811:21]
  wire  n55_valid_down; // @[Top.scala 811:21]
  wire [31:0] n55_I; // @[Top.scala 811:21]
  wire [31:0] n55_O; // @[Top.scala 811:21]
  wire  n56_clock; // @[Top.scala 814:21]
  wire  n56_reset; // @[Top.scala 814:21]
  wire  n56_valid_up; // @[Top.scala 814:21]
  wire  n56_valid_down; // @[Top.scala 814:21]
  wire [31:0] n56_I; // @[Top.scala 814:21]
  wire [31:0] n56_O; // @[Top.scala 814:21]
  wire  n57_valid_up; // @[Top.scala 817:21]
  wire  n57_valid_down; // @[Top.scala 817:21]
  wire [31:0] n57_I0; // @[Top.scala 817:21]
  wire [31:0] n57_I1; // @[Top.scala 817:21]
  wire [31:0] n57_O_0; // @[Top.scala 817:21]
  wire [31:0] n57_O_1; // @[Top.scala 817:21]
  wire  n64_valid_up; // @[Top.scala 821:21]
  wire  n64_valid_down; // @[Top.scala 821:21]
  wire [31:0] n64_I0_0; // @[Top.scala 821:21]
  wire [31:0] n64_I0_1; // @[Top.scala 821:21]
  wire [31:0] n64_I1; // @[Top.scala 821:21]
  wire [31:0] n64_O_0; // @[Top.scala 821:21]
  wire [31:0] n64_O_1; // @[Top.scala 821:21]
  wire [31:0] n64_O_2; // @[Top.scala 821:21]
  wire  n71_valid_up; // @[Top.scala 825:21]
  wire  n71_valid_down; // @[Top.scala 825:21]
  wire [31:0] n71_I_0; // @[Top.scala 825:21]
  wire [31:0] n71_I_1; // @[Top.scala 825:21]
  wire [31:0] n71_I_2; // @[Top.scala 825:21]
  wire [31:0] n71_O_0_0; // @[Top.scala 825:21]
  wire [31:0] n71_O_0_1; // @[Top.scala 825:21]
  wire [31:0] n71_O_0_2; // @[Top.scala 825:21]
  wire  n76_clock; // @[Top.scala 828:21]
  wire  n76_reset; // @[Top.scala 828:21]
  wire  n76_valid_up; // @[Top.scala 828:21]
  wire  n76_valid_down; // @[Top.scala 828:21]
  wire [31:0] n76_I_0_0; // @[Top.scala 828:21]
  wire [31:0] n76_I_0_1; // @[Top.scala 828:21]
  wire [31:0] n76_I_0_2; // @[Top.scala 828:21]
  wire [31:0] n76_O_0; // @[Top.scala 828:21]
  wire  n77_valid_up; // @[Top.scala 831:21]
  wire  n77_valid_down; // @[Top.scala 831:21]
  wire [31:0] n77_I0_0_0; // @[Top.scala 831:21]
  wire [31:0] n77_I0_0_1; // @[Top.scala 831:21]
  wire [31:0] n77_I1_0; // @[Top.scala 831:21]
  wire [31:0] n77_O_0_0; // @[Top.scala 831:21]
  wire [31:0] n77_O_0_1; // @[Top.scala 831:21]
  wire [31:0] n77_O_0_2; // @[Top.scala 831:21]
  wire  n84_valid_up; // @[Top.scala 835:21]
  wire  n84_valid_down; // @[Top.scala 835:21]
  wire [31:0] n84_I_0_0; // @[Top.scala 835:21]
  wire [31:0] n84_I_0_1; // @[Top.scala 835:21]
  wire [31:0] n84_I_0_2; // @[Top.scala 835:21]
  wire [31:0] n84_O_0_0_0; // @[Top.scala 835:21]
  wire [31:0] n84_O_0_0_1; // @[Top.scala 835:21]
  wire [31:0] n84_O_0_0_2; // @[Top.scala 835:21]
  wire  n89_valid_up; // @[Top.scala 838:21]
  wire  n89_valid_down; // @[Top.scala 838:21]
  wire [31:0] n89_I_0_0_0; // @[Top.scala 838:21]
  wire [31:0] n89_I_0_0_1; // @[Top.scala 838:21]
  wire [31:0] n89_I_0_0_2; // @[Top.scala 838:21]
  wire [31:0] n89_O_0_0; // @[Top.scala 838:21]
  wire [31:0] n89_O_0_1; // @[Top.scala 838:21]
  wire [31:0] n89_O_0_2; // @[Top.scala 838:21]
  wire  n90_valid_up; // @[Top.scala 841:21]
  wire  n90_valid_down; // @[Top.scala 841:21]
  wire [31:0] n90_I_0_0; // @[Top.scala 841:21]
  wire [31:0] n90_I_0_1; // @[Top.scala 841:21]
  wire [31:0] n90_I_0_2; // @[Top.scala 841:21]
  wire [31:0] n90_O_0_0; // @[Top.scala 841:21]
  wire [31:0] n90_O_0_1; // @[Top.scala 841:21]
  wire [31:0] n90_O_0_2; // @[Top.scala 841:21]
  wire  n440_clock; // @[Top.scala 844:22]
  wire  n440_reset; // @[Top.scala 844:22]
  wire  n440_valid_up; // @[Top.scala 844:22]
  wire  n440_valid_down; // @[Top.scala 844:22]
  wire [31:0] n440_I_0_0; // @[Top.scala 844:22]
  wire [31:0] n440_I_0_1; // @[Top.scala 844:22]
  wire [31:0] n440_I_0_2; // @[Top.scala 844:22]
  wire [31:0] n440_O_0_t0b; // @[Top.scala 844:22]
  wire [31:0] n440_O_0_t1b_t0b; // @[Top.scala 844:22]
  wire [31:0] n440_O_0_t1b_t1b; // @[Top.scala 844:22]
  wire  n441_valid_up; // @[Top.scala 847:22]
  wire  n441_valid_down; // @[Top.scala 847:22]
  wire [31:0] n441_I_0_t0b; // @[Top.scala 847:22]
  wire [31:0] n441_I_0_t1b_t0b; // @[Top.scala 847:22]
  wire [31:0] n441_I_0_t1b_t1b; // @[Top.scala 847:22]
  wire [31:0] n441_O_t0b; // @[Top.scala 847:22]
  wire [31:0] n441_O_t1b_t0b; // @[Top.scala 847:22]
  wire [31:0] n441_O_t1b_t1b; // @[Top.scala 847:22]
  wire  n442_valid_up; // @[Top.scala 850:22]
  wire  n442_valid_down; // @[Top.scala 850:22]
  wire [31:0] n442_I_t0b; // @[Top.scala 850:22]
  wire [31:0] n442_I_t1b_t0b; // @[Top.scala 850:22]
  wire [31:0] n442_I_t1b_t1b; // @[Top.scala 850:22]
  wire [31:0] n442_O_t0b; // @[Top.scala 850:22]
  wire [31:0] n442_O_t1b_t0b; // @[Top.scala 850:22]
  wire [31:0] n442_O_t1b_t1b; // @[Top.scala 850:22]
  wire  n447_valid_up; // @[Top.scala 853:22]
  wire  n447_valid_down; // @[Top.scala 853:22]
  wire [31:0] n447_I_t0b; // @[Top.scala 853:22]
  wire [31:0] n447_O; // @[Top.scala 853:22]
  wire  n448_clock; // @[Top.scala 856:22]
  wire  n448_reset; // @[Top.scala 856:22]
  wire  n448_valid_up; // @[Top.scala 856:22]
  wire  n448_valid_down; // @[Top.scala 856:22]
  wire [31:0] n448_I; // @[Top.scala 856:22]
  wire [31:0] n448_O; // @[Top.scala 856:22]
  wire  n449_clock; // @[Top.scala 859:22]
  wire  n449_reset; // @[Top.scala 859:22]
  wire  n449_valid_up; // @[Top.scala 859:22]
  wire  n449_valid_down; // @[Top.scala 859:22]
  wire [31:0] n449_I; // @[Top.scala 859:22]
  wire [31:0] n449_O; // @[Top.scala 859:22]
  wire  n450_clock; // @[Top.scala 862:22]
  wire  n450_reset; // @[Top.scala 862:22]
  wire  n450_valid_up; // @[Top.scala 862:22]
  wire  n450_valid_down; // @[Top.scala 862:22]
  wire [31:0] n450_I; // @[Top.scala 862:22]
  wire [31:0] n450_O; // @[Top.scala 862:22]
  wire  n451_clock; // @[Top.scala 865:22]
  wire  n451_reset; // @[Top.scala 865:22]
  wire  n451_valid_up; // @[Top.scala 865:22]
  wire  n451_valid_down; // @[Top.scala 865:22]
  wire [31:0] n451_I; // @[Top.scala 865:22]
  wire [31:0] n451_O; // @[Top.scala 865:22]
  wire  n452_valid_up; // @[Top.scala 868:22]
  wire  n452_valid_down; // @[Top.scala 868:22]
  wire [31:0] n452_I0; // @[Top.scala 868:22]
  wire [31:0] n452_I1; // @[Top.scala 868:22]
  wire [31:0] n452_O_0; // @[Top.scala 868:22]
  wire [31:0] n452_O_1; // @[Top.scala 868:22]
  wire  n459_valid_up; // @[Top.scala 872:22]
  wire  n459_valid_down; // @[Top.scala 872:22]
  wire [31:0] n459_I0_0; // @[Top.scala 872:22]
  wire [31:0] n459_I0_1; // @[Top.scala 872:22]
  wire [31:0] n459_I1; // @[Top.scala 872:22]
  wire [31:0] n459_O_0; // @[Top.scala 872:22]
  wire [31:0] n459_O_1; // @[Top.scala 872:22]
  wire [31:0] n459_O_2; // @[Top.scala 872:22]
  wire  n466_valid_up; // @[Top.scala 876:22]
  wire  n466_valid_down; // @[Top.scala 876:22]
  wire [31:0] n466_I_0; // @[Top.scala 876:22]
  wire [31:0] n466_I_1; // @[Top.scala 876:22]
  wire [31:0] n466_I_2; // @[Top.scala 876:22]
  wire [31:0] n466_O_0_0; // @[Top.scala 876:22]
  wire [31:0] n466_O_0_1; // @[Top.scala 876:22]
  wire [31:0] n466_O_0_2; // @[Top.scala 876:22]
  wire  n471_clock; // @[Top.scala 879:22]
  wire  n471_reset; // @[Top.scala 879:22]
  wire  n471_valid_up; // @[Top.scala 879:22]
  wire  n471_valid_down; // @[Top.scala 879:22]
  wire [31:0] n471_I_0_0; // @[Top.scala 879:22]
  wire [31:0] n471_I_0_1; // @[Top.scala 879:22]
  wire [31:0] n471_I_0_2; // @[Top.scala 879:22]
  wire [31:0] n471_O_0; // @[Top.scala 879:22]
  wire  n472_clock; // @[Top.scala 882:22]
  wire  n472_reset; // @[Top.scala 882:22]
  wire  n472_valid_up; // @[Top.scala 882:22]
  wire  n472_valid_down; // @[Top.scala 882:22]
  wire [31:0] n472_I; // @[Top.scala 882:22]
  wire [31:0] n472_O; // @[Top.scala 882:22]
  wire  n473_clock; // @[Top.scala 885:22]
  wire  n473_reset; // @[Top.scala 885:22]
  wire  n473_valid_up; // @[Top.scala 885:22]
  wire  n473_valid_down; // @[Top.scala 885:22]
  wire [31:0] n473_I; // @[Top.scala 885:22]
  wire [31:0] n473_O; // @[Top.scala 885:22]
  wire  n474_valid_up; // @[Top.scala 888:22]
  wire  n474_valid_down; // @[Top.scala 888:22]
  wire [31:0] n474_I0; // @[Top.scala 888:22]
  wire [31:0] n474_I1; // @[Top.scala 888:22]
  wire [31:0] n474_O_0; // @[Top.scala 888:22]
  wire [31:0] n474_O_1; // @[Top.scala 888:22]
  wire  n481_valid_up; // @[Top.scala 892:22]
  wire  n481_valid_down; // @[Top.scala 892:22]
  wire [31:0] n481_I0_0; // @[Top.scala 892:22]
  wire [31:0] n481_I0_1; // @[Top.scala 892:22]
  wire [31:0] n481_I1; // @[Top.scala 892:22]
  wire [31:0] n481_O_0; // @[Top.scala 892:22]
  wire [31:0] n481_O_1; // @[Top.scala 892:22]
  wire [31:0] n481_O_2; // @[Top.scala 892:22]
  wire  n488_valid_up; // @[Top.scala 896:22]
  wire  n488_valid_down; // @[Top.scala 896:22]
  wire [31:0] n488_I_0; // @[Top.scala 896:22]
  wire [31:0] n488_I_1; // @[Top.scala 896:22]
  wire [31:0] n488_I_2; // @[Top.scala 896:22]
  wire [31:0] n488_O_0_0; // @[Top.scala 896:22]
  wire [31:0] n488_O_0_1; // @[Top.scala 896:22]
  wire [31:0] n488_O_0_2; // @[Top.scala 896:22]
  wire  n493_clock; // @[Top.scala 899:22]
  wire  n493_reset; // @[Top.scala 899:22]
  wire  n493_valid_up; // @[Top.scala 899:22]
  wire  n493_valid_down; // @[Top.scala 899:22]
  wire [31:0] n493_I_0_0; // @[Top.scala 899:22]
  wire [31:0] n493_I_0_1; // @[Top.scala 899:22]
  wire [31:0] n493_I_0_2; // @[Top.scala 899:22]
  wire [31:0] n493_O_0; // @[Top.scala 899:22]
  wire  n494_valid_up; // @[Top.scala 902:22]
  wire  n494_valid_down; // @[Top.scala 902:22]
  wire [31:0] n494_I0_0; // @[Top.scala 902:22]
  wire [31:0] n494_I1_0; // @[Top.scala 902:22]
  wire [31:0] n494_O_0_0; // @[Top.scala 902:22]
  wire [31:0] n494_O_0_1; // @[Top.scala 902:22]
  wire  n501_clock; // @[Top.scala 906:22]
  wire  n501_reset; // @[Top.scala 906:22]
  wire  n501_valid_up; // @[Top.scala 906:22]
  wire  n501_valid_down; // @[Top.scala 906:22]
  wire [31:0] n501_I; // @[Top.scala 906:22]
  wire [31:0] n501_O; // @[Top.scala 906:22]
  wire  n502_clock; // @[Top.scala 909:22]
  wire  n502_reset; // @[Top.scala 909:22]
  wire  n502_valid_up; // @[Top.scala 909:22]
  wire  n502_valid_down; // @[Top.scala 909:22]
  wire [31:0] n502_I; // @[Top.scala 909:22]
  wire [31:0] n502_O; // @[Top.scala 909:22]
  wire  n503_valid_up; // @[Top.scala 912:22]
  wire  n503_valid_down; // @[Top.scala 912:22]
  wire [31:0] n503_I0; // @[Top.scala 912:22]
  wire [31:0] n503_I1; // @[Top.scala 912:22]
  wire [31:0] n503_O_0; // @[Top.scala 912:22]
  wire [31:0] n503_O_1; // @[Top.scala 912:22]
  wire  n510_valid_up; // @[Top.scala 916:22]
  wire  n510_valid_down; // @[Top.scala 916:22]
  wire [31:0] n510_I0_0; // @[Top.scala 916:22]
  wire [31:0] n510_I0_1; // @[Top.scala 916:22]
  wire [31:0] n510_I1; // @[Top.scala 916:22]
  wire [31:0] n510_O_0; // @[Top.scala 916:22]
  wire [31:0] n510_O_1; // @[Top.scala 916:22]
  wire [31:0] n510_O_2; // @[Top.scala 916:22]
  wire  n517_valid_up; // @[Top.scala 920:22]
  wire  n517_valid_down; // @[Top.scala 920:22]
  wire [31:0] n517_I_0; // @[Top.scala 920:22]
  wire [31:0] n517_I_1; // @[Top.scala 920:22]
  wire [31:0] n517_I_2; // @[Top.scala 920:22]
  wire [31:0] n517_O_0_0; // @[Top.scala 920:22]
  wire [31:0] n517_O_0_1; // @[Top.scala 920:22]
  wire [31:0] n517_O_0_2; // @[Top.scala 920:22]
  wire  n522_clock; // @[Top.scala 923:22]
  wire  n522_reset; // @[Top.scala 923:22]
  wire  n522_valid_up; // @[Top.scala 923:22]
  wire  n522_valid_down; // @[Top.scala 923:22]
  wire [31:0] n522_I_0_0; // @[Top.scala 923:22]
  wire [31:0] n522_I_0_1; // @[Top.scala 923:22]
  wire [31:0] n522_I_0_2; // @[Top.scala 923:22]
  wire [31:0] n522_O_0; // @[Top.scala 923:22]
  wire  n523_valid_up; // @[Top.scala 926:22]
  wire  n523_valid_down; // @[Top.scala 926:22]
  wire [31:0] n523_I0_0_0; // @[Top.scala 926:22]
  wire [31:0] n523_I0_0_1; // @[Top.scala 926:22]
  wire [31:0] n523_I1_0; // @[Top.scala 926:22]
  wire [31:0] n523_O_0_0; // @[Top.scala 926:22]
  wire [31:0] n523_O_0_1; // @[Top.scala 926:22]
  wire [31:0] n523_O_0_2; // @[Top.scala 926:22]
  wire  n530_valid_up; // @[Top.scala 930:22]
  wire  n530_valid_down; // @[Top.scala 930:22]
  wire [31:0] n530_I_0_0; // @[Top.scala 930:22]
  wire [31:0] n530_I_0_1; // @[Top.scala 930:22]
  wire [31:0] n530_I_0_2; // @[Top.scala 930:22]
  wire [31:0] n530_O_0_0; // @[Top.scala 930:22]
  wire [31:0] n530_O_0_1; // @[Top.scala 930:22]
  wire [31:0] n530_O_0_2; // @[Top.scala 930:22]
  wire  n533_valid_up; // @[Top.scala 933:22]
  wire  n533_valid_down; // @[Top.scala 933:22]
  wire [31:0] n533_I_0_0; // @[Top.scala 933:22]
  wire [31:0] n533_I_0_1; // @[Top.scala 933:22]
  wire [31:0] n533_I_0_2; // @[Top.scala 933:22]
  wire [31:0] n533_O_0; // @[Top.scala 933:22]
  wire [31:0] n533_O_1; // @[Top.scala 933:22]
  wire [31:0] n533_O_2; // @[Top.scala 933:22]
  wire  n586_clock; // @[Top.scala 936:22]
  wire  n586_reset; // @[Top.scala 936:22]
  wire  n586_valid_up; // @[Top.scala 936:22]
  wire  n586_valid_down; // @[Top.scala 936:22]
  wire [31:0] n586_I_0; // @[Top.scala 936:22]
  wire [31:0] n586_I_1; // @[Top.scala 936:22]
  wire [31:0] n586_I_2; // @[Top.scala 936:22]
  wire [31:0] n586_O; // @[Top.scala 936:22]
  wire  n587_valid_up; // @[Top.scala 939:22]
  wire  n587_valid_down; // @[Top.scala 939:22]
  wire [31:0] n587_I; // @[Top.scala 939:22]
  wire [31:0] n587_O; // @[Top.scala 939:22]
  wire  n588_clock; // @[Top.scala 942:22]
  wire  n588_reset; // @[Top.scala 942:22]
  wire  n588_valid_up; // @[Top.scala 942:22]
  wire  n588_valid_down; // @[Top.scala 942:22]
  wire [31:0] n588_I; // @[Top.scala 942:22]
  wire [31:0] n588_O; // @[Top.scala 942:22]
  wire  n589_clock; // @[Top.scala 945:22]
  wire  n589_reset; // @[Top.scala 945:22]
  wire  n589_valid_up; // @[Top.scala 945:22]
  wire  n589_valid_down; // @[Top.scala 945:22]
  wire [31:0] n589_I0; // @[Top.scala 945:22]
  wire [31:0] n589_I1; // @[Top.scala 945:22]
  wire [31:0] n589_O; // @[Top.scala 945:22]
  wire  n625_valid_up; // @[Top.scala 949:22]
  wire  n625_valid_down; // @[Top.scala 949:22]
  wire [31:0] n625_I_t1b_t0b; // @[Top.scala 949:22]
  wire [31:0] n625_I_t1b_t1b; // @[Top.scala 949:22]
  wire [31:0] n625_O; // @[Top.scala 949:22]
  wire  n626_clock; // @[Top.scala 952:22]
  wire  n626_reset; // @[Top.scala 952:22]
  wire  n626_valid_up; // @[Top.scala 952:22]
  wire  n626_valid_down; // @[Top.scala 952:22]
  wire [31:0] n626_I; // @[Top.scala 952:22]
  wire [31:0] n626_O; // @[Top.scala 952:22]
  wire  n627_clock; // @[Top.scala 955:22]
  wire  n627_reset; // @[Top.scala 955:22]
  wire  n627_valid_up; // @[Top.scala 955:22]
  wire  n627_valid_down; // @[Top.scala 955:22]
  wire [31:0] n627_I; // @[Top.scala 955:22]
  wire [31:0] n627_O; // @[Top.scala 955:22]
  wire  n628_clock; // @[Top.scala 958:22]
  wire  n628_reset; // @[Top.scala 958:22]
  wire  n628_valid_up; // @[Top.scala 958:22]
  wire  n628_valid_down; // @[Top.scala 958:22]
  wire [31:0] n628_I; // @[Top.scala 958:22]
  wire [31:0] n628_O; // @[Top.scala 958:22]
  wire  n629_clock; // @[Top.scala 961:22]
  wire  n629_reset; // @[Top.scala 961:22]
  wire  n629_valid_up; // @[Top.scala 961:22]
  wire  n629_valid_down; // @[Top.scala 961:22]
  wire [31:0] n629_I; // @[Top.scala 961:22]
  wire [31:0] n629_O; // @[Top.scala 961:22]
  wire  n630_valid_up; // @[Top.scala 964:22]
  wire  n630_valid_down; // @[Top.scala 964:22]
  wire [31:0] n630_I0; // @[Top.scala 964:22]
  wire [31:0] n630_I1; // @[Top.scala 964:22]
  wire [31:0] n630_O_0; // @[Top.scala 964:22]
  wire [31:0] n630_O_1; // @[Top.scala 964:22]
  wire  n637_valid_up; // @[Top.scala 968:22]
  wire  n637_valid_down; // @[Top.scala 968:22]
  wire [31:0] n637_I0_0; // @[Top.scala 968:22]
  wire [31:0] n637_I0_1; // @[Top.scala 968:22]
  wire [31:0] n637_I1; // @[Top.scala 968:22]
  wire [31:0] n637_O_0; // @[Top.scala 968:22]
  wire [31:0] n637_O_1; // @[Top.scala 968:22]
  wire [31:0] n637_O_2; // @[Top.scala 968:22]
  wire  n644_valid_up; // @[Top.scala 972:22]
  wire  n644_valid_down; // @[Top.scala 972:22]
  wire [31:0] n644_I_0; // @[Top.scala 972:22]
  wire [31:0] n644_I_1; // @[Top.scala 972:22]
  wire [31:0] n644_I_2; // @[Top.scala 972:22]
  wire [31:0] n644_O_0_0; // @[Top.scala 972:22]
  wire [31:0] n644_O_0_1; // @[Top.scala 972:22]
  wire [31:0] n644_O_0_2; // @[Top.scala 972:22]
  wire  n649_clock; // @[Top.scala 975:22]
  wire  n649_reset; // @[Top.scala 975:22]
  wire  n649_valid_up; // @[Top.scala 975:22]
  wire  n649_valid_down; // @[Top.scala 975:22]
  wire [31:0] n649_I_0_0; // @[Top.scala 975:22]
  wire [31:0] n649_I_0_1; // @[Top.scala 975:22]
  wire [31:0] n649_I_0_2; // @[Top.scala 975:22]
  wire [31:0] n649_O_0; // @[Top.scala 975:22]
  wire  n650_clock; // @[Top.scala 978:22]
  wire  n650_reset; // @[Top.scala 978:22]
  wire  n650_valid_up; // @[Top.scala 978:22]
  wire  n650_valid_down; // @[Top.scala 978:22]
  wire [31:0] n650_I; // @[Top.scala 978:22]
  wire [31:0] n650_O; // @[Top.scala 978:22]
  wire  n651_clock; // @[Top.scala 981:22]
  wire  n651_reset; // @[Top.scala 981:22]
  wire  n651_valid_up; // @[Top.scala 981:22]
  wire  n651_valid_down; // @[Top.scala 981:22]
  wire [31:0] n651_I; // @[Top.scala 981:22]
  wire [31:0] n651_O; // @[Top.scala 981:22]
  wire  n652_valid_up; // @[Top.scala 984:22]
  wire  n652_valid_down; // @[Top.scala 984:22]
  wire [31:0] n652_I0; // @[Top.scala 984:22]
  wire [31:0] n652_I1; // @[Top.scala 984:22]
  wire [31:0] n652_O_0; // @[Top.scala 984:22]
  wire [31:0] n652_O_1; // @[Top.scala 984:22]
  wire  n659_valid_up; // @[Top.scala 988:22]
  wire  n659_valid_down; // @[Top.scala 988:22]
  wire [31:0] n659_I0_0; // @[Top.scala 988:22]
  wire [31:0] n659_I0_1; // @[Top.scala 988:22]
  wire [31:0] n659_I1; // @[Top.scala 988:22]
  wire [31:0] n659_O_0; // @[Top.scala 988:22]
  wire [31:0] n659_O_1; // @[Top.scala 988:22]
  wire [31:0] n659_O_2; // @[Top.scala 988:22]
  wire  n666_valid_up; // @[Top.scala 992:22]
  wire  n666_valid_down; // @[Top.scala 992:22]
  wire [31:0] n666_I_0; // @[Top.scala 992:22]
  wire [31:0] n666_I_1; // @[Top.scala 992:22]
  wire [31:0] n666_I_2; // @[Top.scala 992:22]
  wire [31:0] n666_O_0_0; // @[Top.scala 992:22]
  wire [31:0] n666_O_0_1; // @[Top.scala 992:22]
  wire [31:0] n666_O_0_2; // @[Top.scala 992:22]
  wire  n671_clock; // @[Top.scala 995:22]
  wire  n671_reset; // @[Top.scala 995:22]
  wire  n671_valid_up; // @[Top.scala 995:22]
  wire  n671_valid_down; // @[Top.scala 995:22]
  wire [31:0] n671_I_0_0; // @[Top.scala 995:22]
  wire [31:0] n671_I_0_1; // @[Top.scala 995:22]
  wire [31:0] n671_I_0_2; // @[Top.scala 995:22]
  wire [31:0] n671_O_0; // @[Top.scala 995:22]
  wire  n672_valid_up; // @[Top.scala 998:22]
  wire  n672_valid_down; // @[Top.scala 998:22]
  wire [31:0] n672_I0_0; // @[Top.scala 998:22]
  wire [31:0] n672_I1_0; // @[Top.scala 998:22]
  wire [31:0] n672_O_0_0; // @[Top.scala 998:22]
  wire [31:0] n672_O_0_1; // @[Top.scala 998:22]
  wire  n679_clock; // @[Top.scala 1002:22]
  wire  n679_reset; // @[Top.scala 1002:22]
  wire  n679_valid_up; // @[Top.scala 1002:22]
  wire  n679_valid_down; // @[Top.scala 1002:22]
  wire [31:0] n679_I; // @[Top.scala 1002:22]
  wire [31:0] n679_O; // @[Top.scala 1002:22]
  wire  n680_clock; // @[Top.scala 1005:22]
  wire  n680_reset; // @[Top.scala 1005:22]
  wire  n680_valid_up; // @[Top.scala 1005:22]
  wire  n680_valid_down; // @[Top.scala 1005:22]
  wire [31:0] n680_I; // @[Top.scala 1005:22]
  wire [31:0] n680_O; // @[Top.scala 1005:22]
  wire  n681_valid_up; // @[Top.scala 1008:22]
  wire  n681_valid_down; // @[Top.scala 1008:22]
  wire [31:0] n681_I0; // @[Top.scala 1008:22]
  wire [31:0] n681_I1; // @[Top.scala 1008:22]
  wire [31:0] n681_O_0; // @[Top.scala 1008:22]
  wire [31:0] n681_O_1; // @[Top.scala 1008:22]
  wire  n688_valid_up; // @[Top.scala 1012:22]
  wire  n688_valid_down; // @[Top.scala 1012:22]
  wire [31:0] n688_I0_0; // @[Top.scala 1012:22]
  wire [31:0] n688_I0_1; // @[Top.scala 1012:22]
  wire [31:0] n688_I1; // @[Top.scala 1012:22]
  wire [31:0] n688_O_0; // @[Top.scala 1012:22]
  wire [31:0] n688_O_1; // @[Top.scala 1012:22]
  wire [31:0] n688_O_2; // @[Top.scala 1012:22]
  wire  n695_valid_up; // @[Top.scala 1016:22]
  wire  n695_valid_down; // @[Top.scala 1016:22]
  wire [31:0] n695_I_0; // @[Top.scala 1016:22]
  wire [31:0] n695_I_1; // @[Top.scala 1016:22]
  wire [31:0] n695_I_2; // @[Top.scala 1016:22]
  wire [31:0] n695_O_0_0; // @[Top.scala 1016:22]
  wire [31:0] n695_O_0_1; // @[Top.scala 1016:22]
  wire [31:0] n695_O_0_2; // @[Top.scala 1016:22]
  wire  n700_clock; // @[Top.scala 1019:22]
  wire  n700_reset; // @[Top.scala 1019:22]
  wire  n700_valid_up; // @[Top.scala 1019:22]
  wire  n700_valid_down; // @[Top.scala 1019:22]
  wire [31:0] n700_I_0_0; // @[Top.scala 1019:22]
  wire [31:0] n700_I_0_1; // @[Top.scala 1019:22]
  wire [31:0] n700_I_0_2; // @[Top.scala 1019:22]
  wire [31:0] n700_O_0; // @[Top.scala 1019:22]
  wire  n701_valid_up; // @[Top.scala 1022:22]
  wire  n701_valid_down; // @[Top.scala 1022:22]
  wire [31:0] n701_I0_0_0; // @[Top.scala 1022:22]
  wire [31:0] n701_I0_0_1; // @[Top.scala 1022:22]
  wire [31:0] n701_I1_0; // @[Top.scala 1022:22]
  wire [31:0] n701_O_0_0; // @[Top.scala 1022:22]
  wire [31:0] n701_O_0_1; // @[Top.scala 1022:22]
  wire [31:0] n701_O_0_2; // @[Top.scala 1022:22]
  wire  n708_valid_up; // @[Top.scala 1026:22]
  wire  n708_valid_down; // @[Top.scala 1026:22]
  wire [31:0] n708_I_0_0; // @[Top.scala 1026:22]
  wire [31:0] n708_I_0_1; // @[Top.scala 1026:22]
  wire [31:0] n708_I_0_2; // @[Top.scala 1026:22]
  wire [31:0] n708_O_0_0; // @[Top.scala 1026:22]
  wire [31:0] n708_O_0_1; // @[Top.scala 1026:22]
  wire [31:0] n708_O_0_2; // @[Top.scala 1026:22]
  wire  n711_valid_up; // @[Top.scala 1029:22]
  wire  n711_valid_down; // @[Top.scala 1029:22]
  wire [31:0] n711_I_0_0; // @[Top.scala 1029:22]
  wire [31:0] n711_I_0_1; // @[Top.scala 1029:22]
  wire [31:0] n711_I_0_2; // @[Top.scala 1029:22]
  wire [31:0] n711_O_0; // @[Top.scala 1029:22]
  wire [31:0] n711_O_1; // @[Top.scala 1029:22]
  wire [31:0] n711_O_2; // @[Top.scala 1029:22]
  wire  n764_clock; // @[Top.scala 1032:22]
  wire  n764_reset; // @[Top.scala 1032:22]
  wire  n764_valid_up; // @[Top.scala 1032:22]
  wire  n764_valid_down; // @[Top.scala 1032:22]
  wire [31:0] n764_I_0; // @[Top.scala 1032:22]
  wire [31:0] n764_I_1; // @[Top.scala 1032:22]
  wire [31:0] n764_I_2; // @[Top.scala 1032:22]
  wire [31:0] n764_O; // @[Top.scala 1032:22]
  wire  n765_valid_up; // @[Top.scala 1035:22]
  wire  n765_valid_down; // @[Top.scala 1035:22]
  wire [31:0] n765_I; // @[Top.scala 1035:22]
  wire [31:0] n765_O; // @[Top.scala 1035:22]
  wire  n766_clock; // @[Top.scala 1038:22]
  wire  n766_reset; // @[Top.scala 1038:22]
  wire  n766_valid_up; // @[Top.scala 1038:22]
  wire  n766_valid_down; // @[Top.scala 1038:22]
  wire [31:0] n766_I; // @[Top.scala 1038:22]
  wire [31:0] n766_O; // @[Top.scala 1038:22]
  wire  n767_clock; // @[Top.scala 1041:22]
  wire  n767_reset; // @[Top.scala 1041:22]
  wire  n767_valid_up; // @[Top.scala 1041:22]
  wire  n767_valid_down; // @[Top.scala 1041:22]
  wire [31:0] n767_I0; // @[Top.scala 1041:22]
  wire [31:0] n767_I1; // @[Top.scala 1041:22]
  wire [31:0] n767_O; // @[Top.scala 1041:22]
  wire  n803_valid_up; // @[Top.scala 1045:22]
  wire  n803_valid_down; // @[Top.scala 1045:22]
  wire [31:0] n803_I_t1b_t0b; // @[Top.scala 1045:22]
  wire [31:0] n803_I_t1b_t1b; // @[Top.scala 1045:22]
  wire [31:0] n803_O; // @[Top.scala 1045:22]
  wire  n804_clock; // @[Top.scala 1048:22]
  wire  n804_reset; // @[Top.scala 1048:22]
  wire  n804_valid_up; // @[Top.scala 1048:22]
  wire  n804_valid_down; // @[Top.scala 1048:22]
  wire [31:0] n804_I; // @[Top.scala 1048:22]
  wire [31:0] n804_O; // @[Top.scala 1048:22]
  wire  n805_clock; // @[Top.scala 1051:22]
  wire  n805_reset; // @[Top.scala 1051:22]
  wire  n805_valid_up; // @[Top.scala 1051:22]
  wire  n805_valid_down; // @[Top.scala 1051:22]
  wire [31:0] n805_I; // @[Top.scala 1051:22]
  wire [31:0] n805_O; // @[Top.scala 1051:22]
  wire  n806_clock; // @[Top.scala 1054:22]
  wire  n806_reset; // @[Top.scala 1054:22]
  wire  n806_valid_up; // @[Top.scala 1054:22]
  wire  n806_valid_down; // @[Top.scala 1054:22]
  wire [31:0] n806_I; // @[Top.scala 1054:22]
  wire [31:0] n806_O; // @[Top.scala 1054:22]
  wire  n807_clock; // @[Top.scala 1057:22]
  wire  n807_reset; // @[Top.scala 1057:22]
  wire  n807_valid_up; // @[Top.scala 1057:22]
  wire  n807_valid_down; // @[Top.scala 1057:22]
  wire [31:0] n807_I; // @[Top.scala 1057:22]
  wire [31:0] n807_O; // @[Top.scala 1057:22]
  wire  n808_valid_up; // @[Top.scala 1060:22]
  wire  n808_valid_down; // @[Top.scala 1060:22]
  wire [31:0] n808_I0; // @[Top.scala 1060:22]
  wire [31:0] n808_I1; // @[Top.scala 1060:22]
  wire [31:0] n808_O_0; // @[Top.scala 1060:22]
  wire [31:0] n808_O_1; // @[Top.scala 1060:22]
  wire  n815_valid_up; // @[Top.scala 1064:22]
  wire  n815_valid_down; // @[Top.scala 1064:22]
  wire [31:0] n815_I0_0; // @[Top.scala 1064:22]
  wire [31:0] n815_I0_1; // @[Top.scala 1064:22]
  wire [31:0] n815_I1; // @[Top.scala 1064:22]
  wire [31:0] n815_O_0; // @[Top.scala 1064:22]
  wire [31:0] n815_O_1; // @[Top.scala 1064:22]
  wire [31:0] n815_O_2; // @[Top.scala 1064:22]
  wire  n822_valid_up; // @[Top.scala 1068:22]
  wire  n822_valid_down; // @[Top.scala 1068:22]
  wire [31:0] n822_I_0; // @[Top.scala 1068:22]
  wire [31:0] n822_I_1; // @[Top.scala 1068:22]
  wire [31:0] n822_I_2; // @[Top.scala 1068:22]
  wire [31:0] n822_O_0_0; // @[Top.scala 1068:22]
  wire [31:0] n822_O_0_1; // @[Top.scala 1068:22]
  wire [31:0] n822_O_0_2; // @[Top.scala 1068:22]
  wire  n827_clock; // @[Top.scala 1071:22]
  wire  n827_reset; // @[Top.scala 1071:22]
  wire  n827_valid_up; // @[Top.scala 1071:22]
  wire  n827_valid_down; // @[Top.scala 1071:22]
  wire [31:0] n827_I_0_0; // @[Top.scala 1071:22]
  wire [31:0] n827_I_0_1; // @[Top.scala 1071:22]
  wire [31:0] n827_I_0_2; // @[Top.scala 1071:22]
  wire [31:0] n827_O_0; // @[Top.scala 1071:22]
  wire  n828_clock; // @[Top.scala 1074:22]
  wire  n828_reset; // @[Top.scala 1074:22]
  wire  n828_valid_up; // @[Top.scala 1074:22]
  wire  n828_valid_down; // @[Top.scala 1074:22]
  wire [31:0] n828_I; // @[Top.scala 1074:22]
  wire [31:0] n828_O; // @[Top.scala 1074:22]
  wire  n829_clock; // @[Top.scala 1077:22]
  wire  n829_reset; // @[Top.scala 1077:22]
  wire  n829_valid_up; // @[Top.scala 1077:22]
  wire  n829_valid_down; // @[Top.scala 1077:22]
  wire [31:0] n829_I; // @[Top.scala 1077:22]
  wire [31:0] n829_O; // @[Top.scala 1077:22]
  wire  n830_valid_up; // @[Top.scala 1080:22]
  wire  n830_valid_down; // @[Top.scala 1080:22]
  wire [31:0] n830_I0; // @[Top.scala 1080:22]
  wire [31:0] n830_I1; // @[Top.scala 1080:22]
  wire [31:0] n830_O_0; // @[Top.scala 1080:22]
  wire [31:0] n830_O_1; // @[Top.scala 1080:22]
  wire  n837_valid_up; // @[Top.scala 1084:22]
  wire  n837_valid_down; // @[Top.scala 1084:22]
  wire [31:0] n837_I0_0; // @[Top.scala 1084:22]
  wire [31:0] n837_I0_1; // @[Top.scala 1084:22]
  wire [31:0] n837_I1; // @[Top.scala 1084:22]
  wire [31:0] n837_O_0; // @[Top.scala 1084:22]
  wire [31:0] n837_O_1; // @[Top.scala 1084:22]
  wire [31:0] n837_O_2; // @[Top.scala 1084:22]
  wire  n844_valid_up; // @[Top.scala 1088:22]
  wire  n844_valid_down; // @[Top.scala 1088:22]
  wire [31:0] n844_I_0; // @[Top.scala 1088:22]
  wire [31:0] n844_I_1; // @[Top.scala 1088:22]
  wire [31:0] n844_I_2; // @[Top.scala 1088:22]
  wire [31:0] n844_O_0_0; // @[Top.scala 1088:22]
  wire [31:0] n844_O_0_1; // @[Top.scala 1088:22]
  wire [31:0] n844_O_0_2; // @[Top.scala 1088:22]
  wire  n849_clock; // @[Top.scala 1091:22]
  wire  n849_reset; // @[Top.scala 1091:22]
  wire  n849_valid_up; // @[Top.scala 1091:22]
  wire  n849_valid_down; // @[Top.scala 1091:22]
  wire [31:0] n849_I_0_0; // @[Top.scala 1091:22]
  wire [31:0] n849_I_0_1; // @[Top.scala 1091:22]
  wire [31:0] n849_I_0_2; // @[Top.scala 1091:22]
  wire [31:0] n849_O_0; // @[Top.scala 1091:22]
  wire  n850_valid_up; // @[Top.scala 1094:22]
  wire  n850_valid_down; // @[Top.scala 1094:22]
  wire [31:0] n850_I0_0; // @[Top.scala 1094:22]
  wire [31:0] n850_I1_0; // @[Top.scala 1094:22]
  wire [31:0] n850_O_0_0; // @[Top.scala 1094:22]
  wire [31:0] n850_O_0_1; // @[Top.scala 1094:22]
  wire  n857_clock; // @[Top.scala 1098:22]
  wire  n857_reset; // @[Top.scala 1098:22]
  wire  n857_valid_up; // @[Top.scala 1098:22]
  wire  n857_valid_down; // @[Top.scala 1098:22]
  wire [31:0] n857_I; // @[Top.scala 1098:22]
  wire [31:0] n857_O; // @[Top.scala 1098:22]
  wire  n858_clock; // @[Top.scala 1101:22]
  wire  n858_reset; // @[Top.scala 1101:22]
  wire  n858_valid_up; // @[Top.scala 1101:22]
  wire  n858_valid_down; // @[Top.scala 1101:22]
  wire [31:0] n858_I; // @[Top.scala 1101:22]
  wire [31:0] n858_O; // @[Top.scala 1101:22]
  wire  n859_valid_up; // @[Top.scala 1104:22]
  wire  n859_valid_down; // @[Top.scala 1104:22]
  wire [31:0] n859_I0; // @[Top.scala 1104:22]
  wire [31:0] n859_I1; // @[Top.scala 1104:22]
  wire [31:0] n859_O_0; // @[Top.scala 1104:22]
  wire [31:0] n859_O_1; // @[Top.scala 1104:22]
  wire  n866_valid_up; // @[Top.scala 1108:22]
  wire  n866_valid_down; // @[Top.scala 1108:22]
  wire [31:0] n866_I0_0; // @[Top.scala 1108:22]
  wire [31:0] n866_I0_1; // @[Top.scala 1108:22]
  wire [31:0] n866_I1; // @[Top.scala 1108:22]
  wire [31:0] n866_O_0; // @[Top.scala 1108:22]
  wire [31:0] n866_O_1; // @[Top.scala 1108:22]
  wire [31:0] n866_O_2; // @[Top.scala 1108:22]
  wire  n873_valid_up; // @[Top.scala 1112:22]
  wire  n873_valid_down; // @[Top.scala 1112:22]
  wire [31:0] n873_I_0; // @[Top.scala 1112:22]
  wire [31:0] n873_I_1; // @[Top.scala 1112:22]
  wire [31:0] n873_I_2; // @[Top.scala 1112:22]
  wire [31:0] n873_O_0_0; // @[Top.scala 1112:22]
  wire [31:0] n873_O_0_1; // @[Top.scala 1112:22]
  wire [31:0] n873_O_0_2; // @[Top.scala 1112:22]
  wire  n878_clock; // @[Top.scala 1115:22]
  wire  n878_reset; // @[Top.scala 1115:22]
  wire  n878_valid_up; // @[Top.scala 1115:22]
  wire  n878_valid_down; // @[Top.scala 1115:22]
  wire [31:0] n878_I_0_0; // @[Top.scala 1115:22]
  wire [31:0] n878_I_0_1; // @[Top.scala 1115:22]
  wire [31:0] n878_I_0_2; // @[Top.scala 1115:22]
  wire [31:0] n878_O_0; // @[Top.scala 1115:22]
  wire  n879_valid_up; // @[Top.scala 1118:22]
  wire  n879_valid_down; // @[Top.scala 1118:22]
  wire [31:0] n879_I0_0_0; // @[Top.scala 1118:22]
  wire [31:0] n879_I0_0_1; // @[Top.scala 1118:22]
  wire [31:0] n879_I1_0; // @[Top.scala 1118:22]
  wire [31:0] n879_O_0_0; // @[Top.scala 1118:22]
  wire [31:0] n879_O_0_1; // @[Top.scala 1118:22]
  wire [31:0] n879_O_0_2; // @[Top.scala 1118:22]
  wire  n886_valid_up; // @[Top.scala 1122:22]
  wire  n886_valid_down; // @[Top.scala 1122:22]
  wire [31:0] n886_I_0_0; // @[Top.scala 1122:22]
  wire [31:0] n886_I_0_1; // @[Top.scala 1122:22]
  wire [31:0] n886_I_0_2; // @[Top.scala 1122:22]
  wire [31:0] n886_O_0_0; // @[Top.scala 1122:22]
  wire [31:0] n886_O_0_1; // @[Top.scala 1122:22]
  wire [31:0] n886_O_0_2; // @[Top.scala 1122:22]
  wire  n889_valid_up; // @[Top.scala 1125:22]
  wire  n889_valid_down; // @[Top.scala 1125:22]
  wire [31:0] n889_I_0_0; // @[Top.scala 1125:22]
  wire [31:0] n889_I_0_1; // @[Top.scala 1125:22]
  wire [31:0] n889_I_0_2; // @[Top.scala 1125:22]
  wire [31:0] n889_O_0; // @[Top.scala 1125:22]
  wire [31:0] n889_O_1; // @[Top.scala 1125:22]
  wire [31:0] n889_O_2; // @[Top.scala 1125:22]
  wire  n942_clock; // @[Top.scala 1128:22]
  wire  n942_reset; // @[Top.scala 1128:22]
  wire  n942_valid_up; // @[Top.scala 1128:22]
  wire  n942_valid_down; // @[Top.scala 1128:22]
  wire [31:0] n942_I_0; // @[Top.scala 1128:22]
  wire [31:0] n942_I_1; // @[Top.scala 1128:22]
  wire [31:0] n942_I_2; // @[Top.scala 1128:22]
  wire [31:0] n942_O; // @[Top.scala 1128:22]
  wire  n943_valid_up; // @[Top.scala 1131:22]
  wire  n943_valid_down; // @[Top.scala 1131:22]
  wire [31:0] n943_I; // @[Top.scala 1131:22]
  wire [31:0] n943_O; // @[Top.scala 1131:22]
  wire  n944_clock; // @[Top.scala 1134:22]
  wire  n944_reset; // @[Top.scala 1134:22]
  wire  n944_valid_up; // @[Top.scala 1134:22]
  wire  n944_valid_down; // @[Top.scala 1134:22]
  wire [31:0] n944_I; // @[Top.scala 1134:22]
  wire [31:0] n944_O; // @[Top.scala 1134:22]
  wire  n945_clock; // @[Top.scala 1137:22]
  wire  n945_reset; // @[Top.scala 1137:22]
  wire  n945_valid_up; // @[Top.scala 1137:22]
  wire  n945_valid_down; // @[Top.scala 1137:22]
  wire [31:0] n945_I0; // @[Top.scala 1137:22]
  wire [31:0] n945_I1; // @[Top.scala 1137:22]
  wire [31:0] n945_O; // @[Top.scala 1137:22]
  wire  n976_valid_up; // @[Top.scala 1141:22]
  wire  n976_valid_down; // @[Top.scala 1141:22]
  wire [31:0] n976_I0; // @[Top.scala 1141:22]
  wire [31:0] n976_I1; // @[Top.scala 1141:22]
  wire [31:0] n976_O_t0b; // @[Top.scala 1141:22]
  wire [31:0] n976_O_t1b; // @[Top.scala 1141:22]
  wire  n983_valid_up; // @[Top.scala 1145:22]
  wire  n983_valid_down; // @[Top.scala 1145:22]
  wire [31:0] n983_I0; // @[Top.scala 1145:22]
  wire [31:0] n983_I1_t0b; // @[Top.scala 1145:22]
  wire [31:0] n983_I1_t1b; // @[Top.scala 1145:22]
  wire [31:0] n983_O_t0b; // @[Top.scala 1145:22]
  wire [31:0] n983_O_t1b_t0b; // @[Top.scala 1145:22]
  wire [31:0] n983_O_t1b_t1b; // @[Top.scala 1145:22]
  wire  n990_clock; // @[Top.scala 1149:22]
  wire  n990_reset; // @[Top.scala 1149:22]
  wire  n990_valid_up; // @[Top.scala 1149:22]
  wire  n990_valid_down; // @[Top.scala 1149:22]
  wire [31:0] n990_I_t0b; // @[Top.scala 1149:22]
  wire [31:0] n990_I_t1b_t0b; // @[Top.scala 1149:22]
  wire [31:0] n990_I_t1b_t1b; // @[Top.scala 1149:22]
  wire [31:0] n990_O_t0b; // @[Top.scala 1149:22]
  wire [31:0] n990_O_t1b_t0b; // @[Top.scala 1149:22]
  wire [31:0] n990_O_t1b_t1b; // @[Top.scala 1149:22]
  wire  n991_clock; // @[Top.scala 1152:22]
  wire  n991_reset; // @[Top.scala 1152:22]
  wire  n991_valid_up; // @[Top.scala 1152:22]
  wire  n991_valid_down; // @[Top.scala 1152:22]
  wire [31:0] n991_I_t0b; // @[Top.scala 1152:22]
  wire [31:0] n991_I_t1b_t0b; // @[Top.scala 1152:22]
  wire [31:0] n991_I_t1b_t1b; // @[Top.scala 1152:22]
  wire [31:0] n991_O_t0b; // @[Top.scala 1152:22]
  wire [31:0] n991_O_t1b_t0b; // @[Top.scala 1152:22]
  wire [31:0] n991_O_t1b_t1b; // @[Top.scala 1152:22]
  wire  n992_clock; // @[Top.scala 1155:22]
  wire  n992_reset; // @[Top.scala 1155:22]
  wire  n992_valid_up; // @[Top.scala 1155:22]
  wire  n992_valid_down; // @[Top.scala 1155:22]
  wire [31:0] n992_I_t0b; // @[Top.scala 1155:22]
  wire [31:0] n992_I_t1b_t0b; // @[Top.scala 1155:22]
  wire [31:0] n992_I_t1b_t1b; // @[Top.scala 1155:22]
  wire [31:0] n992_O_t0b; // @[Top.scala 1155:22]
  wire [31:0] n992_O_t1b_t0b; // @[Top.scala 1155:22]
  wire [31:0] n992_O_t1b_t1b; // @[Top.scala 1155:22]
  FIFO n1 ( // @[Top.scala 758:20]
    .clock(n1_clock),
    .reset(n1_reset),
    .valid_up(n1_valid_up),
    .valid_down(n1_valid_down),
    .I(n1_I),
    .O(n1_O)
  );
  ShiftTN n2 ( // @[Top.scala 761:20]
    .clock(n2_clock),
    .reset(n2_reset),
    .valid_up(n2_valid_up),
    .valid_down(n2_valid_down),
    .I(n2_I),
    .O(n2_O)
  );
  ShiftTN n3 ( // @[Top.scala 764:20]
    .clock(n3_clock),
    .reset(n3_reset),
    .valid_up(n3_valid_up),
    .valid_down(n3_valid_down),
    .I(n3_I),
    .O(n3_O)
  );
  ShiftTN_2 n4 ( // @[Top.scala 767:20]
    .clock(n4_clock),
    .reset(n4_reset),
    .valid_up(n4_valid_up),
    .valid_down(n4_valid_down),
    .I(n4_I),
    .O(n4_O)
  );
  ShiftTN_2 n5 ( // @[Top.scala 770:20]
    .clock(n5_clock),
    .reset(n5_reset),
    .valid_up(n5_valid_up),
    .valid_down(n5_valid_down),
    .I(n5_I),
    .O(n5_O)
  );
  Map2T_1 n6 ( // @[Top.scala 773:20]
    .valid_up(n6_valid_up),
    .valid_down(n6_valid_down),
    .I0(n6_I0),
    .I1(n6_I1),
    .O_0(n6_O_0),
    .O_1(n6_O_1)
  );
  Map2T_3 n13 ( // @[Top.scala 777:21]
    .valid_up(n13_valid_up),
    .valid_down(n13_valid_down),
    .I0_0(n13_I0_0),
    .I0_1(n13_I0_1),
    .I1(n13_I1),
    .O_0(n13_O_0),
    .O_1(n13_O_1),
    .O_2(n13_O_2)
  );
  Passthrough n20 ( // @[Top.scala 781:21]
    .valid_up(n20_valid_up),
    .valid_down(n20_valid_down),
    .I_0(n20_I_0),
    .I_1(n20_I_1),
    .I_2(n20_I_2),
    .O_0_0(n20_O_0_0),
    .O_0_1(n20_O_0_1),
    .O_0_2(n20_O_0_2)
  );
  MapT n25 ( // @[Top.scala 784:21]
    .clock(n25_clock),
    .reset(n25_reset),
    .valid_up(n25_valid_up),
    .valid_down(n25_valid_down),
    .I_0_0(n25_I_0_0),
    .I_0_1(n25_I_0_1),
    .I_0_2(n25_I_0_2),
    .O_0(n25_O_0)
  );
  ShiftTN_2 n26 ( // @[Top.scala 787:21]
    .clock(n26_clock),
    .reset(n26_reset),
    .valid_up(n26_valid_up),
    .valid_down(n26_valid_down),
    .I(n26_I),
    .O(n26_O)
  );
  ShiftTN_2 n27 ( // @[Top.scala 790:21]
    .clock(n27_clock),
    .reset(n27_reset),
    .valid_up(n27_valid_up),
    .valid_down(n27_valid_down),
    .I(n27_I),
    .O(n27_O)
  );
  Map2T_1 n28 ( // @[Top.scala 793:21]
    .valid_up(n28_valid_up),
    .valid_down(n28_valid_down),
    .I0(n28_I0),
    .I1(n28_I1),
    .O_0(n28_O_0),
    .O_1(n28_O_1)
  );
  Map2T_3 n35 ( // @[Top.scala 797:21]
    .valid_up(n35_valid_up),
    .valid_down(n35_valid_down),
    .I0_0(n35_I0_0),
    .I0_1(n35_I0_1),
    .I1(n35_I1),
    .O_0(n35_O_0),
    .O_1(n35_O_1),
    .O_2(n35_O_2)
  );
  Passthrough n42 ( // @[Top.scala 801:21]
    .valid_up(n42_valid_up),
    .valid_down(n42_valid_down),
    .I_0(n42_I_0),
    .I_1(n42_I_1),
    .I_2(n42_I_2),
    .O_0_0(n42_O_0_0),
    .O_0_1(n42_O_0_1),
    .O_0_2(n42_O_0_2)
  );
  MapT n47 ( // @[Top.scala 804:21]
    .clock(n47_clock),
    .reset(n47_reset),
    .valid_up(n47_valid_up),
    .valid_down(n47_valid_down),
    .I_0_0(n47_I_0_0),
    .I_0_1(n47_I_0_1),
    .I_0_2(n47_I_0_2),
    .O_0(n47_O_0)
  );
  Map2T_8 n48 ( // @[Top.scala 807:21]
    .valid_up(n48_valid_up),
    .valid_down(n48_valid_down),
    .I0_0(n48_I0_0),
    .I1_0(n48_I1_0),
    .O_0_0(n48_O_0_0),
    .O_0_1(n48_O_0_1)
  );
  ShiftTN_2 n55 ( // @[Top.scala 811:21]
    .clock(n55_clock),
    .reset(n55_reset),
    .valid_up(n55_valid_up),
    .valid_down(n55_valid_down),
    .I(n55_I),
    .O(n55_O)
  );
  ShiftTN_2 n56 ( // @[Top.scala 814:21]
    .clock(n56_clock),
    .reset(n56_reset),
    .valid_up(n56_valid_up),
    .valid_down(n56_valid_down),
    .I(n56_I),
    .O(n56_O)
  );
  Map2T_1 n57 ( // @[Top.scala 817:21]
    .valid_up(n57_valid_up),
    .valid_down(n57_valid_down),
    .I0(n57_I0),
    .I1(n57_I1),
    .O_0(n57_O_0),
    .O_1(n57_O_1)
  );
  Map2T_3 n64 ( // @[Top.scala 821:21]
    .valid_up(n64_valid_up),
    .valid_down(n64_valid_down),
    .I0_0(n64_I0_0),
    .I0_1(n64_I0_1),
    .I1(n64_I1),
    .O_0(n64_O_0),
    .O_1(n64_O_1),
    .O_2(n64_O_2)
  );
  Passthrough n71 ( // @[Top.scala 825:21]
    .valid_up(n71_valid_up),
    .valid_down(n71_valid_down),
    .I_0(n71_I_0),
    .I_1(n71_I_1),
    .I_2(n71_I_2),
    .O_0_0(n71_O_0_0),
    .O_0_1(n71_O_0_1),
    .O_0_2(n71_O_0_2)
  );
  MapT n76 ( // @[Top.scala 828:21]
    .clock(n76_clock),
    .reset(n76_reset),
    .valid_up(n76_valid_up),
    .valid_down(n76_valid_down),
    .I_0_0(n76_I_0_0),
    .I_0_1(n76_I_0_1),
    .I_0_2(n76_I_0_2),
    .O_0(n76_O_0)
  );
  Map2T_13 n77 ( // @[Top.scala 831:21]
    .valid_up(n77_valid_up),
    .valid_down(n77_valid_down),
    .I0_0_0(n77_I0_0_0),
    .I0_0_1(n77_I0_0_1),
    .I1_0(n77_I1_0),
    .O_0_0(n77_O_0_0),
    .O_0_1(n77_O_0_1),
    .O_0_2(n77_O_0_2)
  );
  Passthrough_3 n84 ( // @[Top.scala 835:21]
    .valid_up(n84_valid_up),
    .valid_down(n84_valid_down),
    .I_0_0(n84_I_0_0),
    .I_0_1(n84_I_0_1),
    .I_0_2(n84_I_0_2),
    .O_0_0_0(n84_O_0_0_0),
    .O_0_0_1(n84_O_0_0_1),
    .O_0_0_2(n84_O_0_0_2)
  );
  MapT_3 n89 ( // @[Top.scala 838:21]
    .valid_up(n89_valid_up),
    .valid_down(n89_valid_down),
    .I_0_0_0(n89_I_0_0_0),
    .I_0_0_1(n89_I_0_0_1),
    .I_0_0_2(n89_I_0_0_2),
    .O_0_0(n89_O_0_0),
    .O_0_1(n89_O_0_1),
    .O_0_2(n89_O_0_2)
  );
  Passthrough_5 n90 ( // @[Top.scala 841:21]
    .valid_up(n90_valid_up),
    .valid_down(n90_valid_down),
    .I_0_0(n90_I_0_0),
    .I_0_1(n90_I_0_1),
    .I_0_2(n90_I_0_2),
    .O_0_0(n90_O_0_0),
    .O_0_1(n90_O_0_1),
    .O_0_2(n90_O_0_2)
  );
  MapT_54 n440 ( // @[Top.scala 844:22]
    .clock(n440_clock),
    .reset(n440_reset),
    .valid_up(n440_valid_up),
    .valid_down(n440_valid_down),
    .I_0_0(n440_I_0_0),
    .I_0_1(n440_I_0_1),
    .I_0_2(n440_I_0_2),
    .O_0_t0b(n440_O_0_t0b),
    .O_0_t1b_t0b(n440_O_0_t1b_t0b),
    .O_0_t1b_t1b(n440_O_0_t1b_t1b)
  );
  Passthrough_11 n441 ( // @[Top.scala 847:22]
    .valid_up(n441_valid_up),
    .valid_down(n441_valid_down),
    .I_0_t0b(n441_I_0_t0b),
    .I_0_t1b_t0b(n441_I_0_t1b_t0b),
    .I_0_t1b_t1b(n441_I_0_t1b_t1b),
    .O_t0b(n441_O_t0b),
    .O_t1b_t0b(n441_O_t1b_t0b),
    .O_t1b_t1b(n441_O_t1b_t1b)
  );
  Passthrough_12 n442 ( // @[Top.scala 850:22]
    .valid_up(n442_valid_up),
    .valid_down(n442_valid_down),
    .I_t0b(n442_I_t0b),
    .I_t1b_t0b(n442_I_t1b_t0b),
    .I_t1b_t1b(n442_I_t1b_t1b),
    .O_t0b(n442_O_t0b),
    .O_t1b_t0b(n442_O_t1b_t0b),
    .O_t1b_t1b(n442_O_t1b_t1b)
  );
  MapT_56 n447 ( // @[Top.scala 853:22]
    .valid_up(n447_valid_up),
    .valid_down(n447_valid_down),
    .I_t0b(n447_I_t0b),
    .O(n447_O)
  );
  ShiftTN n448 ( // @[Top.scala 856:22]
    .clock(n448_clock),
    .reset(n448_reset),
    .valid_up(n448_valid_up),
    .valid_down(n448_valid_down),
    .I(n448_I),
    .O(n448_O)
  );
  ShiftTN n449 ( // @[Top.scala 859:22]
    .clock(n449_clock),
    .reset(n449_reset),
    .valid_up(n449_valid_up),
    .valid_down(n449_valid_down),
    .I(n449_I),
    .O(n449_O)
  );
  ShiftTN_2 n450 ( // @[Top.scala 862:22]
    .clock(n450_clock),
    .reset(n450_reset),
    .valid_up(n450_valid_up),
    .valid_down(n450_valid_down),
    .I(n450_I),
    .O(n450_O)
  );
  ShiftTN_2 n451 ( // @[Top.scala 865:22]
    .clock(n451_clock),
    .reset(n451_reset),
    .valid_up(n451_valid_up),
    .valid_down(n451_valid_down),
    .I(n451_I),
    .O(n451_O)
  );
  Map2T_1 n452 ( // @[Top.scala 868:22]
    .valid_up(n452_valid_up),
    .valid_down(n452_valid_down),
    .I0(n452_I0),
    .I1(n452_I1),
    .O_0(n452_O_0),
    .O_1(n452_O_1)
  );
  Map2T_3 n459 ( // @[Top.scala 872:22]
    .valid_up(n459_valid_up),
    .valid_down(n459_valid_down),
    .I0_0(n459_I0_0),
    .I0_1(n459_I0_1),
    .I1(n459_I1),
    .O_0(n459_O_0),
    .O_1(n459_O_1),
    .O_2(n459_O_2)
  );
  Passthrough n466 ( // @[Top.scala 876:22]
    .valid_up(n466_valid_up),
    .valid_down(n466_valid_down),
    .I_0(n466_I_0),
    .I_1(n466_I_1),
    .I_2(n466_I_2),
    .O_0_0(n466_O_0_0),
    .O_0_1(n466_O_0_1),
    .O_0_2(n466_O_0_2)
  );
  MapT n471 ( // @[Top.scala 879:22]
    .clock(n471_clock),
    .reset(n471_reset),
    .valid_up(n471_valid_up),
    .valid_down(n471_valid_down),
    .I_0_0(n471_I_0_0),
    .I_0_1(n471_I_0_1),
    .I_0_2(n471_I_0_2),
    .O_0(n471_O_0)
  );
  ShiftTN_2 n472 ( // @[Top.scala 882:22]
    .clock(n472_clock),
    .reset(n472_reset),
    .valid_up(n472_valid_up),
    .valid_down(n472_valid_down),
    .I(n472_I),
    .O(n472_O)
  );
  ShiftTN_2 n473 ( // @[Top.scala 885:22]
    .clock(n473_clock),
    .reset(n473_reset),
    .valid_up(n473_valid_up),
    .valid_down(n473_valid_down),
    .I(n473_I),
    .O(n473_O)
  );
  Map2T_1 n474 ( // @[Top.scala 888:22]
    .valid_up(n474_valid_up),
    .valid_down(n474_valid_down),
    .I0(n474_I0),
    .I1(n474_I1),
    .O_0(n474_O_0),
    .O_1(n474_O_1)
  );
  Map2T_3 n481 ( // @[Top.scala 892:22]
    .valid_up(n481_valid_up),
    .valid_down(n481_valid_down),
    .I0_0(n481_I0_0),
    .I0_1(n481_I0_1),
    .I1(n481_I1),
    .O_0(n481_O_0),
    .O_1(n481_O_1),
    .O_2(n481_O_2)
  );
  Passthrough n488 ( // @[Top.scala 896:22]
    .valid_up(n488_valid_up),
    .valid_down(n488_valid_down),
    .I_0(n488_I_0),
    .I_1(n488_I_1),
    .I_2(n488_I_2),
    .O_0_0(n488_O_0_0),
    .O_0_1(n488_O_0_1),
    .O_0_2(n488_O_0_2)
  );
  MapT n493 ( // @[Top.scala 899:22]
    .clock(n493_clock),
    .reset(n493_reset),
    .valid_up(n493_valid_up),
    .valid_down(n493_valid_down),
    .I_0_0(n493_I_0_0),
    .I_0_1(n493_I_0_1),
    .I_0_2(n493_I_0_2),
    .O_0(n493_O_0)
  );
  Map2T_8 n494 ( // @[Top.scala 902:22]
    .valid_up(n494_valid_up),
    .valid_down(n494_valid_down),
    .I0_0(n494_I0_0),
    .I1_0(n494_I1_0),
    .O_0_0(n494_O_0_0),
    .O_0_1(n494_O_0_1)
  );
  ShiftTN_2 n501 ( // @[Top.scala 906:22]
    .clock(n501_clock),
    .reset(n501_reset),
    .valid_up(n501_valid_up),
    .valid_down(n501_valid_down),
    .I(n501_I),
    .O(n501_O)
  );
  ShiftTN_2 n502 ( // @[Top.scala 909:22]
    .clock(n502_clock),
    .reset(n502_reset),
    .valid_up(n502_valid_up),
    .valid_down(n502_valid_down),
    .I(n502_I),
    .O(n502_O)
  );
  Map2T_1 n503 ( // @[Top.scala 912:22]
    .valid_up(n503_valid_up),
    .valid_down(n503_valid_down),
    .I0(n503_I0),
    .I1(n503_I1),
    .O_0(n503_O_0),
    .O_1(n503_O_1)
  );
  Map2T_3 n510 ( // @[Top.scala 916:22]
    .valid_up(n510_valid_up),
    .valid_down(n510_valid_down),
    .I0_0(n510_I0_0),
    .I0_1(n510_I0_1),
    .I1(n510_I1),
    .O_0(n510_O_0),
    .O_1(n510_O_1),
    .O_2(n510_O_2)
  );
  Passthrough n517 ( // @[Top.scala 920:22]
    .valid_up(n517_valid_up),
    .valid_down(n517_valid_down),
    .I_0(n517_I_0),
    .I_1(n517_I_1),
    .I_2(n517_I_2),
    .O_0_0(n517_O_0_0),
    .O_0_1(n517_O_0_1),
    .O_0_2(n517_O_0_2)
  );
  MapT n522 ( // @[Top.scala 923:22]
    .clock(n522_clock),
    .reset(n522_reset),
    .valid_up(n522_valid_up),
    .valid_down(n522_valid_down),
    .I_0_0(n522_I_0_0),
    .I_0_1(n522_I_0_1),
    .I_0_2(n522_I_0_2),
    .O_0(n522_O_0)
  );
  Map2T_13 n523 ( // @[Top.scala 926:22]
    .valid_up(n523_valid_up),
    .valid_down(n523_valid_down),
    .I0_0_0(n523_I0_0_0),
    .I0_0_1(n523_I0_0_1),
    .I1_0(n523_I1_0),
    .O_0_0(n523_O_0_0),
    .O_0_1(n523_O_0_1),
    .O_0_2(n523_O_0_2)
  );
  Passthrough_5 n530 ( // @[Top.scala 930:22]
    .valid_up(n530_valid_up),
    .valid_down(n530_valid_down),
    .I_0_0(n530_I_0_0),
    .I_0_1(n530_I_0_1),
    .I_0_2(n530_I_0_2),
    .O_0_0(n530_O_0_0),
    .O_0_1(n530_O_0_1),
    .O_0_2(n530_O_0_2)
  );
  MapT_60 n533 ( // @[Top.scala 933:22]
    .valid_up(n533_valid_up),
    .valid_down(n533_valid_down),
    .I_0_0(n533_I_0_0),
    .I_0_1(n533_I_0_1),
    .I_0_2(n533_I_0_2),
    .O_0(n533_O_0),
    .O_1(n533_O_1),
    .O_2(n533_O_2)
  );
  MapT_67 n586 ( // @[Top.scala 936:22]
    .clock(n586_clock),
    .reset(n586_reset),
    .valid_up(n586_valid_up),
    .valid_down(n586_valid_down),
    .I_0(n586_I_0),
    .I_1(n586_I_1),
    .I_2(n586_I_2),
    .O(n586_O)
  );
  DownT_1 n587 ( // @[Top.scala 939:22]
    .valid_up(n587_valid_up),
    .valid_down(n587_valid_down),
    .I(n587_I),
    .O(n587_O)
  );
  FIFO_18 n588 ( // @[Top.scala 942:22]
    .clock(n588_clock),
    .reset(n588_reset),
    .valid_up(n588_valid_up),
    .valid_down(n588_valid_down),
    .I(n588_I),
    .O(n588_O)
  );
  Map2T_80 n589 ( // @[Top.scala 945:22]
    .clock(n589_clock),
    .reset(n589_reset),
    .valid_up(n589_valid_up),
    .valid_down(n589_valid_down),
    .I0(n589_I0),
    .I1(n589_I1),
    .O(n589_O)
  );
  MapT_69 n625 ( // @[Top.scala 949:22]
    .valid_up(n625_valid_up),
    .valid_down(n625_valid_down),
    .I_t1b_t0b(n625_I_t1b_t0b),
    .I_t1b_t1b(n625_I_t1b_t1b),
    .O(n625_O)
  );
  ShiftTN n626 ( // @[Top.scala 952:22]
    .clock(n626_clock),
    .reset(n626_reset),
    .valid_up(n626_valid_up),
    .valid_down(n626_valid_down),
    .I(n626_I),
    .O(n626_O)
  );
  ShiftTN n627 ( // @[Top.scala 955:22]
    .clock(n627_clock),
    .reset(n627_reset),
    .valid_up(n627_valid_up),
    .valid_down(n627_valid_down),
    .I(n627_I),
    .O(n627_O)
  );
  ShiftTN_2 n628 ( // @[Top.scala 958:22]
    .clock(n628_clock),
    .reset(n628_reset),
    .valid_up(n628_valid_up),
    .valid_down(n628_valid_down),
    .I(n628_I),
    .O(n628_O)
  );
  ShiftTN_2 n629 ( // @[Top.scala 961:22]
    .clock(n629_clock),
    .reset(n629_reset),
    .valid_up(n629_valid_up),
    .valid_down(n629_valid_down),
    .I(n629_I),
    .O(n629_O)
  );
  Map2T_1 n630 ( // @[Top.scala 964:22]
    .valid_up(n630_valid_up),
    .valid_down(n630_valid_down),
    .I0(n630_I0),
    .I1(n630_I1),
    .O_0(n630_O_0),
    .O_1(n630_O_1)
  );
  Map2T_3 n637 ( // @[Top.scala 968:22]
    .valid_up(n637_valid_up),
    .valid_down(n637_valid_down),
    .I0_0(n637_I0_0),
    .I0_1(n637_I0_1),
    .I1(n637_I1),
    .O_0(n637_O_0),
    .O_1(n637_O_1),
    .O_2(n637_O_2)
  );
  Passthrough n644 ( // @[Top.scala 972:22]
    .valid_up(n644_valid_up),
    .valid_down(n644_valid_down),
    .I_0(n644_I_0),
    .I_1(n644_I_1),
    .I_2(n644_I_2),
    .O_0_0(n644_O_0_0),
    .O_0_1(n644_O_0_1),
    .O_0_2(n644_O_0_2)
  );
  MapT n649 ( // @[Top.scala 975:22]
    .clock(n649_clock),
    .reset(n649_reset),
    .valid_up(n649_valid_up),
    .valid_down(n649_valid_down),
    .I_0_0(n649_I_0_0),
    .I_0_1(n649_I_0_1),
    .I_0_2(n649_I_0_2),
    .O_0(n649_O_0)
  );
  ShiftTN_2 n650 ( // @[Top.scala 978:22]
    .clock(n650_clock),
    .reset(n650_reset),
    .valid_up(n650_valid_up),
    .valid_down(n650_valid_down),
    .I(n650_I),
    .O(n650_O)
  );
  ShiftTN_2 n651 ( // @[Top.scala 981:22]
    .clock(n651_clock),
    .reset(n651_reset),
    .valid_up(n651_valid_up),
    .valid_down(n651_valid_down),
    .I(n651_I),
    .O(n651_O)
  );
  Map2T_1 n652 ( // @[Top.scala 984:22]
    .valid_up(n652_valid_up),
    .valid_down(n652_valid_down),
    .I0(n652_I0),
    .I1(n652_I1),
    .O_0(n652_O_0),
    .O_1(n652_O_1)
  );
  Map2T_3 n659 ( // @[Top.scala 988:22]
    .valid_up(n659_valid_up),
    .valid_down(n659_valid_down),
    .I0_0(n659_I0_0),
    .I0_1(n659_I0_1),
    .I1(n659_I1),
    .O_0(n659_O_0),
    .O_1(n659_O_1),
    .O_2(n659_O_2)
  );
  Passthrough n666 ( // @[Top.scala 992:22]
    .valid_up(n666_valid_up),
    .valid_down(n666_valid_down),
    .I_0(n666_I_0),
    .I_1(n666_I_1),
    .I_2(n666_I_2),
    .O_0_0(n666_O_0_0),
    .O_0_1(n666_O_0_1),
    .O_0_2(n666_O_0_2)
  );
  MapT n671 ( // @[Top.scala 995:22]
    .clock(n671_clock),
    .reset(n671_reset),
    .valid_up(n671_valid_up),
    .valid_down(n671_valid_down),
    .I_0_0(n671_I_0_0),
    .I_0_1(n671_I_0_1),
    .I_0_2(n671_I_0_2),
    .O_0(n671_O_0)
  );
  Map2T_8 n672 ( // @[Top.scala 998:22]
    .valid_up(n672_valid_up),
    .valid_down(n672_valid_down),
    .I0_0(n672_I0_0),
    .I1_0(n672_I1_0),
    .O_0_0(n672_O_0_0),
    .O_0_1(n672_O_0_1)
  );
  ShiftTN_2 n679 ( // @[Top.scala 1002:22]
    .clock(n679_clock),
    .reset(n679_reset),
    .valid_up(n679_valid_up),
    .valid_down(n679_valid_down),
    .I(n679_I),
    .O(n679_O)
  );
  ShiftTN_2 n680 ( // @[Top.scala 1005:22]
    .clock(n680_clock),
    .reset(n680_reset),
    .valid_up(n680_valid_up),
    .valid_down(n680_valid_down),
    .I(n680_I),
    .O(n680_O)
  );
  Map2T_1 n681 ( // @[Top.scala 1008:22]
    .valid_up(n681_valid_up),
    .valid_down(n681_valid_down),
    .I0(n681_I0),
    .I1(n681_I1),
    .O_0(n681_O_0),
    .O_1(n681_O_1)
  );
  Map2T_3 n688 ( // @[Top.scala 1012:22]
    .valid_up(n688_valid_up),
    .valid_down(n688_valid_down),
    .I0_0(n688_I0_0),
    .I0_1(n688_I0_1),
    .I1(n688_I1),
    .O_0(n688_O_0),
    .O_1(n688_O_1),
    .O_2(n688_O_2)
  );
  Passthrough n695 ( // @[Top.scala 1016:22]
    .valid_up(n695_valid_up),
    .valid_down(n695_valid_down),
    .I_0(n695_I_0),
    .I_1(n695_I_1),
    .I_2(n695_I_2),
    .O_0_0(n695_O_0_0),
    .O_0_1(n695_O_0_1),
    .O_0_2(n695_O_0_2)
  );
  MapT n700 ( // @[Top.scala 1019:22]
    .clock(n700_clock),
    .reset(n700_reset),
    .valid_up(n700_valid_up),
    .valid_down(n700_valid_down),
    .I_0_0(n700_I_0_0),
    .I_0_1(n700_I_0_1),
    .I_0_2(n700_I_0_2),
    .O_0(n700_O_0)
  );
  Map2T_13 n701 ( // @[Top.scala 1022:22]
    .valid_up(n701_valid_up),
    .valid_down(n701_valid_down),
    .I0_0_0(n701_I0_0_0),
    .I0_0_1(n701_I0_0_1),
    .I1_0(n701_I1_0),
    .O_0_0(n701_O_0_0),
    .O_0_1(n701_O_0_1),
    .O_0_2(n701_O_0_2)
  );
  Passthrough_5 n708 ( // @[Top.scala 1026:22]
    .valid_up(n708_valid_up),
    .valid_down(n708_valid_down),
    .I_0_0(n708_I_0_0),
    .I_0_1(n708_I_0_1),
    .I_0_2(n708_I_0_2),
    .O_0_0(n708_O_0_0),
    .O_0_1(n708_O_0_1),
    .O_0_2(n708_O_0_2)
  );
  MapT_60 n711 ( // @[Top.scala 1029:22]
    .valid_up(n711_valid_up),
    .valid_down(n711_valid_down),
    .I_0_0(n711_I_0_0),
    .I_0_1(n711_I_0_1),
    .I_0_2(n711_I_0_2),
    .O_0(n711_O_0),
    .O_1(n711_O_1),
    .O_2(n711_O_2)
  );
  MapT_80 n764 ( // @[Top.scala 1032:22]
    .clock(n764_clock),
    .reset(n764_reset),
    .valid_up(n764_valid_up),
    .valid_down(n764_valid_down),
    .I_0(n764_I_0),
    .I_1(n764_I_1),
    .I_2(n764_I_2),
    .O(n764_O)
  );
  DownT_1 n765 ( // @[Top.scala 1035:22]
    .valid_up(n765_valid_up),
    .valid_down(n765_valid_down),
    .I(n765_I),
    .O(n765_O)
  );
  FIFO_18 n766 ( // @[Top.scala 1038:22]
    .clock(n766_clock),
    .reset(n766_reset),
    .valid_up(n766_valid_up),
    .valid_down(n766_valid_down),
    .I(n766_I),
    .O(n766_O)
  );
  Map2T_80 n767 ( // @[Top.scala 1041:22]
    .clock(n767_clock),
    .reset(n767_reset),
    .valid_up(n767_valid_up),
    .valid_down(n767_valid_down),
    .I0(n767_I0),
    .I1(n767_I1),
    .O(n767_O)
  );
  MapT_82 n803 ( // @[Top.scala 1045:22]
    .valid_up(n803_valid_up),
    .valid_down(n803_valid_down),
    .I_t1b_t0b(n803_I_t1b_t0b),
    .I_t1b_t1b(n803_I_t1b_t1b),
    .O(n803_O)
  );
  ShiftTN n804 ( // @[Top.scala 1048:22]
    .clock(n804_clock),
    .reset(n804_reset),
    .valid_up(n804_valid_up),
    .valid_down(n804_valid_down),
    .I(n804_I),
    .O(n804_O)
  );
  ShiftTN n805 ( // @[Top.scala 1051:22]
    .clock(n805_clock),
    .reset(n805_reset),
    .valid_up(n805_valid_up),
    .valid_down(n805_valid_down),
    .I(n805_I),
    .O(n805_O)
  );
  ShiftTN_2 n806 ( // @[Top.scala 1054:22]
    .clock(n806_clock),
    .reset(n806_reset),
    .valid_up(n806_valid_up),
    .valid_down(n806_valid_down),
    .I(n806_I),
    .O(n806_O)
  );
  ShiftTN_2 n807 ( // @[Top.scala 1057:22]
    .clock(n807_clock),
    .reset(n807_reset),
    .valid_up(n807_valid_up),
    .valid_down(n807_valid_down),
    .I(n807_I),
    .O(n807_O)
  );
  Map2T_1 n808 ( // @[Top.scala 1060:22]
    .valid_up(n808_valid_up),
    .valid_down(n808_valid_down),
    .I0(n808_I0),
    .I1(n808_I1),
    .O_0(n808_O_0),
    .O_1(n808_O_1)
  );
  Map2T_3 n815 ( // @[Top.scala 1064:22]
    .valid_up(n815_valid_up),
    .valid_down(n815_valid_down),
    .I0_0(n815_I0_0),
    .I0_1(n815_I0_1),
    .I1(n815_I1),
    .O_0(n815_O_0),
    .O_1(n815_O_1),
    .O_2(n815_O_2)
  );
  Passthrough n822 ( // @[Top.scala 1068:22]
    .valid_up(n822_valid_up),
    .valid_down(n822_valid_down),
    .I_0(n822_I_0),
    .I_1(n822_I_1),
    .I_2(n822_I_2),
    .O_0_0(n822_O_0_0),
    .O_0_1(n822_O_0_1),
    .O_0_2(n822_O_0_2)
  );
  MapT n827 ( // @[Top.scala 1071:22]
    .clock(n827_clock),
    .reset(n827_reset),
    .valid_up(n827_valid_up),
    .valid_down(n827_valid_down),
    .I_0_0(n827_I_0_0),
    .I_0_1(n827_I_0_1),
    .I_0_2(n827_I_0_2),
    .O_0(n827_O_0)
  );
  ShiftTN_2 n828 ( // @[Top.scala 1074:22]
    .clock(n828_clock),
    .reset(n828_reset),
    .valid_up(n828_valid_up),
    .valid_down(n828_valid_down),
    .I(n828_I),
    .O(n828_O)
  );
  ShiftTN_2 n829 ( // @[Top.scala 1077:22]
    .clock(n829_clock),
    .reset(n829_reset),
    .valid_up(n829_valid_up),
    .valid_down(n829_valid_down),
    .I(n829_I),
    .O(n829_O)
  );
  Map2T_1 n830 ( // @[Top.scala 1080:22]
    .valid_up(n830_valid_up),
    .valid_down(n830_valid_down),
    .I0(n830_I0),
    .I1(n830_I1),
    .O_0(n830_O_0),
    .O_1(n830_O_1)
  );
  Map2T_3 n837 ( // @[Top.scala 1084:22]
    .valid_up(n837_valid_up),
    .valid_down(n837_valid_down),
    .I0_0(n837_I0_0),
    .I0_1(n837_I0_1),
    .I1(n837_I1),
    .O_0(n837_O_0),
    .O_1(n837_O_1),
    .O_2(n837_O_2)
  );
  Passthrough n844 ( // @[Top.scala 1088:22]
    .valid_up(n844_valid_up),
    .valid_down(n844_valid_down),
    .I_0(n844_I_0),
    .I_1(n844_I_1),
    .I_2(n844_I_2),
    .O_0_0(n844_O_0_0),
    .O_0_1(n844_O_0_1),
    .O_0_2(n844_O_0_2)
  );
  MapT n849 ( // @[Top.scala 1091:22]
    .clock(n849_clock),
    .reset(n849_reset),
    .valid_up(n849_valid_up),
    .valid_down(n849_valid_down),
    .I_0_0(n849_I_0_0),
    .I_0_1(n849_I_0_1),
    .I_0_2(n849_I_0_2),
    .O_0(n849_O_0)
  );
  Map2T_8 n850 ( // @[Top.scala 1094:22]
    .valid_up(n850_valid_up),
    .valid_down(n850_valid_down),
    .I0_0(n850_I0_0),
    .I1_0(n850_I1_0),
    .O_0_0(n850_O_0_0),
    .O_0_1(n850_O_0_1)
  );
  ShiftTN_2 n857 ( // @[Top.scala 1098:22]
    .clock(n857_clock),
    .reset(n857_reset),
    .valid_up(n857_valid_up),
    .valid_down(n857_valid_down),
    .I(n857_I),
    .O(n857_O)
  );
  ShiftTN_2 n858 ( // @[Top.scala 1101:22]
    .clock(n858_clock),
    .reset(n858_reset),
    .valid_up(n858_valid_up),
    .valid_down(n858_valid_down),
    .I(n858_I),
    .O(n858_O)
  );
  Map2T_1 n859 ( // @[Top.scala 1104:22]
    .valid_up(n859_valid_up),
    .valid_down(n859_valid_down),
    .I0(n859_I0),
    .I1(n859_I1),
    .O_0(n859_O_0),
    .O_1(n859_O_1)
  );
  Map2T_3 n866 ( // @[Top.scala 1108:22]
    .valid_up(n866_valid_up),
    .valid_down(n866_valid_down),
    .I0_0(n866_I0_0),
    .I0_1(n866_I0_1),
    .I1(n866_I1),
    .O_0(n866_O_0),
    .O_1(n866_O_1),
    .O_2(n866_O_2)
  );
  Passthrough n873 ( // @[Top.scala 1112:22]
    .valid_up(n873_valid_up),
    .valid_down(n873_valid_down),
    .I_0(n873_I_0),
    .I_1(n873_I_1),
    .I_2(n873_I_2),
    .O_0_0(n873_O_0_0),
    .O_0_1(n873_O_0_1),
    .O_0_2(n873_O_0_2)
  );
  MapT n878 ( // @[Top.scala 1115:22]
    .clock(n878_clock),
    .reset(n878_reset),
    .valid_up(n878_valid_up),
    .valid_down(n878_valid_down),
    .I_0_0(n878_I_0_0),
    .I_0_1(n878_I_0_1),
    .I_0_2(n878_I_0_2),
    .O_0(n878_O_0)
  );
  Map2T_13 n879 ( // @[Top.scala 1118:22]
    .valid_up(n879_valid_up),
    .valid_down(n879_valid_down),
    .I0_0_0(n879_I0_0_0),
    .I0_0_1(n879_I0_0_1),
    .I1_0(n879_I1_0),
    .O_0_0(n879_O_0_0),
    .O_0_1(n879_O_0_1),
    .O_0_2(n879_O_0_2)
  );
  Passthrough_5 n886 ( // @[Top.scala 1122:22]
    .valid_up(n886_valid_up),
    .valid_down(n886_valid_down),
    .I_0_0(n886_I_0_0),
    .I_0_1(n886_I_0_1),
    .I_0_2(n886_I_0_2),
    .O_0_0(n886_O_0_0),
    .O_0_1(n886_O_0_1),
    .O_0_2(n886_O_0_2)
  );
  MapT_60 n889 ( // @[Top.scala 1125:22]
    .valid_up(n889_valid_up),
    .valid_down(n889_valid_down),
    .I_0_0(n889_I_0_0),
    .I_0_1(n889_I_0_1),
    .I_0_2(n889_I_0_2),
    .O_0(n889_O_0),
    .O_1(n889_O_1),
    .O_2(n889_O_2)
  );
  MapT_93 n942 ( // @[Top.scala 1128:22]
    .clock(n942_clock),
    .reset(n942_reset),
    .valid_up(n942_valid_up),
    .valid_down(n942_valid_down),
    .I_0(n942_I_0),
    .I_1(n942_I_1),
    .I_2(n942_I_2),
    .O(n942_O)
  );
  DownT_1 n943 ( // @[Top.scala 1131:22]
    .valid_up(n943_valid_up),
    .valid_down(n943_valid_down),
    .I(n943_I),
    .O(n943_O)
  );
  FIFO_18 n944 ( // @[Top.scala 1134:22]
    .clock(n944_clock),
    .reset(n944_reset),
    .valid_up(n944_valid_up),
    .valid_down(n944_valid_down),
    .I(n944_I),
    .O(n944_O)
  );
  Map2T_80 n945 ( // @[Top.scala 1137:22]
    .clock(n945_clock),
    .reset(n945_reset),
    .valid_up(n945_valid_up),
    .valid_down(n945_valid_down),
    .I0(n945_I0),
    .I1(n945_I1),
    .O(n945_O)
  );
  Map2T_17 n976 ( // @[Top.scala 1141:22]
    .valid_up(n976_valid_up),
    .valid_down(n976_valid_down),
    .I0(n976_I0),
    .I1(n976_I1),
    .O_t0b(n976_O_t0b),
    .O_t1b(n976_O_t1b)
  );
  Map2T_23 n983 ( // @[Top.scala 1145:22]
    .valid_up(n983_valid_up),
    .valid_down(n983_valid_down),
    .I0(n983_I0),
    .I1_t0b(n983_I1_t0b),
    .I1_t1b(n983_I1_t1b),
    .O_t0b(n983_O_t0b),
    .O_t1b_t0b(n983_O_t1b_t0b),
    .O_t1b_t1b(n983_O_t1b_t1b)
  );
  FIFO_24 n990 ( // @[Top.scala 1149:22]
    .clock(n990_clock),
    .reset(n990_reset),
    .valid_up(n990_valid_up),
    .valid_down(n990_valid_down),
    .I_t0b(n990_I_t0b),
    .I_t1b_t0b(n990_I_t1b_t0b),
    .I_t1b_t1b(n990_I_t1b_t1b),
    .O_t0b(n990_O_t0b),
    .O_t1b_t0b(n990_O_t1b_t0b),
    .O_t1b_t1b(n990_O_t1b_t1b)
  );
  FIFO_24 n991 ( // @[Top.scala 1152:22]
    .clock(n991_clock),
    .reset(n991_reset),
    .valid_up(n991_valid_up),
    .valid_down(n991_valid_down),
    .I_t0b(n991_I_t0b),
    .I_t1b_t0b(n991_I_t1b_t0b),
    .I_t1b_t1b(n991_I_t1b_t1b),
    .O_t0b(n991_O_t0b),
    .O_t1b_t0b(n991_O_t1b_t0b),
    .O_t1b_t1b(n991_O_t1b_t1b)
  );
  FIFO_24 n992 ( // @[Top.scala 1155:22]
    .clock(n992_clock),
    .reset(n992_reset),
    .valid_up(n992_valid_up),
    .valid_down(n992_valid_down),
    .I_t0b(n992_I_t0b),
    .I_t1b_t0b(n992_I_t1b_t0b),
    .I_t1b_t1b(n992_I_t1b_t1b),
    .O_t0b(n992_O_t0b),
    .O_t1b_t0b(n992_O_t1b_t0b),
    .O_t1b_t1b(n992_O_t1b_t1b)
  );
  assign valid_down = n992_valid_down; // @[Top.scala 1159:16]
  assign O_t0b = n992_O_t0b; // @[Top.scala 1158:7]
  assign O_t1b_t0b = n992_O_t1b_t0b; // @[Top.scala 1158:7]
  assign O_t1b_t1b = n992_O_t1b_t1b; // @[Top.scala 1158:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 760:17]
  assign n1_I = I; // @[Top.scala 759:10]
  assign n2_clock = clock;
  assign n2_reset = reset;
  assign n2_valid_up = n1_valid_down; // @[Top.scala 763:17]
  assign n2_I = n1_O; // @[Top.scala 762:10]
  assign n3_clock = clock;
  assign n3_reset = reset;
  assign n3_valid_up = n2_valid_down; // @[Top.scala 766:17]
  assign n3_I = n2_O; // @[Top.scala 765:10]
  assign n4_clock = clock;
  assign n4_reset = reset;
  assign n4_valid_up = n3_valid_down; // @[Top.scala 769:17]
  assign n4_I = n3_O; // @[Top.scala 768:10]
  assign n5_clock = clock;
  assign n5_reset = reset;
  assign n5_valid_up = n4_valid_down; // @[Top.scala 772:17]
  assign n5_I = n4_O; // @[Top.scala 771:10]
  assign n6_valid_up = n5_valid_down & n4_valid_down; // @[Top.scala 776:17]
  assign n6_I0 = n5_O; // @[Top.scala 774:11]
  assign n6_I1 = n4_O; // @[Top.scala 775:11]
  assign n13_valid_up = n6_valid_down & n3_valid_down; // @[Top.scala 780:18]
  assign n13_I0_0 = n6_O_0; // @[Top.scala 778:12]
  assign n13_I0_1 = n6_O_1; // @[Top.scala 778:12]
  assign n13_I1 = n3_O; // @[Top.scala 779:12]
  assign n20_valid_up = n13_valid_down; // @[Top.scala 783:18]
  assign n20_I_0 = n13_O_0; // @[Top.scala 782:11]
  assign n20_I_1 = n13_O_1; // @[Top.scala 782:11]
  assign n20_I_2 = n13_O_2; // @[Top.scala 782:11]
  assign n25_clock = clock;
  assign n25_reset = reset;
  assign n25_valid_up = n20_valid_down; // @[Top.scala 786:18]
  assign n25_I_0_0 = n20_O_0_0; // @[Top.scala 785:11]
  assign n25_I_0_1 = n20_O_0_1; // @[Top.scala 785:11]
  assign n25_I_0_2 = n20_O_0_2; // @[Top.scala 785:11]
  assign n26_clock = clock;
  assign n26_reset = reset;
  assign n26_valid_up = n2_valid_down; // @[Top.scala 789:18]
  assign n26_I = n2_O; // @[Top.scala 788:11]
  assign n27_clock = clock;
  assign n27_reset = reset;
  assign n27_valid_up = n26_valid_down; // @[Top.scala 792:18]
  assign n27_I = n26_O; // @[Top.scala 791:11]
  assign n28_valid_up = n27_valid_down & n26_valid_down; // @[Top.scala 796:18]
  assign n28_I0 = n27_O; // @[Top.scala 794:12]
  assign n28_I1 = n26_O; // @[Top.scala 795:12]
  assign n35_valid_up = n28_valid_down & n2_valid_down; // @[Top.scala 800:18]
  assign n35_I0_0 = n28_O_0; // @[Top.scala 798:12]
  assign n35_I0_1 = n28_O_1; // @[Top.scala 798:12]
  assign n35_I1 = n2_O; // @[Top.scala 799:12]
  assign n42_valid_up = n35_valid_down; // @[Top.scala 803:18]
  assign n42_I_0 = n35_O_0; // @[Top.scala 802:11]
  assign n42_I_1 = n35_O_1; // @[Top.scala 802:11]
  assign n42_I_2 = n35_O_2; // @[Top.scala 802:11]
  assign n47_clock = clock;
  assign n47_reset = reset;
  assign n47_valid_up = n42_valid_down; // @[Top.scala 806:18]
  assign n47_I_0_0 = n42_O_0_0; // @[Top.scala 805:11]
  assign n47_I_0_1 = n42_O_0_1; // @[Top.scala 805:11]
  assign n47_I_0_2 = n42_O_0_2; // @[Top.scala 805:11]
  assign n48_valid_up = n25_valid_down & n47_valid_down; // @[Top.scala 810:18]
  assign n48_I0_0 = n25_O_0; // @[Top.scala 808:12]
  assign n48_I1_0 = n47_O_0; // @[Top.scala 809:12]
  assign n55_clock = clock;
  assign n55_reset = reset;
  assign n55_valid_up = n1_valid_down; // @[Top.scala 813:18]
  assign n55_I = n1_O; // @[Top.scala 812:11]
  assign n56_clock = clock;
  assign n56_reset = reset;
  assign n56_valid_up = n55_valid_down; // @[Top.scala 816:18]
  assign n56_I = n55_O; // @[Top.scala 815:11]
  assign n57_valid_up = n56_valid_down & n55_valid_down; // @[Top.scala 820:18]
  assign n57_I0 = n56_O; // @[Top.scala 818:12]
  assign n57_I1 = n55_O; // @[Top.scala 819:12]
  assign n64_valid_up = n57_valid_down & n1_valid_down; // @[Top.scala 824:18]
  assign n64_I0_0 = n57_O_0; // @[Top.scala 822:12]
  assign n64_I0_1 = n57_O_1; // @[Top.scala 822:12]
  assign n64_I1 = n1_O; // @[Top.scala 823:12]
  assign n71_valid_up = n64_valid_down; // @[Top.scala 827:18]
  assign n71_I_0 = n64_O_0; // @[Top.scala 826:11]
  assign n71_I_1 = n64_O_1; // @[Top.scala 826:11]
  assign n71_I_2 = n64_O_2; // @[Top.scala 826:11]
  assign n76_clock = clock;
  assign n76_reset = reset;
  assign n76_valid_up = n71_valid_down; // @[Top.scala 830:18]
  assign n76_I_0_0 = n71_O_0_0; // @[Top.scala 829:11]
  assign n76_I_0_1 = n71_O_0_1; // @[Top.scala 829:11]
  assign n76_I_0_2 = n71_O_0_2; // @[Top.scala 829:11]
  assign n77_valid_up = n48_valid_down & n76_valid_down; // @[Top.scala 834:18]
  assign n77_I0_0_0 = n48_O_0_0; // @[Top.scala 832:12]
  assign n77_I0_0_1 = n48_O_0_1; // @[Top.scala 832:12]
  assign n77_I1_0 = n76_O_0; // @[Top.scala 833:12]
  assign n84_valid_up = n77_valid_down; // @[Top.scala 837:18]
  assign n84_I_0_0 = n77_O_0_0; // @[Top.scala 836:11]
  assign n84_I_0_1 = n77_O_0_1; // @[Top.scala 836:11]
  assign n84_I_0_2 = n77_O_0_2; // @[Top.scala 836:11]
  assign n89_valid_up = n84_valid_down; // @[Top.scala 840:18]
  assign n89_I_0_0_0 = n84_O_0_0_0; // @[Top.scala 839:11]
  assign n89_I_0_0_1 = n84_O_0_0_1; // @[Top.scala 839:11]
  assign n89_I_0_0_2 = n84_O_0_0_2; // @[Top.scala 839:11]
  assign n90_valid_up = n89_valid_down; // @[Top.scala 843:18]
  assign n90_I_0_0 = n89_O_0_0; // @[Top.scala 842:11]
  assign n90_I_0_1 = n89_O_0_1; // @[Top.scala 842:11]
  assign n90_I_0_2 = n89_O_0_2; // @[Top.scala 842:11]
  assign n440_clock = clock;
  assign n440_reset = reset;
  assign n440_valid_up = n90_valid_down; // @[Top.scala 846:19]
  assign n440_I_0_0 = n90_O_0_0; // @[Top.scala 845:12]
  assign n440_I_0_1 = n90_O_0_1; // @[Top.scala 845:12]
  assign n440_I_0_2 = n90_O_0_2; // @[Top.scala 845:12]
  assign n441_valid_up = n440_valid_down; // @[Top.scala 849:19]
  assign n441_I_0_t0b = n440_O_0_t0b; // @[Top.scala 848:12]
  assign n441_I_0_t1b_t0b = n440_O_0_t1b_t0b; // @[Top.scala 848:12]
  assign n441_I_0_t1b_t1b = n440_O_0_t1b_t1b; // @[Top.scala 848:12]
  assign n442_valid_up = n441_valid_down; // @[Top.scala 852:19]
  assign n442_I_t0b = n441_O_t0b; // @[Top.scala 851:12]
  assign n442_I_t1b_t0b = n441_O_t1b_t0b; // @[Top.scala 851:12]
  assign n442_I_t1b_t1b = n441_O_t1b_t1b; // @[Top.scala 851:12]
  assign n447_valid_up = n442_valid_down; // @[Top.scala 855:19]
  assign n447_I_t0b = n442_O_t0b; // @[Top.scala 854:12]
  assign n448_clock = clock;
  assign n448_reset = reset;
  assign n448_valid_up = n447_valid_down; // @[Top.scala 858:19]
  assign n448_I = n447_O; // @[Top.scala 857:12]
  assign n449_clock = clock;
  assign n449_reset = reset;
  assign n449_valid_up = n448_valid_down; // @[Top.scala 861:19]
  assign n449_I = n448_O; // @[Top.scala 860:12]
  assign n450_clock = clock;
  assign n450_reset = reset;
  assign n450_valid_up = n449_valid_down; // @[Top.scala 864:19]
  assign n450_I = n449_O; // @[Top.scala 863:12]
  assign n451_clock = clock;
  assign n451_reset = reset;
  assign n451_valid_up = n450_valid_down; // @[Top.scala 867:19]
  assign n451_I = n450_O; // @[Top.scala 866:12]
  assign n452_valid_up = n451_valid_down & n450_valid_down; // @[Top.scala 871:19]
  assign n452_I0 = n451_O; // @[Top.scala 869:13]
  assign n452_I1 = n450_O; // @[Top.scala 870:13]
  assign n459_valid_up = n452_valid_down & n449_valid_down; // @[Top.scala 875:19]
  assign n459_I0_0 = n452_O_0; // @[Top.scala 873:13]
  assign n459_I0_1 = n452_O_1; // @[Top.scala 873:13]
  assign n459_I1 = n449_O; // @[Top.scala 874:13]
  assign n466_valid_up = n459_valid_down; // @[Top.scala 878:19]
  assign n466_I_0 = n459_O_0; // @[Top.scala 877:12]
  assign n466_I_1 = n459_O_1; // @[Top.scala 877:12]
  assign n466_I_2 = n459_O_2; // @[Top.scala 877:12]
  assign n471_clock = clock;
  assign n471_reset = reset;
  assign n471_valid_up = n466_valid_down; // @[Top.scala 881:19]
  assign n471_I_0_0 = n466_O_0_0; // @[Top.scala 880:12]
  assign n471_I_0_1 = n466_O_0_1; // @[Top.scala 880:12]
  assign n471_I_0_2 = n466_O_0_2; // @[Top.scala 880:12]
  assign n472_clock = clock;
  assign n472_reset = reset;
  assign n472_valid_up = n448_valid_down; // @[Top.scala 884:19]
  assign n472_I = n448_O; // @[Top.scala 883:12]
  assign n473_clock = clock;
  assign n473_reset = reset;
  assign n473_valid_up = n472_valid_down; // @[Top.scala 887:19]
  assign n473_I = n472_O; // @[Top.scala 886:12]
  assign n474_valid_up = n473_valid_down & n472_valid_down; // @[Top.scala 891:19]
  assign n474_I0 = n473_O; // @[Top.scala 889:13]
  assign n474_I1 = n472_O; // @[Top.scala 890:13]
  assign n481_valid_up = n474_valid_down & n448_valid_down; // @[Top.scala 895:19]
  assign n481_I0_0 = n474_O_0; // @[Top.scala 893:13]
  assign n481_I0_1 = n474_O_1; // @[Top.scala 893:13]
  assign n481_I1 = n448_O; // @[Top.scala 894:13]
  assign n488_valid_up = n481_valid_down; // @[Top.scala 898:19]
  assign n488_I_0 = n481_O_0; // @[Top.scala 897:12]
  assign n488_I_1 = n481_O_1; // @[Top.scala 897:12]
  assign n488_I_2 = n481_O_2; // @[Top.scala 897:12]
  assign n493_clock = clock;
  assign n493_reset = reset;
  assign n493_valid_up = n488_valid_down; // @[Top.scala 901:19]
  assign n493_I_0_0 = n488_O_0_0; // @[Top.scala 900:12]
  assign n493_I_0_1 = n488_O_0_1; // @[Top.scala 900:12]
  assign n493_I_0_2 = n488_O_0_2; // @[Top.scala 900:12]
  assign n494_valid_up = n471_valid_down & n493_valid_down; // @[Top.scala 905:19]
  assign n494_I0_0 = n471_O_0; // @[Top.scala 903:13]
  assign n494_I1_0 = n493_O_0; // @[Top.scala 904:13]
  assign n501_clock = clock;
  assign n501_reset = reset;
  assign n501_valid_up = n447_valid_down; // @[Top.scala 908:19]
  assign n501_I = n447_O; // @[Top.scala 907:12]
  assign n502_clock = clock;
  assign n502_reset = reset;
  assign n502_valid_up = n501_valid_down; // @[Top.scala 911:19]
  assign n502_I = n501_O; // @[Top.scala 910:12]
  assign n503_valid_up = n502_valid_down & n501_valid_down; // @[Top.scala 915:19]
  assign n503_I0 = n502_O; // @[Top.scala 913:13]
  assign n503_I1 = n501_O; // @[Top.scala 914:13]
  assign n510_valid_up = n503_valid_down & n447_valid_down; // @[Top.scala 919:19]
  assign n510_I0_0 = n503_O_0; // @[Top.scala 917:13]
  assign n510_I0_1 = n503_O_1; // @[Top.scala 917:13]
  assign n510_I1 = n447_O; // @[Top.scala 918:13]
  assign n517_valid_up = n510_valid_down; // @[Top.scala 922:19]
  assign n517_I_0 = n510_O_0; // @[Top.scala 921:12]
  assign n517_I_1 = n510_O_1; // @[Top.scala 921:12]
  assign n517_I_2 = n510_O_2; // @[Top.scala 921:12]
  assign n522_clock = clock;
  assign n522_reset = reset;
  assign n522_valid_up = n517_valid_down; // @[Top.scala 925:19]
  assign n522_I_0_0 = n517_O_0_0; // @[Top.scala 924:12]
  assign n522_I_0_1 = n517_O_0_1; // @[Top.scala 924:12]
  assign n522_I_0_2 = n517_O_0_2; // @[Top.scala 924:12]
  assign n523_valid_up = n494_valid_down & n522_valid_down; // @[Top.scala 929:19]
  assign n523_I0_0_0 = n494_O_0_0; // @[Top.scala 927:13]
  assign n523_I0_0_1 = n494_O_0_1; // @[Top.scala 927:13]
  assign n523_I1_0 = n522_O_0; // @[Top.scala 928:13]
  assign n530_valid_up = n523_valid_down; // @[Top.scala 932:19]
  assign n530_I_0_0 = n523_O_0_0; // @[Top.scala 931:12]
  assign n530_I_0_1 = n523_O_0_1; // @[Top.scala 931:12]
  assign n530_I_0_2 = n523_O_0_2; // @[Top.scala 931:12]
  assign n533_valid_up = n530_valid_down; // @[Top.scala 935:19]
  assign n533_I_0_0 = n530_O_0_0; // @[Top.scala 934:12]
  assign n533_I_0_1 = n530_O_0_1; // @[Top.scala 934:12]
  assign n533_I_0_2 = n530_O_0_2; // @[Top.scala 934:12]
  assign n586_clock = clock;
  assign n586_reset = reset;
  assign n586_valid_up = n533_valid_down; // @[Top.scala 938:19]
  assign n586_I_0 = n533_O_0; // @[Top.scala 937:12]
  assign n586_I_1 = n533_O_1; // @[Top.scala 937:12]
  assign n586_I_2 = n533_O_2; // @[Top.scala 937:12]
  assign n587_valid_up = n586_valid_down; // @[Top.scala 941:19]
  assign n587_I = n586_O; // @[Top.scala 940:12]
  assign n588_clock = clock;
  assign n588_reset = reset;
  assign n588_valid_up = n447_valid_down; // @[Top.scala 944:19]
  assign n588_I = n447_O; // @[Top.scala 943:12]
  assign n589_clock = clock;
  assign n589_reset = reset;
  assign n589_valid_up = n587_valid_down & n588_valid_down; // @[Top.scala 948:19]
  assign n589_I0 = n587_O; // @[Top.scala 946:13]
  assign n589_I1 = n588_O; // @[Top.scala 947:13]
  assign n625_valid_up = n442_valid_down; // @[Top.scala 951:19]
  assign n625_I_t1b_t0b = n442_O_t1b_t0b; // @[Top.scala 950:12]
  assign n625_I_t1b_t1b = n442_O_t1b_t1b; // @[Top.scala 950:12]
  assign n626_clock = clock;
  assign n626_reset = reset;
  assign n626_valid_up = n625_valid_down; // @[Top.scala 954:19]
  assign n626_I = n625_O; // @[Top.scala 953:12]
  assign n627_clock = clock;
  assign n627_reset = reset;
  assign n627_valid_up = n626_valid_down; // @[Top.scala 957:19]
  assign n627_I = n626_O; // @[Top.scala 956:12]
  assign n628_clock = clock;
  assign n628_reset = reset;
  assign n628_valid_up = n627_valid_down; // @[Top.scala 960:19]
  assign n628_I = n627_O; // @[Top.scala 959:12]
  assign n629_clock = clock;
  assign n629_reset = reset;
  assign n629_valid_up = n628_valid_down; // @[Top.scala 963:19]
  assign n629_I = n628_O; // @[Top.scala 962:12]
  assign n630_valid_up = n629_valid_down & n628_valid_down; // @[Top.scala 967:19]
  assign n630_I0 = n629_O; // @[Top.scala 965:13]
  assign n630_I1 = n628_O; // @[Top.scala 966:13]
  assign n637_valid_up = n630_valid_down & n627_valid_down; // @[Top.scala 971:19]
  assign n637_I0_0 = n630_O_0; // @[Top.scala 969:13]
  assign n637_I0_1 = n630_O_1; // @[Top.scala 969:13]
  assign n637_I1 = n627_O; // @[Top.scala 970:13]
  assign n644_valid_up = n637_valid_down; // @[Top.scala 974:19]
  assign n644_I_0 = n637_O_0; // @[Top.scala 973:12]
  assign n644_I_1 = n637_O_1; // @[Top.scala 973:12]
  assign n644_I_2 = n637_O_2; // @[Top.scala 973:12]
  assign n649_clock = clock;
  assign n649_reset = reset;
  assign n649_valid_up = n644_valid_down; // @[Top.scala 977:19]
  assign n649_I_0_0 = n644_O_0_0; // @[Top.scala 976:12]
  assign n649_I_0_1 = n644_O_0_1; // @[Top.scala 976:12]
  assign n649_I_0_2 = n644_O_0_2; // @[Top.scala 976:12]
  assign n650_clock = clock;
  assign n650_reset = reset;
  assign n650_valid_up = n626_valid_down; // @[Top.scala 980:19]
  assign n650_I = n626_O; // @[Top.scala 979:12]
  assign n651_clock = clock;
  assign n651_reset = reset;
  assign n651_valid_up = n650_valid_down; // @[Top.scala 983:19]
  assign n651_I = n650_O; // @[Top.scala 982:12]
  assign n652_valid_up = n651_valid_down & n650_valid_down; // @[Top.scala 987:19]
  assign n652_I0 = n651_O; // @[Top.scala 985:13]
  assign n652_I1 = n650_O; // @[Top.scala 986:13]
  assign n659_valid_up = n652_valid_down & n626_valid_down; // @[Top.scala 991:19]
  assign n659_I0_0 = n652_O_0; // @[Top.scala 989:13]
  assign n659_I0_1 = n652_O_1; // @[Top.scala 989:13]
  assign n659_I1 = n626_O; // @[Top.scala 990:13]
  assign n666_valid_up = n659_valid_down; // @[Top.scala 994:19]
  assign n666_I_0 = n659_O_0; // @[Top.scala 993:12]
  assign n666_I_1 = n659_O_1; // @[Top.scala 993:12]
  assign n666_I_2 = n659_O_2; // @[Top.scala 993:12]
  assign n671_clock = clock;
  assign n671_reset = reset;
  assign n671_valid_up = n666_valid_down; // @[Top.scala 997:19]
  assign n671_I_0_0 = n666_O_0_0; // @[Top.scala 996:12]
  assign n671_I_0_1 = n666_O_0_1; // @[Top.scala 996:12]
  assign n671_I_0_2 = n666_O_0_2; // @[Top.scala 996:12]
  assign n672_valid_up = n649_valid_down & n671_valid_down; // @[Top.scala 1001:19]
  assign n672_I0_0 = n649_O_0; // @[Top.scala 999:13]
  assign n672_I1_0 = n671_O_0; // @[Top.scala 1000:13]
  assign n679_clock = clock;
  assign n679_reset = reset;
  assign n679_valid_up = n625_valid_down; // @[Top.scala 1004:19]
  assign n679_I = n625_O; // @[Top.scala 1003:12]
  assign n680_clock = clock;
  assign n680_reset = reset;
  assign n680_valid_up = n679_valid_down; // @[Top.scala 1007:19]
  assign n680_I = n679_O; // @[Top.scala 1006:12]
  assign n681_valid_up = n680_valid_down & n679_valid_down; // @[Top.scala 1011:19]
  assign n681_I0 = n680_O; // @[Top.scala 1009:13]
  assign n681_I1 = n679_O; // @[Top.scala 1010:13]
  assign n688_valid_up = n681_valid_down & n625_valid_down; // @[Top.scala 1015:19]
  assign n688_I0_0 = n681_O_0; // @[Top.scala 1013:13]
  assign n688_I0_1 = n681_O_1; // @[Top.scala 1013:13]
  assign n688_I1 = n625_O; // @[Top.scala 1014:13]
  assign n695_valid_up = n688_valid_down; // @[Top.scala 1018:19]
  assign n695_I_0 = n688_O_0; // @[Top.scala 1017:12]
  assign n695_I_1 = n688_O_1; // @[Top.scala 1017:12]
  assign n695_I_2 = n688_O_2; // @[Top.scala 1017:12]
  assign n700_clock = clock;
  assign n700_reset = reset;
  assign n700_valid_up = n695_valid_down; // @[Top.scala 1021:19]
  assign n700_I_0_0 = n695_O_0_0; // @[Top.scala 1020:12]
  assign n700_I_0_1 = n695_O_0_1; // @[Top.scala 1020:12]
  assign n700_I_0_2 = n695_O_0_2; // @[Top.scala 1020:12]
  assign n701_valid_up = n672_valid_down & n700_valid_down; // @[Top.scala 1025:19]
  assign n701_I0_0_0 = n672_O_0_0; // @[Top.scala 1023:13]
  assign n701_I0_0_1 = n672_O_0_1; // @[Top.scala 1023:13]
  assign n701_I1_0 = n700_O_0; // @[Top.scala 1024:13]
  assign n708_valid_up = n701_valid_down; // @[Top.scala 1028:19]
  assign n708_I_0_0 = n701_O_0_0; // @[Top.scala 1027:12]
  assign n708_I_0_1 = n701_O_0_1; // @[Top.scala 1027:12]
  assign n708_I_0_2 = n701_O_0_2; // @[Top.scala 1027:12]
  assign n711_valid_up = n708_valid_down; // @[Top.scala 1031:19]
  assign n711_I_0_0 = n708_O_0_0; // @[Top.scala 1030:12]
  assign n711_I_0_1 = n708_O_0_1; // @[Top.scala 1030:12]
  assign n711_I_0_2 = n708_O_0_2; // @[Top.scala 1030:12]
  assign n764_clock = clock;
  assign n764_reset = reset;
  assign n764_valid_up = n711_valid_down; // @[Top.scala 1034:19]
  assign n764_I_0 = n711_O_0; // @[Top.scala 1033:12]
  assign n764_I_1 = n711_O_1; // @[Top.scala 1033:12]
  assign n764_I_2 = n711_O_2; // @[Top.scala 1033:12]
  assign n765_valid_up = n764_valid_down; // @[Top.scala 1037:19]
  assign n765_I = n764_O; // @[Top.scala 1036:12]
  assign n766_clock = clock;
  assign n766_reset = reset;
  assign n766_valid_up = n625_valid_down; // @[Top.scala 1040:19]
  assign n766_I = n625_O; // @[Top.scala 1039:12]
  assign n767_clock = clock;
  assign n767_reset = reset;
  assign n767_valid_up = n765_valid_down & n766_valid_down; // @[Top.scala 1044:19]
  assign n767_I0 = n765_O; // @[Top.scala 1042:13]
  assign n767_I1 = n766_O; // @[Top.scala 1043:13]
  assign n803_valid_up = n442_valid_down; // @[Top.scala 1047:19]
  assign n803_I_t1b_t0b = n442_O_t1b_t0b; // @[Top.scala 1046:12]
  assign n803_I_t1b_t1b = n442_O_t1b_t1b; // @[Top.scala 1046:12]
  assign n804_clock = clock;
  assign n804_reset = reset;
  assign n804_valid_up = n803_valid_down; // @[Top.scala 1050:19]
  assign n804_I = n803_O; // @[Top.scala 1049:12]
  assign n805_clock = clock;
  assign n805_reset = reset;
  assign n805_valid_up = n804_valid_down; // @[Top.scala 1053:19]
  assign n805_I = n804_O; // @[Top.scala 1052:12]
  assign n806_clock = clock;
  assign n806_reset = reset;
  assign n806_valid_up = n805_valid_down; // @[Top.scala 1056:19]
  assign n806_I = n805_O; // @[Top.scala 1055:12]
  assign n807_clock = clock;
  assign n807_reset = reset;
  assign n807_valid_up = n806_valid_down; // @[Top.scala 1059:19]
  assign n807_I = n806_O; // @[Top.scala 1058:12]
  assign n808_valid_up = n807_valid_down & n806_valid_down; // @[Top.scala 1063:19]
  assign n808_I0 = n807_O; // @[Top.scala 1061:13]
  assign n808_I1 = n806_O; // @[Top.scala 1062:13]
  assign n815_valid_up = n808_valid_down & n805_valid_down; // @[Top.scala 1067:19]
  assign n815_I0_0 = n808_O_0; // @[Top.scala 1065:13]
  assign n815_I0_1 = n808_O_1; // @[Top.scala 1065:13]
  assign n815_I1 = n805_O; // @[Top.scala 1066:13]
  assign n822_valid_up = n815_valid_down; // @[Top.scala 1070:19]
  assign n822_I_0 = n815_O_0; // @[Top.scala 1069:12]
  assign n822_I_1 = n815_O_1; // @[Top.scala 1069:12]
  assign n822_I_2 = n815_O_2; // @[Top.scala 1069:12]
  assign n827_clock = clock;
  assign n827_reset = reset;
  assign n827_valid_up = n822_valid_down; // @[Top.scala 1073:19]
  assign n827_I_0_0 = n822_O_0_0; // @[Top.scala 1072:12]
  assign n827_I_0_1 = n822_O_0_1; // @[Top.scala 1072:12]
  assign n827_I_0_2 = n822_O_0_2; // @[Top.scala 1072:12]
  assign n828_clock = clock;
  assign n828_reset = reset;
  assign n828_valid_up = n804_valid_down; // @[Top.scala 1076:19]
  assign n828_I = n804_O; // @[Top.scala 1075:12]
  assign n829_clock = clock;
  assign n829_reset = reset;
  assign n829_valid_up = n828_valid_down; // @[Top.scala 1079:19]
  assign n829_I = n828_O; // @[Top.scala 1078:12]
  assign n830_valid_up = n829_valid_down & n828_valid_down; // @[Top.scala 1083:19]
  assign n830_I0 = n829_O; // @[Top.scala 1081:13]
  assign n830_I1 = n828_O; // @[Top.scala 1082:13]
  assign n837_valid_up = n830_valid_down & n804_valid_down; // @[Top.scala 1087:19]
  assign n837_I0_0 = n830_O_0; // @[Top.scala 1085:13]
  assign n837_I0_1 = n830_O_1; // @[Top.scala 1085:13]
  assign n837_I1 = n804_O; // @[Top.scala 1086:13]
  assign n844_valid_up = n837_valid_down; // @[Top.scala 1090:19]
  assign n844_I_0 = n837_O_0; // @[Top.scala 1089:12]
  assign n844_I_1 = n837_O_1; // @[Top.scala 1089:12]
  assign n844_I_2 = n837_O_2; // @[Top.scala 1089:12]
  assign n849_clock = clock;
  assign n849_reset = reset;
  assign n849_valid_up = n844_valid_down; // @[Top.scala 1093:19]
  assign n849_I_0_0 = n844_O_0_0; // @[Top.scala 1092:12]
  assign n849_I_0_1 = n844_O_0_1; // @[Top.scala 1092:12]
  assign n849_I_0_2 = n844_O_0_2; // @[Top.scala 1092:12]
  assign n850_valid_up = n827_valid_down & n849_valid_down; // @[Top.scala 1097:19]
  assign n850_I0_0 = n827_O_0; // @[Top.scala 1095:13]
  assign n850_I1_0 = n849_O_0; // @[Top.scala 1096:13]
  assign n857_clock = clock;
  assign n857_reset = reset;
  assign n857_valid_up = n803_valid_down; // @[Top.scala 1100:19]
  assign n857_I = n803_O; // @[Top.scala 1099:12]
  assign n858_clock = clock;
  assign n858_reset = reset;
  assign n858_valid_up = n857_valid_down; // @[Top.scala 1103:19]
  assign n858_I = n857_O; // @[Top.scala 1102:12]
  assign n859_valid_up = n858_valid_down & n857_valid_down; // @[Top.scala 1107:19]
  assign n859_I0 = n858_O; // @[Top.scala 1105:13]
  assign n859_I1 = n857_O; // @[Top.scala 1106:13]
  assign n866_valid_up = n859_valid_down & n803_valid_down; // @[Top.scala 1111:19]
  assign n866_I0_0 = n859_O_0; // @[Top.scala 1109:13]
  assign n866_I0_1 = n859_O_1; // @[Top.scala 1109:13]
  assign n866_I1 = n803_O; // @[Top.scala 1110:13]
  assign n873_valid_up = n866_valid_down; // @[Top.scala 1114:19]
  assign n873_I_0 = n866_O_0; // @[Top.scala 1113:12]
  assign n873_I_1 = n866_O_1; // @[Top.scala 1113:12]
  assign n873_I_2 = n866_O_2; // @[Top.scala 1113:12]
  assign n878_clock = clock;
  assign n878_reset = reset;
  assign n878_valid_up = n873_valid_down; // @[Top.scala 1117:19]
  assign n878_I_0_0 = n873_O_0_0; // @[Top.scala 1116:12]
  assign n878_I_0_1 = n873_O_0_1; // @[Top.scala 1116:12]
  assign n878_I_0_2 = n873_O_0_2; // @[Top.scala 1116:12]
  assign n879_valid_up = n850_valid_down & n878_valid_down; // @[Top.scala 1121:19]
  assign n879_I0_0_0 = n850_O_0_0; // @[Top.scala 1119:13]
  assign n879_I0_0_1 = n850_O_0_1; // @[Top.scala 1119:13]
  assign n879_I1_0 = n878_O_0; // @[Top.scala 1120:13]
  assign n886_valid_up = n879_valid_down; // @[Top.scala 1124:19]
  assign n886_I_0_0 = n879_O_0_0; // @[Top.scala 1123:12]
  assign n886_I_0_1 = n879_O_0_1; // @[Top.scala 1123:12]
  assign n886_I_0_2 = n879_O_0_2; // @[Top.scala 1123:12]
  assign n889_valid_up = n886_valid_down; // @[Top.scala 1127:19]
  assign n889_I_0_0 = n886_O_0_0; // @[Top.scala 1126:12]
  assign n889_I_0_1 = n886_O_0_1; // @[Top.scala 1126:12]
  assign n889_I_0_2 = n886_O_0_2; // @[Top.scala 1126:12]
  assign n942_clock = clock;
  assign n942_reset = reset;
  assign n942_valid_up = n889_valid_down; // @[Top.scala 1130:19]
  assign n942_I_0 = n889_O_0; // @[Top.scala 1129:12]
  assign n942_I_1 = n889_O_1; // @[Top.scala 1129:12]
  assign n942_I_2 = n889_O_2; // @[Top.scala 1129:12]
  assign n943_valid_up = n942_valid_down; // @[Top.scala 1133:19]
  assign n943_I = n942_O; // @[Top.scala 1132:12]
  assign n944_clock = clock;
  assign n944_reset = reset;
  assign n944_valid_up = n803_valid_down; // @[Top.scala 1136:19]
  assign n944_I = n803_O; // @[Top.scala 1135:12]
  assign n945_clock = clock;
  assign n945_reset = reset;
  assign n945_valid_up = n943_valid_down & n944_valid_down; // @[Top.scala 1140:19]
  assign n945_I0 = n943_O; // @[Top.scala 1138:13]
  assign n945_I1 = n944_O; // @[Top.scala 1139:13]
  assign n976_valid_up = n767_valid_down & n945_valid_down; // @[Top.scala 1144:19]
  assign n976_I0 = n767_O; // @[Top.scala 1142:13]
  assign n976_I1 = n945_O; // @[Top.scala 1143:13]
  assign n983_valid_up = n589_valid_down & n976_valid_down; // @[Top.scala 1148:19]
  assign n983_I0 = n589_O; // @[Top.scala 1146:13]
  assign n983_I1_t0b = n976_O_t0b; // @[Top.scala 1147:13]
  assign n983_I1_t1b = n976_O_t1b; // @[Top.scala 1147:13]
  assign n990_clock = clock;
  assign n990_reset = reset;
  assign n990_valid_up = n983_valid_down; // @[Top.scala 1151:19]
  assign n990_I_t0b = n983_O_t0b; // @[Top.scala 1150:12]
  assign n990_I_t1b_t0b = n983_O_t1b_t0b; // @[Top.scala 1150:12]
  assign n990_I_t1b_t1b = n983_O_t1b_t1b; // @[Top.scala 1150:12]
  assign n991_clock = clock;
  assign n991_reset = reset;
  assign n991_valid_up = n990_valid_down; // @[Top.scala 1154:19]
  assign n991_I_t0b = n990_O_t0b; // @[Top.scala 1153:12]
  assign n991_I_t1b_t0b = n990_O_t1b_t0b; // @[Top.scala 1153:12]
  assign n991_I_t1b_t1b = n990_O_t1b_t1b; // @[Top.scala 1153:12]
  assign n992_clock = clock;
  assign n992_reset = reset;
  assign n992_valid_up = n991_valid_down; // @[Top.scala 1157:19]
  assign n992_I_t0b = n991_O_t0b; // @[Top.scala 1156:12]
  assign n992_I_t1b_t0b = n991_O_t1b_t0b; // @[Top.scala 1156:12]
  assign n992_I_t1b_t1b = n991_O_t1b_t1b; // @[Top.scala 1156:12]
endmodule
