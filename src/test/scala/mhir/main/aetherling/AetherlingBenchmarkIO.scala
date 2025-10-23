package mhir.main.aetherling

import mhir.gen.{verilog, vhdl}
import mhir.ir._
import mhir.ir.typecheck.TypeCheck

/** Inputs and expected outputs to use in tests for each Aetherling benchmark.
  */
object AetherlingBenchmarkIO {

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
    * @param kernelDenom
    *   a number by which to divide each element after the convolution.
    * @return
    *   the result of the convolution and division. `None` represents undefined
    *   values.
    */
  private def conv2d(
      inputs: Seq[Seq[Option[Int]]],
      kernel: Seq[Seq[Int]],
      kernelDenom: Int
  ): Seq[Seq[Option[Int]]] = {
    val (inputWidth, inputHeight) = dim(inputs)
    val (kernelWidth, kernelHeight) = dim(kernel)
    val outputs =
      ((1 - kernelHeight) to (inputHeight - kernelHeight)).map({ i =>
        ((1 - kernelWidth) to (inputWidth - kernelWidth)).map({ j =>
          // (i, j) is the current position of the top-left element of the
          // kernel within the input
          if (i < 0 || j < 0) {
            None
          } else {
            val relevantInputs =
              (0 until kernelHeight).flatMap(deltaI =>
                (0 until kernelWidth).map(deltaJ =>
                  inputs(i + deltaI)(j + deltaJ)
                )
              )
            if (relevantInputs.forall(_.nonEmpty)) {
              val dot = relevantInputs
                .map(_.get)
                .zip(kernel.flatten)
                .map({ case (x, y) => x * y })
                .sum
              Some(dot / kernelDenom)
            } else {
              None
            }
          }
        })
      })
    assert(dim(outputs) == dim(inputs))
    outputs
  }

  private def mapIO: Map[String, AbstractTestIO] = {
    val n = 200
    val sequentialIn = AbstractTestInput(
      (t: Int) => (_: Int) => C(t)(U8),
      elemTypes = Seq(U8),
      len = n,
      hold = 1
    )
    val sequentialOut = AbstractTestOutput(
      (t: Int) => C(t + 5)(U8),
      elemTyp = U8,
      len = n,
      skip = 0
    )
    Seq(1, 2, 4, 5, 8, 10, 20, 40, 200)
      .map({ par =>
        val io = par match {
          case 1 => AbstractTestIO(sequentialIn, sequentialOut)
          case _ =>
            AbstractTestIO(sequentialIn.vec(par), sequentialOut.vec(par))
        }
        s"map_$par" -> io
      })
      .toMap
  }

  private def sumIO: Map[String, AbstractTestIO] = {
    val n = 840
    val sequentialIn = AbstractTestInput(
      (t: Int) => (_: Int) => C(t)(U32),
      elemTypes = Seq(U32),
      len = n,
      hold = 1
    )
    val sum = C((0 until n).sum)(U32)
    val out = AbstractTestOutput(Seq(sum))
    Seq(1, 2, 3, 4, 5, 6, 7, 8)
      .map({ par =>
        val in = par match {
          case 1 => sequentialIn
          case _ => sequentialIn.vec(par)
        }
        s"sum_1_${840 / par}" -> AbstractTestIO(in, out)
      })
      .toMap
  }

  private def dotIO: Map[String, AbstractTestIO] = {
    val n = 840
    val result = (0 until n)
      .zip((n - 1) until 0 by -1)
      .map({ case (x, y) => (x % 16, y % 16) })
      .map({ case (x, y) => x * y })
      .sum
    val sequentialIn = new AbstractTestInput(
      (t: Int) =>
        (i: Int) => if (i == 0) C(t % 16)(U16) else C((n - 1 - t) % 16)(U16),
      elemTypes = Seq(U16, U16),
      len = n,
      hold = 1
    )
    val out = AbstractTestOutput(Seq(C(result)(U16)))
    Seq(1, 2, 3, 4, 5, 6, 7, 8)
      .map({ par =>
        val in = par match {
          case 1 => sequentialIn
          case _ => sequentialIn.vec(par)
        }
        s"dot_1_${840 / par}" -> AbstractTestIO(in, out)
      })
      .toMap
  }

