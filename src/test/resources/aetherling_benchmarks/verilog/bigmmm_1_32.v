module FIFO(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  input  [15:0] I_1,
  input  [15:0] I_2,
  input  [15:0] I_3,
  input  [15:0] I_4,
  input  [15:0] I_5,
  input  [15:0] I_6,
  input  [15:0] I_7,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4,
  output [15:0] O_5,
  output [15:0] O_6,
  output [15:0] O_7
);
  reg [15:0] _T__0; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_0;
  reg [15:0] _T__1; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_1;
  reg [15:0] _T__2; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_2;
  reg [15:0] _T__3; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_3;
  reg [15:0] _T__4; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_4;
  reg [15:0] _T__5; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_5;
  reg [15:0] _T__6; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_6;
  reg [15:0] _T__7; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_7;
  reg  _T_1; // @[FIFO.scala 15:27]
  reg [31:0] _RAND_8;
  assign valid_down = _T_1; // @[FIFO.scala 16:16]
  assign O_0 = _T__0; // @[FIFO.scala 14:7]
  assign O_1 = _T__1; // @[FIFO.scala 14:7]
  assign O_2 = _T__2; // @[FIFO.scala 14:7]
  assign O_3 = _T__3; // @[FIFO.scala 14:7]
  assign O_4 = _T__4; // @[FIFO.scala 14:7]
  assign O_5 = _T__5; // @[FIFO.scala 14:7]
  assign O_6 = _T__6; // @[FIFO.scala 14:7]
  assign O_7 = _T__7; // @[FIFO.scala 14:7]
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
  _T__4 = _RAND_4[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T__5 = _RAND_5[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T__6 = _RAND_6[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T__7 = _RAND_7[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{`RANDOM}};
  _T_1 = _RAND_8[0:0];
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
    _T__7 <= I_7;
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
  input  [15:0] I_4,
  input  [15:0] I_5,
  input  [15:0] I_6,
  input  [15:0] I_7,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4,
  output [15:0] O_5,
  output [15:0] O_6,
  output [15:0] O_7
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_0 = I_0; // @[Passthrough.scala 17:68]
  assign O_1 = I_1; // @[Passthrough.scala 17:68]
  assign O_2 = I_2; // @[Passthrough.scala 17:68]
  assign O_3 = I_3; // @[Passthrough.scala 17:68]
  assign O_4 = I_4; // @[Passthrough.scala 17:68]
  assign O_5 = I_5; // @[Passthrough.scala 17:68]
  assign O_6 = I_6; // @[Passthrough.scala 17:68]
  assign O_7 = I_7; // @[Passthrough.scala 17:68]
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
  output [4:0] cur_valid
);
  wire  NestedCounters_CE; // @[NestedCounters.scala 20:44]
  wire  NestedCounters_valid; // @[NestedCounters.scala 20:44]
  wire  _T = CE & NestedCounters_valid; // @[NestedCounters.scala 25:49]
  reg [4:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire [4:0] _T_4 = value + 5'h1; // @[Counter.scala 39:22]
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
  value = _RAND_0[4:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 5'h0;
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
  output [15:0] RDATA_4,
  output [15:0] RDATA_5,
  output [15:0] RDATA_6,
  output [15:0] RDATA_7,
  input         WE,
  input  [15:0] WDATA_0,
  input  [15:0] WDATA_1,
  input  [15:0] WDATA_2,
  input  [15:0] WDATA_3,
  input  [15:0] WDATA_4,
  input  [15:0] WDATA_5,
  input  [15:0] WDATA_6,
  input  [15:0] WDATA_7
);
  wire  write_elem_counter_clock; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_reset; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_CE; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_valid; // @[RAM_ST.scala 20:34]
  wire [4:0] write_elem_counter_cur_valid; // @[RAM_ST.scala 20:34]
  wire  read_elem_counter_clock; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_reset; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_CE; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_valid; // @[RAM_ST.scala 21:33]
  wire [4:0] read_elem_counter_cur_valid; // @[RAM_ST.scala 21:33]
  reg [127:0] ram [0:31]; // @[RAM_ST.scala 29:24]
  reg [127:0] _RAND_0;
  wire [127:0] ram__T_15_data; // @[RAM_ST.scala 29:24]
  wire [4:0] ram__T_15_addr; // @[RAM_ST.scala 29:24]
  wire [127:0] ram__T_9_data; // @[RAM_ST.scala 29:24]
  wire [4:0] ram__T_9_addr; // @[RAM_ST.scala 29:24]
  wire  ram__T_9_mask; // @[RAM_ST.scala 29:24]
  wire  ram__T_9_en; // @[RAM_ST.scala 29:24]
  reg  ram__T_15_en_pipe_0;
  reg [31:0] _RAND_1;
  reg [4:0] ram__T_15_addr_pipe_0;
  reg [31:0] _RAND_2;
  wire [5:0] _T = {{1'd0}, write_elem_counter_cur_valid}; // @[RAM_ST.scala 31:71]
  wire [63:0] _T_4 = {WDATA_3,WDATA_2,WDATA_1,WDATA_0}; // @[RAM_ST.scala 31:115]
  wire [63:0] _T_7 = {WDATA_7,WDATA_6,WDATA_5,WDATA_4}; // @[RAM_ST.scala 31:115]
  wire [5:0] _T_10 = {{1'd0}, read_elem_counter_cur_valid}; // @[RAM_ST.scala 32:46]
  wire [127:0] _T_17 = ram__T_15_data;
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
  assign ram__T_15_addr = ram__T_15_addr_pipe_0;
  assign ram__T_15_data = ram[ram__T_15_addr]; // @[RAM_ST.scala 29:24]
  assign ram__T_9_data = {_T_7,_T_4};
  assign ram__T_9_addr = _T[4:0];
  assign ram__T_9_mask = 1'h1;
  assign ram__T_9_en = write_elem_counter_valid;
  assign RDATA_0 = _T_17[15:0]; // @[RAM_ST.scala 32:9]
  assign RDATA_1 = _T_17[31:16]; // @[RAM_ST.scala 32:9]
  assign RDATA_2 = _T_17[47:32]; // @[RAM_ST.scala 32:9]
  assign RDATA_3 = _T_17[63:48]; // @[RAM_ST.scala 32:9]
  assign RDATA_4 = _T_17[79:64]; // @[RAM_ST.scala 32:9]
  assign RDATA_5 = _T_17[95:80]; // @[RAM_ST.scala 32:9]
  assign RDATA_6 = _T_17[111:96]; // @[RAM_ST.scala 32:9]
  assign RDATA_7 = _T_17[127:112]; // @[RAM_ST.scala 32:9]
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
  _RAND_0 = {4{`RANDOM}};
  `ifdef RANDOMIZE_MEM_INIT
  for (initvar = 0; initvar < 32; initvar = initvar+1)
    ram[initvar] = _RAND_0[127:0];
  `endif // RANDOMIZE_MEM_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  ram__T_15_en_pipe_0 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  ram__T_15_addr_pipe_0 = _RAND_2[4:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if(ram__T_9_en & ram__T_9_mask) begin
      ram[ram__T_9_addr] <= ram__T_9_data; // @[RAM_ST.scala 29:24]
    end
    ram__T_15_en_pipe_0 <= read_elem_counter_valid;
    if (read_elem_counter_valid) begin
      ram__T_15_addr_pipe_0 <= _T_10[4:0];
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
  input  [15:0] I_4,
  input  [15:0] I_5,
  input  [15:0] I_6,
  input  [15:0] I_7,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4,
  output [15:0] O_5,
  output [15:0] O_6,
  output [15:0] O_7
);
  wire  mem_clock; // @[Upsample.scala 31:19]
  wire  mem_reset; // @[Upsample.scala 31:19]
  wire  mem_RE; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_0; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_1; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_2; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_3; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_4; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_5; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_6; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_7; // @[Upsample.scala 31:19]
  wire  mem_WE; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_0; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_1; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_2; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_3; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_4; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_5; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_6; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_7; // @[Upsample.scala 31:19]
  reg [4:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_1 = value == 5'h1f; // @[Counter.scala 38:24]
  wire [4:0] _T_3 = value + 5'h1; // @[Counter.scala 39:22]
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
  wire [15:0] dataOut_4 = mem_RDATA_4; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_5 = mem_RDATA_5; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_6 = mem_RDATA_6; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_7 = mem_RDATA_7; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  RAM_ST mem ( // @[Upsample.scala 31:19]
    .clock(mem_clock),
    .reset(mem_reset),
    .RE(mem_RE),
    .RDATA_0(mem_RDATA_0),
    .RDATA_1(mem_RDATA_1),
    .RDATA_2(mem_RDATA_2),
    .RDATA_3(mem_RDATA_3),
    .RDATA_4(mem_RDATA_4),
    .RDATA_5(mem_RDATA_5),
    .RDATA_6(mem_RDATA_6),
    .RDATA_7(mem_RDATA_7),
    .WE(mem_WE),
    .WDATA_0(mem_WDATA_0),
    .WDATA_1(mem_WDATA_1),
    .WDATA_2(mem_WDATA_2),
    .WDATA_3(mem_WDATA_3),
    .WDATA_4(mem_WDATA_4),
    .WDATA_5(mem_WDATA_5),
    .WDATA_6(mem_WDATA_6),
    .WDATA_7(mem_WDATA_7)
  );
  assign valid_down = valid_up; // @[Upsample.scala 46:14]
  assign O_0 = _T_10 ? I_0 : dataOut_0; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_1 = _T_10 ? I_1 : dataOut_1; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_2 = _T_10 ? I_2 : dataOut_2; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_3 = _T_10 ? I_3 : dataOut_3; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_4 = _T_10 ? I_4 : dataOut_4; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_5 = _T_10 ? I_5 : dataOut_5; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_6 = _T_10 ? I_6 : dataOut_6; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_7 = _T_10 ? I_7 : dataOut_7; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign mem_clock = clock;
  assign mem_reset = reset;
  assign mem_RE = valid_up & _T_13; // @[Upsample.scala 40:110 Upsample.scala 41:27 Upsample.scala 42:44]
  assign mem_WE = valid_up & _T_10; // @[Upsample.scala 39:54 Upsample.scala 39:86 Upsample.scala 42:25]
  assign mem_WDATA_0 = I_0; // @[Upsample.scala 36:13]
  assign mem_WDATA_1 = I_1; // @[Upsample.scala 36:13]
  assign mem_WDATA_2 = I_2; // @[Upsample.scala 36:13]
  assign mem_WDATA_3 = I_3; // @[Upsample.scala 36:13]
  assign mem_WDATA_4 = I_4; // @[Upsample.scala 36:13]
  assign mem_WDATA_5 = I_5; // @[Upsample.scala 36:13]
  assign mem_WDATA_6 = I_6; // @[Upsample.scala 36:13]
  assign mem_WDATA_7 = I_7; // @[Upsample.scala 36:13]
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
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  value_1 = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 5'h0;
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
module Module_0(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  input  [15:0] I_1,
  input  [15:0] I_2,
  input  [15:0] I_3,
  input  [15:0] I_4,
  input  [15:0] I_5,
  input  [15:0] I_6,
  input  [15:0] I_7,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4,
  output [15:0] O_5,
  output [15:0] O_6,
  output [15:0] O_7
);
  wire  n3_valid_up; // @[Top.scala 17:20]
  wire  n3_valid_down; // @[Top.scala 17:20]
  wire [15:0] n3_I_0; // @[Top.scala 17:20]
  wire [15:0] n3_I_1; // @[Top.scala 17:20]
  wire [15:0] n3_I_2; // @[Top.scala 17:20]
  wire [15:0] n3_I_3; // @[Top.scala 17:20]
  wire [15:0] n3_I_4; // @[Top.scala 17:20]
  wire [15:0] n3_I_5; // @[Top.scala 17:20]
  wire [15:0] n3_I_6; // @[Top.scala 17:20]
  wire [15:0] n3_I_7; // @[Top.scala 17:20]
  wire [15:0] n3_O_0; // @[Top.scala 17:20]
  wire [15:0] n3_O_1; // @[Top.scala 17:20]
  wire [15:0] n3_O_2; // @[Top.scala 17:20]
  wire [15:0] n3_O_3; // @[Top.scala 17:20]
  wire [15:0] n3_O_4; // @[Top.scala 17:20]
  wire [15:0] n3_O_5; // @[Top.scala 17:20]
  wire [15:0] n3_O_6; // @[Top.scala 17:20]
  wire [15:0] n3_O_7; // @[Top.scala 17:20]
  wire  n4_clock; // @[Top.scala 20:20]
  wire  n4_reset; // @[Top.scala 20:20]
  wire  n4_valid_up; // @[Top.scala 20:20]
  wire  n4_valid_down; // @[Top.scala 20:20]
  wire [15:0] n4_I_0; // @[Top.scala 20:20]
  wire [15:0] n4_I_1; // @[Top.scala 20:20]
  wire [15:0] n4_I_2; // @[Top.scala 20:20]
  wire [15:0] n4_I_3; // @[Top.scala 20:20]
  wire [15:0] n4_I_4; // @[Top.scala 20:20]
  wire [15:0] n4_I_5; // @[Top.scala 20:20]
  wire [15:0] n4_I_6; // @[Top.scala 20:20]
  wire [15:0] n4_I_7; // @[Top.scala 20:20]
  wire [15:0] n4_O_0; // @[Top.scala 20:20]
  wire [15:0] n4_O_1; // @[Top.scala 20:20]
  wire [15:0] n4_O_2; // @[Top.scala 20:20]
  wire [15:0] n4_O_3; // @[Top.scala 20:20]
  wire [15:0] n4_O_4; // @[Top.scala 20:20]
  wire [15:0] n4_O_5; // @[Top.scala 20:20]
  wire [15:0] n4_O_6; // @[Top.scala 20:20]
  wire [15:0] n4_O_7; // @[Top.scala 20:20]
  wire  n5_valid_up; // @[Top.scala 23:20]
  wire  n5_valid_down; // @[Top.scala 23:20]
  wire [15:0] n5_I_0; // @[Top.scala 23:20]
  wire [15:0] n5_I_1; // @[Top.scala 23:20]
  wire [15:0] n5_I_2; // @[Top.scala 23:20]
  wire [15:0] n5_I_3; // @[Top.scala 23:20]
  wire [15:0] n5_I_4; // @[Top.scala 23:20]
  wire [15:0] n5_I_5; // @[Top.scala 23:20]
  wire [15:0] n5_I_6; // @[Top.scala 23:20]
  wire [15:0] n5_I_7; // @[Top.scala 23:20]
  wire [15:0] n5_O_0; // @[Top.scala 23:20]
  wire [15:0] n5_O_1; // @[Top.scala 23:20]
  wire [15:0] n5_O_2; // @[Top.scala 23:20]
  wire [15:0] n5_O_3; // @[Top.scala 23:20]
  wire [15:0] n5_O_4; // @[Top.scala 23:20]
  wire [15:0] n5_O_5; // @[Top.scala 23:20]
  wire [15:0] n5_O_6; // @[Top.scala 23:20]
  wire [15:0] n5_O_7; // @[Top.scala 23:20]
  Passthrough n3 ( // @[Top.scala 17:20]
    .valid_up(n3_valid_up),
    .valid_down(n3_valid_down),
    .I_0(n3_I_0),
    .I_1(n3_I_1),
    .I_2(n3_I_2),
    .I_3(n3_I_3),
    .I_4(n3_I_4),
    .I_5(n3_I_5),
    .I_6(n3_I_6),
    .I_7(n3_I_7),
    .O_0(n3_O_0),
    .O_1(n3_O_1),
    .O_2(n3_O_2),
    .O_3(n3_O_3),
    .O_4(n3_O_4),
    .O_5(n3_O_5),
    .O_6(n3_O_6),
    .O_7(n3_O_7)
  );
  UpT n4 ( // @[Top.scala 20:20]
    .clock(n4_clock),
    .reset(n4_reset),
    .valid_up(n4_valid_up),
    .valid_down(n4_valid_down),
    .I_0(n4_I_0),
    .I_1(n4_I_1),
    .I_2(n4_I_2),
    .I_3(n4_I_3),
    .I_4(n4_I_4),
    .I_5(n4_I_5),
    .I_6(n4_I_6),
    .I_7(n4_I_7),
    .O_0(n4_O_0),
    .O_1(n4_O_1),
    .O_2(n4_O_2),
    .O_3(n4_O_3),
    .O_4(n4_O_4),
    .O_5(n4_O_5),
    .O_6(n4_O_6),
    .O_7(n4_O_7)
  );
  Passthrough n5 ( // @[Top.scala 23:20]
    .valid_up(n5_valid_up),
    .valid_down(n5_valid_down),
    .I_0(n5_I_0),
    .I_1(n5_I_1),
    .I_2(n5_I_2),
    .I_3(n5_I_3),
    .I_4(n5_I_4),
    .I_5(n5_I_5),
    .I_6(n5_I_6),
    .I_7(n5_I_7),
    .O_0(n5_O_0),
    .O_1(n5_O_1),
    .O_2(n5_O_2),
    .O_3(n5_O_3),
    .O_4(n5_O_4),
    .O_5(n5_O_5),
    .O_6(n5_O_6),
    .O_7(n5_O_7)
  );
  assign valid_down = n5_valid_down; // @[Top.scala 27:16]
  assign O_0 = n5_O_0; // @[Top.scala 26:7]
  assign O_1 = n5_O_1; // @[Top.scala 26:7]
  assign O_2 = n5_O_2; // @[Top.scala 26:7]
  assign O_3 = n5_O_3; // @[Top.scala 26:7]
  assign O_4 = n5_O_4; // @[Top.scala 26:7]
  assign O_5 = n5_O_5; // @[Top.scala 26:7]
  assign O_6 = n5_O_6; // @[Top.scala 26:7]
  assign O_7 = n5_O_7; // @[Top.scala 26:7]
  assign n3_valid_up = valid_up; // @[Top.scala 19:17]
  assign n3_I_0 = I_0; // @[Top.scala 18:10]
  assign n3_I_1 = I_1; // @[Top.scala 18:10]
  assign n3_I_2 = I_2; // @[Top.scala 18:10]
  assign n3_I_3 = I_3; // @[Top.scala 18:10]
  assign n3_I_4 = I_4; // @[Top.scala 18:10]
  assign n3_I_5 = I_5; // @[Top.scala 18:10]
  assign n3_I_6 = I_6; // @[Top.scala 18:10]
  assign n3_I_7 = I_7; // @[Top.scala 18:10]
  assign n4_clock = clock;
  assign n4_reset = reset;
  assign n4_valid_up = n3_valid_down; // @[Top.scala 22:17]
  assign n4_I_0 = n3_O_0; // @[Top.scala 21:10]
  assign n4_I_1 = n3_O_1; // @[Top.scala 21:10]
  assign n4_I_2 = n3_O_2; // @[Top.scala 21:10]
  assign n4_I_3 = n3_O_3; // @[Top.scala 21:10]
  assign n4_I_4 = n3_O_4; // @[Top.scala 21:10]
  assign n4_I_5 = n3_O_5; // @[Top.scala 21:10]
  assign n4_I_6 = n3_O_6; // @[Top.scala 21:10]
  assign n4_I_7 = n3_O_7; // @[Top.scala 21:10]
  assign n5_valid_up = n4_valid_down; // @[Top.scala 25:17]
  assign n5_I_0 = n4_O_0; // @[Top.scala 24:10]
  assign n5_I_1 = n4_O_1; // @[Top.scala 24:10]
  assign n5_I_2 = n4_O_2; // @[Top.scala 24:10]
  assign n5_I_3 = n4_O_3; // @[Top.scala 24:10]
  assign n5_I_4 = n4_O_4; // @[Top.scala 24:10]
  assign n5_I_5 = n4_O_5; // @[Top.scala 24:10]
  assign n5_I_6 = n4_O_6; // @[Top.scala 24:10]
  assign n5_I_7 = n4_O_7; // @[Top.scala 24:10]
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
  input  [15:0] I_4,
  input  [15:0] I_5,
  input  [15:0] I_6,
  input  [15:0] I_7,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4,
  output [15:0] O_5,
  output [15:0] O_6,
  output [15:0] O_7
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [15:0] op_I_0; // @[MapT.scala 8:20]
  wire [15:0] op_I_1; // @[MapT.scala 8:20]
  wire [15:0] op_I_2; // @[MapT.scala 8:20]
  wire [15:0] op_I_3; // @[MapT.scala 8:20]
  wire [15:0] op_I_4; // @[MapT.scala 8:20]
  wire [15:0] op_I_5; // @[MapT.scala 8:20]
  wire [15:0] op_I_6; // @[MapT.scala 8:20]
  wire [15:0] op_I_7; // @[MapT.scala 8:20]
  wire [15:0] op_O_0; // @[MapT.scala 8:20]
  wire [15:0] op_O_1; // @[MapT.scala 8:20]
  wire [15:0] op_O_2; // @[MapT.scala 8:20]
  wire [15:0] op_O_3; // @[MapT.scala 8:20]
  wire [15:0] op_O_4; // @[MapT.scala 8:20]
  wire [15:0] op_O_5; // @[MapT.scala 8:20]
  wire [15:0] op_O_6; // @[MapT.scala 8:20]
  wire [15:0] op_O_7; // @[MapT.scala 8:20]
  Module_0 op ( // @[MapT.scala 8:20]
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
    .I_7(op_I_7),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2),
    .O_3(op_O_3),
    .O_4(op_O_4),
    .O_5(op_O_5),
    .O_6(op_O_6),
    .O_7(op_O_7)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0 = op_O_0; // @[MapT.scala 15:7]
  assign O_1 = op_O_1; // @[MapT.scala 15:7]
  assign O_2 = op_O_2; // @[MapT.scala 15:7]
  assign O_3 = op_O_3; // @[MapT.scala 15:7]
  assign O_4 = op_O_4; // @[MapT.scala 15:7]
  assign O_5 = op_O_5; // @[MapT.scala 15:7]
  assign O_6 = op_O_6; // @[MapT.scala 15:7]
  assign O_7 = op_O_7; // @[MapT.scala 15:7]
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
  assign op_I_7 = I_7; // @[MapT.scala 14:10]
endmodule
module NestedCounters_9(
  input   CE,
  output  valid
);
  wire  NestedCounters_CE; // @[NestedCounters.scala 43:31]
  wire  NestedCounters_valid; // @[NestedCounters.scala 43:31]
  NestedCounters_2 NestedCounters ( // @[NestedCounters.scala 43:31]
    .CE(NestedCounters_CE),
    .valid(NestedCounters_valid)
  );
  assign valid = NestedCounters_valid; // @[NestedCounters.scala 48:11]
  assign NestedCounters_CE = CE; // @[NestedCounters.scala 49:22]
endmodule
module NestedCountersWithNumValid_2(
  input         clock,
  input         reset,
  input         CE,
  output        valid,
  output [12:0] cur_valid
);
  wire  NestedCounters_CE; // @[NestedCounters.scala 20:44]
  wire  NestedCounters_valid; // @[NestedCounters.scala 20:44]
  wire  _T = CE & NestedCounters_valid; // @[NestedCounters.scala 25:49]
  reg [12:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire [12:0] _T_4 = value + 13'h1; // @[Counter.scala 39:22]
  NestedCounters_9 NestedCounters ( // @[NestedCounters.scala 20:44]
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
  value = _RAND_0[12:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 13'h0;
    end else if (_T) begin
      value <= _T_4;
    end
  end
endmodule
module RAM_ST_1(
  input         clock,
  input         reset,
  input         RE,
  output [15:0] RDATA_0,
  output [15:0] RDATA_1,
  output [15:0] RDATA_2,
  output [15:0] RDATA_3,
  output [15:0] RDATA_4,
  output [15:0] RDATA_5,
  output [15:0] RDATA_6,
  output [15:0] RDATA_7,
  input         WE,
  input  [15:0] WDATA_0,
  input  [15:0] WDATA_1,
  input  [15:0] WDATA_2,
  input  [15:0] WDATA_3,
  input  [15:0] WDATA_4,
  input  [15:0] WDATA_5,
  input  [15:0] WDATA_6,
  input  [15:0] WDATA_7
);
  wire  write_elem_counter_clock; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_reset; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_CE; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_valid; // @[RAM_ST.scala 20:34]
  wire [12:0] write_elem_counter_cur_valid; // @[RAM_ST.scala 20:34]
  wire  read_elem_counter_clock; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_reset; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_CE; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_valid; // @[RAM_ST.scala 21:33]
  wire [12:0] read_elem_counter_cur_valid; // @[RAM_ST.scala 21:33]
  reg [127:0] ram [0:8191]; // @[RAM_ST.scala 29:24]
  reg [127:0] _RAND_0;
  wire [127:0] ram__T_15_data; // @[RAM_ST.scala 29:24]
  wire [12:0] ram__T_15_addr; // @[RAM_ST.scala 29:24]
  wire [127:0] ram__T_9_data; // @[RAM_ST.scala 29:24]
  wire [12:0] ram__T_9_addr; // @[RAM_ST.scala 29:24]
  wire  ram__T_9_mask; // @[RAM_ST.scala 29:24]
  wire  ram__T_9_en; // @[RAM_ST.scala 29:24]
  reg  ram__T_15_en_pipe_0;
  reg [31:0] _RAND_1;
  reg [12:0] ram__T_15_addr_pipe_0;
  reg [31:0] _RAND_2;
  wire [13:0] _T = {{1'd0}, write_elem_counter_cur_valid}; // @[RAM_ST.scala 31:71]
  wire [63:0] _T_4 = {WDATA_3,WDATA_2,WDATA_1,WDATA_0}; // @[RAM_ST.scala 31:115]
  wire [63:0] _T_7 = {WDATA_7,WDATA_6,WDATA_5,WDATA_4}; // @[RAM_ST.scala 31:115]
  wire [13:0] _T_10 = {{1'd0}, read_elem_counter_cur_valid}; // @[RAM_ST.scala 32:46]
  wire [127:0] _T_17 = ram__T_15_data;
  NestedCountersWithNumValid_2 write_elem_counter ( // @[RAM_ST.scala 20:34]
    .clock(write_elem_counter_clock),
    .reset(write_elem_counter_reset),
    .CE(write_elem_counter_CE),
    .valid(write_elem_counter_valid),
    .cur_valid(write_elem_counter_cur_valid)
  );
  NestedCountersWithNumValid_2 read_elem_counter ( // @[RAM_ST.scala 21:33]
    .clock(read_elem_counter_clock),
    .reset(read_elem_counter_reset),
    .CE(read_elem_counter_CE),
    .valid(read_elem_counter_valid),
    .cur_valid(read_elem_counter_cur_valid)
  );
  assign ram__T_15_addr = ram__T_15_addr_pipe_0;
  assign ram__T_15_data = ram[ram__T_15_addr]; // @[RAM_ST.scala 29:24]
  assign ram__T_9_data = {_T_7,_T_4};
  assign ram__T_9_addr = _T[12:0];
  assign ram__T_9_mask = 1'h1;
  assign ram__T_9_en = write_elem_counter_valid;
  assign RDATA_0 = _T_17[15:0]; // @[RAM_ST.scala 32:9]
  assign RDATA_1 = _T_17[31:16]; // @[RAM_ST.scala 32:9]
  assign RDATA_2 = _T_17[47:32]; // @[RAM_ST.scala 32:9]
  assign RDATA_3 = _T_17[63:48]; // @[RAM_ST.scala 32:9]
  assign RDATA_4 = _T_17[79:64]; // @[RAM_ST.scala 32:9]
  assign RDATA_5 = _T_17[95:80]; // @[RAM_ST.scala 32:9]
  assign RDATA_6 = _T_17[111:96]; // @[RAM_ST.scala 32:9]
  assign RDATA_7 = _T_17[127:112]; // @[RAM_ST.scala 32:9]
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
  _RAND_0 = {4{`RANDOM}};
  `ifdef RANDOMIZE_MEM_INIT
  for (initvar = 0; initvar < 8192; initvar = initvar+1)
    ram[initvar] = _RAND_0[127:0];
  `endif // RANDOMIZE_MEM_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  ram__T_15_en_pipe_0 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  ram__T_15_addr_pipe_0 = _RAND_2[12:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if(ram__T_9_en & ram__T_9_mask) begin
      ram[ram__T_9_addr] <= ram__T_9_data; // @[RAM_ST.scala 29:24]
    end
    ram__T_15_en_pipe_0 <= read_elem_counter_valid;
    if (read_elem_counter_valid) begin
      ram__T_15_addr_pipe_0 <= _T_10[12:0];
    end
  end
endmodule
module UpT_1(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  input  [15:0] I_1,
  input  [15:0] I_2,
  input  [15:0] I_3,
  input  [15:0] I_4,
  input  [15:0] I_5,
  input  [15:0] I_6,
  input  [15:0] I_7,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4,
  output [15:0] O_5,
  output [15:0] O_6,
  output [15:0] O_7
);
  wire  mem_clock; // @[Upsample.scala 31:19]
  wire  mem_reset; // @[Upsample.scala 31:19]
  wire  mem_RE; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_0; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_1; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_2; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_3; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_4; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_5; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_6; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_7; // @[Upsample.scala 31:19]
  wire  mem_WE; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_0; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_1; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_2; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_3; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_4; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_5; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_6; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_7; // @[Upsample.scala 31:19]
  reg [12:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_1 = value == 13'h1fff; // @[Counter.scala 38:24]
  wire [12:0] _T_3 = value + 13'h1; // @[Counter.scala 39:22]
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
  wire [15:0] dataOut_4 = mem_RDATA_4; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_5 = mem_RDATA_5; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_6 = mem_RDATA_6; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_7 = mem_RDATA_7; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  RAM_ST_1 mem ( // @[Upsample.scala 31:19]
    .clock(mem_clock),
    .reset(mem_reset),
    .RE(mem_RE),
    .RDATA_0(mem_RDATA_0),
    .RDATA_1(mem_RDATA_1),
    .RDATA_2(mem_RDATA_2),
    .RDATA_3(mem_RDATA_3),
    .RDATA_4(mem_RDATA_4),
    .RDATA_5(mem_RDATA_5),
    .RDATA_6(mem_RDATA_6),
    .RDATA_7(mem_RDATA_7),
    .WE(mem_WE),
    .WDATA_0(mem_WDATA_0),
    .WDATA_1(mem_WDATA_1),
    .WDATA_2(mem_WDATA_2),
    .WDATA_3(mem_WDATA_3),
    .WDATA_4(mem_WDATA_4),
    .WDATA_5(mem_WDATA_5),
    .WDATA_6(mem_WDATA_6),
    .WDATA_7(mem_WDATA_7)
  );
  assign valid_down = valid_up; // @[Upsample.scala 46:14]
  assign O_0 = _T_10 ? I_0 : dataOut_0; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_1 = _T_10 ? I_1 : dataOut_1; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_2 = _T_10 ? I_2 : dataOut_2; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_3 = _T_10 ? I_3 : dataOut_3; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_4 = _T_10 ? I_4 : dataOut_4; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_5 = _T_10 ? I_5 : dataOut_5; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_6 = _T_10 ? I_6 : dataOut_6; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_7 = _T_10 ? I_7 : dataOut_7; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign mem_clock = clock;
  assign mem_reset = reset;
  assign mem_RE = valid_up & _T_13; // @[Upsample.scala 40:110 Upsample.scala 41:27 Upsample.scala 42:44]
  assign mem_WE = valid_up & _T_10; // @[Upsample.scala 39:54 Upsample.scala 39:86 Upsample.scala 42:25]
  assign mem_WDATA_0 = I_0; // @[Upsample.scala 36:13]
  assign mem_WDATA_1 = I_1; // @[Upsample.scala 36:13]
  assign mem_WDATA_2 = I_2; // @[Upsample.scala 36:13]
  assign mem_WDATA_3 = I_3; // @[Upsample.scala 36:13]
  assign mem_WDATA_4 = I_4; // @[Upsample.scala 36:13]
  assign mem_WDATA_5 = I_5; // @[Upsample.scala 36:13]
  assign mem_WDATA_6 = I_6; // @[Upsample.scala 36:13]
  assign mem_WDATA_7 = I_7; // @[Upsample.scala 36:13]
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
  value = _RAND_0[12:0];
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
      value <= 13'h0;
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
module Module_1(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I0,
  input  [15:0] I1,
  output [15:0] O
);
  wire  n20_valid_up; // @[Top.scala 34:21]
  wire  n20_valid_down; // @[Top.scala 34:21]
  wire [15:0] n20_I0; // @[Top.scala 34:21]
  wire [15:0] n20_I1; // @[Top.scala 34:21]
  wire [15:0] n20_O_t0b; // @[Top.scala 34:21]
  wire [15:0] n20_O_t1b; // @[Top.scala 34:21]
  wire  n21_clock; // @[Top.scala 38:21]
  wire  n21_reset; // @[Top.scala 38:21]
  wire  n21_valid_up; // @[Top.scala 38:21]
  wire  n21_valid_down; // @[Top.scala 38:21]
  wire [15:0] n21_I_t0b; // @[Top.scala 38:21]
  wire [15:0] n21_I_t1b; // @[Top.scala 38:21]
  wire [15:0] n21_O; // @[Top.scala 38:21]
  AtomTuple n20 ( // @[Top.scala 34:21]
    .valid_up(n20_valid_up),
    .valid_down(n20_valid_down),
    .I0(n20_I0),
    .I1(n20_I1),
    .O_t0b(n20_O_t0b),
    .O_t1b(n20_O_t1b)
  );
  Mul n21 ( // @[Top.scala 38:21]
    .clock(n21_clock),
    .reset(n21_reset),
    .valid_up(n21_valid_up),
    .valid_down(n21_valid_down),
    .I_t0b(n21_I_t0b),
    .I_t1b(n21_I_t1b),
    .O(n21_O)
  );
  assign valid_down = n21_valid_down; // @[Top.scala 42:16]
  assign O = n21_O; // @[Top.scala 41:7]
  assign n20_valid_up = valid_up; // @[Top.scala 37:18]
  assign n20_I0 = I0; // @[Top.scala 35:12]
  assign n20_I1 = I1; // @[Top.scala 36:12]
  assign n21_clock = clock;
  assign n21_reset = reset;
  assign n21_valid_up = n20_valid_down; // @[Top.scala 40:18]
  assign n21_I_t0b = n20_O_t0b; // @[Top.scala 39:11]
  assign n21_I_t1b = n20_O_t1b; // @[Top.scala 39:11]
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
  input  [15:0] I0_4,
  input  [15:0] I0_5,
  input  [15:0] I0_6,
  input  [15:0] I0_7,
  input  [15:0] I1_0,
  input  [15:0] I1_1,
  input  [15:0] I1_2,
  input  [15:0] I1_3,
  input  [15:0] I1_4,
  input  [15:0] I1_5,
  input  [15:0] I1_6,
  input  [15:0] I1_7,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4,
  output [15:0] O_5,
  output [15:0] O_6,
  output [15:0] O_7
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
  wire  other_ops_3_clock; // @[Map2S.scala 10:86]
  wire  other_ops_3_reset; // @[Map2S.scala 10:86]
  wire  other_ops_3_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_3_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_3_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_3_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_3_O; // @[Map2S.scala 10:86]
  wire  other_ops_4_clock; // @[Map2S.scala 10:86]
  wire  other_ops_4_reset; // @[Map2S.scala 10:86]
  wire  other_ops_4_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_4_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_4_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_4_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_4_O; // @[Map2S.scala 10:86]
  wire  other_ops_5_clock; // @[Map2S.scala 10:86]
  wire  other_ops_5_reset; // @[Map2S.scala 10:86]
  wire  other_ops_5_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_5_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_5_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_5_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_5_O; // @[Map2S.scala 10:86]
  wire  other_ops_6_clock; // @[Map2S.scala 10:86]
  wire  other_ops_6_reset; // @[Map2S.scala 10:86]
  wire  other_ops_6_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_6_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_6_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_6_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_6_O; // @[Map2S.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[Map2S.scala 26:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[Map2S.scala 26:83]
  wire  _T_2 = _T_1 & other_ops_2_valid_down; // @[Map2S.scala 26:83]
  wire  _T_3 = _T_2 & other_ops_3_valid_down; // @[Map2S.scala 26:83]
  wire  _T_4 = _T_3 & other_ops_4_valid_down; // @[Map2S.scala 26:83]
  wire  _T_5 = _T_4 & other_ops_5_valid_down; // @[Map2S.scala 26:83]
  Module_1 fst_op ( // @[Map2S.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I0(fst_op_I0),
    .I1(fst_op_I1),
    .O(fst_op_O)
  );
  Module_1 other_ops_0 ( // @[Map2S.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I0(other_ops_0_I0),
    .I1(other_ops_0_I1),
    .O(other_ops_0_O)
  );
  Module_1 other_ops_1 ( // @[Map2S.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I0(other_ops_1_I0),
    .I1(other_ops_1_I1),
    .O(other_ops_1_O)
  );
  Module_1 other_ops_2 ( // @[Map2S.scala 10:86]
    .clock(other_ops_2_clock),
    .reset(other_ops_2_reset),
    .valid_up(other_ops_2_valid_up),
    .valid_down(other_ops_2_valid_down),
    .I0(other_ops_2_I0),
    .I1(other_ops_2_I1),
    .O(other_ops_2_O)
  );
  Module_1 other_ops_3 ( // @[Map2S.scala 10:86]
    .clock(other_ops_3_clock),
    .reset(other_ops_3_reset),
    .valid_up(other_ops_3_valid_up),
    .valid_down(other_ops_3_valid_down),
    .I0(other_ops_3_I0),
    .I1(other_ops_3_I1),
    .O(other_ops_3_O)
  );
  Module_1 other_ops_4 ( // @[Map2S.scala 10:86]
    .clock(other_ops_4_clock),
    .reset(other_ops_4_reset),
    .valid_up(other_ops_4_valid_up),
    .valid_down(other_ops_4_valid_down),
    .I0(other_ops_4_I0),
    .I1(other_ops_4_I1),
    .O(other_ops_4_O)
  );
  Module_1 other_ops_5 ( // @[Map2S.scala 10:86]
    .clock(other_ops_5_clock),
    .reset(other_ops_5_reset),
    .valid_up(other_ops_5_valid_up),
    .valid_down(other_ops_5_valid_down),
    .I0(other_ops_5_I0),
    .I1(other_ops_5_I1),
    .O(other_ops_5_O)
  );
  Module_1 other_ops_6 ( // @[Map2S.scala 10:86]
    .clock(other_ops_6_clock),
    .reset(other_ops_6_reset),
    .valid_up(other_ops_6_valid_up),
    .valid_down(other_ops_6_valid_down),
    .I0(other_ops_6_I0),
    .I1(other_ops_6_I1),
    .O(other_ops_6_O)
  );
  assign valid_down = _T_5 & other_ops_6_valid_down; // @[Map2S.scala 26:14]
  assign O_0 = fst_op_O; // @[Map2S.scala 19:8]
  assign O_1 = other_ops_0_O; // @[Map2S.scala 24:12]
  assign O_2 = other_ops_1_O; // @[Map2S.scala 24:12]
  assign O_3 = other_ops_2_O; // @[Map2S.scala 24:12]
  assign O_4 = other_ops_3_O; // @[Map2S.scala 24:12]
  assign O_5 = other_ops_4_O; // @[Map2S.scala 24:12]
  assign O_6 = other_ops_5_O; // @[Map2S.scala 24:12]
  assign O_7 = other_ops_6_O; // @[Map2S.scala 24:12]
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
  assign other_ops_3_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_3_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_3_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_3_I0 = I0_4; // @[Map2S.scala 22:43]
  assign other_ops_3_I1 = I1_4; // @[Map2S.scala 23:43]
  assign other_ops_4_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_4_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_4_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_4_I0 = I0_5; // @[Map2S.scala 22:43]
  assign other_ops_4_I1 = I1_5; // @[Map2S.scala 23:43]
  assign other_ops_5_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_5_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_5_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_5_I0 = I0_6; // @[Map2S.scala 22:43]
  assign other_ops_5_I1 = I1_6; // @[Map2S.scala 23:43]
  assign other_ops_6_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_6_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_6_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_6_I0 = I0_7; // @[Map2S.scala 22:43]
  assign other_ops_6_I1 = I1_7; // @[Map2S.scala 23:43]
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
  input  [15:0] I0_4,
  input  [15:0] I0_5,
  input  [15:0] I0_6,
  input  [15:0] I0_7,
  input  [15:0] I1_0,
  input  [15:0] I1_1,
  input  [15:0] I1_2,
  input  [15:0] I1_3,
  input  [15:0] I1_4,
  input  [15:0] I1_5,
  input  [15:0] I1_6,
  input  [15:0] I1_7,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4,
  output [15:0] O_5,
  output [15:0] O_6,
  output [15:0] O_7
);
  wire  op_clock; // @[Map2T.scala 8:20]
  wire  op_reset; // @[Map2T.scala 8:20]
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_1; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_2; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_3; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_4; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_5; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_6; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_7; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_0; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_1; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_2; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_3; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_4; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_5; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_6; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_7; // @[Map2T.scala 8:20]
  wire [15:0] op_O_0; // @[Map2T.scala 8:20]
  wire [15:0] op_O_1; // @[Map2T.scala 8:20]
  wire [15:0] op_O_2; // @[Map2T.scala 8:20]
  wire [15:0] op_O_3; // @[Map2T.scala 8:20]
  wire [15:0] op_O_4; // @[Map2T.scala 8:20]
  wire [15:0] op_O_5; // @[Map2T.scala 8:20]
  wire [15:0] op_O_6; // @[Map2T.scala 8:20]
  wire [15:0] op_O_7; // @[Map2T.scala 8:20]
  Map2S op ( // @[Map2T.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0(op_I0_0),
    .I0_1(op_I0_1),
    .I0_2(op_I0_2),
    .I0_3(op_I0_3),
    .I0_4(op_I0_4),
    .I0_5(op_I0_5),
    .I0_6(op_I0_6),
    .I0_7(op_I0_7),
    .I1_0(op_I1_0),
    .I1_1(op_I1_1),
    .I1_2(op_I1_2),
    .I1_3(op_I1_3),
    .I1_4(op_I1_4),
    .I1_5(op_I1_5),
    .I1_6(op_I1_6),
    .I1_7(op_I1_7),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2),
    .O_3(op_O_3),
    .O_4(op_O_4),
    .O_5(op_O_5),
    .O_6(op_O_6),
    .O_7(op_O_7)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0 = op_O_0; // @[Map2T.scala 17:7]
  assign O_1 = op_O_1; // @[Map2T.scala 17:7]
  assign O_2 = op_O_2; // @[Map2T.scala 17:7]
  assign O_3 = op_O_3; // @[Map2T.scala 17:7]
  assign O_4 = op_O_4; // @[Map2T.scala 17:7]
  assign O_5 = op_O_5; // @[Map2T.scala 17:7]
  assign O_6 = op_O_6; // @[Map2T.scala 17:7]
  assign O_7 = op_O_7; // @[Map2T.scala 17:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0 = I0_0; // @[Map2T.scala 15:11]
  assign op_I0_1 = I0_1; // @[Map2T.scala 15:11]
  assign op_I0_2 = I0_2; // @[Map2T.scala 15:11]
  assign op_I0_3 = I0_3; // @[Map2T.scala 15:11]
  assign op_I0_4 = I0_4; // @[Map2T.scala 15:11]
  assign op_I0_5 = I0_5; // @[Map2T.scala 15:11]
  assign op_I0_6 = I0_6; // @[Map2T.scala 15:11]
  assign op_I0_7 = I0_7; // @[Map2T.scala 15:11]
  assign op_I1_0 = I1_0; // @[Map2T.scala 16:11]
  assign op_I1_1 = I1_1; // @[Map2T.scala 16:11]
  assign op_I1_2 = I1_2; // @[Map2T.scala 16:11]
  assign op_I1_3 = I1_3; // @[Map2T.scala 16:11]
  assign op_I1_4 = I1_4; // @[Map2T.scala 16:11]
  assign op_I1_5 = I1_5; // @[Map2T.scala 16:11]
  assign op_I1_6 = I1_6; // @[Map2T.scala 16:11]
  assign op_I1_7 = I1_7; // @[Map2T.scala 16:11]
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
  input  [15:0] I_4,
  input  [15:0] I_5,
  input  [15:0] I_6,
  input  [15:0] I_7,
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
  wire [15:0] AddNoValid_3_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_3_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_3_O; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_4_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_4_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_4_O; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_5_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_5_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_5_O; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_6_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_6_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_6_O; // @[ReduceS.scala 20:43]
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
  reg [15:0] _T_5; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_5;
  reg [15:0] _T_6; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_6;
  reg [15:0] _T_7; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_7;
  reg [15:0] _T_8; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_8;
  reg  _T_9; // @[ReduceS.scala 47:32]
  reg [31:0] _RAND_9;
  reg  _T_10; // @[ReduceS.scala 47:24]
  reg [31:0] _RAND_10;
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
  AddNoValid AddNoValid_6 ( // @[ReduceS.scala 20:43]
    .I_t0b(AddNoValid_6_I_t0b),
    .I_t1b(AddNoValid_6_I_t1b),
    .O(AddNoValid_6_O)
  );
  assign valid_down = _T_10; // @[ReduceS.scala 47:14]
  assign O_0 = _T; // @[ReduceS.scala 27:14]
  assign AddNoValid_I_t0b = _T_2; // @[ReduceS.scala 43:18]
  assign AddNoValid_I_t1b = AddNoValid_1_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_1_I_t0b = AddNoValid_2_O; // @[ReduceS.scala 31:18]
  assign AddNoValid_1_I_t1b = AddNoValid_3_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_2_I_t0b = _T_5; // @[ReduceS.scala 43:18]
  assign AddNoValid_2_I_t1b = AddNoValid_5_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_3_I_t0b = AddNoValid_4_O; // @[ReduceS.scala 31:18]
  assign AddNoValid_3_I_t1b = _T_7; // @[ReduceS.scala 43:18]
  assign AddNoValid_4_I_t0b = _T_1; // @[ReduceS.scala 43:18]
  assign AddNoValid_4_I_t1b = _T_4; // @[ReduceS.scala 43:18]
  assign AddNoValid_5_I_t0b = AddNoValid_6_O; // @[ReduceS.scala 31:18]
  assign AddNoValid_5_I_t1b = _T_6; // @[ReduceS.scala 43:18]
  assign AddNoValid_6_I_t0b = _T_8; // @[ReduceS.scala 43:18]
  assign AddNoValid_6_I_t1b = _T_3; // @[ReduceS.scala 43:18]
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
  _T_5 = _RAND_5[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_6 = _RAND_6[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_7 = _RAND_7[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{`RANDOM}};
  _T_8 = _RAND_8[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_9 = {1{`RANDOM}};
  _T_9 = _RAND_9[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_10 = {1{`RANDOM}};
  _T_10 = _RAND_10[0:0];
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
    _T_8 <= I_7;
    if (reset) begin
      _T_9 <= 1'h0;
    end else begin
      _T_9 <= valid_up;
    end
    _T_10 <= _T_9;
  end
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
  input  [15:0] I_4,
  input  [15:0] I_5,
  input  [15:0] I_6,
  input  [15:0] I_7,
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
  wire [15:0] op_I_4; // @[MapT.scala 8:20]
  wire [15:0] op_I_5; // @[MapT.scala 8:20]
  wire [15:0] op_I_6; // @[MapT.scala 8:20]
  wire [15:0] op_I_7; // @[MapT.scala 8:20]
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
    .I_4(op_I_4),
    .I_5(op_I_5),
    .I_6(op_I_6),
    .I_7(op_I_7),
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
  assign op_I_7 = I_7; // @[MapT.scala 14:10]
endmodule
module NestedCountersWithNumValid_4(
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
  reg [4:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire  _T_3 = value == 5'h1f; // @[Counter.scala 38:24]
  wire [4:0] _T_5 = value + 5'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value == 5'h0; // @[ReduceT.scala 34:60]
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
  NestedCountersWithNumValid_4 NestedCountersWithNumValid ( // @[ReduceT.scala 22:34]
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
  value = _RAND_1[4:0];
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
      value <= 5'h0;
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
module Passthrough_6(
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  output [15:0] O
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O = I_0; // @[Passthrough.scala 17:68]
endmodule
module Module_2(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  input  [15:0] I_1,
  input  [15:0] I_2,
  input  [15:0] I_3,
  input  [15:0] I_4,
  input  [15:0] I_5,
  input  [15:0] I_6,
  input  [15:0] I_7,
  output [15:0] O
);
  wire  n28_clock; // @[Top.scala 48:21]
  wire  n28_reset; // @[Top.scala 48:21]
  wire  n28_valid_up; // @[Top.scala 48:21]
  wire  n28_valid_down; // @[Top.scala 48:21]
  wire [15:0] n28_I_0; // @[Top.scala 48:21]
  wire [15:0] n28_I_1; // @[Top.scala 48:21]
  wire [15:0] n28_I_2; // @[Top.scala 48:21]
  wire [15:0] n28_I_3; // @[Top.scala 48:21]
  wire [15:0] n28_I_4; // @[Top.scala 48:21]
  wire [15:0] n28_I_5; // @[Top.scala 48:21]
  wire [15:0] n28_I_6; // @[Top.scala 48:21]
  wire [15:0] n28_I_7; // @[Top.scala 48:21]
  wire [15:0] n28_O_0; // @[Top.scala 48:21]
  wire  n31_clock; // @[Top.scala 51:21]
  wire  n31_reset; // @[Top.scala 51:21]
  wire  n31_valid_up; // @[Top.scala 51:21]
  wire  n31_valid_down; // @[Top.scala 51:21]
  wire [15:0] n31_I_0; // @[Top.scala 51:21]
  wire [15:0] n31_O_0; // @[Top.scala 51:21]
  wire  n32_valid_up; // @[Top.scala 54:21]
  wire  n32_valid_down; // @[Top.scala 54:21]
  wire [15:0] n32_I_0; // @[Top.scala 54:21]
  wire [15:0] n32_O; // @[Top.scala 54:21]
  MapT_1 n28 ( // @[Top.scala 48:21]
    .clock(n28_clock),
    .reset(n28_reset),
    .valid_up(n28_valid_up),
    .valid_down(n28_valid_down),
    .I_0(n28_I_0),
    .I_1(n28_I_1),
    .I_2(n28_I_2),
    .I_3(n28_I_3),
    .I_4(n28_I_4),
    .I_5(n28_I_5),
    .I_6(n28_I_6),
    .I_7(n28_I_7),
    .O_0(n28_O_0)
  );
  ReduceT n31 ( // @[Top.scala 51:21]
    .clock(n31_clock),
    .reset(n31_reset),
    .valid_up(n31_valid_up),
    .valid_down(n31_valid_down),
    .I_0(n31_I_0),
    .O_0(n31_O_0)
  );
  Passthrough_6 n32 ( // @[Top.scala 54:21]
    .valid_up(n32_valid_up),
    .valid_down(n32_valid_down),
    .I_0(n32_I_0),
    .O(n32_O)
  );
  assign valid_down = n32_valid_down; // @[Top.scala 58:16]
  assign O = n32_O; // @[Top.scala 57:7]
  assign n28_clock = clock;
  assign n28_reset = reset;
  assign n28_valid_up = valid_up; // @[Top.scala 50:18]
  assign n28_I_0 = I_0; // @[Top.scala 49:11]
  assign n28_I_1 = I_1; // @[Top.scala 49:11]
  assign n28_I_2 = I_2; // @[Top.scala 49:11]
  assign n28_I_3 = I_3; // @[Top.scala 49:11]
  assign n28_I_4 = I_4; // @[Top.scala 49:11]
  assign n28_I_5 = I_5; // @[Top.scala 49:11]
  assign n28_I_6 = I_6; // @[Top.scala 49:11]
  assign n28_I_7 = I_7; // @[Top.scala 49:11]
  assign n31_clock = clock;
  assign n31_reset = reset;
  assign n31_valid_up = n28_valid_down; // @[Top.scala 53:18]
  assign n31_I_0 = n28_O_0; // @[Top.scala 52:11]
  assign n32_valid_up = n31_valid_down; // @[Top.scala 56:18]
  assign n32_I_0 = n31_O_0; // @[Top.scala 55:11]
endmodule
module MapT_2(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  input  [15:0] I_0,
  input  [15:0] I_1,
  input  [15:0] I_2,
  input  [15:0] I_3,
  input  [15:0] I_4,
  input  [15:0] I_5,
  input  [15:0] I_6,
  input  [15:0] I_7,
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
  wire [15:0] op_I_4; // @[MapT.scala 8:20]
  wire [15:0] op_I_5; // @[MapT.scala 8:20]
  wire [15:0] op_I_6; // @[MapT.scala 8:20]
  wire [15:0] op_I_7; // @[MapT.scala 8:20]
  wire [15:0] op_O; // @[MapT.scala 8:20]
  Module_2 op ( // @[MapT.scala 8:20]
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
    .I_7(op_I_7),
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
  assign op_I_4 = I_4; // @[MapT.scala 14:10]
  assign op_I_5 = I_5; // @[MapT.scala 14:10]
  assign op_I_6 = I_6; // @[MapT.scala 14:10]
  assign op_I_7 = I_7; // @[MapT.scala 14:10]
endmodule
module FIFO_3(
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
  input  [15:0] I0_4,
  input  [15:0] I0_5,
  input  [15:0] I0_6,
  input  [15:0] I0_7,
  input  [15:0] I1_0,
  input  [15:0] I1_1,
  input  [15:0] I1_2,
  input  [15:0] I1_3,
  input  [15:0] I1_4,
  input  [15:0] I1_5,
  input  [15:0] I1_6,
  input  [15:0] I1_7,
  output [15:0] O
);
  wire  n1_clock; // @[Top.scala 65:20]
  wire  n1_reset; // @[Top.scala 65:20]
  wire  n1_valid_up; // @[Top.scala 65:20]
  wire  n1_valid_down; // @[Top.scala 65:20]
  wire [15:0] n1_I_0; // @[Top.scala 65:20]
  wire [15:0] n1_I_1; // @[Top.scala 65:20]
  wire [15:0] n1_I_2; // @[Top.scala 65:20]
  wire [15:0] n1_I_3; // @[Top.scala 65:20]
  wire [15:0] n1_I_4; // @[Top.scala 65:20]
  wire [15:0] n1_I_5; // @[Top.scala 65:20]
  wire [15:0] n1_I_6; // @[Top.scala 65:20]
  wire [15:0] n1_I_7; // @[Top.scala 65:20]
  wire [15:0] n1_O_0; // @[Top.scala 65:20]
  wire [15:0] n1_O_1; // @[Top.scala 65:20]
  wire [15:0] n1_O_2; // @[Top.scala 65:20]
  wire [15:0] n1_O_3; // @[Top.scala 65:20]
  wire [15:0] n1_O_4; // @[Top.scala 65:20]
  wire [15:0] n1_O_5; // @[Top.scala 65:20]
  wire [15:0] n1_O_6; // @[Top.scala 65:20]
  wire [15:0] n1_O_7; // @[Top.scala 65:20]
  wire  n6_clock; // @[Top.scala 68:20]
  wire  n6_reset; // @[Top.scala 68:20]
  wire  n6_valid_up; // @[Top.scala 68:20]
  wire  n6_valid_down; // @[Top.scala 68:20]
  wire [15:0] n6_I_0; // @[Top.scala 68:20]
  wire [15:0] n6_I_1; // @[Top.scala 68:20]
  wire [15:0] n6_I_2; // @[Top.scala 68:20]
  wire [15:0] n6_I_3; // @[Top.scala 68:20]
  wire [15:0] n6_I_4; // @[Top.scala 68:20]
  wire [15:0] n6_I_5; // @[Top.scala 68:20]
  wire [15:0] n6_I_6; // @[Top.scala 68:20]
  wire [15:0] n6_I_7; // @[Top.scala 68:20]
  wire [15:0] n6_O_0; // @[Top.scala 68:20]
  wire [15:0] n6_O_1; // @[Top.scala 68:20]
  wire [15:0] n6_O_2; // @[Top.scala 68:20]
  wire [15:0] n6_O_3; // @[Top.scala 68:20]
  wire [15:0] n6_O_4; // @[Top.scala 68:20]
  wire [15:0] n6_O_5; // @[Top.scala 68:20]
  wire [15:0] n6_O_6; // @[Top.scala 68:20]
  wire [15:0] n6_O_7; // @[Top.scala 68:20]
  wire  n7_valid_up; // @[Top.scala 71:20]
  wire  n7_valid_down; // @[Top.scala 71:20]
  wire [15:0] n7_I_0; // @[Top.scala 71:20]
  wire [15:0] n7_I_1; // @[Top.scala 71:20]
  wire [15:0] n7_I_2; // @[Top.scala 71:20]
  wire [15:0] n7_I_3; // @[Top.scala 71:20]
  wire [15:0] n7_I_4; // @[Top.scala 71:20]
  wire [15:0] n7_I_5; // @[Top.scala 71:20]
  wire [15:0] n7_I_6; // @[Top.scala 71:20]
  wire [15:0] n7_I_7; // @[Top.scala 71:20]
  wire [15:0] n7_O_0; // @[Top.scala 71:20]
  wire [15:0] n7_O_1; // @[Top.scala 71:20]
  wire [15:0] n7_O_2; // @[Top.scala 71:20]
  wire [15:0] n7_O_3; // @[Top.scala 71:20]
  wire [15:0] n7_O_4; // @[Top.scala 71:20]
  wire [15:0] n7_O_5; // @[Top.scala 71:20]
  wire [15:0] n7_O_6; // @[Top.scala 71:20]
  wire [15:0] n7_O_7; // @[Top.scala 71:20]
  wire  n13_clock; // @[Top.scala 74:21]
  wire  n13_reset; // @[Top.scala 74:21]
  wire  n13_valid_up; // @[Top.scala 74:21]
  wire  n13_valid_down; // @[Top.scala 74:21]
  wire [15:0] n13_I_0; // @[Top.scala 74:21]
  wire [15:0] n13_I_1; // @[Top.scala 74:21]
  wire [15:0] n13_I_2; // @[Top.scala 74:21]
  wire [15:0] n13_I_3; // @[Top.scala 74:21]
  wire [15:0] n13_I_4; // @[Top.scala 74:21]
  wire [15:0] n13_I_5; // @[Top.scala 74:21]
  wire [15:0] n13_I_6; // @[Top.scala 74:21]
  wire [15:0] n13_I_7; // @[Top.scala 74:21]
  wire [15:0] n13_O_0; // @[Top.scala 74:21]
  wire [15:0] n13_O_1; // @[Top.scala 74:21]
  wire [15:0] n13_O_2; // @[Top.scala 74:21]
  wire [15:0] n13_O_3; // @[Top.scala 74:21]
  wire [15:0] n13_O_4; // @[Top.scala 74:21]
  wire [15:0] n13_O_5; // @[Top.scala 74:21]
  wire [15:0] n13_O_6; // @[Top.scala 74:21]
  wire [15:0] n13_O_7; // @[Top.scala 74:21]
  wire  n9_clock; // @[Top.scala 77:20]
  wire  n9_reset; // @[Top.scala 77:20]
  wire  n9_valid_up; // @[Top.scala 77:20]
  wire  n9_valid_down; // @[Top.scala 77:20]
  wire [15:0] n9_I_0; // @[Top.scala 77:20]
  wire [15:0] n9_I_1; // @[Top.scala 77:20]
  wire [15:0] n9_I_2; // @[Top.scala 77:20]
  wire [15:0] n9_I_3; // @[Top.scala 77:20]
  wire [15:0] n9_I_4; // @[Top.scala 77:20]
  wire [15:0] n9_I_5; // @[Top.scala 77:20]
  wire [15:0] n9_I_6; // @[Top.scala 77:20]
  wire [15:0] n9_I_7; // @[Top.scala 77:20]
  wire [15:0] n9_O_0; // @[Top.scala 77:20]
  wire [15:0] n9_O_1; // @[Top.scala 77:20]
  wire [15:0] n9_O_2; // @[Top.scala 77:20]
  wire [15:0] n9_O_3; // @[Top.scala 77:20]
  wire [15:0] n9_O_4; // @[Top.scala 77:20]
  wire [15:0] n9_O_5; // @[Top.scala 77:20]
  wire [15:0] n9_O_6; // @[Top.scala 77:20]
  wire [15:0] n9_O_7; // @[Top.scala 77:20]
  wire  n10_clock; // @[Top.scala 80:21]
  wire  n10_reset; // @[Top.scala 80:21]
  wire  n10_valid_up; // @[Top.scala 80:21]
  wire  n10_valid_down; // @[Top.scala 80:21]
  wire [15:0] n10_I_0; // @[Top.scala 80:21]
  wire [15:0] n10_I_1; // @[Top.scala 80:21]
  wire [15:0] n10_I_2; // @[Top.scala 80:21]
  wire [15:0] n10_I_3; // @[Top.scala 80:21]
  wire [15:0] n10_I_4; // @[Top.scala 80:21]
  wire [15:0] n10_I_5; // @[Top.scala 80:21]
  wire [15:0] n10_I_6; // @[Top.scala 80:21]
  wire [15:0] n10_I_7; // @[Top.scala 80:21]
  wire [15:0] n10_O_0; // @[Top.scala 80:21]
  wire [15:0] n10_O_1; // @[Top.scala 80:21]
  wire [15:0] n10_O_2; // @[Top.scala 80:21]
  wire [15:0] n10_O_3; // @[Top.scala 80:21]
  wire [15:0] n10_O_4; // @[Top.scala 80:21]
  wire [15:0] n10_O_5; // @[Top.scala 80:21]
  wire [15:0] n10_O_6; // @[Top.scala 80:21]
  wire [15:0] n10_O_7; // @[Top.scala 80:21]
  wire  n11_valid_up; // @[Top.scala 83:21]
  wire  n11_valid_down; // @[Top.scala 83:21]
  wire [15:0] n11_I_0; // @[Top.scala 83:21]
  wire [15:0] n11_I_1; // @[Top.scala 83:21]
  wire [15:0] n11_I_2; // @[Top.scala 83:21]
  wire [15:0] n11_I_3; // @[Top.scala 83:21]
  wire [15:0] n11_I_4; // @[Top.scala 83:21]
  wire [15:0] n11_I_5; // @[Top.scala 83:21]
  wire [15:0] n11_I_6; // @[Top.scala 83:21]
  wire [15:0] n11_I_7; // @[Top.scala 83:21]
  wire [15:0] n11_O_0; // @[Top.scala 83:21]
  wire [15:0] n11_O_1; // @[Top.scala 83:21]
  wire [15:0] n11_O_2; // @[Top.scala 83:21]
  wire [15:0] n11_O_3; // @[Top.scala 83:21]
  wire [15:0] n11_O_4; // @[Top.scala 83:21]
  wire [15:0] n11_O_5; // @[Top.scala 83:21]
  wire [15:0] n11_O_6; // @[Top.scala 83:21]
  wire [15:0] n11_O_7; // @[Top.scala 83:21]
  wire  n12_valid_up; // @[Top.scala 86:21]
  wire  n12_valid_down; // @[Top.scala 86:21]
  wire [15:0] n12_I_0; // @[Top.scala 86:21]
  wire [15:0] n12_I_1; // @[Top.scala 86:21]
  wire [15:0] n12_I_2; // @[Top.scala 86:21]
  wire [15:0] n12_I_3; // @[Top.scala 86:21]
  wire [15:0] n12_I_4; // @[Top.scala 86:21]
  wire [15:0] n12_I_5; // @[Top.scala 86:21]
  wire [15:0] n12_I_6; // @[Top.scala 86:21]
  wire [15:0] n12_I_7; // @[Top.scala 86:21]
  wire [15:0] n12_O_0; // @[Top.scala 86:21]
  wire [15:0] n12_O_1; // @[Top.scala 86:21]
  wire [15:0] n12_O_2; // @[Top.scala 86:21]
  wire [15:0] n12_O_3; // @[Top.scala 86:21]
  wire [15:0] n12_O_4; // @[Top.scala 86:21]
  wire [15:0] n12_O_5; // @[Top.scala 86:21]
  wire [15:0] n12_O_6; // @[Top.scala 86:21]
  wire [15:0] n12_O_7; // @[Top.scala 86:21]
  wire  n14_clock; // @[Top.scala 89:21]
  wire  n14_reset; // @[Top.scala 89:21]
  wire  n14_valid_up; // @[Top.scala 89:21]
  wire  n14_valid_down; // @[Top.scala 89:21]
  wire [15:0] n14_I0_0; // @[Top.scala 89:21]
  wire [15:0] n14_I0_1; // @[Top.scala 89:21]
  wire [15:0] n14_I0_2; // @[Top.scala 89:21]
  wire [15:0] n14_I0_3; // @[Top.scala 89:21]
  wire [15:0] n14_I0_4; // @[Top.scala 89:21]
  wire [15:0] n14_I0_5; // @[Top.scala 89:21]
  wire [15:0] n14_I0_6; // @[Top.scala 89:21]
  wire [15:0] n14_I0_7; // @[Top.scala 89:21]
  wire [15:0] n14_I1_0; // @[Top.scala 89:21]
  wire [15:0] n14_I1_1; // @[Top.scala 89:21]
  wire [15:0] n14_I1_2; // @[Top.scala 89:21]
  wire [15:0] n14_I1_3; // @[Top.scala 89:21]
  wire [15:0] n14_I1_4; // @[Top.scala 89:21]
  wire [15:0] n14_I1_5; // @[Top.scala 89:21]
  wire [15:0] n14_I1_6; // @[Top.scala 89:21]
  wire [15:0] n14_I1_7; // @[Top.scala 89:21]
  wire [15:0] n14_O_0; // @[Top.scala 89:21]
  wire [15:0] n14_O_1; // @[Top.scala 89:21]
  wire [15:0] n14_O_2; // @[Top.scala 89:21]
  wire [15:0] n14_O_3; // @[Top.scala 89:21]
  wire [15:0] n14_O_4; // @[Top.scala 89:21]
  wire [15:0] n14_O_5; // @[Top.scala 89:21]
  wire [15:0] n14_O_6; // @[Top.scala 89:21]
  wire [15:0] n14_O_7; // @[Top.scala 89:21]
  wire  n22_valid_up; // @[Top.scala 93:21]
  wire  n22_valid_down; // @[Top.scala 93:21]
  wire [15:0] n22_I_0; // @[Top.scala 93:21]
  wire [15:0] n22_I_1; // @[Top.scala 93:21]
  wire [15:0] n22_I_2; // @[Top.scala 93:21]
  wire [15:0] n22_I_3; // @[Top.scala 93:21]
  wire [15:0] n22_I_4; // @[Top.scala 93:21]
  wire [15:0] n22_I_5; // @[Top.scala 93:21]
  wire [15:0] n22_I_6; // @[Top.scala 93:21]
  wire [15:0] n22_I_7; // @[Top.scala 93:21]
  wire [15:0] n22_O_0; // @[Top.scala 93:21]
  wire [15:0] n22_O_1; // @[Top.scala 93:21]
  wire [15:0] n22_O_2; // @[Top.scala 93:21]
  wire [15:0] n22_O_3; // @[Top.scala 93:21]
  wire [15:0] n22_O_4; // @[Top.scala 93:21]
  wire [15:0] n22_O_5; // @[Top.scala 93:21]
  wire [15:0] n22_O_6; // @[Top.scala 93:21]
  wire [15:0] n22_O_7; // @[Top.scala 93:21]
  wire  n33_clock; // @[Top.scala 96:21]
  wire  n33_reset; // @[Top.scala 96:21]
  wire  n33_valid_up; // @[Top.scala 96:21]
  wire  n33_valid_down; // @[Top.scala 96:21]
  wire [15:0] n33_I_0; // @[Top.scala 96:21]
  wire [15:0] n33_I_1; // @[Top.scala 96:21]
  wire [15:0] n33_I_2; // @[Top.scala 96:21]
  wire [15:0] n33_I_3; // @[Top.scala 96:21]
  wire [15:0] n33_I_4; // @[Top.scala 96:21]
  wire [15:0] n33_I_5; // @[Top.scala 96:21]
  wire [15:0] n33_I_6; // @[Top.scala 96:21]
  wire [15:0] n33_I_7; // @[Top.scala 96:21]
  wire [15:0] n33_O; // @[Top.scala 96:21]
  wire  n34_clock; // @[Top.scala 99:21]
  wire  n34_reset; // @[Top.scala 99:21]
  wire  n34_valid_up; // @[Top.scala 99:21]
  wire  n34_valid_down; // @[Top.scala 99:21]
  wire [15:0] n34_I; // @[Top.scala 99:21]
  wire [15:0] n34_O; // @[Top.scala 99:21]
  wire  n35_clock; // @[Top.scala 102:21]
  wire  n35_reset; // @[Top.scala 102:21]
  wire  n35_valid_up; // @[Top.scala 102:21]
  wire  n35_valid_down; // @[Top.scala 102:21]
  wire [15:0] n35_I; // @[Top.scala 102:21]
  wire [15:0] n35_O; // @[Top.scala 102:21]
  wire  n36_clock; // @[Top.scala 105:21]
  wire  n36_reset; // @[Top.scala 105:21]
  wire  n36_valid_up; // @[Top.scala 105:21]
  wire  n36_valid_down; // @[Top.scala 105:21]
  wire [15:0] n36_I; // @[Top.scala 105:21]
  wire [15:0] n36_O; // @[Top.scala 105:21]
  FIFO n1 ( // @[Top.scala 65:20]
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
    .I_7(n1_I_7),
    .O_0(n1_O_0),
    .O_1(n1_O_1),
    .O_2(n1_O_2),
    .O_3(n1_O_3),
    .O_4(n1_O_4),
    .O_5(n1_O_5),
    .O_6(n1_O_6),
    .O_7(n1_O_7)
  );
  MapT n6 ( // @[Top.scala 68:20]
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
    .I_7(n6_I_7),
    .O_0(n6_O_0),
    .O_1(n6_O_1),
    .O_2(n6_O_2),
    .O_3(n6_O_3),
    .O_4(n6_O_4),
    .O_5(n6_O_5),
    .O_6(n6_O_6),
    .O_7(n6_O_7)
  );
  Passthrough n7 ( // @[Top.scala 71:20]
    .valid_up(n7_valid_up),
    .valid_down(n7_valid_down),
    .I_0(n7_I_0),
    .I_1(n7_I_1),
    .I_2(n7_I_2),
    .I_3(n7_I_3),
    .I_4(n7_I_4),
    .I_5(n7_I_5),
    .I_6(n7_I_6),
    .I_7(n7_I_7),
    .O_0(n7_O_0),
    .O_1(n7_O_1),
    .O_2(n7_O_2),
    .O_3(n7_O_3),
    .O_4(n7_O_4),
    .O_5(n7_O_5),
    .O_6(n7_O_6),
    .O_7(n7_O_7)
  );
  FIFO n13 ( // @[Top.scala 74:21]
    .clock(n13_clock),
    .reset(n13_reset),
    .valid_up(n13_valid_up),
    .valid_down(n13_valid_down),
    .I_0(n13_I_0),
    .I_1(n13_I_1),
    .I_2(n13_I_2),
    .I_3(n13_I_3),
    .I_4(n13_I_4),
    .I_5(n13_I_5),
    .I_6(n13_I_6),
    .I_7(n13_I_7),
    .O_0(n13_O_0),
    .O_1(n13_O_1),
    .O_2(n13_O_2),
    .O_3(n13_O_3),
    .O_4(n13_O_4),
    .O_5(n13_O_5),
    .O_6(n13_O_6),
    .O_7(n13_O_7)
  );
  FIFO n9 ( // @[Top.scala 77:20]
    .clock(n9_clock),
    .reset(n9_reset),
    .valid_up(n9_valid_up),
    .valid_down(n9_valid_down),
    .I_0(n9_I_0),
    .I_1(n9_I_1),
    .I_2(n9_I_2),
    .I_3(n9_I_3),
    .I_4(n9_I_4),
    .I_5(n9_I_5),
    .I_6(n9_I_6),
    .I_7(n9_I_7),
    .O_0(n9_O_0),
    .O_1(n9_O_1),
    .O_2(n9_O_2),
    .O_3(n9_O_3),
    .O_4(n9_O_4),
    .O_5(n9_O_5),
    .O_6(n9_O_6),
    .O_7(n9_O_7)
  );
  UpT_1 n10 ( // @[Top.scala 80:21]
    .clock(n10_clock),
    .reset(n10_reset),
    .valid_up(n10_valid_up),
    .valid_down(n10_valid_down),
    .I_0(n10_I_0),
    .I_1(n10_I_1),
    .I_2(n10_I_2),
    .I_3(n10_I_3),
    .I_4(n10_I_4),
    .I_5(n10_I_5),
    .I_6(n10_I_6),
    .I_7(n10_I_7),
    .O_0(n10_O_0),
    .O_1(n10_O_1),
    .O_2(n10_O_2),
    .O_3(n10_O_3),
    .O_4(n10_O_4),
    .O_5(n10_O_5),
    .O_6(n10_O_6),
    .O_7(n10_O_7)
  );
  Passthrough n11 ( // @[Top.scala 83:21]
    .valid_up(n11_valid_up),
    .valid_down(n11_valid_down),
    .I_0(n11_I_0),
    .I_1(n11_I_1),
    .I_2(n11_I_2),
    .I_3(n11_I_3),
    .I_4(n11_I_4),
    .I_5(n11_I_5),
    .I_6(n11_I_6),
    .I_7(n11_I_7),
    .O_0(n11_O_0),
    .O_1(n11_O_1),
    .O_2(n11_O_2),
    .O_3(n11_O_3),
    .O_4(n11_O_4),
    .O_5(n11_O_5),
    .O_6(n11_O_6),
    .O_7(n11_O_7)
  );
  Passthrough n12 ( // @[Top.scala 86:21]
    .valid_up(n12_valid_up),
    .valid_down(n12_valid_down),
    .I_0(n12_I_0),
    .I_1(n12_I_1),
    .I_2(n12_I_2),
    .I_3(n12_I_3),
    .I_4(n12_I_4),
    .I_5(n12_I_5),
    .I_6(n12_I_6),
    .I_7(n12_I_7),
    .O_0(n12_O_0),
    .O_1(n12_O_1),
    .O_2(n12_O_2),
    .O_3(n12_O_3),
    .O_4(n12_O_4),
    .O_5(n12_O_5),
    .O_6(n12_O_6),
    .O_7(n12_O_7)
  );
  Map2T n14 ( // @[Top.scala 89:21]
    .clock(n14_clock),
    .reset(n14_reset),
    .valid_up(n14_valid_up),
    .valid_down(n14_valid_down),
    .I0_0(n14_I0_0),
    .I0_1(n14_I0_1),
    .I0_2(n14_I0_2),
    .I0_3(n14_I0_3),
    .I0_4(n14_I0_4),
    .I0_5(n14_I0_5),
    .I0_6(n14_I0_6),
    .I0_7(n14_I0_7),
    .I1_0(n14_I1_0),
    .I1_1(n14_I1_1),
    .I1_2(n14_I1_2),
    .I1_3(n14_I1_3),
    .I1_4(n14_I1_4),
    .I1_5(n14_I1_5),
    .I1_6(n14_I1_6),
    .I1_7(n14_I1_7),
    .O_0(n14_O_0),
    .O_1(n14_O_1),
    .O_2(n14_O_2),
    .O_3(n14_O_3),
    .O_4(n14_O_4),
    .O_5(n14_O_5),
    .O_6(n14_O_6),
    .O_7(n14_O_7)
  );
  Passthrough n22 ( // @[Top.scala 93:21]
    .valid_up(n22_valid_up),
    .valid_down(n22_valid_down),
    .I_0(n22_I_0),
    .I_1(n22_I_1),
    .I_2(n22_I_2),
    .I_3(n22_I_3),
    .I_4(n22_I_4),
    .I_5(n22_I_5),
    .I_6(n22_I_6),
    .I_7(n22_I_7),
    .O_0(n22_O_0),
    .O_1(n22_O_1),
    .O_2(n22_O_2),
    .O_3(n22_O_3),
    .O_4(n22_O_4),
    .O_5(n22_O_5),
    .O_6(n22_O_6),
    .O_7(n22_O_7)
  );
  MapT_2 n33 ( // @[Top.scala 96:21]
    .clock(n33_clock),
    .reset(n33_reset),
    .valid_up(n33_valid_up),
    .valid_down(n33_valid_down),
    .I_0(n33_I_0),
    .I_1(n33_I_1),
    .I_2(n33_I_2),
    .I_3(n33_I_3),
    .I_4(n33_I_4),
    .I_5(n33_I_5),
    .I_6(n33_I_6),
    .I_7(n33_I_7),
    .O(n33_O)
  );
  FIFO_3 n34 ( // @[Top.scala 99:21]
    .clock(n34_clock),
    .reset(n34_reset),
    .valid_up(n34_valid_up),
    .valid_down(n34_valid_down),
    .I(n34_I),
    .O(n34_O)
  );
  FIFO_3 n35 ( // @[Top.scala 102:21]
    .clock(n35_clock),
    .reset(n35_reset),
    .valid_up(n35_valid_up),
    .valid_down(n35_valid_down),
    .I(n35_I),
    .O(n35_O)
  );
  FIFO_3 n36 ( // @[Top.scala 105:21]
    .clock(n36_clock),
    .reset(n36_reset),
    .valid_up(n36_valid_up),
    .valid_down(n36_valid_down),
    .I(n36_I),
    .O(n36_O)
  );
  assign valid_down = n36_valid_down; // @[Top.scala 109:16]
  assign O = n36_O; // @[Top.scala 108:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 67:17]
  assign n1_I_0 = I0_0; // @[Top.scala 66:10]
  assign n1_I_1 = I0_1; // @[Top.scala 66:10]
  assign n1_I_2 = I0_2; // @[Top.scala 66:10]
  assign n1_I_3 = I0_3; // @[Top.scala 66:10]
  assign n1_I_4 = I0_4; // @[Top.scala 66:10]
  assign n1_I_5 = I0_5; // @[Top.scala 66:10]
  assign n1_I_6 = I0_6; // @[Top.scala 66:10]
  assign n1_I_7 = I0_7; // @[Top.scala 66:10]
  assign n6_clock = clock;
  assign n6_reset = reset;
  assign n6_valid_up = n1_valid_down; // @[Top.scala 70:17]
  assign n6_I_0 = n1_O_0; // @[Top.scala 69:10]
  assign n6_I_1 = n1_O_1; // @[Top.scala 69:10]
  assign n6_I_2 = n1_O_2; // @[Top.scala 69:10]
  assign n6_I_3 = n1_O_3; // @[Top.scala 69:10]
  assign n6_I_4 = n1_O_4; // @[Top.scala 69:10]
  assign n6_I_5 = n1_O_5; // @[Top.scala 69:10]
  assign n6_I_6 = n1_O_6; // @[Top.scala 69:10]
  assign n6_I_7 = n1_O_7; // @[Top.scala 69:10]
  assign n7_valid_up = n6_valid_down; // @[Top.scala 73:17]
  assign n7_I_0 = n6_O_0; // @[Top.scala 72:10]
  assign n7_I_1 = n6_O_1; // @[Top.scala 72:10]
  assign n7_I_2 = n6_O_2; // @[Top.scala 72:10]
  assign n7_I_3 = n6_O_3; // @[Top.scala 72:10]
  assign n7_I_4 = n6_O_4; // @[Top.scala 72:10]
  assign n7_I_5 = n6_O_5; // @[Top.scala 72:10]
  assign n7_I_6 = n6_O_6; // @[Top.scala 72:10]
  assign n7_I_7 = n6_O_7; // @[Top.scala 72:10]
  assign n13_clock = clock;
  assign n13_reset = reset;
  assign n13_valid_up = n7_valid_down; // @[Top.scala 76:18]
  assign n13_I_0 = n7_O_0; // @[Top.scala 75:11]
  assign n13_I_1 = n7_O_1; // @[Top.scala 75:11]
  assign n13_I_2 = n7_O_2; // @[Top.scala 75:11]
  assign n13_I_3 = n7_O_3; // @[Top.scala 75:11]
  assign n13_I_4 = n7_O_4; // @[Top.scala 75:11]
  assign n13_I_5 = n7_O_5; // @[Top.scala 75:11]
  assign n13_I_6 = n7_O_6; // @[Top.scala 75:11]
  assign n13_I_7 = n7_O_7; // @[Top.scala 75:11]
  assign n9_clock = clock;
  assign n9_reset = reset;
  assign n9_valid_up = valid_up; // @[Top.scala 79:17]
  assign n9_I_0 = I1_0; // @[Top.scala 78:10]
  assign n9_I_1 = I1_1; // @[Top.scala 78:10]
  assign n9_I_2 = I1_2; // @[Top.scala 78:10]
  assign n9_I_3 = I1_3; // @[Top.scala 78:10]
  assign n9_I_4 = I1_4; // @[Top.scala 78:10]
  assign n9_I_5 = I1_5; // @[Top.scala 78:10]
  assign n9_I_6 = I1_6; // @[Top.scala 78:10]
  assign n9_I_7 = I1_7; // @[Top.scala 78:10]
  assign n10_clock = clock;
  assign n10_reset = reset;
  assign n10_valid_up = n9_valid_down; // @[Top.scala 82:18]
  assign n10_I_0 = n9_O_0; // @[Top.scala 81:11]
  assign n10_I_1 = n9_O_1; // @[Top.scala 81:11]
  assign n10_I_2 = n9_O_2; // @[Top.scala 81:11]
  assign n10_I_3 = n9_O_3; // @[Top.scala 81:11]
  assign n10_I_4 = n9_O_4; // @[Top.scala 81:11]
  assign n10_I_5 = n9_O_5; // @[Top.scala 81:11]
  assign n10_I_6 = n9_O_6; // @[Top.scala 81:11]
  assign n10_I_7 = n9_O_7; // @[Top.scala 81:11]
  assign n11_valid_up = n10_valid_down; // @[Top.scala 85:18]
  assign n11_I_0 = n10_O_0; // @[Top.scala 84:11]
  assign n11_I_1 = n10_O_1; // @[Top.scala 84:11]
  assign n11_I_2 = n10_O_2; // @[Top.scala 84:11]
  assign n11_I_3 = n10_O_3; // @[Top.scala 84:11]
  assign n11_I_4 = n10_O_4; // @[Top.scala 84:11]
  assign n11_I_5 = n10_O_5; // @[Top.scala 84:11]
  assign n11_I_6 = n10_O_6; // @[Top.scala 84:11]
  assign n11_I_7 = n10_O_7; // @[Top.scala 84:11]
  assign n12_valid_up = n11_valid_down; // @[Top.scala 88:18]
  assign n12_I_0 = n11_O_0; // @[Top.scala 87:11]
  assign n12_I_1 = n11_O_1; // @[Top.scala 87:11]
  assign n12_I_2 = n11_O_2; // @[Top.scala 87:11]
  assign n12_I_3 = n11_O_3; // @[Top.scala 87:11]
  assign n12_I_4 = n11_O_4; // @[Top.scala 87:11]
  assign n12_I_5 = n11_O_5; // @[Top.scala 87:11]
  assign n12_I_6 = n11_O_6; // @[Top.scala 87:11]
  assign n12_I_7 = n11_O_7; // @[Top.scala 87:11]
  assign n14_clock = clock;
  assign n14_reset = reset;
  assign n14_valid_up = n13_valid_down & n12_valid_down; // @[Top.scala 92:18]
  assign n14_I0_0 = n13_O_0; // @[Top.scala 90:12]
  assign n14_I0_1 = n13_O_1; // @[Top.scala 90:12]
  assign n14_I0_2 = n13_O_2; // @[Top.scala 90:12]
  assign n14_I0_3 = n13_O_3; // @[Top.scala 90:12]
  assign n14_I0_4 = n13_O_4; // @[Top.scala 90:12]
  assign n14_I0_5 = n13_O_5; // @[Top.scala 90:12]
  assign n14_I0_6 = n13_O_6; // @[Top.scala 90:12]
  assign n14_I0_7 = n13_O_7; // @[Top.scala 90:12]
  assign n14_I1_0 = n12_O_0; // @[Top.scala 91:12]
  assign n14_I1_1 = n12_O_1; // @[Top.scala 91:12]
  assign n14_I1_2 = n12_O_2; // @[Top.scala 91:12]
  assign n14_I1_3 = n12_O_3; // @[Top.scala 91:12]
  assign n14_I1_4 = n12_O_4; // @[Top.scala 91:12]
  assign n14_I1_5 = n12_O_5; // @[Top.scala 91:12]
  assign n14_I1_6 = n12_O_6; // @[Top.scala 91:12]
  assign n14_I1_7 = n12_O_7; // @[Top.scala 91:12]
  assign n22_valid_up = n14_valid_down; // @[Top.scala 95:18]
  assign n22_I_0 = n14_O_0; // @[Top.scala 94:11]
  assign n22_I_1 = n14_O_1; // @[Top.scala 94:11]
  assign n22_I_2 = n14_O_2; // @[Top.scala 94:11]
  assign n22_I_3 = n14_O_3; // @[Top.scala 94:11]
  assign n22_I_4 = n14_O_4; // @[Top.scala 94:11]
  assign n22_I_5 = n14_O_5; // @[Top.scala 94:11]
  assign n22_I_6 = n14_O_6; // @[Top.scala 94:11]
  assign n22_I_7 = n14_O_7; // @[Top.scala 94:11]
  assign n33_clock = clock;
  assign n33_reset = reset;
  assign n33_valid_up = n22_valid_down; // @[Top.scala 98:18]
  assign n33_I_0 = n22_O_0; // @[Top.scala 97:11]
  assign n33_I_1 = n22_O_1; // @[Top.scala 97:11]
  assign n33_I_2 = n22_O_2; // @[Top.scala 97:11]
  assign n33_I_3 = n22_O_3; // @[Top.scala 97:11]
  assign n33_I_4 = n22_O_4; // @[Top.scala 97:11]
  assign n33_I_5 = n22_O_5; // @[Top.scala 97:11]
  assign n33_I_6 = n22_O_6; // @[Top.scala 97:11]
  assign n33_I_7 = n22_O_7; // @[Top.scala 97:11]
  assign n34_clock = clock;
  assign n34_reset = reset;
  assign n34_valid_up = n33_valid_down; // @[Top.scala 101:18]
  assign n34_I = n33_O; // @[Top.scala 100:11]
  assign n35_clock = clock;
  assign n35_reset = reset;
  assign n35_valid_up = n34_valid_down; // @[Top.scala 104:18]
  assign n35_I = n34_O; // @[Top.scala 103:11]
  assign n36_clock = clock;
  assign n36_reset = reset;
  assign n36_valid_up = n35_valid_down; // @[Top.scala 107:18]
  assign n36_I = n35_O; // @[Top.scala 106:11]
endmodule
