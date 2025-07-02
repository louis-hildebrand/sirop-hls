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
      case _: IntCst | _: BoolCst | _: Param =>
        // Atomic expressions: nothing to split up
        0
      case _: Tuple | _: VecLiteral | _: StmLiteral =>
        // Already have some kind of brackets
        0
      case _: FunCall | _: TupleAccess | _: VecAccess => 1
      case _: PadTo | _: TruncateTo | _: ToSigned | _: ToUnsigned |
          _: StmBuild | _: StmData | _: StmNextK | _: VecBuild =>
        // These all look like function calls
        Precedence.FunCall
      case _: Not                    => 2
      case _: Prod | _: Div | _: Mod => 3
      case _: Sum                    => 4
      case _: LessThan               => 5
      case _: Equal                  => 6
      case _: And                    => 7
      case _: Or                     => 8
      case _: Function | _: Mux      => 9
      case e: SyntaxSugar            => e.precedence
    }
  }
}
