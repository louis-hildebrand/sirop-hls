#!/bin/python3

"""
This script reports the total compile time for all the ablation study benchmarks.
"""

import lib.ablation_plot_settings as aps
import lib.ablation_results_crud as crud
import lib.constants as c


def main() -> None:
    """
    Script entry point.
    """
    results = crud.read_compile_times(c.ABLATION_COMPILE_TIME_CSV)
    results = {
        k: v
        for k, v in results.items()
        if aps.program_title(k.name) is not None
        and k.lvl in aps.LEVELS_TO_PLOT or k.lvl == aps.BASELINE_LVL
    }
    tot_ctime = sum(t.total for t in results.values())
    max_ctime = max(t.total for t in results.values())
    print(f"Number of programs: {len(results)}")
    print(f"Total compile time: {tot_ctime} ms")
    print(f"Max compile time: {max_ctime} ms")


if __name__ == "__main__":
    main()
