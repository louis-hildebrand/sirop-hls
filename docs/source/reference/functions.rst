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

If the function's input has type ``A`` and its output has type ``B``, then the type of the function is ``A -> B``.
This can be repeated; ``A -> B -> C`` is interpreted as ``A -> (B -> C)``, i.e., a function that takes an input of type ``A`` and returns a function of type ``B -> C``.
See `Currying <https://en.wikipedia.org/wiki/Currying>`__.
In practice, the Sirop compiler will try to eliminate such functions (except the top-level function).
It does so using the following approaches:

* inlining (e.g., replacing ``(x => y => x + y)(a)(b)`` by ``a + b``)
* "uncurrying" (e.g., replacing ``x => y => x + y`` by ``x => x.0 + x.1``)

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

Calling a Function
------------------

Functions can be called in one of two ways: "function-style" notation and "method-style" notation.

For example, suppose ``f`` is a function that takes as input a 2-tuple.
Then we can pass arguments ``x`` and ``y`` to ``f`` as follows:

#. ``f(x, y)`` (function-style)
#. ``x.f(y)`` (method-style)

These two notations are exactly equivalent.

.. NOTE::

    Unlike in many object-oriented programming languages, method-style notation in Sirop does not come with any overhead (e.g., `dynamic dispatch <https://en.wikipedia.org/wiki/Dynamic_dispatch>`__).

.. literalinclude:: /code-examples/reference/function_calls.repl.txt

The same syntax can be used for stream and vector operators, like :func:`StmMap` and :func:`VecMap`.
Method-style notation is especially convenient for expressing long sequences of stream transformations, since the stages of computation are laid out left-to-right (or top-to-bottom, if there are line breaks) rather than inside-out.

.. literalinclude:: /code-examples/reference/function_calls_dot.repl.txt

Omitting the Input Type Annotation
----------------------------------

In some cases, the Sirop compiler can infer the input type for a function from the context.
In these cases, the type annotation can be omitted from the source code.
For example:

.. literalinclude:: /code-examples/reference/functions_without_type_annotation_basic.repl.txt

This also works for "pattern functions" (those that unpack their input):

.. literalinclude:: /code-examples/reference/functions_without_type_annotation_pattern.repl.txt
