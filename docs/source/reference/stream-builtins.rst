Built-In Stream Functions
=========================

This page lists the stream operators that are provided as part of the Sirop language.

.. only:: handshake

    .. function:: StmMap(s: Stm[A, n], f: A -> B): Stm[B, n]

        Applies a function ``f`` element-wise to a stream.

        ::

            > __handshake = true
            > [1:u8, 2:u8, 3:u8, 4:u8]s.StmMap(x => x + 5)
            [6:u8, 7:u8, 8:u8, 9:u8]s

.. only:: not handshake

    .. function:: StmMap(s: Stm[A, n], f: A -> B, head: B = undefined): Stm[B, n]

        Applies a function ``f`` element-wise to a stream.

        ::

            > __handshake = false
            > __show_physical = true
            > [1:u8, 2:u8, 3:u8, 4:u8]s.StmMap(x => x + 5)
            [undefined:u8]s ++ [6:u8, 7:u8, 8:u8, 9:u8]s
            > [1:u8, 2:u8, 3:u8, 4:u8]s.StmMap(x => x + 5, 42:u8)
            [42:u8]s ++ [6:u8, 7:u8, 8:u8, 9:u8]s
