package mhir.main.stored

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.main.shared.BadArgsException
import mhir.parse.AetherlingParser
import mhir.parse.sirop.Parser
import mhir.sugar._
import os.Path

/** A collection of pre-written programs.
  */
object Program {

  private val ResourcesDir: Path =
    os.pwd / "src" / "main" / "resources" / "mhir" / "main" / "stored"

  val MatVecSize: Int = 256

  def apply(name: String): Expr = {
    name.toLowerCase match {
      case "map"         => Parser.parse(ResourcesDir / "map.sirop")
      case "shir:map"    => Parser.parse(ResourcesDir / "map.sirop")
      case "dot"         => Parser.parse(ResourcesDir / "dot.sirop")
      case "shir:dot"    => Parser.parse(ResourcesDir / "dot.sirop")
      case "matvec"      => Parser.parse(ResourcesDir / "matvec.sirop")
      case "shir:matvec" => Parser.parse(ResourcesDir / "matvec.sirop")
      case "smallmatmat" => Parser.parse(ResourcesDir / "small_matmat.sirop")
      case "shir:smallmatmat" =>
        Parser.parse(ResourcesDir / "small_matmat.sirop")
      case "matmat"             => Parser.parse(ResourcesDir / "matmat.sirop")
      case "shir:matmat"        => Parser.parse(ResourcesDir / "matmat.sirop")
      case "conv1d"             => Parser.parse(ResourcesDir / "conv1d.sirop")
      case "shir:conv1d"        => Parser.parse(ResourcesDir / "conv1d.sirop")
      case "conv2d"             => Parser.parse(ResourcesDir / "conv2d.sirop")
      case "shir:conv2d"        => Parser.parse(ResourcesDir / "conv2d.sirop")
      case "aetherling:conv2d"  => AetherlingConv2d
      case "convb2b"            => Parser.parse(ResourcesDir / "convb2b.sirop")
      case "shir:convb2b"       => Parser.parse(ResourcesDir / "convb2b.sirop")
      case "aetherling:convb2b" => AetherlingConvB2b
      case "sharpen"            => Parser.parse(ResourcesDir / "sharpen.sirop")
      case "shir:sharpen"       => Parser.parse(ResourcesDir / "sharpen.sirop")
      case "aetherling:sharpen" => AetherlingSharpen
      case "sobel"              => Parser.parse(ResourcesDir / "sobel.sirop")
      case "shir:sobel"         => Parser.parse(ResourcesDir / "sobel.sirop")
      case "aetherling:sobel"   => AetherlingSobel
      case "camera"             => Parser.parse(ResourcesDir / "camera.sirop")
      case "shir:camera"        => Parser.parse(ResourcesDir / "camera.sirop")
      case "aetherling:camera"  => AetherlingCamera
      case str if str.startsWith("matvec_") =>
        val parStr = str.substring("matvec_".length)
        val par = parStr.toInt
        MatVecMul(
          width = MatVecSize,
          height = MatVecSize,
          par = par,
          uint = U16
        )
      case "sqrt" => Sqrt
      case name =>
        throw new BadArgsException(s"unknown program: $name")
    }
  }

  private def blurKernel(int: Type) = {
    VecLiteral(
      VecLiteral(C(1)(int), C(2)(int), C(1)(int))(),
      VecLiteral(C(2)(int), C(4)(int), C(2)(int))(),
      VecLiteral(C(1)(int), C(2)(int), C(1)(int))()
    )()
  }