  private def conv1dIO: Map[String, TestIO] = {
    // Square wave with 50% duty cycle and period 8
    val flatInputs = (0 until 16).map(t => if (t % 8 < 4) -42 else 42)
    val flatOutputs =
      (Seq(Undefined(I8), Undefined(I8))
        ++ flatInputs.sliding(3).map(v => C(v(2) - v(0))(I8)).toSeq)
    val sequentialIn = AbstractTestInput(flatInputs.map(C(_)(I8)).map(Seq(_)))
    val sequentialOut = AbstractTestOutput(flatOutputs)
    val normalCases = Seq(1, 2, 4, 8, 16)
      .map({ par =>
        val in = sequentialIn.vec(par)
        val out = par match {
          case 1 => sequentialOut.vec(1)
          case _ => sequentialOut.vec(1).vec(par)
        }
        s"conv1d_$par" -> AbstractTestIO(in, out)
      })
      .toMap
    val underutilizedCase = {
      val io = AbstractTestIO(
        in = flatInputs.map(C(_)(I8)).map(Seq(_)),
        out = flatOutputs,
        hold = 3,
        skip = 2
      )
      "conv1d_1_3" -> io
    }
    normalCases + underutilizedCase
  }

  private def conv2dIO(
      name: String,
      width: Int,
      height: Int,
      k: IntCst
  ): Map[String, TestIO] = {
    // Checkerboard pattern (2x2 squares)
    val basicInputs: Seq[Seq[Int]] =
      (0 until height).map(i =>
        (0 until width).map(j => {
          val even = ((i % 4) < 2) == ((j % 4) < 2)
          if (even) k.i.toInt else 0
        })
      )
    val basicInputExprs = basicInputs.flatten.map(C(_)(k.typ))
    val basicOutputs: Seq[Expr] =
      conv2d(
        basicInputs.map(_.map(Some(_))),
        kernel = Seq(Seq(1, 2, 1), Seq(2, 4, 2), Seq(1, 2, 1)),
        kernelDenom = 16
      ).flatten.map({
        case Some(x) => C(x)(k.typ)
        case None    => Undefined(k.typ)
      })
    val normalCases = Seq(1, 2, 4, 8, 16)
      .map({ par =>
        val io = par match {
          case 1 =>
            AbstractTestIO(
              basicInputExprs.map(VecLiteral(_)().tchk()).map(Seq(_)),
              basicOutputs.map(VecLiteral(_)().tchk())
            )
          case n =>
            assert(n > 1)
            // n valid per cycle
            AbstractTestIO(
              basicInputExprs
                .grouped(n)
                .map(VecLiteral(_: _*)().tchk())
                .map(Seq(_))
                .toSeq,
              basicOutputs
                .grouped(n)
                .map(VecLiteral(_: _*)().tchk())
                .toSeq
            )
        }
        s"${name}_$par" -> io
      })
      .toMap
    val underutilizedCases = Seq(3, 9).map({ denom =>
      val io = AbstractTestIO(
        in = basicInputExprs.map(Seq(_)),
        out = basicOutputs,
        hold = denom,
        skip = denom - 1
      )
      s"${name}_1_$denom" -> io
    })
    normalCases ++ underutilizedCases
  }

  private def smallConv2dIO: Map[String, TestIO] = {
    conv2dIO("smallconv2d", width = 4, height = 4, k = C(15)(U8))
  }

  private def bigConv2dIO: Map[String, TestIO] = {
    conv2dIO("bigconv2d", width = 1920, height = 4, k = C(255)(U32))
  }

