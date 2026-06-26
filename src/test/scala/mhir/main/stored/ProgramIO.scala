package mhir.main.stored

import mhir.canonicalize._
import mhir.gen.vhdl.test._
import mhir.ir._
import mhir.main.aetherling.{AbstractTestIO, AetherlingBenchmarkIO}
import mhir.sugar.{AllZero, ExprLowering}
import mhir.typecheck.TypeCheck

object ProgramIO {
  def apply(name: String): PositionalTestIO = {
    if (name.startsWith("map") || name == "shir:map") {
      mapIO
    } else if (name.startsWith("dot") || name == "shir:dot") {
      dotIO
    } else if (name.startsWith("matvec") || name == "shir:matvec") {
      matVecMulIO(width = 256, height = 256, par = 1, uint = U16)
    } else if (name.startsWith("smallmatmat") || name == "shir:smallmatmat") {
      matMatMulIO(n = 4, m = 4, k = 4, par = 2, uint = U16)
    } else if (name.startsWith("matmat") || name == "shir:matmat") {
      matMatMulIO(n = 256, m = 256, k = 256, par = 16, uint = U16)
    } else if (name.startsWith("conv1d") || name == "shir:conv1d") {
      conv1dIO
    } else if (name.startsWith("conv2d") || name == "shir:conv2d") {
      shirConv2dIO
    } else if (name.startsWith("convb2b") || name == "shir:convb2b") {
      shirConvB2bIO
    } else if (name.startsWith("jacobi") || name == "shir:jacobi") {
      shirJacobiIO
    } else if (name.startsWith("sharpen") || name == "shir:sharpen") {
      shirSharpenIO
    } else if (name.startsWith("sobel") || name == "shir:sobel") {
      shirSobelIO
    } else if (name.startsWith("camera") || name == "shir:camera") {
      shirCameraIO
    } else if (name.startsWith("sqrt")) {
      sqrtIO
    } else {
      throw new IllegalArgumentException(s"unknown program: $name")
    }
  }

  private def mapIO: PositionalTestIO = {
    AetherlingBenchmarkIO.mapIO("map_1").toVhdl
  }

  private def dotIO: PositionalTestIO = {
    AetherlingBenchmarkIO.dotIO("dot_1_840").toVhdl
  }

  private def conv1dIO: PositionalTestIO = {
    // Square wave with 50% duty cycle and period 8
    val inputs = (0 until 16).map(t => if (t % 8 < 4) -42 else 42)
    val outputs = inputs.sliding(3).map(v => v(2) - v(0)).toSeq
    PositionalTestIO(
      Seq(DirectTestInput(inputs.map(C(_)(I8)).map(Some(_)))),
      DirectTestOutput(
        outputs.map(C(_)(I8)),
        outputs.map(_ => AllZero(I8).tchk().lower)
      )
    )
  }

  /** Finds the width and height of a rectangular array.
    *
    * @param arr
    *   the array.
    * @return
    *   (width, height)
    */
  private def dim[T](arr: Seq[Seq[T]]): (Int, Int) = {
    val height = arr.length
    val width = {
      val widths = arr.map(_.length).toSet
      require(widths.size == 1)
      widths.head
    }
    (width, height)
  }

  /** Performs a 2D convolution
    *
    * @param inputs
    *   the input array. `None` values are considered undefined.
    * @param kernel
    *   the kernel.
    * @return
    *   the result of the convolution and division. `None` represents undefined
    *   values.
    */
  private def conv2d(
      inputs: Seq[Seq[Int]],
      kernel: Seq[Seq[Int]]
  ): Seq[Seq[Int]] = {
    val (inputWidth, inputHeight) = dim(inputs)
    val (kernelWidth, kernelHeight) = dim(kernel)
    val outputs =
      (0 to (inputHeight - kernelHeight)).map({ i =>
        (0 to (inputWidth - kernelWidth)).map({ j =>
          // (i, j) is the current position of the top-left element of the
          // kernel within the input
          assert(i >= 0)
          assert(i + kernelHeight - 1 < inputHeight)
          assert(j >= 0)
          assert(j + kernelWidth - 1 < inputWidth)
          val relevantInputs =
            (0 until kernelHeight).flatMap(deltaI =>
              (0 until kernelWidth).map(deltaJ =>
                inputs(i + deltaI)(j + deltaJ)
              )
            )
          relevantInputs
            .zip(kernel.flatten)
            .map({ case (x, y) => x * y })
            .sum
        })
      })
    outputs
  }

