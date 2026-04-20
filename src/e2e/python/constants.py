"""
Shared constants.
"""

from pathlib import Path


ROOT = Path(__file__).resolve().parent.parent.parent.parent
JAR = ROOT / "target" / "scala-2.12" / "sirop.jar"
RESOURCES = ROOT / "src" / "e2e" / "resources"
ACTUAL_OUTPUTS = RESOURCES / "actual"
