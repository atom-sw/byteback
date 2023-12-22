/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import byteback.annotations.Contract._;
import byteback.annotations.Operator._;
import byteback.annotations.Operator.{eq => equal};

class LinearSearch {

  @Predicate
  def array_is_not_null[T <: AnyRef](a: Array[T], n: T, left: Int, right: Int): Boolean = {
    return neq(a, null);
  }

  @Predicate
  def array_is_null(a: Array[Int], n: Int, left: Int, right: Int): Boolean = {
    return equal(a, null);
  }

  @Predicate
  def bounded_indices[T <: AnyRef](a: Array[T], n: T, left: Int,
    right: Int): Boolean = {

    return lte(0, left) & lte(left, right) & lte(right, a.length);
  }

  @Predicate
  def result_is_index[T <: AnyRef](a: Array[T], n: T, left: Int, right: Int,
    returns: Int): Boolean = {

    return implies(lte(0, returns), equal(a(returns), n));
  }

  @Require("array_is_not_null")
  @Require("bounded_indices")
  @Ensure("result_is_index")
  @Return
  def search[T <: AnyRef](a: Array[T], n: T, left: Int, right: Int): Int = {
    var i: Int = left;

    while (i < right) {
      invariant(lte(left, i) & lte(i, right));

      if (a(i) eq n) {
        return i;
      }

      i = i + 1;
    }

    return -1;
  }

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
