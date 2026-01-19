library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity select_145 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16_t0;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end select_145;

architecture behavioral of select_145 is
    
    
    
    signal selected_data: type_OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16;
    
    
begin
    
    p1_out_data <= selected_data;
    p1_out_last <= p0_in_last;
    p1_out_valid <= p0_in_valid;
    p0_out_ready <= p1_in_ready;
    selected_data <= p0_in_data.t0;
end behavioral;
