library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_153 is
    generic(
        stream_length: type_NaturalNumberType := 1918
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3ArithType1918;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeIntTypeArithType32ArithType1918;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end mapsimpleorderedstream_153;

architecture behavioral of mapsimpleorderedstream_153 is
    
    component slidevector_62
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType64ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType64ArithType9ArithType1;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorjoin_61
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType64ArithType9;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType9;
            p2_in_data: in type_VectorTypeLogicTypeArithType9;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType9;
            p4_out_data: out type_VectorTypeIntTypeArithType64ArithType9;
            p4_out_last: out type_LastVectorTypeArithType0;
            p4_out_valid: out type_LogicType;
            p4_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_149
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType1;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeLogicTypeArithType32ArithType1;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorfork_147
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType64ArithType9ArithType1;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType64ArithType9ArithType1;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType1;
            p3_out_data: out type_VectorTypeLogicTypeArithType1;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType1
        );
    end component;
    component conversion_151
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
    component mapvector_function_63
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_IntTypeArithType32;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0;
            p1_in_data: in type_VectorTypeIntTypeArithType64ArithType9;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0
        );
    end component;
    component zipvector_31
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0VectorTypeIntTypeArithType32ArithType9TextTypet1VectorTypeIntTypeArithType32ArithType9_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component constantbitvector_27
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_VectorTypeLogicTypeArithType288;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component slidevector_28
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType288;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeLogicTypeArithType32ArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_29
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeLogicTypeArithType32ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeIntTypeArithType32ArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component joinvector_26
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeIntTypeArithType32ArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_30
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_VectorTypeIntTypeArithType32ArithType9;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0VectorTypeIntTypeArithType32ArithType9TextTypet1VectorTypeIntTypeArithType32ArithType9_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component joinvector_150
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeLogicTypeArithType32ArithType1;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeLogicTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorjoin_148
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType1;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType1;
            p2_in_data: in type_VectorTypeLogicTypeArithType1;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType1;
            p4_out_data: out type_VectorTypeIntTypeArithType32ArithType1;
            p4_out_last: out type_LastVectorTypeArithType0;
            p4_out_valid: out type_LogicType;
            p4_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorfork_60
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType9;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType9;
            p3_out_data: out type_VectorTypeLogicTypeArithType9;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType9
        );
    end component;
    component mapvector_function_32
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
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_VectorTypeLogicTypeArithType288;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_VectorTypeVectorTypeLogicTypeArithType32ArithType9;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_data: type_VectorTypeIntTypeArithType32ArithType9;
    signal s09_last: type_LastVectorTypeArithType0;
    signal s10_valid: type_LogicType;
    signal s100_data: type_LastVectorTypeArithType1;
    signal s101_data: type_ReadyVectorTypeArithType1;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_VectorTypeIntTypeArithType32ArithType9;
    signal s13_last: type_LastVectorTypeArithType0;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s16_data: type_NamedTupleTypeTextTypet0VectorTypeIntTypeArithType32ArithType9TextTypet1VectorTypeIntTypeArithType32ArithType9_t0;
    signal s17_last: type_LastVectorTypeArithType0;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType0;
    signal s20_data: type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType9;
    signal s21_data: type_VectorTypeLastVectorTypeArithType0ArithType9;
    signal s22_data: type_VectorTypeLogicTypeArithType9;
    signal s23_ready: type_ReadyVectorTypeArithType0;
    signal s24_ready: type_ReadyVectorTypeArithType0;
    signal s25_ready: type_ReadyVectorTypeArithType0;
    signal s26_ready: type_ReadyVectorTypeArithType0;
    signal s27_ready: type_ReadyVectorTypeArithType0;
    signal s28_ready: type_ReadyVectorTypeArithType0;
    signal s29_ready: type_ReadyVectorTypeArithType0;
    signal s30_ready: type_ReadyVectorTypeArithType0;
    signal s31_ready: type_ReadyVectorTypeArithType0;
    signal s32_data: type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType9;
    signal s33_last: type_LastVectorTypeArithType0;
    signal s34_valid: type_LogicType;
    signal s35_ready: type_ReadyVectorTypeArithType0;
    signal s36_data: type_IntTypeArithType64;
    signal s37_data: type_IntTypeArithType64;
    signal s38_data: type_IntTypeArithType64;
    signal s39_data: type_IntTypeArithType64;
    signal s40_data: type_IntTypeArithType64;
    signal s41_data: type_IntTypeArithType64;
    signal s42_data: type_IntTypeArithType64;
    signal s43_data: type_IntTypeArithType64;
    signal s44_data: type_IntTypeArithType64;
    signal s45_last: type_LastVectorTypeArithType0;
    signal s46_last: type_LastVectorTypeArithType0;
    signal s47_last: type_LastVectorTypeArithType0;
    signal s48_last: type_LastVectorTypeArithType0;
    signal s49_last: type_LastVectorTypeArithType0;
    signal s50_last: type_LastVectorTypeArithType0;
    signal s51_last: type_LastVectorTypeArithType0;
    signal s52_last: type_LastVectorTypeArithType0;
    signal s53_last: type_LastVectorTypeArithType0;
    signal s54_valid: type_LogicType;
    signal s55_valid: type_LogicType;
    signal s56_valid: type_LogicType;
    signal s57_valid: type_LogicType;
    signal s58_valid: type_LogicType;
    signal s59_valid: type_LogicType;
    signal s60_valid: type_LogicType;
    signal s61_valid: type_LogicType;
    signal s62_valid: type_LogicType;
    signal s63_data: type_VectorTypeReadyVectorTypeArithType0ArithType9;
    signal s64_data: type_VectorTypeIntTypeArithType64ArithType9;
    signal s65_last: type_LastVectorTypeArithType0;
    signal s66_valid: type_LogicType;
    signal s67_ready: type_ReadyVectorTypeArithType0;
    signal s68_data: type_VectorTypeVectorTypeIntTypeArithType64ArithType9ArithType1;
    signal s69_data: type_VectorTypeLastVectorTypeArithType0ArithType1;
    signal s70_data: type_VectorTypeLogicTypeArithType1;
    signal s71_ready: type_ReadyVectorTypeArithType0;
    signal s72_data: type_VectorTypeVectorTypeIntTypeArithType64ArithType9ArithType1;
    signal s73_last: type_LastVectorTypeArithType0;
    signal s74_valid: type_LogicType;
    signal s75_ready: type_ReadyVectorTypeArithType0;
    signal s76_data: type_IntTypeArithType32;
    signal s77_last: type_LastVectorTypeArithType0;
    signal s78_valid: type_LogicType;
    signal s79_data: type_VectorTypeReadyVectorTypeArithType0ArithType1;
    signal s80_data: type_VectorTypeIntTypeArithType32ArithType1;
    signal s81_last: type_LastVectorTypeArithType0;
    signal s82_valid: type_LogicType;
    signal s83_ready: type_ReadyVectorTypeArithType0;
    signal s84_data: type_VectorTypeVectorTypeLogicTypeArithType32ArithType1;
    signal s85_last: type_LastVectorTypeArithType0;
    signal s86_valid: type_LogicType;
    signal s87_ready: type_ReadyVectorTypeArithType0;
    signal s88_data: type_VectorTypeLogicTypeArithType32;
    signal s89_last: type_LastVectorTypeArithType0;
    signal s90_valid: type_LogicType;
    signal s91_ready: type_ReadyVectorTypeArithType0;
    signal s92_data: type_IntTypeArithType32;
    signal s93_last: type_LastVectorTypeArithType0;
    signal s94_valid: type_LogicType;
    signal s95_ready: type_ReadyVectorTypeArithType0;
    signal s96_data: type_VectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3;
    signal s97_last: type_LastVectorTypeArithType0;
    signal s98_valid: type_LogicType;
    signal s99_ready: type_ReadyVectorTypeArithType0;
