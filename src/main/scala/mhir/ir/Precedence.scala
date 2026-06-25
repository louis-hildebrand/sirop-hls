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
      case _: FunCall | _: TupleAccess | _: VecAccess => 1
      case _: PadTo | _: TruncateTo | _: ToSigned | _: ToUnsigned |
          _: StmBuild | _: StmData | _: VecBuild | _: Bits =>
        // These all look like function calls
        Precedence.FunCall
      case _: Not                                                      => 2
      case _: Prod | _: WrappingProd | _: IntFixProd | _: Div | _: Mod => 3
      case _: Sum | _: WrappingSum | _: WrappingDiff                   => 4
      case _: LShift | _: ARShift | _: LRShift                         => 5
      case _: LessThan                                                 => 6
      case _: Equal                                                    => 7
      case _: And                                                      => 8
      case _: Or                                                       => 9
      case _: Function | _: Mux                                        => 10
      case _: LetStm      => Precedence.Max
      case e: SyntaxSugar => e.precedence
    }
  }
}
