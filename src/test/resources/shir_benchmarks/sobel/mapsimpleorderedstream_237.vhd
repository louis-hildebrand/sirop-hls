library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_237 is
    generic(
        stream_length: type_NaturalNumberType := 1918
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeVectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3ArithType1918;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType1918;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end mapsimpleorderedstream_237;

architecture behavioral of mapsimpleorderedstream_237 is
    
    component slidevector_160
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType9ArithType1;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_114
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapvector_function_10
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType64;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorfork_158
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType9;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType9;
            p3_out_data: out type_VectorTypeLogicTypeArithType9;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType9
        );
    end component;
    component tuple_8
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType9;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0VectorTypeSignedIntTypeArithType32ArithType9TextTypet1VectorTypeSignedIntTypeArithType32ArithType9_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_111
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType1;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeLogicTypeArithType32ArithType1;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component joinvector_112
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
    component id_236
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component zipvector_120
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0VectorTypeSignedIntTypeArithType32ArithType9TextTypet1VectorTypeSignedIntTypeArithType32ArithType9_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapvector_function_121
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component zipvector_9
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0VectorTypeSignedIntTypeArithType32ArithType9TextTypet1VectorTypeSignedIntTypeArithType32ArithType9_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_3
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_119
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType9;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0VectorTypeSignedIntTypeArithType32ArithType9TextTypet1VectorTypeSignedIntTypeArithType32ArithType9_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component distributor_235
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3ArithType2;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType2;
            p3_out_data: out type_VectorTypeLogicTypeArithType2;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType2
        );
    end component;
    component mapvector_function_41
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_SignedIntTypeArithType32;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0;
            p1_in_data: in type_VectorTypeSignedIntTypeArithType64ArithType9;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0
        );
    end component;
    component slidevector_40
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType64ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeSignedIntTypeArithType64ArithType9ArithType1;
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
            p0_in_data: in type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeSignedIntTypeArithType32ArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorjoin_39
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType64ArithType9;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType9;
            p2_in_data: in type_VectorTypeLogicTypeArithType9;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType9;
            p4_out_data: out type_VectorTypeSignedIntTypeArithType64ArithType9;
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
    component vectorfork_109
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeSignedIntTypeArithType64ArithType9ArithType1;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeSignedIntTypeArithType64ArithType9ArithType1;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType1;
            p3_out_data: out type_VectorTypeLogicTypeArithType1;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType1
        );
    end component;
    component conversion_118
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeLogicTypeArithType32ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeSignedIntTypeArithType32ArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component slidevector_117
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
    component vectorfork_38
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType9;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType9;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType9;
            p3_out_data: out type_VectorTypeLogicTypeArithType9;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType9
        );
    end component;
    component constantbitvector_116
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_VectorTypeLogicTypeArithType288;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorjoin_159
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType9;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType9;
            p2_in_data: in type_VectorTypeLogicTypeArithType9;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType9;
            p4_out_data: out type_VectorTypeSignedIntTypeArithType32ArithType9;
            p4_out_last: out type_LastVectorTypeArithType0;
            p4_out_valid: out type_LogicType;
            p4_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_113
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
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
            p1_out_data: out type_VectorTypeSignedIntTypeArithType32ArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorjoin_230
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType1;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType1;
            p2_in_data: in type_VectorTypeLogicTypeArithType1;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType1;
            p4_out_data: out type_VectorTypeSignedIntTypeArithType32ArithType1;
            p4_out_last: out type_LastVectorTypeArithType0;
            p4_out_valid: out type_LogicType;
            p4_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component joinvector_115
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeSignedIntTypeArithType32ArithType9;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_234
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_SignedIntTypeArithType32;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component joinvector_232
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
    component mapvector_function_161
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_SignedIntTypeArithType32;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0;
            p1_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType9;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorjoin_110
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType1;
            p1_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType1;
            p2_in_data: in type_VectorTypeLogicTypeArithType1;
            p3_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType1;
            p4_out_data: out type_VectorTypeSignedIntTypeArithType32ArithType1;
            p4_out_last: out type_LastVectorTypeArithType0;
            p4_out_valid: out type_LogicType;
            p4_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_231
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType1;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeLogicTypeArithType32ArithType1;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component vectorfork_229
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType9ArithType1;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType9ArithType1;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType1;
            p3_out_data: out type_VectorTypeLogicTypeArithType1;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType1
        );
    end component;
    component conversion_233
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3;
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
    signal s100_data: type_VectorTypeLogicTypeArithType288;
    signal s101_last: type_LastVectorTypeArithType0;
    signal s102_valid: type_LogicType;
    signal s103_ready: type_ReadyVectorTypeArithType0;
    signal s104_data: type_VectorTypeVectorTypeLogicTypeArithType32ArithType9;
    signal s105_last: type_LastVectorTypeArithType0;
    signal s106_valid: type_LogicType;
    signal s107_ready: type_ReadyVectorTypeArithType0;
    signal s108_data: type_VectorTypeSignedIntTypeArithType32ArithType9;
    signal s109_last: type_LastVectorTypeArithType0;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s110_valid: type_LogicType;
    signal s111_ready: type_ReadyVectorTypeArithType0;
    signal s112_data: type_VectorTypeSignedIntTypeArithType32ArithType9;
    signal s113_last: type_LastVectorTypeArithType0;
    signal s114_valid: type_LogicType;
    signal s115_ready: type_ReadyVectorTypeArithType0;
    signal s116_data: type_NamedTupleTypeTextTypet0VectorTypeSignedIntTypeArithType32ArithType9TextTypet1VectorTypeSignedIntTypeArithType32ArithType9_t0;
    signal s117_last: type_LastVectorTypeArithType0;
    signal s118_valid: type_LogicType;
    signal s119_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_VectorTypeSignedIntTypeArithType32ArithType9;
    signal s120_data: type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType9;
    signal s121_data: type_VectorTypeLastVectorTypeArithType0ArithType9;
    signal s122_data: type_VectorTypeLogicTypeArithType9;
    signal s123_ready: type_ReadyVectorTypeArithType0;
    signal s124_ready: type_ReadyVectorTypeArithType0;
    signal s125_ready: type_ReadyVectorTypeArithType0;
    signal s126_ready: type_ReadyVectorTypeArithType0;
    signal s127_ready: type_ReadyVectorTypeArithType0;
    signal s128_ready: type_ReadyVectorTypeArithType0;
    signal s129_ready: type_ReadyVectorTypeArithType0;
    signal s13_last: type_LastVectorTypeArithType0;
    signal s130_ready: type_ReadyVectorTypeArithType0;
    signal s131_ready: type_ReadyVectorTypeArithType0;
    signal s132_data: type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType9;
    signal s133_last: type_LastVectorTypeArithType0;
    signal s134_valid: type_LogicType;
    signal s135_ready: type_ReadyVectorTypeArithType0;
    signal s136_data: type_SignedIntTypeArithType32;
    signal s137_data: type_SignedIntTypeArithType32;
    signal s138_data: type_SignedIntTypeArithType32;
    signal s139_data: type_SignedIntTypeArithType32;
    signal s14_valid: type_LogicType;
    signal s140_data: type_SignedIntTypeArithType32;
    signal s141_data: type_SignedIntTypeArithType32;
    signal s142_data: type_SignedIntTypeArithType32;
    signal s143_data: type_SignedIntTypeArithType32;
    signal s144_data: type_SignedIntTypeArithType32;
    signal s145_last: type_LastVectorTypeArithType0;
    signal s146_last: type_LastVectorTypeArithType0;
    signal s147_last: type_LastVectorTypeArithType0;
    signal s148_last: type_LastVectorTypeArithType0;
    signal s149_last: type_LastVectorTypeArithType0;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s150_last: type_LastVectorTypeArithType0;
    signal s151_last: type_LastVectorTypeArithType0;
    signal s152_last: type_LastVectorTypeArithType0;
    signal s153_last: type_LastVectorTypeArithType0;
    signal s154_valid: type_LogicType;
    signal s155_valid: type_LogicType;
    signal s156_valid: type_LogicType;
    signal s157_valid: type_LogicType;
    signal s158_valid: type_LogicType;
    signal s159_valid: type_LogicType;
    signal s16_data: type_VectorTypeSignedIntTypeArithType32ArithType9;
    signal s160_valid: type_LogicType;
    signal s161_valid: type_LogicType;
    signal s162_valid: type_LogicType;
    signal s163_data: type_VectorTypeReadyVectorTypeArithType0ArithType9;
    signal s164_data: type_VectorTypeSignedIntTypeArithType32ArithType9;
    signal s165_last: type_LastVectorTypeArithType0;
    signal s166_valid: type_LogicType;
    signal s167_ready: type_ReadyVectorTypeArithType0;
    signal s168_data: type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType9ArithType1;
    signal s169_data: type_VectorTypeLastVectorTypeArithType0ArithType1;
    signal s17_last: type_LastVectorTypeArithType0;
    signal s170_data: type_VectorTypeLogicTypeArithType1;
    signal s171_ready: type_ReadyVectorTypeArithType0;
    signal s172_data: type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType9ArithType1;
    signal s173_last: type_LastVectorTypeArithType0;
    signal s174_valid: type_LogicType;
    signal s175_ready: type_ReadyVectorTypeArithType0;
    signal s176_data: type_SignedIntTypeArithType32;
    signal s177_last: type_LastVectorTypeArithType0;
    signal s178_valid: type_LogicType;
    signal s179_data: type_VectorTypeReadyVectorTypeArithType0ArithType1;
    signal s18_valid: type_LogicType;
    signal s180_data: type_VectorTypeSignedIntTypeArithType32ArithType1;
    signal s181_last: type_LastVectorTypeArithType0;
    signal s182_valid: type_LogicType;
    signal s183_ready: type_ReadyVectorTypeArithType0;
    signal s184_data: type_VectorTypeVectorTypeLogicTypeArithType32ArithType1;
    signal s185_last: type_LastVectorTypeArithType0;
    signal s186_valid: type_LogicType;
    signal s187_ready: type_ReadyVectorTypeArithType0;
    signal s188_data: type_VectorTypeLogicTypeArithType32;
    signal s189_last: type_LastVectorTypeArithType0;
    signal s19_ready: type_ReadyVectorTypeArithType0;
    signal s190_valid: type_LogicType;
    signal s191_ready: type_ReadyVectorTypeArithType0;
    signal s192_data: type_SignedIntTypeArithType32;
    signal s193_last: type_LastVectorTypeArithType0;
    signal s194_valid: type_LogicType;
    signal s195_ready: type_ReadyVectorTypeArithType0;
    signal s196_data: type_SignedIntTypeArithType32;
    signal s197_last: type_LastVectorTypeArithType0;
    signal s198_valid: type_LogicType;
    signal s199_ready: type_ReadyVectorTypeArithType0;
    signal s20_data: type_NamedTupleTypeTextTypet0VectorTypeSignedIntTypeArithType32ArithType9TextTypet1VectorTypeSignedIntTypeArithType32ArithType9_t0;
    signal s200_data: type_VectorTypeVectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3ArithType2;
    signal s201_data: type_VectorTypeLastVectorTypeArithType0ArithType2;
    signal s202_data: type_VectorTypeLogicTypeArithType2;
    signal s203_ready: type_ReadyVectorTypeArithType0;
    signal s204_ready: type_ReadyVectorTypeArithType0;
    signal s205_data: type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3;
    signal s206_last: type_LastVectorTypeArithType0;
    signal s207_valid: type_LogicType;
    signal s208_ready: type_ReadyVectorTypeArithType0;
    signal s209_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s21_last: type_LastVectorTypeArithType0;
    signal s210_last: type_LastVectorTypeArithType0;
    signal s211_valid: type_LogicType;
    signal s212_ready: type_ReadyVectorTypeArithType0;
    signal s213_data: type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3;
    signal s214_last: type_LastVectorTypeArithType0;
    signal s215_valid: type_LogicType;
    signal s216_ready: type_ReadyVectorTypeArithType0;
    signal s217_data: type_LastVectorTypeArithType1;
    signal s218_data: type_ReadyVectorTypeArithType1;
    signal s22_valid: type_LogicType;
    signal s23_ready: type_ReadyVectorTypeArithType0;
    signal s24_data: type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType9;
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
    signal s36_data: type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32ArithType9;
    signal s37_last: type_LastVectorTypeArithType0;
    signal s38_valid: type_LogicType;
    signal s39_ready: type_ReadyVectorTypeArithType0;
    signal s40_data: type_SignedIntTypeArithType64;
    signal s41_data: type_SignedIntTypeArithType64;
    signal s42_data: type_SignedIntTypeArithType64;
    signal s43_data: type_SignedIntTypeArithType64;
    signal s44_data: type_SignedIntTypeArithType64;
    signal s45_data: type_SignedIntTypeArithType64;
    signal s46_data: type_SignedIntTypeArithType64;
    signal s47_data: type_SignedIntTypeArithType64;
    signal s48_data: type_SignedIntTypeArithType64;
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
    signal s68_data: type_VectorTypeSignedIntTypeArithType64ArithType9;
    signal s69_last: type_LastVectorTypeArithType0;
    signal s70_valid: type_LogicType;
    signal s71_ready: type_ReadyVectorTypeArithType0;
    signal s72_data: type_VectorTypeVectorTypeSignedIntTypeArithType64ArithType9ArithType1;
    signal s73_data: type_VectorTypeLastVectorTypeArithType0ArithType1;
    signal s74_data: type_VectorTypeLogicTypeArithType1;
    signal s75_ready: type_ReadyVectorTypeArithType0;
    signal s76_data: type_VectorTypeVectorTypeSignedIntTypeArithType64ArithType9ArithType1;
    signal s77_last: type_LastVectorTypeArithType0;
    signal s78_valid: type_LogicType;
    signal s79_ready: type_ReadyVectorTypeArithType0;
    signal s80_data: type_SignedIntTypeArithType32;
    signal s81_last: type_LastVectorTypeArithType0;
    signal s82_valid: type_LogicType;
    signal s83_data: type_VectorTypeReadyVectorTypeArithType0ArithType1;
    signal s84_data: type_VectorTypeSignedIntTypeArithType32ArithType1;
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
    signal s96_data: type_VectorTypeVectorTypeSignedIntTypeArithType32ArithType3ArithType3;
    signal s97_last: type_LastVectorTypeArithType0;
    signal s98_valid: type_LogicType;
    signal s99_ready: type_ReadyVectorTypeArithType0;
