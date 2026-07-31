library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity mac_fallback_unsigned is

    generic(
        AX_WIDTH        : natural;
        AY_WIDTH        : natural;
        B_WIDTH         : natural;
        RESULT_WIDTH    : natural);
    port(
        clk         : in    std_logic;
        ena         : in    std_logic;
        ax          : in    unsigned(AX_WIDTH-1 downto 0);
        ay          : in    unsigned(AY_WIDTH-1 downto 0);
        b           : in    unsigned(B_WIDTH-1 downto 0);
        result      : out   unsigned(RESULT_WIDTH-1 downto 0));

end entity;

architecture behavioral of mac_fallback_unsigned is

    signal output_register : unsigned(RESULT_WIDTH-1 downto 0);

begin

    result <= output_register;

    process
    begin
        wait until rising_edge(clk) and ena = '1';
        output_register <= resize(ax * ay + b, RESULT_WIDTH);
    end process;

end architecture;
