package mhir.parse

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
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

  test("map") {
    val files = os
      .list(OriginalCodeDir)
      .filter(os.isFile)
      .filter(p => p.baseName.startsWith("map_"))
    assert(files.nonEmpty)
    for (f <- files) {
      val code = os.read(f)
      val parsed = AetherlingParser.parse(code).tchk()
      val actual =
        s"// Output type: ${parsed.typ.toString}\n${ExprPrinter.display(parsed)}\n"
      val expectedPath = ParsedCodeDir / s"${f.baseName}.txt"
      if (SaveOutput) {
        save(expectedPath, actual)
      } else {
        val expected = os.read(expectedPath)
        assert(actual == expected)
      }
    }
    assume(!SaveOutput)
  }

  test("sum") {
    pending
  }

  test("conv2d") {
    pending
  }

  test("conv2d_b2b") {
    pending
  }

  test("sharpen") {
    pending
  }

  test("camera") {
    pending
  }
}
