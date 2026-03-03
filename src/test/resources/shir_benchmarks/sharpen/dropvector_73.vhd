library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity dropvector_73 is
    generic(
        low_elements: type_NaturalNumberType := 0;
        high_elements: type_NaturalNumberType := 1
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_VectorTypeIntTypeArithType32ArithType3;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_VectorTypeIntTypeArithType32ArithType2;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end dropvector_73;

architecture behavioral of dropvector_73 is
    
    
    
    
    
    
begin
    
    p1_out_data <= type_VectorTypeIntTypeArithType32ArithType2(p0_in_data((type_VectorTypeIntTypeArithType32ArithType3'high - high_elements) downto (type_VectorTypeIntTypeArithType32ArithType3'low + low_elements)));
    p1_out_last <= p0_in_last;
    p1_out_valid <= p0_in_valid;
    p0_out_ready <= p1_in_ready;
    
end behavioral;
