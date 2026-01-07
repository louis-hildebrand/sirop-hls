library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_126 is
    generic(
        stream_length: type_NaturalNumberType := 1078
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeVectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3ArithType1918ArithType1078;
        p0_in_last: in type_LastVectorTypeArithType2;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType2;
        p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078;
        p1_out_last: out type_LastVectorTypeArithType2;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType2
    );
end mapsimpleorderedstream_126;

architecture behavioral of mapsimpleorderedstream_126 is
    
    component mapsimpleorderedstream_124
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeVectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeSignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_OrderedStreamTypeSignedIntTypeArithType32ArithType1918;
    signal s01_last: type_LastVectorTypeArithType1;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType1;
    signal s04_data: type_OrderedStreamTypeVectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3ArithType1918;
    signal s05_last: type_LastVectorTypeArithType1;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType1;
    signal s08_data: type_LastVectorTypeArithType2;
    signal s09_data: type_ReadyVectorTypeArithType2;
begin
    
    U0_mapsimpleorderedstream: mapsimpleorderedstream_124 port map(
        clk => clk,
        p1_out_data => s00_data,
        p0_in_data => s04_data,
        p1_out_valid => s02_valid,
        p0_in_valid => s06_valid,
        p0_out_ready => s07_ready,
        reset => reset,
        p1_in_ready => s03_ready,
        p1_out_last => s01_last,
        p0_in_last => s05_last
    );
    p1_out_data <= s00_data;
    p1_out_last <= "1" & s01_last when out_counter = stream_length - 1 else "0" & s01_last;
    p1_out_valid <= s02_valid;
    s03_ready <= p1_in_ready(s03_ready'high downto s03_ready'low);
    
    s04_data <= p0_in_data;
    s05_last <= p0_in_last(s05_last'high downto s05_last'low);
    s06_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s07_ready when in_counter = stream_length - 1 and s07_ready(s07_ready'high) = '1' else "0" & s07_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s06_valid = '1' and s07_ready(s07_ready'high) = '1' then
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
                if s02_valid = '1' and s03_ready(s03_ready'high) = '1' then
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
