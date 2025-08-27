module NestedCounters_1(
  input   clock,
  input   reset,
  output  valid,
  output  last
);
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire [1:0] _T_4 = value + 2'h1; // @[Counter.scala 39:22]
  assign valid = value < 2'h1; // @[NestedCounters.scala 48:11]
  assign last = value == 2'h3; // @[NestedCounters.scala 47:10]
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
  wire [7:0] _T_3 = counter_value + 8'h2; // @[Counter.scala 142:97]
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
  Counter_TN counter1 ( // @[Top.scala 16:26]
    .clock(counter1_clock),
    .reset(counter1_reset),
    .O(counter1_O)
  );
  assign valid_down = 1'h1; // @[Top.scala 19:16]
  assign O = counter1_O; // @[Top.scala 18:7]
  assign counter1_clock = clock;
  assign counter1_reset = reset;
endmodule
