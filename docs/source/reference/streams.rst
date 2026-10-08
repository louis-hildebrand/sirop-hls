Streams
#######

As described in :doc:`/getting-started/language-intro`, a stream is an inherently sequential collection.
Each element in a stream is provided at a different clock cycle.
It is not possible to read a stream out of order (skipping elements, rewinding, etc.).

.. _physical-logical-stream:

Parts of a Stream
-----------------

Sirop streams have two parts: a "physical prefix" and a "logical part."
To see why, consider the following `timing diagram <https://en.wikipedia.org/wiki/Digital_timing_diagram>`__:

.. image:: /figures/dark/physical-logical-stream.*
    :class: only-dark
    :scale: 250%
.. image:: /figures/light/physical-logical-stream.*
    :class: only-light
    :scale: 250%

What *logical* data does this stream carry?
There is more than one possible interpretation:

* If there is no latency, the data we care about is ``[1, 2, 3, 4, 5, ...]``.
* If the latency is 1 clock cycle, the data we care about is ``[2, 3, 4, 5, ...]``. The preceding values (``[..., 1]``) should be ignored.
* If the latency is 2 clock cycles, the data we care about is ``[3, 4, 5, ...]``. The preceding values (``[..., 1, 2]``) should be ignored.
* etc.

In Sirop, these interpretations are expressed as follows:

* 0-cycle latency: ``[]s ++ [1, 2, 3, 4, 5]s`` (or, more commonly, just ``[1, 2, 3, 4, 5]s``)
* 1-cycle latency: ``[1]s ++ [2, 3, 4, 5]s``
* 2-cycle latency: ``[1, 2]s ++ [3, 4, 5]s``
* etc.

The part before the ``++`` is the "physical prefix."
We will see these outputs if we observe a design running on a physical FPGA, but they are not part of the logical output we care about.
The part after the ``++`` is the "logical part."

Controlling the Physical Prefix
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Why even write down the physical prefix if we don't care about it?
The answer is that, in some cases, we care about *some properties* of the physical prefix.
For example, we might want the generated VHDL entity to produce a ``valid`` output bit.
In the physical prefix, the ``valid`` bit must always be ``false``.
In the logical part, the ``valid`` bit may be ``true`` or ``false``.

.. literalinclude:: /code-examples/reference/square_with_valid_o.sirop

Many of the built-in functions listed provide some way of controlling the physical prefix.
For instance, as shown in the sample code above, :func:`StmMap` has a ``head`` parameter to set the initial value of the output register.

..
    TODO: link to a page explaining the REPL?

In the REPL, the physical prefix is hidden by default.
If it's important to see the physical prefix, enable it by setting the special variable ``__show_prefix``.

.. literalinclude:: /code-examples/reference/show_prefix.repl.txt

.. _stream-flattening:

Stream Flattening
-----------------

The Sirop compiler flattens nested streams.
(There is only one time dimension, after all.)
For example:

.. literalinclude:: /code-examples/reference/stream_flattening.repl.txt

Flattening happens *after* type checking.
Therefore, if downstream functions require a flat stream, you should use :func:`StmJoin` to explicitly flatten.

.. literalinclude:: /code-examples/reference/stream_flattening_error.repl.txt

Built-In Stream Functions
-------------------------

The following stream operators are provided as part of the Sirop language.

..
    TODO: add note explaining __handshake and __show_prefix at the beginning of each example?
    Or add a section explaining this and link back to it in each "See also" list?

Creating Streams
^^^^^^^^^^^^^^^^

.. only:: not handshake

    .. function:: StmCst(n: I, k: T): Stm[T, n] :: I is an unsigned integer type

        Creates a stream of length ``n`` whose elements are all the constant ``k``.

        .. literalinclude:: /code-examples/reference/StmCst.repl.txt

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
            The REPL prints a 1-dimensional stream due to :ref:`stream flattening <stream-flattening>`.

Transforming Streams Elementwise
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. only:: handshake

    .. function:: StmMap(s: Stm[A, n], f: A -> B): Stm[B, n]

        Applies function ``f`` elementwise to stream ``s``.

        ::

            > __handshake = true
            > [1:u8, 2:u8, 3:u8, 4:u8]s.StmMap(x => x + 5)
            [6:u8, 7:u8, 8:u8, 9:u8]s

        .. seealso::

            :func:`StmMap2`, for simultaneous transformation over two streams

.. only:: not handshake

    .. function:: StmMap(s: Stm[A, n], f: A -> B, head: B = undefined): Stm[B, n]

        Applies function ``f`` elementwise to stream ``s``.

        .. literalinclude:: /code-examples/reference/StmMap.repl.txt

        .. seealso::

            :func:`StmMap2`, for simultaneous transformation over two streams

