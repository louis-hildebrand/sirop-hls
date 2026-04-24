package mhir.gen.vhdl

import mhir.canonicalize._
import mhir.gen.CodegenError
import mhir.ir._
import mhir.sugar._
import mhir.typecheck._
import org.scalatest.funsuite.AnyFunSuite

/** Tests for the error messages raised by the VHDL generator.
  *
  * These are separate from [[VhdlGeneratorTests]] because those are marked with
  * [[mhir.testing.HardwareTest]], so I often skip them. The error cases do not
  * need ModelSim, so I can run them more regularly (e.g., in the pre-commit
  * hook).
  */
class VhdlGeneratorErrorTests extends AnyFunSuite {

  test("s => StmConcat(s, s)") {
    val n = 5
    val s = Param("s", -1)(TyStm(U8, n))
    val concat = StmConcat(s, s)().tchk().lower
    val f = Function(s, concat)().tchk().asInstanceOf[Function]
    val exc = intercept[IllegalArgumentException](
      VhdlGenerator.emitVhdl(f, VhdlTestRunner.VHDL_TEST_DIR)
    )
    assert(exc.getMessage.contains("Top-level parameter s is used 2 times."))
  }

  test("s => StmZip(StmPrefix(s, 2), StmSuffix(s, 2))") {
    val n = 5
    val s = Param("s", -1)(TyStm(U8, n))
    val zip = StmZip(StmPrefix(s, 2)(), StmSuffix(s, 2)())().tchk().lower
    val f = Function(s, zip)().tchk().asInstanceOf[Function]
    val exc = intercept[IllegalArgumentException](
      VhdlGenerator.emitVhdl(f, VhdlTestRunner.VHDL_TEST_DIR)
    )
    assert(exc.getMessage.contains("Top-level parameter s is used 2 times."))
  }

  test("s => let s0 = s in StmZip(s0, s)") {
    val n = 5
    val s = Param("my_input", -1)(TyStm(U8, n))
    val s0 = Param("s0")(TyStm(U8, n))
    val zip = StmZip(s0, s)().tchk().lower
    val f = Function(s, LetStm(1, s0, s, zip)())().tchk().asInstanceOf[Function]
    val exc = intercept[IllegalArgumentException](
      VhdlGenerator.emitVhdl(f, VhdlTestRunner.VHDL_TEST_DIR)
    )
    assert(
      exc.getMessage.contains("Top-level parameter my_input is used 2 times.")
    )
  }

  test("LetStm:NonzeroBufferWithoutHandshaking") {
    val s = Param("s")(TyStm(U8, 10))
    val f = Function(
      s,
      LetStm(1, s, s, SimpleZip(s, SimpleMap(s, x => x + C(5)(U8))))()
    )().tchk().lower
    val options = VhdlGeneratorOptions(handshake = false)
    val exc = intercept[CodegenError](
      VhdlGenerator.emitVhdl(f, VhdlTestRunner.VHDL_TEST_DIR, options)
    )
    assert(
      exc.getMessage.contains(
        "cannot generate letstm with a nonzero buffer size when the handshake protocol is disabled"
      )
    )
  }

  test("ReservedWordInEntityName:Map") {
    val f = TyStm(U8, 64) ::+ (s => SimpleMap(s, x => x + C(5)(U8)))
    val options = VhdlGeneratorOptions(topName = "map")
    val exc = intercept[CodegenError](
      VhdlGenerator.emitVhdl(f, VhdlTestRunner.VHDL_TEST_DIR, options)
    )
    assert(
      exc.getMessage.contains(
        "cannot generate entity 'map', since its name is a reserved keyword in VHDL"
      )
    )
  }

  test("ReservedWordInEntityName:Buffer") {
    val f = TyStm(U8, 64) ::+ (s => SimpleNop(s))
    val options = VhdlGeneratorOptions(topName = "buffer")
    val exc = intercept[CodegenError](
      VhdlGenerator.emitVhdl(f, VhdlTestRunner.VHDL_TEST_DIR, options)
    )
    assert(
      exc.getMessage.contains(
        "cannot generate entity 'buffer', since its name is a reserved keyword in VHDL"
      )
    )
  }

  test("ReservedWordInInputStreamName:Begin") {
    val x = Param("begin", -1)(TyStm(I16, 100))
    val f = Function(x, SimpleMap(x, x => x * C(3)(I16)))().tchk().lower
    val options = VhdlGeneratorOptions(handshake = false)
    val exc = intercept[CodegenError](
      VhdlGenerator.emitVhdl(f, VhdlTestRunner.VHDL_TEST_DIR, options)
    )
    assert(
      exc.getMessage.contains(
        "'begin' cannot be used as an input stream name, since it is a reserved keyword in VHDL"
      )
    )
  }

  test("ReservedWordInInputStreamName:Body") {
    val x = Param("body", -1)(TyStm(I16, 100))
    val f = Function(x, SimpleMap(x, x => x * C(3)(I16)))().tchk().lower
    val options = VhdlGeneratorOptions(handshake = false)
    val exc = intercept[CodegenError](
      VhdlGenerator.emitVhdl(f, VhdlTestRunner.VHDL_TEST_DIR, options)
    )
    assert(
      exc.getMessage.contains(
        "'body' cannot be used as an input stream name, since it is a reserved keyword in VHDL"
      )
    )
  }

  test("ReservedWordInOutputStreamName:Out") {
    val x = Param("s", -1)(TyStm(I16, 100))
    val f = Function(x, SimpleMap(x, x => x * C(3)(I16)))().tchk().lower
    val options = VhdlGeneratorOptions(handshake = false, outName = Some("out"))
    val exc = intercept[CodegenError](
      VhdlGenerator.emitVhdl(f, VhdlTestRunner.VHDL_TEST_DIR, options)
    )
    assert(
      exc.getMessage.contains(
        "'out' cannot be used as an output stream name, since it is a reserved keyword in VHDL"
      )
    )
  }

  test("ReservedWordInOutputStreamName:Next") {
    val x = Param("s", -1)(TyStm(I16, 100))
    val f = Function(x, SimpleMap(x, x => x * C(3)(I16)))().tchk().lower
    val options =
      VhdlGeneratorOptions(handshake = false, outName = Some("next"))
    val exc = intercept[CodegenError](
      VhdlGenerator.emitVhdl(f, VhdlTestRunner.VHDL_TEST_DIR, options)
    )
    assert(
      exc.getMessage.contains(
        "'next' cannot be used as an output stream name, since it is a reserved keyword in VHDL"
      )
    )
  }

  test("NameClash:InputStreamCalledData") {
    val x = Param("data", -1)(TyStm(I16, 100))
    val f = Function(x, SimpleMap(x, x => x * C(3)(I16)))().tchk().lower
    val options = VhdlGeneratorOptions(handshake = false)
    val exc = intercept[CodegenError](
      VhdlGenerator.emitVhdl(f, VhdlTestRunner.VHDL_TEST_DIR, options)
    )
    assert(
      exc.getMessage.contains(
        "'data' cannot be used as an input stream name, since it is also used for the output stream"
      )
    )
  }
}
