package mhir.parse

import mhir.ir._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite
import os.Path

class AetherlingParserTests extends AnyFunSuite {

  /** The directory containing benchmarks written in Aetherling's space-time
    * language.
    */
  private val OriginalCodeDir: Path =
    os.pwd / "src" / "test" / "resources" / "aetherling_benchmarks" / "original"

  /** The directory containing the Aetherling benchmarks after parsing to an
    * [[mhir.ir.Expr]].
    */
  private val ParsedCodeDir: Path =
    os.pwd / "src" / "test" / "resources" / "aetherling_benchmarks" / "parsed"

  /** Whether to save the parsed programs to [[ParsedCodeDir]].
    *
    * If false, then the programs in [[ParsedCodeDir]] will be used as the
    * expected outputs and nothing will be written.
    *
    * If true, then the parsed programs will be saved to [[ParsedCodeDir]]. This
    * is useful in case you want to change the expected outputs, just don't
    * forget to review the outputs and change this field back to `false`.
    */
  private val SaveOutput: Boolean = false

  private def save(f: Path, code: String): Unit = {
    if (os.exists(f)) os.remove(f)
    os.write(f, code)
  }

  test("SaveOutput") {
    assert(!SaveOutput)
  }

  private val files = os.list(OriginalCodeDir).filter(os.isFile)

  test("NonEmpty") {
    assert(files.nonEmpty)
  }

  for (f <- files) {
    test(f.baseName) {
      mhir.ir.reset()
      val code = os.read(f)
      val parsed = AetherlingParser.parse(code).tchk()
      val actual =
        (s"// Output type: ${parsed.typ.toString}"
          + s"\n${ExprPrinter.display(parsed)}"
          + "\n")
      val expectedPath = ParsedCodeDir / s"${f.baseName}.txt"
      if (SaveOutput) {
        save(expectedPath, actual)
      } else {
        val expected = os.read(expectedPath)
        assert(actual == expected)
      }
      assume(!SaveOutput)
    }
  }
}
