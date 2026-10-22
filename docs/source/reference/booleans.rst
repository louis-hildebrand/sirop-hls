Booleans
########

Sirop has booleans and boolean operators like most other programming languages.

.. _logical-operators:

Logical Operators
-----------------

``&&`` performs logical AND.

.. literalinclude:: /code-examples/reference/booleans/logical_and.repl.txt

``||`` performs logical OR.

.. literalinclude:: /code-examples/reference/booleans/logical_or.repl.txt

``!`` performs logical NOT.

.. literalinclude:: /code-examples/reference/booleans/logical_not.repl.txt

Unlike other operators, logical operators can sometimes produce a concrete value even with one of the operands being ``undefined``.
This makes them a bit like the `short-circuiting <https://en.wikipedia.org/wiki/Short-circuit_evaluation>`__ logical operators in other languages.
However, unlike in other languages, the order does not matter.
(In hardware, both branches are evaluated simultaneously.)

.. literalinclude:: /code-examples/reference/booleans/logical_ops_undefined.repl.txt

.. _relational-operators:

Relational Operators
--------------------

``==`` evaluates to ``true`` if and only if its operands have the same value.
``!=`` is its inverse.
``<``, ``<=``, ``>``, and ``>=`` have their usual mathematical meaning.

.. literalinclude:: /code-examples/reference/booleans/relational.repl.txt
