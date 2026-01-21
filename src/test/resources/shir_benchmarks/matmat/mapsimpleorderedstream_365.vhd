library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_365 is
    generic(
        stream_length: type_NaturalNumberType := 16
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0VectorTypeIntTypeArithType16ArithType16TextTypet1VectorTypeIntTypeArithType16ArithType16ArithType16;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeIntTypeArithType16ArithType16;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end mapsimpleorderedstream_365;

architecture behavioral of mapsimpleorderedstream_365 is
    
    component conversion_361
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType17;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeLogicTypeArithType17;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorfork_247
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType16;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType16;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType16;
            p3_out_data: out type_VectorTypeLogicTypeArithType16;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType16
        );
    end component;
    component mapvector_function_250
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType16ArithType2;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType16;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorjoin_248
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType16ArithType16;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType16;
            p2_in_data: in type_VectorTypeLogicTypeArithType16;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType16;
            p4_out_data: out type_VectorTypeIntTypeArithType16ArithType16;
            p4_out_last: out type_LastVectorTypeArithType0;
            p4_out_valid: out type_LogicType;
            p4_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component slidevector_249
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType16ArithType16;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType16ArithType2ArithType8;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorjoin_308
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType16ArithType8;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType8;
            p2_in_data: in type_VectorTypeLogicTypeArithType8;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType8;
            p4_out_data: out type_VectorTypeIntTypeArithType16ArithType8;
            p4_out_last: out type_LastVectorTypeArithType0;
            p4_out_valid: out type_LogicType;
            p4_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorfork_357
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType16ArithType2ArithType2;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType16ArithType2ArithType2;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType2;
            p3_out_data: out type_VectorTypeLogicTypeArithType2;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType2
        );
    end component;
    component dropvector_362
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType17;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeLogicTypeArithType16;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorjoin_340
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType16ArithType4;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType4;
            p2_in_data: in type_VectorTypeLogicTypeArithType4;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType4;
            p4_out_data: out type_VectorTypeIntTypeArithType16ArithType4;
            p4_out_last: out type_LastVectorTypeArithType0;
            p4_out_valid: out type_LogicType;
            p4_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component slidevector_341
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType16ArithType4;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType16ArithType2ArithType2;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component slidevector_309
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType16ArithType8;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType16ArithType2ArithType4;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapvector_function_310
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType16ArithType2;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType16;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapvector_function_342
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType16ArithType2;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType16;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorfork_307
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType16ArithType2ArithType8;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType16ArithType2ArithType8;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType8;
            p3_out_data: out type_VectorTypeLogicTypeArithType8;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType8
        );
    end component;
    component vectortotuple_359
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType16ArithType2;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorfork_339
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType16ArithType2ArithType4;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType16ArithType2ArithType4;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType4;
            p3_out_data: out type_VectorTypeLogicTypeArithType4;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType4
        );
    end component;
    component conversion_363
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType16;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType16;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component addint_360
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType17;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapvector_function_150
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType16;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorjoin_358
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType16ArithType2;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType2;
            p2_in_data: in type_VectorTypeLogicTypeArithType2;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType2;
            p4_out_data: out type_VectorTypeIntTypeArithType16ArithType2;
            p4_out_last: out type_LastVectorTypeArithType0;
            p4_out_valid: out type_LogicType;
            p4_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component zipvector_149
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0VectorTypeIntTypeArithType16ArithType16TextTypet1VectorTypeIntTypeArithType16ArithType16_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType16;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType16;
    signal s01_data: type_VectorTypeLastVectorTypeArithType0ArithType16;
    signal s02_data: type_VectorTypeLogicTypeArithType16;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_ready: type_ReadyVectorTypeArithType0;
    signal s05_ready: type_ReadyVectorTypeArithType0;
    signal s06_ready: type_ReadyVectorTypeArithType0;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_ready: type_ReadyVectorTypeArithType0;
    signal s09_ready: type_ReadyVectorTypeArithType0;
    signal s10_ready: type_ReadyVectorTypeArithType0;
    signal s100_last: type_LastVectorTypeArithType0;
    signal s101_last: type_LastVectorTypeArithType0;
    signal s102_last: type_LastVectorTypeArithType0;
    signal s103_last: type_LastVectorTypeArithType0;
    signal s104_last: type_LastVectorTypeArithType0;
    signal s105_last: type_LastVectorTypeArithType0;
    signal s106_last: type_LastVectorTypeArithType0;
    signal s107_valid: type_LogicType;
    signal s108_valid: type_LogicType;
    signal s109_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s110_valid: type_LogicType;
    signal s111_valid: type_LogicType;
    signal s112_valid: type_LogicType;
    signal s113_valid: type_LogicType;
    signal s114_valid: type_LogicType;
    signal s115_data: type_VectorTypeReadyVectorTypeArithType0ArithType8;
    signal s116_data: type_VectorTypeIntTypeArithType16ArithType8;
    signal s117_last: type_LastVectorTypeArithType0;
    signal s118_valid: type_LogicType;
    signal s119_ready: type_ReadyVectorTypeArithType0;
    signal s12_ready: type_ReadyVectorTypeArithType0;
    signal s120_data: type_VectorTypeVectorTypeIntTypeArithType16ArithType2ArithType4;
    signal s121_data: type_VectorTypeLastVectorTypeArithType0ArithType4;
    signal s122_data: type_VectorTypeLogicTypeArithType4;
    signal s123_ready: type_ReadyVectorTypeArithType0;
    signal s124_ready: type_ReadyVectorTypeArithType0;
    signal s125_ready: type_ReadyVectorTypeArithType0;
    signal s126_ready: type_ReadyVectorTypeArithType0;
    signal s127_data: type_VectorTypeVectorTypeIntTypeArithType16ArithType2ArithType4;
    signal s128_last: type_LastVectorTypeArithType0;
    signal s129_valid: type_LogicType;
    signal s13_ready: type_ReadyVectorTypeArithType0;
    signal s130_ready: type_ReadyVectorTypeArithType0;
    signal s131_data: type_IntTypeArithType16;
    signal s132_data: type_IntTypeArithType16;
    signal s133_data: type_IntTypeArithType16;
    signal s134_data: type_IntTypeArithType16;
    signal s135_last: type_LastVectorTypeArithType0;
    signal s136_last: type_LastVectorTypeArithType0;
    signal s137_last: type_LastVectorTypeArithType0;
    signal s138_last: type_LastVectorTypeArithType0;
    signal s139_valid: type_LogicType;
    signal s14_ready: type_ReadyVectorTypeArithType0;
    signal s140_valid: type_LogicType;
    signal s141_valid: type_LogicType;
    signal s142_valid: type_LogicType;
    signal s143_data: type_VectorTypeReadyVectorTypeArithType0ArithType4;
    signal s144_data: type_VectorTypeIntTypeArithType16ArithType4;
    signal s145_last: type_LastVectorTypeArithType0;
    signal s146_valid: type_LogicType;
    signal s147_ready: type_ReadyVectorTypeArithType0;
    signal s148_data: type_VectorTypeVectorTypeIntTypeArithType16ArithType2ArithType2;
    signal s149_data: type_VectorTypeLastVectorTypeArithType0ArithType2;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s150_data: type_VectorTypeLogicTypeArithType2;
    signal s151_ready: type_ReadyVectorTypeArithType0;
    signal s152_ready: type_ReadyVectorTypeArithType0;
    signal s153_data: type_VectorTypeVectorTypeIntTypeArithType16ArithType2ArithType2;
    signal s154_last: type_LastVectorTypeArithType0;
    signal s155_valid: type_LogicType;
    signal s156_ready: type_ReadyVectorTypeArithType0;
    signal s157_data: type_IntTypeArithType16;
    signal s158_data: type_IntTypeArithType16;
    signal s159_last: type_LastVectorTypeArithType0;
    signal s16_ready: type_ReadyVectorTypeArithType0;
    signal s160_last: type_LastVectorTypeArithType0;
    signal s161_valid: type_LogicType;
    signal s162_valid: type_LogicType;
    signal s163_data: type_VectorTypeReadyVectorTypeArithType0ArithType2;
    signal s164_data: type_VectorTypeIntTypeArithType16ArithType2;
    signal s165_last: type_LastVectorTypeArithType0;
    signal s166_valid: type_LogicType;
    signal s167_ready: type_ReadyVectorTypeArithType0;
    signal s168_data: type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0;
    signal s169_last: type_LastVectorTypeArithType0;
    signal s17_ready: type_ReadyVectorTypeArithType0;
    signal s170_valid: type_LogicType;
    signal s171_ready: type_ReadyVectorTypeArithType0;
    signal s172_data: type_IntTypeArithType17;
    signal s173_last: type_LastVectorTypeArithType0;
    signal s174_valid: type_LogicType;
    signal s175_ready: type_ReadyVectorTypeArithType0;
    signal s176_data: type_VectorTypeLogicTypeArithType17;
    signal s177_last: type_LastVectorTypeArithType0;
    signal s178_valid: type_LogicType;
    signal s179_ready: type_ReadyVectorTypeArithType0;
    signal s18_ready: type_ReadyVectorTypeArithType0;
    signal s180_data: type_VectorTypeLogicTypeArithType16;
    signal s181_last: type_LastVectorTypeArithType0;
    signal s182_valid: type_LogicType;
    signal s183_ready: type_ReadyVectorTypeArithType0;
    signal s184_data: type_IntTypeArithType16;
    signal s185_last: type_LastVectorTypeArithType0;
    signal s186_valid: type_LogicType;
    signal s187_ready: type_ReadyVectorTypeArithType0;
    signal s188_data: type_NamedTupleTypeTextTypet0VectorTypeIntTypeArithType16ArithType16TextTypet1VectorTypeIntTypeArithType16ArithType16_t0;
    signal s189_last: type_LastVectorTypeArithType0;
    signal s19_data: type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType16;
    signal s190_valid: type_LogicType;
    signal s191_ready: type_ReadyVectorTypeArithType0;
    signal s192_data: type_LastVectorTypeArithType1;
    signal s193_data: type_ReadyVectorTypeArithType1;
    signal s20_last: type_LastVectorTypeArithType0;
    signal s21_valid: type_LogicType;
    signal s22_ready: type_ReadyVectorTypeArithType0;
    signal s23_data: type_IntTypeArithType16;
    signal s24_data: type_IntTypeArithType16;
    signal s25_data: type_IntTypeArithType16;
    signal s26_data: type_IntTypeArithType16;
    signal s27_data: type_IntTypeArithType16;
    signal s28_data: type_IntTypeArithType16;
    signal s29_data: type_IntTypeArithType16;
    signal s30_data: type_IntTypeArithType16;
    signal s31_data: type_IntTypeArithType16;
    signal s32_data: type_IntTypeArithType16;
    signal s33_data: type_IntTypeArithType16;
    signal s34_data: type_IntTypeArithType16;
    signal s35_data: type_IntTypeArithType16;
    signal s36_data: type_IntTypeArithType16;
    signal s37_data: type_IntTypeArithType16;
    signal s38_data: type_IntTypeArithType16;
    signal s39_last: type_LastVectorTypeArithType0;
    signal s40_last: type_LastVectorTypeArithType0;
    signal s41_last: type_LastVectorTypeArithType0;
    signal s42_last: type_LastVectorTypeArithType0;
    signal s43_last: type_LastVectorTypeArithType0;
    signal s44_last: type_LastVectorTypeArithType0;
    signal s45_last: type_LastVectorTypeArithType0;
    signal s46_last: type_LastVectorTypeArithType0;
    signal s47_last: type_LastVectorTypeArithType0;
    signal s48_last: type_LastVectorTypeArithType0;
    signal s49_last: type_LastVectorTypeArithType0;
    signal s50_last: type_LastVectorTypeArithType0;
    signal s51_last: type_LastVectorTypeArithType0;
    signal s52_last: type_LastVectorTypeArithType0;
    signal s53_last: type_LastVectorTypeArithType0;
    signal s54_last: type_LastVectorTypeArithType0;
    signal s55_valid: type_LogicType;
    signal s56_valid: type_LogicType;
    signal s57_valid: type_LogicType;
    signal s58_valid: type_LogicType;
    signal s59_valid: type_LogicType;
    signal s60_valid: type_LogicType;
    signal s61_valid: type_LogicType;
    signal s62_valid: type_LogicType;
    signal s63_valid: type_LogicType;
    signal s64_valid: type_LogicType;
    signal s65_valid: type_LogicType;
    signal s66_valid: type_LogicType;
    signal s67_valid: type_LogicType;
    signal s68_valid: type_LogicType;
    signal s69_valid: type_LogicType;
    signal s70_valid: type_LogicType;
    signal s71_data: type_VectorTypeReadyVectorTypeArithType0ArithType16;
    signal s72_data: type_VectorTypeIntTypeArithType16ArithType16;
    signal s73_last: type_LastVectorTypeArithType0;
    signal s74_valid: type_LogicType;
    signal s75_ready: type_ReadyVectorTypeArithType0;
    signal s76_data: type_VectorTypeVectorTypeIntTypeArithType16ArithType2ArithType8;
    signal s77_data: type_VectorTypeLastVectorTypeArithType0ArithType8;
    signal s78_data: type_VectorTypeLogicTypeArithType8;
    signal s79_ready: type_ReadyVectorTypeArithType0;
    signal s80_ready: type_ReadyVectorTypeArithType0;
    signal s81_ready: type_ReadyVectorTypeArithType0;
    signal s82_ready: type_ReadyVectorTypeArithType0;
    signal s83_ready: type_ReadyVectorTypeArithType0;
    signal s84_ready: type_ReadyVectorTypeArithType0;
    signal s85_ready: type_ReadyVectorTypeArithType0;
    signal s86_ready: type_ReadyVectorTypeArithType0;
    signal s87_data: type_VectorTypeVectorTypeIntTypeArithType16ArithType2ArithType8;
    signal s88_last: type_LastVectorTypeArithType0;
    signal s89_valid: type_LogicType;
    signal s90_ready: type_ReadyVectorTypeArithType0;
    signal s91_data: type_IntTypeArithType16;
    signal s92_data: type_IntTypeArithType16;
    signal s93_data: type_IntTypeArithType16;
    signal s94_data: type_IntTypeArithType16;
    signal s95_data: type_IntTypeArithType16;
    signal s96_data: type_IntTypeArithType16;
    signal s97_data: type_IntTypeArithType16;
    signal s98_data: type_IntTypeArithType16;
    signal s99_last: type_LastVectorTypeArithType0;
