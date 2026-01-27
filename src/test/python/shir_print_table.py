#!/bin/python3

"""
This script generates a LaTeX table with the results of the comparison with SHIR.
"""

from fractions import Fraction

import lib.benchmark as lb
import lib.constants as c
import lib.plt_utils as pu
import lib.results_crud as crud
from lib.benchmark import Benchmark, BenchmarkImpl
from lib.latency import LatencyResult
from lib.resource_usage import ResourceUsage


def print_table(
    area_results: dict[BenchmarkImpl, ResourceUsage],
    latency_results: dict[BenchmarkImpl, LatencyResult],
) -> None:
    """
    Print the table of results.
    """
    print("\t"+r"\begin{tabular}{lrrlcrrlcrrlcrrl}")
    print("\t\t"+r"\toprule")
    print(
        "\t\t"
        + r"& \multicolumn{3}{c}{\textbf{Latency}} & "
        + r"& \multicolumn{3}{c}{\textbf{\glspl{alm}}} & "
        + r"& \multicolumn{3}{c}{\textbf{\glspl{bram}}} & "
        + r"& \multicolumn{3}{c}{\textbf{\glspl{dsp}}} \\"
    )
    print("\t\t"+r"\cmidrule{2-4}\cmidrule{6-8}\cmidrule{10-12}\cmidrule{14-16}")
    print(
        "\t\t"
        + r" & \multicolumn{1}{c}{\shir}"
        + r" & \multicolumn{2}{c}{\ourlang}"
        + r" &"
        + r" & \multicolumn{1}{c}{\shir}"
        + r" & \multicolumn{2}{c}{\ourlang}"
        + r" &"
        + r" & \multicolumn{1}{c}{\shir}"
        + r" & \multicolumn{2}{c}{\ourlang}"
        + r" &"
        + r" & \multicolumn{1}{c}{\shir}"
        + r" & \multicolumn{2}{c}{\ourlang} \\"
    )
    print("\t\t"+r"\midrule")

    program_names = pu.dedup([
        b.bench.name
        for b in latency_results.keys()
        if lb.benchmark_title(b.bench.name) is not None
    ])
    program_names = sorted(program_names, key=lb.benchmark_order)
    for prog in program_names:
        shir_key = BenchmarkImpl(Benchmark(prog, Fraction(-1)), "shir")
        shir_latency = latency_results.get(shir_key)
        shir_latency_str = "-" if shir_latency is None else f"{shir_latency.latency:,}"
        shir_area = area_results.get(shir_key)
        shir_alm = "-" if shir_area is None else f"{shir_area.alm:,}"
        shir_bram = "-" if shir_area is None else f"{shir_area.bram:,}"
        shir_dsp = "-" if shir_area is None else f"{shir_area.dsp:,}"

        sirop_key = BenchmarkImpl(Benchmark(prog, Fraction(-1)), "sirop")
        sirop_latency = latency_results.get(sirop_key)
        sirop_latency_str = "-" if sirop_latency is None else f"{sirop_latency.latency:,}"
        sirop_area = area_results.get(sirop_key)
        sirop_alm = "-" if sirop_area is None else f"{sirop_area.alm:,}"
        sirop_bram = "-" if sirop_area is None else f"{sirop_area.bram:,}"
        sirop_dsp = "-" if sirop_area is None else f"{sirop_area.dsp:,}"

        if (
            shir_latency is None
            or shir_latency.latency is None
            or sirop_latency is None
            or sirop_latency.latency is None
            or shir_latency.latency == sirop_latency.latency
        ):
            latency_change = ""
        else:
            latency_change = sirop_latency.latency - shir_latency.latency
            if latency_change > 0:
                latency_change = f"\\hspace{{-0.5em}}$\\mathit{{(+{latency_change:,})}}$"
                latency_change = f"\\textcolor{{BrickRed}}{{{latency_change}}}"
            else:
                latency_change = f"\\hspace{{-0.5em}}$\\mathit{{({latency_change:,})}}$"
                latency_change = f"\\textcolor{{PineGreen}}{{{latency_change}}}"
            latency_change = latency_change.replace(",", r"{,}")
        if shir_area is None or sirop_area is None or shir_area.alm == sirop_area.alm:
            alm_change = ""
        else:
            alm_change = sirop_area.alm / shir_area.alm
            if alm_change >= 1:
                alm_change = f"\\hspace{{-0.5em}}$\\mathit{{(+{alm_change-1:.0%})}}$"
                alm_change = f"\\textcolor{{BrickRed}}{{{alm_change}}}"
            else:
                alm_change = f"\\hspace{{-0.5em}}$\\mathit{{(-{1-alm_change:.0%})}}$"
                alm_change = f"\\textcolor{{PineGreen}}{{{alm_change}}}"
            alm_change = alm_change.replace("%", r"\,\%")
        if shir_area is None or sirop_area is None or shir_area.bram == sirop_area.bram:
            bram_change = ""
        else:
            bram_change = sirop_area.bram / shir_area.bram
            if bram_change >= 1:
                bram_change = f"\\hspace{{-0.5em}}$\\mathit{{(+{bram_change-1:.0%})}}$"
                bram_change = f"\\textcolor{{BrickRed}}{{{bram_change}}}"
            else:
                bram_change = f"\\hspace{{-0.5em}}$\\mathit{{(-{1-bram_change:.0%})}}$"
                bram_change = f"\\textcolor{{PineGreen}}{{{bram_change}}}"
            bram_change = bram_change.replace("%", r"\,\%")
        if shir_area is None or sirop_area is None or shir_area.dsp == sirop_area.dsp:
            dsp_change = ""
        else:
            dsp_change = sirop_area.dsp / shir_area.dsp
            if dsp_change >= 1:
                dsp_change = f"\\hspace{{-0.5em}}$\\mathit{{(+{dsp_change-1:.0%})}}$"
                dsp_change = f"\\textcolor{{BrickRed}}{{{dsp_change}}}"
            else:
                dsp_change = f"\\hspace{{-0.5em}}$\\mathit{{(-{1-dsp_change:.0%})}}$"
                dsp_change = f"\\textcolor{{PineGreen}}{{{dsp_change}}}"
            dsp_change = dsp_change.replace("%", r"\,\%")

        cells = [
            lb.benchmark_title(prog),
            shir_latency_str, sirop_latency_str, latency_change,
            "",
            shir_alm, sirop_alm, alm_change,
            "",
            shir_bram, sirop_bram, bram_change,
            "",
            shir_dsp, sirop_dsp, dsp_change,
        ]
        print(f"\t\t{' & '.join(cells)} \\\\")

    print("\t\t\\bottomrule")
    print("\t\\end{tabular}", end="")


def main() -> None:
    """
    The program entry point.
    """
    area_results = crud.read_valid_resource_usage_results(c.SHIR_RESOURCE_USAGE_CSV)
    latency_results = crud.read_valid_latency_results(c.SHIR_LATENCY_CSV)
    print_table(area_results, latency_results)


if __name__ == "__main__":
    main()
