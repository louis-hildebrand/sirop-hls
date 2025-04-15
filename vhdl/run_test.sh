#!/bin/bash

# $1: Path to VHDL project directory
# Additionally, pass -v or --verbose to see output from the synthesizer and simulator.

set -u

function echoerr {
    >&2 echo "$1"
}

VERBOSE='false'
for arg in "$@"; do
    if [[ "$arg" = "--verbose" ]] || [[ "$arg" = "-v" ]]; then
        VERBOSE='true'
        break
    fi
done
if [[ "$VERBOSE" = 'true' ]]; then
    echo "Running in verbose mode."
fi

if [[ "$#" -lt 1 ]]; then
    echoerr "Missing required argument PROJ_DIR."
    exit 2
fi
PROJ_DIR="$1"

function run_simulation {
    vsim -c -do "run -all; quit -code [coverage attribute -name TESTSTATUS -concise]" -t 1ps -L altera -L lpm -L sgate -L altera_mf -L altera_lnsim -L cyclonev -L cyclonev_hssi -L work -voptargs="+acc" testbench
}

function main {
    cd "$PROJ_DIR" || {
        error "Failed to move to directory $PROJ_DIR. Does it exist?"
        exit 3
    }

    for f in design/*.vhd; do
        echo ""
        echo "Compiling design file $f..."
        vcom "$f" || {
            echoerr "Failed to compile design file $f."
            exit 4
        }
    done
    for f in test/*.vhd; do
        echo ""
        echo "Compiling testbench $f..."
        vcom "$f" || {
            echoerr "Failed to compile testbench $f."
            exit 5
        }
    done

    echo ""
    echo "Running simulation..."
    run_simulation || {
        echoerr "Simulation failed."
        exit 6
    }
}

if [[ "$VERBOSE" = "true" ]]; then
    main && echo "All good :D"
else
    main 2>&1 > /dev/null
fi
