Vectors
#######

As described in :doc:`/getting-started/language-intro`, a vector is a combinational collection.
Elements of a vector can be accessed all at once, in the same clock cycle.

Unlike tuples, vectors are homogeneous; every element in a given vector has the same type.

Constructor and Destructor
--------------------------

Vector literals are written by placing the elements between square brackets followed by a ``v``.
For example, ``[1:u8, 2:u8, 3:u8]v`` is a vector containing elements 1, 2, and 3---each with type ``u8``.

Simple Vector Accesses
^^^^^^^^^^^^^^^^^^^^^^

The notation ``v[i]`` is used to access the element at index ``i`` in vector ``v``.
The indices in a vector start at 0.
For example:

.. literalinclude:: /code-examples/reference/vectors/vec_access.repl.txt

The index must be an unsigned integer.

.. literalinclude:: /code-examples/reference/vectors/vec_access_negative.repl.txt

Out-of-bounds vector accesses evaluate to ``undefined``.

.. literalinclude:: /code-examples/reference/vectors/vec_access_oob.repl.txt

Vector Slicing
^^^^^^^^^^^^^^

The notation ``vec[start:len:step]`` produces a "slice" of vector ``vec``.
``start`` is the first index to read, ``len`` is the number of elements to read, and ``step`` is the difference between consecutive indices.
In other words, ``vec[start:len:step]`` produces ``[ vec[start], vec[start + step], vec[start + 2*step], ..., vec[start + (len - 1)*step] ]v``.
For example:

.. literalinclude:: /code-examples/reference/vectors/vec_slice_all_args.repl.txt

As with simple vector accesses, out-of-bounds elements will be ``undefined``.

.. literalinclude:: /code-examples/reference/vectors/vec_slice_oob.repl.txt

Any of the three values can be omitted.
If the ``step`` is omitted, it defaults to 1.

.. literalinclude:: /code-examples/reference/vectors/vec_slice_missing_step.repl.txt

If the ``start`` is omitted, its default is chosen as follows:

* When ``step < 0``: the default ``start`` is the last index.
* When ``step >= 0``: the default ``start`` is 0.

.. literalinclude:: /code-examples/reference/vectors/vec_slice_missing_start.repl.txt

If the ``len`` is omitted, its value will be chosen to maximize the length of the slice while avoiding out-of-bounds accesses.
In other words, the slice will continue all the way to the end of the vector (or all the way to the start, when ``step < 0``).

.. literalinclude:: /code-examples/reference/vectors/vec_slice_missing_len.repl.txt

.. WARNING::
    The ``len`` cannot be omitted when ``step = 0``.

..
    TODO: specify the behaviour in this case?

Built-In Vector Operators
-------------------------

The following vector-related operators are built into the Sirop language.

.. seealso::

    :ref:`Relational operators <relational-operators>`, which accept vectors as input

Creating Vectors
^^^^^^^^^^^^^^^^

.. function:: VecCst(n: I, k: T): Vec[T, n] :: I is an unsigned integer type

    Creates a vector of length ``n`` whose elements are all the constant ``k``.

    .. literalinclude:: /code-examples/reference/VecCst.repl.txt

.. function:: VecCount(n: I, init: J, delta: J): Vec[J, n] :: I is an unsigned integer type and J is any integer type

    Creates a vector of ``n`` integers starting at ``init`` and increasing by ``delta``.

    .. literalinclude:: /code-examples/reference/VecCount.repl.txt

Transforming Vectors Elementwise
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. function:: VecMap(v: Vec[A, n], f: A -> B): Vec[B, n]

    Applies function ``f`` elementwise to vector ``v``.

    .. literalinclude:: /code-examples/reference/VecMap.repl.txt

Combining Multiple Vectors
^^^^^^^^^^^^^^^^^^^^^^^^^^

.. function:: VecConcat(v1: Vec[T, n], v2: Vec[T, m]): Vec[T, n+m]

    Concatenates two vectors.

    .. literalinclude:: /code-examples/reference/VecConcat.repl.txt

