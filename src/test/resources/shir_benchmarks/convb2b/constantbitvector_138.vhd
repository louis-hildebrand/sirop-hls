library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity constantbitvector_138 is
    generic(
        value: type_VectorTypeLogicTypeArithType128 := "00000000000000000000000000000001000000000000000000000000000001000000000000000000000000000000001000000000000000000000000000000001";
        loop_all: type_LogicType := '1'
        );
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_VectorTypeLogicTypeArithType128;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end constantbitvector_138;
    
    architecture behavioral of constantbitvector_138 is
        
        
        
        
        
        
    begin
        
        p0_out_data(value'high downto value'low) <= value;
        p0_out_data(p0_out_data'high downto p0_out_data'low + value'length) <= (others => '0');
        p0_out_last <= (others => '0');
        p0_out_valid <= '1';
        
    end behavioral;
