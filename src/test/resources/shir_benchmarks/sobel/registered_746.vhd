library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity registered_746 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end registered_746;

architecture behavioral of registered_746 is
    
    
    
    -- this implementation uses double buffering to also 'cut' the paths for valid and ready control signals
    -- therefore registered can also be used to prevent too long combinational paths, when caused by control logic signals
    
    -- write the two buffer entries separately instead of using a array:
    -- the code looks nastier, but it avoids Quartus trying to infer BRAMs
    -- without the need of a ramstyle attribute.
    signal buffer0, buffer1: type_NamedTupleTypeTextTypet0SignedIntTypeArithType32TextTypet1SignedIntTypeArithType32TextTypet2SignedIntTypeArithType32_t0;
    signal output_buffer_count: natural range 0 to 2 := 0;
    
    signal next_free_output_buffer_entry: std_logic := '0';
    signal first_used_output_buffer_entry: std_logic := '0';
    
    signal in_ready: std_logic_vector(p0_out_ready'range) := (others => '0');
    signal out_valid: std_logic := '0';
    
    
begin
    
    in_ready <= "1" when output_buffer_count <= 1 else "0";
    p0_out_ready <= in_ready;
    
    p1_out_data <= buffer0 when first_used_output_buffer_entry = '0' else buffer1;
    p1_out_last <= (others => '0');
    out_valid <= '1' when output_buffer_count > 0 else '0';
    p1_out_valid <= out_valid;
    
    -- since next_free_output_buffer_entry only swaps on p0_in_valid, the
    -- data stored will eventually be valid.
    
    buffer0_store_logic: process(clk)
    begin
        if rising_edge(clk) then
            if in_ready(in_ready'low) = '1' and next_free_output_buffer_entry = '0' then
                buffer0 <= p0_in_data;
            end if;
        end if;
    end process;
    
    buffer1_store_logic: process(clk)
    begin
        if rising_edge(clk) then
            if in_ready(in_ready'low) = '1' and next_free_output_buffer_entry = '1' then
                buffer1 <= p0_in_data;
            end if;
        end if;
    end process;
    
    output_buffer_logic: process(clk)
        variable output_buffer_count_v: natural range 0 to 3 := 0;
    begin
        if rising_edge(clk) then
            if reset = '1' then
                output_buffer_count <= 0;
                next_free_output_buffer_entry <= '0';
                first_used_output_buffer_entry <= '0';
            else
                output_buffer_count_v := output_buffer_count;
                if p0_in_valid = '1' and in_ready(in_ready'low) = '1' then
                    output_buffer_count_v := output_buffer_count_v + 1;
                    next_free_output_buffer_entry <= not next_free_output_buffer_entry;
                end if;
                if out_valid = '1' and p1_in_ready(p1_in_ready'low) = '1' then
                    output_buffer_count_v := output_buffer_count_v - 1;
                    first_used_output_buffer_entry <= not first_used_output_buffer_entry;
                end if;
                output_buffer_count <= output_buffer_count_v;
            end if;
        end if;
    end process;
    
end behavioral;
