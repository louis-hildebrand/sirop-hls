package mhir.parse.sirop

import org.scalatest.funsuite.AnyFunSuite

class ParserPrinterConsistencyTests extends AnyFunSuite {

  def testUnary(op: String): Unit = {
    test(s"Unary $op") {
      val src = s"${op}x"
      val parsed = Parser.parse(src).body
      val printed = parsed.toString
      assert(printed == src)
    }
  }

  testUnary("!")
  testUnary("~")

  def testBinop(op: String): Unit = {
    test(op) {
      val src = s"x $op y"
      val parsed = Parser.parse(src).body
      val printed = parsed.toString
      assert(printed == src)
    }
  }

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
    test(f) {
      val src = s"$f(${args.mkString(", ")})"
      val parsed = Parser.parse(src).body
      val printed = parsed.toString
      assert(printed == src)
    }
  }

  testFunCallLike("bits")
  testFunCallLike("interpret_as:[(Vec[(u8, i8), 4:u3], bool)]")
}
