library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapvector_function_30 is
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
end mapvector_function_30;

architecture behavioral of mapvector_function_30 is
    
    component tuple_43
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType8;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_IntTypeArithType8;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType8TextTypet1IntTypeArithType8_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_40
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
    component dropvector_32
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType8ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeIntTypeArithType8ArithType2;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component addint_34
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType8TextTypet1IntTypeArithType8_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component dropvector_36
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeLogicTypeArithType8;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component joinvector_41
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
    component id_49
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType8ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeIntTypeArithType8ArithType3;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_38
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType8ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeIntTypeArithType8ArithType3;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_31
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType8ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeIntTypeArithType8ArithType3;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectortotuple_33
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType8ArithType2;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType8TextTypet1IntTypeArithType8_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_37
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
    component dropvector_46
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeLogicTypeArithType8;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_42
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
    component dropvector_39
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType8ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeIntTypeArithType8ArithType1;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component distributor_48
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType8ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType8ArithType3ArithType2;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType2;
            p3_out_data: out type_VectorTypeLogicTypeArithType2;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType2
        );
    end component;
    component conversion_47
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
    component conversion_45
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeLogicTypeArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_35
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeLogicTypeArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component addint_44
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType8TextTypet1IntTypeArithType8_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    
    
    signal s00_data: type_VectorTypeIntTypeArithType8ArithType3;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_VectorTypeIntTypeArithType8ArithType2;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_data: type_NamedTupleTypeTextTypet0IntTypeArithType8TextTypet1IntTypeArithType8_t0;
    signal s09_last: type_LastVectorTypeArithType0;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_IntTypeArithType9;
    signal s13_last: type_LastVectorTypeArithType0;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s16_data: type_VectorTypeLogicTypeArithType9;
    signal s17_last: type_LastVectorTypeArithType0;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType0;
    signal s20_data: type_VectorTypeLogicTypeArithType8;
    signal s21_last: type_LastVectorTypeArithType0;
    signal s22_valid: type_LogicType;
    signal s23_ready: type_ReadyVectorTypeArithType0;
    signal s24_data: type_VectorTypeIntTypeArithType8ArithType3;
    signal s25_last: type_LastVectorTypeArithType0;
    signal s26_valid: type_LogicType;
    signal s27_ready: type_ReadyVectorTypeArithType0;
    signal s28_data: type_VectorTypeIntTypeArithType8ArithType1;
    signal s29_last: type_LastVectorTypeArithType0;
    signal s30_valid: type_LogicType;
    signal s31_ready: type_ReadyVectorTypeArithType0;
    signal s32_data: type_VectorTypeVectorTypeLogicTypeArithType8ArithType1;
    signal s33_last: type_LastVectorTypeArithType0;
    signal s34_valid: type_LogicType;
    signal s35_ready: type_ReadyVectorTypeArithType0;
    signal s36_data: type_VectorTypeLogicTypeArithType8;
    signal s37_last: type_LastVectorTypeArithType0;
    signal s38_valid: type_LogicType;
    signal s39_ready: type_ReadyVectorTypeArithType0;
    signal s40_data: type_IntTypeArithType8;
    signal s41_last: type_LastVectorTypeArithType0;
    signal s42_valid: type_LogicType;
    signal s43_ready: type_ReadyVectorTypeArithType0;
    signal s44_data: type_IntTypeArithType8;
    signal s45_last: type_LastVectorTypeArithType0;
    signal s46_valid: type_LogicType;
    signal s47_ready: type_ReadyVectorTypeArithType0;
    signal s48_data: type_NamedTupleTypeTextTypet0IntTypeArithType8TextTypet1IntTypeArithType8_t0;
    signal s49_last: type_LastVectorTypeArithType0;
    signal s50_valid: type_LogicType;
    signal s51_ready: type_ReadyVectorTypeArithType0;
    signal s52_data: type_IntTypeArithType9;
    signal s53_last: type_LastVectorTypeArithType0;
    signal s54_valid: type_LogicType;
    signal s55_ready: type_ReadyVectorTypeArithType0;
    signal s56_data: type_VectorTypeLogicTypeArithType9;
    signal s57_last: type_LastVectorTypeArithType0;
    signal s58_valid: type_LogicType;
    signal s59_ready: type_ReadyVectorTypeArithType0;
    signal s60_data: type_VectorTypeLogicTypeArithType8;
    signal s61_last: type_LastVectorTypeArithType0;
    signal s62_valid: type_LogicType;
    signal s63_ready: type_ReadyVectorTypeArithType0;
    signal s64_data: type_VectorTypeVectorTypeIntTypeArithType8ArithType3ArithType2;
    signal s65_data: type_VectorTypeLastVectorTypeArithType0ArithType2;
    signal s66_data: type_VectorTypeLogicTypeArithType2;
    signal s67_ready: type_ReadyVectorTypeArithType0;
    signal s68_ready: type_ReadyVectorTypeArithType0;
    signal s69_data: type_VectorTypeIntTypeArithType8ArithType3;
    signal s70_last: type_LastVectorTypeArithType0;
    signal s71_valid: type_LogicType;
    signal s72_ready: type_ReadyVectorTypeArithType0;
