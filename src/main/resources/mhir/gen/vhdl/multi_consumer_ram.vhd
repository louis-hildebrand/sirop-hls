library IEEE;
use IEEE.numeric_std.all;
use IEEE.std_logic_1164.all;

-- BRAM with (potentially) more than one consumer, each (potentially) reading from a different address.
-- There is only one writer.
entity multi_consumer_ram is
    generic (
        ADDR_WIDTH  : integer;  -- address width
        RAM_LEN     : integer;  -- number of elements in each RAM
        DATA_WIDTH  : integer;  -- width of the elements stored in the RAM
        N_CONSUMERS : integer); -- number of consumers
    port (
        clk:       in  std_logic;
        rd_addr:   in  std_logic_vector(N_CONSUMERS*ADDR_WIDTH-1 downto 0);
        rd_data:   out std_logic_vector(N_CONSUMERS*DATA_WIDTH-1 downto 0);
        wr_enable: in  std_logic;
        wr_addr:   in  unsigned(ADDR_WIDTH-1 downto 0);
        wr_data:   in  std_logic_vector(DATA_WIDTH-1 downto 0));
end multi_consumer_ram;

architecture arch of multi_consumer_ram is

    type rd_addr_array_type is array (0 to N_CONSUMERS-1) of unsigned(ADDR_WIDTH-1 downto 0);
    type rd_data_array_type is array (0 to N_CONSUMERS-1) of std_logic_vector(DATA_WIDTH-1 downto 0);

    signal rd_addr_array : rd_addr_array_type;
    signal rd_data_array : rd_data_array_type;

begin

    gen_ram : for i in 0 to N_CONSUMERS-1 generate
        DPRAM : entity work.dual_port_ram
            generic map (
                ADDR_WIDTH => ADDR_WIDTH,
                RAM_LEN => RAM_LEN,
                DATA_WIDTH => DATA_WIDTH)
            port map(
                clk       => clk,
                rd_addr   => rd_addr_array(i),
                rd_data   => rd_data_array(i),
                wr_enable => wr_enable,
                wr_addr   => wr_addr,
                wr_data   => wr_data);
    end generate gen_ram;

    proc_rd_addr_array : process (rd_addr)
        variable msb : natural range 0 to N_CONSUMERS*ADDR_WIDTH-1;
        variable lsb : natural range 0 to N_CONSUMERS*ADDR_WIDTH-1;
    begin
        for i in 0 to N_CONSUMERS-1 loop
            msb := (N_CONSUMERS - i) * ADDR_WIDTH - 1;
            lsb := msb - ADDR_WIDTH + 1;
            rd_addr_array(i) <= unsigned(rd_addr(msb downto lsb));
        end loop;
    end process;

    proc_rd_data_array : process (rd_data_array)
        variable msb : natural range 0 to N_CONSUMERS*DATA_WIDTH-1;
        variable lsb : natural range 0 to N_CONSUMERS*DATA_WIDTH-1;
    begin
        for i in 0 to N_CONSUMERS-1 loop
            msb := (N_CONSUMERS - i) * DATA_WIDTH - 1;
            lsb := msb - DATA_WIDTH + 1;
            rd_data(msb downto lsb) <= rd_data_array(i);
        end loop;
    end process;

end arch;
