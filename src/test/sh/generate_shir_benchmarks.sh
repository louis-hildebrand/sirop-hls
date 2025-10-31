#!/bin/bash

set -ue

mhir_repo_root="$(readlink -f "$(dirname "$(readlink -f "$0")")/../../..")"

if ! [[ -e ./vhdltemplates ]]; then
    echo "You do not seem to be in the SHIR repo (./vhdltemplates does not exist)."
    echo "This script must be called from the root of the SHIR repo."
    exit 1
fi

rm -rf ./out
sbt 'testOnly *SiropBenches'
cp -r ./out/* "$mhir_repo_root/src/test/resources/shir_benchmarks"
