#!/usr/bin/env python3
"""
This script measures the number of lines of code for each benchmark.
"""
import subprocess
import tempfile
from argparse import ArgumentParser, Namespace

import lib.constants as c


def remove_main(orig_src: str) -> str:
    """
    Remove the main function from the given source code.
    """
    lines = orig_src.splitlines()
    filtered_lines = []
    main_found = False
    for ln in lines:
        if "int main()" in ln:
            main_found = True
            break
        if "#include <stdio.h>" in ln:
            continue
        filtered_lines.append(ln)
    if not main_found:
        print("WARNING: no main() function found")
    return "\n".join(filtered_lines)


def main(programs: list[str], show_src: bool) -> None:
    """
    Script entry point.
    """
    results = {}
    with tempfile.NamedTemporaryFile(suffix=".c", mode="w+", encoding="utf-8") as f:
        for prog in programs:
            src = c.IHC_DIR.joinpath(prog, f"{prog}.cpp").read_text(encoding="utf-8")
            src = remove_main(src)
            f.write(src)
            if show_src:
                f.seek(0)
                print("-"*20 + prog + "-"*20)
                print(f.read())
                print("-"*20 + prog + "-"*20)
            f.seek(0)
            result = subprocess.run(
                ["cloc", "--csv", f.name],
                check=True, capture_output=True, encoding="utf-8"
            )
            count = int(result.stdout.splitlines()[-1].split(",")[-1])
            results[prog] = count
    for prog, count in results.items():
        print(f"{prog:>10s} : {count} lines")


def parse_args() -> Namespace:
    """
    Parse the command-line arguments.
    """
    parser = ArgumentParser(
        description="counts lines of code for the given benchmarks"
    )
    parser.add_argument(
        "programs",
        nargs="*",
        help="the names of the programs to process"
    )
    parser.add_argument(
        "--show",
        action="store_true",
        help="print the benchmark source code after removing main()"
    )
    args = parser.parse_args()
    if not args.programs:
        args.programs = c.ACTIVE_BENCHES
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(programs=_args.programs, show_src=_args.show)
