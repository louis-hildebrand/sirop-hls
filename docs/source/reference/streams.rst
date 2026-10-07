Streams
#######

As described in :doc:`/getting-started/language-intro`, a stream is an inherently sequential collection.
Each element in a stream is provided at a different clock cycle.
It is not possible to read a stream out of order (skipping elements, rewinding, etc.).

..
    TODO: show timing diagram

..
    TODO: explain physical prefix
.. _physical-logical-stream:

Parts of a Stream
-----------------

..
    TODO: explain latency matching?

..
    TODO: introduce syntax for stream literals

..
    TODO: explain that compiler flattens all streams
.. _stream-flattening:

Stream Flattening
-----------------

Built-In Stream Functions
-------------------------

The following stream operators are provided as part of the Sirop language.

..
    TODO: add note explaining __handshake and __show_prefix at the beginning of each example?

Creating Streams
^^^^^^^^^^^^^^^^

.. only:: not handshake

    .. function:: StmCount(n: I, init: J = 0, delta: J = 1): Stm[J, n] :: I is an unsigned integer type and J is any integer type

        Creates a stream of ``n`` integers starting at ``init`` and increasing by ``delta``.

        .. literalinclude:: /code-examples/reference/StmCount.repl.txt

.. only:: not handshake

    .. function:: StmCount2D(n: I, m: J): Stm[Stm[(I, J), m], n] :: I and J are unsigned integer types

        Creates a nested stream such that the element at "row" ``i`` and "column" ``j`` is ``(i, j)``.
        The types of ``i`` and ``j`` are the same as the lengths ``n`` and ``m``, respectively.

        ..
            TODO: try to fit example on one line

        .. literalinclude:: /code-examples/reference/StmCount2D.repl.txt

        .. NOTE::
            The value printed in the REPL is :ref:`flattened <stream-flattening>`, as described earlier.
            Nevertheless, the type checker sees the stream as nested.
            If the rest of your code expects a non-nested stream, use :func:`StmJoin`.

        .. literalinclude:: /code-examples/reference/StmCount2D_error.repl.txt

.. only:: not handshake

    .. function:: StmCst(n: I, k: T): Stm[T, n] :: I is an unsigned integer type

        Creates a stream of length ``n`` whose elements are all the constant ``k``.

        .. literalinclude:: /code-examples/reference/StmCst.repl.txt

Transforming Streams Elementwise
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. only:: handshake

    .. function:: StmMap(s: Stm[A, n], f: A -> B): Stm[B, n]

        Applies function ``f`` elementwise to a stream.

        ::

            > __handshake = true
            > [1:u8, 2:u8, 3:u8, 4:u8]s.StmMap(x => x + 5)
            [6:u8, 7:u8, 8:u8, 9:u8]s

        **See also:**

        * :func:`StmMap2`, for simultaneous transformation over two streams

.. only:: not handshake

    .. function:: StmMap(s: Stm[A, n], f: A -> B, head: B = undefined): Stm[B, n]

        Applies function ``f`` elementwise to stream ``s``.

        .. literalinclude:: /code-examples/reference/StmMap.repl.txt

        **See also:**

        * :func:`StmMap2`, for simultaneous transformation over two streams

Combining Multiple Streams
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. only:: not handshake

    .. function:: StmConcat(s1: Stm[T, n], s2: Stm[T, m], head: T = undefined): Stm[T, n+m]

        Concatenates two streams.

        .. literalinclude:: /code-examples/reference/StmConcat.repl.txt

.. only:: not handshake

    .. function:: StmMap2(s1: Stm[A, n], s2: Stm[B, n], f: A -> B -> C, head: C = undefined): Stm[C, n]

        Like :func:`StmMap`, but for two streams at once.

        ..
            TODO: simplify example so it fits on one line?

        .. literalinclude:: /code-examples/reference/StmMap2.repl.txt

        **See also**:

        * :func:`StmZip`, the special case where ``f`` is simply ``x => y => (x, y)``

.. only:: not handshake

    .. function:: StmZip(s1: Stm[A, n], s2: Stm[B, n], head: (A, B) = undefined): Stm[(A, B), n]

        Pair up the elements of two streams.

        ..
            TODO: simplify example so it fits on one line?

        .. literalinclude:: /code-examples/reference/StmZip.repl.txt

        **See also:**

        * :func:`StmMap2`, which zips two streams with an arbitrary function.

Sliding Windows
^^^^^^^^^^^^^^^

.. only:: not handshake

    .. function:: StmSlide(s: Stm[T, n], w: I, head: T = undefined): Stm[Vec[T, w], n-w+1] :: I is an unsigned integer type

        Produces a stream of sliding windows over stream ``s``.
        Each window has size ``w``.

        .. literalinclude:: /code-examples/reference/StmSlide.repl.txt

Aggregation
^^^^^^^^^^^

