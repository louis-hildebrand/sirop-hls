#!/bin/python3
"""
This script extracts the compile time for all the given programs.
"""
import csv
import shutil
from argparse import ArgumentParser, Namespace
from typing import TextIO

import lib.constants as c
import lib.results_crud as crud
from lib.benchmark import Benchmark
from lib.compile_time import CompileTimeReport


def extract_and_save_compile_time(
    prog: str,
    writer: csv.DictWriter,
    f: TextIO,
) -> None:
    """
    Extract the compile time for the given benchmark and save the result.
    """
    print(f"Extracting compile time for {prog}... ")
    ctime_file = c.DSE_COMPILE_TIME_DIR.joinpath(f"{prog}.csv")
    ctime = CompileTimeReport.extract(ctime_file)
    crud.save_compile_time(writer, Benchmark.parse(prog), ctime)
    f.flush()


def main(programs: list[str]) -> None:
    """
    Script entry point.
    """
    out_path = c.AETHERLING_COMPILE_TIME_CSV
    out_path.parent.mkdir(exist_ok=True)
    backup_out_path = out_path.with_suffix(out_path.suffix + ".bak")
    if out_path.exists():
        shutil.copy(src=out_path, dst=backup_out_path)

    print("-" * 80)
    print("- Extracting compile time...")
    print(f"- Programs  : {', '.join(programs)}")
    print(f"- Output file : {out_path.as_posix()}")
    print("-" * 80)

    try:
        with open(out_path, "w", encoding="utf-8", newline="") as out_file:
            writer = csv.DictWriter(
                out_file,
                fieldnames=crud.COMPILE_TIME_HEADERS,
            )
            writer.writeheader()
            for prog_name in programs:
                extract_and_save_compile_time(prog_name, writer=writer, f=out_file)
    finally:
        crud.merge_compile_times(old=backup_out_path, new=out_path)

    print()
    print()


def parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(
        description="extracts the compile times for the given programs"
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
