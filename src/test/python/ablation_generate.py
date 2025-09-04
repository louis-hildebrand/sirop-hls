#!/bin/python3

"""
This script generates VHDL projects for the given pre-written programs.
"""

import os
import subprocess
from argparse import ArgumentParser, Namespace

import lib.constants as c


def main(programs: list[str]) -> None:
    """
    Script entry point.
    """
    print("-" * 80)
    print("- Generating projects...")
    print(f"- Programs : {', '.join(programs)}")
    print("-" * 80)

    c.ABLATION_VHDL_DIR.mkdir(exist_ok=True, parents=True)
    sbt_tasks = []
    for prog in programs:
        # No optimizations
        out_dir = c.ABLATION_VHDL_DIR.joinpath(f"{prog}_none")
        sbt_tasks.append(
            f"Test/runMain {c.STORED_PROGRAM_COMPILER} {prog} --out-dir {out_dir} --overwrite"
            " --opt:no-fuse --opt:no-match-latency --opt:no-simplify"
        )
        # Only basic simplification
        out_dir = c.ABLATION_VHDL_DIR.joinpath(f"{prog}_simpl")
        sbt_tasks.append(
            f"Test/runMain {c.STORED_PROGRAM_COMPILER} {prog} --out-dir {out_dir} --overwrite"
            " --opt:no-fuse --opt:no-match-latency"
        )
        # Latency matching
        out_dir = c.ABLATION_VHDL_DIR.joinpath(f"{prog}_latmatch")
        sbt_tasks.append(
            f"Test/runMain {c.STORED_PROGRAM_COMPILER} {prog} --out-dir {out_dir} --overwrite"
            " --opt:no-fuse"
        )
        # Fusion
        out_dir = c.ABLATION_VHDL_DIR.joinpath(f"{prog}_fuse")
        sbt_tasks.append(
            f"Test/runMain {c.STORED_PROGRAM_COMPILER} {prog} --out-dir {out_dir} --overwrite"
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
