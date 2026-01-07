library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapvector_function_240 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_VectorTypeIntTypeArithType32ArithType2;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_IntTypeArithType32;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end mapvector_function_240;

architecture behavioral of mapvector_function_240 is
    
    component addint_243
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
    component dropvector_245
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
    component conversion_246
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
    component vectortotuple_242
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
    component conversion_244
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
    
    
    
    signal s00_data: type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_IntTypeArithType33;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_data: type_VectorTypeLogicTypeArithType33;
    signal s09_last: type_LastVectorTypeArithType0;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_VectorTypeLogicTypeArithType32;
    signal s13_last: type_LastVectorTypeArithType0;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType0;
begin
    
    U0_vectortotuple: vectortotuple_242 port map(
        clk => clk,
        p1_out_valid => s02_valid,
        p0_in_valid => p0_in_valid,
        p1_out_last => s01_last,
        p0_out_ready => p0_out_ready,
        p1_out_data => s00_data,
        p0_in_last => p0_in_last,
        reset => reset,
        p1_in_ready => s03_ready,
        p0_in_data => p0_in_data
    );
    U1_addint: addint_243 port map(
        clk => clk,
        p1_out_valid => s06_valid,
        p0_in_valid => s02_valid,
        p1_out_last => s05_last,
        p1_out_data => s04_data,
        p0_out_ready => s03_ready,
        p0_in_last => s01_last,
        reset => reset,
        p1_in_ready => s07_ready,
        p0_in_data => s00_data
    );
    U2_conversion: conversion_244 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s06_valid,
        p1_out_data => s08_data,
        p0_in_data => s04_data,
        p1_out_last => s09_last,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => s11_ready
    );
    U3_dropvector: dropvector_245 port map(
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
    U4_conversion: conversion_246 port map(
        clk => clk,
        p1_out_valid => p1_out_valid,
        p0_in_valid => s14_valid,
        p1_out_last => p1_out_last,
        p0_out_ready => s15_ready,
        p0_in_last => s13_last,
        reset => reset,
        p1_in_ready => p1_in_ready,
        p1_out_data => p1_out_data,
        p0_in_data => s12_data
    );
    
    
end behavioral;
