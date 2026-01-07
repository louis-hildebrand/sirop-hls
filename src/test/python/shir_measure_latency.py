#!/usr/bin/env python3

"""
This script measures the latency (in clock cycles) for the given programs by simulation.
"""

import csv
import shutil
import subprocess
from argparse import ArgumentParser, Namespace
from fractions import Fraction
from pathlib import Path

import lib.constants as c
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl
from lib.latency import LatencyResult


def measure_latency_shir(proj_dir: Path) -> LatencyResult:
    """
    Measure the latency of one SHIR design.
    """
    result = subprocess.run(
        [
            c.TEST_SH_DIR.joinpath("shir_measure_latency_with_logging.sh"),
            proj_dir.resolve().as_posix()
        ],
        check=False,
        capture_output=True,
        text=True,
        encoding="utf-8",
    )
    sim_success = result.returncode == 0
    try:
        latency = int(result.stdout)
    except ValueError:
        latency = None
    return LatencyResult(latency=latency, sim_success=sim_success)


def measure_and_save_latency_shir(prog_name: str, writer: csv.DictWriter) -> None:
    """
    Measure the latency of a SHIR design and save it.
    """
    print(
        f"Measuring latency for {prog_name} (SHIR)... ",
        end="",
        flush=True,
    )
    latency = measure_latency_shir(c.SHIR_SHIR_VHDL_DIR.joinpath(prog_name))
    print(latency)
    bi = BenchmarkImpl(Benchmark(prog_name, Fraction(-1)), "shir")
    crud.save_latency(writer, bi, latency)


def measure_latency_sirop(proj_dir: Path) -> LatencyResult:
    """
    Measure the latency of one Sirop design.
    """
    result = subprocess.run(
        [
            c.TEST_SH_DIR.joinpath("ablation_measure_latency_with_logging.sh"),
            proj_dir.resolve().as_posix()
        ],
        check=False,
        capture_output=True,
        text=True,
        encoding="utf-8",
    )
    sim_success = result.returncode == 0
    try:
        latency = int(result.stdout)
    except ValueError:
        latency = None
    return LatencyResult(latency=latency, sim_success=sim_success)


def measure_and_save_latency_sirop(prog_name: str, writer: csv.DictWriter) -> None:
    """
    Measure the latency of a Sirop design and save it.
    """
    print(
        f"Measuring latency for {prog_name} (Sirop)... ",
        end="",
        flush=True,
    )
    latency = measure_latency_sirop(c.SHIR_SIROP_VHDL_DIR.joinpath(prog_name))
    print(latency)
    bi = BenchmarkImpl(Benchmark(prog_name, Fraction(-1)), "sirop")
    crud.save_latency(writer, bi, latency)


def main(programs: list[str], skip_shir: bool, skip_sirop: bool) -> None:
    """
    Script entry point.
    """
    out_path = c.SHIR_LATENCY_CSV
    out_path.parent.mkdir(exist_ok=True)
    backup_out_path = out_path.with_suffix(out_path.suffix + ".bak")
    if out_path.exists():
        shutil.copy(src=out_path, dst=backup_out_path)

    print("-" * 80)
    print("- Measuring latency...")
    print(f"- Programs  : {', '.join(programs)}")
    print(f"- Output file : {out_path.as_posix()}")
    print("-" * 80)

    try:
        with open(out_path, "w", encoding="utf-8", newline="") as out_file:
            writer = csv.DictWriter(out_file, fieldnames=crud.LATENCY_HEADERS)
            writer.writeheader()
            if not skip_shir:
                for prog_name in programs:
                    measure_and_save_latency_shir(prog_name, writer)
                    out_file.flush()
            if not skip_sirop:
                for prog_name in programs:
                    measure_and_save_latency_sirop(prog_name, writer)
                    out_file.flush()
    finally:
        crud.merge_latency_results(old=backup_out_path, new=out_path)


def parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(
        description=(
            """
            This script measures the latency (in clock cycles) for the given benchmarks
            by simulation.
            """
        )
    )
    parser.add_argument(
        "programs",
        nargs="*",
        help="the names of the programs to process"
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
    return parser.parse_args()


if __name__ == "__main__":
    _args = parse_args()
    main(
        _args.programs,
        skip_shir=_args.skip_shir,
        skip_sirop=_args.skip_sirop,
    )
