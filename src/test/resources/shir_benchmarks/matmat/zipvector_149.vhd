library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity zipvector_149 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_NamedTupleTypeTextTypet0VectorTypeIntTypeArithType16ArithType16TextTypet1VectorTypeIntTypeArithType16ArithType16_t0;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType16;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end zipvector_149;

architecture behavioral of zipvector_149 is
    
    
    
    
    
    
begin
    
    connect: process(p0_in_data)
    begin
        for i in 0 to p1_out_data'length - 1 loop
            p1_out_data(i).t0 <= p0_in_data.t0(i);
            p1_out_data(i).t1 <= p0_in_data.t1(i);
        end loop;
    end process;
    p1_out_last <= p0_in_last;
    p1_out_valid <= p0_in_valid;
    p0_out_ready <= p1_in_ready;
    
end behavioral;
