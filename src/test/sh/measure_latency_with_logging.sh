#!/bin/bash

set -u

TIME_LIMIT='3h'

proj_dir="$(readlink -f "$1")"

now="$(date '+%Y%m%d-%H%M%S')"
log_file="$proj_dir/latency-$now.log"
cd "$proj_dir" || exit 1
if [[ -f ./test_vhdl.sh ]]; then
    ./test_vhdl.sh . -v --time-limit="$TIME_LIMIT" &> "$log_file"
elif [[ -f ./test_verilog.sh ]]; then
    ./test_verilog.sh . -v --time-limit="$TIME_LIMIT" &> "$log_file"
else
    echo "No test runner shell script found"
    exit 1
fi
exit_code=$?
cat "$log_file" | sed -nE 's/.*LATENCY:\s+([0-9]+) cycles$/\1/p'
exit "$exit_code"
