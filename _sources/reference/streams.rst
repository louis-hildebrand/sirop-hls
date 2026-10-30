Streams
#######

As described in :doc:`/getting-started/language-intro`, a stream is an inherently sequential collection.
Each element in a stream is provided at a different clock cycle.
It is not possible to read a stream out of order (skipping elements, rewinding, etc.).

.. _physical-logical-stream:

Parts of a Stream
-----------------

Sirop streams have two parts: a "physical prefix" and a "logical part."
To see why, consider the following `timing diagram <https://en.wikipedia.org/wiki/Digital_timing_diagram>`__:

.. image:: /figures/dark/physical-logical-stream.*
    :class: only-dark
    :scale: 250%
.. image:: /figures/light/physical-logical-stream.*
    :class: only-light
    :scale: 250%

What *logical* data does this stream carry?
There is more than one possible interpretation:

* If there is no latency, the data we care about is ``[1, 2, 3, 4, 5, ...]``.
* If the latency is 1 clock cycle, the data we care about is ``[2, 3, 4, 5, ...]``. The preceding values (``[..., 1]``) should be ignored.
* If the latency is 2 clock cycles, the data we care about is ``[3, 4, 5, ...]``. The preceding values (``[..., 1, 2]``) should be ignored.
* etc.

In Sirop, these interpretations are expressed as follows:

* 0-cycle latency: ``[]s ++ [1, 2, 3, 4, 5]s`` (or, more commonly, just ``[1, 2, 3, 4, 5]s``)
* 1-cycle latency: ``[1]s ++ [2, 3, 4, 5]s``
* 2-cycle latency: ``[1, 2]s ++ [3, 4, 5]s``
* etc.

The part before the ``++`` is the "physical prefix."
We will see these outputs if we observe a design running on a physical FPGA, but they are not part of the logical output we care about.
The part after the ``++`` is the "logical part."

Controlling the Physical Prefix
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Why even write down the physical prefix if we don't care about it?
The answer is that, in some cases, we care about *some properties* of the physical prefix.
For example, we might want the generated VHDL entity to produce a ``valid`` output bit.
In the physical prefix, the ``valid`` bit must always be ``false``.
In the logical part, the ``valid`` bit may be ``true`` or ``false``.

.. literalinclude:: /code-examples/reference/square_with_valid_o.sirop

Many of the built-in functions listed provide some way of controlling the physical prefix.
For instance, as shown in the sample code above, :func:`StmMap` has a ``head`` parameter to set the initial value of the output register.

..
    TODO: link to a page explaining the REPL?

In the REPL, the physical prefix is hidden by default.
If it's important to see the physical prefix, enable it by setting the special variable ``__show_prefix``.

.. literalinclude:: /code-examples/reference/show_prefix.repl.txt

Learn More
----------

.. toctree::
    :maxdepth: 1

    streams/delay-annotations.rst
    streams/flattening.rst
    streams/builtins.rst
