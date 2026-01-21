library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity arbitersyncfunction_116 is
    generic(
        num_clients: type_NaturalNumberType := 2
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_out_data: out type_NamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
        p0_out_last: out type_LastVectorTypeArithType0;
        p0_out_valid: out type_LogicType;
        p0_in_ready: in type_ReadyVectorTypeArithType0;
        p1_in_data: in type_IntTypeArithType256;
        p1_in_last: in type_LastVectorTypeArithType0;
        p1_in_valid: in type_LogicType;
        p1_out_ready: out type_ReadyVectorTypeArithType0;
        p2_in_data: in type_VectorTypeNamedTupleTypeTextTypeaddrIntTypeArithType13TextTypedataIntTypeArithType256TextTypeweLogicTypeArithType2;
        p3_in_data: in type_VectorTypeLastVectorTypeArithType0ArithType2;
        p4_in_data: in type_VectorTypeLogicTypeArithType2;
        p5_out_data: out type_VectorTypeReadyVectorTypeArithType0ArithType2;
        p6_out_data: out type_VectorTypeIntTypeArithType256ArithType2;
        p7_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType2;
        p8_out_data: out type_VectorTypeLogicTypeArithType2;
        p9_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType2
    );
end arbitersyncfunction_116;

architecture behavioral of arbitersyncfunction_116 is
    
    
    
    subtype client_id_type is natural range 0 to num_clients-1;
    
    signal selected_client: client_id_type := 0;
    
    signal pipeline_count: natural := 0;
    
    signal start_arrival: std_logic := '0';
    
    
begin
    
    -- to master
    p0_out_data <= p2_in_data(selected_client);
    p0_out_last <= p3_in_data(selected_client);
    p0_out_valid <= p4_in_data(selected_client);
    master_ready_signal: process(selected_client, p0_in_ready)
    begin
        p5_out_data <= (others => (others => '0'));
        p5_out_data(selected_client) <= p0_in_ready;
    end process;
    
    -- from master
    p6_out_data <= (others => p1_in_data);
    p7_out_data <= (others => p1_in_last);
    clients_valid_signal: process(selected_client, p1_in_valid)
    begin
        p8_out_data <= (others => '0');
        p8_out_data(selected_client) <= p1_in_valid;
    end process;
    p1_out_ready <= p9_in_data(selected_client);
    
    -- simple round robin WITH possible wait times! (if a client is not ready to make a request)
    select_client_logic: process(clk)
        variable next_client: client_id_type := 0;
    begin
        if rising_edge(clk) then
            if reset = '1' then
                selected_client <= 0;
            else
                if selected_client = client_id_type'high then
                    next_client := client_id_type'low;
                else
                    next_client := selected_client + 1;
                end if;
                -- remove second condition here to go back to true round robin without possible starvation of clients
                if pipeline_count = 0 and p4_in_data(selected_client) = '0' and start_arrival = '0' then
                    selected_client <= next_client;
                end if;
            end if;
        end if;
    end process;
    
    pipeline_counter: process(clk)
        variable pipeline_count_v: natural := 0;
    begin
        if rising_edge(clk) then
            if reset = '1' then
                pipeline_count <= 0;
            else
                pipeline_count_v := pipeline_count;
                if p4_in_data(selected_client) = '1' and p0_in_ready(p0_in_ready'low) = '1' then
                    pipeline_count_v := pipeline_count_v + 1;
                end if;
                if p1_in_valid = '1' and p9_in_data(selected_client)(p0_in_ready'low) = '1' then
                    pipeline_count_v := pipeline_count_v - 1;
                end if;
                pipeline_count <= pipeline_count_v;
            end if;
        end if;
    end process;
    
    start_logic: process(clk)
        constant last_all_one: std_logic_vector(p3_in_data(0)'range) := (others => '1');
    begin
        if rising_edge(clk) then
            if reset = '1' then
                start_arrival <= '0';
            else
                if p4_in_data(selected_client) = '1' and p0_in_ready(p0_in_ready'low) = '1' then
                    if p3_in_data(selected_client) = last_all_one then
                        start_arrival <= '0';
                    else
                        start_arrival <= '1';
                    end if;
                end if;
            end if;
        end if;
    end process;
    
end behavioral;