Combining Multiple Streams
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. only:: not handshake

    .. function:: StmConcat(s1: Stm[T, n], s2: Stm[T, m], head: T = undefined): Stm[T, n+m]

        Concatenates two streams.

        .. literalinclude:: /code-examples/reference/StmConcat.repl.txt

.. only:: not handshake

    .. function:: StmZip(s1: Stm[A, n], s2: Stm[B, n], head: (A, B) = undefined): Stm[(A, B), n]

        Pair up the elements of two streams.

        .. literalinclude:: /code-examples/reference/StmZip.repl.txt

     .. seealso::

            :func:`StmMap2`, which zips two streams with an arbitrary function.

.. only:: not handshake

    .. function:: StmMap2(s1: Stm[A, n], s2: Stm[B, n], f: A -> B -> C, head: C = undefined): Stm[C, n]

        Like :func:`StmMap`, but for two streams at once.

        ..
            TODO: simplify example so it fits on one line?

        .. literalinclude:: /code-examples/reference/StmMap2.repl.txt

        .. seealso::

            :func:`StmZip`, the special case where ``f`` is simply ``x => y => (x, y)``

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

        .. seealso::

            :func:`StmFold`, for aggregation with an arbitrary function.

.. only:: not handshake

    .. function:: StmAny(s: Stm[bool, n]): Stm[bool, 1]

        Returns ``true`` if any elements in the :ref:`logical part <physical-logical-stream>` of stream ``s`` are ``true``.

        .. literalinclude:: /code-examples/reference/StmAny.repl.txt

        When applied to an empty stream, :func:`StmAny` returns ``false``.

        .. literalinclude:: /code-examples/reference/StmAny_empty.repl.txt

        .. seealso::

            :func:`StmFold`, for aggregation with an arbitrary function.

.. only:: not handshake

    .. function:: StmSum(s: Stm[I, n]): Stm[I, 1] :: I is an integer type

        Returns the sum of the elements in the :ref:`logical part <physical-logical-stream>` of stream ``s``.

        .. literalinclude:: /code-examples/reference/StmSum.repl.txt

        When applied to an empty stream, :func:`StmSum` returns 0.

        .. literalinclude:: /code-examples/reference/StmSum_empty.repl.txt

        .. WARNING::
            Beware of `overflow <https://en.wikipedia.org/wiki/Integer_overflow>`!
            The sum is performed with the same type as the inputs.

        .. literalinclude:: /code-examples/reference/StmSum_overflow.repl.txt

        .. seealso::

            :func:`StmFold`, for aggregation with an arbitrary function.

.. only:: not handshake

    .. function:: StmFold(s: Stm[A, n], z: B, f: (B, A) -> B): Stm[B, 1]


        Combines the elements in the :ref:`logical part <physical-logical-stream>` of stream ``s`` to a single element using function ``f`` and initial value ``z``.

        For example, for a 3-element stream ``[x1, x2, x3]s``, the result will be ``[ f(f(f(z, x1), x2), x3) ]s``

        .. literalinclude:: /code-examples/reference/StmFold.repl.txt

        Unlike with :func:`StmReduce`, the input stream can be empty.

        .. literalinclude:: /code-examples/reference/StmFold_empty.repl.txt

        .. seealso::

            :func:`StmAll`, which is a special case of :func:`StmFold` with logical AND

            :func:`StmAny`, which is a special case of :func:`StmFold` with logical OR

            :func:`StmSum`, which is a special case of :func:`StmFold` with addition

            :func:`StmReduce`, for aggregation of non-empty streams without needing to specify an initial value

.. only:: not handshake

    .. function:: StmReduce(s: Stm[T, n], f: (T, T)): Stm[T, 1]

        Combines the elements in the :ref:`logical part <physical-logical-stream>` of stream ``s`` to a single element using function ``f``.

        For example, for a 3-element stream ``[x1, x2, x3]s``, the result will be ``[ f(f(x1, x2), x3) ]s``

        .. literalinclude:: /code-examples/reference/StmReduce.repl.txt

        .. WARNING::
            The input stream must be non-empty.

        .. literalinclude:: /code-examples/reference/StmReduce_empty.repl.txt

        .. seealso::

            :func:`StmFold`, for aggregation of possibly empty streams with a given initial value

Nested Streams
^^^^^^^^^^^^^^

