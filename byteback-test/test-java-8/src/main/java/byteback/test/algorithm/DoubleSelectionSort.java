/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import static byteback.specification.Contract.*;
import static byteback.specification.Operators.*;
import static byteback.specification.Quantifiers.*;
import static byteback.specification.Special.*;

import byteback.specification.Bindings;
import byteback.specification.Contract.Ensure;

public class DoubleSelectionSort {

	@Behavior
	public static boolean bounded_index(final double[] a, final double i) {
		return lte(0, i) & lt(i, a.length);
	}

	@Behavior
	public static boolean bounded_indices(final double[] a, final int i, final int j) {
		return bounded_index(a, i) & bounded_index(a, j);
	}

	@Behavior
	public static boolean bounded_index(final double[] a, final int i, final int m) {
		return bounded_index(a, m);
	}

	@Behavior
	public static boolean is_minimum(final double[] a, final int i, final int j, final int m) {
		final int k = Bindings.integer();

		return forall(k, implies(lte(i, k) & lt(k, j), gte(a[k], a[m])));
	}

	@Behavior
	public static boolean is_minimum(final double[] a, final int i, final int m) {
		return is_minimum(a, i, a.length, m);
	}

	@Behavior
	public static boolean sorted(final double[] a, final int i, final int j) {
		final int k = Bindings.integer();
		final int l = Bindings.integer();

		return forall(k, forall(l,
				implies(lte(i, k) & lt(k, l) & lt(l, j), lte(a[k], a[l]))));
	}
	
	@Behavior
	public static boolean partitioned(final double[] a, final int i) {
		final int k = Bindings.integer();
		final int l = Bindings.integer();

		// forall k: int, l: int :: 0 <= k <= i < l <= a.length ==> a[k] <= a[l]
		return forall(k, forall(l, implies(lte(0, k) & lt(k, i) & lte(i, l) & lt(l, a.length), lte(a[k], a[l]))));
	}

	@Behavior
	public static boolean array_is_not_empty(final double[] a) {
		return gt(a.length, 1);
	}

	@Behavior
	public static boolean array_is_sorted(final double[] a) {
		return sorted(a, 0, a.length);
	}

	@Behavior
	public static boolean array_is_not_null(final double[] a) {
		return neq(a, null);
	}

	@TwoState
	@Behavior
	public static boolean swapped_elements(final double[] a, final int i, final int j) {
		return eq(old(a[i]), a[j]) & eq(old(a[j]), a[i]);
	}

	@TwoState
	@Behavior
	public static boolean elements_invariance(final double[] a, final int i, final int j) {
		final int k = Bindings.integer();

		return forall(k, implies(lte(0, k) & lt(k, a.length) & neq(k, i) & neq(k, j), eq(a[k], old(a[k]))));
	}

	@Return
	@Require("array_is_not_null")
	@Require("array_is_not_empty")
	@Ensure("array_is_sorted")
	public static void sort(final double[] a) {
		for (int i = 0; i < a.length; ++i) {
			invariant(lte(0, i) & lte(i, a.length));
			invariant(sorted(a, 0, i));
			invariant(partitioned(a, i));

			int m = i;

			for (int j = i; j < a.length; ++j) {
				invariant(lte(i, j) & lte(j, a.length));
				invariant(lte(i, m) & lt(m, a.length));
				invariant(is_minimum(a, i, j, m));

				if (a[j] < a[m]) {
					m = j;
				}
			}

			final double y = a[i];
			a[i] = a[m];
			a[m] = y;
		}
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
