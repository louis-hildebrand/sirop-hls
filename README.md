# min_hw_ir

This project is an intermediate representation and compiler for streaming accelerators.
The goal is to be able to take a high-level algorithm, expressed using familiar functional building blocks like `map` and `fold`, and turn it into a high-performance streaming accelerator (e.g., in VHDL).

## Documentation

Documentation for the Scala code can be generated and opened using the following command (assuming you're in the project root directory).
```shell
sbt doc && open target/scala-2.12/api/index.html
```

There are also rough notes explaining certain design decisions in [notes/](./notes/).

## Development Tasks

### Running the Tests

The following command runs all the tests
```shell
sbt test
```

Some tests involve generating and simulating VHDL.
These are tagged with `@mhir.testing.VhdlTest`.
Therefore, the following command will run all tests *except* the VHDL tests.
```shell
sbt 'testOnly * -- -l mhir.testing.VhdlTest'
```

Running the VHDL tests requires a VHDL compiler and simulator.
This project was tested using Quartus Prime Lite 21.1.
The test suite `TestRunnerTests` verifies that VHDL can be compiled and simulated.
```shell
sbt 'testOnly *TestRunnerTests'
```

### Git Pre-Commit Hook

A Git pre-commit hook is available to check formatting and run some of the tests ([githooks/pre-commit](./githooks/pre-commit)).
The following command installs it.
```shell
git config core.hooksPath githooks
```
