Input Stream Delay Annotations
##############################

.. versionadded:: 2.3.0

Sirop supports "delay annotations" that express the relative arrival times of the inputs to the VHDL entity.

First, consider the following simple program *without* delay annotations.
Given streams ``a = [a0, a1, a2, ...]s`` and ``b = [b0, b1, b2, ...]s``, it returns ``[(a0*a0 + b0), (a1*a1 + b1), (a2*a2 + b2), ...]s``.
Since the input streams do *not* have delay annotations, the Sirop compiler assumes ``a`` and ``b`` will arrive at the same time.

.. literalinclude:: /code-examples/reference/delay_annotations_default.sirop
    :name: delay-annotations-default

The behaviour of this program is illustrated in the following timing diagram.

.. image:: /figures/dark/delay-annotations-default.*
    :class: only-dark
    :scale: 150%
.. image:: /figures/light/delay-annotations-default.*
    :class: only-light
    :scale: 150%

..
    TODO: Explain why this requires a shift register.
    TODO: It would be nice to have some way to demonstrate it more clearly than the pretty-printer.
    TODO: Maybe it would be possible to implement a basic "RTL viewer"-like tool.

However, suppose we want stream ``b`` to arrive one cycle later than stream ``a``.
This can be expressed as follows.

.. literalinclude:: /code-examples/reference/delay_annotations_explicit.sirop
    :name: delay-annotations-explicit

This program is described by the following timing diagram.

.. image:: /figures/dark/delay-annotations-explicit.*
    :class: only-dark
    :scale: 150%
.. image:: /figures/light/delay-annotations-explicit.*
    :class: only-light
    :scale: 150%

.. NOTE::
    Only the *relative* values of the delay annotations matter.
    In the example above, annotating ``a`` with ``@ 100`` and ``b`` with ``@ 101`` would have had the same effect as just annotating ``b`` with ``@ 1``.

Effect on Resource Usage
------------------------

Carefully choosing the delay annotations can lower ALM usage.

.. subfigure::
    :name: delay-annotations-resource-usage

    .. image:: /figures/dark/delay-annotations-resource-usage.*
        :class: only-dark
        :scale: 150%

    .. image:: /figures/light/delay-annotations-resource-usage.*
        :class: only-light
        :scale: 150%

    VHDL generated from the example program with and without delay annotations.


In :ref:`the original program <delay-annotations-default>`, ``a`` passes through one pipeline stage (``StmMap(x => x * x)``) before reaching the adder.
By contrast, ``b`` feeds directly into the adder.
Therefore, the Sirop compiler must add a register to balance the latency (see :ref:`latency matching <latency-matching>`).
This is the blue register on the left-hand side of :numref:`figure {number} <delay-annotations-resource-usage>`.

In :ref:`the program with a delay annotation <delay-annotations-explicit>`, the programmer promises that ``b`` will already be delayed by one clock cycle at the input.
Therefore, the Sirop compiler does not need to add any extra registers.
The result is shown on the right-hand side of :numref:`figure {number} <delay-annotations-resource-usage>`.
