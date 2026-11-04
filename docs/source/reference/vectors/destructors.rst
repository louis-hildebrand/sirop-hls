Reading a Vector
################

Simple Vector Accesses
----------------------

The notation ``v[i]`` is used to access the element at index ``i`` in vector ``v``.
The indices in a vector start at 0.
For example:

.. literalinclude:: /code-examples/reference/vectors/vec_access.repl.txt

The index must be an unsigned integer.

.. literalinclude:: /code-examples/reference/vectors/vec_access_negative.repl.txt

Out-of-bounds vector accesses evaluate to ``undefined``.

.. literalinclude:: /code-examples/reference/vectors/vec_access_oob.repl.txt

Vector Slicing
--------------

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
