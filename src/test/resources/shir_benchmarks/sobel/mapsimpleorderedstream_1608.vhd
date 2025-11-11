library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_1608 is
    generic(
        stream_length: type_NaturalNumberType := 1078
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeSignedIntTypeArithType32ArithType1918TextTypet1OrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078;
        p0_in_last: in type_LastVectorTypeArithType2;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType2;
        p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078;
        p1_out_last: out type_LastVectorTypeArithType2;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType2
    );
end mapsimpleorderedstream_1608;

architecture behavioral of mapsimpleorderedstream_1608 is
    
    component mapsimpleorderedstream_387
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_306
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeSignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_1602
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_630
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_792
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_468
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_954
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_1359
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_1521
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_1116
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_1606
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeSignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_1278
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_711
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_873
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_1035
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_1440
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component zipstream_280
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeSignedIntTypeArithType32ArithType1918TextTypet1OrderedStreamTypeSignedIntTypeArithType32ArithType1918_t0;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_549
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_1197
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_300
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType1918;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeSignedIntTypeArithType32ArithType1918;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType1918;
    signal s01_last: type_LastVectorTypeArithType1;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType1;
    signal s04_data: type_OrderedStreamTypeSignedIntTypeArithType32ArithType1918;
    signal s05_last: type_LastVectorTypeArithType1;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType1;
    signal s08_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s09_last: type_LastVectorTypeArithType1;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType1;
    signal s12_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s13_last: type_LastVectorTypeArithType1;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType1;
    signal s16_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s17_last: type_LastVectorTypeArithType1;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType1;
    signal s20_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s21_last: type_LastVectorTypeArithType1;
    signal s22_valid: type_LogicType;
    signal s23_ready: type_ReadyVectorTypeArithType1;
    signal s24_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s25_last: type_LastVectorTypeArithType1;
    signal s26_valid: type_LogicType;
    signal s27_ready: type_ReadyVectorTypeArithType1;
    signal s28_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s29_last: type_LastVectorTypeArithType1;
    signal s30_valid: type_LogicType;
    signal s31_ready: type_ReadyVectorTypeArithType1;
    signal s32_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s33_last: type_LastVectorTypeArithType1;
    signal s34_valid: type_LogicType;
    signal s35_ready: type_ReadyVectorTypeArithType1;
    signal s36_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s37_last: type_LastVectorTypeArithType1;
    signal s38_valid: type_LogicType;
    signal s39_ready: type_ReadyVectorTypeArithType1;
    signal s40_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s41_last: type_LastVectorTypeArithType1;
    signal s42_valid: type_LogicType;
    signal s43_ready: type_ReadyVectorTypeArithType1;
    signal s44_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s45_last: type_LastVectorTypeArithType1;
    signal s46_valid: type_LogicType;
    signal s47_ready: type_ReadyVectorTypeArithType1;
    signal s48_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s49_last: type_LastVectorTypeArithType1;
    signal s50_valid: type_LogicType;
    signal s51_ready: type_ReadyVectorTypeArithType1;
    signal s52_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s53_last: type_LastVectorTypeArithType1;
    signal s54_valid: type_LogicType;
    signal s55_ready: type_ReadyVectorTypeArithType1;
    signal s56_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s57_last: type_LastVectorTypeArithType1;
    signal s58_valid: type_LogicType;
    signal s59_ready: type_ReadyVectorTypeArithType1;
    signal s60_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s61_last: type_LastVectorTypeArithType1;
    signal s62_valid: type_LogicType;
    signal s63_ready: type_ReadyVectorTypeArithType1;
    signal s64_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s65_last: type_LastVectorTypeArithType1;
    signal s66_valid: type_LogicType;
    signal s67_ready: type_ReadyVectorTypeArithType1;
    signal s68_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s69_last: type_LastVectorTypeArithType1;
    signal s70_valid: type_LogicType;
    signal s71_ready: type_ReadyVectorTypeArithType1;
    signal s72_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType1918;
    signal s73_last: type_LastVectorTypeArithType1;
    signal s74_valid: type_LogicType;
    signal s75_ready: type_ReadyVectorTypeArithType1;
    signal s76_data: type_OrderedStreamTypeSignedIntTypeArithType32ArithType1918;
    signal s77_last: type_LastVectorTypeArithType1;
    signal s78_valid: type_LogicType;
    signal s79_ready: type_ReadyVectorTypeArithType1;
    signal s80_data: type_NamedTupleTypeTextTypet0OrderedStreamTypeSignedIntTypeArithType32ArithType1918TextTypet1OrderedStreamTypeSignedIntTypeArithType32ArithType1918_t0;
    signal s81_last: type_LastVectorTypeArithType1;
    signal s82_valid: type_LogicType;
    signal s83_ready: type_ReadyVectorTypeArithType1;
    signal s84_data: type_LastVectorTypeArithType2;
    signal s85_data: type_ReadyVectorTypeArithType2;
