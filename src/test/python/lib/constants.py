"""
Constants.
"""

from pathlib import Path

from lib.optimization_level import OptimizationLevel

VERILOG_PROJ_INITIALIZER = "mhir.gen.verilog.VerilogProjectInitializer"
LATENCY_MEASUREMENT_CLS = "mhir.main.aetherling.AetherlingBenchmarkLatencyMeasurement"

ROOT_DIR = Path(__file__).parent.parent.parent.parent.parent.resolve()
JAR_PATH = ROOT_DIR.joinpath("target", "scala-2.12", "sirop.jar")
AETHERLING_SPACETIME_DIR = (
    ROOT_DIR.joinpath("src", "test", "resources", "aetherling_benchmarks", "original")
)
AETHERLING_VERILOG_DIR = (
    ROOT_DIR.joinpath("src", "test", "resources", "aetherling_benchmarks", "verilog")
)
SHIR_SUBMODULE_DIR = ROOT_DIR.joinpath("src", "test", "shir")
SHIR_TESTBENCHES_DIR = ROOT_DIR.joinpath("src", "test", "resources", "shir_benchmarks")
VHDL_DIR = ROOT_DIR.joinpath("src", "test", "vhdl", "aetherling")
ABLATION_VHDL_DIR = ROOT_DIR.joinpath("src", "test", "vhdl", "ablation")
DSE_VHDL_DIR = ROOT_DIR.joinpath("src", "test", "vhdl", "dse")
SHIR_SHIR_VHDL_DIR = ROOT_DIR.joinpath("src", "test", "vhdl", "shir_shir")
SHIR_SIROP_VHDL_DIR = ROOT_DIR.joinpath("src", "test", "vhdl", "shir_sirop")
VERILOG_DIR = ROOT_DIR.joinpath("src", "test", "verilog", "aetherling")
TEST_SH_DIR = ROOT_DIR.joinpath("src", "test", "sh")
IHC_DIR = ROOT_DIR.joinpath("src", "test", "hls")

AETHERLING_COMPILE_TIME_DIR = ROOT_DIR.joinpath("src", "test", "compile_time", "aetherling")
ABLATION_COMPILE_TIME_DIR = ROOT_DIR.joinpath("src", "test", "compile_time", "ablation")
DSE_COMPILE_TIME_DIR = ROOT_DIR.joinpath("src", "test", "compile_time", "dse")
SHIR_COMPILE_TIME_DIR = ROOT_DIR.joinpath("src", "test", "compile_time", "shir")

RESULTS_DIR = ROOT_DIR.joinpath("results")
RESOURCE_USAGE_CSV = RESULTS_DIR.joinpath("aetherling_resource_usage.csv")
RESOURCE_USAGE_PDF = RESULTS_DIR.joinpath("aetherling_resource_usage.pdf")
FMAX_ESTIMATE_CSV = RESULTS_DIR.joinpath("aetherling_fmax_estimates.csv")
FMAX_ESTIMATE_PDF = RESULTS_DIR.joinpath("aetherling_fmax_estimates.pdf")
FMAX_MEASUREMENT_CSV = RESULTS_DIR.joinpath("aetherling_fmax_measurements.csv")
FMAX_MEASUREMENT_PDF = RESULTS_DIR.joinpath("aetherling_fmax_measurements.pdf")
LATENCY_CSV = RESULTS_DIR.joinpath("aetherling_latency.csv")
LATENCY_PDF = RESULTS_DIR.joinpath("aetherling_latency.pdf")
AETHERLING_COMPILE_TIME_CSV = RESULTS_DIR.joinpath("aetherling_compile_time.csv")
AETHERLING_COMPILE_TIME_PDF = RESULTS_DIR.joinpath("aetherling_compile_time.pdf")