  private def shirConv2dIO: PositionalTestIO = {
    val width = 1920
    val height = 16
    val k = C(255)(U32)
    // Checkerboard pattern (10x10 squares)
    val basicInputs: Seq[Seq[Int]] =
      (0 until height).map(i =>
        (0 until width).map(j => {
          val even = ((i % 20) < 10) == ((j % 20) < 10)
          if (even) k.i.toInt else 0
        })
      )
    val basicInputExprs = basicInputs.flatten.map(C(_)(k.typ))
    val basicOutputs: Seq[Expr] =
      conv2d(
        basicInputs,
        kernel = Seq(Seq(1, 2, 1), Seq(2, 4, 2), Seq(1, 2, 1))
      ).flatten.map(_ / 16).map(C(_)(k.typ))
    AbstractTestIO(basicInputExprs.map(Seq(_)), basicOutputs).toVhdl
  }

  private def shirJacobiIO: PositionalTestIO = {
    val width = 128
    val height = 32
    val k = C(255)(U8)
    // Checkerboard pattern (10x10 squares)
    val basicInputs: Seq[Seq[Int]] =
      (0 until height).map(i =>
        (0 until width).map(j => {
          val even = ((i % 20) < 10) == ((j % 20) < 10)
          if (even) k.i.toInt else 0
        })
      )
    val basicInputExprs = basicInputs.flatten.map(C(_)(k.typ))
    val basicOutputs: Seq[Expr] =
      conv2d(
        basicInputs,
        kernel = Seq(Seq(0, 1, 0), Seq(1, 0, 1), Seq(0, 1, 0))
      ).flatten.map(_ / 4).map(C(_)(k.typ))
    AbstractTestIO(basicInputExprs.map(Seq(_)), basicOutputs).toVhdl
  }

  private def shirConvB2bIO: PositionalTestIO = {
    val width = 1920
    val height = 16
    val k = C(255)(U32)
    // Checkerboard pattern (10x10 squares)
    val basicInputs: Seq[Seq[Int]] =
      (0 until height).map(i =>
        (0 until width).map(j => {
          val even = ((i % 20) < 10) == ((j % 20) < 10)
          if (even) k.i.toInt else 0
        })
      )
    val basicInputExprs = basicInputs.flatten.map(C(_)(k.typ))
    val basicOutputs: Seq[Expr] =
      conv2d(
        conv2d(
          basicInputs,
          kernel = Seq(Seq(1, 2, 1), Seq(2, 4, 2), Seq(1, 2, 1))
        ).map(xs => xs.map(_ / 16)),
        kernel = Seq(Seq(1, 2), Seq(4, 1))
      ).flatten.map(_ / 8).map(C(_)(k.typ))
    AbstractTestIO(basicInputExprs.map(Seq(_)), basicOutputs).toVhdl
  }

  private def shirSharpenIO: PositionalTestIO = {
    val width = 1920
    val height = 16
    val k = C(255)(U32)
    // Checkerboard pattern (10x10 squares)
    val basicInputs: Seq[Seq[Int]] =
      (0 until height).map(i =>
        (0 until width).map(j => {
          val even = ((i % 20) < 10) == ((j % 20) < 10)
          if (even) k.i.toInt else 0
        })
      )
    val basicInputExprs = basicInputs.flatten.map(C(_)(k.typ))
    val basicOutputs: Seq[Expr] = {
      val blurred = conv2d(
        basicInputs,
        kernel = Seq(Seq(1, 2, 1), Seq(2, 4, 2), Seq(1, 2, 1))
      ).map(_.map(_ / 16))
      val original = conv2d(
        basicInputs,
        kernel = Seq(Seq(0, 0, 0), Seq(0, 1, 0), Seq(0, 0, 0))
      )
      val sharpened = blurred.flatten
        .zip(original.flatten)
        .map({ case (a, b) =>
          val alphaH = (b - a) >>> 2
          b + alphaH
        })
      sharpened.map(C(_)(k.typ))
    }
    AbstractTestIO(basicInputExprs.map(Seq(_)), basicOutputs).toVhdl
  }

  private def shirSobelIO: PositionalTestIO = {
    val width = 1920
    val height = 16
    val k = C(255)(U32)
    // Checkerboard pattern (10x10 squares)
    val basicInputs: Seq[Seq[Int]] =
      (0 until height).map(i =>
        (0 until width).map(j => {
          val even = ((i % 20) < 10) == ((j % 20) < 10)
          if (even) k.i.toInt else 0
        })
      )
    val basicInputExprs = basicInputs.flatten.map(C(_)(k.typ))
    val basicOutputs: Seq[Expr] = {
      val gx = conv2d(
        basicInputs,
        kernel = Seq(Seq(-1, 0, 1), Seq(-2, 0, 2), Seq(-1, 0, 1))
      )
      val gy = conv2d(
        basicInputs,
        kernel = Seq(Seq(-1, -2, -1), Seq(0, 0, 0), Seq(1, 2, 1))
      )
      val sqrt = gx.flatten
        .zip(gy.flatten)
        .map({ case (x, y) => math.sqrt(x * x + y * y).floor.toLong })
      sqrt.map(C(_)(k.typ))
    }
    AbstractTestIO(basicInputExprs.map(Seq(_)), basicOutputs).toVhdl
  }

