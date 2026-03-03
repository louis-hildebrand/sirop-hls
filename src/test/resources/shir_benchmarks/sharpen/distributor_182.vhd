library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity distributor_182 is
    generic(
        num_clients: type_NaturalNumberType := 2
    );
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32_t0;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_VectorTypeNamedTupleTypeTextTypet0IntTypeArithType32TextTypet1IntTypeArithType32ArithType2;
        p2_out_data: out type_VectorTypeLastVectorTypeArithType0ArithType2;
        p3_out_data: out type_VectorTypeLogicTypeArithType2;
        p4_in_data: in type_VectorTypeReadyVectorTypeArithType0ArithType2
    );
end distributor_182;

architecture behavioral of distributor_182 is
    
    
    
    signal transmitted: std_logic_vector(num_clients-1 downto 0) := (others => '0');
    
    
begin
    
    p1_out_data <= (others => p0_in_data);
    p2_out_data <= (others => p0_in_last);
    
    valid_signal: process(p0_in_valid, transmitted)
    begin
        for i in transmitted'range loop
            p3_out_data(i) <= p0_in_valid and not transmitted(i);
        end loop;
    end process;
    
    ready_signal: process(p4_in_data, transmitted)
        variable ready: std_logic_vector(p0_out_ready'range) := (others => '1');
        constant transmitted_all_one: std_logic_vector(transmitted'range) := (others => '1');
    begin
        --ready := p4_in_data(0);
        if transmitted = transmitted_all_one then
            ready := (others => '0');
        else
            ready := (others => '1');
        end if;
        
        for i in 0 to num_clients - 1 loop
            if transmitted(i) = '0' then
                ready := ready and p4_in_data(i);
            end if;
        end loop;
        p0_out_ready <= ready;
    end process;
    
    transmitted_state: process(clk)
        constant transmitted_all_one: std_logic_vector(transmitted'range) := (others => '1');
        variable transmitted_v: std_logic_vector(transmitted'range) := (others => '0');
    begin
        if rising_edge(clk) then
            if reset = '1' then
                transmitted <= (others => '0');
            else
                transmitted_v := transmitted;
                if p0_in_valid = '1' then
                    for i in transmitted'range loop
                        if p4_in_data(i)(0) = '1' then
                            -- mark current client, because it has received the data now
                            transmitted_v(i) := '1';
                        end if;
                    end loop;
                end if;
                
                -- if all clients received the data, transmittion is complete. Reset the state!
                if transmitted_v = transmitted_all_one then
                    transmitted_v := (others => '0');
                end if;
                
                transmitted <= transmitted_v;
            end if;
        end if;
    end process;
    
end behavioral;
