Types in Sirop
==============

The Sirop programming language supports the following types:

..
    TODO: link to the relevant pages for each type, like for streams

===========================  ==========================================================
Type                             Description
===========================  ==========================================================
``bool``                     Boolean
``u0``, ``u1``, ``u2``, ...  Unsigned integer with the given bitwidth
``i1``, ``i2``, ``i3``, ...  Signed integer with the given bitwidth
``(A, B, ...)``              Tuple whose elements have type ``A``, ``B``, etc.
``Vec[T, n]``                :doc:`Vector </reference/vectors>` of length ``n`` whose elements have type ``T``
``Stm[T, n]``                :doc:`Stream </reference/streams>` of length ``n`` whose elements have type ``T``
``A -> B``                   Function with input of type ``A`` and output of type ``B``
===========================  ==========================================================

Note
----

* The minimum bitwidth for unsigned integers is 0. There is only one value of type ``u0``, namely 0.
* The minimum bitwidth for signed integers is 1.
* ``A -> B -> C`` is interpreted as ``A -> (B -> C)``, i.e., a function that takes an input of type ``A`` and returns a function of type ``B -> C``.
  See `Currying <https://en.wikipedia.org/wiki/Currying>`__.
  In practice, the Sirop compiler will ensure no such functions remain in the final program, using one of the following approaches:

  * inlining (e.g., replacing ``(x => y => x + y)(a)(b)`` by ``a + b``)
  * "uncurrying" (e.g., replacing ``x => y => x + y`` by ``x => x.0 + x.1``)