begin
    
    U0_zipstream: zipstream_280 port map(
        clk => clk,
        p1_out_valid => s02_valid,
        p0_in_valid => s82_valid,
        p0_out_ready => s83_ready,
        p0_in_data => s80_data,
        p1_out_data => s00_data,
        reset => reset,
        p1_in_ready => s03_ready,
        p1_out_last => s01_last,
        p0_in_last => s81_last
    );
    U1_mapsimpleorderedstream: mapsimpleorderedstream_300 port map(
        clk => clk,
        p1_out_data => s04_data,
        p0_in_data => s00_data,
        p1_out_valid => s06_valid,
        p0_in_valid => s02_valid,
        p0_out_ready => s03_ready,
        reset => reset,
        p1_in_ready => s07_ready,
        p1_out_last => s05_last,
        p0_in_last => s01_last
    );
    U2_mapsimpleorderedstream: mapsimpleorderedstream_306 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s06_valid,
        p0_out_ready => s07_ready,
        p0_in_data => s04_data,
        reset => reset,
        p1_in_ready => s11_ready,
        p1_out_last => s09_last,
        p0_in_last => s05_last,
        p1_out_data => s08_data
    );
    U3_mapsimpleorderedstream: mapsimpleorderedstream_387 port map(
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => s10_valid,
        p0_in_data => s08_data,
        p0_out_ready => s11_ready,
        reset => reset,
        p1_in_ready => s15_ready,
        p1_out_last => s13_last,
        p0_in_last => s09_last,
        p1_out_data => s12_data
    );
    U4_mapsimpleorderedstream: mapsimpleorderedstream_468 port map(
        clk => clk,
        p1_out_valid => s18_valid,
        p0_in_valid => s14_valid,
        p0_in_data => s12_data,
        p0_out_ready => s15_ready,
        reset => reset,
        p1_in_ready => s19_ready,
        p1_out_last => s17_last,
        p0_in_last => s13_last,
        p1_out_data => s16_data
    );
    U5_mapsimpleorderedstream: mapsimpleorderedstream_549 port map(
        clk => clk,
        p1_out_valid => s22_valid,
        p0_in_valid => s18_valid,
        p0_in_data => s16_data,
        p0_out_ready => s19_ready,
        reset => reset,
        p1_in_ready => s23_ready,
        p1_out_last => s21_last,
        p0_in_last => s17_last,
        p1_out_data => s20_data
    );
    U6_mapsimpleorderedstream: mapsimpleorderedstream_630 port map(
        clk => clk,
        p1_out_valid => s26_valid,
        p0_in_valid => s22_valid,
        p0_in_data => s20_data,
        p0_out_ready => s23_ready,
        reset => reset,
        p1_in_ready => s27_ready,
        p1_out_last => s25_last,
        p0_in_last => s21_last,
        p1_out_data => s24_data
    );
    U7_mapsimpleorderedstream: mapsimpleorderedstream_711 port map(
        clk => clk,
        p1_out_valid => s30_valid,
        p0_in_valid => s26_valid,
        p0_in_data => s24_data,
        p0_out_ready => s27_ready,
        reset => reset,
        p1_in_ready => s31_ready,
        p1_out_last => s29_last,
        p0_in_last => s25_last,
        p1_out_data => s28_data
    );
    U8_mapsimpleorderedstream: mapsimpleorderedstream_792 port map(
        clk => clk,
        p1_out_valid => s34_valid,
        p0_in_valid => s30_valid,
        p0_in_data => s28_data,
        p0_out_ready => s31_ready,
        reset => reset,
        p1_in_ready => s35_ready,
        p1_out_last => s33_last,
        p0_in_last => s29_last,
        p1_out_data => s32_data
    );
    U9_mapsimpleorderedstream: mapsimpleorderedstream_873 port map(
        clk => clk,
        p1_out_valid => s38_valid,
        p0_in_valid => s34_valid,
        p0_in_data => s32_data,
        p0_out_ready => s35_ready,
        reset => reset,
        p1_in_ready => s39_ready,
        p1_out_last => s37_last,
        p0_in_last => s33_last,
        p1_out_data => s36_data
    );
    U10_mapsimpleorderedstream: mapsimpleorderedstream_954 port map(
        clk => clk,
        p1_out_valid => s42_valid,
        p0_in_valid => s38_valid,
        p0_in_data => s36_data,
        p0_out_ready => s39_ready,
        reset => reset,
        p1_in_ready => s43_ready,
        p1_out_last => s41_last,
        p0_in_last => s37_last,
        p1_out_data => s40_data
    );
    U11_mapsimpleorderedstream: mapsimpleorderedstream_1035 port map(
        clk => clk,
        p1_out_valid => s46_valid,
        p0_in_valid => s42_valid,
        p0_in_data => s40_data,
        p0_out_ready => s43_ready,
        reset => reset,
        p1_in_ready => s47_ready,
        p1_out_last => s45_last,
        p0_in_last => s41_last,
        p1_out_data => s44_data
    );
    U12_mapsimpleorderedstream: mapsimpleorderedstream_1116 port map(
        clk => clk,
        p1_out_valid => s50_valid,
        p0_in_valid => s46_valid,
        p0_in_data => s44_data,
        p0_out_ready => s47_ready,
        reset => reset,
        p1_in_ready => s51_ready,
        p1_out_last => s49_last,
        p0_in_last => s45_last,
        p1_out_data => s48_data
    );
    U13_mapsimpleorderedstream: mapsimpleorderedstream_1197 port map(
        clk => clk,
        p1_out_valid => s54_valid,
        p0_in_valid => s50_valid,
        p0_in_data => s48_data,
        p0_out_ready => s51_ready,
        reset => reset,
        p1_in_ready => s55_ready,
        p1_out_last => s53_last,
        p0_in_last => s49_last,
        p1_out_data => s52_data
    );
    U14_mapsimpleorderedstream: mapsimpleorderedstream_1278 port map(
        clk => clk,
        p1_out_valid => s58_valid,
        p0_in_valid => s54_valid,
        p0_in_data => s52_data,
        p0_out_ready => s55_ready,
        reset => reset,
        p1_in_ready => s59_ready,
        p1_out_last => s57_last,
        p0_in_last => s53_last,
        p1_out_data => s56_data
    );
    U15_mapsimpleorderedstream: mapsimpleorderedstream_1359 port map(
        clk => clk,
        p1_out_valid => s62_valid,
        p0_in_valid => s58_valid,
        p0_in_data => s56_data,
        p0_out_ready => s59_ready,
        reset => reset,
        p1_in_ready => s63_ready,
        p1_out_last => s61_last,
        p0_in_last => s57_last,
        p1_out_data => s60_data
    );
    U16_mapsimpleorderedstream: mapsimpleorderedstream_1440 port map(
        clk => clk,
        p1_out_valid => s66_valid,
        p0_in_valid => s62_valid,
        p0_in_data => s60_data,
        p0_out_ready => s63_ready,
        reset => reset,
        p1_in_ready => s67_ready,
        p1_out_last => s65_last,
        p0_in_last => s61_last,
        p1_out_data => s64_data
    );
    U17_mapsimpleorderedstream: mapsimpleorderedstream_1521 port map(
        clk => clk,
        p1_out_valid => s70_valid,
        p0_in_valid => s66_valid,
        p0_in_data => s64_data,
        p0_out_ready => s67_ready,
        reset => reset,
        p1_in_ready => s71_ready,
        p1_out_last => s69_last,
        p0_in_last => s65_last,
        p1_out_data => s68_data
    );
    U18_mapsimpleorderedstream: mapsimpleorderedstream_1602 port map(
        clk => clk,
        p1_out_valid => s74_valid,
        p0_in_valid => s70_valid,
        p0_in_data => s68_data,
        p0_out_ready => s71_ready,
        reset => reset,
        p1_in_ready => s75_ready,
        p1_out_last => s73_last,
        p0_in_last => s69_last,
        p1_out_data => s72_data
    );
    U19_mapsimpleorderedstream: mapsimpleorderedstream_1606 port map(
        clk => clk,
        p1_out_data => s76_data,
        p1_out_valid => s78_valid,
        p0_in_valid => s74_valid,
        p0_in_data => s72_data,
        p0_out_ready => s75_ready,
        reset => reset,
        p1_in_ready => s79_ready,
        p1_out_last => s77_last,
        p0_in_last => s73_last
    );
    p1_out_data <= s76_data;
    p1_out_last <= "1" & s77_last when out_counter = stream_length - 1 else "0" & s77_last;
    p1_out_valid <= s78_valid;
    s79_ready <= p1_in_ready(s79_ready'high downto s79_ready'low);
    
    s80_data <= p0_in_data;
    s81_last <= p0_in_last(s81_last'high downto s81_last'low);
    s82_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s83_ready when in_counter = stream_length - 1 and s83_ready(s83_ready'high) = '1' else "0" & s83_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s82_valid = '1' and s83_ready(s83_ready'high) = '1' then
                    if in_counter < stream_length - 1 then
                        in_counter <= in_counter + 1;
                    else
                        in_counter <= 0;
                    end if;
                end if;
            end if;
        end if;
    end process;
    
    outgoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                out_counter <= 0;
            else
                if s78_valid = '1' and s79_ready(s79_ready'high) = '1' then
                    if out_counter < stream_length - 1 then
                        out_counter <= out_counter + 1;
                    else
                        out_counter <= 0;
                    end if;
                end if;
            end if;
        end if;
    end process;
    
end behavioral;
