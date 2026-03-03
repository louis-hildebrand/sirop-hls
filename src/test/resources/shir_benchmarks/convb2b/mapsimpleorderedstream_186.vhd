library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_186 is
    generic(
        stream_length: type_NaturalNumberType := 1917
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType2ArithType2ArithType1917;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeIntTypeArithType32ArithType1917;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end mapsimpleorderedstream_186;

architecture behavioral of mapsimpleorderedstream_186 is
    
    component vectorjoin_177
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType2;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType2;
            p2_in_data: in type_VectorTypeLogicTypeArithType2;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType2;
            p4_out_data: out type_VectorTypeIntTypeArithType32ArithType2;
            p4_out_last: out type_LastVectorTypeArithType0;
            p4_out_valid: out type_LogicType;
            p4_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component slidevector_160
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType64ArithType4;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType64ArithType2ArithType2;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapvector_function_145
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType64;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorfork_158
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType4;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType4;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType4;
            p3_out_data: out type_VectorTypeLogicTypeArithType4;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType4
        );
    end component;
    component addint_179
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType33;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component joinvector_139
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType32ArithType2ArithType2;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeIntTypeArithType32ArithType4;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component constantbitvector_140
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_VectorTypeLogicTypeArithType128;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component resizeinteger_184
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType29;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_182
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorfork_176
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType64ArithType2ArithType2;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType64ArithType2ArithType2;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType2;
            p3_out_data: out type_VectorTypeLogicTypeArithType2;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType2
        );
    end component;
    component resizelowinteger_183
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType29;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component zipvector_144
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0VectorTypeIntTypeArithType32ArithType4TextTypet1VectorTypeIntTypeArithType32ArithType4_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType4;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorjoin_159
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType64ArithType4;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType4;
            p2_in_data: in type_VectorTypeLogicTypeArithType4;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType4;
            p4_out_data: out type_VectorTypeIntTypeArithType64ArithType4;
            p4_out_last: out type_LastVectorTypeArithType0;
            p4_out_valid: out type_LogicType;
            p4_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_142
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeLogicTypeArithType32ArithType4;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeIntTypeArithType32ArithType4;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component slidevector_141
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType128;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeLogicTypeArithType32ArithType4;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapvector_function_161
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType64ArithType2;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component dropvector_181
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType33;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeLogicTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_180
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType33;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeLogicTypeArithType33;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectortotuple_178
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType2;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_143
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType4;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_VectorTypeIntTypeArithType32ArithType4;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0VectorTypeIntTypeArithType32ArithType4TextTypet1VectorTypeIntTypeArithType32ArithType4_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_VectorTypeLogicTypeArithType128;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_VectorTypeVectorTypeLogicTypeArithType32ArithType4;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_data: type_VectorTypeIntTypeArithType32ArithType4;
    signal s09_last: type_LastVectorTypeArithType0;
    signal s10_valid: type_LogicType;
    signal s100_data: type_LastVectorTypeArithType1;
    signal s101_data: type_ReadyVectorTypeArithType1;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_VectorTypeIntTypeArithType32ArithType4;
    signal s13_last: type_LastVectorTypeArithType0;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s16_data: type_NamedTupleTypeTextTypet0VectorTypeIntTypeArithType32ArithType4TextTypet1VectorTypeIntTypeArithType32ArithType4_t0;
    signal s17_last: type_LastVectorTypeArithType0;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType0;
    signal s20_data: type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType4;
    signal s21_data: type_VectorTypeLastVectorTypeArithType0ArithType4;
    signal s22_data: type_VectorTypeLogicTypeArithType4;
    signal s23_ready: type_ReadyVectorTypeArithType0;
    signal s24_ready: type_ReadyVectorTypeArithType0;
    signal s25_ready: type_ReadyVectorTypeArithType0;
    signal s26_ready: type_ReadyVectorTypeArithType0;
    signal s27_data: type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType4;
    signal s28_last: type_LastVectorTypeArithType0;
    signal s29_valid: type_LogicType;
    signal s30_ready: type_ReadyVectorTypeArithType0;
    signal s31_data: type_IntTypeArithType64;
    signal s32_data: type_IntTypeArithType64;
    signal s33_data: type_IntTypeArithType64;
    signal s34_data: type_IntTypeArithType64;
    signal s35_last: type_LastVectorTypeArithType0;
    signal s36_last: type_LastVectorTypeArithType0;
    signal s37_last: type_LastVectorTypeArithType0;
    signal s38_last: type_LastVectorTypeArithType0;
    signal s39_valid: type_LogicType;
    signal s40_valid: type_LogicType;
    signal s41_valid: type_LogicType;
    signal s42_valid: type_LogicType;
    signal s43_data: type_VectorTypeReadyVectorTypeArithType0ArithType4;
    signal s44_data: type_VectorTypeIntTypeArithType64ArithType4;
    signal s45_last: type_LastVectorTypeArithType0;
    signal s46_valid: type_LogicType;
    signal s47_ready: type_ReadyVectorTypeArithType0;
    signal s48_data: type_VectorTypeVectorTypeIntTypeArithType64ArithType2ArithType2;
    signal s49_data: type_VectorTypeLastVectorTypeArithType0ArithType2;
    signal s50_data: type_VectorTypeLogicTypeArithType2;
    signal s51_ready: type_ReadyVectorTypeArithType0;
    signal s52_ready: type_ReadyVectorTypeArithType0;
    signal s53_data: type_VectorTypeVectorTypeIntTypeArithType64ArithType2ArithType2;
    signal s54_last: type_LastVectorTypeArithType0;
    signal s55_valid: type_LogicType;
    signal s56_ready: type_ReadyVectorTypeArithType0;
    signal s57_data: type_IntTypeArithType32;
    signal s58_data: type_IntTypeArithType32;
    signal s59_last: type_LastVectorTypeArithType0;
    signal s60_last: type_LastVectorTypeArithType0;
    signal s61_valid: type_LogicType;
    signal s62_valid: type_LogicType;
    signal s63_data: type_VectorTypeReadyVectorTypeArithType0ArithType2;
    signal s64_data: type_VectorTypeIntTypeArithType32ArithType2;
    signal s65_last: type_LastVectorTypeArithType0;
    signal s66_valid: type_LogicType;
    signal s67_ready: type_ReadyVectorTypeArithType0;
    signal s68_data: type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
    signal s69_last: type_LastVectorTypeArithType0;
    signal s70_valid: type_LogicType;
    signal s71_ready: type_ReadyVectorTypeArithType0;
    signal s72_data: type_IntTypeArithType33;
    signal s73_last: type_LastVectorTypeArithType0;
    signal s74_valid: type_LogicType;
    signal s75_ready: type_ReadyVectorTypeArithType0;
    signal s76_data: type_VectorTypeLogicTypeArithType33;
    signal s77_last: type_LastVectorTypeArithType0;
    signal s78_valid: type_LogicType;
    signal s79_ready: type_ReadyVectorTypeArithType0;
    signal s80_data: type_VectorTypeLogicTypeArithType32;
    signal s81_last: type_LastVectorTypeArithType0;
    signal s82_valid: type_LogicType;
    signal s83_ready: type_ReadyVectorTypeArithType0;
    signal s84_data: type_IntTypeArithType32;
    signal s85_last: type_LastVectorTypeArithType0;
    signal s86_valid: type_LogicType;
    signal s87_ready: type_ReadyVectorTypeArithType0;
    signal s88_data: type_IntTypeArithType29;
    signal s89_last: type_LastVectorTypeArithType0;
    signal s90_valid: type_LogicType;
    signal s91_ready: type_ReadyVectorTypeArithType0;
    signal s92_data: type_IntTypeArithType32;
    signal s93_last: type_LastVectorTypeArithType0;
    signal s94_valid: type_LogicType;
    signal s95_ready: type_ReadyVectorTypeArithType0;
    signal s96_data: type_VectorTypeVectorTypeIntTypeArithType32ArithType2ArithType2;
    signal s97_last: type_LastVectorTypeArithType0;
    signal s98_valid: type_LogicType;
    signal s99_ready: type_ReadyVectorTypeArithType0;
