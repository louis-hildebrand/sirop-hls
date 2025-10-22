module NestedCounters(
  input   CE,
  output  valid
);
  assign valid = CE; // @[NestedCounters.scala 65:13]
endmodule
module NestedCounters_1(
  input   clock,
  input   reset,
  output  valid,
  output  last
);
  wire  NestedCounters_CE; // @[NestedCounters.scala 43:31]
  wire  NestedCounters_valid; // @[NestedCounters.scala 43:31]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_2 = value == 2'h2; // @[Counter.scala 38:24]
  wire [1:0] _T_4 = value + 2'h1; // @[Counter.scala 39:22]
  wire  _T_7 = value < 2'h1; // @[NestedCounters.scala 48:35]
  NestedCounters NestedCounters ( // @[NestedCounters.scala 43:31]
    .CE(NestedCounters_CE),
    .valid(NestedCounters_valid)
  );
  assign valid = _T_7 & NestedCounters_valid; // @[NestedCounters.scala 48:11]
  assign last = value == 2'h2; // @[NestedCounters.scala 47:10]
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
    end else if (_T_2) begin
      value <= 2'h0;
    end else begin
      value <= _T_4;
    end
  end
endmodule
module NestedCounters_2(
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
  reg [2:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_2 = value == 3'h5; // @[Counter.scala 38:24]
  wire [2:0] _T_4 = value + 3'h1; // @[Counter.scala 39:22]
  wire  _T_7 = value < 3'h6; // @[NestedCounters.scala 48:35]
  NestedCounters_1 NestedCounters ( // @[NestedCounters.scala 43:31]
    .clock(NestedCounters_clock),
    .reset(NestedCounters_reset),
    .valid(NestedCounters_valid),
    .last(NestedCounters_last)
  );
  assign valid = _T_7 & NestedCounters_valid; // @[NestedCounters.scala 48:11]
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
  value = _RAND_0[2:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 3'h0;
    end else if (_T) begin
      if (_T_2) begin
        value <= 3'h0;
      end else begin
        value <= _T_4;
      end
    end
  end
endmodule
module Counter_TN(
  input        clock,
  input        reset,
  output [7:0] O
);
  wire  nested_counters_clock; // @[Counter.scala 118:31]
  wire  nested_counters_reset; // @[Counter.scala 118:31]
  wire  nested_counters_valid; // @[Counter.scala 118:31]
  wire  nested_counters_last; // @[Counter.scala 118:31]
  reg [7:0] counter_value; // @[Counter.scala 134:30]
  reg [31:0] _RAND_0;
  wire  _T = nested_counters_last; // @[Counter.scala 141:19]
  wire  _T_1 = nested_counters_valid; // @[Counter.scala 142:25]
  wire [7:0] _T_3 = counter_value + 8'h3; // @[Counter.scala 142:97]
  NestedCounters_2 nested_counters ( // @[Counter.scala 118:31]
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
  counter_value = _RAND_0[7:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      counter_value <= 8'h0;
    end else if (_T) begin
      counter_value <= 8'h0;
    end else if (_T_1) begin
      counter_value <= _T_3;
    end
  end
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
module NestedCounters_7(
  input   clock,
  input   reset,
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
    end else if (_T_2) begin
      value <= 2'h0;
    end else begin
      value <= _T_4;
    end
  end
endmodule
module ShiftTN(
  input        clock,
  input        reset,
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
  NestedCounters_7 inner_valid ( // @[ShiftTN.scala 56:27]
    .clock(inner_valid_clock),
    .reset(inner_valid_reset),
    .valid(inner_valid_valid)
  );
  assign O = value_store_RDATA; // @[ShiftTN.scala 66:5]
  assign value_store_clock = clock;
  assign value_store_RE = inner_valid_valid; // @[ShiftTN.scala 64:18]
  assign value_store_WE = inner_valid_valid; // @[ShiftTN.scala 63:18]
  assign value_store_WDATA = I; // @[ShiftTN.scala 65:21]
  assign next_ram_addr_CE = 1'h1; // @[ShiftTN.scala 45:20]
  assign inner_valid_clock = clock;
  assign inner_valid_reset = reset;
endmodule
module Top(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  output [7:0] O
);
  wire  counter1_clock; // @[Top.scala 16:26]
  wire  counter1_reset; // @[Top.scala 16:26]
  wire [7:0] counter1_O; // @[Top.scala 16:26]
  wire  n2_clock; // @[Top.scala 18:20]
  wire  n2_reset; // @[Top.scala 18:20]
  wire [7:0] n2_I; // @[Top.scala 18:20]
  wire [7:0] n2_O; // @[Top.scala 18:20]
  Counter_TN counter1 ( // @[Top.scala 16:26]
    .clock(counter1_clock),
    .reset(counter1_reset),
    .O(counter1_O)
  );
  ShiftTN n2 ( // @[Top.scala 18:20]
    .clock(n2_clock),
    .reset(n2_reset),
    .I(n2_I),
    .O(n2_O)
  );
  assign valid_down = 1'h1; // @[Top.scala 22:16]
  assign O = n2_O; // @[Top.scala 21:7]
  assign counter1_clock = clock;
  assign counter1_reset = reset;
  assign n2_clock = clock;
  assign n2_reset = reset;
  assign n2_I = counter1_O; // @[Top.scala 19:10]
endmodule
