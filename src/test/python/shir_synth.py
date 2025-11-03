#!/bin/python3

"""
This script performs synthesis for each program.
"""

from argparse import ArgumentParser, Namespace

import lib.constants as c
from lib import synth


def synthesize_shir(prog: str) -> None:
    """
    Invoke the synthesis tool for the given program.
    """
    proj_dir = c.SHIR_VHDL_DIR.joinpath(prog)
    ok = synth.synthesize_design(proj_dir, top="top")
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

    print("Synthesizing SHIR designs...")
    for prog in programs:
        synthesize_shir(prog)


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
