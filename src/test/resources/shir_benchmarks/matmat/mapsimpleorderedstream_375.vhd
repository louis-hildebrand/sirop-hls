library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_375 is
    generic(
        stream_length: type_NaturalNumberType := 256
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16ArithType256;
        p0_in_last: in type_LastVectorTypeArithType2;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType2;
        p1_out_data: out type_OrderedStreamTypeIntTypeArithType20ArithType256;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end mapsimpleorderedstream_375;

architecture behavioral of mapsimpleorderedstream_375 is
    
    component zipstream_147
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16_t0;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0VectorTypeIntTypeArithType16ArithType16TextTypet1VectorTypeIntTypeArithType16ArithType16ArithType16;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component id_374
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16_t0;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16_t0;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component tuple_146
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_in_data: in type_OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16;
            p1_in_last: in type_LastVectorTypeArithType1;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType1;
            p2_out_data: out type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16_t0;
            p2_out_last: out type_LastVectorTypeArithType1;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component distributor_373
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16_t0;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16ArithType2;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType1ArithType2;
            p3_out_data: out type_VectorTypeLogicTypeArithType2;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType1ArithType2
        );
    end component;
    component mapsimpleorderedstream_365
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0VectorTypeIntTypeArithType16ArithType16TextTypet1VectorTypeIntTypeArithType16ArithType16ArithType16;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeIntTypeArithType16ArithType16;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component mapsimpleorderedstream_143
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeIntTypeArithType256ArithType16;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component foldorderedstream_372
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeIntTypeArithType16ArithType16;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_IntTypeArithType20;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_144
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16_t0;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16_t0;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component select_145
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16_t0;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component id_136
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16_t0;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16_t0;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component select_137
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16_t0;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeIntTypeArithType256ArithType16;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16_t0;
    signal s01_last: type_LastVectorTypeArithType1;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType1;
    signal s04_data: type_OrderedStreamTypeIntTypeArithType256ArithType16;
    signal s05_last: type_LastVectorTypeArithType1;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType1;
    signal s08_data: type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16_t0;
    signal s09_last: type_LastVectorTypeArithType1;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType1;
    signal s12_data: type_OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16;
    signal s13_last: type_LastVectorTypeArithType1;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType1;
    signal s16_data: type_OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16;
    signal s17_last: type_LastVectorTypeArithType1;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType1;
    signal s20_data: type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16_t0;
    signal s21_last: type_LastVectorTypeArithType1;
    signal s22_valid: type_LogicType;
    signal s23_ready: type_ReadyVectorTypeArithType1;
    signal s24_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0VectorTypeIntTypeArithType16ArithType16TextTypet1VectorTypeIntTypeArithType16ArithType16ArithType16;
    signal s25_last: type_LastVectorTypeArithType1;
    signal s26_valid: type_LogicType;
    signal s27_ready: type_ReadyVectorTypeArithType1;
    signal s28_data: type_OrderedStreamTypeIntTypeArithType16ArithType16;
    signal s29_last: type_LastVectorTypeArithType1;
    signal s30_valid: type_LogicType;
    signal s31_ready: type_ReadyVectorTypeArithType1;
    signal s32_data: type_VectorTypeNamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16ArithType2;
    signal s33_data: type_VectorTypeLastVectorTypeArithType1ArithType2;
    signal s34_data: type_VectorTypeLogicTypeArithType2;
    signal s35_ready: type_ReadyVectorTypeArithType1;
    signal s36_ready: type_ReadyVectorTypeArithType1;
    signal s37_data: type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16_t0;
    signal s38_last: type_LastVectorTypeArithType1;
    signal s39_valid: type_LogicType;
    signal s40_ready: type_ReadyVectorTypeArithType1;
    signal s41_data: type_IntTypeArithType20;
    signal s42_last: type_LastVectorTypeArithType0;
    signal s43_valid: type_LogicType;
    signal s44_ready: type_ReadyVectorTypeArithType0;
    signal s45_data: type_NamedTupleTypeTextTypet0OrderedStreamTypeVectorTypeIntTypeArithType16ArithType16ArithType16TextTypet1OrderedStreamTypeIntTypeArithType256ArithType16_t0;
    signal s46_last: type_LastVectorTypeArithType1;
    signal s47_valid: type_LogicType;
    signal s48_ready: type_ReadyVectorTypeArithType1;
    signal s49_data: type_LastVectorTypeArithType2;
    signal s50_data: type_ReadyVectorTypeArithType2;
