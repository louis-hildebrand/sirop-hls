package mhir.main.stored

import mhir.ir._
import mhir.ir.typecheck.TypeCheck
import mhir.main.shared.BadArgsException
import mhir.parse.AetherlingParser
import mhir.sugar.{StmMap, StmReduce, StmZip}
import mhir.sugar._

/** A collection of pre-written programs.
  */
object Program {

  val MatVecSize: Int = 256

  def apply(name: String): Expr = {
    name.toLowerCase match {
      case "map"         => Map
      case "shir:map"    => Map
      case "dot"         => Dot
      case "shir:dot"    => Dot
      case "conv1d"      => Conv1d
      case "shir:conv1d" => ShirConv1d
      case "conv2d"      => Conv2d
      case "shir:conv2d" => ShirConv2d
      case "convb2b"     => ConvB2b
      case "sharpen"     => Sharpen
      case "camera"      => Camera
      case str if str.startsWith("matvec_") =>
        val parStr = str.substring("matvec_".length)
        val par = parStr.toInt
        MatVecMul(
          width = MatVecSize,
          height = MatVecSize,
          par = par,
          uint = U16
        )
      case "shir:matvec" =>
        MatVecMul(
          width = MatVecSize,
          height = MatVecSize,
          par = 1,
          uint = U16
        )
      case "sqrt"  => Sqrt
      case "sobel" => Sobel
      case name =>
        throw new BadArgsException(s"unknown program: $name")
    }
  }

  /** Add 5 to every element of a stream.
    */
  private val Map: Expr = {
    val input = Param("I")(TyStm(U8, 200))
    Function(input, StmMap(input, U8 ::+ (x => x + 5))())()
  }

  /** Dot product of two streams.
    */
  private val Dot: Expr = {
    val I0 = Param("I0")(TyStm(U16, 840))
    val I1 = Param("I1")(TyStm(U16, 840))
    val zipped = StmZip(I0, I1)()
    val multiplied = StmMap(zipped, (U16, U16) ::+ (x => x.__0 * x.__1))()
    val dot = StmReduce(multiplied, (U16, U16) ::+ (x => x.__0 + x.__1))()
    Function(I0, Function(I1, dot)())()
  }

  /** 1-dimensional convolution.
    */
  private val Conv1d: Expr = {
    val kernel = VecLiteral(C(-1)(I8), C(0)(I8), C(1)(I8))()
    val input = Param("I")(TyStm(I8, 16))
    val slide = StmSlideV(input, 3)()
    val kernelStm = StmCst(14, kernel)()
    val s0 = StmZip(slide, kernelStm)()
    val s1 = StmMap(
      s0,
      (TyVec(I8, 3), TyVec(I8, 3)) ::+ (x => VecZip(x.__0, x.__1))
    )()
    val s2 = StmMap(
      s1,
      TyVec((I8, I8), 3) ::+ (v =>
        VecMap(v, (I8, I8) ::+ (x => x.__0 * x.__1))()
      )
    )()
    val s3 = StmMap(
      s2,
      TyVec(I8, 3) ::+ (v =>
        VecReduceComb(v, (I8, I8) ::+ (x => x.__0 + x.__1))()
      )
    )()
    val s4 = StmMap(s3, TyVec(I8, 1) ::+ (v => VecAccess(v, 0)()))()
    Function(input, s4)()
  }

  /** 1-dimensional convolution.
    */
  private val ShirConv1d: Expr = {
    val input = Param("I")(TyStm(I8, 16))
    val windows = StmSlideV(input, 3)()
    val result = StmMap(
      windows,
      TyVec(I8, 3) ::+ ({ window =>
        val kernel = VecLiteral(C(-1)(I8), C(0)(I8), C(1)(I8))()
        val zipped = VecZip(window, kernel)
        val products = VecMap(zipped, (I8, I8) ::+ (x => x.__0 * x.__1))()
        val sum = VecAccess(
          VecReduceComb(products, (I8, I8) ::+ (x => x.__0 + x.__1))(),
          0
        )()
        sum
      })
    )()
    Function(input, result)()
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
            TyVec(uint, 3) ::+ (v1 => TyVec(uint, 3) ::+ (v2 => VecZip(v1, v2)))
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
            TyVec(uint, 2) ::+ (v1 => TyVec(uint, 2) ::+ (v2 => VecZip(v1, v2)))
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
  private val Conv2d: Expr = {
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

  private val ShirConv2d: Expr = {
    val int = U32
    val height = 1080
    val width = 1920
    val input = Param("I")(TyStm(TyStm(int, width), height))
    val flatInput = StmJoin(input)()
    val rowWindows = StmMap(
      StmSlideV(flatInput, 3 * width, width)(),
      TyVec(int, 3 * width) ::+ (rowGroup => VecSplit(rowGroup, width)())
    )()
    val transposedRowWindows = StmMap(
      rowWindows,
      TyVec(TyVec(int, width), 3) ::+ (rowWindow =>
        Vec2Stm(VecTranspose(rowWindow))()
      )
    )()
    val windows = StmMap(
      transposedRowWindows,
      TyStm(TyVec(int, 3), width) ::+ (rowWindow =>
        StmSlideV(rowWindow, 3, 1)()
      )
    )()
    val conv = StmMap(
      windows,
      TyStm(TyVec(TyVec(int, 3), 3), width - 2) ::+ (x =>
        StmMap(
          x,
          TyVec(TyVec(int, 3), 3) ::+ { window =>
            val kernel = VecLiteral(
              C(1)(int),
              C(2)(int),
              C(1)(int),
              C(2)(int),
              C(4)(int),
              C(2)(int),
              C(1)(int),
              C(2)(int),
              C(1)(int)
            )()
            val flatWindow = VecJoin(window)()
            val zipped = VecZip(flatWindow, kernel)
            val products = VecMap(zipped, (int, int) ::+ (x => x.__0 * x.__1))()
            val sum = VecAccess(
              VecReduceComb(
                products,
                (int, int) ::+ (x => x.__0 + x.__1)
              )(),
              0
            )()
            sum
          }
        )()
      )
    )()
    Function(input, conv)()
  }

  /** 3D convolution followed by 2D convolution.
    */
  private val ConvB2b: Expr = {
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
  private val Sharpen: Expr = {
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
  private val Camera: Expr = {
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
        (TyVec(uint, par), TyVec(uint, par)) ::+ (x => VecZip(x.__0, x.__1))
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
      val mid = (lo + hi + 1) >> 1
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

  private val Sobel: Expr = {
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
