/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --pei --strict -o %t.bpl
 */
package byteback.test.algorithm

import byteback.specification.Contract._
import byteback.specification.Operators._
import byteback.specification.Quantifiers._
import byteback.specification.Bindings

class DoubleMax {

  @Behavior
  def array_is_not_null(a: Array[Double]): Boolean = {
    neq(a, null)
  }

  @Behavior
  def max_in_range(a: Array[Double], n: Double, start: Int, `end`: Int): Boolean = {
    val i = Bindings.integer()

    forall(i, implies(lte(start, i) & lt(i, `end`), gte(n, a(i))))
  }

  @Behavior
  def array_is_not_empty(a: Array[Double]): Boolean = {
    gt(a.length, 0)
  }

  @Behavior
  def result_is_max(a: Array[Double], t: Double): Boolean = {
    max_in_range(a, t, 0, a.length)
  }

  @Require("array_is_not_null")
  @Require("array_is_not_empty")
  @Ensure("result_is_max")
  def max(a: Array[Double]): Double = {
    var t = a(0)
    var i = 1

    while (i < a.length) {
      invariant(lte(0, i) & lte(i, a.length))
      invariant(max_in_range(a, t, 0, i))

      if (a(i) > t) {
        t = a(i)
      }
      i += 1
    }

    t
  }
}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
