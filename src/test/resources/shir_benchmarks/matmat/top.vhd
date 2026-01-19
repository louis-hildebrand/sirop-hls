library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity top is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256;
        p0_in_last: in type_LastVectorTypeArithType2;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType2;
        p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType20ArithType256ArithType256;
        p1_out_last: out type_LastVectorTypeArithType2;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType2;
        p2_in_data: in type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256;
        p2_in_last: in type_LastVectorTypeArithType2;
        p2_in_valid: in type_LogicType;
        p2_out_ready: out type_ReadyVectorTypeArithType2
    );
end top;

architecture behavioral of top is
    
    component id_114
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType13;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType13;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component writesyncmemorycontroller_69
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256_addr;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_UnitType;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0;
            p3_in_data: in type_IntTypeArithType256;
            p3_in_last: in type_LastVectorTypeArithType0;
            p3_in_valid: in type_LogicType;
            p3_out_ready: out type_ReadyVectorTypeArithType0
        );
    end component;
    component constantvalue_71
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_BaseAddrTypeArithType1BlockRamTypeIdType2;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component zipstream_132
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16ArithType256ArithType256_t0;
            p0_in_last: in type_LastVectorTypeArithType3;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType3;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16ArithType256ArithType256;
            p1_out_last: out type_LastVectorTypeArithType3;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType3
        );
    end component;
    component tuple_90
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType12ArithType16ArithType256ArithType256;
            p0_in_last: in type_LastVectorTypeArithType3;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType3;
            p1_in_data: in type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16ArithType256ArithType256;
            p1_in_last: in type_LastVectorTypeArithType3;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType3;
            p2_out_data: out type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType12ArithType16ArithType256ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16ArithType256ArithType256_t0;
            p2_out_last: out type_LastVectorTypeArithType3;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType3
        );
    end component;
    component id_118
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType256;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType256;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component counterinteger_88
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType12ArithType16ArithType256ArithType256;
            p0_out_last: out type_LastVectorTypeArithType3;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType3
        );
    end component;
    component id_111
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_BaseAddrTypeArithType1BlockRamTypeIdType2;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_BaseAddrTypeArithType1BlockRamTypeIdType2;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_379
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component id_117
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapsimpleorderedstream_109
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType12ArithType16ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16ArithType256ArithType256;
            p0_in_last: in type_LastVectorTypeArithType3;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType3;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256ArithType256;
            p1_out_last: out type_LastVectorTypeArithType3;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType3;
            p2_out_data: out type_IntTypeArithType13;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0;
            p3_in_data: in type_IntTypeArithType256;
            p3_in_last: in type_LastVectorTypeArithType0;
            p3_in_valid: in type_LogicType;
            p3_out_ready: out type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_131
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256ArithType256;
            p0_in_last: in type_LastVectorTypeArithType3;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType3;
            p1_in_data: in type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16ArithType256ArithType256;
            p1_in_last: in type_LastVectorTypeArithType3;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType3;
            p2_out_data: out type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16ArithType256ArithType256_t0;
            p2_out_last: out type_LastVectorTypeArithType3;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType3
        );
    end component;
    component id_112
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256_addr;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256_addr;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component arbitersyncfunction_116
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
            p0_out_last: out type_LastVectorTypeArithType0;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType0;
            p1_in_data: in type_IntTypeArithType256;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_in_data: in type_VectorTypeNamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256TextTypeweLogicTypeArithType2;
            p3_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType2;
            p4_in_data: in type_VectorTypeLogicTypeArithType2;
            p5_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType2;
            p6_out_data: out type_VectorTypeIntTypeArithType256ArithType2;
            p7_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType2;
            p8_out_data: out type_VectorTypeLogicTypeArithType2;
            p9_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType2
        );
    end component;
    component mapsimpleorderedstream_57
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256ArithType256;
            p1_out_last: out type_LastVectorTypeArithType3;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType3
        );
    end component;
    component id_115
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType256;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType256;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapsimpleorderedstream_377
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16ArithType256ArithType256;
            p0_in_last: in type_LastVectorTypeArithType3;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType3;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType20ArithType256ArithType256;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component readsyncmemorycontroller_64
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType13;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType256;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0;
            p3_in_data: in type_IntTypeArithType256;
            p3_in_last: in type_LastVectorTypeArithType0;
            p3_in_valid: in type_LogicType;
            p3_out_ready: out type_ReadyVectorTypeArithType0
        );
    end component;
    component id_113
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_UnitType;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_UnitType;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component zipstream_91
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType12ArithType16ArithType256ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16ArithType256ArithType256_t0;
            p0_in_last: in type_LastVectorTypeArithType3;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType3;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType12ArithType16ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16ArithType256ArithType256;
            p1_out_last: out type_LastVectorTypeArithType3;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType3
        );
    end component;
    component repeat_87
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16ArithType256ArithType256;
            p0_out_last: out type_LastVectorTypeArithType3;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType3;
            p1_in_data: in type_BaseAddrTypeArithType1BlockRamTypeIdType2;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_in_data: in type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256;
            p2_in_last: in type_LastVectorTypeArithType2;
            p2_in_valid: in type_LogicType;
            p2_out_ready: out type_ReadyVectorTypeArithType2;
            p3_out_data: out type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256_addr;
            p3_out_last: out type_LastVectorTypeArithType0;
            p3_out_valid: out type_LogicType;
            p3_in_ready: in type_ReadyVectorTypeArithType0;
            p4_in_data: in type_UnitType;
            p4_in_last: in type_LastVectorTypeArithType0;
            p4_in_valid: in type_LogicType;
            p4_out_ready: out type_ReadyVectorTypeArithType0
        );
    end component;
    component blockram_59
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType256;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component mapsimpleorderedstream_130
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256ArithType256;
            p0_in_last: in type_LastVectorTypeArithType3;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType3;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16ArithType256ArithType256;
            p1_out_last: out type_LastVectorTypeArithType3;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType3
        );
    end component;
    
    
    
    signal s00_data: type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType12ArithType16ArithType256ArithType256;
    signal s01_last: type_LastVectorTypeArithType3;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType3;
    signal s04_data: type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType12ArithType16ArithType256ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16ArithType256ArithType256_t0;
    signal s05_last: type_LastVectorTypeArithType3;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType3;
    signal s08_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType12ArithType16ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16ArithType256ArithType256;
    signal s09_last: type_LastVectorTypeArithType3;
    signal s10_valid: type_LogicType;
    signal s100_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16ArithType256ArithType256;
    signal s101_last: type_LastVectorTypeArithType3;
    signal s102_valid: type_LogicType;
    signal s103_ready: type_ReadyVectorTypeArithType3;
    signal s104_data: type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256;
    signal s105_last: type_LastVectorTypeArithType2;
    signal s106_valid: type_LogicType;
    signal s107_ready: type_ReadyVectorTypeArithType2;
    signal s11_ready: type_ReadyVectorTypeArithType3;
    signal s12_data: type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16ArithType256ArithType256;
    signal s13_last: type_LastVectorTypeArithType3;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType3;
    signal s16_data: type_BaseAddrTypeArithType1BlockRamTypeIdType2;
    signal s17_last: type_LastVectorTypeArithType0;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType0;
    signal s20_data: type_BaseAddrTypeArithType1BlockRamTypeIdType2;
    signal s21_last: type_LastVectorTypeArithType0;
    signal s22_valid: type_LogicType;
    signal s23_ready: type_ReadyVectorTypeArithType0;
    signal s24_data: type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256_addr;
    signal s25_last: type_LastVectorTypeArithType0;
    signal s26_valid: type_LogicType;
    signal s27_ready: type_ReadyVectorTypeArithType0;
    signal s28_data: type_UnitType;
    signal s29_last: type_LastVectorTypeArithType0;
    signal s30_valid: type_LogicType;
    signal s31_ready: type_ReadyVectorTypeArithType0;
    signal s32_data: type_UnitType;
    signal s33_last: type_LastVectorTypeArithType0;
    signal s34_valid: type_LogicType;
    signal s35_ready: type_ReadyVectorTypeArithType0;
    signal s36_data: type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256_addr;
    signal s37_last: type_LastVectorTypeArithType0;
    signal s38_valid: type_LogicType;
    signal s39_ready: type_ReadyVectorTypeArithType0;
    signal s40_data: type_IntTypeArithType13;
    signal s41_last: type_LastVectorTypeArithType0;
    signal s42_valid: type_LogicType;
    signal s43_ready: type_ReadyVectorTypeArithType0;
    signal s44_data: type_IntTypeArithType256;
    signal s45_last: type_LastVectorTypeArithType0;
    signal s46_valid: type_LogicType;
    signal s47_ready: type_ReadyVectorTypeArithType0;
    signal s48_data: type_IntTypeArithType256;
    signal s49_last: type_LastVectorTypeArithType0;
    signal s50_valid: type_LogicType;
    signal s51_ready: type_ReadyVectorTypeArithType0;
    signal s52_data: type_IntTypeArithType13;
    signal s53_last: type_LastVectorTypeArithType0;
    signal s54_valid: type_LogicType;
    signal s55_ready: type_ReadyVectorTypeArithType0;
    signal s56_data: type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
    signal s57_last: type_LastVectorTypeArithType0;
    signal s58_valid: type_LogicType;
    signal s59_data: type_VectorTypeReadyVectorTypeArithType0ArithType2;
    signal s60_data: type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
    signal s61_last: type_LastVectorTypeArithType0;
    signal s62_valid: type_LogicType;
    signal s63_data: type_VectorTypeIntTypeArithType256ArithType2;
    signal s64_data: type_VectorTypeLastVectorTypeArithType0ArithType2;
    signal s65_data: type_VectorTypeLogicTypeArithType2;
    signal s66_ready: type_ReadyVectorTypeArithType0;
    signal s67_ready: type_ReadyVectorTypeArithType0;
    signal s68_data: type_IntTypeArithType256;
    signal s69_last: type_LastVectorTypeArithType0;
    signal s70_valid: type_LogicType;
    signal s71_ready: type_ReadyVectorTypeArithType0;
    signal s72_data: type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
    signal s73_last: type_LastVectorTypeArithType0;
    signal s74_valid: type_LogicType;
    signal s75_ready: type_ReadyVectorTypeArithType0;
    signal s76_data: type_IntTypeArithType256;
    signal s77_last: type_LastVectorTypeArithType0;
    signal s78_valid: type_LogicType;
    signal s79_ready: type_ReadyVectorTypeArithType0;
    signal s80_data: type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
    signal s81_last: type_LastVectorTypeArithType0;
    signal s82_valid: type_LogicType;
    signal s83_ready: type_ReadyVectorTypeArithType0;
    signal s84_data: type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256ArithType256;
    signal s85_last: type_LastVectorTypeArithType3;
    signal s86_valid: type_LogicType;
    signal s87_ready: type_ReadyVectorTypeArithType3;
    signal s88_data: type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256ArithType256;
    signal s89_last: type_LastVectorTypeArithType3;
    signal s90_valid: type_LogicType;
    signal s91_ready: type_ReadyVectorTypeArithType3;
    signal s92_data: type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16ArithType256ArithType256;
    signal s93_last: type_LastVectorTypeArithType3;
    signal s94_valid: type_LogicType;
    signal s95_ready: type_ReadyVectorTypeArithType3;
    signal s96_data: type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16ArithType256ArithType256_t0;
    signal s97_last: type_LastVectorTypeArithType3;
    signal s98_valid: type_LogicType;
    signal s99_ready: type_ReadyVectorTypeArithType3;
