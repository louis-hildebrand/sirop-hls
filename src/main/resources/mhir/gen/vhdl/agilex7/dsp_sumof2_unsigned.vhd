library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

library %{LIBRARY}%;

entity dsp_sumof2_unsigned is

    generic(
        AX_WIDTH        : natural;
        AY_WIDTH        : natural;
        BX_WIDTH        : natural;
        BY_WIDTH        : natural;
        RESULT_WIDTH    : natural;
        PIPELINE        : natural);
    port(
        clk         : in    std_logic;
        ena         : in    std_logic;
        ax          : in    unsigned(AX_WIDTH-1 downto 0);
        ay          : in    unsigned(AY_WIDTH-1 downto 0);
        bx          : in    unsigned(BX_WIDTH-1 downto 0);
        by          : in    unsigned(BY_WIDTH-1 downto 0);
        result      : out   unsigned(RESULT_WIDTH-1 downto 0));

end entity;

architecture structural of dsp_sumof2_unsigned is
begin

    DSP : entity %{LIBRARY}%.dsp_sumof2
        generic map(
            USE_SIGNED      => false,
            AX_WIDTH        => AX_WIDTH,
            AY_WIDTH        => AY_WIDTH,
            BX_WIDTH        => BX_WIDTH,
            BY_WIDTH        => BY_WIDTH,
            RESULT_WIDTH    => RESULT_WIDTH,
            PIPELINE        => PIPELINE
        )
        port map(
            clk                => clk,
            ena                => ena,
            ax                 => std_logic_vector(ax),
            ay                 => std_logic_vector(ay),
            bx                 => std_logic_vector(bx),
            by                 => std_logic_vector(by),
            unsigned(result)   => result
        );

end architecture;
