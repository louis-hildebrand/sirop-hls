package mhir.optimize

import mhir.ir.Lowering.ExprLowering
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

/** Various special cases for simplifying common patterns in
  * [[mhir.ir.StmBuild]].
  */
object SpecialCaseStmBuildSimplifier {

  // TODO: It would be nice to have a more general method that handles these
  //       cases.
  //       Maybe try evaluating the first few terms in each recurrence to see
  //       if there are obvious cycles.

  def simplify(stm: StmBuild): StmBuild = {
    val s0 = applyCase0(stm)
    val s1 = applyCase1(s0)
    s1
  }

  private def applyCase0(s: StmBuild): StmBuild = {
    // This method recognizes and simplifies the following trio of
    // accumulators:
    // (in_ctr : u32) = {
    //   init: 0:u32,
    //   next: if (
    //     !is_first_step && in_ctr == 1:u32 && out_ctr == 2:u32
    //       || in_ctr == 0:u32 && out_ctr == 2:u32 && is_first_step
    //   ) then {
    //     0:u32
    //   } else if (is_first_step) then {
    //     1:u32 + in_ctr
    //   } else {
    //     in_ctr
    //   }
    // }
    // (is_first_step : bool) = {
    //   init: true,
    //   next: !is_first_step && in_ctr == 1:u32 && out_ctr == 2:u32
    //     || in_ctr == 0:u32 && out_ctr == 2:u32 && is_first_step
    // }
    // (out_ctr : u32) = {
    //   init: 0:u32,
    //   next: if (
    //     !is_first_step && in_ctr == 1:u32 && out_ctr == 2:u32
    //       || in_ctr == 0:u32 && out_ctr == 2:u32 && is_first_step
    //   ) then {
    //     0:u32
    //   } else {
    //     1:u32 + out_ctr
    //   }
    // }
    //
    // They behave as follows:
    //   | first_step | in_ctr | out_ctr |
    //   | true       |      0 |       0 |
    //   | false      |      1 |       1 |
    //   | false      |      1 |       2 |
    //   | true       |      0 |       0 |
    //   etc.
    s.equations
      .flatMap({
        case (
              fst0,
              (
                True,
                reset @ Or(
                  And(
                    Not(fst1: Param),
                    Equal(in1: Param, IntCst(1)),
                    Equal(out1: Param, IntCst(k1))
                  ),
                  And(
                    Equal(in2: Param, IntCst(0)),
                    Equal(out2: Param, IntCst(k2)),
                    fst2: Param
                  )
                )
              )
            )
            if fst1 == fst0 && fst2 == fst0 && in2 == in1 && out2 == out1 && k1 == k2 =>
          val inMatches = s.equations.get(in1) match {
            case Some(
                  (
                    IntCst(0),
                    Mux(
                      reset1: Or,
                      IntCst(0),
                      Mux(fst3: Param, Sum(IntCst(1), in3), in4)
                    )
                  )
                ) if reset1 == reset && fst3 == fst0 && in3 == in1 && in4 == in1 =>
              true
            case _ =>
              false
          }
          val outMatches = s.equations.get(out1) match {
            case Some(
                  (IntCst(0), Mux(reset2: Or, IntCst(0), Sum(IntCst(1), out3)))
                ) if reset2 == reset && out3 == out1 =>
              true
            case _ =>
              false
          }
          if (inMatches && outMatches) {
            Some((fst0, in1, out1, k1, reset))
          } else {
            None
          }
        case _ => None
      })
      .headOption match {
      case Some((fst, in, out, k, reset)) =>
        val newOutTyp = TyAnyInt.tightest(0, k)
        val newOut = Param(out.prefix)(newOutTyp)
        val subs =
          Map[Expr, Expr](
            reset -> Equal(newOut, C(k)(newOutTyp))().tchk(),
            in -> Mux(fst, C(0)(in.typ), C(1)(in.typ))().tchk(),
            // TODO: What if ReshapeData doesn't work?
            out -> ReshapeData(newOut, out.typ)().tchk().lower()
          )
        val result = StmBuild(
          s.n,
          s.data.subPreserveType(subs),
          s.valid.subPreserveType(subs),
          s.equations
            .filter({ case (x, _) => x != in })
            .filter({ case (x, _) => x != out })
            .map({ case (x, (z, next)) =>
              x -> (z, next.subPreserveType(subs))
            })
            .+(
              newOut -> (
                C(0)(newOutTyp),
                Mux(
                  Equal(newOut, C(k)(newOutTyp))(),
                  C(0)(newOutTyp),
                  Sum(C(1)(newOutTyp), newOut)()
                )()
              )
            )
        )().tchk().asInstanceOf[StmBuild]
        assert(result.typ ~= s.typ)
        assert(!result.hasSyntaxSugar)
        result
      case _ =>
        s
    }
  }

  private def applyCase1(s: StmBuild): StmBuild = {
    // (i : u32) = {
    //   init: 0:u32,
    //   next: if (out_ctr == 2:u2) then {
    //     0:u32
    //   } else {
    //     1:u32 + (if (is_first_step) then { 0:u32 } else { i })
    //   }
    // }
    // (is_first_step : bool) = {
    //   init: true,
    //   next: out_ctr == 2:u2
    // }
    // (out_ctr : u2) = {
    //   init: 0:u2,
    //   next: if (out_ctr == 2:u2) then { 0:u2 } else { 1:u2 + out_ctr }
    // }
    val subs: Map[Expr, Expr] = s.equations
      .flatMap({
        case (
              i,
              (
                IntCst(0),
                Mux(
                  // TODO: Generalize 2
                  Equal(outCtr0: Param, IntCst(k0)),
                  IntCst(0),
                  Sum(IntCst(1), Mux(fst0: Param, IntCst(0), i0: Param))
                )
              )
            ) if i0 == i =>
          val fstMatches = s.equations.get(fst0) match {
            case Some((True, Equal(outCtr1: Param, IntCst(k1)))) =>
              outCtr1 == outCtr0 && k1 == k0
            case _ => false
          }
          val outCtrMatches = s.equations.get(outCtr0) match {
            case Some(
                  (
                    IntCst(0),
                    Mux(
                      Equal(outCtr1: Param, IntCst(k1)),
                      IntCst(0),
                      Sum(IntCst(1), outCtr2: Param)
                    )
                  )
                ) =>
              outCtr1 == outCtr0 && outCtr2 == outCtr0 && k1 == k0
            case _ => false
          }
          if (fstMatches && outCtrMatches) {
            Map[Expr, Expr](i -> ReshapeData(outCtr0, i.typ)().tchk().lower())
          } else {
            Map[Expr, Expr]()
          }
        case _ => Map[Expr, Expr]()
      })
    val result = StmBuild(
      s.n,
      s.data.subPreserveType(subs),
      s.valid.subPreserveType(subs),
      s.equations.map({ case (x, (z, next)) =>
        x -> (z, next.subPreserveType(subs))
      })
    )().tchk().asInstanceOf[StmBuild]
    assert(result.typ ~= s.typ)
    assert(!result.hasSyntaxSugar)
    result
  }
}