  private def makeConv3x3(
      width: Int,
      height: Int,
      input: Expr,
      kernelVec: Expr,
      kernelCoeff: Expr
  ): Expr = {
    val TyStm(uint, _) = input.typ
    val kernelStm = StmCst(width * height, kernelVec)()
    val row0Elem0 = Param("row_0_0")(TyStm(uint, width * height))
    val row0Elem1 = Param("row_0_1")(TyStm(uint, width * height))
    val row0Elem2 = Param("row_0_2")(TyStm(uint, width * height))
    val row0 = Param("row_0")(TyStm(TyVec(uint, 3), width * height))
    val row1Elem0 = Param("row_1_0")(TyStm(uint, width * height))
    val row1Elem1 = Param("row_1_1")(TyStm(uint, width * height))
    val row1Elem2 = Param("row_1_2")(TyStm(uint, width * height))
    val row1 = Param("row_1")(TyStm(TyVec(uint, 3), width * height))
    val row2Elem0 = Param("row_2_0")(TyStm(uint, width * height))
    val row2Elem1 = Param("row_2_1")(TyStm(uint, width * height))
    val row2Elem2 = Param("row_2_2")(TyStm(uint, width * height))
    val row2 = Param("row_2")(TyStm(TyVec(uint, 3), width * height))
    val window =
      Param("window")(TyStm(TyVec(TyVec(uint, 3), 3), width * height))
    val windowAndKernelZipped =
      Param("w_k_zip")(TyStm(TyVec(TyVec((uint, uint), 3), 3), width * height))
    val windowAndKernelMultiplied =
      Param("w_k_mul")(TyStm(TyVec(TyVec(uint, 3), 3), width * height))
    val rowSums =
      Param("row_sums")(TyStm(TyVec(uint, 3), width * height))
    val colSums =
      Param("col_sums")(TyStm(uint, width * height))
    val result =
      Param("result")(TyStm(uint, width * height))
    val conv = Lets(
      // Shift
      row2Elem2 -> input,
      row2Elem1 -> StmShiftRightGarbage(row2Elem2, 1)(),
      row2Elem0 -> StmShiftRightGarbage(row2Elem1, 1)(),
      row1Elem2 -> StmShiftRightGarbage(row2Elem2, width)(),
      row1Elem1 -> StmShiftRightGarbage(row1Elem2, 1)(),
      row1Elem0 -> StmShiftRightGarbage(row1Elem1, 1)(),
      row0Elem2 -> StmShiftRightGarbage(row1Elem2, width)(),
      row0Elem1 -> StmShiftRightGarbage(row0Elem2, 1)(),
      row0Elem0 -> StmShiftRightGarbage(row0Elem1, 1)(),
      // Join shifted streams into window
      row0 -> StmMap(
        StmZip(row0Elem0, StmZip(row0Elem1, row0Elem2)())(),
        TyTuple(uint, TyTuple(uint, uint)) ::+ (x =>
          VecLiteral(x.__0, x.__1.__0, x.__1.__1)()
        )
      )(),
      row1 -> StmMap(
        StmZip(row1Elem0, StmZip(row1Elem1, row1Elem2)())(),
        TyTuple(uint, TyTuple(uint, uint)) ::+ (x =>
          VecLiteral(x.__0, x.__1.__0, x.__1.__1)()
        )
      )(),
      row2 -> StmMap(
        StmZip(row2Elem0, StmZip(row2Elem1, row2Elem2)())(),
        TyTuple(uint, TyTuple(uint, uint)) ::+ (x =>
          VecLiteral(x.__0, x.__1.__0, x.__1.__1)()
        )
      )(),
      window -> StmMap(
        StmZip(row0, StmZip(row1, row2)())(),
        TyTuple(TyVec(uint, 3), TyTuple(TyVec(uint, 3), TyVec(uint, 3))) ::+ (
          x => VecLiteral(x.__0, x.__1.__0, x.__1.__1)()
        )
      )(),
      // Multiply with kernel
      windowAndKernelZipped -> StmMap(
        StmZip(window, kernelStm)(),
        (TyVec(TyVec(uint, 3), 3), TyVec(TyVec(uint, 3), 3)) ::+ (x =>
          VecMap2(
            x.__0,
            x.__1,
            TyVec(uint, 3) ::+ (v1 =>
              TyVec(uint, 3) ::+ (v2 => VecZip(v1, v2)())
            )
          )()
        )
      )(),
      windowAndKernelMultiplied -> StmMap(
        windowAndKernelZipped,
        TyVec(TyVec((uint, uint), 3), 3) ::+ (v =>
          VecMap(
            v,
            TyVec((uint, uint), 3) ::+ (v =>
              VecMap(v, (uint, uint) ::+ (x => x.__0 *% x.__1))()
            )
          )()
        )
      )(),
      rowSums -> StmMap(
        windowAndKernelMultiplied,
        TyVec(TyVec(uint, 3), 3) ::+ (v =>
          VecMap(
            v,
            TyVec(uint, 3) ::+ (v =>
              VecAccess(
                VecReduceComb(v, (uint, uint) ::+ (x => x.__0 +% x.__1))(),
                0
              )()
            )
          )()
        )
      )(),
      colSums -> StmMap(
        rowSums,
        TyVec(uint, 3) ::+ (v =>
          VecAccess(
            VecReduceComb(v, (uint, uint) ::+ (x => x.__0 +% x.__1))(),
            0
          )()
        )
      )(),
      result -> StmMap(
        colSums,
        uint ::+ (x =>
          kernelCoeff.typ match {
            case _: TyFix    => IntFixProd(x, kernelCoeff)()
            case _: TyAnyInt => x *% kernelCoeff
            case _           => ???
          }
        )
      )()
    )(
      result
    )
    conv.tchk()
  }

