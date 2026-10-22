Tuples
######

Tuples are collections with the following properties:

* they are heterogeneous, i.e., their elements may have different types;
* their size is fixed at compile time.

For example, ``(u8, bool)`` is the type of a tuple whose first element is an 8-bit unsigned :doc:`integer </reference/integers>` and whose second element is a :doc:`boolean </reference/booleans>`.

Constructing a Tuple
--------------------

Tuples are constructed by simply placing the elements in parentheses.
For example, ``(42:u8, true)`` is a value of type ``(u8, bool)``.

Accessing Elements in a Tuple
-----------------------------

``x.0`` accesses the first element in the tuple, ``x.1`` accesses the second element, ``x.2`` accesses the third element, and so on.

.. literalinclude:: /code-examples/reference/tuple_accesses.repl.txt
