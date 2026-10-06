Streams
#######

As described in :doc:`/getting-started/language-intro`, a stream is an inherently sequential collection.
Each element in a stream is provided at a different clock cycle.
It is not possible to read a stream out of order (skipping elements, rewinding, etc.).

..
    TODO: explain physical prefix

..
    TODO: introduce syntax for stream literals

..
    TODO: explain that compiler flattens all streams


Built-In Stream Functions
-------------------------

The following stream operators are provided as part of the Sirop language.

.. only:: not handshake

    .. function:: StmCascade(s: Stm[Vec[T, m], n]): Stm[Vec[T, m], n]

        ..
            TODO: add description and examples

.. only:: handshake

    .. function:: StmMap(s: Stm[A, n], f: A -> B): Stm[B, n]

        Applies a function ``f`` element-wise to a stream.

        ::

            > __handshake = true
            > [1:u8, 2:u8, 3:u8, 4:u8]s.StmMap(x => x + 5)
            [6:u8, 7:u8, 8:u8, 9:u8]s

.. only:: not handshake

    .. function:: StmMap(s: Stm[A, n], f: A -> B, head: B = undefined): Stm[B, n]

        Applies a function ``f`` element-wise to stream ``s``.

        ::

            > __handshake = false
            > __show_prefix = true
            > ([0:u8]s ++ [1:u8, 2:u8, 3:u8, 4:u8]s).StmMap(x => x + 5)
            [undefined:u8, 5:u8]s ++ [6:u8, 7:u8, 8:u8, 9:u8]s
            > ([0:u8]s ++ [1:u8, 2:u8, 3:u8, 4:u8]s).StmMap(x => x + 5, 42:u8)
            [42:u8, 5:u8]s ++ [6:u8, 7:u8, 8:u8, 9:u8]s

        **See also:**

        * :func:`StmMap2`, for simultaneous transformation over two streams

.. only:: not handshake

    .. function:: StmMapDot(s1: Stm[I, n], s2: Stm[J, n], delay: K): Stm[u44, n] :: I and J are unsigned integer types, K is any integer type
                  StmMapDot(s1: Stm[I, n], s2: Stm[J, n], delay: K): Stm[i44, n] :: I, J, and K are integer types

        ..
            TODO: add description and examples

.. only:: not handshake

    .. function:: StmMapDotCascaded(s1: Stm[I, n], s2: Stm[J, n], delay: K): Stm[u44, n] :: I and J are unsigned integer types, K is any integer type
                  StmMapDotCascaded(s1: Stm[I, n], s2: Stm[J, n], delay: K): Stm[u44, n] :: I, J, and K are integer types

        ..
            TODO: add description and examples

.. only:: not handshake

    .. function:: StmSlide(s: Stm[T, n], w: I, head: T = undefined): Stm[Vec[T, w], n-w+1] :: I is an integer type

        ..
            TODO: add description and examples

Stream Sources
^^^^^^^^^^^^^^

.. only:: not handshake

    .. function:: StmCst(n: I, k: T): Stm[T, n] :: I is an integer type

        ..
            TODO: add description and examples

.. only:: not handshake

    .. function:: StmCount2D(n: I, m: J): Stm[Stm[(I, J), m], n]

        ..
            TODO: add description and examples

.. only:: not handshake

    .. function:: StmRange(n: I, z: J, d: J): Stm[J, n] :: I and J are integer types

        ..
            TODO: add description and examples

Combining Multiple Streams
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. only:: not handshake

    .. function:: StmConcat(s1: Stm[T, n], s2: Stm[T, m]): Stm[T, n+m]

        ..
            TODO: add description and examples

.. only:: not handshake

    .. function:: StmMap2(s1: Stm[A, n], s2: Stm[B, n], f: A -> B -> C, head: C = undefined): Stm[C, n]

        Like :func:`StmMap`, but for two streams at once.

        ..
            TODO: simplify example so it fits on one line?

        ::

            > __handshake = false
            > __show_prefix = true
            > u = [0:u8]s ++ [1:u8, 2:u8, 3:u8, 4:u8]s
            > v = [-1:i16]s ++ [-9:i16, -8:i16, -7:i16, -6:i16]s
            > StmMap2(u, v, x => y => (x, y, x*y))
            [
              undefined:(u8, i16, i16),
              (0:u8, -1:i16, 0:i16)
            ]s ++ [
              (1:u8, -9:i16, -9:i16),
              (2:u8, -8:i16, -16:i16),
              (3:u8, -7:i16, -21:i16),
              (4:u8, -6:i16, -24:i16)
            ]s
            > StmMap2(u, v, x => y => (x, y, x*y), (0:u8, 0:i16, 0:i16))
            [
              (0:u8, 0:i16, 0:i16),
              (0:u8, -1:i16, 0:i16)
            ]s ++ [
              (1:u8, -9:i16, -9:i16),
              (2:u8, -8:i16, -16:i16),
              (3:u8, -7:i16, -21:i16),
              (4:u8, -6:i16, -24:i16)
            ]s

        **See also**:

        * :func:`StmZip`, the special case where ``f`` is simply ``x => y => (x, y)``

