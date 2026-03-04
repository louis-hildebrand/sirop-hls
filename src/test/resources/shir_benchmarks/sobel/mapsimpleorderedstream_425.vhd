library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_425 is
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
end mapsimpleorderedstream_425;

architecture behavioral of mapsimpleorderedstream_425 is
    
    component resizeinteger_381
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
    component registered_422
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
    component tuple_384
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
    component select_352
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
    component resizeinteger_359
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
    component tuple_373
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
    component resizeinteger_361
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
    component constantvalue_376
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_SignedIntTypeArithType32;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_424
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
    component constantvalue_362
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_SignedIntTypeArithType32;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component addint_374
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
    component select_370
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
    component resizeinteger_379
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
    component tuple_377
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
    component id_351
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
    component resizeinteger_375
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
    component tuple_366
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
    component addint_358
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
    component addint_354
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
    component select_348
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
    component conversion_420
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_VectorTypeLogicTypeArithType1;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType1;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component resizelowinteger_380
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
    component constantvalue_356
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_SignedIntTypeArithType32;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_347
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
    component tuple_385
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32TextTypet1NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_363
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
    component id_369
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
    component select_383
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
    component tuple_357
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
    component select_372
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
    component resizeinteger_365
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
    component tuple_353
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
    component select_350
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
    component id_371
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
    component id_382
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
    component select_346
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
    component addint_378
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
    component subint_364
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
    component distributor_423
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType13;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType13;
            p3_out_data: out type_VectorTypeLogicTypeArithType13;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType13
        );
    end component;
    component resizeinteger_355
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
    component id_349
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
    component id_367
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
    component select_368
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
    component resizelowinteger_360
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
    component vectorgenerator_419
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_VectorTypeLogicTypeArithType1;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0;
            p1_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
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
            p5_out_ready: out type_ReadyVectorTypeArithType0
        );
    end component;
    component id_345
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
    component index_421
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32TextTypet1NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_IntTypeArithType1;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
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
    signal s116_data: type_SignedIntTypeArithType32;
    signal s117_last: type_LastVectorTypeArithType0;
    signal s118_valid: type_LogicType;
    signal s119_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s120_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s121_last: type_LastVectorTypeArithType0;
    signal s122_valid: type_LogicType;
    signal s123_ready: type_ReadyVectorTypeArithType0;
    signal s124_data: type_SignedIntTypeArithType33;
    signal s125_last: type_LastVectorTypeArithType0;
    signal s126_valid: type_LogicType;
    signal s127_ready: type_ReadyVectorTypeArithType0;
    signal s128_data: type_SignedIntTypeArithType32;
    signal s129_last: type_LastVectorTypeArithType0;
    signal s13_last: type_LastVectorTypeArithType0;
    signal s130_valid: type_LogicType;
    signal s131_ready: type_ReadyVectorTypeArithType0;
    signal s132_data: type_SignedIntTypeArithType31;
    signal s133_last: type_LastVectorTypeArithType0;
    signal s134_valid: type_LogicType;
    signal s135_ready: type_ReadyVectorTypeArithType0;
    signal s136_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s137_last: type_LastVectorTypeArithType0;
    signal s138_valid: type_LogicType;
    signal s139_ready: type_ReadyVectorTypeArithType0;
    signal s14_valid: type_LogicType;
    signal s140_data: type_SignedIntTypeArithType32;
    signal s141_last: type_LastVectorTypeArithType0;
    signal s142_valid: type_LogicType;
    signal s143_ready: type_ReadyVectorTypeArithType0;
    signal s144_data: type_SignedIntTypeArithType32;
    signal s145_last: type_LastVectorTypeArithType0;
    signal s146_valid: type_LogicType;
    signal s147_ready: type_ReadyVectorTypeArithType0;
    signal s148_data: type_SignedIntTypeArithType32;
    signal s149_last: type_LastVectorTypeArithType0;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s150_valid: type_LogicType;
    signal s151_ready: type_ReadyVectorTypeArithType0;
    signal s152_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s153_last: type_LastVectorTypeArithType0;
    signal s154_valid: type_LogicType;
    signal s155_ready: type_ReadyVectorTypeArithType0;
    signal s156_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s157_last: type_LastVectorTypeArithType0;
    signal s158_valid: type_LogicType;
    signal s159_ready: type_ReadyVectorTypeArithType0;
    signal s16_data: type_SignedIntTypeArithType32;
    signal s160_data: type_VectorTypeLogicTypeArithType1;
    signal s161_last: type_LastVectorTypeArithType0;
    signal s162_valid: type_LogicType;
    signal s163_ready: type_ReadyVectorTypeArithType0;
    signal s164_data: type_NamedTupleTypeTextTypet0NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32TextTypet1NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s165_last: type_LastVectorTypeArithType0;
    signal s166_valid: type_LogicType;
    signal s167_ready: type_ReadyVectorTypeArithType0;
    signal s168_data: type_IntTypeArithType1;
    signal s169_last: type_LastVectorTypeArithType0;
    signal s17_last: type_LastVectorTypeArithType0;
    signal s170_valid: type_LogicType;
    signal s171_ready: type_ReadyVectorTypeArithType0;
    signal s172_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s173_last: type_LastVectorTypeArithType0;
    signal s174_valid: type_LogicType;
    signal s175_ready: type_ReadyVectorTypeArithType0;
    signal s176_data: type_VectorTypeNamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32ArithType13;
    signal s177_data: type_VectorTypeLastVectorTypeArithType0ArithType13;
    signal s178_data: type_VectorTypeLogicTypeArithType13;
    signal s179_ready: type_ReadyVectorTypeArithType0;
    signal s18_valid: type_LogicType;
    signal s180_ready: type_ReadyVectorTypeArithType0;
    signal s181_ready: type_ReadyVectorTypeArithType0;
    signal s182_ready: type_ReadyVectorTypeArithType0;
    signal s183_ready: type_ReadyVectorTypeArithType0;
    signal s184_ready: type_ReadyVectorTypeArithType0;
    signal s185_ready: type_ReadyVectorTypeArithType0;
    signal s186_ready: type_ReadyVectorTypeArithType0;
    signal s187_ready: type_ReadyVectorTypeArithType0;
    signal s188_ready: type_ReadyVectorTypeArithType0;
    signal s189_ready: type_ReadyVectorTypeArithType0;
    signal s19_ready: type_ReadyVectorTypeArithType0;
    signal s190_ready: type_ReadyVectorTypeArithType0;
    signal s191_ready: type_ReadyVectorTypeArithType0;
    signal s192_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s193_last: type_LastVectorTypeArithType0;
    signal s194_valid: type_LogicType;
    signal s195_ready: type_ReadyVectorTypeArithType0;
    signal s196_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s197_last: type_LastVectorTypeArithType0;
    signal s198_valid: type_LogicType;
    signal s199_ready: type_ReadyVectorTypeArithType0;
    signal s20_data: type_SignedIntTypeArithType32;
    signal s200_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s201_last: type_LastVectorTypeArithType0;
    signal s202_valid: type_LogicType;
    signal s203_ready: type_ReadyVectorTypeArithType0;
    signal s204_data: type_LastVectorTypeArithType1;
    signal s205_data: type_ReadyVectorTypeArithType1;
    signal s21_last: type_LastVectorTypeArithType0;
    signal s22_valid: type_LogicType;
    signal s23_ready: type_ReadyVectorTypeArithType0;
    signal s24_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s25_last: type_LastVectorTypeArithType0;
    signal s26_valid: type_LogicType;
    signal s27_ready: type_ReadyVectorTypeArithType0;
    signal s28_data: type_SignedIntTypeArithType33;
    signal s29_last: type_LastVectorTypeArithType0;
    signal s30_valid: type_LogicType;
    signal s31_ready: type_ReadyVectorTypeArithType0;
    signal s32_data: type_SignedIntTypeArithType32;
    signal s33_last: type_LastVectorTypeArithType0;
    signal s34_valid: type_LogicType;
    signal s35_ready: type_ReadyVectorTypeArithType0;
    signal s36_data: type_SignedIntTypeArithType32;
    signal s37_last: type_LastVectorTypeArithType0;
    signal s38_valid: type_LogicType;
    signal s39_ready: type_ReadyVectorTypeArithType0;
    signal s40_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s41_last: type_LastVectorTypeArithType0;
    signal s42_valid: type_LogicType;
    signal s43_ready: type_ReadyVectorTypeArithType0;
    signal s44_data: type_SignedIntTypeArithType33;
    signal s45_last: type_LastVectorTypeArithType0;
    signal s46_valid: type_LogicType;
    signal s47_ready: type_ReadyVectorTypeArithType0;
    signal s48_data: type_SignedIntTypeArithType32;
    signal s49_last: type_LastVectorTypeArithType0;
    signal s50_valid: type_LogicType;
    signal s51_ready: type_ReadyVectorTypeArithType0;
    signal s52_data: type_SignedIntTypeArithType31;
    signal s53_last: type_LastVectorTypeArithType0;
    signal s54_valid: type_LogicType;
    signal s55_ready: type_ReadyVectorTypeArithType0;
    signal s56_data: type_SignedIntTypeArithType32;
    signal s57_last: type_LastVectorTypeArithType0;
    signal s58_valid: type_LogicType;
    signal s59_ready: type_ReadyVectorTypeArithType0;
    signal s60_data: type_SignedIntTypeArithType32;
    signal s61_last: type_LastVectorTypeArithType0;
    signal s62_valid: type_LogicType;
    signal s63_ready: type_ReadyVectorTypeArithType0;
    signal s64_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32_t0;
    signal s65_last: type_LastVectorTypeArithType0;
    signal s66_valid: type_LogicType;
    signal s67_ready: type_ReadyVectorTypeArithType0;
    signal s68_data: type_SignedIntTypeArithType33;
    signal s69_last: type_LastVectorTypeArithType0;
    signal s70_valid: type_LogicType;
    signal s71_ready: type_ReadyVectorTypeArithType0;
    signal s72_data: type_SignedIntTypeArithType32;
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
    signal s84_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s85_last: type_LastVectorTypeArithType0;
    signal s86_valid: type_LogicType;
    signal s87_ready: type_ReadyVectorTypeArithType0;
    signal s88_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s89_last: type_LastVectorTypeArithType0;
    signal s90_valid: type_LogicType;
    signal s91_ready: type_ReadyVectorTypeArithType0;
    signal s92_data: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal s93_last: type_LastVectorTypeArithType0;
    signal s94_valid: type_LogicType;
    signal s95_ready: type_ReadyVectorTypeArithType0;
    signal s96_data: type_SignedIntTypeArithType32;
    signal s97_last: type_LastVectorTypeArithType0;
    signal s98_valid: type_LogicType;
    signal s99_ready: type_ReadyVectorTypeArithType0;
