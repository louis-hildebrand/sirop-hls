#!/usr/bin/env python3
"""
This script extracts the estimated maximum clock frequency for all the given programs.
"""
import csv
import shutil
from argparse import ArgumentParser, Namespace
from fractions import Fraction

import lib.constants as c
import lib.fmax as fm
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl


def extract_and_save_fmax(prog: str, writer: csv.DictWriter) -> None:
    """
    Extract fmax for the given benchmark and save the result.
    """
    print(
        f"Extracting fmax for {prog}... ",
        flush=True,
        end="",
    )
    project_dir = c.IHC_DIR / prog / f"{prog}.prj" / "quartus"
    fmax = fm.extract_fmax(project_dir, top="quartus_compile")
    print("failed" if fmax is None else "OK")
    crud.save_fmax_estimate(writer, BenchmarkImpl(Benchmark(prog, Fraction(-1)), "ihc"), fmax)


def main(programs: list[str]) -> None:
    """
    Script entry point.
    """
    out_path = c.IHC_FMAX_CSV
    out_path.parent.mkdir(exist_ok=True)
    backup_out_path = out_path.with_suffix(out_path.suffix + ".bak")
    if out_path.exists():
        shutil.copy(src=out_path, dst=backup_out_path)

    print("-" * 80)
    print("- Extracting fmax...")
    print(f"- Programs  : {', '.join(programs)}")
    print(f"- Output file : {out_path.as_posix()}")
    print("-" * 80)

    try:
        with open(out_path, "w", encoding="utf-8", newline="") as out_file:
            writer = csv.DictWriter(
                out_file,
                fieldnames=crud.FMAX_ESTIMATE_HEADERS,
            )
            writer.writeheader()
            for prog_name in programs:
                extract_and_save_fmax(prog_name, writer=writer)
                out_file.flush()
    finally:
        crud.merge_fmax_estimates(old=backup_out_path, new=out_path)

    print()
    print()


def parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(
        description="extracts the estimated max clock frequency for the given programs"
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
