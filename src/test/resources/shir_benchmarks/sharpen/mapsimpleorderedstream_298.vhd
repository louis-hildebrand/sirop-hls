library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_298 is
    generic(
        stream_length: type_NaturalNumberType := 1918
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType1918;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeIntTypeArithType33ArithType1918;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end mapsimpleorderedstream_298;

architecture behavioral of mapsimpleorderedstream_298 is
    
    component select_288
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_287
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component resizelowinteger_292
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType33;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType30;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component addint_295
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType33;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component subint_291
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType33;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component distributor_296
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType2;
            p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType2;
            p3_out_data: out type_VectorTypeLogicTypeArithType2;
            p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType2
        );
    end component;
    component fliptuple_290
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_297
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component id_289
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_294
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType32;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_IntTypeArithType32;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component resizeinteger_293
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType30;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType32;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
    signal s01_last: type_LastVectorTypeArithType0;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType0;
    signal s04_data: type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
    signal s05_last: type_LastVectorTypeArithType0;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType0;
    signal s08_data: type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
    signal s09_last: type_LastVectorTypeArithType0;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType0;
    signal s12_data: type_IntTypeArithType33;
    signal s13_last: type_LastVectorTypeArithType0;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType0;
    signal s16_data: type_IntTypeArithType30;
    signal s17_last: type_LastVectorTypeArithType0;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType0;
    signal s20_data: type_IntTypeArithType32;
    signal s21_last: type_LastVectorTypeArithType0;
    signal s22_valid: type_LogicType;
    signal s23_ready: type_ReadyVectorTypeArithType0;
    signal s24_data: type_IntTypeArithType32;
    signal s25_last: type_LastVectorTypeArithType0;
    signal s26_valid: type_LogicType;
    signal s27_ready: type_ReadyVectorTypeArithType0;
    signal s28_data: type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
    signal s29_last: type_LastVectorTypeArithType0;
    signal s30_valid: type_LogicType;
    signal s31_ready: type_ReadyVectorTypeArithType0;
    signal s32_data: type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType2;
    signal s33_data: type_VectorTypeLastVectorTypeArithType0ArithType2;
    signal s34_data: type_VectorTypeLogicTypeArithType2;
    signal s35_ready: type_ReadyVectorTypeArithType0;
    signal s36_ready: type_ReadyVectorTypeArithType0;
    signal s37_data: type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
    signal s38_last: type_LastVectorTypeArithType0;
    signal s39_valid: type_LogicType;
    signal s40_ready: type_ReadyVectorTypeArithType0;
    signal s41_data: type_IntTypeArithType33;
    signal s42_last: type_LastVectorTypeArithType0;
    signal s43_valid: type_LogicType;
    signal s44_ready: type_ReadyVectorTypeArithType0;
    signal s45_data: type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
    signal s46_last: type_LastVectorTypeArithType0;
    signal s47_valid: type_LogicType;
    signal s48_ready: type_ReadyVectorTypeArithType0;
    signal s49_data: type_LastVectorTypeArithType1;
    signal s50_data: type_ReadyVectorTypeArithType1;
begin
    
    U0_id: id_287 port map(
        clk => clk,
        p1_out_valid => s02_valid,
        p0_in_valid => s34_data(0),
        p1_out_last => s01_last,
        p0_out_ready => s35_ready,
        p1_out_data => s00_data,
        p0_in_last => s33_data(0),
        reset => reset,
        p1_in_ready => s03_ready,
        p0_in_data => s32_data(0)
    );
    U1_select: select_288 port map(
        clk => clk,
        p1_out_valid => s22_valid,
        p0_in_valid => s02_valid,
        p1_out_last => s21_last,
        p0_out_ready => s03_ready,
        p0_in_last => s01_last,
        reset => reset,
        p1_in_ready => s23_ready,
        p1_out_data => s20_data,
        p0_in_data => s00_data
    );
    U2_id: id_289 port map(
        clk => clk,
        p1_out_valid => s06_valid,
        p0_in_valid => s34_data(1),
        p1_out_last => s05_last,
        p0_out_ready => s36_ready,
        p1_out_data => s04_data,
        p0_in_last => s33_data(1),
        reset => reset,
        p1_in_ready => s07_ready,
        p0_in_data => s32_data(1)
    );
    U3_fliptuple: fliptuple_290 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s06_valid,
        p1_out_last => s09_last,
        p0_out_ready => s07_ready,
        p1_out_data => s08_data,
        p0_in_last => s05_last,
        reset => reset,
        p1_in_ready => s11_ready,
        p0_in_data => s04_data
    );
    U4_subint: subint_291 port map(
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => s10_valid,
        p1_out_last => s13_last,
        p1_out_data => s12_data,
        p0_out_ready => s11_ready,
        p0_in_last => s09_last,
        reset => reset,
        p1_in_ready => s15_ready,
        p0_in_data => s08_data
    );
    U5_resizelowinteger: resizelowinteger_292 port map(
        clk => clk,
        p1_out_valid => s18_valid,
        p0_in_valid => s14_valid,
        p0_in_data => s12_data,
        p1_out_last => s17_last,
        p0_out_ready => s15_ready,
        p1_out_data => s16_data,
        p0_in_last => s13_last,
        reset => reset,
        p1_in_ready => s19_ready
    );
    U6_resizeinteger: resizeinteger_293 port map(
        clk => clk,
        p1_out_valid => s26_valid,
        p0_in_valid => s18_valid,
        p0_in_data => s16_data,
        p1_out_last => s25_last,
        p0_out_ready => s19_ready,
        p0_in_last => s17_last,
        reset => reset,
        p1_in_ready => s27_ready,
        p1_out_data => s24_data
    );
    U7_tuple: tuple_294 port map(
        clk => clk,
        p2_out_valid => s30_valid,
        p1_in_last => s25_last,
        p0_in_valid => s22_valid,
        p2_out_data => s28_data,
        p0_in_data => s20_data,
        p2_out_last => s29_last,
        p0_out_ready => s23_ready,
        p1_in_valid => s26_valid,
        p2_in_ready => s31_ready,
        p1_in_data => s24_data,
        p0_in_last => s21_last,
        reset => reset,
        p1_out_ready => s27_ready
    );
    U8_addint: addint_295 port map(
        clk => clk,
        p1_out_valid => s43_valid,
        p0_in_valid => s30_valid,
        p1_out_last => s42_last,
        p1_out_data => s41_data,
        p0_out_ready => s31_ready,
        p0_in_last => s29_last,
        reset => reset,
        p1_in_ready => s44_ready,
        p0_in_data => s28_data
    );
    U9_distributor: distributor_296 port map(
        clk => clk,
        p0_in_valid => s39_valid,
        p4_in_data(0) => s35_ready,
        p4_in_data(1) => s36_ready,
        p0_out_ready => s40_ready,
        p1_out_data => s32_data,
        p2_out_data => s33_data,
        p0_in_last => s38_last,
        reset => reset,
        p3_out_data => s34_data,
        p0_in_data => s37_data
    );
    U10_id: id_297 port map(
        clk => clk,
        p1_out_valid => s39_valid,
        p0_in_valid => s47_valid,
        p1_out_last => s38_last,
        p0_out_ready => s48_ready,
        p1_out_data => s37_data,
        p0_in_last => s46_last,
        reset => reset,
        p1_in_ready => s40_ready,
        p0_in_data => s45_data
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
