library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity mac_fallback_signed is

    generic(
        AX_WIDTH        : natural;
        AY_WIDTH        : natural;
        B_WIDTH         : natural;
        RESULT_WIDTH    : natural);
    port(
        clk         : in    std_logic;
        ena         : in    std_logic;
        ax          : in    signed(AX_WIDTH-1 downto 0);
        ay          : in    signed(AY_WIDTH-1 downto 0);
        b           : in    signed(B_WIDTH-1 downto 0);
        result      : out   signed(RESULT_WIDTH-1 downto 0));

end entity;

architecture behavioral of mac_fallback_signed is

    signal output_register : signed(RESULT_WIDTH-1 downto 0);

begin

    result <= output_register;

    process
    begin
        wait until rising_edge(clk) and ena = '1';
        output_register <= resize(ax * ay + b, RESULT_WIDTH);
    end process;

end architecture;
