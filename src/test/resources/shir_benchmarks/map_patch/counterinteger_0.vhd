library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity counterinteger_0 is
    port(
        clk: in std_logic;
        reset: in std_logic;
        p0_out_data: out std_logic_vector(7 downto 0);
        p0_out_last: out std_logic_vector(0 downto 0);
        p0_out_valid: out std_logic;
        p0_in_ready: in std_logic_vector(1 downto 0)
    );
end counterinteger_0;

architecture behavioral of counterinteger_0 is
    constant N : integer := 8;
    signal lfsr : std_logic_vector(N-1 downto 0) := (others => '1');
begin
    process
    begin
        wait until rising_edge(clk);
        lfsr(N-2 downto 0) <= lfsr(N-1 downto 1);
        lfsr(N-1) <= lfsr(2) xor lfsr(3);
    end process;

    p0_out_data <= lfsr;
    p0_out_last <= (others => '0');
    p0_out_valid <= '1';
end behavioral;
