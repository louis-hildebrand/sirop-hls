library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_1090 is
    generic(
        stream_length: type_NaturalNumberType := 1918
    );
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
end mapsimpleorderedstream_1090;

architecture behavioral of mapsimpleorderedstream_1090 is
    
    component resizeinteger_1064
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType31;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component addint_1061
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType33;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component resizeinteger_1080
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType64;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component resizeinteger_1058
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType33;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_1050
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component select_1068
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_1069
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
    component distributor_1088
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType12;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType12;
            p3_out_data: out type_VectorTypeLogicTypeArithType12;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType12
        );
    end component;
    component tuple_1056
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
    component resizeinteger_1075
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType33;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_1078
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
    component resizelowinteger_1076
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType31;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_1060
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
    component select_1053
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component select_1082
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component resizelowinteger_1063
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType31;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component resizeinteger_1062
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType33;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_1073
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
    component tuple_1083
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
    component select_1066
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_1081
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_1065
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component constantvalue_1059
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_SignedIntTypeArithType32;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_1067
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component select_1055
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_1052
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component addint_1070
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType33;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component resizeinteger_1071
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType33;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component select_1051
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component constantvalue_1072
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_SignedIntTypeArithType32;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component addint_1057
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType33;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component resizeinteger_1077
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType31;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component ifelse_1087
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1IntTypeArithType1_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0;
            p2_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p2_in_last: in type_LastVectorTypeArithType0;
            p2_in_valid: in type_LogicType;
            p2_out_ready: out type_ReadyVectorTypeArithType0;
            p3_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p3_in_last: in type_LastVectorTypeArithType0;
            p3_in_valid: in type_LogicType;
            p3_out_ready: out type_ReadyVectorTypeArithType0;
            p4_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p4_in_last: in type_LastVectorTypeArithType0;
            p4_in_valid: in type_LogicType;
            p4_out_ready: out type_ReadyVectorTypeArithType0;
            p5_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p5_in_last: in type_LastVectorTypeArithType0;
            p5_in_valid: in type_LogicType;
            p5_out_ready: out type_ReadyVectorTypeArithType0;
            p6_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p6_in_last: in type_LastVectorTypeArithType0;
            p6_in_valid: in type_LogicType;
            p6_out_ready: out type_ReadyVectorTypeArithType0;
            p7_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p7_in_last: in type_LastVectorTypeArithType0;
            p7_in_valid: in type_LogicType;
            p7_out_ready: out type_ReadyVectorTypeArithType0
        );
    end component;
    component mulint_1079
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
    component id_1089
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component cmpint_1084
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_LogicType;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_1054
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component addint_1074
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_SignedIntTypeArithType33;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_1086
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_SignedIntTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_IntTypeArithType1;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1IntTypeArithType1_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component conversion_1085
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_LogicType;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType1;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s09_last: type_LastVectorTypeArithType0;
    signal s10_valid: type_LogicType;
    signal s100_data: type_SignedIntTypeArithType32;
    signal s101_last: type_LastVectorTypeArithType0;
    signal s102_valid: type_LogicType;
    signal s103_ready: type_ReadyVectorTypeArithType0;
    signal s104_data: type_SignedIntTypeArithType32;
    signal s105_last: type_LastVectorTypeArithType0;
    signal s106_valid: type_LogicType;
    signal s107_ready: type_ReadyVectorTypeArithType0;
    signal s108_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s109_last: type_LastVectorTypeArithType0;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s110_valid: type_LogicType;
    signal s111_ready: type_ReadyVectorTypeArithType0;
    signal s112_data: type_SignedIntTypeArithType64;
    signal s113_last: type_LastVectorTypeArithType0;
    signal s114_valid: type_LogicType;
    signal s115_ready: type_ReadyVectorTypeArithType0;
    signal s116_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s117_last: type_LastVectorTypeArithType0;
    signal s118_valid: type_LogicType;
    signal s119_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_SignedIntTypeArithType32;
    signal s120_data: type_SignedIntTypeArithType32;
    signal s121_last: type_LastVectorTypeArithType0;
    signal s122_valid: type_LogicType;
    signal s123_ready: type_ReadyVectorTypeArithType0;
    signal s124_data: type_SignedIntTypeArithType32;
    signal s125_last: type_LastVectorTypeArithType0;
    signal s126_valid: type_LogicType;
    signal s127_ready: type_ReadyVectorTypeArithType0;
    signal s128_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s129_last: type_LastVectorTypeArithType0;
    signal s13_last: type_LastVectorTypeArithType0;
    signal s130_valid: type_LogicType;
    signal s131_ready: type_ReadyVectorTypeArithType0;
    signal s132_data: type_LogicType;
    signal s133_last: type_LastVectorTypeArithType0;
    signal s134_valid: type_LogicType;
    signal s135_ready: type_ReadyVectorTypeArithType0;
    signal s136_data: type_SignedIntTypeArithType32;
    signal s137_last: type_LastVectorTypeArithType0;
    signal s138_valid: type_LogicType;
    signal s139_ready: type_ReadyVectorTypeArithType0;
    signal s14_valid: type_LogicType;
    signal s140_data: type_IntTypeArithType1;
    signal s141_last: type_LastVectorTypeArithType0;
    signal s142_valid: type_LogicType;
    signal s143_ready: type_ReadyVectorTypeArithType0;
    signal s144_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1IntTypeArithType1_t0;
    signal s145_last: type_LastVectorTypeArithType0;
    signal s146_valid: type_LogicType;
    signal s147_ready: type_ReadyVectorTypeArithType0;
    signal s148_data: type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType12;
    signal s149_data: type_VectorTypeLastVectorTypeArithType0ArithType12;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s150_data: type_VectorTypeLogicTypeArithType12;
    signal s151_ready: type_ReadyVectorTypeArithType0;
    signal s152_ready: type_ReadyVectorTypeArithType0;
    signal s153_ready: type_ReadyVectorTypeArithType0;
    signal s154_ready: type_ReadyVectorTypeArithType0;
    signal s155_ready: type_ReadyVectorTypeArithType0;
    signal s156_ready: type_ReadyVectorTypeArithType0;
    signal s157_ready: type_ReadyVectorTypeArithType0;
    signal s158_ready: type_ReadyVectorTypeArithType0;
    signal s159_ready: type_ReadyVectorTypeArithType0;
    signal s16_data: type_SignedIntTypeArithType32;
    signal s160_ready: type_ReadyVectorTypeArithType0;
    signal s161_ready: type_ReadyVectorTypeArithType0;
    signal s162_ready: type_ReadyVectorTypeArithType0;
    signal s163_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s164_last: type_LastVectorTypeArithType0;
    signal s165_valid: type_LogicType;
    signal s166_ready: type_ReadyVectorTypeArithType0;
    signal s167_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s168_last: type_LastVectorTypeArithType0;
    signal s169_valid: type_LogicType;
    signal s17_last: type_LastVectorTypeArithType0;
    signal s170_ready: type_ReadyVectorTypeArithType0;
    signal s171_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s172_last: type_LastVectorTypeArithType0;
    signal s173_valid: type_LogicType;
    signal s174_ready: type_ReadyVectorTypeArithType0;
    signal s175_data: type_LastVectorTypeArithType1;
    signal s176_data: type_ReadyVectorTypeArithType1;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType0;
    signal s20_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s21_last: type_LastVectorTypeArithType0;
    signal s22_valid: type_LogicType;
    signal s23_ready: type_ReadyVectorTypeArithType0;
    signal s24_data: type_SignedIntTypeArithType33;
    signal s25_last: type_LastVectorTypeArithType0;
    signal s26_valid: type_LogicType;
    signal s27_ready: type_ReadyVectorTypeArithType0;
    signal s28_data: type_SignedIntTypeArithType32;
    signal s29_last: type_LastVectorTypeArithType0;
    signal s30_valid: type_LogicType;
    signal s31_ready: type_ReadyVectorTypeArithType0;
    signal s32_data: type_SignedIntTypeArithType32;
    signal s33_last: type_LastVectorTypeArithType0;
    signal s34_valid: type_LogicType;
    signal s35_ready: type_ReadyVectorTypeArithType0;
    signal s36_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s37_last: type_LastVectorTypeArithType0;
    signal s38_valid: type_LogicType;
    signal s39_ready: type_ReadyVectorTypeArithType0;
    signal s40_data: type_SignedIntTypeArithType33;
    signal s41_last: type_LastVectorTypeArithType0;
    signal s42_valid: type_LogicType;
    signal s43_ready: type_ReadyVectorTypeArithType0;
    signal s44_data: type_SignedIntTypeArithType32;
    signal s45_last: type_LastVectorTypeArithType0;
    signal s46_valid: type_LogicType;
    signal s47_ready: type_ReadyVectorTypeArithType0;
    signal s48_data: type_SignedIntTypeArithType31;
    signal s49_last: type_LastVectorTypeArithType0;
    signal s50_valid: type_LogicType;
    signal s51_ready: type_ReadyVectorTypeArithType0;
    signal s52_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s53_last: type_LastVectorTypeArithType0;
    signal s54_valid: type_LogicType;
    signal s55_ready: type_ReadyVectorTypeArithType0;
    signal s56_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s57_last: type_LastVectorTypeArithType0;
    signal s58_valid: type_LogicType;
    signal s59_ready: type_ReadyVectorTypeArithType0;
    signal s60_data: type_SignedIntTypeArithType32;
    signal s61_last: type_LastVectorTypeArithType0;
    signal s62_valid: type_LogicType;
    signal s63_ready: type_ReadyVectorTypeArithType0;
    signal s64_data: type_SignedIntTypeArithType32;
    signal s65_last: type_LastVectorTypeArithType0;
    signal s66_valid: type_LogicType;
    signal s67_ready: type_ReadyVectorTypeArithType0;
    signal s68_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s69_last: type_LastVectorTypeArithType0;
    signal s70_valid: type_LogicType;
    signal s71_ready: type_ReadyVectorTypeArithType0;
    signal s72_data: type_SignedIntTypeArithType33;
    signal s73_last: type_LastVectorTypeArithType0;
    signal s74_valid: type_LogicType;
    signal s75_ready: type_ReadyVectorTypeArithType0;
    signal s76_data: type_SignedIntTypeArithType32;
    signal s77_last: type_LastVectorTypeArithType0;
    signal s78_valid: type_LogicType;
    signal s79_ready: type_ReadyVectorTypeArithType0;
    signal s80_data: type_SignedIntTypeArithType32;
    signal s81_last: type_LastVectorTypeArithType0;
    signal s82_valid: type_LogicType;
    signal s83_ready: type_ReadyVectorTypeArithType0;
    signal s84_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s85_last: type_LastVectorTypeArithType0;
    signal s86_valid: type_LogicType;
    signal s87_ready: type_ReadyVectorTypeArithType0;
    signal s88_data: type_SignedIntTypeArithType33;
    signal s89_last: type_LastVectorTypeArithType0;
    signal s90_valid: type_LogicType;
    signal s91_ready: type_ReadyVectorTypeArithType0;
    signal s92_data: type_SignedIntTypeArithType32;
    signal s93_last: type_LastVectorTypeArithType0;
    signal s94_valid: type_LogicType;
    signal s95_ready: type_ReadyVectorTypeArithType0;
    signal s96_data: type_SignedIntTypeArithType31;
    signal s97_last: type_LastVectorTypeArithType0;
    signal s98_valid: type_LogicType;
    signal s99_ready: type_ReadyVectorTypeArithType0;
