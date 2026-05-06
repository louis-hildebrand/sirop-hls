#!/usr/bin/env python3

"""
This script generates VHDL projects for the given pre-written programs.
"""

import os
import subprocess
from argparse import ArgumentParser, Namespace
from pathlib import Path

import lib.constants as c
from lib.optimization_level import OptimizationLevel


def proj_dir(prog: str, lvl: OptimizationLevel) -> Path:
    """
    Find the VHDL project directory for the given benchmark and settings.
    """
    return c.ABLATION_VHDL_DIR.joinpath(f"{prog}_{lvl}")


def generate_design(prog: str, lvl: OptimizationLevel) -> None:
    """
    Generate VHDL project using the Sirop compiler with the given optimization settings.
    """
    ctime_file = c.ABLATION_COMPILE_TIME_DIR.joinpath(f"{prog}_{lvl}.csv")
    command = [
        # Increase stack size because some poorly-optimized programs might need it
        "java", "-Xss8m", "-jar", c.JAR_PATH.as_posix(),
        "-s", "stored", "-i", prog,
        "--out:vhdl", proj_dir(prog, lvl).as_posix(), "--overwrite",
        "--out:vhdl:family", "Arria 10",
        "--out:vhdl:device", "10AX115N2F40E2LG",
        "--out:pp", "-",
        "--out:ctime", ctime_file.as_posix(),
    ] + lvl.flags.split()
    command = [x for x in command if x]
    print()
    print(f"Running : {' '.join(command)}")
    subprocess.run(command, check=True)


def generate_testbenches(programs: list[str], levels: list[OptimizationLevel]) -> None:
    """
    Add testbenches to the VHDL projects.
    """
    os.chdir(c.ROOT_DIR)
    cls = c.STORED_TESTBENCH_GEN_CLS
    sbt_command = "; ".join([
        f"Test/runMain {cls} {proj_dir(prog, lvl).resolve().as_posix()}"
        for prog in programs
        for lvl in levels
    ])
    subprocess.run(["sbt", sbt_command], check=True)


def main(programs: list[str], levels: list[OptimizationLevel], recompile_sirop: bool) -> None:
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
    if recompile_sirop:
        subprocess.run(["sbt", "assembly"], check=True)
    for prog in programs:
        for lvl in levels:
            generate_design(prog, lvl)
    generate_testbenches(programs, levels)


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
    valid_levels = [lvl.value for lvl in c.ACTIVE_OPT_LEVELS]
    parser.add_argument(
        "--lvl",
        nargs="*",
        type=OptimizationLevel,
        help=(
            "the optimization levels to test"
            f" (the ones in the paper are: {' '.join(valid_levels)})"
        ),
    )
    parser.add_argument(
        "--recompile-sirop",
        action="store_true",
        help="run 'sbt assembly' before invoking the Sirop compiler",
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
        recompile_sirop=_args.recompile_sirop,
    )
