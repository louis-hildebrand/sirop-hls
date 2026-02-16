#!/bin/bash

# Extract the DSP count from the resource report.
# This script must be called from within the root directory of the VHDL project.

set -u

if [[ "$#" -ge 1 ]]; then
    f="output_files/${1}.fit.summary"
elif [[ -f 'output_files/top.fit.summary' ]]; then
    f='output_files/top.fit.summary'
elif [[ -f 'output_files/Top.fit.summary' ]]; then
    f='output_files/Top.fit.summary'
else
    >&2 echo "Could not find resource usage report."
    exit 1
fi

cat "$f" | sed -nE 's/Total DSP Blocks : ([0-9,]+) .*/\1/p' | sed 's/,//g'
