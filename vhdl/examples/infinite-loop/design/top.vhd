-- StmCount(12)
library IEEE ;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;

entity top is
port (
    clk        : in  std_logic;
    data       : out std_logic_vector(31 downto 0);
    data_valid : out std_logic;
    data_ready : in  std_logic);
end top;

architecture arch of top is
    signal i : integer := 1;  -- <-- Wrong initial value
    signal data_valid_internal : boolean;
    signal data_internal : integer := 0;
    signal transfer_ok : boolean;
begin
    data_valid_internal <= false;  -- <-- Testbench will hang
    data_valid <= '1' when data_valid_internal else '0';

    transfer_ok <= data_ready = '1' and data_valid_internal;

    data_internal <= i when transfer_ok else 0;
    data <= std_logic_vector(to_signed(data_internal, data'length));

	process
	begin
        wait until rising_edge(clk);
        if transfer_ok then
            i <= i + 1;
        end if;
	end process ;
end arch;
