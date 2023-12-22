/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import static byteback.annotations.Contract.*;
import static byteback.annotations.Operator.*;
import static byteback.annotations.Quantifier.*;

import byteback.annotations.Binding;

public class DoubleInsertionSort {

	@Pure
	public static boolean sorted(final double[] a, final int i, final int j) {
		final int k = Binding.integer();

		return forall(k, implies(lt(i, k) & lt(k, j), lte(a[k - 1], a[k])));
	}

	@Predicate
	public static boolean array_is_not_empty(final double[] a) {
		return gt(a.length, 0);
	}

	@Predicate
	public static boolean array_is_not_null(final double[] a) {
		return neq(a, null);
	}

	@Predicate
	public static boolean array_is_sorted(final double[] a) {
		return sorted(a, 0, a.length);
	}

	@Require("array_is_not_null")
	@Require("array_is_not_empty")
	@Ensure("array_is_sorted")
	public static void sort(final double[] a) {

		for (int i = 1; i < a.length; ++i) {
			invariant(lt(0, i) & lte(i, a.length));
			invariant(sorted(a, 0, i));

			for (int j = i; j > 0 && a[j - 1] > a[j]; --j) {
				invariant(lte(0, j) & lte(j, i));
				invariant(sorted(a, 0, j));
				invariant(sorted(a, j, i + 1));

				final double y;

				y = a[j];
				a[j] = a[j - 1];
				a[j - 1] = y;
			}
		}

	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
