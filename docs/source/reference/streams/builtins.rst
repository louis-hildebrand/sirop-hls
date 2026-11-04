Built-In Stream Operators
#########################

The following stream-related operators are built into the Sirop language.

..
    TODO: add note explaining __handshake and __show_prefix at the beginning of each example?
    Or add a section explaining this and link back to it in each "See also" list?

Creating Streams
----------------

.. only:: not handshake

    .. function:: StmCst(n: I, k: T): Stm[T, n] :: I is an unsigned integer type

        Creates a stream of length ``n`` whose elements are all the constant ``k``.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmCst.repl.txt

.. only:: not handshake

    .. function:: StmCount(n: I, init: J = 0, delta: J = 1): Stm[J, n] :: I is an unsigned integer type and J is any integer type

        Creates a stream of ``n`` integers starting at ``init`` and increasing by ``delta``.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmCount.repl.txt

.. only:: not handshake

    .. function:: StmCount2D(n: I, m: J): Stm[Stm[(I, J), m], n] :: I and J are unsigned integer types

        Creates a nested stream such that the element at "row" ``i`` and "column" ``j`` is ``(i, j)``.
        The types of ``i`` and ``j`` are the same as the lengths ``n`` and ``m``, respectively.

        ..
            TODO: try to fit example on one line

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmCount2D.repl.txt

        .. NOTE::
            The REPL prints a 1-dimensional stream due to :ref:`stream flattening <stream-flattening>`.

Transforming Streams Elementwise
--------------------------------

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

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmMap.repl.txt

        .. seealso::

            :func:`StmMap2`, for simultaneous transformation over two streams

Combining Multiple Streams
--------------------------

.. only:: not handshake

    .. function:: StmConcat(s1: Stm[T, n], s2: Stm[T, m], head: ~T = undefined): Stm[T, n+m]

        Concatenates two streams.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmConcat.repl.txt

.. only:: not handshake

    .. function:: StmZip(s1: Stm[A, n], s2: Stm[B, n], head: (A, B) = undefined): Stm[(A, B), n]

        Pairs up the elements of two streams.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmZip.repl.txt

        .. seealso::

            :func:`StmMap2`, which zips two streams with an arbitrary function.

.. only:: not handshake

    .. function:: StmMap2(s1: Stm[A, n], s2: Stm[B, n], f: A -> B -> C, head: C = undefined): Stm[C, n]

        Like :func:`StmMap`, but for two streams at once.

        ..
            TODO: simplify example so it fits on one line?

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmMap2.repl.txt

        .. seealso::

            :func:`StmZip`, the special case where ``f`` is simply ``x => y => (x, y)``

Sliding Windows
---------------

.. only:: not handshake

    .. function:: StmSlide(s: Stm[T, n], w: I, head: T = undefined): Stm[Vec[T, w], n-w+1] :: I is an unsigned integer type

        Produces a stream of sliding windows over stream ``s``.
        Each window has size ``w``.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmSlide.repl.txt

Aggregation
-----------

.. only:: not handshake

    .. function:: StmAll(s: Stm[bool, n]): Stm[bool, 1]

        Returns ``true`` if all elements in the :ref:`logical part <physical-logical-stream>` of stream ``s`` are ``true``.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmAll.repl.txt

        When applied to an empty stream, :func:`StmAll` returns ``true``.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmAll_empty.repl.txt

        .. seealso::

            :func:`StmFold`, for aggregation with an arbitrary function.

.. only:: not handshake

    .. function:: StmAny(s: Stm[bool, n]): Stm[bool, 1]

        Returns ``true`` if any elements in the :ref:`logical part <physical-logical-stream>` of stream ``s`` are ``true``.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmAny.repl.txt

        When applied to an empty stream, :func:`StmAny` returns ``false``.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmAny_empty.repl.txt

        .. seealso::

            :func:`StmFold`, for aggregation with an arbitrary function.

