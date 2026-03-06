#!/usr/bin/env python3

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
    proj_dir = c.SHIR_SHIR_VHDL_DIR.joinpath(prog)
    ok = synth.synthesize_design(proj_dir, top="top")
    if not ok:
        print(f"Failed to synthesize {proj_dir}")


def synthesize_sirop(prog: str) -> None:
    """
    Invoke the synthesis tool for the given program.
    """
    proj_dir = c.SHIR_SIROP_VHDL_DIR.joinpath(prog)
    ok = synth.synthesize_design(proj_dir, top="top")
    if not ok:
        print(f"Failed to synthesize {proj_dir}")


def main(programs: list[str], skip_shir: bool, skip_sirop: bool) -> None:
    """
    Script entry point.
    """
    print("-" * 80)
    print("- Synthesizing projects...")
    print(f"- Programs : {', '.join(programs)}")
    print("-" * 80)

    if not skip_shir:
        print("Synthesizing SHIR designs...")
        for prog in programs:
            synthesize_shir(prog)

    if not skip_sirop:
        print("Synthesizing Sirop designs...")
        for prog in programs:
            synthesize_sirop(prog)


def parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(description="runs synthesis for each of the given programs")
    parser.add_argument(
        "--skip-shir",
        action="store_true",
        help="don't synthesize any SHIR projects",
    )
    parser.add_argument(
        "--skip-sirop",
        action="store_true",
        help="don't synthesize any Sirop projects",
    )
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
    main(
        _args.programs,
        skip_shir=_args.skip_shir,
        skip_sirop=_args.skip_sirop,
    )
