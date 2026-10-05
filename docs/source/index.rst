Sirop documentation
===================

Sirop is a language and compiler for generating streaming accelerators.
Similarly to projects like `HLS4ML <https://fastmachinelearning.org/hls4ml/intro/introduction.html>`__ and `Altera HLS IP Gen <https://www.altera.com/products/development-tools/hls_ip_gen_compiler>`__, the goal is to convert high-level code into VHDL that can be synthesized and run on an FPGA.

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
    :maxdepth: 2
    :caption: Getting Started

    install
    language-intro
    related-projects
    example-dot
    example-fir

.. toctree::
    :maxdepth: 2
    :caption: Reference
