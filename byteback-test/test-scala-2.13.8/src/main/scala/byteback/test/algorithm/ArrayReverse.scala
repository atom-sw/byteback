/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import byteback.annotations.Contract._;
import byteback.annotations.Special._;
import byteback.annotations.Operator._;
import byteback.annotations.Operator.{eq => equal};
import byteback.annotations.Binding;
import byteback.annotations.Quantifier._;

class ArrayReverse {

  @Pure
  def reverse_of(a: Array[Int], b: Array[Int]): Boolean = {
    val i: Int = Binding.integer()

    return and(equal(a.length, b.length), forall(i, implies(lte(0, i) & lt(i, a.length), equal(a(i), b(b.length - 1 - i)))))
  }


  @Predicate
  def array_is_not_null(a: Array[Int], i: Int, j: Int): Boolean = {
    return neq(a, null)
  }

  @Predicate
  def swapped(a: Array[Int], i: Int, j: Int): Boolean = {
    return implies(neq(a, null), equal(old(a(i)), a(j)) & equal(old(a(j)), a(i)));
  }

  @Return(when = "array_is_not_null")
  @Ensure("swapped")
  def swap(a: Array[Int], i: Int, j: Int): Unit = {
    val y: Int = a(i)
    a(i) = a(j)
    a(j) = y
  }

  @Pure
  @Predicate
  def array_is_null(a: Array[Int]): Boolean = {
    return equal(a, null);
  }

  @Predicate
  def reversed(a: Array[Int]): Boolean = {
    return implies(not(array_is_null(a)), reverse_of(a, old(a)))
  }

  @Raise(exception = classOf[IllegalArgumentException], when = "array_is_null")
  @Ensure("reversed")
  def apply(a: Array[Int]): Unit = {
    val l = a.length - 1
    var i = 0

    if (a == null) {
      throw new IllegalArgumentException("Input array cannot be null");
    }

    while (i < (l - i)) {
      val k = Binding.integer();
      invariant(forall(k, implies(lte(0, k) & lt(k, i) | lt(l - i, k) & lte(k, l), equal(a(k), old(a(l - k))))))
      invariant(forall(k, implies(lte(i, k) & lt(k, l - i), equal(a(k), old(a(k))))))
      invariant(lte(0, i) & lte(i, (l + 1) / 2))

      swap(a, i, l - i)

      i = i + 1
    }
  }

}
/**
  * RUN: %{verify} %t.bpl | filecheck %s
  * CHECK: Boogie program verifier finished with 3 verified, 0 errors
  */
