library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity ifelse_465 is
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
end ifelse_465;

architecture behavioral of ifelse_465 is
    
    component id_411
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
    component select_392
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
    component id_409
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
    component tuple_417
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
    component addint_414
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
    component addint_398
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
    component addint_394
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
    component resizeinteger_401
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
    component addint_418
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
    component constantvalue_416
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_SignedIntTypeArithType32;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_391
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
    component resizeinteger_425
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
    component select_408
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
    component id_402
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
    component tuple_423
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
    component select_403
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
    component resizelowinteger_400
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
    component resizelowinteger_420
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
    component resizeinteger_415
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
    component tuple_393
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
    component subint_424
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
    component tuple_426
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
            p2_in_data: in type_SignedIntTypeArithType32;
            p2_in_last: in type_LastVectorTypeArithType0;
            p2_in_valid: in type_LogicType;
            p2_out_ready: out type_ReadyVectorTypeArithType0;
            p3_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p3_out_last: out type_LastVectorTypeArithType0;
            p3_out_valid: out type_LogicType;
            p3_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_404
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
            p2_in_data: in type_SignedIntTypeArithType32;
            p2_in_last: in type_LastVectorTypeArithType0;
            p2_in_valid: in type_LogicType;
            p2_out_ready: out type_ReadyVectorTypeArithType0;
            p3_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p3_out_last: out type_LastVectorTypeArithType0;
            p3_out_valid: out type_LogicType;
            p3_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component select_410
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
    component id_407
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
    component resizeinteger_419
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
    component tuple_397
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
    component constantvalue_422
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_SignedIntTypeArithType32;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component resizeinteger_395
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
    component resizeinteger_421
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
    component tuple_413
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
    component id_389
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
    component constantvalue_396
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_SignedIntTypeArithType32;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component select_412
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
    component resizeinteger_399
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
    component select_390
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
    
    type state_type is (select_idle, select_f1, select_f2);
    signal state: state_type := select_idle;
    
    signal s00_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_data: type_SignedIntTypeArithType32;
    signal s09_last: type_LastVectorTypeArithType0;
    signal s10_valid: type_LogicType;
    signal s100_data: type_SignedIntTypeArithType32;
    signal s101_last: type_LastVectorTypeArithType0;
    signal s102_valid: type_LogicType;
    signal s103_ready: type_ReadyVectorTypeArithType0;
    signal s104_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s105_last: type_LastVectorTypeArithType0;
    signal s106_valid: type_LogicType;
    signal s107_ready: type_ReadyVectorTypeArithType0;
    signal s108_data: type_SignedIntTypeArithType33;
    signal s109_last: type_LastVectorTypeArithType0;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s110_valid: type_LogicType;
    signal s111_ready: type_ReadyVectorTypeArithType0;
    signal s112_data: type_SignedIntTypeArithType32;
    signal s113_last: type_LastVectorTypeArithType0;
    signal s114_valid: type_LogicType;
    signal s115_ready: type_ReadyVectorTypeArithType0;
    signal s116_data: type_SignedIntTypeArithType31;
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
    signal s132_data: type_SignedIntTypeArithType33;
    signal s133_last: type_LastVectorTypeArithType0;
    signal s134_valid: type_LogicType;
    signal s135_ready: type_ReadyVectorTypeArithType0;
    signal s136_data: type_SignedIntTypeArithType32;
    signal s137_last: type_LastVectorTypeArithType0;
    signal s138_valid: type_LogicType;
    signal s139_ready: type_ReadyVectorTypeArithType0;
    signal s14_valid: type_LogicType;
    signal s140_data: type_SignedIntTypeArithType32;
    signal s141_last: type_LastVectorTypeArithType0;
    signal s142_valid: type_LogicType;
    signal s143_ready: type_ReadyVectorTypeArithType0;
    signal s144_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s145_last: type_LastVectorTypeArithType0;
    signal s146_valid: type_LogicType;
    signal s147_ready: type_ReadyVectorTypeArithType0;
    signal s148_data: type_SignedIntTypeArithType32;
    signal s149_last: type_LastVectorTypeArithType0;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s150_valid: type_LogicType;
    signal s151_ready: type_ReadyVectorTypeArithType0;
    signal s16_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s17_last: type_LastVectorTypeArithType0;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType0;
    signal s20_data: type_SignedIntTypeArithType33;
    signal s21_last: type_LastVectorTypeArithType0;
    signal s22_valid: type_LogicType;
    signal s23_ready: type_ReadyVectorTypeArithType0;
    signal s24_data: type_SignedIntTypeArithType32;
    signal s25_last: type_LastVectorTypeArithType0;
    signal s26_valid: type_LogicType;
    signal s27_ready: type_ReadyVectorTypeArithType0;
    signal s28_data: type_SignedIntTypeArithType32;
    signal s29_last: type_LastVectorTypeArithType0;
    signal s30_valid: type_LogicType;
    signal s31_ready: type_ReadyVectorTypeArithType0;
    signal s32_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s33_last: type_LastVectorTypeArithType0;
    signal s34_valid: type_LogicType;
    signal s35_ready: type_ReadyVectorTypeArithType0;
    signal s36_data: type_SignedIntTypeArithType33;
    signal s37_last: type_LastVectorTypeArithType0;
    signal s38_valid: type_LogicType;
    signal s39_ready: type_ReadyVectorTypeArithType0;
    signal s40_data: type_SignedIntTypeArithType32;
    signal s41_last: type_LastVectorTypeArithType0;
    signal s42_valid: type_LogicType;
    signal s43_ready: type_ReadyVectorTypeArithType0;
    signal s44_data: type_SignedIntTypeArithType31;
    signal s45_last: type_LastVectorTypeArithType0;
    signal s46_valid: type_LogicType;
    signal s47_ready: type_ReadyVectorTypeArithType0;
    signal s48_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s49_last: type_LastVectorTypeArithType0;
    signal s50_valid: type_LogicType;
    signal s51_ready: type_ReadyVectorTypeArithType0;
    signal s52_data: type_SignedIntTypeArithType32;
    signal s53_last: type_LastVectorTypeArithType0;
    signal s54_valid: type_LogicType;
    signal s55_ready: type_ReadyVectorTypeArithType0;
    signal s56_data: type_SignedIntTypeArithType32;
    signal s57_last: type_LastVectorTypeArithType0;
    signal s58_valid: type_LogicType;
    signal s59_ready: type_ReadyVectorTypeArithType0;
    signal s60_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s61_last: type_LastVectorTypeArithType0;
    signal s62_valid: type_LogicType;
    signal s63_ready: type_ReadyVectorTypeArithType0;
    signal s64_data: type_SignedIntTypeArithType32;
    signal s65_last: type_LastVectorTypeArithType0;
    signal s66_valid: type_LogicType;
    signal s67_ready: type_ReadyVectorTypeArithType0;
    signal s68_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s69_last: type_LastVectorTypeArithType0;
    signal s70_valid: type_LogicType;
    signal s71_ready: type_ReadyVectorTypeArithType0;
    signal s72_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s73_last: type_LastVectorTypeArithType0;
    signal s74_valid: type_LogicType;
    signal s75_ready: type_ReadyVectorTypeArithType0;
    signal s76_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s77_last: type_LastVectorTypeArithType0;
    signal s78_valid: type_LogicType;
    signal s79_ready: type_ReadyVectorTypeArithType0;
    signal s80_data: type_SignedIntTypeArithType32;
    signal s81_last: type_LastVectorTypeArithType0;
    signal s82_valid: type_LogicType;
    signal s83_ready: type_ReadyVectorTypeArithType0;
    signal s84_data: type_SignedIntTypeArithType32;
    signal s85_last: type_LastVectorTypeArithType0;
    signal s86_valid: type_LogicType;
    signal s87_ready: type_ReadyVectorTypeArithType0;
    signal s88_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s89_last: type_LastVectorTypeArithType0;
    signal s90_valid: type_LogicType;
    signal s91_ready: type_ReadyVectorTypeArithType0;
    signal s92_data: type_SignedIntTypeArithType33;
    signal s93_last: type_LastVectorTypeArithType0;
    signal s94_valid: type_LogicType;
    signal s95_ready: type_ReadyVectorTypeArithType0;
    signal s96_data: type_SignedIntTypeArithType32;
    signal s97_last: type_LastVectorTypeArithType0;
    signal s98_valid: type_LogicType;
    signal s99_ready: type_ReadyVectorTypeArithType0;
