#!/usr/bin/env python3

"""
This script extracts Quartus' estimate of the maximum clock frequency for benchmarks which have
already been synthesized.
"""

import csv
import shutil
from argparse import ArgumentParser, Namespace
from typing import TextIO

import lib.constants as c
import lib.fmax as fm
import lib.list_benchmarks as lb
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl


def main(bench_names: list[str], save_to_csv: bool, skip_verilog: bool, skip_vhdl: bool) -> None:
    """
    Script entry point.
    """
    benchmarks = sorted([Benchmark.parse(b) for b in bench_names])

    out_path = c.FMAX_ESTIMATE_CSV
    out_path.parent.mkdir(exist_ok=True)
    backup_out_path = out_path.with_suffix(out_path.suffix + ".bak")
    if out_path.exists():
        shutil.copy(src=out_path, dst=backup_out_path)

    c.VHDL_DIR.mkdir(exist_ok=True)
    c.VERILOG_DIR.mkdir(exist_ok=True, parents=True)

    print("-" * 80)
    print("- Extracting fmax...")
    print(f"- Benchmarks  : {', '.join(bench_names)}")
    print(f"- Output file : {out_path.as_posix()}")
    print("-" * 80)

    out_file: TextIO | None = None
    writer: csv.DictWriter | None = None
    try:
        if save_to_csv:
            # pylint: disable=consider-using-with
            out_file = open(out_path, "w", encoding="utf-8", newline="")
            writer = csv.DictWriter(
                out_file,
                fieldnames=crud.FMAX_ESTIMATE_HEADERS,
            )
            writer.writeheader()
        for bench in benchmarks:
            if not skip_verilog:
                print(
                    f"Measuring fmax for {bench.full_name} (Verilog)... ",
                    flush=True,
                    end="",
                )
                fmax = fm.extract_fmax(c.VERILOG_DIR.joinpath(bench.full_name))
                print("not found" if fmax is None else f"{fmax} MHz")
                if save_to_csv:
                    assert out_file is not None
                    assert writer is not None
                    crud.save_fmax_estimate(writer, BenchmarkImpl(bench, "verilog"), fmax)
            if not skip_vhdl:
                print(
                    f"Measuring fmax for {bench.full_name} (VHDL)... ",
                    flush=True,
                    end="",
                )
                fmax = fm.extract_fmax(c.VHDL_DIR.joinpath(bench.full_name))
                print("not found" if fmax is None else f"{fmax} MHz")
                if save_to_csv:
                    assert out_file is not None
                    assert writer is not None
                    crud.save_fmax_estimate(writer, BenchmarkImpl(bench, "vhdl"), fmax)
    finally:
        if out_file is not None:
            out_file.close()
        if save_to_csv:
            crud.merge_fmax_estimates(old=backup_out_path, new=out_path)


def parse_args() -> Namespace:
    """
    Parse the command line arguments
    """
    parser = ArgumentParser(
        description=(
            "This script extracts the estimated maximum clock frequency"
            " for a design that has already been synthesized."
        )
    )
    parser.add_argument(
        "benchmarks",
        nargs="*",
        help=(
            "the benchmarks to process"
            f"(the ones in the paper can be selected by {lb.ACTIVE_BENCH_GLOB})"
        ),
    )
    parser.add_argument(
        "--no-save",
        action="store_true",
        help="don't write the results to the CSV file"
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
        save_to_csv=not _args.no_save,
        skip_verilog=_args.skip_verilog,
        skip_vhdl=_args.skip_vhdl,
    )
