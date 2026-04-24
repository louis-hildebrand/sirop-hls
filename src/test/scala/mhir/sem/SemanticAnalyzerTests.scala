package mhir.sem

import mhir.canonicalize._
import mhir.ir._
import mhir.sugar._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

class SemanticAnalyzerTests extends AnyFunSuite {

  test("CheckNames:OK") {
    val x = Param("x", -1)(TyStm(U16, 100))
    val y = Param("y", -1)(TyStm(U16, 100))
    val z = Param("z", -1)(TyStm(U16, 100))
    val f = Function(
      x,
      Function(
        y,
        Function(z, SimpleZip(SimpleMap(x, x => x + C(5)(U16)), y, z))()
      )()
    )().tchk().lower
    val prog = Program(f)
    SemanticAnalyzer.checkNames(prog)
  }

  test("CheckNames:DuplicateParameter") {
    val x = Param("x", -1)(TyStm(U16, 100))
    val y = Param("y", -1)(TyStm(U16, 100))
    val f = Function(
      x,
      Function(
        x,
        Function(y, SimpleZip(SimpleMap(x, x => x + C(5)(U16)), y))()
      )()
    )().tchk().lower
    val prog = Program(f)
    val ex = intercept[SemanticError](SemanticAnalyzer.checkNames(prog))
    assert(ex.msg == "duplicate parameter in top-level function: x")
  }

  test("CheckNames:DuplicateParameters") {
    val x = Param("x", -1)(TyStm(U16, 100))
    val y = Param("y", -1)(TyStm(U16, 100))
    val z = Param("z", -1)(TyStm(U16, 100))
    val f = Function(
      z,
      Function(
        z,
        Function(
          y,
          Function(
            x,
            Function(x, SimpleZip(SimpleMap(x, x => x + C(5)(U16)), y, z))()
          )()
        )()
      )()
    )().tchk().lower
    val prog = Program(f)
    val ex = intercept[SemanticError](SemanticAnalyzer.checkNames(prog))
    assert(ex.msg == "duplicate parameters in top-level function: x, z")
  }

  test("CheckNames:OutNameMatchesInName") {
    val x = Param("x", -1)(TyStm(U16, 100))
    val f = Function(x, SimpleMap(x, x => x + C(5)(U16)))().tchk().lower
    val prog = Program("top", Map("out_name" -> x), Seq(), f)
    val ex = intercept[SemanticError](SemanticAnalyzer.checkNames(prog))
    assert(ex.msg == "output name 'x' is already used for an input")
  }

  test("Handshake:StmConcat") {
    val n = C(10)(U8)
    val m = C(20)(U8)
    val e = SimpleMap(
      StmConcat(StmCount(n)(), StmCount(m)())().tchk(),
      x => x * x
    ).tchk().lower
    val prog = Program("top", Map(), Seq(), e)
    SemanticAnalyzer.check(prog)
  }

  test("NoHandshake:StmConcat") {
    val n = C(10)(U8)
    val m = C(20)(U8)
    val e = SimpleMap(
      StmConcat(StmCount(n)(), StmCount(m)())().tchk(),
      x => x * x
    ).tchk().lower
    val prog = Program("top", Map("no_handshake" -> True), Seq(), e)
    val ex = intercept[SemanticError](SemanticAnalyzer.check(prog))
    assert(
      ex.msg == "stream operator StmConcat cannot be used without the handshake protocol: it is not always ready to receive input"
    )
  }

  test("Handshake:StmSlide") {
    val n = C(20)(U8)
    val e = SimpleMap(
      StmSlide(StmCount(n)(), 2)().tchk(),
      v => VecAccess(v, 0)() + VecAccess(v, 1)()
    ).tchk().lower
    val prog = Program("top", Map(), Seq(), e)
    SemanticAnalyzer.check(prog)
  }

  test("NoHandshake:StmSlide") {
    val n = C(20)(U8)
    val e = SimpleMap(
      StmSlide(StmCount(n)(), 2)().tchk(),
      v => VecAccess(v, 0)() + VecAccess(v, 1)()
    ).tchk().lower
    val prog = Program("top", Map("no_handshake" -> True), Seq(), e)
    val ex = intercept[SemanticError](SemanticAnalyzer.check(prog))
    assert(
      ex.msg == "stream operator StmSlide cannot be used without the handshake protocol: its output is not always valid"
    )
  }

  test("NoHandshake:StmSlide2D") {
    val n = C(16)(U8)
    val m = C(16)(U8)
    val e = StmSlide2D(StmCount2D(n, m)(), 3, 3)().tchk().lower
    val prog = Program("top", Map("no_handshake" -> True), Seq(), e)
    val ex = intercept[SemanticError](SemanticAnalyzer.check(prog))
    assert(
      ex.msg == "stream operator StmSlide2D cannot be used without the handshake protocol: its output is not always valid"
    )
  }

  test("NoHandshake:StmReduce") {
    val n = C(20)(U8)
    val e = StmReduce(
      StmCount(n)(),
      (U8, U8) ::+ (x => x.__0 + x.__1)
    )().tchk().lower
    val prog = Program("top", Map("no_handshake" -> True), Seq(), e)
    val ex = intercept[SemanticError](SemanticAnalyzer.check(prog))
    assert(
      ex.msg == "stream operator StmReduce cannot be used without the handshake protocol: its output is not always valid"
    )
  }
}
