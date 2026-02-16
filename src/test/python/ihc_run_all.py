#!/usr/bin/env python3

"""
This script runs all the measurements for the comparison with the Intel HLS compiler.
"""

from argparse import ArgumentParser, Namespace

import ihc_extract_fmax
import ihc_extract_resource_usage
import ihc_synth

ACTIVE_BENCHES = [
    "map", "dot", "matvec"#, "matmat",
    #"conv1d", "conv2d", "convb2b",
    #"sharpen", "sobel"
]


def main(programs: list[str]) -> None:
    """
    Script entry point.
    """
    ihc_synth.main(programs)

    ihc_extract_resource_usage.main(programs)
    ihc_extract_fmax.main(programs)

    print(f"TODO: measure latency for programs: {', '.join(programs)}")

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
            f"(the ones in the paper are: {' '.join(ACTIVE_BENCHES)})"
        )
    )
    args = parser.parse_args()
    if not args.programs:
        args.programs = ACTIVE_BENCHES
    return args


if __name__ == "__main__":
    _args = parse_args()
    main(_args.programs)
