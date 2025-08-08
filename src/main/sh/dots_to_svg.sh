#!/bin/bash

set -ue

dir="$1"
for in_file in "$dir"/*.dot; do
    out_file="${in_file%.dot}.svg"
    dot -Tsvg -o "$out_file" "$in_file"
done