.. function:: VecZip(v1: Vec[A, n], v2: Vec[B, n]): Vec[(A, B), n]

    Pairs up the elements of two vectors.

    .. literalinclude:: /code-examples/reference/VecZip.repl.txt

    .. seealso::

        :func:`VecMap2`, which zips two vectors with an arbitrary function

.. function:: VecMap2(v1: Vec[A, n], v2: Vec[B, n], f: A -> B -> C): Vec[C, n]

    Like :func:`VecMap`, but for two streams at once.

    .. literalinclude:: /code-examples/reference/VecMap2.repl.txt

    .. seealso::

        :func:`VecZip`, the special case where ``f`` is simply ``x => y => (x, y)``

Aggregation
^^^^^^^^^^^

.. function:: VecAll(v: Vec[bool, n]): bool

    Returns ``true`` if all elements of vector ``v`` are ``true``.

    .. literalinclude:: /code-examples/reference/VecAll.repl.txt

    When applied to an empty vector, :func:`VecAll` returns ``true``.

    .. literalinclude:: /code-examples/reference/VecAll_empty.repl.txt

    .. seealso::

        :func:`VecFold`, for aggregation with an arbitrary function

.. function:: VecAny(v: Vec[bool, n]): bool

    Returns ``true`` if any elements of vector ``v`` are ``true``.

    .. literalinclude:: /code-examples/reference/VecAny.repl.txt

    When applied to an empty vector, :func:`VecAny` returns ``false``.

    .. literalinclude:: /code-examples/reference/VecAny_empty.repl.txt

    .. seealso::

        :func:`VecFold`, for aggregation with an arbitrary function

.. function:: VecSum(v: Vec[I, n]): I :: I is an integer type

    Returns the sum of the elements in vector ``v``.

    .. literalinclude:: /code-examples/reference/VecSum.repl.txt

    When applied to an empty vector, :func:`VecSum` returns 0.

    .. literalinclude:: /code-examples/reference/VecSum_empty.repl.txt

    .. WARNING::
        Beware of `overflow <https://en.wikipedia.org/wiki/Integer_overflow>`__!
        The sum is performed with the same type as the inputs.

    .. literalinclude:: /code-examples/reference/VecSum_overflow.repl.txt

    .. seealso::

        :func:`VecFold`, for aggregation with an arbitrary function

.. function:: VecFold(v: Vec[A, n], z: B, f: (B, A) -> B): B

    Combines the elements in vector ``v`` to a single value using function ``f`` and initial value ``z``.

    For example, for a 3-element stream ``[a, b, c]v``, the result will be ``f(f(f(z, a), b), c)``.

    .. literalinclude:: /code-examples/reference/VecFold.repl.txt

    Unlike with :func:`VecReduce`, the input vector can be empty.

    .. literalinclude:: /code-examples/reference/VecFold_empty.repl.txt

    .. seealso::

        :func:`VecAll`, the special case of :func:`VecFold` with logical AND

        :func:`VecAny`, the special case of :func:`VecFold` with logical OR

        :func:`VecSum`, the special case of :func:`VecFold` with addition

        :func:`VecReduce`, for aggregation of a non-empty vector without needing to specify an initial value

.. function:: VecReduce(v: Vec[T, n], f: (T, T) -> T): Vec[T, 1]

    Combines the elements in vector ``v`` to a single value using function ``f``.

    For example, for a 3-element vector ``[a, b, c]v``, the result will be ``f(f(a, b), c)``.

    .. literalinclude:: /code-examples/reference/VecReduce.repl.txt

    .. WARNING::
        The input vector must be non-empty.

    .. literalinclude:: /code-examples/reference/VecReduce_empty.repl.txt

    .. seealso::

        :func:`VecFold`, for aggregation of a possibly empty vector with a given initial value

Nested Vectors
^^^^^^^^^^^^^^

.. function:: VecJoin(v: Vec[Vec[T, m], n]): Vec[T, n*m]

    Removes the outermost level of nesting from vector ``v``.

    .. literalinclude:: /code-examples/reference/VecJoin.repl.txt

    .. seealso::

        :func:`VecSplit`, for converting a flat vector back to a nested vector

