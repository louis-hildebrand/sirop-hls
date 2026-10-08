Example with Static Scheduling: FIR Filter
==========================================

Now consider the following example, which implements an `FIR filter <https://en.wikipedia.org/wiki/Finite_impulse_response>`__ without backpressure.

.. literalinclude:: /code-examples/fir.sirop
    :linenos:

Test, compile, and simulate the design using the following command.

.. NOTE::
    To change the compilation target, use the ``--out:vhdl:family`` and ``--out:vhdl:device`` flags.
    Run ``sirop --help`` for more details.

::

    $ sirop -i fir.sirop --out:test --out:vhdl vhdl_project_dir/ --out:vhdl:run-sim
    [INFO ] the design has a latency of 5 cycles
    [INFO ] test 0: PASSED
    [INFO ] 1/1 test passed!
    [INFO ] VHDL testbench passed!