  private def shirCameraIO: PositionalTestIO = {
    val width = 1920
    val height = 8
    val basicInputExprs = (1 to (width * height))
      .map(i => (i * i) % U32.maxInt)
      .map(_.toLong)
      .map(C(_)(U32))
    val basicOutputs: Seq[Expr] = {
      val f =
        os.pwd / "src" / "test" / "resources" / "aetherling_benchmarks" / "camera_outputs.csv"
      os.read
        .lines(f)
        .flatMap({ line =>
          val Array(r, g, b) = line
            .split(",")
            .map(_.toLong)
            .map({
              // Aetherling uses 253 as a sentinel value for undefined elements
              case 253 => Undefined(U32)
              case x   => C(x)(U32)
            })
          if (r.isInstanceOf[Undefined]) {
            assert(g.isInstanceOf[Undefined])
            assert(b.isInstanceOf[Undefined])
            None
          } else {
            assert(!g.isInstanceOf[Undefined])
            assert(!b.isInstanceOf[Undefined])
            Some(Tuple(r, g, b)().tchk())
          }
        })
    }
    PositionalTestIO(
      Seq(DirectTestInput(basicInputExprs.map(Some(_)))),
      DirectTestOutput(
        basicOutputs,
        basicOutputs.map(_ => AllZero(TyTuple(U32, U32, U32)).tchk().lower)
      )
    )
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
  def matVecMulIO(
      width: Int,
      height: Int,
      par: Int,
      uint: TyUInt
  ): PositionalTestIO = {
    val mat = (0 until height).map(i => (0 until width).map(j => (i + j) % 16))
    val vec = (0 until width).map(_ % 16)
    val outputs = mat.map(row => row.zip(vec).map({ case (x, y) => x * y }).sum)
    PositionalTestIO(
      Seq(
        DirectTestInput(
          mat.flatten
            .map(C(_)(uint))
            .grouped(par)
            .map(xs => VecLiteral(xs: _*)().tchk())
            .map(Some(_))
            .toSeq
        ),
        DirectTestInput(
          vec
            .map(C(_)(uint))
            .grouped(par)
            .map(xs => VecLiteral(xs: _*)().tchk())
            .map(Some(_))
            .toSeq
        )
      ),
      DirectTestOutput(outputs.map(C(_)(uint)), outputs.map(_ => AllZero(uint)))
    )
  }

  /** Matrix-matrix multiplication.
    *
    * @param n
    *   number of rows in the first matrix, `A`.
    * @param m
    *   number of columns in the first matrix, `A`. This is also the number of
    *   rows in the second matrix, `B`.
    * @param k
    *   number of columns in the second matrix, `B`.
    * @param par
    *   the degree of spatial parallelism.
    * @param uint
    *   the integer type to use.
    */
  private def matMatMulIO(
      n: Int,
      m: Int,
      k: Int,
      par: Int,
      uint: TyUInt
  ): PositionalTestIO = {
    val matA = (0 until n).map(i =>
      (0 until m).map(j => ((4 * i + j) * (4 * i + j)) % 6)
    )
    val matBTranspose =
      (0 until m).map(i => (0 until k).map(j => (4 * i + j) % 6)).transpose
    val outputs = matA.flatMap(rowA =>
      matBTranspose.map(rowB =>
        rowA.zip(rowB).map({ case (x, y) => x * y }).sum
      )
    )
    PositionalTestIO(
      Seq(
        DirectTestInput(
          matA.flatten
            .map(C(_)(uint))
            .grouped(par)
            .map(xs => VecLiteral(xs: _*)())
            .map(Some(_))
            .toSeq
        ),
        DirectTestInput(
          matBTranspose.flatten
            .map(C(_)(uint))
            .grouped(par)
            .map(xs => VecLiteral(xs: _*)())
            .map(Some(_))
            .toSeq
        )
      ),
      DirectTestOutput(outputs.map(C(_)(uint)), outputs.map(_ => AllZero(uint)))
    )
  }

  private def sqrtIO: PositionalTestIO = {
    AetherlingBenchmarkIO.vhdlIO("sqrt_1")
  }
}
