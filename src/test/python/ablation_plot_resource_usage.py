#!/bin/python3

"""
This script plots the resource usages for the ablation study.
"""

import sys

import matplotlib.pyplot as plt

import lib.ablation_results_crud as crud
import lib.benchmark as lb
import lib.constants as c
from lib.optimization_level import OptimizationLevel
from lib.program_variant import ProgramVariant
from lib.resource_usage import ResourceUsage

BAR_SPACE = 0.2
BAR_WIDTH = (1 - BAR_SPACE) / len(OptimizationLevel)
BAR_HATCH = ["", "xx", "++", ".."]
HATCH_WIDTH = 1


def dedup(xs: list[str]) -> list[str]:
    """
    Deduplicate elements in a list while preserving order.
    """
    return list(dict.fromkeys(xs))


def plot_resource_usages(results: dict[ProgramVariant, ResourceUsage]) -> None:
    """
    Plot resource usage for each program.
    """
    program_names = dedup([p.name for p in results.keys()])
    program_names = sorted(program_names, key=lb.benchmark_order)
    if not program_names:
        raise ValueError("Nothing to plot.")
    plt.rcParams.update({
        "text.usetex": True,
        "font.family": "Times New Roman",
        "font.size": 10,
    })
    fig, alm_ax = plt.subplots(
        nrows=1, ncols=1,
        figsize=(8, 1.5),
        layout="compressed",
        sharex="col",
    )
    artists = []
    for i, lvl in enumerate(OptimizationLevel):
        xs = [x + i * BAR_WIDTH for x in range(len(program_names))]
        # ALM usage
        ys = []
        for p in program_names:
            ru = results.get(ProgramVariant(p, lvl))
            y = ru.alm if ru else 0
            ys.append(y)
        artist, *_ = alm_ax.bar(
            xs, ys,
            width=BAR_WIDTH,
            label=str(lvl),
            hatch=BAR_HATCH[i],
            hatch_linewidth=HATCH_WIDTH,
        )
        artists.append(artist)
        # # BRAM usage
        # ys = []
        # for p in program_names:
        #     ru = results.get(ProgramVariant(p, lvl))
        #     y = ru.bram if ru else 0
        #     ys.append(y)
        # bram_ax.bar(
        #     xs, ys,
        #     width=BAR_WIDTH,
        #     label=str(lvl),
        #     hatch=BAR_HATCH[i],
        #     hatch_linewidth=HATCH_WIDTH,
        # )
        # # ALM usage
        # xs = [x + i * BAR_WIDTH for x in range(len(program_names))]
        # ys = []
        # for p in program_names:
        #     ru = results.get(ProgramVariant(p, lvl))
        #     y = ru.dsp if ru else 0
        #     ys.append(y)
        # dsp_ax.bar(
        #     xs, ys,
        #     width=BAR_WIDTH,
        #     label=str(lvl),
        #     hatch=BAR_HATCH[i],
        #     hatch_linewidth=HATCH_WIDTH,
        # )
    alm_ax.set_ylabel("ALMs")
    alm_ax.set_xticks(
        [x + (len(program_names) / 2) * BAR_WIDTH for x in range(len(program_names))],
        program_names
    )
    alm_ax.tick_params(axis="x", which="both", length=0)
    # bram_ax.set_ylabel("BRAMs")
    # bram_ax.set_xticks([])
    # dsp_ax.set_ylabel("DSPs")
    # dsp_ax.set_xticks([])
    fig.legend(
        labels=[str(lvl) for lvl in OptimizationLevel],
        handles=artists,
        loc="lower center",
        bbox_to_anchor=(0.5, -0.2),
        ncols=len(OptimizationLevel),
    )
    fig.savefig(c.ABLATION_RESOURCE_USAGE_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    results = crud.read_valid_resource_usage_results(c.ABLATION_RESOURCE_USAGE_CSV)
    if not results:
        sys.exit("No results to plot.")
    plot_resource_usages(results)


if __name__ == "__main__":
    main()
