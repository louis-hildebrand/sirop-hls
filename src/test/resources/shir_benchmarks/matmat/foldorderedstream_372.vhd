library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity foldorderedstream_372 is
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
end foldorderedstream_372;

architecture behavioral of foldorderedstream_372 is
    
    component tuple_368
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_IntTypeArithType20;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_IntTypeArithType16;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
            p2_out_data: out type_NamedTupleTypeTextTypet0IntTypeArithType20TextTypet1IntTypeArithType16_t0;
            p2_out_last: out type_LastVectorTypeArithType0;
            p2_out_valid: out type_LogicType;
            p2_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    component addint_369
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType20TextTypet1IntTypeArithType16_t0;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_out_data: out type_IntTypeArithType20;
            p1_out_last: out type_LastVectorTypeArithType0;
            p1_out_valid: out type_LogicType;
            p1_in_ready: in type_ReadyVectorTypeArithType0
        );
    end component;
    
    signal first_element: std_logic := '1';
    
    signal in_ready: std_logic_vector(p0_out_ready'range) := (others => '0');
    
    -- accumulates the values in this stream
    signal buf: std_logic_vector(p1_out_data'range) := (others => '0');
    signal buf_valid: std_logic := '1'; -- buffer contains a value that is ready to be processes for the next iteration
        signal buf_last: std_logic := '0'; -- if the buffer contains the reduction up to the last value of the stream => output is ready!
        signal f_last: std_logic := '0'; -- if the function is currently working of the last value of the stream
        
        signal s00_data: type_NamedTupleTypeTextTypet0IntTypeArithType20TextTypet1IntTypeArithType16_t0;
        signal s01_last: type_LastVectorTypeArithType0;
        signal s02_valid: type_LogicType;
        signal s03_ready: type_ReadyVectorTypeArithType0;
        signal s04_data: type_IntTypeArithType20;
        signal s05_last: type_LastVectorTypeArithType0;
        signal s06_valid: type_LogicType;
        signal s07_ready: type_ReadyVectorTypeArithType0;
        signal s08_data: type_IntTypeArithType20;
        signal s09_last: type_LastVectorTypeArithType0;
        signal s10_valid: type_LogicType;
        signal s11_ready: type_ReadyVectorTypeArithType0;
        signal s12_data: type_IntTypeArithType16;
        signal s13_last: type_LastVectorTypeArithType0;
        signal s14_valid: type_LogicType;
        signal s15_ready: type_ReadyVectorTypeArithType0;
    begin
        
        U0_tuple: tuple_368 port map(
            clk => clk,
            p2_out_valid => s02_valid,
            p1_in_last => s13_last,
            p0_in_valid => s10_valid,
            p2_out_data => s00_data,
            p1_in_data => s12_data,
            p2_out_last => s01_last,
            p0_out_ready => s11_ready,
            p1_in_valid => s14_valid,
            p2_in_ready => s03_ready,
            p0_in_last => s09_last,
            reset => reset,
            p0_in_data => s08_data,
            p1_out_ready => s15_ready
        );
        U1_addint: addint_369 port map(
            clk => clk,
            p1_out_data => s04_data,
            p1_out_valid => s06_valid,
            p0_in_valid => s02_valid,
            p1_out_last => s05_last,
            p0_in_data => s00_data,
            p0_out_ready => s03_ready,
            p0_in_last => s01_last,
            reset => reset,
            p1_in_ready => s07_ready
        );
        p1_out_data <= buf;
        p1_out_valid <= '1' when buf_valid = '1' and buf_last = '1' else '0';
        
        s08_data <= buf;
        s09_last <= (others => '0');
        s10_valid <= '1' when buf_valid = '1' and buf_last = '0' else '0';
        
        s12_data <= p0_in_data;
        s13_last <= p0_in_last(s13_last'high downto s13_last'low);
        s14_valid <= '1' when first_element = '0' and p0_in_valid = '1' and buf_last = '0' else '0';
        
        in_ready(0) <= s15_ready(0) when first_element = '0' else ((not buf_valid) or (buf_last and p1_in_ready(p1_in_ready'high)));
        in_ready(1) <= p0_in_last(p0_in_last'high) and in_ready(0);
        p0_out_ready <= in_ready;
        
        -- there will never be the request to repeat the stream, because the result of the reduction is just an element, that can be "reread"
        s07_ready <= "1" when buf_last = '0' else "0";
        
        state_logic: process(clk)
        begin
            if rising_edge(clk) then
                if reset = '1' then
                    first_element <= '1';
                else
                    if p0_in_valid = '1' and in_ready(in_ready'low) = '1' then
                        -- if length of the stream is 1, last is always 1 and first_element must be always '1', too.
                        if p0_in_last(p0_in_last'high) = '1' then
                            first_element <= '1';
                        elsif first_element = '1' then
                            first_element <= '0';
                        end if;
                    end if;
                end if;
            end if;
        end process;
        
        buffer_logic: process(clk)
        begin
            if rising_edge(clk) then
                if reset = '1' then
                    buf <= (others => '0');
                    buf_valid <= '0';
                    buf_last <= '0';
                    f_last <= '0';
                else
                    if buf_valid = '1' and buf_last = '1' and p1_in_ready(p1_in_ready'high) = '1' then
                        buf_valid <= '0';
                        buf_last <= '0';
                    end if;
                    
                    if first_element = '1' then
                        if p0_in_valid = '1' and in_ready(in_ready'low) = '1' and (buf_valid = '0' or (buf_last = '1' and p1_in_ready(p1_in_ready'high) = '1')) then
                            buf <= (others => '0');
                            buf(p0_in_data'high downto p0_in_data'low) <= p0_in_data;
                            buf_valid <= '1';
                            if p0_in_last(p0_in_last'high) = '1' then
                                buf_last <= p0_in_last(p0_in_last'high);
                            end if;
                        end if;
                    else
                        -- new data just entered function f, buffer contains 'old' results and is not valid anymore
                        if s10_valid = '1' and s14_valid = '1' and s11_ready(s11_ready'high) = '1' and s15_ready(s15_ready'high) = '1' then
                            buf_valid <= '0';
                            f_last <= p0_in_last(p0_in_last'high);
                        end if;
                        -- if function f is done, store result in buffer and mark as valid
                        if s06_valid = '1' and s07_ready(s07_ready'high) = '1' then
                            buf <= s04_data(buf'high downto buf'low);
                            buf_valid <= '1';
                            -- seems like this function has 0 cycles of delay => directly forward last signal
                            if s10_valid = '1' and s14_valid = '1' and s15_ready(s15_ready'high) = '1' then
                                buf_last <= p0_in_last(p0_in_last'high);
                            else
                                buf_last <= f_last;
                            end if;
                        end if;
                    end if;
                end if;
            end if;
        end process;
        
    end behavioral;