.. only:: not handshake

    .. function:: StmAll(s: Stm[bool, n]): Stm[bool, 1]

        Returns ``true`` if all elements in the :ref:`logical part <physical-logical-stream>` of stream ``s`` are ``true``.

        .. literalinclude:: /code-examples/reference/StmAll.repl.txt

        When applied to an empty stream, :func:`StmAll` returns ``true``.

        .. literalinclude:: /code-examples/reference/StmAll_empty.repl.txt

        **See also:**

        * :func:`StmFold`, for aggregation with an arbitrary function.

.. only:: not handshake

    .. function:: StmAny(s: Stm[bool, n]): Stm[bool, 1]

        Returns ``true`` if any elements in the :ref:`logical part <physical-logical-stream>` of stream ``s`` are ``true``.

        .. literalinclude:: /code-examples/reference/StmAny.repl.txt

        When applied to an empty stream, :func:`StmAny` returns ``false``.

        .. literalinclude:: /code-examples/reference/StmAny_empty.repl.txt

        **See also:**

        * :func:`StmFold`, for aggregation with an arbitrary function.

.. only:: not handshake

    .. function:: StmFold(s: Stm[A, n], z: B, f: (B, A) -> B): Stm[B, 1]


        Combines the elements in the :ref:`logical part <physical-logical-stream>` of stream ``s`` to a single element using function ``f`` and initial value ``z``.

        For example, for a 3-element stream ``[x1, x2, x3]s``, the result will be ``[ f(f(f(z, x1), x2), x3) ]s``

        .. literalinclude:: /code-examples/reference/StmFold.repl.txt

        Unlike with :func:`StmReduce`, the input stream can be empty.

        .. literalinclude:: /code-examples/reference/StmFold_empty.repl.txt

        **See also:**

        * :func:`StmAll`, which is a special case of :func:`StmFold` with logical AND
        * :func:`StmAny`, which is a special case of :func:`StmFold` with logical OR
        * :func:`StmSum`, which is a special case of :func:`StmFold` with addition
        * :func:`StmReduce`, for aggregation of non-empty streams without needing to specify an initial value

.. only:: not handshake

    .. function:: StmReduce(s: Stm[T, n], f: (T, T)): Stm[T, 1]

        Combines the elements in the :ref:`logical part <physical-logical-stream>` of stream ``s`` to a single element using function ``f``.

        For example, for a 3-element stream ``[x1, x2, x3]s``, the result will be ``[ f(f(x1, x2), x3) ]s``

        .. literalinclude:: /code-examples/reference/StmReduce.repl.txt

        .. WARNING::
            The input stream must be non-empty.

        .. literalinclude:: /code-examples/reference/StmReduce_empty.repl.txt

        **See also:**

        * :func:`StmFold`, for aggregation of possibly empty streams with a given initial value

.. only:: not handshake

    .. function:: StmSum(s: Stm[I, n]): Stm[I, 1] :: I is an integer type

        Returns the sum of the elements in the :ref:`logical part <physical-logical-stream>` of stream ``s``.

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

        Discards ``k`` elements from the :ref:`logical part <physical-logical-stream>` of stream ``s``.

        .. literalinclude:: /code-examples/reference/StmDrop.repl.txt

        .. WARNING::
            It is an error to drop more than the available number of elements.

        .. literalinclude:: /code-examples/reference/StmDrop_too_many.repl.txt

        **See also:**

        * :func:`StmTake`, for discarding elements from the end of a stream

.. only:: not handshake

    .. function:: StmTake(s: Stm[T, n], k: I): Stm[T, k] :: I is an integer type

        Changes the length of stream ``s`` to ``k``.

        .. literalinclude:: /code-examples/reference/StmTake.repl.txt

        .. WARNING::
            If the original stream length ``n`` is less than ``k``, the extra elements will be undefined.

        .. literalinclude:: /code-examples/reference/StmTake_too_many.repl.txt

        **See also:**

        * :func:`StmDrop`, for discarding elements from the beginning of a stream

Using Specialized Digital Signal Processing Blocks
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

FPGAs generally have dedicated digital signal processing (DSP) blocks to perform multiplication and accumulation.
The following stream operators are helpful for taking advantage of this functionality, especially the "systolic mode" on Agilex FPGAs (e.g., https://docs.altera.com/r/docs/683037/24.3.1/agilextm-7-variable-precision-dsp-blocks-user-guide/agilextm-7-variable-precision-dsp-blocks-overview).

.. function:: StmCascade(s: Stm[Vec[T, m], n]): Stm[Vec[T, m], n]

    ..
        TODO: add description and examples

.. function:: StmMapDot(s1: Stm[I, n], s2: Stm[J, n], delay: K): Stm[u44, n] :: I and J are unsigned integer types, K is any integer type
              StmMapDot(s1: Stm[I, n], s2: Stm[J, n], delay: K): Stm[i44, n] :: I, J, and K are integer types

    ..
        TODO: add description and examples

.. function:: StmMapDotCascaded(s1: Stm[I, n], s2: Stm[J, n], delay: K): Stm[u44, n] :: I and J are unsigned integer types, K is any integer type
              StmMapDotCascaded(s1: Stm[I, n], s2: Stm[J, n], delay: K): Stm[u44, n] :: I, J, and K are integer types

    ..
        TODO: add description and examples
