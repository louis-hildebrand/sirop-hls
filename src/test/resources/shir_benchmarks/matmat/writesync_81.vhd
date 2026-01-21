library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity writesync_81 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_BaseAddrTypeArithType1BlockRamTypeIdType2;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType256TextTypet1IntTypeArithType12_t0;
        p1_in_last: in type_LastVectorTypeArithType0;
        p1_in_valid: in type_LogicType;
        p1_out_ready: out type_ReadyVectorTypeArithType0;
        p2_out_data: out type_BaseAddrTypeArithType1BlockRamTypeIdType2;
        p2_out_last: out type_LastVectorTypeArithType0;
        p2_out_valid: out type_LogicType;
        p2_in_ready: in type_ReadyVectorTypeArithType0;
        p3_out_data: out type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256_addr;
        p3_out_last: out type_LastVectorTypeArithType0;
        p3_out_valid: out type_LogicType;
        p3_in_ready: in type_ReadyVectorTypeArithType0;
        p4_in_data: in type_UnitType;
        p4_in_last: in type_LastVectorTypeArithType0;
        p4_in_valid: in type_LogicType;
        p4_out_ready: out type_ReadyVectorTypeArithType0
    );
end writesync_81;

architecture behavioral of writesync_81 is
    
    component id_79
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
    component id_80
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
    
    
    
    signal s00_data: type_UnitType;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256_addr;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
begin
    
    U0_id: id_79 port map(
        clk => clk,
        p1_out_valid => p3_out_valid,
        p0_in_valid => s06_valid,
        p1_out_data => p3_out_data,
        p1_out_last => p3_out_last,
        p0_out_ready => s07_ready,
        p0_in_data => s04_data,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => p3_in_ready
    );
    U1_id: id_80 port map(
        clk => clk,
        p0_in_data => p4_in_data,
        p1_out_valid => s02_valid,
        p0_in_valid => p4_in_valid,
        p1_out_last => s01_last,
        p0_out_ready => p4_out_ready,
        p0_in_last => p4_in_last,
        reset => reset,
        p1_out_data => s00_data,
        p1_in_ready => s03_ready
    );
    s04_data.data <= p1_in_data.t0;
    s04_data.addr <= std_logic_vector(to_unsigned(
    to_integer(unsigned(p0_in_data)) +
    to_integer(unsigned(p1_in_data.t1)),
    s04_data.addr'length));
    s05_last <= p1_in_last;
    s06_valid <= p1_in_valid and p0_in_valid;
    p1_out_ready <= s07_ready when p0_in_valid = '1' else "0";
    p0_out_ready <= s07_ready when p1_in_valid = '1' else "0";
    
    p2_out_data <= p0_in_data;
    p2_out_last <= p0_in_last;
    p2_out_valid <= s02_valid;
    s03_ready <= p2_in_ready;
    
end behavioral;
