library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity top is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeIntTypeArithType16ArithType840;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_in_data: in type_OrderedStreamTypeIntTypeArithType16ArithType840;
        p1_in_last: in type_LastVectorTypeArithType1;
        p1_in_valid: in type_LogicType;
        p1_out_ready: out type_ReadyVectorTypeArithType1;
        p2_out_data: out type_IntTypeArithType26;
        p2_out_last: out type_LastVectorTypeArithType0;
        p2_out_valid: out type_LogicType;
        p2_in_ready: in type_ReadyVectorTypeArithType0
    );
end top;

architecture behavioral of top is
    
    component tuple_2
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeIntTypeArithType16ArithType840;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_in_data: in type_OrderedStreamTypeIntTypeArithType16ArithType840;
            p1_in_last: in type_LastVectorTypeArithType1;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType1;
            p2_out_data: out type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType16ArithType840TextTypet1OrderedStreamTypeIntTypeArithType16ArithType840_t0;
            p2_out_last: out type_LastVectorTypeArithType1;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component zipstream_3
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType16ArithType840TextTypet1OrderedStreamTypeIntTypeArithType16ArithType840_t0;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType840;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_10
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType840;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeIntTypeArithType16ArithType840;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component foldorderedstream_17
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeIntTypeArithType16ArithType840;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_IntTypeArithType26;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    
    
    signal s00_data: type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType16ArithType840TextTypet1OrderedStreamTypeIntTypeArithType16ArithType840_t0;
    signal s01_last: type_LastVectorTypeArithType1;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType1;
    signal s04_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType840;
    signal s05_last: type_LastVectorTypeArithType1;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType1;
    signal s08_data: type_OrderedStreamTypeIntTypeArithType16ArithType840;
    signal s09_last: type_LastVectorTypeArithType1;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType1;
begin
    
    U0_tuple: tuple_2 port map(
        clk => clk,
        p2_out_valid => s02_valid,
        p2_out_data => s00_data,
        p1_in_last => p1_in_last,
        p0_in_valid => p0_in_valid,
        p1_out_ready => p1_out_ready,
        p0_in_data => p0_in_data,
        p0_out_ready => p0_out_ready,
        p2_out_last => s01_last,
        p1_in_valid => p1_in_valid,
        reset => reset,
        p2_in_ready => s03_ready,
        p1_in_data => p1_in_data,
        p0_in_last => p0_in_last
    );
    U1_zipstream: zipstream_3 port map(
        clk => clk,
        p1_out_data => s04_data,
        p1_out_valid => s06_valid,
        p0_in_valid => s02_valid,
        p0_out_ready => s03_ready,
        p0_in_data => s00_data,
        reset => reset,
        p1_in_ready => s07_ready,
        p1_out_last => s05_last,
        p0_in_last => s01_last
    );
    U2_mapsimpleorderedstream: mapsimpleorderedstream_10 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s06_valid,
        p1_out_data => s08_data,
        p0_out_ready => s07_ready,
        p0_in_data => s04_data,
        reset => reset,
        p1_in_ready => s11_ready,
        p1_out_last => s09_last,
        p0_in_last => s05_last
    );
    U3_foldorderedstream: foldorderedstream_17 port map(
        clk => clk,
        p1_out_valid => p2_out_valid,
        p0_in_valid => s10_valid,
        p0_in_data => s08_data,
        p0_out_ready => s11_ready,
        p1_out_last => p2_out_last,
        reset => reset,
        p1_in_ready => p2_in_ready,
        p1_out_data => p2_out_data,
        p0_in_last => s09_last
    );
    
    
end behavioral;
