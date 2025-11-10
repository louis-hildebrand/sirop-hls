library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity orderedstreamtovector_2 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_OrderedStreamTypeIntTypeArithType32ArithType2073600;
        p0_in_last: in type_LastVectorTypeArithType1;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType1;
        p1_out_data: out type_VectorTypeIntTypeArithType32ArithType2073600;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end orderedstreamtovector_2;

architecture behavioral of orderedstreamtovector_2 is
    
    
    
    signal in_ready: std_logic_vector(p0_out_ready'range) := (others => '0');
    signal reg: type_VectorTypeIntTypeArithType32ArithType2073600; -- := (others => (others => '0')); -- not generic
    type state_type is (receiving, sending);
    signal state: state_type := receiving;
    
    
begin
    
    p0_out_ready <= in_ready;
    ready_signal: process(state, in_ready, p0_in_last, p1_in_ready)
    begin
        if state = sending and not p1_in_ready = "1"then
            in_ready <= (others => '0');
        else
            -- always ready!
            in_ready(0) <= '1';
            for i in in_ready'low + 1 to in_ready'high loop
                if p0_in_last(i - 1) = '1' and in_ready(i - 1) = '1' then
                    in_ready(i) <= '1';
                else
                    in_ready(i) <= '0';
                end if;
            end loop;
        end if;
    end process;
    
    p1_out_data <= reg;
    p1_out_valid <= '1' when state = sending else '0';
    
    shift_reg: process(clk)
    begin
        if rising_edge(clk) then
            if state = receiving or (state = sending and p1_in_ready = "1") then
                if p0_in_valid = '1' then
                    for i in reg'low to reg'high - 1 loop
                        reg(i) <= reg(i + 1);
                    end loop;
                    reg(reg'high) <= p0_in_data;
                end if;
            end if;
        end if;
    end process;
    
    next_state_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                state <= receiving;
            else
                case state is
                    when receiving =>
                    if p0_in_valid = '1' and p0_in_last = "1" then
                        state <= sending;
                    end if;
                    when sending =>
                    if p1_in_ready = "1" then
                        state <= receiving;
                    end if;
                end case;
            end if;
        end if;
    end process;
    
end behavioral;
