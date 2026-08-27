package mhir.parse.sirop

import mhir.ir._
import org.scalatest.funsuite.AnyFunSuite

class ParserPrinterConsistencyTests extends AnyFunSuite {

  def testSource(src: String): Unit = {
    test(src) {
      val parsed = Parser.parse(src).body
      val printed = parsed.toString
      assert(printed == src)
    }
  }

  testSource("undefined:u8")
  testSource("undefined:(u8, bool)")

  def testUnary(op: String): Unit = testSource(s"${op}x")

  testUnary("!")
  testUnary("~")

  def testBinop(op: String): Unit = testSource(s"x $op y")

  // Relational operators
  testBinop("==")
  testBinop("==`")
  testBinop("!=")
  testBinop("<`")
  testBinop("<")
  testBinop(">")
  testBinop("<=")
  testBinop(">=")

  // Arithmetic
  testBinop("+")
  testBinop("+`")
  testBinop("+%")
  testBinop("+%`")
  testBinop("+^")
  testBinop("-")
  testBinop("-%")
  testBinop("-%`")
  testBinop("-^")
  testBinop("*")
  testBinop("*`")
  testBinop("*%")
  testBinop("*%`")
  testBinop("*^")
  testBinop("/")
  testBinop("/`")
  testBinop("%")
  testBinop("%`")

  // Bitwise
  testBinop("&")
  testBinop("|")

  def testFunCallLike(f: String, args: Seq[String] = Seq("x")): Unit = {
    testSource(s"$f(${args.mkString(", ")})")
  }

  testFunCallLike("bits")
  testFunCallLike("interpret_as:[(Vec[(u8, i8), 4:u3], bool)]")

  testSource("zeros:[(i16, bool)]()")
  testSource("ones:[(i16, bool)]()")
  testSource("if (c1) then { x } else if (c2) then { y } else { z }")
  testSource("iff (c1) then { x } else iff (c2) then { y } else { z }")

  def testType(src: String): Unit = {
    test(src) {
      val fullSrc = s"(x : $src) => x"
      val Function(x, _) = Parser.parse(fullSrc).body
      val parsed = x.typ
      val printed = parsed.toString
      assert(printed == src)
    }
  }

  testType("bool")
  testType("i32")
  testType("u16")
  testType("()")
  testType("(u8,)")
  testType("((i32, bool), bool, i16)")
  testType("Vec[(u8, bool), 42:u6]")
  testType("Stm[(u8, bool), 42:u6]")
  testType("u8 -> (u8 -> u8) -> u8")
}
