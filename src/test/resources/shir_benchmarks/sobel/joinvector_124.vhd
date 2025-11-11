library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity joinvector_124 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_VectorTypeVectorTypeLogicTypeArithType64ArithType1;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_VectorTypeLogicTypeArithType64;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end joinvector_124;

architecture behavioral of joinvector_124 is
    
    
    
    
    
    
begin
    
    p1_out_valid <= p0_in_valid;
    p0_out_ready <= p1_in_ready;
    
    process(p0_in_data)
        constant chunksize: natural := p0_in_data(0)'length;
    begin
        for i in 0 to p0_in_data'length-1 loop
            for j in 0 to chunksize-1 loop
                p1_out_data(i*chunksize + j) <= p0_in_data(i)(j);
            end loop;
        end loop;
        --for i in 0 to p0_in_data'length-1 loop
            --    p1_out_data((i+1)*chunksize-1 downto i*chunksize) <= p0_in_data(i);
            --end loop;
        end process;
        
    end behavioral;
