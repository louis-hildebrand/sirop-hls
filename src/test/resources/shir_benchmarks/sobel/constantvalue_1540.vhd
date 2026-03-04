library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity constantvalue_1540 is
    generic(
        value: type_VectorTypeLogicTypeArithType32 := "00000000000000000000000000000001";
        loop_all: type_LogicType := '1'
        );
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_SignedIntTypeArithType32;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end constantvalue_1540;
    
    architecture behavioral of constantvalue_1540 is
        
        
        
        
        
        
    begin
        
        p0_out_data(value'high downto value'low) <= value;
        p0_out_data(p0_out_data'high downto p0_out_data'low + value'length) <= (others => '0');
        p0_out_last <= (others => '0');
        p0_out_valid <= '1';
        
    end behavioral;
