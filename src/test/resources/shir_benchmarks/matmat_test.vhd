library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity testbench is
    -- empty
end testbench;

architecture tb of testbench is

    constant CLK_CYCLE   : time := 20 ns;
    signal   test_done   : boolean := false;
    signal   clk         : std_logic := '1';
    signal   reset       : std_logic := '0';
    signal   t           : integer := -1;

    constant N : integer := 256; -- Size of matrices (they're all square)
    constant P : integer := 16;  -- Vectorization factor
    constant W : integer := 16;  -- Input int bit width

    -- Easier debugging
    signal   data_t     : unsigned(19 downto 0);

    -- Input stream: I0
    signal I0_data  : std_logic_vector(P*W-1 downto 0);
    signal I0_last  : std_logic_vector(1 downto 0);
    signal I0_valid : std_logic;
    signal I0_ready : std_logic_vector(2 downto 0);

    -- Input stream: I1
    signal I1_data  : std_logic_vector(P*W-1 downto 0);
    signal I1_last  : std_logic_vector(1 downto 0);
    signal I1_valid : std_logic;
    signal I1_ready : std_logic_vector(2 downto 0);

    -- Expected outputs
    signal data  : std_logic_vector(19 downto 0);
    signal last  : std_logic_vector(1 downto 0);
    signal valid : std_logic;
    signal ready : std_logic_vector(2 downto 0);

    ----------------------------------------------------------------------------
    -- Test case 0
    ----------------------------------------------------------------------------

    signal test_0_start : boolean := false;
    signal test_0_I0_inputs_done : boolean := false;
    signal test_0_I1_inputs_done : boolean := false;
    signal test_0_outputs_done : boolean := false;

    pure function get_I0_data(i: in integer; j: in integer) return integer is
    begin
        return ((4*i+j) * (4*i+j)) mod 6;
    end function;

    pure function get_I1_data(i: in integer; j: in integer) return integer is
    begin
        return (4*i + j) mod 6;
    end function;

    pure function get_out_data(i: in integer; j: in integer) return integer is
        variable k: integer;
        variable sum: integer := 0;
    begin
        for k in 0 to N-1 loop
            sum := sum + get_I0_data(i, k) * get_I1_data(k, j);
        end loop;
        return sum;
    end function;

begin

    DUT : entity work.top port map(
        clk => clk, reset => reset,
        p0_in_data => I0_data, p0_in_last => I0_last, p0_in_valid => I0_valid, p0_out_ready => I0_ready,
        p2_in_data => I1_data, p2_in_last => I1_last, p2_in_valid => I1_valid, p2_out_ready => I1_ready,
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
        variable i : integer;
        variable j : integer;
        variable k : integer;
        variable msb : integer;
        variable lsb : integer;
    begin
        I0_valid <= 'Z';
        I0_data <= (others => 'Z');
        wait until test_0_start;

        for i in 0 to N-1 loop
            for j in 0 to N/P-1 loop
                wait until falling_edge(clk); -- prepare input well before the next rising edge
                I0_valid <= '1';
                -- last
                if i = N-1 then
                    I0_last(1) <= '1';
                else
                    I0_last(1) <= '0';
                end if;
                if j = N/P-1 then
                    I0_last(0) <= '1';
                else
                    I0_last(0) <= '0';
                end if;
                -- data
                k := 0;
                msb := P*W - 1;
                lsb := msb - W + 1;
                while lsb >= 0 loop
                    I0_data(msb downto lsb) <= std_logic_vector(to_unsigned(get_I0_data(i, P*j+k), W));
                    k := k + 1;
                    msb := msb - W;
                    lsb := lsb - W;
                end loop;
                wait until rising_edge(clk) and (test_0_outputs_done or I0_ready /= "000"); -- must wait for the design to accept the input
            end loop;
        end loop;

        test_0_I0_inputs_done <= true;
        report "Finished giving inputs for test case 0, parameter I0." severity note;
        wait;
    end process;

    -- Generate inputs (I1)
    -- Recall that this input must be transposed.
    process
        variable i : integer;
        variable j : integer;
        variable k : integer;
        variable msb : integer;
        variable lsb : integer;
    begin
        I1_valid <= 'Z';
        I1_data <= (others => 'Z');
        wait until test_0_start;

        for j in 0 to N-1 loop
            for i in 0 to N/P-1 loop
                wait until falling_edge(clk); -- prepare input well before the next rising edge
                I1_valid <= '1';
                -- last
                if i = N/P-1 then
                    I1_last(1) <= '1';
                else
                    I1_last(1) <= '0';
                end if;
                if j = N-1 then
                    I1_last(0) <= '1';
                else
                    I1_last(0) <= '0';
                end if;
                -- data
                k := 0;
                msb := P*W - 1;
                lsb := msb - W + 1;
                while lsb >= 0 loop
                    I1_data(msb downto lsb) <= std_logic_vector(to_unsigned(get_I1_data(P*i+k, j), W));
                    k := k + 1;
                    msb := msb - W;
                    lsb := lsb - W;
                end loop;
                wait until rising_edge(clk) and (test_0_outputs_done or I1_ready /= "000"); -- must wait for the design to accept the input
            end loop;
        end loop;

        test_0_I1_inputs_done <= true;
        report "Finished giving inputs for test case 0, parameter I1." severity note;
        wait;
    end process;

    -- Check outputs
    out_check_0 : process
        variable expected : unsigned(19 downto 0);
        variable i : integer;
        variable j : integer;
    begin
        ready <= "ZZZ";
        wait until test_0_start;

        for i in 0 to N-1 loop
            for j in 0 to N-1 loop
                -- Wait until falling edge to give output time to settle down (probably not necessary, but just in case)
                wait until falling_edge(clk) and valid = '1';

                expected := to_unsigned(get_out_data(i, j), 20);

                ready <= "111";
                assert(data = std_logic_vector(expected)) report "Wrong data at t = " & integer'image(t) & ".";
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
