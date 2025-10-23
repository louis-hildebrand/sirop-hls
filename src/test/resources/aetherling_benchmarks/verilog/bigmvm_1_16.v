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
  input  [15:0] I_8,
  input  [15:0] I_9,
  input  [15:0] I_10,
  input  [15:0] I_11,
  input  [15:0] I_12,
  input  [15:0] I_13,
  input  [15:0] I_14,
  input  [15:0] I_15,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4,
  output [15:0] O_5,
  output [15:0] O_6,
  output [15:0] O_7,
  output [15:0] O_8,
  output [15:0] O_9,
  output [15:0] O_10,
  output [15:0] O_11,
  output [15:0] O_12,
  output [15:0] O_13,
  output [15:0] O_14,
  output [15:0] O_15
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
  reg [15:0] _T__8; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_8;
  reg [15:0] _T__9; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_9;
  reg [15:0] _T__10; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_10;
  reg [15:0] _T__11; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_11;
  reg [15:0] _T__12; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_12;
  reg [15:0] _T__13; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_13;
  reg [15:0] _T__14; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_14;
  reg [15:0] _T__15; // @[FIFO.scala 13:26]
  reg [31:0] _RAND_15;
  reg  _T_1; // @[FIFO.scala 15:27]
  reg [31:0] _RAND_16;
  assign valid_down = _T_1; // @[FIFO.scala 16:16]
  assign O_0 = _T__0; // @[FIFO.scala 14:7]
  assign O_1 = _T__1; // @[FIFO.scala 14:7]
  assign O_2 = _T__2; // @[FIFO.scala 14:7]
  assign O_3 = _T__3; // @[FIFO.scala 14:7]
  assign O_4 = _T__4; // @[FIFO.scala 14:7]
  assign O_5 = _T__5; // @[FIFO.scala 14:7]
  assign O_6 = _T__6; // @[FIFO.scala 14:7]
  assign O_7 = _T__7; // @[FIFO.scala 14:7]
  assign O_8 = _T__8; // @[FIFO.scala 14:7]
  assign O_9 = _T__9; // @[FIFO.scala 14:7]
  assign O_10 = _T__10; // @[FIFO.scala 14:7]
  assign O_11 = _T__11; // @[FIFO.scala 14:7]
  assign O_12 = _T__12; // @[FIFO.scala 14:7]
  assign O_13 = _T__13; // @[FIFO.scala 14:7]
  assign O_14 = _T__14; // @[FIFO.scala 14:7]
  assign O_15 = _T__15; // @[FIFO.scala 14:7]
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
  _T__8 = _RAND_8[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_9 = {1{`RANDOM}};
  _T__9 = _RAND_9[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_10 = {1{`RANDOM}};
  _T__10 = _RAND_10[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_11 = {1{`RANDOM}};
  _T__11 = _RAND_11[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_12 = {1{`RANDOM}};
  _T__12 = _RAND_12[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_13 = {1{`RANDOM}};
  _T__13 = _RAND_13[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_14 = {1{`RANDOM}};
  _T__14 = _RAND_14[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_15 = {1{`RANDOM}};
  _T__15 = _RAND_15[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_16 = {1{`RANDOM}};
  _T_1 = _RAND_16[0:0];
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
    _T__8 <= I_8;
    _T__9 <= I_9;
    _T__10 <= I_10;
    _T__11 <= I_11;
    _T__12 <= I_12;
    _T__13 <= I_13;
    _T__14 <= I_14;
    _T__15 <= I_15;
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
  input  [15:0] I_8,
  input  [15:0] I_9,
  input  [15:0] I_10,
  input  [15:0] I_11,
  input  [15:0] I_12,
  input  [15:0] I_13,
  input  [15:0] I_14,
  input  [15:0] I_15,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4,
  output [15:0] O_5,
  output [15:0] O_6,
  output [15:0] O_7,
  output [15:0] O_8,
  output [15:0] O_9,
  output [15:0] O_10,
  output [15:0] O_11,
  output [15:0] O_12,
  output [15:0] O_13,
  output [15:0] O_14,
  output [15:0] O_15
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
  assign O_8 = I_8; // @[Passthrough.scala 17:68]
  assign O_9 = I_9; // @[Passthrough.scala 17:68]
  assign O_10 = I_10; // @[Passthrough.scala 17:68]
  assign O_11 = I_11; // @[Passthrough.scala 17:68]
  assign O_12 = I_12; // @[Passthrough.scala 17:68]
  assign O_13 = I_13; // @[Passthrough.scala 17:68]
  assign O_14 = I_14; // @[Passthrough.scala 17:68]
  assign O_15 = I_15; // @[Passthrough.scala 17:68]
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
  output [3:0] cur_valid
);
  wire  NestedCounters_CE; // @[NestedCounters.scala 20:44]
  wire  NestedCounters_valid; // @[NestedCounters.scala 20:44]
  wire  _T = CE & NestedCounters_valid; // @[NestedCounters.scala 25:49]
  reg [3:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire [3:0] _T_4 = value + 4'h1; // @[Counter.scala 39:22]
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
  output [15:0] RDATA_8,
  output [15:0] RDATA_9,
  output [15:0] RDATA_10,
  output [15:0] RDATA_11,
  output [15:0] RDATA_12,
  output [15:0] RDATA_13,
  output [15:0] RDATA_14,
  output [15:0] RDATA_15,
  input         WE,
  input  [15:0] WDATA_0,
  input  [15:0] WDATA_1,
  input  [15:0] WDATA_2,
  input  [15:0] WDATA_3,
  input  [15:0] WDATA_4,
  input  [15:0] WDATA_5,
  input  [15:0] WDATA_6,
  input  [15:0] WDATA_7,
  input  [15:0] WDATA_8,
  input  [15:0] WDATA_9,
  input  [15:0] WDATA_10,
  input  [15:0] WDATA_11,
  input  [15:0] WDATA_12,
  input  [15:0] WDATA_13,
  input  [15:0] WDATA_14,
  input  [15:0] WDATA_15
);
  wire  write_elem_counter_clock; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_reset; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_CE; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_valid; // @[RAM_ST.scala 20:34]
  wire [3:0] write_elem_counter_cur_valid; // @[RAM_ST.scala 20:34]
  wire  read_elem_counter_clock; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_reset; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_CE; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_valid; // @[RAM_ST.scala 21:33]
  wire [3:0] read_elem_counter_cur_valid; // @[RAM_ST.scala 21:33]
  reg [255:0] ram [0:15]; // @[RAM_ST.scala 29:24]
  reg [255:0] _RAND_0;
  wire [255:0] ram__T_23_data; // @[RAM_ST.scala 29:24]
  wire [3:0] ram__T_23_addr; // @[RAM_ST.scala 29:24]
  wire [255:0] ram__T_17_data; // @[RAM_ST.scala 29:24]
  wire [3:0] ram__T_17_addr; // @[RAM_ST.scala 29:24]
  wire  ram__T_17_mask; // @[RAM_ST.scala 29:24]
  wire  ram__T_17_en; // @[RAM_ST.scala 29:24]
  reg  ram__T_23_en_pipe_0;
  reg [31:0] _RAND_1;
  reg [3:0] ram__T_23_addr_pipe_0;
  reg [31:0] _RAND_2;
  wire [4:0] _T = {{1'd0}, write_elem_counter_cur_valid}; // @[RAM_ST.scala 31:71]
  wire [127:0] _T_8 = {WDATA_7,WDATA_6,WDATA_5,WDATA_4,WDATA_3,WDATA_2,WDATA_1,WDATA_0}; // @[RAM_ST.scala 31:115]
  wire [127:0] _T_15 = {WDATA_15,WDATA_14,WDATA_13,WDATA_12,WDATA_11,WDATA_10,WDATA_9,WDATA_8}; // @[RAM_ST.scala 31:115]
  wire [4:0] _T_18 = {{1'd0}, read_elem_counter_cur_valid}; // @[RAM_ST.scala 32:46]
  wire [255:0] _T_25 = ram__T_23_data;
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
  assign ram__T_23_addr = ram__T_23_addr_pipe_0;
  assign ram__T_23_data = ram[ram__T_23_addr]; // @[RAM_ST.scala 29:24]
  assign ram__T_17_data = {_T_15,_T_8};
  assign ram__T_17_addr = _T[3:0];
  assign ram__T_17_mask = 1'h1;
  assign ram__T_17_en = write_elem_counter_valid;
  assign RDATA_0 = _T_25[15:0]; // @[RAM_ST.scala 32:9]
  assign RDATA_1 = _T_25[31:16]; // @[RAM_ST.scala 32:9]
  assign RDATA_2 = _T_25[47:32]; // @[RAM_ST.scala 32:9]
  assign RDATA_3 = _T_25[63:48]; // @[RAM_ST.scala 32:9]
  assign RDATA_4 = _T_25[79:64]; // @[RAM_ST.scala 32:9]
  assign RDATA_5 = _T_25[95:80]; // @[RAM_ST.scala 32:9]
  assign RDATA_6 = _T_25[111:96]; // @[RAM_ST.scala 32:9]
  assign RDATA_7 = _T_25[127:112]; // @[RAM_ST.scala 32:9]
  assign RDATA_8 = _T_25[143:128]; // @[RAM_ST.scala 32:9]
  assign RDATA_9 = _T_25[159:144]; // @[RAM_ST.scala 32:9]
  assign RDATA_10 = _T_25[175:160]; // @[RAM_ST.scala 32:9]
  assign RDATA_11 = _T_25[191:176]; // @[RAM_ST.scala 32:9]
  assign RDATA_12 = _T_25[207:192]; // @[RAM_ST.scala 32:9]
  assign RDATA_13 = _T_25[223:208]; // @[RAM_ST.scala 32:9]
  assign RDATA_14 = _T_25[239:224]; // @[RAM_ST.scala 32:9]
  assign RDATA_15 = _T_25[255:240]; // @[RAM_ST.scala 32:9]
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
  _RAND_0 = {8{`RANDOM}};
  `ifdef RANDOMIZE_MEM_INIT
  for (initvar = 0; initvar < 16; initvar = initvar+1)
    ram[initvar] = _RAND_0[255:0];
  `endif // RANDOMIZE_MEM_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  ram__T_23_en_pipe_0 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  ram__T_23_addr_pipe_0 = _RAND_2[3:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if(ram__T_17_en & ram__T_17_mask) begin
      ram[ram__T_17_addr] <= ram__T_17_data; // @[RAM_ST.scala 29:24]
    end
    ram__T_23_en_pipe_0 <= read_elem_counter_valid;
    if (read_elem_counter_valid) begin
      ram__T_23_addr_pipe_0 <= _T_18[3:0];
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
  input  [15:0] I_8,
  input  [15:0] I_9,
  input  [15:0] I_10,
  input  [15:0] I_11,
  input  [15:0] I_12,
  input  [15:0] I_13,
  input  [15:0] I_14,
  input  [15:0] I_15,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4,
  output [15:0] O_5,
  output [15:0] O_6,
  output [15:0] O_7,
  output [15:0] O_8,
  output [15:0] O_9,
  output [15:0] O_10,
  output [15:0] O_11,
  output [15:0] O_12,
  output [15:0] O_13,
  output [15:0] O_14,
  output [15:0] O_15
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
  wire [15:0] mem_RDATA_8; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_9; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_10; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_11; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_12; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_13; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_14; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA_15; // @[Upsample.scala 31:19]
  wire  mem_WE; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_0; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_1; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_2; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_3; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_4; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_5; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_6; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_7; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_8; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_9; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_10; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_11; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_12; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_13; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_14; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA_15; // @[Upsample.scala 31:19]
  reg [3:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_1 = value == 4'hf; // @[Counter.scala 38:24]
  wire [3:0] _T_3 = value + 4'h1; // @[Counter.scala 39:22]
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
  wire [15:0] dataOut_8 = mem_RDATA_8; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_9 = mem_RDATA_9; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_10 = mem_RDATA_10; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_11 = mem_RDATA_11; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_12 = mem_RDATA_12; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_13 = mem_RDATA_13; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_14 = mem_RDATA_14; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  wire [15:0] dataOut_15 = mem_RDATA_15; // @[Upsample.scala 33:21 Upsample.scala 37:11]
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
    .RDATA_8(mem_RDATA_8),
    .RDATA_9(mem_RDATA_9),
    .RDATA_10(mem_RDATA_10),
    .RDATA_11(mem_RDATA_11),
    .RDATA_12(mem_RDATA_12),
    .RDATA_13(mem_RDATA_13),
    .RDATA_14(mem_RDATA_14),
    .RDATA_15(mem_RDATA_15),
    .WE(mem_WE),
    .WDATA_0(mem_WDATA_0),
    .WDATA_1(mem_WDATA_1),
    .WDATA_2(mem_WDATA_2),
    .WDATA_3(mem_WDATA_3),
    .WDATA_4(mem_WDATA_4),
    .WDATA_5(mem_WDATA_5),
    .WDATA_6(mem_WDATA_6),
    .WDATA_7(mem_WDATA_7),
    .WDATA_8(mem_WDATA_8),
    .WDATA_9(mem_WDATA_9),
    .WDATA_10(mem_WDATA_10),
    .WDATA_11(mem_WDATA_11),
    .WDATA_12(mem_WDATA_12),
    .WDATA_13(mem_WDATA_13),
    .WDATA_14(mem_WDATA_14),
    .WDATA_15(mem_WDATA_15)
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
  assign O_8 = _T_10 ? I_8 : dataOut_8; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_9 = _T_10 ? I_9 : dataOut_9; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_10 = _T_10 ? I_10 : dataOut_10; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_11 = _T_10 ? I_11 : dataOut_11; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_12 = _T_10 ? I_12 : dataOut_12; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_13 = _T_10 ? I_13 : dataOut_13; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_14 = _T_10 ? I_14 : dataOut_14; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign O_15 = _T_10 ? I_15 : dataOut_15; // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
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
  assign mem_WDATA_8 = I_8; // @[Upsample.scala 36:13]
  assign mem_WDATA_9 = I_9; // @[Upsample.scala 36:13]
  assign mem_WDATA_10 = I_10; // @[Upsample.scala 36:13]
  assign mem_WDATA_11 = I_11; // @[Upsample.scala 36:13]
  assign mem_WDATA_12 = I_12; // @[Upsample.scala 36:13]
  assign mem_WDATA_13 = I_13; // @[Upsample.scala 36:13]
  assign mem_WDATA_14 = I_14; // @[Upsample.scala 36:13]
  assign mem_WDATA_15 = I_15; // @[Upsample.scala 36:13]
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
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  value_1 = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 4'h0;
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
  input  [15:0] I0_4,
  input  [15:0] I0_5,
  input  [15:0] I0_6,
  input  [15:0] I0_7,
  input  [15:0] I0_8,
  input  [15:0] I0_9,
  input  [15:0] I0_10,
  input  [15:0] I0_11,
  input  [15:0] I0_12,
  input  [15:0] I0_13,
  input  [15:0] I0_14,
  input  [15:0] I0_15,
  input  [15:0] I1_0,
  input  [15:0] I1_1,
  input  [15:0] I1_2,
  input  [15:0] I1_3,
  input  [15:0] I1_4,
  input  [15:0] I1_5,
  input  [15:0] I1_6,
  input  [15:0] I1_7,
  input  [15:0] I1_8,
  input  [15:0] I1_9,
  input  [15:0] I1_10,
  input  [15:0] I1_11,
  input  [15:0] I1_12,
  input  [15:0] I1_13,
  input  [15:0] I1_14,
  input  [15:0] I1_15,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4,
  output [15:0] O_5,
  output [15:0] O_6,
  output [15:0] O_7,
  output [15:0] O_8,
  output [15:0] O_9,
  output [15:0] O_10,
  output [15:0] O_11,
  output [15:0] O_12,
  output [15:0] O_13,
  output [15:0] O_14,
  output [15:0] O_15
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
  wire  other_ops_7_clock; // @[Map2S.scala 10:86]
  wire  other_ops_7_reset; // @[Map2S.scala 10:86]
  wire  other_ops_7_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_7_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_7_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_7_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_7_O; // @[Map2S.scala 10:86]
  wire  other_ops_8_clock; // @[Map2S.scala 10:86]
  wire  other_ops_8_reset; // @[Map2S.scala 10:86]
  wire  other_ops_8_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_8_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_8_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_8_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_8_O; // @[Map2S.scala 10:86]
  wire  other_ops_9_clock; // @[Map2S.scala 10:86]
  wire  other_ops_9_reset; // @[Map2S.scala 10:86]
  wire  other_ops_9_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_9_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_9_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_9_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_9_O; // @[Map2S.scala 10:86]
  wire  other_ops_10_clock; // @[Map2S.scala 10:86]
  wire  other_ops_10_reset; // @[Map2S.scala 10:86]
  wire  other_ops_10_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_10_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_10_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_10_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_10_O; // @[Map2S.scala 10:86]
  wire  other_ops_11_clock; // @[Map2S.scala 10:86]
  wire  other_ops_11_reset; // @[Map2S.scala 10:86]
  wire  other_ops_11_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_11_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_11_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_11_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_11_O; // @[Map2S.scala 10:86]
  wire  other_ops_12_clock; // @[Map2S.scala 10:86]
  wire  other_ops_12_reset; // @[Map2S.scala 10:86]
  wire  other_ops_12_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_12_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_12_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_12_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_12_O; // @[Map2S.scala 10:86]
  wire  other_ops_13_clock; // @[Map2S.scala 10:86]
  wire  other_ops_13_reset; // @[Map2S.scala 10:86]
  wire  other_ops_13_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_13_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_13_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_13_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_13_O; // @[Map2S.scala 10:86]
  wire  other_ops_14_clock; // @[Map2S.scala 10:86]
  wire  other_ops_14_reset; // @[Map2S.scala 10:86]
  wire  other_ops_14_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_14_valid_down; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_14_I0; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_14_I1; // @[Map2S.scala 10:86]
  wire [15:0] other_ops_14_O; // @[Map2S.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[Map2S.scala 26:83]
  wire  _T_1 = _T & other_ops_1_valid_down; // @[Map2S.scala 26:83]
  wire  _T_2 = _T_1 & other_ops_2_valid_down; // @[Map2S.scala 26:83]
  wire  _T_3 = _T_2 & other_ops_3_valid_down; // @[Map2S.scala 26:83]
  wire  _T_4 = _T_3 & other_ops_4_valid_down; // @[Map2S.scala 26:83]
  wire  _T_5 = _T_4 & other_ops_5_valid_down; // @[Map2S.scala 26:83]
  wire  _T_6 = _T_5 & other_ops_6_valid_down; // @[Map2S.scala 26:83]
  wire  _T_7 = _T_6 & other_ops_7_valid_down; // @[Map2S.scala 26:83]
  wire  _T_8 = _T_7 & other_ops_8_valid_down; // @[Map2S.scala 26:83]
  wire  _T_9 = _T_8 & other_ops_9_valid_down; // @[Map2S.scala 26:83]
  wire  _T_10 = _T_9 & other_ops_10_valid_down; // @[Map2S.scala 26:83]
  wire  _T_11 = _T_10 & other_ops_11_valid_down; // @[Map2S.scala 26:83]
  wire  _T_12 = _T_11 & other_ops_12_valid_down; // @[Map2S.scala 26:83]
  wire  _T_13 = _T_12 & other_ops_13_valid_down; // @[Map2S.scala 26:83]
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
  Module_0 other_ops_3 ( // @[Map2S.scala 10:86]
    .clock(other_ops_3_clock),
    .reset(other_ops_3_reset),
    .valid_up(other_ops_3_valid_up),
    .valid_down(other_ops_3_valid_down),
    .I0(other_ops_3_I0),
    .I1(other_ops_3_I1),
    .O(other_ops_3_O)
  );
  Module_0 other_ops_4 ( // @[Map2S.scala 10:86]
    .clock(other_ops_4_clock),
    .reset(other_ops_4_reset),
    .valid_up(other_ops_4_valid_up),
    .valid_down(other_ops_4_valid_down),
    .I0(other_ops_4_I0),
    .I1(other_ops_4_I1),
    .O(other_ops_4_O)
  );
  Module_0 other_ops_5 ( // @[Map2S.scala 10:86]
    .clock(other_ops_5_clock),
    .reset(other_ops_5_reset),
    .valid_up(other_ops_5_valid_up),
    .valid_down(other_ops_5_valid_down),
    .I0(other_ops_5_I0),
    .I1(other_ops_5_I1),
    .O(other_ops_5_O)
  );
  Module_0 other_ops_6 ( // @[Map2S.scala 10:86]
    .clock(other_ops_6_clock),
    .reset(other_ops_6_reset),
    .valid_up(other_ops_6_valid_up),
    .valid_down(other_ops_6_valid_down),
    .I0(other_ops_6_I0),
    .I1(other_ops_6_I1),
    .O(other_ops_6_O)
  );
  Module_0 other_ops_7 ( // @[Map2S.scala 10:86]
    .clock(other_ops_7_clock),
    .reset(other_ops_7_reset),
    .valid_up(other_ops_7_valid_up),
    .valid_down(other_ops_7_valid_down),
    .I0(other_ops_7_I0),
    .I1(other_ops_7_I1),
    .O(other_ops_7_O)
  );
  Module_0 other_ops_8 ( // @[Map2S.scala 10:86]
    .clock(other_ops_8_clock),
    .reset(other_ops_8_reset),
    .valid_up(other_ops_8_valid_up),
    .valid_down(other_ops_8_valid_down),
    .I0(other_ops_8_I0),
    .I1(other_ops_8_I1),
    .O(other_ops_8_O)
  );
  Module_0 other_ops_9 ( // @[Map2S.scala 10:86]
    .clock(other_ops_9_clock),
    .reset(other_ops_9_reset),
    .valid_up(other_ops_9_valid_up),
    .valid_down(other_ops_9_valid_down),
    .I0(other_ops_9_I0),
    .I1(other_ops_9_I1),
    .O(other_ops_9_O)
  );
  Module_0 other_ops_10 ( // @[Map2S.scala 10:86]
    .clock(other_ops_10_clock),
    .reset(other_ops_10_reset),
    .valid_up(other_ops_10_valid_up),
    .valid_down(other_ops_10_valid_down),
    .I0(other_ops_10_I0),
    .I1(other_ops_10_I1),
    .O(other_ops_10_O)
  );
  Module_0 other_ops_11 ( // @[Map2S.scala 10:86]
    .clock(other_ops_11_clock),
    .reset(other_ops_11_reset),
    .valid_up(other_ops_11_valid_up),
    .valid_down(other_ops_11_valid_down),
    .I0(other_ops_11_I0),
    .I1(other_ops_11_I1),
    .O(other_ops_11_O)
  );
  Module_0 other_ops_12 ( // @[Map2S.scala 10:86]
    .clock(other_ops_12_clock),
    .reset(other_ops_12_reset),
    .valid_up(other_ops_12_valid_up),
    .valid_down(other_ops_12_valid_down),
    .I0(other_ops_12_I0),
    .I1(other_ops_12_I1),
    .O(other_ops_12_O)
  );
  Module_0 other_ops_13 ( // @[Map2S.scala 10:86]
    .clock(other_ops_13_clock),
    .reset(other_ops_13_reset),
    .valid_up(other_ops_13_valid_up),
    .valid_down(other_ops_13_valid_down),
    .I0(other_ops_13_I0),
    .I1(other_ops_13_I1),
    .O(other_ops_13_O)
  );
  Module_0 other_ops_14 ( // @[Map2S.scala 10:86]
    .clock(other_ops_14_clock),
    .reset(other_ops_14_reset),
    .valid_up(other_ops_14_valid_up),
    .valid_down(other_ops_14_valid_down),
    .I0(other_ops_14_I0),
    .I1(other_ops_14_I1),
    .O(other_ops_14_O)
  );
  assign valid_down = _T_13 & other_ops_14_valid_down; // @[Map2S.scala 26:14]
  assign O_0 = fst_op_O; // @[Map2S.scala 19:8]
  assign O_1 = other_ops_0_O; // @[Map2S.scala 24:12]
  assign O_2 = other_ops_1_O; // @[Map2S.scala 24:12]
  assign O_3 = other_ops_2_O; // @[Map2S.scala 24:12]
  assign O_4 = other_ops_3_O; // @[Map2S.scala 24:12]
  assign O_5 = other_ops_4_O; // @[Map2S.scala 24:12]
  assign O_6 = other_ops_5_O; // @[Map2S.scala 24:12]
  assign O_7 = other_ops_6_O; // @[Map2S.scala 24:12]
  assign O_8 = other_ops_7_O; // @[Map2S.scala 24:12]
  assign O_9 = other_ops_8_O; // @[Map2S.scala 24:12]
  assign O_10 = other_ops_9_O; // @[Map2S.scala 24:12]
  assign O_11 = other_ops_10_O; // @[Map2S.scala 24:12]
  assign O_12 = other_ops_11_O; // @[Map2S.scala 24:12]
  assign O_13 = other_ops_12_O; // @[Map2S.scala 24:12]
  assign O_14 = other_ops_13_O; // @[Map2S.scala 24:12]
  assign O_15 = other_ops_14_O; // @[Map2S.scala 24:12]
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
  assign other_ops_7_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_7_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_7_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_7_I0 = I0_8; // @[Map2S.scala 22:43]
  assign other_ops_7_I1 = I1_8; // @[Map2S.scala 23:43]
  assign other_ops_8_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_8_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_8_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_8_I0 = I0_9; // @[Map2S.scala 22:43]
  assign other_ops_8_I1 = I1_9; // @[Map2S.scala 23:43]
  assign other_ops_9_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_9_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_9_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_9_I0 = I0_10; // @[Map2S.scala 22:43]
  assign other_ops_9_I1 = I1_10; // @[Map2S.scala 23:43]
  assign other_ops_10_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_10_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_10_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_10_I0 = I0_11; // @[Map2S.scala 22:43]
  assign other_ops_10_I1 = I1_11; // @[Map2S.scala 23:43]
  assign other_ops_11_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_11_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_11_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_11_I0 = I0_12; // @[Map2S.scala 22:43]
  assign other_ops_11_I1 = I1_12; // @[Map2S.scala 23:43]
  assign other_ops_12_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_12_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_12_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_12_I0 = I0_13; // @[Map2S.scala 22:43]
  assign other_ops_12_I1 = I1_13; // @[Map2S.scala 23:43]
  assign other_ops_13_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_13_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_13_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_13_I0 = I0_14; // @[Map2S.scala 22:43]
  assign other_ops_13_I1 = I1_14; // @[Map2S.scala 23:43]
  assign other_ops_14_clock = clock; // @[Map2S.scala 10:86]
  assign other_ops_14_reset = reset; // @[Map2S.scala 10:86]
  assign other_ops_14_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_14_I0 = I0_15; // @[Map2S.scala 22:43]
  assign other_ops_14_I1 = I1_15; // @[Map2S.scala 23:43]
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
  input  [15:0] I0_8,
  input  [15:0] I0_9,
  input  [15:0] I0_10,
  input  [15:0] I0_11,
  input  [15:0] I0_12,
  input  [15:0] I0_13,
  input  [15:0] I0_14,
  input  [15:0] I0_15,
  input  [15:0] I1_0,
  input  [15:0] I1_1,
  input  [15:0] I1_2,
  input  [15:0] I1_3,
  input  [15:0] I1_4,
  input  [15:0] I1_5,
  input  [15:0] I1_6,
  input  [15:0] I1_7,
  input  [15:0] I1_8,
  input  [15:0] I1_9,
  input  [15:0] I1_10,
  input  [15:0] I1_11,
  input  [15:0] I1_12,
  input  [15:0] I1_13,
  input  [15:0] I1_14,
  input  [15:0] I1_15,
  output [15:0] O_0,
  output [15:0] O_1,
  output [15:0] O_2,
  output [15:0] O_3,
  output [15:0] O_4,
  output [15:0] O_5,
  output [15:0] O_6,
  output [15:0] O_7,
  output [15:0] O_8,
  output [15:0] O_9,
  output [15:0] O_10,
  output [15:0] O_11,
  output [15:0] O_12,
  output [15:0] O_13,
  output [15:0] O_14,
  output [15:0] O_15
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
  wire [15:0] op_I0_8; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_9; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_10; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_11; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_12; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_13; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_14; // @[Map2T.scala 8:20]
  wire [15:0] op_I0_15; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_0; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_1; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_2; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_3; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_4; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_5; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_6; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_7; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_8; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_9; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_10; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_11; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_12; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_13; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_14; // @[Map2T.scala 8:20]
  wire [15:0] op_I1_15; // @[Map2T.scala 8:20]
  wire [15:0] op_O_0; // @[Map2T.scala 8:20]
  wire [15:0] op_O_1; // @[Map2T.scala 8:20]
  wire [15:0] op_O_2; // @[Map2T.scala 8:20]
  wire [15:0] op_O_3; // @[Map2T.scala 8:20]
  wire [15:0] op_O_4; // @[Map2T.scala 8:20]
  wire [15:0] op_O_5; // @[Map2T.scala 8:20]
  wire [15:0] op_O_6; // @[Map2T.scala 8:20]
  wire [15:0] op_O_7; // @[Map2T.scala 8:20]
  wire [15:0] op_O_8; // @[Map2T.scala 8:20]
  wire [15:0] op_O_9; // @[Map2T.scala 8:20]
  wire [15:0] op_O_10; // @[Map2T.scala 8:20]
  wire [15:0] op_O_11; // @[Map2T.scala 8:20]
  wire [15:0] op_O_12; // @[Map2T.scala 8:20]
  wire [15:0] op_O_13; // @[Map2T.scala 8:20]
  wire [15:0] op_O_14; // @[Map2T.scala 8:20]
  wire [15:0] op_O_15; // @[Map2T.scala 8:20]
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
    .I0_8(op_I0_8),
    .I0_9(op_I0_9),
    .I0_10(op_I0_10),
    .I0_11(op_I0_11),
    .I0_12(op_I0_12),
    .I0_13(op_I0_13),
    .I0_14(op_I0_14),
    .I0_15(op_I0_15),
    .I1_0(op_I1_0),
    .I1_1(op_I1_1),
    .I1_2(op_I1_2),
    .I1_3(op_I1_3),
    .I1_4(op_I1_4),
    .I1_5(op_I1_5),
    .I1_6(op_I1_6),
    .I1_7(op_I1_7),
    .I1_8(op_I1_8),
    .I1_9(op_I1_9),
    .I1_10(op_I1_10),
    .I1_11(op_I1_11),
    .I1_12(op_I1_12),
    .I1_13(op_I1_13),
    .I1_14(op_I1_14),
    .I1_15(op_I1_15),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2),
    .O_3(op_O_3),
    .O_4(op_O_4),
    .O_5(op_O_5),
    .O_6(op_O_6),
    .O_7(op_O_7),
    .O_8(op_O_8),
    .O_9(op_O_9),
    .O_10(op_O_10),
    .O_11(op_O_11),
    .O_12(op_O_12),
    .O_13(op_O_13),
    .O_14(op_O_14),
    .O_15(op_O_15)
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
  assign O_8 = op_O_8; // @[Map2T.scala 17:7]
  assign O_9 = op_O_9; // @[Map2T.scala 17:7]
  assign O_10 = op_O_10; // @[Map2T.scala 17:7]
  assign O_11 = op_O_11; // @[Map2T.scala 17:7]
  assign O_12 = op_O_12; // @[Map2T.scala 17:7]
  assign O_13 = op_O_13; // @[Map2T.scala 17:7]
  assign O_14 = op_O_14; // @[Map2T.scala 17:7]
  assign O_15 = op_O_15; // @[Map2T.scala 17:7]
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
  assign op_I0_8 = I0_8; // @[Map2T.scala 15:11]
  assign op_I0_9 = I0_9; // @[Map2T.scala 15:11]
  assign op_I0_10 = I0_10; // @[Map2T.scala 15:11]
  assign op_I0_11 = I0_11; // @[Map2T.scala 15:11]
  assign op_I0_12 = I0_12; // @[Map2T.scala 15:11]
  assign op_I0_13 = I0_13; // @[Map2T.scala 15:11]
  assign op_I0_14 = I0_14; // @[Map2T.scala 15:11]
  assign op_I0_15 = I0_15; // @[Map2T.scala 15:11]
  assign op_I1_0 = I1_0; // @[Map2T.scala 16:11]
  assign op_I1_1 = I1_1; // @[Map2T.scala 16:11]
  assign op_I1_2 = I1_2; // @[Map2T.scala 16:11]
  assign op_I1_3 = I1_3; // @[Map2T.scala 16:11]
  assign op_I1_4 = I1_4; // @[Map2T.scala 16:11]
  assign op_I1_5 = I1_5; // @[Map2T.scala 16:11]
  assign op_I1_6 = I1_6; // @[Map2T.scala 16:11]
  assign op_I1_7 = I1_7; // @[Map2T.scala 16:11]
  assign op_I1_8 = I1_8; // @[Map2T.scala 16:11]
  assign op_I1_9 = I1_9; // @[Map2T.scala 16:11]
  assign op_I1_10 = I1_10; // @[Map2T.scala 16:11]
  assign op_I1_11 = I1_11; // @[Map2T.scala 16:11]
  assign op_I1_12 = I1_12; // @[Map2T.scala 16:11]
  assign op_I1_13 = I1_13; // @[Map2T.scala 16:11]
  assign op_I1_14 = I1_14; // @[Map2T.scala 16:11]
  assign op_I1_15 = I1_15; // @[Map2T.scala 16:11]
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
  input  [15:0] I_8,
  input  [15:0] I_9,
  input  [15:0] I_10,
  input  [15:0] I_11,
  input  [15:0] I_12,
  input  [15:0] I_13,
  input  [15:0] I_14,
  input  [15:0] I_15,
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
  wire [15:0] AddNoValid_7_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_7_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_7_O; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_8_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_8_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_8_O; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_9_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_9_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_9_O; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_10_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_10_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_10_O; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_11_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_11_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_11_O; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_12_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_12_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_12_O; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_13_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_13_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_13_O; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_14_I_t0b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_14_I_t1b; // @[ReduceS.scala 20:43]
  wire [15:0] AddNoValid_14_O; // @[ReduceS.scala 20:43]
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
  reg [15:0] _T_9; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_9;
  reg [15:0] _T_10; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_10;
  reg [15:0] _T_11; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_11;
  reg [15:0] _T_12; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_12;
  reg [15:0] _T_13; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_13;
  reg [15:0] _T_14; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_14;
  reg [15:0] _T_15; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_15;
  reg [15:0] _T_16; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_16;
  reg  _T_17; // @[ReduceS.scala 47:32]
  reg [31:0] _RAND_17;
  reg  _T_18; // @[ReduceS.scala 47:24]
  reg [31:0] _RAND_18;
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
  AddNoValid AddNoValid_7 ( // @[ReduceS.scala 20:43]
    .I_t0b(AddNoValid_7_I_t0b),
    .I_t1b(AddNoValid_7_I_t1b),
    .O(AddNoValid_7_O)
  );
  AddNoValid AddNoValid_8 ( // @[ReduceS.scala 20:43]
    .I_t0b(AddNoValid_8_I_t0b),
    .I_t1b(AddNoValid_8_I_t1b),
    .O(AddNoValid_8_O)
  );
  AddNoValid AddNoValid_9 ( // @[ReduceS.scala 20:43]
    .I_t0b(AddNoValid_9_I_t0b),
    .I_t1b(AddNoValid_9_I_t1b),
    .O(AddNoValid_9_O)
  );
  AddNoValid AddNoValid_10 ( // @[ReduceS.scala 20:43]
    .I_t0b(AddNoValid_10_I_t0b),
    .I_t1b(AddNoValid_10_I_t1b),
    .O(AddNoValid_10_O)
  );
  AddNoValid AddNoValid_11 ( // @[ReduceS.scala 20:43]
    .I_t0b(AddNoValid_11_I_t0b),
    .I_t1b(AddNoValid_11_I_t1b),
    .O(AddNoValid_11_O)
  );
  AddNoValid AddNoValid_12 ( // @[ReduceS.scala 20:43]
    .I_t0b(AddNoValid_12_I_t0b),
    .I_t1b(AddNoValid_12_I_t1b),
    .O(AddNoValid_12_O)
  );
  AddNoValid AddNoValid_13 ( // @[ReduceS.scala 20:43]
    .I_t0b(AddNoValid_13_I_t0b),
    .I_t1b(AddNoValid_13_I_t1b),
    .O(AddNoValid_13_O)
  );
  AddNoValid AddNoValid_14 ( // @[ReduceS.scala 20:43]
    .I_t0b(AddNoValid_14_I_t0b),
    .I_t1b(AddNoValid_14_I_t1b),
    .O(AddNoValid_14_O)
  );
  assign valid_down = _T_18; // @[ReduceS.scala 47:14]
  assign O_0 = _T; // @[ReduceS.scala 27:14]
  assign AddNoValid_I_t0b = _T_11; // @[ReduceS.scala 43:18]
  assign AddNoValid_I_t1b = AddNoValid_1_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_1_I_t0b = AddNoValid_2_O; // @[ReduceS.scala 31:18]
  assign AddNoValid_1_I_t1b = AddNoValid_3_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_2_I_t0b = _T_15; // @[ReduceS.scala 43:18]
  assign AddNoValid_2_I_t1b = AddNoValid_5_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_3_I_t0b = AddNoValid_4_O; // @[ReduceS.scala 31:18]
  assign AddNoValid_3_I_t1b = AddNoValid_7_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_4_I_t0b = _T_8; // @[ReduceS.scala 43:18]
  assign AddNoValid_4_I_t1b = AddNoValid_9_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_5_I_t0b = AddNoValid_6_O; // @[ReduceS.scala 31:18]
  assign AddNoValid_5_I_t1b = AddNoValid_11_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_6_I_t0b = _T_3; // @[ReduceS.scala 43:18]
  assign AddNoValid_6_I_t1b = AddNoValid_13_O; // @[ReduceS.scala 36:18]
  assign AddNoValid_7_I_t0b = AddNoValid_8_O; // @[ReduceS.scala 31:18]
  assign AddNoValid_7_I_t1b = _T_9; // @[ReduceS.scala 43:18]
  assign AddNoValid_8_I_t0b = _T_12; // @[ReduceS.scala 43:18]
  assign AddNoValid_8_I_t1b = _T_16; // @[ReduceS.scala 43:18]
  assign AddNoValid_9_I_t0b = AddNoValid_10_O; // @[ReduceS.scala 31:18]
  assign AddNoValid_9_I_t1b = _T_6; // @[ReduceS.scala 43:18]
  assign AddNoValid_10_I_t0b = _T_10; // @[ReduceS.scala 43:18]
  assign AddNoValid_10_I_t1b = _T_2; // @[ReduceS.scala 43:18]
  assign AddNoValid_11_I_t0b = AddNoValid_12_O; // @[ReduceS.scala 31:18]
  assign AddNoValid_11_I_t1b = _T_7; // @[ReduceS.scala 43:18]
  assign AddNoValid_12_I_t0b = _T_14; // @[ReduceS.scala 43:18]
  assign AddNoValid_12_I_t1b = _T_13; // @[ReduceS.scala 43:18]
  assign AddNoValid_13_I_t0b = AddNoValid_14_O; // @[ReduceS.scala 31:18]
  assign AddNoValid_13_I_t1b = _T_5; // @[ReduceS.scala 43:18]
  assign AddNoValid_14_I_t0b = _T_4; // @[ReduceS.scala 43:18]
  assign AddNoValid_14_I_t1b = _T_1; // @[ReduceS.scala 43:18]
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
  _T_9 = _RAND_9[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_10 = {1{`RANDOM}};
  _T_10 = _RAND_10[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_11 = {1{`RANDOM}};
  _T_11 = _RAND_11[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_12 = {1{`RANDOM}};
  _T_12 = _RAND_12[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_13 = {1{`RANDOM}};
  _T_13 = _RAND_13[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_14 = {1{`RANDOM}};
  _T_14 = _RAND_14[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_15 = {1{`RANDOM}};
  _T_15 = _RAND_15[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_16 = {1{`RANDOM}};
  _T_16 = _RAND_16[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_17 = {1{`RANDOM}};
  _T_17 = _RAND_17[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_18 = {1{`RANDOM}};
  _T_18 = _RAND_18[0:0];
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
    _T_9 <= I_8;
    _T_10 <= I_9;
    _T_11 <= I_10;
    _T_12 <= I_11;
    _T_13 <= I_12;
    _T_14 <= I_13;
    _T_15 <= I_14;
    _T_16 <= I_15;
    if (reset) begin
      _T_17 <= 1'h0;
    end else begin
      _T_17 <= valid_up;
    end
    _T_18 <= _T_17;
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
  input  [15:0] I_4,
  input  [15:0] I_5,
  input  [15:0] I_6,
  input  [15:0] I_7,
  input  [15:0] I_8,
  input  [15:0] I_9,
  input  [15:0] I_10,
  input  [15:0] I_11,
  input  [15:0] I_12,
  input  [15:0] I_13,
  input  [15:0] I_14,
  input  [15:0] I_15,
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
  wire [15:0] op_I_8; // @[MapT.scala 8:20]
  wire [15:0] op_I_9; // @[MapT.scala 8:20]
  wire [15:0] op_I_10; // @[MapT.scala 8:20]
  wire [15:0] op_I_11; // @[MapT.scala 8:20]
  wire [15:0] op_I_12; // @[MapT.scala 8:20]
  wire [15:0] op_I_13; // @[MapT.scala 8:20]
  wire [15:0] op_I_14; // @[MapT.scala 8:20]
  wire [15:0] op_I_15; // @[MapT.scala 8:20]
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
    .I_8(op_I_8),
    .I_9(op_I_9),
    .I_10(op_I_10),
    .I_11(op_I_11),
    .I_12(op_I_12),
    .I_13(op_I_13),
    .I_14(op_I_14),
    .I_15(op_I_15),
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
  assign op_I_8 = I_8; // @[MapT.scala 14:10]
  assign op_I_9 = I_9; // @[MapT.scala 14:10]
  assign op_I_10 = I_10; // @[MapT.scala 14:10]
  assign op_I_11 = I_11; // @[MapT.scala 14:10]
  assign op_I_12 = I_12; // @[MapT.scala 14:10]
  assign op_I_13 = I_13; // @[MapT.scala 14:10]
  assign op_I_14 = I_14; // @[MapT.scala 14:10]
  assign op_I_15 = I_15; // @[MapT.scala 14:10]
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
  reg [3:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire  _T_3 = value == 4'hf; // @[Counter.scala 38:24]
  wire [3:0] _T_5 = value + 4'h1; // @[Counter.scala 39:22]
  wire  _T_6 = value == 4'h0; // @[ReduceT.scala 34:60]
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
  value = _RAND_1[3:0];
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
      value <= 4'h0;
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
  input  [15:0] I_4,
  input  [15:0] I_5,
  input  [15:0] I_6,
  input  [15:0] I_7,
  input  [15:0] I_8,
  input  [15:0] I_9,
  input  [15:0] I_10,
  input  [15:0] I_11,
  input  [15:0] I_12,
  input  [15:0] I_13,
  input  [15:0] I_14,
  input  [15:0] I_15,
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
  wire [15:0] n21_I_4; // @[Top.scala 32:21]
  wire [15:0] n21_I_5; // @[Top.scala 32:21]
  wire [15:0] n21_I_6; // @[Top.scala 32:21]
  wire [15:0] n21_I_7; // @[Top.scala 32:21]
  wire [15:0] n21_I_8; // @[Top.scala 32:21]
  wire [15:0] n21_I_9; // @[Top.scala 32:21]
  wire [15:0] n21_I_10; // @[Top.scala 32:21]
  wire [15:0] n21_I_11; // @[Top.scala 32:21]
  wire [15:0] n21_I_12; // @[Top.scala 32:21]
  wire [15:0] n21_I_13; // @[Top.scala 32:21]
  wire [15:0] n21_I_14; // @[Top.scala 32:21]
  wire [15:0] n21_I_15; // @[Top.scala 32:21]
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
    .I_4(n21_I_4),
    .I_5(n21_I_5),
    .I_6(n21_I_6),
    .I_7(n21_I_7),
    .I_8(n21_I_8),
    .I_9(n21_I_9),
    .I_10(n21_I_10),
    .I_11(n21_I_11),
    .I_12(n21_I_12),
    .I_13(n21_I_13),
    .I_14(n21_I_14),
    .I_15(n21_I_15),
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
  assign n21_I_4 = I_4; // @[Top.scala 33:11]
  assign n21_I_5 = I_5; // @[Top.scala 33:11]
  assign n21_I_6 = I_6; // @[Top.scala 33:11]
  assign n21_I_7 = I_7; // @[Top.scala 33:11]
  assign n21_I_8 = I_8; // @[Top.scala 33:11]
  assign n21_I_9 = I_9; // @[Top.scala 33:11]
  assign n21_I_10 = I_10; // @[Top.scala 33:11]
  assign n21_I_11 = I_11; // @[Top.scala 33:11]
  assign n21_I_12 = I_12; // @[Top.scala 33:11]
  assign n21_I_13 = I_13; // @[Top.scala 33:11]
  assign n21_I_14 = I_14; // @[Top.scala 33:11]
  assign n21_I_15 = I_15; // @[Top.scala 33:11]
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
  input  [15:0] I_4,
  input  [15:0] I_5,
  input  [15:0] I_6,
  input  [15:0] I_7,
  input  [15:0] I_8,
  input  [15:0] I_9,
  input  [15:0] I_10,
  input  [15:0] I_11,
  input  [15:0] I_12,
  input  [15:0] I_13,
  input  [15:0] I_14,
  input  [15:0] I_15,
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
  wire [15:0] op_I_8; // @[MapT.scala 8:20]
  wire [15:0] op_I_9; // @[MapT.scala 8:20]
  wire [15:0] op_I_10; // @[MapT.scala 8:20]
  wire [15:0] op_I_11; // @[MapT.scala 8:20]
  wire [15:0] op_I_12; // @[MapT.scala 8:20]
  wire [15:0] op_I_13; // @[MapT.scala 8:20]
  wire [15:0] op_I_14; // @[MapT.scala 8:20]
  wire [15:0] op_I_15; // @[MapT.scala 8:20]
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
    .I_4(op_I_4),
    .I_5(op_I_5),
    .I_6(op_I_6),
    .I_7(op_I_7),
    .I_8(op_I_8),
    .I_9(op_I_9),
    .I_10(op_I_10),
    .I_11(op_I_11),
    .I_12(op_I_12),
    .I_13(op_I_13),
    .I_14(op_I_14),
    .I_15(op_I_15),
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
  assign op_I_8 = I_8; // @[MapT.scala 14:10]
  assign op_I_9 = I_9; // @[MapT.scala 14:10]
  assign op_I_10 = I_10; // @[MapT.scala 14:10]
  assign op_I_11 = I_11; // @[MapT.scala 14:10]
  assign op_I_12 = I_12; // @[MapT.scala 14:10]
  assign op_I_13 = I_13; // @[MapT.scala 14:10]
  assign op_I_14 = I_14; // @[MapT.scala 14:10]
  assign op_I_15 = I_15; // @[MapT.scala 14:10]
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
  input  [15:0] I0_4,
  input  [15:0] I0_5,
  input  [15:0] I0_6,
  input  [15:0] I0_7,
  input  [15:0] I0_8,
  input  [15:0] I0_9,
  input  [15:0] I0_10,
  input  [15:0] I0_11,
  input  [15:0] I0_12,
  input  [15:0] I0_13,
  input  [15:0] I0_14,
  input  [15:0] I0_15,
  input  [15:0] I1_0,
  input  [15:0] I1_1,
  input  [15:0] I1_2,
  input  [15:0] I1_3,
  input  [15:0] I1_4,
  input  [15:0] I1_5,
  input  [15:0] I1_6,
  input  [15:0] I1_7,
  input  [15:0] I1_8,
  input  [15:0] I1_9,
  input  [15:0] I1_10,
  input  [15:0] I1_11,
  input  [15:0] I1_12,
  input  [15:0] I1_13,
  input  [15:0] I1_14,
  input  [15:0] I1_15,
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
  wire [15:0] n1_I_4; // @[Top.scala 49:20]
  wire [15:0] n1_I_5; // @[Top.scala 49:20]
  wire [15:0] n1_I_6; // @[Top.scala 49:20]
  wire [15:0] n1_I_7; // @[Top.scala 49:20]
  wire [15:0] n1_I_8; // @[Top.scala 49:20]
  wire [15:0] n1_I_9; // @[Top.scala 49:20]
  wire [15:0] n1_I_10; // @[Top.scala 49:20]
  wire [15:0] n1_I_11; // @[Top.scala 49:20]
  wire [15:0] n1_I_12; // @[Top.scala 49:20]
  wire [15:0] n1_I_13; // @[Top.scala 49:20]
  wire [15:0] n1_I_14; // @[Top.scala 49:20]
  wire [15:0] n1_I_15; // @[Top.scala 49:20]
  wire [15:0] n1_O_0; // @[Top.scala 49:20]
  wire [15:0] n1_O_1; // @[Top.scala 49:20]
  wire [15:0] n1_O_2; // @[Top.scala 49:20]
  wire [15:0] n1_O_3; // @[Top.scala 49:20]
  wire [15:0] n1_O_4; // @[Top.scala 49:20]
  wire [15:0] n1_O_5; // @[Top.scala 49:20]
  wire [15:0] n1_O_6; // @[Top.scala 49:20]
  wire [15:0] n1_O_7; // @[Top.scala 49:20]
  wire [15:0] n1_O_8; // @[Top.scala 49:20]
  wire [15:0] n1_O_9; // @[Top.scala 49:20]
  wire [15:0] n1_O_10; // @[Top.scala 49:20]
  wire [15:0] n1_O_11; // @[Top.scala 49:20]
  wire [15:0] n1_O_12; // @[Top.scala 49:20]
  wire [15:0] n1_O_13; // @[Top.scala 49:20]
  wire [15:0] n1_O_14; // @[Top.scala 49:20]
  wire [15:0] n1_O_15; // @[Top.scala 49:20]
  wire  n3_clock; // @[Top.scala 52:20]
  wire  n3_reset; // @[Top.scala 52:20]
  wire  n3_valid_up; // @[Top.scala 52:20]
  wire  n3_valid_down; // @[Top.scala 52:20]
  wire [15:0] n3_I_0; // @[Top.scala 52:20]
  wire [15:0] n3_I_1; // @[Top.scala 52:20]
  wire [15:0] n3_I_2; // @[Top.scala 52:20]
  wire [15:0] n3_I_3; // @[Top.scala 52:20]
  wire [15:0] n3_I_4; // @[Top.scala 52:20]
  wire [15:0] n3_I_5; // @[Top.scala 52:20]
  wire [15:0] n3_I_6; // @[Top.scala 52:20]
  wire [15:0] n3_I_7; // @[Top.scala 52:20]
  wire [15:0] n3_I_8; // @[Top.scala 52:20]
  wire [15:0] n3_I_9; // @[Top.scala 52:20]
  wire [15:0] n3_I_10; // @[Top.scala 52:20]
  wire [15:0] n3_I_11; // @[Top.scala 52:20]
  wire [15:0] n3_I_12; // @[Top.scala 52:20]
  wire [15:0] n3_I_13; // @[Top.scala 52:20]
  wire [15:0] n3_I_14; // @[Top.scala 52:20]
  wire [15:0] n3_I_15; // @[Top.scala 52:20]
  wire [15:0] n3_O_0; // @[Top.scala 52:20]
  wire [15:0] n3_O_1; // @[Top.scala 52:20]
  wire [15:0] n3_O_2; // @[Top.scala 52:20]
  wire [15:0] n3_O_3; // @[Top.scala 52:20]
  wire [15:0] n3_O_4; // @[Top.scala 52:20]
  wire [15:0] n3_O_5; // @[Top.scala 52:20]
  wire [15:0] n3_O_6; // @[Top.scala 52:20]
  wire [15:0] n3_O_7; // @[Top.scala 52:20]
  wire [15:0] n3_O_8; // @[Top.scala 52:20]
  wire [15:0] n3_O_9; // @[Top.scala 52:20]
  wire [15:0] n3_O_10; // @[Top.scala 52:20]
  wire [15:0] n3_O_11; // @[Top.scala 52:20]
  wire [15:0] n3_O_12; // @[Top.scala 52:20]
  wire [15:0] n3_O_13; // @[Top.scala 52:20]
  wire [15:0] n3_O_14; // @[Top.scala 52:20]
  wire [15:0] n3_O_15; // @[Top.scala 52:20]
  wire  n4_valid_up; // @[Top.scala 55:20]
  wire  n4_valid_down; // @[Top.scala 55:20]
  wire [15:0] n4_I_0; // @[Top.scala 55:20]
  wire [15:0] n4_I_1; // @[Top.scala 55:20]
  wire [15:0] n4_I_2; // @[Top.scala 55:20]
  wire [15:0] n4_I_3; // @[Top.scala 55:20]
  wire [15:0] n4_I_4; // @[Top.scala 55:20]
  wire [15:0] n4_I_5; // @[Top.scala 55:20]
  wire [15:0] n4_I_6; // @[Top.scala 55:20]
  wire [15:0] n4_I_7; // @[Top.scala 55:20]
  wire [15:0] n4_I_8; // @[Top.scala 55:20]
  wire [15:0] n4_I_9; // @[Top.scala 55:20]
  wire [15:0] n4_I_10; // @[Top.scala 55:20]
  wire [15:0] n4_I_11; // @[Top.scala 55:20]
  wire [15:0] n4_I_12; // @[Top.scala 55:20]
  wire [15:0] n4_I_13; // @[Top.scala 55:20]
  wire [15:0] n4_I_14; // @[Top.scala 55:20]
  wire [15:0] n4_I_15; // @[Top.scala 55:20]
  wire [15:0] n4_O_0; // @[Top.scala 55:20]
  wire [15:0] n4_O_1; // @[Top.scala 55:20]
  wire [15:0] n4_O_2; // @[Top.scala 55:20]
  wire [15:0] n4_O_3; // @[Top.scala 55:20]
  wire [15:0] n4_O_4; // @[Top.scala 55:20]
  wire [15:0] n4_O_5; // @[Top.scala 55:20]
  wire [15:0] n4_O_6; // @[Top.scala 55:20]
  wire [15:0] n4_O_7; // @[Top.scala 55:20]
  wire [15:0] n4_O_8; // @[Top.scala 55:20]
  wire [15:0] n4_O_9; // @[Top.scala 55:20]
  wire [15:0] n4_O_10; // @[Top.scala 55:20]
  wire [15:0] n4_O_11; // @[Top.scala 55:20]
  wire [15:0] n4_O_12; // @[Top.scala 55:20]
  wire [15:0] n4_O_13; // @[Top.scala 55:20]
  wire [15:0] n4_O_14; // @[Top.scala 55:20]
  wire [15:0] n4_O_15; // @[Top.scala 55:20]
  wire  n5_clock; // @[Top.scala 58:20]
  wire  n5_reset; // @[Top.scala 58:20]
  wire  n5_valid_up; // @[Top.scala 58:20]
  wire  n5_valid_down; // @[Top.scala 58:20]
  wire [15:0] n5_I_0; // @[Top.scala 58:20]
  wire [15:0] n5_I_1; // @[Top.scala 58:20]
  wire [15:0] n5_I_2; // @[Top.scala 58:20]
  wire [15:0] n5_I_3; // @[Top.scala 58:20]
  wire [15:0] n5_I_4; // @[Top.scala 58:20]
  wire [15:0] n5_I_5; // @[Top.scala 58:20]
  wire [15:0] n5_I_6; // @[Top.scala 58:20]
  wire [15:0] n5_I_7; // @[Top.scala 58:20]
  wire [15:0] n5_I_8; // @[Top.scala 58:20]
  wire [15:0] n5_I_9; // @[Top.scala 58:20]
  wire [15:0] n5_I_10; // @[Top.scala 58:20]
  wire [15:0] n5_I_11; // @[Top.scala 58:20]
  wire [15:0] n5_I_12; // @[Top.scala 58:20]
  wire [15:0] n5_I_13; // @[Top.scala 58:20]
  wire [15:0] n5_I_14; // @[Top.scala 58:20]
  wire [15:0] n5_I_15; // @[Top.scala 58:20]
  wire [15:0] n5_O_0; // @[Top.scala 58:20]
  wire [15:0] n5_O_1; // @[Top.scala 58:20]
  wire [15:0] n5_O_2; // @[Top.scala 58:20]
  wire [15:0] n5_O_3; // @[Top.scala 58:20]
  wire [15:0] n5_O_4; // @[Top.scala 58:20]
  wire [15:0] n5_O_5; // @[Top.scala 58:20]
  wire [15:0] n5_O_6; // @[Top.scala 58:20]
  wire [15:0] n5_O_7; // @[Top.scala 58:20]
  wire [15:0] n5_O_8; // @[Top.scala 58:20]
  wire [15:0] n5_O_9; // @[Top.scala 58:20]
  wire [15:0] n5_O_10; // @[Top.scala 58:20]
  wire [15:0] n5_O_11; // @[Top.scala 58:20]
  wire [15:0] n5_O_12; // @[Top.scala 58:20]
  wire [15:0] n5_O_13; // @[Top.scala 58:20]
  wire [15:0] n5_O_14; // @[Top.scala 58:20]
  wire [15:0] n5_O_15; // @[Top.scala 58:20]
  wire  n6_valid_up; // @[Top.scala 61:20]
  wire  n6_valid_down; // @[Top.scala 61:20]
  wire [15:0] n6_I_0; // @[Top.scala 61:20]
  wire [15:0] n6_I_1; // @[Top.scala 61:20]
  wire [15:0] n6_I_2; // @[Top.scala 61:20]
  wire [15:0] n6_I_3; // @[Top.scala 61:20]
  wire [15:0] n6_I_4; // @[Top.scala 61:20]
  wire [15:0] n6_I_5; // @[Top.scala 61:20]
  wire [15:0] n6_I_6; // @[Top.scala 61:20]
  wire [15:0] n6_I_7; // @[Top.scala 61:20]
  wire [15:0] n6_I_8; // @[Top.scala 61:20]
  wire [15:0] n6_I_9; // @[Top.scala 61:20]
  wire [15:0] n6_I_10; // @[Top.scala 61:20]
  wire [15:0] n6_I_11; // @[Top.scala 61:20]
  wire [15:0] n6_I_12; // @[Top.scala 61:20]
  wire [15:0] n6_I_13; // @[Top.scala 61:20]
  wire [15:0] n6_I_14; // @[Top.scala 61:20]
  wire [15:0] n6_I_15; // @[Top.scala 61:20]
  wire [15:0] n6_O_0; // @[Top.scala 61:20]
  wire [15:0] n6_O_1; // @[Top.scala 61:20]
  wire [15:0] n6_O_2; // @[Top.scala 61:20]
  wire [15:0] n6_O_3; // @[Top.scala 61:20]
  wire [15:0] n6_O_4; // @[Top.scala 61:20]
  wire [15:0] n6_O_5; // @[Top.scala 61:20]
  wire [15:0] n6_O_6; // @[Top.scala 61:20]
  wire [15:0] n6_O_7; // @[Top.scala 61:20]
  wire [15:0] n6_O_8; // @[Top.scala 61:20]
  wire [15:0] n6_O_9; // @[Top.scala 61:20]
  wire [15:0] n6_O_10; // @[Top.scala 61:20]
  wire [15:0] n6_O_11; // @[Top.scala 61:20]
  wire [15:0] n6_O_12; // @[Top.scala 61:20]
  wire [15:0] n6_O_13; // @[Top.scala 61:20]
  wire [15:0] n6_O_14; // @[Top.scala 61:20]
  wire [15:0] n6_O_15; // @[Top.scala 61:20]
  wire  n7_clock; // @[Top.scala 64:20]
  wire  n7_reset; // @[Top.scala 64:20]
  wire  n7_valid_up; // @[Top.scala 64:20]
  wire  n7_valid_down; // @[Top.scala 64:20]
  wire [15:0] n7_I0_0; // @[Top.scala 64:20]
  wire [15:0] n7_I0_1; // @[Top.scala 64:20]
  wire [15:0] n7_I0_2; // @[Top.scala 64:20]
  wire [15:0] n7_I0_3; // @[Top.scala 64:20]
  wire [15:0] n7_I0_4; // @[Top.scala 64:20]
  wire [15:0] n7_I0_5; // @[Top.scala 64:20]
  wire [15:0] n7_I0_6; // @[Top.scala 64:20]
  wire [15:0] n7_I0_7; // @[Top.scala 64:20]
  wire [15:0] n7_I0_8; // @[Top.scala 64:20]
  wire [15:0] n7_I0_9; // @[Top.scala 64:20]
  wire [15:0] n7_I0_10; // @[Top.scala 64:20]
  wire [15:0] n7_I0_11; // @[Top.scala 64:20]
  wire [15:0] n7_I0_12; // @[Top.scala 64:20]
  wire [15:0] n7_I0_13; // @[Top.scala 64:20]
  wire [15:0] n7_I0_14; // @[Top.scala 64:20]
  wire [15:0] n7_I0_15; // @[Top.scala 64:20]
  wire [15:0] n7_I1_0; // @[Top.scala 64:20]
  wire [15:0] n7_I1_1; // @[Top.scala 64:20]
  wire [15:0] n7_I1_2; // @[Top.scala 64:20]
  wire [15:0] n7_I1_3; // @[Top.scala 64:20]
  wire [15:0] n7_I1_4; // @[Top.scala 64:20]
  wire [15:0] n7_I1_5; // @[Top.scala 64:20]
  wire [15:0] n7_I1_6; // @[Top.scala 64:20]
  wire [15:0] n7_I1_7; // @[Top.scala 64:20]
  wire [15:0] n7_I1_8; // @[Top.scala 64:20]
  wire [15:0] n7_I1_9; // @[Top.scala 64:20]
  wire [15:0] n7_I1_10; // @[Top.scala 64:20]
  wire [15:0] n7_I1_11; // @[Top.scala 64:20]
  wire [15:0] n7_I1_12; // @[Top.scala 64:20]
  wire [15:0] n7_I1_13; // @[Top.scala 64:20]
  wire [15:0] n7_I1_14; // @[Top.scala 64:20]
  wire [15:0] n7_I1_15; // @[Top.scala 64:20]
  wire [15:0] n7_O_0; // @[Top.scala 64:20]
  wire [15:0] n7_O_1; // @[Top.scala 64:20]
  wire [15:0] n7_O_2; // @[Top.scala 64:20]
  wire [15:0] n7_O_3; // @[Top.scala 64:20]
  wire [15:0] n7_O_4; // @[Top.scala 64:20]
  wire [15:0] n7_O_5; // @[Top.scala 64:20]
  wire [15:0] n7_O_6; // @[Top.scala 64:20]
  wire [15:0] n7_O_7; // @[Top.scala 64:20]
  wire [15:0] n7_O_8; // @[Top.scala 64:20]
  wire [15:0] n7_O_9; // @[Top.scala 64:20]
  wire [15:0] n7_O_10; // @[Top.scala 64:20]
  wire [15:0] n7_O_11; // @[Top.scala 64:20]
  wire [15:0] n7_O_12; // @[Top.scala 64:20]
  wire [15:0] n7_O_13; // @[Top.scala 64:20]
  wire [15:0] n7_O_14; // @[Top.scala 64:20]
  wire [15:0] n7_O_15; // @[Top.scala 64:20]
  wire  n15_valid_up; // @[Top.scala 68:21]
  wire  n15_valid_down; // @[Top.scala 68:21]
  wire [15:0] n15_I_0; // @[Top.scala 68:21]
  wire [15:0] n15_I_1; // @[Top.scala 68:21]
  wire [15:0] n15_I_2; // @[Top.scala 68:21]
  wire [15:0] n15_I_3; // @[Top.scala 68:21]
  wire [15:0] n15_I_4; // @[Top.scala 68:21]
  wire [15:0] n15_I_5; // @[Top.scala 68:21]
  wire [15:0] n15_I_6; // @[Top.scala 68:21]
  wire [15:0] n15_I_7; // @[Top.scala 68:21]
  wire [15:0] n15_I_8; // @[Top.scala 68:21]
  wire [15:0] n15_I_9; // @[Top.scala 68:21]
  wire [15:0] n15_I_10; // @[Top.scala 68:21]
  wire [15:0] n15_I_11; // @[Top.scala 68:21]
  wire [15:0] n15_I_12; // @[Top.scala 68:21]
  wire [15:0] n15_I_13; // @[Top.scala 68:21]
  wire [15:0] n15_I_14; // @[Top.scala 68:21]
  wire [15:0] n15_I_15; // @[Top.scala 68:21]
  wire [15:0] n15_O_0; // @[Top.scala 68:21]
  wire [15:0] n15_O_1; // @[Top.scala 68:21]
  wire [15:0] n15_O_2; // @[Top.scala 68:21]
  wire [15:0] n15_O_3; // @[Top.scala 68:21]
  wire [15:0] n15_O_4; // @[Top.scala 68:21]
  wire [15:0] n15_O_5; // @[Top.scala 68:21]
  wire [15:0] n15_O_6; // @[Top.scala 68:21]
  wire [15:0] n15_O_7; // @[Top.scala 68:21]
  wire [15:0] n15_O_8; // @[Top.scala 68:21]
  wire [15:0] n15_O_9; // @[Top.scala 68:21]
  wire [15:0] n15_O_10; // @[Top.scala 68:21]
  wire [15:0] n15_O_11; // @[Top.scala 68:21]
  wire [15:0] n15_O_12; // @[Top.scala 68:21]
  wire [15:0] n15_O_13; // @[Top.scala 68:21]
  wire [15:0] n15_O_14; // @[Top.scala 68:21]
  wire [15:0] n15_O_15; // @[Top.scala 68:21]
  wire  n26_clock; // @[Top.scala 71:21]
  wire  n26_reset; // @[Top.scala 71:21]
  wire  n26_valid_up; // @[Top.scala 71:21]
  wire  n26_valid_down; // @[Top.scala 71:21]
  wire [15:0] n26_I_0; // @[Top.scala 71:21]
  wire [15:0] n26_I_1; // @[Top.scala 71:21]
  wire [15:0] n26_I_2; // @[Top.scala 71:21]
  wire [15:0] n26_I_3; // @[Top.scala 71:21]
  wire [15:0] n26_I_4; // @[Top.scala 71:21]
  wire [15:0] n26_I_5; // @[Top.scala 71:21]
  wire [15:0] n26_I_6; // @[Top.scala 71:21]
  wire [15:0] n26_I_7; // @[Top.scala 71:21]
  wire [15:0] n26_I_8; // @[Top.scala 71:21]
  wire [15:0] n26_I_9; // @[Top.scala 71:21]
  wire [15:0] n26_I_10; // @[Top.scala 71:21]
  wire [15:0] n26_I_11; // @[Top.scala 71:21]
  wire [15:0] n26_I_12; // @[Top.scala 71:21]
  wire [15:0] n26_I_13; // @[Top.scala 71:21]
  wire [15:0] n26_I_14; // @[Top.scala 71:21]
  wire [15:0] n26_I_15; // @[Top.scala 71:21]
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
    .I_4(n1_I_4),
    .I_5(n1_I_5),
    .I_6(n1_I_6),
    .I_7(n1_I_7),
    .I_8(n1_I_8),
    .I_9(n1_I_9),
    .I_10(n1_I_10),
    .I_11(n1_I_11),
    .I_12(n1_I_12),
    .I_13(n1_I_13),
    .I_14(n1_I_14),
    .I_15(n1_I_15),
    .O_0(n1_O_0),
    .O_1(n1_O_1),
    .O_2(n1_O_2),
    .O_3(n1_O_3),
    .O_4(n1_O_4),
    .O_5(n1_O_5),
    .O_6(n1_O_6),
    .O_7(n1_O_7),
    .O_8(n1_O_8),
    .O_9(n1_O_9),
    .O_10(n1_O_10),
    .O_11(n1_O_11),
    .O_12(n1_O_12),
    .O_13(n1_O_13),
    .O_14(n1_O_14),
    .O_15(n1_O_15)
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
    .I_4(n3_I_4),
    .I_5(n3_I_5),
    .I_6(n3_I_6),
    .I_7(n3_I_7),
    .I_8(n3_I_8),
    .I_9(n3_I_9),
    .I_10(n3_I_10),
    .I_11(n3_I_11),
    .I_12(n3_I_12),
    .I_13(n3_I_13),
    .I_14(n3_I_14),
    .I_15(n3_I_15),
    .O_0(n3_O_0),
    .O_1(n3_O_1),
    .O_2(n3_O_2),
    .O_3(n3_O_3),
    .O_4(n3_O_4),
    .O_5(n3_O_5),
    .O_6(n3_O_6),
    .O_7(n3_O_7),
    .O_8(n3_O_8),
    .O_9(n3_O_9),
    .O_10(n3_O_10),
    .O_11(n3_O_11),
    .O_12(n3_O_12),
    .O_13(n3_O_13),
    .O_14(n3_O_14),
    .O_15(n3_O_15)
  );
  Passthrough n4 ( // @[Top.scala 55:20]
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
    .I_8(n4_I_8),
    .I_9(n4_I_9),
    .I_10(n4_I_10),
    .I_11(n4_I_11),
    .I_12(n4_I_12),
    .I_13(n4_I_13),
    .I_14(n4_I_14),
    .I_15(n4_I_15),
    .O_0(n4_O_0),
    .O_1(n4_O_1),
    .O_2(n4_O_2),
    .O_3(n4_O_3),
    .O_4(n4_O_4),
    .O_5(n4_O_5),
    .O_6(n4_O_6),
    .O_7(n4_O_7),
    .O_8(n4_O_8),
    .O_9(n4_O_9),
    .O_10(n4_O_10),
    .O_11(n4_O_11),
    .O_12(n4_O_12),
    .O_13(n4_O_13),
    .O_14(n4_O_14),
    .O_15(n4_O_15)
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
    .I_4(n5_I_4),
    .I_5(n5_I_5),
    .I_6(n5_I_6),
    .I_7(n5_I_7),
    .I_8(n5_I_8),
    .I_9(n5_I_9),
    .I_10(n5_I_10),
    .I_11(n5_I_11),
    .I_12(n5_I_12),
    .I_13(n5_I_13),
    .I_14(n5_I_14),
    .I_15(n5_I_15),
    .O_0(n5_O_0),
    .O_1(n5_O_1),
    .O_2(n5_O_2),
    .O_3(n5_O_3),
    .O_4(n5_O_4),
    .O_5(n5_O_5),
    .O_6(n5_O_6),
    .O_7(n5_O_7),
    .O_8(n5_O_8),
    .O_9(n5_O_9),
    .O_10(n5_O_10),
    .O_11(n5_O_11),
    .O_12(n5_O_12),
    .O_13(n5_O_13),
    .O_14(n5_O_14),
    .O_15(n5_O_15)
  );
  Passthrough n6 ( // @[Top.scala 61:20]
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
    .I_8(n6_I_8),
    .I_9(n6_I_9),
    .I_10(n6_I_10),
    .I_11(n6_I_11),
    .I_12(n6_I_12),
    .I_13(n6_I_13),
    .I_14(n6_I_14),
    .I_15(n6_I_15),
    .O_0(n6_O_0),
    .O_1(n6_O_1),
    .O_2(n6_O_2),
    .O_3(n6_O_3),
    .O_4(n6_O_4),
    .O_5(n6_O_5),
    .O_6(n6_O_6),
    .O_7(n6_O_7),
    .O_8(n6_O_8),
    .O_9(n6_O_9),
    .O_10(n6_O_10),
    .O_11(n6_O_11),
    .O_12(n6_O_12),
    .O_13(n6_O_13),
    .O_14(n6_O_14),
    .O_15(n6_O_15)
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
    .I0_4(n7_I0_4),
    .I0_5(n7_I0_5),
    .I0_6(n7_I0_6),
    .I0_7(n7_I0_7),
    .I0_8(n7_I0_8),
    .I0_9(n7_I0_9),
    .I0_10(n7_I0_10),
    .I0_11(n7_I0_11),
    .I0_12(n7_I0_12),
    .I0_13(n7_I0_13),
    .I0_14(n7_I0_14),
    .I0_15(n7_I0_15),
    .I1_0(n7_I1_0),
    .I1_1(n7_I1_1),
    .I1_2(n7_I1_2),
    .I1_3(n7_I1_3),
    .I1_4(n7_I1_4),
    .I1_5(n7_I1_5),
    .I1_6(n7_I1_6),
    .I1_7(n7_I1_7),
    .I1_8(n7_I1_8),
    .I1_9(n7_I1_9),
    .I1_10(n7_I1_10),
    .I1_11(n7_I1_11),
    .I1_12(n7_I1_12),
    .I1_13(n7_I1_13),
    .I1_14(n7_I1_14),
    .I1_15(n7_I1_15),
    .O_0(n7_O_0),
    .O_1(n7_O_1),
    .O_2(n7_O_2),
    .O_3(n7_O_3),
    .O_4(n7_O_4),
    .O_5(n7_O_5),
    .O_6(n7_O_6),
    .O_7(n7_O_7),
    .O_8(n7_O_8),
    .O_9(n7_O_9),
    .O_10(n7_O_10),
    .O_11(n7_O_11),
    .O_12(n7_O_12),
    .O_13(n7_O_13),
    .O_14(n7_O_14),
    .O_15(n7_O_15)
  );
  Passthrough n15 ( // @[Top.scala 68:21]
    .valid_up(n15_valid_up),
    .valid_down(n15_valid_down),
    .I_0(n15_I_0),
    .I_1(n15_I_1),
    .I_2(n15_I_2),
    .I_3(n15_I_3),
    .I_4(n15_I_4),
    .I_5(n15_I_5),
    .I_6(n15_I_6),
    .I_7(n15_I_7),
    .I_8(n15_I_8),
    .I_9(n15_I_9),
    .I_10(n15_I_10),
    .I_11(n15_I_11),
    .I_12(n15_I_12),
    .I_13(n15_I_13),
    .I_14(n15_I_14),
    .I_15(n15_I_15),
    .O_0(n15_O_0),
    .O_1(n15_O_1),
    .O_2(n15_O_2),
    .O_3(n15_O_3),
    .O_4(n15_O_4),
    .O_5(n15_O_5),
    .O_6(n15_O_6),
    .O_7(n15_O_7),
    .O_8(n15_O_8),
    .O_9(n15_O_9),
    .O_10(n15_O_10),
    .O_11(n15_O_11),
    .O_12(n15_O_12),
    .O_13(n15_O_13),
    .O_14(n15_O_14),
    .O_15(n15_O_15)
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
    .I_4(n26_I_4),
    .I_5(n26_I_5),
    .I_6(n26_I_6),
    .I_7(n26_I_7),
    .I_8(n26_I_8),
    .I_9(n26_I_9),
    .I_10(n26_I_10),
    .I_11(n26_I_11),
    .I_12(n26_I_12),
    .I_13(n26_I_13),
    .I_14(n26_I_14),
    .I_15(n26_I_15),
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
  assign n1_I_4 = I0_4; // @[Top.scala 50:10]
  assign n1_I_5 = I0_5; // @[Top.scala 50:10]
  assign n1_I_6 = I0_6; // @[Top.scala 50:10]
  assign n1_I_7 = I0_7; // @[Top.scala 50:10]
  assign n1_I_8 = I0_8; // @[Top.scala 50:10]
  assign n1_I_9 = I0_9; // @[Top.scala 50:10]
  assign n1_I_10 = I0_10; // @[Top.scala 50:10]
  assign n1_I_11 = I0_11; // @[Top.scala 50:10]
  assign n1_I_12 = I0_12; // @[Top.scala 50:10]
  assign n1_I_13 = I0_13; // @[Top.scala 50:10]
  assign n1_I_14 = I0_14; // @[Top.scala 50:10]
  assign n1_I_15 = I0_15; // @[Top.scala 50:10]
  assign n3_clock = clock;
  assign n3_reset = reset;
  assign n3_valid_up = valid_up; // @[Top.scala 54:17]
  assign n3_I_0 = I1_0; // @[Top.scala 53:10]
  assign n3_I_1 = I1_1; // @[Top.scala 53:10]
  assign n3_I_2 = I1_2; // @[Top.scala 53:10]
  assign n3_I_3 = I1_3; // @[Top.scala 53:10]
  assign n3_I_4 = I1_4; // @[Top.scala 53:10]
  assign n3_I_5 = I1_5; // @[Top.scala 53:10]
  assign n3_I_6 = I1_6; // @[Top.scala 53:10]
  assign n3_I_7 = I1_7; // @[Top.scala 53:10]
  assign n3_I_8 = I1_8; // @[Top.scala 53:10]
  assign n3_I_9 = I1_9; // @[Top.scala 53:10]
  assign n3_I_10 = I1_10; // @[Top.scala 53:10]
  assign n3_I_11 = I1_11; // @[Top.scala 53:10]
  assign n3_I_12 = I1_12; // @[Top.scala 53:10]
  assign n3_I_13 = I1_13; // @[Top.scala 53:10]
  assign n3_I_14 = I1_14; // @[Top.scala 53:10]
  assign n3_I_15 = I1_15; // @[Top.scala 53:10]
  assign n4_valid_up = n3_valid_down; // @[Top.scala 57:17]
  assign n4_I_0 = n3_O_0; // @[Top.scala 56:10]
  assign n4_I_1 = n3_O_1; // @[Top.scala 56:10]
  assign n4_I_2 = n3_O_2; // @[Top.scala 56:10]
  assign n4_I_3 = n3_O_3; // @[Top.scala 56:10]
  assign n4_I_4 = n3_O_4; // @[Top.scala 56:10]
  assign n4_I_5 = n3_O_5; // @[Top.scala 56:10]
  assign n4_I_6 = n3_O_6; // @[Top.scala 56:10]
  assign n4_I_7 = n3_O_7; // @[Top.scala 56:10]
  assign n4_I_8 = n3_O_8; // @[Top.scala 56:10]
  assign n4_I_9 = n3_O_9; // @[Top.scala 56:10]
  assign n4_I_10 = n3_O_10; // @[Top.scala 56:10]
  assign n4_I_11 = n3_O_11; // @[Top.scala 56:10]
  assign n4_I_12 = n3_O_12; // @[Top.scala 56:10]
  assign n4_I_13 = n3_O_13; // @[Top.scala 56:10]
  assign n4_I_14 = n3_O_14; // @[Top.scala 56:10]
  assign n4_I_15 = n3_O_15; // @[Top.scala 56:10]
  assign n5_clock = clock;
  assign n5_reset = reset;
  assign n5_valid_up = n4_valid_down; // @[Top.scala 60:17]
  assign n5_I_0 = n4_O_0; // @[Top.scala 59:10]
  assign n5_I_1 = n4_O_1; // @[Top.scala 59:10]
  assign n5_I_2 = n4_O_2; // @[Top.scala 59:10]
  assign n5_I_3 = n4_O_3; // @[Top.scala 59:10]
  assign n5_I_4 = n4_O_4; // @[Top.scala 59:10]
  assign n5_I_5 = n4_O_5; // @[Top.scala 59:10]
  assign n5_I_6 = n4_O_6; // @[Top.scala 59:10]
  assign n5_I_7 = n4_O_7; // @[Top.scala 59:10]
  assign n5_I_8 = n4_O_8; // @[Top.scala 59:10]
  assign n5_I_9 = n4_O_9; // @[Top.scala 59:10]
  assign n5_I_10 = n4_O_10; // @[Top.scala 59:10]
  assign n5_I_11 = n4_O_11; // @[Top.scala 59:10]
  assign n5_I_12 = n4_O_12; // @[Top.scala 59:10]
  assign n5_I_13 = n4_O_13; // @[Top.scala 59:10]
  assign n5_I_14 = n4_O_14; // @[Top.scala 59:10]
  assign n5_I_15 = n4_O_15; // @[Top.scala 59:10]
  assign n6_valid_up = n5_valid_down; // @[Top.scala 63:17]
  assign n6_I_0 = n5_O_0; // @[Top.scala 62:10]
  assign n6_I_1 = n5_O_1; // @[Top.scala 62:10]
  assign n6_I_2 = n5_O_2; // @[Top.scala 62:10]
  assign n6_I_3 = n5_O_3; // @[Top.scala 62:10]
  assign n6_I_4 = n5_O_4; // @[Top.scala 62:10]
  assign n6_I_5 = n5_O_5; // @[Top.scala 62:10]
  assign n6_I_6 = n5_O_6; // @[Top.scala 62:10]
  assign n6_I_7 = n5_O_7; // @[Top.scala 62:10]
  assign n6_I_8 = n5_O_8; // @[Top.scala 62:10]
  assign n6_I_9 = n5_O_9; // @[Top.scala 62:10]
  assign n6_I_10 = n5_O_10; // @[Top.scala 62:10]
  assign n6_I_11 = n5_O_11; // @[Top.scala 62:10]
  assign n6_I_12 = n5_O_12; // @[Top.scala 62:10]
  assign n6_I_13 = n5_O_13; // @[Top.scala 62:10]
  assign n6_I_14 = n5_O_14; // @[Top.scala 62:10]
  assign n6_I_15 = n5_O_15; // @[Top.scala 62:10]
  assign n7_clock = clock;
  assign n7_reset = reset;
  assign n7_valid_up = n1_valid_down & n6_valid_down; // @[Top.scala 67:17]
  assign n7_I0_0 = n1_O_0; // @[Top.scala 65:11]
  assign n7_I0_1 = n1_O_1; // @[Top.scala 65:11]
  assign n7_I0_2 = n1_O_2; // @[Top.scala 65:11]
  assign n7_I0_3 = n1_O_3; // @[Top.scala 65:11]
  assign n7_I0_4 = n1_O_4; // @[Top.scala 65:11]
  assign n7_I0_5 = n1_O_5; // @[Top.scala 65:11]
  assign n7_I0_6 = n1_O_6; // @[Top.scala 65:11]
  assign n7_I0_7 = n1_O_7; // @[Top.scala 65:11]
  assign n7_I0_8 = n1_O_8; // @[Top.scala 65:11]
  assign n7_I0_9 = n1_O_9; // @[Top.scala 65:11]
  assign n7_I0_10 = n1_O_10; // @[Top.scala 65:11]
  assign n7_I0_11 = n1_O_11; // @[Top.scala 65:11]
  assign n7_I0_12 = n1_O_12; // @[Top.scala 65:11]
  assign n7_I0_13 = n1_O_13; // @[Top.scala 65:11]
  assign n7_I0_14 = n1_O_14; // @[Top.scala 65:11]
  assign n7_I0_15 = n1_O_15; // @[Top.scala 65:11]
  assign n7_I1_0 = n6_O_0; // @[Top.scala 66:11]
  assign n7_I1_1 = n6_O_1; // @[Top.scala 66:11]
  assign n7_I1_2 = n6_O_2; // @[Top.scala 66:11]
  assign n7_I1_3 = n6_O_3; // @[Top.scala 66:11]
  assign n7_I1_4 = n6_O_4; // @[Top.scala 66:11]
  assign n7_I1_5 = n6_O_5; // @[Top.scala 66:11]
  assign n7_I1_6 = n6_O_6; // @[Top.scala 66:11]
  assign n7_I1_7 = n6_O_7; // @[Top.scala 66:11]
  assign n7_I1_8 = n6_O_8; // @[Top.scala 66:11]
  assign n7_I1_9 = n6_O_9; // @[Top.scala 66:11]
  assign n7_I1_10 = n6_O_10; // @[Top.scala 66:11]
  assign n7_I1_11 = n6_O_11; // @[Top.scala 66:11]
  assign n7_I1_12 = n6_O_12; // @[Top.scala 66:11]
  assign n7_I1_13 = n6_O_13; // @[Top.scala 66:11]
  assign n7_I1_14 = n6_O_14; // @[Top.scala 66:11]
  assign n7_I1_15 = n6_O_15; // @[Top.scala 66:11]
  assign n15_valid_up = n7_valid_down; // @[Top.scala 70:18]
  assign n15_I_0 = n7_O_0; // @[Top.scala 69:11]
  assign n15_I_1 = n7_O_1; // @[Top.scala 69:11]
  assign n15_I_2 = n7_O_2; // @[Top.scala 69:11]
  assign n15_I_3 = n7_O_3; // @[Top.scala 69:11]
  assign n15_I_4 = n7_O_4; // @[Top.scala 69:11]
  assign n15_I_5 = n7_O_5; // @[Top.scala 69:11]
  assign n15_I_6 = n7_O_6; // @[Top.scala 69:11]
  assign n15_I_7 = n7_O_7; // @[Top.scala 69:11]
  assign n15_I_8 = n7_O_8; // @[Top.scala 69:11]
  assign n15_I_9 = n7_O_9; // @[Top.scala 69:11]
  assign n15_I_10 = n7_O_10; // @[Top.scala 69:11]
  assign n15_I_11 = n7_O_11; // @[Top.scala 69:11]
  assign n15_I_12 = n7_O_12; // @[Top.scala 69:11]
  assign n15_I_13 = n7_O_13; // @[Top.scala 69:11]
  assign n15_I_14 = n7_O_14; // @[Top.scala 69:11]
  assign n15_I_15 = n7_O_15; // @[Top.scala 69:11]
  assign n26_clock = clock;
  assign n26_reset = reset;
  assign n26_valid_up = n15_valid_down; // @[Top.scala 73:18]
  assign n26_I_0 = n15_O_0; // @[Top.scala 72:11]
  assign n26_I_1 = n15_O_1; // @[Top.scala 72:11]
  assign n26_I_2 = n15_O_2; // @[Top.scala 72:11]
  assign n26_I_3 = n15_O_3; // @[Top.scala 72:11]
  assign n26_I_4 = n15_O_4; // @[Top.scala 72:11]
  assign n26_I_5 = n15_O_5; // @[Top.scala 72:11]
  assign n26_I_6 = n15_O_6; // @[Top.scala 72:11]
  assign n26_I_7 = n15_O_7; // @[Top.scala 72:11]
  assign n26_I_8 = n15_O_8; // @[Top.scala 72:11]
  assign n26_I_9 = n15_O_9; // @[Top.scala 72:11]
  assign n26_I_10 = n15_O_10; // @[Top.scala 72:11]
  assign n26_I_11 = n15_O_11; // @[Top.scala 72:11]
  assign n26_I_12 = n15_O_12; // @[Top.scala 72:11]
  assign n26_I_13 = n15_O_13; // @[Top.scala 72:11]
  assign n26_I_14 = n15_O_14; // @[Top.scala 72:11]
  assign n26_I_15 = n15_O_15; // @[Top.scala 72:11]
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
