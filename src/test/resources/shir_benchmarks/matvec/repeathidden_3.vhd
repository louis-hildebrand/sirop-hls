library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity repeathidden_3 is
    generic(
        stream_length: type_NaturalNumberType := 256
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_out_data: out type_OrderedStreamTypeIntTypeArithType16ArithType256;
        p0_out_last: out type_LastVectorTypeArithType1;
        p0_out_valid: out type_LogicType;
        p0_in_ready: in type_ReadyVectorTypeArithType1
    );
end repeathidden_3;

architecture behavioral of repeathidden_3 is
    
    component counterinteger_2
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_OrderedStreamTypeIntTypeArithType16ArithType256;
            p0_out_last: out type_LastVectorTypeArithType1;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    
    signal elem_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_OrderedStreamTypeIntTypeArithType16ArithType256;
    signal s01_last: type_LastVectorTypeArithType1;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType1;
begin
    
    U0_counterinteger: counterinteger_2 port map(
        clk => clk,
        p0_out_valid => s02_valid,
        p0_out_last => s01_last,
        reset => reset,
        p0_in_ready => s03_ready,
        p0_out_data => s00_data
    );
    p0_out_data <= s00_data;
    p0_out_last <= s01_last;
    p0_out_valid <= s02_valid;
    
    ready_signal: process(p0_in_ready, elem_counter)
    begin
        s03_ready <= p0_in_ready;
        if elem_counter < stream_length - 1 then
            s03_ready(s03_ready'high) <= '0';
        end if;
    end process;
    
    process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                elem_counter <= 0;
            else
                if s02_valid = '1' and p0_in_ready(p0_in_ready'high) = '1' then
                    if elem_counter < stream_length - 1 then
                        elem_counter <= elem_counter + 1;
                    else
                        elem_counter <= 0;
                    end if;
                end if;
            end if;
        end if;
    end process;
    
end behavioral;
