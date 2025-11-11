library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;
use ieee.math_real.all;
use work.common.all;

entity vectortoorderedstream_144 is
    port(
        clk: in type_LogicType;
        reset: in type_LogicType;
        p0_in_data: in type_VectorTypeSignedIntTypeArithType32ArithType1920;
        p0_in_last: in type_LastVectorTypeArithType0;
        p0_in_valid: in type_LogicType;
        p0_out_ready: out type_ReadyVectorTypeArithType0;
        p1_out_data: out type_OrderedStreamTypeSignedIntTypeArithType32ArithType1920;
        p1_out_last: out type_LastVectorTypeArithType1;
        p1_out_valid: out type_LogicType;
        p1_in_ready: in type_ReadyVectorTypeArithType1
    );
end vectortoorderedstream_144;

architecture behavioral of vectortoorderedstream_144 is
    
    
    
    signal cnt: natural range 0 to p0_in_data'high := 0;
    
    
begin
    
    p1_out_data <= p0_in_data(cnt);
    p1_out_last <= "1" when cnt = p0_in_data'high else "0";
    p1_out_valid <= p0_in_valid;
    
    process(cnt, p1_in_ready)
    begin
        p0_out_ready <= (others => '0');
        if cnt = p0_in_data'high and p1_in_ready(p1_in_ready'high-1) = '1' then
            p0_out_ready <= (others => '1');
            p0_out_ready(p0_out_ready'high) <= p1_in_ready(p1_in_ready'high);
        end if;
    end process;
    
    send_counter: process(clk)
begin
    if rising_edge(clk) then
        if reset = '1' then
            cnt <= 0;
        else
            if p0_in_valid = '1' and p1_in_ready(p1_in_ready'high-1) = '1' then
                if cnt < p0_in_data'high then
                    cnt <= cnt + 1;
                else
                    cnt <= 0;
                end if;
            end if;
        end if;
    end if;
end process;

end behavioral;
