"""
Dataclass for describing the resource usage of a design.
"""

import os
import subprocess
from dataclasses import dataclass
from pathlib import Path
from subprocess import CalledProcessError

import lib.constants as c


@dataclass(frozen=True)
class ResourceUsage:
    """
    The resource usage of a hardware design.
    """
    alm: int
    bram: int
    dsp: int


def extract_resource_usage(project_dir: Path) -> ResourceUsage | None:
    """
    Extract the resource usage for a given design.
    """
    os.chdir(project_dir)
    try:
        result = subprocess.run(
            [c.TEST_SH_DIR.joinpath("extract_alm_count.sh")],
            check=True, capture_output=True, text=True, encoding="utf-8"
        )
        if not result.stdout:
            return None
        alm = int(result.stdout)
        result = subprocess.run(
            [c.TEST_SH_DIR.joinpath("extract_bram_count.sh")],
            check=True, capture_output=True, text=True, encoding="utf-8"
        )
        if not result.stdout:
            return None
        bram = int(result.stdout)
        result = subprocess.run(
            [c.TEST_SH_DIR.joinpath("extract_dsp_count.sh")],
            check=True, capture_output=True, text=True, encoding="utf-8"
        )
        if not result.stdout:
            return None
        dsp = int(result.stdout)
    except CalledProcessError:
        return None
    return ResourceUsage(alm=alm, bram=bram, dsp=dsp)
