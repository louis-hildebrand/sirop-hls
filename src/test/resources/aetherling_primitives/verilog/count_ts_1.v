module Counter_T(
  input        clock,
  input        reset,
  output [7:0] O
);
  reg [7:0] counter_value; // @[Counter.scala 53:30]
  reg [31:0] _RAND_0;
  wire  _T = counter_value == 8'hbd; // @[Counter.scala 61:49]
  wire [7:0] _T_3 = counter_value + 8'h15; // @[Counter.scala 63:70]
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
module Counter_TS(
  input        clock,
  input        reset,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2,
  output [7:0] O_3,
  output [7:0] O_4,
  output [7:0] O_5,
  output [7:0] O_6
);
  wire  counter_t_clock; // @[Counter.scala 84:25]
  wire  counter_t_reset; // @[Counter.scala 84:25]
  wire [7:0] counter_t_O; // @[Counter.scala 84:25]
  wire [8:0] _T = {{1'd0}, counter_t_O}; // @[Counter.scala 95:49]
  Counter_T counter_t ( // @[Counter.scala 84:25]
    .clock(counter_t_clock),
    .reset(counter_t_reset),
    .O(counter_t_O)
  );
  assign O_0 = _T[7:0]; // @[Counter.scala 95:12]
  assign O_1 = 8'h3 + counter_t_O; // @[Counter.scala 95:12]
  assign O_2 = 8'h6 + counter_t_O; // @[Counter.scala 95:12]
  assign O_3 = 8'h9 + counter_t_O; // @[Counter.scala 95:12]
  assign O_4 = 8'hc + counter_t_O; // @[Counter.scala 95:12]
  assign O_5 = 8'hf + counter_t_O; // @[Counter.scala 95:12]
  assign O_6 = 8'h12 + counter_t_O; // @[Counter.scala 95:12]
  assign counter_t_clock = clock;
  assign counter_t_reset = reset;
endmodule
module Top(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2,
  output [7:0] O_3,
  output [7:0] O_4,
  output [7:0] O_5,
  output [7:0] O_6
);
  wire  counter1_clock; // @[Top.scala 16:26]
  wire  counter1_reset; // @[Top.scala 16:26]
  wire [7:0] counter1_O_0; // @[Top.scala 16:26]
  wire [7:0] counter1_O_1; // @[Top.scala 16:26]
  wire [7:0] counter1_O_2; // @[Top.scala 16:26]
  wire [7:0] counter1_O_3; // @[Top.scala 16:26]
  wire [7:0] counter1_O_4; // @[Top.scala 16:26]
  wire [7:0] counter1_O_5; // @[Top.scala 16:26]
  wire [7:0] counter1_O_6; // @[Top.scala 16:26]
  Counter_TS counter1 ( // @[Top.scala 16:26]
    .clock(counter1_clock),
    .reset(counter1_reset),
    .O_0(counter1_O_0),
    .O_1(counter1_O_1),
    .O_2(counter1_O_2),
    .O_3(counter1_O_3),
    .O_4(counter1_O_4),
    .O_5(counter1_O_5),
    .O_6(counter1_O_6)
  );
  assign valid_down = 1'h1; // @[Top.scala 19:16]
  assign O_0 = counter1_O_0; // @[Top.scala 18:7]
  assign O_1 = counter1_O_1; // @[Top.scala 18:7]
  assign O_2 = counter1_O_2; // @[Top.scala 18:7]
  assign O_3 = counter1_O_3; // @[Top.scala 18:7]
  assign O_4 = counter1_O_4; // @[Top.scala 18:7]
  assign O_5 = counter1_O_5; // @[Top.scala 18:7]
  assign O_6 = counter1_O_6; // @[Top.scala 18:7]
  assign counter1_clock = clock;
  assign counter1_reset = reset;
endmodule
