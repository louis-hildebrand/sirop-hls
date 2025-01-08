package debug

import ir.*
import org.scalatest.funsuite.AnyFunSuite

class DotPrinterTests extends AnyFunSuite {
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

  test("LogicalOp") {
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
}
