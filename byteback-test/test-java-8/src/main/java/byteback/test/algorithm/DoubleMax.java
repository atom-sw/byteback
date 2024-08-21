/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import static byteback.specification.Contract.*;
import static byteback.specification.Operators.*;
import static byteback.specification.Quantifiers.*;

import byteback.specification.Bindings;

public class DoubleMax {

	@Behavior
	public static boolean array_is_not_null(double[] a) {
		return neq(a, null);
	}

	@Behavior
	public static boolean max_in_range(double[] a, double n, int start, int end) {
		int i = Bindings.integer();

		return forall(i, implies(lte(start, i) & lt(i, end), gte(n, a[i])));
	}

	@Behavior
	public static boolean array_is_not_empty(double[] a) {
		return gt(a.length, 0);
	}

	@Behavior
	public static boolean result_is_max(double[] a, double t) {
		return max_in_range(a, t, 0, a.length);
	}

	@Require("array_is_not_null")
	@Require("array_is_not_empty")
	@Ensure("result_is_max")
	@Return
	public static double max(double[] a) {
		double t = a[0];

		for (int i = 1; i < a.length; ++i) {
			invariant(lte(0, i) & lte(i, a.length));
			invariant(max_in_range(a, t, 0, i));

			if (a[i] > t) {
				t = a[i];
			}
		}

		return t;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
