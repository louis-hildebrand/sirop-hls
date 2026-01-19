library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_44 is
    generic(
        stream_length: type_NaturalNumberType := 16
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3ArithType16;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeIntTypeArithType256ArithType16;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1;
        p2_out_data: out type_IntTypeArithType5;
        p2_out_last: out type_LastVectorTypeArithType0;
        p2_out_valid: out type_LogicType;
        p2_in_ready: in type_ReadyVectorTypeArithType0;
        p3_in_data: in type_IntTypeArithType256;
        p3_in_last: in type_LastVectorTypeArithType0;
        p3_in_valid: in type_LogicType;
        p3_out_ready: out type_ReadyVectorTypeArithType0
    );
end mapsimpleorderedstream_44;

architecture behavioral of mapsimpleorderedstream_44 is
    
    component id_35
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component distributor_42
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3ArithType2;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType2;
            p3_out_data: out type_VectorTypeLogicTypeArithType2;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType2
        );
    end component;
    component select_38
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType4;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component readsync_41
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_BaseAddrTypeArithType1BlockRamTypeIdType3;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_IntTypeArithType4;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_IntTypeArithType256;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0;
            p3_out_data: out type_IntTypeArithType5;
            p3_out_last: out type_LastVectorTypeArithType0;
            p3_out_valid: out type_LogicType;
            p3_in_ready: in type_ReadyVectorTypeArithType0;
            p4_in_data: in type_IntTypeArithType256;
            p4_in_last: in type_LastVectorTypeArithType0;
            p4_in_valid: in type_LogicType;
            p4_out_ready: out type_ReadyVectorTypeArithType0
        );
    end component;
    component id_43
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_37
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component select_36
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_BaseAddrTypeArithType1BlockRamTypeIdType3;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_NamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3_t0;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_NamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3_t0;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_data: type_BaseAddrTypeArithType1BlockRamTypeIdType3;
    signal s09_last: type_LastVectorTypeArithType0;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_IntTypeArithType4;
    signal s13_last: type_LastVectorTypeArithType0;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s16_data: type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3ArithType2;
    signal s17_data: type_VectorTypeLastVectorTypeArithType0ArithType2;
    signal s18_data: type_VectorTypeLogicTypeArithType2;
    signal s19_ready: type_ReadyVectorTypeArithType0;
    signal s20_ready: type_ReadyVectorTypeArithType0;
    signal s21_data: type_NamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3_t0;
    signal s22_last: type_LastVectorTypeArithType0;
    signal s23_valid: type_LogicType;
    signal s24_ready: type_ReadyVectorTypeArithType0;
    signal s25_data: type_IntTypeArithType256;
    signal s26_last: type_LastVectorTypeArithType0;
    signal s27_valid: type_LogicType;
    signal s28_ready: type_ReadyVectorTypeArithType0;
    signal s29_data: type_NamedTupleTypeTextTypet0IntTypeArithType4TextTypet1BaseAddrTypeArithType1BlockRamTypeIdType3_t0;
    signal s30_last: type_LastVectorTypeArithType0;
    signal s31_valid: type_LogicType;
    signal s32_ready: type_ReadyVectorTypeArithType0;
    signal s33_data: type_LastVectorTypeArithType1;
    signal s34_data: type_ReadyVectorTypeArithType1;
begin
    
    U0_id: id_35 port map(
        p1_out_data => s00_data,
        clk => clk,
        p1_out_valid => s02_valid,
        p0_in_valid => s18_data(0),
        p1_out_last => s01_last,
        p0_out_ready => s19_ready,
        p0_in_last => s17_data(0),
        reset => reset,
        p1_in_ready => s03_ready,
        p0_in_data => s16_data(0)
    );
    U1_select: select_36 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s02_valid,
        p1_out_last => s09_last,
        p0_out_ready => s03_ready,
        p1_out_data => s08_data,
        p0_in_last => s01_last,
        reset => reset,
        p1_in_ready => s11_ready,
        p0_in_data => s00_data
    );
    U2_id: id_37 port map(
        p1_out_data => s04_data,
        clk => clk,
        p1_out_valid => s06_valid,
        p0_in_valid => s18_data(1),
        p1_out_last => s05_last,
        p0_out_ready => s20_ready,
        p0_in_last => s17_data(1),
        reset => reset,
        p1_in_ready => s07_ready,
        p0_in_data => s16_data(1)
    );
    U3_select: select_38 port map(
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => s06_valid,
        p1_out_data => s12_data,
        p1_out_last => s13_last,
        p0_out_ready => s07_ready,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => s15_ready,
        p0_in_data => s04_data
    );
    U4_readsync: readsync_41 port map(
        clk => clk,
        p2_out_valid => s27_valid,
        p1_in_last => s13_last,
        p0_in_data => s08_data,
        p0_in_valid => s10_valid,
        p4_in_last => p3_in_last,
        p3_out_valid => p2_out_valid,
        p4_in_data => p3_in_data,
        p1_in_data => s12_data,
        p4_in_valid => p3_in_valid,
        p2_out_last => s26_last,
        p0_out_ready => s11_ready,
        p1_in_valid => s14_valid,
        p3_out_data => p2_out_data,
        p2_in_ready => s28_ready,
        p3_out_last => p2_out_last,
        p0_in_last => s09_last,
        reset => reset,
        p4_out_ready => p3_out_ready,
        p2_out_data => s25_data,
        p1_out_ready => s15_ready,
        p3_in_ready => p2_in_ready
    );
    U5_distributor: distributor_42 port map(
        clk => clk,
        p0_in_valid => s23_valid,
        p1_out_data => s16_data,
        p4_in_data(0) => s19_ready,
        p4_in_data(1) => s20_ready,
        p0_out_ready => s24_ready,
        p2_out_data => s17_data,
        p0_in_last => s22_last,
        reset => reset,
        p3_out_data => s18_data,
        p0_in_data => s21_data
    );
    U6_id: id_43 port map(
        p1_out_data => s21_data,
        clk => clk,
        p1_out_valid => s23_valid,
        p0_in_valid => s31_valid,
        p1_out_last => s22_last,
        p0_out_ready => s32_ready,
        p0_in_last => s30_last,
        reset => reset,
        p1_in_ready => s24_ready,
        p0_in_data => s29_data
    );
    p1_out_data <= s25_data;
    p1_out_last <= "1" & s26_last when out_counter = stream_length - 1 else "0" & s26_last;
    p1_out_valid <= s27_valid;
    s28_ready <= p1_in_ready(s28_ready'high downto s28_ready'low);
    
    s29_data <= p0_in_data;
    s30_last <= p0_in_last(s30_last'high downto s30_last'low);
    s31_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s32_ready when in_counter = stream_length - 1 and s32_ready(s32_ready'high) = '1' else "0" & s32_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s31_valid = '1' and s32_ready(s32_ready'high) = '1' then
                    if in_counter < stream_length - 1 then
                        in_counter <= in_counter + 1;
                    else
                        in_counter <= 0;
                    end if;
                end if;
            end if;
        end if;
    end process;
    
    outgoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                out_counter <= 0;
            else
                if s27_valid = '1' and s28_ready(s28_ready'high) = '1' then
                    if out_counter < stream_length - 1 then
                        out_counter <= out_counter + 1;
                    else
                        out_counter <= 0;
                    end if;
                end if;
            end if;
        end if;
    end process;
    
end behavioral;
