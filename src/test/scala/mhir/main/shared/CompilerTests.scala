package mhir.main.shared

import mhir.ir._
import mhir.optimize.OptimizerOptions
import mhir.parse.sirop.Parser
import org.scalatest.funsuite.AnyFunSuite

import java.time.Duration

class CompilerTests extends AnyFunSuite {

  test("multiple lets") {
    val in = Parser.parse(
      s"""let s = StmRange(5, 0:u8, 1:u8) in
         |let k = 5:u8 in
         |StmMap(s, (x) => x + k)
         |""".stripMargin.stripTrailing
    )
    val options = CompilerOptions(
      targets = Set(NullTarget),
      optFlags = OptimizerOptions.all(
        assumeThroughputsMatch = false,
        maxLetStmBufSize = None
      )
    )
    val out = Compiler.compile(in, options, Duration.ZERO, Duration.ZERO)
    val expected =
      StmLiteral(C(5)(U8), C(6)(U8), C(7)(U8), C(8)(U8), C(9)(U8))()
    assert(mhir.eval.eval(out) == expected)
  }
}
