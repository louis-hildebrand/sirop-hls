library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;
use std.textio.all;
use work.common.all;

entity testbench is
    -- empty
end testbench;

architecture tb of testbench is

    constant CLK_CYCLE : time := 20 ns;
    signal   test_done : boolean := false;
    signal   clk       : std_logic := '1';
    signal   reset     : std_logic := '0';
    signal   t         : integer := -1;

    constant HEIGHT : integer := 8;
    constant WIDTH  : integer := 1920;

    -- Easier debugging
    type rgb is record
        red   : unsigned(31 downto 0);
        green : unsigned(31 downto 0);
        blue  : unsigned(31 downto 0);
    end record;
    signal data_t : rgb;

    -- Input stream: I0
    signal I0_data  : std_logic_vector(31 downto 0);
    signal I0_last  : std_logic_vector(1 downto 0);
    signal I0_valid : std_logic;
    signal I0_ready : std_logic_vector(2 downto 0);

    -- Expected outputs
    signal data  : type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32TextTypet2IntTypeArithType32_t0;
    signal last  : std_logic_vector(1 downto 0);
    signal valid : std_logic;
    signal ready : std_logic_vector(2 downto 0);

    ----------------------------------------------------------------------------
    -- Test case 0
    ----------------------------------------------------------------------------

    signal test_0_start : boolean := false;
    signal test_0_I0_inputs_done : boolean := false;
    signal test_0_outputs_done : boolean := false;

begin

    DUT : entity work.top port map(
        clk => clk, reset => reset,
        p0_in_data => I0_data, p0_in_last => I0_last, p0_in_valid => I0_valid, p0_out_ready => I0_ready,
        p1_out_data => data, p1_out_last => last, p1_out_valid => valid, p1_in_ready => ready
    );

    data_t <= (
        red   => unsigned(data.t0),
        green => unsigned(data.t1),
        blue  => unsigned(data.t2)
    );

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
        variable input: unsigned(31 downto 0);
    begin
        I0_valid <= 'Z';
        I0_data <= (others => 'Z');
        wait until test_0_start;

        for i in 0 to HEIGHT-1 loop
            for j in 0 to WIDTH-1 loop
                -- Prepare input well before the next rising edge
                wait until falling_edge(clk);
                I0_valid <= '1';
                if j = WIDTH-1 then
                    I0_last <= "01";
                else
                    I0_last <= "00";
                end if;
                input := to_unsigned((WIDTH*i + j + 1) * (WIDTH*i + j + 1), 32);
                I0_data <= std_logic_vector(input);
                wait until rising_edge(clk) and (test_0_outputs_done or I0_ready /= "000"); -- must wait for the design to accept the input
            end loop;
        end loop;

        test_0_I0_inputs_done <= true;
        report "Finished giving inputs for test case 0, parameter I0." severity note;
        wait;
    end process;

    -- Check outputs
    out_check_0 : process
        file f : text open read_mode is "./test/outputs.txt";
        variable ok : boolean;
        variable current_line : line;
        variable bv : bit_vector(3*32-1 downto 0);
        variable expected : std_logic_vector(3*32-1 downto 0);
        variable expected_rgb : rgb;
        variable actual_rgb : rgb;
    begin
        ready <= "ZZZ";
        wait until test_0_start;

        for i in 1 to (WIDTH-4)*(HEIGHT-4) loop
            -- Wait until falling edge to give output time to settle down (probably not necessary, but just in case)
            wait until falling_edge(clk) and valid = '1';

            readline(f, current_line);
            read(current_line, bv, ok);
            assert(ok) report "Failed to read file (line " & integer'image(i) & ")." severity failure;
            expected := to_stdlogicvector(bv);
            expected_rgb := (
                red   => unsigned(expected(3*32-1 downto 2*32)),
                green => unsigned(expected(2*32-1 downto 1*32)),
                blue  => unsigned(expected(1*32-1 downto 0*32))
            );

            actual_rgb := (
                red => unsigned(data.t0),
                green => unsigned(data.t1),
                blue => unsigned(data.t2)
            );

            ready <= "111";
            assert(
                actual_rgb.red = expected_rgb.red
                and actual_rgb.green = expected_rgb.green
                and actual_rgb.blue = expected_rgb.blue
            ) report (
                "Wrong data at t = " & integer'image(t) & " (i=" & integer'image(i) & "): "
                & "expected (" & integer'image(to_integer(expected_rgb.red)) & "," & integer'image(to_integer(expected_rgb.green)) & "," & integer'image(to_integer(expected_rgb.blue)) & "), "
                & "but got (" & integer'image(to_integer(actual_rgb.red)) & "," & integer'image(to_integer(actual_rgb.green)) & "," & integer'image(to_integer(actual_rgb.blue)) & ")."
            ) severity error;
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
