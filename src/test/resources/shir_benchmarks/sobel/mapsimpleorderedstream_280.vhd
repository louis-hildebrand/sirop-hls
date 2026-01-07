library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_280 is
    generic(
        stream_length: type_NaturalNumberType := 1918
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeSignedIntTypeArithType32ArithType1918;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end mapsimpleorderedstream_280;

architecture behavioral of mapsimpleorderedstream_280 is
    
    component constantvalue_276
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_SignedIntTypeArithType32;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component constantvalue_277
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_SignedIntTypeArithType32;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_278
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
            p2_in_data: in type_SignedIntTypeArithType32;
            p2_in_last: in type_LastVectorTypeArithType0;
            p2_in_valid: in type_LogicType;
            p2_out_ready: out type_ReadyVectorTypeArithType0;
            p3_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p3_out_last: out type_LastVectorTypeArithType0;
            p3_out_valid: out type_LogicType;
            p3_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_SignedIntTypeArithType32;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_SignedIntTypeArithType32;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s09_last: type_LastVectorTypeArithType0;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_SignedIntTypeArithType32;
    signal s13_last: type_LastVectorTypeArithType0;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s16_data: type_LastVectorTypeArithType1;
    signal s17_data: type_ReadyVectorTypeArithType1;
begin
    
    U0_constantvalue: constantvalue_276 port map(
        p0_out_last => s01_last,
        clk => clk,
        p0_in_ready => s03_ready,
        p0_out_valid => s02_valid,
        reset => reset,
        p0_out_data => s00_data
    );
    U1_constantvalue: constantvalue_277 port map(
        p0_out_last => s05_last,
        clk => clk,
        p0_in_ready => s07_ready,
        p0_out_valid => s06_valid,
        reset => reset,
        p0_out_data => s04_data
    );
    U2_tuple: tuple_278 port map(
        clk => clk,
        p1_in_last => s01_last,
        p0_in_valid => s14_valid,
        p3_out_valid => s10_valid,
        p2_out_ready => s07_ready,
        p0_in_data => s12_data,
        p2_in_valid => s06_valid,
        p2_in_data => s04_data,
        p0_out_ready => s15_ready,
        p1_in_valid => s02_valid,
        p3_out_last => s09_last,
        p1_in_data => s00_data,
        p0_in_last => s13_last,
        reset => reset,
        p2_in_last => s05_last,
        p3_out_data => s08_data,
        p1_out_ready => s03_ready,
        p3_in_ready => s11_ready
    );
    p1_out_data <= s08_data;
    p1_out_last <= "1" & s09_last when out_counter = stream_length - 1 else "0" & s09_last;
    p1_out_valid <= s10_valid;
    s11_ready <= p1_in_ready(s11_ready'high downto s11_ready'low);
    
    s12_data <= p0_in_data;
    s13_last <= p0_in_last(s13_last'high downto s13_last'low);
    s14_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s15_ready when in_counter = stream_length - 1 and s15_ready(s15_ready'high) = '1' else "0" & s15_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s14_valid = '1' and s15_ready(s15_ready'high) = '1' then
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
                if s10_valid = '1' and s11_ready(s11_ready'high) = '1' then
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
