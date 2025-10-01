#!/bin/python3

"""
This script generates VHDL projects for the given pre-written programs.
"""

import os
import subprocess
from argparse import ArgumentParser, Namespace

import lib.constants as c
from lib.optimization_level import OptimizationLevel


def main(programs: list[str]) -> None:
    """
    Script entry point.
    """
    print("-" * 80)
    print("- Generating projects...")
    print(f"- Programs : {', '.join(programs)}")
    print("-" * 80)

    c.DSE_VHDL_DIR.mkdir(exist_ok=True, parents=True)
    sbt_tasks = []
    for prog in programs:
        out_dir = c.DSE_VHDL_DIR.joinpath(prog)
        lvl = OptimizationLevel.ALL
        sbt_tasks.append(
            f"runMain {c.STORED_PROGRAM_COMPILER} {prog}"
            f" --out-dir {out_dir} --overwrite"
            " --show-final"
            f" {lvl.flags}"
        )
    os.chdir(c.ROOT_DIR)
    subprocess.run(["sbt", "; ".join(sbt_tasks)], check=True)


def parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(
        description="generates VHDL projects from the given pre-written programs."
    )
    parser.add_argument(
        "programs",
        nargs="*",
        help="the names of the programs to process"
    )
    return parser.parse_args()


if __name__ == "__main__":
    _args = parse_args()
    main(_args.programs)
