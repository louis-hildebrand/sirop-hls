library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity vectorfork_70 is
    generic(
        num_clients: type_NaturalNumberType := 4
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType64ArithType3ArithType4;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType64ArithType3ArithType4;
        p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType4;
        p3_out_data: out type_VectorTypeLogicTypeArithType4;
        p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType4
    );
end vectorfork_70;

architecture behavioral of vectorfork_70 is
    
    
    
    
    
    
begin
    
    p1_out_data <= p0_in_data;
    p2_out_data <= (others => p0_in_last);
    p3_out_data <= (others => p0_in_valid);
    -- TODO this is not very generic! check that all clients have been ready, then send ready signal
    p0_out_ready <= p4_in_data(0);
    
end behavioral;
