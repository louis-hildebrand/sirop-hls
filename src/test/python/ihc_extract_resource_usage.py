#!/usr/bin/env python3
"""
This script extracts the resource usage for all the given programs.
"""
import csv
import shutil
from argparse import ArgumentParser, Namespace
from fractions import Fraction

import lib.constants as c
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl
from lib.resource_usage import extract_resource_usage


def extract_and_save_resource_usage(prog: str, writer: csv.DictWriter) -> None:
    """
    Extract the resource usage for the given benchmark and save the results.
    """
    print(
        f"Extracting resource usage for {prog}... ",
        flush=True,
        end="",
    )
    project_dir = c.INTEL_HLS_DIR.joinpath(prog, f"{prog}.prj", "quartus")
    ru = extract_resource_usage(project_dir, component="quartus_compile")
    print("failed" if ru is None else "OK")
    crud.save_resource_usage(writer, BenchmarkImpl(Benchmark(prog, Fraction(-1)), "ihc"), ru)


def main(programs: list[str]) -> None:
    """
    Script entry point.
    """
    out_path = c.INTEL_HLS_RESOURCE_USAGE_CSV
    out_path.parent.mkdir(exist_ok=True)
    backup_out_path = out_path.with_suffix(out_path.suffix + ".bak")
    if out_path.exists():
        shutil.copy(src=out_path, dst=backup_out_path)

    print("-" * 80)
    print("- Extracting resource usage...")
    print(f"- Programs  : {', '.join(programs)}")
    print(f"- Output file : {out_path.as_posix()}")
    print("-" * 80)

    try:
        with open(out_path, "w", encoding="utf-8", newline="") as out_file:
            writer = csv.DictWriter(
                out_file,
                fieldnames=crud.RESOURCE_USAGE_HEADERS,
            )
            writer.writeheader()
            for prog_name in programs:
                extract_and_save_resource_usage(prog_name, writer=writer)
                out_file.flush()
    finally:
        crud.merge_resource_usages(old=backup_out_path, new=out_path)

    print()
    print()


def parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(
        description="extracts the resource usages for the given programs"
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
