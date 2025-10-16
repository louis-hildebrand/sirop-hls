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
    c.DSE_COMPILE_TIME_DIR.mkdir(exist_ok=True, parents=True)

    os.chdir(c.ROOT_DIR)
    subprocess.run(["sbt", "assembly"], check=True)
    for prog in programs:
        out_dir = c.DSE_VHDL_DIR.joinpath(prog)
        ctime_file = c.DSE_COMPILE_TIME_DIR.joinpath(f"{prog}.csv")
        lvl = OptimizationLevel.ALL
        command = (
            f"java -jar {c.JAR_PATH} -s stored -i {prog}"
            f" --out:vhdl {out_dir} --overwrite"
            f" --out:pp -"
            f" --out:ctime {ctime_file}"
            f" {lvl.flags}"
        ).split(" ")
        command = [x for x in command if x]
        print()
        print(f"Running : {' '.join(command)}")
        subprocess.run(command, check=True)


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
