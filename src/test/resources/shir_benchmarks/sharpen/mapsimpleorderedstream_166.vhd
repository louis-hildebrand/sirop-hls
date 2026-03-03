library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_166 is
    generic(
        stream_length: type_NaturalNumberType := 1918
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3ArithType1918;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType1918;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end mapsimpleorderedstream_166;

architecture behavioral of mapsimpleorderedstream_166 is
    
    component joinvector_160
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeIntTypeArithType32ArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component select_162
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32TextTypet2IntTypeArithType32TextTypet3IntTypeArithType32TextTypet4IntTypeArithType32TextTypet5IntTypeArithType32TextTypet6IntTypeArithType32TextTypet7IntTypeArithType32TextTypet8IntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapvector_function_10
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorfork_65
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType9;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType9;
            p3_out_data: out type_VectorTypeLogicTypeArithType9;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType9
        );
    end component;
    component tuple_8
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_VectorTypeIntTypeArithType32ArithType9;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0VectorTypeIntTypeArithType32ArithType9TextTypet1VectorTypeIntTypeArithType32ArithType9_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component resizelowinteger_157
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType28;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component zipvector_9
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0VectorTypeIntTypeArithType32ArithType9TextTypet1VectorTypeIntTypeArithType32ArithType9_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapvector_function_68
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_IntTypeArithType32;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0;
            p1_in_data: in type_VectorTypeIntTypeArithType32ArithType9;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0
        );
    end component;
    component resizeinteger_158
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType28;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_3
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_163
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_IntTypeArithType32;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_159
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component constantbitvector_5
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_VectorTypeLogicTypeArithType288;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component joinvector_4
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeIntTypeArithType32ArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectortotuple_161
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32TextTypet2IntTypeArithType32TextTypet3IntTypeArithType32TextTypet4IntTypeArithType32TextTypet5IntTypeArithType32TextTypet6IntTypeArithType32TextTypet7IntTypeArithType32TextTypet8IntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorjoin_153
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType1;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType1;
            p2_in_data: in type_VectorTypeLogicTypeArithType1;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType1;
            p4_out_data: out type_VectorTypeIntTypeArithType32ArithType1;
            p4_out_last: out type_LastVectorTypeArithType0;
            p4_out_valid: out type_LogicType;
            p4_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component slidevector_6
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType288;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeLogicTypeArithType32ArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component slidevector_67
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType32ArithType9ArithType1;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component joinvector_155
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeLogicTypeArithType32ArithType1;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeLogicTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_156
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_154
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType1;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeLogicTypeArithType32ArithType1;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_7
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeLogicTypeArithType32ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeIntTypeArithType32ArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorjoin_66
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeIntTypeArithType32ArithType9;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType9;
            p2_in_data: in type_VectorTypeLogicTypeArithType9;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType9;
            p4_out_data: out type_VectorTypeIntTypeArithType32ArithType9;
            p4_out_last: out type_LastVectorTypeArithType0;
            p4_out_valid: out type_LogicType;
            p4_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_165
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component distributor_164
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3ArithType2;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType2;
            p3_out_data: out type_VectorTypeLogicTypeArithType2;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType2
        );
    end component;
    component vectorfork_152
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeIntTypeArithType32ArithType9ArithType1;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeIntTypeArithType32ArithType9ArithType1;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType1;
            p3_out_data: out type_VectorTypeLogicTypeArithType1;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType1
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_VectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_VectorTypeLogicTypeArithType288;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_data: type_VectorTypeVectorTypeLogicTypeArithType32ArithType9;
    signal s09_last: type_LastVectorTypeArithType0;
    signal s10_valid: type_LogicType;
    signal s100_data: type_IntTypeArithType28;
    signal s101_last: type_LastVectorTypeArithType0;
    signal s102_valid: type_LogicType;
    signal s103_ready: type_ReadyVectorTypeArithType0;
    signal s104_data: type_VectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3;
    signal s105_last: type_LastVectorTypeArithType0;
    signal s106_valid: type_LogicType;
    signal s107_ready: type_ReadyVectorTypeArithType0;
    signal s108_data: type_VectorTypeIntTypeArithType32ArithType9;
    signal s109_last: type_LastVectorTypeArithType0;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s110_valid: type_LogicType;
    signal s111_ready: type_ReadyVectorTypeArithType0;
    signal s112_data: type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32TextTypet2IntTypeArithType32TextTypet3IntTypeArithType32TextTypet4IntTypeArithType32TextTypet5IntTypeArithType32TextTypet6IntTypeArithType32TextTypet7IntTypeArithType32TextTypet8IntTypeArithType32_t0;
    signal s113_last: type_LastVectorTypeArithType0;
    signal s114_valid: type_LogicType;
    signal s115_ready: type_ReadyVectorTypeArithType0;
    signal s116_data: type_IntTypeArithType32;
    signal s117_last: type_LastVectorTypeArithType0;
    signal s118_valid: type_LogicType;
    signal s119_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_VectorTypeIntTypeArithType32ArithType9;
    signal s120_data: type_IntTypeArithType32;
    signal s121_last: type_LastVectorTypeArithType0;
    signal s122_valid: type_LogicType;
    signal s123_ready: type_ReadyVectorTypeArithType0;
    signal s124_data: type_VectorTypeVectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3ArithType2;
    signal s125_data: type_VectorTypeLastVectorTypeArithType0ArithType2;
    signal s126_data: type_VectorTypeLogicTypeArithType2;
    signal s127_ready: type_ReadyVectorTypeArithType0;
    signal s128_ready: type_ReadyVectorTypeArithType0;
    signal s129_data: type_VectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3;
    signal s13_last: type_LastVectorTypeArithType0;
    signal s130_last: type_LastVectorTypeArithType0;
    signal s131_valid: type_LogicType;
    signal s132_ready: type_ReadyVectorTypeArithType0;
    signal s133_data: type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
    signal s134_last: type_LastVectorTypeArithType0;
    signal s135_valid: type_LogicType;
    signal s136_ready: type_ReadyVectorTypeArithType0;
    signal s137_data: type_VectorTypeVectorTypeIntTypeArithType32ArithType3ArithType3;
    signal s138_last: type_LastVectorTypeArithType0;
    signal s139_valid: type_LogicType;
    signal s14_valid: type_LogicType;
    signal s140_ready: type_ReadyVectorTypeArithType0;
    signal s141_data: type_LastVectorTypeArithType1;
    signal s142_data: type_ReadyVectorTypeArithType1;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s16_data: type_VectorTypeIntTypeArithType32ArithType9;
    signal s17_last: type_LastVectorTypeArithType0;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType0;
    signal s20_data: type_NamedTupleTypeTextTypet0VectorTypeIntTypeArithType32ArithType9TextTypet1VectorTypeIntTypeArithType32ArithType9_t0;
    signal s21_last: type_LastVectorTypeArithType0;
    signal s22_valid: type_LogicType;
    signal s23_ready: type_ReadyVectorTypeArithType0;
    signal s24_data: type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType9;
    signal s25_data: type_VectorTypeLastVectorTypeArithType0ArithType9;
    signal s26_data: type_VectorTypeLogicTypeArithType9;
    signal s27_ready: type_ReadyVectorTypeArithType0;
    signal s28_ready: type_ReadyVectorTypeArithType0;
    signal s29_ready: type_ReadyVectorTypeArithType0;
    signal s30_ready: type_ReadyVectorTypeArithType0;
    signal s31_ready: type_ReadyVectorTypeArithType0;
    signal s32_ready: type_ReadyVectorTypeArithType0;
    signal s33_ready: type_ReadyVectorTypeArithType0;
    signal s34_ready: type_ReadyVectorTypeArithType0;
    signal s35_ready: type_ReadyVectorTypeArithType0;
    signal s36_data: type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType9;
    signal s37_last: type_LastVectorTypeArithType0;
    signal s38_valid: type_LogicType;
    signal s39_ready: type_ReadyVectorTypeArithType0;
    signal s40_data: type_IntTypeArithType32;
    signal s41_data: type_IntTypeArithType32;
    signal s42_data: type_IntTypeArithType32;
    signal s43_data: type_IntTypeArithType32;
    signal s44_data: type_IntTypeArithType32;
    signal s45_data: type_IntTypeArithType32;
    signal s46_data: type_IntTypeArithType32;
    signal s47_data: type_IntTypeArithType32;
    signal s48_data: type_IntTypeArithType32;
    signal s49_last: type_LastVectorTypeArithType0;
    signal s50_last: type_LastVectorTypeArithType0;
    signal s51_last: type_LastVectorTypeArithType0;
    signal s52_last: type_LastVectorTypeArithType0;
    signal s53_last: type_LastVectorTypeArithType0;
    signal s54_last: type_LastVectorTypeArithType0;
    signal s55_last: type_LastVectorTypeArithType0;
    signal s56_last: type_LastVectorTypeArithType0;
    signal s57_last: type_LastVectorTypeArithType0;
    signal s58_valid: type_LogicType;
    signal s59_valid: type_LogicType;
    signal s60_valid: type_LogicType;
    signal s61_valid: type_LogicType;
    signal s62_valid: type_LogicType;
    signal s63_valid: type_LogicType;
    signal s64_valid: type_LogicType;
    signal s65_valid: type_LogicType;
    signal s66_valid: type_LogicType;
    signal s67_data: type_VectorTypeReadyVectorTypeArithType0ArithType9;
    signal s68_data: type_VectorTypeIntTypeArithType32ArithType9;
    signal s69_last: type_LastVectorTypeArithType0;
    signal s70_valid: type_LogicType;
    signal s71_ready: type_ReadyVectorTypeArithType0;
    signal s72_data: type_VectorTypeVectorTypeIntTypeArithType32ArithType9ArithType1;
    signal s73_data: type_VectorTypeLastVectorTypeArithType0ArithType1;
    signal s74_data: type_VectorTypeLogicTypeArithType1;
    signal s75_ready: type_ReadyVectorTypeArithType0;
    signal s76_data: type_VectorTypeVectorTypeIntTypeArithType32ArithType9ArithType1;
    signal s77_last: type_LastVectorTypeArithType0;
    signal s78_valid: type_LogicType;
    signal s79_ready: type_ReadyVectorTypeArithType0;
    signal s80_data: type_IntTypeArithType32;
    signal s81_last: type_LastVectorTypeArithType0;
    signal s82_valid: type_LogicType;
    signal s83_data: type_VectorTypeReadyVectorTypeArithType0ArithType1;
    signal s84_data: type_VectorTypeIntTypeArithType32ArithType1;
    signal s85_last: type_LastVectorTypeArithType0;
    signal s86_valid: type_LogicType;
    signal s87_ready: type_ReadyVectorTypeArithType0;
    signal s88_data: type_VectorTypeVectorTypeLogicTypeArithType32ArithType1;
    signal s89_last: type_LastVectorTypeArithType0;
    signal s90_valid: type_LogicType;
    signal s91_ready: type_ReadyVectorTypeArithType0;
    signal s92_data: type_VectorTypeLogicTypeArithType32;
    signal s93_last: type_LastVectorTypeArithType0;
    signal s94_valid: type_LogicType;
    signal s95_ready: type_ReadyVectorTypeArithType0;
    signal s96_data: type_IntTypeArithType32;
    signal s97_last: type_LastVectorTypeArithType0;
    signal s98_valid: type_LogicType;
    signal s99_ready: type_ReadyVectorTypeArithType0;
