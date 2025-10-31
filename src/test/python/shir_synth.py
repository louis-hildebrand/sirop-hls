#!/bin/python3

"""
This script performs synthesis for each program.
"""

from argparse import ArgumentParser, Namespace

import lib.constants as c
from lib import synth


def synthesize_shir(prog: str) -> None:
    """
    Call the synthesis tool for the given program variant.
    """
    proj_dir = c.SHIR_VHDL_DIR.joinpath(prog)
    ok = synth.synthesize_design(proj_dir, top="top")
    if not ok:
        print(f"Failed to synthesize {proj_dir}")
        return
    patch_dir = c.SHIR_ORIGINALS_DIR.joinpath(f"{prog}_patch")
    input_components = [
        line.strip()
        for line in patch_dir.joinpath("inputs.txt").read_text().split("\n")
        if line.strip()
    ]
    for component in input_components:
        ok = synth.synthesize_design(proj_dir, top=component)
        if not ok:
            print(f"Failed to synthesize input component {component} in {proj_dir}")
            return


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
