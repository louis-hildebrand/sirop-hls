#!/usr/bin/env python3
"""
This script extracts the resource usage for all the given benchmarks.
"""
import csv
import shutil
from argparse import ArgumentParser, Namespace
from typing import TextIO

import lib.constants as c
import lib.list_benchmarks as lb
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl
from lib.resource_usage import extract_resource_usage


def extract_and_save_resource_usage(
    b: BenchmarkImpl,
    writer: csv.DictWriter,
    f: TextIO,
) -> None:
    """
    Extract the resource usage for the given benchmark and save the results.
    """
    bench = b.bench
    print(
        f"Measuring resource usage for {bench.full_name} ({b.language})... ",
        flush=True,
        end="",
    )
    project_dir = (
        c.VERILOG_DIR.joinpath(bench.full_name)
        if b.language.lower() == "verilog"
        else c.VHDL_DIR.joinpath(bench.full_name)
    )
    ru = extract_resource_usage(project_dir)
    print("failed" if ru is None else "OK")
    crud.save_resource_usage(writer, b, ru)
    f.flush()


def main(bench_names: list[str], skip_verilog: bool, skip_vhdl: bool) -> None:
    """
    Script entry point.
    """
    benchmarks = sorted([Benchmark.parse(b) for b in bench_names])

    out_path = c.RESOURCE_USAGE_CSV
    out_path.parent.mkdir(exist_ok=True)
    backup_out_path = out_path.with_suffix(out_path.suffix + ".bak")
    if out_path.exists():
        shutil.copy(src=out_path, dst=backup_out_path)

    c.VHDL_DIR.mkdir(exist_ok=True)
    c.VERILOG_DIR.mkdir(exist_ok=True, parents=True)

    print("-" * 80)
    print("- Extracting resource usage...")
    print(f"- Benchmarks  : {', '.join(bench_names)}")
    print(f"- Output file : {out_path.as_posix()}")
    print("-" * 80)

    try:
        with open(out_path, "w", encoding="utf-8", newline="") as out_file:
            writer = csv.DictWriter(
                out_file,
                fieldnames=crud.RESOURCE_USAGE_HEADERS,
            )
            writer.writeheader()
            for bench in benchmarks:
                if not skip_verilog:
                    extract_and_save_resource_usage(
                        BenchmarkImpl(bench, "verilog"),
                        writer=writer,
                        f=out_file,
                    )
                if not skip_vhdl:
                    extract_and_save_resource_usage(
                        BenchmarkImpl(bench, "vhdl"),
                        writer=writer,
                        f=out_file,
                    )
    finally:
        crud.merge_resource_usages(old=backup_out_path, new=out_path)

    print()
    print()


def parse_args() -> Namespace:
    """
    Parse the command line arguments
    """
    parser = ArgumentParser(
        description=(
            "This script extracts the resource usage"
            " for a design that has already been synthesized."
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
    parser.add_argument(
        "--skip-verilog",
        action="store_true",
        help="don't process the Verilog implementation of the benchmarks"
    )
    parser.add_argument(
        "--skip-vhdl",
        action="store_true",
        help="don't process the VHDL implementation of the benchmarks"
    )
    args = parser.parse_args()
    args.benchmarks = lb.names_from_args(args.benchmarks)
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(
        bench_names=_args.benchmarks,
        skip_verilog=_args.skip_verilog,
        skip_vhdl=_args.skip_vhdl,
    )
