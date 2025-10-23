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
module NestedCounters_1(
  input   CE,
  output  valid
);
  wire  NestedCounters_CE; // @[NestedCounters.scala 43:31]
  wire  NestedCounters_valid; // @[NestedCounters.scala 43:31]
  NestedCounters NestedCounters ( // @[NestedCounters.scala 43:31]
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
  output [1:0] cur_valid
);
  wire  NestedCounters_CE; // @[NestedCounters.scala 20:44]
  wire  NestedCounters_valid; // @[NestedCounters.scala 20:44]
  wire  _T = CE & NestedCounters_valid; // @[NestedCounters.scala 25:49]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire [1:0] _T_4 = value + 2'h1; // @[Counter.scala 39:22]
  NestedCounters_1 NestedCounters ( // @[NestedCounters.scala 20:44]
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
  value = _RAND_0[1:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 2'h0;
    end else if (_T) begin
      value <= _T_4;
    end
  end
endmodule
module RAM_ST(
  input        clock,
  input        reset,
  input        RE,
  output [7:0] RDATA,
  input        WE,
  input  [7:0] WDATA
);
  wire  write_elem_counter_clock; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_reset; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_CE; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_valid; // @[RAM_ST.scala 20:34]
  wire [1:0] write_elem_counter_cur_valid; // @[RAM_ST.scala 20:34]
  wire  read_elem_counter_clock; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_reset; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_CE; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_valid; // @[RAM_ST.scala 21:33]
  wire [1:0] read_elem_counter_cur_valid; // @[RAM_ST.scala 21:33]
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
  wire [2:0] _T = {{1'd0}, write_elem_counter_cur_valid}; // @[RAM_ST.scala 31:71]
  wire [2:0] _T_3 = {{1'd0}, read_elem_counter_cur_valid}; // @[RAM_ST.scala 32:46]
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
  assign ram__T_8_addr = ram__T_8_addr_pipe_0;
  assign ram__T_8_data = ram[ram__T_8_addr]; // @[RAM_ST.scala 29:24]
  assign ram__T_2_data = WDATA;
  assign ram__T_2_addr = _T[1:0];
  assign ram__T_2_mask = 1'h1;
  assign ram__T_2_en = write_elem_counter_valid;
  assign RDATA = ram__T_8_data; // @[RAM_ST.scala 32:9]
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
module UpT(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  wire  mem_clock; // @[Upsample.scala 31:19]
  wire  mem_reset; // @[Upsample.scala 31:19]
  wire  mem_RE; // @[Upsample.scala 31:19]
  wire [7:0] mem_RDATA; // @[Upsample.scala 31:19]
  wire  mem_WE; // @[Upsample.scala 31:19]
  wire [7:0] mem_WDATA; // @[Upsample.scala 31:19]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_1 = value == 2'h3; // @[Counter.scala 38:24]
  wire [1:0] _T_3 = value + 2'h1; // @[Counter.scala 39:22]
  wire  _GEN_1 = valid_up & _T_1; // @[Counter.scala 67:17]
  reg [1:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire [1:0] _T_9 = value_1 + 2'h1; // @[Counter.scala 39:22]
  wire  _T_10 = value_1 == 2'h0; // @[Upsample.scala 39:36]
  wire  _T_11 = value_1 != 2'h0; // @[Upsample.scala 40:36]
  wire  _T_13 = _T_11 | _T_1; // @[Upsample.scala 40:44]
  wire [7:0] dataOut = mem_RDATA; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  RAM_ST mem ( // @[Upsample.scala 31:19]
    .clock(mem_clock),
    .reset(mem_reset),
    .RE(mem_RE),
    .RDATA(mem_RDATA),
    .WE(mem_WE),
    .WDATA(mem_WDATA)
  );
  assign valid_down = valid_up; // @[Upsample.scala 46:14]
  assign O = _T_10 ? I : dataOut; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign mem_clock = clock;
  assign mem_reset = reset;
  assign mem_RE = valid_up & _T_13; // @[Upsample.scala 40:110 Upsample.scala 41:27 Upsample.scala 42:44]
  assign mem_WE = valid_up & _T_10; // @[Upsample.scala 39:54 Upsample.scala 39:86 Upsample.scala 42:25]
  assign mem_WDATA = I; // @[Upsample.scala 36:13]
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
    end else if (valid_up) begin
      value <= _T_3;
    end
    if (reset) begin
      value_1 <= 2'h0;
    end else if (_GEN_1) begin
      value_1 <= _T_9;
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
  wire  n11_valid_up; // @[Top.scala 18:21]
  wire  n11_valid_down; // @[Top.scala 18:21]
  wire [7:0] n11_I0; // @[Top.scala 18:21]
  wire [7:0] n11_I1; // @[Top.scala 18:21]
  wire [7:0] n11_O_t0b; // @[Top.scala 18:21]
  wire [7:0] n11_O_t1b; // @[Top.scala 18:21]
  wire  n12_clock; // @[Top.scala 22:21]
  wire  n12_reset; // @[Top.scala 22:21]
  wire  n12_valid_up; // @[Top.scala 22:21]
  wire  n12_valid_down; // @[Top.scala 22:21]
  wire [7:0] n12_I_t0b; // @[Top.scala 22:21]
  wire [7:0] n12_I_t1b; // @[Top.scala 22:21]
  wire [7:0] n12_O; // @[Top.scala 22:21]
  AtomTuple n11 ( // @[Top.scala 18:21]
    .valid_up(n11_valid_up),
    .valid_down(n11_valid_down),
    .I0(n11_I0),
    .I1(n11_I1),
    .O_t0b(n11_O_t0b),
    .O_t1b(n11_O_t1b)
  );
  Mul n12 ( // @[Top.scala 22:21]
    .clock(n12_clock),
    .reset(n12_reset),
    .valid_up(n12_valid_up),
    .valid_down(n12_valid_down),
    .I_t0b(n12_I_t0b),
    .I_t1b(n12_I_t1b),
    .O(n12_O)
  );
  assign valid_down = n12_valid_down; // @[Top.scala 26:16]
  assign O = n12_O; // @[Top.scala 25:7]
  assign n11_valid_up = valid_up; // @[Top.scala 21:18]
  assign n11_I0 = I0; // @[Top.scala 19:12]
  assign n11_I1 = I1; // @[Top.scala 20:12]
  assign n12_clock = clock;
  assign n12_reset = reset;
  assign n12_valid_up = n11_valid_down; // @[Top.scala 24:18]
  assign n12_I_t0b = n11_O_t0b; // @[Top.scala 23:11]
  assign n12_I_t1b = n11_O_t1b; // @[Top.scala 23:11]
endmodule
module Map2T(
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
module Map2T_1(
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
  Map2T op ( // @[Map2T.scala 8:20]
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
module NestedCountersWithNumValid_2(
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
  wire  _T_3 = value == 2'h3; // @[Counter.scala 38:24]
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
  NestedCountersWithNumValid_2 NestedCountersWithNumValid ( // @[ReduceT.scala 22:34]
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
module MapT(
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
module Top(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O
);
  wire  n1_clock; // @[Top.scala 33:20]
  wire  n1_reset; // @[Top.scala 33:20]
  wire  n1_valid_up; // @[Top.scala 33:20]
  wire  n1_valid_down; // @[Top.scala 33:20]
  wire [7:0] n1_I; // @[Top.scala 33:20]
  wire [7:0] n1_O; // @[Top.scala 33:20]
  wire  n3_clock; // @[Top.scala 36:20]
  wire  n3_reset; // @[Top.scala 36:20]
  wire  n3_valid_up; // @[Top.scala 36:20]
  wire  n3_valid_down; // @[Top.scala 36:20]
  wire [7:0] n3_I; // @[Top.scala 36:20]
  wire [7:0] n3_O; // @[Top.scala 36:20]
  wire  n4_clock; // @[Top.scala 39:20]
  wire  n4_reset; // @[Top.scala 39:20]
  wire  n4_valid_up; // @[Top.scala 39:20]
  wire  n4_valid_down; // @[Top.scala 39:20]
  wire [7:0] n4_I; // @[Top.scala 39:20]
  wire [7:0] n4_O; // @[Top.scala 39:20]
  wire  n5_clock; // @[Top.scala 42:20]
  wire  n5_reset; // @[Top.scala 42:20]
  wire  n5_valid_up; // @[Top.scala 42:20]
  wire  n5_valid_down; // @[Top.scala 42:20]
  wire [7:0] n5_I0; // @[Top.scala 42:20]
  wire [7:0] n5_I1; // @[Top.scala 42:20]
  wire [7:0] n5_O; // @[Top.scala 42:20]
  wire  n17_clock; // @[Top.scala 46:21]
  wire  n17_reset; // @[Top.scala 46:21]
  wire  n17_valid_up; // @[Top.scala 46:21]
  wire  n17_valid_down; // @[Top.scala 46:21]
  wire [7:0] n17_I; // @[Top.scala 46:21]
  wire [7:0] n17_O; // @[Top.scala 46:21]
  wire  n18_clock; // @[Top.scala 49:21]
  wire  n18_reset; // @[Top.scala 49:21]
  wire  n18_valid_up; // @[Top.scala 49:21]
  wire  n18_valid_down; // @[Top.scala 49:21]
  wire [7:0] n18_I; // @[Top.scala 49:21]
  wire [7:0] n18_O; // @[Top.scala 49:21]
  wire  n19_clock; // @[Top.scala 52:21]
  wire  n19_reset; // @[Top.scala 52:21]
  wire  n19_valid_up; // @[Top.scala 52:21]
  wire  n19_valid_down; // @[Top.scala 52:21]
  wire [7:0] n19_I; // @[Top.scala 52:21]
  wire [7:0] n19_O; // @[Top.scala 52:21]
  wire  n20_clock; // @[Top.scala 55:21]
  wire  n20_reset; // @[Top.scala 55:21]
  wire  n20_valid_up; // @[Top.scala 55:21]
  wire  n20_valid_down; // @[Top.scala 55:21]
  wire [7:0] n20_I; // @[Top.scala 55:21]
  wire [7:0] n20_O; // @[Top.scala 55:21]
  FIFO n1 ( // @[Top.scala 33:20]
    .clock(n1_clock),
    .reset(n1_reset),
    .valid_up(n1_valid_up),
    .valid_down(n1_valid_down),
    .I(n1_I),
    .O(n1_O)
  );
  FIFO n3 ( // @[Top.scala 36:20]
    .clock(n3_clock),
    .reset(n3_reset),
    .valid_up(n3_valid_up),
    .valid_down(n3_valid_down),
    .I(n3_I),
    .O(n3_O)
  );
  UpT n4 ( // @[Top.scala 39:20]
    .clock(n4_clock),
    .reset(n4_reset),
    .valid_up(n4_valid_up),
    .valid_down(n4_valid_down),
    .I(n4_I),
    .O(n4_O)
  );
  Map2T_1 n5 ( // @[Top.scala 42:20]
    .clock(n5_clock),
    .reset(n5_reset),
    .valid_up(n5_valid_up),
    .valid_down(n5_valid_down),
    .I0(n5_I0),
    .I1(n5_I1),
    .O(n5_O)
  );
  MapT n17 ( // @[Top.scala 46:21]
    .clock(n17_clock),
    .reset(n17_reset),
    .valid_up(n17_valid_up),
    .valid_down(n17_valid_down),
    .I(n17_I),
    .O(n17_O)
  );
  FIFO n18 ( // @[Top.scala 49:21]
    .clock(n18_clock),
    .reset(n18_reset),
    .valid_up(n18_valid_up),
    .valid_down(n18_valid_down),
    .I(n18_I),
    .O(n18_O)
  );
  FIFO n19 ( // @[Top.scala 52:21]
    .clock(n19_clock),
    .reset(n19_reset),
    .valid_up(n19_valid_up),
    .valid_down(n19_valid_down),
    .I(n19_I),
    .O(n19_O)
  );
  FIFO n20 ( // @[Top.scala 55:21]
    .clock(n20_clock),
    .reset(n20_reset),
    .valid_up(n20_valid_up),
    .valid_down(n20_valid_down),
    .I(n20_I),
    .O(n20_O)
  );
  assign valid_down = n20_valid_down; // @[Top.scala 59:16]
  assign O = n20_O; // @[Top.scala 58:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 35:17]
  assign n1_I = I0; // @[Top.scala 34:10]
  assign n3_clock = clock;
  assign n3_reset = reset;
  assign n3_valid_up = valid_up; // @[Top.scala 38:17]
  assign n3_I = I1; // @[Top.scala 37:10]
  assign n4_clock = clock;
  assign n4_reset = reset;
  assign n4_valid_up = n3_valid_down; // @[Top.scala 41:17]
  assign n4_I = n3_O; // @[Top.scala 40:10]
  assign n5_clock = clock;
  assign n5_reset = reset;
  assign n5_valid_up = n1_valid_down & n4_valid_down; // @[Top.scala 45:17]
  assign n5_I0 = n1_O; // @[Top.scala 43:11]
  assign n5_I1 = n4_O; // @[Top.scala 44:11]
  assign n17_clock = clock;
  assign n17_reset = reset;
  assign n17_valid_up = n5_valid_down; // @[Top.scala 48:18]
  assign n17_I = n5_O; // @[Top.scala 47:11]
  assign n18_clock = clock;
  assign n18_reset = reset;
  assign n18_valid_up = n17_valid_down; // @[Top.scala 51:18]
  assign n18_I = n17_O; // @[Top.scala 50:11]
  assign n19_clock = clock;
  assign n19_reset = reset;
  assign n19_valid_up = n18_valid_down; // @[Top.scala 54:18]
  assign n19_I = n18_O; // @[Top.scala 53:11]
  assign n20_clock = clock;
  assign n20_reset = reset;
  assign n20_valid_up = n19_valid_down; // @[Top.scala 57:18]
  assign n20_I = n19_O; // @[Top.scala 56:11]
endmodule
