#!/bin/bash

# Extract the ALM count from the resource report.
# This script must be called from within the root directory of the VHDL project.

cd output_files
sed_command='s/Logic utilization \(in ALMs\) : ([0-9,]+) .*/\1/p'
if [[ -f top.fit.summary ]]; then
    cat top.fit.summary | sed -nE "$sed_command" | sed 's/,//g'
elif [[ -f Top.fit.summary ]]; then
    cat Top.fit.summary | sed -nE "$sed_command" | sed 's/,//g'
else
    >&2 echo "Could not find resource usage report."
    exit 1
fi
