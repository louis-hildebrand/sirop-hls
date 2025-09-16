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
    c_data  : out std_logic_vector(BIT_WIDTH - 1 downto 0);
    c_valid : out std_logic;
    c_ready : in  std_logic;
    -- Handshake with producer
    p_data  : in  std_logic_vector(BIT_WIDTH - 1 downto 0);
    p_valid : in  std_logic;
    p_ready : out std_logic);
end stm_nop;

architecture arch of stm_nop is
begin
    c_data <= p_data;
    c_valid <= p_valid;
    p_ready <= c_ready;
end arch;
