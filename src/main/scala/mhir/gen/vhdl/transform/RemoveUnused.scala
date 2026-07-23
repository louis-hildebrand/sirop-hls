package mhir.gen.vhdl.transform

import mhir.gen.vhdl.GenStmBuild
import mhir.ir.Param

import scala.annotation.tailrec

object RemoveUnused {

  def apply(s: GenStmBuild): GenStmBuild = {
    @tailrec
    def fix(s: GenStmBuild): GenStmBuild = {
      val used = this.findUsed(s)
      val next = GenStmBuild(
        data = s.data,
        valid = s.valid,
        accumulators = s.accumulators
          .filter({ case (x, _) => used.contains(x) }),
        producers = s.producers
          .filter({ case (x, _) => used.contains(x) }),
        intermediates = s.intermediates
          .filter({ case (x, _) => used.contains(x) })
      )
      if (next == s) {
        s
      } else {
        // Some other intermediates might have been used before, but not anymore
        fix(next)
      }
    }
    fix(s)
  }

  private def findUsed(s: GenStmBuild): Set[Param] = {
    s.data.freeVars ++ s.valid.freeVars ++
      s.accumulators.flatMap({ case (_, acc) => acc.freeVars }) ++
      s.producers.flatMap({ case (_, (_, ready)) => ready.freeVars }) ++
      s.intermediates.flatMap({ case (_, i) => i.freeVars })
  }
}
