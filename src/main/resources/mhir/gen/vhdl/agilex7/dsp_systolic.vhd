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
