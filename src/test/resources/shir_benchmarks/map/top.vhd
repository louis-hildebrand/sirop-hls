library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity top is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeIntTypeArithType8ArithType200;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeIntTypeArithType8ArithType200;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end top;

architecture behavioral of top is
    
    component mapsimpleorderedstream_6
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeIntTypeArithType8ArithType200;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeIntTypeArithType8ArithType200;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    
    
    
    
begin
    
    U0_mapsimpleorderedstream: mapsimpleorderedstream_6 port map(
        clk => clk,
        p1_out_valid => p1_out_valid,
        p0_in_valid => p0_in_valid,
        p0_out_ready => p0_out_ready,
        p1_out_data => p1_out_data,
        reset => reset,
        p0_in_data => p0_in_data,
        p1_in_ready => p1_in_ready,
        p1_out_last => p1_out_last,
        p0_in_last => p0_in_last
    );
    
    
end behavioral;
