"""
Shared constants.
"""

from pathlib import Path


ROOT = Path(__file__).resolve().parent.parent.parent.parent
JAR = ROOT / "target" / "scala-2.12" / "sirop.jar"
RESOURCES = ROOT / "src" / "e2e" / "resources"
ACTUAL_OUTPUTS = RESOURCES / "actual"

# The test runner will error out if there are any unrecognized files.
# The following directories and files will be ignored here; they will never
# trigger the error.
IGNORE_DIRECTORIES = {
    ACTUAL_OUTPUTS,
}
IGNORE_FILES = {
    RESOURCES / "FileError" / "do_not_overwrite.txt",
    RESOURCES / "FileError" / "do_not_overwrite" / ".gitkeep",
}