  private def convB2bIO(
      name: String,
      width: Int,
      height: Int,
      typ: TyUInt
  ): Map[String, TestIO] = {
    val basicInputs: Seq[Seq[Int]] =
      (0 until (width * height))
        .map(i => 5 * (i + 1))
        .grouped(width)
        .toSeq
    val basicInputExprs = basicInputs.flatten.map(C(_)(typ))
    val basicOutputs = {
      val step1 = conv2d(
        inputs = basicInputs.map(_.map(Some(_))),
        kernel = Seq(Seq(1, 2, 1), Seq(2, 4, 2), Seq(1, 2, 1)),
        kernelDenom = 1
      ).map(row =>
        row.map(x =>
          // Handle overflow before dividing
          x.map(_ % typ.maxInt.toLong)
            .map(_ / 16)
            .map(_.toInt)
        )
      )
      val step2 = conv2d(
        inputs = step1,
        kernel = Seq(Seq(1, 4), Seq(2, 1)),
        kernelDenom = 8
      )
      step2.flatten.map({
        case Some(x) => C(x)(typ)
        case None    => Undefined(typ)
      })
    }
    val normalCases = Seq(1, 2, 4, 8, 16)
      .map({ par =>
        val io = par match {
          case 1 =>
            AbstractTestIO(
              basicInputExprs.map(VecLiteral(_)().tchk()).map(Seq(_)),
              basicOutputs.map(VecLiteral(_)().tchk())
            )
          case n =>
            assert(n > 1)
            // n valid per cycle
            AbstractTestIO(
              basicInputExprs
                .grouped(n)
                .map(VecLiteral(_: _*)().tchk())
                .map(Seq(_))
                .toSeq,
              basicOutputs
                .grouped(n)
                .map(VecLiteral(_: _*)().tchk())
                .toSeq
            )
        }
        s"${name}_$par" -> io
      })
      .toMap
    val underutilizedCases = Seq(3, 9).map({ denom =>
      val io = AbstractTestIO(
        in = basicInputExprs.map(Seq(_)),
        out = basicOutputs,
        hold = denom,
        skip = denom - 1
      )
      s"${name}_1_$denom" -> io
    })
    normalCases ++ underutilizedCases
  }

  private def smallConvB2bIO: Map[String, TestIO] = {
    convB2bIO("smallconvb2b", width = 4, height = 4, typ = U8)
  }

  private def bigConvB2bIO: Map[String, TestIO] = {
    convB2bIO("bigconvb2b", width = 1920, height = 4, typ = U32)
  }

  private def sharpenIO(
      name: String,
      width: Int,
      height: Int,
      typ: TyUInt
  ): Map[String, TestIO] = {
    def sharpen1(a: Int, b: Int): Int = {
      val threshold = 15
      val passedThreshold = (a - b > threshold) || (b - a > threshold)
      val h = if (passedThreshold) (b - a) / 4 else 0
      b + h
    }
    def sharpenImage(img: Seq[Seq[Int]]): Seq[Option[Int]] = {
      val blurredImg = conv2d(
        img.map(_.map(Some(_))),
        kernel = Seq(Seq(1, 2, 1), Seq(2, 4, 2), Seq(1, 2, 1)),
        kernelDenom = 1
      ).map(row =>
        row.map(x =>
          // Handle overflow before dividing
          x.map(_ % typ.maxInt.toLong)
            .map(_ / 16)
            .map(_.toInt)
        )
      )
      blurredImg.flatten
        .zip(img.flatten)
        .map({
          case (None, _)    => None
          case (Some(a), b) => Some(sharpen1(a, b))
        })
    }
    val basicInputs: Seq[Seq[Int]] =
      (1 to (width * height)).map(_ * 5).grouped(width).toSeq
    val basicInputExprs = basicInputs.flatten.map(C(_)(typ))
    val basicOutputs = sharpenImage(basicInputs).map({
      case Some(x) => C(x)(typ)
      case None    => Undefined(typ)
    })
    val normalCases = Seq(1, 2, 4, 8, 16)
      .map({ par =>
        val io = par match {
          case 1 =>
            AbstractTestIO(
              basicInputExprs.map(VecLiteral(_)().tchk()).map(Seq(_)),
              basicOutputs.map(VecLiteral(_)().tchk())
            )
          case n =>
            assert(n > 1)
            // n valid per cycle
            AbstractTestIO(
              basicInputExprs
                .grouped(n)
                .map(VecLiteral(_: _*)().tchk())
                .map(Seq(_))
                .toSeq,
              basicOutputs
                .grouped(n)
                .map(VecLiteral(_: _*)().tchk())
                .toSeq
            )
        }
        s"${name}_$par" -> io
      })
      .toMap
    val underutilizedCases = Seq(3, 9).map({ denom =>
      val io = AbstractTestIO(
        in = basicInputExprs.map(Seq(_)),
        out = basicOutputs,
        hold = denom,
        skip = denom - 1
      )
      s"${name}_1_$denom" -> io
    })
    normalCases ++ underutilizedCases
  }

