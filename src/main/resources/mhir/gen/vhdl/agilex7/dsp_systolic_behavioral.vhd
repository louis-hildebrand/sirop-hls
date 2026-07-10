library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

architecture behavioral of dsp_systolic is

    type pipeline_ax_t is array (0 to 2) of std_logic_vector(AX_WIDTH-1 downto 0);
    signal pipeline_ax : pipeline_ax_t;
    type pipeline_ay_t is array (0 to 2) of std_logic_vector(AY_WIDTH-1 downto 0);
    signal pipeline_ay : pipeline_ay_t;
    type pipeline_bx_t is array (0 to 2) of std_logic_vector(BX_WIDTH-1 downto 0);
    signal pipeline_bx : pipeline_bx_t;
    type pipeline_by_t is array (0 to 2) of std_logic_vector(BY_WIDTH-1 downto 0);
    signal pipeline_by : pipeline_by_t;

    signal systolic_register_x       : std_logic_vector(AX_WIDTH-1 downto 0);
    signal systolic_register_y       : std_logic_vector(AY_WIDTH-1 downto 0);

    signal top_multiplier_output    : std_logic_vector(AX_WIDTH+AY_WIDTH-1 downto 0);
    signal bottom_multiplier_output : std_logic_vector(BX_WIDTH+BY_WIDTH-1 downto 0);

    signal systolic_register_chainin : std_logic_vector(43 downto 0);

    signal output_register : std_logic_vector(43 downto 0);

begin

    gen_pipeline : for i in 0 to 2 generate
        gen_pipeline_if_0 : if (PIPELINE > i) and (i = 0) generate
            -- Register driven by input
            process
            begin
                wait until rising_edge(clk) and ena = '1';
                pipeline_ax(i) <= ax;
                pipeline_ay(i) <= ay;
                pipeline_bx(i) <= bx;
                pipeline_by(i) <= by;
            end process;
        end generate;
        gen_pipeline_if_1 : if (PIPELINE > i) and (i > 0) generate
            -- Register driven by previous register
            process
            begin
                wait until rising_edge(clk) and ena = '1';
                pipeline_ax(i) <= pipeline_ax(i - 1);
                pipeline_ay(i) <= pipeline_ay(i - 1);
                pipeline_bx(i) <= pipeline_bx(i - 1);
                pipeline_by(i) <= pipeline_by(i - 1);
            end process;
        end generate;
        gen_pipeline_if_2 : if (PIPELINE <= i) and (i = 0) generate
            -- Pass through input
            pipeline_ax(i) <= ax;
            pipeline_ay(i) <= ay;
            pipeline_bx(i) <= bx;
            pipeline_by(i) <= by;
        end generate;
        gen_pipeline_if_3 : if (PIPELINE <= i) and (i > 0) generate
            -- Pass through previous stage
            pipeline_ax(i) <= pipeline_ax(i - 1);
            pipeline_ay(i) <= pipeline_ay(i - 1);
            pipeline_bx(i) <= pipeline_bx(i - 1);
            pipeline_by(i) <= pipeline_by(i - 1);
        end generate;
    end generate;

    proc_systolic_registers : process
    begin
        wait until rising_edge(clk) and ena = '1';
        systolic_register_x <= pipeline_ax(2);
        systolic_register_y <= pipeline_ay(2);
    end process;

    gen_top_multiplier_signed : if USE_SIGNED generate
        top_multiplier_output <= std_logic_vector(signed(systolic_register_x) * signed(systolic_register_y));
    end generate;
    gen_top_multiplier_unsigned : if not USE_SIGNED generate
        top_multiplier_output <= std_logic_vector(unsigned(systolic_register_x) * unsigned(systolic_register_y));
    end generate;

    gen_bottom_multiplier_signed : if USE_SIGNED generate
        bottom_multiplier_output <= std_logic_vector(signed(pipeline_bx(2)) * signed(pipeline_by(2)));
    end generate;
    gen_bottom_multiplier_unsigned : if not USE_SIGNED generate
        bottom_multiplier_output <= std_logic_vector(unsigned(pipeline_bx(2)) * unsigned(pipeline_by(2)));
    end generate;

    gen_systolic_register_chainin : if ENABLE_CHAININ generate
        process
        begin
            wait until rising_edge(clk) and ena = '1';
            systolic_register_chainin <= chainin;
        end process;
    end generate;
    gen_no_chainin : if not ENABLE_CHAININ generate
        systolic_register_chainin <= (others => '0');
    end generate;

    gen_output_register_signed : if USE_SIGNED generate
        process
        begin
            wait until rising_edge(clk) and ena = '1';
            output_register <= std_logic_vector(resize(
                                    signed(systolic_register_chainin) + (signed(top_multiplier_output) + signed(bottom_multiplier_output)),
                                    44));
        end process;
    end generate;
    gen_output_register_unsigned : if not USE_SIGNED generate
        process
        begin
            wait until rising_edge(clk) and ena = '1';
            output_register <= std_logic_vector(resize(
                                    unsigned(systolic_register_chainin) + (unsigned(top_multiplier_output) + unsigned(bottom_multiplier_output)),
                                    44));
        end process;
    end generate;

    chainout <= output_register;

    gen_result_signed : if USE_SIGNED generate
        result <= std_logic_vector(resize(signed(output_register), RESULT_WIDTH));
    end generate;
    gen_result_unsigned : if not USE_SIGNED generate
        result <= std_logic_vector(resize(unsigned(output_register), RESULT_WIDTH));
    end generate;

end architecture;
