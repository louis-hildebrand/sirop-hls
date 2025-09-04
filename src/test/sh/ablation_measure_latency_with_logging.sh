#!/bin/bash

set -u

proj_dir="$(readlink -f "$1")"

mhir_repo_root="$(readlink -f "$(dirname "$(readlink -f "$0")")/../../..")"

now="$(date '+%Y%m%d-%H%M%S')"
log_file="$proj_dir/latency-$now.log"
cd "$mhir_repo_root"
sbt "Test/runMain mhir.main.stored.LatencyMeasurement \"$proj_dir\"" &> "$log_file"
exit_code=$?
cat "$log_file" | sed -nE 's/^LATENCY: ([0-9]+) cycles$/\1/p'
exit "$exit_code"
