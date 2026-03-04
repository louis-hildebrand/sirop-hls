library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_257 is
    generic(
        stream_length: type_NaturalNumberType := 1918
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType1918;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeSignedIntTypeArithType32ArithType1918;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end mapsimpleorderedstream_257;

architecture behavioral of mapsimpleorderedstream_257 is
    
    component resizeinteger_244
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType64;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component resizeinteger_254
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType33;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_240
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component resizeinteger_251
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType64;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_242
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_SignedIntTypeArithType32;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component select_239
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component select_246
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mulint_250
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType64;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mulint_243
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType64;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component addint_253
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType33;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_249
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_SignedIntTypeArithType32;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_256
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_245
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_238
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component select_248
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component distributor_255
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType4;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType4;
            p3_out_data: out type_VectorTypeLogicTypeArithType4;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType4
        );
    end component;
    component id_247
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component select_241
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_252
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_SignedIntTypeArithType32;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_data: type_SignedIntTypeArithType32;
    signal s09_last: type_LastVectorTypeArithType0;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_SignedIntTypeArithType32;
    signal s13_last: type_LastVectorTypeArithType0;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s16_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s17_last: type_LastVectorTypeArithType0;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType0;
    signal s20_data: type_SignedIntTypeArithType64;
    signal s21_last: type_LastVectorTypeArithType0;
    signal s22_valid: type_LogicType;
    signal s23_ready: type_ReadyVectorTypeArithType0;
    signal s24_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s25_last: type_LastVectorTypeArithType0;
    signal s26_valid: type_LogicType;
    signal s27_ready: type_ReadyVectorTypeArithType0;
    signal s28_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s29_last: type_LastVectorTypeArithType0;
    signal s30_valid: type_LogicType;
    signal s31_ready: type_ReadyVectorTypeArithType0;
    signal s32_data: type_SignedIntTypeArithType32;
    signal s33_last: type_LastVectorTypeArithType0;
    signal s34_valid: type_LogicType;
    signal s35_ready: type_ReadyVectorTypeArithType0;
    signal s36_data: type_SignedIntTypeArithType32;
    signal s37_last: type_LastVectorTypeArithType0;
    signal s38_valid: type_LogicType;
    signal s39_ready: type_ReadyVectorTypeArithType0;
    signal s40_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s41_last: type_LastVectorTypeArithType0;
    signal s42_valid: type_LogicType;
    signal s43_ready: type_ReadyVectorTypeArithType0;
    signal s44_data: type_SignedIntTypeArithType64;
    signal s45_last: type_LastVectorTypeArithType0;
    signal s46_valid: type_LogicType;
    signal s47_ready: type_ReadyVectorTypeArithType0;
    signal s48_data: type_SignedIntTypeArithType32;
    signal s49_last: type_LastVectorTypeArithType0;
    signal s50_valid: type_LogicType;
    signal s51_ready: type_ReadyVectorTypeArithType0;
    signal s52_data: type_SignedIntTypeArithType32;
    signal s53_last: type_LastVectorTypeArithType0;
    signal s54_valid: type_LogicType;
    signal s55_ready: type_ReadyVectorTypeArithType0;
    signal s56_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s57_last: type_LastVectorTypeArithType0;
    signal s58_valid: type_LogicType;
    signal s59_ready: type_ReadyVectorTypeArithType0;
    signal s60_data: type_SignedIntTypeArithType33;
    signal s61_last: type_LastVectorTypeArithType0;
    signal s62_valid: type_LogicType;
    signal s63_ready: type_ReadyVectorTypeArithType0;
    signal s64_data: type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType4;
    signal s65_data: type_VectorTypeLastVectorTypeArithType0ArithType4;
    signal s66_data: type_VectorTypeLogicTypeArithType4;
    signal s67_ready: type_ReadyVectorTypeArithType0;
    signal s68_ready: type_ReadyVectorTypeArithType0;
    signal s69_ready: type_ReadyVectorTypeArithType0;
    signal s70_ready: type_ReadyVectorTypeArithType0;
    signal s71_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s72_last: type_LastVectorTypeArithType0;
    signal s73_valid: type_LogicType;
    signal s74_ready: type_ReadyVectorTypeArithType0;
    signal s75_data: type_SignedIntTypeArithType32;
    signal s76_last: type_LastVectorTypeArithType0;
    signal s77_valid: type_LogicType;
    signal s78_ready: type_ReadyVectorTypeArithType0;
    signal s79_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s80_last: type_LastVectorTypeArithType0;
    signal s81_valid: type_LogicType;
    signal s82_ready: type_ReadyVectorTypeArithType0;
    signal s83_data: type_LastVectorTypeArithType1;
    signal s84_data: type_ReadyVectorTypeArithType1;
