#!/usr/bin/env python3

"""
This script reports the total compile time for all the SHIR benchmarks.
"""

import lib.constants as c
import lib.results_crud as crud


def main() -> None:
    """
    Script entry point.
    """
    results = crud.read_compile_times(c.SHIR_COMPILE_TIME_CSV)
    tot_ctime = sum(t.total for t in results.values())
    max_ctime = max(t.total for t in results.values())
    print(f"Number of programs: {len(results)}")
    print(f"Total compile time: {tot_ctime} ms")
    print(f"Max compile time: {max_ctime} ms")


if __name__ == "__main__":
    main()
