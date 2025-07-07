#!/bin/bash

# Extract the ALM count from the resource report.
# This script must be called from within the root directory of the VHDL project.

cat output_files/top.fit.summary | sed -nE 's/Logic utilization \(in ALMs\) : ([0-9]+) .*/\1/p'
