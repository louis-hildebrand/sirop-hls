package mhir.gen

import mhir.ir._
import org.scalatest.funsuite.AnyFunSuite

class BinaryTests extends AnyFunSuite {

  test("10:u8") {
    val actual = Binary.int2bin(C(10)(U8))
    val expected = "00001010"
    assert(actual == expected)
  }

  test("13:i8") {
    val actual = Binary.int2bin(C(13)(I8))
    val expected = "00001101"
    assert(actual == expected)
  }

  test("-7:i8") {
    val actual = Binary.int2bin(C(-7)(I8))
    val expected = "11111001"
    assert(actual == expected)
  }

  test("42:u65") {
    val actual = Binary.int2bin(C(42)(TyUInt(65)))
    val expected =
      "00000000000000000000000000000000000000000000000000000000000101010"
    assert(actual == expected)
  }

  test("41:i65") {
    val actual = Binary.int2bin(C(41)(TySInt(65)))
    val expected =
      "00000000000000000000000000000000000000000000000000000000000101001"
    assert(actual == expected)
  }

  test("-34567:i65") {
    val actual = Binary.int2bin(C(-34567)(TySInt(65)))
    val expected =
      "11111111111111111111111111111111111111111111111110111100011111001"
    assert(actual == expected)
  }
}
