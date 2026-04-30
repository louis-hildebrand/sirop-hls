#!/bin/bash

set -u

proj_dir="$(readlink -f "$1")"

mhir_repo_root="$(readlink -f "$(dirname "$(readlink -f "$0")")/../../..")"

now="$(date '+%Y%m%d-%H%M%S')"
log_file="$proj_dir/latency-$now.log"
cd "$mhir_repo_root" || exit 1
./src/main/resources/mhir/gen/vhdl/test_vhdl.sh "$proj_dir" -v --time-limit=1h &> "$log_file"
exit_code=$?
cat "$log_file" | sed -nE 's/.*LATENCY: ([0-9]+) cycles$/\1/p'
exit "$exit_code"
