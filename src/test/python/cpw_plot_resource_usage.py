#!/usr/bin/env python3

"""
This script plots the resource usage for Sirop compared to prior works
(Aetherling, SHIR, Intel HLS).
"""

from fractions import Fraction

import matplotlib.pyplot as plt
from matplotlib.patches import Rectangle

import lib.benchmark as lb
import lib.constants as c
import lib.plt_utils as pu
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl, benchmark_order
from lib.resource_usage import ResourceUsage

BAR_SPACE = 0.3
BAR_PADDING = 0.04
AETHERLING_HATCH = "\\\\\\"
IHC_HATCH = "xxx"
SHIR_HATCH = "///"
OUR_HATCH = ""
SYNTH_FAIL = r"\textbf{\Large $\times$}"


def plot_resource_usages(
    results: dict[BenchmarkImpl, ResourceUsage],
    skip_ihc: bool,
    skip_shir: bool,
    skip_aetherling: bool,
    hide_ihc_alm: bool,
    hide_ihc_bram: bool,
    hide_ihc_dsp: bool,
    hide_shir_alm: bool,
    hide_shir_bram: bool,
    hide_shir_dsp: bool,
    hide_aetherling_alm: bool,
    hide_aetherling_bram: bool,
    hide_aetherling_dsp: bool,
    hide_sirop_alm: bool,
    hide_sirop_bram: bool,
    hide_sirop_dsp: bool,
) -> None:
    """
    Plot resource usage for each program.
    """
    program_names = pu.dedup([
        b.bench.name
        for b in results.keys()
        if lb.benchmark_title(b.bench.name) is not None
    ])
    program_names = sorted(program_names, key=benchmark_order)
    if not program_names:
        raise ValueError("Nothing to plot.")
    plt.rcParams.update({
        "text.usetex": True,
        "font.family": "Times New Roman",
        "font.size": 12,
    })
    fig, (alm_ax, bram_ax, dsp_ax) = plt.subplots(
        nrows=3, ncols=1,
        figsize=(8, 2.3),
        layout="compressed",
        sharex="col",
        sharey="row",
    )

    num_bars = 1 + sum([not skip_ihc, not skip_shir, not skip_aetherling])
    bar_width = (1 - BAR_SPACE) / num_bars

    # Resource usages
    xs = list(range(len(program_names)))
    # ALM usage
    ihc_alms = [
        (
            results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "ihc")].alm
            if BenchmarkImpl(Benchmark(p, Fraction(-1)), "ihc") in results
            else None
        )
        for p in program_names
    ]
    shir_alms = [
        (
            results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir")].alm
            if BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir") in results
            else None
        )
        for p in program_names
    ]
    aetherling_alms = [
        (
            results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "aetherling")].alm
            if BenchmarkImpl(Benchmark(p, Fraction(-1)), "aetherling") in results
            else None
        )
        for p in program_names
    ]
    sirop_alms = [
        results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "sirop")].alm
        for p in program_names
    ]
    delta_x = 0
    if not skip_ihc:
        alm_ax.bar(
            bottom=0,
            x=[x + delta_x for i, x in enumerate(xs) if ihc_alms[i] is not None],
            height=[x for x in ihc_alms if x is not None],
            width=bar_width - BAR_PADDING,
            facecolor=c.IHC_COLOR,
            edgecolor="black",
            linestyle="-",
            hatch=IHC_HATCH,
            zorder=10,
            visible=not hide_ihc_alm,
        )
        delta_x += bar_width
    if not skip_shir:
        alm_ax.bar(
            bottom=0,
            x=[x + delta_x for x in xs],
            height=[x or 0 for x in shir_alms],
            width=bar_width - BAR_PADDING,
            facecolor=c.SHIR_COLOR,
            edgecolor="black",
            linestyle="-",
            hatch=SHIR_HATCH,
            zorder=10,
            visible=not hide_shir_alm,
        )
        delta_x += bar_width
    if not skip_aetherling:
        alm_ax.bar(
            bottom=0,
            x=[x + delta_x for i, x in enumerate(xs) if aetherling_alms[i] is not None],
            height=[x for x in aetherling_alms if x is not None],
            width=bar_width - BAR_PADDING,
            facecolor=c.AETHERLING_COLOR,
            edgecolor="black",
            linestyle="-",
            hatch=AETHERLING_HATCH,
            zorder=10,
            visible=not hide_aetherling_alm,
        )
        delta_x += bar_width
    alm_ax.bar(
        bottom=0,
        x=[x + delta_x for x in xs],
        height=sirop_alms,
        width=bar_width - BAR_PADDING,
        facecolor=c.OUR_COLOR_PALE,
        edgecolor="black",
        linestyle="-",
        hatch=OUR_HATCH,
        zorder=10,
        visible=not hide_sirop_alm,
    )
    # BRAM usage
    ihc_brams = [
        (
            BenchmarkImpl(Benchmark(p, Fraction(-1)), "ihc") in results
            and results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "ihc")].bram
        )
        for p in program_names
    ]
    shir_brams = [
        (
            BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir") in results
            and results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir")].bram
        )
        for p in program_names
    ]
    aetherling_brams = [
        (
            BenchmarkImpl(Benchmark(p, Fraction(-1)), "aetherling") in results
            and results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "aetherling")].bram
        )
        for p in program_names
    ]
    sirop_brams = [
        results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "sirop")].bram
        for p in program_names
    ]
    delta_x = 0
    if not skip_ihc:
        bram_ax.bar(
            bottom=0,
            x=[x + delta_x for i, x in enumerate(xs) if ihc_brams[i] is not None],
            height=[x for x in ihc_brams if x is not None],
            width=bar_width - BAR_PADDING,
            facecolor=c.IHC_COLOR,
            edgecolor="black",
            linestyle="-",
            hatch=IHC_HATCH,
            zorder=10,
            visible=not hide_ihc_bram,
        )
        delta_x += bar_width
    if not skip_shir:
        bram_ax.bar(
            bottom=0,
            x=[x + delta_x for i, x in enumerate(xs) if shir_brams[i] is not None],
            height=[x for x in shir_brams if x is not None],
            width=bar_width - BAR_PADDING,
            facecolor=c.SHIR_COLOR,
            edgecolor="black",
            linestyle="-",
            hatch=SHIR_HATCH,
            zorder=10,
            visible=not hide_shir_bram,
        )
        delta_x += bar_width
    if not skip_aetherling:
        bram_ax.bar(
            bottom=0,
            x=[x + delta_x for i, x in enumerate(xs) if aetherling_brams[i] is not None],
            height=[x for x in aetherling_brams if x is not None],
            width=bar_width - BAR_PADDING,
            facecolor=c.AETHERLING_COLOR,
            edgecolor="black",
            linestyle="-",
            hatch=AETHERLING_HATCH,
            zorder=10,
            visible=not hide_aetherling_bram,
        )
        delta_x += bar_width
    bram_ax.bar(
        bottom=0,
        x=[x + delta_x for x in xs],
        height=sirop_brams,
        width=bar_width - BAR_PADDING,
        facecolor=c.OUR_COLOR_PALE,
        edgecolor="black",
        linestyle="-",
        hatch=OUR_HATCH,
        zorder=10,
        visible=not hide_sirop_bram,
    )
    # DSP usage
    ihc_dsps = [
        (
            BenchmarkImpl(Benchmark(p, Fraction(-1)), "ihc") in results
            and results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "ihc")].dsp
        )
        for p in program_names
    ]
    shir_dsps = [
        (
            BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir") in results
            and results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "shir")].dsp
        )
        for p in program_names
    ]
    aetherling_dsps = [
        (
            BenchmarkImpl(Benchmark(p, Fraction(-1)), "aetherling") in results
            and results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "aetherling")].dsp
        )
        for p in program_names
    ]
    sirop_dsps = [
        results[BenchmarkImpl(Benchmark(p, Fraction(-1)), "sirop")].dsp
        for p in program_names
    ]
    delta_x = 0
    if not skip_ihc:
        dsp_ax.bar(
            bottom=0,
            x=[x + delta_x for i, x in enumerate(xs) if ihc_dsps[i] is not None],
            height=[x for x in ihc_dsps if x is not None],
            width=bar_width - BAR_PADDING,
            facecolor=c.IHC_COLOR,
            edgecolor="black",
            linestyle="-",
            hatch=IHC_HATCH,
            zorder=10,
            visible=not hide_ihc_dsp,
        )
        delta_x += bar_width
    if not skip_shir:
        dsp_ax.bar(
            bottom=0,
            x=[x + delta_x for i, x in enumerate(xs) if shir_dsps[i] is not None],
            height=[x for x in shir_dsps if x is not None],
            width=bar_width - BAR_PADDING,
            facecolor=c.SHIR_COLOR,
            edgecolor="black",
            linestyle="-",
            hatch=SHIR_HATCH,
            zorder=10,
            visible=not hide_shir_dsp,
        )
        delta_x += bar_width
    if not skip_aetherling:
        dsp_ax.bar(
            bottom=0,
            x=[x + delta_x for i, x in enumerate(xs) if aetherling_dsps[i] is not None],
            height=[x for x in aetherling_dsps if x is not None],
            width=bar_width - BAR_PADDING,
            facecolor=c.AETHERLING_COLOR,
            edgecolor="black",
            linestyle="-",
            hatch=AETHERLING_HATCH,
            zorder=10,
            visible=not hide_aetherling_dsp,
        )
        delta_x += bar_width
    dsp_ax.bar(
        bottom=0,
        x=[x + delta_x for x in xs],
        height=sirop_dsps,
        width=bar_width - BAR_PADDING,
        facecolor=c.OUR_COLOR_PALE,
        edgecolor="black",
        linestyle="-",
        hatch=OUR_HATCH,
        zorder=10,
        visible=not hide_sirop_dsp,
    )

    # Display settings
    xlim = (
        -0.5*bar_width - 0.2*BAR_SPACE,
        len(program_names) - 0.5*bar_width - 0.5*BAR_SPACE
    )
    alm_ax.set_xlim(xlim)
    alm_ax.tick_params(axis="x", which="both", length=0)
    alm_ax.set_xticks(
        [x + (num_bars-1)/2*bar_width for x in xs],
        [lb.benchmark_title(p) or "NONE" for p in program_names]
    )
    alm_ax.grid(
        visible=True,
        which="major",
        axis="y",
        linewidth=0.2,
        color=(0.8, 0.8, 0.8)
    )
    alm_ax.set_yscale("log")
    alm_ax.set_ylabel("ALMs\n(log)")
    alm_ax.set_yticks(
        [1, 10, 10**2, 10**3, 10**4],
    )
    alm_ax.set_ylim(1, 2*10**4)
    alm_ax.set_zorder(10**6)

    bram_ax.tick_params(axis="x", which="both", length=0)
    bram_ax.grid(
        visible=True,
        which="major",
        axis="y",
        linewidth=0.2,
        color=(0.8, 0.8, 0.8)
    )
    bram_ax.set_yscale("symlog")
    bram_ax.set_ylabel("BRAMs\n(log)")
    bram_ax.set_yticks([1, 10, 100])
    bram_ax.set_ylim(0.5, 200)

    dsp_ax.tick_params(axis="x", which="both", length=0)
    dsp_ax.grid(
        visible=True,
        which="major",
        axis="y",
        linewidth=0.2,
        color=(0.8, 0.8, 0.8)
    )
    dsp_ax.set_yscale("symlog")
    dsp_ax.set_ylabel("DSPs\n(log)")
    dsp_ax.set_ylim(0.5, 100)

    fig.align_ylabels()

    # Legend
    handles = []
    if not skip_ihc:
        handles.append(
            Rectangle(
                (0, 0), 1, 1,
                label=c.IHC_LABEL,
                facecolor=c.IHC_COLOR,
                edgecolor="black",
                hatch=IHC_HATCH,
            )
        )
    if not skip_shir:
        handles.append(
            Rectangle(
                (0, 0), 1, 1,
                label=c.SHIR_LABEL,
                facecolor=c.SHIR_COLOR,
                edgecolor="black",
                hatch=SHIR_HATCH,
            )
        )
    if not skip_aetherling:
        handles.append(
            Rectangle(
                (0, 0), 1, 1,
                label="Aetherling",
                facecolor=c.AETHERLING_COLOR,
                edgecolor="black",
                hatch=AETHERLING_HATCH,
            )
        )
    handles.append(
        Rectangle(
            (0, 0), 1, 1,
            label=c.OUR_LABEL,
            facecolor=c.OUR_COLOR_PALE,
            edgecolor="black",
            hatch=OUR_HATCH,
        )
    )
    fig.legend(
        handles=handles,
        loc="upper right",
        bbox_to_anchor=(1, 0),
        ncols=len(handles),
    )

    fig.savefig(c.CPW_RESOURCE_USAGE_PDF, bbox_inches="tight")


