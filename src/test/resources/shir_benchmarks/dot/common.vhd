library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
package common is
    subtype type_ReadyVectorTypeArithType0 is std_logic_vector(0 downto 0);
    subtype type_LogicType is std_logic;
    subtype type_LastVectorTypeArithType0 is std_logic_vector(-1 downto 0);
    subtype type_IntTypeArithType26 is std_logic_vector(25 downto 0);
    subtype type_IntTypeArithType16 is std_logic_vector(15 downto 0);
    type type_NamedTupleTypeTextTypet0IntTypeArithType26TextTypet1IntTypeArithType16_t0 is record
        t0: type_IntTypeArithType26;
        t1: type_IntTypeArithType16;
    end record type_NamedTupleTypeTextTypet0IntTypeArithType26TextTypet1IntTypeArithType16_t0;
    subtype type_ReadyVectorTypeArithType1 is std_logic_vector(1 downto 0);
    subtype type_LastVectorTypeArithType1 is std_logic_vector(0 downto 0);
    alias type_OrderedStreamTypeIntTypeArithType16ArithType840 is type_IntTypeArithType16;
    subtype type_VectorTypeLogicTypeArithType16 is std_logic_vector(15 downto 0);
    subtype type_NaturalNumberType is natural;
    subtype type_VectorTypeLogicTypeArithType32 is std_logic_vector(31 downto 0);
    subtype type_IntTypeArithType32 is std_logic_vector(31 downto 0);
    type type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0 is record
        t0: type_IntTypeArithType16;
        t1: type_IntTypeArithType16;
    end record type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0;
    alias type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType840 is type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0;
    type type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType16ArithType840TextTypet1OrderedStreamTypeIntTypeArithType16ArithType840_t0 is record
        t0: type_OrderedStreamTypeIntTypeArithType16ArithType840;
        t1: type_OrderedStreamTypeIntTypeArithType16ArithType840;
    end record type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType16ArithType840TextTypet1OrderedStreamTypeIntTypeArithType16ArithType840_t0;
end package;
