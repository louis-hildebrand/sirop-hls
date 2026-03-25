# Sirop

Sirop is an intermediate representation and compiler for streaming accelerators.
Its goal is to convert a high-level program into a high-performance streaming accelerator (e.g., in VHDL).
Programs are expressed using building blocks like `StmMap` (`map` over streams - i.e., with pipeline parallelism), `VecMap` (`map` over vectors - i.e., with spatial parallelism), `StmReduce`, `VecReduce`, etc.

## Examples

Example programs can be found in the [src/main/resources/mhir/main/stored/](./src/main/resources/mhir/main/stored) directory.
They can easily be run using the `-s stored` compiler option.
For example, to display the lowered and optimized IR for the `dot` program:
```sh
$ java -jar sirop.jar -s stored -i dot --out:pp - --quiet
(I0 : Stm[u16, 840:u10]) =>
  (I1 : Stm[u16, 840:u10]) =>
    sbuild(1:u1)(acc_14 +% sdata(s_16), t_15 == 839:u10) {
      (acc_14 : u16) = {
        init: 0:u16,
        next: if (first_step_17) then { sdata(s_16) } else { acc_14 +% sdata(s_16) }
      },
      (first_step_17 : bool) = {
        init: true,
        next: false
      },
      (t_15 : u10) = {
        init: 0:u10,
        next: 1:u10 + t_15
      }
    } {
      (s_16 : Stm[u16, 840:u10]) = {
        stm: sbuild(840:u10)(sdata(x_5) *% sdata(x_6), true) {} {
          (x_5 : Stm[u16, -1:i1]) = {
            stm: I0,
            ready: true
          },
          (x_6 : Stm[u16, -1:i1]) = {
            stm: I1,
            ready: true
          }
        },
        ready: true
      }
    }
```

## Documentation

Documentation for the Scala code can be generated and opened using the following command (assuming you're in the project root directory).
```shell
sbt doc && open target/scala-2.12/api/index.html
```

There are also rough notes explaining certain design decisions in [notes/](./notes).

## Development Tasks

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

Running the VHDL tests requires a VHDL compiler and simulator.
This project was tested using Quartus Prime Lite 21.1.
The test suite `TestRunnerTests` verifies that VHDL can be compiled and simulated.
```shell
sbt 'testOnly *TestRunnerTests'
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
