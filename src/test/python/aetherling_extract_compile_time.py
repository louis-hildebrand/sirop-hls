#!/usr/bin/env python3
"""
This script extracts the compile times for all the given benchmarks.
"""
import csv
import shutil
from argparse import ArgumentParser, Namespace
from typing import TextIO

import lib.constants as c
import lib.list_benchmarks as lb
import lib.results_crud as crud
from lib.benchmark import Benchmark
from lib.compile_time import CompileTimeReport


def extract_and_save_compile_time(
    bench: Benchmark,
    writer: csv.DictWriter,
    f: TextIO,
) -> None:
    """
    Extract the compile time for the given benchmark and save the results.
    """
    print(f"Getting compile time for {bench.full_name}... ")
    ctime_file = c.AETHERLING_COMPILE_TIME_DIR.joinpath(f"{bench.full_name}.csv")
    ctime = CompileTimeReport.extract(ctime_file)
    crud.save_compile_time(writer, bench, ctime)
    f.flush()


def main(bench_names: list[str]) -> None:
    """
    Script entry point.
    """
    benchmarks = sorted([Benchmark.parse(b) for b in bench_names])

    out_path = c.AETHERLING_COMPILE_TIME_CSV
    out_path.parent.mkdir(exist_ok=True)
    backup_out_path = out_path.with_suffix(out_path.suffix + ".bak")
    if out_path.exists():
        shutil.copy(src=out_path, dst=backup_out_path)

    print("-" * 80)
    print("- Extracting compile times...")
    print(f"- Benchmarks  : {', '.join(bench_names)}")
    print(f"- Output file : {out_path.as_posix()}")
    print("-" * 80)

    try:
        with open(out_path, "w", encoding="utf-8", newline="") as out_file:
            writer = csv.DictWriter(
                out_file,
                fieldnames=crud.COMPILE_TIME_HEADERS,
            )
            writer.writeheader()
            for bench in benchmarks:
                extract_and_save_compile_time(
                    bench,
                    writer=writer,
                    f=out_file,
                )
    finally:
        crud.merge_compile_times(old=backup_out_path, new=out_path)

    print()
    print()


def parse_args() -> Namespace:
    """
    Parse the command line arguments
    """
    parser = ArgumentParser(
        description=(
            "This script extracts the compile time report for the given benchmarks."
        )
    )
    parser.add_argument(
        "benchmarks",
        nargs="*",
        help=(
            "the benchmarks to process"
            f" (the ones in the paper can be selected by {lb.ACTIVE_BENCH_GLOB})"
        ),
    )
    args = parser.parse_args()
    args.benchmarks = lb.names_from_args(args.benchmarks)
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(bench_names=_args.benchmarks)
