module Counter_T(
  input        clock,
  input        reset,
  output [7:0] O
);
  reg [7:0] counter_value; // @[Counter.scala 53:30]
  reg [31:0] _RAND_0;
  wire  _T = counter_value == 8'h15; // @[Counter.scala 61:49]
  wire [7:0] _T_3 = counter_value + 8'h3; // @[Counter.scala 63:70]
  assign O = counter_value; // @[Counter.scala 66:5]
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
    end else begin
      counter_value <= _T_3;
    end
  end
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
module DownT(
  input        clock,
  input        reset,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  wire  InitialDelayCounter_clock; // @[Downsample.scala 25:38]
  wire  InitialDelayCounter_reset; // @[Downsample.scala 25:38]
  wire  InitialDelayCounter_valid_down; // @[Downsample.scala 25:38]
  InitialDelayCounter InitialDelayCounter ( // @[Downsample.scala 25:38]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  assign valid_down = InitialDelayCounter_valid_down; // @[Downsample.scala 27:16]
  assign O = I; // @[Downsample.scala 19:5]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
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
  wire  n2_valid_down; // @[Top.scala 18:20]
  wire [7:0] n2_I; // @[Top.scala 18:20]
  wire [7:0] n2_O; // @[Top.scala 18:20]
  Counter_T counter1 ( // @[Top.scala 16:26]
    .clock(counter1_clock),
    .reset(counter1_reset),
    .O(counter1_O)
  );
  DownT n2 ( // @[Top.scala 18:20]
    .clock(n2_clock),
    .reset(n2_reset),
    .valid_down(n2_valid_down),
    .I(n2_I),
    .O(n2_O)
  );
  assign valid_down = n2_valid_down; // @[Top.scala 22:16]
  assign O = n2_O; // @[Top.scala 21:7]
  assign counter1_clock = clock;
  assign counter1_reset = reset;
  assign n2_clock = clock;
  assign n2_reset = reset;
  assign n2_I = counter1_O; // @[Top.scala 19:10]
endmodule
