library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use work.common.all;

entity fifo_reg_buffer is
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
end fifo_reg_buffer;

architecture behavioral of fifo_reg_buffer is
    
    type buf_type is array (0 to entries - 1) of std_logic_vector(data_width - 1 downto 0);
    signal buf: buf_type := (others => (others => '0'));
    signal buf_count: natural range 0 to (buf_type'high + 1) := 0;
    
    subtype buf_index_type is natural range buf_type'range;
    signal next_free_buf_entry: buf_index_type := 0;
    signal first_used_buf_entry: buf_index_type := 0;
    
    signal buf_full: std_logic := '0';
    signal buf_empty: std_logic := '0';
    
begin
    
    buf_full <= '0' when buf_count <= buf_type'high else '1';
    ready_in(0) <= not buf_full;
    
    data_out <= buf(first_used_buf_entry);
    
    buf_empty <= '0' when buf_count > 0 else '1';
    valid_out <= not buf_empty;
    
    element_count <= buf_count;
    
    buf_logic: process(clk)
        variable buf_count_v: natural range 0 to (buf_type'high + 2) := 0;
    begin
        if rising_edge(clk) then
            if reset = '1' then
                -- commented, reason: see below ...
                -- buf <= (others => (others => '0')); -- enfore the use of registers instead of ram blocks
                buf_count <= 0;
                next_free_buf_entry <= 0;
                first_used_buf_entry <= 0;
            else
                -- router hat problems with the reset signal for this output buffer
                if buf_count = 0 then
                    buf <= (others => (others => '0')); -- enfore the use of registers instead of ram blocks
                end if;
                
                buf_count_v := buf_count;
                if valid_in = '1' and buf_full = '0' then
                    buf(next_free_buf_entry) <= data_in;
                    buf_count_v := buf_count_v + 1;
                    if next_free_buf_entry = buf_index_type'high then
                        next_free_buf_entry <= buf_index_type'low;
                    else
                        next_free_buf_entry <= next_free_buf_entry + 1;
                    end if;
                end if;
                if buf_empty = '0' and ready_out(ready_out'low) = '1' then
                    buf_count_v := buf_count_v - 1;
                    if first_used_buf_entry = buf_index_type'high then
                        first_used_buf_entry <= buf_index_type'low;
                    else
                        first_used_buf_entry <= first_used_buf_entry + 1;
                    end if;
                end if;
                buf_count <= buf_count_v;
            end if;
        end if;
    end process;
    
end behavioral;
