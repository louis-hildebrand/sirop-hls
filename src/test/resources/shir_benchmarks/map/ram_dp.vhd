library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use work.common.all;

entity ram_dp is
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
end ram_dp;

architecture rtl of ram_dp is
    type ram_type is array(0 to entries - 1) of std_logic_vector(data_in'range);
    signal ram_block: ram_type;
begin
    process(clk)
    begin
        if rising_edge(clk) then
            if we = '1' then
                ram_block(to_integer(unsigned(write_addr))) <= data_in;
            end if;
            data_out <= ram_block(to_integer(unsigned(read_addr)));
            -- VHDL semantics imply that data_out doesn't get data in this clk cycle
        end if;
    end process;
end rtl;
