/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import byteback.annotations.Contract._;
import byteback.annotations.Special._;
import byteback.annotations.Operator._;
import byteback.annotations.Operator.{eq => equal};

class GCD {

  @Pure
  def gcd_recursive(a: Int, b: Int): Int = {
    return conditional(equal(a, b),
      a,
      conditional(gt(a, b),
        gcd_recursive(a - b, b),
        gcd_recursive(a, b - a)));
  }

  @Pure
  @Predicate
  def result_is_gcd(a: Int, b: Int, r: Int): Boolean = {
    return implies(not(arguments_are_negative(a, b)), equal(r, gcd_recursive(a, b)));
  }

  @Pure
  @Predicate
  def arguments_are_negative(a: Int, b: Int): Boolean = {
    return lte(a, 0) | lte(b, 0);
  }

  @Raise(exception = classOf[IllegalArgumentException], when = "arguments_are_negative")
  @Ensure("result_is_gcd")
  def apply(a: Int, b: Int): Int = {

		if (a <= 0 || b <= 0) {
			throw new IllegalArgumentException("Both arguments must be positive");
		}

    var r: Int = a;
    var x: Int = b;

    while (r != x) {
      invariant(gt(r, 0) & gt(x, 0));
      invariant(equal(gcd_recursive(r, x), gcd_recursive(a, b)));

      if (r > x) {
        r = r - x;
      } else {
        x = x - r;
      }
    }

    return r;
  }

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
