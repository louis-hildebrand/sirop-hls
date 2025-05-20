package opt

import ir._
import org.scalatest.funsuite.AnyFunSuite

class OptionSimplifierTests extends AnyFunSuite {
  test("NNone") {
    val e = NNone(TyInt).tchk().lower()
    val actual = OptionSimplifier.simplify(e)
    val expected = Tuple(Default(TyInt).lower(), False)()
    assert(actual == expected)
  }

  test("SSome(42, (99, False))") {
    val e = SSome(Tuple(42, Tuple(99, False)())())().tchk().lower()
    val actual = OptionSimplifier.simplify(e)
    val expected = Tuple(Tuple(42, Tuple(99, False)())(), True)()
    assert(actual == expected)
  }

  test("Mux(i === 42, SSome(x), NNone)") {
    val i = Param("i")(TyInt)
    val x = Param("x")(TyInt)
    val e = Mux(i === 42, SSome(x)(), NNone(TyInt))().tchk().lower()
    val actual = OptionSimplifier.simplify(e)
    val expected = Tuple(x, i === 42)()
    assert(actual == expected)
  }

  test("Mux(c, SSome(x), NNone)") {
    val c = Param("c")(TyBool)
    val x = Param("x")(TyInt)
    val e = Mux(c, SSome(x)(), NNone(TyInt))().tchk().lower()
    val actual = OptionSimplifier.simplify(e)
    val expected = Tuple(x, c)()
    assert(actual == expected)
  }
}
