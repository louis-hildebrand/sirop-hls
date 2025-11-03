library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
package common is
    subtype type_ReadyVectorTypeArithType0 is std_logic_vector(0 downto 0);
    subtype type_LogicType is std_logic;
    subtype type_LastVectorTypeArithType0 is std_logic_vector(-1 downto 0);
    subtype type_IntTypeArithType8 is std_logic_vector(7 downto 0);
    subtype type_IntTypeArithType3 is std_logic_vector(2 downto 0);
    type type_NamedTupleTypeTextTypet0IntTypeArithType8TextTypet1IntTypeArithType3_t0 is record
        t0: type_IntTypeArithType8;
        t1: type_IntTypeArithType3;
    end record type_NamedTupleTypeTextTypet0IntTypeArithType8TextTypet1IntTypeArithType3_t0;
    subtype type_VectorTypeLogicTypeArithType3 is std_logic_vector(2 downto 0);
    subtype type_ReadyVectorTypeArithType1 is std_logic_vector(1 downto 0);
    subtype type_LastVectorTypeArithType1 is std_logic_vector(0 downto 0);
    subtype type_NaturalNumberType is natural;
    alias type_OrderedStreamTypeIntTypeArithType8ArithType200 is type_IntTypeArithType8;
end package;
