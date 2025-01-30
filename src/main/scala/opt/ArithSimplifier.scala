package opt

import ir._

object ArithSimplifier {
  def simplifySum(terms: Seq[Expr]): Expr = {
    val newTerms = if (terms.contains(DontCare)) {
      Seq(DontCare)
    } else {
      // Flatten addition tree to maximise opportunities for the following simplifications
      val flatTerms = terms.flatMap({
        case Sum(terms) => terms
        case e          => Seq(e)
      })
      // Combine constants (which also eliminates zeros)
      val const = flatTerms
        .flatMap({
          case IntCst(n) => Some(n)
          case _         => None
        })
        .sum
      val nonConstants = flatTerms.filter(e => !e.isInstanceOf[IntCst])
      // Combine like terms
      val coefficientsAndTerms = nonConstants.map({
        case Prod(factors) => coefficientAndRemaining(factors)
        case e             => (1, e)
      })
      val coefficientByTerm = coefficientsAndTerms
        .groupBy({ case (_, e) => e })
        .map({ case (e, coeffs) =>
          e -> coeffs.map({ case (c, _) => c }).sum
        })
      val newTerms = coefficientByTerm.toSeq
        .flatMap({
          case (_, 0)             => None
          case (e, 1)             => Some(e)
          case (Prod(factors), c) => Some(Prod(IntCst(c) +: factors: _*))
          case (e, c)             => Some(Prod(IntCst(c), e))
        })
      if (const == 0) {
        newTerms
      } else {
        IntCst(const) +: newTerms
      }
    }
    if (newTerms.isEmpty) {
      IntCst(0)
    } else if (newTerms.length == 1) {
      newTerms.head
    } else {
      Sum(newTerms: _*)
    }
  }

  def simplifyProd(factors: Seq[Expr]): Expr = {
    val newFactors = if (factors.contains(DontCare)) {
      Seq(DontCare)
    } else if (factors.contains(IntCst(0))) {
      Seq(IntCst(0))
    } else {
      // Flatten multiplication tree to maximise opportunities for the following simplifications
      val flatFactors = factors.flatMap({
        case Prod(factors) => factors
        case e             => Seq(e)
      })
      // Combine constants (which also eliminates ones)
      val const = flatFactors
        .flatMap({
          case IntCst(n) => Some(n)
          case _         => None
        })
        .product
      val nonConstants = flatFactors.filter(e => !e.isInstanceOf[IntCst])
      if (const == 1) {
        nonConstants
      } else {
        IntCst(const) +: nonConstants
      }
    }
    if (newFactors.isEmpty) {
      IntCst(1)
    } else if (newFactors.length == 1) {
      newFactors.head
    } else {
      Prod(newFactors: _*)
    }
  }

  def simplifyDiv(numerator: Expr, denominator: Expr): Expr = {
    val (numer, denom) = denominator match {
      case IntCst(c) if c < 0 =>
        (simplifyProd(Seq(IntCst(-1), numerator)), IntCst(-c))
      case _ => (numerator, denominator)
    }
    (numer, denom) match {
      case (e1: IntCst, e2: IntCst) => e1.i / e2.i
      case (e, IntCst(1))           => e
      case (Prod(factors), IntCst(c)) =>
        val (newFactors, newDenom) =
          factors.foldRight((Seq[Expr](), c))((e, acc) =>
            e match {
              case IntCst(c) =>
                val gcd = BigInt(c).gcd(acc._2).toInt
                (IntCst(c / gcd) +: acc._1, acc._2 / gcd)
              case e => (e +: acc._1, acc._2)
            }
          )
        val newNumer = simplifyProd(newFactors)
        newDenom match {
          case 1 => newNumer
          case d => Div(newNumer, d)
        }
      case (DontCare, _) | (_, DontCare) => DontCare
      case (e1 @ _, e2 @ _)              => Div(e1, e2)
    }
  }

  /** Given a list of factors, find the constant coefficient (or one) and the
    * remaining term (or one).
    */
  private def coefficientAndRemaining(factors: Seq[Expr]): (Int, Expr) = {
    val const = factors
      .flatMap({
        case IntCst(n) => Some(n)
        case _         => None
      })
      .product
    val nonConst = factors.filter(e => !e.isInstanceOf[IntCst]) match {
      case Seq()   => IntCst(1)
      case Seq(x)  => x
      case factors => Prod(factors: _*)
    }
    (const, nonConst)
  }
}
