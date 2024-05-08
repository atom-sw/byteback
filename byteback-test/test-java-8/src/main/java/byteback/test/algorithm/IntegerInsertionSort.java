/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import static byteback.specification.Contract.*;
import static byteback.specification.Operators.*;
import static byteback.specification.Quantifiers.*;

import byteback.specification.Bindings;

public class IntegerInsertionSort {

	@Behavior
	public static boolean sorted(final int[] a, final int i, final int j) {
		final int k = Bindings.integer();
		final int l = Bindings.integer();

		return forall(k, forall(l,
				implies(lte(i, k) & lt(k, l) & lt(l, j), lte(a[k], a[l]))));
	}

	@Behavior
	public static boolean array_is_not_empty(final int[] a) {
		return gt(a.length, 0);
	}

	@Behavior
	public static boolean array_is_not_null(final int[] a) {
		return neq(a, null);
	}

	@Behavior
	public static boolean array_is_sorted(final int[] a) {
		return sorted(a, 0, a.length);
	}

	@Behavior
	public static boolean partitioned(final int[] a, final int j, final int i) {
		final int k = Bindings.integer();
		final int l = Bindings.integer();

		return forall(k, forall(l,
				implies(lte(0, k) & lt(k, j) & lt(j, l) & lte(l, i), lte(a[k], a[l]))));
	}

	@Require("array_is_not_null")
	@Require("array_is_not_empty")
	@Ensure("array_is_sorted")
	public static void sort(final int[] a) {

		for (int i = 1; i < a.length; ++i) {
			invariant(lt(0, i) & lte(i, a.length));
			invariant(sorted(a, 0, i));

			for (int j = i; j > 0 && a[j - 1] > a[j]; --j) {
				invariant(lt(0, i) & lt(i, a.length));
				invariant(lte(0, j) & lte(j, i));
				invariant(sorted(a, 0, j));
				invariant(sorted(a, j, i + 1));
				invariant(partitioned(a, j, i));

				final int y;
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
