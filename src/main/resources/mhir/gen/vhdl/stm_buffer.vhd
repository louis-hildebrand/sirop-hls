library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;
use work.conversions.all;

-- A streaming component that buffers one element.
-- This is needed to prevent combinational loops in LetStm.
entity stm_buffer is
generic (
    BIT_WIDTH : integer);
port (
    clk            : in  std_logic;
    -- Handshake with consumer
    data_out       : out std_logic_vector(BIT_WIDTH - 1 downto 0);
    valid_out      : out std_logic;
    consumer_ready : in  std_logic;
    -- Handshake with producer
    data_in        : in  std_logic_vector(BIT_WIDTH - 1 downto 0);
    valid_in       : in  std_logic;
    producer_ready : out std_logic);
end stm_buffer;

architecture arch of stm_buffer is
    signal data_reg    : std_logic_vector(BIT_WIDTH - 1 downto 0);
    signal valid_reg   : boolean := false;
    signal can_update  : boolean;
begin
    -- Outputs
    data_out <= data_reg;
    valid_out <= bool2sl(valid_reg);
    producer_ready <= bool2sl(can_update);

    -- Internal signals
    can_update <= not valid_reg or sl2bool(consumer_ready);

    process
    begin
        wait until rising_edge(clk);
        if can_update then
            data_reg <= data_in;
            valid_reg <= sl2bool(valid_in);
        end if;
    end process;
end arch;
