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
