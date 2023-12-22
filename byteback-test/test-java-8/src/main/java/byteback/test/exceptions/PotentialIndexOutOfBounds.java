/**
 * RUN: %{byteback} -cp %{jar} -c %{class} --act -o %t.bpl
 */
package byteback.test.exceptions;

import byteback.annotations.Contract.Predicate;
import byteback.annotations.Contract.Pure;
import byteback.annotations.Contract.Raise;
import byteback.annotations.Contract.Require;
import byteback.annotations.Contract.Return;
import static byteback.annotations.Operator.*;

@SuppressWarnings("unused")
public class PotentialIndexOutOfBounds {

	@Return
	public static void passedTarget(int[] a) {
		if (a.length > 0) {
			int a_0 = a[0];
		}
	}

	@Return
	public static void passedIndex(int i) {
		int[] a = new int[10];

		if (i > 0 && i < a.length) {
			int a_i = a[i];
		}
	}

	@Predicate
	public static boolean array_is_not_empty(int[] a) {
		return gt(a.length, 0);
	}

	@Require("array_is_not_empty")
	public static void constrainedTarget(int[] a) {
		int a_0 = a[0];
	}

	@Return
	public static void passedArrayAndIndex(int[] a, int i) {
		if (a.length > 0 && i >= 0 && i < a.length)  {
			int a_i = a[i];
		}
	}

	@Predicate
	public static boolean array_is_not_empty_and_index_is_in_bounds(int[] a, int i) {
		return gt(a.length, 0) & lte(0, i) & lt(i, a.length);
	}

	@Require("array_is_not_empty_and_index_is_in_bounds")
	public static void constrainedTargetAndIndex(int[] a, int i) {
		int a_i = a[i];
	}

	@Raise(exception = IndexOutOfBoundsException.class)
	public static void expectedIndexOutOfBounds1(int[] a) {
		int a_m1 = a[-1];
	}

	@Raise(exception = IndexOutOfBoundsException.class)
	public static void expectedIndexOutOfBounds2(int[] a) {
		int a_l = a[a.length];
	}

	@Pure
	@Predicate
	public static boolean array_is_empty(int[] a) {
		return eq(a.length, 0);
	}

	@Raise(exception = IndexOutOfBoundsException.class, when = "array_is_empty")
	public static void expectedIndexOutOfBounds3(int[] a) {
		int a_0 = a[0];
	}

	@Pure
	@Predicate
	public static boolean index_is_negative(int i) {
		return lt(i, 0);
	}

	@Raise(exception = IndexOutOfBoundsException.class, when = "index_is_negative")
	public static void expectedIndexOutOfBounds4(int i) {
		int[] a = new int[10];
		int a_i = a[i];
	}

	@Predicate
	public static boolean array_is_empty_or_index_is_negative(int[] a, int i) {
		return array_is_empty(a) | index_is_negative(i);
	}

	@Raise(exception = IndexOutOfBoundsException.class, when = "array_is_empty_or_index_is_negative")
	public static void expectedIndexOutOfBounds5(int[] a, int i) {
		int a_i = a[i];
	}
	
}
/**
 * RUN: %{verify} %t.bpl | filecheck %s
 * CHECK: Boogie program verifier finished with 11 verified, 0 errors
 */