begin
    
    U0_id: id_345 port map(
        clk => clk,
        p1_out_valid => s02_valid,
        p0_in_valid => s178_data(0),
        p1_out_last => s01_last,
        p0_in_data => s176_data(0),
        p0_out_ready => s179_ready,
        p0_in_last => s177_data(0),
        reset => reset,
        p1_in_ready => s03_ready,
        p1_out_data => s00_data
    );
    U1_select: select_346 port map(
        clk => clk,
        p1_out_valid => s74_valid,
        p0_in_valid => s02_valid,
        p1_out_last => s73_last,
        p0_in_data => s00_data,
        p0_out_ready => s03_ready,
        p0_in_last => s01_last,
        reset => reset,
        p1_in_ready => s75_ready,
        p1_out_data => s72_data
    );
    U2_id: id_347 port map(
        clk => clk,
        p1_out_valid => s06_valid,
        p0_in_valid => s178_data(1),
        p1_out_last => s05_last,
        p0_in_data => s176_data(1),
        p0_out_ready => s180_ready,
        p0_in_last => s177_data(1),
        reset => reset,
        p1_in_ready => s07_ready,
        p1_out_data => s04_data
    );
    U3_select: select_348 port map(
        clk => clk,
        p1_out_valid => s78_valid,
        p0_in_valid => s06_valid,
        p1_out_last => s77_last,
        p0_in_data => s04_data,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => s79_ready,
        p1_out_data => s76_data
    );
    U4_id: id_349 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s178_data(2),
        p1_out_last => s09_last,
        p0_in_data => s176_data(2),
        p0_out_ready => s181_ready,
        p0_in_last => s177_data(2),
        reset => reset,
        p1_in_ready => s11_ready,
        p1_out_data => s08_data
    );
    U5_select: select_350 port map(
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
    U6_id: id_351 port map(
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => s178_data(3),
        p1_out_last => s13_last,
        p0_in_data => s176_data(3),
        p0_out_ready => s182_ready,
        p0_in_last => s177_data(3),
        reset => reset,
        p1_in_ready => s15_ready,
        p1_out_data => s12_data
    );
    U7_select: select_352 port map(
        clk => clk,
        p1_out_valid => s22_valid,
        p0_in_valid => s14_valid,
        p1_out_last => s21_last,
        p0_in_data => s12_data,
        p0_out_ready => s15_ready,
        p0_in_last => s13_last,
        reset => reset,
        p1_in_ready => s23_ready,
        p1_out_data => s20_data
    );
    U8_tuple: tuple_353 port map(
        clk => clk,
        p2_out_valid => s26_valid,
        p1_in_last => s21_last,
        p0_in_valid => s18_valid,
        p2_out_data => s24_data,
        p0_in_data => s16_data,
        p2_out_last => s25_last,
        p0_out_ready => s19_ready,
        p1_in_valid => s22_valid,
        p2_in_ready => s27_ready,
        p1_in_data => s20_data,
        p0_in_last => s17_last,
        reset => reset,
        p1_out_ready => s23_ready
    );
    U9_addint: addint_354 port map(
        clk => clk,
        p1_out_valid => s30_valid,
        p0_in_valid => s26_valid,
        p1_out_last => s29_last,
        p1_out_data => s28_data,
        p0_out_ready => s27_ready,
        p0_in_last => s25_last,
        reset => reset,
        p1_in_ready => s31_ready,
        p0_in_data => s24_data
    );
    U10_resizeinteger: resizeinteger_355 port map(
        clk => clk,
        p1_out_valid => s34_valid,
        p0_in_valid => s30_valid,
        p0_in_data => s28_data,
        p1_out_last => s33_last,
        p0_out_ready => s31_ready,
        p0_in_last => s29_last,
        reset => reset,
        p1_in_ready => s35_ready,
        p1_out_data => s32_data
    );
    U11_constantvalue: constantvalue_356 port map(
        p0_out_last => s37_last,
        clk => clk,
        p0_in_ready => s39_ready,
        p0_out_valid => s38_valid,
        reset => reset,
        p0_out_data => s36_data
    );
    U12_tuple: tuple_357 port map(
        clk => clk,
        p2_out_valid => s42_valid,
        p1_in_last => s37_last,
        p0_in_valid => s34_valid,
        p2_out_data => s40_data,
        p0_in_data => s32_data,
        p2_out_last => s41_last,
        p0_out_ready => s35_ready,
        p1_in_valid => s38_valid,
        p2_in_ready => s43_ready,
        p1_in_data => s36_data,
        p0_in_last => s33_last,
        reset => reset,
        p1_out_ready => s39_ready
    );
    U13_addint: addint_358 port map(
        clk => clk,
        p1_out_valid => s46_valid,
        p0_in_valid => s42_valid,
        p1_out_last => s45_last,
        p1_out_data => s44_data,
        p0_out_ready => s43_ready,
        p0_in_last => s41_last,
        reset => reset,
        p1_in_ready => s47_ready,
        p0_in_data => s40_data
    );
    U14_resizeinteger: resizeinteger_359 port map(
        clk => clk,
        p1_out_valid => s50_valid,
        p0_in_valid => s46_valid,
        p0_in_data => s44_data,
        p1_out_last => s49_last,
        p0_out_ready => s47_ready,
        p0_in_last => s45_last,
        reset => reset,
        p1_in_ready => s51_ready,
        p1_out_data => s48_data
    );
    U15_resizelowinteger: resizelowinteger_360 port map(
        clk => clk,
        p1_out_valid => s54_valid,
        p0_in_valid => s50_valid,
        p1_out_last => s53_last,
        p0_in_data => s48_data,
        p0_out_ready => s51_ready,
        p0_in_last => s49_last,
        reset => reset,
        p1_in_ready => s55_ready,
        p1_out_data => s52_data
    );
    U16_resizeinteger: resizeinteger_361 port map(
        clk => clk,
        p1_out_valid => s58_valid,
        p0_in_valid => s54_valid,
        p1_out_last => s57_last,
        p0_out_ready => s55_ready,
        p0_in_last => s53_last,
        reset => reset,
        p1_in_ready => s59_ready,
        p1_out_data => s56_data,
        p0_in_data => s52_data
    );
    U17_constantvalue: constantvalue_362 port map(
        p0_out_last => s61_last,
        clk => clk,
        p0_in_ready => s63_ready,
        p0_out_valid => s62_valid,
        reset => reset,
        p0_out_data => s60_data
    );
    U18_tuple: tuple_363 port map(
        clk => clk,
        p2_out_valid => s66_valid,
        p1_in_last => s61_last,
        p0_in_valid => s58_valid,
        p2_out_data => s64_data,
        p0_in_data => s56_data,
        p2_out_last => s65_last,
        p0_out_ready => s59_ready,
        p1_in_valid => s62_valid,
        p2_in_ready => s67_ready,
        p1_in_data => s60_data,
        p0_in_last => s57_last,
        reset => reset,
        p1_out_ready => s63_ready
    );
    U19_subint: subint_364 port map(
        clk => clk,
        p1_out_valid => s70_valid,
        p0_in_valid => s66_valid,
        p1_out_last => s69_last,
        p1_out_data => s68_data,
        p0_out_ready => s67_ready,
        p0_in_last => s65_last,
        reset => reset,
        p1_in_ready => s71_ready,
        p0_in_data => s64_data
    );
    U20_resizeinteger: resizeinteger_365 port map(
        clk => clk,
        p1_out_valid => s82_valid,
        p0_in_valid => s70_valid,
        p0_in_data => s68_data,
        p1_out_last => s81_last,
        p0_out_ready => s71_ready,
        p0_in_last => s69_last,
        reset => reset,
        p1_in_ready => s83_ready,
        p1_out_data => s80_data
    );
    U21_tuple: tuple_366 port map(
        clk => clk,
        p1_in_last => s77_last,
        p0_in_valid => s74_valid,
        p3_out_valid => s154_valid,
        p2_out_ready => s83_ready,
        p0_in_data => s72_data,
        p2_in_valid => s82_valid,
        p2_in_data => s80_data,
        p0_out_ready => s75_ready,
        p1_in_valid => s78_valid,
        p3_out_last => s153_last,
        p1_in_data => s76_data,
        p0_in_last => s73_last,
        reset => reset,
        p2_in_last => s81_last,
        p3_out_data => s152_data,
        p1_out_ready => s79_ready,
        p3_in_ready => s155_ready
    );
    U22_id: id_367 port map(
        clk => clk,
        p1_out_valid => s86_valid,
        p0_in_valid => s178_data(4),
        p1_out_last => s85_last,
        p0_in_data => s176_data(4),
        p0_out_ready => s183_ready,
        p0_in_last => s177_data(4),
        reset => reset,
        p1_in_ready => s87_ready,
        p1_out_data => s84_data
    );
    U23_select: select_368 port map(
        clk => clk,
        p1_out_valid => s142_valid,
        p0_in_valid => s86_valid,
        p1_out_last => s141_last,
        p0_in_data => s84_data,
        p0_out_ready => s87_ready,
        p0_in_last => s85_last,
        reset => reset,
        p1_in_ready => s143_ready,
        p1_out_data => s140_data
    );
    U24_id: id_369 port map(
        clk => clk,
        p1_out_valid => s90_valid,
        p0_in_valid => s178_data(5),
        p1_out_last => s89_last,
        p0_in_data => s176_data(5),
        p0_out_ready => s184_ready,
        p0_in_last => s177_data(5),
        reset => reset,
        p1_in_ready => s91_ready,
        p1_out_data => s88_data
    );
    U25_select: select_370 port map(
        clk => clk,
        p1_out_valid => s98_valid,
        p0_in_valid => s90_valid,
        p1_out_last => s97_last,
        p0_in_data => s88_data,
        p0_out_ready => s91_ready,
        p0_in_last => s89_last,
        reset => reset,
        p1_in_ready => s99_ready,
        p1_out_data => s96_data
    );
    U26_id: id_371 port map(
        clk => clk,
        p1_out_valid => s94_valid,
        p0_in_valid => s178_data(6),
        p1_out_last => s93_last,
        p0_in_data => s176_data(6),
        p0_out_ready => s185_ready,
        p0_in_last => s177_data(6),
        reset => reset,
        p1_in_ready => s95_ready,
        p1_out_data => s92_data
    );
    U27_select: select_372 port map(
        clk => clk,
        p1_out_valid => s102_valid,
        p0_in_valid => s94_valid,
        p1_out_last => s101_last,
        p0_in_data => s92_data,
        p0_out_ready => s95_ready,
        p0_in_last => s93_last,
        reset => reset,
        p1_in_ready => s103_ready,
        p1_out_data => s100_data
    );
    U28_tuple: tuple_373 port map(
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
    U29_addint: addint_374 port map(
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
    U30_resizeinteger: resizeinteger_375 port map(
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
    U31_constantvalue: constantvalue_376 port map(
        p0_out_last => s117_last,
        clk => clk,
        p0_in_ready => s119_ready,
        p0_out_valid => s118_valid,
        reset => reset,
        p0_out_data => s116_data
    );
    U32_tuple: tuple_377 port map(
        clk => clk,
        p2_out_valid => s122_valid,
        p1_in_last => s117_last,
        p0_in_valid => s114_valid,
        p2_out_data => s120_data,
        p0_in_data => s112_data,
        p2_out_last => s121_last,
        p0_out_ready => s115_ready,
        p1_in_valid => s118_valid,
        p2_in_ready => s123_ready,
        p1_in_data => s116_data,
        p0_in_last => s113_last,
        reset => reset,
        p1_out_ready => s119_ready
    );
    U33_addint: addint_378 port map(
        clk => clk,
        p1_out_valid => s126_valid,
        p0_in_valid => s122_valid,
        p1_out_last => s125_last,
        p1_out_data => s124_data,
        p0_out_ready => s123_ready,
        p0_in_last => s121_last,
        reset => reset,
        p1_in_ready => s127_ready,
        p0_in_data => s120_data
    );
    U34_resizeinteger: resizeinteger_379 port map(
        clk => clk,
        p1_out_valid => s130_valid,
        p0_in_valid => s126_valid,
        p0_in_data => s124_data,
        p1_out_last => s129_last,
        p0_out_ready => s127_ready,
        p0_in_last => s125_last,
        reset => reset,
        p1_in_ready => s131_ready,
        p1_out_data => s128_data
    );
    U35_resizelowinteger: resizelowinteger_380 port map(
        clk => clk,
        p1_out_valid => s134_valid,
        p0_in_valid => s130_valid,
        p1_out_last => s133_last,
        p0_in_data => s128_data,
        p0_out_ready => s131_ready,
        p0_in_last => s129_last,
        reset => reset,
        p1_in_ready => s135_ready,
        p1_out_data => s132_data
    );
    U36_resizeinteger: resizeinteger_381 port map(
        clk => clk,
        p1_out_valid => s146_valid,
        p0_in_valid => s134_valid,
        p1_out_last => s145_last,
        p0_out_ready => s135_ready,
        p0_in_last => s133_last,
        reset => reset,
        p1_in_ready => s147_ready,
        p1_out_data => s144_data,
        p0_in_data => s132_data
    );
    U37_id: id_382 port map(
        clk => clk,
        p1_out_valid => s138_valid,
        p0_in_valid => s178_data(7),
        p1_out_last => s137_last,
        p0_in_data => s176_data(7),
        p0_out_ready => s186_ready,
        p0_in_last => s177_data(7),
        reset => reset,
        p1_in_ready => s139_ready,
        p1_out_data => s136_data
    );
    U38_select: select_383 port map(
        clk => clk,
        p1_out_valid => s150_valid,
        p0_in_valid => s138_valid,
        p1_out_last => s149_last,
        p0_in_data => s136_data,
        p0_out_ready => s139_ready,
        p0_in_last => s137_last,
        reset => reset,
        p1_in_ready => s151_ready,
        p1_out_data => s148_data
    );
    U39_tuple: tuple_384 port map(
        clk => clk,
        p1_in_last => s145_last,
        p0_in_valid => s142_valid,
        p3_out_valid => s158_valid,
        p2_out_ready => s151_ready,
        p0_in_data => s140_data,
        p2_in_valid => s150_valid,
        p2_in_data => s148_data,
        p0_out_ready => s143_ready,
        p1_in_valid => s146_valid,
        p3_out_last => s157_last,
        p1_in_data => s144_data,
        p0_in_last => s141_last,
        reset => reset,
        p2_in_last => s149_last,
        p3_out_data => s156_data,
        p1_out_ready => s147_ready,
        p3_in_ready => s159_ready
    );
    U40_tuple: tuple_385 port map(
        clk => clk,
        p2_out_valid => s166_valid,
        p2_out_data => s164_data,
        p1_in_last => s157_last,
        p0_in_valid => s154_valid,
        p1_in_data => s156_data,
        p0_in_data => s152_data,
        p2_out_last => s165_last,
        p0_out_ready => s155_ready,
        p1_in_valid => s158_valid,
        p2_in_ready => s167_ready,
        p0_in_last => s153_last,
        reset => reset,
        p1_out_ready => s159_ready
    );
    U41_vectorgenerator: vectorgenerator_419 port map(
        p4_in_data => s176_data(11),
        p0_out_last => s161_last,
        p3_in_valid => s178_data(10),
        clk => clk,
        p5_out_ready => s191_ready,
        p1_in_last => s177_data(8),
        p4_in_last => s177_data(11),
        p0_out_data => s160_data,
        p5_in_data => s176_data(12),
        p2_out_ready => s188_ready,
        p2_in_data => s176_data(9),
        p1_in_data => s176_data(8),
        p0_in_ready => s163_ready,
        p2_in_valid => s178_data(9),
        p3_out_ready => s189_ready,
        p0_out_valid => s162_valid,
        p4_in_valid => s178_data(11),
        p5_in_last => s177_data(12),
        p1_in_valid => s178_data(8),
        p3_in_data => s176_data(10),
        p5_in_valid => s178_data(12),
        reset => reset,
        p2_in_last => s177_data(9),
        p4_out_ready => s190_ready,
        p3_in_last => s177_data(10),
        p1_out_ready => s187_ready
    );
    U42_conversion: conversion_420 port map(
        clk => clk,
        p1_out_valid => s170_valid,
        p0_in_valid => s162_valid,
        p1_out_last => s169_last,
        p0_out_ready => s163_ready,
        p0_in_last => s161_last,
        reset => reset,
        p1_in_ready => s171_ready,
        p0_in_data => s160_data,
        p1_out_data => s168_data
    );
    U43_index: index_421 port map(
        clk => clk,
        p2_out_valid => s174_valid,
        p1_in_last => s169_last,
        p0_in_valid => s166_valid,
        p0_in_data => s164_data,
        p2_out_data => s172_data,
        p1_in_data => s168_data,
        p2_out_last => s173_last,
        p0_out_ready => s167_ready,
        p1_in_valid => s170_valid,
        p2_in_ready => s175_ready,
        p0_in_last => s165_last,
        reset => reset,
        p1_out_ready => s171_ready
    );
    U44_registered: registered_422 port map(
        clk => clk,
        p1_out_valid => s198_valid,
        p0_in_valid => s174_valid,
        p1_out_last => s197_last,
        p0_in_data => s172_data,
        p0_out_ready => s175_ready,
        p0_in_last => s173_last,
        reset => reset,
        p1_in_ready => s199_ready,
        p1_out_data => s196_data
    );
    U45_distributor: distributor_423 port map(
        clk => clk,
        p3_out_data => s178_data,
        p1_out_data => s176_data,
        p0_in_valid => s194_valid,
        p0_in_data => s192_data,
        p0_out_ready => s195_ready,
        p2_out_data => s177_data,
        p4_in_data(0) => s179_ready,
        p4_in_data(1) => s180_ready,
        p4_in_data(2) => s181_ready,
        p4_in_data(3) => s182_ready,
        p4_in_data(4) => s183_ready,
        p4_in_data(5) => s184_ready,
        p4_in_data(6) => s185_ready,
        p4_in_data(7) => s186_ready,
        p4_in_data(8) => s187_ready,
        p4_in_data(9) => s188_ready,
        p4_in_data(10) => s189_ready,
        p4_in_data(11) => s190_ready,
        p4_in_data(12) => s191_ready,
        p0_in_last => s193_last,
        reset => reset
    );
    U46_id: id_424 port map(
        clk => clk,
        p1_out_valid => s194_valid,
        p0_in_valid => s202_valid,
        p1_out_last => s193_last,
        p0_in_data => s200_data,
        p0_out_ready => s203_ready,
        p0_in_last => s201_last,
        reset => reset,
        p1_in_ready => s195_ready,
        p1_out_data => s192_data
    );
    p1_out_data <= s196_data;
    p1_out_last <= "1" & s197_last when out_counter = stream_length - 1 else "0" & s197_last;
    p1_out_valid <= s198_valid;
    s199_ready <= p1_in_ready(s199_ready'high downto s199_ready'low);
    
    s200_data <= p0_in_data;
    s201_last <= p0_in_last(s201_last'high downto s201_last'low);
    s202_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s203_ready when in_counter = stream_length - 1 and s203_ready(s203_ready'high) = '1' else "0" & s203_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s202_valid = '1' and s203_ready(s203_ready'high) = '1' then
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
                if s198_valid = '1' and s199_ready(s199_ready'high) = '1' then
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
