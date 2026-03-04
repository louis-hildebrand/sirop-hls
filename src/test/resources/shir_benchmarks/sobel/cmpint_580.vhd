library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity cmpint_580 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_LogicType;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end cmpint_580;

architecture behavioral of cmpint_580 is
    
    
    
    
    
    
begin
    
    p1_out_last <= p0_in_last;
    p1_out_valid <= p0_in_valid;
    p0_out_ready <= p1_in_ready;
    
    -- actual comparison is generated
        p1_out_data <= '1' when signed(p0_in_data.t0) <= signed(p0_in_data.t1) else '0';
    end behavioral;
