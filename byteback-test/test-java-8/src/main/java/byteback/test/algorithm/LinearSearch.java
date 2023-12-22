/**
 * RUN: %{byteback} -cp %{jar} -c %{class} -o %t.bpl
 */
package byteback.test.algorithm;

import static byteback.annotations.Contract.*;
import static byteback.annotations.Operator.*;

public class LinearSearch {

	@Predicate
	public static boolean array_is_not_null(int a[], int n, int left, int right) {
		return neq(a, null);
	}

	@Predicate
	public static boolean bounded_indices(int a[], int n, int left, int right) {
		return lte(0, left) & lte(left, right) & lte(right, a.length);
	}

	@Predicate
	public static boolean result_is_index(int a[], int n, int left, int right, int returns) {
		return implies(lte(0, returns), eq(a[returns], n));
	}

	@Require("array_is_not_null")
	@Require("bounded_indices")
	@Ensure("result_is_index")
	@Return
	public static int search(int a[], int n, int left, int right) {

		for (int i = left; i < right; ++i) {
			invariant(lte(left, i) & lte(i, right));

			if (a[i] == n) {
				return i;
			}
		}

		return -1;
	}

	@Predicate
	public static <T> boolean array_is_not_null(T a[], T n, int left, int right) {
		return neq(a, null);
	}

	@Predicate
	public static <T> boolean bounded_indices(T a[], T n, int left, int right) {
		return lte(0, left) & lte(left, right) & lte(right, a.length);
	}

	@Predicate
	public static <T> boolean result_is_index(T a[], T n, int left, int right, int returns) {
		return implies(lte(0, returns), eq(a[returns], n));
	}

	@Require("array_is_not_null")
	@Require("bounded_indices")
	@Ensure("result_is_index")
	@Return
	public static <T> int search(T[] a, T n, int left, int right) {

		for (int i = left; i < right; ++i) {
			invariant(lte(left, i) & lte(i, right));

			if (a[i] == n) {
				return i;
			}
		}

		return -1;
	}

}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 3 verified, 0 errors
 */
