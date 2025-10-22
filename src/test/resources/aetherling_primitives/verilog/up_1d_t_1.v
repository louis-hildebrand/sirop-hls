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
  output [15:0] RDATA,
  input         WE,
  input  [15:0] WDATA
);
  wire  write_elem_counter_CE; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_valid; // @[RAM_ST.scala 20:34]
  wire  read_elem_counter_CE; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_valid; // @[RAM_ST.scala 21:33]
  reg [15:0] ram [0:0]; // @[RAM_ST.scala 29:24]
  reg [31:0] _RAND_0;
  wire [15:0] ram__T_7_data; // @[RAM_ST.scala 29:24]
  wire  ram__T_7_addr; // @[RAM_ST.scala 29:24]
  wire [15:0] ram__T_3_data; // @[RAM_ST.scala 29:24]
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
  assign read_elem_counter_CE = 1'h1; // @[RAM_ST.scala 24:24]
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
    ram[initvar] = _RAND_0[15:0];
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
module UpT(
  input         clock,
  input         reset,
  input  [15:0] I,
  output [15:0] O
);
  wire  mem_clock; // @[Upsample.scala 31:19]
  wire [15:0] mem_RDATA; // @[Upsample.scala 31:19]
  wire  mem_WE; // @[Upsample.scala 31:19]
  wire [15:0] mem_WDATA; // @[Upsample.scala 31:19]
  reg [2:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire [2:0] _T_6 = value + 3'h1; // @[Counter.scala 39:22]
  wire  _T_7 = value == 3'h0; // @[Upsample.scala 39:36]
  wire [15:0] dataOut = mem_RDATA; // @[Upsample.scala 33:21 Upsample.scala 37:11]
  RAM_ST mem ( // @[Upsample.scala 31:19]
    .clock(mem_clock),
    .RDATA(mem_RDATA),
    .WE(mem_WE),
    .WDATA(mem_WDATA)
  );
  assign O = _T_7 ? $signed(I) : $signed(dataOut); // @[Upsample.scala 21:5 Upsample.scala 44:47 Upsample.scala 44:68]
  assign mem_clock = clock;
  assign mem_WE = value == 3'h0; // @[Upsample.scala 39:54 Upsample.scala 39:86 Upsample.scala 42:25]
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
  value = _RAND_0[2:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 3'h0;
    end else begin
      value <= _T_6;
    end
  end
endmodule
module Top(
  input         clock,
  input         reset,
  input         valid_up,
  output        valid_down,
  output [15:0] O
);
  wire  n2_clock; // @[Top.scala 17:20]
  wire  n2_reset; // @[Top.scala 17:20]
  wire [15:0] n2_I; // @[Top.scala 17:20]
  wire [15:0] n2_O; // @[Top.scala 17:20]
  reg [2:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire [2:0] _T_4 = value + 3'h1; // @[Counter.scala 39:22]
  wire [6:0] _GEN_3 = 3'h1 == value ? 7'h0 : $signed(7'sh2a); // @[Const.scala 25:72]
  wire [6:0] _GEN_4 = 3'h2 == value ? 7'h0 : $signed(_GEN_3); // @[Const.scala 25:72]
  wire [6:0] _GEN_5 = 3'h3 == value ? 7'h0 : $signed(_GEN_4); // @[Const.scala 25:72]
  wire [6:0] _GEN_6 = 3'h4 == value ? 7'h0 : $signed(_GEN_5); // @[Const.scala 25:72]
  wire [6:0] _GEN_7 = 3'h5 == value ? 7'h0 : $signed(_GEN_6); // @[Const.scala 25:72]
  wire [6:0] _GEN_8 = 3'h6 == value ? 7'h0 : $signed(_GEN_7); // @[Const.scala 25:72]
  wire [6:0] _GEN_9 = 3'h7 == value ? 7'h0 : $signed(_GEN_8); // @[Const.scala 25:72]
  UpT n2 ( // @[Top.scala 17:20]
    .clock(n2_clock),
    .reset(n2_reset),
    .I(n2_I),
    .O(n2_O)
  );
  assign valid_down = 1'h1; // @[Top.scala 21:16]
  assign O = n2_O; // @[Top.scala 20:7]
  assign n2_clock = clock;
  assign n2_reset = reset;
  assign n2_I = {{9{_GEN_9[6]}},_GEN_9}; // @[Top.scala 18:10]
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
    end else begin
      value <= _T_4;
    end
  end
endmodule
