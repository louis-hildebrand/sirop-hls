#!/bin/bash

set -ue

mhir_repo_root="$(readlink -f "$(dirname "$(readlink -f "$0")")/../../..")"

if ! [[ -e ./chiselAetherling ]]; then
    echo "You do not seem to be in the Aetherling repo (./chiselAetherling does not exist)."
    echo "This script must be called from the root of the Aetherling repo."
    exit 1
fi

stack test --test-arguments '--num-threads 1'
find ./test/no_bench/ -name '*.txt' -exec cp {} "$mhir_repo_root/src/test/resources/aetherling_benchmarks/original" \;
find ./test/no_bench/ -name '*.v' -exec cp {} "$mhir_repo_root/src/test/resources/aetherling_benchmarks/verilog" \;
"$mhir_repo_root/src/test/sh/rename_benchmarks.sh"

echo -e '// Use the * operator rather than a Xilinx IP block\n`define VERILATOR\n' | cat - ./chiselAetherling/src/main/resources/verilogAetherling/mul.v > "$mhir_repo_root/src/main/resources/mhir/gen/verilog/Mul.v"
