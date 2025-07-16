"""
Constants.
"""

from pathlib import Path

AETHERLING_COMPILER = "mhir.main.aetherling.Compiler"
ROOT_DIR = Path(__file__).parent.parent.parent.parent.parent.resolve()
AETHERLING_SPACETIME_DIR = (
    ROOT_DIR.joinpath("src", "test", "resources", "aetherling_benchmarks", "original")
)
AETHERLING_VERILOG_DIR = (
    ROOT_DIR.joinpath("src", "test", "resources", "aetherling_benchmarks", "verilog")
)
VHDL_DIR = ROOT_DIR.joinpath("src", "test", "vhdl", "aetherling")
VERILOG_DIR = ROOT_DIR.joinpath("src", "test", "verilog", "aetherling")
TEST_SH_DIR = ROOT_DIR.joinpath("src", "test", "sh")
RESULTS_DIR = ROOT_DIR.joinpath("results")
RESOURCE_USAGE_CSV = RESULTS_DIR.joinpath("aetherling_resource_usage.csv")
FMAX_CSV = RESULTS_DIR.joinpath("aetherling_fmax.csv")
DEFAULT_QPF = ROOT_DIR.joinpath("src", "main", "resources", "mhir", "gen", "vhdl", "top.qpf")
DEFAULT_QSF = ROOT_DIR.joinpath("src", "main", "resources", "mhir", "gen", "vhdl", "top.qsf")
DEFAULT_SDC = ROOT_DIR.joinpath("src", "main", "resources", "mhir", "gen", "vhdl", "top.sdc")