begin
    
    U0_mapsimpleorderedstream: mapsimpleorderedstream_57 port map(
        clk => clk,
        p0_out_ready => p0_out_ready,
        p1_out_valid => s90_valid,
        p0_in_valid => p0_in_valid,
        p0_in_data => p0_in_data,
        p1_out_data => s88_data,
        p1_out_last => s89_last,
        p0_in_last => p0_in_last,
        reset => reset,
        p1_in_ready => s91_ready
    );
    U1_blockram: blockram_59 port map(
        clk => clk,
        p0_in_data => s80_data,
        p1_out_valid => s78_valid,
        p0_in_valid => s82_valid,
        p1_out_last => s77_last,
        p0_out_ready => s83_ready,
        p0_in_last => s81_last,
        reset => reset,
        p1_in_ready => s79_ready,
        p1_out_data => s76_data
    );
    U2_readsyncmemorycontroller: readsyncmemorycontroller_64 port map(
        p3_in_valid => s65_data(0),
        clk => clk,
        p2_out_valid => s58_valid,
        p1_out_valid => s50_valid,
        p0_in_valid => s54_valid,
        p1_out_last => s49_last,
        p0_in_data => s52_data,
        p3_out_ready => s66_ready,
        p2_out_last => s57_last,
        p0_out_ready => s55_ready,
        p2_in_ready => s59_data(0),
        p2_out_data => s56_data,
        p0_in_last => s53_last,
        reset => reset,
        p1_in_ready => s51_ready,
        p3_in_last => s64_data(0),
        p3_in_data => s63_data(0),
        p1_out_data => s48_data
    );
    U3_writesyncmemorycontroller: writesyncmemorycontroller_69 port map(
        p3_in_valid => s65_data(1),
        clk => clk,
        p2_out_valid => s62_valid,
        p1_out_valid => s34_valid,
        p0_in_valid => s38_valid,
        p1_out_last => s33_last,
        p3_out_ready => s67_ready,
        p2_out_last => s61_last,
        p0_out_ready => s39_ready,
        p0_in_data => s36_data,
        p2_in_ready => s59_data(1),
        p2_out_data => s60_data,
        p0_in_last => s37_last,
        reset => reset,
        p1_out_data => s32_data,
        p1_in_ready => s35_ready,
        p3_in_last => s64_data(1),
        p3_in_data => s63_data(1)
    );
    U4_constantvalue: constantvalue_71 port map(
        p0_out_last => s21_last,
        clk => clk,
        p0_in_ready => s23_ready,
        p0_out_valid => s22_valid,
        reset => reset,
        p0_out_data => s20_data
    );
    U5_repeat: repeat_87 port map(
        clk => clk,
        p1_in_data => s16_data,
        p1_in_last => s17_last,
        p2_in_last => s105_last,
        p0_out_last => s13_last,
        p4_in_last => s29_last,
        p3_out_valid => s26_valid,
        p3_out_data => s24_data,
        p2_in_valid => s106_valid,
        p0_out_valid => s14_valid,
        p4_in_valid => s30_valid,
        p2_out_ready => s107_ready,
        p2_in_data => s104_data,
        p1_in_valid => s18_valid,
        p3_out_last => s25_last,
        reset => reset,
        p4_out_ready => s31_ready,
        p0_out_data => s12_data,
        p0_in_ready => s15_ready,
        p4_in_data => s28_data,
        p1_out_ready => s19_ready,
        p3_in_ready => s27_ready
    );
    U6_counterinteger: counterinteger_88 port map(
        clk => clk,
        p0_out_last => s01_last,
        p0_out_valid => s02_valid,
        p0_out_data => s00_data,
        reset => reset,
        p0_in_ready => s03_ready
    );
    U7_tuple: tuple_90 port map(
        p2_out_last => s05_last,
        clk => clk,
        p2_out_valid => s06_valid,
        p1_in_last => s13_last,
        p0_in_valid => s02_valid,
        p0_in_data => s00_data,
        p1_in_data => s12_data,
        p0_out_ready => s03_ready,
        p1_out_ready => s15_ready,
        p0_in_last => s01_last,
        p1_in_valid => s14_valid,
        p2_in_ready => s07_ready,
        p2_out_data => s04_data,
        reset => reset
    );
    U8_zipstream: zipstream_91 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s06_valid,
        p0_in_data => s04_data,
        p1_out_data => s08_data,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        p1_out_last => s09_last,
        reset => reset,
        p1_in_ready => s11_ready
    );
    U9_mapsimpleorderedstream: mapsimpleorderedstream_109 port map(
        p3_in_valid => s46_valid,
        clk => clk,
        p2_out_valid => s42_valid,
        p1_out_valid => s86_valid,
        p0_in_valid => s10_valid,
        p1_out_data => s84_data,
        p0_out_ready => s11_ready,
        p3_out_ready => s47_ready,
        p2_out_last => s41_last,
        p0_in_last => s09_last,
        p1_out_last => s85_last,
        p2_out_data => s40_data,
        p2_in_ready => s43_ready,
        reset => reset,
        p3_in_last => s45_last,
        p1_in_ready => s87_ready,
        p3_in_data => s44_data,
        p0_in_data => s08_data
    );
    U10_id: id_111 port map(
        clk => clk,
        p1_out_valid => s18_valid,
        p0_in_valid => s22_valid,
        p1_out_last => s17_last,
        p0_out_ready => s23_ready,
        p0_in_last => s21_last,
        reset => reset,
        p1_out_data => s16_data,
        p1_in_ready => s19_ready,
        p0_in_data => s20_data
    );
    U11_id: id_112 port map(
        clk => clk,
        p1_out_valid => s38_valid,
        p0_in_valid => s26_valid,
        p1_out_data => s36_data,
        p1_out_last => s37_last,
        p0_out_ready => s27_ready,
        p0_in_data => s24_data,
        p0_in_last => s25_last,
        reset => reset,
        p1_in_ready => s39_ready
    );
    U12_id: id_113 port map(
        clk => clk,
        p0_in_data => s32_data,
        p1_out_valid => s30_valid,
        p0_in_valid => s34_valid,
        p1_out_last => s29_last,
        p0_out_ready => s35_ready,
        p0_in_last => s33_last,
        reset => reset,
        p1_out_data => s28_data,
        p1_in_ready => s31_ready
    );
    U13_id: id_114 port map(
        clk => clk,
        p1_out_valid => s54_valid,
        p0_in_valid => s42_valid,
        p1_out_last => s53_last,
        p0_in_data => s40_data,
        p0_out_ready => s43_ready,
        p0_in_last => s41_last,
        reset => reset,
        p1_in_ready => s55_ready,
        p1_out_data => s52_data
    );
    U14_id: id_115 port map(
        clk => clk,
        p1_out_valid => s46_valid,
        p0_in_valid => s50_valid,
        p1_out_last => s45_last,
        p0_out_ready => s51_ready,
        p0_in_last => s49_last,
        reset => reset,
        p1_in_ready => s47_ready,
        p0_in_data => s48_data,
        p1_out_data => s44_data
    );
    U15_arbitersyncfunction: arbitersyncfunction_116 port map(
        p0_out_last => s73_last,
        clk => clk,
        p1_in_last => s69_last,
        p3_in_data(0) => s57_last,
        p3_in_data(1) => s61_last,
        p1_in_data => s68_data,
        p0_out_data => s72_data,
        p2_in_data(0) => s56_data,
        p2_in_data(1) => s60_data,
        p7_out_data => s64_data,
        p6_out_data => s63_data,
        p8_out_data => s65_data,
        p5_out_data => s59_data,
        p0_in_ready => s75_ready,
        p0_out_valid => s74_valid,
        p1_in_valid => s70_valid,
        p4_in_data(0) => s58_valid,
        p4_in_data(1) => s62_valid,
        reset => reset,
        p9_in_data(0) => s66_ready,
        p9_in_data(1) => s67_ready,
        p1_out_ready => s71_ready
    );
    U16_id: id_117 port map(
        clk => clk,
        p0_in_data => s72_data,
        p1_out_valid => s82_valid,
        p0_in_valid => s74_valid,
        p1_out_data => s80_data,
        p1_out_last => s81_last,
        p0_out_ready => s75_ready,
        p0_in_last => s73_last,
        reset => reset,
        p1_in_ready => s83_ready
    );
    U17_id: id_118 port map(
        clk => clk,
        p1_out_valid => s70_valid,
        p0_in_valid => s78_valid,
        p1_out_last => s69_last,
        p0_out_ready => s79_ready,
        p0_in_last => s77_last,
        reset => reset,
        p1_in_ready => s71_ready,
        p0_in_data => s76_data,
        p1_out_data => s68_data
    );
    U18_mapsimpleorderedstream: mapsimpleorderedstream_130 port map(
        clk => clk,
        p1_out_valid => s94_valid,
        p0_in_valid => s86_valid,
        p0_in_data => s84_data,
        p0_out_ready => s87_ready,
        p0_in_last => s85_last,
        p1_out_last => s93_last,
        reset => reset,
        p1_out_data => s92_data,
        p1_in_ready => s95_ready
    );
    U19_tuple: tuple_131 port map(
        p2_out_last => s97_last,
        clk => clk,
        p2_out_valid => s98_valid,
        p1_in_last => s93_last,
        p0_in_valid => s90_valid,
        p1_in_data => s92_data,
        p0_in_data => s88_data,
        p0_out_ready => s91_ready,
        p1_out_ready => s95_ready,
        p0_in_last => s89_last,
        p1_in_valid => s94_valid,
        p2_in_ready => s99_ready,
        reset => reset,
        p2_out_data => s96_data
    );
    U20_zipstream: zipstream_132 port map(
        clk => clk,
        p1_out_valid => s102_valid,
        p0_in_valid => s98_valid,
        p0_out_ready => s99_ready,
        p0_in_data => s96_data,
        p0_in_last => s97_last,
        p1_out_last => s101_last,
        p1_out_data => s100_data,
        reset => reset,
        p1_in_ready => s103_ready
    );
    U21_mapsimpleorderedstream: mapsimpleorderedstream_377 port map(
        clk => clk,
        p1_out_valid => p1_out_valid,
        p0_in_valid => s102_valid,
        p1_out_data => p1_out_data,
        p0_out_ready => s103_ready,
        p0_in_data => s100_data,
        p0_in_last => s101_last,
        p1_out_last => p1_out_last,
        reset => reset,
        p1_in_ready => p1_in_ready
    );
    U22_id: id_379 port map(
        clk => clk,
        p0_out_ready => p2_out_ready,
        p1_out_valid => s106_valid,
        p0_in_valid => p2_in_valid,
        p0_in_data => p2_in_data,
        p1_out_data => s104_data,
        p0_in_last => p2_in_last,
        p1_out_last => s105_last,
        reset => reset,
        p1_in_ready => s107_ready
    );
    
    
end behavioral;
