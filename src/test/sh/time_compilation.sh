#!/bin/bash

time (
    ./ablation_generate.py \
    && ./shir_generate.py --skip-shir \
    && ./aetherling_generate.py --skip-verilog --skip-synth
)

./ablation_extract_compile_time.py
./shir_extract_compile_time.py
./aetherling_extract_compile_time.py
