#!/usr/bin/env python3
"""
This script extracts the estimated maximum clock frequency for all the given programs.
"""
import csv
import shutil
from argparse import ArgumentParser, Namespace
from typing import TextIO

import lib.ablation_results_crud as crud
import lib.constants as c
import lib.fmax as fm
from lib.optimization_level import OptimizationLevel
from lib.program_variant import ProgramVariant


def extract_and_save_fmax(
    p: ProgramVariant,
    writer: csv.DictWriter,
    f: TextIO,
) -> None:
    """
    Extract fmax for the given benchmark and save the result.
    """
    print(
        f"Extracting fmax for {p.name} ({p.lvl})... ",
        flush=True,
        end="",
    )
    project_dir = c.ABLATION_VHDL_DIR.joinpath(f"{p.name}_{p.lvl}")
    fmax = fm.extract_fmax(project_dir)
    print("failed" if fmax is None else "OK")
    crud.save_fmax(writer, p, fmax)
    f.flush()


def main(programs: list[str]) -> None:
    """
    Script entry point.
    """
    out_path = c.ABLATION_FMAX_CSV
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
                fieldnames=crud.FMAX_HEADERS,
            )
            writer.writeheader()
            for prog_name in programs:
                for opt_lvl in OptimizationLevel:
                    extract_and_save_fmax(
                        ProgramVariant(prog_name, opt_lvl),
                        writer=writer,
                        f=out_file,
                    )
    finally:
        crud.merge_fmax(old=backup_out_path, new=out_path)

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
        help=(
            "the names of the programs to process"
            f" (the ones in the paper are: {' '.join(c.ACTIVE_BENCHES)})"
        )
    )
    args = parser.parse_args()
    if not args.programs:
        args.programs = c.ACTIVE_BENCHES
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(_args.programs)
