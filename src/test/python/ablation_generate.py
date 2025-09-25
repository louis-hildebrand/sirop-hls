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

    c.ABLATION_VHDL_DIR.mkdir(exist_ok=True, parents=True)
    sbt_tasks = []
    for prog in programs:
        # No optimizations
        lvl = OptimizationLevel.NONE
        out_dir = c.ABLATION_VHDL_DIR.joinpath(f"{prog}_{lvl}")
        sbt_tasks.append(
            f"Test/runMain {c.STORED_PROGRAM_COMPILER} {prog}"
            f" --out-dir {out_dir} --overwrite"
            " --show-final"
            " --opt:no-fuse --opt:no-match-latency --opt:no-simplify"
            " --opt:max-let-buf-size 100 --opt:assume-throughputs-match"
        )
        # Only basic simplification
        lvl = OptimizationLevel.SIMPLIFY
        out_dir = c.ABLATION_VHDL_DIR.joinpath(f"{prog}_{lvl}")
        sbt_tasks.append(
            f"Test/runMain {c.STORED_PROGRAM_COMPILER} {prog}"
            f" --out-dir {out_dir} --overwrite"
            " --show-final"
            " --opt:no-fuse --opt:no-match-latency"
            " --opt:max-let-buf-size 100 --opt:assume-throughputs-match"
        )
        # Latency matching
        lvl = OptimizationLevel.MATCH_LATENCY
        out_dir = c.ABLATION_VHDL_DIR.joinpath(f"{prog}_{lvl}")
        sbt_tasks.append(
            f"Test/runMain {c.STORED_PROGRAM_COMPILER} {prog}"
            f" --out-dir {out_dir} --overwrite"
            " --show-final"
            " --opt:no-fuse"
            " --opt:max-let-buf-size 100 --opt:assume-throughputs-match"
        )
        # Fusion
        lvl = OptimizationLevel.FUSE
        out_dir = c.ABLATION_VHDL_DIR.joinpath(f"{prog}_{lvl}")
        sbt_tasks.append(
            f"Test/runMain {c.STORED_PROGRAM_COMPILER} {prog}"
            f" --out-dir {out_dir} --overwrite"
            " --show-final"
            " --opt:max-let-buf-size 100 --opt:assume-throughputs-match"
        )
        # All except stream simplification
        lvl = OptimizationLevel.ALL_EXCEPT_SIMPL
        out_dir = c.ABLATION_VHDL_DIR.joinpath(f"{prog}_{lvl}")
        sbt_tasks.append(
            f"Test/runMain {c.STORED_PROGRAM_COMPILER} {prog}"
            f" --out-dir {out_dir} --overwrite"
            " --show-final"
            " --opt:no-simplify"
            " --opt:max-let-buf-size 100 --opt:assume-throughputs-match"
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
