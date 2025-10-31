library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_10 is
    generic(
        stream_length: type_NaturalNumberType := 840
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType840;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeIntTypeArithType16ArithType840;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end mapsimpleorderedstream_10;

architecture behavioral of mapsimpleorderedstream_10 is
    
    component mulint_5
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_6
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeLogicTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component dropvector_7
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeLogicTypeArithType16;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_8
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType16;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType16;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_IntTypeArithType32;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_VectorTypeLogicTypeArithType32;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_data: type_VectorTypeLogicTypeArithType16;
    signal s09_last: type_LastVectorTypeArithType0;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_IntTypeArithType16;
    signal s13_last: type_LastVectorTypeArithType0;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s16_data: type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0;
    signal s17_last: type_LastVectorTypeArithType0;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType0;
    signal s20_data: type_LastVectorTypeArithType1;
    signal s21_data: type_ReadyVectorTypeArithType1;
begin
    
    U0_mulint: mulint_5 port map(
        clk => clk,
        p1_out_valid => s02_valid,
        p0_in_valid => s18_valid,
        p1_out_last => s01_last,
        p0_out_ready => s19_ready,
        p0_in_last => s17_last,
        reset => reset,
        p1_in_ready => s03_ready,
        p1_out_data => s00_data,
        p0_in_data => s16_data
    );
    U1_conversion: conversion_6 port map(
        clk => clk,
        p1_out_valid => s06_valid,
        p0_in_valid => s02_valid,
        p1_out_last => s05_last,
        p0_in_data => s00_data,
        p0_out_ready => s03_ready,
        p0_in_last => s01_last,
        reset => reset,
        p1_in_ready => s07_ready,
        p1_out_data => s04_data
    );
    U2_dropvector: dropvector_7 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s06_valid,
        p1_out_last => s09_last,
        p1_out_data => s08_data,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => s11_ready,
        p0_in_data => s04_data
    );
    U3_conversion: conversion_8 port map(
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => s10_valid,
        p1_out_last => s13_last,
        p0_out_ready => s11_ready,
        p0_in_data => s08_data,
        p0_in_last => s09_last,
        reset => reset,
        p1_in_ready => s15_ready,
        p1_out_data => s12_data
    );
    p1_out_data <= s12_data;
    p1_out_last <= "1" & s13_last when out_counter = stream_length - 1 else "0" & s13_last;
    p1_out_valid <= s14_valid;
    s15_ready <= p1_in_ready(s15_ready'high downto s15_ready'low);
    
    s16_data <= p0_in_data;
    s17_last <= p0_in_last(s17_last'high downto s17_last'low);
    s18_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s19_ready when in_counter = stream_length - 1 and s19_ready(s19_ready'high) = '1' else "0" & s19_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s18_valid = '1' and s19_ready(s19_ready'high) = '1' then
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
                if s14_valid = '1' and s15_ready(s15_ready'high) = '1' then
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
