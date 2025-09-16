library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;
use work.conversions.all;

-- Hardware template for letstm.
entity letstm_buf is
generic (
    BIT_WIDTH   : integer;
    BUF_SIZE    : integer;
    N_CONSUMERS : integer);
port (
    clk     : in  std_logic;
    -- Handshake with producer
    p_data  : in  std_logic_vector(BIT_WIDTH - 1 downto 0);
    p_valid : in  std_logic;
    p_ready : out std_logic;
    -- Handshake with consumer
    c_data  : out std_logic_vector(0 to N_CONSUMERS*BIT_WIDTH-1);
    c_valid : out std_logic_vector(0 to N_CONSUMERS-1);
    c_ready : in  std_logic_vector(0 to N_CONSUMERS-1));
end letstm_buf;

architecture arch of letstm_buf is

    subtype idx_type is natural range 0 to BUF_SIZE;
    type buf_type is array (0 to BUF_SIZE) of std_logic_vector(BIT_WIDTH-1 downto 0);
    type ptr_array_type is array (0 to N_CONSUMERS-1) of idx_type;
    type bool_array_type is array (0 to N_CONSUMERS-1) of boolean;
    type c_data_type is array (0 to N_CONSUMERS-1) of std_logic_vector(BIT_WIDTH-1 downto 0);

    signal p_ready_internal : boolean;
    signal p_valid_internal : boolean;
    signal c_ready_internal : bool_array_type;
    signal c_valid_internal : bool_array_type;
    signal c_data_internal  : c_data_type;

    -- Use circular buffer with "keep one open" scheme, as in https://vhdlwhiz.com/ring-buffer-fifo/
    -- Each consumer has its own pointer to the index it is currently reading from.
    signal buf : buf_type;
    signal tail : idx_type := 0;
    signal head : idx_type := 0;
    signal consumer_pointers : ptr_array_type := (others => 0);

    signal buf_empty : boolean;
    signal buf_full : boolean;
    signal will_increment_head : boolean;
    signal will_increment_consumer_ptr : bool_array_type;
    signal will_increment_tail : boolean;

    pure function next_idx (i : idx_type) return idx_type is
    begin
        if (i = idx_type'high) then
            return 0;
        else
            return i + 1;
        end if;
    end function;

begin

    -- Intermediate signals
    buf_empty <= head = tail;
    buf_full <= next_idx(head) = tail;
    will_increment_head <= p_ready_internal and p_valid_internal;

    proc_will_increment_consumer_ptr : process (c_ready_internal, c_valid_internal)
    begin
        for i in c_ready_internal'range loop
            will_increment_consumer_ptr(i) <= c_ready_internal(i) and c_valid_internal(i);
        end loop;
    end process;

    proc_will_increment_tail : process (will_increment_consumer_ptr, consumer_pointers, tail)
        variable acc : boolean;
        variable is_laggard : boolean;
    begin
        acc := true;
        for i in will_increment_consumer_ptr'range loop
            is_laggard := consumer_pointers(i) = tail;
            acc := acc and (not is_laggard or will_increment_consumer_ptr(i));
        end loop;
        will_increment_tail <= acc;
    end process;

    -- Handshake with producer
    p_ready_internal <= not buf_full or will_increment_tail;
    p_ready <= bool2sl(p_ready_internal);
    p_valid_internal <= sl2bool(p_valid);

    proc_write_buf : process
    begin
        wait until rising_edge(clk);
        if will_increment_head then
            buf(head) <= p_data;
            head <= next_idx(head);
        end if;
        if will_increment_tail then
            tail <= next_idx(tail);
        end if;
    end process;

    -- Handshake with consumer
    proc_c_ready_internal : process (c_ready)
    begin
        for i in c_ready'range loop
            c_ready_internal(i) <= sl2bool(c_ready(i));
        end loop;
    end process;

    proc_data_valid_internal : process (buf, consumer_pointers, head)
    begin
        for i in consumer_pointers'range loop
            c_data_internal(i) <= buf(consumer_pointers(i));
            c_valid_internal(i) <= (consumer_pointers(i) /= head);
        end loop;
    end process;

    proc_c_data : process (c_data_internal)
        variable lsb : natural range 0 to N_CONSUMERS*BIT_WIDTH-1;
        variable msb : natural range 0 to N_CONSUMERS*BIT_WIDTH-1;
    begin
        for i in c_data_internal'range loop
            lsb := i * BIT_WIDTH;
            msb := lsb + BIT_WIDTH - 1;
            c_data(lsb to msb) <= c_data_internal(i);
        end loop;
    end process;

    proc_c_valid : process (c_valid_internal)
    begin
        for i in c_valid_internal'range loop
            c_valid(i) <= bool2sl(c_valid_internal(i));
        end loop;
    end process;

    proc_consumer_pointers : process
    begin
        wait until rising_edge(clk);
        for i in will_increment_consumer_ptr'range loop
            if (will_increment_consumer_ptr(i)) then
                consumer_pointers(i) <= next_idx(consumer_pointers(i));
            end if;
        end loop;
    end process;
end arch;
