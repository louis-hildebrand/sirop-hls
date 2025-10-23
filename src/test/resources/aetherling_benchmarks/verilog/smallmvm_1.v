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
module RAM_ST(
  input        clock,
  input        RE,
  output [7:0] RDATA_0,
  output [7:0] RDATA_1,
  output [7:0] RDATA_2,
  output [7:0] RDATA_3,
  input        WE,
  input  [7:0] WDATA_0,
  input  [7:0] WDATA_1,
  input  [7:0] WDATA_2,
  input  [7:0] WDATA_3
);
  wire  write_elem_counter_CE; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_valid; // @[RAM_ST.scala 20:34]
  wire  read_elem_counter_CE; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_valid; // @[RAM_ST.scala 21:33]
  reg [31:0] ram [0:0]; // @[RAM_ST.scala 29:24]
  reg [31:0] _RAND_0;
  wire [31:0] ram__T_9_data; // @[RAM_ST.scala 29:24]
  wire  ram__T_9_addr; // @[RAM_ST.scala 29:24]
  wire [31:0] ram__T_5_data; // @[RAM_ST.scala 29:24]
  wire  ram__T_5_addr; // @[RAM_ST.scala 29:24]
  wire  ram__T_5_mask; // @[RAM_ST.scala 29:24]
  wire  ram__T_5_en; // @[RAM_ST.scala 29:24]
  reg  ram__T_9_en_pipe_0;
  reg [31:0] _RAND_1;
  reg  ram__T_9_addr_pipe_0;
  reg [31:0] _RAND_2;
  wire [15:0] _T_2 = {WDATA_1,WDATA_0}; // @[RAM_ST.scala 31:115]
  wire [15:0] _T_3 = {WDATA_3,WDATA_2}; // @[RAM_ST.scala 31:115]
  wire [31:0] _T_11 = ram__T_9_data;
  NestedCountersWithNumValid write_elem_counter ( // @[RAM_ST.scala 20:34]
    .CE(write_elem_counter_CE),
    .valid(write_elem_counter_valid)
  );
  NestedCountersWithNumValid read_elem_counter ( // @[RAM_ST.scala 21:33]
    .CE(read_elem_counter_CE),
    .valid(read_elem_counter_valid)
  );
  assign ram__T_9_addr = ram__T_9_addr_pipe_0;
  assign ram__T_9_data = ram[ram__T_9_addr]; // @[RAM_ST.scala 29:24]
  assign ram__T_5_data = {_T_3,_T_2};
  assign ram__T_5_addr = 1'h0;
  assign ram__T_5_mask = 1'h1;
  assign ram__T_5_en = write_elem_counter_valid;
  assign RDATA_0 = _T_11[7:0]; // @[RAM_ST.scala 32:9]
  assign RDATA_1 = _T_11[15:8]; // @[RAM_ST.scala 32:9]
  assign RDATA_2 = _T_11[23:16]; // @[RAM_ST.scala 32:9]
  assign RDATA_3 = _T_11[31:24]; // @[RAM_ST.scala 32:9]
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
  ram__T_9_en_pipe_0 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  ram__T_9_addr_pipe_0 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if(ram__T_5_en & ram__T_5_mask) begin
      ram[ram__T_5_addr] <= ram__T_5_data; // @[RAM_ST.scala 29:24]
    end
    ram__T_9_en_pipe_0 <= read_elem_counter_valid;
    if (read_elem_counter_valid) begin
      ram__T_9_addr_pipe_0 <= 1'h0;
    end
  end