.. only:: not handshake

    .. function:: StmSum(s: Stm[I, n]): Stm[I, 1] :: I is an integer type

        Returns the sum of the elements in the :ref:`logical part <physical-logical-stream>` of stream ``s``.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmSum.repl.txt

        When applied to an empty stream, :func:`StmSum` returns 0.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmSum_empty.repl.txt

        .. WARNING::
            Beware of `overflow <https://en.wikipedia.org/wiki/Integer_overflow>`!
            The sum is performed with the same type as the inputs.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmSum_overflow.repl.txt

        .. seealso::

            :func:`StmFold`, for aggregation with an arbitrary function.

.. only:: not handshake

    .. function:: StmFold(s: Stm[A, n], z: B, f: (B, A) -> B): Stm[B, 1]


        Combines the elements in the :ref:`logical part <physical-logical-stream>` of stream ``s`` to a single element using function ``f`` and initial value ``z``.

        For example, for a 3-element stream ``[x1, x2, x3]s``, the result will be ``[ f(f(f(z, x1), x2), x3) ]s``

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmFold.repl.txt

        Unlike with :func:`StmReduce`, the input stream can be empty.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmFold_empty.repl.txt

        .. seealso::

            :func:`StmAll`, which is a special case of :func:`StmFold` with logical AND

            :func:`StmAny`, which is a special case of :func:`StmFold` with logical OR

            :func:`StmSum`, which is a special case of :func:`StmFold` with addition

            :func:`StmReduce`, for aggregation of non-empty streams without needing to specify an initial value

.. only:: not handshake

    .. function:: StmReduce(s: Stm[T, n], f: (T, T)): Stm[T, 1]

        Combines the elements in the :ref:`logical part <physical-logical-stream>` of stream ``s`` to a single element using function ``f``.

        For example, for a 3-element stream ``[x1, x2, x3]s``, the result will be ``[ f(f(x1, x2), x3) ]s``

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmReduce.repl.txt

        .. WARNING::
            The input stream must be non-empty.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmReduce_empty.repl.txt

        .. seealso::

            :func:`StmFold`, for aggregation of possibly empty streams with a given initial value

Nested Streams
--------------

.. only:: not handshake

    .. function:: StmJoin(s: Stm[Stm[T, m], n]): Stm[T, n*m]

        Removes the outermost level of nesting from the given stream.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmJoin.repl.txt

        .. NOTE::
            The REPL prints 1-dimensional streams in each case due to :ref:`stream flattening <stream-flattening>`.

        .. seealso::

            :func:`StmSplit`, for converting a flat stream back to a nested stream

.. only:: not handshake

    .. function:: StmSplit(s: Stm[T, n], m: I): Stm[Stm[T, m], n/m] :: I is an integer type

        Increases the level of nesting in the given stream.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmSplit.repl.txt

        .. NOTE::
            The REPL prints 1-dimensional streams in each case due to :ref:`stream flattening <stream-flattening>`.

        .. WARNING::
            If ``m`` does not divide ``n``, the output stream will be shorter than the input stream.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmSplit_not_divisible.repl.txt

        .. seealso::

            :func:`StmJoin`, for converting a nested stream back to a flat stream

Discarding Parts of a Stream
----------------------------

.. only:: not handshake

    .. function:: StmDrop(s: Stm[T, n], k: I, head: ~T = undefined): Stm[T, n-k] :: I is an integer type

        Discards the first ``k`` elements from the :ref:`logical part <physical-logical-stream>` of stream ``s``.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmDrop.repl.txt

        .. WARNING::
            It is an error to drop more than the available number of elements.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmDrop_too_many.repl.txt

        .. seealso::

            :func:`StmTakeRight`, if it is easier to say how many elements to *keep* than to say how many to *discard*

            :func:`StmDropRight`, for discarding elements from the end of a stream

            :func:`StmTake`, for discarding elements from the end of a stream

.. only:: not handshake

    .. function:: StmDropRight(s: Stm[T, n], k: I, head: ~T = undefined): Stm[T, n-k] :: I is an integer type

        .. versionadded:: 2.4.0

        Discards the last ``k`` elements from the :ref:`logical part <physical-logical-stream>` of stream ``s``.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmDropRight.repl.txt

        .. WARNING::
            It is an error to drop more than the available number of elements.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmDropRight_too_many.repl.txt

        .. seealso::

            :func:`StmTake`, if it is easier to say how many elements to *keep* than to say how many to *discard*

            :func:`StmDrop`, for discarding elements from the beginning of a stream

            :func:`StmTakeRight`, for discarding elements from the beginning of a stream