begin
    
    U0_id: id_238 port map(
        clk => clk,
        p1_out_valid => s02_valid,
        p0_in_valid => s66_data(0),
        p1_out_last => s01_last,
        p0_out_ready => s67_ready,
        p1_out_data => s00_data,
        p0_in_last => s65_data(0),
        reset => reset,
        p1_in_ready => s03_ready,
        p0_in_data => s64_data(0)
    );
    U1_select: select_239 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s02_valid,
        p1_out_last => s09_last,
        p0_out_ready => s03_ready,
        p0_in_last => s01_last,
        reset => reset,
        p1_in_ready => s11_ready,
        p1_out_data => s08_data,
        p0_in_data => s00_data
    );
    U2_id: id_240 port map(
        clk => clk,
        p1_out_valid => s06_valid,
        p0_in_valid => s66_data(1),
        p1_out_last => s05_last,
        p0_out_ready => s68_ready,
        p1_out_data => s04_data,
        p0_in_last => s65_data(1),
        reset => reset,
        p1_in_ready => s07_ready,
        p0_in_data => s64_data(1)
    );
    U3_select: select_241 port map(
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => s06_valid,
        p1_out_last => s13_last,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => s15_ready,
        p1_out_data => s12_data,
        p0_in_data => s04_data
    );
    U4_tuple: tuple_242 port map(
        clk => clk,
        p2_out_valid => s18_valid,
        p1_in_last => s13_last,
        p0_in_valid => s10_valid,
        p2_out_data => s16_data,
        p0_in_data => s08_data,
        p2_out_last => s17_last,
        p0_out_ready => s11_ready,
        p1_in_valid => s14_valid,
        p2_in_ready => s19_ready,
        p1_in_data => s12_data,
        p0_in_last => s09_last,
        reset => reset,
        p1_out_ready => s15_ready
    );
    U5_mulint: mulint_243 port map(
        clk => clk,
        p1_out_valid => s22_valid,
        p0_in_valid => s18_valid,
        p1_out_data => s20_data,
        p1_out_last => s21_last,
        p0_out_ready => s19_ready,
        p0_in_last => s17_last,
        reset => reset,
        p1_in_ready => s23_ready,
        p0_in_data => s16_data
    );
    U6_resizeinteger: resizeinteger_244 port map(
        clk => clk,
        p0_in_data => s20_data,
        p1_out_valid => s50_valid,
        p0_in_valid => s22_valid,
        p1_out_last => s49_last,
        p0_out_ready => s23_ready,
        p0_in_last => s21_last,
        reset => reset,
        p1_in_ready => s51_ready,
        p1_out_data => s48_data
    );
    U7_id: id_245 port map(
        clk => clk,
        p1_out_valid => s26_valid,
        p0_in_valid => s66_data(2),
        p1_out_last => s25_last,
        p0_out_ready => s69_ready,
        p1_out_data => s24_data,
        p0_in_last => s65_data(2),
        reset => reset,
        p1_in_ready => s27_ready,
        p0_in_data => s64_data(2)
    );
    U8_select: select_246 port map(
        clk => clk,
        p1_out_valid => s34_valid,
        p0_in_valid => s26_valid,
        p1_out_last => s33_last,
        p0_out_ready => s27_ready,
        p0_in_last => s25_last,
        reset => reset,
        p1_in_ready => s35_ready,
        p1_out_data => s32_data,
        p0_in_data => s24_data
    );
    U9_id: id_247 port map(
        clk => clk,
        p1_out_valid => s30_valid,
        p0_in_valid => s66_data(3),
        p1_out_last => s29_last,
        p0_out_ready => s70_ready,
        p1_out_data => s28_data,
        p0_in_last => s65_data(3),
        reset => reset,
        p1_in_ready => s31_ready,
        p0_in_data => s64_data(3)
    );
    U10_select: select_248 port map(
        clk => clk,
        p1_out_valid => s38_valid,
        p0_in_valid => s30_valid,
        p1_out_last => s37_last,
        p0_out_ready => s31_ready,
        p0_in_last => s29_last,
        reset => reset,
        p1_in_ready => s39_ready,
        p1_out_data => s36_data,
        p0_in_data => s28_data
    );
    U11_tuple: tuple_249 port map(
        clk => clk,
        p2_out_valid => s42_valid,
        p1_in_last => s37_last,
        p0_in_valid => s34_valid,
        p2_out_data => s40_data,
        p0_in_data => s32_data,
        p2_out_last => s41_last,
        p0_out_ready => s35_ready,
        p1_in_valid => s38_valid,
        p2_in_ready => s43_ready,
        p1_in_data => s36_data,
        p0_in_last => s33_last,
        reset => reset,
        p1_out_ready => s39_ready
    );
    U12_mulint: mulint_250 port map(
        clk => clk,
        p1_out_valid => s46_valid,
        p0_in_valid => s42_valid,
        p1_out_data => s44_data,
        p1_out_last => s45_last,
        p0_out_ready => s43_ready,
        p0_in_last => s41_last,
        reset => reset,
        p1_in_ready => s47_ready,
        p0_in_data => s40_data
    );
    U13_resizeinteger: resizeinteger_251 port map(
        clk => clk,
        p0_in_data => s44_data,
        p1_out_valid => s54_valid,
        p0_in_valid => s46_valid,
        p1_out_last => s53_last,
        p0_out_ready => s47_ready,
        p0_in_last => s45_last,
        reset => reset,
        p1_in_ready => s55_ready,
        p1_out_data => s52_data
    );
    U14_tuple: tuple_252 port map(
        clk => clk,
        p2_out_valid => s58_valid,
        p1_in_last => s53_last,
        p0_in_valid => s50_valid,
        p2_out_data => s56_data,
        p0_in_data => s48_data,
        p2_out_last => s57_last,
        p0_out_ready => s51_ready,
        p1_in_valid => s54_valid,
        p2_in_ready => s59_ready,
        p1_in_data => s52_data,
        p0_in_last => s49_last,
        reset => reset,
        p1_out_ready => s55_ready
    );
    U15_addint: addint_253 port map(
        clk => clk,
        p1_out_valid => s62_valid,
        p0_in_valid => s58_valid,
        p1_out_last => s61_last,
        p1_out_data => s60_data,
        p0_out_ready => s59_ready,
        p0_in_last => s57_last,
        reset => reset,
        p1_in_ready => s63_ready,
        p0_in_data => s56_data
    );
    U16_resizeinteger: resizeinteger_254 port map(
        clk => clk,
        p1_out_valid => s77_valid,
        p0_in_valid => s62_valid,
        p0_in_data => s60_data,
        p1_out_last => s76_last,
        p0_out_ready => s63_ready,
        p0_in_last => s61_last,
        reset => reset,
        p1_in_ready => s78_ready,
        p1_out_data => s75_data
    );
    U17_distributor: distributor_255 port map(
        clk => clk,
        p0_in_valid => s73_valid,
        p3_out_data => s66_data,
        p0_out_ready => s74_ready,
        p1_out_data => s64_data,
        p0_in_last => s72_last,
        p4_in_data(0) => s67_ready,
        p4_in_data(1) => s68_ready,
        p4_in_data(2) => s69_ready,
        p4_in_data(3) => s70_ready,
        reset => reset,
        p2_out_data => s65_data,
        p0_in_data => s71_data
    );
    U18_id: id_256 port map(
        clk => clk,
        p1_out_valid => s73_valid,
        p0_in_valid => s81_valid,
        p1_out_last => s72_last,
        p0_out_ready => s82_ready,
        p1_out_data => s71_data,
        p0_in_last => s80_last,
        reset => reset,
        p1_in_ready => s74_ready,
        p0_in_data => s79_data
    );
    p1_out_data <= s75_data;
    p1_out_last <= "1" & s76_last when out_counter = stream_length - 1 else "0" & s76_last;
    p1_out_valid <= s77_valid;
    s78_ready <= p1_in_ready(s78_ready'high downto s78_ready'low);
    
    s79_data <= p0_in_data;
    s80_last <= p0_in_last(s80_last'high downto s80_last'low);
    s81_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s82_ready when in_counter = stream_length - 1 and s82_ready(s82_ready'high) = '1' else "0" & s82_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s81_valid = '1' and s82_ready(s82_ready'high) = '1' then
                    if in_counter < stream_length - 1 then
                        in_counter <= in_counter + 1;
                    else
                        in_counter <= 0;
                    end if;
                end if;
            end if;
        end if;
    end process;
    
    outgoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                out_counter <= 0;
            else
                if s77_valid = '1' and s78_ready(s78_ready'high) = '1' then
                    if out_counter < stream_length - 1 then
                        out_counter <= out_counter + 1;
                    else
                        out_counter <= 0;
                    end if;
                end if;
            end if;
        end if;
    end process;
    
end behavioral;
