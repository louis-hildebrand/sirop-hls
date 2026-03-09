#!/usr/bin/env python3
"""
This script extracts the resource usage for all the given programs.
"""
import csv
import shutil
from argparse import ArgumentParser, Namespace
from typing import TextIO

import lib.ablation_results_crud as crud
import lib.constants as c
from lib.optimization_level import OptimizationLevel
from lib.program_variant import ProgramVariant
from lib.resource_usage import extract_resource_usage


def extract_and_save_resource_usage(
    p: ProgramVariant,
    writer: csv.DictWriter,
    f: TextIO,
) -> None:
    """
    Extract the resource usage for the given benchmark and save the results.
    """
    print(
        f"Measuring resource usage for {p.name} ({p.lvl})... ",
        flush=True,
        end="",
    )
    project_dir = c.ABLATION_VHDL_DIR.joinpath(f"{p.name}_{p.lvl}")
    ru = extract_resource_usage(project_dir)
    print("failed" if ru is None else "OK")
    crud.save_resource_usage(writer, p, ru)
    f.flush()


def main(programs: list[str], levels: list[OptimizationLevel]) -> None:
    """
    Script entry point.
    """
    out_path = c.ABLATION_RESOURCE_USAGE_CSV
    out_path.parent.mkdir(exist_ok=True)
    backup_out_path = out_path.with_suffix(out_path.suffix + ".bak")
    if out_path.exists():
        shutil.copy(src=out_path, dst=backup_out_path)

    print("-" * 80)
    print("- Extracting resource usage...")
    print(f"- Programs    : {', '.join(programs)}")
    print(f"- Levels      : {', '.join([lvl.value for lvl in levels])}")
    print(f"- Output file : {out_path.as_posix()}")
    print("-" * 80)

    try:
        with open(out_path, "w", encoding="utf-8", newline="") as out_file:
            writer = csv.DictWriter(
                out_file,
                fieldnames=crud.RESOURCE_USAGE_HEADERS,
            )
            writer.writeheader()
            for prog_name in programs:
                for opt_lvl in levels:
                    extract_and_save_resource_usage(
                        ProgramVariant(prog_name, opt_lvl),
                        writer=writer,
                        f=out_file,
                    )
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
        "programs",
        nargs="*",
        help=(
            "the names of the programs to process"
            f" (the ones in the paper are: {' '.join(c.ACTIVE_BENCHES)})"
        )
    )
    parser.add_argument(
        "--lvl",
        nargs="*",
        type=OptimizationLevel,
        help="the optimization levels to test",
    )
    args = parser.parse_args()
    if not args.programs:
        args.programs = c.ACTIVE_BENCHES
    if not args.lvl:
        args.lvl = list(OptimizationLevel)
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(
        programs=_args.programs,
        levels=_args.lvl,
    )
