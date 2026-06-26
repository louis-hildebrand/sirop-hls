package mhir.parse.sirop

import org.scalatest.funsuite.AnyFunSuite

class ParserPrinterConsistencyTests extends AnyFunSuite {

  def testSource(src: String): Unit = {
    test(src) {
      val parsed = Parser.parse(src).body
      val printed = parsed.toString
      assert(printed == src)
    }
  }

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
}
