#!/bin/bash

set -ue

if [[ -z "${QUARTUS_ROOTDIR:-}" ]]; then
    >&2 echo "Cannot compile IP blocks because QUARTUS_ROOTDIR is not set."
    exit 1
fi
sim_lib_dir="$QUARTUS_ROOTDIR/eda/sim_lib"

function make_lib {
    lib="$1"
    mkdir -p './lib'
    if [[ ! -e "./lib/$lib" ]]; then
        vlib "./lib/$lib"
    fi
    vmap "$lib" "./lib/$lib/"
}

make_lib altera_lnsim
vcom        "$sim_lib_dir/altera_lnsim_components.vhd"  -work altera_lnsim

make_lib tennm
vlog -sv    "$sim_lib_dir/mentor/tennm_atoms_ncrypt.sv" -work tennm
vcom        "$sim_lib_dir/tennm_atoms.vhd"              -work tennm
vcom        "$sim_lib_dir/tennm_components.vhd"         -work tennm
