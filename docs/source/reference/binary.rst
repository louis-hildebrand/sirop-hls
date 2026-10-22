Binary Conversion
#################

Sirop provides built-in functions for converting a value to its binary representation and back.

.. function:: bits(x: T): Vec[bool, n] :: n is the bitwidth for type T

    Produces the binary representation of ``x``.

    .. literalinclude:: /code-examples/reference/binary/bits.repl.txt

    .. seealso::

        :func:`interpret_as`, for converting the binary representation back to some type.

.. function:: interpret_as:[T](x: Vec[bool, n]): T :: T has bitwidth n

    Converts a boolean vector ``x`` to the given type.

    .. literalinclude:: /code-examples/reference/binary/interpret_as.repl.txt

    .. WARNING::

        The target type ``T`` must have bitwidth ``n``.

    .. literalinclude:: /code-examples/reference/binary/interpret_as_wrong_bitwidth.repl.txt

    .. seealso::

        :func:`bits`, for getting the binary representation
