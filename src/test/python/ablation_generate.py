#!/usr/bin/env python3

"""
This script generates VHDL projects for the given pre-written programs.
"""

import os
import subprocess
from argparse import ArgumentParser, Namespace

import lib.constants as c
from lib.optimization_level import OptimizationLevel


def main(programs: list[str], levels: list[OptimizationLevel], skip_sbt: bool = False) -> None:
    """
    Script entry point.
    """
    print("-" * 80)
    print("- Generating projects...")
    print(f"- Programs : {', '.join(programs)}")
    print(f"- Levels   : {', '.join([lvl.value for lvl in levels])}")
    print("-" * 80)

    c.ABLATION_VHDL_DIR.mkdir(exist_ok=True, parents=True)
    c.ABLATION_COMPILE_TIME_DIR.mkdir(exist_ok=True, parents=True)

    os.chdir(c.ROOT_DIR)
    if not skip_sbt:
        subprocess.run(["sbt", "assembly"], check=True)
    for prog in programs:
        for lvl in levels:
            out_dir = c.ABLATION_VHDL_DIR.joinpath(f"{prog}_{lvl}")
            ctime_file = c.ABLATION_COMPILE_TIME_DIR.joinpath(f"{prog}_{lvl}.csv")
            command = [
                "java", "-Xss8m", "-jar", c.JAR_PATH.as_posix(),
                "-s", "stored", "-i", prog,
                "--out:vhdl", out_dir.as_posix(), "--overwrite",
                "--out:vhdl:family", "Arria 10",
                "--out:vhdl:device", "10AX115N2F40E2LG",
                "--out:pp", "-",
                "--out:ctime", ctime_file.as_posix(),
            ] + lvl.flags.split()
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
        help=(
            "the names of the programs to process"
            f" (the ones in the paper are: {' '.join(c.ACTIVE_BENCHES)})"
        )
    )
    parser.add_argument(
        "--lvl",
        nargs="*",
        type=OptimizationLevel,
        help="the optimization levels to test",
    )
    parser.add_argument(
        "--skip-sbt",
        action="store_true",
        help="don't run 'sbt assembly' before invoking the Sirop compiler",
    )
    args = parser.parse_args()
    if not args.programs:
        args.programs = c.ACTIVE_BENCHES
    if not args.lvl:
        args.lvl = c.ACTIVE_OPT_LEVELS
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(
        programs=_args.programs,
        levels=_args.lvl,
        skip_sbt=_args.skip_sbt,
    )
