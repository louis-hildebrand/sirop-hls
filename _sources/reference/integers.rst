Integers
########

Sirop supports arbitrary-bitwidth integers, both signed and unsigned.

Unsigned Integers
-----------------

Unsigned integer types are written as a ``u`` followed by a natural number.
For example:

- ``u0`` is the type of a zero-bit unsigned integer. (The only value of this type is 0.)
- ``u5`` is the type of a five-bit unsigned integer.
- ``u32`` is the type of a 32-bit unsigned integer.

Notice that the bitwidth does not need to be a power of two.

An unsigned integer with :math:`k` bits can represent values in the range :math:`[0, 2^k - 1]`.

.. literalinclude:: /code-examples/reference/integers/unsigned_range.repl.txt

Signed Integers
---------------

Signed integer types are written as an ``i`` followed by a natural number.
For example:

- ``i5`` is the type of a five-bit signed integer.
- ``i32`` is the type of a 32-bit signed integer.

The bitwidth must be at least 1.
In other words, the smallest possible signed integer type is ``i1``.
Notice, however, that the bitwidth does not need to be a power of two.

Signed integers are represented in `two's complement <https://en.wikipedia.org/wiki/Two%27s_complement>`__.
A signed integer with :math:`k` bits can therefore represent values in the range :math:`[-2^{k-1}, 2^{k-1} - 1]`.

.. literalinclude:: /code-examples/reference/integers/signed_range.repl.txt

Integer Literals
----------------

Integer literals are typically written with a type annotation to show their bitwidth and signedness.
For example, ``42:u8`` denotes the number 42, represented as an 8-bit unsigned integer.
``42:i16`` denotes the same abstract value, but implemented as a 16-bit signed integer.

.. literalinclude:: /code-examples/reference/integers/bits_of_42.repl.txt

.. seealso::

    :func:`bits`, which converts a value to its binary representation

If no explicit type is specified, the narrowest type that can contain the given value is chosen.

.. literalinclude:: /code-examples/reference/integers/no_type_annotation.repl.txt

Built-In Integer Operations
---------------------------

The following integer-related operators are built into the Sirop language.

.. seealso::

    :ref:`Relational operators <relational-operators>`, which accept integers as input

Converting Between Integer Types
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. function:: pad{w}(x: u{k}): u{w} :: w, k are natural numbers (possibly zero) and w >= k
              pad{w}(x: u{k}): u{w} :: w, k are natural numbers (possibly zero) and w >= k

    Increases the bitwidth of an integer while preserving its value.
    For signed integers, this involves `sign extension <https://en.wikipedia.org/wiki/Sign_extension>`__.

    .. literalinclude:: /code-examples/reference/integers/pad.repl.txt

    .. WARNING::

        The target bitwidth must be greater than or equal to the original bitwidth.

    .. literalinclude:: /code-examples/reference/integers/pad_too_small.repl.txt

    .. seealso::

        :func:`truncate`, for decreasing the bitwidth of an integer

.. function:: truncate{w}(x: u{k}): u{w} :: w, k are natural numbers (possibly zero) and w <= k
              truncate{w}(x: i{k}): i{w} :: w, k are natural numbers (possibly zero) and w <= k

    Decreases the bitwidth of an integer.

    .. literalinclude:: /code-examples/reference/integers/truncate.repl.txt

    .. WARNING::

        If the value does not fit in the new bitwidth, the output is ``undefined``.

    .. literalinclude:: /code-examples/reference/integers/truncate_value_too_big.repl.txt

    .. WARNING::

        The target bitwidth must be less than or equal to the original bitwidth.

    .. literalinclude:: /code-examples/reference/integers/truncate_bitwidth_too_big.repl.txt

    .. seealso::

        :func:`pad`, for increasing the bitwidth of an integer

.. function:: sign(x: u{w}): i{w+1} :: w is a natural number (possibly zero)

    Converts an unsigned integer to a signed integer while preserving its value.

    .. literalinclude:: /code-examples/reference/integers/signed.repl.txt

    .. seealso::

        :func:`unsign`, for converting a non-negative signed integer back to an unsigned integer

.. function:: unsign(x: i{w+1}): i{w} :: w is a natural number (possibly zero)

    Converts a non-negative signed integer to an unsigned integer.

    .. literalinclude:: /code-examples/reference/integers/unsigned.repl.txt

    .. WARNING::

        If the input is negative, the output is ``undefined``.

    .. literalinclude:: /code-examples/reference/integers/unsigned_negative.repl.txt

    .. seealso::

        :func:`sign`, for converting an unsigned integer back to a signed integer

Arithmetic Operators
^^^^^^^^^^^^^^^^^^^^

Addition
""""""""

Sirop provides a few addition operators that differ in their overflow behaviour.

* ``+``: overflow produces ``undefined``
* ``+%``: the value wraps around on overflow
* ``+^``: the return type is wide enough that overflow is impossible

.. literalinclude:: /code-examples/reference/integers/addition.repl.txt

Subtraction
"""""""""""

Sirop provides a few subtraction operators that differ in their overflow behaviour.

* ``-``: overflow produces ``undefined``
* ``-%``: the value wraps around on overflow
* ``-^``: the return type is wide enough that overflow is impossible

.. literalinclude:: /code-examples/reference/integers/subtraction.repl.txt

Multiplication
""""""""""""""

Sirop provides a few multiplication operators that differ in their overflow behaviour.

* ``*``: overflow produces ``undefined``
* ``*%``: the value wraps around on overflow
* ``*^``: the return type is wide enough that overflow is impossible

.. literalinclude:: /code-examples/reference/integers/multiplication.repl.txt

Bitwise Operators
^^^^^^^^^^^^^^^^^

``<<`` performs a bitwise left shift.

.. literalinclude:: /code-examples/reference/integers/bit_shift_left.repl.txt

``>>`` performs an arithmetic right shift.

.. literalinclude:: /code-examples/reference/integers/bit_shift_right_arithmetic.repl.txt

``>>>`` performs a logical right shift.

.. literalinclude:: /code-examples/reference/integers/bit_shift_right_logical.repl.txt

``&`` performs bitwise AND.

.. literalinclude:: /code-examples/reference/integers/bitwise_and.repl.txt

``|`` performs bitwise OR.

.. literalinclude:: /code-examples/reference/integers/bitwise_or.repl.txt

``~`` performs bitwise NOT.

.. literalinclude:: /code-examples/reference/integers/bitwise_not.repl.txt
