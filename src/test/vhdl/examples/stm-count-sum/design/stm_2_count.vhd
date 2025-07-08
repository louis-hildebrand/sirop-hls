-- StmBuild( 20; t(a_4, true); a_4: (0, 1 + a_4) )
library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity stm_2_count is
port (
    clk : in std_logic;
    data : out std_logic_vector(31 downto 0);
    data_ready : in std_logic;
    data_valid : out std_logic
);
end;

architecture arch of stm_2_count is
    signal a_4 : integer := 0;
    signal all_required_producers_valid : boolean;
    signal can_update_acc : boolean;
    signal data_internal : integer;
    signal data_valid_internal : boolean;
    signal num_outputs : integer := 0;
    signal transfer_ok : boolean;
begin
    all_required_producers_valid <= true;
    can_update_acc <= transfer_ok or (not data_valid_internal and all_required_producers_valid);
    data <= (others => '0') when not transfer_ok else std_logic_vector(to_signed((data_internal), data'length));
    data_internal <= a_4;
    data_valid <= '1' when data_valid_internal else '0';
    data_valid_internal <= (num_outputs < 20) and (true) and all_required_producers_valid;
    transfer_ok <= data_ready = '1' and data_valid_internal;

    process
    begin
        wait until rising_edge(clk) and can_update_acc;
        if (can_update_acc) then a_4 <= (1) + (a_4); end if;
        if (transfer_ok) then num_outputs <= num_outputs + 1; end if;
    end process ;
end;
