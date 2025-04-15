library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity testbench is
    -- empty
end testbench;

architecture tb of testbench is

    constant CLK_CYCLE  : time := 20 ns;
    signal   test_done  : boolean := false;
    signal   clk        : std_logic := '0';
    signal   data       : std_logic_vector(31 downto 0);
    signal   data_valid : std_logic;
    signal   data_ready : std_logic := '0';
    signal   expected   : std_logic_vector(31 downto 0);

begin

    DUT: entity work.top port map(clk => clk, data => data, data_valid => data_valid, data_ready => data_ready);

    -- Generate clock signal
    process
    begin
        if (not test_done) then
            wait for CLK_CYCLE / 2;
            clk <= not clk;
        else
            wait;
        end if;
    end process;

    -- Run tests
    process
    begin
        data_ready <= '1';

        expected <= std_logic_vector(to_signed(0, expected'length));
        wait until rising_edge(clk) and data_valid = '1';
        assert(data = expected) report "Wrong `data` at cycle 0.";

        -- What happens if the consumer is not ready?
        expected <= std_logic_vector(to_signed(0, expected'length));
        data_ready <= '0';
        wait until rising_edge(clk) and data_valid = '1';
        assert(data = expected) report "Wrong `data` when data_ready = '0'.";
        data_ready <= '1';

        expected <= std_logic_vector(to_signed(1, expected'length));
        wait until rising_edge(clk) and data_valid = '1';
        assert(data = expected) report "Wrong `data` at cycle 1.";

        expected <= std_logic_vector(to_signed(2, expected'length));
        wait until rising_edge(clk) and data_valid = '1';
        assert(data = expected) report "Wrong `data` at cycle 2.";

        expected <= std_logic_vector(to_signed(3, expected'length));
        wait until rising_edge(clk) and data_valid = '1';
        assert(data = expected) report "Wrong `data` at cycle 3.";

        expected <= std_logic_vector(to_signed(4, expected'length));
        wait until rising_edge(clk) and data_valid = '1';
        assert(data = expected) report "Wrong `data` at cycle 4.";

        expected <= std_logic_vector(to_signed(5, expected'length));
        wait until rising_edge(clk) and data_valid = '1';
        assert(data = expected) report "Wrong `data` at cycle 5.";

        expected <= std_logic_vector(to_signed(6, expected'length));
        wait until rising_edge(clk) and data_valid = '1';
        assert(data = expected) report "Wrong `data` at cycle 6.";

        expected <= std_logic_vector(to_signed(7, expected'length));
        wait until rising_edge(clk) and data_valid = '1';
        assert(data = expected) report "Wrong `data` at cycle 7.";

        expected <= std_logic_vector(to_signed(8, expected'length));
        wait until rising_edge(clk) and data_valid = '1';
        assert(data = expected) report "Wrong `data` at cycle 8.";

        expected <= std_logic_vector(to_signed(9, expected'length));
        wait until rising_edge(clk) and data_valid = '1';
        assert(data = expected) report "Wrong `data` at cycle 9.";

        expected <= std_logic_vector(to_signed(10, expected'length));
        wait until rising_edge(clk) and data_valid = '1';
        assert(data = expected) report "Wrong `data` at cycle 10.";

        expected <= std_logic_vector(to_signed(11, expected'length));
        wait until rising_edge(clk) and data_valid = '1';
        assert(data = expected) report "Wrong `data` at cycle 12.";

        expected <= (others => '0');
        wait until rising_edge(clk);
        assert(data = expected) report "Wrong `data` at cycle 13.";
        assert(data_valid = '0') report "Wrong `valid` at cycle 13";

        test_done <= true;
        assert false report "Test done." severity note;
        wait;
    end process;
end tb;
