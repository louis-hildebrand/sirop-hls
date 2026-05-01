library IEEE;
use IEEE.std_logic_1164.all;

entity start_delay is
generic (
    MAX_LATENCY : integer);
port (
    clk   : in std_logic;
    reset : in std_logic;
    go    : out std_logic_vector(0 to MAX_LATENCY)
);
end entity start_delay;

architecture arch of start_delay is

    signal buf : std_logic_vector(1 to MAX_LATENCY) := (others => '0');

begin

    buf_shift : process
    begin
        wait until rising_edge(clk);
        if (reset = '1') then
            buf <= (others => '0');
        else
            for i in 1 to MAX_LATENCY loop
                if i = 1 then
                    buf(i) <= '1';
                else
                    buf(i) <= buf(i - 1);
                end if;
            end loop;
        end if;
    end process;

    gen_go : process (buf)
    begin
        go(0) <= '1';
        for i in 1 to MAX_LATENCY loop
            go(i) <= buf(i);
        end loop;
    end process;

end arch;