  private def makeConv2x2(width: Int, height: Int, input: Expr): Expr = {
    val TyStm(uint, _) = input.typ
    val kernelStm = StmCst(
      width * height,
      VecLiteral(
        VecLiteral(C(1)(uint), C(4)(uint))(),
        VecLiteral(C(2)(uint), C(1)(uint))()
      )()
    )()
    val kernelCoeff = FixCst(16)(TyFix(U8, 7))
    val row0Elem0 = Param("row_0_0")(TyStm(uint, width * height))
    val row0Elem1 = Param("row_0_1")(TyStm(uint, width * height))
    val row0 = Param("row_0")(TyStm(TyVec(uint, 2), width * height))
    val row1Elem0 = Param("row_1_0")(TyStm(uint, width * height))
    val row1Elem1 = Param("row_1_1")(TyStm(uint, width * height))
    val row1 = Param("row_1")(TyStm(TyVec(uint, 2), width * height))
    val window =
      Param("window")(TyStm(TyVec(TyVec(uint, 2), 2), width * height))
    val windowAndKernelZipped =
      Param("w_k_zip")(TyStm(TyVec(TyVec((uint, uint), 2), 2), width * height))
    val windowAndKernelMultiplied =
      Param("w_k_mul")(TyStm(TyVec(TyVec(uint, 2), 2), width * height))
    val rowSums =
      Param("row_sums")(TyStm(TyVec(uint, 2), width * height))
    val colSums =
      Param("col_sums")(TyStm(uint, width * height))
    val result =
      Param("result")(TyStm(uint, width * height))
    val conv = Lets(
      // Shift
      row1Elem1 -> input,
      row1Elem0 -> StmShiftRightGarbage(row1Elem1, 1)(),
      row0Elem1 -> StmShiftRightGarbage(row1Elem1, width)(),
      row0Elem0 -> StmShiftRightGarbage(row0Elem1, 1)(),
      // Join shifted streams into window
      row0 -> StmMap(
        StmZip(row0Elem0, row0Elem1)(),
        TyTuple(uint, uint) ::+ (x => VecLiteral(x.__0, x.__1)())
      )(),
      row1 -> StmMap(
        StmZip(row1Elem0, row1Elem1)(),
        TyTuple(uint, uint) ::+ (x => VecLiteral(x.__0, x.__1)())
      )(),
      window -> StmMap(
        StmZip(row0, row1)(),
        TyTuple(TyVec(uint, 2), TyVec(uint, 2)) ::+ (x =>
          VecLiteral(x.__0, x.__1)()
        )
      )(),
      // Multiply with kernel
      windowAndKernelZipped -> StmMap(
        StmZip(window, kernelStm)(),
        (TyVec(TyVec(uint, 2), 2), TyVec(TyVec(uint, 2), 2)) ::+ (x =>
          VecMap2(
            x.__0,
            x.__1,
            TyVec(uint, 2) ::+ (v1 =>
              TyVec(uint, 2) ::+ (v2 => VecZip(v1, v2)())
            )
          )()
        )
      )(),
      windowAndKernelMultiplied -> StmMap(
        windowAndKernelZipped,
        TyVec(TyVec((uint, uint), 2), 2) ::+ (v =>
          VecMap(
            v,
            TyVec((uint, uint), 2) ::+ (v =>
              VecMap(v, (uint, uint) ::+ (x => x.__0 *% x.__1))()
            )
          )()
        )
      )(),
      rowSums -> StmMap(
        windowAndKernelMultiplied,
        TyVec(TyVec(uint, 2), 2) ::+ (v =>
          VecMap(
            v,
            TyVec(uint, 2) ::+ (v =>
              VecAccess(
                VecReduceComb(v, (uint, uint) ::+ (x => x.__0 +% x.__1))(),
                0
              )()
            )
          )()
        )
      )(),
      colSums -> StmMap(
        rowSums,
        TyVec(uint, 2) ::+ (v =>
          VecAccess(
            VecReduceComb(v, (uint, uint) ::+ (x => x.__0 +% x.__1))(),
            0
          )()
        )
      )(),
      result -> StmMap(colSums, uint ::+ (x => IntFixProd(x, kernelCoeff)()))()
    )(
      result
    )
    conv.tchk()
  }

