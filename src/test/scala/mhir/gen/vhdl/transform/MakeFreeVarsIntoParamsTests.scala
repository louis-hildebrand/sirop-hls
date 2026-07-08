package mhir.gen.vhdl
package transform

import mhir.canonicalize._
import mhir.ir._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

import scala.collection.immutable.ListMap

class MakeFreeVarsIntoParamsTests extends AnyFunSuite {

  test("OneFunction") {
    val x = Param("x")(U8)
    val y = Param("y")(U8)
    val z = Param("z")(TyBool)
    val w = Param("w")(U8)
    val p = Param("p")(TyStm(U8, 16))
    val data = Param("data")(TyTuple(TyTuple(U8, TyBool), U8, U8))
    // Notice how f accesses the variables y and z, which are not parameters
    val fBody = /* x => */ Tuple(Sum(x, y)(), z)().tchk()
    val f = Param("f")(x.typ ->: fBody.typ)
    val original = GenStmBuild(
      data = data,
      valid = FunCall(f, C(0)(U8))().__1.tchk(),
      accumulators = Map(
        y -> (C(0)(U8), Sum(C(1)(U8), y)()),
        z -> (True, Not(z)().tchk()),
        w -> (C(0)(U8), FunCall(f, y)().tchk())
      ),
      producers = Map(
        p -> (p, FunCall(f, C(0)(U8))().__1.tchk())
      ),
      intermediates = ListMap(
        f -> FunctionIntermediate(Seq(x), ListMap(), fBody),
        data -> DataIntermediate(
          Tuple(FunCall(f, C(42)(U8))(), StmData(p)(), w)().tchk()
        )
      )
    )
    val actual = MakeFreeVarsIntoParams(original)

    val f2Body = fBody
    val f2 = f
      .rebuild(TyArrow(TyTuple(U8, U8, TyBool), f2Body.typ))
      .asInstanceOf[Param]
    val expected = GenStmBuild(
      data = data,
      valid = FunCall(f2, Tuple(C(0)(U8), y, z)())().__1.tchk(),
      accumulators = Map(
        y -> (C(0)(U8), Sum(C(1)(U8), y)()),
        z -> (True, Not(z)().tchk()),
        w -> (C(0)(U8), FunCall(f2, Tuple(y, y, z)())().tchk())
      ),
      producers = Map(
        p -> (p, FunCall(f2, Tuple(C(0)(U8), y, z)())().__1.tchk())
      ),
      intermediates = ListMap(
        f2 -> FunctionIntermediate(Seq(x, y, z), ListMap(), f2Body),
        data -> DataIntermediate(
          Tuple(FunCall(f2, Tuple(C(42)(U8), y, z)())(), StmData(p)(), w)()
            .tchk()
        )
      )
    )
    assert(actual == expected)
  }

  test("TwoFunctions") {
    val x = Param("x")(U16)
    val a = Param("a")(U16)
    val b = Param("b")(U16)
    val c = Param("c")(U16)
    val d = Param("d")(U16)
    val fBody = /* x => */ Sum(d, Sum(x, a)())().tchk()
    val f = Param("f")(U16 ->: fBody.typ)
    val gBody = /* a => */ Sum(FunCall(f, a)(), b, d)().tchk()
    val g = Param("g")(U16 ->: gBody.typ)
    val hBody = /* x => */ Prod(FunCall(g, x)(), FunCall(f, c)())().tchk()
    val h = Param("h")(U16 ->: hBody.typ)
    val kBody = /* x => */ Prod(x, x)().tchk()
    val k = Param("k")(U16 ->: kBody.typ)
    val original = GenStmBuild(
      data = FunCall(k, FunCall(h, C(42)(U16))())().tchk(),
      valid = True,
      accumulators = Map(
        a -> (C(0)(U16), Sum(C(1)(U16), a)().tchk()),
        b -> (C(42)(U16), WrappingDiff(a, C(1)(U16))().tchk()),
        c -> (C(1)(U16), Sum(C(1)(U16), a)().tchk()),
        d -> (C(9)(U16), Sum(C(1)(U16), d)().tchk())
      ),
      producers = Map(),
      intermediates = ListMap(
        f -> FunctionIntermediate(Seq(x), ListMap(), fBody),
        g -> FunctionIntermediate(Seq(a), ListMap(), gBody),
        h -> FunctionIntermediate(Seq(x), ListMap(), hBody),
        k -> FunctionIntermediate(Seq(x), ListMap(), kBody)
      )
    )
    val actual = MakeFreeVarsIntoParams(original)

    val FunctionIntermediate(Seq(a2, _*), _, _) = actual.intermediates(g)
    val f2Body = /* (x, a, d) => */ Sum(d, Sum(x, a)())().tchk()
    val f2 = f.rebuild((U16, U16, U16) ->: f2Body.typ).asInstanceOf[Param]
    val g2Body = /* (a2, a, b, d) => */ Sum(
      FunCall(f2, Tuple(a2, a, d)())(),
      b,
      d
    )().tchk()
    val g2 =
      g.rebuild(TyTuple(U16, U16, U16, U16) ->: g2Body.typ).asInstanceOf[Param]
    val h2Body = /* (x, a, b, c, d) => */ Prod(
      FunCall(g2, Tuple(x, a, b, d)())(),
      FunCall(f2, Tuple(c, a, d)())()
    )().tchk()
    val h2 = h
      .rebuild(TyTuple(U16, U16, U16, U16, U16) ->: h2Body.typ)
      .asInstanceOf[Param]
    val k2Body = kBody
    val k2 = k.rebuild(TyTuple(U16) ->: k2Body.typ).asInstanceOf[Param]
    val expected = GenStmBuild(
      data = FunCall(
        k2,
        Tuple(FunCall(h2, Tuple(C(42)(U16), a, b, c, d)())())()
      )().tchk(),
      valid = True,
      accumulators = Map(
        a -> (C(0)(U16), Sum(C(1)(U16), a)().tchk()),
        b -> (C(42)(U16), WrappingDiff(a, C(1)(U16))().tchk()),
        c -> (C(1)(U16), Sum(C(1)(U16), a)().tchk()),
        d -> (C(9)(U16), Sum(C(1)(U16), d)().tchk())
      ),
      producers = Map(),
      intermediates = ListMap(
        f2 -> FunctionIntermediate(Seq(x, a, d), ListMap(), f2Body),
        g2 -> FunctionIntermediate(Seq(a2, a, b, d), ListMap(), g2Body),
        h2 -> FunctionIntermediate(Seq(x, a, b, c, d), ListMap(), h2Body),
        k2 -> FunctionIntermediate(Seq(x), ListMap(), k2Body)
      )
    )
    assert(actual == expected)
  }
}
