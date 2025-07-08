# min_hw_ir

This project is an intermediate representation and compiler for streaming accelerators.
The goal is to be able to take a high-level algorithm, expressed using familiar functional building blocks like `map` and `fold`, and turn it into a high-performance streaming accelerator (e.g., in VHDL).

## Evaluation

There are some benchmarks from [Aetherling](https://dl.acm.org/doi/10.1145/3385412.3385983) in [src/test/resources/aetherling_benchmarks/original](src/test/resources/aetherling_benchmarks/original).
> NOTE
>
> The Verilog files in the Aetherling repository seem to be named according to latency.
When they were copied into this repository, they were renamed to show the throughput instead (so that the naming of benchmarks is consistent).
So, for example, map_40.v in the Aetherling repository (which has a latency of 40 cycles and a throughput of 5 elements per cycle) was renamed to map_5.v.

To run these, first create a Python virtual environment and install the required dependencies.
The scripts were developed and tested with Python 3.10.
```shell
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

To measure the resource utilization for each benchmark, run the following command.
It will take a long time.
The raw data will be placed in [results/](./results).
```shell
python src/test/python/aetherling_measure.py
```

To plot the results, run the following command.
This will plot whatever is in the [results/](./results) directory, so you do not need to re-run the measurements if you just want to see the plot.
The plot will also be placed in [results/](./results).
```shell
python src/test/python/aetherling_plot.py
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
