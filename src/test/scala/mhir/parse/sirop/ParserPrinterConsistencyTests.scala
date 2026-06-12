package mhir.parse.sirop

import org.scalatest.funsuite.AnyFunSuite

class ParserPrinterConsistencyTests extends AnyFunSuite {

  def testBinop(op: String): Unit = {
    test(op) {
      val src = s"x $op y"
      val parsed = Parser.parse(src).body
      val printed = parsed.toString
      assert(printed == src)
    }
  }

  testBinop("==")
  testBinop("==`")
  testBinop("!=")
  testBinop("<`")
  testBinop("<")
  testBinop(">")
  testBinop("<=")
  testBinop(">=")
}