begin
    
    U0_joinvector: joinvector_139 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s98_valid,
        p1_out_last => s09_last,
        p1_out_data => s08_data,
        p0_out_ready => s99_ready,
        p0_in_last => s97_last,
        reset => reset,
        p1_in_ready => s11_ready,
        p0_in_data => s96_data
    );
    U1_constantbitvector: constantbitvector_140 port map(
        p0_out_last => s01_last,
        clk => clk,
        p0_in_ready => s03_ready,
        p0_out_valid => s02_valid,
        reset => reset,
        p0_out_data => s00_data
    );
    U2_slidevector: slidevector_141 port map(
        clk => clk,
        p1_out_valid => s06_valid,
        p0_in_valid => s02_valid,
        p1_out_last => s05_last,
        p0_out_ready => s03_ready,
        p0_in_data => s00_data,
        p0_in_last => s01_last,
        reset => reset,
        p1_in_ready => s07_ready,
        p1_out_data => s04_data
    );
    U3_conversion: conversion_142 port map(
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => s06_valid,
        p1_out_last => s13_last,
        p1_out_data => s12_data,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => s15_ready,
        p0_in_data => s04_data
    );
    U4_tuple: tuple_143 port map(
        clk => clk,
        p2_out_valid => s18_valid,
        p1_in_last => s13_last,
        p0_in_valid => s10_valid,
        p2_out_last => s17_last,
        p0_out_ready => s11_ready,
        p1_in_valid => s14_valid,
        p2_out_data => s16_data,
        p2_in_ready => s19_ready,
        p0_in_last => s09_last,
        reset => reset,
        p1_in_data => s12_data,
        p0_in_data => s08_data,
        p1_out_ready => s15_ready
    );
    U5_zipvector: zipvector_144 port map(
        clk => clk,
        p1_out_valid => s29_valid,
        p0_in_valid => s18_valid,
        p1_out_last => s28_last,
        p0_in_data => s16_data,
        p0_out_ready => s19_ready,
        p1_out_data => s27_data,
        p0_in_last => s17_last,
        reset => reset,
        p1_in_ready => s30_ready
    );
    U6_vectorfork: vectorfork_158 port map(
        clk => clk,
        p0_in_valid => s29_valid,
        p0_in_data => s27_data,
        p3_out_data => s22_data,
        p0_out_ready => s30_ready,
        p1_out_data => s20_data,
        p0_in_last => s28_last,
        p4_in_data(0) => s23_ready,
        p4_in_data(1) => s24_ready,
        p4_in_data(2) => s25_ready,
        p4_in_data(3) => s26_ready,
        reset => reset,
        p2_out_data => s21_data
    );
    U7_mapvector_function: mapvector_function_145 port map(
        clk => clk,
        p1_out_valid => s39_valid,
        p0_in_valid => s22_data(0),
        p1_out_data => s31_data,
        p1_out_last => s35_last,
        p0_out_ready => s23_ready,
        p0_in_last => s21_data(0),
        reset => reset,
        p1_in_ready => s43_data(0),
        p0_in_data => s20_data(0)
    );
    U8_mapvector_function: mapvector_function_145 port map(
        clk => clk,
        p1_out_valid => s40_valid,
        p0_in_valid => s22_data(1),
        p1_out_data => s32_data,
        p1_out_last => s36_last,
        p0_out_ready => s24_ready,
        p0_in_last => s21_data(1),
        reset => reset,
        p1_in_ready => s43_data(1),
        p0_in_data => s20_data(1)
    );
    U9_mapvector_function: mapvector_function_145 port map(
        clk => clk,
        p1_out_valid => s41_valid,
        p0_in_valid => s22_data(2),
        p1_out_data => s33_data,
        p1_out_last => s37_last,
        p0_out_ready => s25_ready,
        p0_in_last => s21_data(2),
        reset => reset,
        p1_in_ready => s43_data(2),
        p0_in_data => s20_data(2)
    );
    U10_mapvector_function: mapvector_function_145 port map(
        clk => clk,
        p1_out_valid => s42_valid,
        p0_in_valid => s22_data(3),
        p1_out_data => s34_data,
        p1_out_last => s38_last,
        p0_out_ready => s26_ready,
        p0_in_last => s21_data(3),
        reset => reset,
        p1_in_ready => s43_data(3),
        p0_in_data => s20_data(3)
    );
    U11_vectorjoin: vectorjoin_159 port map(
        clk => clk,
        p3_out_data => s43_data,
        p4_out_last => s45_last,
        p4_out_valid => s46_valid,
        p2_in_data(0) => s39_valid,
        p2_in_data(1) => s40_valid,
        p2_in_data(2) => s41_valid,
        p2_in_data(3) => s42_valid,
        p4_in_ready => s47_ready,
        reset => reset,
        p4_out_data => s44_data,
        p1_in_data(0) => s35_last,
        p1_in_data(1) => s36_last,
        p1_in_data(2) => s37_last,
        p1_in_data(3) => s38_last,
        p0_in_data(0) => s31_data,
        p0_in_data(1) => s32_data,
        p0_in_data(2) => s33_data,
        p0_in_data(3) => s34_data
    );
    U12_slidevector: slidevector_160 port map(
        clk => clk,
        p1_out_valid => s55_valid,
        p0_in_valid => s46_valid,
        p1_out_last => s54_last,
        p1_out_data => s53_data,
        p0_out_ready => s47_ready,
        p0_in_last => s45_last,
        reset => reset,
        p1_in_ready => s56_ready,
        p0_in_data => s44_data
    );
    U13_vectorfork: vectorfork_176 port map(
        clk => clk,
        p0_in_valid => s55_valid,
        p1_out_data => s48_data,
        p4_in_data(0) => s51_ready,
        p4_in_data(1) => s52_ready,
        p0_out_ready => s56_ready,
        p2_out_data => s49_data,
        p0_in_last => s54_last,
        reset => reset,
        p3_out_data => s50_data,
        p0_in_data => s53_data
    );
    U14_mapvector_function: mapvector_function_161 port map(
        clk => clk,
        p0_in_data => s48_data(0),
        p1_out_valid => s61_valid,
        p0_in_valid => s50_data(0),
        p1_out_last => s59_last,
        p0_out_ready => s51_ready,
        p0_in_last => s49_data(0),
        reset => reset,
        p1_in_ready => s63_data(0),
        p1_out_data => s57_data
    );
    U15_mapvector_function: mapvector_function_161 port map(
        clk => clk,
        p0_in_data => s48_data(1),
        p1_out_valid => s62_valid,
        p0_in_valid => s50_data(1),
        p1_out_last => s60_last,
        p0_out_ready => s52_ready,
        p0_in_last => s49_data(1),
        reset => reset,
        p1_in_ready => s63_data(1),
        p1_out_data => s58_data
    );
    U16_vectorjoin: vectorjoin_177 port map(
        clk => clk,
        p4_out_data => s64_data,
        p4_out_last => s65_last,
        p3_out_data => s63_data,
        p4_out_valid => s66_valid,
        p4_in_ready => s67_ready,
        p2_in_data(0) => s61_valid,
        p2_in_data(1) => s62_valid,
        reset => reset,
        p0_in_data(0) => s57_data,
        p0_in_data(1) => s58_data,
        p1_in_data(0) => s59_last,
        p1_in_data(1) => s60_last
    );
    U17_vectortotuple: vectortotuple_178 port map(
        clk => clk,
        p1_out_valid => s70_valid,
        p0_in_valid => s66_valid,
        p1_out_last => s69_last,
        p0_out_ready => s67_ready,
        p1_out_data => s68_data,
        p0_in_last => s65_last,
        reset => reset,
        p1_in_ready => s71_ready,
        p0_in_data => s64_data
    );
    U18_addint: addint_179 port map(
        clk => clk,
        p1_out_valid => s74_valid,
        p0_in_valid => s70_valid,
        p1_out_last => s73_last,
        p1_out_data => s72_data,
        p0_out_ready => s71_ready,
        p0_in_last => s69_last,
        reset => reset,
        p1_in_ready => s75_ready,
        p0_in_data => s68_data
    );
    U19_conversion: conversion_180 port map(
        clk => clk,
        p1_out_valid => s78_valid,
        p0_in_valid => s74_valid,
        p1_out_data => s76_data,
        p0_in_data => s72_data,
        p1_out_last => s77_last,
        p0_out_ready => s75_ready,
        p0_in_last => s73_last,
        reset => reset,
        p1_in_ready => s79_ready
    );
    U20_dropvector: dropvector_181 port map(
        clk => clk,
        p1_out_valid => s82_valid,
        p0_in_valid => s78_valid,
        p0_in_data => s76_data,
        p1_out_last => s81_last,
        p0_out_ready => s79_ready,
        p0_in_last => s77_last,
        reset => reset,
        p1_in_ready => s83_ready,
        p1_out_data => s80_data
    );
    U21_conversion: conversion_182 port map(
        clk => clk,
        p1_out_valid => s86_valid,
        p0_in_valid => s82_valid,
        p1_out_last => s85_last,
        p0_out_ready => s83_ready,
        p0_in_last => s81_last,
        reset => reset,
        p1_in_ready => s87_ready,
        p1_out_data => s84_data,
        p0_in_data => s80_data
    );
    U22_resizelowinteger: resizelowinteger_183 port map(
        clk => clk,
        p1_out_valid => s90_valid,
        p0_in_valid => s86_valid,
        p1_out_last => s89_last,
        p0_in_data => s84_data,
        p0_out_ready => s87_ready,
        p1_out_data => s88_data,
        p0_in_last => s85_last,
        reset => reset,
        p1_in_ready => s91_ready
    );
    U23_resizeinteger: resizeinteger_184 port map(
        clk => clk,
        p1_out_valid => s94_valid,
        p0_in_valid => s90_valid,
        p1_out_last => s93_last,
        p0_out_ready => s91_ready,
        p0_in_last => s89_last,
        p0_in_data => s88_data,
        reset => reset,
        p1_in_ready => s95_ready,
        p1_out_data => s92_data
    );
    p1_out_data <= s92_data;
    p1_out_last <= "1" & s93_last when out_counter = stream_length - 1 else "0" & s93_last;
    p1_out_valid <= s94_valid;
    s95_ready <= p1_in_ready(s95_ready'high downto s95_ready'low);
    
    s96_data <= p0_in_data;
    s97_last <= p0_in_last(s97_last'high downto s97_last'low);
    s98_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s99_ready when in_counter = stream_length - 1 and s99_ready(s99_ready'high) = '1' else "0" & s99_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s98_valid = '1' and s99_ready(s99_ready'high) = '1' then
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
                if s94_valid = '1' and s95_ready(s95_ready'high) = '1' then
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