begin
    
    U0_joinvector: joinvector_26 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s98_valid,
        p0_in_data => s96_data,
        p1_out_last => s09_last,
        p1_out_data => s08_data,
        p0_out_ready => s99_ready,
        p0_in_last => s97_last,
        reset => reset,
        p1_in_ready => s11_ready
    );
    U1_constantbitvector: constantbitvector_27 port map(
        p0_out_last => s01_last,
        clk => clk,
        p0_out_data => s00_data,
        p0_in_ready => s03_ready,
        p0_out_valid => s02_valid,
        reset => reset
    );
    U2_slidevector: slidevector_28 port map(
        clk => clk,
        p1_out_valid => s06_valid,
        p0_in_valid => s02_valid,
        p0_in_data => s00_data,
        p1_out_data => s04_data,
        p1_out_last => s05_last,
        p0_out_ready => s03_ready,
        p0_in_last => s01_last,
        reset => reset,
        p1_in_ready => s07_ready
    );
    U3_conversion: conversion_29 port map(
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => s06_valid,
        p0_in_data => s04_data,
        p1_out_last => s13_last,
        p1_out_data => s12_data,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => s15_ready
    );
    U4_tuple: tuple_30 port map(
        clk => clk,
        p2_out_valid => s18_valid,
        p2_out_data => s16_data,
        p1_in_last => s13_last,
        p0_in_valid => s10_valid,
        p2_out_last => s17_last,
        p0_out_ready => s11_ready,
        p1_in_valid => s14_valid,
        p2_in_ready => s19_ready,
        p0_in_last => s09_last,
        reset => reset,
        p0_in_data => s08_data,
        p1_in_data => s12_data,
        p1_out_ready => s15_ready
    );
    U5_zipvector: zipvector_31 port map(
        clk => clk,
        p1_out_valid => s34_valid,
        p0_in_valid => s18_valid,
        p0_in_data => s16_data,
        p1_out_last => s33_last,
        p0_out_ready => s19_ready,
        p1_out_data => s32_data,
        p0_in_last => s17_last,
        reset => reset,
        p1_in_ready => s35_ready
    );
    U6_vectorfork: vectorfork_60 port map(
        p3_out_data => s22_data,
        clk => clk,
        p0_in_valid => s34_valid,
        p0_in_data => s32_data,
        p2_out_data => s21_data,
        p0_out_ready => s35_ready,
        p1_out_data => s20_data,
        p0_in_last => s33_last,
        reset => reset,
        p4_in_data(0) => s23_ready,
        p4_in_data(1) => s24_ready,
        p4_in_data(2) => s25_ready,
        p4_in_data(3) => s26_ready,
        p4_in_data(4) => s27_ready,
        p4_in_data(5) => s28_ready,
        p4_in_data(6) => s29_ready,
        p4_in_data(7) => s30_ready,
        p4_in_data(8) => s31_ready
    );
    U7_mapvector_function: mapvector_function_32 port map(
        clk => clk,
        p1_out_valid => s54_valid,
        p0_in_valid => s22_data(0),
        p1_out_data => s36_data,
        p1_out_last => s45_last,
        p0_out_ready => s23_ready,
        p0_in_last => s21_data(0),
        reset => reset,
        p1_in_ready => s63_data(0),
        p0_in_data => s20_data(0)
    );
    U8_mapvector_function: mapvector_function_32 port map(
        clk => clk,
        p1_out_valid => s55_valid,
        p0_in_valid => s22_data(1),
        p1_out_data => s37_data,
        p1_out_last => s46_last,
        p0_out_ready => s24_ready,
        p0_in_last => s21_data(1),
        reset => reset,
        p1_in_ready => s63_data(1),
        p0_in_data => s20_data(1)
    );
    U9_mapvector_function: mapvector_function_32 port map(
        clk => clk,
        p1_out_valid => s56_valid,
        p0_in_valid => s22_data(2),
        p1_out_data => s38_data,
        p1_out_last => s47_last,
        p0_out_ready => s25_ready,
        p0_in_last => s21_data(2),
        reset => reset,
        p1_in_ready => s63_data(2),
        p0_in_data => s20_data(2)
    );
    U10_mapvector_function: mapvector_function_32 port map(
        clk => clk,
        p1_out_valid => s57_valid,
        p0_in_valid => s22_data(3),
        p1_out_data => s39_data,
        p1_out_last => s48_last,
        p0_out_ready => s26_ready,
        p0_in_last => s21_data(3),
        reset => reset,
        p1_in_ready => s63_data(3),
        p0_in_data => s20_data(3)
    );
    U11_mapvector_function: mapvector_function_32 port map(
        clk => clk,
        p1_out_valid => s58_valid,
        p0_in_valid => s22_data(4),
        p1_out_data => s40_data,
        p1_out_last => s49_last,
        p0_out_ready => s27_ready,
        p0_in_last => s21_data(4),
        reset => reset,
        p1_in_ready => s63_data(4),
        p0_in_data => s20_data(4)
    );
    U12_mapvector_function: mapvector_function_32 port map(
        clk => clk,
        p1_out_valid => s59_valid,
        p0_in_valid => s22_data(5),
        p1_out_data => s41_data,
        p1_out_last => s50_last,
        p0_out_ready => s28_ready,
        p0_in_last => s21_data(5),
        reset => reset,
        p1_in_ready => s63_data(5),
        p0_in_data => s20_data(5)
    );
    U13_mapvector_function: mapvector_function_32 port map(
        clk => clk,
        p1_out_valid => s60_valid,
        p0_in_valid => s22_data(6),
        p1_out_data => s42_data,
        p1_out_last => s51_last,
        p0_out_ready => s29_ready,
        p0_in_last => s21_data(6),
        reset => reset,
        p1_in_ready => s63_data(6),
        p0_in_data => s20_data(6)
    );
    U14_mapvector_function: mapvector_function_32 port map(
        clk => clk,
        p1_out_valid => s61_valid,
        p0_in_valid => s22_data(7),
        p1_out_data => s43_data,
        p1_out_last => s52_last,
        p0_out_ready => s30_ready,
        p0_in_last => s21_data(7),
        reset => reset,
        p1_in_ready => s63_data(7),
        p0_in_data => s20_data(7)
    );
    U15_mapvector_function: mapvector_function_32 port map(
        clk => clk,
        p1_out_valid => s62_valid,
        p0_in_valid => s22_data(8),
        p1_out_data => s44_data,
        p1_out_last => s53_last,
        p0_out_ready => s31_ready,
        p0_in_last => s21_data(8),
        reset => reset,
        p1_in_ready => s63_data(8),
        p0_in_data => s20_data(8)
    );
    U16_vectorjoin: vectorjoin_61 port map(
        clk => clk,
        p4_out_last => s65_last,
        p4_out_valid => s66_valid,
        p4_out_data => s64_data,
        p0_in_data(0) => s36_data,
        p0_in_data(1) => s37_data,
        p0_in_data(2) => s38_data,
        p0_in_data(3) => s39_data,
        p0_in_data(4) => s40_data,
        p0_in_data(5) => s41_data,
        p0_in_data(6) => s42_data,
        p0_in_data(7) => s43_data,
        p0_in_data(8) => s44_data,
        p1_in_data(0) => s45_last,
        p1_in_data(1) => s46_last,
        p1_in_data(2) => s47_last,
        p1_in_data(3) => s48_last,
        p1_in_data(4) => s49_last,
        p1_in_data(5) => s50_last,
        p1_in_data(6) => s51_last,
        p1_in_data(7) => s52_last,
        p1_in_data(8) => s53_last,
        p4_in_ready => s67_ready,
        p2_in_data(0) => s54_valid,
        p2_in_data(1) => s55_valid,
        p2_in_data(2) => s56_valid,
        p2_in_data(3) => s57_valid,
        p2_in_data(4) => s58_valid,
        p2_in_data(5) => s59_valid,
        p2_in_data(6) => s60_valid,
        p2_in_data(7) => s61_valid,
        p2_in_data(8) => s62_valid,
        reset => reset,
        p3_out_data => s63_data
    );
    U17_slidevector: slidevector_62 port map(
        clk => clk,
        p1_out_valid => s74_valid,
        p0_in_valid => s66_valid,
        p0_in_data => s64_data,
        p1_out_last => s73_last,
        p0_out_ready => s67_ready,
        p0_in_last => s65_last,
        reset => reset,
        p1_in_ready => s75_ready,
        p1_out_data => s72_data
    );
    U18_vectorfork: vectorfork_147 port map(
        clk => clk,
        p0_in_valid => s74_valid,
        p3_out_data => s70_data,
        p0_out_ready => s75_ready,
        p4_in_data(0) => s71_ready,
        p2_out_data => s69_data,
        p0_in_last => s73_last,
        reset => reset,
        p1_out_data => s68_data,
        p0_in_data => s72_data
    );
    U19_mapvector_function: mapvector_function_63 port map(
        p0_out_last => s77_last,
        clk => clk,
        p1_in_last => s69_data(0),
        p0_in_ready => s79_data(0),
        p0_out_valid => s78_valid,
        p1_in_valid => s70_data(0),
        reset => reset,
        p0_out_data => s76_data,
        p1_in_data => s68_data(0),
        p1_out_ready => s71_ready
    );
    U20_vectorjoin: vectorjoin_148 port map(
        clk => clk,
        p4_out_data => s80_data,
        p4_out_last => s81_last,
        p1_in_data(0) => s77_last,
        p4_out_valid => s82_valid,
        p0_in_data(0) => s76_data,
        p2_in_data(0) => s78_valid,
        p3_out_data => s79_data,
        p4_in_ready => s83_ready,
        reset => reset
    );
    U21_conversion: conversion_149 port map(
        clk => clk,
        p1_out_valid => s86_valid,
        p0_in_valid => s82_valid,
        p0_in_data => s80_data,
        p1_out_last => s85_last,
        p1_out_data => s84_data,
        p0_out_ready => s83_ready,
        p0_in_last => s81_last,
        reset => reset,
        p1_in_ready => s87_ready
    );
    U22_joinvector: joinvector_150 port map(
        clk => clk,
        p1_out_valid => s90_valid,
        p0_in_valid => s86_valid,
        p1_out_last => s89_last,
        p0_in_data => s84_data,
        p0_out_ready => s87_ready,
        p0_in_last => s85_last,
        reset => reset,
        p1_in_ready => s91_ready,
        p1_out_data => s88_data
    );
    U23_conversion: conversion_151 port map(
        clk => clk,
        p1_out_valid => s94_valid,
        p0_in_valid => s90_valid,
        p1_out_last => s93_last,
        p0_out_ready => s91_ready,
        p0_in_last => s89_last,
        reset => reset,
        p1_in_ready => s95_ready,
        p1_out_data => s92_data,
        p0_in_data => s88_data
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
