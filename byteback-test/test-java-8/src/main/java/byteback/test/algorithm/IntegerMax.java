/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import static byteback.annotations.Contract.*;
import static byteback.annotations.Operator.*;
import static byteback.annotations.Quantifier.*;

import byteback.annotations.Binding;

public class IntegerMax {

	@Pure
	public static boolean array_is_not_null(int a[]) {
		return neq(a, null);
	}

	@Pure
	public static boolean max_in_range(int a[], int n, int start, int end) {
		int i = Binding.integer();

		return forall(i, implies(lte(start, i) & lt(i, end), gte(n, a[i])));
	}

	@Predicate
	public static boolean array_is_not_empty(int a[]) {
		return gt(a.length, 0);
	}

	@Predicate
	public static boolean result_is_max(int a[], int t) {
		return max_in_range(a, t, 0, a.length);
	}

	@Require("array_is_not_null")
	@Require("array_is_not_empty")
	@Ensure("result_is_max")
	public static int max(int a[]) {
		int t = a[0];

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