  private def smallSharpenIO: Map[String, TestIO] = {
    sharpenIO("smallsharpen", width = 4, height = 4, typ = U8)
  }

  private def bigSharpenIO: Map[String, TestIO] = {
    sharpenIO("bigsharpen", width = 1920, height = 4, typ = U32)
  }

  private def smallCameraIO: Map[String, TestIO] = {
    val width = 8
    val height = 8
    val basicInputExprs = (1 to (width * height))
      .map(i => (i * i) % U32.maxInt)
      .map(_.toLong)
      .map(C(_)(U32))
    val basicOutputs: Seq[Expr] = {
      val f =
        os.pwd / "src" / "test" / "resources" / "aetherling_benchmarks" / "smallcamera_outputs.csv"
      os.read
        .lines(f)
        .map({ line =>
          val Array(r, g, b) = line
            .split(",")
            .map(_.toLong)
            .map({
              // Aetherling uses 253 as a sentinel value for undefined elements
              case 253 => Undefined(U32)
              case x   => C(x)(U32)
            })
          Tuple(r, Tuple(g, b)())().tchk()
        })
    }
    val normalCases = Seq(1, 2, 4, 8, 16)
      .map({ par =>
        val io = par match {
          case 1 =>
            AbstractTestIO(
              basicInputExprs.map(VecLiteral(_)().tchk()).map(Seq(_)),
              basicOutputs.map(VecLiteral(_)().tchk())
            )
          case n =>
            AbstractTestIO(
              basicInputExprs
                .grouped(n)
                .map(VecLiteral(_: _*)().tchk())
                .map(Seq(_))
                .toSeq,
              basicOutputs
                .grouped(n)
                .map(VecLiteral(_: _*)().tchk())
                .toSeq
            )
        }
        s"smallcamera_$par" -> io
      })
      .toMap
    val underutilizedCase = {
      val denom = 4
      val io = AbstractTestIO(
        in = basicInputExprs.map(Seq(_)),
        out = basicOutputs,
        hold = denom,
        skip = denom - 1
      )
      s"smallcamera_1_$denom" -> io
    }
    normalCases + underutilizedCase
  }

