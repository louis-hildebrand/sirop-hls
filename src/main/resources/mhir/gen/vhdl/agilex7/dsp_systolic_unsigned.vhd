library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity dsp_systolic_unsigned is

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
        ax          : in    unsigned(AX_WIDTH-1 downto 0);
        ay          : in    unsigned(AY_WIDTH-1 downto 0);
        bx          : in    unsigned(BX_WIDTH-1 downto 0);
        by          : in    unsigned(BY_WIDTH-1 downto 0);
        chainin     : in    unsigned(43 downto 0);
        chainout    : out   unsigned(43 downto 0);
        result      : out   unsigned(RESULT_WIDTH-1 downto 0));

end entity;

architecture structural of dsp_systolic_unsigned is

    signal chainout_slv : std_logic_vector(43 downto 0);
    signal result_slv   : std_logic_vector(RESULT_WIDTH-1 downto 0);

begin

    DSP : entity work.dsp_systolic
        generic map(
            USE_SIGNED      => false,
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

    chainout <= unsigned(chainout_slv);
    result <= unsigned(result_slv);

end architecture;
