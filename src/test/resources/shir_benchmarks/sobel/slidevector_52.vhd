library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity slidevector_52 is
    generic(
        step_size: type_NaturalNumberType := 2
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_VectorTypeSignedIntTypeArithType64ArithType9;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_VectorTypeVectorTypeSignedIntTypeArithType64ArithType3ArithType4;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end slidevector_52;

architecture behavioral of slidevector_52 is
    
    
    
    
    
    
begin
    
    data_signal: process(p0_in_data)
    begin
        for i in p1_out_data'low to p1_out_data'high loop
            for j in p1_out_data(0)'low to p1_out_data(0)'high loop
                p1_out_data(i)(j) <= p0_in_data(i * step_size + j);
            end loop;
        end loop;
    end process;
    
    p1_out_valid <= p0_in_valid;
    p0_out_ready <= p1_in_ready;
    
end behavioral;
