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


def extract_and_save_resource_usage_shir(prog: str, writer: csv.DictWriter) -> None:
    """
    Extract the resource usage for the given benchmark and save the results.
    """
    print(
        f"Extracting resource usage for {prog} (SHIR)... ",
        flush=True,
        end="",
    )
    project_dir = c.SHIR_SHIR_VHDL_DIR.joinpath(prog)
    ru = extract_resource_usage(project_dir)
    print("failed" if ru is None else "OK")
    crud.save_resource_usage(writer, BenchmarkImpl(Benchmark(prog, Fraction(-1)), "shir"), ru)


def extract_and_save_resource_usage_sirop(prog: str, writer: csv.DictWriter) -> None:
    """
    Extract the resource usage for the given benchmark and save the results.
    """
    print(
        f"Extracting resource usage for {prog} (Sirop)... ",
        flush=True,
        end="",
    )
    project_dir = c.SHIR_SIROP_VHDL_DIR.joinpath(prog)
    ru = extract_resource_usage(project_dir)
    print("failed" if ru is None else "OK")
    crud.save_resource_usage(writer, BenchmarkImpl(Benchmark(prog, Fraction(-1)), "sirop"), ru)


def main(programs: list[str], skip_shir: bool, skip_sirop: bool) -> None:
    """
    Script entry point.
    """
    out_path = c.SHIR_RESOURCE_USAGE_CSV
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
            if not skip_shir:
                for prog_name in programs:
                    extract_and_save_resource_usage_shir(prog_name, writer=writer)
                    out_file.flush()
            if not skip_sirop:
                for prog_name in programs:
                    extract_and_save_resource_usage_sirop(prog_name, writer=writer)
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
        "--skip-shir",
        action="store_true",
        help="skip the SHIR designs",
    )
    parser.add_argument(
        "--skip-sirop",
        action="store_true",
        help="skip the Sirop designs",
    )
    parser.add_argument(
        "programs",
        nargs="*",
        help=(
            "the names of the programs to process"
            f" (the ones in the paper are: {' '.join(c.ACTIVE_BENCHES)})"
        )
    )
    args = parser.parse_args()
    if not args.programs:
        args.programs = c.ACTIVE_BENCHES
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(
        _args.programs,
        skip_shir=_args.skip_shir,
        skip_sirop=_args.skip_sirop,
    )
