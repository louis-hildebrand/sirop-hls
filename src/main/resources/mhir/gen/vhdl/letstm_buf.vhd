library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.numeric_std.all;
use IEEE.math_real.all;
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

-- This architecture uses BRAMs rather than registers and is designed to meet the timing requirements even for
-- somewhat large buffers (2000 elements).
-- You may be able to shave off one or two ALMs for the common case where BUF_SIZE = 1 by writing a specialized
-- architecture for that case, but it's probably not worth the extra code.
architecture arch of letstm_buf is

    -- Add one extra slot to the queue to improve throughput.
    --   Suppose the buffer size is 1 and we only request a new piece of data when the buffer is not full.
    --   Then we would only request a new element every other cycle, which would ruin the pipeline's throughput.
    --   But if we add one extra slot to the buffer, we can fill one slot while the other is being read.
    -- Add another extra slot to the queue to be able to distinguish between the empty and full states, as in
    -- https://vhdlwhiz.com/ring-buffer-fifo/.
    constant RAM_LEN : natural := BUF_SIZE + 2;
    constant IDX_WIDTH : natural := integer(ceil(log2(real(RAM_LEN))));
    subtype idx_type is natural range 0 to RAM_LEN-1;
    type ptr_array_type is array (0 to N_CONSUMERS-1) of idx_type;
    type bool_array_type is array (0 to N_CONSUMERS-1) of boolean;
    type c_data_type is array (0 to N_CONSUMERS-1) of std_logic_vector(BIT_WIDTH-1 downto 0);

    pure function next_idx (i : idx_type) return idx_type is
    begin
        if (i = idx_type'high) then
            return idx_type'low;
        else
            return i + 1;
        end if;
    end function;

    -- Handshake with producer and consumers
    signal p_ready_internal : boolean;
    signal p_valid_internal : boolean;
    signal c_ready_internal : bool_array_type;
    signal c_valid_internal : bool_array_type;
    signal c_data_internal  : c_data_type;

    -- Memory for buffer
    signal tail : idx_type := 0;
    signal head : idx_type := 0;
    signal after_head : idx_type := next_idx(0);
    signal c_ptr : ptr_array_type := (others => 0);
    signal rd_addr : std_logic_vector(N_CONSUMERS*IDX_WIDTH-1 downto 0);
    signal rd_data : std_logic_vector(N_CONSUMERS*BIT_WIDTH-1 downto 0);
    signal wr_enable : std_logic;
    signal wr_addr : unsigned(IDX_WIDTH-1 downto 0);
    signal wr_data : std_logic_vector(BIT_WIDTH-1 downto 0);

    -- Intermediate signals
    signal buf_full : boolean := false;
    signal will_increment_head : boolean;
    signal will_increment_tail : boolean;
    signal will_increment_c_ptr : bool_array_type;
    signal c_ptr_at_head : bool_array_type;
    signal c_ptr_at_tail : bool_array_type;

begin

    -- Memory for buffer
    MULTI_RAM : entity work.multi_consumer_ram
        generic map (
            ADDR_WIDTH => IDX_WIDTH,
            RAM_LEN => RAM_LEN,
            DATA_WIDTH => BIT_WIDTH,
            N_CONSUMERS => N_CONSUMERS)
        port map (
            clk => clk,
            rd_addr => rd_addr,
            rd_data => rd_data,
            wr_enable => wr_enable,
            wr_addr => wr_addr,
            wr_data => wr_data);

    wr_enable <= bool2sl(will_increment_head);
    wr_addr <= to_unsigned(head, IDX_WIDTH);
    wr_data <= p_data;

    proc_pointers : process
    begin
        wait until rising_edge(clk);
        -- head
        if will_increment_head then
            head <= after_head;
            after_head <= next_idx(after_head);
        end if;
        -- tail
        if will_increment_tail then
            tail <= next_idx(tail);
        end if;
        -- consumer pointers
        for i in will_increment_c_ptr'range loop
            if will_increment_c_ptr(i) then
                c_ptr(i) <= next_idx(c_ptr(i));
            end if;
        end loop;
    end process;

    proc_rd_addr : process (c_ptr)
        variable msb : natural range 0 to N_CONSUMERS*IDX_WIDTH-1;
        variable lsb : natural range 0 to N_CONSUMERS*IDX_WIDTH-1;
    begin
        for i in c_ptr'range loop
            msb := (N_CONSUMERS - i) * IDX_WIDTH - 1;
            lsb := msb - IDX_WIDTH + 1;
            rd_addr(msb downto lsb) <= std_logic_vector(to_unsigned(c_ptr(i), IDX_WIDTH));
        end loop;
    end process;

    -- Intermediate signals
    buf_full <= after_head = tail;

    proc_c_ptr_at_head : process (c_ptr, head)
    begin
        for i in c_ptr'range loop
            c_ptr_at_head(i) <= c_ptr(i) = head;
        end loop;
    end process;

    proc_c_ptr_at_tail : process (c_ptr, tail)
    begin
        for i in c_ptr'range loop
            c_ptr_at_tail(i) <= c_ptr(i) = tail;
        end loop;
    end process;

    will_increment_head <= p_ready_internal and p_valid_internal;

    proc_will_increment_c_ptr : process (c_ready_internal, c_valid_internal, c_ptr_at_head)
        variable buf_has_data : boolean;
        variable out_reg_is_free : boolean;
    begin
        for i in c_ready_internal'range loop
            buf_has_data := not c_ptr_at_head(i);
            out_reg_is_free := not c_valid_internal(i) or c_ready_internal(i);
            will_increment_c_ptr(i) <= buf_has_data and out_reg_is_free;
        end loop;
    end process;

    proc_will_increment_tail : process (will_increment_c_ptr, c_ptr_at_tail)
        variable acc : boolean;
    begin
        acc := true;
        for i in will_increment_c_ptr'range loop
            acc := acc and (not c_ptr_at_tail(i) or will_increment_c_ptr(i));
        end loop;
        will_increment_tail <= acc;
    end process;

    -- Handshake with producer
    -- You could slightly decrease the latency by using `not buf_full or will_increment_tail` instead.
    -- Unfortunately, will_increment_tail has fairly long delay and therefore the timing requirements may not be met.
    p_ready_internal <= not buf_full;
    p_ready <= bool2sl(p_ready_internal);
    p_valid_internal <= sl2bool(p_valid);

    -- Handshake with consumer
    proc_c_ready_internal : process (c_ready)
    begin
        for i in c_ready'range loop
            c_ready_internal(i) <= sl2bool(c_ready(i));
        end loop;
    end process;

    proc_data_valid_internal : process
        variable msb : natural range 0 to N_CONSUMERS*BIT_WIDTH-1;
        variable lsb : natural range 0 to N_CONSUMERS*BIT_WIDTH-1;
    begin
        wait until rising_edge(clk);
        for i in c_data_internal'range loop
            if will_increment_c_ptr(i) then
                msb := (N_CONSUMERS - i) * BIT_WIDTH - 1;
                lsb := msb - BIT_WIDTH + 1;
                c_data_internal(i) <= rd_data(msb downto lsb);
                c_valid_internal(i) <= true;
            elsif c_ready_internal(i) and c_valid_internal(i) then
                c_valid_internal(i) <= false;
            end if;
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

end arch;
