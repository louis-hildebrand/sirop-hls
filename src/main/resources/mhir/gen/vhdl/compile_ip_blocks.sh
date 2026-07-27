#!/bin/bash

set -euo pipefail

MY_NAME='compile_ip_blocks.sh'

if [[ -z "${QUARTUS_ROOTDIR:-}" ]]; then
    >&2 echo "$MY_NAME: ERROR: Cannot compile IP blocks because QUARTUS_ROOTDIR is not set."
    exit 1
fi
sim_lib_dir="$QUARTUS_ROOTDIR/eda/sim_lib"

function make_lib_in {
    lib="$1"
    target_dir="$2"
    mkdir -p "$target_dir"
    UPDATED='false'
    if [[ ! -e "$target_dir/$lib" ]]; then
        vlib "$target_dir/$lib"
        UPDATED='true'
    fi
    vmap "$lib" "$target_dir/$lib"
}

function make_lib {
    if [[ -e "$HOME/.cache" ]]; then
        dir="$HOME/.cache/sirop/quartus_sim_lib"
        version_file="$dir/version.txt"
        if [[ ! -e "$version_file" ]]; then
            version_matches='false'
        elif [[ "$(cat "$version_file")" = "$(quartus_sh --version)" ]]; then
            version_matches='true'
        else
            version_matches='false'
        fi
        if [[ "$version_matches" != 'true' ]]; then
            echo "$MY_NAME: INFO: rebuilding cache because current quartus_sh version does not match cached version"
            if [[ "${#dir}" -lt 12 ]]; then
                >&2 echo "$MY_NAME: ERROR: something is wrong: variable dir ('$dir') is shorter than expected."
                >&2 echo "$MY_NAME: ERROR: stopping before anything important gets deleted."
                exit 1
            fi
            rm -rf "$dir"
            mkdir -p "$dir"
            quartus_sh --version > "$version_file"
        fi
        make_lib_in "$1" "$dir"
    else
        make_lib_in "$1" "./lib"
    fi
}

UPDATED=''
make_lib altera_lnsim
if [[ "$UPDATED" = 'false' ]]; then
    echo "$MY_NAME: INFO: reusing cached compilation of altera_lnsim library"
else
    vcom        "$sim_lib_dir/altera_lnsim_components.vhd"  -work altera_lnsim
fi

UPDATED=''
make_lib tennm
if [[ "$UPDATED" = 'false' ]]; then
    echo "$MY_NAME: INFO: reusing cached compilation of tennm library"
else
    vlog -sv    "$sim_lib_dir/mentor/tennm_atoms_ncrypt.sv" -work tennm
    vcom        "$sim_lib_dir/tennm_atoms.vhd"              -work tennm
    vcom        "$sim_lib_dir/tennm_components.vhd"         -work tennm
fi
