# Overview

Sirop is built on the idea of processing _vectors_ and _streams_ of data.
A _vector_ is a sequence whose elements can all be accessed at once.
A _stream_ is a sequence whose elements can only be accessed one at a time, with no way of accessing previous values or skipping upcoming values.
In software terms, a stream is a bit like an iterator.
In hardware terms, a stream is an inherently sequential sequence which produces at most one element per clock cycle.
Streams encode pipeline parallelism.
Vectors, by contrast, can be used in combinational circuits.
They encode spatial parallelism.

Programs transform vectors and streams using "parallel patterns" from the functional programming paradigm.
For example, many languages have a higher-order function called `map` that applies a function to each element of a collection.

```
> // JavaScript
> [1, 2, 3, 4].map(x => x + 5)
[ 6, 7, 8, 9 ]
```

In Sirop, you can transform each element of a vector using `VecMap`.
This will result in four adders being instantiated to process all the vector's elements in parallel.

```sirop
> [1:u8, 2:u8, 3:u8, 4:u8]v.VecMap(x => x + 5)
[6:u8, 7:u8, 8:u8, 9:u8]v
```

```{image} /figures/dark/spatial-parallelism.*
:alt: Diagram showing `VecMap`
:class: only-dark
```
```{image} /figures/light/spatial-parallelism.*
:alt: Diagram showing `VecMap`
:class: only-light
```

Similarly, you can transform each element of a stream using `StmMap`.
In this case, only one adder will be needed because the stream yields just one element per clock cycle.

```sirop
> [1:u8, 2:u8, 3:u8, 4:u8]s.StmMap(x => x + 5)
[6:u8, 7:u8, 8:u8, 9:u8]s
```

```{image} /figures/dark/pipeline-parallelism.*
:alt: Diagram showing `StmMap`
:class: only-dark
```
```{image} /figures/light/pipeline-parallelism.*
:alt: Diagram showing `StmMap`
:class: only-light
```

It is also possible to partially parallelize this code by representing the input as a stream of vectors.
Here, the stream will yield two elements per cycle and there will be two adders to process them.

```sirop
> [[1:u8, 2:u8]v, [3:u8, 4:u8]v]s.StmMap(v => v.VecMap(x => x + 5))
[[6:u8, 7:u8]v, [8:u8, 9:u8]v]s
```

```{image} /figures/dark/mixed-parallelism.*
:alt: Diagram showing `VecMap` inside `StmMap`
:class: only-dark
```
```{image} /figures/light/mixed-parallelism.*
:alt: Diagram showing `VecMap` inside `StmMap`
:class: only-light
```

This idea of using types to represent the level of spatial parallelism appears in prior works, including [Lift-HLS](https://doi.org/10.1145/3315454.3329957), [Aetherling](https://doi.org/10.1145/3385412.3385983), and [SHIR](https://doi.org/10.1145/3501768).

## Dynamic and Static Scheduling

The Sirop compiler supports two "scheduling" modes.
By default, each pipeline stage is connected to the following stage by a latency-insensitive _handshake protocol_.
The data flowing from producer to consumer is accompanied by a `valid` bit that is high whenever the data is valid.
The consumer sends back a `ready` bit that is high whenever it is ready to receive the data.
If `ready` is low, the producer should hold its current output and not move to the next element in the stream.

```{image} /figures/dark/handshake.*
:alt: Block diagram explaining the handshake protocol
:class: only-dark
```
```{image} /figures/light/handshake.*
:alt: Block diagram explaining the handshake protocol
:class: only-light
```

```{image} /figures/dark/handshake-timing.*
:class: Timing diagram explaining the handshake protocol
:class: only-dark
```
```{image} /figures/light/handshake-timing.*
:class: Timing diagram explaining the handshake protocol
:class: only-light
```

The handshake protocol can be disabled as shown below, in the FIR filter example.
In this case, there is no way for a consumer to exert backpressure.
It is still possible to have a `valid` bit in the stream payload itself; see the FIR filter example.

```{image} /figures/dark/no-handshake.*
:alt: Block diagram explaining static scheduling
:class: only-dark
```
```{image} /figures/light/no-handshake.*
:alt: Block diagram explaining static scheduling
:class: only-light
```

```{image} /figures/dark/no-handshake-timing.*
:alt: Timing diagram explaining static scheduling
:class: only-dark
```
```{image} /figures/light/no-handshake-timing.*
:alt: Timing diagram explaining static scheduling
:class: only-light
```