.. only:: not handshake

    .. function:: StmJoin(s: Stm[Stm[T, m], n]): Stm[T, n*m]

        Removes the outermost level of nesting from the given stream.

        .. literalinclude:: /code-examples/reference/StmJoin.repl.txt

        .. NOTE::
            The REPL prints 1-dimensional streams in each case due to :ref:`stream flattening <stream-flattening>`.

        .. seealso::

            :func:`StmSplit`, for converting a flat stream back to a nested stream

.. only:: not handshake

    .. function:: StmSplit(s: Stm[T, n], m: I): Stm[Stm[T, m], n/m] :: I is an integer type

        Increases the level of nesting in the given stream.

        .. literalinclude:: /code-examples/reference/StmSplit.repl.txt

        .. NOTE::
            The REPL prints 1-dimensional streams in each case due to :ref:`stream flattening <stream-flattening>`.

        .. WARNING::
            If ``m`` does not divide ``n``, the output stream will be shorter than the input stream.

        .. literalinclude:: /code-examples/reference/StmSplit_not_divisible.repl.txt

        .. seealso::

            :func:`StmJoin`, for converting a nested stream back to a flat stream

Discarding Parts of a Stream
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. only:: not handshake

    .. function:: StmDrop(s: Stm[T, n], k: I): Stm[T, n-k] :: I is an integer type

        Discards ``k`` elements from the :ref:`logical part <physical-logical-stream>` of stream ``s``.

        .. literalinclude:: /code-examples/reference/StmDrop.repl.txt

        .. WARNING::
            It is an error to drop more than the available number of elements.

        .. literalinclude:: /code-examples/reference/StmDrop_too_many.repl.txt

        .. seealso::

            :func:`StmTake`, for discarding elements from the end of a stream

.. only:: not handshake

    .. function:: StmTake(s: Stm[T, n], k: I): Stm[T, k] :: I is an integer type

        Changes the length of stream ``s`` to ``k``.

        .. literalinclude:: /code-examples/reference/StmTake.repl.txt

        .. WARNING::
            If ``k`` is greater than ``n``, the extra elements will be undefined.

        .. literalinclude:: /code-examples/reference/StmTake_too_many.repl.txt

        .. seealso::

            :func:`StmDrop`, for discarding elements from the beginning of a stream

Using Dedicated DSP Blocks
^^^^^^^^^^^^^^^^^^^^^^^^^^

FPGAs generally have dedicated digital signal processing (DSP) blocks to perform multiplication and accumulation.
The following stream operators are helpful for taking advantage of this functionality, especially the "systolic mode" on Agilex FPGAs (e.g., https://docs.altera.com/r/docs/683037/24.3.1/agilextm-7-variable-precision-dsp-blocks-user-guide/agilextm-7-variable-precision-dsp-blocks-overview).

.. function:: StmMapDot(s1: Stm[Vec[I, m], n], s2: Stm[Vec[J, m], n], delay: K): Stm[u44, n] :: I, J, and K are unsigned integer types
              StmMapDot(s1: Stm[Vec[I, m], n], s2: Stm[Vec[J, m], n], delay: K): Stm[i44, n] :: I and J are integer types, and K is an unsigned integer type

    Computes the dot product of each vector in ``s1`` with the corresponding vector in ``s2``.
    This is defined so that the generated VHDL uses the DSPs in systolic mode.
    ``delay`` is the number of internal registers to enable in the DSPs; increasing this number will increase latency and increase the maximum clock frequency.

    .. literalinclude:: /code-examples/reference/StmMapDot.repl.txt

.. function:: StmMapDotCascaded(s1: Stm[Vec[I, m], n], s2: Stm[Vec[J, m], n], delay: K): Stm[u44, n] :: I and J are unsigned integer types, K is any integer type
              StmMapDotCascaded(s1: Stm[Vec[I, m], n], s2: Stm[Vec[J, m], n], delay: K): Stm[u44, n] :: I, J, and K are integer types

    Like :func:`StmMapDot`, but the input should be cascaded (like the output from :func:`StmCascade`).
    In other words, :func:`StmMapDot` is equivalent to :func:`StmCascade` followed by :func:`StmMapDotCascaded`.

    In systolic mode, each input to the DSP chain must be delayed by one cycle relative to the previous input.
    :func:`StmMapDotCascaded` behaves the same way.
    :func:`StmMapDot` is more user-friendly, but :func:`StmMapDotCascaded` gives you more control in case you want to create the input cascade without :func:`StmCascade`.

.. function:: StmCascade(s: Stm[Vec[T, m], n]): Stm[Vec[T, m], n]

    Delays each element of the vector by 1 cycle relative to the previous element.

    .. literalinclude:: /code-examples/reference/StmCascade.repl.txt
