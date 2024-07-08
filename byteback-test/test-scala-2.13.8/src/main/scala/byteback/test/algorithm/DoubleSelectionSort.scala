/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --pei --strict -o %t.bpl
 */
package byteback.test.algorithm

import byteback.specification.Bindings
import byteback.specification.Contract._
import byteback.specification.Operators._
import byteback.specification.Operators.{eq => equal}
import byteback.specification.Quantifiers._
import byteback.specification.Special._

class DoubleSelectionSort {

  @Behavior
  def bounded_index(a: Array[Double], i: Int): Boolean = {
    lte(0, i) & lt(i, a.length)
  }

  @Behavior
  def bounded_indices(a: Array[Double], i: Int, j: Int): Boolean = {
    bounded_index(a, i) & bounded_index(a, j)
  }

  @Behavior
  def bounded_index(a: Array[Double], i: Int, m: Int): Boolean = {
    bounded_index(a, m)
  }

  @Behavior
  def is_minimum(a: Array[Double], i: Int, j: Int, m: Int): Boolean = {
    val k = Bindings.integer()
    forall(k, implies(lte(i, k) & lt(k, j), gte(a(k), a(m))))
  }

  @Behavior
  def is_minimum(a: Array[Double], i: Int, m: Int): Boolean = {
    is_minimum(a, i, a.length, m)
  }

  @Behavior
  def sorted(a: Array[Double], i: Int, j: Int): Boolean = {
    val k = Bindings.integer()
    val l = Bindings.integer()
    forall(k, forall(l, implies(lte(i, k) & lt(k, l) & lt(l, j), lte(a(k), a(l)))))
  }

  @Behavior
  def partitioned(a: Array[Double], i: Int): Boolean = {
    val k = Bindings.integer()
    val l = Bindings.integer()
    // forall k: int, l: int :: 0 <= k <= i < l <= a.length ==> a[k] <= a[l]
    forall(k, forall(l, implies(lte(0, k) & lt(k, i) & lte(i, l) & lt(l, a.length), lte(a(k), a(l)))))
  }

  @Behavior
  def array_is_not_empty(a: Array[Double]): Boolean = {
    gt(a.length, 1)
  }

  @Behavior
  def array_is_sorted(a: Array[Double]): Boolean = {
    sorted(a, 0, a.length)
  }

  @Behavior
  def array_is_not_null(a: Array[Double]): Boolean = {
    neq(a, null)
  }

  @TwoState
  @Behavior
  def swapped_elements(a: Array[Double], i: Int, j: Int): Boolean = {
    equal(old(a(i)), a(j)) & equal(old(a(j)), a(i))
  }

  @TwoState
  @Behavior
  def elements_invariance(a: Array[Double], i: Int, j: Int): Boolean = {
    val k = Bindings.integer()
    forall(k, implies(lte(0, k) & lt(k, a.length) & neq(k, i) & neq(k, j), equal(a(k), old(a(k)))))
  }

  @Return
  @Require("array_is_not_null")
  @Require("array_is_not_empty")
  @Ensure("array_is_sorted")
  def sort(a: Array[Double]): Unit = {
    var i = 0
    while (i < a.length) {
      invariant(lte(0, i) & lte(i, a.length))
      invariant(sorted(a, 0, i))
      invariant(partitioned(a, i))

      var m = i
      var j = i
      while (j < a.length) {
        invariant(lte(i, j) & lte(j, a.length))
        invariant(lte(i, m) & lt(m, a.length))
        invariant(is_minimum(a, i, j, m))

        if (a(j) < a(m)) {
          m = j
        }
        j += 1
      }

      val y = a(i)
      a(i) = a(m)
      a(m) = y

      i += 1
    }
  }
}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
