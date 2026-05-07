#!/usr/bin/env python3

"""
This script measures the latency (in clock cycles) for the given benchmarks by simulation.
"""

import csv
import shutil
import subprocess
from argparse import ArgumentParser, Namespace
from pathlib import Path

import lib.constants as c
import lib.list_benchmarks as lb
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl
from lib.latency import LatencyResult


def measure_latency(proj_dir: Path) -> LatencyResult:
    """
    Measure the latency of one design.
    """
    result = subprocess.run(
        [c.TEST_SH_DIR.joinpath("measure_latency_with_logging.sh"), proj_dir.resolve().as_posix()],
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


def main(bench_names: list[str], skip_chisel: bool, skip_sirop: bool) -> None:
    """
    Script entry point.
    """
    benchmarks = sorted([Benchmark.parse(b) for b in bench_names])

    out_path = c.AETHERLING_LATENCY_CSV
    out_path.parent.mkdir(exist_ok=True)
    backup_out_path = out_path.with_suffix(out_path.suffix + ".bak")
    if out_path.exists():
        shutil.copy(src=out_path, dst=backup_out_path)

    c.AETHERLING_VHDL_DIR.mkdir(exist_ok=True)
    c.CHISEL_VERILOG_DST_DIR.mkdir(exist_ok=True, parents=True)

    print("-" * 80)
    print("- Measuring latency...")
    print(f"- Benchmarks  : {', '.join(bench_names)}")
    print(f"- Output file : {out_path.as_posix()}")
    print("-" * 80)

    try:
        with open(out_path, "w", encoding="utf-8", newline="") as out_file:
            writer = csv.DictWriter(out_file, fieldnames=crud.LATENCY_HEADERS)
            writer.writeheader()
            for bench in benchmarks:
                if not skip_chisel:
                    print(
                        f"Measuring latency for {bench.full_name} (Verilog)... ",
                        end="",
                        flush=True,
                    )
                    latency = measure_latency(c.CHISEL_VERILOG_DST_DIR.joinpath(bench.full_name))
                    print(latency)
                    crud.save_latency(writer, BenchmarkImpl(bench, "verilog"), latency)
                if not skip_sirop:
                    print(
                        f"Measuring latency for {bench.full_name} (VHDL)... ",
                        end="",
                        flush=True,
                    )
                    latency = measure_latency(c.AETHERLING_VHDL_DIR.joinpath(bench.full_name))
                    print(latency)
                    crud.save_latency(writer, BenchmarkImpl(bench, "vhdl"), latency)
    finally:
        crud.merge_latency_results(old=backup_out_path, new=out_path)


def parse_args() -> Namespace:
    """
    Parse the command line arguments
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
        "benchmarks",
        nargs="*",
        help=(
            "the benchmarks to process"
            f" (the ones in the paper can be selected by {lb.ACTIVE_BENCH_GLOB})"
        ),
    )
    parser.add_argument(
        "--skip-chisel",
        action="store_true",
        help="don't process the Chisel-generated Verilog projects",
    )
    parser.add_argument(
        "--skip-sirop",
        action="store_true",
        help="don't process the Sirop-generated VHDL projects",
    )
    args = parser.parse_args()
    args.benchmarks = lb.names_from_args(args.benchmarks)
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(
        bench_names=_args.benchmarks,
        skip_chisel=_args.skip_chisel,
        skip_sirop=_args.skip_sirop,
    )
