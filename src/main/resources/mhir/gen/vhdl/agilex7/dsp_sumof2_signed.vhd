library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

library %{LIBRARY}%;

entity dsp_sumof2_signed is

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
        ax          : in    signed(AX_WIDTH-1 downto 0);
        ay          : in    signed(AY_WIDTH-1 downto 0);
        bx          : in    signed(BX_WIDTH-1 downto 0);
        by          : in    signed(BY_WIDTH-1 downto 0);
        result      : out   signed(RESULT_WIDTH-1 downto 0));

end entity;

architecture structural of dsp_sumof2_signed is
begin

    DSP : entity %{LIBRARY}%.dsp_sumof2
        generic map(
            USE_SIGNED      => true,
            AX_WIDTH        => AX_WIDTH,
            AY_WIDTH        => AY_WIDTH,
            BX_WIDTH        => BX_WIDTH,
            BY_WIDTH        => BY_WIDTH,
            RESULT_WIDTH    => RESULT_WIDTH,
            PIPELINE        => PIPELINE
        )
        port map(
            clk              => clk,
            ena              => ena,
            ax               => std_logic_vector(ax),
            ay               => std_logic_vector(ay),
            bx               => std_logic_vector(bx),
            by               => std_logic_vector(by),
            signed(result)   => result
        );

end architecture;