begin
    
    U0_id: id_1050 port map(
        clk => clk,
        p1_out_valid => s02_valid,
        p0_in_valid => s150_data(0),
        p1_out_last => s01_last,
        p0_in_data => s148_data(0),
        p0_out_ready => s151_ready,
        p0_in_last => s149_data(0),
        reset => reset,
        p1_in_ready => s03_ready,
        p1_out_data => s00_data
    );
    U1_select: select_1051 port map(
        clk => clk,
        p1_out_valid => s138_valid,
        p0_in_valid => s02_valid,
        p1_out_last => s137_last,
        p0_in_data => s00_data,
        p0_out_ready => s03_ready,
        p0_in_last => s01_last,
        reset => reset,
        p1_in_ready => s139_ready,
        p1_out_data => s136_data
    );
    U2_id: id_1052 port map(
        clk => clk,
        p1_out_valid => s06_valid,
        p0_in_valid => s150_data(1),
        p1_out_last => s05_last,
        p0_in_data => s148_data(1),
        p0_out_ready => s152_ready,
        p0_in_last => s149_data(1),
        reset => reset,
        p1_in_ready => s07_ready,
        p1_out_data => s04_data
    );
    U3_select: select_1053 port map(
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => s06_valid,
        p1_out_last => s13_last,
        p0_in_data => s04_data,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => s15_ready,
        p1_out_data => s12_data
    );
    U4_id: id_1054 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s150_data(2),
        p1_out_last => s09_last,
        p0_in_data => s148_data(2),
        p0_out_ready => s153_ready,
        p0_in_last => s149_data(2),
        reset => reset,
        p1_in_ready => s11_ready,
        p1_out_data => s08_data
    );
    U5_select: select_1055 port map(
        clk => clk,
        p1_out_valid => s18_valid,
        p0_in_valid => s10_valid,
        p1_out_last => s17_last,
        p0_in_data => s08_data,
        p0_out_ready => s11_ready,
        p0_in_last => s09_last,
        reset => reset,
        p1_in_ready => s19_ready,
        p1_out_data => s16_data
    );
    U6_tuple: tuple_1056 port map(
        clk => clk,
        p2_out_valid => s22_valid,
        p1_in_last => s17_last,
        p0_in_valid => s14_valid,
        p2_out_data => s20_data,
        p0_in_data => s12_data,
        p2_out_last => s21_last,
        p0_out_ready => s15_ready,
        p1_in_valid => s18_valid,
        p2_in_ready => s23_ready,
        p1_in_data => s16_data,
        p0_in_last => s13_last,
        reset => reset,
        p1_out_ready => s19_ready
    );
    U7_addint: addint_1057 port map(
        clk => clk,
        p1_out_valid => s26_valid,
        p0_in_valid => s22_valid,
        p1_out_last => s25_last,
        p1_out_data => s24_data,
        p0_out_ready => s23_ready,
        p0_in_last => s21_last,
        reset => reset,
        p1_in_ready => s27_ready,
        p0_in_data => s20_data
    );
    U8_resizeinteger: resizeinteger_1058 port map(
        clk => clk,
        p1_out_valid => s30_valid,
        p0_in_valid => s26_valid,
        p0_in_data => s24_data,
        p1_out_last => s29_last,
        p0_out_ready => s27_ready,
        p0_in_last => s25_last,
        reset => reset,
        p1_in_ready => s31_ready,
        p1_out_data => s28_data
    );
    U9_constantvalue: constantvalue_1059 port map(
        p0_out_last => s33_last,
        clk => clk,
        p0_in_ready => s35_ready,
        p0_out_valid => s34_valid,
        reset => reset,
        p0_out_data => s32_data
    );
    U10_tuple: tuple_1060 port map(
        clk => clk,
        p2_out_valid => s38_valid,
        p1_in_last => s33_last,
        p0_in_valid => s30_valid,
        p2_out_data => s36_data,
        p0_in_data => s28_data,
        p2_out_last => s37_last,
        p0_out_ready => s31_ready,
        p1_in_valid => s34_valid,
        p2_in_ready => s39_ready,
        p1_in_data => s32_data,
        p0_in_last => s29_last,
        reset => reset,
        p1_out_ready => s35_ready
    );
    U11_addint: addint_1061 port map(
        clk => clk,
        p1_out_valid => s42_valid,
        p0_in_valid => s38_valid,
        p1_out_last => s41_last,
        p1_out_data => s40_data,
        p0_out_ready => s39_ready,
        p0_in_last => s37_last,
        reset => reset,
        p1_in_ready => s43_ready,
        p0_in_data => s36_data
    );
    U12_resizeinteger: resizeinteger_1062 port map(
        clk => clk,
        p1_out_valid => s46_valid,
        p0_in_valid => s42_valid,
        p0_in_data => s40_data,
        p1_out_last => s45_last,
        p0_out_ready => s43_ready,
        p0_in_last => s41_last,
        reset => reset,
        p1_in_ready => s47_ready,
        p1_out_data => s44_data
    );
    U13_resizelowinteger: resizelowinteger_1063 port map(
        clk => clk,
        p1_out_valid => s50_valid,
        p0_in_valid => s46_valid,
        p1_out_last => s49_last,
        p0_in_data => s44_data,
        p0_out_ready => s47_ready,
        p0_in_last => s45_last,
        reset => reset,
        p1_in_ready => s51_ready,
        p1_out_data => s48_data
    );
    U14_resizeinteger: resizeinteger_1064 port map(
        clk => clk,
        p1_out_valid => s102_valid,
        p0_in_valid => s50_valid,
        p1_out_last => s101_last,
        p0_out_ready => s51_ready,
        p0_in_last => s49_last,
        reset => reset,
        p1_in_ready => s103_ready,
        p1_out_data => s100_data,
        p0_in_data => s48_data
    );
    U15_id: id_1065 port map(
        clk => clk,
        p1_out_valid => s54_valid,
        p0_in_valid => s150_data(3),
        p1_out_last => s53_last,
        p0_in_data => s148_data(3),
        p0_out_ready => s154_ready,
        p0_in_last => s149_data(3),
        reset => reset,
        p1_in_ready => s55_ready,
        p1_out_data => s52_data
    );
    U16_select: select_1066 port map(
        clk => clk,
        p1_out_valid => s62_valid,
        p0_in_valid => s54_valid,
        p1_out_last => s61_last,
        p0_in_data => s52_data,
        p0_out_ready => s55_ready,
        p0_in_last => s53_last,
        reset => reset,
        p1_in_ready => s63_ready,
        p1_out_data => s60_data
    );
    U17_id: id_1067 port map(
        clk => clk,
        p1_out_valid => s58_valid,
        p0_in_valid => s150_data(4),
        p1_out_last => s57_last,
        p0_in_data => s148_data(4),
        p0_out_ready => s155_ready,
        p0_in_last => s149_data(4),
        reset => reset,
        p1_in_ready => s59_ready,
        p1_out_data => s56_data
    );
    U18_select: select_1068 port map(
        clk => clk,
        p1_out_valid => s66_valid,
        p0_in_valid => s58_valid,
        p1_out_last => s65_last,
        p0_in_data => s56_data,
        p0_out_ready => s59_ready,
        p0_in_last => s57_last,
        reset => reset,
        p1_in_ready => s67_ready,
        p1_out_data => s64_data
    );
    U19_tuple: tuple_1069 port map(
        clk => clk,
        p2_out_valid => s70_valid,
        p1_in_last => s65_last,
        p0_in_valid => s62_valid,
        p2_out_data => s68_data,
        p0_in_data => s60_data,
        p2_out_last => s69_last,
        p0_out_ready => s63_ready,
        p1_in_valid => s66_valid,
        p2_in_ready => s71_ready,
        p1_in_data => s64_data,
        p0_in_last => s61_last,
        reset => reset,
        p1_out_ready => s67_ready
    );
    U20_addint: addint_1070 port map(
        clk => clk,
        p1_out_valid => s74_valid,
        p0_in_valid => s70_valid,
        p1_out_last => s73_last,
        p1_out_data => s72_data,
        p0_out_ready => s71_ready,
        p0_in_last => s69_last,
        reset => reset,
        p1_in_ready => s75_ready,
        p0_in_data => s68_data
    );
    U21_resizeinteger: resizeinteger_1071 port map(
        clk => clk,
        p1_out_valid => s78_valid,
        p0_in_valid => s74_valid,
        p0_in_data => s72_data,
        p1_out_last => s77_last,
        p0_out_ready => s75_ready,
        p0_in_last => s73_last,
        reset => reset,
        p1_in_ready => s79_ready,
        p1_out_data => s76_data
    );
    U22_constantvalue: constantvalue_1072 port map(
        p0_out_last => s81_last,
        clk => clk,
        p0_in_ready => s83_ready,
        p0_out_valid => s82_valid,
        reset => reset,
        p0_out_data => s80_data
    );
    U23_tuple: tuple_1073 port map(
        clk => clk,
        p2_out_valid => s86_valid,
        p1_in_last => s81_last,
        p0_in_valid => s78_valid,
        p2_out_data => s84_data,
        p0_in_data => s76_data,
        p2_out_last => s85_last,
        p0_out_ready => s79_ready,
        p1_in_valid => s82_valid,
        p2_in_ready => s87_ready,
        p1_in_data => s80_data,
        p0_in_last => s77_last,
        reset => reset,
        p1_out_ready => s83_ready
    );
    U24_addint: addint_1074 port map(
        clk => clk,
        p1_out_valid => s90_valid,
        p0_in_valid => s86_valid,
        p1_out_last => s89_last,
        p1_out_data => s88_data,
        p0_out_ready => s87_ready,
        p0_in_last => s85_last,
        reset => reset,
        p1_in_ready => s91_ready,
        p0_in_data => s84_data
    );
    U25_resizeinteger: resizeinteger_1075 port map(
        clk => clk,
        p1_out_valid => s94_valid,
        p0_in_valid => s90_valid,
        p0_in_data => s88_data,
        p1_out_last => s93_last,
        p0_out_ready => s91_ready,
        p0_in_last => s89_last,
        reset => reset,
        p1_in_ready => s95_ready,
        p1_out_data => s92_data
    );
    U26_resizelowinteger: resizelowinteger_1076 port map(
        clk => clk,
        p1_out_valid => s98_valid,
        p0_in_valid => s94_valid,
        p1_out_last => s97_last,
        p0_in_data => s92_data,
        p0_out_ready => s95_ready,
        p0_in_last => s93_last,
        reset => reset,
        p1_in_ready => s99_ready,
        p1_out_data => s96_data
    );
    U27_resizeinteger: resizeinteger_1077 port map(
        clk => clk,
        p1_out_valid => s106_valid,
        p0_in_valid => s98_valid,
        p1_out_last => s105_last,
        p0_out_ready => s99_ready,
        p0_in_last => s97_last,
        reset => reset,
        p1_in_ready => s107_ready,
        p1_out_data => s104_data,
        p0_in_data => s96_data
    );
    U28_tuple: tuple_1078 port map(
        clk => clk,
        p2_out_valid => s110_valid,
        p1_in_last => s105_last,
        p0_in_valid => s102_valid,
        p2_out_data => s108_data,
        p0_in_data => s100_data,
        p2_out_last => s109_last,
        p0_out_ready => s103_ready,
        p1_in_valid => s106_valid,
        p2_in_ready => s111_ready,
        p1_in_data => s104_data,
        p0_in_last => s101_last,
        reset => reset,
        p1_out_ready => s107_ready
    );
    U29_mulint: mulint_1079 port map(
        clk => clk,
        p1_out_valid => s114_valid,
        p0_in_valid => s110_valid,
        p1_out_data => s112_data,
        p1_out_last => s113_last,
        p0_out_ready => s111_ready,
        p0_in_last => s109_last,
        reset => reset,
        p1_in_ready => s115_ready,
        p0_in_data => s108_data
    );
    U30_resizeinteger: resizeinteger_1080 port map(
        clk => clk,
        p0_in_data => s112_data,
        p1_out_valid => s122_valid,
        p0_in_valid => s114_valid,
        p1_out_last => s121_last,
        p0_out_ready => s115_ready,
        p0_in_last => s113_last,
        reset => reset,
        p1_in_ready => s123_ready,
        p1_out_data => s120_data
    );
    U31_id: id_1081 port map(
        clk => clk,
        p1_out_valid => s118_valid,
        p0_in_valid => s150_data(5),
        p1_out_last => s117_last,
        p0_in_data => s148_data(5),
        p0_out_ready => s156_ready,
        p0_in_last => s149_data(5),
        reset => reset,
        p1_in_ready => s119_ready,
        p1_out_data => s116_data
    );
    U32_select: select_1082 port map(
        clk => clk,
        p1_out_valid => s126_valid,
        p0_in_valid => s118_valid,
        p1_out_last => s125_last,
        p0_in_data => s116_data,
        p0_out_ready => s119_ready,
        p0_in_last => s117_last,
        reset => reset,
        p1_in_ready => s127_ready,
        p1_out_data => s124_data
    );
    U33_tuple: tuple_1083 port map(
        clk => clk,
        p2_out_valid => s130_valid,
        p1_in_last => s125_last,
        p0_in_valid => s122_valid,
        p2_out_data => s128_data,
        p0_in_data => s120_data,
        p2_out_last => s129_last,
        p0_out_ready => s123_ready,
        p1_in_valid => s126_valid,
        p2_in_ready => s131_ready,
        p1_in_data => s124_data,
        p0_in_last => s121_last,
        reset => reset,
        p1_out_ready => s127_ready
    );
    U34_cmpint: cmpint_1084 port map(
        clk => clk,
        p1_out_valid => s134_valid,
        p0_in_valid => s130_valid,
        p1_out_last => s133_last,
        p1_out_data => s132_data,
        p0_out_ready => s131_ready,
        p0_in_last => s129_last,
        reset => reset,
        p1_in_ready => s135_ready,
        p0_in_data => s128_data
    );
    U35_conversion: conversion_1085 port map(
        clk => clk,
        p1_out_valid => s142_valid,
        p0_in_valid => s134_valid,
        p1_out_last => s141_last,
        p0_out_ready => s135_ready,
        p0_in_data => s132_data,
        p0_in_last => s133_last,
        reset => reset,
        p1_in_ready => s143_ready,
        p1_out_data => s140_data
    );
    U36_tuple: tuple_1086 port map(
        clk => clk,
        p2_out_valid => s146_valid,
        p2_out_data => s144_data,
        p1_in_last => s141_last,
        p0_in_valid => s138_valid,
        p1_in_data => s140_data,
        p0_in_data => s136_data,
        p2_out_last => s145_last,
        p0_out_ready => s139_ready,
        p1_in_valid => s142_valid,
        p2_in_ready => s147_ready,
        p0_in_last => s137_last,
        reset => reset,
        p1_out_ready => s143_ready
    );
    U37_ifelse: ifelse_1087 port map(
        p4_in_data => s148_data(8),
        p3_in_valid => s150_data(7),
        clk => clk,
        p7_in_valid => s150_data(11),
        p5_out_ready => s160_ready,
        p6_in_data => s148_data(10),
        p1_out_valid => s169_valid,
        p0_in_valid => s146_valid,
        p4_in_last => s149_data(8),
        p7_in_data => s148_data(11),
        p5_in_data => s148_data(9),
        p7_in_last => s149_data(11),
        p2_out_ready => s157_ready,
        p7_out_ready => s162_ready,
        p2_in_data => s148_data(6),
        p6_in_last => s149_data(10),
        p1_out_last => s168_last,
        p6_out_ready => s161_ready,
        p0_in_data => s144_data,
        p2_in_valid => s150_data(6),
        p3_out_ready => s158_ready,
        p4_in_valid => s150_data(8),
        p0_out_ready => s147_ready,
        p5_in_last => s149_data(9),
        p6_in_valid => s150_data(10),
        p3_in_data => s148_data(7),
        p5_in_valid => s150_data(9),
        p0_in_last => s145_last,
        reset => reset,
        p2_in_last => s149_data(6),
        p1_in_ready => s170_ready,
        p4_out_ready => s159_ready,
        p3_in_last => s149_data(7),
        p1_out_data => s167_data
    );
    U38_distributor: distributor_1088 port map(
        clk => clk,
        p2_out_data => s149_data,
        p0_in_valid => s165_valid,
        p4_in_data(0) => s151_ready,
        p4_in_data(1) => s152_ready,
        p4_in_data(2) => s153_ready,
        p4_in_data(3) => s154_ready,
        p4_in_data(4) => s155_ready,
        p4_in_data(5) => s156_ready,
        p4_in_data(6) => s157_ready,
        p4_in_data(7) => s158_ready,
        p4_in_data(8) => s159_ready,
        p4_in_data(9) => s160_ready,
        p4_in_data(10) => s161_ready,
        p4_in_data(11) => s162_ready,
        p0_in_data => s163_data,
        p0_out_ready => s166_ready,
        p1_out_data => s148_data,
        p0_in_last => s164_last,
        reset => reset,
        p3_out_data => s150_data
    );
    U39_id: id_1089 port map(
        clk => clk,
        p1_out_valid => s165_valid,
        p0_in_valid => s173_valid,
        p1_out_last => s164_last,
        p0_in_data => s171_data,
        p0_out_ready => s174_ready,
        p0_in_last => s172_last,
        reset => reset,
        p1_in_ready => s166_ready,
        p1_out_data => s163_data
    );
    p1_out_data <= s167_data;
    p1_out_last <= "1" & s168_last when out_counter = stream_length - 1 else "0" & s168_last;
    p1_out_valid <= s169_valid;
    s170_ready <= p1_in_ready(s170_ready'high downto s170_ready'low);
    
    s171_data <= p0_in_data;
    s172_last <= p0_in_last(s172_last'high downto s172_last'low);
    s173_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s174_ready when in_counter = stream_length - 1 and s174_ready(s174_ready'high) = '1' else "0" & s174_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s173_valid = '1' and s174_ready(s174_ready'high) = '1' then
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
                if s169_valid = '1' and s170_ready(s170_ready'high) = '1' then
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
