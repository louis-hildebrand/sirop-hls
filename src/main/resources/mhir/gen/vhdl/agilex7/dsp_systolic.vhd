library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity dsp_systolic is

    generic(
        AX_WIDTH        : natural;
        AY_WIDTH        : natural;
        BX_WIDTH        : natural;
        BY_WIDTH        : natural;
        RESULT_WIDTH    : natural;
        PIPELINE        : natural;
        USE_SIGNED      : boolean;
        ENABLE_CHAININ  : boolean);
    port(
        clk         : in    std_logic;
        ena         : in    std_logic;
        ax          : in    std_logic_vector(AX_WIDTH-1 downto 0);
        ay          : in    std_logic_vector(AY_WIDTH-1 downto 0);
        bx          : in    std_logic_vector(BX_WIDTH-1 downto 0);
        by          : in    std_logic_vector(BY_WIDTH-1 downto 0);
        chainin     : in    std_logic_vector(43 downto 0);
        chainout    : out   std_logic_vector(43 downto 0);
        result      : out   std_logic_vector(RESULT_WIDTH-1 downto 0));

    pure function ite(c: boolean; t: string; f: string) return string is
    begin
        if c then
            return t;
        else
            return f;
        end if;
    end function;

    pure function ite(c: boolean; t: natural; f: natural) return natural is
    begin
        if c then
            return t;
        else
            return f;
        end if;
    end function;

    constant MAXIMUM_AY_WIDTH : natural := ite(USE_SIGNED, 19, 18);
    constant MAXIMUM_BY_WIDTH : natural := ite(USE_SIGNED, 19, 18);

begin

    assert(AX_WIDTH <= 18)
    report "invalid value for generic AX_WIDTH: " & integer'image(AX_WIDTH) & " (expected value <= 18)"
    severity ERROR;

    assert(AY_WIDTH <= MAXIMUM_AY_WIDTH)
    report "invalid value for generic AY_WIDTH: " & integer'image(AY_WIDTH) & " (expected value <= " & integer'image(MAXIMUM_AY_WIDTH) & ")"
    severity ERROR;

    assert(BX_WIDTH <= 18)
    report "invalid value for generic BX_WIDTH: " & integer'image(BX_WIDTH) & " (expected value <= 18)"
    severity ERROR;

    assert(BY_WIDTH <= MAXIMUM_BY_WIDTH)
    report "invalid value for generic BY_WIDTH: " & integer'image(BY_WIDTH) & " (expected value <= " & integer'image(MAXIMUM_BY_WIDTH) & ")"
    severity ERROR;

    assert(RESULT_WIDTH <= 44)
    report "invalid value for generic RESULT_WIDTH: " & integer'image(RESULT_WIDTH) & " (expected value <= 44)"
    severity ERROR;

    assert(PIPELINE <= 3)
    report "invalid value for generic PIPELINE: " & integer'image(PIPELINE) & " (expected value <= 3)"
    severity ERROR;

end entity;

library tennm;
use tennm.tennm_components.tennm_mac;

architecture structural of dsp_systolic is

    signal full_ena : std_logic_vector(2 downto 0);

