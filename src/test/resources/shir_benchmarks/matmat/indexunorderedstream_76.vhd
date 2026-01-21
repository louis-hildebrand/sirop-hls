library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity indexunorderedstream_76 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_UnorderedStreamTypeIntTypeArithType256ArithType4096;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType256TextTypet1IntTypeArithType12ArithType4096;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end indexunorderedstream_76;

architecture behavioral of indexunorderedstream_76 is
    
    
    
    
    
    
begin
    
    p1_out_data.t0 <= p0_in_data.data;
    p1_out_data.t1 <= p0_in_data.idx;
    p1_out_last <= p0_in_last;
    p1_out_valid <= p0_in_valid;
    p0_out_ready <= p1_in_ready;
    
end behavioral;
