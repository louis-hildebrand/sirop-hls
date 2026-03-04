library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapvector_function_59 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_VectorTypeSignedIntTypeArithType64ArithType2;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_SignedIntTypeArithType32;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end mapvector_function_59;

architecture behavioral of mapvector_function_59 is
    
    component vectortotuple_61
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType64ArithType2;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType64TextTypet1SignedIntTypeArithType64_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component addint_62
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType64TextTypet1SignedIntTypeArithType64_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType65;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component resizeinteger_63
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType65;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    
    
    signal s00_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType64TextTypet1SignedIntTypeArithType64_t0;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_SignedIntTypeArithType65;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
begin
    
    U0_vectortotuple: vectortotuple_61 port map(
        clk => clk,
        p1_out_data => s00_data,
        p0_in_data => p0_in_data,
        p1_out_valid => s02_valid,
        p0_in_valid => p0_in_valid,
        p1_out_last => s01_last,
        p0_out_ready => p0_out_ready,
        p0_in_last => p0_in_last,
        reset => reset,
        p1_in_ready => s03_ready
    );
    U1_addint: addint_62 port map(
        clk => clk,
        p1_out_valid => s06_valid,
        p0_in_valid => s02_valid,
        p0_in_data => s00_data,
        p1_out_last => s05_last,
        p0_out_ready => s03_ready,
        p0_in_last => s01_last,
        reset => reset,
        p1_in_ready => s07_ready,
        p1_out_data => s04_data
    );
    U2_resizeinteger: resizeinteger_63 port map(
        clk => clk,
        p1_out_valid => p1_out_valid,
        p0_in_valid => s06_valid,
        p1_out_last => p1_out_last,
        p0_in_data => s04_data,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => p1_in_ready,
        p1_out_data => p1_out_data
    );
    
    
end behavioral;
