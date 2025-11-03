library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_56 is
    generic(
        stream_length: type_NaturalNumberType := 14
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeVectorTypeSignedIntTypeArithType8ArithType3ArithType14;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeIntTypeArithType8ArithType14;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end mapsimpleorderedstream_56;

architecture behavioral of mapsimpleorderedstream_56 is
    
    component joinvector_53
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeLogicTypeArithType8ArithType1;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeLogicTypeArithType8;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component slidevector_4
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType24;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeLogicTypeArithType8ArithType3;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_52
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType8ArithType1;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeLogicTypeArithType8ArithType1;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_6
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType8ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_VectorTypeSignedIntTypeArithType8ArithType3;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0VectorTypeSignedIntTypeArithType8ArithType3TextTypet1VectorTypeSignedIntTypeArithType8ArithType3_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapvector_function_8
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType8TextTypet1SignedIntTypeArithType8_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType8;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_5
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeLogicTypeArithType8ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeSignedIntTypeArithType8ArithType3;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorfork_27
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType8TextTypet1SignedIntTypeArithType8ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType8TextTypet1SignedIntTypeArithType8ArithType3;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType3;
            p3_out_data: out type_VectorTypeLogicTypeArithType3;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType3
        );
    end component;
    component vectorfork_50
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType8ArithType3ArithType1;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType8ArithType3ArithType1;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType1;
            p3_out_data: out type_VectorTypeLogicTypeArithType1;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType1
        );
    end component;
    component vectorjoin_28
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType8ArithType3;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType3;
            p2_in_data: in type_VectorTypeLogicTypeArithType3;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType3;
            p4_out_data: out type_VectorTypeIntTypeArithType8ArithType3;
            p4_out_last: out type_LastVectorTypeArithType0;
            p4_out_valid: out type_LogicType;
            p4_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapvector_function_30
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_IntTypeArithType8;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0;
            p1_in_data: in type_VectorTypeIntTypeArithType8ArithType3;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorjoin_51
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType8ArithType1;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType1;
            p2_in_data: in type_VectorTypeLogicTypeArithType1;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType1;
            p4_out_data: out type_VectorTypeIntTypeArithType8ArithType1;
            p4_out_last: out type_LastVectorTypeArithType0;
            p4_out_valid: out type_LogicType;
            p4_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component slidevector_29
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType8ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType8ArithType3ArithType1;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_54
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType8;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType8;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component constantbitvector_3
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_VectorTypeLogicTypeArithType24;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component zipvector_7
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0VectorTypeSignedIntTypeArithType8ArithType3TextTypet1VectorTypeSignedIntTypeArithType8ArithType3_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType8TextTypet1SignedIntTypeArithType8ArithType3;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_VectorTypeLogicTypeArithType24;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_VectorTypeVectorTypeLogicTypeArithType8ArithType3;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_data: type_VectorTypeSignedIntTypeArithType8ArithType3;
    signal s09_last: type_LastVectorTypeArithType0;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_NamedTupleTypeTextTypet0VectorTypeSignedIntTypeArithType8ArithType3TextTypet1VectorTypeSignedIntTypeArithType8ArithType3_t0;
    signal s13_last: type_LastVectorTypeArithType0;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s16_data: type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType8TextTypet1SignedIntTypeArithType8ArithType3;
    signal s17_data: type_VectorTypeLastVectorTypeArithType0ArithType3;
    signal s18_data: type_VectorTypeLogicTypeArithType3;
    signal s19_ready: type_ReadyVectorTypeArithType0;
    signal s20_ready: type_ReadyVectorTypeArithType0;
    signal s21_ready: type_ReadyVectorTypeArithType0;
    signal s22_data: type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType8TextTypet1SignedIntTypeArithType8ArithType3;
    signal s23_last: type_LastVectorTypeArithType0;
    signal s24_valid: type_LogicType;
    signal s25_ready: type_ReadyVectorTypeArithType0;
    signal s26_data: type_IntTypeArithType8;
    signal s27_data: type_IntTypeArithType8;
    signal s28_data: type_IntTypeArithType8;
    signal s29_last: type_LastVectorTypeArithType0;
    signal s30_last: type_LastVectorTypeArithType0;
    signal s31_last: type_LastVectorTypeArithType0;
    signal s32_valid: type_LogicType;
    signal s33_valid: type_LogicType;
    signal s34_valid: type_LogicType;
    signal s35_data: type_VectorTypeReadyVectorTypeArithType0ArithType3;
    signal s36_data: type_VectorTypeIntTypeArithType8ArithType3;
    signal s37_last: type_LastVectorTypeArithType0;
    signal s38_valid: type_LogicType;
    signal s39_ready: type_ReadyVectorTypeArithType0;
    signal s40_data: type_VectorTypeVectorTypeIntTypeArithType8ArithType3ArithType1;
    signal s41_data: type_VectorTypeLastVectorTypeArithType0ArithType1;
    signal s42_data: type_VectorTypeLogicTypeArithType1;
    signal s43_ready: type_ReadyVectorTypeArithType0;
    signal s44_data: type_VectorTypeVectorTypeIntTypeArithType8ArithType3ArithType1;
    signal s45_last: type_LastVectorTypeArithType0;
    signal s46_valid: type_LogicType;
    signal s47_ready: type_ReadyVectorTypeArithType0;
    signal s48_data: type_IntTypeArithType8;
    signal s49_last: type_LastVectorTypeArithType0;
    signal s50_valid: type_LogicType;
    signal s51_data: type_VectorTypeReadyVectorTypeArithType0ArithType1;
    signal s52_data: type_VectorTypeIntTypeArithType8ArithType1;
    signal s53_last: type_LastVectorTypeArithType0;
    signal s54_valid: type_LogicType;
    signal s55_ready: type_ReadyVectorTypeArithType0;
    signal s56_data: type_VectorTypeVectorTypeLogicTypeArithType8ArithType1;
    signal s57_last: type_LastVectorTypeArithType0;
    signal s58_valid: type_LogicType;
    signal s59_ready: type_ReadyVectorTypeArithType0;
    signal s60_data: type_VectorTypeLogicTypeArithType8;
    signal s61_last: type_LastVectorTypeArithType0;
    signal s62_valid: type_LogicType;
    signal s63_ready: type_ReadyVectorTypeArithType0;
    signal s64_data: type_IntTypeArithType8;
    signal s65_last: type_LastVectorTypeArithType0;
    signal s66_valid: type_LogicType;
    signal s67_ready: type_ReadyVectorTypeArithType0;
    signal s68_data: type_VectorTypeSignedIntTypeArithType8ArithType3;
    signal s69_last: type_LastVectorTypeArithType0;
    signal s70_valid: type_LogicType;
    signal s71_ready: type_ReadyVectorTypeArithType0;
    signal s72_data: type_LastVectorTypeArithType1;
    signal s73_data: type_ReadyVectorTypeArithType1;
