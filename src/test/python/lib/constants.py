"""
Constants.
"""

from pathlib import Path

AETHERLING_COMPILER = "mhir.main.aetherling.Compiler"
VERILOG_PROJ_INITIALIZER = "mhir.gen.verilog.VerilogProjectInitializer"
LATENCY_MEASUREMENT_CLS = "mhir.main.aetherling.AetherlingBenchmarkLatencyMeasurement"
STORED_PROGRAM_COMPILER = "mhir.main.stored.Compiler"

ROOT_DIR = Path(__file__).parent.parent.parent.parent.parent.resolve()
AETHERLING_SPACETIME_DIR = (
    ROOT_DIR.joinpath("src", "test", "resources", "aetherling_benchmarks", "original")
)
AETHERLING_VERILOG_DIR = (
    ROOT_DIR.joinpath("src", "test", "resources", "aetherling_benchmarks", "verilog")
)
VHDL_DIR = ROOT_DIR.joinpath("src", "test", "vhdl", "aetherling")
ABLATION_VHDL_DIR = ROOT_DIR.joinpath("src", "test", "vhdl", "ablation")
VERILOG_DIR = ROOT_DIR.joinpath("src", "test", "verilog", "aetherling")
TEST_SH_DIR = ROOT_DIR.joinpath("src", "test", "sh")

RESULTS_DIR = ROOT_DIR.joinpath("results")
RESOURCE_USAGE_CSV = RESULTS_DIR.joinpath("aetherling_resource_usage.csv")
RESOURCE_USAGE_PDF = RESULTS_DIR.joinpath("aetherling_resource_usage.pdf")
FMAX_ESTIMATE_CSV = RESULTS_DIR.joinpath("aetherling_fmax_estimates.csv")
FMAX_ESTIMATE_PDF = RESULTS_DIR.joinpath("aetherling_fmax_estimates.pdf")
FMAX_MEASUREMENT_CSV = RESULTS_DIR.joinpath("aetherling_fmax_measurements.csv")
FMAX_MEASUREMENT_PDF = RESULTS_DIR.joinpath("aetherling_fmax_measurements.pdf")
LATENCY_CSV = RESULTS_DIR.joinpath("aetherling_latency.csv")
LATENCY_PDF = RESULTS_DIR.joinpath("aetherling_latency.pdf")

ABLATION_RESOURCE_USAGE_CSV = RESULTS_DIR.joinpath("ablation_resource_usage.csv")
ABLATION_FMAX_CSV = RESULTS_DIR.joinpath("ablation_fmax.csv")
ABLATION_LATENCY_CSV = RESULTS_DIR.joinpath("ablation_latency.csv")

DEFAULT_QPF = ROOT_DIR.joinpath("src", "main", "resources", "mhir", "gen", "top.qpf")
DEFAULT_QSF = ROOT_DIR.joinpath("src", "main", "resources", "mhir", "gen", "top.qsf")
DEFAULT_SDC = ROOT_DIR.joinpath("src", "main", "resources", "mhir", "gen", "top.sdc")
