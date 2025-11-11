library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity zipstream_278 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078TextTypet1OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078_t0;
        p0_in_last: in type_LastVectorTypeArithType2;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType2;
        p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeSignedIntTypeArithType32ArithType1918TextTypet1OrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078;
        p1_out_last: out type_LastVectorTypeArithType2;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType2
    );
end zipstream_278;

architecture behavioral of zipstream_278 is
    
    
    
    
    
    
begin
    
    -- this is just to prevent compiler errors about VHDL types
    p1_out_last <= p0_in_last;
    p1_out_valid <= p0_in_valid;
    p0_out_ready <= p1_in_ready;
    p1_out_data.t0 <= p0_in_data.t0;
    p1_out_data.t1 <= p0_in_data.t1;
end behavioral;