begin
    
    U0_constantbitvector: constantbitvector_3 port map(
        p0_out_last => s01_last,
        clk => clk,
        p0_out_data => s00_data,
        p0_in_ready => s03_ready,
        p0_out_valid => s02_valid,
        reset => reset
    );
    U1_slidevector: slidevector_4 port map(
        clk => clk,
        p1_out_valid => s06_valid,
        p0_in_valid => s02_valid,
        p1_out_data => s04_data,
        p1_out_last => s05_last,
        p0_out_ready => s03_ready,
        p0_in_last => s01_last,
        reset => reset,
        p1_in_ready => s07_ready,
        p0_in_data => s00_data
    );
    U2_conversion: conversion_5 port map(
        clk => clk,
        p0_in_data => s04_data,
        p1_out_valid => s10_valid,
        p0_in_valid => s06_valid,
        p1_out_data => s08_data,
        p1_out_last => s09_last,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => s11_ready
    );
    U3_tuple: tuple_6 port map(
        clk => clk,
        p2_out_valid => s14_valid,
        p0_in_data => s68_data,
        p1_in_last => s09_last,
        p0_in_valid => s70_valid,
        p2_out_last => s13_last,
        p0_out_ready => s71_ready,
        p1_in_valid => s10_valid,
        p2_in_ready => s15_ready,
        p0_in_last => s69_last,
        reset => reset,
        p2_out_data => s12_data,
        p1_in_data => s08_data,
        p1_out_ready => s11_ready
    );
    U4_zipvector: zipvector_7 port map(
        clk => clk,
        p1_out_valid => s24_valid,
        p0_in_valid => s14_valid,
        p1_out_last => s23_last,
        p0_out_ready => s15_ready,
        p1_out_data => s22_data,
        p0_in_data => s12_data,
        p0_in_last => s13_last,
        reset => reset,
        p1_in_ready => s25_ready
    );
    U5_vectorfork: vectorfork_27 port map(
        clk => clk,
        p4_in_data(0) => s19_ready,
        p4_in_data(1) => s20_ready,
        p4_in_data(2) => s21_ready,
        p0_in_valid => s24_valid,
        p3_out_data => s18_data,
        p0_out_ready => s25_ready,
        p1_out_data => s16_data,
        p2_out_data => s17_data,
        p0_in_data => s22_data,
        p0_in_last => s23_last,
        reset => reset
    );
    U6_mapvector_function: mapvector_function_8 port map(
        clk => clk,
        p1_out_valid => s32_valid,
        p0_in_valid => s18_data(0),
        p0_in_data => s16_data(0),
        p1_out_last => s29_last,
        p0_out_ready => s19_ready,
        p1_out_data => s26_data,
        p0_in_last => s17_data(0),
        reset => reset,
        p1_in_ready => s35_data(0)
    );
    U7_mapvector_function: mapvector_function_8 port map(
        clk => clk,
        p1_out_valid => s33_valid,
        p0_in_valid => s18_data(1),
        p0_in_data => s16_data(1),
        p1_out_last => s30_last,
        p0_out_ready => s20_ready,
        p1_out_data => s27_data,
        p0_in_last => s17_data(1),
        reset => reset,
        p1_in_ready => s35_data(1)
    );
    U8_mapvector_function: mapvector_function_8 port map(
        clk => clk,
        p1_out_valid => s34_valid,
        p0_in_valid => s18_data(2),
        p0_in_data => s16_data(2),
        p1_out_last => s31_last,
        p0_out_ready => s21_ready,
        p1_out_data => s28_data,
        p0_in_last => s17_data(2),
        reset => reset,
        p1_in_ready => s35_data(2)
    );
    U9_vectorjoin: vectorjoin_28 port map(
        p4_out_data => s36_data,
        clk => clk,
        p2_in_data(0) => s32_valid,
        p2_in_data(1) => s33_valid,
        p2_in_data(2) => s34_valid,
        p0_in_data(0) => s26_data,
        p0_in_data(1) => s27_data,
        p0_in_data(2) => s28_data,
        p4_out_last => s37_last,
        p4_out_valid => s38_valid,
        p3_out_data => s35_data,
        p1_in_data(0) => s29_last,
        p1_in_data(1) => s30_last,
        p1_in_data(2) => s31_last,
        p4_in_ready => s39_ready,
        reset => reset
    );
    U10_slidevector: slidevector_29 port map(
        clk => clk,
        p1_out_data => s44_data,
        p0_in_data => s36_data,
        p1_out_valid => s46_valid,
        p0_in_valid => s38_valid,
        p1_out_last => s45_last,
        p0_out_ready => s39_ready,
        p0_in_last => s37_last,
        reset => reset,
        p1_in_ready => s47_ready
    );
    U11_vectorfork: vectorfork_50 port map(
        clk => clk,
        p1_out_data => s40_data,
        p0_in_valid => s46_valid,
        p3_out_data => s42_data,
        p0_in_data => s44_data,
        p0_out_ready => s47_ready,
        p4_in_data(0) => s43_ready,
        p2_out_data => s41_data,
        p0_in_last => s45_last,
        reset => reset
    );
    U12_mapvector_function: mapvector_function_30 port map(
        p0_out_last => s49_last,
        clk => clk,
        p1_in_last => s41_data(0),
        p0_out_data => s48_data,
        p0_in_ready => s51_data(0),
        p0_out_valid => s50_valid,
        p1_in_valid => s42_data(0),
        reset => reset,
        p1_in_data => s40_data(0),
        p1_out_ready => s43_ready
    );
    U13_vectorjoin: vectorjoin_51 port map(
        clk => clk,
        p4_out_last => s53_last,
        p1_in_data(0) => s49_last,
        p0_in_data(0) => s48_data,
        p4_out_valid => s54_valid,
        p2_in_data(0) => s50_valid,
        p3_out_data => s51_data,
        p4_in_ready => s55_ready,
        p4_out_data => s52_data,
        reset => reset
    );
    U14_conversion: conversion_52 port map(
        clk => clk,
        p1_out_valid => s58_valid,
        p0_in_valid => s54_valid,
        p0_in_data => s52_data,
        p1_out_last => s57_last,
        p0_out_ready => s55_ready,
        p1_out_data => s56_data,
        p0_in_last => s53_last,
        reset => reset,
        p1_in_ready => s59_ready
    );
    U15_joinvector: joinvector_53 port map(
        clk => clk,
        p1_out_valid => s62_valid,
        p0_in_valid => s58_valid,
        p0_in_data => s56_data,
        p1_out_last => s61_last,
        p1_out_data => s60_data,
        p0_out_ready => s59_ready,
        p0_in_last => s57_last,
        reset => reset,
        p1_in_ready => s63_ready
    );
    U16_conversion: conversion_54 port map(
        clk => clk,
        p1_out_valid => s66_valid,
        p0_in_valid => s62_valid,
        p1_out_last => s65_last,
        p0_out_ready => s63_ready,
        p1_out_data => s64_data,
        p0_in_last => s61_last,
        p0_in_data => s60_data,
        reset => reset,
        p1_in_ready => s67_ready
    );
    p1_out_data <= s64_data;
    p1_out_last <= "1" & s65_last when out_counter = stream_length - 1 else "0" & s65_last;
    p1_out_valid <= s66_valid;
    s67_ready <= p1_in_ready(s67_ready'high downto s67_ready'low);
    
    s68_data <= p0_in_data;
    s69_last <= p0_in_last(s69_last'high downto s69_last'low);
    s70_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s71_ready when in_counter = stream_length - 1 and s71_ready(s71_ready'high) = '1' else "0" & s71_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s70_valid = '1' and s71_ready(s71_ready'high) = '1' then
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
                if s66_valid = '1' and s67_ready(s67_ready'high) = '1' then
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