  private def bigCameraIO: Map[String, TestIO] = {
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
        .map({ line =>
          val Array(r, g, b) = line
            .split(",")
            .map(_.toLong)
            .map({
              // Aetherling uses 253 as a sentinel value for undefined elements
              case 253 => Undefined(U32)
              case x   => C(x)(U32)
            })
          Tuple(r, Tuple(g, b)())().tchk()
        })
    }
    val normalCases = Seq(1, 2, 4, 8, 16)
      .map({ par =>
        val io = par match {
          case 1 =>
            AbstractTestIO(
              basicInputExprs.map(VecLiteral(_)().tchk()).map(Seq(_)),
              basicOutputs.map(VecLiteral(_)().tchk())
            )
          case n =>
            AbstractTestIO(
              basicInputExprs
                .grouped(n)
                .map(VecLiteral(_: _*)().tchk())
                .map(Seq(_))
                .toSeq,
              basicOutputs
                .grouped(n)
                .map(VecLiteral(_: _*)().tchk())
                .toSeq
            )
        }
        s"bigcamera_$par" -> io
      })
      .toMap
    val underutilizedCase = {
      val denom = 4
      val io = AbstractTestIO(
        in = basicInputExprs.map(Seq(_)),
        out = basicOutputs,
        hold = denom,
        skip = denom - 1
      )
      s"bigcamera_1_$denom" -> io
    }
    normalCases + underutilizedCase
  }

  private def smallMVMIO: Map[String, TestIO] = {
    val height = 4
    val width = 4
    val mat =
      (0 until height).flatMap(i => (0 until width).map(j => 1 + i + j))
    val vec = 1 to width
    val result = mat
      .grouped(width)
      .map(row => row.zip(vec).map({ case (x, y) => x * y }).sum)
      .toSeq
    val in = AbstractTestInput(
      (0 until width * height).map(t =>
        Seq(C(mat(t))(U8), if (t < 4) C(vec(t))(U8) else Undefined(U8))
      )
    )
    val out = AbstractTestOutput(result.map(C(_)(U8)), skip = width - 1)
    Map(
      "smallmvm_1_4" -> AbstractTestIO(in, out),
      "smallmvm_1_2" -> AbstractTestIO(
        in.vec(2),
        out.copy(skip = width / 2 - 1)
      ),
      "smallmvm_1" -> AbstractTestIO(
        in.vec(4),
        out.copy(skip = width / 4 - 1).vec(1)
      )
    )
  }

  private def bigMVMIO: Map[String, TestIO] = {
    val height = 256
    val width = 256
    val uint = U16
    val mat =
      (0 until height).flatMap(i => (0 until width).map(j => (1 + i + j) % 16))
    val vec = (1 to width).map(_ % 16)
    val result = mat
      .grouped(width)
      .map(row => row.zip(vec).map({ case (x, y) => x * y }).sum)
      .toSeq
    val in = AbstractTestInput(
      (0 until width * height).map(t =>
        Seq(
          C(mat(t))(uint),
          if (t < width) C(vec(t))(uint) else Undefined(uint)
        )
      )
    )
    val out = AbstractTestOutput(result.map(C(_)(uint)), skip = width - 1)
    Seq(1, 2, 4, 8, 16)
      .map(par =>
        s"bigmvm_1_${width / par}" -> AbstractTestIO(
          if (par == 1) in else in.vec(par),
          out.copy(skip = width / par - 1)
        )
      )
      .toMap
  }

  private def matVecIO: Map[String, vhdl.PositionalTestIO] = {
    Seq(1, 2, 4, 8, 16, 32)
      .map({ par =>
        val io = mhir.main.stored.ProgramIO
          .matVecMulIO(width = 32, height = 32, par = par, uint = U16)
        s"matvec_$par" -> io
      })
      .toMap
  }

  private def sqrtIO: Map[String, TestIO] = {
    val n = 1 << 16
    val sequentialIn = AbstractTestInput(
      (t: Int) => (_: Int) => C(t)(U16),
      elemTypes = Seq(U16),
      len = n,
      hold = 1
    )
    val sequentialOut = AbstractTestOutput(
      (t: Int) => C(math.sqrt(t).floor.toLong)(U16),
      elemTyp = U16,
      len = n,
      skip = 0
    )
    Seq(1, 4)
      .map({ par =>
        val io = par match {
          case 1 => AbstractTestIO(sequentialIn, sequentialOut)
          case _ =>
            AbstractTestIO(sequentialIn.vec(par), sequentialOut.vec(par))
        }
        s"sqrt_$par" -> io
      })
      .toMap
  }

  private def bigSobelIO: Map[String, TestIO] = {
    val width = 1920
    val height = 4
    val k = C(255)(I32)
    // Checkerboard pattern (10x2 rectangle)
    val basicInputs: Seq[Seq[Int]] =
      (0 until height).map(i =>
        (0 until width).map(j => {
          val even = ((i % 4) < 2) == ((j % 20) < 10)
          if (even) k.i.toInt else 0
        })
      )
    val basicInputExprs = basicInputs.flatten.map(C(_)(k.typ))
    val basicOutputs: Seq[Expr] = {
      val gx = conv2d(
        basicInputs.map(_.map(Some(_))),
        kernel = Seq(
          Seq(-1, 0, 1),
          Seq(-2, 0, 2),
          Seq(-1, 0, 1)
        ),
        kernelDenom = 1
      )
      val gy = conv2d(
        basicInputs.map(_.map(Some(_))),
        kernel = Seq(
          Seq(-1, -2, -1),
          Seq(-0, 0, 0),
          Seq(1, 2, 1)
        ),
        kernelDenom = 1
      )
      val g = gx.flatten
        .zip(gy.flatten)
        .map({
          case (Some(x), Some(y)) =>
            C(math.sqrt(x * x + y * y).floor.toLong)(I32)
          case _ => Undefined(I32)
        })
      g
    }
    val normalCases = Seq(1, 2, 4, 8, 16)
      .map({ par =>
        val io = par match {
          case 1 =>
            AbstractTestIO(
              basicInputExprs.map(VecLiteral(_)().tchk()).map(Seq(_)),
              basicOutputs.map(VecLiteral(_)().tchk())
            )
          case n =>
            assert(n > 1)
            // n valid per cycle
            AbstractTestIO(
              basicInputExprs
                .grouped(n)
                .map(VecLiteral(_: _*)().tchk())
                .map(Seq(_))
                .toSeq,
              basicOutputs
                .grouped(n)
                .map(VecLiteral(_: _*)().tchk())
                .toSeq
            )
        }
        s"bigsobel_$par" -> io
      })
      .toMap
    val underutilizedCases = Seq(3, 9).map({ denom =>
      val io = AbstractTestIO(
        in = basicInputExprs.map(Seq(_)),
        out = basicOutputs,
        hold = denom,
        skip = denom - 1
      )
      s"bigsobel_1_$denom" -> io
    })
    normalCases ++ underutilizedCases
  }

  /** Maps benchmark names (e.g., "dot_1_105") to inputs and expected outputs
    * for the VHDL testbench.
    *
    * @note
    *   this must be manually updated for each new benchmark.
    */
  val vhdlIO: Map[String, vhdl.PositionalTestIO] = (
    mapIO.mapValues(_.toVhdl)
      ++ sumIO.mapValues(_.toVhdl)
      ++ dotIO.mapValues(_.toVhdl)
      ++ conv1dIO.mapValues(_.toVhdl)
      ++ smallConv2dIO.mapValues(_.toVhdl)
      ++ bigConv2dIO.mapValues(_.toVhdl)
      ++ smallConvB2bIO.mapValues(_.toVhdl)
      ++ bigConvB2bIO.mapValues(_.toVhdl)
      ++ smallSharpenIO.mapValues(_.toVhdl)
      ++ bigSharpenIO.mapValues(_.toVhdl)
      ++ smallCameraIO.mapValues(_.toVhdl)
      ++ bigCameraIO.mapValues(_.toVhdl)
      ++ smallMVMIO.mapValues(_.toVhdl)
      ++ bigMVMIO.mapValues(_.toVhdl)
      ++ matVecIO
      ++ sqrtIO.mapValues(_.toVhdl)
      ++ bigSobelIO.mapValues(_.toVhdl)
  )

  /** Maps benchmark names (e.g., "dot_1_105") to inputs and expected outputs
    * for the Verilog benchmark.
    *
    * @note
    *   this must be manually updated for each new benchmark.
    */
  val verilogIO: Map[String, verilog.TestIO] = (
    mapIO.mapValues(_.toVerilog)
      ++ sumIO.mapValues(_.toVerilog)
      ++ dotIO.mapValues(_.toVerilog)
      ++ conv1dIO.mapValues(_.toVerilog)
      ++ smallConv2dIO.mapValues(_.toVerilog)
      ++ bigConv2dIO.mapValues(_.toVerilog)
      ++ smallConvB2bIO.mapValues(_.toVerilog)
      ++ bigConvB2bIO.mapValues(_.toVerilog)
      ++ smallSharpenIO.mapValues(_.toVerilog)
      ++ bigSharpenIO.mapValues(_.toVerilog)
      ++ smallCameraIO.mapValues(_.toVerilog)
      ++ bigCameraIO.mapValues(_.toVerilog)
      ++ smallMVMIO.mapValues(_.toVerilog)
      ++ bigMVMIO.mapValues(_.toVerilog)
      ++ sqrtIO.mapValues(_.toVerilog)
      ++ bigSobelIO.mapValues(_.toVerilog)
  )
}
