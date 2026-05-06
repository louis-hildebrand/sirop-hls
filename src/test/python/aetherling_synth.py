#!/usr/bin/env python3

"""
This script generates and synthesizes VHDL and Verilog projects for the given benchmarks.
"""

from argparse import ArgumentParser, Namespace

import lib.constants as c
import lib.list_benchmarks as lb
from lib import synth


def main(benchmarks: list[str], skip_chisel: bool, skip_sirop: bool) -> None:
    """
    Script entry point
    """
    print("-" * 80)
    print("- Synthesizing projects...")
    print(f"- Benchmarks : {', '.join(benchmarks)}")
    print("-" * 80)

    if not skip_chisel:
        print("Synthesizing Verilog...")
        for b in benchmarks:
            proj_dir = c.CHISEL_VERILOG_DST_DIR.joinpath(b)
            try:
                ok = synth.synthesize_design(proj_dir)
            except Exception:
                ok = False
            if not ok:
                print(f" failed to synthesize {proj_dir}")

    if not skip_sirop:
        print("Synthesizing VHDL...")
        for b in benchmarks:
            proj_dir = c.AETHERLING_VHDL_DIR.joinpath(b)
            try:
                ok = synth.synthesize_design(proj_dir)
            except Exception:
                ok = False
            if not ok:
                print(f" failed to synthesize {proj_dir}")

    print()


def parse_args() -> Namespace:
    """
    Parse the command line arguments.
    """
    parser = ArgumentParser(description="Synthesizes the given benchmarks.")
    parser.add_argument(
        "benchmarks",
        nargs="*",
        help=(
            "the benchmarks to process"
            f" (the ones in the paper can be selected by {lb.ACTIVE_BENCH_GLOB})"
        ),
    )
    parser.add_argument(
        "--skip-chisel",
        action="store_true",
        help="don't synthesize any Chisel-generated Verilog code"
    )
    parser.add_argument(
        "--skip-sirop",
        action="store_true",
        help="don't synthesize any Sirop-generated VHDL code"
    )
    args = parser.parse_args()
    args.benchmarks = lb.names_from_args(args.benchmarks)
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(
        benchmarks=_args.benchmarks,
        skip_chisel=_args.skip_chisel,
        skip_sirop=_args.skip_sirop,
    )
