#!/bin/bash

# Extract the DSP count from the resource report.
# This script must be called from within the root directory of the VHDL project.

cat output_files/top.fit.summary | sed -nE 's/Total DSP Blocks : ([0-9]+) .*/\1/p'