.. function:: VecSplit(v: Vec[T, n], m: I): Vec[Vec[T, m], n/m]

    Increases the level of nesting of vector ``v``.

    .. literalinclude:: /code-examples/reference/VecSplit.repl.txt

    .. WARNING::
        If ``m`` does not divide ``n``, some elements will be discarded.

    .. literalinclude:: /code-examples/reference/VecSplit_not_divisible.repl.txt

    .. seealso::

        :func:`VecJoin`, for converting a nested vector back to a flat vector

Reordering Vectors
^^^^^^^^^^^^^^^^^^

.. function:: VecShiftLeft(v: Vec[T, n], input: T): Vec[T, n]

    Shifts vector ``v`` left and inserts ``input`` at the end.

    .. literalinclude:: /code-examples/reference/VecShiftLeft.repl.txt

    If ``v`` is empty, ``input`` is unused.

    .. literalinclude:: /code-examples/reference/VecShiftLeft_empty.repl.txt

.. function:: VecReverse(v: Vec[T, n]): Vec[T, n]

    Reverses vector ``v``.

    .. literalinclude:: /code-examples/reference/VecReverse.repl.txt

.. function:: VecTranspose(v: Vec[Vec[T, m], n]): Vec[Vec[T, n], m]

    Transposes vector ``v``.

    .. literalinclude:: /code-examples/reference/VecTranspose.repl.txt

Discarding Parts of a Vector
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. function:: VecDrop(v: Vec[T, n], k: I): Vec[T, n-k] :: I is an integer type

    Discards the first ``k`` elements from vector ``v``.

    .. literalinclude:: /code-examples/reference/VecDrop.repl.txt

    .. WARNING::
        It is an error to drop more than the available number of elements.

    .. literalinclude:: /code-examples/reference/VecDrop_too_many.repl.txt

    .. seealso::

        :func:`VecTakeRight`, if it is easier to say how many elements to *keep* than to say how many to *discard*

        :func:`VecDropRight`, for discarding elements from the end of a vector

        :func:`VecTake`, for discarding elements from the end of a vector

.. function:: VecDropRight(v: Vec[T, n], k: I): Vec[T, n-k] :: I is an unsigned integer type

    Discards the last ``k`` elements from vector ``v``.

    .. literalinclude:: /code-examples/reference/VecDropRight.repl.txt

    .. WARNING::
        It is an error to drop more than the available number of elements.

    .. literalinclude:: /code-examples/reference/VecDropRight_too_many.repl.txt

    .. seealso::

        :func:`VecTake`, if it is easier to say how many elements to *keep* than to say how many to *discard*

        :func:`VecDrop`, for discarding elements from the beginning of a vector

        :func:`VecTakeRight`, for discarding elements from the beginning of a vector

.. function:: VecTake(v: Vec[T, n], k: I): Vec[T, k] :: I is an unsigned integer type

    Returns the first ``k`` elements of vector ``v``, discarding the rest.

    .. literalinclude:: /code-examples/reference/VecTake.repl.txt

    .. WARNING::
        If ``k`` is greater than ``n``, the extra elements will be undefined.

    .. literalinclude:: /code-examples/reference/VecTake_too_many.repl.txt

    .. seealso::

        :func:`VecDropRight`, if it is easier to say how many elements to *discard* than to say how many to *keep*

        :func:`VecTakeRight`, for discarding elements from the beginning of a vector

        :func:`VecDrop`, for discarding elements from the beginning of a vector

.. function:: VecTakeRight(v: Vec[T, n], k: I): Vec[T, k] :: I is an unsigned integer type

    Returns the last ``k`` elements of vector ``v``, discarding the rest.

    .. literalinclude:: /code-examples/reference/VecTakeRight.repl.txt

    .. WARNING::
        If ``k`` is greater than ``n``, the extra elements will be undefined.

    .. literalinclude:: /code-examples/reference/VecTakeRight_too_many.repl.txt

    .. seealso::

        :func:`VecDrop`, if it is easier to say how many elements to *discard* than to say how many to *keep*

        :func:`VecTake`, for discarding elements from the end of a vector

        :func:`VecDropRight`, for discarding elements from the end of a vector
