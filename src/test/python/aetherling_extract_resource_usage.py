#!/bin/python3
"""
This script extracts the resource usage for all the given benchmarks.
"""
import csv
import os
import shutil
import subprocess
import sys
from pathlib import Path
from subprocess import CalledProcessError
from typing import TextIO

import lib.constants as c
import lib.list_benchmarks as lb
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl
from lib.resource_usage import ResourceUsage


def extract_resource_usage(project_dir: Path) -> ResourceUsage | None:
    """
    Extract the resource usage for a given design.
    """
    os.chdir(project_dir)
    try:
        result = subprocess.run(
            [c.TEST_SH_DIR.joinpath("extract_alm_count.sh")],
            check=True, capture_output=True, text=True, encoding="utf-8"
        )
        alm = int(result.stdout)
        result = subprocess.run(
            [c.TEST_SH_DIR.joinpath("extract_bram_count.sh")],
            check=True, capture_output=True, text=True, encoding="utf-8"
        )
        bram = int(result.stdout)
        result = subprocess.run(
            [c.TEST_SH_DIR.joinpath("extract_dsp_count.sh")],
            check=True, capture_output=True, text=True, encoding="utf-8"
        )
        dsp = int(result.stdout)
    except CalledProcessError:
        return None
    return ResourceUsage(alm=alm, bram=bram, dsp=dsp)


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


def main(bench_names: list[str]) -> None:
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
                fieldnames=["bench_name", "bench_throughput", "language", "alm", "bram", "dsp"]
            )
            writer.writeheader()
            for bench in benchmarks:
                extract_and_save_resource_usage(
                    BenchmarkImpl(bench, "verilog"),
                    writer=writer,
                    f=out_file,
                )
                extract_and_save_resource_usage(
                    BenchmarkImpl(bench, "vhdl"),
                    writer=writer,
                    f=out_file,
                )
    finally:
        crud.merge_resource_usages(old=backup_out_path, new=out_path)

    print()
    print()


if __name__ == "__main__":
    main(lb.names_from_args(sys.argv[1:]))