begin
    
    U0_id: id_389 port map(
        clk => clk,
        p1_out_valid => s02_valid,
        p0_in_valid => p2_in_valid,
        p1_out_last => s01_last,
        p0_in_data => p2_in_data,
        p0_out_ready => p2_out_ready,
        p0_in_last => p2_in_last,
        reset => reset,
        p1_in_ready => s03_ready,
        p1_out_data => s00_data
    );
    U1_select: select_390 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s02_valid,
        p1_out_last => s09_last,
        p0_in_data => s00_data,
        p0_out_ready => s03_ready,
        p0_in_last => s01_last,
        reset => reset,
        p1_in_ready => s11_ready,
        p1_out_data => s08_data
    );
    U2_id: id_391 port map(
        clk => clk,
        p1_out_valid => s06_valid,
        p0_in_valid => p3_in_valid,
        p1_out_last => s05_last,
        p0_in_data => p3_in_data,
        p0_out_ready => p3_out_ready,
        p0_in_last => p3_in_last,
        reset => reset,
        p1_in_ready => s07_ready,
        p1_out_data => s04_data
    );
    U3_select: select_392 port map(
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
    U4_tuple: tuple_393 port map(
        clk => clk,
        p2_out_valid => s18_valid,
        p1_in_last => s13_last,
        p0_in_valid => s10_valid,
        p2_out_data => s16_data,
        p0_in_data => s08_data,
        p2_out_last => s17_last,
        p0_out_ready => s11_ready,
        p1_in_valid => s14_valid,
        p2_in_ready => s19_ready,
        p1_in_data => s12_data,
        p0_in_last => s09_last,
        reset => reset,
        p1_out_ready => s15_ready
    );
    U5_addint: addint_394 port map(
        clk => clk,
        p1_out_valid => s22_valid,
        p0_in_valid => s18_valid,
        p1_out_last => s21_last,
        p1_out_data => s20_data,
        p0_out_ready => s19_ready,
        p0_in_last => s17_last,
        reset => reset,
        p1_in_ready => s23_ready,
        p0_in_data => s16_data
    );
    U6_resizeinteger: resizeinteger_395 port map(
        clk => clk,
        p1_out_valid => s26_valid,
        p0_in_valid => s22_valid,
        p0_in_data => s20_data,
        p1_out_last => s25_last,
        p0_out_ready => s23_ready,
        p0_in_last => s21_last,
        reset => reset,
        p1_in_ready => s27_ready,
        p1_out_data => s24_data
    );
    U7_constantvalue: constantvalue_396 port map(
        p0_out_last => s29_last,
        clk => clk,
        p0_in_ready => s31_ready,
        p0_out_valid => s30_valid,
        reset => reset,
        p0_out_data => s28_data
    );
    U8_tuple: tuple_397 port map(
        clk => clk,
        p2_out_valid => s34_valid,
        p1_in_last => s29_last,
        p0_in_valid => s26_valid,
        p2_out_data => s32_data,
        p0_in_data => s24_data,
        p2_out_last => s33_last,
        p0_out_ready => s27_ready,
        p1_in_valid => s30_valid,
        p2_in_ready => s35_ready,
        p1_in_data => s28_data,
        p0_in_last => s25_last,
        reset => reset,
        p1_out_ready => s31_ready
    );
    U9_addint: addint_398 port map(
        clk => clk,
        p1_out_valid => s38_valid,
        p0_in_valid => s34_valid,
        p1_out_last => s37_last,
        p1_out_data => s36_data,
        p0_out_ready => s35_ready,
        p0_in_last => s33_last,
        reset => reset,
        p1_in_ready => s39_ready,
        p0_in_data => s32_data
    );
    U10_resizeinteger: resizeinteger_399 port map(
        clk => clk,
        p1_out_valid => s42_valid,
        p0_in_valid => s38_valid,
        p0_in_data => s36_data,
        p1_out_last => s41_last,
        p0_out_ready => s39_ready,
        p0_in_last => s37_last,
        reset => reset,
        p1_in_ready => s43_ready,
        p1_out_data => s40_data
    );
    U11_resizelowinteger: resizelowinteger_400 port map(
        clk => clk,
        p1_out_valid => s46_valid,
        p0_in_valid => s42_valid,
        p1_out_last => s45_last,
        p0_in_data => s40_data,
        p0_out_ready => s43_ready,
        p0_in_last => s41_last,
        reset => reset,
        p1_in_ready => s47_ready,
        p1_out_data => s44_data
    );
    U12_resizeinteger: resizeinteger_401 port map(
        clk => clk,
        p1_out_valid => s54_valid,
        p0_in_valid => s46_valid,
        p1_out_last => s53_last,
        p0_out_ready => s47_ready,
        p0_in_last => s45_last,
        reset => reset,
        p1_in_ready => s55_ready,
        p1_out_data => s52_data,
        p0_in_data => s44_data
    );
    U13_id: id_402 port map(
        clk => clk,
        p1_out_valid => s50_valid,
        p0_in_valid => p4_in_valid,
        p1_out_last => s49_last,
        p0_in_data => p4_in_data,
        p0_out_ready => p4_out_ready,
        p0_in_last => p4_in_last,
        reset => reset,
        p1_in_ready => s51_ready,
        p1_out_data => s48_data
    );
    U14_select: select_403 port map(
        clk => clk,
        p1_out_valid => s58_valid,
        p0_in_valid => s50_valid,
        p1_out_last => s57_last,
        p0_in_data => s48_data,
        p0_out_ready => s51_ready,
        p0_in_last => s49_last,
        reset => reset,
        p1_in_ready => s59_ready,
        p1_out_data => s56_data
    );
    U15_tuple: tuple_404 port map(
        clk => clk,
        p1_in_last => s53_last,
        p0_in_valid => s66_valid,
        p3_out_valid => s62_valid,
        p2_out_ready => s59_ready,
        p0_in_data => s64_data,
        p2_in_valid => s58_valid,
        p2_in_data => s56_data,
        p0_out_ready => s67_ready,
        p1_in_valid => s54_valid,
        p3_out_last => s61_last,
        p1_in_data => s52_data,
        p0_in_last => s65_last,
        reset => reset,
        p2_in_last => s57_last,
        p3_out_data => s60_data,
        p1_out_ready => s55_ready,
        p3_in_ready => s63_ready
    );
    U16_id: id_407 port map(
        clk => clk,
        p1_out_valid => s70_valid,
        p0_in_valid => p5_in_valid,
        p1_out_last => s69_last,
        p0_in_data => p5_in_data,
        p0_out_ready => p5_out_ready,
        p0_in_last => p5_in_last,
        reset => reset,
        p1_in_ready => s71_ready,
        p1_out_data => s68_data
    );
    U17_select: select_408 port map(
        clk => clk,
        p1_out_valid => s138_valid,
        p0_in_valid => s70_valid,
        p1_out_last => s137_last,
        p0_in_data => s68_data,
        p0_out_ready => s71_ready,
        p0_in_last => s69_last,
        reset => reset,
        p1_in_ready => s139_ready,
        p1_out_data => s136_data
    );
    U18_id: id_409 port map(
        clk => clk,
        p1_out_valid => s74_valid,
        p0_in_valid => p6_in_valid,
        p1_out_last => s73_last,
        p0_in_data => p6_in_data,
        p0_out_ready => p6_out_ready,
        p0_in_last => p6_in_last,
        reset => reset,
        p1_in_ready => s75_ready,
        p1_out_data => s72_data
    );
    U19_select: select_410 port map(
        clk => clk,
        p1_out_valid => s82_valid,
        p0_in_valid => s74_valid,
        p1_out_last => s81_last,
        p0_in_data => s72_data,
        p0_out_ready => s75_ready,
        p0_in_last => s73_last,
        reset => reset,
        p1_in_ready => s83_ready,
        p1_out_data => s80_data
    );
    U20_id: id_411 port map(
        clk => clk,
        p1_out_valid => s78_valid,
        p0_in_valid => p7_in_valid,
        p1_out_last => s77_last,
        p0_in_data => p7_in_data,
        p0_out_ready => p7_out_ready,
        p0_in_last => p7_in_last,
        reset => reset,
        p1_in_ready => s79_ready,
        p1_out_data => s76_data
    );
    U21_select: select_412 port map(
        clk => clk,
        p1_out_valid => s86_valid,
        p0_in_valid => s78_valid,
        p1_out_last => s85_last,
        p0_in_data => s76_data,
        p0_out_ready => s79_ready,
        p0_in_last => s77_last,
        reset => reset,
        p1_in_ready => s87_ready,
        p1_out_data => s84_data
    );
    U22_tuple: tuple_413 port map(
        clk => clk,
        p2_out_valid => s90_valid,
        p1_in_last => s85_last,
        p0_in_valid => s82_valid,
        p2_out_data => s88_data,
        p0_in_data => s80_data,
        p2_out_last => s89_last,
        p0_out_ready => s83_ready,
        p1_in_valid => s86_valid,
        p2_in_ready => s91_ready,
        p1_in_data => s84_data,
        p0_in_last => s81_last,
        reset => reset,
        p1_out_ready => s87_ready
    );
    U23_addint: addint_414 port map(
        clk => clk,
        p1_out_valid => s94_valid,
        p0_in_valid => s90_valid,
        p1_out_last => s93_last,
        p1_out_data => s92_data,
        p0_out_ready => s91_ready,
        p0_in_last => s89_last,
        reset => reset,
        p1_in_ready => s95_ready,
        p0_in_data => s88_data
    );
    U24_resizeinteger: resizeinteger_415 port map(
        clk => clk,
        p1_out_valid => s98_valid,
        p0_in_valid => s94_valid,
        p0_in_data => s92_data,
        p1_out_last => s97_last,
        p0_out_ready => s95_ready,
        p0_in_last => s93_last,
        reset => reset,
        p1_in_ready => s99_ready,
        p1_out_data => s96_data
    );
    U25_constantvalue: constantvalue_416 port map(
        p0_out_last => s101_last,
        clk => clk,
        p0_in_ready => s103_ready,
        p0_out_valid => s102_valid,
        reset => reset,
        p0_out_data => s100_data
    );
    U26_tuple: tuple_417 port map(
        clk => clk,
        p2_out_valid => s106_valid,
        p1_in_last => s101_last,
        p0_in_valid => s98_valid,
        p2_out_data => s104_data,
        p0_in_data => s96_data,
        p2_out_last => s105_last,
        p0_out_ready => s99_ready,
        p1_in_valid => s102_valid,
        p2_in_ready => s107_ready,
        p1_in_data => s100_data,
        p0_in_last => s97_last,
        reset => reset,
        p1_out_ready => s103_ready
    );
    U27_addint: addint_418 port map(
        clk => clk,
        p1_out_valid => s110_valid,
        p0_in_valid => s106_valid,
        p1_out_last => s109_last,
        p1_out_data => s108_data,
        p0_out_ready => s107_ready,
        p0_in_last => s105_last,
        reset => reset,
        p1_in_ready => s111_ready,
        p0_in_data => s104_data
    );
    U28_resizeinteger: resizeinteger_419 port map(
        clk => clk,
        p1_out_valid => s114_valid,
        p0_in_valid => s110_valid,
        p0_in_data => s108_data,
        p1_out_last => s113_last,
        p0_out_ready => s111_ready,
        p0_in_last => s109_last,
        reset => reset,
        p1_in_ready => s115_ready,
        p1_out_data => s112_data
    );
    U29_resizelowinteger: resizelowinteger_420 port map(
        clk => clk,
        p1_out_valid => s118_valid,
        p0_in_valid => s114_valid,
        p1_out_last => s117_last,
        p0_in_data => s112_data,
        p0_out_ready => s115_ready,
        p0_in_last => s113_last,
        reset => reset,
        p1_in_ready => s119_ready,
        p1_out_data => s116_data
    );
    U30_resizeinteger: resizeinteger_421 port map(
        clk => clk,
        p1_out_valid => s122_valid,
        p0_in_valid => s118_valid,
        p1_out_last => s121_last,
        p0_out_ready => s119_ready,
        p0_in_last => s117_last,
        reset => reset,
        p1_in_ready => s123_ready,
        p1_out_data => s120_data,
        p0_in_data => s116_data
    );
    U31_constantvalue: constantvalue_422 port map(
        p0_out_last => s125_last,
        clk => clk,
        p0_in_ready => s127_ready,
        p0_out_valid => s126_valid,
        reset => reset,
        p0_out_data => s124_data
    );
    U32_tuple: tuple_423 port map(
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
    U33_subint: subint_424 port map(
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
    U34_resizeinteger: resizeinteger_425 port map(
        clk => clk,
        p1_out_valid => s142_valid,
        p0_in_valid => s134_valid,
        p0_in_data => s132_data,
        p1_out_last => s141_last,
        p0_out_ready => s135_ready,
        p0_in_last => s133_last,
        reset => reset,
        p1_in_ready => s143_ready,
        p1_out_data => s140_data
    );
    U35_tuple: tuple_426 port map(
        clk => clk,
        p1_in_last => s137_last,
        p0_in_valid => s150_valid,
        p3_out_valid => s146_valid,
        p2_out_ready => s143_ready,
        p0_in_data => s148_data,
        p2_in_valid => s142_valid,
        p2_in_data => s140_data,
        p0_out_ready => s151_ready,
        p1_in_valid => s138_valid,
        p3_out_last => s145_last,
        p1_in_data => s136_data,
        p0_in_last => s149_last,
        reset => reset,
        p2_in_last => s141_last,
        p3_out_data => s144_data,
        p1_out_ready => s139_ready,
        p3_in_ready => s147_ready
    );
    s64_data <= p0_in_data.t0;
    s65_last <= p0_in_last;
    s66_valid <= p0_in_valid when state = select_f1 else '0';
    s148_data <= p0_in_data.t0;
    s149_last <= p0_in_last;
    s150_valid <= p0_in_valid when state = select_f2 else '0';
    
    p0_out_ready <= s67_ready when state = select_f1 else s151_ready when state = select_f2 else (others => '0');
    
    state_logic: process(clk)
        constant out_last_all_one: std_logic_vector(p1_out_last'range) := (others => '1');
        constant out_ready_all_one: std_logic_vector(p1_in_ready'range) := (others => '1');
        constant in_last_all_one: std_logic_vector(p0_in_last'range) := (others => '1');
        constant in_ready_all_one: std_logic_vector(p0_out_ready'range) := (others => '1');
    begin
        if rising_edge(clk) then
            if reset = '1' then
                state <= select_idle;
            else
                if state = select_idle then
                    if p0_in_valid = '1' then
                        if p0_in_data.t1(0) = '0' then
                            state <= select_f1;
                        else
                            state <= select_f2;
                        end if;
                    end if;
                else
                    if p1_in_ready = out_ready_all_one then
                        if state = select_f1 then
                            if s61_last = out_last_all_one and s62_valid = '1' then
                                state <= select_idle;
                            end if;
                        elsif state = select_f2 then
                            if s145_last = out_last_all_one and s146_valid = '1' then
                                state <= select_idle;
                            end if;
                        end if;
                    end if;
                end if;
            end if;
        end if;
    end process;
    
    p1_out_data <= s60_data when state = select_f1 else s144_data;
    
    with state select p1_out_last <=
    s61_last when select_f1,
    s145_last when select_f2,
    (others => '0') when others;
    with state select p1_out_valid <=
    s62_valid when select_f1,
    s146_valid when select_f2,
    '0' when others;
    
    s63_ready <= p1_in_ready when state = select_f1 else (others => '0');
    s147_ready <= p1_in_ready when state = select_f2 else (others => '0');
    
end behavioral;