begin
    
    U0_zipvector: zipvector_149 port map(
        clk => clk,
        p1_out_data => s19_data,
        p1_out_valid => s21_valid,
        p0_in_valid => s190_valid,
        p0_in_data => s188_data,
        p1_out_last => s20_last,
        p0_out_ready => s191_ready,
        p0_in_last => s189_last,
        reset => reset,
        p1_in_ready => s22_ready
    );
    U1_vectorfork: vectorfork_247 port map(
        p0_in_data => s19_data,
        clk => clk,
        p1_out_data => s00_data,
        p0_in_valid => s21_valid,
        p4_in_data(0) => s03_ready,
        p4_in_data(1) => s04_ready,
        p4_in_data(2) => s05_ready,
        p4_in_data(3) => s06_ready,
        p4_in_data(4) => s07_ready,
        p4_in_data(5) => s08_ready,
        p4_in_data(6) => s09_ready,
        p4_in_data(7) => s10_ready,
        p4_in_data(8) => s11_ready,
        p4_in_data(9) => s12_ready,
        p4_in_data(10) => s13_ready,
        p4_in_data(11) => s14_ready,
        p4_in_data(12) => s15_ready,
        p4_in_data(13) => s16_ready,
        p4_in_data(14) => s17_ready,
        p4_in_data(15) => s18_ready,
        p2_out_data => s01_data,
        p0_out_ready => s22_ready,
        p3_out_data => s02_data,
        p0_in_last => s20_last,
        reset => reset
    );
    U2_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s55_valid,
        p0_in_valid => s02_data(0),
        p1_out_last => s39_last,
        p0_out_ready => s03_ready,
        p0_in_last => s01_data(0),
        reset => reset,
        p1_in_ready => s71_data(0),
        p0_in_data => s00_data(0),
        p1_out_data => s23_data
    );
    U3_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s56_valid,
        p0_in_valid => s02_data(1),
        p1_out_last => s40_last,
        p0_out_ready => s04_ready,
        p0_in_last => s01_data(1),
        reset => reset,
        p1_in_ready => s71_data(1),
        p0_in_data => s00_data(1),
        p1_out_data => s24_data
    );
    U4_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s57_valid,
        p0_in_valid => s02_data(2),
        p1_out_last => s41_last,
        p0_out_ready => s05_ready,
        p0_in_last => s01_data(2),
        reset => reset,
        p1_in_ready => s71_data(2),
        p0_in_data => s00_data(2),
        p1_out_data => s25_data
    );
    U5_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s58_valid,
        p0_in_valid => s02_data(3),
        p1_out_last => s42_last,
        p0_out_ready => s06_ready,
        p0_in_last => s01_data(3),
        reset => reset,
        p1_in_ready => s71_data(3),
        p0_in_data => s00_data(3),
        p1_out_data => s26_data
    );
    U6_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s59_valid,
        p0_in_valid => s02_data(4),
        p1_out_last => s43_last,
        p0_out_ready => s07_ready,
        p0_in_last => s01_data(4),
        reset => reset,
        p1_in_ready => s71_data(4),
        p0_in_data => s00_data(4),
        p1_out_data => s27_data
    );
    U7_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s60_valid,
        p0_in_valid => s02_data(5),
        p1_out_last => s44_last,
        p0_out_ready => s08_ready,
        p0_in_last => s01_data(5),
        reset => reset,
        p1_in_ready => s71_data(5),
        p0_in_data => s00_data(5),
        p1_out_data => s28_data
    );
    U8_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s61_valid,
        p0_in_valid => s02_data(6),
        p1_out_last => s45_last,
        p0_out_ready => s09_ready,
        p0_in_last => s01_data(6),
        reset => reset,
        p1_in_ready => s71_data(6),
        p0_in_data => s00_data(6),
        p1_out_data => s29_data
    );
    U9_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s62_valid,
        p0_in_valid => s02_data(7),
        p1_out_last => s46_last,
        p0_out_ready => s10_ready,
        p0_in_last => s01_data(7),
        reset => reset,
        p1_in_ready => s71_data(7),
        p0_in_data => s00_data(7),
        p1_out_data => s30_data
    );
    U10_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s63_valid,
        p0_in_valid => s02_data(8),
        p1_out_last => s47_last,
        p0_out_ready => s11_ready,
        p0_in_last => s01_data(8),
        reset => reset,
        p1_in_ready => s71_data(8),
        p0_in_data => s00_data(8),
        p1_out_data => s31_data
    );
    U11_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s64_valid,
        p0_in_valid => s02_data(9),
        p1_out_last => s48_last,
        p0_out_ready => s12_ready,
        p0_in_last => s01_data(9),
        reset => reset,
        p1_in_ready => s71_data(9),
        p0_in_data => s00_data(9),
        p1_out_data => s32_data
    );
    U12_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s65_valid,
        p0_in_valid => s02_data(10),
        p1_out_last => s49_last,
        p0_out_ready => s13_ready,
        p0_in_last => s01_data(10),
        reset => reset,
        p1_in_ready => s71_data(10),
        p0_in_data => s00_data(10),
        p1_out_data => s33_data
    );
    U13_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s66_valid,
        p0_in_valid => s02_data(11),
        p1_out_last => s50_last,
        p0_out_ready => s14_ready,
        p0_in_last => s01_data(11),
        reset => reset,
        p1_in_ready => s71_data(11),
        p0_in_data => s00_data(11),
        p1_out_data => s34_data
    );
    U14_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s67_valid,
        p0_in_valid => s02_data(12),
        p1_out_last => s51_last,
        p0_out_ready => s15_ready,
        p0_in_last => s01_data(12),
        reset => reset,
        p1_in_ready => s71_data(12),
        p0_in_data => s00_data(12),
        p1_out_data => s35_data
    );
    U15_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s68_valid,
        p0_in_valid => s02_data(13),
        p1_out_last => s52_last,
        p0_out_ready => s16_ready,
        p0_in_last => s01_data(13),
        reset => reset,
        p1_in_ready => s71_data(13),
        p0_in_data => s00_data(13),
        p1_out_data => s36_data
    );
    U16_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s69_valid,
        p0_in_valid => s02_data(14),
        p1_out_last => s53_last,
        p0_out_ready => s17_ready,
        p0_in_last => s01_data(14),
        reset => reset,
        p1_in_ready => s71_data(14),
        p0_in_data => s00_data(14),
        p1_out_data => s37_data
    );
    U17_mapvector_function: mapvector_function_150 port map(
        clk => clk,
        p1_out_valid => s70_valid,
        p0_in_valid => s02_data(15),
        p1_out_last => s54_last,
        p0_out_ready => s18_ready,
        p0_in_last => s01_data(15),
        reset => reset,
        p1_in_ready => s71_data(15),
        p0_in_data => s00_data(15),
        p1_out_data => s38_data
    );
    U18_vectorjoin: vectorjoin_248 port map(
        clk => clk,
        p4_out_last => s73_last,
        p1_in_data(0) => s39_last,
        p1_in_data(1) => s40_last,
        p1_in_data(2) => s41_last,
        p1_in_data(3) => s42_last,
        p1_in_data(4) => s43_last,
        p1_in_data(5) => s44_last,
        p1_in_data(6) => s45_last,
        p1_in_data(7) => s46_last,
        p1_in_data(8) => s47_last,
        p1_in_data(9) => s48_last,
        p1_in_data(10) => s49_last,
        p1_in_data(11) => s50_last,
        p1_in_data(12) => s51_last,
        p1_in_data(13) => s52_last,
        p1_in_data(14) => s53_last,
        p1_in_data(15) => s54_last,
        p4_out_valid => s74_valid,
        p4_in_ready => s75_ready,
        p3_out_data => s71_data,
        p4_out_data => s72_data,
        p2_in_data(0) => s55_valid,
        p2_in_data(1) => s56_valid,
        p2_in_data(2) => s57_valid,
        p2_in_data(3) => s58_valid,
        p2_in_data(4) => s59_valid,
        p2_in_data(5) => s60_valid,
        p2_in_data(6) => s61_valid,
        p2_in_data(7) => s62_valid,
        p2_in_data(8) => s63_valid,
        p2_in_data(9) => s64_valid,
        p2_in_data(10) => s65_valid,
        p2_in_data(11) => s66_valid,
        p2_in_data(12) => s67_valid,
        p2_in_data(13) => s68_valid,
        p2_in_data(14) => s69_valid,
        p2_in_data(15) => s70_valid,
        reset => reset,
        p0_in_data(0) => s23_data,
        p0_in_data(1) => s24_data,
        p0_in_data(2) => s25_data,
        p0_in_data(3) => s26_data,
        p0_in_data(4) => s27_data,
        p0_in_data(5) => s28_data,
        p0_in_data(6) => s29_data,
        p0_in_data(7) => s30_data,
        p0_in_data(8) => s31_data,
        p0_in_data(9) => s32_data,
        p0_in_data(10) => s33_data,
        p0_in_data(11) => s34_data,
        p0_in_data(12) => s35_data,
        p0_in_data(13) => s36_data,
        p0_in_data(14) => s37_data,
        p0_in_data(15) => s38_data
    );
    U19_slidevector: slidevector_249 port map(
        clk => clk,
        p1_out_valid => s89_valid,
        p0_in_valid => s74_valid,
        p1_out_last => s88_last,
        p0_out_ready => s75_ready,
        p0_in_last => s73_last,
        reset => reset,
        p1_in_ready => s90_ready,
        p0_in_data => s72_data,
        p1_out_data => s87_data
    );
    U20_vectorfork: vectorfork_307 port map(
        p2_out_data => s77_data,
        clk => clk,
        p0_in_data => s87_data,
        p0_in_valid => s89_valid,
        p4_in_data(0) => s79_ready,
        p4_in_data(1) => s80_ready,
        p4_in_data(2) => s81_ready,
        p4_in_data(3) => s82_ready,
        p4_in_data(4) => s83_ready,
        p4_in_data(5) => s84_ready,
        p4_in_data(6) => s85_ready,
        p4_in_data(7) => s86_ready,
        p3_out_data => s78_data,
        p0_out_ready => s90_ready,
        p0_in_last => s88_last,
        reset => reset,
        p1_out_data => s76_data
    );
    U21_mapvector_function: mapvector_function_250 port map(
        clk => clk,
        p1_out_valid => s107_valid,
        p0_in_valid => s78_data(0),
        p0_in_data => s76_data(0),
        p1_out_last => s99_last,
        p0_out_ready => s79_ready,
        p0_in_last => s77_data(0),
        reset => reset,
        p1_in_ready => s115_data(0),
        p1_out_data => s91_data
    );
    U22_mapvector_function: mapvector_function_250 port map(
        clk => clk,
        p1_out_valid => s108_valid,
        p0_in_valid => s78_data(1),
        p0_in_data => s76_data(1),
        p1_out_last => s100_last,
        p0_out_ready => s80_ready,
        p0_in_last => s77_data(1),
        reset => reset,
        p1_in_ready => s115_data(1),
        p1_out_data => s92_data
    );
    U23_mapvector_function: mapvector_function_250 port map(
        clk => clk,
        p1_out_valid => s109_valid,
        p0_in_valid => s78_data(2),
        p0_in_data => s76_data(2),
        p1_out_last => s101_last,
        p0_out_ready => s81_ready,
        p0_in_last => s77_data(2),
        reset => reset,
        p1_in_ready => s115_data(2),
        p1_out_data => s93_data
    );
    U24_mapvector_function: mapvector_function_250 port map(
        clk => clk,
        p1_out_valid => s110_valid,
        p0_in_valid => s78_data(3),
        p0_in_data => s76_data(3),
        p1_out_last => s102_last,
        p0_out_ready => s82_ready,
        p0_in_last => s77_data(3),
        reset => reset,
        p1_in_ready => s115_data(3),
        p1_out_data => s94_data
    );
    U25_mapvector_function: mapvector_function_250 port map(
        clk => clk,
        p1_out_valid => s111_valid,
        p0_in_valid => s78_data(4),
        p0_in_data => s76_data(4),
        p1_out_last => s103_last,
        p0_out_ready => s83_ready,
        p0_in_last => s77_data(4),
        reset => reset,
        p1_in_ready => s115_data(4),
        p1_out_data => s95_data
    );
    U26_mapvector_function: mapvector_function_250 port map(
        clk => clk,
        p1_out_valid => s112_valid,
        p0_in_valid => s78_data(5),
        p0_in_data => s76_data(5),
        p1_out_last => s104_last,
        p0_out_ready => s84_ready,
        p0_in_last => s77_data(5),
        reset => reset,
        p1_in_ready => s115_data(5),
        p1_out_data => s96_data
    );
    U27_mapvector_function: mapvector_function_250 port map(
        clk => clk,
        p1_out_valid => s113_valid,
        p0_in_valid => s78_data(6),
        p0_in_data => s76_data(6),
        p1_out_last => s105_last,
        p0_out_ready => s85_ready,
        p0_in_last => s77_data(6),
        reset => reset,
        p1_in_ready => s115_data(6),
        p1_out_data => s97_data
    );
    U28_mapvector_function: mapvector_function_250 port map(
        clk => clk,
        p1_out_valid => s114_valid,
        p0_in_valid => s78_data(7),
        p0_in_data => s76_data(7),
        p1_out_last => s106_last,
        p0_out_ready => s86_ready,
        p0_in_last => s77_data(7),
        reset => reset,
        p1_in_ready => s115_data(7),
        p1_out_data => s98_data
    );
    U29_vectorjoin: vectorjoin_308 port map(
        clk => clk,
        p3_out_data => s115_data,
        p4_out_last => s117_last,
        p4_out_valid => s118_valid,
        p0_in_data(0) => s91_data,
        p0_in_data(1) => s92_data,
        p0_in_data(2) => s93_data,
        p0_in_data(3) => s94_data,
        p0_in_data(4) => s95_data,
        p0_in_data(5) => s96_data,
        p0_in_data(6) => s97_data,
        p0_in_data(7) => s98_data,
        p2_in_data(0) => s107_valid,
        p2_in_data(1) => s108_valid,
        p2_in_data(2) => s109_valid,
        p2_in_data(3) => s110_valid,
        p2_in_data(4) => s111_valid,
        p2_in_data(5) => s112_valid,
        p2_in_data(6) => s113_valid,
        p2_in_data(7) => s114_valid,
        p4_in_ready => s119_ready,
        p4_out_data => s116_data,
        p1_in_data(0) => s99_last,
        p1_in_data(1) => s100_last,
        p1_in_data(2) => s101_last,
        p1_in_data(3) => s102_last,
        p1_in_data(4) => s103_last,
        p1_in_data(5) => s104_last,
        p1_in_data(6) => s105_last,
        p1_in_data(7) => s106_last,
        reset => reset
    );
    U30_slidevector: slidevector_309 port map(
        clk => clk,
        p1_out_valid => s129_valid,
        p0_in_valid => s118_valid,
        p1_out_last => s128_last,
        p0_in_data => s116_data,
        p0_out_ready => s119_ready,
        p0_in_last => s117_last,
        p1_out_data => s127_data,
        reset => reset,
        p1_in_ready => s130_ready
    );
    U31_vectorfork: vectorfork_339 port map(
        clk => clk,
        p0_in_valid => s129_valid,
        p3_out_data => s122_data,
        p0_out_ready => s130_ready,
        p0_in_last => s128_last,
        p1_out_data => s120_data,
        p4_in_data(0) => s123_ready,
        p4_in_data(1) => s124_ready,
        p4_in_data(2) => s125_ready,
        p4_in_data(3) => s126_ready,
        reset => reset,
        p2_out_data => s121_data,
        p0_in_data => s127_data
    );
    U32_mapvector_function: mapvector_function_310 port map(
        clk => clk,
        p1_out_valid => s139_valid,
        p0_in_valid => s122_data(0),
        p0_in_data => s120_data(0),
        p1_out_last => s135_last,
        p0_out_ready => s123_ready,
        p0_in_last => s121_data(0),
        reset => reset,
        p1_in_ready => s143_data(0),
        p1_out_data => s131_data
    );
    U33_mapvector_function: mapvector_function_310 port map(
        clk => clk,
        p1_out_valid => s140_valid,
        p0_in_valid => s122_data(1),
        p0_in_data => s120_data(1),
        p1_out_last => s136_last,
        p0_out_ready => s124_ready,
        p0_in_last => s121_data(1),
        reset => reset,
        p1_in_ready => s143_data(1),
        p1_out_data => s132_data
    );
    U34_mapvector_function: mapvector_function_310 port map(
        clk => clk,
        p1_out_valid => s141_valid,
        p0_in_valid => s122_data(2),
        p0_in_data => s120_data(2),
        p1_out_last => s137_last,
        p0_out_ready => s125_ready,
        p0_in_last => s121_data(2),
        reset => reset,
        p1_in_ready => s143_data(2),
        p1_out_data => s133_data
    );
    U35_mapvector_function: mapvector_function_310 port map(
        clk => clk,
        p1_out_valid => s142_valid,
        p0_in_valid => s122_data(3),
        p0_in_data => s120_data(3),
        p1_out_last => s138_last,
        p0_out_ready => s126_ready,
        p0_in_last => s121_data(3),
        reset => reset,
        p1_in_ready => s143_data(3),
        p1_out_data => s134_data
    );
    U36_vectorjoin: vectorjoin_340 port map(
        clk => clk,
        p3_out_data => s143_data,
        p4_out_last => s145_last,
        p0_in_data(0) => s131_data,
        p0_in_data(1) => s132_data,
        p0_in_data(2) => s133_data,
        p0_in_data(3) => s134_data,
        p4_out_valid => s146_valid,
        p2_in_data(0) => s139_valid,
        p2_in_data(1) => s140_valid,
        p2_in_data(2) => s141_valid,
        p2_in_data(3) => s142_valid,
        p4_in_ready => s147_ready,
        p4_out_data => s144_data,
        reset => reset,
        p1_in_data(0) => s135_last,
        p1_in_data(1) => s136_last,
        p1_in_data(2) => s137_last,
        p1_in_data(3) => s138_last
    );
    U37_slidevector: slidevector_341 port map(
        clk => clk,
        p1_out_valid => s155_valid,
        p0_in_valid => s146_valid,
        p0_in_data => s144_data,
        p1_out_last => s154_last,
        p0_out_ready => s147_ready,
        p1_out_data => s153_data,
        p0_in_last => s145_last,
        reset => reset,
        p1_in_ready => s156_ready
    );
    U38_vectorfork: vectorfork_357 port map(
        clk => clk,
        p0_in_data => s153_data,
        p0_in_valid => s155_valid,
        p4_in_data(0) => s151_ready,
        p4_in_data(1) => s152_ready,
        p0_out_ready => s156_ready,
        p1_out_data => s148_data,
        p2_out_data => s149_data,
        p0_in_last => s154_last,
        reset => reset,
        p3_out_data => s150_data
    );
    U39_mapvector_function: mapvector_function_342 port map(
        clk => clk,
        p1_out_valid => s161_valid,
        p0_in_valid => s150_data(0),
        p0_in_data => s148_data(0),
        p1_out_last => s159_last,
        p0_out_ready => s151_ready,
        p0_in_last => s149_data(0),
        reset => reset,
        p1_in_ready => s163_data(0),
        p1_out_data => s157_data
    );
    U40_mapvector_function: mapvector_function_342 port map(
        clk => clk,
        p1_out_valid => s162_valid,
        p0_in_valid => s150_data(1),
        p0_in_data => s148_data(1),
        p1_out_last => s160_last,
        p0_out_ready => s152_ready,
        p0_in_last => s149_data(1),
        reset => reset,
        p1_in_ready => s163_data(1),
        p1_out_data => s158_data
    );
    U41_vectorjoin: vectorjoin_358 port map(
        clk => clk,
        p4_out_last => s165_last,
        p3_out_data => s163_data,
        p0_in_data(0) => s157_data,
        p0_in_data(1) => s158_data,
        p4_out_valid => s166_valid,
        p4_in_ready => s167_ready,
        p2_in_data(0) => s161_valid,
        p2_in_data(1) => s162_valid,
        reset => reset,
        p4_out_data => s164_data,
        p1_in_data(0) => s159_last,
        p1_in_data(1) => s160_last
    );
    U42_vectortotuple: vectortotuple_359 port map(
        clk => clk,
        p1_out_valid => s170_valid,
        p0_in_valid => s166_valid,
        p0_in_data => s164_data,
        p1_out_last => s169_last,
        p1_out_data => s168_data,
        p0_out_ready => s167_ready,
        p0_in_last => s165_last,
        reset => reset,
        p1_in_ready => s171_ready
    );
    U43_addint: addint_360 port map(
        clk => clk,
        p1_out_valid => s174_valid,
        p0_in_valid => s170_valid,
        p1_out_last => s173_last,
        p0_out_ready => s171_ready,
        p0_in_last => s169_last,
        reset => reset,
        p1_in_ready => s175_ready,
        p0_in_data => s168_data,
        p1_out_data => s172_data
    );
    U44_conversion: conversion_361 port map(
        clk => clk,
        p1_out_valid => s178_valid,
        p0_in_valid => s174_valid,
        p1_out_data => s176_data,
        p1_out_last => s177_last,
        p0_out_ready => s175_ready,
        p0_in_last => s173_last,
        reset => reset,
        p1_in_ready => s179_ready,
        p0_in_data => s172_data
    );
    U45_dropvector: dropvector_362 port map(
        p0_in_data => s176_data,
        clk => clk,
        p1_out_valid => s182_valid,
        p0_in_valid => s178_valid,
        p1_out_last => s181_last,
        p1_out_data => s180_data,
        p0_out_ready => s179_ready,
        p0_in_last => s177_last,
        reset => reset,
        p1_in_ready => s183_ready
    );
    U46_conversion: conversion_363 port map(
        clk => clk,
        p1_out_valid => s186_valid,
        p0_in_valid => s182_valid,
        p1_out_last => s185_last,
        p0_out_ready => s183_ready,
        p0_in_data => s180_data,
        p0_in_last => s181_last,
        reset => reset,
        p1_in_ready => s187_ready,
        p1_out_data => s184_data
    );
    p1_out_data <= s184_data;
    p1_out_last <= "1" & s185_last when out_counter = stream_length - 1 else "0" & s185_last;
    p1_out_valid <= s186_valid;
    s187_ready <= p1_in_ready(s187_ready'high downto s187_ready'low);
    
    s188_data <= p0_in_data;
    s189_last <= p0_in_last(s189_last'high downto s189_last'low);
    s190_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s191_ready when in_counter = stream_length - 1 and s191_ready(s191_ready'high) = '1' else "0" & s191_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s190_valid = '1' and s191_ready(s191_ready'high) = '1' then
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
                if s186_valid = '1' and s187_ready(s187_ready'high) = '1' then
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
