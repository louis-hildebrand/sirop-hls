# Sirop

Sirop is a language and compiler for generating streaming accelerators.
Similarly to projects like [HLS4ML](https://fastmachinelearning.org/hls4ml/intro/introduction.html) and [Altera HLS IP Gen](https://www.altera.com/products/development-tools/hls_ip_gen_compiler), the goal is to convert high-level code into VHDL that can be synthesized and run on an FPGA.

See the documentation at https://louis-hildebrand.github.io/sirop-hls.

## Development

### Developer Documentation

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

### Local Copy of User Documentation Website

The user documentation site is generated using [Sphinx](https://www.sphinx-doc.org/en/master/index.html).
To generate a local copy (e.g., to see how your changes look), move to the [docs/](./docs/) directory and run `make html`.

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
