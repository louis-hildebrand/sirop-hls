library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity counterinteger_0 is
    generic(
        start: type_NaturalNumberType := 0;
        increment: type_NaturalNumberType := 1;
        dimensions: type_VectorTypeNaturalNumberTypeArithType1 := (0 => 200);
        repetitions: type_VectorTypeLogicTypeArithType1 := "0";
        loop_all: type_LogicType := '0'
        );
        port(
            clk: in type_LogicType;
            reset: in type_LogicType;
            p0_out_data: out type_OrderedStreamTypeIntTypeArithType8ArithType200;
            p0_out_last: out type_LastVectorTypeArithType1;
            p0_out_valid: out type_LogicType;
            p0_in_ready: in type_ReadyVectorTypeArithType1
        );
    end counterinteger_0;
    
    architecture behavioral of counterinteger_0 is
        
        
        
        -- function to precompute constants (during compilation)
        function precomp_increments return type_VectorTypeNaturalNumberTypeArithType1 is
            variable increment_per_dimension: type_VectorTypeNaturalNumberTypeArithType1 := (others => increment);
            
            
        begin
            
            if repetitions(0) = '1' then
                increment_per_dimension(0) := increment;
            else
                increment_per_dimension(0) := dimensions(0) * increment;
            end if;
            
            -- multiplication accumulate
            for i in dimensions'low + 1 to dimensions'high loop
                if repetitions(i) = '1' then
                    increment_per_dimension(i) := increment_per_dimension(i - 1);
                else
                    increment_per_dimension(i) := increment_per_dimension(i - 1) * dimensions(i);
                end if;
            end loop;
            return increment_per_dimension;
        end function;
        
        signal counter_value: natural := 0;
        signal counter_dimensions: type_VectorTypeNaturalNumberTypeArithType1 := (others => 0);
        constant increment_per_dimension: type_VectorTypeNaturalNumberTypeArithType1 := precomp_increments;
        
        signal out_last: std_logic_vector(p0_out_last'range) := (others => '0');
        signal out_valid: std_logic := '1';
        
    begin
        
        p0_out_data <= std_logic_vector(to_unsigned(counter_value + start, p0_out_data'length));
        p0_out_last <= out_last;
        p0_out_valid <= out_valid;
        
        last_signals: process(counter_dimensions)
        begin
            out_last <= (others => '0');
            for i in dimensions'low to dimensions'high loop
                if counter_dimensions(i) = dimensions(i) - 1 then
                    out_last(i) <= '1';
                end if;
            end loop;
        end process;
        
        counter_dimensions_logic: process(clk)
        begin
            if rising_edge(clk) then
                if reset = '1' then
                    counter_dimensions <= (others => 0);
                else
                    if out_valid = '1' then
                        for i in dimensions'low to dimensions'high loop
                            if p0_in_ready(i) = '1' then
                                if counter_dimensions(i) < dimensions(i) - 1 then
                                    counter_dimensions(i) <= counter_dimensions(i) + 1;
                                else
                                    counter_dimensions(i) <= 0;
                                end if;
                            end if;
                        end loop;
                    end if;
                end if;
            end if;
        end process;
        
        counter_value_logic: process(clk)
        begin
            if rising_edge(clk) then
                if reset = '1' then
                    counter_value <= 0;
                    out_valid <= '1';
                else
                    if out_valid = '1' and p0_in_ready(p0_in_ready'low) = '1' then
                        -- increase value
                        if repetitions(repetitions'low) = '0' or out_last(out_last'low) = '1' then
                            if counter_value <= increment_per_dimension(dimensions'high) - increment - 1 then
                                counter_value <= counter_value + increment;
                            else
                                counter_value <= 0;
                                if p0_in_ready(p0_in_ready'high) = '1' then
                                    -- finished counting, do not repeat, if loop_all is not set
                                        out_valid <= loop_all;
                                        end if;
                                    end if;
                                end if;
                                
                                -- repeat
                                for i in dimensions'low to dimensions'high - 1 loop
                                    if out_last(i) = '1' and p0_in_ready(i) = '1' and (p0_in_ready(i + 1) = '0' or (repetitions(i + 1) = '1' and out_last(i + 1) = '0')) then
                                        assert(counter_value + increment - increment_per_dimension(i) >= 0) report "error in incoming ready signals";
                                        counter_value <= counter_value + increment - increment_per_dimension(i);
                                        exit;
                                    end if;
                                end loop;
                            end if;
                        end if;
                    end if;
                end process;
                
            end behavioral;