endmodule
module UpT(
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
  wire  mem_clock; // @[Upsample.scala 31:19]
  wire  mem_RE; // @[Upsample.scala 31:19]
  wire [7:0] mem_RDATA_0; // @[Upsample.scala 31:19]
  wire [7:0] mem_RDATA_1; // @[Upsample.scala 31:19]
  wire [7:0] mem_RDATA_2; // @[Upsample.scala 31:19]
  wire [7:0] mem_RDATA_3; // @[Upsample.scala 31:19]
  wire  mem_WE; // @[Upsample.scala 31:19]
  wire [7:0] mem_WDATA_0; // @[Upsample.scala 31:19]
  wire [7:0] mem_WDATA_1; // @[Upsample.scala 31:19]
  wire [7:0] mem_WDATA_2; // @[Upsample.scala 31:19]
  wire [7:0] mem_WDATA_3; // @[Upsample.scala 31:19]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire [1:0] _T_6 = value + 2'h1; // @[Counter.scala 39:22]
  wire  _T_7 = value == 2'h0; // @[Upsample.scala 39:36]
  wire [7:0] dataOut_0 = mem_RDATA_0; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [7:0] dataOut_1 = mem_RDATA_1; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [7:0] dataOut_2 = mem_RDATA_2; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [7:0] dataOut_3 = mem_RDATA_3; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  RAM_ST mem ( // @[Upsample.scala 31:19]
    .clock(mem_clock),
    .RE(mem_RE),
    .RDATA_0(mem_RDATA_0),
    .RDATA_1(mem_RDATA_1),
    .RDATA_2(mem_RDATA_2),
    .RDATA_3(mem_RDATA_3),
    .WE(mem_WE),
    .WDATA_0(mem_WDATA_0),
    .WDATA_1(mem_WDATA_1),
    .WDATA_2(mem_WDATA_2),
    .WDATA_3(mem_WDATA_3)
  );
  assign valid_down = valid_up; // @[Upsample.scala 46:14]
  assign O_0 = _T_7 ? I_0 : dataOut_0; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_1 = _T_7 ? I_1 : dataOut_1; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_2 = _T_7 ? I_2 : dataOut_2; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_3 = _T_7 ? I_3 : dataOut_3; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign mem_clock = clock;
  assign mem_RE = valid_up; // @[Upsample.scala 40:110 Upsample.scala 41:27 Upsample.scala 42:44]
  assign mem_WE = valid_up & _T_7; // @[Upsample.scala 39:54 Upsample.scala 39:86 Upsample.scala 42:25]
  assign mem_WDATA_0 = I_0; // @[Upsample.scala 36:13]
  assign mem_WDATA_1 = I_1; // @[Upsample.scala 36:13]
  assign mem_WDATA_2 = I_2; // @[Upsample.scala 36:13]
  assign mem_WDATA_3 = I_3; // @[Upsample.scala 36:13]
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
    end else if (valid_up) begin
      value <= _T_6;
    end
  end
endmodule
module Passthrough(
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
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_0 = I_0; // @[Passthrough.scala 17:68]
  assign O_1 = I_1; // @[Passthrough.scala 17:68]
  assign O_2 = I_2; // @[Passthrough.scala 17:68]
  assign O_3 = I_3; // @[Passthrough.scala 17:68]
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
  wire [7:0] BlackBoxMulUInt8_I0; // @[Arithmetic.scala 168:27]
  wire [7:0] BlackBoxMulUInt8_I1; // @[Arithmetic.scala 168:27]
  wire [15:0] BlackBoxMulUInt8_O; // @[Arithmetic.scala 168:27]
  wire  BlackBoxMulUInt8_clock; // @[Arithmetic.scala 168:27]
  reg  _T_1; // @[Arithmetic.scala 220:42]
  reg [31:0] _RAND_0;
  reg  _T_2; // @[Arithmetic.scala 220:34]
  reg [31:0] _RAND_1;
  reg  _T_3; // @[Arithmetic.scala 220:26]
  reg [31:0] _RAND_2;
  BlackBoxMulUInt8 BlackBoxMulUInt8 ( // @[Arithmetic.scala 168:27]
    .I0(BlackBoxMulUInt8_I0),
    .I1(BlackBoxMulUInt8_I1),
    .O(BlackBoxMulUInt8_O),
    .clock(BlackBoxMulUInt8_clock)
  );
  assign valid_down = _T_3; // @[Arithmetic.scala 220:16]
  assign O = BlackBoxMulUInt8_O[7:0]; // @[Arithmetic.scala 171:7]
  assign BlackBoxMulUInt8_I0 = I_t0b; // @[Arithmetic.scala 169:21]
  assign BlackBoxMulUInt8_I1 = I_t1b; // @[Arithmetic.scala 170:21]
  assign BlackBoxMulUInt8_clock = clock; // @[Arithmetic.scala 172:24]
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
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O
);
  wire  n12_valid_up; // @[Top.scala 18:21]
  wire  n12_valid_down; // @[Top.scala 18:21]
  wire [7:0] n12_I0; // @[Top.scala 18:21]
  wire [7:0] n12_I1; // @[Top.scala 18:21]
  wire [7:0] n12_O_t0b; // @[Top.scala 18:21]
  wire [7:0] n12_O_t1b; // @[Top.scala 18:21]
  wire  n13_clock; // @[Top.scala 22:21]
  wire  n13_reset; // @[Top.scala 22:21]
  wire  n13_valid_up; // @[Top.scala 22:21]
  wire  n13_valid_down; // @[Top.scala 22:21]
  wire [7:0] n13_I_t0b; // @[Top.scala 22:21]
  wire [7:0] n13_I_t1b; // @[Top.scala 22:21]
  wire [7:0] n13_O; // @[Top.scala 22:21]
  AtomTuple n12 ( // @[Top.scala 18:21]
    .valid_up(n12_valid_up),
    .valid_down(n12_valid_down),
    .I0(n12_I0),
    .I1(n12_I1),
    .O_t0b(n12_O_t0b),
    .O_t1b(n12_O_t1b)
  );
  Mul n13 ( // @[Top.scala 22:21]
    .clock(n13_clock),
    .reset(n13_reset),
    .valid_up(n13_valid_up),
    .valid_down(n13_valid_down),
    .I_t0b(n13_I_t0b),
    .I_t1b(n13_I_t1b),
    .O(n13_O)
  );
  assign valid_down = n13_valid_down; // @[Top.scala 26:16]
  assign O = n13_O; // @[Top.scala 25:7]
  assign n12_valid_up = valid_up; // @[Top.scala 21:18]
  assign n12_I0 = I0; // @[Top.scala 19:12]
  assign n12_I1 = I1; // @[Top.scala 20:12]
  assign n13_clock = clock;
  assign n13_reset = reset;
  assign n13_valid_up = n12_valid_down; // @[Top.scala 24:18]
  assign n13_I_t0b = n12_O_t0b; // @[Top.scala 23:11]
  assign n13_I_t1b = n12_O_t1b; // @[Top.scala 23:11]
endmodule
module Map2S(
  input        clock,
  input        reset,
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
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2,
  output [7:0] O_3
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
  wire  other_ops_2_clock; // @[Map2S.scala 10:86]
  wire  other_ops_2_reset; // @[Map2S.scala 10:86]
  wire  other_ops_2_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_2_valid_down; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_2_I0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_2_I1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_2_O; // @[Map2S.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[Map2S.scala 26:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[Map2S.scala 26:83]
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
  assign valid_down = _T_1 & other_ops_2_valid_down; // @[Map2S.scala 26:14]
  assign O_0 = fst_op_O; // @[Map2S.scala 19:8]
  assign O_1 = other_ops_0_O; // @[Map2S.scala 24:12]
  assign O_2 = other_ops_1_O; // @[Map2S.scala 24:12]
  assign O_3 = other_ops_2_O; // @[Map2S.scala 24:12]
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
endmodule
module Map2T(
  input        clock,
  input        reset,
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
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2,
  output [7:0] O_3
);
  wire  op_clock; // @[Map2T.scala 8:20]
  wire  op_reset; // @[Map2T.scala 8:20]
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
  wire [7:0] op_O_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_2; // @[Map2T.scala 8:20]
  wire [7:0] op_O_3; // @[Map2T.scala 8:20]
  Map2S op ( // @[Map2T.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
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
  assign op_clock = clock;
  assign op_reset = reset;
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
module AddNoValid(
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  assign O = I_t0b + I_t1b; // @[Arithmetic.scala 125:7]
endmodule
module ReduceS(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  input  [7:0] I_3,
  output [7:0] O_0
);
  wire [7:0] AddNoValid_I_t0b; // @[ReduceS.scala 20:43]
  wire [7:0] AddNoValid_I_t1b; // @[ReduceS.scala 20:43]
  wire [7:0] AddNoValid_O; // @[ReduceS.scala 20:43]
  wire [7:0] AddNoValid_1_I_t0b; // @[ReduceS.scala 20:43]
  wire [7:0] AddNoValid_1_I_t1b; // @[ReduceS.scala 20:43]
  wire [7:0] AddNoValid_1_O; // @[ReduceS.scala 20:43]
  wire [7:0] AddNoValid_2_I_t0b; // @[ReduceS.scala 20:43]
  wire [7:0] AddNoValid_2_I_t1b; // @[ReduceS.scala 20:43]
  wire [7:0] AddNoValid_2_O; // @[ReduceS.scala 20:43]
  reg [7:0] _T; // @[ReduceS.scala 27:24]
  reg [31:0] _RAND_0;
  reg [7:0] _T_1; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_1;
  reg [7:0] _T_2; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_2;
  reg [7:0] _T_3; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_3;
  reg [7:0] _T_4; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_4;
  reg  _T_5; // @[ReduceS.scala 47:32]
  reg [31:0] _RAND_5;
  reg  _T_6; // @[ReduceS.scala 47:24]
  reg [31:0] _RAND_6;
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
  assign valid_down = _T_6; // @[ReduceS.scala 47:14]
  assign O_0 = _T; // @[ReduceS.scala 27:14]
  assign AddNoValid_I_t0b = _T_1; // @[ReduceS.scala 43:18]
  assign AddNoValid_I_t1b = AddNoValid_1_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_1_I_t0b = AddNoValid_2_O; // @[ReduceS.scala 31:18]
  assign AddNoValid_1_I_t1b = _T_4; // @[ReduceS.scala 43:18]
  assign AddNoValid_2_I_t0b = _T_3; // @[ReduceS.scala 43:18]
  assign AddNoValid_2_I_t1b = _T_2; // @[ReduceS.scala 43:18]
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
  _T_4 = _RAND_4[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_5 = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_6 = _RAND_6[0:0];
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
    if (reset) begin
      _T_5 <= 1'h0;
    end else begin
      _T_5 <= valid_up;
    end
    _T_6 <= _T_5;
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
  input  [7:0] I_3,
  output [7:0] O_0
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_2; // @[MapT.scala 8:20]
  wire [7:0] op_I_3; // @[MapT.scala 8:20]
  wire [7:0] op_O_0; // @[MapT.scala 8:20]
  ReduceS op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0(op_I_0),
    .I_1(op_I_1),
    .I_2(op_I_2),
    .I_3(op_I_3),
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
endmodule
module Passthrough_1(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  output [7:0] O_0
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_0 = I_0; // @[Passthrough.scala 17:68]
endmodule
module FIFO_2(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  output [7:0] O_0
);
  reg [7:0] _T_0; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_0;
  reg  _T_1; // @[FIFO.scala 15:27]
  reg [31:0] _RAND_1;
  assign valid_down = _T_1; // @[FIFO.scala 16:16]
  assign O_0 = _T_0; // @[FIFO.scala 14:7]
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
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_1 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    _T_0 <= I_0;
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
  input  [7:0] I0_0,
  input  [7:0] I0_1,
  input  [7:0] I0_2,
  input  [7:0] I0_3,
  input  [7:0] I1_0,
  input  [7:0] I1_1,
  input  [7:0] I1_2,
  input  [7:0] I1_3,
  output [7:0] O_0
);
  wire  n1_clock; // @[Top.scala 33:20]
  wire  n1_reset; // @[Top.scala 33:20]
  wire  n1_valid_up; // @[Top.scala 33:20]
  wire  n1_valid_down; // @[Top.scala 33:20]
  wire [7:0] n1_I_0; // @[Top.scala 33:20]
  wire [7:0] n1_I_1; // @[Top.scala 33:20]
  wire [7:0] n1_I_2; // @[Top.scala 33:20]
  wire [7:0] n1_I_3; // @[Top.scala 33:20]
  wire [7:0] n1_O_0; // @[Top.scala 33:20]
  wire [7:0] n1_O_1; // @[Top.scala 33:20]
  wire [7:0] n1_O_2; // @[Top.scala 33:20]
  wire [7:0] n1_O_3; // @[Top.scala 33:20]
  wire  n3_clock; // @[Top.scala 36:20]
  wire  n3_reset; // @[Top.scala 36:20]
  wire  n3_valid_up; // @[Top.scala 36:20]
  wire  n3_valid_down; // @[Top.scala 36:20]
  wire [7:0] n3_I_0; // @[Top.scala 36:20]
  wire [7:0] n3_I_1; // @[Top.scala 36:20]
  wire [7:0] n3_I_2; // @[Top.scala 36:20]
  wire [7:0] n3_I_3; // @[Top.scala 36:20]
  wire [7:0] n3_O_0; // @[Top.scala 36:20]
  wire [7:0] n3_O_1; // @[Top.scala 36:20]
  wire [7:0] n3_O_2; // @[Top.scala 36:20]
  wire [7:0] n3_O_3; // @[Top.scala 36:20]
  wire  n4_clock; // @[Top.scala 39:20]
  wire  n4_reset; // @[Top.scala 39:20]
  wire  n4_valid_up; // @[Top.scala 39:20]
  wire  n4_valid_down; // @[Top.scala 39:20]
  wire [7:0] n4_I_0; // @[Top.scala 39:20]
  wire [7:0] n4_I_1; // @[Top.scala 39:20]
  wire [7:0] n4_I_2; // @[Top.scala 39:20]
  wire [7:0] n4_I_3; // @[Top.scala 39:20]
  wire [7:0] n4_O_0; // @[Top.scala 39:20]
  wire [7:0] n4_O_1; // @[Top.scala 39:20]
  wire [7:0] n4_O_2; // @[Top.scala 39:20]
  wire [7:0] n4_O_3; // @[Top.scala 39:20]
  wire  n5_valid_up; // @[Top.scala 42:20]
  wire  n5_valid_down; // @[Top.scala 42:20]
  wire [7:0] n5_I_0; // @[Top.scala 42:20]
  wire [7:0] n5_I_1; // @[Top.scala 42:20]
  wire [7:0] n5_I_2; // @[Top.scala 42:20]
  wire [7:0] n5_I_3; // @[Top.scala 42:20]
  wire [7:0] n5_O_0; // @[Top.scala 42:20]
  wire [7:0] n5_O_1; // @[Top.scala 42:20]
  wire [7:0] n5_O_2; // @[Top.scala 42:20]
  wire [7:0] n5_O_3; // @[Top.scala 42:20]
  wire  n6_clock; // @[Top.scala 45:20]
  wire  n6_reset; // @[Top.scala 45:20]
  wire  n6_valid_up; // @[Top.scala 45:20]
  wire  n6_valid_down; // @[Top.scala 45:20]
  wire [7:0] n6_I0_0; // @[Top.scala 45:20]
  wire [7:0] n6_I0_1; // @[Top.scala 45:20]
  wire [7:0] n6_I0_2; // @[Top.scala 45:20]
  wire [7:0] n6_I0_3; // @[Top.scala 45:20]
  wire [7:0] n6_I1_0; // @[Top.scala 45:20]
  wire [7:0] n6_I1_1; // @[Top.scala 45:20]
  wire [7:0] n6_I1_2; // @[Top.scala 45:20]
  wire [7:0] n6_I1_3; // @[Top.scala 45:20]
  wire [7:0] n6_O_0; // @[Top.scala 45:20]
  wire [7:0] n6_O_1; // @[Top.scala 45:20]
  wire [7:0] n6_O_2; // @[Top.scala 45:20]
  wire [7:0] n6_O_3; // @[Top.scala 45:20]
  wire  n18_clock; // @[Top.scala 49:21]
  wire  n18_reset; // @[Top.scala 49:21]
  wire  n18_valid_up; // @[Top.scala 49:21]
  wire  n18_valid_down; // @[Top.scala 49:21]
  wire [7:0] n18_I_0; // @[Top.scala 49:21]
  wire [7:0] n18_I_1; // @[Top.scala 49:21]
  wire [7:0] n18_I_2; // @[Top.scala 49:21]
  wire [7:0] n18_I_3; // @[Top.scala 49:21]
  wire [7:0] n18_O_0; // @[Top.scala 49:21]
  wire  n19_valid_up; // @[Top.scala 52:21]
  wire  n19_valid_down; // @[Top.scala 52:21]
  wire [7:0] n19_I_0; // @[Top.scala 52:21]
  wire [7:0] n19_O_0; // @[Top.scala 52:21]
  wire  n20_clock; // @[Top.scala 55:21]
  wire  n20_reset; // @[Top.scala 55:21]
  wire  n20_valid_up; // @[Top.scala 55:21]
  wire  n20_valid_down; // @[Top.scala 55:21]
  wire [7:0] n20_I_0; // @[Top.scala 55:21]
  wire [7:0] n20_O_0; // @[Top.scala 55:21]
  wire  n21_clock; // @[Top.scala 58:21]
  wire  n21_reset; // @[Top.scala 58:21]
  wire  n21_valid_up; // @[Top.scala 58:21]
  wire  n21_valid_down; // @[Top.scala 58:21]
  wire [7:0] n21_I_0; // @[Top.scala 58:21]
  wire [7:0] n21_O_0; // @[Top.scala 58:21]
  wire  n22_clock; // @[Top.scala 61:21]
  wire  n22_reset; // @[Top.scala 61:21]
  wire  n22_valid_up; // @[Top.scala 61:21]
  wire  n22_valid_down; // @[Top.scala 61:21]
  wire [7:0] n22_I_0; // @[Top.scala 61:21]
  wire [7:0] n22_O_0; // @[Top.scala 61:21]
  FIFO n1 ( // @[Top.scala 33:20]
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
  FIFO n3 ( // @[Top.scala 36:20]
    .clock(n3_clock),
    .reset(n3_reset),
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
  UpT n4 ( // @[Top.scala 39:20]
    .clock(n4_clock),
    .reset(n4_reset),
    .valid_up(n4_valid_up),
    .valid_down(n4_valid_down),
    .I_0(n4_I_0),
    .I_1(n4_I_1),
    .I_2(n4_I_2),
    .I_3(n4_I_3),
    .O_0(n4_O_0),
    .O_1(n4_O_1),
    .O_2(n4_O_2),
    .O_3(n4_O_3)
  );
  Passthrough n5 ( // @[Top.scala 42:20]
    .valid_up(n5_valid_up),
    .valid_down(n5_valid_down),
    .I_0(n5_I_0),
    .I_1(n5_I_1),
    .I_2(n5_I_2),
    .I_3(n5_I_3),
    .O_0(n5_O_0),
    .O_1(n5_O_1),
    .O_2(n5_O_2),
    .O_3(n5_O_3)
  );
  Map2T n6 ( // @[Top.scala 45:20]
    .clock(n6_clock),
    .reset(n6_reset),
    .valid_up(n6_valid_up),
    .valid_down(n6_valid_down),
    .I0_0(n6_I0_0),
    .I0_1(n6_I0_1),
    .I0_2(n6_I0_2),
    .I0_3(n6_I0_3),
    .I1_0(n6_I1_0),
    .I1_1(n6_I1_1),
    .I1_2(n6_I1_2),
    .I1_3(n6_I1_3),
    .O_0(n6_O_0),
    .O_1(n6_O_1),
    .O_2(n6_O_2),
    .O_3(n6_O_3)
  );
  MapT n18 ( // @[Top.scala 49:21]
    .clock(n18_clock),
    .reset(n18_reset),
    .valid_up(n18_valid_up),
    .valid_down(n18_valid_down),
    .I_0(n18_I_0),
    .I_1(n18_I_1),
    .I_2(n18_I_2),
    .I_3(n18_I_3),
    .O_0(n18_O_0)
  );
  Passthrough_1 n19 ( // @[Top.scala 52:21]
    .valid_up(n19_valid_up),
    .valid_down(n19_valid_down),
    .I_0(n19_I_0),
    .O_0(n19_O_0)
  );
  FIFO_2 n20 ( // @[Top.scala 55:21]
    .clock(n20_clock),
    .reset(n20_reset),
    .valid_up(n20_valid_up),
    .valid_down(n20_valid_down),
    .I_0(n20_I_0),
    .O_0(n20_O_0)
  );
  FIFO_2 n21 ( // @[Top.scala 58:21]
    .clock(n21_clock),
    .reset(n21_reset),
    .valid_up(n21_valid_up),
    .valid_down(n21_valid_down),
    .I_0(n21_I_0),
    .O_0(n21_O_0)
  );
  FIFO_2 n22 ( // @[Top.scala 61:21]
    .clock(n22_clock),
    .reset(n22_reset),
    .valid_up(n22_valid_up),
    .valid_down(n22_valid_down),
    .I_0(n22_I_0),
    .O_0(n22_O_0)
  );
  assign valid_down = n22_valid_down; // @[Top.scala 65:16]
  assign O_0 = n22_O_0; // @[Top.scala 64:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 35:17]
  assign n1_I_0 = I0_0; // @[Top.scala 34:10]
  assign n1_I_1 = I0_1; // @[Top.scala 34:10]
  assign n1_I_2 = I0_2; // @[Top.scala 34:10]
  assign n1_I_3 = I0_3; // @[Top.scala 34:10]
  assign n3_clock = clock;
  assign n3_reset = reset;
  assign n3_valid_up = valid_up; // @[Top.scala 38:17]
  assign n3_I_0 = I1_0; // @[Top.scala 37:10]
  assign n3_I_1 = I1_1; // @[Top.scala 37:10]
  assign n3_I_2 = I1_2; // @[Top.scala 37:10]
  assign n3_I_3 = I1_3; // @[Top.scala 37:10]
  assign n4_clock = clock;
  assign n4_reset = reset;
  assign n4_valid_up = n3_valid_down; // @[Top.scala 41:17]
  assign n4_I_0 = n3_O_0; // @[Top.scala 40:10]
  assign n4_I_1 = n3_O_1; // @[Top.scala 40:10]
  assign n4_I_2 = n3_O_2; // @[Top.scala 40:10]
  assign n4_I_3 = n3_O_3; // @[Top.scala 40:10]
  assign n5_valid_up = n4_valid_down; // @[Top.scala 44:17]
  assign n5_I_0 = n4_O_0; // @[Top.scala 43:10]
  assign n5_I_1 = n4_O_1; // @[Top.scala 43:10]
  assign n5_I_2 = n4_O_2; // @[Top.scala 43:10]
  assign n5_I_3 = n4_O_3; // @[Top.scala 43:10]
  assign n6_clock = clock;
  assign n6_reset = reset;
  assign n6_valid_up = n1_valid_down & n5_valid_down; // @[Top.scala 48:17]
  assign n6_I0_0 = n1_O_0; // @[Top.scala 46:11]
  assign n6_I0_1 = n1_O_1; // @[Top.scala 46:11]
  assign n6_I0_2 = n1_O_2; // @[Top.scala 46:11]
  assign n6_I0_3 = n1_O_3; // @[Top.scala 46:11]
  assign n6_I1_0 = n5_O_0; // @[Top.scala 47:11]
  assign n6_I1_1 = n5_O_1; // @[Top.scala 47:11]
  assign n6_I1_2 = n5_O_2; // @[Top.scala 47:11]
  assign n6_I1_3 = n5_O_3; // @[Top.scala 47:11]
  assign n18_clock = clock;
  assign n18_reset = reset;
  assign n18_valid_up = n6_valid_down; // @[Top.scala 51:18]
  assign n18_I_0 = n6_O_0; // @[Top.scala 50:11]
  assign n18_I_1 = n6_O_1; // @[Top.scala 50:11]
  assign n18_I_2 = n6_O_2; // @[Top.scala 50:11]
  assign n18_I_3 = n6_O_3; // @[Top.scala 50:11]
  assign n19_valid_up = n18_valid_down; // @[Top.scala 54:18]
  assign n19_I_0 = n18_O_0; // @[Top.scala 53:11]
  assign n20_clock = clock;
  assign n20_reset = reset;
  assign n20_valid_up = n19_valid_down; // @[Top.scala 57:18]
  assign n20_I_0 = n19_O_0; // @[Top.scala 56:11]
  assign n21_clock = clock;
  assign n21_reset = reset;
  assign n21_valid_up = n20_valid_down; // @[Top.scala 60:18]
  assign n21_I_0 = n20_O_0; // @[Top.scala 59:11]
  assign n22_clock = clock;
  assign n22_reset = reset;
  assign n22_valid_up = n21_valid_down; // @[Top.scala 63:18]
  assign n22_I_0 = n21_O_0; // @[Top.scala 62:11]
endmodule
