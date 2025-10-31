library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity counterinteger_1 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_out_data: out type_OrderedStreamTypeIntTypeArithType16ArithType840;
        p0_out_last: out type_LastVectorTypeArithType1;
        p0_out_valid: out type_LogicType;
        p0_in_ready: in type_ReadyVectorTypeArithType1
    );
end counterinteger_1;

architecture behavioral of counterinteger_1 is
    signal lfsr : std_logic_vector(15 downto 0) := (others => '1');
begin
    process
    begin
        wait until rising_edge(clk);
        lfsr(14 downto 0) <= lfsr(15 downto 1);
        lfsr(15) <= lfsr(10) xor lfsr(12) xor lfsr(13) xor lfsr(15);
    end process;

    p0_out_data <= lfsr;
    p0_out_last <= (others => '0');
    p0_out_valid <= '1';
end behavioral;
