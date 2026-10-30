Creating a Vector
#################

Vector Literals
---------------

Vector literals are written by placing the elements between square brackets followed by a ``v``.
For example, ``[1:u8, 2:u8, 3:u8]v`` is a vector containing elements 1, 2, and 3---each with type ``u8``.

Building an Arbitrary Vector
----------------------------

An arbitrary vector of length ``n`` can be created using the expression ``vbuild(n) { (i: T) => /* expression for value at index i */ }``.
For example:

.. literalinclude:: /code-examples/reference/vectors/vbuild.repl.txt

.. WARNING::
    The index ``i`` must be wide enough to represent all the values in the range ``[0, n - 1]``.

.. literalinclude:: /code-examples/reference/vectors/vbuild_index_too_small.repl.txt
