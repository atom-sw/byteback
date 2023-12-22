/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import static byteback.annotations.Contract.*;
import static byteback.annotations.Operator.*;
import static byteback.annotations.Quantifier.*;

import byteback.annotations.Binding;

public class BinarySearch {

	@Predicate
	public static boolean sorted_array(int a[], int n, int left, int right) {
		int i = Binding.integer();
		int j = Binding.integer();

		return forall(i, forall(j, implies(lte(left, i) & lt(i, j) & lte(j, right), lte(a[i], a[j]))));
	}

	@Predicate
	public static boolean bounded_indices(int a[], int n, int left, int right) {
		return lte(0, left) & lte(left, right) & lte(right, a.length);
	}

	@Predicate
	public static boolean result_is_index(int a[], int n, int left, int right, int returns) {
  	return implies(lte(0, returns), eq(a[returns], n));
	}

	@Predicate
	public static boolean array_is_not_null(final int[] a, int n, int left, int right) {
		return neq(a, null);
	}

	@Return
	@Require("array_is_not_null")
	@Require("sorted_array")
	@Require("bounded_indices")
	@Ensure("result_is_index")
	public static int search(int a[], int n, int left, int right) {
		if (left < right) {
			int p = left + (right - left) / 2;

			assertion(lte(left, p) & lte(p, right));

			if (a[p] < n) {
				return search(a, n, left, p);
			} else if (a[p] > n) {
				return search(a, n, p, right);
			} else {
				assertion(eq(a[p], n));
				return p;
			}
		}

		return -1;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 2 verified, 0 errors
 */
