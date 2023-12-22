/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import static byteback.annotations.Contract.*;
import static byteback.annotations.Operator.*;
import static byteback.annotations.Quantifier.*;
import static byteback.annotations.Special.*;

import byteback.annotations.Binding;
import byteback.annotations.Contract.Ensure;

public class DoubleSelectionSort {

	@Pure
	@Predicate
	public static boolean bounded_index(final double[] a, final int i) {
		return lte(0, i) & lt(i, a.length);
	}

	@Pure
	@Predicate
	public static boolean bounded_indices(final double[] a, final int i, final int j) {
		return bounded_index(a, i) & bounded_index(a, j);
	}

	@Predicate
	public static boolean bounded_index(final double[] a, final int i, final int m) {
		return bounded_index(a, m);
	}

	@Pure
	public static boolean is_minimum(final double[] a, final int i, final int j, final int m) {
		final int k = Binding.integer();

		return forall(k, implies(lte(i, k) & lt(k, j), gte(a[k], a[m])));
	}

	@Pure
	@Predicate
	public static boolean is_minimum(final double[] a, final int i, final int m) {
		return is_minimum(a, i, a.length, m);
	}

	@Return
	@Require("bounded_index")
	@Ensure("is_minimum")
	@Ensure("bounded_index")
	public static int minimum(final double[] a, final int i) {
		int m = i;

		for (int j = i; j < a.length; ++j) {
			invariant(lte(i, j) & lte(j, a.length));
			invariant(lte(i, m) & lt(m, a.length));
			invariant(is_minimum(a, i, j, m));

			if (a[j] < a[m]) {
				m = j;
			}
		}

		return m;
	}

	@Pure
	public static boolean sorted(final double[] a, final int i, final int j) {
		final int k = Binding.integer();

		return forall(k, implies(lt(i, k) & lt(k, j), lte(a[k - 1], a[k])));
	}

	@Pure
	public static boolean partitioned(final double[] a, final int c) {
		final int k = Binding.integer();
		final int l = Binding.integer();

		return forall(k, forall(l, implies(lte(0, k) & lt(k, c) & lte(c, l) & lt(l, a.length), lte(a[k], a[l]))));
	}

	@Predicate
	public static boolean array_is_not_empty(final double[] a) {
		return gt(a.length, 1);
	}

	@Predicate
	public static boolean array_is_sorted(final double[] a) {
		return sorted(a, 0, a.length);
	}

	@Predicate
	public static boolean array_is_not_null(final double[] a) {
		return neq(a, null);
	}

	@Predicate
	public static boolean swapped_elements(final double[] a, final int i, final int j) {
		return eq(old(a[i]), a[j]) & eq(old(a[j]), a[i]);
	}

	@Predicate
	public static boolean elements_invariance(final double[] a, final int i, final int j) {
		final int k = Binding.integer();
		return  forall(k, implies(lte(0, k) & lt(k, a.length) & neq(k, i) & neq(k, j), eq(k, old(k))));
	}

	@Return
	@Require("bounded_indices")
	@Ensure("swapped_elements")
	@Ensure("elements_invariance")
	public static void swap(final double[] a, int i, int j) {
		final double y = a[i];
		a[i] = a[j];
		a[j] = y;
	}

	@Return
	@Require("array_is_not_null")
	@Require("array_is_not_empty")
	@Ensure("array_is_sorted")
	public static void sort(final double[] a) {
		for (int c = 0; c < a.length; ++c) {
			invariant(lte(0, c) & lte(c, a.length));
			invariant(partitioned(a, c));
			invariant(sorted(a, 0, c));

			final int m = minimum(a, c);
			swap(a, m, c);
		}
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 4 verified, 0 errors
 */
