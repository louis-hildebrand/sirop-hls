library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity vectorjoin_28 is
    generic(
        num_clients: type_NaturalNumberType := 3
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_VectorTypeIntTypeArithType8ArithType3;
        p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType3;
        p2_in_data: in type_VectorTypeLogicTypeArithType3;
        p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType3;
        p4_out_data: out type_VectorTypeIntTypeArithType8ArithType3;
        p4_out_last: out type_LastVectorTypeArithType0;
        p4_out_valid: out type_LogicType;
        p4_in_ready: in type_ReadyVectorTypeArithType0
    );
end vectorjoin_28;

architecture behavioral of vectorjoin_28 is
    
    
    
    
    
    
begin
    
    p4_out_data <= p0_in_data;
    p4_out_last <= p1_in_data(0);
    -- TODO this is not very generic! check that all clients have been valid, then send valid signal
    p4_out_valid <= p2_in_data(0);
    p3_out_data <= (others => p4_in_ready);
    
end behavioral;
