library IEEE;
use IEEE.numeric_std.all;
use IEEE.std_logic_1164.all;

-- Dual-port block RAM
-- Based on Meher Krishna Patel's dual_port_RAM.vhd in section 11.4.3 of the
-- tutorial "FPGA designs with VHDL" and this StackOverflow thread:
-- https://stackoverflow.com/q/19751148.
entity dual_port_ram is
    generic (
        ADDR_WIDTH : integer;  -- address width
        RAM_LEN    : integer;  -- number of elements in the RAM
        DATA_WIDTH : integer); -- width of the elements stored in the RAM
    port (
        clk:       in  std_logic;
        rd_addr:   in  unsigned(ADDR_WIDTH-1 downto 0);
        rd_data:   out std_logic_vector(DATA_WIDTH-1 downto 0);
        wr_enable: in  std_logic;
        wr_addr:   in  unsigned(ADDR_WIDTH-1 downto 0);
        wr_data:   in  std_logic_vector(DATA_WIDTH-1 downto 0));
end dual_port_ram;

architecture arch of dual_port_ram is

    type memory_type is array(0 to RAM_LEN-1) of std_logic_vector(DATA_WIDTH-1 downto 0);

    signal ram: memory_type;

begin

    rd_data <= ram(to_integer(rd_addr));

    process
    begin
        wait until rising_edge(clk);
        if wr_enable = '1' then
            ram(to_integer(wr_addr)) <= wr_data;
        end if;
    end process;

end arch;
