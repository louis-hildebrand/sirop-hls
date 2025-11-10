library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity addint_284 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_IntTypeArithType33;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end addint_284;

architecture behavioral of addint_284 is
    
    
    
    signal a1: std_logic_vector(p1_out_data'range) := (others => '0');
    signal a2: std_logic_vector(p1_out_data'range) := (others => '0');
    
    
begin
    
    fill_zero: process(p0_in_data)
    begin
        a1 <= (others => '0');
        a1(p0_in_data.t0'high downto p0_in_data.t0'low) <= p0_in_data.t0;
        a2 <= (others => '0');
        a2(p0_in_data.t1'high downto p0_in_data.t1'low) <= p0_in_data.t1;
    end process;
    
    p1_out_data <= std_logic_vector(unsigned(a1) + unsigned(a2));
    p1_out_last <= p0_in_last;
    p1_out_valid <= p0_in_valid;
    p0_out_ready <= p1_in_ready;
    
end behavioral;