.. only:: not handshake

    .. function:: StmTake(s: Stm[T, n], k: I, head: ~T = undefined): Stm[T, k] :: I is an integer type

        .. versionchanged:: 2.4.0
            Added the ``head`` parameter.

        Returns the first ``k`` elements from the :ref:`logical part <physical-logical-stream>` of stream ``s``, discarding the rest.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmTake.repl.txt

        .. WARNING::
            If ``k`` is greater than ``n``, the extra elements will be undefined.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmTake_too_many.repl.txt

        .. seealso::

            :func:`StmDropRight`, if it is easier to say how many elements to *discard* than to say how many to *keep*

            :func:`StmTakeRight`, for discarding elements from the beginning of a stream

            :func:`StmDrop`, for discarding elements from the beginning of a stream

.. only:: not handshake

    .. function:: StmTakeRight(s: Stm[T, n], k: I, head: ~T = undefined): Stm[T, k] :: I is an integer type

        .. versionadded:: 2.4.0

        Returns the last ``k`` elements from the :ref:`logical part <physical-logical-stream>` of stream ``s``, discarding the rest.

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmTakeRight.repl.txt

        .. WARNING::
            If ``k`` is greater than the total length of the stream including the physical prefix, it is an error.

            If ``k`` is greater than the length of the logical part but less than or equal to the total length, you may be able to get away with it.
            However, the compiler makes no effort to preserve the length of the physical prefix.
            Ideally, avoid taking more than the length of the logical stream.
            If you absolutely, positively, definitely need to, then disable optimizations that may shrink the physical prefix (e.g., fusion, shift register shrinking).

        .. literalinclude:: /code-examples/reference/streams/nohandshake/StmTakeRight_too_many.repl.txt

        .. seealso::

            :func:`StmDrop`, if it is easier to say how many elements to *discard* than to say how many to *keep*

            :func:`StmTake`, for discarding elements from the end of a stream

            :func:`StmDropRight`, for discarding elements from the end of a stream

Using Dedicated DSP Blocks
--------------------------

FPGAs generally have dedicated digital signal processing (DSP) blocks to perform multiplication and accumulation.
The following stream operators are helpful for taking advantage of this functionality, especially on `Agilex FPGAs <https://docs.altera.com/r/docs/683037/24.3.1/agilextm-7-variable-precision-dsp-blocks-user-guide/agilextm-7-variable-precision-dsp-blocks-overview)>`__.

.. function:: StmMapDot:[T = u44](s1: Stm[Vec[I, m], n], s2: Stm[Vec[J, m], n], delay: K, cascade: bool = true): Stm[T, n] :: I, J, and K are unsigned integer types; T is an integer type
              StmMapDot:[T = i44](s1: Stm[Vec[I, m], n], s2: Stm[Vec[J, m], n], delay: K, cascade: bool = true): Stm[T, n] :: I, J, and T are integer types; K is an unsigned integer type

    .. versionchanged:: 2.4.0
        Added the ``cascade`` parameter to select between :func:`StmMapDotCascaded` and :func:`StmMapDotTree`.

    .. versionchanged:: 2.4.0
        Added the type parameter ``T`` to control the type of the output stream.

    Computes the dot product of each vector in ``s1`` with the corresponding vector in ``s2``.

    ``delay`` is the number of internal registers to enable in the DSPs.
    Increasing this number will increase the design's latency and increase its maximum clock frequency.
    It may also affect the design's resource usage, depending on the context.

    ``cascade`` selects between two implementations:

    * :func:`StmMapDotCascaded`, which maps to the ``m18x18_systolic`` mode for the Agilex DSPs;
    * :func:`StmMapDotTree`, which maps to the ``m18x18_sumof2`` mode for the Agilex DSPs followed by an adder tree.

    .. literalinclude:: /code-examples/reference/streams/nohandshake/StmMapDot.repl.txt

    .. WARNING::
        It must be possible to losslessly convert the input integers to the target type.

    .. literalinclude:: /code-examples/reference/streams/nohandshake/StmMapDot_invalid_type.repl.txt

    .. seealso::

        :func:`StmMapDotTree`, for the ``m18x18_sumof2`` case

        :func:`StmMapDotCascaded`, for the ``m18x18_systolic`` case

