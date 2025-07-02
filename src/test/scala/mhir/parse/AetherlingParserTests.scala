package mhir.parse

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.sugar.{Fifo, StmMap, VecMap}
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

  test("Map:10/1") {
    val src =
      s"""module0 (I :: UInt8T) =
         |    n13 = Const_GenN UInt8V 5 1 UInt8T
         |    n6 = ATupleN UInt8T UInt8T I n13
         |    n7 = AddN UInt8T n6
         |
         |module1 (I :: (TSeqT 20 0 (SSeqT 10 UInt8T))) =
         |    n1 = FIFON (TSeqT 20 0 (SSeqT 10 UInt8T)) 1 "I"
         |    n9 = Map_tN 20 0 (Map_sN 10 module0 I) n1
         |    n10 = FIFON (TSeqT 20 0 (SSeqT 10 UInt8T)) 1 "n9"
         |    n11 = FIFON (TSeqT 20 0 (SSeqT 10 UInt8T)) 1 "n10"
         |    n12 = FIFON (TSeqT 20 0 (SSeqT 10 UInt8T)) 1 "n11"
         |
         |""".stripMargin
    val expected = {
      val module0 = Param("module0")()
      val module0Impl = {
        val i = Param("i")(U8)
        val n13 = Param("n13")()
        val n13Val = C(5)(U8)
        val n6 = Param("n6")()
        val n6Val = Tuple(i, n13)()
        val n7Val = n6.__0 + n6.__1
        Function(i, Lets(n13 -> n13Val, n6 -> n6Val)(n7Val))()
      }
      val module1Impl = {
        val i = Param("i")(TyStm(TyVec(U8, 10), 20))
        val n1 = Param("n1")()
        val n1Val = Fifo(i)
        val n9 = Param("n9")()
        val n9Val = {
          val i = Param("i")(TyVec(U8, 10))
          // TODO: Here, module0 is used as the function in VecMap. What if it
          //       was used as the function in StmMap? StmMap requires the
          //       function to be static.
          StmMap(n1, Function(i, VecMap(i, module0)())())()
        }
        val n10 = Param("n10")()
        val n10Val = Fifo(n9)
        val n11 = Param("n11")()
        val n11Val = Fifo(n10)
        val n12Val = Fifo(n11)
        Function(
          i,
          Lets(n1 -> n1Val, n9 -> n9Val, n10 -> n10Val, n11 -> n11Val)(n12Val)
        )()
      }
      Let(module0, module0Impl, module1Impl)()
    }
    val actual = AetherlingParser.parse(src)
    assert(actual == expected)

    val expectedTyp = expected.tchk().typ
    val actualTyp = actual.tchk().typ
    assert(actualTyp == expectedTyp)
  }

  test("map") {
    val files = os
      .list(OriginalCodeDir)
      .filter(os.isFile)
      .filter(p => p.baseName.startsWith("map"))
    assert(files.nonEmpty)
    for (f <- files) {
      val code = os.read(f)
      val parsed = AetherlingParser.parse(code)
      val actual = ExprPrinter.display(parsed)
      val expectedPath = ParsedCodeDir / f.baseName
      if (SaveOutput) {
        save(expectedPath, actual)
      } else {
        val expected = os.read(expectedPath)
        assert(actual == expected)
      }
    }
    assume(!SaveOutput)
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
