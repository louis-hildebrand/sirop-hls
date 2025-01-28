import scala.language.implicitConversions

package object opt {
  implicit def int2Finite(n: Int): IntOrInf = Finite(n)
}
