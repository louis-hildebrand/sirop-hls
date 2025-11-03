library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity mulint_61 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_NamedTupleTypeTextTypet0IntTypeArithType16TextTypet1IntTypeArithType16_t0;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_IntTypeArithType32;
        p1_out_last: out type_LastVectorTypeArithType0;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType0
    );
end mulint_61;

architecture behavioral of mulint_61 is
    
    
    
    signal mul_in1_reg1: std_logic_vector(p0_in_data.t0'range) := (others => '0');
    signal mul_in2_reg1: std_logic_vector(p0_in_data.t1'range) := (others => '0');
    signal mul_in1_reg2: std_logic_vector(p0_in_data.t0'range) := (others => '0');
    signal mul_in2_reg2: std_logic_vector(p0_in_data.t1'range) := (others => '0');
    signal mul_in_valid1: std_logic := '0';
    signal mul_in_valid2: std_logic := '0';
    signal mul_out_reg1: std_logic_vector(p1_out_data'range) := (others => '0');
    signal mul_out_reg2: std_logic_vector(p1_out_data'range) := (others => '0');
    signal mul_out_valid1: std_logic := '0';
    signal mul_out_valid2: std_logic := '0';
    
    signal stall_regs: std_logic := '0';
    
    signal in_ready: std_logic_vector(p0_out_ready'range) := (others => '0');
    
    
begin
    
    in_ready <= "1" when stall_regs = '0' else "0";
    p0_out_ready <= in_ready;
    
    p1_out_data <= mul_out_reg2;
    p1_out_last <= (others => '0');
    p1_out_valid <= mul_out_valid2;
    
    stall_regs <= '1' when mul_out_valid2 = '1' and p1_in_ready = "0" else '0';
    
    mul_logic: process(clk)
    begin
        -- XXX:
        -- Here we omit the DSP reset since resetting the handshaking signal is sufficient.
        -- IF they were used, they MUST be asynchronous!
        if rising_edge(clk) then
            if stall_regs = '0' then
                mul_in1_reg1 <= p0_in_data.t0;
                mul_in2_reg1 <= p0_in_data.t1;
                mul_in1_reg2 <= mul_in1_reg1;
                mul_in2_reg2 <= mul_in2_reg1;
                mul_out_reg1 <= std_logic_vector(unsigned(mul_in1_reg2) * unsigned(mul_in2_reg2));
                mul_out_reg2 <= mul_out_reg1;
            end if;
        end if;
    end process;
    
    pipeline_logic: process(clk)
    begin
        if rising_edge(clk) then
            if reset = '1' then
                mul_in_valid1 <= '0';
                mul_in_valid2 <= '0';
                mul_out_valid1 <= '0';
                mul_out_valid2 <= '0';
            else
                if stall_regs = '0' then
                    mul_in_valid1 <= p0_in_valid;
                    mul_in_valid2 <= mul_in_valid1;
                    mul_out_valid1 <= mul_in_valid2;
                    mul_out_valid2 <= mul_out_valid1;
                end if;
            end if;
        end if;
    end process;
    
end behavioral;
