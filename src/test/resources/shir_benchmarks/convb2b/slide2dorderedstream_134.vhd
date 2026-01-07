library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity slide2dorderedstream_134 is
    generic(
        window_height: type_NaturalNumberType := 2;
        window_width: type_NaturalNumberType := 2;
        row_width: type_NaturalNumberType := 1918
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078;
        p0_in_last: in type_LastVectorTypeArithType2;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType2;
        p1_out_data: out type_OrderedStreamTypeOrderedStreamTypeVectorTypeVectorTypeIntTypeArithType32ArithType2ArithType2ArithType1917ArithType1077;
        p1_out_last: out type_LastVectorTypeArithType2;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType2
    );
end slide2dorderedstream_134;

architecture behavioral of slide2dorderedstream_134 is
    
    
    
    constant SHIFT_REG_SIZE: natural := (window_height-1)*row_width + window_width - 1;
    type shift_reg_type is array(0 to SHIFT_REG_SIZE-1) of type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078;
    signal reg: shift_reg_type;
    
    constant MAX_ROW: natural := window_height - 1;
    constant MAX_COL: natural := row_width + window_width - 2;
    signal current_row: natural range 0 to MAX_ROW := 0;
    signal current_col: natural range 0 to MAX_COL := 0;
    
    signal in_ready: std_logic_vector(p0_out_ready'range) := (others => '0');
    
    
begin
    
    data_signal: process(p0_in_data, reg)
        type window_row_type is array(0 to window_width-1) of type_OrderedStreamTypeOrderedStreamTypeIntTypeArithType32ArithType1918ArithType1078;
        type window_type is array(0 to window_height-1) of window_row_type;
        variable window: window_type;
    begin
        for i in 0 to window_height-1 loop
            for j in 0 to window_width-1 loop
                if row_width*i + j < SHIFT_REG_SIZE then
                    window(i)(j) := reg(row_width*i + j);
                else
                    window(i)(j) := p0_in_data;
                end if;
            end loop;
        end loop;
        -- The output vector is indexed using "downto," whereas the window is
        -- indexed using "to."
        for i in 0 to window_height-1 loop
            for j in 0 to window_width-1 loop
                p1_out_data(i)(j) <= window(window_height-1-i)(window_width-1-j);
            end loop;
        end loop;
    end process;
    
    p1_out_last <= p0_in_last;
    
    p1_out_valid <= '1' when (
    p0_in_valid = '1'
    and current_row = MAX_ROW
    and current_col < row_width
    ) else '0';
    
    p0_out_ready <= in_ready;
    ready_signal: process(p1_in_ready, current_row)
    begin
        in_ready <= p1_in_ready;
        -- Always ready if the current output is not valid (either because the
        -- shift register is not yet full or the window is past the right-hand
        -- edge of the image)
        if current_row /= MAX_ROW or current_col >= row_width then
            in_ready <= (others => '1');
        end if;
    end process;
    
    shift_reg_logic: process
    begin
        wait until rising_edge(clk);
        if p0_in_valid = '1' and in_ready(in_ready'low) = '1' then
            for i in reg'low to reg'high - 1 loop
                reg(i) <= reg(i + 1);
            end loop;
            reg(reg'high) <= p0_in_data;
        end if;
    end process;
    
    element_counter_logic: process
    begin
        wait until rising_edge(clk);
        if reset = '1' then
            current_row <= 0;
            current_col <= 0;
        elsif p0_in_valid = '1' and in_ready(in_ready'low) = '1' then
            if current_col = MAX_COL then
                current_col <= window_width - 1;
                if current_row /= MAX_ROW then
                    current_row <= current_row + 1;
                end if;
            else
                current_col <= current_col + 1;
            end if;
        end if;
    end process;
    
end behavioral;