ABLATION_RESOURCE_USAGE_CSV = RESULTS_DIR.joinpath("ablation_resource_usage.csv")
ABLATION_RESOURCE_USAGE_PDF = RESULTS_DIR.joinpath("ablation_resource_usage.pdf")
ABLATION_FMAX_CSV = RESULTS_DIR.joinpath("ablation_fmax.csv")
ABLATION_FMAX_PDF = RESULTS_DIR.joinpath("ablation_fmax.pdf")
ABLATION_LATENCY_CSV = RESULTS_DIR.joinpath("ablation_latency.csv")
ABLATION_LATENCY_PDF = RESULTS_DIR.joinpath("ablation_latency.pdf")
ABLATION_COMPILE_TIME_CSV = RESULTS_DIR.joinpath("ablation_compile_time.csv")
ABLATION_COMPILE_TIME_PDF = RESULTS_DIR.joinpath("ablation_compile_time.pdf")

SHIR_RESOURCE_USAGE_CSV = RESULTS_DIR.joinpath("shir_resource_usage.csv")
SHIR_RESOURCE_USAGE_PDF = RESULTS_DIR.joinpath("shir_resource_usage.pdf")
SHIR_FMAX_CSV = RESULTS_DIR.joinpath("shir_fmax.csv")
SHIR_LATENCY_CSV = RESULTS_DIR.joinpath("shir_latency.csv")
SHIR_LATENCY_PDF = RESULTS_DIR.joinpath("shir_latency.pdf")
SHIR_COMPILE_TIME_CSV = RESULTS_DIR.joinpath("shir_compile_time.csv")

IHC_FMAX_CSV = RESULTS_DIR.joinpath("ihc_fmax.csv")
IHC_RESOURCE_USAGE_CSV = RESULTS_DIR.joinpath("ihc_resource_usage.csv")
IHC_RESOURCE_USAGE_PDF = RESULTS_DIR.joinpath("ihc_resource_usage.pdf")
IHC_LATENCY_CSV = RESULTS_DIR.joinpath("ihc_latency.csv")
IHC_LATENCY_PDF = RESULTS_DIR.joinpath("ihc_latency.pdf")

CPW_RESOURCE_USAGE_PDF = RESULTS_DIR.joinpath("cpw_resource_usage.pdf")
CPW_LATENCY_PDF = RESULTS_DIR.joinpath("cpw_latency.pdf")

DEFAULT_QPF = ROOT_DIR.joinpath("src", "main", "resources", "mhir", "gen", "top.qpf")
DEFAULT_QSF = ROOT_DIR.joinpath("src", "main", "resources", "mhir", "gen", "top.qsf")
DEFAULT_SDC = ROOT_DIR.joinpath("src", "main", "resources", "mhir", "gen", "top.sdc")

AETHERLING_LABEL = "Chisel"
AETHERLING_LABEL_BLANK = "Chisel (timing requirements not met)"
AETHERLING_LABEL_SIM_FAIL = "Chisel (wrong outputs)"
AETHERLING_MARKER = "s"
AETHERLING_MARKER_SIM_FAIL = "X"
AETHERLING_MARKER_SIZE = 32
IHC_LABEL = "Intel HLS"
SHIR_LABEL = "\\textsc{Shir}"
OUR_LABEL = "\\textsc{Sirop}"
OUR_LABEL_BLANK = "\\textsc{Sirop} (timing requirements not met)"
OUR_MARKER = "o"
OUR_MARKER_SIZE = 8

IHC_COLOR        = "#a6cee3"
SHIR_COLOR       = "#ffe97f"
AETHERLING_COLOR = "#fb9a99"
CHISEL_COLOR     = "#2ba07b"
OUR_COLOR_PALE   = "#ffa54c"
OUR_COLOR        = "#ff7f00"

TARGET_FREQ = 175  # MHz

# The benchmarks that are shown in the paper
ACTIVE_BENCHES = [
    "map",
    "dot", "matvec", "matmat",
    "conv1d", "conv2d", "convb2b", "jacobi",
    "sharpen", "sobel", "camera",
]
# The optimization levels that are shown in the ablation study in the paper
ACTIVE_OPT_LEVELS = [
    OptimizationLevel.ALL,
    OptimizationLevel.EXCEPT_LETSTM_SIMPL,
    OptimizationLevel.EXCEPT_FUSE,
    OptimizationLevel.EXCEPT_FISSION,
]
