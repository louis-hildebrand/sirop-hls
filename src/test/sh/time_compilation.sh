#!/bin/bash

cd "$(git rev-parse --show-toplevel)"

sbt assembly

cd src/test/python

time (
    ./ablation_generate.py --skip-sbt --lvl all except_letstm_simpl except_fuse except_fission \
    && ./shir_generate.py --skip-sbt --skip-shir \
    && ./aetherling_generate.py --skip-sbt --skip-verilog --skip-synth
)

./ablation_extract_compile_time.py
./shir_extract_compile_time.py
./aetherling_extract_compile_time.py
