library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity reduceorderedstream_84 is
    generic(
        pipeline_length: type_NaturalNumberType := 2
    );
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
end reduceorderedstream_84;

architecture behavioral of reduceorderedstream_84 is
    
    component writesync_81
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_in_data: in type_BaseAddrTypeArithType1BlockRamTypeIdType2;
            p0_in_last: in type_LastVectorTypeArithType0;
            p0_in_valid: in type_LogicType;
            p0_out_ready: out type_ReadyVectorTypeArithType0;
            p1_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType256TextTypet1IntTypeArithType12_t0;
            p1_in_last: in type_LastVectorTypeArithType0;
            p1_in_valid: in type_LogicType;
            p1_out_ready: out type_ReadyVectorTypeArithType0;
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
    
    type state_type is (init_acc, reducing);
    signal state: state_type := init_acc;
    
    -- accumulates the values in this stream
    signal buf: std_logic_vector(p2_out_data'range) := (others => '0');
    signal buf_valid: std_logic := '1'; -- buffer contains a value that is ready to be processes for the next iteration
        signal buf_last: std_logic := '0'; -- if the buffer contains the reduction up to the last value of the stream => output is ready!
        signal f_last: std_logic := '0'; -- if the function is currently working of the last value of the stream
        
        
        signal pipeline_count: natural range 0 to pipeline_length := 0;
        signal first_arrival_reg: std_logic := '0';
        signal first_arrival: std_logic := '0';
        signal last_emission: std_logic := '0';
        signal last_emission_reg: std_logic := '0';
        constant p1_in_last_all_one: std_logic_vector(p1_in_last'range) := (others => '1');
        
        signal s00_data: type_BaseAddrTypeArithType1BlockRamTypeIdType2;
        signal s01_last: type_LastVectorTypeArithType0;
        signal s02_valid: type_LogicType;
        signal s03_ready: type_ReadyVectorTypeArithType0;
        signal s04_data: type_BaseAddrTypeArithType1BlockRamTypeIdType2;
        signal s05_last: type_LastVectorTypeArithType0;
        signal s06_valid: type_LogicType;
        signal s07_ready: type_ReadyVectorTypeArithType0;
        signal s08_data: type_NamedTupleTypeTextTypet0IntTypeArithType256TextTypet1IntTypeArithType12_t0;
        signal s09_last: type_LastVectorTypeArithType0;
        signal s10_valid: type_LogicType;
        signal s11_ready: type_ReadyVectorTypeArithType0;
    begin
        
        U0_writesync: writesync_81 port map(
            clk => clk,
            p2_out_valid => s02_valid,
            p1_in_last => s09_last,
            p0_in_valid => s06_valid,
            p4_in_last => p4_in_last,
            p3_out_valid => p3_out_valid,
            p3_out_data => p3_out_data,
            p4_in_valid => p4_in_valid,
            p2_out_last => s01_last,
            p0_out_ready => s07_ready,
            p2_out_data => s00_data,
            p1_in_data => s08_data,
            p1_in_valid => s10_valid,
            p2_in_ready => s03_ready,
            p3_out_last => p3_out_last,
            p0_in_last => s05_last,
            reset => reset,
            p4_out_ready => p4_out_ready,
            p0_in_data => s04_data,
            p4_in_data => p4_in_data,
            p1_out_ready => s11_ready,
            p3_in_ready => p3_in_ready
        );
        p2_out_data <= buf;
        p2_out_valid <= '1' when state = reducing and buf_valid = '1' and buf_last = '1' else '0';
        
        s04_data <= buf;
        s05_last <= (others => '0');
        -- After sending last item, valid signal must be de-asserted.
        s06_valid <= '1' when state = reducing and buf_valid = '1' and buf_last = '0' and last_emission_reg = '0' else '0';
        
        s08_data <= p1_in_data;
        s09_last <= p1_in_last(s09_last'high downto s09_last'low);
        -- After sending last item, valid signal must be de-asserted.
        s10_valid <= '1' when state = reducing and p1_in_valid = '1' and buf_last = '0' and last_emission_reg = '0' else '0';
        
        p1_out_ready <= (p1_in_last(p1_in_last'high) and s11_ready(s11_ready'high)) & s11_ready when state = reducing and last_emission_reg = '0' else (others => '0');
        -- there will never be the request to repeat the stream, because the result of the reduction is just an element, that can be "reread"
        p0_out_ready <= "1" when state = init_acc else "0";
        s03_ready <= "1" when state = reducing and buf_last = '0' else "0";
        
        state_logic: process(clk)
        begin
            if rising_edge(clk) then
                if reset = '1' then
                    state <= init_acc;
                else
                    case state is
                        when init_acc =>
                        if p0_in_valid = '1' then
                            state <= reducing;
                        end if;
                        when reducing =>
                        -- reset buffer to initial_value, when the final value has been transmitted
                        if buf_valid = '1' and buf_last = '1' and p2_in_ready(p2_in_ready'high) = '1' then
                            state <= init_acc;
                        end if;
                    end case;
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
                    case state is
                        when init_acc =>
                        if p0_in_valid = '1' then
                            buf <= (others => '0');
                            buf(p0_in_data'high downto p0_in_data'low) <= p0_in_data;
                            buf_valid <= '1';
                            buf_last <= '0';
                        end if;
                        -- f_last implies function's last signal. Need to be reset.
                        f_last <= '0';
                        when reducing =>
                        -- new data just entered function f, buffer contains 'old' results and is not valid anymore
                        -- After sending last item, buf_valid should not be de-asserted. (There must be a valid item in buffer.)
                        if s06_valid = '1' and s10_valid = '1' and s07_ready(s07_ready'high) = '1' and s11_ready(s11_ready'high) = '1' and last_emission_reg = '0' then
                            buf_valid <= '0';
                            f_last <= p1_in_last(p1_in_last'high);
                        end if;
                        -- if function f is done, store result in buffer and mark as valid.
                        -- Note that pipeline_count will only be valid after first item arrrives.
                        if (s02_valid = '1' and s03_ready(s03_ready'high) = '1') or (pipeline_count < pipeline_length and (first_arrival = '1' or first_arrival_reg = '1')) then
                            buf <= s00_data(buf'high downto buf'low);
                            buf_valid <= '1';
                            -- when pipeline_count is larger than zero, there are still some items processed by funtion f. Must wait until all items are finished.
                                if pipeline_count > 0 then
                                    buf_last <= '0';
                                    -- seems like this function has 0 cycles of delay => directly forward last signal
                                elsif (s06_valid = '1' and s10_valid = '1' and s11_ready(s11_ready'high) = '1') and last_emission_reg = '0' then
                                    buf_last <= p1_in_last(p1_in_last'high);
                                else
                                    buf_last <= f_last;
                                end if;
                            end if;
                        end case;
                    end if;
                end if;
            end process;
            
            -- Counting the items that are sent to function f but not returned.
            queue_logic: process(clk)
                variable pipeline_count_v: natural range 0 to pipeline_length := 0;
            begin
                if rising_edge(clk) then
                    if reset = '1' then
                        pipeline_count <= 0;
                    else
                        pipeline_count_v := pipeline_count;
                        if s06_valid = '1' and s10_valid = '1' and s07_ready(s07_ready'high) = '1' and s11_ready(s11_ready'high) = '1' and pipeline_count < pipeline_length and last_emission_reg = '0' then
                            pipeline_count_v := pipeline_count_v + 1;
                        end if;
                        
                        if s02_valid = '1' and s03_ready(s03_ready'high) = '1' and pipeline_count > 0 then
                            pipeline_count_v := pipeline_count_v - 1;
                        end if;
                        
                        pipeline_count <= pipeline_count_v;
                    end if;
                end if;
            end process;
            
            -- first_arrival indicates that first valid data arrives.
            first_arrival <= '1' when state = reducing and p1_in_valid = '1' and p0_in_valid = '1' else '0';
            
            first_arrival_logic: process(clk)
            begin
                if rising_edge(clk) then
                    if reset = '1' then
                        first_arrival_reg <= '0';
                    else
                        if state = init_acc then
                            first_arrival_reg <= '0';
                        elsif first_arrival_reg = '0' then
                            first_arrival_reg <= first_arrival;
                        end if;
                    end if;
                end if;
            end process;
            
            -- last_emission indicates that last valid data is sent to function f.
            last_emission <= '1' when state = reducing and p1_in_valid = '1' and p1_in_last = p1_in_last_all_one and s11_ready(s11_ready'low) = '1' else '0';
            
            last_emission_logic: process(clk)
            begin
                if rising_edge(clk) then
                    if reset = '1' then
                        last_emission_reg <= '0';
                    else
                        if state = init_acc then
                            last_emission_reg <= '0';
                        elsif last_emission_reg = '0' then
                            last_emission_reg <= last_emission;
                        end if;
                    end if;
                end if;
            end process;
            
        end behavioral;
