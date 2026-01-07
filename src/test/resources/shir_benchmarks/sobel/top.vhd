library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity top is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1920ArithType1080;
        p0_in_last: in type_LastVectorTypeArithType2;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType2;
        p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078;
        p1_out_last: out type_LastVectorTypeArithType2;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType2
    );
end top;

architecture behavioral of top is
    
    component slidevector_4
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType2073600;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType1920ArithType1080;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapsimpleorderedstream_250
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeVectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3ArithType1918ArithType1078;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component mapsimpleorderedstream_9
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeVectorTypeSignedIntTypeArithType32ArithType1920ArithType1080;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1920ArithType1080;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component zipstream_252
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
    end component;
    component mapsimpleorderedstream_126
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeVectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3ArithType1918ArithType1078;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component vectortoorderedstream_129
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType1920ArithType1080;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_OrderedStreamTypeVectorTypeSignedIntTypeArithType32ArithType1920ArithType1080;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component distributor_1583
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType2073600;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType2073600ArithType2;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType2;
            p3_out_data: out type_VectorTypeLogicTypeArithType2;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType2
        );
    end component;
    component id_3
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType2073600;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeSignedIntTypeArithType32ArithType2073600;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component slide2dorderedstream_134
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1920ArithType1080;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeVectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3ArithType1918ArithType1078;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component mapsimpleorderedstream_133
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeVectorTypeSignedIntTypeArithType32ArithType1920ArithType1080;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1920ArithType1080;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component joinorderedstream_1
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1920ArithType1080;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_out_data: out type_OrderedStreamTypeSignedIntTypeArithType32ArithType2073600;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component vectortoorderedstream_5
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType1920ArithType1080;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_OrderedStreamTypeVectorTypeSignedIntTypeArithType32ArithType1920ArithType1080;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_1582
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
    end component;
    component orderedstreamtovector_2
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeSignedIntTypeArithType32ArithType2073600;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_VectorTypeSignedIntTypeArithType32ArithType2073600;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_127
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType2073600;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeSignedIntTypeArithType32ArithType2073600;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component slide2dorderedstream_10
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1920ArithType1080;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeVectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3ArithType1918ArithType1078;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component slidevector_128
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType2073600;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType1920ArithType1080;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_251
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_in_data: in type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078;
            p1_in_last: in type_LastVectorTypeArithType2;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType2;
            p2_out_data: out type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078TextTypet1OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078_t0;
            p2_out_last: out type_LastVectorTypeArithType2;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component id_1584
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType2073600;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeSignedIntTypeArithType32ArithType2073600;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    
    
    signal s00_data: type_OrderedStreamTypeSignedIntTypeArithType32ArithType2073600;
    signal s01_last: type_LastVectorTypeArithType1;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType1;
    signal s04_data: type_VectorTypeSignedIntTypeArithType32ArithType2073600;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_data: type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType1920ArithType1080;
    signal s09_last: type_LastVectorTypeArithType0;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_OrderedStreamTypeVectorTypeSignedIntTypeArithType32ArithType1920ArithType1080;
    signal s13_last: type_LastVectorTypeArithType1;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType1;
    signal s16_data: type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1920ArithType1080;
    signal s17_last: type_LastVectorTypeArithType2;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType2;
    signal s20_data: type_OrderedStreamTypeOrderedStreamTypeVectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3ArithType1918ArithType1078;
    signal s21_last: type_LastVectorTypeArithType2;
    signal s22_valid: type_LogicType;
    signal s23_ready: type_ReadyVectorTypeArithType2;
    signal s24_data: type_VectorTypeSignedIntTypeArithType32ArithType2073600;
    signal s25_last: type_LastVectorTypeArithType0;
    signal s26_valid: type_LogicType;
    signal s27_ready: type_ReadyVectorTypeArithType0;
    signal s28_data: type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType1920ArithType1080;
    signal s29_last: type_LastVectorTypeArithType0;
    signal s30_valid: type_LogicType;
    signal s31_ready: type_ReadyVectorTypeArithType0;
    signal s32_data: type_OrderedStreamTypeVectorTypeSignedIntTypeArithType32ArithType1920ArithType1080;
    signal s33_last: type_LastVectorTypeArithType1;
    signal s34_valid: type_LogicType;
    signal s35_ready: type_ReadyVectorTypeArithType1;
    signal s36_data: type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1920ArithType1080;
    signal s37_last: type_LastVectorTypeArithType2;
    signal s38_valid: type_LogicType;
    signal s39_ready: type_ReadyVectorTypeArithType2;
    signal s40_data: type_OrderedStreamTypeOrderedStreamTypeVectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3ArithType1918ArithType1078;
    signal s41_last: type_LastVectorTypeArithType2;
    signal s42_valid: type_LogicType;
    signal s43_ready: type_ReadyVectorTypeArithType2;
    signal s44_data: type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078;
    signal s45_last: type_LastVectorTypeArithType2;
    signal s46_valid: type_LogicType;
    signal s47_ready: type_ReadyVectorTypeArithType2;
    signal s48_data: type_OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078;
    signal s49_last: type_LastVectorTypeArithType2;
    signal s50_valid: type_LogicType;
    signal s51_ready: type_ReadyVectorTypeArithType2;
    signal s52_data: type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078TextTypet1OrderedStreamTypeOrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078_t0;
    signal s53_last: type_LastVectorTypeArithType2;
    signal s54_valid: type_LogicType;
    signal s55_ready: type_ReadyVectorTypeArithType2;
    signal s56_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeSignedIntTypeArithType32ArithType1918TextTypet1OrderedStreamTypeSignedIntTypeArithType32ArithType1918ArithType1078;
    signal s57_last: type_LastVectorTypeArithType2;
    signal s58_valid: type_LogicType;
    signal s59_ready: type_ReadyVectorTypeArithType2;
    signal s60_data: type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType2073600ArithType2;
    signal s61_data: type_VectorTypeLastVectorTypeArithType0ArithType2;
    signal s62_data: type_VectorTypeLogicTypeArithType2;
    signal s63_ready: type_ReadyVectorTypeArithType0;
    signal s64_ready: type_ReadyVectorTypeArithType0;
    signal s65_data: type_VectorTypeSignedIntTypeArithType32ArithType2073600;
    signal s66_last: type_LastVectorTypeArithType0;
    signal s67_valid: type_LogicType;
    signal s68_ready: type_ReadyVectorTypeArithType0;
    signal s69_data: type_VectorTypeSignedIntTypeArithType32ArithType2073600;
    signal s70_last: type_LastVectorTypeArithType0;
    signal s71_valid: type_LogicType;
    signal s72_ready: type_ReadyVectorTypeArithType0;
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
        p1_out_valid => s71_valid,
        p0_in_valid => s02_valid,
        p1_out_data => s69_data,
        p0_out_ready => s03_ready,
        p1_out_last => s70_last,
        reset => reset,
        p1_in_ready => s72_ready,
        p0_in_data => s00_data,
        p0_in_last => s01_last
    );
    U2_id: id_3 port map(
        clk => clk,
        p1_out_valid => s06_valid,
        p0_in_valid => s62_data(0),
        p1_out_data => s04_data,
        p1_out_last => s05_last,
        p0_out_ready => s63_ready,
        p0_in_last => s61_data(0),
        reset => reset,
        p1_in_ready => s07_ready,
        p0_in_data => s60_data(0)
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
    U6_slide2dorderedstream: slide2dorderedstream_10 port map(
        p0_in_data => s16_data,
        clk => clk,
        p0_out_ready => s19_ready,
        p1_out_valid => s22_valid,
        p0_in_valid => s18_valid,
        p1_out_data => s20_data,
        p0_in_last => s17_last,
        p1_out_last => s21_last,
        reset => reset,
        p1_in_ready => s23_ready
    );
    U7_mapsimpleorderedstream: mapsimpleorderedstream_126 port map(
        clk => clk,
        p0_out_ready => s23_ready,
        p1_out_valid => s46_valid,
        p0_in_valid => s22_valid,
        p0_in_data => s20_data,
        p1_out_data => s44_data,
        p0_in_last => s21_last,
        p1_out_last => s45_last,
        reset => reset,
        p1_in_ready => s47_ready
    );
    U8_id: id_127 port map(
        clk => clk,
        p1_out_valid => s26_valid,
        p0_in_valid => s62_data(1),
        p1_out_data => s24_data,
        p1_out_last => s25_last,
        p0_out_ready => s64_ready,
        p0_in_last => s61_data(1),
        reset => reset,
        p1_in_ready => s27_ready,
        p0_in_data => s60_data(1)
    );
    U9_slidevector: slidevector_128 port map(
        clk => clk,
        p1_out_valid => s30_valid,
        p0_in_valid => s26_valid,
        p1_out_data => s28_data,
        p1_out_last => s29_last,
        p0_out_ready => s27_ready,
        p0_in_last => s25_last,
        reset => reset,
        p1_in_ready => s31_ready,
        p0_in_data => s24_data
    );
    U10_vectortoorderedstream: vectortoorderedstream_129 port map(
        p0_in_data => s28_data,
        clk => clk,
        p1_out_valid => s34_valid,
        p0_in_valid => s30_valid,
        p1_out_data => s32_data,
        p0_out_ready => s31_ready,
        p0_in_last => s29_last,
        reset => reset,
        p1_in_ready => s35_ready,
        p1_out_last => s33_last
    );
    U11_mapsimpleorderedstream: mapsimpleorderedstream_133 port map(
        p0_in_data => s32_data,
        clk => clk,
        p1_out_valid => s38_valid,
        p0_in_valid => s34_valid,
        p1_out_data => s36_data,
        p0_out_ready => s35_ready,
        p1_out_last => s37_last,
        reset => reset,
        p1_in_ready => s39_ready,
        p0_in_last => s33_last
    );
    U12_slide2dorderedstream: slide2dorderedstream_134 port map(
        p0_in_data => s36_data,
        clk => clk,
        p0_out_ready => s39_ready,
        p1_out_valid => s42_valid,
        p0_in_valid => s38_valid,
        p1_out_data => s40_data,
        p0_in_last => s37_last,
        p1_out_last => s41_last,
        reset => reset,
        p1_in_ready => s43_ready
    );
    U13_mapsimpleorderedstream: mapsimpleorderedstream_250 port map(
        clk => clk,
        p0_out_ready => s43_ready,
        p1_out_valid => s50_valid,
        p0_in_valid => s42_valid,
        p0_in_data => s40_data,
        p1_out_data => s48_data,
        p0_in_last => s41_last,
        p1_out_last => s49_last,
        reset => reset,
        p1_in_ready => s51_ready
    );
    U14_tuple: tuple_251 port map(
        clk => clk,
        p0_out_ready => s47_ready,
        p2_out_valid => s54_valid,
        p1_out_ready => s51_ready,
        p0_in_valid => s46_valid,
        p1_in_last => s49_last,
        p2_in_ready => s55_ready,
        p2_out_data => s52_data,
        p0_in_data => s44_data,
        p1_in_valid => s50_valid,
        p2_out_last => s53_last,
        p1_in_data => s48_data,
        p0_in_last => s45_last,
        reset => reset
    );
    U15_zipstream: zipstream_252 port map(
        clk => clk,
        p0_out_ready => s55_ready,
        p1_out_valid => s58_valid,
        p0_in_valid => s54_valid,
        p1_out_data => s56_data,
        p0_in_data => s52_data,
        p0_in_last => s53_last,
        p1_out_last => s57_last,
        reset => reset,
        p1_in_ready => s59_ready
    );
    U16_mapsimpleorderedstream: mapsimpleorderedstream_1582 port map(
        clk => clk,
        p0_out_ready => s59_ready,
        p1_out_valid => p1_out_valid,
        p0_in_valid => s58_valid,
        p1_out_data => p1_out_data,
        p0_in_last => s57_last,
        p1_out_last => p1_out_last,
        reset => reset,
        p0_in_data => s56_data,
        p1_in_ready => p1_in_ready
    );
    U17_distributor: distributor_1583 port map(
        clk => clk,
        p0_in_valid => s67_valid,
        p4_in_data(0) => s63_ready,
        p4_in_data(1) => s64_ready,
        p0_out_ready => s68_ready,
        p1_out_data => s60_data,
        p2_out_data => s61_data,
        p0_in_last => s66_last,
        reset => reset,
        p3_out_data => s62_data,
        p0_in_data => s65_data
    );
    U18_id: id_1584 port map(
        clk => clk,
        p1_out_valid => s67_valid,
        p0_in_valid => s71_valid,
        p1_out_data => s65_data,
        p1_out_last => s66_last,
        p0_out_ready => s72_ready,
        p0_in_last => s70_last,
        reset => reset,
        p1_in_ready => s68_ready,
        p0_in_data => s69_data
    );
    
    
end behavioral;
