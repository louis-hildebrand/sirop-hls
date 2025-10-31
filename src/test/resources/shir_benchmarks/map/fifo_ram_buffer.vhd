library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity fifo_ram_buffer is
    generic(
        data_width: natural := 32;
        entries: natural := 8
    );
    port(
        clk: in std_logic;
        reset: in std_logic;
        data_in: in std_logic_vector(data_width - 1 downto 0);
        valid_in: in std_logic;
        ready_in: out std_logic_vector(0 downto 0);
        data_out: out std_logic_vector(data_width - 1 downto 0);
        valid_out: out std_logic;
        ready_out: in std_logic_vector(0 downto 0);
        element_count: out natural range 0 to entries
    );
end fifo_ram_buffer;

architecture behavioral of fifo_ram_buffer is
    
    -- do not use when using fewer than 4 entries
    -- use registered implementation for up to 8 entries
    
    component ram_dp
        generic (
            data_width: natural := 32;
            addr_width: natural := 4;
            entries: natural := 8
        );
        port(
            clk: in std_logic;
            data_in: in std_logic_vector(data_width - 1 downto 0);
            write_addr: in std_logic_vector(addr_width - 1 downto 0);
            read_addr: in std_logic_vector(addr_width - 1 downto 0);
            we: in std_logic;
            data_out: out std_logic_vector(data_width - 1 downto 0)
        );
    end component;
    
    component fifo_reg_buffer
        generic(
            data_width: natural := 32;
            entries: natural := 8
        );
        port(
            clk: in std_logic;
            reset: in std_logic;
            data_in: in std_logic_vector(data_width - 1 downto 0);
            valid_in: in std_logic;
            ready_in: out std_logic_vector(0 downto 0);
            data_out: out std_logic_vector(data_width - 1 downto 0);
            valid_out: out std_logic;
            ready_out: in std_logic_vector(0 downto 0);
            element_count: out natural range 0 to entries
        );
    end component;
    
    constant output_buf_length: natural := 3;
    constant fifo_entries: natural := entries - output_buf_length;
    
    signal ram_read_addr: std_logic_vector(integer(ceil(log2(real(fifo_entries)))) downto 0) := (others => '0');
    signal ram_we: std_logic := '0';
    signal ram_write_addr: std_logic_vector(ram_read_addr'range) := (others => '0');
    
    signal new_element: std_logic := '0';
    
    signal fifo_count: natural range 0 to fifo_entries := 0;
    
    subtype fifo_index_type is natural range 0 to fifo_entries - 1;
    signal next_free_fifo_entry: fifo_index_type := 0;
    signal first_used_fifo_entry: fifo_index_type := 0;
    
    signal fifo_out_data: std_logic_vector(data_out'range) := (others => '0');
    signal fifo_out_valid: std_logic := '0';
    
    signal output_buf_count: natural range 0 to output_buf_length := 0;
    
    signal in_ready: std_logic_vector(ready_in'range) := (others => '0');
    signal out_valid: std_logic := '0';
    
begin
    
    inferred_ram: ram_dp
    generic map(
        data_width => data_out'length,
        addr_width => ram_read_addr'length,
        entries => fifo_entries
        )
        port map(
            clk => clk,
            data_in => data_in,
            write_addr => ram_write_addr,
            read_addr => ram_read_addr,
            we => ram_we,
            data_out => fifo_out_data
        );
        
        output_buf: fifo_reg_buffer
        generic map(
            data_width => data_out'length,
            entries => output_buf_length
            )
            port map(
                clk => clk,
                reset => reset,
                data_in => fifo_out_data,
                valid_in => fifo_out_valid,
                ready_in => open,
                data_out => data_out,
                valid_out => valid_out,
                ready_out => ready_out,
                element_count => output_buf_count
            );
            
            element_count <= fifo_count + output_buf_count;
            
            ram_write_addr <= std_logic_vector(to_unsigned(next_free_fifo_entry, ram_write_addr'length));
            ram_read_addr <= std_logic_vector(to_unsigned(first_used_fifo_entry, ram_read_addr'length));
            
            in_ready <= "1" when fifo_count < fifo_entries else "0";
            ready_in <= in_ready;
            
            ram_we <= valid_in when in_ready = "1" else '0';
            
            buf_logic: process(clk)
                variable fifo_count_v: natural range 0 to (fifo_entries + 1) := 0;
            begin
                if rising_edge(clk) then
                    if reset = '1' then
                        fifo_count <= 0;
                        fifo_out_valid <= '0';
                        next_free_fifo_entry <= 0;
                        first_used_fifo_entry <= 0;
                    else
                        fifo_out_valid <= '0';
                        
                        fifo_count_v := fifo_count;
                        if valid_in = '1' and in_ready = "1" then
                            fifo_count_v := fifo_count_v + 1;
                            if next_free_fifo_entry = fifo_index_type'high then
                                next_free_fifo_entry <= fifo_index_type'low;
                            else
                                next_free_fifo_entry <= next_free_fifo_entry + 1;
                            end if;
                        end if;
                        if fifo_count > 0 and output_buf_count < output_buf_length - 1 then
                            fifo_out_valid <= '1';
                            fifo_count_v := fifo_count_v - 1;
                            if first_used_fifo_entry = fifo_index_type'high then
                                first_used_fifo_entry <= fifo_index_type'low;
                            else
                                first_used_fifo_entry <= first_used_fifo_entry + 1;
                            end if;
                        end if;
                        fifo_count <= fifo_count_v;
                    end if;
                end if;
            end process;
            
        end behavioral;