  /** 2-dimensional convolution.
    */
  private val AetherlingConv2d: Expr = {
    val width = 1920
    val height = 1080
    val uint = U32
    val input = Param("I")(TyStm(uint, width * height))
    val conv = makeConv3x3(
      width = width,
      height = height,
      input = input,
      kernelVec = blurKernel(uint),
      kernelCoeff = FixCst(8)(TyFix(U8, 7))
    )
    Function(input, conv)()
  }

  /** 3D convolution followed by 2D convolution.
    */
  private val AetherlingConvB2b: Expr = {
    val width = 1920
    val height = 1080
    val uint = U32
    val input = Param("I")(TyStm(uint, width * height))
    val part1 = makeConv3x3(
      width = width,
      height = height,
      input = input,
      kernelVec = blurKernel(uint),
      kernelCoeff = FixCst(8)(TyFix(U8, 7))
    )
    val part2 = makeConv2x2(width = width, height = height, input = part1)
    Function(input, part2)()
  }

  /** An image sharpening operation.
    */
  private val AetherlingSharpen: Expr = {
    val width = 1920
    val height = 1080
    val uint = U32
    val input = Param("I")(TyStm(uint, width * height))
    val sharpenOne = {
      val threshold = 15
      val a = Param("a")(uint)
      val b = Param("b")(uint)
      val passedThreshold = ((a -% b) > threshold) || ((b -% a) > threshold)
      val oneQuarter = FixCst(32)(TyFix(U8, 7))
      val alphaH =
        Mux(passedThreshold, IntFixProd(b -% a, oneQuarter)(), C(0)(uint))()
      val sharp = b +% alphaH
      Function(a, Function(b, sharp)())()
    }
    val sharedInput = Param("I")(TyStm(uint, width * height))
    val blurred =
      makeConv3x3(
        width = width,
        height = height,
        input = sharedInput,
        kernelVec = blurKernel(uint),
        kernelCoeff = FixCst(8)(TyFix(U8, 7))
      )
    val sharpened =
      Let(sharedInput, input, StmMap2(blurred, sharedInput, sharpenOne)())()
    Function(input, sharpened)()
  }

  /** Camera pipeline (demosaic + sharpen).
    */
  private val AetherlingCamera: Expr = {
    // I'm too lazy to write the whole benchmark by hand
    val aetherlingCode = os.read(
      os.pwd / "src" / "test" / "resources" / "aetherling_benchmarks" / "original" / "bigcamera_1.txt"
    )
    AetherlingParser.parse(aetherlingCode)
  }

