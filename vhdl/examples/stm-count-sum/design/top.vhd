-- StmBuild(
--     1;
--     if (i_19 < 19) then {
--         t(0, false)
--     } else {
--         t(StmNext(s_18).__1, true)
--     };
--     s_18: (
--         StmBuild(
--             20;
--             t(acc_10 + StmNext(s2_7).__1, true);
--             s2_7: (
--                 StmBuild( 20; t(a_4, true); a_4: (0, 1 + a_4) ),
--                 StmNext(s2_7).__0
--             );
--             acc_10: (0, acc_10 + StmNext(s2_7).__1)
--         ),
--         StmNext(s_18).__0
--     );
--     i_19: (0, 1 + i_19)
-- )
library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity top is
port (
    clk : in std_logic;
    data : out std_logic_vector(31 downto 0);
    data_ready : in std_logic;
    data_valid : out std_logic
);
end;

architecture arch of top is
    signal all_required_producers_valid : boolean;
    signal can_update_acc : boolean;
    signal data_internal : integer;
    signal data_valid_internal : boolean;
    signal i_19 : integer := 0;
    signal ite_82 : integer;
    signal num_outputs : integer := 0;
    signal s_18_data : integer;
    signal s_18_data_raw : std_logic_vector(31 downto 0);
    signal s_18_data_ready : boolean;
    signal s_18_data_ready_raw : std_logic;
    signal s_18_data_valid : boolean;
    signal s_18_data_valid_raw : std_logic;
    signal transfer_ok : boolean;
begin
    STM_SCAN : entity work.stm_1_scan port map(clk => clk, data_valid => s_18_data_valid_raw, data_ready => s_18_data_ready_raw, data => s_18_data_raw);

    all_required_producers_valid <= (not (true) or (s_18_data_valid));
    can_update_acc <= transfer_ok or (not data_valid_internal and all_required_producers_valid);
    data <= (others => '0') when not transfer_ok else std_logic_vector(to_signed((data_internal), data'length));
    data_internal <= ite_82;
    data_valid <= '1' when data_valid_internal else '0';
    data_valid_internal <= (num_outputs < 1) and (not ((i_19) < (19))) and all_required_producers_valid;
    ite_82 <= (0) when ((i_19) < (19)) else (s_18_data);
    s_18_data <= to_integer(signed(s_18_data_raw));
    s_18_data_ready <= (true) and can_update_acc;
    s_18_data_ready_raw <= '1' when s_18_data_ready else '0';
    s_18_data_valid <= s_18_data_valid_raw = '1';
    transfer_ok <= data_ready = '1' and data_valid_internal;

    process
    begin
        wait until rising_edge(clk) and can_update_acc;
        if (can_update_acc) then i_19 <= (1) + (i_19); end if;
        if (transfer_ok) then num_outputs <= num_outputs + 1; end if;
    end process ;
end;
