#!/usr/bin/env python3

"""
This script measures the latency (in clock cycles) for the given programs by simulation.
"""

import csv
import shutil
import subprocess
from argparse import ArgumentParser, Namespace
from pathlib import Path

import lib.ablation_results_crud as crud
import lib.constants as c
from lib.latency import LatencyResult
from lib.optimization_level import OptimizationLevel
from lib.program_variant import ProgramVariant


def measure_latency(proj_dir: Path) -> LatencyResult:
    """
    Measure the latency of one design.
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


def main(programs: list[str]) -> None:
    """
    Script entry point.
    """
    out_path = c.ABLATION_LATENCY_CSV
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
            for prog_name in programs:
                for opt_lvl in OptimizationLevel:
                    p = ProgramVariant(prog_name, opt_lvl)
                    print(
                        f"Measuring latency for {p.name} ({p.lvl})... ",
                        end="",
                        flush=True,
                    )
                    latency = measure_latency(c.ABLATION_VHDL_DIR.joinpath(f"{p.name}_{p.lvl}"))
                    print(latency)
                    crud.save_latency(writer, p, latency)
    finally:
        crud.merge_latency_results(old=backup_out_path, new=out_path)


def parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(
        description="measures the latency for the given programs"
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
