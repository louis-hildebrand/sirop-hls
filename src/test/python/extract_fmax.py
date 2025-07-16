#!/bin/python3

"""
This script extracts the maximum clock frequency for a given design.
"""

import re
import sys
from pathlib import Path


def get_sta_path(proj_dir: Path) -> Path:
    """
    Find the path to the .sta.rpt file in the given project.
    """
    sta_path = proj_dir.joinpath("output_files", "top.sta.rpt")
    if sta_path.exists():
        return sta_path
    sta_path = proj_dir.joinpath("output_files", "Top.sta.rpt")
    if sta_path.exists():
        return sta_path
    raise ValueError("Could not locate the timing analysis summary.")


def get_fmaxes(sta_path: Path) -> list[int]:
    """
    Extract all the fmax values from the given .sta.rpt file.
    """
    results = []
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


def main(proj_dir: Path) -> None:
    """
    Script entry point.
    """
    sta_path = get_sta_path(proj_dir)
    fmax = min(get_fmaxes(sta_path))
    print(fmax)


if __name__ == "__main__":
    main(Path(sys.argv[1]).resolve())
