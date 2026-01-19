library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity readsync_102 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_BaseAddrTypeArithType1BlockRamTypeIdType2;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_in_data: in type_IntTypeArithType12;
        p1_in_last: in type_LastVectorTypeArithType0;
        p1_in_valid: in type_LogicType;
        p1_out_ready: out type_ReadyVectorTypeArithType0;
        p2_out_data: out type_IntTypeArithType256;
        p2_out_last: out type_LastVectorTypeArithType0;
        p2_out_valid: out type_LogicType;
        p2_in_ready: in type_ReadyVectorTypeArithType0;
        p3_out_data: out type_IntTypeArithType13;
        p3_out_last: out type_LastVectorTypeArithType0;
        p3_out_valid: out type_LogicType;
        p3_in_ready: in type_ReadyVectorTypeArithType0;
        p4_in_data: in type_IntTypeArithType256;
        p4_in_last: in type_LastVectorTypeArithType0;
        p4_in_valid: in type_LogicType;
        p4_out_ready: out type_ReadyVectorTypeArithType0
    );
end readsync_102;

architecture behavioral of readsync_102 is
    
    component id_100
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
    component id_101
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
    
    
    
    signal s00_data: type_IntTypeArithType256;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_IntTypeArithType13;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
begin
    
    U0_id: id_100 port map(
        clk => clk,
        p1_out_valid => p3_out_valid,
        p0_in_valid => s06_valid,
        p1_out_last => p3_out_last,
        p0_in_data => s04_data,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => p3_in_ready,
        p1_out_data => p3_out_data
    );
    U1_id: id_101 port map(
        clk => clk,
        p1_out_valid => s02_valid,
        p0_in_valid => p4_in_valid,
        p1_out_last => s01_last,
        p0_out_ready => p4_out_ready,
        p0_in_last => p4_in_last,
        reset => reset,
        p1_in_ready => s03_ready,
        p0_in_data => p4_in_data,
        p1_out_data => s00_data
    );
    s04_data <= std_logic_vector(to_unsigned(
    to_integer(unsigned(p0_in_data)) +
    to_integer(unsigned(p1_in_data)),
    s04_data'length));
    s05_last <= p1_in_last;
    s06_valid <= p1_in_valid and p0_in_valid;
    p1_out_ready <= s07_ready when p0_in_valid = '1' else "0";
    p0_out_ready <= s07_ready when p1_in_valid = '1' else "0";
    
    p2_out_data <= s00_data;
    p2_out_last <= s01_last;
    p2_out_valid <= s02_valid;
    s03_ready <= p2_in_ready;
    
end behavioral;
