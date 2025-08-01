library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

-- A no-op stream component.
-- This is sometimes useful for implementing LetStm.
entity stm_nop is
generic (
    BIT_WIDTH : integer);
port (
    -- Handshake with consumer
    data_out       : out std_logic_vector(BIT_WIDTH - 1 downto 0);
    valid_out      : out std_logic;
    consumer_ready : in  std_logic;
    -- Handshake with producer
    data_in        : in  std_logic_vector(BIT_WIDTH - 1 downto 0);
    valid_in       : in  std_logic;
    producer_ready : out std_logic);
end stm_nop;

architecture arch of stm_nop is
begin
    data_out <= data_in;
    valid_out <= valid_in;
    producer_ready <= consumer_ready;
end arch;
