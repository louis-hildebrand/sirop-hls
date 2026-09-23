# Sirop

Sirop is a language and compiler for generating streaming accelerators.
Similarly to projects like [HLS4ML](https://fastmachinelearning.org/hls4ml/intro/introduction.html) and [Altera HLS IP Gen](https://www.altera.com/products/development-tools/hls_ip_gen_compiler), the goal is to convert high-level code into VHDL that can be synthesized and run on an FPGA.

<img src="./docs/workflow.svg" alt="Flowchart showing the workflow for using Sirop" width="100%" />

### How does Sirop relate to...

#### ... hardware description languages (e.g., [VHDL](https://en.wikipedia.org/wiki/VHDL), [Verilog](https://en.wikipedia.org/wiki/Verilog))?

Traditional hardware description languages give you a lot of control over the final product, but they are quite low-level and verbose.
Sirop is a higher-level language; it lets you express your algorithm much more concisely.

The Sirop compiler can also perform certain optimizations that are not allowed in synthesis tools like Quartus.
For example, the Sirop compiler can insert registers to balance the latency across different paths.
This makes it easier to focus on the high-level computations rather than low-level details like the latency along each path.
The Sirop compiler can also fuse two pipeline stages into one.
This lets the programmer break down their problem into small steps without sacrificing latency or resource-efficiency.
Conversely, the compiler can split a single stage into two to improve the maximum clock frequency.

#### ... C-based HLS (e.g., [Altera HLS IP Gen](https://www.altera.com/products/development-tools/hls_ip_gen_compiler), [Vitis HLS](https://www.amd.com/en/products/software/adaptive-socs-and-fpgas/vitis/vitis-hls.html))?

C and C++ are widely known and their programming model make a lot of sense when working with a CPU.
However, they are a much less natural fit for programming FPGAs.
Sirop is a custom language designed with FPGAs in mind.

_Avoiding code that is a poor fit for FPGAs._
Sirop provides a set of "parallel patterns" from the functional programming paradigm: `map`, `reduce`, `zip`, etc.
Each of these has a natural implementation in RTL.
Programs written with these building blocks therefore tend to be more resource-efficient than corresponding C++ programs compiled with something like Intel HLS.

_Representing FPGA-specific concepts._
When programming an FPGA, you'll come across certain concepts that do not exist (or are much less prominent) in software.
For example, when processing a sequence of data, you need to decide how much _spatial parallelism_ you want.
More spatial parallelism means processing more data per clock cycle (i.e., higher throughput), but it requires more hardware resources.
A plain `for` loop in C++ doesn't tell the compiler how much spatial parallelism you want; you need to communicate that via tool-specific pragmas.
In Sirop, the level of spatial parallelism is clearly represented in the type system.

See [the LCTES '26 conference paper](https://doi.org/10.1145/3814943.3816175) for more details.

#### ... domain-specific languages (e.g., [HLS4ML](https://fastmachinelearning.org/hls4ml/intro/introduction.html))?

Domain-specific languages are very convenient in their target domain, but not very useful in other cases.
Sirop aims to be more general.

## Installing

The Sirop compiler is provided as an executable .jar file; see the [Releases tab](https://github.com/louis-hildebrand/sirop-hls/releases).
Download the .jar file and run it with

```sh
java -jar sirop.jar --version
```

For convenience, you could define a function like

```sh
function sirop {
    java -jar /absolute/path/to/sirop.jar "$@"
}
export -f sirop
```

and then run

```sh
sirop --version
```

To quickly see what an expression evaluates to, try the [REPL](https://en.wikipedia.org/wiki/Read%E2%80%93eval%E2%80%93print_loop):

```sh
$ sirop
Welcome to the Sirop REPL (v2.0.0)!
Type 'exit' or press Ctrl+D to exit.
> u = [1:u8, 2:u8, 3:u8, 4:u8]s
> v = [5:u8, 6:u8, 7:u8, 8:u8]s
> StmZip(u, v).StmMap( @(x, y) => x * y ).StmSum()
[70:u8]s
```

For help with the command-line interface, run

```sh
sirop --help
```

### Syntax Highlighting

A Vim plugin providing basic syntax highlighting can be found at [github.com/louis-hildebrand/sirop-vim](https://github.com/louis-hildebrand/sirop-vim).

## Introduction to the Language

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

```javascript
> // JavaScript
> [1, 2, 3, 4].map(x => x + 5)
[ 6, 7, 8, 9 ]
```

In Sirop, you can transform each element of a vector using `VecMap`:

```c++
> [1:u8, 2:u8, 3:u8, 4:u8]v.VecMap(x => x + 5)
[6:u8, 7:u8, 8:u8, 9:u8]v
```

This will result in four adders being instantiated to process all the vector's elements in parallel.
Similarly, you can transform each element of a stream using `StmMap`:

```c++
> [1:u8, 2:u8, 3:u8, 4:u8]s.StmMap(x => x + 5)
[6:u8, 7:u8, 8:u8, 9:u8]s
```

In this case, only one adder will be needed because the stream yields just one element per clock cycle.
It's also possible to partially parallelize this code by representing the input as a stream of vectors:

```c++
> [[1:u8, 2:u8]v, [3:u8, 4:u8]v]s.StmMap(v => v.VecMap(x => x + 5))
[[6:u8, 7:u8]v, [8:u8, 9:u8]v]s
```

Here, the stream will yield two elements per cycle and there will be two adders to process them.
This idea of using types to represent the level of spatial parallelism appears in prior works, including [Lift-HLS](https://doi.org/10.1145/3315454.3329957), [Aetherling](https://doi.org/10.1145/3385412.3385983), and [SHIR](https://doi.org/10.1145/3501768).

## Example: Dot Product

As a simple example, consider the [dot product](https://en.wikipedia.org/wiki/Dot_product) of two streams.
This can be expressed as follows in Sirop:

```c++
// u and v are streams of 16-bit unsigned integers, each with length 4
accelerator dot = (u: Stm[u16, 4]) => (v: Stm[u16, 4]) =>
    // EXAMPLE: u = [1:u16, 2:u16, 3:u16, 4:u16]s
    // EXAMPLE: v = [5:u16, 6:u16, 7:u16, 8:u16]s
    StmZip(u, v)
    // EXAMPLE: [(1:u16, 5:u16), (2:u16, 6:u16), (3:u16, 7:u16), (4:u16, 8:u16)]s
    .StmMap( @(x, y) => x * y )
    // EXAMPLE: [5:u16, 12:u16, 21:u16, 32:u16]s
    .StmSum()
    // EXAMPLE: [70:u16]s

assert {
    // when the inputs are...
    u = [1:u16, 2:u16, 3:u16, 4:u16]s,
    v = [5:u16, 6:u16, 7:u16, 8:u16]s
}
// ... then we expect the following output
yields [70:u16]s
```

### Testing and Debugging

The Sirop compiler can test the high-level code directly, without translating it to VHDL:

```sh
$ sirop -i dot.sirop --out:test
[INFO ] test 0: PASSED
[INFO ] 1/1 test passed!
```

If you had accidentally used addition instead of multiplication in `StmMap`, the test would fail:

```sh
$ sed -i 's/x \* y/x + y/g' dot.sirop && \
> sirop -i dot.sirop --out:test ; \
> sed -i 's/x + y/x \* y/g' dot.sirop
[WARN ] test 0: WRONG OUTPUT
TestError: 1/1 test failed.
```

The Sirop compiler can also generate a cycle-by-cycle trace of the execution of the program.
For example, running the following (with the working dot product program):

```sh
# Disable fusion to show each pipeline stage (zip, map, sum).
# By default, the compiler combines everything into a single pipeline stage.
$ sirop -i dot.sirop --out:trace ./trace --opt:no-fuse
```

generates a series of images in `./trace`.

> [!IMPORTANT]
> The images are generated using Graphviz, which must be installed separately.
> See https://graphviz.org/download/.

![Time step 0 of the dot product trace](./docs/dot-trace/step_0.svg)
![Time step 1 of the dot product trace](./docs/dot-trace/step_1.svg)
![Time step 2 of the dot product trace](./docs/dot-trace/step_2.svg)
![Time step 3 of the dot product trace](./docs/dot-trace/step_3.svg)
![Time step 4 of the dot product trace](./docs/dot-trace/step_4.svg)
![Time step 5 of the dot product trace](./docs/dot-trace/step_5.svg)
![Time step 6 of the dot product trace](./docs/dot-trace/step_6.svg)
![Time step 7 of the dot product trace](./docs/dot-trace/step_7.svg)
![Time step 8 of the dot product trace](./docs/dot-trace/step_8.svg)

### Generating VHDL and Running Simulation

Once you're satisfied that the high-level code does what you expect, the Sirop compiler can translate it to VHDL.
It can also generate a testbench to check that the generated VHDL entity behaves the way you expect.

> [!IMPORTANT]
> The testbench is run using Questa.
> The relevant commands (`vcom`, `vsim`, etc.) must be on your `PATH`.
> Furthermore, you may need a license to run `vsim`.

```sh
$ sirop -i dot.sirop --out:vhdl ./hdl --out:vhdl:run-sim
[INFO ] VHDL testbench passed!
```

At this point, you have VHDL code that can be synthesized with Quartus, simulated with your own testbench in Questa, etc.

### More Examples

More example programs can be found in [src/main/resources/mhir/main/stored/](./src/main/resources/mhir/main/stored) and [src/e2e/resources/](./src/main/resources).

## Development

### Documentation

The Sirop intermediate representation is described in a conference paper: https://doi.org/10.1145/3814943.3816175.

Documentation for the Scala code can be generated and opened using the following command (assuming you're in the project root directory).
```shell
sbt doc && open target/scala-2.12/api/index.html
```

There are also rough notes explaining certain design decisions in [notes/](./notes).

### Running the Tests

The following command runs all the tests
```shell
sbt test
```

Some tests involve generating and simulating VHDL or Verilog.
These are tagged with `@mhir.testing.HardwareTest`.
Therefore, the following command will run all tests *except* the hardware tests.
```shell
sbt 'testOnly * -- -l mhir.testing.HardwareTest'
```

Or, conversely, run *only* the hardware tests with
```shell
sbt 'testOnly * -- -n mhir.testing.HardwareTest'
```

Running the VHDL tests requires a VHDL compiler and simulator.
This project was tested using Quartus Prime Lite 21.1.
The test suite `TestRunnerTests` verifies that VHDL can be compiled and simulated.
```shell
sbt 'testOnly *TestRunnerTests'
```

Similarly, run *only* the performance tests with
```shell
sbt 'testOnly * -- -n mhir.testing.PerformanceTest'
```

### Logging

Logging is performed via the scala-logging library with the logback backend.
The log level can be adjusted in [logback.xml](./src/main/resources/logback.xml).

### Debugging

There are some classes that help with debugging in [the debug package](./src/main/scala/mhir/debug).
Consult the package documentation for more information.

### Git Pre-Commit Hook

A Git pre-commit hook is available to check formatting and run some of the tests ([githooks/pre-commit](./githooks/pre-commit)).
The following command installs it.
```shell
git config core.hooksPath githooks
```

### Creating a Release

A new release can be created by first updating the version in [src/main/resources/version.txt](./src/main/resources/version.txt) and then running [release.sh](./release.sh).
