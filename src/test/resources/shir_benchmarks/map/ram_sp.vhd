library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use work.common.all;

-- https://forums.intel.com/s/question/0D50P00003yyGQtSAM/quartus-own-single-port-ram-template-infers-dual-port-ram-with-passthrough-logic?language=en_US
entity ram_sp is
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
end ram_sp;

architecture rtl of ram_sp is
    type ram_type is array(0 to entries - 1) of std_logic_vector(data_in'range);
    signal ram_block: ram_type;
begin
    process(clk)
    begin
        if rising_edge(clk) then
            data_out <= ram_block(to_integer(unsigned(addr)));
            if we = '1' then
                ram_block(to_integer(unsigned(addr))) <= data_in;
                -- Read returns NEW data at addr if we == '1'. This is the natural behavior of TriMatrix memory blocks in Single Port mode
                data_out <= data_in;
            end if;
        end if;
    end process;
end rtl;
