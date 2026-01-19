library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mapsimpleorderedstream_109 is
    generic(
        stream_length: type_NaturalNumberType := 256
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType12ArithType16ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16ArithType256ArithType256;
        p0_in_last: in type_LastVectorTypeArithType3;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType3;
        p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256ArithType256;
        p1_out_last: out type_LastVectorTypeArithType3;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType3;
        p2_out_data: out type_IntTypeArithType13;
        p2_out_last: out type_LastVectorTypeArithType0;
        p2_out_valid: out type_LogicType;
        p2_in_ready: in type_ReadyVectorTypeArithType0;
        p3_in_data: in type_IntTypeArithType256;
        p3_in_last: in type_LastVectorTypeArithType0;
        p3_in_valid: in type_LogicType;
        p3_out_ready: out type_ReadyVectorTypeArithType0
    );
end mapsimpleorderedstream_109;

architecture behavioral of mapsimpleorderedstream_109 is
    
    component zipstream_93
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType12ArithType16ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16ArithType256_t0;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_out_data: out type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType12ArithType16TextTypet1OrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16ArithType256;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2
        );
    end component;
    component mapsimpleorderedstream_107
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType12ArithType16TextTypet1OrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16ArithType256;
            p0_in_last: in type_LastVectorTypeArithType2;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType2;
            p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256;
            p1_out_last: out type_LastVectorTypeArithType2;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType2;
            p2_out_data: out type_IntTypeArithType13;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0;
            p3_in_data: in type_IntTypeArithType256;
            p3_in_last: in type_LastVectorTypeArithType0;
            p3_in_valid: in type_LogicType;
            p3_out_ready: out type_ReadyVectorTypeArithType0
        );
    end component;
    
    signal in_counter: natural range 0 to stream_length - 1 := 0;
    signal out_counter: natural range 0 to stream_length - 1 := 0;
    
    signal s00_data: type_OrderedStreamTypeNamedTupleTypeTextTypet0OrderedStreamTypeIntTypeArithType12ArithType16TextTypet1OrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16ArithType256;
    signal s01_last: type_LastVectorTypeArithType2;
    signal s02_valid: type_LogicType;
    signal s03_ready: type_ReadyVectorTypeArithType2;
    signal s04_data: type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType256ArithType16ArithType256;
    signal s05_last: type_LastVectorTypeArithType2;
    signal s06_valid: type_LogicType;
    signal s07_ready: type_ReadyVectorTypeArithType2;
    signal s08_data: type_NamedTupleTypeTextTypet0OrderedStreamTypeOrderedStreamTypeIntTypeArithType12ArithType16ArithType256TextTypet1OrderedStreamTypeOrderedStreamTypeBaseAddrTypeArithType1BlockRamTypeIdType2ArithType16ArithType256_t0;
    signal s09_last: type_LastVectorTypeArithType2;
    signal s10_valid: type_LogicType;
    signal s11_ready: type_ReadyVectorTypeArithType2;
    signal s12_data: type_LastVectorTypeArithType3;
    signal s13_data: type_ReadyVectorTypeArithType3;
begin
    
    U0_zipstream: zipstream_93 port map(
        clk => clk,
        p0_out_ready => s11_ready,
        p1_out_valid => s02_valid,
        p0_in_valid => s10_valid,
        p0_in_data => s08_data,
        p0_in_last => s09_last,
        p1_out_last => s01_last,
        reset => reset,
        p1_out_data => s00_data,
        p1_in_ready => s03_ready
    );
    U1_mapsimpleorderedstream: mapsimpleorderedstream_107 port map(
        p3_in_valid => p3_in_valid,
        clk => clk,
        p0_out_ready => s03_ready,
        p2_out_valid => p2_out_valid,
        p1_out_valid => s06_valid,
        p0_in_valid => s02_valid,
        p1_out_data => s04_data,
        p3_out_ready => p3_out_ready,
        p2_out_last => p2_out_last,
        p2_out_data => p2_out_data,
        p2_in_ready => p2_in_ready,
        p0_in_last => s01_last,
        p1_out_last => s05_last,
        reset => reset,
        p0_in_data => s00_data,
        p3_in_last => p3_in_last,
        p3_in_data => p3_in_data,
        p1_in_ready => s07_ready
    );
    p1_out_data <= s04_data;
    p1_out_last <= "1" & s05_last when out_counter = stream_length - 1 else "0" & s05_last;
    p1_out_valid <= s06_valid;
    s07_ready <= p1_in_ready(s07_ready'high downto s07_ready'low);
    
    s08_data <= p0_in_data;
    s09_last <= p0_in_last(s09_last'high downto s09_last'low);
    s10_valid <= p0_in_valid;
    -- TODO problem: never repeats!!!!
    p0_out_ready <= "1" & s11_ready when in_counter = stream_length - 1 and s11_ready(s11_ready'high) = '1' else "0" & s11_ready;
    
    ingoing_elements_counter_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                in_counter <= 0;
            else
                if s10_valid = '1' and s11_ready(s11_ready'high) = '1' then
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
                if s06_valid = '1' and s07_ready(s07_ready'high) = '1' then
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
