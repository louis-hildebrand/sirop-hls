#!/bin/bash

# $1: Path to VHDL project directory
# Additionally, pass -v or --verbose to see output from the synthesizer and simulator.

set -u

TIMEOUT='10s'

BAD_ARGS=2
MISSING_PROJ=3
DESIGN_COMPILE_FAILED=4
TESTBENCH_COMPILE_FAILED=5
SIMULATION_TIMEOUT=6
SIMULATION_FAILED=7

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
    exit "$BAD_ARGS"
fi
PROJ_DIR="$1"

function compile {
  vcom -2008 "$1"
}

function run_simulation {
    timeout "$TIMEOUT" vsim -c -do "run -all; quit -code [coverage attribute -name TESTSTATUS -concise]" -t 1ps -L altera -L lpm -L sgate -L altera_mf -L altera_lnsim -L cyclonev -L cyclonev_hssi -L work -voptargs="+acc" testbench
}

function main {
    cd "$PROJ_DIR" || {
        echoerr "Failed to move to directory $PROJ_DIR. Does it exist?"
        exit "$MISSING_PROJ"
    }

    for f in design/*.vhd; do
        echo ""
        echo "Compiling design file $f..."
        compile "$f" || {
            echoerr "Failed to compile design file $f."
            exit "$DESIGN_COMPILE_FAILED"
        }
    done
    for f in test/*.vhd; do
        echo ""
        echo "Compiling testbench $f..."
        compile "$f" || {
            echoerr "Failed to compile testbench $f."
            exit "$TESTBENCH_COMPILE_FAILED"
        }
    done

    echo ""
    echo "Running simulation..."
    run_simulation || {
        case "$?" in
          124)
            echoerr "Simulation timed out. Is there an infinite loop?"
            exit "$SIMULATION_TIMEOUT"
            ;;
          *)
            echoerr "Simulation failed."
            exit "$SIMULATION_FAILED"
            ;;
        esac
    }
}

if [[ "$VERBOSE" = "true" ]]; then
    main && echo "All good :D"
else
    main 2>&1 > /dev/null
fi
