package mhir.parse.sirop

import com.typesafe.scalalogging.Logger
import mhir.canonicalize._
import mhir.ir._
import mhir.logging.time
import mhir.testing.PerformanceTest
import mhir.typecheck._
import org.scalatest.concurrent.TimeLimits.failAfter
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.time.{Millis, Span}
import org.slf4j.event.Level
import os.Path

import scala.util.Random

@PerformanceTest
class ParserPerformanceTests extends AnyFunSuite {

  val DIR: Path = os.pwd / "src" / "test" / "resources" / "generated_benchmarks"
  val COLLATZ_PATH: Path = this.DIR / "collatz.sirop"
  private val NUM_LINES = 1000
  private val NUM_TESTS = 100
  private val STM_LEN = 128

  private implicit val logger: Logger = Logger(getClass.getName)

  private def setUpCollatz(): Unit = {
    os.makeDir.all(this.DIR)
    val rng = new Random(42)
    val lines =
      (s"const N: u32 = $STM_LEN"
        #:: ""
        #:: "accelerator[no_handshake, out_name=result] foo ="
        #:: "(a: Stm[i16, N]) =>"
        #:: "    a"
        #:: Stream
          .from(0)
          .take(NUM_LINES)
          .flatMap({ _ =>
            // TODO: Come up with a more representative input program
            Seq(
              "    .StmMap( x => (x, x % 2 == 0) ) // check whether number is even",
              "    /* EVEN : divide by two",
              "     * ODD  : multiply by three and add one",
              "     */",
              "    .StmMap( @(x, even) => if (even) then x >> 2 else 3 * x + 1 )"
            )
          })
        #::: Stream
          .from(0)
          .take(NUM_TESTS)
          .flatMap({ _ =>
            val inputs = StmLiteral(
              (0 until STM_LEN)
                .map(_ =>
                  I16.minInt.toInt +
                    rng.nextInt(
                      I16.maxInt.toInt - I16.minInt.toInt
                    )
                )
                .map(C(_)(I16)): _*
            )().tchk()
            val outputs = (0 until STM_LEN).map(_ => C(1)(I16))
            Seq(
              "assert {",
              s"    a = $inputs",
              "}",
              s"yields $outputs"
            )
          }))
        .map(_ + "\n")
    os.write.over(this.COLLATZ_PATH, lines)
  }

  test("collatz") {
    setUpCollatz()
    // TODO: one full second is still disgracefully slow
    failAfter(Span(1000, Millis)) {
      time("collatz", Level.DEBUG) {
        Parser.parse(this.COLLATZ_PATH)
      }
    }
  }
}
