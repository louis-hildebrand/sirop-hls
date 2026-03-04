library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapvector_function_44 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_VectorTypeSignedIntTypeArithType64ArithType3;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_VectorTypeSignedIntTypeArithType64ArithType2;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end mapvector_function_44;

architecture behavioral of mapvector_function_44 is
    
    component dropvector_46
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType64ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeSignedIntTypeArithType64ArithType2;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    
    
    
begin
    
    U0_dropvector: dropvector_46 port map(
        clk => clk,
        p0_in_data => p0_in_data,
        p1_out_valid => p1_out_valid,
        p0_in_valid => p0_in_valid,
        p1_out_last => p1_out_last,
        p0_out_ready => p0_out_ready,
        p1_out_data => p1_out_data,
        p0_in_last => p0_in_last,
        reset => reset,
        p1_in_ready => p1_in_ready
    );
    
    
end behavioral;