begin

    full_ena <= (others => ena);

    DSP : component tennm_mac
    generic map(
        LPM_TYPE                        => "tennm_mac",
        OPERATION_MODE                  => "m18x18_systolic",
        -- Input A
        SIGNED_MAX                      => ite(USE_SIGNED, "true", "false"),
        AX_WIDTH                        => AX_WIDTH,
        AX_CLKEN                        => ite(PIPELINE >= 1, "0", "no_reg"),
        SIGNED_MAY                      => ite(USE_SIGNED, "true", "false"),
        AY_SCAN_IN_WIDTH                => AY_WIDTH,
        AY_SCAN_IN_CLKEN                => ite(PIPELINE >= 1, "0", "no_reg"),
        -- Input B
        SIGNED_MBX                      => ite(USE_SIGNED, "true", "false"),
        BX_WIDTH                        => BX_WIDTH,
        BX_CLKEN                        => ite(PIPELINE >= 1, "0", "no_reg"),
        SIGNED_MBY                      => ite(USE_SIGNED, "true", "false"),
        BY_WIDTH                        => BY_WIDTH,
        BY_CLKEN                        => ite(PIPELINE >= 1, "0", "no_reg"),
        -- Chainin
        USE_CHAINADDER                  => ite(ENABLE_CHAININ, "true", "false"),
        CHAIN_INOUT_WIDTH               => 44,
        -- Output
        RESULT_A_WIDTH                  => RESULT_WIDTH,
        RESULT_B_WIDTH                  => 0,
        OUTPUT_CLKEN                    => "0",
        -- Pipelining
        -- Counterintuitively, if you're only enabling one of these two
        -- registers, it has to be SECOND_PIPELINE_CLKEN.
        -- Otherwise, Quartus produces the error
        -- "Input pipeline register for DSP block WYSIWYG primitive [...] can only
        -- be enabled when (1) Pre-adder and/or Internal coefficient feature and
        -- input register and output register are used, OR (2) Input register and
        -- second pipeline register are used, OR (3) Input register, second
        -- pipeline register and output register are used."
        INPUT_PIPELINE_CLKEN            => ite(PIPELINE >= 3, "0", "no_reg"),
        SECOND_PIPELINE_CLKEN           => ite(PIPELINE >= 2, "0", "no_reg"),
        INPUT_SYSTOLIC_CLKEN            => "0",
        SUB_SYSTOLIC_REG                => "no_reg",
        NEGATE_SYSTOLIC_REG             => "no_reg",
        -- Input C (never used)
        SIGNED_MCX                      => "false",
        CX_WIDTH                        => 0,
        CX_CLKEN                        => "no_reg",
        SIGNED_MCY                      => "false",
        CY_WIDTH                        => 0,
        CY_CLKEN                        => "no_reg",
        -- Input D (never used)
        SIGNED_MDX                      => "false",
        DX_WIDTH                        => 0,
        DX_CLKEN                        => "no_reg",
        SIGNED_MDY                      => "false",
        DY_WIDTH                        => 0,
        DY_CLKEN                        => "no_reg",
        -- Input E (never used)
        SIGNED_MEX                      => "false",
        EX_WIDTH                        => 0,
        EX_CLKEN                        => "no_reg",
        SIGNED_MEY                      => "false",
        EY_WIDTH                        => 0,
        EY_CLKEN                        => "no_reg",
        -- Input F (never used)
        SIGNED_MFX                      => "false",
        FX_WIDTH                        => 0,
        FX_CLKEN                        => "no_reg",
        SIGNED_MFY                      => "false",
        FY_WIDTH                        => 0,
        FY_CLKEN                        => "no_reg",
        -- Subtraction (never used),
        SUB_CLKEN                       => "no_reg",
        -- Input cascade (never used)
        DELAY_SCAN_OUT_AY               => "false",
        DELAY_SCAN_OUT_BY               => "false",
        SCAN_OUT_WIDTH                  => 0,
        -- Pre-adder (never used)
        OPERAND_SOURCE_MAY              => "input",
        OPERAND_SOURCE_MBY              => "input",
        PREADDER_SUBTRACT_A             => "false",
        PREADDER_SUBTRACT_B             => "false",
        AZ_WIDTH                        => 0,
        AZ_CLKEN                        => "no_reg",
        BZ_WIDTH                        => 0,
        BZ_CLKEN                        => "no_reg",
        -- Internal coefficients (never used)
        OPERAND_SOURCE_MAX              => "input",
        OPERAND_SOURCE_MBX              => "input",
        COEF_SEL_A_CLKEN                => "no_reg",
        COEF_SEL_B_CLKEN                => "no_reg",
        COEF_A_0                        => 0,
        COEF_A_1                        => 0,
        COEF_A_2                        => 0,
        COEF_A_3                        => 0,
        COEF_A_4                        => 0,
        COEF_A_5                        => 0,
        COEF_A_6                        => 0,
        COEF_A_7                        => 0,
        COEF_B_0                        => 0,
        COEF_B_1                        => 0,
        COEF_B_2                        => 0,
        COEF_B_3                        => 0,
        COEF_B_4                        => 0,
        COEF_B_5                        => 0,
        COEF_B_6                        => 0,
        COEF_B_7                        => 0,
        -- Accumulator (never used)
        ACCUMULATE_CLKEN                => "no_reg",
        ACCUM_PIPELINE_CLKEN            => "no_reg",
        ACCUM_2ND_PIPELINE_CLKEN        => "no_reg",
        ENABLE_DOUBLE_ACCUM             => "false",
        NEGATE_CLKEN                    => "no_reg",
        LOAD_CONST_CLKEN                => "no_reg",
        LOAD_CONST_VALUE                => 0,
        LOAD_CONST_PIPELINE_CLKEN       => "no_reg",
        LOAD_CONST_2ND_PIPELINE_CLKEN   => "no_reg",
        -- Clear signal (never used)
        CLEAR_TYPE                      => "none"
    )
    port map(
        clk                 => clk,
        ena                 => full_ena,
        ax                  => ax,
        ay                  => ay,
        bx                  => bx,
        by                  => by,
        resulta             => result,
        chainin             => chainin,
        chainout            => chainout,
        -- Other parameters (never used)
        accumulate          => open,
        -- Quartus produces the following error when clr is left open:
        -- Error(15360): DSP block WYSIWYG primitive [...] has unconnected port
        --               CLR[1] -- port must be connected because corresponding
        --               register is used
        clr                 => (others => '0'),
        az                  => open,
        bz                  => open,
        cx                  => open,
        cy                  => open,
        dx                  => open,
        dy                  => open,
        ex                  => open,
        ey                  => open,
        fx                  => open,
        fy                  => open,
        coefsela            => open,
        coefselb            => open,
        loadconst           => open,
        negate              => open,
        scanin              => open,
        sub                 => open,
        dfxlfsrena          => open,
        dfxmisrena          => open,
        disable_chainout    => open,
        disable_scanin      => open,
        resultb             => open,
        scanout             => open
    );

end architecture;
