library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity vectorjoin_135 is
    generic(
        num_clients: type_NaturalNumberType := 1
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_VectorTypeIntTypeArithType32ArithType1;
        p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType1;
        p2_in_data: in type_VectorTypeLogicTypeArithType1;
        p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType1;
        p4_out_data: out type_VectorTypeIntTypeArithType32ArithType1;
        p4_out_last: out type_LastVectorTypeArithType0;
        p4_out_valid: out type_LogicType;
        p4_in_ready: in type_ReadyVectorTypeArithType0
    );
end vectorjoin_135;

architecture behavioral of vectorjoin_135 is
    
    
    
    
    
    
begin
    
    p4_out_data <= p0_in_data;
    p4_out_last <= p1_in_data(0);
    -- TODO this is not very generic! check that all clients have been valid, then send valid signal
    p4_out_valid <= p2_in_data(0);
    p3_out_data <= (others => p4_in_ready);
    
end behavioral;
