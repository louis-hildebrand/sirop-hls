library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity slideorderedstream_164 is
    generic(
        window_width: type_NaturalNumberType := 5760;
        step_size: type_NaturalNumberType := 1920
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeIntTypeArithType32ArithType2073600;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_OrderedStreamTypeVectorTypeIntTypeArithType32ArithType5760ArithType1078;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end slideorderedstream_164;

architecture behavioral of slideorderedstream_164 is
    
    
    
    type shift_reg_type is array(window_width - 2 downto 0) of type_OrderedStreamTypeIntTypeArithType32ArithType2073600;
    
    signal elements: natural range 0 to window_width - 1 := 0;
    
    signal reg: shift_reg_type;
    
    signal out_valid: std_logic := '0';
    signal in_ready: std_logic_vector(p0_out_ready'range) := (others => '0');
    
    
begin
    
    data_signal: process(p0_in_data, reg)
    begin
        for i in reg'low to reg'high loop
            p1_out_data(i) <= reg(i);
        end loop;
        p1_out_data(p1_out_data'high) <= p0_in_data;
    end process;
    
    p1_out_last <= p0_in_last;
    
    out_valid <= '1' when p0_in_valid = '1' and elements = window_width - 1 else '0';
    p1_out_valid <= out_valid;
    
    p0_out_ready <= in_ready;
    ready_signal: process(p1_in_ready, elements)
    begin
        in_ready <= p1_in_ready;
        -- always ready if shift register is not full!
        if elements < window_width - 1 then
            in_ready(in_ready'low) <= '1';
        end if;
    end process;
    
    shift_reg_logic: process(clk)
    begin
        if rising_edge(clk) then
            if p0_in_valid = '1' and in_ready(in_ready'low) = '1' then
                -- shift
                for i in reg'low to reg'high - 1 loop
                    reg(i) <= reg(i + 1);
                end loop;
                reg(reg'high) <= p0_in_data;
            end if;
        end if;
    end process;
    
    element_counter_logic: process(clk)
        variable elements_v: natural range 0 to window_width := 0;
    begin
        if rising_edge(clk) then
            if reset = '1' then
                elements <= 0;
            else
                elements_v := elements;
                if p0_in_valid = '1' and in_ready(in_ready'low) = '1' then
                    elements_v := elements_v + 1;
                end if;
                if out_valid = '1' and p1_in_ready(p1_in_ready'low) = '1' then
                    if p0_in_last(p0_in_last'high) = '1' then
                        elements_v := 0;
                    else
                        elements_v := elements_v - step_size;
                    end if;
                end if;
                elements <= elements_v;
            end if;
        end if;
    end process;
    
end behavioral;
