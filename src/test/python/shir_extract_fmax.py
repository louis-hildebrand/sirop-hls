#!/bin/python3
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


def extract_and_save_fmax_shir(prog: str, writer: csv.DictWriter) -> None:
    """
    Extract fmax for the given benchmark and save the result.
    """
    print(
        f"Extracting fmax for {prog} (SHIR)... ",
        flush=True,
        end="",
    )
    project_dir = c.SHIR_SHIR_VHDL_DIR.joinpath(f"{prog}")
    fmax = fm.extract_fmax(project_dir)
    print("failed" if fmax is None else "OK")
    crud.save_fmax_estimate(writer, BenchmarkImpl(Benchmark(prog, Fraction(-1)), "shir"), fmax)


def extract_and_save_fmax_sirop(prog: str, writer: csv.DictWriter) -> None:
    """
    Extract fmax for the given benchmark and save the result.
    """
    print(
        f"Extracting fmax for {prog} (Sirop)... ",
        flush=True,
        end="",
    )
    project_dir = c.SHIR_SIROP_VHDL_DIR.joinpath(f"{prog}")
    fmax = fm.extract_fmax(project_dir)
    print("failed" if fmax is None else "OK")
    crud.save_fmax_estimate(writer, BenchmarkImpl(Benchmark(prog, Fraction(-1)), "sirop"), fmax)


def main(programs: list[str], skip_shir: bool, skip_sirop: bool) -> None:
    """
    Script entry point.
    """
    out_path = c.SHIR_FMAX_CSV
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
            if not skip_shir:
                for prog_name in programs:
                    extract_and_save_fmax_shir(prog_name, writer=writer)
                    out_file.flush()
            if not skip_sirop:
                for prog_name in programs:
                    extract_and_save_fmax_sirop(prog_name, writer=writer)
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