begin
    
    U0_id: id_3 port map(
        clk => clk,
        p1_out_valid => s02_valid,
        p0_in_valid => s126_data(0),
        p0_in_data => s124_data(0),
        p1_out_last => s01_last,
        p1_out_data => s00_data,
        p0_out_ready => s127_ready,
        p0_in_last => s125_data(0),
        reset => reset,
        p1_in_ready => s03_ready
    );
    U1_joinvector: joinvector_4 port map(
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => s02_valid,
        p0_in_data => s00_data,
        p1_out_last => s13_last,
        p1_out_data => s12_data,
        p0_out_ready => s03_ready,
        p0_in_last => s01_last,
        reset => reset,
        p1_in_ready => s15_ready
    );
    U2_constantbitvector: constantbitvector_5 port map(
        p0_out_last => s05_last,
        clk => clk,
        p0_out_data => s04_data,
        p0_in_ready => s07_ready,
        p0_out_valid => s06_valid,
        reset => reset
    );
    U3_slidevector: slidevector_6 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s06_valid,
        p0_in_data => s04_data,
        p1_out_data => s08_data,
        p1_out_last => s09_last,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => s11_ready
    );
    U4_conversion: conversion_7 port map(
        clk => clk,
        p1_out_valid => s18_valid,
        p0_in_valid => s10_valid,
        p0_in_data => s08_data,
        p1_out_last => s17_last,
        p1_out_data => s16_data,
        p0_out_ready => s11_ready,
        p0_in_last => s09_last,
        reset => reset,
        p1_in_ready => s19_ready
    );
    U5_tuple: tuple_8 port map(
        clk => clk,
        p2_out_valid => s22_valid,
        p2_out_data => s20_data,
        p1_in_last => s17_last,
        p0_in_valid => s14_valid,
        p2_out_last => s21_last,
        p0_out_ready => s15_ready,
        p1_in_valid => s18_valid,
        p2_in_ready => s23_ready,
        p0_in_last => s13_last,
        reset => reset,
        p0_in_data => s12_data,
        p1_in_data => s16_data,
        p1_out_ready => s19_ready
    );
    U6_zipvector: zipvector_9 port map(
        clk => clk,
        p1_out_valid => s38_valid,
        p0_in_valid => s22_valid,
        p0_in_data => s20_data,
        p1_out_last => s37_last,
        p0_out_ready => s23_ready,
        p1_out_data => s36_data,
        p0_in_last => s21_last,
        reset => reset,
        p1_in_ready => s39_ready
    );
    U7_vectorfork: vectorfork_65 port map(
        p3_out_data => s26_data,
        clk => clk,
        p0_in_valid => s38_valid,
        p0_in_data => s36_data,
        p2_out_data => s25_data,
        p0_out_ready => s39_ready,
        p1_out_data => s24_data,
        p0_in_last => s37_last,
        reset => reset,
        p4_in_data(0) => s27_ready,
        p4_in_data(1) => s28_ready,
        p4_in_data(2) => s29_ready,
        p4_in_data(3) => s30_ready,
        p4_in_data(4) => s31_ready,
        p4_in_data(5) => s32_ready,
        p4_in_data(6) => s33_ready,
        p4_in_data(7) => s34_ready,
        p4_in_data(8) => s35_ready
    );
    U8_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s58_valid,
        p0_in_valid => s26_data(0),
        p1_out_last => s49_last,
        p0_out_ready => s27_ready,
        p0_in_last => s25_data(0),
        reset => reset,
        p1_in_ready => s67_data(0),
        p1_out_data => s40_data,
        p0_in_data => s24_data(0)
    );
    U9_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s59_valid,
        p0_in_valid => s26_data(1),
        p1_out_last => s50_last,
        p0_out_ready => s28_ready,
        p0_in_last => s25_data(1),
        reset => reset,
        p1_in_ready => s67_data(1),
        p1_out_data => s41_data,
        p0_in_data => s24_data(1)
    );
    U10_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s60_valid,
        p0_in_valid => s26_data(2),
        p1_out_last => s51_last,
        p0_out_ready => s29_ready,
        p0_in_last => s25_data(2),
        reset => reset,
        p1_in_ready => s67_data(2),
        p1_out_data => s42_data,
        p0_in_data => s24_data(2)
    );
    U11_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s61_valid,
        p0_in_valid => s26_data(3),
        p1_out_last => s52_last,
        p0_out_ready => s30_ready,
        p0_in_last => s25_data(3),
        reset => reset,
        p1_in_ready => s67_data(3),
        p1_out_data => s43_data,
        p0_in_data => s24_data(3)
    );
    U12_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s62_valid,
        p0_in_valid => s26_data(4),
        p1_out_last => s53_last,
        p0_out_ready => s31_ready,
        p0_in_last => s25_data(4),
        reset => reset,
        p1_in_ready => s67_data(4),
        p1_out_data => s44_data,
        p0_in_data => s24_data(4)
    );
    U13_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s63_valid,
        p0_in_valid => s26_data(5),
        p1_out_last => s54_last,
        p0_out_ready => s32_ready,
        p0_in_last => s25_data(5),
        reset => reset,
        p1_in_ready => s67_data(5),
        p1_out_data => s45_data,
        p0_in_data => s24_data(5)
    );
    U14_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s64_valid,
        p0_in_valid => s26_data(6),
        p1_out_last => s55_last,
        p0_out_ready => s33_ready,
        p0_in_last => s25_data(6),
        reset => reset,
        p1_in_ready => s67_data(6),
        p1_out_data => s46_data,
        p0_in_data => s24_data(6)
    );
    U15_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s65_valid,
        p0_in_valid => s26_data(7),
        p1_out_last => s56_last,
        p0_out_ready => s34_ready,
        p0_in_last => s25_data(7),
        reset => reset,
        p1_in_ready => s67_data(7),
        p1_out_data => s47_data,
        p0_in_data => s24_data(7)
    );
    U16_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s66_valid,
        p0_in_valid => s26_data(8),
        p1_out_last => s57_last,
        p0_out_ready => s35_ready,
        p0_in_last => s25_data(8),
        reset => reset,
        p1_in_ready => s67_data(8),
        p1_out_data => s48_data,
        p0_in_data => s24_data(8)
    );
    U17_vectorjoin: vectorjoin_66 port map(
        clk => clk,
        p4_out_last => s69_last,
        p4_out_valid => s70_valid,
        p1_in_data(0) => s49_last,
        p1_in_data(1) => s50_last,
        p1_in_data(2) => s51_last,
        p1_in_data(3) => s52_last,
        p1_in_data(4) => s53_last,
        p1_in_data(5) => s54_last,
        p1_in_data(6) => s55_last,
        p1_in_data(7) => s56_last,
        p1_in_data(8) => s57_last,
        p4_in_ready => s71_ready,
        p2_in_data(0) => s58_valid,
        p2_in_data(1) => s59_valid,
        p2_in_data(2) => s60_valid,
        p2_in_data(3) => s61_valid,
        p2_in_data(4) => s62_valid,
        p2_in_data(5) => s63_valid,
        p2_in_data(6) => s64_valid,
        p2_in_data(7) => s65_valid,
        p2_in_data(8) => s66_valid,
        reset => reset,
        p0_in_data(0) => s40_data,
        p0_in_data(1) => s41_data,
        p0_in_data(2) => s42_data,
        p0_in_data(3) => s43_data,
        p0_in_data(4) => s44_data,
        p0_in_data(5) => s45_data,
        p0_in_data(6) => s46_data,
        p0_in_data(7) => s47_data,
        p0_in_data(8) => s48_data,
        p4_out_data => s68_data,
        p3_out_data => s67_data
    );
    U18_slidevector: slidevector_67 port map(
        clk => clk,
        p1_out_valid => s78_valid,
        p0_in_valid => s70_valid,
        p1_out_last => s77_last,
        p0_out_ready => s71_ready,
        p0_in_last => s69_last,
        reset => reset,
        p1_in_ready => s79_ready,
        p0_in_data => s68_data,
        p1_out_data => s76_data
    );
    U19_vectorfork: vectorfork_152 port map(
        clk => clk,
        p0_in_valid => s78_valid,
        p3_out_data => s74_data,
        p0_in_data => s76_data,
        p0_out_ready => s79_ready,
        p4_in_data(0) => s75_ready,
        p2_out_data => s73_data,
        p0_in_last => s77_last,
        reset => reset,
        p1_out_data => s72_data
    );
    U20_mapvector_function: mapvector_function_68 port map(
        p0_out_last => s81_last,
        clk => clk,
        p1_in_last => s73_data(0),
        p0_in_ready => s83_data(0),
        p0_out_valid => s82_valid,
        p1_in_valid => s74_data(0),
        reset => reset,
        p0_out_data => s80_data,
        p1_in_data => s72_data(0),
        p1_out_ready => s75_ready
    );
    U21_vectorjoin: vectorjoin_153 port map(
        clk => clk,
        p4_out_data => s84_data,
        p4_out_last => s85_last,
        p1_in_data(0) => s81_last,
        p4_out_valid => s86_valid,
        p0_in_data(0) => s80_data,
        p2_in_data(0) => s82_valid,
        p3_out_data => s83_data,
        p4_in_ready => s87_ready,
        reset => reset
    );
    U22_conversion: conversion_154 port map(
        clk => clk,
        p1_out_valid => s90_valid,
        p0_in_valid => s86_valid,
        p0_in_data => s84_data,
        p1_out_last => s89_last,
        p1_out_data => s88_data,
        p0_out_ready => s87_ready,
        p0_in_last => s85_last,
        reset => reset,
        p1_in_ready => s91_ready
    );
    U23_joinvector: joinvector_155 port map(
        clk => clk,
        p1_out_valid => s94_valid,
        p0_in_valid => s90_valid,
        p1_out_last => s93_last,
        p0_in_data => s88_data,
        p0_out_ready => s91_ready,
        p0_in_last => s89_last,
        reset => reset,
        p1_in_ready => s95_ready,
        p1_out_data => s92_data
    );
    U24_conversion: conversion_156 port map(
        clk => clk,
        p1_out_valid => s98_valid,
        p0_in_valid => s94_valid,
        p1_out_last => s97_last,
        p0_out_ready => s95_ready,
        p0_in_last => s93_last,
        reset => reset,
        p1_in_ready => s99_ready,
        p1_out_data => s96_data,
        p0_in_data => s92_data
    );
    U25_resizelowinteger: resizelowinteger_157 port map(
        clk => clk,
        p1_out_valid => s102_valid,
        p0_in_valid => s98_valid,
        p1_out_last => s101_last,
        p0_in_data => s96_data,
        p1_out_data => s100_data,
        p0_out_ready => s99_ready,
        p0_in_last => s97_last,
        reset => reset,
        p1_in_ready => s103_ready
    );
    U26_resizeinteger: resizeinteger_158 port map(
        p0_in_data => s100_data,
        clk => clk,
        p1_out_valid => s118_valid,
        p0_in_valid => s102_valid,
        p1_out_last => s117_last,
        p0_out_ready => s103_ready,
        p0_in_last => s101_last,
        reset => reset,
        p1_in_ready => s119_ready,
        p1_out_data => s116_data
    );
    U27_id: id_159 port map(
        clk => clk,
        p1_out_valid => s106_valid,
        p0_in_valid => s126_data(1),
        p0_in_data => s124_data(1),
        p1_out_last => s105_last,
        p1_out_data => s104_data,
        p0_out_ready => s128_ready,
        p0_in_last => s125_data(1),
        reset => reset,
        p1_in_ready => s107_ready
    );
    U28_joinvector: joinvector_160 port map(
        clk => clk,
        p1_out_valid => s110_valid,
        p0_in_valid => s106_valid,
        p0_in_data => s104_data,
        p1_out_last => s109_last,
        p1_out_data => s108_data,
        p0_out_ready => s107_ready,
        p0_in_last => s105_last,
        reset => reset,
        p1_in_ready => s111_ready
    );
    U29_vectortotuple: vectortotuple_161 port map(
        clk => clk,
        p1_out_valid => s114_valid,
        p0_in_valid => s110_valid,
        p1_out_last => s113_last,
        p0_out_ready => s111_ready,
        p1_out_data => s112_data,
        p0_in_last => s109_last,
        reset => reset,
        p1_in_ready => s115_ready,
        p0_in_data => s108_data
    );
    U30_select: select_162 port map(
        clk => clk,
        p1_out_valid => s122_valid,
        p0_in_valid => s114_valid,
        p1_out_last => s121_last,
        p0_in_data => s112_data,
        p0_out_ready => s115_ready,
        p0_in_last => s113_last,
        reset => reset,
        p1_in_ready => s123_ready,
        p1_out_data => s120_data
    );
    U31_tuple: tuple_163 port map(
        clk => clk,
        p2_out_valid => s135_valid,
        p1_in_last => s121_last,
        p0_in_valid => s118_valid,
        p2_out_data => s133_data,
        p0_in_data => s116_data,
        p2_out_last => s134_last,
        p0_out_ready => s119_ready,
        p1_in_valid => s122_valid,
        p2_in_ready => s136_ready,
        p1_in_data => s120_data,
        p0_in_last => s117_last,
        reset => reset,
        p1_out_ready => s123_ready
    );
    U32_distributor: distributor_164 port map(
        clk => clk,
        p0_in_valid => s131_valid,
        p0_in_data => s129_data,
        p1_out_data => s124_data,
        p4_in_data(0) => s127_ready,
        p4_in_data(1) => s128_ready,
        p0_out_ready => s132_ready,
        p2_out_data => s125_data,
        p0_in_last => s130_last,
        reset => reset,
        p3_out_data => s126_data
    );
    U33_id: id_165 port map(
        clk => clk,
        p1_out_valid => s131_valid,
        p0_in_valid => s139_valid,
        p0_in_data => s137_data,
        p1_out_last => s130_last,
        p1_out_data => s129_data,
        p0_out_ready => s140_ready,
        p0_in_last => s138_last,
        reset => reset,
        p1_in_ready => s132_ready
    );
    p1_out_data <= s133_data;
    p1_out_last <= "1" & s134_last when out_counter = stream_length - 1 else "0" & s134_last;
    p1_out_valid <= s135_valid;
    s136_ready <= p1_in_ready(s136_ready'high downto s136_ready'low);
    
    s137_data <= p0_in_data;
    s138_last <= p0_in_last(s138_last'high downto s138_last'low);
    s139_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s140_ready when in_counter = stream_length - 1 and s140_ready(s140_ready'high) = '1' else "0" & s140_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s139_valid = '1' and s140_ready(s140_ready'high) = '1' then
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
                if s135_valid = '1' and s136_ready(s136_ready'high) = '1' then
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
