library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity resizelowinteger_1427 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_SignedIntTypeArithType32;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_SignedIntTypeArithType31;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end resizelowinteger_1427;

architecture behavioral of resizelowinteger_1427 is
    
    
    
    
    
    
begin
    
    -- because this only affects the low bits and not the signed bit,
    -- the same template works for both signed and unsigned integers.
    
    CASE_PAD: if p1_out_data'length >= p0_in_data'length generate
        -- pads zeros on the right-hand side: 1.01 --> 1.010000
        p1_out_data(p1_out_data'high downto p1_out_data'high - p0_in_data'length + 1) <= p0_in_data;
        p1_out_data(p1_out_data'high - p0_in_data'length downto p1_out_data'low) <= (others => '0');
    end generate;
    
    CASE_TRUNC: if p1_out_data'length < p0_in_data'length generate
        -- truncates on the right-hand side: 1.010110 --> 1.01
        p1_out_data <= p0_in_data(p0_in_data'high downto p0_in_data'high - p1_out_data'length + 1);
    end generate;
    
    p1_out_last <= p0_in_last;
    p1_out_valid <= p0_in_valid;
    p0_out_ready <= p1_in_ready;
    
end behavioral;
