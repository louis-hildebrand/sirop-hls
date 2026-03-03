library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity vectortotuple_161 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_VectorTypeIntTypeArithType32ArithType9;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32TextTypet2IntTypeArithType32TextTypet3IntTypeArithType32TextTypet4IntTypeArithType32TextTypet5IntTypeArithType32TextTypet6IntTypeArithType32TextTypet7IntTypeArithType32TextTypet8IntTypeArithType32_t0;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end vectortotuple_161;

architecture behavioral of vectortotuple_161 is
    
    
    
    
    
    
begin
    
    p1_out_last <= p0_in_last;
    p1_out_valid <= p0_in_valid;
    p0_out_ready <= p1_in_ready;
    p1_out_data.t0 <= p0_in_data(0);
    p1_out_data.t1 <= p0_in_data(1);
    p1_out_data.t2 <= p0_in_data(2);
    p1_out_data.t3 <= p0_in_data(3);
    p1_out_data.t4 <= p0_in_data(4);
    p1_out_data.t5 <= p0_in_data(5);
    p1_out_data.t6 <= p0_in_data(6);
    p1_out_data.t7 <= p0_in_data(7);
    p1_out_data.t8 <= p0_in_data(8);
end behavioral;
