library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity readsyncmemorycontroller_7 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_IntTypeArithType5;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_IntTypeArithType256;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0;
        p2_out_data: out type_NamedTupleTypeTextTypeaddrIntTypeArithType5TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
        p2_out_last: out type_LastVectorTypeArithType0;
        p2_out_valid: out type_LogicType;
        p2_in_ready: in type_ReadyVectorTypeArithType0;
        p3_in_data: in type_IntTypeArithType256;
        p3_in_last: in type_LastVectorTypeArithType0;
        p3_in_valid: in type_LogicType;
        p3_out_ready: out type_ReadyVectorTypeArithType0
    );
end readsyncmemorycontroller_7;

architecture behavioral of readsyncmemorycontroller_7 is
    
    component id_5
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypeaddrIntTypeArithType5TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypeaddrIntTypeArithType5TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_6
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
    signal s04_data: type_NamedTupleTypeTextTypeaddrIntTypeArithType5TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
begin
    
    U0_id: id_5 port map(
        clk => clk,
        p1_out_data => p2_out_data,
        p1_out_valid => p2_out_valid,
        p0_in_valid => s06_valid,
        p1_out_last => p2_out_last,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => p2_in_ready,
        p0_in_data => s04_data
    );
    U1_id: id_6 port map(
        clk => clk,
        p1_out_valid => s02_valid,
        p0_in_valid => p3_in_valid,
        p1_out_last => s01_last,
        p0_out_ready => p3_out_ready,
        p0_in_last => p3_in_last,
        reset => reset,
        p1_in_ready => s03_ready,
        p0_in_data => p3_in_data,
        p1_out_data => s00_data
    );
    -- s04_data.data <= (others => '0'); -- not generic! does not work for vector types!
    s04_data.addr <= p0_in_data;
    s04_data.we <= '0';
    s05_last <= p0_in_last;
    s06_valid <= p0_in_valid;
    p0_out_ready <= s07_ready;
    
    p1_out_data <= s00_data;
    p1_out_last <= s01_last;
    p1_out_valid <= s02_valid;
    s03_ready <= p1_in_ready;
    
end behavioral;
