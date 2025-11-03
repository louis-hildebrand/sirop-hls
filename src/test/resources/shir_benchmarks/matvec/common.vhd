library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
package common is
    subtype type_ReadyVectorTypeArithType1 is std_logic_vector(1 downto 0);
    subtype type_LogicType is std_logic;
    subtype type_LastVectorTypeArithType1 is std_logic_vector(0 downto 0);
    subtype type_IntTypeArithType16 is std_logic_vector(15 downto 0);
    alias type_OrderedStreamTypeIntTypeArithType16ArithType256 is type_IntTypeArithType16;
    type type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType16ArithType256TextTypet1OrderedStreamTypeIntTypeArithType16ArithType256_t0 is record
        t0: type_OrderedStreamTypeIntTypeArithType16ArithType256;
        t1: type_OrderedStreamTypeIntTypeArithType16ArithType256;
    end record type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType16ArithType256TextTypet1OrderedStreamTypeIntTypeArithType16ArithType256_t0;
    subtype type_ReadyVectorTypeArithType0 is std_logic_vector(0 downto 0);
    subtype type_LastVectorTypeArithType0 is std_logic_vector(-1 downto 0);
    subtype type_IntTypeArithType24 is std_logic_vector(23 downto 0);
    type type_NamedTupleTypeTextTypet0IntTypeArithType24TextTypet1IntTypeArithType16_t0 is record
        t0: type_IntTypeArithType24;
        t1: type_IntTypeArithType16;
    end record type_NamedTupleTypeTextTypet0IntTypeArithType24TextTypet1IntTypeArithType16_t0;
    type type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0 is record
        t0: type_IntTypeArithType16;
        t1: type_IntTypeArithType16;
    end record type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0;
    alias type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType256 is type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0;
    subtype type_VectorTypeLogicTypeArithType1 is std_logic_vector(0 downto 0);
    subtype type_NaturalNumberType is natural;
    type type_VectorTypeNaturalNumberTypeArithType1 is array (0 downto 0) of type_NaturalNumberType;
    subtype type_VectorTypeLogicTypeArithType16 is std_logic_vector(15 downto 0);
    subtype type_VectorTypeLogicTypeArithType32 is std_logic_vector(31 downto 0);
    subtype type_IntTypeArithType32 is std_logic_vector(31 downto 0);
    subtype type_ReadyVectorTypeArithType2 is std_logic_vector(2 downto 0);
    subtype type_LastVectorTypeArithType2 is std_logic_vector(1 downto 0);
    alias type_OrderedStreamTypeIntTypeArithType24ArithType256 is type_IntTypeArithType24;
    alias type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType16ArithType256ArithType256 is type_OrderedStreamTypeIntTypeArithType16ArithType256;
    subtype type_VectorTypeLogicTypeArithType2 is std_logic_vector(1 downto 0);
    type type_VectorTypeNaturalNumberTypeArithType2 is array (1 downto 0) of type_NaturalNumberType;
end package;
