"""
Functions for synthesizing designs.
"""

import os
import subprocess
from pathlib import Path
from subprocess import CalledProcessError


def synthesize_design(proj_dir: Path) -> bool:
    """
    Synthesize a VHDL or Verilog project.
    Return `True` on success and `False` on failure.
    """
    if proj_dir.joinpath("design/top.vhd").exists():
        top = "top"
    elif proj_dir.joinpath("Top.v").exists():
        top = "Top"
    else:
        raise ValueError(
            f"Could not decide whether {proj_dir} is a VHDL or Verilog project,"
            " since neither design/top.vhd nor Top.v exist."
        )
    prev_cwd = os.getcwd()
    try:
        os.chdir(proj_dir)
        subprocess.run(
            ["quartus_sh", "--flow", "compile", top],
            check=True,
        )
        return True
    except CalledProcessError:
        os.chdir(prev_cwd)
        return False
