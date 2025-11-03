library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_21 is
    generic(
        stream_length: type_NaturalNumberType := 256
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType16ArithType256ArithType256;
        p0_in_last: in type_LastVectorTypeArithType2;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType2;
        p1_out_data: out type_OrderedStreamTypeIntTypeArithType24ArithType256;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end mapsimpleorderedstream_21;

architecture behavioral of mapsimpleorderedstream_21 is
    
    component mapsimpleorderedstream_12
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType256;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeIntTypeArithType16ArithType256;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component repeathidden_3
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_OrderedStreamTypeIntTypeArithType16ArithType256;
            p0_out_last: out type_LastVectorTypeArithType1;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component zipstream_5
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType16ArithType256TextTypet1OrderedStreamTypeIntTypeArithType16ArithType256_t0;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType256;
            p1_out_last: out type_LastVectorTypeArithType1;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    component foldorderedstream_19
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeIntTypeArithType16ArithType256;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_out_data: out type_IntTypeArithType24;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component tuple_4
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeIntTypeArithType16ArithType256;
            p0_in_last: in type_LastVectorTypeArithType1;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType1;
            p1_in_data: in type_OrderedStreamTypeIntTypeArithType16ArithType256;
            p1_in_last: in type_LastVectorTypeArithType1;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType1;
            p2_out_data: out type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType16ArithType256TextTypet1OrderedStreamTypeIntTypeArithType16ArithType256_t0;
            p2_out_last: out type_LastVectorTypeArithType1;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType1
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_OrderedStreamTypeIntTypeArithType16ArithType256;
    signal s01_last: type_LastVectorTypeArithType1;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType1;
    signal s04_data: type_NamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType16ArithType256TextTypet1OrderedStreamTypeIntTypeArithType16ArithType256_t0;
    signal s05_last: type_LastVectorTypeArithType1;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType1;
    signal s08_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16ArithType256;
    signal s09_last: type_LastVectorTypeArithType1;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType1;
    signal s12_data: type_OrderedStreamTypeIntTypeArithType16ArithType256;
    signal s13_last: type_LastVectorTypeArithType1;
    signal s14_valid: type_LogicType;
    signal s15_ready: type_ReadyVectorTypeArithType1;
    signal s16_data: type_IntTypeArithType24;
    signal s17_last: type_LastVectorTypeArithType0;
    signal s18_valid: type_LogicType;
    signal s19_ready: type_ReadyVectorTypeArithType0;
    signal s20_data: type_OrderedStreamTypeIntTypeArithType16ArithType256;
    signal s21_last: type_LastVectorTypeArithType1;
    signal s22_valid: type_LogicType;
    signal s23_ready: type_ReadyVectorTypeArithType1;
    signal s24_data: type_LastVectorTypeArithType2;
    signal s25_data: type_ReadyVectorTypeArithType2;
begin
    
    U0_repeathidden: repeathidden_3 port map(
        clk => clk,
        p0_out_valid => s02_valid,
        p0_out_last => s01_last,
        reset => reset,
        p0_in_ready => s03_ready,
        p0_out_data => s00_data
    );
    U1_tuple: tuple_4 port map(
        p0_in_data => s20_data,
        clk => clk,
        p2_out_valid => s06_valid,
        p1_in_last => s01_last,
        p0_in_valid => s22_valid,
        p1_out_ready => s03_ready,
        p0_out_ready => s23_ready,
        p1_in_data => s00_data,
        p2_out_last => s05_last,
        p1_in_valid => s02_valid,
        p2_out_data => s04_data,
        reset => reset,
        p2_in_ready => s07_ready,
        p0_in_last => s21_last
    );
    U2_zipstream: zipstream_5 port map(
        clk => clk,
        p1_out_valid => s10_valid,
        p0_in_valid => s06_valid,
        p0_out_ready => s07_ready,
        p1_out_data => s08_data,
        reset => reset,
        p1_in_ready => s11_ready,
        p1_out_last => s09_last,
        p0_in_data => s04_data,
        p0_in_last => s05_last
    );
    U3_mapsimpleorderedstream: mapsimpleorderedstream_12 port map(
        clk => clk,
        p1_out_valid => s14_valid,
        p0_in_valid => s10_valid,
        p0_out_ready => s11_ready,
        p1_out_data => s12_data,
        reset => reset,
        p0_in_data => s08_data,
        p1_in_ready => s15_ready,
        p1_out_last => s13_last,
        p0_in_last => s09_last
    );
    U4_foldorderedstream: foldorderedstream_19 port map(
        p0_in_data => s12_data,
        clk => clk,
        p1_out_valid => s18_valid,
        p0_in_valid => s14_valid,
        p0_out_ready => s15_ready,
        p1_out_last => s17_last,
        reset => reset,
        p1_in_ready => s19_ready,
        p0_in_last => s13_last,
        p1_out_data => s16_data
    );
    p1_out_data <= s16_data;
    p1_out_last <= "1" & s17_last when out_counter = stream_length - 1 else "0" & s17_last;
    p1_out_valid <= s18_valid;
    s19_ready <= p1_in_ready(s19_ready'high downto s19_ready'low);
    
    s20_data <= p0_in_data;
    s21_last <= p0_in_last(s21_last'high downto s21_last'low);
    s22_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s23_ready when in_counter = stream_length - 1 and s23_ready(s23_ready'high) = '1' else "0" & s23_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s22_valid = '1' and s23_ready(s23_ready'high) = '1' then
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
                if s18_valid = '1' and s19_ready(s19_ready'high) = '1' then
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
