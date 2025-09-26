#!/bin/bash

set -u

DEFAULT_TIMEOUT='10s'
SHOW_WAVES='true'
TIME_RESOLUTION='100ps'

BAD_ARGS=2
MISSING_PROJ=3
DESIGN_COMPILE_FAILED=4
TESTBENCH_COMPILE_FAILED=5
SIMULATION_TIMEOUT=6
SIMULATION_FAILED=7

function print_basic_usage {
    echo "Usage: test_vhdl.sh PROJ_DIR [-h|--help] [-v|--verbose] [-i|--interactive] [--time-limit=TL]"
}

function print_usage {
    print_basic_usage
    echo
    echo "Arguments:"
    echo "  PROJ_DIR           Path to the VHDL project directory"
    echo "  -h, --help         Print the help message and exit"
    echo "  -v, --verbose      Show the output from the VHDL compiler and simulator"
    echo "  -i, --interactive  Open the simulator GUI"
    echo "  --time-limit=TL    Set the timeout value to pass to the timeout command (default: $DEFAULT_TIMEOUT)"
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
    time_limit="$DEFAULT_TIMEOUT"
    for arg in "${@:2}"; do
        if [[ "$arg" = "--verbose" ]] || [[ "$arg" = "-v" ]]; then
            verbose_mode='true'
        elif [[ "$arg" = "--interactive" ]] || [[ "$arg" = "-i" ]]; then
            interactive_mode='true'
        elif [[ "$arg" =~ ^--time-limit= ]]; then
            time_limit="${arg#--time-limit=}"
        else
            usage_error "Unrecognized argument: $arg"
        fi
    done
}

function compile {
    echo "Compiling" "$@"
    vcom -2002 -autoorder "$@"
}

function run_simulation {
    if [[ "$interactive_mode" == 'true' ]]; then
        tcl_script="run -all"
        if [[ "$SHOW_WAVES" == "true" ]]; then
            tcl_script="add wave sim:/testbench/*; add wave sim:/testbench/out_check_*/*; add wave sim:/testbench/DUT/*; $tcl_script"
        fi
        vsim -i -do "$tcl_script" -t "$TIME_RESOLUTION" -L work -voptargs="+acc" testbench
    else
        tcl_script="set NumericStdNoWarnings 1; run -all; quit -code [coverage attribute -name TESTSTATUS -concise]"
        timeout "$time_limit" vsim -c -do "$tcl_script" -t "$TIME_RESOLUTION" -L work -voptargs="+acc" testbench
    fi
}

function main {
    cd "$proj_dir" || {
        echoerr "Failed to move to directory $proj_dir. Does it exist?"
        exit "$MISSING_PROJ"
    }

    echo ""
    compile design/*.vhd || {
        echoerr "Failed to compile design."
        exit "$DESIGN_COMPILE_FAILED"
    }

    echo ""
    compile test/*.vhd || {
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