def main() -> None:
    """
    The program entry point.
    """
    (area_results, _, _) = crud.read_combined_results(
        c.AETHERLING_RESOURCE_USAGE_CSV,
        c.AETHERLING_LATENCY_CSV,
        c.AETHERLING_FMAX_ESTIMATE_CSV,
        c.SHIR_RESOURCE_USAGE_CSV,
        c.SHIR_LATENCY_CSV,
        c.SHIR_FMAX_CSV,
        c.IHC_RESOURCE_USAGE_CSV,
        c.IHC_LATENCY_CSV,
        c.IHC_FMAX_CSV,
        c.SIROP_RESOURCE_USAGE_CSV,
        c.SIROP_LATENCY_CSV,
        c.SIROP_FMAX_CSV,
    )
    # TODO: Expose these as command-line flags
    plot_resource_usages(
        area_results,
        skip_ihc             = False,
        skip_shir            = False,
        skip_aetherling      = False,
        hide_ihc_alm         = False,
        hide_ihc_bram        = False,
        hide_ihc_dsp         = False,
        hide_shir_alm        = False,
        hide_shir_bram       = False,
        hide_shir_dsp        = False,
        hide_aetherling_alm  = False,
        hide_aetherling_bram = False,
        hide_aetherling_dsp  = False,
        hide_sirop_alm       = False,
        hide_sirop_bram      = False,
        hide_sirop_dsp       = False,
    )


if __name__ == "__main__":
    main()
