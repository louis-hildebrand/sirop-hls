package mhir.ir

/** Defines the precedence of expressions within the IR.
  *
  * To find the precedence of a given expression, use the [[apply]] method.
  */
object Precedence {

  /** The minimum (i.e., tightest) possible precedence.
    */
  val Min: Int = Precedence(C(0)(U8))

  /** The maximum (i.e., loosest) possible precedence.
    */
  val Max: Int = Int.MaxValue

  /** The precedence of a function call.
    */
  val FunCall: Int = Precedence(mhir.ir.FunCall(C(0)(), C(0)())())

  /** The precedence of a sum.
    */
  val Sum: Int = Precedence(mhir.ir.Sum(C(0)(), C(0)())())

  /** The precedence of a product.
    */
  val Prod: Int = Precedence(mhir.ir.Prod(C(1)(), C(1)())())

  /** The precedence of a quotient.
    */
  val Div: Int = Precedence(mhir.ir.Div(C(1)(), C(1)())())

  /** The precedence of [[mhir.ir.Mod]].
    */
  val Mod: Int = Precedence(mhir.ir.Mod(C(1)(), C(1)())())

  /** The precedence of [[mhir.ir.Equal]].
    */
  val Equal: Int = Precedence(mhir.ir.Equal(C(1)(), C(1)())())

  /** The precedence of [[mhir.ir.LessThan]].
    */
  val LessThan: Int = Precedence(mhir.ir.LessThan(C(1)(), C(1)())())

  /** The precedence of [[mhir.ir.Not]].
    */
  val Not: Int = Precedence(mhir.ir.Not(C(1)())())

  /** The precedence of bitwise AND.
    */
  val BitwiseAnd: Int = 800

  /** The precedence of bitwise OR.
    */
  val BitwiseOr: Int = 900

  /** The precedence of if-then-else.
    */
  val If: Int = Precedence(mhir.ir.Mux(C(0)(), C(0)(), C(0)())())

  /** Gets the precedence of the given expression.
    *
    * Lower numbers indicate that the given operator binds "more tightly." For
    * example, the precedence of addition is higher than that of multiplication
    * because multiplication binds more tightly.
    *
    * @param e
    *   the expression whose precedence to find.
    */
  def apply(e: Expr): Int = {
    e match {
      case _: IntCst | _: FixCst | _: BoolCst | _: Param | _: Undefined =>
        // Atomic expressions: nothing to split up
        0
      case _: Tuple | _: VecLiteral | _: StmLiteral =>
        // Already have some kind of brackets
        0
      case _: FunCall | _: TupleAccess | _: VecAccess => 100
      case _: PadTo | _: TruncateTo | _: ToSigned | _: ToUnsigned |
          _: StmBuild | _: StmData | _: VecBuild | _: Bits | _: InterpretAs =>
        // These all look like function calls
        Precedence.FunCall
      case _: Not                                                      => 200
      case _: Prod | _: WrappingProd | _: IntFixProd | _: Div | _: Mod => 300
      case _: Sum | _: WrappingSum | _: WrappingDiff                   => 400
      case _: LShift | _: ARShift | _: LRShift                         => 500
      case _: LessThan                                                 => 600
      case _: Equal                                                    => 700
      case _: And                                                      => 1000
      case _: Or                                                       => 1100
      case _: Function | _: Mux                                        => 1200
      case _: LetStm      => Precedence.Max
      case e: SyntaxSugar => e.precedence
    }
  }
}
