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
module Passthrough(
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
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_0 = I_0; // @[Passthrough.scala 17:68]
  assign O_1 = I_1; // @[Passthrough.scala 17:68]
  assign O_2 = I_2; // @[Passthrough.scala 17:68]
  assign O_3 = I_3; // @[Passthrough.scala 17:68]
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
module NestedCounters_2(
  input   CE,
  output  valid
);
  wire  NestedCounters_CE; // @[NestedCounters.scala 43:31]
  wire  NestedCounters_valid; // @[NestedCounters.scala 43:31]
  NestedCounters_1 NestedCounters ( // @[NestedCounters.scala 43:31]
    .CE(NestedCounters_CE),
    .valid(NestedCounters_valid)
  );
  assign valid = NestedCounters_valid; // @[NestedCounters.scala 48:11]
  assign NestedCounters_CE = CE; // @[NestedCounters.scala 49:22]
endmodule
module NestedCountersWithNumValid(
  input        clock,
  input        reset,
  input        CE,
  output       valid,
  output [5:0] cur_valid
);
  wire  NestedCounters_CE; // @[NestedCounters.scala 20:44]
  wire  NestedCounters_valid; // @[NestedCounters.scala 20:44]
  wire  _T = CE & NestedCounters_valid; // @[NestedCounters.scala 25:49]
  reg [5:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire [5:0] _T_4 = value + 6'h1; // @[Counter.scala 39:22]
  NestedCounters_2 NestedCounters ( // @[NestedCounters.scala 20:44]
    .CE(NestedCounters_CE),
    .valid(NestedCounters_valid)
  );
  assign valid = NestedCounters_valid; // @[NestedCounters.scala 22:9]
  assign cur_valid = value; // @[NestedCounters.scala 27:13]
  assign NestedCounters_CE = CE; // @[NestedCounters.scala 21:27]
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
    end else if (_T) begin
      value <= _T_4;
    end
  end
endmodule
module RAM_ST(
  input         clock,
  input         reset,
  input         RE,
  output [15:0] RDATA_0,
  output [15:0] RDATA_1,
  output [15:0] RDATA_2,
  output [15:0] RDATA_3,
  input         WE,
  input  [15:0] WDATA_0,
  input  [15:0] WDATA_1,
  input  [15:0] WDATA_2,
  input  [15:0] WDATA_3
);
  wire  write_elem_counter_clock; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_reset; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_CE; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_valid; // @[RAM_ST.scala 20:34]
  wire [5:0] write_elem_counter_cur_valid; // @[RAM_ST.scala 20:34]
  wire  read_elem_counter_clock; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_reset; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_CE; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_valid; // @[RAM_ST.scala 21:33]
  wire [5:0] read_elem_counter_cur_valid; // @[RAM_ST.scala 21:33]
  reg [63:0] ram [0:63]; // @[RAM_ST.scala 29:24]
  reg [63:0] _RAND_0;
  wire [63:0] ram__T_11_data; // @[RAM_ST.scala 29:24]
  wire [5:0] ram__T_11_addr; // @[RAM_ST.scala 29:24]
  wire [63:0] ram__T_5_data; // @[RAM_ST.scala 29:24]
  wire [5:0] ram__T_5_addr; // @[RAM_ST.scala 29:24]
  wire  ram__T_5_mask; // @[RAM_ST.scala 29:24]
  wire  ram__T_5_en; // @[RAM_ST.scala 29:24]
  reg  ram__T_11_en_pipe_0;
  reg [31:0] _RAND_1;
  reg [5:0] ram__T_11_addr_pipe_0;
  reg [31:0] _RAND_2;
  wire [6:0] _T = {{1'd0}, write_elem_counter_cur_valid}; // @[RAM_ST.scala 31:71]
  wire [31:0] _T_2 = {WDATA_1,WDATA_0}; // @[RAM_ST.scala 31:115]
  wire [31:0] _T_3 = {WDATA_3,WDATA_2}; // @[RAM_ST.scala 31:115]
  wire [6:0] _T_6 = {{1'd0}, read_elem_counter_cur_valid}; // @[RAM_ST.scala 32:46]
  wire [63:0] _T_13 = ram__T_11_data;
  NestedCountersWithNumValid write_elem_counter ( // @[RAM_ST.scala 20:34]
    .clock(write_elem_counter_clock),
    .reset(write_elem_counter_reset),
    .CE(write_elem_counter_CE),
    .valid(write_elem_counter_valid),
    .cur_valid(write_elem_counter_cur_valid)
  );
  NestedCountersWithNumValid read_elem_counter ( // @[RAM_ST.scala 21:33]
    .clock(read_elem_counter_clock),
    .reset(read_elem_counter_reset),
    .CE(read_elem_counter_CE),
    .valid(read_elem_counter_valid),
    .cur_valid(read_elem_counter_cur_valid)
  );
  assign ram__T_11_addr = ram__T_11_addr_pipe_0;
  assign ram__T_11_data = ram[ram__T_11_addr]; // @[RAM_ST.scala 29:24]
  assign ram__T_5_data = {_T_3,_T_2};
  assign ram__T_5_addr = _T[5:0];
  assign ram__T_5_mask = 1'h1;
  assign ram__T_5_en = write_elem_counter_valid;
  assign RDATA_0 = _T_13[15:0]; // @[RAM_ST.scala 32:9]
  assign RDATA_1 = _T_13[31:16]; // @[RAM_ST.scala 32:9]
  assign RDATA_2 = _T_13[47:32]; // @[RAM_ST.scala 32:9]
  assign RDATA_3 = _T_13[63:48]; // @[RAM_ST.scala 32:9]
  assign write_elem_counter_clock = clock;
  assign write_elem_counter_reset = reset;
  assign write_elem_counter_CE = WE; // @[RAM_ST.scala 23:25]
  assign read_elem_counter_clock = clock;
  assign read_elem_counter_reset = reset;
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
  _RAND_0 = {2{`RANDOM}};
  `ifdef RANDOMIZE_MEM_INIT
  for (initvar = 0; initvar < 64; initvar = initvar+1)
    ram[initvar] = _RAND_0[63:0];
  `endif // RANDOMIZE_MEM_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  ram__T_11_en_pipe_0 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  ram__T_11_addr_pipe_0 = _RAND_2[5:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if(ram__T_5_en & ram__T_5_mask) begin
      ram[ram__T_5_addr] <= ram__T_5_data; // @[RAM_ST.scala 29:24]
    end
    ram__T_11_en_pipe_0 <= read_elem_counter_valid;
    if (read_elem_counter_valid) begin
      ram__T_11_addr_pipe_0 <= _T_6[5:0];
    end
  end
endmodule
module UpT(
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
  wire  mem_clock; // @[Upsample.scala 31:19]
  wire  mem_reset; // @[Upsample.scala 31:19]
  wire  mem_RE; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_0; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_1; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_2; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_3; // @[Upsample.scala 31:19]
  wire  mem_WE; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_0; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_1; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_2; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_3; // @[Upsample.scala 31:19]
  reg [5:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_1 = value == 6'h3f; // @[Counter.scala 38:24]
  wire [5:0] _T_3 = value + 6'h1; // @[Counter.scala 39:22]
  wire  _GEN_1 = valid_up & _T_1; // @[Counter.scala 67:17]
  reg [7:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire [7:0] _T_9 = value_1 + 8'h1; // @[Counter.scala 39:22]
  wire  _T_10 = value_1 == 8'h0; // @[Upsample.scala 39:36]
  wire  _T_11 = value_1 != 8'h0; // @[Upsample.scala 40:36]
  wire  _T_13 = _T_11 | _T_1; // @[Upsample.scala 40:44]
  wire [15:0] dataOut_0 = mem_RDATA_0; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_1 = mem_RDATA_1; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_2 = mem_RDATA_2; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_3 = mem_RDATA_3; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  RAM_ST mem ( // @[Upsample.scala 31:19]
    .clock(mem_clock),
    .reset(mem_reset),
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
  assign O_0 = _T_10 ? I_0 : dataOut_0; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_1 = _T_10 ? I_1 : dataOut_1; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_2 = _T_10 ? I_2 : dataOut_2; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_3 = _T_10 ? I_3 : dataOut_3; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign mem_clock = clock;
  assign mem_reset = reset;
  assign mem_RE = valid_up & _T_13; // @[Upsample.scala 40:110 Upsample.scala 41:27 Upsample.scala 42:44]
  assign mem_WE = valid_up & _T_10; // @[Upsample.scala 39:54 Upsample.scala 39:86 Upsample.scala 42:25]
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
  value = _RAND_0[5:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  value_1 = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 6'h0;
    end else if (valid_up) begin
      value <= _T_3;
    end
    if (reset) begin
      value_1 <= 8'h0;
    end else if (_GEN_1) begin
      value_1 <= _T_9;
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
module Module_0(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I0,
  input  [15:0] I1,
  output [15:0] O
);
  wire  n13_valid_up; // @[Top.scala 18:21]
  wire  n13_valid_down; // @[Top.scala 18:21]
  wire [15:0] n13_I0; // @[Top.scala 18:21]
  wire [15:0] n13_I1; // @[Top.scala 18:21]
  wire [15:0] n13_O_t0b; // @[Top.scala 18:21]
  wire [15:0] n13_O_t1b; // @[Top.scala 18:21]
  wire  n14_clock; // @[Top.scala 22:21]
  wire  n14_reset; // @[Top.scala 22:21]
  wire  n14_valid_up; // @[Top.scala 22:21]
  wire  n14_valid_down; // @[Top.scala 22:21]
  wire [15:0] n14_I_t0b; // @[Top.scala 22:21]
  wire [15:0] n14_I_t1b; // @[Top.scala 22:21]
  wire [15:0] n14_O; // @[Top.scala 22:21]
  AtomTuple n13 ( // @[Top.scala 18:21]
    .valid_up(n13_valid_up),
    .valid_down(n13_valid_down),
    .I0(n13_I0),
    .I1(n13_I1),
    .O_t0b(n13_O_t0b),
    .O_t1b(n13_O_t1b)
  );
  Mul n14 ( // @[Top.scala 22:21]
    .clock(n14_clock),
    .reset(n14_reset),
    .valid_up(n14_valid_up),
    .valid_down(n14_valid_down),
    .I_t0b(n14_I_t0b),
    .I_t1b(n14_I_t1b),
    .O(n14_O)
  );
  assign valid_down = n14_valid_down; // @[Top.scala 26:16]
  assign O = n14_O; // @[Top.scala 25:7]
  assign n13_valid_up = valid_up; // @[Top.scala 21:18]
  assign n13_I0 = I0; // @[Top.scala 19:12]
  assign n13_I1 = I1; // @[Top.scala 20:12]
  assign n14_clock = clock;
  assign n14_reset = reset;
  assign n14_valid_up = n13_valid_down; // @[Top.scala 24:18]
  assign n14_I_t0b = n13_O_t0b; // @[Top.scala 23:11]
  assign n14_I_t1b = n13_O_t1b; // @[Top.scala 23:11]
endmodule
module Map2S(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I0_0,
  input  [15:0] I0_1,
  input  [15:0] I0_2,
  input  [15:0] I0_3,
  input  [15:0] I1_0,
  input  [15:0] I1_1,
  input  [15:0] I1_2,
  input  [15:0] I1_3,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3
);
  wire  fst_op_clock; // @[Map2S.scala 9:22]
  wire  fst_op_reset; // @[Map2S.scala 9:22]
  wire  fst_op_valid_up; // @[Map2S.scala 9:22]
  wire  fst_op_valid_down; // @[Map2S.scala 9:22]
  wire [15:0] fst_op_I0; // @[Map2S.scala 9:22]
  wire [15:0] fst_op_I1; // @[Map2S.scala 9:22]
  wire [15:0] fst_op_O; // @[Map2S.scala 9:22]
  wire  other_ops_0_clock; // @[Map2S.scala 10:86]
  wire  other_ops_0_reset; // @[Map2S.scala 10:86]
  wire  other_ops_0_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_0_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_0_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_0_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_0_O; // @[Map2S.scala 10:86]
  wire  other_ops_1_clock; // @[Map2S.scala 10:86]
  wire  other_ops_1_reset; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_1_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_1_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_1_O; // @[Map2S.scala 10:86]
  wire  other_ops_2_clock; // @[Map2S.scala 10:86]
  wire  other_ops_2_reset; // @[Map2S.scala 10:86]
  wire  other_ops_2_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_2_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_2_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_2_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_2_O; // @[Map2S.scala 10:86]
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
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I0_0,
  input  [15:0] I0_1,
  input  [15:0] I0_2,
  input  [15:0] I0_3,
  input  [15:0] I1_0,
  input  [15:0] I1_1,
  input  [15:0] I1_2,
  input  [15:0] I1_3,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3
);
  wire  op_clock; // @[Map2T.scala 8:20]
  wire  op_reset; // @[Map2T.scala 8:20]
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_1; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_2; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_3; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_0; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_1; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_2; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_3; // @[Map2T.scala 8:20]
  wire [15:0] op_O_0; // @[Map2T.scala 8:20]
  wire [15:0] op_O_1; // @[Map2T.scala 8:20]
  wire [15:0] op_O_2; // @[Map2T.scala 8:20]
  wire [15:0] op_O_3; // @[Map2T.scala 8:20]
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
  input  [15:0] I_t0b,
  input  [15:0] I_t1b,
  output [15:0] O
);
  assign O = I_t0b + I_t1b; // @[Arithmetic.scala 125:7]
endmodule
module ReduceS(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  input  [15:0] I_1,
  input  [15:0] I_2,
  input  [15:0] I_3,
  output [15:0] O_0
);
  wire [15:0] AddNoValid_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_O; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_1_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_1_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_1_O; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_2_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_2_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_2_O; // @[ReduceS.scala 20:43]
  reg [15:0] _T; // @[ReduceS.scala 27:24]
  reg [31:0] _RAND_0;
  reg [15:0] _T_1; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_1;
  reg [15:0] _T_2; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_2;
  reg [15:0] _T_3; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_3;
  reg [15:0] _T_4; // @[ReduceS.scala 43:46]
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
  assign AddNoValid_I_t0b = _T_4; // @[ReduceS.scala 43:18]
  assign AddNoValid_I_t1b = AddNoValid_1_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_1_I_t0b = AddNoValid_2_O; // @[ReduceS.scala 31:18]
  assign AddNoValid_1_I_t1b = _T_1; // @[ReduceS.scala 43:18]
  assign AddNoValid_2_I_t0b = _T_2; // @[ReduceS.scala 43:18]
  assign AddNoValid_2_I_t1b = _T_3; // @[ReduceS.scala 43:18]
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
  _T_1 = _RAND_1[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_2 = _RAND_2[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_3 = _RAND_3[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_4 = _RAND_4[15:0];
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
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  input  [15:0] I_1,
  input  [15:0] I_2,
  input  [15:0] I_3,
  output [15:0] O_0
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0; // @[MapT.scala 8:20]
  wire [15:0] op_I_1; // @[MapT.scala 8:20]
  wire [15:0] op_I_2; // @[MapT.scala 8:20]
  wire [15:0] op_I_3; // @[MapT.scala 8:20]
  wire [15:0] op_O_0; // @[MapT.scala 8:20]
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
module NestedCountersWithNumValid_2(
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
  input  [15:0] I_0_t0b,
  input  [15:0] I_0_t1b,
  output [15:0] O_0
);
  wire [15:0] fst_op_I_t0b; // @[MapS.scala 28:22]
  wire [15:0] fst_op_I_t1b; // @[MapS.scala 28:22]
  wire [15:0] fst_op_O; // @[MapS.scala 28:22]
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
  input  [15:0] I_0,
  output [15:0] O_0
);
  wire  NestedCountersWithNumValid_CE; // @[ReduceT.scala 22:34]
  wire  NestedCountersWithNumValid_valid; // @[ReduceT.scala 22:34]
  wire [15:0] MapSNoValid_I_0_t0b; // @[ReduceT.scala 25:25]
  wire [15:0] MapSNoValid_I_0_t1b; // @[ReduceT.scala 25:25]
  wire [15:0] MapSNoValid_O_0; // @[ReduceT.scala 25:25]
  reg  _T; // @[ReduceT.scala 26:50]
  reg [31:0] _RAND_0;
  reg [5:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire  _T_3 = value == 6'h3f; // @[Counter.scala 38:24]
  wire [5:0] _T_5 = value + 6'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value == 6'h0; // @[ReduceT.scala 34:60]
  reg [15:0] _T_7_0; // @[ReduceT.scala 34:76]
  reg [31:0] _RAND_2;
  reg [15:0] _T_9_0; // @[ReduceT.scala 35:24]
  reg [31:0] _RAND_3;
  reg  _T_10; // @[ReduceT.scala 37:35]
  reg [31:0] _RAND_4;
  reg [15:0] _T_11_0; // @[ReduceT.scala 44:83]
  reg [31:0] _RAND_5;
  reg  _T_12; // @[ReduceT.scala 51:28]
  reg [31:0] _RAND_6;
  wire  _T_14 = _T_12 | _T_3; // @[ReduceT.scala 52:28]
  reg [15:0] _T_15_0; // @[ReduceT.scala 56:15]
  reg [31:0] _RAND_7;
  NestedCountersWithNumValid_2 NestedCountersWithNumValid ( // @[ReduceT.scala 22:34]
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
  value = _RAND_1[5:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_7_0 = _RAND_2[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_9_0 = _RAND_3[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_10 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_11_0 = _RAND_5[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_12 = _RAND_6[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_15_0 = _RAND_7[15:0];
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
      value <= 6'h0;
    end else if (_T) begin
      value <= _T_5;
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
module Passthrough_3(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  output [15:0] O
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O = I_0; // @[Passthrough.scala 17:68]
endmodule
module Module_1(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  input  [15:0] I_1,
  input  [15:0] I_2,
  input  [15:0] I_3,
  output [15:0] O
);
  wire  n21_clock; // @[Top.scala 32:21]
  wire  n21_reset; // @[Top.scala 32:21]
  wire  n21_valid_up; // @[Top.scala 32:21]
  wire  n21_valid_down; // @[Top.scala 32:21]
  wire [15:0] n21_I_0; // @[Top.scala 32:21]
  wire [15:0] n21_I_1; // @[Top.scala 32:21]
  wire [15:0] n21_I_2; // @[Top.scala 32:21]
  wire [15:0] n21_I_3; // @[Top.scala 32:21]
  wire [15:0] n21_O_0; // @[Top.scala 32:21]
  wire  n24_clock; // @[Top.scala 35:21]
  wire  n24_reset; // @[Top.scala 35:21]
  wire  n24_valid_up; // @[Top.scala 35:21]
  wire  n24_valid_down; // @[Top.scala 35:21]
  wire [15:0] n24_I_0; // @[Top.scala 35:21]
  wire [15:0] n24_O_0; // @[Top.scala 35:21]
  wire  n25_valid_up; // @[Top.scala 38:21]
  wire  n25_valid_down; // @[Top.scala 38:21]
  wire [15:0] n25_I_0; // @[Top.scala 38:21]
  wire [15:0] n25_O; // @[Top.scala 38:21]
  MapT n21 ( // @[Top.scala 32:21]
    .clock(n21_clock),
    .reset(n21_reset),
    .valid_up(n21_valid_up),
    .valid_down(n21_valid_down),
    .I_0(n21_I_0),
    .I_1(n21_I_1),
    .I_2(n21_I_2),
    .I_3(n21_I_3),
    .O_0(n21_O_0)
  );
  ReduceT n24 ( // @[Top.scala 35:21]
    .clock(n24_clock),
    .reset(n24_reset),
    .valid_up(n24_valid_up),
    .valid_down(n24_valid_down),
    .I_0(n24_I_0),
    .O_0(n24_O_0)
  );
  Passthrough_3 n25 ( // @[Top.scala 38:21]
    .valid_up(n25_valid_up),
    .valid_down(n25_valid_down),
    .I_0(n25_I_0),
    .O(n25_O)
  );
  assign valid_down = n25_valid_down; // @[Top.scala 42:16]
  assign O = n25_O; // @[Top.scala 41:7]
  assign n21_clock = clock;
  assign n21_reset = reset;
  assign n21_valid_up = valid_up; // @[Top.scala 34:18]
  assign n21_I_0 = I_0; // @[Top.scala 33:11]
  assign n21_I_1 = I_1; // @[Top.scala 33:11]
  assign n21_I_2 = I_2; // @[Top.scala 33:11]
  assign n21_I_3 = I_3; // @[Top.scala 33:11]
  assign n24_clock = clock;
  assign n24_reset = reset;
  assign n24_valid_up = n21_valid_down; // @[Top.scala 37:18]
  assign n24_I_0 = n21_O_0; // @[Top.scala 36:11]
  assign n25_valid_up = n24_valid_down; // @[Top.scala 40:18]
  assign n25_I_0 = n24_O_0; // @[Top.scala 39:11]
endmodule
module MapT_1(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  input  [15:0] I_1,
  input  [15:0] I_2,
  input  [15:0] I_3,
  output [15:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0; // @[MapT.scala 8:20]
  wire [15:0] op_I_1; // @[MapT.scala 8:20]
  wire [15:0] op_I_2; // @[MapT.scala 8:20]
  wire [15:0] op_I_3; // @[MapT.scala 8:20]
  wire [15:0] op_O; // @[MapT.scala 8:20]
  Module_1 op ( // @[MapT.scala 8:20]
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
module FIFO_2(
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
module Top(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I0_0,
  input  [15:0] I0_1,
  input  [15:0] I0_2,
  input  [15:0] I0_3,
  input  [15:0] I1_0,
  input  [15:0] I1_1,
  input  [15:0] I1_2,
  input  [15:0] I1_3,
  output [15:0] O
);
  wire  n1_clock; // @[Top.scala 49:20]
  wire  n1_reset; // @[Top.scala 49:20]
  wire  n1_valid_up; // @[Top.scala 49:20]
  wire  n1_valid_down; // @[Top.scala 49:20]
  wire [15:0] n1_I_0; // @[Top.scala 49:20]
  wire [15:0] n1_I_1; // @[Top.scala 49:20]
  wire [15:0] n1_I_2; // @[Top.scala 49:20]
  wire [15:0] n1_I_3; // @[Top.scala 49:20]
  wire [15:0] n1_O_0; // @[Top.scala 49:20]
  wire [15:0] n1_O_1; // @[Top.scala 49:20]
  wire [15:0] n1_O_2; // @[Top.scala 49:20]
  wire [15:0] n1_O_3; // @[Top.scala 49:20]
  wire  n3_clock; // @[Top.scala 52:20]
  wire  n3_reset; // @[Top.scala 52:20]
  wire  n3_valid_up; // @[Top.scala 52:20]
  wire  n3_valid_down; // @[Top.scala 52:20]
  wire [15:0] n3_I_0; // @[Top.scala 52:20]
  wire [15:0] n3_I_1; // @[Top.scala 52:20]
  wire [15:0] n3_I_2; // @[Top.scala 52:20]
  wire [15:0] n3_I_3; // @[Top.scala 52:20]
  wire [15:0] n3_O_0; // @[Top.scala 52:20]
  wire [15:0] n3_O_1; // @[Top.scala 52:20]
  wire [15:0] n3_O_2; // @[Top.scala 52:20]
  wire [15:0] n3_O_3; // @[Top.scala 52:20]
  wire  n4_valid_up; // @[Top.scala 55:20]
  wire  n4_valid_down; // @[Top.scala 55:20]
  wire [15:0] n4_I_0; // @[Top.scala 55:20]
  wire [15:0] n4_I_1; // @[Top.scala 55:20]
  wire [15:0] n4_I_2; // @[Top.scala 55:20]
  wire [15:0] n4_I_3; // @[Top.scala 55:20]
  wire [15:0] n4_O_0; // @[Top.scala 55:20]
  wire [15:0] n4_O_1; // @[Top.scala 55:20]
  wire [15:0] n4_O_2; // @[Top.scala 55:20]
  wire [15:0] n4_O_3; // @[Top.scala 55:20]
  wire  n5_clock; // @[Top.scala 58:20]
  wire  n5_reset; // @[Top.scala 58:20]
  wire  n5_valid_up; // @[Top.scala 58:20]
  wire  n5_valid_down; // @[Top.scala 58:20]
  wire [15:0] n5_I_0; // @[Top.scala 58:20]
  wire [15:0] n5_I_1; // @[Top.scala 58:20]
  wire [15:0] n5_I_2; // @[Top.scala 58:20]
  wire [15:0] n5_I_3; // @[Top.scala 58:20]
  wire [15:0] n5_O_0; // @[Top.scala 58:20]
  wire [15:0] n5_O_1; // @[Top.scala 58:20]
  wire [15:0] n5_O_2; // @[Top.scala 58:20]
  wire [15:0] n5_O_3; // @[Top.scala 58:20]
  wire  n6_valid_up; // @[Top.scala 61:20]
  wire  n6_valid_down; // @[Top.scala 61:20]
  wire [15:0] n6_I_0; // @[Top.scala 61:20]
  wire [15:0] n6_I_1; // @[Top.scala 61:20]
  wire [15:0] n6_I_2; // @[Top.scala 61:20]
  wire [15:0] n6_I_3; // @[Top.scala 61:20]
  wire [15:0] n6_O_0; // @[Top.scala 61:20]
  wire [15:0] n6_O_1; // @[Top.scala 61:20]
  wire [15:0] n6_O_2; // @[Top.scala 61:20]
  wire [15:0] n6_O_3; // @[Top.scala 61:20]
  wire  n7_clock; // @[Top.scala 64:20]
  wire  n7_reset; // @[Top.scala 64:20]
  wire  n7_valid_up; // @[Top.scala 64:20]
  wire  n7_valid_down; // @[Top.scala 64:20]
  wire [15:0] n7_I0_0; // @[Top.scala 64:20]
  wire [15:0] n7_I0_1; // @[Top.scala 64:20]
  wire [15:0] n7_I0_2; // @[Top.scala 64:20]
  wire [15:0] n7_I0_3; // @[Top.scala 64:20]
  wire [15:0] n7_I1_0; // @[Top.scala 64:20]
  wire [15:0] n7_I1_1; // @[Top.scala 64:20]
  wire [15:0] n7_I1_2; // @[Top.scala 64:20]
  wire [15:0] n7_I1_3; // @[Top.scala 64:20]
  wire [15:0] n7_O_0; // @[Top.scala 64:20]
  wire [15:0] n7_O_1; // @[Top.scala 64:20]
  wire [15:0] n7_O_2; // @[Top.scala 64:20]
  wire [15:0] n7_O_3; // @[Top.scala 64:20]
  wire  n15_valid_up; // @[Top.scala 68:21]
  wire  n15_valid_down; // @[Top.scala 68:21]
  wire [15:0] n15_I_0; // @[Top.scala 68:21]
  wire [15:0] n15_I_1; // @[Top.scala 68:21]
  wire [15:0] n15_I_2; // @[Top.scala 68:21]
  wire [15:0] n15_I_3; // @[Top.scala 68:21]
  wire [15:0] n15_O_0; // @[Top.scala 68:21]
  wire [15:0] n15_O_1; // @[Top.scala 68:21]
  wire [15:0] n15_O_2; // @[Top.scala 68:21]
  wire [15:0] n15_O_3; // @[Top.scala 68:21]
  wire  n26_clock; // @[Top.scala 71:21]
  wire  n26_reset; // @[Top.scala 71:21]
  wire  n26_valid_up; // @[Top.scala 71:21]
  wire  n26_valid_down; // @[Top.scala 71:21]
  wire [15:0] n26_I_0; // @[Top.scala 71:21]
  wire [15:0] n26_I_1; // @[Top.scala 71:21]
  wire [15:0] n26_I_2; // @[Top.scala 71:21]
  wire [15:0] n26_I_3; // @[Top.scala 71:21]
  wire [15:0] n26_O; // @[Top.scala 71:21]
  wire  n27_clock; // @[Top.scala 74:21]
  wire  n27_reset; // @[Top.scala 74:21]
  wire  n27_valid_up; // @[Top.scala 74:21]
  wire  n27_valid_down; // @[Top.scala 74:21]
  wire [15:0] n27_I; // @[Top.scala 74:21]
  wire [15:0] n27_O; // @[Top.scala 74:21]
  wire  n28_clock; // @[Top.scala 77:21]
  wire  n28_reset; // @[Top.scala 77:21]
  wire  n28_valid_up; // @[Top.scala 77:21]
  wire  n28_valid_down; // @[Top.scala 77:21]
  wire [15:0] n28_I; // @[Top.scala 77:21]
  wire [15:0] n28_O; // @[Top.scala 77:21]
  wire  n29_clock; // @[Top.scala 80:21]
  wire  n29_reset; // @[Top.scala 80:21]
  wire  n29_valid_up; // @[Top.scala 80:21]
  wire  n29_valid_down; // @[Top.scala 80:21]
  wire [15:0] n29_I; // @[Top.scala 80:21]
  wire [15:0] n29_O; // @[Top.scala 80:21]
  FIFO n1 ( // @[Top.scala 49:20]
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
  FIFO n3 ( // @[Top.scala 52:20]
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
  Passthrough n4 ( // @[Top.scala 55:20]
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
  UpT n5 ( // @[Top.scala 58:20]
    .clock(n5_clock),
    .reset(n5_reset),
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
  Passthrough n6 ( // @[Top.scala 61:20]
    .valid_up(n6_valid_up),
    .valid_down(n6_valid_down),
    .I_0(n6_I_0),
    .I_1(n6_I_1),
    .I_2(n6_I_2),
    .I_3(n6_I_3),
    .O_0(n6_O_0),
    .O_1(n6_O_1),
    .O_2(n6_O_2),
    .O_3(n6_O_3)
  );
  Map2T n7 ( // @[Top.scala 64:20]
    .clock(n7_clock),
    .reset(n7_reset),
    .valid_up(n7_valid_up),
    .valid_down(n7_valid_down),
    .I0_0(n7_I0_0),
    .I0_1(n7_I0_1),
    .I0_2(n7_I0_2),
    .I0_3(n7_I0_3),
    .I1_0(n7_I1_0),
    .I1_1(n7_I1_1),
    .I1_2(n7_I1_2),
    .I1_3(n7_I1_3),
    .O_0(n7_O_0),
    .O_1(n7_O_1),
    .O_2(n7_O_2),
    .O_3(n7_O_3)
  );
  Passthrough n15 ( // @[Top.scala 68:21]
    .valid_up(n15_valid_up),
    .valid_down(n15_valid_down),
    .I_0(n15_I_0),
    .I_1(n15_I_1),
    .I_2(n15_I_2),
    .I_3(n15_I_3),
    .O_0(n15_O_0),
    .O_1(n15_O_1),
    .O_2(n15_O_2),
    .O_3(n15_O_3)
  );
  MapT_1 n26 ( // @[Top.scala 71:21]
    .clock(n26_clock),
    .reset(n26_reset),
    .valid_up(n26_valid_up),
    .valid_down(n26_valid_down),
    .I_0(n26_I_0),
    .I_1(n26_I_1),
    .I_2(n26_I_2),
    .I_3(n26_I_3),
    .O(n26_O)
  );
  FIFO_2 n27 ( // @[Top.scala 74:21]
    .clock(n27_clock),
    .reset(n27_reset),
    .valid_up(n27_valid_up),
    .valid_down(n27_valid_down),
    .I(n27_I),
    .O(n27_O)
  );
  FIFO_2 n28 ( // @[Top.scala 77:21]
    .clock(n28_clock),
    .reset(n28_reset),
    .valid_up(n28_valid_up),
    .valid_down(n28_valid_down),
    .I(n28_I),
    .O(n28_O)
  );
  FIFO_2 n29 ( // @[Top.scala 80:21]
    .clock(n29_clock),
    .reset(n29_reset),
    .valid_up(n29_valid_up),
    .valid_down(n29_valid_down),
    .I(n29_I),
    .O(n29_O)
  );
  assign valid_down = n29_valid_down; // @[Top.scala 84:16]
  assign O = n29_O; // @[Top.scala 83:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 51:17]
  assign n1_I_0 = I0_0; // @[Top.scala 50:10]
  assign n1_I_1 = I0_1; // @[Top.scala 50:10]
  assign n1_I_2 = I0_2; // @[Top.scala 50:10]
  assign n1_I_3 = I0_3; // @[Top.scala 50:10]
  assign n3_clock = clock;
  assign n3_reset = reset;
  assign n3_valid_up = valid_up; // @[Top.scala 54:17]
  assign n3_I_0 = I1_0; // @[Top.scala 53:10]
  assign n3_I_1 = I1_1; // @[Top.scala 53:10]
  assign n3_I_2 = I1_2; // @[Top.scala 53:10]
  assign n3_I_3 = I1_3; // @[Top.scala 53:10]
  assign n4_valid_up = n3_valid_down; // @[Top.scala 57:17]
  assign n4_I_0 = n3_O_0; // @[Top.scala 56:10]
  assign n4_I_1 = n3_O_1; // @[Top.scala 56:10]
  assign n4_I_2 = n3_O_2; // @[Top.scala 56:10]
  assign n4_I_3 = n3_O_3; // @[Top.scala 56:10]
  assign n5_clock = clock;
  assign n5_reset = reset;
  assign n5_valid_up = n4_valid_down; // @[Top.scala 60:17]
  assign n5_I_0 = n4_O_0; // @[Top.scala 59:10]
  assign n5_I_1 = n4_O_1; // @[Top.scala 59:10]
  assign n5_I_2 = n4_O_2; // @[Top.scala 59:10]
  assign n5_I_3 = n4_O_3; // @[Top.scala 59:10]
  assign n6_valid_up = n5_valid_down; // @[Top.scala 63:17]
  assign n6_I_0 = n5_O_0; // @[Top.scala 62:10]
  assign n6_I_1 = n5_O_1; // @[Top.scala 62:10]
  assign n6_I_2 = n5_O_2; // @[Top.scala 62:10]
  assign n6_I_3 = n5_O_3; // @[Top.scala 62:10]
  assign n7_clock = clock;
  assign n7_reset = reset;
  assign n7_valid_up = n1_valid_down & n6_valid_down; // @[Top.scala 67:17]
  assign n7_I0_0 = n1_O_0; // @[Top.scala 65:11]
  assign n7_I0_1 = n1_O_1; // @[Top.scala 65:11]
  assign n7_I0_2 = n1_O_2; // @[Top.scala 65:11]
  assign n7_I0_3 = n1_O_3; // @[Top.scala 65:11]
  assign n7_I1_0 = n6_O_0; // @[Top.scala 66:11]
  assign n7_I1_1 = n6_O_1; // @[Top.scala 66:11]
  assign n7_I1_2 = n6_O_2; // @[Top.scala 66:11]
  assign n7_I1_3 = n6_O_3; // @[Top.scala 66:11]
  assign n15_valid_up = n7_valid_down; // @[Top.scala 70:18]
  assign n15_I_0 = n7_O_0; // @[Top.scala 69:11]
  assign n15_I_1 = n7_O_1; // @[Top.scala 69:11]
  assign n15_I_2 = n7_O_2; // @[Top.scala 69:11]
  assign n15_I_3 = n7_O_3; // @[Top.scala 69:11]
  assign n26_clock = clock;
  assign n26_reset = reset;
  assign n26_valid_up = n15_valid_down; // @[Top.scala 73:18]
  assign n26_I_0 = n15_O_0; // @[Top.scala 72:11]
  assign n26_I_1 = n15_O_1; // @[Top.scala 72:11]
  assign n26_I_2 = n15_O_2; // @[Top.scala 72:11]
  assign n26_I_3 = n15_O_3; // @[Top.scala 72:11]
  assign n27_clock = clock;
  assign n27_reset = reset;
  assign n27_valid_up = n26_valid_down; // @[Top.scala 76:18]
  assign n27_I = n26_O; // @[Top.scala 75:11]
  assign n28_clock = clock;
  assign n28_reset = reset;
  assign n28_valid_up = n27_valid_down; // @[Top.scala 79:18]
  assign n28_I = n27_O; // @[Top.scala 78:11]
  assign n29_clock = clock;
  assign n29_reset = reset;
  assign n29_valid_up = n28_valid_down; // @[Top.scala 82:18]
  assign n29_I = n28_O; // @[Top.scala 81:11]
endmodule
