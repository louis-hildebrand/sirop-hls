library IEEE;
use IEEE.std_logic_1164.all;

entity testbench is
    -- empty
end testbench;

architecture arch of testbench is
begin

    DUT: entity work.top port map(a_in, b_in, q_out);

    process
    begin
        assert false report "Test done." severity note;
        wait;
    end process;
end arch;
