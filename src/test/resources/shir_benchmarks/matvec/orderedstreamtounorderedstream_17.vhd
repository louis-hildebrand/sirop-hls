library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity orderedstreamtounorderedstream_17 is
    generic(
        stream_length: type_NaturalNumberType := 256
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeIntTypeArithType16ArithType256;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_UnorderedStreamTypeIntTypeArithType16ArithType256;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end orderedstreamtounorderedstream_17;

architecture behavioral of orderedstreamtounorderedstream_17 is
    
    
    
    signal counter: natural range 0 to stream_length - 1 := 0;
    
    
begin
    
    p1_out_data.data <= p0_in_data;
    p1_out_data.idx <= std_logic_vector(to_unsigned(counter, p1_out_data.idx'length));
    p1_out_last <= p0_in_last;
    p1_out_valid <= p0_in_valid;
    p0_out_ready <= p1_in_ready;
    
    counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                counter <= 0;
            else
                if p0_in_valid = '1' and p1_in_ready(p1_in_ready'high - 1) = '1' then
                    if counter < stream_length - 1 then
                        counter <= counter + 1;
                    else
                        counter <= 0;
                    end if;
                end if;
            end if;
        end if;
    end process;
    
end behavioral;
