library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity testbench is
    -- empty
end testbench;

architecture tb of testbench is

    constant CLK_CYCLE : time := 20 ns;
    signal   test_done : boolean := false;
    signal   clk       : std_logic := '1';
    signal   reset     : std_logic := '0';
    signal   t         : integer := -1;

    -- Easier debugging
    signal   data_t : unsigned(31 downto 0);

    -- Binary file I/O
    type binary_file is file of character;

    -- Input stream: I0
    signal I0_data  : std_logic_vector(31 downto 0);
    signal I0_last  : std_logic_vector(1 downto 0);
    signal I0_valid : std_logic;
    signal I0_ready : std_logic_vector(2 downto 0);

    -- Expected outputs
    signal data  : std_logic_vector(31 downto 0);
    signal last  : std_logic_vector(1 downto 0);
    signal valid : std_logic;
    signal ready : std_logic_vector(2 downto 0);

    ----------------------------------------------------------------------------
    -- Test case 0
    ----------------------------------------------------------------------------

    signal test_0_start : boolean := false;
    signal test_0_I0_inputs_done : boolean := false;
    signal test_0_outputs_done : boolean := false;

    pure function get_input_data (i : in integer; j : in integer) return unsigned is
    begin
        if ((i mod 20) < 10) = ((j mod 20) < 10) then
            return to_unsigned(255, 32);
        else
            return to_unsigned(0, 32);
        end if;
    end function;

    pure function get_output_data_1 (i_top : in integer; j_left : in integer) return unsigned is
        variable output  : unsigned(31 downto 0);
        variable i_delta : integer;
        variable j_delta : integer;
        variable i       : integer;
        variable j       : integer;
    begin
        output := to_unsigned(0, 32);
        for i_delta in 0 to 2 loop
            i := i_top + i_delta;
            for j_delta in 0 to 2 loop
                j := j_left + j_delta;
                if (i_delta = 1 and j_delta = 1) then
                    output := output + (get_input_data(i, j) sll 2);
                elsif (i_delta = 1 or j_delta = 1) then
                    output := output + (get_input_data(i, j) sll 1);
                else
                    output := output + get_input_data(i, j);
                end if;
            end loop;
        end loop;
        return output;
    end function;

    pure function get_output_data_2 (i_top : in integer; j_left : in integer) return unsigned is
        variable output  : unsigned(31 downto 0);
        variable i_delta : integer;
        variable j_delta : integer;
        variable i       : integer;
        variable j       : integer;
    begin
        output := to_unsigned(0, 32);
        for i_delta in 0 to 1 loop
            i := i_top + i_delta;
            for j_delta in 0 to 1 loop
                j := j_left + j_delta;
                if (i_delta = 0 and j_delta = 1) then
                    output := output + (get_output_data_1(i, j) sll 1);
                elsif (i_delta = 1 and j_delta = 0) then
                    output := output + (get_output_data_1(i, j) sll 2);
                else
                    output := output + get_output_data_1(i, j);
                end if;
            end loop;
        end loop;
        return output;
    end function;

begin

    DUT : entity work.top port map(
        clk => clk, reset => reset,
        p0_in_data => I0_data, p0_in_last => I0_last, p0_in_valid => I0_valid, p0_out_ready => I0_ready,
        p1_out_data => data, p1_out_last => last, p1_out_valid => valid, p1_in_ready => ready
    );

    data_t <= unsigned(data);

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

    -- Keep track of time
    process
    begin
        wait until test_0_start and rising_edge(clk);
        t <= t + 1;
    end process;

    ----------------------------------------------------------------------------
    -- Test case 0
    ----------------------------------------------------------------------------

    -- Generate inputs (I0)
    process
    begin
        I0_valid <= 'Z';
        I0_data <= (others => 'Z');
        wait until test_0_start;

        for i in 0 to 7 loop
            for j in 0 to 1919 loop
                wait until falling_edge(clk); -- prepare input well before the next rising edge
                I0_valid <= '1';
                if j = 1919 then
                    I0_last <= "01";
                else
                    I0_last <= "00";
                end if;
                I0_data <= std_logic_vector(get_input_data(i, j));
                wait until rising_edge(clk) and (test_0_outputs_done or I0_ready /= "000"); -- must wait for the design to accept the input
            end loop;
        end loop;

        test_0_I0_inputs_done <= true;
        report "Finished giving inputs for test case 0, parameter I0." severity note;
        wait;
    end process;

    -- Check outputs
    out_check_0 : process
        variable expected : std_logic_vector(31 downto 0);
    begin
        ready <= "ZZZ";
        wait until test_0_start;

        for i_top in 0 to 4 loop
            for j_left in 0 to 1916 loop
                -- Wait until falling edge to give output time to settle down (probably not necessary, but just in case)
                wait until falling_edge(clk) and valid = '1';

                expected := std_logic_vector(get_output_data_2(i_top, j_left));

                ready <= "111";
                assert(data = expected) report "Wrong data at t = " & integer'image(t) & ".";
            end loop;
        end loop;

        report "LATENCY: " & integer'image(t) & " cycles" severity note;

        -- Accept the last output before moving on to the next test case
        wait until rising_edge(clk); wait until falling_edge(clk);
        test_0_outputs_done <= true;
        report "Finished checking outputs for test case 0." severity note;
        wait;
    end process out_check_0;

    main : process
            procedure run_test_0 is
            begin
                test_0_start <= true;
                wait until test_0_I0_inputs_done and test_0_outputs_done;
                report "Test 0 done." severity note;
            end procedure run_test_0;
    begin
        wait until falling_edge(clk);
        reset <= '1';
        wait until falling_edge(clk);
        reset <= '0';

        run_test_0;

        wait until falling_edge(clk);
        test_done <= true;
        assert false report "Test done." severity note;
        wait;
    end process;
end tb;
