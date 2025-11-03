library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity repeat_28 is
    generic(
        stream_length: type_NaturalNumberType := 256
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_out_data: out type_OrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType256ArithType256;
        p0_out_last: out type_LastVectorTypeArithType2;
        p0_out_valid: out type_LogicType;
        p0_in_ready: in type_ReadyVectorTypeArithType2;
        p1_in_data: in type_BaseAddrTypeArithType1BlockRamTypeIdType2;
        p1_in_last: in type_LastVectorTypeArithType0;
        p1_in_valid: in type_LogicType;
        p1_out_ready: out type_ReadyVectorTypeArithType0;
        p2_in_data: in type_OrderedStreamTypeIntTypeArithType16ArithType256;
        p2_in_last: in type_LastVectorTypeArithType1;
        p2_in_valid: in type_LogicType;
        p2_out_ready: out type_ReadyVectorTypeArithType1;
        p3_out_data: out type_NamedTupleTypeTextTypeaddrIntTypeArithType9TextTypedataIntTypeArithType16_addr;
        p3_out_last: out type_LastVectorTypeArithType0;
        p3_out_valid: out type_LogicType;
        p3_in_ready: in type_ReadyVectorTypeArithType0;
        p4_in_data: in type_UnitType;
        p4_in_last: in type_LastVectorTypeArithType0;
        p4_in_valid: in type_LogicType;
        p4_out_ready: out type_ReadyVectorTypeArithType0
    );
end repeat_28;

architecture behavioral of repeat_28 is
    
    component repeat_27
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_OrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType256;
            p0_out_last: out type_LastVectorTypeArithType1;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType1;
            p1_in_data: in type_BaseAddrTypeArithType1BlockRamTypeIdType2;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_in_data: in type_OrderedStreamTypeIntTypeArithType16ArithType256;
            p2_in_last: in type_LastVectorTypeArithType1;
            p2_in_valid: in type_LogicType;
            p2_out_ready: out type_ReadyVectorTypeArithType1;
            p3_out_data: out type_NamedTupleTypeTextTypeaddrIntTypeArithType9TextTypedataIntTypeArithType16_addr;
            p3_out_last: out type_LastVectorTypeArithType0;
            p3_out_valid: out type_LogicType;
            p3_in_ready: in type_ReadyVectorTypeArithType0;
            p4_in_data: in type_UnitType;
            p4_in_last: in type_LastVectorTypeArithType0;
            p4_in_valid: in type_LogicType;
            p4_out_ready: out type_ReadyVectorTypeArithType0
        );
    end component;
    
    signal elem_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_OrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType256;
    signal s01_last: type_LastVectorTypeArithType1;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType1;
begin
    
    U0_repeat: repeat_27 port map(
        p2_in_last => p2_in_last,
        clk => clk,
        p1_in_data => p1_in_data,
        p1_in_last => p1_in_last,
        p4_in_last => p4_in_last,
        p3_out_valid => p3_out_valid,
        p2_in_valid => p2_in_valid,
        p0_out_valid => s02_valid,
        p4_in_valid => p4_in_valid,
        p3_out_data => p3_out_data,
        p1_in_valid => p1_in_valid,
        p0_out_last => s01_last,
        p3_out_last => p3_out_last,
        reset => reset,
        p4_out_ready => p4_out_ready,
        p2_out_ready => p2_out_ready,
        p0_out_data => s00_data,
        p0_in_ready => s03_ready,
        p4_in_data => p4_in_data,
        p1_out_ready => p1_out_ready,
        p2_in_data => p2_in_data,
        p3_in_ready => p3_in_ready
    );
    p0_out_data <= s00_data;
    p0_out_last <= "1" & s01_last when elem_counter = stream_length - 1 else "0" & s01_last;
    p0_out_valid <= s02_valid;
    
    s03_ready <= (p0_in_ready(p0_in_ready'high) and p0_in_ready(p0_in_ready'high - 1)) & p0_in_ready(p0_in_ready'high - 2 downto p0_in_ready'low);
    
    process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                elem_counter <= 0;
            else
                if s02_valid = '1' and p0_in_ready(p0_in_ready'high - 1) = '1' then
                    if elem_counter < stream_length - 1 then
                        elem_counter <= elem_counter + 1;
                    else
                        elem_counter <= 0;
                    end if;
                end if;
            end if;
        end if;
    end process;
    
end behavioral;
