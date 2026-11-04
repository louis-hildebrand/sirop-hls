.. _stream-flattening:

Stream Flattening
#################

The Sirop compiler flattens nested streams.
(There is only one time dimension, after all.)
For example:

.. literalinclude:: /code-examples/reference/stream_flattening.repl.txt

Flattening happens *after* type checking.
Therefore, if downstream functions require a flat stream, you should use :func:`StmJoin` to explicitly flatten.

.. literalinclude:: /code-examples/reference/stream_flattening_error.repl.txt
