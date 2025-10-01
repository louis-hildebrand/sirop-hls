#!/bin/python3

"""
This script performs synthesis for each program.
"""

from argparse import ArgumentParser, Namespace

import lib.constants as c
from lib import synth


def synthesize(prog: str) -> None:
    """
    Call the synthesis tool for the given program variant.
    """
    proj_dir = c.DSE_VHDL_DIR.joinpath(prog)
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
        synthesize(prog)


def parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(description="runs synthesis for each of the given programs")
    parser.add_argument(
        "programs",
        nargs="*",
        help="the names of the programs to process"
    )
    return parser.parse_args()


if __name__ == "__main__":
    _args = parse_args()
    main(_args.programs)
