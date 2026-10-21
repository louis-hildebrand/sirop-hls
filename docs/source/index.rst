Sirop documentation
===================

Sirop is a language and compiler for generating streaming accelerators.
Similarly to projects like `HLS4ML <https://fastmachinelearning.org/hls4ml/intro/introduction.html>`__ and `Altera HLS IP Gen <https://www.altera.com/products/software-tools/hls-ip-gen-compiler>`__, the goal is to convert high-level code into `VHDL <https://en.wikipedia.org/wiki/VHDL>`__ that can be synthesized and run on an `FPGA <https://en.wikipedia.org/wiki/Field-programmable_gate_array>`__.

.. image:: figures/dark/workflow.*
    :width: 100%
    :class: only-dark
.. image:: figures/light/workflow.*
    :width: 100%
    :class: only-light

Programs are written in a functional style using "parallel patterns."
For example, a `dot product <https://en.wikipedia.org/wiki/Dot_product>`__ can be expressed as follows:

.. literalinclude:: code-examples/dot.repl.txt

.. toctree::
    :maxdepth: 1
    :caption: Getting Started

    getting-started/install
    getting-started/language-intro
    getting-started/related-projects
    getting-started/example-dot
    getting-started/example-fir

.. toctree::
    :maxdepth: 1
    :caption: Reference

    reference/types
    reference/booleans
    reference/integers
    reference/functions
    reference/vectors
    reference/streams
    reference/binary
