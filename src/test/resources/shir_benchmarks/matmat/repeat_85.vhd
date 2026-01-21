library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity repeat_85 is
    generic(
        stream_length: type_NaturalNumberType := 16
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_out_data: out type_OrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16;
        p0_out_last: out type_LastVectorTypeArithType1;
        p0_out_valid: out type_LogicType;
        p0_in_ready: in type_ReadyVectorTypeArithType1;
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
end repeat_85;

architecture behavioral of repeat_85 is
    
    component id_73
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
    component orderedstreamtounorderedstream_75
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeIntTypeArithType256ArithType4096;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_UnorderedStreamTypeIntTypeArithType256ArithType4096;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component reduceorderedstream_84
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_BaseAddrTypeArithType1BlockRamTypeIdType2;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType256TextTypet1IntTypeArithType12ArithType4096;
            p1_in_last: in type_LastVectorTypeArithType1;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType1;
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
    end component;
    component joinorderedstream_74
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_out_data: out type_OrderedStreamTypeIntTypeArithType256ArithType4096;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component id_72
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
    component indexunorderedstream_76
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_UnorderedStreamTypeIntTypeArithType256ArithType4096;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType256TextTypet1IntTypeArithType12ArithType4096;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    
    signal elem_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256;
    signal s01_last: type_LastVectorTypeArithType2;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType2;
    signal s04_data: type_OrderedStreamTypeIntTypeArithType256ArithType4096;
    signal s05_last: type_LastVectorTypeArithType1;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType1;
    signal s08_data: type_UnorderedStreamTypeIntTypeArithType256ArithType4096;
    signal s09_last: type_LastVectorTypeArithType1;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType1;
    signal s12_data: type_BaseAddrTypeArithType1BlockRamTypeIdType2;
    signal s13_last: type_LastVectorTypeArithType0;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s16_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType256TextTypet1IntTypeArithType12ArithType4096;
    signal s17_last: type_LastVectorTypeArithType1;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType1;
    signal s20_data: type_BaseAddrTypeArithType1BlockRamTypeIdType2;
    signal s21_last: type_LastVectorTypeArithType0;
    signal s22_valid: type_LogicType;
    signal s23_ready: type_ReadyVectorTypeArithType0;
begin
    
    U0_id: id_72 port map(
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => p1_in_valid,
        p1_out_last => s13_last,
        p0_out_ready => p1_out_ready,
        p0_in_last => p1_in_last,
        reset => reset,
        p1_out_data => s12_data,
        p1_in_ready => s15_ready,
        p0_in_data => p1_in_data
    );
    U1_id: id_73 port map(
        clk => clk,
        p0_out_ready => p2_out_ready,
        p1_out_valid => s02_valid,
        p0_in_valid => p2_in_valid,
        p0_in_data => p2_in_data,
        p1_out_data => s00_data,
        p0_in_last => p2_in_last,
        p1_out_last => s01_last,
        reset => reset,
        p1_in_ready => s03_ready
    );
    U2_joinorderedstream: joinorderedstream_74 port map(
        clk => clk,
        p0_out_ready => s03_ready,
        p1_out_data => s04_data,
        p1_out_valid => s06_valid,
        p0_in_valid => s02_valid,
        p0_in_data => s00_data,
        p0_in_last => s01_last,
        reset => reset,
        p1_in_ready => s07_ready,
        p1_out_last => s05_last
    );
    U3_orderedstreamtounorderedstream: orderedstreamtounorderedstream_75 port map(
        clk => clk,
        p1_out_data => s08_data,
        p1_out_valid => s10_valid,
        p0_in_valid => s06_valid,
        p0_out_ready => s07_ready,
        p0_in_data => s04_data,
        reset => reset,
        p1_in_ready => s11_ready,
        p1_out_last => s09_last,
        p0_in_last => s05_last
    );
    U4_indexunorderedstream: indexunorderedstream_76 port map(
        clk => clk,
        p1_out_data => s16_data,
        p1_out_valid => s18_valid,
        p0_in_valid => s10_valid,
        p0_out_ready => s11_ready,
        p0_in_data => s08_data,
        reset => reset,
        p1_in_ready => s19_ready,
        p1_out_last => s17_last,
        p0_in_last => s09_last
    );
    U5_reduceorderedstream: reduceorderedstream_84 port map(
        clk => clk,
        p2_out_valid => s22_valid,
        p1_in_last => s17_last,
        p0_in_valid => s14_valid,
        p4_in_last => p4_in_last,
        p3_out_valid => p3_out_valid,
        p1_in_data => s16_data,
        p1_out_ready => s19_ready,
        p3_out_data => p3_out_data,
        p4_in_valid => p4_in_valid,
        p2_out_last => s21_last,
        p0_out_ready => s15_ready,
        p2_out_data => s20_data,
        p1_in_valid => s18_valid,
        p2_in_ready => s23_ready,
        p3_out_last => p3_out_last,
        p0_in_last => s13_last,
        reset => reset,
        p4_out_ready => p4_out_ready,
        p0_in_data => s12_data,
        p4_in_data => p4_in_data,
        p3_in_ready => p3_in_ready
    );
    p0_out_data <= s20_data;
    p0_out_last <= "1" & s21_last when elem_counter = stream_length - 1 else "0" & s21_last;
    p0_out_valid <= s22_valid;
    
    s23_ready <= (p0_in_ready(p0_in_ready'high) and p0_in_ready(p0_in_ready'high - 1)) & p0_in_ready(p0_in_ready'high - 2 downto p0_in_ready'low);
    
    process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                elem_counter <= 0;
            else
                if s22_valid = '1' and p0_in_ready(p0_in_ready'high - 1) = '1' then
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
