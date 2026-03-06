#!/usr/bin/env python3

"""
This script performs synthesis for each program.
"""

from argparse import ArgumentParser, Namespace

import lib.constants as c
from lib import synth
from lib.optimization_level import OptimizationLevel


def synthesize(prog: str, opt_lvl: OptimizationLevel) -> None:
    """
    Call the synthesis tool for the given program variant.
    """
    proj_dir = c.ABLATION_VHDL_DIR.joinpath(f"{prog}_{opt_lvl}")
    ok = synth.synthesize_design(proj_dir)
    if not ok:
        print(f"Failed to synthesize {proj_dir}")


def main(programs: list[str]) -> None:
    """
    Script entry point.
    """
    print("-" * 80)
    print("- Synthesizing projects...")
    print(f"- Programs : {', '.join(programs)}")
    print("-" * 80)

    for prog in programs:
        for lvl in OptimizationLevel:
            synthesize(prog, lvl)


def parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(description="runs synthesis for each of the given programs")
    parser.add_argument(
        "programs",
        nargs="*",
        help=(
            "the names of the programs to process"
            f" (the ones in the paper are: {' '.join(c.ACTIVE_BENCHES)})"
        )
    )
    args = parser.parse_args()
    if not args.programs:
        args.programs = c.ACTIVE_BENCHES
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(_args.programs)