.. function:: StmMapDotTree:[T = u44](s1: Stm[Vec[I, m], n], s2: Stm[Vec[J, m], n], delay: K): Stm[T, n] :: I, J, and K are unsigned integer types; T is an integer type
              StmMapDotTree:[T = i44](s1: Stm[Vec[I, m], n], s2: Stm[Vec[J, m], n], delay: K): Stm[T, n] :: I, J, and T are integer types; K is an unsigned integer type

    .. versionadded:: 2.4.0

    Computes the dot product of each vector in ``s1`` with the corresponding vector in ``s2``.

    ``delay`` is the number of internal registers to enable in the DSPs.
    Increasing this number will increase the design's latency and increase its maximum clock frequency.
    It may also affect the design's resource usage, depending on the context.

    :func:`StmMapDotTree` is implemented so that it maps to the ``m18x18_sumof2`` mode for the Agilex DSPs followed by an adder tree.

    .. literalinclude:: /code-examples/reference/streams/nohandshake/StmMapDotTree.repl.txt

    .. WARNING::
        It must be possible to losslessly convert the input integers to the target type.

    .. literalinclude:: /code-examples/reference/streams/nohandshake/StmMapDotTree_invalid_type.repl.txt

    .. seealso::
        :func:`StmMapDot`, which supports other DSP modes

.. function:: StmMapDotCascaded:[T = u44](s1: Stm[Vec[I, m], n], s2: Stm[Vec[J, m], n], delay: K): Stm[T, n+1-m] :: I, J, and K are unsigned integer types; T is an integer type
              StmMapDotCascaded:[T = i44](s1: Stm[Vec[I, m], n], s2: Stm[Vec[J, m], n], delay: K): Stm[T, n+1-m] :: I, J, and T are integer types; K is an unsigned integer type

    .. versionchanged:: 2.4.0
        Added the type parameter ``T`` to control the type of the output stream.

    Computes the dot product of each vector in ``s1`` with the corresponding vector in ``s2``.
    ``s1`` and ``s2`` must be cascaded, like the output of :func:`StmCascade`.

    ``delay`` is the number of internal registers to enable in the DSPs.
    Increasing this number will increase the design's latency and increase its maximum clock frequency.
    It may also affect the design's resource usage, depending on the context.

    :func:`StmMapDotCascaded` is implemented so that it maps to the ``m18x18_systolic`` mode for the Agilex DSPs.
    In this mode, each input to the DSPs must be delayed by one clock cycle relative to the previous input.

    .. literalinclude:: /code-examples/reference/streams/nohandshake/StmMapDotCascaded.repl.txt

    :func:`StmMapDot` (with ``cascade = true``) is equivalent to :func:`StmCascade` followed by :func:`StmMapDotCascaded`.
    It is therefore easier to understand the inputs and outputs of :func:`StmMapDot`.
    However, in some cases you may be able to find a more resource-efficient way of creating the input cascade.
    That is why :func:`StmMapDotCascaded` is sometimes useful.

    .. literalinclude:: /code-examples/reference/streams/nohandshake/cascade_without_StmCascade.repl.txt

    .. WARNING::
        It must be possible to losslessly convert the input integers to the target type.

    .. literalinclude:: /code-examples/reference/streams/nohandshake/StmMapDotCascaded_invalid_type.repl.txt

    .. seealso::
        :func:`StmMapDot`, which is more user-friendly, but may require more registers

.. function:: StmCascade(s: Stm[Vec[T, m], n]): Stm[Vec[T, m], n+m-1]

    Delays each element of the vector by 1 cycle relative to the previous element.

    .. literalinclude:: /code-examples/reference/streams/nohandshake/StmCascade.repl.txt