begin
    
    U0_id: id_3 port map(
        clk => clk,
        p1_out_valid => s02_valid,
        p0_in_valid => s202_data(0),
        p0_in_data => s200_data(0),
        p1_out_last => s01_last,
        p1_out_data => s00_data,
        p0_out_ready => s203_ready,
        p0_in_last => s201_data(0),
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
    U7_vectorfork: vectorfork_38 port map(
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
        p1_out_data => s40_data,
        p1_out_last => s49_last,
        p0_out_ready => s27_ready,
        p0_in_last => s25_data(0),
        reset => reset,
        p1_in_ready => s67_data(0),
        p0_in_data => s24_data(0)
    );
    U9_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s59_valid,
        p0_in_valid => s26_data(1),
        p1_out_data => s41_data,
        p1_out_last => s50_last,
        p0_out_ready => s28_ready,
        p0_in_last => s25_data(1),
        reset => reset,
        p1_in_ready => s67_data(1),
        p0_in_data => s24_data(1)
    );
    U10_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s60_valid,
        p0_in_valid => s26_data(2),
        p1_out_data => s42_data,
        p1_out_last => s51_last,
        p0_out_ready => s29_ready,
        p0_in_last => s25_data(2),
        reset => reset,
        p1_in_ready => s67_data(2),
        p0_in_data => s24_data(2)
    );
    U11_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s61_valid,
        p0_in_valid => s26_data(3),
        p1_out_data => s43_data,
        p1_out_last => s52_last,
        p0_out_ready => s30_ready,
        p0_in_last => s25_data(3),
        reset => reset,
        p1_in_ready => s67_data(3),
        p0_in_data => s24_data(3)
    );
    U12_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s62_valid,
        p0_in_valid => s26_data(4),
        p1_out_data => s44_data,
        p1_out_last => s53_last,
        p0_out_ready => s31_ready,
        p0_in_last => s25_data(4),
        reset => reset,
        p1_in_ready => s67_data(4),
        p0_in_data => s24_data(4)
    );
    U13_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s63_valid,
        p0_in_valid => s26_data(5),
        p1_out_data => s45_data,
        p1_out_last => s54_last,
        p0_out_ready => s32_ready,
        p0_in_last => s25_data(5),
        reset => reset,
        p1_in_ready => s67_data(5),
        p0_in_data => s24_data(5)
    );
    U14_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s64_valid,
        p0_in_valid => s26_data(6),
        p1_out_data => s46_data,
        p1_out_last => s55_last,
        p0_out_ready => s33_ready,
        p0_in_last => s25_data(6),
        reset => reset,
        p1_in_ready => s67_data(6),
        p0_in_data => s24_data(6)
    );
    U15_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s65_valid,
        p0_in_valid => s26_data(7),
        p1_out_data => s47_data,
        p1_out_last => s56_last,
        p0_out_ready => s34_ready,
        p0_in_last => s25_data(7),
        reset => reset,
        p1_in_ready => s67_data(7),
        p0_in_data => s24_data(7)
    );
    U16_mapvector_function: mapvector_function_10 port map(
        clk => clk,
        p1_out_valid => s66_valid,
        p0_in_valid => s26_data(8),
        p1_out_data => s48_data,
        p1_out_last => s57_last,
        p0_out_ready => s35_ready,
        p0_in_last => s25_data(8),
        reset => reset,
        p1_in_ready => s67_data(8),
        p0_in_data => s24_data(8)
    );
    U17_vectorjoin: vectorjoin_39 port map(
        clk => clk,
        p4_out_last => s69_last,
        p4_out_valid => s70_valid,
        p4_out_data => s68_data,
        p0_in_data(0) => s40_data,
        p0_in_data(1) => s41_data,
        p0_in_data(2) => s42_data,
        p0_in_data(3) => s43_data,
        p0_in_data(4) => s44_data,
        p0_in_data(5) => s45_data,
        p0_in_data(6) => s46_data,
        p0_in_data(7) => s47_data,
        p0_in_data(8) => s48_data,
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
        p3_out_data => s67_data
    );
    U18_slidevector: slidevector_40 port map(
        clk => clk,
        p1_out_valid => s78_valid,
        p0_in_valid => s70_valid,
        p0_in_data => s68_data,
        p1_out_last => s77_last,
        p0_out_ready => s71_ready,
        p0_in_last => s69_last,
        reset => reset,
        p1_in_ready => s79_ready,
        p1_out_data => s76_data
    );
    U19_vectorfork: vectorfork_109 port map(
        clk => clk,
        p0_in_valid => s78_valid,
        p3_out_data => s74_data,
        p0_out_ready => s79_ready,
        p4_in_data(0) => s75_ready,
        p2_out_data => s73_data,
        p0_in_last => s77_last,
        reset => reset,
        p1_out_data => s72_data,
        p0_in_data => s76_data
    );
    U20_mapvector_function: mapvector_function_41 port map(
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
    U21_vectorjoin: vectorjoin_110 port map(
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
    U22_conversion: conversion_111 port map(
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
    U23_joinvector: joinvector_112 port map(
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
    U24_conversion: conversion_113 port map(
        clk => clk,
        p1_out_valid => s194_valid,
        p0_in_valid => s94_valid,
        p1_out_last => s193_last,
        p0_out_ready => s95_ready,
        p0_in_last => s93_last,
        reset => reset,
        p1_in_ready => s195_ready,
        p1_out_data => s192_data,
        p0_in_data => s92_data
    );
    U25_id: id_114 port map(
        clk => clk,
        p1_out_valid => s98_valid,
        p0_in_valid => s202_data(1),
        p0_in_data => s200_data(1),
        p1_out_last => s97_last,
        p1_out_data => s96_data,
        p0_out_ready => s204_ready,
        p0_in_last => s201_data(1),
        reset => reset,
        p1_in_ready => s99_ready
    );
    U26_joinvector: joinvector_115 port map(
        clk => clk,
        p1_out_valid => s110_valid,
        p0_in_valid => s98_valid,
        p0_in_data => s96_data,
        p1_out_last => s109_last,
        p1_out_data => s108_data,
        p0_out_ready => s99_ready,
        p0_in_last => s97_last,
        reset => reset,
        p1_in_ready => s111_ready
    );
    U27_constantbitvector: constantbitvector_116 port map(
        p0_out_last => s101_last,
        clk => clk,
        p0_out_data => s100_data,
        p0_in_ready => s103_ready,
        p0_out_valid => s102_valid,
        reset => reset
    );
    U28_slidevector: slidevector_117 port map(
        clk => clk,
        p1_out_valid => s106_valid,
        p0_in_valid => s102_valid,
        p0_in_data => s100_data,
        p1_out_data => s104_data,
        p1_out_last => s105_last,
        p0_out_ready => s103_ready,
        p0_in_last => s101_last,
        reset => reset,
        p1_in_ready => s107_ready
    );
    U29_conversion: conversion_118 port map(
        clk => clk,
        p1_out_valid => s114_valid,
        p0_in_valid => s106_valid,
        p0_in_data => s104_data,
        p1_out_last => s113_last,
        p1_out_data => s112_data,
        p0_out_ready => s107_ready,
        p0_in_last => s105_last,
        reset => reset,
        p1_in_ready => s115_ready
    );
    U30_tuple: tuple_119 port map(
        clk => clk,
        p2_out_valid => s118_valid,
        p2_out_data => s116_data,
        p1_in_last => s113_last,
        p0_in_valid => s110_valid,
        p2_out_last => s117_last,
        p0_out_ready => s111_ready,
        p1_in_valid => s114_valid,
        p2_in_ready => s119_ready,
        p0_in_last => s109_last,
        reset => reset,
        p0_in_data => s108_data,
        p1_in_data => s112_data,
        p1_out_ready => s115_ready
    );
    U31_zipvector: zipvector_120 port map(
        clk => clk,
        p1_out_valid => s134_valid,
        p0_in_valid => s118_valid,
        p0_in_data => s116_data,
        p1_out_last => s133_last,
        p0_out_ready => s119_ready,
        p1_out_data => s132_data,
        p0_in_last => s117_last,
        reset => reset,
        p1_in_ready => s135_ready
    );
    U32_vectorfork: vectorfork_158 port map(
        p3_out_data => s122_data,
        clk => clk,
        p0_in_valid => s134_valid,
        p0_in_data => s132_data,
        p2_out_data => s121_data,
        p0_out_ready => s135_ready,
        p1_out_data => s120_data,
        p0_in_last => s133_last,
        reset => reset,
        p4_in_data(0) => s123_ready,
        p4_in_data(1) => s124_ready,
        p4_in_data(2) => s125_ready,
        p4_in_data(3) => s126_ready,
        p4_in_data(4) => s127_ready,
        p4_in_data(5) => s128_ready,
        p4_in_data(6) => s129_ready,
        p4_in_data(7) => s130_ready,
        p4_in_data(8) => s131_ready
    );
    U33_mapvector_function: mapvector_function_121 port map(
        clk => clk,
        p1_out_valid => s154_valid,
        p0_in_valid => s122_data(0),
        p1_out_last => s145_last,
        p0_out_ready => s123_ready,
        p0_in_last => s121_data(0),
        reset => reset,
        p1_in_ready => s163_data(0),
        p1_out_data => s136_data,
        p0_in_data => s120_data(0)
    );
    U34_mapvector_function: mapvector_function_121 port map(
        clk => clk,
        p1_out_valid => s155_valid,
        p0_in_valid => s122_data(1),
        p1_out_last => s146_last,
        p0_out_ready => s124_ready,
        p0_in_last => s121_data(1),
        reset => reset,
        p1_in_ready => s163_data(1),
        p1_out_data => s137_data,
        p0_in_data => s120_data(1)
    );
    U35_mapvector_function: mapvector_function_121 port map(
        clk => clk,
        p1_out_valid => s156_valid,
        p0_in_valid => s122_data(2),
        p1_out_last => s147_last,
        p0_out_ready => s125_ready,
        p0_in_last => s121_data(2),
        reset => reset,
        p1_in_ready => s163_data(2),
        p1_out_data => s138_data,
        p0_in_data => s120_data(2)
    );
    U36_mapvector_function: mapvector_function_121 port map(
        clk => clk,
        p1_out_valid => s157_valid,
        p0_in_valid => s122_data(3),
        p1_out_last => s148_last,
        p0_out_ready => s126_ready,
        p0_in_last => s121_data(3),
        reset => reset,
        p1_in_ready => s163_data(3),
        p1_out_data => s139_data,
        p0_in_data => s120_data(3)
    );
    U37_mapvector_function: mapvector_function_121 port map(
        clk => clk,
        p1_out_valid => s158_valid,
        p0_in_valid => s122_data(4),
        p1_out_last => s149_last,
        p0_out_ready => s127_ready,
        p0_in_last => s121_data(4),
        reset => reset,
        p1_in_ready => s163_data(4),
        p1_out_data => s140_data,
        p0_in_data => s120_data(4)
    );
    U38_mapvector_function: mapvector_function_121 port map(
        clk => clk,
        p1_out_valid => s159_valid,
        p0_in_valid => s122_data(5),
        p1_out_last => s150_last,
        p0_out_ready => s128_ready,
        p0_in_last => s121_data(5),
        reset => reset,
        p1_in_ready => s163_data(5),
        p1_out_data => s141_data,
        p0_in_data => s120_data(5)
    );
    U39_mapvector_function: mapvector_function_121 port map(
        clk => clk,
        p1_out_valid => s160_valid,
        p0_in_valid => s122_data(6),
        p1_out_last => s151_last,
        p0_out_ready => s129_ready,
        p0_in_last => s121_data(6),
        reset => reset,
        p1_in_ready => s163_data(6),
        p1_out_data => s142_data,
        p0_in_data => s120_data(6)
    );
    U40_mapvector_function: mapvector_function_121 port map(
        clk => clk,
        p1_out_valid => s161_valid,
        p0_in_valid => s122_data(7),
        p1_out_last => s152_last,
        p0_out_ready => s130_ready,
        p0_in_last => s121_data(7),
        reset => reset,
        p1_in_ready => s163_data(7),
        p1_out_data => s143_data,
        p0_in_data => s120_data(7)
    );
    U41_mapvector_function: mapvector_function_121 port map(
        clk => clk,
        p1_out_valid => s162_valid,
        p0_in_valid => s122_data(8),
        p1_out_last => s153_last,
        p0_out_ready => s131_ready,
        p0_in_last => s121_data(8),
        reset => reset,
        p1_in_ready => s163_data(8),
        p1_out_data => s144_data,
        p0_in_data => s120_data(8)
    );
    U42_vectorjoin: vectorjoin_159 port map(
        clk => clk,
        p4_out_last => s165_last,
        p4_out_valid => s166_valid,
        p1_in_data(0) => s145_last,
        p1_in_data(1) => s146_last,
        p1_in_data(2) => s147_last,
        p1_in_data(3) => s148_last,
        p1_in_data(4) => s149_last,
        p1_in_data(5) => s150_last,
        p1_in_data(6) => s151_last,
        p1_in_data(7) => s152_last,
        p1_in_data(8) => s153_last,
        p4_in_ready => s167_ready,
        p2_in_data(0) => s154_valid,
        p2_in_data(1) => s155_valid,
        p2_in_data(2) => s156_valid,
        p2_in_data(3) => s157_valid,
        p2_in_data(4) => s158_valid,
        p2_in_data(5) => s159_valid,
        p2_in_data(6) => s160_valid,
        p2_in_data(7) => s161_valid,
        p2_in_data(8) => s162_valid,
        reset => reset,
        p0_in_data(0) => s136_data,
        p0_in_data(1) => s137_data,
        p0_in_data(2) => s138_data,
        p0_in_data(3) => s139_data,
        p0_in_data(4) => s140_data,
        p0_in_data(5) => s141_data,
        p0_in_data(6) => s142_data,
        p0_in_data(7) => s143_data,
        p0_in_data(8) => s144_data,
        p4_out_data => s164_data,
        p3_out_data => s163_data
    );
    U43_slidevector: slidevector_160 port map(
        clk => clk,
        p1_out_valid => s174_valid,
        p0_in_valid => s166_valid,
        p1_out_last => s173_last,
        p0_out_ready => s167_ready,
        p0_in_last => s165_last,
        reset => reset,
        p1_in_ready => s175_ready,
        p0_in_data => s164_data,
        p1_out_data => s172_data
    );
    U44_vectorfork: vectorfork_229 port map(
        clk => clk,
        p0_in_valid => s174_valid,
        p3_out_data => s170_data,
        p0_in_data => s172_data,
        p0_out_ready => s175_ready,
        p4_in_data(0) => s171_ready,
        p2_out_data => s169_data,
        p0_in_last => s173_last,
        reset => reset,
        p1_out_data => s168_data
    );
    U45_mapvector_function: mapvector_function_161 port map(
        p0_out_last => s177_last,
        clk => clk,
        p1_in_last => s169_data(0),
        p0_in_ready => s179_data(0),
        p0_out_valid => s178_valid,
        p1_in_valid => s170_data(0),
        reset => reset,
        p0_out_data => s176_data,
        p1_in_data => s168_data(0),
        p1_out_ready => s171_ready
    );
    U46_vectorjoin: vectorjoin_230 port map(
        clk => clk,
        p4_out_data => s180_data,
        p4_out_last => s181_last,
        p1_in_data(0) => s177_last,
        p4_out_valid => s182_valid,
        p0_in_data(0) => s176_data,
        p2_in_data(0) => s178_valid,
        p3_out_data => s179_data,
        p4_in_ready => s183_ready,
        reset => reset
    );
    U47_conversion: conversion_231 port map(
        clk => clk,
        p1_out_valid => s186_valid,
        p0_in_valid => s182_valid,
        p0_in_data => s180_data,
        p1_out_last => s185_last,
        p1_out_data => s184_data,
        p0_out_ready => s183_ready,
        p0_in_last => s181_last,
        reset => reset,
        p1_in_ready => s187_ready
    );
    U48_joinvector: joinvector_232 port map(
        clk => clk,
        p1_out_valid => s190_valid,
        p0_in_valid => s186_valid,
        p1_out_last => s189_last,
        p0_in_data => s184_data,
        p0_out_ready => s187_ready,
        p0_in_last => s185_last,
        reset => reset,
        p1_in_ready => s191_ready,
        p1_out_data => s188_data
    );
    U49_conversion: conversion_233 port map(
        clk => clk,
        p1_out_valid => s198_valid,
        p0_in_valid => s190_valid,
        p1_out_last => s197_last,
        p0_out_ready => s191_ready,
        p0_in_last => s189_last,
        reset => reset,
        p1_in_ready => s199_ready,
        p1_out_data => s196_data,
        p0_in_data => s188_data
    );
    U50_tuple: tuple_234 port map(
        clk => clk,
        p2_out_valid => s211_valid,
        p1_in_last => s197_last,
        p0_in_valid => s194_valid,
        p2_out_data => s209_data,
        p0_in_data => s192_data,
        p2_out_last => s210_last,
        p0_out_ready => s195_ready,
        p1_in_valid => s198_valid,
        p2_in_ready => s212_ready,
        p1_in_data => s196_data,
        p0_in_last => s193_last,
        reset => reset,
        p1_out_ready => s199_ready
    );
    U51_distributor: distributor_235 port map(
        clk => clk,
        p0_in_valid => s207_valid,
        p0_in_data => s205_data,
        p1_out_data => s200_data,
        p4_in_data(0) => s203_ready,
        p4_in_data(1) => s204_ready,
        p0_out_ready => s208_ready,
        p2_out_data => s201_data,
        p0_in_last => s206_last,
        reset => reset,
        p3_out_data => s202_data
    );
    U52_id: id_236 port map(
        clk => clk,
        p1_out_valid => s207_valid,
        p0_in_valid => s215_valid,
        p0_in_data => s213_data,
        p1_out_last => s206_last,
        p1_out_data => s205_data,
        p0_out_ready => s216_ready,
        p0_in_last => s214_last,
        reset => reset,
        p1_in_ready => s208_ready
    );
    p1_out_data <= s209_data;
    p1_out_last <= "1" & s210_last when out_counter = stream_length - 1 else "0" & s210_last;
    p1_out_valid <= s211_valid;
    s212_ready <= p1_in_ready(s212_ready'high downto s212_ready'low);
    
    s213_data <= p0_in_data;
    s214_last <= p0_in_last(s214_last'high downto s214_last'low);
    s215_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s216_ready when in_counter = stream_length - 1 and s216_ready(s216_ready'high) = '1' else "0" & s216_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s215_valid = '1' and s216_ready(s216_ready'high) = '1' then
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
                if s211_valid = '1' and s212_ready(s212_ready'high) = '1' then
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
