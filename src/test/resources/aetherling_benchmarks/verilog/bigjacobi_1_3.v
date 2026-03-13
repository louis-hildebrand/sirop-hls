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
  input  [6:0] RADDR,
  output [7:0] RDATA,
  input        WE,
  input  [6:0] WADDR,
  input  [7:0] WDATA
);
  wire  write_elem_counter_CE; // @[RAM_ST.scala 20:34]
  wire  write_elem_counter_valid; // @[RAM_ST.scala 20:34]
  wire  read_elem_counter_CE; // @[RAM_ST.scala 21:33]
  wire  read_elem_counter_valid; // @[RAM_ST.scala 21:33]
  reg [7:0] ram [0:127]; // @[RAM_ST.scala 29:24]
  reg [31:0] _RAND_0;
  wire [7:0] ram__T_8_data; // @[RAM_ST.scala 29:24]
  wire [6:0] ram__T_8_addr; // @[RAM_ST.scala 29:24]
  wire [7:0] ram__T_2_data; // @[RAM_ST.scala 29:24]
  wire [6:0] ram__T_2_addr; // @[RAM_ST.scala 29:24]
  wire  ram__T_2_mask; // @[RAM_ST.scala 29:24]
  wire  ram__T_2_en; // @[RAM_ST.scala 29:24]
  reg  ram__T_8_en_pipe_0;
  reg [31:0] _RAND_1;
  reg [6:0] ram__T_8_addr_pipe_0;
  reg [31:0] _RAND_2;
  wire [6:0] _GEN_1 = 7'h1 == WADDR ? 7'h1 : 7'h0; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_2 = 7'h2 == WADDR ? 7'h2 : _GEN_1; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_3 = 7'h3 == WADDR ? 7'h3 : _GEN_2; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_4 = 7'h4 == WADDR ? 7'h4 : _GEN_3; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_5 = 7'h5 == WADDR ? 7'h5 : _GEN_4; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_6 = 7'h6 == WADDR ? 7'h6 : _GEN_5; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_7 = 7'h7 == WADDR ? 7'h7 : _GEN_6; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_8 = 7'h8 == WADDR ? 7'h8 : _GEN_7; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_9 = 7'h9 == WADDR ? 7'h9 : _GEN_8; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_10 = 7'ha == WADDR ? 7'ha : _GEN_9; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_11 = 7'hb == WADDR ? 7'hb : _GEN_10; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_12 = 7'hc == WADDR ? 7'hc : _GEN_11; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_13 = 7'hd == WADDR ? 7'hd : _GEN_12; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_14 = 7'he == WADDR ? 7'he : _GEN_13; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_15 = 7'hf == WADDR ? 7'hf : _GEN_14; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_16 = 7'h10 == WADDR ? 7'h10 : _GEN_15; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_17 = 7'h11 == WADDR ? 7'h11 : _GEN_16; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_18 = 7'h12 == WADDR ? 7'h12 : _GEN_17; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_19 = 7'h13 == WADDR ? 7'h13 : _GEN_18; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_20 = 7'h14 == WADDR ? 7'h14 : _GEN_19; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_21 = 7'h15 == WADDR ? 7'h15 : _GEN_20; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_22 = 7'h16 == WADDR ? 7'h16 : _GEN_21; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_23 = 7'h17 == WADDR ? 7'h17 : _GEN_22; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_24 = 7'h18 == WADDR ? 7'h18 : _GEN_23; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_25 = 7'h19 == WADDR ? 7'h19 : _GEN_24; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_26 = 7'h1a == WADDR ? 7'h1a : _GEN_25; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_27 = 7'h1b == WADDR ? 7'h1b : _GEN_26; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_28 = 7'h1c == WADDR ? 7'h1c : _GEN_27; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_29 = 7'h1d == WADDR ? 7'h1d : _GEN_28; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_30 = 7'h1e == WADDR ? 7'h1e : _GEN_29; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_31 = 7'h1f == WADDR ? 7'h1f : _GEN_30; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_32 = 7'h20 == WADDR ? 7'h20 : _GEN_31; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_33 = 7'h21 == WADDR ? 7'h21 : _GEN_32; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_34 = 7'h22 == WADDR ? 7'h22 : _GEN_33; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_35 = 7'h23 == WADDR ? 7'h23 : _GEN_34; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_36 = 7'h24 == WADDR ? 7'h24 : _GEN_35; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_37 = 7'h25 == WADDR ? 7'h25 : _GEN_36; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_38 = 7'h26 == WADDR ? 7'h26 : _GEN_37; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_39 = 7'h27 == WADDR ? 7'h27 : _GEN_38; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_40 = 7'h28 == WADDR ? 7'h28 : _GEN_39; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_41 = 7'h29 == WADDR ? 7'h29 : _GEN_40; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_42 = 7'h2a == WADDR ? 7'h2a : _GEN_41; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_43 = 7'h2b == WADDR ? 7'h2b : _GEN_42; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_44 = 7'h2c == WADDR ? 7'h2c : _GEN_43; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_45 = 7'h2d == WADDR ? 7'h2d : _GEN_44; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_46 = 7'h2e == WADDR ? 7'h2e : _GEN_45; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_47 = 7'h2f == WADDR ? 7'h2f : _GEN_46; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_48 = 7'h30 == WADDR ? 7'h30 : _GEN_47; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_49 = 7'h31 == WADDR ? 7'h31 : _GEN_48; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_50 = 7'h32 == WADDR ? 7'h32 : _GEN_49; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_51 = 7'h33 == WADDR ? 7'h33 : _GEN_50; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_52 = 7'h34 == WADDR ? 7'h34 : _GEN_51; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_53 = 7'h35 == WADDR ? 7'h35 : _GEN_52; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_54 = 7'h36 == WADDR ? 7'h36 : _GEN_53; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_55 = 7'h37 == WADDR ? 7'h37 : _GEN_54; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_56 = 7'h38 == WADDR ? 7'h38 : _GEN_55; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_57 = 7'h39 == WADDR ? 7'h39 : _GEN_56; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_58 = 7'h3a == WADDR ? 7'h3a : _GEN_57; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_59 = 7'h3b == WADDR ? 7'h3b : _GEN_58; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_60 = 7'h3c == WADDR ? 7'h3c : _GEN_59; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_61 = 7'h3d == WADDR ? 7'h3d : _GEN_60; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_62 = 7'h3e == WADDR ? 7'h3e : _GEN_61; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_63 = 7'h3f == WADDR ? 7'h3f : _GEN_62; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_64 = 7'h40 == WADDR ? 7'h40 : _GEN_63; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_65 = 7'h41 == WADDR ? 7'h41 : _GEN_64; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_66 = 7'h42 == WADDR ? 7'h42 : _GEN_65; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_67 = 7'h43 == WADDR ? 7'h43 : _GEN_66; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_68 = 7'h44 == WADDR ? 7'h44 : _GEN_67; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_69 = 7'h45 == WADDR ? 7'h45 : _GEN_68; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_70 = 7'h46 == WADDR ? 7'h46 : _GEN_69; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_71 = 7'h47 == WADDR ? 7'h47 : _GEN_70; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_72 = 7'h48 == WADDR ? 7'h48 : _GEN_71; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_73 = 7'h49 == WADDR ? 7'h49 : _GEN_72; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_74 = 7'h4a == WADDR ? 7'h4a : _GEN_73; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_75 = 7'h4b == WADDR ? 7'h4b : _GEN_74; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_76 = 7'h4c == WADDR ? 7'h4c : _GEN_75; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_77 = 7'h4d == WADDR ? 7'h4d : _GEN_76; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_78 = 7'h4e == WADDR ? 7'h4e : _GEN_77; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_79 = 7'h4f == WADDR ? 7'h4f : _GEN_78; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_80 = 7'h50 == WADDR ? 7'h50 : _GEN_79; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_81 = 7'h51 == WADDR ? 7'h51 : _GEN_80; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_82 = 7'h52 == WADDR ? 7'h52 : _GEN_81; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_83 = 7'h53 == WADDR ? 7'h53 : _GEN_82; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_84 = 7'h54 == WADDR ? 7'h54 : _GEN_83; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_85 = 7'h55 == WADDR ? 7'h55 : _GEN_84; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_86 = 7'h56 == WADDR ? 7'h56 : _GEN_85; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_87 = 7'h57 == WADDR ? 7'h57 : _GEN_86; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_88 = 7'h58 == WADDR ? 7'h58 : _GEN_87; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_89 = 7'h59 == WADDR ? 7'h59 : _GEN_88; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_90 = 7'h5a == WADDR ? 7'h5a : _GEN_89; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_91 = 7'h5b == WADDR ? 7'h5b : _GEN_90; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_92 = 7'h5c == WADDR ? 7'h5c : _GEN_91; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_93 = 7'h5d == WADDR ? 7'h5d : _GEN_92; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_94 = 7'h5e == WADDR ? 7'h5e : _GEN_93; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_95 = 7'h5f == WADDR ? 7'h5f : _GEN_94; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_96 = 7'h60 == WADDR ? 7'h60 : _GEN_95; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_97 = 7'h61 == WADDR ? 7'h61 : _GEN_96; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_98 = 7'h62 == WADDR ? 7'h62 : _GEN_97; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_99 = 7'h63 == WADDR ? 7'h63 : _GEN_98; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_100 = 7'h64 == WADDR ? 7'h64 : _GEN_99; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_101 = 7'h65 == WADDR ? 7'h65 : _GEN_100; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_102 = 7'h66 == WADDR ? 7'h66 : _GEN_101; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_103 = 7'h67 == WADDR ? 7'h67 : _GEN_102; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_104 = 7'h68 == WADDR ? 7'h68 : _GEN_103; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_105 = 7'h69 == WADDR ? 7'h69 : _GEN_104; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_106 = 7'h6a == WADDR ? 7'h6a : _GEN_105; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_107 = 7'h6b == WADDR ? 7'h6b : _GEN_106; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_108 = 7'h6c == WADDR ? 7'h6c : _GEN_107; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_109 = 7'h6d == WADDR ? 7'h6d : _GEN_108; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_110 = 7'h6e == WADDR ? 7'h6e : _GEN_109; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_111 = 7'h6f == WADDR ? 7'h6f : _GEN_110; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_112 = 7'h70 == WADDR ? 7'h70 : _GEN_111; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_113 = 7'h71 == WADDR ? 7'h71 : _GEN_112; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_114 = 7'h72 == WADDR ? 7'h72 : _GEN_113; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_115 = 7'h73 == WADDR ? 7'h73 : _GEN_114; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_116 = 7'h74 == WADDR ? 7'h74 : _GEN_115; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_117 = 7'h75 == WADDR ? 7'h75 : _GEN_116; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_118 = 7'h76 == WADDR ? 7'h76 : _GEN_117; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_119 = 7'h77 == WADDR ? 7'h77 : _GEN_118; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_120 = 7'h78 == WADDR ? 7'h78 : _GEN_119; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_121 = 7'h79 == WADDR ? 7'h79 : _GEN_120; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_122 = 7'h7a == WADDR ? 7'h7a : _GEN_121; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_123 = 7'h7b == WADDR ? 7'h7b : _GEN_122; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_124 = 7'h7c == WADDR ? 7'h7c : _GEN_123; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_125 = 7'h7d == WADDR ? 7'h7d : _GEN_124; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_126 = 7'h7e == WADDR ? 7'h7e : _GEN_125; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_127 = 7'h7f == WADDR ? 7'h7f : _GEN_126; // @[RAM_ST.scala 31:71]
  wire [7:0] _T = {{1'd0}, _GEN_127}; // @[RAM_ST.scala 31:71]
  wire [6:0] _GEN_134 = 7'h1 == RADDR ? 7'h1 : 7'h0; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_135 = 7'h2 == RADDR ? 7'h2 : _GEN_134; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_136 = 7'h3 == RADDR ? 7'h3 : _GEN_135; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_137 = 7'h4 == RADDR ? 7'h4 : _GEN_136; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_138 = 7'h5 == RADDR ? 7'h5 : _GEN_137; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_139 = 7'h6 == RADDR ? 7'h6 : _GEN_138; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_140 = 7'h7 == RADDR ? 7'h7 : _GEN_139; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_141 = 7'h8 == RADDR ? 7'h8 : _GEN_140; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_142 = 7'h9 == RADDR ? 7'h9 : _GEN_141; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_143 = 7'ha == RADDR ? 7'ha : _GEN_142; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_144 = 7'hb == RADDR ? 7'hb : _GEN_143; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_145 = 7'hc == RADDR ? 7'hc : _GEN_144; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_146 = 7'hd == RADDR ? 7'hd : _GEN_145; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_147 = 7'he == RADDR ? 7'he : _GEN_146; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_148 = 7'hf == RADDR ? 7'hf : _GEN_147; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_149 = 7'h10 == RADDR ? 7'h10 : _GEN_148; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_150 = 7'h11 == RADDR ? 7'h11 : _GEN_149; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_151 = 7'h12 == RADDR ? 7'h12 : _GEN_150; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_152 = 7'h13 == RADDR ? 7'h13 : _GEN_151; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_153 = 7'h14 == RADDR ? 7'h14 : _GEN_152; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_154 = 7'h15 == RADDR ? 7'h15 : _GEN_153; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_155 = 7'h16 == RADDR ? 7'h16 : _GEN_154; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_156 = 7'h17 == RADDR ? 7'h17 : _GEN_155; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_157 = 7'h18 == RADDR ? 7'h18 : _GEN_156; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_158 = 7'h19 == RADDR ? 7'h19 : _GEN_157; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_159 = 7'h1a == RADDR ? 7'h1a : _GEN_158; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_160 = 7'h1b == RADDR ? 7'h1b : _GEN_159; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_161 = 7'h1c == RADDR ? 7'h1c : _GEN_160; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_162 = 7'h1d == RADDR ? 7'h1d : _GEN_161; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_163 = 7'h1e == RADDR ? 7'h1e : _GEN_162; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_164 = 7'h1f == RADDR ? 7'h1f : _GEN_163; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_165 = 7'h20 == RADDR ? 7'h20 : _GEN_164; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_166 = 7'h21 == RADDR ? 7'h21 : _GEN_165; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_167 = 7'h22 == RADDR ? 7'h22 : _GEN_166; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_168 = 7'h23 == RADDR ? 7'h23 : _GEN_167; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_169 = 7'h24 == RADDR ? 7'h24 : _GEN_168; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_170 = 7'h25 == RADDR ? 7'h25 : _GEN_169; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_171 = 7'h26 == RADDR ? 7'h26 : _GEN_170; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_172 = 7'h27 == RADDR ? 7'h27 : _GEN_171; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_173 = 7'h28 == RADDR ? 7'h28 : _GEN_172; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_174 = 7'h29 == RADDR ? 7'h29 : _GEN_173; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_175 = 7'h2a == RADDR ? 7'h2a : _GEN_174; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_176 = 7'h2b == RADDR ? 7'h2b : _GEN_175; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_177 = 7'h2c == RADDR ? 7'h2c : _GEN_176; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_178 = 7'h2d == RADDR ? 7'h2d : _GEN_177; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_179 = 7'h2e == RADDR ? 7'h2e : _GEN_178; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_180 = 7'h2f == RADDR ? 7'h2f : _GEN_179; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_181 = 7'h30 == RADDR ? 7'h30 : _GEN_180; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_182 = 7'h31 == RADDR ? 7'h31 : _GEN_181; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_183 = 7'h32 == RADDR ? 7'h32 : _GEN_182; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_184 = 7'h33 == RADDR ? 7'h33 : _GEN_183; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_185 = 7'h34 == RADDR ? 7'h34 : _GEN_184; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_186 = 7'h35 == RADDR ? 7'h35 : _GEN_185; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_187 = 7'h36 == RADDR ? 7'h36 : _GEN_186; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_188 = 7'h37 == RADDR ? 7'h37 : _GEN_187; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_189 = 7'h38 == RADDR ? 7'h38 : _GEN_188; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_190 = 7'h39 == RADDR ? 7'h39 : _GEN_189; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_191 = 7'h3a == RADDR ? 7'h3a : _GEN_190; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_192 = 7'h3b == RADDR ? 7'h3b : _GEN_191; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_193 = 7'h3c == RADDR ? 7'h3c : _GEN_192; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_194 = 7'h3d == RADDR ? 7'h3d : _GEN_193; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_195 = 7'h3e == RADDR ? 7'h3e : _GEN_194; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_196 = 7'h3f == RADDR ? 7'h3f : _GEN_195; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_197 = 7'h40 == RADDR ? 7'h40 : _GEN_196; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_198 = 7'h41 == RADDR ? 7'h41 : _GEN_197; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_199 = 7'h42 == RADDR ? 7'h42 : _GEN_198; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_200 = 7'h43 == RADDR ? 7'h43 : _GEN_199; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_201 = 7'h44 == RADDR ? 7'h44 : _GEN_200; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_202 = 7'h45 == RADDR ? 7'h45 : _GEN_201; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_203 = 7'h46 == RADDR ? 7'h46 : _GEN_202; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_204 = 7'h47 == RADDR ? 7'h47 : _GEN_203; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_205 = 7'h48 == RADDR ? 7'h48 : _GEN_204; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_206 = 7'h49 == RADDR ? 7'h49 : _GEN_205; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_207 = 7'h4a == RADDR ? 7'h4a : _GEN_206; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_208 = 7'h4b == RADDR ? 7'h4b : _GEN_207; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_209 = 7'h4c == RADDR ? 7'h4c : _GEN_208; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_210 = 7'h4d == RADDR ? 7'h4d : _GEN_209; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_211 = 7'h4e == RADDR ? 7'h4e : _GEN_210; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_212 = 7'h4f == RADDR ? 7'h4f : _GEN_211; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_213 = 7'h50 == RADDR ? 7'h50 : _GEN_212; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_214 = 7'h51 == RADDR ? 7'h51 : _GEN_213; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_215 = 7'h52 == RADDR ? 7'h52 : _GEN_214; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_216 = 7'h53 == RADDR ? 7'h53 : _GEN_215; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_217 = 7'h54 == RADDR ? 7'h54 : _GEN_216; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_218 = 7'h55 == RADDR ? 7'h55 : _GEN_217; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_219 = 7'h56 == RADDR ? 7'h56 : _GEN_218; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_220 = 7'h57 == RADDR ? 7'h57 : _GEN_219; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_221 = 7'h58 == RADDR ? 7'h58 : _GEN_220; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_222 = 7'h59 == RADDR ? 7'h59 : _GEN_221; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_223 = 7'h5a == RADDR ? 7'h5a : _GEN_222; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_224 = 7'h5b == RADDR ? 7'h5b : _GEN_223; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_225 = 7'h5c == RADDR ? 7'h5c : _GEN_224; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_226 = 7'h5d == RADDR ? 7'h5d : _GEN_225; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_227 = 7'h5e == RADDR ? 7'h5e : _GEN_226; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_228 = 7'h5f == RADDR ? 7'h5f : _GEN_227; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_229 = 7'h60 == RADDR ? 7'h60 : _GEN_228; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_230 = 7'h61 == RADDR ? 7'h61 : _GEN_229; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_231 = 7'h62 == RADDR ? 7'h62 : _GEN_230; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_232 = 7'h63 == RADDR ? 7'h63 : _GEN_231; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_233 = 7'h64 == RADDR ? 7'h64 : _GEN_232; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_234 = 7'h65 == RADDR ? 7'h65 : _GEN_233; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_235 = 7'h66 == RADDR ? 7'h66 : _GEN_234; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_236 = 7'h67 == RADDR ? 7'h67 : _GEN_235; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_237 = 7'h68 == RADDR ? 7'h68 : _GEN_236; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_238 = 7'h69 == RADDR ? 7'h69 : _GEN_237; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_239 = 7'h6a == RADDR ? 7'h6a : _GEN_238; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_240 = 7'h6b == RADDR ? 7'h6b : _GEN_239; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_241 = 7'h6c == RADDR ? 7'h6c : _GEN_240; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_242 = 7'h6d == RADDR ? 7'h6d : _GEN_241; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_243 = 7'h6e == RADDR ? 7'h6e : _GEN_242; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_244 = 7'h6f == RADDR ? 7'h6f : _GEN_243; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_245 = 7'h70 == RADDR ? 7'h70 : _GEN_244; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_246 = 7'h71 == RADDR ? 7'h71 : _GEN_245; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_247 = 7'h72 == RADDR ? 7'h72 : _GEN_246; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_248 = 7'h73 == RADDR ? 7'h73 : _GEN_247; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_249 = 7'h74 == RADDR ? 7'h74 : _GEN_248; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_250 = 7'h75 == RADDR ? 7'h75 : _GEN_249; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_251 = 7'h76 == RADDR ? 7'h76 : _GEN_250; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_252 = 7'h77 == RADDR ? 7'h77 : _GEN_251; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_253 = 7'h78 == RADDR ? 7'h78 : _GEN_252; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_254 = 7'h79 == RADDR ? 7'h79 : _GEN_253; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_255 = 7'h7a == RADDR ? 7'h7a : _GEN_254; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_256 = 7'h7b == RADDR ? 7'h7b : _GEN_255; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_257 = 7'h7c == RADDR ? 7'h7c : _GEN_256; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_258 = 7'h7d == RADDR ? 7'h7d : _GEN_257; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_259 = 7'h7e == RADDR ? 7'h7e : _GEN_258; // @[RAM_ST.scala 32:46]
  wire [6:0] _GEN_260 = 7'h7f == RADDR ? 7'h7f : _GEN_259; // @[RAM_ST.scala 32:46]
  wire [7:0] _T_3 = {{1'd0}, _GEN_260}; // @[RAM_ST.scala 32:46]
  NestedCountersWithNumValid write_elem_counter ( // @[RAM_ST.scala 20:34]
    .CE(write_elem_counter_CE),
    .valid(write_elem_counter_valid)
  );
  NestedCountersWithNumValid read_elem_counter ( // @[RAM_ST.scala 21:33]
    .CE(read_elem_counter_CE),
    .valid(read_elem_counter_valid)
  );
  assign ram__T_8_addr = ram__T_8_addr_pipe_0;
  assign ram__T_8_data = ram[ram__T_8_addr]; // @[RAM_ST.scala 29:24]
  assign ram__T_2_data = WDATA;
  assign ram__T_2_addr = _T[6:0];
  assign ram__T_2_mask = 1'h1;
  assign ram__T_2_en = write_elem_counter_valid;
  assign RDATA = ram__T_8_data; // @[RAM_ST.scala 32:9]
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
  for (initvar = 0; initvar < 128; initvar = initvar+1)
    ram[initvar] = _RAND_0[7:0];
  `endif // RANDOMIZE_MEM_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  ram__T_8_en_pipe_0 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  ram__T_8_addr_pipe_0 = _RAND_2[6:0];
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
      ram__T_8_addr_pipe_0 <= _T_3[6:0];
    end
  end
endmodule
module NestedCounters_4(
  input   clock,
  input   reset,
  input   CE,
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
    end else if (CE) begin
      if (_T_2) begin
        value <= 2'h0;
      end else begin
        value <= _T_4;
      end
    end
  end
endmodule
module ShiftTN(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  wire  value_store_clock; // @[ShiftTN.scala 42:27]
  wire  value_store_RE; // @[ShiftTN.scala 42:27]
  wire [6:0] value_store_RADDR; // @[ShiftTN.scala 42:27]
  wire [7:0] value_store_RDATA; // @[ShiftTN.scala 42:27]
  wire  value_store_WE; // @[ShiftTN.scala 42:27]
  wire [6:0] value_store_WADDR; // @[ShiftTN.scala 42:27]
  wire [7:0] value_store_WDATA; // @[ShiftTN.scala 42:27]
  wire  next_ram_addr_CE; // @[ShiftTN.scala 44:29]
  wire  next_ram_addr_valid; // @[ShiftTN.scala 44:29]
  wire  inner_valid_clock; // @[ShiftTN.scala 56:27]
  wire  inner_valid_reset; // @[ShiftTN.scala 56:27]
  wire  inner_valid_CE; // @[ShiftTN.scala 56:27]
  wire  inner_valid_valid; // @[ShiftTN.scala 56:27]
  wire  _T_2 = valid_up & inner_valid_valid; // @[ShiftTN.scala 58:74]
  reg [6:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_4 = value == 7'h7f; // @[Counter.scala 38:24]
  wire [6:0] _T_6 = value + 7'h1; // @[Counter.scala 39:22]
  RAM_ST value_store ( // @[ShiftTN.scala 42:27]
    .clock(value_store_clock),
    .RE(value_store_RE),
    .RADDR(value_store_RADDR),
    .RDATA(value_store_RDATA),
    .WE(value_store_WE),
    .WADDR(value_store_WADDR),
    .WDATA(value_store_WDATA)
  );
  NestedCounters next_ram_addr ( // @[ShiftTN.scala 44:29]
    .CE(next_ram_addr_CE),
    .valid(next_ram_addr_valid)
  );
  NestedCounters_4 inner_valid ( // @[ShiftTN.scala 56:27]
    .clock(inner_valid_clock),
    .reset(inner_valid_reset),
    .CE(inner_valid_CE),
    .valid(inner_valid_valid)
  );
  assign valid_down = valid_up; // @[ShiftTN.scala 69:14]
  assign O = value_store_RDATA; // @[ShiftTN.scala 66:5]
  assign value_store_clock = clock;
  assign value_store_RE = valid_up & inner_valid_valid; // @[ShiftTN.scala 64:18]
  assign value_store_RADDR = _T_4 ? 7'h0 : _T_6; // @[ShiftTN.scala 61:74 ShiftTN.scala 62:36]
  assign value_store_WE = valid_up & inner_valid_valid; // @[ShiftTN.scala 63:18]
  assign value_store_WADDR = value; // @[ShiftTN.scala 60:21]
  assign value_store_WDATA = I; // @[ShiftTN.scala 65:21]
  assign next_ram_addr_CE = valid_up; // @[ShiftTN.scala 45:20]
  assign inner_valid_clock = clock;
  assign inner_valid_reset = reset;
  assign inner_valid_CE = valid_up; // @[ShiftTN.scala 57:18]
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
  value = _RAND_0[6:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 7'h0;
    end else if (_T_2) begin
      value <= _T_6;
    end
  end
endmodule
module RAM_ST_2(
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
module ShiftTN_2(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
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
  wire  inner_valid_CE; // @[ShiftTN.scala 56:27]
  wire  inner_valid_valid; // @[ShiftTN.scala 56:27]
  RAM_ST_2 value_store ( // @[ShiftTN.scala 42:27]
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
  NestedCounters_4 inner_valid ( // @[ShiftTN.scala 56:27]
    .clock(inner_valid_clock),
    .reset(inner_valid_reset),
    .CE(inner_valid_CE),
    .valid(inner_valid_valid)
  );
  assign valid_down = valid_up; // @[ShiftTN.scala 69:14]
  assign O = value_store_RDATA; // @[ShiftTN.scala 66:5]
  assign value_store_clock = clock;
  assign value_store_RE = valid_up & inner_valid_valid; // @[ShiftTN.scala 64:18]
  assign value_store_WE = valid_up & inner_valid_valid; // @[ShiftTN.scala 63:18]
  assign value_store_WDATA = I; // @[ShiftTN.scala 65:21]
  assign next_ram_addr_CE = valid_up; // @[ShiftTN.scala 45:20]
  assign inner_valid_clock = clock;
  assign inner_valid_reset = reset;
  assign inner_valid_CE = valid_up; // @[ShiftTN.scala 57:18]
endmodule
module SSeqTupleCreator(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O_0,
  output [7:0] O_1
);
  assign valid_down = valid_up; // @[Tuple.scala 15:14]
  assign O_0 = I0; // @[Tuple.scala 12:32]
  assign O_1 = I1; // @[Tuple.scala 13:32]
endmodule
module Map2T(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O_0,
  output [7:0] O_1
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1; // @[Map2T.scala 8:20]
  SSeqTupleCreator op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0(op_I0),
    .I1(op_I1),
    .O_0(op_O_0),
    .O_1(op_O_1)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0 = op_O_0; // @[Map2T.scala 17:7]
  assign O_1 = op_O_1; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0 = I0; // @[Map2T.scala 15:11]
  assign op_I1 = I1; // @[Map2T.scala 16:11]
endmodule
module Map2T_1(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O_0,
  output [7:0] O_1
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1; // @[Map2T.scala 8:20]
  Map2T op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0(op_I0),
    .I1(op_I1),
    .O_0(op_O_0),
    .O_1(op_O_1)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0 = op_O_0; // @[Map2T.scala 17:7]
  assign O_1 = op_O_1; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0 = I0; // @[Map2T.scala 15:11]
  assign op_I1 = I1; // @[Map2T.scala 16:11]
endmodule
module SSeqTupleAppender(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I0_1,
  input  [7:0] I1,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  assign valid_down = valid_up; // @[Tuple.scala 28:14]
  assign O_0 = I0_0; // @[Tuple.scala 24:34]
  assign O_1 = I0_1; // @[Tuple.scala 24:34]
  assign O_2 = I1; // @[Tuple.scala 26:32]
endmodule
module Map2T_2(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I0_1,
  input  [7:0] I1,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_2; // @[Map2T.scala 8:20]
  SSeqTupleAppender op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0(op_I0_0),
    .I0_1(op_I0_1),
    .I1(op_I1),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0 = op_O_0; // @[Map2T.scala 17:7]
  assign O_1 = op_O_1; // @[Map2T.scala 17:7]
  assign O_2 = op_O_2; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0 = I0_0; // @[Map2T.scala 15:11]
  assign op_I0_1 = I0_1; // @[Map2T.scala 15:11]
  assign op_I1 = I1; // @[Map2T.scala 16:11]
endmodule
module Map2T_3(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I0_1,
  input  [7:0] I1,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_2; // @[Map2T.scala 8:20]
  Map2T_2 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0(op_I0_0),
    .I0_1(op_I0_1),
    .I1(op_I1),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0 = op_O_0; // @[Map2T.scala 17:7]
  assign O_1 = op_O_1; // @[Map2T.scala 17:7]
  assign O_2 = op_O_2; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0 = I0_0; // @[Map2T.scala 15:11]
  assign op_I0_1 = I0_1; // @[Map2T.scala 15:11]
  assign op_I1 = I1; // @[Map2T.scala 16:11]
endmodule
module Passthrough(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O_0_0,
  output [7:0] O_0_1,
  output [7:0] O_0_2
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_0_0 = I_0; // @[Passthrough.scala 17:68]
  assign O_0_1 = I_1; // @[Passthrough.scala 17:68]
  assign O_0_2 = I_2; // @[Passthrough.scala 17:68]
endmodule
module Serialize(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O
);
  wire  elem_counter_CE; // @[Serialize.scala 14:28]
  wire  elem_counter_valid; // @[Serialize.scala 14:28]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_2 = value == 2'h2; // @[Counter.scala 38:24]
  wire [1:0] _T_4 = value + 2'h1; // @[Counter.scala 39:22]
  reg [7:0] mux_input_wire_1; // @[Serialize.scala 39:47]
  reg [31:0] _RAND_1;
  reg [7:0] mux_input_wire_2; // @[Serialize.scala 39:47]
  reg [31:0] _RAND_2;
  wire  _T_7 = value == 2'h0; // @[Serialize.scala 42:34]
  wire  _T_8 = _T_7 & valid_up; // @[Serialize.scala 42:42]
  wire  _T_11 = 2'h2 == value; // @[Mux.scala 68:19]
  wire  _T_13 = 2'h1 == value; // @[Mux.scala 68:19]
  wire  _T_15 = 2'h0 == value; // @[Mux.scala 68:19]
  reg [7:0] _T_17; // @[Serialize.scala 53:15]
  reg [31:0] _RAND_3;
  reg  _T_18; // @[Serialize.scala 54:24]
  reg [31:0] _RAND_4;
  NestedCountersWithNumValid elem_counter ( // @[Serialize.scala 14:28]
    .CE(elem_counter_CE),
    .valid(elem_counter_valid)
  );
  assign valid_down = _T_18; // @[Serialize.scala 54:14]
  assign O = _T_17; // @[Serialize.scala 53:5]
  assign elem_counter_CE = valid_up; // @[Serialize.scala 16:19]
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
  mux_input_wire_1 = _RAND_1[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  mux_input_wire_2 = _RAND_2[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_17 = _RAND_3[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_18 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    if (reset) begin
      value <= 2'h0;
    end else if (valid_up) begin
      if (_T_2) begin
        value <= 2'h0;
      end else begin
        value <= _T_4;
      end
    end
    if (_T_8) begin
      mux_input_wire_1 <= I_1;
    end
    if (_T_8) begin
      mux_input_wire_2 <= I_2;
    end
    if (_T_15) begin
      _T_17 <= I_0;
    end else if (_T_13) begin
      _T_17 <= mux_input_wire_1;
    end else if (_T_11) begin
      _T_17 <= mux_input_wire_2;
    end else begin
      _T_17 <= I_0;
    end
    if (reset) begin
      _T_18 <= 1'h0;
    end else begin
      _T_18 <= valid_up;
    end
  end
endmodule
module MapS(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_0_1,
  input  [7:0] I_0_2,
  output [7:0] O_0
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_0; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_1; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_2; // @[MapS.scala 9:22]
  wire [7:0] fst_op_O; // @[MapS.scala 9:22]
  Serialize fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_0(fst_op_I_0),
    .I_1(fst_op_I_1),
    .I_2(fst_op_I_2),
    .O(fst_op_O)
  );
  assign valid_down = fst_op_valid_down; // @[MapS.scala 23:14]
  assign O_0 = fst_op_O; // @[MapS.scala 17:8]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_0 = I_0_0; // @[MapS.scala 16:12]
  assign fst_op_I_1 = I_0_1; // @[MapS.scala 16:12]
  assign fst_op_I_2 = I_0_2; // @[MapS.scala 16:12]
endmodule
module MapT(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_0_1,
  input  [7:0] I_0_2,
  output [7:0] O_0
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_0; // @[MapT.scala 8:20]
  MapS op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_0(op_I_0_0),
    .I_0_1(op_I_0_1),
    .I_0_2(op_I_0_2),
    .O_0(op_O_0)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0 = op_O_0; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_0 = I_0_0; // @[MapT.scala 14:10]
  assign op_I_0_1 = I_0_1; // @[MapT.scala 14:10]
  assign op_I_0_2 = I_0_2; // @[MapT.scala 14:10]
endmodule
module Map2S(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I1_0,
  output [7:0] O_0_0,
  output [7:0] O_0_1
);
  wire  fst_op_valid_up; // @[Map2S.scala 9:22]
  wire  fst_op_valid_down; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I0; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I1; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_0; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_1; // @[Map2S.scala 9:22]
  SSeqTupleCreator fst_op ( // @[Map2S.scala 9:22]
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I0(fst_op_I0),
    .I1(fst_op_I1),
    .O_0(fst_op_O_0),
    .O_1(fst_op_O_1)
  );
  assign valid_down = fst_op_valid_down; // @[Map2S.scala 26:14]
  assign O_0_0 = fst_op_O_0; // @[Map2S.scala 19:8]
  assign O_0_1 = fst_op_O_1; // @[Map2S.scala 19:8]
  assign fst_op_valid_up = valid_up; // @[Map2S.scala 16:19]
  assign fst_op_I0 = I0_0; // @[Map2S.scala 17:13]
  assign fst_op_I1 = I1_0; // @[Map2S.scala 18:13]
endmodule
module Map2T_8(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I1_0,
  output [7:0] O_0_0,
  output [7:0] O_0_1
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_1; // @[Map2T.scala 8:20]
  Map2S op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0(op_I0_0),
    .I1_0(op_I1_0),
    .O_0_0(op_O_0_0),
    .O_0_1(op_O_0_1)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0_0 = op_O_0_0; // @[Map2T.scala 17:7]
  assign O_0_1 = op_O_0_1; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0 = I0_0; // @[Map2T.scala 15:11]
  assign op_I1_0 = I1_0; // @[Map2T.scala 16:11]
endmodule
module Map2S_1(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0_0,
  input  [7:0] I0_0_1,
  input  [7:0] I1_0,
  output [7:0] O_0_0,
  output [7:0] O_0_1,
  output [7:0] O_0_2
);
  wire  fst_op_valid_up; // @[Map2S.scala 9:22]
  wire  fst_op_valid_down; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I0_0; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I0_1; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I1; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_0; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_1; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_2; // @[Map2S.scala 9:22]
  SSeqTupleAppender fst_op ( // @[Map2S.scala 9:22]
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I0_0(fst_op_I0_0),
    .I0_1(fst_op_I0_1),
    .I1(fst_op_I1),
    .O_0(fst_op_O_0),
    .O_1(fst_op_O_1),
    .O_2(fst_op_O_2)
  );
  assign valid_down = fst_op_valid_down; // @[Map2S.scala 26:14]
  assign O_0_0 = fst_op_O_0; // @[Map2S.scala 19:8]
  assign O_0_1 = fst_op_O_1; // @[Map2S.scala 19:8]
  assign O_0_2 = fst_op_O_2; // @[Map2S.scala 19:8]
  assign fst_op_valid_up = valid_up; // @[Map2S.scala 16:19]
  assign fst_op_I0_0 = I0_0_0; // @[Map2S.scala 17:13]
  assign fst_op_I0_1 = I0_0_1; // @[Map2S.scala 17:13]
  assign fst_op_I1 = I1_0; // @[Map2S.scala 18:13]
endmodule
module Map2T_13(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0_0,
  input  [7:0] I0_0_1,
  input  [7:0] I1_0,
  output [7:0] O_0_0,
  output [7:0] O_0_1,
  output [7:0] O_0_2
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_0_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_2; // @[Map2T.scala 8:20]
  Map2S_1 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0_0(op_I0_0_0),
    .I0_0_1(op_I0_0_1),
    .I1_0(op_I1_0),
    .O_0_0(op_O_0_0),
    .O_0_1(op_O_0_1),
    .O_0_2(op_O_0_2)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0_0 = op_O_0_0; // @[Map2T.scala 17:7]
  assign O_0_1 = op_O_0_1; // @[Map2T.scala 17:7]
  assign O_0_2 = op_O_0_2; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0_0 = I0_0_0; // @[Map2T.scala 15:11]
  assign op_I0_0_1 = I0_0_1; // @[Map2T.scala 15:11]
  assign op_I1_0 = I1_0; // @[Map2T.scala 16:11]
endmodule
module Passthrough_3(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_0_1,
  input  [7:0] I_0_2,
  output [7:0] O_0_0,
  output [7:0] O_0_1,
  output [7:0] O_0_2
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_0_0 = I_0_0; // @[Passthrough.scala 17:68]
  assign O_0_1 = I_0_1; // @[Passthrough.scala 17:68]
  assign O_0_2 = I_0_2; // @[Passthrough.scala 17:68]
endmodule
module Passthrough_4(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_0_1,
  input  [7:0] I_0_2,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O_0 = I_0_0; // @[Passthrough.scala 17:68]
  assign O_1 = I_0_1; // @[Passthrough.scala 17:68]
  assign O_2 = I_0_2; // @[Passthrough.scala 17:68]
endmodule
module MapT_3(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_0,
  input  [7:0] I_0_1,
  input  [7:0] I_0_2,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_1; // @[MapT.scala 8:20]
  wire [7:0] op_O_2; // @[MapT.scala 8:20]
  Passthrough_4 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_0(op_I_0_0),
    .I_0_1(op_I_0_1),
    .I_0_2(op_I_0_2),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0 = op_O_0; // @[MapT.scala 15:7]
  assign O_1 = op_O_1; // @[MapT.scala 15:7]
  assign O_2 = op_O_2; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_0 = I_0_0; // @[MapT.scala 14:10]
  assign op_I_0_1 = I_0_1; // @[MapT.scala 14:10]
  assign op_I_0_2 = I_0_2; // @[MapT.scala 14:10]
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
module Map2T_14(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O_t0b,
  output [7:0] O_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_t0b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_t1b; // @[Map2T.scala 8:20]
  AtomTuple op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0(op_I0),
    .I1(op_I1),
    .O_t0b(op_O_t0b),
    .O_t1b(op_O_t1b)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_t0b = op_O_t0b; // @[Map2T.scala 17:7]
  assign O_t1b = op_O_t1b; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0 = I0; // @[Map2T.scala 15:11]
  assign op_I1 = I1; // @[Map2T.scala 16:11]
endmodule
module Map2S_2(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I0_1,
  input  [7:0] I0_2,
  input  [7:0] I1_0,
  input  [7:0] I1_1,
  input  [7:0] I1_2,
  output [7:0] O_0_t0b,
  output [7:0] O_0_t1b,
  output [7:0] O_1_t0b,
  output [7:0] O_1_t1b,
  output [7:0] O_2_t0b,
  output [7:0] O_2_t1b
);
  wire  fst_op_valid_up; // @[Map2S.scala 9:22]
  wire  fst_op_valid_down; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I0; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_I1; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_t0b; // @[Map2S.scala 9:22]
  wire [7:0] fst_op_O_t1b; // @[Map2S.scala 9:22]
  wire  other_ops_0_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_0_valid_down; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_I0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_I1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_O_t0b; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_0_O_t1b; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_up; // @[Map2S.scala 10:86]
  wire  other_ops_1_valid_down; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_I0; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_I1; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_O_t0b; // @[Map2S.scala 10:86]
  wire [7:0] other_ops_1_O_t1b; // @[Map2S.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[Map2S.scala 26:83]
  Map2T_14 fst_op ( // @[Map2S.scala 9:22]
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I0(fst_op_I0),
    .I1(fst_op_I1),
    .O_t0b(fst_op_O_t0b),
    .O_t1b(fst_op_O_t1b)
  );
  Map2T_14 other_ops_0 ( // @[Map2S.scala 10:86]
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I0(other_ops_0_I0),
    .I1(other_ops_0_I1),
    .O_t0b(other_ops_0_O_t0b),
    .O_t1b(other_ops_0_O_t1b)
  );
  Map2T_14 other_ops_1 ( // @[Map2S.scala 10:86]
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I0(other_ops_1_I0),
    .I1(other_ops_1_I1),
    .O_t0b(other_ops_1_O_t0b),
    .O_t1b(other_ops_1_O_t1b)
  );
  assign valid_down = _T & other_ops_1_valid_down; // @[Map2S.scala 26:14]
  assign O_0_t0b = fst_op_O_t0b; // @[Map2S.scala 19:8]
  assign O_0_t1b = fst_op_O_t1b; // @[Map2S.scala 19:8]
  assign O_1_t0b = other_ops_0_O_t0b; // @[Map2S.scala 24:12]
  assign O_1_t1b = other_ops_0_O_t1b; // @[Map2S.scala 24:12]
  assign O_2_t0b = other_ops_1_O_t0b; // @[Map2S.scala 24:12]
  assign O_2_t1b = other_ops_1_O_t1b; // @[Map2S.scala 24:12]
  assign fst_op_valid_up = valid_up; // @[Map2S.scala 16:19]
  assign fst_op_I0 = I0_0; // @[Map2S.scala 17:13]
  assign fst_op_I1 = I1_0; // @[Map2S.scala 18:13]
  assign other_ops_0_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_0_I0 = I0_1; // @[Map2S.scala 22:43]
  assign other_ops_0_I1 = I1_1; // @[Map2S.scala 23:43]
  assign other_ops_1_valid_up = valid_up; // @[Map2S.scala 21:39]
  assign other_ops_1_I0 = I0_2; // @[Map2S.scala 22:43]
  assign other_ops_1_I1 = I1_2; // @[Map2S.scala 23:43]
endmodule
module Map2T_15(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0_0,
  input  [7:0] I0_1,
  input  [7:0] I0_2,
  input  [7:0] I1_0,
  input  [7:0] I1_1,
  input  [7:0] I1_2,
  output [7:0] O_0_t0b,
  output [7:0] O_0_t1b,
  output [7:0] O_1_t0b,
  output [7:0] O_1_t1b,
  output [7:0] O_2_t0b,
  output [7:0] O_2_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I0_2; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_1; // @[Map2T.scala 8:20]
  wire [7:0] op_I1_2; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_t0b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_0_t1b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1_t0b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_1_t1b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_2_t0b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_2_t1b; // @[Map2T.scala 8:20]
  Map2S_2 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0_0(op_I0_0),
    .I0_1(op_I0_1),
    .I0_2(op_I0_2),
    .I1_0(op_I1_0),
    .I1_1(op_I1_1),
    .I1_2(op_I1_2),
    .O_0_t0b(op_O_0_t0b),
    .O_0_t1b(op_O_0_t1b),
    .O_1_t0b(op_O_1_t0b),
    .O_1_t1b(op_O_1_t1b),
    .O_2_t0b(op_O_2_t0b),
    .O_2_t1b(op_O_2_t1b)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_0_t0b = op_O_0_t0b; // @[Map2T.scala 17:7]
  assign O_0_t1b = op_O_0_t1b; // @[Map2T.scala 17:7]
  assign O_1_t0b = op_O_1_t0b; // @[Map2T.scala 17:7]
  assign O_1_t1b = op_O_1_t1b; // @[Map2T.scala 17:7]
  assign O_2_t0b = op_O_2_t0b; // @[Map2T.scala 17:7]
  assign O_2_t1b = op_O_2_t1b; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0_0 = I0_0; // @[Map2T.scala 15:11]
  assign op_I0_1 = I0_1; // @[Map2T.scala 15:11]
  assign op_I0_2 = I0_2; // @[Map2T.scala 15:11]
  assign op_I1_0 = I1_0; // @[Map2T.scala 16:11]
  assign op_I1_1 = I1_1; // @[Map2T.scala 16:11]
  assign op_I1_2 = I1_2; // @[Map2T.scala 16:11]
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
module MapT_4(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_t1b; // @[MapT.scala 8:20]
  wire [7:0] op_O; // @[MapT.scala 8:20]
  Mul op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b(op_I_t1b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b = I_t1b; // @[MapT.scala 14:10]
endmodule
module MapS_3(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_t0b,
  input  [7:0] I_0_t1b,
  input  [7:0] I_1_t0b,
  input  [7:0] I_1_t1b,
  input  [7:0] I_2_t0b,
  input  [7:0] I_2_t1b,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_t0b; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I_t1b; // @[MapS.scala 9:22]
  wire [7:0] fst_op_O; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_I_t0b; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_I_t1b; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_O; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_I_t0b; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_I_t1b; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_O; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  MapT_4 fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I_t0b(fst_op_I_t0b),
    .I_t1b(fst_op_I_t1b),
    .O(fst_op_O)
  );
  MapT_4 other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I_t0b(other_ops_0_I_t0b),
    .I_t1b(other_ops_0_I_t1b),
    .O(other_ops_0_O)
  );
  MapT_4 other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I_t0b(other_ops_1_I_t0b),
    .I_t1b(other_ops_1_I_t1b),
    .O(other_ops_1_O)
  );
  assign valid_down = _T & other_ops_1_valid_down; // @[MapS.scala 23:14]
  assign O_0 = fst_op_O; // @[MapS.scala 17:8]
  assign O_1 = other_ops_0_O; // @[MapS.scala 21:12]
  assign O_2 = other_ops_1_O; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I_t0b = I_0_t0b; // @[MapS.scala 16:12]
  assign fst_op_I_t1b = I_0_t1b; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I_t0b = I_1_t0b; // @[MapS.scala 20:41]
  assign other_ops_0_I_t1b = I_1_t1b; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I_t0b = I_2_t0b; // @[MapS.scala 20:41]
  assign other_ops_1_I_t1b = I_2_t1b; // @[MapS.scala 20:41]
endmodule
module MapT_5(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0_t0b,
  input  [7:0] I_0_t1b,
  input  [7:0] I_1_t0b,
  input  [7:0] I_1_t1b,
  input  [7:0] I_2_t0b,
  input  [7:0] I_2_t1b,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_0_t1b; // @[MapT.scala 8:20]
  wire [7:0] op_I_1_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_1_t1b; // @[MapT.scala 8:20]
  wire [7:0] op_I_2_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_2_t1b; // @[MapT.scala 8:20]
  wire [7:0] op_O_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_1; // @[MapT.scala 8:20]
  wire [7:0] op_O_2; // @[MapT.scala 8:20]
  MapS_3 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0_t0b(op_I_0_t0b),
    .I_0_t1b(op_I_0_t1b),
    .I_1_t0b(op_I_1_t0b),
    .I_1_t1b(op_I_1_t1b),
    .I_2_t0b(op_I_2_t0b),
    .I_2_t1b(op_I_2_t1b),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0 = op_O_0; // @[MapT.scala 15:7]
  assign O_1 = op_O_1; // @[MapT.scala 15:7]
  assign O_2 = op_O_2; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0_t0b = I_0_t0b; // @[MapT.scala 14:10]
  assign op_I_0_t1b = I_0_t1b; // @[MapT.scala 14:10]
  assign op_I_1_t0b = I_1_t0b; // @[MapT.scala 14:10]
  assign op_I_1_t1b = I_1_t1b; // @[MapT.scala 14:10]
  assign op_I_2_t0b = I_2_t0b; // @[MapT.scala 14:10]
  assign op_I_2_t1b = I_2_t1b; // @[MapT.scala 14:10]
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
  wire  _T_3 = value == 2'h2; // @[Counter.scala 38:24]
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
  NestedCountersWithNumValid NestedCountersWithNumValid ( // @[ReduceT.scala 22:34]
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
      if (_T_3) begin
        value <= 2'h0;
      end else begin
        value <= _T_5;
      end
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
module MapS_4(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  fst_op_clock; // @[MapS.scala 9:22]
  wire  fst_op_reset; // @[MapS.scala 9:22]
  wire  fst_op_valid_up; // @[MapS.scala 9:22]
  wire  fst_op_valid_down; // @[MapS.scala 9:22]
  wire [7:0] fst_op_I; // @[MapS.scala 9:22]
  wire [7:0] fst_op_O; // @[MapS.scala 9:22]
  wire  other_ops_0_clock; // @[MapS.scala 10:86]
  wire  other_ops_0_reset; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_0_valid_down; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_I; // @[MapS.scala 10:86]
  wire [7:0] other_ops_0_O; // @[MapS.scala 10:86]
  wire  other_ops_1_clock; // @[MapS.scala 10:86]
  wire  other_ops_1_reset; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_up; // @[MapS.scala 10:86]
  wire  other_ops_1_valid_down; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_I; // @[MapS.scala 10:86]
  wire [7:0] other_ops_1_O; // @[MapS.scala 10:86]
  wire  _T = fst_op_valid_down & other_ops_0_valid_down; // @[MapS.scala 23:83]
  ReduceT fst_op ( // @[MapS.scala 9:22]
    .clock(fst_op_clock),
    .reset(fst_op_reset),
    .valid_up(fst_op_valid_up),
    .valid_down(fst_op_valid_down),
    .I(fst_op_I),
    .O(fst_op_O)
  );
  ReduceT other_ops_0 ( // @[MapS.scala 10:86]
    .clock(other_ops_0_clock),
    .reset(other_ops_0_reset),
    .valid_up(other_ops_0_valid_up),
    .valid_down(other_ops_0_valid_down),
    .I(other_ops_0_I),
    .O(other_ops_0_O)
  );
  ReduceT other_ops_1 ( // @[MapS.scala 10:86]
    .clock(other_ops_1_clock),
    .reset(other_ops_1_reset),
    .valid_up(other_ops_1_valid_up),
    .valid_down(other_ops_1_valid_down),
    .I(other_ops_1_I),
    .O(other_ops_1_O)
  );
  assign valid_down = _T & other_ops_1_valid_down; // @[MapS.scala 23:14]
  assign O_0 = fst_op_O; // @[MapS.scala 17:8]
  assign O_1 = other_ops_0_O; // @[MapS.scala 21:12]
  assign O_2 = other_ops_1_O; // @[MapS.scala 21:12]
  assign fst_op_clock = clock;
  assign fst_op_reset = reset;
  assign fst_op_valid_up = valid_up; // @[MapS.scala 15:19]
  assign fst_op_I = I_0; // @[MapS.scala 16:12]
  assign other_ops_0_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_0_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_0_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_0_I = I_1; // @[MapS.scala 20:41]
  assign other_ops_1_clock = clock; // @[MapS.scala 10:86]
  assign other_ops_1_reset = reset; // @[MapS.scala 10:86]
  assign other_ops_1_valid_up = valid_up; // @[MapS.scala 19:39]
  assign other_ops_1_I = I_2; // @[MapS.scala 20:41]
endmodule
module MapT_6(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O_0,
  output [7:0] O_1,
  output [7:0] O_2
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_0; // @[MapT.scala 8:20]
  wire [7:0] op_O_1; // @[MapT.scala 8:20]
  wire [7:0] op_O_2; // @[MapT.scala 8:20]
  MapS_4 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0(op_I_0),
    .I_1(op_I_1),
    .I_2(op_I_2),
    .O_0(op_O_0),
    .O_1(op_O_1),
    .O_2(op_O_2)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O_0 = op_O_0; // @[MapT.scala 15:7]
  assign O_1 = op_O_1; // @[MapT.scala 15:7]
  assign O_2 = op_O_2; // @[MapT.scala 15:7]
  assign op_clock = clock;
  assign op_reset = reset;
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_0 = I_0; // @[MapT.scala 14:10]
  assign op_I_1 = I_1; // @[MapT.scala 14:10]
  assign op_I_2 = I_2; // @[MapT.scala 14:10]
endmodule
module MapTNoValid(
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  wire [7:0] op_I_t0b; // @[MapT.scala 21:20]
  wire [7:0] op_I_t1b; // @[MapT.scala 21:20]
  wire [7:0] op_O; // @[MapT.scala 21:20]
  AddNoValid op ( // @[MapT.scala 21:20]
    .I_t0b(op_I_t0b),
    .I_t1b(op_I_t1b),
    .O(op_O)
  );
  assign O = op_O; // @[MapT.scala 27:7]
  assign op_I_t0b = I_t0b; // @[MapT.scala 26:10]
  assign op_I_t1b = I_t1b; // @[MapT.scala 26:10]
endmodule
module ReduceS(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O_0
);
  wire [7:0] MapTNoValid_I_t0b; // @[ReduceS.scala 20:43]
  wire [7:0] MapTNoValid_I_t1b; // @[ReduceS.scala 20:43]
  wire [7:0] MapTNoValid_O; // @[ReduceS.scala 20:43]
  wire [7:0] MapTNoValid_1_I_t0b; // @[ReduceS.scala 20:43]
  wire [7:0] MapTNoValid_1_I_t1b; // @[ReduceS.scala 20:43]
  wire [7:0] MapTNoValid_1_O; // @[ReduceS.scala 20:43]
  reg [7:0] _T; // @[ReduceS.scala 27:24]
  reg [31:0] _RAND_0;
  reg [7:0] _T_1; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_1;
  reg [7:0] _T_2; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_2;
  reg [7:0] _T_3; // @[ReduceS.scala 43:46]
  reg [31:0] _RAND_3;
  reg  _T_4; // @[ReduceS.scala 47:32]
  reg [31:0] _RAND_4;
  reg  _T_5; // @[ReduceS.scala 47:24]
  reg [31:0] _RAND_5;
  MapTNoValid MapTNoValid ( // @[ReduceS.scala 20:43]
    .I_t0b(MapTNoValid_I_t0b),
    .I_t1b(MapTNoValid_I_t1b),
    .O(MapTNoValid_O)
  );
  MapTNoValid MapTNoValid_1 ( // @[ReduceS.scala 20:43]
    .I_t0b(MapTNoValid_1_I_t0b),
    .I_t1b(MapTNoValid_1_I_t1b),
    .O(MapTNoValid_1_O)
  );
  assign valid_down = _T_5; // @[ReduceS.scala 47:14]
  assign O_0 = _T; // @[ReduceS.scala 27:14]
  assign MapTNoValid_I_t0b = _T_1; // @[ReduceS.scala 43:18]
  assign MapTNoValid_I_t1b = MapTNoValid_1_O; // @[ReduceS.scala 36:18]
  assign MapTNoValid_1_I_t0b = _T_3; // @[ReduceS.scala 43:18]
  assign MapTNoValid_1_I_t1b = _T_2; // @[ReduceS.scala 43:18]
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
  _T_4 = _RAND_4[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_5 = _RAND_5[0:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    _T <= MapTNoValid_O;
    _T_1 <= I_0;
    _T_2 <= I_1;
    _T_3 <= I_2;
    if (reset) begin
      _T_4 <= 1'h0;
    end else begin
      _T_4 <= valid_up;
    end
    _T_5 <= _T_4;
  end
endmodule
module MapT_7(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O_0
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_2; // @[MapT.scala 8:20]
  wire [7:0] op_O_0; // @[MapT.scala 8:20]
  ReduceS op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0(op_I_0),
    .I_1(op_I_1),
    .I_2(op_I_2),
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
endmodule
module ReduceT_1(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  output [7:0] O_0
);
  reg [7:0] undelayed_out_0; // @[ReduceT.scala 17:29]
  reg [31:0] _RAND_0;
  reg  _T_1; // @[ReduceT.scala 18:34]
  reg [31:0] _RAND_1;
  reg  _T_2; // @[ReduceT.scala 18:26]
  reg [31:0] _RAND_2;
  reg [7:0] _T_3_0; // @[ReduceT.scala 56:15]
  reg [31:0] _RAND_3;
  assign valid_down = _T_2; // @[ReduceT.scala 18:16]
  assign O_0 = _T_3_0; // @[ReduceT.scala 56:5]
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
  undelayed_out_0 = _RAND_0[7:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  _T_1 = _RAND_1[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  _T_2 = _RAND_2[0:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  _T_3_0 = _RAND_3[7:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    undelayed_out_0 <= I_0;
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
    _T_3_0 <= undelayed_out_0;
  end
endmodule
module Passthrough_5(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  output [7:0] O
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O = I_0; // @[Passthrough.scala 17:68]
endmodule
module InitialDelayCounter_1(
  input   clock,
  input   reset,
  output  valid_down
);
  reg [3:0] value; // @[InitialDelayCounter.scala 8:34]
  reg [31:0] _RAND_0;
  wire  _T_1 = value < 4'hd; // @[InitialDelayCounter.scala 17:17]
  wire [3:0] _T_4 = value + 4'h1; // @[InitialDelayCounter.scala 17:53]
  assign valid_down = value == 4'hd; // @[InitialDelayCounter.scala 16:16]
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
    end else if (_T_1) begin
      value <= _T_4;
    end
  end
endmodule
module Map2T_17(
  input        valid_up,
  output       valid_down,
  input  [7:0] I0,
  input  [7:0] I1,
  output [7:0] O_t0b,
  output [7:0] O_t1b
);
  wire  op_valid_up; // @[Map2T.scala 8:20]
  wire  op_valid_down; // @[Map2T.scala 8:20]
  wire [7:0] op_I0; // @[Map2T.scala 8:20]
  wire [7:0] op_I1; // @[Map2T.scala 8:20]
  wire [7:0] op_O_t0b; // @[Map2T.scala 8:20]
  wire [7:0] op_O_t1b; // @[Map2T.scala 8:20]
  Map2T_14 op ( // @[Map2T.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I0(op_I0),
    .I1(op_I1),
    .O_t0b(op_O_t0b),
    .O_t1b(op_O_t1b)
  );
  assign valid_down = op_valid_down; // @[Map2T.scala 18:16]
  assign O_t0b = op_O_t0b; // @[Map2T.scala 17:7]
  assign O_t1b = op_O_t1b; // @[Map2T.scala 17:7]
  assign op_valid_up = valid_up; // @[Map2T.scala 14:17]
  assign op_I0 = I0; // @[Map2T.scala 15:11]
  assign op_I1 = I1; // @[Map2T.scala 16:11]
endmodule
module RShift(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  assign valid_down = valid_up; // @[Arithmetic.scala 405:14]
  assign O = I_t0b >> I_t1b; // @[Arithmetic.scala 403:7]
endmodule
module MapT_8(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_t1b; // @[MapT.scala 8:20]
  wire [7:0] op_O; // @[MapT.scala 8:20]
  RShift op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b(op_I_t1b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b = I_t1b; // @[MapT.scala 14:10]
endmodule
module MapT_9(
  input        valid_up,
  output       valid_down,
  input  [7:0] I_t0b,
  input  [7:0] I_t1b,
  output [7:0] O
);
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_t0b; // @[MapT.scala 8:20]
  wire [7:0] op_I_t1b; // @[MapT.scala 8:20]
  wire [7:0] op_O; // @[MapT.scala 8:20]
  MapT_8 op ( // @[MapT.scala 8:20]
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_t0b(op_I_t0b),
    .I_t1b(op_I_t1b),
    .O(op_O)
  );
  assign valid_down = op_valid_down; // @[MapT.scala 16:16]
  assign O = op_O; // @[MapT.scala 15:7]
  assign op_valid_up = valid_up; // @[MapT.scala 13:17]
  assign op_I_t0b = I_t0b; // @[MapT.scala 14:10]
  assign op_I_t1b = I_t1b; // @[MapT.scala 14:10]
endmodule
module Module_0(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O
);
  wire  InitialDelayCounter_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_valid_down; // @[Const.scala 11:33]
  wire  n91_valid_up; // @[Top.scala 18:21]
  wire  n91_valid_down; // @[Top.scala 18:21]
  wire [7:0] n91_I0_0; // @[Top.scala 18:21]
  wire [7:0] n91_I0_1; // @[Top.scala 18:21]
  wire [7:0] n91_I0_2; // @[Top.scala 18:21]
  wire [7:0] n91_I1_0; // @[Top.scala 18:21]
  wire [7:0] n91_I1_1; // @[Top.scala 18:21]
  wire [7:0] n91_I1_2; // @[Top.scala 18:21]
  wire [7:0] n91_O_0_t0b; // @[Top.scala 18:21]
  wire [7:0] n91_O_0_t1b; // @[Top.scala 18:21]
  wire [7:0] n91_O_1_t0b; // @[Top.scala 18:21]
  wire [7:0] n91_O_1_t1b; // @[Top.scala 18:21]
  wire [7:0] n91_O_2_t0b; // @[Top.scala 18:21]
  wire [7:0] n91_O_2_t1b; // @[Top.scala 18:21]
  wire  n107_clock; // @[Top.scala 22:22]
  wire  n107_reset; // @[Top.scala 22:22]
  wire  n107_valid_up; // @[Top.scala 22:22]
  wire  n107_valid_down; // @[Top.scala 22:22]
  wire [7:0] n107_I_0_t0b; // @[Top.scala 22:22]
  wire [7:0] n107_I_0_t1b; // @[Top.scala 22:22]
  wire [7:0] n107_I_1_t0b; // @[Top.scala 22:22]
  wire [7:0] n107_I_1_t1b; // @[Top.scala 22:22]
  wire [7:0] n107_I_2_t0b; // @[Top.scala 22:22]
  wire [7:0] n107_I_2_t1b; // @[Top.scala 22:22]
  wire [7:0] n107_O_0; // @[Top.scala 22:22]
  wire [7:0] n107_O_1; // @[Top.scala 22:22]
  wire [7:0] n107_O_2; // @[Top.scala 22:22]
  wire  n114_clock; // @[Top.scala 25:22]
  wire  n114_reset; // @[Top.scala 25:22]
  wire  n114_valid_up; // @[Top.scala 25:22]
  wire  n114_valid_down; // @[Top.scala 25:22]
  wire [7:0] n114_I_0; // @[Top.scala 25:22]
  wire [7:0] n114_I_1; // @[Top.scala 25:22]
  wire [7:0] n114_I_2; // @[Top.scala 25:22]
  wire [7:0] n114_O_0; // @[Top.scala 25:22]
  wire [7:0] n114_O_1; // @[Top.scala 25:22]
  wire [7:0] n114_O_2; // @[Top.scala 25:22]
  wire  n121_clock; // @[Top.scala 28:22]
  wire  n121_reset; // @[Top.scala 28:22]
  wire  n121_valid_up; // @[Top.scala 28:22]
  wire  n121_valid_down; // @[Top.scala 28:22]
  wire [7:0] n121_I_0; // @[Top.scala 28:22]
  wire [7:0] n121_I_1; // @[Top.scala 28:22]
  wire [7:0] n121_I_2; // @[Top.scala 28:22]
  wire [7:0] n121_O_0; // @[Top.scala 28:22]
  wire  n124_clock; // @[Top.scala 31:22]
  wire  n124_reset; // @[Top.scala 31:22]
  wire  n124_valid_up; // @[Top.scala 31:22]
  wire  n124_valid_down; // @[Top.scala 31:22]
  wire [7:0] n124_I_0; // @[Top.scala 31:22]
  wire [7:0] n124_O_0; // @[Top.scala 31:22]
  wire  n125_valid_up; // @[Top.scala 34:22]
  wire  n125_valid_down; // @[Top.scala 34:22]
  wire [7:0] n125_I_0; // @[Top.scala 34:22]
  wire [7:0] n125_O; // @[Top.scala 34:22]
  wire  InitialDelayCounter_1_clock; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_reset; // @[Const.scala 11:33]
  wire  InitialDelayCounter_1_valid_down; // @[Const.scala 11:33]
  wire  n128_valid_up; // @[Top.scala 38:22]
  wire  n128_valid_down; // @[Top.scala 38:22]
  wire [7:0] n128_I0; // @[Top.scala 38:22]
  wire [7:0] n128_I1; // @[Top.scala 38:22]
  wire [7:0] n128_O_t0b; // @[Top.scala 38:22]
  wire [7:0] n128_O_t1b; // @[Top.scala 38:22]
  wire  n139_valid_up; // @[Top.scala 42:22]
  wire  n139_valid_down; // @[Top.scala 42:22]
  wire [7:0] n139_I_t0b; // @[Top.scala 42:22]
  wire [7:0] n139_I_t1b; // @[Top.scala 42:22]
  wire [7:0] n139_O; // @[Top.scala 42:22]
  reg [1:0] value; // @[Counter.scala 29:33]
  reg [31:0] _RAND_0;
  wire  _T_5 = value == 2'h2; // @[Counter.scala 38:24]
  wire [1:0] _T_7 = value + 2'h1; // @[Counter.scala 39:22]
  wire [7:0] _GEN_6 = 2'h1 == value ? 8'h1 : 8'h0; // @[Const.scala 25:72]
  wire [7:0] _GEN_7 = 2'h1 == value ? 8'h0 : 8'h1; // @[Const.scala 25:72]
  reg [1:0] value_1; // @[Counter.scala 29:33]
  reg [31:0] _RAND_1;
  wire  _T_11 = value_1 == 2'h2; // @[Counter.scala 38:24]
  wire [1:0] _T_13 = value_1 + 2'h1; // @[Counter.scala 39:22]
  wire [7:0] _GEN_16 = 2'h1 == value_1 ? 8'h0 : 8'h2; // @[Const.scala 25:72]
  InitialDelayCounter InitialDelayCounter ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_clock),
    .reset(InitialDelayCounter_reset),
    .valid_down(InitialDelayCounter_valid_down)
  );
  Map2T_15 n91 ( // @[Top.scala 18:21]
    .valid_up(n91_valid_up),
    .valid_down(n91_valid_down),
    .I0_0(n91_I0_0),
    .I0_1(n91_I0_1),
    .I0_2(n91_I0_2),
    .I1_0(n91_I1_0),
    .I1_1(n91_I1_1),
    .I1_2(n91_I1_2),
    .O_0_t0b(n91_O_0_t0b),
    .O_0_t1b(n91_O_0_t1b),
    .O_1_t0b(n91_O_1_t0b),
    .O_1_t1b(n91_O_1_t1b),
    .O_2_t0b(n91_O_2_t0b),
    .O_2_t1b(n91_O_2_t1b)
  );
  MapT_5 n107 ( // @[Top.scala 22:22]
    .clock(n107_clock),
    .reset(n107_reset),
    .valid_up(n107_valid_up),
    .valid_down(n107_valid_down),
    .I_0_t0b(n107_I_0_t0b),
    .I_0_t1b(n107_I_0_t1b),
    .I_1_t0b(n107_I_1_t0b),
    .I_1_t1b(n107_I_1_t1b),
    .I_2_t0b(n107_I_2_t0b),
    .I_2_t1b(n107_I_2_t1b),
    .O_0(n107_O_0),
    .O_1(n107_O_1),
    .O_2(n107_O_2)
  );
  MapT_6 n114 ( // @[Top.scala 25:22]
    .clock(n114_clock),
    .reset(n114_reset),
    .valid_up(n114_valid_up),
    .valid_down(n114_valid_down),
    .I_0(n114_I_0),
    .I_1(n114_I_1),
    .I_2(n114_I_2),
    .O_0(n114_O_0),
    .O_1(n114_O_1),
    .O_2(n114_O_2)
  );
  MapT_7 n121 ( // @[Top.scala 28:22]
    .clock(n121_clock),
    .reset(n121_reset),
    .valid_up(n121_valid_up),
    .valid_down(n121_valid_down),
    .I_0(n121_I_0),
    .I_1(n121_I_1),
    .I_2(n121_I_2),
    .O_0(n121_O_0)
  );
  ReduceT_1 n124 ( // @[Top.scala 31:22]
    .clock(n124_clock),
    .reset(n124_reset),
    .valid_up(n124_valid_up),
    .valid_down(n124_valid_down),
    .I_0(n124_I_0),
    .O_0(n124_O_0)
  );
  Passthrough_5 n125 ( // @[Top.scala 34:22]
    .valid_up(n125_valid_up),
    .valid_down(n125_valid_down),
    .I_0(n125_I_0),
    .O(n125_O)
  );
  InitialDelayCounter_1 InitialDelayCounter_1 ( // @[Const.scala 11:33]
    .clock(InitialDelayCounter_1_clock),
    .reset(InitialDelayCounter_1_reset),
    .valid_down(InitialDelayCounter_1_valid_down)
  );
  Map2T_17 n128 ( // @[Top.scala 38:22]
    .valid_up(n128_valid_up),
    .valid_down(n128_valid_down),
    .I0(n128_I0),
    .I1(n128_I1),
    .O_t0b(n128_O_t0b),
    .O_t1b(n128_O_t1b)
  );
  MapT_9 n139 ( // @[Top.scala 42:22]
    .valid_up(n139_valid_up),
    .valid_down(n139_valid_down),
    .I_t0b(n139_I_t0b),
    .I_t1b(n139_I_t1b),
    .O(n139_O)
  );
  assign valid_down = n139_valid_down; // @[Top.scala 46:16]
  assign O = n139_O; // @[Top.scala 45:7]
  assign InitialDelayCounter_clock = clock;
  assign InitialDelayCounter_reset = reset;
  assign n91_valid_up = valid_up & InitialDelayCounter_valid_down; // @[Top.scala 21:18]
  assign n91_I0_0 = I_0; // @[Top.scala 19:12]
  assign n91_I0_1 = I_1; // @[Top.scala 19:12]
  assign n91_I0_2 = I_2; // @[Top.scala 19:12]
  assign n91_I1_0 = 2'h2 == value ? 8'h0 : _GEN_6; // @[Top.scala 20:12]
  assign n91_I1_1 = 2'h2 == value ? 8'h1 : _GEN_7; // @[Top.scala 20:12]
  assign n91_I1_2 = 2'h2 == value ? 8'h0 : _GEN_6; // @[Top.scala 20:12]
  assign n107_clock = clock;
  assign n107_reset = reset;
  assign n107_valid_up = n91_valid_down; // @[Top.scala 24:19]
  assign n107_I_0_t0b = n91_O_0_t0b; // @[Top.scala 23:12]
  assign n107_I_0_t1b = n91_O_0_t1b; // @[Top.scala 23:12]
  assign n107_I_1_t0b = n91_O_1_t0b; // @[Top.scala 23:12]
  assign n107_I_1_t1b = n91_O_1_t1b; // @[Top.scala 23:12]
  assign n107_I_2_t0b = n91_O_2_t0b; // @[Top.scala 23:12]
  assign n107_I_2_t1b = n91_O_2_t1b; // @[Top.scala 23:12]
  assign n114_clock = clock;
  assign n114_reset = reset;
  assign n114_valid_up = n107_valid_down; // @[Top.scala 27:19]
  assign n114_I_0 = n107_O_0; // @[Top.scala 26:12]
  assign n114_I_1 = n107_O_1; // @[Top.scala 26:12]
  assign n114_I_2 = n107_O_2; // @[Top.scala 26:12]
  assign n121_clock = clock;
  assign n121_reset = reset;
  assign n121_valid_up = n114_valid_down; // @[Top.scala 30:19]
  assign n121_I_0 = n114_O_0; // @[Top.scala 29:12]
  assign n121_I_1 = n114_O_1; // @[Top.scala 29:12]
  assign n121_I_2 = n114_O_2; // @[Top.scala 29:12]
  assign n124_clock = clock;
  assign n124_reset = reset;
  assign n124_valid_up = n121_valid_down; // @[Top.scala 33:19]
  assign n124_I_0 = n121_O_0; // @[Top.scala 32:12]
  assign n125_valid_up = n124_valid_down; // @[Top.scala 36:19]
  assign n125_I_0 = n124_O_0; // @[Top.scala 35:12]
  assign InitialDelayCounter_1_clock = clock;
  assign InitialDelayCounter_1_reset = reset;
  assign n128_valid_up = n125_valid_down & InitialDelayCounter_1_valid_down; // @[Top.scala 41:19]
  assign n128_I0 = n125_O; // @[Top.scala 39:13]
  assign n128_I1 = 2'h2 == value_1 ? 8'h0 : _GEN_16; // @[Top.scala 40:13]
  assign n139_valid_up = n128_valid_down; // @[Top.scala 44:19]
  assign n139_I_t0b = n128_O_t0b; // @[Top.scala 43:12]
  assign n139_I_t1b = n128_O_t1b; // @[Top.scala 43:12]
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
    end else if (InitialDelayCounter_valid_down) begin
      if (_T_5) begin
        value <= 2'h0;
      end else begin
        value <= _T_7;
      end
    end
    if (reset) begin
      value_1 <= 2'h0;
    end else if (InitialDelayCounter_1_valid_down) begin
      if (_T_11) begin
        value_1 <= 2'h0;
      end else begin
        value_1 <= _T_13;
      end
    end
  end
endmodule
module MapT_10(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I_0,
  input  [7:0] I_1,
  input  [7:0] I_2,
  output [7:0] O
);
  wire  op_clock; // @[MapT.scala 8:20]
  wire  op_reset; // @[MapT.scala 8:20]
  wire  op_valid_up; // @[MapT.scala 8:20]
  wire  op_valid_down; // @[MapT.scala 8:20]
  wire [7:0] op_I_0; // @[MapT.scala 8:20]
  wire [7:0] op_I_1; // @[MapT.scala 8:20]
  wire [7:0] op_I_2; // @[MapT.scala 8:20]
  wire [7:0] op_O; // @[MapT.scala 8:20]
  Module_0 op ( // @[MapT.scala 8:20]
    .clock(op_clock),
    .reset(op_reset),
    .valid_up(op_valid_up),
    .valid_down(op_valid_down),
    .I_0(op_I_0),
    .I_1(op_I_1),
    .I_2(op_I_2),
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
endmodule
module Passthrough_6(
  input        valid_up,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  assign valid_down = valid_up; // @[Passthrough.scala 18:14]
  assign O = I; // @[Passthrough.scala 17:68]
endmodule
module Top(
  input        clock,
  input        reset,
  input        valid_up,
  output       valid_down,
  input  [7:0] I,
  output [7:0] O
);
  wire  n1_clock; // @[Top.scala 52:20]
  wire  n1_reset; // @[Top.scala 52:20]
  wire  n1_valid_up; // @[Top.scala 52:20]
  wire  n1_valid_down; // @[Top.scala 52:20]
  wire [7:0] n1_I; // @[Top.scala 52:20]
  wire [7:0] n1_O; // @[Top.scala 52:20]
  wire  n2_clock; // @[Top.scala 55:20]
  wire  n2_reset; // @[Top.scala 55:20]
  wire  n2_valid_up; // @[Top.scala 55:20]
  wire  n2_valid_down; // @[Top.scala 55:20]
  wire [7:0] n2_I; // @[Top.scala 55:20]
  wire [7:0] n2_O; // @[Top.scala 55:20]
  wire  n3_clock; // @[Top.scala 58:20]
  wire  n3_reset; // @[Top.scala 58:20]
  wire  n3_valid_up; // @[Top.scala 58:20]
  wire  n3_valid_down; // @[Top.scala 58:20]
  wire [7:0] n3_I; // @[Top.scala 58:20]
  wire [7:0] n3_O; // @[Top.scala 58:20]
  wire  n4_clock; // @[Top.scala 61:20]
  wire  n4_reset; // @[Top.scala 61:20]
  wire  n4_valid_up; // @[Top.scala 61:20]
  wire  n4_valid_down; // @[Top.scala 61:20]
  wire [7:0] n4_I; // @[Top.scala 61:20]
  wire [7:0] n4_O; // @[Top.scala 61:20]
  wire  n5_clock; // @[Top.scala 64:20]
  wire  n5_reset; // @[Top.scala 64:20]
  wire  n5_valid_up; // @[Top.scala 64:20]
  wire  n5_valid_down; // @[Top.scala 64:20]
  wire [7:0] n5_I; // @[Top.scala 64:20]
  wire [7:0] n5_O; // @[Top.scala 64:20]
  wire  n6_valid_up; // @[Top.scala 67:20]
  wire  n6_valid_down; // @[Top.scala 67:20]
  wire [7:0] n6_I0; // @[Top.scala 67:20]
  wire [7:0] n6_I1; // @[Top.scala 67:20]
  wire [7:0] n6_O_0; // @[Top.scala 67:20]
  wire [7:0] n6_O_1; // @[Top.scala 67:20]
  wire  n13_valid_up; // @[Top.scala 71:21]
  wire  n13_valid_down; // @[Top.scala 71:21]
  wire [7:0] n13_I0_0; // @[Top.scala 71:21]
  wire [7:0] n13_I0_1; // @[Top.scala 71:21]
  wire [7:0] n13_I1; // @[Top.scala 71:21]
  wire [7:0] n13_O_0; // @[Top.scala 71:21]
  wire [7:0] n13_O_1; // @[Top.scala 71:21]
  wire [7:0] n13_O_2; // @[Top.scala 71:21]
  wire  n20_valid_up; // @[Top.scala 75:21]
  wire  n20_valid_down; // @[Top.scala 75:21]
  wire [7:0] n20_I_0; // @[Top.scala 75:21]
  wire [7:0] n20_I_1; // @[Top.scala 75:21]
  wire [7:0] n20_I_2; // @[Top.scala 75:21]
  wire [7:0] n20_O_0_0; // @[Top.scala 75:21]
  wire [7:0] n20_O_0_1; // @[Top.scala 75:21]
  wire [7:0] n20_O_0_2; // @[Top.scala 75:21]
  wire  n25_clock; // @[Top.scala 78:21]
  wire  n25_reset; // @[Top.scala 78:21]
  wire  n25_valid_up; // @[Top.scala 78:21]
  wire  n25_valid_down; // @[Top.scala 78:21]
  wire [7:0] n25_I_0_0; // @[Top.scala 78:21]
  wire [7:0] n25_I_0_1; // @[Top.scala 78:21]
  wire [7:0] n25_I_0_2; // @[Top.scala 78:21]
  wire [7:0] n25_O_0; // @[Top.scala 78:21]
  wire  n26_clock; // @[Top.scala 81:21]
  wire  n26_reset; // @[Top.scala 81:21]
  wire  n26_valid_up; // @[Top.scala 81:21]
  wire  n26_valid_down; // @[Top.scala 81:21]
  wire [7:0] n26_I; // @[Top.scala 81:21]
  wire [7:0] n26_O; // @[Top.scala 81:21]
  wire  n27_clock; // @[Top.scala 84:21]
  wire  n27_reset; // @[Top.scala 84:21]
  wire  n27_valid_up; // @[Top.scala 84:21]
  wire  n27_valid_down; // @[Top.scala 84:21]
  wire [7:0] n27_I; // @[Top.scala 84:21]
  wire [7:0] n27_O; // @[Top.scala 84:21]
  wire  n28_valid_up; // @[Top.scala 87:21]
  wire  n28_valid_down; // @[Top.scala 87:21]
  wire [7:0] n28_I0; // @[Top.scala 87:21]
  wire [7:0] n28_I1; // @[Top.scala 87:21]
  wire [7:0] n28_O_0; // @[Top.scala 87:21]
  wire [7:0] n28_O_1; // @[Top.scala 87:21]
  wire  n35_valid_up; // @[Top.scala 91:21]
  wire  n35_valid_down; // @[Top.scala 91:21]
  wire [7:0] n35_I0_0; // @[Top.scala 91:21]
  wire [7:0] n35_I0_1; // @[Top.scala 91:21]
  wire [7:0] n35_I1; // @[Top.scala 91:21]
  wire [7:0] n35_O_0; // @[Top.scala 91:21]
  wire [7:0] n35_O_1; // @[Top.scala 91:21]
  wire [7:0] n35_O_2; // @[Top.scala 91:21]
  wire  n42_valid_up; // @[Top.scala 95:21]
  wire  n42_valid_down; // @[Top.scala 95:21]
  wire [7:0] n42_I_0; // @[Top.scala 95:21]
  wire [7:0] n42_I_1; // @[Top.scala 95:21]
  wire [7:0] n42_I_2; // @[Top.scala 95:21]
  wire [7:0] n42_O_0_0; // @[Top.scala 95:21]
  wire [7:0] n42_O_0_1; // @[Top.scala 95:21]
  wire [7:0] n42_O_0_2; // @[Top.scala 95:21]
  wire  n47_clock; // @[Top.scala 98:21]
  wire  n47_reset; // @[Top.scala 98:21]
  wire  n47_valid_up; // @[Top.scala 98:21]
  wire  n47_valid_down; // @[Top.scala 98:21]
  wire [7:0] n47_I_0_0; // @[Top.scala 98:21]
  wire [7:0] n47_I_0_1; // @[Top.scala 98:21]
  wire [7:0] n47_I_0_2; // @[Top.scala 98:21]
  wire [7:0] n47_O_0; // @[Top.scala 98:21]
  wire  n48_valid_up; // @[Top.scala 101:21]
  wire  n48_valid_down; // @[Top.scala 101:21]
  wire [7:0] n48_I0_0; // @[Top.scala 101:21]
  wire [7:0] n48_I1_0; // @[Top.scala 101:21]
  wire [7:0] n48_O_0_0; // @[Top.scala 101:21]
  wire [7:0] n48_O_0_1; // @[Top.scala 101:21]
  wire  n55_clock; // @[Top.scala 105:21]
  wire  n55_reset; // @[Top.scala 105:21]
  wire  n55_valid_up; // @[Top.scala 105:21]
  wire  n55_valid_down; // @[Top.scala 105:21]
  wire [7:0] n55_I; // @[Top.scala 105:21]
  wire [7:0] n55_O; // @[Top.scala 105:21]
  wire  n56_clock; // @[Top.scala 108:21]
  wire  n56_reset; // @[Top.scala 108:21]
  wire  n56_valid_up; // @[Top.scala 108:21]
  wire  n56_valid_down; // @[Top.scala 108:21]
  wire [7:0] n56_I; // @[Top.scala 108:21]
  wire [7:0] n56_O; // @[Top.scala 108:21]
  wire  n57_valid_up; // @[Top.scala 111:21]
  wire  n57_valid_down; // @[Top.scala 111:21]
  wire [7:0] n57_I0; // @[Top.scala 111:21]
  wire [7:0] n57_I1; // @[Top.scala 111:21]
  wire [7:0] n57_O_0; // @[Top.scala 111:21]
  wire [7:0] n57_O_1; // @[Top.scala 111:21]
  wire  n64_valid_up; // @[Top.scala 115:21]
  wire  n64_valid_down; // @[Top.scala 115:21]
  wire [7:0] n64_I0_0; // @[Top.scala 115:21]
  wire [7:0] n64_I0_1; // @[Top.scala 115:21]
  wire [7:0] n64_I1; // @[Top.scala 115:21]
  wire [7:0] n64_O_0; // @[Top.scala 115:21]
  wire [7:0] n64_O_1; // @[Top.scala 115:21]
  wire [7:0] n64_O_2; // @[Top.scala 115:21]
  wire  n71_valid_up; // @[Top.scala 119:21]
  wire  n71_valid_down; // @[Top.scala 119:21]
  wire [7:0] n71_I_0; // @[Top.scala 119:21]
  wire [7:0] n71_I_1; // @[Top.scala 119:21]
  wire [7:0] n71_I_2; // @[Top.scala 119:21]
  wire [7:0] n71_O_0_0; // @[Top.scala 119:21]
  wire [7:0] n71_O_0_1; // @[Top.scala 119:21]
  wire [7:0] n71_O_0_2; // @[Top.scala 119:21]
  wire  n76_clock; // @[Top.scala 122:21]
  wire  n76_reset; // @[Top.scala 122:21]
  wire  n76_valid_up; // @[Top.scala 122:21]
  wire  n76_valid_down; // @[Top.scala 122:21]
  wire [7:0] n76_I_0_0; // @[Top.scala 122:21]
  wire [7:0] n76_I_0_1; // @[Top.scala 122:21]
  wire [7:0] n76_I_0_2; // @[Top.scala 122:21]
  wire [7:0] n76_O_0; // @[Top.scala 122:21]
  wire  n77_valid_up; // @[Top.scala 125:21]
  wire  n77_valid_down; // @[Top.scala 125:21]
  wire [7:0] n77_I0_0_0; // @[Top.scala 125:21]
  wire [7:0] n77_I0_0_1; // @[Top.scala 125:21]
  wire [7:0] n77_I1_0; // @[Top.scala 125:21]
  wire [7:0] n77_O_0_0; // @[Top.scala 125:21]
  wire [7:0] n77_O_0_1; // @[Top.scala 125:21]
  wire [7:0] n77_O_0_2; // @[Top.scala 125:21]
  wire  n84_valid_up; // @[Top.scala 129:21]
  wire  n84_valid_down; // @[Top.scala 129:21]
  wire [7:0] n84_I_0_0; // @[Top.scala 129:21]
  wire [7:0] n84_I_0_1; // @[Top.scala 129:21]
  wire [7:0] n84_I_0_2; // @[Top.scala 129:21]
  wire [7:0] n84_O_0_0; // @[Top.scala 129:21]
  wire [7:0] n84_O_0_1; // @[Top.scala 129:21]
  wire [7:0] n84_O_0_2; // @[Top.scala 129:21]
  wire  n87_valid_up; // @[Top.scala 132:21]
  wire  n87_valid_down; // @[Top.scala 132:21]
  wire [7:0] n87_I_0_0; // @[Top.scala 132:21]
  wire [7:0] n87_I_0_1; // @[Top.scala 132:21]
  wire [7:0] n87_I_0_2; // @[Top.scala 132:21]
  wire [7:0] n87_O_0; // @[Top.scala 132:21]
  wire [7:0] n87_O_1; // @[Top.scala 132:21]
  wire [7:0] n87_O_2; // @[Top.scala 132:21]
  wire  n140_clock; // @[Top.scala 135:22]
  wire  n140_reset; // @[Top.scala 135:22]
  wire  n140_valid_up; // @[Top.scala 135:22]
  wire  n140_valid_down; // @[Top.scala 135:22]
  wire [7:0] n140_I_0; // @[Top.scala 135:22]
  wire [7:0] n140_I_1; // @[Top.scala 135:22]
  wire [7:0] n140_I_2; // @[Top.scala 135:22]
  wire [7:0] n140_O; // @[Top.scala 135:22]
  wire  n141_valid_up; // @[Top.scala 138:22]
  wire  n141_valid_down; // @[Top.scala 138:22]
  wire [7:0] n141_I; // @[Top.scala 138:22]
  wire [7:0] n141_O; // @[Top.scala 138:22]
  wire  n142_clock; // @[Top.scala 141:22]
  wire  n142_reset; // @[Top.scala 141:22]
  wire  n142_valid_up; // @[Top.scala 141:22]
  wire  n142_valid_down; // @[Top.scala 141:22]
  wire [7:0] n142_I; // @[Top.scala 141:22]
  wire [7:0] n142_O; // @[Top.scala 141:22]
  wire  n143_clock; // @[Top.scala 144:22]
  wire  n143_reset; // @[Top.scala 144:22]
  wire  n143_valid_up; // @[Top.scala 144:22]
  wire  n143_valid_down; // @[Top.scala 144:22]
  wire [7:0] n143_I; // @[Top.scala 144:22]
  wire [7:0] n143_O; // @[Top.scala 144:22]
  wire  n144_clock; // @[Top.scala 147:22]
  wire  n144_reset; // @[Top.scala 147:22]
  wire  n144_valid_up; // @[Top.scala 147:22]
  wire  n144_valid_down; // @[Top.scala 147:22]
  wire [7:0] n144_I; // @[Top.scala 147:22]
  wire [7:0] n144_O; // @[Top.scala 147:22]
  FIFO n1 ( // @[Top.scala 52:20]
    .clock(n1_clock),
    .reset(n1_reset),
    .valid_up(n1_valid_up),
    .valid_down(n1_valid_down),
    .I(n1_I),
    .O(n1_O)
  );
  ShiftTN n2 ( // @[Top.scala 55:20]
    .clock(n2_clock),
    .reset(n2_reset),
    .valid_up(n2_valid_up),
    .valid_down(n2_valid_down),
    .I(n2_I),
    .O(n2_O)
  );
  ShiftTN n3 ( // @[Top.scala 58:20]
    .clock(n3_clock),
    .reset(n3_reset),
    .valid_up(n3_valid_up),
    .valid_down(n3_valid_down),
    .I(n3_I),
    .O(n3_O)
  );
  ShiftTN_2 n4 ( // @[Top.scala 61:20]
    .clock(n4_clock),
    .reset(n4_reset),
    .valid_up(n4_valid_up),
    .valid_down(n4_valid_down),
    .I(n4_I),
    .O(n4_O)
  );
  ShiftTN_2 n5 ( // @[Top.scala 64:20]
    .clock(n5_clock),
    .reset(n5_reset),
    .valid_up(n5_valid_up),
    .valid_down(n5_valid_down),
    .I(n5_I),
    .O(n5_O)
  );
  Map2T_1 n6 ( // @[Top.scala 67:20]
    .valid_up(n6_valid_up),
    .valid_down(n6_valid_down),
    .I0(n6_I0),
    .I1(n6_I1),
    .O_0(n6_O_0),
    .O_1(n6_O_1)
  );
  Map2T_3 n13 ( // @[Top.scala 71:21]
    .valid_up(n13_valid_up),
    .valid_down(n13_valid_down),
    .I0_0(n13_I0_0),
    .I0_1(n13_I0_1),
    .I1(n13_I1),
    .O_0(n13_O_0),
    .O_1(n13_O_1),
    .O_2(n13_O_2)
  );
  Passthrough n20 ( // @[Top.scala 75:21]
    .valid_up(n20_valid_up),
    .valid_down(n20_valid_down),
    .I_0(n20_I_0),
    .I_1(n20_I_1),
    .I_2(n20_I_2),
    .O_0_0(n20_O_0_0),
    .O_0_1(n20_O_0_1),
    .O_0_2(n20_O_0_2)
  );
  MapT n25 ( // @[Top.scala 78:21]
    .clock(n25_clock),
    .reset(n25_reset),
    .valid_up(n25_valid_up),
    .valid_down(n25_valid_down),
    .I_0_0(n25_I_0_0),
    .I_0_1(n25_I_0_1),
    .I_0_2(n25_I_0_2),
    .O_0(n25_O_0)
  );
  ShiftTN_2 n26 ( // @[Top.scala 81:21]
    .clock(n26_clock),
    .reset(n26_reset),
    .valid_up(n26_valid_up),
    .valid_down(n26_valid_down),
    .I(n26_I),
    .O(n26_O)
  );
  ShiftTN_2 n27 ( // @[Top.scala 84:21]
    .clock(n27_clock),
    .reset(n27_reset),
    .valid_up(n27_valid_up),
    .valid_down(n27_valid_down),
    .I(n27_I),
    .O(n27_O)
  );
  Map2T_1 n28 ( // @[Top.scala 87:21]
    .valid_up(n28_valid_up),
    .valid_down(n28_valid_down),
    .I0(n28_I0),
    .I1(n28_I1),
    .O_0(n28_O_0),
    .O_1(n28_O_1)
  );
  Map2T_3 n35 ( // @[Top.scala 91:21]
    .valid_up(n35_valid_up),
    .valid_down(n35_valid_down),
    .I0_0(n35_I0_0),
    .I0_1(n35_I0_1),
    .I1(n35_I1),
    .O_0(n35_O_0),
    .O_1(n35_O_1),
    .O_2(n35_O_2)
  );
  Passthrough n42 ( // @[Top.scala 95:21]
    .valid_up(n42_valid_up),
    .valid_down(n42_valid_down),
    .I_0(n42_I_0),
    .I_1(n42_I_1),
    .I_2(n42_I_2),
    .O_0_0(n42_O_0_0),
    .O_0_1(n42_O_0_1),
    .O_0_2(n42_O_0_2)
  );
  MapT n47 ( // @[Top.scala 98:21]
    .clock(n47_clock),
    .reset(n47_reset),
    .valid_up(n47_valid_up),
    .valid_down(n47_valid_down),
    .I_0_0(n47_I_0_0),
    .I_0_1(n47_I_0_1),
    .I_0_2(n47_I_0_2),
    .O_0(n47_O_0)
  );
  Map2T_8 n48 ( // @[Top.scala 101:21]
    .valid_up(n48_valid_up),
    .valid_down(n48_valid_down),
    .I0_0(n48_I0_0),
    .I1_0(n48_I1_0),
    .O_0_0(n48_O_0_0),
    .O_0_1(n48_O_0_1)
  );
  ShiftTN_2 n55 ( // @[Top.scala 105:21]
    .clock(n55_clock),
    .reset(n55_reset),
    .valid_up(n55_valid_up),
    .valid_down(n55_valid_down),
    .I(n55_I),
    .O(n55_O)
  );
  ShiftTN_2 n56 ( // @[Top.scala 108:21]
    .clock(n56_clock),
    .reset(n56_reset),
    .valid_up(n56_valid_up),
    .valid_down(n56_valid_down),
    .I(n56_I),
    .O(n56_O)
  );
  Map2T_1 n57 ( // @[Top.scala 111:21]
    .valid_up(n57_valid_up),
    .valid_down(n57_valid_down),
    .I0(n57_I0),
    .I1(n57_I1),
    .O_0(n57_O_0),
    .O_1(n57_O_1)
  );
  Map2T_3 n64 ( // @[Top.scala 115:21]
    .valid_up(n64_valid_up),
    .valid_down(n64_valid_down),
    .I0_0(n64_I0_0),
    .I0_1(n64_I0_1),
    .I1(n64_I1),
    .O_0(n64_O_0),
    .O_1(n64_O_1),
    .O_2(n64_O_2)
  );
  Passthrough n71 ( // @[Top.scala 119:21]
    .valid_up(n71_valid_up),
    .valid_down(n71_valid_down),
    .I_0(n71_I_0),
    .I_1(n71_I_1),
    .I_2(n71_I_2),
    .O_0_0(n71_O_0_0),
    .O_0_1(n71_O_0_1),
    .O_0_2(n71_O_0_2)
  );
  MapT n76 ( // @[Top.scala 122:21]
    .clock(n76_clock),
    .reset(n76_reset),
    .valid_up(n76_valid_up),
    .valid_down(n76_valid_down),
    .I_0_0(n76_I_0_0),
    .I_0_1(n76_I_0_1),
    .I_0_2(n76_I_0_2),
    .O_0(n76_O_0)
  );
  Map2T_13 n77 ( // @[Top.scala 125:21]
    .valid_up(n77_valid_up),
    .valid_down(n77_valid_down),
    .I0_0_0(n77_I0_0_0),
    .I0_0_1(n77_I0_0_1),
    .I1_0(n77_I1_0),
    .O_0_0(n77_O_0_0),
    .O_0_1(n77_O_0_1),
    .O_0_2(n77_O_0_2)
  );
  Passthrough_3 n84 ( // @[Top.scala 129:21]
    .valid_up(n84_valid_up),
    .valid_down(n84_valid_down),
    .I_0_0(n84_I_0_0),
    .I_0_1(n84_I_0_1),
    .I_0_2(n84_I_0_2),
    .O_0_0(n84_O_0_0),
    .O_0_1(n84_O_0_1),
    .O_0_2(n84_O_0_2)
  );
  MapT_3 n87 ( // @[Top.scala 132:21]
    .valid_up(n87_valid_up),
    .valid_down(n87_valid_down),
    .I_0_0(n87_I_0_0),
    .I_0_1(n87_I_0_1),
    .I_0_2(n87_I_0_2),
    .O_0(n87_O_0),
    .O_1(n87_O_1),
    .O_2(n87_O_2)
  );
  MapT_10 n140 ( // @[Top.scala 135:22]
    .clock(n140_clock),
    .reset(n140_reset),
    .valid_up(n140_valid_up),
    .valid_down(n140_valid_down),
    .I_0(n140_I_0),
    .I_1(n140_I_1),
    .I_2(n140_I_2),
    .O(n140_O)
  );
  Passthrough_6 n141 ( // @[Top.scala 138:22]
    .valid_up(n141_valid_up),
    .valid_down(n141_valid_down),
    .I(n141_I),
    .O(n141_O)
  );
  FIFO n142 ( // @[Top.scala 141:22]
    .clock(n142_clock),
    .reset(n142_reset),
    .valid_up(n142_valid_up),
    .valid_down(n142_valid_down),
    .I(n142_I),
    .O(n142_O)
  );
  FIFO n143 ( // @[Top.scala 144:22]
    .clock(n143_clock),
    .reset(n143_reset),
    .valid_up(n143_valid_up),
    .valid_down(n143_valid_down),
    .I(n143_I),
    .O(n143_O)
  );
  FIFO n144 ( // @[Top.scala 147:22]
    .clock(n144_clock),
    .reset(n144_reset),
    .valid_up(n144_valid_up),
    .valid_down(n144_valid_down),
    .I(n144_I),
    .O(n144_O)
  );
  assign valid_down = n144_valid_down; // @[Top.scala 151:16]
  assign O = n144_O; // @[Top.scala 150:7]
  assign n1_clock = clock;
  assign n1_reset = reset;
  assign n1_valid_up = valid_up; // @[Top.scala 54:17]
  assign n1_I = I; // @[Top.scala 53:10]
  assign n2_clock = clock;
  assign n2_reset = reset;
  assign n2_valid_up = n1_valid_down; // @[Top.scala 57:17]
  assign n2_I = n1_O; // @[Top.scala 56:10]
  assign n3_clock = clock;
  assign n3_reset = reset;
  assign n3_valid_up = n2_valid_down; // @[Top.scala 60:17]
  assign n3_I = n2_O; // @[Top.scala 59:10]
  assign n4_clock = clock;
  assign n4_reset = reset;
  assign n4_valid_up = n3_valid_down; // @[Top.scala 63:17]
  assign n4_I = n3_O; // @[Top.scala 62:10]
  assign n5_clock = clock;
  assign n5_reset = reset;
  assign n5_valid_up = n4_valid_down; // @[Top.scala 66:17]
  assign n5_I = n4_O; // @[Top.scala 65:10]
  assign n6_valid_up = n5_valid_down & n4_valid_down; // @[Top.scala 70:17]
  assign n6_I0 = n5_O; // @[Top.scala 68:11]
  assign n6_I1 = n4_O; // @[Top.scala 69:11]
  assign n13_valid_up = n6_valid_down & n3_valid_down; // @[Top.scala 74:18]
  assign n13_I0_0 = n6_O_0; // @[Top.scala 72:12]
  assign n13_I0_1 = n6_O_1; // @[Top.scala 72:12]
  assign n13_I1 = n3_O; // @[Top.scala 73:12]
  assign n20_valid_up = n13_valid_down; // @[Top.scala 77:18]
  assign n20_I_0 = n13_O_0; // @[Top.scala 76:11]
  assign n20_I_1 = n13_O_1; // @[Top.scala 76:11]
  assign n20_I_2 = n13_O_2; // @[Top.scala 76:11]
  assign n25_clock = clock;
  assign n25_reset = reset;
  assign n25_valid_up = n20_valid_down; // @[Top.scala 80:18]
  assign n25_I_0_0 = n20_O_0_0; // @[Top.scala 79:11]
  assign n25_I_0_1 = n20_O_0_1; // @[Top.scala 79:11]
  assign n25_I_0_2 = n20_O_0_2; // @[Top.scala 79:11]
  assign n26_clock = clock;
  assign n26_reset = reset;
  assign n26_valid_up = n2_valid_down; // @[Top.scala 83:18]
  assign n26_I = n2_O; // @[Top.scala 82:11]
  assign n27_clock = clock;
  assign n27_reset = reset;
  assign n27_valid_up = n26_valid_down; // @[Top.scala 86:18]
  assign n27_I = n26_O; // @[Top.scala 85:11]
  assign n28_valid_up = n27_valid_down & n26_valid_down; // @[Top.scala 90:18]
  assign n28_I0 = n27_O; // @[Top.scala 88:12]
  assign n28_I1 = n26_O; // @[Top.scala 89:12]
  assign n35_valid_up = n28_valid_down & n2_valid_down; // @[Top.scala 94:18]
  assign n35_I0_0 = n28_O_0; // @[Top.scala 92:12]
  assign n35_I0_1 = n28_O_1; // @[Top.scala 92:12]
  assign n35_I1 = n2_O; // @[Top.scala 93:12]
  assign n42_valid_up = n35_valid_down; // @[Top.scala 97:18]
  assign n42_I_0 = n35_O_0; // @[Top.scala 96:11]
  assign n42_I_1 = n35_O_1; // @[Top.scala 96:11]
  assign n42_I_2 = n35_O_2; // @[Top.scala 96:11]
  assign n47_clock = clock;
  assign n47_reset = reset;
  assign n47_valid_up = n42_valid_down; // @[Top.scala 100:18]
  assign n47_I_0_0 = n42_O_0_0; // @[Top.scala 99:11]
  assign n47_I_0_1 = n42_O_0_1; // @[Top.scala 99:11]
  assign n47_I_0_2 = n42_O_0_2; // @[Top.scala 99:11]
  assign n48_valid_up = n25_valid_down & n47_valid_down; // @[Top.scala 104:18]
  assign n48_I0_0 = n25_O_0; // @[Top.scala 102:12]
  assign n48_I1_0 = n47_O_0; // @[Top.scala 103:12]
  assign n55_clock = clock;
  assign n55_reset = reset;
  assign n55_valid_up = n1_valid_down; // @[Top.scala 107:18]
  assign n55_I = n1_O; // @[Top.scala 106:11]
  assign n56_clock = clock;
  assign n56_reset = reset;
  assign n56_valid_up = n55_valid_down; // @[Top.scala 110:18]
  assign n56_I = n55_O; // @[Top.scala 109:11]
  assign n57_valid_up = n56_valid_down & n55_valid_down; // @[Top.scala 114:18]
  assign n57_I0 = n56_O; // @[Top.scala 112:12]
  assign n57_I1 = n55_O; // @[Top.scala 113:12]
  assign n64_valid_up = n57_valid_down & n1_valid_down; // @[Top.scala 118:18]
  assign n64_I0_0 = n57_O_0; // @[Top.scala 116:12]
  assign n64_I0_1 = n57_O_1; // @[Top.scala 116:12]
  assign n64_I1 = n1_O; // @[Top.scala 117:12]
  assign n71_valid_up = n64_valid_down; // @[Top.scala 121:18]
  assign n71_I_0 = n64_O_0; // @[Top.scala 120:11]
  assign n71_I_1 = n64_O_1; // @[Top.scala 120:11]
  assign n71_I_2 = n64_O_2; // @[Top.scala 120:11]
  assign n76_clock = clock;
  assign n76_reset = reset;
  assign n76_valid_up = n71_valid_down; // @[Top.scala 124:18]
  assign n76_I_0_0 = n71_O_0_0; // @[Top.scala 123:11]
  assign n76_I_0_1 = n71_O_0_1; // @[Top.scala 123:11]
  assign n76_I_0_2 = n71_O_0_2; // @[Top.scala 123:11]
  assign n77_valid_up = n48_valid_down & n76_valid_down; // @[Top.scala 128:18]
  assign n77_I0_0_0 = n48_O_0_0; // @[Top.scala 126:12]
  assign n77_I0_0_1 = n48_O_0_1; // @[Top.scala 126:12]
  assign n77_I1_0 = n76_O_0; // @[Top.scala 127:12]
  assign n84_valid_up = n77_valid_down; // @[Top.scala 131:18]
  assign n84_I_0_0 = n77_O_0_0; // @[Top.scala 130:11]
  assign n84_I_0_1 = n77_O_0_1; // @[Top.scala 130:11]
  assign n84_I_0_2 = n77_O_0_2; // @[Top.scala 130:11]
  assign n87_valid_up = n84_valid_down; // @[Top.scala 134:18]
  assign n87_I_0_0 = n84_O_0_0; // @[Top.scala 133:11]
  assign n87_I_0_1 = n84_O_0_1; // @[Top.scala 133:11]
  assign n87_I_0_2 = n84_O_0_2; // @[Top.scala 133:11]
  assign n140_clock = clock;
  assign n140_reset = reset;
  assign n140_valid_up = n87_valid_down; // @[Top.scala 137:19]
  assign n140_I_0 = n87_O_0; // @[Top.scala 136:12]
  assign n140_I_1 = n87_O_1; // @[Top.scala 136:12]
  assign n140_I_2 = n87_O_2; // @[Top.scala 136:12]
  assign n141_valid_up = n140_valid_down; // @[Top.scala 140:19]
  assign n141_I = n140_O; // @[Top.scala 139:12]
  assign n142_clock = clock;
  assign n142_reset = reset;
  assign n142_valid_up = n141_valid_down; // @[Top.scala 143:19]
  assign n142_I = n141_O; // @[Top.scala 142:12]
  assign n143_clock = clock;
  assign n143_reset = reset;
  assign n143_valid_up = n142_valid_down; // @[Top.scala 146:19]
  assign n143_I = n142_O; // @[Top.scala 145:12]
  assign n144_clock = clock;
  assign n144_reset = reset;
  assign n144_valid_up = n143_valid_down; // @[Top.scala 149:19]
  assign n144_I = n143_O; // @[Top.scala 148:12]
endmodule
