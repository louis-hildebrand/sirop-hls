# LCTES '26 Artifact Evaluation: Sirop

## Setup

[//]: # TODO: explain how to connect to lab server

[//]: # TODO: explain how to obtain and unzip sirop repo

```sh
SIROP=~/sirop
```

[//]: # TODO: explain how to set up venv on lab server, since scripts need at least Python 3.10

[//]: # TODO: tell reader about `check_host_tools.sh`

[//]: # TODO: tell reader about precompiled solutions and how they can use those to immediately generate the plots?

[//]: # TODO: rest of guide assumes user is in src/test/python

[//]: # TODO: explain how to debug (e.g., run `test_vhdl.sh . -v`, check `transcript` and `latency*.log`)

[//]: # TODO: suggest deleting results/ folder before starting to prove that the new results are fresh?

[//]: # TODO: Add logging

[//]: # TODO: ignore info message about no assertions in source code

[//]: # TODO: ignore warning about missing `bigsobel_1_3.v`

[//]: # TODO: Say how long each step will take

[//]: # TODO: Say where the benchmark source code is found in each case

[//]: # TODO: Add --docker flag for each of the `*_run_all.py` scripts to make things easier?

[//]: # TODO: Explain how to check results in section 7.5, paragraph "Extensibility"

## Step-by-step instructions

### Figure 17 (ablation study)

This section explains how to generate figure 17 (showing the effects of optimizations).

First, run the Sirop compiler to translate the benchmark source code (in `$SIROP/src/main/resources/mhir/main/stored/`) to VHDL (in `$SIROP/src/test/vhdl/ablation/`).
This should be done using the Docker image.
For example, to do this only for the `map` benchmark, run the following command.

```sh
./docker_run.py './ablation_generate.py map'
```

> ***Note***
>
> Most of the scripts mentioned in this document accept a list of benchmarks to run.
> For example, the command above will call the Sirop compiler only for the `map` benchmark.
> Passing an empty list instead will cause the scripts to run all benchmarks.
> For example, `./docker_run.py './ablation_generate.py'` will call the Sirop compiler for all benchmarks.

Once the VHDL project has been generated, the following commands will synthesize the VHDL code and then extract the Fmax and resource usage results.
The results will be saved in `$SIROP/results/ablation_fmax.csv` and `$SIROP/results/ablation_resource_usage.csv`.

```sh
./ablation_synth.py map
./ablation_extract_fmax.py map
./ablation_extract_resource_usage.py map
```

Finally, to generate the plots, run the following command (in the Docker container again).
One PDF file will be generated: `$SIROP/ablation_resource_usage.pdf`.
Figure 17 in the paper shows just the resource usage.

```sh
./docker_run './ablation_plot_resource_usage.py'
```

*Optionally*, the latency can also be measured and plotted.
`ablation_measure_latency.py` will run the VHDL testbench and report the total latency, in clock cycles.
The result will be saved in `$SIROP/results/ablation_latency.csv`.
`ablation_plot_latency.pdf` will then generate the PDF file `$SIROP/results/ablation_latency.pdf`.

```sh
./ablation_measure_latency.py map
./docker_run.py './ablation_plot_latency.py'
```

> ***Reminder***
>
> So far, we have only reproduced the results for the `map` benchmark.
> To try other benchmarks, follow the same instructions but replace `map`.

[//]: # TODO: explain how to copy plot off of lab server

### Figure 19 (design space exploration with Aetherling)

This section explains how to generate figure 19 (comparing the resource usage and latency of Sirop to Aetherling's existing Chisel backend).
The steps are similar to those for the ablation study (figure 17).

Each step below handles both compilation flows: Aetherling --> Chisel --> Verilog and Aetherling --> Sirop --> VHDL.
For the first flow (Chisel), the generated Verilog has been precomputed and stored in `$SIROP/src/test/resources/aetherling_benchmarks/verilog/`, since Aetherling takes a long time on some benchmarks.
These files are copied to `$SIROP/src/test/verilog/aetherling_chisel/` along with a testbench and other project files (.qpf, .qsf, .sdc, etc.)
For the second flow (Sirop), the Aetherling Lst code in `$SIROP/src/test/resources/aetherling_benchmarks/original/` is parsed and compiled by the Sirop compiler.
The VHDL projects are placed in `$SIROP/src/test/vhdl/aetherling_sirop/`.

Similarly to the previous section, the latency results will be saved in `$SIROP/aetherling_latency.csv`, the Fmax results will be saved in `$SIROP/aetherling_fmax.csv`, and the resource usage results will be saved in `$SIROP/aetherling_resource_usage.csv`.
Two PDF files will be generated for the plots: `$SIROP/results/aetherling_resource_usage.pdf` and `$SIROP/results/aetherling_latency.pdf`.
Together, they form figure 19.

> ***Note***
>
> The scripts shown below take as input the paths to the files for which to run the experiments.
> In this case, only the `map` benchmark with a target throughput of 1 element per cycle will be evaluated.
> Calling those scripts with no arguments will evaluate all benchmarks at all target throughputs.

```sh
./docker_run.py './aetherling_generate.py ../resources/aetherling_benchmarks/original/map_1.txt'

./aetherling_measure_latency.py ../resources/aetherling_benchmarks/original/map_1.txt

./aetherling_synth.py ../resources/aetherling_benchmarks/original/map_1.txt
./aetherling_extract_fmax_estimates.py ../resources/aetherling_benchmarks/original/map_1.txt
./aetherling_extract_resource_usage.py ../resources/aetherling_benchmarks/original/map_1.txt

./docker_run './ablation_plot_resource_usage.py'
./docker_run './ablation_plot_latency.py'
```

### Figure 18 (comparison with prior works)

#### Intel HLS

Since the Intel HLS compiler is proprietary software, it is not included in the Docker image.
Instead, the entire evaluation flow should be run on the host machine.
This can be done as follows.

```sh
./ihc_run_all.py dot # Try just the dot benchmark, for a change
```

This will synthesize each of the HLS C++ programs in `$SIROP/src/test/hls`.
As before, the post-place and route results will be stored in `$SIROP/results/ihc_fmax.csv` and `$SIROP/results/ihc_resource_usage.csv`.
Unfortunately, the latency measurement is not automated.
To test a program by simulation, go to the directory for the desired benchmark and use `make run-fpga`.
For example:

```sh
cd $SIROP/src/test/hls/dot
make run-fpga
```

It should print "PASSED".
Then, inspect the timing diagram by running

```sh
vsim dot.prj/verification/vsim.wlf
```

[//]: # TODO: Provide more details about how to count clock cycles

> ***Reminder***
>
> Don't forget to `cd` back to `$SIROP/src/test/python` to follow the rest of these instructions!

#### Aetherling

Aetherling's existing Chisel backend is evaluated as part of generating figure 19.
Figure 18 uses the same data.

[//]: # TODO: explain how the scripts choose among the different benchmark/throughput pairs?

#### Sirop

[//]: # TODO

```sh
docker_run.py ./sirop_generate.py map

./sirop_synth.py map
./sirop_extract_fmax.py map
./sirop_extract_resource_usage.py map

./sirop_latency.py map
```

#### Plotting the Data

The following scripts will produce the two PDF files that make up figure 18: `$SIROP/results/cpw_resource_usage.pdf` and `$SIROP/results/cpw_latency.pdf`.

```sh
./docker_run './cpw_plot_resource_usage.py'
./docker_run './cpw_plot_latency.py'
```

### Table 2 (hardware generator code size)

This section explains how to find the data in table 2, which shows the size of different HLS tools' hardware generators.

#### Aetherling

To count the lines of code required for hardware generating in Aetherling, use the following command

```sh
./docker_run.py 'cloc /sirop/src/test/aetherling/chiselAetherling/src/main/ /sirop/src/test/aetherling/src/Core/Aetherling/Interpretations/Backend_Execute/Chisel/Expr_To_String.hs'
```

Note that there is also some hardware generation-related code in `src/Core/Aetherling/Interpretations/Backend_Execute/Compile.hs`, but since it is mixed in with some other code (e.g., for other backends) we do not count it.

#### Chisel

To measure the size of the Chisel project, use the following commands.

```sh
./docker_run.py 'cloc /chisel/src/main /chisel/chiselFrontend/src/main'
```

Note that the Docker container has the specific version of Chisel (v3.2) used by Aetherling.

#### SHIR

To count the lines of Scala code required for hardware generation in SHIR, use the following command.

```sh
./docker_run.py 'cloc /sirop/src/test/shir/src/main/backend/hdl/{graph,vhdl}'
```

[//]: # TODO: This number is not what I had before. How was I measuring it previously?

To measure the amount of VHDL code used for hardware generation in SHIR, use the following command.

```sh
./docker_run.py 'cloc /sirop/src/test/shir/vhdltemplates/ --not-match-f "mem|async|arbiter|temp" --not-match-d "drafts|testing|unused"'
```

Note that we ignore templates related to host memory (which is accessed in an asynchronous manner) and draft code.

### Sirop

To count the number of lines of code in Sirop's VHDL generator, use the command

```sh
./docker_run.py 'cloc /sirop/src/main/scala/mhir/gen/vhdl/ --exclude-dir nohandshake --exclude-dir test'
```

Note that two directories are excluded because they implement features that are not discussed in the paper and, to the best of our knowledge, are not available in Aetherling or SHIR.

- `nohandshake` implements hardware generation without a handshake protocol (i.e., `valid` and `ready` must always be true). Aetherling generates hardware with static schedules, but it does not have *both* options available (dynamic and static scheduling).
- `test` implements a testbench generator, which allows the programmer to specify test cases in Sirop and obtain a VHDL testbench.
