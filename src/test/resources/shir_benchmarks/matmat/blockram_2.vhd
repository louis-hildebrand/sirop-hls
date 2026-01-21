library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity blockram_2 is
    generic(
        entries: type_NaturalNumberType := 16;
        data_width: type_NaturalNumberType := 256
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_NamedTupleTypeTextTypeaddrIntTypeArithType5TextTypedataIntTypeArithType256TextTypeweLogicType_addr;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_IntTypeArithType256;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end blockram_2;

architecture behavioral of blockram_2 is
    component ram_sp
        generic (
            data_width: natural := 32;
            addr_width: natural := 4;
            entries: natural := 8
        );
        port(
            clk: in std_logic;
            data_in: in std_logic_vector(data_width - 1 downto 0);
            addr: in std_logic_vector(addr_width - 1 downto 0);
            we: in std_logic;
            data_out: out std_logic_vector(data_width - 1 downto 0)
        );
    end component;
    
    
    signal ram_in_we: std_logic := '0';
    signal ram_out_valid: std_logic := '0';
    signal ram_out_data: std_logic_vector(data_width - 1 downto 0) := (others => '0');
    
    signal pipeline_count: natural range 0 to 1 := 0;
    
    type output_buffer_type is array (0 to 2) of std_logic_vector(p0_in_data.data'range);
    signal output_buffer: output_buffer_type;
    signal output_buffer_count: natural range 0 to (output_buffer_type'high + 1) := 0;
    
    -- force Quartus to implement output_buffer with FF's (and not BRAM).
    attribute ramstyle: string;
    attribute ramstyle of output_buffer: signal is "logic";
    
    subtype output_buffer_index_type is natural range output_buffer_type'range;
    signal next_free_output_buffer_entry: output_buffer_index_type := 0;
    signal first_used_output_buffer_entry: output_buffer_index_type := 0;
    
    signal in_ready: std_logic_vector(p0_out_ready'range) := (others => '0');
    signal out_valid: std_logic := '0';
    
    
begin
    inferred_ram: ram_sp
    generic map(
        data_width => data_width,
        addr_width => p0_in_data.addr'length,
        entries => entries
        )
        port map(
            clk => clk,
            data_in => p0_in_data.data,
            addr => p0_in_data.addr,
            we => ram_in_we,
            data_out => ram_out_data
        );
        ram_in_we <= p0_in_data.we and p0_in_valid;
        
        in_ready <= "1" when output_buffer_count + pipeline_count <= output_buffer_type'high else "0";
        p0_out_ready <= in_ready;
        
        p1_out_data <= output_buffer(first_used_output_buffer_entry);
        --output_conversion: process(output_data_buffer)
            --begin
            --    for i in p1_out_data'range loop
                --        for j in p1_out_data(0)'range loop
                    --            p1_out_data(i)(j) <= output_data_buffer(i * p1_out_data(0)'length + j);
                    --        end loop;
                    --    end loop;
                    --end process;
                    p1_out_last <= (others => '0');
                    out_valid <= '1' when output_buffer_count > 0 else '0';
                    p1_out_valid <= out_valid;
                    
                    pipeline_counter: process(clk)
                        variable pipeline_count_v: natural range 0 to (1 + 1) := 0;
                    begin
                        if rising_edge(clk) then
                            if reset = '1' then
                                pipeline_count <= 0;
                            else
                                pipeline_count_v := pipeline_count;
                                if p0_in_valid = '1' and in_ready = "1" then
                                    pipeline_count_v := pipeline_count_v + 1;
                                end if;
                                if ram_out_valid = '1' then
                                    pipeline_count_v := pipeline_count_v - 1;
                                end if;
                                pipeline_count <= pipeline_count_v;
                            end if;
                        end if;
                    end process;
                    
                    store_logic: process(clk)
                    begin
                        if rising_edge(clk) then
                            if ram_out_valid = '1' then
                                output_buffer(next_free_output_buffer_entry) <= ram_out_data;
                            end if;
                        end if;
                    end process;
                    
                    output_buffer_logic: process(clk)
                        variable output_buffer_count_v: natural range 0 to (output_buffer_type'high + 2) := 0;
                    begin
                        if rising_edge(clk) then
                            if reset = '1' then
                                output_buffer_count <= 0;
                                next_free_output_buffer_entry <= 0;
                                first_used_output_buffer_entry <= 0;
                            else
                                output_buffer_count_v := output_buffer_count;
                                if ram_out_valid = '1' then
                                    output_buffer_count_v := output_buffer_count_v + 1;
                                    if next_free_output_buffer_entry = output_buffer_index_type'high then
                                        next_free_output_buffer_entry <= output_buffer_index_type'low;
                                    else
                                        next_free_output_buffer_entry <= next_free_output_buffer_entry + 1;
                                    end if;
                                end if;
                                if out_valid = '1' and p1_in_ready(p1_in_ready'low) = '1' then
                                    output_buffer_count_v := output_buffer_count_v - 1;
                                    if first_used_output_buffer_entry = output_buffer_index_type'high then
                                        first_used_output_buffer_entry <= output_buffer_index_type'low;
                                    else
                                        first_used_output_buffer_entry <= first_used_output_buffer_entry + 1;
                                    end if;
                                end if;
                                output_buffer_count <= output_buffer_count_v;
                            end if;
                        end if;
                    end process;
                    
                    pipeline_logic: process(clk)
                    begin
                        if rising_edge(clk) then
                            if reset = '1' then
                                ram_out_valid <= '0';
                            else
                                ram_out_valid <= '0';
                                if p0_in_valid = '1' and in_ready = "1" then
                                    ram_out_valid <= '1';
                                end if;
                            end if;
                        end if;
                    end process;
                    
                end behavioral;