.. only:: not handshake

    .. function:: StmZip(s1: Stm[A, n], s2: Stm[B, n], head: (A, B) = undefined): Stm[(A, B), n]

        Pair up the elements of two streams.

        ..
            TODO: simplify example so it fits on one line?

        ::

            > __handshake = false
            > __show_prefix = true
            > u = [0:u8]s ++ [1:u8, 2:u8, 3:u8]s
            > v = [-1:i16]s ++ [-9:i16, -8:i16, -7:i16]s
            > u.StmZip(v)
            [
              undefined:(u8, i16),
              (0:u8, -1:i16)
            ]s ++ [
              (1:u8, -9:i16),
              (2:u8, -8:i16),
              (3:u8, -7:i16)
            ]s
            > u.StmZip(v, (42:u8, 43:i16))
            [
              (42:u8, 43:i16),
              (0:u8, -1:i16)
            ]s ++ [
              (1:u8, -9:i16),
              (2:u8, -8:i16),
              (3:u8, -7:i16)
            ]s

        **See also:**

        * :func:`StmMap2`, which is more general.

Aggregation
^^^^^^^^^^^

.. only:: not handshake

    .. function:: StmAll(s: Stm[bool, n]): Stm[bool, 1]

        Returns ``true`` if all elements of *the logical part of* the given stream are ``true``.

        .. literalinclude:: /code-examples/reference/StmAll.repl.txt

        When applied to an empty stream, :func:`StmAll` returns ``true``.

        .. literalinclude:: /code-examples/reference/StmAll_empty.repl.txt

        **See also:**

        * :func:`StmFold`, for aggregation with an arbitrary function.

.. only:: not handshake

    .. function:: StmAny(s: Stm[bool, n]): Stm[bool, 1]

        Returns ``true`` if any elements in *the logical part of* the given stream are ``true``.

        .. literalinclude:: /code-examples/reference/StmAny.repl.txt

        When applied to an empty stream, :func:`StmAny` returns ``false``.

        .. literalinclude:: /code-examples/reference/StmAny_empty.repl.txt

        **See also:**

        * :func:`StmFold`, for aggregation with an arbitrary function.

.. only:: not handshake

    .. function:: StmFold(s: Stm[A, n], z: B, f: (B, A) -> B): Stm[B, 1]

        ..
            TODO: Write description with examples

        **See also:**

        * :func:`StmAll`, which is a special case of :func:`StmFold` with logical AND
        * :func:`StmAny`, which is a special case of :func:`StmFold` with logical OR
        * :func:`StmReduce`, for aggregation of non-empty streams without needing to specify an initial value
        * :func:`StmSum`, which is a special case of :func:`StmFold` with addition

.. only:: not handshake

    .. function:: StmReduce(s: Stm[T, n], f: (T, T)): Stm[T, 1]

        ..
            TODO: Write description with examples

        **See also:**

        * :func:`StmFold`, for aggregation of possibly empty streams with a given initial value

.. only:: not handshake

    .. function:: StmSum(s: Stm[I, n]): Stm[I, 1] :: I is an integer type

        Returns the sum of the elements in *the logical part of* the given stream.

        .. literalinclude:: /code-examples/reference/StmSum.repl.txt

        .. WARNING::

            The sum is performed with the same type as the inputs.
            Beware of overflow!

        .. literalinclude:: /code-examples/reference/StmSum_overflow.repl.txt

        When applied to an empty stream, :func:`StmSum` returns 0.

        .. literalinclude:: /code-examples/reference/StmSum_empty.repl.txt

        **See also:**

        * :func:`StmFold`, for aggregation with an arbitrary function.

Nested Streams
^^^^^^^^^^^^^^

.. only:: not handshake

    .. function:: StmJoin(s: Stm[Stm[T, m], n]): Stm[T, n*m]

        ..
            TODO: add description and examples

        **See also:**

        * :func:`StmSplit`, for converting a flat stream back to a nested stream

.. only:: not handshake

    .. function:: StmSplit(s: Stm[T, n], m: I): Stm[Stm[T, m], n/m] :: I is an integer type

        ..
            TODO: add description and examples

        **See also:**

        * :func:`StmJoin`, for converting a nested stream back to a flat stream

Discarding Parts of a Stream
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. only:: not handshake

    .. function:: StmDrop(s: Stm[T, n], k: I): Stm[T, n-k] :: I is an integer type

        ..
            TODO: add description and examples

        **See also:**

        * :func:`StmTake`, for discarding elements from the end of a stream

.. only:: not handshake

    .. function:: StmTake(s: Stm[T, n], k: I): Stm[T, k] :: I is an integer type

        ..
            TODO: add description and examples

        **See also:**

        * :func:`StmDrop`, for discarding elements from the beginning of a stream
