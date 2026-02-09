#!/usr/bin/env python3

"""
This script performs synthesis for each program.
"""

import os
import subprocess
from argparse import ArgumentParser, Namespace
from subprocess import CalledProcessError

import lib.constants as c


def synthesize(prog: str) -> None:
    """
    Invoke the synthesis tool for the given program.
    """
    proj_dir = c.INTEL_HLS_DIR.joinpath(prog)
    prev_cwd = os.getcwd()
    try:
        os.chdir(proj_dir)
        subprocess.run(["make", "synth"], check=True)
    except CalledProcessError:
        print(f"Failed to synthesize {proj_dir}")
    finally:
        os.chdir(prev_cwd)


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
