Types in Sirop
==============

The Sirop programming language supports the following types:

..
    TODO: link to the relevant pages for each type, like for streams

===========================  ==========================================================
Type                             Description
===========================  ==========================================================
``bool``                     :doc:`Boolean </reference/booleans>`
``u0``, ``u1``, ``u2``, ...  Unsigned :doc:`integer </reference/integers>` with the given bitwidth
``i1``, ``i2``, ``i3``, ...  Signed :doc:`integer </reference/integers>` with the given bitwidth
``(A, B, ...)``              :doc:`Tuple </reference/tuples>` whose elements have type ``A``, ``B``, etc.
``A -> B``                   :doc:`Function </reference/functions>` with input of type ``A`` and output of type ``B``
``Vec[T, n]``                :doc:`Vector </reference/vectors>` of length ``n`` whose elements have type ``T``
``Stm[T, n]``                :doc:`Stream </reference/streams>` of length ``n`` whose elements have type ``T``
===========================  ==========================================================

Combinational Data Types
------------------------

In a function signature (e.g., :func:`StmTake`), the notation ``~T`` refers to the "combinational version" of type ``T``.
Intuitively, ``~Stm[T, n]`` is replaced with ``~T``, and this process continues until ``T`` is no longer a stream.
For example:

* ``~bool`` is ``bool``.
* ``~u8`` is ``u8``.
* ``~Stm[i8, N]`` is ``i8``.
* ``~Stm[Stm[(i16, i8), M], N]`` is ``(i16, i8)``.
* ``~(A -> B)`` is not defined.

..
    TODO: also give example of a vector of streams? Would probably first need to explain how types are lowered first.

Consider :func:`StmTake`.
The input stream is expected to have type ``Stm[T, n]`` and the ``head`` parameter is expected to have type ``~T``.

.. literalinclude:: /code-examples/reference/type_tilde.repl.txt