  /** Matrix-vector multiplication.
    *
    * @param width
    *   the number of columns in the matrix.
    * @param height
    *   the number of rows in the matrix.
    * @param par
    *   the degree of spatial parallelism.
    * @param uint
    *   the type of the elements in the matrix.
    */
  private def MatVecMul(
      width: Int,
      height: Int,
      par: Int,
      uint: TyUInt
  ): Expr = {
    require(width % par == 0)
    val mat = Param("mat")(TyStm(TyStm(TyVec(uint, par), width / par), height))
    val vec = Param("vec")(TyStm(TyVec(uint, par), width / par))
    val row = Param("row")(TyStm(TyVec(uint, par), width / par))
    val dot = {
      val zipStm = StmZip(row, vec)() // Stm[(Vec[uint, p], Vec[uint, p]), w/p]
      val zipVec = StmMap( // Stm[Vec[(uint, uint), p], w/p]
        zipStm,
        (TyVec(uint, par), TyVec(uint, par)) ::+ (x => VecZip(x.__0, x.__1)())
      )()
      val multiplied = StmMap( // Stm[Vec[uint, p], w/p]
        zipVec,
        TyVec((uint, uint), par) ::+ (v =>
          VecMap(v, (uint, uint) ::+ (x => x.__0 * x.__1))()
        )
      )()
      val sumVec = StmMap( // Stm[uint, w/p]
        multiplied,
        TyVec(uint, par) ::+ (v =>
          VecAccess(
            VecReduceComb(v, (uint, uint) ::+ (x => x.__0 + x.__1))(),
            0
          )()
        )
      )()
      val sumStm = // Stm[uint, 1]
        StmReduce(sumVec, (uint, uint) ::+ (x => x.__0 + x.__1))()
      sumStm
    }
    val prod = StmMap(mat, Function(row, dot)())()
    Function(mat, Function(vec, prod)())()
  }

  private def makeSqrt(int: TyAnyInt, stages: Int, input: Expr): Expr = {
    val init = int ::+ (x => {
      val lo = C(0)(int)
      val hi = C(math.sqrt(int.maxInt.toDouble).floor.toLong)(int)
      Tuple(x, lo, hi)()
    })
    val step = (int, int, int) ::+ (x => {
      val n = x.__0
      val lo = x.__1
      val hi = x.__2
      val mid = (lo + hi + 1) >>> 1
      Mux(
        mid *% mid <= n,
        Tuple(n, mid, hi)(),
        Tuple(n, lo, mid -% C(1)(int))()
      )()
    })
    val binarySearchStart = StmMap(input, init)()
    val binarySearchEnd = (0 until stages).foldLeft(binarySearchStart)({
      case (acc, _) => StmMap(acc, step)()
    })
    StmMap(binarySearchEnd, (int, int, int) ::+ (x => x.__1))()
  }

  private val Sqrt: Expr = {
    val input = Param("I")(TyStm(U16, 1020))
    val sqrt = makeSqrt(U16, stages = 16, input)
    Function(input, sqrt)()
  }

  private val AetherlingSobel: Expr = {
    val width = 1920
    val height = 1080
    val int = I32
    val input = Param("I")(TyStm(int, width * height))
    val gx = makeConv3x3(
      width = width,
      height = height,
      input = input,
      kernelVec = VecLiteral(
        VecLiteral(C(-1)(int), C(0)(int), C(1)(int))(),
        VecLiteral(C(-2)(int), C(0)(int), C(2)(int))(),
        VecLiteral(C(-1)(int), C(0)(int), C(1)(int))()
      )(),
      kernelCoeff = C(1)(int)
    )
    val gy = makeConv3x3(
      width = width,
      height = height,
      input = input,
      kernelVec = VecLiteral(
        VecLiteral(C(-1)(int), C(-2)(int), C(-1)(int))(),
        VecLiteral(C(0)(int), C(0)(int), C(0)(int))(),
        VecLiteral(C(1)(int), C(2)(int), C(1)(int))()
      )(),
      kernelCoeff = C(1)(int)
    )
    val normSquared = StmMap(
      StmZip(gx, gy)(),
      (int, int) ::+ (x => x.__0 *% x.__0 +% x.__1 *% x.__1)
    )()
    val norm = makeSqrt(int, stages = 16, normSquared)
    Function(input, Let(input, input, norm)())()
  }
}
