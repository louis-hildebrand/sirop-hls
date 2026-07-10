library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity dsp_systolic_signed is

    generic(
        AX_WIDTH        : natural;
        AY_WIDTH        : natural;
        BX_WIDTH        : natural;
        BY_WIDTH        : natural;
        RESULT_WIDTH    : natural;
        PIPELINE        : natural;
        ENABLE_CHAININ  : boolean);
    port(
        clk         : in    std_logic;
        ena         : in    std_logic;
        ax          : in    signed(AX_WIDTH-1 downto 0);
        ay          : in    signed(AY_WIDTH-1 downto 0);
        bx          : in    signed(BX_WIDTH-1 downto 0);
        by          : in    signed(BY_WIDTH-1 downto 0);
        chainin     : in    signed(43 downto 0);
        chainout    : out   signed(43 downto 0);
        result      : out   signed(RESULT_WIDTH-1 downto 0));

end entity;

architecture structural of dsp_systolic_signed is

    signal chainout_slv : std_logic_vector(43 downto 0);
    signal result_slv   : std_logic_vector(RESULT_WIDTH-1 downto 0);

begin

    DSP : entity work.dsp_systolic
        generic map(
            USE_SIGNED      => true,
            AX_WIDTH        => AX_WIDTH,
            AY_WIDTH        => AY_WIDTH,
            BX_WIDTH        => BX_WIDTH,
            BY_WIDTH        => BY_WIDTH,
            RESULT_WIDTH    => RESULT_WIDTH,
            PIPELINE        => PIPELINE,
            ENABLE_CHAININ  => ENABLE_CHAININ
        )
        port map(
            clk      => clk,
            ena      => ena,
            ax       => std_logic_vector(ax),
            ay       => std_logic_vector(ay),
            bx       => std_logic_vector(bx),
            by       => std_logic_vector(by),
            chainin  => std_logic_vector(chainin),
            chainout => chainout_slv,
            result   => result_slv
        );

    chainout <= signed(chainout_slv);
    result <= signed(result_slv);

end architecture;
