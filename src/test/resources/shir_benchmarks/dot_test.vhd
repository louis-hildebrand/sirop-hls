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
    signal   data_t : unsigned(25 downto 0);

    -- Input stream: I0
    signal I0_data  : std_logic_vector(15 downto 0);
    signal I0_last  : std_logic_vector(0 downto 0);
    signal I0_valid : std_logic;
    signal I0_ready : std_logic_vector(1 downto 0);

    -- Input stream: I1
    signal I1_data  : std_logic_vector(15 downto 0);
    signal I1_last  : std_logic_vector(0 downto 0);
    signal I1_valid : std_logic;
    signal I1_ready : std_logic_vector(1 downto 0);

    -- Expected outputs
    signal data  : std_logic_vector(25 downto 0);
    signal last  : std_logic_vector(-1 downto 0);
    signal valid : std_logic;
    signal ready : std_logic_vector(0 downto 0);

    ----------------------------------------------------------------------------
    -- Test case 0
    ----------------------------------------------------------------------------

    signal test_0_start : boolean := false;
    signal test_0_I0_inputs_done : boolean := false;
    signal test_0_I1_inputs_done : boolean := false;
    signal test_0_outputs_done : boolean := false;

begin

    DUT : entity work.top port map(
        clk => clk, reset => reset,
        p0_in_data => I0_data, p0_in_last => I0_last, p0_in_valid => I0_valid, p0_out_ready => I0_ready,
        p1_in_data => I1_data, p1_in_last => I1_last, p1_in_valid => I1_valid, p1_out_ready => I1_ready,
        p2_out_data => data, p2_out_last => last, p2_out_valid => valid, p2_in_ready => ready
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
        variable i : integer;
    begin
        I0_valid <= 'Z';
        I0_data <= (others => 'Z');
        wait until test_0_start;

        for i in 0 to 839 loop
            wait until falling_edge(clk); -- prepare input well before the next rising edge
            I0_valid <= '1';
            if i = 839 then
                I0_last <= "1";
            else
                I0_last <= "0";
            end if;
            I0_data <= std_logic_vector(to_unsigned(i mod 16, 16));
            wait until rising_edge(clk) and (test_0_outputs_done or I0_ready /= "00"); -- must wait for the design to accept the input
        end loop;

        test_0_I0_inputs_done <= true;
        report "Finished giving inputs for test case 0, parameter I0." severity note;
        wait;
    end process;

    -- Generate inputs (I1)
    process
        variable i : integer;
    begin
        I1_valid <= 'Z';
        I1_data <= (others => 'Z');
        wait until test_0_start;

        for i in 0 to 840 loop
            wait until falling_edge(clk); -- prepare input well before the next rising edge
            I1_valid <= '1';
            if i = 839 then
                I1_last <= "1";
            else
                I1_last <= "0";
            end if;
            I1_data <= std_logic_vector(to_unsigned((15 - i - 8) mod 16, 16));
            wait until rising_edge(clk) and (test_0_outputs_done or I1_ready /= "00"); -- must wait for the design to accept the input
        end loop;

        test_0_I1_inputs_done <= true;
        report "Finished giving inputs for test case 0, parameter I1." severity note;
        wait;
    end process;

    -- Check outputs
    out_check_0 : process
        variable expected : std_logic_vector(25 downto 0);
    begin
        ready <= "Z";
        wait until test_0_start;

        -- Wait until falling edge to give output time to settle down (probably not necessary, but just in case)
        wait until falling_edge(clk) and valid = '1';
        ready <= "1";
        expected := std_logic_vector(to_unsigned(55800, 26));
        assert(data = expected) report "Wrong data at t = " & integer'image(t) & ".";

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
                wait until test_0_I0_inputs_done and test_0_I1_inputs_done and test_0_outputs_done;
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
