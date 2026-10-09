Functions
#########

In their most basic form, Sirop functions look like ``(input: Type) => output``.
For example, the following function takes as input ``x``, an unsigned 8-bit integer, and returns ``x + 5``:

.. code::

    (x: u8) => x + 5


The functions themselves are anonymous, but they can be bound to a name using ``let``:

.. literalinclude:: /code-examples/reference/let_function.repl.txt

The example above creates a vector of three integers.
A straightforward translation to VHDL would therefore instantiate three copies of ``f``, i.e., three adders.

.. _pattern-functions:

Unpacking the Input
-------------------

For functions that take as input a tuple, it is often convenient to name each part of the input.
For example, the following function:

.. code::

    (x: (u8, u8)) => x.0 + x.1

can also be written as

.. code::

    @(x: u8, y: u8) => x + y

Omitting the Input Type Annotation
----------------------------------

In some cases, the Sirop compiler can infer the input type for a function from the context.
In these cases, the type annotation can be omitted from the source code.
For example:

.. literalinclude:: /code-examples/reference/functions_without_type_annotation_basic.repl.txt

This also works for "pattern functions" (those that unpack their input):

.. literalinclude:: /code-examples/reference/functions_without_type_annotation_pattern.repl.txt
