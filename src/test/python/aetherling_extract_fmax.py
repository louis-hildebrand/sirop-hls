#!/bin/python3

"""
This script measures the maximum clock frequency for each of the given benchmarks.
"""

import csv
import re
import shutil
import sys
from pathlib import Path
from typing import TextIO

import lib.constants as c
import lib.list_benchmarks as lb
from lib.benchmark import Benchmark, BenchmarkImpl
from lib.read_results import read_all_fmax_results


def get_sta_path(proj_dir: Path) -> Path | None:
    """
    Find the path to the .sta.rpt file in the given project.
    """
    sta_path = proj_dir.joinpath("output_files", "top.sta.rpt")
    if sta_path.exists():
        return sta_path
    sta_path = proj_dir.joinpath("output_files", "Top.sta.rpt")
    if sta_path.exists():
        return sta_path
    return None


def extract_all_fmaxes(sta_path: Path) -> list[float]:
    """
    Extract all the fmax values from the given .sta.rpt file.
    """
    results: list[float] = []
    with open(sta_path, "r", encoding="utf-8") as f:
        while True:
            line = next(f, None)
            if line is None:
                break
            # Strip trailing \n
            line = line[:-1]
            if re.match(r'; .* Fmax Summary', line):
                line = next(f)[:-1]
                if not re.fullmatch(r'\+-+\+-+\+-+\+-+\+', line):
                    raise RuntimeError(f"Unrecognized line: {line}")
                line = next(f)[:-1]
                pattern = r';\s+Fmax\s+;\s+Restricted Fmax\s+;\s+Clock Name\s+;\s+Note\s+;'
                if not re.fullmatch(pattern, line):
                    raise RuntimeError(f"Unrecognized line: {line}")
                line = next(f)[:-1]
                if not re.fullmatch(r'\+-+\+-+\+-+\+-+\+', line):
                    raise RuntimeError(f"Unrecognized line: {line}")
                line = next(f)[:-1]
                m = re.match(r'; ([\d.]+) MHz\s+;\s+([\d.]+) MHz', line)
                if not m:
                    raise ValueError(f"Failed to extract fmax from line: {line}")
                fmax_1 = float(m[1])
                fmax_2 = float(m[2])
                results += [fmax_1, fmax_2]
    return results


def extract_fmax(proj_dir: Path) -> float | None:
    """
    Find the maximum clock frequency for the given project.
    """
    sta_path = get_sta_path(proj_dir)
    if sta_path is None:
        return None
    fmaxes = extract_all_fmaxes(sta_path)
    if not fmaxes:
        raise ValueError(f"Failed to extract fmax from {sta_path}.")
    return min(fmaxes)


def save_result(writer: csv.DictWriter, b: BenchmarkImpl, fmax: float | None) -> None:
    """
    Save one result to a CSV file.
    """
    writer.writerow({
        "bench_name": b.bench.name,
        "bench_throughput": b.bench.throughput_str,
        "language": b.language.lower(),
        "fmax": "" if fmax is None else fmax
    })


def extract_and_save_fmax(
    b: BenchmarkImpl,
    writer: csv.DictWriter,
    f: TextIO,
) -> None:
    """
    Extract the maximum clock frequency for the given benchmark and save the
    results.
    """
    bench = b.bench
    print(
        f"Measuring fmax for {bench.full_name} ({b.language})... ",
        flush=True,
        end="",
    )
    project_dir = (
        c.VERILOG_DIR.joinpath(bench.full_name)
        if b.language.lower() == "verilog"
        else c.VHDL_DIR.joinpath(bench.full_name)
    )
    fmax = extract_fmax(project_dir)
    print("failed" if fmax is None else "OK")
    save_result(writer, b, fmax)
    f.flush()


def merge_results(old: Path, new: Path) -> None:
    """
    Combine the old and new results.

    If a given benchmark has both an old result and a new result, only the new
    result will be kept.
    """
    if not old.exists():
        return
    old_results = read_all_fmax_results(old)
    new_results = read_all_fmax_results(new)
    combined_results = old_results | new_results
    with open(new, "w", encoding="utf-8", newline="") as f:
        writer = csv.DictWriter(
            f,
            fieldnames=["bench_name", "bench_throughput", "language", "fmax"]
        )
        writer.writeheader()
        for b, ru in combined_results.items():
            save_result(writer, b, ru)
    old.unlink()


def main(bench_names: list[str]) -> None:
    """
    Script entry point.
    """
    benchmarks = sorted([Benchmark.parse(b) for b in bench_names])

    out_path = c.FMAX_CSV
    out_path.parent.mkdir(exist_ok=True)
    backup_out_path = out_path.with_suffix(out_path.suffix + ".bak")
    if out_path.exists():
        shutil.copy(src=out_path, dst=backup_out_path)

    c.VHDL_DIR.mkdir(exist_ok=True)
    c.VERILOG_DIR.mkdir(exist_ok=True, parents=True)

    print("-" * 80)
    print("- Measuring fmax...")
    print(f"- Benchmarks  : {', '.join(bench_names)}")
    print(f"- Output file : {out_path.as_posix()}")
    print("-" * 80)

    try:
        with open(out_path, "w", encoding="utf-8", newline="") as out_file:
            writer = csv.DictWriter(
                out_file,
                fieldnames=["bench_name", "bench_throughput", "language", "fmax"]
            )
            writer.writeheader()
            for bench in benchmarks:
                extract_and_save_fmax(
                    BenchmarkImpl(bench, "verilog"),
                    writer=writer,
                    f=out_file,
                )
                extract_and_save_fmax(
                    BenchmarkImpl(bench, "vhdl"),
                    writer=writer,
                    f=out_file,
                )
    finally:
        merge_results(old=backup_out_path, new=out_path)


if __name__ == "__main__":
    main(lb.names_from_args(sys.argv[1:]))
