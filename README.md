# min_hw_ir

This project is an intermediate representation and compiler for streaming accelerators.
The goal is to be able to take a high-level algorithm, expressed using familiar functional building blocks like `map` and `fold`, and turn it into a high-performance streaming accelerator (e.g., in VHDL).

## Evaluation

Many of the evaluation scripts are written in Python.
They were developed and tested in Python 3.10.
To create a Python virtual environment and install the required dependencies, use the following commands.
```shell
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

### Compared to Aetherling

There are some benchmarks from [Aetherling](https://dl.acm.org/doi/10.1145/3385412.3385983) in [src/test/resources/aetherling_benchmarks/original](src/test/resources/aetherling_benchmarks/original).

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

#### Getting the Benchmarks from Aetherling

If you want to see for yourself that Aetherling indeed produces the benchmarks in [src/test/resources/aetherling_benchmarks/original](src/test/resources/aetherling_benchmarks/original), follow these instructions.

First, clone my fork of Aetherling from https://github.com/louis-hildebrand/aetherling and follow the setup instructions.
The fork includes some tweaks to Aetherling's test code so that only the space/time code and Verilog code we need are produced.

Once everything is set up, run the tests by executing the following command (while inside the Aetherling repo).
```shell
stack test --test-arguments '--num-threads 1'
```

This should generate a bunch of .txt and .v files in the `test/no_bench/` directory (still within the Aetherling repo).
To copy those to this repo, run the following commands, where `MHIR_REPO` is set to the path to this repo:
```shell
MHIR_REPO=...
find ./test/no_bench/ -name '*.txt' -exec cp {} "$MHIR_REPO/src/test/resources/aetherling_benchmarks/original" \;
find ./test/no_bench/ -name '*.v' -exec cp {} "$MHIR_REPO/src/test/resources/aetherling_benchmarks/verilog" \;
```

Finally, rename the benchmarks using the following command:
```shell
"$MHIR_REPO/src/test/sh/rename_benchmarks.sh"
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

### Git Pre-Commit Hook

A Git pre-commit hook is available to check formatting and run some of the tests ([githooks/pre-commit](./githooks/pre-commit)).
The following command installs it.
```shell
git config core.hooksPath githooks
```
