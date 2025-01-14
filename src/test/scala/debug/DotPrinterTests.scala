package debug

import ir.*
import operations.{StmRange, VecMap}
import org.scalatest.funsuite.AnyFunSuite

import java.nio.file.{Files, Paths}

class DotPrinterTests extends AnyFunSuite {
  Files.createDirectories(Paths.get("./img/"))

  test("IntCst") {
    val e = IntCst(42)
    DotPrinter.save(e, "./img/IntCst.dot")
  }

  test("DontCare") {
    DotPrinter.save(DontCare, "./img/DontCare.dot")
  }

  test("ArithBinOp") {
    val e = ((((IntCst(1) + 2) - 3) * 4) / 5) % 6
    DotPrinter.save(e, "./img/ArithBinOp.dot")
  }

  test("LogicalOps") {
    val e = True && (False || True)
    DotPrinter.save(e, "./img/LogicalOps.dot")
  }

  test("ComparisonOps") {
    val e = (
      (IntCst(1) < 2)
        === (IntCst(3) <= 4)
        !== (IntCst(5) > 6)
          === (IntCst(7) >= 8)
    )
    DotPrinter.save(e, "./img/ComparisonOps.dot")
  }

  test("IfThenElse") {
    val e = IfThenElse(1 === 2, 3, 4)
    DotPrinter.save(e, "./img/IfThenElse.dot")
  }

  test("FreeVar") {
    val x = Param()
    DotPrinter.save(x, "./img/FreeVar.dot", nameByVar = Map(x -> "x"))
  }

  test("SimpleTuple") {
    val y = Param()
    val e = Tuple(IntCst(42), True, y - 1)
    DotPrinter.save(e, "./img/SimpleTuple.dot", nameByVar = Map(y -> "y"))
  }

  test("NestedTuple") {
    val y = Param()
    val e = Tuple(
      IntCst(42),
      True,
      VecBuild(3, (i: Expr) => i * i),
      Tuple(IntCst(0), y + 2)
    )
    DotPrinter.save(e, "./img/NestedTuple.dot", nameByVar = Map(y -> "y"))
  }

  test("SimpleVecBuild") {
    val x = Param()
    val e = VecBuild(4, (i: Expr) => IfThenElse(i === 0, x + 1, i * i))
    DotPrinter.save(e, "./img/SimpleVecBuild.dot", nameByVar = Map(x -> "x"))
  }

  test("NestedVecBuild") {
    val x = Param()
    val e = VecBuild(
      4,
      (i: Expr) => VecBuild(3, (j: Expr) => IfThenElse(i === j, x + i, i + j))
    )
    DotPrinter.save(e, "./img/NestedVecBuild.dot", nameByVar = Map(x -> "x"))
  }

  test("VecAccessUnknownIndex") {
    val i = Param()
    val e = VecAccess(VecBuild(5, (j: Expr) => j), i)
    DotPrinter.save(
      e,
      "./img/VecAccessUnknownIndex.dot",
      nameByVar = Map(i -> "i")
    )
  }

  test("VecMap") {
    val v = VecBuild(3, (i: Expr) => 1 + i * i)
    val e = VecMap(VecBuild(3, (i: Expr) => 1 + i * i), (x: Expr) => 2 * x)
    DotPrinter.save(e, "./img/VecMap.dot")
  }

  test("SimpleFunction") {
    val f = (x: Expr) => x - 1
    DotPrinter.save(f, "./img/SimpleFunction.dot")
  }

  test("Let") {
    val x = Param()
    val y = Param()
    val z = Param()
    val e = Let(x, 42, Let(y, x + 1, Let(z, x * x + y * y, z + 2)))
    DotPrinter.save(e, "./img/Let.dot")
  }

  test("StmRange") {
    val n = Param()
    val delta = Param()
    val e = StmRange(n, 0, delta)
    DotPrinter.save(
      e,
      "./img/StmRange.dot",
      nameByVar = Map(n -> "n", delta -> "delta"),
      keepDotFile = true
    )
  }
}
