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
        p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType33ArithType1918ArithType1078;
        p1_out_last: out type_LastVectorTypeArithType2;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType2
    );
end top;

architecture behavioral of top is
    
    component mapsimpleorderedstream_15
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
    component vectortoorderedstream_158
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType32ArithType1920ArithType1080;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_OrderedStreamTypeVectorTypeIntTypeArithType32ArithType1920ArithType1080;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component slidevector_4
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType2073600;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType32ArithType1920ArithType1080;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapsimpleorderedstream_155
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
    component mapsimpleorderedstream_9
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeVectorTypeIntTypeArithType32ArithType1920ArithType1080;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1920ArithType1080;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component slideorderedstream_11
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
    component mapsimpleorderedstream_21
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
    component id_3
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType2073600;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeIntTypeArithType32ArithType2073600;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_156
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType2073600;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeIntTypeArithType32ArithType2073600;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component zipstream_310
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078TextTypet1OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078_t0;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType32ArithType1918TextTypet1OrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component tuple_309
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_in_data: in type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078;
            p1_in_last: in type_LastVectorTypeArithType2;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType2;
            p2_out_data: out type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078TextTypet1OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078_t0;
            p2_out_last: out type_LastVectorTypeArithType2;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component mapsimpleorderedstream_162
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeVectorTypeIntTypeArithType32ArithType1920ArithType1080;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1920ArithType1080;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component joinorderedstream_10
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
    component id_328
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType2073600;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeIntTypeArithType32ArithType2073600;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectortoorderedstream_5
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType32ArithType1920ArithType1080;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_OrderedStreamTypeVectorTypeIntTypeArithType32ArithType1920ArithType1080;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component orderedstreamtovector_2
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeIntTypeArithType32ArithType2073600;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_VectorTypeIntTypeArithType32ArithType2073600;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component slidevector_157
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType2073600;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType32ArithType1920ArithType1080;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapsimpleorderedstream_168
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
    component distributor_327
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType2073600;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType32ArithType2073600ArithType2;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType2;
            p3_out_data: out type_VectorTypeLogicTypeArithType2;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType2
        );
    end component;
    component slideorderedstream_164
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
    component mapsimpleorderedstream_308
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
    component mapsimpleorderedstream_326
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType32ArithType1918TextTypet1OrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType33ArithType1918ArithType1078;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component mapsimpleorderedstream_174
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
    component joinorderedstream_163
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
    
    
    
    signal s00_data: type_OrderedStreamTypeIntTypeArithType32ArithType2073600;
    signal s01_last: type_LastVectorTypeArithType1;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType1;
    signal s04_data: type_VectorTypeIntTypeArithType32ArithType2073600;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_data: type_VectorTypeVectorTypeIntTypeArithType32ArithType1920ArithType1080;
    signal s09_last: type_LastVectorTypeArithType0;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_OrderedStreamTypeVectorTypeIntTypeArithType32ArithType1920ArithType1080;
    signal s13_last: type_LastVectorTypeArithType1;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType1;
    signal s16_data: type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1920ArithType1080;
    signal s17_last: type_LastVectorTypeArithType2;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType2;
    signal s20_data: type_OrderedStreamTypeIntTypeArithType32ArithType2073600;
    signal s21_last: type_LastVectorTypeArithType1;
    signal s22_valid: type_LogicType;
    signal s23_ready: type_ReadyVectorTypeArithType1;
    signal s24_data: type_OrderedStreamTypeVectorTypeIntTypeArithType32ArithType5760ArithType1078;
    signal s25_last: type_LastVectorTypeArithType1;
    signal s26_valid: type_LogicType;
    signal s27_ready: type_ReadyVectorTypeArithType1;
    signal s28_data: type_OrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType1920ArithType3ArithType1078;
    signal s29_last: type_LastVectorTypeArithType1;
    signal s30_valid: type_LogicType;
    signal s31_ready: type_ReadyVectorTypeArithType1;
    signal s32_data: type_OrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType3ArithType1920ArithType1078;
    signal s33_last: type_LastVectorTypeArithType1;
    signal s34_valid: type_LogicType;
    signal s35_ready: type_ReadyVectorTypeArithType1;
    signal s36_data: type_VectorTypeIntTypeArithType32ArithType2073600;
    signal s37_last: type_LastVectorTypeArithType0;
    signal s38_valid: type_LogicType;
    signal s39_ready: type_ReadyVectorTypeArithType0;
    signal s40_data: type_VectorTypeVectorTypeIntTypeArithType32ArithType1920ArithType1080;
    signal s41_last: type_LastVectorTypeArithType0;
    signal s42_valid: type_LogicType;
    signal s43_ready: type_ReadyVectorTypeArithType0;
    signal s44_data: type_OrderedStreamTypeVectorTypeIntTypeArithType32ArithType1920ArithType1080;
    signal s45_last: type_LastVectorTypeArithType1;
    signal s46_valid: type_LogicType;
    signal s47_ready: type_ReadyVectorTypeArithType1;
    signal s48_data: type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1920ArithType1080;
    signal s49_last: type_LastVectorTypeArithType2;
    signal s50_valid: type_LogicType;
    signal s51_ready: type_ReadyVectorTypeArithType2;
    signal s52_data: type_OrderedStreamTypeIntTypeArithType32ArithType2073600;
    signal s53_last: type_LastVectorTypeArithType1;
    signal s54_valid: type_LogicType;
    signal s55_ready: type_ReadyVectorTypeArithType1;
    signal s56_data: type_OrderedStreamTypeVectorTypeIntTypeArithType32ArithType5760ArithType1078;
    signal s57_last: type_LastVectorTypeArithType1;
    signal s58_valid: type_LogicType;
    signal s59_ready: type_ReadyVectorTypeArithType1;
    signal s60_data: type_OrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType1920ArithType3ArithType1078;
    signal s61_last: type_LastVectorTypeArithType1;
    signal s62_valid: type_LogicType;
    signal s63_ready: type_ReadyVectorTypeArithType1;
    signal s64_data: type_OrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType3ArithType1920ArithType1078;
    signal s65_last: type_LastVectorTypeArithType1;
    signal s66_valid: type_LogicType;
    signal s67_ready: type_ReadyVectorTypeArithType1;
    signal s68_data: type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078;
    signal s69_last: type_LastVectorTypeArithType2;
    signal s70_valid: type_LogicType;
    signal s71_ready: type_ReadyVectorTypeArithType2;
    signal s72_data: type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078;
    signal s73_last: type_LastVectorTypeArithType2;
    signal s74_valid: type_LogicType;
    signal s75_ready: type_ReadyVectorTypeArithType2;
    signal s76_data: type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078TextTypet1OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078_t0;
    signal s77_last: type_LastVectorTypeArithType2;
    signal s78_valid: type_LogicType;
    signal s79_ready: type_ReadyVectorTypeArithType2;
    signal s80_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType32ArithType1918TextTypet1OrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078;
    signal s81_last: type_LastVectorTypeArithType2;
    signal s82_valid: type_LogicType;
    signal s83_ready: type_ReadyVectorTypeArithType2;
    signal s84_data: type_VectorTypeVectorTypeIntTypeArithType32ArithType2073600ArithType2;
    signal s85_data: type_VectorTypeLastVectorTypeArithType0ArithType2;
    signal s86_data: type_VectorTypeLogicTypeArithType2;
    signal s87_ready: type_ReadyVectorTypeArithType0;
    signal s88_ready: type_ReadyVectorTypeArithType0;
    signal s89_data: type_VectorTypeIntTypeArithType32ArithType2073600;
    signal s90_last: type_LastVectorTypeArithType0;
    signal s91_valid: type_LogicType;
    signal s92_ready: type_ReadyVectorTypeArithType0;
    signal s93_data: type_VectorTypeIntTypeArithType32ArithType2073600;
    signal s94_last: type_LastVectorTypeArithType0;
    signal s95_valid: type_LogicType;
    signal s96_ready: type_ReadyVectorTypeArithType0;
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
    U1_orderedstreamtovector: orderedstreamtovector_2 port map(
        clk => clk,
        p1_out_valid => s95_valid,
        p0_in_valid => s02_valid,
        p1_out_data => s93_data,
        p0_out_ready => s03_ready,
        p1_out_last => s94_last,
        reset => reset,
        p1_in_ready => s96_ready,
        p0_in_data => s00_data,
        p0_in_last => s01_last
    );
    U2_id: id_3 port map(
        clk => clk,
        p1_out_valid => s06_valid,
        p0_in_valid => s86_data(0),
        p1_out_data => s04_data,
        p1_out_last => s05_last,
        p0_out_ready => s87_ready,
        p0_in_last => s85_data(0),
        reset => reset,
        p1_in_ready => s07_ready,
        p0_in_data => s84_data(0)
    );
    U3_slidevector: slidevector_4 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s06_valid,
        p1_out_data => s08_data,
        p1_out_last => s09_last,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => s11_ready,
        p0_in_data => s04_data
    );
    U4_vectortoorderedstream: vectortoorderedstream_5 port map(
        p0_in_data => s08_data,
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => s10_valid,
        p1_out_data => s12_data,
        p0_out_ready => s11_ready,
        p0_in_last => s09_last,
        reset => reset,
        p1_in_ready => s15_ready,
        p1_out_last => s13_last
    );
    U5_mapsimpleorderedstream: mapsimpleorderedstream_9 port map(
        p0_in_data => s12_data,
        clk => clk,
        p1_out_valid => s18_valid,
        p0_in_valid => s14_valid,
        p1_out_data => s16_data,
        p0_out_ready => s15_ready,
        p1_out_last => s17_last,
        reset => reset,
        p1_in_ready => s19_ready,
        p0_in_last => s13_last
    );
    U6_joinorderedstream: joinorderedstream_10 port map(
        p0_in_data => s16_data,
        clk => clk,
        p0_out_ready => s19_ready,
        p1_out_valid => s22_valid,
        p0_in_valid => s18_valid,
        p1_out_data => s20_data,
        p0_in_last => s17_last,
        reset => reset,
        p1_in_ready => s23_ready,
        p1_out_last => s21_last
    );
    U7_slideorderedstream: slideorderedstream_11 port map(
        clk => clk,
        p1_out_valid => s26_valid,
        p0_in_valid => s22_valid,
        p0_out_ready => s23_ready,
        p1_out_data => s24_data,
        reset => reset,
        p1_in_ready => s27_ready,
        p1_out_last => s25_last,
        p0_in_data => s20_data,
        p0_in_last => s21_last
    );
    U8_mapsimpleorderedstream: mapsimpleorderedstream_15 port map(
        p1_out_data => s28_data,
        clk => clk,
        p1_out_valid => s30_valid,
        p0_in_valid => s26_valid,
        p0_out_ready => s27_ready,
        reset => reset,
        p1_in_ready => s31_ready,
        p1_out_last => s29_last,
        p0_in_last => s25_last,
        p0_in_data => s24_data
    );
    U9_mapsimpleorderedstream: mapsimpleorderedstream_21 port map(
        clk => clk,
        p1_out_valid => s34_valid,
        p0_in_valid => s30_valid,
        p1_out_data => s32_data,
        p0_out_ready => s31_ready,
        reset => reset,
        p1_in_ready => s35_ready,
        p1_out_last => s33_last,
        p0_in_last => s29_last,
        p0_in_data => s28_data
    );
    U10_mapsimpleorderedstream: mapsimpleorderedstream_155 port map(
        clk => clk,
        p1_out_valid => s70_valid,
        p0_in_valid => s34_valid,
        p0_out_ready => s35_ready,
        p0_in_data => s32_data,
        p1_out_data => s68_data,
        p1_out_last => s69_last,
        reset => reset,
        p1_in_ready => s71_ready,
        p0_in_last => s33_last
    );
    U11_id: id_156 port map(
        clk => clk,
        p1_out_valid => s38_valid,
        p0_in_valid => s86_data(1),
        p1_out_data => s36_data,
        p1_out_last => s37_last,
        p0_out_ready => s88_ready,
        p0_in_last => s85_data(1),
        reset => reset,
        p1_in_ready => s39_ready,
        p0_in_data => s84_data(1)
    );
    U12_slidevector: slidevector_157 port map(
        clk => clk,
        p1_out_valid => s42_valid,
        p0_in_valid => s38_valid,
        p1_out_data => s40_data,
        p1_out_last => s41_last,
        p0_out_ready => s39_ready,
        p0_in_last => s37_last,
        reset => reset,
        p1_in_ready => s43_ready,
        p0_in_data => s36_data
    );
    U13_vectortoorderedstream: vectortoorderedstream_158 port map(
        p0_in_data => s40_data,
        clk => clk,
        p1_out_valid => s46_valid,
        p0_in_valid => s42_valid,
        p1_out_data => s44_data,
        p0_out_ready => s43_ready,
        p0_in_last => s41_last,
        reset => reset,
        p1_in_ready => s47_ready,
        p1_out_last => s45_last
    );
    U14_mapsimpleorderedstream: mapsimpleorderedstream_162 port map(
        p0_in_data => s44_data,
        clk => clk,
        p1_out_valid => s50_valid,
        p0_in_valid => s46_valid,
        p1_out_data => s48_data,
        p0_out_ready => s47_ready,
        p1_out_last => s49_last,
        reset => reset,
        p1_in_ready => s51_ready,
        p0_in_last => s45_last
    );
    U15_joinorderedstream: joinorderedstream_163 port map(
        p0_in_data => s48_data,
        clk => clk,
        p0_out_ready => s51_ready,
        p1_out_valid => s54_valid,
        p0_in_valid => s50_valid,
        p1_out_data => s52_data,
        p0_in_last => s49_last,
        reset => reset,
        p1_in_ready => s55_ready,
        p1_out_last => s53_last
    );
    U16_slideorderedstream: slideorderedstream_164 port map(
        clk => clk,
        p1_out_valid => s58_valid,
        p0_in_valid => s54_valid,
        p0_out_ready => s55_ready,
        p1_out_data => s56_data,
        reset => reset,
        p1_in_ready => s59_ready,
        p1_out_last => s57_last,
        p0_in_data => s52_data,
        p0_in_last => s53_last
    );
    U17_mapsimpleorderedstream: mapsimpleorderedstream_168 port map(
        p1_out_data => s60_data,
        clk => clk,
        p1_out_valid => s62_valid,
        p0_in_valid => s58_valid,
        p0_out_ready => s59_ready,
        reset => reset,
        p1_in_ready => s63_ready,
        p1_out_last => s61_last,
        p0_in_last => s57_last,
        p0_in_data => s56_data
    );
    U18_mapsimpleorderedstream: mapsimpleorderedstream_174 port map(
        clk => clk,
        p1_out_valid => s66_valid,
        p0_in_valid => s62_valid,
        p1_out_data => s64_data,
        p0_out_ready => s63_ready,
        reset => reset,
        p1_in_ready => s67_ready,
        p1_out_last => s65_last,
        p0_in_last => s61_last,
        p0_in_data => s60_data
    );
    U19_mapsimpleorderedstream: mapsimpleorderedstream_308 port map(
        clk => clk,
        p1_out_valid => s74_valid,
        p0_in_valid => s66_valid,
        p0_out_ready => s67_ready,
        p0_in_data => s64_data,
        p1_out_data => s72_data,
        p1_out_last => s73_last,
        reset => reset,
        p1_in_ready => s75_ready,
        p0_in_last => s65_last
    );
    U20_tuple: tuple_309 port map(
        clk => clk,
        p0_out_ready => s71_ready,
        p2_out_valid => s78_valid,
        p1_out_ready => s75_ready,
        p0_in_valid => s70_valid,
        p1_in_last => s73_last,
        p2_in_ready => s79_ready,
        p2_out_data => s76_data,
        p0_in_data => s68_data,
        p1_in_valid => s74_valid,
        p2_out_last => s77_last,
        p1_in_data => s72_data,
        p0_in_last => s69_last,
        reset => reset
    );
    U21_zipstream: zipstream_310 port map(
        clk => clk,
        p0_out_ready => s79_ready,
        p1_out_valid => s82_valid,
        p0_in_valid => s78_valid,
        p1_out_data => s80_data,
        p0_in_data => s76_data,
        p0_in_last => s77_last,
        p1_out_last => s81_last,
        reset => reset,
        p1_in_ready => s83_ready
    );
    U22_mapsimpleorderedstream: mapsimpleorderedstream_326 port map(
        clk => clk,
        p0_out_ready => s83_ready,
        p1_out_data => p1_out_data,
        p1_out_valid => p1_out_valid,
        p0_in_valid => s82_valid,
        p0_in_last => s81_last,
        p1_out_last => p1_out_last,
        reset => reset,
        p0_in_data => s80_data,
        p1_in_ready => p1_in_ready
    );
    U23_distributor: distributor_327 port map(
        clk => clk,
        p0_in_valid => s91_valid,
        p4_in_data(0) => s87_ready,
        p4_in_data(1) => s88_ready,
        p0_out_ready => s92_ready,
        p1_out_data => s84_data,
        p2_out_data => s85_data,
        p0_in_last => s90_last,
        reset => reset,
        p3_out_data => s86_data,
        p0_in_data => s89_data
    );
    U24_id: id_328 port map(
        clk => clk,
        p1_out_valid => s91_valid,
        p0_in_valid => s95_valid,
        p1_out_data => s89_data,
        p1_out_last => s90_last,
        p0_out_ready => s96_ready,
        p0_in_last => s94_last,
        reset => reset,
        p1_in_ready => s92_ready,
        p0_in_data => s93_data
    );
    
    
end behavioral;
