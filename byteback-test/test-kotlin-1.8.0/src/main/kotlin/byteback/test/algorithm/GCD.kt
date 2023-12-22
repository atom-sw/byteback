/**
  * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
  */
package byteback.test.algorithm;

import byteback.annotations.Contract.*
import byteback.annotations.Special.*
import byteback.annotations.Operator.*

class GCD {

  @Pure
  fun `gcd recursive`(a: Int, b: Int): Int {
    return conditional(eq(a, b),
      a,
      conditional(gt(a, b),
        `gcd recursive`(a - b, b),
        `gcd recursive`(a, b - a)));
  }

  @Pure
  @Predicate
  fun `result is gcd`(a: Int, b: Int, r: Int): Boolean {
    return implies(not(`arguments are negative`(a, b)), eq(r, `gcd recursive`(a, b)));
  }

  @Pure
  @Predicate
  fun `arguments are negative`(a: Int, b: Int): Boolean {
    return lte(a, 0) or lte(b, 0)
  }

  @Raise(`exception` = IllegalArgumentException::class, `when` = "arguments are negative")
  @Ensure("result is gcd")
  fun apply(a: Int, b: Int): Int {

		if (a <= 0 || b <= 0) {
			throw IllegalArgumentException("Both arguments must be positive");
		}

    var r: Int = a;
    var x: Int = b;

    while (r != x) {
      invariant(gt(r, 0) and gt(x, 0));
      invariant(eq(`gcd recursive`(r, x), `gcd recursive`(a, b)));

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
