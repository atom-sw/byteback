/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import static byteback.specification.Contract.*;
import static byteback.specification.Operators.*;
import static byteback.specification.Special.*;

public class GCD {

	@Behavior
	public static int gcd_recursive(int a, int b) {
		return conditional(eq(a, b), a, conditional(gt(a, b), gcd_recursive(a - b, b), gcd_recursive(a, b - a)));
	}

	@Behavior
	public static boolean result_is_gcd(int a, int b, int r) {
		return implies(not(arguments_are_negative(a, b)), eq(r, gcd_recursive(a, b)));
	}

	@Behavior
	public static boolean arguments_are_negative(int a, int b) {
		return lte(a, 0) | lte(b, 0);
	}

	@Behavior
	public static boolean arguments_are_positive(int a, int b) {
		return lte(a, 0) & lte(b, 0);
	}

	@Raise(exception = IllegalArgumentException.class, when = "arguments_are_negative")
	@Ensure("result_is_gcd")
	public static int gcd(final int a, final int b) {
		if (a <= 0 || b <= 0) {
			throw new IllegalArgumentException("Both arguments must be positive");
		}

		int r = a;
		int x = b;

		while (r != x) {
			invariant(gt(r, 0) & gt(x, 0));
			invariant(eq(gcd_recursive(r, x), gcd_recursive(a, b)));

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
