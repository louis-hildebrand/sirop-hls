library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity top is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeSignedIntTypeArithType8ArithType16;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeIntTypeArithType8ArithType14;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end top;

architecture behavioral of top is
    
    component slideorderedstream_1
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeSignedIntTypeArithType8ArithType16;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeVectorTypeSignedIntTypeArithType8ArithType3ArithType14;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_56
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeVectorTypeSignedIntTypeArithType8ArithType3ArithType14;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeIntTypeArithType8ArithType14;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    
    
    
    signal s00_data: type_OrderedStreamTypeVectorTypeSignedIntTypeArithType8ArithType3ArithType14;
    signal s01_last: type_LastVectorTypeArithType1;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType1;
begin
    
    U0_slideorderedstream: slideorderedstream_1 port map(
        clk => clk,
        p1_out_valid => s02_valid,
        p0_in_valid => p0_in_valid,
        p0_out_ready => p0_out_ready,
        p0_in_data => p0_in_data,
        reset => reset,
        p1_in_ready => s03_ready,
        p1_out_last => s01_last,
        p1_out_data => s00_data,
        p0_in_last => p0_in_last
    );
    U1_mapsimpleorderedstream: mapsimpleorderedstream_56 port map(
        clk => clk,
        p1_out_valid => p1_out_valid,
        p0_in_valid => s02_valid,
        p0_out_ready => s03_ready,
        p1_out_data => p1_out_data,
        reset => reset,
        p1_in_ready => p1_in_ready,
        p1_out_last => p1_out_last,
        p0_in_last => s01_last,
        p0_in_data => s00_data
    );
    
    
end behavioral;