begin
    
    U0_id: id_31 port map(
        clk => clk,
        p0_in_data => s64_data(0),
        p1_out_valid => s02_valid,
        p0_in_valid => s66_data(0),
        p1_out_data => s00_data,
        p1_out_last => s01_last,
        p0_out_ready => s67_ready,
        p0_in_last => s65_data(0),
        reset => reset,
        p1_in_ready => s03_ready
    );
    U1_dropvector: dropvector_32 port map(
        clk => clk,
        p0_in_data => s00_data,
        p1_out_valid => s06_valid,
        p0_in_valid => s02_valid,
        p1_out_last => s05_last,
        p1_out_data => s04_data,
        p0_out_ready => s03_ready,
        p0_in_last => s01_last,
        reset => reset,
        p1_in_ready => s07_ready
    );
    U2_vectortotuple: vectortotuple_33 port map(
        clk => clk,
        p1_out_data => s08_data,
        p1_out_valid => s10_valid,
        p0_in_valid => s06_valid,
        p1_out_last => s09_last,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => s11_ready,
        p0_in_data => s04_data
    );
    U3_addint: addint_34 port map(
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => s10_valid,
        p0_in_data => s08_data,
        p1_out_last => s13_last,
        p0_out_ready => s11_ready,
        p0_in_last => s09_last,
        reset => reset,
        p1_in_ready => s15_ready,
        p1_out_data => s12_data
    );
    U4_conversion: conversion_35 port map(
        clk => clk,
        p1_out_valid => s18_valid,
        p0_in_valid => s14_valid,
        p1_out_last => s17_last,
        p0_out_ready => s15_ready,
        p0_in_data => s12_data,
        p0_in_last => s13_last,
        reset => reset,
        p1_in_ready => s19_ready,
        p1_out_data => s16_data
    );
    U5_dropvector: dropvector_36 port map(
        clk => clk,
        p1_out_valid => s22_valid,
        p0_in_valid => s18_valid,
        p1_out_last => s21_last,
        p1_out_data => s20_data,
        p0_out_ready => s19_ready,
        p0_in_data => s16_data,
        p0_in_last => s17_last,
        reset => reset,
        p1_in_ready => s23_ready
    );
    U6_conversion: conversion_37 port map(
        clk => clk,
        p1_out_valid => s42_valid,
        p0_in_valid => s22_valid,
        p1_out_last => s41_last,
        p0_out_ready => s23_ready,
        p1_out_data => s40_data,
        p0_in_last => s21_last,
        p0_in_data => s20_data,
        reset => reset,
        p1_in_ready => s43_ready
    );
    U7_id: id_38 port map(
        clk => clk,
        p0_in_data => s64_data(1),
        p1_out_valid => s26_valid,
        p0_in_valid => s66_data(1),
        p1_out_data => s24_data,
        p1_out_last => s25_last,
        p0_out_ready => s68_ready,
        p0_in_last => s65_data(1),
        reset => reset,
        p1_in_ready => s27_ready
    );
    U8_dropvector: dropvector_39 port map(
        clk => clk,
        p0_in_data => s24_data,
        p1_out_valid => s30_valid,
        p0_in_valid => s26_valid,
        p1_out_last => s29_last,
        p0_out_ready => s27_ready,
        p1_out_data => s28_data,
        p0_in_last => s25_last,
        reset => reset,
        p1_in_ready => s31_ready
    );
    U9_conversion: conversion_40 port map(
        clk => clk,
        p1_out_valid => s34_valid,
        p0_in_valid => s30_valid,
        p0_in_data => s28_data,
        p1_out_last => s33_last,
        p0_out_ready => s31_ready,
        p1_out_data => s32_data,
        p0_in_last => s29_last,
        reset => reset,
        p1_in_ready => s35_ready
    );
    U10_joinvector: joinvector_41 port map(
        clk => clk,
        p1_out_valid => s38_valid,
        p0_in_valid => s34_valid,
        p0_in_data => s32_data,
        p1_out_last => s37_last,
        p1_out_data => s36_data,
        p0_out_ready => s35_ready,
        p0_in_last => s33_last,
        reset => reset,
        p1_in_ready => s39_ready
    );
    U11_conversion: conversion_42 port map(
        clk => clk,
        p1_out_valid => s46_valid,
        p0_in_valid => s38_valid,
        p1_out_last => s45_last,
        p0_out_ready => s39_ready,
        p1_out_data => s44_data,
        p0_in_last => s37_last,
        p0_in_data => s36_data,
        reset => reset,
        p1_in_ready => s47_ready
    );
    U12_tuple: tuple_43 port map(
        clk => clk,
        p2_out_valid => s50_valid,
        p1_in_last => s45_last,
        p0_in_valid => s42_valid,
        p0_in_data => s40_data,
        p1_in_data => s44_data,
        p2_out_last => s49_last,
        p0_out_ready => s43_ready,
        p1_in_valid => s46_valid,
        p2_in_ready => s51_ready,
        p0_in_last => s41_last,
        reset => reset,
        p2_out_data => s48_data,
        p1_out_ready => s47_ready
    );
    U13_addint: addint_44 port map(
        clk => clk,
        p1_out_valid => s54_valid,
        p0_in_valid => s50_valid,
        p0_in_data => s48_data,
        p1_out_last => s53_last,
        p0_out_ready => s51_ready,
        p0_in_last => s49_last,
        reset => reset,
        p1_in_ready => s55_ready,
        p1_out_data => s52_data
    );
    U14_conversion: conversion_45 port map(
        clk => clk,
        p1_out_valid => s58_valid,
        p0_in_valid => s54_valid,
        p1_out_last => s57_last,
        p0_out_ready => s55_ready,
        p0_in_data => s52_data,
        p0_in_last => s53_last,
        reset => reset,
        p1_in_ready => s59_ready,
        p1_out_data => s56_data
    );
    U15_dropvector: dropvector_46 port map(
        clk => clk,
        p1_out_valid => s62_valid,
        p0_in_valid => s58_valid,
        p1_out_last => s61_last,
        p1_out_data => s60_data,
        p0_out_ready => s59_ready,
        p0_in_data => s56_data,
        p0_in_last => s57_last,
        reset => reset,
        p1_in_ready => s63_ready
    );
    U16_conversion: conversion_47 port map(
        clk => clk,
        p1_out_valid => p0_out_valid,
        p0_in_valid => s62_valid,
        p1_out_last => p0_out_last,
        p0_out_ready => s63_ready,
        p1_out_data => p0_out_data,
        p0_in_last => s61_last,
        p0_in_data => s60_data,
        reset => reset,
        p1_in_ready => p0_in_ready
    );
    U17_distributor: distributor_48 port map(
        clk => clk,
        p0_in_data => s69_data,
        p0_in_valid => s71_valid,
        p4_in_data(0) => s67_ready,
        p4_in_data(1) => s68_ready,
        p0_out_ready => s72_ready,
        p2_out_data => s65_data,
        p0_in_last => s70_last,
        reset => reset,
        p3_out_data => s66_data,
        p1_out_data => s64_data
    );
    U18_id: id_49 port map(
        clk => clk,
        p0_in_data => p1_in_data,
        p1_out_valid => s71_valid,
        p0_in_valid => p1_in_valid,
        p1_out_data => s69_data,
        p1_out_last => s70_last,
        p0_out_ready => p1_out_ready,
        p0_in_last => p1_in_last,
        reset => reset,
        p1_in_ready => s72_ready
    );
    
    
end behavioral;
