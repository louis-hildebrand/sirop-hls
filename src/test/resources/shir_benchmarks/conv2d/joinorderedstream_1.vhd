library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity joinorderedstream_1 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1920ArithType1080;
        p0_in_last: in type_LastVectorTypeArithType2;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType2;
        p1_out_data: out type_OrderedStreamTypeIntTypeArithType32ArithType2073600;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end joinorderedstream_1;

architecture behavioral of joinorderedstream_1 is
    
    
    
    
    
    
begin
    
    p1_out_data <= p0_in_data;
    p1_out_valid <= p0_in_valid;
    
    last_signal: process(p0_in_last)
    begin
        p1_out_last <= p0_in_last(p0_in_last'high - 1 downto p0_in_last'low);
        if p0_in_last(p0_in_last'high) = '1' and p0_in_last(p0_in_last'high - 1) = '1' then
            p1_out_last(p1_out_last'high) <= '1';
        else
            p1_out_last(p1_out_last'high) <= '0';
        end if;
    end process;
    
    ready_signal: process(p0_in_last, p1_in_ready)
    begin
        p0_out_ready <= p1_in_ready(p1_in_ready'high) & p1_in_ready;
        if p0_in_last(p0_in_last'high - 1) = '1' and p1_in_ready(p1_in_ready'high - 1) = '1' then
            -- there will never be a demand to repeat the inner stream only!
            p0_out_ready(p0_out_ready'high - 1) <= '1';
        else
            p0_out_ready(p0_out_ready'high - 1) <= '0';
        end if;
    end process;
    
end behavioral;
