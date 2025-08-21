#!/bin/bash

set -u

TIMEOUT='10s'

BAD_ARGS=2
MISSING_PROJ=3
DESIGN_COMPILE_FAILED=4
TESTBENCH_COMPILE_FAILED=5
SIMULATION_TIMEOUT=6
SIMULATION_FAILED=7

function print_basic_usage {
    echo "Usage: test_verilog.sh PROJ_DIR [-h|--help] [-v|--verbose] [-i|--interactive]"
}

function print_usage {
    print_basic_usage
    echo
    echo "Arguments:"
    echo "  PROJ_DIR           Path to the Verilog project directory"
    echo "  -h, --help         Print the help message and exit"
    echo "  -v, --verbose      Show the output from the Verilog compiler and simulator"
    echo "  -i, --interactive  Open the simulator GUI"
    echo
    echo "Status codes:"
    echo "  $BAD_ARGS  Bad args"
    echo "  $MISSING_PROJ  Project could not be found"
    echo "  $DESIGN_COMPILE_FAILED  The design could not be compiled"
    echo "  $TESTBENCH_COMPILE_FAILED  The testbench could not be compiled"
    echo "  $SIMULATION_TIMEOUT  The simulation timed out"
    echo "  $SIMULATION_FAILED  The simulation failed (probably due to incorrect output from the design)"
}

function echoerr {
    >&2 echo "$1"
}

function usage_error {
    >&2 print_basic_usage
    echoerr ""
    echoerr "$1"
    exit "$BAD_ARGS"
}

function parse_args {
    for arg in "$@"; do
        if [[ "$arg" = "--help" ]] || [[ "$arg" = "-h" ]]; then
            print_usage
            exit 0
        fi
    done

    if [[ "$#" -lt 1 ]]; then
        usage_error "Missing required argument PROJ_DIR."
    fi
    proj_dir="$1"
    if [[ "$proj_dir" == -* ]]; then
        usage_error "Flags must come after PROJ_DIR."
    fi

    verbose_mode='false'
    interactive_mode='false'
    for arg in "${@:2}"; do
        if [[ "$arg" = "--verbose" ]] || [[ "$arg" = "-v" ]]; then
            verbose_mode='true'
        elif [[ "$arg" = "--interactive" ]] || [[ "$arg" = "-i" ]]; then
            interactive_mode='true'
        else
            usage_error "Unrecognized argument: $arg"
        fi
    done
}

function compile {
    echo "Compiling" "$@"
    vlog "$@"
}

function run_simulation {
    if [[ "$interactive_mode" == 'true' ]]; then
        vsim -i -do "add wave sim:/Test/*; add wave sim:/Test/in_gen/*; add wave sim:/Test/out_check/*; add wave sim:/Test/DUT/*; run -all" -t 1ps -L altera_ver -L lpm_ver -L sgate_ver -L altera_mf_ver -L altera_lnsim_ver -L cyclonev_ver -L cyclonev_hssi_ver -L cyclonev_pcie_hip_ver -L work -voptargs="+acc" Test
    else
        timeout "$TIMEOUT" vsim -c -do "run -all; quit -code [coverage attribute -name TESTSTATUS -concise]" -t 1ps -L altera_ver -L lpm_ver -L sgate_ver -L altera_mf_ver -L altera_lnsim_ver -L cyclonev_ver -L cyclonev_hssi_ver -L cyclonev_pcie_hip_ver -L work -voptargs="+acc" Test

    fi
}

function main {
    cd "$proj_dir" || {
        echoerr "Failed to move to directory $proj_dir. Does it exist?"
        exit "$MISSING_PROJ"
    }

    echo ""
    compile Top.v || {
        echoerr "Failed to compile design."
        exit "$DESIGN_COMPILE_FAILED"
    }
    if [[ -e Mul.v ]]; then
        compile Mul.v || {
            echoerr "Failed to compile design."
            exit "$DESIGN_COMPILE_FAILED"
        }
    fi

    echo ""
    compile Test.v || {
        echoerr "Failed to compile testbench."
        exit "$TESTBENCH_COMPILE_FAILED"
    }

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

parse_args "$@"
if [[ "$verbose_mode" = "true" ]]; then
    main && echo "All good :D"
else
    main > /dev/null 2>&1
fi
