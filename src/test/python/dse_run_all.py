#!/bin/python3

"""
This script runs all the measurements for the manual DSE study.
"""

from argparse import ArgumentParser, Namespace

import dse_extract_fmax
import dse_extract_resource_usage
import dse_generate
import dse_measure_latency
import dse_synth


def main(programs: list[str]) -> None:
    """
    Script entry point.
    """
    dse_generate.main(programs)
    dse_synth.main(programs)

    dse_extract_resource_usage.main(programs)
    dse_measure_latency.main(programs)
    dse_extract_fmax.main(programs)


def parse_args() -> Namespace:
    """
    Parse the command line arguments
    """
    parser = ArgumentParser(
        description="This script runs the full ablation study."
    )
    parser.add_argument(
        "programs",
        nargs="*",
        help="the names of the programs to process"
    )
    return parser.parse_args()


if __name__ == "__main__":
    _args = parse_args()
    main(programs=_args.programs)
