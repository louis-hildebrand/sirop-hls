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
    
    component mapsimpleorderedstream_6
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeVectorTypeIntTypeArithType32ArithType5760ArithType1078;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType1920ArithType3ArithType1078;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_12
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType1920ArithType3ArithType1078;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType3ArithType1920ArithType1078;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component joinorderedstream_1
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1920ArithType1080;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_out_data: out type_OrderedStreamTypeIntTypeArithType32ArithType2073600;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_146
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType3ArithType1920ArithType1078;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component slideorderedstream_2
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeIntTypeArithType32ArithType2073600;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeVectorTypeIntTypeArithType32ArithType5760ArithType1078;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    
    
    
    signal s00_data: type_OrderedStreamTypeIntTypeArithType32ArithType2073600;
    signal s01_last: type_LastVectorTypeArithType1;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType1;
    signal s04_data: type_OrderedStreamTypeVectorTypeIntTypeArithType32ArithType5760ArithType1078;
    signal s05_last: type_LastVectorTypeArithType1;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType1;
    signal s08_data: type_OrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType1920ArithType3ArithType1078;
    signal s09_last: type_LastVectorTypeArithType1;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType1;
    signal s12_data: type_OrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType3ArithType1920ArithType1078;
    signal s13_last: type_LastVectorTypeArithType1;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType1;
begin
    
    U0_joinorderedstream: joinorderedstream_1 port map(
        p0_in_data => p0_in_data,
        clk => clk,
        p0_out_ready => p0_out_ready,
        p1_out_valid => s02_valid,
        p0_in_valid => p0_in_valid,
        p1_out_data => s00_data,
        p0_in_last => p0_in_last,
        reset => reset,
        p1_in_ready => s03_ready,
        p1_out_last => s01_last
    );
    U1_slideorderedstream: slideorderedstream_2 port map(
        clk => clk,
        p1_out_valid => s06_valid,
        p0_in_valid => s02_valid,
        p0_out_ready => s03_ready,
        p1_out_data => s04_data,
        reset => reset,
        p1_in_ready => s07_ready,
        p1_out_last => s05_last,
        p0_in_data => s00_data,
        p0_in_last => s01_last
    );
    U2_mapsimpleorderedstream: mapsimpleorderedstream_6 port map(
        p1_out_data => s08_data,
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s06_valid,
        p0_out_ready => s07_ready,
        reset => reset,
        p1_in_ready => s11_ready,
        p1_out_last => s09_last,
        p0_in_last => s05_last,
        p0_in_data => s04_data
    );
    U3_mapsimpleorderedstream: mapsimpleorderedstream_12 port map(
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => s10_valid,
        p1_out_data => s12_data,
        p0_out_ready => s11_ready,
        reset => reset,
        p1_in_ready => s15_ready,
        p1_out_last => s13_last,
        p0_in_last => s09_last,
        p0_in_data => s08_data
    );
    U4_mapsimpleorderedstream: mapsimpleorderedstream_146 port map(
        clk => clk,
        p1_out_valid => p1_out_valid,
        p0_in_valid => s14_valid,
        p0_out_ready => s15_ready,
        p0_in_data => s12_data,
        p1_out_data => p1_out_data,
        p1_out_last => p1_out_last,
        reset => reset,
        p1_in_ready => p1_in_ready,
        p0_in_last => s13_last
    );
    
    
end behavioral;
