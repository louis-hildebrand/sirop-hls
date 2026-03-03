library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity vectortotuple_163 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_VectorTypeIntTypeArithType64ArithType2;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType64TextTypet1IntTypeArithType64_t0;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end vectortotuple_163;

architecture behavioral of vectortotuple_163 is
    
    
    
    
    
    
begin
    
    p1_out_last <= p0_in_last;
    p1_out_valid <= p0_in_valid;
    p0_out_ready <= p1_in_ready;
    p1_out_data.t0 <= p0_in_data(0);
    p1_out_data.t1 <= p0_in_data(1);
end behavioral;
