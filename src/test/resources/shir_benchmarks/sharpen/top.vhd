library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity top is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1920ArithType1080;
        p0_in_last: in type_LastVectorTypeArithType2;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType2;
        p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078;
        p1_out_last: out type_LastVectorTypeArithType2;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType2
    );
end top;

architecture behavioral of top is
    
    component slide2dorderedstream_1
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1920ArithType1080;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3ArithType1918ArithType1078;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component mapsimpleorderedstream_186
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3ArithType1918ArithType1078;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    
    
    
    signal s00_data: type_OrderedStreamTypeOrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3ArithType1918ArithType1078;
    signal s01_last: type_LastVectorTypeArithType2;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType2;
begin
    
    U0_slide2dorderedstream: slide2dorderedstream_1 port map(
        p0_in_data => p0_in_data,
        clk => clk,
        p0_out_ready => p0_out_ready,
        p1_out_valid => s02_valid,
        p0_in_valid => p0_in_valid,
        p1_out_data => s00_data,
        p0_in_last => p0_in_last,
        p1_out_last => s01_last,
        reset => reset,
        p1_in_ready => s03_ready
    );
    U1_mapsimpleorderedstream: mapsimpleorderedstream_186 port map(
        clk => clk,
        p0_out_ready => s03_ready,
        p1_out_valid => p1_out_valid,
        p0_in_valid => s02_valid,
        p0_in_data => s00_data,
        p1_out_data => p1_out_data,
        p0_in_last => s01_last,
        p1_out_last => p1_out_last,
        reset => reset,
        p1_in_ready => p1_in_ready
    );
    
    
end behavioral;