begin
    
    U0_id: id_136 port map(
        clk => clk,
        p0_in_data => s32_data(0),
        p1_out_data => s00_data,
        p1_out_valid => s02_valid,
        p0_in_valid => s34_data(0),
        p0_out_ready => s35_ready,
        reset => reset,
        p1_in_ready => s03_ready,
        p1_out_last => s01_last,
        p0_in_last => s33_data(0)
    );
    U1_select: select_137 port map(
        clk => clk,
        p0_in_data => s00_data,
        p1_out_valid => s06_valid,
        p0_in_valid => s02_valid,
        p1_out_data => s04_data,
        p0_out_ready => s03_ready,
        reset => reset,
        p1_in_ready => s07_ready,
        p1_out_last => s05_last,
        p0_in_last => s01_last
    );
    U2_mapsimpleorderedstream: mapsimpleorderedstream_143 port map(
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => s06_valid,
        p0_out_ready => s07_ready,
        p0_in_data => s04_data,
        p1_out_data => s12_data,
        reset => reset,
        p1_in_ready => s15_ready,
        p1_out_last => s13_last,
        p0_in_last => s05_last
    );
    U3_id: id_144 port map(
        clk => clk,
        p0_in_data => s32_data(1),
        p1_out_data => s08_data,
        p1_out_valid => s10_valid,
        p0_in_valid => s34_data(1),
        p0_out_ready => s36_ready,
        reset => reset,
        p1_in_ready => s11_ready,
        p1_out_last => s09_last,
        p0_in_last => s33_data(1)
    );
    U4_select: select_145 port map(
        clk => clk,
        p0_in_data => s08_data,
        p1_out_valid => s18_valid,
        p0_in_valid => s10_valid,
        p0_out_ready => s11_ready,
        p1_out_data => s16_data,
        reset => reset,
        p1_in_ready => s19_ready,
        p1_out_last => s17_last,
        p0_in_last => s09_last
    );
    U5_tuple: tuple_146 port map(
        clk => clk,
        p2_out_valid => s22_valid,
        p1_in_last => s17_last,
        p0_in_valid => s14_valid,
        p1_out_ready => s19_ready,
        p0_out_ready => s15_ready,
        p0_in_data => s12_data,
        p2_out_last => s21_last,
        p1_in_data => s16_data,
        p1_in_valid => s18_valid,
        reset => reset,
        p2_in_ready => s23_ready,
        p0_in_last => s13_last,
        p2_out_data => s20_data
    );
    U6_zipstream: zipstream_147 port map(
        clk => clk,
        p1_out_valid => s26_valid,
        p0_in_valid => s22_valid,
        p0_out_ready => s23_ready,
        p0_in_data => s20_data,
        reset => reset,
        p1_in_ready => s27_ready,
        p1_out_last => s25_last,
        p1_out_data => s24_data,
        p0_in_last => s21_last
    );
    U7_mapsimpleorderedstream: mapsimpleorderedstream_365 port map(
        clk => clk,
        p1_out_valid => s30_valid,
        p0_in_valid => s26_valid,
        p0_out_ready => s27_ready,
        p1_out_data => s28_data,
        reset => reset,
        p0_in_data => s24_data,
        p1_in_ready => s31_ready,
        p1_out_last => s29_last,
        p0_in_last => s25_last
    );
    U8_foldorderedstream: foldorderedstream_372 port map(
        clk => clk,
        p1_out_data => s41_data,
        p1_out_valid => s43_valid,
        p0_in_valid => s30_valid,
        p0_out_ready => s31_ready,
        p1_out_last => s42_last,
        reset => reset,
        p1_in_ready => s44_ready,
        p0_in_data => s28_data,
        p0_in_last => s29_last
    );
    U9_distributor: distributor_373 port map(
        clk => clk,
        p0_in_data => s37_data,
        p0_in_valid => s39_valid,
        p2_out_data => s33_data,
        p0_out_ready => s40_ready,
        p4_in_data(0) => s35_ready,
        p4_in_data(1) => s36_ready,
        reset => reset,
        p3_out_data => s34_data,
        p0_in_last => s38_last,
        p1_out_data => s32_data
    );
    U10_id: id_374 port map(
        clk => clk,
        p0_in_data => s45_data,
        p1_out_data => s37_data,
        p1_out_valid => s39_valid,
        p0_in_valid => s47_valid,
        p0_out_ready => s48_ready,
        reset => reset,
        p1_in_ready => s40_ready,
        p1_out_last => s38_last,
        p0_in_last => s46_last
    );
    p1_out_data <= s41_data;
    p1_out_last <= "1" & s42_last when out_counter = stream_length - 1 else "0" & s42_last;
    p1_out_valid <= s43_valid;
    s44_ready <= p1_in_ready(s44_ready'high downto s44_ready'low);
    
    s45_data <= p0_in_data;
    s46_last <= p0_in_last(s46_last'high downto s46_last'low);
    s47_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s48_ready when in_counter = stream_length - 1 and s48_ready(s48_ready'high) = '1' else "0" & s48_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s47_valid = '1' and s48_ready(s48_ready'high) = '1' then
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
                if s43_valid = '1' and s44_ready(s44_ready'high) = '1' then
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
