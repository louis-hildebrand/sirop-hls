library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
package common is
    subtype type_ReadyVectorTypeArithType2 is std_logic_vector(2 downto 0);
    subtype type_LogicType is std_logic;
    subtype type_LastVectorTypeArithType2 is std_logic_vector(1 downto 0);
    subtype type_IntTypeArithType8 is std_logic_vector(7 downto 0);
    alias type_OrderedStreamTypeIntTypeArithType8ArithType256 is type_IntTypeArithType8;
    subtype type_BaseAddrTypeArithType1BlockRamTypeIdType2 is std_logic_vector(0 downto 0);
    alias type_OrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType256 is type_BaseAddrTypeArithType1BlockRamTypeIdType2;
    type type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType8ArithType256TextTypet1OrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType256_t0 is record
        t0: type_OrderedStreamTypeIntTypeArithType8ArithType256;
        t1: type_OrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType256;
    end record type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType8ArithType256TextTypet1OrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType256_t0;
    alias type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType8ArithType256TextTypet1OrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType256ArithType256 is type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType8ArithType256TextTypet1OrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType256_t0;
    alias type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType8ArithType256ArithType256 is type_OrderedStreamTypeIntTypeArithType8ArithType256;
    alias type_OrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType256ArithType256 is type_OrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType256;
    type type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType8ArithType256ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType256ArithType256_t0 is record
        t0: type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType8ArithType256ArithType256;
        t1: type_OrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType256ArithType256;
    end record type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType8ArithType256ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType256ArithType256_t0;
    subtype type_ReadyVectorTypeArithType0 is std_logic_vector(0 downto 0);
    subtype type_LastVectorTypeArithType0 is std_logic_vector(-1 downto 0);
    type type_NamedTupleTypeTextTypet0IntTypeArithType8TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType2_t0 is record
        t0: type_IntTypeArithType8;
        t1: type_BaseAddrTypeArithType1BlockRamTypeIdType2;
    end record type_NamedTupleTypeTextTypet0IntTypeArithType8TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType2_t0;
    subtype type_IntTypeArithType16 is std_logic_vector(15 downto 0);
    subtype type_IntTypeArithType9 is std_logic_vector(8 downto 0);
    subtype type_NaturalNumberType is natural;
    type type_VectorTypeReadyVectorTypeArithType0ArithType2 is array (1 downto 0) of type_ReadyVectorTypeArithType0;
    subtype type_VectorTypeLogicTypeArithType2 is std_logic_vector(1 downto 0);
    type type_VectorTypeLastVectorTypeArithType0ArithType2 is array (1 downto 0) of type_LastVectorTypeArithType0;
    type type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType8TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType2ArithType2 is array (1 downto 0) of type_NamedTupleTypeTextTypet0IntTypeArithType8TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType2_t0;
    subtype type_ReadyVectorTypeArithType1 is std_logic_vector(1 downto 0);
    subtype type_LastVectorTypeArithType1 is std_logic_vector(0 downto 0);
    alias type_OrderedStreamTypeIntTypeArithType16ArithType256 is type_IntTypeArithType16;
    alias type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType8TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType2ArithType256 is type_NamedTupleTypeTextTypet0IntTypeArithType8TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType2_t0;
    alias type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType16ArithType256ArithType256 is type_OrderedStreamTypeIntTypeArithType16ArithType256;
    type type_NamedTupleTypeTextTypeaddrIntTypeArithType9TextTypedataIntTypeArithType16TextTypeweLogicType_addr is record
        addr: type_IntTypeArithType9;
        data: type_IntTypeArithType16;
        we: type_LogicType;
    end record type_NamedTupleTypeTextTypeaddrIntTypeArithType9TextTypedataIntTypeArithType16TextTypeweLogicType_addr;
    subtype type_UnitType is std_logic_vector(-1 downto 0);
    type type_NamedTupleTypeTextTypeaddrIntTypeArithType9TextTypedataIntTypeArithType16_addr is record
        addr: type_IntTypeArithType9;
        data: type_IntTypeArithType16;
    end record type_NamedTupleTypeTextTypeaddrIntTypeArithType9TextTypedataIntTypeArithType16_addr;
    type type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType16ArithType256TextTypet1OrderedStreamTypeIntTypeArithType16ArithType256_t0 is record
        t0: type_OrderedStreamTypeIntTypeArithType16ArithType256;
        t1: type_OrderedStreamTypeIntTypeArithType16ArithType256;
    end record type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType16ArithType256TextTypet1OrderedStreamTypeIntTypeArithType16ArithType256_t0;
    alias type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType16ArithType256TextTypet1OrderedStreamTypeIntTypeArithType16ArithType256ArithType256 is type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType16ArithType256TextTypet1OrderedStreamTypeIntTypeArithType16ArithType256_t0;
    type type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType16ArithType256ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeIntTypeArithType16ArithType256ArithType256_t0 is record
        t0: type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType16ArithType256ArithType256;
        t1: type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType16ArithType256ArithType256;
    end record type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType16ArithType256ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeIntTypeArithType16ArithType256ArithType256_t0;
    type type_VectorTypeNaturalNumberTypeArithType2 is array (1 downto 0) of type_NaturalNumberType;
    subtype type_VectorTypeLogicTypeArithType1 is std_logic_vector(0 downto 0);
    type type_UnorderedStreamTypeIntTypeArithType16ArithType256 is record
        data: type_IntTypeArithType16;
        idx: type_IntTypeArithType8;
    end record type_UnorderedStreamTypeIntTypeArithType16ArithType256;
    type type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType8_t0 is record
        t0: type_IntTypeArithType16;
        t1: type_IntTypeArithType8;
    end record type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType8_t0;
    alias type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType8ArithType256 is type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType8_t0;
    subtype type_IntTypeArithType24 is std_logic_vector(23 downto 0);
    type type_NamedTupleTypeTextTypet0IntTypeArithType24TextTypet1IntTypeArithType16_t0 is record
        t0: type_IntTypeArithType24;
        t1: type_IntTypeArithType16;
    end record type_NamedTupleTypeTextTypet0IntTypeArithType24TextTypet1IntTypeArithType16_t0;
    subtype type_VectorTypeLogicTypeArithType16 is std_logic_vector(15 downto 0);
    subtype type_VectorTypeLogicTypeArithType32 is std_logic_vector(31 downto 0);
    subtype type_IntTypeArithType32 is std_logic_vector(31 downto 0);
    type type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0 is record
        t0: type_IntTypeArithType16;
        t1: type_IntTypeArithType16;
    end record type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0;
    alias type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType256 is type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0;
    alias type_OrderedStreamTypeIntTypeArithType24ArithType256 is type_IntTypeArithType24;
    type type_VectorTypeIntTypeArithType16ArithType2 is array (1 downto 0) of type_IntTypeArithType16;
    type type_VectorTypeNamedTupleTypeTextTypeaddrIntTypeArithType9TextTypedataIntTypeArithType16TextTypeweLogicTypeArithType2 is array (1 downto 0) of type_NamedTupleTypeTextTypeaddrIntTypeArithType9TextTypedataIntTypeArithType16TextTypeweLogicType_addr;
end package;
